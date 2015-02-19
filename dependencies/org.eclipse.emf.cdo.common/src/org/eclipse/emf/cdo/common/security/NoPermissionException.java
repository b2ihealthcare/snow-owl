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
package org.eclipse.emf.cdo.common.security;

/**
 * A {@link SecurityException security exception} indicating the lack of permission required to do something.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class NoPermissionException extends SecurityException
{
  private static final long serialVersionUID = 1L;

  private Object protectableObject;

  public NoPermissionException(Object protectableObject)
  {
    this(protectableObject, "No permission to access " + protectableObject);
  }

  public NoPermissionException(Object protectableObject, String message)
  {
    super(message);
    this.protectableObject = protectableObject;
  }

  public Object getProtectableObject()
  {
    return protectableObject;
  }
}
