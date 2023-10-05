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

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.linuxforhealth.fhir.model.r5.resource.*;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.code.*;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.FhirDates;

/**
 * @since 9.0
 */
public abstract class AbstractConverter_50 {

	protected AbstractConverter_50() {
		// Empty constructor
	}
	
	// Primitive data types

	protected final Id fromInternal(com.b2international.snowowl.fhir.core.model.dt.Id id) {
		if (id == null || StringUtils.isEmpty(id.getIdValue())) {
			return null;
		}
		
		return Id.of(id.getIdValue());
	}
	
	protected final java.lang.String fromInternalToString(com.b2international.snowowl.fhir.core.model.dt.Id id) {
		if (id == null || StringUtils.isEmpty(id.getIdValue())) {
			return null;
		}
		
		return id.getIdValue();
	}

	protected final Uri fromInternal(com.b2international.snowowl.fhir.core.model.dt.Uri uri) {
		if (uri == null || StringUtils.isEmpty(uri.getUriValue())) {
			return null;
		}
		
		return Uri.of(uri.getUriValue());
	}

	protected final Uri fromInternalToUri(java.lang.String uriValue) {
		if (StringUtils.isEmpty(uriValue)) {
			return null;
		}
		
		return Uri.of(uriValue);
	}
	
	protected final Canonical fromInternalToCanonical(com.b2international.snowowl.fhir.core.model.dt.Uri uri) {
		if (uri == null || StringUtils.isEmpty(uri.getUriValue())) {
			return null;
		}
		
		return Canonical.of(uri.getUriValue());
	}
	
	protected final Code fromInternalToCode(java.lang.String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		
		return Code.of(value);
	}
	
	protected final Code fromInternal(com.b2international.snowowl.fhir.core.model.dt.Code code) {
		if (code == null || StringUtils.isEmpty(code.getCodeValue())) {
			return null;
		}
		
		return Code.of(code.getCodeValue());
	}
	
	protected final Boolean fromInternal(java.lang.Boolean value) {
		if (value == null) {
			return null;
		}
		
		return Boolean.of(value);
	}
	
	protected final String fromInternal(java.lang.String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		
		return String.of(value);
	}

	protected final Coding fromInternal(com.b2international.snowowl.fhir.core.model.dt.Coding coding) {
		if (coding == null) {
			return null;
		}
		
		Coding.Builder builder = Coding.builder();
		
		builder.system(fromInternal(coding.getSystem()));
		builder.code(fromInternal(coding.getCode()));
		builder.version(coding.getVersion());
		builder.display(coding.getDisplay());
		
		return builder.build();
	}

	
	protected final DateTime fromInternal(Date date) {
		if (date == null) {
			return null;
		}
		
		return DateTime.of(date.toInstant().atZone(ZoneOffset.UTC));
	}

	protected final Instant fromInternal(com.b2international.snowowl.fhir.core.model.dt.Instant instant) {
		if (instant == null) {
			return null;
		}
		
		return Instant.of(instant.getInstant());
	}

	protected final Integer fromInternal(java.lang.Integer value) {
		if (value == null) {
			return null;
		}
		
		return Integer.of(value);
	}
	
	protected final Decimal fromInternal(java.lang.Float value) {
		if (value == null) {
			return null;
		}
		
		return Decimal.of(value);
	}

	protected final Decimal fromInternal(java.lang.Double value) {
		if (value == null) {
			return null;
		}
		
		return Decimal.of(value);
	}
	
	protected final Markdown fromInternalToMarkdown(java.lang.String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		
		return Markdown.of(value);
	}
	
	protected final UnsignedInt fromInternalToUnsignedInt(java.lang.Integer value) {
		if (value == null) {
			return null;
		}
		
		return UnsignedInt.of(value);
	}
	
	// Elements

	protected final Meta fromInternal(com.b2international.snowowl.fhir.core.model.Meta meta) {
		if (meta == null) {
			return null;
		}
		
		Meta.Builder builder = Meta.builder();
		
		builder.lastUpdated(fromInternal(meta.getLastUpdated()));
		builder.versionId(fromInternal(meta.getVersionId()));
		
		var tags = meta.getTags();
		if (!CompareUtils.isEmpty(tags)) {
			for (var tag : tags) {
				if (tag != null) {
					builder.tag(fromInternal(tag));
				}
			}
		}
		
		return builder.build();
	}

	protected final Narrative fromInternal(com.b2international.snowowl.fhir.core.model.dt.Narrative narrative) {
		if (narrative == null) {
			return null;
		}
		
		Narrative.Builder builder = Narrative.builder();
		
		if (!StringUtils.isEmpty(narrative.getDiv())) {
			// XXX: Assuming escapes do not need to be treated
			builder.div(Xhtml.of(narrative.getDiv()));
		}
		
		Code status = fromInternal(narrative.getStatus());
		if (status != null) {
			builder.status(NarrativeStatus.of(status.getValue()));
		}
		
		return builder.build();
	}
	
	protected final Identifier fromInternal(com.b2international.snowowl.fhir.core.model.dt.Identifier identifier) {
		if (identifier == null) {
			return null;
		}
		
		Identifier.Builder builder = Identifier.builder();
		
		Code use = fromInternal(identifier.getUse());
		if (use != null) {
			builder.use(IdentifierUse.of(use.getValue()));
		}
		
		builder.system(fromInternal(identifier.getSystem()));
		builder.value(identifier.getValue());
		
		return builder.build();
	}

	protected final ContactDetail fromInternal(com.b2international.snowowl.fhir.core.model.ContactDetail contactDetail) {
		if (contactDetail == null) {
			return null;
		}
		
		ContactDetail.Builder builder = ContactDetail.builder();
		
		// "id" is not converted
		// "extension" is not converted
		builder.name(contactDetail.getName());
		
		var telecoms = contactDetail.getTelecoms();
		if (!CompareUtils.isEmpty(telecoms)) {
			for (var telecom : telecoms) {
				if (telecom != null) {
					builder.telecom(fromInternal(telecom));
				}
			}
		}
		
		return builder.build();
	}

	protected final ContactPoint fromInternal(com.b2international.snowowl.fhir.core.model.dt.ContactPoint contactPoint) {
		if (contactPoint == null) {
			return null;
		}
		
		ContactPoint.Builder builder = ContactPoint.builder();
		
		// "id" is not converted
		// "extension" is not converted
		
		Code system = fromInternal(contactPoint.getSystem());
		if (system != null) {
			builder.system(ContactPointSystem.of(system.getValue()));
		}
		
		builder.value(contactPoint.getValue());
		
		Code use = fromInternal(contactPoint.getUse());
		if (use != null) {
			builder.use(ContactPointUse.of(use.getValue()));
		}
		
		// "rank" is not converted
		// "period" is not converted
		
		return builder.build();
	}
	
	// Resources
	
	protected final Resource fromInternal(com.b2international.snowowl.fhir.core.model.FhirResource resource) {
		if (resource == null) {
			return null;
		}
		
		if (resource instanceof com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem codeSystem) {
			return CodeSystemConverter_50.INSTANCE.fromInternal(codeSystem);
		} else if (resource instanceof com.b2international.snowowl.fhir.core.model.valueset.ValueSet valueSet) {
			return ValueSetConverter_50.INSTANCE.fromInternal(valueSet);
		} else if (resource instanceof com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap conceptMap) {
			return ConceptMapConverter_50.INSTANCE.fromInternal(conceptMap);
		} else {
			throw new IllegalArgumentException("Unsupported resource type '" + resource.getClass().getSimpleName() + "'.");
		}
	}

	protected final void fromInternalResource(
		Resource.Builder builder, 
		com.b2international.snowowl.fhir.core.model.FhirResource resource
	) {
		builder.id(fromInternalToString(resource.getId()));
		builder.meta(fromInternal(resource.getMeta()));
		builder.implicitRules(fromInternal(resource.getImplicitRules()));
		builder.language(fromInternal(resource.getLanguage()));
	}

	protected final void fromInternalDomainResource(
		DomainResource.Builder builder, 
		com.b2international.snowowl.fhir.core.model.DomainResource domainResource
	) {
		builder.text(fromInternal(domainResource.getText()));
		// "contained" is not converted
		// "extension" is not converted
		// "modifierExtension" is not converted
	}
	
	protected final void fromInternalCanonicalResource(
		CanonicalResource.Builder builder, 
		com.b2international.snowowl.fhir.core.model.MetadataResource metadataResource
	) {
		builder.url(fromInternal(metadataResource.getUrl()));
		// "identifier" is pushed down to individual resources, not present in superclass
		builder.version(fromInternal(metadataResource.getVersion()));
		// "versionAlgorithm[x]" is not mapped
		builder.name(fromInternal(metadataResource.getName()));
		builder.title(fromInternal(metadataResource.getTitle()));
		
		Code status = fromInternal(metadataResource.getStatus());
		if (status != null) {
			builder.status(PublicationStatus.of(status.getValue()));
		}
		
		builder.experimental(metadataResource.getExperimental());
		builder.date(fromInternal(metadataResource.getDate()));
		builder.publisher(fromInternal(metadataResource.getPublisher()));
		
		var contacts = metadataResource.getContacts();
		if (!CompareUtils.isEmpty(contacts)) {
			for (var contact : contacts) {
				if (contact != null) {
					builder.contact(fromInternal(contact));
				}
			}
		}
		
		builder.description(fromInternalToMarkdown(metadataResource.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(fromInternalToMarkdown(metadataResource.getPurpose()));
		// "copyright" is pushed down to individual resources, not present in superclass
		// "copyrightLabel" is not converted
	}
	
	// Primitive data types

	protected final com.b2international.snowowl.fhir.core.model.dt.Id toInternalId(Id id) {
		if (id == null) {
			return null;
		}
		
		return new com.b2international.snowowl.fhir.core.model.dt.Id(id.getValue());
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.Uri toInternal(Uri uri) {
		if (uri == null) {
			return null;
		}
		
		return new com.b2international.snowowl.fhir.core.model.dt.Uri(uri.getValue());
	}

	protected final java.lang.String toInternalString(Uri uri) {
		if (uri == null) {
			return null;
		}
		
		return uri.getValue();
	}
	
	protected final java.lang.String toInternal(String string) {
		if (string == null) {
			return null;
		}
		
		return string.getValue();
	}
	
	protected final java.lang.Boolean toInternal(Boolean b) {
		if (b == null) {
			return null;
		}
		
		return b.getValue();
	}

	protected final java.lang.String toInternalString(Boolean b) {
		if (b == null) {
			return null;
		}
		
		return b.getValue().toString();
	}
	
	protected final com.b2international.snowowl.fhir.core.model.dt.Uri toInternal(Canonical canonical) {
		if (canonical == null) {
			return null;
		}
		
		return new com.b2international.snowowl.fhir.core.model.dt.Uri(canonical.getValue());
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.Code toInternal(Code code) {
		if (code == null) {
			return null;
		}
		
		return com.b2international.snowowl.fhir.core.model.dt.Code.valueOf(code.getValue());
	}
	
	protected final java.lang.String toInternalString(Code code) {
		if (code == null) {
			return null;
		}
		
		return code.getValue();
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.Coding toInternal(Coding coding) {
		if (coding == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.dt.Coding.builder();
		
		builder.system(toInternalString(coding.getSystem()));
		builder.code(toInternal(coding.getCode()));
		builder.version(toInternal(coding.getVersion()));
		builder.display(toInternal(coding.getDisplay()));
		
		return builder.build();
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.CodeableConcept toInternal(CodeableConcept codeableConcept) {
		if (codeableConcept == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.dt.CodeableConcept.builder();
		
		List<Coding> codingElements = codeableConcept.getCoding();
		for (Coding coding : codingElements) {
			builder.addCoding(toInternal(coding));
		}
		
		return builder.build();
	}
	
	protected final Date toInternal(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		
		java.lang.String value = DateTime.PARSER_FORMATTER.format(dateTime.getValue());
		return FhirDates.parse(value);
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.Instant toInternal(Instant instant) {
		if (instant == null) {
			return null;
		}
		
		return com.b2international.snowowl.fhir.core.model.dt.Instant.builder()
			.instant(instant.getValue().toInstant())
			.build();
	}

	protected final java.lang.Float toInternal(Decimal value) {
		if (value == null) {
			return null;
		}
		
		// XXX: Precision can potentially be truncated
		return value.getValue().floatValue();
	}

	protected final java.lang.Double toInternalAsDouble(Decimal value) {
		if (value == null) {
			return null;
		}
		
		// XXX: Precision can potentially be truncated
		return value.getValue().doubleValue();
	}
	
	protected final java.lang.String toInternal(Markdown markdown) {
		if (markdown == null) {
			return null;
		}
		
		return markdown.getValue();
	}

	protected final java.lang.Integer toInternal(Integer value) {
		if (value == null) {
			return null;
		}
		
		return value.getValue();
	}
	
	protected final java.lang.Integer toInternal(UnsignedInt value) {
		if (value == null) {
			return null;
		}
		
		return value.getValue();
	}

	// Elements
	
	protected final com.b2international.snowowl.fhir.core.model.Meta toInternal(Meta meta) {
		if (meta == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.Meta.builder();
		
		builder.lastUpdated(toInternal(meta.getLastUpdated()));
		builder.versionId(toInternalId(meta.getVersionId()));
		
		List<Coding> tags = meta.getTag();
		for (Coding tag : tags) {
			builder.addTag(toInternal(tag));
		}
		
		return builder.build();
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.Narrative toInternal(Narrative narrative) {
		if (narrative == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.dt.Narrative.builder();
		
		if (narrative.getDiv() != null) {
			// XXX: Assuming escapes do not need to be treated
			builder.div(narrative.getDiv().getValue());
		}
		
		var status = toInternal(narrative.getStatus());
		if (status != null) {
			builder.status(com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus.forValue(status.getCodeValue()));
		}
		
		return builder.build();
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.Identifier toInternal(Identifier identifier) {
		if (identifier == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.dt.Identifier.builder();
		
		var use = toInternal(identifier.getUse());
		if (use != null) {
			builder.use(com.b2international.snowowl.fhir.core.codesystems.IdentifierUse.forValue(use.getCodeValue()));
		}
		
		builder.system(toInternal(identifier.getSystem()));
		builder.value(toInternal(identifier.getValue()));
		
		return builder.build();
	}

	protected final com.b2international.snowowl.fhir.core.model.ContactDetail toInternal(ContactDetail contactDetail) {
		if (contactDetail == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.ContactDetail.builder();
		
		// "id" is not converted
		// "extension" is not converted
		builder.name(toInternal(contactDetail.getName()));
		
		List<ContactPoint> telecoms = contactDetail.getTelecom();
		for (ContactPoint telecom : telecoms) {
			builder.addTelecom(toInternal(telecom));
		}
		
		return builder.build();
	}

	protected final com.b2international.snowowl.fhir.core.model.dt.ContactPoint toInternal(ContactPoint contactPoint) {
		if (contactPoint == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.dt.ContactPoint.builder();
		
		// "id" is not converted
		// "extension" is not converted
		
		var system = toInternal(contactPoint.getSystem());
		if (system != null) {
			builder.system(system);
		}
		
		builder.value(toInternal(contactPoint.getValue()));
		
		var use = toInternal(contactPoint.getUse());
		if (use != null) {
			builder.use(use);
		}
		
		// "rank" is not converted
		// "period" is not converted
		
		return builder.build();
	}

	// Resources
	
	protected final com.b2international.snowowl.fhir.core.model.FhirResource toInternal(Resource resource) {
		if (resource == null) {
			return null;
		}
		
		if (resource instanceof CodeSystem codeSystem) {
			return CodeSystemConverter_50.INSTANCE.toInternal(codeSystem);
		} else if (resource instanceof ValueSet valueSet) {
			return ValueSetConverter_50.INSTANCE.toInternal(valueSet);
		} else if (resource instanceof ConceptMap conceptMap) {
			return ConceptMapConverter_50.INSTANCE.toInternal(conceptMap);
		} else {
			throw new IllegalArgumentException("Unsupported resource type '" + resource.getClass().getSimpleName() + "'.");
		}
	}

	protected final void toInternalResource(
		com.b2international.snowowl.fhir.core.model.FhirResource.Builder<?, ?> builder, 
		Resource resource
	) {
		builder.id(resource.getId());
		builder.meta(toInternal(resource.getMeta()));
		builder.implicitRules(toInternal(resource.getImplicitRules()));
		builder.language(toInternal(resource.getLanguage()));
	}

	protected final void toInternalDomainResource(
		com.b2international.snowowl.fhir.core.model.DomainResource.Builder<?, ?> builder, 
		DomainResource domainResource
	) {
		builder.text(toInternal(domainResource.getText()));
		// "contained" is not converted
		// "extension" is not converted
		// "modifierExtension" is not converted
	}

	protected final void toInternalCanonicalResource(
		com.b2international.snowowl.fhir.core.model.MetadataResource.Builder<?, ?> builder, 
		CanonicalResource canonicalResource
	) {
		builder.url(toInternal(canonicalResource.getUrl()));
		// "identifier" is pushed down to individual resources, not present in builder of superclass
		builder.version(toInternal(canonicalResource.getVersion()));
		// "versionAlgorithm[x]" is not mapped
		builder.name(toInternal(canonicalResource.getName()));
		builder.title(toInternal(canonicalResource.getTitle()));
		
		var status = toInternal(canonicalResource.getStatus());
		if (status != null) {
			builder.status(com.b2international.snowowl.fhir.core.codesystems.PublicationStatus.getByCodeValue(status.getCodeValue()));
		}
		
		builder.experimental(toInternal(canonicalResource.getExperimental()));
		builder.date(toInternal(canonicalResource.getDate()));
		builder.publisher(toInternal(canonicalResource.getPublisher()));
		
		List<ContactDetail> contacts = canonicalResource.getContact();
		for (ContactDetail contact : contacts) {
			builder.addContact(toInternal(contact));
		}
		
		builder.description(toInternal(canonicalResource.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(toInternal(canonicalResource.getPurpose()));
		// "copyright" is pushed down to individual resources, not present in builder of superclass
		// "copyrightLabel" is not converted
	}

	protected final void addParameter(Parameters.Builder builder, java.lang.String name, Element value) {
		if (value == null) {
			return;
		}
		
		Parameters.Parameter parameter = Parameters.Parameter.builder()
			.name(name)
			.value(value)
			.build();
		
		builder.parameter(parameter);
	}

	protected final void addPart(Parameters.Parameter.Builder builder, java.lang.String name, Element value) {
		if (value == null) {
			return;
		}
		
		builder.part(Parameters.Parameter.builder()
			.name(name)
			.value(value)
			.build());
	}
}
