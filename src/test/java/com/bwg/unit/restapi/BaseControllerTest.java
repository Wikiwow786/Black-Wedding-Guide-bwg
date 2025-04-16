package com.bwg.unit.restapi;

import com.bwg.model.AuthModel;
import com.bwg.resolver.AuthPrincipalResolver;
import com.bwg.service.StorageService;
import com.bwg.unit.service.util.TestDataFactory;
import com.bwg.util.BeanUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public abstract class BaseControllerTest {
    protected AuthModel mockAuth;

    @MockBean
    protected AuthPrincipalResolver authPrincipalResolver;

    @BeforeEach
    void baseSetUp() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = "ROLE_ANONYMOUS";

        if (auth != null && auth.getAuthorities() != null) {
            role = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_ANONYMOUS");
        }

        mockAuth = TestDataFactory.buildAuthModel("123", role);
        StorageService mockStorageService = Mockito.mock(StorageService.class);
        when(mockStorageService.getUrl(anyString())).thenReturn("http://dummy.url");

        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        when(mockContext.getBean(StorageService.class)).thenReturn(mockStorageService);

        try {
            Field field = BeanUtil.class.getDeclaredField("applicationContext");
            field.setAccessible(true);
            field.set(null, mockContext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mock ApplicationContext in BeanUtil", e);
        }
        when(authPrincipalResolver.supportsParameter(any())).thenReturn(true);
        when(authPrincipalResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(mockAuth);
    }


    @AfterEach
    void baseTearDown() {
        Mockito.reset(authPrincipalResolver);
    }
}
