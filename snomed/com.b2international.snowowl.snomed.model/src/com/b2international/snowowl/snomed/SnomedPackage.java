/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see com.b2international.snowowl.snomed.SnomedFactory
 * @model kind="package"
 * @generated
 */
public interface SnomedPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "snomed";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://b2international.com/snowowl/sct/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "sct";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SnomedPackage eINSTANCE = com.b2international.snowowl.snomed.impl.SnomedPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.impl.ComponentImpl <em>Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.impl.ComponentImpl
	 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getComponent()
	 * @generated
	 */
	int COMPONENT = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT__ID = 0;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT__ACTIVE = 1;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT__EFFECTIVE_TIME = 2;

	/**
	 * The feature id for the '<em><b>Module</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT__MODULE = 3;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT__RELEASED = 4;

	/**
	 * The number of structural features of the '<em>Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.impl.InactivatableImpl <em>Inactivatable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.impl.InactivatableImpl
	 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getInactivatable()
	 * @generated
	 */
	int INACTIVATABLE = 1;

	/**
	 * The feature id for the '<em><b>Inactivation Indicator Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS = 0;

	/**
	 * The feature id for the '<em><b>Association Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS = 1;

	/**
	 * The number of structural features of the '<em>Inactivatable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INACTIVATABLE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.impl.AnnotatableImpl <em>Annotatable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.impl.AnnotatableImpl
	 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getAnnotatable()
	 * @generated
	 */
	int ANNOTATABLE = 2;

	/**
	 * The feature id for the '<em><b>Concrete Domain Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATABLE__CONCRETE_DOMAIN_REF_SET_MEMBERS = 0;

	/**
	 * The number of structural features of the '<em>Annotatable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATABLE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.impl.DescriptionImpl <em>Description</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.impl.DescriptionImpl
	 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getDescription()
	 * @generated
	 */
	int DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__ID = COMPONENT__ID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__ACTIVE = COMPONENT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__EFFECTIVE_TIME = COMPONENT__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Module</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__MODULE = COMPONENT__MODULE;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__RELEASED = COMPONENT__RELEASED;

	/**
	 * The feature id for the '<em><b>Inactivation Indicator Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__INACTIVATION_INDICATOR_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Association Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__ASSOCIATION_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Language Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__LANGUAGE_CODE = COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Term</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__TERM = COMPONENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Concept</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__CONCEPT = COMPONENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__TYPE = COMPONENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Case Significance</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__CASE_SIGNIFICANCE = COMPONENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Language Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__LANGUAGE_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>Description</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_FEATURE_COUNT = COMPONENT_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.impl.ConceptImpl <em>Concept</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.impl.ConceptImpl
	 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getConcept()
	 * @generated
	 */
	int CONCEPT = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__ID = COMPONENT__ID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__ACTIVE = COMPONENT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__EFFECTIVE_TIME = COMPONENT__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Module</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__MODULE = COMPONENT__MODULE;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__RELEASED = COMPONENT__RELEASED;

	/**
	 * The feature id for the '<em><b>Inactivation Indicator Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__INACTIVATION_INDICATOR_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Association Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__ASSOCIATION_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Concrete Domain Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__CONCRETE_DOMAIN_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Definition Status</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__DEFINITION_STATUS = COMPONENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Outbound Relationships</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__OUTBOUND_RELATIONSHIPS = COMPONENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Descriptions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__DESCRIPTIONS = COMPONENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Exhaustive</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__EXHAUSTIVE = COMPONENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Fully Specified Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__FULLY_SPECIFIED_NAME = COMPONENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Primitive</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT__PRIMITIVE = COMPONENT_FEATURE_COUNT + 8;

	/**
	 * The number of structural features of the '<em>Concept</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_FEATURE_COUNT = COMPONENT_FEATURE_COUNT + 9;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.impl.RelationshipImpl <em>Relationship</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.impl.RelationshipImpl
	 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getRelationship()
	 * @generated
	 */
	int RELATIONSHIP = 5;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__ID = COMPONENT__ID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__ACTIVE = COMPONENT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__EFFECTIVE_TIME = COMPONENT__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Module</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__MODULE = COMPONENT__MODULE;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__RELEASED = COMPONENT__RELEASED;

	/**
	 * The feature id for the '<em><b>Concrete Domain Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__CONCRETE_DOMAIN_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__GROUP = COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Union Group</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__UNION_GROUP = COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Destination Negated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__DESTINATION_NEGATED = COMPONENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Source</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__SOURCE = COMPONENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Destination</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__DESTINATION = COMPONENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__TYPE = COMPONENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Characteristic Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__CHARACTERISTIC_TYPE = COMPONENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Modifier</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__MODIFIER = COMPONENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Refinability Ref Set Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP__REFINABILITY_REF_SET_MEMBERS = COMPONENT_FEATURE_COUNT + 9;

	/**
	 * The number of structural features of the '<em>Relationship</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATIONSHIP_FEATURE_COUNT = COMPONENT_FEATURE_COUNT + 10;


	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.impl.ConceptsImpl <em>Concepts</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.impl.ConceptsImpl
	 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getConcepts()
	 * @generated
	 */
	int CONCEPTS = 6;

	/**
	 * The feature id for the '<em><b>Concepts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPTS__CONCEPTS = 0;

	/**
	 * The number of structural features of the '<em>Concepts</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPTS_FEATURE_COUNT = 1;


	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.Component <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component</em>'.
	 * @see com.b2international.snowowl.snomed.Component
	 * @generated
	 */
	EClass getComponent();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Component#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see com.b2international.snowowl.snomed.Component#getId()
	 * @see #getComponent()
	 * @generated
	 */
	EAttribute getComponent_Id();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Component#isActive <em>Active</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Active</em>'.
	 * @see com.b2international.snowowl.snomed.Component#isActive()
	 * @see #getComponent()
	 * @generated
	 */
	EAttribute getComponent_Active();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Component#getEffectiveTime <em>Effective Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Effective Time</em>'.
	 * @see com.b2international.snowowl.snomed.Component#getEffectiveTime()
	 * @see #getComponent()
	 * @generated
	 */
	EAttribute getComponent_EffectiveTime();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Component#getModule <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Module</em>'.
	 * @see com.b2international.snowowl.snomed.Component#getModule()
	 * @see #getComponent()
	 * @generated
	 */
	EReference getComponent_Module();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Component#isReleased <em>Released</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Released</em>'.
	 * @see com.b2international.snowowl.snomed.Component#isReleased()
	 * @see #getComponent()
	 * @generated
	 */
	EAttribute getComponent_Released();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.Inactivatable <em>Inactivatable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Inactivatable</em>'.
	 * @see com.b2international.snowowl.snomed.Inactivatable
	 * @generated
	 */
	EClass getInactivatable();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Inactivatable#getInactivationIndicatorRefSetMembers <em>Inactivation Indicator Ref Set Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Inactivation Indicator Ref Set Members</em>'.
	 * @see com.b2international.snowowl.snomed.Inactivatable#getInactivationIndicatorRefSetMembers()
	 * @see #getInactivatable()
	 * @generated
	 */
	EReference getInactivatable_InactivationIndicatorRefSetMembers();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Inactivatable#getAssociationRefSetMembers <em>Association Ref Set Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Association Ref Set Members</em>'.
	 * @see com.b2international.snowowl.snomed.Inactivatable#getAssociationRefSetMembers()
	 * @see #getInactivatable()
	 * @generated
	 */
	EReference getInactivatable_AssociationRefSetMembers();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.Annotatable <em>Annotatable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Annotatable</em>'.
	 * @see com.b2international.snowowl.snomed.Annotatable
	 * @generated
	 */
	EClass getAnnotatable();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Annotatable#getConcreteDomainRefSetMembers <em>Concrete Domain Ref Set Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Concrete Domain Ref Set Members</em>'.
	 * @see com.b2international.snowowl.snomed.Annotatable#getConcreteDomainRefSetMembers()
	 * @see #getAnnotatable()
	 * @generated
	 */
	EReference getAnnotatable_ConcreteDomainRefSetMembers();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.Description <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Description</em>'.
	 * @see com.b2international.snowowl.snomed.Description
	 * @generated
	 */
	EClass getDescription();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Description#getLanguageCode <em>Language Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Language Code</em>'.
	 * @see com.b2international.snowowl.snomed.Description#getLanguageCode()
	 * @see #getDescription()
	 * @generated
	 */
	EAttribute getDescription_LanguageCode();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Description#getTerm <em>Term</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Term</em>'.
	 * @see com.b2international.snowowl.snomed.Description#getTerm()
	 * @see #getDescription()
	 * @generated
	 */
	EAttribute getDescription_Term();

	/**
	 * Returns the meta object for the container reference '{@link com.b2international.snowowl.snomed.Description#getConcept <em>Concept</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Concept</em>'.
	 * @see com.b2international.snowowl.snomed.Description#getConcept()
	 * @see #getDescription()
	 * @generated
	 */
	EReference getDescription_Concept();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Description#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Type</em>'.
	 * @see com.b2international.snowowl.snomed.Description#getType()
	 * @see #getDescription()
	 * @generated
	 */
	EReference getDescription_Type();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Description#getCaseSignificance <em>Case Significance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Case Significance</em>'.
	 * @see com.b2international.snowowl.snomed.Description#getCaseSignificance()
	 * @see #getDescription()
	 * @generated
	 */
	EReference getDescription_CaseSignificance();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Description#getLanguageRefSetMembers <em>Language Ref Set Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Language Ref Set Members</em>'.
	 * @see com.b2international.snowowl.snomed.Description#getLanguageRefSetMembers()
	 * @see #getDescription()
	 * @generated
	 */
	EReference getDescription_LanguageRefSetMembers();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.Concept <em>Concept</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concept</em>'.
	 * @see com.b2international.snowowl.snomed.Concept
	 * @generated
	 */
	EClass getConcept();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Concept#getDefinitionStatus <em>Definition Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Definition Status</em>'.
	 * @see com.b2international.snowowl.snomed.Concept#getDefinitionStatus()
	 * @see #getConcept()
	 * @generated
	 */
	EReference getConcept_DefinitionStatus();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Concept#getOutboundRelationships <em>Outbound Relationships</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Outbound Relationships</em>'.
	 * @see com.b2international.snowowl.snomed.Concept#getOutboundRelationships()
	 * @see #getConcept()
	 * @generated
	 */
	EReference getConcept_OutboundRelationships();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Concept#getDescriptions <em>Descriptions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Descriptions</em>'.
	 * @see com.b2international.snowowl.snomed.Concept#getDescriptions()
	 * @see #getConcept()
	 * @generated
	 */
	EReference getConcept_Descriptions();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Concept#isExhaustive <em>Exhaustive</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exhaustive</em>'.
	 * @see com.b2international.snowowl.snomed.Concept#isExhaustive()
	 * @see #getConcept()
	 * @generated
	 */
	EAttribute getConcept_Exhaustive();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Concept#getFullySpecifiedName <em>Fully Specified Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fully Specified Name</em>'.
	 * @see com.b2international.snowowl.snomed.Concept#getFullySpecifiedName()
	 * @see #getConcept()
	 * @generated
	 */
	EAttribute getConcept_FullySpecifiedName();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Concept#isPrimitive <em>Primitive</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Primitive</em>'.
	 * @see com.b2international.snowowl.snomed.Concept#isPrimitive()
	 * @see #getConcept()
	 * @generated
	 */
	EAttribute getConcept_Primitive();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.Relationship <em>Relationship</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Relationship</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship
	 * @generated
	 */
	EClass getRelationship();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Relationship#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Group</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getGroup()
	 * @see #getRelationship()
	 * @generated
	 */
	EAttribute getRelationship_Group();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Relationship#getUnionGroup <em>Union Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Union Group</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getUnionGroup()
	 * @see #getRelationship()
	 * @generated
	 */
	EAttribute getRelationship_UnionGroup();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.Relationship#isDestinationNegated <em>Destination Negated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Destination Negated</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#isDestinationNegated()
	 * @see #getRelationship()
	 * @generated
	 */
	EAttribute getRelationship_DestinationNegated();

	/**
	 * Returns the meta object for the container reference '{@link com.b2international.snowowl.snomed.Relationship#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Source</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getSource()
	 * @see #getRelationship()
	 * @generated
	 */
	EReference getRelationship_Source();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Relationship#getDestination <em>Destination</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Destination</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getDestination()
	 * @see #getRelationship()
	 * @generated
	 */
	EReference getRelationship_Destination();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Relationship#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Type</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getType()
	 * @see #getRelationship()
	 * @generated
	 */
	EReference getRelationship_Type();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Relationship#getCharacteristicType <em>Characteristic Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Characteristic Type</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getCharacteristicType()
	 * @see #getRelationship()
	 * @generated
	 */
	EReference getRelationship_CharacteristicType();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.Relationship#getModifier <em>Modifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Modifier</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getModifier()
	 * @see #getRelationship()
	 * @generated
	 */
	EReference getRelationship_Modifier();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Relationship#getRefinabilityRefSetMembers <em>Refinability Ref Set Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Refinability Ref Set Members</em>'.
	 * @see com.b2international.snowowl.snomed.Relationship#getRefinabilityRefSetMembers()
	 * @see #getRelationship()
	 * @generated
	 */
	EReference getRelationship_RefinabilityRefSetMembers();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.Concepts <em>Concepts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concepts</em>'.
	 * @see com.b2international.snowowl.snomed.Concepts
	 * @generated
	 */
	EClass getConcepts();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.Concepts#getConcepts <em>Concepts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Concepts</em>'.
	 * @see com.b2international.snowowl.snomed.Concepts#getConcepts()
	 * @see #getConcepts()
	 * @generated
	 */
	EReference getConcepts_Concepts();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SnomedFactory getSnomedFactory();

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
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.impl.ComponentImpl <em>Component</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.impl.ComponentImpl
		 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getComponent()
		 * @generated
		 */
		EClass COMPONENT = eINSTANCE.getComponent();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENT__ID = eINSTANCE.getComponent_Id();

		/**
		 * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENT__ACTIVE = eINSTANCE.getComponent_Active();

		/**
		 * The meta object literal for the '<em><b>Effective Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENT__EFFECTIVE_TIME = eINSTANCE.getComponent_EffectiveTime();

		/**
		 * The meta object literal for the '<em><b>Module</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENT__MODULE = eINSTANCE.getComponent_Module();

		/**
		 * The meta object literal for the '<em><b>Released</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENT__RELEASED = eINSTANCE.getComponent_Released();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.impl.InactivatableImpl <em>Inactivatable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.impl.InactivatableImpl
		 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getInactivatable()
		 * @generated
		 */
		EClass INACTIVATABLE = eINSTANCE.getInactivatable();

		/**
		 * The meta object literal for the '<em><b>Inactivation Indicator Ref Set Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS = eINSTANCE.getInactivatable_InactivationIndicatorRefSetMembers();

		/**
		 * The meta object literal for the '<em><b>Association Ref Set Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS = eINSTANCE.getInactivatable_AssociationRefSetMembers();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.impl.AnnotatableImpl <em>Annotatable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.impl.AnnotatableImpl
		 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getAnnotatable()
		 * @generated
		 */
		EClass ANNOTATABLE = eINSTANCE.getAnnotatable();

		/**
		 * The meta object literal for the '<em><b>Concrete Domain Ref Set Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATABLE__CONCRETE_DOMAIN_REF_SET_MEMBERS = eINSTANCE.getAnnotatable_ConcreteDomainRefSetMembers();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.impl.DescriptionImpl <em>Description</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.impl.DescriptionImpl
		 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getDescription()
		 * @generated
		 */
		EClass DESCRIPTION = eINSTANCE.getDescription();

		/**
		 * The meta object literal for the '<em><b>Language Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DESCRIPTION__LANGUAGE_CODE = eINSTANCE.getDescription_LanguageCode();

		/**
		 * The meta object literal for the '<em><b>Term</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DESCRIPTION__TERM = eINSTANCE.getDescription_Term();

		/**
		 * The meta object literal for the '<em><b>Concept</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESCRIPTION__CONCEPT = eINSTANCE.getDescription_Concept();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESCRIPTION__TYPE = eINSTANCE.getDescription_Type();

		/**
		 * The meta object literal for the '<em><b>Case Significance</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESCRIPTION__CASE_SIGNIFICANCE = eINSTANCE.getDescription_CaseSignificance();

		/**
		 * The meta object literal for the '<em><b>Language Ref Set Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESCRIPTION__LANGUAGE_REF_SET_MEMBERS = eINSTANCE.getDescription_LanguageRefSetMembers();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.impl.ConceptImpl <em>Concept</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.impl.ConceptImpl
		 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getConcept()
		 * @generated
		 */
		EClass CONCEPT = eINSTANCE.getConcept();

		/**
		 * The meta object literal for the '<em><b>Definition Status</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONCEPT__DEFINITION_STATUS = eINSTANCE.getConcept_DefinitionStatus();

		/**
		 * The meta object literal for the '<em><b>Outbound Relationships</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONCEPT__OUTBOUND_RELATIONSHIPS = eINSTANCE.getConcept_OutboundRelationships();

		/**
		 * The meta object literal for the '<em><b>Descriptions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONCEPT__DESCRIPTIONS = eINSTANCE.getConcept_Descriptions();

		/**
		 * The meta object literal for the '<em><b>Exhaustive</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT__EXHAUSTIVE = eINSTANCE.getConcept_Exhaustive();

		/**
		 * The meta object literal for the '<em><b>Fully Specified Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT__FULLY_SPECIFIED_NAME = eINSTANCE.getConcept_FullySpecifiedName();

		/**
		 * The meta object literal for the '<em><b>Primitive</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT__PRIMITIVE = eINSTANCE.getConcept_Primitive();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.impl.RelationshipImpl <em>Relationship</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.impl.RelationshipImpl
		 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getRelationship()
		 * @generated
		 */
		EClass RELATIONSHIP = eINSTANCE.getRelationship();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RELATIONSHIP__GROUP = eINSTANCE.getRelationship_Group();

		/**
		 * The meta object literal for the '<em><b>Union Group</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RELATIONSHIP__UNION_GROUP = eINSTANCE.getRelationship_UnionGroup();

		/**
		 * The meta object literal for the '<em><b>Destination Negated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RELATIONSHIP__DESTINATION_NEGATED = eINSTANCE.getRelationship_DestinationNegated();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP__SOURCE = eINSTANCE.getRelationship_Source();

		/**
		 * The meta object literal for the '<em><b>Destination</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP__DESTINATION = eINSTANCE.getRelationship_Destination();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP__TYPE = eINSTANCE.getRelationship_Type();

		/**
		 * The meta object literal for the '<em><b>Characteristic Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP__CHARACTERISTIC_TYPE = eINSTANCE.getRelationship_CharacteristicType();

		/**
		 * The meta object literal for the '<em><b>Modifier</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP__MODIFIER = eINSTANCE.getRelationship_Modifier();

		/**
		 * The meta object literal for the '<em><b>Refinability Ref Set Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RELATIONSHIP__REFINABILITY_REF_SET_MEMBERS = eINSTANCE.getRelationship_RefinabilityRefSetMembers();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.impl.ConceptsImpl <em>Concepts</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.impl.ConceptsImpl
		 * @see com.b2international.snowowl.snomed.impl.SnomedPackageImpl#getConcepts()
		 * @generated
		 */
		EClass CONCEPTS = eINSTANCE.getConcepts();

		/**
		 * The meta object literal for the '<em><b>Concepts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONCEPTS__CONCEPTS = eINSTANCE.getConcepts_Concepts();

	}

} //SnomedPackage