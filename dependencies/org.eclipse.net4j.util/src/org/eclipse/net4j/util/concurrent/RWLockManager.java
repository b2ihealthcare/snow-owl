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
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Support Multiple reads/no write and upgrade lock from read to write. Many context could request
 * {@link IRWLockManager.LockType#WRITE write} lock at the same time. It will privileges first context that has already
 * a {@link IRWLockManager.LockType#READ read} lock. If no one has any read lock, it's "first come first serve".
 * 
 * @author Simon McDuff
 * @since 2.0
 * @deprecated Use {@link RWOLockManager}
 */
@Deprecated
public class RWLockManager<OBJECT, CONTEXT> extends Lifecycle implements IRWLockManager<OBJECT, CONTEXT>
{
  private LockStrategy<OBJECT, CONTEXT> readLockStrategy = new LockStrategy<OBJECT, CONTEXT>()
  {
    public boolean isLocked(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isReadLock(context);
    }

    public boolean isLockedByOthers(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isReadLockByOthers(context);
    }

    public boolean canObtainLock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.canObtainReadLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> lock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.readLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> unlock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.readUnlock(context);
    }

    @Override
    public String toString()
    {
      return "ReadLockStrategy";
    }
  };

  private LockStrategy<OBJECT, CONTEXT> writeLockStrategy = new LockStrategy<OBJECT, CONTEXT>()
  {
    public boolean isLocked(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isWriteLock(context);
    }

    public boolean isLockedByOthers(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.isWriteLockByOthers(context);
    }

    public boolean canObtainLock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.canObtainWriteLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> lock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.writeLock(context);
    }

    public LockEntry<OBJECT, CONTEXT> unlock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context)
    {
      return entry.writeUnlock(context);
    }

    @Override
    public String toString()
    {
      return "WriteLockStrategy";
    }
  };

  private Map<OBJECT, LockEntry<OBJECT, CONTEXT>> lockEntries = new HashMap<OBJECT, LockEntry<OBJECT, CONTEXT>>();

  private LockChanged lockChanged = new LockChanged();

  /**
   * @since 3.0
   */
  public void lock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout)
      throws InterruptedException
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    lock(lockingStrategy, context, objectsToLock, timeout);
  }

  /**
   * @since 3.0
   */
  public void lock(LockType type, CONTEXT context, OBJECT objectToLock, long timeout) throws InterruptedException
  {
    List<OBJECT> objectsToLock = Collections.singletonList(objectToLock);
    lock(type, context, objectsToLock, timeout);
  }

  /**
   * Attempts to release for a given locktype, context and objects.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking objects without lock.
   * @since 3.0
   */
  public void unlock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock)
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    unlock(lockingStrategy, context, objectsToUnlock);
  }

  /**
   * Attempts to release all locks(read and write) for a given context.
   */
  public void unlock(CONTEXT context)
  {
    synchronized (lockChanged)
    {
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToRemove = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToAdd = new ArrayList<LockEntry<OBJECT, CONTEXT>>();

      for (Entry<OBJECT, LockEntry<OBJECT, CONTEXT>> entry : lockEntries.entrySet())
      {
        LockEntry<OBJECT, CONTEXT> lockedContext = entry.getValue();
        LockEntry<OBJECT, CONTEXT> newEntry = lockedContext.clearLock(context);
        if (newEntry == null)
        {
          lockEntrysToRemove.add(lockedContext);
        }
        else if (newEntry != entry)
        {
          lockEntrysToAdd.add(newEntry);
        }
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToRemove)
      {
        OBJECT object = lockEntry.getObject();
        lockEntries.remove(object);
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToAdd)
      {
        OBJECT object = lockEntry.getObject();
        lockEntries.put(object, lockEntry);
      }

      lockChanged.notifyAll();
    }
  }

  /**
   * @since 3.0
   */
  public boolean hasLock(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    return hasLock(lockingStrategy, context, objectToLock);
  }

  /**
   * @since 3.0
   */
  public boolean hasLockByOthers(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    LockStrategy<OBJECT, CONTEXT> lockingStrategy = getLockingStrategy(type);
    LockEntry<OBJECT, CONTEXT> entry = getLockEntry(objectToLock);
    return entry != null && lockingStrategy.isLockedByOthers(entry, context);
  }

  /**
   * @since 3.1
   */
  protected void handleLockEntries(CONTEXT context, LockEntryHandler<OBJECT, CONTEXT> handler)
  {
    synchronized (lockChanged)
    {
      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntries.values())
      {
        if (context == null || lockEntry.hasContext(context))
        {
          if (!handler.handleLockEntry(lockEntry))
          {
            break;
          }
        }
      }
    }
  }

  /**
   * @since 3.1
   */
  protected LockEntry<OBJECT, CONTEXT> getLockEntry(OBJECT objectToLock)
  {
    synchronized (lockChanged)
    {
      return lockEntries.get(objectToLock);
    }
  }

  /**
   * @since 3.1
   */
  protected LockStrategy<OBJECT, CONTEXT> getLockingStrategy(LockType type)
  {
    if (type == LockType.READ)
    {
      return readLockStrategy;
    }

    if (type == LockType.WRITE)
    {
      return writeLockStrategy;
    }

    throw new IllegalArgumentException("Invalid lock type: " + type);
  }

  /**
   * @since 3.1
   */
  protected void changeContext(CONTEXT oldContext, CONTEXT newContext)
  {
    synchronized (lockChanged)
    {
      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntries.values())
      {
        lockEntry.changeContext(oldContext, newContext);
      }
    }
  }

  /**
   * Attempts to release this lock.
   * <p>
   * If the number of context is now zero then the lock is made available for write lock attempts.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking object not locked.
   */
  private void unlock(LockStrategy<OBJECT, CONTEXT> lockingStrategy, CONTEXT context,
      Collection<? extends OBJECT> objectsToLock)
  {
    synchronized (lockChanged)
    {
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToRemove = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
      List<LockEntry<OBJECT, CONTEXT>> lockEntrysToAdd = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
      for (OBJECT objectToLock : objectsToLock)
      {
        LockEntry<OBJECT, CONTEXT> entry = lockEntries.get(objectToLock);
        if (entry == null)
        {
          throw new IllegalMonitorStateException();
        }

        LockEntry<OBJECT, CONTEXT> newEntry = lockingStrategy.unlock(entry, context);
        if (newEntry == null)
        {
          lockEntrysToRemove.add(entry);
        }
        else if (newEntry != entry)
        {
          lockEntrysToAdd.add(newEntry);
        }
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToRemove)
      {
        OBJECT object = lockEntry.getObject();
        lockEntries.remove(object);
      }

      for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrysToAdd)
      {
        OBJECT object = lockEntry.getObject();
        lockEntries.put(object, lockEntry);
      }

      lockChanged.notifyAll();
    }
  }

  private boolean hasLock(LockStrategy<OBJECT, CONTEXT> lockingStrategy, CONTEXT context, OBJECT objectToLock)
  {
    LockEntry<OBJECT, CONTEXT> entry = getLockEntry(objectToLock);
    return entry != null && lockingStrategy.isLocked(entry, context);
  }

  private void lock(LockStrategy<OBJECT, CONTEXT> lockStrategy, CONTEXT context,
      Collection<? extends OBJECT> objectsToLocks, long timeout) throws InterruptedException
  {
    long startTime = System.currentTimeMillis();
    while (true)
    {
      synchronized (lockChanged)
      {
        OBJECT conflict = obtainLock(lockStrategy, context, objectsToLocks);
        if (conflict == null)
        {
          lockChanged.notifyAll();
          return;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (timeout != WAIT && elapsedTime > timeout)
        {
          throw new TimeoutRuntimeException("Could not lock " + conflict + " within " + timeout + " milli seconds"); //$NON-NLS-1$
        }

        if (timeout == WAIT)
        {
          lockChanged.wait();
        }
        else
        {
          lockChanged.wait(Math.max(1, timeout - elapsedTime));
        }
      }
    }
  }

  private OBJECT obtainLock(LockStrategy<OBJECT, CONTEXT> lockingStrategy, CONTEXT context,
      Collection<? extends OBJECT> objectsToLock)
  {
    List<LockEntry<OBJECT, CONTEXT>> lockEntrys = new ArrayList<LockEntry<OBJECT, CONTEXT>>();
    for (OBJECT objectToLock : objectsToLock)
    {
      LockEntry<OBJECT, CONTEXT> entry = lockEntries.get(objectToLock);
      if (entry == null)
      {
        entry = new NoLockEntry<OBJECT, CONTEXT>(objectToLock);
      }

      if (lockingStrategy.canObtainLock(entry, context))
      {
        lockEntrys.add(entry);
      }
      else
      {
        return objectToLock;
      }
    }

    for (LockEntry<OBJECT, CONTEXT> lockEntry : lockEntrys)
    {
      OBJECT object = lockEntry.getObject();
      LockEntry<OBJECT, CONTEXT> lock = lockingStrategy.lock(lockEntry, context);
      lockEntries.put(object, lock);
    }

    return null;
  }

  /**
   * @author Simon McDuff
   * @since 3.1
   * @deprecated Use {@link RWOLockManager}
   */
  @Deprecated
  protected interface LockStrategy<OBJECT, CONTEXT>
  {
    public boolean isLocked(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context);

    public boolean isLockedByOthers(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context);

    public boolean canObtainLock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context);

    public LockEntry<OBJECT, CONTEXT> lock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context);

    public LockEntry<OBJECT, CONTEXT> unlock(LockEntry<OBJECT, CONTEXT> entry, CONTEXT context);
  }

  /**
   * @author Simon McDuff
   * @since 3.1
   * @deprecated Use {@link RWOLockManager}
   */
  @Deprecated
  protected interface LockEntry<OBJECT, CONTEXT>
  {
    public OBJECT getObject();

    public boolean isReadLock(CONTEXT context);

    public boolean isWriteLock(CONTEXT context);

    public boolean isReadLockByOthers(CONTEXT context);

    public boolean isWriteLockByOthers(CONTEXT context);

    public boolean canObtainReadLock(CONTEXT context);

    public boolean canObtainWriteLock(CONTEXT context);

    public LockEntry<OBJECT, CONTEXT> readLock(CONTEXT context);

    public LockEntry<OBJECT, CONTEXT> writeLock(CONTEXT context);

    public LockEntry<OBJECT, CONTEXT> readUnlock(CONTEXT context);

    public LockEntry<OBJECT, CONTEXT> writeUnlock(CONTEXT context);

    public LockEntry<OBJECT, CONTEXT> clearLock(CONTEXT context);

    /**
     * @since 3.1
     */
    public void changeContext(CONTEXT oldContext, CONTEXT newContext);

    /**
     * @since 3.1
     */
    public boolean hasContext(CONTEXT context);
  }

  /**
   * @author Eike Stepper
   * @since 3.1
   * @deprecated Use {@link RWOLockManager}
   */
  @Deprecated
  protected interface LockEntryHandler<OBJECT, CONTEXT>
  {
    public boolean handleLockEntry(LockEntry<OBJECT, CONTEXT> lockEntry);
  }

  /**
   * @author Simon McDuff
   */
  private static final class ReadLockEntry<OBJECT, CONTEXT> implements LockEntry<OBJECT, CONTEXT>
  {
    private OBJECT object;

    private Set<CONTEXT> contexts = new HashBag<CONTEXT>();

    public ReadLockEntry(OBJECT objectToLock, CONTEXT context)
    {
      this.object = objectToLock;
      contexts.add(context);
    }

    public OBJECT getObject()
    {
      return object;
    }

    public boolean isReadLock(CONTEXT context)
    {
      return contexts.contains(context);
    }

    public boolean isWriteLock(CONTEXT context)
    {
      return false;
    }

    public boolean isReadLockByOthers(CONTEXT context)
    {
      if (contexts.isEmpty())
      {
        return false;
      }

      return contexts.size() > (isReadLock(context) ? 1 : 0);
    }

    public boolean isWriteLockByOthers(CONTEXT context)
    {
      return false;
    }

    public boolean canObtainReadLock(CONTEXT context)
    {
      return true;
    }

    public boolean canObtainWriteLock(CONTEXT context)
    {
      return contexts.size() == 1 && contexts.contains(context);
    }

    public LockEntry<OBJECT, CONTEXT> readLock(CONTEXT context)
    {
      contexts.add(context);
      return this;
    }

    public LockEntry<OBJECT, CONTEXT> writeLock(CONTEXT context)
    {
      return new WriteLockEntry<OBJECT, CONTEXT>(object, context, this);
    }

    public LockEntry<OBJECT, CONTEXT> readUnlock(CONTEXT context)
    {
      contexts.remove(context);
      return contexts.isEmpty() ? null : this;
    }

    public LockEntry<OBJECT, CONTEXT> writeUnlock(CONTEXT context)
    {
      throw new IllegalMonitorStateException();
    }

    public LockEntry<OBJECT, CONTEXT> clearLock(CONTEXT context)
    {
      while (contexts.remove(context))
      {
      }

      return contexts.isEmpty() ? null : this;
    }

    public void changeContext(CONTEXT oldContext, CONTEXT newContext)
    {
      if (contexts.remove(oldContext))
      {
        contexts.add(newContext);
      }
    }

    public boolean hasContext(CONTEXT context)
    {
      return contexts.contains(context);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ReadLockEntry[object={0}, contexts={1}]", object, contexts);
    }
  }

  /**
   * @author Simon McDuff
   */
  private static final class WriteLockEntry<OBJECT, CONTEXT> implements LockEntry<OBJECT, CONTEXT>
  {
    private OBJECT object;

    private CONTEXT context;

    private int count;

    private ReadLockEntry<OBJECT, CONTEXT> readLock;

    public WriteLockEntry(OBJECT object, CONTEXT context, ReadLockEntry<OBJECT, CONTEXT> readLock)
    {
      this.object = object;
      this.context = context;
      this.readLock = readLock;
      this.count = 1;
    }

    public OBJECT getObject()
    {
      return object;
    }

    public boolean isReadLock(CONTEXT context)
    {
      return readLock != null ? readLock.isReadLock(context) : false;
    }

    public boolean isWriteLock(CONTEXT context)
    {
      return ObjectUtil.equals(this.context, context);
    }

    public boolean isReadLockByOthers(CONTEXT context)
    {
      return readLock != null ? readLock.isReadLockByOthers(context) : false;
    }

    public boolean isWriteLockByOthers(CONTEXT context)
    {
      return context != this.context;
    }

    public boolean canObtainWriteLock(CONTEXT context)
    {
      return ObjectUtil.equals(this.context, context);
    }

    public boolean canObtainReadLock(CONTEXT context)
    {
      return ObjectUtil.equals(this.context, context);
    }

    public LockEntry<OBJECT, CONTEXT> readLock(CONTEXT context)
    {
      ReadLockEntry<OBJECT, CONTEXT> lock = getReadLock();
      lock.readLock(context);
      return this;
    }

    public LockEntry<OBJECT, CONTEXT> writeLock(CONTEXT context)
    {
      count++;
      return this;
    }

    public LockEntry<OBJECT, CONTEXT> readUnlock(CONTEXT context)
    {
      if (readLock != null)
      {
        if (readLock.readUnlock(context) == null)
        {
          readLock = null;
        }

        return this;
      }

      throw new IllegalMonitorStateException();
    }

    public LockEntry<OBJECT, CONTEXT> writeUnlock(CONTEXT context)
    {
      return --count <= 0 ? readLock : this;
    }

    public LockEntry<OBJECT, CONTEXT> clearLock(CONTEXT context)
    {
      if (readLock != null)
      {
        if (readLock.clearLock(context) == null)
        {
          readLock = null;
        }
      }

      return ObjectUtil.equals(this.context, context) ? readLock : this;
    }

    public void changeContext(CONTEXT oldContext, CONTEXT newContext)
    {
      if (ObjectUtil.equals(context, oldContext))
      {
        context = newContext;
      }
    }

    public boolean hasContext(CONTEXT context)
    {
      return ObjectUtil.equals(this.context, context);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("WriteLockEntry[object={0}, context={1}, count={2}]", object, context, count);
    }

    private ReadLockEntry<OBJECT, CONTEXT> getReadLock()
    {
      if (readLock == null)
      {
        readLock = new ReadLockEntry<OBJECT, CONTEXT>(object, context);
      }

      return readLock;
    }
  }

  /**
   * @author Simon McDuff
   */
  private static final class NoLockEntry<OBJECT, CONTEXT> implements LockEntry<OBJECT, CONTEXT>
  {
    private OBJECT object;

    public NoLockEntry(OBJECT objectToLock)
    {
      this.object = objectToLock;
    }

    public OBJECT getObject()
    {
      return object;
    }

    public boolean isReadLock(CONTEXT context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isWriteLock(CONTEXT context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isReadLockByOthers(CONTEXT context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isWriteLockByOthers(CONTEXT context)
    {
      throw new UnsupportedOperationException();
    }

    public boolean canObtainWriteLock(CONTEXT context)
    {
      return true;
    }

    public boolean canObtainReadLock(CONTEXT context)
    {
      return true;
    }

    public LockEntry<OBJECT, CONTEXT> readLock(CONTEXT context)
    {
      return new ReadLockEntry<OBJECT, CONTEXT>(object, context);
    }

    public LockEntry<OBJECT, CONTEXT> writeLock(CONTEXT context)
    {
      return new WriteLockEntry<OBJECT, CONTEXT>(object, context, null);
    }

    public LockEntry<OBJECT, CONTEXT> readUnlock(CONTEXT context)
    {
      throw new UnsupportedOperationException();
    }

    public LockEntry<OBJECT, CONTEXT> writeUnlock(CONTEXT context)
    {
      throw new UnsupportedOperationException();
    }

    public LockEntry<OBJECT, CONTEXT> clearLock(CONTEXT context)
    {
      throw new UnsupportedOperationException();
    }

    public void changeContext(CONTEXT oldContext, CONTEXT newContext)
    {
      // Do nothing
    }

    public boolean hasContext(CONTEXT context)
    {
      return false;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("NoLockEntry[object={0}]", object);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LockChanged
  {
  }
}
