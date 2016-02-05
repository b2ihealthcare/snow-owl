/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.common.protocol;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockAreaImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockChangeInfoImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockOwnerImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockStateImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDAndBranchImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public abstract class CDODataInputImpl extends ExtendedDataInput.Delegating implements CDODataInput
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDODataInputImpl.class);

  public CDODataInputImpl(ExtendedDataInput delegate)
  {
    super(delegate);
  }

  public CDOPackageUnit readCDOPackageUnit(ResourceSet resourceSet) throws IOException
  {
    InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
    packageUnit.read(this, resourceSet);
    return packageUnit;
  }

  public CDOPackageUnit[] readCDOPackageUnits(ResourceSet resourceSet) throws IOException
  {
    int size = readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} package units", size); //$NON-NLS-1$
    }

    CDOPackageUnit[] packageUnits = new CDOPackageUnit[size];
    for (int i = 0; i < size; i++)
    {
      packageUnits[i] = readCDOPackageUnit(resourceSet);
    }

    return packageUnits;
  }

  public CDOPackageUnit.Type readCDOPackageUnitType() throws IOException
  {
    return CDOPackageUnit.Type.values()[readByte()];
  }

  public CDOPackageInfo readCDOPackageInfo() throws IOException
  {
    InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
    packageInfo.read(this);
    return packageInfo;
  }

  public String readCDOPackageURI() throws IOException
  {
    return getPackageURICompressor().read(this);
  }

  public CDOClassifierRef readCDOClassifierRef() throws IOException
  {
    return new CDOClassifierRef(this);
  }

  public EClassifier readCDOClassifierRefAndResolve() throws IOException
  {
    CDOClassifierRef classifierRef = readCDOClassifierRef();
    EClassifier classifier = classifierRef.resolve(getPackageRegistry());
    if (classifier == null)
    {
      throw new IOException("Unable to resolve " + classifierRef);
    }

    return classifier;
  }

  public CDOType readCDOType() throws IOException
  {
    byte typeID = readByte();
    return CDOModelUtil.getType(typeID);
  }

  public CDOBranch readCDOBranch() throws IOException
  {
    int branchID = readInt();
    return getBranchManager().getBranch(branchID);
  }

  public CDOBranchPoint readCDOBranchPoint() throws IOException
  {
    CDOBranch branch = readCDOBranch();
    long timeStamp = readLong();
    return branch.getPoint(timeStamp);
  }

  public CDOBranchVersion readCDOBranchVersion() throws IOException
  {
    CDOBranch branch = readCDOBranch();
    int version = readInt();
    return branch.getVersion(version);
  }

  public CDOChangeSetData readCDOChangeSetData() throws IOException
  {
    int size1 = readInt();
    List<CDOIDAndVersion> newObjects = new ArrayList<CDOIDAndVersion>(size1);
    for (int i = 0; i < size1; i++)
    {
      boolean revision = readBoolean();
      CDOIDAndVersion data = revision ? readCDORevision() : readCDOIDAndVersion();
      newObjects.add(data);
    }

    int size2 = readInt();
    List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>(size2);
    for (int i = 0; i < size2; i++)
    {
      boolean delta = readBoolean();
      CDORevisionKey data = delta ? readCDORevisionDelta() : readCDORevisionKey();
      changedObjects.add(data);
    }

    int size3 = readInt();
    List<CDOIDAndVersion> detachedObjects = new ArrayList<CDOIDAndVersion>(size3);
    for (int i = 0; i < size3; i++)
    {
      CDOIDAndVersion data = readCDOIDAndVersion();
      detachedObjects.add(data);
    }

    return new CDOChangeSetDataImpl(newObjects, changedObjects, detachedObjects);
  }

  public CDOCommitData readCDOCommitData() throws IOException
  {
    InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)getPackageRegistry();
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.setPackageRegistry(packageRegistry);

    int size = readInt();
    List<CDOPackageUnit> newPackageUnits = new ArrayList<CDOPackageUnit>(size);
    for (int i = 0; i < size; i++)
    {
      CDOPackageUnit data = readCDOPackageUnit(resourceSet);
      newPackageUnits.add(data);
      packageRegistry.putPackageUnit((InternalCDOPackageUnit)data);
    }

    CDOChangeSetData data = readCDOChangeSetData();
    return new CDOCommitDataImpl(newPackageUnits, data.getNewObjects(), data.getChangedObjects(),
        data.getDetachedObjects());
  }

  public CDOCommitInfo readCDOCommitInfo() throws IOException
  {
    long timeStamp = readLong();
    long previousTimeStamp = readLong();

    if (readBoolean())
    {
      CDOBranch branch = readCDOBranch();
      String userID = readString();
      String comment = readString();
      CDOCommitData commitData = readCDOCommitData();

      InternalCDOCommitInfoManager commitInfoManager = (InternalCDOCommitInfoManager)getCommitInfoManager();
      return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, commitData);
    }

    return new FailureCommitInfo(timeStamp, previousTimeStamp);
  }

  public CDOLockChangeInfo readCDOLockChangeInfo() throws IOException
  {
    boolean isInvalidateAll = readBoolean();
    if (isInvalidateAll)
    {
      return CDOLockUtil.createLockChangeInfo();
    }

    CDOBranchPoint branchPoint = readCDOBranchPoint();
    CDOLockOwner lockOwner = readCDOLockOwner();
    Operation operation = readEnum(Operation.class);
    LockType lockType = readCDOLockType();

    int n = readInt();
    CDOLockState[] lockStates = new CDOLockState[n];
    for (int i = 0; i < n; i++)
    {
      lockStates[i] = readCDOLockState();
    }

    return new CDOLockChangeInfoImpl(branchPoint, lockOwner, lockStates, operation, lockType);
  }

  public LockArea readCDOLockArea() throws IOException
  {
    String durableLockingID = readString();
    CDOBranch branch = readCDOBranch();
    long timestamp = readLong();
    String userID = readString();
    boolean readOnly = readBoolean();

    int nLockStates = readInt();
    Map<CDOID, LockGrade> locks = new HashMap<CDOID, LockGrade>();
    for (int i = 0; i < nLockStates; i++)
    {
      CDOID key = readCDOID();
      LockGrade value = readEnum(LockGrade.class);
      locks.put(key, value);
    }

    return new CDOLockAreaImpl(durableLockingID, userID, branch.getPoint(timestamp), readOnly, locks);
  }

  public CDOLockOwner readCDOLockOwner() throws IOException
  {
    int session = readInt();
    int view = readInt();
    String lockAreaID = readString();
    boolean isDurableView = readBoolean();
    return new CDOLockOwnerImpl(session, view, lockAreaID, isDurableView);
  }

  public CDOLockState readCDOLockState() throws IOException
  {
    Object target;
    boolean sendingBranchWithID = readBoolean();
    if (!sendingBranchWithID)
    {
      target = readCDOID();
    }
    else
    {
      target = readCDOIDAndBranch();
    }

    InternalCDOLockState lockState = new CDOLockStateImpl(target);

    int nReadLockOwners = readInt();
    for (int i = 0; i < nReadLockOwners; i++)
    {
      CDOLockOwner lockOwner = readCDOLockOwner();
      lockState.addReadLockOwner(lockOwner);
    }

    boolean hasWriteLock = readBoolean();
    if (hasWriteLock)
    {
      CDOLockOwner lockOwner = readCDOLockOwner();
      lockState.setWriteLockOwner(lockOwner);
    }

    boolean hasWriteOption = readBoolean();
    if (hasWriteOption)
    {
      CDOLockOwner lockOwner = readCDOLockOwner();
      lockState.setWriteOptionOwner(lockOwner);
    }

    return lockState;
  }

  public LockType readCDOLockType() throws IOException
  {
    return readEnum(LockType.class);
  }

  public CDOID readCDOID() throws IOException
  {
    return CDOIDUtil.read(this);
  }

  public CDOIDReference readCDOIDReference() throws IOException
  {
    return new CDOIDReference(this);
  }

  public CDOIDAndVersion readCDOIDAndVersion() throws IOException
  {
    CDOID id = readCDOID();
    int version = readInt();
    return new CDOIDAndVersionImpl(id, version);
  }

  public CDOIDAndBranch readCDOIDAndBranch() throws IOException
  {
    CDOID id = readCDOID();
    CDOBranch branch = readCDOBranch();
    return new CDOIDAndBranchImpl(id, branch);
  }

  public CDORevisionKey readCDORevisionKey() throws IOException
  {
    CDOID id = readCDOID();
    CDOBranch branch = readCDOBranch();
    int version = readInt();
    return CDORevisionUtil.createRevisionKey(id, branch, version);
  }

  public CDORevision readCDORevision() throws IOException
  {
    return readCDORevision(true);
  }

  public CDORevision readCDORevision(boolean freeze) throws IOException
  {
    boolean notNull = readBoolean();
    if (notNull)
    {
      InternalCDORevision revision = (InternalCDORevision)getRevisionFactory().createRevision(null);
      revision.read(this);

      if (freeze)
      {
        revision.freeze();
      }

      return revision;
    }

    return null;
  }

  public CDORevisable readCDORevisable() throws IOException
  {
    CDOBranch branch = readCDOBranch();
    int version = readInt();
    long timeStamp = readLong();
    long revised = readLong();
    return CDORevisionUtil.createRevisable(branch, version, timeStamp, revised);
  }

  public CDOList readCDOList(EClass owner, EStructuralFeature feature) throws IOException
  {
    int referenceChunk;
    int size = readInt();
    if (size < 0)
    {
      size = -size;
      referenceChunk = readInt();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read feature {0}: size={1}, referenceChunk={2}", feature.getName(), size, referenceChunk); //$NON-NLS-1$
      }
    }
    else
    {
      referenceChunk = size;
      if (TRACER.isEnabled())
      {
        TRACER.format("Read feature {0}: size={1}", feature.getName(), size); //$NON-NLS-1$
      }
    }

    Object value = null;
    CDOType type = null;
    boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
    if (!isFeatureMap)
    {
      type = CDOModelUtil.getType(feature.getEType());
    }

    InternalCDOList list = (InternalCDOList)getListFactory().createList(size, size, referenceChunk, feature.isOrdered());
    for (int j = 0; j < referenceChunk; j++)
    {
      if (isFeatureMap)
      {
        int featureID = readInt();
        EStructuralFeature innerFeature = owner.getEStructuralFeature(featureID);
        type = CDOModelUtil.getType(innerFeature.getEType());
        value = type.readValue(this);
        value = CDORevisionUtil.createFeatureMapEntry(innerFeature, value);
      }
      else
      {
        value = type.readValue(this);
      }

      list.set(j, value);
      if (TRACER.isEnabled())
      {
        TRACER.trace("    " + value); //$NON-NLS-1$
      }
    }

    return list;
  }

  public Object readCDOFeatureValue(EStructuralFeature feature) throws IOException
  {
    CDOType type = CDOModelUtil.getType(feature);
    Object value = type.readValue(this);
    if (value instanceof CDOLob<?>)
    {
      CDOLob<?> lob = (CDOLob<?>)value;
      CDOLobUtil.setStore(getLobStore(), lob);
    }

    return value;
  }

  public CDORevisionDelta readCDORevisionDelta() throws IOException
  {
    return new CDORevisionDeltaImpl(this);
  }

  public CDOFeatureDelta readCDOFeatureDelta(EClass owner) throws IOException
  {
    int typeOrdinal = readInt();
    CDOFeatureDelta.Type type = CDOFeatureDelta.Type.values()[typeOrdinal];
    switch (type)
    {
    case ADD:
      return new CDOAddFeatureDeltaImpl(this, owner);

    case SET:
      return new CDOSetFeatureDeltaImpl(this, owner);

    case LIST:
      return new CDOListFeatureDeltaImpl(this, owner);

    case MOVE:
      return new CDOMoveFeatureDeltaImpl(this, owner);

    case CLEAR:
      return new CDOClearFeatureDeltaImpl(this, owner);

    case REMOVE:
      return new CDORemoveFeatureDeltaImpl(this, owner);

    case CONTAINER:
      return new CDOContainerFeatureDeltaImpl(this, owner);

    case UNSET:
      return new CDOUnsetFeatureDeltaImpl(this, owner);

    default:
      throw new IOException(MessageFormat.format(Messages.getString("CDODataInputImpl.5"), typeOrdinal)); //$NON-NLS-1$
    }
  }

  public Object readCDORevisionOrPrimitive() throws IOException
  {
    CDOType type = readCDOType();
    return type.readValue(this);
  }

  public Object readCDORevisionOrPrimitiveOrClassifier() throws IOException
  {
    boolean isClassifier = readBoolean();
    if (isClassifier)
    {
      return readCDOClassifierRefAndResolve();
    }

    return readCDORevisionOrPrimitive();
  }

  protected StringIO getPackageURICompressor()
  {
    return StringIO.DIRECT;
  }

  protected abstract CDOPackageRegistry getPackageRegistry();

  protected abstract CDOBranchManager getBranchManager();

  protected abstract CDOCommitInfoManager getCommitInfoManager();

  protected abstract CDORevisionFactory getRevisionFactory();

  protected abstract CDOListFactory getListFactory();

  protected abstract CDOLobStore getLobStore();
}
