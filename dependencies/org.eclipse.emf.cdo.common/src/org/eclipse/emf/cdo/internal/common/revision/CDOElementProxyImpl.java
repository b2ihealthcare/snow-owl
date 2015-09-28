/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.internal.common.revision;

import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.revision.CDOElementProxy;

/**
 * @author Eike Stepper
 */
public final class CDOElementProxyImpl implements CDOElementProxy
{
  private int index;

  public CDOElementProxyImpl(int index)
  {
    this.index = index;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOElementProxy[{0}]", index); //$NON-NLS-1$
  }
}
