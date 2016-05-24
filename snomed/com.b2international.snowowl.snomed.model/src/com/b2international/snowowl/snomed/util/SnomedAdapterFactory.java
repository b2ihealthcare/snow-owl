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
package com.b2international.snowowl.snomed.util;

import com.b2international.snowowl.snomed.*;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.SnomedPackage
 * @generated
 */
public class SnomedAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static SnomedPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = SnomedPackage.eINSTANCE;
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
	protected SnomedSwitch<Adapter> modelSwitch =
		new SnomedSwitch<Adapter>() {
			@Override
			public Adapter caseComponent(Component object) {
				return createComponentAdapter();
			}
			@Override
			public Adapter caseInactivatable(Inactivatable object) {
				return createInactivatableAdapter();
			}
			@Override
			public Adapter caseAnnotatable(Annotatable object) {
				return createAnnotatableAdapter();
			}
			@Override
			public Adapter caseDescription(Description object) {
				return createDescriptionAdapter();
			}
			@Override
			public Adapter caseConcept(Concept object) {
				return createConceptAdapter();
			}
			@Override
			public Adapter caseRelationship(Relationship object) {
				return createRelationshipAdapter();
			}
			@Override
			public Adapter caseConcepts(Concepts object) {
				return createConceptsAdapter();
			}
			@Override
			public Adapter caseSnomedRelease(SnomedRelease object) {
				return createSnomedReleaseAdapter();
			}
			@Override
			public Adapter caseSnomedVersion(SnomedVersion object) {
				return createSnomedVersionAdapter();
			}
			@Override
			public Adapter caseCodeSystem(CodeSystem object) {
				return createCodeSystemAdapter();
			}
			@Override
			public Adapter caseCodeSystemVersion(CodeSystemVersion object) {
				return createCodeSystemVersionAdapter();
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
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.Component <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.Component
	 * @generated
	 */
	public Adapter createComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.Inactivatable <em>Inactivatable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.Inactivatable
	 * @generated
	 */
	public Adapter createInactivatableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.Annotatable <em>Annotatable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.Annotatable
	 * @generated
	 */
	public Adapter createAnnotatableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.Description <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.Description
	 * @generated
	 */
	public Adapter createDescriptionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.Concept <em>Concept</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.Concept
	 * @generated
	 */
	public Adapter createConceptAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.Relationship <em>Relationship</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.Relationship
	 * @generated
	 */
	public Adapter createRelationshipAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.Concepts <em>Concepts</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.Concepts
	 * @generated
	 */
	public Adapter createConceptsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.SnomedRelease <em>Release</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.SnomedRelease
	 * @generated
	 */
	public Adapter createSnomedReleaseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.SnomedVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.SnomedVersion
	 * @generated
	 */
	public Adapter createSnomedVersionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.terminologymetadata.CodeSystem <em>Code System</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem
	 * @generated
	 */
	public Adapter createCodeSystemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion <em>Code System Version</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion
	 * @generated
	 */
	public Adapter createCodeSystemVersionAdapter() {
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

} //SnomedAdapterFactory