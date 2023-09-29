/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.annotation.Choice;
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DocumentReferenceStatus;
import org.linuxforhealth.fhir.model.r5.type.code.ReferredDocumentStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A reference to a document of any kind for any purpose. While the term “document�? implies a more narrow focus, for 
 * this resource this "document" encompasses *any* serialized object with a mime-type, it includes formal patient-centric 
 * documents (CDA), clinical notes, scanned paper, non-patient specific documents like policy text, as well as a photo, 
 * video, or audio recording acquired or used in healthcare. The DocumentReference resource provides metadata about the 
 * document so that the document can be discovered and managed. The actual content may be inline base64 encoded data or 
 * provided by direct reference.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "docRef-1",
    level = "Warning",
    location = "(base)",
    description = "facilityType SHALL only be present if context is not an encounter",
    expression = "facilityType.empty() or context.where(resolve() is Encounter).empty()",
    source = "http://hl7.org/fhir/StructureDefinition/DocumentReference"
)
@Constraint(
    id = "docRef-2",
    level = "Warning",
    location = "(base)",
    description = "practiceSetting SHALL only be present if context is not present",
    expression = "practiceSetting.empty() or context.where(resolve() is Encounter).empty()",
    source = "http://hl7.org/fhir/StructureDefinition/DocumentReference"
)
@Constraint(
    id = "documentReference-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html",
    expression = "modality.exists() implies (modality.all(memberOf('http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/DocumentReference",
    generated = true
)
@Constraint(
    id = "documentReference-4",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/doc-typecodes",
    expression = "type.exists() implies (type.memberOf('http://hl7.org/fhir/ValueSet/doc-typecodes', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/DocumentReference",
    generated = true
)
@Constraint(
    id = "documentReference-5",
    level = "Warning",
    location = "attester.mode",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/composition-attestation-mode",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/composition-attestation-mode', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/DocumentReference",
    generated = true
)
@Constraint(
    id = "documentReference-6",
    level = "Warning",
    location = "relatesTo.code",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/document-relationship-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/document-relationship-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/DocumentReference",
    generated = true
)
@Constraint(
    id = "documentReference-7",
    level = "Warning",
    location = "content.profile.value",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/v3-HL7FormatCodes",
    expression = "$this.as(Coding).memberOf('http://terminology.hl7.org/ValueSet/v3-HL7FormatCodes', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/DocumentReference",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DocumentReference extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final String version;
    @ReferenceTarget({ "Appointment", "AppointmentResponse", "CarePlan", "Claim", "CommunicationRequest", "Contract", "CoverageEligibilityRequest", "DeviceRequest", "EnrollmentRequest", "ImmunizationRecommendation", "MedicationRequest", "NutritionOrder", "RequestOrchestration", "ServiceRequest", "SupplyRequest", "VisionPrescription" })
    private final List<Reference> basedOn;
    @Summary
    @Binding(
        bindingName = "DocumentReferenceStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the document reference.",
        valueSet = "http://hl7.org/fhir/ValueSet/document-reference-status|5.0.0"
    )
    @Required
    private final DocumentReferenceStatus status;
    @Summary
    @Binding(
        bindingName = "ReferredDocumentStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Status of the underlying document.",
        valueSet = "http://hl7.org/fhir/ValueSet/composition-status|5.0.0"
    )
    private final ReferredDocumentStatus docStatus;
    @Summary
    @Binding(
        bindingName = "ImagingModality",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Type of acquired data in the instance.",
        valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html"
    )
    private final List<CodeableConcept> modality;
    @Summary
    @Binding(
        bindingName = "DocumentType",
        strength = BindingStrength.Value.PREFERRED,
        description = "Precise type of clinical document.",
        valueSet = "http://hl7.org/fhir/ValueSet/doc-typecodes"
    )
    private final CodeableConcept type;
    @Summary
    @Binding(
        bindingName = "ReferencedItemCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "High-level kind of document at a macro level.",
        valueSet = "http://hl7.org/fhir/ValueSet/referenced-item-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    private final Reference subject;
    @ReferenceTarget({ "Appointment", "Encounter", "EpisodeOfCare" })
    private final List<Reference> context;
    @Binding(
        bindingName = "DocumentEventType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "This list of codes represents the main clinical acts being documented.",
        valueSet = "http://terminology.hl7.org/ValueSet/v3-ActCode"
    )
    private final List<CodeableReference> event;
    @Summary
    @Binding(
        bindingName = "BodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT Body site concepts",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final List<CodeableReference> bodySite;
    @Binding(
        bindingName = "DocumentC80FacilityType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "XDS Facility Type.",
        valueSet = "http://hl7.org/fhir/ValueSet/c80-facilitycodes"
    )
    private final CodeableConcept facilityType;
    @Binding(
        bindingName = "DocumentC80PracticeSetting",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Additional details about where the content was created (e.g. clinical specialty).",
        valueSet = "http://hl7.org/fhir/ValueSet/c80-practice-codes"
    )
    private final CodeableConcept practiceSetting;
    @Summary
    private final Period period;
    @Summary
    private final Instant date;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Device", "Patient", "RelatedPerson", "CareTeam" })
    private final List<Reference> author;
    private final List<Attester> attester;
    @ReferenceTarget({ "Organization" })
    private final Reference custodian;
    @Summary
    private final List<RelatesTo> relatesTo;
    @Summary
    private final Markdown description;
    @Summary
    @Binding(
        bindingName = "SecurityLabels",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Example Security Labels from the Healthcare Privacy and Security Classification System.",
        valueSet = "http://hl7.org/fhir/ValueSet/security-label-examples"
    )
    private final List<CodeableConcept> securityLabel;
    @Summary
    @Required
    private final List<Content> content;

    private DocumentReference(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        basedOn = Collections.unmodifiableList(builder.basedOn);
        status = builder.status;
        docStatus = builder.docStatus;
        modality = Collections.unmodifiableList(builder.modality);
        type = builder.type;
        category = Collections.unmodifiableList(builder.category);
        subject = builder.subject;
        context = Collections.unmodifiableList(builder.context);
        event = Collections.unmodifiableList(builder.event);
        bodySite = Collections.unmodifiableList(builder.bodySite);
        facilityType = builder.facilityType;
        practiceSetting = builder.practiceSetting;
        period = builder.period;
        date = builder.date;
        author = Collections.unmodifiableList(builder.author);
        attester = Collections.unmodifiableList(builder.attester);
        custodian = builder.custodian;
        relatesTo = Collections.unmodifiableList(builder.relatesTo);
        description = builder.description;
        securityLabel = Collections.unmodifiableList(builder.securityLabel);
        content = Collections.unmodifiableList(builder.content);
    }

    /**
     * Other business identifiers associated with the document, including version independent identifiers.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * An explicitly assigned identifer of a variation of the content in the DocumentReference.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getVersion() {
        return version;
    }

    /**
     * A procedure that is fulfilled in whole or in part by the creation of this media.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * The status of this document reference.
     * 
     * @return
     *     An immutable object of type {@link DocumentReferenceStatus} that is non-null.
     */
    public DocumentReferenceStatus getStatus() {
        return status;
    }

    /**
     * The status of the underlying document.
     * 
     * @return
     *     An immutable object of type {@link ReferredDocumentStatus} that may be null.
     */
    public ReferredDocumentStatus getDocStatus() {
        return docStatus;
    }

    /**
     * Imaging modality used. This may include both acquisition and non-acquisition modalities.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getModality() {
        return modality;
    }

    /**
     * Specifies the particular kind of document referenced (e.g. History and Physical, Discharge Summary, Progress Note). 
     * This usually equates to the purpose of making the document referenced.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * A categorization for the type of document referenced - helps for indexing and searching. This may be implied by or 
     * derived from the code specified in the DocumentReference.type.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Who or what the document is about. The document can be about a person, (patient or healthcare practitioner), a device 
     * (e.g. a machine) or even a group of subjects (such as a document about a herd of farm animals, or a set of patients 
     * that share a common exposure).
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * Describes the clinical encounter or type of care that the document content is associated with.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getContext() {
        return context;
    }

    /**
     * This list of codes represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In 
     * some cases, the event is inherent in the type Code, such as a "History and Physical Report" in which the procedure 
     * being documented is necessarily a "History and Physical" act.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getEvent() {
        return event;
    }

    /**
     * The anatomic structures included in the document.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getBodySite() {
        return bodySite;
    }

    /**
     * The kind of facility where the patient was seen.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getFacilityType() {
        return facilityType;
    }

    /**
     * This property may convey specifics about the practice setting where the content was created, often reflecting the 
     * clinical specialty.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPracticeSetting() {
        return practiceSetting;
    }

    /**
     * The time period over which the service that is described by the document was provided.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * When the document reference was created.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getDate() {
        return date;
    }

    /**
     * Identifies who is responsible for adding the information to the document.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getAuthor() {
        return author;
    }

    /**
     * A participant who has authenticated the accuracy of the document.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Attester} that may be empty.
     */
    public List<Attester> getAttester() {
        return attester;
    }

    /**
     * Identifies the organization or group who is responsible for ongoing maintenance of and access to the document.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getCustodian() {
        return custodian;
    }

    /**
     * Relationships that this document has with other document references that already exist.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatesTo} that may be empty.
     */
    public List<RelatesTo> getRelatesTo() {
        return relatesTo;
    }

    /**
     * Human-readable description of the source document.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * A set of Security-Tag codes specifying the level of privacy/security of the Document found at DocumentReference.
     * content.attachment.url. Note that DocumentReference.meta.security contains the security labels of the data elements in 
     * DocumentReference, while DocumentReference.securityLabel contains the security labels for the document the reference 
     * refers to. The distinction recognizes that the document may contain sensitive information, while the DocumentReference 
     * is metadata about the document and thus might not be as sensitive as the document. For example: a psychotherapy 
     * episode may contain highly sensitive information, while the metadata may simply indicate that some episode happened.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSecurityLabel() {
        return securityLabel;
    }

    /**
     * The document and format referenced. If there are multiple content element repetitions, these must all represent the 
     * same document in different format, or attachment metadata.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Content} that is non-empty.
     */
    public List<Content> getContent() {
        return content;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (version != null) || 
            !basedOn.isEmpty() || 
            (status != null) || 
            (docStatus != null) || 
            !modality.isEmpty() || 
            (type != null) || 
            !category.isEmpty() || 
            (subject != null) || 
            !context.isEmpty() || 
            !event.isEmpty() || 
            !bodySite.isEmpty() || 
            (facilityType != null) || 
            (practiceSetting != null) || 
            (period != null) || 
            (date != null) || 
            !author.isEmpty() || 
            !attester.isEmpty() || 
            (custodian != null) || 
            !relatesTo.isEmpty() || 
            (description != null) || 
            !securityLabel.isEmpty() || 
            !content.isEmpty();
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(meta, "meta", visitor);
                accept(implicitRules, "implicitRules", visitor);
                accept(language, "language", visitor);
                accept(text, "text", visitor);
                accept(contained, "contained", visitor, Resource.class);
                accept(extension, "extension", visitor, Extension.class);
                accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(version, "version", visitor);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(docStatus, "docStatus", visitor);
                accept(modality, "modality", visitor, CodeableConcept.class);
                accept(type, "type", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(subject, "subject", visitor);
                accept(context, "context", visitor, Reference.class);
                accept(event, "event", visitor, CodeableReference.class);
                accept(bodySite, "bodySite", visitor, CodeableReference.class);
                accept(facilityType, "facilityType", visitor);
                accept(practiceSetting, "practiceSetting", visitor);
                accept(period, "period", visitor);
                accept(date, "date", visitor);
                accept(author, "author", visitor, Reference.class);
                accept(attester, "attester", visitor, Attester.class);
                accept(custodian, "custodian", visitor);
                accept(relatesTo, "relatesTo", visitor, RelatesTo.class);
                accept(description, "description", visitor);
                accept(securityLabel, "securityLabel", visitor, CodeableConcept.class);
                accept(content, "content", visitor, Content.class);
            }
            visitor.visitEnd(elementName, elementIndex, this);
            visitor.postVisit(this);
        }
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
        DocumentReference other = (DocumentReference) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(version, other.version) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(status, other.status) && 
            Objects.equals(docStatus, other.docStatus) && 
            Objects.equals(modality, other.modality) && 
            Objects.equals(type, other.type) && 
            Objects.equals(category, other.category) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(context, other.context) && 
            Objects.equals(event, other.event) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(facilityType, other.facilityType) && 
            Objects.equals(practiceSetting, other.practiceSetting) && 
            Objects.equals(period, other.period) && 
            Objects.equals(date, other.date) && 
            Objects.equals(author, other.author) && 
            Objects.equals(attester, other.attester) && 
            Objects.equals(custodian, other.custodian) && 
            Objects.equals(relatesTo, other.relatesTo) && 
            Objects.equals(description, other.description) && 
            Objects.equals(securityLabel, other.securityLabel) && 
            Objects.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                meta, 
                implicitRules, 
                language, 
                text, 
                contained, 
                extension, 
                modifierExtension, 
                identifier, 
                version, 
                basedOn, 
                status, 
                docStatus, 
                modality, 
                type, 
                category, 
                subject, 
                context, 
                event, 
                bodySite, 
                facilityType, 
                practiceSetting, 
                period, 
                date, 
                author, 
                attester, 
                custodian, 
                relatesTo, 
                description, 
                securityLabel, 
                content);
            hashCode = result;
        }
        return result;
    }

    @Override
    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends DomainResource.Builder {
        private List<Identifier> identifier = new ArrayList<>();
        private String version;
        private List<Reference> basedOn = new ArrayList<>();
        private DocumentReferenceStatus status;
        private ReferredDocumentStatus docStatus;
        private List<CodeableConcept> modality = new ArrayList<>();
        private CodeableConcept type;
        private List<CodeableConcept> category = new ArrayList<>();
        private Reference subject;
        private List<Reference> context = new ArrayList<>();
        private List<CodeableReference> event = new ArrayList<>();
        private List<CodeableReference> bodySite = new ArrayList<>();
        private CodeableConcept facilityType;
        private CodeableConcept practiceSetting;
        private Period period;
        private Instant date;
        private List<Reference> author = new ArrayList<>();
        private List<Attester> attester = new ArrayList<>();
        private Reference custodian;
        private List<RelatesTo> relatesTo = new ArrayList<>();
        private Markdown description;
        private List<CodeableConcept> securityLabel = new ArrayList<>();
        private List<Content> content = new ArrayList<>();

        private Builder() {
            super();
        }

        /**
         * The logical id of the resource, as used in the URL for the resource. Once assigned, this value never changes.
         * 
         * @param id
         *     Logical id of this artifact
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        /**
         * The metadata about the resource. This is content that is maintained by the infrastructure. Changes to the content 
         * might not always be associated with version changes to the resource.
         * 
         * @param meta
         *     Metadata about the resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder meta(Meta meta) {
            return (Builder) super.meta(meta);
        }

        /**
         * A reference to a set of rules that were followed when the resource was constructed, and which must be understood when 
         * processing the content. Often, this is a reference to an implementation guide that defines the special rules along 
         * with other profiles etc.
         * 
         * @param implicitRules
         *     A set of rules under which this content was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder implicitRules(Uri implicitRules) {
            return (Builder) super.implicitRules(implicitRules);
        }

        /**
         * The base language in which the resource is written.
         * 
         * @param language
         *     Language of the resource content
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder language(Code language) {
            return (Builder) super.language(language);
        }

        /**
         * A human-readable narrative that contains a summary of the resource and can be used to represent the content of the 
         * resource to a human. The narrative need not encode all the structured data, but is required to contain sufficient 
         * detail to make it "clinically safe" for a human to just read the narrative. Resource definitions may define what 
         * content should be represented in the narrative to ensure clinical safety.
         * 
         * @param text
         *     Text summary of the resource, for human interpretation
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder text(Narrative text) {
            return (Builder) super.text(text);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder contained(Resource... contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder contained(Collection<Resource> contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
         * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
         * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
         * of the definition of the extension.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param extension
         *     Additional content defined by implementations
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
         * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
         * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
         * of the definition of the extension.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param extension
         *     Additional content defined by implementations
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder modifierExtension(Extension... modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder modifierExtension(Collection<Extension> modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * Other business identifiers associated with the document, including version independent identifiers.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifiers for the document
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder identifier(Identifier... identifier) {
            for (Identifier value : identifier) {
                this.identifier.add(value);
            }
            return this;
        }

        /**
         * Other business identifiers associated with the document, including version independent identifiers.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifiers for the document
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder identifier(Collection<Identifier> identifier) {
            this.identifier = new ArrayList<>(identifier);
            return this;
        }

        /**
         * Convenience method for setting {@code version}.
         * 
         * @param version
         *     An explicitly assigned identifer of a variation of the content in the DocumentReference
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #version(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder version(java.lang.String version) {
            this.version = (version == null) ? null : String.of(version);
            return this;
        }

        /**
         * An explicitly assigned identifer of a variation of the content in the DocumentReference.
         * 
         * @param version
         *     An explicitly assigned identifer of a variation of the content in the DocumentReference
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * A procedure that is fulfilled in whole or in part by the creation of this media.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * <li>{@link AppointmentResponse}</li>
         * <li>{@link CarePlan}</li>
         * <li>{@link Claim}</li>
         * <li>{@link CommunicationRequest}</li>
         * <li>{@link Contract}</li>
         * <li>{@link CoverageEligibilityRequest}</li>
         * <li>{@link DeviceRequest}</li>
         * <li>{@link EnrollmentRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link RequestOrchestration}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link SupplyRequest}</li>
         * <li>{@link VisionPrescription}</li>
         * </ul>
         * 
         * @param basedOn
         *     Procedure that caused this media to be created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder basedOn(Reference... basedOn) {
            for (Reference value : basedOn) {
                this.basedOn.add(value);
            }
            return this;
        }

        /**
         * A procedure that is fulfilled in whole or in part by the creation of this media.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * <li>{@link AppointmentResponse}</li>
         * <li>{@link CarePlan}</li>
         * <li>{@link Claim}</li>
         * <li>{@link CommunicationRequest}</li>
         * <li>{@link Contract}</li>
         * <li>{@link CoverageEligibilityRequest}</li>
         * <li>{@link DeviceRequest}</li>
         * <li>{@link EnrollmentRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link RequestOrchestration}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link SupplyRequest}</li>
         * <li>{@link VisionPrescription}</li>
         * </ul>
         * 
         * @param basedOn
         *     Procedure that caused this media to be created
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder basedOn(Collection<Reference> basedOn) {
            this.basedOn = new ArrayList<>(basedOn);
            return this;
        }

        /**
         * The status of this document reference.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     current | superseded | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(DocumentReferenceStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The status of the underlying document.
         * 
         * @param docStatus
         *     registered | partial | preliminary | final | amended | corrected | appended | cancelled | entered-in-error | 
         *     deprecated | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder docStatus(ReferredDocumentStatus docStatus) {
            this.docStatus = docStatus;
            return this;
        }

        /**
         * Imaging modality used. This may include both acquisition and non-acquisition modalities.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modality
         *     Imaging modality used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder modality(CodeableConcept... modality) {
            for (CodeableConcept value : modality) {
                this.modality.add(value);
            }
            return this;
        }

        /**
         * Imaging modality used. This may include both acquisition and non-acquisition modalities.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modality
         *     Imaging modality used
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder modality(Collection<CodeableConcept> modality) {
            this.modality = new ArrayList<>(modality);
            return this;
        }

        /**
         * Specifies the particular kind of document referenced (e.g. History and Physical, Discharge Summary, Progress Note). 
         * This usually equates to the purpose of making the document referenced.
         * 
         * @param type
         *     Kind of document (LOINC if possible)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * A categorization for the type of document referenced - helps for indexing and searching. This may be implied by or 
         * derived from the code specified in the DocumentReference.type.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Categorization of document
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder category(CodeableConcept... category) {
            for (CodeableConcept value : category) {
                this.category.add(value);
            }
            return this;
        }

        /**
         * A categorization for the type of document referenced - helps for indexing and searching. This may be implied by or 
         * derived from the code specified in the DocumentReference.type.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Categorization of document
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder category(Collection<CodeableConcept> category) {
            this.category = new ArrayList<>(category);
            return this;
        }

        /**
         * Who or what the document is about. The document can be about a person, (patient or healthcare practitioner), a device 
         * (e.g. a machine) or even a group of subjects (such as a document about a herd of farm animals, or a set of patients 
         * that share a common exposure).
         * 
         * @param subject
         *     Who/what is the subject of the document
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Describes the clinical encounter or type of care that the document content is associated with.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * <li>{@link Encounter}</li>
         * <li>{@link EpisodeOfCare}</li>
         * </ul>
         * 
         * @param context
         *     Context of the document content
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder context(Reference... context) {
            for (Reference value : context) {
                this.context.add(value);
            }
            return this;
        }

        /**
         * Describes the clinical encounter or type of care that the document content is associated with.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Appointment}</li>
         * <li>{@link Encounter}</li>
         * <li>{@link EpisodeOfCare}</li>
         * </ul>
         * 
         * @param context
         *     Context of the document content
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder context(Collection<Reference> context) {
            this.context = new ArrayList<>(context);
            return this;
        }

        /**
         * This list of codes represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In 
         * some cases, the event is inherent in the type Code, such as a "History and Physical Report" in which the procedure 
         * being documented is necessarily a "History and Physical" act.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param event
         *     Main clinical acts documented
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder event(CodeableReference... event) {
            for (CodeableReference value : event) {
                this.event.add(value);
            }
            return this;
        }

        /**
         * This list of codes represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In 
         * some cases, the event is inherent in the type Code, such as a "History and Physical Report" in which the procedure 
         * being documented is necessarily a "History and Physical" act.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param event
         *     Main clinical acts documented
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder event(Collection<CodeableReference> event) {
            this.event = new ArrayList<>(event);
            return this;
        }

        /**
         * The anatomic structures included in the document.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Body part included
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodySite(CodeableReference... bodySite) {
            for (CodeableReference value : bodySite) {
                this.bodySite.add(value);
            }
            return this;
        }

        /**
         * The anatomic structures included in the document.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     Body part included
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder bodySite(Collection<CodeableReference> bodySite) {
            this.bodySite = new ArrayList<>(bodySite);
            return this;
        }

        /**
         * The kind of facility where the patient was seen.
         * 
         * @param facilityType
         *     Kind of facility where patient was seen
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder facilityType(CodeableConcept facilityType) {
            this.facilityType = facilityType;
            return this;
        }

        /**
         * This property may convey specifics about the practice setting where the content was created, often reflecting the 
         * clinical specialty.
         * 
         * @param practiceSetting
         *     Additional details about where the content was created (e.g. clinical specialty)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder practiceSetting(CodeableConcept practiceSetting) {
            this.practiceSetting = practiceSetting;
            return this;
        }

        /**
         * The time period over which the service that is described by the document was provided.
         * 
         * @param period
         *     Time of service that is being documented
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * Convenience method for setting {@code date}.
         * 
         * @param date
         *     When this document reference was created
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #date(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder date(java.time.ZonedDateTime date) {
            this.date = (date == null) ? null : Instant.of(date);
            return this;
        }

        /**
         * When the document reference was created.
         * 
         * @param date
         *     When this document reference was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(Instant date) {
            this.date = date;
            return this;
        }

        /**
         * Identifies who is responsible for adding the information to the document.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Device}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param author
         *     Who and/or what authored the document
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder author(Reference... author) {
            for (Reference value : author) {
                this.author.add(value);
            }
            return this;
        }

        /**
         * Identifies who is responsible for adding the information to the document.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Device}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param author
         *     Who and/or what authored the document
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder author(Collection<Reference> author) {
            this.author = new ArrayList<>(author);
            return this;
        }

        /**
         * A participant who has authenticated the accuracy of the document.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param attester
         *     Attests to accuracy of the document
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder attester(Attester... attester) {
            for (Attester value : attester) {
                this.attester.add(value);
            }
            return this;
        }

        /**
         * A participant who has authenticated the accuracy of the document.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param attester
         *     Attests to accuracy of the document
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder attester(Collection<Attester> attester) {
            this.attester = new ArrayList<>(attester);
            return this;
        }

        /**
         * Identifies the organization or group who is responsible for ongoing maintenance of and access to the document.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param custodian
         *     Organization which maintains the document
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder custodian(Reference custodian) {
            this.custodian = custodian;
            return this;
        }

        /**
         * Relationships that this document has with other document references that already exist.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatesTo
         *     Relationships to other documents
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relatesTo(RelatesTo... relatesTo) {
            for (RelatesTo value : relatesTo) {
                this.relatesTo.add(value);
            }
            return this;
        }

        /**
         * Relationships that this document has with other document references that already exist.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatesTo
         *     Relationships to other documents
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relatesTo(Collection<RelatesTo> relatesTo) {
            this.relatesTo = new ArrayList<>(relatesTo);
            return this;
        }

        /**
         * Human-readable description of the source document.
         * 
         * @param description
         *     Human-readable description
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * A set of Security-Tag codes specifying the level of privacy/security of the Document found at DocumentReference.
         * content.attachment.url. Note that DocumentReference.meta.security contains the security labels of the data elements in 
         * DocumentReference, while DocumentReference.securityLabel contains the security labels for the document the reference 
         * refers to. The distinction recognizes that the document may contain sensitive information, while the DocumentReference 
         * is metadata about the document and thus might not be as sensitive as the document. For example: a psychotherapy 
         * episode may contain highly sensitive information, while the metadata may simply indicate that some episode happened.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param securityLabel
         *     Document security-tags
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder securityLabel(CodeableConcept... securityLabel) {
            for (CodeableConcept value : securityLabel) {
                this.securityLabel.add(value);
            }
            return this;
        }

        /**
         * A set of Security-Tag codes specifying the level of privacy/security of the Document found at DocumentReference.
         * content.attachment.url. Note that DocumentReference.meta.security contains the security labels of the data elements in 
         * DocumentReference, while DocumentReference.securityLabel contains the security labels for the document the reference 
         * refers to. The distinction recognizes that the document may contain sensitive information, while the DocumentReference 
         * is metadata about the document and thus might not be as sensitive as the document. For example: a psychotherapy 
         * episode may contain highly sensitive information, while the metadata may simply indicate that some episode happened.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param securityLabel
         *     Document security-tags
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder securityLabel(Collection<CodeableConcept> securityLabel) {
            this.securityLabel = new ArrayList<>(securityLabel);
            return this;
        }

        /**
         * The document and format referenced. If there are multiple content element repetitions, these must all represent the 
         * same document in different format, or attachment metadata.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param content
         *     Document referenced
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder content(Content... content) {
            for (Content value : content) {
                this.content.add(value);
            }
            return this;
        }

        /**
         * The document and format referenced. If there are multiple content element repetitions, these must all represent the 
         * same document in different format, or attachment metadata.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param content
         *     Document referenced
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder content(Collection<Content> content) {
            this.content = new ArrayList<>(content);
            return this;
        }

        /**
         * Build the {@link DocumentReference}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>content</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link DocumentReference}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid DocumentReference per the base specification
         */
        @Override
        public DocumentReference build() {
            DocumentReference documentReference = new DocumentReference(this);
            if (validating) {
                validate(documentReference);
            }
            return documentReference;
        }

        protected void validate(DocumentReference documentReference) {
            super.validate(documentReference);
            ValidationSupport.checkList(documentReference.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(documentReference.basedOn, "basedOn", Reference.class);
            ValidationSupport.requireNonNull(documentReference.status, "status");
            ValidationSupport.checkList(documentReference.modality, "modality", CodeableConcept.class);
            ValidationSupport.checkList(documentReference.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(documentReference.context, "context", Reference.class);
            ValidationSupport.checkList(documentReference.event, "event", CodeableReference.class);
            ValidationSupport.checkList(documentReference.bodySite, "bodySite", CodeableReference.class);
            ValidationSupport.checkList(documentReference.author, "author", Reference.class);
            ValidationSupport.checkList(documentReference.attester, "attester", Attester.class);
            ValidationSupport.checkList(documentReference.relatesTo, "relatesTo", RelatesTo.class);
            ValidationSupport.checkList(documentReference.securityLabel, "securityLabel", CodeableConcept.class);
            ValidationSupport.checkNonEmptyList(documentReference.content, "content", Content.class);
            ValidationSupport.checkReferenceType(documentReference.basedOn, "basedOn", "Appointment", "AppointmentResponse", "CarePlan", "Claim", "CommunicationRequest", "Contract", "CoverageEligibilityRequest", "DeviceRequest", "EnrollmentRequest", "ImmunizationRecommendation", "MedicationRequest", "NutritionOrder", "RequestOrchestration", "ServiceRequest", "SupplyRequest", "VisionPrescription");
            ValidationSupport.checkReferenceType(documentReference.context, "context", "Appointment", "Encounter", "EpisodeOfCare");
            ValidationSupport.checkReferenceType(documentReference.author, "author", "Practitioner", "PractitionerRole", "Organization", "Device", "Patient", "RelatedPerson", "CareTeam");
            ValidationSupport.checkReferenceType(documentReference.custodian, "custodian", "Organization");
        }

        protected Builder from(DocumentReference documentReference) {
            super.from(documentReference);
            identifier.addAll(documentReference.identifier);
            version = documentReference.version;
            basedOn.addAll(documentReference.basedOn);
            status = documentReference.status;
            docStatus = documentReference.docStatus;
            modality.addAll(documentReference.modality);
            type = documentReference.type;
            category.addAll(documentReference.category);
            subject = documentReference.subject;
            context.addAll(documentReference.context);
            event.addAll(documentReference.event);
            bodySite.addAll(documentReference.bodySite);
            facilityType = documentReference.facilityType;
            practiceSetting = documentReference.practiceSetting;
            period = documentReference.period;
            date = documentReference.date;
            author.addAll(documentReference.author);
            attester.addAll(documentReference.attester);
            custodian = documentReference.custodian;
            relatesTo.addAll(documentReference.relatesTo);
            description = documentReference.description;
            securityLabel.addAll(documentReference.securityLabel);
            content.addAll(documentReference.content);
            return this;
        }
    }

    /**
     * A participant who has authenticated the accuracy of the document.
     */
    public static class Attester extends BackboneElement {
        @Binding(
            bindingName = "DocumentAttestationMode",
            strength = BindingStrength.Value.PREFERRED,
            description = "The way in which a person authenticated a document.",
            valueSet = "http://hl7.org/fhir/ValueSet/composition-attestation-mode"
        )
        @Required
        private final CodeableConcept mode;
        private final DateTime time;
        @ReferenceTarget({ "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization" })
        private final Reference party;

        private Attester(Builder builder) {
            super(builder);
            mode = builder.mode;
            time = builder.time;
            party = builder.party;
        }

        /**
         * The type of attestation the authenticator offers.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getMode() {
            return mode;
        }

        /**
         * When the document was attested by the party.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getTime() {
            return time;
        }

        /**
         * Who attested the document in the specified way.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getParty() {
            return party;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (mode != null) || 
                (time != null) || 
                (party != null);
        }

        @Override
        public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
            if (visitor.preVisit(this)) {
                visitor.visitStart(elementName, elementIndex, this);
                if (visitor.visit(elementName, elementIndex, this)) {
                    // visit children
                    accept(id, "id", visitor);
                    accept(extension, "extension", visitor, Extension.class);
                    accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                    accept(mode, "mode", visitor);
                    accept(time, "time", visitor);
                    accept(party, "party", visitor);
                }
                visitor.visitEnd(elementName, elementIndex, this);
                visitor.postVisit(this);
            }
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
            Attester other = (Attester) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(mode, other.mode) && 
                Objects.equals(time, other.time) && 
                Objects.equals(party, other.party);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    mode, 
                    time, 
                    party);
                hashCode = result;
            }
            return result;
        }

        @Override
        public Builder toBuilder() {
            return new Builder().from(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BackboneElement.Builder {
            private CodeableConcept mode;
            private DateTime time;
            private Reference party;

            private Builder() {
                super();
            }

            /**
             * Unique id for the element within a resource (for internal references). This may be any string value that does not 
             * contain spaces.
             * 
             * @param id
             *     Unique id for inter-element referencing
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder id(java.lang.String id) {
                return (Builder) super.id(id);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder extension(Extension... extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder extension(Collection<Extension> extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder modifierExtension(Extension... modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder modifierExtension(Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * The type of attestation the authenticator offers.
             * 
             * <p>This element is required.
             * 
             * @param mode
             *     personal | professional | legal | official
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder mode(CodeableConcept mode) {
                this.mode = mode;
                return this;
            }

            /**
             * When the document was attested by the party.
             * 
             * @param time
             *     When the document was attested
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder time(DateTime time) {
                this.time = time;
                return this;
            }

            /**
             * Who attested the document in the specified way.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param party
             *     Who attested the document
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder party(Reference party) {
                this.party = party;
                return this;
            }

            /**
             * Build the {@link Attester}
             * 
             * <p>Required elements:
             * <ul>
             * <li>mode</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Attester}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Attester per the base specification
             */
            @Override
            public Attester build() {
                Attester attester = new Attester(this);
                if (validating) {
                    validate(attester);
                }
                return attester;
            }

            protected void validate(Attester attester) {
                super.validate(attester);
                ValidationSupport.requireNonNull(attester.mode, "mode");
                ValidationSupport.checkReferenceType(attester.party, "party", "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization");
                ValidationSupport.requireValueOrChildren(attester);
            }

            protected Builder from(Attester attester) {
                super.from(attester);
                mode = attester.mode;
                time = attester.time;
                party = attester.party;
                return this;
            }
        }
    }

    /**
     * Relationships that this document has with other document references that already exist.
     */
    public static class RelatesTo extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "DocumentRelationshipType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The type of relationship between the documents.",
            valueSet = "http://hl7.org/fhir/ValueSet/document-relationship-type"
        )
        @Required
        private final CodeableConcept code;
        @Summary
        @ReferenceTarget({ "DocumentReference" })
        @Required
        private final Reference target;

        private RelatesTo(Builder builder) {
            super(builder);
            code = builder.code;
            target = builder.target;
        }

        /**
         * The type of relationship that this document has with anther document.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * The target document of this relationship.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getTarget() {
            return target;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (target != null);
        }

        @Override
        public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
            if (visitor.preVisit(this)) {
                visitor.visitStart(elementName, elementIndex, this);
                if (visitor.visit(elementName, elementIndex, this)) {
                    // visit children
                    accept(id, "id", visitor);
                    accept(extension, "extension", visitor, Extension.class);
                    accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                    accept(code, "code", visitor);
                    accept(target, "target", visitor);
                }
                visitor.visitEnd(elementName, elementIndex, this);
                visitor.postVisit(this);
            }
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
            RelatesTo other = (RelatesTo) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(target, other.target);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    target);
                hashCode = result;
            }
            return result;
        }

        @Override
        public Builder toBuilder() {
            return new Builder().from(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BackboneElement.Builder {
            private CodeableConcept code;
            private Reference target;

            private Builder() {
                super();
            }

            /**
             * Unique id for the element within a resource (for internal references). This may be any string value that does not 
             * contain spaces.
             * 
             * @param id
             *     Unique id for inter-element referencing
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder id(java.lang.String id) {
                return (Builder) super.id(id);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder extension(Extension... extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder extension(Collection<Extension> extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder modifierExtension(Extension... modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder modifierExtension(Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * The type of relationship that this document has with anther document.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     The relationship type with another document
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * The target document of this relationship.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link DocumentReference}</li>
             * </ul>
             * 
             * @param target
             *     Target of the relationship
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder target(Reference target) {
                this.target = target;
                return this;
            }

            /**
             * Build the {@link RelatesTo}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * <li>target</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link RelatesTo}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid RelatesTo per the base specification
             */
            @Override
            public RelatesTo build() {
                RelatesTo relatesTo = new RelatesTo(this);
                if (validating) {
                    validate(relatesTo);
                }
                return relatesTo;
            }

            protected void validate(RelatesTo relatesTo) {
                super.validate(relatesTo);
                ValidationSupport.requireNonNull(relatesTo.code, "code");
                ValidationSupport.requireNonNull(relatesTo.target, "target");
                ValidationSupport.checkReferenceType(relatesTo.target, "target", "DocumentReference");
                ValidationSupport.requireValueOrChildren(relatesTo);
            }

            protected Builder from(RelatesTo relatesTo) {
                super.from(relatesTo);
                code = relatesTo.code;
                target = relatesTo.target;
                return this;
            }
        }
    }

    /**
     * The document and format referenced. If there are multiple content element repetitions, these must all represent the 
     * same document in different format, or attachment metadata.
     */
    public static class Content extends BackboneElement {
        @Summary
        @Required
        private final Attachment attachment;
        @Summary
        private final List<Profile> profile;

        private Content(Builder builder) {
            super(builder);
            attachment = builder.attachment;
            profile = Collections.unmodifiableList(builder.profile);
        }

        /**
         * The document or URL of the document along with critical metadata to prove content has integrity.
         * 
         * @return
         *     An immutable object of type {@link Attachment} that is non-null.
         */
        public Attachment getAttachment() {
            return attachment;
        }

        /**
         * An identifier of the document constraints, encoding, structure, and template that the document conforms to beyond the 
         * base format indicated in the mimeType.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Profile} that may be empty.
         */
        public List<Profile> getProfile() {
            return profile;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (attachment != null) || 
                !profile.isEmpty();
        }

        @Override
        public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
            if (visitor.preVisit(this)) {
                visitor.visitStart(elementName, elementIndex, this);
                if (visitor.visit(elementName, elementIndex, this)) {
                    // visit children
                    accept(id, "id", visitor);
                    accept(extension, "extension", visitor, Extension.class);
                    accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                    accept(attachment, "attachment", visitor);
                    accept(profile, "profile", visitor, Profile.class);
                }
                visitor.visitEnd(elementName, elementIndex, this);
                visitor.postVisit(this);
            }
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
            Content other = (Content) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(attachment, other.attachment) && 
                Objects.equals(profile, other.profile);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    attachment, 
                    profile);
                hashCode = result;
            }
            return result;
        }

        @Override
        public Builder toBuilder() {
            return new Builder().from(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BackboneElement.Builder {
            private Attachment attachment;
            private List<Profile> profile = new ArrayList<>();

            private Builder() {
                super();
            }

            /**
             * Unique id for the element within a resource (for internal references). This may be any string value that does not 
             * contain spaces.
             * 
             * @param id
             *     Unique id for inter-element referencing
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder id(java.lang.String id) {
                return (Builder) super.id(id);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder extension(Extension... extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder extension(Collection<Extension> extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder modifierExtension(Extension... modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder modifierExtension(Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * The document or URL of the document along with critical metadata to prove content has integrity.
             * 
             * <p>This element is required.
             * 
             * @param attachment
             *     Where to access the document
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder attachment(Attachment attachment) {
                this.attachment = attachment;
                return this;
            }

            /**
             * An identifier of the document constraints, encoding, structure, and template that the document conforms to beyond the 
             * base format indicated in the mimeType.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param profile
             *     Content profile rules for the document
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder profile(Profile... profile) {
                for (Profile value : profile) {
                    this.profile.add(value);
                }
                return this;
            }

            /**
             * An identifier of the document constraints, encoding, structure, and template that the document conforms to beyond the 
             * base format indicated in the mimeType.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param profile
             *     Content profile rules for the document
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder profile(Collection<Profile> profile) {
                this.profile = new ArrayList<>(profile);
                return this;
            }

            /**
             * Build the {@link Content}
             * 
             * <p>Required elements:
             * <ul>
             * <li>attachment</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Content}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Content per the base specification
             */
            @Override
            public Content build() {
                Content content = new Content(this);
                if (validating) {
                    validate(content);
                }
                return content;
            }

            protected void validate(Content content) {
                super.validate(content);
                ValidationSupport.requireNonNull(content.attachment, "attachment");
                ValidationSupport.checkList(content.profile, "profile", Profile.class);
                ValidationSupport.requireValueOrChildren(content);
            }

            protected Builder from(Content content) {
                super.from(content);
                attachment = content.attachment;
                profile.addAll(content.profile);
                return this;
            }
        }

        /**
         * An identifier of the document constraints, encoding, structure, and template that the document conforms to beyond the 
         * base format indicated in the mimeType.
         */
        public static class Profile extends BackboneElement {
            @Summary
            @Choice({ Coding.class, Uri.class, Canonical.class })
            @Binding(
                bindingName = "DocumentFormat",
                strength = BindingStrength.Value.PREFERRED,
                description = "Document Format Codes.",
                valueSet = "http://terminology.hl7.org/ValueSet/v3-HL7FormatCodes"
            )
            @Required
            private final Element value;

            private Profile(Builder builder) {
                super(builder);
                value = builder.value;
            }

            /**
             * Code|uri|canonical.
             * 
             * @return
             *     An immutable object of type {@link Coding}, {@link Uri} or {@link Canonical} that is non-null.
             */
            public Element getValue() {
                return value;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (value != null);
            }

            @Override
            public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
                if (visitor.preVisit(this)) {
                    visitor.visitStart(elementName, elementIndex, this);
                    if (visitor.visit(elementName, elementIndex, this)) {
                        // visit children
                        accept(id, "id", visitor);
                        accept(extension, "extension", visitor, Extension.class);
                        accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                        accept(value, "value", visitor);
                    }
                    visitor.visitEnd(elementName, elementIndex, this);
                    visitor.postVisit(this);
                }
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
                Profile other = (Profile) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(value, other.value);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        value);
                    hashCode = result;
                }
                return result;
            }

            @Override
            public Builder toBuilder() {
                return new Builder().from(this);
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder extends BackboneElement.Builder {
                private Element value;

                private Builder() {
                    super();
                }

                /**
                 * Unique id for the element within a resource (for internal references). This may be any string value that does not 
                 * contain spaces.
                 * 
                 * @param id
                 *     Unique id for inter-element referencing
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder id(java.lang.String id) {
                    return (Builder) super.id(id);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder extension(Extension... extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder extension(Collection<Extension> extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder modifierExtension(Extension... modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder modifierExtension(Collection<Extension> modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * Code|uri|canonical.
                 * 
                 * <p>This element is required.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Coding}</li>
                 * <li>{@link Uri}</li>
                 * <li>{@link Canonical}</li>
                 * </ul>
                 * 
                 * @param value
                 *     Code|uri|canonical
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(Element value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Build the {@link Profile}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>value</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Profile}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Profile per the base specification
                 */
                @Override
                public Profile build() {
                    Profile profile = new Profile(this);
                    if (validating) {
                        validate(profile);
                    }
                    return profile;
                }

                protected void validate(Profile profile) {
                    super.validate(profile);
                    ValidationSupport.requireChoiceElement(profile.value, "value", Coding.class, Uri.class, Canonical.class);
                    ValidationSupport.requireValueOrChildren(profile);
                }

                protected Builder from(Profile profile) {
                    super.from(profile);
                    value = profile.value;
                    return this;
                }
            }
        }
    }
}
