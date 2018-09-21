/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es.admin;

import com.b2international.index.Analyzers;
import com.b2international.index.Normalizers;

/**
 * Provides the supported analyzer, normalizer identifier for an {@link Analyzers} or {@link Normalizers} enum literal.
 * 
 * @since 5.10
 * @see analysis.json file for analyzer and normalizer definitions
 */
public final class EsTextAnalysis {

	private EsTextAnalysis() {}
	
	public static String getAnalyzer(Analyzers analyzer) {
		switch (analyzer) {
		case DEFAULT: return "standard";
		case EXACT: return "exact";
		case TOKENIZED: return "tokenized";
		case STEMMING: return "stemming";
		case SEARCH_STEMMING: return "search_stemming";
		case CASE_SENSITIVE: return "case_sensitive";
		case CASE_SENSITIVE_ASCII: return "case_sensitive_ascii";
		case CASE_SENSITIVE_ASCII_EXACT: return "case_sensitive_ascii_exact";
		case CASE_SENSITIVE_PREFIX: return "case_sensitive_prefix";
		case PREFIX: return "prefix";
		default: throw new UnsupportedOperationException("Unsupported analyzer: " + analyzer);
		}
	}
	
	public static String getNormalizer(Normalizers normalizer) {
		switch (normalizer) {
		case LOWER_ASCII: return "lowerascii";
		case NONE: return null;
		default: throw new UnsupportedOperationException("Unsupported normalizer: " + normalizer);
		}
	}
	
}
