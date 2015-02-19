/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 299194: unsettable features inconsistent between revisions
 *    Erdal Karaca - added support for HASHMAP CDO Type
 */
package org.eclipse.emf.cdo.internal.common.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * @author Eike Stepper
 */
public abstract class CDOTypeImpl implements CDOType
{
  private static CDOTypeImpl[] ids = new CDOTypeImpl[Byte.MAX_VALUE - Byte.MIN_VALUE + 1];

  private static final byte BOOLEAN_DEFAULT_PRIMITIVE = 0;

  private static final char CHARACTER_DEFAULT_PRIMITIVE = 0;

  private static final short SHORT_DEFAULT_PRIMITIVE = 0;

  public static final Boolean BOOLEAN_DEFAULT = new Boolean(false);

  public static final Byte BYTE_DEFAULT = new Byte(BOOLEAN_DEFAULT_PRIMITIVE);

  public static final Character CHARACTER_DEFAULT = new Character(CHARACTER_DEFAULT_PRIMITIVE);

  public static final Double DOUBLE_DEFAULT = new Double(0.0);

  public static final Float FLOAT_DEFAULT = new Float(0.0);

  public static final Integer INTEGER_DEFAULT = new Integer(0);

  public static final Long LONG_DEFAULT = new Long(0L);

  public static final Short SHORT_DEFAULT = new Short(SHORT_DEFAULT_PRIMITIVE);

  public static final CDOType BOOLEAN = new CDOTypeImpl("BOOLEAN", EcorePackage.EBOOLEAN, false, BOOLEAN_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      boolean v = (Boolean)(value == null ? getDefaultValue() : value);
      out.writeBoolean(v);
    }

    public Boolean readValue(CDODataInput in) throws IOException
    {
      return in.readBoolean();
    }
  };

  public static final CDOType BYTE = new CDOTypeImpl("BYTE", EcorePackage.EBYTE, false, BYTE_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeByte((Byte)(value == null ? getDefaultValue() : value));
    }

    public Byte readValue(CDODataInput in) throws IOException
    {
      return in.readByte();
    }
  };

  public static final CDOType CHAR = new CDOTypeImpl("CHAR", EcorePackage.ECHAR, false, CHARACTER_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeChar(((Character)(value == null ? getDefaultValue() : value)).charValue());
    }

    public Character readValue(CDODataInput in) throws IOException
    {
      return in.readChar();
    }
  };

  public static final CDOType DOUBLE = new CDOTypeImpl("DOUBLE", EcorePackage.EDOUBLE, false, DOUBLE_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeDouble((Double)(value == null ? getDefaultValue() : value));
    }

    public Double readValue(CDODataInput in) throws IOException
    {
      return in.readDouble();
    }
  };

  public static final CDOType FLOAT = new CDOTypeImpl("FLOAT", EcorePackage.EFLOAT, false, FLOAT_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeFloat((Float)(value == null ? getDefaultValue() : value));
    }

    public Float readValue(CDODataInput in) throws IOException
    {
      return in.readFloat();
    }
  };

  public static final CDOType INT = new CDOTypeImpl("INT", EcorePackage.EINT, false, INTEGER_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)(value == null ? getDefaultValue() : value));
    }

    public Integer readValue(CDODataInput in) throws IOException
    {
      return in.readInt();
    }
  };

  public static final CDOType LONG = new CDOTypeImpl("LONG", EcorePackage.ELONG, false, LONG_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeLong((Long)(value == null ? getDefaultValue() : value));
    }

    public Long readValue(CDODataInput in) throws IOException
    {
      return in.readLong();
    }
  };

  public static final CDOType SHORT = new CDOTypeImpl("SHORT", EcorePackage.ESHORT, false, SHORT_DEFAULT) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeShort((Short)(value == null ? getDefaultValue() : value));
    }

    public Short readValue(CDODataInput in) throws IOException
    {
      return in.readShort();
    }
  };

  public static final CDOType BIG_DECIMAL = new CDOTypeImpl("BIG_DECIMAL", EcorePackage.EBIG_DECIMAL, true) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value == null)
      {
        out.writeByteArray(null);
      }
      else
      {
        BigDecimal bigDecimal = (BigDecimal)value;
        out.writeByteArray(bigDecimal.unscaledValue().toByteArray());
        out.writeInt(bigDecimal.scale());
      }
    }

    public BigDecimal readValue(CDODataInput in) throws IOException
    {
      byte[] array = in.readByteArray();
      if (array == null)
      {
        return null;
      }

      BigInteger unscaled = new BigInteger(array);
      int scale = in.readInt();
      return new BigDecimal(unscaled, scale);
    }
  };

  public static final CDOType BIG_INTEGER = new CDOTypeImpl("BIG_INTEGER", EcorePackage.EBIG_INTEGER, true) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value == null)
      {
        out.writeByteArray(null);
      }
      else
      {
        out.writeByteArray(((BigInteger)value).toByteArray());
      }
    }

    public BigInteger readValue(CDODataInput in) throws IOException
    {
      byte[] array = in.readByteArray();
      if (array == null)
      {
        return null;
      }

      return new BigInteger(array);
    }
  };

  public static final CDOType OBJECT = new CDOTypeImpl("OBJECT", EcorePackage.EOBJECT, true, CDOID.NULL) //$NON-NLS-1$
  {
    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value instanceof CDORevision)
      {
        out.writeCDOID(((CDORevision)value).getID());
      }
      else
      {
        out.writeCDOID((CDOID)value);
      }
    }

    public CDOID readValue(CDODataInput in) throws IOException
    {
      return in.readCDOID();
    }

    @Override
    public Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value, EStructuralFeature feature, int index)
    {
      return adjuster.adjustReference(value, feature, index);
    }
  };

  public static final CDOType BOOLEAN_OBJECT = new ObjectType("BOOLEAN_OBJECT", EcorePackage.EBOOLEAN_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeBoolean((Boolean)value);
    }

    @Override
    protected Boolean doReadValue(CDODataInput in) throws IOException
    {
      return in.readBoolean();
    }
  };

  public static final CDOType BYTE_OBJECT = new ObjectType("BYTE_OBJECT", EcorePackage.EBYTE_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeByte((Byte)value);
    }

    @Override
    protected Byte doReadValue(CDODataInput in) throws IOException
    {
      return in.readByte();
    }
  };

  public static final CDOType CHARACTER_OBJECT = new ObjectType("CHARACTER_OBJECT", EcorePackage.ECHARACTER_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeChar((Character)value);
    }

    @Override
    protected Character doReadValue(CDODataInput in) throws IOException
    {
      return in.readChar();
    }
  };

  public static final CDOType DATE = new ObjectType("DATE", EcorePackage.EDATE) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeLong(((Date)value).getTime());
    }

    @Override
    protected Date doReadValue(CDODataInput in) throws IOException
    {
      return new Date(in.readLong());
    }
  };

  public static final CDOType DOUBLE_OBJECT = new ObjectType("DOUBLE_OBJECT", EcorePackage.EDOUBLE_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeDouble((Double)value);
    }

    @Override
    protected Double doReadValue(CDODataInput in) throws IOException
    {
      return in.readDouble();
    }
  };

  public static final CDOType FLOAT_OBJECT = new ObjectType("FLOAT_OBJECT", EcorePackage.EFLOAT_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeFloat((Float)value);
    }

    @Override
    protected Float doReadValue(CDODataInput in) throws IOException
    {
      return in.readFloat();
    }
  };

  public static final CDOType INTEGER_OBJECT = new ObjectType("INTEGER_OBJECT", EcorePackage.EINTEGER_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)value);
    }

    @Override
    protected Integer doReadValue(CDODataInput in) throws IOException
    {
      return in.readInt();
    }
  };

  public static final CDOType LONG_OBJECT = new ObjectType("LONG_OBJECT", EcorePackage.ELONG_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeLong((Long)value);
    }

    @Override
    protected Long doReadValue(CDODataInput in) throws IOException
    {
      return in.readLong();
    }
  };

  public static final CDOType SHORT_OBJECT = new ObjectType("SHORT_OBJECT", EcorePackage.ESHORT_OBJECT) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeShort((Short)value);
    }

    @Override
    protected Short doReadValue(CDODataInput in) throws IOException
    {
      return in.readShort();
    }
  };

  public static final CDOType STRING = new CDOTypeImpl("STRING", EcorePackage.ESTRING, true) //$NON-NLS-1$
  {
    @Override
    protected String doCopyValue(Object value)
    {
      return (String)value;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeString((String)value);
    }

    public String readValue(CDODataInput in) throws IOException
    {
      return in.readString();
    }
  };

  public static final CDOType BYTE_ARRAY = new CDOTypeImpl("BYTE_ARRAY", EcorePackage.EBYTE_ARRAY, true) //$NON-NLS-1$
  {
    @Override
    protected byte[] doCopyValue(Object value)
    {
      byte[] array = (byte[])value;
      byte[] result = new byte[array.length];
      System.arraycopy(value, 0, result, 0, array.length);
      return result;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeByteArray((byte[])value);
    }

    public byte[] readValue(CDODataInput in) throws IOException
    {
      return in.readByteArray();
    }
  };

  public static final CDOType FEATURE_MAP_ENTRY = new CDOTypeImpl("FEATURE_MAP_ENTRY", EcorePackage.EFEATURE_MAP_ENTRY, //$NON-NLS-1$
      false)
  {
    @Override
    protected FeatureMap.Entry doCopyValue(Object value)
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature innerFeature = entry.getEStructuralFeature();
      Object innerValue = entry.getValue();
      CDOType innerType = CDOModelUtil.getType(innerFeature.getEType());

      Object innerCopy = innerType.copyValue(innerValue);
      return CDORevisionUtil.createFeatureMapEntry(innerFeature, innerCopy);
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    public FeatureMap.Entry readValue(CDODataInput in) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value, EStructuralFeature feature, int index)
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature innerFeature = entry.getEStructuralFeature();
      Object innerValue = entry.getValue();
      CDOType innerType = CDOModelUtil.getType(innerFeature.getEType());

      Object innerCopy = innerType.adjustReferences(adjuster, innerValue, feature, index);
      if (innerCopy != innerValue) // Just an optimization for NOOP adjusters
      {
        value = CDORevisionUtil.createFeatureMapEntry(innerFeature, innerCopy);
      }

      return value;
    }
  };

  public static final CDOType CUSTOM = new CDOTypeImpl("CUSTOM", 0, true) //$NON-NLS-1$
  {
    @Override
    protected String doCopyValue(Object value)
    {
      return (String)value;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeString((String)value);
    }

    public String readValue(CDODataInput in) throws IOException
    {
      return in.readString();
    }

    @Override
    public Object convertToEMF(EClassifier eType, Object value)
    {
      return EcoreUtil.createFromString((EDataType)eType, (String)value);
    }

    @Override
    public Object convertToCDO(EClassifier eType, Object value)
    {
      return EcoreUtil.convertToString((EDataType)eType, value);
    }
  };

  /**
   * TODO Transfer integers!
   */
  public static final CDOType ENUM_ORDINAL = new ObjectType("ENUM_ORDINAL", -1) //$NON-NLS-1$
  {
    @Override
    protected Integer doCopyValue(Object value)
    {
      return (Integer)value;
    }

    @Override
    public void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      out.writeInt((Integer)value);
    }

    @Override
    public Integer doReadValue(CDODataInput in) throws IOException
    {
      return in.readInt();
    }

    @Override
    public Object convertToCDO(EClassifier type, Object value)
    {
      for (EEnumLiteral literal : ((EEnum)type).getELiterals())
      {
        if (literal == value || literal.getInstance() == value)
        {
          return literal.getValue();
        }
      }

      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOTypeImpl.23"), value)); //$NON-NLS-1$
    }

    @Override
    public Object convertToEMF(EClassifier type, Object value)
    {
      return ((EEnum)type).getEEnumLiteral((Integer)value).getInstance();
    }
  };

  public static final CDOType ENUM_LITERAL = new ObjectType("ENUM_LITERAL", -2) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      EEnum eEnum;
      if (value instanceof EEnumLiteral)
      {
        eEnum = ((EEnumLiteral)value).getEEnum();
      }
      else
      {
        eEnum = findEnum((InternalCDOPackageRegistry)out.getPackageRegistry(), value);
      }

      out.writeCDOClassifierRef(eEnum);
      out.writeInt(((Enumerator)value).getValue());
    }

    @Override
    protected Object doReadValue(CDODataInput in) throws IOException
    {
      EEnum eEnum = (EEnum)in.readCDOClassifierRefAndResolve();
      int ordinal = in.readInt();

      EEnumLiteral literal = eEnum.getEEnumLiteral(ordinal);
      if (literal == null)
      {
        throw new IllegalArgumentException("Enum literal " + ordinal + " not found in " + eEnum);
      }

      return literal.getInstance();
    }

    private EEnum findEnum(InternalCDOPackageRegistry registry, Object value)
    {
      Set<String> keys = registry.getAllKeys();

      // First try all the packages that are already resolved
      for (String nsURI : keys)
      {
        Object possiblePackage = registry.getWithDelegation(nsURI, false);
        if (possiblePackage instanceof EPackage)
        {
          EPackage ePackage = (EPackage)possiblePackage;
          EEnum eEnum = findEnum(ePackage, value);
          if (eEnum != null)
          {
            return eEnum;
          }
        }
      }

      // Then try all the package descriptors
      for (String nsURI : keys)
      {
        Object possiblePackage = registry.getWithDelegation(nsURI, false);
        if (possiblePackage instanceof EPackage.Descriptor)
        {
          EPackage ePackage = registry.getEPackage(nsURI);
          EEnum eEnum = findEnum(ePackage, value);
          if (eEnum != null)
          {
            return eEnum;
          }
        }
      }

      throw new IllegalArgumentException("EENum instance " + value.getClass().getName() + " not supported");
    }

    private EEnum findEnum(EPackage ePackage, Object value)
    {
      for (EClassifier eClassifier : ePackage.getEClassifiers())
      {
        if (eClassifier instanceof EEnum)
        {
          EEnum eEnum = (EEnum)eClassifier;
          if (eEnum.getInstanceClass() != null && eEnum.getInstanceClass() == value.getClass())
          {
            return eEnum;
          }
        }
      }

      return null;
    }
  };

  public static final CDOType BLOB = new CDOTypeImpl("BLOB", -3, true) //$NON-NLS-1$
  {
    public CDOBlob readValue(CDODataInput in) throws IOException
    {
      if (in.readBoolean())
      {
        return CDOLobUtil.readBlob(in);
      }

      return null;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value != null)
      {
        out.writeBoolean(true);
        CDOLobUtil.write(out, (CDOBlob)value);
      }
      else
      {
        out.writeBoolean(false);
      }
    }
  };

  public static final CDOType CLOB = new CDOTypeImpl("CLOB", -4, true) //$NON-NLS-1$
  {
    public CDOClob readValue(CDODataInput in) throws IOException
    {
      if (in.readBoolean())
      {
        return CDOLobUtil.readClob(in);
      }

      return null;
    }

    public void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value != null)
      {
        out.writeBoolean(true);
        CDOLobUtil.write(out, (CDOClob)value);
      }
      else
      {
        out.writeBoolean(false);
      }
    }
  };

  public static final CDOType OBJECT_ARRAY = new ObjectType("OBJECT_ARRAY", -5) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      Object[] objects = (Object[])value;
      out.writeInt(objects.length);
      for (Object object : objects)
      {
        writeTypeAndValue(out, object);
      }
    }

    @Override
    protected Object[] doReadValue(CDODataInput in) throws IOException
    {
      int size = in.readInt();
      Object[] objects = new Object[size];
      for (int i = 0; i < size; i++)
      {
        objects[i] = readTypeAndValue(in);
      }

      return objects;
    }

    @Override
    public Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value, EStructuralFeature feature, int index)
    {
      Object[] objects = (Object[])value;
      int i = 0;
      for (Object object : objects)
      {
        if (object instanceof CDOID)
        {
          objects[i] = adjuster.adjustReference(object, feature, i);
        }
        else
        {
          objects[i] = object;
        }

        ++i;
      }

      return objects;
    }
  };

  public static final CDOType MAP = new ObjectType("MAP", -6) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      @SuppressWarnings("unchecked")
      Map<Object, Object> map = (Map<Object, Object>)value;
      out.writeInt(map.size());

      for (Entry<Object, Object> entry : map.entrySet())
      {
        writeTypeAndValue(out, entry.getKey());
        writeTypeAndValue(out, entry.getValue());
      }
    }

    @Override
    protected Map<Object, Object> doReadValue(CDODataInput in) throws IOException
    {
      Map<Object, Object> result = new HashMap<Object, Object>();
      int size = in.readInt();
      for (int i = 0; i < size; i++)
      {
        Object key = readTypeAndValue(in);
        Object value = readTypeAndValue(in);
        result.put(key, value == CDOID.NULL ? null : value);
      }

      return result;
    }
  };

  public static final CDOType SET = new ObjectType("SET", -7) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      @SuppressWarnings("unchecked")
      Set<Object> set = (Set<Object>)value;
      out.writeInt(set.size());
      for (Object element : set)
      {
        writeTypeAndValue(out, element);
      }
    }

    @Override
    protected Set<Object> doReadValue(CDODataInput in) throws IOException
    {
      Set<Object> result = new HashSet<Object>();
      int size = in.readInt();
      for (int i = 0; i < size; i++)
      {
        Object element = readTypeAndValue(in);
        result.add(element == CDOID.NULL ? null : element);
      }

      return result;
    }
  };

  public static final CDOType LIST = new ObjectType("LIST", -8) //$NON-NLS-1$
  {
    @Override
    protected void doWriteValue(CDODataOutput out, Object value) throws IOException
    {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>)value;
      out.writeInt(list.size());
      for (Object element : list)
      {
        writeTypeAndValue(out, element);
      }
    }

    @Override
    protected List<Object> doReadValue(CDODataInput in) throws IOException
    {
      List<Object> result = new ArrayList<Object>();
      int size = in.readInt();
      for (int i = 0; i < size; i++)
      {
        Object element = readTypeAndValue(in);
        result.add(element == CDOID.NULL ? null : element);
      }

      return result;
    }
  };

  private String name;

  private byte typeID;

  private boolean canBeNull;

  private Object defaultValue;

  private CDOTypeImpl(String name, int typeID, boolean canBeNull, Object defaultValue)
  {
    ids[typeID - Byte.MIN_VALUE] = this;

    this.name = name;
    this.typeID = (byte)typeID;
    this.canBeNull = canBeNull;
    this.defaultValue = defaultValue;
  }

  private CDOTypeImpl(String name, int typeID, boolean canBeNull)
  {
    this(name, typeID, canBeNull, null);
  }

  public String getName()
  {
    return name;
  }

  public byte getTypeID()
  {
    return typeID;
  }

  public boolean canBeNull()
  {
    return canBeNull;
  }

  public Object getDefaultValue()
  {
    return defaultValue;
  }

  @Override
  public String toString()
  {
    return name;
  }

  public final Object copyValue(Object value)
  {
    if (value == null || value == CDORevisionData.NIL)
    {
      return value;
    }

    return doCopyValue(value);
  }

  protected Object doCopyValue(Object value)
  {
    return value;
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeByte(typeID);
  }

  final public Object adjustReferences(CDOReferenceAdjuster adjuster, Object value, EStructuralFeature feature,
      int index)
  {
    return value == null ? null : doAdjustReferences(adjuster, value, feature, index);
  }

  protected Object doAdjustReferences(CDOReferenceAdjuster adjuster, Object value, EStructuralFeature feature,
      int indexs)
  {
    return value;
  }

  /**
   * @since 2.0
   */
  public Object convertToEMF(EClassifier feature, Object value)
  {
    return value;
  }

  /**
   * @since 2.0
   */
  public Object convertToCDO(EClassifier feature, Object value)
  {
    return value;
  }

  protected void writeTypeAndValue(CDODataOutput out, Object object) throws IOException
  {
    CDOType cdoType = CDOModelUtil.getTypeOfObject(object);
    out.writeByte(cdoType.getTypeID());
    cdoType.writeValue(out, object);
  }

  protected Object readTypeAndValue(CDODataInput in) throws IOException
  {
    byte typeID = in.readByte();
    CDOType cdoType = CDOModelUtil.getType(typeID);
    return cdoType.readValue(in);
  }

  public static CDOType getType(byte typeID)
  {
    CDOTypeImpl type = ids[typeID - Byte.MIN_VALUE];
    if (type == null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOModelUtil.6"), typeID));
    }

    return type;
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class ObjectType extends CDOTypeImpl
  {
    public ObjectType(String name, int typeID)
    {
      super(name, typeID, true);
    }

    public final void writeValue(CDODataOutput out, Object value) throws IOException
    {
      if (value == null)
      {
        out.writeBoolean(false);
      }
      else
      {
        out.writeBoolean(true);
        doWriteValue(out, value);
      }
    }

    protected abstract void doWriteValue(CDODataOutput out, Object value) throws IOException;

    public final Object readValue(CDODataInput in) throws IOException
    {
      boolean notNull = in.readBoolean();
      if (notNull)
      {
        return doReadValue(in);
      }

      return null;
    }

    protected abstract Object doReadValue(CDODataInput in) throws IOException;
  }
}
