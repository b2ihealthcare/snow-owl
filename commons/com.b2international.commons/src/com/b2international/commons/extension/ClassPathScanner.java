/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.b2international.commons.CommonsActivator;
import com.google.common.base.Stopwatch;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * @since 7.0
 */
public enum ClassPathScanner {
	
	INSTANCE;

	private static final long SYSTEM_BUNDLE_ID = 0L;
	private final ScanResult registry;

	private ClassPathScanner() {
		List<ClassLoader> classLoaders = newArrayList();
		Stopwatch w = Stopwatch.createStarted();
		for (Bundle bundle : CommonsActivator.getContext().getBundles()) {
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
		System.err.println("Bundle read: " + w + " nob: " + classLoaders.size());
		w.reset().start();
		
		registry = new ClassGraph()
				.disableRuntimeInvisibleAnnotations()
				.overrideClassLoaders(classLoaders.toArray(new ClassLoader[classLoaders.size()]))
				.enableAllInfo()
				.scan();
		
		System.err.println("Classpath scanning: " + w);
		w.reset().start();
		
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

}
