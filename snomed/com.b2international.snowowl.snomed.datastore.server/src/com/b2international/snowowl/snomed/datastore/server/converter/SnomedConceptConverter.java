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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.server.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.5
 */
final class SnomedConceptConverter extends BaseSnomedComponentConverter<SnomedConceptIndexEntry, ISnomedConcept, SnomedConcepts> {

	SnomedConceptConverter(final BranchContext context, Options expand, List<ExtendedLocale> locales, final AbstractSnomedRefSetMembershipLookupService membershipLookupService) {
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
		return result;
	}
	
	@Override
	protected void expand(List<ISnomedConcept> results) {
		expandInactivationProperties(results);
		
		if (expand().isEmpty()) {
			return;
		}
		
		final Set<String> conceptIds = FluentIterable.from(results).transform(ID_FUNCTION).toSet();
		final DescriptionRequestHelper helper = new DescriptionRequestHelper() {
			@Override
			protected SnomedDescriptions execute(SnomedDescriptionSearchRequestBuilder req) {
				return req.build().execute(context());
			}
		};
		
		expandPreferredTerm(results, conceptIds, helper);
		expandFullySpecifiedName(results, conceptIds, helper);
		expandDescriptions(results, conceptIds);
		expandRelationships(results, conceptIds);
		expandDescendants(results);
		expandAncestors(results);
	}

	private void expandInactivationProperties(List<ISnomedConcept> results) {
		new InactivationExpander<ISnomedConcept>(context(), Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR) {
			@Override
			protected void setAssociationTargets(ISnomedConcept result,Multimap<AssociationType, String> associationTargets) {
				((SnomedConcept) result).setAssociationTargets(associationTargets);
			}
			
			@Override
			protected void setInactivationIndicator(ISnomedConcept result, String valueId) {
				((SnomedConcept) result).setInactivationIndicator(InactivationIndicator.getByConceptId(valueId));				
			}
		}.expand(results);
	}

	private void expandPreferredTerm(List<ISnomedConcept> results, final Set<String> conceptIds, final DescriptionRequestHelper helper) {
		if (expand().containsKey("pt")) {
			final Map<String, ISnomedDescription> terms = helper.getPreferredTerms(conceptIds, locales());
			for (ISnomedConcept concept : results) {
				((SnomedConcept) concept).setPt(terms.get(concept.getId()));
			}
		}
	}

	private void expandFullySpecifiedName(List<ISnomedConcept> results, final Set<String> conceptIds, final DescriptionRequestHelper helper) {
		if (expand().containsKey("fsn")) {
			final Map<String, ISnomedDescription> terms = helper.getFullySpecifiedNames(conceptIds, locales());
			for (ISnomedConcept concept : results) {
				((SnomedConcept) concept).setFsn(terms.get(concept.getId()));
			}
		}
	}

	private void expandDescriptions(List<ISnomedConcept> results, final Set<String> conceptIds) {
		if (expand().containsKey("descriptions")) {
			final SnomedDescriptions descriptions = SnomedRequests
				.prepareSearchDescription()
				.all()
				.filterByConceptId(StringToLongFunction.copyOf(conceptIds))
				.build()
				.execute(context());
			
			final Multimap<String, ISnomedDescription> descriptionsByConceptId = Multimaps.index(descriptions, new Function<ISnomedDescription, String>() {
				@Override
				public String apply(ISnomedDescription input) {
					return input.getConceptId();
				}
			});
			
			for (ISnomedConcept concept : results) {
				final List<ISnomedDescription> conceptDescriptions = ImmutableList.copyOf(descriptionsByConceptId.get(concept.getId()));
				((SnomedConcept) concept).setDescriptions(new SnomedDescriptions(conceptDescriptions, 0, conceptDescriptions.size(), conceptDescriptions.size()));
			}
		}
	}
	
	private void expandRelationships(List<ISnomedConcept> results, final Set<String> conceptIds) {
		if (expand().containsKey("relationships")) {
			final Options expandOptions = expand().get("relationships", Options.class);
			final SnomedRelationships relationships = SnomedRequests
					.prepareSearchRelationship()
					.all()
					.setExpand(expandOptions.get("expand", Options.class))
					.filterBySource(conceptIds)
					.build()
					.execute(context());
			
			final Multimap<String, ISnomedRelationship> relationshipsByConceptId = Multimaps.index(relationships, new Function<ISnomedRelationship, String>() {
				@Override
				public String apply(ISnomedRelationship input) {
					return input.getSourceId();
				}
			});
			
			for (ISnomedConcept concept : results) {
				final List<ISnomedRelationship> conceptRelationships = ImmutableList.copyOf(relationshipsByConceptId.get(concept.getId()));
				((SnomedConcept) concept).setRelationships(new SnomedRelationships(conceptRelationships, 0, conceptRelationships.size(), conceptRelationships.size()));
			}
		}
	}

	private void expandDescendants(List<ISnomedConcept> results) {
		if (expand().containsKey("descendants")) {
			Options expandOptions = expand().get("descendants", Options.class);
			
			if (results.size() > 1) {
				throw new BadRequestException("Descendants can only be expanded for a single concept");
			}
			
			final ISnomedConcept concept = Iterables.getOnlyElement(results);

			if (!expandOptions.containsKey("direct")) {
				throw new BadRequestException("Direct parameter required for descendants expansion");
			}
			
			SnomedConceptSearchRequestBuilder req = SnomedRequests.prepareSearchConcept()
					.filterByActive(true)
					.setLocales(locales())
					.setExpand(expandOptions.get("expand", Options.class));
			
			if (expandOptions.getBoolean("direct")) {
				req.filterByParent(concept.getId());
			} else {
				req.filterByAncestor(concept.getId());
			}
			
			if (expandOptions.containsKey("offset")) {
				req.setOffset(expandOptions.get("offset", Integer.class));
			}
			
			if (expandOptions.containsKey("limit")) {
				req.setLimit(expandOptions.get("limit", Integer.class));
			}
			
			final SnomedConcepts descendants = req.build().execute(context());
			((SnomedConcept) concept).setDescendants(descendants);
		}
	}

	private void expandAncestors(List<ISnomedConcept> results) {
		if (expand().containsKey("ancestors")) {
			Options expandOptions = expand().get("ancestors", Options.class);
			
			if (results.size() > 1) {
				throw new BadRequestException("Ancestors can only be expanded for a single concept");
			}
			
			final ISnomedConcept concept = Iterables.getOnlyElement(results);
			
			if (!expandOptions.containsKey("direct")) {
				throw new BadRequestException("Direct parameter required for ancestors expansion");
			}
			
			Query conceptQuery = new ConstantScoreQuery(SnomedMappings.newQuery()
					.concept()
					.active()
					.id(concept.getId())
					.matchAll());
			
			IndexSearcher searcher = context().service(IndexSearcher.class);
			
			int offset = 0;
			int limit = 50;

			if (expandOptions.containsKey("offset")) {
				offset = expandOptions.get("offset", Integer.class);
			}
			
			if (expandOptions.containsKey("limit")) {
				limit = expandOptions.get("limit", Integer.class);
			}
			
			try {
				TopDocs search = searcher.search(conceptQuery, 1);
				if (search.scoreDocs.length < 1) {
					((SnomedConcept) concept).setAncestors(new SnomedConcepts(offset, limit, search.totalHits));	
				}
				
				final Document doc = searcher.doc(search.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().parent().ancestor().build());
				ImmutableSet.Builder<String> collectedIds = ImmutableSet.builder(); 
				collectedIds.addAll(SnomedMappings.parent().getValuesAsStringList(doc));

				if (!expandOptions.getBoolean("direct")) {
					collectedIds.addAll(SnomedMappings.ancestor().getValuesAsStringList(doc));	
				}

				SnomedConcepts ancestors = SnomedRequests.prepareSearchConcept()
						.filterByActive(true)
						.setComponentIds(collectedIds.build())
						.setLocales(locales())
						.setExpand(expandOptions.get("expand", Options.class))
						.setOffset(offset)
						.setLimit(limit)
						.build()
						.execute(context());
				
				((SnomedConcept) concept).setAncestors(ancestors);
			} catch (IOException e) {
				throw SnowowlRuntimeException.wrap(e);
			}
		}
	}

	private DefinitionStatus toDefinitionStatus(final boolean primitive) {
		return primitive ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED;
	}

	private SubclassDefinitionStatus toSubclassDefinitionStatus(final boolean exhaustive) {
		return exhaustive ? SubclassDefinitionStatus.DISJOINT_SUBCLASSES : SubclassDefinitionStatus.NON_DISJOINT_SUBCLASSES;
	}
}
