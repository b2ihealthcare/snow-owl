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
package com.b2international.snowowl.datastore.index;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.WithScore;
import com.b2international.index.query.Expression;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.api.IComponent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @since 4.7
 */
public abstract class RevisionDocument extends Revision implements IComponent<String>, WithScore {

	public static abstract class Expressions {

		protected Expressions() {
		}
		
		public static final Expression id(String id) {
			return exactMatch(Fields.ID, id);
		}
		
		public static final Expression ids(Collection<String> ids) {
			return matchAny(Fields.ID, ids);
		}

	}

	/**
	 * @since 4.7
	 */
	public static class Fields {
		public static final String ID = "id";
		public static final String ICON_ID = "iconId";
	}
	
	/**
	 * @param <B> - the builder type
	 * @since 4.7
	 */
	public static abstract class RevisionDocumentBuilder<B extends RevisionDocumentBuilder<B>> {
		
		protected String id;
		protected String label;
		protected String iconId;
		protected float score = 0.0f;
		
		// XXX only for JSON deserialization
		protected long storageKey;
		protected String branchPath;
		protected long commitTimestamp;
		protected Collection<Integer> replacedIns = Collections.emptyList();
		protected int segmentId;

		/**
		 * @deprecated - see reason at {@link com.b2international.snowowl.core.domain.IComponent#getStorageKey()} why this should be removed
		 */
		public final B storageKey(long storageKey) {
			this.storageKey = storageKey;
			return getSelf();
		} 
		
		public B id(final String id) {
			this.id = id;
			return getSelf();
		}
		
		/**
		 * @deprecated - UNSUPPORTED, will be removed in 4.7
		 */
		public B label(final String label) {
			this.label = label;
			return getSelf();
		}
		
		public B iconId(final String iconId) {
			this.iconId = iconId;
			return getSelf();
		}
		
		public B score(final float score) {
			this.score = score;
			return getSelf();
		}
		
		B commitTimestamp(final long commitTimestamp) {
			this.commitTimestamp = commitTimestamp;
			return getSelf();
		}
		
		B branchPath(final String branchPath) {
			this.branchPath = branchPath;
			return getSelf();
		}
		
		B replacedIns(final Collection<Integer> replacedIns) {
			this.replacedIns = replacedIns;
			return getSelf();
		}
		
		B segmentId(final int segmentId) {
			this.segmentId = segmentId;
			return getSelf();
		}
		
		protected abstract B getSelf();
		
	}
	
	private final String id;
	private final String label;
	private final String iconId;
	private float score = 0.0f;
	
	protected RevisionDocument(final String id, final String label, String iconId) {
		this.id = id;
		this.label = label;
		this.iconId = iconId;
	}
	
	@JsonIgnore
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getIconId() {
		return iconId;
	}

	@JsonIgnore
	@Override
	public float getScore() {
		return score ;
	}
	
	@JsonIgnore
	@Override
	public void setScore(float score) {
		this.score = score;
	}
	
	@Override
	public final int hashCode() {
		return Objects.hashCode(_id());
	}

	@Override
	protected MoreObjects.ToStringHelper doToString() {
		return super.doToString()
				.add("id", id)
				.add("label", label)
				.add("iconId", iconId)
				.add("score", score);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final RevisionDocument other = (RevisionDocument) obj;
		return Objects.equal(_id(), other._id());
	}

	/**
	 * (non-API)
	 * 
	 * @return returns with the storage key of the current component as a CDO ID.
	 */
	public CDOID cdoID() {
		return CDOIDUtil.createLong(getStorageKey());
	}
	
}
