/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

	private static final long serialVersionUID = 1L;
	private String id;
	private long itemId;
	private String namespace;
	private int formatIdentifier;
	private int componentIdentifier;
	private int checkDigit;

	public SnomedIdentifierImpl(final long itemId, final String namespace, final int partitionIdentifier, final int componentIdentifier, final int checkDigit) {
		this.itemId = itemId;
		this.namespace = namespace;
		this.formatIdentifier = partitionIdentifier;
		this.componentIdentifier = componentIdentifier;
		this.checkDigit = checkDigit;
	}

	@Override
	public long getItemId() {
		return itemId;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}
	
	@Override
	public boolean equalsNamespace(String namespace) {
		if (namespace == null & this.namespace == null) {
			return true;
		}
		
		if (this.namespace == null) {
			return false;
		}
		
		return this.namespace.equals(namespace);
	}

	@Override
	public boolean isNamespace(long namespace) {
		return equalsNamespace(String.valueOf(namespace));
	}
	
	@Override
	public boolean hasNamespace() {
		return namespace != null;
	}

	@Override
	public int getFormatIdentifier() {
		return formatIdentifier;
	}

	public int getComponentIdentifier() {
		return componentIdentifier;
	}

	@Override
	public int getCheckDigit() {
		return checkDigit;
	}

	@Override
	public ComponentCategory getComponentCategory() {
		return ComponentCategory.getByOrdinal(getComponentIdentifier());
	}
	
	@Override
	public String toString() {
		if (id == null) {
			id = String.format("%s%s%s%s%s", itemId, Strings.nullToEmpty(namespace), formatIdentifier, componentIdentifier, checkDigit);
		}
		return id;
	}

}
