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
package org.eclipse.emf.cdo.server.embedded;

import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.server.IRepository;

/**
 * Deprecated, not yet supported.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @deprecated Not yet supported.
 */
@Deprecated
public interface CDOSessionConfiguration extends org.eclipse.emf.cdo.session.CDOSessionConfiguration
{
  public IRepository getRepository();

  public void setRepository(IRepository repository);

  public CDORevisionManager getRevisionManager();

  public void setRevisionManager(CDORevisionManager revisionManager);

  public org.eclipse.emf.cdo.server.embedded.CDOSession openSession();
}
