/**
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.ecl.ecl.impl;

import com.b2international.snowowl.snomed.ecl.ecl.*;

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
public class EclFactoryImpl extends EFactoryImpl implements EclFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static EclFactory init()
  {
    try
    {
      EclFactory theEclFactory = (EclFactory)EPackage.Registry.INSTANCE.getEFactory(EclPackage.eNS_URI);
      if (theEclFactory != null)
      {
        return theEclFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EclFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclFactoryImpl()
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
      case EclPackage.EXPRESSION_CONSTRAINT: return createExpressionConstraint();
      case EclPackage.CHILD_OF: return createChildOf();
      case EclPackage.DESCENDANT_OF: return createDescendantOf();
      case EclPackage.DESCENDANT_OR_SELF_OF: return createDescendantOrSelfOf();
      case EclPackage.PARENT_OF: return createParentOf();
      case EclPackage.ANCESTOR_OF: return createAncestorOf();
      case EclPackage.ANCESTOR_OR_SELF_OF: return createAncestorOrSelfOf();
      case EclPackage.MEMBER_OF: return createMemberOf();
      case EclPackage.CONCEPT_REFERENCE: return createConceptReference();
      case EclPackage.ANY: return createAny();
      case EclPackage.REFINEMENT: return createRefinement();
      case EclPackage.NESTED_REFINEMENT: return createNestedRefinement();
      case EclPackage.ATTRIBUTE_GROUP: return createAttributeGroup();
      case EclPackage.ATTRIBUTE_CONSTRAINT: return createAttributeConstraint();
      case EclPackage.CARDINALITY: return createCardinality();
      case EclPackage.COMPARISON: return createComparison();
      case EclPackage.ATTRIBUTE_COMPARISON: return createAttributeComparison();
      case EclPackage.DATA_TYPE_COMPARISON: return createDataTypeComparison();
      case EclPackage.ATTRIBUTE_VALUE_EQUALS: return createAttributeValueEquals();
      case EclPackage.ATTRIBUTE_VALUE_NOT_EQUALS: return createAttributeValueNotEquals();
      case EclPackage.STRING_VALUE_EQUALS: return createStringValueEquals();
      case EclPackage.STRING_VALUE_NOT_EQUALS: return createStringValueNotEquals();
      case EclPackage.INTEGER_VALUE_EQUALS: return createIntegerValueEquals();
      case EclPackage.INTEGER_VALUE_NOT_EQUALS: return createIntegerValueNotEquals();
      case EclPackage.INTEGER_VALUE_GREATER_THAN: return createIntegerValueGreaterThan();
      case EclPackage.INTEGER_VALUE_LESS_THAN: return createIntegerValueLessThan();
      case EclPackage.INTEGER_VALUE_GREATER_THAN_EQUALS: return createIntegerValueGreaterThanEquals();
      case EclPackage.INTEGER_VALUE_LESS_THAN_EQUALS: return createIntegerValueLessThanEquals();
      case EclPackage.DECIMAL_VALUE_EQUALS: return createDecimalValueEquals();
      case EclPackage.DECIMAL_VALUE_NOT_EQUALS: return createDecimalValueNotEquals();
      case EclPackage.DECIMAL_VALUE_GREATER_THAN: return createDecimalValueGreaterThan();
      case EclPackage.DECIMAL_VALUE_LESS_THAN: return createDecimalValueLessThan();
      case EclPackage.DECIMAL_VALUE_GREATER_THAN_EQUALS: return createDecimalValueGreaterThanEquals();
      case EclPackage.DECIMAL_VALUE_LESS_THAN_EQUALS: return createDecimalValueLessThanEquals();
      case EclPackage.NESTED_EXPRESSION: return createNestedExpression();
      case EclPackage.OR_EXPRESSION_CONSTRAINT: return createOrExpressionConstraint();
      case EclPackage.AND_EXPRESSION_CONSTRAINT: return createAndExpressionConstraint();
      case EclPackage.EXCLUSION_EXPRESSION_CONSTRAINT: return createExclusionExpressionConstraint();
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT: return createRefinedExpressionConstraint();
      case EclPackage.DOTTED_EXPRESSION_CONSTRAINT: return createDottedExpressionConstraint();
      case EclPackage.OR_REFINEMENT: return createOrRefinement();
      case EclPackage.AND_REFINEMENT: return createAndRefinement();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionConstraint createExpressionConstraint()
  {
    ExpressionConstraintImpl expressionConstraint = new ExpressionConstraintImpl();
    return expressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ChildOf createChildOf()
  {
    ChildOfImpl childOf = new ChildOfImpl();
    return childOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DescendantOf createDescendantOf()
  {
    DescendantOfImpl descendantOf = new DescendantOfImpl();
    return descendantOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DescendantOrSelfOf createDescendantOrSelfOf()
  {
    DescendantOrSelfOfImpl descendantOrSelfOf = new DescendantOrSelfOfImpl();
    return descendantOrSelfOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ParentOf createParentOf()
  {
    ParentOfImpl parentOf = new ParentOfImpl();
    return parentOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AncestorOf createAncestorOf()
  {
    AncestorOfImpl ancestorOf = new AncestorOfImpl();
    return ancestorOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AncestorOrSelfOf createAncestorOrSelfOf()
  {
    AncestorOrSelfOfImpl ancestorOrSelfOf = new AncestorOrSelfOfImpl();
    return ancestorOrSelfOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberOf createMemberOf()
  {
    MemberOfImpl memberOf = new MemberOfImpl();
    return memberOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConceptReference createConceptReference()
  {
    ConceptReferenceImpl conceptReference = new ConceptReferenceImpl();
    return conceptReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Any createAny()
  {
    AnyImpl any = new AnyImpl();
    return any;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Refinement createRefinement()
  {
    RefinementImpl refinement = new RefinementImpl();
    return refinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NestedRefinement createNestedRefinement()
  {
    NestedRefinementImpl nestedRefinement = new NestedRefinementImpl();
    return nestedRefinement;
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
  public AttributeConstraint createAttributeConstraint()
  {
    AttributeConstraintImpl attributeConstraint = new AttributeConstraintImpl();
    return attributeConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Cardinality createCardinality()
  {
    CardinalityImpl cardinality = new CardinalityImpl();
    return cardinality;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Comparison createComparison()
  {
    ComparisonImpl comparison = new ComparisonImpl();
    return comparison;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeComparison createAttributeComparison()
  {
    AttributeComparisonImpl attributeComparison = new AttributeComparisonImpl();
    return attributeComparison;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataTypeComparison createDataTypeComparison()
  {
    DataTypeComparisonImpl dataTypeComparison = new DataTypeComparisonImpl();
    return dataTypeComparison;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeValueEquals createAttributeValueEquals()
  {
    AttributeValueEqualsImpl attributeValueEquals = new AttributeValueEqualsImpl();
    return attributeValueEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeValueNotEquals createAttributeValueNotEquals()
  {
    AttributeValueNotEqualsImpl attributeValueNotEquals = new AttributeValueNotEqualsImpl();
    return attributeValueNotEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StringValueEquals createStringValueEquals()
  {
    StringValueEqualsImpl stringValueEquals = new StringValueEqualsImpl();
    return stringValueEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StringValueNotEquals createStringValueNotEquals()
  {
    StringValueNotEqualsImpl stringValueNotEquals = new StringValueNotEqualsImpl();
    return stringValueNotEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntegerValueEquals createIntegerValueEquals()
  {
    IntegerValueEqualsImpl integerValueEquals = new IntegerValueEqualsImpl();
    return integerValueEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntegerValueNotEquals createIntegerValueNotEquals()
  {
    IntegerValueNotEqualsImpl integerValueNotEquals = new IntegerValueNotEqualsImpl();
    return integerValueNotEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntegerValueGreaterThan createIntegerValueGreaterThan()
  {
    IntegerValueGreaterThanImpl integerValueGreaterThan = new IntegerValueGreaterThanImpl();
    return integerValueGreaterThan;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntegerValueLessThan createIntegerValueLessThan()
  {
    IntegerValueLessThanImpl integerValueLessThan = new IntegerValueLessThanImpl();
    return integerValueLessThan;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntegerValueGreaterThanEquals createIntegerValueGreaterThanEquals()
  {
    IntegerValueGreaterThanEqualsImpl integerValueGreaterThanEquals = new IntegerValueGreaterThanEqualsImpl();
    return integerValueGreaterThanEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntegerValueLessThanEquals createIntegerValueLessThanEquals()
  {
    IntegerValueLessThanEqualsImpl integerValueLessThanEquals = new IntegerValueLessThanEqualsImpl();
    return integerValueLessThanEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecimalValueEquals createDecimalValueEquals()
  {
    DecimalValueEqualsImpl decimalValueEquals = new DecimalValueEqualsImpl();
    return decimalValueEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecimalValueNotEquals createDecimalValueNotEquals()
  {
    DecimalValueNotEqualsImpl decimalValueNotEquals = new DecimalValueNotEqualsImpl();
    return decimalValueNotEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecimalValueGreaterThan createDecimalValueGreaterThan()
  {
    DecimalValueGreaterThanImpl decimalValueGreaterThan = new DecimalValueGreaterThanImpl();
    return decimalValueGreaterThan;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecimalValueLessThan createDecimalValueLessThan()
  {
    DecimalValueLessThanImpl decimalValueLessThan = new DecimalValueLessThanImpl();
    return decimalValueLessThan;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecimalValueGreaterThanEquals createDecimalValueGreaterThanEquals()
  {
    DecimalValueGreaterThanEqualsImpl decimalValueGreaterThanEquals = new DecimalValueGreaterThanEqualsImpl();
    return decimalValueGreaterThanEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecimalValueLessThanEquals createDecimalValueLessThanEquals()
  {
    DecimalValueLessThanEqualsImpl decimalValueLessThanEquals = new DecimalValueLessThanEqualsImpl();
    return decimalValueLessThanEquals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NestedExpression createNestedExpression()
  {
    NestedExpressionImpl nestedExpression = new NestedExpressionImpl();
    return nestedExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrExpressionConstraint createOrExpressionConstraint()
  {
    OrExpressionConstraintImpl orExpressionConstraint = new OrExpressionConstraintImpl();
    return orExpressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AndExpressionConstraint createAndExpressionConstraint()
  {
    AndExpressionConstraintImpl andExpressionConstraint = new AndExpressionConstraintImpl();
    return andExpressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExclusionExpressionConstraint createExclusionExpressionConstraint()
  {
    ExclusionExpressionConstraintImpl exclusionExpressionConstraint = new ExclusionExpressionConstraintImpl();
    return exclusionExpressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RefinedExpressionConstraint createRefinedExpressionConstraint()
  {
    RefinedExpressionConstraintImpl refinedExpressionConstraint = new RefinedExpressionConstraintImpl();
    return refinedExpressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DottedExpressionConstraint createDottedExpressionConstraint()
  {
    DottedExpressionConstraintImpl dottedExpressionConstraint = new DottedExpressionConstraintImpl();
    return dottedExpressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrRefinement createOrRefinement()
  {
    OrRefinementImpl orRefinement = new OrRefinementImpl();
    return orRefinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AndRefinement createAndRefinement()
  {
    AndRefinementImpl andRefinement = new AndRefinementImpl();
    return andRefinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclPackage getEclPackage()
  {
    return (EclPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EclPackage getPackage()
  {
    return EclPackage.eINSTANCE;
  }

} //EclFactoryImpl
