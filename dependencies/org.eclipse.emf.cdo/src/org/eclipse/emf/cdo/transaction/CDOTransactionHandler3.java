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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

/**
 * A call-back interface that is called by a {@link CDOTransaction transcation} before it is committed and after it has
 * been committed (with result info) or rolled back.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public interface CDOTransactionHandler3 extends CDOTransactionHandler2
{
  /**
   * Called by a <code>CDOTransaction</code> <b>after</b> it is being committed. The implementor of this method is
   * <b>not</b> allowed to throw an unchecked exception.
   * <p>
   * Note that {@link CDOTransactionHandler2#committedTransaction(CDOTransaction, CDOCommitContext)
   * CDOTransactionHandler2.committedTransaction()} is not called.
   */
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext, CDOCommitInfo result);
}
