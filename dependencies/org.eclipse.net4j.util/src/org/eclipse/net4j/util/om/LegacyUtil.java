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
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.om.LegacyBundle;

/**
 * Various static helper methods for dealing with {@link OMBundle bundles} if OSGi {@link OMPlatform#isOSGiRunning() is
 * not running}.
 * 
 * @author Eike Stepper
 */
public final class LegacyUtil
{
  private static final String[] UNINITIALIZED = {};

  private static String[] commandLineArgs = UNINITIALIZED;

  private LegacyUtil()
  {
  }

  /**
   * @since 3.2
   */
  public static String[] getCommandLineArgs() throws IllegalStateException
  {
    if (commandLineArgs == UNINITIALIZED)
    {
      throw new IllegalStateException("Command line argumentshave not been set");
    }

    return commandLineArgs;
  }

  /**
   * @since 3.2
   */
  public static void setCommandLineArgs(String[] commandLineArgs)
  {
    LegacyUtil.commandLineArgs = commandLineArgs;
  }

  public static void startBundles(OMBundle[] bundles) throws Exception
  {
    for (int i = 0; i < bundles.length; i++)
    {
      ((LegacyBundle)bundles[i]).start();
    }
  }

  public static void stopBundles(OMBundle[] bundles) throws Exception
  {
    for (int i = bundles.length - 1; i >= 0; i--)
    {
      ((LegacyBundle)bundles[i]).stop();
    }
  }
}
