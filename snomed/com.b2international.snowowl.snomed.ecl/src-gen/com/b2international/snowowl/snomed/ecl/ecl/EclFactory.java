/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage
 * @generated
 */
public interface EclFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EclFactory eINSTANCE = com.b2international.snowowl.snomed.ecl.ecl.impl.EclFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression Constraint</em>'.
   * @generated
   */
  ExpressionConstraint createExpressionConstraint();

  /**
   * Returns a new object of class '<em>Descendant Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Descendant Of</em>'.
   * @generated
   */
  DescendantOf createDescendantOf();

  /**
   * Returns a new object of class '<em>Descendant Or Self Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Descendant Or Self Of</em>'.
   * @generated
   */
  DescendantOrSelfOf createDescendantOrSelfOf();

  /**
   * Returns a new object of class '<em>Member Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Member Of</em>'.
   * @generated
   */
  MemberOf createMemberOf();

  /**
   * Returns a new object of class '<em>Concept Reference</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Concept Reference</em>'.
   * @generated
   */
  ConceptReference createConceptReference();

  /**
   * Returns a new object of class '<em>Any</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Any</em>'.
   * @generated
   */
  Any createAny();

  /**
   * Returns a new object of class '<em>Nestable Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nestable Expression</em>'.
   * @generated
   */
  NestableExpression createNestableExpression();

  /**
   * Returns a new object of class '<em>Or Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Expression Constraint</em>'.
   * @generated
   */
  OrExpressionConstraint createOrExpressionConstraint();

  /**
   * Returns a new object of class '<em>And Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Expression Constraint</em>'.
   * @generated
   */
  AndExpressionConstraint createAndExpressionConstraint();

  /**
   * Returns a new object of class '<em>Exclusion Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Exclusion Expression Constraint</em>'.
   * @generated
   */
  ExclusionExpressionConstraint createExclusionExpressionConstraint();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  EclPackage getEclPackage();

} //EclFactory
