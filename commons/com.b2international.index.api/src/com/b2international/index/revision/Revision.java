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

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.b2international.index.Script;
import com.b2international.index.WithId;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	public static final String STORAGE_KEY = "storageKey";
	public static final String CREATED = "created";
	public static final String REVISED = "revised";
	
	// scripts
	public static final String UPDATE_REVISED = "updateRevised";

	private String _id;
	
	private long storageKey;
	private RevisionBranchPoint created;
	private List<RevisionBranchPoint> revised = Collections.emptyList();
	
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
	
	protected final void setStorageKey(long storageKey) {
		this.storageKey = storageKey;
	}
	
	protected final void setCreated(RevisionBranchPoint created) {
		this.created = created;
	}
	
	protected final void setRevised(List<RevisionBranchPoint> revised) {
		this.revised = revised;
	}
	
	public RevisionBranchPoint getCreated() {
		return created;
	}
	
	public List<RevisionBranchPoint> getRevised() {
		return revised;
	}
	
	public final long getStorageKey() {
		return storageKey;
	}

	@Override
	public final String toString() {
		return doToString().toString();
	}
	
	protected ToStringHelper doToString() {
		return Objects.toStringHelper(this)
				.add(DocumentMapping._ID, _id)
				.add(STORAGE_KEY, storageKey)
				.add(Revision.CREATED, created)
				.add(Revision.REVISED, revised);
	}
	
	public static Expression toRevisionFilter(SortedSet<RevisionSegment> segments) {
		final ExpressionBuilder query = Expressions.builder();
		final ExpressionBuilder created = Expressions.builder();
		
		for (RevisionSegment segment : segments) {
			final String start = segment.getStartAddress();
			final String end = segment.getEndAddress();
			created.should(Expressions.matchRange(Revision.CREATED, start, end));
			query.mustNot(Expressions.matchRange(Revision.REVISED, start, end));
		}
		
		return query
				.filter(created.build())
				.build();
	}

	public static Expression toCreatedInFilter(SortedSet<RevisionSegment> segments) {
		final ExpressionBuilder createdIn = Expressions.builder();
		for (RevisionSegment segment : segments) {
			createdIn.should(segment.toRangeExpression(Revision.CREATED));
		}
		return createdIn.build(); 
	}

	public static Expression toRevisedInFilter(SortedSet<RevisionSegment> segments) {
		final ExpressionBuilder revisedIn = Expressions.builder();
		for (RevisionSegment segment : segments) {
			revisedIn.should(segment.toRangeExpression(Revision.REVISED));
		}
		return revisedIn.build();
	}

}
