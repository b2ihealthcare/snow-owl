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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.options.Metadata;
import com.b2international.index.Doc;
import com.b2international.index.Script;
import com.b2international.index.WithId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

/**
 * @since 6.0
 */
@Doc(type="branch")
@JsonDeserialize(builder=RevisionBranch.Builder.class)
@Script(name=RevisionBranch.Scripts.WITH_HEADTIMESTAMP, script="ctx._source.headTimestamp = params.headTimestamp")
@Script(name=RevisionBranch.Scripts.WITH_DELETED, script="ctx._source.deleted = true")
@Script(name=RevisionBranch.Scripts.WITH_METADATA, script="ctx._source.metadata = params.metadata")
@Script(name=RevisionBranch.Scripts.REPLACE, script="ctx._source = params.replace")
@Script(name=RevisionBranch.Scripts.WITH_SEGMENTID, script=""
		+ "ctx._source.segmentId = params.segmentId;"
		+ "if (!ctx._source.segments.contains(params.segmentId)) {"
		+ "    ctx._source.segments.add(params.segmentId);"
		+ "}")
public final class RevisionBranch implements WithId {

	public static final String MAIN_PATH = "MAIN";
	public static final String SEPARATOR = "/";
	
	public static final class Scripts {
		public static final String WITH_HEADTIMESTAMP = "withHeadTimestamp";
		public static final String WITH_DELETED = "withDeleted";
		public static final String WITH_METADATA = "withMetadata";
		public static final String REPLACE = "replace";
		public static final String WITH_SEGMENTID = "withSegmentId";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder {
		
		private String type;
		private String parentPath;
		private String name;
		private String path;
		private long baseTimestamp;
		private long headTimestamp;
		private boolean deleted;
		private Metadata metadata;
		@Deprecated
		private int cdoBranchId;
		private int segmentId;
		private Set<Integer> segments;
		private Set<Integer> parentSegments;
		
		Builder() {}
		
		public Builder type(String type) {
			this.type = type;
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
		
		public Builder path(String path) {
			this.path = path;
			return this;
		}
		
		public Builder baseTimestamp(long baseTimestamp) {
			this.baseTimestamp = baseTimestamp;
			return this;
		}
		
		public Builder headTimestamp(long headTimestamp) {
			this.headTimestamp = headTimestamp;
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
		
		@Deprecated
		public Builder cdoBranchId(int cdoBranchId) {
			this.cdoBranchId = cdoBranchId;
			return this;
		}
		
		public Builder segmentId(int segmentId) {
			this.segmentId = segmentId;
			return this;
		}
		
		public Builder segments(Iterable<Integer> segments) {
			this.segments = Collections3.toImmutableSet(segments);
			return this;
		}
		
		public Builder parentSegments(Iterable<Integer> parentSegments) {
			this.parentSegments = Collections3.toImmutableSet(parentSegments);
			return this;
		}
		
		public RevisionBranch build() {
			return new RevisionBranch(
					type, 
					parentPath, 
					name, 
					path, 
					baseTimestamp, 
					headTimestamp, 
					deleted, 
					metadata, 
					cdoBranchId, 
					segmentId, 
					segments, 
					parentSegments);
		}
		
	}
	
	private String _id;
	
	private final String type;
	private final String name;
    private final String parentPath;
    private final String path;
    private final long baseTimestamp;
    private final long headTimestamp;
    private final boolean deleted;
	private final Metadata metadata;
	
	@Deprecated
	private final int cdoBranchId;
	private final int segmentId;
	private final Set<Integer> segments;
	private final Set<Integer> parentSegments;

    private RevisionBranch(String type, 
    		String parentPath, 
    		String name, 
    		String path, 
    		long baseTimestamp, 
    		long headTimestamp, 
    		boolean deleted, 
    		Metadata metadata,
    		int cdoBranchId,
    		int segmentId,
    		Set<Integer> segments,
    		Set<Integer> parentSegments) {
		this.type = checkNotNull(type, "Type cannot be null");
		this.parentPath = parentPath;
		this.name = name;
		this.path = path;
		this.baseTimestamp = baseTimestamp;
		this.headTimestamp = headTimestamp;
		this.deleted = deleted;
		this.metadata = metadata;
		this.cdoBranchId = cdoBranchId;
		this.segmentId = segmentId;
		this.segments = segments;
		this.parentSegments = parentSegments;
	}
	
    @Override
    public String _id() {
    	return _id;
    }
    
    @Override
    public void set_id(String _id) {
    	this._id = _id;
    }
    
    public String getType() {
		return type;
	}
    
    public String getPath() {
		return path;
	}
    
    public String getParentPath() {
		return parentPath;
	}
    
    public long getBaseTimestamp() {
		return baseTimestamp;
	}
    
    public long getHeadTimestamp() {
		return headTimestamp;
	}
    
    public String getName() {
		return name;
	}
    
    public boolean isDeleted() {
		return deleted;
	}

    public Metadata getMetadata() {
		return metadata;
	}
    
    @Deprecated
    public int getCdoBranchId() {
		return cdoBranchId;
	}
    
    public int getSegmentId() {
		return segmentId;
	}
    
    public Set<Integer> getSegments() {
		return segments;
	}
    
    public Set<Integer> getParentSegments() {
		return parentSegments;
	}
    
    @JsonIgnore
    public RevisionBranchSegments getRevisionBranchSegments() {
    	return new RevisionBranchSegments(path, segmentId, ImmutableSet.<Integer>builder().addAll(segments).addAll(parentSegments).build());
    }
    
    @JsonIgnore
    public RevisionBranchSegments getParentRevisionBranchSegments() {
    	return new RevisionBranchSegments(parentPath, Ordering.natural().max(parentSegments), parentSegments);
    }
    
}
