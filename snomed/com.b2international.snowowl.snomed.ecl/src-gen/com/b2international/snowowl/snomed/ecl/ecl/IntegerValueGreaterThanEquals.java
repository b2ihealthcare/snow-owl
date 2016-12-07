/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Integer Value Greater Than Equals</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getIntegerValueGreaterThanEquals()
 * @model
 * @generated
 */
public interface IntegerValueGreaterThanEquals extends DataTypeComparison
{
  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(int)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getIntegerValueGreaterThanEquals_Value()
   * @model
   * @generated
   */
  int getValue();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(int value);

} // IntegerValueGreaterThanEquals
