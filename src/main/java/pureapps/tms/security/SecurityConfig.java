package pureapps.tms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the PasswordEncoder bean that will be used for hashing passwords.
     * BCrypt is the recommended standard.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * WARNING: This configuration is permissive for development purposes ONLY.
     * It disables CSRF and allows all requests. Secure this properly later!
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection - common for stateless REST APIs.
                // Ensure you understand the implications if using sessions.
                .csrf(AbstractHttpConfigurer::disable)
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Permit all requests to any endpoint FOR NOW.
                        // !!! THIS IS INSECURE FOR PRODUCTION !!!
                        // TODO: Replace with specific authorization rules based on roles/endpoints
                        .requestMatchers("/**").permitAll()
                        // Example of a more specific rule (if not using permitAll above):
                        // .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                        // .requestMatchers("/api/manager/**").hasAnyRole("ADMINISTRATOR", "MANAGER")
                        // .requestMatchers("/api/users/me/**").hasAnyRole("ADMINISTRATOR", "MANAGER", "EMPLOYEE")
                        .anyRequest().authenticated() // Fallback: any other request needs authentication (may be redundant with permitAll)
                );
        // Add other configurations like session management, form login, logout, JWT filter etc. as needed later.

        return http.build();
    }
}