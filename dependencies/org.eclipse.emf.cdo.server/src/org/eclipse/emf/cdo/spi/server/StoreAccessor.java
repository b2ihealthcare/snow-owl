/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.LimitedInputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class StoreAccessor extends StoreAccessorBase
{
  protected StoreAccessor(Store store, ISession session)
  {
    super(store, session);
  }

  protected StoreAccessor(Store store, ITransaction transaction)
  {
    super(store, transaction);
  }

  /**
   * @since 4.0
   */
  @Override
  protected void doWrite(InternalCommitContext context, OMMonitor monitor)
  {
    CDOBranch branch = context.getBranchPoint().getBranch();
    long timeStamp = context.getBranchPoint().getTimeStamp();
    long previousTimeStamp = context.getPreviousTimeStamp();
    String userID = context.getUserID();
    String commitComment = context.getCommitComment();

    boolean deltas = getStore().getSupportedChangeFormats().contains(IStore.ChangeFormat.DELTA);

    InternalCDOPackageUnit[] newPackageUnits = context.getNewPackageUnits();
    InternalCDORevision[] newObjects = context.getNewObjects();
    CDOID[] detachedObjects = context.getDetachedObjects();
    int dirtyCount = deltas ? context.getDirtyObjectDeltas().length : context.getDirtyObjects().length;

    try
    {
      monitor.begin(1 + newPackageUnits.length + 2 + newObjects.length + detachedObjects.length + dirtyCount);
      writeCommitInfo(branch, timeStamp, previousTimeStamp, userID, commitComment, monitor.fork());

      if (newPackageUnits.length != 0)
      {
        writePackageUnits(newPackageUnits, monitor.fork(newPackageUnits.length));
      }

      if (getStore().getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE)
      {
        addIDMappings(context, monitor.fork());
      }

      applyIDMappings(context, monitor);

      if (detachedObjects.length != 0)
      {
        detachObjects(detachedObjects, branch, timeStamp, monitor.fork(detachedObjects.length));
      }

      if (newObjects.length != 0)
      {
        writeRevisions(newObjects, branch, monitor.fork(newObjects.length));
      }

      if (dirtyCount != 0)
      {
        if (deltas)
        {
          writeRevisionDeltas(context.getDirtyObjectDeltas(), branch, timeStamp, monitor.fork(dirtyCount));
        }
        else
        {
          writeRevisions(context.getDirtyObjects(), branch, monitor.fork(dirtyCount));
        }
      }

      ExtendedDataInputStream in = context.getLobs();
      if (in != null)
      {
        try
        {
          int count = in.readInt();
          for (int i = 0; i < count; i++)
          {
            byte[] id = in.readByteArray();
            long size = in.readLong();
            if (size > 0)
            {
              writeBlob(id, size, new LimitedInputStream(in, size));
            }
            else
            {
              writeClob(id, -size, new InputStreamReader(new LimitedInputStream(in, -size)));
            }
          }
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @since 3.0
   */
  protected void applyIDMappings(InternalCommitContext context, OMMonitor monitor)
  {
    context.applyIDMappings(monitor.fork());
  }

  /**
   * @since 4.0
   */
  protected abstract void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor);

  /**
   * @since 3.0
   */
  protected abstract void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor);

  /**
   * @since 3.0
   */
  protected abstract void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch,
      long created, OMMonitor monitor);

  /**
   * @since 3.0
   */
  protected abstract void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor);

  /**
   * @since 4.0
   */
  protected abstract void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException;

  /**
   * @since 4.0
   */
  protected abstract void writeClob(byte[] id, long size, Reader reader) throws IOException;
}
