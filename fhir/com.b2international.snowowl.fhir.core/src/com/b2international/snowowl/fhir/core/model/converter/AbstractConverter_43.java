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

import org.linuxforhealth.fhir.model.r4b.resource.CodeSystem;
import org.linuxforhealth.fhir.model.r4b.resource.Resource;
import org.linuxforhealth.fhir.model.r4b.type.*;
import org.linuxforhealth.fhir.model.r4b.type.Boolean;
import org.linuxforhealth.fhir.model.r4b.type.Integer;
import org.linuxforhealth.fhir.model.r4b.type.String;
import org.linuxforhealth.fhir.model.r4b.type.code.ContactPointSystem;
import org.linuxforhealth.fhir.model.r4b.type.code.ContactPointUse;
import org.linuxforhealth.fhir.model.r4b.type.code.IdentifierUse;
import org.linuxforhealth.fhir.model.r4b.type.code.NarrativeStatus;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.FhirDates;

/**
 * @since 9.0
 */
public abstract class AbstractConverter_43 {

	protected AbstractConverter_43() {
		// Empty constructor
	}
	
	// Primitive data types

	protected Id fromInternal(com.b2international.snowowl.fhir.core.model.dt.Id id) {
		if (id == null || StringUtils.isEmpty(id.getIdValue())) {
			return null;
		}
		
		return Id.of(id.getIdValue());
	}
	
	protected java.lang.String fromInternalToString(com.b2international.snowowl.fhir.core.model.dt.Id id) {
		if (id == null || StringUtils.isEmpty(id.getIdValue())) {
			return null;
		}
		
		return id.getIdValue();
	}

	protected Uri fromInternal(com.b2international.snowowl.fhir.core.model.dt.Uri uri) {
		if (uri == null || StringUtils.isEmpty(uri.getUriValue())) {
			return null;
		}
		
		return Uri.of(uri.getUriValue());
	}

	protected Canonical fromInternalToCanonical(com.b2international.snowowl.fhir.core.model.dt.Uri uri) {
		if (uri == null || StringUtils.isEmpty(uri.getUriValue())) {
			return null;
		}
		
		return Canonical.of(uri.getUriValue());
	}
	
	protected String fromInternal(java.lang.String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		
		return String.of(value);
	}
	
	protected Code fromInternal(com.b2international.snowowl.fhir.core.model.dt.Code code) {
		if (code == null || StringUtils.isEmpty(code.getCodeValue())) {
			return null;
		}
		
		return Code.of(code.getCodeValue());
	}

	protected Coding fromInternal(com.b2international.snowowl.fhir.core.model.dt.Coding coding) {
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

	
	protected DateTime fromInternal(Date date) {
		if (date == null) {
			return null;
		}
		
		return DateTime.of(date.toInstant().atZone(ZoneOffset.UTC));
	}

	protected Instant fromInternal(com.b2international.snowowl.fhir.core.model.dt.Instant instant) {
		if (instant == null) {
			return null;
		}
		
		return Instant.of(instant.getInstant());
	}

	protected Decimal fromInternal(Float value) {
		if (value == null) {
			return null;
		}
		
		return Decimal.of(value);
	}

	protected Markdown fromInternalToMarkdown(java.lang.String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		
		return Markdown.of(value);
	}
	
	protected UnsignedInt fromInternalToUnsignedInt(java.lang.Integer value) {
		if (value == null) {
			return null;
		}
		
		return UnsignedInt.of(value);
	}
	
	// Elements

	protected Meta fromInternal(com.b2international.snowowl.fhir.core.model.Meta meta) {
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

	protected Narrative fromInternal(com.b2international.snowowl.fhir.core.model.dt.Narrative narrative) {
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
	
	protected Identifier fromInternal(com.b2international.snowowl.fhir.core.model.dt.Identifier identifier) {
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

	protected ContactDetail fromInternal(com.b2international.snowowl.fhir.core.model.ContactDetail contactDetail) {
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

	protected ContactPoint fromInternal(com.b2international.snowowl.fhir.core.model.dt.ContactPoint contactPoint) {
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
	
	protected Resource fromInternal(com.b2international.snowowl.fhir.core.model.FhirResource resource) {
		if (resource == null) {
			return null;
		}
		
		if (resource instanceof com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem codeSystem) {
			return CodeSystemConverter_43.INSTANCE.fromInternal(codeSystem);
		} else {
			throw new IllegalArgumentException("Unsupported resource type '" + resource.getClass().getSimpleName() + "'.");
		}
	}

	// Primitive data types
	
	protected com.b2international.snowowl.fhir.core.model.dt.Id toInternalId(Id id) {
		if (id == null) {
			return null;
		}
		
		return new com.b2international.snowowl.fhir.core.model.dt.Id(id.getValue());
	}

	protected com.b2international.snowowl.fhir.core.model.dt.Uri toInternal(Uri uri) {
		if (uri == null) {
			return null;
		}
		
		return new com.b2international.snowowl.fhir.core.model.dt.Uri(uri.getValue());
	}

	protected java.lang.String toInternalString(Uri uri) {
		if (uri == null) {
			return null;
		}
		
		return uri.getValue();
	}
	
	protected java.lang.String toInternal(String string) {
		if (string == null) {
			return null;
		}
		
		return string.getValue();
	}
	
	protected java.lang.Boolean toInternal(Boolean b) {
		if (b == null) {
			return null;
		}
		
		return b.getValue();
	}

	protected com.b2international.snowowl.fhir.core.model.dt.Uri toInternal(Canonical canonical) {
		if (canonical == null) {
			return null;
		}
		
		return new com.b2international.snowowl.fhir.core.model.dt.Uri(canonical.getValue());
	}

	protected com.b2international.snowowl.fhir.core.model.dt.Code toInternal(Code code) {
		if (code == null) {
			return null;
		}
		
		return com.b2international.snowowl.fhir.core.model.dt.Code.valueOf(code.getValue());
	}
	
	protected java.lang.String toInternalString(Code code) {
		if (code == null) {
			return null;
		}
		
		return code.getValue();
	}

	protected com.b2international.snowowl.fhir.core.model.dt.Coding toInternal(Coding coding) {
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

	protected Date toInternal(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		
		java.lang.String value = DateTime.PARSER_FORMATTER.format(dateTime.getValue());
		return FhirDates.parse(value);
	}

	protected com.b2international.snowowl.fhir.core.model.dt.Instant toInternal(Instant instant) {
		if (instant == null) {
			return null;
		}
		
		return com.b2international.snowowl.fhir.core.model.dt.Instant.builder()
			.instant(instant.getValue().toInstant())
			.build();
	}

	protected java.lang.Float toInternal(Decimal value) {
		if (value == null) {
			return null;
		}
		
		// XXX: Precision will be truncated
		return value.getValue().floatValue();
	}

	protected java.lang.String toInternal(Markdown markdown) {
		if (markdown == null) {
			return null;
		}
		
		return markdown.getValue();
	}

	protected java.lang.Integer toInternal(Integer value) {
		if (value == null) {
			return null;
		}
		
		return value.getValue();
	}
	
	protected java.lang.Integer toInternal(UnsignedInt value) {
		if (value == null) {
			return null;
		}
		
		return value.getValue();
	}

	// Elements
	
	protected com.b2international.snowowl.fhir.core.model.Meta toInternal(Meta meta) {
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

	protected com.b2international.snowowl.fhir.core.model.dt.Narrative toInternal(Narrative narrative) {
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

	protected com.b2international.snowowl.fhir.core.model.dt.Identifier toInternal(Identifier identifier) {
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

	protected com.b2international.snowowl.fhir.core.model.ContactDetail toInternal(ContactDetail contactDetail) {
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

	protected com.b2international.snowowl.fhir.core.model.dt.ContactPoint toInternal(ContactPoint contactPoint) {
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
	
	protected com.b2international.snowowl.fhir.core.model.FhirResource toInternal(Resource resource) {
		if (resource == null) {
			return null;
		}
		
		if (resource instanceof CodeSystem codeSystem) {
			return CodeSystemConverter_43.INSTANCE.toInternal(codeSystem);
		} else {
			throw new IllegalArgumentException("Unsupported resource type '" + resource.getClass().getSimpleName() + "'.");
		}
	}
}
