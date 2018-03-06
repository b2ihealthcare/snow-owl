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
package com.b2international.snowowl.snomed.datastore.request.rf2.exporter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentSearchRequestBuilder;

/**
 * @since 6.3
 */
public abstract class Rf2CoreComponentExporter<B extends SnomedComponentSearchRequestBuilder<B, R>, R extends PageableCollectionResource<C>, C extends SnomedCoreComponent> extends Rf2Exporter<B, R, C> {

	public Rf2CoreComponentExporter(final Rf2ReleaseType releaseType, 
			final String countryNamespaceElement,
			final String namespaceFilter, 
			final String transientEffectiveTime, 
			final String archiveEffectiveTime, 
			final boolean includePreReleaseContent, 
			final Collection<String> modules) {

		super(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime, 
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules);
	}

	@Override
	protected final Path getRelativeDirectory() {
		return Paths.get(releaseType.toString(), "Terminology");
	}

	@Override
	protected final Path getFileName() {
		return Paths.get(String.format("sct2_%s_%s%s_%s_%s.txt",
				getCoreComponentType(),
				releaseType.toString(),
				getLanguageElement(),
				countryNamespaceElement,
				archiveEffectiveTime));
	}

	protected String getLanguageElement() {
		return "";
	}

	protected abstract String getCoreComponentType();

	@Override
	protected final B createSearchRequestBuilder() {
		final B requestBuilder = createComponentSearchRequestBuilder();
		if (namespaceFilter != null) {
			requestBuilder.filterByNamespace(namespaceFilter);
		}
		return requestBuilder;
	}

	protected abstract B createComponentSearchRequestBuilder();
}
