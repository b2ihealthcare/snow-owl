/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.Doc;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ITreeComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

/**
 * A transfer object representing a SNOMED CT concept.
 */
@Doc
@JsonDeserialize(builder=SnomedConceptIndexEntry.Builder.class)
public class SnomedConceptIndexEntry extends SnomedIndexEntry implements IComponent<String>, IIndexEntry, Serializable, ITreeComponent {

	private static final long serialVersionUID = -824286402410205210L;

	public static class Fields {
		public static final String PRIMITIVE = "primitive";
		public static final String EXHAUSTIVE = "exhaustive";
		public static final String ANCESTORS = "ancestors";
		public static final String STATED_ANCESTORS = "statedAncestors";
		public static final String PARENTS = "parents";
		public static final String STATED_PARENTS = "statedParents";
		public static final String PREDICATES = "predicates";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
//	public static Builder builder(final Document doc) {
//		return builder()
//				.id(SnomedMappings.id().getValueAsString(doc))
//				.moduleId(SnomedMappings.module().getValueAsString(doc))
//				.storageKey(Mappings.storageKey().getValue(doc))
//				.active(BooleanUtils.valueOf(SnomedMappings.active().getValue(doc).intValue())) 
//				.released(BooleanUtils.valueOf(SnomedMappings.released().getValue(doc).intValue()))
//				.effectiveTimeLong(SnomedMappings.effectiveTime().getValue(doc))
//				.iconId(Mappings.iconId().getValue(doc))
//				.primitive(BooleanUtils.valueOf(SnomedMappings.primitive().getValue(doc).intValue()))
//				.exhaustive(BooleanUtils.valueOf(SnomedMappings.exhaustive().getValue(doc).intValue()));
//	}
	
	public static Builder builder(final SnomedConceptIndexEntry input) {
		final Builder builder = builder()
				.id(input.getId())
				.storageKey(input.getStorageKey())
				.score(input.getScore())
				.moduleId(input.getModuleId())
				.active(input.isActive())
				.released(input.isReleased())
				.effectiveTime(input.getEffectiveTime())
				.iconId(input.getIconId())
				.primitive(input.isPrimitive())
				.exhaustive(input.isExhaustive())
				.parents(input.getParents())
				.ancestors(input.getAncestors())
				.statedParents(input.getStatedParents())
				.statedAncestors(input.getStatedAncestors());
		
		return builder;
	}
	
	public static Builder builder(ISnomedConcept input) {
		final Builder builder = builder()
				.id(input.getId())
				.moduleId(input.getModuleId())
				.active(input.isActive())
				.released(input.isReleased())
				.effectiveTime(EffectiveTimes.getEffectiveTime(input.getEffectiveTime()))
				.iconId(input.getIconId())
				.primitive(input.getDefinitionStatus().isPrimitive())
				.exhaustive(input.getSubclassDefinitionStatus().isExhaustive())
				.parents(PrimitiveSets.newLongOpenHashSet(input.getParentIds()))
				.ancestors(PrimitiveSets.newLongOpenHashSet(input.getAncestorIds()))
				.statedParents(PrimitiveSets.newLongOpenHashSet(input.getStatedParentIds()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(input.getStatedAncestorIds()));
		
		if (input.getScore() != null) {
			builder.score(input.getScore());
		}
		
		return builder;
	}
	
	public static List<SnomedConceptIndexEntry> fromConcepts(Iterable<ISnomedConcept> concepts) {
		return FluentIterable.from(concepts).transform(new Function<ISnomedConcept, SnomedConceptIndexEntry>() {
			@Override
			public SnomedConceptIndexEntry apply(ISnomedConcept input) {
				final ISnomedDescription pt = input.getPt();
				final String preferredTerm = pt == null ? input.getId() : pt.getTerm();
				return SnomedConceptIndexEntry.builder(input).label(preferredTerm).build();
			}
		}).toList();
	}

	public static class Builder extends AbstractBuilder<Builder> {

		private String iconId;
		private boolean primitive;
		private boolean exhaustive;
		private LongSet parents;
		private LongSet ancestors;
		private LongSet statedParents;
		private LongSet statedAncestors;
		private Collection<String> predicates = Collections.emptyList();

		@JsonCreator
		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder iconId(final String iconId) {
			this.iconId = iconId;
			return getSelf();
		}

		public Builder primitive(final boolean primitive) {
			this.primitive = primitive;
			return getSelf();
		}

		public Builder exhaustive(final boolean exhaustive) {
			this.exhaustive = exhaustive;
			return getSelf();
		}
		
		public Builder parents(final LongSet parents) {
			this.parents = parents;
			return getSelf();
		}
		
		public Builder statedParents(final LongSet statedParents) {
			this.statedParents = statedParents;
			return getSelf();
		}
		
		public Builder ancestors(final LongSet ancestors) {
			this.ancestors = ancestors;
			return getSelf();
		}
		
		public Builder statedAncestors(final LongSet statedAncestors) {
			this.statedAncestors = statedAncestors;
			return getSelf();
		}
		
		public Builder predicates(final Collection<String> componentReferringPredicates) {
			this.predicates = componentReferringPredicates;
			return getSelf();
		}

		public SnomedConceptIndexEntry build() {
			final SnomedConceptIndexEntry entry = new SnomedConceptIndexEntry(id,
					label,
					iconId, 
					score, 
					moduleId, 
					released, 
					active, 
					effectiveTime, 
					primitive, 
					exhaustive);
			
			if (parents != null) {
				entry.setParents(parents);
			}
			
			if (statedParents != null) {
				entry.setStatedParents(statedParents);
			}
			
			if (ancestors != null) {
				entry.setAncestors(ancestors);
			}
			
			if (statedAncestors != null) {
				entry.setStatedAncestors(statedAncestors);
			}
			
			if (predicates != null) {
				entry.setComponentReferringPredicates(predicates);
			}
			
			return entry;
		}
	}

	private final boolean primitive;
	private final boolean exhaustive;
	private LongSet parents;
	private LongSet ancestors;
	private LongSet statedParents;
	private LongSet statedAncestors;
	private Collection<String> predicates; 

	protected SnomedConceptIndexEntry(final String id,
			final String label,
			final String iconId, 
			final float score, 
			final String moduleId,
			final boolean released,
			final boolean active,
			final long effectiveTimeLong,
			final boolean primitive,
			final boolean exhaustive) {

		super(id, 
				label,
				iconId,
				score, 
				-1L, // FIXME remove storageKeys from index entries 
				moduleId, 
				released, 
				active,
				effectiveTimeLong);

		this.primitive = primitive;
		this.exhaustive = exhaustive;
	}
	
	public Collection<ConstraintDomain> getPredicates() {
		return FluentIterable.from(predicates).transform(new Function<String, ConstraintDomain>() {
			@Override 
			public ConstraintDomain apply(final String predicateKey) {
				final List<String> segments = Splitter.on(PredicateUtils.PREDICATE_SEPARATOR).limit(2).splitToList(predicateKey);
				final long storageKey = Long.parseLong(segments.get(0));
				final String predicateKeySuffix = segments.get(1);
				return new ConstraintDomain(Long.parseLong(getId()), predicateKeySuffix, storageKey);
			}
		}).toList();
	}
	
	private void setComponentReferringPredicates(Collection<String> componentReferringPredicates) {
		this.predicates = componentReferringPredicates;
	}

	/**
	 * @return {@code true} if the concept definition status is 900000000000074008 (primitive), {@code false} otherwise
	 */
	public boolean isPrimitive() {
		return primitive;
	}

	/**
	 * @return {@code true} if the concept subclass definition status is exhaustive, {@code false} otherwise
	 */
	public boolean isExhaustive() {
		return exhaustive;
	}
	
	private void setParents(LongSet parents) {
		this.parents = parents;
	}
	
	private void setStatedParents(LongSet statedParents) {
		this.statedParents = statedParents;
	}
	
	@Override
	public LongSet getParents() {
		return parents;
	}
	
	public LongSet getStatedParents() {
		return statedParents;
	}
	
	private void setAncestors(LongSet ancestors) {
		this.ancestors = ancestors;
	}
	
	private void setStatedAncestors(LongSet statedAncestors) {
		this.statedAncestors = statedAncestors;
	}
	
	@Override
	public LongSet getAncestors() {
		return ancestors;
	}
	
	public LongSet getStatedAncestors() {
		return statedAncestors;
	}

	@Override
	public String toString() {
		return toStringHelper()
				.add("primitive", primitive)
				.add("exhaustive", exhaustive)
				.toString();
	}

}

