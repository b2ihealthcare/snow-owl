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

import com.b2international.snowowl.snomed.api.ISnomedConceptService;

/**
 * Enumerates different search criteria which can be used when looking up concepts.
 * 
 * @see ISnomedConceptService
 */
public enum SearchKind {

	/**
	 * Search by concept label (a preferred term in a system-defined dialect)
	 */
	LABEL,

	/**
	 * Search by ESCG expression
	 */
	ESCG,

	/**
	 * Search by matching concept module
	 */
	MODULE,

	/**
	 * Search by matching namespace part of concept identifier
	 */
	NAMESPACE;
}
