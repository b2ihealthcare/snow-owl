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
package com.b2international.index.es;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.elasticsearch.common.Strings;

import com.google.common.collect.ImmutableList;

/**
 * @since 6.7
 */
public final class EsClientConfiguration {

	public static final String TCP_SCHEME = "tcp://";
	public static final String HTTP_SCHEME = "http://";
	public static final String HTTPS_SCHEME = "https://";
	
	private final String clusterName;
	private final String clusterUrl;
	private final String username;
	private final String password;
	private final int connectTimeout;
	private final int socketTimeout;

	public EsClientConfiguration(
			final String clusterName,
			final String clusterUrl, 
			final String username, 
			final String password, 
			final int connectTimeout, 
			final int socketTimeout) {
		this.clusterName = clusterName;
		this.clusterUrl = clusterUrl;
		this.username = username;
		this.password = password;
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
		checkArgument(
			isTcp() || isHttp(), 
			"Unsupported networking scheme in clusterUrl: %s. Supported schemes are: %s.", clusterUrl, ImmutableList.of(TCP_SCHEME, HTTP_SCHEME, HTTPS_SCHEME)
		);
	}

	public String getClusterName() {
		return clusterName;
	}
	
	public String getClusterUrl() {
		return clusterUrl;
	}
	
	public String getUserName() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	@Override
	public int hashCode() {
		return Objects.hash(clusterUrl);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final EsClientConfiguration other = (EsClientConfiguration) obj;

		// First client configuration for a host wins
		return Objects.equals(clusterUrl, other.clusterUrl);
	}

	public boolean isHttp() {
		return clusterUrl.startsWith(HTTP_SCHEME) || clusterUrl.startsWith(HTTPS_SCHEME);
	}
	
	public boolean isTcp() {
		return clusterUrl.startsWith(TCP_SCHEME);
	}

	/**
	 * @return <code>true</code> if both username and password is provided, meaning that the target Elasticsearch cluster is protected by authentication, <code>false</code> if not protected.
	 */
	public boolean isProtected() {
		return !Strings.isNullOrEmpty(getUserName()) && !Strings.isNullOrEmpty(getPassword());
	}
	
}
