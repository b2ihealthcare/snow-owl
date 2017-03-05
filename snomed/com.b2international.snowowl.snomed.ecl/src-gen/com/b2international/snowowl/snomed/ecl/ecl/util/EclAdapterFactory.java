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
package com.b2international.snowowl.snomed.ecl.ecl.util;

import com.b2international.snowowl.snomed.ecl.ecl.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage
 * @generated
 */
public class EclAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static EclPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = EclPackage.eINSTANCE;
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
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
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
  protected EclSwitch<Adapter> modelSwitch =
    new EclSwitch<Adapter>()
    {
      @Override
      public Adapter caseExpressionConstraint(ExpressionConstraint object)
      {
        return createExpressionConstraintAdapter();
      }
      @Override
      public Adapter caseChildOf(ChildOf object)
      {
        return createChildOfAdapter();
      }
      @Override
      public Adapter caseDescendantOf(DescendantOf object)
      {
        return createDescendantOfAdapter();
      }
      @Override
      public Adapter caseDescendantOrSelfOf(DescendantOrSelfOf object)
      {
        return createDescendantOrSelfOfAdapter();
      }
      @Override
      public Adapter caseParentOf(ParentOf object)
      {
        return createParentOfAdapter();
      }
      @Override
      public Adapter caseAncestorOf(AncestorOf object)
      {
        return createAncestorOfAdapter();
      }
      @Override
      public Adapter caseAncestorOrSelfOf(AncestorOrSelfOf object)
      {
        return createAncestorOrSelfOfAdapter();
      }
      @Override
      public Adapter caseMemberOf(MemberOf object)
      {
        return createMemberOfAdapter();
      }
      @Override
      public Adapter caseConceptReference(ConceptReference object)
      {
        return createConceptReferenceAdapter();
      }
      @Override
      public Adapter caseAny(Any object)
      {
        return createAnyAdapter();
      }
      @Override
      public Adapter caseRefinement(Refinement object)
      {
        return createRefinementAdapter();
      }
      @Override
      public Adapter caseNestedRefinement(NestedRefinement object)
      {
        return createNestedRefinementAdapter();
      }
      @Override
      public Adapter caseAttributeGroup(AttributeGroup object)
      {
        return createAttributeGroupAdapter();
      }
      @Override
      public Adapter caseAttributeConstraint(AttributeConstraint object)
      {
        return createAttributeConstraintAdapter();
      }
      @Override
      public Adapter caseCardinality(Cardinality object)
      {
        return createCardinalityAdapter();
      }
      @Override
      public Adapter caseComparison(Comparison object)
      {
        return createComparisonAdapter();
      }
      @Override
      public Adapter caseAttributeComparison(AttributeComparison object)
      {
        return createAttributeComparisonAdapter();
      }
      @Override
      public Adapter caseDataTypeComparison(DataTypeComparison object)
      {
        return createDataTypeComparisonAdapter();
      }
      @Override
      public Adapter caseAttributeValueEquals(AttributeValueEquals object)
      {
        return createAttributeValueEqualsAdapter();
      }
      @Override
      public Adapter caseAttributeValueNotEquals(AttributeValueNotEquals object)
      {
        return createAttributeValueNotEqualsAdapter();
      }
      @Override
      public Adapter caseStringValueEquals(StringValueEquals object)
      {
        return createStringValueEqualsAdapter();
      }
      @Override
      public Adapter caseStringValueNotEquals(StringValueNotEquals object)
      {
        return createStringValueNotEqualsAdapter();
      }
      @Override
      public Adapter caseIntegerValueEquals(IntegerValueEquals object)
      {
        return createIntegerValueEqualsAdapter();
      }
      @Override
      public Adapter caseIntegerValueNotEquals(IntegerValueNotEquals object)
      {
        return createIntegerValueNotEqualsAdapter();
      }
      @Override
      public Adapter caseIntegerValueGreaterThan(IntegerValueGreaterThan object)
      {
        return createIntegerValueGreaterThanAdapter();
      }
      @Override
      public Adapter caseIntegerValueLessThan(IntegerValueLessThan object)
      {
        return createIntegerValueLessThanAdapter();
      }
      @Override
      public Adapter caseIntegerValueGreaterThanEquals(IntegerValueGreaterThanEquals object)
      {
        return createIntegerValueGreaterThanEqualsAdapter();
      }
      @Override
      public Adapter caseIntegerValueLessThanEquals(IntegerValueLessThanEquals object)
      {
        return createIntegerValueLessThanEqualsAdapter();
      }
      @Override
      public Adapter caseDecimalValueEquals(DecimalValueEquals object)
      {
        return createDecimalValueEqualsAdapter();
      }
      @Override
      public Adapter caseDecimalValueNotEquals(DecimalValueNotEquals object)
      {
        return createDecimalValueNotEqualsAdapter();
      }
      @Override
      public Adapter caseDecimalValueGreaterThan(DecimalValueGreaterThan object)
      {
        return createDecimalValueGreaterThanAdapter();
      }
      @Override
      public Adapter caseDecimalValueLessThan(DecimalValueLessThan object)
      {
        return createDecimalValueLessThanAdapter();
      }
      @Override
      public Adapter caseDecimalValueGreaterThanEquals(DecimalValueGreaterThanEquals object)
      {
        return createDecimalValueGreaterThanEqualsAdapter();
      }
      @Override
      public Adapter caseDecimalValueLessThanEquals(DecimalValueLessThanEquals object)
      {
        return createDecimalValueLessThanEqualsAdapter();
      }
      @Override
      public Adapter caseNestedExpression(NestedExpression object)
      {
        return createNestedExpressionAdapter();
      }
      @Override
      public Adapter caseOrExpressionConstraint(OrExpressionConstraint object)
      {
        return createOrExpressionConstraintAdapter();
      }
      @Override
      public Adapter caseAndExpressionConstraint(AndExpressionConstraint object)
      {
        return createAndExpressionConstraintAdapter();
      }
      @Override
      public Adapter caseExclusionExpressionConstraint(ExclusionExpressionConstraint object)
      {
        return createExclusionExpressionConstraintAdapter();
      }
      @Override
      public Adapter caseRefinedExpressionConstraint(RefinedExpressionConstraint object)
      {
        return createRefinedExpressionConstraintAdapter();
      }
      @Override
      public Adapter caseDottedExpressionConstraint(DottedExpressionConstraint object)
      {
        return createDottedExpressionConstraintAdapter();
      }
      @Override
      public Adapter caseOrRefinement(OrRefinement object)
      {
        return createOrRefinementAdapter();
      }
      @Override
      public Adapter caseAndRefinement(AndRefinement object)
      {
        return createAndRefinementAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
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
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint <em>Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint
   * @generated
   */
  public Adapter createExpressionConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.ChildOf <em>Child Of</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ChildOf
   * @generated
   */
  public Adapter createChildOfAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOf <em>Descendant Of</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOf
   * @generated
   */
  public Adapter createDescendantOfAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf <em>Descendant Or Self Of</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf
   * @generated
   */
  public Adapter createDescendantOrSelfOfAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.ParentOf <em>Parent Of</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ParentOf
   * @generated
   */
  public Adapter createParentOfAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOf <em>Ancestor Of</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AncestorOf
   * @generated
   */
  public Adapter createAncestorOfAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf <em>Ancestor Or Self Of</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf
   * @generated
   */
  public Adapter createAncestorOrSelfOfAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf <em>Member Of</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.MemberOf
   * @generated
   */
  public Adapter createMemberOfAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.ConceptReference <em>Concept Reference</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ConceptReference
   * @generated
   */
  public Adapter createConceptReferenceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.Any <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Any
   * @generated
   */
  public Adapter createAnyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Refinement
   * @generated
   */
  public Adapter createRefinementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement <em>Nested Refinement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement
   * @generated
   */
  public Adapter createNestedRefinementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup <em>Attribute Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup
   * @generated
   */
  public Adapter createAttributeGroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint <em>Attribute Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint
   * @generated
   */
  public Adapter createAttributeConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality <em>Cardinality</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Cardinality
   * @generated
   */
  public Adapter createCardinalityAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.Comparison <em>Comparison</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Comparison
   * @generated
   */
  public Adapter createComparisonAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison <em>Attribute Comparison</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison
   * @generated
   */
  public Adapter createAttributeComparisonAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DataTypeComparison <em>Data Type Comparison</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DataTypeComparison
   * @generated
   */
  public Adapter createDataTypeComparisonAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals <em>Attribute Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals
   * @generated
   */
  public Adapter createAttributeValueEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals <em>Attribute Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals
   * @generated
   */
  public Adapter createAttributeValueNotEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals <em>String Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals
   * @generated
   */
  public Adapter createStringValueEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals <em>String Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals
   * @generated
   */
  public Adapter createStringValueNotEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueEquals <em>Integer Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueEquals
   * @generated
   */
  public Adapter createIntegerValueEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueNotEquals <em>Integer Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueNotEquals
   * @generated
   */
  public Adapter createIntegerValueNotEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThan <em>Integer Value Greater Than</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThan
   * @generated
   */
  public Adapter createIntegerValueGreaterThanAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThan <em>Integer Value Less Than</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThan
   * @generated
   */
  public Adapter createIntegerValueLessThanAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals <em>Integer Value Greater Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals
   * @generated
   */
  public Adapter createIntegerValueGreaterThanEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThanEquals <em>Integer Value Less Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThanEquals
   * @generated
   */
  public Adapter createIntegerValueLessThanEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals <em>Decimal Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals
   * @generated
   */
  public Adapter createDecimalValueEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals <em>Decimal Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals
   * @generated
   */
  public Adapter createDecimalValueNotEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan <em>Decimal Value Greater Than</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan
   * @generated
   */
  public Adapter createDecimalValueGreaterThanAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan <em>Decimal Value Less Than</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan
   * @generated
   */
  public Adapter createDecimalValueLessThanAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals <em>Decimal Value Greater Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals
   * @generated
   */
  public Adapter createDecimalValueGreaterThanEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals <em>Decimal Value Less Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals
   * @generated
   */
  public Adapter createDecimalValueLessThanEqualsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.NestedExpression <em>Nested Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.NestedExpression
   * @generated
   */
  public Adapter createNestedExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint <em>Or Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint
   * @generated
   */
  public Adapter createOrExpressionConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint <em>And Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint
   * @generated
   */
  public Adapter createAndExpressionConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint <em>Exclusion Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint
   * @generated
   */
  public Adapter createExclusionExpressionConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint <em>Refined Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint
   * @generated
   */
  public Adapter createRefinedExpressionConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint <em>Dotted Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint
   * @generated
   */
  public Adapter createDottedExpressionConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.OrRefinement <em>Or Refinement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrRefinement
   * @generated
   */
  public Adapter createOrRefinementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement <em>And Refinement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndRefinement
   * @generated
   */
  public Adapter createAndRefinementAdapter()
  {
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
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //EclAdapterFactory
