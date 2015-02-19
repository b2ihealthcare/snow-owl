/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.etypes.EtypesFactory
 * @model kind="package"
 * @generated
 */
public interface EtypesPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "etypes"; //$NON-NLS-1$

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/Etypes/4.0.0"; //$NON-NLS-1$

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "etypes"; //$NON-NLS-1$

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  EtypesPackage eINSTANCE = org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.etypes.impl.ModelElementImpl <em>Model Element</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.etypes.impl.ModelElementImpl
   * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getModelElement()
   * @generated
   */
  int MODEL_ELEMENT = 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODEL_ELEMENT__ANNOTATIONS = 0;

  /**
   * The number of structural features of the '<em>Model Element</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int MODEL_ELEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl <em>Annotation</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.etypes.impl.AnnotationImpl
   * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getAnnotation()
   * @generated
   */
  int ANNOTATION = 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ANNOTATION__ANNOTATIONS = MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Source</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ANNOTATION__SOURCE = MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Details</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ANNOTATION__DETAILS = MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Model Element</b></em>' container reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ANNOTATION__MODEL_ELEMENT = MODEL_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Contents</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ANNOTATION__CONTENTS = MODEL_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>References</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ANNOTATION__REFERENCES = MODEL_ELEMENT_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Annotation</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ANNOTATION_FEATURE_COUNT = MODEL_ELEMENT_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '<em>Blob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.common.lob.CDOBlob
   * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getBlob()
   * @generated
   */
  int BLOB = 2;

  /**
   * The meta object id for the '<em>Clob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.common.lob.CDOClob
   * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getClob()
   * @generated
   */
  int CLOB = 3;

  /**
   * The meta object id for the '<em>Lob</em>' data type. <!-- begin-user-doc -->
   *
   * @since 4.1 <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.common.lob.CDOLob
   * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getLob()
   * @generated
   */
  int LOB = 4;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.etypes.ModelElement <em>Model Element</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Model Element</em>'.
   * @see org.eclipse.emf.cdo.etypes.ModelElement
   * @generated
   */
  EClass getModelElement();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.etypes.ModelElement#getAnnotations <em>Annotations</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Annotations</em>'.
   * @see org.eclipse.emf.cdo.etypes.ModelElement#getAnnotations()
   * @see #getModelElement()
   * @generated
   */
  EReference getModelElement_Annotations();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.etypes.Annotation <em>Annotation</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Annotation</em>'.
   * @see org.eclipse.emf.cdo.etypes.Annotation
   * @generated
   */
  EClass getAnnotation();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.etypes.Annotation#getSource <em>Source</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Source</em>'.
   * @see org.eclipse.emf.cdo.etypes.Annotation#getSource()
   * @see #getAnnotation()
   * @generated
   */
  EAttribute getAnnotation_Source();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.etypes.Annotation#getDetails <em>Details</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the map '<em>Details</em>'.
   * @see org.eclipse.emf.cdo.etypes.Annotation#getDetails()
   * @see #getAnnotation()
   * @generated
   */
  EReference getAnnotation_Details();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.etypes.Annotation#getModelElement
   * <em>Model Element</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Model Element</em>'.
   * @see org.eclipse.emf.cdo.etypes.Annotation#getModelElement()
   * @see #getAnnotation()
   * @generated
   */
  EReference getAnnotation_ModelElement();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.etypes.Annotation#getContents <em>Contents</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Contents</em>'.
   * @see org.eclipse.emf.cdo.etypes.Annotation#getContents()
   * @see #getAnnotation()
   * @generated
   */
  EReference getAnnotation_Contents();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.etypes.Annotation#getReferences
   * <em>References</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>References</em>'.
   * @see org.eclipse.emf.cdo.etypes.Annotation#getReferences()
   * @see #getAnnotation()
   * @generated
   */
  EReference getAnnotation_References();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.common.lob.CDOBlob <em>Blob</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for data type '<em>Blob</em>'.
   * @see org.eclipse.emf.cdo.common.lob.CDOBlob
   * @model instanceClass="org.eclipse.emf.cdo.common.lob.CDOBlob"
   * @generated
   */
  EDataType getBlob();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.common.lob.CDOClob <em>Clob</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for data type '<em>Clob</em>'.
   * @see org.eclipse.emf.cdo.common.lob.CDOClob
   * @model instanceClass="org.eclipse.emf.cdo.common.lob.CDOClob"
   * @generated
   */
  EDataType getClob();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.common.lob.CDOLob <em>Lob</em>}'. <!--
   * begin-user-doc -->
   *
   * @since 4.1<!-- end-user-doc -->
   * @return the meta object for data type '<em>Lob</em>'.
   * @see org.eclipse.emf.cdo.common.lob.CDOLob
   * @model instanceClass="org.eclipse.emf.cdo.common.lob.CDOLob"
   * @generated
   */
  EDataType getLob();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EtypesFactory getEtypesFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   *
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.etypes.impl.ModelElementImpl <em>Model Element</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.cdo.etypes.impl.ModelElementImpl
     * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getModelElement()
     * @generated
     */
    EClass MODEL_ELEMENT = eINSTANCE.getModelElement();

    /**
     * The meta object literal for the '<em><b>Annotations</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference MODEL_ELEMENT__ANNOTATIONS = eINSTANCE.getModelElement_Annotations();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl <em>Annotation</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.cdo.etypes.impl.AnnotationImpl
     * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getAnnotation()
     * @generated
     */
    EClass ANNOTATION = eINSTANCE.getAnnotation();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EAttribute ANNOTATION__SOURCE = eINSTANCE.getAnnotation_Source();

    /**
     * The meta object literal for the '<em><b>Details</b></em>' map feature. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @generated
     */
    EReference ANNOTATION__DETAILS = eINSTANCE.getAnnotation_Details();

    /**
     * The meta object literal for the '<em><b>Model Element</b></em>' container reference feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference ANNOTATION__MODEL_ELEMENT = eINSTANCE.getAnnotation_ModelElement();

    /**
     * The meta object literal for the '<em><b>Contents</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EReference ANNOTATION__CONTENTS = eINSTANCE.getAnnotation_Contents();

    /**
     * The meta object literal for the '<em><b>References</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EReference ANNOTATION__REFERENCES = eINSTANCE.getAnnotation_References();

    /**
     * The meta object literal for the '<em>Blob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.cdo.common.lob.CDOBlob
     * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getBlob()
     * @generated
     */
    EDataType BLOB = eINSTANCE.getBlob();

    /**
     * The meta object literal for the '<em>Clob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.cdo.common.lob.CDOClob
     * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getClob()
     * @generated
     */
    EDataType CLOB = eINSTANCE.getClob();

    /**
     * The meta object literal for the '<em>Lob</em>' data type. <!-- begin-user-doc -->
     *
     * @since 4.1<!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.common.lob.CDOLob
     * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getLob()
     * @generated
     */
    EDataType LOB = eINSTANCE.getLob();

  }

} // EtypesPackage
