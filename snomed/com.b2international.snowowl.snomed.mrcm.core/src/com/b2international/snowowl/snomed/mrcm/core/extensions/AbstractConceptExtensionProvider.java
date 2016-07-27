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
package com.b2international.snowowl.snomed.mrcm.core.extensions;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.snomed.Concept;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Represents an abstract extension provider that collects extension keyed by their "id" attributes and collects
 * applicable extensions for {@link Concept}s.
 * 
 * @param <T> The collected extension type
 */
public abstract class AbstractConceptExtensionProvider<T extends IConceptExtension> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractConceptExtensionProvider.class);

	protected final Map<String, T> registeredExtensions = Maps.newHashMap();

	private final AtomicBoolean initialized = new AtomicBoolean();

	private final Class<T> extensionInterface;

	protected AbstractConceptExtensionProvider(final Class<T> extensionInterface) {
		this.extensionInterface = extensionInterface;
	}

	protected Collection<T> getExtensions(final String branch, final String conceptId) {

		initializeElements();
		final Collection<T> elements = Lists.newArrayList();

		for (final T extension : registeredExtensions.values()) {
			if (extension.handlesConcept(branch, conceptId)) {
				elements.add(extension);
			}
		}

		return elements;
	}

	protected void initializeElements() {

		if (!initialized.compareAndSet(false, true)) {
			return;
		}

		final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(getExtensionPointId());

		for (final IConfigurationElement element : configurationElements) {

			try {

				final String extensionId = element.getAttribute(ExtensionConstants.ID_ATTRIBUTE);
				final T extensionInstance = ClassUtils.checkAndCast(element.createExecutableExtension(ExtensionConstants.CLASS_ATTRIBUTE), extensionInterface);
				registeredExtensions.put(extensionId, extensionInstance);

			} catch (final CoreException e) {
				LOGGER.warn(MessageFormat.format("Cannot load validation rules from {0}, ignoring.", element.getContributor().getName()), e);
			}
		}
	}

	protected abstract String getExtensionPointId();
}