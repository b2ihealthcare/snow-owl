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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalCommitManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CommitManager extends Lifecycle implements InternalCommitManager
{
  private InternalRepository repository;

  @ExcludeFromDump
  private transient ExecutorService executors;

  private boolean shutdownExecutorService;

  @ExcludeFromDump
  private transient Map<InternalTransaction, TransactionCommitContextEntry> contextEntries = new ConcurrentHashMap<InternalTransaction, TransactionCommitContextEntry>();

  public CommitManager()
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

  public synchronized ExecutorService getExecutors()
  {
    if (executors == null)
    {
      shutdownExecutorService = true;
      executors = Executors.newFixedThreadPool(10);
    }

    return executors;
  }

  public synchronized void setExecutors(ExecutorService executors)
  {
    if (shutdownExecutorService)
    {
      this.executors.shutdown();
      shutdownExecutorService = false;
    }

    this.executors = executors;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
    setExecutors(null);
  }

  /**
   * Create a future to execute commitContext in a different thread.
   */
  public void preCommit(InternalCommitContext commitContext, OMMonitor monitor)
  {
    TransactionCommitContextEntry contextEntry = new TransactionCommitContextEntry(monitor);
    contextEntry.setContext(commitContext);

    Future<Object> future = getExecutors().submit(contextEntry.createCallable());
    contextEntry.setFuture(future);

    contextEntries.put(commitContext.getTransaction(), contextEntry);
  }

  /**
   * Called after a commitContext is done successfully or not.
   */
  public void remove(InternalCommitContext commitContext)
  {
    contextEntries.remove(commitContext.getTransaction());
  }

  public void rollback(InternalCommitContext commitContext)
  {
    TransactionCommitContextEntry contextEntry = contextEntries.get(commitContext.getTransaction());
    if (contextEntry != null)
    {
      contextEntry.getFuture().cancel(true);
      commitContext.rollback("Remote rollback"); //$NON-NLS-1$
      commitContext.postCommit(false);
    }
  }

  /**
   * Waiting for a commit to be done.
   */
  public void waitForTermination(InternalTransaction transaction) throws InterruptedException, ExecutionException
  {
    TransactionCommitContextEntry contextEntry = contextEntries.get(transaction);
    contextEntry.getFuture().get();
  }

  public InternalCommitContext get(InternalTransaction transaction)
  {
    TransactionCommitContextEntry contextEntry = contextEntries.get(transaction);
    if (contextEntry != null)
    {
      return contextEntry.getContext();
    }

    return null;
  }

  /**
   * @author Simon McDuff
   */
  private static final class TransactionCommitContextEntry
  {
    private InternalCommitContext context;

    private Future<Object> future;

    private OMMonitor monitor;

    public TransactionCommitContextEntry(OMMonitor monitor)
    {
      this.monitor = monitor;
    }

    public Callable<Object> createCallable()
    {
      return new Callable<Object>()
      {
        public Object call() throws Exception
        {
          context.write(monitor);
          return null;
        }
      };
    }

    public InternalCommitContext getContext()
    {
      return context;
    }

    public void setContext(InternalCommitContext context)
    {
      this.context = context;
    }

    public Future<Object> getFuture()
    {
      return future;
    }

    public void setFuture(Future<Object> future)
    {
      this.future = future;
    }
  }
}
