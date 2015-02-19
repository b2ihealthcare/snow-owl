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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * An unchecked exception being thrown if write access to {@link CDOObject objects} of a {@link CDOView#isReadOnly()
 * read-only} view is attempted.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ReadOnlyException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public ReadOnlyException()
  {
  }

  public ReadOnlyException(String message)
  {
    super(message);
  }

  public ReadOnlyException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ReadOnlyException(Throwable cause)
  {
    super(cause);
  }
}
