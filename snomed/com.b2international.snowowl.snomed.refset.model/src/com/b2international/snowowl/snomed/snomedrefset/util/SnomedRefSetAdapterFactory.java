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
package com.b2international.snowowl.snomed.snomedrefset.util;

import com.b2international.snowowl.snomed.snomedrefset.*;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage
 * @generated
 */
public class SnomedRefSetAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static SnomedRefSetPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRefSetAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = SnomedRefSetPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedRefSetSwitch<Adapter> modelSwitch =
		new SnomedRefSetSwitch<Adapter>() {
			@Override
			public Adapter caseSnomedRefSet(SnomedRefSet object) {
				return createSnomedRefSetAdapter();
			}
			@Override
			public Adapter caseSnomedRegularRefSet(SnomedRegularRefSet object) {
				return createSnomedRegularRefSetAdapter();
			}
			@Override
			public Adapter caseSnomedStructuralRefSet(SnomedStructuralRefSet object) {
				return createSnomedStructuralRefSetAdapter();
			}
			@Override
			public Adapter caseSnomedMappingRefSet(SnomedMappingRefSet object) {
				return createSnomedMappingRefSetAdapter();
			}
			@Override
			public Adapter caseSnomedConcreteDataTypeRefSet(SnomedConcreteDataTypeRefSet object) {
				return createSnomedConcreteDataTypeRefSetAdapter();
			}
			@Override
			public Adapter caseSnomedRefSetMember(SnomedRefSetMember object) {
				return createSnomedRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedDescriptionTypeRefSetMember(SnomedDescriptionTypeRefSetMember object) {
				return createSnomedDescriptionTypeRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedSimpleMapRefSetMember(SnomedSimpleMapRefSetMember object) {
				return createSnomedSimpleMapRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedComplexMapRefSetMember(SnomedComplexMapRefSetMember object) {
				return createSnomedComplexMapRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedQueryRefSetMember(SnomedQueryRefSetMember object) {
				return createSnomedQueryRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedAttributeValueRefSetMember(SnomedAttributeValueRefSetMember object) {
				return createSnomedAttributeValueRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedLanguageRefSetMember(SnomedLanguageRefSetMember object) {
				return createSnomedLanguageRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedConcreteDataTypeRefSetMember(SnomedConcreteDataTypeRefSetMember object) {
				return createSnomedConcreteDataTypeRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedAssociationRefSetMember(SnomedAssociationRefSetMember object) {
				return createSnomedAssociationRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedModuleDependencyRefSetMember(SnomedModuleDependencyRefSetMember object) {
				return createSnomedModuleDependencyRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedAnnotationRefSetMember(SnomedAnnotationRefSetMember object) {
				return createSnomedAnnotationRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedMRCMDomainRefSetMember(SnomedMRCMDomainRefSetMember object) {
				return createSnomedMRCMDomainRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedMRCMAttributeDomainRefSetMember(SnomedMRCMAttributeDomainRefSetMember object) {
				return createSnomedMRCMAttributeDomainRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedMRCMAttributeRangeRefSetMember(SnomedMRCMAttributeRangeRefSetMember object) {
				return createSnomedMRCMAttributeRangeRefSetMemberAdapter();
			}
			@Override
			public Adapter caseSnomedMRCMModuleScopeRefSetMember(SnomedMRCMModuleScopeRefSetMember object) {
				return createSnomedMRCMModuleScopeRefSetMemberAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet <em>Snomed Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet
	 * @generated
	 */
	public Adapter createSnomedRefSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet <em>Snomed Regular Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet
	 * @generated
	 */
	public Adapter createSnomedRegularRefSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet <em>Snomed Structural Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet
	 * @generated
	 */
	public Adapter createSnomedStructuralRefSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet <em>Snomed Mapping Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet
	 * @generated
	 */
	public Adapter createSnomedMappingRefSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet <em>Snomed Concrete Data Type Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet
	 * @generated
	 */
	public Adapter createSnomedConcreteDataTypeRefSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember <em>Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember
	 * @generated
	 */
	public Adapter createSnomedRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember <em>Snomed Description Type Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember
	 * @generated
	 */
	public Adapter createSnomedDescriptionTypeRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember <em>Snomed Simple Map Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember
	 * @generated
	 */
	public Adapter createSnomedSimpleMapRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember <em>Snomed Complex Map Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember
	 * @generated
	 */
	public Adapter createSnomedComplexMapRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember <em>Snomed Query Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember
	 * @generated
	 */
	public Adapter createSnomedQueryRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember <em>Snomed Attribute Value Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember
	 * @generated
	 */
	public Adapter createSnomedAttributeValueRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember <em>Snomed Language Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember
	 * @generated
	 */
	public Adapter createSnomedLanguageRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember <em>Snomed Concrete Data Type Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember
	 * @generated
	 */
	public Adapter createSnomedConcreteDataTypeRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember <em>Snomed Association Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember
	 * @generated
	 */
	public Adapter createSnomedAssociationRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember <em>Snomed Module Dependency Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember
	 * @generated
	 */
	public Adapter createSnomedModuleDependencyRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember <em>Snomed Annotation Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember
	 * @generated
	 */
	public Adapter createSnomedAnnotationRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember <em>Snomed MRCM Domain Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember
	 * @generated
	 */
	public Adapter createSnomedMRCMDomainRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember <em>Snomed MRCM Attribute Domain Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember
	 * @generated
	 */
	public Adapter createSnomedMRCMAttributeDomainRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember <em>Snomed MRCM Attribute Range Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember
	 * @generated
	 */
	public Adapter createSnomedMRCMAttributeRangeRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember <em>Snomed MRCM Module Scope Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember
	 * @generated
	 */
	public Adapter createSnomedMRCMModuleScopeRefSetMemberAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //SnomedRefSetAdapterFactory