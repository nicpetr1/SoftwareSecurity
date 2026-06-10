package com.example.progetto;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Override
	public @Nullable AbstractAuthenticationToken convert(Jwt source) { 
		return new JwtAuthenticationToken(
				
				source,
				Stream.concat(
						new JwtGrantedAuthoritiesConverter().convert(source).stream(),
						extractResourceRoles(source).stream()
						).collect(Collectors.toSet()) 
				);
	}
	
	private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt){

		Object resourceAccessObj = jwt.getClaim("resource_access");

		if (!(resourceAccessObj instanceof Map<?, ?> resourceAccess)) {
			return Collections.emptySet();
		}

		Object clientObj = resourceAccess.get("neg-ant-client");

		if (!(clientObj instanceof Map<?, ?> clientAccess)) {
			return Collections.emptySet();
		}

		Object rolesObj = clientAccess.get("roles");

		if (!(rolesObj instanceof List<?> roles)) {
			return Collections.emptySet();
		}

		
		return roles.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
            .collect(Collectors.toSet());
	}

}
