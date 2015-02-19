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

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;

import org.eclipse.emf.ecore.EClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Store extends Lifecycle implements InternalStore
{
  /**
   * @since 3.0
   * @deprecated Use CDOBranchPoint.UNSPECIFIED_DATE
   */
  @Deprecated
  public static final long UNSPECIFIED_DATE = CDOBranchPoint.UNSPECIFIED_DATE;

  private String type;

  private Set<ObjectType> objectIDTypes;

  private Set<ChangeFormat> supportedChangeFormats;

  private Set<RevisionTemporality> supportedRevisionTemporalities;

  private Set<RevisionParallelism> supportedRevisionParallelisms;

  private RevisionTemporality revisionTemporality = RevisionTemporality.NONE;

  private RevisionParallelism revisionParallelism = RevisionParallelism.NONE;

  private InternalRepository repository;

  private boolean dropAllDataOnActivate;

  /**
   * Is protected against concurrent thread access through {@link Repository#createBranchLock}.
   */
  @ExcludeFromDump
  private transient int lastBranchID;

  /**
   * Is protected against concurrent thread access through {@link Repository#createBranchLock}.
   */
  @ExcludeFromDump
  private transient int lastLocalBranchID;

  @ExcludeFromDump
  private transient long lastCommitTime;

  @ExcludeFromDump
  private transient Object lastCommitTimeLock = new Object();

  @ExcludeFromDump
  private transient long lastNonLocalCommitTime;

  @ExcludeFromDump
  private transient Object lastNonLocalCommitTimeLock = new Object();

  /**
   * @since 3.0
   */
  @ExcludeFromDump
  private transient ProgressDistributor indicatingCommitDistributor = new ProgressDistributor.Geometric()
  {
    @Override
    public String toString()
    {
      String result = "indicatingCommitDistributor"; //$NON-NLS-1$
      if (repository != null)
      {
        result += ": " + repository.getName(); //$NON-NLS-1$
      }

      return result;
    }
  };

  /**
   * @since 3.0
   */
  public Store(String type, Set<CDOID.ObjectType> objectIDTypes, Set<ChangeFormat> supportedChangeFormats,
      Set<RevisionTemporality> supportedRevisionTemporalities, Set<RevisionParallelism> supportedRevisionParallelisms)
  {
    checkArg(!StringUtil.isEmpty(type), "Empty type"); //$NON-NLS-1$
    this.type = type;
    this.objectIDTypes = objectIDTypes;

    checkArg(supportedChangeFormats != null && !supportedChangeFormats.isEmpty(), "Empty supportedChangeFormats"); //$NON-NLS-1$
    this.supportedChangeFormats = supportedChangeFormats;

    checkArg(supportedRevisionTemporalities != null && !supportedRevisionTemporalities.isEmpty(),
        "Empty supportedRevisionTemporalities"); //$NON-NLS-1$
    this.supportedRevisionTemporalities = supportedRevisionTemporalities;

    checkArg(supportedRevisionParallelisms != null && !supportedRevisionParallelisms.isEmpty(),
        "Empty supportedRevisionParallelisms"); //$NON-NLS-1$
    this.supportedRevisionParallelisms = supportedRevisionParallelisms;
  }

  public final String getType()
  {
    return type;
  }

  /**
   * @since 3.0
   */
  public Set<CDOID.ObjectType> getObjectIDTypes()
  {
    return objectIDTypes;
  }

  /**
   * @since 4.0
   */
  protected void setObjectIDTypes(Set<ObjectType> objectIDTypes)
  {
    this.objectIDTypes = objectIDTypes;
  }

  public Set<ChangeFormat> getSupportedChangeFormats()
  {
    return supportedChangeFormats;
  }

  public Set<RevisionTemporality> getSupportedRevisionTemporalities()
  {
    return supportedRevisionTemporalities;
  }

  public final Set<RevisionParallelism> getSupportedRevisionParallelisms()
  {
    return supportedRevisionParallelisms;
  }

  public RevisionTemporality getRevisionTemporality()
  {
    return revisionTemporality;
  }

  public void setRevisionTemporality(RevisionTemporality revisionTemporality)
  {
    checkInactive();
    checkState(supportedRevisionTemporalities.contains(revisionTemporality), "Revision temporality not supported: " //$NON-NLS-1$
        + revisionTemporality);
    this.revisionTemporality = revisionTemporality;
  }

  public RevisionParallelism getRevisionParallelism()
  {
    return revisionParallelism;
  }

  public void setRevisionParallelism(RevisionParallelism revisionParallelism)
  {
    checkInactive();
    checkState(supportedRevisionParallelisms.contains(revisionParallelism), "Revision parallelism not supported: " //$NON-NLS-1$
        + revisionParallelism);
    this.revisionParallelism = revisionParallelism;
  }

  /**
   * @since 3.0
   */
  public InternalRepository getRepository()
  {
    return repository;
  }

  public void setRepository(IRepository repository)
  {
    this.repository = (InternalRepository)repository;
  }

  /**
   * @since 4.0
   */
  public boolean isDropAllDataOnActivate()
  {
    return dropAllDataOnActivate;
  }

  /**
   * @since 4.0
   */
  public void setDropAllDataOnActivate(boolean dropAllDataOnActivate)
  {
    this.dropAllDataOnActivate = dropAllDataOnActivate;
  }

  /**
   * @since 3.0
   */
  public int getLastBranchID()
  {
    return lastBranchID;
  }

  /**
   * @since 3.0
   */
  public void setLastBranchID(int lastBranchID)
  {
    this.lastBranchID = lastBranchID;
  }

  /**
   * @since 3.0
   */
  public int getNextBranchID()
  {
    return ++lastBranchID;
  }

  /**
   * @since 3.0
   */
  public int getLastLocalBranchID()
  {
    return lastLocalBranchID;
  }

  /**
   * @since 3.0
   */
  public void setLastLocalBranchID(int lastLocalBranchID)
  {
    this.lastLocalBranchID = lastLocalBranchID;
  }

  /**
   * @since 3.0
   */
  public int getNextLocalBranchID()
  {
    return --lastLocalBranchID;
  }

  /**
   * @since 3.0
   */
  public long getLastCommitTime()
  {
    synchronized (lastCommitTimeLock)
    {
      return lastCommitTime;
    }
  }

  /**
   * @since 3.0
   */
  public void setLastCommitTime(long lastCommitTime)
  {
    synchronized (lastCommitTimeLock)
    {
      if (this.lastCommitTime < lastCommitTime)
      {
        this.lastCommitTime = lastCommitTime;
      }
    }
  }

  /**
   * @since 3.0
   */
  public long getLastNonLocalCommitTime()
  {
    synchronized (lastNonLocalCommitTimeLock)
    {
      return lastNonLocalCommitTime;
    }
  }

  /**
   * @since 3.0
   */
  public void setLastNonLocalCommitTime(long lastNonLocalCommitTime)
  {
    synchronized (lastNonLocalCommitTimeLock)
    {
      if (this.lastNonLocalCommitTime < lastNonLocalCommitTime)
      {
        this.lastNonLocalCommitTime = lastNonLocalCommitTime;
      }
    }
  }

  public IStoreAccessor getReader(ISession session)
  {
    IStoreAccessor reader = null;
    StoreAccessorPool pool = getReaderPool(session, false);
    if (pool != null)
    {
      reader = pool.removeStoreAccessor(session);
    }

    if (reader == null && session != null)
    {
      CDOCommonView[] views = session.getViews();
      for (CDOCommonView view : views)
      {
        pool = getWriterPool((IView)view, false);
        if (pool != null)
        {
          reader = pool.removeStoreAccessor(view);
          if (reader != null)
          {
            break;
          }
        }
      }
    }

    if (reader == null)
    {
      reader = createReader(session);
      LifecycleUtil.activate(reader);
    }

    return reader;
  }

  public IStoreAccessor getWriter(ITransaction transaction)
  {
    IStoreAccessor writer = null;
    StoreAccessorPool pool = getWriterPool(transaction, false);
    if (pool != null)
    {
      writer = pool.removeStoreAccessor(transaction);
    }

    if (writer == null)
    {
      writer = createWriter(transaction);
      LifecycleUtil.activate(writer);
    }

    return writer;
  }

  public ProgressDistributor getIndicatingCommitDistributor()
  {
    return indicatingCommitDistributor;
  }

  /**
   * @since 3.0
   */
  public InternalCDORevision createRevision(EClass eClass, CDOID id)
  {
    CDORevisionFactory factory = repository.getRevisionManager().getFactory();
    InternalCDORevision revision = (InternalCDORevision)factory.createRevision(eClass);
    revision.setID(id);
    return revision;
  }

  /**
   * @since 4.0
   */
  protected void releaseAccessor(StoreAccessorBase accessor)
  {
    StoreAccessorPool pool = null;
    if (accessor.isReader())
    {
      pool = getReaderPool(accessor.getSession(), true);
    }
    else
    {
      pool = getWriterPool(accessor.getTransaction(), true);
    }

    if (pool != null)
    {
      pool.addStoreAccessor(accessor);
    }
    else
    {
      accessor.deactivate();
    }
  }

  /**
   * Returns a {@link StoreAccessorPool pool} that may contain {@link IStoreAccessor} instances that are compatible with
   * the given session. The implementor may return <code>null</code> to indicate that no pooling occurs. It's also left
   * to the implementors choice how to determine the appropriate pool instance to be used for the given session, for
   * example it could always return the same pool instance, regardless of the given session.
   * <p>
   * If the implementor of this method decides to create pools that are only compatible with certain sessions or views,
   * then it is his responsibility to listen to {@link Kind#REMOVED REMOVED} events sent by either the
   * {@link ISessionManager} (indicating that a session is closed) or any of its sessions (indicating that a view is
   * closed). <b>Note:</b> Closing a session <em>implies</em> that all contained views are closed sliently without
   * firing respective events!
   * 
   * @param session
   *          The context which the pool must be compatible with. Must not be <code>null</code>.
   * @param forReleasing
   *          Enables lazy pool creation. The implementor is not supposed to create a new pool if <code>false</code> is
   *          passed. If <code>true</code> is passed it's up to the implementor whether to create a new pool or not.
   */
  protected abstract StoreAccessorPool getReaderPool(ISession session, boolean forReleasing);

  /**
   * Returns a {@link StoreAccessorPool pool} that may contain {@link IStoreAccessor} instances that are compatible with
   * the given session. The implementor may return <code>null</code> to indicate that no pooling occurs. It's also left
   * to the implementors choice how to determine the appropriate pool instance to be used for the given session, for
   * example it could always return the same pool instance, regardless of the given session.
   * <p>
   * If the implementor of this method decides to create pools that are only compatible with certain sessions or views,
   * then it is his responsibility to listen to {@link Kind#REMOVED REMOVED} events sent by either the
   * {@link ISessionManager} (indicating that a session is closed) or any of its sessions (indicating that a view is
   * closed). <b>Note:</b> Closing a session <em>implies</em> that all contained views are closed sliently without
   * firing respective events!
   * 
   * @param view
   *          The context which the pool must be compatible with. Must not be <code>null</code>.
   * @param forReleasing
   *          Enables lazy pool creation. The implementor is not supposed to create a new pool if <code>false</code> is
   *          passed. If <code>true</code> is passed it's up to the implementor whether to create a new pool or not.
   */
  protected abstract StoreAccessorPool getWriterPool(IView view, boolean forReleasing);

  /**
   * Creates and returns a <b>new</b> {@link IStoreAccessor} instance. The caller of this method is responsible for
   * {@link Lifecycle#activate() activating} the new instance.
   */
  protected abstract IStoreAccessor createReader(ISession session);

  /**
   * Creates and returns a <b>new</b> {@link IStoreAccessor} instance. The caller of this method is responsible for
   * {@link Lifecycle#activate() activating} the new instance.
   */
  protected abstract IStoreAccessor createWriter(ITransaction transaction);

  protected static <T> Set<T> set(T... elements)
  {
    return Collections.unmodifiableSet(new HashSet<T>(Arrays.asList(elements)));
  }

  /**
   * @since 4.0
   */
  public static String idToString(CDOID id)
  {
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, id);
    return builder.toString();
  }

  /**
   * @since 4.0
   */
  public static CDOID stringToID(String string)
  {
    return CDOIDUtil.read(string);
  }

  /**
   * @since 3.0
   */
  public static IStoreAccessor.QueryResourcesContext.ExactMatch createExactMatchContext(final CDOID folderID,
      final String name, final CDOBranchPoint branchPoint)
  {
    return new IStoreAccessor.QueryResourcesContext.ExactMatch()
    {
      private CDOID resourceID;

      public CDOID getResourceID()
      {
        return resourceID;
      }

      public CDOBranch getBranch()
      {
        return branchPoint.getBranch();
      }

      public long getTimeStamp()
      {
        return branchPoint.getTimeStamp();
      }

      public CDOID getFolderID()
      {
        return folderID;
      }

      public String getName()
      {
        return name;
      }

      public boolean exactMatch()
      {
        return true;
      }

      public int getMaxResults()
      {
        return 1;
      }

      public boolean addResource(CDOID resourceID)
      {
        this.resourceID = resourceID;
        return false;
      }
    };
  }
}
