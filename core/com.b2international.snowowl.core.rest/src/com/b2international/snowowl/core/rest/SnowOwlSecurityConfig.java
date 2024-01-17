/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.core.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.b2international.snowowl.core.rest.util.CORSFilter;
import com.b2international.snowowl.core.util.PlatformUtil;

/**
 * @since 7.0
 */
@Configuration
public class SnowOwlSecurityConfig {

	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}

	@SuppressWarnings({ "lgtm[java/spring-disabled-csrf-protection]" })
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// configure stateless session policy
		http.sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});
		
		// disable CSRF
		http.csrf(csrf -> {
			csrf.disable();
		});

		// handle X-Forwarded headers
		http.addFilterBefore(new ForwardedHeaderFilter(), BasicAuthenticationFilter.class);
		
		// add dev time CORS filter
		if (PlatformUtil.isDevVersion()) {
			http.addFilterAfter(new CORSFilter(), BasicAuthenticationFilter.class);
		}
		
		// authentication is handled internally in AuthorizedRequest
		http.authorizeHttpRequests((authz) -> authz.requestMatchers("/**").permitAll());
		return http.build();
	}
	
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

}
