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
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Model Element</b></em>'.
 * 
 * @since 4.0 <!-- end-user-doc -->
 *        <p>
 *        The following features are implemented:
 *        <ul>
 *        <li>{@link org.eclipse.emf.cdo.etypes.impl.ModelElementImpl#getAnnotations <em>Annotations</em>}</li>
 *        </ul>
 *        </p>
 * @generated
 */
public abstract class ModelElementImpl extends CDOObjectImpl implements ModelElement
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ModelElementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EtypesPackage.Literals.MODEL_ELEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Annotation> getAnnotations()
  {
    return (EList<Annotation>)eGet(EtypesPackage.Literals.MODEL_ELEMENT__ANNOTATIONS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public Annotation getAnnotation(String source)
  {
    EList<Annotation> annotations = getAnnotations();
    if (annotations != null)
    {
      if (annotations instanceof BasicEList<?>)
      {
        int size = annotations.size();
        if (size > 0)
        {
          Annotation[] annotationArray = (Annotation[])((BasicEList<?>)annotations).data();
          if (source == null)
          {
            for (int i = 0; i < size; ++i)
            {
              Annotation annotation = annotationArray[i];
              if (annotation.getSource() == null)
              {
                return annotation;
              }
            }
          }
          else
          {
            for (int i = 0; i < size; ++i)
            {
              Annotation annotation = annotationArray[i];
              if (source.equals(annotation.getSource()))
              {
                return annotation;
              }
            }
          }
        }
      }
      else
      {
        if (source == null)
        {
          for (Annotation annotation : annotations)
          {
            if (annotation.getSource() == null)
            {
              return annotation;
            }
          }
        }
        else
        {
          for (Annotation annotation : annotations)
          {
            if (source.equals(annotation.getSource()))
            {
              return annotation;
            }
          }
        }
      }
    }

    return null;
  }

} // ModelElementImpl
