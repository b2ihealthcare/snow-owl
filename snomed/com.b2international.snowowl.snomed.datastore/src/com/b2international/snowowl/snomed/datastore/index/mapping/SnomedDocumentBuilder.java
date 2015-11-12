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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CORRELATION_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_ADVICE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_CATEGORY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_PRIORITY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_RULE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_QUERY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_INFERRED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderFactory;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.IntIndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

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
			final IntIndexField typeField = Mappings.type();
			final boolean refSetMember = doc.getField(typeField.fieldName()) == null;
			final Document newDoc = new Document();
			final SnomedDocumentBuilder newDocBuilder = new SnomedDocumentBuilder(newDoc);
			
			final Builder<IndexField<?>> fieldsToCopy = ImmutableSet.<IndexField<?>>builder();
			final IndexField<Long> storageKey = Mappings.storageKey();
			fieldsToCopy.add(storageKey);
			
			// compute type specific fields
			if (refSetMember) {
				fieldsToCopy
					.add(SnomedMappings.active())
					.add(SnomedMappings.module())
					.add(SnomedMappings.memberRefSetType())
					.add(SnomedMappings.memberRefSetId())
					.add(SnomedMappings.memberReferencedComponentId())
					.add(SnomedMappings.memberReferencedComponentType())
					.add(SnomedMappings.released())
					.add(Mappings.stringField(REFERENCE_SET_MEMBER_UUID))
					.add(SnomedMappings.effectiveTime());
				final SnomedRefSetType refSetType = SnomedRefSetType.get(SnomedMappings.memberRefSetType().getValue(doc));
				switch (refSetType) {
				case SIMPLE: break;
				case ASSOCIATION:
					fieldsToCopy.add(Mappings.stringField(REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID));
					break;
				case ATTRIBUTE_VALUE:
					fieldsToCopy.add(Mappings.stringField(REFERENCE_SET_MEMBER_VALUE_ID));
					break;
				case QUERY:
					fieldsToCopy.add(Mappings.stringField(REFERENCE_SET_MEMBER_QUERY));
					break;
				case EXTENDED_MAP:
				case COMPLEX_MAP:
					fieldsToCopy
						.add(Mappings.storedOnlyIntField(REFERENCE_SET_MEMBER_MAP_GROUP))
						.add(Mappings.storedOnlyIntField(REFERENCE_SET_MEMBER_MAP_PRIORITY))
						.add(Mappings.stringField(REFERENCE_SET_MEMBER_MAP_RULE))
						.add(Mappings.stringField(REFERENCE_SET_MEMBER_MAP_ADVICE))
						.add(Mappings.longField(REFERENCE_SET_MEMBER_MAP_CATEGORY_ID))
						.add(Mappings.longField(REFERENCE_SET_MEMBER_CORRELATION_ID))
						.add(Mappings.stringField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID))
						.add(Mappings.intField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID));
					break;
				case DESCRIPTION_TYPE:
					fieldsToCopy
						.add(Mappings.longField(REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID))
						.add(Mappings.intField(REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH));
					break;
				case LANGUAGE:
					fieldsToCopy
						.add(Mappings.longField(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID));
					break;
				case CONCRETE_DATA_TYPE:
					fieldsToCopy
						.add(Mappings.longField(REFERENCE_SET_MEMBER_OPERATOR_ID))
						.add(Mappings.stringDocValuesField(REFERENCE_SET_MEMBER_SERIALIZED_VALUE))
						.add(Mappings.longDocValuesField(REFERENCE_SET_MEMBER_UOM_ID))
						.add(Mappings.longField(REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID))
						.add(Mappings.intDocValuesField(REFERENCE_SET_MEMBER_DATA_TYPE_VALUE))
						.add(Mappings.longDocValuesField(REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID));
					break;
				case SIMPLE_MAP:
					fieldsToCopy
						.add(Mappings.stringField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID))
						.add(Mappings.intField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID))
						.add(Mappings.textField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION));
					break;
				case MODULE_DEPENDENCY:
					fieldsToCopy
						.add(Mappings.longField(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME))
						.add(Mappings.longField(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME));
					break;
				default: throw new IllegalArgumentException("Unknown refset type " + refSetType);
				}
			} else {
				fieldsToCopy
					.add(typeField)
					.add(SnomedMappings.id())
					.add(SnomedMappings.module());
				final short type = typeField.getShortValue(doc);
				switch (type) {
				case SnomedTerminologyComponentConstants.PREDICATE_TYPE_ID:
					// ignored
					break;
				case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
				case SnomedTerminologyComponentConstants.REFSET_NUMBER:
					fieldsToCopy
						.add(SnomedMappings.refSetStorageKey())
						.add(SnomedMappings.active())
						.add(SnomedMappings.ancestor())
						.add(SnomedMappings.parent())
						.add(SnomedMappings.iconId())
						.add(SnomedMappings.ancestor(Concepts.STATED_RELATIONSHIP))
						.add(SnomedMappings.parent(Concepts.STATED_RELATIONSHIP))
						.add(SnomedMappings.conceptReferringRefSetId())
						.add(SnomedMappings.conceptReferringMappingRefSetId())
						.add(SnomedMappings.primitive())
						.add(SnomedMappings.exhaustive())
						.add(SnomedMappings.released())
						.add(SnomedMappings.effectiveTime())
						.add(Mappings.floatDocValuesField(CONCEPT_DEGREE_OF_INTEREST))
						.add(Mappings.stringField(COMPONENT_REFERRING_PREDICATE))
						.add(Mappings.intField(REFERENCE_SET_TYPE))
						.add(Mappings.intField(REFERENCE_SET_REFERENCED_COMPONENT_TYPE))
						.add(Mappings.intField(REFERENCE_SET_STRUCTURAL));
					// handle special fields here
					final ISnomedComponentService componentService = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
					//XXX intentionally works on MAIN
					final long namespaceId = componentService.getExtensionConceptId(BranchPathUtils.createMainPath(), SnomedMappings.id().getValueAsString(doc));
					Mappings.searchOnlyLongField(SnomedIndexBrowserConstants.CONCEPT_NAMESPACE_ID).addTo(newDoc, namespaceId);
					break;
				case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
					fieldsToCopy
						.add(Mappings.label())
						.add(SnomedMappings.active())
						.add(Mappings.storedOnlyLongFieldWithDocValues(DESCRIPTION_CASE_SIGNIFICANCE_ID))
						.add(Mappings.storedOnlyIntField(COMPONENT_RELEASED))
						.add(SnomedMappings.effectiveTime())
						.add(SnomedMappings.descriptionConcept())
						.add(SnomedMappings.descriptionType());
					break;
				case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
					fieldsToCopy
						.add(SnomedMappings.active())
						.add(SnomedMappings.relationshipType())
						.add(SnomedMappings.relationshipCharacteristicType())
						.add(Mappings.longDocValuesField(RELATIONSHIP_VALUE_ID))
						.add(Mappings.longDocValuesField(RELATIONSHIP_OBJECT_ID))
						.add(Mappings.storedOnlyIntField(COMPONENT_RELEASED))
						.add(SnomedMappings.effectiveTime())
						.add(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_GROUP))
						.add(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_UNION_GROUP))
						.add(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_DESTINATION_NEGATED))
						.add(Mappings.storedOnlyIntField(RELATIONSHIP_INFERRED))
						.add(Mappings.storedOnlyIntFieldWithDocValues(RELATIONSHIP_UNIVERSAL));
					break;
				default:
					throw new IllegalArgumentException("Unknown document type: " + type);
				}
			}
			
			// copy all registered fields
			for (IndexField<?> field : fieldsToCopy.build()) {
				field.copyTo(doc, newDoc);
			}
			
			
			return newDocBuilder;
		}
	}

	protected SnomedDocumentBuilder() {
		super();
	}
	
	protected SnomedDocumentBuilder(Document doc) {
		super(doc);
	}
	
	@Override
	protected SnomedDocumentBuilder getSelf() {
		return this;
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
		return update(SnomedMappings.iconId(), value);
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
		return update(SnomedMappings.active(), toIntValue(value));
	}

	public SnomedDocumentBuilder released(boolean value) {
		return update(SnomedMappings.released(), toIntValue(value));
	}
	
	public SnomedDocumentBuilder exhaustive(boolean value) {
		return update(SnomedMappings.exhaustive(), toIntValue(value));
	}
	
	public SnomedDocumentBuilder primitive(boolean value) {
		return update(SnomedMappings.primitive(), toIntValue(value));
	}

	public SnomedDocumentBuilder module(String value) {
		return module(Long.valueOf(value));
	}

	public SnomedDocumentBuilder module(Long value) {
		return update(SnomedMappings.module(), value);
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
		return update(SnomedMappings.relationshipCharacteristicType(), value);
	}

	public SnomedDocumentBuilder descriptionType(Long value) {
		return update(SnomedMappings.descriptionType(), value);
	}
	
	public SnomedDocumentBuilder descriptionConcept(Long value) {
		return update(SnomedMappings.descriptionConcept(), value);
	}

	public SnomedDocumentBuilder conceptReferringRefSetId(Long value) {
		return addToDoc(SnomedMappings.conceptReferringRefSetId(), value);
	}
	
	public SnomedDocumentBuilder conceptReferringMappingRefSetId(Long value) {
		return addToDoc(SnomedMappings.conceptReferringMappingRefSetId(), value);
	}

	public final SnomedDocumentBuilder effectiveTime(Long value) {
		return addToDoc(SnomedMappings.effectiveTime(), value);
	}
}
