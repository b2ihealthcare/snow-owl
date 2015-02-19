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
package com.b2international.snowowl.snomed.snomedrefset;


/**
 * SNOMED&nbsp;CT query specification type reference set member representation.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember#getQuery <em>Query</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedQueryRefSetMember()
 * @model
 * @generated
 */
public interface SnomedQueryRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns with the ESCG query expression associated with the current query type reference set member.
	 * @return the ESCG query expression of the reference set member.
	 * @see #setQuery(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedQueryRefSetMember_Query()
	 * @model required="true"
	 * @generated
	 */
	String getQuery();

	/**
	 * Counterpart of the {@link #getQuery()}.
	 * @param value the ESCG query expression for the member.
	 * @see #getQuery()
	 * @generated
	 */
	void setQuery(String value);

} // SnomedQueryRefSetMember