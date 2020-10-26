/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import static org.junit.Assert.*;

import org.junit.Test;

import com.b2international.commons.options.Options;

/**
 * @since 7.5.1
 */
public class SearchResourceRequestTest {

	public enum OptionKey {
		
		SPECIAL
		
	}
	
	@Test
	public void nullSpecialOptionKey() throws Exception {
		final Options options = Options.builder().build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, null);
		assertTrue(options == actual);
	}
	
	@Test
	public void specialOptionKeyWithNoValue() throws Exception {
		final Options options = Options.builder().build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertTrue(options == actual);
	}
	
	@Test
	public void specialOptionKeyWithNonExpressionValue() throws Exception {
		final Options options = Options.builder()
				.put(OptionKey.SPECIAL, "not an expression")
				.build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertTrue(options == actual);
	}
	
	@Test
	public void specialOptionKeyWithExpressionValueIncorrectSyntaxOnlyLeadingChar() throws Exception {
		final Options options = Options.builder()
				.put(OptionKey.SPECIAL, "@")
				.build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertTrue(options == actual);
	}
	
	@Test
	public void specialOptionKeyWithExpressionValueIncorrectSyntaxNoValueBracket() throws Exception {
		final Options options = Options.builder()
				.put(OptionKey.SPECIAL, "@field")
				.build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertTrue(options == actual);
	}
	
	@Test
	public void specialOptionKeyWithExpressionValueIncorrectSyntaxNoValue() throws Exception {
		final Options options = Options.builder()
				.put(OptionKey.SPECIAL, "@field()")
				.build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertTrue(options == actual);
	}
	
	@Test
	public void specialOptionKeyWithExpressionValue() throws Exception {
		final Options options = Options.builder()
				.put(OptionKey.SPECIAL, "@field(value)")
				.build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertEquals(Options.builder()
				.put("FIELD", "value")
				.build(), actual);
	}
	
	@Test
	public void specialOptionKeyWithExpressionValueOtherOptionKeys() throws Exception {
		final Options options = Options.builder()
				.put(OptionKey.SPECIAL, "@field(value)")
				.put("OTHER", "filter")
				.build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertEquals(Options.builder()
				.put("FIELD", "value")
				.put("OTHER", "filter")
				.build(), actual);
	}
	
	@Test
	public void specialOptionKeyWithExtraParenthesis() throws Exception {
		final Options options = Options.builder()
				.put(OptionKey.SPECIAL, "@field(value (extra))")
				.build();
		final Options actual = SearchResourceRequest.processSpecialOptionKey(options, OptionKey.SPECIAL);
		assertEquals(Options.builder()
				.put("FIELD", "value (extra)")
				.build(), actual);
	}
	
}
