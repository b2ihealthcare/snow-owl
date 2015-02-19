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
package org.eclipse.emf.cdo.spi.common.admin;

/**
 * Symbolic protocol constants commonly used in CDO administration.
 *
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAdminProtocolConstants
{
  public static final String PROTOCOL_NAME = "cdo-admin"; //$NON-NLS-1$

  // //////////////////////////////////////////////////////////////////////
  // Signal IDs

  public static final short SIGNAL_QUERY_REPOSITORIES = 1;

  public static final short SIGNAL_CREATE_REPOSITORY = 2;

  public static final short SIGNAL_DELETE_REPOSITORY = 3;

  public static final short SIGNAL_REPOSITORY_ADDED = 4;

  public static final short SIGNAL_REPOSITORY_REMOVED = 5;

  public static final short SIGNAL_REPOSITORY_TYPE_CHANGED = 6;

  public static final short SIGNAL_REPOSITORY_STATE_CHANGED = 7;

  public static final short SIGNAL_REPOSITORY_REPLICATION_PROGRESSED = 8;
}
