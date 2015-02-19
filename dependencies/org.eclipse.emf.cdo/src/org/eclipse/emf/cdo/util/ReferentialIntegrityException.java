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

import org.eclipse.emf.cdo.CDOObjectReference;

import java.util.List;

/**
 * A {@link CommitException commit exception} that indicates referential integrity problems detected by the server.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ReferentialIntegrityException extends CommitException
{
  private static final long serialVersionUID = 1L;

  private List<CDOObjectReference> xRefs;

  public ReferentialIntegrityException(String msg, List<CDOObjectReference> xRefs)
  {
    super(msg);
    this.xRefs = xRefs;
  }

  public List<CDOObjectReference> getXRefs()
  {
    return xRefs;
  }
}
