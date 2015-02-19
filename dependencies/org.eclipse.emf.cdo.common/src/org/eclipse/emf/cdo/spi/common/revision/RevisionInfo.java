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
package org.eclipse.emf.cdo.spi.common.revision;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class RevisionInfo
{
  private static final int NO_RESULT = 0;

  private static final int POINTER_RESULT = 1;

  private static final int DETACHED_RESULT = 2;

  private static final int NORMAL_RESULT = 3;

  private CDOID id;

  private CDOBranchPoint requestedBranchPoint;

  private InternalCDORevision result;

  private SyntheticCDORevision synthetic;

  protected RevisionInfo(CDOID id, CDOBranchPoint requestedBranchPoint)
  {
    CheckUtil.checkArg(requestedBranchPoint, "requestedBranchPoint");
    this.id = id;
    this.requestedBranchPoint = requestedBranchPoint;
  }

  protected RevisionInfo(CDODataInput in, CDOBranchPoint requestedBranchPoint) throws IOException
  {
    CheckUtil.checkArg(requestedBranchPoint, "requestedBranchPoint");
    id = in.readCDOID();
    this.requestedBranchPoint = requestedBranchPoint;
  }

  public abstract Type getType();

  public final CDOID getID()
  {
    return id;
  }

  public final CDOBranchPoint getRequestedBranchPoint()
  {
    return requestedBranchPoint;
  }

  public InternalCDORevision getResult()
  {
    return result;
  }

  public void setResult(InternalCDORevision result)
  {
    this.result = result;
  }

  public SyntheticCDORevision getSynthetic()
  {
    return synthetic;
  }

  public void setSynthetic(SyntheticCDORevision synthetic)
  {
    this.synthetic = synthetic;
  }

  public abstract boolean isLoadNeeded();

  public void write(CDODataOutput out) throws IOException
  {
    out.writeByte(getType().ordinal());
    out.writeCDOID(getID());
  }

  public static RevisionInfo read(CDODataInput in, CDOBranchPoint requestedBranchPoint) throws IOException
  {
    byte ordinal = in.readByte();
    Type type = Type.values()[ordinal];
    switch (type)
    {
    case AVAILABLE_NORMAL:
      return new Available.Normal(in, requestedBranchPoint);

    case AVAILABLE_POINTER:
      return new Available.Pointer(in, requestedBranchPoint);

    case AVAILABLE_DETACHED:
      return new Available.Detached(in, requestedBranchPoint);

    case MISSING:
      return new Missing(in, requestedBranchPoint);

    default:
      throw new IOException("Invalid revision info type: " + type);
    }
  }

  public void execute(InternalCDORevisionManager revisionManager, int referenceChunk)
  {
    SyntheticCDORevision[] synthetics = new SyntheticCDORevision[1];
    result = revisionManager.getRevision(getID(), requestedBranchPoint, referenceChunk, CDORevision.DEPTH_NONE, true,
        synthetics);
    synthetic = synthetics[0];
  }

  /**
   * @deprecated Not called anymore by the framework
   */
  @Deprecated
  public void writeResult(CDODataOutput out, int referenceChunk) throws IOException
  {
    writeResult(out, referenceChunk, null);
  }

  /**
   * @since 4.1
   */
  public void writeResult(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    writeRevision(out, referenceChunk, securityContext);
    writeResult(out, synthetic, referenceChunk, securityContext);
  }

  public void readResult(CDODataInput in) throws IOException
  {
    readRevision(in);
    synthetic = (SyntheticCDORevision)readResult(in, getID(), requestedBranchPoint.getBranch());
  }

  public void processResult(InternalCDORevisionManager revisionManager, List<CDORevision> results,
      SyntheticCDORevision[] synthetics, int i)
  {
    if (result instanceof DetachedCDORevision)
    {
      results.add(null);
    }
    else
    {
      results.add(result);
    }

    if (result != null)
    {
      revisionManager.addRevision(result);
    }

    if (synthetic != null)
    {
      revisionManager.addRevision(synthetic);
      if (synthetic instanceof PointerCDORevision)
      {
        PointerCDORevision pointer = (PointerCDORevision)synthetic;
        CDOBranchVersion target = pointer.getTarget();
        if (target != result && target instanceof InternalCDORevision)
        {
          revisionManager.addRevision((CDORevision)target);
        }
      }

      if (synthetics != null)
      {
        synthetics[i] = synthetic;
      }
    }
  }

  /**
   * @deprecated Not called anymore by the framework
   */
  @Deprecated
  protected void writeRevision(CDODataOutput out, int referenceChunk) throws IOException
  {
    writeRevision(out, referenceChunk, null);
  }

  /**
   * @since 4.1
   */
  protected void writeRevision(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext)
      throws IOException
  {
    out.writeCDORevision(result, referenceChunk, securityContext);
  }

  protected void readRevision(CDODataInput in) throws IOException
  {
    result = (InternalCDORevision)in.readCDORevision();
  }

  /**
   * @since 4.0
   * @deprecated Call {@link #writeResult(CDODataOutput, InternalCDORevision, int, CDOBranchPoint)}
   */
  @Deprecated
  public static void writeResult(CDODataOutput out, InternalCDORevision revision, int referenceChunk)
      throws IOException
  {
    writeResult(out, revision, referenceChunk, null);
  }

  /**
   * @since 4.1
   */
  public static void writeResult(CDODataOutput out, InternalCDORevision revision, int referenceChunk,
      CDOBranchPoint securityContext) throws IOException
  {
    if (revision == null)
    {
      out.writeByte(NO_RESULT);
    }
    else if (revision instanceof PointerCDORevision)
    {
      PointerCDORevision pointer = (PointerCDORevision)revision;
      out.writeByte(POINTER_RESULT);
      out.writeCDOClassifierRef(pointer.getEClass());
      out.writeLong(pointer.getRevised());

      CDOBranchVersion target = pointer.getTarget();
      if (target instanceof InternalCDORevision)
      {
        writeResult(out, (InternalCDORevision)target, referenceChunk, securityContext);
      }
      else
      {
        out.writeByte(NO_RESULT);
      }
    }
    else if (revision instanceof DetachedCDORevision)
    {
      DetachedCDORevision detached = (DetachedCDORevision)revision;
      out.writeByte(DETACHED_RESULT);
      out.writeCDOClassifierRef(detached.getEClass());
      out.writeLong(detached.getTimeStamp());
      out.writeLong(detached.getRevised());
      out.writeInt(detached.getVersion());
    }
    else
    {
      out.writeByte(NORMAL_RESULT);
      out.writeCDORevision(revision, referenceChunk, securityContext);
    }
  }

  /**
   * @since 4.0
   */
  public static InternalCDORevision readResult(CDODataInput in, CDOID id, CDOBranch branch) throws IOException
  {
    byte type = in.readByte();
    switch (type)
    {
    case NO_RESULT:
      return null;

    case POINTER_RESULT:
    {
      EClassifier classifier = in.readCDOClassifierRefAndResolve();
      long revised = in.readLong();
      InternalCDORevision target = readResult(in, id, branch);
      return new PointerCDORevision((EClass)classifier, id, branch, revised, target);
    }

    case DETACHED_RESULT:
    {
      EClassifier classifier = in.readCDOClassifierRefAndResolve();
      long timeStamp = in.readLong();
      long revised = in.readLong();
      int version = in.readInt();
      return new DetachedCDORevision((EClass)classifier, id, branch, version, timeStamp, revised);
    }

    case NORMAL_RESULT:
      return (InternalCDORevision)in.readCDORevision();

    default:
      throw new IllegalStateException("Invalid synthetic type: " + type);
    }
  }

  /**
   * @deprecated Not called anymore by the framework
   */
  @Deprecated
  protected void doWriteResult(CDODataOutput out, InternalCDORevision revision, int referenceChunk) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated Not called anymore by the framework
   */
  @Deprecated
  protected InternalCDORevision doReadResult(CDODataInput in) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static enum Type
  {
    AVAILABLE_NORMAL, AVAILABLE_POINTER, AVAILABLE_DETACHED, MISSING
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static abstract class Available extends RevisionInfo
  {
    private CDOBranchVersion availableBranchVersion;

    protected Available(CDOID id, CDOBranchPoint requestedBranchPoint, CDOBranchVersion availableBranchVersion)
    {
      super(id, requestedBranchPoint);
      this.availableBranchVersion = availableBranchVersion;
    }

    protected Available(CDODataInput in, CDOBranchPoint requestedBranchPoint) throws IOException
    {
      super(in, requestedBranchPoint);
      availableBranchVersion = in.readCDOBranchVersion();
    }

    public CDOBranchVersion getAvailableBranchVersion()
    {
      return availableBranchVersion;
    }

    public boolean isDirect()
    {
      return ObjectUtil.equals(availableBranchVersion.getBranch(), getRequestedBranchPoint().getBranch());
    }

    @Override
    public boolean isLoadNeeded()
    {
      return !isDirect();
    }

    @Override
    public void write(CDODataOutput out) throws IOException
    {
      super.write(out);
      out.writeCDOBranchVersion(availableBranchVersion);
    }

    @Override
    protected void writeRevision(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext)
        throws IOException
    {
      InternalCDORevision result = getResult();
      if (result != null && ObjectUtil.equals(result.getBranch(), availableBranchVersion.getBranch()))
      {
        // Use available
        out.writeBoolean(true);
      }
      else
      {
        out.writeBoolean(false);
        super.writeRevision(out, referenceChunk, securityContext);
      }
    }

    @Override
    protected void readRevision(CDODataInput in) throws IOException
    {
      boolean useAvailable = in.readBoolean();
      if (useAvailable)
      {
        setResult((InternalCDORevision)availableBranchVersion);
      }
      else
      {
        super.readRevision(in);
      }
    }

    /**
     * @author Eike Stepper
     * @since 3.0
     */
    public static class Normal extends Available
    {
      public Normal(CDOID id, CDOBranchPoint requestedBranchPoint, CDOBranchVersion availableBranchVersion)
      {
        super(id, requestedBranchPoint, availableBranchVersion);
      }

      private Normal(CDODataInput in, CDOBranchPoint requestedBranchPoint) throws IOException
      {
        super(in, requestedBranchPoint);
      }

      @Override
      public Type getType()
      {
        return Type.AVAILABLE_NORMAL;
      }

      @Override
      public InternalCDORevision getResult()
      {
        if (isDirect())
        {
          CDOBranchVersion branchVersion = getAvailableBranchVersion();
          if (branchVersion instanceof InternalCDORevision)
          {
            return (InternalCDORevision)branchVersion;
          }
        }

        return super.getResult();
      }

      @Override
      public void processResult(InternalCDORevisionManager revisionManager, List<CDORevision> results,
          SyntheticCDORevision[] synthetics, int i)
      {
        if (!isLoadNeeded())
        {
          setResult((InternalCDORevision)getAvailableBranchVersion());
        }

        super.processResult(revisionManager, results, synthetics, i);
      }
    }

    /**
     * @author Eike Stepper
     * @since 3.0
     */
    public static class Pointer extends Available
    {
      private CDOBranchVersion targetBranchVersion;

      private boolean hasTarget;

      public Pointer(CDOID id, CDOBranchPoint requestedBranchPoint, CDOBranchVersion availableBranchVersion,
          CDOBranchVersion targetBranchVersion)
      {
        super(id, requestedBranchPoint, availableBranchVersion);
        this.targetBranchVersion = targetBranchVersion;
        hasTarget = targetBranchVersion instanceof InternalCDORevision;
      }

      private Pointer(CDODataInput in, CDOBranchPoint requestedBranchPoint) throws IOException
      {
        super(in, requestedBranchPoint);
        if (in.readBoolean())
        {
          targetBranchVersion = in.readCDOBranchVersion();
          hasTarget = in.readBoolean();
        }
      }

      public CDOBranchVersion getTargetBranchVersion()
      {
        return targetBranchVersion;
      }

      @Override
      public Type getType()
      {
        return Type.AVAILABLE_POINTER;
      }

      public boolean hasTarget()
      {
        return hasTarget;
      }

      @Override
      public boolean isLoadNeeded()
      {
        if (getRequestedBranchPoint().getBranch().isMainBranch())
        {
          return false;
        }

        return !isDirect() || !hasTarget();
      }

      @Override
      public void write(CDODataOutput out) throws IOException
      {
        super.write(out);
        if (targetBranchVersion != null)
        {
          out.writeBoolean(true);
          out.writeCDOBranchVersion(targetBranchVersion);
          out.writeBoolean(hasTarget);
        }
        else
        {
          out.writeBoolean(false);
        }
      }

      @Override
      public void processResult(InternalCDORevisionManager revisionManager, List<CDORevision> results,
          SyntheticCDORevision[] synthetics, int i)
      {
        if (!isLoadNeeded())
        {
          CDOBranchVersion target = getTargetBranchVersion();
          if (target instanceof InternalCDORevision)
          {
            setResult((InternalCDORevision)target);
          }

          setSynthetic((PointerCDORevision)getAvailableBranchVersion());
        }

        super.processResult(revisionManager, results, synthetics, i);
      }
    }

    /**
     * @author Eike Stepper
     * @since 3.0
     */
    public static class Detached extends Available
    {
      public Detached(CDOID id, CDOBranchPoint requestedBranchPoint, CDOBranchVersion availableBranchVersion)
      {
        super(id, requestedBranchPoint, availableBranchVersion);
      }

      private Detached(CDODataInput in, CDOBranchPoint requestedBranchPoint) throws IOException
      {
        super(in, requestedBranchPoint);
      }

      @Override
      public Type getType()
      {
        return Type.AVAILABLE_DETACHED;
      }

      @Override
      public void processResult(InternalCDORevisionManager revisionManager, List<CDORevision> results,
          SyntheticCDORevision[] synthetics, int i)
      {
        if (!isLoadNeeded())
        {
          setSynthetic((DetachedCDORevision)getAvailableBranchVersion());
        }

        super.processResult(revisionManager, results, synthetics, i);
      }
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class Missing extends RevisionInfo
  {
    public Missing(CDOID id, CDOBranchPoint requestedBranchPoint)
    {
      super(id, requestedBranchPoint);
    }

    private Missing(CDODataInput in, CDOBranchPoint requestedBranchPoint) throws IOException
    {
      super(in, requestedBranchPoint);
    }

    @Override
    public Type getType()
    {
      return Type.MISSING;
    }

    @Override
    public boolean isLoadNeeded()
    {
      return true;
    }
  }
}
