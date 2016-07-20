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
package com.b2international.snowowl.datastore.server.index;

import java.util.Collection;
import java.util.Date;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.TokenMgrError;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.b2international.index.lucene.Fields;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.advancedsearch.AbstractSearchCriteria;
import com.b2international.snowowl.datastore.advancedsearch.AdvancedSearchModel;
import com.b2international.snowowl.datastore.advancedsearch.AdvancedSearchModel.MatchType;
import com.b2international.snowowl.datastore.advancedsearch.AdvancedSearchQueryParseException;
import com.b2international.snowowl.datastore.advancedsearch.BooleanSearchCriteria;
import com.b2international.snowowl.datastore.advancedsearch.DateRangeSearchCriteria;
import com.b2international.snowowl.datastore.advancedsearch.LongSearchCriteria;
import com.b2international.snowowl.datastore.advancedsearch.StringSearchCriteria;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;

/**
 * @since 3.0.1
 */
public abstract class AbstractAdvancedSearchQueryBuilder {

	private final AdvancedSearchModel model;

	public AbstractAdvancedSearchQueryBuilder(final AdvancedSearchModel model) {
		this.model = model;
	}

	public Query buildQuery() throws SnowowlServiceException {
		return createBooleanQuery(model.getSearchCriteria(), model.getMatchType(), getTerminologyComponentId());
	}

	protected Query createBooleanQuery(final Collection<AbstractSearchCriteria> searchCriterias, final MatchType matchType, final short terminologyComponentTypeId) throws SnowowlServiceException {

		final BooleanQuery compoundQuery = new BooleanQuery();

		Occur occur = null;

		if (MatchType.ALL.equals(matchType)) {
			occur = Occur.MUST;
		} else if (MatchType.ANY.equals(matchType)) {
			occur = Occur.SHOULD;
		}
		// TODO: handle unexpected match type
		compoundQuery.add(Fields.newQuery().type(terminologyComponentTypeId).matchAll(), Occur.MUST);
		if (!searchCriterias.isEmpty()) {
			final BooleanQuery subQuery = new BooleanQuery();

			for (final AbstractSearchCriteria criteria : searchCriterias) {
				subQuery.add(createQuery(criteria), occur);
			}
			compoundQuery.add(subQuery, Occur.MUST);
		}

		return compoundQuery;
	}

	protected Query createQuery(final AbstractSearchCriteria criteria) throws SnowowlServiceException {

		final String type = criteria.getType();
		final String indexKey = getIndexKey(type);

		if (indexKey != null) {

			if (criteria instanceof StringSearchCriteria) {

				final StringSearchCriteria searchCriteria = (StringSearchCriteria) criteria;
				final String searchString = searchCriteria.getSearchString();

				if (searchCriteria.isExactTerm()) {
					return new TermQuery(new Term(indexKey, searchString));
				} else {
					try {
						final IndexQueryBuilder queryBuilder = new IndexQueryBuilder().matchParsedTerm(indexKey, searchString);
						return queryBuilder.toQuery();
					} catch (IndexException e) {
						Throwable tokenMgrError = e.getCause().getCause();
						if (e.getCause() instanceof ParseException && tokenMgrError instanceof TokenMgrError) {
							String message = tokenMgrError.getMessage();
							throw new AdvancedSearchQueryParseException(message, tokenMgrError, type, searchString);
						} else {
							throw new SnowowlServiceException(e);
						}
					}
				}

			} else if (criteria instanceof BooleanSearchCriteria) {
				
				final BooleanSearchCriteria booleanSearchCriteria = (BooleanSearchCriteria) criteria;
				return Fields.newQuery().field(indexKey, toIntValue(booleanSearchCriteria.getValue())).matchAll();
				
			} else if (criteria instanceof DateRangeSearchCriteria) {

				final DateRangeSearchCriteria dateIntervalSearchCriteria = (DateRangeSearchCriteria) criteria;

				long min = Dates.MIN_DATE_LONG;
				long max = Long.MAX_VALUE;

				if (dateIntervalSearchCriteria.isUnpublished()) {
					min = EffectiveTimes.UNSET_EFFECTIVE_TIME;
					max = EffectiveTimes.UNSET_EFFECTIVE_TIME;
				} else {

					final Date fromDate = dateIntervalSearchCriteria.getFromDate();
					final Date toDate = dateIntervalSearchCriteria.getToDate();

					if (fromDate != null) {
						min = fromDate.getTime();
					}

					if (toDate != null) {
						max = toDate.getTime();
					}
				}
				
				return NumericRangeQuery.newLongRange(indexKey, min, max, true, true);
			
			} else if (criteria instanceof LongSearchCriteria) {
				final LongSearchCriteria longSearchCriteria = (LongSearchCriteria) criteria;
				return Fields.newQuery().field(indexKey, longSearchCriteria.getSearchValue()).matchAll();
			}
		}
		return new BooleanQuery(); // empty clause
	}

	protected AdvancedSearchModel getModel() {
		return model;
	}

	protected abstract String getIndexKey(String searchConstant);

	protected abstract short getTerminologyComponentId();

	// XXX: unfortunately, method implementations don't always use 1 for true
	protected abstract int toIntValue(boolean value);
}
