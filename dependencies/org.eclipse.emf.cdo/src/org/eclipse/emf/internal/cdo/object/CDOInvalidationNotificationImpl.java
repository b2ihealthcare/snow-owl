/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Simon McDuff
 */
public class CDOInvalidationNotificationImpl implements CDOInvalidationNotification
{
  private EObject eObject;

  public CDOInvalidationNotificationImpl(EObject eObject)
  {
    this.eObject = CDOUtil.getEObject(eObject);
  }

  public Object getNotifier()
  {
    return eObject;
  }

  public int getEventType()
  {
    return INVALIDATE;
  }

  public Object getFeature()
  {
    return null;
  }

  public int getFeatureID(Class<?> expectedClass)
  {
    return NO_FEATURE_ID;
  }

  public int getPosition()
  {
    return NO_INDEX;
  }

  public boolean wasSet()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isReset()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isTouch()
  {
    return false;
  }

  public boolean merge(Notification notification)
  {
    throw new UnsupportedOperationException();
  }

  public boolean getNewBooleanValue()
  {
    throw new UnsupportedOperationException();
  }

  public byte getNewByteValue()
  {
    throw new UnsupportedOperationException();
  }

  public char getNewCharValue()
  {
    throw new UnsupportedOperationException();
  }

  public double getNewDoubleValue()
  {
    throw new UnsupportedOperationException();
  }

  public float getNewFloatValue()
  {
    throw new UnsupportedOperationException();
  }

  public int getNewIntValue()
  {
    throw new UnsupportedOperationException();
  }

  public long getNewLongValue()
  {
    throw new UnsupportedOperationException();
  }

  public short getNewShortValue()
  {
    throw new UnsupportedOperationException();
  }

  public String getNewStringValue()
  {
    throw new UnsupportedOperationException();
  }

  public Object getNewValue()
  {
    throw new UnsupportedOperationException();
  }

  public boolean getOldBooleanValue()
  {
    throw new UnsupportedOperationException();
  }

  public byte getOldByteValue()
  {
    throw new UnsupportedOperationException();
  }

  public char getOldCharValue()
  {
    throw new UnsupportedOperationException();
  }

  public double getOldDoubleValue()
  {
    throw new UnsupportedOperationException();
  }

  public float getOldFloatValue()
  {
    throw new UnsupportedOperationException();
  }

  public int getOldIntValue()
  {
    throw new UnsupportedOperationException();
  }

  public long getOldLongValue()
  {
    throw new UnsupportedOperationException();
  }

  public short getOldShortValue()
  {
    throw new UnsupportedOperationException();
  }

  public String getOldStringValue()
  {
    throw new UnsupportedOperationException();
  }

  public Object getOldValue()
  {
    throw new UnsupportedOperationException();
  }
}
