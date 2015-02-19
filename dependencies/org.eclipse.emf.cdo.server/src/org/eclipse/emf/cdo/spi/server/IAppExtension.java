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
package org.eclipse.emf.cdo.spi.server;

import java.io.File;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface IAppExtension
{
  public static final String EXT_POINT = "appExtensions"; //$NON-NLS-1$

  public void start(File configFile) throws Exception;

  public void stop() throws Exception;
}
