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
package org.eclipse.emf.cdo.net4j;

/**
 * A {@link RecoveringCDOSessionConfiguration session configuration} that recovers from network problems by failing over
 * to backup repositories as directed by a fail-over monitor.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface FailoverCDOSessionConfiguration extends RecoveringCDOSessionConfiguration
{
  public String getMonitorConnectorDescription();

  public String getRepositoryGroup();
}
