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
 * A representation of the model object '<em><b>Attribute Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Represents an constraint on an attribute (e.g. relationship or description) of a concept.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getForm <em>Form</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getDomain <em>Domain</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getPredicate <em>Predicate</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getAttributeConstraint()
 * @model
 * @generated
 */
public interface AttributeConstraint extends ConstraintBase {
	/**
	 * Returns the value of the '<em><b>Form</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.mrcm.ConstraintForm}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Form</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Form</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintForm
	 * @see #setForm(ConstraintForm)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getAttributeConstraint_Form()
	 * @model required="true"
	 * @generated
	 */
	ConstraintForm getForm();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getForm <em>Form</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Form</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintForm
	 * @see #getForm()
	 * @generated
	 */
	void setForm(ConstraintForm value);

	/**
	 * Returns the value of the '<em><b>Domain</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Domain</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Domain</em>' containment reference.
	 * @see #setDomain(ConceptSetDefinition)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getAttributeConstraint_Domain()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ConceptSetDefinition getDomain();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getDomain <em>Domain</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Domain</em>' containment reference.
	 * @see #getDomain()
	 * @generated
	 */
	void setDomain(ConceptSetDefinition value);

	/**
	 * Returns the value of the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Predicate</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Predicate</em>' containment reference.
	 * @see #setPredicate(ConceptModelPredicate)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getAttributeConstraint_Predicate()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ConceptModelPredicate getPredicate();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getPredicate <em>Predicate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Predicate</em>' containment reference.
	 * @see #getPredicate()
	 * @generated
	 */
	void setPredicate(ConceptModelPredicate value);

} // AttributeConstraint
