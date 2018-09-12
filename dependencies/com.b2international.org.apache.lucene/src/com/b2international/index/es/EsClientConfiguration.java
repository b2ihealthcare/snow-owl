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

import java.util.Objects;

import org.apache.http.HttpHost;

/**
 * @since 6.7
 */
public final class EsClientConfiguration {

	private final int connectTimeout;
	private final int socketTimeout;
	private final HttpHost host;
	private String username;
	private String password;

	public EsClientConfiguration(final int connectTimeout, final int socketTimeout, final HttpHost host, String username, String password) {
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
		this.host = host;
		this.username = username;
		this.password = password;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public HttpHost getHost() {
		return host;
	}
	
	public String getUserName() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

	@Override
	public int hashCode() {
		return Objects.hash(host);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final EsClientConfiguration other = (EsClientConfiguration) obj;

		// First client configuration for a host wins
		return Objects.equals(host, other.host);
	}
}
