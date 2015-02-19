/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233314
 *    Simon McDuff - bug 247143
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * A call-back interface that is called by a {@link CDOTransaction transcation} before {@link CDOObject objects} are
 * attached, modified or detached.
 *
 * @see CDOPostEventTransactionHandler
 * @author Eike Stepper
 * @since 4.0
 */
public interface CDOTransactionHandler1 extends CDOTransactionHandlerBase
{
  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is added. The implementor of this method is allowed
   * to throw an unchecked exception that will propagate up to the operation that is about to add the object (thereby
   * preventing the operation from successful completion).
   * <p>
   * <b>Note:</b> Implementors <b>must not</b> start threads which access the {@link CDOView view} and wait for their
   * completion since deadlocks can result. The following example causes a deadlock:<br>
   *
   * <pre>
   * getDisplay().syncExec(new Runnable()
   * {
   *   public void run()
   *   {
   *     try
   *     {
   *       cdoObject.getName();
   *     }
   *     catch (Exception ignore)
   *     {
   *     }
   *   }
   * });
   * </pre>
   *
   * If you need to synchronously execute threads which access the {@link CDOView view} you should use
   * {@link CDOAsyncTransactionHandler}.
   */
  public void attachingObject(CDOTransaction transaction, CDOObject object);

  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is detached. The implementor of this method is
   * allowed to throw an unchecked exception that will propagate up to the operation that is about to remove the object
   * (thereby preventing the operation from completing successfully).
   * <p>
   * <b>Note:</b> Implementors <b>must not</b> start threads which access the {@link CDOView view} and wait for their
   * completion since deadlocks can result. The following example causes a deadlock:<br>
   *
   * <pre>
   * getDisplay().syncExec(new Runnable()
   * {
   *   public void run()
   *   {
   *     try
   *     {
   *       cdoObject.getName();
   *     }
   *     catch (Exception ignore)
   *     {
   *     }
   *   }
   * });
   * </pre>
   *
   * If you need to synchronously execute threads which access the {@link CDOView view} you should use
   * {@link CDOAsyncTransactionHandler}.
   */
  public void detachingObject(CDOTransaction transaction, CDOObject object);

  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is modified. The implementor of this method is
   * allowed to throw an unchecked exception that will propagate up to the operation that is about to modify the object
   * (thereby preventing the operation from completing successfully).
   * <p>
   * <b>Note:</b> Implementors <b>must not</b> start threads which access the {@link CDOView view} and wait for their
   * completion since deadlocks can result. The following example causes a deadlock:<br>
   *
   * <pre>
   * getDisplay().syncExec(new Runnable()
   * {
   *   public void run()
   *   {
   *     try
   *     {
   *       cdoObject.getName();
   *     }
   *     catch (Exception ignore)
   *     {
   *     }
   *   }
   * });
   * </pre>
   *
   * If you need to synchronously execute threads which access the {@link CDOView view} you should use
   * {@link CDOAsyncTransactionHandler}.
   */
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta);
}
