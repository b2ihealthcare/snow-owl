/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.impl;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.snomed.common.ContentSubType.DELTA;
import static com.b2international.snowowl.snomed.common.ContentSubType.FULL;
import static com.b2international.snowowl.snomed.common.ContentSubType.SNAPSHOT;
import static com.b2international.snowowl.snomed.exporter.model.SnomedRf2ExportModel.createExportModelWithAllRefSets;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.ISnomedExportService;
import com.b2international.snowowl.snomed.api.exception.SnomedExportException;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.core.domain.ISnomedExportConfiguration;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.exporter.model.SnomedRf2ExportModel;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

/**
 * {@link ISnomedExportService export service} implementation for the SNOMED CT ontology.
 */
public class SnomedExportService implements ISnomedExportService {

	@Resource
	private IEventBus bus;
	
	private static final Map<Rf2ReleaseType, ContentSubType> TYPE_MAPPING = ImmutableMap.of(
			Rf2ReleaseType.DELTA, DELTA, 
			Rf2ReleaseType.SNAPSHOT, SNAPSHOT, 
			Rf2ReleaseType.FULL, FULL
		);
	
	@Override
	public String resolveNamespaceId(Branch branch) {
		
		String branchMetaShortname = getEffectiveBranchMetadataValue(branch, "shortname");
		String branchMetaDefaultNamespace = getEffectiveBranchMetadataValue(branch, "defaultNamespace");
		
		if (!Strings.isNullOrEmpty(branchMetaShortname) && !Strings.isNullOrEmpty(branchMetaDefaultNamespace)) {
			return String.format("%s%s", branchMetaShortname.toUpperCase(), branchMetaDefaultNamespace);
		}
		
		return SnomedIdentifiers.INT_NAMESPACE;
	}
	
	// FIXME This should not be here, see IHTSDO/com.b2international.snowowl.snomed.core.domain.BranchMetadataResolver
	private String getEffectiveBranchMetadataValue(Branch branch, String metadataKey) {
		final String metadataValue = branch.metadata().getString(metadataKey);
		if (metadataValue != null) {
			return metadataValue;
		} else {
			final Branch parent = branch.parent();
			if (parent != null && branch != parent) {
				return getEffectiveBranchMetadataValue(parent, metadataKey);
			}
		}
		return null;
	}
	
	@Override
	public File export(final ISnomedExportConfiguration configuration) {
		return tryExport(convertConfiguration(checkNotNull(configuration, "Configuration was missing for the export operation.")));
	}

	private File tryExport(final SnomedRf2ExportModel model) {
		try {
			return doExport(model);
		} catch (final Exception e) {
			return throwExportException(isEmpty(e.getMessage())	? "Error occurred while exporting SNOMED CT." : e.getMessage());
		}
	}

	private File doExport(final SnomedRf2ExportModel model) throws Exception {
		return getDelegateService().export(model, new NullProgressMonitor());
	}

	private com.b2international.snowowl.snomed.exporter.service.SnomedExportService getDelegateService() {
		return new com.b2international.snowowl.snomed.exporter.service.SnomedExportService();
	}

	private SnomedRf2ExportModel convertConfiguration(final ISnomedExportConfiguration configuration) {
		
		final ContentSubType contentSubType = convertType(configuration.getRf2ReleaseType());
		
		Branch branch = RepositoryRequests.branching()
			.prepareGet(configuration.getBranchPath())
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(bus)
			.getSync();
				
		final SnomedRf2ExportModel model = createExportModelWithAllRefSets(contentSubType, branch, configuration.getNamespaceId());

		if (configuration.getModuleIds().isEmpty()) {
			model.getModulesToExport().addAll(SnomedRequests.prepareSearchConcept()
					.all()
					.filterByActive(true)
					.filterByAncestor(Concepts.MODULE_ROOT)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.path())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then(new Function<SnomedConcepts, Collection<String>>() {
						@Override
						public Collection<String> apply(SnomedConcepts input) {
							return FluentIterable.from(input).transform(IComponent.ID_FUNCTION).toSet();
						}
					})
					.getSync());
		} else {
			model.getModulesToExport().addAll(configuration.getModuleIds());
		}
		
		model.setStartEffectiveTime(configuration.getStartEffectiveTime());
		model.setEndEffectiveTime(configuration.getEndEffectiveTime());
		model.setIncludeUnpublised(configuration.isIncludeUnpublised());
		
		final String transientEffectiveTime = configuration.getTransientEffectiveTime();
		
		if (StringUtils.isEmpty(transientEffectiveTime)) {
			model.setUnsetEffectiveTimeLabel("");
		} else if ("NOW".equals(transientEffectiveTime)) {
			model.setUnsetEffectiveTimeLabel(EffectiveTimes.format(Dates.todayGmt(), DateFormats.SHORT));
		} else {
			
			try {
				EffectiveTimes.parse(transientEffectiveTime, DateFormats.SHORT);
			} catch (SnowowlRuntimeException e) {
				throw new BadRequestException("Transient effective time '%s' is not in the expected date format.", transientEffectiveTime);
			}
			
			model.setUnsetEffectiveTimeLabel(transientEffectiveTime);
		}
		
		model.setCodeSystemShortName(configuration.getCodeSystemShortName());
		model.setExtensionOnly(configuration.isExtensionOnly());

		return model; 
	}

	private ContentSubType convertType(final Rf2ReleaseType typeToConvert) {
		return checkNotNull(TYPE_MAPPING.get(typeToConvert), "Unknown or unexpected RF2 release type of: " + typeToConvert + ".");
	}

	private <T> T checkNotNull(final T arg, final String message) {
		return null == arg ? this.<T>throwExportException(message) : arg;
	}

	private <T> T throwExportException(final String message) {
		throw new SnomedExportException(nullToEmpty(message));
	}
}
