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
package com.b2international.snowowl.fhir.core.model.r5;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.dt.*;

/**
 * @since 9.0
 */
public class FhirDataTypeConverter {

	public static org.hl7.fhir.r5.model.Bundle.BundleType toFhirBundleType(final Code coreBundleType) {
		return org.hl7.fhir.r5.model.Bundle.BundleType.fromCode(coreBundleType.getCodeValue());
	}

	public static org.hl7.fhir.r5.model.OperationOutcome.IssueType toFhirIssueType(final Code coreIssueType) {
		return org.hl7.fhir.r5.model.OperationOutcome.IssueType.fromCode(coreIssueType.getCodeValue());
	}

	public static org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity toFhirIssueSeverity(final Code coreIssueSeverity) {
		return org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity.fromCode(coreIssueSeverity.getCodeValue());
	}

	public static org.hl7.fhir.r5.model.Enumerations.PublicationStatus toFhirPublicationStatus(final Code corePublicationStatus) {
		return org.hl7.fhir.r5.model.Enumerations.PublicationStatus.fromCode(corePublicationStatus.getCodeValue());
	}

	public static org.hl7.fhir.r5.model.Narrative.NarrativeStatus toFhirNarrativeStatus(final Code coreNarrativeStatus) {
		return org.hl7.fhir.r5.model.Narrative.NarrativeStatus.fromCode(coreNarrativeStatus.getCodeValue());
	}
	
	public static org.hl7.fhir.r5.model.ContactPoint.ContactPointSystem toFhirContactPointSystem(final Code coreSystem) {
		return org.hl7.fhir.r5.model.ContactPoint.ContactPointSystem.fromCode(coreSystem.getCodeValue());
	}
	
	public static org.hl7.fhir.r5.model.Identifier.IdentifierUse toFhirIdentifierUse(final Code coreUse) {
		return org.hl7.fhir.r5.model.Identifier.IdentifierUse.fromCode(coreUse.getCodeValue());
	}
	
	public static org.hl7.fhir.r5.model.Enumerations.CodeSystemContentMode toFhirCodeSystemContentMode(final Code coreContent) {
		return org.hl7.fhir.r5.model.Enumerations.CodeSystemContentMode.fromCode(coreContent.getCodeValue());
	}
	
	public static org.hl7.fhir.r5.model.Enumerations.FilterOperator toFhirFilterOperator(final Code coreOperator) {
		return org.hl7.fhir.r5.model.Enumerations.FilterOperator.fromCode(coreOperator.getCodeValue());
	}
	
	public static org.hl7.fhir.r5.model.CodeSystem.PropertyType toFhirPropertyType(final Code coreType) {
		return org.hl7.fhir.r5.model.CodeSystem.PropertyType.fromCode(coreType.getCodeValue());
	}	
	
	public static org.hl7.fhir.r5.model.Enumerations.ConceptMapRelationship toFhirRelationship(final Code coreRelationship) {
		return org.hl7.fhir.r5.model.Enumerations.ConceptMapRelationship.fromCode(coreRelationship.getCodeValue());
	}	

	public static org.hl7.fhir.r5.model.ConceptMap.ConceptMapGroupUnmappedMode toFhirUnmappedMode(final Code coreMode) {
		return org.hl7.fhir.r5.model.ConceptMap.ConceptMapGroupUnmappedMode.fromCode(coreMode.getCodeValue());
	}	

	public static org.hl7.fhir.r5.model.InstantType toFhirInstant(final Instant coreInstant) {
		if (coreInstant == null) {
			return null;
		}
		
		return new org.hl7.fhir.r5.model.InstantType(coreInstant.getInstant());
	}
	
	public static org.hl7.fhir.r5.model.DateTimeType toFhirDateTime(final Date coreDateTime) {
		if (coreDateTime == null) {
			return null;
		}
		
		return new org.hl7.fhir.r5.model.DateTimeType(coreDateTime);
	}

	public static org.hl7.fhir.r5.model.CodeType toFhirCode(final Code code) {
		if (code == null) {
			return null;
		}
		
		return new org.hl7.fhir.r5.model.CodeType(code.getCodeValue());
	}

	public static org.hl7.fhir.r5.model.Coding toFhirCoding(final Coding coreCoding) {
		if (coreCoding == null) {
			return null;
		}
		
		final var fhirCoding = new org.hl7.fhir.r5.model.Coding();

		final String system = coreCoding.getSystemValue();
		if (!StringUtils.isEmpty(system)) {
			fhirCoding.setSystem(system);
		}

		final String version = coreCoding.getVersion();
		if (!StringUtils.isEmpty(version)) {
			fhirCoding.setVersion(version);
		}

		final String code = coreCoding.getCodeValue();
		if (!StringUtils.isEmpty(code)) {
			fhirCoding.setCode(code);
		}

		final String display = coreCoding.getDisplay();
		if (!StringUtils.isEmpty(display)) {
			fhirCoding.setDisplay(display);
		}
		
		if (!fhirCoding.isEmpty()) {
			return fhirCoding;
		} else {
			return null;
		}
	}

	public static org.hl7.fhir.r5.model.CodeableConcept toFhirCodeableConcept(final CodeableConcept coreCodeableConcept) {
		if (coreCodeableConcept == null) {
			return null;
		}
		
		final var fhirCodeableConcept = new org.hl7.fhir.r5.model.CodeableConcept();
		
		final Collection<Coding> codings = coreCodeableConcept.getCodings();
		if (codings != null) {
			for (final Coding coding : codings) {
				final var fhirCoding = toFhirCoding(coding);
				
				if (fhirCoding != null && !fhirCoding.isEmpty()) {
					fhirCodeableConcept.addCoding(fhirCoding);
				}
			}
		}
		
		if (!fhirCodeableConcept.isEmpty()) {
			return fhirCodeableConcept;
		} else {
			return null;
		}
	}
	
	public static org.hl7.fhir.r5.model.UriType toFhirUri(final Uri uri) {
		if (uri == null || StringUtils.isEmpty(uri.getUriValue())) {
			return null;
		}

		return new org.hl7.fhir.r5.model.UriType(uri.getUriValue());
	}

	public static org.hl7.fhir.r5.model.CanonicalType toFhirCanonicalUri(final Uri uri) {
		if (uri == null || StringUtils.isEmpty(uri.getUriValue())) {
			return null;
		}
	
		return new org.hl7.fhir.r5.model.CanonicalType(uri.getUriValue());
	}
	
	public static org.hl7.fhir.r5.model.CanonicalType toFhirCanonicalUri(final Uri uri, final String version) {
		if (uri == null || StringUtils.isEmpty(uri.getUriValue())) {
			return null;
		}
		
		if (StringUtils.isEmpty(version)) {
			return new org.hl7.fhir.r5.model.CanonicalType(uri.getUriValue());
		} else {
			return new org.hl7.fhir.r5.model.CanonicalType(uri.getUriValue() + "|" + version);
		}
	}

	public static org.hl7.fhir.r5.model.DataType toFhirDataType(final Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof final Boolean bool) {
			return new org.hl7.fhir.r5.model.BooleanType(bool);
		} else if (value instanceof final Integer integer) {
			return new org.hl7.fhir.r5.model.IntegerType(integer);
		} else if (value instanceof final BigDecimal decimal) {
			return new org.hl7.fhir.r5.model.DecimalType(decimal);
		} else if (value instanceof final String string) {
			return new org.hl7.fhir.r5.model.StringType(string);
		} else if (value instanceof final Date dateTime) {
			return toFhirDateTime(dateTime);
		} else if (value instanceof final Code code) {
			return toFhirCode(code);
		} else if (value instanceof final Coding coding) {
			return toFhirCoding(coding);
		} else if (value instanceof final CodeableConcept codeableConcept) {
			return toFhirCodeableConcept(codeableConcept);
		} else {
			// FIXME: throw an exception here, maybe?
			return null;
		}
	}

	public static List<org.hl7.fhir.r5.model.StringType> toFhirStringList(final Collection<String> values) {
		if (values == null) {
			return null;
		}

		return values.stream()
			.filter(s -> !StringUtils.isEmpty(s))
			.map(org.hl7.fhir.r5.model.StringType::new)
			.collect(Collectors.toList());
	}
}
