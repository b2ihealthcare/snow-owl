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

import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;

import com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter;
import com.b2international.snowowl.snomed.ql.ql.ActiveFilter;
import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Constraint;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.Domain;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.Filter;
import com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter;
import com.b2international.snowowl.snomed.ql.ql.LexicalSearchType;
import com.b2international.snowowl.snomed.ql.ql.ModuleFilter;
import com.b2international.snowowl.snomed.ql.ql.NestedFilter;
import com.b2international.snowowl.snomed.ql.ql.PreferredInFilter;
import com.b2international.snowowl.snomed.ql.ql.QlFactory;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;
import com.b2international.snowowl.snomed.ql.ql.Query;
import com.b2international.snowowl.snomed.ql.ql.TermFilter;
import com.b2international.snowowl.snomed.ql.ql.TypeFilter;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

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
  private EClass constraintEClass = null;

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
  private EClass filterEClass = null;

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
  private EClass disjunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conjunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass exclusionEClass = null;

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
    QlPackageImpl theQlPackage = (QlPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof QlPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new QlPackageImpl());

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
  public EClass getQuery()
  {
    return queryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getQuery_Ecl()
  {
    return (EReference)queryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getQuery_Constraint()
  {
    return (EReference)queryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConstraint()
  {
    return constraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNestedFilter()
  {
    return nestedFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNestedFilter_Constraint()
  {
    return (EReference)nestedFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFilter()
  {
    return filterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getActiveFilter()
  {
    return activeFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActiveFilter_Domain()
  {
    return (EAttribute)activeFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActiveFilter_Active()
  {
    return (EAttribute)activeFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModuleFilter()
  {
    return moduleFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModuleFilter_Domain()
  {
    return (EAttribute)moduleFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModuleFilter_ModuleId()
  {
    return (EReference)moduleFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTermFilter()
  {
    return termFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTermFilter_LexicalSearchType()
  {
    return (EAttribute)termFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTermFilter_Term()
  {
    return (EAttribute)termFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPreferredInFilter()
  {
    return preferredInFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferredInFilter_LanguageRefSetId()
  {
    return (EReference)preferredInFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAcceptableInFilter()
  {
    return acceptableInFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAcceptableInFilter_LanguageRefSetId()
  {
    return (EReference)acceptableInFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLanguageRefSetFilter()
  {
    return languageRefSetFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLanguageRefSetFilter_LanguageRefSetId()
  {
    return (EReference)languageRefSetFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTypeFilter()
  {
    return typeFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTypeFilter_Type()
  {
    return (EReference)typeFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDisjunction()
  {
    return disjunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDisjunction_Left()
  {
    return (EReference)disjunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDisjunction_Right()
  {
    return (EReference)disjunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConjunction()
  {
    return conjunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConjunction_Left()
  {
    return (EReference)conjunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConjunction_Right()
  {
    return (EReference)conjunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExclusion()
  {
    return exclusionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExclusion_Left()
  {
    return (EReference)exclusionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExclusion_Right()
  {
    return (EReference)exclusionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getLexicalSearchType()
  {
    return lexicalSearchTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getDomain()
  {
    return domainEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
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
    createEReference(queryEClass, QUERY__ECL);
    createEReference(queryEClass, QUERY__CONSTRAINT);

    constraintEClass = createEClass(CONSTRAINT);

    nestedFilterEClass = createEClass(NESTED_FILTER);
    createEReference(nestedFilterEClass, NESTED_FILTER__CONSTRAINT);

    filterEClass = createEClass(FILTER);

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

    disjunctionEClass = createEClass(DISJUNCTION);
    createEReference(disjunctionEClass, DISJUNCTION__LEFT);
    createEReference(disjunctionEClass, DISJUNCTION__RIGHT);

    conjunctionEClass = createEClass(CONJUNCTION);
    createEReference(conjunctionEClass, CONJUNCTION__LEFT);
    createEReference(conjunctionEClass, CONJUNCTION__RIGHT);

    exclusionEClass = createEClass(EXCLUSION);
    createEReference(exclusionEClass, EXCLUSION__LEFT);
    createEReference(exclusionEClass, EXCLUSION__RIGHT);

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
    nestedFilterEClass.getESuperTypes().add(this.getFilter());
    filterEClass.getESuperTypes().add(this.getConstraint());
    activeFilterEClass.getESuperTypes().add(this.getFilter());
    moduleFilterEClass.getESuperTypes().add(this.getFilter());
    termFilterEClass.getESuperTypes().add(this.getFilter());
    preferredInFilterEClass.getESuperTypes().add(this.getFilter());
    acceptableInFilterEClass.getESuperTypes().add(this.getFilter());
    languageRefSetFilterEClass.getESuperTypes().add(this.getFilter());
    typeFilterEClass.getESuperTypes().add(this.getFilter());
    disjunctionEClass.getESuperTypes().add(this.getConstraint());
    conjunctionEClass.getESuperTypes().add(this.getConstraint());
    exclusionEClass.getESuperTypes().add(this.getConstraint());

    // Initialize classes and features; add operations and parameters
    initEClass(queryEClass, Query.class, "Query", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getQuery_Ecl(), theEclPackage.getExpressionConstraint(), null, "ecl", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getQuery_Constraint(), this.getConstraint(), null, "constraint", null, 0, 1, Query.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(constraintEClass, Constraint.class, "Constraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(nestedFilterEClass, NestedFilter.class, "NestedFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNestedFilter_Constraint(), this.getConstraint(), null, "constraint", null, 0, 1, NestedFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(filterEClass, Filter.class, "Filter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

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

    initEClass(disjunctionEClass, Disjunction.class, "Disjunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDisjunction_Left(), this.getConstraint(), null, "left", null, 0, 1, Disjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDisjunction_Right(), this.getConstraint(), null, "right", null, 0, 1, Disjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conjunctionEClass, Conjunction.class, "Conjunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConjunction_Left(), this.getConstraint(), null, "left", null, 0, 1, Conjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConjunction_Right(), this.getConstraint(), null, "right", null, 0, 1, Conjunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(exclusionEClass, Exclusion.class, "Exclusion", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExclusion_Left(), this.getFilter(), null, "left", null, 0, 1, Exclusion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExclusion_Right(), this.getFilter(), null, "right", null, 0, 1, Exclusion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

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
