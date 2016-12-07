/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Nested Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.NestedExpression#getNested <em>Nested</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getNestedExpression()
 * @model
 * @generated
 */
public interface NestedExpression extends ExpressionConstraint
{
  /**
   * Returns the value of the '<em><b>Nested</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nested</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Nested</em>' containment reference.
   * @see #setNested(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getNestedExpression_Nested()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getNested();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.NestedExpression#getNested <em>Nested</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Nested</em>' containment reference.
   * @see #getNested()
   * @generated
   */
  void setNested(ExpressionConstraint value);

} // NestedExpression
