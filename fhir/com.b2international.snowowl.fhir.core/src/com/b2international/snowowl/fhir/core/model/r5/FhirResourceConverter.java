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

import static com.b2international.snowowl.fhir.core.model.r5.FhirDataTypeConverter.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.conceptmap.*;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty;
import com.b2international.snowowl.fhir.core.model.valueset.*;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Parameter;

/**
 * @since 9.0
 */
public class FhirResourceConverter {

	public static org.hl7.fhir.r5.model.Resource toFhirResource(final FhirResource coreResource) {
		if (coreResource == null) {
			return null;
		}
		
		if (coreResource instanceof final CodeSystem coreCodeSystem) {
			return toFhirCodeSystem(coreCodeSystem);
		} else if (coreResource instanceof final ValueSet coreValueSet) {
			return toFhirValueSet(coreValueSet);
		} else if (coreResource instanceof final ConceptMap coreConceptMap) {
			return toFhirConceptMap(coreConceptMap);
		} else {
			return null;
		}
	}

	public static org.hl7.fhir.r5.model.CodeSystem toFhirCodeSystem(final CodeSystem coreCodeSystem) {
		if (coreCodeSystem == null) {
			return null;
		}

		final var fhirCodeSystem = new org.hl7.fhir.r5.model.CodeSystem();
		populateFhirMetadataResource(fhirCodeSystem, coreCodeSystem, coreCodeSystem.getIdentifiers(), coreCodeSystem.getCopyright());

		final Code content = coreCodeSystem.getContent();
		if (content != null) {
			fhirCodeSystem.setContent(toFhirCodeSystemContentMode(content));
		}
		
		fhirCodeSystem.setCount(coreCodeSystem.getCount());
		
		populateFhirConcepts(fhirCodeSystem, coreCodeSystem.getConcepts());
		populateFhirFilters(fhirCodeSystem, coreCodeSystem.getFilters());
		populateFhirProperties(fhirCodeSystem, coreCodeSystem.getProperties());

		return fhirCodeSystem;
	}

	private static void populateFhirConcepts(final org.hl7.fhir.r5.model.CodeSystem fhirCodeSystem, final Collection<Concept> coreConcepts) {
		if (coreConcepts == null) {
			return;
		}
		
		for (final Concept coreConcept : coreConcepts) {
			final var fhirConcept = new org.hl7.fhir.r5.model.CodeSystem.ConceptDefinitionComponent();
			
			final Code code = coreConcept.getCode();
			fhirConcept.setCodeElement(toFhirCode(code));
			fhirConcept.setDisplay(coreConcept.getDisplay());
			
			final Collection<Designation> coreDesignations = coreConcept.getDesignations();
			if (coreDesignations != null) {
				for (final Designation coreDesignation : coreDesignations) {
					final var fhirDesignation = new org.hl7.fhir.r5.model.CodeSystem.ConceptDefinitionDesignationComponent();
					
					fhirDesignation.setLanguage(coreDesignation.getLanguage());
					fhirDesignation.setValue(coreDesignation.getValue());
					final Coding use = coreDesignation.getUse();
					fhirDesignation.setUse(toFhirCoding(use));
				
					if (!fhirDesignation.isEmpty()) {
						fhirConcept.addDesignation(fhirDesignation);
					}
				}
			}
			
			@SuppressWarnings("rawtypes")
			final Collection<ConceptProperty> coreProperties = coreConcept.getProperties();
			if (coreProperties != null) {
				for (final ConceptProperty<?> coreProperty : coreProperties) {
					if (coreProperty != null) {
						final var convertedProperty = new org.hl7.fhir.r5.model.CodeSystem.ConceptPropertyComponent();
						
						final Code propertyCode = coreProperty.getCode();
						convertedProperty.setCodeElement(toFhirCode(propertyCode));
						
						final Object value = coreProperty.getValue();
						convertedProperty.setValue(toFhirDataType(value));
						
						if (!convertedProperty.isEmpty()) {
							fhirConcept.addProperty(convertedProperty);
						}
					}
				}
			}
			
			if (!fhirConcept.isEmpty()) {
				fhirCodeSystem.addConcept(fhirConcept);
			}
		}
	}

	private static void populateFhirFilters(final org.hl7.fhir.r5.model.CodeSystem fhirCodeSystem, final Collection<Filter> coreFilters) {
		if (coreFilters == null) {
			return;
		}
		
		for (final Filter coreFilter : coreFilters) {
			final var fhirFilter = new org.hl7.fhir.r5.model.CodeSystem.CodeSystemFilterComponent();
			
			final Code code = coreFilter.getCode();
			fhirFilter.setCodeElement(toFhirCode(code));
			
			final Collection<Code> operators = coreFilter.getOperators();
			if (operators != null) {
				for (final Code operator : operators) {
					fhirFilter.addOperator(toFhirFilterOperator(operator));
				}
			}
			
			fhirFilter.setDescription(coreFilter.getDescription());
			fhirFilter.setValue(coreFilter.getValue());
			
			if (!fhirFilter.isEmpty()) {
				fhirCodeSystem.addFilter(fhirFilter);
			}
		}
	}

	private static void populateFhirProperties(final org.hl7.fhir.r5.model.CodeSystem fhirCodeSystem, final Collection<SupportedConceptProperty> coreProperties) {
		if (coreProperties == null) {
			return;
		}
		
		for (final SupportedConceptProperty coreProperty : coreProperties) {
			final var fhirProperty = new org.hl7.fhir.r5.model.CodeSystem.PropertyComponent();
			
			final Code code = coreProperty.getCode();
			fhirProperty.setCodeElement(toFhirCode(code));
			
			final Uri uri = coreProperty.getUri();
			fhirProperty.setUriElement(toFhirUri(uri));
			
			fhirProperty.setDescription(coreProperty.getDescription());
			
			final Code type = coreProperty.getType();
			if (type != null) {
				fhirProperty.setType(toFhirPropertyType(type));
			}
			
			if (!fhirProperty.isEmpty()) {
				fhirCodeSystem.addProperty(fhirProperty);
			}
		}
	}

	public static org.hl7.fhir.r5.model.ValueSet toFhirValueSet(final ValueSet coreValueSet) {
		if (coreValueSet == null) {
			return null;
		}

		final var fhirValueSet = new org.hl7.fhir.r5.model.ValueSet();
		populateFhirMetadataResource(fhirValueSet, coreValueSet, coreValueSet.getIdentifiers(), coreValueSet.getCopyright());
		
		final Boolean immutable = coreValueSet.getImmutable();
		if (immutable != null) {
			fhirValueSet.setImmutable(immutable);
		}
		
		populateFhirCompose(fhirValueSet, coreValueSet.getCompose());
		populateFhirExpansion(fhirValueSet, coreValueSet.getExpansion());
		
		return fhirValueSet;
	}

	private static void populateFhirCompose(final org.hl7.fhir.r5.model.ValueSet fhirValueSet, final Compose coreCompose) {
		if (coreCompose == null) {
			return;
		}
		
		final var fhirCompose = new org.hl7.fhir.r5.model.ValueSet.ValueSetComposeComponent();
		
		populateFhirIncludes(fhirCompose::addInclude, coreCompose.getIncludes());
		populateFhirIncludes(fhirCompose::addExclude, coreCompose.getExcludes());
		
		if (!fhirCompose.isEmpty()) {
			fhirValueSet.setCompose(fhirCompose);
		}
	}

	private static void populateFhirIncludes(final Consumer<org.hl7.fhir.r5.model.ValueSet.ConceptSetComponent> fhirClauseConsumer, final Collection<Include> coreClauses) {
		if (coreClauses == null) {
			return;
		}
		
		for (final Include coreClause : coreClauses) {
			final var fhirClause = new org.hl7.fhir.r5.model.ValueSet.ConceptSetComponent();
			
			final Uri system = coreClause.getSystem();
			fhirClause.setSystemElement(toFhirUri(system));
			
			fhirClause.setVersion(coreClause.getVersion());
			
			final Collection<ValueSetConcept> coreConcepts = coreClause.getConcepts();
			if (coreConcepts != null) {
				for (final ValueSetConcept coreConcept : coreConcepts) {
					final var fhirConcept = new org.hl7.fhir.r5.model.ValueSet.ConceptReferenceComponent();
							
					final Code code = coreConcept.getCode();
					fhirConcept.setCodeElement(toFhirCode(code));
					
					fhirConcept.setDisplay(coreConcept.getDisplay());
					
					final Collection<Designation> coreDesignations = coreConcept.getDesignations();
					populateDesignations(fhirConcept::addDesignation, coreDesignations);
					
					if (!fhirConcept.isEmpty()) {
						fhirClause.addConcept(fhirConcept);
					}
				}
			}
			
			final Collection<ValueSetFilter> coreFilters = coreClause.getFilters();
			if (coreFilters != null) {
				for (final ValueSetFilter coreFilter : coreFilters) {
					final var fhirFilter = new org.hl7.fhir.r5.model.ValueSet.ConceptSetFilterComponent();
							
					final Code property = coreFilter.getProperty();
					fhirFilter.setPropertyElement(toFhirCode(property));
					
					final Code op = coreFilter.getOperator();
					if (op != null) {
						fhirFilter.setOp(toFhirFilterOperator(op));
					}
					
					fhirFilter.setValue(coreFilter.getValue());
					
					if (!fhirFilter.isEmpty()) {
						fhirClause.addFilter(fhirFilter);
					}
				}
			}
			
			// FIXME: We don't support inclusion of entire value sets yet, this is here just for the sake of completeness
			final Collection<Uri> coreValueSets = coreClause.getValueSets();
			if (coreValueSets != null) {
				for (final Uri coreValueSet : coreValueSets) {
					fhirClause.addValueSet(coreValueSet.getUriValue());
				}
			}
			
			if (!fhirClause.isEmpty()) {
				fhirClauseConsumer.accept(fhirClause);
			}
		}
	}

	private static void populateFhirExpansion(final org.hl7.fhir.r5.model.ValueSet fhirValueSet, final Expansion coreExpansion) {
		if (coreExpansion == null) {
			return;
		}
		
		final var fhirExpansion = new org.hl7.fhir.r5.model.ValueSet.ValueSetExpansionComponent();
		
		final Uri identifier = coreExpansion.getIdentifier();
		fhirExpansion.setIdentifierElement(toFhirUri(identifier));

		// TODO: convert "searchAfter" expression to complete "next" URLs that clients can call to get the next page of expansions
		final String after = coreExpansion.getAfter();
  		fhirExpansion.setNext(after);
  		
  		final Date timestamp = coreExpansion.getTimestamp();
		fhirExpansion.setTimestampElement(toFhirDateTime(timestamp));
		
		fhirExpansion.setOffset(coreExpansion.getOffset());
		fhirExpansion.setTotal(coreExpansion.getTotal());
		
		final var coreParameters = coreExpansion.getParameters();
		if (coreParameters != null) {
			for (@SuppressWarnings("rawtypes") Parameter coreParameter : coreParameters) {
				final var fhirParameter = new org.hl7.fhir.r5.model.ValueSet.ValueSetExpansionParameterComponent();
				
				fhirParameter.setName(coreParameter.getName());
				fhirParameter.setValue(toFhirDataType(coreParameter.getValue()));
				
				if (!fhirParameter.isEmpty()) {
					fhirExpansion.addParameter(fhirParameter);
				}
			}
		}
		
		// TODO: add support for "property" list
		
		final Collection<Contains> coreContainsCollection = coreExpansion.getContains();
		populateContains(fhirExpansion::addContains, coreContainsCollection);
		
		if (!fhirExpansion.isEmpty()) {
			fhirValueSet.setExpansion(fhirExpansion);
		}
	}

	private static void populateDesignations(
		final Consumer<org.hl7.fhir.r5.model.ValueSet.ConceptReferenceDesignationComponent> fhirDesignationConsumer, 
		final Collection<Designation> coreDesignations
	) {
		if (coreDesignations == null) {
			return;
		}
			
		for (final Designation coreDesignation : coreDesignations) {
			final var fhirDesignation = new org.hl7.fhir.r5.model.ValueSet.ConceptReferenceDesignationComponent();
			
			fhirDesignation.setLanguage(coreDesignation.getLanguage());
			fhirDesignation.setValue(coreDesignation.getValue());
			final Coding use = coreDesignation.getUse();
			fhirDesignation.setUse(toFhirCoding(use));
		
			if (!fhirDesignation.isEmpty()) {
				fhirDesignationConsumer.accept(fhirDesignation);
			}
		}
	}

	private static void populateContains(
		final Consumer<org.hl7.fhir.r5.model.ValueSet.ValueSetExpansionContainsComponent> fhirContainsConsumer, 
		final Collection<Contains> coreContainsCollection
	) {
		if (coreContainsCollection == null) {
			return;
		}
		
		for (final Contains coreContains : coreContainsCollection) {
			final var fhirContains = new org.hl7.fhir.r5.model.ValueSet.ValueSetExpansionContainsComponent();
			
			final Uri system = coreContains.getSystem();
			fhirContains.setSystemElement(toFhirUri(system));
			
			final Boolean isAbstract = coreContains.getIsAbstract();
			if (isAbstract != null) {
				fhirContains.setAbstract(isAbstract);
			}
			
			final Boolean inactive = coreContains.getInactive();
			if (inactive != null) {
				fhirContains.setInactive(inactive);
			}
			
			fhirContains.setVersion(coreContains.getVersion());
			
			final Code code = coreContains.getCode();
			fhirContains.setCodeElement(toFhirCode(code));
			
			fhirContains.setDisplay(coreContains.getDisplay());
			
			final Collection<Designation> coreDesignations = coreContains.getDesignations();
			populateDesignations(fhirContains::addDesignation, coreDesignations);
			
			// Recurse if the "contains" set is hierarchical
			final Collection<Contains> nestedCoreContainsCollection = coreContains.getContains();
			populateContains(fhirContains::addContains, nestedCoreContainsCollection);
			
			if (!fhirContains.isEmpty()) {
				fhirContainsConsumer.accept(fhirContains);
			}				
		}
	}

	public static org.hl7.fhir.r5.model.ConceptMap toFhirConceptMap(final ConceptMap coreConceptMap) {
		if (coreConceptMap == null) {
			return null;
		}

		final var fhirConceptMap = new org.hl7.fhir.r5.model.ConceptMap();
		populateFhirMetadataResource(fhirConceptMap, coreConceptMap, coreConceptMap.getIdentifiers(), coreConceptMap.getCopyright());
		
		final Uri sourceUri = coreConceptMap.getSourceUri();
		fhirConceptMap.setSourceScope(toFhirUri(sourceUri));
		
		// Don't overwrite in cases where only a non-canonical URI exists
		final Uri sourceCanonical = coreConceptMap.getSourceCanonical();
		if (sourceCanonical != null) {
			fhirConceptMap.setSourceScope(toFhirCanonicalUri(sourceCanonical));
		}
		
		final Uri targetUri = coreConceptMap.getSourceUri();
		fhirConceptMap.setSourceScope(toFhirUri(targetUri));
		
		// Don't overwrite in cases where only a non-canonical URI exists
		final Uri targetCanonical = coreConceptMap.getSourceCanonical();
		if (targetCanonical != null) {
			fhirConceptMap.setSourceScope(toFhirCanonicalUri(targetCanonical));
		}
		
		final Collection<Group> coreGroups = coreConceptMap.getGroups();
		if (coreGroups != null) {
			for (final Group coreGroup : coreGroups) {
				final var fhirGroup = new org.hl7.fhir.r5.model.ConceptMap.ConceptMapGroupComponent();
				
				/*
				 * Create a canonical URI that concatenates the version part with a "|"
				 * separator. This means that we'll have to support versioned reads for
				 * versioned resources :(
				 */
				final Uri source = coreGroup.getSource();
				final String sourceVersion = coreGroup.getSourceVersion();
				fhirGroup.setSourceElement(toFhirCanonicalUri(source, sourceVersion));
				
				final Uri target = coreGroup.getSource();
				final String targetVersion = coreGroup.getSourceVersion();
				fhirGroup.setSourceElement(toFhirCanonicalUri(target, targetVersion));
				
				final List<ConceptMapElement> coreElements = coreGroup.getElements();
				if (coreElements != null) {
					for (final ConceptMapElement coreElement : coreElements) {
						final var fhirElement = new org.hl7.fhir.r5.model.ConceptMap.SourceElementComponent();		
						
						final Code code = coreElement.getCode();
						fhirElement.setCodeElement(toFhirCode(code));
						
						fhirElement.setDisplay(coreElement.getDisplay());
						
						final Collection<Target> coreTargets = coreElement.getTargets();
						if (coreTargets != null) {
							for (final Target coreTarget : coreTargets) {
								final var fhirTarget = new org.hl7.fhir.r5.model.ConceptMap.TargetElementComponent();
								
								final Code targetCode = coreTarget.getCode();
								fhirTarget.setCodeElement(toFhirCode(targetCode));
								
								fhirTarget.setDisplay(coreTarget.getDisplay());
								fhirTarget.setComment(coreTarget.getComment());
								
								final Code relationship = coreTarget.getEquivalence();
								if (relationship != null) {
									fhirTarget.setRelationship(toFhirRelationship(relationship));
								}
								
								// TODO: add "dependsOn" and "product" support which is on our model but we don't set it anywhere
								
								if (!fhirTarget.isEmpty()) {
									fhirElement.addTarget(fhirTarget);
								}
							}
						}
						
						if (!fhirElement.isEmpty()) {
							fhirGroup.addElement(fhirElement);
						}
					}
				}
				
				final UnMapped coreUnmapped = coreGroup.getUnmapped();
				if (coreUnmapped != null) {
					final var fhirUnmapped = new org.hl7.fhir.r5.model.ConceptMap.ConceptMapGroupUnmappedComponent();
					
					final Code mode = coreUnmapped.getMode();
					if (mode != null) {
						fhirUnmapped.setMode(toFhirUnmappedMode(mode));
					}
					
					final Code code = coreUnmapped.getCode();
					fhirUnmapped.setCodeElement(toFhirCode(code));
					
					fhirUnmapped.setDisplay(coreUnmapped.getDisplay());
					
					// FIXME: Is this what this property was originally intended for? In R5 this is now called "otherMap"
					final Uri otherMap = coreUnmapped.getUrl();
					fhirUnmapped.setOtherMapElement(toFhirCanonicalUri(otherMap));
					
					if (!fhirUnmapped.isEmpty()) {
						fhirGroup.setUnmapped(fhirUnmapped);
					}
				}
				
				if (!fhirGroup.isEmpty()) {
					fhirConceptMap.addGroup(fhirGroup);
				}
			}
		}
		
		return fhirConceptMap;
	}
	
	private static <T extends org.hl7.fhir.r5.model.MetadataResource> T populateFhirMetadataResource(
		final T fhirResource,
		final MetadataResource coreResource,
		final Collection<Identifier> coreIdentifiers,
		final String copyright
	) {
		// TODO: make the identifier FHIR-friendly for versioned resources, on the request level!
		final Id id = coreResource.getId();
		if (id != null && !StringUtils.isEmpty(id.getIdValue())) {
			fhirResource.setId(id.getIdValue());
		}
		
		// The resource name can keep its '/' separator for versioned resources however
		fhirResource.setName(coreResource.getName());
	
		final Meta meta = coreResource.getMeta();
		if (meta != null) {
			final Instant lastUpdated = meta.getLastUpdated();
			fhirResource.getMeta().setLastUpdatedElement(toFhirInstant(lastUpdated));
		}
	
		final Code status = coreResource.getStatus();
		if (status != null) {
			fhirResource.setStatus(toFhirPublicationStatus(status));
		}
	
		fhirResource.setTitle(coreResource.getTitle());
	
		final Uri url = coreResource.getUrl();
		fhirResource.setUrlElement(toFhirUri(url));
	
		final Narrative text = coreResource.getText();
		if (text != null) {
			// "div" should not be empty on a Narrative
			final var convertedText = fhirResource.getText();
			convertedText.setDivAsString(text.getDiv());
	
			final Code textStatus = text.getStatus();
			if (textStatus != null) {
				convertedText.setStatus(toFhirNarrativeStatus(textStatus));
			}
		}
	
		fhirResource.setVersion(coreResource.getVersion());
		fhirResource.setPublisher(coreResource.getPublisher());
	
		final Code language = coreResource.getLanguage();
		if (language != null) {
			fhirResource.setLanguage(language.getCodeValue());
		}
	
		fhirResource.setDate(coreResource.getDate());
		fhirResource.setDescription(coreResource.getDescription());
		fhirResource.setPurpose(coreResource.getPurpose());
	
		final Collection<ContactDetail> coreContacts = coreResource.getContacts();
		if (coreContacts != null) {
			for (final ContactDetail coreContact : coreContacts) {
				if (coreContact != null) {
					final var fhirContact = new org.hl7.fhir.r5.model.ContactDetail();
					
					final Collection<ContactPoint> coreTelecoms = coreContact.getTelecoms();
					if (coreTelecoms != null) {
						for (final ContactPoint coreTelecom : coreTelecoms) {
							if (coreTelecom != null) {
								final var fhirTelecom = new org.hl7.fhir.r5.model.ContactPoint();
	
								final Code system = coreTelecom.getSystem();
								if (system != null) {
									fhirTelecom.setSystem(toFhirContactPointSystem(system));
								}
	
								fhirTelecom.setValue(coreTelecom.getValue());
	
								if (!fhirTelecom.isEmpty()) {
									fhirContact.addTelecom(fhirTelecom);
								}
							}
						}
					}
					
					if (!fhirContact.isEmpty()) {
						fhirResource.addContact(fhirContact);
					}
				}
			}
		}
		
		if (coreIdentifiers != null) {
			for (final Identifier coreIdentifier : coreIdentifiers) {
				if (coreIdentifier != null) {
					final var fhirIdentifier = new org.hl7.fhir.r5.model.Identifier();

					final Uri system = coreIdentifier.getSystem();
					fhirIdentifier.setSystemElement(toFhirUri(system));

					final Code use = coreIdentifier.getUse();
					if (use != null) {
						fhirIdentifier.setUse(toFhirIdentifierUse(use));
					}

					fhirIdentifier.setValue(coreIdentifier.getValue());

					if (!fhirIdentifier.isEmpty()) {
						fhirResource.addIdentifier(fhirIdentifier);
					}
				}
			}
		}
		
		fhirResource.setCopyright(copyright);
	
		return fhirResource;
	}

	public static org.hl7.fhir.r5.model.Resource toFhirOperationOutcome(final OperationOutcome coreOperationOutcome) {
		if (coreOperationOutcome == null) {
			return null;
		}
		
		final var fhirOperationOutcome = new org.hl7.fhir.r5.model.OperationOutcome();
		
		final Collection<Issue> coreIssues = coreOperationOutcome.getIssues();
		if (coreIssues != null) {
			for (final Issue coreIssue : coreIssues) {
				final var fhirIssue = new org.hl7.fhir.r5.model.OperationOutcome.OperationOutcomeIssueComponent();
				
				final Code severity = coreIssue.getSeverity();
				if (severity != null) {
					fhirIssue.setSeverity(toFhirIssueSeverity(severity));
				}
				
				final Code code = coreIssue.getCode();
				if (code != null) {
					fhirIssue.setCode(toFhirIssueType(code));
				}
				
				final CodeableConcept codeableConcept = coreIssue.getDetails();
				fhirIssue.setDetails(toFhirCodeableConcept(codeableConcept));

				final String diagnostics = coreIssue.getDiagnostics();
				fhirIssue.setDiagnostics(diagnostics);
				
				final Collection<String> locations = coreIssue.getLocations();
				fhirIssue.setLocation(toFhirStringList(locations));
				
				final Collection<String> expressions = coreIssue.getExpressions();
				fhirIssue.setExpression(toFhirStringList(expressions));
				
				if (!fhirIssue.isEmpty()) {
					fhirOperationOutcome.addIssue(fhirIssue);
				}
			}
		}
		
		return fhirOperationOutcome;
	}
}
