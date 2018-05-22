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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.QUERY;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.unmodifiableSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Utility class for retrieving information about reference set member redundancy associated 
 * with SNOMED CT reference sets.<br>This class is not intended to be instantiated.
 */
public abstract class RefSetMemberRedundancyAnalyzer {

	private static final Function<SnomedRefSetMember, String> MEMBER_TO_REFERENCED_COMPONENT_ID = new Function<SnomedRefSetMember, String>() {
		@Override public String apply(final SnomedRefSetMember member) { return member.getReferencedComponentId(); }
	};
	
	/**
	 * Private constructor.
	 */
	private RefSetMemberRedundancyAnalyzer() { /*suppress construction*/ }
	
	/**
	 * Returns with a {@link ReferencedComponentIdSet a set of redundant and non-redundant member IDs} based on the specified SNOMED CT 
	 * reference set and the terminology independent component identifier.
	 * <br><br><b>Note: </b>this method does nothing but returns with the specified <b>componentIds</b> wrapped to a {@link ReferencedComponentIdSet} instance
	 * since redundancy based on the referenced components is not available for SNOMED CT {@link SnomedRefSetType#SIMPLE_MAP simple map} type and 
	 * SNOMED CT {@link SnomedRefSetType#COMPLEX_MAP complex map} type reference sets. This method throws IllegalStateException if the specified SNOMED CT
	 * reference set is a {@link SnomedRefSetType#QUERY query type} reference set.
	 * @param refSet the reference where the member redundancy analysis should be performed.
	 * @param monitor the progress monitor for the process. Can be {@code null}. If {@code null} a new {@link NullProgressMonitor} will be instantiated.
	 * @param componentIds the terminology independent component identifiers.
	 * @return the {@link ReferencedComponentIdSet} wrapping the redundant and non-redundant reference set member IDs.
	 */
	public static ReferencedComponentIdSet analyzeMemberRedundancy(@Nonnull final SnomedRegularRefSet refSet, @Nullable IProgressMonitor monitor, final Iterable<String> componentIds) {
		checkNotNull(refSet, "SNOMED CT reference set argument cannot be null.");
		checkNotNull(refSet.getIdentifierId(), "The identifier SNOMED CT concept ID cannot be null.");
		checkState(!StringUtils.isEmpty(refSet.getIdentifierId()), "The identifier SNOMED CT concept ID cannot be empty.");
		checkState(!QUERY.equals(refSet.getType()), "SNOMED CT query type reference set is not supported.");

		if (null == monitor)
			monitor = new NullProgressMonitor();
		
		monitor.beginTask("Analyzing reference set member redundancy...", IProgressMonitor.UNKNOWN);
		
		if (monitor.isCanceled()) {
			monitor.setCanceled(true);
			return ReferencedComponentIdSet.NULL_IMPL;
		}
		
		//no member redundancy check is required for either SNOMED CT simple map type or SNOMED CT complex map type reference sets.
		if (SnomedRefSetUtil.isMapping(refSet.getType())) {
			return ReferencedComponentIdSet.createWithoutRedundantMembers(componentIds);
		}
		
		//get all persisted members and extract the unique component identifiers of the referenced components 
		final Set<String> persistedIds = ImmutableSet.copyOf(getPersistedReferencedComponentIds(refSet));
		
		if (monitor.isCanceled()) {
			monitor.setCanceled(true);
			return ReferencedComponentIdSet.NULL_IMPL;
		}
		
		//need to update persisted IDs with the changes made on the underlying transaction.
		final CDOView view = refSet.cdoView();

		if (monitor.isCanceled()) {
			monitor.setCanceled(true);
			return ReferencedComponentIdSet.NULL_IMPL;
		}
		
		//we need to delete the detached IDs from persisted IDs
		persistedIds.removeAll(Sets.newHashSet(Collections2.transform(getDetachedMembers(view), MEMBER_TO_REFERENCED_COMPONENT_ID)));
		
		if (monitor.isCanceled()) {
			monitor.setCanceled(true);
			return ReferencedComponentIdSet.NULL_IMPL;
		}
		
		//wee need to add the new IDs to the persisted IDs
		persistedIds.addAll(Sets.newHashSet(Collections2.transform(getNewMembers(view), MEMBER_TO_REFERENCED_COMPONENT_ID)));
		
		if (monitor.isCanceled()) {
			monitor.setCanceled(true);
			return ReferencedComponentIdSet.NULL_IMPL;
		}
		
		//after we have the most up-to-date referenced component IDs we should create the return value based on the specified component IDs argument
		final Set<String> allComponentIds = Sets.newHashSet(componentIds);
		final Set<String> nonRedundantMembers = Sets.difference(allComponentIds, persistedIds);
		
		if (monitor.isCanceled()) {
			monitor.setCanceled(true);
			return ReferencedComponentIdSet.NULL_IMPL;
		}
		
		final Set<String> redundantMembers = Sets.newHashSet(componentIds);
		redundantMembers.removeAll(nonRedundantMembers);
		
		return new ReferencedComponentIdSet(redundantMembers, nonRedundantMembers);
	}


	/*returns with all persisted members and extract the unique component identifiers of the referenced components. returns with an empty set if the reference set has no members*/
	private static Collection<String> getPersistedReferencedComponentIds(final SnomedRegularRefSet refSet) {
		final String branch = BranchPathUtils.createPath(refSet.cdoView()).getPath();
		return 0 == refSet.getMembers().size()
				? ImmutableSet.<String>of()
				: getAllMemberReferencedComponentIds(branch, refSet.getIdentifierId());
	}
	
	private static Collection<String> getAllMemberReferencedComponentIds(String branch, String refSetId) {
		return SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(refSetId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedReferenceSetMembers, Collection<String>>() {
					@Override
					public Collection<String> apply(SnomedReferenceSetMembers input) {
						return FluentIterable.from(input).transform(new Function<SnomedReferenceSetMember, String>() {
							@Override
							public String apply(SnomedReferenceSetMember input) {
								return input.getReferencedComponent().getId();
							}
						}).toSet();
					}
				})
				.getSync();
		
	}

	/*returns with the detached reference set members from the CDO view*/
	private static Set<SnomedRefSetMember> getDetachedMembers(final CDOView view) {
		return Sets.newHashSet(ComponentUtils2.getDetachedObjects(view, SnomedRefSetMember.class));
	}
	
	/*returns with the new reference set members from the CDO view*/
	private static Set<SnomedRefSetMember> getNewMembers(final CDOView view) {
		return Sets.newHashSet(ComponentUtils2.getNewObjects(view, SnomedRefSetMember.class));
	}

	/**
	 * Class for encapsulating two sets containing unique identifiers of terminology independent components.
	 * <br><br><b>Note: </b>this class guarantees for the clients that the intersection of the redundant and non redundant 
	 * member identifiers is always empty.
	 */
	@ThreadSafe
	@Immutable
	public static final class ReferencedComponentIdSet {
		
		private static final ReferencedComponentIdSet NULL_IMPL = createWithoutRedundantMembers(Collections.<String>emptyList());
		
		/**
		 * Creates a new instance of this class without redundant members.
		 * @param nonRedundantMembers the non redundant members.
		 * @return a new instance of this class where the redundant members will be an empty set of component IDs.
		 */
		static ReferencedComponentIdSet createWithoutRedundantMembers(@Nonnull final Iterable<String> nonRedundantMembers) {
			checkNotNull(nonRedundantMembers, "Non redundant members argument cannot be null.");
			return new ReferencedComponentIdSet(Sets.<String>newHashSet(), Sets.newHashSet(nonRedundantMembers)); 
		}
		
		private final Set<String> redundantMembers;
		private final Set<String> nonRedundantMembers;

		/**
		 * Creates a new instance of this class based on the specified two sets containing the redundant and non-redundant 
		 * component identifiers. Throw NullPointerException if any of the specified argument is {@code null}. Throws 
		 * IllegalStateException if the intersection of the specified two sets is not empty. 
		 * @param redundantMembers the identifiers of the redundant components. Cannot be {@code null}.
		 * @param nonRedundantMembers the identifiers of the non-redundant components. Cannot be {@code null}.
		 */
		private ReferencedComponentIdSet(@Nonnull final Set<String> redundantMembers, @Nonnull final Set<String> nonRedundantMembers) {
			checkNotNull(redundantMembers, "Redundant members argument cannot be null.");
			checkNotNull(nonRedundantMembers, "Non redundant members argument cannot be null.");
			this.redundantMembers = Sets.newHashSet(redundantMembers);
			this.nonRedundantMembers = Sets.newHashSet(nonRedundantMembers);
			Set<String> intersection = Sets.intersection(this.redundantMembers, this.nonRedundantMembers);
			checkInteresection(intersection);

			//cleanup after state check.
			intersection.clear();
			intersection = null;
		}

		/**
		 * Returns with a copy of the non-redundant component identifiers.
		 * @return the non-redundant component identifiers.
		 */
		public Set<String> getNonRedundantMembers() {
			return unmodifiableSet(nonRedundantMembers);
		}

		/**
		 * Returns with a copy of the redundant component identifiers.
		 * @return the identifiers of the redundant members.
		 */
		public Set<String> getRedundantMembers() {
			return unmodifiableSet(redundantMembers);
		}

		/*checks the state of the two set instances.*/
		private void checkInteresection(final Set<String> intersection) {
			checkState(intersection.isEmpty(), 
				"The intersection of the redundant and non redundant members should be empty. Intersection was: " 
						+ Arrays.toString(intersection.toArray()));
		}
	}
	
}