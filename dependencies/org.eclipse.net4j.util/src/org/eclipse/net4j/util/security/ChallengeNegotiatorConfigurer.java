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

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class ChallengeNegotiatorConfigurer implements IElementProcessor
{
  public ChallengeNegotiatorConfigurer()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof ChallengeNegotiator)
    {
      ChallengeNegotiator negotiator = (ChallengeNegotiator)element;
      if (negotiator.getRandomizer() == null)
      {
        IRandomizer randomizer = getRandomizer(container, description);
        negotiator.setRandomizer(randomizer);
      }

      if (negotiator.getUserManager() == null)
      {
        IUserManager userManager = getUserManager(container, description);
        negotiator.setUserManager(userManager);
      }
    }

    return element;
  }

  protected IRandomizer getRandomizer(IManagedContainer container, String description)
  {
    String productGroup = RandomizerFactory.PRODUCT_GROUP;
    String type = getRandomizerType(description);
    return (IRandomizer)container.getElement(productGroup, type, getRandomizerDescription(description));
  }

  protected String getRandomizerType(String description)
  {
    return RandomizerFactory.TYPE;
  }

  protected String getRandomizerDescription(String description)
  {
    return null;
  }

  protected IUserManager getUserManager(IManagedContainer container, String description)
  {
    String productGroup = FileUserManagerFactory.PRODUCT_GROUP;
    String type = getUserManagerType(description);
    return (IUserManager)container.getElement(productGroup, type, getUserManagerDescription(description));
  }

  protected String getUserManagerType(String description)
  {
    return FileUserManagerFactory.TYPE;
  }

  protected String getUserManagerDescription(String description)
  {
    return description;
  }
}
