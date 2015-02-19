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
package com.b2international.snowowl.core.uri;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.google.common.base.Optional;

/**
 * Provider for {@link ITerminologyComponentUriResolver terminology component URI resolvers}.
 * 
 */
public class TerminologyComponentUriResolverProvider {
	
	private static final String CLASS_ATTRIBUTE_NAME = "class";
	private static final String TERMINOLOGY_ATTRIBUTE_NAME = "terminology";
	private static final String URI_RESOLVER_EXTENSION_ID = "com.b2international.snowowl.core.terminologyComponentUriResolver";

	@SuppressWarnings("rawtypes")
	public List<ITerminologyComponentUriResolver> getUriResolvers() {
		List<ITerminologyComponentUriResolver> resultList = new ArrayList<ITerminologyComponentUriResolver>();
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(URI_RESOLVER_EXTENSION_ID);
		for (final IConfigurationElement element : configurationElements) {
			try {
				resultList.add((ITerminologyComponentUriResolver) element.createExecutableExtension(CLASS_ATTRIBUTE_NAME));
			} catch (CoreException e) {
				throw new RuntimeException("Error when instantiating terminology component URI resolver.", e);
			}
		}
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public Optional<ITerminologyComponentUriResolver<IComponent<?>>> getUriResolverForTerminology(String terminologyId) {
		checkNotNull(terminologyId, "Terminology ID must not be null.");
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(URI_RESOLVER_EXTENSION_ID);
		for (final IConfigurationElement element : configurationElements) {
			try {
				if (terminologyId.equals(element.getAttribute(TERMINOLOGY_ATTRIBUTE_NAME))) {
					return Optional.of((ITerminologyComponentUriResolver<IComponent<?>>) element.createExecutableExtension(CLASS_ATTRIBUTE_NAME));
				}
			} catch (CoreException e) {
				throw new RuntimeException("Error when instantiating terminology component URI resolver.", e);
			}
		}
		return Optional.absent();
	}
	
	@SuppressWarnings("unchecked")
	public Optional<ITerminologyComponentUriResolver<IComponent<?>>> getUriResolver(String uri) {
		checkNotNull(uri, "URI must not be null.");
		if (!UriUtils.isTerminologyUri(uri)) {
			return Optional.absent();
		}
		List<String> uriSegments = UriUtils.getUriSegments(uri);
		String terminologyOid = uriSegments.get(UriUtils.TERMINOLOGY_OID_INDEX);
		String terminologyId = CoreTerminologyBroker.getInstance().getTerminologyIdByOid(terminologyOid);
		
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(URI_RESOLVER_EXTENSION_ID);
		for (final IConfigurationElement element : configurationElements) {
			try {
				if (terminologyId.equals(element.getAttribute(TERMINOLOGY_ATTRIBUTE_NAME))) {
					return Optional.of((ITerminologyComponentUriResolver<IComponent<?>>) element.createExecutableExtension(CLASS_ATTRIBUTE_NAME));
				}
			} catch (CoreException e) {
				throw new RuntimeException("Error when instantiating terminology component URI resolver.", e);
			}
		}
		return Optional.absent();
	}
}