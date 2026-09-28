package vn.huynhtoantravel.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.huynhtoantravel.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
     return config.getAuthenticationManager();
 }

 @Bean UserDetailsService userDetailsService(UserRepository users){
   return username -> users.findByEmailIgnoreCase(username)
     .map(u -> User.withUsername(u.getEmail()).password(u.getPasswordHash()).disabled(!u.isEnabled())
       .authorities(u.getRoles().stream().map(r->r.getName()).toArray(String[]::new)).build())
     .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
 }

 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/login", "/register", "/booking/vehicle", "/hotels/**", "/tickets/**", "/css/**", "/js/**", "/images/**", "/api/webhooks/**", "/api/vehicle-rates", "/api/ticket-rates", "/api/rooms/**", "/api/auth/**").permitAll()
        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
        .requestMatchers("/checkout/**", "/account/**", "/api/bookings/**", "/api/checkout/**").authenticated()
        .anyRequest().permitAll())
     .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", false).permitAll())
     .logout(logout -> logout.logoutSuccessUrl("/"))
     .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
         new org.springframework.security.web.authentication.HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED),
         new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/api/**")
     ))
     .csrf(csrf -> csrf.ignoringRequestMatchers("/api/webhooks/**", "/api/dev/**", "/api/auth/**"));
   return http.build();
 }
}
