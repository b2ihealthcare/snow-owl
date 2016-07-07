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
package com.b2international.snowowl.snomed.refset.core.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.collections.longs.LongKeyMap;
import com.b2international.commons.concurrent.ConcurrentCollectionUtils;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedHierarchy;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.refset.core.compare.ReferencedComponentDelta.DeltaKind;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Compares two reference sets and finds members with additional or related referenced components. Subsumption is a special case of relation, when there is an 'Is a' relationship
 * between the concepts.
 * 
 */
public class RefSetRelationComparator {

	private final IClientTerminologyBrowser<SnomedConceptDocument, String> terminologyBrowser;
	private SubsumptionTester delegateSubsumptionTester;

	/**
	 * Class constructor.
	 * 
	 * @param terminologyBrowser the {@link SnomedClientTerminologyBrowser terminology browser} to use
	 */
	public RefSetRelationComparator(IClientTerminologyBrowser<SnomedConceptDocument, String> terminologyBrowser) {
		this.terminologyBrowser = terminologyBrowser;
	}

	/**
	 * Compare the predicate reference set to the candidate reference set.
	 * 
	 * @param members1
	 *            the members of the predicate {@link SnomedRefSet reference set}
	 * @param members2
	 *            the members of the candidate {@link SnomedRefSet reference set}
	 * @param monitor
	 *            the progress monitor
	 * @return the comparison results
	 * 
	 * @throws OperationCanceledException
	 *             if the progress monitor has been cancelled
	 */
	public List<ReferencedComponentDelta> compare(Collection<SnomedConceptDocument> members1, Collection<SnomedConceptDocument> members2, IProgressMonitor monitor) {
		
		this.delegateSubsumptionTester = new SubsumptionTester(terminologyBrowser);

		Set<SnomedConceptDocument> parentReferencedComponents = (Set<SnomedConceptDocument>) (members1 instanceof Set<?> ? members1 : Sets.newHashSet(members1));
		Set<SnomedConceptDocument> childReferencedComponents = (Set<SnomedConceptDocument>) (members2 instanceof Set<?> ? members2 : Sets.newHashSet(members2));
		Set<SnomedConceptDocument> childReferencedComponentsFiltered = Sets.difference(childReferencedComponents, parentReferencedComponents);

		// detect added and related
		Iterator<Collection<ReferencedComponentDelta>> addedOrSubsumedDeltaIterator = ConcurrentCollectionUtils.transform(
				childReferencedComponentsFiltered.iterator(), new AddedOrRelatedReferencedComponentDeltaFunction(parentReferencedComponents));

		// manually build delta lists to be able to report progress (this may take minutes with large ref sets)
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		SubMonitor childMonitor = subMonitor.newChild(100);
		childMonitor.setTaskName("Comparing reference sets...");
		childMonitor.setWorkRemaining(childReferencedComponentsFiltered.size());
		
		
		List<ReferencedComponentDelta> childDeltaList = Lists.newArrayList();
		while (addedOrSubsumedDeltaIterator.hasNext()) {
			childDeltaList.addAll(addedOrSubsumedDeltaIterator.next());
			childMonitor.worked(1);
			if (childMonitor.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		return childDeltaList;
	}

	private final class AddedOrRelatedReferencedComponentDeltaFunction implements Function<SnomedConceptDocument, Collection<ReferencedComponentDelta>> {
		private final Collection<SnomedConceptDocument> referencedComponents;
		private SnomedHierarchy hierarchy;

		public AddedOrRelatedReferencedComponentDeltaFunction(Collection<SnomedConceptDocument> referencedComponents) {
			final Runnable initBuilder = new Runnable() {
				@Override public void run() {
					hierarchy = SnomedHierarchy.forActiveBranch();
				}
			};
			
			initBuilder.run();
			this.referencedComponents = referencedComponents;
		}

		@Override
		public Collection<ReferencedComponentDelta> apply(SnomedConceptDocument id) {
			Collection<ReferencedComponentDelta> componentDeltas = new ArrayList<ReferencedComponentDelta>();
			for (SnomedConceptDocument refComponent : referencedComponents) {
				if (isSubsumed(refComponent, id)) {
					componentDeltas.add(new ReferencedComponentDelta(id.getId(), id.getLabel(), id.getIconId(), DeltaKind.SUBSUMED, refComponent.getId(), refComponent.getLabel(), refComponent.getIconId()));
				}
				
				if (isRelated(refComponent, id)) {
					componentDeltas.add(new ReferencedComponentDelta(id.getId(), id.getLabel(), id.getIconId(), DeltaKind.RELATED, refComponent.getId(), refComponent.getLabel(), refComponent.getIconId()));
				}
				
			}
			
			// if there is no subsumption or relationship between the two concepts, then it was added simply.
			if (componentDeltas.isEmpty()) {
				componentDeltas.add(new ReferencedComponentDelta(id.getId(), id.getLabel(), id.getIconId(), DeltaKind.ADDED));
			}
			
			return componentDeltas;
		}

		private boolean isSubsumed(SnomedConceptDocument refComponentId, SnomedConceptDocument id) {
			return delegateSubsumptionTester.isSubsumed(Long.parseLong(refComponentId.getId()), Long.parseLong(id.getId()), hierarchy);
		}

		private boolean isRelated(SnomedConceptDocument refComponentId, SnomedConceptDocument id) {
			throw new UnsupportedOperationException("Unsupported API, refactor refset relation comparation");
//			return RelationTester.isRelated(Long.parseLong(refComponentId.getId()), Long.parseLong(id.getId()), statements);
		}
	}
}