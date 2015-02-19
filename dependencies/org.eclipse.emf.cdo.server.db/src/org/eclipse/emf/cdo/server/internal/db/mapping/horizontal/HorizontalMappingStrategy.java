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
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends Lifecycle implements IMappingStrategy
{
  private Map<String, String> properties;

  private IDBStore store;

  private IMappingStrategy delegate;

  public HorizontalMappingStrategy()
  {
  }

  public IMappingStrategy getDelegate()
  {
    return delegate;
  }

  public Map<String, String> getProperties()
  {
    if (delegate != null)
    {
      return delegate.getProperties();
    }

    if (properties != null)
    {
      return properties;
    }

    return new HashMap<String, String>();
  }

  public void setProperties(Map<String, String> properties)
  {
    if (delegate != null)
    {
      delegate.setProperties(properties);
    }
    else
    {
      this.properties = properties;
    }
  }

  public IDBStore getStore()
  {
    if (delegate != null)
    {
      return delegate.getStore();
    }

    return store;
  }

  public void setStore(IDBStore store)
  {
    if (delegate != null)
    {
      delegate.setStore(store);
    }
    else
    {
      this.store = store;
    }
  }

  public ITypeMapping createValueMapping(EStructuralFeature feature)
  {
    return delegate.createValueMapping(feature);
  }

  public IListMapping createListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return delegate.createListMapping(containingClass, feature);
  }

  public String getTableName(ENamedElement element)
  {
    return delegate.getTableName(element);
  }

  public String getTableName(EClass containingClass, EStructuralFeature feature)
  {
    return delegate.getTableName(containingClass, feature);
  }

  public String getFieldName(EStructuralFeature feature)
  {
    return delegate.getFieldName(feature);
  }

  public void createMapping(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    delegate.createMapping(connection, packageUnits, monitor);
  }

  public void removeMapping(Connection connection, InternalCDOPackageUnit[] packageUnits)
  {
    delegate.removeMapping(connection, packageUnits);
  }

  public IClassMapping getClassMapping(EClass eClass)
  {
    return delegate.getClassMapping(eClass);
  }

  public Map<EClass, IClassMapping> getClassMappings()
  {
    return delegate.getClassMappings();
  }

  public Map<EClass, IClassMapping> getClassMappings(boolean createOnDemand)
  {
    return delegate.getClassMappings(createOnDemand);
  }

  public boolean hasDeltaSupport()
  {
    return delegate.hasDeltaSupport();
  }

  public boolean hasAuditSupport()
  {
    return delegate.hasAuditSupport();
  }

  public boolean hasBranchingSupport()
  {
    return delegate.hasBranchingSupport();
  }

  public void queryResources(IDBStoreAccessor accessor, QueryResourcesContext context)
  {
    delegate.queryResources(accessor, context);
  }

  public void queryXRefs(IDBStoreAccessor accessor, QueryXRefsContext context)
  {
    delegate.queryXRefs(accessor, context);
  }

  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    return delegate.readObjectType(accessor, id);
  }

  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor accessor)
  {
    return delegate.readObjectIDs(accessor);
  }

  public void repairAfterCrash(IDBAdapter dbAdapter, Connection connection)
  {
    delegate.repairAfterCrash(dbAdapter, connection);
  }

  public void handleRevisions(IDBStoreAccessor accessor, EClass eClass, CDOBranch branch, long timeStamp,
      boolean exactTime, CDORevisionHandler handler)
  {
    delegate.handleRevisions(accessor, eClass, branch, timeStamp, exactTime, handler);
  }

  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, OMMonitor monitor, String[] nsURIs,
      CDOChangeSetSegment[] segments)
  {
    return delegate.readChangeSet(accessor, monitor, nsURIs, segments);
  }

  public void rawExport(IDBStoreAccessor accessor, CDODataOutput out, int lastReplicatedBranchID, int lastBranchID,
      long lastReplicatedCommitTime, long lastCommitTime) throws IOException
  {
    delegate.rawExport(accessor, out, lastReplicatedBranchID, lastBranchID, lastReplicatedCommitTime, lastCommitTime);
  }

  public void rawImport(IDBStoreAccessor accessor, CDODataInput in, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    delegate.rawImport(accessor, in, fromCommitTime, toCommitTime, monitor);
  }

  public String getListJoin(String attrTable, String listTable)
  {
    return delegate.getListJoin(attrTable, listTable);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    boolean auditing = getBooleanProperty(Props.SUPPORTING_AUDITS);
    boolean branching = getBooleanProperty(Props.SUPPORTING_BRANCHES);

    boolean withRanges = false;
    if (auditing || branching)
    {
      withRanges = getBooleanProperty(CDODBUtil.PROP_WITH_RANGES);
    }

    delegate = CDODBUtil.createHorizontalMappingStrategy(auditing, branching, withRanges);
    delegate.setStore(store);
    delegate.setProperties(properties);
    LifecycleUtil.activate(delegate);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(delegate);
    super.doDeactivate();
  }

  private boolean getBooleanProperty(String prop)
  {
    String valueAudits = properties.get(prop);
    if (valueAudits != null)
    {
      return Boolean.valueOf(valueAudits);
    }

    return false;
  }
}
