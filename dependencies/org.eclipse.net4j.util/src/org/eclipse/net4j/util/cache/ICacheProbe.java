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
package org.eclipse.net4j.util.cache;

/**
 * @author Eike Stepper
 */
public interface ICacheProbe
{
  public boolean isDisposed();

  public void elementCached(int elementSize);

  public void elementEvicted(int elementSize);

  public void elementReconstructed(long reconstructionTime);

  public int getElementCount();

  public long getCacheSize();

  public long getAverageElementSize();

  public long getReconstructionCost();
}
