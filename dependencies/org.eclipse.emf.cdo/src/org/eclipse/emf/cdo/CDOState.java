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
package org.eclipse.emf.cdo;

/**
 * Enumerates the possible states of <b>local</b> {@link CDOObject objects}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public enum CDOState
{
  TRANSIENT, NEW, CLEAN, DIRTY, PROXY, CONFLICT,

  /**
   * @since 2.0
   */
  INVALID,

  /**
   * @since 2.0
   */
  INVALID_CONFLICT,

  /**
   * An intermediary state for internal use only. This state marks the first of two phases during an attach operation.
   */
  PREPARED
}
