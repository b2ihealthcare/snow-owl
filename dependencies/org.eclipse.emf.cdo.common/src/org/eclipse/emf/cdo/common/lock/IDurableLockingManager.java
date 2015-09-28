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
package org.eclipse.emf.cdo.common.lock;

import java.util.Map;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

/**
 * Manages all persistent aspects of durable CDO views such as {@link CDOBranchPoint branch point} and acquired locks.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @apiviz.uses {@link IDurableLockingManager.LockArea} - - manages
 */
public interface IDurableLockingManager
{
  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks) throws LockAreaAlreadyExistsException;

  /**
   * Returns the {@link LockArea lock area} specified by the given durableLockingID, never <code>null</code>.
   * 
   * @throws LockAreaNotFoundException
   *           if the given durableLockingID is unknown.
   */
  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException;

  public void getLockAreas(String userIDPrefix, LockArea.Handler handler);

  public void deleteLockArea(String durableLockingID);

  /**
   * Encapsulates the persistable information about a single durable CDO view like {@link CDOBranchPoint branch point}
   * and acquired locks.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @apiviz.composedOf {@link IDurableLockingManager.LockGrade} - - locks
   */
  public interface LockArea extends CDOBranchPoint
  {
    /**
     * @since 4.1
     */
    public static final int DEFAULT_DURABLE_LOCKING_ID_BYTES = 32;

    public String getDurableLockingID();

    public String getUserID();

    public boolean isReadOnly();

    public Map<CDOID, LockGrade> getLocks();

    /**
     * Returns <code>true</code> if this instance represents a lock area that is known to be missing (not present) on a
     * master server. (Relevant only in a replicating configuration.)
     * 
     * @since 4.1
     */
    public boolean isMissing();

    /**
     * A call-back interface for <em>handling</em> {@link LockArea lock area} objects.
     * 
     * @author Eike Stepper
     * @apiviz.uses {@link IDurableLockingManager.LockArea} - - handles
     */
    public interface Handler
    {
      public boolean handleLockArea(LockArea area);
    }
  }

  /**
   * Thrown if a {@link LockArea lock area} is spcified that does not exist in a CDO {@link CDOCommonRepository
   * repository}.
   * 
   * @author Eike Stepper
   */
  public static class LockAreaNotFoundException extends IllegalStateException
  {
    private static final long serialVersionUID = 1L;

    private String durableLockingID;

    public LockAreaNotFoundException(String durableLockingID)
    {
      super("No lock area for ID=" + durableLockingID);
      this.durableLockingID = durableLockingID;
    }

    public LockAreaNotFoundException(String message, Throwable cause, String durableLockingID)
    {
      super(message, cause);
      this.durableLockingID = durableLockingID;
    }

    public String getDurableLockingID()
    {
      return durableLockingID;
    }
  }

  /**
   * Exception occurs when attempting to create a durable {@link LockArea} that already exists.
   * 
   * @author Caspar De Groot
   * @since 4.1
   */
  public static class LockAreaAlreadyExistsException extends IllegalStateException
  {
    private static final long serialVersionUID = 1L;

    private String durableLockingID;

    public LockAreaAlreadyExistsException(String durableLockingID)
    {
      super("A lock area with ID=" + durableLockingID + " already exists");
      this.durableLockingID = durableLockingID;
    }

    public String getDurableLockingID()
    {
      return durableLockingID;
    }
  }

  /**
   * Enumerates the possible combinations of read and write locks on a single CDO object.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   */
  public enum LockGrade
  {
    NONE(0), READ(1), WRITE(2), READ_WRITE(READ.getValue() | WRITE.getValue()),

    /**
     * @since 4.1
     */
    OPTION(4),

    /**
     * @since 4.1
     */
    READ_OPTION(READ.getValue() | OPTION.getValue()),

    /**
     * @since 4.1
     */
    WRITE_OPTION(WRITE.getValue() | OPTION.getValue()),

    /**
     * @since 4.1
     */
    READ_WRITE_OPTION(READ.getValue() | WRITE.getValue() | OPTION.getValue());

    private final int value;

    private LockGrade(int value)
    {
      this.value = value;
    }

    public int getValue()
    {
      return value;
    }

    public boolean isRead()
    {
      return (value & 1) != 0;
    }

    public boolean isWrite()
    {
      return (value & 2) != 0;
    }

    /**
     * @since 4.1
     */
    public boolean isOption()
    {
      return (value & 4) != 0;
    }

    public LockGrade getUpdated(LockType type, boolean on)
    {
      int mask = getMask(type);

      if (on)
      {
        return get(value | mask);
      }

      return get(value & ~mask);
    }

    private int getMask(LockType type)
    {
      switch (type)
      {
      case READ:
        return 1;

      case WRITE:
        return 2;

      case OPTION:
        return 4;
      }

      return 0;
    }

    public static LockGrade get(LockType type)
    {
      if (type == LockType.READ)
      {
        return READ;
      }

      if (type == LockType.WRITE)
      {
        return WRITE;
      }

      if (type == LockType.OPTION)
      {
        return OPTION;
      }

      return NONE;
    }

    /**
     * @deprecated Use {@link #get(boolean, boolean, boolean)}
     */
    @Deprecated
    public static LockGrade get(boolean read, boolean write)
    {
      return get((read ? 1 : 0) | (write ? 2 : 0));
    }

    /**
     * @since 4.1
     */
    public static LockGrade get(boolean read, boolean write, boolean option)
    {
      return get((read ? 1 : 0) | (write ? 2 : 0) | (option ? 4 : 0));
    }

    public static LockGrade get(int value)
    {
      switch (value)
      {
      case 0:
        return NONE;

      case 1:
        return READ;

      case 2:
        return WRITE;

      case 3:
        return READ_WRITE;

      case 4:
        return OPTION;

      case 1 | 4:
        return READ_OPTION;

      case 2 | 4:
        return WRITE_OPTION;

      case 1 | 2 | 4:
        return READ_WRITE_OPTION;

      default:
        throw new IllegalArgumentException("Invalid lock grade: " + value);
      }
    }
  }
}
