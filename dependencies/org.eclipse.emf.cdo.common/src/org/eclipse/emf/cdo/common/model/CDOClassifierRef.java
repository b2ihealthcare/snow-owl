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
package org.eclipse.emf.cdo.common.model;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * References an {@link EClassifier}.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOClassifierRef
{
  public static final String URI_SEPARATOR = "#"; //$NON-NLS-1$

  private String packageURI;

  private String classifierName;

  public CDOClassifierRef()
  {
  }

  public CDOClassifierRef(EClassifier classifier)
  {
    this(classifier.getEPackage().getNsURI(), classifier.getName());
  }

  public CDOClassifierRef(String packageURI, String classifierName)
  {
    this.packageURI = packageURI.intern();
    this.classifierName = classifierName.intern();
  }

  /**
   * @since 4.0
   */
  public CDOClassifierRef(String uri)
  {
    if (uri == null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOClassifierRef.1") + uri); //$NON-NLS-1$
    }

    int hash = uri.lastIndexOf(URI_SEPARATOR);
    if (hash == -1)
    {
      throw new IllegalArgumentException(Messages.getString("CDOClassifierRef.1") + uri); //$NON-NLS-1$
    }

    packageURI = uri.substring(0, hash);
    classifierName = uri.substring(hash + 1);
  }

  /**
   * @since 3.0
   */
  public CDOClassifierRef(CDODataInput in) throws IOException
  {
    this(in.readCDOPackageURI());
  }

  /**
   * @since 3.0
   */
  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOPackageURI(getURI());
  }

  /**
   * @since 4.0
   */
  public String getURI()
  {
    return packageURI + URI_SEPARATOR + classifierName;
  }

  public String getPackageURI()
  {
    return packageURI;
  }

  public String getClassifierName()
  {
    return classifierName;
  }

  public EClassifier resolve(EPackage.Registry packageRegistry)
  {
    EPackage ePackage = packageRegistry.getEPackage(packageURI);
    if (ePackage == null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOClassifierRef.0"), packageURI)); //$NON-NLS-1$
    }

    return ePackage.getEClassifier(classifierName);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == CDOClassifierRef.class)
    {
      CDOClassifierRef that = (CDOClassifierRef)obj;
      return ObjectUtil.equals(packageURI, that.packageURI) && ObjectUtil.equals(classifierName, that.classifierName);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return packageURI.hashCode() ^ classifierName.hashCode();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOClassifierRef({0}, {1})", packageURI, classifierName); //$NON-NLS-1$
  }

  /**
   * Provides {@link CDOClassifierRef classifier references}.
   * 
   * @author Eike Stepper
   * @since 3.0
   * @apiviz.uses {@link CDOClassifierRef} - - provides
   */
  public interface Provider
  {
    public CDOClassifierRef getClassifierRef();
  }
}
