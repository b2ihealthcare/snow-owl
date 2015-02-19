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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class ResourcesQueryHandler implements IQueryHandler
{
  public ResourcesQueryHandler()
  {
  }

  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    QueryContext resourcesContext = new QueryContext(info, context);
    accessor.queryResources(resourcesContext);

    CDOBranchPoint branchPoint = context;
    CDOBranch branch = branchPoint.getBranch();
    while (!branch.isMainBranch() && resourcesContext.getResourceIDs().size() < info.getMaxResults())
    {
      branchPoint = branch.getBase();
      branch = branchPoint.getBranch();

      resourcesContext.setBranchPoint(branchPoint);
      accessor.queryResources(resourcesContext);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  private static final class QueryContext implements IStoreAccessor.QueryResourcesContext
  {
    private CDOQueryInfo info;

    private IQueryContext context;

    private CDOBranchPoint branchPoint;

    private Set<CDOID> resourceIDs = new HashSet<CDOID>();

    public QueryContext(CDOQueryInfo info, IQueryContext context)
    {
      this.info = info;
      this.context = context;
      branchPoint = context;
    }

    public void setBranchPoint(CDOBranchPoint branchPoint)
    {
      this.branchPoint = branchPoint;
    }

    public Set<CDOID> getResourceIDs()
    {
      return resourceIDs;
    }

    public CDOBranch getBranch()
    {
      return branchPoint.getBranch();
    }

    public long getTimeStamp()
    {
      return branchPoint.getTimeStamp();
    }

    public CDOID getFolderID()
    {
      return (CDOID)info.getParameters().get(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID);
    }

    public String getName()
    {
      return info.getQueryString();
    }

    public boolean exactMatch()
    {
      return (Boolean)info.getParameters().get(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_EXACT_MATCH);
    }

    public int getMaxResults()
    {
      return info.getMaxResults();
    }

    public boolean addResource(CDOID resourceID)
    {
      if (resourceIDs.add(resourceID))
      {
        return context.addResult(resourceID);
      }

      return true;
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Factory extends QueryHandlerFactory
  {
    public Factory()
    {
      super(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES);
    }

    @Override
    public ResourcesQueryHandler create(String description) throws ProductCreationException
    {
      return new ResourcesQueryHandler();
    }
  }
}
