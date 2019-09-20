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
package com.b2international.snowowl.snomed.core.rest.browser;

/**
 * Represents a description search result for the IHTSDO SNOMED CT Browser.
 */
public interface ISnomedBrowserDescriptionResult {

	enum TermType {
		FSN, PT
	}

	/** @return the description's term which matched the query */
	String getTerm();

	/** @return the matching description's status */
	boolean isActive();

	/** @return details of the description's container concept */
	ISnomedBrowserDescriptionResultDetails getConcept();
}
