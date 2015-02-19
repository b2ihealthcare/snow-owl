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
package com.b2international.snowowl.dsl.escg.impl;

import com.b2international.snowowl.dsl.escg.And;
import com.b2international.snowowl.dsl.escg.Attribute;
import com.b2international.snowowl.dsl.escg.AttributeAssignment;
import com.b2international.snowowl.dsl.escg.AttributeGroup;
import com.b2international.snowowl.dsl.escg.AttributeSet;
import com.b2international.snowowl.dsl.escg.Concept;
import com.b2international.snowowl.dsl.escg.ConceptAssignment;
import com.b2international.snowowl.dsl.escg.ConceptGroup;
import com.b2international.snowowl.dsl.escg.EscgFactory;
import com.b2international.snowowl.dsl.escg.EscgPackage;
import com.b2international.snowowl.dsl.escg.Expression;
import com.b2international.snowowl.dsl.escg.LValue;
import com.b2international.snowowl.dsl.escg.NegatableSubExpression;
import com.b2international.snowowl.dsl.escg.NumericalAssignment;
import com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup;
import com.b2international.snowowl.dsl.escg.Or;
import com.b2international.snowowl.dsl.escg.RValue;
import com.b2international.snowowl.dsl.escg.RefSet;
import com.b2international.snowowl.dsl.escg.Refinements;
import com.b2international.snowowl.dsl.escg.SubExpression;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EscgPackageImpl extends EPackageImpl implements EscgPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionEClass = null;

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
  private EClass lValueEClass = null;

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
  private EClass conceptGroupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conceptEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass refinementsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeGroupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeSetEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeAssignmentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conceptAssignmentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass numericalAssignmentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass numericalAssignmentGroupEClass = null;

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
  private EClass negatableSubExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass orEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass andEClass = null;

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
   * @see com.b2international.snowowl.dsl.escg.EscgPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EscgPackageImpl()
  {
    super(eNS_URI, EscgFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link EscgPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EscgPackage init()
  {
    if (isInited) return (EscgPackage)EPackage.Registry.INSTANCE.getEPackage(EscgPackage.eNS_URI);

    // Obtain or create and register package
    EscgPackageImpl theEscgPackage = (EscgPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof EscgPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new EscgPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theEscgPackage.createPackageContents();

    // Initialize created meta-data
    theEscgPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theEscgPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(EscgPackage.eNS_URI, theEscgPackage);
    return theEscgPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExpression()
  {
    return expressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExpression_SubExpression()
  {
    return (EReference)expressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSubExpression()
  {
    return subExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubExpression_LValues()
  {
    return (EReference)subExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubExpression_Refinements()
  {
    return (EReference)subExpressionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLValue()
  {
    return lValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLValue_Negated()
  {
    return (EAttribute)lValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRefSet()
  {
    return refSetEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRefSet_Id()
  {
    return (EAttribute)refSetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRefSet_Term()
  {
    return (EAttribute)refSetEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConceptGroup()
  {
    return conceptGroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getConceptGroup_Constraint()
  {
    return (EAttribute)conceptGroupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConceptGroup_Concept()
  {
    return (EReference)conceptGroupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConcept()
  {
    return conceptEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getConcept_Id()
  {
    return (EAttribute)conceptEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getConcept_Term()
  {
    return (EAttribute)conceptEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRefinements()
  {
    return refinementsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRefinements_AttributeSet()
  {
    return (EReference)refinementsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRefinements_AttributeGroups()
  {
    return (EReference)refinementsEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeGroup()
  {
    return attributeGroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeSet()
  {
    return attributeSetEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeSet_Attributes()
  {
    return (EReference)attributeSetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttribute()
  {
    return attributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_Optional()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttribute_Assignment()
  {
    return (EReference)attributeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeAssignment()
  {
    return attributeAssignmentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConceptAssignment()
  {
    return conceptAssignmentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConceptAssignment_Name()
  {
    return (EReference)conceptAssignmentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConceptAssignment_Value()
  {
    return (EReference)conceptAssignmentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNumericalAssignment()
  {
    return numericalAssignmentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNumericalAssignment_Name()
  {
    return (EReference)numericalAssignmentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNumericalAssignment_Operator()
  {
    return (EAttribute)numericalAssignmentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNumericalAssignment_Value()
  {
    return (EAttribute)numericalAssignmentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNumericalAssignment_Unit()
  {
    return (EAttribute)numericalAssignmentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNumericalAssignmentGroup()
  {
    return numericalAssignmentGroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNumericalAssignmentGroup_IngredientConcept()
  {
    return (EReference)numericalAssignmentGroupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNumericalAssignmentGroup_Substance()
  {
    return (EReference)numericalAssignmentGroupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNumericalAssignmentGroup_NumericValue()
  {
    return (EReference)numericalAssignmentGroupEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRValue()
  {
    return rValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNegatableSubExpression()
  {
    return negatableSubExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getNegatableSubExpression_Negated()
  {
    return (EAttribute)negatableSubExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNegatableSubExpression_Expression()
  {
    return (EReference)negatableSubExpressionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOr()
  {
    return orEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOr_Left()
  {
    return (EReference)orEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOr_Right()
  {
    return (EReference)orEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAnd()
  {
    return andEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAnd_Left()
  {
    return (EReference)andEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAnd_Right()
  {
    return (EReference)andEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EscgFactory getEscgFactory()
  {
    return (EscgFactory)getEFactoryInstance();
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
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    expressionEClass = createEClass(EXPRESSION);
    createEReference(expressionEClass, EXPRESSION__SUB_EXPRESSION);

    subExpressionEClass = createEClass(SUB_EXPRESSION);
    createEReference(subExpressionEClass, SUB_EXPRESSION__LVALUES);
    createEReference(subExpressionEClass, SUB_EXPRESSION__REFINEMENTS);

    lValueEClass = createEClass(LVALUE);
    createEAttribute(lValueEClass, LVALUE__NEGATED);

    refSetEClass = createEClass(REF_SET);
    createEAttribute(refSetEClass, REF_SET__ID);
    createEAttribute(refSetEClass, REF_SET__TERM);

    conceptGroupEClass = createEClass(CONCEPT_GROUP);
    createEAttribute(conceptGroupEClass, CONCEPT_GROUP__CONSTRAINT);
    createEReference(conceptGroupEClass, CONCEPT_GROUP__CONCEPT);

    conceptEClass = createEClass(CONCEPT);
    createEAttribute(conceptEClass, CONCEPT__ID);
    createEAttribute(conceptEClass, CONCEPT__TERM);

    refinementsEClass = createEClass(REFINEMENTS);
    createEReference(refinementsEClass, REFINEMENTS__ATTRIBUTE_SET);
    createEReference(refinementsEClass, REFINEMENTS__ATTRIBUTE_GROUPS);

    attributeGroupEClass = createEClass(ATTRIBUTE_GROUP);

    attributeSetEClass = createEClass(ATTRIBUTE_SET);
    createEReference(attributeSetEClass, ATTRIBUTE_SET__ATTRIBUTES);

    attributeEClass = createEClass(ATTRIBUTE);
    createEAttribute(attributeEClass, ATTRIBUTE__OPTIONAL);
    createEReference(attributeEClass, ATTRIBUTE__ASSIGNMENT);

    attributeAssignmentEClass = createEClass(ATTRIBUTE_ASSIGNMENT);

    conceptAssignmentEClass = createEClass(CONCEPT_ASSIGNMENT);
    createEReference(conceptAssignmentEClass, CONCEPT_ASSIGNMENT__NAME);
    createEReference(conceptAssignmentEClass, CONCEPT_ASSIGNMENT__VALUE);

    numericalAssignmentEClass = createEClass(NUMERICAL_ASSIGNMENT);
    createEReference(numericalAssignmentEClass, NUMERICAL_ASSIGNMENT__NAME);
    createEAttribute(numericalAssignmentEClass, NUMERICAL_ASSIGNMENT__OPERATOR);
    createEAttribute(numericalAssignmentEClass, NUMERICAL_ASSIGNMENT__VALUE);
    createEAttribute(numericalAssignmentEClass, NUMERICAL_ASSIGNMENT__UNIT);

    numericalAssignmentGroupEClass = createEClass(NUMERICAL_ASSIGNMENT_GROUP);
    createEReference(numericalAssignmentGroupEClass, NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT);
    createEReference(numericalAssignmentGroupEClass, NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE);
    createEReference(numericalAssignmentGroupEClass, NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE);

    rValueEClass = createEClass(RVALUE);

    negatableSubExpressionEClass = createEClass(NEGATABLE_SUB_EXPRESSION);
    createEAttribute(negatableSubExpressionEClass, NEGATABLE_SUB_EXPRESSION__NEGATED);
    createEReference(negatableSubExpressionEClass, NEGATABLE_SUB_EXPRESSION__EXPRESSION);

    orEClass = createEClass(OR);
    createEReference(orEClass, OR__LEFT);
    createEReference(orEClass, OR__RIGHT);

    andEClass = createEClass(AND);
    createEReference(andEClass, AND__LEFT);
    createEReference(andEClass, AND__RIGHT);
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
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    lValueEClass.getESuperTypes().add(this.getRValue());
    refSetEClass.getESuperTypes().add(this.getLValue());
    conceptGroupEClass.getESuperTypes().add(this.getLValue());
    attributeSetEClass.getESuperTypes().add(this.getAttributeGroup());
    conceptAssignmentEClass.getESuperTypes().add(this.getAttributeAssignment());
    numericalAssignmentEClass.getESuperTypes().add(this.getAttributeAssignment());
    numericalAssignmentGroupEClass.getESuperTypes().add(this.getAttributeAssignment());
    negatableSubExpressionEClass.getESuperTypes().add(this.getRValue());
    orEClass.getESuperTypes().add(this.getRValue());
    andEClass.getESuperTypes().add(this.getRValue());

    // Initialize classes and features; add operations and parameters
    initEClass(expressionEClass, Expression.class, "Expression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExpression_SubExpression(), this.getSubExpression(), null, "subExpression", null, 0, -1, Expression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(subExpressionEClass, SubExpression.class, "SubExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSubExpression_LValues(), this.getLValue(), null, "lValues", null, 0, -1, SubExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubExpression_Refinements(), this.getRefinements(), null, "refinements", null, 0, 1, SubExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(lValueEClass, LValue.class, "LValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLValue_Negated(), ecorePackage.getEBoolean(), "negated", null, 0, 1, LValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refSetEClass, RefSet.class, "RefSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRefSet_Id(), ecorePackage.getEString(), "id", null, 0, 1, RefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRefSet_Term(), ecorePackage.getEString(), "term", null, 0, 1, RefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conceptGroupEClass, ConceptGroup.class, "ConceptGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConceptGroup_Constraint(), ecorePackage.getEString(), "constraint", null, 0, 1, ConceptGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConceptGroup_Concept(), this.getConcept(), null, "concept", null, 0, 1, ConceptGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conceptEClass, Concept.class, "Concept", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConcept_Id(), ecorePackage.getEString(), "id", null, 0, 1, Concept.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConcept_Term(), ecorePackage.getEString(), "term", null, 0, 1, Concept.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refinementsEClass, Refinements.class, "Refinements", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefinements_AttributeSet(), this.getAttributeSet(), null, "attributeSet", null, 0, 1, Refinements.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRefinements_AttributeGroups(), this.getAttributeGroup(), null, "attributeGroups", null, 0, -1, Refinements.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeGroupEClass, AttributeGroup.class, "AttributeGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(attributeSetEClass, AttributeSet.class, "AttributeSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeSet_Attributes(), this.getAttribute(), null, "attributes", null, 0, -1, AttributeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeEClass, Attribute.class, "Attribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttribute_Optional(), ecorePackage.getEBoolean(), "optional", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttribute_Assignment(), this.getAttributeAssignment(), null, "assignment", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeAssignmentEClass, AttributeAssignment.class, "AttributeAssignment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(conceptAssignmentEClass, ConceptAssignment.class, "ConceptAssignment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConceptAssignment_Name(), this.getLValue(), null, "name", null, 0, 1, ConceptAssignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConceptAssignment_Value(), this.getRValue(), null, "value", null, 0, 1, ConceptAssignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(numericalAssignmentEClass, NumericalAssignment.class, "NumericalAssignment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNumericalAssignment_Name(), this.getConcept(), null, "name", null, 0, 1, NumericalAssignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNumericalAssignment_Operator(), ecorePackage.getEString(), "operator", null, 0, 1, NumericalAssignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNumericalAssignment_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, NumericalAssignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNumericalAssignment_Unit(), ecorePackage.getEString(), "unit", null, 0, 1, NumericalAssignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(numericalAssignmentGroupEClass, NumericalAssignmentGroup.class, "NumericalAssignmentGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNumericalAssignmentGroup_IngredientConcept(), this.getConcept(), null, "ingredientConcept", null, 0, 1, NumericalAssignmentGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNumericalAssignmentGroup_Substance(), this.getRValue(), null, "substance", null, 0, 1, NumericalAssignmentGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNumericalAssignmentGroup_NumericValue(), this.getNumericalAssignment(), null, "numericValue", null, 0, 1, NumericalAssignmentGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(rValueEClass, RValue.class, "RValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(negatableSubExpressionEClass, NegatableSubExpression.class, "NegatableSubExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getNegatableSubExpression_Negated(), ecorePackage.getEBoolean(), "negated", null, 0, 1, NegatableSubExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNegatableSubExpression_Expression(), this.getExpression(), null, "expression", null, 0, 1, NegatableSubExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orEClass, Or.class, "Or", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOr_Left(), this.getRValue(), null, "left", null, 0, 1, Or.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOr_Right(), this.getRValue(), null, "right", null, 0, 1, Or.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(andEClass, And.class, "And", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAnd_Left(), this.getRValue(), null, "left", null, 0, 1, And.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAnd_Right(), this.getRValue(), null, "right", null, 0, 1, And.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //EscgPackageImpl