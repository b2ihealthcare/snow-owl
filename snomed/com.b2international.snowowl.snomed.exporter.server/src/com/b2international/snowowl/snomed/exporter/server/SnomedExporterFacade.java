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
package com.b2international.snowowl.snomed.exporter.server;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.exporter.server.core.SnomedRf1ConceptExporter;
import com.b2international.snowowl.snomed.exporter.server.core.SnomedRf1DescriptionExporter;
import com.b2international.snowowl.snomed.exporter.server.core.SnomedRf1RelationshipExporter;
import com.b2international.snowowl.snomed.exporter.server.refset.SnomedRefSetExporterFactory;
import com.b2international.snowowl.snomed.exporter.server.sandbox.NoopExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedConceptExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedDescriptionExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedRelationshipExporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedStatedRelationshipExporter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Facade for the SNOMED&nbsp;CT terminology publication process.
 */
public class SnomedExporterFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedExporterFacade.class);
	
	private final boolean includeRf1;
	private final boolean includeExtendedDescriptionTypes;
	private final Set<SnomedMapSetSetting> settings;
	private final Set<String> modulesToExport;
	private String countryAndNamespaceId;

	// These fields are used for export activity logging
	private final org.slf4j.Logger activityLogger;
	private final String userId;
	private final IBranchPath branchPath;

	/**
	 * Creates a facade for the SNOMED&nbsp;CT publication process.
	 * @param includeRf1 flag indicating whether the RF1 format should be created or not.
	 * @param includeExtendedDescriptionTypes flag indicating whether the extended description type for the RF1 format is required or not.
	 * @param module the module number to export
	 * @param settings settings used for map set export.
	 * @param deltaExport {@code true} if the export type is delta 
	 * @param countryAndNamespace the country code and the SNOMED&nbsp;CT namespace configured at the client side, if any, else 'INT'. 
	 */
	public SnomedExporterFacade(final String userId,
			final IBranchPath branchPath,
			final org.slf4j.Logger activityLogger,
			final boolean includeRf1, 
			final boolean includeExtendedDescriptionTypes, 
			final Set<SnomedMapSetSetting> settings, 
			final Set<String> modulesToExport, 
			final boolean deltaExport, 
			final Date deltaExportStartEffectiveTime, 
			final Date deltaExportEndEffectiveTime, 
			final String countryAndNamespace) {
		
		this.userId = userId;
		this.branchPath = branchPath;
		this.activityLogger = activityLogger;
		
		this.includeRf1 = includeRf1;
		this.includeExtendedDescriptionTypes = includeExtendedDescriptionTypes;
		this.settings = settings;
		this.modulesToExport = modulesToExport;
		this.countryAndNamespaceId = countryAndNamespace;
	}
	
	public void executeCoreExport(final String workingDirectory, final SnomedExportConfiguration configuration, final OMMonitor monitor) throws IOException {

		if (monitor.isCanceled()) {
			return;
		}
		
		logActivity("Publishing SNOMED CT concepts into RF2 format.");
		
		Async async = null;
		
		try {
			async = monitor.forkAsync(2);
			SnomedConceptExporter conceptExporter = new SnomedConceptExporter(configuration);
			new SnomedExportExecutor(conceptExporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
		} finally {
			if (async != null) {
				async.stop();
				async = null;
			}
		}
		
		if (monitor.isCanceled()) {
			return;
		}
		
		logActivity("Publishing SNOMED CT description into RF2 format.");
		
		try {
			async = monitor.forkAsync(2);
			SnomedDescriptionExporter descriptionExporter = new SnomedDescriptionExporter(configuration);
			new SnomedExportExecutor(descriptionExporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
		} finally {
			if (async != null) {
				async.stop();
				async = null;
			}
		}
		
		if (monitor.isCanceled()) {
			return;
		}
		
		logActivity("Publishing SNOMED CT non-stated relationships into RF2 format.");
		
		try {
			async = monitor.forkAsync(2);
			SnomedExporter relationshipExporter = new SnomedRelationshipExporter(configuration);
			new SnomedExportExecutor(relationshipExporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
		} finally {
			if (async != null) {
				async.stop();
				async = null;
			}
		}
		
		if (monitor.isCanceled()) {
			return;
		}
		
		logActivity("Publishing SNOMED CT stated relationships into RF2 format.");
		
		try {
			async = monitor.forkAsync(2);
			SnomedExporter statedRelationshipExporter = new SnomedStatedRelationshipExporter(configuration);
			new SnomedExportExecutor(statedRelationshipExporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
		} finally {
			if (async != null) {
				async.stop();
				async = null;
			}
		}

		if (monitor.isCanceled()) {
			return;
		}
		
		if (includeRf1) {
			
			final Id2Rf1PropertyMapper mapper = new Id2Rf1PropertyMapper();
			
			logActivity("Publishing SNOMED CT concepts into RF1 format.");
			
			try {
				async = monitor.forkAsync(2);
				SnomedRf1ConceptExporter rf1ConceptExporter = new SnomedRf1ConceptExporter(configuration, mapper);
				new SnomedExportExecutor(rf1ConceptExporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
			} finally {
				if (async != null) {
					async.stop();
					async = null;
				}
			}
			
			if (monitor.isCanceled()) {
				return;
			}
			
			logActivity("Publishing SNOMED CT descriptions into RF1 format.");
			
			try {
				async = monitor.forkAsync(2);
				SnomedRf1DescriptionExporter rf1DescriptionExporter = new SnomedRf1DescriptionExporter(configuration, mapper, includeExtendedDescriptionTypes);
				final SnomedExportExecutor exportExecutor = new SnomedExportExecutor(rf1DescriptionExporter, workingDirectory, modulesToExport, countryAndNamespaceId);
				exportExecutor.execute();
				
				if (includeExtendedDescriptionTypes) {
					exportExecutor.writeExtendedDescriptionTypeExplanation();
				}
			} finally {
				if (async != null) {
					async.stop();
					async = null;
				}
			}
			
			if (monitor.isCanceled()) {
				return;
			}
			
			logActivity("Publishing SNOMED CT relationships into RF1 format.");
			
			try {
				async = monitor.forkAsync(2);
				SnomedRf1RelationshipExporter rf1RelationshipExporter = new SnomedRf1RelationshipExporter(configuration, mapper);
				new SnomedExportExecutor(rf1RelationshipExporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
			} finally {
				if (async != null) {
					async.stop();
					async = null;
				}
			}
			
			if (monitor.isCanceled()) {
				return;
			}
		}
	}

	public void executeRefSetExport(final String workingDirectory, final SnomedExportConfiguration configuration, final String refSetId, 
			final OMMonitor monitor) throws IOException {
	
		Async async = null;
		
		try {
			async = monitor.forkAsync();
			doRefSetExport(workingDirectory, configuration, refSetId);
		} finally {
			if (async != null) {
				async.stop();
				async = null;
			}
		}
	}
	
	private void doRefSetExport(String workingDirectory, SnomedExportConfiguration configuration, String refSetId) throws IOException {

		final SnomedExporter refSetExporter = SnomedRefSetExporterFactory.getRefSetExporter(refSetId, configuration);
		
		if (NoopExporter.INSTANCE == refSetExporter) {
			return;
		}
		
		logActivity("Publishing SNOMED CT reference set into RF2 format. Reference set identifier concept ID: " + refSetId);
		new SnomedExportExecutor(refSetExporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();

		//RF1 export
		if (includeRf1) {
			//RF1 subset exporter.
			boolean alreadyLogged = false;
			for (final SnomedExporter exporter : SnomedRefSetExporterFactory.getSubsetExporter(refSetId, configuration)) {
				if (NoopExporter.INSTANCE != exporter) {
					if (!alreadyLogged) {
						logActivity("Publishing SNOMED CT reference set into RF1 format. Reference set identifier concept ID: " + refSetId);
						alreadyLogged = true;
					}
					new SnomedExportExecutor(exporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
				}
			}
			//RF1 map set exporter.
			final SnomedMapSetSetting mapsetSetting = getSettingForRefSet(refSetId);
			if (null != mapsetSetting) {
				alreadyLogged = false;
				for (final SnomedExporter exporter : SnomedRefSetExporterFactory.getCrossMapExporter(refSetId, configuration, mapsetSetting)) {
					if (NoopExporter.INSTANCE != exporter) {
						if (!alreadyLogged) {
							logActivity("Publishing SNOMED CT reference set into RF1 format. Reference set identifier concept ID: " + refSetId);
							alreadyLogged = true;
						}
						new SnomedExportExecutor(exporter, workingDirectory, modulesToExport, countryAndNamespaceId).execute();
					}
				}
			}
		}
	}

	private void logActivity(final String message) {
		LOGGER.info(message);
		LogUtils.logExportActivity(activityLogger, userId, branchPath, message);
	}

	/*returns with the map set setting for the specified reference set identifier concept ID*/
	private SnomedMapSetSetting getSettingForRefSet(final String refSetId) {
		return Iterables.getOnlyElement(Iterables.filter(settings, new Predicate<SnomedMapSetSetting>() {
			@Override public boolean apply(final SnomedMapSetSetting setting) {
				return setting.getRefSetId().equals(refSetId);
			}
		}), null);
	}
	
}