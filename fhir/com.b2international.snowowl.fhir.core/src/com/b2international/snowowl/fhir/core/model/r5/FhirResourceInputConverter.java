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

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.conceptmap.*;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.property.*;
import com.b2international.snowowl.fhir.core.model.valueset.*;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.*;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Parameter;

/**
 * @since 9.0
 */
public class FhirResourceInputConverter {

	public static CodeSystem toCodeSystem(final org.hl7.fhir.r5.model.CodeSystem fhirCodeSystem) {
		if (fhirCodeSystem == null) {
			return null;
		}

		final CodeSystem.Builder codeSystemBuilder = CodeSystem.builder();
		populateMetadataBuilder(codeSystemBuilder, fhirCodeSystem, codeSystemBuilder::addIdentifier, codeSystemBuilder::copyright);

		final var fhirContent = fhirCodeSystem.getContent();
		if (fhirContent != null) {
			codeSystemBuilder.content(CodeSystemContentMode.getByCodeValue(fhirContent.toCode()));
		}
		
		if (fhirCodeSystem.hasCount()) {
			codeSystemBuilder.count(fhirCodeSystem.getCount());
		}
		
		populateConcepts(codeSystemBuilder, fhirCodeSystem.getConcept());
		populateFilters(codeSystemBuilder, fhirCodeSystem.getFilter());
		populateProperties(codeSystemBuilder, fhirCodeSystem.getProperty());

		return codeSystemBuilder.build();
	}

	private static void populateConcepts(
		final CodeSystem.Builder codeSystemBuilder, 
		final List<org.hl7.fhir.r5.model.CodeSystem.ConceptDefinitionComponent> fhirConcepts
	) {
		for (final var fhirConcept : fhirConcepts) {
			final Concept.Builder conceptBuilder = Concept.builder();
			
			conceptBuilder.code(fhirConcept.getCode());
			conceptBuilder.display(fhirConcept.getDisplay());
			
			final var fhirDesignations = fhirConcept.getDesignation();
			for (final var fhirDesignation : fhirDesignations) {
				final Designation.Builder designationBuilder = Designation.builder();
				
				designationBuilder.language(fhirDesignation.getLanguage());
				designationBuilder.value(fhirDesignation.getValue());
				
				final var use = fhirDesignation.getUse();
				if (use != null) {
					final Coding coding = toCoding(use);
					designationBuilder.use(coding);
				}
			
				conceptBuilder.addDesignation(designationBuilder.build());
			}
			
			final var fhirProperties = fhirConcept.getProperty();
			for (final var fhirProperty : fhirProperties) {
				final ConceptProperty.Builder<?, ?, ?> conceptPropertyBuilder;
				
				if (fhirProperty.hasValueBooleanType()) {
					conceptPropertyBuilder = BooleanConceptProperty.builder().value(fhirProperty.getValueBooleanType().getValue());
				} else if (fhirProperty.hasValueCodeType()) {
					conceptPropertyBuilder = CodeConceptProperty.builder().value(toCode(fhirProperty.getValueCodeType()));
				} else if (fhirProperty.hasValueCoding()) {
					conceptPropertyBuilder = CodingConceptProperty.builder().value(toCoding(fhirProperty.getValueCoding()));
				} else if (fhirProperty.hasValueDateTimeType()) {
					conceptPropertyBuilder = DateTimeConceptProperty.builder().value(fhirProperty.getValueDateTimeType().getValue());
				} else if (fhirProperty.hasValueDecimalType()) {
					// FIXME: loss of precision when converting to Float!
					conceptPropertyBuilder = DecimalConceptProperty.builder().value(fhirProperty.getValueDecimalType().getValue().floatValue());
				} else if (fhirProperty.hasValueIntegerType()) {
					conceptPropertyBuilder = IntegerConceptProperty.builder().value(fhirProperty.getValueIntegerType().getValue());
				} else if (fhirProperty.hasValueStringType()) {
					conceptPropertyBuilder = StringConceptProperty.builder().value(fhirProperty.getValueStringType().getValue());
				} else {
					throw new IllegalStateException("Unexpected data type '" + fhirProperty.getValue().fhirType() + "' for concept property.");
				}
					
				conceptPropertyBuilder.code(fhirProperty.getCode());
				
				conceptBuilder.addProperty(conceptPropertyBuilder.build());
			}
			
			codeSystemBuilder.addConcept(conceptBuilder.build());
		}
	}

	private static void populateFilters(
		final CodeSystem.Builder codeSystemBuilder, 
		final List<org.hl7.fhir.r5.model.CodeSystem.CodeSystemFilterComponent> fhirFilters
	) {
		for (final var fhirFilter : fhirFilters) {
			final Filter.Builder filterBuilder = Filter.builder();
			
			filterBuilder.code(fhirFilter.getCode());
			
			final var fhirOperators = fhirFilter.getOperator();
			for (final var fhirOperator : fhirOperators) {
				filterBuilder.addOperator(FilterOperator.forValue(fhirOperator.getCode()));
			}
			
			filterBuilder.description(fhirFilter.getDescription());
			filterBuilder.value(fhirFilter.getValue());
			
			codeSystemBuilder.addFilter(filterBuilder.build());
		}
	}

	private static void populateProperties(
		final CodeSystem.Builder codeSystemBuilder, 
		final List<org.hl7.fhir.r5.model.CodeSystem.PropertyComponent> fhirProperties
	) {
		for (final var fhirProperty : fhirProperties) {
			final SupportedConceptProperty.Builder propertyBuilder = SupportedConceptProperty.builder();
			
			propertyBuilder.code(toCode(fhirProperty.getCodeElement()));
			
			final String uri = fhirProperty.getUri();
			if (!StringUtils.isEmpty(uri)) {
				propertyBuilder.uri(new Uri(uri));
			}
			
			propertyBuilder.description(fhirProperty.getDescription());
			
			final var type = fhirProperty.getType();
			if (type != null) {
				propertyBuilder.type(new Code(type.toCode()));
			}
			
			codeSystemBuilder.addProperty(propertyBuilder.build());
		}
	}

	public static ValueSet toValueSet(final org.hl7.fhir.r5.model.ValueSet fhirValueSet) {
		if (fhirValueSet == null) {
			return null;
		}

		final ValueSet.Builder valueSetBuilder = ValueSet.builder();
		populateMetadataBuilder(valueSetBuilder, fhirValueSet, valueSetBuilder::addIdentifier, valueSetBuilder::copyright);
		
		if (fhirValueSet.hasImmutableElement()) {
			valueSetBuilder.immutable(fhirValueSet.getImmutable());
		}
		
		populateCompose(valueSetBuilder, fhirValueSet.getCompose());
		populateExpansion(valueSetBuilder, fhirValueSet.getExpansion());
		
		return valueSetBuilder.build();
	}

	private static void populateCompose(
		final ValueSet.Builder valueSetBuilder, 
		final org.hl7.fhir.r5.model.ValueSet.ValueSetComposeComponent compose
	) {
		final Compose.Builder composeBuilder = Compose.builder();
		
		populateIncludes(composeBuilder::addInclude, compose.getInclude());
		populateIncludes(composeBuilder::addExclude, compose.getExclude());
		
		valueSetBuilder.compose(composeBuilder.build());
	}

	private static void populateIncludes(
		final Consumer<Include> includeConsumer, 
		final List<org.hl7.fhir.r5.model.ValueSet.ConceptSetComponent> fhirClauses
	) {
		for (final var fhirClause : fhirClauses) {
			final Include.Builder includeBuilder = Include.builder();
			
			includeBuilder.system(fhirClause.getSystem());
			includeBuilder.version(fhirClause.getVersion());
			
			final var fhirConcepts = fhirClause.getConcept();
			for (final var fhirConcept : fhirConcepts) {
				final ValueSetConcept.Builder conceptBuilder = ValueSetConcept.builder();
						
				conceptBuilder.code(fhirConcept.getCode());
				conceptBuilder.display(fhirConcept.getDisplay());
				
				final var fhirDesignations = fhirConcept.getDesignation();
				populateDesignations(conceptBuilder::addDesignation, fhirDesignations);
				
				includeBuilder.addConcept(conceptBuilder.build());
			}
			
			final var fhirFilters = fhirClause.getFilter();
			for (final var fhirFilter : fhirFilters) {
				final ValueSetFilter.Builder filterBuilder = ValueSetFilter.builder();
						
				filterBuilder.property(fhirFilter.getProperty());
				
				final org.hl7.fhir.r5.model.Enumerations.FilterOperator op = fhirFilter.getOp();
				if (op != null) {
					filterBuilder.operator(FilterOperator.forValue(op.toCode()));
				}
				
				filterBuilder.value(fhirFilter.getValue());
			
				includeBuilder.addFilters(filterBuilder.build());
			}
			
			// FIXME: We don't support inclusion of entire value sets yet, this is here just for the sake of completeness
			final var fhirValueSets = fhirClause.getValueSet();
			for (final var fhirValueSet : fhirValueSets) {
				includeBuilder.addValueSet(fhirValueSet.getValue());
			}
			
			includeConsumer.accept(includeBuilder.build());
		}
	}

	private static void populateExpansion(
		final ValueSet.Builder valueSetBuilder, 
		final org.hl7.fhir.r5.model.ValueSet.ValueSetExpansionComponent fhirExpansion
	) {
		final Expansion.Builder expansionBuilder = Expansion.builder();
		
		expansionBuilder.identifier(fhirExpansion.getIdentifier());

		// TODO: convert "next" URL back into a "searchAfter" expression
  		expansionBuilder.after(fhirExpansion.getNext());
  		
		expansionBuilder.timestamp(fhirExpansion.getTimestamp());
		expansionBuilder.offset(fhirExpansion.getOffset());
		expansionBuilder.total(fhirExpansion.getTotal());
		
		final var fhirParameters = fhirExpansion.getParameter();
		for (final var fhirParameter : fhirParameters) {
			final Parameter.Builder<?, ?, ?> parameterBuilder;
			
			if (fhirParameter.hasValueBooleanType()) {
				parameterBuilder = BooleanParameter.builder().value(fhirParameter.getValueBooleanType().getValue());
			} else if (fhirParameter.hasValueCodeType()) {
				parameterBuilder = CodeParameter.builder().value(toCode(fhirParameter.getValueCodeType()));
			} else if (fhirParameter.hasValueDateTimeType()) {
				parameterBuilder = DateTimeParameter.builder().value(fhirParameter.getValueDateTimeType().getValue());
			} else if (fhirParameter.hasValueDecimalType()) {
				// FIXME: loss of precision when converting to Double!
				parameterBuilder = DecimalParameter.builder().value(fhirParameter.getValueDecimalType().getValue().doubleValue());
			} else if (fhirParameter.hasValueIntegerType()) {
				parameterBuilder = IntegerParameter.builder().value(fhirParameter.getValueIntegerType().getValue());
			} else if (fhirParameter.hasValueStringType()) {
				parameterBuilder = StringParameter.builder().value(fhirParameter.getValueStringType().getValue());
			} else if (fhirParameter.hasValueUriType()) {
				parameterBuilder = UriParameter.builder().value(new Uri(fhirParameter.getValueUriType().getValue()));
			} else {
				throw new IllegalStateException("Unexpected data type '" + fhirParameter.getValue().fhirType() + "' for value set expansion property.");
			}
			
			parameterBuilder.name(fhirParameter.getName());

			expansionBuilder.addParameter(parameterBuilder.build());
		}
		
		// TODO: add support for "property" list
		
		populateContains(expansionBuilder::addContains, fhirExpansion.getContains());
		
		valueSetBuilder.expansion(expansionBuilder.build());
	}

	private static void populateContains(
		final Consumer<Contains> containsConsumer, 
		final List<org.hl7.fhir.r5.model.ValueSet.ValueSetExpansionContainsComponent> fhirContainsList
	) {
		for (final var fhirContains : fhirContainsList) {
			final Contains.Builder containsBuilder = Contains.builder();
			
			containsBuilder.system(fhirContains.getSystem());
			
			if (fhirContains.hasAbstractElement()) {
				containsBuilder.isAbstract(fhirContains.getAbstract());
			}
			
			if (fhirContains.hasInactiveElement()) {
				containsBuilder.inactive(fhirContains.getInactive());
			}
			
			containsBuilder.version(fhirContains.getVersion());
			
			containsBuilder.code(fhirContains.getCode());
			containsBuilder.display(fhirContains.getDisplay());
			
			final var fhirDesignations = fhirContains.getDesignation();
			populateDesignations(containsBuilder::addDesignation, fhirDesignations);
			
			// Recurse if the "contains" set is hierarchical
			final var nestedFhirContainsList = fhirContains.getContains();
			populateContains(containsBuilder::addContains, nestedFhirContainsList);
			
			containsConsumer.accept(containsBuilder.build());
		}
	}

	private static void populateDesignations(
		final Consumer<Designation> designationConsumer, 
		final List<org.hl7.fhir.r5.model.ValueSet.ConceptReferenceDesignationComponent> fhirDesignations
	) {
		for (final var fhirDesignation : fhirDesignations) {
			final Designation.Builder designationBuilder = Designation.builder();
			
			designationBuilder.language(fhirDesignation.getLanguage());
			designationBuilder.value(fhirDesignation.getValue());
			
			final var use = fhirDesignation.getUse();
			if (use != null) {
				designationBuilder.use(toCoding(use));
			}
		
			designationConsumer.accept(designationBuilder.build());
		}
	}

	public static ConceptMap toConceptMap(final org.hl7.fhir.r5.model.ConceptMap fhirConceptMap) {
		if (fhirConceptMap == null) {
			return null;
		}

		final ConceptMap.Builder conceptMapBuilder = ConceptMap.builder();
		populateMetadataBuilder(conceptMapBuilder, fhirConceptMap, conceptMapBuilder::addIdentifier, conceptMapBuilder::copyright);
		
		if (fhirConceptMap.hasSourceScopeCanonicalType()) {
			final var sourceCanonical = fhirConceptMap.getSourceScopeCanonicalType();
			conceptMapBuilder.sourceCanonical(sourceCanonical.getValue());
		} else if (fhirConceptMap.hasSourceScopeUriType()) {
			final var sourceUri = fhirConceptMap.getSourceScopeUriType();
			conceptMapBuilder.sourceUri(sourceUri.getValue());
		}
		
		if (fhirConceptMap.hasTargetScopeCanonicalType()) {
			final var targetCanonical = fhirConceptMap.getTargetScopeCanonicalType();
			conceptMapBuilder.targetCanonical(targetCanonical.getValue());
		} else if (fhirConceptMap.hasTargetScopeUriType()) {
			final var targetUri = fhirConceptMap.getTargetScopeUriType();
			conceptMapBuilder.targetUri(targetUri.getValue());
		}
				
		final var fhirGroups = fhirConceptMap.getGroup();
		for (final var fhirGroup : fhirGroups) {
			final Group.Builder groupBuilder = Group.builder();
			
			// Take version suffix after the last "|" separator.
			if (fhirGroup.hasSourceElement()) {
				final String source = fhirGroup.getSource();
				final int sourceSeparatorIdx = source.lastIndexOf('|');
				if (sourceSeparatorIdx > 0) {
					groupBuilder.source(source.substring(0, sourceSeparatorIdx));
					groupBuilder.sourceVersion(source.substring(sourceSeparatorIdx + 1, source.length()));
				} else {
					groupBuilder.source(source);
				}
			}
			
			if (fhirGroup.hasTargetElement()) {
				final String target = fhirGroup.getTarget();
				final int targetSeparatorIdx = target.lastIndexOf('|');
				if (targetSeparatorIdx > 0) {
					groupBuilder.target(target.substring(0, targetSeparatorIdx));
					groupBuilder.targetVersion(target.substring(targetSeparatorIdx + 1, target.length()));
				} else {
					groupBuilder.target(target);
				}
			}
			
			final var fhirElements = fhirGroup.getElement();
			for (final var fhirElement : fhirElements) {
				final ConceptMapElement.Builder elementBuilder = ConceptMapElement.builder();		
				
				elementBuilder.code(fhirElement.getCode());
				elementBuilder.display(fhirElement.getDisplay());
				
				final var fhirTargets = fhirElement.getTarget();
				for (final var fhirTarget : fhirTargets) {
					final Target.Builder targetBuilder = Target.builder();
					
					targetBuilder.code(fhirTarget.getCode());
					targetBuilder.display(fhirTarget.getDisplay());
					targetBuilder.comment(fhirTarget.getComment());
					
					if (fhirTarget.hasRelationshipElement()) {
						targetBuilder.equivalence(fhirTarget.getRelationship().toCode());
					}
					
					// TODO: add "dependsOn" and "product" support which is on our model but we don't set it anywhere
					
					elementBuilder.addTarget(targetBuilder.build());
				}
				
				groupBuilder.addElement(elementBuilder.build());
			}
			
			if (fhirGroup.hasUnmapped()) {
				final var fhirUnmapped = fhirGroup.getUnmapped();
				final UnMapped.Builder unmappedBuilder = UnMapped.builder();
				
				unmappedBuilder.mode(fhirUnmapped.getMode().toCode());
				unmappedBuilder.code(fhirUnmapped.getCode());
				unmappedBuilder.display(fhirUnmapped.getDisplay());
				
				// FIXME: Is this what this property was originally intended for? In R5 this is now called "otherMap"
				if (fhirUnmapped.hasOtherMap()) {
					unmappedBuilder.url(fhirUnmapped.getOtherMap());
				}

				groupBuilder.unmapped(unmappedBuilder.build());
			}
			
			conceptMapBuilder.addGroup(groupBuilder.build());
		}
		
		return conceptMapBuilder.build();
	}
	
	private static <T extends MetadataResource.Builder<T, ?>> T populateMetadataBuilder(
		final T builder,
		final org.hl7.fhir.r5.model.MetadataResource fhirResource,
		final Consumer<Identifier> identifierConsumer,
		final Consumer<String> copyrightConsumer
	) {
		// TODO: convert the FHIR-friendly identifier back into a Snow Owl compatible variant for versioned resources, on the request level!
		final String id = fhirResource.getId();
		if (!StringUtils.isEmpty(id)) {
			builder.id(id);
		}
		
		// builder.toolingId(...) can not be set externally
		
		builder.name(fhirResource.getName());
	
		final Date fhirLastUpdated = fhirResource.getMeta()
			.getLastUpdated();
		
		if (fhirLastUpdated != null) {
			final Instant coreLastUpdated = Instant.builder()
				.instant(fhirLastUpdated)
				.build();
			
			builder.meta(Meta.builder()
				.lastUpdated(coreLastUpdated)
				.build());
		}

		final var status = fhirResource.getStatus();
		if (status != null) {
			builder.status(new Code(status.toCode()));
		}
	
		builder.title(fhirResource.getTitle());
	
		final String url = fhirResource.getUrl();
		if (!StringUtils.isEmpty(url)) {
			builder.url(url);
		}
	
		final var text = fhirResource.getText();
		if (text != null) {
			// "div" should not be empty on a Narrative
			final Narrative.Builder coreText = Narrative.builder();
			coreText.div(text.getDivAsString());
	
			final var textStatus = text.getStatus();
			if (textStatus != null) {
				coreText.status(NarrativeStatus.forValue(textStatus.toCode()));
			}
			
			builder.text(coreText.build());
		}
	
		builder.version(fhirResource.getVersion());
		builder.publisher(fhirResource.getPublisher());
	
		final String language = fhirResource.getLanguage();
		if (!StringUtils.isEmpty(language)) {
			builder.language(language);
		}
	
		builder.date(fhirResource.getDate());
		builder.description(fhirResource.getDescription());
		builder.purpose(fhirResource.getPurpose());
	
		final var fhirContacts = fhirResource.getContact();
		for (final var fhirContact : fhirContacts) {
			final ContactDetail.Builder contact = ContactDetail.builder();
			
			final var fhirTelecoms = fhirContact.getTelecom();
			for (final var fhirTelecom : fhirTelecoms) {
				final ContactPoint.Builder contactPoint = ContactPoint.builder();

				final var system = fhirTelecom.getSystem();
				if (system != null) {
					contactPoint.system(system.toCode());
				}

				contactPoint.value(fhirTelecom.getValue());

				contact.addTelecom(contactPoint.build());
			}
			
			builder.addContact(contact.build());
		}
		
		final var fhirIdentifiers = fhirResource.getIdentifier();
		for (final var fhirIdentifier : fhirIdentifiers) {
			final Identifier.Builder identifier = Identifier.builder();

			final String system = fhirIdentifier.getSystem();
			if (!StringUtils.isEmpty(system)) {
				identifier.system(system);
			}
			
			final var use = fhirIdentifier.getUse();
			if (use != null) {
				identifier.use(IdentifierUse.forValue(use.toCode()));
			}

			identifier.value(fhirIdentifier.getValue());

			identifierConsumer.accept(identifier.build());
		}
		
		copyrightConsumer.accept(fhirResource.getCopyright());
	
		return builder;
	}

	private static Coding toCoding(final org.hl7.fhir.r5.model.Coding fhirCoding) {
		final Coding.Builder builder = Coding.builder();
		builder.system(fhirCoding.getSystem());
		builder.version(fhirCoding.getVersion());
		builder.display(fhirCoding.getDisplay());
		builder.code(fhirCoding.getCode());
		return builder.build();
	}

	private static Code toCode(final org.hl7.fhir.r5.model.CodeType fhirCodeType) {
		return new Code(fhirCodeType.getCode());
	}
}
