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
package com.b2international.snowowl.snomed.core.lang;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.google.common.collect.ImmutableList;

/**
 * {@link LanguageSetting} implementation to store an immutable preference list containing a selected and a default language. If the selected and the
 * default are equal, then it will contain a single language preference.
 * 
 * @since 4.6
 */
public final class StaticLanguageSetting implements LanguageSetting {

	private final List<ExtendedLocale> locales;

	public StaticLanguageSetting(String language, String defaultLanguage) {
		if (language.equals(defaultLanguage)) {
			locales = ImmutableList.of(ExtendedLocale.valueOf(language));
		} else {
			locales = ImmutableList.of(ExtendedLocale.valueOf(language), ExtendedLocale.valueOf(defaultLanguage));
		}
	}

	@Override
	public List<ExtendedLocale> getLanguagePreference() {
		return locales;
	}

}
