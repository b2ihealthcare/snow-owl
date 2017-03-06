/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.snomed.common.ContentSubType.DELTA;
import static com.b2international.snowowl.snomed.common.ContentSubType.FULL;
import static com.b2international.snowowl.snomed.common.ContentSubType.SNAPSHOT;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

/**
 * @since 5.7
 */
final class SnomedRf2ExportRequest implements Request<BranchContext, UUID> {

	@NotEmpty
	private String codeSystem;
	private boolean includeUnpublished;
	private Rf2ReleaseType releaseType;
	private boolean extensionOnly;
	private String startEffectiveTime;
	private String endEffectiveTime;
	private Collection<String> modules;
	private String transientEffectiveTime;
	private String namespace;

	SnomedRf2ExportRequest() {}
	
	void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}

	void setIncludeUnpublished(boolean includeUnpublished) {
		this.includeUnpublished = includeUnpublished;
	}
	
	void setReleaseType(Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
	}
	
	void setExtensionOnly(boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
	}
	
	void setStartEffectiveTime(String startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
	}

	void setEndEffectiveTime(String endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
	}

	void setModules(Collection<String> modules) {
		this.modules = modules;
	}

	void setTransientEffectiveTime(String transientEffectiveTime) {
		this.transientEffectiveTime = transientEffectiveTime;
	}

	void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@Override
	public UUID execute(BranchContext context) {
		try {
			return doExport(toExportModel(context));
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

	private SnomedRf2ExportModel toExportModel(BranchContext context) {
		
		final ContentSubType contentSubType = convertType(releaseType);
		
		Branch branch = RepositoryRequests.branching()
			.prepareGet(context.branch().path())
			.build()
			.execute(context);
				
		final SnomedRf2ExportModel model = createExportModelWithAllRefSets(contentSubType, branch, namespace);

		if (modules.isEmpty()) {
			final SnomedConcepts allModules = SnomedRequests.prepareSearchConcept()
					.all()
					.filterByActive(true)
					.filterByAncestor(Concepts.MODULE_ROOT)
					.build()
					.execute(context);
			Set<String> moduleIds = FluentIterable.from(allModules).transform(IComponent.ID_FUNCTION).toSet();
			model.getModulesToExport().addAll(allModules);
		} else {
			model.getModulesToExport().addAll(modules);
		}
		
		model.setStartEffectiveTime(startEffectiveTime);
		model.setEndEffectiveTime(endEffectiveTime);
		model.setIncludeUnpublised(includeUnpublished);
		
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
		
		model.setCodeSystemShortName(codeSystem);
		model.setExtensionOnly(extensionOnly);

		return model; 
	}
	
	private static final Map<Rf2ReleaseType, ContentSubType> TYPE_MAPPING = ImmutableMap.of(
			Rf2ReleaseType.DELTA, DELTA, 
			Rf2ReleaseType.SNAPSHOT, SNAPSHOT, 
			Rf2ReleaseType.FULL, FULL
		);
	
	private ContentSubType convertType(final Rf2ReleaseType typeToConvert) {
		return checkNotNull(TYPE_MAPPING.get(typeToConvert), "Unknown or unexpected RF2 release type of: " + typeToConvert + ".");
	}

	private <T> T checkNotNull(final T arg, final String message) {
		return null == arg ? this.<T>throwExportException(message) : arg;
	}

	private <T> T throwExportException(final String message) {
		throw new RuntimeException(nullToEmpty(message));
	}

}
