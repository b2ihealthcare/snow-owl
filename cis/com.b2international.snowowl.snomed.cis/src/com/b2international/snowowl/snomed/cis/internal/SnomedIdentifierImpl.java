/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.internal;

import java.util.Objects;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifier;
import com.google.common.base.Strings;

/**
 * Representing a SNOMED CT Identifier in a Java POJO form, extracts parts of the SNOMED CT Identifer and stores them for later use.
 * 
 * @since 4.0
 */
public final class SnomedIdentifierImpl implements SnomedIdentifier {

	private static final long serialVersionUID = 1L;
	
	private final long itemId;
	private final String namespace;
	private final int formatIdentifier;
	private final int componentIdentifier;
	private final int checkDigit;
	
	// Computed on first access to toString
	private String id;

	public SnomedIdentifierImpl(final long itemId, final String namespace, final int formatIdentifier, final int componentIdentifier, final int checkDigit) {
		this.itemId = itemId;
		this.namespace = namespace;
		this.formatIdentifier = formatIdentifier;
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
		return Objects.equals(this.namespace, namespace);
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
