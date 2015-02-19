/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.DBBrowserPage;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.SmartPreparedStatementCache;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditMappingStrategyWithRanges;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalBranchingMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalBranchingMappingStrategyWithRanges;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditMappingStrategy;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Various static methods that may help in setting up and dealing with {@link IDBStore DB stores}.
 *
 * @author Eike Stepper
 */
public final class CDODBUtil
{
  /**
   * @since 2.0
   */
  public static final int DEFAULT_STATEMENT_CACHE_CAPACITY = 200;

  /**
   * @since 2.0
   */
  public static final String EXT_POINT_MAPPING_STRATEGIES = "mappingStrategies"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String PROP_WITH_RANGES = "withRanges";

  /**
   * @since 4.1
   */
  public static final String PROP_COPY_ON_BRANCH = "copyOnBranch";

  /**
   * @since 4.1
   */
  public static final String PROP_ZEROBASED_INDEX = "forceZeroBasedIndex";

  private CDODBUtil()
  {
  }

  /**
   * @since 4.0
   */
  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new DBBrowserPage.Factory());
  }

  /**
   * @since 2.0
   */
  public static IDBStore createStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter,
      IDBConnectionProvider dbConnectionProvider)
  {
    DBStore store = new DBStore();
    store.setMappingStrategy(mappingStrategy);
    store.setDBAdapter(dbAdapter);
    store.setDbConnectionProvider(dbConnectionProvider);
    return store;
  }

  /**
   * @since 2.0
   */
  public static IMappingStrategy createHorizontalMappingStrategy(boolean auditing)
  {
    return createHorizontalMappingStrategy(auditing, false, false);
  }

  /**
   * @since 3.0
   */
  public static IMappingStrategy createHorizontalMappingStrategy(boolean auditing, boolean branching)
  {
    return createHorizontalMappingStrategy(auditing, branching, false);
  }

  /**
   * @since 4.1
   */
  public static IMappingStrategy createHorizontalMappingStrategy(boolean auditing, boolean branching, boolean withRanges)
  {
    if (branching)
    {
      if (auditing)
      {
        if (withRanges)
        {
          return new HorizontalBranchingMappingStrategyWithRanges();
        }

        return new HorizontalBranchingMappingStrategy();
      }

      throw new IllegalArgumentException("Misconfiguration: Branching requires Auditing!");
    }

    if (auditing)
    {
      if (withRanges)
      {
        return new HorizontalAuditMappingStrategyWithRanges();
      }

      return new HorizontalAuditMappingStrategy();
    }

    return new HorizontalNonAuditMappingStrategy();
  }

  /**
   * Creates a horizontal {@link IMappingStrategy mapping strategy} that supports all valid combinations of auditing and
   * branching.
   *
   * @since 4.1
   */
  public static IMappingStrategy createHorizontalMappingStrategy()
  {
    return new HorizontalMappingStrategy();
  }

  /**
   * Can only be used when Eclipse is running. In standalone scenarios create the mapping strategy instance by directly
   * calling the constructor of the mapping strategy class.
   *
   * @see #createHorizontalMappingStrategy(boolean)
   * @see #createHorizontalMappingStrategy(boolean, boolean)
   * @since 2.0
   */
  public static IMappingStrategy createMappingStrategy(String type)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT_MAPPING_STRATEGIES);
    for (final IConfigurationElement element : elements)
    {
      if ("mappingStrategy".equals(element.getName())) //$NON-NLS-1$
      {
        String typeAttr = element.getAttribute("type"); //$NON-NLS-1$
        if (ObjectUtil.equals(typeAttr, type))
        {
          try
          {
            return (IMappingStrategy)element.createExecutableExtension("class"); //$NON-NLS-1$
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }

    return null;
  }

  /**
   * Creates a prepared statement cache with the {@link CDODBUtil#DEFAULT_STATEMENT_CACHE_CAPACITY default capacity}.
   *
   * @since 2.0
   * @see CDODBUtil#createStatementCache(int)
   */
  public static IPreparedStatementCache createStatementCache()
  {
    return createStatementCache(DEFAULT_STATEMENT_CACHE_CAPACITY);
  }

  /**
   * Creates a prepared statement cache with the given capacity.
   *
   * @since 2.0
   */
  public static IPreparedStatementCache createStatementCache(int capacity)
  {
    return new SmartPreparedStatementCache(capacity);
  }
}
