/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.parser;

import static org.linuxforhealth.fhir.model.util.XMLSupport.*;

import java.io.InputStream;
import java.io.Reader;
import java.util.Stack;
import java.util.StringJoiner;

import javax.annotation.Generated;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.linuxforhealth.fhir.core.ResourceType;
import org.linuxforhealth.fhir.model.parser.exception.FHIRParserException;
import org.linuxforhealth.fhir.model.r5.resource.*;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.code.*;
import org.linuxforhealth.fhir.model.util.XMLSupport.StreamReaderDelegate;

//import net.jcip.annotations.NotThreadSafe;

/*
 * Modifications:
 * 
 * - Remove case for "MetadataResource" and "CanonicalResource"
 * - Remove support for resource types unrelated to terminology services
 */

//@NotThreadSafe
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class FHIRXMLParser extends FHIRAbstractParser {
    public static boolean DEBUG = false;

    private final Stack<java.lang.String> stack = new Stack<>();

    FHIRXMLParser() {
        // only visible to subclasses or classes/interfaces in the same package (e.g. FHIRParser)
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T parse(InputStream in) throws FHIRParserException {
        try (StreamReaderDelegate delegate = createStreamReaderDelegate(in)) {
            reset();
            while (delegate.hasNext()) {
                int eventType = delegate.next();
                switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    requireNamespace(delegate, FHIR_NS_URI);
                    return (T) parseResource(getResourceType(delegate), delegate, -1);
                }
            }
            throw new XMLStreamException("Unexpected end of stream");
        } catch (Exception e) {
            throw new FHIRParserException(e.getMessage(), getPath(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T parse(Reader reader) throws FHIRParserException {
        try (StreamReaderDelegate delegate = createStreamReaderDelegate(reader)) {
            reset();
            while (delegate.hasNext()) {
                int eventType = delegate.next();
                switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    requireNamespace(delegate, FHIR_NS_URI);
                    return (T) parseResource(getResourceType(delegate), delegate, -1);
                }
            }
            throw new XMLStreamException("Unexpected end of stream");
        } catch (Exception e) {
            throw new FHIRParserException(e.getMessage(), getPath(), e);
        }
    }

    private void reset() {
        stack.clear();
    }

    private Resource parseResource(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        if (isResourceContainer(elementName)) {
            reader.nextTag();
        }
        java.lang.String resourceType = getResourceType(reader);
        switch (resourceType) {
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
        	return throwUnsupportedElement(elementName);
            
        case "Bundle":
            return parseBundle(elementName, reader, elementIndex);
        case "CapabilityStatement":
            return parseCapabilityStatement(elementName, reader, elementIndex);
        case "CodeSystem":
            return parseCodeSystem(elementName, reader, elementIndex);
        case "ConceptMap":
            return parseConceptMap(elementName, reader, elementIndex);
        case "OperationDefinition":
            return parseOperationDefinition(elementName, reader, elementIndex);
        case "OperationOutcome":
            return parseOperationOutcome(elementName, reader, elementIndex);
        case "Parameters":
            return parseParameters(elementName, reader, elementIndex);
        case "StructureDefinition":
            return parseStructureDefinition(elementName, reader, elementIndex);
        case "TerminologyCapabilities":
            return parseTerminologyCapabilities(elementName, reader, elementIndex);
        case "ValueSet":
            return parseValueSet(elementName, reader, elementIndex);
        }
        return null;
    }

    private Attachment parseAttachment(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Attachment.Builder builder = Attachment.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "contentType":
                    position = checkElementOrder("contentType", 1, position, false);
                    builder.contentType((Code) parseString(Code.builder(), "contentType", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 2, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "data":
                    position = checkElementOrder("data", 3, position, false);
                    builder.data(parseBase64Binary("data", reader, -1));
                    break;
                case "url":
                    position = checkElementOrder("url", 4, position, false);
                    builder.url((Url) parseUri(Url.builder(), "url", reader, -1));
                    break;
                case "size":
                    position = checkElementOrder("size", 5, position, false);
                    builder.size(parseInteger64("size", reader, -1));
                    break;
                case "hash":
                    position = checkElementOrder("hash", 6, position, false);
                    builder.hash(parseBase64Binary("hash", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 7, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "creation":
                    position = checkElementOrder("creation", 8, position, false);
                    builder.creation(parseDateTime("creation", reader, -1));
                    break;
                case "height":
                    position = checkElementOrder("height", 9, position, false);
                    builder.height((PositiveInt) parseInteger(PositiveInt.builder(), "height", reader, -1));
                    break;
                case "width":
                    position = checkElementOrder("width", 10, position, false);
                    builder.width((PositiveInt) parseInteger(PositiveInt.builder(), "width", reader, -1));
                    break;
                case "frames":
                    position = checkElementOrder("frames", 11, position, false);
                    builder.frames((PositiveInt) parseInteger(PositiveInt.builder(), "frames", reader, -1));
                    break;
                case "duration":
                    position = checkElementOrder("duration", 12, position, false);
                    builder.duration(parseDecimal("duration", reader, -1));
                    break;
                case "pages":
                    position = checkElementOrder("pages", 13, position, false);
                    builder.pages((PositiveInt) parseInteger(PositiveInt.builder(), "pages", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Base64Binary parseBase64Binary(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Base64Binary.Builder builder = Base64Binary.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Boolean parseBoolean(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Boolean.Builder builder = Boolean.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Bundle parseBundle(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Bundle.Builder builder = Bundle.builder();
        builder.setValidating(validating);
        int position = -1;
        int linkElementIndex = 0, entryElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 4, position, false);
                    builder.identifier(parseIdentifier("identifier", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 5, position, false);
                    builder.type((BundleType) parseString(BundleType.builder(), "type", reader, -1));
                    break;
                case "timestamp":
                    position = checkElementOrder("timestamp", 6, position, false);
                    builder.timestamp(parseInstant("timestamp", reader, -1));
                    break;
                case "total":
                    position = checkElementOrder("total", 7, position, false);
                    builder.total((UnsignedInt) parseInteger(UnsignedInt.builder(), "total", reader, -1));
                    break;
                case "link":
                    position = checkElementOrder("link", 8, position, true);
                    builder.link(parseBundleLink("link", reader, linkElementIndex++));
                    break;
                case "entry":
                    position = checkElementOrder("entry", 9, position, true);
                    builder.entry(parseBundleEntry("entry", reader, entryElementIndex++));
                    break;
                case "signature":
                    position = checkElementOrder("signature", 10, position, false);
                    builder.signature(parseSignature("signature", reader, -1));
                    break;
                case "issues":
                    position = checkElementOrder("issues", 11, position, false);
                    builder.issues(parseOperationOutcome("issues", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Bundle.Entry parseBundleEntry(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Bundle.Entry.Builder builder = Bundle.Entry.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, linkElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "link":
                    position = checkElementOrder("link", 2, position, true);
                    builder.link(parseBundleLink("link", reader, linkElementIndex++));
                    break;
                case "fullUrl":
                    position = checkElementOrder("fullUrl", 3, position, false);
                    builder.fullUrl(parseUri("fullUrl", reader, -1));
                    break;
                case "resource":
                    position = checkElementOrder("resource", 4, position, false);
                    builder.resource(parseResource("resource", reader, -1));
                    break;
                case "search":
                    position = checkElementOrder("search", 5, position, false);
                    builder.search(parseBundleEntrySearch("search", reader, -1));
                    break;
                case "request":
                    position = checkElementOrder("request", 6, position, false);
                    builder.request(parseBundleEntryRequest("request", reader, -1));
                    break;
                case "response":
                    position = checkElementOrder("response", 7, position, false);
                    builder.response(parseBundleEntryResponse("response", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Bundle.Entry.Request parseBundleEntryRequest(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Bundle.Entry.Request.Builder builder = Bundle.Entry.Request.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "method":
                    position = checkElementOrder("method", 2, position, false);
                    builder.method((HTTPVerb) parseString(HTTPVerb.builder(), "method", reader, -1));
                    break;
                case "url":
                    position = checkElementOrder("url", 3, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "ifNoneMatch":
                    position = checkElementOrder("ifNoneMatch", 4, position, false);
                    builder.ifNoneMatch(parseString("ifNoneMatch", reader, -1));
                    break;
                case "ifModifiedSince":
                    position = checkElementOrder("ifModifiedSince", 5, position, false);
                    builder.ifModifiedSince(parseInstant("ifModifiedSince", reader, -1));
                    break;
                case "ifMatch":
                    position = checkElementOrder("ifMatch", 6, position, false);
                    builder.ifMatch(parseString("ifMatch", reader, -1));
                    break;
                case "ifNoneExist":
                    position = checkElementOrder("ifNoneExist", 7, position, false);
                    builder.ifNoneExist(parseString("ifNoneExist", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Bundle.Entry.Response parseBundleEntryResponse(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Bundle.Entry.Response.Builder builder = Bundle.Entry.Response.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "status":
                    position = checkElementOrder("status", 2, position, false);
                    builder.status(parseString("status", reader, -1));
                    break;
                case "location":
                    position = checkElementOrder("location", 3, position, false);
                    builder.location(parseUri("location", reader, -1));
                    break;
                case "etag":
                    position = checkElementOrder("etag", 4, position, false);
                    builder.etag(parseString("etag", reader, -1));
                    break;
                case "lastModified":
                    position = checkElementOrder("lastModified", 5, position, false);
                    builder.lastModified(parseInstant("lastModified", reader, -1));
                    break;
                case "outcome":
                    position = checkElementOrder("outcome", 6, position, false);
                    builder.outcome(parseResource("outcome", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Bundle.Entry.Search parseBundleEntrySearch(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Bundle.Entry.Search.Builder builder = Bundle.Entry.Search.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "mode":
                    position = checkElementOrder("mode", 2, position, false);
                    builder.mode((SearchEntryMode) parseString(SearchEntryMode.builder(), "mode", reader, -1));
                    break;
                case "score":
                    position = checkElementOrder("score", 3, position, false);
                    builder.score(parseDecimal("score", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Bundle.Link parseBundleLink(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Bundle.Link.Builder builder = Bundle.Link.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "relation":
                    position = checkElementOrder("relation", 2, position, false);
                    builder.relation((Code) parseString(Code.builder(), "relation", reader, -1));
                    break;
                case "url":
                    position = checkElementOrder("url", 3, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement parseCapabilityStatement(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Builder builder = CapabilityStatement.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, identifierElementIndex = 0, contactElementIndex = 0, useContextElementIndex = 0, jurisdictionElementIndex = 0, instantiatesElementIndex = 0, importsElementIndex = 0, formatElementIndex = 0, patchFormatElementIndex = 0, acceptLanguageElementIndex = 0, implementationGuideElementIndex = 0, restElementIndex = 0, messagingElementIndex = 0, documentElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "url":
                    position = checkElementOrder("url", 8, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 9, position, true);
                    builder.identifier(parseIdentifier("identifier", reader, identifierElementIndex++));
                    break;
                case "version":
                    position = checkElementOrder("version", 10, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "versionAlgorithmString":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseString("versionAlgorithmString", reader, -1));
                    break;
                case "versionAlgorithmCoding":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseCoding("versionAlgorithmCoding", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 12, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 13, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "status":
                    position = checkElementOrder("status", 14, position, false);
                    builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", reader, -1));
                    break;
                case "experimental":
                    position = checkElementOrder("experimental", 15, position, false);
                    builder.experimental(parseBoolean("experimental", reader, -1));
                    break;
                case "date":
                    position = checkElementOrder("date", 16, position, false);
                    builder.date(parseDateTime("date", reader, -1));
                    break;
                case "publisher":
                    position = checkElementOrder("publisher", 17, position, false);
                    builder.publisher(parseString("publisher", reader, -1));
                    break;
                case "contact":
                    position = checkElementOrder("contact", 18, position, true);
                    builder.contact(parseContactDetail("contact", reader, contactElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 19, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "useContext":
                    position = checkElementOrder("useContext", 20, position, true);
                    builder.useContext(parseUsageContext("useContext", reader, useContextElementIndex++));
                    break;
                case "jurisdiction":
                    position = checkElementOrder("jurisdiction", 21, position, true);
                    builder.jurisdiction(parseCodeableConcept("jurisdiction", reader, jurisdictionElementIndex++));
                    break;
                case "purpose":
                    position = checkElementOrder("purpose", 22, position, false);
                    builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", reader, -1));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 23, position, false);
                    builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", reader, -1));
                    break;
                case "copyrightLabel":
                    position = checkElementOrder("copyrightLabel", 24, position, false);
                    builder.copyrightLabel(parseString("copyrightLabel", reader, -1));
                    break;
                case "kind":
                    position = checkElementOrder("kind", 25, position, false);
                    builder.kind((CapabilityStatementKind) parseString(CapabilityStatementKind.builder(), "kind", reader, -1));
                    break;
                case "instantiates":
                    position = checkElementOrder("instantiates", 26, position, true);
                    builder.instantiates((Canonical) parseUri(Canonical.builder(), "instantiates", reader, instantiatesElementIndex++));
                    break;
                case "imports":
                    position = checkElementOrder("imports", 27, position, true);
                    builder.imports((Canonical) parseUri(Canonical.builder(), "imports", reader, importsElementIndex++));
                    break;
                case "software":
                    position = checkElementOrder("software", 28, position, false);
                    builder.software(parseCapabilityStatementSoftware("software", reader, -1));
                    break;
                case "implementation":
                    position = checkElementOrder("implementation", 29, position, false);
                    builder.implementation(parseCapabilityStatementImplementation("implementation", reader, -1));
                    break;
                case "fhirVersion":
                    position = checkElementOrder("fhirVersion", 30, position, false);
                    builder.fhirVersion((FHIRVersion) parseString(FHIRVersion.builder(), "fhirVersion", reader, -1));
                    break;
                case "format":
                    position = checkElementOrder("format", 31, position, true);
                    builder.format((Code) parseString(Code.builder(), "format", reader, formatElementIndex++));
                    break;
                case "patchFormat":
                    position = checkElementOrder("patchFormat", 32, position, true);
                    builder.patchFormat((Code) parseString(Code.builder(), "patchFormat", reader, patchFormatElementIndex++));
                    break;
                case "acceptLanguage":
                    position = checkElementOrder("acceptLanguage", 33, position, true);
                    builder.acceptLanguage((Code) parseString(Code.builder(), "acceptLanguage", reader, acceptLanguageElementIndex++));
                    break;
                case "implementationGuide":
                    position = checkElementOrder("implementationGuide", 34, position, true);
                    builder.implementationGuide((Canonical) parseUri(Canonical.builder(), "implementationGuide", reader, implementationGuideElementIndex++));
                    break;
                case "rest":
                    position = checkElementOrder("rest", 35, position, true);
                    builder.rest(parseCapabilityStatementRest("rest", reader, restElementIndex++));
                    break;
                case "messaging":
                    position = checkElementOrder("messaging", 36, position, true);
                    builder.messaging(parseCapabilityStatementMessaging("messaging", reader, messagingElementIndex++));
                    break;
                case "document":
                    position = checkElementOrder("document", 37, position, true);
                    builder.document(parseCapabilityStatementDocument("document", reader, documentElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Document parseCapabilityStatementDocument(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Document.Builder builder = CapabilityStatement.Document.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "mode":
                    position = checkElementOrder("mode", 2, position, false);
                    builder.mode((DocumentMode) parseString(DocumentMode.builder(), "mode", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 3, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                case "profile":
                    position = checkElementOrder("profile", 4, position, false);
                    builder.profile((Canonical) parseUri(Canonical.builder(), "profile", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Implementation parseCapabilityStatementImplementation(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Implementation.Builder builder = CapabilityStatement.Implementation.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 2, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "url":
                    position = checkElementOrder("url", 3, position, false);
                    builder.url((Url) parseUri(Url.builder(), "url", reader, -1));
                    break;
                case "custodian":
                    position = checkElementOrder("custodian", 4, position, false);
                    builder.custodian(parseReference("custodian", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Messaging parseCapabilityStatementMessaging(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Messaging.Builder builder = CapabilityStatement.Messaging.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, endpointElementIndex = 0, supportedMessageElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "endpoint":
                    position = checkElementOrder("endpoint", 2, position, true);
                    builder.endpoint(parseCapabilityStatementMessagingEndpoint("endpoint", reader, endpointElementIndex++));
                    break;
                case "reliableCache":
                    position = checkElementOrder("reliableCache", 3, position, false);
                    builder.reliableCache((UnsignedInt) parseInteger(UnsignedInt.builder(), "reliableCache", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 4, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                case "supportedMessage":
                    position = checkElementOrder("supportedMessage", 5, position, true);
                    builder.supportedMessage(parseCapabilityStatementMessagingSupportedMessage("supportedMessage", reader, supportedMessageElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Messaging.Endpoint parseCapabilityStatementMessagingEndpoint(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Messaging.Endpoint.Builder builder = CapabilityStatement.Messaging.Endpoint.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "protocol":
                    position = checkElementOrder("protocol", 2, position, false);
                    builder.protocol(parseCoding("protocol", reader, -1));
                    break;
                case "address":
                    position = checkElementOrder("address", 3, position, false);
                    builder.address((Url) parseUri(Url.builder(), "address", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Messaging.SupportedMessage parseCapabilityStatementMessagingSupportedMessage(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Messaging.SupportedMessage.Builder builder = CapabilityStatement.Messaging.SupportedMessage.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "mode":
                    position = checkElementOrder("mode", 2, position, false);
                    builder.mode((EventCapabilityMode) parseString(EventCapabilityMode.builder(), "mode", reader, -1));
                    break;
                case "definition":
                    position = checkElementOrder("definition", 3, position, false);
                    builder.definition((Canonical) parseUri(Canonical.builder(), "definition", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Rest parseCapabilityStatementRest(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Rest.Builder builder = CapabilityStatement.Rest.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, resourceElementIndex = 0, interactionElementIndex = 0, searchParamElementIndex = 0, operationElementIndex = 0, compartmentElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "mode":
                    position = checkElementOrder("mode", 2, position, false);
                    builder.mode((RestfulCapabilityMode) parseString(RestfulCapabilityMode.builder(), "mode", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 3, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                case "security":
                    position = checkElementOrder("security", 4, position, false);
                    builder.security(parseCapabilityStatementRestSecurity("security", reader, -1));
                    break;
                case "resource":
                    position = checkElementOrder("resource", 5, position, true);
                    builder.resource(parseCapabilityStatementRestResource("resource", reader, resourceElementIndex++));
                    break;
                case "interaction":
                    position = checkElementOrder("interaction", 6, position, true);
                    builder.interaction(parseCapabilityStatementRestInteraction("interaction", reader, interactionElementIndex++));
                    break;
                case "searchParam":
                    position = checkElementOrder("searchParam", 7, position, true);
                    builder.searchParam(parseCapabilityStatementRestResourceSearchParam("searchParam", reader, searchParamElementIndex++));
                    break;
                case "operation":
                    position = checkElementOrder("operation", 8, position, true);
                    builder.operation(parseCapabilityStatementRestResourceOperation("operation", reader, operationElementIndex++));
                    break;
                case "compartment":
                    position = checkElementOrder("compartment", 9, position, true);
                    builder.compartment((Canonical) parseUri(Canonical.builder(), "compartment", reader, compartmentElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Rest.Interaction parseCapabilityStatementRestInteraction(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Rest.Interaction.Builder builder = CapabilityStatement.Rest.Interaction.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((SystemRestfulInteraction) parseString(SystemRestfulInteraction.builder(), "code", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 3, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Rest.Resource parseCapabilityStatementRestResource(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Rest.Resource.Builder builder = CapabilityStatement.Rest.Resource.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, supportedProfileElementIndex = 0, interactionElementIndex = 0, referencePolicyElementIndex = 0, searchIncludeElementIndex = 0, searchRevIncludeElementIndex = 0, searchParamElementIndex = 0, operationElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "type":
                    position = checkElementOrder("type", 2, position, false);
                    builder.type((ResourceTypeCode) parseString(ResourceTypeCode.builder(), "type", reader, -1));
                    break;
                case "profile":
                    position = checkElementOrder("profile", 3, position, false);
                    builder.profile((Canonical) parseUri(Canonical.builder(), "profile", reader, -1));
                    break;
                case "supportedProfile":
                    position = checkElementOrder("supportedProfile", 4, position, true);
                    builder.supportedProfile((Canonical) parseUri(Canonical.builder(), "supportedProfile", reader, supportedProfileElementIndex++));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 5, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                case "interaction":
                    position = checkElementOrder("interaction", 6, position, true);
                    builder.interaction(parseCapabilityStatementRestResourceInteraction("interaction", reader, interactionElementIndex++));
                    break;
                case "versioning":
                    position = checkElementOrder("versioning", 7, position, false);
                    builder.versioning((ResourceVersionPolicy) parseString(ResourceVersionPolicy.builder(), "versioning", reader, -1));
                    break;
                case "readHistory":
                    position = checkElementOrder("readHistory", 8, position, false);
                    builder.readHistory(parseBoolean("readHistory", reader, -1));
                    break;
                case "updateCreate":
                    position = checkElementOrder("updateCreate", 9, position, false);
                    builder.updateCreate(parseBoolean("updateCreate", reader, -1));
                    break;
                case "conditionalCreate":
                    position = checkElementOrder("conditionalCreate", 10, position, false);
                    builder.conditionalCreate(parseBoolean("conditionalCreate", reader, -1));
                    break;
                case "conditionalRead":
                    position = checkElementOrder("conditionalRead", 11, position, false);
                    builder.conditionalRead((ConditionalReadStatus) parseString(ConditionalReadStatus.builder(), "conditionalRead", reader, -1));
                    break;
                case "conditionalUpdate":
                    position = checkElementOrder("conditionalUpdate", 12, position, false);
                    builder.conditionalUpdate(parseBoolean("conditionalUpdate", reader, -1));
                    break;
                case "conditionalPatch":
                    position = checkElementOrder("conditionalPatch", 13, position, false);
                    builder.conditionalPatch(parseBoolean("conditionalPatch", reader, -1));
                    break;
                case "conditionalDelete":
                    position = checkElementOrder("conditionalDelete", 14, position, false);
                    builder.conditionalDelete((ConditionalDeleteStatus) parseString(ConditionalDeleteStatus.builder(), "conditionalDelete", reader, -1));
                    break;
                case "referencePolicy":
                    position = checkElementOrder("referencePolicy", 15, position, true);
                    builder.referencePolicy((ReferenceHandlingPolicy) parseString(ReferenceHandlingPolicy.builder(), "referencePolicy", reader, referencePolicyElementIndex++));
                    break;
                case "searchInclude":
                    position = checkElementOrder("searchInclude", 16, position, true);
                    builder.searchInclude(parseString("searchInclude", reader, searchIncludeElementIndex++));
                    break;
                case "searchRevInclude":
                    position = checkElementOrder("searchRevInclude", 17, position, true);
                    builder.searchRevInclude(parseString("searchRevInclude", reader, searchRevIncludeElementIndex++));
                    break;
                case "searchParam":
                    position = checkElementOrder("searchParam", 18, position, true);
                    builder.searchParam(parseCapabilityStatementRestResourceSearchParam("searchParam", reader, searchParamElementIndex++));
                    break;
                case "operation":
                    position = checkElementOrder("operation", 19, position, true);
                    builder.operation(parseCapabilityStatementRestResourceOperation("operation", reader, operationElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Rest.Resource.Interaction parseCapabilityStatementRestResourceInteraction(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Rest.Resource.Interaction.Builder builder = CapabilityStatement.Rest.Resource.Interaction.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((TypeRestfulInteraction) parseString(TypeRestfulInteraction.builder(), "code", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 3, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Rest.Resource.Operation parseCapabilityStatementRestResourceOperation(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Rest.Resource.Operation.Builder builder = CapabilityStatement.Rest.Resource.Operation.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "definition":
                    position = checkElementOrder("definition", 3, position, false);
                    builder.definition((Canonical) parseUri(Canonical.builder(), "definition", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 4, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Rest.Resource.SearchParam parseCapabilityStatementRestResourceSearchParam(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Rest.Resource.SearchParam.Builder builder = CapabilityStatement.Rest.Resource.SearchParam.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "definition":
                    position = checkElementOrder("definition", 3, position, false);
                    builder.definition((Canonical) parseUri(Canonical.builder(), "definition", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 4, position, false);
                    builder.type((SearchParamType) parseString(SearchParamType.builder(), "type", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 5, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Rest.Security parseCapabilityStatementRestSecurity(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Rest.Security.Builder builder = CapabilityStatement.Rest.Security.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, serviceElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "cors":
                    position = checkElementOrder("cors", 2, position, false);
                    builder.cors(parseBoolean("cors", reader, -1));
                    break;
                case "service":
                    position = checkElementOrder("service", 3, position, true);
                    builder.service(parseCodeableConcept("service", reader, serviceElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 4, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CapabilityStatement.Software parseCapabilityStatementSoftware(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CapabilityStatement.Software.Builder builder = CapabilityStatement.Software.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "version":
                    position = checkElementOrder("version", 3, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "releaseDate":
                    position = checkElementOrder("releaseDate", 4, position, false);
                    builder.releaseDate(parseDateTime("releaseDate", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeSystem parseCodeSystem(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeSystem.Builder builder = CodeSystem.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, identifierElementIndex = 0, contactElementIndex = 0, useContextElementIndex = 0, jurisdictionElementIndex = 0, topicElementIndex = 0, authorElementIndex = 0, editorElementIndex = 0, reviewerElementIndex = 0, endorserElementIndex = 0, relatedArtifactElementIndex = 0, filterElementIndex = 0, propertyElementIndex = 0, conceptElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "url":
                    position = checkElementOrder("url", 8, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 9, position, true);
                    builder.identifier(parseIdentifier("identifier", reader, identifierElementIndex++));
                    break;
                case "version":
                    position = checkElementOrder("version", 10, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "versionAlgorithmString":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseString("versionAlgorithmString", reader, -1));
                    break;
                case "versionAlgorithmCoding":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseCoding("versionAlgorithmCoding", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 12, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 13, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "status":
                    position = checkElementOrder("status", 14, position, false);
                    builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", reader, -1));
                    break;
                case "experimental":
                    position = checkElementOrder("experimental", 15, position, false);
                    builder.experimental(parseBoolean("experimental", reader, -1));
                    break;
                case "date":
                    position = checkElementOrder("date", 16, position, false);
                    builder.date(parseDateTime("date", reader, -1));
                    break;
                case "publisher":
                    position = checkElementOrder("publisher", 17, position, false);
                    builder.publisher(parseString("publisher", reader, -1));
                    break;
                case "contact":
                    position = checkElementOrder("contact", 18, position, true);
                    builder.contact(parseContactDetail("contact", reader, contactElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 19, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "useContext":
                    position = checkElementOrder("useContext", 20, position, true);
                    builder.useContext(parseUsageContext("useContext", reader, useContextElementIndex++));
                    break;
                case "jurisdiction":
                    position = checkElementOrder("jurisdiction", 21, position, true);
                    builder.jurisdiction(parseCodeableConcept("jurisdiction", reader, jurisdictionElementIndex++));
                    break;
                case "purpose":
                    position = checkElementOrder("purpose", 22, position, false);
                    builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", reader, -1));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 23, position, false);
                    builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", reader, -1));
                    break;
                case "copyrightLabel":
                    position = checkElementOrder("copyrightLabel", 24, position, false);
                    builder.copyrightLabel(parseString("copyrightLabel", reader, -1));
                    break;
                case "approvalDate":
                    position = checkElementOrder("approvalDate", 25, position, false);
                    builder.approvalDate(parseDate("approvalDate", reader, -1));
                    break;
                case "lastReviewDate":
                    position = checkElementOrder("lastReviewDate", 26, position, false);
                    builder.lastReviewDate(parseDate("lastReviewDate", reader, -1));
                    break;
                case "effectivePeriod":
                    position = checkElementOrder("effectivePeriod", 27, position, false);
                    builder.effectivePeriod(parsePeriod("effectivePeriod", reader, -1));
                    break;
                case "topic":
                    position = checkElementOrder("topic", 28, position, true);
                    builder.topic(parseCodeableConcept("topic", reader, topicElementIndex++));
                    break;
                case "author":
                    position = checkElementOrder("author", 29, position, true);
                    builder.author(parseContactDetail("author", reader, authorElementIndex++));
                    break;
                case "editor":
                    position = checkElementOrder("editor", 30, position, true);
                    builder.editor(parseContactDetail("editor", reader, editorElementIndex++));
                    break;
                case "reviewer":
                    position = checkElementOrder("reviewer", 31, position, true);
                    builder.reviewer(parseContactDetail("reviewer", reader, reviewerElementIndex++));
                    break;
                case "endorser":
                    position = checkElementOrder("endorser", 32, position, true);
                    builder.endorser(parseContactDetail("endorser", reader, endorserElementIndex++));
                    break;
                case "relatedArtifact":
                    position = checkElementOrder("relatedArtifact", 33, position, true);
                    builder.relatedArtifact(parseRelatedArtifact("relatedArtifact", reader, relatedArtifactElementIndex++));
                    break;
                case "caseSensitive":
                    position = checkElementOrder("caseSensitive", 34, position, false);
                    builder.caseSensitive(parseBoolean("caseSensitive", reader, -1));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 35, position, false);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, -1));
                    break;
                case "hierarchyMeaning":
                    position = checkElementOrder("hierarchyMeaning", 36, position, false);
                    builder.hierarchyMeaning((CodeSystemHierarchyMeaning) parseString(CodeSystemHierarchyMeaning.builder(), "hierarchyMeaning", reader, -1));
                    break;
                case "compositional":
                    position = checkElementOrder("compositional", 37, position, false);
                    builder.compositional(parseBoolean("compositional", reader, -1));
                    break;
                case "versionNeeded":
                    position = checkElementOrder("versionNeeded", 38, position, false);
                    builder.versionNeeded(parseBoolean("versionNeeded", reader, -1));
                    break;
                case "content":
                    position = checkElementOrder("content", 39, position, false);
                    builder.content((CodeSystemContentMode) parseString(CodeSystemContentMode.builder(), "content", reader, -1));
                    break;
                case "supplements":
                    position = checkElementOrder("supplements", 40, position, false);
                    builder.supplements((Canonical) parseUri(Canonical.builder(), "supplements", reader, -1));
                    break;
                case "count":
                    position = checkElementOrder("count", 41, position, false);
                    builder.count((UnsignedInt) parseInteger(UnsignedInt.builder(), "count", reader, -1));
                    break;
                case "filter":
                    position = checkElementOrder("filter", 42, position, true);
                    builder.filter(parseCodeSystemFilter("filter", reader, filterElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 43, position, true);
                    builder.property(parseCodeSystemProperty("property", reader, propertyElementIndex++));
                    break;
                case "concept":
                    position = checkElementOrder("concept", 44, position, true);
                    builder.concept(parseCodeSystemConcept("concept", reader, conceptElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeSystem.Concept parseCodeSystemConcept(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeSystem.Concept.Builder builder = CodeSystem.Concept.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, designationElementIndex = 0, propertyElementIndex = 0, conceptElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 3, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                case "definition":
                    position = checkElementOrder("definition", 4, position, false);
                    builder.definition(parseString("definition", reader, -1));
                    break;
                case "designation":
                    position = checkElementOrder("designation", 5, position, true);
                    builder.designation(parseCodeSystemConceptDesignation("designation", reader, designationElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 6, position, true);
                    builder.property(parseCodeSystemConceptProperty("property", reader, propertyElementIndex++));
                    break;
                case "concept":
                    position = checkElementOrder("concept", 7, position, true);
                    builder.concept(parseCodeSystemConcept("concept", reader, conceptElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeSystem.Concept.Designation parseCodeSystemConceptDesignation(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeSystem.Concept.Designation.Builder builder = CodeSystem.Concept.Designation.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, additionalUseElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "language":
                    position = checkElementOrder("language", 2, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "use":
                    position = checkElementOrder("use", 3, position, false);
                    builder.use(parseCoding("use", reader, -1));
                    break;
                case "additionalUse":
                    position = checkElementOrder("additionalUse", 4, position, true);
                    builder.additionalUse(parseCoding("additionalUse", reader, additionalUseElementIndex++));
                    break;
                case "value":
                    position = checkElementOrder("value", 5, position, false);
                    builder.value(parseString("value", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeSystem.Concept.Property parseCodeSystemConceptProperty(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeSystem.Concept.Property.Builder builder = CodeSystem.Concept.Property.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeSystem.Filter parseCodeSystemFilter(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeSystem.Filter.Builder builder = CodeSystem.Filter.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, operatorElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "description":
                    position = checkElementOrder("description", 3, position, false);
                    builder.description(parseString("description", reader, -1));
                    break;
                case "operator":
                    position = checkElementOrder("operator", 4, position, true);
                    builder.operator((FilterOperator) parseString(FilterOperator.builder(), "operator", reader, operatorElementIndex++));
                    break;
                case "value":
                    position = checkElementOrder("value", 5, position, false);
                    builder.value(parseString("value", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeSystem.Property parseCodeSystemProperty(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeSystem.Property.Builder builder = CodeSystem.Property.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "uri":
                    position = checkElementOrder("uri", 3, position, false);
                    builder.uri(parseUri("uri", reader, -1));
                    break;
                case "description":
                    position = checkElementOrder("description", 4, position, false);
                    builder.description(parseString("description", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 5, position, false);
                    builder.type((PropertyType) parseString(PropertyType.builder(), "type", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeableConcept parseCodeableConcept(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeableConcept.Builder builder = CodeableConcept.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, codingElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "coding":
                    position = checkElementOrder("coding", 1, position, true);
                    builder.coding(parseCoding("coding", reader, codingElementIndex++));
                    break;
                case "text":
                    position = checkElementOrder("text", 2, position, false);
                    builder.text(parseString("text", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private CodeableReference parseCodeableReference(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        CodeableReference.Builder builder = CodeableReference.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "concept":
                    position = checkElementOrder("concept", 1, position, false);
                    builder.concept(parseCodeableConcept("concept", reader, -1));
                    break;
                case "reference":
                    position = checkElementOrder("reference", 2, position, false);
                    builder.reference(parseReference("reference", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Coding parseCoding(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Coding.Builder builder = Coding.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "system":
                    position = checkElementOrder("system", 1, position, false);
                    builder.system(parseUri("system", reader, -1));
                    break;
                case "version":
                    position = checkElementOrder("version", 2, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "code":
                    position = checkElementOrder("code", 3, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 4, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                case "userSelected":
                    position = checkElementOrder("userSelected", 5, position, false);
                    builder.userSelected(parseBoolean("userSelected", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap parseConceptMap(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Builder builder = ConceptMap.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, identifierElementIndex = 0, contactElementIndex = 0, useContextElementIndex = 0, jurisdictionElementIndex = 0, topicElementIndex = 0, authorElementIndex = 0, editorElementIndex = 0, reviewerElementIndex = 0, endorserElementIndex = 0, relatedArtifactElementIndex = 0, propertyElementIndex = 0, additionalAttributeElementIndex = 0, groupElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "url":
                    position = checkElementOrder("url", 8, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 9, position, true);
                    builder.identifier(parseIdentifier("identifier", reader, identifierElementIndex++));
                    break;
                case "version":
                    position = checkElementOrder("version", 10, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "versionAlgorithmString":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseString("versionAlgorithmString", reader, -1));
                    break;
                case "versionAlgorithmCoding":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseCoding("versionAlgorithmCoding", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 12, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 13, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "status":
                    position = checkElementOrder("status", 14, position, false);
                    builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", reader, -1));
                    break;
                case "experimental":
                    position = checkElementOrder("experimental", 15, position, false);
                    builder.experimental(parseBoolean("experimental", reader, -1));
                    break;
                case "date":
                    position = checkElementOrder("date", 16, position, false);
                    builder.date(parseDateTime("date", reader, -1));
                    break;
                case "publisher":
                    position = checkElementOrder("publisher", 17, position, false);
                    builder.publisher(parseString("publisher", reader, -1));
                    break;
                case "contact":
                    position = checkElementOrder("contact", 18, position, true);
                    builder.contact(parseContactDetail("contact", reader, contactElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 19, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "useContext":
                    position = checkElementOrder("useContext", 20, position, true);
                    builder.useContext(parseUsageContext("useContext", reader, useContextElementIndex++));
                    break;
                case "jurisdiction":
                    position = checkElementOrder("jurisdiction", 21, position, true);
                    builder.jurisdiction(parseCodeableConcept("jurisdiction", reader, jurisdictionElementIndex++));
                    break;
                case "purpose":
                    position = checkElementOrder("purpose", 22, position, false);
                    builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", reader, -1));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 23, position, false);
                    builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", reader, -1));
                    break;
                case "copyrightLabel":
                    position = checkElementOrder("copyrightLabel", 24, position, false);
                    builder.copyrightLabel(parseString("copyrightLabel", reader, -1));
                    break;
                case "approvalDate":
                    position = checkElementOrder("approvalDate", 25, position, false);
                    builder.approvalDate(parseDate("approvalDate", reader, -1));
                    break;
                case "lastReviewDate":
                    position = checkElementOrder("lastReviewDate", 26, position, false);
                    builder.lastReviewDate(parseDate("lastReviewDate", reader, -1));
                    break;
                case "effectivePeriod":
                    position = checkElementOrder("effectivePeriod", 27, position, false);
                    builder.effectivePeriod(parsePeriod("effectivePeriod", reader, -1));
                    break;
                case "topic":
                    position = checkElementOrder("topic", 28, position, true);
                    builder.topic(parseCodeableConcept("topic", reader, topicElementIndex++));
                    break;
                case "author":
                    position = checkElementOrder("author", 29, position, true);
                    builder.author(parseContactDetail("author", reader, authorElementIndex++));
                    break;
                case "editor":
                    position = checkElementOrder("editor", 30, position, true);
                    builder.editor(parseContactDetail("editor", reader, editorElementIndex++));
                    break;
                case "reviewer":
                    position = checkElementOrder("reviewer", 31, position, true);
                    builder.reviewer(parseContactDetail("reviewer", reader, reviewerElementIndex++));
                    break;
                case "endorser":
                    position = checkElementOrder("endorser", 32, position, true);
                    builder.endorser(parseContactDetail("endorser", reader, endorserElementIndex++));
                    break;
                case "relatedArtifact":
                    position = checkElementOrder("relatedArtifact", 33, position, true);
                    builder.relatedArtifact(parseRelatedArtifact("relatedArtifact", reader, relatedArtifactElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 34, position, true);
                    builder.property(parseConceptMapProperty("property", reader, propertyElementIndex++));
                    break;
                case "additionalAttribute":
                    position = checkElementOrder("additionalAttribute", 35, position, true);
                    builder.additionalAttribute(parseConceptMapAdditionalAttribute("additionalAttribute", reader, additionalAttributeElementIndex++));
                    break;
                case "sourceScopeUri":
                    position = checkElementOrder("sourceScope[x]", 36, position, false);
                    builder.sourceScope(parseUri("sourceScopeUri", reader, -1));
                    break;
                case "sourceScopeCanonical":
                    position = checkElementOrder("sourceScope[x]", 36, position, false);
                    builder.sourceScope((Canonical) parseUri(Canonical.builder(), "sourceScopeCanonical", reader, -1));
                    break;
                case "targetScopeUri":
                    position = checkElementOrder("targetScope[x]", 37, position, false);
                    builder.targetScope(parseUri("targetScopeUri", reader, -1));
                    break;
                case "targetScopeCanonical":
                    position = checkElementOrder("targetScope[x]", 37, position, false);
                    builder.targetScope((Canonical) parseUri(Canonical.builder(), "targetScopeCanonical", reader, -1));
                    break;
                case "group":
                    position = checkElementOrder("group", 38, position, true);
                    builder.group(parseConceptMapGroup("group", reader, groupElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.AdditionalAttribute parseConceptMapAdditionalAttribute(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.AdditionalAttribute.Builder builder = ConceptMap.AdditionalAttribute.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "uri":
                    position = checkElementOrder("uri", 3, position, false);
                    builder.uri(parseUri("uri", reader, -1));
                    break;
                case "description":
                    position = checkElementOrder("description", 4, position, false);
                    builder.description(parseString("description", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 5, position, false);
                    builder.type((ConceptMapmapAttributeType) parseString(ConceptMapmapAttributeType.builder(), "type", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.Group parseConceptMapGroup(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Group.Builder builder = ConceptMap.Group.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, elementElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "source":
                    position = checkElementOrder("source", 2, position, false);
                    builder.source((Canonical) parseUri(Canonical.builder(), "source", reader, -1));
                    break;
                case "target":
                    position = checkElementOrder("target", 3, position, false);
                    builder.target((Canonical) parseUri(Canonical.builder(), "target", reader, -1));
                    break;
                case "element":
                    position = checkElementOrder("element", 4, position, true);
                    builder.element(parseConceptMapGroupElement("element", reader, elementElementIndex++));
                    break;
                case "unmapped":
                    position = checkElementOrder("unmapped", 5, position, false);
                    builder.unmapped(parseConceptMapGroupUnmapped("unmapped", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.Group.Element parseConceptMapGroupElement(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Group.Element.Builder builder = ConceptMap.Group.Element.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, targetElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 3, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 4, position, false);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, -1));
                    break;
                case "noMap":
                    position = checkElementOrder("noMap", 5, position, false);
                    builder.noMap(parseBoolean("noMap", reader, -1));
                    break;
                case "target":
                    position = checkElementOrder("target", 6, position, true);
                    builder.target(parseConceptMapGroupElementTarget("target", reader, targetElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.Group.Element.Target parseConceptMapGroupElementTarget(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Group.Element.Target.Builder builder = ConceptMap.Group.Element.Target.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, propertyElementIndex = 0, dependsOnElementIndex = 0, productElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 3, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 4, position, false);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, -1));
                    break;
                case "relationship":
                    position = checkElementOrder("relationship", 5, position, false);
                    builder.relationship((ConceptMapRelationship) parseString(ConceptMapRelationship.builder(), "relationship", reader, -1));
                    break;
                case "comment":
                    position = checkElementOrder("comment", 6, position, false);
                    builder.comment(parseString("comment", reader, -1));
                    break;
                case "property":
                    position = checkElementOrder("property", 7, position, true);
                    builder.property(parseConceptMapGroupElementTargetProperty("property", reader, propertyElementIndex++));
                    break;
                case "dependsOn":
                    position = checkElementOrder("dependsOn", 8, position, true);
                    builder.dependsOn(parseConceptMapGroupElementTargetDependsOn("dependsOn", reader, dependsOnElementIndex++));
                    break;
                case "product":
                    position = checkElementOrder("product", 9, position, true);
                    builder.product(parseConceptMapGroupElementTargetDependsOn("product", reader, productElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.Group.Element.Target.DependsOn parseConceptMapGroupElementTargetDependsOn(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Group.Element.Target.DependsOn.Builder builder = ConceptMap.Group.Element.Target.DependsOn.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "attribute":
                    position = checkElementOrder("attribute", 2, position, false);
                    builder.attribute((Code) parseString(Code.builder(), "attribute", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueQuantity":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseQuantity("valueQuantity", reader, -1));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 4, position, false);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.Group.Element.Target.Property parseConceptMapGroupElementTargetProperty(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Group.Element.Target.Property.Builder builder = ConceptMap.Group.Element.Target.Property.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.Group.Unmapped parseConceptMapGroupUnmapped(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Group.Unmapped.Builder builder = ConceptMap.Group.Unmapped.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "mode":
                    position = checkElementOrder("mode", 2, position, false);
                    builder.mode((ConceptMapGroupUnmappedMode) parseString(ConceptMapGroupUnmappedMode.builder(), "mode", reader, -1));
                    break;
                case "code":
                    position = checkElementOrder("code", 3, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 4, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 5, position, false);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, -1));
                    break;
                case "relationship":
                    position = checkElementOrder("relationship", 6, position, false);
                    builder.relationship((UnmappedConceptMapRelationship) parseString(UnmappedConceptMapRelationship.builder(), "relationship", reader, -1));
                    break;
                case "otherMap":
                    position = checkElementOrder("otherMap", 7, position, false);
                    builder.otherMap((Canonical) parseUri(Canonical.builder(), "otherMap", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ConceptMap.Property parseConceptMapProperty(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ConceptMap.Property.Builder builder = ConceptMap.Property.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "uri":
                    position = checkElementOrder("uri", 3, position, false);
                    builder.uri(parseUri("uri", reader, -1));
                    break;
                case "description":
                    position = checkElementOrder("description", 4, position, false);
                    builder.description(parseString("description", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 5, position, false);
                    builder.type((PropertyType) parseString(PropertyType.builder(), "type", reader, -1));
                    break;
                case "system":
                    position = checkElementOrder("system", 6, position, false);
                    builder.system((Canonical) parseUri(Canonical.builder(), "system", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ContactDetail parseContactDetail(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ContactDetail.Builder builder = ContactDetail.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, telecomElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 1, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "telecom":
                    position = checkElementOrder("telecom", 2, position, true);
                    builder.telecom(parseContactPoint("telecom", reader, telecomElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ContactPoint parseContactPoint(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ContactPoint.Builder builder = ContactPoint.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "system":
                    position = checkElementOrder("system", 1, position, false);
                    builder.system((ContactPointSystem) parseString(ContactPointSystem.builder(), "system", reader, -1));
                    break;
                case "value":
                    position = checkElementOrder("value", 2, position, false);
                    builder.value(parseString("value", reader, -1));
                    break;
                case "use":
                    position = checkElementOrder("use", 3, position, false);
                    builder.use((ContactPointUse) parseString(ContactPointUse.builder(), "use", reader, -1));
                    break;
                case "rank":
                    position = checkElementOrder("rank", 4, position, false);
                    builder.rank((PositiveInt) parseInteger(PositiveInt.builder(), "rank", reader, -1));
                    break;
                case "period":
                    position = checkElementOrder("period", 5, position, false);
                    builder.period(parsePeriod("period", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Date parseDate(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Date.Builder builder = Date.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private DateTime parseDateTime(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        DateTime.Builder builder = DateTime.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Decimal parseDecimal(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Decimal.Builder builder = Decimal.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition parseElementDefinition(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Builder builder = ElementDefinition.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, representationElementIndex = 0, codeElementIndex = 0, aliasElementIndex = 0, typeElementIndex = 0, exampleElementIndex = 0, conditionElementIndex = 0, constraintElementIndex = 0, valueAlternativesElementIndex = 0, mappingElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "path":
                    position = checkElementOrder("path", 2, position, false);
                    builder.path(parseString("path", reader, -1));
                    break;
                case "representation":
                    position = checkElementOrder("representation", 3, position, true);
                    builder.representation((PropertyRepresentation) parseString(PropertyRepresentation.builder(), "representation", reader, representationElementIndex++));
                    break;
                case "sliceName":
                    position = checkElementOrder("sliceName", 4, position, false);
                    builder.sliceName(parseString("sliceName", reader, -1));
                    break;
                case "sliceIsConstraining":
                    position = checkElementOrder("sliceIsConstraining", 5, position, false);
                    builder.sliceIsConstraining(parseBoolean("sliceIsConstraining", reader, -1));
                    break;
                case "label":
                    position = checkElementOrder("label", 6, position, false);
                    builder.label(parseString("label", reader, -1));
                    break;
                case "code":
                    position = checkElementOrder("code", 7, position, true);
                    builder.code(parseCoding("code", reader, codeElementIndex++));
                    break;
                case "slicing":
                    position = checkElementOrder("slicing", 8, position, false);
                    builder.slicing(parseElementDefinitionSlicing("slicing", reader, -1));
                    break;
                case "short":
                    position = checkElementOrder("short", 9, position, false);
                    builder._short(parseString("short", reader, -1));
                    break;
                case "definition":
                    position = checkElementOrder("definition", 10, position, false);
                    builder.definition((Markdown) parseString(Markdown.builder(), "definition", reader, -1));
                    break;
                case "comment":
                    position = checkElementOrder("comment", 11, position, false);
                    builder.comment((Markdown) parseString(Markdown.builder(), "comment", reader, -1));
                    break;
                case "requirements":
                    position = checkElementOrder("requirements", 12, position, false);
                    builder.requirements((Markdown) parseString(Markdown.builder(), "requirements", reader, -1));
                    break;
                case "alias":
                    position = checkElementOrder("alias", 13, position, true);
                    builder.alias(parseString("alias", reader, aliasElementIndex++));
                    break;
                case "min":
                    position = checkElementOrder("min", 14, position, false);
                    builder.min((UnsignedInt) parseInteger(UnsignedInt.builder(), "min", reader, -1));
                    break;
                case "max":
                    position = checkElementOrder("max", 15, position, false);
                    builder.max(parseString("max", reader, -1));
                    break;
                case "base":
                    position = checkElementOrder("base", 16, position, false);
                    builder.base(parseElementDefinitionBase("base", reader, -1));
                    break;
                case "contentReference":
                    position = checkElementOrder("contentReference", 17, position, false);
                    builder.contentReference(parseUri("contentReference", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 18, position, true);
                    builder.type(parseElementDefinitionType("type", reader, typeElementIndex++));
                    break;
                    
                case "defaultValueAddress":
                case "defaultValueAge":
                case "defaultValueAnnotation":
                case "defaultValueCount":
                case "defaultValueDistance":
                case "defaultValueDuration":
                case "defaultValueHumanName":
                case "defaultValueMoney":
                case "defaultValueRange":
                case "defaultValueRatio":
                case "defaultValueRatioRange":
                case "defaultValueTiming":
                case "defaultValueDataRequirement":
                case "defaultValueExpression":
                case "defaultValueParameterDefinition":
                case "defaultValueSampledData":                    
                case "defaultValueTriggerDefinition":
                case "defaultValueAvailability":
                case "defaultValueExtendedContactDetail":
                case "defaultValueDosage":
                	return throwUnsupportedElement(localName);

                case "defaultValueBase64Binary":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseBase64Binary("defaultValueBase64Binary", reader, -1));
                    break;
                case "defaultValueBoolean":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseBoolean("defaultValueBoolean", reader, -1));
                    break;
                case "defaultValueCanonical":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((Canonical) parseUri(Canonical.builder(), "defaultValueCanonical", reader, -1));
                    break;
                case "defaultValueCode":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((Code) parseString(Code.builder(), "defaultValueCode", reader, -1));
                    break;
                case "defaultValueDate":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseDate("defaultValueDate", reader, -1));
                    break;
                case "defaultValueDateTime":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseDateTime("defaultValueDateTime", reader, -1));
                    break;
                case "defaultValueDecimal":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseDecimal("defaultValueDecimal", reader, -1));
                    break;
                case "defaultValueId":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((Id) parseString(Id.builder(), "defaultValueId", reader, -1));
                    break;
                case "defaultValueInstant":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseInstant("defaultValueInstant", reader, -1));
                    break;
                case "defaultValueInteger":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseInteger("defaultValueInteger", reader, -1));
                    break;
                case "defaultValueInteger64":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseInteger64("defaultValueInteger64", reader, -1));
                    break;
                case "defaultValueMarkdown":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((Markdown) parseString(Markdown.builder(), "defaultValueMarkdown", reader, -1));
                    break;
                case "defaultValueOid":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((Oid) parseUri(Oid.builder(), "defaultValueOid", reader, -1));
                    break;
                case "defaultValuePositiveInt":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((PositiveInt) parseInteger(PositiveInt.builder(), "defaultValuePositiveInt", reader, -1));
                    break;
                case "defaultValueString":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseString("defaultValueString", reader, -1));
                    break;
                case "defaultValueTime":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseTime("defaultValueTime", reader, -1));
                    break;
                case "defaultValueUnsignedInt":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((UnsignedInt) parseInteger(UnsignedInt.builder(), "defaultValueUnsignedInt", reader, -1));
                    break;
                case "defaultValueUri":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseUri("defaultValueUri", reader, -1));
                    break;
                case "defaultValueUrl":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((Url) parseUri(Url.builder(), "defaultValueUrl", reader, -1));
                    break;
                case "defaultValueUuid":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue((Uuid) parseUri(Uuid.builder(), "defaultValueUuid", reader, -1));
                    break;
                case "defaultValueAttachment":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseAttachment("defaultValueAttachment", reader, -1));
                    break;
                case "defaultValueCodeableConcept":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseCodeableConcept("defaultValueCodeableConcept", reader, -1));
                    break;
                case "defaultValueCodeableReference":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseCodeableReference("defaultValueCodeableReference", reader, -1));
                    break;
                case "defaultValueCoding":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseCoding("defaultValueCoding", reader, -1));
                    break;
                case "defaultValueContactPoint":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseContactPoint("defaultValueContactPoint", reader, -1));
                    break;
                case "defaultValueIdentifier":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseIdentifier("defaultValueIdentifier", reader, -1));
                    break;
                case "defaultValuePeriod":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parsePeriod("defaultValuePeriod", reader, -1));
                    break;
                case "defaultValueQuantity":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseQuantity("defaultValueQuantity", reader, -1));
                    break;
                case "defaultValueReference":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseReference("defaultValueReference", reader, -1));
                    break;
                case "defaultValueSignature":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseSignature("defaultValueSignature", reader, -1));
                    break;
                case "defaultValueContactDetail":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseContactDetail("defaultValueContactDetail", reader, -1));
                    break;
                case "defaultValueRelatedArtifact":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseRelatedArtifact("defaultValueRelatedArtifact", reader, -1));
                    break;
                case "defaultValueUsageContext":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseUsageContext("defaultValueUsageContext", reader, -1));
                    break;
                case "defaultValueMeta":
                    position = checkElementOrder("defaultValue[x]", 19, position, false);
                    builder.defaultValue(parseMeta("defaultValueMeta", reader, -1));
                    break;
                case "meaningWhenMissing":
                    position = checkElementOrder("meaningWhenMissing", 20, position, false);
                    builder.meaningWhenMissing((Markdown) parseString(Markdown.builder(), "meaningWhenMissing", reader, -1));
                    break;
                case "orderMeaning":
                    position = checkElementOrder("orderMeaning", 21, position, false);
                    builder.orderMeaning(parseString("orderMeaning", reader, -1));
                    break;
                    
                case "fixedAddress":
                case "fixedAge":
                case "fixedAnnotation":
                case "fixedCount":
                case "fixedDistance":
                case "fixedDuration":
                case "fixedHumanName":
                case "fixedMoney":
                case "fixedRange":
                case "fixedRatio":
                case "fixedRatioRange":
                case "fixedSampledData":
                case "fixedTiming":
                case "fixedDataRequirement":
                case "fixedExpression":
                case "fixedParameterDefinition":
                case "fixedTriggerDefinition":
                case "fixedAvailability":
                case "fixedExtendedContactDetail":
                case "fixedDosage":
                	return throwUnsupportedElement(localName);
                	
                case "fixedBase64Binary":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseBase64Binary("fixedBase64Binary", reader, -1));
                    break;
                case "fixedBoolean":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseBoolean("fixedBoolean", reader, -1));
                    break;
                case "fixedCanonical":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((Canonical) parseUri(Canonical.builder(), "fixedCanonical", reader, -1));
                    break;
                case "fixedCode":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((Code) parseString(Code.builder(), "fixedCode", reader, -1));
                    break;
                case "fixedDate":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseDate("fixedDate", reader, -1));
                    break;
                case "fixedDateTime":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseDateTime("fixedDateTime", reader, -1));
                    break;
                case "fixedDecimal":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseDecimal("fixedDecimal", reader, -1));
                    break;
                case "fixedId":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((Id) parseString(Id.builder(), "fixedId", reader, -1));
                    break;
                case "fixedInstant":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseInstant("fixedInstant", reader, -1));
                    break;
                case "fixedInteger":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseInteger("fixedInteger", reader, -1));
                    break;
                case "fixedInteger64":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseInteger64("fixedInteger64", reader, -1));
                    break;
                case "fixedMarkdown":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((Markdown) parseString(Markdown.builder(), "fixedMarkdown", reader, -1));
                    break;
                case "fixedOid":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((Oid) parseUri(Oid.builder(), "fixedOid", reader, -1));
                    break;
                case "fixedPositiveInt":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((PositiveInt) parseInteger(PositiveInt.builder(), "fixedPositiveInt", reader, -1));
                    break;
                case "fixedString":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseString("fixedString", reader, -1));
                    break;
                case "fixedTime":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseTime("fixedTime", reader, -1));
                    break;
                case "fixedUnsignedInt":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((UnsignedInt) parseInteger(UnsignedInt.builder(), "fixedUnsignedInt", reader, -1));
                    break;
                case "fixedUri":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseUri("fixedUri", reader, -1));
                    break;
                case "fixedUrl":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((Url) parseUri(Url.builder(), "fixedUrl", reader, -1));
                    break;
                case "fixedUuid":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed((Uuid) parseUri(Uuid.builder(), "fixedUuid", reader, -1));
                    break;
                case "fixedAttachment":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseAttachment("fixedAttachment", reader, -1));
                    break;
                case "fixedCodeableConcept":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseCodeableConcept("fixedCodeableConcept", reader, -1));
                    break;
                case "fixedCodeableReference":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseCodeableReference("fixedCodeableReference", reader, -1));
                    break;
                case "fixedCoding":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseCoding("fixedCoding", reader, -1));
                    break;
                case "fixedContactPoint":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseContactPoint("fixedContactPoint", reader, -1));
                    break;
                case "fixedIdentifier":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseIdentifier("fixedIdentifier", reader, -1));
                    break;
                case "fixedPeriod":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parsePeriod("fixedPeriod", reader, -1));
                    break;
                case "fixedQuantity":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseQuantity("fixedQuantity", reader, -1));
                    break;
                case "fixedReference":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseReference("fixedReference", reader, -1));
                    break;
                case "fixedSignature":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseSignature("fixedSignature", reader, -1));
                    break;
                case "fixedContactDetail":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseContactDetail("fixedContactDetail", reader, -1));
                    break;
                case "fixedRelatedArtifact":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseRelatedArtifact("fixedRelatedArtifact", reader, -1));
                    break;
                case "fixedUsageContext":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseUsageContext("fixedUsageContext", reader, -1));
                    break;
                case "fixedMeta":
                    position = checkElementOrder("fixed[x]", 22, position, false);
                    builder.fixed(parseMeta("fixedMeta", reader, -1));
                    break;

                case "patternAddress":
                case "patternAge":
                case "patternAnnotation":
                case "patternCount":
                case "patternDistance":
                case "patternDuration":
                case "patternHumanName":
                case "patternMoney":
                case "patternRange":
                case "patternRatio":
                case "patternRatioRange":
                case "patternSampledData":
                case "patternTiming":
                case "patternDataRequirement":
                case "patternExpression":
                case "patternParameterDefinition":
                case "patternTriggerDefinition":
                case "patternAvailability":
                case "patternExtendedContactDetail":
                case "patternDosage":
                	return throwUnsupportedElement(localName);
                    
                case "patternBase64Binary":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseBase64Binary("patternBase64Binary", reader, -1));
                    break;
                case "patternBoolean":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseBoolean("patternBoolean", reader, -1));
                    break;
                case "patternCanonical":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((Canonical) parseUri(Canonical.builder(), "patternCanonical", reader, -1));
                    break;
                case "patternCode":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((Code) parseString(Code.builder(), "patternCode", reader, -1));
                    break;
                case "patternDate":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseDate("patternDate", reader, -1));
                    break;
                case "patternDateTime":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseDateTime("patternDateTime", reader, -1));
                    break;
                case "patternDecimal":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseDecimal("patternDecimal", reader, -1));
                    break;
                case "patternId":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((Id) parseString(Id.builder(), "patternId", reader, -1));
                    break;
                case "patternInstant":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseInstant("patternInstant", reader, -1));
                    break;
                case "patternInteger":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseInteger("patternInteger", reader, -1));
                    break;
                case "patternInteger64":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseInteger64("patternInteger64", reader, -1));
                    break;
                case "patternMarkdown":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((Markdown) parseString(Markdown.builder(), "patternMarkdown", reader, -1));
                    break;
                case "patternOid":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((Oid) parseUri(Oid.builder(), "patternOid", reader, -1));
                    break;
                case "patternPositiveInt":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((PositiveInt) parseInteger(PositiveInt.builder(), "patternPositiveInt", reader, -1));
                    break;
                case "patternString":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseString("patternString", reader, -1));
                    break;
                case "patternTime":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseTime("patternTime", reader, -1));
                    break;
                case "patternUnsignedInt":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((UnsignedInt) parseInteger(UnsignedInt.builder(), "patternUnsignedInt", reader, -1));
                    break;
                case "patternUri":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseUri("patternUri", reader, -1));
                    break;
                case "patternUrl":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((Url) parseUri(Url.builder(), "patternUrl", reader, -1));
                    break;
                case "patternUuid":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern((Uuid) parseUri(Uuid.builder(), "patternUuid", reader, -1));
                    break;
                case "patternAttachment":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseAttachment("patternAttachment", reader, -1));
                    break;
                case "patternCodeableConcept":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseCodeableConcept("patternCodeableConcept", reader, -1));
                    break;
                case "patternCodeableReference":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseCodeableReference("patternCodeableReference", reader, -1));
                    break;
                case "patternCoding":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseCoding("patternCoding", reader, -1));
                    break;
                case "patternContactPoint":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseContactPoint("patternContactPoint", reader, -1));
                    break;
                case "patternIdentifier":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseIdentifier("patternIdentifier", reader, -1));
                    break;
                case "patternPeriod":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parsePeriod("patternPeriod", reader, -1));
                    break;
                case "patternQuantity":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseQuantity("patternQuantity", reader, -1));
                    break;
                case "patternReference":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseReference("patternReference", reader, -1));
                    break;
                case "patternSignature":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseSignature("patternSignature", reader, -1));
                    break;
                case "patternContactDetail":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseContactDetail("patternContactDetail", reader, -1));
                    break;
                case "patternRelatedArtifact":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseRelatedArtifact("patternRelatedArtifact", reader, -1));
                    break;
                case "patternUsageContext":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseUsageContext("patternUsageContext", reader, -1));
                    break;
                case "patternMeta":
                    position = checkElementOrder("pattern[x]", 23, position, false);
                    builder.pattern(parseMeta("patternMeta", reader, -1));
                    break;
                case "example":
                    position = checkElementOrder("example", 24, position, true);
                    builder.example(parseElementDefinitionExample("example", reader, exampleElementIndex++));
                    break;
                case "minValueDate":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseDate("minValueDate", reader, -1));
                    break;
                case "minValueDateTime":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseDateTime("minValueDateTime", reader, -1));
                    break;
                case "minValueInstant":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseInstant("minValueInstant", reader, -1));
                    break;
                case "minValueTime":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseTime("minValueTime", reader, -1));
                    break;
                case "minValueDecimal":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseDecimal("minValueDecimal", reader, -1));
                    break;
                case "minValueInteger":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseInteger("minValueInteger", reader, -1));
                    break;
                case "minValueInteger64":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseInteger64("minValueInteger64", reader, -1));
                    break;
                case "minValuePositiveInt":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue((PositiveInt) parseInteger(PositiveInt.builder(), "minValuePositiveInt", reader, -1));
                    break;
                case "minValueUnsignedInt":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue((UnsignedInt) parseInteger(UnsignedInt.builder(), "minValueUnsignedInt", reader, -1));
                    break;
                case "minValueQuantity":
                    position = checkElementOrder("minValue[x]", 25, position, false);
                    builder.minValue(parseQuantity("minValueQuantity", reader, -1));
                    break;
                case "maxValueDate":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseDate("maxValueDate", reader, -1));
                    break;
                case "maxValueDateTime":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseDateTime("maxValueDateTime", reader, -1));
                    break;
                case "maxValueInstant":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseInstant("maxValueInstant", reader, -1));
                    break;
                case "maxValueTime":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseTime("maxValueTime", reader, -1));
                    break;
                case "maxValueDecimal":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseDecimal("maxValueDecimal", reader, -1));
                    break;
                case "maxValueInteger":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseInteger("maxValueInteger", reader, -1));
                    break;
                case "maxValueInteger64":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseInteger64("maxValueInteger64", reader, -1));
                    break;
                case "maxValuePositiveInt":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue((PositiveInt) parseInteger(PositiveInt.builder(), "maxValuePositiveInt", reader, -1));
                    break;
                case "maxValueUnsignedInt":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue((UnsignedInt) parseInteger(UnsignedInt.builder(), "maxValueUnsignedInt", reader, -1));
                    break;
                case "maxValueQuantity":
                    position = checkElementOrder("maxValue[x]", 26, position, false);
                    builder.maxValue(parseQuantity("maxValueQuantity", reader, -1));
                    break;
                case "maxLength":
                    position = checkElementOrder("maxLength", 27, position, false);
                    builder.maxLength(parseInteger("maxLength", reader, -1));
                    break;
                case "condition":
                    position = checkElementOrder("condition", 28, position, true);
                    builder.condition((Id) parseString(Id.builder(), "condition", reader, conditionElementIndex++));
                    break;
                case "constraint":
                    position = checkElementOrder("constraint", 29, position, true);
                    builder.constraint(parseElementDefinitionConstraint("constraint", reader, constraintElementIndex++));
                    break;
                case "mustHaveValue":
                    position = checkElementOrder("mustHaveValue", 30, position, false);
                    builder.mustHaveValue(parseBoolean("mustHaveValue", reader, -1));
                    break;
                case "valueAlternatives":
                    position = checkElementOrder("valueAlternatives", 31, position, true);
                    builder.valueAlternatives((Canonical) parseUri(Canonical.builder(), "valueAlternatives", reader, valueAlternativesElementIndex++));
                    break;
                case "mustSupport":
                    position = checkElementOrder("mustSupport", 32, position, false);
                    builder.mustSupport(parseBoolean("mustSupport", reader, -1));
                    break;
                case "isModifier":
                    position = checkElementOrder("isModifier", 33, position, false);
                    builder.isModifier(parseBoolean("isModifier", reader, -1));
                    break;
                case "isModifierReason":
                    position = checkElementOrder("isModifierReason", 34, position, false);
                    builder.isModifierReason(parseString("isModifierReason", reader, -1));
                    break;
                case "isSummary":
                    position = checkElementOrder("isSummary", 35, position, false);
                    builder.isSummary(parseBoolean("isSummary", reader, -1));
                    break;
                case "binding":
                    position = checkElementOrder("binding", 36, position, false);
                    builder.binding(parseElementDefinitionBinding("binding", reader, -1));
                    break;
                case "mapping":
                    position = checkElementOrder("mapping", 37, position, true);
                    builder.mapping(parseElementDefinitionMapping("mapping", reader, mappingElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Base parseElementDefinitionBase(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Base.Builder builder = ElementDefinition.Base.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "path":
                    position = checkElementOrder("path", 2, position, false);
                    builder.path(parseString("path", reader, -1));
                    break;
                case "min":
                    position = checkElementOrder("min", 3, position, false);
                    builder.min((UnsignedInt) parseInteger(UnsignedInt.builder(), "min", reader, -1));
                    break;
                case "max":
                    position = checkElementOrder("max", 4, position, false);
                    builder.max(parseString("max", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Binding parseElementDefinitionBinding(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Binding.Builder builder = ElementDefinition.Binding.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, additionalElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "strength":
                    position = checkElementOrder("strength", 2, position, false);
                    builder.strength((BindingStrength) parseString(BindingStrength.builder(), "strength", reader, -1));
                    break;
                case "description":
                    position = checkElementOrder("description", 3, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 4, position, false);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, -1));
                    break;
                case "additional":
                    position = checkElementOrder("additional", 5, position, true);
                    builder.additional(parseElement("additional", reader, additionalElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Constraint parseElementDefinitionConstraint(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Constraint.Builder builder = ElementDefinition.Constraint.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "key":
                    position = checkElementOrder("key", 2, position, false);
                    builder.key((Id) parseString(Id.builder(), "key", reader, -1));
                    break;
                case "requirements":
                    position = checkElementOrder("requirements", 3, position, false);
                    builder.requirements((Markdown) parseString(Markdown.builder(), "requirements", reader, -1));
                    break;
                case "severity":
                    position = checkElementOrder("severity", 4, position, false);
                    builder.severity((ConstraintSeverity) parseString(ConstraintSeverity.builder(), "severity", reader, -1));
                    break;
                case "suppress":
                    position = checkElementOrder("suppress", 5, position, false);
                    builder.suppress(parseBoolean("suppress", reader, -1));
                    break;
                case "human":
                    position = checkElementOrder("human", 6, position, false);
                    builder.human(parseString("human", reader, -1));
                    break;
                case "expression":
                    position = checkElementOrder("expression", 7, position, false);
                    builder.expression(parseString("expression", reader, -1));
                    break;
                case "source":
                    position = checkElementOrder("source", 8, position, false);
                    builder.source((Canonical) parseUri(Canonical.builder(), "source", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Example parseElementDefinitionExample(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Example.Builder builder = ElementDefinition.Example.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {

                case "valueAddress":
                case "valueAge":
                case "valueAnnotation":
                case "valueCount":
                case "valueDistance":
                case "valueDuration":
                case "valueHumanName":
                case "valueMoney":
                case "valueRange":
                case "valueRatio":
                case "valueRatioRange":
                case "valueSampledData":
                case "valueTiming":
                case "valueDataRequirement":
                case "valueExpression":
                case "valueParameterDefinition":
                case "valueTriggerDefinition":
                case "valueAvailability":
                case "valueExtendedContactDetail":
                case "valueDosage":
                	return throwUnsupportedElement(localName);
                
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "label":
                    position = checkElementOrder("label", 2, position, false);
                    builder.label(parseString("label", reader, -1));
                    break;
                case "valueBase64Binary":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBase64Binary("valueBase64Binary", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueCanonical":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Canonical) parseUri(Canonical.builder(), "valueCanonical", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueDate":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDate("valueDate", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                case "valueId":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Id) parseString(Id.builder(), "valueId", reader, -1));
                    break;
                case "valueInstant":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInstant("valueInstant", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueInteger64":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger64("valueInteger64", reader, -1));
                    break;
                case "valueMarkdown":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Markdown) parseString(Markdown.builder(), "valueMarkdown", reader, -1));
                    break;
                case "valueOid":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Oid) parseUri(Oid.builder(), "valueOid", reader, -1));
                    break;
                case "valuePositiveInt":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((PositiveInt) parseInteger(PositiveInt.builder(), "valuePositiveInt", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseTime("valueTime", reader, -1));
                    break;
                case "valueUnsignedInt":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((UnsignedInt) parseInteger(UnsignedInt.builder(), "valueUnsignedInt", reader, -1));
                    break;
                case "valueUri":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseUri("valueUri", reader, -1));
                    break;
                case "valueUrl":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Url) parseUri(Url.builder(), "valueUrl", reader, -1));
                    break;
                case "valueUuid":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Uuid) parseUri(Uuid.builder(), "valueUuid", reader, -1));
                    break;
                case "valueAttachment":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseAttachment("valueAttachment", reader, -1));
                    break;
                case "valueCodeableConcept":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCodeableConcept("valueCodeableConcept", reader, -1));
                    break;
                case "valueCodeableReference":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCodeableReference("valueCodeableReference", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueContactPoint":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseContactPoint("valueContactPoint", reader, -1));
                    break;
                case "valueIdentifier":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseIdentifier("valueIdentifier", reader, -1));
                    break;
                case "valuePeriod":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parsePeriod("valuePeriod", reader, -1));
                    break;
                case "valueQuantity":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseQuantity("valueQuantity", reader, -1));
                    break;
                case "valueReference":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseReference("valueReference", reader, -1));
                    break;
                case "valueSignature":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseSignature("valueSignature", reader, -1));
                    break;
                case "valueContactDetail":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseContactDetail("valueContactDetail", reader, -1));
                    break;
                case "valueRelatedArtifact":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseRelatedArtifact("valueRelatedArtifact", reader, -1));
                    break;
                case "valueUsageContext":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseUsageContext("valueUsageContext", reader, -1));
                    break;
                case "valueMeta":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseMeta("valueMeta", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Mapping parseElementDefinitionMapping(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Mapping.Builder builder = ElementDefinition.Mapping.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "identity":
                    position = checkElementOrder("identity", 2, position, false);
                    builder.identity((Id) parseString(Id.builder(), "identity", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "map":
                    position = checkElementOrder("map", 4, position, false);
                    builder.map(parseString("map", reader, -1));
                    break;
                case "comment":
                    position = checkElementOrder("comment", 5, position, false);
                    builder.comment((Markdown) parseString(Markdown.builder(), "comment", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Slicing parseElementDefinitionSlicing(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Slicing.Builder builder = ElementDefinition.Slicing.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, discriminatorElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "discriminator":
                    position = checkElementOrder("discriminator", 2, position, true);
                    builder.discriminator(parseElementDefinitionSlicingDiscriminator("discriminator", reader, discriminatorElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 3, position, false);
                    builder.description(parseString("description", reader, -1));
                    break;
                case "ordered":
                    position = checkElementOrder("ordered", 4, position, false);
                    builder.ordered(parseBoolean("ordered", reader, -1));
                    break;
                case "rules":
                    position = checkElementOrder("rules", 5, position, false);
                    builder.rules((SlicingRules) parseString(SlicingRules.builder(), "rules", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Slicing.Discriminator parseElementDefinitionSlicingDiscriminator(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Slicing.Discriminator.Builder builder = ElementDefinition.Slicing.Discriminator.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "type":
                    position = checkElementOrder("type", 2, position, false);
                    builder.type((DiscriminatorType) parseString(DiscriminatorType.builder(), "type", reader, -1));
                    break;
                case "path":
                    position = checkElementOrder("path", 3, position, false);
                    builder.path(parseString("path", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ElementDefinition.Type parseElementDefinitionType(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ElementDefinition.Type.Builder builder = ElementDefinition.Type.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, profileElementIndex = 0, targetProfileElementIndex = 0, aggregationElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code(parseUri("code", reader, -1));
                    break;
                case "profile":
                    position = checkElementOrder("profile", 3, position, true);
                    builder.profile((Canonical) parseUri(Canonical.builder(), "profile", reader, profileElementIndex++));
                    break;
                case "targetProfile":
                    position = checkElementOrder("targetProfile", 4, position, true);
                    builder.targetProfile((Canonical) parseUri(Canonical.builder(), "targetProfile", reader, targetProfileElementIndex++));
                    break;
                case "aggregation":
                    position = checkElementOrder("aggregation", 5, position, true);
                    builder.aggregation((AggregationMode) parseString(AggregationMode.builder(), "aggregation", reader, aggregationElementIndex++));
                    break;
                case "versioning":
                    position = checkElementOrder("versioning", 6, position, false);
                    builder.versioning((ReferenceVersionRules) parseString(ReferenceVersionRules.builder(), "versioning", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Extension parseExtension(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Extension.Builder builder = Extension.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String url = reader.getAttributeValue(null, "url");
        if (url != null) {
            builder.url(url);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                
                case "valueAddress":
                case "valueAge":
                case "valueAnnotation":
                case "valueCount":
                case "valueDistance":
                case "valueDuration":
                case "valueHumanName":
                case "valueMoney":
                case "valueRange":
                case "valueRatio":
                case "valueRatioRange":
                case "valueSampledData":
                case "valueTiming":
                case "valueDataRequirement":
                case "valueExpression":
                case "valueParameterDefinition":
                case "valueTriggerDefinition":
                case "valueAvailability":
                case "valueExtendedContactDetail":
                case "valueDosage":
                	return throwUnsupportedElement(localName);

                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "valueBase64Binary":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseBase64Binary("valueBase64Binary", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueCanonical":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((Canonical) parseUri(Canonical.builder(), "valueCanonical", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueDate":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseDate("valueDate", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                case "valueId":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((Id) parseString(Id.builder(), "valueId", reader, -1));
                    break;
                case "valueInstant":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseInstant("valueInstant", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueInteger64":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseInteger64("valueInteger64", reader, -1));
                    break;
                case "valueMarkdown":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((Markdown) parseString(Markdown.builder(), "valueMarkdown", reader, -1));
                    break;
                case "valueOid":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((Oid) parseUri(Oid.builder(), "valueOid", reader, -1));
                    break;
                case "valuePositiveInt":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((PositiveInt) parseInteger(PositiveInt.builder(), "valuePositiveInt", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueTime":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseTime("valueTime", reader, -1));
                    break;
                case "valueUnsignedInt":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((UnsignedInt) parseInteger(UnsignedInt.builder(), "valueUnsignedInt", reader, -1));
                    break;
                case "valueUri":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseUri("valueUri", reader, -1));
                    break;
                case "valueUrl":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((Url) parseUri(Url.builder(), "valueUrl", reader, -1));
                    break;
                case "valueUuid":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value((Uuid) parseUri(Uuid.builder(), "valueUuid", reader, -1));
                    break;
                case "valueAttachment":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseAttachment("valueAttachment", reader, -1));
                    break;
                case "valueCodeableConcept":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseCodeableConcept("valueCodeableConcept", reader, -1));
                    break;
                case "valueCodeableReference":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseCodeableReference("valueCodeableReference", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueContactPoint":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseContactPoint("valueContactPoint", reader, -1));
                    break;
                case "valueIdentifier":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseIdentifier("valueIdentifier", reader, -1));
                    break;
                case "valuePeriod":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parsePeriod("valuePeriod", reader, -1));
                    break;
                case "valueQuantity":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseQuantity("valueQuantity", reader, -1));
                    break;
                case "valueReference":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseReference("valueReference", reader, -1));
                    break;
                case "valueSignature":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseSignature("valueSignature", reader, -1));
                    break;
                case "valueContactDetail":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseContactDetail("valueContactDetail", reader, -1));
                    break;
                case "valueRelatedArtifact":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseRelatedArtifact("valueRelatedArtifact", reader, -1));
                    break;
                case "valueUsageContext":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseUsageContext("valueUsageContext", reader, -1));
                    break;
                case "valueMeta":
                    position = checkElementOrder("value[x]", 1, position, false);
                    builder.value(parseMeta("valueMeta", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Identifier parseIdentifier(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Identifier.Builder builder = Identifier.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "use":
                    position = checkElementOrder("use", 1, position, false);
                    builder.use((IdentifierUse) parseString(IdentifierUse.builder(), "use", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 2, position, false);
                    builder.type(parseCodeableConcept("type", reader, -1));
                    break;
                case "system":
                    position = checkElementOrder("system", 3, position, false);
                    builder.system(parseUri("system", reader, -1));
                    break;
                case "value":
                    position = checkElementOrder("value", 4, position, false);
                    builder.value(parseString("value", reader, -1));
                    break;
                case "period":
                    position = checkElementOrder("period", 5, position, false);
                    builder.period(parsePeriod("period", reader, -1));
                    break;
                case "assigner":
                    position = checkElementOrder("assigner", 6, position, false);
                    builder.assigner(parseReference("assigner", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Instant parseInstant(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Instant.Builder builder = Instant.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Integer parseInteger(Integer.Builder builder, java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Integer parseInteger(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        return parseInteger(Integer.builder(), elementName, reader, elementIndex);
    }

    private Integer64 parseInteger64(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Integer64.Builder builder = Integer64.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Meta parseMeta(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Meta.Builder builder = Meta.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, profileElementIndex = 0, securityElementIndex = 0, tagElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "versionId":
                    position = checkElementOrder("versionId", 1, position, false);
                    builder.versionId((Id) parseString(Id.builder(), "versionId", reader, -1));
                    break;
                case "lastUpdated":
                    position = checkElementOrder("lastUpdated", 2, position, false);
                    builder.lastUpdated(parseInstant("lastUpdated", reader, -1));
                    break;
                case "source":
                    position = checkElementOrder("source", 3, position, false);
                    builder.source(parseUri("source", reader, -1));
                    break;
                case "profile":
                    position = checkElementOrder("profile", 4, position, true);
                    builder.profile((Canonical) parseUri(Canonical.builder(), "profile", reader, profileElementIndex++));
                    break;
                case "security":
                    position = checkElementOrder("security", 5, position, true);
                    builder.security(parseCoding("security", reader, securityElementIndex++));
                    break;
                case "tag":
                    position = checkElementOrder("tag", 6, position, true);
                    builder.tag(parseCoding("tag", reader, tagElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Narrative parseNarrative(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Narrative.Builder builder = Narrative.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                if ("div".equals(localName)) {
                    requireNamespace(reader, XHTML_NS_URI);
                } else {
                    requireNamespace(reader, FHIR_NS_URI);
                }
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "status":
                    position = checkElementOrder("status", 1, position, false);
                    builder.status((NarrativeStatus) parseString(NarrativeStatus.builder(), "status", reader, -1));
                    break;
                case "div":
                    position = checkElementOrder("div", 2, position, false);
                    builder.div(parseXhtml("div", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OperationDefinition parseOperationDefinition(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        OperationDefinition.Builder builder = OperationDefinition.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, identifierElementIndex = 0, contactElementIndex = 0, useContextElementIndex = 0, jurisdictionElementIndex = 0, resourceElementIndex = 0, parameterElementIndex = 0, overloadElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "url":
                    position = checkElementOrder("url", 8, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 9, position, true);
                    builder.identifier(parseIdentifier("identifier", reader, identifierElementIndex++));
                    break;
                case "version":
                    position = checkElementOrder("version", 10, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "versionAlgorithmString":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseString("versionAlgorithmString", reader, -1));
                    break;
                case "versionAlgorithmCoding":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseCoding("versionAlgorithmCoding", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 12, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 13, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "status":
                    position = checkElementOrder("status", 14, position, false);
                    builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", reader, -1));
                    break;
                case "kind":
                    position = checkElementOrder("kind", 15, position, false);
                    builder.kind((OperationKind) parseString(OperationKind.builder(), "kind", reader, -1));
                    break;
                case "experimental":
                    position = checkElementOrder("experimental", 16, position, false);
                    builder.experimental(parseBoolean("experimental", reader, -1));
                    break;
                case "date":
                    position = checkElementOrder("date", 17, position, false);
                    builder.date(parseDateTime("date", reader, -1));
                    break;
                case "publisher":
                    position = checkElementOrder("publisher", 18, position, false);
                    builder.publisher(parseString("publisher", reader, -1));
                    break;
                case "contact":
                    position = checkElementOrder("contact", 19, position, true);
                    builder.contact(parseContactDetail("contact", reader, contactElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 20, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "useContext":
                    position = checkElementOrder("useContext", 21, position, true);
                    builder.useContext(parseUsageContext("useContext", reader, useContextElementIndex++));
                    break;
                case "jurisdiction":
                    position = checkElementOrder("jurisdiction", 22, position, true);
                    builder.jurisdiction(parseCodeableConcept("jurisdiction", reader, jurisdictionElementIndex++));
                    break;
                case "purpose":
                    position = checkElementOrder("purpose", 23, position, false);
                    builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", reader, -1));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 24, position, false);
                    builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", reader, -1));
                    break;
                case "copyrightLabel":
                    position = checkElementOrder("copyrightLabel", 25, position, false);
                    builder.copyrightLabel(parseString("copyrightLabel", reader, -1));
                    break;
                case "affectsState":
                    position = checkElementOrder("affectsState", 26, position, false);
                    builder.affectsState(parseBoolean("affectsState", reader, -1));
                    break;
                case "code":
                    position = checkElementOrder("code", 27, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "comment":
                    position = checkElementOrder("comment", 28, position, false);
                    builder.comment((Markdown) parseString(Markdown.builder(), "comment", reader, -1));
                    break;
                case "base":
                    position = checkElementOrder("base", 29, position, false);
                    builder.base((Canonical) parseUri(Canonical.builder(), "base", reader, -1));
                    break;
                case "resource":
                    position = checkElementOrder("resource", 30, position, true);
                    builder.resource((FHIRTypes) parseString(FHIRTypes.builder(), "resource", reader, resourceElementIndex++));
                    break;
                case "system":
                    position = checkElementOrder("system", 31, position, false);
                    builder.system(parseBoolean("system", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 32, position, false);
                    builder.type(parseBoolean("type", reader, -1));
                    break;
                case "instance":
                    position = checkElementOrder("instance", 33, position, false);
                    builder.instance(parseBoolean("instance", reader, -1));
                    break;
                case "inputProfile":
                    position = checkElementOrder("inputProfile", 34, position, false);
                    builder.inputProfile((Canonical) parseUri(Canonical.builder(), "inputProfile", reader, -1));
                    break;
                case "outputProfile":
                    position = checkElementOrder("outputProfile", 35, position, false);
                    builder.outputProfile((Canonical) parseUri(Canonical.builder(), "outputProfile", reader, -1));
                    break;
                case "parameter":
                    position = checkElementOrder("parameter", 36, position, true);
                    builder.parameter(parseOperationDefinitionParameter("parameter", reader, parameterElementIndex++));
                    break;
                case "overload":
                    position = checkElementOrder("overload", 37, position, true);
                    builder.overload(parseOperationDefinitionOverload("overload", reader, overloadElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OperationDefinition.Overload parseOperationDefinitionOverload(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        OperationDefinition.Overload.Builder builder = OperationDefinition.Overload.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, parameterNameElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "parameterName":
                    position = checkElementOrder("parameterName", 2, position, true);
                    builder.parameterName(parseString("parameterName", reader, parameterNameElementIndex++));
                    break;
                case "comment":
                    position = checkElementOrder("comment", 3, position, false);
                    builder.comment(parseString("comment", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OperationDefinition.Parameter parseOperationDefinitionParameter(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        OperationDefinition.Parameter.Builder builder = OperationDefinition.Parameter.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, scopeElementIndex = 0, allowedTypeElementIndex = 0, targetProfileElementIndex = 0, referencedFromElementIndex = 0, partElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name((Code) parseString(Code.builder(), "name", reader, -1));
                    break;
                case "use":
                    position = checkElementOrder("use", 3, position, false);
                    builder.use((OperationParameterUse) parseString(OperationParameterUse.builder(), "use", reader, -1));
                    break;
                case "scope":
                    position = checkElementOrder("scope", 4, position, true);
                    builder.scope((OperationParameterScope) parseString(OperationParameterScope.builder(), "scope", reader, scopeElementIndex++));
                    break;
                case "min":
                    position = checkElementOrder("min", 5, position, false);
                    builder.min(parseInteger("min", reader, -1));
                    break;
                case "max":
                    position = checkElementOrder("max", 6, position, false);
                    builder.max(parseString("max", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 7, position, false);
                    builder.documentation((Markdown) parseString(Markdown.builder(), "documentation", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 8, position, false);
                    builder.type((FHIRAllTypes) parseString(FHIRAllTypes.builder(), "type", reader, -1));
                    break;
                case "allowedType":
                    position = checkElementOrder("allowedType", 9, position, true);
                    builder.allowedType((FHIRAllTypes) parseString(FHIRAllTypes.builder(), "allowedType", reader, allowedTypeElementIndex++));
                    break;
                case "targetProfile":
                    position = checkElementOrder("targetProfile", 10, position, true);
                    builder.targetProfile((Canonical) parseUri(Canonical.builder(), "targetProfile", reader, targetProfileElementIndex++));
                    break;
                case "searchType":
                    position = checkElementOrder("searchType", 11, position, false);
                    builder.searchType((SearchParamType) parseString(SearchParamType.builder(), "searchType", reader, -1));
                    break;
                case "binding":
                    position = checkElementOrder("binding", 12, position, false);
                    builder.binding(parseOperationDefinitionParameterBinding("binding", reader, -1));
                    break;
                case "referencedFrom":
                    position = checkElementOrder("referencedFrom", 13, position, true);
                    builder.referencedFrom(parseOperationDefinitionParameterReferencedFrom("referencedFrom", reader, referencedFromElementIndex++));
                    break;
                case "part":
                    position = checkElementOrder("part", 14, position, true);
                    builder.part(parseOperationDefinitionParameter("part", reader, partElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OperationDefinition.Parameter.Binding parseOperationDefinitionParameterBinding(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        OperationDefinition.Parameter.Binding.Builder builder = OperationDefinition.Parameter.Binding.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "strength":
                    position = checkElementOrder("strength", 2, position, false);
                    builder.strength((BindingStrength) parseString(BindingStrength.builder(), "strength", reader, -1));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 3, position, false);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OperationDefinition.Parameter.ReferencedFrom parseOperationDefinitionParameterReferencedFrom(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        OperationDefinition.Parameter.ReferencedFrom.Builder builder = OperationDefinition.Parameter.ReferencedFrom.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "source":
                    position = checkElementOrder("source", 2, position, false);
                    builder.source(parseString("source", reader, -1));
                    break;
                case "sourceId":
                    position = checkElementOrder("sourceId", 3, position, false);
                    builder.sourceId(parseString("sourceId", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OperationOutcome parseOperationOutcome(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        OperationOutcome.Builder builder = OperationOutcome.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, issueElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "issue":
                    position = checkElementOrder("issue", 8, position, true);
                    builder.issue(parseOperationOutcomeIssue("issue", reader, issueElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OperationOutcome.Issue parseOperationOutcomeIssue(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        OperationOutcome.Issue.Builder builder = OperationOutcome.Issue.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, locationElementIndex = 0, expressionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "severity":
                    position = checkElementOrder("severity", 2, position, false);
                    builder.severity((IssueSeverity) parseString(IssueSeverity.builder(), "severity", reader, -1));
                    break;
                case "code":
                    position = checkElementOrder("code", 3, position, false);
                    builder.code((IssueType) parseString(IssueType.builder(), "code", reader, -1));
                    break;
                case "details":
                    position = checkElementOrder("details", 4, position, false);
                    builder.details(parseCodeableConcept("details", reader, -1));
                    break;
                case "diagnostics":
                    position = checkElementOrder("diagnostics", 5, position, false);
                    builder.diagnostics(parseString("diagnostics", reader, -1));
                    break;
                case "location":
                    position = checkElementOrder("location", 6, position, true);
                    builder.location(parseString("location", reader, locationElementIndex++));
                    break;
                case "expression":
                    position = checkElementOrder("expression", 7, position, true);
                    builder.expression(parseString("expression", reader, expressionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Parameters parseParameters(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Parameters.Builder builder = Parameters.builder();
        builder.setValidating(validating);
        int position = -1;
        int parameterElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "parameter":
                    position = checkElementOrder("parameter", 4, position, true);
                    builder.parameter(parseParametersParameter("parameter", reader, parameterElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Parameters.Parameter parseParametersParameter(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Parameters.Parameter.Builder builder = Parameters.Parameter.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, partElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                
                case "valueAddress":
                case "valueAge":
                case "valueAnnotation":
                case "valueCount":
                case "valueDistance":
                case "valueDuration":
                case "valueHumanName":
                case "valueMoney":
                case "valueRange":
                case "valueRatio":
                case "valueRatioRange":
                case "valueSampledData":
                case "valueTiming":
                case "valueDataRequirement":
                case "valueExpression":
                case "valueParameterDefinition":
                case "valueTriggerDefinition":
                case "valueAvailability":
                case "valueExtendedContactDetail":
                case "valueDosage":
                	return throwUnsupportedElement(localName);
                
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "valueBase64Binary":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBase64Binary("valueBase64Binary", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueCanonical":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Canonical) parseUri(Canonical.builder(), "valueCanonical", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueDate":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDate("valueDate", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                case "valueId":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Id) parseString(Id.builder(), "valueId", reader, -1));
                    break;
                case "valueInstant":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInstant("valueInstant", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueInteger64":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger64("valueInteger64", reader, -1));
                    break;
                case "valueMarkdown":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Markdown) parseString(Markdown.builder(), "valueMarkdown", reader, -1));
                    break;
                case "valueOid":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Oid) parseUri(Oid.builder(), "valueOid", reader, -1));
                    break;
                case "valuePositiveInt":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((PositiveInt) parseInteger(PositiveInt.builder(), "valuePositiveInt", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseTime("valueTime", reader, -1));
                    break;
                case "valueUnsignedInt":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((UnsignedInt) parseInteger(UnsignedInt.builder(), "valueUnsignedInt", reader, -1));
                    break;
                case "valueUri":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseUri("valueUri", reader, -1));
                    break;
                case "valueUrl":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Url) parseUri(Url.builder(), "valueUrl", reader, -1));
                    break;
                case "valueUuid":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Uuid) parseUri(Uuid.builder(), "valueUuid", reader, -1));
                    break;
                case "valueAttachment":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseAttachment("valueAttachment", reader, -1));
                    break;
                case "valueCodeableConcept":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCodeableConcept("valueCodeableConcept", reader, -1));
                    break;
                case "valueCodeableReference":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCodeableReference("valueCodeableReference", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueContactPoint":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseContactPoint("valueContactPoint", reader, -1));
                    break;
                case "valueIdentifier":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseIdentifier("valueIdentifier", reader, -1));
                    break;
                case "valuePeriod":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parsePeriod("valuePeriod", reader, -1));
                    break;
                case "valueQuantity":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseQuantity("valueQuantity", reader, -1));
                    break;
                case "valueReference":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseReference("valueReference", reader, -1));
                    break;
                case "valueSignature":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseSignature("valueSignature", reader, -1));
                    break;
                case "valueContactDetail":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseContactDetail("valueContactDetail", reader, -1));
                    break;
                case "valueRelatedArtifact":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseRelatedArtifact("valueRelatedArtifact", reader, -1));
                    break;
                case "valueUsageContext":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseUsageContext("valueUsageContext", reader, -1));
                    break;
                case "valueMeta":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseMeta("valueMeta", reader, -1));
                    break;
                case "resource":
                    position = checkElementOrder("resource", 4, position, false);
                    builder.resource(parseResource("resource", reader, -1));
                    break;
                case "part":
                    position = checkElementOrder("part", 5, position, true);
                    builder.part(parseParametersParameter("part", reader, partElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Period parsePeriod(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Period.Builder builder = Period.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "start":
                    position = checkElementOrder("start", 1, position, false);
                    builder.start(parseDateTime("start", reader, -1));
                    break;
                case "end":
                    position = checkElementOrder("end", 2, position, false);
                    builder.end(parseDateTime("end", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Quantity parseQuantity(Quantity.Builder builder, java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "value":
                    position = checkElementOrder("value", 1, position, false);
                    builder.value(parseDecimal("value", reader, -1));
                    break;
                case "comparator":
                    position = checkElementOrder("comparator", 2, position, false);
                    builder.comparator((QuantityComparator) parseString(QuantityComparator.builder(), "comparator", reader, -1));
                    break;
                case "unit":
                    position = checkElementOrder("unit", 3, position, false);
                    builder.unit(parseString("unit", reader, -1));
                    break;
                case "system":
                    position = checkElementOrder("system", 4, position, false);
                    builder.system(parseUri("system", reader, -1));
                    break;
                case "code":
                    position = checkElementOrder("code", 5, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Quantity parseQuantity(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        return parseQuantity(Quantity.builder(), elementName, reader, elementIndex);
    }

    private Reference parseReference(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Reference.Builder builder = Reference.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "reference":
                    position = checkElementOrder("reference", 1, position, false);
                    builder.reference(parseString("reference", reader, -1));
                    break;
                case "type":
                    position = checkElementOrder("type", 2, position, false);
                    builder.type(parseUri("type", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 3, position, false);
                    builder.identifier(parseIdentifier("identifier", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 4, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private RelatedArtifact parseRelatedArtifact(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
	    stackPush(elementName, elementIndex);
	    RelatedArtifact.Builder builder = RelatedArtifact.builder();
	    builder.setValidating(validating);
	    java.lang.String id = reader.getAttributeValue(null, "id");
	    if (id != null) {
	        builder.id(id);
	    }
	    int position = -1;
	    int extensionElementIndex = 0, classifierElementIndex = 0;
	    while (reader.hasNext()) {
	        int eventType = reader.next();
	        switch (eventType) {
	        case XMLStreamReader.START_ELEMENT:
	            java.lang.String localName = reader.getLocalName();
	            requireNamespace(reader, FHIR_NS_URI);
	            switch (localName) {
	            case "extension":
	                position = checkElementOrder("extension", 0, position, true);
	                builder.extension(parseExtension("extension", reader, extensionElementIndex++));
	                break;
	            case "type":
	                position = checkElementOrder("type", 1, position, false);
	                builder.type((RelatedArtifactType) parseString(RelatedArtifactType.builder(), "type", reader, -1));
	                break;
	            case "classifier":
	                position = checkElementOrder("classifier", 2, position, true);
	                builder.classifier(parseCodeableConcept("classifier", reader, classifierElementIndex++));
	                break;
	            case "label":
	                position = checkElementOrder("label", 3, position, false);
	                builder.label(parseString("label", reader, -1));
	                break;
	            case "display":
	                position = checkElementOrder("display", 4, position, false);
	                builder.display(parseString("display", reader, -1));
	                break;
	            case "citation":
	                position = checkElementOrder("citation", 5, position, false);
	                builder.citation((Markdown) parseString(Markdown.builder(), "citation", reader, -1));
	                break;
	            case "document":
	                position = checkElementOrder("document", 6, position, false);
	                builder.document(parseAttachment("document", reader, -1));
	                break;
	            case "resource":
	                position = checkElementOrder("resource", 7, position, false);
	                builder.resource((Canonical) parseUri(Canonical.builder(), "resource", reader, -1));
	                break;
	            case "resourceReference":
	                position = checkElementOrder("resourceReference", 8, position, false);
	                builder.resourceReference(parseReference("resourceReference", reader, -1));
	                break;
	            case "publicationStatus":
	                position = checkElementOrder("publicationStatus", 9, position, false);
	                builder.publicationStatus((RelatedArtifactPublicationStatus) parseString(RelatedArtifactPublicationStatus.builder(), "publicationStatus", reader, -1));
	                break;
	            case "publicationDate":
	                position = checkElementOrder("publicationDate", 10, position, false);
	                builder.publicationDate(parseDate("publicationDate", reader, -1));
	                break;
	            default:
	                if (!ignoringUnrecognizedElements) {
	                    throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
	                }
	                reader.nextTag();
	                break;
	            }
	            break;
	        case XMLStreamReader.END_ELEMENT:
	            if (reader.getLocalName().equals(elementName)) {
	                stackPop();
	                return builder.build();
	            }
	            break;
	        }
	    }
	    throw new XMLStreamException("Unexpected end of stream");
	}

	private Signature parseSignature(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Signature.Builder builder = Signature.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, typeElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "type":
                    position = checkElementOrder("type", 1, position, true);
                    builder.type(parseCoding("type", reader, typeElementIndex++));
                    break;
                case "when":
                    position = checkElementOrder("when", 2, position, false);
                    builder.when(parseInstant("when", reader, -1));
                    break;
                case "who":
                    position = checkElementOrder("who", 3, position, false);
                    builder.who(parseReference("who", reader, -1));
                    break;
                case "onBehalfOf":
                    position = checkElementOrder("onBehalfOf", 4, position, false);
                    builder.onBehalfOf(parseReference("onBehalfOf", reader, -1));
                    break;
                case "targetFormat":
                    position = checkElementOrder("targetFormat", 5, position, false);
                    builder.targetFormat((Code) parseString(Code.builder(), "targetFormat", reader, -1));
                    break;
                case "sigFormat":
                    position = checkElementOrder("sigFormat", 6, position, false);
                    builder.sigFormat((Code) parseString(Code.builder(), "sigFormat", reader, -1));
                    break;
                case "data":
                    position = checkElementOrder("data", 7, position, false);
                    builder.data(parseBase64Binary("data", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private String parseString(String.Builder builder, java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private String parseString(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        return parseString(String.builder(), elementName, reader, elementIndex);
    }

    private StructureDefinition parseStructureDefinition(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        StructureDefinition.Builder builder = StructureDefinition.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, identifierElementIndex = 0, contactElementIndex = 0, useContextElementIndex = 0, jurisdictionElementIndex = 0, keywordElementIndex = 0, mappingElementIndex = 0, contextElementIndex = 0, contextInvariantElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "url":
                    position = checkElementOrder("url", 8, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 9, position, true);
                    builder.identifier(parseIdentifier("identifier", reader, identifierElementIndex++));
                    break;
                case "version":
                    position = checkElementOrder("version", 10, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "versionAlgorithmString":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseString("versionAlgorithmString", reader, -1));
                    break;
                case "versionAlgorithmCoding":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseCoding("versionAlgorithmCoding", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 12, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 13, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "status":
                    position = checkElementOrder("status", 14, position, false);
                    builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", reader, -1));
                    break;
                case "experimental":
                    position = checkElementOrder("experimental", 15, position, false);
                    builder.experimental(parseBoolean("experimental", reader, -1));
                    break;
                case "date":
                    position = checkElementOrder("date", 16, position, false);
                    builder.date(parseDateTime("date", reader, -1));
                    break;
                case "publisher":
                    position = checkElementOrder("publisher", 17, position, false);
                    builder.publisher(parseString("publisher", reader, -1));
                    break;
                case "contact":
                    position = checkElementOrder("contact", 18, position, true);
                    builder.contact(parseContactDetail("contact", reader, contactElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 19, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "useContext":
                    position = checkElementOrder("useContext", 20, position, true);
                    builder.useContext(parseUsageContext("useContext", reader, useContextElementIndex++));
                    break;
                case "jurisdiction":
                    position = checkElementOrder("jurisdiction", 21, position, true);
                    builder.jurisdiction(parseCodeableConcept("jurisdiction", reader, jurisdictionElementIndex++));
                    break;
                case "purpose":
                    position = checkElementOrder("purpose", 22, position, false);
                    builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", reader, -1));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 23, position, false);
                    builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", reader, -1));
                    break;
                case "copyrightLabel":
                    position = checkElementOrder("copyrightLabel", 24, position, false);
                    builder.copyrightLabel(parseString("copyrightLabel", reader, -1));
                    break;
                case "keyword":
                    position = checkElementOrder("keyword", 25, position, true);
                    builder.keyword(parseCoding("keyword", reader, keywordElementIndex++));
                    break;
                case "fhirVersion":
                    position = checkElementOrder("fhirVersion", 26, position, false);
                    builder.fhirVersion((FHIRVersion) parseString(FHIRVersion.builder(), "fhirVersion", reader, -1));
                    break;
                case "mapping":
                    position = checkElementOrder("mapping", 27, position, true);
                    builder.mapping(parseStructureDefinitionMapping("mapping", reader, mappingElementIndex++));
                    break;
                case "kind":
                    position = checkElementOrder("kind", 28, position, false);
                    builder.kind((StructureDefinitionKind) parseString(StructureDefinitionKind.builder(), "kind", reader, -1));
                    break;
                case "abstract":
                    position = checkElementOrder("abstract", 29, position, false);
                    builder._abstract(parseBoolean("abstract", reader, -1));
                    break;
                case "context":
                    position = checkElementOrder("context", 30, position, true);
                    builder.context(parseStructureDefinitionContext("context", reader, contextElementIndex++));
                    break;
                case "contextInvariant":
                    position = checkElementOrder("contextInvariant", 31, position, true);
                    builder.contextInvariant(parseString("contextInvariant", reader, contextInvariantElementIndex++));
                    break;
                case "type":
                    position = checkElementOrder("type", 32, position, false);
                    builder.type(parseUri("type", reader, -1));
                    break;
                case "baseDefinition":
                    position = checkElementOrder("baseDefinition", 33, position, false);
                    builder.baseDefinition((Canonical) parseUri(Canonical.builder(), "baseDefinition", reader, -1));
                    break;
                case "derivation":
                    position = checkElementOrder("derivation", 34, position, false);
                    builder.derivation((TypeDerivationRule) parseString(TypeDerivationRule.builder(), "derivation", reader, -1));
                    break;
                case "snapshot":
                    position = checkElementOrder("snapshot", 35, position, false);
                    builder.snapshot(parseStructureDefinitionSnapshot("snapshot", reader, -1));
                    break;
                case "differential":
                    position = checkElementOrder("differential", 36, position, false);
                    builder.differential(parseStructureDefinitionDifferential("differential", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private StructureDefinition.Context parseStructureDefinitionContext(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        StructureDefinition.Context.Builder builder = StructureDefinition.Context.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "type":
                    position = checkElementOrder("type", 2, position, false);
                    builder.type((ExtensionContextType) parseString(ExtensionContextType.builder(), "type", reader, -1));
                    break;
                case "expression":
                    position = checkElementOrder("expression", 3, position, false);
                    builder.expression(parseString("expression", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private StructureDefinition.Differential parseStructureDefinitionDifferential(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        StructureDefinition.Differential.Builder builder = StructureDefinition.Differential.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, elementElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "element":
                    position = checkElementOrder("element", 2, position, true);
                    builder.element(parseElementDefinition("element", reader, elementElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private StructureDefinition.Mapping parseStructureDefinitionMapping(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        StructureDefinition.Mapping.Builder builder = StructureDefinition.Mapping.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "identity":
                    position = checkElementOrder("identity", 2, position, false);
                    builder.identity((Id) parseString(Id.builder(), "identity", reader, -1));
                    break;
                case "uri":
                    position = checkElementOrder("uri", 3, position, false);
                    builder.uri(parseUri("uri", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 4, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "comment":
                    position = checkElementOrder("comment", 5, position, false);
                    builder.comment(parseString("comment", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private StructureDefinition.Snapshot parseStructureDefinitionSnapshot(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        StructureDefinition.Snapshot.Builder builder = StructureDefinition.Snapshot.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, elementElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "element":
                    position = checkElementOrder("element", 2, position, true);
                    builder.element(parseElementDefinition("element", reader, elementElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities parseTerminologyCapabilities(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.Builder builder = TerminologyCapabilities.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, identifierElementIndex = 0, contactElementIndex = 0, useContextElementIndex = 0, jurisdictionElementIndex = 0, codeSystemElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "url":
                    position = checkElementOrder("url", 8, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 9, position, true);
                    builder.identifier(parseIdentifier("identifier", reader, identifierElementIndex++));
                    break;
                case "version":
                    position = checkElementOrder("version", 10, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "versionAlgorithmString":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseString("versionAlgorithmString", reader, -1));
                    break;
                case "versionAlgorithmCoding":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseCoding("versionAlgorithmCoding", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 12, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 13, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "status":
                    position = checkElementOrder("status", 14, position, false);
                    builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", reader, -1));
                    break;
                case "experimental":
                    position = checkElementOrder("experimental", 15, position, false);
                    builder.experimental(parseBoolean("experimental", reader, -1));
                    break;
                case "date":
                    position = checkElementOrder("date", 16, position, false);
                    builder.date(parseDateTime("date", reader, -1));
                    break;
                case "publisher":
                    position = checkElementOrder("publisher", 17, position, false);
                    builder.publisher(parseString("publisher", reader, -1));
                    break;
                case "contact":
                    position = checkElementOrder("contact", 18, position, true);
                    builder.contact(parseContactDetail("contact", reader, contactElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 19, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "useContext":
                    position = checkElementOrder("useContext", 20, position, true);
                    builder.useContext(parseUsageContext("useContext", reader, useContextElementIndex++));
                    break;
                case "jurisdiction":
                    position = checkElementOrder("jurisdiction", 21, position, true);
                    builder.jurisdiction(parseCodeableConcept("jurisdiction", reader, jurisdictionElementIndex++));
                    break;
                case "purpose":
                    position = checkElementOrder("purpose", 22, position, false);
                    builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", reader, -1));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 23, position, false);
                    builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", reader, -1));
                    break;
                case "copyrightLabel":
                    position = checkElementOrder("copyrightLabel", 24, position, false);
                    builder.copyrightLabel(parseString("copyrightLabel", reader, -1));
                    break;
                case "kind":
                    position = checkElementOrder("kind", 25, position, false);
                    builder.kind((CapabilityStatementKind) parseString(CapabilityStatementKind.builder(), "kind", reader, -1));
                    break;
                case "software":
                    position = checkElementOrder("software", 26, position, false);
                    builder.software(parseTerminologyCapabilitiesSoftware("software", reader, -1));
                    break;
                case "implementation":
                    position = checkElementOrder("implementation", 27, position, false);
                    builder.implementation(parseTerminologyCapabilitiesImplementation("implementation", reader, -1));
                    break;
                case "lockedDate":
                    position = checkElementOrder("lockedDate", 28, position, false);
                    builder.lockedDate(parseBoolean("lockedDate", reader, -1));
                    break;
                case "codeSystem":
                    position = checkElementOrder("codeSystem", 29, position, true);
                    builder.codeSystem(parseTerminologyCapabilitiesCodeSystem("codeSystem", reader, codeSystemElementIndex++));
                    break;
                case "expansion":
                    position = checkElementOrder("expansion", 30, position, false);
                    builder.expansion(parseTerminologyCapabilitiesExpansion("expansion", reader, -1));
                    break;
                case "codeSearch":
                    position = checkElementOrder("codeSearch", 31, position, false);
                    builder.codeSearch((CodeSearchSupport) parseString(CodeSearchSupport.builder(), "codeSearch", reader, -1));
                    break;
                case "validateCode":
                    position = checkElementOrder("validateCode", 32, position, false);
                    builder.validateCode(parseTerminologyCapabilitiesValidateCode("validateCode", reader, -1));
                    break;
                case "translation":
                    position = checkElementOrder("translation", 33, position, false);
                    builder.translation(parseTerminologyCapabilitiesTranslation("translation", reader, -1));
                    break;
                case "closure":
                    position = checkElementOrder("closure", 34, position, false);
                    builder.closure(parseTerminologyCapabilitiesClosure("closure", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.Closure parseTerminologyCapabilitiesClosure(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.Closure.Builder builder = TerminologyCapabilities.Closure.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "translation":
                    position = checkElementOrder("translation", 2, position, false);
                    builder.translation(parseBoolean("translation", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.CodeSystem parseTerminologyCapabilitiesCodeSystem(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.CodeSystem.Builder builder = TerminologyCapabilities.CodeSystem.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, versionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "uri":
                    position = checkElementOrder("uri", 2, position, false);
                    builder.uri((Canonical) parseUri(Canonical.builder(), "uri", reader, -1));
                    break;
                case "version":
                    position = checkElementOrder("version", 3, position, true);
                    builder.version(parseTerminologyCapabilitiesCodeSystemVersion("version", reader, versionElementIndex++));
                    break;
                case "content":
                    position = checkElementOrder("content", 4, position, false);
                    builder.content((CodeSystemContentMode) parseString(CodeSystemContentMode.builder(), "content", reader, -1));
                    break;
                case "subsumption":
                    position = checkElementOrder("subsumption", 5, position, false);
                    builder.subsumption(parseBoolean("subsumption", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.CodeSystem.Version parseTerminologyCapabilitiesCodeSystemVersion(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.CodeSystem.Version.Builder builder = TerminologyCapabilities.CodeSystem.Version.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, languageElementIndex = 0, filterElementIndex = 0, propertyElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code(parseString("code", reader, -1));
                    break;
                case "isDefault":
                    position = checkElementOrder("isDefault", 3, position, false);
                    builder.isDefault(parseBoolean("isDefault", reader, -1));
                    break;
                case "compositional":
                    position = checkElementOrder("compositional", 4, position, false);
                    builder.compositional(parseBoolean("compositional", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 5, position, true);
                    builder.language((Language) parseString(Language.builder(), "language", reader, languageElementIndex++));
                    break;
                case "filter":
                    position = checkElementOrder("filter", 6, position, true);
                    builder.filter(parseTerminologyCapabilitiesCodeSystemVersionFilter("filter", reader, filterElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 7, position, true);
                    builder.property((Code) parseString(Code.builder(), "property", reader, propertyElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.CodeSystem.Version.Filter parseTerminologyCapabilitiesCodeSystemVersionFilter(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.CodeSystem.Version.Filter.Builder builder = TerminologyCapabilities.CodeSystem.Version.Filter.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, opElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "op":
                    position = checkElementOrder("op", 3, position, true);
                    builder.op((Code) parseString(Code.builder(), "op", reader, opElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.Expansion parseTerminologyCapabilitiesExpansion(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.Expansion.Builder builder = TerminologyCapabilities.Expansion.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, parameterElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "hierarchical":
                    position = checkElementOrder("hierarchical", 2, position, false);
                    builder.hierarchical(parseBoolean("hierarchical", reader, -1));
                    break;
                case "paging":
                    position = checkElementOrder("paging", 3, position, false);
                    builder.paging(parseBoolean("paging", reader, -1));
                    break;
                case "incomplete":
                    position = checkElementOrder("incomplete", 4, position, false);
                    builder.incomplete(parseBoolean("incomplete", reader, -1));
                    break;
                case "parameter":
                    position = checkElementOrder("parameter", 5, position, true);
                    builder.parameter(parseTerminologyCapabilitiesExpansionParameter("parameter", reader, parameterElementIndex++));
                    break;
                case "textFilter":
                    position = checkElementOrder("textFilter", 6, position, false);
                    builder.textFilter((Markdown) parseString(Markdown.builder(), "textFilter", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.Expansion.Parameter parseTerminologyCapabilitiesExpansionParameter(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.Expansion.Parameter.Builder builder = TerminologyCapabilities.Expansion.Parameter.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name((Code) parseString(Code.builder(), "name", reader, -1));
                    break;
                case "documentation":
                    position = checkElementOrder("documentation", 3, position, false);
                    builder.documentation(parseString("documentation", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.Implementation parseTerminologyCapabilitiesImplementation(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.Implementation.Builder builder = TerminologyCapabilities.Implementation.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 2, position, false);
                    builder.description(parseString("description", reader, -1));
                    break;
                case "url":
                    position = checkElementOrder("url", 3, position, false);
                    builder.url((Url) parseUri(Url.builder(), "url", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.Software parseTerminologyCapabilitiesSoftware(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.Software.Builder builder = TerminologyCapabilities.Software.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "version":
                    position = checkElementOrder("version", 3, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.Translation parseTerminologyCapabilitiesTranslation(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.Translation.Builder builder = TerminologyCapabilities.Translation.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "needsMap":
                    position = checkElementOrder("needsMap", 2, position, false);
                    builder.needsMap(parseBoolean("needsMap", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private TerminologyCapabilities.ValidateCode parseTerminologyCapabilitiesValidateCode(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        TerminologyCapabilities.ValidateCode.Builder builder = TerminologyCapabilities.ValidateCode.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "translations":
                    position = checkElementOrder("translations", 2, position, false);
                    builder.translations(parseBoolean("translations", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Time parseTime(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Time.Builder builder = Time.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Uri parseUri(Uri.Builder builder, java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        java.lang.String value = reader.getAttributeValue(null, "value");
        if (value != null) {
            builder.value(value);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Uri parseUri(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        return parseUri(Uri.builder(), elementName, reader, elementIndex);
    }

    private UsageContext parseUsageContext(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        UsageContext.Builder builder = UsageContext.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 1, position, false);
                    builder.code(parseCoding("code", reader, -1));
                    break;
                case "valueCodeableConcept":
                    position = checkElementOrder("value[x]", 2, position, false);
                    builder.value(parseCodeableConcept("valueCodeableConcept", reader, -1));
                    break;
                case "valueQuantity":
                    position = checkElementOrder("value[x]", 2, position, false);
                    builder.value(parseQuantity("valueQuantity", reader, -1));
                    break;
                case "valueRange":
                	return throwUnsupportedElement(localName);
                case "valueReference":
                    position = checkElementOrder("value[x]", 2, position, false);
                    builder.value(parseReference("valueReference", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet parseValueSet(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Builder builder = ValueSet.builder();
        builder.setValidating(validating);
        int position = -1;
        int containedElementIndex = 0, extensionElementIndex = 0, modifierExtensionElementIndex = 0, identifierElementIndex = 0, contactElementIndex = 0, useContextElementIndex = 0, jurisdictionElementIndex = 0, topicElementIndex = 0, authorElementIndex = 0, editorElementIndex = 0, reviewerElementIndex = 0, endorserElementIndex = 0, relatedArtifactElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "id":
                    position = checkElementOrder("id", 0, position, false);
                    builder.id(parseJavaString("id", reader, -1));
                    break;
                case "meta":
                    position = checkElementOrder("meta", 1, position, false);
                    builder.meta(parseMeta("meta", reader, -1));
                    break;
                case "implicitRules":
                    position = checkElementOrder("implicitRules", 2, position, false);
                    builder.implicitRules(parseUri("implicitRules", reader, -1));
                    break;
                case "language":
                    position = checkElementOrder("language", 3, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "text":
                    position = checkElementOrder("text", 4, position, false);
                    builder.text(parseNarrative("text", reader, -1));
                    break;
                case "contained":
                    position = checkElementOrder("contained", 5, position, true);
                    builder.contained(parseResource("contained", reader, containedElementIndex++));
                    break;
                case "extension":
                    position = checkElementOrder("extension", 6, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 7, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "url":
                    position = checkElementOrder("url", 8, position, false);
                    builder.url(parseUri("url", reader, -1));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 9, position, true);
                    builder.identifier(parseIdentifier("identifier", reader, identifierElementIndex++));
                    break;
                case "version":
                    position = checkElementOrder("version", 10, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "versionAlgorithmString":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseString("versionAlgorithmString", reader, -1));
                    break;
                case "versionAlgorithmCoding":
                    position = checkElementOrder("versionAlgorithm[x]", 11, position, false);
                    builder.versionAlgorithm(parseCoding("versionAlgorithmCoding", reader, -1));
                    break;
                case "name":
                    position = checkElementOrder("name", 12, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "title":
                    position = checkElementOrder("title", 13, position, false);
                    builder.title(parseString("title", reader, -1));
                    break;
                case "status":
                    position = checkElementOrder("status", 14, position, false);
                    builder.status((PublicationStatus) parseString(PublicationStatus.builder(), "status", reader, -1));
                    break;
                case "experimental":
                    position = checkElementOrder("experimental", 15, position, false);
                    builder.experimental(parseBoolean("experimental", reader, -1));
                    break;
                case "date":
                    position = checkElementOrder("date", 16, position, false);
                    builder.date(parseDateTime("date", reader, -1));
                    break;
                case "publisher":
                    position = checkElementOrder("publisher", 17, position, false);
                    builder.publisher(parseString("publisher", reader, -1));
                    break;
                case "contact":
                    position = checkElementOrder("contact", 18, position, true);
                    builder.contact(parseContactDetail("contact", reader, contactElementIndex++));
                    break;
                case "description":
                    position = checkElementOrder("description", 19, position, false);
                    builder.description((Markdown) parseString(Markdown.builder(), "description", reader, -1));
                    break;
                case "useContext":
                    position = checkElementOrder("useContext", 20, position, true);
                    builder.useContext(parseUsageContext("useContext", reader, useContextElementIndex++));
                    break;
                case "jurisdiction":
                    position = checkElementOrder("jurisdiction", 21, position, true);
                    builder.jurisdiction(parseCodeableConcept("jurisdiction", reader, jurisdictionElementIndex++));
                    break;
                case "immutable":
                    position = checkElementOrder("immutable", 22, position, false);
                    builder.immutable(parseBoolean("immutable", reader, -1));
                    break;
                case "purpose":
                    position = checkElementOrder("purpose", 23, position, false);
                    builder.purpose((Markdown) parseString(Markdown.builder(), "purpose", reader, -1));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 24, position, false);
                    builder.copyright((Markdown) parseString(Markdown.builder(), "copyright", reader, -1));
                    break;
                case "copyrightLabel":
                    position = checkElementOrder("copyrightLabel", 25, position, false);
                    builder.copyrightLabel(parseString("copyrightLabel", reader, -1));
                    break;
                case "approvalDate":
                    position = checkElementOrder("approvalDate", 26, position, false);
                    builder.approvalDate(parseDate("approvalDate", reader, -1));
                    break;
                case "lastReviewDate":
                    position = checkElementOrder("lastReviewDate", 27, position, false);
                    builder.lastReviewDate(parseDate("lastReviewDate", reader, -1));
                    break;
                case "effectivePeriod":
                    position = checkElementOrder("effectivePeriod", 28, position, false);
                    builder.effectivePeriod(parsePeriod("effectivePeriod", reader, -1));
                    break;
                case "topic":
                    position = checkElementOrder("topic", 29, position, true);
                    builder.topic(parseCodeableConcept("topic", reader, topicElementIndex++));
                    break;
                case "author":
                    position = checkElementOrder("author", 30, position, true);
                    builder.author(parseContactDetail("author", reader, authorElementIndex++));
                    break;
                case "editor":
                    position = checkElementOrder("editor", 31, position, true);
                    builder.editor(parseContactDetail("editor", reader, editorElementIndex++));
                    break;
                case "reviewer":
                    position = checkElementOrder("reviewer", 32, position, true);
                    builder.reviewer(parseContactDetail("reviewer", reader, reviewerElementIndex++));
                    break;
                case "endorser":
                    position = checkElementOrder("endorser", 33, position, true);
                    builder.endorser(parseContactDetail("endorser", reader, endorserElementIndex++));
                    break;
                case "relatedArtifact":
                    position = checkElementOrder("relatedArtifact", 34, position, true);
                    builder.relatedArtifact(parseRelatedArtifact("relatedArtifact", reader, relatedArtifactElementIndex++));
                    break;
                case "compose":
                    position = checkElementOrder("compose", 35, position, false);
                    builder.compose(parseValueSetCompose("compose", reader, -1));
                    break;
                case "expansion":
                    position = checkElementOrder("expansion", 36, position, false);
                    builder.expansion(parseValueSetExpansion("expansion", reader, -1));
                    break;
                case "scope":
                    position = checkElementOrder("scope", 37, position, false);
                    builder.scope(parseValueSetScope("scope", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Compose parseValueSetCompose(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Compose.Builder builder = ValueSet.Compose.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, includeElementIndex = 0, excludeElementIndex = 0, propertyElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "lockedDate":
                    position = checkElementOrder("lockedDate", 2, position, false);
                    builder.lockedDate(parseDate("lockedDate", reader, -1));
                    break;
                case "inactive":
                    position = checkElementOrder("inactive", 3, position, false);
                    builder.inactive(parseBoolean("inactive", reader, -1));
                    break;
                case "include":
                    position = checkElementOrder("include", 4, position, true);
                    builder.include(parseValueSetComposeInclude("include", reader, includeElementIndex++));
                    break;
                case "exclude":
                    position = checkElementOrder("exclude", 5, position, true);
                    builder.exclude(parseValueSetComposeInclude("exclude", reader, excludeElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 6, position, true);
                    builder.property(parseString("property", reader, propertyElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Compose.Include parseValueSetComposeInclude(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Compose.Include.Builder builder = ValueSet.Compose.Include.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, conceptElementIndex = 0, filterElementIndex = 0, valueSetElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "system":
                    position = checkElementOrder("system", 2, position, false);
                    builder.system(parseUri("system", reader, -1));
                    break;
                case "version":
                    position = checkElementOrder("version", 3, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "concept":
                    position = checkElementOrder("concept", 4, position, true);
                    builder.concept(parseValueSetComposeIncludeConcept("concept", reader, conceptElementIndex++));
                    break;
                case "filter":
                    position = checkElementOrder("filter", 5, position, true);
                    builder.filter(parseValueSetComposeIncludeFilter("filter", reader, filterElementIndex++));
                    break;
                case "valueSet":
                    position = checkElementOrder("valueSet", 6, position, true);
                    builder.valueSet((Canonical) parseUri(Canonical.builder(), "valueSet", reader, valueSetElementIndex++));
                    break;
                case "copyright":
                    position = checkElementOrder("copyright", 7, position, false);
                    builder.copyright(parseString("copyright", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Compose.Include.Concept parseValueSetComposeIncludeConcept(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Compose.Include.Concept.Builder builder = ValueSet.Compose.Include.Concept.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, designationElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 3, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                case "designation":
                    position = checkElementOrder("designation", 4, position, true);
                    builder.designation(parseValueSetComposeIncludeConceptDesignation("designation", reader, designationElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Compose.Include.Concept.Designation parseValueSetComposeIncludeConceptDesignation(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Compose.Include.Concept.Designation.Builder builder = ValueSet.Compose.Include.Concept.Designation.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, additionalUseElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "language":
                    position = checkElementOrder("language", 2, position, false);
                    builder.language((Code) parseString(Code.builder(), "language", reader, -1));
                    break;
                case "use":
                    position = checkElementOrder("use", 3, position, false);
                    builder.use(parseCoding("use", reader, -1));
                    break;
                case "additionalUse":
                    position = checkElementOrder("additionalUse", 4, position, true);
                    builder.additionalUse(parseCoding("additionalUse", reader, additionalUseElementIndex++));
                    break;
                case "value":
                    position = checkElementOrder("value", 5, position, false);
                    builder.value(parseString("value", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Compose.Include.Filter parseValueSetComposeIncludeFilter(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Compose.Include.Filter.Builder builder = ValueSet.Compose.Include.Filter.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 2, position, false);
                    builder.property((Code) parseString(Code.builder(), "property", reader, -1));
                    break;
                case "op":
                    position = checkElementOrder("op", 3, position, false);
                    builder.op((FilterOperator) parseString(FilterOperator.builder(), "op", reader, -1));
                    break;
                case "value":
                    position = checkElementOrder("value", 4, position, false);
                    builder.value(parseString("value", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Expansion parseValueSetExpansion(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Expansion.Builder builder = ValueSet.Expansion.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, parameterElementIndex = 0, propertyElementIndex = 0, containsElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "identifier":
                    position = checkElementOrder("identifier", 2, position, false);
                    builder.identifier(parseUri("identifier", reader, -1));
                    break;
                case "next":
                    position = checkElementOrder("next", 3, position, false);
                    builder.next(parseUri("next", reader, -1));
                    break;
                case "timestamp":
                    position = checkElementOrder("timestamp", 4, position, false);
                    builder.timestamp(parseDateTime("timestamp", reader, -1));
                    break;
                case "total":
                    position = checkElementOrder("total", 5, position, false);
                    builder.total(parseInteger("total", reader, -1));
                    break;
                case "offset":
                    position = checkElementOrder("offset", 6, position, false);
                    builder.offset(parseInteger("offset", reader, -1));
                    break;
                case "parameter":
                    position = checkElementOrder("parameter", 7, position, true);
                    builder.parameter(parseValueSetExpansionParameter("parameter", reader, parameterElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 8, position, true);
                    builder.property(parseValueSetExpansionProperty("property", reader, propertyElementIndex++));
                    break;
                case "contains":
                    position = checkElementOrder("contains", 9, position, true);
                    builder.contains(parseValueSetExpansionContains("contains", reader, containsElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Expansion.Contains parseValueSetExpansionContains(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Expansion.Contains.Builder builder = ValueSet.Expansion.Contains.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, designationElementIndex = 0, propertyElementIndex = 0, containsElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "system":
                    position = checkElementOrder("system", 2, position, false);
                    builder.system(parseUri("system", reader, -1));
                    break;
                case "abstract":
                    position = checkElementOrder("abstract", 3, position, false);
                    builder._abstract(parseBoolean("abstract", reader, -1));
                    break;
                case "inactive":
                    position = checkElementOrder("inactive", 4, position, false);
                    builder.inactive(parseBoolean("inactive", reader, -1));
                    break;
                case "version":
                    position = checkElementOrder("version", 5, position, false);
                    builder.version(parseString("version", reader, -1));
                    break;
                case "code":
                    position = checkElementOrder("code", 6, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "display":
                    position = checkElementOrder("display", 7, position, false);
                    builder.display(parseString("display", reader, -1));
                    break;
                case "designation":
                    position = checkElementOrder("designation", 8, position, true);
                    builder.designation(parseValueSetComposeIncludeConceptDesignation("designation", reader, designationElementIndex++));
                    break;
                case "property":
                    position = checkElementOrder("property", 9, position, true);
                    builder.property(parseValueSetExpansionContainsProperty("property", reader, propertyElementIndex++));
                    break;
                case "contains":
                    position = checkElementOrder("contains", 10, position, true);
                    builder.contains(parseValueSetExpansionContains("contains", reader, containsElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Expansion.Contains.Property parseValueSetExpansionContainsProperty(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Expansion.Contains.Property.Builder builder = ValueSet.Expansion.Contains.Property.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0, subPropertyElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                case "subProperty":
                    position = checkElementOrder("subProperty", 4, position, true);
                    builder.subProperty(parseValueSetExpansionContainsPropertySubProperty("subProperty", reader, subPropertyElementIndex++));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Expansion.Contains.Property.SubProperty parseValueSetExpansionContainsPropertySubProperty(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Expansion.Contains.Property.SubProperty.Builder builder = ValueSet.Expansion.Contains.Property.SubProperty.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueCoding":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseCoding("valueCoding", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Expansion.Parameter parseValueSetExpansionParameter(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Expansion.Parameter.Builder builder = ValueSet.Expansion.Parameter.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "name":
                    position = checkElementOrder("name", 2, position, false);
                    builder.name(parseString("name", reader, -1));
                    break;
                case "valueString":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseString("valueString", reader, -1));
                    break;
                case "valueBoolean":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseBoolean("valueBoolean", reader, -1));
                    break;
                case "valueInteger":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseInteger("valueInteger", reader, -1));
                    break;
                case "valueDecimal":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDecimal("valueDecimal", reader, -1));
                    break;
                case "valueUri":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseUri("valueUri", reader, -1));
                    break;
                case "valueCode":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value((Code) parseString(Code.builder(), "valueCode", reader, -1));
                    break;
                case "valueDateTime":
                    position = checkElementOrder("value[x]", 3, position, false);
                    builder.value(parseDateTime("valueDateTime", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Expansion.Property parseValueSetExpansionProperty(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Expansion.Property.Builder builder = ValueSet.Expansion.Property.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "code":
                    position = checkElementOrder("code", 2, position, false);
                    builder.code((Code) parseString(Code.builder(), "code", reader, -1));
                    break;
                case "uri":
                    position = checkElementOrder("uri", 3, position, false);
                    builder.uri(parseUri("uri", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private ValueSet.Scope parseValueSetScope(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        ValueSet.Scope.Builder builder = ValueSet.Scope.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        int position = -1;
        int extensionElementIndex = 0, modifierExtensionElementIndex = 0;
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                requireNamespace(reader, FHIR_NS_URI);
                switch (localName) {
                case "extension":
                    position = checkElementOrder("extension", 0, position, true);
                    builder.extension(parseExtension("extension", reader, extensionElementIndex++));
                    break;
                case "modifierExtension":
                    position = checkElementOrder("modifierExtension", 1, position, true);
                    builder.modifierExtension(parseExtension("modifierExtension", reader, modifierExtensionElementIndex++));
                    break;
                case "inclusionCriteria":
                    position = checkElementOrder("inclusionCriteria", 2, position, false);
                    builder.inclusionCriteria(parseString("inclusionCriteria", reader, -1));
                    break;
                case "exclusionCriteria":
                    position = checkElementOrder("exclusionCriteria", 3, position, false);
                    builder.exclusionCriteria(parseString("exclusionCriteria", reader, -1));
                    break;
                default:
                    if (!ignoringUnrecognizedElements) {
                        throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
                    }
                    reader.nextTag();
                    break;
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return builder.build();
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private Xhtml parseXhtml(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        Xhtml.Builder builder = Xhtml.builder();
        builder.setValidating(validating);
        java.lang.String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            builder.id(id);
        }
        builder.value(parseDiv(reader));
        stackPop();
        return builder.build();
    }
    
    private Element parseElement(java.lang.String elementName, XMLStreamReader reader, int elementIndex) {
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

    private java.lang.String getPath() {
        StringJoiner joiner = new StringJoiner(".");
        for (java.lang.String s : stack) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    private java.lang.String parseJavaString(java.lang.String elementName, XMLStreamReader reader, int elementIndex) throws XMLStreamException {
        stackPush(elementName, elementIndex);
        java.lang.String javaString = reader.getAttributeValue(null, "value");
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                java.lang.String localName = reader.getLocalName();
                throw new IllegalArgumentException("Unrecognized element: '" + localName + "'");
            case XMLStreamReader.END_ELEMENT:
                if (reader.getLocalName().equals(elementName)) {
                    stackPop();
                    return javaString;
                }
                break;
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private java.lang.String getResourceType(XMLStreamReader reader) throws XMLStreamException {
        java.lang.String resourceType = reader.getLocalName();
        try {
            ResourceType.from(resourceType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid resource type: '" + resourceType + "'");
        }
        return resourceType;
    }
}
