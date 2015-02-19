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
package com.b2international.snowowl.snomed.dsl.query.queryast.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import com.b2international.snowowl.snomed.dsl.query.queryast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.RefSet;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier;
import com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ecoreastPackageImpl extends EPackageImpl implements ecoreastPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass unaryRValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass binaryRValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conceptRefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass refSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass notClauseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subExpressionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass andClauseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass orClauseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass attributeClauseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass attributeClauseGroupEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass numericDataClauseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass numericDataGroupClauseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum subsumptionQuantifierEEnum = null;

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
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ecoreastPackageImpl() {
		super(eNS_URI, ecoreastFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ecoreastPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ecoreastPackage init() {
		if (isInited) return (ecoreastPackage)EPackage.Registry.INSTANCE.getEPackage(ecoreastPackage.eNS_URI);

		// Obtain or create and register package
		ecoreastPackageImpl theecoreastPackage = (ecoreastPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ecoreastPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ecoreastPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theecoreastPackage.createPackageContents();

		// Initialize created meta-data
		theecoreastPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theecoreastPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ecoreastPackage.eNS_URI, theecoreastPackage);
		return theecoreastPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRValue() {
		return rValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUnaryRValue() {
		return unaryRValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUnaryRValue_Value() {
		return (EReference)unaryRValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBinaryRValue() {
		return binaryRValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBinaryRValue_Left() {
		return (EReference)binaryRValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBinaryRValue_Right() {
		return (EReference)binaryRValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConceptRef() {
		return conceptRefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConceptRef_Quantifier() {
		return (EAttribute)conceptRefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConceptRef_ConceptId() {
		return (EAttribute)conceptRefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConceptRef_Label() {
		return (EAttribute)conceptRefEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRefSet() {
		return refSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRefSet_Id() {
		return (EAttribute)refSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNotClause() {
		return notClauseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSubExpression() {
		return subExpressionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAndClause() {
		return andClauseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getOrClause() {
		return orClauseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAttributeClause() {
		return attributeClauseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAttributeClauseGroup() {
		return attributeClauseGroupEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNumericDataClause() {
		return numericDataClauseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNumericDataClause_Operator() {
		return (EAttribute)numericDataClauseEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNumericDataClause_Value() {
		return (EAttribute)numericDataClauseEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNumericDataClause_UnitType() {
		return (EAttribute)numericDataClauseEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNumericDataGroupClause() {
		return numericDataGroupClauseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNumericDataGroupClause_Concepts() {
		return (EReference)numericDataGroupClauseEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNumericDataGroupClause_NumericData() {
		return (EReference)numericDataGroupClauseEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNumericDataGroupClause_Substance() {
		return (EReference)numericDataGroupClauseEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNumericDataClause_Concepts() {
		return (EReference)numericDataClauseEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getSubsumptionQuantifier() {
		return subsumptionQuantifierEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ecoreastFactory getecoreastFactory() {
		return (ecoreastFactory)getEFactoryInstance();
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
		rValueEClass = createEClass(RVALUE);

		unaryRValueEClass = createEClass(UNARY_RVALUE);
		createEReference(unaryRValueEClass, UNARY_RVALUE__VALUE);

		binaryRValueEClass = createEClass(BINARY_RVALUE);
		createEReference(binaryRValueEClass, BINARY_RVALUE__LEFT);
		createEReference(binaryRValueEClass, BINARY_RVALUE__RIGHT);

		conceptRefEClass = createEClass(CONCEPT_REF);
		createEAttribute(conceptRefEClass, CONCEPT_REF__QUANTIFIER);
		createEAttribute(conceptRefEClass, CONCEPT_REF__CONCEPT_ID);
		createEAttribute(conceptRefEClass, CONCEPT_REF__LABEL);

		refSetEClass = createEClass(REF_SET);
		createEAttribute(refSetEClass, REF_SET__ID);

		notClauseEClass = createEClass(NOT_CLAUSE);

		subExpressionEClass = createEClass(SUB_EXPRESSION);

		andClauseEClass = createEClass(AND_CLAUSE);

		orClauseEClass = createEClass(OR_CLAUSE);

		attributeClauseEClass = createEClass(ATTRIBUTE_CLAUSE);

		attributeClauseGroupEClass = createEClass(ATTRIBUTE_CLAUSE_GROUP);

		numericDataClauseEClass = createEClass(NUMERIC_DATA_CLAUSE);
		createEReference(numericDataClauseEClass, NUMERIC_DATA_CLAUSE__CONCEPTS);
		createEAttribute(numericDataClauseEClass, NUMERIC_DATA_CLAUSE__OPERATOR);
		createEAttribute(numericDataClauseEClass, NUMERIC_DATA_CLAUSE__VALUE);
		createEAttribute(numericDataClauseEClass, NUMERIC_DATA_CLAUSE__UNIT_TYPE);

		numericDataGroupClauseEClass = createEClass(NUMERIC_DATA_GROUP_CLAUSE);
		createEReference(numericDataGroupClauseEClass, NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS);
		createEReference(numericDataGroupClauseEClass, NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA);
		createEReference(numericDataGroupClauseEClass, NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE);

		// Create enums
		subsumptionQuantifierEEnum = createEEnum(SUBSUMPTION_QUANTIFIER);
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
		unaryRValueEClass.getESuperTypes().add(this.getRValue());
		binaryRValueEClass.getESuperTypes().add(this.getRValue());
		conceptRefEClass.getESuperTypes().add(this.getRValue());
		refSetEClass.getESuperTypes().add(this.getRValue());
		notClauseEClass.getESuperTypes().add(this.getUnaryRValue());
		subExpressionEClass.getESuperTypes().add(this.getUnaryRValue());
		andClauseEClass.getESuperTypes().add(this.getBinaryRValue());
		orClauseEClass.getESuperTypes().add(this.getBinaryRValue());
		attributeClauseEClass.getESuperTypes().add(this.getBinaryRValue());
		attributeClauseGroupEClass.getESuperTypes().add(this.getUnaryRValue());
		numericDataClauseEClass.getESuperTypes().add(this.getRValue());
		numericDataGroupClauseEClass.getESuperTypes().add(this.getRValue());

		// Initialize classes and features; add operations and parameters
		initEClass(rValueEClass, RValue.class, "RValue", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(unaryRValueEClass, UnaryRValue.class, "UnaryRValue", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getUnaryRValue_Value(), this.getRValue(), null, "value", null, 1, 1, UnaryRValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(binaryRValueEClass, BinaryRValue.class, "BinaryRValue", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBinaryRValue_Left(), this.getRValue(), null, "left", null, 1, 1, BinaryRValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getBinaryRValue_Right(), this.getRValue(), null, "right", null, 1, 1, BinaryRValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(conceptRefEClass, ConceptRef.class, "ConceptRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConceptRef_Quantifier(), this.getSubsumptionQuantifier(), "quantifier", null, 1, 1, ConceptRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConceptRef_ConceptId(), ecorePackage.getEString(), "conceptId", null, 1, 1, ConceptRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConceptRef_Label(), ecorePackage.getEString(), "label", null, 0, 1, ConceptRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(refSetEClass, RefSet.class, "RefSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRefSet_Id(), ecorePackage.getEString(), "id", null, 1, 1, RefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(notClauseEClass, NotClause.class, "NotClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(subExpressionEClass, SubExpression.class, "SubExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(andClauseEClass, AndClause.class, "AndClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(orClauseEClass, OrClause.class, "OrClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(attributeClauseEClass, AttributeClause.class, "AttributeClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(attributeClauseGroupEClass, AttributeClauseGroup.class, "AttributeClauseGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(numericDataClauseEClass, NumericDataClause.class, "NumericDataClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNumericDataClause_Concepts(), this.getRValue(), null, "concepts", null, 0, 1, NumericDataClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNumericDataClause_Operator(), ecorePackage.getEString(), "operator", null, 1, 1, NumericDataClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNumericDataClause_Value(), ecorePackage.getEInt(), "value", null, 1, 1, NumericDataClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNumericDataClause_UnitType(), ecorePackage.getEString(), "unitType", null, 1, 1, NumericDataClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(numericDataGroupClauseEClass, NumericDataGroupClause.class, "NumericDataGroupClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNumericDataGroupClause_Concepts(), this.getRValue(), null, "concepts", null, 0, 1, NumericDataGroupClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNumericDataGroupClause_NumericData(), this.getNumericDataClause(), null, "numericData", null, 1, 1, NumericDataGroupClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNumericDataGroupClause_Substance(), this.getRValue(), null, "substance", null, 1, 1, NumericDataGroupClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(subsumptionQuantifierEEnum, SubsumptionQuantifier.class, "SubsumptionQuantifier");
		addEEnumLiteral(subsumptionQuantifierEEnum, SubsumptionQuantifier.SELF);
		addEEnumLiteral(subsumptionQuantifierEEnum, SubsumptionQuantifier.ANY_SUBTYPE);
		addEEnumLiteral(subsumptionQuantifierEEnum, SubsumptionQuantifier.SELF_AND_ANY_SUBTYPE);

		// Create resource
		createResource(eNS_URI);
	}

} //ecoreastPackageImpl