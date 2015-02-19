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
package com.b2international.snowowl.snomed;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.common.util.EList;

import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;

/**

 * Representation of all SNOMED&nbsp;CT components that can be associated with any kind of concrete domain property. 
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.Annotatable#getConcreteDomainRefSetMembers <em>Concrete Domain Ref Set Members</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getAnnotatable()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Annotatable extends CDOObject {

	/**
	 * A collection of concrete domain elements associated with the current SNOMED&nbsp;CT component.
	 * @return a list of concrete domain elements for the component.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getAnnotatable_ConcreteDomainRefSetMembers()
	 * @model containment="true"
	 * @generated
	 */
	EList<SnomedConcreteDataTypeRefSetMember> getConcreteDomainRefSetMembers();

} // Annotatable