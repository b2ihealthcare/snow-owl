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
package com.b2international.snowowl.snomed.ql.ql.impl;

import com.b2international.snowowl.snomed.ql.ql.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
public class QlFactoryImpl extends EFactoryImpl implements QlFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static QlFactory init()
  {
    try
    {
      QlFactory theQlFactory = (QlFactory)EPackage.Registry.INSTANCE.getEFactory(QlPackage.eNS_URI);
      if (theQlFactory != null)
      {
        return theQlFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new QlFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QlFactoryImpl()
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
      case QlPackage.QUERY: return createQuery();
      case QlPackage.QUERY_CONSTRAINT: return createQueryConstraint();
      case QlPackage.SUB_QUERY: return createSubQuery();
      case QlPackage.DOMAIN_QUERY: return createDomainQuery();
      case QlPackage.NESTED_QUERY: return createNestedQuery();
      case QlPackage.FILTER: return createFilter();
      case QlPackage.NESTED_FILTER: return createNestedFilter();
      case QlPackage.PROPERTY_FILTER: return createPropertyFilter();
      case QlPackage.ACTIVE_FILTER: return createActiveFilter();
      case QlPackage.MODULE_FILTER: return createModuleFilter();
      case QlPackage.TERM_FILTER: return createTermFilter();
      case QlPackage.PREFERRED_IN_FILTER: return createPreferredInFilter();
      case QlPackage.ACCEPTABLE_IN_FILTER: return createAcceptableInFilter();
      case QlPackage.LANGUAGE_REF_SET_FILTER: return createLanguageRefSetFilter();
      case QlPackage.TYPE_FILTER: return createTypeFilter();
      case QlPackage.CASE_SIGNIFICANCE_FILTER: return createCaseSignificanceFilter();
      case QlPackage.LANGUAGE_CODE_FILTER: return createLanguageCodeFilter();
      case QlPackage.QUERY_DISJUNCTION: return createQueryDisjunction();
      case QlPackage.QUERY_CONJUNCTION: return createQueryConjunction();
      case QlPackage.QUERY_EXCLUSION: return createQueryExclusion();
      case QlPackage.DISJUNCTION: return createDisjunction();
      case QlPackage.CONJUNCTION: return createConjunction();
      case QlPackage.EXCLUSION: return createExclusion();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case QlPackage.LEXICAL_SEARCH_TYPE:
        return createLexicalSearchTypeFromString(eDataType, initialValue);
      case QlPackage.DOMAIN:
        return createDomainFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case QlPackage.LEXICAL_SEARCH_TYPE:
        return convertLexicalSearchTypeToString(eDataType, instanceValue);
      case QlPackage.DOMAIN:
        return convertDomainToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Query createQuery()
  {
    QueryImpl query = new QueryImpl();
    return query;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryConstraint createQueryConstraint()
  {
    QueryConstraintImpl queryConstraint = new QueryConstraintImpl();
    return queryConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SubQuery createSubQuery()
  {
    SubQueryImpl subQuery = new SubQueryImpl();
    return subQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DomainQuery createDomainQuery()
  {
    DomainQueryImpl domainQuery = new DomainQueryImpl();
    return domainQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NestedQuery createNestedQuery()
  {
    NestedQueryImpl nestedQuery = new NestedQueryImpl();
    return nestedQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Filter createFilter()
  {
    FilterImpl filter = new FilterImpl();
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NestedFilter createNestedFilter()
  {
    NestedFilterImpl nestedFilter = new NestedFilterImpl();
    return nestedFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PropertyFilter createPropertyFilter()
  {
    PropertyFilterImpl propertyFilter = new PropertyFilterImpl();
    return propertyFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ActiveFilter createActiveFilter()
  {
    ActiveFilterImpl activeFilter = new ActiveFilterImpl();
    return activeFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModuleFilter createModuleFilter()
  {
    ModuleFilterImpl moduleFilter = new ModuleFilterImpl();
    return moduleFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TermFilter createTermFilter()
  {
    TermFilterImpl termFilter = new TermFilterImpl();
    return termFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferredInFilter createPreferredInFilter()
  {
    PreferredInFilterImpl preferredInFilter = new PreferredInFilterImpl();
    return preferredInFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AcceptableInFilter createAcceptableInFilter()
  {
    AcceptableInFilterImpl acceptableInFilter = new AcceptableInFilterImpl();
    return acceptableInFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LanguageRefSetFilter createLanguageRefSetFilter()
  {
    LanguageRefSetFilterImpl languageRefSetFilter = new LanguageRefSetFilterImpl();
    return languageRefSetFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TypeFilter createTypeFilter()
  {
    TypeFilterImpl typeFilter = new TypeFilterImpl();
    return typeFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CaseSignificanceFilter createCaseSignificanceFilter()
  {
    CaseSignificanceFilterImpl caseSignificanceFilter = new CaseSignificanceFilterImpl();
    return caseSignificanceFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LanguageCodeFilter createLanguageCodeFilter()
  {
    LanguageCodeFilterImpl languageCodeFilter = new LanguageCodeFilterImpl();
    return languageCodeFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryDisjunction createQueryDisjunction()
  {
    QueryDisjunctionImpl queryDisjunction = new QueryDisjunctionImpl();
    return queryDisjunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryConjunction createQueryConjunction()
  {
    QueryConjunctionImpl queryConjunction = new QueryConjunctionImpl();
    return queryConjunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryExclusion createQueryExclusion()
  {
    QueryExclusionImpl queryExclusion = new QueryExclusionImpl();
    return queryExclusion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Disjunction createDisjunction()
  {
    DisjunctionImpl disjunction = new DisjunctionImpl();
    return disjunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Conjunction createConjunction()
  {
    ConjunctionImpl conjunction = new ConjunctionImpl();
    return conjunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Exclusion createExclusion()
  {
    ExclusionImpl exclusion = new ExclusionImpl();
    return exclusion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LexicalSearchType createLexicalSearchTypeFromString(EDataType eDataType, String initialValue)
  {
    LexicalSearchType result = LexicalSearchType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertLexicalSearchTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Domain createDomainFromString(EDataType eDataType, String initialValue)
  {
    Domain result = Domain.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDomainToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QlPackage getQlPackage()
  {
    return (QlPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static QlPackage getPackage()
  {
    return QlPackage.eINSTANCE;
  }

} //QlFactoryImpl
