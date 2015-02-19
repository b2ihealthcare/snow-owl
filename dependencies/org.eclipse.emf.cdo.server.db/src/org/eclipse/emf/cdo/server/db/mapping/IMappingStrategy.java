/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

/**
 * The mapping strategy acts as a connection between the DBStore and the database management (and OR-mapping) classes.
 * The {@link DBStore} uses methods of this interface to create and lookup mappings (or mappers, as they could also be
 * named as such) and to get properties and informations about the mappings used. The mapping classes (e.g., instances
 * of IClassMapping and IListMapping) also use this class as a central point of information and as a resource of common
 * functionalities.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IMappingStrategy
{
  /**
   * Name of the integer property that configures the maximum length for table names. A value of zero indicates the
   * value of the {@link IDBAdapter#getMaxTableNameLength() db adapter} to be used.
   */
  public static final String PROP_MAX_TABLE_NAME_LENGTH = "maxTableNameLength"; //$NON-NLS-1$

  /**
   * Name of the integer property that configures the maximum length for column names. A value of zero indicates the
   * value of the {@link IDBAdapter#getMaxFieldNameLength() db adapter} to be used.
   */
  public static final String PROP_MAX_FIELD_NAME_LENGTH = "maxFieldNameLength"; //$NON-NLS-1$

  /**
   * Name of the String property that specifies a common prefix for table names.
   */
  public static final String PROP_TABLE_NAME_PREFIX = "tableNamePrefix"; //$NON-NLS-1$

  /**
   * Name of the boolean property that configures whether the table names are made of simple class names or of qualified
   * class names.
   */
  public static final String PROP_QUALIFIED_NAMES = "qualifiedNames"; //$NON-NLS-1$

  /**
   * Name of the boolean property that configures whether table names and column names are always suffixed with the
   * internal DBID or only in cases where generated names violate the naming constraints of the underlying backend.
   */
  public static final String PROP_FORCE_NAMES_WITH_ID = "forceNamesWithID"; //$NON-NLS-1$

  /**
   * Name of the integer property that configures the size of the object type in-memory cache. Possible configuration
   * values are:
   * <ul>
   * <li>0 (zero). Don't use memory caching.
   * <li>&gt;0. Use memory caching with the cache size given.
   * </ul>
   * Default is a memory cache size of 10,000,000.
   * <p>
   * 
   * @since 4.0
   */
  public static final String PROP_OBJECT_TYPE_CACHE_SIZE = "objectTypeCacheSize"; //$NON-NLS-1$

  /**
   * @return the store, this MappingStrategy instance belongs to.
   */
  public IDBStore getStore();

  /**
   * Set the store to which this MappingStrategy instance belongs. Should only be called by the {@link DBStore}, and
   * only once to initialize the connection between {@link DBStore} and mapping strategy.
   * 
   * @param dbStore
   *          the DBStore instance to which this MappingStrategy instance belongs.
   */
  public void setStore(IDBStore dbStore);

  /**
   * Factory for value mappings of single-valued attributes.
   * 
   * @param feature
   *          the feature for which a mapping should be created. It must hold <code>feature.isMany() == false</code>.
   * @return the mapping created.
   */
  public ITypeMapping createValueMapping(EStructuralFeature feature);

  /**
   * Factory for value mappings of multi-valued-attributes.
   * 
   * @param containingClass
   *          the class containing the feature.
   * @param feature
   *          the feature for which a mapping should be created. It must hold <code>feature.isMany() == true</code>.
   */
  public IListMapping createListMapping(EClass containingClass, EStructuralFeature feature);

  /**
   * Create a suitable table name which can be used to map the given element. Should only be called by mapping classes.
   * 
   * @param element
   *          the element for which the name should be created. It must hold:
   *          <code>element instanceof EClass || element instanceof EPackage</code>.
   * @return the created table name. It is guaranteed that the table name is compatible with the chosen database.
   */
  public String getTableName(ENamedElement element);

  /**
   * Create a suitable table name which can be used to map the given element. Should only be called by mapping classes.
   * Should only be called by mapping classes.
   * 
   * @param containingClass
   *          the class containeng the feature.
   * @param feature
   *          the feature for which the table name should be created.
   * @return the created table name. It is guaranteed that the table name is compatible with the chosen database.
   */
  public String getTableName(EClass containingClass, EStructuralFeature feature);

  /**
   * Create a suitable column name which can be used to map the given element. Should only be called by mapping classes.
   * 
   * @param feature
   *          the feature for which the column name should be created.
   * @return the created column name. It is guaranteed that the name is compatible with the chosen database.
   */
  public String getFieldName(EStructuralFeature feature);

  /**
   * Create and initialize the mapping infrastructure for the given packages. Should be called from the DBStore or the
   * DBStoreAccessor.
   * 
   * @param connection
   *          the connection to use.
   * @param packageUnits
   *          the packages whose elements should be mapped.
   * @param monitor
   *          the monitor to report progress.
   */
  public void createMapping(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor);

  /**
   * Remove the mapping infrastructure for the given packages. Should be called from the DBStore or the DBStoreAccessor.
   * 
   * @param connection
   *          the connection to use.
   * @param packageUnits
   *          the packages for which the mappings should be removed
   * @since 4.0
   */
  // Bugzilla 298632
  public void removeMapping(Connection connection, InternalCDOPackageUnit[] packageUnits);

  /**
   * Look up an existing class mapping for the given class. Before this method is called, the class mapping must have
   * been initialized by calling {@link #createMapping(Connection, InternalCDOPackageUnit[], OMMonitor)} on its
   * containing package.
   * 
   * @param eClass
   *          the class to look up.
   * @return the class mapping.
   */
  public IClassMapping getClassMapping(EClass eClass);

  /**
   * Returns all class mappings of this strategy.
   * 
   * @since 4.0
   */
  public Map<EClass, IClassMapping> getClassMappings();

  /**
   * Returns all class mappings of this strategy.
   * 
   * @since 4.0
   */
  public Map<EClass, IClassMapping> getClassMappings(boolean createOnDemand);

  /**
   * Query if this mapping supports revision deltas. <br>
   * If this method returns <code>true</code>, it is guaranteed that all class mappings returned by
   * {@link #getClassMapping(EClass)} implement {@link IClassMappingDeltaSupport}.
   * 
   * @return <code>true</code> if revision deltas are supported, <code>false</code> else.
   */
  public boolean hasDeltaSupport();

  /**
   * Query if this mapping supports audits. <br>
   * If this method returns <code>true</code>, it is guaranteed that all class mappings returned by
   * {@link #getClassMapping(EClass)} implement {@link IClassMappingAuditSupport}.
   * 
   * @return <code>true</code> if audits are supported, <code>false</code> else.
   */
  public boolean hasAuditSupport();

  /**
   * Query if this mapping supports branches. <br>
   * 
   * @return <code>true</code> if branches are supported, <code>false</code> else.
   * @since 3.0
   */
  public boolean hasBranchingSupport();

  /**
   * Executes a resource query.
   * 
   * @param accessor
   *          the accessor to use.
   * @param context
   *          the context from which the query parameters are read and to which the result is written.
   */
  public void queryResources(IDBStoreAccessor accessor, QueryResourcesContext context);

  /**
   * Executes a cross reference query.
   * 
   * @param accessor
   *          the accessor to use.
   * @param context
   *          the context from which the query parameters are read and to which the result is written.
   * @since 3.0
   */
  public void queryXRefs(IDBStoreAccessor accessor, QueryXRefsContext context);

  /**
   * Read the type (i.e. class) of the object referred to by a given ID.
   * 
   * @param accessor
   *          the accessor to use to look up the type.
   * @param id
   *          the ID of the object for which the type is to be determined.
   * @return the type of the object.
   */
  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id);

  /**
   * Get an iterator over all instances of objects in the store.
   * 
   * @param accessor
   *          the accessor to use.
   * @return the iterator.
   */
  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor accessor);

  /**
   * Return the maximum object id used in the store. This is used by the DBStore if a previous crash is discovered
   * during the startup process. Should only be called by the DBStore and only during startup.
   * 
   * @param dbAdapter
   *          the dbAdapter to use to access the database
   * @param connection
   *          the connection to use to access the database
   * @since 4.0
   */
  public void repairAfterCrash(IDBAdapter dbAdapter, Connection connection);

  /**
   * Returns the configuration properties of this mapping strategy.
   * 
   * @since 4.0
   */
  public Map<String, String> getProperties();

  /**
   * Set configuration properties for this mapping strategy. Should only be called by the factory creating the mapping
   * strategy instance.
   * 
   * @param properties
   *          the configuration properties to set.
   */
  public void setProperties(Map<String, String> properties);

  /**
   * Passes all revisions of the store to the {@link CDORevisionHandler handler} if <b>all</b> of the following
   * conditions are met:
   * <ul>
   * <li>The <code>eClass</code> parameter is <code>null</code> or equal to <code>revision.getEClass()</code>.
   * <li>The <code>branch</code> parameter is <code>null</code> or equal to <code>revision.getBranch()</code>.
   * <li>The <code>timeStamp</code> parameter is {@link CDOBranchPoint#UNSPECIFIED_DATE} or equal to
   * <code>revision.getTimeStamp()</code>.
   * </ul>
   * 
   * @since 4.0
   */
  public void handleRevisions(IDBStoreAccessor accessor, EClass eClass, CDOBranch branch, long timeStamp,
      boolean exactTime, CDORevisionHandler handler);

  /**
   * Returns a set of CDOIDs that have at least one revision in any of the passed branches and time ranges.
   * DetachedCDORevisions must also be considered!
   * 
   * @see IStoreAccessor#readChangeSet(OMMonitor, CDOChangeSetSegment...)
   * @since 4.0
   * @since Snow Owl 2.6
   */
  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, OMMonitor monitor, final String[] nsURIs,
      CDOChangeSetSegment[] segments);

  /**
   * @since 3.0
   */
  public void rawExport(IDBStoreAccessor accessor, CDODataOutput out, int lastReplicatedBranchID, int lastBranchID,
      long lastReplicatedCommitTime, long lastCommitTime) throws IOException;

  /**
   * @since 4.0
   */
  public void rawImport(IDBStoreAccessor accessor, CDODataInput in, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException;

  /**
   * @since 4.0
   */
  public String getListJoin(String attrTable, String listTable);
}
