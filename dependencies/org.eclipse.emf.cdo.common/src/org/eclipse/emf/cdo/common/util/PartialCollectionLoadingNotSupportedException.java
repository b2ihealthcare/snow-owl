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
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.revision.CDOElementProxy;

/**
 * An unchecked exception that indicates that {@link CDOElementProxy list element proxies} have been encountered but
 * cannot be handled.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @since 4.1
 */
public class PartialCollectionLoadingNotSupportedException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  public PartialCollectionLoadingNotSupportedException()
  {
  }

  public PartialCollectionLoadingNotSupportedException(String message)
  {
    super(message);
  }

  public PartialCollectionLoadingNotSupportedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public PartialCollectionLoadingNotSupportedException(Throwable cause)
  {
    super(cause);
  }
}
