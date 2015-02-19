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

import org.eclipse.emf.common.util.EList;

/**
 * Base representation of a regular SNOMED&nbsp;CT reference set.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet#getMembers <em>Members</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRegularRefSet()
 * @model
 * @generated
 */
public interface SnomedRegularRefSet extends SnomedRefSet {
	
	/**
	 * Returns with a collection of reference set members contained by the current regular reference set.
	 * @return a collection of contained reference set members.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRegularRefSet_Members()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<SnomedRefSetMember> getMembers();

} // SnomedRegularRefSet