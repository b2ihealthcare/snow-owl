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

import com.b2international.snowowl.dsl.escg.*;

import org.eclipse.emf.ecore.EClass;
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
public class EscgFactoryImpl extends EFactoryImpl implements EscgFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static EscgFactory init()
  {
    try
    {
      EscgFactory theEscgFactory = (EscgFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.b2international.com/snowowl/dsl/ESCG"); 
      if (theEscgFactory != null)
      {
        return theEscgFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EscgFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EscgFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case EscgPackage.EXPRESSION: return createExpression();
      case EscgPackage.SUB_EXPRESSION: return createSubExpression();
      case EscgPackage.LVALUE: return createLValue();
      case EscgPackage.REF_SET: return createRefSet();
      case EscgPackage.CONCEPT_GROUP: return createConceptGroup();
      case EscgPackage.CONCEPT: return createConcept();
      case EscgPackage.REFINEMENTS: return createRefinements();
      case EscgPackage.ATTRIBUTE_GROUP: return createAttributeGroup();
      case EscgPackage.ATTRIBUTE_SET: return createAttributeSet();
      case EscgPackage.ATTRIBUTE: return createAttribute();
      case EscgPackage.ATTRIBUTE_ASSIGNMENT: return createAttributeAssignment();
      case EscgPackage.CONCEPT_ASSIGNMENT: return createConceptAssignment();
      case EscgPackage.NUMERICAL_ASSIGNMENT: return createNumericalAssignment();
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP: return createNumericalAssignmentGroup();
      case EscgPackage.RVALUE: return createRValue();
      case EscgPackage.NEGATABLE_SUB_EXPRESSION: return createNegatableSubExpression();
      case EscgPackage.OR: return createOr();
      case EscgPackage.AND: return createAnd();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression createExpression()
  {
    ExpressionImpl expression = new ExpressionImpl();
    return expression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SubExpression createSubExpression()
  {
    SubExpressionImpl subExpression = new SubExpressionImpl();
    return subExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LValue createLValue()
  {
    LValueImpl lValue = new LValueImpl();
    return lValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RefSet createRefSet()
  {
    RefSetImpl refSet = new RefSetImpl();
    return refSet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConceptGroup createConceptGroup()
  {
    ConceptGroupImpl conceptGroup = new ConceptGroupImpl();
    return conceptGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Concept createConcept()
  {
    ConceptImpl concept = new ConceptImpl();
    return concept;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Refinements createRefinements()
  {
    RefinementsImpl refinements = new RefinementsImpl();
    return refinements;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeGroup createAttributeGroup()
  {
    AttributeGroupImpl attributeGroup = new AttributeGroupImpl();
    return attributeGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeSet createAttributeSet()
  {
    AttributeSetImpl attributeSet = new AttributeSetImpl();
    return attributeSet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Attribute createAttribute()
  {
    AttributeImpl attribute = new AttributeImpl();
    return attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeAssignment createAttributeAssignment()
  {
    AttributeAssignmentImpl attributeAssignment = new AttributeAssignmentImpl();
    return attributeAssignment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConceptAssignment createConceptAssignment()
  {
    ConceptAssignmentImpl conceptAssignment = new ConceptAssignmentImpl();
    return conceptAssignment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NumericalAssignment createNumericalAssignment()
  {
    NumericalAssignmentImpl numericalAssignment = new NumericalAssignmentImpl();
    return numericalAssignment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NumericalAssignmentGroup createNumericalAssignmentGroup()
  {
    NumericalAssignmentGroupImpl numericalAssignmentGroup = new NumericalAssignmentGroupImpl();
    return numericalAssignmentGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RValue createRValue()
  {
    RValueImpl rValue = new RValueImpl();
    return rValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NegatableSubExpression createNegatableSubExpression()
  {
    NegatableSubExpressionImpl negatableSubExpression = new NegatableSubExpressionImpl();
    return negatableSubExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Or createOr()
  {
    OrImpl or = new OrImpl();
    return or;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public And createAnd()
  {
    AndImpl and = new AndImpl();
    return and;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EscgPackage getEscgPackage()
  {
    return (EscgPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EscgPackage getPackage()
  {
    return EscgPackage.eINSTANCE;
  }

} //EscgFactoryImpl