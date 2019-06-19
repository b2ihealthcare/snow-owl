/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public class ExtendedLocale implements Serializable {

	private static final Pattern EXTENDED_LOCALE_PATTERN = Pattern.compile("([a-zA-Z]{2})(-([a-zA-Z]{2}))?(-x-([1-9][0-9]{5,17}))?");

	public static ExtendedLocale valueOf(String input) {
		final Matcher matcher = EXTENDED_LOCALE_PATTERN.matcher(input);
		if (matcher.matches()) {
			return new ExtendedLocale(matcher.group(1), matcher.group(3), matcher.group(5));
		} else {
			throw new IllegalArgumentException("Couldn't convert input " + input + " to an extended locale.");
		}
	}
	
	private final String language;
	private String country;
	private String languageRefSetId;

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
	@JsonValue
	public String toString() {
		if (languageRefSetId.isEmpty()) {
			return getLanguageTag();
		} else {
			return getLanguageTag() + "-x-" + languageRefSetId;
		}
	}
}
