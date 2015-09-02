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
package com.b2international.snowowl.snomed.datastore.index.mapping;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.3
 */
public class SnomedDocumentBuilder extends DocumentBuilderBase<SnomedDocumentBuilder> {

	public static class Factory implements DocumentBuilderFactory<SnomedDocumentBuilder> {

		@Override
		public SnomedDocumentBuilder createBuilder() {
			return new SnomedDocumentBuilder();
		}

		@Override
		public SnomedDocumentBuilder createBuilder(Document doc) {
			return new SnomedDocumentBuilder(doc);
		}

	}

	protected SnomedDocumentBuilder() {
		super();
	}
	
	protected SnomedDocumentBuilder(Document doc) {
		super(doc);
	}

	@Override
	public final SnomedDocumentBuilder id(String value) {
		return id(Long.valueOf(value));
	}
	
	@Override
	public final SnomedDocumentBuilder parent(String value) {
		return parent(Long.valueOf(value));
	}
	
	@Override
	public final SnomedDocumentBuilder ancestor(String value) {
		return ancestor(Long.valueOf(value));
	}
	
	@Override
	public SnomedDocumentBuilder iconId(String value) {
		return iconId(Long.valueOf(value));
	}
	
	public SnomedDocumentBuilder iconId(Long value) {
		return addToDoc(SnomedMappings.iconId(), value);
	}
	
	public final SnomedDocumentBuilder id(Long value) {
		return addToDoc(SnomedMappings.id(), value);
	}
	
	public final SnomedDocumentBuilder parent(Long value) {
		return addToDoc(SnomedMappings.parent(), value);
	}
	
	public final SnomedDocumentBuilder ancestor(Long value) {
		return addToDoc(SnomedMappings.ancestor(), value);
	}
	
	public SnomedDocumentBuilder active(boolean value) {
		return addToDoc(SnomedMappings.active(), value ? 1 : 0);
	}

	public SnomedDocumentBuilder module(String value) {
		return module(Long.valueOf(value));
	}

	public SnomedDocumentBuilder module(Long value) {
		return addToDoc(SnomedMappings.module(), value);
	}

	public SnomedDocumentBuilder memberRefSetType(SnomedRefSetType type) {
		return memberRefSetType(type.getValue());
	}

	public SnomedDocumentBuilder memberRefSetType(int value) {
		return addToDoc(SnomedMappings.memberRefSetType(), value);
	}

	public SnomedDocumentBuilder memberReferencedComponentType(int value) {
		return addToDoc(SnomedMappings.memberReferencedComponentType(), value);
	}

	public SnomedDocumentBuilder memberReferencedComponentId(String value) {
		return memberReferencedComponentId(Long.valueOf(value));
	}
	
	public SnomedDocumentBuilder memberReferencedComponentId(Long value) {
		return addToDoc(SnomedMappings.memberReferencedComponentId(), value);
	}

	public SnomedDocumentBuilder memberRefSetId(String value) {
		return memberRefSetId(Long.valueOf(value));
	}

	public SnomedDocumentBuilder memberRefSetId(Long value) {
		return addToDoc(SnomedMappings.memberRefSetId(), value);
	}

	public SnomedDocumentBuilder relationshipType(Long value) {
		return addToDoc(SnomedMappings.relationshipType(), value);
	}

	public SnomedDocumentBuilder relationshipCharacteristicType(Long value) {
		return addToDoc(SnomedMappings.relationshipCharacteristicType(), value);
	}

	public SnomedDocumentBuilder descriptionType(Long value) {
		return addToDoc(SnomedMappings.descriptionType(), value);
	}
	
	public SnomedDocumentBuilder descriptionConcept(Long value) {
		return addToDoc(SnomedMappings.descriptionConcept(), value);
	}

}
