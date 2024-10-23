package enerhi.jwt.config;

import enerhi.jwt.config.auth.jwt.JwtAuthenticationFilter;
import enerhi.jwt.config.auth.jwt.JwtAuthorizationFilter;
import enerhi.jwt.config.repository.UserRepository;
import enerhi.jwt.filter.MyFilter1;
import enerhi.jwt.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록
public class SecurityConfig {

    @Autowired
    private final CorsConfig corsConfig;
    @Autowired
    private final UserRepository userRepository;

    @Bean // authenticationManager를 IoC에 등록해줌.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        //AuthenticationManager authenticationManager =  http.getSharedObject(AuthenticationManager.class);

        http.csrf(AbstractHttpConfigurer::disable);
        http
                .sessionManagement(sc -> sc.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음.
                .addFilter(corsConfig.corsFilter()) // @CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O) --> 모든 요청 허용.
                //.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class) // ---> 22, 23강 테스트용.
//                .addFilter(new JwtAuthenticationFilter(authenticationManager)) // AuthenticationManager ---> 24강 테스트
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository)) // AuthenticationManager ---> 27강 테스트
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                /* --------- security 최신 버전에서는 권한 적용시 ROLE_ 쓰지 않음. 즉, USER, ADMIN, MANAGER로 써야함 ---------- */
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/user/**").authenticated() // /user라는 url로 들어오면 인증이 필요하다.
                        .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN") // manager으로 들어오는 MANAGER 인증 또는 ADMIN인증이 필요하다는 뜻이다.
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // //admin으로 들어오면 ADMIN권한이 있는 사람만 들어올 수 있음
//                        .requestMatchers("/login").permitAll()
                        .anyRequest().permitAll() // 그리고 나머지 url은 전부 권한을 허용해준다.
                );

        return http.build();
    }
}
