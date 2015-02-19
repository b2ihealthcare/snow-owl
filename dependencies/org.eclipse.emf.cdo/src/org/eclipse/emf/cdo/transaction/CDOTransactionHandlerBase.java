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

/**
 * A marker interface for use with {@link CDOTransaction#addTransactionHandler(CDOTransactionHandlerBase)
 * CDOTransaction.addTransactionHandler()} and
 * {@link CDOTransaction#removeTransactionHandler(CDOTransactionHandlerBase) CDOTransaction.removeTransactionHandler()}.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public interface CDOTransactionHandlerBase
{
}
