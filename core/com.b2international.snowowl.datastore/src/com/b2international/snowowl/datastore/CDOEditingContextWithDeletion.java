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
package com.b2international.snowowl.datastore;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.index.AbstractIndexEntry;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * {@link CDOEditingContext} extension for deletion related operations.
 * 
 * @since 2.7
 */
public abstract class CDOEditingContextWithDeletion extends CDOEditingContext {

	protected CDOEditingContextWithDeletion(EPackage ePackage) {
		super(ePackage);
	}
	
	protected CDOEditingContextWithDeletion(EPackage ePackage, final IBranchPath branchPath) {
		super(ePackage, branchPath);
	}
	
	/**
	 * Deletes the objects in the deletion plan from the database.
	 * 
	 * @param deletionPlan the {@link ComponentDeletionPlan} containing all the objects to delete
	 */
	public abstract void delete(ComponentDeletionPlan deletionPlan);
	
	/**
	 * Collects the indexes of the items in the deletion plan.
	 * @param deletionPlan the {@link ComponentDeletionPlan} contains all components to delete.
	 * @return a collection of indexes and items ordered reverse by the indexes.
	 */
	protected Collection<Entry<Integer,EObject>> getOrderedToDeleteIndexes(ComponentDeletionPlan deletionPlan) {
		final Multimap<Integer, EObject> itemMap = ArrayListMultimap.create();
		for (CDOObject item : deletionPlan.getDeletedItems()) {
			final int index = getIndex(item);
			if (index >= 0) {
				itemMap.put(index, item);
			}
		}
		return Ordering.from(new Comparator<Entry<Integer, EObject>>() {
			@Override
			public int compare(Entry<Integer, EObject> o1, Entry<Integer, EObject> o2) {
				return o1.getKey() - o2.getKey();
			}
		}).reverse().sortedCopy(itemMap.entries());
	}

	/**
	 * Returns the index of the given {@link EObject} in his containment list. 
	 * @param item
	 * @return
	 */
	protected int getIndex(CDOObject item) {
		if (item instanceof IComponent<?>) {
			return getIndexFromDatabase(item, item.cdoResource(), "ERESOURCE_CDORESOURCE_CONTENTS_LIST");
		} else {
			// if the item has eContainer then get the index from the eContainment feature
			if (null != item.eContainer()) {
				final EObject eContainer = item.eContainer();
				final int index = eContainer.eContents().indexOf(item);
				Preconditions.checkState(index > -1, "Cannot find item marked for deletion ' " + item + "' in its container " + eContainer);
				return index;
			}
		}
		return -1;
	}

	public abstract ComponentDeletionPlan getDeletionPlan(AbstractIndexEntry indexEntry);

}