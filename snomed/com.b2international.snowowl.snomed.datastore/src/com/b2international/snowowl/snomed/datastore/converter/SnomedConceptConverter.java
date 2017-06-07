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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.b2international.snowowl.core.domain.IComponent.ID_FUNCTION;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.commons.functions.LongToStringFunction;
import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
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
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedFieldsToLoadBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

/**
 * @since 4.5
 */
final class SnomedConceptConverter extends BaseSnomedComponentConverter<SnomedConceptIndexEntry, ISnomedConcept, SnomedConcepts> {

	SnomedConceptConverter(final BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
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
		result.setIconId(input.getIconId());
		result.setReleased(input.isReleased());
		result.setSubclassDefinitionStatus(toSubclassDefinitionStatus(input.isExhaustive()));
		result.setScore(input.getScore());
		
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
	protected void expand(List<ISnomedConcept> results) {
		if (expand().isEmpty() || results.isEmpty()) {
			return;
		}
		
		final Set<String> conceptIds = FluentIterable.from(results).transform(ID_FUNCTION).toSet();
		expandInactivationProperties(results, conceptIds);
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
		expandDescendants(results, conceptIds);
		expandAncestors(results, conceptIds);
	}

	private void expandInactivationProperties(List<ISnomedConcept> results, Set<String> conceptIds) {
		new InactivationExpander<ISnomedConcept>(context(), Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR) {
			@Override
			protected void setAssociationTargets(ISnomedConcept result,Multimap<AssociationType, String> associationTargets) {
				((SnomedConcept) result).setAssociationTargets(associationTargets);
			}
			
			@Override
			protected void setInactivationIndicator(ISnomedConcept result, String valueId) {
				((SnomedConcept) result).setInactivationIndicator(InactivationIndicator.getByConceptId(valueId));				
			}
		}.expand(results, conceptIds);
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
			final Options expandOptions = expand().get("descriptions", Options.class);
			final SnomedDescriptions descriptions = SnomedRequests
				.prepareSearchDescription()
				.all()
				.setExpand(expandOptions.get("expand", Options.class))
				.filterByConceptId(StringToLongFunction.copyOf(conceptIds))
				.setLocales(locales())
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
					.filterBySource(conceptIds)
					.setExpand(expandOptions.get("expand", Options.class))
					.setLocales(locales())
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

	private void expandDescendants(List<ISnomedConcept> results, final Set<String> conceptIds) {
		if (expand().containsKey("descendants")) {
			final Options expandOptions = expand().get("descendants", Options.class);
			
			final boolean direct = checkDirect(expandOptions);
			final boolean stated = checkForm(expandOptions);
			final IndexField<Long> parentField = stated ? SnomedMappings.statedParent() : SnomedMappings.parent();
			final IndexField<Long> ancestorField = stated ? SnomedMappings.statedAncestor() : SnomedMappings.ancestor();
			
			try {
				final Query conceptQuery = new ConstantScoreQuery(SnomedMappings.newQuery()
						.concept()
						.active()
						.matchAll());
				final BooleanFilter filter = new BooleanFilter();
				filter.add(parentField.createTermsFilter(StringToLongFunction.copyOf(conceptIds)), Occur.SHOULD);
				if (!direct) {
					filter.add(ancestorField.createTermsFilter(StringToLongFunction.copyOf(conceptIds)), Occur.SHOULD);
				}
				final Query query = new ConstantScoreQuery(new FilteredQuery(conceptQuery, filter));
				
				final IndexSearcher searcher = context().service(IndexSearcher.class);
				
				final TotalHitCountCollector totalCollector = new TotalHitCountCollector();
				searcher.search(query, totalCollector);
				final int totalHits = totalCollector.getTotalHits();
				
				if (totalHits < 1) {
					for (ISnomedConcept concept : results) {
						((SnomedConcept) concept).setDescendants(new SnomedConcepts(0, 0, 0));
					}
					return;
				}
				
				// in case of only one match and limit zero, use shortcut instead of loading all IDs and components
				// XXX won't work if number of results is greater than one, either use custom ConceptSearch or figure out how to expand descendants effectively
				final int limit = getLimit(expandOptions);
				if (conceptIds.size() == 1 && limit == 0) {
					for (ISnomedConcept concept : results) {
						((SnomedConcept) concept).setDescendants(new SnomedConcepts(0, 0, totalHits));
					}
					return;
				}
				
				final TopDocs search = searcher.search(query, totalHits);
				
				final SnomedFieldsToLoadBuilder fieldsToLoadBuilder = SnomedMappings.fieldsToLoad().id();
				if (stated) {
					fieldsToLoadBuilder.statedParent();
				} else {
					fieldsToLoadBuilder.parent();
				}
				if (!direct) {
					if (stated) {
						fieldsToLoadBuilder.statedAncestor();
					} else {
						fieldsToLoadBuilder.ancestor();
					}
				}
				final Set<String> fieldsToLoad = fieldsToLoadBuilder.build();
				
				final Multimap<String, String> descendantsByAncestor = TreeMultimap.create();
				for (int i = 0; i < search.scoreDocs.length; i++) {
					final Document doc = searcher.doc(search.scoreDocs[i].doc, fieldsToLoad);
					final String descendantConceptId = SnomedMappings.id().getValueAsString(doc);
					
					final Set<String> parentsAndAncestors = newHashSet();
					parentsAndAncestors.addAll(parentField.getValuesAsStringSet(doc));
					if (!direct) {
						parentsAndAncestors.addAll(ancestorField.getValuesAsStringSet(doc));
					}
					
					parentsAndAncestors.retainAll(conceptIds);
					for (String ancestor : parentsAndAncestors) {
						descendantsByAncestor.put(ancestor, descendantConceptId);
					}
				}
				
				final int offset = getOffset(expandOptions);
				final Set<String> componentIds = newHashSet(descendantsByAncestor.values());
				
				if (limit > 0 && !componentIds.isEmpty()) {
					final Set<String> alreadySeenDescendantIds = newHashSet(Sets.intersection(componentIds, conceptIds));
					// remove any already known concept definition
					componentIds.removeAll(conceptIds);
					
					if (!alreadySeenDescendantIds.isEmpty()) {
						// make sure we expand already seen descendants
						final List<ISnomedConcept> alreadySeenDescendants = FluentIterable.from(results).filter(new Predicate<ISnomedConcept>() {
							@Override
							public boolean apply(ISnomedConcept input) {
								return alreadySeenDescendantIds.contains(input.getId());
							}
						}).toList();
						new SnomedConceptConverter(context(), expandOptions.get("expand", Options.class), locales()).expand(alreadySeenDescendants);
					}
					
					final SnomedConcepts descendants = SnomedRequests.prepareSearchConcept()
							.all()
							.filterByActive(true)
							.setComponentIds(componentIds)
							.setLocales(locales())
							.setExpand(expandOptions.get("expand", Options.class))
							.build().execute(context());
					
					
					final Map<String, ISnomedConcept> descendantsById = newHashMap();
					descendantsById.putAll(Maps.uniqueIndex(descendants, ID_FUNCTION));
					descendantsById.putAll(Maps.uniqueIndex(results, ID_FUNCTION));
					for (ISnomedConcept concept : results) {
						final Collection<String> descendantIds = descendantsByAncestor.get(concept.getId());
						final List<ISnomedConcept> currentDescendants = FluentIterable.from(descendantIds).skip(offset).limit(limit).transform(Functions.forMap(descendantsById)).toList();
						((SnomedConcept) concept).setDescendants(new SnomedConcepts(currentDescendants, 0, limit, descendantIds.size()));
					}
				} else {
					for (ISnomedConcept concept : results) {
						final Collection<String> descendantIds = descendantsByAncestor.get(concept.getId());
						((SnomedConcept) concept).setDescendants(new SnomedConcepts(0, limit, descendantIds.size()));
					}
				}
				
			} catch (IOException e) {
				throw SnowowlRuntimeException.wrap(e);
			}
		}
	}

	private boolean checkDirect(final Options expandOptions) {
		if (!expandOptions.containsKey("direct")) {
			throw new BadRequestException("Direct parameter required for descendants expansion");
		}
		return expandOptions.getBoolean("direct");
	}

	private boolean checkForm(final Options expandOptions) {
		final String form = expandOptions.getString("form");
		if (!Trees.STATED_FORM.equals(form) && !Trees.INFERRED_FORM.equals(form)) {
			throw new BadRequestException("Form parameter required for descendants expansion, it should be either 'stated' or 'inferred'");
		}
		return Trees.STATED_FORM.equals(form);
	}

	private void expandAncestors(List<ISnomedConcept> results, Set<String> conceptIds) {
		if (expand().containsKey("ancestors")) {
			final Options expandOptions = expand().get("ancestors", Options.class);
			
			final boolean direct = checkDirect(expandOptions);
			final boolean stated = checkForm(expandOptions);
			
			final Multimap<String, String> ancestorsByDescendant = TreeMultimap.create();
			
			final LongToStringFunction toString = new LongToStringFunction();
			for (ISnomedConcept concept : results) {
				final long[] parentIds = stated ? concept.getStatedParentIds() : concept.getParentIds();
				if (parentIds != null) {
					for (long parent : parentIds) {
						ancestorsByDescendant.put(concept.getId(), toString.apply(parent));
					}
				}
				if (!direct) {
					final long[] ancestorIds = stated ? concept.getStatedAncestorIds() : concept.getAncestorIds();
					if (ancestorIds != null) {
						for (long ancestor : ancestorIds) {
							ancestorsByDescendant.put(concept.getId(), toString.apply(ancestor));
						}
					}
				}
			}
			
			final int offset = getOffset(expandOptions);
			final int limit = getLimit(expandOptions);

			final Collection<String> componentIds = newHashSet(ancestorsByDescendant.values());
			
			if (limit > 0 && !componentIds.isEmpty()) {
				// remove any already known concept definitions
				componentIds.removeAll(conceptIds);
				
				final SnomedConcepts ancestors = SnomedRequests.prepareSearchConcept()
						.all()
						.filterByActive(true)
						.setComponentIds(componentIds)
						.setLocales(locales())
						.setExpand(expandOptions.get("expand", Options.class))
						.build().execute(context());
				
				final Map<String, ISnomedConcept> ancestorsById = newHashMap();
				ancestorsById.putAll(Maps.uniqueIndex(ancestors, ID_FUNCTION));
				ancestorsById.putAll(Maps.uniqueIndex(results, ID_FUNCTION));
				for (ISnomedConcept concept : results) {
					final Collection<String> ancestorIds = ancestorsByDescendant.get(concept.getId());
					final List<ISnomedConcept> conceptAncestors = FluentIterable.from(ancestorIds).skip(offset).limit(limit).transform(Functions.forMap(ancestorsById)).toList();
					((SnomedConcept) concept).setAncestors(new SnomedConcepts(conceptAncestors, 0, limit, ancestorIds.size()));
				}
			} else {
				for (ISnomedConcept concept : results) {
					final Collection<String> ancestorIds = ancestorsByDescendant.get(concept.getId());
					((SnomedConcept) concept).setAncestors(new SnomedConcepts(0, limit, ancestorIds.size()));
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
