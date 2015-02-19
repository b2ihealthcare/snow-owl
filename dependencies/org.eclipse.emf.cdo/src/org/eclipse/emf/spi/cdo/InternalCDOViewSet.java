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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.common.notify.Adapter;

import java.util.concurrent.Callable;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOViewSet extends CDOViewSet, Adapter
{
  public void add(InternalCDOView view);

  public void remove(InternalCDOView view);

  public InternalCDOView resolveView(String repositoryUUID);

  /**
   * @since 4.1
   */
  public <V> V executeWithoutNotificationHandling(Callable<V> callable);
}
