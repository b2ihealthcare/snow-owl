/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.importer.rf2.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import com.b2international.commons.StringUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Stores index and column names for setting up and tearing down database indexes.
 *
 */
public class IndexConfiguration {
	
	/**
	 * 
	 * @param logger
	 * @param connection
	 * @param monitor 
	 * @return
	 */
	public static int dropAll(final Logger logger, final Connection connection, IProgressMonitor monitor) {
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Dropping SNOMED CT database indexes", IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.size());
		
		int dropCount = 0;
		
		for (ComponentImportType type : IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.keySet()) {
			final SubMonitor typeMonitor = subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE);
			typeMonitor.beginTask("Processing " + StringUtils.capitalizeFirstLetter(type.getDisplayName()) + "s", IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.get(type).size());
			
			for (IndexConfiguration configuration : IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.get(type)) {
				typeMonitor.subTask(configuration.indexName);
				if (configuration.drop(logger, connection)) {
					++dropCount;
				}
			}
		}
		
		return dropCount;
	}
	
	/**
	 * 
	 * @param logger
	 * @param connection
	 * @param monitor 
	 * @return
	 */
	public static int createAll(final Logger logger, final Connection connection, IProgressMonitor monitor) {
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Creating SNOMED CT database indexes", IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.size());
		
		int createCount = 0;
		
		for (ComponentImportType type : IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.keySet()) {
			final SubMonitor typeMonitor = subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE);
			typeMonitor.beginTask("Processing " + StringUtils.capitalizeFirstLetter(type.getDisplayName()) + "s", IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.get(type).size());
			
			for (IndexConfiguration configuration : IndexConfigurationConstants.CONFIGURATIONS_BY_TYPE.get(type)) {
				typeMonitor.subTask(configuration.indexName);
				if (configuration.create(logger, connection)) {
					++createCount;
				}
			}
		}
		
		return createCount;
	}
	
	private final String indexName;
	private final String tableName;
	private final List<String> columnNames;
	
	public IndexConfiguration(final String indexName, final String tableName, final String firstColumnName, final String... restColumnNames) {
		this.indexName = indexName;
		this.tableName = tableName;
		this.columnNames = Lists.asList(firstColumnName, restColumnNames);
	}

	public boolean create(final Logger logger, final Connection connection) {
		// Values refer to tables and columns, and as such can't be substituted via the normal parameter supplying mechanism 
		final String formattedQuery = MessageFormat.format("CREATE INDEX {0} ON {1} ({2})", indexName, tableName, Joiner.on(", ").join(columnNames));
		return executeQuery(logger, connection, formattedQuery);
	}

	public boolean drop(final Logger logger, final Connection connection) {
		final String formattedQuery = MessageFormat.format("DROP INDEX {0} /*! ON {1} */", indexName, tableName);
		return executeQuery(logger, connection, formattedQuery);
	}

	private boolean executeQuery(final Logger logger, final Connection connection, final String formattedQuery) {
		PreparedStatement query = null;
		try {	
			query = connection.prepareStatement(formattedQuery);
			query.executeUpdate();
			return true;
		} catch (final SQLException e) {
			logger.debug("Couldn't create or drop index {} for table {}.", indexName, tableName, e);
			return false;
		} finally {
			if (null != query) {
				try {
					query.close();
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
