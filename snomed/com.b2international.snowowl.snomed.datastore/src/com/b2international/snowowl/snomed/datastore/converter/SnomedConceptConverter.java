/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.b2international.snowowl.core.domain.IComponent.ID_FUNCTION;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.collect.LongSets;
import com.b2international.commons.functions.LongToStringFunction;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.TreeMultimap;

/**
 * @since 4.5
 */
final class SnomedConceptConverter extends BaseRevisionResourceConverter<SnomedConceptDocument, SnomedConcept, SnomedConcepts> {

	private SnomedReferenceSetConverter referenceSetConverter;
	
	SnomedConceptConverter(final BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}
	
	private SnomedReferenceSetConverter getReferenceSetConverter() {
		if (referenceSetConverter == null) {
			// Not null, even if the expand parameter is missing
			Options expandOptions = expand().getOptions(SnomedConcept.Expand.REFERENCE_SET);
			referenceSetConverter = new SnomedReferenceSetConverter(context(), expandOptions.getOptions("expand"), locales());
		}
		
		return referenceSetConverter;
	}
	
	@Override
	protected SnomedConcepts createCollectionResource(List<SnomedConcept> results, String scrollId, String searchAfter, int limit, int total) {
		return new SnomedConcepts(results, scrollId, searchAfter, limit, total);
	}

	@Override
	protected SnomedConcept toResource(final SnomedConceptDocument input) {
		final SnomedConcept result = new SnomedConcept();
		result.setStorageKey(input.getStorageKey());
		result.setActive(input.isActive());
		result.setDefinitionStatus(toDefinitionStatus(input.isPrimitive()));
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTime()));
		result.setId(input.getId());
		result.setModuleId(input.getModuleId());
		result.setIconId(input.getIconId());
		result.setReleased(input.isReleased());
		result.setSubclassDefinitionStatus(toSubclassDefinitionStatus(input.isExhaustive()));
		result.setScore(input.getScore());
		
		// XXX: Core reference set information will not be included if the expand option is not set
		if (expand().containsKey(SnomedConcept.Expand.REFERENCE_SET) && input.getRefSetStorageKey() != CDOUtils.NO_STORAGE_KEY) {
			result.setReferenceSet(getReferenceSetConverter().toResource(input));
		}
		
		if (input.getAncestors() != null) {
			result.setAncestorIds(input.getAncestors().toArray());
		}
		
		if (input.getParents() != null) { 
			result.setParentIds(input.getParents().toArray());
		}
		
		if (input.getStatedParents() != null) {
			result.setStatedParentIds(input.getStatedParents().toArray());
		}
		
		if (input.getStatedAncestors() != null) {
			result.setStatedAncestorIds(input.getStatedAncestors().toArray());
		}
		
		if (expand().containsKey(SnomedConcept.Expand.PREFERRED_DESCRIPTIONS) || expand().containsKey(SnomedConcept.Expand.PREFERRED_TERM) || expand().containsKey(SnomedConcept.Expand.FULLY_SPECIFIED_NAME)) {
			List<SnomedDescription> preferredDescriptions = input.getPreferredDescriptions().stream().map(description -> {
				SnomedDescription preferredDescription = new SnomedDescription(description.getId());
				preferredDescription.setStorageKey(description.getStorageKey());
				preferredDescription.setConceptId(result.getId());
				preferredDescription.setTerm(description.getTerm());
				preferredDescription.setTypeId(description.getTypeId());
				preferredDescription.setAcceptabilityMap(Maps.toMap(description.getLanguageRefSetIds(), any -> Acceptability.PREFERRED));
				return preferredDescription;
			}).collect(Collectors.toList());
			result.setPreferredDescriptions(new SnomedDescriptions(preferredDescriptions, null, null, preferredDescriptions.size(), preferredDescriptions.size()));
		}
			
		return result;
	}
	
	@Override
	protected void expand(List<SnomedConcept> results) {
		if (expand().isEmpty()) {
			return;
		}
		
		final Set<String> conceptIds = FluentIterable.from(results).transform(ID_FUNCTION).toSet();
		
		expandReferenceSet(results);
		expandInactivationProperties(results, conceptIds);
		new MembersExpander(context(), expand(), locales()).expand(results, conceptIds);
		
		expandPreferredTerm(results, conceptIds);
		expandFullySpecifiedName(results, conceptIds);
		expandDescriptions(results, conceptIds);
		
		expandRelationships(results, conceptIds);
		expandInboundRelationships(results, conceptIds);
		
		expandDescendants(results, conceptIds, SnomedConcept.Expand.DESCENDANTS, false);
		expandDescendants(results, conceptIds, SnomedConcept.Expand.STATED_DESCENDANTS, true);
		expandAncestors(results, conceptIds, SnomedConcept.Expand.ANCESTORS, false);
		expandAncestors(results, conceptIds, SnomedConcept.Expand.STATED_ANCESTORS, true);
		
		
		// XXX make sure we set the preferred descriptions field to null if the client did not explicitly request it
		// it is necessary to expand this field for pt and fsn expand, but do not increase the payload unnecessarily
		if (!expand().containsKey(SnomedConcept.Expand.PREFERRED_DESCRIPTIONS)) {
			results.forEach(concept -> {
				concept.setPreferredDescriptions(null);
			});
		}
	}

	private void expandReferenceSet(List<SnomedConcept> results) {
		if (!expand().containsKey(SnomedConcept.Expand.REFERENCE_SET)) {
			return;
		}
		
		List<SnomedReferenceSet> referenceSets = FluentIterable.from(results)
			.filter(concept -> concept.getReferenceSet() != null)
			.transform(concept -> concept.getReferenceSet())
			.toList();
		
		getReferenceSetConverter().expand(referenceSets);
	}

	private void expandInactivationProperties(List<SnomedConcept> results, Set<String> conceptIds) {
		if (!expand().containsKey(SnomedConcept.Expand.INACTIVATION_PROPERTIES)) {
			return;
		}

		new InactivationExpander<SnomedConcept>(context(), Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR) {
			@Override
			protected void setAssociationTargets(SnomedConcept result, Multimap<AssociationType, String> associationTargets) {
				result.setAssociationTargets(associationTargets);
			}
			
			@Override
			protected void setInactivationIndicator(SnomedConcept result, String valueId) {
				result.setInactivationIndicator(InactivationIndicator.getByConceptId(valueId));				
			}
		}.expand(results, conceptIds);
	}

	private void expandPreferredTerm(List<SnomedConcept> results, final Set<String> conceptIds) {
		if (!expand().containsKey(SnomedConcept.Expand.PREFERRED_TERM)) {
			return;
		}
		
		final List<SnomedDescription> synonyms = newArrayList();
		for (SnomedConcept result : results) {
			for (SnomedDescription description : result.getPreferredDescriptions()) {
				if (!Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
					synonyms.add(description);
				}
			}
		}
		
		final Map<String, SnomedDescription> terms = DescriptionRequestHelper.indexBestPreferredByConceptId(synonyms, locales());
		for (SnomedConcept concept : results) {
			concept.setPt(terms.get(concept.getId()));
		}
	}

	private void expandFullySpecifiedName(List<SnomedConcept> results, final Set<String> conceptIds) {
		if (!expand().containsKey(SnomedConcept.Expand.FULLY_SPECIFIED_NAME)) {
			return;
		}
		
		final Map<String, SnomedDescription> firstFsnByConceptId = newHashMap();
		final List<SnomedDescription> fsns = newArrayList();
		for (SnomedConcept concept : results) {
			for (SnomedDescription description : concept.getPreferredDescriptions()) {
				if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
					fsns.add(description);
					if (!firstFsnByConceptId.containsKey(concept.getId())) {
						firstFsnByConceptId.put(concept.getId(), description);
					}
				}
			}
		}
		
		final Map<String, SnomedDescription> terms = DescriptionRequestHelper.indexBestPreferredByConceptId(fsns, locales());
		
		for (SnomedConcept concept : results) {
			SnomedDescription fsn = terms.get(concept.getId());
			if (fsn == null) {
				fsn = firstFsnByConceptId.get(concept.getId());
			}
			concept.setFsn(fsn);
		}
	}

	private void expandDescriptions(List<SnomedConcept> results, final Set<String> conceptIds) {
		if (!expand().containsKey(SnomedConcept.Expand.DESCRIPTIONS)) {
			return;
		}
		
		final Options expandOptions = expand().get(SnomedConcept.Expand.DESCRIPTIONS, Options.class);
		final SnomedDescriptions descriptions = SnomedRequests
			.prepareSearchDescription()
			.all()
			.setExpand(expandOptions.get("expand", Options.class))
			.filterByType(expandOptions.containsKey("typeId") ? expandOptions.getString("typeId") : null)
			.filterByConceptId(conceptIds)
			.setLocales(locales())
			.build()
			.execute(context());
		
		final ListMultimap<String, SnomedDescription> descriptionsByConceptId = Multimaps.index(descriptions, 
				description -> description.getConceptId());
		
		for (SnomedConcept concept : results) {
			final List<SnomedDescription> conceptDescriptions = descriptionsByConceptId.get(concept.getId());
			concept.setDescriptions(new SnomedDescriptions(conceptDescriptions, null, null, conceptDescriptions.size(), conceptDescriptions.size()));
		}
	}
	
	private void expandRelationships(List<SnomedConcept> results, final Set<String> conceptIds) {
		if (!expand().containsKey(SnomedConcept.Expand.RELATIONSHIPS)) {
			return;
		}
		
		final Options expandOptions = expand().get(SnomedConcept.Expand.RELATIONSHIPS, Options.class);
		final SnomedRelationships relationships = SnomedRequests
				.prepareSearchRelationship()
				.all()
				.filterByActive(expandOptions.containsKey("active") ? expandOptions.getBoolean("active") : null)
				.filterByCharacteristicType(expandOptions.containsKey("characteristicType") ? expandOptions.getString("characteristicType") : null)
				.filterByType(expandOptions.containsKey("typeId") ? expandOptions.getCollection("typeId", String.class) : null)
				.filterByDestination(expandOptions.containsKey("destinationId") ? expandOptions.getCollection("destinationId", String.class) : null)
				.filterBySource(conceptIds)
				.setExpand(expandOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
		
		final ListMultimap<String, SnomedRelationship> relationshipsByConceptId = Multimaps.index(relationships, 
				relationship -> relationship.getSourceId());
		
		for (SnomedConcept concept : results) {
			final List<SnomedRelationship> conceptRelationships = relationshipsByConceptId.get(concept.getId());
			concept.setRelationships(new SnomedRelationships(conceptRelationships, null, null, conceptRelationships.size(), conceptRelationships.size()));
		}
	}
	
	private void expandInboundRelationships(List<SnomedConcept> results, final Set<String> conceptIds) {
		if (!expand().containsKey(SnomedConcept.Expand.INBOUND_RELATIONSHIPS)) {
			return;
		}
		
		final Options expandOptions = expand().get(SnomedConcept.Expand.INBOUND_RELATIONSHIPS, Options.class);
		
		final int relationshipSearchLimit = getLimit(expandOptions);
		
		final SnomedRelationships inboundRelationships = SnomedRequests.prepareSearchRelationship()
			.setLimit(relationshipSearchLimit)
			.filterByType(expandOptions.containsKey("typeId") ? expandOptions.getCollection("typeId", String.class) : null)
			.filterBySource(expandOptions.containsKey("sourceId") ? expandOptions.getCollection("sourceId", String.class) : null)
			.filterByActive(expandOptions.containsKey("active") ? expandOptions.getBoolean("active") : null)
			.filterByCharacteristicType(expandOptions.containsKey("characteristicType") ? expandOptions.getString("characteristicType") : null)
			.filterByDestination(conceptIds)
			.setExpand(expandOptions.get("expand", Options.class))
			.setLocales(locales())
			.build()
			.execute(context());
		
		final ListMultimap<String, SnomedRelationship> inboundRelationshipsByConceptId = Multimaps.index(inboundRelationships,
				inboundRelationship -> inboundRelationship.getDestinationId());
		
		for (SnomedConcept concept : results) {
			final List<SnomedRelationship> conceptInboundRelationships = inboundRelationshipsByConceptId.get(concept.getId());
			concept.setInboundRelationships(new SnomedRelationships(conceptInboundRelationships, null, null, conceptInboundRelationships.size(), conceptInboundRelationships.size()));
		}
	}

	private void expandDescendants(List<SnomedConcept> results, final Set<String> conceptIds, String descendantKey, boolean stated) {
		if (!expand().containsKey(descendantKey)) {
			return;
		}
		
		final Options expandOptions = expand().get(descendantKey, Options.class);
		final boolean direct = checkDirect(expandOptions);
		
		try {
			
			final ExpressionBuilder expression = Expressions.builder();
			expression.filter(active());
			final ExpressionBuilder descendantFilter = Expressions.builder();
			if (stated) {
				descendantFilter.should(statedParents(conceptIds));
				if (!direct) {
					descendantFilter.should(statedAncestors(conceptIds));
				}
			} else {
				descendantFilter.should(parents(conceptIds));
				if (!direct) {
					descendantFilter.should(ancestors(conceptIds));
				}
			}
			expression.filter(descendantFilter.build());
			
			final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
					.where(expression.build())
					.limit(Integer.MAX_VALUE)
					.build();
			
			final RevisionSearcher searcher = context().service(RevisionSearcher.class);
			final Hits<SnomedConceptDocument> hits = searcher.search(query);
			
			if (hits.getTotal() < 1) {
				final SnomedConcepts descendants = new SnomedConcepts(0, 0);
				for (SnomedConcept concept : results) {
					if (stated) {
						concept.setStatedDescendants(descendants);
					} else {
						concept.setDescendants(descendants);
					}
				}
				return;
			}
			
			// in case of only one match and limit zero, use shortcut instead of loading all IDs and components
			// XXX won't work if number of results is greater than one, either use custom ConceptSearch or figure out how to expand descendants effectively
			final int limit = getLimit(expandOptions);
			if (conceptIds.size() == 1 && limit == 0) {
				for (SnomedConcept concept : results) {
					final SnomedConcepts descendants = new SnomedConcepts(0, hits.getTotal());
					if (stated) {
						concept.setStatedDescendants(descendants);
					} else {
						concept.setDescendants(descendants);
					}
				}
				return;
			}
			
			final Multimap<String, String> descendantsByAncestor = TreeMultimap.create();
			for (SnomedConceptDocument hit : hits) {
				final Set<String> parentsAndAncestors = newHashSet();
				if (stated) {
					parentsAndAncestors.addAll(LongSets.toStringSet(hit.getStatedParents()));
					if (!direct) {
						parentsAndAncestors.addAll(LongSets.toStringSet(hit.getStatedAncestors()));
					}
				} else {
					parentsAndAncestors.addAll(LongSets.toStringSet(hit.getParents()));
					if (!direct) {
						parentsAndAncestors.addAll(LongSets.toStringSet(hit.getAncestors()));
					}
				}
				
				parentsAndAncestors.retainAll(conceptIds);
				for (String ancestor : parentsAndAncestors) {
					descendantsByAncestor.put(ancestor, hit.getId());
				}
			}
			
			final int offset = getOffset(expandOptions);
			final Collection<String> componentIds = newHashSet(descendantsByAncestor.values());
			
			if (limit > 0 && !componentIds.isEmpty()) {
				// query descendants again
				final SnomedConcepts descendants = SnomedRequests.prepareSearchConcept()
						.all()
						.filterByActive(true)
						.filterByIds(componentIds)
						.setLocales(locales())
						.setExpand(expandOptions.get("expand", Options.class))
						.build().execute(context());
				
				final Map<String, SnomedConcept> descendantsById = newHashMap();
				descendantsById.putAll(Maps.uniqueIndex(descendants, ID_FUNCTION));
				for (SnomedConcept concept : results) {
					final Collection<String> descendantIds = descendantsByAncestor.get(concept.getId());
					final List<SnomedConcept> currentDescendants = FluentIterable.from(descendantIds).skip(offset).limit(limit).transform(Functions.forMap(descendantsById)).toList();
					final SnomedConcepts descendantConcepts = new SnomedConcepts(currentDescendants, null, null, limit, descendantIds.size());
					if (stated) {
						concept.setStatedDescendants(descendantConcepts);
					} else {
						concept.setDescendants(descendantConcepts);
					}
				}
			} else {
				for (SnomedConcept concept : results) {
					final Collection<String> descendantIds = descendantsByAncestor.get(concept.getId());
					final SnomedConcepts descendants = new SnomedConcepts(limit, descendantIds.size());
					if (stated) {
						concept.setStatedDescendants(descendants);
					} else {
						concept.setDescendants(descendants);
					}
				}
			}
			
		} catch (IOException e) {
			throw SnowowlRuntimeException.wrap(e);
		}
	}

	private boolean checkDirect(final Options expandOptions) {
		if (!expandOptions.containsKey("direct")) {
			throw new BadRequestException("Direct parameter required for descendants expansion");
		}
		return expandOptions.getBoolean("direct");
	}

	private void expandAncestors(List<SnomedConcept> results, Set<String> conceptIds, String key, boolean stated) {
		if (!expand().containsKey(key)) {
			return;
		}

		final Options expandOptions = expand().get(key, Options.class);
		final boolean direct = checkDirect(expandOptions);
		
		final Multimap<String, String> ancestorsByDescendant = TreeMultimap.create();
		
		final LongToStringFunction toString = new LongToStringFunction();
		for (SnomedConcept concept : results) {
			final long[] parentIds = stated ? concept.getStatedParentIds() : concept.getParentIds();
			if (parentIds != null) {
				for (long parent : parentIds) {
					if (IComponent.ROOT_IDL != parent) {
						ancestorsByDescendant.put(concept.getId(), toString.apply(parent));
					}
				}
			}
			if (!direct) {
				final long[] ancestorIds = stated ? concept.getStatedAncestorIds() : concept.getAncestorIds();
				if (ancestorIds != null) {
					for (long ancestor : ancestorIds) {
						if (IComponent.ROOT_IDL != ancestor) {
							ancestorsByDescendant.put(concept.getId(), toString.apply(ancestor));
						}
					}
				}
			}
		}
		
		final int offset = getOffset(expandOptions);
		final int limit = getLimit(expandOptions);

		final Collection<String> componentIds = newHashSet(ancestorsByDescendant.values());
		
		if (limit > 0 && !componentIds.isEmpty()) {
			final SnomedConcepts ancestors = SnomedRequests.prepareSearchConcept()
					.all()
					.filterByActive(true)
					.filterByIds(componentIds)
					.setLocales(locales())
					.setExpand(expandOptions.get("expand", Options.class))
					.build().execute(context());
			
			final Map<String, SnomedConcept> ancestorsById = newHashMap();
			ancestorsById.putAll(Maps.uniqueIndex(ancestors, ID_FUNCTION));
			for (SnomedConcept concept : results) {
				final Collection<String> ancestorIds = ancestorsByDescendant.get(concept.getId());
				final List<SnomedConcept> conceptAncestors = FluentIterable.from(ancestorIds).skip(offset).limit(limit).transform(Functions.forMap(ancestorsById)).toList();
				final SnomedConcepts ancestorConcepts = new SnomedConcepts(conceptAncestors, null, null, limit, ancestorIds.size());
				if (stated) {
					concept.setStatedAncestors(ancestorConcepts);
				} else {
					concept.setAncestors(ancestorConcepts);
				}
			}
		} else {
			for (SnomedConcept concept : results) {
				final Collection<String> ancestorIds = ancestorsByDescendant.get(concept.getId());
				final SnomedConcepts ancestors = new SnomedConcepts(limit, ancestorIds.size());
				if (stated) {
					concept.setStatedAncestors(ancestors);
				} else {
					concept.setAncestors(ancestors);
				}
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
