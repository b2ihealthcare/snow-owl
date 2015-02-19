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

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.collections.Collections3.compare;
import static com.b2international.commons.collections.Collections3.toSet;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.convertIntoBasePath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_NUMBER;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.SetDifference;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.CoreTerminologyBroker.ICoreTerminologyComponentInformation;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.diff.FeatureChange;
import com.b2international.snowowl.datastore.index.diff.NodeChange;
import com.b2international.snowowl.datastore.index.diff.NodeDelta;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.NodeDiffImpl;
import com.b2international.snowowl.datastore.server.version.NodeTransformerImpl;
import com.b2international.snowowl.emf.compare.DiffCollector;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;

/**
 * Node transformer for SNOMED&nbsp;CT.
 *
 */
public class SnomedNodeTransformer extends NodeTransformerImpl {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.version.NodeTransformerImpl#doTransform(org.eclipse.emf.cdo.view.CDOView, org.eclipse.emf.cdo.view.CDOView, com.b2international.snowowl.datastore.index.diff.NodeDiff)
	 */
	@Override
	protected NodeChange doTransform(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		final Collection<NodeDiff> diffs = getAllRelatedDiffs(sourceView, targetView, diff);
		return createNodeChange(diff, sourceView, targetView, createDeltas(sourceView, targetView, diffs));
	}

	private long getConceptStorageKey(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		return getTerminologyBrowser().getStorageKey(getBranchPath(sourceView, targetView, diff), diff.getId());
	}

	private long getRefSetStorageKey(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		return getRefSetBrowser().getStorageKey(getBranchPath(sourceView, targetView, diff), diff.getId());
	}

	private NodeDiffImpl createConceptDiff(final NodeDiff diff, final long refSetStorageKey) {
		final NodeDiffImpl conceptDiff = new NodeDiffImpl(CONCEPT_NUMBER, refSetStorageKey, diff.getId(), diff.getLabel(), diff.getIconId(), diff.getParent(), diff.getChange());
		for (final NodeDiff child : diff.getChildren()) {
			conceptDiff.addChild(child);	
		}
		return conceptDiff;
	}

	private NodeDiffImpl createRefSetDiff(final NodeDiff diff, final long conceptStorageKey) {
		final NodeDiffImpl conceptDiff = new NodeDiffImpl(REFSET_NUMBER, conceptStorageKey, diff.getId(), diff.getLabel(), diff.getIconId(), diff.getParent(), diff.getChange());
		for (final NodeDiff child : diff.getChildren()) {
			conceptDiff.addChild(child);	
		}
		return conceptDiff;
	}

	private Iterable<NodeDelta> createDeltas(final CDOView sourceView, final CDOView targetView, final Iterable<? extends NodeDiff> diffs) {
		final Collection<NodeDelta> deltas = newArrayList();
		for (final NodeDiff diff : diffs) {
			if (isAddition(diff)) {
				deltas.addAll(createDeltaForNewComponent(sourceView, targetView, diff));
			} else if (isDeletion(diff)) {
				deltas.addAll(createDeltaForDeletedComponent(sourceView, targetView, diff));
			} else {
				deltas.addAll(createDeltaForChangedDiff(sourceView, targetView, diff));
			}
		}
		return deltas;
	}

	private Collection<NodeDelta> createDeltaForChangedDiff(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		
		if (isRefSet(diff) && !isModuleDependencyRefSet(diff)) {
			return compareRefSet(sourceView, targetView, diff);
		} else {
			final CDOObject source = loadObject(sourceView, diff);
			final CDOObject target = loadObject(targetView, diff);
			return DiffCollector.compare(source, target, new SnomedDiffProcessor(sourceView, targetView));
		}
	}

	private Collection<NodeDelta> compareRefSet(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		IBranchPath sourceBranchPath = createPath(sourceView);
		if (sourceView.getBranch().equals(targetView.getBranch())) {
			sourceBranchPath = convertIntoBasePath(sourceBranchPath);
		}
		final Collection<NodeDelta> refSetChanges = compareRefSetByMembers(sourceBranchPath, createPath(targetView), diff);
		refSetChanges.addAll(compareRefSetByMapTarget(sourceView, targetView, diff));
		return refSetChanges;
	}

	private Collection<NodeDelta> compareRefSetByMembers(final IBranchPath sourcePath, final IBranchPath targetPath, final NodeDiff diff) {
		
		final Set<SnomedRefSetMemberFragment> sourceMembers = toSet(getRefSetMembers(sourcePath, diff.getId()));
		final Set<SnomedRefSetMemberFragment> targetMembers = toSet(getRefSetMembers(targetPath, diff.getId()));
		
		final SetDifference<SnomedRefSetMemberFragment> difference = compare(sourceMembers, targetMembers, SnomedRefSetMemberFragment.EQUIVALENCE);
		
		final Set<SnomedRefSetMemberFragment> newOrChangedMembers = newHashSet(difference.entriesOnlyOnRight());
		final Collection<NodeDelta> memberChanges = newArrayList();
		
		for (final SnomedRefSetMemberFragment member : difference.entriesOnlyOnLeft()) {
			if (newOrChangedMembers.contains(member)) {
				memberChanges.add(createMemberStatusChangeDelta(member, targetPath));
				newOrChangedMembers.remove(member);
			} else {
				memberChanges.add(createMemberDeletedChangeDelta(member, diff, sourcePath));
			}
		}
		
		for (final SnomedRefSetMemberFragment newMember : newOrChangedMembers) {
			memberChanges.add(createMemberAddedChangeDelta(newMember, diff, targetPath));
		}
		
		return memberChanges;
	}

	private NodeDelta createMemberAddedChangeDelta(final SnomedRefSetMemberFragment member, final NodeDiff refSetDiff, final IBranchPath targetPath) {
		final String memberLabel = getMemberLabel(member, targetPath);
		return createDeltaForAddition(memberLabel, createEmptyFeatureChange(), REFSET_MEMBER_NUMBER);
	}

	private NodeDelta createMemberDeletedChangeDelta(final SnomedRefSetMemberFragment member, final NodeDiff refSetDiff, final IBranchPath sourcePath) {
		final String memberLabel = getMemberLabel(member, sourcePath);
		return createDeltaForDeletion(memberLabel, createEmptyFeatureChange(), REFSET_MEMBER_NUMBER);
	}

	private NodeDelta createMemberStatusChangeDelta(final SnomedRefSetMemberFragment member, final IBranchPath targetPath) {
		final String memberLabel = getMemberLabel(member, targetPath);
		final FeatureChange featureChange = createFeatureChange("Status", toActiveInactive(member.isActive()), toActiveInactive(!member.isActive()));
		return createDeltaForUpdate(memberLabel, featureChange, REFSET_MEMBER_NUMBER);
	}
	
	private Collection<SnomedRefSetMemberFragment> getRefSetMembers(final IBranchPath sourcePath, final String refSetId) {
		return getComponentService().getRefSetMemberFragments(sourcePath, refSetId);
	}

	private Collection<NodeDelta> createDeltaForDeletedComponent(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		if (isRefSet(diff)) {
			final Collection<NodeDelta> deltas = newArrayList();
			deltas.add(createDeltaForDeletion(diff.getLabel(), createEmptyFeatureChange(), REFSET_NUMBER));
			for (final Pair<String, String> labelPair : getComponentService().getReferenceSetMemberLabels(getBranchPath(sourceView, targetView, diff), diff.getId())) {
				final String memberLabel = getMemberLabel(labelPair);
				deltas.add(createDeltaForDeletion(memberLabel, createEmptyFeatureChange(), REFSET_MEMBER_NUMBER));
			}
			return deltas;
		} else {
			final FeatureChange featureChange = createEmptyFeatureChange();
			return toCollection(createDeltaForDeletion(diff.getLabel(), featureChange, CONCEPT_NUMBER));
		}
	}

	private Collection<NodeDelta> createDeltaForNewComponent(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		if (isRefSet(diff)) {
			final Collection<NodeDelta> deltas = newArrayList();
			deltas.add(createDeltaForAddition(diff.getLabel(), createEmptyFeatureChange(), REFSET_NUMBER));
			for (final Pair<String, String> labelPair : getComponentService().getReferenceSetMemberLabels(getBranchPath(sourceView, targetView, diff), diff.getId())) {
				final String memberLabel = getMemberLabel(labelPair);
				deltas.add(createDeltaForAddition(memberLabel, createEmptyFeatureChange(), REFSET_MEMBER_NUMBER));
			}
			return deltas;
		} else {
			final FeatureChange featureChange = createEmptyFeatureChange();
			return toCollection(createDeltaForAddition(diff.getLabel(), featureChange, CONCEPT_NUMBER));
		}
	}

	private Collection<NodeDelta> compareRefSetByMapTarget(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		final CDOObject object = loadObject(sourceView, diff);
		if (object instanceof SnomedMappingRefSet) {
			final short sourceMapTargetComponentType = ((SnomedMappingRefSet) object).getMapTargetComponentType();
			final short targetMapTargetComponentType = ((SnomedMappingRefSet) loadObject(targetView, diff)).getMapTargetComponentType();
			if (sourceMapTargetComponentType != targetMapTargetComponentType) {
				final String sourceMapTargetComponentName = getComponentName(sourceMapTargetComponentType);
				final String targetMapTargetComponentName = getComponentName(targetMapTargetComponentType);
				if (!isEmpty(sourceMapTargetComponentName) && !isEmpty(targetMapTargetComponentName)) {
					final FeatureChange featureChange = createFeatureChange("Map target", sourceMapTargetComponentName, targetMapTargetComponentName);
					return toCollection(createDeltaForUpdate(diff.getLabel(), featureChange, REFSET_NUMBER));
				}
			}
		}
		return emptyList();
	}

	private String getComponentName(final short sourceMapTargetComponentType) {
		if (isUnspecified(sourceMapTargetComponentType)) {
			return StringUtils.capitalizeFirstLetter(CoreTerminologyBroker.UNSPECIFIED.toLowerCase());
		} else {
			final ICoreTerminologyComponentInformation sourceComponentInformation = getComponentInformation(sourceMapTargetComponentType);
			if (null != sourceComponentInformation) {
				return nullToEmpty(sourceComponentInformation.getName());
			}
		}
		return null;
	}

	private ICoreTerminologyComponentInformation getComponentInformation(final short sourceMapTargetComponentType) {
		return CoreTerminologyBroker.getInstance().getComponentInformation(sourceMapTargetComponentType);
	}

	private boolean isUnspecified(final short sourceMapTargetComponentType) {
		return CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == sourceMapTargetComponentType;
	}
	
	private IBranchPath getBranchPath(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		return isDeletion(diff) ? createPath(sourceView) : createPath(targetView);
	}

	private String getMemberLabel(final Pair<String, String> labelPair) {
		final StringBuffer sb = new StringBuffer();
		sb.append(labelPair.getA());
		if (!isEmpty(labelPair.getB())) {
			sb.append(" - ");
			sb.append(labelPair.getB());
		}
		return sb.toString();
	}
	
	private Collection<NodeDiff> getAllRelatedDiffs(final CDOView sourceView, final CDOView targetView, final NodeDiff diff) {
		
		final Collection<NodeDiff> diffs = newArrayList();
		diffs.add(diff);
		
		if (isRefSet(diff)) {
			final long conceptStorageKey = getConceptStorageKey(sourceView, targetView, diff);
			if (exists(conceptStorageKey)) {
				final NodeDiffImpl conceptDiff = createConceptDiff(diff, conceptStorageKey);
				diffs.add(conceptDiff);
			}
		} else {
			final long refSetStorageKey = getRefSetStorageKey(sourceView, targetView, diff);
			final IBranchPath branchPath = getBranchPath(sourceView, targetView, diff);
			if (exists(refSetStorageKey) && isRegulaOrModuleRefSet(diff, refSetStorageKey, branchPath)) {
				final NodeDiffImpl conceptDiff = createRefSetDiff(diff, refSetStorageKey);
				diffs.add(conceptDiff);
			}
		}
		
		return diffs;
	}

	private boolean isRegulaOrModuleRefSet(final NodeDiff diff, final long refSetStorageKey, final IBranchPath branchPath) {
		return isModuleDependencyRefSet(diff) || isRegularRefSet(branchPath, refSetStorageKey);
	}

	private boolean isModuleDependencyRefSet(final NodeDiff refSetDiff) {
		return REFSET_MODULE_DEPENDENCY_TYPE.equals(refSetDiff.getId());
	}

	private String getMemberLabel(final SnomedRefSetMemberFragment member, final IBranchPath branchPath) {
		final Pair<String, String> labelPair = getComponentService().getMemberLabel(branchPath, member.getUuid());
		final StringBuffer sb = new StringBuffer();
		sb.append(labelPair.getA());
		if (!isEmpty(labelPair.getB())) {
			sb.append(" - ");
			sb.append(labelPair.getB());
		}
		return sb.toString();
	}
	
	private boolean isRegularRefSet(final IBranchPath branchPath, final long refSetStorageKey) {
		return getRefSetBrowser().isRegularRefSet(branchPath, refSetStorageKey);
	}
	
	private boolean exists(final long conceptStorageKey) {
		return NO_STORAGE_KEY < conceptStorageKey;
	}

	private boolean isRefSet(final NodeDiff diff) {
		return REFSET_NUMBER == diff.getTerminologyComponentId();
	}

	private ISnomedComponentService getComponentService() {
		return getServiceForClass(ISnomedComponentService.class);
	}
	
	private SnomedRefSetBrowser getRefSetBrowser() {
		return getServiceForClass(SnomedRefSetBrowser.class);
	}

	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return getServiceForClass(SnomedTerminologyBrowser.class);
	}
	
}