/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

/**
 * @since 8.10.0
 */
public class AcceptLanguageHeaderTest {

	@Test(expected = IllegalArgumentException.class)
	public void unsupportedMixedHeaderValue() throws Exception {
		AcceptLanguageHeader.parseHeader("de,*");
	}
	
	@Test
	public void wildcardWithExplicitDefault() throws Exception {
		List<ExtendedLocale> locales = AcceptLanguageHeader.parseHeader("*", "en");
		assertThat(locales)
			.extracting(ExtendedLocale::getLanguageTag)
			.containsExactly("en");
	}
	
	@Test
	public void wildcardWithImplicitDefault() throws Exception {
		List<ExtendedLocale> locales = AcceptLanguageHeader.parseHeader("*");
		assertThat(locales)
			.extracting(ExtendedLocale::getLanguageTag)
			.containsExactly("en-us", "en-gb", "en");
	}
	
	@Test
	public void acceptLanguageHeaderWithReversePreferenceOrder() throws Exception {
		List<ExtendedLocale> locales = AcceptLanguageHeader.parseHeader("en;q=0.4,en-GB;q=0.6,en-US;q=0.8");
		assertThat(locales)
			.extracting(ExtendedLocale::getLanguageTag)
			.containsExactly("en-us", "en-gb", "en");
	}
	
}
