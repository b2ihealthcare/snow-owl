/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 213402
 *    Martin Taal - Added subtype handling and EClass conversion, bug 283106
 */
package org.eclipse.emf.cdo.common.id;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOID.Type;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongWithClassifierImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectStringImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectStringWithClassifierImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectUUIDImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDAndBranchImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDAndVersionImpl;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDByteArray;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDString;
import org.eclipse.emf.cdo.spi.common.id.InternalCDOIDObject;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.UUIDGenerator;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * Various static methods that may help with CDO {@link CDOID IDs}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOIDUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDOIDUtil.class);

  private CDOIDUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static boolean isNull(CDOID id)
  {
    return id == null || id.isNull();
  }

  public static long getLong(CDOID id)
  {
    if (id == null)
    {
      return AbstractCDOIDLong.NULL_VALUE;
    }

    switch (id.getType())
    {
    case NULL:
      return AbstractCDOIDLong.NULL_VALUE;

    case OBJECT:
      if (id instanceof AbstractCDOIDLong)
      {
        return ((AbstractCDOIDLong)id).getLongValue();
      }

      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.0"), id.getClass().getName())); //$NON-NLS-1$

    case TEMP_OBJECT:
      throw new IllegalArgumentException(Messages.getString("CDOIDUtil.1")); //$NON-NLS-1$

    case EXTERNAL_OBJECT:
    case EXTERNAL_TEMP_OBJECT:
      throw new IllegalArgumentException(Messages.getString("CDOIDUtil.2")); //$NON-NLS-1$

    default:
      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.3"), id.getClass().getName())); //$NON-NLS-1$
    }
  }

  /**
   * @since 4.0
   */
  public static String getString(CDOID id)
  {
    if (id == null)
    {
      return AbstractCDOIDString.NULL_VALUE;
    }

    switch (id.getType())
    {
    case NULL:
      return AbstractCDOIDString.NULL_VALUE;

    case OBJECT:
      if (id instanceof AbstractCDOIDString)
      {
        return ((AbstractCDOIDString)id).getStringValue();
      }

      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.0"), id.getClass().getName())); //$NON-NLS-1$

    case TEMP_OBJECT:
      throw new IllegalArgumentException(Messages.getString("CDOIDUtil.1")); //$NON-NLS-1$

    case EXTERNAL_OBJECT:
    case EXTERNAL_TEMP_OBJECT:
      if (id instanceof CDOIDExternalImpl)
      {
        return ((CDOIDExternalImpl)id).getURI();
      }

      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.0"), id.getClass().getName())); //$NON-NLS-1$

    default:
      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.3"), id.getClass().getName())); //$NON-NLS-1$
    }
  }

  /**
   * @since 4.1
   */
  public static byte[] getByteArray(CDOID id)
  {
    if (id == null)
    {
      return null;
    }

    switch (id.getType())
    {
    case NULL:
      return null;

    case OBJECT:
      if (id instanceof AbstractCDOIDByteArray)
      {
        return ((AbstractCDOIDByteArray)id).getByteArrayValue();
      }

      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.0"), id.getClass().getName())); //$NON-NLS-1$

    default:
      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("CDOIDUtil.3"), id.getClass().getName())); //$NON-NLS-1$
    }
  }

  /**
   * @since 3.0
   */
  public static CDOClassifierRef getClassifierRef(CDOID id)
  {
    if (id instanceof CDOClassifierRef.Provider)
    {
      return ((CDOClassifierRef.Provider)id).getClassifierRef();
    }

    return null;
  }

  public static CDOIDTemp createTempObject(int value)
  {
    return new CDOIDTempObjectImpl(value);
  }

  /**
   * @since 3.0
   */
  public static CDOIDExternal createTempObjectExternal(String uri)
  {
    return new CDOIDTempObjectExternalImpl(uri);
  }

  public static CDOID createLong(long value)
  {
    if (value == AbstractCDOIDLong.NULL_VALUE)
    {
      return CDOID.NULL;
    }

    return new CDOIDObjectLongImpl(value);
  }

  /**
   * @since 3.0
   */
  public static CDOID createLongWithClassifier(CDOClassifierRef classifierRef, long value)
  {
    return new CDOIDObjectLongWithClassifierImpl(classifierRef, value);
  }

  /**
   * @since 4.0
   */
  public static CDOID createString(String value)
  {
    return new CDOIDObjectStringImpl(value);
  }

  /**
   * @since 3.0
   */
  public static CDOID createStringWithClassifier(CDOClassifierRef classifierRef, String value)
  {
    return new CDOIDObjectStringWithClassifierImpl(classifierRef, value);
  }

  /**
   * @since 4.1
   */
  public static CDOID createUUID(byte[] value)
  {
    return new CDOIDObjectUUIDImpl(value);
  }

  /**
   * @since 4.1
   */
  public static CDOID createUUID()
  {
    byte[] value = new byte[16];
    UUIDGenerator.DEFAULT.generate(value);
    return createUUID(value);
  }

  /**
   * @since 4.1
   */
  public static String encodeUUID(byte[] bytes)
  {
    return UUIDGenerator.DEFAULT.encode(bytes);
  }

  /**
   * @since 4.1
   */
  public static byte[] decodeUUID(String string)
  {
    return UUIDGenerator.DEFAULT.decode(string);
  }

  /**
   * @since 2.0
   */
  public static CDOIDExternal createExternal(String uri)
  {
    return new CDOIDExternalImpl(uri);
  }

  /**
   * @since 4.0
   */
  public static CDOIDAndVersion createIDAndVersion(CDOID id, int version)
  {
    return new CDOIDAndVersionImpl(id, version);
  }

  /**
   * @since 4.0
   */
  public static CDOIDAndVersion createIDAndVersion(CDOIDAndVersion source)
  {
    return createIDAndVersion(source.getID(), source.getVersion());
  }

  /**
   * @since 4.0
   */
  public static CDOIDAndBranch createIDAndBranch(CDOID id, CDOBranch branch)
  {
    return new CDOIDAndBranchImpl(id, branch);
  }

  /**
   * Creates the correct implementation class for the passed {@link CDOID.ObjectType}.
   *
   * @param subType
   *          the subType for which to create an empty CDOID instance
   * @return the instance of CDOIDObject which represents the subtype.
   * @since 3.0
   */
  public static AbstractCDOID createCDOIDObject(CDOID.ObjectType subType)
  {
    if (subType == null)
    {
      throw new IllegalArgumentException("SubType may not be null");
    }

    InternalCDOIDObject id;
    switch (subType)
    {
    case LONG:
      id = new CDOIDObjectLongImpl();
      break;

    case STRING:
      id = new CDOIDObjectStringImpl();
      break;

    case LONG_WITH_CLASSIFIER:
      id = new CDOIDObjectLongWithClassifierImpl();
      break;

    case STRING_WITH_CLASSIFIER:
      id = new CDOIDObjectStringWithClassifierImpl();
      break;

    case UUID:
      id = new CDOIDObjectUUIDImpl();
      break;

    default:
      throw new IllegalArgumentException("Subtype " + subType.name() + " not supported");
    }

    if (id.getSubType() != subType)
    {
      throw new IllegalStateException("Subtype of created id " + id + " is unequal (" + id.getSubType().name()
          + ") to requested subtype " + subType.name());
    }

    return (AbstractCDOID)id;
  }

  /**
   * Format of the uri fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   *
   * @since 2.0
   */
  public static void write(StringBuilder builder, CDOID id)
  {
    if (id == null)
    {
      id = CDOID.NULL;
    }

    if (id instanceof InternalCDOIDObject)
    {
      ObjectType subType = ((InternalCDOIDObject)id).getSubType();
      builder.append(subType.getID());
    }
    else
    {
      Type type = id.getType();
      builder.append(type.getID());
    }

    builder.append(id.toURIFragment());
  }

  /**
   * Format of the URI fragment.
   * <p>
   * Non-legacy: <code>&lt;ID TYPE>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   * <p>
   * Legacy: <code>&lt;ID TYPE>/&lt;PACKAGE URI>/&lt;CLASSIFIER ID>/&lt;CUSTOM STRING FROM OBJECT FACTORY></code>
   *
   * @since 3.0
   */
  public static CDOID read(String uriFragment)
  {
    char typeID = uriFragment.charAt(0);
    Enum<?> literal = CDOID.Type.getLiteral(typeID);
    if (literal == null)
    {
      throw new IllegalArgumentException("Unknown type ID: " + typeID);
    }

    String fragment = uriFragment.substring(1);
    if (literal instanceof ObjectType)
    {
      return readCDOIDObject(fragment, (ObjectType)literal);
    }

    Type type = (Type)literal;
    switch (type)
    {
    case NULL:
      return CDOID.NULL;

    case TEMP_OBJECT:
      return new CDOIDTempObjectImpl(Integer.valueOf(fragment));

    case EXTERNAL_OBJECT:
      return new CDOIDExternalImpl(fragment);

    case EXTERNAL_TEMP_OBJECT:
      return new CDOIDTempObjectExternalImpl(fragment);

    case OBJECT:
    {
      // Normally this case should not occur (is an OBJECT subtype).
      throw new IllegalArgumentException();
    }

    default:
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOIDUtil.5"), uriFragment)); //$NON-NLS-1$
    }
  }

  private static CDOID readCDOIDObject(String fragment, CDOID.ObjectType subType)
  {
    AbstractCDOID id = createCDOIDObject(subType);
    id.read(fragment);
    return id;
  }

  /**
   * @since 4.1
   */
  public static void write(ExtendedDataOutput out, CDOID id) throws IOException
  {
    if (id == null)
    {
      id = CDOID.NULL;
    }

    if (id instanceof InternalCDOIDObject)
    {
      CDOID.ObjectType subType = ((InternalCDOIDObject)id).getSubType();
      int ordinal = subType.ordinal();
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing CDOIDObject of subtype {0} ({1})", ordinal, subType); //$NON-NLS-1$
      }

      // Negated to distinguish between the subtypes and the maintypes.
      // Note: Added 1 because ordinal start at 0
      out.writeByte(-ordinal - 1);
    }
    else
    {
      CDOID.Type type = id.getType();
      int ordinal = type.ordinal();
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing CDOID of type {0} ({1})", ordinal, type); //$NON-NLS-1$
      }

      out.writeByte(ordinal);
    }

    ((AbstractCDOID)id).write(out);
  }

  /**
   * @since 4.1
   */
  public static CDOID read(ExtendedDataInput in) throws IOException
  {
    byte ordinal = in.readByte();

    // A subtype of OBJECT
    if (ordinal < 0)
    {
      // The ordinal value is negated in the stream to distinguish from the main type.
      // Note: Added 1 because ordinal start at 0, so correct by minus 1.
      return readCDOIDObject(in, -ordinal - 1);
    }

    if (TRACER.isEnabled())
    {
      String type;
      try
      {
        type = Type.values()[ordinal].toString();
      }
      catch (RuntimeException ex)
      {
        type = ex.getMessage();
      }

      TRACER.format("Reading CDOID of type {0} ({1})", ordinal, type); //$NON-NLS-1$
    }

    Type type = Type.values()[ordinal];
    switch (type)
    {
    case NULL:
      return CDOID.NULL;

    case TEMP_OBJECT:
      return new CDOIDTempObjectImpl(in.readInt());

    case EXTERNAL_OBJECT:
      return new CDOIDExternalImpl(in.readString());

    case EXTERNAL_TEMP_OBJECT:
      return new CDOIDTempObjectExternalImpl(in.readString());

    case OBJECT:
    {
      // should normally not occur is handled by
      // readCDOIDObject, code remains here
      // for backward compatibility
      AbstractCDOID id = new CDOIDObjectLongImpl();
      id.read(in);
      return id;
    }

    default:
      throw new IOException("Illegal type: " + type);
    }
  }

  private static CDOID readCDOIDObject(ExtendedDataInput in, int subTypeOrdinal) throws IOException
  {
    if (TRACER.isEnabled())
    {
      String subType;

      try
      {
        subType = CDOID.ObjectType.values()[subTypeOrdinal].toString();
      }
      catch (RuntimeException ex)
      {
        subType = ex.getMessage();
      }

      TRACER.format("Reading CDOIDObject of sub type {0} ({1})", subTypeOrdinal, subType); //$NON-NLS-1$
    }

    CDOID.ObjectType subType = CDOID.ObjectType.values()[subTypeOrdinal];
    AbstractCDOID id = CDOIDUtil.createCDOIDObject(subType);
    id.read(in);
    return id;
  }

  /**
   * @since 2.0
   */
  public static boolean equals(CDOID id1, CDOID id2)
  {
    if (id1 == null)
    {
      id1 = CDOID.NULL;
    }

    if (id2 == null)
    {
      id2 = CDOID.NULL;
    }

    return ObjectUtil.equals(id1, id2);
  }
}
