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
package com.b2international.snowowl.datastore.index;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.TextConstants;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.datastore.index.lucene.ComponentTermAnalyzer;
import com.google.common.base.Splitter;

/**
 * Base class for {@link ILuceneQueryAdapter} implementations.
 * 
 * 
 * @param <D> the search results' type
 * 
 * @deprecated
 */
public abstract class IndexAdapterBase<E extends IIndexEntry> extends QueryDslIndexQueryAdapter<E> {
	
	private static final long serialVersionUID = -4258097603110523835L;

	protected String searchString;
	
	protected IndexAdapterBase(final String searchString) {
		this(searchString, null);
	}
	
	protected IndexAdapterBase(final String searchString, final String[] componentIds) {
		super(searchString, SEARCH_DEFAULT, componentIds);
		this.searchString = searchString;
	}
	
	@Override
	public Query createQuery() {

		try {
			
			// build query in subclass
			final Query query = buildQuery();
			
			// nothing to query?
			if (query == null || isEmptyBooleanQuery(query)) {
				return null;
			}
			
			return query;
			
		} catch (final ParseException e) {
			ApplicationContext.handleException(CoreActivator.getContext().getBundle(), e, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private boolean isEmptyBooleanQuery(final Query query) {
		
		if (!(query instanceof BooleanQuery)) {
			return false;
		}
		
		final BooleanQuery booleanQuery = (BooleanQuery) query;
		return 0 == booleanQuery.getClauses().length;
	}
	
	/**
	 * Returns with the query term of this Lucene specific query adapter.
	 * @return the search string (aka. the query term).
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 * Tokenizes the search string at whitespace and build {@link TermQuery} with Occur.SHOULD.
	 * Method filters out stopwords defined in {@link IndexStopWords}.
	 * (TermQuery means no wildcard or fuzzy matching or AND/OR, just exact match.
	 * 
	 * @param query
	 * @param fieldName
	 * @param searchString
	 * @return
	 */
	protected Query addTermClause(final BooleanQuery query, final String fieldName, final String searchString) {
		
		final List<String> tokens = IndexUtils.split(new ComponentTermAnalyzer(), searchString);
		
		for (final String token : tokens) {
			query.add(new BooleanClause(new TermQuery(new Term(fieldName, token)), Occur.SHOULD));
		}
		
		return query;
	}
	
	/**
	 * Tokenizes the search string at whitespace and build {@link TermQuery} with Occur.MUST.
	 * Method filters out stopwords defined in {@link IndexStopWords}.
	 * (TermQuery means no wildcard or fuzzy matching or AND/OR, just exact match.
	 * 
	 * @param query
	 * @param fieldName
	 * @param searchString
	 * @return
	 */
	protected Query addTermClauseWithAndOperator(final BooleanQuery query, final String fieldName, final String searchString) throws ParseException {
		
		final BooleanQuery wrapper = new BooleanQuery();
		final List<String> tokens = IndexUtils.split(new ComponentTermAnalyzer(), searchString);
		
		for (final String token : tokens) {
			wrapper.add(new BooleanClause(new TermQuery(new Term(fieldName, token)), Occur.MUST));
		}
		
		query.add(wrapper, Occur.SHOULD);
		return query;
	}
	
	/**
	 * Creates parsed query from the passed search string. Clauses are connected with AND operator.
	 * 
	 * @param query
	 * @param fieldName
	 * @param searchString
	 * @return
	 * @throws ParseException
	 */
	protected Query addParsedClause(final BooleanQuery query, final String fieldName, final String searchString) throws ParseException {
		
		final QueryParser parser = new QueryParser(Version.LUCENE_4_9, fieldName, new ComponentTermAnalyzer());
		parser.setDefaultOperator(Operator.AND);
		parser.setAllowLeadingWildcard(true);
		
		query.add(parser.parse(escape(searchString)), Occur.SHOULD);
		return query;
	}
	
	/**
	 * Escapes the specified string, allowing a subset of Lucene's query syntax to pass through (in particular, '?',
	 * '*', '~' and '^' will not be escaped).
	 * 
	 * @see QueryParser#escape(String)
	 * 
	 * @param searchString the string to escape
	 * @return the escaped string
	 */
	private String escape(final String searchString) {
		
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < searchString.length(); i++) {

			final char c = searchString.charAt(i);

			//XXX GOTO: http://xkcd.com/292/
			outerLoop : { switch (c) {
					case '\\':
						//if we already escaped special characters e.g.: '~' to '\~' and we would like to avoid to have '\\~'. 
						if (i != searchString.length() -1) {
							final char charAt = searchString.charAt(i + 1);
							switch (charAt) {
								case '^':
								case '?':
								case '~':
								case '*':
									break outerLoop;
							}
						}
					case '+':
					case '-':
					case '!':
					case '(':
					case ')':
					case ':':
					case '[':
					case ']':
					case '\"':
					case '{':
					case '}':
					case '|':
					case '&':
						sb.append('\\');
						break;
				}
			}
			
			sb.append(c);
			
			switch (c) {
				case '~':
					/*
					 * XXX: append space so that we don't treat possible following numeric characters as fuzzy similarity;
					 * tokenized fields are already separated on ~ characters, so this should not do too much harm. 
					 * See LuceneUtils#DELIMITERS.
					 */
					sb.append(' ');
					break;
			}
		}

		return sb.toString();
	}

	/**
	 * Tokenizes the search string at whitespace and build {@link PrefixQuery} and tokens are connected with Occur.MUST.
	 * The whole query is created with Occur.SHOULD.
	 * (PrefixQuery means implicit '*' at the end of each tokens. No wildcard or fuzzy matching or AND/OR is available.
	 * 
	 * @param query
	 * @param fieldName
	 * @param searchString
	 * @return
	 */
	protected Query addTermPrefixClause(final BooleanQuery query, final String fieldName, final String searchString) {
		
		final BooleanQuery wrapper = new BooleanQuery();
		final Iterable<String> tokens = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER).split(searchString.toLowerCase()); // XXX: we need to keep stopwords in this case!
		
		for (final String token : tokens) {
			wrapper.add(new BooleanClause(new PrefixQuery(new Term(fieldName, token)), Occur.MUST));
		}
		
		query.add(wrapper, Occur.SHOULD);
		
		return query;
	}
	
	/**
	 * Adds a {@link TermQuery} to the specified boolean query with an occurrence of {@link Occur#SHOULD} that will match
	 * on terms that are equal to the given search string. Mostly useful when searching for IDs or boolean flags.
	 * 
	 * @param query the query to add the new clause to
	 * @param fieldName the field to match
	 * @param searchString the text to match
	 * @return {@code query}, may be used to chain clause additions
	 */
	protected Query addExactTermClause(final BooleanQuery query, final String fieldName, final String searchString) {
		query.add(new TermQuery(new Term(fieldName, searchString)), Occur.SHOULD);
		return query;
	}
	
	/**
	 * Adds a {@link TermQuery} to the specified boolean query with an occurrence of {@link Occur#SHOULD} that will match
	 * on terms that are equal to the given search string. Mostly useful when searching for IDs or boolean flags.
	 * 
	 * @param query the query to add the new clause to
	 * @param fieldName the field to match
	 * @param searchString the text to match
	 * @return {@code query}, may be used to chain clause additions
	 */
	protected Query addExactTermClause(final BooleanQuery query, final String fieldName, final BytesRef searchString) {
		query.add(new TermQuery(new Term(fieldName, searchString)), Occur.SHOULD);
		return query;
	}
	
	/**
	 * Adds a {@link TermQuery} to the specified boolean query with an occurrence of {@link Occur#MUST} that will match
	 * on terms that are equal to the given search string. Mostly useful when searching for IDs or boolean flags.
	 * 
	 * @param query the query to add the new clause to
	 * @param fieldName the field to match
	 * @param searchString the text to match
	 * @return {@code query}, may be used to chain clause additions
	 */
	protected Query addExactTermClauseWithAndOperator(final BooleanQuery query, final String fieldName, final String searchString) {
		query.add(new TermQuery(new Term(fieldName, searchString)), Occur.MUST);
		return query;
	}
	
	/**
	 * Adds a {@link TermQuery} to the specified boolean query with an occurrence of {@link Occur#MUST} that will match
	 * on terms that are equal to the given search string. Mostly useful when searching for IDs or boolean flags.
	 * 
	 * @param query the query to add the new clause to
	 * @param fieldName the field to match
	 * @param searchString the text to match
	 * @return {@code query}, may be used to chain clause additions
	 */
	protected Query addExactTermClauseWithAndOperator(final BooleanQuery query, final String fieldName, final BytesRef searchString) {
		query.add(new TermQuery(new Term(fieldName, searchString)), Occur.MUST);
		return query;
	}
	
	/** 
	 * Override to build query from search string.
	 *  
	 * @throws ParseException 
	 */
	protected abstract Query buildQuery() throws ParseException;
	
	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		throw new UnsupportedOperationException("IndexQueryBuilder creation is not supported.");
	}
}