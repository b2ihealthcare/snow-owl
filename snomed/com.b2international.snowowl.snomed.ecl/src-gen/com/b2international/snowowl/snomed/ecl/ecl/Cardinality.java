/**
 */
package com.b2international.snowowl.snomed.ecl.ecl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cardinality</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMin <em>Min</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMax <em>Max</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getCardinality()
 * @model
 * @generated
 */
public interface Cardinality extends EObject
{
  /**
   * Returns the value of the '<em><b>Min</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Min</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Min</em>' attribute.
   * @see #setMin(int)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getCardinality_Min()
   * @model
   * @generated
   */
  int getMin();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMin <em>Min</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Min</em>' attribute.
   * @see #getMin()
   * @generated
   */
  void setMin(int value);

  /**
   * Returns the value of the '<em><b>Max</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Max</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Max</em>' attribute.
   * @see #setMax(int)
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#getCardinality_Max()
   * @model
   * @generated
   */
  int getMax();

  /**
   * Sets the value of the '{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMax <em>Max</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Max</em>' attribute.
   * @see #getMax()
   * @generated
   */
  void setMax(int value);

} // Cardinality
