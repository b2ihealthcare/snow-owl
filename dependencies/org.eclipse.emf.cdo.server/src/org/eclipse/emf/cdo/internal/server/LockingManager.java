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
 *    Caspar De Groot - write options
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking2;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.concurrent.RWOLockManager;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.options.IOptionsContainer;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 3.0
 */
public class LockingManager extends RWOLockManager<Object, IView> implements InternalLockManager
{
  private InternalRepository repository;

  private Map<String, InternalView> openViews = new HashMap<String, InternalView>();

  private Map<String, DurableView> durableViews = new HashMap<String, DurableView>();

  private ConcurrentArray<DurableViewHandler> durableViewHandlers = new ConcurrentArray<DurableViewHandler>()
  {
    @Override
    protected DurableViewHandler[] newArray(int length)
    {
      return new DurableViewHandler[length];
    }
  };

  @ExcludeFromDump
  private transient IListener sessionListener = new ContainerEventAdapter<IView>()
  {
    @Override
    protected void onRemoved(IContainer<IView> container, IView view)
    {
      String durableLockingID = view.getDurableLockingID();
      if (durableLockingID == null)
      {
        unlock(view);
      }
      else
      {
        DurableView durableView = new DurableView(durableLockingID);
        changeContext(view, durableView);
        unregisterOpenView(durableLockingID);
        durableViews.put(durableLockingID, durableView);
      }
    }
  };

  @ExcludeFromDump
  private transient IListener sessionManagerListener = new ContainerEventAdapter<ISession>()
  {
    @Override
    protected void onAdded(IContainer<ISession> container, ISession session)
    {
      session.addListener(sessionListener);
    }

    @Override
    protected void onRemoved(IContainer<ISession> container, ISession session)
    {
      session.removeListener(sessionListener);
    }
  };

  public LockingManager()
  {
  }

  public InternalRepository getRepository()
  {
    return repository;
  }

  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;
  }

  public synchronized Object getLockEntryObject(Object key)
  {
    LockState<Object, IView> lockState = getObjectToLocksMap().get(key);
    return lockState == null ? null : lockState.getLockedObject();
  }

  public Object getLockKey(CDOID id, CDOBranch branch)
  {
    if (repository.isSupportingBranches())
    {
      return CDOIDUtil.createIDAndBranch(id, branch);
    }

    return id;
  }

  public synchronized Map<CDOID, LockGrade> getLocks(final IView view)
  {
    final Map<CDOID, LockGrade> result = new HashMap<CDOID, LockGrade>();

    for (LockState<Object, IView> lockState : getObjectToLocksMap().values())
    {
      LockGrade grade = LockGrade.NONE;
      if (lockState.hasLock(LockType.READ, view, false))
      {
        grade = grade.getUpdated(LockType.READ, true);
      }

      if (lockState.hasLock(LockType.WRITE, view, false))
      {
        grade = grade.getUpdated(LockType.WRITE, true);
      }

      if (lockState.hasLock(LockType.OPTION, view, false))
      {
        grade = grade.getUpdated(LockType.OPTION, true);
      }

      if (grade != LockGrade.NONE)
      {
        CDOID id = getLockKeyID(lockState.getLockedObject());
        result.put(id, grade);
      }
    }

    return result;
  }

  @Deprecated
  public void lock(boolean explicit, LockType type, IView view, Collection<? extends Object> objectsToLock, long timeout)
      throws InterruptedException
  {
    lock2(explicit, type, view, objectsToLock, false, timeout);
  }

  public List<LockState<Object, IView>> lock2(boolean explicit, LockType type, IView view,
      Collection<? extends Object> objectsToLock, boolean recursive, long timeout) throws InterruptedException
  {
    String durableLockingID = null;
    DurableLocking accessor = null;

    if (explicit)
    {
      durableLockingID = view.getDurableLockingID();
      if (durableLockingID != null)
      {
        accessor = getDurableLocking();
      }
    }

    long startTime = timeout == WAIT ? 0L : currentTimeMillis();

    List<LockState<Object, IView>> newLockStates;
    synchronized (this)
    {
      if (recursive)
      {
        objectsToLock = createContentSet(objectsToLock, view);
      }

      // Adjust timeout for delay we may have incurred on entering this synchronized block
      if (timeout != WAIT)
      {
        timeout -= currentTimeMillis() - startTime;
      }

      newLockStates = super.lock2(type, view, objectsToLock, timeout);
    }

    if (accessor != null)
    {
      accessor.lock(durableLockingID, type, objectsToLock);
    }

    return newLockStates;
  }

  private Set<? extends Object> createContentSet(Collection<? extends Object> objectsToLock, IView view)
  {
    CDOBranch branch = view.getBranch();
    CDOView cdoView = CDOServerUtil.openView(view.getSession(), branch.getHead(), true);

    Set<Object> contents = new HashSet<Object>();
    for (Object o : objectsToLock)
    {
      contents.add(o);

      boolean isIDandBranch = o instanceof CDOIDAndBranch;
      CDOID id;
      if (isIDandBranch)
      {
        id = ((CDOIDAndBranch)o).getID();
      }
      else
      {
        id = (CDOID)o;
      }

      CDOObject obj = cdoView.getObject(id);
      TreeIterator<EObject> iter = obj.eAllContents();
      while (iter.hasNext())
      {
        EObject eObj = iter.next();
        CDOObject cdoObj = CDOUtil.getCDOObject(eObj);
        CDOID childID = cdoObj.cdoID();
        Object child;
        if (isIDandBranch)
        {
          child = CDOIDUtil.createIDAndBranch(childID, branch);
        }
        else
        {
          child = childID;
        }
        contents.add(child);
      }
    }

    return contents;
  }

  @Deprecated
  public synchronized void unlock(boolean explicit, LockType type, IView view,
      Collection<? extends Object> objectsToUnlock)
  {
    unlock2(explicit, type, view, objectsToUnlock, false);
  }

  public synchronized List<LockState<Object, IView>> unlock2(boolean explicit, LockType type, IView view,
      Collection<? extends Object> objects, boolean recursive)
  {
    List<LockState<Object, IView>> newLockStates;
    synchronized (this)
    {
      if (recursive)
      {
        objects = createContentSet(objects, view);
      }

      newLockStates = super.unlock2(type, view, objects);
    }

    if (explicit)
    {
      String durableLockingID = view.getDurableLockingID();
      if (durableLockingID != null)
      {
        DurableLocking accessor = getDurableLocking();
        accessor.unlock(durableLockingID, type, objects);
      }
    }

    return newLockStates;
  }

  @Deprecated
  public synchronized void unlock(boolean explicit, IView view)
  {
    unlock2(explicit, view);
  }

  public synchronized List<LockState<Object, IView>> unlock2(boolean explicit, IView view)
  {
    if (explicit)
    {
      String durableLockingID = view.getDurableLockingID();
      if (durableLockingID != null)
      {
        DurableLocking accessor = getDurableLocking();
        accessor.unlock(durableLockingID);
      }
    }

    return super.unlock2(view);
  }

  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks)
  {
    return createLockArea(userID, branchPoint, readOnly, locks, null);
  }

  private LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks, String lockAreaID)
  {
    if (lockAreaID == null)
    {
      DurableLocking accessor = getDurableLocking();
      return accessor.createLockArea(userID, branchPoint, readOnly, locks);
    }

    DurableLocking2 accessor = getDurableLocking2();
    return accessor.createLockArea(lockAreaID, userID, branchPoint, readOnly, locks);
  }

  public LockArea createLockArea(InternalView view)
  {
    return createLockArea(view, null);
  }

  public LockArea createLockArea(InternalView view, String lockAreaID)
  {
    String userID = view.getSession().getUserID();
    CDOBranchPoint branchPoint = CDOBranchUtil.copyBranchPoint(view);
    boolean readOnly = view.isReadOnly();
    Map<CDOID, LockGrade> locks = getLocks(view);

    LockArea area = createLockArea(userID, branchPoint, readOnly, locks, lockAreaID);
    synchronized (openViews)
    {
      openViews.put(area.getDurableLockingID(), view);
    }

    return area;
  }

  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException
  {
    DurableLocking accessor = getDurableLocking();
    return accessor.getLockArea(durableLockingID);
  }

  public void getLockAreas(String userIDPrefix, LockArea.Handler handler)
  {
    if (userIDPrefix == null)
    {
      userIDPrefix = "";
    }

    DurableLocking accessor = getDurableLocking();
    accessor.getLockAreas(userIDPrefix, handler);
  }

  public void deleteLockArea(String durableLockingID)
  {
    DurableLocking accessor = getDurableLocking();
    accessor.deleteLockArea(durableLockingID);
    unregisterOpenView(durableLockingID);
  }

  public IView openView(ISession session, int viewID, boolean readOnly, final String durableLockingID)
  {
    synchronized (openViews)
    {
      InternalView view = openViews.get(durableLockingID);
      if (view != null)
      {
        throw new IllegalStateException("Durable view is already open: " + view);
      }

      LockArea area = getLockArea(durableLockingID);
      if (area.isReadOnly() != readOnly)
      {
        throw new IllegalStateException("Durable read-only state does not match the request");
      }

      for (DurableViewHandler handler : durableViewHandlers.get())
      {
        try
        {
          handler.openingView(session, viewID, readOnly, area);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      if (readOnly)
      {
        view = (InternalView)session.openView(viewID, area);
      }
      else
      {
        view = (InternalView)session.openTransaction(viewID, area);
      }

      DurableView durableView = durableViews.get(durableLockingID);
      changeContext(durableView, view);
      view.setDurableLockingID(durableLockingID);
      view.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          synchronized (openViews)
          {
            openViews.remove(durableLockingID);
          }
        }
      });

      openViews.put(durableLockingID, view);
      return view;
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    loadLocks();
    getRepository().getSessionManager().addListener(sessionManagerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    ISessionManager sessionManager = getRepository().getSessionManager();
    sessionManager.removeListener(sessionManagerListener);
    for (ISession session : sessionManager.getSessions())
    {
      session.removeListener(sessionListener);
    }

    super.doDeactivate();
  }

  private DurableLocking getDurableLocking()
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (accessor instanceof DurableLocking)
    {
      return (DurableLocking)accessor;
    }

    throw new IllegalStateException("Store does not implement " + DurableLocking.class.getSimpleName());
  }

  private DurableLocking2 getDurableLocking2()
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (accessor instanceof DurableLocking2)
    {
      return (DurableLocking2)accessor;
    }

    throw new IllegalStateException("Store does not implement " + DurableLocking2.class.getSimpleName());
  }

  public void reloadLocks()
  {
    DurableLockLoader handler = new DurableLockLoader();
    getLockAreas(null, handler);
  }

  private void loadLocks()
  {
    InternalStore store = repository.getStore();
    IStoreAccessor reader = null;

    try
    {
      reader = store.getReader(null);
      if (reader instanceof DurableLocking)
      {
        StoreThreadLocal.setAccessor(reader);
        reloadLocks();
      }
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  private void unregisterOpenView(String durableLockingID)
  {
    synchronized (openViews)
    {
      InternalView view = openViews.remove(durableLockingID);
      if (view != null)
      {
        view.setDurableLockingID(null);
      }
    }
  }

  public CDOID getLockKeyID(Object key)
  {
    if (key instanceof CDOID)
    {
      return (CDOID)key;
    }

    if (key instanceof CDOIDAndBranch)
    {
      return ((CDOIDAndBranch)key).getID();
    }

    throw new ImplementationError("Unexpected lock object: " + key);
  }

  public void addDurableViewHandler(DurableViewHandler handler)
  {
    durableViewHandlers.add(handler);
  }

  public void removeDurableViewHandler(DurableViewHandler handler)
  {
    durableViewHandlers.remove(handler);
  }

  public DurableViewHandler[] getDurableViewHandlers()
  {
    return durableViewHandlers.get();
  }

  /**
   * @author Eike Stepper
   */
  private final class DurableView implements IView, CDOCommonView.Options
  {
    private String durableLockingID;

    public DurableView(String durableLockingID)
    {
      this.durableLockingID = durableLockingID;
    }

    public String getDurableLockingID()
    {
      return durableLockingID;
    }

    public boolean isDurableView()
    {
      return true;
    }

    public int getSessionID()
    {
      throw new UnsupportedOperationException();
    }

    public int getViewID()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isReadOnly()
    {
      throw new UnsupportedOperationException();
    }

    public CDOBranch getBranch()
    {
      throw new UnsupportedOperationException();
    }

    public long getTimeStamp()
    {
      throw new UnsupportedOperationException();
    }

    public CDORevision getRevision(CDOID id)
    {
      throw new UnsupportedOperationException();
    }

    public void close()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isClosed()
    {
      throw new UnsupportedOperationException();
    }

    public IRepository getRepository()
    {
      throw new UnsupportedOperationException();
    }

    public ISession getSession()
    {
      return null;
    }

    @Override
    public int hashCode()
    {
      return durableLockingID.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (obj instanceof DurableView)
      {
        DurableView that = (DurableView)obj;
        return durableLockingID.equals(that.getDurableLockingID());
      }

      return false;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("DurableView[{0}]", durableLockingID);
    }

    public IOptionsContainer getContainer()
    {
      return null;
    }

    public void addListener(IListener listener)
    {
    }

    public void removeListener(IListener listener)
    {
    }

    public boolean hasListeners()
    {
      return false;
    }

    public IListener[] getListeners()
    {
      return null;
    }

    public Options options()
    {
      return this;
    }

    public boolean isLockNotificationEnabled()
    {
      return false;
    }

    public void setLockNotificationEnabled(boolean enabled)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DurableLockLoader implements LockArea.Handler
  {
    public DurableLockLoader()
    {
    }

    private IView getView(String lockAreaID)
    {
      IView view = openViews.get(lockAreaID);
      if (view == null)
      {
        view = durableViews.get(lockAreaID);
      }

      return view;
    }

    public boolean handleLockArea(LockArea area)
    {
      String durableLockingID = area.getDurableLockingID();
      IView view = getView(durableLockingID);
      if (view != null)
      {
        unlock2(view);
      }

      if (view == null)
      {
        view = new DurableView(durableLockingID);
        durableViews.put(durableLockingID, (DurableView)view);
      }

      Collection<Object> readLocks = new ArrayList<Object>();
      Collection<Object> writeLocks = new ArrayList<Object>();
      Collection<Object> writeOptions = new ArrayList<Object>();
      for (Entry<CDOID, LockGrade> entry : area.getLocks().entrySet())
      {
        Object key = getLockKey(entry.getKey(), area.getBranch());
        LockGrade grade = entry.getValue();
        if (grade.isRead())
        {
          readLocks.add(key);
        }

        if (grade.isWrite())
        {
          writeLocks.add(key);
        }

        if (grade.isOption())
        {
          writeOptions.add(key);
        }
      }

      try
      {
        lock(LockType.READ, view, readLocks, 1000L);
        lock(LockType.WRITE, view, writeLocks, 1000L);
        lock(LockType.OPTION, view, writeOptions, 1000L);
      }
      catch (InterruptedException ex)
      {
        throw WrappedException.wrap(ex);
      }

      return true;
    }
  }

  public LockGrade getLockGrade(Object key)
  {
    LockState<Object, IView> lockState = getObjectToLocksMap().get(key);
    LockGrade grade = LockGrade.NONE;
    if (lockState != null)
    {
      for (LockType type : LockType.values())
      {
        if (lockState.hasLock(type))
        {
          grade = grade.getUpdated(type, true);
        }
      }
    }

    return grade;
  }

  private LockArea getLockAreaNoEx(String durableLockingID)
  {
    try
    {
      return getLockArea(durableLockingID);
    }
    catch (LockAreaNotFoundException e)
    {
      return null;
    }
  }

  public void updateLockArea(LockArea lockArea)
  {
    String durableLockingID = lockArea.getDurableLockingID();
    DurableLocking2 accessor = getDurableLocking2();

    if (lockArea.isMissing())
    {
      LockArea localLockArea = getLockAreaNoEx(durableLockingID);
      if (localLockArea != null && localLockArea.getLocks().size() > 0)
      {
        accessor.deleteLockArea(durableLockingID);
        DurableView deletedView = durableViews.remove(durableLockingID);
        CheckUtil.checkNull(deletedView, "deletedView");
      }
    }
    else
    {
      accessor.updateLockArea(lockArea);
      new DurableLockLoader().handleLockArea(lockArea);
    }
  }
}
