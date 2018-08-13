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
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
			.setAnnotationVisibility(RetentionPolicy.RUNTIME)
			.overrideClassLoaders(classLoaders.toArray(new ClassLoader[classLoaders.size()]))
			.registerClassLoaderHandler(PDEOSGiDefaultClassLoaderHandler.class)
			.scan();
	}
	
	/**
	 * Returns classes annotated with the given annotation.
	 * 
	 * @param annotation
	 * @return
	 */
	public Collection<Class<?>> getComponentClasses(Class<? extends Annotation> annotation) {
		final List<String> namesOfClassesWithAnnotation = registry.getNamesOfClassesWithAnnotation(annotation);
		return getComponentClasses(namesOfClassesWithAnnotation);
	}

	/**
	 * Returns classes that implement the given interface type.
	 * @param type
	 * @return
	 */
	public Collection<Class<?>> getComponentsClassesByInterface(Class<?> type) {
		final List<String> namesOfClassesWithAnnotation = registry.getNamesOfClassesImplementing(type);
		return getComponentClasses(namesOfClassesWithAnnotation);
	}
	
	/**
	 * Returns classes that extend the given superclass type.
	 * @param type
	 * @return
	 */
	public Collection<Class<?>> getComponentsClassesBySuperclass(Class<?> type) {
		final List<String> namesOfClassesWithAnnotation = registry.getNamesOfSubclassesOf(type);
		return getComponentClasses(namesOfClassesWithAnnotation);
	}
	
	/*Filters and returns Class<?> instances for the given classNames, where the class is annotated with the Component annotation*/
	private List<Class<?>> getComponentClasses(final List<String> classNames) {
		return registry.classNamesToClassRefs(classNames).stream()
				.filter(type -> type.isAnnotationPresent(Component.class))
				.collect(Collectors.toList());
	}
	
	/**
	 * Returns instances of classes annotated with the given annotation.
	 * @param annotation
	 * @return
	 */
	public Collection<Object> getComponentsByAnnotation(Class<? extends Annotation> annotation) {
		return getComponentsByAnnotation(annotation, Object.class);
	}
	
	/**
	 * Returns instances of classes annotated with the given annotation and subtype of the given type.
	 * 
	 * @param annotation - the expected annotation
	 * @param expectedType - the expected type
	 * @return
	 */
	public <T> Collection<T> getComponentsByAnnotation(Class<? extends Annotation> annotation, Class<T> expectedType) {
		return instantiate(getComponentClasses(annotation), expectedType);
	}
	
	/**
	 * Returns instances of classes implementing the given interface.
	 * 
	 * @param interfaceType - the expected type
	 * @return
	 */
	public <T> Collection<T> getComponentsByInterface(Class<T> interfaceType) {
		return instantiate(getComponentsClassesByInterface(interfaceType), interfaceType);
	}
	
	/**
	 * Returns instances of classes extending the given superclass.
	 * 
	 * @param interfaceType - the expected type
	 * @return
	 */
	public <T> Collection<T> getComponentsBySuperclass(Class<T> superclass) {
		return instantiate(getComponentsClassesBySuperclass(superclass), superclass);
	}
	
	private <T> Collection<T> instantiate(Collection<Class<?>> classes, Class<T> type) {
		return classes.stream()
			.filter(type::isAssignableFrom)
			.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
			.map(clazz -> {
				try {
					return type.cast(clazz.newInstance());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Failed to instantiate type: " + clazz, e);
				}
			})
			.collect(Collectors.toList());
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
