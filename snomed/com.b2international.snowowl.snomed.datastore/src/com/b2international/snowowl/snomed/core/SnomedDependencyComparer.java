/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.branch.compare.BranchCompareChangeStatistic;
import com.b2international.snowowl.core.branch.compare.BranchCompareResult;
import com.b2international.snowowl.core.compare.AnalysisCompareChangeKind;
import com.b2international.snowowl.core.compare.AnalysisCompareResult;
import com.b2international.snowowl.core.compare.AnalysisCompareResultItem;
import com.b2international.snowowl.core.compare.DependencyComparer;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.google.common.collect.ImmutableList;

/**
 * @since 9.0.0
 */
public class SnomedDependencyComparer implements DependencyComparer {

	@Override
	public AnalysisCompareResult compareResource(
		final RepositoryContext context, 
		final ResourceURIWithQuery fromUri, 
		final ResourceURIWithQuery toUri,
		final boolean includeChanges
	) {

		final ResourceURI fromWithoutQuery = fromUri.getResourceUri();
		final ResourceURI toWithoutQuery = toUri.getResourceUri();
		final ResourceURIPathResolver pathResolver = context.service(ResourceURIPathResolver.class);

		final List<String> branchPaths = pathResolver.resolve(context, ImmutableList.of(fromWithoutQuery, toWithoutQuery));
		final String baseBranch = branchPaths.get(0);
		final String compareBranch = branchPaths.get(1);

		final BranchCompareResult compareResult = RepositoryRequests.branching().prepareCompare()
				.setBase(baseBranch)
				.setCompare(compareBranch)
				// we only care about changes concepts, regardless of the change type
				.filterByType(SnomedConcept.TYPE)
				.setIncludeComponentChanges(true)
				.setIncludeDerivedComponentChanges(false)
				.withChangeStatsFor(
					SnomedConcept.Fields.ACTIVE,
					SnomedDescription.TYPE,
					SnomedRelationship.TYPE
				)
				.build()
				.execute(context);

		final Map<String, AnalysisCompareChangeKind> changeDetails = new HashMap<>();
		
		// add added and deleted stuff to changeDetails
		compareResult.getNewComponents().forEach(ci -> changeDetails.put(ci.getComponentId(), AnalysisCompareChangeKind.ADDED));
		compareResult.getDeletedComponents().forEach(ci -> changeDetails.put(ci.getComponentId(), AnalysisCompareChangeKind.DELETED));
		
		// Register all changed concepts using a prioritized change kind
		for (BranchCompareChangeStatistic stats : compareResult.getStats()) {
			AnalysisCompareChangeKind changeKind = null;
			if (SnomedConcept.Fields.ACTIVE.equals(stats.getProperty())) {
				changeKind = AnalysisCompareChangeKind.INACTIVATION;
			} else if (SnomedDescription.TYPE.equals(stats.getProperty())) {
				changeKind = AnalysisCompareChangeKind.DESCRIPTION_CHANGE;
			} else if (SnomedRelationship.TYPE.equals(stats.getProperty())) {
				changeKind = AnalysisCompareChangeKind.DEFINITION_CHANGE;
			}
			if (changeKind != null) {
				for (ComponentIdentifier componentId : stats.getComponentIds()) {
					// select and report only the greatest issue severity
					changeDetails.merge(componentId.getComponentId(), changeKind, (prev, next) -> prev.ordinal() <= next.ordinal() ? prev : next);
				}
			}
		}

		// count the number of different changes left after selecting the highest issue with a component
		final Map<String, Integer> counters = new HashMap<>();
		changeDetails.forEach((conceptId, changeKind) -> {
			counters.merge(changeKind.name().toLowerCase(), 1, Integer::sum);
		});
		
		final AnalysisCompareResult result;
		
		if (includeChanges) {
			final List<AnalysisCompareResultItem> items = changeDetails
				.entrySet()
				.stream()
				.map(e -> new AnalysisCompareResultItem(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
			
			result = new AnalysisCompareResult(items, fromUri, toUri);
		} else {
			// No change detail items needed
			result = new AnalysisCompareResult(fromUri, toUri);
		}
		
		result.setNewComponents(compareResult.getTotalNew());
		result.setChangedComponents(changeDetails.size());
		result.setDeletedComponents(compareResult.getTotalDeleted());
		
		for (String counterName : counters.keySet()) {
			// prevent registering added and deleted counter keys twice
			if (AnalysisCompareChangeKind.ADDED.name().toLowerCase().equals(counterName) || AnalysisCompareChangeKind.DELETED.name().toLowerCase().equals(counterName)) {
				continue;
			}
			result.setCounterValue(counterName, counters.get(counterName));
		}
		
		return result;
	}

}
