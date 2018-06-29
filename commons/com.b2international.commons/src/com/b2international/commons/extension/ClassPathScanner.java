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
package com.b2international.commons.extension;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader;
import org.eclipse.osgi.internal.baseadaptor.DevClassPathHelper;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.classloaderhandler.OSGiDefaultClassLoaderHandler;
import io.github.lukehutch.fastclasspathscanner.scanner.ClasspathOrder;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanSpec;
import io.github.lukehutch.fastclasspathscanner.utils.LogNode;
import io.github.lukehutch.fastclasspathscanner.utils.ReflectionUtils;

/**
 * @since 7.0
 */
@SuppressWarnings("restriction")
public enum ClassPathScanner {
	
	INSTANCE;

	private static final long SYSTEM_BUNDLE_ID = 0L;
	
	private final ScanResult registry;

	private ClassPathScanner() {
		List<ClassLoader> classLoaders = newArrayList();
		for (BundleDescription bundleDescription : Platform.getPlatformAdmin().getState(false).getBundles()) {
			Bundle bundle = bundleDescription.getBundle();
			if (SYSTEM_BUNDLE_ID  == bundle.getBundleId()) {
				continue;
			}
			ClassLoader classLoader = bundle.adapt(BundleWiring.class).getClassLoader();
			if (classLoader != null) {
				classLoaders.add(classLoader);
			}
		}
		registry = new FastClasspathScanner()
			.overrideClassLoaders(classLoaders.toArray(new ClassLoader[classLoaders.size()]))
			.registerClassLoaderHandler(PDEOSGiDefaultClassLoaderHandler.class)
			.scan();
	}
	
	/**
	 * Returns extension classes annotated with the given annotation.
	 * 
	 * @param annotation
	 * @return
	 */
	public Collection<Class<?>> getExtensionsByAnnotation(Class<? extends Annotation> annotation) {
		final List<String> namesOfClassesWithAnnotation = registry.getNamesOfClassesWithAnnotation(annotation);
		return registry.classNamesToClassRefs(namesOfClassesWithAnnotation);
	}
	
	/**
	 * Returns extension classes that implement the given interface type.
	 * @param type
	 * @return
	 */
	public Collection<Class<?>> getExtensionsByInterface(Class<?> type) {
		final List<String> namesOfClassesWithAnnotation = registry.getNamesOfClassesImplementing(type);
		return registry.classNamesToClassRefs(namesOfClassesWithAnnotation);
	}
	
	public static class PDEOSGiDefaultClassLoaderHandler extends OSGiDefaultClassLoaderHandler {
	
		@Override
		public void handle(ScanSpec scanSpec, ClassLoader classloader, ClasspathOrder classpathOrderOut, LogNode log) {
			final Object classpathManager = ReflectionUtils.invokeMethod(classloader, "getClasspathManager", false);
	        final Object[] entries = (Object[]) ReflectionUtils.getFieldVal(classpathManager, "entries", false);
	        if (entries != null) {
	            for (int i = 0; i < entries.length; i++) {
	                final Object bundleFile = ReflectionUtils.invokeMethod(entries[i], "getBundleFile", false);
	                final File baseFile = (File) ReflectionUtils.invokeMethod(bundleFile, "getBaseFile", false);
	                if (baseFile != null && baseFile.isDirectory() && DevClassPathHelper.inDevelopmentMode()) {
	                	DefaultClassLoader cl = (DefaultClassLoader) classloader;
	                	String[] devClassPath = DevClassPathHelper.getDevClassPath(cl.getBundle().getSymbolicName());
	                	for (String cp : devClassPath) {
                			final File cpFile = new File(baseFile, cp);
                			if (cpFile.isDirectory()) {
                				classpathOrderOut.addClasspathElement(cpFile.getPath(), classloader, log);
                			}
	                	}
	                }
	            }
	        }
		}
		
	}
	
}
