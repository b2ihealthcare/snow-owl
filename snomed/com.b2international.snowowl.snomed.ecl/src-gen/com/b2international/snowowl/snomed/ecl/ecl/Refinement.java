/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Refinement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement#isReversed <em>Reversed</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement#getComparison <em>Comparison</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinement()
 * @model
 * @generated
 */
public interface Refinement extends EObject
{
  /**
   * Returns the value of the '<em><b>Reversed</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Reversed</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reversed</em>' attribute.
   * @see #setReversed(boolean)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinement_Reversed()
   * @model
   * @generated
   */
  boolean isReversed();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement#isReversed <em>Reversed</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Reversed</em>' attribute.
   * @see #isReversed()
   * @generated
   */
  void setReversed(boolean value);

  /**
   * Returns the value of the '<em><b>Attribute</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute</em>' containment reference.
   * @see #setAttribute(ExpressionConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinement_Attribute()
   * @model containment="true"
   * @generated
   */
  ExpressionConstraint getAttribute();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement#getAttribute <em>Attribute</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute</em>' containment reference.
   * @see #getAttribute()
   * @generated
   */
  void setAttribute(ExpressionConstraint value);

  /**
   * Returns the value of the '<em><b>Comparison</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Comparison</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comparison</em>' containment reference.
   * @see #setComparison(Comparison)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getRefinement_Comparison()
   * @model containment="true"
   * @generated
   */
  Comparison getComparison();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement#getComparison <em>Comparison</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Comparison</em>' containment reference.
   * @see #getComparison()
   * @generated
   */
  void setComparison(Comparison value);

} // Refinement
