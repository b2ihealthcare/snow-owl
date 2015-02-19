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
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.internal.db.DBAnnotation;
import org.eclipse.emf.cdo.server.internal.db.MetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is a default implementation for the {@link ITypeMapping} interface which provides default behavor for all common
 * types. Implementors should provide a constructor which the factory (see below) can use and implement
 * {@link #getResultSetValue(ResultSet)}. If needed, {@link #doSetValue(PreparedStatement, int, Object)} can also be
 * overridden as a counterpart to {@link #getResultSetValue(ResultSet)}. Finally, an implementor should also implement a
 * suitable factory for the {@link TypeMappingRegistry} and register it either manually using
 * {@link IManagedContainer#registerFactory(org.eclipse.net4j.util.factory.IFactory)} or using the Net4j Extension Point
 * <code>factories</code>.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 4.0
 */
public abstract class AbstractTypeMapping implements ITypeMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractTypeMapping.class);

  private IMappingStrategy mappingStrategy;

  private EStructuralFeature feature;

  private IDBField field;

  private DBType dbType;

  /**
   * Create a new type mapping
   */
  public AbstractTypeMapping()
  {
    super();
  }

  public final void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
  }

  public final IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public final void setFeature(EStructuralFeature feature)
  {
    this.feature = feature;
  }

  public final EStructuralFeature getFeature()
  {
    return feature;
  }

  public final void setValueFromRevision(PreparedStatement stmt, int index, InternalCDORevision revision)
      throws SQLException
  {
    setValue(stmt, index, getRevisionValue(revision));
  }

  public final void setDefaultValue(PreparedStatement stmt, int index) throws SQLException
  {
    setValue(stmt, index, getDefaultValue());
  }

  public final void setValue(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    if (value == CDORevisionData.NIL)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("TypeMapping for {0}: converting Revision.NIL to DB-null", feature.getName()); //$NON-NLS-1$
      }

      stmt.setNull(index, getSqlType());
    }
    else if (value == null)
    {
      if (feature.isMany() || getDefaultValue() == null)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("TypeMapping for {0}: writing Revision.null as DB.null", feature.getName()); //$NON-NLS-1$
        }

        stmt.setNull(index, getSqlType());
      }
      else
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("TypeMapping for {0}: converting Revision.null to default value", feature.getName()); //$NON-NLS-1$
        }

        setDefaultValue(stmt, index);
      }
    }
    else
    {
      doSetValue(stmt, index, value);
    }
  }

  public final void createDBField(IDBTable table)
  {
    createDBField(table, mappingStrategy.getFieldName(feature));
  }

  public final void createDBField(IDBTable table, String fieldName)
  {
    DBType fieldType = getDBType();
    int fieldLength = getDBLength(fieldType);
    field = table.addField(fieldName, fieldType, fieldLength);
  }

  public final void setDBField(IDBTable table, String fieldName)
  {
    field = table.getField(fieldName);
  }

  public final IDBField getField()
  {
    return field;
  }

  public final void readValueToRevision(ResultSet resultSet, InternalCDORevision revision) throws SQLException
  {
    Object value = readValue(resultSet);
    revision.setValue(getFeature(), value);
  }

  public final Object readValue(ResultSet resultSet) throws SQLException
  {
    Object value = getResultSetValue(resultSet);
    if (resultSet.wasNull())
    {
      if (feature.isMany())
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("TypeMapping for {0}: read db.null - setting Revision.null", feature.getName()); //$NON-NLS-1$
        }

        value = null;
      }
      else
      {
        if (getDefaultValue() == null)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format(
                "TypeMapping for {0}: read db.null - setting Revision.null, because of default", feature.getName()); //$NON-NLS-1$
          }

          value = null;
        }
        else
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("TypeMapping for {0}: read db.null - setting Revision.NIL", feature.getName()); //$NON-NLS-1$
          }

          value = CDORevisionData.NIL;
        }
      }
    }

    return value;
  }

  protected Object getDefaultValue()
  {
    return feature.getDefaultValue();
  }

  protected final Object getRevisionValue(InternalCDORevision revision)
  {
    return revision.getValue(getFeature());
  }

  /**
   * Implementors could override this method to convert a given value to the database representation and set it to the
   * prepared statement.
   * 
   * @param stmt
   *          the {@link PreparedStatement} which is used for DB access
   * @param index
   *          the parameter index in the statement which should be set
   * @param value
   *          the value of the feature which should be written into the DB
   */
  protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    stmt.setObject(index, value, getSqlType());
  }

  /**
   * Returns the SQL type of this TypeMapping. The default implementation considers the type map held by the
   * {@link MetaDataManager meta-data manager}. Subclasses may override.
   * 
   * @return The sql type of this TypeMapping.
   */
  protected int getSqlType()
  {
    return getDBType().getCode();
  }

  public final void setDBType(DBType dbType)
  {
    this.dbType = dbType;
  }

  public DBType getDBType()
  {
    return dbType;
  }

  /**
   * @since 4.1.1
   */
  protected int getDBLength(DBType type)
  {
    String value = DBAnnotation.COLUMN_LENGTH.getValue(feature);
    if (value != null)
    {
      try
      {
        return Integer.parseInt(value);
      }
      catch (NumberFormatException e)
      {
        OM.LOG.error("Illegal columnLength annotation of feature " + feature.getName());
      }
    }

    // TODO: implement DBAdapter.getDBLength
    // mappingStrategy.getStore().getDBAdapter().getDBLength(type);
    // which should then return the correct default field length for the db type

    // XXX (apeteri): we need a common maximum length that is acceptable by both H2 and MySQL
    return type == DBType.VARCHAR ? 2048 : IDBField.DEFAULT;
  }

  /**
   * Subclasses should implement this method to read the value from the result set. Typical implementations should look
   * similar to this one: <code>resultSet.getString(getField().getName())</code>
   * 
   * @param resultSet
   *          the result set to read from
   * @return the result value read (this has to be compatible with the {@link #feature}.
   */
  protected abstract Object getResultSetValue(ResultSet resultSet) throws SQLException;

}
