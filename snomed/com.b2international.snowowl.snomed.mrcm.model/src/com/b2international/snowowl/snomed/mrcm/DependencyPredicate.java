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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dependency Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Predicate to check for a group of child predicates.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getChildren <em>Children</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getOperator <em>Operator</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getGroupRule <em>Group Rule</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getDependencyPredicate()
 * @model
 * @generated
 */
public interface DependencyPredicate extends ConceptModelPredicate {
	/**
	 * Returns the value of the '<em><b>Children</b></em>' containment reference list.
	 * The list contents are of type {@link com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Children</em>' containment reference list.
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getDependencyPredicate_Children()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConceptModelPredicate> getChildren();

	/**
	 * Returns the value of the '<em><b>Operator</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.mrcm.DependencyOperator}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operator</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operator</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyOperator
	 * @see #setOperator(DependencyOperator)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getDependencyPredicate_Operator()
	 * @model required="true"
	 * @generated
	 */
	DependencyOperator getOperator();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getOperator <em>Operator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operator</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyOperator
	 * @see #getOperator()
	 * @generated
	 */
	void setOperator(DependencyOperator value);

	/**
	 * Returns the value of the '<em><b>Group Rule</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.mrcm.GroupRule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group Rule</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group Rule</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.GroupRule
	 * @see #setGroupRule(GroupRule)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getDependencyPredicate_GroupRule()
	 * @model required="true"
	 * @generated
	 */
	GroupRule getGroupRule();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getGroupRule <em>Group Rule</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Group Rule</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.GroupRule
	 * @see #getGroupRule()
	 * @generated
	 */
	void setGroupRule(GroupRule value);

} // DependencyPredicate
