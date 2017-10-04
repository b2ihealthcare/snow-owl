/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Identity module configuration. Use to configure the underlying identity services (authentication, authorization, tokens, etc.). By default the
 * configuration selects the property file (PROP_FILE) based identity manager. To select a different provider specify the type of the provider in the
 * type field, like this:
 * <p>
 * Example:
 * 
 * <pre>
 * authentication:
 *   type: LDAP
 * </pre>
 * </p>
 * 
 * @since 3.4
 */
public class IdentityConfiguration {

	@NotEmpty
	private String type = "PROP_FILE";

	private boolean adminParty = false;
	
	private Map<String, Object> properties = newHashMap();

	@JsonProperty
	public String getType() {
		return type;
	}

	@JsonProperty
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty
	public boolean isAdminParty() {
		return adminParty;
	}

	@JsonProperty
	public void setAdminParty(boolean adminParty) {
		this.adminParty = adminParty;
	}
	
	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	@JsonAnySetter
	public void setProperties(String key, Object value) {
		this.properties.put(key, value);
	}

}