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
public class PasswordCredentialsProvider implements IPasswordCredentialsProvider
{
  private IPasswordCredentials credentials;

  /**
   * @since 3.1
   */
  public PasswordCredentialsProvider()
  {
  }

  public PasswordCredentialsProvider(IPasswordCredentials credentials)
  {
    this.credentials = credentials;
  }

  /**
   * @since 2.0
   */
  public PasswordCredentialsProvider(String userID, char[] password)
  {
    this(new PasswordCredentials(userID, password));
  }

  /**
   * @since 2.0
   */
  public PasswordCredentialsProvider(String userID, String password)
  {
    this(userID, password.toCharArray());
  }

  public boolean isInteractive()
  {
    return false;
  }

  public IPasswordCredentials getCredentials()
  {
    return credentials;
  }

  /**
   * @since 3.1
   */
  public void setCredentials(IPasswordCredentials credentials)
  {
    this.credentials = credentials;
  }

  /**
   * @author Eike Stepper
   * @since 3.1
   */
  public static class Delegating extends PasswordCredentialsProvider
  {
    private IPasswordCredentialsProvider delegate;

    public Delegating(IPasswordCredentialsProvider delegate)
    {
      this.delegate = delegate;
    }

    public IPasswordCredentialsProvider getDelegate()
    {
      return delegate;
    }

    @Override
    public boolean isInteractive()
    {
      return delegate.isInteractive();
    }

    @Override
    public IPasswordCredentials getCredentials()
    {
      IPasswordCredentials credentials = super.getCredentials();
      if (credentials == null)
      {
        credentials = delegate.getCredentials();
        setCredentials(credentials);
      }

      return credentials;
    }
  }
}
