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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class QueryManager extends Lifecycle implements InternalQueryManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, QueryManager.class);

  private InternalRepository repository;

  private Map<Integer, QueryContext> queryContexts = new ConcurrentHashMap<Integer, QueryContext>();

  private ExecutorService executors;

  private boolean shutdownExecutorService;

  private int nextQuery;

  private boolean allowInterruptRunningQueries = true;

  public QueryManager()
  {
  }

  public InternalRepository getRepository()
  {
    return repository;
  }

  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;

    String value = repository.getProperties().get(IRepository.Props.ALLOW_INTERRUPT_RUNNING_QUERIES);
    if (value != null)
    {
      allowInterruptRunningQueries = Boolean.parseBoolean(value);
    }
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

  public InternalQueryResult execute(InternalView view, CDOQueryInfo queryInfo)
  {
    InternalQueryResult queryResult = new QueryResult(view, queryInfo, getNextQueryID());
    QueryContext queryContext = new QueryContext(queryResult);
    execute(queryContext);
    return queryResult;
  }

  public boolean isRunning(int queryID)
  {
    QueryContext queryContext = queryContexts.get(queryID);
    return queryContext != null;
  }

  public void cancel(int queryID)
  {
    QueryContext queryContext = queryContexts.get(queryID);
    if (queryContext == null || queryContext.getFuture().isDone())
    {
      throw new RuntimeException("Query " + queryID + " is not running anymore"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Cancelling query for context: " + queryContext); //$NON-NLS-1$
    }

    queryContext.cancel();
  }

  public synchronized void register(QueryContext queryContext)
  {
    int queryID = queryContext.getQueryResult().getQueryID();
    queryContexts.put(queryID, queryContext);
    queryContext.addListener();
  }

  public synchronized void unregister(QueryContext queryContext)
  {
    int queryID = queryContext.getQueryResult().getQueryID();
    queryContexts.remove(queryID);
    queryContext.removeListener();
  }

  public synchronized int getNextQueryID()
  {
    return ++nextQuery;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
    setExecutors(null);
  }

  private Future<?> execute(QueryContext queryContext)
  {
    register(queryContext);

    Future<?> future = getExecutors().submit(queryContext);
    queryContext.setFuture(future);
    return future;
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  private class QueryContext implements IQueryContext, Runnable
  {
    private CDOBranchPoint branchPoint;

    private InternalQueryResult queryResult;

    private boolean started;

    private boolean cancelled;

    private int resultCount;

    private Future<?> future;

    private IListener sessionListener = new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof SingleDeltaContainerEvent<?>)
        {
          IView view = getQueryResult().getView();
          SingleDeltaContainerEvent<?> deltaEvent = (SingleDeltaContainerEvent<?>)event;
          if (deltaEvent.getDeltaKind() == Kind.REMOVED && deltaEvent.getDeltaElement() == view)
          {
            // Cancel the query when view is closing
            cancel();
          }
        }
      }
    };

    public QueryContext(InternalQueryResult queryResult)
    {
      this.queryResult = queryResult;

      // Remember the branchPoint because it can change
      InternalView view = getView();

      // long timeStamp = view.getTimeStamp();
      // if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE && repository.isSupportingAudits())
      // {
      // timeStamp = repository.getTimeStamp();
      // }
      //
      // branchPoint = view.getBranch().getPoint(timeStamp);

      branchPoint = CDOBranchUtil.copyBranchPoint(view);
    }

    public InternalQueryResult getQueryResult()
    {
      return queryResult;
    }

    public InternalView getView()
    {
      return queryResult.getView();
    }

    public CDOBranch getBranch()
    {
      return branchPoint.getBranch();
    }

    public long getTimeStamp()
    {
      return branchPoint.getTimeStamp();
    }

    public Future<?> getFuture()
    {
      return future;
    }

    public void setFuture(Future<?> future)
    {
      this.future = future;
    }

    public void cancel()
    {
      cancelled = true;
      if (future != null)
      {
        future.cancel(allowInterruptRunningQueries);
      }

      if (!started)
      {
        unregister(this);
      }
    }

    public int getResultCount()
    {
      return resultCount;
    }

    public boolean addResult(Object object)
    {
      if (resultCount == 0)
      {
        throw new IllegalStateException("Maximum number of results exceeded"); //$NON-NLS-1$
      }

      queryResult.getQueue().add(object);
      return !cancelled && --resultCount > 0;
    }

    public void run()
    {
      InternalSession session = queryResult.getView().getSession();
      StoreThreadLocal.setSession(session);

      try
      {
        started = true;
        CDOQueryInfo info = queryResult.getQueryInfo();
        resultCount = info.getMaxResults() < 0 ? Integer.MAX_VALUE : info.getMaxResults();
        IQueryHandler handler = repository.getQueryHandler(info);
        handler.executeQuery(info, this);
      }
      catch (Throwable exception)
      {
        queryResult.getQueue().setException(exception);
      }
      finally
      {
        queryResult.getQueue().close();
        unregister(this);
        StoreThreadLocal.release();
      }
    }

    public void addListener()
    {
      InternalSession session = getQueryResult().getView().getSession();
      session.addListener(sessionListener);
    }

    public void removeListener()
    {
      InternalSession session = getQueryResult().getView().getSession();
      session.removeListener(sessionListener);
    }
  }
}
