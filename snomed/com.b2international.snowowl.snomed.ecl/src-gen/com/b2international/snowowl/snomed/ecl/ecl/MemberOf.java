/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Member Of</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConcept <em>Concept</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getMemberOf()
 * @model
 * @generated
 */
public interface MemberOf extends FocusConcept
{
  /**
   * Returns the value of the '<em><b>Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Concept</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Concept</em>' containment reference.
   * @see #setConcept(FocusConcept)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getMemberOf_Concept()
   * @model containment="true"
   * @generated
   */
  FocusConcept getConcept();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConcept <em>Concept</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Concept</em>' containment reference.
   * @see #getConcept()
   * @generated
   */
  void setConcept(FocusConcept value);

} // MemberOf
