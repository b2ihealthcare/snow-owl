/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.match;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchAnyLong;
import static com.b2international.index.query.Expressions.matchRange;
import static com.b2international.index.query.Expressions.matchTextAll;
import static com.b2international.index.query.Expressions.matchTextPhrase;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.Text;
import com.b2international.index.WithScore;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @since 7.0
 */
@Doc
@JsonDeserialize(builder = Commit.Builder.class)
public final class Commit implements WithScore {

	public static final Long NO_COMMIT_TIMESTAMP = -1L;

	static Builder builder() {
		return new Builder();
	}

	/**
	 * @since 7.0
	 */
	@JsonPOJOBuilder(withPrefix = "")
	static final class Builder {

		private String id;
		private String branch;
		private String author;
		private String comment;
		private long timestamp;
		private List<CommitDetail> details;
		private String groupId;
		private RevisionBranchPoint mergeSource;
		private Boolean squashMerge;

		public Builder id(final String id) {
			this.id = id;
			return this;
		}

		public Builder branch(final String branch) {
			this.branch = branch;
			return this;
		}

		public Builder author(final String author) {
			this.author = author;
			return this;
		}

		public Builder comment(final String comment) {
			this.comment = comment;
			return this;
		}

		public Builder timestamp(final long timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public Builder groupId(String groupId) {
			this.groupId = groupId;
			return this;
		}
		
		public Builder details(final List<CommitDetail> details) {
			this.details = details;
			return this;
		}
		
		public Builder mergeSource(final RevisionBranchPoint mergeSource) {
			this.mergeSource = mergeSource;
			return this;
		}
		
		public Builder squashMerge(Boolean squashMerge) {
			this.squashMerge = squashMerge;
			return this;
		}
		
		public Commit build() {
			return new Commit(id, branch, author, comment, timestamp, groupId, details, mergeSource, squashMerge);
		}

	}
	
	/**
	 * @since 7.0
	 */
	public static final class Expressions {
		
		private Expressions() {}
		
		public static final Expression id(String id) {
			return DocumentMapping.matchId(id);
		}
		
		public static final Expression ids(Collection<String> ids) {
			return matchAny(DocumentMapping._ID, ids);
		}
		
		public static Expression branches(final String...branchPaths) {
			return branches(Arrays.asList(branchPaths));
		}
		
		public static Expression branches(final Iterable<String> branchPaths) {
			return matchAny(Fields.BRANCH, branchPaths);
		}
		
		public static Expression author(final String author) {
			return exactMatch(Fields.AUTHOR, author);
		}
		
		public static Expression exactComment(final String comment) {
			return matchTextPhrase(Fields.COMMENT, comment);
		}
		
		public static Expression allCommentPrefixesPresent(final String comment) {
			return matchTextAll(Fields.COMMENT_PREFIX, comment);
		}
		
		public static Expression timestamp(final long timeStamp) {
			return exactMatch(Fields.TIMESTAMP, timeStamp);
		}

		public static Expression timestamps(final Iterable<Long> timeStamps) {
			return matchAnyLong(Fields.TIMESTAMP, timeStamps);
		}
		
		public static Expression timestampRange(final long from, final long to) {
			return matchRange(Fields.TIMESTAMP, from, to);
		}

		public static Expression affectedObject(String objectId) {
			return com.b2international.index.query.Expressions.builder()
					.should(exactMatch(Fields.DETAILS_OBJECT, objectId))
					.should(exactMatch(Fields.DETAILS_COMPONENT, objectId))
					.build();
		}

		public static Expression mergeFrom(long branchId, long mergeSourceTimestampStart, long mergeSourceTimestampEnd, boolean squash) {
			return com.b2international.index.query.Expressions.builder()
					.filter(match("squashMerge", squash))
					.filter(matchRange("mergeSource", RevisionBranchPoint.toIpv6(branchId, mergeSourceTimestampStart), RevisionBranchPoint.toIpv6(branchId, mergeSourceTimestampEnd), true, true))
					.build();
		}
		
	}
	
	/**
	 * @since 7.0
	 */
	public static final class Fields {
		public static final String BRANCH = "branch";
		public static final String AUTHOR = "author";
		public static final String COMMENT = "comment";
		public static final String COMMENT_PREFIX = "comment.prefix";
		public static final String TIMESTAMP = "timestamp";
		public static final String GROUP_ID = "groupId";
		private static final String DETAILS_OBJECT = "details.objects";
		private static final String DETAILS_COMPONENT = "details.components";
		// Sort keys
		public static final Set<String> ALL = ImmutableSet.of(BRANCH, AUTHOR, TIMESTAMP);
	}

	private final String id;
	private final String branch;
	private final String author;
	@Text(analyzer=Analyzers.TOKENIZED)
	@Text(alias="prefix", analyzer=Analyzers.PREFIX, searchAnalyzer=Analyzers.TOKENIZED)
	private final String comment;
	private final long timestamp;
	private final String groupId;
	private final List<CommitDetail> details;
	private final RevisionBranchPoint mergeSource;
	private final Boolean squashMerge;
	
	private float score = 0.0f;
	
	@JsonIgnore
	private transient Multimap<String, CommitDetail> detailsByObject;
	
	private Commit(
			final String id,
			final String branch,
			final String author,
			final String comment,
			final long timestamp,
			final String groupId,
			final List<CommitDetail> details, 
			final RevisionBranchPoint mergeSource,
			final Boolean squashMerge) {
		this.id = id;
		this.branch = branch;
		this.author = author;
		this.comment = comment;
		this.timestamp = timestamp;
		this.groupId = groupId;
		this.mergeSource = mergeSource;
		this.squashMerge = squashMerge;
		this.details = Collections3.toImmutableList(details);
	}

	public String getId() {
		return id;
	}
	
	@Override
	public void setScore(float score) {
		this.score = score;
	}

	@Override
	@JsonIgnore
	public float getScore() {
		return score;
	}

	public String getBranch() {
		return branch;
	}

	public String getAuthor() {
		return author;
	}

	public String getComment() {
		return comment;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public List<CommitDetail> getDetails() {
		return details;
	}
	
	public RevisionBranchPoint getMergeSource() {
		return mergeSource;
	}
	
	public Boolean getSquashMerge() {
		return squashMerge;
	}
	
	@JsonIgnore
	public boolean isMergeCommit() {
		return mergeSource != null;
	}
	
	public Collection<CommitDetail> getDetailsByObject(String objectId) {
		if (detailsByObject == null) {
			detailsByObject = ArrayListMultimap.create();
		}
		if (!detailsByObject.containsKey(objectId)) {
			final List<CommitDetail> detailsForObject = newArrayList();
			
			final Set<ObjectId> visited = newHashSet();
			final Deque<ObjectId> toCheck = new ArrayDeque<>(); 
						
			/* 
			 * First pass: find non-property changes where objectId is an object or a component,
			 * or property changes where objectId itself changed 
			 */
			for (final CommitDetail detail : details) {
				if (detail.isPropertyChange() && detail.getObjects().contains(objectId)) {
					// Register objectId as visited, but don't add it to the queue
					visited.add(ObjectId.of(detail.getObjectType(), objectId));

					detailsForObject.add(CommitDetail.changedProperty(detail.getProp(), 
							detail.getFrom(), 
							detail.getTo(), 
							detail.getObjectType(), 
							Collections.singletonList(objectId)));
					
					continue; // jump to next CommitDetail
				}
				
				// Is objectId a container?
				int objectIdx = detail.getObjects().indexOf(objectId);
				if (objectIdx >= 0) {
					// Register objectId as visited, but don't add it to the queue
					visited.add(ObjectId.of(detail.getObjectType(), objectId));
					
					final Set<String> componentIds = detail.getComponents().get(objectIdx);
					for (final String componentId : componentIds) {
						// Record child component as an item that needs to be checked for contained changes
						ObjectId childId = ObjectId.of(detail.getComponentType(), componentId);
						if (visited.add(childId)) {
							toCheck.add(childId);
						}
					}
					
					final CommitDetail filteredDetail = new CommitDetail.Builder()
							.op(detail.getOp())
							.objectType(detail.getObjectType())
							.componentType(detail.getComponentType())
							.putObjects(objectId, componentIds)
							.build();

					detailsForObject.add(filteredDetail);
					continue; // jump to next CommitDetail
				}
				
				final List<Set<String>> components = detail.getComponents();
				if (components == null) {
					continue; // jump to next CommitDetail
				}
				
				// Is objectId a contained object?
				final int componentSize = components.size();
				for (objectIdx = 0; objectIdx < componentSize; objectIdx++) {
					final Set<String> componentIds = components.get(objectIdx);
					
					if (componentIds.contains(objectId)) {
						String containerId = detail.getObjects().get(objectIdx);
						
						// Register objectId as visited, but don't add it to the queue
						visited.add(ObjectId.of(detail.getComponentType(), objectId));
						
						final CommitDetail filteredDetail = new CommitDetail.Builder()
								.op(detail.getOp())
								.objectType(detail.getObjectType())
								.componentType(detail.getComponentType())
								.putObjects(containerId, Collections.singleton(objectId))
								.build();

						detailsForObject.add(filteredDetail);
						break; // exit objectIdx loop
					}
				}
			}
			
			// Second pass: follow containment chains and collect relevant object property changes as well
			while (!toCheck.isEmpty()) {
				final ObjectId childId = toCheck.removeFirst();
				
				for (final CommitDetail detail : details) {
					if (detail.isPropertyChange()
							&& detail.getObjectType().equals(childId.type())
							&& detail.getObjects().contains(childId.id())) {
						
						detailsForObject.add(CommitDetail.changedProperty(detail.getProp(), 
								detail.getFrom(), 
								detail.getTo(), 
								detail.getObjectType(), 
								Collections.singletonList(childId.id())));
						
						continue; // jump to next CommitDetail
					}
					
					// Is childId a container?
					if (!detail.getObjectType().equals(childId.type())) {
						continue; // jump to next CommitDetail
					}
					
					int childIdx = detail.getObjects().indexOf(childId.id());
					if (childIdx >= 0) {
						final Set<String> componentIds = detail.getComponents().get(childIdx);
						for (final String componentId : componentIds) {
							// Record child component as an item that needs to be checked for contained changes
							ObjectId descendantId = ObjectId.of(detail.getComponentType(), componentId);
							if (visited.add(descendantId)) {
								toCheck.add(descendantId);
							}
						}
						
						final CommitDetail filteredDetail = new CommitDetail.Builder()
								.op(detail.getOp())
								.objectType(detail.getObjectType())
								.componentType(detail.getComponentType())
								.putObjects(childId.id(), componentIds)
								.build();

						detailsForObject.add(filteredDetail);
						continue; // jump to next CommitDetail
					}
					
					/* 
					 * We don't check if childId is contained somewhere else in this pass,
					 * as it would lead to duplicate detail objects 
					 */
				}
			}
			
			detailsByObject.putAll(objectId, detailsForObject);
		}
		
		return detailsByObject.get(objectId);
	}
}
