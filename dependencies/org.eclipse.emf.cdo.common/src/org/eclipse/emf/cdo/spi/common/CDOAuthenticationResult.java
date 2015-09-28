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
package org.eclipse.emf.cdo.spi.common;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * The result of an authentication operation. Carries a userID and a crypted token.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOAuthenticationResult implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String userID;

  private byte[] cryptedToken;

  public CDOAuthenticationResult(String userID, byte[] cryptedToken)
  {
    this.userID = userID;
    this.cryptedToken = cryptedToken;
  }

  public CDOAuthenticationResult(ExtendedDataInput in) throws IOException
  {
    userID = in.readString();
    cryptedToken = in.readByteArray();
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeString(userID);
    out.writeByteArray(cryptedToken);
  }

  public String getUserID()
  {
    return userID;
  }

  public byte[] getCryptedToken()
  {
    return cryptedToken;
  }
}
