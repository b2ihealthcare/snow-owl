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

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.SecurityUtil;
import org.eclipse.net4j.util.security.UserManagerFactory;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class RepositoryUserManager extends Lifecycle implements IUserManager
{
  private IManagedContainer container;

  private String repositoryName;

  protected RepositoryUserManager()
  {
  }

  private void setContainer(IManagedContainer container)
  {
    this.container = container;
  }

  private void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public void addUser(String userID, char[] password)
  {
    // Cann be overridden in subclasses.
  }

  public void removeUser(String userID)
  {
    // Cann be overridden in subclasses.
  }

  public byte[] encrypt(String userID, byte[] data, String algorithmName, byte[] salt, int count)
      throws SecurityException
  {
    IRepository repository = getRepository(container, repositoryName);
    if (repository == null)
    {
      throw new SecurityException("Repository not found: " + repositoryName); //$NON-NLS-1$
    }

    char[] password = getPassword(repository, userID);
    if (password == null)
    {
      throw new SecurityException("No such user: " + userID); //$NON-NLS-1$
    }

    try
    {
      return SecurityUtil.encrypt(data, password, algorithmName, salt, count);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new SecurityException(ex);
    }
  }

  protected IRepository getRepository(IManagedContainer container, String repositoryName)
  {
    return CDOServerUtil.getRepository(container, repositoryName);
  }

  protected abstract char[] getPassword(IRepository repository, String userID);

  public static void prepareContainer(IManagedContainer container, RepositoryUserManagerFactory factory)
  {
    container.registerFactory(factory);
    container.addPostProcessor(new RepositoryInjector());
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class RepositoryUserManagerFactory extends UserManagerFactory
  {
    protected RepositoryUserManagerFactory(String type)
    {
      super(type);
    }

    public final Object create(String description) throws ProductCreationException
    {
      RepositoryUserManager userManager = doCreate(description);
      String repositoryName = getRepositoryName(description);
      userManager.setRepositoryName(repositoryName);
      return userManager;
    }

    protected String getRepositoryName(String description)
    {
      return description;
    }

    protected abstract RepositoryUserManager doCreate(String description) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public static class RepositoryInjector implements IElementProcessor
  {
    public RepositoryInjector()
    {
    }

    public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
        Object element)
    {
      if (element instanceof RepositoryUserManager)
      {
        RepositoryUserManager userManager = (RepositoryUserManager)element;
        userManager.setContainer(container);
      }

      return element;
    }
  }
}
