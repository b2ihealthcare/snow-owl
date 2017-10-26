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
package com.b2international.snowowl.datastore.server.internal.branch;

import java.util.Collection;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataHolderMixin;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @since 4.1
 */
@JsonTypeInfo(
		defaultImpl = CDOBranchImpl.class,
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "type")
@JsonSubTypes(
	@JsonSubTypes.Type(value = CDOMainBranchImpl.class)
)
public abstract class CDOBranchImplMixin implements MetadataHolderMixin {

	@JsonCreator
	CDOBranchImplMixin(
			@JsonProperty("name") String name, 
			@JsonProperty("parentPath") String parentPath, 
			@JsonProperty("baseTimestamp") long baseTimestamp,
			@JsonProperty("headTimestamp") long headTimestamp, 
			@JsonProperty("deleted") boolean deleted,
			@JsonProperty("metadata") Metadata metadata,
			@JsonProperty("cdobranchId") int cdoBranchId, 
			@JsonProperty("segmentId") int segmentId,
			@JsonProperty("segments") Collection<Integer> segments, 
			@JsonProperty("parentSegments") Collection<Integer> parentSegments) {
	}
	
	@JsonProperty
	public abstract long baseTimestamp();
	
	@JsonProperty
	public abstract long headTimestamp();
	
	@JsonProperty
	public abstract String name();
	
	@JsonProperty
	public abstract String parentPath();

	@JsonProperty
	public abstract String path();
	
	@JsonIgnore
	public abstract BranchManagerImpl getBranchManager();
	
	@JsonProperty
	public abstract int cdoBranchId();
	
	@JsonProperty
	public abstract int segmentId();
	
	@JsonProperty
	public abstract Collection<Integer> segments();
	
	@JsonProperty
	public abstract Collection<Integer> parentSegments();

}
