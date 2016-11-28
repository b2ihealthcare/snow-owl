/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Exclusion Expression Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getLeft <em>Left</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getRight <em>Right</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getExclusionExpressionConstraint()
 * @model
 * @generated
 */
public interface ExclusionExpressionConstraint extends ExpressionConstraint
{
  /**
   * Returns the value of the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Left</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Left</em>' containment reference.
   * @see #setLeft(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getExclusionExpressionConstraint_Left()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getLeft();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getLeft <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Left</em>' containment reference.
   * @see #getLeft()
   * @generated
   */
  void setLeft(ExpressionConstraint value);

  /**
   * Returns the value of the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Right</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Right</em>' containment reference.
   * @see #setRight(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getExclusionExpressionConstraint_Right()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getRight();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getRight <em>Right</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Right</em>' containment reference.
   * @see #getRight()
   * @generated
   */
  void setRight(ExpressionConstraint value);

} // ExclusionExpressionConstraint
