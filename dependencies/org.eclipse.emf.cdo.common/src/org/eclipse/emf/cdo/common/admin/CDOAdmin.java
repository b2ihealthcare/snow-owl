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

import java.util.Map;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * An administrative interface to a remote server with CDO {@link CDOCommonRepository repositories}.
 *
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAdmin extends IContainer<CDOAdminRepository>, Closeable
{
  public CDOAdminRepository[] getRepositories();

  public CDOAdminRepository getRepository(String name);

  /**
   * Creates a new remote {@link CDOCommonRepository repository} and returns its administrative interface.
   * <p>
   * On the server-side the creation is delegated to an instance of <code>org.eclipse.emf.cdo.server.spi.admin.CDOAdminHandler</code>
   * that is registered with the server's {@link IManagedContainer container} under the given <code>type</code> argument.
   * The <code>name</code> and <code>properties</code> arguments are passed on to the registered handler.
   */
  public CDOAdminRepository createRepository(String name, String type, Map<String, Object> properties);

  public CDOAdminRepository waitForRepository(String name);
}
