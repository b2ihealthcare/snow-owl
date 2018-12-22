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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.google.common.collect.ImmutableList;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import nonapi.io.github.classgraph.ScanSpec;
import nonapi.io.github.classgraph.classloaderhandler.AntClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry;
import nonapi.io.github.classgraph.classloaderhandler.EquinoxClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.EquinoxContextFinderClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.FelixClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.JBossClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.JPMSClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.OSGiDefaultClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.ParentLastDelegationOrderTestClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.URLClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.WeblogicClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.WebsphereLibertyClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.WebsphereTraditionalClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

/**
 * @since 7.0
 */
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
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			if (wiring != null) {
				ClassLoader classLoader = wiring.getClassLoader();
				if (classLoader != null) {
					classLoaders.add(classLoader);
				}
			}
		}
		
		// XXX hacking default class loader registry to make ClassGraph work with Equinox 3.9
		try {
			Field f = ClassLoaderHandlerRegistry.class.getDeclaredField("CLASS_LOADER_HANDLERS");
			f.setAccessible(true);
			
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			
			f.set(null, ImmutableList.<ClassLoaderHandlerRegistryEntry>builder()
					// ClassLoaderHandlers for other ClassLoaders that are handled by ClassGraph
					.add(new ClassLoaderHandlerRegistryEntry(AntClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(EquinoxClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(EquinoxContextFinderClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(FelixClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(JBossClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(WeblogicClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(WebsphereLibertyClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(WebsphereTraditionalClassLoaderHandler.class))
			        // XXX Register PDE Dev Mode aware class loader handler to make it work with Equinox 3.9 class loading
			        .add(new ClassLoaderHandlerRegistryEntry(PDEOSGiDefaultClassLoaderHandler.class))
			        .add(new ClassLoaderHandlerRegistryEntry(OSGiDefaultClassLoaderHandler.class))
	                // For unit testing of PARENT_LAST delegation order
			        .add(new ClassLoaderHandlerRegistryEntry(ParentLastDelegationOrderTestClassLoaderHandler.class))
	                // JPMS support
			        .add(new ClassLoaderHandlerRegistryEntry(JPMSClassLoaderHandler.class))
	                // Java 7/8 support (list last, as fallback)
			        .add(new ClassLoaderHandlerRegistryEntry(URLClassLoaderHandler.class))
					.build());
		} catch (Exception e) {
			throw new RuntimeException("Couldn't hack ClassGraph :-(", e);
		}
				
		registry = new ClassGraph()
			.disableRuntimeInvisibleAnnotations()
			.overrideClassLoaders(classLoaders.toArray(new ClassLoader[classLoaders.size()]))
			.scan();
	}
	
	/**
	 * Returns classes annotated with the given annotation.
	 * 
	 * @param annotation
	 * @return
	 */
	public Collection<Class<?>> getComponentClasses(Class<? extends Annotation> annotation) {
		final ClassInfoList namesOfClassesWithAnnotation = registry.getClassesWithAnnotation(annotation.getName());
		return getComponentClasses(namesOfClassesWithAnnotation);
	}

	/**
	 * Returns classes that implement the given interface type.
	 * @param type
	 * @return
	 */
	public Collection<Class<?>> getComponentsClassesByInterface(Class<?> type) {
		final ClassInfoList namesOfClassesWithAnnotation = registry.getClassesImplementing(type.getName());
		return getComponentClasses(namesOfClassesWithAnnotation);
	}
	
	/**
	 * Returns classes that extend the given superclass type.
	 * @param type
	 * @return
	 */
	public Collection<Class<?>> getComponentsClassesBySuperclass(Class<?> type) {
		final ClassInfoList namesOfClassesWithAnnotation = registry.getSubclasses(type.getName());
		return getComponentClasses(namesOfClassesWithAnnotation);
	}
	
	/*Filters and returns Class<?> instances for the given classNames, where the class is annotated with the Component annotation*/
	private List<Class<?>> getComponentClasses(final ClassInfoList classes) {
		return classes.stream().map(ClassInfo::loadClass)
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
	 * @param interfaceType - the expected interface
	 * @return
	 */
	public <T> Collection<T> getComponentsByInterface(Class<T> interfaceType) {
		return instantiate(getComponentsClassesByInterface(interfaceType), interfaceType);
	}
	
	/**
	 * Returns instances of classes extending the given superclass.
	 * 
	 * @param superclass - the expected superclass
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
	                if (baseFile != null) {
	                	try {
							Class<?> devClassPathHelperClass = Class.forName("org.eclipse.osgi.internal.baseadaptor.DevClassPathHelper", false, classloader);
							handleEquinox39(baseFile, classloader, classpathOrderOut, log, devClassPathHelperClass);
						} catch (ClassNotFoundException ignored) {
						}
	                }
	            }
	        }
		}

		private static void handleEquinox39(final File baseFile, ClassLoader classloader, ClasspathOrder classpathOrderOut, LogNode log, Class<?> devClassPathHelperClass) {
			Boolean inDevMode = (Boolean) ReflectionUtils.invokeStaticMethod(devClassPathHelperClass, "inDevelopmentMode", true);
        	if (inDevMode && baseFile.isDirectory()) {
        		Bundle bundle = (Bundle) ReflectionUtils.invokeMethod(classloader, "getBundle", true);
	    		String[] devClassPath = (String[]) ReflectionUtils.invokeStaticMethod(devClassPathHelperClass, "getDevClassPath", String.class, bundle.getSymbolicName(), true);
	    		for (String cp : devClassPath) {
	    			final File cpFile = new File(baseFile, cp);
	    			if (cpFile.isDirectory()) {
	    				classpathOrderOut.addClasspathElement(cpFile.getPath(), classloader, log);
	    			}
	    		}
	    	} else {
	    		classpathOrderOut.addClasspathElement(baseFile.getPath(), classloader, log);
	    	}
		}
		
	}

}
