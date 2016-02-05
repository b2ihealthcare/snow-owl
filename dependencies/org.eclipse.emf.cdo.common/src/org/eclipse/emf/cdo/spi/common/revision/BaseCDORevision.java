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
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.revision;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.cdo.common.security.NoPermissionException;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PerfTracer;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class BaseCDORevision extends AbstractCDORevision
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, BaseCDORevision.class);

  private static final PerfTracer READING = new PerfTracer(OM.PERF_REVISION_READING, BaseCDORevision.class);

  private static final PerfTracer WRITING = new PerfTracer(OM.PERF_REVISION_WRITING, BaseCDORevision.class);

  private static final byte UNSET = 0;

  private static final byte SET_NULL = 1;

  private static final byte SET_NOT_NULL = 2;

  private static final byte FROZEN_FLAG = 0x04;

  private static final byte UNCHUNKED_FLAG = 0x08;

  private static final byte PERMISSION_MASK = 0x03;

  private static final byte TRANSFER_MASK = PERMISSION_MASK | UNCHUNKED_FLAG;

  private CDOID id;

  private CDOBranchPoint branchPoint;

  private int version;

  private long revised;

  private CDOID resourceID;

  /**
   * On a client, between a local modification and the commit the value of this <i>ID</i> can be an EObject.
   */
  private Object containerID;

  private int containingFeatureID;

  private transient byte flags;

  /**
   * @since 3.0
   */
  public BaseCDORevision(EClass eClass)
  {
    super(eClass);
    if (eClass != null)
    {
      version = UNSPECIFIED_VERSION;
      revised = UNSPECIFIED_DATE;
      resourceID = CDOID.NULL;
      containerID = CDOID.NULL;
      containingFeatureID = 0;
      initValues(getAllPersistentFeatures());
    }

    flags = CDOPermission.WRITE.getBits();
  }

  protected BaseCDORevision(BaseCDORevision source)
  {
    super(source.getEClass());
    id = source.id;
    branchPoint = source.branchPoint;
    version = source.version;
    revised = source.revised;
    resourceID = source.resourceID;
    containerID = source.containerID;
    containingFeatureID = source.containingFeatureID;
    flags = (byte)(source.flags & TRANSFER_MASK);
  }

  /**
   * @since 3.0
   */
  public void read(CDODataInput in) throws IOException
  {
    if (READING.isEnabled())
    {
      READING.start(this);
    }

    readSystemValues(in);

    byte flagBits = (byte)(in.readByte() & TRANSFER_MASK);
    if ((flagBits & PERMISSION_MASK) != CDOPermission.NONE.ordinal())
    {
      readValues(in);
    }

    flags = flagBits;

    if (READING.isEnabled())
    {
      READING.stop(this);
    }
  }

  /**
   * @since 4.0
   */
  protected void readSystemValues(CDODataInput in) throws IOException
  {
    EClassifier classifier = in.readCDOClassifierRefAndResolve();
    CDOClassInfo classInfo = CDOModelUtil.getClassInfo((EClass)classifier);
    setClassInfo(classInfo);

    id = in.readCDOID();
    branchPoint = in.readCDOBranchPoint();
    version = in.readInt();
    if (!id.isTemporary())
    {
      revised = in.readLong();
    }

    resourceID = in.readCDOID();
    containerID = in.readCDOID();
    containingFeatureID = in.readInt();

    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Reading revision: ID={0}, className={1}, version={2}, branchPoint={3}, revised={4}, resource={5}, container={6}, featureID={7}", //$NON-NLS-1$
              id, getEClass().getName(), version, branchPoint, revised, resourceID, containerID, containingFeatureID);
    }
  }

  /**
   * @since 4.0
   */
  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    write(out, referenceChunk, null);
  }

  /**
   * @since 4.1
   */
  public void write(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    if (WRITING.isEnabled())
    {
      WRITING.start(this);
    }

    writeSystemValues(out);

    CDOPermissionProvider permissionProvider = out.getPermissionProvider();
    CDOPermission permission = permissionProvider.getPermission(this, securityContext);

    int bits = flags & TRANSFER_MASK & ~PERMISSION_MASK;
    bits |= permission.getBits();

    if (referenceChunk == CDORevision.UNCHUNKED)
    {
      bits |= UNCHUNKED_FLAG;
    }
    else
    {
      bits &= ~UNCHUNKED_FLAG;
    }

    out.writeByte(bits);

    if (permission != CDOPermission.NONE)
    {
      writeValues(out, referenceChunk);
    }

    if (WRITING.isEnabled())
    {
      WRITING.stop(this);
    }
  }

  /**
   * @since 4.0
   */
  protected void writeSystemValues(CDODataOutput out) throws IOException
  {
    EClass eClass = getEClass();
    CDOClassifierRef classRef = new CDOClassifierRef(eClass);

    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Writing revision: ID={0}, className={1}, version={2}, branchPoint={3}, revised={4}, resource={5}, container={6}, featureID={7}", //$NON-NLS-1$
              id, eClass.getName(), getVersion(), branchPoint, revised, resourceID, containerID, containingFeatureID);
    }

    out.writeCDOClassifierRef(classRef);
    out.writeCDOID(id);
    out.writeCDOBranchPoint(branchPoint);
    out.writeInt(getVersion());
    if (!id.isTemporary())
    {
      out.writeLong(revised);
    }

    out.writeCDOID(resourceID);
    out.writeCDOID(out.getIDProvider().provideCDOID(containerID));
    out.writeInt(containingFeatureID);
  }

  /**
   * @see #write(CDODataOutput, int)
   * @since 3.0
   */
  public void convertEObjects(CDOIDProvider idProvider)
  {
    if (!(containerID instanceof CDOID))
    {
      containerID = idProvider.provideCDOID(containerID);
    }

    EStructuralFeature[] features = getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        CDOList list = getValueAsList(i);
        if (list != null)
        {
          boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
          for (int j = 0; j < list.size(); j++)
          {
            Object value = list.get(j, false);
            EStructuralFeature innerFeature = feature; // Prepare for possible feature map
            if (isFeatureMap)
            {
              Entry entry = (FeatureMap.Entry)value;
              innerFeature = entry.getEStructuralFeature();
              value = entry.getValue();
            }

            if (value != null && innerFeature instanceof EReference)
            {
              CDOID newValue = idProvider.provideCDOID(value);
              if (newValue != value)
              {
                list.set(j, newValue);
              }
            }
          }
        }
      }
      else
      {
        checkNoFeatureMap(feature);
        Object value = getValue(i);
        if (value != null && feature instanceof EReference)
        {
          CDOID newValue = idProvider.provideCDOID(value);
          if (newValue != value)
          {
            setValue(i, newValue);
          }
        }
      }
    }
  }

  public CDOID getID()
  {
    return id;
  }

  public void setID(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      throw new IllegalArgumentException(Messages.getString("AbstractCDORevision.1")); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  /**
   * @since 3.0
   */
  public CDOBranch getBranch()
  {
    if (branchPoint == null)
    {
      return null;
    }

    return branchPoint.getBranch();
  }

  /**
   * @since 3.0
   */
  public long getTimeStamp()
  {
    if (branchPoint == null)
    {
      return UNSPECIFIED_DATE;
    }

    return branchPoint.getTimeStamp();
  }

  /**
   * @since 3.0
   */
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting branchPoint {0}: {1}", this, branchPoint);
    }

    this.branchPoint = branchPoint;
  }

  public int getVersion()
  {
    return version;
  }

  public void setVersion(int version)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting version for {0}: v{1}", this, version);
    }

    this.version = version;
  }

  public long getRevised()
  {
    return revised;
  }

  public void setRevised(long revised)
  {
    long created = branchPoint.getTimeStamp();
    if (revised != UNSPECIFIED_DATE && revised < Math.max(0, created))
    {
      throw new IllegalArgumentException("revision=" + this + ", created=" + CDOCommonUtil.formatTimeStamp(created)
          + ", revised=" + CDOCommonUtil.formatTimeStamp(revised));
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revised {0}: {1}", this, CDOCommonUtil.formatTimeStamp(revised));
    }

    this.revised = revised;
  }

  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    return new CDORevisionDeltaImpl(origin, this);
  }

  public void merge(CDORevisionDelta delta)
  {
    CDORevisionMerger applier = new CDORevisionMerger();
    applier.merge(this, delta);
  }

  public CDOID getResourceID()
  {
    return resourceID;
  }

  public void setResourceID(CDOID resourceID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resourceID {0}: {1}", this, resourceID);
    }

    this.resourceID = resourceID;
  }

  public Object getContainerID()
  {
    return containerID;
  }

  public void setContainerID(Object containerID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting containerID {0}: {1}", this, containerID);
    }

    this.containerID = containerID;
  }

  public int getContainingFeatureID()
  {
    return containingFeatureID;
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting containingFeatureID {0}: {1}", this, containingFeatureID);
    }

    this.containingFeatureID = containingFeatureID;
  }

  public int hashCode(EStructuralFeature feature)
  {
    return getValue(feature).hashCode();
  }

  public Object get(EStructuralFeature feature, int index)
  {
    if (feature.isMany() && index != EStore.NO_INDEX)
    {
      CDOList list = getList(feature);
      return list.get(index);
    }

    return getValue(feature);
  }

  public boolean contains(EStructuralFeature feature, Object value)
  {
    CDOList list = getList(feature);
    return list.contains(value);
  }

  public int indexOf(EStructuralFeature feature, Object value)
  {
    CDOList list = getList(feature);
    return list.indexOf(value);
  }

  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    CDOList list = getList(feature);
    return list.lastIndexOf(value);
  }

  public boolean isEmpty(EStructuralFeature feature)
  {
    CDOList list = getList(feature);
    return list.isEmpty();
  }

  public int size(EStructuralFeature feature)
  {
    CDOList list = getList(feature);
    return list.size();
  }

  public Object[] toArray(EStructuralFeature feature)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    CDOList list = getList(feature);
    return list.toArray();
  }

  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    CDOList list = getList(feature);
    return list.toArray(array);
  }

  public void add(EStructuralFeature feature, int index, Object value)
  {
    CDOList list = getList(feature);
    list.add(index, value);
  }

  public void clear(EStructuralFeature feature)
  {
    setValue(feature, null);
  }

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    CDOList list = getList(feature);
    return list.move(targetIndex, sourceIndex);
  }

  public Object remove(EStructuralFeature feature, int index)
  {
    CDOList list = getList(feature);
    return list.remove(index);
  }

  public Object set(EStructuralFeature feature, int index, Object value)
  {
    if (feature.isMany())
    {
      CDOList list = getList(feature);
      return list.set(index, value);
    }

    return setValue(feature, value);
  }

  public void unset(EStructuralFeature feature)
  {
    setValue(feature, null);
  }

  /**
   * @since 4.0
   */
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adjusting references for revision {0}", this);
    }

    boolean changed = false;

    CDOID id1 = (CDOID)referenceAdjuster.adjustReference(resourceID, CDOContainerFeatureDelta.CONTAINER_FEATURE,
        CDOFeatureDelta.NO_INDEX);
    if (id1 != resourceID)
    {
      resourceID = id1;
      changed = true;
    }

    Object id2 = referenceAdjuster.adjustReference(containerID, CDOContainerFeatureDelta.CONTAINER_FEATURE,
        CDOFeatureDelta.NO_INDEX);
    if (id2 != containerID)
    {
      containerID = id2;
      changed = true;
    }

    EStructuralFeature[] features = getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature instanceof EReference || FeatureMapUtil.isFeatureMap(feature))
      {
        if (feature.isMany())
        {
          InternalCDOList list = (InternalCDOList)getValueAsList(i);
          if (list != null)
          {
            changed |= list.adjustReferences(referenceAdjuster, feature);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature);
          Object oldValue = getValue(i);
          Object newValue = type.adjustReferences(referenceAdjuster, oldValue, feature, CDOFeatureDelta.NO_INDEX);
          if (oldValue != newValue) // Just an optimization for NOOP adjusters
          {
            setValue(i, newValue);
            changed = true;
          }
        }
      }
    }

    return changed;
  }

  public Object getValue(EStructuralFeature feature)
  {
    int featureIndex = getFeatureIndex(feature);
    return getValue(featureIndex);
  }

  public Object setValue(EStructuralFeature feature, Object value)
  {
    int featureIndex = getFeatureIndex(feature);

    try
    {
      Object old = getValue(featureIndex);
      setValue(featureIndex, value);
      return old;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("AbstractCDORevision.20"), feature,
          getClassInfo()), ex);
    }
  }

  public CDOList getList(EStructuralFeature feature)
  {
    return getList(feature, 0);
  }

  public CDOList getList(EStructuralFeature feature, int size)
  {
    int featureIndex = getFeatureIndex(feature);
    CDOList list = (CDOList)getValue(featureIndex);
    if (list == null && size != -1)
    {
      list = CDOListFactory.DEFAULT.createList(size, 0, 0, feature.isOrdered());

      synchronized (this)
      {
        CDOPermission permission = getPermission();
        if (permission != CDOPermission.WRITE)
        {
          setPermission(CDOPermission.WRITE);
        }

        try
        {
          setValue(featureIndex, list);
        }
        finally
        {
          if (permission != CDOPermission.WRITE)
          {
            setPermission(permission);
          }
        }
      }
    }

    return list;
  }

  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    int featureIndex = getFeatureIndex(feature);
    setValue(featureIndex, list);
  }

  /**
   * @since 4.1
   */
  public CDOPermission getPermission()
  {
    return CDOPermission.get(flags & PERMISSION_MASK);
  }

  /**
   * @since 4.1
   */
  public void setPermission(CDOPermission permission)
  {
    flags = (byte)(flags & ~PERMISSION_MASK | permission.getBits() & PERMISSION_MASK);
  }

  /**
   * @since 4.1
   */
  public void freeze()
  {
    flags |= FROZEN_FLAG;

    if (isReadable())
    {
      EStructuralFeature[] features = getAllPersistentFeatures();
      for (int i = 0; i < features.length; i++)
      {
        EStructuralFeature feature = features[i];
        if (feature.isMany())
        {
          InternalCDOList list = (InternalCDOList)doGetValue(i);
          if (list != null)
          {
            list.freeze();
          }
        }
      }
    }
  }

  /**
   * @since 4.1
   */
  public boolean isUnchunked()
  {
    return (flags & UNCHUNKED_FLAG) != 0;
  }

  /**
   * @since 4.1
   */
  public void setUnchunked()
  {
    flags |= UNCHUNKED_FLAG;
  }

  protected Object getValue(int featureIndex)
  {
    checkReadable();
    return doGetValue(featureIndex);
  }

  protected void setValue(int featureIndex, Object value)
  {
    checkFrozen(featureIndex, value);
    checkWritable();
    doSetValue(featureIndex, value);
  }

  protected abstract void initValues(EStructuralFeature[] allPersistentFeatures);

  /**
   * @since 4.1
   */
  protected abstract Object doGetValue(int featureIndex);

  /**
   * @since 4.1
   */
  protected abstract void doSetValue(int featureIndex, Object value);

  private CDOList getValueAsList(int i)
  {
    return (CDOList)getValue(i);
  }

  private void checkFrozen(int featureIndex, Object value)
  {
    if ((flags & FROZEN_FLAG) != 0)
    {
      Object oldValue = getValue(featureIndex);

      // Exception 1: Setting an empty list as the value for an isMany feature, is
      // allowed if the old value is null. This is a case of lazy initialization.
      boolean newIsEmptyList = value instanceof EList<?> && ((EList<?>)value).size() == 0;
      if (newIsEmptyList && oldValue == null)
      {
        return;
      }

      // Exception 2a: Replacing a temp ID with a regular ID is allowed (happens during
      // postCommit of new objects)
      // Exception 2b: Replacing a temp ID with another temp ID is also allowed (happens
      // when changes are imported in a PushTx).
      if (oldValue instanceof CDOIDTemp && value instanceof CDOID)
      {
        return;
      }

      throw new IllegalStateException("Cannot modify a frozen revision");
    }
  }

  private void checkReadable()
  {
    if (!isReadable())
    {
      throw new NoPermissionException(this);
    }
  }

  private void checkWritable()
  {
    if (!isWritable())
    {
      throw new NoPermissionException(this);
    }
  }

  private void writeValues(CDODataOutput out, int referenceChunk) throws IOException
  {
    EClass owner = getEClass();
    EStructuralFeature[] features = getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      Object value = getValue(i);
      if (value == null)
      {
        // Feature is NOT set
        out.writeByte(UNSET);
        continue;
      }

      // Feature IS set
      if (value == CDORevisionData.NIL)
      {
        // Feature IS null
        out.writeByte(SET_NULL);
        continue;
      }

      // Feature is NOT null
      out.writeByte(SET_NOT_NULL);
      if (feature.isMany())
      {
        CDOList list = (CDOList)value;
        out.writeCDOList(owner, feature, list, referenceChunk);
      }
      else
      {
        checkNoFeatureMap(feature);
        if (feature instanceof EReference)
        {
          value = out.getIDProvider().provideCDOID(value);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Writing feature {0}: {1}", feature.getName(), value);
        }

        out.writeCDOFeatureValue(feature, value);
      }
    }
  }

  private void readValues(CDODataInput in) throws IOException
  {
    EClass owner = getEClass();
    EStructuralFeature[] features = getAllPersistentFeatures();
    initValues(features);
    for (int i = 0; i < features.length; i++)
    {
      Object value;
      EStructuralFeature feature = features[i];
      byte unsetState = in.readByte();
      switch (unsetState)
      {
      case UNSET:
        continue;

      case SET_NULL:
        setValue(i, CDORevisionData.NIL);
        continue;
      }

      if (feature.isMany())
      {
        value = in.readCDOList(owner, feature);
      }
      else
      {
        value = in.readCDOFeatureValue(feature);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read feature {0}: {1}", feature.getName(), value);
        }
      }

      setValue(i, value);
    }
  }

  public static void checkNoFeatureMap(EStructuralFeature feature)
  {
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      throw new UnsupportedOperationException("Single-valued feature maps not yet handled");
    }
  }

  public static Object remapID(Object value, Map<CDOID, CDOID> idMappings, boolean allowUnmappedTempIDs)
  {
    if (value instanceof CDOID)
    {
      CDOID oldID = (CDOID)value;
      if (!oldID.isNull())
      {
        CDOID newID = idMappings.get(oldID);
        if (newID != null)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Adjusting ID: {0} --> {1}", oldID, newID);
          }

          return newID;
        }

        if (oldID instanceof CDOIDTemp)
        {
          throw new IllegalStateException(MessageFormat.format(Messages.getString("AbstractCDORevision.2"), oldID));
        }
      }
    }

    return value;
  }
}
