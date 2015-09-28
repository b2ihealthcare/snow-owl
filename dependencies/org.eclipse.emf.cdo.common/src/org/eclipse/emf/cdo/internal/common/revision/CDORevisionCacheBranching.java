/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.internal.common.revision;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public class CDORevisionCacheBranching extends CDORevisionCacheAuditing
{
  private Map<CDOID, TypeAndRefCounter> typeMap = new HashMap<CDOID, TypeAndRefCounter>();

  public CDORevisionCacheBranching()
  {
  }

  @Override
  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return new CDORevisionCacheBranching();
  }

  @Override
  public EClass getObjectType(CDOID id)
  {
    synchronized (revisionLists)
    {
      TypeAndRefCounter typeCounter = typeMap.get(id);
      if (typeCounter != null)
      {
        return typeCounter.getType();
      }

      return null;
    }
  }

  @Override
  protected void typeRefIncrease(CDOID id, EClass type)
  {
    TypeAndRefCounter typeCounter = typeMap.get(id);
    if (typeCounter == null)
    {
      typeCounter = new TypeAndRefCounter(type);
      typeMap.put(id, typeCounter);
    }

    typeCounter.increase();
  }

  @Override
  protected void typeRefDecrease(CDOID id)
  {
    TypeAndRefCounter typeCounter = typeMap.get(id);
    if (typeCounter != null && typeCounter.decreaseAndGet() == 0)
    {
      typeMap.remove(id);
    }
  }

  @Override
  protected void typeRefDispose()
  {
    typeMap.clear();
  }

  @Override
  protected boolean isKeyInBranch(Object key, CDOBranch branch)
  {
    return ObjectUtil.equals(((CDOIDAndBranch)key).getBranch(), branch);
  }

  @Override
  protected Object createKey(CDOID id, CDOBranch branch)
  {
    return CDOIDUtil.createIDAndBranch(id, branch);
  }

  /**
   * @author Eike Stepper
   */
  private static final class TypeAndRefCounter
  {
    private EClass type;

    private int refCounter;

    public TypeAndRefCounter(EClass type)
    {
      this.type = type;
    }

    public EClass getType()
    {
      return type;
    }

    public void increase()
    {
      ++refCounter;
    }

    public int decreaseAndGet()
    {
      return --refCounter;
    }
  }
}
