/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getExpressionConstraint()
 * @model
 * @generated
 */
public interface ExpressionConstraint extends EObject
{
  /**
   * Returns the value of the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expression</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expression</em>' containment reference.
   * @see #setExpression(ConceptReference)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getExpressionConstraint_Expression()
   * @model containment="true"
   * @generated
   */
  ConceptReference getExpression();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint#getExpression <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expression</em>' containment reference.
   * @see #getExpression()
   * @generated
   */
  void setExpression(ConceptReference value);

} // ExpressionConstraint
