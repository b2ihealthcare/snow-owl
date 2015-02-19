/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Christopher Albert - 254455: [DB] Support FeatureMaps bug 254455
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingRegistry;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.factory.IFactory;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Mapping of single values to and from the database.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface ITypeMapping
{
  /**
   * @return The feature which is associated with this mapping.
   */
  public EStructuralFeature getFeature();

  /**
   * @return The db field which is associated with this mapping.
   */
  public IDBField getField();

  /**
   * @return The db type which is associated with this mapping.
   * @since 3.0
   */
  public DBType getDBType();

  /**
   * @since 4.0
   */
  public void setMappingStrategy(IMappingStrategy mappingStrategy);

  /**
   * @since 4.0
   */
  public void setFeature(EStructuralFeature feature);

  /**
   * @since 4.0
   */
  public void setDBType(DBType dbType);

  /**
   * Creates the DBField and adds it to the given table. The name of the DBField is derived from the feature.
   * 
   * @param table
   *          the table to add this field to.
   */
  public void createDBField(IDBTable table);

  /**
   * Creates the DBField and adds it to the given table. The name of the DBField is explicitly determined by the
   * corresponding parameter.
   * 
   * @param table
   *          the table to add this field to.
   * @param fieldName
   *          the name for the DBField.
   */
  public void createDBField(IDBTable table, String fieldName);

  /**
   * Sets the DBField. The name of the DBField is explicitly determined by the corresponding parameter.
   * 
   * @param table
   *          the table to add this field to.
   * @param fieldName
   *          the name for the DBField.
   * @since 3.0
   */
  public void setDBField(IDBTable table, String fieldName);

  /**
   * Set the given value to the JDBC {@link PreparedStatement} using an appropriate <code>setXxx</code> method.
   * 
   * @param stmt
   *          the prepared statement to set the value
   * @param index
   *          the index to use for the <code>setXxx</code> method.
   * @param value
   *          the value to set.
   * @throws SQLException
   *           if the <code>setXxx</code> throws it.
   */
  public void setValue(PreparedStatement stmt, int index, Object value) throws SQLException;

  /**
   * Set the feature's default value to the JDBC {@link PreparedStatement} using an appropriate <code>setXxx</code>
   * method.
   * 
   * @param stmt
   *          the prepared statement to set the value
   * @param index
   *          the index to use for the <code>setXxx</code> method.
   * @throws SQLException
   *           if the <code>setXxx</code> throws it.
   * @since 3.0
   */
  public void setDefaultValue(PreparedStatement stmt, int index) throws SQLException;

  /**
   * Set a value of the given revision to the JDBC {@link PreparedStatement} using an appropriate <code>setXxx</code>
   * method. The feature from which the value is taken is determined by {@link #getFeature()}.
   * 
   * @param stmt
   *          the prepared statement to set the value
   * @param index
   *          the index to use for the <code>setXxx</code> method.
   * @param value
   *          the revision to get the value to set from.
   * @throws SQLException
   *           if the <code>setXxx</code> throws it.
   */
  public void setValueFromRevision(PreparedStatement stmt, int index, InternalCDORevision value) throws SQLException;

  /**
   * Read the value from a {@link ResultSet} and convert it from the DB to the CDO representation. The resultSet field
   * to read from is determined automatically by the internal {@link #getField()} name.
   * 
   * @param resultSet
   *          the result set to read from
   * @return the read value
   * @throws SQLException
   *           if reading the value throws an SQLException
   * @since 3.0
   */
  public Object readValue(ResultSet resultSet) throws SQLException;

  /**
   * Read a value from a {@link ResultSet}, convert it from the DB to the CDO representation and set it to the feature
   * of the revision. The feature is determined by getFeature() The resultSet field to read from is determined
   * automatically by the internal {@link #getField()} name.
   * 
   * @param resultSet
   *          the result set to read from
   * @param revision
   *          the revision to which the value should be set.
   * @throws SQLException
   *           if reading the value throws an SQLException
   * @since 3.0
   */
  public void readValueToRevision(ResultSet resultSet, InternalCDORevision revision) throws SQLException;

  /**
   * A descriptor which describes one type mapping class. The descriptor is encoded in the factoryType which is used as
   * a string description for the extension point mechanism. Translations and instantiations can be done using the
   * methods in {@link TypeMappingUtil}.
   * 
   * @author Stefan Winkler
   * @since 4.0
   */
  public interface Descriptor
  {
    /**
     * The factoryType of the factory which can create the type mapping
     */
    public String getFactoryType();

    /**
     * The ID of the described type mapping.
     */
    public String getID();

    /**
     * The source (i.e., model) type that can be mapped by the type mapping.
     */
    public EClassifier getEClassifier();

    /**
     * The target (i.e., db) type that can be mapped by the type mapping.
     */
    public DBType getDBType();

  }

  /**
   * A global (singleton) registry which collects all available type mappings which are either available in the CDO
   * core, as declared extensions, or registered manually.
   * 
   * @author Stefan Winkler
   * @since 4.0
   */
  public interface Registry
  {
    /**
     * The one global (singleton) registry instance.
     */
    public static Registry INSTANCE = new TypeMappingRegistry();

    /**
     * Register a type mapping by descriptor.
     */
    public void registerTypeMapping(ITypeMapping.Descriptor descriptor);

    /**
     * Provides a list of all DBTypes for which type mappings exist in the registry. This is used in feature map tables
     * to create columns for all of these types.
     */
    public Collection<DBType> getDefaultFeatureMapDBTypes();
  }

  /**
   * A provider for type mapping information. This provider is used by the {@link TypeMappingRegistry} to create an
   * {@link ITypeMapping} instance suitable for a given feature and DB field. Usually, one factory is responsible for
   * one type mapping.
   * 
   * @author Stefan Winkler
   * @since 4.0
   */
  public interface Provider
  {
    /**
     * The one global (singleton) provider instance.
     */
    public static Provider INSTANCE = (Provider)Registry.INSTANCE;

    /**
     * Create an {@link ITypeMapping} implementation.
     * 
     * @param mappingStrategy
     *          the mapping strategy
     * @param feature
     *          the feature the new type mapping shall be responsible for
     * @return the newly created {@link ITypeMapping} instance
     */
    public ITypeMapping createTypeMapping(IMappingStrategy mappingStrategy, EStructuralFeature feature);
  }

  /**
   * A factory for typeMappings. This is a regular Net4j factory registered by the respective extension point. It
   * enhances the regular factory using a descriptor which is translated from and to the factoryType by the methods in
   * {@link TypeMappingUtil}.
   * 
   * @author Stefan Winkler
   * @since 4.0
   */
  public interface Factory extends IFactory
  {
    /**
     * The Net4j factory product group for type mappings
     */
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.typeMappings";

    /**
     * Return the descriptor of the kind of type mapping created by this factory.
     */
    public ITypeMapping.Descriptor getDescriptor();
  }
}
