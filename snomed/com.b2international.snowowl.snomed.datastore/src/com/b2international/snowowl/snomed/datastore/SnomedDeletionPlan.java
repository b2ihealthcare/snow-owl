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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_MEMBER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A DTO about the projected outcome of a delete operation in SNOMED CT.
 * 
 */
public class SnomedDeletionPlan {

	public static final String REJECT_MESSAGE_FORMAT = "The %s '%s' has been released, and cannot be deleted.";
	
	private final List<String> rejectionReasons = new ArrayList<String>();
	
	//	deleting concepts that are member of description type refset may affect concept descriptions.
	private Collection<Description> descriptionsToUpdate = Sets.newHashSet();

	// keep deletedItems sorted by type for nicer display to the user
	private final Set<CDOObject> deletedItems = new TreeSet<CDOObject>(ComponentUtils2.CDO_OBJECT_COMPARATOR);
	
	//keep terminology component identifier and component identifier identifying the deleted objects
	private final Set<ComponentIdentifierPair<String>> deletedComponents = Sets.newHashSet();
	
	/** @return the reason why the delete plan is rejected, for example if a concept cannot be deleted once it was released. */
	public List<String> getRejectionReasons() {
		return rejectionReasons;
	}
	public void addRejectionReason(final String rejectionReason) {
		rejectionReasons.add(rejectionReason);
	}

	/** @return true if the requested plan cannot be executed because a concept or a relationship cannot be deleted */
	public boolean isRejected() {
		return rejectionReasons.size() > 0 || isEmpty();
	}

	/** @return true if there are any concepts or relationships in the delete plan */
	public boolean isEmpty() {
		return deletedItems.isEmpty();
	}
	
	/** @return the items deleted according to the plan */
	public Set<CDOObject> getDeletedItems() {
		return deletedItems;
	}
	
	/**
	 * Returns with a copy of the deleted components represented as {@link ComponentIdentifierPair identifier pair} instances.
	 * @return a set of component identifier pairs. Generally another representation of the components marked for deletion.
	 */
	public Set<ComponentIdentifierPair<String>> getDeletedComponentIdentifiers() {
		return Collections.unmodifiableSet(deletedComponents);
	}

	/**
	 * Marks an component for deletion.
	 * @param cdoObject the component to delete.
	 */
	public void markForDeletion(final CDOObject cdoObject) {
		internalMarkForDeletion(Collections.singletonList(cdoObject));
	}
	
	/**
	 * Marks a collection of components for deletion.
	 * @param items the items to delete.
	 */
	public void markForDeletion(final Collection<? extends CDOObject> items) {
		internalMarkForDeletion(items);
	}
	
	private void internalMarkForDeletion(final Collection<? extends CDOObject> items) {
		for (final EObject object : items) {
			final ComponentIdentifierPair<String> pair = createIdentifierPair(object);
			if (null == pair) //e.g.: concrete domain elements
				continue;
			
			if (object instanceof SnomedRefSet) {
				final SnomedRefSet refSet = (SnomedRefSet) object;
				//reference set member is containment in a reference set, we do not have to remove them but the associated markers have to be removed
				//we run a query, get members and generated component identifier pairs to identify the reference set members
				for (final SnomedRefSetMemberIndexEntry member : getMembers(refSet)) {
					deletedComponents.add(ComponentIdentifierPair.<String>create(REFSET_MEMBER, member.getId()));
				}
			}
			deletedComponents.add(pair);
		}
		deletedItems.addAll(items);
	}
	
	private List<SnomedRefSetMemberIndexEntry> getMembers(final SnomedRefSet refSet) {
		int memberCount = getMemberCount(refSet);
		return memberCount > 0
			? getIndexService().search(createMembersQuery(refSet), memberCount)
			: Lists.<SnomedRefSetMemberIndexEntry>newArrayList();
	}
	
	private int getMemberCount(final SnomedRefSet refSet) {
		
		if (refSet instanceof SnomedRegularRefSet) {
			return ((SnomedRegularRefSet) refSet).getMembers().size();
		} else {
			// Don't have any better guess for the expected number of results in structural reference sets
			return Integer.MAX_VALUE; 
		}
	}
	
	private SnomedRefSetMemberIndexQueryAdapter createMembersQuery(final SnomedRefSet refSet) {
		return new SnomedRefSetMemberIndexQueryAdapter(refSet.getIdentifierId(), null);
	}
	
	private SnomedClientIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
	}
	
	private ComponentIdentifierPair<String> createIdentifierPair(final Object object) {
		final IComponent<?> component = CoreTerminologyBroker.getInstance().adapt(object);
		if (null == component)
			return null;
		final String terminologyComponentId = getTerminolgyComponentId(object);
		return createIdentifierPair(component, terminologyComponentId);
	}
	
	private ComponentIdentifierPair<String> createIdentifierPair(final IComponent<?> component, final String terminologyComponentId) {
		return ComponentIdentifierPair.<String>create(terminologyComponentId, String.valueOf(component.getId()));
	}
	private String getTerminolgyComponentId(final Object object) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(object);
	}
	
	public Collection<Description> getDirtyDescriptions() {
		return Collections.unmodifiableCollection(descriptionsToUpdate);
	}
	
	public void addDirtyDescription(Description... descriptions) {
		descriptionsToUpdate.addAll(Sets.newHashSet(descriptions));
	}
}