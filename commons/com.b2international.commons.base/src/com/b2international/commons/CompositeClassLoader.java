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
package com.b2international.commons;

import static java.util.Collections.synchronizedSet;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @since 2.7
 */
public class CompositeClassLoader extends ClassLoader {

	private final Set<ClassLoader> classLoaders = synchronizedSet(Sets.<ClassLoader>newHashSet());

	public void add(ClassLoader classLoader) {
		if (classLoader != null) {
			classLoaders.add(classLoader);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return loadClass(name);
	}
	
	@Override
	public Class loadClass(String name) throws ClassNotFoundException {
		for (final Iterator<ClassLoader> iterator = classLoaders.iterator(); iterator.hasNext();) {
			final ClassLoader classLoader = iterator.next();
			try {
				return classLoader.loadClass(name);
			} catch (ClassNotFoundException e) {
				// don't do anything try to resolve from another class loader
			}
		}
		// if all loader fails to load the class throw the exception
		throw new ClassNotFoundException(name);
	}

	public void remove(ClassLoader classLoader) {
		if (classLoader != null) {
			classLoaders.remove(classLoader);
		}
	}

}