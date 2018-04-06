/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.search;

/**
 * Values for the _summary request parameter
 * @since 6.4
 */
public enum SummaryParameter {
	
	TRUE, //	Return only those elements marked as "summary" in the base definition of the resource(s) (see ElementDefinition.isSummary)
	TEXT, //	Return only the "text" element, the 'id' element, the 'meta' element, and only top-level mandatory elements
	DATA	, //Remove the text element
	COUNT, //Search only: just return a count of the matching resources, without returning the actual matches
	FALSE; //Return all parts of the resource(s)
	
	public static SummaryParameter fromRequestParameter(String requestParam) {
		return valueOf(requestParam.toUpperCase());
	}

}
