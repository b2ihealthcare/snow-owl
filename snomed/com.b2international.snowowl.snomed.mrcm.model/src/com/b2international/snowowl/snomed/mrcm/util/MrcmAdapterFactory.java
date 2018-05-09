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
package com.b2international.snowowl.snomed.mrcm.util;

import com.b2international.snowowl.snomed.mrcm.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage
 * @generated
 */
public class MrcmAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MrcmPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MrcmAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = MrcmPackage.eINSTANCE;
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
	protected MrcmSwitch<Adapter> modelSwitch =
		new MrcmSwitch<Adapter>() {
			@Override
			public Adapter caseConceptModel(ConceptModel object) {
				return createConceptModelAdapter();
			}
			@Override
			public Adapter caseConceptModelComponent(ConceptModelComponent object) {
				return createConceptModelComponentAdapter();
			}
			@Override
			public Adapter caseConceptModelPredicate(ConceptModelPredicate object) {
				return createConceptModelPredicateAdapter();
			}
			@Override
			public Adapter caseCardinalityPredicate(CardinalityPredicate object) {
				return createCardinalityPredicateAdapter();
			}
			@Override
			public Adapter caseConcreteDomainElementPredicate(ConcreteDomainElementPredicate object) {
				return createConcreteDomainElementPredicateAdapter();
			}
			@Override
			public Adapter caseDependencyPredicate(DependencyPredicate object) {
				return createDependencyPredicateAdapter();
			}
			@Override
			public Adapter caseDescriptionPredicate(DescriptionPredicate object) {
				return createDescriptionPredicateAdapter();
			}
			@Override
			public Adapter caseRelationshipPredicate(RelationshipPredicate object) {
				return createRelationshipPredicateAdapter();
			}
			@Override
			public Adapter caseConceptSetDefinition(ConceptSetDefinition object) {
				return createConceptSetDefinitionAdapter();
			}
			@Override
			public Adapter caseCompositeConceptSetDefinition(CompositeConceptSetDefinition object) {
				return createCompositeConceptSetDefinitionAdapter();
			}
			@Override
			public Adapter caseEnumeratedConceptSetDefinition(EnumeratedConceptSetDefinition object) {
				return createEnumeratedConceptSetDefinitionAdapter();
			}
			@Override
			public Adapter caseHierarchyConceptSetDefinition(HierarchyConceptSetDefinition object) {
				return createHierarchyConceptSetDefinitionAdapter();
			}
			@Override
			public Adapter caseReferenceSetConceptSetDefinition(ReferenceSetConceptSetDefinition object) {
				return createReferenceSetConceptSetDefinitionAdapter();
			}
			@Override
			public Adapter caseRelationshipConceptSetDefinition(RelationshipConceptSetDefinition object) {
				return createRelationshipConceptSetDefinitionAdapter();
			}
			@Override
			public Adapter caseConstraintBase(ConstraintBase object) {
				return createConstraintBaseAdapter();
			}
			@Override
			public Adapter caseAttributeConstraint(AttributeConstraint object) {
				return createAttributeConstraintAdapter();
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
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.ConceptModel <em>Concept Model</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModel
	 * @generated
	 */
	public Adapter createConceptModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent <em>Concept Model Component</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelComponent
	 * @generated
	 */
	public Adapter createConceptModelComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate <em>Concept Model Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate
	 * @generated
	 */
	public Adapter createConceptModelPredicateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate <em>Cardinality Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.CardinalityPredicate
	 * @generated
	 */
	public Adapter createCardinalityPredicateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate <em>Concrete Domain Element Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate
	 * @generated
	 */
	public Adapter createConcreteDomainElementPredicateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate <em>Dependency Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyPredicate
	 * @generated
	 */
	public Adapter createDependencyPredicateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.DescriptionPredicate <em>Description Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.DescriptionPredicate
	 * @generated
	 */
	public Adapter createDescriptionPredicateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.RelationshipPredicate <em>Relationship Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipPredicate
	 * @generated
	 */
	public Adapter createRelationshipPredicateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition <em>Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition
	 * @generated
	 */
	public Adapter createConceptSetDefinitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition <em>Composite Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition
	 * @generated
	 */
	public Adapter createCompositeConceptSetDefinitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition <em>Enumerated Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition
	 * @generated
	 */
	public Adapter createEnumeratedConceptSetDefinitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition <em>Hierarchy Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition
	 * @generated
	 */
	public Adapter createHierarchyConceptSetDefinitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition <em>Reference Set Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition
	 * @generated
	 */
	public Adapter createReferenceSetConceptSetDefinitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition <em>Relationship Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition
	 * @generated
	 */
	public Adapter createRelationshipConceptSetDefinitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase <em>Constraint Base</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintBase
	 * @generated
	 */
	public Adapter createConstraintBaseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint <em>Attribute Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.b2international.snowowl.snomed.mrcm.AttributeConstraint
	 * @generated
	 */
	public Adapter createAttributeConstraintAdapter() {
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

} //MrcmAdapterFactory
