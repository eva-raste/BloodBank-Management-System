package com.project.BloodBank.Config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?");
        manager.setAuthoritiesByUsernameQuery(
            "SELECT u.username, a.authority FROM authority a " +
            "INNER JOIN user u ON a.user_id = u.id " +
            "WHERE u.username = ?"
        );
        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
            configurer
                .requestMatchers("/", "/login", "/register", "/recipient-registration", "/css/**", "/js/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/donors/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/recipients/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blood-inventory/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/chat").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ai/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/ai/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/donors/all").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/donors/me").hasAuthority("ROLE_DONOR")
                .requestMatchers(HttpMethod.GET, "/api/donors/{id}").hasAnyAuthority("ROLE_DONOR", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/donors/update").hasAuthority("ROLE_DONOR")
                .requestMatchers(HttpMethod.POST, "/api/donors/donate-blood").hasAuthority("ROLE_DONOR")

                .requestMatchers(HttpMethod.GET, "/api/recipients/me").hasAuthority("ROLE_RECIPIENT")
                .requestMatchers(HttpMethod.GET, "/api/recipients/{id}").hasAnyAuthority("ROLE_RECIPIENT", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/recipients/update").hasAuthority("ROLE_RECIPIENT")
                .requestMatchers(HttpMethod.POST, "/api/recipients/request-blood").permitAll()

                .requestMatchers(HttpMethod.PUT, "/api/blood-inventory/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/blood-inventory/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/transactions/all").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/tester/**").hasAuthority("ROLE_TESTER")

                .requestMatchers("/blood-request-form", "/request-blood").permitAll()

                .anyRequest().authenticated()
        );

        // ✅ Setup login page
        http.formLogin(form -> form
            //.loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        );
        // ✅ Setup logout
        http.logout(logout -> logout
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        );

        // ✅ Disable CSRF (only in development)
      //  http.csrf(csrf -> csrf.disable());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
