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
import java.util.List;

import com.b2international.index.WithScore;
import com.b2international.index.query.Expression;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.snowowl.core.api.IComponent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects.ToStringHelper;

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
	public static class Fields extends Revision.Fields {
		public static final String STORAGE_KEY = "storageKey";
		public static final String ICON_ID = "iconId";
	}
	
	/**
	 * @since 4.7
	 */
	public static abstract class RevisionDocumentBuilder<B extends RevisionDocumentBuilder<B, T>, T extends RevisionDocument> extends Revision.Builder<B, T> {
		
		protected String id;
		protected String label;
		protected String iconId;
		protected float score = 0.0f;
		
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
		
	}
	
	private final String label;
	private final String iconId;
	private float score = 0.0f;
	
	protected RevisionDocument(final String id, final String label, String iconId) {
		super(id);
		this.label = label;
		this.iconId = iconId;
	}
	
	@JsonIgnore
	@Override
	public String getLabel() {
		return label;
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
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("label", label)
				.add("iconId", iconId)
				.add("score", score);
	}

}
