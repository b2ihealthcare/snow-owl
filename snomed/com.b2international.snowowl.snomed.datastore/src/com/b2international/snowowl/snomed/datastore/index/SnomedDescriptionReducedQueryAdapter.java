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
package com.b2international.snowowl.snomed.datastore.index;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.google.common.base.Optional;

public class SnomedDescriptionReducedQueryAdapter extends SnomedDescriptionIndexQueryAdapter implements Serializable {

	private static final long serialVersionUID = -1041630591287866356L;
	
	public static final int SEARCH_DESCRIPTION_ID = 1 << 0;
	public static final int SEARCH_DESCRIPTION_TERM = 1 << 1;
	public static final int SEARCH_DESCRIPTION_ACTIVE_ONLY = 1 << 2;
	public static final int SEARCH_DESCRIPTION_CONCEPT_ID = 1 << 3;
	public static final int SEARCH_STORAGE_KEY = 1 << 4;

	private final String descriptionTypeId;
	
	public SnomedDescriptionReducedQueryAdapter(String searchString, int searchFlags) {
		this(searchString, searchFlags, null);
	}
	
	public SnomedDescriptionReducedQueryAdapter(String searchString, int searchFlags, @Nullable String descriptionTypeId) {
		this(searchString, searchFlags, descriptionTypeId, null);
	}
	
	public SnomedDescriptionReducedQueryAdapter(String searchString, int searchFlags, @Nullable String descriptionTypeId, @Nullable String[] componentIds) {
		super(searchString, checkFlags(searchFlags, SEARCH_DESCRIPTION_ID, SEARCH_DESCRIPTION_TERM, SEARCH_DESCRIPTION_ACTIVE_ONLY, SEARCH_DESCRIPTION_CONCEPT_ID), componentIds);
		this.descriptionTypeId = descriptionTypeId;
	}
	
	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		if (StringUtils.isEmpty(searchString)) {
			return getDescriptionTypeQuery(super.createIndexQueryBuilder());
		} else {
			if (anyFlagSet(SEARCH_DESCRIPTION_ID | SEARCH_DESCRIPTION_CONCEPT_ID)) {
				Optional<Long> parsedSearchStringOptional = IndexUtils.parseLong(searchString);
				if (parsedSearchStringOptional.isPresent()) {
					return createIndexQueryBuilderWithIdTerms(parsedSearchStringOptional.get());
				} else if (anyFlagSet(SEARCH_DESCRIPTION_TERM)) {
					return createIndexQueryBuilderWithoutIdTerms();
				} else {
					// XXX: Search string could not be parsed into a long, so we query for an invalid ID instead. See SnomedRefSetIndexQueryAdapter.
					return new IndexQueryBuilder().require(SnomedMappings.newQuery().id(-1L).matchAll());
				}
			} else {
				return createIndexQueryBuilderWithoutIdTerms();
			}
		}
	}

	private IndexQueryBuilder createIndexQueryBuilderWithIdTerms(Long id) {
		return getDescriptionTypeQuery(super.createIndexQueryBuilder())
				.require(new IndexQueryBuilder()
				.matchIf(anyFlagSet(SEARCH_DESCRIPTION_ID), SnomedMappings.newQuery().id(id).matchAll())
				.matchParsedTermIf(anyFlagSet(SEARCH_DESCRIPTION_TERM), SnomedMappings.descriptionTerm().fieldName(), searchString)
				.matchIf(anyFlagSet(SEARCH_DESCRIPTION_CONCEPT_ID), SnomedMappings.newQuery().descriptionConcept(id).matchAll()));
	}

	private IndexQueryBuilder getDescriptionTypeQuery(IndexQueryBuilder builder) {
		if (anyFlagSet(SEARCH_DESCRIPTION_ACTIVE_ONLY) || !StringUtils.isEmpty(descriptionTypeId) || StringUtils.isEmpty(searchString)) {
			final SnomedQueryBuilder query = SnomedMappings.newQuery();
			if (anyFlagSet(SEARCH_DESCRIPTION_ACTIVE_ONLY)) {
				query.active();
			}
			if (!StringUtils.isEmpty(descriptionTypeId)) {
				query.descriptionType(descriptionTypeId);
			}
			if (StringUtils.isEmpty(searchString)) {
				query.and(SnomedMappings.id().toExistsQuery());
			}
			builder.require(query.matchAll());
		}
		return builder;
	}

	private IndexQueryBuilder createIndexQueryBuilderWithoutIdTerms() {
		return requireType(super.createIndexQueryBuilder())
				.requireIf(anyFlagSet(SEARCH_DESCRIPTION_ACTIVE_ONLY), SnomedMappings.newQuery().active().matchAll())
				.requireIf(StringUtils.isEmpty(searchString), SnomedMappings.id().toExistsQuery())
				.finishIf(StringUtils.isEmpty(searchString))
				.require(new IndexQueryBuilder().matchParsedTerm(SnomedMappings.descriptionTerm().fieldName(), searchString));
	}	
	
	private IndexQueryBuilder requireType(IndexQueryBuilder builder) {
		if (!StringUtils.isEmpty(descriptionTypeId)) {
			builder.require(SnomedMappings.newQuery().descriptionType(descriptionTypeId).matchAll());
		}
		return builder;
	}
	
}