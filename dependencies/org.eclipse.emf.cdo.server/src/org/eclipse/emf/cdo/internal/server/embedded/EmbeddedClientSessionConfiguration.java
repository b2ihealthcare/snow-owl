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
package org.eclipse.emf.cdo.internal.server.embedded;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.embedded.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.internal.cdo.session.CDOSessionConfigurationImpl;

import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.Set;

/**
 * @author Eike Stepper
 * @deprecated Not yet supported.
 */
@Deprecated
public class EmbeddedClientSessionConfiguration extends CDOSessionConfigurationImpl implements CDOSessionConfiguration
{
  private InternalRepository repository;

  public EmbeddedClientSessionConfiguration()
  {
    throw new UnsupportedOperationException("Embedded sessions are not yet supported");
  }

  public InternalRepository getRepository()
  {
    return repository;
  }

  public void setRepository(IRepository repository)
  {
    checkNotOpen();
    this.repository = (InternalRepository)repository;
  }

  @Override
  public org.eclipse.emf.cdo.server.embedded.CDOSession openSession()
  {
    return (org.eclipse.emf.cdo.server.embedded.CDOSession)super.openSession();
  }

  public InternalCDOSession createSession()
  {
    if (isActivateOnOpen())
    {
      CheckUtil.checkState(repository, "Specify a repository"); //$NON-NLS-1$
    }

    return new EmbeddedClientSession();
  }

  /**
   * @author Eike Stepper
   */
  protected static class RepositoryInfo implements org.eclipse.emf.cdo.session.CDORepositoryInfo
  {
    private EmbeddedClientSession session;

    public RepositoryInfo(EmbeddedClientSession session)
    {
      this.session = session;
    }

    public String getName()
    {
      return session.getRepository().getName();
    }

    public String getUUID()
    {
      return session.getRepository().getUUID();
    }

    public Type getType()
    {
      return session.getRepository().getType();
    }

    public State getState()
    {
      return session.getRepository().getState();
    }

    public long getCreationTime()
    {
      return session.getRepository().getCreationTime();
    }

    public long getTimeStamp()
    {
      return getTimeStamp(false);
    }

    public long getTimeStamp(boolean forceRefresh)
    {
      return System.currentTimeMillis();
    }

    public CDOID getRootResourceID()
    {
      return session.getRepository().getRootResourceID();
    }

    public boolean isSupportingAudits()
    {
      return session.getRepository().isSupportingAudits();
    }

    public boolean isSupportingBranches()
    {
      return session.getRepository().isSupportingBranches();
    }

    public boolean isSupportingEcore()
    {
      return session.getRepository().isSupportingEcore();
    }

    public boolean isEnsuringReferentialIntegrity()
    {
      return session.getRepository().isEnsuringReferentialIntegrity();
    }

    public IDGenerationLocation getIDGenerationLocation()
    {
      return session.getRepository().getIDGenerationLocation();
    }

    public String getStoreType()
    {
      return session.getRepository().getStoreType();
    }

    public Set<ObjectType> getObjectIDTypes()
    {
      return session.getRepository().getObjectIDTypes();
    }
  }
}
