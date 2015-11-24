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
package com.b2international.snowowl.snomed.datastore.server.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.5
 */
public class SnomedConceptConverter extends BaseSnomedComponentConverter<SnomedConceptIndexEntry, ISnomedConcept, SnomedConcepts> {

	SnomedConceptConverter(final BranchContext context, List<String> expand, List<ExtendedLocale> locales, final AbstractSnomedRefSetMembershipLookupService membershipLookupService) {
		super(context, expand, locales, membershipLookupService);
	}
	
	@Override
	protected SnomedConcepts createCollectionResource(List<ISnomedConcept> results, int offset, int limit, int total) {
		return new SnomedConcepts(results, offset, limit, total);
	}

	@Override
	protected SnomedConcept toResource(final SnomedConceptIndexEntry input) {
		final SnomedConcept result = new SnomedConcept();
		result.setActive(input.isActive());
		result.setDefinitionStatus(toDefinitionStatus(input.isPrimitive()));
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTimeAsLong()));
		result.setId(input.getId());
		result.setModuleId(input.getModuleId());
		result.setReleased(input.isReleased());
		result.setSubclassDefinitionStatus(toSubclassDefinitionStatus(input.isExhaustive()));
		result.setInactivationIndicator(toInactivationIndicator(input.getId()));
		result.setAssociationTargets(toAssociationTargets(SnomedTerminologyComponentConstants.CONCEPT, input.getId()));
		return result;
	}
	
	@Override
	protected void expand(List<ISnomedConcept> results) {
		List<Long> conceptIds = Collections.emptyList();
		
		if (!expand().isEmpty()) {
			conceptIds = FluentIterable.from(results).transform(new Function<ISnomedConcept, Long>() {
				@Override
				public Long apply(ISnomedConcept input) {
					return Long.valueOf(input.getId());
				}
			}).toList();
		}
		
		if (expand().contains("pt")) {
			final Collection<ISnomedDescription> terms = SnomedRequests.prepareDescriptionSearch()
				.all()
				.filterByActive(true)
				.filterByConceptId(conceptIds)
				.filterByType("<<" + Concepts.SYNONYM)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales())
				.build()
				.execute(context())
				.getItems();
			final Multimap<String,ISnomedDescription> termsByConceptId = Multimaps.index(terms, new Function<ISnomedDescription, String>() {
				@Override
				public String apply(ISnomedDescription input) {
					return input.getConceptId();
				}
			});
			
			for (ISnomedConcept concept : results) {
				final Collection<ISnomedDescription> conceptTerms = termsByConceptId.get(concept.getId());
				((SnomedConcept) concept).setPt(Iterables.getFirst(conceptTerms, null));
			}
		}
		if (expand().contains("fsn")) {
			final Collection<ISnomedDescription> terms = SnomedRequests.prepareDescriptionSearch()
				.all()
				.filterByActive(true)
				.filterByConceptId(conceptIds)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales())
				.build()
				.execute(context())
				.getItems();
			final Multimap<String,ISnomedDescription> termsByConceptId = Multimaps.index(terms, new Function<ISnomedDescription, String>() {
				@Override
				public String apply(ISnomedDescription input) {
					return input.getConceptId();
				}
			});
			
			for (ISnomedConcept concept : results) {
				final Collection<ISnomedDescription> conceptTerms = termsByConceptId.get(concept.getId());
				((SnomedConcept) concept).setFsn(Iterables.getFirst(conceptTerms, null));
			}
		}
	}
	
	private DefinitionStatus toDefinitionStatus(final boolean primitive) {
		return primitive ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED;
	}

	private SubclassDefinitionStatus toSubclassDefinitionStatus(final boolean exhaustive) {
		return exhaustive ? SubclassDefinitionStatus.DISJOINT_SUBCLASSES : SubclassDefinitionStatus.NON_DISJOINT_SUBCLASSES;
	}

	private InactivationIndicator toInactivationIndicator(final String id) {
		final Collection<SnomedRefSetMemberIndexEntry> members = getRefSetMembershipLookupService().getMembers(
				SnomedTerminologyComponentConstants.CONCEPT,
				ImmutableList.of(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR),
				id);

		for (final SnomedRefSetMemberIndexEntry member : members) {
			if (member.isActive()) {
				return member.getInactivationIndicator();
			}
		}

		return null;
	}

}