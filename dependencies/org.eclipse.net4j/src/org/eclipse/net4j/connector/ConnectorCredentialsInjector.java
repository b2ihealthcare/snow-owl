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
package org.eclipse.net4j.connector;

import org.eclipse.net4j.util.security.INegotiatorAware;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.security.ResponseNegotiatorInjector;
import org.eclipse.net4j.util.security.SecurityUtil;

/**
 * Injects a configurable response negotiator into selected client connectors.
 * <p>
 * An example:
 * 
 * <pre>
 * IManagedContainer container = IPluginContainer.INSTANCE;
 * 
 * String connectorDescription = &quot;localhost:2036&quot;;
 * String userID = &quot;name&quot;;
 * String password = &quot;secret&quot;;
 * 
 * IPasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(userID, password);
 * 
 * container.addPostProcessor(new ConnectorCredentialsInjector(connectorDescription, credentialsProvider));
 * IConnector connector = (IConnector)container.getElement(&quot;org.eclipse.net4j.connectors&quot;, &quot;tcp&quot;, connectorDescription);
 * 
 * IChannel channel = connector.openChannel();
 * // ...
 * </pre>
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class ConnectorCredentialsInjector extends ResponseNegotiatorInjector
{
  private String connectorDescription;

  /**
   * @param connectorDescription
   *          The description of the IConnector that the negotiator shall be injected into, or <code>null</code> to
   *          bypass the description check.
   */
  public ConnectorCredentialsInjector(String connectorDescription, IPasswordCredentialsProvider credentialsProvider,
      String algorithmName)
  {
    super(createNegotiator(credentialsProvider, algorithmName));
    this.connectorDescription = connectorDescription;
  }

  /**
   * @param connectorDescription
   *          The description of the IConnector that the negotiator shall be injected into, or <code>null</code> to
   *          bypass the description check.
   */
  public ConnectorCredentialsInjector(String connectorDescription, IPasswordCredentialsProvider credentialsProvider)
  {
    this(connectorDescription, credentialsProvider, SecurityUtil.PBE_WITH_MD5_AND_DES);
  }

  @Override
  protected boolean filterElement(String productGroup, String factoryType, String description,
      INegotiatorAware negotiatorAware)
  {
    if (negotiatorAware instanceof IConnector)
    {
      IConnector connector = (IConnector)negotiatorAware;
      if (connector.isClient())
      {
        return filterConnectorDescription(description);
      }
    }

    return false;
  }

  protected boolean filterConnectorDescription(String description)
  {
    if (connectorDescription == null)
    {
      return true;
    }

    return connectorDescription.equals(description);
  }

  private static ResponseNegotiator createNegotiator(IPasswordCredentialsProvider credentialsProvider,
      String algorithmName)
  {
    ResponseNegotiator negotiator = new ResponseNegotiator();
    negotiator.setCredentialsProvider(credentialsProvider);
    negotiator.setEncryptionAlgorithmName(algorithmName);
    return negotiator;
  }
}
