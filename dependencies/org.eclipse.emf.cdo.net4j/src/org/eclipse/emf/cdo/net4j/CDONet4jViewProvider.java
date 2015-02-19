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
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.util.CDOURIData;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A {@link CDOViewProvider view provider} that uses Net4j-specific CDO {@link CDONet4jSession sessions} to open views.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDONet4jViewProvider extends AbstractCDOViewProvider
{
  private String transport;

  public CDONet4jViewProvider(String transport, int priority)
  {
    super("cdo\\.net4j\\." + transport + "://.*", priority);
    this.transport = transport;
  }

  public CDOView getView(URI uri, ResourceSet resourceSet)
  {
    CDOURIData data = new CDOURIData(uri);

    IConnector connector = getConnector(data.getAuthority());
    CDONet4jSession session = getNet4jSession(connector, data.getUserName(), data.getPassWord(),
        data.getRepositoryName());

    String viewID = data.getViewID();
    if (viewID != null)
    {
      if (data.isTransactional())
      {
        return session.openTransaction(viewID, resourceSet);
      }

      return session.openView(viewID, resourceSet);
    }

    String branchPath = data.getBranchPath().toPortableString();
    CDOBranch branch = session.getBranchManager().getBranch(branchPath);
    long timeStamp = data.getTimeStamp();

    if (data.isTransactional())
    {
      return session.openTransaction(branch, resourceSet);
    }

    return session.openView(branch, timeStamp, resourceSet);
  }

  @Override
  public URI getResourceURI(CDOView view, String path)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("cdo.net4j.");
    builder.append(transport);
    builder.append("://");

    CDONet4jSession session = (CDONet4jSession)view.getSession();

    // CDOAuthenticator authenticator = ((InternalCDOSession)session).getAuthenticator();
    // IPasswordCredentialsProvider credentialsProvider = authenticator.getCredentialsProvider();
    // if (credentialsProvider != null)
    // {
    // IPasswordCredentials credentials = credentialsProvider.getCredentials();
    // builder.append(credentials.getUserID());
    //
    // char[] password = credentials.getPassword();
    // if (password != null)
    // {
    // builder.append(":");
    // builder.append(password);
    // }
    //
    // builder.append("@");
    // }

    IConnector connector = (IConnector)session.options().getNet4jProtocol().getChannel().getMultiplexer();
    String repositoryName = session.getRepositoryInfo().getName();
    append(builder, connector, repositoryName);

    if (!path.startsWith("/"))
    {
      builder.append("/");
    }

    builder.append(path);

    int params = 0;

    String branchPath = view.getBranch().getPathName();
    if (!CDOBranch.MAIN_BRANCH_NAME.equalsIgnoreCase(branchPath))
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(CDOURIData.BRANCH_PARAMETER);
      builder.append("=");
      builder.append(branchPath);
    }

    long timeStamp = view.getTimeStamp();
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(CDOURIData.TIME_PARAMETER);
      builder.append("=");
      builder.append(new SimpleDateFormat().format(new Date(timeStamp)));
    }

    if (!view.isReadOnly())
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(CDOURIData.TRANSACTIONAL_PARAMETER);
      builder.append("=true");
    }

    return URI.createURI(builder.toString());
  }

  protected String getURIAuthority(IConnector connector)
  {
    String url = connector.getURL().toString();
    return URI.createURI(url).authority();
  }

  /**
   * @since 4.1
   */
  protected CDONet4jSession getNet4jSession(IConnector connector, String userName, String passWord,
      String repositoryName)
  {
    CDONet4jSessionConfiguration configuration = getNet4jSessionConfiguration(connector, userName, passWord,
        repositoryName);
    return configuration.openNet4jSession();
  }

  /**
   * @since 4.1
   */
  protected CDONet4jSessionConfiguration getNet4jSessionConfiguration(IConnector connector, String userName,
      String passWord, String repositoryName)
  {
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);

    IPasswordCredentialsProvider credentialsProvider = null;
    if (userName != null && passWord != null)
    {
      credentialsProvider = new PasswordCredentialsProvider(userName, passWord);
    }
    else
    {
      StringBuilder builder = new StringBuilder();
      append(builder, connector, repositoryName);
      String resource = builder.toString();

      try
      {
        credentialsProvider = (IPasswordCredentialsProvider)getContainer().getElement(
            "org.eclipse.net4j.util.credentialsProviders", "password", resource);
      }
      catch (FactoryNotFoundException ex)
      {
        // Ignore
      }
    }

    configuration.getAuthenticator().setCredentialsProvider(credentialsProvider);
    return configuration;
  }

  /**
   * @deprecated Use {@link #getNet4jSession(IConnector, String, String, String) getNet4jSession()}.
   */
  @Deprecated
  protected CDOSession getSession(IConnector connector, String userName, String passWord, String repositoryName)
  {
    return (CDOSession)getNet4jSession(connector, userName, passWord, repositoryName);
  }

  /**
   * @deprecated Use {@link #getNet4jSessionConfiguration(IConnector, String, String, String)
   *             getNet4jSessionConfiguration()}.
   */
  @Deprecated
  protected CDOSessionConfiguration getSessionConfiguration(IConnector connector, String userName, String passWord,
      String repositoryName)
  {
    return (CDOSessionConfiguration)getNet4jSessionConfiguration(connector, userName, passWord, repositoryName);
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected IConnector getConnector(String authority)
  {
    IManagedContainer container = getContainer();
    String description = getConnectorDescription(authority);
    return Net4jUtil.getConnector(container, transport, description);
  }

  protected String getConnectorDescription(String authority)
  {
    return authority;
  }

  private void append(StringBuilder builder, IConnector connector, String repositoryName)
  {
    String authority = getURIAuthority(connector);
    builder.append(authority);

    builder.append("/");
    builder.append(repositoryName);
  }

  /**
   * A TCP-based {@link CDONet4jViewProvider view provider}.
   *
   * @author Eike Stepper
   */
  public static class TCP extends CDONet4jViewProvider
  {
    public TCP(int priority)
    {
      super("tcp", priority);
    }

    public TCP()
    {
      this(DEFAULT_PRIORITY);
    }

  }

  /**
   * An SSL-based {@link CDONet4jViewProvider view provider}.
   *
   * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
   */
  public static class SSL extends CDONet4jViewProvider
  {
    public SSL(int priority)
    {
      super("ssl", priority);
    }

    public SSL()
    {
      this(DEFAULT_PRIORITY);
    }

  }

  /**
   * A JVM-based {@link CDONet4jViewProvider view provider}.
   *
   * @author Eike Stepper
   */
  public static class JVM extends CDONet4jViewProvider
  {
    public JVM(int priority)
    {
      super("jvm", priority);
    }

    public JVM()
    {
      this(DEFAULT_PRIORITY);
    }
  }
}
