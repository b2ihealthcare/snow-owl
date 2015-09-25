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
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache.EvictionEvent;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.net4j.util.event.Event;

/**
 * @author Eike Stepper
 */
public class EvictionEventImpl extends Event implements EvictionEvent
{
  private static final long serialVersionUID = 1L;

  private CDORevisionKey key;

  public EvictionEventImpl(CDORevisionCache cache, CDORevisionKey key)
  {
    super(cache);
    this.key = key;
  }

  @Override
  public CDORevisionCache getSource()
  {
    return (CDORevisionCache)super.getSource();
  }

  public CDOID getID()
  {
    return key.getID();
  }

  public CDOBranch getBranch()
  {
    return key.getBranch();
  }

  public int getVersion()
  {
    return key.getVersion();
  }

  public InternalCDORevision getRevision()
  {
    if (key instanceof InternalCDORevision)
    {
      return (InternalCDORevision)key;
    }

    return null;
  }
}
