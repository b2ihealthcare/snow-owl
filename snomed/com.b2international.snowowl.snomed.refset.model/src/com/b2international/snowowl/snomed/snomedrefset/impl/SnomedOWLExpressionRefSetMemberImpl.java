/**
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed OWL Expression Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedOWLExpressionRefSetMemberImpl#getOwlExpression <em>Owl Expression</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedOWLExpressionRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedOWLExpressionRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedOWLExpressionRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOwlExpression() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER__OWL_EXPRESSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOwlExpression(String newOwlExpression) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER__OWL_EXPRESSION, newOwlExpression);
	}

} //SnomedOWLExpressionRefSetMemberImpl
