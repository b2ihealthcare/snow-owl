/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.match;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchRange;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Predicate;

/**
 * Common superclass for SNOMED CT transfer objects.
 */
public abstract class SnomedDocument extends RevisionDocument {

	public static abstract class Expressions extends RevisionDocument.Expressions {
		
		protected Expressions() {
		}
		
		public static final Expression active() {
			return active(true);
		}
		
		public static final Expression inactive() {
			return active(false);
		}
		
		public static Expression active(boolean active) {
			return match(Fields.ACTIVE, active);
		}
		
		public static final Expression module(String moduleId) {
			return exactMatch(Fields.MODULE_ID, moduleId);
		}
		
		/**
		 * Returns the expression that matches any of the modules passed in
		 * @param modules
		 * @return expression
		 */
		public static Expression modules(Iterable<String> modules) {
			return matchAny(Fields.MODULE_ID, modules);
		}

		public static final Expression released() {
			return released(true);
		}
		
		public static final Expression unreleased() {
			return released(false);
		}
		
		public static final Expression released(boolean released) {
			return match(Fields.RELEASED, released);
		}
		
		public static final Expression effectiveTime(long effectiveTime) {
			return effectiveTime(effectiveTime, effectiveTime);
		}
		
		public static final Expression effectiveTime(long from, long to) {
			return matchRange(Fields.EFFECTIVE_TIME, from, to);
		}
		
		public static final Expression effectiveTime(long from, long to, boolean minInclusive, boolean maxInclusive) {
			return matchRange(Fields.EFFECTIVE_TIME, from, to, minInclusive, maxInclusive);
		}


	}

	public static final Predicate<SnomedDocument> ACTIVE_PREDICATE = new Predicate<SnomedDocument>() {
		@Override
		public boolean apply(SnomedDocument input) {
			return input.isActive();
		}
	};

	// XXX: Type parameter reveals subclass to AbstractBuilder for fluent API
	public static abstract class Builder<B extends Builder<B, T>, T extends SnomedDocument> extends RevisionDocumentBuilder<B, T> {

		protected String moduleId;
		protected Boolean active;
		protected Boolean released;
		protected Long effectiveTime = EffectiveTimes.UNSET_EFFECTIVE_TIME;

		public B moduleId(final String moduleId) {
			this.moduleId = moduleId;
			return getSelf();
		}

		public B active(final Boolean active) {
			this.active = active;
			return getSelf();
		}

		public B released(final Boolean released) {
			this.released = released;
			return getSelf();
		}

		public B effectiveTime(final Long effectiveTime) {
			this.effectiveTime = effectiveTime;
			return getSelf();
		}

	}
	
	public static class Fields extends RevisionDocument.Fields {
		public static final String MODULE_ID = SnomedRf2Headers.FIELD_MODULE_ID;
		public static final String ACTIVE = SnomedRf2Headers.FIELD_ACTIVE;
		public static final String EFFECTIVE_TIME = SnomedRf2Headers.FIELD_EFFECTIVE_TIME;
		public static final String RELEASED = "released";
	}

	private final Boolean released;
	private final String moduleId;
	private final Boolean active;
	private final Long effectiveTime;

	protected SnomedDocument(final String id,
			final String label,
			final String iconId,
			final String moduleId, 
			final Boolean released, 
			final Boolean active, 
			final Long effectiveTime) {
		super(id, label, iconId);
		this.moduleId = moduleId;
		this.released = released;
		this.active = active;
		this.effectiveTime = effectiveTime;
	}
	
	/**
	 * @return {@code true} if the component has already appeared in an RF2 release, {@code false} otherwise
	 */
	public Boolean isReleased() {
		return released;
	}

	/**
	 * @return {@code true} if the component is active, {@code false} otherwise
	 */
	public Boolean isActive() {
		return active;
	}

	/**
	 * @return the module concept identifier of this component
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * @return the effective time of the component, or {@link EffectiveTimes#UNSET_EFFECTIVE_TIME} if the component currently has
	 *         no effective time set
	 */
	public Long getEffectiveTime() {
		return effectiveTime;
	}

	/**
	 * @return the effective time of the component formatted using {@link EffectiveTimes#format(Object)}, or
	 *         {@link EffectiveTimes#UNSET_EFFECTIVE_TIME_LABEL} if the component currently has no effective time set
	 */
	@JsonIgnore
	public String getEffectiveTimeAsString() {
		return EffectiveTimes.format(effectiveTime);
	}

	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
				.add("id", getId())
				.add("label", getLabel())
				.add("iconId", getIconId())
				.add("moduleId", moduleId)
				.add("released", released)
				.add("active", active)
				.add("effectiveTime", effectiveTime);
	}
	
	public static final Class<? extends SnomedDocument> getType(ComponentCategory componentCategory) {
		switch (componentCategory) {
		case CONCEPT: return SnomedConceptDocument.class;
		case DESCRIPTION: return SnomedDescriptionIndexEntry.class;
		case RELATIONSHIP: return SnomedRelationshipIndexEntry.class;
		case SET_MEMBER:
		case MAP_MEMBER: return SnomedRefSetMemberIndexEntry.class;
		default: throw new UnsupportedOperationException("Unsupported component category: " + componentCategory);
		}
	}
	
	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("moduleId", moduleId)
				.add("released", released)
				.add("active", active)
				.add("effectiveTime", effectiveTime);
	}

}
