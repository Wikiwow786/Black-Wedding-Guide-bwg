package com.bwg.resolver;

import com.bwg.domain.Users;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.exception.UnauthorizedException;
import com.bwg.model.AuthModel;
import com.bwg.repository.UsersRepository;
import com.bwg.util.CorrelationIdHolder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Native;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

public class AuthPrincipalResolver implements HandlerMethodArgumentResolver {

    private static final String GOOGLE_PUBLIC_KEYS_URL =
            "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";

    private final UsersRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${api.auth.register}")
    private String registerEndpoint;


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

        String authorization = extractAuthorization(webRequest, parameter.getParameterAnnotation(AuthPrincipal.class));
        String correlationId = retrieveCorrelationId(webRequest,parameter.getParameterAnnotation(AuthPrincipal.class));

        if (!StringUtils.hasText(authorization)) {
            CorrelationIdHolder.setCorrelationId(correlationId);
            return new AuthModel(null, null, null, correlationId);
        }

        AuthModel authModel = decodeToken(authorization,correlationId);
        String requestURI = webRequest.getNativeRequest(HttpServletRequest.class).getRequestURI();
        boolean isRegisterRequest = requestURI.equals(registerEndpoint);
        info(LOG_SERVICE_OR_REPOSITORY, format("Register end point " + registerEndpoint), this);


        if (isRegisterRequest) {
            CorrelationIdHolder.setCorrelationId(correlationId);
            return authModel;
        }
        return authenticateUser(authModel);
    }

    private String extractAuthorization(NativeWebRequest webRequest, AuthPrincipal annotation) {
        if (annotation == null) return null;

        String token = webRequest.getParameter(annotation.authorization());
        if (!StringUtils.hasText(token)) {
            token = webRequest.getHeader(annotation.authorization());
        }

        return (StringUtils.hasText(token) && token.startsWith("Bearer ")) ? token.substring(7) : token;
    }

    private String retrieveCorrelationId(NativeWebRequest webRequest, AuthPrincipal annotation) {
        String correlationId = (annotation.correlationId() != null) ? webRequest.getHeader(annotation.correlationId()) : null;
        return (correlationId != null) ? correlationId : generateUuid();
    }

    private AuthModel decodeToken(String token,String correlationId) {
        try {
            PublicKey publicKey = getGooglePublicKey(token);
            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new AuthModel(token, claims.get("user_id", String.class), claims.get("email", String.class), correlationId);
        } catch (Exception e) {
            CorrelationIdHolder.setCorrelationId(correlationId);
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    private PublicKey getGooglePublicKey(String token) throws IOException, GeneralSecurityException {
        Map<String, String> publicKeys = objectMapper.readValue(new URL(GOOGLE_PUBLIC_KEYS_URL),
                new TypeReference<>() {
                });

        String keyId = extractKeyIdFromToken(token);
        String publicKeyPem = publicKeys.get(keyId);

        if (publicKeyPem == null) {
            throw new RuntimeException("Public key not found for key ID: " + keyId);
        }

        return convertPemToPublicKey(publicKeyPem);
    }

    private String extractKeyIdFromToken(String token) throws IOException {
        String[] tokenParts = token.split("\\.");
        if (tokenParts.length < 3) {
            throw new RuntimeException("Invalid JWT: Malformed token structure");
        }

        JsonNode headerNode = objectMapper.readTree(decodeBase64UrlSafe(tokenParts[0]));
        if (!headerNode.has("kid")) {
            throw new RuntimeException("Invalid JWT: Missing 'kid' in header");
        }

        return headerNode.get("kid").asText();
    }

    private PublicKey convertPemToPublicKey(String pem) throws GeneralSecurityException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8)));
        return certificate.getPublicKey();
    }

    private AuthModel authenticateUser(AuthModel authModel) {
        Users user = userRepository.findByEmailIgnoreCase(authModel.email());
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found for the email extracted from JWT token.");
        }
            String role = "ROLE_" + user.getRole().name().toUpperCase();
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(authModel, null, authorities)
            );
            return new AuthModel(authModel.authorization(),  user.getUserId().toString(), authModel.email(), authModel.correlationId());
    }

    private static String decodeBase64UrlSafe(String base64) {
        if (base64 == null || base64.isEmpty()) {
            throw new RuntimeException("Invalid JWT: Empty Base64 segment");
        }

        base64 = base64.trim().replaceAll("[^A-Za-z0-9\\-_=]", "")
                .replace('-', '+').replace('_', '/');

        while (base64.length() % 4 != 0) {
            base64 += "=";
        }

        try {
            return new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64 Decoding Failed", e);
        }
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
