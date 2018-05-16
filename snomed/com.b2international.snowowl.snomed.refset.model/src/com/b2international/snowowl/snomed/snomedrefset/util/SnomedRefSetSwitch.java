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
package com.b2international.snowowl.snomed.snomedrefset.util;

import com.b2international.snowowl.snomed.snomedrefset.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage
 * @generated
 */
public class SnomedRefSetSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static SnomedRefSetPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRefSetSwitch() {
		if (modelPackage == null) {
			modelPackage = SnomedRefSetPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case SnomedRefSetPackage.SNOMED_REF_SET: {
				SnomedRefSet snomedRefSet = (SnomedRefSet)theEObject;
				T result = caseSnomedRefSet(snomedRefSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_REGULAR_REF_SET: {
				SnomedRegularRefSet snomedRegularRefSet = (SnomedRegularRefSet)theEObject;
				T result = caseSnomedRegularRefSet(snomedRegularRefSet);
				if (result == null) result = caseSnomedRefSet(snomedRegularRefSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_STRUCTURAL_REF_SET: {
				SnomedStructuralRefSet snomedStructuralRefSet = (SnomedStructuralRefSet)theEObject;
				T result = caseSnomedStructuralRefSet(snomedStructuralRefSet);
				if (result == null) result = caseSnomedRefSet(snomedStructuralRefSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_MAPPING_REF_SET: {
				SnomedMappingRefSet snomedMappingRefSet = (SnomedMappingRefSet)theEObject;
				T result = caseSnomedMappingRefSet(snomedMappingRefSet);
				if (result == null) result = caseSnomedRegularRefSet(snomedMappingRefSet);
				if (result == null) result = caseSnomedRefSet(snomedMappingRefSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_CONCRETE_DATA_TYPE_REF_SET: {
				SnomedConcreteDataTypeRefSet snomedConcreteDataTypeRefSet = (SnomedConcreteDataTypeRefSet)theEObject;
				T result = caseSnomedConcreteDataTypeRefSet(snomedConcreteDataTypeRefSet);
				if (result == null) result = caseSnomedStructuralRefSet(snomedConcreteDataTypeRefSet);
				if (result == null) result = caseSnomedRefSet(snomedConcreteDataTypeRefSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_REF_SET_MEMBER: {
				SnomedRefSetMember snomedRefSetMember = (SnomedRefSetMember)theEObject;
				T result = caseSnomedRefSetMember(snomedRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER: {
				SnomedDescriptionTypeRefSetMember snomedDescriptionTypeRefSetMember = (SnomedDescriptionTypeRefSetMember)theEObject;
				T result = caseSnomedDescriptionTypeRefSetMember(snomedDescriptionTypeRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedDescriptionTypeRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_SIMPLE_MAP_REF_SET_MEMBER: {
				SnomedSimpleMapRefSetMember snomedSimpleMapRefSetMember = (SnomedSimpleMapRefSetMember)theEObject;
				T result = caseSnomedSimpleMapRefSetMember(snomedSimpleMapRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedSimpleMapRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_COMPLEX_MAP_REF_SET_MEMBER: {
				SnomedComplexMapRefSetMember snomedComplexMapRefSetMember = (SnomedComplexMapRefSetMember)theEObject;
				T result = caseSnomedComplexMapRefSetMember(snomedComplexMapRefSetMember);
				if (result == null) result = caseSnomedSimpleMapRefSetMember(snomedComplexMapRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedComplexMapRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_QUERY_REF_SET_MEMBER: {
				SnomedQueryRefSetMember snomedQueryRefSetMember = (SnomedQueryRefSetMember)theEObject;
				T result = caseSnomedQueryRefSetMember(snomedQueryRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedQueryRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER: {
				SnomedAttributeValueRefSetMember snomedAttributeValueRefSetMember = (SnomedAttributeValueRefSetMember)theEObject;
				T result = caseSnomedAttributeValueRefSetMember(snomedAttributeValueRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedAttributeValueRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_LANGUAGE_REF_SET_MEMBER: {
				SnomedLanguageRefSetMember snomedLanguageRefSetMember = (SnomedLanguageRefSetMember)theEObject;
				T result = caseSnomedLanguageRefSetMember(snomedLanguageRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedLanguageRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER: {
				SnomedConcreteDataTypeRefSetMember snomedConcreteDataTypeRefSetMember = (SnomedConcreteDataTypeRefSetMember)theEObject;
				T result = caseSnomedConcreteDataTypeRefSetMember(snomedConcreteDataTypeRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedConcreteDataTypeRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_ASSOCIATION_REF_SET_MEMBER: {
				SnomedAssociationRefSetMember snomedAssociationRefSetMember = (SnomedAssociationRefSetMember)theEObject;
				T result = caseSnomedAssociationRefSetMember(snomedAssociationRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedAssociationRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER: {
				SnomedModuleDependencyRefSetMember snomedModuleDependencyRefSetMember = (SnomedModuleDependencyRefSetMember)theEObject;
				T result = caseSnomedModuleDependencyRefSetMember(snomedModuleDependencyRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedModuleDependencyRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_ANNOTATION_REF_SET_MEMBER: {
				SnomedAnnotationRefSetMember snomedAnnotationRefSetMember = (SnomedAnnotationRefSetMember)theEObject;
				T result = caseSnomedAnnotationRefSetMember(snomedAnnotationRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedAnnotationRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER: {
				SnomedOWLExpressionRefSetMember snomedOWLExpressionRefSetMember = (SnomedOWLExpressionRefSetMember)theEObject;
				T result = caseSnomedOWLExpressionRefSetMember(snomedOWLExpressionRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedOWLExpressionRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER: {
				SnomedMRCMDomainRefSetMember snomedMRCMDomainRefSetMember = (SnomedMRCMDomainRefSetMember)theEObject;
				T result = caseSnomedMRCMDomainRefSetMember(snomedMRCMDomainRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedMRCMDomainRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER: {
				SnomedMRCMAttributeDomainRefSetMember snomedMRCMAttributeDomainRefSetMember = (SnomedMRCMAttributeDomainRefSetMember)theEObject;
				T result = caseSnomedMRCMAttributeDomainRefSetMember(snomedMRCMAttributeDomainRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedMRCMAttributeDomainRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER: {
				SnomedMRCMAttributeRangeRefSetMember snomedMRCMAttributeRangeRefSetMember = (SnomedMRCMAttributeRangeRefSetMember)theEObject;
				T result = caseSnomedMRCMAttributeRangeRefSetMember(snomedMRCMAttributeRangeRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedMRCMAttributeRangeRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case SnomedRefSetPackage.SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER: {
				SnomedMRCMModuleScopeRefSetMember snomedMRCMModuleScopeRefSetMember = (SnomedMRCMModuleScopeRefSetMember)theEObject;
				T result = caseSnomedMRCMModuleScopeRefSetMember(snomedMRCMModuleScopeRefSetMember);
				if (result == null) result = caseSnomedRefSetMember(snomedMRCMModuleScopeRefSetMember);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Ref Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedRefSet(SnomedRefSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Regular Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Regular Ref Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedRegularRefSet(SnomedRegularRefSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Structural Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Structural Ref Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedStructuralRefSet(SnomedStructuralRefSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Mapping Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Mapping Ref Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedMappingRefSet(SnomedMappingRefSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Concrete Data Type Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Concrete Data Type Ref Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedConcreteDataTypeRefSet(SnomedConcreteDataTypeRefSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedRefSetMember(SnomedRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Description Type Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Description Type Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedDescriptionTypeRefSetMember(SnomedDescriptionTypeRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Simple Map Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Simple Map Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedSimpleMapRefSetMember(SnomedSimpleMapRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Complex Map Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Complex Map Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedComplexMapRefSetMember(SnomedComplexMapRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Query Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Query Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedQueryRefSetMember(SnomedQueryRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Attribute Value Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Attribute Value Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedAttributeValueRefSetMember(SnomedAttributeValueRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Language Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Language Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedLanguageRefSetMember(SnomedLanguageRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Concrete Data Type Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Concrete Data Type Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedConcreteDataTypeRefSetMember(SnomedConcreteDataTypeRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Association Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Association Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedAssociationRefSetMember(SnomedAssociationRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Module Dependency Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Module Dependency Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedModuleDependencyRefSetMember(SnomedModuleDependencyRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed Annotation Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed Annotation Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedAnnotationRefSetMember(SnomedAnnotationRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed OWL Expression Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed OWL Expression Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedOWLExpressionRefSetMember(SnomedOWLExpressionRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed MRCM Domain Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed MRCM Domain Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedMRCMDomainRefSetMember(SnomedMRCMDomainRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed MRCM Attribute Domain Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed MRCM Attribute Domain Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedMRCMAttributeDomainRefSetMember(SnomedMRCMAttributeDomainRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed MRCM Attribute Range Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed MRCM Attribute Range Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedMRCMAttributeRangeRefSetMember(SnomedMRCMAttributeRangeRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Snomed MRCM Module Scope Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Snomed MRCM Module Scope Ref Set Member</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSnomedMRCMModuleScopeRefSetMember(SnomedMRCMModuleScopeRefSetMember object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //SnomedRefSetSwitch
