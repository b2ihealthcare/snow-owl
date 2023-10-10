/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.parser;

import static org.linuxforhealth.fhir.model.r5.util.JsonSupport.*;
import static org.linuxforhealth.fhir.model.r5.util.ModelSupport.getChoiceElementName;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Stack;
import java.util.StringJoiner;

import javax.annotation.Generated;

//import org.eclipse.parsson.api.JsonConfig;
import org.linuxforhealth.fhir.model.parser.exception.FHIRParserException;
import org.linuxforhealth.fhir.model.r5.resource.*;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.code.*;
import org.linuxforhealth.fhir.model.r5.util.ElementFilter;
//import net.jcip.annotations.NotThreadSafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

/*
 * Modifications:
 * 
 * - Use Guava and Jackson for low-level JSON node manipulation
 * - Disable @NotThreadSafe annotation
 * - Remove support for resource types unrelated to terminology services
 */

//@NotThreadSafe
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class FHIRJsonParser extends FHIRAbstractParser {
    public static boolean DEBUG = false;

    private final Stack<java.lang.String> stack = new Stack<>();

    FHIRJsonParser() {
        // only visible to subclasses or classes/interfaces in the same package (e.g. FHIRParser)
    }

    @Override
    public <T extends Resource> T parse(InputStream in) throws FHIRParserException {
        return parseAndFilter(in, null);
    }

    /**
     * Read a resource from the passed InputStream and filter its top-level elements to the collection of elementsToInclude.
     * This method does not close the passed InputStream.
     * 
     * @param <T>
     *     The resource type to read
     * @param in
     *     An input stream with the JSON contents of a FHIR resource
     * @param elementsToInclude
     *     The top-level elements to include or null to indicate that no filter should be applied
     * @return
     * @throws FHIRParserException
     *     if the resource could not be parsed for any reason
     */
    public <T extends Resource> T parseAndFilter(InputStream in, Collection<java.lang.String> elementsToInclude) throws FHIRParserException {
        try {
        	JsonNode jsonNode = JSON_OBJECT_MAPPER.readTree(nonClosingInputStream(in));
            return parseAndFilter(jsonNode, elementsToInclude);
        } catch (FHIRParserException e) {
            throw e;
        } catch (Exception e) {
            throw new FHIRParserException(e.getMessage(), getPath(), e);
        }
    }

    @Override
    public <T extends Resource> T parse(Reader reader) throws FHIRParserException {
        return parseAndFilter(reader, null);
    }

    /**
     * Read a resource using the passed Reader and filter its top-level elements to the collection of elementsToInclude.
     * This method does not close the passed InputStream.
     * 
     * @param <T>
     *     The resource type to read
     * @param reader
     *     A reader with the JSON contents of a FHIR resource
     * @param elementsToInclude
     *     The top-level elements to include or null to indicate that no filter should be applied
     * @return
     * @throws FHIRParserException
     *     if the resource could not be parsed for any reason
     */
    public <T extends Resource> T parseAndFilter(Reader reader, Collection<java.lang.String> elementsToInclude) throws FHIRParserException {
    	try {
    		JsonNode jsonNode = JSON_OBJECT_MAPPER.readTree(nonClosingReader(reader));
            return parseAndFilter(jsonNode, elementsToInclude);
        } catch (FHIRParserException e) {
            throw e;
        } catch (Exception e) {
            throw new FHIRParserException(e.getMessage(), getPath(), e);
        }
    }

    /**
     * Read a resource from a JsonNode. This method does not close the passed InputStream.
     * 
     * @param <T>
     *     The resource type to read
     * @param jsonNode
     *     A JsonNode with the contents of a FHIR resource
     * @return
     * @throws FHIRParserException
     *     if the resource could not be parsed for any reason
     */
    public <T extends Resource> T parse(JsonNode jsonNode) throws FHIRParserException {
        return parseAndFilter(jsonNode, null);
    }

    /**
     * Read a resource from a JsonNode and filter its top-level elements to the collection of elementsToInclude.
     * This method does not close the passed InputStream.
     * 
     * @param <T>
     *     The resource type to read
     * @param jsonNode
     *     A JsonNode with the contents of a FHIR resource
     * @param elementsToInclude
     *     The top-level elements to include or null to indicate that no filter should be applied
     * @return
     * @throws FHIRParserException
     *     if the resource could not be parsed for any reason
     */
    @SuppressWarnings("unchecked")
    public <T extends Resource> T parseAndFilter(JsonNode jsonNode, Collection<java.lang.String> elementsToInclude) throws FHIRParserException {
        try {
            reset();
            Class<?> resourceType = getResourceType(jsonNode);
            if (elementsToInclude != null) {
                ElementFilter elementFilter = new ElementFilter(resourceType, elementsToInclude);
                jsonNode = elementFilter.apply(jsonNode);
            }
            return (T) parseResource(resourceType.getSimpleName(), jsonNode, -1);
        } catch (Exception e) {
            throw new FHIRParserException(e.getMessage(), getPath(), e);
        }
    }

    private void reset() {
        stack.clear();
    }

    private Resource parseResource(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        Class<?> resourceType = getResourceType(jsonNode);
        switch (resourceType.getSimpleName()) {
        case "Account":
        case "ActivityDefinition":
        case "ActorDefinition":
        case "AdministrableProductDefinition":
        case "AdverseEvent":
        case "AllergyIntolerance":
        case "Appointment":
        case "AppointmentResponse":
        case "ArtifactAssessment":
        case "AuditEvent":
        case "Basic":
        case "Binary":
        case "BiologicallyDerivedProduct":
        case "BiologicallyDerivedProductDispense":
        case "BodyStructure":
        case "CarePlan":
        case "CareTeam":
        case "ChargeItem":
        case "ChargeItemDefinition":
        case "Citation":
        case "Claim":
        case "ClaimResponse":
        case "ClinicalImpression":
        case "ClinicalUseDefinition":
        case "Communication":
        case "CommunicationRequest":
        case "CompartmentDefinition":
        case "Composition":
        case "Condition":
        case "ConditionDefinition":
        case "Consent":
        case "Contract":
        case "Coverage":
        case "CoverageEligibilityRequest":
        case "CoverageEligibilityResponse":
        case "DetectedIssue":
        case "Device":
        case "DeviceAssociation":
        case "DeviceDefinition":
        case "DeviceDispense":
        case "DeviceMetric":
        case "DeviceRequest":
        case "DeviceUsage":
        case "DiagnosticReport":
        case "DocumentReference":
        case "Encounter":
        case "EncounterHistory":
        case "Endpoint":
        case "EnrollmentRequest":
        case "EnrollmentResponse":
        case "EpisodeOfCare":
        case "EventDefinition":
        case "Evidence":
        case "EvidenceReport":
        case "EvidenceVariable":
        case "ExampleScenario":
        case "ExplanationOfBenefit":
        case "FamilyMemberHistory":
        case "Flag":
        case "FormularyItem":
        case "GenomicStudy":
        case "Goal":
        case "GraphDefinition":
        case "Group":
        case "GuidanceResponse":
        case "HealthcareService":
        case "ImagingSelection":
        case "ImagingStudy":
        case "Immunization":
        case "ImmunizationEvaluation":
        case "ImmunizationRecommendation":
        case "ImplementationGuide":
        case "Ingredient":
        case "InsurancePlan":
        case "InventoryItem":
        case "InventoryReport":
        case "Invoice":
        case "Library":
        case "Linkage":
        case "List":
        case "Location":
        case "ManufacturedItemDefinition":
        case "Measure":
        case "MeasureReport":
        case "Medication":
        case "MedicationAdministration":
        case "MedicationDispense":
        case "MedicationKnowledge":
        case "MedicationRequest":
        case "MedicationStatement":
        case "MedicinalProductDefinition":
        case "MessageDefinition":
        case "MessageHeader":
        case "StructureMap":
        case "Subscription":
        case "SubscriptionStatus":
        case "SubscriptionTopic":
        case "Substance":
        case "SubstanceDefinition":
        case "SubstanceNucleicAcid":
        case "SubstancePolymer":
        case "SubstanceProtein":
        case "SubstanceReferenceInformation":
        case "SubstanceSourceMaterial":
        case "SupplyDelivery":
        case "SupplyRequest":
        case "Task":
        case "TestPlan":
        case "TestReport":
        case "TestScript":
        case "Transport":
        case "VerificationResult":
        case "VisionPrescription":
        case "MolecularSequence":
        case "NamingSystem":
        case "NutritionIntake":
        case "NutritionOrder":
        case "NutritionProduct":
        case "Observation":
        case "ObservationDefinition":
        case "Organization":
        case "OrganizationAffiliation":
        case "PackagedProductDefinition":
        case "Patient":
        case "PaymentNotice":
        case "PaymentReconciliation":
        case "Permission":
        case "Person":
        case "PlanDefinition":
        case "Practitioner":
        case "PractitionerRole":
        case "Procedure":
        case "Provenance":
        case "Questionnaire":
        case "QuestionnaireResponse":
        case "RegulatedAuthorization":
        case "RelatedPerson":
        case "RequestOrchestration":
        case "Requirements":
        case "ResearchStudy":
        case "ResearchSubject":
        case "RiskAssessment":
        case "Schedule":
        case "SearchParameter":
        case "ServiceRequest":
        case "Slot":
        case "Specimen":
        case "SpecimenDefinition":
        	return throwUnsupportedElement(elementName);
        	
        case "Bundle":
            return parseBundle(elementName, jsonNode, elementIndex);
        case "CapabilityStatement":
            return parseCapabilityStatement(elementName, jsonNode, elementIndex);
        case "CodeSystem":
            return parseCodeSystem(elementName, jsonNode, elementIndex);
        case "ConceptMap":
            return parseConceptMap(elementName, jsonNode, elementIndex);
        case "OperationDefinition":
            return parseOperationDefinition(elementName, jsonNode, elementIndex);
        case "OperationOutcome":
            return parseOperationOutcome(elementName, jsonNode, elementIndex);
        case "Parameters":
            return parseParameters(elementName, jsonNode, elementIndex);
        case "StructureDefinition":
            return parseStructureDefinition(elementName, jsonNode, elementIndex);
        case "TerminologyCapabilities":
            return parseTerminologyCapabilities(elementName, jsonNode, elementIndex);
        case "ValueSet":
            return parseValueSet(elementName, jsonNode, elementIndex);
        }
        return null;
    }

    private Address parseAddress(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Address.class, jsonNode);
        }
        Address.Builder builder = Address.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.use((AddressUse) parseString(AddressUse.builder(), "use", getJsonNode(jsonNode, "use", TextNode.class), jsonNode.get("_use"), -1));
        builder.type((AddressType) parseString(AddressType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.text(parseString("text", getJsonNode(jsonNode, "text", TextNode.class), jsonNode.get("_text"), -1));
        ArrayNode lineArray = getArrayNode(jsonNode, "line", true);
        if (lineArray != null) {
            ArrayNode _lineArray = getArrayNode(jsonNode, "_line");
            for (int i = 0; i < lineArray.size(); i++) {
                builder.line(parseString("line", lineArray.get(i), getJsonNode(_lineArray, i), i));
            }
        }
        builder.city(parseString("city", getJsonNode(jsonNode, "city", TextNode.class), jsonNode.get("_city"), -1));
        builder.district(parseString("district", getJsonNode(jsonNode, "district", TextNode.class), jsonNode.get("_district"), -1));
        builder.state(parseString("state", getJsonNode(jsonNode, "state", TextNode.class), jsonNode.get("_state"), -1));
        builder.postalCode(parseString("postalCode", getJsonNode(jsonNode, "postalCode", TextNode.class), jsonNode.get("_postalCode"), -1));
        builder.country(parseString("country", getJsonNode(jsonNode, "country", TextNode.class), jsonNode.get("_country"), -1));
        builder.period(parsePeriod("period", getJsonNode(jsonNode, "period", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Annotation parseAnnotation(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Annotation.class, jsonNode);
        }
        Annotation.Builder builder = Annotation.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.author(parseChoiceElement("author", jsonNode, Reference.class, String.class));
        builder.time(parseDateTime("time", getJsonNode(jsonNode, "time", TextNode.class), jsonNode.get("_time"), -1));
        builder.text((Markdown) parseString(Markdown.builder(), "text", getJsonNode(jsonNode, "text", TextNode.class), jsonNode.get("_text"), -1));
        stackPop();
        return builder.build();
    }

    private Attachment parseAttachment(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Attachment.class, jsonNode);
        }
        Attachment.Builder builder = Attachment.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.contentType((Code) parseString(Code.builder(), "contentType", getJsonNode(jsonNode, "contentType", TextNode.class), jsonNode.get("_contentType"), -1));
        builder.language((Code) parseString(Code.builder(), "language", getJsonNode(jsonNode, "language", TextNode.class), jsonNode.get("_language"), -1));
        builder.data(parseBase64Binary("data", getJsonNode(jsonNode, "data", TextNode.class), jsonNode.get("_data"), -1));
        builder.url((Url) parseUri(Url.builder(), "url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        builder.size(parseInteger64("size", getJsonNode(jsonNode, "size", TextNode.class), jsonNode.get("_size"), -1));
        builder.hash(parseBase64Binary("hash", getJsonNode(jsonNode, "hash", TextNode.class), jsonNode.get("_hash"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.creation(parseDateTime("creation", getJsonNode(jsonNode, "creation", TextNode.class), jsonNode.get("_creation"), -1));
        builder.height((PositiveInt) parseInteger(PositiveInt.builder(), "height", getJsonNode(jsonNode, "height", NumericNode.class), jsonNode.get("_height"), -1));
        builder.width((PositiveInt) parseInteger(PositiveInt.builder(), "width", getJsonNode(jsonNode, "width", NumericNode.class), jsonNode.get("_width"), -1));
        builder.frames((PositiveInt) parseInteger(PositiveInt.builder(), "frames", getJsonNode(jsonNode, "frames", NumericNode.class), jsonNode.get("_frames"), -1));
        builder.duration(parseDecimal("duration", getJsonNode(jsonNode, "duration", NumericNode.class), jsonNode.get("_duration"), -1));
        builder.pages((PositiveInt) parseInteger(PositiveInt.builder(), "pages", getJsonNode(jsonNode, "pages", NumericNode.class), jsonNode.get("_pages"), -1));
        stackPop();
        return builder.build();
    }

    private Availability parseAvailability(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Availability.class, jsonNode);
        }
        Availability.Builder builder = Availability.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        ArrayNode availableTimeArray = getArrayNode(jsonNode, "availableTime");
        if (availableTimeArray != null) {
            for (int i = 0; i < availableTimeArray.size(); i++) {
                if (availableTimeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + availableTimeArray.get(i).getNodeType() + " for element: availableTime");
                }
                builder.availableTime(parseElement("availableTime", availableTimeArray.get(i), i));
            }
        }
        ArrayNode notAvailableTimeArray = getArrayNode(jsonNode, "notAvailableTime");
        if (notAvailableTimeArray != null) {
            for (int i = 0; i < notAvailableTimeArray.size(); i++) {
                if (notAvailableTimeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + notAvailableTimeArray.get(i).getNodeType() + " for element: notAvailableTime");
                }
                builder.notAvailableTime(parseElement("notAvailableTime", notAvailableTimeArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private void parseBackboneElement(BackboneElement.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        parseElement(builder, jsonNode);
        ArrayNode modifierExtensionArray = getArrayNode(jsonNode, "modifierExtension");
        if (modifierExtensionArray != null) {
            for (int i = 0; i < modifierExtensionArray.size(); i++) {
                if (modifierExtensionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + modifierExtensionArray.get(i).getNodeType() + " for element: modifierExtension");
                }
                builder.modifierExtension(parseExtension("modifierExtension", modifierExtensionArray.get(i), i));
            }
        }
    }

    private void parseBackboneType(BackboneType.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        ArrayNode modifierExtensionArray = getArrayNode(jsonNode, "modifierExtension");
        if (modifierExtensionArray != null) {
            for (int i = 0; i < modifierExtensionArray.size(); i++) {
                if (modifierExtensionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + modifierExtensionArray.get(i).getNodeType() + " for element: modifierExtension");
                }
                builder.modifierExtension(parseExtension("modifierExtension", modifierExtensionArray.get(i), i));
            }
        }
    }

    private Base64Binary parseBase64Binary(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Base64Binary.Builder builder = Base64Binary.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Boolean parseBoolean(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Boolean.Builder builder = Boolean.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (BooleanNode.TRUE.equals(jsonNode) || BooleanNode.FALSE.equals(jsonNode)) {
            builder.value(BooleanNode.TRUE.equals(jsonNode) ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE);
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: TRUE or FALSE but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Bundle parseBundle(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Bundle.class, jsonNode);
        }
        Bundle.Builder builder = Bundle.builder();
        builder.setValidating(validating);
        parseResource(builder, jsonNode);
        builder.identifier(parseIdentifier("identifier", getJsonNode(jsonNode, "identifier", JsonNode.class), -1));
        builder.type((BundleType) parseString(BundleType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.timestamp(parseInstant("timestamp", getJsonNode(jsonNode, "timestamp", TextNode.class), jsonNode.get("_timestamp"), -1));
        builder.total((UnsignedInt) parseInteger(UnsignedInt.builder(), "total", getJsonNode(jsonNode, "total", NumericNode.class), jsonNode.get("_total"), -1));
        ArrayNode linkArray = getArrayNode(jsonNode, "link");
        if (linkArray != null) {
            for (int i = 0; i < linkArray.size(); i++) {
                if (linkArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + linkArray.get(i).getNodeType() + " for element: link");
                }
                builder.link(parseBundleLink("link", linkArray.get(i), i));
            }
        }
        ArrayNode entryArray = getArrayNode(jsonNode, "entry");
        if (entryArray != null) {
            for (int i = 0; i < entryArray.size(); i++) {
                if (entryArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + entryArray.get(i).getNodeType() + " for element: entry");
                }
                builder.entry(parseBundleEntry("entry", entryArray.get(i), i));
            }
        }
        builder.signature(parseSignature("signature", getJsonNode(jsonNode, "signature", JsonNode.class), -1));
        builder.issues(parseOperationOutcome("issues", getJsonNode(jsonNode, "issues", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Bundle.Entry parseBundleEntry(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Bundle.Entry.class, jsonNode);
        }
        Bundle.Entry.Builder builder = Bundle.Entry.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        ArrayNode linkArray = getArrayNode(jsonNode, "link");
        if (linkArray != null) {
            for (int i = 0; i < linkArray.size(); i++) {
                if (linkArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + linkArray.get(i).getNodeType() + " for element: link");
                }
                builder.link(parseBundleLink("link", linkArray.get(i), i));
            }
        }
        builder.fullUrl(parseUri("fullUrl", getJsonNode(jsonNode, "fullUrl", TextNode.class), jsonNode.get("_fullUrl"), -1));
        builder.resource(parseResource("resource", getJsonNode(jsonNode, "resource", JsonNode.class), -1));
        builder.search(parseBundleEntrySearch("search", getJsonNode(jsonNode, "search", JsonNode.class), -1));
        builder.request(parseBundleEntryRequest("request", getJsonNode(jsonNode, "request", JsonNode.class), -1));
        builder.response(parseBundleEntryResponse("response", getJsonNode(jsonNode, "response", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Bundle.Entry.Request parseBundleEntryRequest(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Bundle.Entry.Request.class, jsonNode);
        }
        Bundle.Entry.Request.Builder builder = Bundle.Entry.Request.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.method((HTTPVerb) parseString(HTTPVerb.builder(), "method", getJsonNode(jsonNode, "method", TextNode.class), jsonNode.get("_method"), -1));
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        builder.ifNoneMatch(parseString("ifNoneMatch", getJsonNode(jsonNode, "ifNoneMatch", TextNode.class), jsonNode.get("_ifNoneMatch"), -1));
        builder.ifModifiedSince(parseInstant("ifModifiedSince", getJsonNode(jsonNode, "ifModifiedSince", TextNode.class), jsonNode.get("_ifModifiedSince"), -1));
        builder.ifMatch(parseString("ifMatch", getJsonNode(jsonNode, "ifMatch", TextNode.class), jsonNode.get("_ifMatch"), -1));
        builder.ifNoneExist(parseString("ifNoneExist", getJsonNode(jsonNode, "ifNoneExist", TextNode.class), jsonNode.get("_ifNoneExist"), -1));
        stackPop();
        return builder.build();
    }

    private Bundle.Entry.Response parseBundleEntryResponse(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Bundle.Entry.Response.class, jsonNode);
        }
        Bundle.Entry.Response.Builder builder = Bundle.Entry.Response.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.status(parseString("status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.location(parseUri("location", getJsonNode(jsonNode, "location", TextNode.class), jsonNode.get("_location"), -1));
        builder.etag(parseString("etag", getJsonNode(jsonNode, "etag", TextNode.class), jsonNode.get("_etag"), -1));
        builder.lastModified(parseInstant("lastModified", getJsonNode(jsonNode, "lastModified", TextNode.class), jsonNode.get("_lastModified"), -1));
        builder.outcome(parseResource("outcome", getJsonNode(jsonNode, "outcome", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Bundle.Entry.Search parseBundleEntrySearch(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Bundle.Entry.Search.class, jsonNode);
        }
        Bundle.Entry.Search.Builder builder = Bundle.Entry.Search.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.mode((SearchEntryMode) parseString(SearchEntryMode.builder(), "mode", getJsonNode(jsonNode, "mode", JsonNode.class), jsonNode.get("_mode"), -1));
        builder.score(parseDecimal("score", getJsonNode(jsonNode, "score", NumericNode.class), jsonNode.get("_score"), -1));
        stackPop();
        return builder.build();
    }

    private Bundle.Link parseBundleLink(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Bundle.Link.class, jsonNode);
        }
        Bundle.Link.Builder builder = Bundle.Link.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.relation((Code) parseString(Code.builder(), "relation", getJsonNode(jsonNode, "relation", TextNode.class), jsonNode.get("_relation"), -1));
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        stackPop();
        return builder.build();
    }

    private void parseCanonicalResource(CanonicalResource.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
    }

    private CapabilityStatement parseCapabilityStatement(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.class, jsonNode);
        }
        CapabilityStatement.Builder builder = CapabilityStatement.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
        builder.kind((CapabilityStatementKind) parseString(CapabilityStatementKind.builder(), "kind", getJsonNode(jsonNode, "kind", TextNode.class), jsonNode.get("_kind"), -1));
        ArrayNode instantiatesArray = getArrayNode(jsonNode, "instantiates", true);
        if (instantiatesArray != null) {
            ArrayNode _instantiatesArray = getArrayNode(jsonNode, "_instantiates");
            for (int i = 0; i < instantiatesArray.size(); i++) {
                builder.instantiates((Canonical) parseUri(Canonical.builder(), "instantiates", instantiatesArray.get(i), getJsonNode(_instantiatesArray, i), i));
            }
        }
        ArrayNode importsArray = getArrayNode(jsonNode, "imports", true);
        if (importsArray != null) {
            ArrayNode _importsArray = getArrayNode(jsonNode, "_imports");
            for (int i = 0; i < importsArray.size(); i++) {
                builder.imports((Canonical) parseUri(Canonical.builder(), "imports", importsArray.get(i), getJsonNode(_importsArray, i), i));
            }
        }
        builder.software(parseCapabilityStatementSoftware("software", getJsonNode(jsonNode, "software", JsonNode.class), -1));
        builder.implementation(parseCapabilityStatementImplementation("implementation", getJsonNode(jsonNode, "implementation", JsonNode.class), -1));
        builder.fhirVersion((FHIRVersion) parseString(FHIRVersion.builder(), "fhirVersion", getJsonNode(jsonNode, "fhirVersion", TextNode.class), jsonNode.get("_fhirVersion"), -1));
        ArrayNode formatArray = getArrayNode(jsonNode, "format", true);
        if (formatArray != null) {
            ArrayNode _formatArray = getArrayNode(jsonNode, "_format");
            for (int i = 0; i < formatArray.size(); i++) {
                builder.format((Code) parseString(Code.builder(), "format", formatArray.get(i), getJsonNode(_formatArray, i), i));
            }
        }
        ArrayNode patchFormatArray = getArrayNode(jsonNode, "patchFormat", true);
        if (patchFormatArray != null) {
            ArrayNode _patchFormatArray = getArrayNode(jsonNode, "_patchFormat");
            for (int i = 0; i < patchFormatArray.size(); i++) {
                builder.patchFormat((Code) parseString(Code.builder(), "patchFormat", patchFormatArray.get(i), getJsonNode(_patchFormatArray, i), i));
            }
        }
        ArrayNode acceptLanguageArray = getArrayNode(jsonNode, "acceptLanguage", true);
        if (acceptLanguageArray != null) {
            ArrayNode _acceptLanguageArray = getArrayNode(jsonNode, "_acceptLanguage");
            for (int i = 0; i < acceptLanguageArray.size(); i++) {
                builder.acceptLanguage((Code) parseString(Code.builder(), "acceptLanguage", acceptLanguageArray.get(i), getJsonNode(_acceptLanguageArray, i), i));
            }
        }
        ArrayNode implementationGuideArray = getArrayNode(jsonNode, "implementationGuide", true);
        if (implementationGuideArray != null) {
            ArrayNode _implementationGuideArray = getArrayNode(jsonNode, "_implementationGuide");
            for (int i = 0; i < implementationGuideArray.size(); i++) {
                builder.implementationGuide((Canonical) parseUri(Canonical.builder(), "implementationGuide", implementationGuideArray.get(i), getJsonNode(_implementationGuideArray, i), i));
            }
        }
        ArrayNode restArray = getArrayNode(jsonNode, "rest");
        if (restArray != null) {
            for (int i = 0; i < restArray.size(); i++) {
                if (restArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + restArray.get(i).getNodeType() + " for element: rest");
                }
                builder.rest(parseCapabilityStatementRest("rest", restArray.get(i), i));
            }
        }
        ArrayNode messagingArray = getArrayNode(jsonNode, "messaging");
        if (messagingArray != null) {
            for (int i = 0; i < messagingArray.size(); i++) {
                if (messagingArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + messagingArray.get(i).getNodeType() + " for element: messaging");
                }
                builder.messaging(parseCapabilityStatementMessaging("messaging", messagingArray.get(i), i));
            }
        }
        ArrayNode documentArray = getArrayNode(jsonNode, "document");
        if (documentArray != null) {
            for (int i = 0; i < documentArray.size(); i++) {
                if (documentArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + documentArray.get(i).getNodeType() + " for element: document");
                }
                builder.document(parseCapabilityStatementDocument("document", documentArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Document parseCapabilityStatementDocument(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Document.class, jsonNode);
        }
        CapabilityStatement.Document.Builder builder = CapabilityStatement.Document.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.mode((DocumentMode) parseString(DocumentMode.builder(), "mode", getJsonNode(jsonNode, "mode", TextNode.class), jsonNode.get("_mode"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        builder.profile((Canonical) parseUri(Canonical.builder(), "profile", getJsonNode(jsonNode, "profile", TextNode.class), jsonNode.get("_profile"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Implementation parseCapabilityStatementImplementation(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Implementation.class, jsonNode);
        }
        CapabilityStatement.Implementation.Builder builder = CapabilityStatement.Implementation.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.url((Url) parseUri(Url.builder(), "url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        builder.custodian(parseReference("custodian", getJsonNode(jsonNode, "custodian", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Messaging parseCapabilityStatementMessaging(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Messaging.class, jsonNode);
        }
        CapabilityStatement.Messaging.Builder builder = CapabilityStatement.Messaging.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        ArrayNode endpointArray = getArrayNode(jsonNode, "endpoint");
        if (endpointArray != null) {
            for (int i = 0; i < endpointArray.size(); i++) {
                if (endpointArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + endpointArray.get(i).getNodeType() + " for element: endpoint");
                }
                builder.endpoint(parseCapabilityStatementMessagingEndpoint("endpoint", endpointArray.get(i), i));
            }
        }
        builder.reliableCache((UnsignedInt) parseInteger(UnsignedInt.builder(), "reliableCache", getJsonNode(jsonNode, "reliableCache", NumericNode.class), jsonNode.get("_reliableCache"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        ArrayNode supportedMessageArray = getArrayNode(jsonNode, "supportedMessage");
        if (supportedMessageArray != null) {
            for (int i = 0; i < supportedMessageArray.size(); i++) {
                if (supportedMessageArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + supportedMessageArray.get(i).getNodeType() + " for element: supportedMessage");
                }
                builder.supportedMessage(parseCapabilityStatementMessagingSupportedMessage("supportedMessage", supportedMessageArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Messaging.Endpoint parseCapabilityStatementMessagingEndpoint(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Messaging.Endpoint.class, jsonNode);
        }
        CapabilityStatement.Messaging.Endpoint.Builder builder = CapabilityStatement.Messaging.Endpoint.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.protocol(parseCoding("protocol", getJsonNode(jsonNode, "protocol", JsonNode.class), -1));
        builder.address((Url) parseUri(Url.builder(), "address", getJsonNode(jsonNode, "address", TextNode.class), jsonNode.get("_address"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Messaging.SupportedMessage parseCapabilityStatementMessagingSupportedMessage(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Messaging.SupportedMessage.class, jsonNode);
        }
        CapabilityStatement.Messaging.SupportedMessage.Builder builder = CapabilityStatement.Messaging.SupportedMessage.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.mode((EventCapabilityMode) parseString(EventCapabilityMode.builder(), "mode", getJsonNode(jsonNode, "mode", TextNode.class), jsonNode.get("_mode"), -1));
        builder.definition((Canonical) parseUri(Canonical.builder(), "definition", getJsonNode(jsonNode, "definition", TextNode.class), jsonNode.get("_definition"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Rest parseCapabilityStatementRest(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Rest.class, jsonNode);
        }
        CapabilityStatement.Rest.Builder builder = CapabilityStatement.Rest.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.mode((RestfulCapabilityMode) parseString(RestfulCapabilityMode.builder(), "mode", getJsonNode(jsonNode, "mode", TextNode.class), jsonNode.get("_mode"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        builder.security(parseCapabilityStatementRestSecurity("security", getJsonNode(jsonNode, "security", JsonNode.class), -1));
        ArrayNode resourceArray = getArrayNode(jsonNode, "resource");
        if (resourceArray != null) {
            for (int i = 0; i < resourceArray.size(); i++) {
                if (resourceArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + resourceArray.get(i).getNodeType() + " for element: resource");
                }
                builder.resource(parseCapabilityStatementRestResource("resource", resourceArray.get(i), i));
            }
        }
        ArrayNode interactionArray = getArrayNode(jsonNode, "interaction");
        if (interactionArray != null) {
            for (int i = 0; i < interactionArray.size(); i++) {
                if (interactionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + interactionArray.get(i).getNodeType() + " for element: interaction");
                }
                builder.interaction(parseCapabilityStatementRestInteraction("interaction", interactionArray.get(i), i));
            }
        }
        ArrayNode searchParamArray = getArrayNode(jsonNode, "searchParam");
        if (searchParamArray != null) {
            for (int i = 0; i < searchParamArray.size(); i++) {
                if (searchParamArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + searchParamArray.get(i).getNodeType() + " for element: searchParam");
                }
                builder.searchParam(parseCapabilityStatementRestResourceSearchParam("searchParam", searchParamArray.get(i), i));
            }
        }
        ArrayNode operationArray = getArrayNode(jsonNode, "operation");
        if (operationArray != null) {
            for (int i = 0; i < operationArray.size(); i++) {
                if (operationArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + operationArray.get(i).getNodeType() + " for element: operation");
                }
                builder.operation(parseCapabilityStatementRestResourceOperation("operation", operationArray.get(i), i));
            }
        }
        ArrayNode compartmentArray = getArrayNode(jsonNode, "compartment", true);
        if (compartmentArray != null) {
            ArrayNode _compartmentArray = getArrayNode(jsonNode, "_compartment");
            for (int i = 0; i < compartmentArray.size(); i++) {
                builder.compartment((Canonical) parseUri(Canonical.builder(), "compartment", compartmentArray.get(i), getJsonNode(_compartmentArray, i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Rest.Interaction parseCapabilityStatementRestInteraction(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Rest.Interaction.class, jsonNode);
        }
        CapabilityStatement.Rest.Interaction.Builder builder = CapabilityStatement.Rest.Interaction.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((SystemRestfulInteraction) parseString(SystemRestfulInteraction.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Rest.Resource parseCapabilityStatementRestResource(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Rest.Resource.class, jsonNode);
        }
        CapabilityStatement.Rest.Resource.Builder builder = CapabilityStatement.Rest.Resource.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.type((ResourceTypeCode) parseString(ResourceTypeCode.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.profile((Canonical) parseUri(Canonical.builder(), "profile", getJsonNode(jsonNode, "profile", TextNode.class), jsonNode.get("_profile"), -1));
        ArrayNode supportedProfileArray = getArrayNode(jsonNode, "supportedProfile", true);
        if (supportedProfileArray != null) {
            ArrayNode _supportedProfileArray = getArrayNode(jsonNode, "_supportedProfile");
            for (int i = 0; i < supportedProfileArray.size(); i++) {
                builder.supportedProfile((Canonical) parseUri(Canonical.builder(), "supportedProfile", supportedProfileArray.get(i), getJsonNode(_supportedProfileArray, i), i));
            }
        }
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        ArrayNode interactionArray = getArrayNode(jsonNode, "interaction");
        if (interactionArray != null) {
            for (int i = 0; i < interactionArray.size(); i++) {
                if (interactionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + interactionArray.get(i).getNodeType() + " for element: interaction");
                }
                builder.interaction(parseCapabilityStatementRestResourceInteraction("interaction", interactionArray.get(i), i));
            }
        }
        builder.versioning((ResourceVersionPolicy) parseString(ResourceVersionPolicy.builder(), "versioning", getJsonNode(jsonNode, "versioning", TextNode.class), jsonNode.get("_versioning"), -1));
        builder.readHistory(parseBoolean("readHistory", getJsonNode(jsonNode, "readHistory", JsonNode.class), jsonNode.get("_readHistory"), -1));
        builder.updateCreate(parseBoolean("updateCreate", getJsonNode(jsonNode, "updateCreate", JsonNode.class), jsonNode.get("_updateCreate"), -1));
        builder.conditionalCreate(parseBoolean("conditionalCreate", getJsonNode(jsonNode, "conditionalCreate", JsonNode.class), jsonNode.get("_conditionalCreate"), -1));
        builder.conditionalRead((ConditionalReadStatus) parseString(ConditionalReadStatus.builder(), "conditionalRead", getJsonNode(jsonNode, "conditionalRead", TextNode.class), jsonNode.get("_conditionalRead"), -1));
        builder.conditionalUpdate(parseBoolean("conditionalUpdate", getJsonNode(jsonNode, "conditionalUpdate", JsonNode.class), jsonNode.get("_conditionalUpdate"), -1));
        builder.conditionalPatch(parseBoolean("conditionalPatch", getJsonNode(jsonNode, "conditionalPatch", JsonNode.class), jsonNode.get("_conditionalPatch"), -1));
        builder.conditionalDelete((ConditionalDeleteStatus) parseString(ConditionalDeleteStatus.builder(), "conditionalDelete", getJsonNode(jsonNode, "conditionalDelete", TextNode.class), jsonNode.get("_conditionalDelete"), -1));
        ArrayNode referencePolicyArray = getArrayNode(jsonNode, "referencePolicy", true);
        if (referencePolicyArray != null) {
            ArrayNode _referencePolicyArray = getArrayNode(jsonNode, "_referencePolicy");
            for (int i = 0; i < referencePolicyArray.size(); i++) {
                builder.referencePolicy((ReferenceHandlingPolicy) parseString(ReferenceHandlingPolicy.builder(), "referencePolicy", referencePolicyArray.get(i), getJsonNode(_referencePolicyArray, i), i));
            }
        }
        ArrayNode searchIncludeArray = getArrayNode(jsonNode, "searchInclude", true);
        if (searchIncludeArray != null) {
            ArrayNode _searchIncludeArray = getArrayNode(jsonNode, "_searchInclude");
            for (int i = 0; i < searchIncludeArray.size(); i++) {
                builder.searchInclude(parseString("searchInclude", searchIncludeArray.get(i), getJsonNode(_searchIncludeArray, i), i));
            }
        }
        ArrayNode searchRevIncludeArray = getArrayNode(jsonNode, "searchRevInclude", true);
        if (searchRevIncludeArray != null) {
            ArrayNode _searchRevIncludeArray = getArrayNode(jsonNode, "_searchRevInclude");
            for (int i = 0; i < searchRevIncludeArray.size(); i++) {
                builder.searchRevInclude(parseString("searchRevInclude", searchRevIncludeArray.get(i), getJsonNode(_searchRevIncludeArray, i), i));
            }
        }
        ArrayNode searchParamArray = getArrayNode(jsonNode, "searchParam");
        if (searchParamArray != null) {
            for (int i = 0; i < searchParamArray.size(); i++) {
                if (searchParamArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + searchParamArray.get(i).getNodeType() + " for element: searchParam");
                }
                builder.searchParam(parseCapabilityStatementRestResourceSearchParam("searchParam", searchParamArray.get(i), i));
            }
        }
        ArrayNode operationArray = getArrayNode(jsonNode, "operation");
        if (operationArray != null) {
            for (int i = 0; i < operationArray.size(); i++) {
                if (operationArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + operationArray.get(i).getNodeType() + " for element: operation");
                }
                builder.operation(parseCapabilityStatementRestResourceOperation("operation", operationArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Rest.Resource.Interaction parseCapabilityStatementRestResourceInteraction(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Rest.Resource.Interaction.class, jsonNode);
        }
        CapabilityStatement.Rest.Resource.Interaction.Builder builder = CapabilityStatement.Rest.Resource.Interaction.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((TypeRestfulInteraction) parseString(TypeRestfulInteraction.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Rest.Resource.Operation parseCapabilityStatementRestResourceOperation(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Rest.Resource.Operation.class, jsonNode);
        }
        CapabilityStatement.Rest.Resource.Operation.Builder builder = CapabilityStatement.Rest.Resource.Operation.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.definition((Canonical) parseUri(Canonical.builder(), "definition", getJsonNode(jsonNode, "definition", TextNode.class), jsonNode.get("_definition"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Rest.Resource.SearchParam parseCapabilityStatementRestResourceSearchParam(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Rest.Resource.SearchParam.class, jsonNode);
        }
        CapabilityStatement.Rest.Resource.SearchParam.Builder builder = CapabilityStatement.Rest.Resource.SearchParam.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.definition((Canonical) parseUri(Canonical.builder(), "definition", getJsonNode(jsonNode, "definition", TextNode.class), jsonNode.get("_definition"), -1));
        builder.type((SearchParamType) parseString(SearchParamType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Rest.Security parseCapabilityStatementRestSecurity(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Rest.Security.class, jsonNode);
        }
        CapabilityStatement.Rest.Security.Builder builder = CapabilityStatement.Rest.Security.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.cors(parseBoolean("cors", getJsonNode(jsonNode, "cors", JsonNode.class), jsonNode.get("_cors"), -1));
        ArrayNode serviceArray = getArrayNode(jsonNode, "service");
        if (serviceArray != null) {
            for (int i = 0; i < serviceArray.size(); i++) {
                if (serviceArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + serviceArray.get(i).getNodeType() + " for element: service");
                }
                builder.service(parseCodeableConcept("service", serviceArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        stackPop();
        return builder.build();
    }

    private CapabilityStatement.Software parseCapabilityStatementSoftware(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CapabilityStatement.Software.class, jsonNode);
        }
        CapabilityStatement.Software.Builder builder = CapabilityStatement.Software.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.releaseDate(parseDateTime("releaseDate", getJsonNode(jsonNode, "releaseDate", TextNode.class), jsonNode.get("_releaseDate"), -1));
        stackPop();
        return builder.build();
    }

    private CodeSystem parseCodeSystem(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeSystem.class, jsonNode);
        }
        CodeSystem.Builder builder = CodeSystem.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
        builder.approvalDate(parseDate("approvalDate", getJsonNode(jsonNode, "approvalDate", TextNode.class), jsonNode.get("_approvalDate"), -1));
        builder.lastReviewDate(parseDate("lastReviewDate", getJsonNode(jsonNode, "lastReviewDate", TextNode.class), jsonNode.get("_lastReviewDate"), -1));
        builder.effectivePeriod(parsePeriod("effectivePeriod", getJsonNode(jsonNode, "effectivePeriod", JsonNode.class), -1));
        ArrayNode topicArray = getArrayNode(jsonNode, "topic");
        if (topicArray != null) {
            for (int i = 0; i < topicArray.size(); i++) {
                if (topicArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + topicArray.get(i).getNodeType() + " for element: topic");
                }
                builder.topic(parseCodeableConcept("topic", topicArray.get(i), i));
            }
        }
        ArrayNode authorArray = getArrayNode(jsonNode, "author");
        if (authorArray != null) {
            for (int i = 0; i < authorArray.size(); i++) {
                if (authorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + authorArray.get(i).getNodeType() + " for element: author");
                }
                builder.author(parseContactDetail("author", authorArray.get(i), i));
            }
        }
        ArrayNode editorArray = getArrayNode(jsonNode, "editor");
        if (editorArray != null) {
            for (int i = 0; i < editorArray.size(); i++) {
                if (editorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + editorArray.get(i).getNodeType() + " for element: editor");
                }
                builder.editor(parseContactDetail("editor", editorArray.get(i), i));
            }
        }
        ArrayNode reviewerArray = getArrayNode(jsonNode, "reviewer");
        if (reviewerArray != null) {
            for (int i = 0; i < reviewerArray.size(); i++) {
                if (reviewerArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + reviewerArray.get(i).getNodeType() + " for element: reviewer");
                }
                builder.reviewer(parseContactDetail("reviewer", reviewerArray.get(i), i));
            }
        }
        ArrayNode endorserArray = getArrayNode(jsonNode, "endorser");
        if (endorserArray != null) {
            for (int i = 0; i < endorserArray.size(); i++) {
                if (endorserArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + endorserArray.get(i).getNodeType() + " for element: endorser");
                }
                builder.endorser(parseContactDetail("endorser", endorserArray.get(i), i));
            }
        }
        ArrayNode relatedArtifactArray = getArrayNode(jsonNode, "relatedArtifact");
        if (relatedArtifactArray != null) {
            for (int i = 0; i < relatedArtifactArray.size(); i++) {
                if (relatedArtifactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + relatedArtifactArray.get(i).getNodeType() + " for element: relatedArtifact");
                }
                builder.relatedArtifact(parseRelatedArtifact("relatedArtifact", relatedArtifactArray.get(i), i));
            }
        }
        builder.caseSensitive(parseBoolean("caseSensitive", getJsonNode(jsonNode, "caseSensitive", JsonNode.class), jsonNode.get("_caseSensitive"), -1));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        builder.hierarchyMeaning((CodeSystemHierarchyMeaning) parseString(CodeSystemHierarchyMeaning.builder(), "hierarchyMeaning", getJsonNode(jsonNode, "hierarchyMeaning", TextNode.class), jsonNode.get("_hierarchyMeaning"), -1));
        builder.compositional(parseBoolean("compositional", getJsonNode(jsonNode, "compositional", JsonNode.class), jsonNode.get("_compositional"), -1));
        builder.versionNeeded(parseBoolean("versionNeeded", getJsonNode(jsonNode, "versionNeeded", JsonNode.class), jsonNode.get("_versionNeeded"), -1));
        builder.content((CodeSystemContentMode) parseString(CodeSystemContentMode.builder(), "content", getJsonNode(jsonNode, "content", TextNode.class), jsonNode.get("_content"), -1));
        builder.supplements((Canonical) parseUri(Canonical.builder(), "supplements", getJsonNode(jsonNode, "supplements", TextNode.class), jsonNode.get("_supplements"), -1));
        builder.count((UnsignedInt) parseInteger(UnsignedInt.builder(), "count", getJsonNode(jsonNode, "count", NumericNode.class), jsonNode.get("_count"), -1));
        ArrayNode filterArray = getArrayNode(jsonNode, "filter");
        if (filterArray != null) {
            for (int i = 0; i < filterArray.size(); i++) {
                if (filterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + filterArray.get(i).getNodeType() + " for element: filter");
                }
                builder.filter(parseCodeSystemFilter("filter", filterArray.get(i), i));
            }
        }
        ArrayNode propertyArray = getArrayNode(jsonNode, "property");
        if (propertyArray != null) {
            for (int i = 0; i < propertyArray.size(); i++) {
                if (propertyArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + propertyArray.get(i).getNodeType() + " for element: property");
                }
                builder.property(parseCodeSystemProperty("property", propertyArray.get(i), i));
            }
        }
        ArrayNode conceptArray = getArrayNode(jsonNode, "concept");
        if (conceptArray != null) {
            for (int i = 0; i < conceptArray.size(); i++) {
                if (conceptArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + conceptArray.get(i).getNodeType() + " for element: concept");
                }
                builder.concept(parseCodeSystemConcept("concept", conceptArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private CodeSystem.Concept parseCodeSystemConcept(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeSystem.Concept.class, jsonNode);
        }
        CodeSystem.Concept.Builder builder = CodeSystem.Concept.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        builder.definition(parseString("definition", getJsonNode(jsonNode, "definition", TextNode.class), jsonNode.get("_definition"), -1));
        ArrayNode designationArray = getArrayNode(jsonNode, "designation");
        if (designationArray != null) {
            for (int i = 0; i < designationArray.size(); i++) {
                if (designationArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + designationArray.get(i).getNodeType() + " for element: designation");
                }
                builder.designation(parseCodeSystemConceptDesignation("designation", designationArray.get(i), i));
            }
        }
        ArrayNode propertyArray = getArrayNode(jsonNode, "property");
        if (propertyArray != null) {
            for (int i = 0; i < propertyArray.size(); i++) {
                if (propertyArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + propertyArray.get(i).getNodeType() + " for element: property");
                }
                builder.property(parseCodeSystemConceptProperty("property", propertyArray.get(i), i));
            }
        }
        ArrayNode conceptArray = getArrayNode(jsonNode, "concept");
        if (conceptArray != null) {
            for (int i = 0; i < conceptArray.size(); i++) {
                if (conceptArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + conceptArray.get(i).getNodeType() + " for element: concept");
                }
                builder.concept(parseCodeSystemConcept("concept", conceptArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private CodeSystem.Concept.Designation parseCodeSystemConceptDesignation(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeSystem.Concept.Designation.class, jsonNode);
        }
        CodeSystem.Concept.Designation.Builder builder = CodeSystem.Concept.Designation.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.language((Code) parseString(Code.builder(), "language", getJsonNode(jsonNode, "language", TextNode.class), jsonNode.get("_language"), -1));
        builder.use(parseCoding("use", getJsonNode(jsonNode, "use", JsonNode.class), -1));
        ArrayNode additionalUseArray = getArrayNode(jsonNode, "additionalUse");
        if (additionalUseArray != null) {
            for (int i = 0; i < additionalUseArray.size(); i++) {
                if (additionalUseArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + additionalUseArray.get(i).getNodeType() + " for element: additionalUse");
                }
                builder.additionalUse(parseCoding("additionalUse", additionalUseArray.get(i), i));
            }
        }
        builder.value(parseString("value", getJsonNode(jsonNode, "value", TextNode.class), jsonNode.get("_value"), -1));
        stackPop();
        return builder.build();
    }

    private CodeSystem.Concept.Property parseCodeSystemConceptProperty(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeSystem.Concept.Property.class, jsonNode);
        }
        CodeSystem.Concept.Property.Builder builder = CodeSystem.Concept.Property.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.value(parseChoiceElement("value", jsonNode, Code.class, Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class));
        stackPop();
        return builder.build();
    }

    private CodeSystem.Filter parseCodeSystemFilter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeSystem.Filter.class, jsonNode);
        }
        CodeSystem.Filter.Builder builder = CodeSystem.Filter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.description(parseString("description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode operatorArray = getArrayNode(jsonNode, "operator", true);
        if (operatorArray != null) {
            ArrayNode _operatorArray = getArrayNode(jsonNode, "_operator");
            for (int i = 0; i < operatorArray.size(); i++) {
                builder.operator((FilterOperator) parseString(FilterOperator.builder(), "operator", operatorArray.get(i), getJsonNode(_operatorArray, i), i));
            }
        }
        builder.value(parseString("value", getJsonNode(jsonNode, "value", TextNode.class), jsonNode.get("_value"), -1));
        stackPop();
        return builder.build();
    }

    private CodeSystem.Property parseCodeSystemProperty(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeSystem.Property.class, jsonNode);
        }
        CodeSystem.Property.Builder builder = CodeSystem.Property.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.uri(parseUri("uri", getJsonNode(jsonNode, "uri", TextNode.class), jsonNode.get("_uri"), -1));
        builder.description(parseString("description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.type((PropertyType) parseString(PropertyType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        stackPop();
        return builder.build();
    }

    private CodeableConcept parseCodeableConcept(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeableConcept.class, jsonNode);
        }
        CodeableConcept.Builder builder = CodeableConcept.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        ArrayNode codingArray = getArrayNode(jsonNode, "coding");
        if (codingArray != null) {
            for (int i = 0; i < codingArray.size(); i++) {
                if (codingArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + codingArray.get(i).getNodeType() + " for element: coding");
                }
                builder.coding(parseCoding("coding", codingArray.get(i), i));
            }
        }
        builder.text(parseString("text", getJsonNode(jsonNode, "text", TextNode.class), jsonNode.get("_text"), -1));
        stackPop();
        return builder.build();
    }

    private CodeableReference parseCodeableReference(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(CodeableReference.class, jsonNode);
        }
        CodeableReference.Builder builder = CodeableReference.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.concept(parseCodeableConcept("concept", getJsonNode(jsonNode, "concept", JsonNode.class), -1));
        builder.reference(parseReference("reference", getJsonNode(jsonNode, "reference", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Coding parseCoding(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Coding.class, jsonNode);
        }
        Coding.Builder builder = Coding.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.system(parseUri("system", getJsonNode(jsonNode, "system", TextNode.class), jsonNode.get("_system"), -1));
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        builder.userSelected(parseBoolean("userSelected", getJsonNode(jsonNode, "userSelected", JsonNode.class), jsonNode.get("_userSelected"), -1));
        stackPop();
        return builder.build();
    }

    private ConceptMap parseConceptMap(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.class, jsonNode);
        }
        ConceptMap.Builder builder = ConceptMap.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
        builder.approvalDate(parseDate("approvalDate", getJsonNode(jsonNode, "approvalDate", TextNode.class), jsonNode.get("_approvalDate"), -1));
        builder.lastReviewDate(parseDate("lastReviewDate", getJsonNode(jsonNode, "lastReviewDate", TextNode.class), jsonNode.get("_lastReviewDate"), -1));
        builder.effectivePeriod(parsePeriod("effectivePeriod", getJsonNode(jsonNode, "effectivePeriod", JsonNode.class), -1));
        ArrayNode topicArray = getArrayNode(jsonNode, "topic");
        if (topicArray != null) {
            for (int i = 0; i < topicArray.size(); i++) {
                if (topicArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + topicArray.get(i).getNodeType() + " for element: topic");
                }
                builder.topic(parseCodeableConcept("topic", topicArray.get(i), i));
            }
        }
        ArrayNode authorArray = getArrayNode(jsonNode, "author");
        if (authorArray != null) {
            for (int i = 0; i < authorArray.size(); i++) {
                if (authorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + authorArray.get(i).getNodeType() + " for element: author");
                }
                builder.author(parseContactDetail("author", authorArray.get(i), i));
            }
        }
        ArrayNode editorArray = getArrayNode(jsonNode, "editor");
        if (editorArray != null) {
            for (int i = 0; i < editorArray.size(); i++) {
                if (editorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + editorArray.get(i).getNodeType() + " for element: editor");
                }
                builder.editor(parseContactDetail("editor", editorArray.get(i), i));
            }
        }
        ArrayNode reviewerArray = getArrayNode(jsonNode, "reviewer");
        if (reviewerArray != null) {
            for (int i = 0; i < reviewerArray.size(); i++) {
                if (reviewerArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + reviewerArray.get(i).getNodeType() + " for element: reviewer");
                }
                builder.reviewer(parseContactDetail("reviewer", reviewerArray.get(i), i));
            }
        }
        ArrayNode endorserArray = getArrayNode(jsonNode, "endorser");
        if (endorserArray != null) {
            for (int i = 0; i < endorserArray.size(); i++) {
                if (endorserArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + endorserArray.get(i).getNodeType() + " for element: endorser");
                }
                builder.endorser(parseContactDetail("endorser", endorserArray.get(i), i));
            }
        }
        ArrayNode relatedArtifactArray = getArrayNode(jsonNode, "relatedArtifact");
        if (relatedArtifactArray != null) {
            for (int i = 0; i < relatedArtifactArray.size(); i++) {
                if (relatedArtifactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + relatedArtifactArray.get(i).getNodeType() + " for element: relatedArtifact");
                }
                builder.relatedArtifact(parseRelatedArtifact("relatedArtifact", relatedArtifactArray.get(i), i));
            }
        }
        ArrayNode propertyArray = getArrayNode(jsonNode, "property");
        if (propertyArray != null) {
            for (int i = 0; i < propertyArray.size(); i++) {
                if (propertyArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + propertyArray.get(i).getNodeType() + " for element: property");
                }
                builder.property(parseConceptMapProperty("property", propertyArray.get(i), i));
            }
        }
        ArrayNode additionalAttributeArray = getArrayNode(jsonNode, "additionalAttribute");
        if (additionalAttributeArray != null) {
            for (int i = 0; i < additionalAttributeArray.size(); i++) {
                if (additionalAttributeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + additionalAttributeArray.get(i).getNodeType() + " for element: additionalAttribute");
                }
                builder.additionalAttribute(parseConceptMapAdditionalAttribute("additionalAttribute", additionalAttributeArray.get(i), i));
            }
        }
        builder.sourceScope(parseChoiceElement("sourceScope", jsonNode, Uri.class, Canonical.class));
        builder.targetScope(parseChoiceElement("targetScope", jsonNode, Uri.class, Canonical.class));
        ArrayNode groupArray = getArrayNode(jsonNode, "group");
        if (groupArray != null) {
            for (int i = 0; i < groupArray.size(); i++) {
                if (groupArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + groupArray.get(i).getNodeType() + " for element: group");
                }
                builder.group(parseConceptMapGroup("group", groupArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ConceptMap.AdditionalAttribute parseConceptMapAdditionalAttribute(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.AdditionalAttribute.class, jsonNode);
        }
        ConceptMap.AdditionalAttribute.Builder builder = ConceptMap.AdditionalAttribute.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.uri(parseUri("uri", getJsonNode(jsonNode, "uri", TextNode.class), jsonNode.get("_uri"), -1));
        builder.description(parseString("description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.type((ConceptMapmapAttributeType) parseString(ConceptMapmapAttributeType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        stackPop();
        return builder.build();
    }

    private ConceptMap.Group parseConceptMapGroup(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.Group.class, jsonNode);
        }
        ConceptMap.Group.Builder builder = ConceptMap.Group.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.source((Canonical) parseUri(Canonical.builder(), "source", getJsonNode(jsonNode, "source", TextNode.class), jsonNode.get("_source"), -1));
        builder.target((Canonical) parseUri(Canonical.builder(), "target", getJsonNode(jsonNode, "target", TextNode.class), jsonNode.get("_target"), -1));
        ArrayNode elementArray = getArrayNode(jsonNode, "element");
        if (elementArray != null) {
            for (int i = 0; i < elementArray.size(); i++) {
                if (elementArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + elementArray.get(i).getNodeType() + " for element: element");
                }
                builder.element(parseConceptMapGroupElement("element", elementArray.get(i), i));
            }
        }
        builder.unmapped(parseConceptMapGroupUnmapped("unmapped", getJsonNode(jsonNode, "unmapped", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private ConceptMap.Group.Element parseConceptMapGroupElement(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.Group.Element.class, jsonNode);
        }
        ConceptMap.Group.Element.Builder builder = ConceptMap.Group.Element.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        builder.noMap(parseBoolean("noMap", getJsonNode(jsonNode, "noMap", JsonNode.class), jsonNode.get("_noMap"), -1));
        ArrayNode targetArray = getArrayNode(jsonNode, "target");
        if (targetArray != null) {
            for (int i = 0; i < targetArray.size(); i++) {
                if (targetArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + targetArray.get(i).getNodeType() + " for element: target");
                }
                builder.target(parseConceptMapGroupElementTarget("target", targetArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ConceptMap.Group.Element.Target parseConceptMapGroupElementTarget(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.Group.Element.Target.class, jsonNode);
        }
        ConceptMap.Group.Element.Target.Builder builder = ConceptMap.Group.Element.Target.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        builder.relationship((ConceptMapRelationship) parseString(ConceptMapRelationship.builder(), "relationship", getJsonNode(jsonNode, "relationship", TextNode.class), jsonNode.get("_relationship"), -1));
        builder.comment(parseString("comment", getJsonNode(jsonNode, "comment", TextNode.class), jsonNode.get("_comment"), -1));
        ArrayNode propertyArray = getArrayNode(jsonNode, "property");
        if (propertyArray != null) {
            for (int i = 0; i < propertyArray.size(); i++) {
                if (propertyArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + propertyArray.get(i).getNodeType() + " for element: property");
                }
                builder.property(parseConceptMapGroupElementTargetProperty("property", propertyArray.get(i), i));
            }
        }
        ArrayNode dependsOnArray = getArrayNode(jsonNode, "dependsOn");
        if (dependsOnArray != null) {
            for (int i = 0; i < dependsOnArray.size(); i++) {
                if (dependsOnArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + dependsOnArray.get(i).getNodeType() + " for element: dependsOn");
                }
                builder.dependsOn(parseConceptMapGroupElementTargetDependsOn("dependsOn", dependsOnArray.get(i), i));
            }
        }
        ArrayNode productArray = getArrayNode(jsonNode, "product");
        if (productArray != null) {
            for (int i = 0; i < productArray.size(); i++) {
                if (productArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + productArray.get(i).getNodeType() + " for element: product");
                }
                builder.product(parseConceptMapGroupElementTargetDependsOn("product", productArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ConceptMap.Group.Element.Target.DependsOn parseConceptMapGroupElementTargetDependsOn(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.Group.Element.Target.DependsOn.class, jsonNode);
        }
        ConceptMap.Group.Element.Target.DependsOn.Builder builder = ConceptMap.Group.Element.Target.DependsOn.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.attribute((Code) parseString(Code.builder(), "attribute", getJsonNode(jsonNode, "attribute", TextNode.class), jsonNode.get("_attribute"), -1));
        builder.value(parseChoiceElement("value", jsonNode, Code.class, Coding.class, String.class, Boolean.class, Quantity.class));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        stackPop();
        return builder.build();
    }

    private ConceptMap.Group.Element.Target.Property parseConceptMapGroupElementTargetProperty(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.Group.Element.Target.Property.class, jsonNode);
        }
        ConceptMap.Group.Element.Target.Property.Builder builder = ConceptMap.Group.Element.Target.Property.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.value(parseChoiceElement("value", jsonNode, Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class, Code.class));
        stackPop();
        return builder.build();
    }

    private ConceptMap.Group.Unmapped parseConceptMapGroupUnmapped(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.Group.Unmapped.class, jsonNode);
        }
        ConceptMap.Group.Unmapped.Builder builder = ConceptMap.Group.Unmapped.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.mode((ConceptMapGroupUnmappedMode) parseString(ConceptMapGroupUnmappedMode.builder(), "mode", getJsonNode(jsonNode, "mode", TextNode.class), jsonNode.get("_mode"), -1));
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        builder.relationship((UnmappedConceptMapRelationship) parseString(UnmappedConceptMapRelationship.builder(), "relationship", getJsonNode(jsonNode, "relationship", TextNode.class), jsonNode.get("_relationship"), -1));
        builder.otherMap((Canonical) parseUri(Canonical.builder(), "otherMap", getJsonNode(jsonNode, "otherMap", TextNode.class), jsonNode.get("_otherMap"), -1));
        stackPop();
        return builder.build();
    }

    private ConceptMap.Property parseConceptMapProperty(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ConceptMap.Property.class, jsonNode);
        }
        ConceptMap.Property.Builder builder = ConceptMap.Property.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.uri(parseUri("uri", getJsonNode(jsonNode, "uri", TextNode.class), jsonNode.get("_uri"), -1));
        builder.description(parseString("description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.type((PropertyType) parseString(PropertyType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.system((Canonical) parseUri(Canonical.builder(), "system", getJsonNode(jsonNode, "system", TextNode.class), jsonNode.get("_system"), -1));
        stackPop();
        return builder.build();
    }

    private ContactDetail parseContactDetail(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ContactDetail.class, jsonNode);
        }
        ContactDetail.Builder builder = ContactDetail.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        ArrayNode telecomArray = getArrayNode(jsonNode, "telecom");
        if (telecomArray != null) {
            for (int i = 0; i < telecomArray.size(); i++) {
                if (telecomArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + telecomArray.get(i).getNodeType() + " for element: telecom");
                }
                builder.telecom(parseContactPoint("telecom", telecomArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ContactPoint parseContactPoint(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ContactPoint.class, jsonNode);
        }
        ContactPoint.Builder builder = ContactPoint.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.system((ContactPointSystem) parseString(ContactPointSystem.builder(), "system", getJsonNode(jsonNode, "system", TextNode.class), jsonNode.get("_system"), -1));
        builder.value(parseString("value", getJsonNode(jsonNode, "value", TextNode.class), jsonNode.get("_value"), -1));
        builder.use((ContactPointUse) parseString(ContactPointUse.builder(), "use", getJsonNode(jsonNode, "use", TextNode.class), jsonNode.get("_use"), -1));
        builder.rank((PositiveInt) parseInteger(PositiveInt.builder(), "rank", getJsonNode(jsonNode, "rank", NumericNode.class), jsonNode.get("_rank"), -1));
        builder.period(parsePeriod("period", getJsonNode(jsonNode, "period", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Contributor parseContributor(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Contributor.class, jsonNode);
        }
        Contributor.Builder builder = Contributor.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.type((ContributorType) parseString(ContributorType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private DataRequirement parseDataRequirement(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(DataRequirement.class, jsonNode);
        }
        DataRequirement.Builder builder = DataRequirement.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.type((FHIRTypes) parseString(FHIRTypes.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        ArrayNode profileArray = getArrayNode(jsonNode, "profile", true);
        if (profileArray != null) {
            ArrayNode _profileArray = getArrayNode(jsonNode, "_profile");
            for (int i = 0; i < profileArray.size(); i++) {
                builder.profile((Canonical) parseUri(Canonical.builder(), "profile", profileArray.get(i), getJsonNode(_profileArray, i), i));
            }
        }
        builder.subject(parseChoiceElement("subject", jsonNode, CodeableConcept.class, Reference.class));
        ArrayNode mustSupportArray = getArrayNode(jsonNode, "mustSupport", true);
        if (mustSupportArray != null) {
            ArrayNode _mustSupportArray = getArrayNode(jsonNode, "_mustSupport");
            for (int i = 0; i < mustSupportArray.size(); i++) {
                builder.mustSupport(parseString("mustSupport", mustSupportArray.get(i), getJsonNode(_mustSupportArray, i), i));
            }
        }
        ArrayNode codeFilterArray = getArrayNode(jsonNode, "codeFilter");
        if (codeFilterArray != null) {
            for (int i = 0; i < codeFilterArray.size(); i++) {
                if (codeFilterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + codeFilterArray.get(i).getNodeType() + " for element: codeFilter");
                }
                builder.codeFilter(parseDataRequirementCodeFilter("codeFilter", codeFilterArray.get(i), i));
            }
        }
        ArrayNode dateFilterArray = getArrayNode(jsonNode, "dateFilter");
        if (dateFilterArray != null) {
            for (int i = 0; i < dateFilterArray.size(); i++) {
                if (dateFilterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + dateFilterArray.get(i).getNodeType() + " for element: dateFilter");
                }
                builder.dateFilter(parseDataRequirementDateFilter("dateFilter", dateFilterArray.get(i), i));
            }
        }
        ArrayNode valueFilterArray = getArrayNode(jsonNode, "valueFilter");
        if (valueFilterArray != null) {
            for (int i = 0; i < valueFilterArray.size(); i++) {
                if (valueFilterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + valueFilterArray.get(i).getNodeType() + " for element: valueFilter");
                }
                builder.valueFilter(parseElement("valueFilter", valueFilterArray.get(i), i));
            }
        }
        builder.limit((PositiveInt) parseInteger(PositiveInt.builder(), "limit", getJsonNode(jsonNode, "limit", NumericNode.class), jsonNode.get("_limit"), -1));
        ArrayNode sortArray = getArrayNode(jsonNode, "sort");
        if (sortArray != null) {
            for (int i = 0; i < sortArray.size(); i++) {
                if (sortArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + sortArray.get(i).getNodeType() + " for element: sort");
                }
                builder.sort(parseDataRequirementSort("sort", sortArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private DataRequirement.CodeFilter parseDataRequirementCodeFilter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(DataRequirement.CodeFilter.class, jsonNode);
        }
        DataRequirement.CodeFilter.Builder builder = DataRequirement.CodeFilter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.path(parseString("path", getJsonNode(jsonNode, "path", TextNode.class), jsonNode.get("_path"), -1));
        builder.searchParam(parseString("searchParam", getJsonNode(jsonNode, "searchParam", TextNode.class), jsonNode.get("_searchParam"), -1));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        ArrayNode codeArray = getArrayNode(jsonNode, "code");
        if (codeArray != null) {
            for (int i = 0; i < codeArray.size(); i++) {
                if (codeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + codeArray.get(i).getNodeType() + " for element: code");
                }
                builder.code(parseCoding("code", codeArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private DataRequirement.DateFilter parseDataRequirementDateFilter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(DataRequirement.DateFilter.class, jsonNode);
        }
        DataRequirement.DateFilter.Builder builder = DataRequirement.DateFilter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.path(parseString("path", getJsonNode(jsonNode, "path", TextNode.class), jsonNode.get("_path"), -1));
        builder.searchParam(parseString("searchParam", getJsonNode(jsonNode, "searchParam", TextNode.class), jsonNode.get("_searchParam"), -1));
        builder.value(parseChoiceElement("value", jsonNode, DateTime.class, Period.class, Duration.class));
        stackPop();
        return builder.build();
    }

    private DataRequirement.Sort parseDataRequirementSort(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(DataRequirement.Sort.class, jsonNode);
        }
        DataRequirement.Sort.Builder builder = DataRequirement.Sort.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.path(parseString("path", getJsonNode(jsonNode, "path", TextNode.class), jsonNode.get("_path"), -1));
        builder.direction((SortDirection) parseString(SortDirection.builder(), "direction", getJsonNode(jsonNode, "direction", TextNode.class), jsonNode.get("_direction"), -1));
        stackPop();
        return builder.build();
    }

    private void parseDataType(DataType.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        parseElement(builder, jsonNode);
    }

    private Date parseDate(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Date.Builder builder = Date.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private DateTime parseDateTime(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        DateTime.Builder builder = DateTime.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Decimal parseDecimal(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Decimal.Builder builder = Decimal.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
        	ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.NUMBER) {
            NumericNode jsonNumber = (NumericNode) jsonNode;
            builder.value(jsonNumber.decimalValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: NUMBER but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private void parseDomainResource(DomainResource.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        parseResource(builder, jsonNode);
        builder.text(parseNarrative("text", getJsonNode(jsonNode, "text", JsonNode.class), -1));
        ArrayNode containedArray = getArrayNode(jsonNode, "contained");
        if (containedArray != null) {
            for (int i = 0; i < containedArray.size(); i++) {
                if (containedArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + containedArray.get(i).getNodeType() + " for element: contained");
                }
                builder.contained(parseResource("contained", containedArray.get(i), i));
            }
        }
        ArrayNode extensionArray = getArrayNode(jsonNode, "extension");
        if (extensionArray != null) {
            for (int i = 0; i < extensionArray.size(); i++) {
                if (extensionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + extensionArray.get(i).getNodeType() + " for element: extension");
                }
                builder.extension(parseExtension("extension", extensionArray.get(i), i));
            }
        }
        ArrayNode modifierExtensionArray = getArrayNode(jsonNode, "modifierExtension");
        if (modifierExtensionArray != null) {
            for (int i = 0; i < modifierExtensionArray.size(); i++) {
                if (modifierExtensionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + modifierExtensionArray.get(i).getNodeType() + " for element: modifierExtension");
                }
                builder.modifierExtension(parseExtension("modifierExtension", modifierExtensionArray.get(i), i));
            }
        }
    }

    private Dosage parseDosage(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Dosage.class, jsonNode);
        }
        Dosage.Builder builder = Dosage.builder();
        builder.setValidating(validating);
        parseBackboneType(builder, jsonNode);
        builder.sequence(parseInteger("sequence", getJsonNode(jsonNode, "sequence", NumericNode.class), jsonNode.get("_sequence"), -1));
        builder.text(parseString("text", getJsonNode(jsonNode, "text", TextNode.class), jsonNode.get("_text"), -1));
        ArrayNode additionalInstructionArray = getArrayNode(jsonNode, "additionalInstruction");
        if (additionalInstructionArray != null) {
            for (int i = 0; i < additionalInstructionArray.size(); i++) {
                if (additionalInstructionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + additionalInstructionArray.get(i).getNodeType() + " for element: additionalInstruction");
                }
                builder.additionalInstruction(parseCodeableConcept("additionalInstruction", additionalInstructionArray.get(i), i));
            }
        }
        builder.patientInstruction(parseString("patientInstruction", getJsonNode(jsonNode, "patientInstruction", TextNode.class), jsonNode.get("_patientInstruction"), -1));
        builder.timing(parseTiming("timing", getJsonNode(jsonNode, "timing", JsonNode.class), -1));
        builder.asNeeded(parseBoolean("asNeeded", getJsonNode(jsonNode, "asNeeded", JsonNode.class), jsonNode.get("_asNeeded"), -1));
        ArrayNode asNeededForArray = getArrayNode(jsonNode, "asNeededFor");
        if (asNeededForArray != null) {
            for (int i = 0; i < asNeededForArray.size(); i++) {
                if (asNeededForArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + asNeededForArray.get(i).getNodeType() + " for element: asNeededFor");
                }
                builder.asNeededFor(parseCodeableConcept("asNeededFor", asNeededForArray.get(i), i));
            }
        }
        builder.site(parseCodeableConcept("site", getJsonNode(jsonNode, "site", JsonNode.class), -1));
        builder.route(parseCodeableConcept("route", getJsonNode(jsonNode, "route", JsonNode.class), -1));
        builder.method(parseCodeableConcept("method", getJsonNode(jsonNode, "method", JsonNode.class), -1));
        ArrayNode doseAndRateArray = getArrayNode(jsonNode, "doseAndRate");
        if (doseAndRateArray != null) {
            for (int i = 0; i < doseAndRateArray.size(); i++) {
                if (doseAndRateArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + doseAndRateArray.get(i).getNodeType() + " for element: doseAndRate");
                }
                builder.doseAndRate(parseDosageDoseAndRate("doseAndRate", doseAndRateArray.get(i), i));
            }
        }
        ArrayNode maxDosePerPeriodArray = getArrayNode(jsonNode, "maxDosePerPeriod");
        if (maxDosePerPeriodArray != null) {
            for (int i = 0; i < maxDosePerPeriodArray.size(); i++) {
                if (maxDosePerPeriodArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + maxDosePerPeriodArray.get(i).getNodeType() + " for element: maxDosePerPeriod");
                }
                builder.maxDosePerPeriod(parseRatio("maxDosePerPeriod", maxDosePerPeriodArray.get(i), i));
            }
        }
        builder.maxDosePerAdministration((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "maxDosePerAdministration", getJsonNode(jsonNode, "maxDosePerAdministration", JsonNode.class), -1));
        builder.maxDosePerLifetime((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "maxDosePerLifetime", getJsonNode(jsonNode, "maxDosePerLifetime", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Dosage.DoseAndRate parseDosageDoseAndRate(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Dosage.DoseAndRate.class, jsonNode);
        }
        Dosage.DoseAndRate.Builder builder = Dosage.DoseAndRate.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.type(parseCodeableConcept("type", getJsonNode(jsonNode, "type", JsonNode.class), -1));
        builder.dose(parseChoiceElement("dose", jsonNode, Range.class, SimpleQuantity.class));
        builder.rate(parseChoiceElement("rate", jsonNode, Ratio.class, Range.class, SimpleQuantity.class));
        stackPop();
        return builder.build();
    }

    private void parseElement(Element.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        builder.id(parseJavaString("id", getJsonNode(jsonNode, "id", TextNode.class), -1));
        ArrayNode extensionArray = getArrayNode(jsonNode, "extension");
        if (extensionArray != null) {
            for (int i = 0; i < extensionArray.size(); i++) {
                if (extensionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + extensionArray.get(i).getNodeType() + " for element: extension");
                }
                builder.extension(parseExtension("extension", extensionArray.get(i), i));
            }
        }
    }

    private ElementDefinition parseElementDefinition(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.class, jsonNode);
        }
        ElementDefinition.Builder builder = ElementDefinition.builder();
        builder.setValidating(validating);
        parseBackboneType(builder, jsonNode);
        builder.path(parseString("path", getJsonNode(jsonNode, "path", TextNode.class), jsonNode.get("_path"), -1));
        ArrayNode representationArray = getArrayNode(jsonNode, "representation", true);
        if (representationArray != null) {
            ArrayNode _representationArray = getArrayNode(jsonNode, "_representation");
            for (int i = 0; i < representationArray.size(); i++) {
                builder.representation((PropertyRepresentation) parseString(PropertyRepresentation.builder(), "representation", representationArray.get(i), getJsonNode(_representationArray, i), i));
            }
        }
        builder.sliceName(parseString("sliceName", getJsonNode(jsonNode, "sliceName", TextNode.class), jsonNode.get("_sliceName"), -1));
        builder.sliceIsConstraining(parseBoolean("sliceIsConstraining", getJsonNode(jsonNode, "sliceIsConstraining", JsonNode.class), jsonNode.get("_sliceIsConstraining"), -1));
        builder.label(parseString("label", getJsonNode(jsonNode, "label", TextNode.class), jsonNode.get("_label"), -1));
        ArrayNode codeArray = getArrayNode(jsonNode, "code");
        if (codeArray != null) {
            for (int i = 0; i < codeArray.size(); i++) {
                if (codeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + codeArray.get(i).getNodeType() + " for element: code");
                }
                builder.code(parseCoding("code", codeArray.get(i), i));
            }
        }
        builder.slicing(parseElementDefinitionSlicing("slicing", getJsonNode(jsonNode, "slicing", JsonNode.class), -1));
        builder._short(parseString("short", getJsonNode(jsonNode, "short", TextNode.class), jsonNode.get("_short"), -1));
        builder.definition((Markdown) parseString(Markdown.builder(), "definition", getJsonNode(jsonNode, "definition", TextNode.class), jsonNode.get("_definition"), -1));
        builder.comment((Markdown) parseString(Markdown.builder(), "comment", getJsonNode(jsonNode, "comment", TextNode.class), jsonNode.get("_comment"), -1));
        builder.requirements((Markdown) parseString(Markdown.builder(), "requirements", getJsonNode(jsonNode, "requirements", TextNode.class), jsonNode.get("_requirements"), -1));
        ArrayNode aliasArray = getArrayNode(jsonNode, "alias", true);
        if (aliasArray != null) {
            ArrayNode _aliasArray = getArrayNode(jsonNode, "_alias");
            for (int i = 0; i < aliasArray.size(); i++) {
                builder.alias(parseString("alias", aliasArray.get(i), getJsonNode(_aliasArray, i), i));
            }
        }
        builder.min((UnsignedInt) parseInteger(UnsignedInt.builder(), "min", getJsonNode(jsonNode, "min", NumericNode.class), jsonNode.get("_min"), -1));
        builder.max(parseString("max", getJsonNode(jsonNode, "max", TextNode.class), jsonNode.get("_max"), -1));
        builder.base(parseElementDefinitionBase("base", getJsonNode(jsonNode, "base", JsonNode.class), -1));
        builder.contentReference(parseUri("contentReference", getJsonNode(jsonNode, "contentReference", TextNode.class), jsonNode.get("_contentReference"), -1));
        ArrayNode typeArray = getArrayNode(jsonNode, "type");
        if (typeArray != null) {
            for (int i = 0; i < typeArray.size(); i++) {
                if (typeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + typeArray.get(i).getNodeType() + " for element: type");
                }
                builder.type(parseElementDefinitionType("type", typeArray.get(i), i));
            }
        }
        builder.defaultValue(parseChoiceElement("defaultValue", jsonNode, Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class));
        builder.meaningWhenMissing((Markdown) parseString(Markdown.builder(), "meaningWhenMissing", getJsonNode(jsonNode, "meaningWhenMissing", TextNode.class), jsonNode.get("_meaningWhenMissing"), -1));
        builder.orderMeaning(parseString("orderMeaning", getJsonNode(jsonNode, "orderMeaning", TextNode.class), jsonNode.get("_orderMeaning"), -1));
        builder.fixed(parseChoiceElement("fixed", jsonNode, Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class));
        builder.pattern(parseChoiceElement("pattern", jsonNode, Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class));
        ArrayNode exampleArray = getArrayNode(jsonNode, "example");
        if (exampleArray != null) {
            for (int i = 0; i < exampleArray.size(); i++) {
                if (exampleArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + exampleArray.get(i).getNodeType() + " for element: example");
                }
                builder.example(parseElementDefinitionExample("example", exampleArray.get(i), i));
            }
        }
        builder.minValue(parseChoiceElement("minValue", jsonNode, Date.class, DateTime.class, Instant.class, Time.class, Decimal.class, Integer.class, Integer64.class, PositiveInt.class, UnsignedInt.class, Quantity.class));
        builder.maxValue(parseChoiceElement("maxValue", jsonNode, Date.class, DateTime.class, Instant.class, Time.class, Decimal.class, Integer.class, Integer64.class, PositiveInt.class, UnsignedInt.class, Quantity.class));
        builder.maxLength(parseInteger("maxLength", getJsonNode(jsonNode, "maxLength", NumericNode.class), jsonNode.get("_maxLength"), -1));
        ArrayNode conditionArray = getArrayNode(jsonNode, "condition", true);
        if (conditionArray != null) {
            ArrayNode _conditionArray = getArrayNode(jsonNode, "_condition");
            for (int i = 0; i < conditionArray.size(); i++) {
                builder.condition((Id) parseString(Id.builder(), "condition", conditionArray.get(i), getJsonNode(_conditionArray, i), i));
            }
        }
        ArrayNode constraintArray = getArrayNode(jsonNode, "constraint");
        if (constraintArray != null) {
            for (int i = 0; i < constraintArray.size(); i++) {
                if (constraintArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + constraintArray.get(i).getNodeType() + " for element: constraint");
                }
                builder.constraint(parseElementDefinitionConstraint("constraint", constraintArray.get(i), i));
            }
        }
        builder.mustHaveValue(parseBoolean("mustHaveValue", getJsonNode(jsonNode, "mustHaveValue", JsonNode.class), jsonNode.get("_mustHaveValue"), -1));
        ArrayNode valueAlternativesArray = getArrayNode(jsonNode, "valueAlternatives", true);
        if (valueAlternativesArray != null) {
            ArrayNode _valueAlternativesArray = getArrayNode(jsonNode, "_valueAlternatives");
            for (int i = 0; i < valueAlternativesArray.size(); i++) {
                builder.valueAlternatives((Canonical) parseUri(Canonical.builder(), "valueAlternatives", valueAlternativesArray.get(i), getJsonNode(_valueAlternativesArray, i), i));
            }
        }
        builder.mustSupport(parseBoolean("mustSupport", getJsonNode(jsonNode, "mustSupport", JsonNode.class), jsonNode.get("_mustSupport"), -1));
        builder.isModifier(parseBoolean("isModifier", getJsonNode(jsonNode, "isModifier", JsonNode.class), jsonNode.get("_isModifier"), -1));
        builder.isModifierReason(parseString("isModifierReason", getJsonNode(jsonNode, "isModifierReason", TextNode.class), jsonNode.get("_isModifierReason"), -1));
        builder.isSummary(parseBoolean("isSummary", getJsonNode(jsonNode, "isSummary", JsonNode.class), jsonNode.get("_isSummary"), -1));
        builder.binding(parseElementDefinitionBinding("binding", getJsonNode(jsonNode, "binding", JsonNode.class), -1));
        ArrayNode mappingArray = getArrayNode(jsonNode, "mapping");
        if (mappingArray != null) {
            for (int i = 0; i < mappingArray.size(); i++) {
                if (mappingArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + mappingArray.get(i).getNodeType() + " for element: mapping");
                }
                builder.mapping(parseElementDefinitionMapping("mapping", mappingArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ElementDefinition.Base parseElementDefinitionBase(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Base.class, jsonNode);
        }
        ElementDefinition.Base.Builder builder = ElementDefinition.Base.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.path(parseString("path", getJsonNode(jsonNode, "path", TextNode.class), jsonNode.get("_path"), -1));
        builder.min((UnsignedInt) parseInteger(UnsignedInt.builder(), "min", getJsonNode(jsonNode, "min", NumericNode.class), jsonNode.get("_min"), -1));
        builder.max(parseString("max", getJsonNode(jsonNode, "max", TextNode.class), jsonNode.get("_max"), -1));
        stackPop();
        return builder.build();
    }

    private ElementDefinition.Binding parseElementDefinitionBinding(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Binding.class, jsonNode);
        }
        ElementDefinition.Binding.Builder builder = ElementDefinition.Binding.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.strength((BindingStrength) parseString(BindingStrength.builder(), "strength", getJsonNode(jsonNode, "strength", TextNode.class), jsonNode.get("_strength"), -1));
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        ArrayNode additionalArray = getArrayNode(jsonNode, "additional");
        if (additionalArray != null) {
            for (int i = 0; i < additionalArray.size(); i++) {
                if (additionalArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + additionalArray.get(i).getNodeType() + " for element: additional");
                }
                builder.additional(parseElement("additional", additionalArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

	private ElementDefinition.Constraint parseElementDefinitionConstraint(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Constraint.class, jsonNode);
        }
        ElementDefinition.Constraint.Builder builder = ElementDefinition.Constraint.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.key((Id) parseString(Id.builder(), "key", getJsonNode(jsonNode, "key", TextNode.class), jsonNode.get("_key"), -1));
        builder.requirements((Markdown) parseString(Markdown.builder(), "requirements", getJsonNode(jsonNode, "requirements", TextNode.class), jsonNode.get("_requirements"), -1));
        builder.severity((ConstraintSeverity) parseString(ConstraintSeverity.builder(), "severity", getJsonNode(jsonNode, "severity", TextNode.class), jsonNode.get("_severity"), -1));
        builder.suppress(parseBoolean("suppress", getJsonNode(jsonNode, "suppress", JsonNode.class), jsonNode.get("_suppress"), -1));
        builder.human(parseString("human", getJsonNode(jsonNode, "human", TextNode.class), jsonNode.get("_human"), -1));
        builder.expression(parseString("expression", getJsonNode(jsonNode, "expression", TextNode.class), jsonNode.get("_expression"), -1));
        builder.source((Canonical) parseUri(Canonical.builder(), "source", getJsonNode(jsonNode, "source", TextNode.class), jsonNode.get("_source"), -1));
        stackPop();
        return builder.build();
    }

    private ElementDefinition.Example parseElementDefinitionExample(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Example.class, jsonNode);
        }
        ElementDefinition.Example.Builder builder = ElementDefinition.Example.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.label(parseString("label", getJsonNode(jsonNode, "label", TextNode.class), jsonNode.get("_label"), -1));
        builder.value(parseChoiceElement("value", jsonNode, Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class));
        stackPop();
        return builder.build();
    }

    private ElementDefinition.Mapping parseElementDefinitionMapping(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Mapping.class, jsonNode);
        }
        ElementDefinition.Mapping.Builder builder = ElementDefinition.Mapping.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.identity((Id) parseString(Id.builder(), "identity", getJsonNode(jsonNode, "identity", TextNode.class), jsonNode.get("_identity"), -1));
        builder.language((Code) parseString(Code.builder(), "language", getJsonNode(jsonNode, "language", TextNode.class), jsonNode.get("_language"), -1));
        builder.map(parseString("map", getJsonNode(jsonNode, "map", TextNode.class), jsonNode.get("_map"), -1));
        builder.comment((Markdown) parseString(Markdown.builder(), "comment", getJsonNode(jsonNode, "comment", TextNode.class), jsonNode.get("_comment"), -1));
        stackPop();
        return builder.build();
    }

    private ElementDefinition.Slicing parseElementDefinitionSlicing(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Slicing.class, jsonNode);
        }
        ElementDefinition.Slicing.Builder builder = ElementDefinition.Slicing.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        ArrayNode discriminatorArray = getArrayNode(jsonNode, "discriminator");
        if (discriminatorArray != null) {
            for (int i = 0; i < discriminatorArray.size(); i++) {
                if (discriminatorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + discriminatorArray.get(i).getNodeType() + " for element: discriminator");
                }
                builder.discriminator(parseElementDefinitionSlicingDiscriminator("discriminator", discriminatorArray.get(i), i));
            }
        }
        builder.description(parseString("description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.ordered(parseBoolean("ordered", getJsonNode(jsonNode, "ordered", JsonNode.class), jsonNode.get("_ordered"), -1));
        builder.rules((SlicingRules) parseString(SlicingRules.builder(), "rules", getJsonNode(jsonNode, "rules", TextNode.class), jsonNode.get("_rules"), -1));
        stackPop();
        return builder.build();
    }

    private ElementDefinition.Slicing.Discriminator parseElementDefinitionSlicingDiscriminator(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Slicing.Discriminator.class, jsonNode);
        }
        ElementDefinition.Slicing.Discriminator.Builder builder = ElementDefinition.Slicing.Discriminator.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.type((DiscriminatorType) parseString(DiscriminatorType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.path(parseString("path", getJsonNode(jsonNode, "path", TextNode.class), jsonNode.get("_path"), -1));
        stackPop();
        return builder.build();
    }

    private ElementDefinition.Type parseElementDefinitionType(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ElementDefinition.Type.class, jsonNode);
        }
        ElementDefinition.Type.Builder builder = ElementDefinition.Type.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code(parseUri("code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        ArrayNode profileArray = getArrayNode(jsonNode, "profile", true);
        if (profileArray != null) {
            ArrayNode _profileArray = getArrayNode(jsonNode, "_profile");
            for (int i = 0; i < profileArray.size(); i++) {
                builder.profile((Canonical) parseUri(Canonical.builder(), "profile", profileArray.get(i), getJsonNode(_profileArray, i), i));
            }
        }
        ArrayNode targetProfileArray = getArrayNode(jsonNode, "targetProfile", true);
        if (targetProfileArray != null) {
            ArrayNode _targetProfileArray = getArrayNode(jsonNode, "_targetProfile");
            for (int i = 0; i < targetProfileArray.size(); i++) {
                builder.targetProfile((Canonical) parseUri(Canonical.builder(), "targetProfile", targetProfileArray.get(i), getJsonNode(_targetProfileArray, i), i));
            }
        }
        ArrayNode aggregationArray = getArrayNode(jsonNode, "aggregation", true);
        if (aggregationArray != null) {
            ArrayNode _aggregationArray = getArrayNode(jsonNode, "_aggregation");
            for (int i = 0; i < aggregationArray.size(); i++) {
                builder.aggregation((AggregationMode) parseString(AggregationMode.builder(), "aggregation", aggregationArray.get(i), getJsonNode(_aggregationArray, i), i));
            }
        }
        builder.versioning((ReferenceVersionRules) parseString(ReferenceVersionRules.builder(), "versioning", getJsonNode(jsonNode, "versioning", TextNode.class), jsonNode.get("_versioning"), -1));
        stackPop();
        return builder.build();
    }

    private Expression parseExpression(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Expression.class, jsonNode);
        }
        Expression.Builder builder = Expression.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.description(parseString("description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.name((Code) parseString(Code.builder(), "name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.language((Code) parseString(Code.builder(), "language", getJsonNode(jsonNode, "language", TextNode.class), jsonNode.get("_language"), -1));
        builder.expression(parseString("expression", getJsonNode(jsonNode, "expression", TextNode.class), jsonNode.get("_expression"), -1));
        builder.reference(parseUri("reference", getJsonNode(jsonNode, "reference", TextNode.class), jsonNode.get("_reference"), -1));
        stackPop();
        return builder.build();
    }

    private ExtendedContactDetail parseExtendedContactDetail(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ExtendedContactDetail.class, jsonNode);
        }
        ExtendedContactDetail.Builder builder = ExtendedContactDetail.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.purpose(parseCodeableConcept("purpose", getJsonNode(jsonNode, "purpose", JsonNode.class), -1));
        ArrayNode nameArray = getArrayNode(jsonNode, "name");
        if (nameArray != null) {
            for (int i = 0; i < nameArray.size(); i++) {
                if (nameArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + nameArray.get(i).getNodeType() + " for element: name");
                }
                builder.name(parseHumanName("name", nameArray.get(i), i));
            }
        }
        ArrayNode telecomArray = getArrayNode(jsonNode, "telecom");
        if (telecomArray != null) {
            for (int i = 0; i < telecomArray.size(); i++) {
                if (telecomArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + telecomArray.get(i).getNodeType() + " for element: telecom");
                }
                builder.telecom(parseContactPoint("telecom", telecomArray.get(i), i));
            }
        }
        builder.address(parseAddress("address", getJsonNode(jsonNode, "address", JsonNode.class), -1));
        builder.organization(parseReference("organization", getJsonNode(jsonNode, "organization", JsonNode.class), -1));
        builder.period(parsePeriod("period", getJsonNode(jsonNode, "period", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Extension parseExtension(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Extension.class, jsonNode);
        }
        Extension.Builder builder = Extension.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.url(parseJavaString("url", getJsonNode(jsonNode, "url", TextNode.class), -1));
        builder.value(parseChoiceElement("value", jsonNode, Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class));
        stackPop();
        return builder.build();
    }

    private HumanName parseHumanName(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(HumanName.class, jsonNode);
        }
        HumanName.Builder builder = HumanName.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.use((NameUse) parseString(NameUse.builder(), "use", getJsonNode(jsonNode, "use", TextNode.class), jsonNode.get("_use"), -1));
        builder.text(parseString("text", getJsonNode(jsonNode, "text", TextNode.class), jsonNode.get("_text"), -1));
        builder.family(parseString("family", getJsonNode(jsonNode, "family", TextNode.class), jsonNode.get("_family"), -1));
        ArrayNode givenArray = getArrayNode(jsonNode, "given", true);
        if (givenArray != null) {
            ArrayNode _givenArray = getArrayNode(jsonNode, "_given");
            for (int i = 0; i < givenArray.size(); i++) {
                builder.given(parseString("given", givenArray.get(i), getJsonNode(_givenArray, i), i));
            }
        }
        ArrayNode prefixArray = getArrayNode(jsonNode, "prefix", true);
        if (prefixArray != null) {
            ArrayNode _prefixArray = getArrayNode(jsonNode, "_prefix");
            for (int i = 0; i < prefixArray.size(); i++) {
                builder.prefix(parseString("prefix", prefixArray.get(i), getJsonNode(_prefixArray, i), i));
            }
        }
        ArrayNode suffixArray = getArrayNode(jsonNode, "suffix", true);
        if (suffixArray != null) {
            ArrayNode _suffixArray = getArrayNode(jsonNode, "_suffix");
            for (int i = 0; i < suffixArray.size(); i++) {
                builder.suffix(parseString("suffix", suffixArray.get(i), getJsonNode(_suffixArray, i), i));
            }
        }
        builder.period(parsePeriod("period", getJsonNode(jsonNode, "period", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Identifier parseIdentifier(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Identifier.class, jsonNode);
        }
        Identifier.Builder builder = Identifier.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.use((IdentifierUse) parseString(IdentifierUse.builder(), "use", getJsonNode(jsonNode, "use", TextNode.class), jsonNode.get("_use"), -1));
        builder.type(parseCodeableConcept("type", getJsonNode(jsonNode, "type", JsonNode.class), -1));
        builder.system(parseUri("system", getJsonNode(jsonNode, "system", TextNode.class), jsonNode.get("_system"), -1));
        builder.value(parseString("value", getJsonNode(jsonNode, "value", TextNode.class), jsonNode.get("_value"), -1));
        builder.period(parsePeriod("period", getJsonNode(jsonNode, "period", JsonNode.class), -1));
        builder.assigner(parseReference("assigner", getJsonNode(jsonNode, "assigner", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Instant parseInstant(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Instant.Builder builder = Instant.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Integer parseInteger(Integer.Builder builder, java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.NUMBER) {
            NumericNode jsonNumber = (NumericNode) jsonNode;
            // XXX: This rounds fractions instead of report an error for non-integer input
            builder.value(jsonNumber.intValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: NUMBER but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Integer parseInteger(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        return parseInteger(Integer.builder(), elementName, jsonNode, _jsonNode, elementIndex);
    }

    private Integer64 parseInteger64(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Integer64.Builder builder = Integer64.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        stackPop();
        return builder.build();
    }

    private MarketingStatus parseMarketingStatus(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(MarketingStatus.class, jsonNode);
        }
        MarketingStatus.Builder builder = MarketingStatus.builder();
        builder.setValidating(validating);
        parseBackboneType(builder, jsonNode);
        builder.country(parseCodeableConcept("country", getJsonNode(jsonNode, "country", JsonNode.class), -1));
        builder.jurisdiction(parseCodeableConcept("jurisdiction", getJsonNode(jsonNode, "jurisdiction", JsonNode.class), -1));
        builder.status(parseCodeableConcept("status", getJsonNode(jsonNode, "status", JsonNode.class), -1));
        builder.dateRange(parsePeriod("dateRange", getJsonNode(jsonNode, "dateRange", JsonNode.class), -1));
        builder.restoreDate(parseDateTime("restoreDate", getJsonNode(jsonNode, "restoreDate", TextNode.class), jsonNode.get("_restoreDate"), -1));
        stackPop();
        return builder.build();
    }

    private Meta parseMeta(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Meta.class, jsonNode);
        }
        Meta.Builder builder = Meta.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.versionId((Id) parseString(Id.builder(), "versionId", getJsonNode(jsonNode, "versionId", TextNode.class), jsonNode.get("_versionId"), -1));
        builder.lastUpdated(parseInstant("lastUpdated", getJsonNode(jsonNode, "lastUpdated", TextNode.class), jsonNode.get("_lastUpdated"), -1));
        builder.source(parseUri("source", getJsonNode(jsonNode, "source", TextNode.class), jsonNode.get("_source"), -1));
        ArrayNode profileArray = getArrayNode(jsonNode, "profile", true);
        if (profileArray != null) {
            ArrayNode _profileArray = getArrayNode(jsonNode, "_profile");
            for (int i = 0; i < profileArray.size(); i++) {
                builder.profile((Canonical) parseUri(Canonical.builder(), "profile", profileArray.get(i), getJsonNode(_profileArray, i), i));
            }
        }
        ArrayNode securityArray = getArrayNode(jsonNode, "security");
        if (securityArray != null) {
            for (int i = 0; i < securityArray.size(); i++) {
                if (securityArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + securityArray.get(i).getNodeType() + " for element: security");
                }
                builder.security(parseCoding("security", securityArray.get(i), i));
            }
        }
        ArrayNode tagArray = getArrayNode(jsonNode, "tag");
        if (tagArray != null) {
            for (int i = 0; i < tagArray.size(); i++) {
                if (tagArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + tagArray.get(i).getNodeType() + " for element: tag");
                }
                builder.tag(parseCoding("tag", tagArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private void parseMetadataResource(MetadataResource.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.approvalDate(parseDate("approvalDate", getJsonNode(jsonNode, "approvalDate", TextNode.class), jsonNode.get("_approvalDate"), -1));
        builder.lastReviewDate(parseDate("lastReviewDate", getJsonNode(jsonNode, "lastReviewDate", TextNode.class), jsonNode.get("_lastReviewDate"), -1));
        builder.effectivePeriod(parsePeriod("effectivePeriod", getJsonNode(jsonNode, "effectivePeriod", JsonNode.class), -1));
        ArrayNode topicArray = getArrayNode(jsonNode, "topic");
        if (topicArray != null) {
            for (int i = 0; i < topicArray.size(); i++) {
                if (topicArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + topicArray.get(i).getNodeType() + " for element: topic");
                }
                builder.topic(parseCodeableConcept("topic", topicArray.get(i), i));
            }
        }
        ArrayNode authorArray = getArrayNode(jsonNode, "author");
        if (authorArray != null) {
            for (int i = 0; i < authorArray.size(); i++) {
                if (authorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + authorArray.get(i).getNodeType() + " for element: author");
                }
                builder.author(parseContactDetail("author", authorArray.get(i), i));
            }
        }
        ArrayNode editorArray = getArrayNode(jsonNode, "editor");
        if (editorArray != null) {
            for (int i = 0; i < editorArray.size(); i++) {
                if (editorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + editorArray.get(i).getNodeType() + " for element: editor");
                }
                builder.editor(parseContactDetail("editor", editorArray.get(i), i));
            }
        }
        ArrayNode reviewerArray = getArrayNode(jsonNode, "reviewer");
        if (reviewerArray != null) {
            for (int i = 0; i < reviewerArray.size(); i++) {
                if (reviewerArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + reviewerArray.get(i).getNodeType() + " for element: reviewer");
                }
                builder.reviewer(parseContactDetail("reviewer", reviewerArray.get(i), i));
            }
        }
        ArrayNode endorserArray = getArrayNode(jsonNode, "endorser");
        if (endorserArray != null) {
            for (int i = 0; i < endorserArray.size(); i++) {
                if (endorserArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + endorserArray.get(i).getNodeType() + " for element: endorser");
                }
                builder.endorser(parseContactDetail("endorser", endorserArray.get(i), i));
            }
        }
        ArrayNode relatedArtifactArray = getArrayNode(jsonNode, "relatedArtifact");
        if (relatedArtifactArray != null) {
            for (int i = 0; i < relatedArtifactArray.size(); i++) {
                if (relatedArtifactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + relatedArtifactArray.get(i).getNodeType() + " for element: relatedArtifact");
                }
                builder.relatedArtifact(parseRelatedArtifact("relatedArtifact", relatedArtifactArray.get(i), i));
            }
        }
    }

    private MonetaryComponent parseMonetaryComponent(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(MonetaryComponent.class, jsonNode);
        }
        MonetaryComponent.Builder builder = MonetaryComponent.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.type((PriceComponentType) parseString(PriceComponentType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.code(parseCodeableConcept("code", getJsonNode(jsonNode, "code", JsonNode.class), -1));
        builder.factor(parseDecimal("factor", getJsonNode(jsonNode, "factor", NumericNode.class), jsonNode.get("_factor"), -1));
        builder.amount(parseMoney("amount", getJsonNode(jsonNode, "amount", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Money parseMoney(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Money.class, jsonNode);
        }
        Money.Builder builder = Money.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.value(parseDecimal("value", getJsonNode(jsonNode, "value", NumericNode.class), jsonNode.get("_value"), -1));
        builder.currency((Code) parseString(Code.builder(), "currency", getJsonNode(jsonNode, "currency", TextNode.class), jsonNode.get("_currency"), -1));
        stackPop();
        return builder.build();
    }

    private Narrative parseNarrative(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Narrative.class, jsonNode);
        }
        Narrative.Builder builder = Narrative.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.status((NarrativeStatus) parseString(NarrativeStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.div(parseXhtml("div", getJsonNode(jsonNode, "div", TextNode.class), jsonNode.get("_div"), -1));
        stackPop();
        return builder.build();
    }

    private OperationDefinition parseOperationDefinition(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(OperationDefinition.class, jsonNode);
        }
        OperationDefinition.Builder builder = OperationDefinition.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.kind((OperationKind) parseString(OperationKind.builder(), "kind", getJsonNode(jsonNode, "kind", TextNode.class), jsonNode.get("_kind"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
        builder.affectsState(parseBoolean("affectsState", getJsonNode(jsonNode, "affectsState", JsonNode.class), jsonNode.get("_affectsState"), -1));
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.comment((Markdown) parseString(Markdown.builder(), "comment", getJsonNode(jsonNode, "comment", TextNode.class), jsonNode.get("_comment"), -1));
        builder.base((Canonical) parseUri(Canonical.builder(), "base", getJsonNode(jsonNode, "base", TextNode.class), jsonNode.get("_base"), -1));
        ArrayNode resourceArray = getArrayNode(jsonNode, "resource", true);
        if (resourceArray != null) {
            ArrayNode _resourceArray = getArrayNode(jsonNode, "_resource");
            for (int i = 0; i < resourceArray.size(); i++) {
                builder.resource((FHIRTypes) parseString(FHIRTypes.builder(), "resource", resourceArray.get(i), getJsonNode(_resourceArray, i), i));
            }
        }
        builder.system(parseBoolean("system", getJsonNode(jsonNode, "system", JsonNode.class), jsonNode.get("_system"), -1));
        builder.type(parseBoolean("type", getJsonNode(jsonNode, "type", JsonNode.class), jsonNode.get("_type"), -1));
        builder.instance(parseBoolean("instance", getJsonNode(jsonNode, "instance", JsonNode.class), jsonNode.get("_instance"), -1));
        builder.inputProfile((Canonical) parseUri(Canonical.builder(), "inputProfile", getJsonNode(jsonNode, "inputProfile", TextNode.class), jsonNode.get("_inputProfile"), -1));
        builder.outputProfile((Canonical) parseUri(Canonical.builder(), "outputProfile", getJsonNode(jsonNode, "outputProfile", TextNode.class), jsonNode.get("_outputProfile"), -1));
        ArrayNode parameterArray = getArrayNode(jsonNode, "parameter");
        if (parameterArray != null) {
            for (int i = 0; i < parameterArray.size(); i++) {
                if (parameterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + parameterArray.get(i).getNodeType() + " for element: parameter");
                }
                builder.parameter(parseOperationDefinitionParameter("parameter", parameterArray.get(i), i));
            }
        }
        ArrayNode overloadArray = getArrayNode(jsonNode, "overload");
        if (overloadArray != null) {
            for (int i = 0; i < overloadArray.size(); i++) {
                if (overloadArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + overloadArray.get(i).getNodeType() + " for element: overload");
                }
                builder.overload(parseOperationDefinitionOverload("overload", overloadArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private OperationDefinition.Overload parseOperationDefinitionOverload(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(OperationDefinition.Overload.class, jsonNode);
        }
        OperationDefinition.Overload.Builder builder = OperationDefinition.Overload.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        ArrayNode parameterNameArray = getArrayNode(jsonNode, "parameterName", true);
        if (parameterNameArray != null) {
            ArrayNode _parameterNameArray = getArrayNode(jsonNode, "_parameterName");
            for (int i = 0; i < parameterNameArray.size(); i++) {
                builder.parameterName(parseString("parameterName", parameterNameArray.get(i), getJsonNode(_parameterNameArray, i), i));
            }
        }
        builder.comment(parseString("comment", getJsonNode(jsonNode, "comment", TextNode.class), jsonNode.get("_comment"), -1));
        stackPop();
        return builder.build();
    }

    private OperationDefinition.Parameter parseOperationDefinitionParameter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(OperationDefinition.Parameter.class, jsonNode);
        }
        OperationDefinition.Parameter.Builder builder = OperationDefinition.Parameter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name((Code) parseString(Code.builder(), "name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.use((OperationParameterUse) parseString(OperationParameterUse.builder(), "use", getJsonNode(jsonNode, "use", TextNode.class), jsonNode.get("_use"), -1));
        ArrayNode scopeArray = getArrayNode(jsonNode, "scope", true);
        if (scopeArray != null) {
            ArrayNode _scopeArray = getArrayNode(jsonNode, "_scope");
            for (int i = 0; i < scopeArray.size(); i++) {
                builder.scope((OperationParameterScope) parseString(OperationParameterScope.builder(), "scope", scopeArray.get(i), getJsonNode(_scopeArray, i), i));
            }
        }
        builder.min(parseInteger("min", getJsonNode(jsonNode, "min", NumericNode.class), jsonNode.get("_min"), -1));
        builder.max(parseString("max", getJsonNode(jsonNode, "max", TextNode.class), jsonNode.get("_max"), -1));
        builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        builder.type((FHIRAllTypes) parseString(FHIRAllTypes.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        ArrayNode allowedTypeArray = getArrayNode(jsonNode, "allowedType", true);
        if (allowedTypeArray != null) {
            ArrayNode _allowedTypeArray = getArrayNode(jsonNode, "_allowedType");
            for (int i = 0; i < allowedTypeArray.size(); i++) {
                builder.allowedType((FHIRAllTypes) parseString(FHIRAllTypes.builder(), "allowedType", allowedTypeArray.get(i), getJsonNode(_allowedTypeArray, i), i));
            }
        }
        ArrayNode targetProfileArray = getArrayNode(jsonNode, "targetProfile", true);
        if (targetProfileArray != null) {
            ArrayNode _targetProfileArray = getArrayNode(jsonNode, "_targetProfile");
            for (int i = 0; i < targetProfileArray.size(); i++) {
                builder.targetProfile((Canonical) parseUri(Canonical.builder(), "targetProfile", targetProfileArray.get(i), getJsonNode(_targetProfileArray, i), i));
            }
        }
        builder.searchType((SearchParamType) parseString(SearchParamType.builder(), "searchType", getJsonNode(jsonNode, "searchType", TextNode.class), jsonNode.get("_searchType"), -1));
        builder.binding(parseOperationDefinitionParameterBinding("binding", getJsonNode(jsonNode, "binding", JsonNode.class), -1));
        ArrayNode referencedFromArray = getArrayNode(jsonNode, "referencedFrom");
        if (referencedFromArray != null) {
            for (int i = 0; i < referencedFromArray.size(); i++) {
                if (referencedFromArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + referencedFromArray.get(i).getNodeType() + " for element: referencedFrom");
                }
                builder.referencedFrom(parseOperationDefinitionParameterReferencedFrom("referencedFrom", referencedFromArray.get(i), i));
            }
        }
        ArrayNode partArray = getArrayNode(jsonNode, "part");
        if (partArray != null) {
            for (int i = 0; i < partArray.size(); i++) {
                if (partArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + partArray.get(i).getNodeType() + " for element: part");
                }
                builder.part(parseOperationDefinitionParameter("part", partArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private OperationDefinition.Parameter.Binding parseOperationDefinitionParameterBinding(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(OperationDefinition.Parameter.Binding.class, jsonNode);
        }
        OperationDefinition.Parameter.Binding.Builder builder = OperationDefinition.Parameter.Binding.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.strength((BindingStrength) parseString(BindingStrength.builder(), "strength", getJsonNode(jsonNode, "strength", TextNode.class), jsonNode.get("_strength"), -1));
        builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", getJsonNode(jsonNode, "valueSet", TextNode.class), jsonNode.get("_valueSet"), -1));
        stackPop();
        return builder.build();
    }

    private OperationDefinition.Parameter.ReferencedFrom parseOperationDefinitionParameterReferencedFrom(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(OperationDefinition.Parameter.ReferencedFrom.class, jsonNode);
        }
        OperationDefinition.Parameter.ReferencedFrom.Builder builder = OperationDefinition.Parameter.ReferencedFrom.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.source(parseString("source", getJsonNode(jsonNode, "source", TextNode.class), jsonNode.get("_source"), -1));
        builder.sourceId(parseString("sourceId", getJsonNode(jsonNode, "sourceId", TextNode.class), jsonNode.get("_sourceId"), -1));
        stackPop();
        return builder.build();
    }

    private OperationOutcome parseOperationOutcome(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(OperationOutcome.class, jsonNode);
        }
        OperationOutcome.Builder builder = OperationOutcome.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        ArrayNode issueArray = getArrayNode(jsonNode, "issue");
        if (issueArray != null) {
            for (int i = 0; i < issueArray.size(); i++) {
                if (issueArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + issueArray.get(i).getNodeType() + " for element: issue");
                }
                builder.issue(parseOperationOutcomeIssue("issue", issueArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private OperationOutcome.Issue parseOperationOutcomeIssue(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(OperationOutcome.Issue.class, jsonNode);
        }
        OperationOutcome.Issue.Builder builder = OperationOutcome.Issue.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.severity((IssueSeverity) parseString(IssueSeverity.builder(), "severity", getJsonNode(jsonNode, "severity", TextNode.class), jsonNode.get("_severity"), -1));
        builder.code((IssueType) parseString(IssueType.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.details(parseCodeableConcept("details", getJsonNode(jsonNode, "details", JsonNode.class), -1));
        builder.diagnostics(parseString("diagnostics", getJsonNode(jsonNode, "diagnostics", TextNode.class), jsonNode.get("_diagnostics"), -1));
        ArrayNode locationArray = getArrayNode(jsonNode, "location", true);
        if (locationArray != null) {
            ArrayNode _locationArray = getArrayNode(jsonNode, "_location");
            for (int i = 0; i < locationArray.size(); i++) {
                builder.location(parseString("location", locationArray.get(i), getJsonNode(_locationArray, i), i));
            }
        }
        ArrayNode expressionArray = getArrayNode(jsonNode, "expression", true);
        if (expressionArray != null) {
            ArrayNode _expressionArray = getArrayNode(jsonNode, "_expression");
            for (int i = 0; i < expressionArray.size(); i++) {
                builder.expression(parseString("expression", expressionArray.get(i), getJsonNode(_expressionArray, i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ParameterDefinition parseParameterDefinition(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ParameterDefinition.class, jsonNode);
        }
        ParameterDefinition.Builder builder = ParameterDefinition.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.name((Code) parseString(Code.builder(), "name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.use((ParameterUse) parseString(ParameterUse.builder(), "use", getJsonNode(jsonNode, "use", TextNode.class), jsonNode.get("_use"), -1));
        builder.min(parseInteger("min", getJsonNode(jsonNode, "min", NumericNode.class), jsonNode.get("_min"), -1));
        builder.max(parseString("max", getJsonNode(jsonNode, "max", TextNode.class), jsonNode.get("_max"), -1));
        builder.documentation(parseString("documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        builder.type((FHIRTypes) parseString(FHIRTypes.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.profile((Canonical) parseUri(Canonical.builder(), "profile", getJsonNode(jsonNode, "profile", TextNode.class), jsonNode.get("_profile"), -1));
        stackPop();
        return builder.build();
    }

    private Parameters parseParameters(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Parameters.class, jsonNode);
        }
        Parameters.Builder builder = Parameters.builder();
        builder.setValidating(validating);
        parseResource(builder, jsonNode);
        ArrayNode parameterArray = getArrayNode(jsonNode, "parameter");
        if (parameterArray != null) {
            for (int i = 0; i < parameterArray.size(); i++) {
                if (parameterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + parameterArray.get(i).getNodeType() + " for element: parameter");
                }
                builder.parameter(parseParametersParameter("parameter", parameterArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private Parameters.Parameter parseParametersParameter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Parameters.Parameter.class, jsonNode);
        }
        Parameters.Parameter.Builder builder = Parameters.Parameter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.value(parseChoiceElement("value", jsonNode, Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class));
        builder.resource(parseResource("resource", getJsonNode(jsonNode, "resource", JsonNode.class), -1));
        ArrayNode partArray = getArrayNode(jsonNode, "part");
        if (partArray != null) {
            for (int i = 0; i < partArray.size(); i++) {
                if (partArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + partArray.get(i).getNodeType() + " for element: part");
                }
                builder.part(parseParametersParameter("part", partArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private Period parsePeriod(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Period.class, jsonNode);
        }
        Period.Builder builder = Period.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.start(parseDateTime("start", getJsonNode(jsonNode, "start", TextNode.class), jsonNode.get("_start"), -1));
        builder.end(parseDateTime("end", getJsonNode(jsonNode, "end", TextNode.class), jsonNode.get("_end"), -1));
        stackPop();
        return builder.build();
    }

    private void parsePrimitiveType(PrimitiveType.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
    }

    private ProductShelfLife parseProductShelfLife(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ProductShelfLife.class, jsonNode);
        }
        ProductShelfLife.Builder builder = ProductShelfLife.builder();
        builder.setValidating(validating);
        parseBackboneType(builder, jsonNode);
        builder.type(parseCodeableConcept("type", getJsonNode(jsonNode, "type", JsonNode.class), -1));
        builder.period(parseChoiceElement("period", jsonNode, Duration.class, String.class));
        ArrayNode specialPrecautionsForStorageArray = getArrayNode(jsonNode, "specialPrecautionsForStorage");
        if (specialPrecautionsForStorageArray != null) {
            for (int i = 0; i < specialPrecautionsForStorageArray.size(); i++) {
                if (specialPrecautionsForStorageArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + specialPrecautionsForStorageArray.get(i).getNodeType() + " for element: specialPrecautionsForStorage");
                }
                builder.specialPrecautionsForStorage(parseCodeableConcept("specialPrecautionsForStorage", specialPrecautionsForStorageArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private Quantity parseQuantity(Quantity.Builder builder, java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Quantity.class, jsonNode);
        }
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.value(parseDecimal("value", getJsonNode(jsonNode, "value", NumericNode.class), jsonNode.get("_value"), -1));
        builder.comparator((QuantityComparator) parseString(QuantityComparator.builder(), "comparator", getJsonNode(jsonNode, "comparator", TextNode.class), jsonNode.get("_comparator"), -1));
        builder.unit(parseString("unit", getJsonNode(jsonNode, "unit", TextNode.class), jsonNode.get("_unit"), -1));
        builder.system(parseUri("system", getJsonNode(jsonNode, "system", TextNode.class), jsonNode.get("_system"), -1));
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        stackPop();
        return builder.build();
    }

    private Quantity parseQuantity(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        return parseQuantity(Quantity.builder(), elementName, jsonNode, elementIndex);
    }

    private Range parseRange(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Range.class, jsonNode);
        }
        Range.Builder builder = Range.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.low((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "low", getJsonNode(jsonNode, "low", JsonNode.class), -1));
        builder.high((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "high", getJsonNode(jsonNode, "high", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Ratio parseRatio(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Ratio.class, jsonNode);
        }
        Ratio.Builder builder = Ratio.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.numerator(parseQuantity("numerator", getJsonNode(jsonNode, "numerator", JsonNode.class), -1));
        builder.denominator((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "denominator", getJsonNode(jsonNode, "denominator", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private RatioRange parseRatioRange(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(RatioRange.class, jsonNode);
        }
        RatioRange.Builder builder = RatioRange.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.lowNumerator((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "lowNumerator", getJsonNode(jsonNode, "lowNumerator", JsonNode.class), -1));
        builder.highNumerator((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "highNumerator", getJsonNode(jsonNode, "highNumerator", JsonNode.class), -1));
        builder.denominator((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "denominator", getJsonNode(jsonNode, "denominator", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Reference parseReference(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Reference.class, jsonNode);
        }
        Reference.Builder builder = Reference.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.reference(parseString("reference", getJsonNode(jsonNode, "reference", TextNode.class), jsonNode.get("_reference"), -1));
        builder.type(parseUri("type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.identifier(parseIdentifier("identifier", getJsonNode(jsonNode, "identifier", JsonNode.class), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        stackPop();
        return builder.build();
    }

    private RelatedArtifact parseRelatedArtifact(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(RelatedArtifact.class, jsonNode);
        }
        RelatedArtifact.Builder builder = RelatedArtifact.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.type((RelatedArtifactType) parseString(RelatedArtifactType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        ArrayNode classifierArray = getArrayNode(jsonNode, "classifier");
        if (classifierArray != null) {
            for (int i = 0; i < classifierArray.size(); i++) {
                if (classifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + classifierArray.get(i).getNodeType() + " for element: classifier");
                }
                builder.classifier(parseCodeableConcept("classifier", classifierArray.get(i), i));
            }
        }
        builder.label(parseString("label", getJsonNode(jsonNode, "label", TextNode.class), jsonNode.get("_label"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        builder.citation((Markdown) parseString(Markdown.builder(), "citation", getJsonNode(jsonNode, "citation", TextNode.class), jsonNode.get("_citation"), -1));
        builder.document(parseAttachment("document", getJsonNode(jsonNode, "document", JsonNode.class), -1));
        builder.resource((Canonical) parseUri(Canonical.builder(), "resource", getJsonNode(jsonNode, "resource", TextNode.class), jsonNode.get("_resource"), -1));
        builder.resourceReference(parseReference("resourceReference", getJsonNode(jsonNode, "resourceReference", JsonNode.class), -1));
        builder.publicationStatus((RelatedArtifactPublicationStatus) parseString(RelatedArtifactPublicationStatus.builder(), "publicationStatus", getJsonNode(jsonNode, "publicationStatus", TextNode.class), jsonNode.get("_publicationStatus"), -1));
        builder.publicationDate(parseDate("publicationDate", getJsonNode(jsonNode, "publicationDate", TextNode.class), jsonNode.get("_publicationDate"), -1));
        stackPop();
        return builder.build();
    }

    private void parseResource(Resource.Builder builder, JsonNode jsonNode) {
        builder.setValidating(validating);
        builder.id(parseJavaString("id", getJsonNode(jsonNode, "id", TextNode.class), -1));
        builder.meta(parseMeta("meta", getJsonNode(jsonNode, "meta", JsonNode.class), -1));
        builder.implicitRules(parseUri("implicitRules", getJsonNode(jsonNode, "implicitRules", TextNode.class), jsonNode.get("_implicitRules"), -1));
        builder.language((Code) parseString(Code.builder(), "language", getJsonNode(jsonNode, "language", TextNode.class), jsonNode.get("_language"), -1));
    }

    private SampledData parseSampledData(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(SampledData.class, jsonNode);
        }
        SampledData.Builder builder = SampledData.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.origin((SimpleQuantity) parseQuantity(SimpleQuantity.builder(), "origin", getJsonNode(jsonNode, "origin", JsonNode.class), -1));
        builder.interval(parseDecimal("interval", getJsonNode(jsonNode, "interval", NumericNode.class), jsonNode.get("_interval"), -1));
        builder.intervalUnit((Code) parseString(Code.builder(), "intervalUnit", getJsonNode(jsonNode, "intervalUnit", TextNode.class), jsonNode.get("_intervalUnit"), -1));
        builder.factor(parseDecimal("factor", getJsonNode(jsonNode, "factor", NumericNode.class), jsonNode.get("_factor"), -1));
        builder.lowerLimit(parseDecimal("lowerLimit", getJsonNode(jsonNode, "lowerLimit", NumericNode.class), jsonNode.get("_lowerLimit"), -1));
        builder.upperLimit(parseDecimal("upperLimit", getJsonNode(jsonNode, "upperLimit", NumericNode.class), jsonNode.get("_upperLimit"), -1));
        builder.dimensions((PositiveInt) parseInteger(PositiveInt.builder(), "dimensions", getJsonNode(jsonNode, "dimensions", NumericNode.class), jsonNode.get("_dimensions"), -1));
        builder.codeMap((Canonical) parseUri(Canonical.builder(), "codeMap", getJsonNode(jsonNode, "codeMap", TextNode.class), jsonNode.get("_codeMap"), -1));
        builder.offsets(parseString("offsets", getJsonNode(jsonNode, "offsets", TextNode.class), jsonNode.get("_offsets"), -1));
        builder.data(parseString("data", getJsonNode(jsonNode, "data", TextNode.class), jsonNode.get("_data"), -1));
        stackPop();
        return builder.build();
    }

    private Signature parseSignature(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Signature.class, jsonNode);
        }
        Signature.Builder builder = Signature.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        ArrayNode typeArray = getArrayNode(jsonNode, "type");
        if (typeArray != null) {
            for (int i = 0; i < typeArray.size(); i++) {
                if (typeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + typeArray.get(i).getNodeType() + " for element: type");
                }
                builder.type(parseCoding("type", typeArray.get(i), i));
            }
        }
        builder.when(parseInstant("when", getJsonNode(jsonNode, "when", TextNode.class), jsonNode.get("_when"), -1));
        builder.who(parseReference("who", getJsonNode(jsonNode, "who", JsonNode.class), -1));
        builder.onBehalfOf(parseReference("onBehalfOf", getJsonNode(jsonNode, "onBehalfOf", JsonNode.class), -1));
        builder.targetFormat((Code) parseString(Code.builder(), "targetFormat", getJsonNode(jsonNode, "targetFormat", TextNode.class), jsonNode.get("_targetFormat"), -1));
        builder.sigFormat((Code) parseString(Code.builder(), "sigFormat", getJsonNode(jsonNode, "sigFormat", TextNode.class), jsonNode.get("_sigFormat"), -1));
        builder.data(parseBase64Binary("data", getJsonNode(jsonNode, "data", TextNode.class), jsonNode.get("_data"), -1));
        stackPop();
        return builder.build();
    }

    private String parseString(String.Builder builder, java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private String parseString(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        return parseString(String.builder(), elementName, jsonNode, _jsonNode, elementIndex);
    }

    private StructureDefinition parseStructureDefinition(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(StructureDefinition.class, jsonNode);
        }
        StructureDefinition.Builder builder = StructureDefinition.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
        ArrayNode keywordArray = getArrayNode(jsonNode, "keyword");
        if (keywordArray != null) {
            for (int i = 0; i < keywordArray.size(); i++) {
                if (keywordArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + keywordArray.get(i).getNodeType() + " for element: keyword");
                }
                builder.keyword(parseCoding("keyword", keywordArray.get(i), i));
            }
        }
        builder.fhirVersion((FHIRVersion) parseString(FHIRVersion.builder(), "fhirVersion", getJsonNode(jsonNode, "fhirVersion", TextNode.class), jsonNode.get("_fhirVersion"), -1));
        ArrayNode mappingArray = getArrayNode(jsonNode, "mapping");
        if (mappingArray != null) {
            for (int i = 0; i < mappingArray.size(); i++) {
                if (mappingArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + mappingArray.get(i).getNodeType() + " for element: mapping");
                }
                builder.mapping(parseStructureDefinitionMapping("mapping", mappingArray.get(i), i));
            }
        }
        builder.kind((StructureDefinitionKind) parseString(StructureDefinitionKind.builder(), "kind", getJsonNode(jsonNode, "kind", TextNode.class), jsonNode.get("_kind"), -1));
        builder._abstract(parseBoolean("abstract", getJsonNode(jsonNode, "abstract", JsonNode.class), jsonNode.get("_abstract"), -1));
        ArrayNode contextArray = getArrayNode(jsonNode, "context");
        if (contextArray != null) {
            for (int i = 0; i < contextArray.size(); i++) {
                if (contextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contextArray.get(i).getNodeType() + " for element: context");
                }
                builder.context(parseStructureDefinitionContext("context", contextArray.get(i), i));
            }
        }
        ArrayNode contextInvariantArray = getArrayNode(jsonNode, "contextInvariant", true);
        if (contextInvariantArray != null) {
            ArrayNode _contextInvariantArray = getArrayNode(jsonNode, "_contextInvariant");
            for (int i = 0; i < contextInvariantArray.size(); i++) {
                builder.contextInvariant(parseString("contextInvariant", contextInvariantArray.get(i), getJsonNode(_contextInvariantArray, i), i));
            }
        }
        builder.type(parseUri("type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.baseDefinition((Canonical) parseUri(Canonical.builder(), "baseDefinition", getJsonNode(jsonNode, "baseDefinition", TextNode.class), jsonNode.get("_baseDefinition"), -1));
        builder.derivation((TypeDerivationRule) parseString(TypeDerivationRule.builder(), "derivation", getJsonNode(jsonNode, "derivation", TextNode.class), jsonNode.get("_derivation"), -1));
        builder.snapshot(parseStructureDefinitionSnapshot("snapshot", getJsonNode(jsonNode, "snapshot", JsonNode.class), -1));
        builder.differential(parseStructureDefinitionDifferential("differential", getJsonNode(jsonNode, "differential", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private StructureDefinition.Context parseStructureDefinitionContext(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(StructureDefinition.Context.class, jsonNode);
        }
        StructureDefinition.Context.Builder builder = StructureDefinition.Context.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.type((ExtensionContextType) parseString(ExtensionContextType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.expression(parseString("expression", getJsonNode(jsonNode, "expression", TextNode.class), jsonNode.get("_expression"), -1));
        stackPop();
        return builder.build();
    }

    private StructureDefinition.Differential parseStructureDefinitionDifferential(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(StructureDefinition.Differential.class, jsonNode);
        }
        StructureDefinition.Differential.Builder builder = StructureDefinition.Differential.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        ArrayNode elementArray = getArrayNode(jsonNode, "element");
        if (elementArray != null) {
            for (int i = 0; i < elementArray.size(); i++) {
                if (elementArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + elementArray.get(i).getNodeType() + " for element: element");
                }
                builder.element(parseElementDefinition("element", elementArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private StructureDefinition.Mapping parseStructureDefinitionMapping(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(StructureDefinition.Mapping.class, jsonNode);
        }
        StructureDefinition.Mapping.Builder builder = StructureDefinition.Mapping.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.identity((Id) parseString(Id.builder(), "identity", getJsonNode(jsonNode, "identity", TextNode.class), jsonNode.get("_identity"), -1));
        builder.uri(parseUri("uri", getJsonNode(jsonNode, "uri", TextNode.class), jsonNode.get("_uri"), -1));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.comment(parseString("comment", getJsonNode(jsonNode, "comment", TextNode.class), jsonNode.get("_comment"), -1));
        stackPop();
        return builder.build();
    }

    private StructureDefinition.Snapshot parseStructureDefinitionSnapshot(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(StructureDefinition.Snapshot.class, jsonNode);
        }
        StructureDefinition.Snapshot.Builder builder = StructureDefinition.Snapshot.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        ArrayNode elementArray = getArrayNode(jsonNode, "element");
        if (elementArray != null) {
            for (int i = 0; i < elementArray.size(); i++) {
                if (elementArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + elementArray.get(i).getNodeType() + " for element: element");
                }
                builder.element(parseElementDefinition("element", elementArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities parseTerminologyCapabilities(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.class, jsonNode);
        }
        TerminologyCapabilities.Builder builder = TerminologyCapabilities.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
        builder.kind((CapabilityStatementKind) parseString(CapabilityStatementKind.builder(), "kind", getJsonNode(jsonNode, "kind", TextNode.class), jsonNode.get("_kind"), -1));
        builder.software(parseTerminologyCapabilitiesSoftware("software", getJsonNode(jsonNode, "software", JsonNode.class), -1));
        builder.implementation(parseTerminologyCapabilitiesImplementation("implementation", getJsonNode(jsonNode, "implementation", JsonNode.class), -1));
        builder.lockedDate(parseBoolean("lockedDate", getJsonNode(jsonNode, "lockedDate", JsonNode.class), jsonNode.get("_lockedDate"), -1));
        ArrayNode codeSystemArray = getArrayNode(jsonNode, "codeSystem");
        if (codeSystemArray != null) {
            for (int i = 0; i < codeSystemArray.size(); i++) {
                if (codeSystemArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + codeSystemArray.get(i).getNodeType() + " for element: codeSystem");
                }
                builder.codeSystem(parseTerminologyCapabilitiesCodeSystem("codeSystem", codeSystemArray.get(i), i));
            }
        }
        builder.expansion(parseTerminologyCapabilitiesExpansion("expansion", getJsonNode(jsonNode, "expansion", JsonNode.class), -1));
        builder.codeSearch((CodeSearchSupport) parseString(CodeSearchSupport.builder(), "codeSearch", getJsonNode(jsonNode, "codeSearch", TextNode.class), jsonNode.get("_codeSearch"), -1));
        builder.validateCode(parseTerminologyCapabilitiesValidateCode("validateCode", getJsonNode(jsonNode, "validateCode", JsonNode.class), -1));
        builder.translation(parseTerminologyCapabilitiesTranslation("translation", getJsonNode(jsonNode, "translation", JsonNode.class), -1));
        builder.closure(parseTerminologyCapabilitiesClosure("closure", getJsonNode(jsonNode, "closure", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.Closure parseTerminologyCapabilitiesClosure(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.Closure.class, jsonNode);
        }
        TerminologyCapabilities.Closure.Builder builder = TerminologyCapabilities.Closure.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.translation(parseBoolean("translation", getJsonNode(jsonNode, "translation", JsonNode.class), jsonNode.get("_translation"), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.CodeSystem parseTerminologyCapabilitiesCodeSystem(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.CodeSystem.class, jsonNode);
        }
        TerminologyCapabilities.CodeSystem.Builder builder = TerminologyCapabilities.CodeSystem.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.uri((Canonical) parseUri(Canonical.builder(), "uri", getJsonNode(jsonNode, "uri", TextNode.class), jsonNode.get("_uri"), -1));
        ArrayNode versionArray = getArrayNode(jsonNode, "version");
        if (versionArray != null) {
            for (int i = 0; i < versionArray.size(); i++) {
                if (versionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + versionArray.get(i).getNodeType() + " for element: version");
                }
                builder.version(parseTerminologyCapabilitiesCodeSystemVersion("version", versionArray.get(i), i));
            }
        }
        builder.content((CodeSystemContentMode) parseString(CodeSystemContentMode.builder(), "content", getJsonNode(jsonNode, "content", TextNode.class), jsonNode.get("_content"), -1));
        builder.subsumption(parseBoolean("subsumption", getJsonNode(jsonNode, "subsumption", JsonNode.class), jsonNode.get("_subsumption"), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.CodeSystem.Version parseTerminologyCapabilitiesCodeSystemVersion(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.CodeSystem.Version.class, jsonNode);
        }
        TerminologyCapabilities.CodeSystem.Version.Builder builder = TerminologyCapabilities.CodeSystem.Version.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code(parseString("code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.isDefault(parseBoolean("isDefault", getJsonNode(jsonNode, "isDefault", JsonNode.class), jsonNode.get("_isDefault"), -1));
        builder.compositional(parseBoolean("compositional", getJsonNode(jsonNode, "compositional", JsonNode.class), jsonNode.get("_compositional"), -1));
        ArrayNode languageArray = getArrayNode(jsonNode, "language", true);
        if (languageArray != null) {
            ArrayNode _languageArray = getArrayNode(jsonNode, "_language");
            for (int i = 0; i < languageArray.size(); i++) {
                builder.language((Language) parseString(Language.builder(), "language", languageArray.get(i), getJsonNode(_languageArray, i), i));
            }
        }
        ArrayNode filterArray = getArrayNode(jsonNode, "filter");
        if (filterArray != null) {
            for (int i = 0; i < filterArray.size(); i++) {
                if (filterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + filterArray.get(i).getNodeType() + " for element: filter");
                }
                builder.filter(parseTerminologyCapabilitiesCodeSystemVersionFilter("filter", filterArray.get(i), i));
            }
        }
        ArrayNode propertyArray = getArrayNode(jsonNode, "property", true);
        if (propertyArray != null) {
            ArrayNode _propertyArray = getArrayNode(jsonNode, "_property");
            for (int i = 0; i < propertyArray.size(); i++) {
                builder.property((Code) parseString(Code.builder(), "property", propertyArray.get(i), getJsonNode(_propertyArray, i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.CodeSystem.Version.Filter parseTerminologyCapabilitiesCodeSystemVersionFilter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.CodeSystem.Version.Filter.class, jsonNode);
        }
        TerminologyCapabilities.CodeSystem.Version.Filter.Builder builder = TerminologyCapabilities.CodeSystem.Version.Filter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        ArrayNode opArray = getArrayNode(jsonNode, "op", true);
        if (opArray != null) {
            ArrayNode _opArray = getArrayNode(jsonNode, "_op");
            for (int i = 0; i < opArray.size(); i++) {
                builder.op((Code) parseString(Code.builder(), "op", opArray.get(i), getJsonNode(_opArray, i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.Expansion parseTerminologyCapabilitiesExpansion(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.Expansion.class, jsonNode);
        }
        TerminologyCapabilities.Expansion.Builder builder = TerminologyCapabilities.Expansion.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.hierarchical(parseBoolean("hierarchical", getJsonNode(jsonNode, "hierarchical", JsonNode.class), jsonNode.get("_hierarchical"), -1));
        builder.paging(parseBoolean("paging", getJsonNode(jsonNode, "paging", JsonNode.class), jsonNode.get("_paging"), -1));
        builder.incomplete(parseBoolean("incomplete", getJsonNode(jsonNode, "incomplete", JsonNode.class), jsonNode.get("_incomplete"), -1));
        ArrayNode parameterArray = getArrayNode(jsonNode, "parameter");
        if (parameterArray != null) {
            for (int i = 0; i < parameterArray.size(); i++) {
                if (parameterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + parameterArray.get(i).getNodeType() + " for element: parameter");
                }
                builder.parameter(parseTerminologyCapabilitiesExpansionParameter("parameter", parameterArray.get(i), i));
            }
        }
        builder.textFilter((Markdown) parseString(Markdown.builder(), "textFilter", getJsonNode(jsonNode, "textFilter", TextNode.class), jsonNode.get("_textFilter"), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.Expansion.Parameter parseTerminologyCapabilitiesExpansionParameter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.Expansion.Parameter.class, jsonNode);
        }
        TerminologyCapabilities.Expansion.Parameter.Builder builder = TerminologyCapabilities.Expansion.Parameter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name((Code) parseString(Code.builder(), "name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.documentation(parseString("documentation", getJsonNode(jsonNode, "documentation", TextNode.class), jsonNode.get("_documentation"), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.Implementation parseTerminologyCapabilitiesImplementation(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.Implementation.class, jsonNode);
        }
        TerminologyCapabilities.Implementation.Builder builder = TerminologyCapabilities.Implementation.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.description(parseString("description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        builder.url((Url) parseUri(Url.builder(), "url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.Software parseTerminologyCapabilitiesSoftware(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.Software.class, jsonNode);
        }
        TerminologyCapabilities.Software.Builder builder = TerminologyCapabilities.Software.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.Translation parseTerminologyCapabilitiesTranslation(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.Translation.class, jsonNode);
        }
        TerminologyCapabilities.Translation.Builder builder = TerminologyCapabilities.Translation.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.needsMap(parseBoolean("needsMap", getJsonNode(jsonNode, "needsMap", JsonNode.class), jsonNode.get("_needsMap"), -1));
        stackPop();
        return builder.build();
    }

    private TerminologyCapabilities.ValidateCode parseTerminologyCapabilitiesValidateCode(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TerminologyCapabilities.ValidateCode.class, jsonNode);
        }
        TerminologyCapabilities.ValidateCode.Builder builder = TerminologyCapabilities.ValidateCode.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.translations(parseBoolean("translations", getJsonNode(jsonNode, "translations", JsonNode.class), jsonNode.get("_translations"), -1));
        stackPop();
        return builder.build();
    }

    private Time parseTime(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Time.Builder builder = Time.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Timing parseTiming(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Timing.class, jsonNode);
        }
        Timing.Builder builder = Timing.builder();
        builder.setValidating(validating);
        parseBackboneType(builder, jsonNode);
        ArrayNode eventArray = getArrayNode(jsonNode, "event", true);
        if (eventArray != null) {
            ArrayNode _eventArray = getArrayNode(jsonNode, "_event");
            for (int i = 0; i < eventArray.size(); i++) {
                builder.event(parseDateTime("event", eventArray.get(i), getJsonNode(_eventArray, i), i));
            }
        }
        builder.repeat(parseTimingRepeat("repeat", getJsonNode(jsonNode, "repeat", JsonNode.class), -1));
        builder.code(parseCodeableConcept("code", getJsonNode(jsonNode, "code", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Timing.Repeat parseTimingRepeat(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(Timing.Repeat.class, jsonNode);
        }
        Timing.Repeat.Builder builder = Timing.Repeat.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.bounds(parseChoiceElement("bounds", jsonNode, Duration.class, Range.class, Period.class));
        builder.count((PositiveInt) parseInteger(PositiveInt.builder(), "count", getJsonNode(jsonNode, "count", NumericNode.class), jsonNode.get("_count"), -1));
        builder.countMax((PositiveInt) parseInteger(PositiveInt.builder(), "countMax", getJsonNode(jsonNode, "countMax", NumericNode.class), jsonNode.get("_countMax"), -1));
        builder.duration(parseDecimal("duration", getJsonNode(jsonNode, "duration", NumericNode.class), jsonNode.get("_duration"), -1));
        builder.durationMax(parseDecimal("durationMax", getJsonNode(jsonNode, "durationMax", NumericNode.class), jsonNode.get("_durationMax"), -1));
        builder.durationUnit((UnitsOfTime) parseString(UnitsOfTime.builder(), "durationUnit", getJsonNode(jsonNode, "durationUnit", TextNode.class), jsonNode.get("_durationUnit"), -1));
        builder.frequency((PositiveInt) parseInteger(PositiveInt.builder(), "frequency", getJsonNode(jsonNode, "frequency", NumericNode.class), jsonNode.get("_frequency"), -1));
        builder.frequencyMax((PositiveInt) parseInteger(PositiveInt.builder(), "frequencyMax", getJsonNode(jsonNode, "frequencyMax", NumericNode.class), jsonNode.get("_frequencyMax"), -1));
        builder.period(parseDecimal("period", getJsonNode(jsonNode, "period", NumericNode.class), jsonNode.get("_period"), -1));
        builder.periodMax(parseDecimal("periodMax", getJsonNode(jsonNode, "periodMax", NumericNode.class), jsonNode.get("_periodMax"), -1));
        builder.periodUnit((UnitsOfTime) parseString(UnitsOfTime.builder(), "periodUnit", getJsonNode(jsonNode, "periodUnit", TextNode.class), jsonNode.get("_periodUnit"), -1));
        ArrayNode dayOfWeekArray = getArrayNode(jsonNode, "dayOfWeek", true);
        if (dayOfWeekArray != null) {
            ArrayNode _dayOfWeekArray = getArrayNode(jsonNode, "_dayOfWeek");
            for (int i = 0; i < dayOfWeekArray.size(); i++) {
                builder.dayOfWeek((DayOfWeek) parseString(DayOfWeek.builder(), "dayOfWeek", dayOfWeekArray.get(i), getJsonNode(_dayOfWeekArray, i), i));
            }
        }
        ArrayNode timeOfDayArray = getArrayNode(jsonNode, "timeOfDay", true);
        if (timeOfDayArray != null) {
            ArrayNode _timeOfDayArray = getArrayNode(jsonNode, "_timeOfDay");
            for (int i = 0; i < timeOfDayArray.size(); i++) {
                builder.timeOfDay(parseTime("timeOfDay", timeOfDayArray.get(i), getJsonNode(_timeOfDayArray, i), i));
            }
        }
        ArrayNode whenArray = getArrayNode(jsonNode, "when", true);
        if (whenArray != null) {
            ArrayNode _whenArray = getArrayNode(jsonNode, "_when");
            for (int i = 0; i < whenArray.size(); i++) {
                builder.when((EventTiming) parseString(EventTiming.builder(), "when", whenArray.get(i), getJsonNode(_whenArray, i), i));
            }
        }
        builder.offset((UnsignedInt) parseInteger(UnsignedInt.builder(), "offset", getJsonNode(jsonNode, "offset", NumericNode.class), jsonNode.get("_offset"), -1));
        stackPop();
        return builder.build();
    }

    private TriggerDefinition parseTriggerDefinition(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(TriggerDefinition.class, jsonNode);
        }
        TriggerDefinition.Builder builder = TriggerDefinition.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.type((TriggerType) parseString(TriggerType.builder(), "type", getJsonNode(jsonNode, "type", TextNode.class), jsonNode.get("_type"), -1));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.code(parseCodeableConcept("code", getJsonNode(jsonNode, "code", JsonNode.class), -1));
        builder.subscriptionTopic((Canonical) parseUri(Canonical.builder(), "subscriptionTopic", getJsonNode(jsonNode, "subscriptionTopic", TextNode.class), jsonNode.get("_subscriptionTopic"), -1));
        builder.timing(parseChoiceElement("timing", jsonNode, Timing.class, Reference.class, Date.class, DateTime.class));
        ArrayNode dataArray = getArrayNode(jsonNode, "data");
        if (dataArray != null) {
            for (int i = 0; i < dataArray.size(); i++) {
                if (dataArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + dataArray.get(i).getNodeType() + " for element: data");
                }
                builder.data(parseDataRequirement("data", dataArray.get(i), i));
            }
        }
        builder.condition(parseExpression("condition", getJsonNode(jsonNode, "condition", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private Uri parseUri(Uri.Builder builder, java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
            ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Uri parseUri(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        return parseUri(Uri.builder(), elementName, jsonNode, _jsonNode, elementIndex);
    }

    private UsageContext parseUsageContext(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(UsageContext.class, jsonNode);
        }
        UsageContext.Builder builder = UsageContext.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.code(parseCoding("code", getJsonNode(jsonNode, "code", JsonNode.class), -1));
        builder.value(parseChoiceElement("value", jsonNode, CodeableConcept.class, Quantity.class, Range.class, Reference.class));
        stackPop();
        return builder.build();
    }

    private ValueSet parseValueSet(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.class, jsonNode);
        }
        ValueSet.Builder builder = ValueSet.builder();
        builder.setValidating(validating);
        parseDomainResource(builder, jsonNode);
        builder.url(parseUri("url", getJsonNode(jsonNode, "url", TextNode.class), jsonNode.get("_url"), -1));
        ArrayNode identifierArray = getArrayNode(jsonNode, "identifier");
        if (identifierArray != null) {
            for (int i = 0; i < identifierArray.size(); i++) {
                if (identifierArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + identifierArray.get(i).getNodeType() + " for element: identifier");
                }
                builder.identifier(parseIdentifier("identifier", identifierArray.get(i), i));
            }
        }
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.versionAlgorithm(parseChoiceElement("versionAlgorithm", jsonNode, String.class, Coding.class));
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.title(parseString("title", getJsonNode(jsonNode, "title", TextNode.class), jsonNode.get("_title"), -1));
        builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", getJsonNode(jsonNode, "status", TextNode.class), jsonNode.get("_status"), -1));
        builder.experimental(parseBoolean("experimental", getJsonNode(jsonNode, "experimental", JsonNode.class), jsonNode.get("_experimental"), -1));
        builder.date(parseDateTime("date", getJsonNode(jsonNode, "date", TextNode.class), jsonNode.get("_date"), -1));
        builder.publisher(parseString("publisher", getJsonNode(jsonNode, "publisher", TextNode.class), jsonNode.get("_publisher"), -1));
        ArrayNode contactArray = getArrayNode(jsonNode, "contact");
        if (contactArray != null) {
            for (int i = 0; i < contactArray.size(); i++) {
                if (contactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + contactArray.get(i).getNodeType() + " for element: contact");
                }
                builder.contact(parseContactDetail("contact", contactArray.get(i), i));
            }
        }
        builder.description((Markdown) parseString(Markdown.builder(), "description", getJsonNode(jsonNode, "description", TextNode.class), jsonNode.get("_description"), -1));
        ArrayNode useContextArray = getArrayNode(jsonNode, "useContext");
        if (useContextArray != null) {
            for (int i = 0; i < useContextArray.size(); i++) {
                if (useContextArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + useContextArray.get(i).getNodeType() + " for element: useContext");
                }
                builder.useContext(parseUsageContext("useContext", useContextArray.get(i), i));
            }
        }
        ArrayNode jurisdictionArray = getArrayNode(jsonNode, "jurisdiction");
        if (jurisdictionArray != null) {
            for (int i = 0; i < jurisdictionArray.size(); i++) {
                if (jurisdictionArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + jurisdictionArray.get(i).getNodeType() + " for element: jurisdiction");
                }
                builder.jurisdiction(parseCodeableConcept("jurisdiction", jurisdictionArray.get(i), i));
            }
        }
        builder.immutable(parseBoolean("immutable", getJsonNode(jsonNode, "immutable", JsonNode.class), jsonNode.get("_immutable"), -1));
        builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", getJsonNode(jsonNode, "purpose", TextNode.class), jsonNode.get("_purpose"), -1));
        builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        builder.copyrightLabel(parseString("copyrightLabel", getJsonNode(jsonNode, "copyrightLabel", TextNode.class), jsonNode.get("_copyrightLabel"), -1));
        builder.approvalDate(parseDate("approvalDate", getJsonNode(jsonNode, "approvalDate", TextNode.class), jsonNode.get("_approvalDate"), -1));
        builder.lastReviewDate(parseDate("lastReviewDate", getJsonNode(jsonNode, "lastReviewDate", TextNode.class), jsonNode.get("_lastReviewDate"), -1));
        builder.effectivePeriod(parsePeriod("effectivePeriod", getJsonNode(jsonNode, "effectivePeriod", JsonNode.class), -1));
        ArrayNode topicArray = getArrayNode(jsonNode, "topic");
        if (topicArray != null) {
            for (int i = 0; i < topicArray.size(); i++) {
                if (topicArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + topicArray.get(i).getNodeType() + " for element: topic");
                }
                builder.topic(parseCodeableConcept("topic", topicArray.get(i), i));
            }
        }
        ArrayNode authorArray = getArrayNode(jsonNode, "author");
        if (authorArray != null) {
            for (int i = 0; i < authorArray.size(); i++) {
                if (authorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + authorArray.get(i).getNodeType() + " for element: author");
                }
                builder.author(parseContactDetail("author", authorArray.get(i), i));
            }
        }
        ArrayNode editorArray = getArrayNode(jsonNode, "editor");
        if (editorArray != null) {
            for (int i = 0; i < editorArray.size(); i++) {
                if (editorArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + editorArray.get(i).getNodeType() + " for element: editor");
                }
                builder.editor(parseContactDetail("editor", editorArray.get(i), i));
            }
        }
        ArrayNode reviewerArray = getArrayNode(jsonNode, "reviewer");
        if (reviewerArray != null) {
            for (int i = 0; i < reviewerArray.size(); i++) {
                if (reviewerArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + reviewerArray.get(i).getNodeType() + " for element: reviewer");
                }
                builder.reviewer(parseContactDetail("reviewer", reviewerArray.get(i), i));
            }
        }
        ArrayNode endorserArray = getArrayNode(jsonNode, "endorser");
        if (endorserArray != null) {
            for (int i = 0; i < endorserArray.size(); i++) {
                if (endorserArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + endorserArray.get(i).getNodeType() + " for element: endorser");
                }
                builder.endorser(parseContactDetail("endorser", endorserArray.get(i), i));
            }
        }
        ArrayNode relatedArtifactArray = getArrayNode(jsonNode, "relatedArtifact");
        if (relatedArtifactArray != null) {
            for (int i = 0; i < relatedArtifactArray.size(); i++) {
                if (relatedArtifactArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + relatedArtifactArray.get(i).getNodeType() + " for element: relatedArtifact");
                }
                builder.relatedArtifact(parseRelatedArtifact("relatedArtifact", relatedArtifactArray.get(i), i));
            }
        }
        builder.compose(parseValueSetCompose("compose", getJsonNode(jsonNode, "compose", JsonNode.class), -1));
        builder.expansion(parseValueSetExpansion("expansion", getJsonNode(jsonNode, "expansion", JsonNode.class), -1));
        builder.scope(parseValueSetScope("scope", getJsonNode(jsonNode, "scope", JsonNode.class), -1));
        stackPop();
        return builder.build();
    }

    private ValueSet.Compose parseValueSetCompose(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Compose.class, jsonNode);
        }
        ValueSet.Compose.Builder builder = ValueSet.Compose.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.lockedDate(parseDate("lockedDate", getJsonNode(jsonNode, "lockedDate", TextNode.class), jsonNode.get("_lockedDate"), -1));
        builder.inactive(parseBoolean("inactive", getJsonNode(jsonNode, "inactive", JsonNode.class), jsonNode.get("_inactive"), -1));
        ArrayNode includeArray = getArrayNode(jsonNode, "include");
        if (includeArray != null) {
            for (int i = 0; i < includeArray.size(); i++) {
                if (includeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + includeArray.get(i).getNodeType() + " for element: include");
                }
                builder.include(parseValueSetComposeInclude("include", includeArray.get(i), i));
            }
        }
        ArrayNode excludeArray = getArrayNode(jsonNode, "exclude");
        if (excludeArray != null) {
            for (int i = 0; i < excludeArray.size(); i++) {
                if (excludeArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + excludeArray.get(i).getNodeType() + " for element: exclude");
                }
                builder.exclude(parseValueSetComposeInclude("exclude", excludeArray.get(i), i));
            }
        }
        ArrayNode propertyArray = getArrayNode(jsonNode, "property", true);
        if (propertyArray != null) {
            ArrayNode _propertyArray = getArrayNode(jsonNode, "_property");
            for (int i = 0; i < propertyArray.size(); i++) {
                builder.property(parseString("property", propertyArray.get(i), getJsonNode(_propertyArray, i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ValueSet.Compose.Include parseValueSetComposeInclude(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Compose.Include.class, jsonNode);
        }
        ValueSet.Compose.Include.Builder builder = ValueSet.Compose.Include.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.system(parseUri("system", getJsonNode(jsonNode, "system", TextNode.class), jsonNode.get("_system"), -1));
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        ArrayNode conceptArray = getArrayNode(jsonNode, "concept");
        if (conceptArray != null) {
            for (int i = 0; i < conceptArray.size(); i++) {
                if (conceptArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + conceptArray.get(i).getNodeType() + " for element: concept");
                }
                builder.concept(parseValueSetComposeIncludeConcept("concept", conceptArray.get(i), i));
            }
        }
        ArrayNode filterArray = getArrayNode(jsonNode, "filter");
        if (filterArray != null) {
            for (int i = 0; i < filterArray.size(); i++) {
                if (filterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + filterArray.get(i).getNodeType() + " for element: filter");
                }
                builder.filter(parseValueSetComposeIncludeFilter("filter", filterArray.get(i), i));
            }
        }
        ArrayNode valueSetArray = getArrayNode(jsonNode, "valueSet", true);
        if (valueSetArray != null) {
            ArrayNode _valueSetArray = getArrayNode(jsonNode, "_valueSet");
            for (int i = 0; i < valueSetArray.size(); i++) {
                builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", valueSetArray.get(i), getJsonNode(_valueSetArray, i), i));
            }
        }
        builder.copyright(parseString("copyright", getJsonNode(jsonNode, "copyright", TextNode.class), jsonNode.get("_copyright"), -1));
        stackPop();
        return builder.build();
    }

    private ValueSet.Compose.Include.Concept parseValueSetComposeIncludeConcept(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Compose.Include.Concept.class, jsonNode);
        }
        ValueSet.Compose.Include.Concept.Builder builder = ValueSet.Compose.Include.Concept.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        ArrayNode designationArray = getArrayNode(jsonNode, "designation");
        if (designationArray != null) {
            for (int i = 0; i < designationArray.size(); i++) {
                if (designationArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + designationArray.get(i).getNodeType() + " for element: designation");
                }
                builder.designation(parseValueSetComposeIncludeConceptDesignation("designation", designationArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ValueSet.Compose.Include.Concept.Designation parseValueSetComposeIncludeConceptDesignation(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Compose.Include.Concept.Designation.class, jsonNode);
        }
        ValueSet.Compose.Include.Concept.Designation.Builder builder = ValueSet.Compose.Include.Concept.Designation.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.language((Code) parseString(Code.builder(), "language", getJsonNode(jsonNode, "language", TextNode.class), jsonNode.get("_language"), -1));
        builder.use(parseCoding("use", getJsonNode(jsonNode, "use", JsonNode.class), -1));
        ArrayNode additionalUseArray = getArrayNode(jsonNode, "additionalUse");
        if (additionalUseArray != null) {
            for (int i = 0; i < additionalUseArray.size(); i++) {
                if (additionalUseArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + additionalUseArray.get(i).getNodeType() + " for element: additionalUse");
                }
                builder.additionalUse(parseCoding("additionalUse", additionalUseArray.get(i), i));
            }
        }
        builder.value(parseString("value", getJsonNode(jsonNode, "value", TextNode.class), jsonNode.get("_value"), -1));
        stackPop();
        return builder.build();
    }

    private ValueSet.Compose.Include.Filter parseValueSetComposeIncludeFilter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Compose.Include.Filter.class, jsonNode);
        }
        ValueSet.Compose.Include.Filter.Builder builder = ValueSet.Compose.Include.Filter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.property((Code) parseString(Code.builder(), "property", getJsonNode(jsonNode, "property", TextNode.class), jsonNode.get("_property"), -1));
        builder.op((FilterOperator) parseString(FilterOperator.builder(), "op", getJsonNode(jsonNode, "op", TextNode.class), jsonNode.get("_op"), -1));
        builder.value(parseString("value", getJsonNode(jsonNode, "value", TextNode.class), jsonNode.get("_value"), -1));
        stackPop();
        return builder.build();
    }

    private ValueSet.Expansion parseValueSetExpansion(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Expansion.class, jsonNode);
        }
        ValueSet.Expansion.Builder builder = ValueSet.Expansion.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.identifier(parseUri("identifier", getJsonNode(jsonNode, "identifier", TextNode.class), jsonNode.get("_identifier"), -1));
        builder.next(parseUri("next", getJsonNode(jsonNode, "next", TextNode.class), jsonNode.get("_next"), -1));
        builder.timestamp(parseDateTime("timestamp", getJsonNode(jsonNode, "timestamp", TextNode.class), jsonNode.get("_timestamp"), -1));
        builder.total(parseInteger("total", getJsonNode(jsonNode, "total", NumericNode.class), jsonNode.get("_total"), -1));
        builder.offset(parseInteger("offset", getJsonNode(jsonNode, "offset", NumericNode.class), jsonNode.get("_offset"), -1));
        ArrayNode parameterArray = getArrayNode(jsonNode, "parameter");
        if (parameterArray != null) {
            for (int i = 0; i < parameterArray.size(); i++) {
                if (parameterArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + parameterArray.get(i).getNodeType() + " for element: parameter");
                }
                builder.parameter(parseValueSetExpansionParameter("parameter", parameterArray.get(i), i));
            }
        }
        ArrayNode propertyArray = getArrayNode(jsonNode, "property");
        if (propertyArray != null) {
            for (int i = 0; i < propertyArray.size(); i++) {
                if (propertyArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + propertyArray.get(i).getNodeType() + " for element: property");
                }
                builder.property(parseValueSetExpansionProperty("property", propertyArray.get(i), i));
            }
        }
        ArrayNode containsArray = getArrayNode(jsonNode, "contains");
        if (containsArray != null) {
            for (int i = 0; i < containsArray.size(); i++) {
                if (containsArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + containsArray.get(i).getNodeType() + " for element: contains");
                }
                builder.contains(parseValueSetExpansionContains("contains", containsArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ValueSet.Expansion.Contains parseValueSetExpansionContains(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Expansion.Contains.class, jsonNode);
        }
        ValueSet.Expansion.Contains.Builder builder = ValueSet.Expansion.Contains.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.system(parseUri("system", getJsonNode(jsonNode, "system", TextNode.class), jsonNode.get("_system"), -1));
        builder._abstract(parseBoolean("abstract", getJsonNode(jsonNode, "abstract", JsonNode.class), jsonNode.get("_abstract"), -1));
        builder.inactive(parseBoolean("inactive", getJsonNode(jsonNode, "inactive", JsonNode.class), jsonNode.get("_inactive"), -1));
        builder.version(parseString("version", getJsonNode(jsonNode, "version", TextNode.class), jsonNode.get("_version"), -1));
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.display(parseString("display", getJsonNode(jsonNode, "display", TextNode.class), jsonNode.get("_display"), -1));
        ArrayNode designationArray = getArrayNode(jsonNode, "designation");
        if (designationArray != null) {
            for (int i = 0; i < designationArray.size(); i++) {
                if (designationArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + designationArray.get(i).getNodeType() + " for element: designation");
                }
                builder.designation(parseValueSetComposeIncludeConceptDesignation("designation", designationArray.get(i), i));
            }
        }
        ArrayNode propertyArray = getArrayNode(jsonNode, "property");
        if (propertyArray != null) {
            for (int i = 0; i < propertyArray.size(); i++) {
                if (propertyArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + propertyArray.get(i).getNodeType() + " for element: property");
                }
                builder.property(parseValueSetExpansionContainsProperty("property", propertyArray.get(i), i));
            }
        }
        ArrayNode containsArray = getArrayNode(jsonNode, "contains");
        if (containsArray != null) {
            for (int i = 0; i < containsArray.size(); i++) {
                if (containsArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + containsArray.get(i).getNodeType() + " for element: contains");
                }
                builder.contains(parseValueSetExpansionContains("contains", containsArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ValueSet.Expansion.Contains.Property parseValueSetExpansionContainsProperty(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Expansion.Contains.Property.class, jsonNode);
        }
        ValueSet.Expansion.Contains.Property.Builder builder = ValueSet.Expansion.Contains.Property.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.value(parseChoiceElement("value", jsonNode, Code.class, Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class));
        ArrayNode subPropertyArray = getArrayNode(jsonNode, "subProperty");
        if (subPropertyArray != null) {
            for (int i = 0; i < subPropertyArray.size(); i++) {
                if (subPropertyArray.get(i).getNodeType() != JsonNodeType.OBJECT) {
                    throw new IllegalArgumentException("Expected: OBJECT but found: " + subPropertyArray.get(i).getNodeType() + " for element: subProperty");
                }
                builder.subProperty(parseValueSetExpansionContainsPropertySubProperty("subProperty", subPropertyArray.get(i), i));
            }
        }
        stackPop();
        return builder.build();
    }

    private ValueSet.Expansion.Contains.Property.SubProperty parseValueSetExpansionContainsPropertySubProperty(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Expansion.Contains.Property.SubProperty.class, jsonNode);
        }
        ValueSet.Expansion.Contains.Property.SubProperty.Builder builder = ValueSet.Expansion.Contains.Property.SubProperty.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.value(parseChoiceElement("value", jsonNode, Code.class, Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class));
        stackPop();
        return builder.build();
    }

    private ValueSet.Expansion.Parameter parseValueSetExpansionParameter(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Expansion.Parameter.class, jsonNode);
        }
        ValueSet.Expansion.Parameter.Builder builder = ValueSet.Expansion.Parameter.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.name(parseString("name", getJsonNode(jsonNode, "name", TextNode.class), jsonNode.get("_name"), -1));
        builder.value(parseChoiceElement("value", jsonNode, String.class, Boolean.class, Integer.class, Decimal.class, Uri.class, Code.class, DateTime.class));
        stackPop();
        return builder.build();
    }

    private ValueSet.Expansion.Property parseValueSetExpansionProperty(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Expansion.Property.class, jsonNode);
        }
        ValueSet.Expansion.Property.Builder builder = ValueSet.Expansion.Property.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.code((Code) parseString(Code.builder(), "code", getJsonNode(jsonNode, "code", TextNode.class), jsonNode.get("_code"), -1));
        builder.uri(parseUri("uri", getJsonNode(jsonNode, "uri", TextNode.class), jsonNode.get("_uri"), -1));
        stackPop();
        return builder.build();
    }

    private ValueSet.Scope parseValueSetScope(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(ValueSet.Scope.class, jsonNode);
        }
        ValueSet.Scope.Builder builder = ValueSet.Scope.builder();
        builder.setValidating(validating);
        parseBackboneElement(builder, jsonNode);
        builder.inclusionCriteria(parseString("inclusionCriteria", getJsonNode(jsonNode, "inclusionCriteria", TextNode.class), jsonNode.get("_inclusionCriteria"), -1));
        builder.exclusionCriteria(parseString("exclusionCriteria", getJsonNode(jsonNode, "exclusionCriteria", TextNode.class), jsonNode.get("_exclusionCriteria"), -1));
        stackPop();
        return builder.build();
    }

    private VirtualServiceDetail parseVirtualServiceDetail(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
        if (jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        if (!ignoringUnrecognizedElements) {
            checkForUnrecognizedElements(VirtualServiceDetail.class, jsonNode);
        }
        VirtualServiceDetail.Builder builder = VirtualServiceDetail.builder();
        builder.setValidating(validating);
        parseDataType(builder, jsonNode);
        builder.channelType(parseCoding("channelType", getJsonNode(jsonNode, "channelType", JsonNode.class), -1));
        builder.address(parseChoiceElement("address", jsonNode, Url.class, String.class, ContactPoint.class, ExtendedContactDetail.class));
        ArrayNode additionalInfoArray = getArrayNode(jsonNode, "additionalInfo", true);
        if (additionalInfoArray != null) {
            ArrayNode _additionalInfoArray = getArrayNode(jsonNode, "_additionalInfo");
            for (int i = 0; i < additionalInfoArray.size(); i++) {
                builder.additionalInfo((Url) parseUri(Url.builder(), "additionalInfo", additionalInfoArray.get(i), getJsonNode(_additionalInfoArray, i), i));
            }
        }
        builder.maxParticipants((PositiveInt) parseInteger(PositiveInt.builder(), "maxParticipants", getJsonNode(jsonNode, "maxParticipants", NumericNode.class), jsonNode.get("_maxParticipants"), -1));
        builder.sessionKey(parseString("sessionKey", getJsonNode(jsonNode, "sessionKey", TextNode.class), jsonNode.get("_sessionKey"), -1));
        stackPop();
        return builder.build();
    }

    private Xhtml parseXhtml(java.lang.String elementName, JsonNode jsonNode, JsonNode _jsonNode, int elementIndex) {
        if (jsonNode == null && _jsonNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        Xhtml.Builder builder = Xhtml.builder();
        builder.setValidating(validating);
        if (_jsonNode != null && _jsonNode.getNodeType() == JsonNodeType.OBJECT) {
        	ObjectNode objectNode = (ObjectNode) _jsonNode;
            if (!ignoringUnrecognizedElements) {
                checkForUnrecognizedElements(Element.class, objectNode);
            }
            parseElement(builder, objectNode);
        } else if (_jsonNode != null && (_jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: OBJECT but found: " + _jsonNode.getNodeType() + " for element: _" + elementName);
        }
        if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.STRING) {
            TextNode textNode = (TextNode) jsonNode;
            builder.value(textNode.textValue());
        } else if (jsonNode != null && (jsonNode.getNodeType() != JsonNodeType.NULL || elementIndex == -1)) {
            throw new IllegalArgumentException("Expected: STRING but found: " + jsonNode.getNodeType() + " for element: " + elementName);
        }
        stackPop();
        return builder.build();
    }

    private Element parseElement(java.lang.String elementName, JsonNode jsonNode, int elementIndex) {
    	throw new UnsupportedOperationException("Element " + elementName + " is not modeled.");
	}

    private void stackPush(java.lang.String elementName, int elementIndex) {
        if (elementIndex != -1) {
            stack.push(elementName + "[" + elementIndex + "]");
        } else {
            stack.push(elementName);
        }
        if (DEBUG) {
            System.out.println(getPath());
        }
    }

    private void stackPop() {
        stack.pop();
    }

    private Element parseChoiceElement(java.lang.String name, JsonNode jsonNode, Class<?>... choiceTypes) {
        if (jsonNode == null) {
            return null;
        }

        java.lang.String elementName = null;
        java.lang.String _elementName = null;
        Class<?> elementType = null;

        for (Class<?> choiceType : choiceTypes) {
            java.lang.String key = getChoiceElementName(name, choiceType);
            if (jsonNode.has(key)) {
                if (elementName != null) {
                    throw new IllegalArgumentException("Only one choice element key of the form: " + name + "[x] is allowed");
                }
                elementName = key;
                elementType = choiceType;
            }

            java.lang.String _key = "_" + key;
            if (jsonNode.has(_key)) {
                if (_elementName != null) {
                    throw new IllegalArgumentException("Only one choice element key of the form: _" + name + "[x] is allowed");
                }
                _elementName = _key;
                if (elementType == null) {
                    elementType = choiceType;
                }
            }
        }

        if (elementName != null && _elementName != null && !_elementName.endsWith(elementName)) {
            throw new IllegalArgumentException("Choice element keys: " + elementName + " and " + _elementName + " are not consistent");
        }

        JsonNode nestedNode = null;
        if (elementName != null) {
            nestedNode = jsonNode.get(elementName);
        }

        JsonNode _nestedNode = null;
        if (_elementName != null) {
            _nestedNode = jsonNode.get(_elementName);
        }

        if (elementType != null) {
            switch (elementType.getSimpleName()) {
            case "Base64Binary":
                return parseBase64Binary(elementName, nestedNode, _nestedNode, -1);
            case "Boolean":
                return parseBoolean(elementName, nestedNode, _nestedNode, -1);
            case "Canonical":
                return parseUri(Canonical.builder(), elementName, nestedNode, _nestedNode, -1);
            case "Code":
                return parseString(Code.builder(), elementName, nestedNode, _nestedNode, -1);
            case "Date":
                return parseDate(elementName, nestedNode, _nestedNode, -1);
            case "DateTime":
                return parseDateTime(elementName, nestedNode, _nestedNode, -1);
            case "Decimal":
                return parseDecimal(elementName, nestedNode, _nestedNode, -1);
            case "Id":
                return parseString(Id.builder(), elementName, nestedNode, _nestedNode, -1);
            case "Instant":
                return parseInstant(elementName, nestedNode, _nestedNode, -1);
            case "Integer":
                return parseInteger(elementName, nestedNode, _nestedNode, -1);
            case "Markdown":
                return parseString(Markdown.builder(), elementName, nestedNode, _nestedNode, -1);
            case "Oid":
                return parseUri(Oid.builder(), elementName, nestedNode, _nestedNode, -1);
            case "PositiveInt":
                return parseInteger(PositiveInt.builder(), elementName, nestedNode, _nestedNode, -1);
            case "String":
                return parseString(elementName, nestedNode, _nestedNode, -1);
            case "Time":
                return parseTime(elementName, nestedNode, _nestedNode, -1);
            case "UnsignedInt":
                return parseInteger(UnsignedInt.builder(), elementName, nestedNode, _nestedNode, -1);
            case "Uri":
                return parseUri(elementName, nestedNode, _nestedNode, -1);
            case "Url":
                return parseUri(Url.builder(), elementName, nestedNode, _nestedNode, -1);
            case "Uuid":
                return parseUri(Uuid.builder(), elementName, nestedNode, _nestedNode, -1);
            case "Address":
                return parseAddress(elementName, (JsonNode) nestedNode, -1);
            case "Age":
                return parseQuantity(Age.builder(), elementName, (JsonNode) nestedNode, -1);
            case "Annotation":
                return parseAnnotation(elementName, (JsonNode) nestedNode, -1);
            case "Attachment":
                return parseAttachment(elementName, (JsonNode) nestedNode, -1);
            case "CodeableConcept":
                return parseCodeableConcept(elementName, (JsonNode) nestedNode, -1);
            case "CodeableReference":
                return parseCodeableReference(elementName, (JsonNode) nestedNode, -1);
            case "Coding":
                return parseCoding(elementName, (JsonNode) nestedNode, -1);
            case "ContactPoint":
                return parseContactPoint(elementName, (JsonNode) nestedNode, -1);
            case "Count":
                return parseQuantity(Count.builder(), elementName, (JsonNode) nestedNode, -1);
            case "Distance":
                return parseQuantity(Distance.builder(), elementName, (JsonNode) nestedNode, -1);
            case "Duration":
                return parseQuantity(Duration.builder(), elementName, (JsonNode) nestedNode, -1);
            case "HumanName":
                return parseHumanName(elementName, (JsonNode) nestedNode, -1);
            case "Identifier":
                return parseIdentifier(elementName, (JsonNode) nestedNode, -1);
            case "Money":
                return parseMoney(elementName, (JsonNode) nestedNode, -1);
            case "MoneyQuantity":
                return parseQuantity(MoneyQuantity.builder(), elementName, (JsonNode) nestedNode, -1);
            case "Period":
                return parsePeriod(elementName, (JsonNode) nestedNode, -1);
            case "Quantity":
                return parseQuantity(elementName, (JsonNode) nestedNode, -1);
            case "Range":
                return parseRange(elementName, (JsonNode) nestedNode, -1);
            case "Ratio":
                return parseRatio(elementName, (JsonNode) nestedNode, -1);
            case "RatioRange":
                return parseRatioRange(elementName, (JsonNode) nestedNode, -1);
            case "Reference":
                return parseReference(elementName, (JsonNode) nestedNode, -1);
            case "SampledData":
                return parseSampledData(elementName, (JsonNode) nestedNode, -1);
            case "SimpleQuantity":
                return parseQuantity(SimpleQuantity.builder(), elementName, (JsonNode) nestedNode, -1);
            case "Signature":
                return parseSignature(elementName, (JsonNode) nestedNode, -1);
            case "Timing":
                return parseTiming(elementName, (JsonNode) nestedNode, -1);
            case "ContactDetail":
                return parseContactDetail(elementName, (JsonNode) nestedNode, -1);
            case "Contributor":
                return parseContributor(elementName, (JsonNode) nestedNode, -1);
            case "DataRequirement":
                return parseDataRequirement(elementName, (JsonNode) nestedNode, -1);
            case "Expression":
                return parseExpression(elementName, (JsonNode) nestedNode, -1);
            case "ParameterDefinition":
                return parseParameterDefinition(elementName, (JsonNode) nestedNode, -1);
            case "RelatedArtifact":
                return parseRelatedArtifact(elementName, (JsonNode) nestedNode, -1);
            case "TriggerDefinition":
                return parseTriggerDefinition(elementName, (JsonNode) nestedNode, -1);
            case "UsageContext":
                return parseUsageContext(elementName, (JsonNode) nestedNode, -1);
            case "Dosage":
                return parseDosage(elementName, (JsonNode) nestedNode, -1);
            case "Meta":
                return parseMeta(elementName, (JsonNode) nestedNode, -1);
            }
        }

        return null;
    }

    private java.lang.String getPath() {
        StringJoiner joiner = new StringJoiner(".");
        for (java.lang.String s : stack) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    private java.lang.String parseJavaString(java.lang.String elementName, TextNode textNode, int elementIndex) {
        if (textNode == null) {
            return null;
        }
        stackPush(elementName, elementIndex);
        java.lang.String javaString = textNode.textValue();
        stackPop();
        return javaString;
    }
}
