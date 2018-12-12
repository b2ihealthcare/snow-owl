/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.normalform;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.Objects;

import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * Wraps concept concrete domain members, used in the normal form generation process.
 */
final class NormalFormValue implements NormalFormProperty {

	private final ConcreteDomainFragment fragment;
	private final ReasonerTaxonomy reasonerTaxonomy;

	/**
	 * Creates a new instance from the specified concrete domain member.
	 *
	 * @param fragment the concrete domain fragment to wrap (may not be <code>null</code>)
	 * @param reasonerTaxonomy
	 *
	 * @throws NullPointerException if the given concrete domain member is <code>null</code>
	 */
	public NormalFormValue(final ConcreteDomainFragment fragment, final ReasonerTaxonomy reasonerTaxonomy) {
		this.fragment = checkNotNull(fragment, "fragment");
		this.reasonerTaxonomy = checkNotNull(reasonerTaxonomy, "reasonerTaxonomy");
	}
	
	public String getId() {
		return fragment.getId();
	}

	public String getSerializedValue() {
		return fragment.getSerializedValue();
	}

	public long getTypeId() {
		return fragment.getTypeId();
	}

	public long getStorageKey() {
		return fragment.getStorageKey();
	}

	public long getRefSetId() {
		return fragment.getRefSetId();
	}

	public DataType getDataType() {
		return fragment.getDataType();
	}

	@Override
	public boolean isSameOrStrongerThan(final NormalFormProperty property) {
		if (this == property) { return true; }
		if (!(property instanceof NormalFormValue)) { return false; }

		final NormalFormValue other = (NormalFormValue) property;

		// Check type SCTID subsumption, data type (reference set SCTID) and value equality 
		return true
				&& getRefSetId() == other.getRefSetId()
				&& closureContains(getTypeId(), other.getTypeId())
				&& getSerializedValue().equals(other.getSerializedValue());
	}

	private boolean ancestorsContains(final long conceptId1, final long conceptId2) {
		return reasonerTaxonomy.getAncestors(conceptId1).contains(conceptId2);
	}

	private boolean closureContains(final long conceptId1, final long conceptId2) {
		return (conceptId1 == conceptId2) || ancestorsContains(conceptId1, conceptId2);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof NormalFormValue)) { return false; }

		final NormalFormValue other = (NormalFormValue) obj;

		if (getRefSetId() != other.getRefSetId()) { return false; }
		if (getTypeId() != other.getTypeId()) { return false; }
		if (!getSerializedValue().equals(other.getSerializedValue())) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSerializedValue(), getRefSetId(), getTypeId());
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0,number,#} : {1} [{2}]", getTypeId(), getSerializedValue(), getDataType());
	}
}
