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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.net4j.util.security.IUserManager;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a CDO-specific {@link URI} in connection-aware format.
 * <p>
 * CDO URIs are in one of two different formats, either canonical or connection-aware. The connection-aware format is:
 *
 * <blockquote><b>cdo.net4j.</b> <i>ConnectorType</i> <b>://</b> [<i>User</i> [<b>:</b> <i>Password</i>] <b>@</b>]
 * <i>ConnectorSpecificAuthority</i> <b>/</b> <i>RepositoryName</i> <b>/</b> <i>ResourcePath</i> [<b>?</b> <i>Param</i><b>=</b><i>Value</i>
 * (<b>&</b> <i>Param</i><b>=</b><i>Value</i>)*]</blockquote>
 *
 * The non-terminals being:
 * <p>
 * <ul>
 * <li><i>ConnectorType</i>: one of <b>tcp</b> | <b>ssl</b> | <b>jvm</b> | <b>http</b>
 * <li><i>User/Password</i>: to be provided if the repository is configured with an
 * {@link IUserManager} and, hence, triggers authentication on the client. Note: the
 * password may be stored in resources in clear text!
 * <li><i>ConnectorSpecificAuthority</i>: examples are
 * <ul>
 * <li><i>Host</i> [<b>:</b> <i>Port</i>] (if <i>ConnectorType</i> is <b>tcp</b>)
 * <li><i>AcceptorName</i> (if <i>ConnectorType</i> is <b>jvm</b>)
 * </ul>
 * <li><i>RepositoryName</i>: the {@link CDOCommonRepository#getName() name} of the repository (not the {@link CDOCommonRepository#getUUID() UUID}!).
 * <li><i>ResourcePath</i>: the full path of the {@link CDOResource resource} within the repository, segments separated by slashes, no leading slash.
 * <li><i>Param</i>: one of the following
 * <ul>
 * <li><b>branch</b>: the value must be a {@link CDOBranch#getPathName() branch path}, the full path of the branch in the branch tree, segments separated by slashes, no leading slash, defaults to <b>MAIN</b>.
 * <li><b>time</b>: the value must be the time at which the resource is supposed to be valid, parseable by SimpleDateFormat. The special value <b>HEAD</b> indicates a floating view/transaction that always shows the latest state in the chosen branch, the default if no <i>Time</i> parameter is specified.
 * <li><b>transactional</b>: a boolean value. The value <b>true</b> forces a the resource to be opened in a transaction rather than in a read-only view. This can not be combined with a <i>Time</i> other than <b>HEAD</b>.
 * <li><b>prefetch</b>: a boolean value. The value <b>true</b> attempts to load all objects contained by the resource in a single server-round trip and cache the results.
 * </ul>
 * </ul>
 * <p>
 * Note: With the current design and implementation of connection-aware URI
 * (mainly CDONet4jViewProvider) it is still unclear when and how the allocated
 * "resources" (aka IConnector, CDOSession, CDOView, etc) are supposed to be freed!
 * <p>
 * For a description of the canonical URI format refer to {@link CDOURIUtil}.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOURIData
{
  public static final String BRANCH_PARAMETER = "branch";

  public static final String TIME_PARAMETER = "time";

  /**
   * @since 4.1
   */
  public static final String VIEW_ID_PARAMETER = "view";

  public static final String TRANSACTIONAL_PARAMETER = "transactional";

  private String scheme;

  private String userName;

  private String passWord;

  private String authority;

  private String repositoryName;

  private IPath resourcePath;

  private IPath branchPath = new Path(CDOBranch.MAIN_BRANCH_NAME);

  private long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

  private String viewID;

  private boolean transactional;

  private Map<String, String> extraParameters;

  public CDOURIData()
  {
  }

  public CDOURIData(String uri) throws InvalidURIException
  {
    this(URI.createURI(uri));
  }

  public CDOURIData(URI uri) throws InvalidURIException
  {
    try
    {
      scheme = uri.scheme();
      authority = uri.authority();
      String userInfo = uri.userInfo();
      if (userInfo != null)
      {
        authority = authority.substring(userInfo.length() + 1);
        int colon = userInfo.indexOf(':');
        if (colon != -1)
        {
          userName = userInfo.substring(0, colon);
          passWord = userInfo.substring(colon + 1);
        }
        else
        {
          userName = userInfo;
        }
      }

      IPath path = new Path(uri.path()).makeAbsolute();
      repositoryName = path.segment(0);
      resourcePath = path.removeFirstSegments(1);

      String query = uri.query();
      if (query != null && query.length() != 0)
      {
        Map<String, String> parameters = CDOURIUtil.getParameters(query);
        String branch = parameters.remove(BRANCH_PARAMETER);
        if (branch != null)
        {
          branchPath = new Path(branch).makeRelative();
        }

        String time = parameters.remove(TIME_PARAMETER);
        if (time != null)
        {
          if (!"HEAD".equalsIgnoreCase(time))
          {
            timeStamp = Long.parseLong(time);
          }
        }

        viewID = parameters.remove(VIEW_ID_PARAMETER);

        String transactional = parameters.remove(TRANSACTIONAL_PARAMETER);
        if (transactional != null)
        {
          this.transactional = Boolean.parseBoolean(transactional);
        }

        if (!parameters.isEmpty())
        {
          extraParameters = parameters;
        }
      }

      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE && transactional)
      {
        throw new IllegalArgumentException("Only HEAD can be transactional");
      }
    }
    catch (Throwable t)
    {
      throw new InvalidURIException(uri, t);
    }

    // branchPath = Path.EMPTY.makeAbsolute();
    // timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    //
    // while (resourcePath.segmentCount() != 0)
    // {
    // String segment = resourcePath.segment(0);
    // resourcePath = resourcePath.removeFirstSegments(1);
    //
    // if (segment.startsWith("@"))
    // {
    // if (segment.length() != 1)
    // {
    // if (!segment.equals("@HEAD"))
    // {
    // timeStamp = Long.parseLong(segment.substring(1));
    // }
    // }
    //
    // break;
    // }
    //
    // branchPath = branchPath.append(segment);
    // }
    //
    // int segments = branchPath.segmentCount();
    // if (segments == 0 || segments == 1 && !branchPath.segment(0).equals(CDOBranch.MAIN_BRANCH_NAME))
    // {
    // branchPath = new Path(CDOBranch.MAIN_BRANCH_NAME).append(branchPath);
    // }
  }

  public String getScheme()
  {
    return scheme;
  }

  public void setScheme(String scheme)
  {
    this.scheme = scheme;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getPassWord()
  {
    return passWord;
  }

  public void setPassWord(String passWord)
  {
    this.passWord = passWord;
  }

  public String getAuthority()
  {
    return authority;
  }

  public void setAuthority(String authority)
  {
    this.authority = authority;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public IPath getResourcePath()
  {
    return resourcePath;
  }

  public void setResourcePath(IPath resourcePath)
  {
    this.resourcePath = resourcePath;
  }

  public IPath getBranchPath()
  {
    return branchPath;
  }

  public void setBranchPath(IPath branchPath)
  {
    this.branchPath = branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  /**
   * @since 4.1
   */
  public String getViewID()
  {
    return viewID;
  }

  /**
   * @since 4.1
   */
  public void setViewID(String viewID)
  {
    this.viewID = viewID;
  }

  public boolean isTransactional()
  {
    return transactional;
  }

  public void setTransactional(boolean transactional)
  {
    this.transactional = transactional;
  }

  /**
   * @since 4.1
   */
  public Map<String, String> getExtraParameters()
  {
    return extraParameters;
  }

  public URI toURI()
  {
    return URI.createURI(toString());
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(scheme);
    builder.append("://");
    if (userName != null)
    {
      builder.append(userName);
      if (passWord != null)
      {
        builder.append(":");
        builder.append(passWord);
      }

      builder.append("@");
    }

    builder.append(authority);
    builder.append("/");
    builder.append(repositoryName);

    if (resourcePath != null)
    {
      builder.append("/");
      builder.append(resourcePath);
    }

    int params = 0;
    if (branchPath != null && !branchPath.equals(new Path(CDOBranch.MAIN_BRANCH_NAME)))
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(BRANCH_PARAMETER);
      builder.append("=");
      builder.append(branchPath.toPortableString());
    }

    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(TIME_PARAMETER);
      builder.append("=");
      builder.append(timeStamp);
    }

    if (viewID != null)
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(VIEW_ID_PARAMETER);
      builder.append("=");
      builder.append(viewID);
    }

    if (transactional)
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(TRANSACTIONAL_PARAMETER);
      builder.append("=");
      builder.append(transactional);
    }

    if (extraParameters != null)
    {
      for (Entry<String, String> entry : extraParameters.entrySet())
      {
        builder.append(params++ == 0 ? "?" : "&");
        builder.append(entry.getKey());
        builder.append("=");
        builder.append(entry.getValue());
      }
    }

    return builder.toString();
  }
}
