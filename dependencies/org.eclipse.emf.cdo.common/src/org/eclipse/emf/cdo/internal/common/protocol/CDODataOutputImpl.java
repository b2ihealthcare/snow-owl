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
package org.eclipse.emf.cdo.internal.common.protocol;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public abstract class CDODataOutputImpl extends ExtendedDataOutput.Delegating implements CDODataOutput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataOutputImpl.class);

  public CDODataOutputImpl(ExtendedDataOutput delegate)
  {
    super(delegate);
  }

  public void writeCDOPackageUnit(CDOPackageUnit packageUnit, boolean withPackages) throws IOException
  {
    ((InternalCDOPackageUnit)packageUnit).write(this, withPackages);
  }

  public void writeCDOPackageUnits(CDOPackageUnit... packageUnits) throws IOException
  {
    int size = packageUnits.length;
    writeInt(size);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} package units", size); //$NON-NLS-1$
    }

    for (CDOPackageUnit packageUnit : packageUnits)
    {
      writeCDOPackageUnit(packageUnit, false);
    }
  }

  public void writeCDOPackageUnitType(CDOPackageUnit.Type type) throws IOException
  {
    writeByte(type.ordinal());
  }

  public void writeCDOPackageInfo(CDOPackageInfo packageInfo) throws IOException
  {
    ((InternalCDOPackageInfo)packageInfo).write(this);
  }

  public void writeCDOClassifierRef(CDOClassifierRef eClassifierRef) throws IOException
  {
    eClassifierRef.write(this);
  }

  public void writeCDOClassifierRef(EClassifier eClassifier) throws IOException
  {
    writeCDOClassifierRef(new CDOClassifierRef(eClassifier));
  }

  public void writeCDOPackageURI(String uri) throws IOException
  {
    getPackageURICompressor().write(this, uri);
  }

  public void writeCDOType(CDOType cdoType) throws IOException
  {
    ((CDOTypeImpl)cdoType).write(this);
  }

  public void writeCDOBranch(CDOBranch branch) throws IOException
  {
    writeInt(branch.getID());
  }

  public void writeCDOBranchPoint(CDOBranchPoint branchPoint) throws IOException
  {
    writeCDOBranch(branchPoint.getBranch());
    writeLong(branchPoint.getTimeStamp());
  }

  public void writeCDOBranchVersion(CDOBranchVersion branchVersion) throws IOException
  {
    writeCDOBranch(branchVersion.getBranch());
    writeInt(branchVersion.getVersion());
  }

  public void writeCDOChangeSetData(CDOChangeSetData changeSetData) throws IOException
  {
    Collection<CDOIDAndVersion> newObjects = changeSetData.getNewObjects();
    writeInt(newObjects.size());
    for (CDOIDAndVersion data : newObjects)
    {
      if (data instanceof CDORevision)
      {
        writeBoolean(true);
        writeCDORevision((CDORevision)data, CDORevision.UNCHUNKED);
      }
      else
      {
        writeBoolean(false);
        writeCDOIDAndVersion(data);
      }
    }

    Collection<CDORevisionKey> changedObjects = changeSetData.getChangedObjects();
    writeInt(changedObjects.size());
    for (CDORevisionKey data : changedObjects)
    {
      if (data instanceof CDORevisionDelta)
      {
        writeBoolean(true);
        writeCDORevisionDelta((CDORevisionDelta)data);
      }
      else
      {
        writeBoolean(false);
        writeCDORevisionKey(data);
      }
    }

    Collection<CDOIDAndVersion> detachedObjects = changeSetData.getDetachedObjects();
    writeInt(detachedObjects.size());
    for (CDOIDAndVersion data : detachedObjects)
    {
      writeCDOIDAndVersion(data);
    }
  }

  public void writeCDOCommitData(CDOCommitData commitData) throws IOException
  {
    Collection<CDOPackageUnit> newPackageUnits = commitData.getNewPackageUnits();
    writeInt(newPackageUnits.size());
    for (CDOPackageUnit data : newPackageUnits)
    {
      writeCDOPackageUnit(data, false);
    }

    writeCDOChangeSetData(commitData);
  }

  public void writeCDOCommitInfo(CDOCommitInfo commitInfo) throws IOException
  {
    writeLong(commitInfo.getTimeStamp());
    writeLong(commitInfo.getPreviousTimeStamp());

    CDOBranch branch = commitInfo.getBranch();
    if (branch != null)
    {
      writeBoolean(true);
      writeCDOBranch(branch);
      writeString(commitInfo.getUserID());
      writeString(commitInfo.getComment());
      writeCDOCommitData(commitInfo);
    }
    else
    {
      // FailureCommitInfo
      writeBoolean(false);
    }
  }

  public void writeCDOLockChangeInfo(CDOLockChangeInfo lockChangeInfo) throws IOException
  {
    if (lockChangeInfo.isInvalidateAll())
    {
      writeBoolean(true);
    }
    else
    {
      writeBoolean(false);
      writeCDOBranchPoint(lockChangeInfo);
      writeCDOLockOwner(lockChangeInfo.getLockOwner());
      writeEnum(lockChangeInfo.getOperation());
      writeCDOLockType(lockChangeInfo.getLockType());

      CDOLockState[] lockStates = lockChangeInfo.getLockStates();
      writeInt(lockStates.length);
      for (CDOLockState lockState : lockStates)
      {
        writeCDOLockState(lockState);
      }
    }
  }

  public void writeCDOLockArea(LockArea lockArea) throws IOException
  {
    writeString(lockArea.getDurableLockingID());
    writeCDOBranch(lockArea.getBranch());
    writeLong(lockArea.getTimeStamp());
    writeString(lockArea.getUserID());
    writeBoolean(lockArea.isReadOnly());

    writeInt(lockArea.getLocks().size());
    for (Map.Entry<CDOID, LockGrade> entry : lockArea.getLocks().entrySet())
    {
      writeCDOID(entry.getKey());
      writeEnum(entry.getValue());
    }
  }

  public void writeCDOLockOwner(CDOLockOwner lockOwner) throws IOException
  {
    writeInt(lockOwner.getSessionID());
    writeInt(lockOwner.getViewID());
    writeString(lockOwner.getDurableLockingID());
    writeBoolean(lockOwner.isDurableView());
  }

  public void writeCDOLockState(CDOLockState lockState) throws IOException
  {
    Object o = lockState.getLockedObject();
    if (o instanceof CDOID)
    {
      writeBoolean(false);
      writeCDOID((CDOID)o);
    }
    else if (o instanceof CDOIDAndBranch)
    {
      writeBoolean(true);
      writeCDOIDAndBranch((CDOIDAndBranch)o);
    }
    else
    {
      throw new AssertionError("Unexpected type: " + o.getClass().getSimpleName());
    }

    Set<CDOLockOwner> readLockOwners = lockState.getReadLockOwners();
    writeInt(readLockOwners.size());
    for (CDOLockOwner readLockOwner : readLockOwners)
    {
      writeCDOLockOwner(readLockOwner);
    }

    CDOLockOwner writeLockOwner = lockState.getWriteLockOwner();
    if (writeLockOwner != null)
    {
      writeBoolean(true);
      writeCDOLockOwner(writeLockOwner);
    }
    else
    {
      writeBoolean(false);
    }

    CDOLockOwner writeOptionOwner = lockState.getWriteOptionOwner();
    if (writeOptionOwner != null)
    {
      writeBoolean(true);
      writeCDOLockOwner(writeOptionOwner);
    }
    else
    {
      writeBoolean(false);
    }
  }

  public void writeCDOLockType(LockType lockType) throws IOException
  {
    writeEnum(lockType);
  }

  public void writeCDOID(CDOID id) throws IOException
  {
    CDOIDUtil.write(this, id);
  }

  public void writeCDOIDReference(CDOIDReference idReference) throws IOException
  {
    idReference.write(this);
  }

  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException
  {
    writeCDOID(idAndVersion.getID());
    writeInt(idAndVersion.getVersion());
  }

  public void writeCDOIDAndBranch(CDOIDAndBranch idAndBranch) throws IOException
  {
    writeCDOID(idAndBranch.getID());
    writeCDOBranch(idAndBranch.getBranch());
  }

  public void writeCDORevisionKey(CDORevisionKey revisionKey) throws IOException
  {
    writeCDOID(revisionKey.getID());
    writeCDOBranch(revisionKey.getBranch());
    writeInt(revisionKey.getVersion());
  }

  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException
  {
    writeCDORevision(revision, referenceChunk, null);
  }

  public void writeCDORevision(CDORevision revision, int referenceChunk, CDOBranchPoint securityContext)
      throws IOException
  {
    if (revision != null)
    {
      writeBoolean(true);
      ((InternalCDORevision)revision).write(this, referenceChunk, securityContext);
    }
    else
    {
      writeBoolean(false);
    }
  }

  public void writeCDORevisable(CDORevisable revisable) throws IOException
  {
    writeCDOBranch(revisable.getBranch());
    writeInt(revisable.getVersion());
    writeLong(revisable.getTimeStamp());
    writeLong(revisable.getRevised());
  }

  public void writeCDOList(EClass owner, EStructuralFeature feature, CDOList list, int referenceChunk)
      throws IOException
  {
    // TODO Simon: Could most of this stuff be moved into the list?
    // (only if protected methods of this class don't need to become public)
    int size = list == null ? 0 : list.size();
    if (size > 0)
    {
      // Need to adjust the referenceChunk in case where we do not have enough value in the list.
      // Even if the referenceChunk is specified, a provider of data could have override that value.
      int sizeToLook = referenceChunk == CDORevision.UNCHUNKED ? size : Math.min(referenceChunk, size);
      for (int i = 0; i < sizeToLook; i++)
      {
        Object element = list.get(i, false);
        if (element == CDORevisionUtil.UNINITIALIZED)
        {
          referenceChunk = i;
          break;
        }
      }
    }

    if (referenceChunk != CDORevision.UNCHUNKED && referenceChunk < size)
    {
      // This happens only on server-side
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}, referenceChunk={2}", feature.getName(), size, referenceChunk); //$NON-NLS-1$
      }

      writeInt(-size);
      writeInt(referenceChunk);
      size = referenceChunk;
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing feature {0}: size={1}", feature.getName(), size); //$NON-NLS-1$
      }

      writeInt(size);
    }

    CDOIDProvider idProvider = getIDProvider();
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    for (int j = 0; j < size; j++)
    {
      Object value = list.get(j, false);
      EStructuralFeature innerFeature = feature; // Prepare for possible feature map
      if (isFeatureMap)
      {
        Entry entry = (FeatureMap.Entry)value;
        innerFeature = entry.getEStructuralFeature();
        value = entry.getValue();

        int featureID = owner.getFeatureID(innerFeature);
        writeInt(featureID);
      }

      if (value != null && innerFeature instanceof EReference)
      {
        value = idProvider.provideCDOID(value);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("    " + value); //$NON-NLS-1$
      }

      writeCDOFeatureValue(innerFeature, value);
    }
  }

  public void writeCDOFeatureValue(EStructuralFeature feature, Object value) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);
    type.writeValue(this, value);
  }

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException
  {
    ((CDORevisionDeltaImpl)revisionDelta).write(this);
  }

  public void writeCDOFeatureDelta(EClass owner, CDOFeatureDelta featureDelta) throws IOException
  {
    ((CDOFeatureDeltaImpl)featureDelta).write(this, owner);
  }

  public void writeCDORevisionOrPrimitive(Object value) throws IOException
  {
    if (value == null)
    {
      value = CDOID.NULL;
    }
    else if (value instanceof EObject)
    {
      value = getIDProvider().provideCDOID(value);
    }
    else if (value instanceof CDORevision)
    {
      value = ((CDORevision)value).getID();
    }

    CDOType type = null;
    if (value instanceof CDOID)
    {
      type = CDOType.OBJECT;
    }
    else
    {
      type = CDOModelUtil.getPrimitiveType(value.getClass());
      if (type == null)
      {
        throw new IllegalArgumentException(MessageFormat.format(
            Messages.getString("CDODataOutputImpl.6"), value.getClass())); //$NON-NLS-1$
      }
    }

    writeCDOType(type);
    type.writeValue(this, value);
  }

  public void writeCDORevisionOrPrimitiveOrClassifier(Object value) throws IOException
  {
    if (value instanceof EClassifier)
    {
      writeBoolean(true);
      writeCDOClassifierRef((EClass)value);
    }
    else
    {
      writeBoolean(false);
      writeCDORevisionOrPrimitive(value);
    }
  }

  public CDOPackageRegistry getPackageRegistry()
  {
    return null;
  }

  public CDOIDProvider getIDProvider()
  {
    return null;
  }

  public CDOPermissionProvider getPermissionProvider()
  {
    return CDORevision.PERMISSION_PROVIDER;
  }

  protected StringIO getPackageURICompressor()
  {
    return StringIO.DIRECT;
  }
}
