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

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrentValue;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class XATransactionCommitContext extends TransactionCommitContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, XATransactionCommitContext.class);

  private ConcurrentValue<CommitState> state = new ConcurrentValue<CommitState>(CommitState.STARTING);

  public XATransactionCommitContext(InternalTransaction transaction)
  {
    super(transaction);
  }

  public ConcurrentValue<CommitState> getState()
  {
    return state;
  }

  @Override
  public void preWrite()
  {
    super.preWrite();
    StoreThreadLocal.setAccessor(null);
  }

  @Override
  public void commit(OMMonitor monitor)
  {
    StoreThreadLocal.setAccessor(getAccessor());
    try
    {
      super.commit(monitor);
    }
    finally
    {
      StoreThreadLocal.setAccessor(null);
    }
  }

  @Override
  public void write(OMMonitor monitor)
  {
    StoreThreadLocal.setAccessor(getAccessor());
    try
    {
      super.write(monitor);
    }
    finally
    {
      StoreThreadLocal.setAccessor(null);
    }
  }

  @Override
  public void postCommit(boolean success)
  {
    StoreThreadLocal.setAccessor(getAccessor());
    InternalRepository repository = getTransaction().getRepository();
    repository.getCommitManager().remove(this);
    super.postCommit(success);
  }

  @Override
  public synchronized void rollback(String message)
  {
    super.rollback(message);

    // Change the state to unblock call.
    state.set(CommitState.ROLLED_BACK);
  }

  /**
   * Wait until another thread fills ID mapping for external objects.
   */
  @Override
  public void applyIDMappings(OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Notify phase2 to fill ID mapping."); //$NON-NLS-1$
    }

    state.set(CommitState.APPLY_ID_MAPPING);
    if (TRACER.isEnabled())
    {
      TRACER.format("Waiting for phase2 to be completed before continueing."); //$NON-NLS-1$
    }

    try
    {
      state.acquire(PHASEAPPLYMAPPING_DONE);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Received signal to continue."); //$NON-NLS-1$
    }

    super.applyIDMappings(monitor);
  }

  /**
   * Object to test if the process is at ApplyIDMapping
   */
  final public static Object PHASEAPPLYMAPPING = new Object()
  {
    @Override
    public int hashCode()
    {
      return CommitState.APPLY_ID_MAPPING.hashCode();
    }

    @Override
    public boolean equals(Object object)
    {
      if (object == CommitState.ROLLED_BACK)
      {
        throw new RuntimeException("RolledBack"); //$NON-NLS-1$
      }

      return CommitState.APPLY_ID_MAPPING == object;
    }
  };

  /**
   * Object to test if the process did applyIDMapping
   */
  final public static Object PHASEAPPLYMAPPING_DONE = new Object()
  {
    @Override
    public int hashCode()
    {
      return CommitState.APPLY_ID_MAPPING_DONE.hashCode();
    }

    @Override
    public boolean equals(Object object)
    {
      if (object == CommitState.ROLLED_BACK)
      {
        throw new RuntimeException("RolledBack"); //$NON-NLS-1$
      }

      return CommitState.APPLY_ID_MAPPING_DONE == object;
    }
  };

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  public enum CommitState
  {
    STARTING, APPLY_ID_MAPPING, APPLY_ID_MAPPING_DONE, ROLLED_BACK
  }
}
