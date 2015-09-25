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

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.security.SecureRandom;

/**
 * @author Eike Stepper
 */
public class Randomizer extends Lifecycle implements IRandomizer
{
  public static final String ALGORITHM_SHA1PRNG = "SHA1PRNG"; //$NON-NLS-1$

  public static final String DEFAULT_ALGORITHM_NAME = ALGORITHM_SHA1PRNG;

  private String algorithmName = DEFAULT_ALGORITHM_NAME;

  private String providerName;

  @ExcludeFromDump
  private byte[] seed;

  @ExcludeFromDump
  private transient SecureRandom secureRandom;

  public Randomizer()
  {
  }

  public synchronized String getAlgorithmName()
  {
    return algorithmName;
  }

  public synchronized void setAlgorithmName(String algorithmName)
  {
    this.algorithmName = algorithmName;
  }

  public synchronized String getProviderName()
  {
    return providerName;
  }

  public synchronized void setProviderName(String providerName)
  {
    this.providerName = providerName;
  }

  public synchronized void setSeed(byte[] seed)
  {
    this.seed = seed;
    if (isActive())
    {
      setSeed();
    }
  }

  public synchronized void setSeed(long seed)
  {
    setSeed(String.valueOf(seed).getBytes());
  }

  public synchronized boolean nextBoolean()
  {
    checkActive();
    return secureRandom.nextBoolean();
  }

  public synchronized double nextDouble()
  {
    checkActive();
    return secureRandom.nextDouble();
  }

  public synchronized float nextFloat()
  {
    checkActive();
    return secureRandom.nextFloat();
  }

  public synchronized double nextGaussian()
  {
    checkActive();
    return secureRandom.nextGaussian();
  }

  public synchronized int nextInt()
  {
    checkActive();
    return secureRandom.nextInt();
  }

  public synchronized int nextInt(int n)
  {
    checkActive();
    return secureRandom.nextInt(n);
  }

  public synchronized long nextLong()
  {
    checkActive();
    return secureRandom.nextLong();
  }

  public synchronized byte[] generateSeed(int numBytes)
  {
    checkActive();
    return secureRandom.generateSeed(numBytes);
  }

  public synchronized String getAlgorithm()
  {
    checkActive();
    return secureRandom.getAlgorithm();
  }

  public synchronized void nextBytes(byte[] bytes)
  {
    checkActive();
    secureRandom.nextBytes(bytes);
  }

  public synchronized String nextString(int length, String alphabet)
  {
    checkActive();
    int n = alphabet.length();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++)
    {
      int pos = nextInt(n);
      char c = alphabet.charAt(pos);
      builder.append(c);
    }

    return builder.toString();
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(algorithmName, "algorithmName"); //$NON-NLS-1$
    if (seed == null)
    {
      setSeed(System.currentTimeMillis());
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (providerName == null)
    {
      secureRandom = SecureRandom.getInstance(algorithmName);
    }
    else
    {
      secureRandom = SecureRandom.getInstance(algorithmName, providerName);
    }

    setSeed();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    secureRandom = null;
    super.doDeactivate();
  }

  private void setSeed()
  {
    secureRandom.setSeed(seed);
  }
}
