/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * An unchecked exception being thrown if {@link CDOLock locks} could not be
 * {@link CDOView#lockObjects(java.util.Collection, org.eclipse.net4j.util.concurrent.IRWLockManager.LockType, long)
 * acquired} within the specified timeout period.
 * 
 * @author Caspar De Groot
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class LockTimeoutException extends CDOException
{
  private static final long serialVersionUID = 293135415513673577L;

  public LockTimeoutException()
  {
  }
}
