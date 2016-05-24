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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Concepts</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.Concepts#getConcepts <em>Concepts</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcepts()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Concepts extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Concepts</b></em>' containment reference list.
	 * The list contents are of type {@link com.b2international.snowowl.snomed.Concept}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Concepts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Concepts</em>' containment reference list.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getConcepts_Concepts()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<Concept> getConcepts();

} // Concepts