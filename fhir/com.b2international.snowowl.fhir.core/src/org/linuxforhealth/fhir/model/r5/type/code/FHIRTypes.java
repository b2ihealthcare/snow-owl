/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/fhir-types")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class FHIRTypes extends Code {
    /**
     * Base
     * 
     * <p>Base Type: Base definition for all types defined in FHIR type system.
     */
    public static final FHIRTypes BASE = FHIRTypes.builder().value(Value.BASE).build();

    /**
     * Element
     * 
     * <p>Element Type: Base definition for all elements in a resource.
     */
    public static final FHIRTypes ELEMENT = FHIRTypes.builder().value(Value.ELEMENT).build();

    /**
     * BackboneElement
     * 
     * <p>BackboneElement Type: Base definition for all elements that are defined inside a resource - but not those in a data 
     * type.
     */
    public static final FHIRTypes BACKBONE_ELEMENT = FHIRTypes.builder().value(Value.BACKBONE_ELEMENT).build();

    /**
     * DataType
     * 
     * <p>DataType Type: The base class for all re-useable types defined as part of the FHIR Specification.
     */
    public static final FHIRTypes DATA_TYPE = FHIRTypes.builder().value(Value.DATA_TYPE).build();

    /**
     * Address
     * 
     * <p>Address Type: An address expressed using postal conventions (as opposed to GPS or other location definition 
     * formats). This data type may be used to convey addresses for use in delivering mail as well as for visiting locations 
     * which might not be valid for mail delivery. There are a variety of postal address formats defined around the world.

     * The ISO21090-codedString may be used to provide a coded representation of the contents of strings in an Address.
     */
    public static final FHIRTypes ADDRESS = FHIRTypes.builder().value(Value.ADDRESS).build();

    /**
     * Annotation
     * 
     * <p>Annotation Type: A text note which also contains information about who made the statement and when.
     */
    public static final FHIRTypes ANNOTATION = FHIRTypes.builder().value(Value.ANNOTATION).build();

    /**
     * Attachment
     * 
     * <p>Attachment Type: For referring to data content defined in other formats.
     */
    public static final FHIRTypes ATTACHMENT = FHIRTypes.builder().value(Value.ATTACHMENT).build();

    /**
     * Availability
     * 
     * <p>Availability Type: Availability data for an {item}.
     */
    public static final FHIRTypes AVAILABILITY = FHIRTypes.builder().value(Value.AVAILABILITY).build();

    /**
     * BackboneType
     * 
     * <p>BackboneType Type: Base definition for the few data types that are allowed to carry modifier extensions.
     */
    public static final FHIRTypes BACKBONE_TYPE = FHIRTypes.builder().value(Value.BACKBONE_TYPE).build();

    /**
     * Dosage
     * 
     * <p>Dosage Type: Indicates how the medication is/was taken or should be taken by the patient.
     */
    public static final FHIRTypes DOSAGE = FHIRTypes.builder().value(Value.DOSAGE).build();

    /**
     * ElementDefinition
     * 
     * <p>ElementDefinition Type: Captures constraints on each element within the resource, profile, or extension.
     */
    public static final FHIRTypes ELEMENT_DEFINITION = FHIRTypes.builder().value(Value.ELEMENT_DEFINITION).build();

    /**
     * MarketingStatus
     * 
     * <p>MarketingStatus Type: The marketing status describes the date when a medicinal product is actually put on the 
     * market or the date as of which it is no longer available.
     */
    public static final FHIRTypes MARKETING_STATUS = FHIRTypes.builder().value(Value.MARKETING_STATUS).build();

    /**
     * ProductShelfLife
     * 
     * <p>ProductShelfLife Type: The shelf-life and storage information for a medicinal product item or container can be 
     * described using this class.
     */
    public static final FHIRTypes PRODUCT_SHELF_LIFE = FHIRTypes.builder().value(Value.PRODUCT_SHELF_LIFE).build();

    /**
     * Timing
     * 
     * <p>Timing Type: Specifies an event that may occur multiple times. Timing schedules are used to record when things are 
     * planned, expected or requested to occur. The most common usage is in dosage instructions for medications. They are 
     * also used when planning care of various kinds, and may be used for reporting the schedule to which past regular 
     * activities were carried out.
     */
    public static final FHIRTypes TIMING = FHIRTypes.builder().value(Value.TIMING).build();

    /**
     * CodeableConcept
     * 
     * <p>CodeableConcept Type: A concept that may be defined by a formal reference to a terminology or ontology or may be 
     * provided by text.
     */
    public static final FHIRTypes CODEABLE_CONCEPT = FHIRTypes.builder().value(Value.CODEABLE_CONCEPT).build();

    /**
     * CodeableReference
     * 
     * <p>CodeableReference Type: A reference to a resource (by instance), or instead, a reference to a concept defined in a 
     * terminology or ontology (by class).
     */
    public static final FHIRTypes CODEABLE_REFERENCE = FHIRTypes.builder().value(Value.CODEABLE_REFERENCE).build();

    /**
     * Coding
     * 
     * <p>Coding Type: A reference to a code defined by a terminology system.
     */
    public static final FHIRTypes CODING = FHIRTypes.builder().value(Value.CODING).build();

    /**
     * ContactDetail
     * 
     * <p>ContactDetail Type: Specifies contact information for a person or organization.
     */
    public static final FHIRTypes CONTACT_DETAIL = FHIRTypes.builder().value(Value.CONTACT_DETAIL).build();

    /**
     * ContactPoint
     * 
     * <p>ContactPoint Type: Details for all kinds of technology mediated contact points for a person or organization, 
     * including telephone, email, etc.
     */
    public static final FHIRTypes CONTACT_POINT = FHIRTypes.builder().value(Value.CONTACT_POINT).build();

    /**
     * Contributor
     * 
     * <p>Contributor Type: A contributor to the content of a knowledge asset, including authors, editors, reviewers, and 
     * endorsers.
     */
    public static final FHIRTypes CONTRIBUTOR = FHIRTypes.builder().value(Value.CONTRIBUTOR).build();

    /**
     * DataRequirement
     * 
     * <p>DataRequirement Type: Describes a required data item for evaluation in terms of the type of data, and optional code 
     * or date-based filters of the data.
     */
    public static final FHIRTypes DATA_REQUIREMENT = FHIRTypes.builder().value(Value.DATA_REQUIREMENT).build();

    /**
     * Expression
     * 
     * <p>Expression Type: A expression that is evaluated in a specified context and returns a value. The context of use of 
     * the expression must specify the context in which the expression is evaluated, and how the result of the expression is 
     * used.
     */
    public static final FHIRTypes EXPRESSION = FHIRTypes.builder().value(Value.EXPRESSION).build();

    /**
     * ExtendedContactDetail
     * 
     * <p>ExtendedContactDetail Type: Specifies contact information for a specific purpose over a period of time, might be 
     * handled/monitored by a specific named person or organization.
     */
    public static final FHIRTypes EXTENDED_CONTACT_DETAIL = FHIRTypes.builder().value(Value.EXTENDED_CONTACT_DETAIL).build();

    /**
     * Extension
     * 
     * <p>Extension Type: Optional Extension Element - found in all resources.
     */
    public static final FHIRTypes EXTENSION = FHIRTypes.builder().value(Value.EXTENSION).build();

    /**
     * HumanName
     * 
     * <p>HumanName Type: A name, normally of a human, that can be used for other living entities (e.g. animals but not 
     * organizations) that have been assigned names by a human and may need the use of name parts or the need for usage 
     * information.
     */
    public static final FHIRTypes HUMAN_NAME = FHIRTypes.builder().value(Value.HUMAN_NAME).build();

    /**
     * Identifier
     * 
     * <p>Identifier Type: An identifier - identifies some entity uniquely and unambiguously. Typically this is used for 
     * business identifiers.
     */
    public static final FHIRTypes IDENTIFIER = FHIRTypes.builder().value(Value.IDENTIFIER).build();

    /**
     * Meta
     * 
     * <p>Meta Type: The metadata about a resource. This is content in the resource that is maintained by the infrastructure. 
     * Changes to the content might not always be associated with version changes to the resource.
     */
    public static final FHIRTypes META = FHIRTypes.builder().value(Value.META).build();

    /**
     * MonetaryComponent
     * 
     * <p>MonetaryComponent Type: Availability data for an {item}.
     */
    public static final FHIRTypes MONETARY_COMPONENT = FHIRTypes.builder().value(Value.MONETARY_COMPONENT).build();

    /**
     * Money
     * 
     * <p>Money Type: An amount of economic utility in some recognized currency.
     */
    public static final FHIRTypes MONEY = FHIRTypes.builder().value(Value.MONEY).build();

    /**
     * Narrative
     * 
     * <p>Narrative Type: A human-readable summary of the resource conveying the essential clinical and business information 
     * for the resource.
     */
    public static final FHIRTypes NARRATIVE = FHIRTypes.builder().value(Value.NARRATIVE).build();

    /**
     * ParameterDefinition
     * 
     * <p>ParameterDefinition Type: The parameters to the module. This collection specifies both the input and output 
     * parameters. Input parameters are provided by the caller as part of the $evaluate operation. Output parameters are 
     * included in the GuidanceResponse.
     */
    public static final FHIRTypes PARAMETER_DEFINITION = FHIRTypes.builder().value(Value.PARAMETER_DEFINITION).build();

    /**
     * Period
     * 
     * <p>Period Type: A time period defined by a start and end date and optionally time.
     */
    public static final FHIRTypes PERIOD = FHIRTypes.builder().value(Value.PERIOD).build();

    /**
     * PrimitiveType
     * 
     * <p>PrimitiveType Type: The base type for all re-useable types defined that have a simple property.
     */
    public static final FHIRTypes PRIMITIVE_TYPE = FHIRTypes.builder().value(Value.PRIMITIVE_TYPE).build();

    /**
     * base64Binary
     * 
     * <p>base64Binary Type: A stream of bytes
     */
    public static final FHIRTypes BASE64BINARY = FHIRTypes.builder().value(Value.BASE64BINARY).build();

    /**
     * boolean
     * 
     * <p>boolean Type: Value of "true" or "false"
     */
    public static final FHIRTypes BOOLEAN = FHIRTypes.builder().value(Value.BOOLEAN).build();

    /**
     * date
     * 
     * <p>date Type: A date or partial date (e.g. just year or year + month). There is no UTC offset. The format is a union 
     * of the schema types gYear, gYearMonth and date. Dates SHALL be valid dates.
     */
    public static final FHIRTypes DATE = FHIRTypes.builder().value(Value.DATE).build();

    /**
     * dateTime
     * 
     * <p>dateTime Type: A date, date-time or partial date (e.g. just year or year + month). If hours and minutes are 
     * specified, a UTC offset SHALL be populated. The format is a union of the schema types gYear, gYearMonth, date and 
     * dateTime. Seconds must be provided due to schema type constraints but may be zero-filled and may be ignored. Dates 
     * SHALL be valid dates.
     */
    public static final FHIRTypes DATE_TIME = FHIRTypes.builder().value(Value.DATE_TIME).build();

    /**
     * decimal
     * 
     * <p>decimal Type: A rational number with implicit precision
     */
    public static final FHIRTypes DECIMAL = FHIRTypes.builder().value(Value.DECIMAL).build();

    /**
     * instant
     * 
     * <p>instant Type: An instant in time - known at least to the second
     */
    public static final FHIRTypes INSTANT = FHIRTypes.builder().value(Value.INSTANT).build();

    /**
     * integer
     * 
     * <p>integer Type: A whole number
     */
    public static final FHIRTypes INTEGER = FHIRTypes.builder().value(Value.INTEGER).build();

    /**
     * positiveInt
     * 
     * <p>positiveInt type: An integer with a value that is positive (e.g. &gt;0)
     */
    public static final FHIRTypes POSITIVE_INT = FHIRTypes.builder().value(Value.POSITIVE_INT).build();

    /**
     * unsignedInt
     * 
     * <p>unsignedInt type: An integer with a value that is not negative (e.g. &gt;= 0)
     */
    public static final FHIRTypes UNSIGNED_INT = FHIRTypes.builder().value(Value.UNSIGNED_INT).build();

    /**
     * integer64
     * 
     * <p>integer64 Type: A very large whole number
     */
    public static final FHIRTypes INTEGER64 = FHIRTypes.builder().value(Value.INTEGER64).build();

    /**
     * string
     * 
     * <p>string Type: A sequence of Unicode characters
     */
    public static final FHIRTypes STRING = FHIRTypes.builder().value(Value.STRING).build();

    /**
     * code
     * 
     * <p>code type: A string which has at least one character and no leading or trailing whitespace and where there is no 
     * whitespace other than single spaces in the contents
     */
    public static final FHIRTypes CODE = FHIRTypes.builder().value(Value.CODE).build();

    /**
     * id
     * 
     * <p>id type: Any combination of letters, numerals, "-" and ".", with a length limit of 64 characters. (This might be an 
     * integer, an unprefixed OID, UUID or any other identifier pattern that meets these constraints.) Ids are case-
     * insensitive.
     */
    public static final FHIRTypes ID = FHIRTypes.builder().value(Value.ID).build();

    /**
     * markdown
     * 
     * <p>markdown type: A string that may contain Github Flavored Markdown syntax for optional processing by a mark down 
     * presentation engine
     */
    public static final FHIRTypes MARKDOWN = FHIRTypes.builder().value(Value.MARKDOWN).build();

    /**
     * time
     * 
     * <p>time Type: A time during the day, with no date specified
     */
    public static final FHIRTypes TIME = FHIRTypes.builder().value(Value.TIME).build();

    /**
     * uri
     * 
     * <p>uri Type: String of characters used to identify a name or a resource
     */
    public static final FHIRTypes URI = FHIRTypes.builder().value(Value.URI).build();

    /**
     * canonical
     * 
     * <p>canonical type: A URI that is a reference to a canonical URL on a FHIR resource
     */
    public static final FHIRTypes CANONICAL = FHIRTypes.builder().value(Value.CANONICAL).build();

    /**
     * oid
     * 
     * <p>oid type: An OID represented as a URI
     */
    public static final FHIRTypes OID = FHIRTypes.builder().value(Value.OID).build();

    /**
     * url
     * 
     * <p>url type: A URI that is a literal reference
     */
    public static final FHIRTypes URL = FHIRTypes.builder().value(Value.URL).build();

    /**
     * uuid
     * 
     * <p>uuid type: A UUID, represented as a URI
     */
    public static final FHIRTypes UUID = FHIRTypes.builder().value(Value.UUID).build();

    /**
     * Quantity
     * 
     * <p>Quantity Type: A measured amount (or an amount that can potentially be measured). Note that measured amounts 
     * include amounts that are not precisely quantified, including amounts involving arbitrary units and floating currencies.
     */
    public static final FHIRTypes QUANTITY = FHIRTypes.builder().value(Value.QUANTITY).build();

    /**
     * Age
     * 
     * <p>Age Type: A duration of time during which an organism (or a process) has existed.
     */
    public static final FHIRTypes AGE = FHIRTypes.builder().value(Value.AGE).build();

    /**
     * Count
     * 
     * <p>Count Type: A measured amount (or an amount that can potentially be measured). Note that measured amounts include 
     * amounts that are not precisely quantified, including amounts involving arbitrary units and floating currencies.
     */
    public static final FHIRTypes COUNT = FHIRTypes.builder().value(Value.COUNT).build();

    /**
     * Distance
     * 
     * <p>Distance Type: A length - a value with a unit that is a physical distance.
     */
    public static final FHIRTypes DISTANCE = FHIRTypes.builder().value(Value.DISTANCE).build();

    /**
     * Duration
     * 
     * <p>Duration Type: A length of time.
     */
    public static final FHIRTypes DURATION = FHIRTypes.builder().value(Value.DURATION).build();

    /**
     * Range
     * 
     * <p>Range Type: A set of ordered Quantities defined by a low and high limit.
     */
    public static final FHIRTypes RANGE = FHIRTypes.builder().value(Value.RANGE).build();

    /**
     * Ratio
     * 
     * <p>Ratio Type: A relationship of two Quantity values - expressed as a numerator and a denominator.
     */
    public static final FHIRTypes RATIO = FHIRTypes.builder().value(Value.RATIO).build();

    /**
     * RatioRange
     * 
     * <p>RatioRange Type: A range of ratios expressed as a low and high numerator and a denominator.
     */
    public static final FHIRTypes RATIO_RANGE = FHIRTypes.builder().value(Value.RATIO_RANGE).build();

    /**
     * Reference
     * 
     * <p>Reference Type: A reference from one resource to another.
     */
    public static final FHIRTypes REFERENCE = FHIRTypes.builder().value(Value.REFERENCE).build();

    /**
     * RelatedArtifact
     * 
     * <p>RelatedArtifact Type: Related artifacts such as additional documentation, justification, or bibliographic 
     * references.
     */
    public static final FHIRTypes RELATED_ARTIFACT = FHIRTypes.builder().value(Value.RELATED_ARTIFACT).build();

    /**
     * SampledData
     * 
     * <p>SampledData Type: A series of measurements taken by a device, with upper and lower limits. There may be more than 
     * one dimension in the data.
     */
    public static final FHIRTypes SAMPLED_DATA = FHIRTypes.builder().value(Value.SAMPLED_DATA).build();

    /**
     * Signature
     * 
     * <p>Signature Type: A signature along with supporting context. The signature may be a digital signature that is 
     * cryptographic in nature, or some other signature acceptable to the domain. This other signature may be as simple as a 
     * graphical image representing a hand-written signature, or a signature ceremony Different signature approaches have 
     * different utilities.
     */
    public static final FHIRTypes SIGNATURE = FHIRTypes.builder().value(Value.SIGNATURE).build();

    /**
     * TriggerDefinition
     * 
     * <p>TriggerDefinition Type: A description of a triggering event. Triggering events can be named events, data events, or 
     * periodic, as determined by the type element.
     */
    public static final FHIRTypes TRIGGER_DEFINITION = FHIRTypes.builder().value(Value.TRIGGER_DEFINITION).build();

    /**
     * UsageContext
     * 
     * <p>UsageContext Type: Specifies clinical/business/etc. metadata that can be used to retrieve, index and/or categorize 
     * an artifact. This metadata can either be specific to the applicable population (e.g., age category, DRG) or the 
     * specific context of care (e.g., venue, care setting, provider of care).
     */
    public static final FHIRTypes USAGE_CONTEXT = FHIRTypes.builder().value(Value.USAGE_CONTEXT).build();

    /**
     * VirtualServiceDetail
     * 
     * <p>VirtualServiceDetail Type: Virtual Service Contact Details.
     */
    public static final FHIRTypes VIRTUAL_SERVICE_DETAIL = FHIRTypes.builder().value(Value.VIRTUAL_SERVICE_DETAIL).build();

    /**
     * xhtml
     * 
     * <p>xhtml Type definition
     */
    public static final FHIRTypes XHTML = FHIRTypes.builder().value(Value.XHTML).build();

    /**
     * Resource
     * 
     * <p>This is the base resource type for everything.
     */
    public static final FHIRTypes RESOURCE = FHIRTypes.builder().value(Value.RESOURCE).build();

    /**
     * Binary
     * 
     * <p>A resource that represents the data of a single raw artifact as digital content accessible in its native format. A 
     * Binary resource can contain any content, whether text, image, pdf, zip archive, etc.
     */
    public static final FHIRTypes BINARY = FHIRTypes.builder().value(Value.BINARY).build();

    /**
     * Bundle
     * 
     * <p>A container for a collection of resources.
     */
    public static final FHIRTypes BUNDLE = FHIRTypes.builder().value(Value.BUNDLE).build();

    /**
     * DomainResource
     * 
     * <p>A resource that includes narrative, extensions, and contained resources.
     */
    public static final FHIRTypes DOMAIN_RESOURCE = FHIRTypes.builder().value(Value.DOMAIN_RESOURCE).build();

    /**
     * Account
     * 
     * <p>A financial tool for tracking value accrued for a particular purpose. In the healthcare field, used to track 
     * charges for a patient, cost centers, etc.
     */
    public static final FHIRTypes ACCOUNT = FHIRTypes.builder().value(Value.ACCOUNT).build();

    /**
     * ActivityDefinition
     * 
     * <p>This resource allows for the definition of some activity to be performed, independent of a particular patient, 
     * practitioner, or other performance context.
     */
    public static final FHIRTypes ACTIVITY_DEFINITION = FHIRTypes.builder().value(Value.ACTIVITY_DEFINITION).build();

    /**
     * ActorDefinition
     * 
     * <p>The ActorDefinition resource is used to describe an actor - a human or an application that plays a role in data 
     * exchange, and that may have obligations associated with the role the actor plays.
     */
    public static final FHIRTypes ACTOR_DEFINITION = FHIRTypes.builder().value(Value.ACTOR_DEFINITION).build();

    /**
     * AdministrableProductDefinition
     * 
     * <p>A medicinal product in the final form which is suitable for administering to a patient (after any mixing of 
     * multiple components, dissolution etc. has been performed).
     */
    public static final FHIRTypes ADMINISTRABLE_PRODUCT_DEFINITION = FHIRTypes.builder().value(Value.ADMINISTRABLE_PRODUCT_DEFINITION).build();

    /**
     * AdverseEvent
     * 
     * <p>An event (i.e. any change to current patient status) that may be related to unintended effects on a patient or 
     * research participant. The unintended effects may require additional monitoring, treatment, hospitalization, or may 
     * result in death. The AdverseEvent resource also extends to potential or avoided events that could have had such 
     * effects. There are two major domains where the AdverseEvent resource is expected to be used. One is in clinical care 
     * reported adverse events and the other is in reporting adverse events in clinical research trial management. Adverse 
     * events can be reported by healthcare providers, patients, caregivers or by medical products manufacturers. Given the 
     * differences between these two concepts, we recommend consulting the domain specific implementation guides when 
     * implementing the AdverseEvent Resource. The implementation guides include specific extensions, value sets and 
     * constraints.
     */
    public static final FHIRTypes ADVERSE_EVENT = FHIRTypes.builder().value(Value.ADVERSE_EVENT).build();

    /**
     * AllergyIntolerance
     * 
     * <p>Risk of harmful or undesirable, physiological response which is unique to an individual and associated with 
     * exposure to a substance.
     */
    public static final FHIRTypes ALLERGY_INTOLERANCE = FHIRTypes.builder().value(Value.ALLERGY_INTOLERANCE).build();

    /**
     * Appointment
     * 
     * <p>A booking of a healthcare event among patient(s), practitioner(s), related person(s) and/or device(s) for a 
     * specific date/time. This may result in one or more Encounter(s).
     */
    public static final FHIRTypes APPOINTMENT = FHIRTypes.builder().value(Value.APPOINTMENT).build();

    /**
     * AppointmentResponse
     * 
     * <p>A reply to an appointment request for a patient and/or practitioner(s), such as a confirmation or rejection.
     */
    public static final FHIRTypes APPOINTMENT_RESPONSE = FHIRTypes.builder().value(Value.APPOINTMENT_RESPONSE).build();

    /**
     * ArtifactAssessment
     * 
     * <p>This Resource provides one or more comments, classifiers or ratings about a Resource and supports attribution and 
     * rights management metadata for the added content.
     */
    public static final FHIRTypes ARTIFACT_ASSESSMENT = FHIRTypes.builder().value(Value.ARTIFACT_ASSESSMENT).build();

    /**
     * AuditEvent
     * 
     * <p>A record of an event relevant for purposes such as operations, privacy, security, maintenance, and performance 
     * analysis.
     */
    public static final FHIRTypes AUDIT_EVENT = FHIRTypes.builder().value(Value.AUDIT_EVENT).build();

    /**
     * Basic
     * 
     * <p>Basic is used for handling concepts not yet defined in FHIR, narrative-only resources that don't map to an existing 
     * resource, and custom resources not appropriate for inclusion in the FHIR specification.
     */
    public static final FHIRTypes BASIC = FHIRTypes.builder().value(Value.BASIC).build();

    /**
     * BiologicallyDerivedProduct
     * 
     * <p>A biological material originating from a biological entity intended to be transplanted or infused into another 
     * (possibly the same) biological entity.
     */
    public static final FHIRTypes BIOLOGICALLY_DERIVED_PRODUCT = FHIRTypes.builder().value(Value.BIOLOGICALLY_DERIVED_PRODUCT).build();

    /**
     * BiologicallyDerivedProductDispense
     * 
     * <p>A record of dispensation of a biologically derived product.
     */
    public static final FHIRTypes BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE = FHIRTypes.builder().value(Value.BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE).build();

    /**
     * BodyStructure
     * 
     * <p>Record details about an anatomical structure. This resource may be used when a coded concept does not provide the 
     * necessary detail needed for the use case.
     */
    public static final FHIRTypes BODY_STRUCTURE = FHIRTypes.builder().value(Value.BODY_STRUCTURE).build();

    /**
     * CanonicalResource
     * 
     * <p>Common Interface declaration for conformance and knowledge artifact resources.
     */
    public static final FHIRTypes CANONICAL_RESOURCE = FHIRTypes.builder().value(Value.CANONICAL_RESOURCE).build();

    /**
     * CapabilityStatement
     * 
     * <p>A Capability Statement documents a set of capabilities (behaviors) of a FHIR Server or Client for a particular 
     * version of FHIR that may be used as a statement of actual server functionality or a statement of required or desired 
     * server implementation.
     */
    public static final FHIRTypes CAPABILITY_STATEMENT = FHIRTypes.builder().value(Value.CAPABILITY_STATEMENT).build();

    /**
     * CarePlan
     * 
     * <p>Describes the intention of how one or more practitioners intend to deliver care for a particular patient, group or 
     * community for a period of time, possibly limited to care for a specific condition or set of conditions.
     */
    public static final FHIRTypes CARE_PLAN = FHIRTypes.builder().value(Value.CARE_PLAN).build();

    /**
     * CareTeam
     * 
     * <p>The Care Team includes all the people and organizations who plan to participate in the coordination and delivery of 
     * care.
     */
    public static final FHIRTypes CARE_TEAM = FHIRTypes.builder().value(Value.CARE_TEAM).build();

    /**
     * ChargeItem
     * 
     * <p>The resource ChargeItem describes the provision of healthcare provider products for a certain patient, therefore 
     * referring not only to the product, but containing in addition details of the provision, like date, time, amounts and 
     * participating organizations and persons. Main Usage of the ChargeItem is to enable the billing process and internal 
     * cost allocation.
     */
    public static final FHIRTypes CHARGE_ITEM = FHIRTypes.builder().value(Value.CHARGE_ITEM).build();

    /**
     * ChargeItemDefinition
     * 
     * <p>The ChargeItemDefinition resource provides the properties that apply to the (billing) codes necessary to calculate 
     * costs and prices. The properties may differ largely depending on type and realm, therefore this resource gives only a 
     * rough structure and requires profiling for each type of billing code system.
     */
    public static final FHIRTypes CHARGE_ITEM_DEFINITION = FHIRTypes.builder().value(Value.CHARGE_ITEM_DEFINITION).build();

    /**
     * Citation
     * 
     * <p>The Citation Resource enables reference to any knowledge artifact for purposes of identification and attribution. 
     * The Citation Resource supports existing reference structures and developing publication practices such as versioning, 
     * expressing complex contributorship roles, and referencing computable resources.
     */
    public static final FHIRTypes CITATION = FHIRTypes.builder().value(Value.CITATION).build();

    /**
     * Claim
     * 
     * <p>A provider issued list of professional services and products which have been provided, or are to be provided, to a 
     * patient which is sent to an insurer for reimbursement.
     */
    public static final FHIRTypes CLAIM = FHIRTypes.builder().value(Value.CLAIM).build();

    /**
     * ClaimResponse
     * 
     * <p>This resource provides the adjudication details from the processing of a Claim resource.
     */
    public static final FHIRTypes CLAIM_RESPONSE = FHIRTypes.builder().value(Value.CLAIM_RESPONSE).build();

    /**
     * ClinicalImpression
     * 
     * <p>A record of a clinical assessment performed to determine what problem(s) may affect the patient and before planning 
     * the treatments or management strategies that are best to manage a patient's condition. Assessments are often 1:1 with 
     * a clinical consultation / encounter, but this varies greatly depending on the clinical workflow. This resource is 
     * called "ClinicalImpression" rather than "ClinicalAssessment" to avoid confusion with the recording of assessment tools 
     * such as Apgar score.
     */
    public static final FHIRTypes CLINICAL_IMPRESSION = FHIRTypes.builder().value(Value.CLINICAL_IMPRESSION).build();

    /**
     * ClinicalUseDefinition
     * 
     * <p>A single issue - either an indication, contraindication, interaction or an undesirable effect for a medicinal 
     * product, medication, device or procedure.
     */
    public static final FHIRTypes CLINICAL_USE_DEFINITION = FHIRTypes.builder().value(Value.CLINICAL_USE_DEFINITION).build();

    /**
     * CodeSystem
     * 
     * <p>The CodeSystem resource is used to declare the existence of and describe a code system or code system supplement 
     * and its key properties, and optionally define a part or all of its content.
     */
    public static final FHIRTypes CODE_SYSTEM = FHIRTypes.builder().value(Value.CODE_SYSTEM).build();

    /**
     * Communication
     * 
     * <p>A clinical or business level record of information being transmitted or shared; e.g. an alert that was sent to a 
     * responsible provider, a public health agency communication to a provider/reporter in response to a case report for a 
     * reportable condition.
     */
    public static final FHIRTypes COMMUNICATION = FHIRTypes.builder().value(Value.COMMUNICATION).build();

    /**
     * CommunicationRequest
     * 
     * <p>A request to convey information; e.g. the CDS system proposes that an alert be sent to a responsible provider, the 
     * CDS system proposes that the public health agency be notified about a reportable condition.
     */
    public static final FHIRTypes COMMUNICATION_REQUEST = FHIRTypes.builder().value(Value.COMMUNICATION_REQUEST).build();

    /**
     * CompartmentDefinition
     * 
     * <p>A compartment definition that defines how resources are accessed on a server.
     */
    public static final FHIRTypes COMPARTMENT_DEFINITION = FHIRTypes.builder().value(Value.COMPARTMENT_DEFINITION).build();

    /**
     * Composition
     * 
     * <p>A set of healthcare-related information that is assembled together into a single logical package that provides a 
     * single coherent statement of meaning, establishes its own context and that has clinical attestation with regard to who 
     * is making the statement. A Composition defines the structure and narrative content necessary for a document. However, 
     * a Composition alone does not constitute a document. Rather, the Composition must be the first entry in a Bundle where 
     * Bundle.type=document, and any other resources referenced from Composition must be included as subsequent entries in 
     * the Bundle (for example Patient, Practitioner, Encounter, etc.).
     */
    public static final FHIRTypes COMPOSITION = FHIRTypes.builder().value(Value.COMPOSITION).build();

    /**
     * ConceptMap
     * 
     * <p>A statement of relationships from one set of concepts to one or more other concepts - either concepts in code 
     * systems, or data element/data element concepts, or classes in class models.
     */
    public static final FHIRTypes CONCEPT_MAP = FHIRTypes.builder().value(Value.CONCEPT_MAP).build();

    /**
     * Condition
     * 
     * <p>A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept that has risen to a 
     * level of concern.
     */
    public static final FHIRTypes CONDITION = FHIRTypes.builder().value(Value.CONDITION).build();

    /**
     * ConditionDefinition
     * 
     * <p>A definition of a condition and information relevant to managing it.
     */
    public static final FHIRTypes CONDITION_DEFINITION = FHIRTypes.builder().value(Value.CONDITION_DEFINITION).build();

    /**
     * Consent
     * 
     * <p>A record of a healthcare consumer’s choices or choices made on their behalf by a third party, which permits or 
     * denies identified recipient(s) or recipient role(s) to perform one or more actions within a given policy context, for 
     * specific purposes and periods of time.
     */
    public static final FHIRTypes CONSENT = FHIRTypes.builder().value(Value.CONSENT).build();

    /**
     * Contract
     * 
     * <p>Legally enforceable, formally recorded unilateral or bilateral directive i.e., a policy or agreement.
     */
    public static final FHIRTypes CONTRACT = FHIRTypes.builder().value(Value.CONTRACT).build();

    /**
     * Coverage
     * 
     * <p>Financial instrument which may be used to reimburse or pay for health care products and services. Includes both 
     * insurance and self-payment.
     */
    public static final FHIRTypes COVERAGE = FHIRTypes.builder().value(Value.COVERAGE).build();

    /**
     * CoverageEligibilityRequest
     * 
     * <p>The CoverageEligibilityRequest provides patient and insurance coverage information to an insurer for them to 
     * respond, in the form of an CoverageEligibilityResponse, with information regarding whether the stated coverage is 
     * valid and in-force and optionally to provide the insurance details of the policy.
     */
    public static final FHIRTypes COVERAGE_ELIGIBILITY_REQUEST = FHIRTypes.builder().value(Value.COVERAGE_ELIGIBILITY_REQUEST).build();

    /**
     * CoverageEligibilityResponse
     * 
     * <p>This resource provides eligibility and plan details from the processing of an CoverageEligibilityRequest resource.
     */
    public static final FHIRTypes COVERAGE_ELIGIBILITY_RESPONSE = FHIRTypes.builder().value(Value.COVERAGE_ELIGIBILITY_RESPONSE).build();

    /**
     * DetectedIssue
     * 
     * <p>Indicates an actual or potential clinical issue with or between one or more active or proposed clinical actions for 
     * a patient; e.g. Drug-drug interaction, Ineffective treatment frequency, Procedure-condition conflict, gaps in care, 
     * etc.
     */
    public static final FHIRTypes DETECTED_ISSUE = FHIRTypes.builder().value(Value.DETECTED_ISSUE).build();

    /**
     * Device
     * 
     * <p>This resource describes the properties (regulated, has real time clock, etc.), adminstrative (manufacturer name, 
     * model number, serial number, firmware, etc.), and type (knee replacement, blood pressure cuff, MRI, etc.) of a 
     * physical unit (these values do not change much within a given module, for example the serail number, manufacturer 
     * name, and model number). An actual unit may consist of several modules in a distinct hierarchy and these are 
     * represented by multiple Device resources and bound through the 'parent' element.
     */
    public static final FHIRTypes DEVICE = FHIRTypes.builder().value(Value.DEVICE).build();

    /**
     * DeviceAssociation
     * 
     * <p>A record of association of a device.
     */
    public static final FHIRTypes DEVICE_ASSOCIATION = FHIRTypes.builder().value(Value.DEVICE_ASSOCIATION).build();

    /**
     * DeviceDefinition
     * 
     * <p>This is a specialized resource that defines the characteristics and capabilities of a device.
     */
    public static final FHIRTypes DEVICE_DEFINITION = FHIRTypes.builder().value(Value.DEVICE_DEFINITION).build();

    /**
     * DeviceDispense
     * 
     * <p>Indicates that a device is to be or has been dispensed for a named person/patient. This includes a description of 
     * the product (supply) provided and the instructions for using the device.
     */
    public static final FHIRTypes DEVICE_DISPENSE = FHIRTypes.builder().value(Value.DEVICE_DISPENSE).build();

    /**
     * DeviceMetric
     * 
     * <p>Describes a measurement, calculation or setting capability of a device. The DeviceMetric resource is derived from 
     * the ISO/IEEE 11073-10201 Domain Information Model standard, but is more widely applicable. 
     */
    public static final FHIRTypes DEVICE_METRIC = FHIRTypes.builder().value(Value.DEVICE_METRIC).build();

    /**
     * DeviceRequest
     * 
     * <p>Represents a request a device to be provided to a specific patient. The device may be an implantable device to be 
     * subsequently implanted, or an external assistive device, such as a walker, to be delivered and subsequently be used.
     */
    public static final FHIRTypes DEVICE_REQUEST = FHIRTypes.builder().value(Value.DEVICE_REQUEST).build();

    /**
     * DeviceUsage
     * 
     * <p>A record of a device being used by a patient where the record is the result of a report from the patient or a 
     * clinician.
     */
    public static final FHIRTypes DEVICE_USAGE = FHIRTypes.builder().value(Value.DEVICE_USAGE).build();

    /**
     * DiagnosticReport
     * 
     * <p>The findings and interpretation of diagnostic tests performed on patients, groups of patients, products, 
     * substances, devices, and locations, and/or specimens derived from these. The report includes clinical context such as 
     * requesting provider information, and some mix of atomic results, images, textual and coded interpretations, and 
     * formatted representation of diagnostic reports. The report also includes non-clinical context such as batch analysis 
     * and stability reporting of products and substances.
     */
    public static final FHIRTypes DIAGNOSTIC_REPORT = FHIRTypes.builder().value(Value.DIAGNOSTIC_REPORT).build();

    /**
     * DocumentReference
     * 
     * <p>A reference to a document of any kind for any purpose. While the term “document�? implies a more narrow focus, 
     * for this resource this “document�? encompasses *any* serialized object with a mime-type, it includes formal patient-
     * centric documents (CDA), clinical notes, scanned paper, non-patient specific documents like policy text, as well as a 
     * photo, video, or audio recording acquired or used in healthcare. The DocumentReference resource provides metadata 
     * about the document so that the document can be discovered and managed. The actual content may be inline base64 encoded 
     * data or provided by direct reference.
     */
    public static final FHIRTypes DOCUMENT_REFERENCE = FHIRTypes.builder().value(Value.DOCUMENT_REFERENCE).build();

    /**
     * Encounter
     * 
     * <p>An interaction between healthcare provider(s), and/or patient(s) for the purpose of providing healthcare service(s) 
     * or assessing the health status of patient(s).
     */
    public static final FHIRTypes ENCOUNTER = FHIRTypes.builder().value(Value.ENCOUNTER).build();

    /**
     * EncounterHistory
     * 
     * <p>A record of significant events/milestones key data throughout the history of an Encounter
     */
    public static final FHIRTypes ENCOUNTER_HISTORY = FHIRTypes.builder().value(Value.ENCOUNTER_HISTORY).build();

    /**
     * Endpoint
     * 
     * <p>The technical details of an endpoint that can be used for electronic services, such as for web services providing 
     * XDS.b, a REST endpoint for another FHIR server, or a s/Mime email address. This may include any security context 
     * information.
     */
    public static final FHIRTypes ENDPOINT = FHIRTypes.builder().value(Value.ENDPOINT).build();

    /**
     * EnrollmentRequest
     * 
     * <p>This resource provides the insurance enrollment details to the insurer regarding a specified coverage.
     */
    public static final FHIRTypes ENROLLMENT_REQUEST = FHIRTypes.builder().value(Value.ENROLLMENT_REQUEST).build();

    /**
     * EnrollmentResponse
     * 
     * <p>This resource provides enrollment and plan details from the processing of an EnrollmentRequest resource.
     */
    public static final FHIRTypes ENROLLMENT_RESPONSE = FHIRTypes.builder().value(Value.ENROLLMENT_RESPONSE).build();

    /**
     * EpisodeOfCare
     * 
     * <p>An association between a patient and an organization / healthcare provider(s) during which time encounters may 
     * occur. The managing organization assumes a level of responsibility for the patient during this time.
     */
    public static final FHIRTypes EPISODE_OF_CARE = FHIRTypes.builder().value(Value.EPISODE_OF_CARE).build();

    /**
     * EventDefinition
     * 
     * <p>The EventDefinition resource provides a reusable description of when a particular event can occur.
     */
    public static final FHIRTypes EVENT_DEFINITION = FHIRTypes.builder().value(Value.EVENT_DEFINITION).build();

    /**
     * Evidence
     * 
     * <p>The Evidence Resource provides a machine-interpretable expression of an evidence concept including the evidence 
     * variables (e.g., population, exposures/interventions, comparators, outcomes, measured variables, confounding 
     * variables), the statistics, and the certainty of this evidence.
     */
    public static final FHIRTypes EVIDENCE = FHIRTypes.builder().value(Value.EVIDENCE).build();

    /**
     * EvidenceReport
     * 
     * <p>The EvidenceReport Resource is a specialized container for a collection of resources and codeable concepts, adapted 
     * to support compositions of Evidence, EvidenceVariable, and Citation resources and related concepts.
     */
    public static final FHIRTypes EVIDENCE_REPORT = FHIRTypes.builder().value(Value.EVIDENCE_REPORT).build();

    /**
     * EvidenceVariable
     * 
     * <p>The EvidenceVariable resource describes an element that knowledge (Evidence) is about.
     */
    public static final FHIRTypes EVIDENCE_VARIABLE = FHIRTypes.builder().value(Value.EVIDENCE_VARIABLE).build();

    /**
     * ExampleScenario
     * 
     * <p>A walkthrough of a workflow showing the interaction between systems and the instances shared, possibly including 
     * the evolution of instances over time.
     */
    public static final FHIRTypes EXAMPLE_SCENARIO = FHIRTypes.builder().value(Value.EXAMPLE_SCENARIO).build();

    /**
     * ExplanationOfBenefit
     * 
     * <p>This resource provides: the claim details; adjudication details from the processing of a Claim; and optionally 
     * account balance information, for informing the subscriber of the benefits provided.
     */
    public static final FHIRTypes EXPLANATION_OF_BENEFIT = FHIRTypes.builder().value(Value.EXPLANATION_OF_BENEFIT).build();

    /**
     * FamilyMemberHistory
     * 
     * <p>Significant health conditions for a person related to the patient relevant in the context of care for the patient.
     */
    public static final FHIRTypes FAMILY_MEMBER_HISTORY = FHIRTypes.builder().value(Value.FAMILY_MEMBER_HISTORY).build();

    /**
     * Flag
     * 
     * <p>Prospective warnings of potential issues when providing care to the patient.
     */
    public static final FHIRTypes FLAG = FHIRTypes.builder().value(Value.FLAG).build();

    /**
     * FormularyItem
     * 
     * <p>This resource describes a product or service that is available through a program and includes the conditions and 
     * constraints of availability. All of the information in this resource is specific to the inclusion of the item in the 
     * formulary and is not inherent to the item itself.
     */
    public static final FHIRTypes FORMULARY_ITEM = FHIRTypes.builder().value(Value.FORMULARY_ITEM).build();

    /**
     * GenomicStudy
     * 
     * <p>A set of analyses performed to analyze and generate genomic data.
     */
    public static final FHIRTypes GENOMIC_STUDY = FHIRTypes.builder().value(Value.GENOMIC_STUDY).build();

    /**
     * Goal
     * 
     * <p>Describes the intended objective(s) for a patient, group or organization care, for example, weight loss, restoring 
     * an activity of daily living, obtaining herd immunity via immunization, meeting a process improvement objective, etc.
     */
    public static final FHIRTypes GOAL = FHIRTypes.builder().value(Value.GOAL).build();

    /**
     * GraphDefinition
     * 
     * <p>A formal computable definition of a graph of resources - that is, a coherent set of resources that form a graph by 
     * following references. The Graph Definition resource defines a set and makes rules about the set.
     */
    public static final FHIRTypes GRAPH_DEFINITION = FHIRTypes.builder().value(Value.GRAPH_DEFINITION).build();

    /**
     * Group
     * 
     * <p>Represents a defined collection of entities that may be discussed or acted upon collectively but which are not 
     * expected to act collectively, and are not formally or legally recognized; i.e. a collection of entities that isn't an 
     * Organization.
     */
    public static final FHIRTypes GROUP = FHIRTypes.builder().value(Value.GROUP).build();

    /**
     * GuidanceResponse
     * 
     * <p>A guidance response is the formal response to a guidance request, including any output parameters returned by the 
     * evaluation, as well as the description of any proposed actions to be taken.
     */
    public static final FHIRTypes GUIDANCE_RESPONSE = FHIRTypes.builder().value(Value.GUIDANCE_RESPONSE).build();

    /**
     * HealthcareService
     * 
     * <p>The details of a healthcare service available at a location or in a catalog. In the case where there is a hierarchy 
     * of services (for example, Lab -&gt; Pathology -&gt; Wound Cultures), this can be represented using a set of linked 
     * HealthcareServices.
     */
    public static final FHIRTypes HEALTHCARE_SERVICE = FHIRTypes.builder().value(Value.HEALTHCARE_SERVICE).build();

    /**
     * ImagingSelection
     * 
     * <p>A selection of DICOM SOP instances and/or frames within a single Study and Series. This might include additional 
     * specifics such as an image region, an Observation UID or a Segmentation Number, allowing linkage to an Observation 
     * Resource or transferring this information along with the ImagingStudy Resource.
     */
    public static final FHIRTypes IMAGING_SELECTION = FHIRTypes.builder().value(Value.IMAGING_SELECTION).build();

    /**
     * ImagingStudy
     * 
     * <p>Representation of the content produced in a DICOM imaging study. A study comprises a set of series, each of which 
     * includes a set of Service-Object Pair Instances (SOP Instances - images or other data) acquired or produced in a 
     * common context. A series is of only one modality (e.g. X-ray, CT, MR, ultrasound), but a study may have multiple 
     * series of different modalities.
     */
    public static final FHIRTypes IMAGING_STUDY = FHIRTypes.builder().value(Value.IMAGING_STUDY).build();

    /**
     * Immunization
     * 
     * <p>Describes the event of a patient being administered a vaccine or a record of an immunization as reported by a 
     * patient, a clinician or another party.
     */
    public static final FHIRTypes IMMUNIZATION = FHIRTypes.builder().value(Value.IMMUNIZATION).build();

    /**
     * ImmunizationEvaluation
     * 
     * <p>Describes a comparison of an immunization event against published recommendations to determine if the 
     * administration is "valid" in relation to those recommendations.
     */
    public static final FHIRTypes IMMUNIZATION_EVALUATION = FHIRTypes.builder().value(Value.IMMUNIZATION_EVALUATION).build();

    /**
     * ImmunizationRecommendation
     * 
     * <p>A patient's point-in-time set of recommendations (i.e. forecasting) according to a published schedule with optional 
     * supporting justification.
     */
    public static final FHIRTypes IMMUNIZATION_RECOMMENDATION = FHIRTypes.builder().value(Value.IMMUNIZATION_RECOMMENDATION).build();

    /**
     * ImplementationGuide
     * 
     * <p>A set of rules of how a particular interoperability or standards problem is solved - typically through the use of 
     * FHIR resources. This resource is used to gather all the parts of an implementation guide into a logical whole and to 
     * publish a computable definition of all the parts.
     */
    public static final FHIRTypes IMPLEMENTATION_GUIDE = FHIRTypes.builder().value(Value.IMPLEMENTATION_GUIDE).build();

    /**
     * Ingredient
     * 
     * <p>An ingredient of a manufactured item or pharmaceutical product.
     */
    public static final FHIRTypes INGREDIENT = FHIRTypes.builder().value(Value.INGREDIENT).build();

    /**
     * InsurancePlan
     * 
     * <p>Details of a Health Insurance product/plan provided by an organization.
     */
    public static final FHIRTypes INSURANCE_PLAN = FHIRTypes.builder().value(Value.INSURANCE_PLAN).build();

    /**
     * InventoryItem
     * 
     * <p>functional description of an inventory item used in inventory and supply-related workflows.
     */
    public static final FHIRTypes INVENTORY_ITEM = FHIRTypes.builder().value(Value.INVENTORY_ITEM).build();

    /**
     * InventoryReport
     * 
     * <p>A report of inventory or stock items.
     */
    public static final FHIRTypes INVENTORY_REPORT = FHIRTypes.builder().value(Value.INVENTORY_REPORT).build();

    /**
     * Invoice
     * 
     * <p>Invoice containing collected ChargeItems from an Account with calculated individual and total price for Billing 
     * purpose.
     */
    public static final FHIRTypes INVOICE = FHIRTypes.builder().value(Value.INVOICE).build();

    /**
     * Library
     * 
     * <p>The Library resource is a general-purpose container for knowledge asset definitions. It can be used to describe and 
     * expose existing knowledge assets such as logic libraries and information model descriptions, as well as to describe a 
     * collection of knowledge assets.
     */
    public static final FHIRTypes LIBRARY = FHIRTypes.builder().value(Value.LIBRARY).build();

    /**
     * Linkage
     * 
     * <p>Identifies two or more records (resource instances) that refer to the same real-world "occurrence".
     */
    public static final FHIRTypes LINKAGE = FHIRTypes.builder().value(Value.LINKAGE).build();

    /**
     * List
     * 
     * <p>A List is a curated collection of resources, for things such as problem lists, allergy lists, facility list, 
     * organization list, etc.
     */
    public static final FHIRTypes LIST = FHIRTypes.builder().value(Value.LIST).build();

    /**
     * Location
     * 
     * <p>Details and position information for a place where services are provided and resources and participants may be 
     * stored, found, contained, or accommodated.
     */
    public static final FHIRTypes LOCATION = FHIRTypes.builder().value(Value.LOCATION).build();

    /**
     * ManufacturedItemDefinition
     * 
     * <p>The definition and characteristics of a medicinal manufactured item, such as a tablet or capsule, as contained in a 
     * packaged medicinal product.
     */
    public static final FHIRTypes MANUFACTURED_ITEM_DEFINITION = FHIRTypes.builder().value(Value.MANUFACTURED_ITEM_DEFINITION).build();

    /**
     * Measure
     * 
     * <p>The Measure resource provides the definition of a quality measure.
     */
    public static final FHIRTypes MEASURE = FHIRTypes.builder().value(Value.MEASURE).build();

    /**
     * MeasureReport
     * 
     * <p>The MeasureReport resource contains the results of the calculation of a measure; and optionally a reference to the 
     * resources involved in that calculation.
     */
    public static final FHIRTypes MEASURE_REPORT = FHIRTypes.builder().value(Value.MEASURE_REPORT).build();

    /**
     * Medication
     * 
     * <p>This resource is primarily used for the identification and definition of a medication, including ingredients, for 
     * the purposes of prescribing, dispensing, and administering a medication as well as for making statements about 
     * medication use.
     */
    public static final FHIRTypes MEDICATION = FHIRTypes.builder().value(Value.MEDICATION).build();

    /**
     * MedicationAdministration
     * 
     * <p>Describes the event of a patient consuming or otherwise being administered a medication. This may be as simple as 
     * swallowing a tablet or it may be a long running infusion. Related resources tie this event to the authorizing 
     * prescription, and the specific encounter between patient and health care practitioner. This event can also be used to 
     * record waste using a status of not-done and the appropriate statusReason.
     */
    public static final FHIRTypes MEDICATION_ADMINISTRATION = FHIRTypes.builder().value(Value.MEDICATION_ADMINISTRATION).build();

    /**
     * MedicationDispense
     * 
     * <p>Indicates that a medication product is to be or has been dispensed for a named person/patient. This includes a 
     * description of the medication product (supply) provided and the instructions for administering the medication. The 
     * medication dispense is the result of a pharmacy system responding to a medication order.
     */
    public static final FHIRTypes MEDICATION_DISPENSE = FHIRTypes.builder().value(Value.MEDICATION_DISPENSE).build();

    /**
     * MedicationKnowledge
     * 
     * <p>Information about a medication that is used to support knowledge.
     */
    public static final FHIRTypes MEDICATION_KNOWLEDGE = FHIRTypes.builder().value(Value.MEDICATION_KNOWLEDGE).build();

    /**
     * MedicationRequest
     * 
     * <p>An order or request for both supply of the medication and the instructions for administration of the medication to 
     * a patient. The resource is called "MedicationRequest" rather than "MedicationPrescription" or "MedicationOrder" to 
     * generalize the use across inpatient and outpatient settings, including care plans, etc., and to harmonize with 
     * workflow patterns.
     */
    public static final FHIRTypes MEDICATION_REQUEST = FHIRTypes.builder().value(Value.MEDICATION_REQUEST).build();

    /**
     * MedicationStatement
     * 
     * <p>A record of a medication that is being consumed by a patient. A MedicationStatement may indicate that the patient 
     * may be taking the medication now or has taken the medication in the past or will be taking the medication in the 
     * future. The source of this information can be the patient, significant other (such as a family member or spouse), or a 
     * clinician. A common scenario where this information is captured is during the history taking process during a patient 
     * visit or stay. The medication information may come from sources such as the patient's memory, from a prescription 
     * bottle, or from a list of medications the patient, clinician or other party maintains. 

The primary difference 
     * between a medicationstatement and a medicationadministration is that the medication administration has complete 
     * administration information and is based on actual administration information from the person who administered the 
     * medication. A medicationstatement is often, if not always, less specific. There is no required date/time when the 
     * medication was administered, in fact we only know that a source has reported the patient is taking this medication, 
     * where details such as time, quantity, or rate or even medication product may be incomplete or missing or less precise. 
     * As stated earlier, the Medication Statement information may come from the patient's memory, from a prescription bottle 
     * or from a list of medications the patient, clinician or other party maintains. Medication administration is more 
     * formal and is not missing detailed information.
     */
    public static final FHIRTypes MEDICATION_STATEMENT = FHIRTypes.builder().value(Value.MEDICATION_STATEMENT).build();

    /**
     * MedicinalProductDefinition
     * 
     * <p>Detailed definition of a medicinal product, typically for uses other than direct patient care (e.g. regulatory use, 
     * drug catalogs, to support prescribing, adverse events management etc.).
     */
    public static final FHIRTypes MEDICINAL_PRODUCT_DEFINITION = FHIRTypes.builder().value(Value.MEDICINAL_PRODUCT_DEFINITION).build();

    /**
     * MessageDefinition
     * 
     * <p>Defines the characteristics of a message that can be shared between systems, including the type of event that 
     * initiates the message, the content to be transmitted and what response(s), if any, are permitted.
     */
    public static final FHIRTypes MESSAGE_DEFINITION = FHIRTypes.builder().value(Value.MESSAGE_DEFINITION).build();

    /**
     * MessageHeader
     * 
     * <p>The header for a message exchange that is either requesting or responding to an action. The reference(s) that are 
     * the subject of the action as well as other information related to the action are typically transmitted in a bundle in 
     * which the MessageHeader resource instance is the first resource in the bundle.
     */
    public static final FHIRTypes MESSAGE_HEADER = FHIRTypes.builder().value(Value.MESSAGE_HEADER).build();

    /**
     * MetadataResource
     * 
     * <p>Common Interface declaration for conformance and knowledge artifact resources.
     */
    public static final FHIRTypes METADATA_RESOURCE = FHIRTypes.builder().value(Value.METADATA_RESOURCE).build();

    /**
     * MolecularSequence
     * 
     * <p>Representation of a molecular sequence.
     */
    public static final FHIRTypes MOLECULAR_SEQUENCE = FHIRTypes.builder().value(Value.MOLECULAR_SEQUENCE).build();

    /**
     * NamingSystem
     * 
     * <p>A curated namespace that issues unique symbols within that namespace for the identification of concepts, people, 
     * devices, etc. Represents a "System" used within the Identifier and Coding data types.
     */
    public static final FHIRTypes NAMING_SYSTEM = FHIRTypes.builder().value(Value.NAMING_SYSTEM).build();

    /**
     * NutritionIntake
     * 
     * <p>A record of food or fluid that is being consumed by a patient. A NutritionIntake may indicate that the patient may 
     * be consuming the food or fluid now or has consumed the food or fluid in the past. The source of this information can 
     * be the patient, significant other (such as a family member or spouse), or a clinician. A common scenario where this 
     * information is captured is during the history taking process during a patient visit or stay or through an app that 
     * tracks food or fluids consumed. The consumption information may come from sources such as the patient's memory, from a 
     * nutrition label, or from a clinician documenting observed intake.
     */
    public static final FHIRTypes NUTRITION_INTAKE = FHIRTypes.builder().value(Value.NUTRITION_INTAKE).build();

    /**
     * NutritionOrder
     * 
     * <p>A request to supply a diet, formula feeding (enteral) or oral nutritional supplement to a patient/resident.
     */
    public static final FHIRTypes NUTRITION_ORDER = FHIRTypes.builder().value(Value.NUTRITION_ORDER).build();

    /**
     * NutritionProduct
     * 
     * <p>A food or supplement that is consumed by patients.
     */
    public static final FHIRTypes NUTRITION_PRODUCT = FHIRTypes.builder().value(Value.NUTRITION_PRODUCT).build();

    /**
     * Observation
     * 
     * <p>Measurements and simple assertions made about a patient, device or other subject.
     */
    public static final FHIRTypes OBSERVATION = FHIRTypes.builder().value(Value.OBSERVATION).build();

    /**
     * ObservationDefinition
     * 
     * <p>Set of definitional characteristics for a kind of observation or measurement produced or consumed by an orderable 
     * health care service.
     */
    public static final FHIRTypes OBSERVATION_DEFINITION = FHIRTypes.builder().value(Value.OBSERVATION_DEFINITION).build();

    /**
     * OperationDefinition
     * 
     * <p>A formal computable definition of an operation (on the RESTful interface) or a named query (using the search 
     * interaction).
     */
    public static final FHIRTypes OPERATION_DEFINITION = FHIRTypes.builder().value(Value.OPERATION_DEFINITION).build();

    /**
     * OperationOutcome
     * 
     * <p>A collection of error, warning, or information messages that result from a system action.
     */
    public static final FHIRTypes OPERATION_OUTCOME = FHIRTypes.builder().value(Value.OPERATION_OUTCOME).build();

    /**
     * Organization
     * 
     * <p>A formally or informally recognized grouping of people or organizations formed for the purpose of achieving some 
     * form of collective action. Includes companies, institutions, corporations, departments, community groups, healthcare 
     * practice groups, payer/insurer, etc.
     */
    public static final FHIRTypes ORGANIZATION = FHIRTypes.builder().value(Value.ORGANIZATION).build();

    /**
     * OrganizationAffiliation
     * 
     * <p>Defines an affiliation/assotiation/relationship between 2 distinct organizations, that is not a part-of 
     * relationship/sub-division relationship.
     */
    public static final FHIRTypes ORGANIZATION_AFFILIATION = FHIRTypes.builder().value(Value.ORGANIZATION_AFFILIATION).build();

    /**
     * PackagedProductDefinition
     * 
     * <p>A medically related item or items, in a container or package.
     */
    public static final FHIRTypes PACKAGED_PRODUCT_DEFINITION = FHIRTypes.builder().value(Value.PACKAGED_PRODUCT_DEFINITION).build();

    /**
     * Patient
     * 
     * <p>Demographics and other administrative information about an individual or animal receiving care or other health-
     * related services.
     */
    public static final FHIRTypes PATIENT = FHIRTypes.builder().value(Value.PATIENT).build();

    /**
     * PaymentNotice
     * 
     * <p>This resource provides the status of the payment for goods and services rendered, and the request and response 
     * resource references.
     */
    public static final FHIRTypes PAYMENT_NOTICE = FHIRTypes.builder().value(Value.PAYMENT_NOTICE).build();

    /**
     * PaymentReconciliation
     * 
     * <p>This resource provides the details including amount of a payment and allocates the payment items being paid.
     */
    public static final FHIRTypes PAYMENT_RECONCILIATION = FHIRTypes.builder().value(Value.PAYMENT_RECONCILIATION).build();

    /**
     * Permission
     * 
     * <p>Permission resource holds access rules for a given data and context.
     */
    public static final FHIRTypes PERMISSION = FHIRTypes.builder().value(Value.PERMISSION).build();

    /**
     * Person
     * 
     * <p>Demographics and administrative information about a person independent of a specific health-related context.
     */
    public static final FHIRTypes PERSON = FHIRTypes.builder().value(Value.PERSON).build();

    /**
     * PlanDefinition
     * 
     * <p>This resource allows for the definition of various types of plans as a sharable, consumable, and executable 
     * artifact. The resource is general enough to support the description of a broad range of clinical and non-clinical 
     * artifacts such as clinical decision support rules, order sets, protocols, and drug quality specifications.
     */
    public static final FHIRTypes PLAN_DEFINITION = FHIRTypes.builder().value(Value.PLAN_DEFINITION).build();

    /**
     * Practitioner
     * 
     * <p>A person who is directly or indirectly involved in the provisioning of healthcare or related services.
     */
    public static final FHIRTypes PRACTITIONER = FHIRTypes.builder().value(Value.PRACTITIONER).build();

    /**
     * PractitionerRole
     * 
     * <p>A specific set of Roles/Locations/specialties/services that a practitioner may perform, or has performed at an 
     * organization during a period of time.
     */
    public static final FHIRTypes PRACTITIONER_ROLE = FHIRTypes.builder().value(Value.PRACTITIONER_ROLE).build();

    /**
     * Procedure
     * 
     * <p>An action that is or was performed on or for a patient, practitioner, device, organization, or location. For 
     * example, this can be a physical intervention on a patient like an operation, or less invasive like long term services, 
     * counseling, or hypnotherapy. This can be a quality or safety inspection for a location, organization, or device. This 
     * can be an accreditation procedure on a practitioner for licensing.
     */
    public static final FHIRTypes PROCEDURE = FHIRTypes.builder().value(Value.PROCEDURE).build();

    /**
     * Provenance
     * 
     * <p>Provenance of a resource is a record that describes entities and processes involved in producing and delivering or 
     * otherwise influencing that resource. Provenance provides a critical foundation for assessing authenticity, enabling 
     * trust, and allowing reproducibility. Provenance assertions are a form of contextual metadata and can themselves become 
     * important records with their own provenance. Provenance statement indicates clinical significance in terms of 
     * confidence in authenticity, reliability, and trustworthiness, integrity, and stage in lifecycle (e.g. Document 
     * Completion - has the artifact been legally authenticated), all of which may impact security, privacy, and trust 
     * policies.
     */
    public static final FHIRTypes PROVENANCE = FHIRTypes.builder().value(Value.PROVENANCE).build();

    /**
     * Questionnaire
     * 
     * <p>A structured set of questions intended to guide the collection of answers from end-users. Questionnaires provide 
     * detailed control over order, presentation, phraseology and grouping to allow coherent, consistent data collection.
     */
    public static final FHIRTypes QUESTIONNAIRE = FHIRTypes.builder().value(Value.QUESTIONNAIRE).build();

    /**
     * QuestionnaireResponse
     * 
     * <p>A structured set of questions and their answers. The questions are ordered and grouped into coherent subsets, 
     * corresponding to the structure of the grouping of the questionnaire being responded to.
     */
    public static final FHIRTypes QUESTIONNAIRE_RESPONSE = FHIRTypes.builder().value(Value.QUESTIONNAIRE_RESPONSE).build();

    /**
     * RegulatedAuthorization
     * 
     * <p>Regulatory approval, clearance or licencing related to a regulated product, treatment, facility or activity that is 
     * cited in a guidance, regulation, rule or legislative act. An example is Market Authorization relating to a Medicinal 
     * Product.
     */
    public static final FHIRTypes REGULATED_AUTHORIZATION = FHIRTypes.builder().value(Value.REGULATED_AUTHORIZATION).build();

    /**
     * RelatedPerson
     * 
     * <p>Information about a person that is involved in a patient's health or the care for a patient, but who is not the 
     * target of healthcare, nor has a formal responsibility in the care process.
     */
    public static final FHIRTypes RELATED_PERSON = FHIRTypes.builder().value(Value.RELATED_PERSON).build();

    /**
     * RequestOrchestration
     * 
     * <p>A set of related requests that can be used to capture intended activities that have inter-dependencies such as 
     * "give this medication after that one".
     */
    public static final FHIRTypes REQUEST_ORCHESTRATION = FHIRTypes.builder().value(Value.REQUEST_ORCHESTRATION).build();

    /**
     * Requirements
     * 
     * <p>The Requirements resource is used to describe an actor - a human or an application that plays a role in data 
     * exchange, and that may have obligations associated with the role the actor plays.
     */
    public static final FHIRTypes REQUIREMENTS = FHIRTypes.builder().value(Value.REQUIREMENTS).build();

    /**
     * ResearchStudy
     * 
     * <p>A scientific study of nature that sometimes includes processes involved in health and disease. For example, 
     * clinical trials are research studies that involve people. These studies may be related to new ways to screen, prevent, 
     * diagnose, and treat disease. They may also study certain outcomes and certain groups of people by looking at data 
     * collected in the past or future.
     */
    public static final FHIRTypes RESEARCH_STUDY = FHIRTypes.builder().value(Value.RESEARCH_STUDY).build();

    /**
     * ResearchSubject
     * 
     * <p>A ResearchSubject is a participant or object which is the recipient of investigative activities in a research study.
     */
    public static final FHIRTypes RESEARCH_SUBJECT = FHIRTypes.builder().value(Value.RESEARCH_SUBJECT).build();

    /**
     * RiskAssessment
     * 
     * <p>An assessment of the likely outcome(s) for a patient or other subject as well as the likelihood of each outcome.
     */
    public static final FHIRTypes RISK_ASSESSMENT = FHIRTypes.builder().value(Value.RISK_ASSESSMENT).build();

    /**
     * Schedule
     * 
     * <p>A container for slots of time that may be available for booking appointments.
     */
    public static final FHIRTypes SCHEDULE = FHIRTypes.builder().value(Value.SCHEDULE).build();

    /**
     * SearchParameter
     * 
     * <p>A search parameter that defines a named search item that can be used to search/filter on a resource.
     */
    public static final FHIRTypes SEARCH_PARAMETER = FHIRTypes.builder().value(Value.SEARCH_PARAMETER).build();

    /**
     * ServiceRequest
     * 
     * <p>A record of a request for service such as diagnostic investigations, treatments, or operations to be performed.
     */
    public static final FHIRTypes SERVICE_REQUEST = FHIRTypes.builder().value(Value.SERVICE_REQUEST).build();

    /**
     * Slot
     * 
     * <p>A slot of time on a schedule that may be available for booking appointments.
     */
    public static final FHIRTypes SLOT = FHIRTypes.builder().value(Value.SLOT).build();

    /**
     * Specimen
     * 
     * <p>A sample to be used for analysis.
     */
    public static final FHIRTypes SPECIMEN = FHIRTypes.builder().value(Value.SPECIMEN).build();

    /**
     * SpecimenDefinition
     * 
     * <p>A kind of specimen with associated set of requirements.
     */
    public static final FHIRTypes SPECIMEN_DEFINITION = FHIRTypes.builder().value(Value.SPECIMEN_DEFINITION).build();

    /**
     * StructureDefinition
     * 
     * <p>A definition of a FHIR structure. This resource is used to describe the underlying resources, data types defined in 
     * FHIR, and also for describing extensions and constraints on resources and data types.
     */
    public static final FHIRTypes STRUCTURE_DEFINITION = FHIRTypes.builder().value(Value.STRUCTURE_DEFINITION).build();

    /**
     * StructureMap
     * 
     * <p>A Map of relationships between 2 structures that can be used to transform data.
     */
    public static final FHIRTypes STRUCTURE_MAP = FHIRTypes.builder().value(Value.STRUCTURE_MAP).build();

    /**
     * Subscription
     * 
     * <p>The subscription resource describes a particular client's request to be notified about a SubscriptionTopic.
     */
    public static final FHIRTypes SUBSCRIPTION = FHIRTypes.builder().value(Value.SUBSCRIPTION).build();

    /**
     * SubscriptionStatus
     * 
     * <p>The SubscriptionStatus resource describes the state of a Subscription during notifications. It is not persisted.
     */
    public static final FHIRTypes SUBSCRIPTION_STATUS = FHIRTypes.builder().value(Value.SUBSCRIPTION_STATUS).build();

    /**
     * SubscriptionTopic
     * 
     * <p>Describes a stream of resource state changes identified by trigger criteria and annotated with labels useful to 
     * filter projections from this topic.
     */
    public static final FHIRTypes SUBSCRIPTION_TOPIC = FHIRTypes.builder().value(Value.SUBSCRIPTION_TOPIC).build();

    /**
     * Substance
     * 
     * <p>A homogeneous material with a definite composition.
     */
    public static final FHIRTypes SUBSTANCE = FHIRTypes.builder().value(Value.SUBSTANCE).build();

    /**
     * SubstanceDefinition
     * 
     * <p>The detailed description of a substance, typically at a level beyond what is used for prescribing.
     */
    public static final FHIRTypes SUBSTANCE_DEFINITION = FHIRTypes.builder().value(Value.SUBSTANCE_DEFINITION).build();

    /**
     * SubstanceNucleicAcid
     * 
     * <p>Nucleic acids are defined by three distinct elements: the base, sugar and linkage. Individual substance/moiety IDs 
     * will be created for each of these elements. The nucleotide sequence will be always entered in the 5’-3’ direction.
     */
    public static final FHIRTypes SUBSTANCE_NUCLEIC_ACID = FHIRTypes.builder().value(Value.SUBSTANCE_NUCLEIC_ACID).build();

    /**
     * SubstancePolymer
     * 
     * <p>Properties of a substance specific to it being a polymer.
     */
    public static final FHIRTypes SUBSTANCE_POLYMER = FHIRTypes.builder().value(Value.SUBSTANCE_POLYMER).build();

    /**
     * SubstanceProtein
     * 
     * <p>A SubstanceProtein is defined as a single unit of a linear amino acid sequence, or a combination of subunits that 
     * are either covalently linked or have a defined invariant stoichiometric relationship. This includes all synthetic, 
     * recombinant and purified SubstanceProteins of defined sequence, whether the use is therapeutic or prophylactic. This 
     * set of elements will be used to describe albumins, coagulation factors, cytokines, growth factors, 
     * peptide/SubstanceProtein hormones, enzymes, toxins, toxoids, recombinant vaccines, and immunomodulators.
     */
    public static final FHIRTypes SUBSTANCE_PROTEIN = FHIRTypes.builder().value(Value.SUBSTANCE_PROTEIN).build();

    /**
     * SubstanceReferenceInformation
     * 
     * <p>Todo.
     */
    public static final FHIRTypes SUBSTANCE_REFERENCE_INFORMATION = FHIRTypes.builder().value(Value.SUBSTANCE_REFERENCE_INFORMATION).build();

    /**
     * SubstanceSourceMaterial
     * 
     * <p>Source material shall capture information on the taxonomic and anatomical origins as well as the fraction of a 
     * material that can result in or can be modified to form a substance. This set of data elements shall be used to define 
     * polymer substances isolated from biological matrices. Taxonomic and anatomical origins shall be described using a 
     * controlled vocabulary as required. This information is captured for naturally derived polymers ( . starch) and 
     * structurally diverse substances. For Organisms belonging to the Kingdom Plantae the Substance level defines the fresh 
     * material of a single species or infraspecies, the Herbal Drug and the Herbal preparation. For Herbal preparations, the 
     * fraction information will be captured at the Substance information level and additional information for herbal 
     * extracts will be captured at the Specified Substance Group 1 information level. See for further explanation the 
     * Substance Class: Structurally Diverse and the herbal annex.
     */
    public static final FHIRTypes SUBSTANCE_SOURCE_MATERIAL = FHIRTypes.builder().value(Value.SUBSTANCE_SOURCE_MATERIAL).build();

    /**
     * SupplyDelivery
     * 
     * <p>Record of delivery of what is supplied.
     */
    public static final FHIRTypes SUPPLY_DELIVERY = FHIRTypes.builder().value(Value.SUPPLY_DELIVERY).build();

    /**
     * SupplyRequest
     * 
     * <p>A record of a non-patient specific request for a medication, substance, device, certain types of biologically 
     * derived product, and nutrition product used in the healthcare setting.
     */
    public static final FHIRTypes SUPPLY_REQUEST = FHIRTypes.builder().value(Value.SUPPLY_REQUEST).build();

    /**
     * Task
     * 
     * <p>A task to be performed.
     */
    public static final FHIRTypes TASK = FHIRTypes.builder().value(Value.TASK).build();

    /**
     * TerminologyCapabilities
     * 
     * <p>A TerminologyCapabilities resource documents a set of capabilities (behaviors) of a FHIR Terminology Server that 
     * may be used as a statement of actual server functionality or a statement of required or desired server implementation.
     */
    public static final FHIRTypes TERMINOLOGY_CAPABILITIES = FHIRTypes.builder().value(Value.TERMINOLOGY_CAPABILITIES).build();

    /**
     * TestPlan
     * 
     * <p>A plan for executing testing on an artifact or specifications
     */
    public static final FHIRTypes TEST_PLAN = FHIRTypes.builder().value(Value.TEST_PLAN).build();

    /**
     * TestReport
     * 
     * <p>A summary of information based on the results of executing a TestScript.
     */
    public static final FHIRTypes TEST_REPORT = FHIRTypes.builder().value(Value.TEST_REPORT).build();

    /**
     * TestScript
     * 
     * <p>A structured set of tests against a FHIR server or client implementation to determine compliance against the FHIR 
     * specification.
     */
    public static final FHIRTypes TEST_SCRIPT = FHIRTypes.builder().value(Value.TEST_SCRIPT).build();

    /**
     * Transport
     * 
     * <p>Record of transport.
     */
    public static final FHIRTypes TRANSPORT = FHIRTypes.builder().value(Value.TRANSPORT).build();

    /**
     * ValueSet
     * 
     * <p>A ValueSet resource instance specifies a set of codes drawn from one or more code systems, intended for use in a 
     * particular context. Value sets link between [[[CodeSystem]]] definitions and their use in [coded elements]
     * (terminologies.html).
     */
    public static final FHIRTypes VALUE_SET = FHIRTypes.builder().value(Value.VALUE_SET).build();

    /**
     * VerificationResult
     * 
     * <p>Describes validation requirements, source(s), status and dates for one or more elements.
     */
    public static final FHIRTypes VERIFICATION_RESULT = FHIRTypes.builder().value(Value.VERIFICATION_RESULT).build();

    /**
     * VisionPrescription
     * 
     * <p>An authorization for the provision of glasses and/or contact lenses to a patient.
     */
    public static final FHIRTypes VISION_PRESCRIPTION = FHIRTypes.builder().value(Value.VISION_PRESCRIPTION).build();

    /**
     * Parameters
     * 
     * <p>This resource is used to pass information into and back from an operation (whether invoked directly from REST or 
     * within a messaging environment). It is not persisted or allowed to be referenced by other resources except as 
     * described in the definition of the Parameters resource.
     */
    public static final FHIRTypes PARAMETERS = FHIRTypes.builder().value(Value.PARAMETERS).build();

    private volatile int hashCode;

    private FHIRTypes(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this FHIRTypes as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating FHIRTypes objects from a passed enum value.
     */
    public static FHIRTypes of(Value value) {
        switch (value) {
        case BASE:
            return BASE;
        case ELEMENT:
            return ELEMENT;
        case BACKBONE_ELEMENT:
            return BACKBONE_ELEMENT;
        case DATA_TYPE:
            return DATA_TYPE;
        case ADDRESS:
            return ADDRESS;
        case ANNOTATION:
            return ANNOTATION;
        case ATTACHMENT:
            return ATTACHMENT;
        case AVAILABILITY:
            return AVAILABILITY;
        case BACKBONE_TYPE:
            return BACKBONE_TYPE;
        case DOSAGE:
            return DOSAGE;
        case ELEMENT_DEFINITION:
            return ELEMENT_DEFINITION;
        case MARKETING_STATUS:
            return MARKETING_STATUS;
        case PRODUCT_SHELF_LIFE:
            return PRODUCT_SHELF_LIFE;
        case TIMING:
            return TIMING;
        case CODEABLE_CONCEPT:
            return CODEABLE_CONCEPT;
        case CODEABLE_REFERENCE:
            return CODEABLE_REFERENCE;
        case CODING:
            return CODING;
        case CONTACT_DETAIL:
            return CONTACT_DETAIL;
        case CONTACT_POINT:
            return CONTACT_POINT;
        case CONTRIBUTOR:
            return CONTRIBUTOR;
        case DATA_REQUIREMENT:
            return DATA_REQUIREMENT;
        case EXPRESSION:
            return EXPRESSION;
        case EXTENDED_CONTACT_DETAIL:
            return EXTENDED_CONTACT_DETAIL;
        case EXTENSION:
            return EXTENSION;
        case HUMAN_NAME:
            return HUMAN_NAME;
        case IDENTIFIER:
            return IDENTIFIER;
        case META:
            return META;
        case MONETARY_COMPONENT:
            return MONETARY_COMPONENT;
        case MONEY:
            return MONEY;
        case NARRATIVE:
            return NARRATIVE;
        case PARAMETER_DEFINITION:
            return PARAMETER_DEFINITION;
        case PERIOD:
            return PERIOD;
        case PRIMITIVE_TYPE:
            return PRIMITIVE_TYPE;
        case BASE64BINARY:
            return BASE64BINARY;
        case BOOLEAN:
            return BOOLEAN;
        case DATE:
            return DATE;
        case DATE_TIME:
            return DATE_TIME;
        case DECIMAL:
            return DECIMAL;
        case INSTANT:
            return INSTANT;
        case INTEGER:
            return INTEGER;
        case POSITIVE_INT:
            return POSITIVE_INT;
        case UNSIGNED_INT:
            return UNSIGNED_INT;
        case INTEGER64:
            return INTEGER64;
        case STRING:
            return STRING;
        case CODE:
            return CODE;
        case ID:
            return ID;
        case MARKDOWN:
            return MARKDOWN;
        case TIME:
            return TIME;
        case URI:
            return URI;
        case CANONICAL:
            return CANONICAL;
        case OID:
            return OID;
        case URL:
            return URL;
        case UUID:
            return UUID;
        case QUANTITY:
            return QUANTITY;
        case AGE:
            return AGE;
        case COUNT:
            return COUNT;
        case DISTANCE:
            return DISTANCE;
        case DURATION:
            return DURATION;
        case RANGE:
            return RANGE;
        case RATIO:
            return RATIO;
        case RATIO_RANGE:
            return RATIO_RANGE;
        case REFERENCE:
            return REFERENCE;
        case RELATED_ARTIFACT:
            return RELATED_ARTIFACT;
        case SAMPLED_DATA:
            return SAMPLED_DATA;
        case SIGNATURE:
            return SIGNATURE;
        case TRIGGER_DEFINITION:
            return TRIGGER_DEFINITION;
        case USAGE_CONTEXT:
            return USAGE_CONTEXT;
        case VIRTUAL_SERVICE_DETAIL:
            return VIRTUAL_SERVICE_DETAIL;
        case XHTML:
            return XHTML;
        case RESOURCE:
            return RESOURCE;
        case BINARY:
            return BINARY;
        case BUNDLE:
            return BUNDLE;
        case DOMAIN_RESOURCE:
            return DOMAIN_RESOURCE;
        case ACCOUNT:
            return ACCOUNT;
        case ACTIVITY_DEFINITION:
            return ACTIVITY_DEFINITION;
        case ACTOR_DEFINITION:
            return ACTOR_DEFINITION;
        case ADMINISTRABLE_PRODUCT_DEFINITION:
            return ADMINISTRABLE_PRODUCT_DEFINITION;
        case ADVERSE_EVENT:
            return ADVERSE_EVENT;
        case ALLERGY_INTOLERANCE:
            return ALLERGY_INTOLERANCE;
        case APPOINTMENT:
            return APPOINTMENT;
        case APPOINTMENT_RESPONSE:
            return APPOINTMENT_RESPONSE;
        case ARTIFACT_ASSESSMENT:
            return ARTIFACT_ASSESSMENT;
        case AUDIT_EVENT:
            return AUDIT_EVENT;
        case BASIC:
            return BASIC;
        case BIOLOGICALLY_DERIVED_PRODUCT:
            return BIOLOGICALLY_DERIVED_PRODUCT;
        case BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE:
            return BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE;
        case BODY_STRUCTURE:
            return BODY_STRUCTURE;
        case CANONICAL_RESOURCE:
            return CANONICAL_RESOURCE;
        case CAPABILITY_STATEMENT:
            return CAPABILITY_STATEMENT;
        case CARE_PLAN:
            return CARE_PLAN;
        case CARE_TEAM:
            return CARE_TEAM;
        case CHARGE_ITEM:
            return CHARGE_ITEM;
        case CHARGE_ITEM_DEFINITION:
            return CHARGE_ITEM_DEFINITION;
        case CITATION:
            return CITATION;
        case CLAIM:
            return CLAIM;
        case CLAIM_RESPONSE:
            return CLAIM_RESPONSE;
        case CLINICAL_IMPRESSION:
            return CLINICAL_IMPRESSION;
        case CLINICAL_USE_DEFINITION:
            return CLINICAL_USE_DEFINITION;
        case CODE_SYSTEM:
            return CODE_SYSTEM;
        case COMMUNICATION:
            return COMMUNICATION;
        case COMMUNICATION_REQUEST:
            return COMMUNICATION_REQUEST;
        case COMPARTMENT_DEFINITION:
            return COMPARTMENT_DEFINITION;
        case COMPOSITION:
            return COMPOSITION;
        case CONCEPT_MAP:
            return CONCEPT_MAP;
        case CONDITION:
            return CONDITION;
        case CONDITION_DEFINITION:
            return CONDITION_DEFINITION;
        case CONSENT:
            return CONSENT;
        case CONTRACT:
            return CONTRACT;
        case COVERAGE:
            return COVERAGE;
        case COVERAGE_ELIGIBILITY_REQUEST:
            return COVERAGE_ELIGIBILITY_REQUEST;
        case COVERAGE_ELIGIBILITY_RESPONSE:
            return COVERAGE_ELIGIBILITY_RESPONSE;
        case DETECTED_ISSUE:
            return DETECTED_ISSUE;
        case DEVICE:
            return DEVICE;
        case DEVICE_ASSOCIATION:
            return DEVICE_ASSOCIATION;
        case DEVICE_DEFINITION:
            return DEVICE_DEFINITION;
        case DEVICE_DISPENSE:
            return DEVICE_DISPENSE;
        case DEVICE_METRIC:
            return DEVICE_METRIC;
        case DEVICE_REQUEST:
            return DEVICE_REQUEST;
        case DEVICE_USAGE:
            return DEVICE_USAGE;
        case DIAGNOSTIC_REPORT:
            return DIAGNOSTIC_REPORT;
        case DOCUMENT_REFERENCE:
            return DOCUMENT_REFERENCE;
        case ENCOUNTER:
            return ENCOUNTER;
        case ENCOUNTER_HISTORY:
            return ENCOUNTER_HISTORY;
        case ENDPOINT:
            return ENDPOINT;
        case ENROLLMENT_REQUEST:
            return ENROLLMENT_REQUEST;
        case ENROLLMENT_RESPONSE:
            return ENROLLMENT_RESPONSE;
        case EPISODE_OF_CARE:
            return EPISODE_OF_CARE;
        case EVENT_DEFINITION:
            return EVENT_DEFINITION;
        case EVIDENCE:
            return EVIDENCE;
        case EVIDENCE_REPORT:
            return EVIDENCE_REPORT;
        case EVIDENCE_VARIABLE:
            return EVIDENCE_VARIABLE;
        case EXAMPLE_SCENARIO:
            return EXAMPLE_SCENARIO;
        case EXPLANATION_OF_BENEFIT:
            return EXPLANATION_OF_BENEFIT;
        case FAMILY_MEMBER_HISTORY:
            return FAMILY_MEMBER_HISTORY;
        case FLAG:
            return FLAG;
        case FORMULARY_ITEM:
            return FORMULARY_ITEM;
        case GENOMIC_STUDY:
            return GENOMIC_STUDY;
        case GOAL:
            return GOAL;
        case GRAPH_DEFINITION:
            return GRAPH_DEFINITION;
        case GROUP:
            return GROUP;
        case GUIDANCE_RESPONSE:
            return GUIDANCE_RESPONSE;
        case HEALTHCARE_SERVICE:
            return HEALTHCARE_SERVICE;
        case IMAGING_SELECTION:
            return IMAGING_SELECTION;
        case IMAGING_STUDY:
            return IMAGING_STUDY;
        case IMMUNIZATION:
            return IMMUNIZATION;
        case IMMUNIZATION_EVALUATION:
            return IMMUNIZATION_EVALUATION;
        case IMMUNIZATION_RECOMMENDATION:
            return IMMUNIZATION_RECOMMENDATION;
        case IMPLEMENTATION_GUIDE:
            return IMPLEMENTATION_GUIDE;
        case INGREDIENT:
            return INGREDIENT;
        case INSURANCE_PLAN:
            return INSURANCE_PLAN;
        case INVENTORY_ITEM:
            return INVENTORY_ITEM;
        case INVENTORY_REPORT:
            return INVENTORY_REPORT;
        case INVOICE:
            return INVOICE;
        case LIBRARY:
            return LIBRARY;
        case LINKAGE:
            return LINKAGE;
        case LIST:
            return LIST;
        case LOCATION:
            return LOCATION;
        case MANUFACTURED_ITEM_DEFINITION:
            return MANUFACTURED_ITEM_DEFINITION;
        case MEASURE:
            return MEASURE;
        case MEASURE_REPORT:
            return MEASURE_REPORT;
        case MEDICATION:
            return MEDICATION;
        case MEDICATION_ADMINISTRATION:
            return MEDICATION_ADMINISTRATION;
        case MEDICATION_DISPENSE:
            return MEDICATION_DISPENSE;
        case MEDICATION_KNOWLEDGE:
            return MEDICATION_KNOWLEDGE;
        case MEDICATION_REQUEST:
            return MEDICATION_REQUEST;
        case MEDICATION_STATEMENT:
            return MEDICATION_STATEMENT;
        case MEDICINAL_PRODUCT_DEFINITION:
            return MEDICINAL_PRODUCT_DEFINITION;
        case MESSAGE_DEFINITION:
            return MESSAGE_DEFINITION;
        case MESSAGE_HEADER:
            return MESSAGE_HEADER;
        case METADATA_RESOURCE:
            return METADATA_RESOURCE;
        case MOLECULAR_SEQUENCE:
            return MOLECULAR_SEQUENCE;
        case NAMING_SYSTEM:
            return NAMING_SYSTEM;
        case NUTRITION_INTAKE:
            return NUTRITION_INTAKE;
        case NUTRITION_ORDER:
            return NUTRITION_ORDER;
        case NUTRITION_PRODUCT:
            return NUTRITION_PRODUCT;
        case OBSERVATION:
            return OBSERVATION;
        case OBSERVATION_DEFINITION:
            return OBSERVATION_DEFINITION;
        case OPERATION_DEFINITION:
            return OPERATION_DEFINITION;
        case OPERATION_OUTCOME:
            return OPERATION_OUTCOME;
        case ORGANIZATION:
            return ORGANIZATION;
        case ORGANIZATION_AFFILIATION:
            return ORGANIZATION_AFFILIATION;
        case PACKAGED_PRODUCT_DEFINITION:
            return PACKAGED_PRODUCT_DEFINITION;
        case PATIENT:
            return PATIENT;
        case PAYMENT_NOTICE:
            return PAYMENT_NOTICE;
        case PAYMENT_RECONCILIATION:
            return PAYMENT_RECONCILIATION;
        case PERMISSION:
            return PERMISSION;
        case PERSON:
            return PERSON;
        case PLAN_DEFINITION:
            return PLAN_DEFINITION;
        case PRACTITIONER:
            return PRACTITIONER;
        case PRACTITIONER_ROLE:
            return PRACTITIONER_ROLE;
        case PROCEDURE:
            return PROCEDURE;
        case PROVENANCE:
            return PROVENANCE;
        case QUESTIONNAIRE:
            return QUESTIONNAIRE;
        case QUESTIONNAIRE_RESPONSE:
            return QUESTIONNAIRE_RESPONSE;
        case REGULATED_AUTHORIZATION:
            return REGULATED_AUTHORIZATION;
        case RELATED_PERSON:
            return RELATED_PERSON;
        case REQUEST_ORCHESTRATION:
            return REQUEST_ORCHESTRATION;
        case REQUIREMENTS:
            return REQUIREMENTS;
        case RESEARCH_STUDY:
            return RESEARCH_STUDY;
        case RESEARCH_SUBJECT:
            return RESEARCH_SUBJECT;
        case RISK_ASSESSMENT:
            return RISK_ASSESSMENT;
        case SCHEDULE:
            return SCHEDULE;
        case SEARCH_PARAMETER:
            return SEARCH_PARAMETER;
        case SERVICE_REQUEST:
            return SERVICE_REQUEST;
        case SLOT:
            return SLOT;
        case SPECIMEN:
            return SPECIMEN;
        case SPECIMEN_DEFINITION:
            return SPECIMEN_DEFINITION;
        case STRUCTURE_DEFINITION:
            return STRUCTURE_DEFINITION;
        case STRUCTURE_MAP:
            return STRUCTURE_MAP;
        case SUBSCRIPTION:
            return SUBSCRIPTION;
        case SUBSCRIPTION_STATUS:
            return SUBSCRIPTION_STATUS;
        case SUBSCRIPTION_TOPIC:
            return SUBSCRIPTION_TOPIC;
        case SUBSTANCE:
            return SUBSTANCE;
        case SUBSTANCE_DEFINITION:
            return SUBSTANCE_DEFINITION;
        case SUBSTANCE_NUCLEIC_ACID:
            return SUBSTANCE_NUCLEIC_ACID;
        case SUBSTANCE_POLYMER:
            return SUBSTANCE_POLYMER;
        case SUBSTANCE_PROTEIN:
            return SUBSTANCE_PROTEIN;
        case SUBSTANCE_REFERENCE_INFORMATION:
            return SUBSTANCE_REFERENCE_INFORMATION;
        case SUBSTANCE_SOURCE_MATERIAL:
            return SUBSTANCE_SOURCE_MATERIAL;
        case SUPPLY_DELIVERY:
            return SUPPLY_DELIVERY;
        case SUPPLY_REQUEST:
            return SUPPLY_REQUEST;
        case TASK:
            return TASK;
        case TERMINOLOGY_CAPABILITIES:
            return TERMINOLOGY_CAPABILITIES;
        case TEST_PLAN:
            return TEST_PLAN;
        case TEST_REPORT:
            return TEST_REPORT;
        case TEST_SCRIPT:
            return TEST_SCRIPT;
        case TRANSPORT:
            return TRANSPORT;
        case VALUE_SET:
            return VALUE_SET;
        case VERIFICATION_RESULT:
            return VERIFICATION_RESULT;
        case VISION_PRESCRIPTION:
            return VISION_PRESCRIPTION;
        case PARAMETERS:
            return PARAMETERS;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating FHIRTypes objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static FHIRTypes of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating FHIRTypes objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating FHIRTypes objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(Value.from(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FHIRTypes other = (FHIRTypes) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(Value.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for FHIRTypes
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public FHIRTypes build() {
            FHIRTypes fHIRTypes = new FHIRTypes(this);
            if (validating) {
                validate(fHIRTypes);
            }
            return fHIRTypes;
        }

        protected void validate(FHIRTypes fHIRTypes) {
            super.validate(fHIRTypes);
        }

        protected Builder from(FHIRTypes fHIRTypes) {
            super.from(fHIRTypes);
            return this;
        }
    }

    public enum Value {
        /**
         * Base
         * 
         * <p>Base Type: Base definition for all types defined in FHIR type system.
         */
        BASE("Base"),

        /**
         * Element
         * 
         * <p>Element Type: Base definition for all elements in a resource.
         */
        ELEMENT("Element"),

        /**
         * BackboneElement
         * 
         * <p>BackboneElement Type: Base definition for all elements that are defined inside a resource - but not those in a data 
         * type.
         */
        BACKBONE_ELEMENT("BackboneElement"),

        /**
         * DataType
         * 
         * <p>DataType Type: The base class for all re-useable types defined as part of the FHIR Specification.
         */
        DATA_TYPE("DataType"),

        /**
         * Address
         * 
         * <p>Address Type: An address expressed using postal conventions (as opposed to GPS or other location definition 
         * formats). This data type may be used to convey addresses for use in delivering mail as well as for visiting locations 
         * which might not be valid for mail delivery. There are a variety of postal address formats defined around the world.

         * The ISO21090-codedString may be used to provide a coded representation of the contents of strings in an Address.
         */
        ADDRESS("Address"),

        /**
         * Annotation
         * 
         * <p>Annotation Type: A text note which also contains information about who made the statement and when.
         */
        ANNOTATION("Annotation"),

        /**
         * Attachment
         * 
         * <p>Attachment Type: For referring to data content defined in other formats.
         */
        ATTACHMENT("Attachment"),

        /**
         * Availability
         * 
         * <p>Availability Type: Availability data for an {item}.
         */
        AVAILABILITY("Availability"),

        /**
         * BackboneType
         * 
         * <p>BackboneType Type: Base definition for the few data types that are allowed to carry modifier extensions.
         */
        BACKBONE_TYPE("BackboneType"),

        /**
         * Dosage
         * 
         * <p>Dosage Type: Indicates how the medication is/was taken or should be taken by the patient.
         */
        DOSAGE("Dosage"),

        /**
         * ElementDefinition
         * 
         * <p>ElementDefinition Type: Captures constraints on each element within the resource, profile, or extension.
         */
        ELEMENT_DEFINITION("ElementDefinition"),

        /**
         * MarketingStatus
         * 
         * <p>MarketingStatus Type: The marketing status describes the date when a medicinal product is actually put on the 
         * market or the date as of which it is no longer available.
         */
        MARKETING_STATUS("MarketingStatus"),

        /**
         * ProductShelfLife
         * 
         * <p>ProductShelfLife Type: The shelf-life and storage information for a medicinal product item or container can be 
         * described using this class.
         */
        PRODUCT_SHELF_LIFE("ProductShelfLife"),

        /**
         * Timing
         * 
         * <p>Timing Type: Specifies an event that may occur multiple times. Timing schedules are used to record when things are 
         * planned, expected or requested to occur. The most common usage is in dosage instructions for medications. They are 
         * also used when planning care of various kinds, and may be used for reporting the schedule to which past regular 
         * activities were carried out.
         */
        TIMING("Timing"),

        /**
         * CodeableConcept
         * 
         * <p>CodeableConcept Type: A concept that may be defined by a formal reference to a terminology or ontology or may be 
         * provided by text.
         */
        CODEABLE_CONCEPT("CodeableConcept"),

        /**
         * CodeableReference
         * 
         * <p>CodeableReference Type: A reference to a resource (by instance), or instead, a reference to a concept defined in a 
         * terminology or ontology (by class).
         */
        CODEABLE_REFERENCE("CodeableReference"),

        /**
         * Coding
         * 
         * <p>Coding Type: A reference to a code defined by a terminology system.
         */
        CODING("Coding"),

        /**
         * ContactDetail
         * 
         * <p>ContactDetail Type: Specifies contact information for a person or organization.
         */
        CONTACT_DETAIL("ContactDetail"),

        /**
         * ContactPoint
         * 
         * <p>ContactPoint Type: Details for all kinds of technology mediated contact points for a person or organization, 
         * including telephone, email, etc.
         */
        CONTACT_POINT("ContactPoint"),

        /**
         * Contributor
         * 
         * <p>Contributor Type: A contributor to the content of a knowledge asset, including authors, editors, reviewers, and 
         * endorsers.
         */
        CONTRIBUTOR("Contributor"),

        /**
         * DataRequirement
         * 
         * <p>DataRequirement Type: Describes a required data item for evaluation in terms of the type of data, and optional code 
         * or date-based filters of the data.
         */
        DATA_REQUIREMENT("DataRequirement"),

        /**
         * Expression
         * 
         * <p>Expression Type: A expression that is evaluated in a specified context and returns a value. The context of use of 
         * the expression must specify the context in which the expression is evaluated, and how the result of the expression is 
         * used.
         */
        EXPRESSION("Expression"),

        /**
         * ExtendedContactDetail
         * 
         * <p>ExtendedContactDetail Type: Specifies contact information for a specific purpose over a period of time, might be 
         * handled/monitored by a specific named person or organization.
         */
        EXTENDED_CONTACT_DETAIL("ExtendedContactDetail"),

        /**
         * Extension
         * 
         * <p>Extension Type: Optional Extension Element - found in all resources.
         */
        EXTENSION("Extension"),

        /**
         * HumanName
         * 
         * <p>HumanName Type: A name, normally of a human, that can be used for other living entities (e.g. animals but not 
         * organizations) that have been assigned names by a human and may need the use of name parts or the need for usage 
         * information.
         */
        HUMAN_NAME("HumanName"),

        /**
         * Identifier
         * 
         * <p>Identifier Type: An identifier - identifies some entity uniquely and unambiguously. Typically this is used for 
         * business identifiers.
         */
        IDENTIFIER("Identifier"),

        /**
         * Meta
         * 
         * <p>Meta Type: The metadata about a resource. This is content in the resource that is maintained by the infrastructure. 
         * Changes to the content might not always be associated with version changes to the resource.
         */
        META("Meta"),

        /**
         * MonetaryComponent
         * 
         * <p>MonetaryComponent Type: Availability data for an {item}.
         */
        MONETARY_COMPONENT("MonetaryComponent"),

        /**
         * Money
         * 
         * <p>Money Type: An amount of economic utility in some recognized currency.
         */
        MONEY("Money"),

        /**
         * Narrative
         * 
         * <p>Narrative Type: A human-readable summary of the resource conveying the essential clinical and business information 
         * for the resource.
         */
        NARRATIVE("Narrative"),

        /**
         * ParameterDefinition
         * 
         * <p>ParameterDefinition Type: The parameters to the module. This collection specifies both the input and output 
         * parameters. Input parameters are provided by the caller as part of the $evaluate operation. Output parameters are 
         * included in the GuidanceResponse.
         */
        PARAMETER_DEFINITION("ParameterDefinition"),

        /**
         * Period
         * 
         * <p>Period Type: A time period defined by a start and end date and optionally time.
         */
        PERIOD("Period"),

        /**
         * PrimitiveType
         * 
         * <p>PrimitiveType Type: The base type for all re-useable types defined that have a simple property.
         */
        PRIMITIVE_TYPE("PrimitiveType"),

        /**
         * base64Binary
         * 
         * <p>base64Binary Type: A stream of bytes
         */
        BASE64BINARY("base64Binary"),

        /**
         * boolean
         * 
         * <p>boolean Type: Value of "true" or "false"
         */
        BOOLEAN("boolean"),

        /**
         * date
         * 
         * <p>date Type: A date or partial date (e.g. just year or year + month). There is no UTC offset. The format is a union 
         * of the schema types gYear, gYearMonth and date. Dates SHALL be valid dates.
         */
        DATE("date"),

        /**
         * dateTime
         * 
         * <p>dateTime Type: A date, date-time or partial date (e.g. just year or year + month). If hours and minutes are 
         * specified, a UTC offset SHALL be populated. The format is a union of the schema types gYear, gYearMonth, date and 
         * dateTime. Seconds must be provided due to schema type constraints but may be zero-filled and may be ignored. Dates 
         * SHALL be valid dates.
         */
        DATE_TIME("dateTime"),

        /**
         * decimal
         * 
         * <p>decimal Type: A rational number with implicit precision
         */
        DECIMAL("decimal"),

        /**
         * instant
         * 
         * <p>instant Type: An instant in time - known at least to the second
         */
        INSTANT("instant"),

        /**
         * integer
         * 
         * <p>integer Type: A whole number
         */
        INTEGER("integer"),

        /**
         * positiveInt
         * 
         * <p>positiveInt type: An integer with a value that is positive (e.g. &gt;0)
         */
        POSITIVE_INT("positiveInt"),

        /**
         * unsignedInt
         * 
         * <p>unsignedInt type: An integer with a value that is not negative (e.g. &gt;= 0)
         */
        UNSIGNED_INT("unsignedInt"),

        /**
         * integer64
         * 
         * <p>integer64 Type: A very large whole number
         */
        INTEGER64("integer64"),

        /**
         * string
         * 
         * <p>string Type: A sequence of Unicode characters
         */
        STRING("string"),

        /**
         * code
         * 
         * <p>code type: A string which has at least one character and no leading or trailing whitespace and where there is no 
         * whitespace other than single spaces in the contents
         */
        CODE("code"),

        /**
         * id
         * 
         * <p>id type: Any combination of letters, numerals, "-" and ".", with a length limit of 64 characters. (This might be an 
         * integer, an unprefixed OID, UUID or any other identifier pattern that meets these constraints.) Ids are case-
         * insensitive.
         */
        ID("id"),

        /**
         * markdown
         * 
         * <p>markdown type: A string that may contain Github Flavored Markdown syntax for optional processing by a mark down 
         * presentation engine
         */
        MARKDOWN("markdown"),

        /**
         * time
         * 
         * <p>time Type: A time during the day, with no date specified
         */
        TIME("time"),

        /**
         * uri
         * 
         * <p>uri Type: String of characters used to identify a name or a resource
         */
        URI("uri"),

        /**
         * canonical
         * 
         * <p>canonical type: A URI that is a reference to a canonical URL on a FHIR resource
         */
        CANONICAL("canonical"),

        /**
         * oid
         * 
         * <p>oid type: An OID represented as a URI
         */
        OID("oid"),

        /**
         * url
         * 
         * <p>url type: A URI that is a literal reference
         */
        URL("url"),

        /**
         * uuid
         * 
         * <p>uuid type: A UUID, represented as a URI
         */
        UUID("uuid"),

        /**
         * Quantity
         * 
         * <p>Quantity Type: A measured amount (or an amount that can potentially be measured). Note that measured amounts 
         * include amounts that are not precisely quantified, including amounts involving arbitrary units and floating currencies.
         */
        QUANTITY("Quantity"),

        /**
         * Age
         * 
         * <p>Age Type: A duration of time during which an organism (or a process) has existed.
         */
        AGE("Age"),

        /**
         * Count
         * 
         * <p>Count Type: A measured amount (or an amount that can potentially be measured). Note that measured amounts include 
         * amounts that are not precisely quantified, including amounts involving arbitrary units and floating currencies.
         */
        COUNT("Count"),

        /**
         * Distance
         * 
         * <p>Distance Type: A length - a value with a unit that is a physical distance.
         */
        DISTANCE("Distance"),

        /**
         * Duration
         * 
         * <p>Duration Type: A length of time.
         */
        DURATION("Duration"),

        /**
         * Range
         * 
         * <p>Range Type: A set of ordered Quantities defined by a low and high limit.
         */
        RANGE("Range"),

        /**
         * Ratio
         * 
         * <p>Ratio Type: A relationship of two Quantity values - expressed as a numerator and a denominator.
         */
        RATIO("Ratio"),

        /**
         * RatioRange
         * 
         * <p>RatioRange Type: A range of ratios expressed as a low and high numerator and a denominator.
         */
        RATIO_RANGE("RatioRange"),

        /**
         * Reference
         * 
         * <p>Reference Type: A reference from one resource to another.
         */
        REFERENCE("Reference"),

        /**
         * RelatedArtifact
         * 
         * <p>RelatedArtifact Type: Related artifacts such as additional documentation, justification, or bibliographic 
         * references.
         */
        RELATED_ARTIFACT("RelatedArtifact"),

        /**
         * SampledData
         * 
         * <p>SampledData Type: A series of measurements taken by a device, with upper and lower limits. There may be more than 
         * one dimension in the data.
         */
        SAMPLED_DATA("SampledData"),

        /**
         * Signature
         * 
         * <p>Signature Type: A signature along with supporting context. The signature may be a digital signature that is 
         * cryptographic in nature, or some other signature acceptable to the domain. This other signature may be as simple as a 
         * graphical image representing a hand-written signature, or a signature ceremony Different signature approaches have 
         * different utilities.
         */
        SIGNATURE("Signature"),

        /**
         * TriggerDefinition
         * 
         * <p>TriggerDefinition Type: A description of a triggering event. Triggering events can be named events, data events, or 
         * periodic, as determined by the type element.
         */
        TRIGGER_DEFINITION("TriggerDefinition"),

        /**
         * UsageContext
         * 
         * <p>UsageContext Type: Specifies clinical/business/etc. metadata that can be used to retrieve, index and/or categorize 
         * an artifact. This metadata can either be specific to the applicable population (e.g., age category, DRG) or the 
         * specific context of care (e.g., venue, care setting, provider of care).
         */
        USAGE_CONTEXT("UsageContext"),

        /**
         * VirtualServiceDetail
         * 
         * <p>VirtualServiceDetail Type: Virtual Service Contact Details.
         */
        VIRTUAL_SERVICE_DETAIL("VirtualServiceDetail"),

        /**
         * xhtml
         * 
         * <p>xhtml Type definition
         */
        XHTML("xhtml"),

        /**
         * Resource
         * 
         * <p>This is the base resource type for everything.
         */
        RESOURCE("Resource"),

        /**
         * Binary
         * 
         * <p>A resource that represents the data of a single raw artifact as digital content accessible in its native format. A 
         * Binary resource can contain any content, whether text, image, pdf, zip archive, etc.
         */
        BINARY("Binary"),

        /**
         * Bundle
         * 
         * <p>A container for a collection of resources.
         */
        BUNDLE("Bundle"),

        /**
         * DomainResource
         * 
         * <p>A resource that includes narrative, extensions, and contained resources.
         */
        DOMAIN_RESOURCE("DomainResource"),

        /**
         * Account
         * 
         * <p>A financial tool for tracking value accrued for a particular purpose. In the healthcare field, used to track 
         * charges for a patient, cost centers, etc.
         */
        ACCOUNT("Account"),

        /**
         * ActivityDefinition
         * 
         * <p>This resource allows for the definition of some activity to be performed, independent of a particular patient, 
         * practitioner, or other performance context.
         */
        ACTIVITY_DEFINITION("ActivityDefinition"),

        /**
         * ActorDefinition
         * 
         * <p>The ActorDefinition resource is used to describe an actor - a human or an application that plays a role in data 
         * exchange, and that may have obligations associated with the role the actor plays.
         */
        ACTOR_DEFINITION("ActorDefinition"),

        /**
         * AdministrableProductDefinition
         * 
         * <p>A medicinal product in the final form which is suitable for administering to a patient (after any mixing of 
         * multiple components, dissolution etc. has been performed).
         */
        ADMINISTRABLE_PRODUCT_DEFINITION("AdministrableProductDefinition"),

        /**
         * AdverseEvent
         * 
         * <p>An event (i.e. any change to current patient status) that may be related to unintended effects on a patient or 
         * research participant. The unintended effects may require additional monitoring, treatment, hospitalization, or may 
         * result in death. The AdverseEvent resource also extends to potential or avoided events that could have had such 
         * effects. There are two major domains where the AdverseEvent resource is expected to be used. One is in clinical care 
         * reported adverse events and the other is in reporting adverse events in clinical research trial management. Adverse 
         * events can be reported by healthcare providers, patients, caregivers or by medical products manufacturers. Given the 
         * differences between these two concepts, we recommend consulting the domain specific implementation guides when 
         * implementing the AdverseEvent Resource. The implementation guides include specific extensions, value sets and 
         * constraints.
         */
        ADVERSE_EVENT("AdverseEvent"),

        /**
         * AllergyIntolerance
         * 
         * <p>Risk of harmful or undesirable, physiological response which is unique to an individual and associated with 
         * exposure to a substance.
         */
        ALLERGY_INTOLERANCE("AllergyIntolerance"),

        /**
         * Appointment
         * 
         * <p>A booking of a healthcare event among patient(s), practitioner(s), related person(s) and/or device(s) for a 
         * specific date/time. This may result in one or more Encounter(s).
         */
        APPOINTMENT("Appointment"),

        /**
         * AppointmentResponse
         * 
         * <p>A reply to an appointment request for a patient and/or practitioner(s), such as a confirmation or rejection.
         */
        APPOINTMENT_RESPONSE("AppointmentResponse"),

        /**
         * ArtifactAssessment
         * 
         * <p>This Resource provides one or more comments, classifiers or ratings about a Resource and supports attribution and 
         * rights management metadata for the added content.
         */
        ARTIFACT_ASSESSMENT("ArtifactAssessment"),

        /**
         * AuditEvent
         * 
         * <p>A record of an event relevant for purposes such as operations, privacy, security, maintenance, and performance 
         * analysis.
         */
        AUDIT_EVENT("AuditEvent"),

        /**
         * Basic
         * 
         * <p>Basic is used for handling concepts not yet defined in FHIR, narrative-only resources that don't map to an existing 
         * resource, and custom resources not appropriate for inclusion in the FHIR specification.
         */
        BASIC("Basic"),

        /**
         * BiologicallyDerivedProduct
         * 
         * <p>A biological material originating from a biological entity intended to be transplanted or infused into another 
         * (possibly the same) biological entity.
         */
        BIOLOGICALLY_DERIVED_PRODUCT("BiologicallyDerivedProduct"),

        /**
         * BiologicallyDerivedProductDispense
         * 
         * <p>A record of dispensation of a biologically derived product.
         */
        BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE("BiologicallyDerivedProductDispense"),

        /**
         * BodyStructure
         * 
         * <p>Record details about an anatomical structure. This resource may be used when a coded concept does not provide the 
         * necessary detail needed for the use case.
         */
        BODY_STRUCTURE("BodyStructure"),

        /**
         * CanonicalResource
         * 
         * <p>Common Interface declaration for conformance and knowledge artifact resources.
         */
        CANONICAL_RESOURCE("CanonicalResource"),

        /**
         * CapabilityStatement
         * 
         * <p>A Capability Statement documents a set of capabilities (behaviors) of a FHIR Server or Client for a particular 
         * version of FHIR that may be used as a statement of actual server functionality or a statement of required or desired 
         * server implementation.
         */
        CAPABILITY_STATEMENT("CapabilityStatement"),

        /**
         * CarePlan
         * 
         * <p>Describes the intention of how one or more practitioners intend to deliver care for a particular patient, group or 
         * community for a period of time, possibly limited to care for a specific condition or set of conditions.
         */
        CARE_PLAN("CarePlan"),

        /**
         * CareTeam
         * 
         * <p>The Care Team includes all the people and organizations who plan to participate in the coordination and delivery of 
         * care.
         */
        CARE_TEAM("CareTeam"),

        /**
         * ChargeItem
         * 
         * <p>The resource ChargeItem describes the provision of healthcare provider products for a certain patient, therefore 
         * referring not only to the product, but containing in addition details of the provision, like date, time, amounts and 
         * participating organizations and persons. Main Usage of the ChargeItem is to enable the billing process and internal 
         * cost allocation.
         */
        CHARGE_ITEM("ChargeItem"),

        /**
         * ChargeItemDefinition
         * 
         * <p>The ChargeItemDefinition resource provides the properties that apply to the (billing) codes necessary to calculate 
         * costs and prices. The properties may differ largely depending on type and realm, therefore this resource gives only a 
         * rough structure and requires profiling for each type of billing code system.
         */
        CHARGE_ITEM_DEFINITION("ChargeItemDefinition"),

        /**
         * Citation
         * 
         * <p>The Citation Resource enables reference to any knowledge artifact for purposes of identification and attribution. 
         * The Citation Resource supports existing reference structures and developing publication practices such as versioning, 
         * expressing complex contributorship roles, and referencing computable resources.
         */
        CITATION("Citation"),

        /**
         * Claim
         * 
         * <p>A provider issued list of professional services and products which have been provided, or are to be provided, to a 
         * patient which is sent to an insurer for reimbursement.
         */
        CLAIM("Claim"),

        /**
         * ClaimResponse
         * 
         * <p>This resource provides the adjudication details from the processing of a Claim resource.
         */
        CLAIM_RESPONSE("ClaimResponse"),

        /**
         * ClinicalImpression
         * 
         * <p>A record of a clinical assessment performed to determine what problem(s) may affect the patient and before planning 
         * the treatments or management strategies that are best to manage a patient's condition. Assessments are often 1:1 with 
         * a clinical consultation / encounter, but this varies greatly depending on the clinical workflow. This resource is 
         * called "ClinicalImpression" rather than "ClinicalAssessment" to avoid confusion with the recording of assessment tools 
         * such as Apgar score.
         */
        CLINICAL_IMPRESSION("ClinicalImpression"),

        /**
         * ClinicalUseDefinition
         * 
         * <p>A single issue - either an indication, contraindication, interaction or an undesirable effect for a medicinal 
         * product, medication, device or procedure.
         */
        CLINICAL_USE_DEFINITION("ClinicalUseDefinition"),

        /**
         * CodeSystem
         * 
         * <p>The CodeSystem resource is used to declare the existence of and describe a code system or code system supplement 
         * and its key properties, and optionally define a part or all of its content.
         */
        CODE_SYSTEM("CodeSystem"),

        /**
         * Communication
         * 
         * <p>A clinical or business level record of information being transmitted or shared; e.g. an alert that was sent to a 
         * responsible provider, a public health agency communication to a provider/reporter in response to a case report for a 
         * reportable condition.
         */
        COMMUNICATION("Communication"),

        /**
         * CommunicationRequest
         * 
         * <p>A request to convey information; e.g. the CDS system proposes that an alert be sent to a responsible provider, the 
         * CDS system proposes that the public health agency be notified about a reportable condition.
         */
        COMMUNICATION_REQUEST("CommunicationRequest"),

        /**
         * CompartmentDefinition
         * 
         * <p>A compartment definition that defines how resources are accessed on a server.
         */
        COMPARTMENT_DEFINITION("CompartmentDefinition"),

        /**
         * Composition
         * 
         * <p>A set of healthcare-related information that is assembled together into a single logical package that provides a 
         * single coherent statement of meaning, establishes its own context and that has clinical attestation with regard to who 
         * is making the statement. A Composition defines the structure and narrative content necessary for a document. However, 
         * a Composition alone does not constitute a document. Rather, the Composition must be the first entry in a Bundle where 
         * Bundle.type=document, and any other resources referenced from Composition must be included as subsequent entries in 
         * the Bundle (for example Patient, Practitioner, Encounter, etc.).
         */
        COMPOSITION("Composition"),

        /**
         * ConceptMap
         * 
         * <p>A statement of relationships from one set of concepts to one or more other concepts - either concepts in code 
         * systems, or data element/data element concepts, or classes in class models.
         */
        CONCEPT_MAP("ConceptMap"),

        /**
         * Condition
         * 
         * <p>A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept that has risen to a 
         * level of concern.
         */
        CONDITION("Condition"),

        /**
         * ConditionDefinition
         * 
         * <p>A definition of a condition and information relevant to managing it.
         */
        CONDITION_DEFINITION("ConditionDefinition"),

        /**
         * Consent
         * 
         * <p>A record of a healthcare consumer’s choices or choices made on their behalf by a third party, which permits or 
         * denies identified recipient(s) or recipient role(s) to perform one or more actions within a given policy context, for 
         * specific purposes and periods of time.
         */
        CONSENT("Consent"),

        /**
         * Contract
         * 
         * <p>Legally enforceable, formally recorded unilateral or bilateral directive i.e., a policy or agreement.
         */
        CONTRACT("Contract"),

        /**
         * Coverage
         * 
         * <p>Financial instrument which may be used to reimburse or pay for health care products and services. Includes both 
         * insurance and self-payment.
         */
        COVERAGE("Coverage"),

        /**
         * CoverageEligibilityRequest
         * 
         * <p>The CoverageEligibilityRequest provides patient and insurance coverage information to an insurer for them to 
         * respond, in the form of an CoverageEligibilityResponse, with information regarding whether the stated coverage is 
         * valid and in-force and optionally to provide the insurance details of the policy.
         */
        COVERAGE_ELIGIBILITY_REQUEST("CoverageEligibilityRequest"),

        /**
         * CoverageEligibilityResponse
         * 
         * <p>This resource provides eligibility and plan details from the processing of an CoverageEligibilityRequest resource.
         */
        COVERAGE_ELIGIBILITY_RESPONSE("CoverageEligibilityResponse"),

        /**
         * DetectedIssue
         * 
         * <p>Indicates an actual or potential clinical issue with or between one or more active or proposed clinical actions for 
         * a patient; e.g. Drug-drug interaction, Ineffective treatment frequency, Procedure-condition conflict, gaps in care, 
         * etc.
         */
        DETECTED_ISSUE("DetectedIssue"),

        /**
         * Device
         * 
         * <p>This resource describes the properties (regulated, has real time clock, etc.), adminstrative (manufacturer name, 
         * model number, serial number, firmware, etc.), and type (knee replacement, blood pressure cuff, MRI, etc.) of a 
         * physical unit (these values do not change much within a given module, for example the serail number, manufacturer 
         * name, and model number). An actual unit may consist of several modules in a distinct hierarchy and these are 
         * represented by multiple Device resources and bound through the 'parent' element.
         */
        DEVICE("Device"),

        /**
         * DeviceAssociation
         * 
         * <p>A record of association of a device.
         */
        DEVICE_ASSOCIATION("DeviceAssociation"),

        /**
         * DeviceDefinition
         * 
         * <p>This is a specialized resource that defines the characteristics and capabilities of a device.
         */
        DEVICE_DEFINITION("DeviceDefinition"),

        /**
         * DeviceDispense
         * 
         * <p>Indicates that a device is to be or has been dispensed for a named person/patient. This includes a description of 
         * the product (supply) provided and the instructions for using the device.
         */
        DEVICE_DISPENSE("DeviceDispense"),

        /**
         * DeviceMetric
         * 
         * <p>Describes a measurement, calculation or setting capability of a device. The DeviceMetric resource is derived from 
         * the ISO/IEEE 11073-10201 Domain Information Model standard, but is more widely applicable. 
         */
        DEVICE_METRIC("DeviceMetric"),

        /**
         * DeviceRequest
         * 
         * <p>Represents a request a device to be provided to a specific patient. The device may be an implantable device to be 
         * subsequently implanted, or an external assistive device, such as a walker, to be delivered and subsequently be used.
         */
        DEVICE_REQUEST("DeviceRequest"),

        /**
         * DeviceUsage
         * 
         * <p>A record of a device being used by a patient where the record is the result of a report from the patient or a 
         * clinician.
         */
        DEVICE_USAGE("DeviceUsage"),

        /**
         * DiagnosticReport
         * 
         * <p>The findings and interpretation of diagnostic tests performed on patients, groups of patients, products, 
         * substances, devices, and locations, and/or specimens derived from these. The report includes clinical context such as 
         * requesting provider information, and some mix of atomic results, images, textual and coded interpretations, and 
         * formatted representation of diagnostic reports. The report also includes non-clinical context such as batch analysis 
         * and stability reporting of products and substances.
         */
        DIAGNOSTIC_REPORT("DiagnosticReport"),

        /**
         * DocumentReference
         * 
         * <p>A reference to a document of any kind for any purpose. While the term “document�? implies a more narrow focus, 
         * for this resource this “document�? encompasses *any* serialized object with a mime-type, it includes formal patient-
         * centric documents (CDA), clinical notes, scanned paper, non-patient specific documents like policy text, as well as a 
         * photo, video, or audio recording acquired or used in healthcare. The DocumentReference resource provides metadata 
         * about the document so that the document can be discovered and managed. The actual content may be inline base64 encoded 
         * data or provided by direct reference.
         */
        DOCUMENT_REFERENCE("DocumentReference"),

        /**
         * Encounter
         * 
         * <p>An interaction between healthcare provider(s), and/or patient(s) for the purpose of providing healthcare service(s) 
         * or assessing the health status of patient(s).
         */
        ENCOUNTER("Encounter"),

        /**
         * EncounterHistory
         * 
         * <p>A record of significant events/milestones key data throughout the history of an Encounter
         */
        ENCOUNTER_HISTORY("EncounterHistory"),

        /**
         * Endpoint
         * 
         * <p>The technical details of an endpoint that can be used for electronic services, such as for web services providing 
         * XDS.b, a REST endpoint for another FHIR server, or a s/Mime email address. This may include any security context 
         * information.
         */
        ENDPOINT("Endpoint"),

        /**
         * EnrollmentRequest
         * 
         * <p>This resource provides the insurance enrollment details to the insurer regarding a specified coverage.
         */
        ENROLLMENT_REQUEST("EnrollmentRequest"),

        /**
         * EnrollmentResponse
         * 
         * <p>This resource provides enrollment and plan details from the processing of an EnrollmentRequest resource.
         */
        ENROLLMENT_RESPONSE("EnrollmentResponse"),

        /**
         * EpisodeOfCare
         * 
         * <p>An association between a patient and an organization / healthcare provider(s) during which time encounters may 
         * occur. The managing organization assumes a level of responsibility for the patient during this time.
         */
        EPISODE_OF_CARE("EpisodeOfCare"),

        /**
         * EventDefinition
         * 
         * <p>The EventDefinition resource provides a reusable description of when a particular event can occur.
         */
        EVENT_DEFINITION("EventDefinition"),

        /**
         * Evidence
         * 
         * <p>The Evidence Resource provides a machine-interpretable expression of an evidence concept including the evidence 
         * variables (e.g., population, exposures/interventions, comparators, outcomes, measured variables, confounding 
         * variables), the statistics, and the certainty of this evidence.
         */
        EVIDENCE("Evidence"),

        /**
         * EvidenceReport
         * 
         * <p>The EvidenceReport Resource is a specialized container for a collection of resources and codeable concepts, adapted 
         * to support compositions of Evidence, EvidenceVariable, and Citation resources and related concepts.
         */
        EVIDENCE_REPORT("EvidenceReport"),

        /**
         * EvidenceVariable
         * 
         * <p>The EvidenceVariable resource describes an element that knowledge (Evidence) is about.
         */
        EVIDENCE_VARIABLE("EvidenceVariable"),

        /**
         * ExampleScenario
         * 
         * <p>A walkthrough of a workflow showing the interaction between systems and the instances shared, possibly including 
         * the evolution of instances over time.
         */
        EXAMPLE_SCENARIO("ExampleScenario"),

        /**
         * ExplanationOfBenefit
         * 
         * <p>This resource provides: the claim details; adjudication details from the processing of a Claim; and optionally 
         * account balance information, for informing the subscriber of the benefits provided.
         */
        EXPLANATION_OF_BENEFIT("ExplanationOfBenefit"),

        /**
         * FamilyMemberHistory
         * 
         * <p>Significant health conditions for a person related to the patient relevant in the context of care for the patient.
         */
        FAMILY_MEMBER_HISTORY("FamilyMemberHistory"),

        /**
         * Flag
         * 
         * <p>Prospective warnings of potential issues when providing care to the patient.
         */
        FLAG("Flag"),

        /**
         * FormularyItem
         * 
         * <p>This resource describes a product or service that is available through a program and includes the conditions and 
         * constraints of availability. All of the information in this resource is specific to the inclusion of the item in the 
         * formulary and is not inherent to the item itself.
         */
        FORMULARY_ITEM("FormularyItem"),

        /**
         * GenomicStudy
         * 
         * <p>A set of analyses performed to analyze and generate genomic data.
         */
        GENOMIC_STUDY("GenomicStudy"),

        /**
         * Goal
         * 
         * <p>Describes the intended objective(s) for a patient, group or organization care, for example, weight loss, restoring 
         * an activity of daily living, obtaining herd immunity via immunization, meeting a process improvement objective, etc.
         */
        GOAL("Goal"),

        /**
         * GraphDefinition
         * 
         * <p>A formal computable definition of a graph of resources - that is, a coherent set of resources that form a graph by 
         * following references. The Graph Definition resource defines a set and makes rules about the set.
         */
        GRAPH_DEFINITION("GraphDefinition"),

        /**
         * Group
         * 
         * <p>Represents a defined collection of entities that may be discussed or acted upon collectively but which are not 
         * expected to act collectively, and are not formally or legally recognized; i.e. a collection of entities that isn't an 
         * Organization.
         */
        GROUP("Group"),

        /**
         * GuidanceResponse
         * 
         * <p>A guidance response is the formal response to a guidance request, including any output parameters returned by the 
         * evaluation, as well as the description of any proposed actions to be taken.
         */
        GUIDANCE_RESPONSE("GuidanceResponse"),

        /**
         * HealthcareService
         * 
         * <p>The details of a healthcare service available at a location or in a catalog. In the case where there is a hierarchy 
         * of services (for example, Lab -&gt; Pathology -&gt; Wound Cultures), this can be represented using a set of linked 
         * HealthcareServices.
         */
        HEALTHCARE_SERVICE("HealthcareService"),

        /**
         * ImagingSelection
         * 
         * <p>A selection of DICOM SOP instances and/or frames within a single Study and Series. This might include additional 
         * specifics such as an image region, an Observation UID or a Segmentation Number, allowing linkage to an Observation 
         * Resource or transferring this information along with the ImagingStudy Resource.
         */
        IMAGING_SELECTION("ImagingSelection"),

        /**
         * ImagingStudy
         * 
         * <p>Representation of the content produced in a DICOM imaging study. A study comprises a set of series, each of which 
         * includes a set of Service-Object Pair Instances (SOP Instances - images or other data) acquired or produced in a 
         * common context. A series is of only one modality (e.g. X-ray, CT, MR, ultrasound), but a study may have multiple 
         * series of different modalities.
         */
        IMAGING_STUDY("ImagingStudy"),

        /**
         * Immunization
         * 
         * <p>Describes the event of a patient being administered a vaccine or a record of an immunization as reported by a 
         * patient, a clinician or another party.
         */
        IMMUNIZATION("Immunization"),

        /**
         * ImmunizationEvaluation
         * 
         * <p>Describes a comparison of an immunization event against published recommendations to determine if the 
         * administration is "valid" in relation to those recommendations.
         */
        IMMUNIZATION_EVALUATION("ImmunizationEvaluation"),

        /**
         * ImmunizationRecommendation
         * 
         * <p>A patient's point-in-time set of recommendations (i.e. forecasting) according to a published schedule with optional 
         * supporting justification.
         */
        IMMUNIZATION_RECOMMENDATION("ImmunizationRecommendation"),

        /**
         * ImplementationGuide
         * 
         * <p>A set of rules of how a particular interoperability or standards problem is solved - typically through the use of 
         * FHIR resources. This resource is used to gather all the parts of an implementation guide into a logical whole and to 
         * publish a computable definition of all the parts.
         */
        IMPLEMENTATION_GUIDE("ImplementationGuide"),

        /**
         * Ingredient
         * 
         * <p>An ingredient of a manufactured item or pharmaceutical product.
         */
        INGREDIENT("Ingredient"),

        /**
         * InsurancePlan
         * 
         * <p>Details of a Health Insurance product/plan provided by an organization.
         */
        INSURANCE_PLAN("InsurancePlan"),

        /**
         * InventoryItem
         * 
         * <p>functional description of an inventory item used in inventory and supply-related workflows.
         */
        INVENTORY_ITEM("InventoryItem"),

        /**
         * InventoryReport
         * 
         * <p>A report of inventory or stock items.
         */
        INVENTORY_REPORT("InventoryReport"),

        /**
         * Invoice
         * 
         * <p>Invoice containing collected ChargeItems from an Account with calculated individual and total price for Billing 
         * purpose.
         */
        INVOICE("Invoice"),

        /**
         * Library
         * 
         * <p>The Library resource is a general-purpose container for knowledge asset definitions. It can be used to describe and 
         * expose existing knowledge assets such as logic libraries and information model descriptions, as well as to describe a 
         * collection of knowledge assets.
         */
        LIBRARY("Library"),

        /**
         * Linkage
         * 
         * <p>Identifies two or more records (resource instances) that refer to the same real-world "occurrence".
         */
        LINKAGE("Linkage"),

        /**
         * List
         * 
         * <p>A List is a curated collection of resources, for things such as problem lists, allergy lists, facility list, 
         * organization list, etc.
         */
        LIST("List"),

        /**
         * Location
         * 
         * <p>Details and position information for a place where services are provided and resources and participants may be 
         * stored, found, contained, or accommodated.
         */
        LOCATION("Location"),

        /**
         * ManufacturedItemDefinition
         * 
         * <p>The definition and characteristics of a medicinal manufactured item, such as a tablet or capsule, as contained in a 
         * packaged medicinal product.
         */
        MANUFACTURED_ITEM_DEFINITION("ManufacturedItemDefinition"),

        /**
         * Measure
         * 
         * <p>The Measure resource provides the definition of a quality measure.
         */
        MEASURE("Measure"),

        /**
         * MeasureReport
         * 
         * <p>The MeasureReport resource contains the results of the calculation of a measure; and optionally a reference to the 
         * resources involved in that calculation.
         */
        MEASURE_REPORT("MeasureReport"),

        /**
         * Medication
         * 
         * <p>This resource is primarily used for the identification and definition of a medication, including ingredients, for 
         * the purposes of prescribing, dispensing, and administering a medication as well as for making statements about 
         * medication use.
         */
        MEDICATION("Medication"),

        /**
         * MedicationAdministration
         * 
         * <p>Describes the event of a patient consuming or otherwise being administered a medication. This may be as simple as 
         * swallowing a tablet or it may be a long running infusion. Related resources tie this event to the authorizing 
         * prescription, and the specific encounter between patient and health care practitioner. This event can also be used to 
         * record waste using a status of not-done and the appropriate statusReason.
         */
        MEDICATION_ADMINISTRATION("MedicationAdministration"),

        /**
         * MedicationDispense
         * 
         * <p>Indicates that a medication product is to be or has been dispensed for a named person/patient. This includes a 
         * description of the medication product (supply) provided and the instructions for administering the medication. The 
         * medication dispense is the result of a pharmacy system responding to a medication order.
         */
        MEDICATION_DISPENSE("MedicationDispense"),

        /**
         * MedicationKnowledge
         * 
         * <p>Information about a medication that is used to support knowledge.
         */
        MEDICATION_KNOWLEDGE("MedicationKnowledge"),

        /**
         * MedicationRequest
         * 
         * <p>An order or request for both supply of the medication and the instructions for administration of the medication to 
         * a patient. The resource is called "MedicationRequest" rather than "MedicationPrescription" or "MedicationOrder" to 
         * generalize the use across inpatient and outpatient settings, including care plans, etc., and to harmonize with 
         * workflow patterns.
         */
        MEDICATION_REQUEST("MedicationRequest"),

        /**
         * MedicationStatement
         * 
         * <p>A record of a medication that is being consumed by a patient. A MedicationStatement may indicate that the patient 
         * may be taking the medication now or has taken the medication in the past or will be taking the medication in the 
         * future. The source of this information can be the patient, significant other (such as a family member or spouse), or a 
         * clinician. A common scenario where this information is captured is during the history taking process during a patient 
         * visit or stay. The medication information may come from sources such as the patient's memory, from a prescription 
         * bottle, or from a list of medications the patient, clinician or other party maintains. 

The primary difference 
         * between a medicationstatement and a medicationadministration is that the medication administration has complete 
         * administration information and is based on actual administration information from the person who administered the 
         * medication. A medicationstatement is often, if not always, less specific. There is no required date/time when the 
         * medication was administered, in fact we only know that a source has reported the patient is taking this medication, 
         * where details such as time, quantity, or rate or even medication product may be incomplete or missing or less precise. 
         * As stated earlier, the Medication Statement information may come from the patient's memory, from a prescription bottle 
         * or from a list of medications the patient, clinician or other party maintains. Medication administration is more 
         * formal and is not missing detailed information.
         */
        MEDICATION_STATEMENT("MedicationStatement"),

        /**
         * MedicinalProductDefinition
         * 
         * <p>Detailed definition of a medicinal product, typically for uses other than direct patient care (e.g. regulatory use, 
         * drug catalogs, to support prescribing, adverse events management etc.).
         */
        MEDICINAL_PRODUCT_DEFINITION("MedicinalProductDefinition"),

        /**
         * MessageDefinition
         * 
         * <p>Defines the characteristics of a message that can be shared between systems, including the type of event that 
         * initiates the message, the content to be transmitted and what response(s), if any, are permitted.
         */
        MESSAGE_DEFINITION("MessageDefinition"),

        /**
         * MessageHeader
         * 
         * <p>The header for a message exchange that is either requesting or responding to an action. The reference(s) that are 
         * the subject of the action as well as other information related to the action are typically transmitted in a bundle in 
         * which the MessageHeader resource instance is the first resource in the bundle.
         */
        MESSAGE_HEADER("MessageHeader"),

        /**
         * MetadataResource
         * 
         * <p>Common Interface declaration for conformance and knowledge artifact resources.
         */
        METADATA_RESOURCE("MetadataResource"),

        /**
         * MolecularSequence
         * 
         * <p>Representation of a molecular sequence.
         */
        MOLECULAR_SEQUENCE("MolecularSequence"),

        /**
         * NamingSystem
         * 
         * <p>A curated namespace that issues unique symbols within that namespace for the identification of concepts, people, 
         * devices, etc. Represents a "System" used within the Identifier and Coding data types.
         */
        NAMING_SYSTEM("NamingSystem"),

        /**
         * NutritionIntake
         * 
         * <p>A record of food or fluid that is being consumed by a patient. A NutritionIntake may indicate that the patient may 
         * be consuming the food or fluid now or has consumed the food or fluid in the past. The source of this information can 
         * be the patient, significant other (such as a family member or spouse), or a clinician. A common scenario where this 
         * information is captured is during the history taking process during a patient visit or stay or through an app that 
         * tracks food or fluids consumed. The consumption information may come from sources such as the patient's memory, from a 
         * nutrition label, or from a clinician documenting observed intake.
         */
        NUTRITION_INTAKE("NutritionIntake"),

        /**
         * NutritionOrder
         * 
         * <p>A request to supply a diet, formula feeding (enteral) or oral nutritional supplement to a patient/resident.
         */
        NUTRITION_ORDER("NutritionOrder"),

        /**
         * NutritionProduct
         * 
         * <p>A food or supplement that is consumed by patients.
         */
        NUTRITION_PRODUCT("NutritionProduct"),

        /**
         * Observation
         * 
         * <p>Measurements and simple assertions made about a patient, device or other subject.
         */
        OBSERVATION("Observation"),

        /**
         * ObservationDefinition
         * 
         * <p>Set of definitional characteristics for a kind of observation or measurement produced or consumed by an orderable 
         * health care service.
         */
        OBSERVATION_DEFINITION("ObservationDefinition"),

        /**
         * OperationDefinition
         * 
         * <p>A formal computable definition of an operation (on the RESTful interface) or a named query (using the search 
         * interaction).
         */
        OPERATION_DEFINITION("OperationDefinition"),

        /**
         * OperationOutcome
         * 
         * <p>A collection of error, warning, or information messages that result from a system action.
         */
        OPERATION_OUTCOME("OperationOutcome"),

        /**
         * Organization
         * 
         * <p>A formally or informally recognized grouping of people or organizations formed for the purpose of achieving some 
         * form of collective action. Includes companies, institutions, corporations, departments, community groups, healthcare 
         * practice groups, payer/insurer, etc.
         */
        ORGANIZATION("Organization"),

        /**
         * OrganizationAffiliation
         * 
         * <p>Defines an affiliation/assotiation/relationship between 2 distinct organizations, that is not a part-of 
         * relationship/sub-division relationship.
         */
        ORGANIZATION_AFFILIATION("OrganizationAffiliation"),

        /**
         * PackagedProductDefinition
         * 
         * <p>A medically related item or items, in a container or package.
         */
        PACKAGED_PRODUCT_DEFINITION("PackagedProductDefinition"),

        /**
         * Patient
         * 
         * <p>Demographics and other administrative information about an individual or animal receiving care or other health-
         * related services.
         */
        PATIENT("Patient"),

        /**
         * PaymentNotice
         * 
         * <p>This resource provides the status of the payment for goods and services rendered, and the request and response 
         * resource references.
         */
        PAYMENT_NOTICE("PaymentNotice"),

        /**
         * PaymentReconciliation
         * 
         * <p>This resource provides the details including amount of a payment and allocates the payment items being paid.
         */
        PAYMENT_RECONCILIATION("PaymentReconciliation"),

        /**
         * Permission
         * 
         * <p>Permission resource holds access rules for a given data and context.
         */
        PERMISSION("Permission"),

        /**
         * Person
         * 
         * <p>Demographics and administrative information about a person independent of a specific health-related context.
         */
        PERSON("Person"),

        /**
         * PlanDefinition
         * 
         * <p>This resource allows for the definition of various types of plans as a sharable, consumable, and executable 
         * artifact. The resource is general enough to support the description of a broad range of clinical and non-clinical 
         * artifacts such as clinical decision support rules, order sets, protocols, and drug quality specifications.
         */
        PLAN_DEFINITION("PlanDefinition"),

        /**
         * Practitioner
         * 
         * <p>A person who is directly or indirectly involved in the provisioning of healthcare or related services.
         */
        PRACTITIONER("Practitioner"),

        /**
         * PractitionerRole
         * 
         * <p>A specific set of Roles/Locations/specialties/services that a practitioner may perform, or has performed at an 
         * organization during a period of time.
         */
        PRACTITIONER_ROLE("PractitionerRole"),

        /**
         * Procedure
         * 
         * <p>An action that is or was performed on or for a patient, practitioner, device, organization, or location. For 
         * example, this can be a physical intervention on a patient like an operation, or less invasive like long term services, 
         * counseling, or hypnotherapy. This can be a quality or safety inspection for a location, organization, or device. This 
         * can be an accreditation procedure on a practitioner for licensing.
         */
        PROCEDURE("Procedure"),

        /**
         * Provenance
         * 
         * <p>Provenance of a resource is a record that describes entities and processes involved in producing and delivering or 
         * otherwise influencing that resource. Provenance provides a critical foundation for assessing authenticity, enabling 
         * trust, and allowing reproducibility. Provenance assertions are a form of contextual metadata and can themselves become 
         * important records with their own provenance. Provenance statement indicates clinical significance in terms of 
         * confidence in authenticity, reliability, and trustworthiness, integrity, and stage in lifecycle (e.g. Document 
         * Completion - has the artifact been legally authenticated), all of which may impact security, privacy, and trust 
         * policies.
         */
        PROVENANCE("Provenance"),

        /**
         * Questionnaire
         * 
         * <p>A structured set of questions intended to guide the collection of answers from end-users. Questionnaires provide 
         * detailed control over order, presentation, phraseology and grouping to allow coherent, consistent data collection.
         */
        QUESTIONNAIRE("Questionnaire"),

        /**
         * QuestionnaireResponse
         * 
         * <p>A structured set of questions and their answers. The questions are ordered and grouped into coherent subsets, 
         * corresponding to the structure of the grouping of the questionnaire being responded to.
         */
        QUESTIONNAIRE_RESPONSE("QuestionnaireResponse"),

        /**
         * RegulatedAuthorization
         * 
         * <p>Regulatory approval, clearance or licencing related to a regulated product, treatment, facility or activity that is 
         * cited in a guidance, regulation, rule or legislative act. An example is Market Authorization relating to a Medicinal 
         * Product.
         */
        REGULATED_AUTHORIZATION("RegulatedAuthorization"),

        /**
         * RelatedPerson
         * 
         * <p>Information about a person that is involved in a patient's health or the care for a patient, but who is not the 
         * target of healthcare, nor has a formal responsibility in the care process.
         */
        RELATED_PERSON("RelatedPerson"),

        /**
         * RequestOrchestration
         * 
         * <p>A set of related requests that can be used to capture intended activities that have inter-dependencies such as 
         * "give this medication after that one".
         */
        REQUEST_ORCHESTRATION("RequestOrchestration"),

        /**
         * Requirements
         * 
         * <p>The Requirements resource is used to describe an actor - a human or an application that plays a role in data 
         * exchange, and that may have obligations associated with the role the actor plays.
         */
        REQUIREMENTS("Requirements"),

        /**
         * ResearchStudy
         * 
         * <p>A scientific study of nature that sometimes includes processes involved in health and disease. For example, 
         * clinical trials are research studies that involve people. These studies may be related to new ways to screen, prevent, 
         * diagnose, and treat disease. They may also study certain outcomes and certain groups of people by looking at data 
         * collected in the past or future.
         */
        RESEARCH_STUDY("ResearchStudy"),

        /**
         * ResearchSubject
         * 
         * <p>A ResearchSubject is a participant or object which is the recipient of investigative activities in a research study.
         */
        RESEARCH_SUBJECT("ResearchSubject"),

        /**
         * RiskAssessment
         * 
         * <p>An assessment of the likely outcome(s) for a patient or other subject as well as the likelihood of each outcome.
         */
        RISK_ASSESSMENT("RiskAssessment"),

        /**
         * Schedule
         * 
         * <p>A container for slots of time that may be available for booking appointments.
         */
        SCHEDULE("Schedule"),

        /**
         * SearchParameter
         * 
         * <p>A search parameter that defines a named search item that can be used to search/filter on a resource.
         */
        SEARCH_PARAMETER("SearchParameter"),

        /**
         * ServiceRequest
         * 
         * <p>A record of a request for service such as diagnostic investigations, treatments, or operations to be performed.
         */
        SERVICE_REQUEST("ServiceRequest"),

        /**
         * Slot
         * 
         * <p>A slot of time on a schedule that may be available for booking appointments.
         */
        SLOT("Slot"),

        /**
         * Specimen
         * 
         * <p>A sample to be used for analysis.
         */
        SPECIMEN("Specimen"),

        /**
         * SpecimenDefinition
         * 
         * <p>A kind of specimen with associated set of requirements.
         */
        SPECIMEN_DEFINITION("SpecimenDefinition"),

        /**
         * StructureDefinition
         * 
         * <p>A definition of a FHIR structure. This resource is used to describe the underlying resources, data types defined in 
         * FHIR, and also for describing extensions and constraints on resources and data types.
         */
        STRUCTURE_DEFINITION("StructureDefinition"),

        /**
         * StructureMap
         * 
         * <p>A Map of relationships between 2 structures that can be used to transform data.
         */
        STRUCTURE_MAP("StructureMap"),

        /**
         * Subscription
         * 
         * <p>The subscription resource describes a particular client's request to be notified about a SubscriptionTopic.
         */
        SUBSCRIPTION("Subscription"),

        /**
         * SubscriptionStatus
         * 
         * <p>The SubscriptionStatus resource describes the state of a Subscription during notifications. It is not persisted.
         */
        SUBSCRIPTION_STATUS("SubscriptionStatus"),

        /**
         * SubscriptionTopic
         * 
         * <p>Describes a stream of resource state changes identified by trigger criteria and annotated with labels useful to 
         * filter projections from this topic.
         */
        SUBSCRIPTION_TOPIC("SubscriptionTopic"),

        /**
         * Substance
         * 
         * <p>A homogeneous material with a definite composition.
         */
        SUBSTANCE("Substance"),

        /**
         * SubstanceDefinition
         * 
         * <p>The detailed description of a substance, typically at a level beyond what is used for prescribing.
         */
        SUBSTANCE_DEFINITION("SubstanceDefinition"),

        /**
         * SubstanceNucleicAcid
         * 
         * <p>Nucleic acids are defined by three distinct elements: the base, sugar and linkage. Individual substance/moiety IDs 
         * will be created for each of these elements. The nucleotide sequence will be always entered in the 5’-3’ direction.
         */
        SUBSTANCE_NUCLEIC_ACID("SubstanceNucleicAcid"),

        /**
         * SubstancePolymer
         * 
         * <p>Properties of a substance specific to it being a polymer.
         */
        SUBSTANCE_POLYMER("SubstancePolymer"),

        /**
         * SubstanceProtein
         * 
         * <p>A SubstanceProtein is defined as a single unit of a linear amino acid sequence, or a combination of subunits that 
         * are either covalently linked or have a defined invariant stoichiometric relationship. This includes all synthetic, 
         * recombinant and purified SubstanceProteins of defined sequence, whether the use is therapeutic or prophylactic. This 
         * set of elements will be used to describe albumins, coagulation factors, cytokines, growth factors, 
         * peptide/SubstanceProtein hormones, enzymes, toxins, toxoids, recombinant vaccines, and immunomodulators.
         */
        SUBSTANCE_PROTEIN("SubstanceProtein"),

        /**
         * SubstanceReferenceInformation
         * 
         * <p>Todo.
         */
        SUBSTANCE_REFERENCE_INFORMATION("SubstanceReferenceInformation"),

        /**
         * SubstanceSourceMaterial
         * 
         * <p>Source material shall capture information on the taxonomic and anatomical origins as well as the fraction of a 
         * material that can result in or can be modified to form a substance. This set of data elements shall be used to define 
         * polymer substances isolated from biological matrices. Taxonomic and anatomical origins shall be described using a 
         * controlled vocabulary as required. This information is captured for naturally derived polymers ( . starch) and 
         * structurally diverse substances. For Organisms belonging to the Kingdom Plantae the Substance level defines the fresh 
         * material of a single species or infraspecies, the Herbal Drug and the Herbal preparation. For Herbal preparations, the 
         * fraction information will be captured at the Substance information level and additional information for herbal 
         * extracts will be captured at the Specified Substance Group 1 information level. See for further explanation the 
         * Substance Class: Structurally Diverse and the herbal annex.
         */
        SUBSTANCE_SOURCE_MATERIAL("SubstanceSourceMaterial"),

        /**
         * SupplyDelivery
         * 
         * <p>Record of delivery of what is supplied.
         */
        SUPPLY_DELIVERY("SupplyDelivery"),

        /**
         * SupplyRequest
         * 
         * <p>A record of a non-patient specific request for a medication, substance, device, certain types of biologically 
         * derived product, and nutrition product used in the healthcare setting.
         */
        SUPPLY_REQUEST("SupplyRequest"),

        /**
         * Task
         * 
         * <p>A task to be performed.
         */
        TASK("Task"),

        /**
         * TerminologyCapabilities
         * 
         * <p>A TerminologyCapabilities resource documents a set of capabilities (behaviors) of a FHIR Terminology Server that 
         * may be used as a statement of actual server functionality or a statement of required or desired server implementation.
         */
        TERMINOLOGY_CAPABILITIES("TerminologyCapabilities"),

        /**
         * TestPlan
         * 
         * <p>A plan for executing testing on an artifact or specifications
         */
        TEST_PLAN("TestPlan"),

        /**
         * TestReport
         * 
         * <p>A summary of information based on the results of executing a TestScript.
         */
        TEST_REPORT("TestReport"),

        /**
         * TestScript
         * 
         * <p>A structured set of tests against a FHIR server or client implementation to determine compliance against the FHIR 
         * specification.
         */
        TEST_SCRIPT("TestScript"),

        /**
         * Transport
         * 
         * <p>Record of transport.
         */
        TRANSPORT("Transport"),

        /**
         * ValueSet
         * 
         * <p>A ValueSet resource instance specifies a set of codes drawn from one or more code systems, intended for use in a 
         * particular context. Value sets link between [[[CodeSystem]]] definitions and their use in [coded elements]
         * (terminologies.html).
         */
        VALUE_SET("ValueSet"),

        /**
         * VerificationResult
         * 
         * <p>Describes validation requirements, source(s), status and dates for one or more elements.
         */
        VERIFICATION_RESULT("VerificationResult"),

        /**
         * VisionPrescription
         * 
         * <p>An authorization for the provision of glasses and/or contact lenses to a patient.
         */
        VISION_PRESCRIPTION("VisionPrescription"),

        /**
         * Parameters
         * 
         * <p>This resource is used to pass information into and back from an operation (whether invoked directly from REST or 
         * within a messaging environment). It is not persisted or allowed to be referenced by other resources except as 
         * described in the definition of the Parameters resource.
         */
        PARAMETERS("Parameters");

        private final java.lang.String value;

        Value(java.lang.String value) {
            this.value = value;
        }

        /**
         * @return
         *     The java.lang.String value of the code represented by this enum
         */
        public java.lang.String value() {
            return value;
        }

        /**
         * Factory method for creating FHIRTypes.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding FHIRTypes.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "Base":
                return BASE;
            case "Element":
                return ELEMENT;
            case "BackboneElement":
                return BACKBONE_ELEMENT;
            case "DataType":
                return DATA_TYPE;
            case "Address":
                return ADDRESS;
            case "Annotation":
                return ANNOTATION;
            case "Attachment":
                return ATTACHMENT;
            case "Availability":
                return AVAILABILITY;
            case "BackboneType":
                return BACKBONE_TYPE;
            case "Dosage":
                return DOSAGE;
            case "ElementDefinition":
                return ELEMENT_DEFINITION;
            case "MarketingStatus":
                return MARKETING_STATUS;
            case "ProductShelfLife":
                return PRODUCT_SHELF_LIFE;
            case "Timing":
                return TIMING;
            case "CodeableConcept":
                return CODEABLE_CONCEPT;
            case "CodeableReference":
                return CODEABLE_REFERENCE;
            case "Coding":
                return CODING;
            case "ContactDetail":
                return CONTACT_DETAIL;
            case "ContactPoint":
                return CONTACT_POINT;
            case "Contributor":
                return CONTRIBUTOR;
            case "DataRequirement":
                return DATA_REQUIREMENT;
            case "Expression":
                return EXPRESSION;
            case "ExtendedContactDetail":
                return EXTENDED_CONTACT_DETAIL;
            case "Extension":
                return EXTENSION;
            case "HumanName":
                return HUMAN_NAME;
            case "Identifier":
                return IDENTIFIER;
            case "Meta":
                return META;
            case "MonetaryComponent":
                return MONETARY_COMPONENT;
            case "Money":
                return MONEY;
            case "Narrative":
                return NARRATIVE;
            case "ParameterDefinition":
                return PARAMETER_DEFINITION;
            case "Period":
                return PERIOD;
            case "PrimitiveType":
                return PRIMITIVE_TYPE;
            case "base64Binary":
                return BASE64BINARY;
            case "boolean":
                return BOOLEAN;
            case "date":
                return DATE;
            case "dateTime":
                return DATE_TIME;
            case "decimal":
                return DECIMAL;
            case "instant":
                return INSTANT;
            case "integer":
                return INTEGER;
            case "positiveInt":
                return POSITIVE_INT;
            case "unsignedInt":
                return UNSIGNED_INT;
            case "integer64":
                return INTEGER64;
            case "string":
                return STRING;
            case "code":
                return CODE;
            case "id":
                return ID;
            case "markdown":
                return MARKDOWN;
            case "time":
                return TIME;
            case "uri":
                return URI;
            case "canonical":
                return CANONICAL;
            case "oid":
                return OID;
            case "url":
                return URL;
            case "uuid":
                return UUID;
            case "Quantity":
                return QUANTITY;
            case "Age":
                return AGE;
            case "Count":
                return COUNT;
            case "Distance":
                return DISTANCE;
            case "Duration":
                return DURATION;
            case "Range":
                return RANGE;
            case "Ratio":
                return RATIO;
            case "RatioRange":
                return RATIO_RANGE;
            case "Reference":
                return REFERENCE;
            case "RelatedArtifact":
                return RELATED_ARTIFACT;
            case "SampledData":
                return SAMPLED_DATA;
            case "Signature":
                return SIGNATURE;
            case "TriggerDefinition":
                return TRIGGER_DEFINITION;
            case "UsageContext":
                return USAGE_CONTEXT;
            case "VirtualServiceDetail":
                return VIRTUAL_SERVICE_DETAIL;
            case "xhtml":
                return XHTML;
            case "Resource":
                return RESOURCE;
            case "Binary":
                return BINARY;
            case "Bundle":
                return BUNDLE;
            case "DomainResource":
                return DOMAIN_RESOURCE;
            case "Account":
                return ACCOUNT;
            case "ActivityDefinition":
                return ACTIVITY_DEFINITION;
            case "ActorDefinition":
                return ACTOR_DEFINITION;
            case "AdministrableProductDefinition":
                return ADMINISTRABLE_PRODUCT_DEFINITION;
            case "AdverseEvent":
                return ADVERSE_EVENT;
            case "AllergyIntolerance":
                return ALLERGY_INTOLERANCE;
            case "Appointment":
                return APPOINTMENT;
            case "AppointmentResponse":
                return APPOINTMENT_RESPONSE;
            case "ArtifactAssessment":
                return ARTIFACT_ASSESSMENT;
            case "AuditEvent":
                return AUDIT_EVENT;
            case "Basic":
                return BASIC;
            case "BiologicallyDerivedProduct":
                return BIOLOGICALLY_DERIVED_PRODUCT;
            case "BiologicallyDerivedProductDispense":
                return BIOLOGICALLY_DERIVED_PRODUCT_DISPENSE;
            case "BodyStructure":
                return BODY_STRUCTURE;
            case "CanonicalResource":
                return CANONICAL_RESOURCE;
            case "CapabilityStatement":
                return CAPABILITY_STATEMENT;
            case "CarePlan":
                return CARE_PLAN;
            case "CareTeam":
                return CARE_TEAM;
            case "ChargeItem":
                return CHARGE_ITEM;
            case "ChargeItemDefinition":
                return CHARGE_ITEM_DEFINITION;
            case "Citation":
                return CITATION;
            case "Claim":
                return CLAIM;
            case "ClaimResponse":
                return CLAIM_RESPONSE;
            case "ClinicalImpression":
                return CLINICAL_IMPRESSION;
            case "ClinicalUseDefinition":
                return CLINICAL_USE_DEFINITION;
            case "CodeSystem":
                return CODE_SYSTEM;
            case "Communication":
                return COMMUNICATION;
            case "CommunicationRequest":
                return COMMUNICATION_REQUEST;
            case "CompartmentDefinition":
                return COMPARTMENT_DEFINITION;
            case "Composition":
                return COMPOSITION;
            case "ConceptMap":
                return CONCEPT_MAP;
            case "Condition":
                return CONDITION;
            case "ConditionDefinition":
                return CONDITION_DEFINITION;
            case "Consent":
                return CONSENT;
            case "Contract":
                return CONTRACT;
            case "Coverage":
                return COVERAGE;
            case "CoverageEligibilityRequest":
                return COVERAGE_ELIGIBILITY_REQUEST;
            case "CoverageEligibilityResponse":
                return COVERAGE_ELIGIBILITY_RESPONSE;
            case "DetectedIssue":
                return DETECTED_ISSUE;
            case "Device":
                return DEVICE;
            case "DeviceAssociation":
                return DEVICE_ASSOCIATION;
            case "DeviceDefinition":
                return DEVICE_DEFINITION;
            case "DeviceDispense":
                return DEVICE_DISPENSE;
            case "DeviceMetric":
                return DEVICE_METRIC;
            case "DeviceRequest":
                return DEVICE_REQUEST;
            case "DeviceUsage":
                return DEVICE_USAGE;
            case "DiagnosticReport":
                return DIAGNOSTIC_REPORT;
            case "DocumentReference":
                return DOCUMENT_REFERENCE;
            case "Encounter":
                return ENCOUNTER;
            case "EncounterHistory":
                return ENCOUNTER_HISTORY;
            case "Endpoint":
                return ENDPOINT;
            case "EnrollmentRequest":
                return ENROLLMENT_REQUEST;
            case "EnrollmentResponse":
                return ENROLLMENT_RESPONSE;
            case "EpisodeOfCare":
                return EPISODE_OF_CARE;
            case "EventDefinition":
                return EVENT_DEFINITION;
            case "Evidence":
                return EVIDENCE;
            case "EvidenceReport":
                return EVIDENCE_REPORT;
            case "EvidenceVariable":
                return EVIDENCE_VARIABLE;
            case "ExampleScenario":
                return EXAMPLE_SCENARIO;
            case "ExplanationOfBenefit":
                return EXPLANATION_OF_BENEFIT;
            case "FamilyMemberHistory":
                return FAMILY_MEMBER_HISTORY;
            case "Flag":
                return FLAG;
            case "FormularyItem":
                return FORMULARY_ITEM;
            case "GenomicStudy":
                return GENOMIC_STUDY;
            case "Goal":
                return GOAL;
            case "GraphDefinition":
                return GRAPH_DEFINITION;
            case "Group":
                return GROUP;
            case "GuidanceResponse":
                return GUIDANCE_RESPONSE;
            case "HealthcareService":
                return HEALTHCARE_SERVICE;
            case "ImagingSelection":
                return IMAGING_SELECTION;
            case "ImagingStudy":
                return IMAGING_STUDY;
            case "Immunization":
                return IMMUNIZATION;
            case "ImmunizationEvaluation":
                return IMMUNIZATION_EVALUATION;
            case "ImmunizationRecommendation":
                return IMMUNIZATION_RECOMMENDATION;
            case "ImplementationGuide":
                return IMPLEMENTATION_GUIDE;
            case "Ingredient":
                return INGREDIENT;
            case "InsurancePlan":
                return INSURANCE_PLAN;
            case "InventoryItem":
                return INVENTORY_ITEM;
            case "InventoryReport":
                return INVENTORY_REPORT;
            case "Invoice":
                return INVOICE;
            case "Library":
                return LIBRARY;
            case "Linkage":
                return LINKAGE;
            case "List":
                return LIST;
            case "Location":
                return LOCATION;
            case "ManufacturedItemDefinition":
                return MANUFACTURED_ITEM_DEFINITION;
            case "Measure":
                return MEASURE;
            case "MeasureReport":
                return MEASURE_REPORT;
            case "Medication":
                return MEDICATION;
            case "MedicationAdministration":
                return MEDICATION_ADMINISTRATION;
            case "MedicationDispense":
                return MEDICATION_DISPENSE;
            case "MedicationKnowledge":
                return MEDICATION_KNOWLEDGE;
            case "MedicationRequest":
                return MEDICATION_REQUEST;
            case "MedicationStatement":
                return MEDICATION_STATEMENT;
            case "MedicinalProductDefinition":
                return MEDICINAL_PRODUCT_DEFINITION;
            case "MessageDefinition":
                return MESSAGE_DEFINITION;
            case "MessageHeader":
                return MESSAGE_HEADER;
            case "MetadataResource":
                return METADATA_RESOURCE;
            case "MolecularSequence":
                return MOLECULAR_SEQUENCE;
            case "NamingSystem":
                return NAMING_SYSTEM;
            case "NutritionIntake":
                return NUTRITION_INTAKE;
            case "NutritionOrder":
                return NUTRITION_ORDER;
            case "NutritionProduct":
                return NUTRITION_PRODUCT;
            case "Observation":
                return OBSERVATION;
            case "ObservationDefinition":
                return OBSERVATION_DEFINITION;
            case "OperationDefinition":
                return OPERATION_DEFINITION;
            case "OperationOutcome":
                return OPERATION_OUTCOME;
            case "Organization":
                return ORGANIZATION;
            case "OrganizationAffiliation":
                return ORGANIZATION_AFFILIATION;
            case "PackagedProductDefinition":
                return PACKAGED_PRODUCT_DEFINITION;
            case "Patient":
                return PATIENT;
            case "PaymentNotice":
                return PAYMENT_NOTICE;
            case "PaymentReconciliation":
                return PAYMENT_RECONCILIATION;
            case "Permission":
                return PERMISSION;
            case "Person":
                return PERSON;
            case "PlanDefinition":
                return PLAN_DEFINITION;
            case "Practitioner":
                return PRACTITIONER;
            case "PractitionerRole":
                return PRACTITIONER_ROLE;
            case "Procedure":
                return PROCEDURE;
            case "Provenance":
                return PROVENANCE;
            case "Questionnaire":
                return QUESTIONNAIRE;
            case "QuestionnaireResponse":
                return QUESTIONNAIRE_RESPONSE;
            case "RegulatedAuthorization":
                return REGULATED_AUTHORIZATION;
            case "RelatedPerson":
                return RELATED_PERSON;
            case "RequestOrchestration":
                return REQUEST_ORCHESTRATION;
            case "Requirements":
                return REQUIREMENTS;
            case "ResearchStudy":
                return RESEARCH_STUDY;
            case "ResearchSubject":
                return RESEARCH_SUBJECT;
            case "RiskAssessment":
                return RISK_ASSESSMENT;
            case "Schedule":
                return SCHEDULE;
            case "SearchParameter":
                return SEARCH_PARAMETER;
            case "ServiceRequest":
                return SERVICE_REQUEST;
            case "Slot":
                return SLOT;
            case "Specimen":
                return SPECIMEN;
            case "SpecimenDefinition":
                return SPECIMEN_DEFINITION;
            case "StructureDefinition":
                return STRUCTURE_DEFINITION;
            case "StructureMap":
                return STRUCTURE_MAP;
            case "Subscription":
                return SUBSCRIPTION;
            case "SubscriptionStatus":
                return SUBSCRIPTION_STATUS;
            case "SubscriptionTopic":
                return SUBSCRIPTION_TOPIC;
            case "Substance":
                return SUBSTANCE;
            case "SubstanceDefinition":
                return SUBSTANCE_DEFINITION;
            case "SubstanceNucleicAcid":
                return SUBSTANCE_NUCLEIC_ACID;
            case "SubstancePolymer":
                return SUBSTANCE_POLYMER;
            case "SubstanceProtein":
                return SUBSTANCE_PROTEIN;
            case "SubstanceReferenceInformation":
                return SUBSTANCE_REFERENCE_INFORMATION;
            case "SubstanceSourceMaterial":
                return SUBSTANCE_SOURCE_MATERIAL;
            case "SupplyDelivery":
                return SUPPLY_DELIVERY;
            case "SupplyRequest":
                return SUPPLY_REQUEST;
            case "Task":
                return TASK;
            case "TerminologyCapabilities":
                return TERMINOLOGY_CAPABILITIES;
            case "TestPlan":
                return TEST_PLAN;
            case "TestReport":
                return TEST_REPORT;
            case "TestScript":
                return TEST_SCRIPT;
            case "Transport":
                return TRANSPORT;
            case "ValueSet":
                return VALUE_SET;
            case "VerificationResult":
                return VERIFICATION_RESULT;
            case "VisionPrescription":
                return VISION_PRESCRIPTION;
            case "Parameters":
                return PARAMETERS;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
