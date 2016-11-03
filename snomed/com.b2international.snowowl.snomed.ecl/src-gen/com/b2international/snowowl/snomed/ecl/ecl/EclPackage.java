/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclFactory
 * @model kind="package"
 * @generated
 */
public interface EclPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "ecl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.b2international.com/snowowl/snomed/Ecl";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "ecl";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EclPackage eINSTANCE = com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl.init();

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl <em>Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExpressionConstraint()
   * @generated
   */
  int EXPRESSION_CONSTRAINT = 0;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_CONSTRAINT__EXPRESSION = 0;

  /**
   * The number of structural features of the '<em>Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_CONSTRAINT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.FocusConceptImpl <em>Focus Concept</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.FocusConceptImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getFocusConcept()
   * @generated
   */
  int FOCUS_CONCEPT = 1;

  /**
   * The number of structural features of the '<em>Focus Concept</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FOCUS_CONCEPT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl <em>Member Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getMemberOf()
   * @generated
   */
  int MEMBER_OF = 2;

  /**
   * The feature id for the '<em><b>Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_OF__CONCEPT = FOCUS_CONCEPT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Member Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_OF_FEATURE_COUNT = FOCUS_CONCEPT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getConceptReference()
   * @generated
   */
  int CONCEPT_REFERENCE = 3;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__ID = FOCUS_CONCEPT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__TERM = FOCUS_CONCEPT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Concept Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE_FEATURE_COUNT = FOCUS_CONCEPT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl <em>Any</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAny()
   * @generated
   */
  int ANY = 4;

  /**
   * The number of structural features of the '<em>Any</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANY_FEATURE_COUNT = FOCUS_CONCEPT_FEATURE_COUNT + 0;


  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint <em>Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint
   * @generated
   */
  EClass getExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expression</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint#getExpression()
   * @see #getExpressionConstraint()
   * @generated
   */
  EReference getExpressionConstraint_Expression();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.FocusConcept <em>Focus Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Focus Concept</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.FocusConcept
   * @generated
   */
  EClass getFocusConcept();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf <em>Member Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Member Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.MemberOf
   * @generated
   */
  EClass getMemberOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConcept <em>Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Concept</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConcept()
   * @see #getMemberOf()
   * @generated
   */
  EReference getMemberOf_Concept();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ConceptReference <em>Concept Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Reference</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ConceptReference
   * @generated
   */
  EClass getConceptReference();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getId()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Id();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getTerm()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Term();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.Any <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Any</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Any
   * @generated
   */
  EClass getAny();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EclFactory getEclFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl <em>Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExpressionConstraint()
     * @generated
     */
    EClass EXPRESSION_CONSTRAINT = eINSTANCE.getExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION_CONSTRAINT__EXPRESSION = eINSTANCE.getExpressionConstraint_Expression();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.FocusConceptImpl <em>Focus Concept</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.FocusConceptImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getFocusConcept()
     * @generated
     */
    EClass FOCUS_CONCEPT = eINSTANCE.getFocusConcept();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl <em>Member Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getMemberOf()
     * @generated
     */
    EClass MEMBER_OF = eINSTANCE.getMemberOf();

    /**
     * The meta object literal for the '<em><b>Concept</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_OF__CONCEPT = eINSTANCE.getMemberOf_Concept();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getConceptReference()
     * @generated
     */
    EClass CONCEPT_REFERENCE = eINSTANCE.getConceptReference();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT_REFERENCE__ID = eINSTANCE.getConceptReference_Id();

    /**
     * The meta object literal for the '<em><b>Term</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT_REFERENCE__TERM = eINSTANCE.getConceptReference_Term();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl <em>Any</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAny()
     * @generated
     */
    EClass ANY = eINSTANCE.getAny();

  }

} //EclPackage
