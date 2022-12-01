/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.identity.jwks;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.identity.JWTCapableIdentityProviderConfig;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @since 8.8.0
 */
@JsonTypeName(JwksIdentityProvider.TYPE)
public class JwksIdentityProviderConfig extends JWTCapableIdentityProviderConfig {

	@NotEmpty
	private String issuer;
	
	@NotEmpty
	private String jws;
	
	@NotEmpty
	private String jwksUrl;
	
	private String permissionsClaimProperty;
	
	public String getIssuer() {
		return issuer;
	}
	
	public String getJws() {
		return jws;
	}
	
	public String getJwksUrl() {
		return jwksUrl;
	}
	
	public String getPermissionsClaimProperty() {
		return permissionsClaimProperty;
	}
	
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public void setJws(String jws) {
		this.jws = jws;
	}
	
	public void setJwksUrl(String jwksUrl) {
		this.jwksUrl = jwksUrl;
	}
	
	public void setPermissionsClaimProperty(String permissionsClaimProperty) {
		this.permissionsClaimProperty = permissionsClaimProperty;
	}

}
