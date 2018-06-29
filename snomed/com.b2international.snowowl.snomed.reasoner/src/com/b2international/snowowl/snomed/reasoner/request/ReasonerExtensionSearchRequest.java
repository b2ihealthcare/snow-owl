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
package com.b2international.snowowl.snomed.reasoner.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerExtension;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerExtensions;

/**
 * @since 7.0
 */
final class ReasonerExtensionSearchRequest implements Request<ServiceProvider, ReasonerExtensions> {

	private static final String EXTENSION_POINT_ID = "org.protege.editor.owl.inference_reasonerfactory";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String VALUE_ATTRIBUTE = "value";

	@Override
	public ReasonerExtensions execute(final ServiceProvider context) {

		final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		final IExtension[] extensions = extensionPoint.getExtensions();
		final List<ReasonerExtension> reasonerExtensions = newArrayList();

		for (final IExtension extension : extensions) {

			final IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			final Optional<String> name = Arrays.asList(configurationElements)
					.stream()
					.filter(e -> NAME_ATTRIBUTE.equals(e.getName()))
					.findFirst()
					.map(e -> e.getAttribute(VALUE_ATTRIBUTE));

			final String extensionId = extension.getUniqueIdentifier();
			final Bundle contributorBundle = Platform.getBundle(extension.getContributor().getName());
			final String version = contributorBundle.getVersion().toString();

			final ReasonerExtension reasonerExtension = new ReasonerExtension();
			reasonerExtension.setName(name.orElse(""));
			reasonerExtension.setExtensionId(extensionId);
			reasonerExtension.setVersion(version);

			reasonerExtensions.add(reasonerExtension);
		}

		return new ReasonerExtensions(reasonerExtensions, null, null, reasonerExtensions.size(), reasonerExtensions.size());
	}
}
