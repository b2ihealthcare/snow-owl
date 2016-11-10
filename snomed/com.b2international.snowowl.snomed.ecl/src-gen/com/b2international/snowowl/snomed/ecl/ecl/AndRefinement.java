/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>And Refinement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getLeft <em>Left</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getRight <em>Right</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAndRefinement()
 * @model
 * @generated
 */
public interface AndRefinement extends Refinement
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
   * @see #setLeft(Refinement)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAndRefinement_Left()
   * @model containment="true"
   * @generated
   */
  Refinement getLeft();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getLeft <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Left</em>' containment reference.
   * @see #getLeft()
   * @generated
   */
  void setLeft(Refinement value);

  /**
   * Returns the value of the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Right</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Right</em>' containment reference.
   * @see #setRight(AttributeConstraint)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getAndRefinement_Right()
   * @model containment="true"
   * @generated
   */
  AttributeConstraint getRight();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getRight <em>Right</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Right</em>' containment reference.
   * @see #getRight()
   * @generated
   */
  void setRight(AttributeConstraint value);

} // AndRefinement
