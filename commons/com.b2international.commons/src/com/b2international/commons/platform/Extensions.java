/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.platform;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

import com.b2international.commons.ClassUtils;

/**
 * Useful utility methods when working with {@link IExtension}s, {@link IExtensionPoint}s and {@link IConfigurationElement}s.
 * 
 * @since 3.3
 */
public class Extensions {

	private Extensions() {
	}

	/**
	 * Returns a collection of newly created instances of the given extension point's extensions <code>class</code> attribute if it conforms to the
	 * given type parameter.
	 * 
	 * @param extensionPoint
	 * @param type
	 * @return
	 */
	public static final <T> Collection<T> getExtensions(final String extensionPoint, final Class<T> type) {
		return getExtensions(extensionPoint, "class", type);
	}
	
	/**
	 * Returns the instance of an 'class' extension that has the highest-priority.
	 * @param extensionPoint
	 * @param type
	 * @return extension instance
	 */
	public static final <T> T getFirstPriorityExtension(final String extensionPoint, final Class<T> type) {
		checkNotNull(extensionPoint, "extensionPoint");
		checkNotNull(type, "type");

		final String priorityAttributeName = "priority";
		final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionPoint);
		final Optional<IConfigurationElement> firstPriorityElement = Arrays.stream(elements)
				.filter(e -> hasAttributeOf(e, "class"))
				.filter(e -> hasAttributeOf(e, priorityAttributeName))
				.filter(e -> {
					try {
						Integer.parseInt(e.getAttribute(priorityAttributeName));
						return true;
					} catch (NumberFormatException | InvalidRegistryObjectException ex) {
						return false;
					}
				})
				.sorted((e1, e2) -> {
					int p1 = Integer.parseInt(e1.getAttribute(priorityAttributeName));
					int p2 = Integer.parseInt(e2.getAttribute(priorityAttributeName));
					return -Integer.compare(p1, p2); // apply reverse sorting: higher priority appears earlier
				})
				.findFirst();

		final Optional<T> firstPriorityInstance = firstPriorityElement.map(e -> {
			try {
				return instantiate(e, "class", type);
			} catch (CoreException ex) {
				String bundleName = e.getContributor().getName();
				throw new RuntimeException(String.format("Exception happened when creating element from %s bundle's extension: %s", bundleName, extensionPoint));
			}
		});

		return firstPriorityInstance.orElse(null);
	}

	/**
	 * Returns a collection of newly created instances based on the given extension point ID and classAttributeName.
	 * 
	 * @param extensionPoint
	 * @param classAttributeName
	 * @param type
	 * @return
	 * @see #getExtensions(String, Class)
	 */
	public static final <T> Collection<T> getExtensions(final String extensionPoint, final String classAttributeName, final Class<T> type) {
		checkNotNull(extensionPoint, "extensionPoint");
		checkNotNull(classAttributeName, "classAttributeName");
		checkNotNull(type, "type");
		final Collection<T> extensions = newArrayList();
		final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionPoint);
		for (final IConfigurationElement element : elements) {
			try {
				if (hasAttributeOf(element, classAttributeName)) {
					extensions.add(instantiate(element, classAttributeName, type));
				}
			} catch (final CoreException e) {
				throw new RuntimeException(String.format("Exception happened when creating element from %s bundle's extension: %s", element
						.getContributor().getName(), extensionPoint), e);
			}
		}
		return extensions;
	}

	/**
	 * Instantiates a default 'class' attribute from the given {@link IConfigurationElement} and returns it as the given type.
	 * 
	 * @param element
	 * @param type
	 * @return
	 * @throws CoreException - if the instantiation fails
	 */
	public static final <T> T instantiate(final IConfigurationElement element, final Class<T> type) throws CoreException {
		return instantiate(element, "class", type);
	}

	/**
	 * Instantiates the given attribute from the given {@link IConfigurationElement} and returns it as the given type.
	 * 
	 * @param element
	 * @param type
	 * @return
	 * @throws CoreException - if the instantiation fails
	 */
	public static final <T> T instantiate(final IConfigurationElement element, final String classAttributeName, final Class<T> type) throws CoreException {
		final Object object = checkNotNull(element, "element").createExecutableExtension(classAttributeName);
		return ClassUtils.checkAndCast(object, type);
	}

	/*Returns with true if the configuration element has the given attribute. Otherwise returns with false.*/
	private static boolean hasAttributeOf(final IConfigurationElement element, final String attributeName) {
		return newHashSet(element.getAttributeNames()).contains(attributeName);
	}
}
