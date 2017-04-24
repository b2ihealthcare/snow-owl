/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;

import com.google.common.collect.ImmutableMap;

/**
 * Lucene specific implementations of the supported {@link Analyzers} enum.
 * 
 * @since 5.0
 */
public class AnalyzerImpls {

	private static final int POSITION_INCREMENT_GAP = 100;

	public static final Analyzer DEFAULT = new StandardAnalyzer();

	private static Analyzer EXACT;
	private static Analyzer TOKENIZED;
	private static Analyzer STEMMING;
	private static Analyzer SEARCH_STEMMING;
	private static Analyzer CASE_SENSITIVE;
	private static Analyzer CASE_SENSITIVE_ASCII;
	private static Analyzer CASE_SENSITIVE_ASCII_EXACT;
	private static Analyzer CASE_SENSITIVE_PREFIX;
	private static Analyzer PREFIX;

	static {
		try {
			ClasspathResourceLoader loader = new ClasspathResourceLoader(AnalyzerImpls.class);
			EXACT = CustomAnalyzer.builder(loader)
					.withTokenizer("keyword")
					.addTokenFilter("asciifolding")
					.addTokenFilter("lowercase")
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
			TOKENIZED = CustomAnalyzer.builder(loader)
					.withTokenizer("whitespace")
					.addTokenFilter("asciifolding")
					.addTokenFilter("lowercase")
					.addTokenFilter("removeduplicates")
					.addTokenFilter("worddelimiter", newHashMap(ImmutableMap.of(
						"splitOnCaseChange", "0",
						"preserveOriginal", "1",
						"stemEnglishPossessive", "1",
						"types", "word_delimiter.txt"
					)))
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
			STEMMING = CustomAnalyzer.builder(loader)
					.withTokenizer("whitespace")
					.addTokenFilter("asciifolding")
					.addTokenFilter("lowercase")
					.addTokenFilter("snowballporter")
					.addTokenFilter("removeduplicates")
					.addTokenFilter("worddelimiter", newHashMap(ImmutableMap.of(
						"splitOnCaseChange", "0",
						"preserveOriginal", "1",
						"stemEnglishPossessive", "0",
						"types", "word_delimiter.txt"
					)))
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
			
			SEARCH_STEMMING = CustomAnalyzer.builder(loader)
					.withTokenizer("whitespace")
					.addTokenFilter("asciifolding")
					.addTokenFilter("lowercase")
					.addTokenFilter("snowballporter")
					.addTokenFilter("removeduplicates")
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();

			CASE_SENSITIVE = CustomAnalyzer.builder(loader)
					.withTokenizer("whitespace")
					.addTokenFilter("removeduplicates")
					.addTokenFilter("worddelimiter", newHashMap(ImmutableMap.of(
							"splitOnCaseChange", "0",
							"preserveOriginal", "1",
							"stemEnglishPossessive", "1",
							"types", "word_delimiter.txt"
						)))
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
			
			CASE_SENSITIVE_ASCII = CustomAnalyzer.builder(loader)
					.withTokenizer("whitespace")
					.addTokenFilter("asciifolding")
					.addTokenFilter("removeduplicates")
					.addTokenFilter("worddelimiter", newHashMap(ImmutableMap.of(
							"splitOnCaseChange", "0",
							"preserveOriginal", "1",
							"stemEnglishPossessive", "1",
							"types", "word_delimiter.txt"
						)))
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
			
			CASE_SENSITIVE_ASCII_EXACT = CustomAnalyzer.builder(loader)
					.withTokenizer("keyword")
					.addTokenFilter("asciifolding")
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
			
			PREFIX = CustomAnalyzer.builder(loader)
					.withTokenizer("whitespace")
					.addTokenFilter("lowercase")
					.addTokenFilter("asciifolding")
					.addTokenFilter("removeduplicates")
					.addTokenFilter("worddelimiter", newHashMap(ImmutableMap.of(
							"splitOnCaseChange", "0",
							"preserveOriginal", "1",
							"stemEnglishPossessive", "1",
							"types", "word_delimiter.txt"
						)))
					.addTokenFilter("edgengram", newHashMap(ImmutableMap.of(
						"minGramSize", "1",
						"maxGramSize", "20"
					)))
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
			
			CASE_SENSITIVE_PREFIX = CustomAnalyzer.builder(loader)
					.withTokenizer("whitespace")
					.addTokenFilter("asciifolding")
					.addTokenFilter("removeduplicates")
					.addTokenFilter("worddelimiter", newHashMap(ImmutableMap.of(
							"splitOnCaseChange", "0",
							"preserveOriginal", "1",
							"stemEnglishPossessive", "1",
							"types", "word_delimiter.txt"
						)))
					.addTokenFilter("edgengram", newHashMap(ImmutableMap.of(
							"minGramSize", "1",
							"maxGramSize", "20"
						)))
					.withPositionIncrementGap(POSITION_INCREMENT_GAP)
					.build();
		} catch (IOException e) {
			throw new RuntimeException("Failed to initialize analyzers", e);
		}
	}
	
	public static Analyzer getAnalyzer(Analyzers analyzer) {
		switch (analyzer) {
		case DEFAULT: return DEFAULT;
		case EXACT: return EXACT;
		case TOKENIZED: return TOKENIZED;
		case STEMMING: return STEMMING;
		case SEARCH_STEMMING: return SEARCH_STEMMING;
		case CASE_SENSITIVE: return CASE_SENSITIVE;
		case CASE_SENSITIVE_ASCII: return CASE_SENSITIVE_ASCII;
		case CASE_SENSITIVE_ASCII_EXACT: return CASE_SENSITIVE_ASCII_EXACT;
		case CASE_SENSITIVE_PREFIX: return CASE_SENSITIVE_PREFIX;
		case PREFIX: return PREFIX;
		default: throw new UnsupportedOperationException("Unsupported analyzer: " + analyzer);
		}
	}

}
