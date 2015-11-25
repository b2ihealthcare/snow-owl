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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;

import com.b2international.snowowl.core.TextConstants;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.lucene.ComponentTermAnalyzer;
import com.google.common.base.Splitter;

/**
 * Builds Lucene-specific {@link Query} instances.
 * @deprecated - if possible use {@link QueryBuilder} or specific subclass instead 
 */
public class IndexQueryBuilder {

	/**
	 * Escapes the specified string, allowing a subset of Lucene's query syntax to pass through (in particular, '?',
	 * '*', '~' and '^' will not be escaped).
	 * 
	 * @see QueryParser#escape(String)
	 * 
	 * @param searchString the string to escape
	 * @return the escaped string
	 */
	private static String escape(final String searchString) {
		
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
					case '/':
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
	private final Analyzer analyzer;
	
	private final BooleanQuery builtQuery;
	
	private boolean done = false;
	
	public IndexQueryBuilder() {
		this(new ComponentTermAnalyzer());
	}
	
	public IndexQueryBuilder(final Analyzer analyzer) {
		this.analyzer = checkNotNull(analyzer, "analyzer");
		this.builtQuery = new BooleanQuery();
	}

	public IndexQueryBuilder boost(final float boost) {
		getLastClause().getQuery().setBoost(boost);
		return this;
	}
	
	private Query createPhrasePrefixQuery(final String fieldName, final String searchString) {
		// TODO: Use MultiPhraseQuery with leading bookend to pin it to the beginning of a term
		final Query wrapper = new MultiPhraseQuery();

//		final Iterable<String> tokens = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER).split(searchString.toLowerCase()); 
//		
//		for (final String token : tokens) {
//			wrapper.add(new Term(fieldName, token));
//		}
//		
		return wrapper;
	}
	
	private Query createPhraseQuery(final String fieldName, final String searchString) {
		return new QueryBuilder(analyzer).createPhraseQuery(fieldName, searchString);
	}
	
	public IndexQueryBuilder finishIf(final boolean condition) {
		if (condition) this.done = true;
		return this;
	}
	
	private BooleanClause getLastClause() {
		return builtQuery.getClauses()[builtQuery.getClauses().length - 1];
	}

	public boolean isEmpty() {
		return (0 == builtQuery.getClauses().length);
	}

	
	public IndexQueryBuilder match(final Query query) {
		builtQuery.add(query, Occur.SHOULD);
		return this;
	}
	
	public IndexQueryBuilder matchIf(final boolean condition, final Query query) {
		if (!done && condition) match(query);
		return this;
	}

	public IndexQueryBuilder match(final IndexQueryBuilder builder) {
		builtQuery.add(builder.toQuery(), Occur.SHOULD);
		return this;
	}
	
	/**
	 * Tokenizes the search string at whitespace and build {@link PrefixQuery} and tokens are connected with Occur.MUST.
	 * The whole query is created with Occur.SHOULD.
	 * (PrefixQuery means implicit '*' at the end of each tokens. No wildcard or fuzzy matching or AND/OR is available.
	 * 
	 * @param fieldName
	 * @param searchString
	 * @return
	 */
	public IndexQueryBuilder matchAllTokenizedTermPrefixes(final String fieldName, final String searchString) {
		
		final BooleanQuery wrapper = new BooleanQuery();
		// XXX: we need to keep stopwords in this case!
		final Iterable<String> tokens = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER).split(searchString.toLowerCase()); 
		
		for (final String token : tokens) {
			wrapper.add(new BooleanClause(new PrefixQuery(new Term(fieldName, token)), Occur.MUST));
		}
		
		builtQuery.add(wrapper, Occur.SHOULD);
		return this;
	}

	public IndexQueryBuilder matchAllTokenizedTermPrefixesIf(final boolean condition, final String fieldName, final String searchString) {
		if (!done && condition) matchAllTokenizedTermPrefixes(fieldName, searchString);
		return this;
	}
	
	public IndexQueryBuilder matchAllTokenizedTermPrefixSequences(final String fieldName, final String searchString) {
		final Query wrapper = createPhrasePrefixQuery(fieldName, searchString);
		builtQuery.add(wrapper, Occur.SHOULD);
		return this;
	}

	/**
	 * Tokenizes the search string at whitespace and build {@link TermQuery} with Occur.MUST.
	 * Method filters out stopwords defined in {@link IndexStopWords}.
	 * (TermQuery means no wildcard or fuzzy matching or AND/OR, just exact match.
	 * 
	 * @param fieldName
	 * @param searchString
	 * @return
	 */
	public IndexQueryBuilder matchAllTokenizedTerms(final String fieldName, final String searchString) {
		final Query wrapper = new QueryBuilder(analyzer).createBooleanQuery(fieldName, searchString, Occur.MUST);
		builtQuery.add(wrapper, Occur.SHOULD);
		return this;
	}
	
	public IndexQueryBuilder matchAllTokenizedTermsIf(final boolean condition, final String fieldName, final String searchString) {
		if (!done && condition) matchAllTokenizedTerms(fieldName, searchString);
		return this;
	}
	
	/**
	 * Tokenizes the search string at whitespace and build {@link TermQuery} with Occur.SHOULD.
	 * Method filters out stopwords defined in {@link IndexStopWords}.
	 * (TermQuery means no wildcard or fuzzy matching or AND/OR, just exact match.
	 * 
	 * @param fieldName
	 * @param searchString
	 * @return this builder
	 */
	public IndexQueryBuilder matchAnyTokenizedTerms(final String fieldName, final String searchString) {
		
		final List<String> tokens = IndexUtils.split(analyzer, searchString);
		
		for (final String token : tokens) {
			builtQuery.add(new TermQuery(new Term(fieldName, token)), Occur.SHOULD);
		}
		
		return this;
	}
	
	public IndexQueryBuilder matchExactTerm(final String fieldName, final BytesRef searchString) {
		builtQuery.add(new TermQuery(new Term(fieldName, searchString)), Occur.SHOULD);
		return this;
	}
	
	/**
	 * Adds a {@link TermQuery} to the specified boolean query with an occurrence of {@link Occur#SHOULD} that will match
	 * on terms that are equal to the given search string. Mostly useful when searching for IDs or boolean flags.
	 * 
	 * @param fieldName the field to match
	 * @param searchString the text to match
	 * @return this builder
	 */
	public IndexQueryBuilder matchExactTerm(final String fieldName, final String searchString) {
		builtQuery.add(new TermQuery(new Term(fieldName, searchString)), Occur.SHOULD);
		return this;
	}

	public IndexQueryBuilder matchExactTermIf(final boolean condition, final String fieldName, final String searchString) {
		if (!done && condition) matchExactTerm(fieldName, searchString);
		return this;
	}

	public IndexQueryBuilder matchExactTermIf(final boolean condition, final String fieldName, final BytesRef searchString) {
		if (!done && condition) matchExactTerm(fieldName, searchString);
		return this;
	}
	
	public IndexQueryBuilder matchExistingTerm(final String fieldName) {
		builtQuery.add(new PrefixQuery(new Term(fieldName)), Occur.SHOULD);
		return this;
	}

	public IndexQueryBuilder matchExistingTermIf(final boolean condition, final String fieldName) {
		if (!done && condition) matchExistingTerm(fieldName);
		return this;
	}
	
	public IndexQueryBuilder matchPrefixTerm(final String fieldName, final String searchString) {
		builtQuery.add(new PrefixQuery(new Term(fieldName, searchString)), Occur.SHOULD);
		return this;
	}

	public IndexQueryBuilder matchPrefixTerm(final String fieldName, final BytesRef searchString) {
		builtQuery.add(new PrefixQuery(new Term(fieldName, searchString)), Occur.SHOULD);
		return this;
	}

	/**
	 * Creates parsed query from the passed search string. Clauses are connected with AND operator.
	 * 
	 * @param fieldName
	 * @param searchString
	 * @return
	 */
	public IndexQueryBuilder matchParsedTerm(final String fieldName, final String searchString) {
		
		final QueryParser parser = new QueryParser(Version.LUCENE_4_9, fieldName, analyzer);
		parser.setDefaultOperator(Operator.AND);
		parser.setAllowLeadingWildcard(true);
		
		try {
			builtQuery.add(parser.parse(escape(searchString)), Occur.SHOULD);
		} catch (final ParseException e) {
			throw new IndexException(e);
		}
		
		return this;
	}
	
	public IndexQueryBuilder matchParsedTermIf(final boolean condition, final String fieldName, final String searchString) {
		if (!done && condition) matchParsedTerm(fieldName, searchString);
		return this;
	}

	public IndexQueryBuilder matchTokenizedTermSequence(final String fieldName, final String searchString) {
		final Query wrappedQuery = createPhraseQuery(fieldName, searchString);
		builtQuery.add(wrappedQuery, Occur.SHOULD);
		return this;
	}

	public IndexQueryBuilder require(final IndexQueryBuilder queryBuilder) {
		builtQuery.add(queryBuilder.toQuery(), Occur.MUST);
		return this;
	}
	
	public IndexQueryBuilder require(final Query query) {
		builtQuery.add(query, Occur.MUST);
		return this;
	}

	public IndexQueryBuilder requireIf(final boolean condition, final Query query) {
		if (!done && condition) require(query);
		return this;
	}

	public IndexQueryBuilder requireNot(final Query query) {
		builtQuery.add(query, Occur.MUST_NOT);
		return this;
	}

	public IndexQueryBuilder requireAllTokenizedTermPrefixes(final String fieldName, final String searchString) {
		
		final BooleanQuery wrapper = new BooleanQuery();
		// XXX: we need to keep stopwords in this case!
		final Iterable<String> tokens = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER).split(searchString.toLowerCase()); 
		
		for (final String token : tokens) {
			wrapper.add(new BooleanClause(new PrefixQuery(new Term(fieldName, token)), Occur.MUST));
		}
		
		builtQuery.add(wrapper, Occur.MUST);
		return this;
	}

	public IndexQueryBuilder requireAllTokenizedTermPrefixSequences(final String fieldName, final String searchString) {
		final Query wrapper = createPhrasePrefixQuery(fieldName, searchString);
		builtQuery.add(wrapper, Occur.MUST);
		return this;
	}
	
	public IndexQueryBuilder requireAllTokenizedTerms(final String fieldName, final String searchString) {
		
		final List<String> tokens = IndexUtils.split(analyzer, searchString);
		
		for (final String token : tokens) {
			builtQuery.add(new BooleanClause(new TermQuery(new Term(fieldName, token)), Occur.MUST));
		}
		
		return this;
	}

	public IndexQueryBuilder requireAnyExactTerms(final String fieldName, final Iterable<String> tokens) {
		
		final BooleanQuery wrapper = new BooleanQuery();
		
		for (final String token : tokens) {
			wrapper.add(new BooleanClause(new TermQuery(new Term(fieldName, token)), Occur.SHOULD));
		}
		
		builtQuery.add(wrapper, Occur.MUST);
		return this;
	}
	
	public IndexQueryBuilder requireAnyExactBytesRefTerms(final String fieldName, final Iterable<BytesRef> tokens) {

		final BooleanQuery wrapper = new BooleanQuery();
		
		for (final BytesRef token : tokens) {
			wrapper.add(new BooleanClause(new TermQuery(new Term(fieldName, token)), Occur.SHOULD));
		}
		
		builtQuery.add(wrapper, Occur.MUST);
		return this;
	}

	public IndexQueryBuilder requireExactTerm(final String fieldName, final BytesRef ref) {
		builtQuery.add(new TermQuery(new Term(fieldName, ref)), Occur.MUST);
		return this;
	}

	public IndexQueryBuilder requireExactTerm(final String fieldName, final String searchString) {
		builtQuery.add(new TermQuery(new Term(fieldName, searchString)), Occur.MUST);
		return this;
	}

	public IndexQueryBuilder requireExactTermIf(final boolean condition, final String fieldName, final BytesRef ref) {
		if (!done && condition) requireExactTerm(fieldName, ref);
		return this;
	}

	public IndexQueryBuilder requireExactTermIf(final boolean condition, final String fieldName, final String searchString) {
		if (!done && condition) requireExactTerm(fieldName, searchString);
		return this;
	}

	public IndexQueryBuilder requireExistingTerm(final String fieldName) {
		builtQuery.add(new PrefixQuery(new Term(fieldName)), Occur.MUST);
		return this;
	}
	
	public IndexQueryBuilder requireExistingTermIf(final boolean condition, final String fieldName) {
		if (!done && condition) requireExistingTerm(fieldName);
		return this;
	}

	public IndexQueryBuilder requireTokenizedTermSequence(final String fieldName, final String searchString) {
		final Query wrappedQuery = createPhraseQuery(fieldName, searchString);
		builtQuery.add(wrappedQuery, Occur.MUST);
		return this;
	}

	public IndexQueryBuilder exclude(final IndexQueryBuilder queryBuilder) {
		builtQuery.add(queryBuilder.toQuery(), Occur.MUST_NOT);
		return this;
	}
	
	public Query toQuery() {
		return isEmpty() ? new MatchAllDocsQuery() : builtQuery;
	}
}