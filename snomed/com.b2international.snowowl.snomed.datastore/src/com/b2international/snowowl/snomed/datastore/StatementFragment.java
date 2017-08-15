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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;

/**
 * Represents the bare minimum of a SNOMED CT relationship (without binding the source concept).
 */
public class StatementFragment implements Serializable {

	private static final long serialVersionUID = 8281299401725022928L;

	private final long typeId;
	private final long destinationId;
	private final boolean destinationNegated;
	private final byte group;
	private final byte unionGroup;
	private final boolean universal;

	// Only stored if the original relationship identifier and storage key is known
	private final long statementId;
	private final long storageKey;

	public StatementFragment(final long typeId, final long destinationId) {
		this(typeId, destinationId, false, (byte) 0, (byte) 0, false, -1L, -1L);
	}

	public StatementFragment(final long typeId,
			final long destinationId,
			final boolean destinationNegated,
			final byte group,
			final byte unionGroup,
			final boolean universal,
			final long statementId,
			final long storageKey) {

		this.typeId = typeId;
		this.destinationId = destinationId;
		this.destinationNegated = destinationNegated;
		this.group = group;
		this.unionGroup = unionGroup;
		this.universal = universal;

		this.statementId = statementId;
		this.storageKey = storageKey;
	}

	public long getTypeId() {
		return typeId;
	}

	public long getDestinationId() {
		return destinationId;
	}

	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	public byte getGroup() {
		return group;
	}

	public byte getUnionGroup() {
		return unionGroup;
	}

	public boolean isUniversal() {
		return universal;
	}

	public long getStatementId() {
		return statementId;
	}

	public long getStorageKey() {
		return storageKey;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("StatementFragment [typeId=");
		builder.append(typeId);
		builder.append(", destinationId=");
		builder.append(destinationId);
		builder.append(", destinationNegated=");
		builder.append(destinationNegated);
		builder.append(", group=");
		builder.append(group);
		builder.append(", unionGroup=");
		builder.append(unionGroup);
		builder.append(", universal=");
		builder.append(universal);
		builder.append(", statementId=");
		builder.append(statementId);
		builder.append(", storageKey=");
		builder.append(storageKey);
		builder.append("]");
		return builder.toString();
	}
}
