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

import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.mrcm.*;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MrcmFactoryImpl extends EFactoryImpl implements MrcmFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MrcmFactory init() {
		try {
			MrcmFactory theMrcmFactory = (MrcmFactory)EPackage.Registry.INSTANCE.getEFactory(MrcmPackage.eNS_URI);
			if (theMrcmFactory != null) {
				return theMrcmFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MrcmFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MrcmFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case MrcmPackage.CONCEPT_MODEL: return (EObject)createConceptModel();
			case MrcmPackage.CONCEPT_MODEL_COMPONENT: return (EObject)createConceptModelComponent();
			case MrcmPackage.CARDINALITY_PREDICATE: return (EObject)createCardinalityPredicate();
			case MrcmPackage.CONCRETE_DOMAIN_ELEMENT_PREDICATE: return (EObject)createConcreteDomainElementPredicate();
			case MrcmPackage.DEPENDENCY_PREDICATE: return (EObject)createDependencyPredicate();
			case MrcmPackage.DESCRIPTION_PREDICATE: return (EObject)createDescriptionPredicate();
			case MrcmPackage.RELATIONSHIP_PREDICATE: return (EObject)createRelationshipPredicate();
			case MrcmPackage.COMPOSITE_CONCEPT_SET_DEFINITION: return (EObject)createCompositeConceptSetDefinition();
			case MrcmPackage.ENUMERATED_CONCEPT_SET_DEFINITION: return (EObject)createEnumeratedConceptSetDefinition();
			case MrcmPackage.HIERARCHY_CONCEPT_SET_DEFINITION: return (EObject)createHierarchyConceptSetDefinition();
			case MrcmPackage.REFERENCE_SET_CONCEPT_SET_DEFINITION: return (EObject)createReferenceSetConceptSetDefinition();
			case MrcmPackage.RELATIONSHIP_CONCEPT_SET_DEFINITION: return (EObject)createRelationshipConceptSetDefinition();
			case MrcmPackage.ATTRIBUTE_CONSTRAINT: return (EObject)createAttributeConstraint();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case MrcmPackage.GROUP_RULE:
				return createGroupRuleFromString(eDataType, initialValue);
			case MrcmPackage.DEPENDENCY_OPERATOR:
				return createDependencyOperatorFromString(eDataType, initialValue);
			case MrcmPackage.HIERARCHY_INCLUSION_TYPE:
				return createHierarchyInclusionTypeFromString(eDataType, initialValue);
			case MrcmPackage.CONSTRAINT_STRENGTH:
				return createConstraintStrengthFromString(eDataType, initialValue);
			case MrcmPackage.CONSTRAINT_FORM:
				return createConstraintFormFromString(eDataType, initialValue);
			case MrcmPackage.DATA_TYPE:
				return createDataTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case MrcmPackage.GROUP_RULE:
				return convertGroupRuleToString(eDataType, instanceValue);
			case MrcmPackage.DEPENDENCY_OPERATOR:
				return convertDependencyOperatorToString(eDataType, instanceValue);
			case MrcmPackage.HIERARCHY_INCLUSION_TYPE:
				return convertHierarchyInclusionTypeToString(eDataType, instanceValue);
			case MrcmPackage.CONSTRAINT_STRENGTH:
				return convertConstraintStrengthToString(eDataType, instanceValue);
			case MrcmPackage.CONSTRAINT_FORM:
				return convertConstraintFormToString(eDataType, instanceValue);
			case MrcmPackage.DATA_TYPE:
				return convertDataTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConceptModel createConceptModel() {
		ConceptModelImpl conceptModel = new ConceptModelImpl();
		return conceptModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConceptModelComponent createConceptModelComponent() {
		ConceptModelComponentImpl conceptModelComponent = new ConceptModelComponentImpl();
		return conceptModelComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CardinalityPredicate createCardinalityPredicate() {
		CardinalityPredicateImpl cardinalityPredicate = new CardinalityPredicateImpl();
		return cardinalityPredicate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConcreteDomainElementPredicate createConcreteDomainElementPredicate() {
		ConcreteDomainElementPredicateImpl concreteDomainElementPredicate = new ConcreteDomainElementPredicateImpl();
		return concreteDomainElementPredicate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DependencyPredicate createDependencyPredicate() {
		DependencyPredicateImpl dependencyPredicate = new DependencyPredicateImpl();
		return dependencyPredicate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DescriptionPredicate createDescriptionPredicate() {
		DescriptionPredicateImpl descriptionPredicate = new DescriptionPredicateImpl();
		return descriptionPredicate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RelationshipPredicate createRelationshipPredicate() {
		RelationshipPredicateImpl relationshipPredicate = new RelationshipPredicateImpl();
		return relationshipPredicate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CompositeConceptSetDefinition createCompositeConceptSetDefinition() {
		CompositeConceptSetDefinitionImpl compositeConceptSetDefinition = new CompositeConceptSetDefinitionImpl();
		return compositeConceptSetDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumeratedConceptSetDefinition createEnumeratedConceptSetDefinition() {
		EnumeratedConceptSetDefinitionImpl enumeratedConceptSetDefinition = new EnumeratedConceptSetDefinitionImpl();
		return enumeratedConceptSetDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HierarchyConceptSetDefinition createHierarchyConceptSetDefinition() {
		HierarchyConceptSetDefinitionImpl hierarchyConceptSetDefinition = new HierarchyConceptSetDefinitionImpl();
		return hierarchyConceptSetDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceSetConceptSetDefinition createReferenceSetConceptSetDefinition() {
		ReferenceSetConceptSetDefinitionImpl referenceSetConceptSetDefinition = new ReferenceSetConceptSetDefinitionImpl();
		return referenceSetConceptSetDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RelationshipConceptSetDefinition createRelationshipConceptSetDefinition() {
		RelationshipConceptSetDefinitionImpl relationshipConceptSetDefinition = new RelationshipConceptSetDefinitionImpl();
		return relationshipConceptSetDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeConstraint createAttributeConstraint() {
		AttributeConstraintImpl attributeConstraint = new AttributeConstraintImpl();
		return attributeConstraint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GroupRule createGroupRuleFromString(EDataType eDataType, String initialValue) {
		GroupRule result = GroupRule.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGroupRuleToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DependencyOperator createDependencyOperatorFromString(EDataType eDataType, String initialValue) {
		DependencyOperator result = DependencyOperator.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDependencyOperatorToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HierarchyInclusionType createHierarchyInclusionTypeFromString(EDataType eDataType, String initialValue) {
		HierarchyInclusionType result = HierarchyInclusionType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertHierarchyInclusionTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConstraintStrength createConstraintStrengthFromString(EDataType eDataType, String initialValue) {
		ConstraintStrength result = ConstraintStrength.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConstraintStrengthToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConstraintForm createConstraintFormFromString(EDataType eDataType, String initialValue) {
		ConstraintForm result = ConstraintForm.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConstraintFormToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public DataType createDataTypeFromString(EDataType eDataType, String initialValue) {
		return (DataType) SnomedRefSetFactory.eINSTANCE.createFromString(SnomedRefSetPackage.eINSTANCE.getDataType(), initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String convertDataTypeToString(EDataType eDataType, Object instanceValue) {
		return SnomedRefSetFactory.eINSTANCE.convertToString(SnomedRefSetPackage.eINSTANCE.getDataType(), instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MrcmPackage getMrcmPackage() {
		return (MrcmPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MrcmPackage getPackage() {
		return MrcmPackage.eINSTANCE;
	}

} //MrcmFactoryImpl
