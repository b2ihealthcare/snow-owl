/**
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
package com.b2international.snowowl.snomed.mrcm;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.mrcm.MrcmFactory
 * @model kind="package"
 * @generated
 */
public interface MrcmPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "mrcm";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://b2international.com/snowowl/snomed/mrcm";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "mrcm";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MrcmPackage eINSTANCE = com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelImpl <em>Concept Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptModelImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptModel()
	 * @generated
	 */
	int CONCEPT_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Constraints</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL__CONSTRAINTS = 0;

	/**
	 * The number of structural features of the '<em>Concept Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl <em>Concept Model Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptModelComponent()
	 * @generated
	 */
	int CONCEPT_MODEL_COMPONENT = 1;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_COMPONENT__UUID = 0;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_COMPONENT__ACTIVE = 1;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME = 2;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_COMPONENT__AUTHOR = 3;

	/**
	 * The number of structural features of the '<em>Concept Model Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_COMPONENT_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelPredicateImpl <em>Concept Model Predicate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptModelPredicateImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptModelPredicate()
	 * @generated
	 */
	int CONCEPT_MODEL_PREDICATE = 2;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_PREDICATE__UUID = CONCEPT_MODEL_COMPONENT__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_PREDICATE__ACTIVE = CONCEPT_MODEL_COMPONENT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_PREDICATE__EFFECTIVE_TIME = CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_PREDICATE__AUTHOR = CONCEPT_MODEL_COMPONENT__AUTHOR;

	/**
	 * The number of structural features of the '<em>Concept Model Predicate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_MODEL_PREDICATE_FEATURE_COUNT = CONCEPT_MODEL_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl <em>Cardinality Predicate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getCardinalityPredicate()
	 * @generated
	 */
	int CARDINALITY_PREDICATE = 3;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__UUID = CONCEPT_MODEL_PREDICATE__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__ACTIVE = CONCEPT_MODEL_PREDICATE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__EFFECTIVE_TIME = CONCEPT_MODEL_PREDICATE__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__AUTHOR = CONCEPT_MODEL_PREDICATE__AUTHOR;

	/**
	 * The feature id for the '<em><b>Min Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__MIN_CARDINALITY = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Max Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__MAX_CARDINALITY = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Group Rule</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__GROUP_RULE = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE__PREDICATE = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Cardinality Predicate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CARDINALITY_PREDICATE_FEATURE_COUNT = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl <em>Concrete Domain Element Predicate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConcreteDomainElementPredicate()
	 * @generated
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE = 4;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__UUID = CONCEPT_MODEL_PREDICATE__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__ACTIVE = CONCEPT_MODEL_PREDICATE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__EFFECTIVE_TIME = CONCEPT_MODEL_PREDICATE__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__AUTHOR = CONCEPT_MODEL_PREDICATE__AUTHOR;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__NAME = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__LABEL = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__TYPE = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Characteristic Type Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Concrete Domain Element Predicate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCRETE_DOMAIN_ELEMENT_PREDICATE_FEATURE_COUNT = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.DependencyPredicateImpl <em>Dependency Predicate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.DependencyPredicateImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDependencyPredicate()
	 * @generated
	 */
	int DEPENDENCY_PREDICATE = 5;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE__UUID = CONCEPT_MODEL_PREDICATE__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE__ACTIVE = CONCEPT_MODEL_PREDICATE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE__EFFECTIVE_TIME = CONCEPT_MODEL_PREDICATE__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE__AUTHOR = CONCEPT_MODEL_PREDICATE__AUTHOR;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE__CHILDREN = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Operator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE__OPERATOR = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Group Rule</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE__GROUP_RULE = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Dependency Predicate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_PREDICATE_FEATURE_COUNT = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.DescriptionPredicateImpl <em>Description Predicate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.DescriptionPredicateImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDescriptionPredicate()
	 * @generated
	 */
	int DESCRIPTION_PREDICATE = 6;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_PREDICATE__UUID = CONCEPT_MODEL_PREDICATE__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_PREDICATE__ACTIVE = CONCEPT_MODEL_PREDICATE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_PREDICATE__EFFECTIVE_TIME = CONCEPT_MODEL_PREDICATE__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_PREDICATE__AUTHOR = CONCEPT_MODEL_PREDICATE__AUTHOR;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_PREDICATE__TYPE_ID = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Description Predicate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_PREDICATE_FEATURE_COUNT = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.RelationshipPredicateImpl <em>Relationship Predicate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.RelationshipPredicateImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getRelationshipPredicate()
	 * @generated
	 */
	int RELATIONSHIP_PREDICATE = 7;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE__UUID = CONCEPT_MODEL_PREDICATE__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE__ACTIVE = CONCEPT_MODEL_PREDICATE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE__EFFECTIVE_TIME = CONCEPT_MODEL_PREDICATE__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE__AUTHOR = CONCEPT_MODEL_PREDICATE__AUTHOR;

	/**
	 * The feature id for the '<em><b>Attribute</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE__ATTRIBUTE = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Range</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE__RANGE = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Characteristic Type Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Relationship Predicate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_PREDICATE_FEATURE_COUNT = CONCEPT_MODEL_PREDICATE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptSetDefinitionImpl <em>Concept Set Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptSetDefinitionImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptSetDefinition()
	 * @generated
	 */
	int CONCEPT_SET_DEFINITION = 8;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_SET_DEFINITION__UUID = CONCEPT_MODEL_COMPONENT__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_SET_DEFINITION__ACTIVE = CONCEPT_MODEL_COMPONENT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_SET_DEFINITION__EFFECTIVE_TIME = CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_SET_DEFINITION__AUTHOR = CONCEPT_MODEL_COMPONENT__AUTHOR;

	/**
	 * The number of structural features of the '<em>Concept Set Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_SET_DEFINITION_FEATURE_COUNT = CONCEPT_MODEL_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.CompositeConceptSetDefinitionImpl <em>Composite Concept Set Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.CompositeConceptSetDefinitionImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getCompositeConceptSetDefinition()
	 * @generated
	 */
	int COMPOSITE_CONCEPT_SET_DEFINITION = 9;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_CONCEPT_SET_DEFINITION__UUID = CONCEPT_SET_DEFINITION__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_CONCEPT_SET_DEFINITION__ACTIVE = CONCEPT_SET_DEFINITION__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_CONCEPT_SET_DEFINITION__EFFECTIVE_TIME = CONCEPT_SET_DEFINITION__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_CONCEPT_SET_DEFINITION__AUTHOR = CONCEPT_SET_DEFINITION__AUTHOR;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_CONCEPT_SET_DEFINITION__CHILDREN = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Composite Concept Set Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPOSITE_CONCEPT_SET_DEFINITION_FEATURE_COUNT = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.EnumeratedConceptSetDefinitionImpl <em>Enumerated Concept Set Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.EnumeratedConceptSetDefinitionImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getEnumeratedConceptSetDefinition()
	 * @generated
	 */
	int ENUMERATED_CONCEPT_SET_DEFINITION = 10;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUMERATED_CONCEPT_SET_DEFINITION__UUID = CONCEPT_SET_DEFINITION__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUMERATED_CONCEPT_SET_DEFINITION__ACTIVE = CONCEPT_SET_DEFINITION__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUMERATED_CONCEPT_SET_DEFINITION__EFFECTIVE_TIME = CONCEPT_SET_DEFINITION__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUMERATED_CONCEPT_SET_DEFINITION__AUTHOR = CONCEPT_SET_DEFINITION__AUTHOR;

	/**
	 * The feature id for the '<em><b>Concept Ids</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUMERATED_CONCEPT_SET_DEFINITION__CONCEPT_IDS = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Enumerated Concept Set Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUMERATED_CONCEPT_SET_DEFINITION_FEATURE_COUNT = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.HierarchyConceptSetDefinitionImpl <em>Hierarchy Concept Set Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.HierarchyConceptSetDefinitionImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getHierarchyConceptSetDefinition()
	 * @generated
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION = 11;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION__UUID = CONCEPT_SET_DEFINITION__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION__ACTIVE = CONCEPT_SET_DEFINITION__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION__EFFECTIVE_TIME = CONCEPT_SET_DEFINITION__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION__AUTHOR = CONCEPT_SET_DEFINITION__AUTHOR;

	/**
	 * The feature id for the '<em><b>Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION__CONCEPT_ID = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Inclusion Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION__INCLUSION_TYPE = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Hierarchy Concept Set Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIERARCHY_CONCEPT_SET_DEFINITION_FEATURE_COUNT = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ReferenceSetConceptSetDefinitionImpl <em>Reference Set Concept Set Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.ReferenceSetConceptSetDefinitionImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getReferenceSetConceptSetDefinition()
	 * @generated
	 */
	int REFERENCE_SET_CONCEPT_SET_DEFINITION = 12;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SET_CONCEPT_SET_DEFINITION__UUID = CONCEPT_SET_DEFINITION__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SET_CONCEPT_SET_DEFINITION__ACTIVE = CONCEPT_SET_DEFINITION__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SET_CONCEPT_SET_DEFINITION__EFFECTIVE_TIME = CONCEPT_SET_DEFINITION__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SET_CONCEPT_SET_DEFINITION__AUTHOR = CONCEPT_SET_DEFINITION__AUTHOR;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SET_CONCEPT_SET_DEFINITION__REF_SET_IDENTIFIER_CONCEPT_ID = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Reference Set Concept Set Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SET_CONCEPT_SET_DEFINITION_FEATURE_COUNT = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.RelationshipConceptSetDefinitionImpl <em>Relationship Concept Set Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.RelationshipConceptSetDefinitionImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getRelationshipConceptSetDefinition()
	 * @generated
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION = 13;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION__UUID = CONCEPT_SET_DEFINITION__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION__ACTIVE = CONCEPT_SET_DEFINITION__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION__EFFECTIVE_TIME = CONCEPT_SET_DEFINITION__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION__AUTHOR = CONCEPT_SET_DEFINITION__AUTHOR;

	/**
	 * The feature id for the '<em><b>Type Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION__TYPE_CONCEPT_ID = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Destination Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION__DESTINATION_CONCEPT_ID = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Relationship Concept Set Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_CONCEPT_SET_DEFINITION_FEATURE_COUNT = CONCEPT_SET_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConstraintBaseImpl <em>Constraint Base</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.ConstraintBaseImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConstraintBase()
	 * @generated
	 */
	int CONSTRAINT_BASE = 14;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE__UUID = CONCEPT_MODEL_COMPONENT__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE__ACTIVE = CONCEPT_MODEL_COMPONENT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE__EFFECTIVE_TIME = CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE__AUTHOR = CONCEPT_MODEL_COMPONENT__AUTHOR;

	/**
	 * The feature id for the '<em><b>Strength</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE__STRENGTH = CONCEPT_MODEL_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Validation Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE__VALIDATION_MESSAGE = CONCEPT_MODEL_COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE__DESCRIPTION = CONCEPT_MODEL_COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Constraint Base</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_BASE_FEATURE_COUNT = CONCEPT_MODEL_COMPONENT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.impl.AttributeConstraintImpl <em>Attribute Constraint</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.impl.AttributeConstraintImpl
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getAttributeConstraint()
	 * @generated
	 */
	int ATTRIBUTE_CONSTRAINT = 15;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__UUID = CONSTRAINT_BASE__UUID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__ACTIVE = CONSTRAINT_BASE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__EFFECTIVE_TIME = CONSTRAINT_BASE__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__AUTHOR = CONSTRAINT_BASE__AUTHOR;

	/**
	 * The feature id for the '<em><b>Strength</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__STRENGTH = CONSTRAINT_BASE__STRENGTH;

	/**
	 * The feature id for the '<em><b>Validation Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__VALIDATION_MESSAGE = CONSTRAINT_BASE__VALIDATION_MESSAGE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__DESCRIPTION = CONSTRAINT_BASE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Form</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__FORM = CONSTRAINT_BASE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Domain</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__DOMAIN = CONSTRAINT_BASE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT__PREDICATE = CONSTRAINT_BASE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Attribute Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CONSTRAINT_FEATURE_COUNT = CONSTRAINT_BASE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.GroupRule <em>Group Rule</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.GroupRule
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getGroupRule()
	 * @generated
	 */
	int GROUP_RULE = 16;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.DependencyOperator <em>Dependency Operator</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyOperator
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDependencyOperator()
	 * @generated
	 */
	int DEPENDENCY_OPERATOR = 17;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType <em>Hierarchy Inclusion Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getHierarchyInclusionType()
	 * @generated
	 */
	int HIERARCHY_INCLUSION_TYPE = 18;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.ConstraintStrength <em>Constraint Strength</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintStrength
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConstraintStrength()
	 * @generated
	 */
	int CONSTRAINT_STRENGTH = 19;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.mrcm.ConstraintForm <em>Constraint Form</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintForm
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConstraintForm()
	 * @generated
	 */
	int CONSTRAINT_FORM = 20;

	/**
	 * The meta object id for the '<em>Data Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
	 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDataType()
	 * @generated
	 */
	int DATA_TYPE = 21;


	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.ConceptModel <em>Concept Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concept Model</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModel
	 * @generated
	 */
	EClass getConceptModel();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.mrcm.ConceptModel#getConstraints <em>Constraints</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Constraints</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModel#getConstraints()
	 * @see #getConceptModel()
	 * @generated
	 */
	EReference getConceptModel_Constraints();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent <em>Concept Model Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concept Model Component</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelComponent
	 * @generated
	 */
	EClass getConceptModelComponent();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getUuid <em>Uuid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uuid</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getUuid()
	 * @see #getConceptModelComponent()
	 * @generated
	 */
	EAttribute getConceptModelComponent_Uuid();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#isActive <em>Active</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Active</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#isActive()
	 * @see #getConceptModelComponent()
	 * @generated
	 */
	EAttribute getConceptModelComponent_Active();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getEffectiveTime <em>Effective Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Effective Time</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getEffectiveTime()
	 * @see #getConceptModelComponent()
	 * @generated
	 */
	EAttribute getConceptModelComponent_EffectiveTime();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getAuthor()
	 * @see #getConceptModelComponent()
	 * @generated
	 */
	EAttribute getConceptModelComponent_Author();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate <em>Concept Model Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concept Model Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate
	 * @generated
	 */
	EClass getConceptModelPredicate();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate <em>Cardinality Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cardinality Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.CardinalityPredicate
	 * @generated
	 */
	EClass getCardinalityPredicate();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMinCardinality <em>Min Cardinality</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Cardinality</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMinCardinality()
	 * @see #getCardinalityPredicate()
	 * @generated
	 */
	EAttribute getCardinalityPredicate_MinCardinality();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMaxCardinality <em>Max Cardinality</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Cardinality</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMaxCardinality()
	 * @see #getCardinalityPredicate()
	 * @generated
	 */
	EAttribute getCardinalityPredicate_MaxCardinality();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getGroupRule <em>Group Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Group Rule</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getGroupRule()
	 * @see #getCardinalityPredicate()
	 * @generated
	 */
	EAttribute getCardinalityPredicate_GroupRule();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getPredicate()
	 * @see #getCardinalityPredicate()
	 * @generated
	 */
	EReference getCardinalityPredicate_Predicate();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate <em>Concrete Domain Element Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concrete Domain Element Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate
	 * @generated
	 */
	EClass getConcreteDomainElementPredicate();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getName()
	 * @see #getConcreteDomainElementPredicate()
	 * @generated
	 */
	EAttribute getConcreteDomainElementPredicate_Name();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getLabel()
	 * @see #getConcreteDomainElementPredicate()
	 * @generated
	 */
	EAttribute getConcreteDomainElementPredicate_Label();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getType()
	 * @see #getConcreteDomainElementPredicate()
	 * @generated
	 */
	EAttribute getConcreteDomainElementPredicate_Type();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getCharacteristicTypeConceptId <em>Characteristic Type Concept Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Characteristic Type Concept Id</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getCharacteristicTypeConceptId()
	 * @see #getConcreteDomainElementPredicate()
	 * @generated
	 */
	EAttribute getConcreteDomainElementPredicate_CharacteristicTypeConceptId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate <em>Dependency Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dependency Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyPredicate
	 * @generated
	 */
	EClass getDependencyPredicate();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Children</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getChildren()
	 * @see #getDependencyPredicate()
	 * @generated
	 */
	EReference getDependencyPredicate_Children();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getOperator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operator</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getOperator()
	 * @see #getDependencyPredicate()
	 * @generated
	 */
	EAttribute getDependencyPredicate_Operator();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getGroupRule <em>Group Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Group Rule</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyPredicate#getGroupRule()
	 * @see #getDependencyPredicate()
	 * @generated
	 */
	EAttribute getDependencyPredicate_GroupRule();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.DescriptionPredicate <em>Description Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Description Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.DescriptionPredicate
	 * @generated
	 */
	EClass getDescriptionPredicate();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.DescriptionPredicate#getTypeId <em>Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Id</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.DescriptionPredicate#getTypeId()
	 * @see #getDescriptionPredicate()
	 * @generated
	 */
	EAttribute getDescriptionPredicate_TypeId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.RelationshipPredicate <em>Relationship Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Relationship Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipPredicate
	 * @generated
	 */
	EClass getRelationshipPredicate();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.mrcm.RelationshipPredicate#getAttribute <em>Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Attribute</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipPredicate#getAttribute()
	 * @see #getRelationshipPredicate()
	 * @generated
	 */
	EReference getRelationshipPredicate_Attribute();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.mrcm.RelationshipPredicate#getRange <em>Range</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Range</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipPredicate#getRange()
	 * @see #getRelationshipPredicate()
	 * @generated
	 */
	EReference getRelationshipPredicate_Range();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.RelationshipPredicate#getCharacteristicTypeConceptId <em>Characteristic Type Concept Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Characteristic Type Concept Id</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipPredicate#getCharacteristicTypeConceptId()
	 * @see #getRelationshipPredicate()
	 * @generated
	 */
	EAttribute getRelationshipPredicate_CharacteristicTypeConceptId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition <em>Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concept Set Definition</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition
	 * @generated
	 */
	EClass getConceptSetDefinition();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition <em>Composite Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Composite Concept Set Definition</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition
	 * @generated
	 */
	EClass getCompositeConceptSetDefinition();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Children</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition#getChildren()
	 * @see #getCompositeConceptSetDefinition()
	 * @generated
	 */
	EReference getCompositeConceptSetDefinition_Children();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition <em>Enumerated Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Enumerated Concept Set Definition</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition
	 * @generated
	 */
	EClass getEnumeratedConceptSetDefinition();

	/**
	 * Returns the meta object for the attribute list '{@link com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition#getConceptIds <em>Concept Ids</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Concept Ids</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition#getConceptIds()
	 * @see #getEnumeratedConceptSetDefinition()
	 * @generated
	 */
	EAttribute getEnumeratedConceptSetDefinition_ConceptIds();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition <em>Hierarchy Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Hierarchy Concept Set Definition</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition
	 * @generated
	 */
	EClass getHierarchyConceptSetDefinition();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getConceptId <em>Concept Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Concept Id</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getConceptId()
	 * @see #getHierarchyConceptSetDefinition()
	 * @generated
	 */
	EAttribute getHierarchyConceptSetDefinition_ConceptId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getInclusionType <em>Inclusion Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Inclusion Type</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition#getInclusionType()
	 * @see #getHierarchyConceptSetDefinition()
	 * @generated
	 */
	EAttribute getHierarchyConceptSetDefinition_InclusionType();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition <em>Reference Set Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reference Set Concept Set Definition</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition
	 * @generated
	 */
	EClass getReferenceSetConceptSetDefinition();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition#getRefSetIdentifierConceptId <em>Ref Set Identifier Concept Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref Set Identifier Concept Id</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition#getRefSetIdentifierConceptId()
	 * @see #getReferenceSetConceptSetDefinition()
	 * @generated
	 */
	EAttribute getReferenceSetConceptSetDefinition_RefSetIdentifierConceptId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition <em>Relationship Concept Set Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Relationship Concept Set Definition</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition
	 * @generated
	 */
	EClass getRelationshipConceptSetDefinition();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getTypeConceptId <em>Type Concept Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Concept Id</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getTypeConceptId()
	 * @see #getRelationshipConceptSetDefinition()
	 * @generated
	 */
	EAttribute getRelationshipConceptSetDefinition_TypeConceptId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getDestinationConceptId <em>Destination Concept Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Destination Concept Id</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getDestinationConceptId()
	 * @see #getRelationshipConceptSetDefinition()
	 * @generated
	 */
	EAttribute getRelationshipConceptSetDefinition_DestinationConceptId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase <em>Constraint Base</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Constraint Base</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintBase
	 * @generated
	 */
	EClass getConstraintBase();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getStrength <em>Strength</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strength</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintBase#getStrength()
	 * @see #getConstraintBase()
	 * @generated
	 */
	EAttribute getConstraintBase_Strength();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getValidationMessage <em>Validation Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Validation Message</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintBase#getValidationMessage()
	 * @see #getConstraintBase()
	 * @generated
	 */
	EAttribute getConstraintBase_ValidationMessage();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintBase#getDescription()
	 * @see #getConstraintBase()
	 * @generated
	 */
	EAttribute getConstraintBase_Description();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint <em>Attribute Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute Constraint</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.AttributeConstraint
	 * @generated
	 */
	EClass getAttributeConstraint();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getForm <em>Form</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Form</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getForm()
	 * @see #getAttributeConstraint()
	 * @generated
	 */
	EAttribute getAttributeConstraint_Form();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getDomain <em>Domain</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Domain</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getDomain()
	 * @see #getAttributeConstraint()
	 * @generated
	 */
	EReference getAttributeConstraint_Domain();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Predicate</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.AttributeConstraint#getPredicate()
	 * @see #getAttributeConstraint()
	 * @generated
	 */
	EReference getAttributeConstraint_Predicate();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.mrcm.GroupRule <em>Group Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Group Rule</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.GroupRule
	 * @generated
	 */
	EEnum getGroupRule();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.mrcm.DependencyOperator <em>Dependency Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Dependency Operator</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.DependencyOperator
	 * @generated
	 */
	EEnum getDependencyOperator();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType <em>Hierarchy Inclusion Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Hierarchy Inclusion Type</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType
	 * @generated
	 */
	EEnum getHierarchyInclusionType();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.mrcm.ConstraintStrength <em>Constraint Strength</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Constraint Strength</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintStrength
	 * @generated
	 */
	EEnum getConstraintStrength();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.mrcm.ConstraintForm <em>Constraint Form</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Constraint Form</em>'.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintForm
	 * @generated
	 */
	EEnum getConstraintForm();

	/**
	 * Returns the meta object for data type '{@link com.b2international.snowowl.snomed.snomedrefset.DataType <em>Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Data Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
	 * @model instanceClass="com.b2international.snowowl.snomed.snomedrefset.DataType"
	 * @generated
	 */
	EDataType getDataType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MrcmFactory getMrcmFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelImpl <em>Concept Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptModelImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptModel()
		 * @generated
		 */
		EClass CONCEPT_MODEL = eINSTANCE.getConceptModel();

		/**
		 * The meta object literal for the '<em><b>Constraints</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONCEPT_MODEL__CONSTRAINTS = eINSTANCE.getConceptModel_Constraints();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl <em>Concept Model Component</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptModelComponentImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptModelComponent()
		 * @generated
		 */
		EClass CONCEPT_MODEL_COMPONENT = eINSTANCE.getConceptModelComponent();

		/**
		 * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT_MODEL_COMPONENT__UUID = eINSTANCE.getConceptModelComponent_Uuid();

		/**
		 * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT_MODEL_COMPONENT__ACTIVE = eINSTANCE.getConceptModelComponent_Active();

		/**
		 * The meta object literal for the '<em><b>Effective Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT_MODEL_COMPONENT__EFFECTIVE_TIME = eINSTANCE.getConceptModelComponent_EffectiveTime();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT_MODEL_COMPONENT__AUTHOR = eINSTANCE.getConceptModelComponent_Author();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptModelPredicateImpl <em>Concept Model Predicate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptModelPredicateImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptModelPredicate()
		 * @generated
		 */
		EClass CONCEPT_MODEL_PREDICATE = eINSTANCE.getConceptModelPredicate();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl <em>Cardinality Predicate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getCardinalityPredicate()
		 * @generated
		 */
		EClass CARDINALITY_PREDICATE = eINSTANCE.getCardinalityPredicate();

		/**
		 * The meta object literal for the '<em><b>Min Cardinality</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CARDINALITY_PREDICATE__MIN_CARDINALITY = eINSTANCE.getCardinalityPredicate_MinCardinality();

		/**
		 * The meta object literal for the '<em><b>Max Cardinality</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CARDINALITY_PREDICATE__MAX_CARDINALITY = eINSTANCE.getCardinalityPredicate_MaxCardinality();

		/**
		 * The meta object literal for the '<em><b>Group Rule</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CARDINALITY_PREDICATE__GROUP_RULE = eINSTANCE.getCardinalityPredicate_GroupRule();

		/**
		 * The meta object literal for the '<em><b>Predicate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CARDINALITY_PREDICATE__PREDICATE = eINSTANCE.getCardinalityPredicate_Predicate();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl <em>Concrete Domain Element Predicate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConcreteDomainElementPredicate()
		 * @generated
		 */
		EClass CONCRETE_DOMAIN_ELEMENT_PREDICATE = eINSTANCE.getConcreteDomainElementPredicate();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCRETE_DOMAIN_ELEMENT_PREDICATE__NAME = eINSTANCE.getConcreteDomainElementPredicate_Name();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCRETE_DOMAIN_ELEMENT_PREDICATE__LABEL = eINSTANCE.getConcreteDomainElementPredicate_Label();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCRETE_DOMAIN_ELEMENT_PREDICATE__TYPE = eINSTANCE.getConcreteDomainElementPredicate_Type();

		/**
		 * The meta object literal for the '<em><b>Characteristic Type Concept Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCRETE_DOMAIN_ELEMENT_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID = eINSTANCE.getConcreteDomainElementPredicate_CharacteristicTypeConceptId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.DependencyPredicateImpl <em>Dependency Predicate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.DependencyPredicateImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDependencyPredicate()
		 * @generated
		 */
		EClass DEPENDENCY_PREDICATE = eINSTANCE.getDependencyPredicate();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPENDENCY_PREDICATE__CHILDREN = eINSTANCE.getDependencyPredicate_Children();

		/**
		 * The meta object literal for the '<em><b>Operator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPENDENCY_PREDICATE__OPERATOR = eINSTANCE.getDependencyPredicate_Operator();

		/**
		 * The meta object literal for the '<em><b>Group Rule</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPENDENCY_PREDICATE__GROUP_RULE = eINSTANCE.getDependencyPredicate_GroupRule();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.DescriptionPredicateImpl <em>Description Predicate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.DescriptionPredicateImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDescriptionPredicate()
		 * @generated
		 */
		EClass DESCRIPTION_PREDICATE = eINSTANCE.getDescriptionPredicate();

		/**
		 * The meta object literal for the '<em><b>Type Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DESCRIPTION_PREDICATE__TYPE_ID = eINSTANCE.getDescriptionPredicate_TypeId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.RelationshipPredicateImpl <em>Relationship Predicate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.RelationshipPredicateImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getRelationshipPredicate()
		 * @generated
		 */
		EClass RELATIONSHIP_PREDICATE = eINSTANCE.getRelationshipPredicate();

		/**
		 * The meta object literal for the '<em><b>Attribute</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP_PREDICATE__ATTRIBUTE = eINSTANCE.getRelationshipPredicate_Attribute();

		/**
		 * The meta object literal for the '<em><b>Range</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP_PREDICATE__RANGE = eINSTANCE.getRelationshipPredicate_Range();

		/**
		 * The meta object literal for the '<em><b>Characteristic Type Concept Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RELATIONSHIP_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID = eINSTANCE.getRelationshipPredicate_CharacteristicTypeConceptId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConceptSetDefinitionImpl <em>Concept Set Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.ConceptSetDefinitionImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConceptSetDefinition()
		 * @generated
		 */
		EClass CONCEPT_SET_DEFINITION = eINSTANCE.getConceptSetDefinition();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.CompositeConceptSetDefinitionImpl <em>Composite Concept Set Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.CompositeConceptSetDefinitionImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getCompositeConceptSetDefinition()
		 * @generated
		 */
		EClass COMPOSITE_CONCEPT_SET_DEFINITION = eINSTANCE.getCompositeConceptSetDefinition();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPOSITE_CONCEPT_SET_DEFINITION__CHILDREN = eINSTANCE.getCompositeConceptSetDefinition_Children();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.EnumeratedConceptSetDefinitionImpl <em>Enumerated Concept Set Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.EnumeratedConceptSetDefinitionImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getEnumeratedConceptSetDefinition()
		 * @generated
		 */
		EClass ENUMERATED_CONCEPT_SET_DEFINITION = eINSTANCE.getEnumeratedConceptSetDefinition();

		/**
		 * The meta object literal for the '<em><b>Concept Ids</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENUMERATED_CONCEPT_SET_DEFINITION__CONCEPT_IDS = eINSTANCE.getEnumeratedConceptSetDefinition_ConceptIds();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.HierarchyConceptSetDefinitionImpl <em>Hierarchy Concept Set Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.HierarchyConceptSetDefinitionImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getHierarchyConceptSetDefinition()
		 * @generated
		 */
		EClass HIERARCHY_CONCEPT_SET_DEFINITION = eINSTANCE.getHierarchyConceptSetDefinition();

		/**
		 * The meta object literal for the '<em><b>Concept Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HIERARCHY_CONCEPT_SET_DEFINITION__CONCEPT_ID = eINSTANCE.getHierarchyConceptSetDefinition_ConceptId();

		/**
		 * The meta object literal for the '<em><b>Inclusion Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HIERARCHY_CONCEPT_SET_DEFINITION__INCLUSION_TYPE = eINSTANCE.getHierarchyConceptSetDefinition_InclusionType();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ReferenceSetConceptSetDefinitionImpl <em>Reference Set Concept Set Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.ReferenceSetConceptSetDefinitionImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getReferenceSetConceptSetDefinition()
		 * @generated
		 */
		EClass REFERENCE_SET_CONCEPT_SET_DEFINITION = eINSTANCE.getReferenceSetConceptSetDefinition();

		/**
		 * The meta object literal for the '<em><b>Ref Set Identifier Concept Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_SET_CONCEPT_SET_DEFINITION__REF_SET_IDENTIFIER_CONCEPT_ID = eINSTANCE.getReferenceSetConceptSetDefinition_RefSetIdentifierConceptId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.RelationshipConceptSetDefinitionImpl <em>Relationship Concept Set Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.RelationshipConceptSetDefinitionImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getRelationshipConceptSetDefinition()
		 * @generated
		 */
		EClass RELATIONSHIP_CONCEPT_SET_DEFINITION = eINSTANCE.getRelationshipConceptSetDefinition();

		/**
		 * The meta object literal for the '<em><b>Type Concept Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RELATIONSHIP_CONCEPT_SET_DEFINITION__TYPE_CONCEPT_ID = eINSTANCE.getRelationshipConceptSetDefinition_TypeConceptId();

		/**
		 * The meta object literal for the '<em><b>Destination Concept Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RELATIONSHIP_CONCEPT_SET_DEFINITION__DESTINATION_CONCEPT_ID = eINSTANCE.getRelationshipConceptSetDefinition_DestinationConceptId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.ConstraintBaseImpl <em>Constraint Base</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.ConstraintBaseImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConstraintBase()
		 * @generated
		 */
		EClass CONSTRAINT_BASE = eINSTANCE.getConstraintBase();

		/**
		 * The meta object literal for the '<em><b>Strength</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONSTRAINT_BASE__STRENGTH = eINSTANCE.getConstraintBase_Strength();

		/**
		 * The meta object literal for the '<em><b>Validation Message</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONSTRAINT_BASE__VALIDATION_MESSAGE = eINSTANCE.getConstraintBase_ValidationMessage();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONSTRAINT_BASE__DESCRIPTION = eINSTANCE.getConstraintBase_Description();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.impl.AttributeConstraintImpl <em>Attribute Constraint</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.impl.AttributeConstraintImpl
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getAttributeConstraint()
		 * @generated
		 */
		EClass ATTRIBUTE_CONSTRAINT = eINSTANCE.getAttributeConstraint();

		/**
		 * The meta object literal for the '<em><b>Form</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE_CONSTRAINT__FORM = eINSTANCE.getAttributeConstraint_Form();

		/**
		 * The meta object literal for the '<em><b>Domain</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE_CONSTRAINT__DOMAIN = eINSTANCE.getAttributeConstraint_Domain();

		/**
		 * The meta object literal for the '<em><b>Predicate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE_CONSTRAINT__PREDICATE = eINSTANCE.getAttributeConstraint_Predicate();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.GroupRule <em>Group Rule</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.GroupRule
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getGroupRule()
		 * @generated
		 */
		EEnum GROUP_RULE = eINSTANCE.getGroupRule();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.DependencyOperator <em>Dependency Operator</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.DependencyOperator
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDependencyOperator()
		 * @generated
		 */
		EEnum DEPENDENCY_OPERATOR = eINSTANCE.getDependencyOperator();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType <em>Hierarchy Inclusion Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getHierarchyInclusionType()
		 * @generated
		 */
		EEnum HIERARCHY_INCLUSION_TYPE = eINSTANCE.getHierarchyInclusionType();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.ConstraintStrength <em>Constraint Strength</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.ConstraintStrength
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConstraintStrength()
		 * @generated
		 */
		EEnum CONSTRAINT_STRENGTH = eINSTANCE.getConstraintStrength();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.mrcm.ConstraintForm <em>Constraint Form</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.mrcm.ConstraintForm
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getConstraintForm()
		 * @generated
		 */
		EEnum CONSTRAINT_FORM = eINSTANCE.getConstraintForm();

		/**
		 * The meta object literal for the '<em>Data Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
		 * @see com.b2international.snowowl.snomed.mrcm.impl.MrcmPackageImpl#getDataType()
		 * @generated
		 */
		EDataType DATA_TYPE = eINSTANCE.getDataType();

	}

} //MrcmPackage
