/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Caspar De Groot - maintenance  
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;

import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class DBStoreFactory implements IStoreFactory
{
  public DBStoreFactory()
  {
  }

  public String getStoreType()
  {
    return DBStore.TYPE;
  }

  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    IMappingStrategy mappingStrategy = getMappingStrategy(repositoryName, repositoryProperties, storeConfig);
    IDBAdapter dbAdapter = getDBAdapter(storeConfig);
    DataSource dataSource = getDataSource(storeConfig);
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);

    DBStore store = new DBStore();
    store.setMappingStrategy(mappingStrategy);
    store.setDBAdapter(dbAdapter);
    store.setDbConnectionProvider(connectionProvider);

    Map<String, String> storeProperties = RepositoryConfigurator.getProperties(storeConfig, 1);
    store.setProperties(storeProperties);

    return store;
  }

  private IMappingStrategy getMappingStrategy(String repositoryName, Map<String, String> repositoryProperties,
      Element storeConfig)
  {
    NodeList mappingStrategyConfigs = storeConfig.getElementsByTagName("mappingStrategy"); //$NON-NLS-1$
    if (mappingStrategyConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one mapping strategy must be configured for DB store"); //$NON-NLS-1$
    }

    Element mappingStrategyConfig = (Element)mappingStrategyConfigs.item(0);
    String mappingStrategyType = mappingStrategyConfig.getAttribute("type"); //$NON-NLS-1$
    IMappingStrategy mappingStrategy = CDODBUtil.createMappingStrategy(mappingStrategyType);
    if (mappingStrategy == null)
    {
      throw new IllegalArgumentException("Unknown mapping strategy: " + mappingStrategyType); //$NON-NLS-1$
    }

    Map<String, String> properties = RepositoryConfigurator.getProperties(mappingStrategyConfig, 1);
    properties.put("repositoryName", repositoryName);
    properties.putAll(repositoryProperties);
    mappingStrategy.setProperties(properties);

    return mappingStrategy;
  }

  private IDBAdapter getDBAdapter(Element storeConfig)
  {
    NodeList dbAdapterConfigs = storeConfig.getElementsByTagName("dbAdapter"); //$NON-NLS-1$
    if (dbAdapterConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one dbAdapter must be configured for DB store"); //$NON-NLS-1$
    }

    Element dbAdapterConfig = (Element)dbAdapterConfigs.item(0);
    String dbAdapterName = dbAdapterConfig.getAttribute("name"); //$NON-NLS-1$
    IDBAdapter dbAdapter = DBUtil.getDBAdapter(dbAdapterName);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("Unknown DB adapter: " + dbAdapterName); //$NON-NLS-1$
    }

    return dbAdapter;
  }

  private DataSource getDataSource(Element storeConfig)
  {
    NodeList dataSourceConfigs = storeConfig.getElementsByTagName("dataSource"); //$NON-NLS-1$
    if (dataSourceConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one dataSource must be configured for DB store"); //$NON-NLS-1$
    }

    Properties properties = new Properties();
    Element dataSourceConfig = (Element)dataSourceConfigs.item(0);
    NamedNodeMap attributes = dataSourceConfig.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++)
    {
      Attr attribute = (Attr)attributes.item(i);
      properties.put(attribute.getName(), attribute.getValue());
    }

    return DBUtil.createDataSource(properties);
  }
}
