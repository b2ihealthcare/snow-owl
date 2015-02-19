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
 * Bare minimum of a SNOMED&nbsp;CT relationship.
 *
 */
public class StatementFragment implements Serializable {

	private static final long serialVersionUID = 8281299401725022928L;

	private final boolean destinationNegated;
	private final boolean universal;
	private final byte group;
	private final byte unionGroup;
	private final long typeId;
	private final long destinationId;
	private final long statementId;
	private final long storageKey;

	/**
	 * Creates a statement fragment.
	 * @param statementId the ID of the statement.
	 * @param destinationId ID of the destination concept.
	 * @param typeId ID of the statement type concept.
	 * @param l
	 * @param destinationNegated {@code true} if destination is negated, otherwise {@code false}.
	 * @param universal {@code true} if universal restriction, otherwise {@code false}.
	 * @param group the statement group.
	 * @param unionGroup the statement union group.
	 */
	public StatementFragment(final long statementId,
			final long storageKey,
			final long destinationId,
			final long typeId,
			final boolean destinationNegated,
			final boolean universal,
			final byte group,
			final byte unionGroup) {

		this.storageKey = storageKey;
		this.destinationNegated = destinationNegated;
		this.universal = universal;
		this.group = group;
		this.unionGroup = unionGroup;
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.statementId = statementId;
	}

	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	public boolean isUniversal() {
		return universal;
	}

	public byte getGroup() {
		return group;
	}

	public byte getUnionGroup() {
		return unionGroup;
	}

	public long getTypeId() {
		return typeId;
	}

	public long getDestinationId() {
		return destinationId;
	}

	public long getStatementId() {
		return statementId;
	}

	public long getStorageKey() {
		return storageKey;
	}

	@Override
	public String toString() {
		return "StatementFragment [destinationNegated=" + destinationNegated + ", universal=" + universal + ", group=" + group + ", unionGroup="
				+ unionGroup + ", typeId=" + typeId + ", destinationId=" + destinationId + ", statementId=" + statementId + ", storageKey="
				+ storageKey + "]";
	}
}