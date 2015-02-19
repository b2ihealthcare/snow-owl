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

import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;

/**
 * Representation of all SNOMED&nbsp;CT components that could be retired. In other word the component could be inactivated.
 * <br>For more details, please refer to {@link Component#isActive()}.
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.Inactivatable#getInactivationIndicatorRefSetMembers <em>Inactivation Indicator Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Inactivatable#getAssociationRefSetMembers <em>Association Ref Set Members</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getInactivatable()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Inactivatable extends CDOObject {

	/**
	 * A collection of SNOMED&nbsp;CT inactivation indicator reference set members associated with the current component.
	 * @return the inactivation indicators for the current instance.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getInactivatable_InactivationIndicatorRefSetMembers()
	 * @model containment="true"
	 * @generated
	 */
	EList<SnomedAttributeValueRefSetMember> getInactivationIndicatorRefSetMembers();

	/**
	 * A collection of SNOMED&nbsp;CT association reference set member associated with the component.
	 * @return the associations for the current component.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getInactivatable_AssociationRefSetMembers()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<SnomedAssociationRefSetMember> getAssociationRefSetMembers();

} // Inactivatable