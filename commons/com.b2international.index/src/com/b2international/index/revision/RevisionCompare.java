/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.b2international.commons.collections.Collections3;

/**
 * @since 5.0
 */
public final class RevisionCompare {

	static Builder builder(RevisionBranchRef base, RevisionBranchRef compare, RevisionCompareOptions options) {
		return new Builder(base, compare, options);
	}
	
	static class Builder {
		
		private final RevisionBranchRef base;
		private final RevisionBranchRef compare;
		
		private final TreeMap<String, RevisionCompareDetail> detailsByComponent = new TreeMap<>();
		private final RevisionCompareOptions options;
		
		Builder(RevisionBranchRef base, RevisionBranchRef compare, RevisionCompareOptions options) {
			this.base = base;
			this.compare = compare;
			this.options = options;
		}
		
		public Builder apply(Commit commit) {
			for (CommitDetail detail : commit.getDetails()) {
								
				List<String> objects = detail.getObjects();
				// XXX index order is required to collect corresponding component array from details
				for (int i = 0; i < objects.size(); i++) {
					String object = objects.get(i);
					final ObjectId objectId = ObjectId.of(detail.getObjectType(), object);
					
					// if the main object is not selected via type filters then skip
					if (options.getTypes() != null && !options.getTypes().contains(objectId.type())) {
						continue;
					}
					
					// if main object is not the root object and it is not selected via id filter then skip
					if (!objectId.isRoot() && options.getIds() != null && !options.getIds().contains(objectId.id())) {
						continue;
					}
					
					final List<RevisionCompareDetail> details;
					if (detail.isPropertyChange()) {
						
						// ignore property change if an existing ADD detail has been added for the component
						RevisionCompareDetail existingObjectDetail = detailsByComponent.get(objectId.toString());
						if (existingObjectDetail != null && existingObjectDetail.isAdd()) {
							details = Collections.emptyList();
						} else {
							details = Collections.singletonList(
									RevisionCompareDetail.propertyChange(
											detail.getOp(), 
											objectId, 
											detail.getProp(), 
											detail.getFrom(), detail.getTo()));
						}
					} else if (
						// include component change list if, it is either an ADD or REMOVE list
						!detail.isChange()
						// or include if it is a component change list for a non-root component and it was requested explicitly
						|| (!objectId.isRoot() && options.isIncludeComponentChanges())
						// or include if it is a component change list for a root component (usually derived data change markers)
						|| (objectId.isRoot() && options.isIncludeDerivedComponentChanges())
					) {
						details = detail.getComponents()
								.get(i)
								.stream()
								.filter(component -> !objectId.isRoot() || options.getIds() == null || options.getIds().contains(component))
								.map(component -> RevisionCompareDetail.componentChange(detail.getOp(), objectId, ObjectId.of(detail.getComponentType(), component)))
								.collect(Collectors.toList());
					} else {
						details = Collections.emptyList();
					}
					
					details.forEach(compareDetail -> {
						// if a REMOVED detail comes for a component, delete all previously registered property changes, ADD will be handled by the merge operation
						if (compareDetail.isComponentChange() && compareDetail.isRemove()) {
							final String propChangeKey = compareDetail.key() + RevisionCompareDetail.PROPERTY_CHANGE_KEY_SEPARATOR;
							Iterator<Entry<String, RevisionCompareDetail>> followingCompareDetails = detailsByComponent.tailMap(propChangeKey, true).entrySet().iterator();
							while (followingCompareDetails.hasNext()) {
								Entry<String, RevisionCompareDetail> followingCompareDetail = followingCompareDetails.next();
								if (followingCompareDetail.getValue().isPropertyChange() && followingCompareDetail.getKey().startsWith(propChangeKey)) {
									followingCompareDetails.remove();
								} else {
									break;
								}
							}
						}
						detailsByComponent.merge(compareDetail.key(), compareDetail, (oldV, newV) -> oldV.merge(newV));
					});
				}
			}
			return this;
		}
		
		public RevisionCompare build() {
			// count changes only once at the end
			int added = 0;
			int changed = 0;
			int removed = 0;
			for (RevisionCompareDetail compareDetail : detailsByComponent.values()) {
				switch (compareDetail.getOp()) {
				case ADD:
					added++;
					break;
				case CHANGE:
					// count only property changes
					if (compareDetail.isPropertyChange()) {
						changed++;
					}
					break;
				case REMOVE:
					removed++;
					break;
				}
			}
			
			final List<RevisionCompareDetail> details = detailsByComponent.values().stream().limit(options.getLimit()).collect(Collectors.toUnmodifiableList());
			
			return new RevisionCompare(
				base, 
				compare,
				details,
				added,
				changed,
				removed
			);
		}
		
	}
	
	private final RevisionBranchRef base;
	private final RevisionBranchRef compare;
	private final List<RevisionCompareDetail> details;
	private final int totalAdded;
	private final int totalChanged;
	private final int totalRemoved;

	private RevisionCompare(RevisionBranchRef base,	RevisionBranchRef compare, List<RevisionCompareDetail> details, int totalAdded, int totalChanged, int totalRemoved) {
		this.base = base;
		this.compare = compare;
		this.details = Collections3.toImmutableList(details);
		this.totalAdded = totalAdded;
		this.totalChanged = totalChanged;
		this.totalRemoved = totalRemoved;
	}

	public RevisionBranchRef getBase() {
		return base;
	}
	
	public RevisionBranchRef getCompare() {
		return compare;
	}
	
	public List<RevisionCompareDetail> getDetails() {
		return details;
	}
	
	public int getTotalAdded() {
		return totalAdded;
	}
	
	public int getTotalChanged() {
		return totalChanged;
	}
	
	public int getTotalRemoved() {
		return totalRemoved;
	}
	
}
