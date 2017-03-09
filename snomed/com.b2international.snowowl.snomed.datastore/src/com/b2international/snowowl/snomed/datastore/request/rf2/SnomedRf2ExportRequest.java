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

import static com.b2international.snowowl.snomed.common.ContentSubType.DELTA;
import static com.b2international.snowowl.snomed.common.ContentSubType.FULL;
import static com.b2international.snowowl.snomed.common.ContentSubType.SNAPSHOT;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedClientProtocol;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportClientRequest;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRf2ExportModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

/**
 * @since 5.7
 */
final class SnomedRf2ExportRequest implements Request<BranchContext, UUID> {

	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	private String codeSystem;
	private boolean includeUnpublished;
	@NotNull
	private Rf2ReleaseType releaseType;
	private boolean extensionOnly;
	private String startEffectiveTime;
	private String endEffectiveTime;
	private Collection<String> modules;
	private String transientEffectiveTime;
	private String namespace;
	private Collection<String> refSets;

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
	
	public void setRefSets(Collection<String> refSets) {
		this.refSets = Collections3.toImmutableSet(refSets);
	}
	
	@Override
	public UUID execute(BranchContext context) {
		try {
			final File file = doExport(toExportModel(context));
			final UUID fileId = UUID.randomUUID();
			context.service(FileRegistry.class).upload(fileId, new FileInputStream(file));
			return fileId;
		} catch (final Exception e) {
			return throwExportException("Error occurred while exporting SNOMED CT.", e);
		}
	}

	private File doExport(final SnomedRf2ExportModel model) throws Exception {
		final SnomedExportClientRequest snomedExportClientRequest = new SnomedExportClientRequest(SnomedClientProtocol.getInstance(), model);
		final StringBuilder sb = new StringBuilder("Performing SNOMED CT publication into ");
		
		if (model.isExportToRf1()) {
			sb.append("RF1 and ");
		}
		
		sb.append("RF2 release format...");
		
		final SubMonitor subMonitor = SubMonitor.convert(new NullProgressMonitor(), sb.toString(), 1000).newChild(1000, SubMonitor.SUPPRESS_ALL_LABELS);
		subMonitor.worked(5);
		
		final File resultFile = snomedExportClientRequest.send(new EclipseMonitor(subMonitor));
		final SnomedExportResult result = snomedExportClientRequest.getExportResult();
		model.getExportResult().setResultAndMessage(result.getResult(), result.getMessage());

		return resultFile;
	}

	private SnomedRf2ExportModel toExportModel(BranchContext context) {
		
		final ContentSubType contentSubType = convertType(releaseType);
		
		Branch branch = RepositoryRequests.branching()
			.prepareGet(context.branch().path())
			.build()
			.execute(context);
				
		final SnomedRf2ExportModel model = SnomedRf2ExportModel.createExportModelWithAllRefSets(contentSubType, branch, namespace);

		if (CompareUtils.isEmpty(modules)) {
			final SnomedConcepts allModules = SnomedRequests.prepareSearchConcept()
					.all()
					.filterByActive(true)
					.filterByAncestor(Concepts.MODULE_ROOT)
					.build()
					.execute(context);
			Set<String> allModuleIds = FluentIterable.from(allModules).transform(IComponent.ID_FUNCTION).toSet();
			model.getModulesToExport().addAll(allModuleIds);
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
		model.getRefSetIds().addAll(refSets);

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
		return null == arg ? this.throwExportException(message, null) : arg;
	}

	private <T> T throwExportException(final String message, Throwable t) {
		throw new RuntimeException(nullToEmpty(message), t);
	}

}
