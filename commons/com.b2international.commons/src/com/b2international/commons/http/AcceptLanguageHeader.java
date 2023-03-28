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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * @since 8.10.0
 */
public final class AcceptLanguageHeader {

	public static final String DEFAULT_ACCEPT_LANGUAGE_HEADER = "en-US;q=0.8,en-GB;q=0.6,en;q=0.4";

	/**
	 * Parses an Accept-Language header into a {@link List} of {@link ExtendedLocale} instances.
	 * 
	 * @param acceptLanguageHeader
	 *            - the desired header value to parse into {@link ExtendedLocale} instances
	 * @return a {@link List} of {@link ExtendedLocale} instances parsed from the input parameter or if the input parameter is the wildcard header
	 *         then it falls back to the {@link #DEFAULT_ACCEPT_LANGUAGE_HEADER} as header value.
	 * @see #parseHeader(String, String)
	 */
	public static final List<ExtendedLocale> parseHeader(final String acceptLanguageHeader) {
		return parseHeader(acceptLanguageHeader, DEFAULT_ACCEPT_LANGUAGE_HEADER);
	}

	/**
	 * Parses an Accept-Language header into a {@link List} of {@link ExtendedLocale} instances.
	 * 
	 * @param acceptLanguageHeader
	 *            - the desired header value to parse into {@link ExtendedLocale} instances
	 * @param defaultAcceptLanguageHeader
	 *            - a fall back value when clients use the wildcard '*' accept-language header value
	 * @return a {@link List} of {@link ExtendedLocale} instances parsed from one of the two language header parameters
	 * @see #parseHeader(String)
	 */
	public static final List<ExtendedLocale> parseHeader(final String acceptLanguageHeader, final String defaultAcceptLanguageHeader) {
		// if the incoming value is a wildcard header value, then fall back to the specified defaultAcceptLanguageHeader parameter
		try (StringReader reader = new StringReader("*".equals(acceptLanguageHeader) ? defaultAcceptLanguageHeader : acceptLanguageHeader)) {
			return AcceptHeader.parse(reader, ExtendedLocale::valueOf);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}
