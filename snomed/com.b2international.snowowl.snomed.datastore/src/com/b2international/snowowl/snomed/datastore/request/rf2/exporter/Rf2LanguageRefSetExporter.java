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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.IndexReadRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.3
 */
public final class Rf2LanguageRefSetExporter extends Rf2RefSetExporter {

	private final String languageCode;

	public Rf2LanguageRefSetExporter(final Rf2ReleaseType releaseType, 
			final String countrNamespaceElement, 
			final String namespaceFilter, 
			final String transientEffectiveTime, 
			final String archiveEffectiveTime, 
			final boolean includePreReleaseContent,
			final Collection<String> modules,
			final SnomedRefSetType refSetType,
			final Collection<SnomedConcept> referenceSets,
			final String languageCode) {

		super(releaseType, 
				countrNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime, 
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules,
				Rf2RefSetExportLayout.COMBINED, // XXX: language reference sets are always aggregated by language code 
				refSetType, 
				referenceSets);

		this.languageCode = languageCode;
	}

	@Override
	protected String getLanguageElement() {
		return "-" + languageCode;
	}

	@Override
	protected Stream<List<String>> getMappedStream(final SnomedReferenceSetMembers results, 
			final RepositoryContext context,
			final String branch) {

		// Run a sub-query for descriptions that are mentioned in this batch, and have the expected language code
		final Set<String> referenceDescriptionIds = results.stream()
				.map(m -> m.getReferencedComponent().getId())
				.collect(Collectors.toSet());

		final Request<BranchContext, SnomedDescriptions> request = SnomedRequests.prepareSearchDescription()
				.all()
				.filterByIds(referenceDescriptionIds)
				.filterByLanguageCodes(ImmutableSet.of(languageCode))
				.setFields(SnomedDescriptionIndexEntry.Fields.ID)
				.build();

		final Set<String> validDescriptionIds = new IndexReadRequest<>(new BranchRequest<>(branch, new RevisionIndexReadRequest<>(request)))
				.execute(context)
				.stream()
				.map(d -> d.getId())
				.collect(Collectors.toSet());

		return super.getMappedStream(results, context, branch)
				.filter(row -> validDescriptionIds.contains(row.get(5))); // referencedComponentId
	}
}
