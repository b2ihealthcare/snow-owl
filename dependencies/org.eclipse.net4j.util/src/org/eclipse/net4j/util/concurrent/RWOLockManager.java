/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of locks on objects. Locks are owned by contexts. A particular combination of locks and their owners, for
 * a given object, is represented by instances of the {@link LockState} class. This class is also repsonsible for
 * deciding whether or not a new lock can be granted, based on the locks already present.
 * 
 * @author Caspar De Groot
 * @since 3.2
 */
public class RWOLockManager<OBJECT, CONTEXT> extends Lifecycle implements IRWOLockManager<OBJECT, CONTEXT>
{
  private final List<LockState<OBJECT, CONTEXT>> EMPTY_RESULT = Collections.emptyList();

  private final Map<OBJECT, LockState<OBJECT, CONTEXT>> objectToLockStateMap = createObjectToLocksMap();

  /**
   * A mapping of contexts (owners of locks) to the lock states that they are involved in. Here, an 'involvement' means
   * that the context owns at least one lock on the object that the lock state is for. To determine exactly what kind of
   * lock, the lock state object obtained from this map must be queried.
   * <p>
   * This map is a performance optimization to avoid having to scan all lock states.
   */
  private final Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> contextToLockStates = createContextToLocksMap();

  public void lock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout)
      throws InterruptedException
  {
    lock2(type, context, objectsToLock, timeout);
  }

  public List<LockState<OBJECT, CONTEXT>> lock2(LockType type, CONTEXT context,
      Collection<? extends OBJECT> objectsToLock, long timeout) throws InterruptedException
  {
    if (objectsToLock.isEmpty())
    {
      return EMPTY_RESULT;
    }

    // Must come before the synchronized block!
    long startTime = timeout == WAIT ? 0L : currentTimeMillis();

    // Do not synchronize the entire method as it would corrupt the timeout!
    synchronized (this)
    {
      int count = objectsToLock.size();
      List<LockState<OBJECT, CONTEXT>> lockStates = new ArrayList<LockState<OBJECT, CONTEXT>>(count);

      for (;;)
      {
    	lockStates.clear(); // B2i bugfix
        if (canLockInContext(type, context, objectsToLock, lockStates))
        {
          for (int i = 0; i < count; i++)
          {
            LockState<OBJECT, CONTEXT> lockState = lockStates.get(i);
            lockState.lock(type, context);
            addContextToLockStateMapping(context, lockState);
          }

          return lockStates;
        }

        wait(startTime, timeout);
      }
    }
  }

  public void lock(LockType type, CONTEXT context, OBJECT objectToLock, long timeout) throws InterruptedException
  {
    // Do not synchronize the entire method as it would corrupt the timeout!
    lock(type, context, Collections.singleton(objectToLock), timeout);
  }

  public synchronized void unlock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock)
  {
    unlock2(type, context, objectsToUnlock);
  }

  public synchronized List<LockState<OBJECT, CONTEXT>> unlock2(CONTEXT context,
      Collection<? extends OBJECT> objectsToUnlock)
  {
    if (objectsToUnlock.isEmpty())
    {
      return EMPTY_RESULT;
    }

    Set<LockState<OBJECT, CONTEXT>> lockStates = new HashSet<LockState<OBJECT, CONTEXT>>();

    Iterator<? extends OBJECT> it = objectsToUnlock.iterator();
    while (it.hasNext())
    {
      OBJECT o = it.next();
      LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(o);

      if (lockState == null)
      {
        continue;
      }

      for (LockType lockType : LockType.values())
      {
        while (lockState.canUnlock(lockType, context))
        {
          lockState.unlock(lockType, context);
          lockStates.add(lockState);
        }
      }
    }

    for (LockState<OBJECT, CONTEXT> lockState : lockStates)
    {
      removeLockStateForContext(context, lockState);

      if (lockState.hasNoLocks())
      {
        objectToLockStateMap.remove(lockState.getLockedObject());
      }
    }

    notifyAll();

    return new LinkedList<RWOLockManager.LockState<OBJECT, CONTEXT>>(lockStates);
  }

  public synchronized List<LockState<OBJECT, CONTEXT>> unlock2(LockType type, CONTEXT context,
      Collection<? extends OBJECT> objectsToUnlock)
  {
    if (objectsToUnlock.isEmpty())
    {
      return EMPTY_RESULT;
    }

    List<LockState<OBJECT, CONTEXT>> lockStates = new LinkedList<LockState<OBJECT, CONTEXT>>();

    Iterator<? extends OBJECT> it = objectsToUnlock.iterator();
    while (it.hasNext())
    {
      OBJECT o = it.next();
      LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(o);
      if (lockState == null || !lockState.canUnlock(type, context))
      {
        throw new IllegalMonitorStateException();
      }

      lockStates.add(lockState);
    }

    for (LockState<OBJECT, CONTEXT> lockState : lockStates)
    {
      lockState.unlock(type, context);
      if (!lockState.hasLocks(context))
      {
        removeLockStateForContext(context, lockState);
      }

      if (lockState.hasNoLocks())
      {
        objectToLockStateMap.remove(lockState.getLockedObject());
      }
    }

    notifyAll();

    return lockStates;
  }

  public synchronized void unlock(CONTEXT context)
  {
    unlock2(context);
  }

  public synchronized List<LockState<OBJECT, CONTEXT>> unlock2(CONTEXT context)
  {
    Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.get(context);
    if (lockStates == null)
    {
      return EMPTY_RESULT;
    }

    List<OBJECT> objectsWithoutLocks = new LinkedList<OBJECT>();

    for (LockState<OBJECT, CONTEXT> lockState : lockStates)
    {
      for (LockType lockType : LockType.values())
      {
        if (lockState.hasLock(lockType, context, false))
        {
          lockState.unlock(lockType, context);
        }
      }

      if (lockState.hasNoLocks())
      {
        OBJECT o = lockState.getLockedObject();
        objectsWithoutLocks.add(o);
      }
    }

    contextToLockStates.remove(context);

    // This must be done outside the above iteration, in order to avoid ConcurrentModEx
    for (OBJECT o : objectsWithoutLocks)
    {
      objectToLockStateMap.remove(o);
    }

    notifyAll();

    return toList(lockStates);
  }

  @SuppressWarnings("unchecked")
  private List<LockState<OBJECT, CONTEXT>> toList(Set<LockState<OBJECT, CONTEXT>> lockStates)
  {
    if (lockStates instanceof List)
    {
      return (List<LockState<OBJECT, CONTEXT>>)lockStates;
    }

    List<LockState<OBJECT, CONTEXT>> list = new LinkedList<LockState<OBJECT, CONTEXT>>();
    for (LockState<OBJECT, CONTEXT> lockState : lockStates)
    {
      list.add(lockState);
    }

    return list;
  }

  public synchronized boolean hasLock(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(objectToLock);
    return lockState != null && lockState.hasLock(type, context, false);
  }

  public synchronized boolean hasLockByOthers(LockType type, CONTEXT context, OBJECT objectToLock)
  {
    LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(objectToLock);
    return lockState != null && lockState.hasLock(type, context, true);
  }

  protected synchronized void changeContext(CONTEXT oldContext, CONTEXT newContext)
  {
    for (LockState<OBJECT, CONTEXT> lockState : objectToLockStateMap.values())
    {
      lockState.replaceContext(oldContext, newContext);
    }

    Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.remove(oldContext);
    if (lockStates != null)
    {
      contextToLockStates.put(newContext, lockStates);
    }
  }

  protected long currentTimeMillis()
  {
    return System.currentTimeMillis();
  }

  protected Map<OBJECT, LockState<OBJECT, CONTEXT>> createObjectToLocksMap()
  {
    return new HashMap<OBJECT, LockState<OBJECT, CONTEXT>>();
  }

  protected Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> createContextToLocksMap()
  {
    return new HashMap<CONTEXT, Set<LockState<OBJECT, CONTEXT>>>();
  }

  /**
   * All access to the returned map must be properly synchronized on this {@link RWOLockManager}.
   */
  protected final Map<OBJECT, LockState<OBJECT, CONTEXT>> getObjectToLocksMap()
  {
    return objectToLockStateMap;
  }

  /**
   * All access to the returned map must be properly synchronized on this {@link RWOLockManager}.
   */
  protected final Map<CONTEXT, Set<LockState<OBJECT, CONTEXT>>> getContextToLocksMap()
  {
    return contextToLockStates;
  }

  public LockState<OBJECT, CONTEXT> getLockState(OBJECT key)
  {
    return objectToLockStateMap.get(key);
  }

  public synchronized void setLockState(OBJECT key, LockState<OBJECT, CONTEXT> lockState)
  {
    objectToLockStateMap.put(key, lockState);

    for (CONTEXT readLockOwner : lockState.getReadLockOwners())
    {
      addContextToLockStateMapping(readLockOwner, lockState);
    }
    CONTEXT writeLockOwner = lockState.getWriteLockOwner();
    if (writeLockOwner != null)
    {
      addContextToLockStateMapping(writeLockOwner, lockState);
    }
    CONTEXT writeOptionOwner = lockState.getWriteOptionOwner();
    if (writeOptionOwner != null)
    {
      addContextToLockStateMapping(writeOptionOwner, lockState);
    }
  }

  private LockState<OBJECT, CONTEXT> getOrCreateLockState(OBJECT o)
  {
    LockState<OBJECT, CONTEXT> lockState = objectToLockStateMap.get(o);
    if (lockState == null)
    {
      lockState = new LockState<OBJECT, CONTEXT>(o);
      objectToLockStateMap.put(o, lockState);
    }

    return lockState;
  }

  private boolean canLockInContext(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock,
      List<LockState<OBJECT, CONTEXT>> lockStatesToFill)
  {
    Iterator<? extends OBJECT> it = objectsToLock.iterator();
    for (int i = 0; i < objectsToLock.size(); i++)
    {
      OBJECT o = it.next();
      LockState<OBJECT, CONTEXT> lockState = getOrCreateLockState(o);
      if (!lockState.canLock(type, context))
      {
        return false;
      }

      lockStatesToFill.add(lockState);
    }

    return true;
  }

  private void addContextToLockStateMapping(CONTEXT context, LockState<OBJECT, CONTEXT> lockState)
  {
    Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.get(context);
    if (lockStates == null)
    {
      lockStates = new HashSet<LockState<OBJECT, CONTEXT>>();
      contextToLockStates.put(context, lockStates);
    }

    lockStates.add(lockState);
  }

  /**
   * Removes a lockState from the set of all lockStates that the given context is involved in. If the lockState being
   * removed is the last one for the given context, then the set becomes empty, and is therefore removed from the
   * contextToLockStates mp.
   */
  private void removeLockStateForContext(CONTEXT context, LockState<OBJECT, CONTEXT> lockState)
  {
    Set<LockState<OBJECT, CONTEXT>> lockStates = contextToLockStates.get(context);
    lockStates.remove(lockState);
    if (lockStates.isEmpty())
    {
      contextToLockStates.remove(context);
    }
  }

  private void wait(long startTime, long timeout) throws InterruptedException
  {
    if (timeout == WAIT)
    {
      wait();
    }
    else
    {
      long elapsedTime = currentTimeMillis() - startTime;
      long waitTime = timeout - elapsedTime;
      if (waitTime < 1)
      {
        throw new TimeoutRuntimeException("Could not lock objects within " + timeout + " milli seconds");
      }

      wait(waitTime);
    }
  }

  /**
   * Represents a combination of locks for one OBJECT. The different lock types are represented by the values of the
   * enum {@link IRWLockManager.LockType}
   * <p>
   * The locking semantics established by this class are as follows:
   * <li>a read lock prevents a write lock by another, but allows read locks by others and allows a write option by
   * another, and is therefore <b>non-exclusive</b></li>
   * <li>a write lock prevents read locks by others, a write lock by another, and a write option by another, and is
   * therefore <b>exclusive</b></li>
   * <li>a write option prevents write locks by others and a write option by another, but allows read locks by others,
   * and is therefore <b>exclusive</b></li>
   * <p>
   * 
   * @author Caspar De Groot
   * @since 3.2
   */
  public static class LockState<OBJECT, CONTEXT>
  {
    private final OBJECT lockedObject;

    private final HashBag<CONTEXT> readLockOwners = new HashBag<CONTEXT>();

    private CONTEXT writeLockOwner;

    private CONTEXT writeOptionOwner;

    private int writeLockCounter;

    LockState(OBJECT lockedObject)
    {
      CheckUtil.checkArg(lockedObject, "lockedObject");
      this.lockedObject = lockedObject;
    }

    public OBJECT getLockedObject()
    {
      return lockedObject;
    }

    public boolean hasLock(org.eclipse.net4j.util.concurrent.IRWLockManager.LockType type, CONTEXT view,
        boolean byOthers)
    {
      CheckUtil.checkArg(view, "view");

      switch (type)
      {
      case READ:
        if (byOthers)
        {
          return readLockOwners.size() > 1 || readLockOwners.size() == 1 && !readLockOwners.contains(view);
        }

        return readLockOwners.contains(view);

      case WRITE:
        if (byOthers)
        {
          return writeLockOwner != null && writeLockOwner != view;
        }

        return writeLockOwner == view;

      case OPTION:
        if (byOthers)
        {
          return writeOptionOwner != null && writeOptionOwner != view;
        }

        return writeOptionOwner == view;
      }

      return false;
    }

    public boolean hasLock(org.eclipse.net4j.util.concurrent.IRWLockManager.LockType type)
    {
      switch (type)
      {
      case READ:
        return readLockOwners.size() > 0;

      case WRITE:
        return writeLockOwner != null;

      case OPTION:
        return writeOptionOwner != null;
      }

      return false;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder("LockState[target=");
      builder.append(lockedObject);

      if (readLockOwners.size() > 0)
      {
        builder.append(", read=");
        boolean first = true;
        for (CONTEXT view : readLockOwners)
        {
          if (first)
          {
            first = false;
          }
          else
          {
            builder.append(", ");
          }

          builder.append(view);
        }

        builder.deleteCharAt(builder.length() - 1);
      }

      if (writeLockOwner != null)
      {
        builder.append(", write=");
        builder.append(writeLockOwner);
      }

      if (writeOptionOwner != null)
      {
        builder.append(", option=");
        builder.append(writeOptionOwner);
      }

      builder.append(']');
      return builder.toString();
    }

    void lock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        doReadLock(context);
        return;

      case WRITE:
        doWriteLock(context);
        return;

      case OPTION:
        doWriteOption(context);
        return;
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    boolean canLock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        return canReadLock(context);

      case WRITE:
        return canWriteLock(context);

      case OPTION:
        return canWriteOption(context);
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    boolean canUnlock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        return canReadUnlock(context);

      case WRITE:
        return canWriteUnlock(context);

      case OPTION:
        return canWriteUnoption(context);
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    void unlock(LockType type, CONTEXT context)
    {
      CheckUtil.checkArg(context, "context");
      switch (type)
      {
      case READ:
        doReadUnlock(context);
        return;

      case WRITE:
        doWriteUnlock(context);
        return;

      case OPTION:
        doWriteUnoption(context);
        return;
      }

      throw new AssertionError("Unknown lock type " + type);
    }

    void replaceContext(CONTEXT oldContext, CONTEXT newContext)
    {
      int readLocksOwnedByOldView = readLockOwners.getCounterFor(oldContext);
      if (readLocksOwnedByOldView > 0)
      {
        for (int i = 0; i < readLocksOwnedByOldView; i++)
        {
          readLockOwners.remove(oldContext);
          readLockOwners.add(newContext);
        }
      }

      if (ObjectUtil.equals(writeLockOwner, oldContext))
      {
        writeLockOwner = newContext;
      }

      if (ObjectUtil.equals(writeOptionOwner, oldContext))
      {
        writeOptionOwner = newContext;
      }
    }

    boolean hasNoLocks()
    {
      return readLockOwners.isEmpty() && writeLockOwner == null && writeOptionOwner == null;
    }

    boolean hasLocks(CONTEXT context)
    {
      return readLockOwners.contains(context) || writeLockOwner == context || writeOptionOwner == context;
    }

    private boolean canReadLock(CONTEXT context)
    {
      if (writeLockOwner != null && writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doReadLock(CONTEXT context)
    {
      readLockOwners.add(context);
    }

    private boolean canWriteLock(CONTEXT context)
    {
      // If another context owns a writeLock, we can't write-lock
      if (writeLockOwner != null && writeLockOwner != context)
      {
        return false;
      }

      // If another context owns a writeOption, we can't write-lock
      if (writeOptionOwner != null && writeOptionOwner != context)
      {
        return false;
      }

      // If another context owns a readLock, we can't write-lock
      if (readLockOwners.size() > 1)
      {
        return false;
      }

      if (readLockOwners.size() == 1)
      {
        if (!readLockOwners.contains(context))
        {
          return false;
        }
      }

      return true;
    }

    private void doWriteLock(CONTEXT context)
    {
      writeLockOwner = context;
      writeLockCounter++;
    }

    private boolean canWriteOption(CONTEXT context)
    {
      if (writeOptionOwner != null && writeOptionOwner != context)
      {
        return false;
      }

      if (writeLockOwner != null && writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doWriteOption(CONTEXT context)
    {
      writeOptionOwner = context;
    }

    private boolean canReadUnlock(CONTEXT context)
    {
      if (!readLockOwners.contains(context))
      {
        return false;
      }

      return true;
    }

    private void doReadUnlock(CONTEXT context)
    {
      readLockOwners.remove(context);
    }

    private boolean canWriteUnlock(CONTEXT context)
    {
      if (writeLockOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doWriteUnlock(CONTEXT context)
    {
      writeLockCounter--;
      if (writeLockCounter == 0)
      {
        writeLockOwner = null;
      }
    }

    private boolean canWriteUnoption(CONTEXT context)
    {
      if (writeOptionOwner != context)
      {
        return false;
      }

      return true;
    }

    private void doWriteUnoption(CONTEXT context)
    {
      writeOptionOwner = null;
    }

    public Set<CONTEXT> getReadLockOwners()
    {
      return Collections.unmodifiableSet(readLockOwners);
    }

    public CONTEXT getWriteLockOwner()
    {
      return writeLockOwner;
    }

    public CONTEXT getWriteOptionOwner()
    {
      return writeOptionOwner;
    }
  }
}
