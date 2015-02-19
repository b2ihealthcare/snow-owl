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

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.session.CDOSession;

/**
 * An unchecked exception being thrown when opening a {@link CDOSession session} to a named {@link IRepository
 * repository} that cannot be found.
 * 
 * @author Eike Stepper
 */
public class RepositoryNotFoundException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public RepositoryNotFoundException(String repositoryName)
  {
    super(repositoryName);
  }

  public String getRepositoryName()
  {
    return super.getMessage();
  }

  @Override
  public String getMessage()
  {
    return "Repository not found: " + getRepositoryName(); //$NON-NLS-1$
  }
}
