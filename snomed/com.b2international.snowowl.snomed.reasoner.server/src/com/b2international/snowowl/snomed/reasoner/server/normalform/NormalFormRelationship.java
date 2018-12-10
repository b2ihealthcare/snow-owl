/*
 * Copyright 2009 International Health Terminology Standards Development Organisation
 * Copyright 2013-2018 B2i Healthcare Pte Ltd, http://b2i.sg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright CSIRO Australian e-Health Research Centre (http://aehrc.com).
 * All rights reserved. Use is subject to license terms and conditions.
 */
package com.b2international.snowowl.snomed.reasoner.server.normalform;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.Objects;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;

/**
 * Represents concept attribute-value pairs, used when relationships originating
 * from different sources are being processed.
 *
 * @author law223
 */
final class NormalFormRelationship implements NormalFormProperty {

	private final StatementFragment fragment;
	private final ReasonerTaxonomy reasonerTaxonomy;
	private final ReasonerTaxonomyBuilder reasonerTaxonomyBuilder;
	
	/**
	 * Creates a new instance from the specified relationship.
	 *
	 * @param fragment the relationship to wrap (may not be <code>null</code>)
	 * @param reasonerTaxonomy
	 * @param reasonerTaxonomyBuilder
	 *
	 * @throws NullPointerException if the given relationship is <code>null</code>
	 */
	public NormalFormRelationship(final StatementFragment fragment, final ReasonerTaxonomy reasonerTaxonomy, final ReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
		this.fragment = checkNotNull(fragment, "fragment");
		this.reasonerTaxonomy = checkNotNull(reasonerTaxonomy, "reasonerTaxonomy");
		this.reasonerTaxonomyBuilder = checkNotNull(reasonerTaxonomyBuilder, "reasonerTaxonomyBuilder");
	}

	public boolean isDestinationNegated() {
		return fragment.isDestinationNegated();
	}

	public boolean isUniversal() {
		return fragment.isUniversal();
	}

	public long getTypeId() {
		return fragment.getTypeId();
	}

	public long getDestinationId() {
		return fragment.getDestinationId();
	}

	public long getStatementId() {
		return fragment.getStatementId();
	}

	public long getStorageKey() {
		return fragment.getStorageKey();
	}

	@Override
	public boolean isSameOrStrongerThan(final NormalFormProperty property) {
		if (this == property) { return true; }
		if (!(property instanceof NormalFormRelationship)) { return false; }

		final NormalFormRelationship other = (NormalFormRelationship) property;

		if (isUniversal() != other.isUniversal()) {
			return false;
		}

		if (!isDestinationNegated() && !other.isDestinationNegated()) {

			/*
			 * Things same or stronger than (some/all) rA:
			 *
			 * - (some/all) r'A, where r' is equal to r or is a descendant of r
			 * - (some/all) rA', where A' is equal to A or is a descendant of A
			 * - (some/all) r'A', where both of the above applies
			 */
			return true
					&& closureContains(getTypeId(), other.getTypeId()) 
					&& closureContains(getDestinationId(), other.getDestinationId());

		} else if (isDestinationNegated() && !other.isDestinationNegated()) {

			/*
			 * Note that "other" itself may be exhaustive in this case --
			 * the negation will work entirely within the confines of
			 * "other", so it is still going to be more expressive than
			 * "other".
			 *
			 * Supertypes of the negated value can only appear above the
			 * "layers" of exhaustive concepts, because any other case
			 * should be unsatisfiable.
			 */
			return true 
					&& (hasCommonExhaustiveSuperType(other) || isDestinationExhaustive()) 
					&& closureContains(other.getTypeId(), getTypeId()) 
					&& ancestorsContains(getDestinationId(), other.getDestinationId());

		} else if (!isDestinationNegated() && other.isDestinationNegated()) {

			/*
			 * Any contradictions should be filtered out by the reasoner beforehand, so we just check if the two concepts
			 * have a common exhaustive ancestor.
			 */
			return true
					&& hasCommonExhaustiveSuperType(other)
					&& closureContains(getTypeId(), other.getTypeId()); 

		} else /* if (destinationNegated && other.destinationNegated) */ {

			/*
			 * Note that the comparison is the exact opposite of the first case - if both fragments are negated,
			 * the one which negates a more loose definition is the one that is more strict in the end.
			 */
			return true
					&& closureContains(other.getTypeId(), getTypeId()) 
					&& closureContains(other.getDestinationId(), getDestinationId());
		}
	}

	private boolean ancestorsContains(final long conceptId1, final long conceptId2) {
		return reasonerTaxonomy.getAncestors(conceptId1).contains(conceptId2);
	}

	private boolean closureContains(final long conceptId1, final long conceptId2) {
		if (conceptId1 == conceptId2) { 
			return true; 
		} else if (ancestorsContains(conceptId1, conceptId2)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isDestinationExhaustive() {
		return isExhaustive(getDestinationId());
	}

	private boolean hasCommonExhaustiveSuperType(final NormalFormRelationship other) {

		final LongSet valueAncestors = reasonerTaxonomy.getAncestors(getDestinationId());
		final LongSet otherValueAncestors = reasonerTaxonomy.getAncestors(other.getDestinationId());
		final LongSet commonAncestors = LongSets.intersection(valueAncestors, otherValueAncestors);

		for (final LongIterator itr = commonAncestors.iterator(); itr.hasNext(); /* empty */) {
			final long commonAncestor = itr.next();
			if (isExhaustive(commonAncestor)) {
				return true;
			}
		}

		return false;
	}

	private boolean isExhaustive(final long conceptId) {
		return reasonerTaxonomyBuilder.isExhaustive(conceptId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof NormalFormRelationship)) { return false; }

		final NormalFormRelationship other = (NormalFormRelationship) obj;

		if (isUniversal() != other.isUniversal()) { return false; }
		if (isDestinationNegated() != other.isDestinationNegated()) { return false; }
		if (getTypeId() != other.getTypeId()) { return false; }
		if (getDestinationId() != other.getDestinationId()) { return false; }
		
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(isUniversal(), isDestinationNegated(), getTypeId(), getDestinationId());
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0,number,#} : {1}{2,number,#} ({3})", getTypeId(), (isDestinationNegated() ? "NOT" : ""), getDestinationId(), isUniversal());
	}
}
