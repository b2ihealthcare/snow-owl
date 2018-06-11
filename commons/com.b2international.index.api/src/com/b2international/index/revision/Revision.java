/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.List;

import com.b2international.index.Script;
import com.b2international.index.WithId;
import com.b2international.index.mapping.DocumentMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @since 4.7
 */
@Script(name=Revision.UPDATE_REVISED, script=""
		+ "int idx = ctx._source.revised.indexOf(params.oldRevised);"
		+ "if (idx > -1) {"
		+ "    ctx._source.revised.set(idx, params.newRevised);"
		+ "} else {"
		+ "    ctx._source.revised.add(params.newRevised);"
		+ "}")
public abstract class Revision implements WithId {
	
	static final String ROOT = "-1";

	public static class Fields {
		public static final String ID = "id";
		public static final String CREATED = "created";
		public static final String REVISED = "revised";
	}

	// scripts
	public static final String UPDATE_REVISED = "updateRevised";

	private String _id;
	
	private String id;
	private RevisionBranchPoint created;
	private List<RevisionBranchPoint> revised = Collections.emptyList();

	@JsonIgnore
	public ObjectNode _source;
	
	public Revision(String id) {
		this.id = checkNotNull(id, "Logical identifier cannot be null");
	}
	
	@Override
	public final void set_id(String _id) {
		this._id = _id;
	}
	
	@Override
	@JsonIgnore
	public final String _id() {
		checkState(_id != null, "Partial documents do not have document IDs. Load the entire document or extract the required data from this object.");
		return _id;
	}
	
	final void setCreated(RevisionBranchPoint created) {
		this.created = created;
	}
	
	final void setRevised(List<RevisionBranchPoint> revised) {
		this.revised = revised;
	}
	
	public final String getId() {
		return id;
	}
	
	public final RevisionBranchPoint getCreated() {
		return created;
	}
	
	public final List<RevisionBranchPoint> getRevised() {
		return revised;
	}
	
	/**
	 * Providers high-level component identifier if this component is a subcomponent of the high-level component identifier. This method by default
	 * returns the component's own identifier and should return that ID if this component is root of a component hierarchy.
	 * 
	 * @return the container identifier
	 */
	@JsonIgnore
	public String getContainerId() {
		return ROOT; // TODO move root container ID to a constant
	}
	
	/**
	 * @return whether this component is a root component in a hierarchy or a subcomponent of another component. By default returns <code>true</code>.
	 */
	@JsonIgnore
	public final boolean isRoot() {
		return ROOT.equals(getContainerId());
	}
	
	@Override
	public final String toString() {
		return doToString().toString();
	}
	
	protected ToStringHelper doToString() {
		return Objects.toStringHelper(this)
				.add(DocumentMapping._ID, _id)
				.add(Revision.Fields.ID, id)
				.add(Revision.Fields.CREATED, created)
				.add(Revision.Fields.REVISED, revised);
	}
	
}
