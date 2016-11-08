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
   * Returns a new object of class '<em>Child Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Child Of</em>'.
   * @generated
   */
  ChildOf createChildOf();

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
   * Returns a new object of class '<em>Parent Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parent Of</em>'.
   * @generated
   */
  ParentOf createParentOf();

  /**
   * Returns a new object of class '<em>Ancestor Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Ancestor Of</em>'.
   * @generated
   */
  AncestorOf createAncestorOf();

  /**
   * Returns a new object of class '<em>Ancestor Or Self Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Ancestor Or Self Of</em>'.
   * @generated
   */
  AncestorOrSelfOf createAncestorOrSelfOf();

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
   * Returns a new object of class '<em>Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Refinement</em>'.
   * @generated
   */
  Refinement createRefinement();

  /**
   * Returns a new object of class '<em>Cardinality</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Cardinality</em>'.
   * @generated
   */
  Cardinality createCardinality();

  /**
   * Returns a new object of class '<em>Comparison</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Comparison</em>'.
   * @generated
   */
  Comparison createComparison();

  /**
   * Returns a new object of class '<em>Attribute Value Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Value Equals</em>'.
   * @generated
   */
  AttributeValueEquals createAttributeValueEquals();

  /**
   * Returns a new object of class '<em>Attribute Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Value Not Equals</em>'.
   * @generated
   */
  AttributeValueNotEquals createAttributeValueNotEquals();

  /**
   * Returns a new object of class '<em>Nested Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nested Expression</em>'.
   * @generated
   */
  NestedExpression createNestedExpression();

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
   * Returns a new object of class '<em>Refined Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Refined Expression Constraint</em>'.
   * @generated
   */
  RefinedExpressionConstraint createRefinedExpressionConstraint();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  EclPackage getEclPackage();

} //EclFactory
