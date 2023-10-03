/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.converter;

import java.util.List;

import org.linuxforhealth.fhir.model.r4b.resource.CodeSystem;
import org.linuxforhealth.fhir.model.r4b.type.*;
import org.linuxforhealth.fhir.model.r4b.type.Boolean;
import org.linuxforhealth.fhir.model.r4b.type.Integer;
import org.linuxforhealth.fhir.model.r4b.type.String;
import org.linuxforhealth.fhir.model.r4b.type.code.*;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.fhir.core.model.property.*;

/**
 * @since 9.0
 */
public class CodeSystemConverter_43 extends AbstractConverter_43 implements CodeSystemConverter<CodeSystem> {

	public static final CodeSystemConverter<CodeSystem> INSTANCE = new CodeSystemConverter_43();
	
	private CodeSystemConverter_43() {
		super();
	}
	
	@Override
	public CodeSystem fromInternal(com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem codeSystem) {
		if (codeSystem == null) {
			return null;
		}
		
		CodeSystem.Builder builder = CodeSystem.builder();
		
		fromInternalResource(builder, codeSystem);
		fromInternalDomainResource(builder, codeSystem);
		
		// CodeSystem properties
		builder.url(fromInternal(codeSystem.getUrl()));
		
		var identifiers = codeSystem.getIdentifiers();
		if (!CompareUtils.isEmpty(identifiers)) {
			for (var identifier : identifiers) {
				if (identifier != null) {
					builder.identifier(fromInternal(identifier));
				}
			}
		}
		
		builder.version(codeSystem.getVersion());
		builder.name(codeSystem.getName());
		builder.title(codeSystem.getTitle());
		
		Code status = fromInternal(codeSystem.getStatus());
		if (status != null) {
			builder.status(PublicationStatus.of(status.getValue()));
		}
		
		builder.experimental(codeSystem.getExperimental());
		builder.date(fromInternal(codeSystem.getDate()));
		builder.publisher(codeSystem.getPublisher());
		
		var contacts = codeSystem.getContacts();
		if (!CompareUtils.isEmpty(contacts)) {
			for (var contact : contacts) {
				if (contact != null) {
					builder.contact(fromInternal(contact));
				}
			}
		}
		
		builder.description(fromInternalToMarkdown(codeSystem.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(fromInternalToMarkdown(codeSystem.getPurpose()));
		builder.copyright(fromInternalToMarkdown(codeSystem.getCopyright()));
		
		builder.caseSensitive(codeSystem.getCaseSensitive());
		builder.valueSet(fromInternalToCanonical(codeSystem.getValueSet()));
		
		Code hierarchyMeaning = fromInternal(codeSystem.getHierarchyMeaning());
		if (hierarchyMeaning != null) {
			builder.hierarchyMeaning(CodeSystemHierarchyMeaning.of(hierarchyMeaning.getValue()));
		}
		
		builder.compositional(codeSystem.getCompositional());
		builder.versionNeeded(codeSystem.getVersionNeeded());
		
		Code content = fromInternal(codeSystem.getContent());
		if (content != null) {
			builder.content(CodeSystemContentMode.of(content.getValue()));
		}
		
		builder.supplements(fromInternalToCanonical(codeSystem.getSupplements()));
		builder.count(fromInternalToUnsignedInt(codeSystem.getCount()));
		
		var filters = codeSystem.getFilters();
		if (!CompareUtils.isEmpty(filters)) {
			for (var filter : filters) {
				if (filter != null) {
					builder.filter(fromInternal(filter));
				}
			}
		}
		
		var properties = codeSystem.getProperties();
		if (!CompareUtils.isEmpty(properties)) {
			for (var property : properties) {
				if (property != null) {
					builder.property(fromInternal(property));
				}
			}
		}
		
		var concepts = codeSystem.getConcepts();
		if (!CompareUtils.isEmpty(concepts)) {
			for (var concept : concepts) {
				if (concept != null) {
					builder.concept(fromInternal(concept));
				}
			}
		}
		
		return builder.build();
	}

	

	
	
	// Elements

	private CodeSystem.Filter fromInternal(com.b2international.snowowl.fhir.core.model.codesystem.Filter filter) {
		if (filter == null) {
			return null;
		}
		
		CodeSystem.Filter.Builder builder = CodeSystem.Filter.builder();
		
		builder.code(fromInternal(filter.getCode()));
		builder.description(filter.getDescription());
	
		var operators = filter.getOperators();
		if (!CompareUtils.isEmpty(operators)) {
			for (var operator : operators) {
				Code operatorCode = fromInternal(operator);
				if (operatorCode != null) {
					builder.operator(FilterOperator.of(operatorCode.getValue()));
				}
			}
		}
		
		builder.value(filter.getValue());
		
		return builder.build();
	}
	
	private CodeSystem.Property fromInternal(com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty property) {
		if (property == null) {
			return null;
		}
		
		CodeSystem.Property.Builder builder = CodeSystem.Property.builder();
		
		builder.code(fromInternal(property.getCode()));
		builder.uri(fromInternal(property.getUri()));
		builder.description(property.getDescription());
		
		Code type = fromInternal(property.getType());
		if (type != null) {
			builder.type(PropertyType.of(type.getValue()));
		}

		return builder.build();
	}
	
	
	private CodeSystem.Concept fromInternal(com.b2international.snowowl.fhir.core.model.codesystem.Concept concept) {
		if (concept == null) {
			return null;
		}
		
		CodeSystem.Concept.Builder builder = CodeSystem.Concept.builder();
		
		builder.code(fromInternal(concept.getCode()));
		builder.display(concept.getDisplay());
		builder.definition(concept.getDefinition());
		
		var designations = concept.getDesignations();
		if (!CompareUtils.isEmpty(designations)) {
			for (var designation : designations) {
				if (designation != null) {
					builder.designation(fromInternal(designation));
				}
			}
		}
		
		var properties = concept.getProperties();
		if (!CompareUtils.isEmpty(properties)) {
			for (var property : properties) {
				if (property != null) {
					builder.property(fromInternal(property));
				}
			}
		}

		var children = concept.getChildren();
		if (!CompareUtils.isEmpty(children)) {
			for (var child : children) {
				if (child != null) {
					builder.concept(fromInternal(child));
				}
			}
		}

		return builder.build();
	}

	private CodeSystem.Concept.Designation fromInternal(com.b2international.snowowl.fhir.core.model.Designation designation) {
		if (designation == null) {
			return null;
		}
		
		CodeSystem.Concept.Designation.Builder builder = CodeSystem.Concept.Designation.builder();
		
		builder.language(fromInternal(designation.getLanguageCode()));
		builder.use(fromInternal(designation.getUse()));
		builder.value(designation.getValue());
		
		return builder.build();
	}
	
	private CodeSystem.Concept.Property fromInternal(com.b2international.snowowl.fhir.core.model.property.ConceptProperty<?> property) {
		if (property == null) {
			return null;
		}
		
		CodeSystem.Concept.Property.Builder builder = CodeSystem.Concept.Property.builder();
		
		builder.code(fromInternal(property.getCode()));
		
		switch (property.getPropertyType()) {
			case BOOLEAN:
				builder.value((java.lang.Boolean) property.getValue());
				break;
			case CODE:
				builder.value(fromInternal((com.b2international.snowowl.fhir.core.model.dt.Code) property.getValue()));
				break;
			case CODING:
				builder.value(fromInternal((com.b2international.snowowl.fhir.core.model.dt.Coding) property.getValue()));
				break;
			case DATETIME:
				builder.value(fromInternal((java.util.Date) property.getValue()));
				break;
			case DECIMAL:
				builder.value(fromInternal((java.lang.Float) property.getValue()));
				break;
			case INTEGER:
				builder.value((java.lang.Integer) property.getValue());
				break;
			case STRING:
				builder.value((java.lang.String) property.getValue());
				break;
			default:
				throw new IllegalArgumentException("Unexpected property type '" + property.getPropertyType() + "'.");
		}
		
		return builder.build();
	}

	@Override
	public com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem toInternal(CodeSystem codeSystem) {
		if (codeSystem == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.builder();
		
		toInternalResource(builder, codeSystem);
		toInternalDomainResource(builder, codeSystem);
		
		// CodeSystem properties
		builder.url(toInternal(codeSystem.getUrl()));
		
		List<Identifier> identifiers = codeSystem.getIdentifier();
		for (Identifier identifier : identifiers) {
			builder.addIdentifier(toInternal(identifier));
		}
		
		builder.version(toInternal(codeSystem.getVersion()));
		builder.name(toInternal(codeSystem.getName()));
		builder.title(toInternal(codeSystem.getTitle()));
		
		var status = toInternal(codeSystem.getStatus());
		if (status != null) {
			builder.status(com.b2international.snowowl.fhir.core.codesystems.PublicationStatus.getByCodeValue(status.getCodeValue()));
		}
		
		builder.experimental(toInternal(codeSystem.getExperimental()));
		builder.date(toInternal(codeSystem.getDate()));
		builder.publisher(toInternal(codeSystem.getPublisher()));
		
		List<ContactDetail> contacts = codeSystem.getContact();
		for (ContactDetail contact : contacts) {
			builder.addContact(toInternal(contact));
		}
		
		builder.description(toInternal(codeSystem.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(toInternal(codeSystem.getPurpose()));
		builder.copyright(toInternal(codeSystem.getCopyright()));
		
		builder.caseSensitive(toInternal(codeSystem.getCaseSensitive()));
		builder.valueSet(toInternal(codeSystem.getValueSet()));
		
		var hierarchyMeaning = toInternal(codeSystem.getHierarchyMeaning());
		if (hierarchyMeaning != null) {
			builder.hierarchyMeaning(hierarchyMeaning);
		}
		
		builder.compositional(toInternal(codeSystem.getCompositional()));
		builder.versionNeeded(toInternal(codeSystem.getVersionNeeded()));
		
		var content = toInternal(codeSystem.getContent());
		if (content != null) {
			builder.content(content);
		}
		
		builder.supplements(toInternal(codeSystem.getSupplements()));
		builder.count(toInternal(codeSystem.getCount()));
		
		List<CodeSystem.Filter> filters = codeSystem.getFilter();
		for (CodeSystem.Filter filter : filters) {
			builder.addFilter(toInternal(filter));
		}
		
		List<CodeSystem.Property> properties = codeSystem.getProperty();
		for (CodeSystem.Property property : properties) {
			builder.addProperty(toInternal(property));
		}
		
		List<CodeSystem.Concept> concepts = codeSystem.getConcept();
		for (CodeSystem.Concept concept : concepts) {
			builder.addConcept(toInternal(concept));
		}
		
		return builder.build();
	}

	// Elements
	
	private com.b2international.snowowl.fhir.core.model.codesystem.Filter toInternal(CodeSystem.Filter filter) {
		if (filter == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.codesystem.Filter.builder();
		
		builder.code(toInternalString(filter.getCode()));
		builder.description(toInternal(filter.getDescription()));
	
		List<FilterOperator> operators = filter.getOperator();
		for (FilterOperator operator : operators) {
			var operatorCode = toInternal(operator);
			if (operatorCode != null) {
				builder.addOperator(com.b2international.snowowl.fhir.core.codesystems.FilterOperator.valueOf(operatorCode.getCodeValue()));
			}
		}
		
		builder.value(toInternal(filter.getValue()));
		
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty toInternal(CodeSystem.Property property) {
		if (property == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty.builder();
		
		builder.code(toInternal(property.getCode()));
		builder.uri(toInternal(property.getUri()));
		builder.description(toInternal(property.getDescription()));
		
		var type = toInternal(property.getType());
		if (type != null) {
			builder.type(type);
		}
	
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.codesystem.Concept toInternal(CodeSystem.Concept concept) {
		if (concept == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.codesystem.Concept.builder();
		
		builder.code(toInternalString(concept.getCode()));
		builder.display(toInternal(concept.getDisplay()));
		builder.definition(toInternal(concept.getDefinition()));
		
		List<CodeSystem.Concept.Designation> designations = concept.getDesignation();
		for (CodeSystem.Concept.Designation designation : designations) {
			builder.addDesignation(toInternal(designation));
		}
		
		List<CodeSystem.Concept.Property> properties = concept.getProperty();
		for (CodeSystem.Concept.Property property : properties) {
			builder.addProperty(toInternal(property));
		}
	
		List<CodeSystem.Concept> children = concept.getConcept();
		for (CodeSystem.Concept child : children) {
			builder.addChildConcept(toInternal(child));
		}
	
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.Designation toInternal(CodeSystem.Concept.Designation designation) {
		if (designation == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.Designation.builder();
		
		builder.language(toInternal(designation.getLanguage()));
		builder.use(toInternal(designation.getUse()));
		builder.value(toInternal(designation.getValue()));
		
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.property.ConceptProperty<?> toInternal(CodeSystem.Concept.Property property) {
		if (property == null) {
			return null;
		}
		
		final com.b2international.snowowl.fhir.core.model.property.ConceptProperty.Builder<?, ?, ?> builder;
		
		Element value = property.getValue();
		if (value instanceof Code code) {
			builder = CodeConceptProperty.builder().value(toInternal(code));
		} else if (value instanceof Coding coding) {
			builder = CodingConceptProperty.builder().value(toInternal(coding));
		} else if (value instanceof String string) {
			builder = StringConceptProperty.builder().value(toInternal(string));
		} else if (value instanceof Integer integer) {
			builder = IntegerConceptProperty.builder().value(toInternal(integer));
		} else if (value instanceof Boolean b) {
			builder = BooleanConceptProperty.builder().value(toInternal(b));
		} else if (value instanceof DateTime dateTime) {
			builder = DateTimeConceptProperty.builder().value(toInternal(dateTime));
		} else if (value instanceof Decimal decimal) {
			builder = DecimalConceptProperty.builder().value(toInternal(decimal));
		} else {
			throw new IllegalArgumentException("Unexpected property type '" + value.getClass().getSimpleName() + "'.");			
		}
		
		builder.code(toInternal(property.getCode()));
		
		return builder.build();
	}
}
