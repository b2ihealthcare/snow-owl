/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.SortedSet;
import java.util.regex.Pattern;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataHolderImpl;
import com.b2international.index.Doc;
import com.b2international.index.Script;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 6.0
 */
@Doc(type="branch")
@JsonDeserialize(builder=RevisionBranch.Builder.class)
@Script(name=RevisionBranch.Scripts.WITH_HEADTIMESTAMP, script=""
		+ "for (segment in ctx._source.segments) {"
		+ "    if (segment.branchId == ctx._source.id) {"
		+ "        segment.end = params.headTimestamp;"
		+ "        return null;"
		+ "    }"
		+ "}"
		+ "throw new RuntimeException(\"Missing branch segment\")")
@Script(name=RevisionBranch.Scripts.WITH_DELETED, script="ctx._source.deleted = true")
@Script(name=RevisionBranch.Scripts.WITH_METADATA, script="ctx._source.metadata = params.metadata")
@Script(name=RevisionBranch.Scripts.REPLACE, script="ctx._source = params.replace")
public final class RevisionBranch extends MetadataHolderImpl {

	/**
	 * @since 6.5
	 */
	public static enum BranchState {
		UP_TO_DATE, FORWARD, BEHIND, DIVERGED, STALE
	}
	
	/**
	 * Allowed set of characters for a branch name.
	 */
	public static final String DEFAULT_ALLOWED_BRANCH_NAME_CHARACTER_SET = "a-zA-Z0-9_-";

	/**
	 * The maximum length of a branch.
	 */
	public static final int DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH = 50;

	/**
	 * Branch name prefix used for temporary branches during rebase.
	 */
	public static final String TEMP_PREFIX = "$";
	
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
	 * @since 6.5
	 */
	interface BranchNameValidator {

		BranchNameValidator DEFAULT = new BranchNameValidatorImpl();

		/**
		 * Validates a branch name and throws {@link IllegalArgumentException} if not valid.
		 * 
		 * @param name
		 * @throws IllegalArgumentException
		 */
		void checkName(String name) throws IllegalArgumentException;

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
				pattern = Pattern.compile(String.format("^(%s)?[%s]{1,%s}(_[0-9]{1,19})?$", Pattern.quote(TEMP_PREFIX), allowedCharacterSet, maximumLength));
			}

			@Override
			public void checkName(String name) {
				if (Strings.isNullOrEmpty(name)) {
					throw new IllegalArgumentException("Name cannot be empty");
				}
				if (!pattern.matcher(name).matches()) {
					throw new IllegalArgumentException(String.format(
							"'%s' is either too long (max %s characters) or it contains invalid characters (only '%s' characters are allowed).", name,
							maximumLength, allowedCharacterSet));
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
		public static final String WITH_HEADTIMESTAMP = "withHeadTimestamp";
		public static final String WITH_DELETED = "withDeleted";
		public static final String WITH_METADATA = "withMetadata";
		public static final String REPLACE = "replace";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder {
		
		private long id;
		private String parentPath;
		private String name;
		private boolean deleted;
		private Metadata metadata;
		private SortedSet<RevisionSegment> segments;
		
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
					segments);
		}
		
	}
	
	private final long id;
	private final String name;
    private final String parentPath;
    private final String path;
    private final boolean deleted;
	private final SortedSet<RevisionSegment> segments;

    private RevisionBranch(
    		long id,
    		String parentPath, 
    		String name, 
    		boolean deleted, 
    		Metadata metadata,
    		SortedSet<RevisionSegment> segments) {
    	super(metadata);
    	BranchNameValidator.DEFAULT.checkName(name);
    	checkArgument(!CompareUtils.isEmpty(segments), "At least one segment is required to created a revision branch.");
    	this.id = id;
		this.parentPath = parentPath;
		this.name = name;
		this.path = CompareUtils.isEmpty(parentPath) ? name : String.format("%s%s%s", parentPath, SEPARATOR, name);
		this.deleted = deleted;
		this.segments = ImmutableSortedSet.copyOf(segments);
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

    @JsonIgnore
	public RevisionBranchRef ref() {
		return new RevisionBranchRef(getId(), getPath(), getSegments());
	}

    @JsonIgnore
	public RevisionBranchRef baseRef() {
		return new RevisionBranchRef(getId(), getParentPath(), getSegments().headSet(getSegments().last()));
	}
    
    /**
	 * Returns the {@link BranchState} of this {@link RevisionBranch} compared to the given target {@link RevisionBranch}.
	 * TODO document how BranchState calculation works
	 * 
	 * @param target
	 * @return
	 */
    @JsonIgnore
	public BranchState state(RevisionBranch target) {
    	if (MAIN_PATH.equals(getPath())) {
    		throw new UnsupportedOperationException(getPath() + " cannot compute state compared to target " + target.getPath());
    	}
    	final long baseTimestamp = getBaseTimestamp();
    	final long headTimestamp = getHeadTimestamp();
    	final long targetBaseTimestamp = target.getBaseTimestamp();
    	final long targetHeadTimestamp = target.getHeadTimestamp();
		if (baseTimestamp < targetBaseTimestamp) {
        	return BranchState.STALE;
        } else if (headTimestamp > baseTimestamp && targetHeadTimestamp < baseTimestamp) {
        	return BranchState.FORWARD;
        } else if (headTimestamp == baseTimestamp && targetHeadTimestamp > baseTimestamp) {
        	return BranchState.BEHIND;
        } else if (headTimestamp > baseTimestamp && targetHeadTimestamp > baseTimestamp) {
        	return BranchState.DIVERGED;
        } else {
    	    return BranchState.UP_TO_DATE;
        }
    }
    
}
