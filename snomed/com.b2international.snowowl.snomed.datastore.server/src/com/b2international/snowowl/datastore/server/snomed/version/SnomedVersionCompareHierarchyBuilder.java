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
package com.b2international.snowowl.datastore.server.snomed.version;

import static com.b2international.commons.ChangeKind.UNCHANGED;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentIconIdProvider;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.browser.ExtendedComponentProvider;
import com.b2international.snowowl.core.api.browser.SuperTypeIdProvider;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.NodeDiffImpl;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.b2international.snowowl.datastore.server.version.VersionCompareHierarchyBuilderImpl;
import com.b2international.snowowl.datastore.version.NodeDiffPredicate;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIconIdProvider;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedCachingSuperTypeIdProvider;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Version compare hierarchy builder implementation for the SNOMED&nbsp;CT ontology.
 */
public class SnomedVersionCompareHierarchyBuilder extends VersionCompareHierarchyBuilderImpl {

	private static final String INACTIVE_SNOMED_CT_CONCEPTS_LABEL = "Inactive SNOMED CT Concepts";
	private static final String INACTIVE_SNOMED_CT_CONCEPTS_ID = "-1";
	private static final IComponentIconIdProvider<String> ICON_ID_PROVIDER = new SnomedConceptIconIdProvider();
	private static final Comparator<NodeDiff> INACTIVE_ALWAYS_LAST_LABEL_COMPARATOR = new Comparator<NodeDiff>() {
		@Override public int compare(final NodeDiff o1, final NodeDiff o2) {
			if (o1 != o2) {
				if (INACTIVE_SNOMED_CT_CONCEPTS_ID.equals(o1.getId())) {
					return 1;
				} else if (INACTIVE_SNOMED_CT_CONCEPTS_ID.equals(o2.getId())) {
					return -1;
				}
			}
			return LABEL_COMPARATOR.compare(o1, o2);
		}
	}; 

	private final Predicate<NodeDiff> topLevelNodePredicate = new NodeDiffPredicate() {
		@Override public boolean apply(final NodeDiff nodeDiff) {
			return isTopLevel(checkNotNull(nodeDiff, "nodeDiff"));
		}
	};
	
	private final SuperTypeIdProvider<String> idProvider = new SnomedCachingSuperTypeIdProvider();
	
	@Override
	protected IComponentIconIdProvider<String> getIconIdProvider() {
		return ICON_ID_PROVIDER;
	}
	
	@Override
	protected IComponentNameProvider getNameProvider() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected String getLabel(IBranchPath branchPath, String componentId) {
		return componentId;
	}
	
	@Override
	public Map<String, String> resolveLabels(Multimap<IBranchPath, String> componentIdsByBranch) {
		final ApplicationContext context = ApplicationContext.getInstance();
		final List<ExtendedLocale> locales = context.getService(LanguageSetting.class).getLanguagePreference();
		final IEventBus bus = context.getService(IEventBus.class);
		
		final Map<String, ISnomedDescription> pts = newHashMap();
		for (final IBranchPath branch : componentIdsByBranch.keySet()) {
			final Set<String> componentIds = newHashSet(componentIdsByBranch.get(branch));
			pts.putAll(new DescriptionRequestHelper() {
				@Override
				protected SnomedDescriptions execute(SnomedDescriptionSearchRequestBuilder req) {
					return req.build(branch.getPath()).executeSync(bus);
				}
			}.getPreferredTerms(componentIds, locales));
		}
		return Maps.transformValues(pts, new Function<ISnomedDescription, String>() {
			@Override
			public String apply(ISnomedDescription input) {
				return input.getTerm();
			}
		});
	}
	
	@Override
	protected short getTerminologyComponentId() {
		return CONCEPT_NUMBER;
	}
	
	@Override
	public boolean isRoot(final NodeDiff node) {
		return ROOT_CONCEPT.equals(checkNotNull(node, "node").getId()) 
				|| INACTIVE_SNOMED_CT_CONCEPTS_ID.equals(node.getId());
	}

	@Override
	public CompareResult createCompareResult(final VersionCompareConfiguration configuration, final Collection<NodeDiff> changedNodes) {
		
		checkNotNull(configuration, "configuration");
		checkNotNull(changedNodes, "changedNodes");
		
		boolean hasInactiveNode = false;
		final NodeDiffImpl fakeGroupNode = createFakeInactiveGroupNode();
		for (final NodeDiff diff : changedNodes) {
			if (null == diff.getParent() && !isRoot(diff)) {
				((NodeDiffImpl) diff).setParent(fakeGroupNode);
				fakeGroupNode.addChild(diff);
				hasInactiveNode = true;
			}
		}
		
		if (hasInactiveNode) {
			changedNodes.add(fakeGroupNode);
		}
		
		return super.createCompareResult(configuration, changedNodes);
	}

	@Override
	protected Predicate<NodeDiff> getNodeFilterPredicate() {
		return and(super.getNodeFilterPredicate(), not(topLevelNodePredicate));
	}

	@Override
	protected ExtendedComponentProvider getExtendedComponentProvider() {
		return getServiceForClass(SnomedTerminologyBrowser.class);
	}

	@Override
	protected SuperTypeIdProvider<String> getSuperTypeIdProvider() {
		return idProvider;
	}
	
	@Override
	protected Comparator<NodeDiff> getComparator() {
		return INACTIVE_ALWAYS_LAST_LABEL_COMPARATOR;
	}

	private boolean isTopLevel(final NodeDiff diff) {
		return null != diff.getParent() && isRoot(diff.getParent());
	}
	
	private NodeDiffImpl createFakeInactiveGroupNode() { 
		return new NodeDiffImpl(CONCEPT_NUMBER, NO_STORAGE_KEY, INACTIVE_SNOMED_CT_CONCEPTS_ID, 
			INACTIVE_SNOMED_CT_CONCEPTS_LABEL, ROOT_CONCEPT, null, UNCHANGED);
	}
	
}