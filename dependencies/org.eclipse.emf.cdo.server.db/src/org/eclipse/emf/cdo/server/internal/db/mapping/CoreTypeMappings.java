/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 271444: [DB] Multiple refactorings
 *    Stefan Winkler - bug 275303: [DB] DBStore does not handle BIG_INTEGER and BIG_DECIMAL
 *    Kai Schlamp - bug 282976: [DB] Influence Mappings through EAnnotations
 *    Stefan Winkler - bug 282976: [DB] Influence Mappings through EAnnotations
 *    Stefan Winkler - bug 285270: [DB] Support XSD based models
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMappingFactory;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EcorePackage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * This is a default implementation for the {@link ITypeMapping} interface which provides default behavor for all common
 * types.
 * 
 * @author Eike Stepper
 */
public class CoreTypeMappings
{
  public static final String ID_PREFIX = "org.eclipse.emf.cdo.server.db.CoreTypeMappings";

  /**
   * @author Eike Stepper
   */
  public static class TMEnum extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Enum",
        EcorePackage.eINSTANCE.getEEnum(), DBType.INTEGER));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      // see Bug 271941
      return resultSet.getInt(getField().getName());
      // EEnum type = (EEnum)getFeature().getEType();
      // int value = resultSet.getInt(column);
      // return type.getEEnumLiteral(value);
    }

    @Override
    protected Object getDefaultValue()
    {
      EEnum eenum = (EEnum)getFeature().getEType();

      String defaultValueLiteral = getFeature().getDefaultValueLiteral();
      if (defaultValueLiteral != null)
      {
        EEnumLiteral literal = eenum.getEEnumLiteralByLiteral(defaultValueLiteral);
        return literal.getValue();
      }

      Enumerator enumerator = (Enumerator)eenum.getDefaultValue();
      return enumerator.getValue();
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMEnum();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMString extends AbstractTypeMapping
  {
    public static final Factory FACTORY_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".StringVarchar", EcorePackage.eINSTANCE.getEString(), DBType.VARCHAR));

    public static final Factory FACTORY_LONG_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".StringLongVarchar", EcorePackage.eINSTANCE.getEString(), DBType.LONGVARCHAR));

    public static final Factory FACTORY_CLOB = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".StringClob",
        EcorePackage.eINSTANCE.getEString(), DBType.CLOB));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getString(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMString();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBlob extends AbstractTypeMapping
  {
    public static final Factory FACTORY_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".BlobStream", EtypesPackage.eINSTANCE.getBlob(), DBType.VARCHAR));

    public static final Factory FACTORY_LONG_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".BlobStreamLongVarchar", EtypesPackage.eINSTANCE.getBlob(), DBType.LONGVARCHAR));

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      CDOBlob blob = (CDOBlob)value;
      stmt.setString(index, HexUtil.bytesToHex(blob.getID()) + "-" + blob.getSize());
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String str = resultSet.getString(getField().getName());
      if (str == null)
      {
        return null;
      }

      int pos = str.indexOf('-');

      byte[] id = HexUtil.hexToBytes(str.substring(0, pos));
      long size = Long.parseLong(str.substring(pos + 1));
      return CDOLobUtil.createBlob(id, size);
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBlob();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMClob extends AbstractTypeMapping
  {
    public static final Factory FACTORY_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".ClobStream", EtypesPackage.eINSTANCE.getClob(), DBType.VARCHAR));

    public static final Factory FACTORY_LONG_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".ClobStreamLongVarchar", EtypesPackage.eINSTANCE.getClob(), DBType.LONGVARCHAR));

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      CDOClob clob = (CDOClob)value;
      stmt.setString(index, HexUtil.bytesToHex(clob.getID()) + "-" + clob.getSize());
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String str = resultSet.getString(getField().getName());
      if (str == null)
      {
        return null;
      }

      int pos = str.indexOf('-');

      byte[] id = HexUtil.hexToBytes(str.substring(0, pos));
      long size = Long.parseLong(str.substring(pos + 1));
      return CDOLobUtil.createClob(id, size);
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMClob();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMShort extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Short",
        EcorePackage.eINSTANCE.getEShort(), DBType.SMALLINT));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".ShortObject", EcorePackage.eINSTANCE.getEShortObject(), DBType.SMALLINT));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getShort(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMShort();
      }
    }
  }

  /**
   * @author Eike Stepper <br>
   */
  public static class TMObject extends AbstractTypeMapping
  {
    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      CDOID id = idHandler.getCDOID(resultSet, getField().getName());

      if (id == null && getFeature().isUnsettable())
      {
        return CDORevisionData.NIL;
      }

      return id;
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      idHandler.setCDOID(stmt, index, (CDOID)value);
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMObject();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMLong extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Long",
        EcorePackage.eINSTANCE.getELong(), DBType.BIGINT));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(
        ID_PREFIX + ".LongObject", EcorePackage.eINSTANCE.getELongObject(), DBType.BIGINT));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getLong(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMLong();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMInteger extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Integer",
        EcorePackage.eINSTANCE.getEInt(), DBType.INTEGER));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".IntegerObject", EcorePackage.eINSTANCE.getEIntegerObject(), DBType.INTEGER));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getInt(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMInteger();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMFloat extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Float",
        EcorePackage.eINSTANCE.getEFloat(), DBType.FLOAT));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".FloatObject", EcorePackage.eINSTANCE.getEFloatObject(), DBType.FLOAT));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getFloat(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMFloat();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMDouble extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Double",
        EcorePackage.eINSTANCE.getEDouble(), DBType.DOUBLE));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".DoubleObject", EcorePackage.eINSTANCE.getEDoubleObject(), DBType.DOUBLE));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getDouble(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDouble();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMDate2Timestamp extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Timestamp",
        EcorePackage.eINSTANCE.getEDate(), DBType.TIMESTAMP));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getTimestamp(getField().getName());
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setTimestamp(index, new Timestamp(((Date)value).getTime()));
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDate2Timestamp();
      }
    }
  }

  /**
   * @author Heiko Ahlig
   */
  public static class TMDate2Date extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Date",
        EcorePackage.eINSTANCE.getEDate(), DBType.DATE));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getDate(getField().getName(), Calendar.getInstance());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDate2Date();
      }
    }
  }

  /**
   * @author Heiko Ahlig
   */
  public static class TMDate2Time extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Time",
        EcorePackage.eINSTANCE.getEDate(), DBType.TIME));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getTime(getField().getName(), Calendar.getInstance());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDate2Time();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMCharacter extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Character",
        EcorePackage.eINSTANCE.getEChar(), DBType.CHAR));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".CharacterObject", EcorePackage.eINSTANCE.getECharacterObject(), DBType.CHAR));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String str = resultSet.getString(getField().getName());
      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return str.charAt(0);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setString(index, ((Character)value).toString());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMCharacter();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMByte extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Byte",
        EcorePackage.eINSTANCE.getEByte(), DBType.SMALLINT));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(
        ID_PREFIX + ".ByteObject", EcorePackage.eINSTANCE.getEByteObject(), DBType.SMALLINT));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getByte(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMByte();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBytes extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".ByteArray",
        EcorePackage.eINSTANCE.getEByteArray(), DBType.BLOB));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getBytes(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBytes();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBytesVarbinary extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".ByteArrayVarbinary", EcorePackage.eINSTANCE.getEByteArray(), DBType.VARBINARY));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getBytes(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBytesVarbinary();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBoolean extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".Boolean",
        EcorePackage.eINSTANCE.getEBoolean(), DBType.BOOLEAN));

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".BooleanObject", EcorePackage.eINSTANCE.getEBooleanObject(), DBType.BOOLEAN));

    public static final Factory FACTORY_SMALLINT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".Boolean_SMALLINT", EcorePackage.eINSTANCE.getEBoolean(), DBType.SMALLINT));

    public static final Factory FACTORY_OBJECT_SMALLINT = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".BooleanObject_SMALLINT", EcorePackage.eINSTANCE.getEBooleanObject(), DBType.SMALLINT));

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getBoolean(getField().getName());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBoolean();
      }
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class TMBigInteger extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".BigInteger",
        EcorePackage.eINSTANCE.getEBigInteger(), DBType.VARCHAR));

    public static final Factory FACTORY_LONG_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".BigIntegerLongVarChar", EcorePackage.eINSTANCE.getEBigInteger(), DBType.LONGVARCHAR));

    @Override
    protected Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String val = resultSet.getString(getField().getName());

      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return new BigInteger(val);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setString(index, ((BigInteger)value).toString());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBigInteger();
      }
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class TMBigDecimal extends AbstractTypeMapping
  {
    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".BigDecimal",
        EcorePackage.eINSTANCE.getEBigDecimal(), DBType.VARCHAR));

    public static final Factory FACTORY_LONG_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".BigDecimalLongVarchar", EcorePackage.eINSTANCE.getEBigDecimal(), DBType.LONGVARCHAR));

    @Override
    protected Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String val = resultSet.getString(getField().getName());

      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return new BigDecimal(val);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setString(index, ((BigDecimal)value).toPlainString());
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBigDecimal();
      }
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class TMCustom extends AbstractTypeMapping
  {
    public static final Factory FACTORY_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".CustomVarchar", EcorePackage.eINSTANCE.getEDataType(), DBType.VARCHAR));

    public static final Factory FACTORY_LONG_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX
        + ".CustomLongVarchar", EcorePackage.eINSTANCE.getEDataType(), DBType.LONGVARCHAR));

    public static final Factory FACTORY_CLOB = new Factory(TypeMappingUtil.createDescriptor(ID_PREFIX + ".CustomClob",
        EcorePackage.eINSTANCE.getEDataType(), DBType.CLOB));

    @Override
    protected Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String val = resultSet.getString(getField().getName());
      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return val;
    }

    @Override
    protected Object getDefaultValue()
    {
      Object defaultValue = getFeature().getDefaultValue();
      if (defaultValue == null)
      {
        return null;
      }

      EFactory factory = getFeature().getEType().getEPackage().getEFactoryInstance();
      return factory.convertToString((EDataType)getFeature().getEType(), defaultValue);
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMCustom();
      }
    }
  }
}
