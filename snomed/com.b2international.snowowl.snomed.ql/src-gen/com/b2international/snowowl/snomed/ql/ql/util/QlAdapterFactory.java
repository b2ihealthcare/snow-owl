/**
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
      public Adapter caseQueryConstraint(QueryConstraint object)
      {
        return createQueryConstraintAdapter();
      }
      @Override
      public Adapter caseSubQuery(SubQuery object)
      {
        return createSubQueryAdapter();
      }
      @Override
      public Adapter caseDomainQuery(DomainQuery object)
      {
        return createDomainQueryAdapter();
      }
      @Override
      public Adapter caseNestedQuery(NestedQuery object)
      {
        return createNestedQueryAdapter();
      }
      @Override
      public Adapter caseFilter(Filter object)
      {
        return createFilterAdapter();
      }
      @Override
      public Adapter caseNestedFilter(NestedFilter object)
      {
        return createNestedFilterAdapter();
      }
      @Override
      public Adapter casePropertyFilter(PropertyFilter object)
      {
        return createPropertyFilterAdapter();
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
      public Adapter caseCaseSignificanceFilter(CaseSignificanceFilter object)
      {
        return createCaseSignificanceFilterAdapter();
      }
      @Override
      public Adapter caseLanguageCodeFilter(LanguageCodeFilter object)
      {
        return createLanguageCodeFilterAdapter();
      }
      @Override
      public Adapter caseQueryDisjunction(QueryDisjunction object)
      {
        return createQueryDisjunctionAdapter();
      }
      @Override
      public Adapter caseQueryConjunction(QueryConjunction object)
      {
        return createQueryConjunctionAdapter();
      }
      @Override
      public Adapter caseQueryExclusion(QueryExclusion object)
      {
        return createQueryExclusionAdapter();
      }
      @Override
      public Adapter caseDisjunctionFilter(DisjunctionFilter object)
      {
        return createDisjunctionFilterAdapter();
      }
      @Override
      public Adapter caseConjunctionFilter(ConjunctionFilter object)
      {
        return createConjunctionFilterAdapter();
      }
      @Override
      public Adapter caseExclusionFilter(ExclusionFilter object)
      {
        return createExclusionFilterAdapter();
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
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.QueryConstraint <em>Query Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.QueryConstraint
   * @generated
   */
  public Adapter createQueryConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.SubQuery <em>Sub Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.SubQuery
   * @generated
   */
  public Adapter createSubQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.DomainQuery <em>Domain Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.DomainQuery
   * @generated
   */
  public Adapter createDomainQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.NestedQuery <em>Nested Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.NestedQuery
   * @generated
   */
  public Adapter createNestedQueryAdapter()
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
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.PropertyFilter <em>Property Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.PropertyFilter
   * @generated
   */
  public Adapter createPropertyFilterAdapter()
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
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.CaseSignificanceFilter <em>Case Significance Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.CaseSignificanceFilter
   * @generated
   */
  public Adapter createCaseSignificanceFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter <em>Language Code Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter
   * @generated
   */
  public Adapter createLanguageCodeFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.QueryDisjunction <em>Query Disjunction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.QueryDisjunction
   * @generated
   */
  public Adapter createQueryDisjunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.QueryConjunction <em>Query Conjunction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.QueryConjunction
   * @generated
   */
  public Adapter createQueryConjunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.QueryExclusion <em>Query Exclusion</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.QueryExclusion
   * @generated
   */
  public Adapter createQueryExclusionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.DisjunctionFilter <em>Disjunction Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.DisjunctionFilter
   * @generated
   */
  public Adapter createDisjunctionFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.ConjunctionFilter <em>Conjunction Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.ConjunctionFilter
   * @generated
   */
  public Adapter createConjunctionFilterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.ql.ql.ExclusionFilter <em>Exclusion Filter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.ql.ql.ExclusionFilter
   * @generated
   */
  public Adapter createExclusionFilterAdapter()
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
