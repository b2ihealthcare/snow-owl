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

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class RandomizerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.randomizers"; //$NON-NLS-1$

  public static final String TYPE = "default"; //$NON-NLS-1$

  public RandomizerFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public Randomizer create(String description) throws ProductCreationException
  {
    Randomizer randomizer = new Randomizer();
    if (description != null)
    {
      randomizer.setAlgorithmName(description);
    }

    return randomizer;
  }
}
