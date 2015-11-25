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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.server.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 */
final class SnomedConceptConverter extends BaseSnomedComponentConverter<SnomedConceptIndexEntry, ISnomedConcept, SnomedConcepts> {

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
		
		if (expand().contains("pt")) {
			final Map<String, ISnomedDescription> terms = helper.getPreferredTerms(conceptIds, locales());
			for (ISnomedConcept concept : results) {
				((SnomedConcept) concept).setPt(terms.get(concept.getId()));
			}
		}
		if (expand().contains("fsn")) {
			final Map<String, ISnomedDescription> terms = helper.getFullySpecifiedNames(conceptIds, locales());
			for (ISnomedConcept concept : results) {
				((SnomedConcept) concept).setFsn(terms.get(concept.getId()));
			}
		}
		
		Optional<Map<String,String>> descendantParams = getExpandParamsForPrefix("descendants");
		if (descendantParams.isPresent()) {
			Map<String, String> paramMap = descendantParams.get();
			
			if (results.size() > 1) {
				throw new BadRequestException("Unsupported expand parameter descendants");
			}
			
			final ISnomedConcept concept = Iterables.getOnlyElement(results);
			
			if (!paramMap.containsKey("direct")) {
				throw new BadRequestException("Direct parameter required for descendants expansion");
			}
			
			SnomedConceptSearchRequestBuilder req = SnomedRequests.prepareConceptSearch().filterByActive(true);
			if (Boolean.parseBoolean(paramMap.get("direct"))) {
				req.filterByParent(concept.getId());
			} else {
				req.filterByAncestor(concept.getId());
			}
			
			if (paramMap.containsKey("offset")) {
				req.setOffset(Integer.parseInt(paramMap.get("offset")));
			}
			
			if (paramMap.containsKey("limit")) {
				req.setLimit(Integer.parseInt(paramMap.get("limit")));
			}
			
			final SnomedConcepts descendants = req.build().execute(context());
			((SnomedConcept) concept).setDescendants(descendants);
		}
		
		Optional<Map<String,String>> ancestorParams = getExpandParamsForPrefix("ancestors");
		if (ancestorParams.isPresent()) {
			Map<String, String> paramMap = ancestorParams.get();
			
			if (results.size() > 1) {
				throw new BadRequestException("Unsupported expand parameter ancestors");
			}
			
			final ISnomedConcept concept = Iterables.getOnlyElement(results);
			
			if (!paramMap.containsKey("direct")) {
				throw new BadRequestException("Direct parameter required for ancestors expansion");
			}
			
			final boolean direct = Boolean.parseBoolean(paramMap.get("direct"));
			int offset = 0;
			int limit = 50;
			
			if (paramMap.containsKey("offset")) {
				offset = Integer.parseInt(paramMap.get("offset"));
			}
			
			if (paramMap.containsKey("limit")) {
				limit = Integer.parseInt(paramMap.get("limit"));
			}
			
			Query conceptQuery = new ConstantScoreQuery(SnomedMappings.newQuery()
					.concept()
					.active()
					.id(concept.getId())
					.matchAll());
			
			IndexSearcher searcher = context().service(IndexSearcher.class);
			
			try {
				TopDocs search = searcher.search(conceptQuery, 1);
				if (IndexUtils.isEmpty(search)) {
					((SnomedConcept) concept).setAncestors(new SnomedConcepts(offset, limit, 0));	
				}
				
				final Document doc = searcher.doc(search.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().parent().ancestor().build());
				ImmutableSet.Builder<String> collectedIds = ImmutableSet.builder(); 
				collectedIds.addAll(SnomedMappings.parent().getValuesAsString(doc));
					
				if (!direct) {
					collectedIds.addAll(SnomedMappings.ancestor().getValuesAsString(doc));	
				}
				
				SnomedConcepts ancestors = SnomedRequests.prepareConceptSearch()
						.filterByActive(true)
						.setComponentIds(collectedIds.build())
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

	private Optional<Map<String,String>> getExpandParamsForPrefix(final String property) {
		return Iterables.tryFind(expand(), new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.startsWith(property);
			}
		}).transform(new Function<String, Map<String, String>>() {
			@Override
			public Map<String, String> apply(String input) {
				StringTokenizer tok = new StringTokenizer(input, "():,");
				Map<String, String> values = newHashMap(); 
				String expand;
				
				if (tok.hasMoreTokens()) {
					expand = tok.nextToken();
					if (!expand.equals(property)) {
						throw new BadRequestException("Unsupported expand parameter %s", expand);
					}
				} else {
					throw new IllegalArgumentException("Expand parameter did not match prefix " + input);
				}
				
				while (tok.hasMoreTokens()) {
					String key = tok.nextToken();
					
					if (!tok.hasMoreTokens()) {
						throw new BadRequestException("Missing value for key %s in parameter %s.", key, expand);
					} else {
						String value = tok.nextToken();
						values.put(key, value);
					}
				}
				
				return values;
			}
		});
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
