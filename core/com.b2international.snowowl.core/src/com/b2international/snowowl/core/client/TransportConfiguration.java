/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.client;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration parameters for repository connection.
 * 
 * @since 3.4
 */
public class TransportConfiguration {

	private static final int DEFAULT_CONNECTION_TIMEOUT_SECONDS = 10;
	private static final int DEFAULT_WATCHDOG_RATE_SECONDS = 30;
	private static final int DEFAULT_WATCHDOG_TIMEOUT_SECONDS = 300;
	private static final String DEFAULT_CERTIFICATE_PATH = "";
	private static final int DEFAULT_MAX_OBJECT_SIZE = Integer.MAX_VALUE - 1024;
	public static final int DEFAULT_UPLOAD_CHUNK_SIZE = 10_485_760; // 10 Mb
	public static final int DEFAULT_DOWNLOAD_CHUNK_SIZE = 1_048_576; // 1 Mb 
	
	@Min(0)
	@Max(300)
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT_SECONDS;
	
	@Min(0)
	private int watchdogRate = DEFAULT_WATCHDOG_RATE_SECONDS;
	
	@Min(0)
	private int watchdogTimeout = DEFAULT_WATCHDOG_TIMEOUT_SECONDS;
	
	@NotNull
	private String certificatePath = DEFAULT_CERTIFICATE_PATH;
	
	@Min(0)
	private int maxObjectSize = DEFAULT_MAX_OBJECT_SIZE;
	
	@Min(0)
	private int uploadChunkSize = DEFAULT_UPLOAD_CHUNK_SIZE;
	
	@Min(0)
	private int downloadChunkSize = DEFAULT_DOWNLOAD_CHUNK_SIZE;
	
	/**
	 * @return the number of seconds to wait before a connection attempt times out at login.
	 */
	@JsonProperty
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * @param connectionTimeout the connectionTimeout to set
	 */
	@JsonProperty
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * @return the interval in seconds, at which messages should be exchanged to determine whether a connection
	 * is working correctly.
	 */
	@JsonProperty
	public int getWatchdogRate() {
		return watchdogRate;
	}

	/**
	 * @param watchdogRate the watchdogRate to set
	 */
	@JsonProperty
	public void setWatchdogRate(int watchdogRate) {
		this.watchdogRate = watchdogRate;
	}

	/**
	 * @return the timeout in seconds, after which a connection, that seems to be working correctly, is
	 * determined to be broken.
	 */
	@JsonProperty
	public int getWatchdogTimeout() {
		return watchdogTimeout;
	}
	
	/**
	 * @param watchdogTimeout the watchdogTimeout to set
	 */
	@JsonProperty
	public void setWatchdogTimeout(int watchdogTimeout) {
		this.watchdogTimeout = watchdogTimeout;
	}
	
	/**
	 * The path to the trusted certificate relative to the configuration folder
	 */
	@JsonProperty
	public String getCertificatePath() {
		return certificatePath;
	}
	
	/**
	 * @param certificatePath the trusted certificate path to set (relative to the configuration folder)
	 */
	@JsonProperty
	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	/**
	 * @return the maximum number of bytes allowed to be serialized/deserialized as a Java object over the network
	 */
	@JsonProperty
	public int getMaxObjectSize() {
		return maxObjectSize;
	}
	
	/**
	 * @param maxObjectSize the object size limit to set
	 */
	@JsonProperty
	public void setMaxObjectSize(int maxObjectSize) {
		this.maxObjectSize = maxObjectSize;
	}
	
	@JsonProperty
	public int getUploadChunkSize() {
		return uploadChunkSize;
	}
	
	@JsonProperty
	public void setUploadChunkSize(int uploadChunkSize) {
		this.uploadChunkSize = uploadChunkSize;
	}
	
	@JsonProperty
	public int getDownloadChunkSize() {
		return downloadChunkSize;
	}
	
	@JsonProperty
	public void setDownloadChunkSize(int downloadChunkSize) {
		this.downloadChunkSize = downloadChunkSize;
	}
	
}
