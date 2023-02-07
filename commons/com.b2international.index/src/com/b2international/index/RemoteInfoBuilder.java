/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.index;

import static com.google.common.collect.Maps.newHashMap;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.RemoteInfo;

/**
 * @since 8.9
 */
public class RemoteInfoBuilder {

	private static final String API_KEY_FORMAT = "ApiKey %s";

	public static RemoteInfoBuilder builder() {
		return new RemoteInfoBuilder();
	}

	private RemoteInfoBuilder() {}

	private String scheme;
	private String host;
	private int port;
	private BytesReference query;
	private String username;
	private String password;
	private final Map<String, String> headers = newHashMap();

	private TimeValue socketTimeout = RemoteInfo.DEFAULT_SOCKET_TIMEOUT;
	private TimeValue connectionTimeout = RemoteInfo.DEFAULT_CONNECT_TIMEOUT;

	public RemoteInfoBuilder uri(final URI uri) {
		this.scheme = uri.getScheme();
		this.host = uri.getHost();
		this.port = uri.getPort();
		return this;
	}

	public RemoteInfoBuilder scheme(final String scheme) {
		this.scheme = scheme;
		return this;
	}

	public RemoteInfoBuilder host(final String host) {
		this.host = host;
		return this;
	}

	public RemoteInfoBuilder port(final int port) {
		this.port = port;
		return this;
	}

	public RemoteInfoBuilder query(final String query) {
		this.query = new BytesArray(query);
		return this;
	}

	public RemoteInfoBuilder query(final QueryBuilder query) {
		return query(query.toString());
	}

	public RemoteInfoBuilder username(final String username) {
		this.username = username;
		return this;
	}

	public RemoteInfoBuilder password(final String password) {
		this.password = password;
		return this;
	}

	public RemoteInfoBuilder apiKey(final String apiKey) {
		headers.put("Authorization", String.format(API_KEY_FORMAT, apiKey));
		return this;
	}

	public RemoteInfoBuilder socketTimeout(final long duration, final TimeUnit timeUnit) {
		this.socketTimeout = new TimeValue(duration, timeUnit);
		return this;
	}

	public RemoteInfoBuilder connectionTimeout(final long duration, final TimeUnit timeUnit) {
		this.connectionTimeout = new TimeValue(duration, timeUnit);
		return this;
	}

	public RemoteInfo build() {
		return new RemoteInfo(
				scheme,
				host,
				port,
				null, // path prefix is not used
				query,
				username,
				password,
				headers,
				socketTimeout,
				connectionTimeout
			);
	}

}
