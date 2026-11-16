package com.uniremingto.inventario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/** Define autenticación de demostración y autorización por rol. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Configura las rutas protegidas, el formulario de acceso y el cierre de sesión. */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/api/usuarios", "/api/usuarios/", "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/usuarios/**", "/categorias/**", "/proveedores/**").hasRole("ADMIN")
                .requestMatchers("/productos/**", "/ventas/**", "/movimientos-inventario/**", "/dashboard").hasAnyRole("ADMIN", "VENDEDOR")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll());

        return http.build();
    }

    /** Crea los usuarios temporales de demostración almacenados en memoria. */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin@farmacia.com")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails vendedor = User.builder()
                .username("vendedor@farmacia.com")
                .password(passwordEncoder.encode("vendedor123"))
                .roles("VENDEDOR")
                .build();

        return new InMemoryUserDetailsManager(admin, vendedor);
    }

    /** Proporciona BCrypt para codificar las contraseñas de demostración. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
