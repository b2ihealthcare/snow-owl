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
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage
 * @generated
 */
public class MrcmSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MrcmPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MrcmSwitch() {
		if (modelPackage == null) {
			modelPackage = MrcmPackage.eINSTANCE;
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
			case MrcmPackage.CONCEPT_MODEL: {
				ConceptModel conceptModel = (ConceptModel)theEObject;
				T result = caseConceptModel(conceptModel);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.CONCEPT_MODEL_COMPONENT: {
				ConceptModelComponent conceptModelComponent = (ConceptModelComponent)theEObject;
				T result = caseConceptModelComponent(conceptModelComponent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.CONCEPT_MODEL_PREDICATE: {
				ConceptModelPredicate conceptModelPredicate = (ConceptModelPredicate)theEObject;
				T result = caseConceptModelPredicate(conceptModelPredicate);
				if (result == null) result = caseConceptModelComponent(conceptModelPredicate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.CARDINALITY_PREDICATE: {
				CardinalityPredicate cardinalityPredicate = (CardinalityPredicate)theEObject;
				T result = caseCardinalityPredicate(cardinalityPredicate);
				if (result == null) result = caseConceptModelPredicate(cardinalityPredicate);
				if (result == null) result = caseConceptModelComponent(cardinalityPredicate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.CONCRETE_DOMAIN_ELEMENT_PREDICATE: {
				ConcreteDomainElementPredicate concreteDomainElementPredicate = (ConcreteDomainElementPredicate)theEObject;
				T result = caseConcreteDomainElementPredicate(concreteDomainElementPredicate);
				if (result == null) result = caseConceptModelPredicate(concreteDomainElementPredicate);
				if (result == null) result = caseConceptModelComponent(concreteDomainElementPredicate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.DEPENDENCY_PREDICATE: {
				DependencyPredicate dependencyPredicate = (DependencyPredicate)theEObject;
				T result = caseDependencyPredicate(dependencyPredicate);
				if (result == null) result = caseConceptModelPredicate(dependencyPredicate);
				if (result == null) result = caseConceptModelComponent(dependencyPredicate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.DESCRIPTION_PREDICATE: {
				DescriptionPredicate descriptionPredicate = (DescriptionPredicate)theEObject;
				T result = caseDescriptionPredicate(descriptionPredicate);
				if (result == null) result = caseConceptModelPredicate(descriptionPredicate);
				if (result == null) result = caseConceptModelComponent(descriptionPredicate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.RELATIONSHIP_PREDICATE: {
				RelationshipPredicate relationshipPredicate = (RelationshipPredicate)theEObject;
				T result = caseRelationshipPredicate(relationshipPredicate);
				if (result == null) result = caseConceptModelPredicate(relationshipPredicate);
				if (result == null) result = caseConceptModelComponent(relationshipPredicate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.CONCEPT_SET_DEFINITION: {
				ConceptSetDefinition conceptSetDefinition = (ConceptSetDefinition)theEObject;
				T result = caseConceptSetDefinition(conceptSetDefinition);
				if (result == null) result = caseConceptModelComponent(conceptSetDefinition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.COMPOSITE_CONCEPT_SET_DEFINITION: {
				CompositeConceptSetDefinition compositeConceptSetDefinition = (CompositeConceptSetDefinition)theEObject;
				T result = caseCompositeConceptSetDefinition(compositeConceptSetDefinition);
				if (result == null) result = caseConceptSetDefinition(compositeConceptSetDefinition);
				if (result == null) result = caseConceptModelComponent(compositeConceptSetDefinition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.ENUMERATED_CONCEPT_SET_DEFINITION: {
				EnumeratedConceptSetDefinition enumeratedConceptSetDefinition = (EnumeratedConceptSetDefinition)theEObject;
				T result = caseEnumeratedConceptSetDefinition(enumeratedConceptSetDefinition);
				if (result == null) result = caseConceptSetDefinition(enumeratedConceptSetDefinition);
				if (result == null) result = caseConceptModelComponent(enumeratedConceptSetDefinition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.HIERARCHY_CONCEPT_SET_DEFINITION: {
				HierarchyConceptSetDefinition hierarchyConceptSetDefinition = (HierarchyConceptSetDefinition)theEObject;
				T result = caseHierarchyConceptSetDefinition(hierarchyConceptSetDefinition);
				if (result == null) result = caseConceptSetDefinition(hierarchyConceptSetDefinition);
				if (result == null) result = caseConceptModelComponent(hierarchyConceptSetDefinition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.REFERENCE_SET_CONCEPT_SET_DEFINITION: {
				ReferenceSetConceptSetDefinition referenceSetConceptSetDefinition = (ReferenceSetConceptSetDefinition)theEObject;
				T result = caseReferenceSetConceptSetDefinition(referenceSetConceptSetDefinition);
				if (result == null) result = caseConceptSetDefinition(referenceSetConceptSetDefinition);
				if (result == null) result = caseConceptModelComponent(referenceSetConceptSetDefinition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.RELATIONSHIP_CONCEPT_SET_DEFINITION: {
				RelationshipConceptSetDefinition relationshipConceptSetDefinition = (RelationshipConceptSetDefinition)theEObject;
				T result = caseRelationshipConceptSetDefinition(relationshipConceptSetDefinition);
				if (result == null) result = caseConceptSetDefinition(relationshipConceptSetDefinition);
				if (result == null) result = caseConceptModelComponent(relationshipConceptSetDefinition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.CONSTRAINT_BASE: {
				ConstraintBase constraintBase = (ConstraintBase)theEObject;
				T result = caseConstraintBase(constraintBase);
				if (result == null) result = caseConceptModelComponent(constraintBase);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MrcmPackage.ATTRIBUTE_CONSTRAINT: {
				AttributeConstraint attributeConstraint = (AttributeConstraint)theEObject;
				T result = caseAttributeConstraint(attributeConstraint);
				if (result == null) result = caseConstraintBase(attributeConstraint);
				if (result == null) result = caseConceptModelComponent(attributeConstraint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Concept Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Concept Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConceptModel(ConceptModel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Concept Model Component</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Concept Model Component</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConceptModelComponent(ConceptModelComponent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Concept Model Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Concept Model Predicate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConceptModelPredicate(ConceptModelPredicate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Cardinality Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Cardinality Predicate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCardinalityPredicate(CardinalityPredicate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Concrete Domain Element Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Concrete Domain Element Predicate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConcreteDomainElementPredicate(ConcreteDomainElementPredicate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Dependency Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dependency Predicate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDependencyPredicate(DependencyPredicate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Description Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Description Predicate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDescriptionPredicate(DescriptionPredicate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Relationship Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Relationship Predicate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRelationshipPredicate(RelationshipPredicate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Concept Set Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConceptSetDefinition(ConceptSetDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Composite Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Composite Concept Set Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCompositeConceptSetDefinition(CompositeConceptSetDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Enumerated Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Enumerated Concept Set Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEnumeratedConceptSetDefinition(EnumeratedConceptSetDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Hierarchy Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Hierarchy Concept Set Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseHierarchyConceptSetDefinition(HierarchyConceptSetDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reference Set Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference Set Concept Set Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReferenceSetConceptSetDefinition(ReferenceSetConceptSetDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Relationship Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Relationship Concept Set Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRelationshipConceptSetDefinition(RelationshipConceptSetDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Constraint Base</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Constraint Base</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConstraintBase(ConstraintBase object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attribute Constraint</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attribute Constraint</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttributeConstraint(AttributeConstraint object) {
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

} //MrcmSwitch
