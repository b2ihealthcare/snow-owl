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
package com.b2international.snowowl.dsl.escg.util;

import com.b2international.snowowl.dsl.escg.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.dsl.escg.EscgPackage
 * @generated
 */
public class EscgAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static EscgPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EscgAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = EscgPackage.eINSTANCE;
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
  protected EscgSwitch<Adapter> modelSwitch =
    new EscgSwitch<Adapter>()
    {
      @Override
      public Adapter caseExpression(Expression object)
      {
        return createExpressionAdapter();
      }
      @Override
      public Adapter caseSubExpression(SubExpression object)
      {
        return createSubExpressionAdapter();
      }
      @Override
      public Adapter caseLValue(LValue object)
      {
        return createLValueAdapter();
      }
      @Override
      public Adapter caseRefSet(RefSet object)
      {
        return createRefSetAdapter();
      }
      @Override
      public Adapter caseConceptGroup(ConceptGroup object)
      {
        return createConceptGroupAdapter();
      }
      @Override
      public Adapter caseConcept(Concept object)
      {
        return createConceptAdapter();
      }
      @Override
      public Adapter caseRefinements(Refinements object)
      {
        return createRefinementsAdapter();
      }
      @Override
      public Adapter caseAttributeGroup(AttributeGroup object)
      {
        return createAttributeGroupAdapter();
      }
      @Override
      public Adapter caseAttributeSet(AttributeSet object)
      {
        return createAttributeSetAdapter();
      }
      @Override
      public Adapter caseAttribute(Attribute object)
      {
        return createAttributeAdapter();
      }
      @Override
      public Adapter caseAttributeAssignment(AttributeAssignment object)
      {
        return createAttributeAssignmentAdapter();
      }
      @Override
      public Adapter caseConceptAssignment(ConceptAssignment object)
      {
        return createConceptAssignmentAdapter();
      }
      @Override
      public Adapter caseNumericalAssignment(NumericalAssignment object)
      {
        return createNumericalAssignmentAdapter();
      }
      @Override
      public Adapter caseNumericalAssignmentGroup(NumericalAssignmentGroup object)
      {
        return createNumericalAssignmentGroupAdapter();
      }
      @Override
      public Adapter caseRValue(RValue object)
      {
        return createRValueAdapter();
      }
      @Override
      public Adapter caseNegatableSubExpression(NegatableSubExpression object)
      {
        return createNegatableSubExpressionAdapter();
      }
      @Override
      public Adapter caseOr(Or object)
      {
        return createOrAdapter();
      }
      @Override
      public Adapter caseAnd(And object)
      {
        return createAndAdapter();
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
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.Expression
   * @generated
   */
  public Adapter createExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.SubExpression <em>Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.SubExpression
   * @generated
   */
  public Adapter createSubExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.LValue <em>LValue</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.LValue
   * @generated
   */
  public Adapter createLValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.RefSet <em>Ref Set</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.RefSet
   * @generated
   */
  public Adapter createRefSetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.ConceptGroup <em>Concept Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.ConceptGroup
   * @generated
   */
  public Adapter createConceptGroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.Concept <em>Concept</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.Concept
   * @generated
   */
  public Adapter createConceptAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.Refinements <em>Refinements</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.Refinements
   * @generated
   */
  public Adapter createRefinementsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.AttributeGroup <em>Attribute Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.AttributeGroup
   * @generated
   */
  public Adapter createAttributeGroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.AttributeSet <em>Attribute Set</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.AttributeSet
   * @generated
   */
  public Adapter createAttributeSetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.Attribute
   * @generated
   */
  public Adapter createAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.AttributeAssignment <em>Attribute Assignment</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.AttributeAssignment
   * @generated
   */
  public Adapter createAttributeAssignmentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.ConceptAssignment <em>Concept Assignment</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.ConceptAssignment
   * @generated
   */
  public Adapter createConceptAssignmentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.NumericalAssignment <em>Numerical Assignment</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignment
   * @generated
   */
  public Adapter createNumericalAssignmentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup <em>Numerical Assignment Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup
   * @generated
   */
  public Adapter createNumericalAssignmentGroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.RValue <em>RValue</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.RValue
   * @generated
   */
  public Adapter createRValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.NegatableSubExpression <em>Negatable Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.NegatableSubExpression
   * @generated
   */
  public Adapter createNegatableSubExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.Or <em>Or</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.Or
   * @generated
   */
  public Adapter createOrAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.dsl.escg.And <em>And</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.dsl.escg.And
   * @generated
   */
  public Adapter createAndAdapter()
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

} //EscgAdapterFactory