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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.spi.server.PluginRepositoryProvider;

/**
 * @author Eike Stepper
 */
public final class CDOPluginProtocolFactory extends CDOServerProtocolFactory
{
  public CDOPluginProtocolFactory()
  {
    super(PluginRepositoryProvider.INSTANCE);
  }
}
