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

import org.apache.lucene.analysis.Analyzer;

import com.b2international.index.analyzer.ComponentTermAnalyzer;

/**
 * Lucene specific implementations of the supported {@link Analyzers} enum.
 * 
 * @since 5.0
 */
public class AnalyzerImpls {

	public static final Analyzer DEFAULT = new ComponentTermAnalyzer(true, true);
	
	public static final Analyzer NON_BOOKEND = new ComponentTermAnalyzer(false, false);
	
	public static Analyzer getAnalyzer(Analyzers analyzer) {
		switch (analyzer) {
		case DEFAULT: return DEFAULT;
		case NON_BOOKEND: return NON_BOOKEND;
		default: throw new UnsupportedOperationException("Unsupported analyzer: " + analyzer);
		}
	}
	
}
