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
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor.SynchronizedSubProgressMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOUserSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOXASavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext.CDOXAState;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Three-phase commit.
 * <p>
 * Phase 1 does the following for each CDOTransaction:<br>
 * - preCommit <br>
 * - Accumulate external temporary ID.<br>
 * - request the commit to the server.<br>
 * - The server registers the commit context and returns the final ID for each temporary ID.
 * <p>
 * Phase 2 does the following for each CDOTransaction:<br>
 * - Transfer to the server a list of mapping of temporary externalID and final external ID that we accumulate
 * previously<br>
 * - Returns to the client only when commit process is ready to flush to disk (commit). <br>
 * <p>
 * Phase 3 does the following for each CDOTransaction:<br>
 * - Make modifications permanent.<br>
 * - PostCommit.
 * <p>
 * If an exception occurred during phase 1 or phase 2, the commit will be cancelled for all {@link CDOTransaction}
 * include in the XA transaction. If an exception occurred during phase 3, the commit will be cancelled only for the
 * {@link CDOTransaction} where the error happened.
 * <p>
 * All {@link CDOTransaction} includes in the commit process need to have finish their phase before moving to the next
 * phase. For one phase, every {@link CDOTransaction} could have their own thread. It depends of the ExecutorService.
 * <p>
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOXATransactionImpl implements InternalCDOXATransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, CDOXATransactionImpl.class);

  /**
   * Parallel execution leads to deadlocks because the view lock is being held by the scheduler.
   * <p>
   * Contact the authors if you want to have this executed in parallel.
   */
  private static boolean SEQUENTIAL_EXECUTION = true;

  private List<InternalCDOTransaction> transactions = new ArrayList<InternalCDOTransaction>();

  private boolean allowRequestFromTransactionEnabled = true;

  private ExecutorService executorService = createExecutorService();

  private Map<InternalCDOTransaction, InternalCDOXACommitContext> activeContexts = new HashMap<InternalCDOTransaction, InternalCDOXACommitContext>();

  private Map<InternalCDOTransaction, Set<CDOID>> requestedCDOIDs = new HashMap<InternalCDOTransaction, Set<CDOID>>();

  private InternalCDOXASavepoint lastSavepoint = createSavepoint(null);

  private InternalCDOXASavepoint firstSavepoint = lastSavepoint;

  private CDOTransactionStrategy transactionStrategy = createTransactionStrategy();

  private CDOXAInternalAdapter internalAdapter = createInternalAdapter();

  public CDOXATransactionImpl()
  {
  }

  public boolean isAllowRequestFromTransactionEnabled()
  {
    return allowRequestFromTransactionEnabled;
  }

  public void setAllowRequestFromTransactionEnabled(boolean on)
  {
    allowRequestFromTransactionEnabled = on;
  }

  public void add(InternalCDOTransaction transaction)
  {
    transaction.setTransactionStrategy(transactionStrategy);
  }

  public void remove(InternalCDOTransaction transaction)
  {
    if (transaction != null)
    {
      transaction.setTransactionStrategy(null);
    }
  }

  public synchronized void add(CDOViewSet viewSet)
  {
    CDOXATransaction transSet = CDOUtil.getXATransaction(viewSet);
    if (transSet != null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOXATransactionImpl.0")); //$NON-NLS-1$
    }

    viewSet.eAdapters().add(internalAdapter);

    for (InternalCDOTransaction transaction : getTransactions(viewSet))
    {
      add(transaction);
    }
  }

  public synchronized void remove(CDOViewSet viewSet)
  {
    CDOXATransaction transSet = CDOUtil.getXATransaction(viewSet);
    if (transSet != this)
    {
      throw new IllegalArgumentException(Messages.getString("CDOXATransactionImpl.1")); //$NON-NLS-1$
    }

    for (InternalCDOTransaction transaction : getTransactions(viewSet))
    {
      remove(transaction);
    }

    viewSet.eAdapters().remove(internalAdapter);
  }

  public void add(InternalCDOTransaction transaction, CDOID object)
  {
    synchronized (requestedCDOIDs)
    {
      Set<CDOID> ids = requestedCDOIDs.get(transaction);
      if (ids == null)
      {
        ids = new HashSet<CDOID>();
        requestedCDOIDs.put(transaction, ids);
      }

      ids.add(object);
    }
  }

  public CDOID[] get(InternalCDOTransaction transaction)
  {
    Set<CDOID> ids = requestedCDOIDs.get(transaction);
    return ids.toArray(new CDOID[ids.size()]);
  }

  public InternalCDOXACommitContext getCommitContext(CDOTransaction transaction)
  {
    return activeContexts.get(transaction);
  }

  private void send(Collection<InternalCDOXACommitContext> xaContexts, final IProgressMonitor progressMonitor)
      throws InterruptedException, ExecutionException
  {
    int xaContextSize = xaContexts.size();
    progressMonitor.beginTask("", xaContextSize); //$NON-NLS-1$

    try
    {
      Map<Future<Object>, InternalCDOXACommitContext> futures = new HashMap<Future<Object>, InternalCDOXACommitContext>();
      for (InternalCDOXACommitContext xaContext : xaContexts)
      {
        xaContext.setProgressMonitor(new SynchronizedSubProgressMonitor(progressMonitor, 1));
        Future<Object> future = executorService.submit(xaContext);
        futures.put(future, xaContext);
      }

      int nbProcessDone = 0;

      do
      {
        for (Iterator<Entry<Future<Object>, InternalCDOXACommitContext>> it = futures.entrySet().iterator(); it
            .hasNext();)
        {
          Entry<Future<Object>, InternalCDOXACommitContext> entry = it.next();
          Future<Object> future = entry.getKey();
          InternalCDOXACommitContext xaContext = entry.getValue();

          try
          {
            future.get(1000, TimeUnit.MILLISECONDS);
            nbProcessDone++;
            it.remove();
            if (TRACER.isEnabled())
            {
              TRACER.format("Got {0}", xaContext);
            }
          }
          catch (TimeoutException ex)
          {
            // Just retry
            if (TRACER.isEnabled())
            {
              TRACER.format("Waiting for {0}", xaContext);
            }
          }
        }
      } while (xaContextSize != nbProcessDone);
    }
    finally
    {
      progressMonitor.done();
      for (InternalCDOXACommitContext xaContext : xaContexts)
      {
        xaContext.setProgressMonitor(null);
      }
    }
  }

  private void cleanUp()
  {
    activeContexts.clear();
    requestedCDOIDs.clear();
  }

  private List<InternalCDOTransaction> getTransactions(CDOViewSet viewSet)
  {
    List<InternalCDOTransaction> transactions = new ArrayList<InternalCDOTransaction>();
    for (CDOView view : viewSet.getViews())
    {
      if (view instanceof InternalCDOTransaction)
      {
        transactions.add((InternalCDOTransaction)view);
      }
    }

    return transactions;
  }

  public CDOCommitInfo commit() throws CommitException
  {
    return commit(new NullProgressMonitor());
  }

  public CDOCommitInfo commit(IProgressMonitor progressMonitor) throws CommitException
  {
    CheckUtil.checkArg(progressMonitor, "progressMonitor"); //$NON-NLS-1$
    progressMonitor.beginTask(Messages.getString("CDOXATransactionImpl.4"), 3); //$NON-NLS-1$
    int phase = 0;

    for (InternalCDOTransaction transaction : transactions)
    {
      InternalCDOCommitContext context = transaction.createCommitContext();
      InternalCDOXACommitContext xaContext = createXACommitContext(context);
      xaContext.setState(CDOXAPhase1State.INSTANCE);
      activeContexts.put(transaction, xaContext);
    }

    try
    {
      // We need to complete 3 phases
      while (phase < 3)
      {
        send(activeContexts.values(), new SubProgressMonitor(progressMonitor, 1));
        ++phase;
      }

      return null;
    }
    catch (Exception ex)
    {
      if (phase < 2)
      {
        // Phase 0 and 1 are the only two phases we can cancel.
        for (InternalCDOXACommitContext xaContext : activeContexts.values())
        {
          xaContext.setState(CDOXACancel.INSTANCE);
        }

        try
        {
          send(activeContexts.values(), new SubProgressMonitor(progressMonitor, 2 - phase));
        }
        catch (InterruptedException ex1)
        {
          throw WrappedException.wrap(ex1);
        }
        catch (ExecutionException ex1)
        {
          OM.LOG.warn(ex1);
        }
      }

      throw new CommitException(ex);
    }
    finally
    {
      cleanUp();
      progressMonitor.done();
    }
  }

  public InternalCDOXASavepoint getLastSavepoint()
  {
    return lastSavepoint;
  }

  public void rollback()
  {
    rollback(firstSavepoint);
  }

  public void rollback(InternalCDOXASavepoint savepoint)
  {
    if (!savepoint.isValid())
    {
      throw new IllegalArgumentException(Messages.getString("CDOXATransactionImpl.7") + savepoint); //$NON-NLS-1$
    }

    List<CDOSavepoint> savepoints = savepoint.getSavepoints();
    if (savepoints == null)
    {
      savepoints = getListSavepoints();
    }

    for (CDOSavepoint indexSavePoint : savepoints)
    {
      InternalCDOTransaction transaction = (InternalCDOTransaction)indexSavePoint.getTransaction();
      CDOSingleTransactionStrategyImpl.INSTANCE.rollback(transaction, (InternalCDOUserSavepoint)indexSavePoint);
    }

    lastSavepoint = savepoint;
    lastSavepoint.setNextSavepoint(null);
    lastSavepoint.setSavepoints(null);
  }

  public InternalCDOXASavepoint setSavepoint()
  {
    List<CDOSavepoint> savepoints = getListSavepoints();
    for (CDOSavepoint savepoint : savepoints)
    {
      InternalCDOTransaction transaction = (InternalCDOTransaction)savepoint.getTransaction();
      CDOSingleTransactionStrategyImpl.INSTANCE.setSavepoint(transaction);
    }

    getLastSavepoint().setSavepoints(savepoints);
    lastSavepoint = createSavepoint(getLastSavepoint());
    return lastSavepoint;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOXATransaction[size={0}]", transactions.size());
  }

  protected CDOXACommitContextImpl createXACommitContext(InternalCDOCommitContext context)
  {
    return new CDOXACommitContextImpl(this, context);
  }

  protected CDOXATransactionStrategyImpl createTransactionStrategy()
  {
    return new CDOXATransactionStrategyImpl(this);
  }

  protected CDOXAInternalAdapter createInternalAdapter()
  {
    return new CDOXAInternalAdapter(this);
  }

  protected CDOXASavepointImpl createSavepoint(InternalCDOXASavepoint lastSavepoint)
  {
    return new CDOXASavepointImpl(this, lastSavepoint);
  }

  protected final ExecutorService createExecutorService()
  {
    if (SEQUENTIAL_EXECUTION)
    {
      return new AbstractExecutorService()
      {
        public void execute(Runnable command)
        {
          command.run();
        }

        public List<Runnable> shutdownNow()
        {
          return null;
        }

        public void shutdown()
        {
        }

        public boolean isTerminated()
        {
          return false;
        }

        public boolean isShutdown()
        {
          return false;
        }

        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
        {
          return false;
        }
      };
    }

    return Executors.newFixedThreadPool(10);
  }

  private List<CDOSavepoint> getListSavepoints()
  {
    synchronized (transactions)
    {
      List<CDOSavepoint> savepoints = new ArrayList<CDOSavepoint>();
      for (InternalCDOTransaction transaction : transactions)
      {
        savepoints.add(transaction.getLastSavepoint());
      }

      return savepoints;
    }
  }

  /**
   * @author Simon McDuff
   */
  public static class CDOXAInternalAdapter implements Adapter
  {
    private InternalCDOXATransaction xaTransaction;

    public CDOXAInternalAdapter(InternalCDOXATransaction xaTransaction)
    {
      this.xaTransaction = xaTransaction;
    }

    public InternalCDOXATransaction getXATransaction()
    {
      return xaTransaction;
    }

    public Notifier getTarget()
    {
      return null;
    }

    public boolean isAdapterForType(Object type)
    {
      return false;
    }

    public void notifyChanged(Notification notification)
    {
      switch (notification.getEventType())
      {
      case Notification.ADD:
        if (notification.getNewValue() instanceof InternalCDOTransaction)
        {
          getXATransaction().add((InternalCDOTransaction)notification.getNewValue());
        }

        break;

      case Notification.REMOVE:
        if (notification.getOldValue() instanceof InternalCDOTransaction)
        {
          getXATransaction().remove((InternalCDOTransaction)notification.getNewValue());
        }

        break;
      }
    }

    public void setTarget(Notifier newTarget)
    {
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class CDOXATransactionStrategyImpl implements CDOTransactionStrategy
  {
    private InternalCDOXATransaction xaTransaction;

    public CDOXATransactionStrategyImpl(InternalCDOXATransaction xaTransaction)
    {
      this.xaTransaction = xaTransaction;
    }

    public void setTarget(InternalCDOTransaction transaction)
    {
      synchronized (transactions)
      {
        transactions.add(transaction);
      }
    }

    public void unsetTarget(InternalCDOTransaction transaction)
    {
      synchronized (transactions)
      {
        transactions.remove(transaction);
      }
    }

    private void checkAccess()
    {
      if (!allowRequestFromTransactionEnabled)
      {
        throw new IllegalStateException(Messages.getString("CDOXATransactionImpl.8")); //$NON-NLS-1$
      }
    }

    public CDOCommitInfo commit(InternalCDOTransaction transactionCommit, IProgressMonitor progressMonitor)
        throws Exception
    {
      checkAccess();
      return xaTransaction.commit(progressMonitor);
    }

    public void rollback(InternalCDOTransaction transaction, InternalCDOUserSavepoint savepoint)
    {
      checkAccess();
      xaTransaction.rollback((InternalCDOXASavepoint)savepoint);
    }

    public InternalCDOUserSavepoint setSavepoint(InternalCDOTransaction transaction)
    {
      checkAccess();
      return xaTransaction.setSavepoint();
    }
  }

  /**
   * @author Simon McDuff
   */
  public static class CDOXAPhase1State extends CDOXAState
  {
    public static final CDOXAPhase1State INSTANCE = new CDOXAPhase1State();

    @Override
    public void handle(InternalCDOXACommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      xaContext.preCommit();
      CommitTransactionResult result = null;
      if (xaContext.getTransaction().isDirty())
      {
        CDOSessionProtocol sessionProtocol = xaContext.getTransaction().getSession().getSessionProtocol();
        OMMonitor monitor = new EclipseMonitor(progressMonitor);
        result = sessionProtocol.commitXATransactionPhase1(xaContext, monitor);
        check_result(result);
      }

      xaContext.setResult(result);
      xaContext.setState(CDOXAPhase2State.INSTANCE);
    }

    @Override
    public String toString()
    {
      return "PHASE1";
    }
  }

  /**
   * @author Simon McDuff
   */
  public static class CDOXAPhase2State extends CDOXAState
  {
    public static final CDOXAPhase2State INSTANCE = new CDOXAPhase2State();

    public CDOXAPhase2State()
    {
    }

    @Override
    public void handle(InternalCDOXACommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      if (xaContext.getTransaction().isDirty())
      {
        CDOSessionProtocol sessionProtocol = xaContext.getTransaction().getSession().getSessionProtocol();
        OMMonitor monitor = new EclipseMonitor(progressMonitor);
        CommitTransactionResult result = sessionProtocol.commitXATransactionPhase2(xaContext, monitor);
        check_result(result);
      }

      xaContext.setState(CDOXAPhase3State.INSTANCE);
    }

    @Override
    public String toString()
    {
      return "PHASE2";
    }
  }

  /**
   * @author Simon McDuff
   */
  public static class CDOXAPhase3State extends CDOXAState
  {
    public static final CDOXAPhase3State INSTANCE = new CDOXAPhase3State();

    public CDOXAPhase3State()
    {
    }

    @Override
    public void handle(InternalCDOXACommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      if (xaContext.getTransaction().isDirty())
      {
        CDOSessionProtocol sessionProtocol = xaContext.getTransaction().getSession().getSessionProtocol();
        OMMonitor monitor = new EclipseMonitor(progressMonitor);
        CommitTransactionResult result = sessionProtocol.commitXATransactionPhase3(xaContext, monitor);
        check_result(result);
      }

      xaContext.postCommit(xaContext.getResult());
      xaContext.setState(null);
    }

    @Override
    public String toString()
    {
      return "PHASE3";
    }
  }

  /**
   * @author Simon McDuff
   */
  public static class CDOXACancel extends CDOXAState
  {
    public static final CDOXACancel INSTANCE = new CDOXACancel();

    public CDOXACancel()
    {
    }

    @Override
    public void handle(InternalCDOXACommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      CDOSessionProtocol sessionProtocol = xaContext.getTransaction().getSession().getSessionProtocol();
      OMMonitor monitor = new EclipseMonitor(progressMonitor);
      CommitTransactionResult result = sessionProtocol.commitXATransactionCancel(xaContext, monitor);
      check_result(result);
    }

    @Override
    public String toString()
    {
      return "CANCEL";
    }
  }
}
