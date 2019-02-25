/**
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ql.ql.util;

import com.b2international.snowowl.snomed.ql.ql.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage
 * @generated
 */
public class QlAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static QlPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QlAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = QlPackage.eINSTANCE;
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
  protected QlSwitch<Adapter> modelSwitch =
    new QlSwitch<Adapter>()
    {
      @Override
      public Adapter caseQuery(Query object)
      {
        return createQueryAdapter();
      }
      @Override
      public Adapter caseConstraint(Constraint object)
      {
        return createConstraintAdapter();
      }
      @Override
      public Adapter caseNestedFilter(NestedFilter object)
      {
        return createNestedFilterAdapter();
      }
      @Override
      public Adapter caseFilter(Filter object)
      {
        return createFilterAdapter();
      }
      @Override
      public Adapter caseActiveFilter(ActiveFilter object)
      {
        return createActiveFilterAdapter();
      }
      @Override
      public Adapter caseModuleFilter(ModuleFilter object)
      {
        return createModuleFilterAdapter();
      }
      @Override
      public Adapter caseTermFilter(TermFilter object)
      {
        return createTermFilterAdapter();
      }
      @Override
      public Adapter casePreferredInFilter(PreferredInFilter object)
      {
        return createPreferredInFilterAdapter();
      }
      @Override
      public Adapter caseAcceptableInFilter(AcceptableInFilter object)
      {
        return createAcceptableInFilterAdapter();
      }
      @Override
      public Adapter caseLanguageRefSetFilter(LanguageRefSetFilter object)
      {
        return createLanguageRefSetFilterAdapter();
      }
      @Override
      public Adapter caseTypeFilter(TypeFilter object)
      {
        return createTypeFilterAdapter();
      }
      @Override
      public Adapter caseDisjunction(Disjunction object)
      {
        return createDisjunctionAdapter();
      }
      @Override
      public Adapter caseConjunction(Conjunction object)
      {
        return createConjunctionAdapter();
      }
      @Override
      public Adapter caseExclusion(Exclusion object)
      {
        return createExclusionAdapter();
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
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.Query <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.Query
   * @generated
   */
  public Adapter createQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.Constraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.Constraint
   * @generated
   */
  public Adapter createConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.NestedFilter <em>Nested Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.NestedFilter
   * @generated
   */
  public Adapter createNestedFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.Filter <em>Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.Filter
   * @generated
   */
  public Adapter createFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.ActiveFilter <em>Active Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.ActiveFilter
   * @generated
   */
  public Adapter createActiveFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter <em>Module Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.ModuleFilter
   * @generated
   */
  public Adapter createModuleFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.TermFilter <em>Term Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.TermFilter
   * @generated
   */
  public Adapter createTermFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.PreferredInFilter <em>Preferred In Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.PreferredInFilter
   * @generated
   */
  public Adapter createPreferredInFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter <em>Acceptable In Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter
   * @generated
   */
  public Adapter createAcceptableInFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter <em>Language Ref Set Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter
   * @generated
   */
  public Adapter createLanguageRefSetFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.TypeFilter <em>Type Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.TypeFilter
   * @generated
   */
  public Adapter createTypeFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.Disjunction <em>Disjunction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.Disjunction
   * @generated
   */
  public Adapter createDisjunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.Conjunction <em>Conjunction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.Conjunction
   * @generated
   */
  public Adapter createConjunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.Exclusion <em>Exclusion</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.Exclusion
   * @generated
   */
  public Adapter createExclusionAdapter()
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

} //QlAdapterFactory
