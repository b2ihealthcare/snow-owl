/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public final class ExtendedLocale implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Pattern EXTENDED_LOCALE_PATTERN = Pattern.compile("([a-zA-Z]{2})(-([a-zA-Z]{2}))?(-x-([1-9][0-9]{5,17}))?");

	@JsonCreator
	public static ExtendedLocale valueOf(String input) {
		final Matcher matcher = EXTENDED_LOCALE_PATTERN.matcher(input);
		if (matcher.matches()) {
			return new ExtendedLocale(matcher.group(1), matcher.group(3), matcher.group(5));
		} else {
			throw new IllegalArgumentException("Couldn't convert input " + input + " to an extended locale.");
		}
	}
	
	private final String language;
	private final String country;
	private final String languageRefSetId;

	public ExtendedLocale(String language, String country, String languageRefSetId) {
		this.language = Strings.nullToEmpty(language).toLowerCase(Locale.ENGLISH);
		this.country = Strings.nullToEmpty(country).toLowerCase(Locale.ENGLISH);
		this.languageRefSetId = Strings.nullToEmpty(languageRefSetId);
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getCountry() {
		return country;
	}
	
	public String getLanguageRefSetId() {
		return languageRefSetId;
	}
	
	public String getLanguageTag() {
		if (country.isEmpty()) {
			return language;
		} else {
			return language + "-" + country;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(language, country, languageRefSetId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true;}
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		
		ExtendedLocale other = (ExtendedLocale) obj;
		return Objects.equals(language, other.language)
				&& Objects.equals(country, other.country) 
				&& Objects.equals(languageRefSetId, other.languageRefSetId);
	}

	@Override
	@JsonValue
	public String toString() {
		if (languageRefSetId.isEmpty()) {
			return getLanguageTag();
		} else {
			return getLanguageTag() + "-x-" + languageRefSetId;
		}
	}
	
	/**
	 * Parses an Accept-Language header into a {@link List} of {@link ExtendedLocale} instances.
	 * 
	 * @param acceptLanguageHeader
	 * @return
	 */
	public static final List<ExtendedLocale> parseLocales(final String acceptLanguageHeader) {
		try {
			return AcceptHeader.parseExtendedLocales(new StringReader(acceptLanguageHeader));
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
}
