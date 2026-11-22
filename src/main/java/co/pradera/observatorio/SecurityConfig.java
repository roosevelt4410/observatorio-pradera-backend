package co.pradera.observatorio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    UserDetailsService adminUsers(@Value("${observatorio.admin.username:admin}") String username,
                                  @Value("${observatorio.admin.password:}") String password,
                                  PasswordEncoder encoder) {
        if (password == null || password.isBlank() || password.length() < 12) {
            throw new IllegalStateException("Configure OBS_ADMIN_PASSWORD con al menos 12 caracteres antes de iniciar");
        }
        return new InMemoryUserDetailsManager(User.withUsername(username)
            .password(encoder.encode(password)).roles("ADMIN").build());
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // API sin cookies de sesion; usar HTTPS antes de desplegar
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/salud", "/api/v1/indicadores/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/caracterizaciones/EMPRESA", "/api/v1/caracterizaciones/TALENTO").permitAll()
                .requestMatchers("/api/v1/caracterizaciones/**").hasRole("ADMIN")
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
