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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import com.b2international.index.Script;
import com.b2international.index.mapping.AutoGenerateID;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * @since 4.7
 */
@AutoGenerateID
@Script(
	name=Revision.UPDATE_REVISED, 
	script=""
		+ "int idx = ctx._source.revised.indexOf(params.oldRevised);"
		+ "if (idx > -1) {"
		+ "    ctx._source.revised.set(idx, params.newRevised);"
		+ "} else {"
		+ "    ctx._source.revised.add(params.newRevised);"
		+ "}"
)
public abstract class Revision {
	
	/**
	 * @since 4.7
	 */
	public static class Fields {
		public static final String ID = "id";
		public static final String CREATED = "created";
		public static final String REVISED = "revised";
	}

	// scripts
	public static final String UPDATE_REVISED = "updateRevised";

	@Field(defaultSortBy = true)
	private String id;
	
	/**
	 * Created branch point that represents both time and space when this revision has been created. It is a read-only field after revision creation,
	 * the only time when this field is set is during the first commit via {@link #setCreated(RevisionBranchPoint)} by the {@link RevisionWriter}.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private RevisionBranchPoint created;

	/**
	 * List of revised branch points. These represent both time and space from where this revision is no longer available and should be skipped when
	 * searching revision documents. The field is read-only, non-null and it is always initialized with an empty list. The actual values are
	 * maintained by the {@link RevisionWriter} during commit via the {@link #UPDATE_REVISED} script.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private List<RevisionBranchPoint> revised = Collections.emptyList();

	public Revision(String id) {
		this.id = checkNotNull(id, "Logical identifier cannot be null");
	}
	
	public final String getId() {
		return id;
	}
	
	final void setCreated(RevisionBranchPoint created) {
		this.created = created;
	}
	
	protected final RevisionBranchPoint getCreated() {
		return created;
	}
	
	protected final List<RevisionBranchPoint> getRevised() {
		return revised;
	}
	
	/**
	 * Provides high-level component identifier if this component is a subcomponent of the high-level component identifier. This method by default
	 * returns a {@link #ROOT} object ID therefore this object is a ROOT component in a given hierarchy.
	 * 
	 * @return the container identifier
	 */
	@JsonIgnore
	protected ObjectId getContainerId() {
		return ObjectId.rootOf(DocumentMapping.getDocType(getClass()));
	}
	
	@JsonIgnore
	public ObjectId getObjectId() {
		return ObjectId.of(DocumentMapping.getDocType(getClass()), getId());
	}
	
	@Override
	public final String toString() {
		return doToString().toString();
	}
	
	protected ToStringHelper doToString() {
		return MoreObjects.toStringHelper(this)
				.add(Revision.Fields.ID, id)
				.add(Revision.Fields.CREATED, created)
				.add(Revision.Fields.REVISED, revised);
	}
	
	/**
	 * @since 7.0
	 * @param <B>
	 * @param <T>
	 */
	@JsonIgnoreProperties(value = { "created", "revised" })
	public static abstract class Builder<B extends Builder<B, T>, T extends Revision> {
		
		protected abstract B getSelf();
		
		public abstract T build();
		
	}

	/**
	 * @param field - the field to test
	 * @return <code>true</code> if the given field is a property that is being used by the revision system and it is not part of the document's schema, <code>false</code> otherwise.
	 */
	public static boolean isRevisionField(String field) {
		return Fields.CREATED.equals(field) 
				|| Revision.Fields.REVISED.equals(field);
	}

}
