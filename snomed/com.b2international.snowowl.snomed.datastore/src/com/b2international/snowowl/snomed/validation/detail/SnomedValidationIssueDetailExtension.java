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

import java.util.Collection;

import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtension;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * @since 6.4
 */
public class SnomedValidationIssueDetailExtension implements ValidationIssueDetailExtension {

	private static final String FIELD_CONCEPT_STATUS = "conceptStatus";
	
	@Override
	public void prepareQuery(ExpressionBuilder queryBuilder, Options options) {

		if (options.containsKey(SnomedRf2Headers.FIELD_ACTIVE)) {
			final Boolean isActive = options.get(SnomedRf2Headers.FIELD_ACTIVE, Boolean.class);
			queryBuilder.filter(Expressions.match(SnomedRf2Headers.FIELD_ACTIVE, isActive));
		}

		if (options.containsKey(SnomedRf2Headers.FIELD_MODULE_ID)) {
			final Collection<String> moduleIds = options.getCollection(SnomedRf2Headers.FIELD_MODULE_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(SnomedRf2Headers.FIELD_MODULE_ID, moduleIds));
		}
		
		if (options.containsKey(FIELD_CONCEPT_STATUS)) {
			final Boolean isConceptActive = options.get(FIELD_CONCEPT_STATUS, Boolean.class);
			queryBuilder.filter(Expressions.match(FIELD_CONCEPT_STATUS, isConceptActive));
		}

	}
	
	@Override
	public void expandIssueWithDetails(BranchContext context, ValidationIssue issue) {
		RevisionSearcher searcher = context.service(RevisionSearcher.class);

		final ComponentCategory componentCategory = getComponentCategory(issue.getAffectedComponent().getTerminologyComponentId());

		QueryBuilder<String[]> queryBuilder = Query.select(String[].class);
		
		switch (componentCategory) {
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
		
		Query<String[]> query = queryBuilder.where(SnomedDocument.Expressions.id(issue.getAffectedComponent().getComponentId())).limit(10000).build();
		
		for (Hits<String[]> hits : searcher.scroll(query)) {
			for (String[] hit : hits) {
				if (CONCEPT == componentCategory) {
					issue.setDetails(SnomedRf2Headers.FIELD_ACTIVE, hit[1]);
					issue.setDetails(SnomedRf2Headers.FIELD_MODULE_ID, hit[2]);
					issue.setDetails(FIELD_CONCEPT_STATUS, hit[1]);
				} else if (DESCRIPTION == componentCategory || RELATIONSHIP == componentCategory) {
					Iterable<Hits<String[]>> conceptsResult = searcher.scroll(
						Query.select(String[].class)
							.from(SnomedConceptDocument.class)
							.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.ACTIVE)
							.where(SnomedConceptDocument.Expressions.id(hit[3]))
							.limit(10000)
							.build()
					);
					
					for (Hits<String[]> results : conceptsResult) {
						for (String[] conceptFields : results) {
							issue.setDetails(SnomedRf2Headers.FIELD_ACTIVE, hit[1]);
							issue.setDetails(SnomedRf2Headers.FIELD_MODULE_ID, hit[2]);
							issue.setDetails(FIELD_CONCEPT_STATUS, conceptFields[1]);
						}
					}
				}
			}
		}
			
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