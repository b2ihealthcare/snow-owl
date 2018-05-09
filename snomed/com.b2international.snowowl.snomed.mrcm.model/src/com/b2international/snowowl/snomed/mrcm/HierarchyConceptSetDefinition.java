/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.mrcm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hierarchy Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concept set definition for including a concept and/or its subtypes.                                       
 * <p>Examples:                                                                                              
 * <ul>                                                                                                      
 * <li>= 373873005 -> <i>Pharamaceutical / biologic product</i></li>                                         
 * <li>< 373873005 -> all subtypes of <i>Pharamaceutical / biologic product</i></li>                         
 * <li><< 900000000000454005 -> all descendants of <i>Foundation metadata concepts</i>, including itself</li>
 * </ul>                                                                                                     
 * 
 * @author zstorok
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getConceptId <em>Concept Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getInclusionType <em>Inclusion Type</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getHierarchyConceptSetDefinition()
 * @model
 * @generated
 */
public interface HierarchyConceptSetDefinition extends ConceptSetDefinition {
	/**
	 * Returns the value of the '<em><b>Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Concept Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Concept Id</em>' attribute.
	 * @see #setConceptId(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getHierarchyConceptSetDefinition_ConceptId()
	 * @model required="true"
	 * @generated
	 */
	String getConceptId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getConceptId <em>Concept Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Concept Id</em>' attribute.
	 * @see #getConceptId()
	 * @generated
	 */
	void setConceptId(String value);

	/**
	 * Returns the value of the '<em><b>Inclusion Type</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Inclusion Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inclusion Type</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType
	 * @see #setInclusionType(HierarchyInclusionType)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getHierarchyConceptSetDefinition_InclusionType()
	 * @model required="true"
	 * @generated
	 */
	HierarchyInclusionType getInclusionType();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getInclusionType <em>Inclusion Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inclusion Type</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType
	 * @see #getInclusionType()
	 * @generated
	 */
	void setInclusionType(HierarchyInclusionType value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" required="true"
	 * @generated
	 */
	String getFocusConceptId();

} // HierarchyConceptSetDefinition
