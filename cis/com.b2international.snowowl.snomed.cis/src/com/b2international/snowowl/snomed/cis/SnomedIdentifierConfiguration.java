/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
 */package com.b2international.snowowl.snomed.cis;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Identifier related application level configuration parameters.
 * 
 * @since 4.5
 */
public class SnomedIdentifierConfiguration {

	public enum IdGenerationStrategy {
		EMBEDDED,
		CIS // Component Identifier Service (IHTSDO) based service
	}

	public static final int DEFAULT_ID_GENERATION_ATTEMPTS = 100_000;

	@JsonProperty(value = "strategy", required = false)
	private IdGenerationStrategy strategy = IdGenerationStrategy.EMBEDDED;
	@JsonProperty(value = "cisBaseUrl", required = false)
	private String cisBaseUrl;
	@JsonProperty(value = "cisContextRoot", required = false)
	private String cisContextRoot;
	@JsonProperty(value = "cisUserName", required = false)
	private String cisUserName;
	@JsonProperty(value = "cisPassword", required = false)
	private String cisPassword;
	// the key to associate the client software within the external CIS service
	@JsonProperty(value = "cisClientSoftwareKey", required = false)
	private String cisClientSoftwareKey = "Snow Owl";
	@Min(1)
	@JsonProperty(value = "cisNumberOfPollTries", required = false)
	private long cisNumberOfPollTries = 240;
	@Min(1)
	@JsonProperty(value = "cisTimeBetweenPollTries", required = false)
	private long cisTimeBetweenPollTries = 500;
	
	@JsonProperty(required = false)
	private int cisMaxConnections = 100;
	
	@JsonProperty(required = false)
	private int maxIdGenerationAttempts = DEFAULT_ID_GENERATION_ATTEMPTS;

	@Min(1)
	@JsonProperty(value = "cisNumberOfReauthTries", required = false)
	private int cisNumberOfReauthTries = 2;
	
	public IdGenerationStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(IdGenerationStrategy strategy) {
		this.strategy = strategy;
	}

	public String getCisBaseUrl() {
		return cisBaseUrl;
	}

	public void setCisBaseUrl(String cisBaseUrl) {
		this.cisBaseUrl = cisBaseUrl;
	}

	public String getCisContextRoot() {
		return cisContextRoot;
	}

	public void setCisContextRoot(String cisContextRoot) {
		this.cisContextRoot = cisContextRoot;
	}

	public String getCisUserName() {
		return cisUserName;
	}

	public void setCisUserName(String cisUserName) {
		this.cisUserName = cisUserName;
	}

	public String getCisPassword() {
		return cisPassword;
	}

	public void setCisPassword(String cisPassword) {
		this.cisPassword = cisPassword;
	}

	public String getCisClientSoftwareKey() {
		return cisClientSoftwareKey;
	}

	public void setCisClientSoftwareKey(String cisClientSoftwareKey) {
		this.cisClientSoftwareKey = cisClientSoftwareKey;
	}

	public long getCisNumberOfPollTries() {
		return cisNumberOfPollTries;
	}

	public void setCisNumberOfPollTries(long cisNumberOfPollTries) {
		this.cisNumberOfPollTries = cisNumberOfPollTries;
	}

	public long getCisTimeBetweenPollTries() {
		return cisTimeBetweenPollTries;
	}

	public void setCisTimeBetweenPollTries(long cisTimeBetweenPollTries) {
		this.cisTimeBetweenPollTries = cisTimeBetweenPollTries;
	}

	public int getCisMaxConnections() {
		return cisMaxConnections ;
	}
	
	public void setCisMaxConnections(int cisMaxConnections) {
		this.cisMaxConnections = cisMaxConnections;
	}

	public int getMaxIdGenerationAttempts() {
		return maxIdGenerationAttempts;
	}
	
	public void setMaxIdGenerationAttempts(int maxIdGenerationAttempts) {
		this.maxIdGenerationAttempts = maxIdGenerationAttempts;
	}

	public void setCisNumberOfReauthTries(int cisNumberOfReauthTries) {
		this.cisNumberOfReauthTries = cisNumberOfReauthTries;
	}
	
	public int getCisNumberOfReauthTries() {
		return cisNumberOfReauthTries;
	}
}
