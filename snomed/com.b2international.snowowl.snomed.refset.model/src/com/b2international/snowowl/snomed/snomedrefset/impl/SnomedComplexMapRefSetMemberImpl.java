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
package com.b2international.snowowl.snomed.snomedrefset.impl;

import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed Complex Map Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl#getMapGroup <em>Map Group</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl#getMapPriority <em>Map Priority</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl#getMapRule <em>Map Rule</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl#getMapAdvice <em>Map Advice</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl#getCorrelationId <em>Correlation Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl#getMapCategoryId <em>Map Category Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedComplexMapRefSetMemberImpl extends SnomedSimpleMapRefSetMemberImpl implements SnomedComplexMapRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedComplexMapRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getMapGroup() {
		return (Integer)eGet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_GROUP, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMapGroup(int newMapGroup) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_GROUP, newMapGroup);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getMapPriority() {
		return (Integer)eGet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_PRIORITY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMapPriority(int newMapPriority) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_PRIORITY, newMapPriority);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMapRule() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_RULE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMapRule(String newMapRule) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_RULE, newMapRule);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMapAdvice() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_ADVICE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMapAdvice(String newMapAdvice) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_ADVICE, newMapAdvice);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCorrelationId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__CORRELATION_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCorrelationId(String newCorrelationId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__CORRELATION_ID, newCorrelationId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMapCategoryId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_CATEGORY_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMapCategoryId(String newMapCategoryId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_CATEGORY_ID, newMapCategoryId);
	}

} //SnomedComplexMapRefSetMemberImpl