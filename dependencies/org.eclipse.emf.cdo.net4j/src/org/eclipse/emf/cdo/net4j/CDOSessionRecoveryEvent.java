/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.session.CDOSessionEvent;

/**
 * A {@link CDOSessionEvent session event} fired from {@link RecoveringCDOSessionConfiguration recovering session} when
 * recovery has started or finished.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSessionRecoveryEvent extends CDOSessionEvent
{
  public Type getType();

  /**
   * Enumerates the possible types of {@link CDOSessionRecoveryEvent session recovery events}.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   */
  public enum Type
  {
    STARTED, FINISHED
  }
}
