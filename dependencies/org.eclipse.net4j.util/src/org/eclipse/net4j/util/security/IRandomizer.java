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
package org.eclipse.net4j.util.security;

/**
 * @author Eike Stepper
 */
public interface IRandomizer
{
  public byte[] generateSeed(int numBytes);

  public boolean nextBoolean();

  public double nextDouble();

  public float nextFloat();

  public double nextGaussian();

  public int nextInt();

  public int nextInt(int n);

  public long nextLong();

  public void nextBytes(byte[] bytes);

  public String nextString(int length, String alphabet);
}
