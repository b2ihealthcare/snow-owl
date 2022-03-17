/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.os;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.codecs.Codec;
import org.opensearch.client.HttpAsyncResponseConsumerFactory;
import org.opensearch.client.HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.node.Node;

import com.b2international.index.IndexClient;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.os.admin.OsIndexAdmin;
import com.b2international.index.os.client.OsClient;
import com.b2international.index.os.client.tcp.OsTcpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

/**
 * @since 5.10
 */
public final class OsIndexClientFactory implements IndexClientFactory {

	private static final Path DEFAULT_PATH = Paths.get("target", "resources", "indexes");

	private static final int LARGE_BUFFER_LIMIT = 1024 * 1024 * 1024;
	
	private static ClassLoader bundleClassLoader;

	@Override
	public void start(ClassLoader bundleClassLoader) throws Exception {
		OsIndexClientFactory.bundleClassLoader = bundleClassLoader;
		
		/*
		 * Trigger default Codec class loading. 
		 * This way we prevent that the class loading will be performed by a thread started from the Lucene's core bundle.
		 */
		Codec.getDefault();
		
		// Elasticsearch's default JVM configurations for third party dependencies 
		// Prevent Log4j2 from registering a shutdown hook; we will manage the logging system's lifecycle manually.
		System.setProperty("log4j.shutdownHookEnabled", "false");
		System.setProperty("log4j2.disable.jmx", "true");
		// Disable log4j2 formatMsgNoLookups to mitigate CVE-2021-44228 security vulnerability, see more info at: https://logging.apache.org/log4j/2.x/security.html
		System.setProperty("log4j2.formatMsgNoLookups", "true");
		
		System.setProperty("io.netty.noUnsafe", "true");
		System.setProperty("io.netty.noKeySetOptimization", "true");
		System.setProperty("io.netty.recycler.maxCapacityPerThread", "0");
		
		withTccl(() -> {
			// Initialize Log4j2
			LogManager.getContext();
			
			/* 
			 * FIXME: Set the default response consumer factory via reflection to allow processing greater than 
			 * 100 MB of data as its input. Reflection is a really bad (but also the only) way of doing this at
			 * the moment!
			 */
			final HttpAsyncResponseConsumerFactory consumerFactory = new HeapBufferedResponseConsumerFactory(LARGE_BUFFER_LIMIT);
			final Field defaultField = HttpAsyncResponseConsumerFactory.class.getDeclaredField("DEFAULT");
	        defaultField.setAccessible(true);
			
	        final Field modifiers = getModifiersField();
	        modifiers.setAccessible(true);
	        modifiers.setInt(defaultField, defaultField.getModifiers() & ~Modifier.FINAL);
	        
	        defaultField.set(null, consumerFactory);

	        // Initialize Elasticsearch's XContent extensibility mechanism 
			return JsonXContent.contentBuilder();
		});
	}

	private Field getModifiersField() throws IllegalAccessException, NoSuchFieldException {
		
		try {
			// Pre-JDK 12: retrieve "modifiers" field on Field directly
			return Field.class.getDeclaredField("modifiers");
		} catch (NoSuchFieldException e) {
			try {

				// JDK 12: gain access to private getDeclaredFields0 method that returns unfiltered results
				Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
				boolean accessibleBeforeSet = getDeclaredFields0.isAccessible();
				
				try {
					getDeclaredFields0.setAccessible(true);
					Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
					return Arrays.stream(fields)
							.filter(f -> "modifiers".equals(f.getName()))
							.findFirst()
							.orElseThrow(() -> e);
				} finally {
					getDeclaredFields0.setAccessible(accessibleBeforeSet);
				}
				
			} catch (NoSuchMethodException ex) {
				e.addSuppressed(ex);
				throw e;
			} catch (InvocationTargetException ex) {
				e.addSuppressed(ex);
				throw e;
			}
		}
	}

	@Override
	public void stop() throws Exception {
		OsClient.closeAll();
		OsNode.stop();
		withTccl(() -> LogManager.shutdown());
		bundleClassLoader = null;
	}
	
	public static void withTccl(Runnable runnable) {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(bundleClassLoader);
			runnable.run();
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}
	
	public static <T> T withTccl(Callable<T> callable) {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(bundleClassLoader);

			try {
				return callable.call();
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
				throw new RuntimeException(e);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}
	
	@Override
	public IndexClient createClient(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings) {
		final boolean persistent = settings.containsKey(DATA_DIRECTORY);
		final Object dataSetting = settings.getOrDefault(DATA_DIRECTORY, DEFAULT_PATH);
		final Object configSetting = settings.getOrDefault(CONFIG_DIRECTORY, DEFAULT_PATH);
		final Path dataDirectory = dataSetting instanceof Path ? (Path) dataSetting : Paths.get((String) dataSetting);
		final Path configDirectory = configSetting instanceof Path ? (Path) configSetting : Paths.get((String) configSetting);

		// generic OS cluster settings
		final String clusterName = (String) settings.getOrDefault(CLUSTER_NAME, DEFAULT_CLUSTER_NAME);
		final Object connectTimeoutSetting = settings.getOrDefault(CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
		final Object socketTimeoutSetting = settings.getOrDefault(SOCKET_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
		final int connectTimeout = connectTimeoutSetting instanceof Integer ? (int) connectTimeoutSetting : Integer.parseInt((String) connectTimeoutSetting);
		final int socketTimeout = socketTimeoutSetting instanceof Integer ? (int) socketTimeoutSetting : Integer.parseInt((String) socketTimeoutSetting);
		final String username = (String) settings.getOrDefault(CLUSTER_USERNAME, "");
		final String password = (String) settings.getOrDefault(CLUSTER_PASSWORD, "");
		
		final OsClient client;
		if (settings.containsKey(CLUSTER_URL)) {
			final String clusterUrl = (String) settings.get(CLUSTER_URL);
			client = OsClient.create(new OsClientConfiguration(clusterName, clusterUrl, username, password, connectTimeout, socketTimeout));
		} else {
			// Start an embedded OS node only if a cluster URL is not set
			Node node = OsNode.getInstance(clusterName, configDirectory, dataDirectory, persistent);
			// check sysprop to force HTTP client when still using embedded mode
			if (System.getProperty("so.index.os.useHttp") != null) {
				client = OsClient.create(new OsClientConfiguration(clusterName, "http://127.0.0.1:9200", username, password, connectTimeout, socketTimeout));
			} else {
				// and use the local NodeClient to communicate via the embedded node
				client = new OsTcpClient(node.client());
			}
		}
		
		return new OsIndexClient(new OsIndexAdmin(client, mapper, name, mappings, settings), mapper);
	}
}
