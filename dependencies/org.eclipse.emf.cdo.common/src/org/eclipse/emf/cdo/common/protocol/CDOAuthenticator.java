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
package org.eclipse.emf.cdo.common.protocol;

import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * The front-end of the CDO challenge/response authentication.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOAuthenticator
{
  public String getEncryptionAlgorithmName();

  public void setEncryptionAlgorithmName(String encryptionAlgorithmName);

  public byte[] getEncryptionSaltBytes();

  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes);

  public int getEncryptionIterationCount();

  public void setEncryptionIterationCount(int encryptionIterationCount);

  public IPasswordCredentialsProvider getCredentialsProvider();

  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider);

  /**
   * @since 4.0
   */
  public CDOAuthenticationResult authenticate(byte[] randomToken);
}
