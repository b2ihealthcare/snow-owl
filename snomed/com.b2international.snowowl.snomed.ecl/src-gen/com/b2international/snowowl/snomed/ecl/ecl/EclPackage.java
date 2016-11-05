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
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.NestableExpressionImpl <em>Nestable Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.NestableExpressionImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getNestableExpression()
   * @generated
   */
  int NESTABLE_EXPRESSION = 6;

  /**
   * The number of structural features of the '<em>Nestable Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NESTABLE_EXPRESSION_FEATURE_COUNT = 0;

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
   * The number of structural features of the '<em>Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_CONSTRAINT_FEATURE_COUNT = NESTABLE_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl <em>Descendant Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOf()
   * @generated
   */
  int DESCENDANT_OF = 1;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Descendant Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl <em>Descendant Or Self Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOrSelfOf()
   * @generated
   */
  int DESCENDANT_OR_SELF_OF = 2;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OR_SELF_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Descendant Or Self Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OR_SELF_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl <em>Member Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getMemberOf()
   * @generated
   */
  int MEMBER_OF = 3;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Member Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getConceptReference()
   * @generated
   */
  int CONCEPT_REFERENCE = 4;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__ID = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__TERM = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Concept Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl <em>Any</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAny()
   * @generated
   */
  int ANY = 5;

  /**
   * The number of structural features of the '<em>Any</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANY_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl <em>Or Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getOrExpressionConstraint()
   * @generated
   */
  int OR_EXPRESSION_CONSTRAINT = 7;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_EXPRESSION_CONSTRAINT__LEFT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_EXPRESSION_CONSTRAINT__RIGHT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Or Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl <em>And Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAndExpressionConstraint()
   * @generated
   */
  int AND_EXPRESSION_CONSTRAINT = 8;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_EXPRESSION_CONSTRAINT__LEFT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_EXPRESSION_CONSTRAINT__RIGHT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>And Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl <em>Exclusion Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExclusionExpressionConstraint()
   * @generated
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT = 9;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT__LEFT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Exclusion Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;


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
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOf <em>Descendant Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Descendant Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOf
   * @generated
   */
  EClass getDescendantOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOf#getConstraint()
   * @see #getDescendantOf()
   * @generated
   */
  EReference getDescendantOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf <em>Descendant Or Self Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Descendant Or Self Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf
   * @generated
   */
  EClass getDescendantOrSelfOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf#getConstraint()
   * @see #getDescendantOrSelfOf()
   * @generated
   */
  EReference getDescendantOrSelfOf_Constraint();

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
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConstraint()
   * @see #getMemberOf()
   * @generated
   */
  EReference getMemberOf_Constraint();

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
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.NestableExpression <em>Nestable Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Nestable Expression</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.NestableExpression
   * @generated
   */
  EClass getNestableExpression();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint <em>Or Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint
   * @generated
   */
  EClass getOrExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getLeft()
   * @see #getOrExpressionConstraint()
   * @generated
   */
  EReference getOrExpressionConstraint_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getRight()
   * @see #getOrExpressionConstraint()
   * @generated
   */
  EReference getOrExpressionConstraint_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint <em>And Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint
   * @generated
   */
  EClass getAndExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getLeft()
   * @see #getAndExpressionConstraint()
   * @generated
   */
  EReference getAndExpressionConstraint_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getRight()
   * @see #getAndExpressionConstraint()
   * @generated
   */
  EReference getAndExpressionConstraint_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint <em>Exclusion Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Exclusion Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint
   * @generated
   */
  EClass getExclusionExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getLeft()
   * @see #getExclusionExpressionConstraint()
   * @generated
   */
  EReference getExclusionExpressionConstraint_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getRight()
   * @see #getExclusionExpressionConstraint()
   * @generated
   */
  EReference getExclusionExpressionConstraint_Right();

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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl <em>Descendant Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOf()
     * @generated
     */
    EClass DESCENDANT_OF = eINSTANCE.getDescendantOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCENDANT_OF__CONSTRAINT = eINSTANCE.getDescendantOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl <em>Descendant Or Self Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOrSelfOf()
     * @generated
     */
    EClass DESCENDANT_OR_SELF_OF = eINSTANCE.getDescendantOrSelfOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCENDANT_OR_SELF_OF__CONSTRAINT = eINSTANCE.getDescendantOrSelfOf_Constraint();

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
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_OF__CONSTRAINT = eINSTANCE.getMemberOf_Constraint();

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

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.NestableExpressionImpl <em>Nestable Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.NestableExpressionImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getNestableExpression()
     * @generated
     */
    EClass NESTABLE_EXPRESSION = eINSTANCE.getNestableExpression();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl <em>Or Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getOrExpressionConstraint()
     * @generated
     */
    EClass OR_EXPRESSION_CONSTRAINT = eINSTANCE.getOrExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR_EXPRESSION_CONSTRAINT__LEFT = eINSTANCE.getOrExpressionConstraint_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR_EXPRESSION_CONSTRAINT__RIGHT = eINSTANCE.getOrExpressionConstraint_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl <em>And Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAndExpressionConstraint()
     * @generated
     */
    EClass AND_EXPRESSION_CONSTRAINT = eINSTANCE.getAndExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND_EXPRESSION_CONSTRAINT__LEFT = eINSTANCE.getAndExpressionConstraint_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND_EXPRESSION_CONSTRAINT__RIGHT = eINSTANCE.getAndExpressionConstraint_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl <em>Exclusion Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExclusionExpressionConstraint()
     * @generated
     */
    EClass EXCLUSION_EXPRESSION_CONSTRAINT = eINSTANCE.getExclusionExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXCLUSION_EXPRESSION_CONSTRAINT__LEFT = eINSTANCE.getExclusionExpressionConstraint_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT = eINSTANCE.getExclusionExpressionConstraint_Right();

  }

} //EclPackage
