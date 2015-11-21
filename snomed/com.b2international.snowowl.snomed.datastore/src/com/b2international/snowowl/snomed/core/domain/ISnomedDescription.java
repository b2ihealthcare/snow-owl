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
package com.b2international.snowowl.snomed.core.domain;

import java.util.Map;

import com.google.common.collect.Multimap;

/**
 * Represents a SNOMED CT description.
 * <p>
 * Information about the inactivation reason can also be retrieved from this object if applicable.
 */
public interface ISnomedDescription extends SnomedCoreComponent {

	/**
	 * Returns the associated concept's identifier, eg. "{@code 363698007}".
	 * 
	 * @return the concept identifier
	 */
	String getConceptId();

	/**
	 * Returns the description type identifier, eg. "{@code 900000000000013009}".
	 * 
	 * @return the type identifier
	 */
	String getTypeId();

	/**
	 * Returns the description term, eg. "{@code Finding site}".
	 * 
	 * @return the description term
	 */
	String getTerm();

	/**
	 * Returns the description's language code, not including any dialects or variations, eg. "{@code en}".
	 * 
	 * @return the language code of this description
	 */
	String getLanguageCode();

	/**
	 * Returns the description's case significance attribute, indicating whether character case within the term should
	 * be preserved or is interchangeable.
	 * 
	 * @return the case significance of this description
	 */
	CaseSignificance getCaseSignificance();

	/**
	 * Returns language reference set member acceptability values for this description, keyed by language reference set identifier.
	 * 
	 * @return the acceptability map for this description
	 */
	Map<String, Acceptability> getAcceptabilityMap();

	/**
	 * Returns the inactivation indicator (if any) of the description that can be used to identify the reason why the
	 * current description has been deactivated.
	 * 
	 * @return the inactivation reason for this description, or {@code null} if the description is still active, or no
	 * reason has been given
	 */
	DescriptionInactivationIndicator getInactivationIndicator();
	
	/**
	 * Returns association reference set member targets keyed by the association type.
	 * 
	 * @return related association targets, or {@code null} if the description is still active
	 */
	Multimap<AssociationType, String> getAssociationTargets();

}
