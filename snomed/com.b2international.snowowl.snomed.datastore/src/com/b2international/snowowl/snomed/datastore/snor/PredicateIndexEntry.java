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
package com.b2international.snowowl.snomed.datastore.snor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.b2international.snowowl.core.api.ITerminologyComponentIdProvider;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Lightweight representation of a MRCM rule associated with a SNOMED&nbsp;CT concept attribute.
 * <p><b>NOTE: </b>this class is equals based on the {@link #mostSigBits} and the {@link #leastSigBits} values.
 * @see PredicateType
 */
@Immutable
@ThreadSafe
public class PredicateIndexEntry implements IIndexEntry, Serializable, ITerminologyComponentIdProvider {

	private static final long serialVersionUID = -3084452506109842527L;

	/**
	 * SNOMED&nbsp;CT concept attribute predicate type. 
	 * @see PredicateIndexEntry
	 */
	@Immutable
	@ThreadSafe
	public static enum PredicateType {
		/**Relationship type predicate.*/
		RELATIONSHIP,
		/**Description type predicate.*/
		DESCRIPTION,
		/**Data type predicate.*/
		DATATYPE;
		
		/**
		 * Returns with the {@link PredicateType predicate type} enumeration identified by the unique ordinal value.
		 * @param ordinal the unique ordinal value of the predicate type.
 		 * @return the predicate type.
		 */
		public static PredicateType getByOrdinal(final int ordinal) {
			return PredicateType.values()[ordinal];
		}
	}
	
	/**
	 * Creates a description type predicate.
	 * @param storageKey unique identifier of the SNOMED&nbsp;CT concept attribute constraint.
	 * @param queryExpression the query expression describing the domain part of an MRCM attribute constraint.
	 * @param descriptionTypeId the description type concept identifier.
	 * @param flags a flag encapsulating the {@link #isRequired()} and {@link #isMultiple()} properties. 
	 * @return the new description type predicate instance.
	 */
	public static PredicateIndexEntry createDescriptionTypePredicate(final long storageKey, final String queryExpression, final long descriptionTypeId, final byte flags) {
		final PredicateIndexEntry predicate = new PredicateIndexEntry(storageKey, queryExpression, PredicateType.DESCRIPTION, flags);
		predicate.descriptionTypeId = descriptionTypeId;
		return predicate;
	}

	/**
	 * Creates a concrete domain predicate.
	 * @param storageKey unique identifier of the SNOMED&nbsp;CT concept attribute constraint.
	 * @param queryExpression the query expression describing the domain part of an MRCM attribute constraint.
	 * @param dataType the data type of the concrete domain. See: {@link DataType}.
	 * @param dataTypeName the unique name of the concrete domain. E.g.: {@code isVitamin} or {@code isClinicallySignificant}.
	 * @param dataTypeLabel the humane readable label of the concrete domain. E.g.: {@code Vitamin} or {@code Clinically significant}.
	 * @param flags a flag encapsulating the {@link #isRequired()} and {@link #isMultiple()} properties. 
	 * @return the new data type predicate instance.
	 */
	public static PredicateIndexEntry createDataTypeTypePredicate(final long storageKey, final String queryExpression, final DataType dataType, final String dataTypeName, final String dataTypeLabel, final byte flags) {
		final PredicateIndexEntry predicate = new PredicateIndexEntry(storageKey, queryExpression, PredicateType.DATATYPE, flags);
		predicate.dataTypeType = checkNotNull(dataType, "Data type argument cannot be null.");
		predicate.dataTypeLabel = checkNotNull(dataTypeLabel, "Concrete domain data type label argument cannot be null.");
		predicate.dataTypeName = checkNotNull(dataTypeName, "Concrete domain data type name argument cannot be null.");
		return predicate;
	}
	
	/**
	 * Factory method for creating a new relationship type predicate.
	 * @param storageKey unique identifier of the SNOMED&nbsp;CT concept attribute constraint.
	 * @param queryExpression the query expression describing the domain part of an MRCM attribute constraint.
	 * @param relationshipTypeExpression expression specifying the IDs of the allowed relationship type SNOMED&nbsp;CT concepts. 
	 * @param relationshipvValueExpression expression specifying the IDs of the allowed relationship value SNOMED&nbsp;CT concepts.
	 * @param characteristicTypeExpression expression specifying the IDs of the allowed relationship characteristic type SNOMED&nbsp;CT concepts.
	 * @param groupRule the group role. See: {@link GroupRule}.
	 * @param flags a flag encapsulating the {@link #isRequired()} and {@link #isMultiple()} properties.
	 * @return the new relationship type predicate.
	 */
	public static PredicateIndexEntry createRelationshipTypePredicate(final long storageKey, final String queryExpression, final String relationshipTypeExpression, final String relationshipvValueExpression, 
			final String characteristicTypeExpression, final GroupRule groupRule, final byte flags) {
		
		final PredicateIndexEntry predicate = new PredicateIndexEntry(storageKey, queryExpression, PredicateType.RELATIONSHIP, flags);
		predicate.relationshipTypeExpression = checkNotNull(relationshipTypeExpression, "Relationship type IDs argument cannot be null.");
		predicate.relationshipvValueExpression = checkNotNull(relationshipvValueExpression, "Relationship value IDs argument cannot be null.");
		predicate.characteristicTypeExpression = checkNotNull(characteristicTypeExpression, "Relationship characteristic type IDs argument cannot be null.");
		
		predicate.groupRule = checkNotNull(groupRule, "Group role argument cannot be null.");
		return predicate;
	}
	
	/**
	 * Factory method for encapsulating the {@link #isRequired()} and {@link #isMultiple()} properties.
	 * @param required boolean flag for describing the required property.
	 * @param multiple boolean flag for representing the multiple property.
	 * @return the byte flag representing the above boolean value.
	 */
	public static byte createFlags(final boolean required, final boolean multiple) {
		byte flags = 0;
		if (required){ 
			flags |= MIN_CARDINALITY;
		}
		if (multiple) {
			flags |= MAX_CARDINALITY;
		}
		return flags;
	}

	/**Required or optional.*/
	private static final int MIN_CARDINALITY = 1 << 0;
	/**Single or multiple.*/
	private static final int MAX_CARDINALITY = 1 << 1;
	
	/**Type of the predicate. Cannot be {@code null}.*/
	@Nonnull private final PredicateType type;
	/**The unique ID of the description type SNOMED&nbsp;CT concept. Can be {@code null}.*/
	@Nullable private long descriptionTypeId;
	/**The humane readable name of the concrete domain data type. E.g.: {@code Vitamin} or {@code Clinically significant}.*/
	@Nullable private String dataTypeLabel;
	/**The unique came-case name of the concrete domain data type. E.g.: {@code isVitamin}. Can be {@code null}.*/
	@Nullable private String dataTypeName;
	/**Represents the concrete domain of the predicate. Can be {@code null} if the current predicate type is NOT {@link PredicateType#DATATYPE data type}.*/
	@Nullable private DataType dataTypeType;
	/**ESCG expression describing the allowed SNOMED&nbsp;CT relationship type concept IDs. Can be {@code null}.*/
	@Nullable private String relationshipTypeExpression;
	/**ESCG expression describing the allowed SNOMED&nbsp;CT relationship value concept IDs. Can be {@code null}.*/
	@Nullable private String relationshipvValueExpression;
	/**ESCG expression describing the allowed SNOMED&nbsp;CT relationship characteristic type concept IDs. Can be {@code null}.*/
	@Nullable private String characteristicTypeExpression;
	/**Enumeration instance for the relationship group role. Can be {@code null} if the current predicate instance is NOT a {@link PredicateType#RELATIONSHIP relationship type}.*/
	@Nullable private GroupRule groupRule;
	/**The parsed query expression. Represents the domain part of the MRCM attribute constraint.*/
	@Nonnull private final String queryExpression;
	/**Flags for representing the {@link #isMultiple()} and {@link #isRequired()} properties.*/
	private final byte flags;
	private long storageKey;
	
	/**
	 * Private constructor.
	 * @param queryExpression query expression describing the domain part of the attribute constraint.
	 * @param type the type of the predicate representation.
	 * @param flags the flags for describing the {@code isMultiple} and {@code isRequired} boolean properties.
	 */
	private PredicateIndexEntry(final long storageKey, final String queryExpression, final PredicateType type, final byte flags) {
		this.storageKey = storageKey;
		this.queryExpression = checkNotNull(queryExpression, "Query expression argument cannot be null.");
		this.type = checkNotNull(type, "Predicate type argument cannot be null.");
		this.flags = flags;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (storageKey ^ (storageKey >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PredicateIndexEntry other = (PredicateIndexEntry) obj;
		if (storageKey != other.storageKey)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("StorageKey", storageKey).add("Type", getType()).toString();
	}

	/**
	 * Returns with the type of the current predicate instance.
	 * @return the predicate type.
	 * @see PredicateType.
	 */
	public PredicateType getType() {
		return type;
	}

	/**
	 * Returns with the unique ID of the description type SNOMED&nbsp;CT concept.
	 * @return the ID of the description type concept.
	 */
	public long getDescriptionTypeId() {
		Preconditions.checkState(PredicateType.DESCRIPTION.equals(type), "Predicate type was not a description type but " + type);
		return descriptionTypeId;
	}

	/**
	 * Returns with the unique name of the concrete domain data type. This name *SHOULD* uniquely identify the predicate.
	 * <br>The format is given in camel-case. E.g.: {@code isVitamin}.
	 * @return the unique camel-case name of the concrete domain data type.
	 */
	public String getDataTypeName() {
		Preconditions.checkState(PredicateType.DATATYPE.equals(type), "Predicate type was not a data type type but " + type);
		return dataTypeName;
	}
	
	/**
	 * Returns with the humane readable name of the concrete domain data type. E.g.: {@code Vitamin}.
	 * @return the human readable name of the data type.
	 */
	public String getDataTypeLabel() {
		Preconditions.checkState(PredicateType.DATATYPE.equals(type), "Predicate type was not a data type type but " + type);
		return dataTypeLabel;
	}
	
	/**
	 * Returns with the type of the concrete domain.
	 * @return the concrete domain.
	 */
	public DataType getDataTypeType() {
		Preconditions.checkState(PredicateType.DATATYPE.equals(type), "Predicate type was not a data type type but " + type);
		return dataTypeType;
	}

	/**
	 * Returns with an ESCG expression describing the SNOMED&nbsp;CT relationship characteristic type concept IDs associated with the current predicate.
	 * @return the relationship characteristic type concept IDs represented as an ESCG expression.
	 */
	public String getCharacteristicTypeExpression() {
		Preconditions.checkState(PredicateType.RELATIONSHIP.equals(type), "Predicate type was not a relationship type but " + type);
		return characteristicTypeExpression;
	}

	/**
	 * Returns with an ESCG expression describing the SNOMED&nbsp;CT relationship type concept IDs associated with the current predicate.
	 * @return the relationship type concept IDs represented as an ESCG expression.
	 */
	public String getRelationshipTypeExpression() {
		Preconditions.checkState(PredicateType.RELATIONSHIP.equals(type), "Predicate type was not a relationship type but " + type);
		return relationshipTypeExpression;
	}
	
	/**
	 * Returns with an ESCG expression describing the SNOMED&nbsp;CT relationship value concept IDs associated with the current predicate.
	 * @return the relationship value concept IDs represented as an ESCG expression.
	 */
	public String getRelationshipValueExpression() {
		Preconditions.checkState(PredicateType.RELATIONSHIP.equals(type), "Predicate type was not a relationship type but " + type);
		return relationshipvValueExpression;
	}
	
	/**
	 * Returns with the group role.
	 * @return the group role.
	 */
	public GroupRule getGroupRule() {
		Preconditions.checkState(PredicateType.RELATIONSHIP.equals(type), "Predicate type was not a relationship type but " + type);
		return groupRule;
	}

	/**
	 * Returns with the value of the {@link #flags} property.
	 * @return the value of the {@link #flags} property.
	 */
	public byte getFlags() {
		return flags;
	}
	
	/**
	 * Returns {@code true} if the predicate is required according to the associated MRCM rule. 
	 * @return {@code true} if the predicate is required, otherwise {@code false}.
	 */
	public boolean isRequired() {
		return isAnyFlagSet(MIN_CARDINALITY);
	}

	/**
	 * Returns {@code true} if the predicate is multiple according to the associated MRCM rule. 
	 * @return {@code true} if the predicate is multiple, otherwise {@code false}.
	 */
	public boolean isMultiple() {
		return isAnyFlagSet(MAX_CARDINALITY);
	}
	
	/**
	 * Returns with the query expression wrapper representing the domain part of the MRCM attribute constraint.
	 * @return the query expression wrapper.
	 */
	public String getQueryExpression() {
		return queryExpression;
	}

	/*helper method to extract boolean properties from a byte flag*/
	private boolean isAnyFlagSet(final int flag) {
		return (flags & flag) != 0;
	}

	@Override
	public String getId() {
		return Long.toString(storageKey);
	}

	@Override
	public String getLabel() {
		return toString();
	}
	
	@Override
	public void setLabel(String label) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getScore() {
		return 0.0f;
	}

	@Override
	public long getStorageKey() {
		return storageKey;
	}
	
	@Override
	public String getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.PREDICATE_TYPE;
	}
}