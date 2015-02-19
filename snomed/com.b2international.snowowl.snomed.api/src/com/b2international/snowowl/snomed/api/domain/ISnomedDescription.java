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

import javax.annotation.Nullable;

/**
 * TODO document
 */
public interface ISnomedDescription extends ISnomedComponent {

	/**
	 * TODO document
	 * @return
	 */
	String getConceptId();
	
	/**
	 * TODO document
	 * @return
	 */
	String getTypeId();

	/**
	 * TODO document
	 * @return
	 */
	String getTerm();

	/**
	 * TODO document
	 * @return
	 */
	String getLanguageCode();

	/**
	 * TODO document
	 * @return
	 */
	CaseSignificance getCaseSignificance();
	
	/**
	 * TODO document
	 * @return
	 */
	Map<String, Acceptability> getAcceptabilityMap();

	/**
	 * Returns with the inactivation indicator (if any) of the description
	 * that can be used to identify the reason why the current description has
	 * been inactivated. 
	 * <p>May return with {@code null} even if the description is 
	 * inactive this means no reason was given for the inactivation.
	 * @return the inactivation reason. Or {@code null} if not available.
	 * @see DescriptionInactivationIndicator
	 */
	@Nullable DescriptionInactivationIndicator getDescriptionInactivationIndicator();
}