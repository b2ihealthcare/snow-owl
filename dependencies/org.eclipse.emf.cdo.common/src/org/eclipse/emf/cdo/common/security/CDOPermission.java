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
 * Describes the possible protection levels a protectable object may have.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public enum CDOPermission
{
  NONE(0x00), READ(0x01), WRITE(0x03);

  private byte bits;

  private CDOPermission(int bits)
  {
    this.bits = (byte)bits;
  }

  public byte getBits()
  {
    return bits;
  }

  /**
   * @since 4.1
   */
  public boolean isReadable()
  {
    return this != CDOPermission.NONE;
  }

  /**
   * @since 4.1
   */
  public boolean isWritable()
  {
    return this == CDOPermission.WRITE;
  }

  public static CDOPermission get(int bits)
  {
    switch (bits)
    {
    case 0x00:
      return NONE;
    case 0x01:
      return READ;
    case 0x03:
      return WRITE;
    default:
      throw new IllegalArgumentException("Invalid bits: " + bits);
    }
  }
}
