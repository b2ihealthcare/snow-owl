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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

/**
 * @author Eike Stepper
 */
public class CommitXATransactionRequest extends CommitTransactionRequest
{
  private InternalCDOXACommitContext xaContext;

  public CommitXATransactionRequest(CDOClientProtocol protocol, short signalID, InternalCDOXACommitContext xaContext)
  {
    super(protocol, signalID, xaContext);
    this.xaContext = xaContext;
  }

  protected InternalCDOXACommitContext getCommitContext()
  {
    return xaContext;
  }
}
