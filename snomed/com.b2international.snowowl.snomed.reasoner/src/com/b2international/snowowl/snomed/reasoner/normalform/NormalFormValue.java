/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.normalform;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Objects;

import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithValue;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomy;

/**
 * Wraps property values, used in the normal form generation process.
 */
final class NormalFormValue implements NormalFormProperty {

	private final long typeId;
	private final RelationshipValue value;
	private final boolean released;
	private final ReasonerTaxonomy reasonerTaxonomy;
	private final long statementId;

	private static RelationshipValue relationshipValue(final long refSetId, final String serializedValue) {
		final DataType dataType = SnomedRefSetUtil.getDataType(Long.toString(refSetId));
		switch (dataType) {
			case DECIMAL: return new RelationshipValue(new BigDecimal(serializedValue));
			case INTEGER: return new RelationshipValue(Integer.valueOf(serializedValue));
			case STRING: return new RelationshipValue(serializedValue);
			default: throw new IllegalArgumentException("Unsupported data type '" + dataType + "'.");
		}
	}
	
	private static RelationshipValue relationshipValue(final RelationshipValueType valueType, final String rawValue) {
		switch (valueType) {
			case DECIMAL: return new RelationshipValue(new BigDecimal(rawValue));
			case INTEGER: return new RelationshipValue(Integer.valueOf(rawValue));
			case STRING: return new RelationshipValue(rawValue);
			default: throw new IllegalArgumentException("Unsupported relationship value type '" + valueType + "'.");
		}
	}

	public NormalFormValue(final StatementFragmentWithValue statement, final ReasonerTaxonomy reasonerTaxonomy) {
		this(
			statement.getTypeId(), 
			relationshipValue(statement.getValueType(), statement.getRawValue()), 
			statement.isReleased(), 
			statement.getStatementId(),
			reasonerTaxonomy);
	}

	public NormalFormValue(final ConcreteDomainFragment member, final ReasonerTaxonomy reasonerTaxonomy) {
		this(
			member.getTypeId(),
			relationshipValue(member.getRefSetId(), member.getSerializedValue()),
			member.isReleased(),
			-1L,
			reasonerTaxonomy);
	}

	/**
	 * Creates a new instance from the specified arguments.
	 *
	 * @param typeId the type ID of the property value
	 * @param value the property value
	 * @param reasonerTaxonomy
	 *
	 * @throws NullPointerException if the given concrete domain member is <code>null</code>
	 */
	private NormalFormValue(
		final long typeId, 
		final RelationshipValue value, 
		final boolean released, 
		final long statementId,
		final ReasonerTaxonomy reasonerTaxonomy) {
		
		this.typeId = typeId;
		this.value = checkNotNull(value, "value");
		this.released = released;
		this.statementId = statementId;
		this.reasonerTaxonomy = checkNotNull(reasonerTaxonomy, "reasonerTaxonomy");
	}

	public long getTypeId() {
		return typeId;
	}
	
	public RelationshipValue getValue() {
		return value;
	}
	
	public boolean isReleased() {
		return released;
	}

	public long getStatementId() {
		return statementId;
	}

	@Override
	public boolean isSameOrStrongerThan(final NormalFormProperty property) {
		if (this == property) { return true; }
		if (!(property instanceof NormalFormValue)) { return false; }

		final NormalFormValue other = (NormalFormValue) property;

		/*
		 * Check type SCTID subsumption, and RF2 literal equality. Value type is taken
		 * into account, but allows matching numbers like #50 (decimal) and #50
		 * (integer).
		 */
		return true
				&& closureContains(getTypeId(), other.getTypeId())
				&& getValue().toLiteral().equals(other.getValue().toLiteral());
	}

	private boolean ancestorsContains(final long conceptId1, final long conceptId2) {
		return reasonerTaxonomy.getInferredAncestors().getDestinations(conceptId1, false).contains(conceptId2);
	}

	private boolean closureContains(final long conceptId1, final long conceptId2) {
		return (conceptId1 == conceptId2) || ancestorsContains(conceptId1, conceptId2);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof NormalFormValue)) { return false; }

		final NormalFormValue other = (NormalFormValue) obj;

		if (getTypeId() != other.getTypeId()) { return false; }
		if (!getValue().equals(other.getValue())) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTypeId(), getValue());
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0,number,#} : {1}]", getTypeId(), getValue());
	}
}
