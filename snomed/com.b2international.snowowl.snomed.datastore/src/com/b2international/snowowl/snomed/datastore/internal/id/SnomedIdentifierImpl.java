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
package com.b2international.snowowl.snomed.datastore.internal.id;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.google.common.base.Strings;

/**
 * Representing a SNOMED CT Identifier in a Java POJO form, extracts parts of the SNOMED CT Identifer and stores them for later use.
 * 
 * @since 4.0
 */
public final class SnomedIdentifierImpl implements SnomedIdentifier {

	private String id;
	private long itemId;
	private String namespace;
	private int partitionIdentifier;
	private int componentIdentifier;
	private int checkDigit;

	public SnomedIdentifierImpl(final long itemId, final String namespace, final int partitionIdentifier, final int componentIdentifier,
			final int checkDigit) {
		this.itemId = itemId;
		this.namespace = namespace;
		this.partitionIdentifier = partitionIdentifier;
		this.componentIdentifier = componentIdentifier;
		this.checkDigit = checkDigit;
	}

	public long getItemId() {
		return itemId;
	}

	public String getNamespace() {
		return namespace;
	}

	public int getPartitionIdentifier() {
		return partitionIdentifier;
	}

	public int getComponentIdentifier() {
		return componentIdentifier;
	}

	public int getCheckDigit() {
		return checkDigit;
	}

	public ComponentCategory getComponentCategory() {
		return ComponentCategory.getByOrdinal(getComponentIdentifier());
	}
	
	@Override
	public String toString() {
		if (id == null) {
			id = String.format("%s%s%s%s%s", itemId, Strings.nullToEmpty(namespace), partitionIdentifier, componentIdentifier, checkDigit);
		}
		return id;
	}

}
