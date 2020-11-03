/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

	static Builder builder(RevisionBranchRef base, RevisionBranchRef compare, int limit) {
		return new Builder(base, compare, limit);
	}
	
	static class Builder {
		
		private final RevisionBranchRef base;
		private final RevisionBranchRef compare;
		private int added;
		private int changed;
		private int removed;
		private final int limit;
	
		private final TreeMap<String, RevisionCompareDetail> detailsByComponent = new TreeMap<>();
		
		Builder(RevisionBranchRef base, RevisionBranchRef compare, int limit) {
			this.base = base;
			this.compare = compare;
			this.limit = limit;
		}
		
		public Builder apply(Commit commit) {
			for (CommitDetail detail : commit.getDetails()) {
				List<String> objects = detail.getObjects();
				for (int i = 0; i < objects.size(); i++) {
					String object = objects.get(i);
					final ObjectId objectId = ObjectId.of(detail.getObjectType(), object);
					
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
					} else {
						details = detail.getComponents()
								.get(i)
								.stream()
								.map(component -> RevisionCompareDetail.componentChange(detail.getOp(), objectId, ObjectId.of(detail.getComponentType(), component)))
								.collect(Collectors.toList());
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
			final List<RevisionCompareDetail> details = detailsByComponent.values().stream()
					.map(compareDetail -> {
						switch (compareDetail.getOp()) {
						case ADD:
							added++;
							break;
						case CHANGE:
							changed++;
							break;
						case REMOVE:
							removed++;
							break;
						}
						return compareDetail;
					})
					.limit(limit)
					.collect(Collectors.toUnmodifiableList());
			return new RevisionCompare(
					base, 
					compare,
					details,
					added,
					changed,
					removed);
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
