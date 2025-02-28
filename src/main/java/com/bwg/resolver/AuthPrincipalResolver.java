package com.bwg.resolver;

import com.bwg.domain.Users;
import com.bwg.model.AuthModel;
import com.bwg.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthPrincipalResolver implements HandlerMethodArgumentResolver {

    private static final String GOOGLE_PUBLIC_KEYS_URL =
            "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";

    private final UsersRepository userRepository;

    @Autowired
    public AuthPrincipalResolver(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthPrincipal.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        AuthPrincipal annotation = parameter.getParameterAnnotation(AuthPrincipal.class);
        String authorization = null;

        if (annotation != null) {
            authorization = webRequest.getParameter(annotation.authorization());
            if (!StringUtils.hasText(authorization)) {
                authorization = webRequest.getHeader(annotation.authorization());
            }
        }

        if (!StringUtils.hasText(authorization)) {
            return new AuthModel(null, null, null, null);
        }

        AuthModel authModel = decodeToken(authorization);

        Users user = userRepository.findByEmailIgnoreCase(authModel.email());

        if (user != null) {
            final String role = "ROLE_" + user.getRole().name().toUpperCase();
            List<GrantedAuthority> authorities = List.of((GrantedAuthority) () -> role);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(authModel, null, authorities)
            );

            return new AuthModel(authModel.authorization(), authModel.userId(), authModel.email(), generateUuId());
        }
        return new AuthModel(authModel.authorization(), authModel.userId(), authModel.email(), null);
    }

    private AuthModel decodeToken(String tokenId) {
        try {
            PublicKey publicKey = getGooglePublicKey(tokenId);
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(tokenId)
                    .getPayload();

            String userId = claims.get("user_id", String.class);
            String email = claims.get("email", String.class);

            return new AuthModel(tokenId, userId, email,null);

        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }


    private static PublicKey getGooglePublicKey(String token) throws IOException, GeneralSecurityException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> publicKeys = objectMapper.readValue(new URL(GOOGLE_PUBLIC_KEYS_URL), Map.class);
        String headerJson = new String(Base64.getUrlDecoder().decode(token.split("\\.")[0]));
        String keyId = objectMapper.readTree(headerJson).get("kid").asText();
        String publicKeyPem = publicKeys.get(keyId);
        if (publicKeyPem == null) {
            throw new RuntimeException("Public key not found for key ID: " + keyId);
        }
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(publicKeyPem.getBytes()));

        return certificate.getPublicKey();
    }

    private String generateUuId(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
