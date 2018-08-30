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
package com.b2international.snowowl.snomed.validation.detail;

import static com.b2international.snowowl.core.terminology.ComponentCategory.CONCEPT;
import static com.b2international.snowowl.core.terminology.ComponentCategory.DESCRIPTION;
import static com.b2international.snowowl.core.terminology.ComponentCategory.RELATIONSHIP;
import static com.b2international.snowowl.core.terminology.ComponentCategory.SET_MEMBER;
import static com.b2international.snowowl.core.validation.issue.ValidationIssue.Fields.AFFECTED_COMPONENT_ID;
import static com.b2international.snowowl.snomed.validation.detail.SnomedValidationIssueDetailExtension.SnomedIssueDetailFilterFields.COMPONENT_MODULE_ID;
import static com.b2international.snowowl.snomed.validation.detail.SnomedValidationIssueDetailExtension.SnomedIssueDetailFilterFields.COMPONENT_STATUS;
import static com.b2international.snowowl.snomed.validation.detail.SnomedValidationIssueDetailExtension.SnomedIssueDetailFilterFields.CONCEPT_STATUS;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtension;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @since 6.4
 */
public class SnomedValidationIssueDetailExtension implements ValidationIssueDetailExtension {

	public final static class SnomedIssueDetailFilterFields {

		private SnomedIssueDetailFilterFields() {};
		
		public static final String COMPONENT_STATUS = "componentStatus";
		public static final String COMPONENT_MODULE_ID = "componentModuleId";
		public static final String CONCEPT_STATUS = "conceptStatus";
		
	}
	
	private static final int SCROLL_SIZE = 50_000;
	
	@Override
	public void prepareQuery(ServiceProvider context, ExpressionBuilder queryBuilder, Options options, Collection<String> branches) {
		if (options.containsKey(COMPONENT_STATUS)) {
			final Boolean isActive = options.get(COMPONENT_STATUS, Boolean.class);
			queryBuilder.filter(Expressions.match(COMPONENT_STATUS, isActive));
		}

		if (options.containsKey(COMPONENT_MODULE_ID)) {
			final Collection<String> moduleIds = options.getCollection(COMPONENT_MODULE_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(COMPONENT_MODULE_ID, moduleIds));
		}
		
		if (options.containsKey(CONCEPT_STATUS)) {
			final Boolean isConceptActive = options.get(CONCEPT_STATUS, Boolean.class);
			queryBuilder.filter(Expressions.match(CONCEPT_STATUS, isConceptActive));
		}
		
		if (options.containsKey(AFFECTED_COMPONENT_ID)) {
			final String searchString = options.getString(AFFECTED_COMPONENT_ID);
			
			final Collection<String> affectedComponentIds = getMatchingComponentIds(Iterables.getOnlyElement(branches), context, queryBuilder, searchString);
			queryBuilder.filter(Expressions.matchAny(AFFECTED_COMPONENT_ID, affectedComponentIds));
		}
	}
	
	private Collection<String> getMatchingComponentIds(String branch, ServiceProvider context, ExpressionBuilder queryBuilder, String searchString) {
		final Multimap<Short, String> affectedComponentCategoryMap = HashMultimap.create();
		
		RevisionIndexReadRequest<Set<String>> request = new RevisionIndexReadRequest<>(branchContext -> {
			final RevisionSearcher searcher = branchContext.service(RevisionSearcher.class);
			final Set<String> matchingAffectedComponentIds = Sets.newHashSet();
			final Set<String> preferredTypes = getPTs(searcher);
			
			//Collect ids of relevant validation issues based on other filters
			final Query<String[]> affectedComponentQuery = Query.select(String[].class)
					.from(ValidationIssue.class)
					.fields(ValidationIssue.Fields.AFFECTED_COMPONENT_ID, ValidationIssue.Fields.AFFECTED_COMPONENT_TYPE)
					.where(queryBuilder.build())
					.limit(SCROLL_SIZE)
					.build();
			
			for (Hits<String[]> hits : context.service(ValidationRepository.class).searcher().scroll(affectedComponentQuery)) {
				for (String[] hit : hits) {
					String affectedCompId = hit[0];
					Short affectedCompType = Short.valueOf(hit[1]);
					affectedComponentCategoryMap.put(affectedCompType, affectedCompId);
				}
			}

			//Use ids to limit results to affected components in component type specific preferred description searches
			for (Short componentType : affectedComponentCategoryMap.keySet()) {
				final ExpressionBuilder expression = Expressions.builder()
						.filter(Expressions.matchAny(SnomedDescriptionIndexEntry.Fields.TYPE_ID, preferredTypes));

				switch((short)componentType) {
				case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
					matchingAffectedComponentIds.addAll(filterAffectedConcepts(searcher, affectedComponentCategoryMap.get(componentType), expression, searchString));
					break;
				case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
					matchingAffectedComponentIds.addAll(filterAffectedDescriptions(searcher, affectedComponentCategoryMap.get(componentType), expression, searchString));
					break;
				case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
					matchingAffectedComponentIds.addAll(filterAffectedRelationships(searcher, affectedComponentCategoryMap.get(componentType), expression, searchString));
					break;
				default:
					break;
				}
			}

			return matchingAffectedComponentIds;
		});
		
		return new RepositoryRequest<>(SnomedDatastoreActivator.REPOSITORY_UUID,
				new BranchRequest<>(branch, request)
			).execute(context);
	}
	
	private Set<String> filterAffectedConcepts(RevisionSearcher searcher, final Collection<String> conceptIds, ExpressionBuilder expression, String searchString) {
		expression.filter(Expressions.matchAny(SnomedDescriptionIndexEntry.Fields.CONCEPT_ID , conceptIds));
		final Query<String[]> descriptionQuery = Query.select(String[].class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.TERM, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
				.where(expression.build())
				.limit(SCROLL_SIZE)
				.build();
		
		Set<String> matchingAffectedComponentIds = Sets.newHashSet();
		for (Hits<String[]> hits : searcher.scroll(descriptionQuery)) {
			for (String[] hit : hits) {
				String affectedCompLabel = hit[0];
				String affectedCompId = hit[1];
				if (affectedCompLabel.toLowerCase().contains(searchString.toLowerCase())) {
					matchingAffectedComponentIds.add(affectedCompId);
				}
			}
		}
		return matchingAffectedComponentIds;
	}
	
	private Set<String> filterAffectedDescriptions(RevisionSearcher searcher, final Collection<String> descriptionIds, ExpressionBuilder expression, String searchString) {
		expression.filter(Expressions.matchAny(SnomedDescriptionIndexEntry.Fields.ID , descriptionIds));
		final Query<String[]> descriptionQuery = Query.select(String[].class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.TERM, SnomedDescriptionIndexEntry.Fields.ID)
				.where(expression.build())
				.limit(SCROLL_SIZE)
				.build();
		Set<String> matchingAffectedComponentIds = Sets.newHashSet();
		
		for (Hits<String[]> hits : searcher.scroll(descriptionQuery)) {
			for (String[] hit : hits) {
				String affectedCompLabel = hit[0];
				String affectedCompId = hit[1];
				if (affectedCompLabel.toLowerCase().contains(searchString.toLowerCase())) {
					matchingAffectedComponentIds.add(affectedCompId);
				}
			}
		}
		return matchingAffectedComponentIds;
	}
	
	private Set<String> filterAffectedRelationships(RevisionSearcher searcher, final Collection<String> relationshipIds, ExpressionBuilder expression, String searchString) {
		Multimap<String, String> relationshipConceptIdMap = relationshipConceptIdMap(searcher, relationshipIds);
		expression.filter(Expressions.matchAny(SnomedDescriptionIndexEntry.Fields.CONCEPT_ID, relationshipConceptIdMap.keySet()));
		final Query<String[]> descriptionQuery = Query.select(String[].class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.TERM, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
				.where(expression.build())
				.limit(SCROLL_SIZE)
				.build();
		
		Set<String> matchingAffectedComponentIds = Sets.newHashSet();
		for (Hits<String[]> hits : searcher.scroll(descriptionQuery)) {
			for (String[] hit : hits) {
				String conceptLabel = hit[0];
				String conceptId = hit[1];
				if (conceptLabel.toLowerCase().contains(searchString.toLowerCase())) {
					matchingAffectedComponentIds.addAll(relationshipConceptIdMap.get(conceptId));
				}
			}
		}
		return matchingAffectedComponentIds;
	}
	
	private Multimap<String, String> relationshipConceptIdMap(RevisionSearcher searcher, Collection<String> relationshipIds) {
		Multimap<String, String> relationshipConceptIdMap = HashMultimap.create();
		Query<String[]> relationshipQuery = Query.select(String[].class)
			.from(SnomedRelationshipIndexEntry.class)
			.fields(SnomedRelationshipIndexEntry.Fields.ID,
					SnomedRelationshipIndexEntry.Fields.SOURCE_ID,
					SnomedRelationshipIndexEntry.Fields.TYPE_ID,
					SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
			.where(Expressions.matchAny(SnomedRelationshipIndexEntry.Fields.ID, relationshipIds))
			.limit(SCROLL_SIZE)
			.build();
		
		//Map each concept to the relationship containing it
		for (Hits<String[]> hits : searcher.scroll(relationshipQuery)) {
			for (String[] hit : hits) {
				relationshipConceptIdMap.put(hit[1], hit[0]);
				relationshipConceptIdMap.put(hit[2], hit[0]);
				relationshipConceptIdMap.put(hit[3], hit[0]);
			}
		}
		return relationshipConceptIdMap;
	}
	
	private Set<String> getPTs(RevisionSearcher searcher) {
		Set<String> ptTypes = Sets.newHashSet(SnomedConstants.Concepts.SYNONYM);
		
		final Query<String> synonymQuery = Query.select(String.class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID)
				.where(Expressions.matchAnyLong(SnomedConceptDocument.Fields.ANCESTORS, StringToLongFunction.copyOf(Collections.singleton(SnomedConstants.Concepts.SYNONYM))))
				.limit(SCROLL_SIZE)
				.build();
		
		for (Hits<String> hits : searcher.scroll(synonymQuery)) {
			ptTypes.addAll(hits.getHits());
		}
	 	return ptTypes;
	}

	@Override
	public void extendIssuesWithDetails(BranchContext context, Collection<ValidationIssue> issues) {
		final Multimap<String, ValidationIssue> issuesByComponentId = Multimaps.index(issues, issue -> issue.getAffectedComponent().getComponentId());

		final RevisionSearcher searcher = context.service(RevisionSearcher.class);

		final Multimap<ComponentCategory, String> issueComponentIdsByComponentCategory = HashMultimap.create();
		issues.stream().forEach(issue -> {
			final ComponentCategory componentCategory = getComponentCategory(issue.getAffectedComponent().getTerminologyComponentId());
			issueComponentIdsByComponentCategory.put(componentCategory, issue.getAffectedComponent().getComponentId());
		});
		
		final Multimap<String, String> issueIdsByConceptIds = HashMultimap.create();
		final Set<String> alreadyFetchedConceptIds  = Sets.newHashSet();
		for (ComponentCategory category : issueComponentIdsByComponentCategory.keySet()) {
			final Query<String[]> query = buildQuery(category, issueComponentIdsByComponentCategory.get(category));
			
			for (Hits<String[]> hits : searcher.scroll(query)) {
				for (String[] hit : hits) {
					String id = hit[0];
					String status = hit[1];
					String moduleId = hit[2];
					issuesByComponentId.get(id).forEach(validationIssue -> {
						validationIssue.setDetails(COMPONENT_STATUS, status);
						validationIssue.setDetails(COMPONENT_MODULE_ID, moduleId);
						if (CONCEPT == category) {
							validationIssue.setDetails(CONCEPT_STATUS, status);
							alreadyFetchedConceptIds.add(id);
						} else if (DESCRIPTION == category || RELATIONSHIP == category) {
							String containerConceptId = hit[3];
							if (!Strings.isNullOrEmpty(containerConceptId) && (!issueIdsByConceptIds.containsKey(containerConceptId) || !alreadyFetchedConceptIds.contains(containerConceptId))) {
								issueIdsByConceptIds.put(containerConceptId, id);
							}
						}
					});
				}
			}
		}
		
		if (!issueIdsByConceptIds.isEmpty()) {
			final Query<String[]> conceptStatusQuery = Query.select(String[].class)
					.from(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.ACTIVE)
					.where(SnomedConceptDocument.Expressions.ids(issueIdsByConceptIds.keySet()))
					.limit(SCROLL_SIZE)
					.build();
			
			for (Hits<String[]> hits : searcher.scroll(conceptStatusQuery)) {
				for (String[] hit : hits) {
					Collection<String> issueIds = issueIdsByConceptIds.get(hit[0]);
					issueIds.stream().forEach(id -> {
						issuesByComponentId.get(id).forEach(validationIssue -> validationIssue.setDetails(CONCEPT_STATUS, hit[1]));
					});
				}
			}
			
		}
	}
	
	private Query<String[]> buildQuery(ComponentCategory category, Collection<String> issueIds) {
		final QueryBuilder<String[]> queryBuilder = Query.select(String[].class);
		switch (category) {
		case CONCEPT:
			queryBuilder.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.ACTIVE, SnomedConceptDocument.Fields.MODULE_ID);
			break;
		case DESCRIPTION:
			queryBuilder.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.ACTIVE, SnomedDescriptionIndexEntry.Fields.MODULE_ID, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID);
			break;
		case RELATIONSHIP:
			queryBuilder.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID, SnomedRelationshipIndexEntry.Fields.ACTIVE, SnomedRelationshipIndexEntry.Fields.MODULE_ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID);
			break;
		default:
			break;
		}
		return queryBuilder.where(SnomedDocument.Expressions.ids(issueIds)).limit(SCROLL_SIZE).build();
	}
	
	private ComponentCategory getComponentCategory(short terminologyComponentId) {
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER == terminologyComponentId) {
			return CONCEPT;
		} else if (SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER == terminologyComponentId) {
			return DESCRIPTION;
		} else if (SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER == terminologyComponentId) {
			return RELATIONSHIP;
		} else if (SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER == terminologyComponentId) {
			return SET_MEMBER;
		} else {
			throw new UnsupportedOperationException("Unsupported terminology component id: " + terminologyComponentId);
		}
	}

	@Override
	public String getToolingId() {
		return SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
	}
}