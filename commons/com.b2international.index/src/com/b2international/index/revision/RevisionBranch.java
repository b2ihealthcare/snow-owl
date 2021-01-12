/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataHolderImpl;
import com.b2international.index.Doc;
import com.b2international.index.ID;
import com.b2international.index.Script;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.primitives.Longs;

/**
 * @since 6.0
 */
@Doc(type="branch")
@JsonDeserialize(builder=RevisionBranch.Builder.class)
@Script(name=RevisionBranch.Scripts.COMMIT, script=""
		+ "if (params.mergeSources != null) {"
		+ "    ctx._source.mergeSources.add(['timestamp': params.headTimestamp, 'branchPoints': params.mergeSources, 'squash': params.squash]);"
		+ "}"
		+ "boolean found = false;"
		+ "for (segment in ctx._source.segments) {"
		+ "    if (segment.branchId == ctx._source.id) {"
		+ "        segment.end = params.headTimestamp;"
		+ "        found = true;"
		+ "    }"
		+ "}"
		+ "if (!found) {"
		+ "    throw new RuntimeException(\"Missing branch segment\")"
		+ "}")
@Script(name=RevisionBranch.Scripts.WITH_DELETED, script="ctx._source.deleted = true")
@Script(name=RevisionBranch.Scripts.WITH_METADATA, script="ctx._source.metadata = params.metadata")
public final class RevisionBranch extends MetadataHolderImpl {

	/**
	 * @since 6.5
	 */
	public static enum BranchState {
		UP_TO_DATE, FORWARD, BEHIND, DIVERGED
	}
	
	/**
	 * Allowed set of characters for a branch name.
	 */
	public static final String DEFAULT_ALLOWED_BRANCH_NAME_CHARACTER_SET = "a-zA-Z0-9.~_-";

	/**
	 * The maximum length of a branch.
	 */
	public static final int DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH = 50;

	/**
	 * Temporary branch name format. Values are prefix, name, current time. 
	 */
	public static final String TEMP_BRANCH_NAME_FORMAT = "%s%s_%s";
	
	/**
	 * The path of the main branch.
	 */
	public static final String MAIN_PATH = "MAIN";
	
	/**
	 * Segment separator in {@link RevisionBranch#getPath()} values.
	 */
	public static final String SEPARATOR = "/";

	/**
	 * @since 7.0
	 */
	public static final class Fields {
		public static final String ID = "id";
		public static final String PATH = "path";
		public static final String NAME = "name";
		public static final String PARENT_PATH = "parentPath";
	}
	
	/**
	 * @since 6.5
	 */
	public static interface BranchNameValidator {

		BranchNameValidator DEFAULT = new BranchNameValidatorImpl();

		/**
		 * Validates a branch name and throws {@link BadRequestException} if not valid.
		 * 
		 * @param name
		 * @throws BadRequestException
		 */
		void checkName(String name) throws BadRequestException;

		/**
		 * @since 6.5
		 */
		class BranchNameValidatorImpl implements BranchNameValidator {

			private Pattern pattern;
			private String allowedCharacterSet;
			private int maximumLength;

			public BranchNameValidatorImpl() {
				this(DEFAULT_ALLOWED_BRANCH_NAME_CHARACTER_SET, DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH);
			}

			public BranchNameValidatorImpl(String allowedCharacterSet, int maximumLength) {
				this.allowedCharacterSet = allowedCharacterSet;
				this.maximumLength = maximumLength;
				pattern = Pattern.compile(String.format("^[%s]{1,%s}(_[0-9]{1,19})?$", allowedCharacterSet, maximumLength));
			}

			@Override
			public void checkName(String name) {
				if (Strings.isNullOrEmpty(name)) {
					throw new BadRequestException("Name cannot be empty");
				}
				if (!pattern.matcher(name).matches()) {
					throw new BadRequestException("'%s' is either too long (max %s characters) or it contains invalid characters (only '%s' characters are allowed).", name,
							maximumLength, allowedCharacterSet);
				}
			}

		}

	}
	
	/**
	 * Scripts that can be used to update {@link RevisionBranch} documents.
	 */
	protected static final class Scripts {
		/**
		 * @param headTimestamp - the new headTimestamp value of this branch
		 */
		public static final String COMMIT = "commit";
		public static final String WITH_DELETED = "withDeleted";
		public static final String WITH_METADATA = "withMetadata";
		public static final String WITH_MERGE_SOURCE = "withMergeSource";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(RevisionBranch from) {
		return builder()
				.id(from.getId())
				.name(from.getName())
				.parentPath(from.getParentPath())
				.metadata(from.metadata())
				.deleted(from.isDeleted())
				.segments(from.getSegments())
				.mergeSources(from.getMergeSources());
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder {
		
		private long id;
		private String parentPath;
		private String name;
		private boolean deleted;
		private Metadata metadata;
		private SortedSet<RevisionSegment> segments;
		private List<RevisionBranchMergeSource> mergeSources;
		
		Builder() {}
		
		public Builder id(long id) {
			this.id = id;
			return this;
		}
		
		public Builder parentPath(String parentPath) {
			this.parentPath = parentPath;
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder deleted(boolean deleted) {
			this.deleted = deleted;
			return this;
		}
		
		public Builder metadata(Metadata metadata) {
			this.metadata = metadata;
			return this;
		}
		
		public Builder segments(SortedSet<RevisionSegment> segments) {
			this.segments = segments;
			return this;
		}
		
		public Builder mergeSources(List<RevisionBranchMergeSource> mergeSources) {
			this.mergeSources = mergeSources;
			return this;
		}
		
		Builder path(String path) {
			return this;
		}
		
		public RevisionBranch build() {
			return new RevisionBranch(
					id,
					parentPath, 
					name, 
					deleted, 
					metadata, 
					segments,
					mergeSources);
		}
		
	}
	
	private final long id;
	private final String name;
    private final String parentPath;
    
    @ID
    private final String path;
    private final boolean deleted;
	private final SortedSet<RevisionSegment> segments;
	private final List<RevisionBranchMergeSource> mergeSources;

    private RevisionBranch(
    		long id,
    		String parentPath, 
    		String name, 
    		boolean deleted, 
    		Metadata metadata,
    		SortedSet<RevisionSegment> segments, 
    		List<RevisionBranchMergeSource> mergeSources) {
    	super(metadata);
    	BranchNameValidator.DEFAULT.checkName(name);
    	checkArgument(!CompareUtils.isEmpty(segments), "At least one segment is required to created a revision branch.");
    	this.id = id;
		this.parentPath = parentPath;
		this.name = name;
		this.path = CompareUtils.isEmpty(parentPath) ? name : String.join(SEPARATOR, parentPath, name);
		this.deleted = deleted;
		this.segments = ImmutableSortedSet.copyOf(segments);
		this.mergeSources = Collections3.toImmutableList(mergeSources);
	}
    
    /**
     * Returns the unique integer ID of this {@link RevisionBranch}.
     * @return
     */
    public long getId() {
    	return id;
    }
    
    /**
	 * Returns the unique path of this {@link RevisionBranch}.
	 * 
	 * @return
	 */
    public String getPath() {
		return path;
	}
    
    /**
	 * Returns the unique path of the parent of this {@link RevisionBranch}.
	 * 
	 * @return
	 */
    public String getParentPath() {
		return parentPath;
	}
    
    /**
	 * Returns the name of the {@link RevisionBranch}, which is often the same value as the last segment of the {@link #getPath()}.
	 * 
	 * @return
	 */
    public String getName() {
		return name;
	}
    
    /**
	 * Returns the base timestamp value of this {@link RevisionBranch}. The base timestamp represents the time when this branch has been created, or branched
	 * of from its parent.
	 * 
	 * @return
	 */
    @JsonIgnore
    public long getBaseTimestamp() {
		return getSegment(getId()).start();
	}
    
    /**
	 * Returns the head timestamp value for this {@link RevisionBranch}. The head timestamp represents the time when the last commit arrived on this
	 * {@link RevisionBranch}.
	 * 
	 * @return
	 */
    @JsonIgnore
    public long getHeadTimestamp() {
		return getSegment(getId()).end();
	}
    
    private RevisionSegment getSegment(long branchId) {
		return segments.stream().filter(segment -> segment.branchId() == branchId).findFirst().get();
	}

	/**
	 * @return whether this branch is deleted or not
	 */
    public boolean isDeleted() {
		return deleted;
	}
    
    /**
     * Returns the segments that are visible when querying revisions on this branch.
     * @return
     */
    public SortedSet<RevisionSegment> getSegments() {
    	return segments;
    }
    
    /**
     * Returns the current merge sources. A merge source represents a set of new {@link RevisionBranchPoint}s introduced by a merge commit on this branch.
	 * 
	 * @return
	 */
    public List<RevisionBranchMergeSource> getMergeSources() {
		return mergeSources;
	}
    
    /**
     * Returns the segments common with the parent branch.
     * @return
     */
    @JsonIgnore
    public SortedSet<RevisionSegment> getParentSegments() {
    	return getSegments().headSet(getHead());
    }

	private RevisionSegment getHead() {
		return getSegments().last();
	}

    @JsonIgnore
	public RevisionBranchRef ref() {
    	final Map<Long, RevisionBranchPoint> latestMergeSources = getLatestMergeSources(false);
    	// extend segments with the latest merge timestamp to access all revisions
    	final SortedSet<RevisionSegment> visibleSegments = getSegments().stream()
    			.map(segment -> {
    				RevisionBranchPoint latestMergeSource = latestMergeSources.remove(segment.branchId());
    				if (latestMergeSource != null && latestMergeSource.getTimestamp() > segment.end()) {
    					return segment.withEnd(latestMergeSource.getTimestamp());
    				} else {
    					return segment;
    				}
    			})
    			.collect(Collectors.toCollection(TreeSet::new));
    	
    	// add all remaining merge sources to the visible segment list
    	latestMergeSources.values().forEach(latestMergeSource -> {
    		// TODO start timestamp???
    		visibleSegments.add(new RevisionSegment(latestMergeSource.getBranchId(), 0L, latestMergeSource.getTimestamp()));
    	});
    	
		return new RevisionBranchRef(getId(), getPath(), visibleSegments);
	}

    @JsonIgnore
	public RevisionBranchRef baseRef() {
		final SortedSet<RevisionSegment> parentSegments = getParentSegments().stream()
    			.map(segment -> {
    				RevisionBranchPoint latestMergeSource = getLatestMergeSource(segment.branchId(), false);
    				if (latestMergeSource != null && latestMergeSource.getTimestamp() > segment.end()) {
    					return segment.withEnd(latestMergeSource.getTimestamp());
    				} else {
    					return segment;
    				}
    			})
    			.collect(Collectors.toCollection(TreeSet::new));
		return new RevisionBranchRef(parentSegments.last().branchId(), getParentPath(), parentSegments);
	}
    
    /**
     * @return whether this branch is the MAIN branch or a child branch.
     */
    @JsonIgnore
    public boolean isMain() {
		return MAIN_PATH.equals(path);
	}
    
	RevisionBranchPoint getLatestMergeSource(long branchToFind, boolean withSquashMerges) {
		return getLatestMergeSources(withSquashMerges).get(branchToFind);
	}

	private Map<Long, RevisionBranchPoint> getLatestMergeSources(boolean withSquashMerges) {
		final Map<Long, RevisionBranchPoint> latestMergeSources = newHashMap();
		getMergeSources()
			.stream()
			.filter(ms -> withSquashMerges || !ms.isSquash()) // skip squash merge sources when not required
			.flatMap(ms -> ms.getBranchPoints().stream())
			.sorted((p1, p2) -> -1 * Longs.compare(p1.getTimestamp(), p2.getTimestamp()))
			.forEach(branchPoint -> {
				if (!latestMergeSources.containsKey(branchPoint.getBranchId())) {
					latestMergeSources.put(branchPoint.getBranchId(), branchPoint);
				}
			});
		return latestMergeSources;
	}

	/**
	 * A branch is considered empty when the base and head timestamps point to the same branch point.
	 * @return
	 */
	@JsonIgnore
	public boolean isEmpty() {
		return getBaseTimestamp() == getHeadTimestamp();
	}

	/**
	 * Returns the intersection {@link RevisionBranchRef} of this {@link RevisionBranch} and the given other {@link RevisionBranch}.
	 * 
	 * @param other - the branch to use for computing the intersection
	 * @return
	 */
	public RevisionBranchRef intersection(RevisionBranch other) {
		return ref().intersection(other.ref());
	}

	/**
	 * Returns the difference {@link RevisionBranchRef} of this {@link RevisionBranch} and the given other {@link RevisionBranch}.
	 * 
	 * @param other - the branch to use for computing the difference
	 * @return
	 */
	public RevisionBranchRef difference(RevisionBranch other) {
		return ref().difference(other.ref());
	}

	public RevisionBranch.Builder toBuilder() {
		return builder(this);
	}

}
