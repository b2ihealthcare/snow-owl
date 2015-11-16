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

/**
 * Enumerates different search criteria which can be used when looking up concepts.
 */
public enum SearchKind {

	/**
	 * Search by (preferred) fully specified name
	 */
	FSN,
	
	/**
	 * Search by preferred term (synonym and subtypes)
	 */
	PT,

	/**
	 * Search by acceptable synonym and subtypes
	 */
	SYN,
	
	/**
	 * Search by non-synonym description of any acceptability
	 */
	OTHER,

	/**
	 * Search by component status
	 */
	ACTIVE,
	
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
