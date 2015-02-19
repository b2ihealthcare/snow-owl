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

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.util.LockTimeoutException;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;

/**
 * @author Simon McDuff
 * @since 4.0
 */
public class CDOLockImpl implements CDOLock
{
  public static final CDOLock NOOP = new NOOPLockImpl();

  private final InternalCDOObject object;

  private final LockType type;

  private final CDOLockOwner owner;

  public CDOLockImpl(InternalCDOObject object, LockType type)
  {
    this.object = object;
    this.type = type;
    owner = CDOLockUtil.createLockOwner(object.cdoView());
  }

  public LockType getType()
  {
    return type;
  }

  public boolean isLocked()
  {
    return object.cdoLockState().isLocked(type, owner, false);
  }

  /**
   * @see org.eclipse.emf.cdo.CDOLock#isLockedByOthers()
   */
  public boolean isLockedByOthers()
  {
    return object.cdoLockState().isLocked(type, owner, true);
  }

  public void lock()
  {
    try
    {
      object.cdoView().lockObjects(Collections.singletonList(object), type, WAIT);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public void lock(long time, TimeUnit unit) throws TimeoutException
  {
    try
    {
      if (!tryLock(time, unit))
      {
        throw new TimeoutException();
      }
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public void lock(long millis) throws TimeoutException
  {
    lock(millis, TimeUnit.MILLISECONDS);
  }

  public boolean tryLock(long millis) throws InterruptedException
  {
    return tryLock(millis, TimeUnit.MILLISECONDS);
  }

  public void lockInterruptibly() throws InterruptedException
  {
    lock();
  }

  public Condition newCondition()
  {
    throw new UnsupportedOperationException();
  }

  public boolean tryLock()
  {
    try
    {
      object.cdoView().lockObjects(Collections.singletonList(object), type, NO_WAIT);
      return true;
    }
    catch (LockTimeoutException ex)
    {
      return false;
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
  {
    try
    {
      object.cdoView().lockObjects(Collections.singletonList(object), type, unit.toMillis(time));
      return true;
    }
    catch (LockTimeoutException ex)
    {
      return false;
    }
  }

  public void unlock()
  {
    object.cdoView().unlockObjects(Collections.singletonList(object), type);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOLock[object={0}, type={1}]", object, type);
  }

  /**
   * @author Simon McDuff
   */
  public static final class NOOPLockImpl implements CDOLock
  {
    private NOOPLockImpl()
    {
    }

    public boolean isLocked()
    {
      return false;
    }

    /**
     * @see org.eclipse.emf.cdo.CDOLock#isLockedByOthers()
     */
    public boolean isLockedByOthers()
    {
      return false;
    }

    public void lock()
    {
      throw new UnsupportedOperationException();
    }

    public void lockInterruptibly() throws InterruptedException
    {
      throw new UnsupportedOperationException();
    }

    public Condition newCondition()
    {
      return null;
    }

    public void lock(long time, TimeUnit unit) throws TimeoutException
    {
      throw new UnsupportedOperationException();
    }

    public void lock(long millis) throws TimeoutException
    {
      throw new UnsupportedOperationException();
    }

    public boolean tryLock(long millis) throws InterruptedException
    {
      return false;
    }

    public boolean tryLock()
    {
      return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
    {
      return false;
    }

    public void unlock()
    {
      throw new UnsupportedOperationException();
    }

    public LockType getType()
    {
      return null;
    }
  }
}
