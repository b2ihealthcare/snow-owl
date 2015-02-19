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

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.common.util.URI;

import java.text.MessageFormat;

/**
 * An unchecked exception being thrown to indicate problems with the {@link URI} of a {@link CDOResource resource}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class InvalidURIException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private URI uri;

  public InvalidURIException(URI uri, Throwable cause)
  {
    super(MessageFormat.format(Messages.getString("InvalidURIException.0"), uri, cause), cause); //$NON-NLS-1$
    this.uri = uri;
  }

  public InvalidURIException(URI uri)
  {
    this(uri, null);
  }

  public URI getURI()
  {
    return uri;
  }
}
