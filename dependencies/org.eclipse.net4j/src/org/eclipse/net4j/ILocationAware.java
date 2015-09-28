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
package org.eclipse.net4j;

/**
 * A concept that has a {@link Location location} in a {@link Location#CLIENT client}/{@link Location#SERVER server}
 * scenario.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public interface ILocationAware
{
  /**
   * Returns the location of this object in a {@link Location#CLIENT client}/{@link Location#SERVER server} scenario.
   */
  public Location getLocation();

  /**
   * Same as <code>{@link #getLocation()} == {@link Location#CLIENT}</code>.
   */
  public boolean isClient();

  /**
   * Same as <code>{@link #getLocation()} == {@link Location#SERVER}</code>.
   */
  public boolean isServer();

  /**
   * A {@link Location location} in a {@link Location#CLIENT client}/{@link Location#SERVER server} scenario.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public enum Location
  {
    CLIENT, SERVER
  }
}
