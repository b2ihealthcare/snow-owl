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
package com.b2international.snowowl.dsl.escg;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.StringReader;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.b2international.snowowl.dsl.ESCGRewriter;
import com.b2international.snowowl.dsl.ESCGStandaloneSetup;
import com.b2international.snowowl.dsl.parser.antlr.ESCGParser;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;
import com.b2international.snowowl.snomed.dsl.query.ast.RValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Utility service singleton for ESCG expressions.
 * 
 */
public enum EscgUtils implements EscgRewriter {
	
	INSTANCE;
	
	private static final Logger LOGGER = getLogger(EscgUtils.class);
	
	private final LoadingCache<String, RValue> expressionToRvalueCache = CacheBuilder.newBuilder().build(new CacheLoader<String, RValue>() {
		public RValue load(final String expression) throws Exception {
			return parseRewriteNoCache(expression);
		}
	});
	
	@Override
	public RValue parseRewrite(final String expression) {
		try {
			return expressionToRvalueCache.get(expression);
		} catch (final ExecutionException | UncheckedExecutionException e) {
			LOGGER.error("Error while parsing ESCG expression: '" + expression + "'. Ignoring cache and falling back to default parsing process.", e);
			return parseRewriteNoCache(expression);
		}
	}
	
	/*creates the ESCG query parser*/
	private ESCGParser getParser() {
		return ESCGStandaloneSetup.getInstance().getParser();
	}
	
	private RValue parseRewriteNoCache(final String expression) {
		return new ESCGRewriter(getParser()).parse(new StringReader(expression));
	}
}