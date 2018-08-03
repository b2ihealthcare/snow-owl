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
package com.b2international.org.apache.lucene;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.codecs.Codec;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import com.b2international.index.es.EsNode;
import com.google.common.base.Throwables;

public class Activator implements BundleActivator {

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
		
		// Prevent Log4j2 from registering a shutdown hook; we will manage the logging system's lifecycle manually.
		System.setProperty("log4j.shutdownHookEnabled", "false");
		withTccl(() -> {
			LogManager.getContext();
			return JsonXContent.contentBuilder();
		});
	}

	public void stop(BundleContext context) throws Exception {
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
