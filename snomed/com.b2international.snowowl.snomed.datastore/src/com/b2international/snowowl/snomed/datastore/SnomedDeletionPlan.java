/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * A DTO about the projected outcome of a delete operation in SNOMED CT.
 */
public class SnomedDeletionPlan {

	private final List<String> rejectionReasons = new ArrayList<String>();
	
	// keep deletedItems sorted by type for nicer display to the user
	private final Set<CDOObject> deletedItems = new TreeSet<CDOObject>(ComponentUtils2.CDO_OBJECT_COMPARATOR);
	
	/** @return the reason why the delete plan is rejected, for example if a concept cannot be deleted once it was released. */
	public List<String> getRejectionReasons() {
		return rejectionReasons;
	}
	
	public void addRejectionReason(final String rejectionReason) {
		rejectionReasons.add(rejectionReason);
	}

	/** @return true if the requested plan cannot be executed because a concept or a relationship cannot be deleted */
	public boolean isRejected() {
		return rejectionReasons.size() > 0;
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
		deletedItems.addAll(items);
	}
	
	@Override
	public String toString() {
		return rejectionReasons.size() < 2 ? Iterables.getFirst(rejectionReasons, "") : Joiner.on(", ").join(rejectionReasons);
	}
}