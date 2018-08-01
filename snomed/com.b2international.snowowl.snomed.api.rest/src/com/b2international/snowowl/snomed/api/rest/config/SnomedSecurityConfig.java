/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * @since 7.0
 */
@Configuration
@EnableWebSecurity
public class SnomedSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	    StrictHttpFirewall firewall = new StrictHttpFirewall();
	    firewall.setAllowUrlEncodedSlash(true);
	    return firewall;
	}
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		// REST should have no state, each request is re-authenticated
		http
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
				.antMatchers("/api-docs/**").authenticated()
				.antMatchers("/static/**").authenticated()
				.antMatchers("/").authenticated()
				.antMatchers("/**").hasAuthority("ROLE_USER")
			.and()
			.httpBasic()
			.and()
			.csrf()
				.disable();
	}
	
	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
