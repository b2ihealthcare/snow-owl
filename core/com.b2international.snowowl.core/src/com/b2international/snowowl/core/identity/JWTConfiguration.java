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
package com.b2international.snowowl.core.identity;

/**
 * @since 8.8.0
 */
public class JWTConfiguration {

	private static final String SNOW_OWL_ISSUER = "Snow Owl";
	
	// JWT configuration
	private String issuer = SNOW_OWL_ISSUER;
	private String jws;
	private String secret;
	private String signingKey;
	private String verificationKey;
	private String emailClaimProperty = "sub";
	private String permissionsClaimProperty = "permissions";
	
	public String getIssuer() {
		return issuer;
	}
	
	public String getEmailClaimProperty() {
		return emailClaimProperty;
	}

	public String getJws() {
		return jws;
	}
	
	public String getPermissionsClaimProperty() {
		return permissionsClaimProperty;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public String getSigningKey() {
		return signingKey;
	}
	
	public String getVerificationKey() {
		return verificationKey;
	}
	
	public void setEmailClaimProperty(String emailClaimProperty) {
		this.emailClaimProperty = emailClaimProperty;
	}
	
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public void setJws(String jws) {
		this.jws = jws;
	}
	
	public void setPermissionsClaimProperty(String permissionsClaimProperty) {
		this.permissionsClaimProperty = permissionsClaimProperty;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public void setSigningKey(String signingKey) {
		this.signingKey = signingKey;
	}
	
	public void setVerificationKey(String verificationKey) {
		this.verificationKey = verificationKey;
	}

}
