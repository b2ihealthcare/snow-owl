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
package org.eclipse.emf.cdo.common.admin;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.INotifier;

/**
 * An administrative interface to a remote {@link CDOCommonRepository repository}.
 *
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAdminRepository extends CDOCommonRepository, INotifier
{
  public CDOAdmin getAdmin();

  /**
   * Deletes the remote {@link CDOCommonRepository repository} administered by this administrative interface.
   * <p>
   * On the server-side the deletion is delegated to an instance of <code>org.eclipse.emf.cdo.server.spi.admin.CDOAdminHandler</code>
   * that is registered with the server's {@link IManagedContainer container} under the given <code>type</code> argument.
   */
  public boolean delete(String type);

  /**
   * May be unsupported on the client side.
   */
  public long getTimeStamp() throws UnsupportedOperationException;
}
