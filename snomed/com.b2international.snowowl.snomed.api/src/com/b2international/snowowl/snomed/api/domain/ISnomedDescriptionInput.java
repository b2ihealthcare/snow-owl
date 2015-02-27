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
package com.b2international.snowowl.snomed.api.domain;

import java.util.Map;

/**
 * Contains properties required for creating SNOMED CT descriptions.
 */
public interface ISnomedDescriptionInput extends ISnomedComponentInput {

	/**
	 * Returns the new description's associated concept identifier, eg. "{@code 363698007}".
	 * 
	 * @return the concept identifier
	 */
	String getConceptId();

	/**
	 * Returns the new description's type identifier, eg. "{@code 900000000000013009}".
	 * 
	 * @return the type identifier
	 */
	String getTypeId();

	/**
	 * Returns the new description's term, eg. "{@code Finding site}".
	 * 
	 * @return the description term
	 */
	String getTerm();

	/**
	 * Returns the new description's language code, not including any dialects or variations, eg. "{@code en}".
	 * 
	 * @return the language code of this description
	 */
	String getLanguageCode();

	/**
	 * Returns the new description's case significance attribute, indicating whether character case within the term should
	 * be preserved or is interchangeable.
	 * 
	 * @return the case significance of this description
	 */
	CaseSignificance getCaseSignificance();

	/**
	 * Returns the new descriptions's language reference set member acceptability values for this description, keyed by language reference set identifier.
	 * <p>
	 * Language reference set members will be created along with the description in accordance with the map values.
	 * 
	 * @return the acceptability map for this description
	 */
	Map<String, Acceptability> getAcceptability();
}
