/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Refined Expression Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getRefinement <em>Refinement</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinedExpressionConstraint()
 * @model
 * @generated
 */
public interface RefinedExpressionConstraint extends ExpressionConstraint
{
  /**
   * Returns the value of the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Constraint</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Constraint</em>' containment reference.
   * @see #setConstraint(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinedExpressionConstraint_Constraint()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getConstraint();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getConstraint <em>Constraint</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Constraint</em>' containment reference.
   * @see #getConstraint()
   * @generated
   */
  void setConstraint(ExpressionConstraint value);

  /**
   * Returns the value of the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Refinement</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Refinement</em>' containment reference.
   * @see #setRefinement(Refinement)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinedExpressionConstraint_Refinement()
   * @model containment="true"
   * @generated
   */
  Refinement getRefinement();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getRefinement <em>Refinement</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Refinement</em>' containment reference.
   * @see #getRefinement()
   * @generated
   */
  void setRefinement(Refinement value);

} // RefinedExpressionConstraint
