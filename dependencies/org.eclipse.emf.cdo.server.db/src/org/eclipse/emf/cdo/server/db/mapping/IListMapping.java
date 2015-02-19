/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.ddl.IDBTable;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.List;

/**
 * Interface for mapping features with <code>isMany() == true</code>.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IListMapping
{
  /**
   * Return the mapped feature.
   * 
   * @return the mapped feature.
   */
  public EStructuralFeature getFeature();

  /**
   * Returns all DB tables which are used by this feature.
   * 
   * @return a collection of all tables of this feature.
   */
  public Collection<IDBTable> getDBTables();

  /**
   * Write a complete list of values to the database.
   * 
   * @param accessor
   *          the accessor to use.
   * @param revision
   *          the revision containing the list to be written.
   */
  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision);

  /**
   * Read the list size and the complete list or the first part of it.
   * 
   * @param accessor
   *          the accessor to use.
   * @param revision
   *          the revision into which the list values should be read.
   * @param listChunk
   *          indicating the lazy loading behavior: {@link CDORevision#UNCHUNKED} means that the whole list should be
   *          read. Else, if <code>listChunk >= 0</code>, the list is filled with {@link InternalCDOList#UNINITIALIZED}
   *          and only the first <code>listChunk</code> values are read.
   */
  public void readValues(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk);

  /**
   * Used to load-on-demand chunks of a list.
   * 
   * @param dbStoreChunkReader
   *          the chunkReader to use
   * @param chunks
   *          the chunks to read
   * @param where
   *          the where-clause to use in order to read the chunks.
   */
  public void readChunks(IDBStoreChunkReader dbStoreChunkReader, List<Chunk> chunks, String where);

  /**
   * Hook with which a list mapping is notified that a containing object has been revised. Can be implemented in order
   * to clean up lists of revised objects.
   * 
   * @param accessor
   *          the accessor to use.
   * @param id
   *          the ID of the object which has been revised.
   * @param revised
   *          the timestamp at which the object was revised.
   * @since 3.0
   */
  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised);

  /**
   * Retrieve cross-references from DB.
   * 
   * @see IClassMapping#queryXRefs(IDBStoreAccessor, IStoreAccessor.QueryXRefsContext, String)
   * @see IStoreAccessor#queryXRefs(IStoreAccessor.QueryXRefsContext)
   * @since 4.0
   */
  public boolean queryXRefs(IDBStoreAccessor accessor, String mainTableName, String mainTableWhere,
      QueryXRefsContext context, String idString);
}
