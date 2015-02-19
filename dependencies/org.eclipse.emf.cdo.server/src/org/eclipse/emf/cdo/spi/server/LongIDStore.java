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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class LongIDStore extends Store
{
  /**
   * @since 3.0
   */
  public static final Set<ObjectType> OBJECT_ID_TYPES = Collections.singleton(CDOID.ObjectType.LONG);

  /**
   * @since 3.0
   */
  public static final long NULL = CDOIDUtil.getLong(CDOID.NULL);

  @ExcludeFromDump
  private transient AtomicLong lastObjectID = new AtomicLong();

  @ExcludeFromDump
  private transient AtomicLong nextLocalObjectID = new AtomicLong(Long.MAX_VALUE);

  public LongIDStore(String type, Set<ChangeFormat> supportedChangeFormats,
      Set<RevisionTemporality> supportedRevisionTemporalities, Set<RevisionParallelism> supportedRevisionParallelisms)
  {
    super(type, OBJECT_ID_TYPES, supportedChangeFormats, supportedRevisionTemporalities, supportedRevisionParallelisms);
  }

  /**
   * @since 4.0
   */
  public CDOID createObjectID(String val)
  {
    Long id = Long.valueOf(val);
    return CDOIDUtil.createLong(id);
  }

  public long getLastObjectID()
  {
    return lastObjectID.get();
  }

  public void setLastObjectID(long lastObjectID)
  {
    this.lastObjectID.set(lastObjectID);
  }

  /**
   * @since 3.0
   */
  public long getNextLocalObjectID()
  {
    return nextLocalObjectID.get();
  }

  /**
   * @since 3.0
   */
  public void setNextLocalObjectID(long nextLocalObjectID)
  {
    this.nextLocalObjectID.set(nextLocalObjectID);
  }

  /**
   * @since 4.0
   */
  public CDOID getNextCDOID(LongIDStoreAccessor accessor, CDORevision revision)
  {
    if (revision.getBranch().isLocal())
    {
      return CDOIDUtil.createLong(nextLocalObjectID.getAndDecrement());
    }

    return CDOIDUtil.createLong(lastObjectID.incrementAndGet());
  }

  /**
   * @since 4.0
   */
  public boolean isLocal(CDOID id)
  {
    long value = CDOIDUtil.getLong(id);
    return value > nextLocalObjectID.get();
  }

  /**
   * @since 4.0
   */
  public void ensureLastObjectID(CDOID id)
  {
    long addedID = CDOIDUtil.getLong(id);
    if (addedID > getLastObjectID())
    {
      setLastObjectID(addedID);
    }
  }
}
