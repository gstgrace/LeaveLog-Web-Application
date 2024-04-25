package com.lms.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures security settings for LeaveLog application, enabling web security
 * and method-level security. This configuration class uses Spring Security for
 * authentication and authorization to secure the application.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

  @Autowired
  private DataSource dataSource;

  @Value("${queries.users-query}")
  private String usersQuery;

  @Value("${queries.roles-query}")
  private String rolesQuery;

  /**
   * Creates a bean that provides the password encoding service using BCrypt hashing algorithm.
   *
   * @return A PasswordEncoder that uses the BCrypt strong hashing function.
   */
  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the global authentication manager with JDBC authentication, user details, and authorities.
   *
   * @param auth the AuthenticationManagerBuilder to configure
   * @throws Exception if an error occurs when adding the JDBC authentication configuration
   */
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
        .dataSource(dataSource)
        .usersByUsernameQuery(usersQuery)
        .authoritiesByUsernameQuery(rolesQuery)
        .passwordEncoder(passwordEncoder());
  }

  /**
   * Defines the security filter chain for HTTP requests, configuring CSRF, form login,
   * logout properties, and exception handling.
   *
   * @param http the HttpSecurity to configure
   * @return the SecurityFilterChain that controls the security filter behavior
   * @throws Exception if an error occurs during the configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // Disable CSRF
        .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/", "/login", "/registration").permitAll()
                .requestMatchers("/user/manage-users/**", "/user/manage-leaves/**").hasAuthority("Manager")  // Only 'Manager' can manage users and leaves
                .requestMatchers("/user/**").hasAnyAuthority("Manager", "Employee")  // Both 'Manager' and 'Employee' can access other /user/ endpoints
//            .requestMatchers("/user/**").authenticated()
                .anyRequest().authenticated()
        )
        .formLogin((formLogin) -> formLogin
            .loginPage("/login")
            .failureUrl("/login?error=true")
            .defaultSuccessUrl("/user/home")
            .usernameParameter("email")
            .passwordParameter("password")
        )
        .logout((logout) -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
        )
        .exceptionHandling((exceptions) -> exceptions
            .accessDeniedPage("/access-denied")
        );
    return http.build();
  }

  /**
   * Customizes web security to ignore requests to static resources like CSS, JavaScript, and images,
   * which do not require security checks.
   *
   * @return A WebSecurityCustomizer that specifies paths to be ignored by Spring Security.
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers("/resources/**", "/static/**", "/css/**", "/fonts/**", "/js/**", "/img/**");
  }
}
