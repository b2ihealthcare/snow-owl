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

import static com.b2international.index.query.Expressions.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.api.ITreeComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

/**
 * A transfer object representing a SNOMED CT concept.
 */
@Doc
@JsonDeserialize(builder=SnomedConceptDocument.Builder.class)
public class SnomedConceptDocument extends SnomedComponentDocument implements ITreeComponent {

	public static final Long ROOT_ID = -1L;
	public static final float DEFAULT_DOI = 1.0f;
	private static final long serialVersionUID = -824286402410205210L;

	public static Builder builder() {
		return new Builder();
	}
	
	public final static class Expressions extends SnomedComponentDocument.Expressions {
		
		private Expressions() {
		}

		public static Expression parents(Collection<String> parentIds) {
			return matchAny(Fields.PARENTS, parentIds);
		}

		public static Expression ancestors(Collection<String> ancestorIds) {
			return matchAny(Fields.ANCESTORS, ancestorIds);
		}

		public static Expression statedParents(Collection<String> statedParentIds) {
			return matchAny(Fields.STATED_PARENTS, statedParentIds);
		}
		
		public static Expression statedAncestors(Collection<String> statedAncestorIds) {
			return matchAny(Fields.STATED_ANCESTORS, statedAncestorIds);
		}
		
		public static Expression primitive() {
			return match(Fields.PRIMITIVE, true);
		}
		
		public static Expression defining() {
			return match(Fields.PRIMITIVE, false);
		}
		
		public static Expression exhaustive() {
			return match(Fields.EXHAUSTIVE, true);
		}
		
		public static Expression refSetStorageKey(long storageKey) {
			return exactMatch(Fields.REFSET_STORAGEKEY, storageKey);
		}
		
		public static Expression refSetType(SnomedRefSetType type) {
			return match(Fields.REFSET_TYPE, type.ordinal());
		}
		
		public static Expression referencedComponentType(int referencedComponentType) {
			return match(Fields.REFERENCED_COMPONENT_TYPE, referencedComponentType);
		}
		
		public static Expression structuralRefSet() {
			return match(Fields.STRUCTURAL, true);
		}
		
		public static Expression regularRefSet() {
			return match(Fields.STRUCTURAL, false);
		}
		
		public static Expression referringRefSet(String referringRefSet) {
			return exactMatch(Fields.REFERRING_REFSETS, referringRefSet);
		}
		
		public static Expression referringMappingRefSet(String referringMappingRefSet) {
			return exactMatch(Fields.REFERRING_MAPPING_REFSETS, referringMappingRefSet);
		}
		
		public static Expression referringPredicate(String referringPredicate) {
			return exactMatch(Fields.REFERRING_PREDICATES, referringPredicate);
		}
		
	}

	public static class Fields extends SnomedComponentDocument.Fields {
		public static final String REFSET_STORAGEKEY = "refSetStorageKey";
		public static final String REFERRING_PREDICATES = "referringPredicates";
		public static final String PRIMITIVE = "primitive";
		public static final String EXHAUSTIVE = "exhaustive";
		public static final String ANCESTORS = "ancestors";
		public static final String STATED_ANCESTORS = "statedAncestors";
		public static final String PARENTS = "parents";
		public static final String STATED_PARENTS = "statedParents";
		public static final String PREDICATES = "predicates";
		public static final String REFSET_TYPE = "refSetType";
		public static final String REFERENCED_COMPONENT_TYPE = "referencedComponentType";
		public static final String STRUCTURAL = "structural";
		public static final String REFERRING_REFSETS = "referringRefSets";
		public static final String REFERRING_MAPPING_REFSETS = "referringMappingRefSets";
	}
	
	public static Builder builder(final SnomedConceptDocument input) {
		final Builder builder = builder()
				.id(input.getId())
//				.score(input.getScore())
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
		
		// TODO add back scoring
//		if (input.getScore() != null) {
//			builder.score(input.getScore());
//		}
		
		return builder;
	}
	
	public static List<SnomedConceptDocument> fromConcepts(Iterable<ISnomedConcept> concepts) {
		return FluentIterable.from(concepts).transform(new Function<ISnomedConcept, SnomedConceptDocument>() {
			@Override
			public SnomedConceptDocument apply(ISnomedConcept input) {
				final ISnomedDescription pt = input.getPt();
				final String preferredTerm = pt == null ? input.getId() : pt.getTerm();
				return SnomedConceptDocument.builder(input).label(preferredTerm).build();
			}
		}).toList();
	}

	public static class Builder extends SnomedComponentDocumentBuilder<Builder> {

		private boolean primitive;
		private boolean exhaustive;
		private LongSet parents;
		private LongSet ancestors;
		private LongSet statedParents;
		private LongSet statedAncestors;
		private Collection<String> referringPredicates = Collections.emptyList();
		private SnomedRefSetType refSetType;
		private short referencedComponentType;
		private float doi = DEFAULT_DOI;
		private Collection<String> referringRefSets;
		private Collection<String> referringMappingRefSets;
		private long refSetStorageKey = CDOUtils.NO_STORAGE_KEY;

		@JsonCreator
		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
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
		
		public Builder referringPredicates(final Collection<String> referringPredicates) {
			this.referringPredicates = referringPredicates;
			return getSelf();
		}
		
		public Builder refSet(final SnomedRefSet refSet) {
			this.refSetType = refSet.getType();
			this.referencedComponentType = refSet.getReferencedComponentType();
			this.refSetStorageKey = CDOIDUtil.getLong(refSet.cdoID());
			return getSelf();
		}
		
		public Builder doi(float doi) {
			this.doi = doi;
			return getSelf();
		}
		
		public Builder referringRefSets(Collection<String> referringRefSets) {
			this.referringRefSets = referringRefSets;
			return getSelf();
		}
		
		public Builder referringMappingRefSets(Collection<String> referringMappingRefSets) {
			this.referringMappingRefSets = referringMappingRefSets;
			return getSelf();
		}

		public SnomedConceptDocument build() {
			final SnomedConceptDocument entry = new SnomedConceptDocument(id,
					label,
					iconId, 
					moduleId, 
					released, 
					active, 
					effectiveTime, 
					namespace,
					primitive, 
					exhaustive,
					refSetType, referencedComponentType, refSetStorageKey);
			
			entry.setDoi(doi);
			
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
			
			if (referringPredicates != null) {
				entry.setReferringPredicates(referringPredicates);
			}
			
			if (referringRefSets != null) {
				entry.setReferringRefSets(referringRefSets);
			}
			
			if (referringMappingRefSets != null) {
				entry.setReferringMappingRefSets(referringMappingRefSets);
			}
			
			return entry;
		}


	}

	private final boolean primitive;
	private final boolean exhaustive;
	private final SnomedRefSetType refSetType;
	private final short referencedComponentType;
	private final boolean structural;
	private final long refSetStorageKey;
	
	private LongSet parents;
	private LongSet ancestors;
	private LongSet statedParents;
	private LongSet statedAncestors;
	private Collection<String> referringPredicates;
	private float doi;
	private Collection<String> referringRefSets;
	private Collection<String> referringMappingRefSets;

	protected SnomedConceptDocument(final String id,
			final String label,
			final String iconId, 
			final String moduleId,
			final boolean released,
			final boolean active,
			final long effectiveTime,
			final String namespace,
			final boolean primitive,
			final boolean exhaustive, 
			final SnomedRefSetType refSetType, 
			final short referencedComponentType,
			final long refSetStorageKey) {

		super(id, label, iconId, moduleId, released, active, effectiveTime, namespace);
		this.primitive = primitive;
		this.exhaustive = exhaustive;
		this.refSetType = refSetType;
		this.referencedComponentType = referencedComponentType;
		this.refSetStorageKey = refSetStorageKey;
		this.structural = SnomedRefSetUtil.isStructural(id, refSetType);
	}
	
	@JsonIgnore
	public Collection<ConstraintDomain> getPredicates() {
		return FluentIterable.from(referringPredicates).transform(new Function<String, ConstraintDomain>() {
			@Override 
			public ConstraintDomain apply(final String predicateKey) {
				final List<String> segments = Splitter.on(PredicateUtils.PREDICATE_SEPARATOR).limit(2).splitToList(predicateKey);
				final long storageKey = Long.parseLong(segments.get(0));
				final String predicateKeySuffix = segments.get(1);
				return new ConstraintDomain(Long.parseLong(getId()), predicateKeySuffix, storageKey);
			}
		}).toList();
	}
	
	private void setReferringPredicates(Collection<String> componentReferringPredicates) {
		this.referringPredicates = componentReferringPredicates;
	}
	
	public Collection<String> getReferringPredicates() {
		return referringPredicates;
	}
	
	public long getRefSetStorageKey() {
		return refSetStorageKey;
	}
	
	public float getDoi() {
		return doi;
	}
	
	void setDoi(float doi) {
		this.doi = doi;
	}
	
	public Collection<String> getReferringRefSets() {
		return referringRefSets;
	}
	
	void setReferringRefSets(Collection<String> referringRefSets) {
		this.referringRefSets = referringRefSets;
	}
	
	public Collection<String> getReferringMappingRefSets() {
		return referringMappingRefSets;
	}
	
	void setReferringMappingRefSets(Collection<String> referringMappingRefSets) {
		this.referringMappingRefSets = referringMappingRefSets;
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
	
	public SnomedRefSetType getRefSetType() {
		return refSetType;
	}
	
	public short getReferencedComponentType() {
		return referencedComponentType;
	}
	
	public boolean isStructural() {
		return structural;
	}
	
	@Override
	public String toString() {
		return toStringHelper()
				.add(Fields.PRIMITIVE, primitive)
				.add(Fields.EXHAUSTIVE, exhaustive)
				.add(Fields.REFSET_TYPE, refSetType)
				.add(Fields.REFERENCED_COMPONENT_TYPE, referencedComponentType)
				.toString();
	}

}

