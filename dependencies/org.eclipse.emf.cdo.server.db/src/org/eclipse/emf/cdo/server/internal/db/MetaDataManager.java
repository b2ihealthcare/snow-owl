/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 271444: [DB] Multiple refactorings
 *    Kai Schlamp - bug 282976: [DB] Influence Mappings through EAnnotations
 *    Stefan Winkler - bug 282976: [DB] Influence Mappings through EAnnotations
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Eike Stepper
 */
public class MetaDataManager extends Lifecycle implements IMetaDataManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MetaDataManager.class);

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private IDBStore store;

  private Map<EModelElement, CDOID> modelElementToMetaID = new HashMap<EModelElement, CDOID>();

  private Map<CDOID, EModelElement> metaIDToModelElement = new HashMap<CDOID, EModelElement>();

  public MetaDataManager(IDBStore store)
  {
    this.store = store;
  }

  public synchronized CDOID getMetaID(EModelElement modelElement, long commitTime)
  {
    CDOID metaID = modelElementToMetaID.get(modelElement);
    if (metaID != null)
    {
      return metaID;
    }

    IDBStoreAccessor accessor = (IDBStoreAccessor)StoreThreadLocal.getAccessor();
    String uri = EcoreUtil.getURI(modelElement).toString();
    metaID = store.getIDHandler().mapURI(accessor, uri, commitTime);
    cacheMetaIDMapping(modelElement, metaID);

    return metaID;
  }

  public synchronized EModelElement getMetaInstance(CDOID id)
  {
    EModelElement modelElement = metaIDToModelElement.get(id);
    if (modelElement != null)
    {
      return modelElement;
    }

    IDBStoreAccessor accessor = (IDBStoreAccessor)StoreThreadLocal.getAccessor();
    String uri = store.getIDHandler().unmapURI(accessor, id);

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.setPackageRegistry(getStore().getRepository().getPackageRegistry());

    return (EModelElement)resourceSet.getEObject(URI.createURI(uri), true);
  }

  public synchronized void clearMetaIDMappings()
  {
    modelElementToMetaID.clear();
    metaIDToModelElement.clear();
  }

  public final EPackage[] loadPackageUnit(Connection connection, InternalCDOPackageUnit packageUnit)
  {
    String where = CDODBSchema.PACKAGE_UNITS_ID.getName() + "='" + packageUnit.getID() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
    Object[] values = DBUtil.select(connection, where, CDODBSchema.PACKAGE_UNITS_PACKAGE_DATA);
    byte[] bytes = (byte[])values[0];
    EPackage ePackage = createEPackage(packageUnit, bytes);
    return EMFUtil.getAllPackages(ePackage);
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection)
  {
    return readPackageUnits(connection, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, new Monitor());
  }

  public final void writePackageUnits(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    try
    {
      monitor.begin(2);
      fillSystemTables(connection, packageUnits, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    // Export package units
    String where = " WHERE p_u." + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelConstants.CORE_PACKAGE_URI + //
        "' AND p_u." + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelConstants.RESOURCE_PACKAGE_URI + //
        "' AND p_u." + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelConstants.TYPES_PACKAGE_URI + //
        "' AND p_u." + CDODBSchema.PACKAGE_UNITS_TIME_STAMP + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, CDODBSchema.PACKAGE_UNITS, "p_u", where);

    // Export package infos
    String join = ", " + CDODBSchema.PACKAGE_UNITS + " p_u" + where + " AND p_i." + CDODBSchema.PACKAGE_INFOS_UNIT
        + "=p_u." + CDODBSchema.PACKAGE_UNITS_ID;
    DBUtil.serializeTable(out, connection, CDODBSchema.PACKAGE_INFOS, "p_i", join);
  }

  public Collection<InternalCDOPackageUnit> rawImport(Connection connection, CDODataInput in, long fromCommitTime,
      long toCommitTime, OMMonitor monitor) throws IOException
  {
    monitor.begin(3);

    try
    {
      DBUtil.deserializeTable(in, connection, CDODBSchema.PACKAGE_UNITS, monitor.fork());
      DBUtil.deserializeTable(in, connection, CDODBSchema.PACKAGE_INFOS, monitor.fork());
      return readPackageUnits(connection, fromCommitTime, toCommitTime, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  protected IDBStore getStore()
  {
    return store;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    checkState(store, "Store is not set"); //$NON-NLS-1$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    clearMetaIDMappings();
    super.doDeactivate();
  }

  protected InternalCDOPackageInfo createPackageInfo()
  {
    return (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
  }

  protected InternalCDOPackageUnit createPackageUnit()
  {
    return (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
  }

  private InternalCDOPackageRegistry getPackageRegistry()
  {
    return (InternalCDOPackageRegistry)store.getRepository().getPackageRegistry();
  }

  private EPackage createEPackage(InternalCDOPackageUnit packageUnit, byte[] bytes)
  {
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(getPackageRegistry());
    return EMFUtil.createEPackage(packageUnit.getID(), bytes, ZIP_PACKAGE_BYTES, resourceSet, false);
  }

  private byte[] getEPackageBytes(InternalCDOPackageUnit packageUnit)
  {
    EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
    return EMFUtil.getEPackageBytes(ePackage, ZIP_PACKAGE_BYTES, getPackageRegistry());
  }

  private void fillSystemTables(Connection connection, InternalCDOPackageUnit packageUnit, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing package unit: {0}", packageUnit); //$NON-NLS-1$
    }

    InternalCDOPackageInfo[] packageInfos = packageUnit.getPackageInfos();
    Async async = null;
    monitor.begin(1 + packageInfos.length);

    try
    {
      String sql = "INSERT INTO " + CDODBSchema.PACKAGE_UNITS + " VALUES (?, ?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$
      DBUtil.trace(sql);
      PreparedStatement stmt = null;

      try
      {
        async = monitor.forkAsync();
        stmt = connection.prepareStatement(sql);
        stmt.setString(1, packageUnit.getID());
        stmt.setInt(2, packageUnit.getOriginalType().ordinal());
        stmt.setLong(3, packageUnit.getTimeStamp());
        stmt.setBytes(4, getEPackageBytes(packageUnit));

        if (stmt.execute())
        {
          throw new DBException("No result set expected"); //$NON-NLS-1$
        }

        if (stmt.getUpdateCount() == 0)
        {
          throw new DBException("No row inserted into table " + CDODBSchema.PACKAGE_UNITS); //$NON-NLS-1$
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        DBUtil.close(stmt);
        if (async != null)
        {
          async.stop();
        }
      }

      for (InternalCDOPackageInfo packageInfo : packageInfos)
      {
        fillSystemTables(connection, packageInfo, monitor); // Don't fork monitor
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void fillSystemTables(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    try
    {
      monitor.begin(packageUnits.length);
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        fillSystemTables(connection, packageUnit, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void fillSystemTables(Connection connection, InternalCDOPackageInfo packageInfo, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing package info: {0}", packageInfo); //$NON-NLS-1$
    }

    String packageURI = packageInfo.getPackageURI();
    String parentURI = packageInfo.getParentURI();
    String unitID = packageInfo.getPackageUnit().getID();

    String sql = "INSERT INTO " + CDODBSchema.PACKAGE_INFOS + " VALUES (?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$
    DBUtil.trace(sql);
    PreparedStatement stmt = null;
    Async async = monitor.forkAsync();

    try
    {
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, packageURI);
      stmt.setString(2, parentURI);
      stmt.setString(3, unitID);

      if (stmt.execute())
      {
        throw new DBException("No result set expected"); //$NON-NLS-1$
      }

      if (stmt.getUpdateCount() == 0)
      {
        throw new DBException("No row inserted into table " + CDODBSchema.PACKAGE_INFOS); //$NON-NLS-1$
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
      if (async != null)
      {
        async.stop();
      }
    }
  }

  private Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection, long fromCommitTime,
      long toCommitTime, OMMonitor monitor)
  {
    final Map<String, InternalCDOPackageUnit> packageUnits = new HashMap<String, InternalCDOPackageUnit>();
    IDBRowHandler unitRowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        InternalCDOPackageUnit packageUnit = createPackageUnit();
        packageUnit.setOriginalType(CDOPackageUnit.Type.values()[(Integer)values[1]]);
        packageUnit.setTimeStamp((Long)values[2]);
        packageUnits.put((String)values[0], packageUnit);
        return true;
      }
    };

    String where = null;
    if (fromCommitTime != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      where = CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelConstants.CORE_PACKAGE_URI + "' AND "
          + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelConstants.RESOURCE_PACKAGE_URI + "' AND "
          + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelConstants.TYPES_PACKAGE_URI + "' AND "
          + CDODBSchema.PACKAGE_UNITS_TIME_STAMP + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    }

    DBUtil.select(connection, unitRowHandler, where, CDODBSchema.PACKAGE_UNITS_ID,
        CDODBSchema.PACKAGE_UNITS_ORIGINAL_TYPE, CDODBSchema.PACKAGE_UNITS_TIME_STAMP);

    final Map<String, List<InternalCDOPackageInfo>> packageInfos = new HashMap<String, List<InternalCDOPackageInfo>>();
    IDBRowHandler infoRowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        InternalCDOPackageInfo packageInfo = createPackageInfo();
        packageInfo.setPackageURI((String)values[1]);
        packageInfo.setParentURI((String)values[2]);

        String unit = (String)values[0];
        List<InternalCDOPackageInfo> list = packageInfos.get(unit);
        if (list == null)
        {
          list = new ArrayList<InternalCDOPackageInfo>();
          packageInfos.put(unit, list);
        }

        list.add(packageInfo);
        return true;
      }
    };

    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      DBUtil.select(connection, infoRowHandler, CDODBSchema.PACKAGE_INFOS_UNIT, CDODBSchema.PACKAGE_INFOS_URI,
          CDODBSchema.PACKAGE_INFOS_PARENT);
    }
    finally
    {
      async.stop();
      monitor.done();
    }

    for (Entry<String, InternalCDOPackageUnit> entry : packageUnits.entrySet())
    {
      String id = entry.getKey();
      InternalCDOPackageUnit packageUnit = entry.getValue();

      List<InternalCDOPackageInfo> list = packageInfos.get(id);
      InternalCDOPackageInfo[] array = list.toArray(new InternalCDOPackageInfo[list.size()]);
      packageUnit.setPackageInfos(array);
    }

    return packageUnits.values();
  }

  private void cacheMetaIDMapping(EModelElement modelElement, CDOID metaID)
  {
    modelElementToMetaID.put(modelElement, metaID);
    metaIDToModelElement.put(metaID, modelElement);
  }
}
