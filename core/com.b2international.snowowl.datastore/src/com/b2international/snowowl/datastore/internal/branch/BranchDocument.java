/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal.branch;

import java.util.Collection;

import com.b2international.index.Doc;
import com.b2international.index.Script;
import com.b2international.index.WithId;
import com.b2international.snowowl.core.Metadata;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 6.0
 */
@Doc(type="branch")
@JsonDeserialize(builder=BranchDocument.Builder.class)
@Script(name=BranchDocument.Scripts.WITH_HEADTIMESTAMP, script="ctx._source.headTimestamp = params.headTimestamp")
@Script(name=BranchDocument.Scripts.WITH_DELETED, script="ctx._source.deleted = true")
@Script(name=BranchDocument.Scripts.WITH_METADATA, script="ctx._source.metadata = params.metadata")
@Script(name=BranchDocument.Scripts.REPLACE, script="ctx._source = params.replace")
@Script(name=BranchDocument.Scripts.WITH_SEGMENTID, script=""
		+ "ctx._source.segmentId = params.segmentId;"
		+ "if (!ctx._source.segments.contains(params.segmentId)) {"
		+ "    ctx._source.segments.add(params.segmentId);"
		+ "}")
public final class BranchDocument implements WithId {

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
		private int cdoBranchId;
		private int segmentId;
		private Collection<Integer> segments;
		private Collection<Integer> parentSegments;
		
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
		
		public Builder cdoBranchId(int cdoBranchId) {
			this.cdoBranchId = cdoBranchId;
			return this;
		}
		
		public Builder segmentId(int segmentId) {
			this.segmentId = segmentId;
			return this;
		}
		
		public Builder segments(Collection<Integer> segments) {
			this.segments = segments;
			return this;
		}
		
		public Builder parentSegments(Collection<Integer> parentSegments) {
			this.parentSegments = parentSegments;
			return this;
		}
		
		public BranchDocument build() {
			return new BranchDocument(
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
	
	private final int cdoBranchId;
	private final int segmentId;
	private final Collection<Integer> segments;
	private final Collection<Integer> parentSegments;

    private BranchDocument(String type, 
    		String parentPath, 
    		String name, 
    		String path, 
    		long baseTimestamp, 
    		long headTimestamp, 
    		boolean deleted, 
    		Metadata metadata,
    		int cdoBranchId,
    		int segmentId,
    		Collection<Integer> segments,
    		Collection<Integer> parentSegments) {
		this.type = type;
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
    
    public int getCdoBranchId() {
		return cdoBranchId;
	}
    
    public int getSegmentId() {
		return segmentId;
	}
    
    public Collection<Integer> getSegments() {
		return segments;
	}
    
    public Collection<Integer> getParentSegments() {
		return parentSegments;
	}
    
}
