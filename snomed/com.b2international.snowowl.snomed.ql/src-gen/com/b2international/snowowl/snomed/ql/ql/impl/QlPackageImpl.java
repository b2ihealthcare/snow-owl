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
package com.b2international.snowowl.snomed.ql.ql.impl;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import com.b2international.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ql.ql.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class QlPackageImpl extends EPackageImpl implements QlPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass subQueryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass domainQueryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nestedQueryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass filterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nestedFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertyFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass activeFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass moduleFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass termFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass preferredInFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass acceptableInFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass languageRefSetFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass typeFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass caseSignificanceFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass languageCodeFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryDisjunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryConjunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryExclusionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass disjunctionFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conjunctionFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass exclusionFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum lexicalSearchTypeEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum domainEEnum = null;

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
   * @see com.b2international.snowowl.snomed.ql.ql.QlPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private QlPackageImpl()
  {
    super(eNS_URI, QlFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link QlPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static QlPackage init()
  {
    if (isInited) return (QlPackage)EPackage.Registry.INSTANCE.getEPackage(QlPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredQlPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    QlPackageImpl theQlPackage = registeredQlPackage instanceof QlPackageImpl ? (QlPackageImpl)registeredQlPackage : new QlPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EclPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theQlPackage.createPackageContents();

    // Initialize created meta-data
    theQlPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theQlPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(QlPackage.eNS_URI, theQlPackage);
    return theQlPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getQuery()
  {
    return queryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getQuery_Query()
  {
    return (EReference)queryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getQueryConstraint()
  {
    return queryConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSubQuery()
  {
    return subQueryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDomainQuery()
  {
    return domainQueryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDomainQuery_Ecl()
  {
    return (EReference)domainQueryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDomainQuery_Filter()
  {
    return (EReference)domainQueryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getNestedQuery()
  {
    return nestedQueryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getNestedQuery_Nested()
  {
    return (EReference)nestedQueryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getFilter()
  {
    return filterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getNestedFilter()
  {
    return nestedFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getNestedFilter_Nested()
  {
    return (EReference)nestedFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPropertyFilter()
  {
    return propertyFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getActiveFilter()
  {
    return activeFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getActiveFilter_Domain()
  {
    return (EAttribute)activeFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getActiveFilter_Active()
  {
    return (EAttribute)activeFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getModuleFilter()
  {
    return moduleFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getModuleFilter_Domain()
  {
    return (EAttribute)moduleFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getModuleFilter_ModuleId()
  {
    return (EReference)moduleFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTermFilter()
  {
    return termFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTermFilter_LexicalSearchType()
  {
    return (EAttribute)termFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTermFilter_Term()
  {
    return (EAttribute)termFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPreferredInFilter()
  {
    return preferredInFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPreferredInFilter_LanguageRefSetId()
  {
    return (EReference)preferredInFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAcceptableInFilter()
  {
    return acceptableInFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAcceptableInFilter_LanguageRefSetId()
  {
    return (EReference)acceptableInFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getLanguageRefSetFilter()
  {
    return languageRefSetFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getLanguageRefSetFilter_LanguageRefSetId()
  {
    return (EReference)languageRefSetFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTypeFilter()
  {
    return typeFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTypeFilter_Type()
  {
    return (EReference)typeFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCaseSignificanceFilter()
  {
    return caseSignificanceFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCaseSignificanceFilter_CaseSignificanceId()
  {
    return (EReference)caseSignificanceFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getLanguageCodeFilter()
  {
    return languageCodeFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getLanguageCodeFilter_LanguageCode()
  {
    return (EAttribute)languageCodeFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getQueryDisjunction()
  {
    return queryDisjunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getQueryDisjunction_Left()
  {
    return (EReference)queryDisjunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getQueryDisjunction_Right()
  {
    return (EReference)queryDisjunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getQueryConjunction()
  {
    return queryConjunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getQueryConjunction_Left()
  {
    return (EReference)queryConjunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getQueryConjunction_Right()
  {
    return (EReference)queryConjunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getQueryExclusion()
  {
    return queryExclusionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getQueryExclusion_Left()
  {
    return (EReference)queryExclusionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getQueryExclusion_Right()
  {
    return (EReference)queryExclusionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDisjunctionFilter()
  {
    return disjunctionFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDisjunctionFilter_Left()
  {
    return (EReference)disjunctionFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDisjunctionFilter_Right()
  {
    return (EReference)disjunctionFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getConjunctionFilter()
  {
    return conjunctionFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getConjunctionFilter_Left()
  {
    return (EReference)conjunctionFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getConjunctionFilter_Right()
  {
    return (EReference)conjunctionFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getExclusionFilter()
  {
    return exclusionFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getExclusionFilter_Left()
  {
    return (EReference)exclusionFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getExclusionFilter_Right()
  {
    return (EReference)exclusionFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getLexicalSearchType()
  {
    return lexicalSearchTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getDomain()
  {
    return domainEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public QlFactory getQlFactory()
  {
    return (QlFactory)getEFactoryInstance();
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
    queryEClass = createEClass(QUERY);
    createEReference(queryEClass, QUERY__QUERY);

    queryConstraintEClass = createEClass(QUERY_CONSTRAINT);

    subQueryEClass = createEClass(SUB_QUERY);

    domainQueryEClass = createEClass(DOMAIN_QUERY);
    createEReference(domainQueryEClass, DOMAIN_QUERY__ECL);
    createEReference(domainQueryEClass, DOMAIN_QUERY__FILTER);

    nestedQueryEClass = createEClass(NESTED_QUERY);
    createEReference(nestedQueryEClass, NESTED_QUERY__NESTED);

    filterEClass = createEClass(FILTER);

    nestedFilterEClass = createEClass(NESTED_FILTER);
    createEReference(nestedFilterEClass, NESTED_FILTER__NESTED);

    propertyFilterEClass = createEClass(PROPERTY_FILTER);

    activeFilterEClass = createEClass(ACTIVE_FILTER);
    createEAttribute(activeFilterEClass, ACTIVE_FILTER__DOMAIN);
    createEAttribute(activeFilterEClass, ACTIVE_FILTER__ACTIVE);

    moduleFilterEClass = createEClass(MODULE_FILTER);
    createEAttribute(moduleFilterEClass, MODULE_FILTER__DOMAIN);
    createEReference(moduleFilterEClass, MODULE_FILTER__MODULE_ID);

    termFilterEClass = createEClass(TERM_FILTER);
    createEAttribute(termFilterEClass, TERM_FILTER__LEXICAL_SEARCH_TYPE);
    createEAttribute(termFilterEClass, TERM_FILTER__TERM);

    preferredInFilterEClass = createEClass(PREFERRED_IN_FILTER);
    createEReference(preferredInFilterEClass, PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID);

    acceptableInFilterEClass = createEClass(ACCEPTABLE_IN_FILTER);
    createEReference(acceptableInFilterEClass, ACCEPTABLE_IN_FILTER__LANGUAGE_REF_SET_ID);

    languageRefSetFilterEClass = createEClass(LANGUAGE_REF_SET_FILTER);
    createEReference(languageRefSetFilterEClass, LANGUAGE_REF_SET_FILTER__LANGUAGE_REF_SET_ID);

    typeFilterEClass = createEClass(TYPE_FILTER);
    createEReference(typeFilterEClass, TYPE_FILTER__TYPE);

    caseSignificanceFilterEClass = createEClass(CASE_SIGNIFICANCE_FILTER);
    createEReference(caseSignificanceFilterEClass, CASE_SIGNIFICANCE_FILTER__CASE_SIGNIFICANCE_ID);

    languageCodeFilterEClass = createEClass(LANGUAGE_CODE_FILTER);
    createEAttribute(languageCodeFilterEClass, LANGUAGE_CODE_FILTER__LANGUAGE_CODE);

    queryDisjunctionEClass = createEClass(QUERY_DISJUNCTION);
    createEReference(queryDisjunctionEClass, QUERY_DISJUNCTION__LEFT);
    createEReference(queryDisjunctionEClass, QUERY_DISJUNCTION__RIGHT);

    queryConjunctionEClass = createEClass(QUERY_CONJUNCTION);
    createEReference(queryConjunctionEClass, QUERY_CONJUNCTION__LEFT);
    createEReference(queryConjunctionEClass, QUERY_CONJUNCTION__RIGHT);

    queryExclusionEClass = createEClass(QUERY_EXCLUSION);
    createEReference(queryExclusionEClass, QUERY_EXCLUSION__LEFT);
    createEReference(queryExclusionEClass, QUERY_EXCLUSION__RIGHT);

    disjunctionFilterEClass = createEClass(DISJUNCTION_FILTER);
    createEReference(disjunctionFilterEClass, DISJUNCTION_FILTER__LEFT);
    createEReference(disjunctionFilterEClass, DISJUNCTION_FILTER__RIGHT);

    conjunctionFilterEClass = createEClass(CONJUNCTION_FILTER);
    createEReference(conjunctionFilterEClass, CONJUNCTION_FILTER__LEFT);
    createEReference(conjunctionFilterEClass, CONJUNCTION_FILTER__RIGHT);

    exclusionFilterEClass = createEClass(EXCLUSION_FILTER);
    createEReference(exclusionFilterEClass, EXCLUSION_FILTER__LEFT);
    createEReference(exclusionFilterEClass, EXCLUSION_FILTER__RIGHT);

    // Create enums
    lexicalSearchTypeEEnum = createEEnum(LEXICAL_SEARCH_TYPE);
    domainEEnum = createEEnum(DOMAIN);
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

    // Obtain other dependent packages
    EclPackage theEclPackage = (EclPackage)EPackage.Registry.INSTANCE.getEPackage(EclPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    subQueryEClass.getESuperTypes().add(this.getQueryConstraint());
    domainQueryEClass.getESuperTypes().add(this.getSubQuery());
    nestedQueryEClass.getESuperTypes().add(this.getSubQuery());
    nestedFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    propertyFilterEClass.getESuperTypes().add(this.getFilter());
    activeFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    moduleFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    termFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    preferredInFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    acceptableInFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    languageRefSetFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    typeFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    caseSignificanceFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    languageCodeFilterEClass.getESuperTypes().add(this.getPropertyFilter());
    queryDisjunctionEClass.getESuperTypes().add(this.getQueryConstraint());
    queryConjunctionEClass.getESuperTypes().add(this.getQueryConstraint());
    queryExclusionEClass.getESuperTypes().add(this.getQueryConstraint());
    disjunctionFilterEClass.getESuperTypes().add(this.getFilter());
    conjunctionFilterEClass.getESuperTypes().add(this.getFilter());
    exclusionFilterEClass.getESuperTypes().add(this.getFilter());

    // Initialize classes and features; add operations and parameters
    initEClass(queryEClass, Query.class, "Query", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getQuery_Query(), this.getQueryConstraint(), null, "query", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(queryConstraintEClass, QueryConstraint.class, "QueryConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(subQueryEClass, SubQuery.class, "SubQuery", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(domainQueryEClass, DomainQuery.class, "DomainQuery", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDomainQuery_Ecl(), theEclPackage.getExpressionConstraint(), null, "ecl", null, 0, 1, DomainQuery.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDomainQuery_Filter(), this.getFilter(), null, "filter", null, 0, 1, DomainQuery.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(nestedQueryEClass, NestedQuery.class, "NestedQuery", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNestedQuery_Nested(), this.getQueryConstraint(), null, "nested", null, 0, 1, NestedQuery.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(filterEClass, Filter.class, "Filter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(nestedFilterEClass, NestedFilter.class, "NestedFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNestedFilter_Nested(), this.getFilter(), null, "nested", null, 0, 1, NestedFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertyFilterEClass, PropertyFilter.class, "PropertyFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(activeFilterEClass, ActiveFilter.class, "ActiveFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getActiveFilter_Domain(), this.getDomain(), "domain", null, 0, 1, ActiveFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActiveFilter_Active(), ecorePackage.getEBoolean(), "active", null, 0, 1, ActiveFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(moduleFilterEClass, ModuleFilter.class, "ModuleFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModuleFilter_Domain(), this.getDomain(), "domain", null, 0, 1, ModuleFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModuleFilter_ModuleId(), theEclPackage.getExpressionConstraint(), null, "moduleId", null, 0, 1, ModuleFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(termFilterEClass, TermFilter.class, "TermFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTermFilter_LexicalSearchType(), this.getLexicalSearchType(), "lexicalSearchType", null, 0, 1, TermFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTermFilter_Term(), ecorePackage.getEString(), "term", null, 0, 1, TermFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(preferredInFilterEClass, PreferredInFilter.class, "PreferredInFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPreferredInFilter_LanguageRefSetId(), theEclPackage.getExpressionConstraint(), null, "languageRefSetId", null, 0, 1, PreferredInFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(acceptableInFilterEClass, AcceptableInFilter.class, "AcceptableInFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAcceptableInFilter_LanguageRefSetId(), theEclPackage.getExpressionConstraint(), null, "languageRefSetId", null, 0, 1, AcceptableInFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(languageRefSetFilterEClass, LanguageRefSetFilter.class, "LanguageRefSetFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLanguageRefSetFilter_LanguageRefSetId(), theEclPackage.getExpressionConstraint(), null, "languageRefSetId", null, 0, 1, LanguageRefSetFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(typeFilterEClass, TypeFilter.class, "TypeFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTypeFilter_Type(), theEclPackage.getExpressionConstraint(), null, "type", null, 0, 1, TypeFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(caseSignificanceFilterEClass, CaseSignificanceFilter.class, "CaseSignificanceFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCaseSignificanceFilter_CaseSignificanceId(), theEclPackage.getExpressionConstraint(), null, "caseSignificanceId", null, 0, 1, CaseSignificanceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(languageCodeFilterEClass, LanguageCodeFilter.class, "LanguageCodeFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLanguageCodeFilter_LanguageCode(), ecorePackage.getEString(), "languageCode", null, 0, 1, LanguageCodeFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(queryDisjunctionEClass, QueryDisjunction.class, "QueryDisjunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getQueryDisjunction_Left(), this.getQueryConstraint(), null, "left", null, 0, 1, QueryDisjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getQueryDisjunction_Right(), this.getQueryConstraint(), null, "right", null, 0, 1, QueryDisjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(queryConjunctionEClass, QueryConjunction.class, "QueryConjunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getQueryConjunction_Left(), this.getQueryConstraint(), null, "left", null, 0, 1, QueryConjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getQueryConjunction_Right(), this.getQueryConstraint(), null, "right", null, 0, 1, QueryConjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(queryExclusionEClass, QueryExclusion.class, "QueryExclusion", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getQueryExclusion_Left(), this.getSubQuery(), null, "left", null, 0, 1, QueryExclusion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getQueryExclusion_Right(), this.getSubQuery(), null, "right", null, 0, 1, QueryExclusion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(disjunctionFilterEClass, DisjunctionFilter.class, "DisjunctionFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDisjunctionFilter_Left(), this.getFilter(), null, "left", null, 0, 1, DisjunctionFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDisjunctionFilter_Right(), this.getFilter(), null, "right", null, 0, 1, DisjunctionFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conjunctionFilterEClass, ConjunctionFilter.class, "ConjunctionFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConjunctionFilter_Left(), this.getFilter(), null, "left", null, 0, 1, ConjunctionFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConjunctionFilter_Right(), this.getFilter(), null, "right", null, 0, 1, ConjunctionFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(exclusionFilterEClass, ExclusionFilter.class, "ExclusionFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExclusionFilter_Left(), this.getPropertyFilter(), null, "left", null, 0, 1, ExclusionFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExclusionFilter_Right(), this.getPropertyFilter(), null, "right", null, 0, 1, ExclusionFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(lexicalSearchTypeEEnum, LexicalSearchType.class, "LexicalSearchType");
    addEEnumLiteral(lexicalSearchTypeEEnum, LexicalSearchType.MATCH);
    addEEnumLiteral(lexicalSearchTypeEEnum, LexicalSearchType.REGEX);
    addEEnumLiteral(lexicalSearchTypeEEnum, LexicalSearchType.EXACT);

    initEEnum(domainEEnum, Domain.class, "Domain");
    addEEnumLiteral(domainEEnum, Domain.CONCEPT);
    addEEnumLiteral(domainEEnum, Domain.DESCRIPTION);

    // Create resource
    createResource(eNS_URI);
  }

} //QlPackageImpl
