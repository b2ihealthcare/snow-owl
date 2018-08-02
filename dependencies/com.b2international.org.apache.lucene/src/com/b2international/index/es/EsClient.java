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

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.b2international.org.apache.lucene.Activator;

/**
 * @since 6.6
 */
public final class EsClient {

	public static final RestHighLevelClient create(final HttpHost host) {
		
		// XXX: Temporarily set the thread context classloader to this bundle while ES client is initializing 
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(Activator.class.getClassLoader());
			return new RestHighLevelClient(RestClient.builder(host));
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}
}
