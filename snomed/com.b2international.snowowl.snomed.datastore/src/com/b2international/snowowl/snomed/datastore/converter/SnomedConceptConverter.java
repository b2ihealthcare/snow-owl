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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.b2international.snowowl.core.domain.IComponent.ID_FUNCTION;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
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
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.TreeMultimap;

/**
 * @since 4.5
 */
final class SnomedConceptConverter extends BaseRevisionResourceConverter<SnomedConceptDocument, SnomedConcept, SnomedConcepts> {

	private final SnomedReferenceSetConverter referenceSetConverter;

	SnomedConceptConverter(final BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
		this.referenceSetConverter = new SnomedReferenceSetConverter(context, expand().getOptions(SnomedConcept.EXPAND_REFSET).getOptions("expand"), locales);
	}
	
	@Override
	protected SnomedConcepts createCollectionResource(List<SnomedConcept> results, int offset, int limit, int total) {
		return new SnomedConcepts(results, offset, limit, total);
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
		
		if (expand().containsKey(SnomedConcept.EXPAND_REFSET) && input.getRefSetStorageKey() != CDOUtils.NO_STORAGE_KEY) {
			result.setReferenceSet(referenceSetConverter.toResource(input));
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
			
		return result;
	}
	
	@Override
	protected void expand(List<SnomedConcept> results) {
		final Set<String> conceptIds = FluentIterable.from(results).transform(ID_FUNCTION).toSet();
		expandInactivationProperties(results, conceptIds);
		
		if (expand().isEmpty()) {
			return;
		}
		
		
		new MembersExpander(context(), expand(), locales()).expand(results, conceptIds);
		
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
		if (expand().containsKey("descendants")) {
			final Options expandOptions = expand().get("descendants", Options.class);
			expandDescendants(results, conceptIds, expandOptions, false);
		}
		if (expand().containsKey("statedDescendants")) {
			final Options expandOptions = expand().get("statedDescendants", Options.class);
			expandDescendants(results, conceptIds, expandOptions, true);
		}
		if (expand().containsKey("ancestors")) {
			final Options expandOptions = expand().get("ancestors", Options.class);
			expandAncestors(results, conceptIds, expandOptions, false);
		}
		if (expand().containsKey("statedAncestors")) {
			final Options expandOptions = expand().get("statedAncestors", Options.class);
			expandAncestors(results, conceptIds, expandOptions, true);
		}
		referenceSetConverter.expand(results.stream().filter(c -> c.getReferenceSet() != null).map(SnomedConcept::getReferenceSet).collect(Collectors.toList()));
	}

	private void expandInactivationProperties(List<SnomedConcept> results, Set<String> conceptIds) {
		new InactivationExpander<SnomedConcept>(context(), Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR) {
			@Override
			protected void setAssociationTargets(SnomedConcept result,Multimap<AssociationType, String> associationTargets) {
				((SnomedConcept) result).setAssociationTargets(associationTargets);
			}
			
			@Override
			protected void setInactivationIndicator(SnomedConcept result, String valueId) {
				((SnomedConcept) result).setInactivationIndicator(InactivationIndicator.getByConceptId(valueId));				
			}
		}.expand(results, conceptIds);
	}

	private void expandPreferredTerm(List<SnomedConcept> results, final Set<String> conceptIds, final DescriptionRequestHelper helper) {
		if (expand().containsKey("pt")) {
			final Map<String, SnomedDescription> terms = helper.getPreferredTerms(conceptIds, locales());
			for (SnomedConcept concept : results) {
				((SnomedConcept) concept).setPt(terms.get(concept.getId()));
			}
		}
	}

	private void expandFullySpecifiedName(List<SnomedConcept> results, final Set<String> conceptIds, final DescriptionRequestHelper helper) {
		if (expand().containsKey("fsn")) {
			final Map<String, SnomedDescription> terms = helper.getFullySpecifiedNames(conceptIds, locales());
			for (SnomedConcept concept : results) {
				((SnomedConcept) concept).setFsn(terms.get(concept.getId()));
			}
		}
	}

	private void expandDescriptions(List<SnomedConcept> results, final Set<String> conceptIds) {
		if (expand().containsKey("descriptions")) {
			final Options expandOptions = expand().get("descriptions", Options.class);
			final SnomedDescriptions descriptions = SnomedRequests
				.prepareSearchDescription()
				.all()
				.setExpand(expandOptions.get("expand", Options.class))
				.filterByConceptId(conceptIds)
				.setLocales(locales())
				.build()
				.execute(context());
			
			final Multimap<String, SnomedDescription> descriptionsByConceptId = Multimaps.index(descriptions, new Function<SnomedDescription, String>() {
				@Override
				public String apply(SnomedDescription input) {
					return input.getConceptId();
				}
			});
			
			for (SnomedConcept concept : results) {
				final List<SnomedDescription> conceptDescriptions = ImmutableList.copyOf(descriptionsByConceptId.get(concept.getId()));
				((SnomedConcept) concept).setDescriptions(new SnomedDescriptions(conceptDescriptions, 0, conceptDescriptions.size(), conceptDescriptions.size()));
			}
		}
	}
	
	private void expandRelationships(List<SnomedConcept> results, final Set<String> conceptIds) {
		if (expand().containsKey("relationships")) {
			final Options expandOptions = expand().get("relationships", Options.class);
			final SnomedRelationships relationships = SnomedRequests
					.prepareSearchRelationship()
					.all()
					.filterByActive(expandOptions.containsKey("active") ? expandOptions.getBoolean("active") : null)
					.filterByCharacteristicType(expandOptions.containsKey("characteristicType") ? expandOptions.getString("characteristicType") : null)
					.filterBySource(conceptIds)
					.setExpand(expandOptions.get("expand", Options.class))
					.setLocales(locales())
					.build()
					.execute(context());
			
			final Multimap<String, SnomedRelationship> relationshipsByConceptId = Multimaps.index(relationships, new Function<SnomedRelationship, String>() {
				@Override
				public String apply(SnomedRelationship input) {
					return input.getSourceId();
				}
			});
			
			for (SnomedConcept concept : results) {
				final List<SnomedRelationship> conceptRelationships = ImmutableList.copyOf(relationshipsByConceptId.get(concept.getId()));
				((SnomedConcept) concept).setRelationships(new SnomedRelationships(conceptRelationships, 0, conceptRelationships.size(), conceptRelationships.size()));
			}
		}
	}

	private void expandDescendants(List<SnomedConcept> results, final Set<String> conceptIds, Options expandOptions, boolean stated) {
		final boolean direct = checkDirect(expandOptions);
		
		try {
			
			final ExpressionBuilder expression = Expressions.builder();
			expression.must(active());
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
			expression.must(descendantFilter.build());
			
			final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
					.where(expression.build())
					.limit(Integer.MAX_VALUE)
					.build();
			
			final RevisionSearcher searcher = context().service(RevisionSearcher.class);
			final Hits<SnomedConceptDocument> hits = searcher.search(query);
			
			if (hits.getTotal() < 1) {
				final SnomedConcepts descendants = new SnomedConcepts(0, 0, 0);
				for (SnomedConcept concept : results) {
					if (stated) {
						((SnomedConcept) concept).setStatedDescendants(descendants);
					} else {
						((SnomedConcept) concept).setDescendants(descendants);
					}
				}
				return;
			}
			
			// in case of only one match and limit zero, use shortcut instead of loading all IDs and components
			// XXX won't work if number of results is greater than one, either use custom ConceptSearch or figure out how to expand descendants effectively
			final int limit = getLimit(expandOptions);
			if (conceptIds.size() == 1 && limit == 0) {
				for (SnomedConcept concept : results) {
					final SnomedConcepts descendants = new SnomedConcepts(0, 0, hits.getTotal());
					if (stated) {
						((SnomedConcept) concept).setStatedDescendants(descendants);
					} else {
						((SnomedConcept) concept).setDescendants(descendants);
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
					final SnomedConcepts descendantConcepts = new SnomedConcepts(currentDescendants, 0, limit, descendantIds.size());
					if (stated) {
						((SnomedConcept) concept).setStatedDescendants(descendantConcepts);
					} else {
						((SnomedConcept) concept).setDescendants(descendantConcepts);
					}
				}
			} else {
				for (SnomedConcept concept : results) {
					final Collection<String> descendantIds = descendantsByAncestor.get(concept.getId());
					final SnomedConcepts descendants = new SnomedConcepts(0, limit, descendantIds.size());
					if (stated) {
						((SnomedConcept) concept).setStatedDescendants(descendants);
					} else {
						((SnomedConcept) concept).setDescendants(descendants);
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

	private void expandAncestors(List<SnomedConcept> results, Set<String> conceptIds, Options expandOptions, boolean stated) {
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
				final SnomedConcepts ancestorConcepts = new SnomedConcepts(conceptAncestors, 0, limit, ancestorIds.size());
				if (stated) {
					((SnomedConcept) concept).setStatedAncestors(ancestorConcepts);
				} else {
					((SnomedConcept) concept).setAncestors(ancestorConcepts);
				}
			}
		} else {
			for (SnomedConcept concept : results) {
				final Collection<String> ancestorIds = ancestorsByDescendant.get(concept.getId());
				final SnomedConcepts ancestors = new SnomedConcepts(0, limit, ancestorIds.size());
				if (stated) {
					((SnomedConcept) concept).setStatedAncestors(ancestors);
				} else {
					((SnomedConcept) concept).setAncestors(ancestors);
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
