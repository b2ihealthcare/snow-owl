/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Comparison</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.Comparison#getConstraint <em>Constraint</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getComparison()
 * @model
 * @generated
 */
public interface Comparison extends EObject
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
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getComparison_Constraint()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getConstraint();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.Comparison#getConstraint <em>Constraint</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Constraint</em>' containment reference.
   * @see #getConstraint()
   * @generated
   */
  void setConstraint(ExpressionConstraint value);

} // Comparison
