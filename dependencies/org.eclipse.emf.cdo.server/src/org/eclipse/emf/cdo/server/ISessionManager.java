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
package org.eclipse.emf.cdo.server;

import org.eclipse.net4j.util.container.IContainer;

/**
 * Manages the user {@link ISession sessions} of a {@link IRepository repository}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.composedOf {@link ISession}
 */
public interface ISessionManager extends IContainer<ISession>
{
  /**
   * @since 2.0
   */
  public IRepository getRepository();

  public ISession[] getSessions();

  /**
   * @since 2.0
   */
  public ISession getSession(int sessionID);
}
