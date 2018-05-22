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
package com.b2international.snowowl.snomed.mrcm.impl;

import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import java.util.Date;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concept Model Component</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl#getUuid <em>Uuid</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl#isActive <em>Active</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl#getEffectiveTime <em>Effective Time</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl#getAuthor <em>Author</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConceptModelComponentImpl extends CDOObjectImpl implements ConceptModelComponent {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConceptModelComponentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected int eStaticFeatureCount() {
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUuid() {
		return (String)eGet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__UUID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUuid(String newUuid) {
		eSet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__UUID, newUuid);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isActive() {
		return (Boolean)eGet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__ACTIVE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setActive(boolean newActive) {
		eSet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__ACTIVE, newActive);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getEffectiveTime() {
		return (Date)eGet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEffectiveTime(Date newEffectiveTime) {
		eSet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME, newEffectiveTime);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAuthor() {
		return (String)eGet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__AUTHOR, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAuthor(String newAuthor) {
		eSet(MrcmPackage.Literals.CONCEPT_MODEL_COMPONENT__AUTHOR, newAuthor);
	}

} //ConceptModelComponentImpl
