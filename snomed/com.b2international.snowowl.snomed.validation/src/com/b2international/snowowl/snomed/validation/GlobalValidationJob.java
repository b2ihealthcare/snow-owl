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
package com.b2international.snowowl.snomed.validation;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.TimedProgressMonitorWrapper;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ValuedJob;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.core.markers.MarkerManager;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.validation.IClientSnomedComponentValidationService;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Evaluates all global validation constraints, then proceeds with evaluating all concept validation rules
 * on all concepts.
 * 
 */
public class GlobalValidationJob extends ValuedJob<Integer> {
	
	private static final String[] EMPTY_ARRAY = {};
	private static final Logger LOG = LoggerFactory.getLogger(GlobalValidationJob.class);
	
	private final IClientSnomedComponentValidationService validationService;
	private final Collection<String> globalValidationConstraintIds;

	public GlobalValidationJob(final String name, final Object family) {
		this(name, family, EMPTY_ARRAY);
	}
	
	public GlobalValidationJob(final String name, final Object family, final String... globalValidationConstraintIds) {
		super(name, family);
		this.validationService = getServiceForClass(IClientSnomedComponentValidationService.class);
		this.globalValidationConstraintIds = newHashSet(globalValidationConstraintIds);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		
		LOG.info("Validation started...");
		final Stopwatch stopwatch = Stopwatch.createStarted();
		
		final SubMonitor delegateMonitor = SubMonitor.convert(new TimedProgressMonitorWrapper(monitor), 100);
		
		final Collection<ComponentValidationDiagnostic> validationResults;
		
		if (isEmpty(globalValidationConstraintIds)) {
			validationResults = validationService.validateAll(delegateMonitor.newChild(80));
		} else {
			validationResults = validationService.validateGlobalConstraints(globalValidationConstraintIds, delegateMonitor.newChild(80));
		}
		
		if (validationResults.size() == 1 && DiagnosticSeverity.CANCEL.equals(Iterables.getFirst(validationResults, null).getProblemMarkerSeverity())) {
			return Status.CANCEL_STATUS;
		}

		final Collection<String> violatingComponentIds = collectViolatingComponentIds(validationResults, delegateMonitor.newChild(20));
		
		if (delegateMonitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		
		setValue(violatingComponentIds.size());
		
		LOG.info("Validation finished in {}.", TimeUtil.toString(stopwatch));
		
		return Status.OK_STATUS;
	}

	private Collection<String> collectViolatingComponentIds(final Collection<ComponentValidationDiagnostic> validationResults, final IProgressMonitor monitor) {
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		final MarkerManager markerManager = ApplicationContext.getInstance().getService(MarkerManager.class);
		
		final Map<String, SnomedConceptIndexEntry> idToIndexEntryMap = getIdToIndexEntryMap(validationResults, subMonitor.newChild(1));
		
		final SubMonitor markerMonitor = subMonitor.newChild(1);
		markerMonitor.setWorkRemaining(validationResults.size());
		
		final Collection<String> violatingComponentIds = newHashSet(); 
		
		for (final ComponentValidationDiagnostic diagnostic : validationResults) {
			
			if (!diagnostic.isOk()) {
				violatingComponentIds.add(diagnostic.getId());
			}
			
			markerManager.createValidationMarkerOnComponent(idToIndexEntryMap.get(diagnostic.getId()), diagnostic);
			
			if (markerMonitor.isCanceled()) {
				return emptySet();
			}
			
			markerMonitor.worked(1);
		}
		
		return violatingComponentIds;
	}

	private Map<String, SnomedConceptIndexEntry> getIdToIndexEntryMap(final Collection<ComponentValidationDiagnostic> validationResults, final IProgressMonitor monitor) {
		
		final Set<String> componentIds = FluentIterable.from(validationResults).transform(new Function<ComponentValidationDiagnostic, String>() {
			@Override public String apply(final ComponentValidationDiagnostic input) {
				return input.getId();
			}
		}).toSet();
		
		final String branchPath = BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath();

		final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
			.setComponentIds(componentIds)
			.setLocales(getLocales())
			.setExpand("pt()")
			.all()
			.build(branchPath)
			.executeSync(getEventbus());
		
		final Map<String, SnomedConceptIndexEntry> idToEntryMap = Maps.uniqueIndex(SnomedConceptIndexEntry.fromConcepts(concepts), new Function<SnomedConceptIndexEntry, String>() {
			@Override
			public String apply(final SnomedConceptIndexEntry input) {
				return input.getId();
			}
		});
		
		monitor.worked(1);
		
		return idToEntryMap;
	}

	private IEventBus getEventbus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
	}
}