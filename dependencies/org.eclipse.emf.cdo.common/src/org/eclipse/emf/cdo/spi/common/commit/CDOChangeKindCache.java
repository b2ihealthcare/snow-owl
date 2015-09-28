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
package org.eclipse.emf.cdo.spi.common.commit;

import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeKindProvider;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOChangeKindCache extends HashMap<CDOID, CDOChangeKind> implements CDOChangeKindProvider
{
  private static final long serialVersionUID = 1L;

  public CDOChangeKindCache(CDOChangeSetData changeSetData)
  {
    List<CDOIDAndVersion> newObjects = changeSetData.getNewObjects();
    if (newObjects != null)
    {
      for (CDOIDAndVersion key : newObjects)
      {
        put(key.getID(), CDOChangeKind.NEW);
      }
    }

    List<CDORevisionKey> changedObjects = changeSetData.getChangedObjects();
    if (changedObjects != null)
    {
      for (CDOIDAndVersion key : changedObjects)
      {
        put(key.getID(), CDOChangeKind.CHANGED);
      }
    }

    List<CDOIDAndVersion> detachedObjects = changeSetData.getDetachedObjects();
    if (detachedObjects != null)
    {
      for (CDOIDAndVersion key : detachedObjects)
      {
        put(key.getID(), CDOChangeKind.DETACHED);
      }
    }
  }

  public CDOChangeKind getChangeKind(CDOID id)
  {
    return get(id);
  }
}
