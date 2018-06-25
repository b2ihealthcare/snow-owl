/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.importer.rf2.refset.AbstractSnomedOWLExpressionRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedAssociationRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedAttributeValueRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedComplexMapTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedConcreteDataTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedDescriptionTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedLanguageRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedMRCMAttributeDomainRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedMRCMAttributeRangeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedMRCMDomainRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedMRCMModuleScopeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedQueryRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedSimpleMapTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedSimpleTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedConceptImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedDescriptionImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedRelationshipImporter;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Stores index and column names for setting up and tearing down database indexes.
 */
public class IndexConfiguration {

	public static final Map<ComponentImportType, List<IndexConfiguration>> createConfigurationMap() {
		return ImmutableMap.<ComponentImportType, List<IndexConfiguration>>builder()
			.put(ComponentImportType.ASSOCIATION_TYPE_REFSET, SnomedAssociationRefSetImporter.INDEXES)
			.put(ComponentImportType.ATTRIBUTE_VALUE_REFSET, SnomedAttributeValueRefSetImporter.INDEXES)
			.put(ComponentImportType.COMPLEX_MAP_TYPE_REFSET, SnomedComplexMapTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.CONCEPT, SnomedConceptImporter.INDEXES)
			.put(ComponentImportType.CONCRETE_DOMAIN_REFSET, SnomedConcreteDataTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.DESCRIPTION, SnomedDescriptionImporter.INDEXES)
			.put(ComponentImportType.DESCRIPTION_TYPE_REFSET, SnomedDescriptionTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.LANGUAGE_TYPE_REFSET, SnomedLanguageRefSetImporter.INDEXES)
			.put(ComponentImportType.QUERY_TYPE_REFSET, SnomedQueryRefSetImporter.INDEXES)
			.put(ComponentImportType.RELATIONSHIP, SnomedRelationshipImporter.INDEXES)
			.put(ComponentImportType.SIMPLE_MAP_TYPE_REFSET, SnomedSimpleMapTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.SIMPLE_TYPE_REFSET, SnomedSimpleTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.OWL_EXPRESSION_REFSET, AbstractSnomedOWLExpressionRefSetImporter.INDEXES)
			.put(ComponentImportType.MRCM_DOMAIN_REFSET, SnomedMRCMDomainRefSetImporter.INDEXES)
			.put(ComponentImportType.MRCM_ATTRIBUTE_DOMAIN_REFSET, SnomedMRCMAttributeDomainRefSetImporter.INDEXES)
			.put(ComponentImportType.MRCM_ATTRIBUTE_RANGE_REFSET, SnomedMRCMAttributeRangeRefSetImporter.INDEXES)
			.put(ComponentImportType.MRCM_MODULE_SCOPE_REFSET, SnomedMRCMModuleScopeRefSetImporter.INDEXES)
			.build();
	}
	
	/**
	 * @param logger
	 * @param connection
	 * @param monitor 
	 * @return
	 */
	public static int dropAll(final Logger logger, final Connection connection, IProgressMonitor monitor) {
		
		final Map<ComponentImportType, List<IndexConfiguration>> configurationMap = createConfigurationMap();
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Dropping SNOMED CT database indexes", configurationMap.size());
		
		int dropCount = 0;
		
		for (ComponentImportType type : configurationMap.keySet()) {
			final SubMonitor typeMonitor = subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE);
			typeMonitor.beginTask("Processing " + StringUtils.capitalizeFirstLetter(type.getDisplayName()) + "s", configurationMap.get(type).size());
			
			for (IndexConfiguration configuration : configurationMap.get(type)) {
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
		
		final Map<ComponentImportType, List<IndexConfiguration>> configurationMap = createConfigurationMap();
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Creating SNOMED CT database indexes", configurationMap.size());
		
		int createCount = 0;
		
		for (ComponentImportType type : configurationMap.keySet()) {
			final SubMonitor typeMonitor = subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE);
			typeMonitor.beginTask("Processing " + StringUtils.capitalizeFirstLetter(type.getDisplayName()) + "s", configurationMap.get(type).size());
			
			for (IndexConfiguration configuration : configurationMap.get(type)) {
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
