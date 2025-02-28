package com.bwg.resolver;

import com.bwg.repository.UsersRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcContext implements WebMvcConfigurer {

    private final AuthPrincipalResolver authPrincipalResolver;

    public WebMvcContext(@Lazy AuthPrincipalResolver authPrincipalResolver) {
        this.authPrincipalResolver = authPrincipalResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authPrincipalResolver);
    }

    @Bean
    public AuthPrincipalResolver authPrincipalResolver(UsersRepository usersRepository) {
        return new AuthPrincipalResolver(usersRepository);
    }
}
