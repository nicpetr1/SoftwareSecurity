
package com.example.progetto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
			.headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions.deny())
				  .contentSecurityPolicy(csp -> csp                
						  .policyDirectives("default-src 'self'; " +                                   
				  "script-src 'self' 'unsafe-inline' https://trusted-scripts.com; " +       
         "style-src 'self' 'unsafe-inline'; " + "img-src 'self' data:; " + "frame-ancestors 'none';"))) 
				
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers("/cliente/**").hasAuthority("ROLE_client_cliente")
					.requestMatchers("/gestore/**").hasAuthority("ROLE_client_gestore")
				.anyRequest().authenticated()
				
			)
			
			.oauth2ResourceServer((oauth2) -> oauth2
				.jwt(token -> token.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
			)
			
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
		
		
	}

}
