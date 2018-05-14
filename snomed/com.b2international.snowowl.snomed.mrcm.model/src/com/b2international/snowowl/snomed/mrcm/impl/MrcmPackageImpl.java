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

import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.DependencyOperator;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;

import com.b2international.snowowl.snomed.snomedrefset.DataType;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MrcmPackageImpl extends EPackageImpl implements MrcmPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conceptModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conceptModelComponentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conceptModelPredicateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cardinalityPredicateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass concreteDomainElementPredicateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dependencyPredicateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass descriptionPredicateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass relationshipPredicateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conceptSetDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass compositeConceptSetDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass enumeratedConceptSetDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass hierarchyConceptSetDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceSetConceptSetDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass relationshipConceptSetDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass constraintBaseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass attributeConstraintEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum groupRuleEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dependencyOperatorEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum hierarchyInclusionTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum constraintStrengthEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum constraintFormEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType dataTypeEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private MrcmPackageImpl() {
		super(eNS_URI, MrcmFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link MrcmPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static MrcmPackage init() {
		if (isInited) return (MrcmPackage)EPackage.Registry.INSTANCE.getEPackage(MrcmPackage.eNS_URI);

		// Obtain or create and register package
		MrcmPackageImpl theMrcmPackage = (MrcmPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof MrcmPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new MrcmPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theMrcmPackage.createPackageContents();

		// Initialize created meta-data
		theMrcmPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theMrcmPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(MrcmPackage.eNS_URI, theMrcmPackage);
		return theMrcmPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConceptModel() {
		return conceptModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getConceptModel_Constraints() {
		return (EReference)conceptModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConceptModelComponent() {
		return conceptModelComponentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConceptModelComponent_Uuid() {
		return (EAttribute)conceptModelComponentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConceptModelComponent_Active() {
		return (EAttribute)conceptModelComponentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConceptModelComponent_EffectiveTime() {
		return (EAttribute)conceptModelComponentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConceptModelComponent_Author() {
		return (EAttribute)conceptModelComponentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConceptModelPredicate() {
		return conceptModelPredicateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCardinalityPredicate() {
		return cardinalityPredicateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCardinalityPredicate_MinCardinality() {
		return (EAttribute)cardinalityPredicateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCardinalityPredicate_MaxCardinality() {
		return (EAttribute)cardinalityPredicateEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCardinalityPredicate_GroupRule() {
		return (EAttribute)cardinalityPredicateEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCardinalityPredicate_Predicate() {
		return (EReference)cardinalityPredicateEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConcreteDomainElementPredicate() {
		return concreteDomainElementPredicateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConcreteDomainElementPredicate_Name() {
		return (EAttribute)concreteDomainElementPredicateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConcreteDomainElementPredicate_Label() {
		return (EAttribute)concreteDomainElementPredicateEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConcreteDomainElementPredicate_Type() {
		return (EAttribute)concreteDomainElementPredicateEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConcreteDomainElementPredicate_CharacteristicTypeConceptId() {
		return (EAttribute)concreteDomainElementPredicateEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDependencyPredicate() {
		return dependencyPredicateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDependencyPredicate_Children() {
		return (EReference)dependencyPredicateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDependencyPredicate_Operator() {
		return (EAttribute)dependencyPredicateEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDependencyPredicate_GroupRule() {
		return (EAttribute)dependencyPredicateEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDescriptionPredicate() {
		return descriptionPredicateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDescriptionPredicate_TypeId() {
		return (EAttribute)descriptionPredicateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRelationshipPredicate() {
		return relationshipPredicateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRelationshipPredicate_Attribute() {
		return (EReference)relationshipPredicateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRelationshipPredicate_Range() {
		return (EReference)relationshipPredicateEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRelationshipPredicate_CharacteristicTypeConceptId() {
		return (EAttribute)relationshipPredicateEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConceptSetDefinition() {
		return conceptSetDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCompositeConceptSetDefinition() {
		return compositeConceptSetDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCompositeConceptSetDefinition_Children() {
		return (EReference)compositeConceptSetDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEnumeratedConceptSetDefinition() {
		return enumeratedConceptSetDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEnumeratedConceptSetDefinition_ConceptIds() {
		return (EAttribute)enumeratedConceptSetDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getHierarchyConceptSetDefinition() {
		return hierarchyConceptSetDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getHierarchyConceptSetDefinition_ConceptId() {
		return (EAttribute)hierarchyConceptSetDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getHierarchyConceptSetDefinition_InclusionType() {
		return (EAttribute)hierarchyConceptSetDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getReferenceSetConceptSetDefinition() {
		return referenceSetConceptSetDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getReferenceSetConceptSetDefinition_RefSetIdentifierConceptId() {
		return (EAttribute)referenceSetConceptSetDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRelationshipConceptSetDefinition() {
		return relationshipConceptSetDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRelationshipConceptSetDefinition_TypeConceptId() {
		return (EAttribute)relationshipConceptSetDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRelationshipConceptSetDefinition_DestinationConceptId() {
		return (EAttribute)relationshipConceptSetDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConstraintBase() {
		return constraintBaseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConstraintBase_Strength() {
		return (EAttribute)constraintBaseEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConstraintBase_ValidationMessage() {
		return (EAttribute)constraintBaseEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConstraintBase_Description() {
		return (EAttribute)constraintBaseEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAttributeConstraint() {
		return attributeConstraintEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAttributeConstraint_Form() {
		return (EAttribute)attributeConstraintEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAttributeConstraint_Domain() {
		return (EReference)attributeConstraintEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAttributeConstraint_Predicate() {
		return (EReference)attributeConstraintEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getGroupRule() {
		return groupRuleEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDependencyOperator() {
		return dependencyOperatorEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getHierarchyInclusionType() {
		return hierarchyInclusionTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getConstraintStrength() {
		return constraintStrengthEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getConstraintForm() {
		return constraintFormEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getDataType() {
		return dataTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MrcmFactory getMrcmFactory() {
		return (MrcmFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		conceptModelEClass = createEClass(CONCEPT_MODEL);
		createEReference(conceptModelEClass, CONCEPT_MODEL__CONSTRAINTS);

		conceptModelComponentEClass = createEClass(CONCEPT_MODEL_COMPONENT);
		createEAttribute(conceptModelComponentEClass, CONCEPT_MODEL_COMPONENT__UUID);
		createEAttribute(conceptModelComponentEClass, CONCEPT_MODEL_COMPONENT__ACTIVE);
		createEAttribute(conceptModelComponentEClass, CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME);
		createEAttribute(conceptModelComponentEClass, CONCEPT_MODEL_COMPONENT__AUTHOR);

		conceptModelPredicateEClass = createEClass(CONCEPT_MODEL_PREDICATE);

		cardinalityPredicateEClass = createEClass(CARDINALITY_PREDICATE);
		createEAttribute(cardinalityPredicateEClass, CARDINALITY_PREDICATE__MIN_CARDINALITY);
		createEAttribute(cardinalityPredicateEClass, CARDINALITY_PREDICATE__MAX_CARDINALITY);
		createEAttribute(cardinalityPredicateEClass, CARDINALITY_PREDICATE__GROUP_RULE);
		createEReference(cardinalityPredicateEClass, CARDINALITY_PREDICATE__PREDICATE);

		concreteDomainElementPredicateEClass = createEClass(CONCRETE_DOMAIN_ELEMENT_PREDICATE);
		createEAttribute(concreteDomainElementPredicateEClass, CONCRETE_DOMAIN_ELEMENT_PREDICATE__NAME);
		createEAttribute(concreteDomainElementPredicateEClass, CONCRETE_DOMAIN_ELEMENT_PREDICATE__LABEL);
		createEAttribute(concreteDomainElementPredicateEClass, CONCRETE_DOMAIN_ELEMENT_PREDICATE__TYPE);
		createEAttribute(concreteDomainElementPredicateEClass, CONCRETE_DOMAIN_ELEMENT_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID);

		dependencyPredicateEClass = createEClass(DEPENDENCY_PREDICATE);
		createEReference(dependencyPredicateEClass, DEPENDENCY_PREDICATE__CHILDREN);
		createEAttribute(dependencyPredicateEClass, DEPENDENCY_PREDICATE__OPERATOR);
		createEAttribute(dependencyPredicateEClass, DEPENDENCY_PREDICATE__GROUP_RULE);

		descriptionPredicateEClass = createEClass(DESCRIPTION_PREDICATE);
		createEAttribute(descriptionPredicateEClass, DESCRIPTION_PREDICATE__TYPE_ID);

		relationshipPredicateEClass = createEClass(RELATIONSHIP_PREDICATE);
		createEReference(relationshipPredicateEClass, RELATIONSHIP_PREDICATE__ATTRIBUTE);
		createEReference(relationshipPredicateEClass, RELATIONSHIP_PREDICATE__RANGE);
		createEAttribute(relationshipPredicateEClass, RELATIONSHIP_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID);

		conceptSetDefinitionEClass = createEClass(CONCEPT_SET_DEFINITION);

		compositeConceptSetDefinitionEClass = createEClass(COMPOSITE_CONCEPT_SET_DEFINITION);
		createEReference(compositeConceptSetDefinitionEClass, COMPOSITE_CONCEPT_SET_DEFINITION__CHILDREN);

		enumeratedConceptSetDefinitionEClass = createEClass(ENUMERATED_CONCEPT_SET_DEFINITION);
		createEAttribute(enumeratedConceptSetDefinitionEClass, ENUMERATED_CONCEPT_SET_DEFINITION__CONCEPT_IDS);

		hierarchyConceptSetDefinitionEClass = createEClass(HIERARCHY_CONCEPT_SET_DEFINITION);
		createEAttribute(hierarchyConceptSetDefinitionEClass, HIERARCHY_CONCEPT_SET_DEFINITION__CONCEPT_ID);
		createEAttribute(hierarchyConceptSetDefinitionEClass, HIERARCHY_CONCEPT_SET_DEFINITION__INCLUSION_TYPE);

		referenceSetConceptSetDefinitionEClass = createEClass(REFERENCE_SET_CONCEPT_SET_DEFINITION);
		createEAttribute(referenceSetConceptSetDefinitionEClass, REFERENCE_SET_CONCEPT_SET_DEFINITION__REF_SET_IDENTIFIER_CONCEPT_ID);

		relationshipConceptSetDefinitionEClass = createEClass(RELATIONSHIP_CONCEPT_SET_DEFINITION);
		createEAttribute(relationshipConceptSetDefinitionEClass, RELATIONSHIP_CONCEPT_SET_DEFINITION__TYPE_CONCEPT_ID);
		createEAttribute(relationshipConceptSetDefinitionEClass, RELATIONSHIP_CONCEPT_SET_DEFINITION__DESTINATION_CONCEPT_ID);

		constraintBaseEClass = createEClass(CONSTRAINT_BASE);
		createEAttribute(constraintBaseEClass, CONSTRAINT_BASE__STRENGTH);
		createEAttribute(constraintBaseEClass, CONSTRAINT_BASE__VALIDATION_MESSAGE);
		createEAttribute(constraintBaseEClass, CONSTRAINT_BASE__DESCRIPTION);

		attributeConstraintEClass = createEClass(ATTRIBUTE_CONSTRAINT);
		createEAttribute(attributeConstraintEClass, ATTRIBUTE_CONSTRAINT__FORM);
		createEReference(attributeConstraintEClass, ATTRIBUTE_CONSTRAINT__DOMAIN);
		createEReference(attributeConstraintEClass, ATTRIBUTE_CONSTRAINT__PREDICATE);

		// Create enums
		groupRuleEEnum = createEEnum(GROUP_RULE);
		dependencyOperatorEEnum = createEEnum(DEPENDENCY_OPERATOR);
		hierarchyInclusionTypeEEnum = createEEnum(HIERARCHY_INCLUSION_TYPE);
		constraintStrengthEEnum = createEEnum(CONSTRAINT_STRENGTH);
		constraintFormEEnum = createEEnum(CONSTRAINT_FORM);

		// Create data types
		dataTypeEDataType = createEDataType(DATA_TYPE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		conceptModelPredicateEClass.getESuperTypes().add(this.getConceptModelComponent());
		cardinalityPredicateEClass.getESuperTypes().add(this.getConceptModelPredicate());
		concreteDomainElementPredicateEClass.getESuperTypes().add(this.getConceptModelPredicate());
		dependencyPredicateEClass.getESuperTypes().add(this.getConceptModelPredicate());
		descriptionPredicateEClass.getESuperTypes().add(this.getConceptModelPredicate());
		relationshipPredicateEClass.getESuperTypes().add(this.getConceptModelPredicate());
		conceptSetDefinitionEClass.getESuperTypes().add(this.getConceptModelComponent());
		compositeConceptSetDefinitionEClass.getESuperTypes().add(this.getConceptSetDefinition());
		enumeratedConceptSetDefinitionEClass.getESuperTypes().add(this.getConceptSetDefinition());
		hierarchyConceptSetDefinitionEClass.getESuperTypes().add(this.getConceptSetDefinition());
		referenceSetConceptSetDefinitionEClass.getESuperTypes().add(this.getConceptSetDefinition());
		relationshipConceptSetDefinitionEClass.getESuperTypes().add(this.getConceptSetDefinition());
		constraintBaseEClass.getESuperTypes().add(this.getConceptModelComponent());
		attributeConstraintEClass.getESuperTypes().add(this.getConstraintBase());

		// Initialize classes and features; add operations and parameters
		initEClass(conceptModelEClass, ConceptModel.class, "ConceptModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getConceptModel_Constraints(), this.getConstraintBase(), null, "constraints", null, 0, -1, ConceptModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(conceptModelComponentEClass, ConceptModelComponent.class, "ConceptModelComponent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConceptModelComponent_Uuid(), ecorePackage.getEString(), "uuid", null, 1, 1, ConceptModelComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConceptModelComponent_Active(), ecorePackage.getEBoolean(), "active", null, 1, 1, ConceptModelComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConceptModelComponent_EffectiveTime(), ecorePackage.getEDate(), "effectiveTime", null, 1, 1, ConceptModelComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConceptModelComponent_Author(), ecorePackage.getEString(), "author", null, 1, 1, ConceptModelComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(conceptModelPredicateEClass, ConceptModelPredicate.class, "ConceptModelPredicate", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(cardinalityPredicateEClass, CardinalityPredicate.class, "CardinalityPredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCardinalityPredicate_MinCardinality(), ecorePackage.getEInt(), "minCardinality", null, 1, 1, CardinalityPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCardinalityPredicate_MaxCardinality(), ecorePackage.getEInt(), "maxCardinality", null, 1, 1, CardinalityPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCardinalityPredicate_GroupRule(), this.getGroupRule(), "groupRule", null, 1, 1, CardinalityPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCardinalityPredicate_Predicate(), this.getConceptModelPredicate(), null, "predicate", null, 1, 1, CardinalityPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(concreteDomainElementPredicateEClass, ConcreteDomainElementPredicate.class, "ConcreteDomainElementPredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConcreteDomainElementPredicate_Name(), ecorePackage.getEString(), "name", null, 1, 1, ConcreteDomainElementPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConcreteDomainElementPredicate_Label(), ecorePackage.getEString(), "label", "", 1, 1, ConcreteDomainElementPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConcreteDomainElementPredicate_Type(), this.getDataType(), "type", null, 1, 1, ConcreteDomainElementPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConcreteDomainElementPredicate_CharacteristicTypeConceptId(), ecorePackage.getEString(), "characteristicTypeConceptId", null, 0, 1, ConcreteDomainElementPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dependencyPredicateEClass, DependencyPredicate.class, "DependencyPredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDependencyPredicate_Children(), this.getConceptModelPredicate(), null, "children", null, 0, -1, DependencyPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDependencyPredicate_Operator(), this.getDependencyOperator(), "operator", null, 1, 1, DependencyPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDependencyPredicate_GroupRule(), this.getGroupRule(), "groupRule", null, 1, 1, DependencyPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(descriptionPredicateEClass, DescriptionPredicate.class, "DescriptionPredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDescriptionPredicate_TypeId(), ecorePackage.getEString(), "typeId", null, 1, 1, DescriptionPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(relationshipPredicateEClass, RelationshipPredicate.class, "RelationshipPredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRelationshipPredicate_Attribute(), this.getConceptSetDefinition(), null, "attribute", null, 1, 1, RelationshipPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRelationshipPredicate_Range(), this.getConceptSetDefinition(), null, "range", null, 1, 1, RelationshipPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRelationshipPredicate_CharacteristicTypeConceptId(), ecorePackage.getEString(), "characteristicTypeConceptId", null, 0, 1, RelationshipPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(conceptSetDefinitionEClass, ConceptSetDefinition.class, "ConceptSetDefinition", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(compositeConceptSetDefinitionEClass, CompositeConceptSetDefinition.class, "CompositeConceptSetDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCompositeConceptSetDefinition_Children(), this.getConceptSetDefinition(), null, "children", null, 0, -1, CompositeConceptSetDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(compositeConceptSetDefinitionEClass, ecorePackage.getEBoolean(), "addChild", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getConceptSetDefinition(), "child", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(compositeConceptSetDefinitionEClass, ecorePackage.getEBoolean(), "removeChild", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getConceptSetDefinition(), "child", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(enumeratedConceptSetDefinitionEClass, EnumeratedConceptSetDefinition.class, "EnumeratedConceptSetDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEnumeratedConceptSetDefinition_ConceptIds(), ecorePackage.getEString(), "conceptIds", null, 0, -1, EnumeratedConceptSetDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(hierarchyConceptSetDefinitionEClass, HierarchyConceptSetDefinition.class, "HierarchyConceptSetDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getHierarchyConceptSetDefinition_ConceptId(), ecorePackage.getEString(), "conceptId", null, 1, 1, HierarchyConceptSetDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHierarchyConceptSetDefinition_InclusionType(), this.getHierarchyInclusionType(), "inclusionType", null, 1, 1, HierarchyConceptSetDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(hierarchyConceptSetDefinitionEClass, ecorePackage.getEString(), "getFocusConceptId", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(referenceSetConceptSetDefinitionEClass, ReferenceSetConceptSetDefinition.class, "ReferenceSetConceptSetDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getReferenceSetConceptSetDefinition_RefSetIdentifierConceptId(), ecorePackage.getEString(), "refSetIdentifierConceptId", null, 1, 1, ReferenceSetConceptSetDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(relationshipConceptSetDefinitionEClass, RelationshipConceptSetDefinition.class, "RelationshipConceptSetDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRelationshipConceptSetDefinition_TypeConceptId(), ecorePackage.getEString(), "typeConceptId", null, 1, 1, RelationshipConceptSetDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRelationshipConceptSetDefinition_DestinationConceptId(), ecorePackage.getEString(), "destinationConceptId", null, 1, 1, RelationshipConceptSetDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(constraintBaseEClass, ConstraintBase.class, "ConstraintBase", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConstraintBase_Strength(), this.getConstraintStrength(), "strength", null, 1, 1, ConstraintBase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConstraintBase_ValidationMessage(), ecorePackage.getEString(), "validationMessage", null, 0, 1, ConstraintBase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConstraintBase_Description(), ecorePackage.getEString(), "description", null, 0, 1, ConstraintBase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(attributeConstraintEClass, AttributeConstraint.class, "AttributeConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAttributeConstraint_Form(), this.getConstraintForm(), "form", null, 1, 1, AttributeConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAttributeConstraint_Domain(), this.getConceptSetDefinition(), null, "domain", null, 1, 1, AttributeConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAttributeConstraint_Predicate(), this.getConceptModelPredicate(), null, "predicate", null, 1, 1, AttributeConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(groupRuleEEnum, GroupRule.class, "GroupRule");
		addEEnumLiteral(groupRuleEEnum, GroupRule.UNGROUPED);
		addEEnumLiteral(groupRuleEEnum, GroupRule.SINGLE_GROUP);
		addEEnumLiteral(groupRuleEEnum, GroupRule.ALL_GROUPS);
		addEEnumLiteral(groupRuleEEnum, GroupRule.MULTIPLE_GROUPS);

		initEEnum(dependencyOperatorEEnum, DependencyOperator.class, "DependencyOperator");
		addEEnumLiteral(dependencyOperatorEEnum, DependencyOperator.ONE);
		addEEnumLiteral(dependencyOperatorEEnum, DependencyOperator.SOME);
		addEEnumLiteral(dependencyOperatorEEnum, DependencyOperator.ALL);

		initEEnum(hierarchyInclusionTypeEEnum, HierarchyInclusionType.class, "HierarchyInclusionType");
		addEEnumLiteral(hierarchyInclusionTypeEEnum, HierarchyInclusionType.SELF);
		addEEnumLiteral(hierarchyInclusionTypeEEnum, HierarchyInclusionType.DESCENDANT);
		addEEnumLiteral(hierarchyInclusionTypeEEnum, HierarchyInclusionType.SELF_OR_DESCENDANT);

		initEEnum(constraintStrengthEEnum, ConstraintStrength.class, "ConstraintStrength");
		addEEnumLiteral(constraintStrengthEEnum, ConstraintStrength.MANDATORY_CM);
		addEEnumLiteral(constraintStrengthEEnum, ConstraintStrength.RECOMMENDED_CM);
		addEEnumLiteral(constraintStrengthEEnum, ConstraintStrength.ADVISORY_CM);
		addEEnumLiteral(constraintStrengthEEnum, ConstraintStrength.MANDATORY_PC);
		addEEnumLiteral(constraintStrengthEEnum, ConstraintStrength.INFORMATION_MODEL_PC);
		addEEnumLiteral(constraintStrengthEEnum, ConstraintStrength.USE_CASE_SPECIFIC_PC);
		addEEnumLiteral(constraintStrengthEEnum, ConstraintStrength.IMPLEMENTATION_SPECIFIC_PC);

		initEEnum(constraintFormEEnum, ConstraintForm.class, "ConstraintForm");
		addEEnumLiteral(constraintFormEEnum, ConstraintForm.ALL_FORMS);
		addEEnumLiteral(constraintFormEEnum, ConstraintForm.DISTRIBUTION_FORM);
		addEEnumLiteral(constraintFormEEnum, ConstraintForm.STATED_FORM);
		addEEnumLiteral(constraintFormEEnum, ConstraintForm.CLOSE_TO_USER_FORM);
		addEEnumLiteral(constraintFormEEnum, ConstraintForm.LONG_NORMAL_FORM);
		addEEnumLiteral(constraintFormEEnum, ConstraintForm.SHORT_NORMAL_FORM);

		// Initialize data types
		initEDataType(dataTypeEDataType, DataType.class, "DataType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //MrcmPackageImpl
