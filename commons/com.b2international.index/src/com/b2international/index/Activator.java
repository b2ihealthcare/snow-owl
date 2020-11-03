/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.codecs.Codec;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import com.b2international.index.es.EsNode;
import com.b2international.index.es.client.EsClient;
import com.google.common.base.Throwables;

public class Activator implements BundleActivator {

	private static final int LARGE_BUFFER_LIMIT = 1024 * 1024 * 1024;
	
	private static ClassLoader bundleClassLoader;

	public void start(BundleContext context) throws Exception {
		
		Bundle bundle = context.getBundle();
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		bundleClassLoader = bundleWiring.getClassLoader();
		
		/*
		 * Trigger default Codec class loading. 
		 * This way we prevent that the class loading will be performed by a thread started from the Lucene's core bundle.
		 */
		Codec.getDefault();
		
		// Elasticsearch's default JVM configurations for third party dependencies 
		// Prevent Log4j2 from registering a shutdown hook; we will manage the logging system's lifecycle manually.
		System.setProperty("log4j.shutdownHookEnabled", "false");
		System.setProperty("log4j2.disable.jmx", "true");
		
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

	public void stop(BundleContext context) throws Exception {
		EsClient.closeAll();
		EsNode.stop();
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
}
