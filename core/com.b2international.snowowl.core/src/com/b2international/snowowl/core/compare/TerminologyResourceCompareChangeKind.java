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
package com.b2international.snowowl.core.compare;

import java.util.Locale;

import com.b2international.commons.StringUtils;

/**
 * Enumerates possible ways a primary component can be changed. 
 * 
 * @since 9.0
 */
public enum TerminologyResourceCompareChangeKind {

	/** An intrinsic property of the component itself changed (eg. status or definition status) */
	DEFINITION_CHANGE,

	/** A relationship or value representing the meaning of the component changed */
	PROPERTY_CHANGE,

	/** A term used to described the component has changed */
	TERM_CHANGE,

	/** A change that can not be categorized into the cases above */
	COMPONENT_CHANGE;

	/**
	 * @return a human-readable name of this change kind (eg. "Definition change"
	 * for the literal {@link #DEFINITION_CHANGE})
	 */
	public String getDisplayName() {
		return StringUtils.splitCamelCaseAndCapitalize(name().toLowerCase(Locale.ENGLISH));
	}
}
