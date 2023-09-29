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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.ContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.SpecimenContainedPreference;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A kind of specimen with associated set of requirements.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "specimenDefinition-0",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/SpecimenDefinition",
    generated = true
)
@Constraint(
    id = "specimenDefinition-1",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/SpecimenDefinition",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class SpecimenDefinition extends DomainResource {
    @Summary
    private final Uri url;
    @Summary
    private final Identifier identifier;
    @Summary
    private final String version;
    @Summary
    @Choice({ String.class, Coding.class })
    @Binding(
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/version-algorithm"
    )
    private final Element versionAlgorithm;
    @Summary
    private final String name;
    @Summary
    private final String title;
    @Summary
    private final List<Canonical> derivedFromCanonical;
    @Summary
    private final List<Uri> derivedFromUri;
    @Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes identifying the status of a SpecimenDefinition resource.",
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    @Required
    private final PublicationStatus status;
    @Summary
    private final Boolean experimental;
    @Summary
    @ReferenceTarget({ "Group" })
    @Choice({ CodeableConcept.class, Reference.class })
    private final Element subject;
    @Summary
    private final DateTime date;
    @Summary
    private final String publisher;
    @Summary
    private final List<ContactDetail> contact;
    private final Markdown description;
    private final List<UsageContext> useContext;
    @Summary
    @Binding(
        bindingName = "Jurisdiction",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Codes for country, country subdivision and region for indicating where a resource is intended to be used.",
        valueSet = "http://hl7.org/fhir/ValueSet/jurisdiction"
    )
    private final List<CodeableConcept> jurisdiction;
    private final Markdown purpose;
    private final Markdown copyright;
    private final String copyrightLabel;
    private final Date approvalDate;
    private final Date lastReviewDate;
    @Summary
    private final Period effectivePeriod;
    @Summary
    @Binding(
        bindingName = "CollectedSpecimenType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The type of the specimen to be collected.",
        valueSet = "http://terminology.hl7.org/ValueSet/v2-0487"
    )
    private final CodeableConcept typeCollected;
    @Summary
    @Binding(
        bindingName = "PreparePatient",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SCT descendants of 703763000 |Precondition value (qualifier value)|",
        valueSet = "http://hl7.org/fhir/ValueSet/prepare-patient-prior-specimen-collection"
    )
    private final List<CodeableConcept> patientPreparation;
    @Summary
    private final String timeAspect;
    @Summary
    @Binding(
        bindingName = "SpecimenCollection",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SCT actions and procedures for specimen collection",
        valueSet = "http://hl7.org/fhir/ValueSet/specimen-collection"
    )
    private final List<CodeableConcept> collection;
    private final List<TypeTested> typeTested;

    private SpecimenDefinition(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = builder.identifier;
        version = builder.version;
        versionAlgorithm = builder.versionAlgorithm;
        name = builder.name;
        title = builder.title;
        derivedFromCanonical = Collections.unmodifiableList(builder.derivedFromCanonical);
        derivedFromUri = Collections.unmodifiableList(builder.derivedFromUri);
        status = builder.status;
        experimental = builder.experimental;
        subject = builder.subject;
        date = builder.date;
        publisher = builder.publisher;
        contact = Collections.unmodifiableList(builder.contact);
        description = builder.description;
        useContext = Collections.unmodifiableList(builder.useContext);
        jurisdiction = Collections.unmodifiableList(builder.jurisdiction);
        purpose = builder.purpose;
        copyright = builder.copyright;
        copyrightLabel = builder.copyrightLabel;
        approvalDate = builder.approvalDate;
        lastReviewDate = builder.lastReviewDate;
        effectivePeriod = builder.effectivePeriod;
        typeCollected = builder.typeCollected;
        patientPreparation = Collections.unmodifiableList(builder.patientPreparation);
        timeAspect = builder.timeAspect;
        collection = Collections.unmodifiableList(builder.collection);
        typeTested = Collections.unmodifiableList(builder.typeTested);
    }

    /**
     * An absolute URL that is used to identify this SpecimenDefinition when it is referenced in a specification, model, 
     * design or an instance. This SHALL be a URL, SHOULD be globally unique, and SHOULD be an address at which this 
     * SpecimenDefinition is (or will be) published. The URL SHOULD include the major version of the SpecimenDefinition. For 
     * more information see Technical and Business Versions.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A business identifier assigned to this SpecimenDefinition.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the SpecimenDefinition when it is referenced in a 
     * specification, model, design or instance. This is an arbitrary value managed by the SpecimenDefinition author and is 
     * not expected to be globally unique.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Indicates the mechanism used to compare versions to determine which is more current.
     * 
     * @return
     *     An immutable object of type {@link String} or {@link Coding} that may be null.
     */
    public Element getVersionAlgorithm() {
        return versionAlgorithm;
    }

    /**
     * A natural language name identifying the {{title}}. This name should be usable as an identifier for the module by 
     * machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the SpecimenDefinition.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The canonical URL pointing to another FHIR-defined SpecimenDefinition that is adhered to in whole or in part by this 
     * definition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getDerivedFromCanonical() {
        return derivedFromCanonical;
    }

    /**
     * The URL pointing to an externally-defined type of specimen, guideline or other definition that is adhered to in whole 
     * or in part by this definition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getDerivedFromUri() {
        return derivedFromUri;
    }

    /**
     * The current state of theSpecimenDefinition.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A flag to indicate that this SpecimenDefinition is not authored for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * A code or group definition that describes the intended subject from which this kind of specimen is to be collected.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} or {@link Reference} that may be null.
     */
    public Element getSubject() {
        return subject;
    }

    /**
     * For draft definitions, indicates the date of initial creation. For active definitions, represents the date of 
     * activation. For withdrawn definitions, indicates the date of withdrawal.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * Helps establish the "authority/credibility" of the SpecimenDefinition. May also allow for contact.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Contact details to assist a user in finding and communicating with the publisher.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getContact() {
        return contact;
    }

    /**
     * A free text natural language description of the SpecimenDefinition from the consumer's perspective.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * The content was developed with a focus and intent of supporting the contexts that are listed. These terms may be used 
     * to assist with indexing and searching of specimen definitions.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A jurisdiction in which the SpecimenDefinition is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explains why this SpecimeDefinition is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * Copyright statement relating to the SpecimenDefinition and/or its contents. Copyright statements are generally legal 
     * restrictions on the use and publishing of the SpecimenDefinition.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getCopyright() {
        return copyright;
    }

    /**
     * A short string (&lt;50 characters), suitable for inclusion in a page footer that identifies the copyright holder, 
     * effective period, and optionally whether rights are resctricted. (e.g. 'All rights reserved', 'Some rights reserved').
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getCopyrightLabel() {
        return copyrightLabel;
    }

    /**
     * The date on which the asset content was approved by the publisher. Approval happens once when the content is 
     * officially approved for usage.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getApprovalDate() {
        return approvalDate;
    }

    /**
     * The date on which the asset content was last reviewed. Review happens periodically after that, but doesn't change the 
     * original approval date.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getLastReviewDate() {
        return lastReviewDate;
    }

    /**
     * The period during which the SpecimenDefinition content was or is planned to be effective.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * The kind of material to be collected.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getTypeCollected() {
        return typeCollected;
    }

    /**
     * Preparation of the patient for specimen collection.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getPatientPreparation() {
        return patientPreparation;
    }

    /**
     * Time aspect of specimen collection (duration or offset).
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTimeAspect() {
        return timeAspect;
    }

    /**
     * The action to be performed for collecting the specimen.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCollection() {
        return collection;
    }

    /**
     * Specimen conditioned in a container as expected by the testing laboratory.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link TypeTested} that may be empty.
     */
    public List<TypeTested> getTypeTested() {
        return typeTested;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (url != null) || 
            (identifier != null) || 
            (version != null) || 
            (versionAlgorithm != null) || 
            (name != null) || 
            (title != null) || 
            !derivedFromCanonical.isEmpty() || 
            !derivedFromUri.isEmpty() || 
            (status != null) || 
            (experimental != null) || 
            (subject != null) || 
            (date != null) || 
            (publisher != null) || 
            !contact.isEmpty() || 
            (description != null) || 
            !useContext.isEmpty() || 
            !jurisdiction.isEmpty() || 
            (purpose != null) || 
            (copyright != null) || 
            (copyrightLabel != null) || 
            (approvalDate != null) || 
            (lastReviewDate != null) || 
            (effectivePeriod != null) || 
            (typeCollected != null) || 
            !patientPreparation.isEmpty() || 
            (timeAspect != null) || 
            !collection.isEmpty() || 
            !typeTested.isEmpty();
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
                accept(url, "url", visitor);
                accept(identifier, "identifier", visitor);
                accept(version, "version", visitor);
                accept(versionAlgorithm, "versionAlgorithm", visitor);
                accept(name, "name", visitor);
                accept(title, "title", visitor);
                accept(derivedFromCanonical, "derivedFromCanonical", visitor, Canonical.class);
                accept(derivedFromUri, "derivedFromUri", visitor, Uri.class);
                accept(status, "status", visitor);
                accept(experimental, "experimental", visitor);
                accept(subject, "subject", visitor);
                accept(date, "date", visitor);
                accept(publisher, "publisher", visitor);
                accept(contact, "contact", visitor, ContactDetail.class);
                accept(description, "description", visitor);
                accept(useContext, "useContext", visitor, UsageContext.class);
                accept(jurisdiction, "jurisdiction", visitor, CodeableConcept.class);
                accept(purpose, "purpose", visitor);
                accept(copyright, "copyright", visitor);
                accept(copyrightLabel, "copyrightLabel", visitor);
                accept(approvalDate, "approvalDate", visitor);
                accept(lastReviewDate, "lastReviewDate", visitor);
                accept(effectivePeriod, "effectivePeriod", visitor);
                accept(typeCollected, "typeCollected", visitor);
                accept(patientPreparation, "patientPreparation", visitor, CodeableConcept.class);
                accept(timeAspect, "timeAspect", visitor);
                accept(collection, "collection", visitor, CodeableConcept.class);
                accept(typeTested, "typeTested", visitor, TypeTested.class);
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
        SpecimenDefinition other = (SpecimenDefinition) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(url, other.url) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(version, other.version) && 
            Objects.equals(versionAlgorithm, other.versionAlgorithm) && 
            Objects.equals(name, other.name) && 
            Objects.equals(title, other.title) && 
            Objects.equals(derivedFromCanonical, other.derivedFromCanonical) && 
            Objects.equals(derivedFromUri, other.derivedFromUri) && 
            Objects.equals(status, other.status) && 
            Objects.equals(experimental, other.experimental) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(date, other.date) && 
            Objects.equals(publisher, other.publisher) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(description, other.description) && 
            Objects.equals(useContext, other.useContext) && 
            Objects.equals(jurisdiction, other.jurisdiction) && 
            Objects.equals(purpose, other.purpose) && 
            Objects.equals(copyright, other.copyright) && 
            Objects.equals(copyrightLabel, other.copyrightLabel) && 
            Objects.equals(approvalDate, other.approvalDate) && 
            Objects.equals(lastReviewDate, other.lastReviewDate) && 
            Objects.equals(effectivePeriod, other.effectivePeriod) && 
            Objects.equals(typeCollected, other.typeCollected) && 
            Objects.equals(patientPreparation, other.patientPreparation) && 
            Objects.equals(timeAspect, other.timeAspect) && 
            Objects.equals(collection, other.collection) && 
            Objects.equals(typeTested, other.typeTested);
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
                url, 
                identifier, 
                version, 
                versionAlgorithm, 
                name, 
                title, 
                derivedFromCanonical, 
                derivedFromUri, 
                status, 
                experimental, 
                subject, 
                date, 
                publisher, 
                contact, 
                description, 
                useContext, 
                jurisdiction, 
                purpose, 
                copyright, 
                copyrightLabel, 
                approvalDate, 
                lastReviewDate, 
                effectivePeriod, 
                typeCollected, 
                patientPreparation, 
                timeAspect, 
                collection, 
                typeTested);
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
        private Uri url;
        private Identifier identifier;
        private String version;
        private Element versionAlgorithm;
        private String name;
        private String title;
        private List<Canonical> derivedFromCanonical = new ArrayList<>();
        private List<Uri> derivedFromUri = new ArrayList<>();
        private PublicationStatus status;
        private Boolean experimental;
        private Element subject;
        private DateTime date;
        private String publisher;
        private List<ContactDetail> contact = new ArrayList<>();
        private Markdown description;
        private List<UsageContext> useContext = new ArrayList<>();
        private List<CodeableConcept> jurisdiction = new ArrayList<>();
        private Markdown purpose;
        private Markdown copyright;
        private String copyrightLabel;
        private Date approvalDate;
        private Date lastReviewDate;
        private Period effectivePeriod;
        private CodeableConcept typeCollected;
        private List<CodeableConcept> patientPreparation = new ArrayList<>();
        private String timeAspect;
        private List<CodeableConcept> collection = new ArrayList<>();
        private List<TypeTested> typeTested = new ArrayList<>();

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
         * An absolute URL that is used to identify this SpecimenDefinition when it is referenced in a specification, model, 
         * design or an instance. This SHALL be a URL, SHOULD be globally unique, and SHOULD be an address at which this 
         * SpecimenDefinition is (or will be) published. The URL SHOULD include the major version of the SpecimenDefinition. For 
         * more information see Technical and Business Versions.
         * 
         * @param url
         *     Logical canonical URL to reference this SpecimenDefinition (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A business identifier assigned to this SpecimenDefinition.
         * 
         * @param identifier
         *     Business identifier
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder identifier(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * Convenience method for setting {@code version}.
         * 
         * @param version
         *     Business version of the SpecimenDefinition
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
         * The identifier that is used to identify this version of the SpecimenDefinition when it is referenced in a 
         * specification, model, design or instance. This is an arbitrary value managed by the SpecimenDefinition author and is 
         * not expected to be globally unique.
         * 
         * @param version
         *     Business version of the SpecimenDefinition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * Convenience method for setting {@code versionAlgorithm} with choice type String.
         * 
         * @param versionAlgorithm
         *     How to compare versions
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #versionAlgorithm(Element)
         */
        public Builder versionAlgorithm(java.lang.String versionAlgorithm) {
            this.versionAlgorithm = (versionAlgorithm == null) ? null : String.of(versionAlgorithm);
            return this;
        }

        /**
         * Indicates the mechanism used to compare versions to determine which is more current.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link String}</li>
         * <li>{@link Coding}</li>
         * </ul>
         * 
         * @param versionAlgorithm
         *     How to compare versions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder versionAlgorithm(Element versionAlgorithm) {
            this.versionAlgorithm = versionAlgorithm;
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Name for this {{title}} (computer friendly)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #name(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder name(java.lang.String name) {
            this.name = (name == null) ? null : String.of(name);
            return this;
        }

        /**
         * A natural language name identifying the {{title}}. This name should be usable as an identifier for the module by 
         * machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this {{title}} (computer friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Convenience method for setting {@code title}.
         * 
         * @param title
         *     Name for this SpecimenDefinition (Human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #title(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder title(java.lang.String title) {
            this.title = (title == null) ? null : String.of(title);
            return this;
        }

        /**
         * A short, descriptive, user-friendly title for the SpecimenDefinition.
         * 
         * @param title
         *     Name for this SpecimenDefinition (Human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * The canonical URL pointing to another FHIR-defined SpecimenDefinition that is adhered to in whole or in part by this 
         * definition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromCanonical
         *     Based on FHIR definition of another SpecimenDefinition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFromCanonical(Canonical... derivedFromCanonical) {
            for (Canonical value : derivedFromCanonical) {
                this.derivedFromCanonical.add(value);
            }
            return this;
        }

        /**
         * The canonical URL pointing to another FHIR-defined SpecimenDefinition that is adhered to in whole or in part by this 
         * definition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromCanonical
         *     Based on FHIR definition of another SpecimenDefinition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder derivedFromCanonical(Collection<Canonical> derivedFromCanonical) {
            this.derivedFromCanonical = new ArrayList<>(derivedFromCanonical);
            return this;
        }

        /**
         * The URL pointing to an externally-defined type of specimen, guideline or other definition that is adhered to in whole 
         * or in part by this definition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromUri
         *     Based on external definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFromUri(Uri... derivedFromUri) {
            for (Uri value : derivedFromUri) {
                this.derivedFromUri.add(value);
            }
            return this;
        }

        /**
         * The URL pointing to an externally-defined type of specimen, guideline or other definition that is adhered to in whole 
         * or in part by this definition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromUri
         *     Based on external definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder derivedFromUri(Collection<Uri> derivedFromUri) {
            this.derivedFromUri = new ArrayList<>(derivedFromUri);
            return this;
        }

        /**
         * The current state of theSpecimenDefinition.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | retired | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(PublicationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Convenience method for setting {@code experimental}.
         * 
         * @param experimental
         *     If this SpecimenDefinition is not for real usage
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #experimental(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder experimental(java.lang.Boolean experimental) {
            this.experimental = (experimental == null) ? null : Boolean.of(experimental);
            return this;
        }

        /**
         * A flag to indicate that this SpecimenDefinition is not authored for genuine usage.
         * 
         * @param experimental
         *     If this SpecimenDefinition is not for real usage
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder experimental(Boolean experimental) {
            this.experimental = experimental;
            return this;
        }

        /**
         * A code or group definition that describes the intended subject from which this kind of specimen is to be collected.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link CodeableConcept}</li>
         * <li>{@link Reference}</li>
         * </ul>
         * 
         * When of type {@link Reference}, the allowed resource types for this reference are:
         * <ul>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     Type of subject for specimen collection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Element subject) {
            this.subject = subject;
            return this;
        }

        /**
         * For draft definitions, indicates the date of initial creation. For active definitions, represents the date of 
         * activation. For withdrawn definitions, indicates the date of withdrawal.
         * 
         * @param date
         *     Date status first applied
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(DateTime date) {
            this.date = date;
            return this;
        }

        /**
         * Convenience method for setting {@code publisher}.
         * 
         * @param publisher
         *     The name of the individual or organization that published the SpecimenDefinition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #publisher(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder publisher(java.lang.String publisher) {
            this.publisher = (publisher == null) ? null : String.of(publisher);
            return this;
        }

        /**
         * Helps establish the "authority/credibility" of the SpecimenDefinition. May also allow for contact.
         * 
         * @param publisher
         *     The name of the individual or organization that published the SpecimenDefinition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        /**
         * Contact details to assist a user in finding and communicating with the publisher.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contact(ContactDetail... contact) {
            for (ContactDetail value : contact) {
                this.contact.add(value);
            }
            return this;
        }

        /**
         * Contact details to assist a user in finding and communicating with the publisher.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for the publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contact(Collection<ContactDetail> contact) {
            this.contact = new ArrayList<>(contact);
            return this;
        }

        /**
         * A free text natural language description of the SpecimenDefinition from the consumer's perspective.
         * 
         * @param description
         *     Natural language description of the SpecimenDefinition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * The content was developed with a focus and intent of supporting the contexts that are listed. These terms may be used 
         * to assist with indexing and searching of specimen definitions.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     Content intends to support these contexts
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder useContext(UsageContext... useContext) {
            for (UsageContext value : useContext) {
                this.useContext.add(value);
            }
            return this;
        }

        /**
         * The content was developed with a focus and intent of supporting the contexts that are listed. These terms may be used 
         * to assist with indexing and searching of specimen definitions.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     Content intends to support these contexts
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder useContext(Collection<UsageContext> useContext) {
            this.useContext = new ArrayList<>(useContext);
            return this;
        }

        /**
         * A jurisdiction in which the SpecimenDefinition is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for this SpecimenDefinition (if applicable)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder jurisdiction(CodeableConcept... jurisdiction) {
            for (CodeableConcept value : jurisdiction) {
                this.jurisdiction.add(value);
            }
            return this;
        }

        /**
         * A jurisdiction in which the SpecimenDefinition is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for this SpecimenDefinition (if applicable)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder jurisdiction(Collection<CodeableConcept> jurisdiction) {
            this.jurisdiction = new ArrayList<>(jurisdiction);
            return this;
        }

        /**
         * Explains why this SpecimeDefinition is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this SpecimenDefinition is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * Copyright statement relating to the SpecimenDefinition and/or its contents. Copyright statements are generally legal 
         * restrictions on the use and publishing of the SpecimenDefinition.
         * 
         * @param copyright
         *     Use and/or publishing restrictions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder copyright(Markdown copyright) {
            this.copyright = copyright;
            return this;
        }

        /**
         * Convenience method for setting {@code copyrightLabel}.
         * 
         * @param copyrightLabel
         *     Copyright holder and year(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #copyrightLabel(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder copyrightLabel(java.lang.String copyrightLabel) {
            this.copyrightLabel = (copyrightLabel == null) ? null : String.of(copyrightLabel);
            return this;
        }

        /**
         * A short string (&lt;50 characters), suitable for inclusion in a page footer that identifies the copyright holder, 
         * effective period, and optionally whether rights are resctricted. (e.g. 'All rights reserved', 'Some rights reserved').
         * 
         * @param copyrightLabel
         *     Copyright holder and year(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder copyrightLabel(String copyrightLabel) {
            this.copyrightLabel = copyrightLabel;
            return this;
        }

        /**
         * Convenience method for setting {@code approvalDate}.
         * 
         * @param approvalDate
         *     When SpecimenDefinition was approved by publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #approvalDate(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder approvalDate(java.time.LocalDate approvalDate) {
            this.approvalDate = (approvalDate == null) ? null : Date.of(approvalDate);
            return this;
        }

        /**
         * The date on which the asset content was approved by the publisher. Approval happens once when the content is 
         * officially approved for usage.
         * 
         * @param approvalDate
         *     When SpecimenDefinition was approved by publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder approvalDate(Date approvalDate) {
            this.approvalDate = approvalDate;
            return this;
        }

        /**
         * Convenience method for setting {@code lastReviewDate}.
         * 
         * @param lastReviewDate
         *     The date on which the asset content was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #lastReviewDate(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder lastReviewDate(java.time.LocalDate lastReviewDate) {
            this.lastReviewDate = (lastReviewDate == null) ? null : Date.of(lastReviewDate);
            return this;
        }

        /**
         * The date on which the asset content was last reviewed. Review happens periodically after that, but doesn't change the 
         * original approval date.
         * 
         * @param lastReviewDate
         *     The date on which the asset content was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
        }

        /**
         * The period during which the SpecimenDefinition content was or is planned to be effective.
         * 
         * @param effectivePeriod
         *     The effective date range for the SpecimenDefinition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effectivePeriod(Period effectivePeriod) {
            this.effectivePeriod = effectivePeriod;
            return this;
        }

        /**
         * The kind of material to be collected.
         * 
         * @param typeCollected
         *     Kind of material to collect
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder typeCollected(CodeableConcept typeCollected) {
            this.typeCollected = typeCollected;
            return this;
        }

        /**
         * Preparation of the patient for specimen collection.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param patientPreparation
         *     Patient preparation for collection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patientPreparation(CodeableConcept... patientPreparation) {
            for (CodeableConcept value : patientPreparation) {
                this.patientPreparation.add(value);
            }
            return this;
        }

        /**
         * Preparation of the patient for specimen collection.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param patientPreparation
         *     Patient preparation for collection
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder patientPreparation(Collection<CodeableConcept> patientPreparation) {
            this.patientPreparation = new ArrayList<>(patientPreparation);
            return this;
        }

        /**
         * Convenience method for setting {@code timeAspect}.
         * 
         * @param timeAspect
         *     Time aspect for collection
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #timeAspect(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder timeAspect(java.lang.String timeAspect) {
            this.timeAspect = (timeAspect == null) ? null : String.of(timeAspect);
            return this;
        }

        /**
         * Time aspect of specimen collection (duration or offset).
         * 
         * @param timeAspect
         *     Time aspect for collection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder timeAspect(String timeAspect) {
            this.timeAspect = timeAspect;
            return this;
        }

        /**
         * The action to be performed for collecting the specimen.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param collection
         *     Specimen collection procedure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder collection(CodeableConcept... collection) {
            for (CodeableConcept value : collection) {
                this.collection.add(value);
            }
            return this;
        }

        /**
         * The action to be performed for collecting the specimen.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param collection
         *     Specimen collection procedure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder collection(Collection<CodeableConcept> collection) {
            this.collection = new ArrayList<>(collection);
            return this;
        }

        /**
         * Specimen conditioned in a container as expected by the testing laboratory.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param typeTested
         *     Specimen in container intended for testing by lab
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder typeTested(TypeTested... typeTested) {
            for (TypeTested value : typeTested) {
                this.typeTested.add(value);
            }
            return this;
        }

        /**
         * Specimen conditioned in a container as expected by the testing laboratory.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param typeTested
         *     Specimen in container intended for testing by lab
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder typeTested(Collection<TypeTested> typeTested) {
            this.typeTested = new ArrayList<>(typeTested);
            return this;
        }

        /**
         * Build the {@link SpecimenDefinition}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link SpecimenDefinition}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid SpecimenDefinition per the base specification
         */
        @Override
        public SpecimenDefinition build() {
            SpecimenDefinition specimenDefinition = new SpecimenDefinition(this);
            if (validating) {
                validate(specimenDefinition);
            }
            return specimenDefinition;
        }

        protected void validate(SpecimenDefinition specimenDefinition) {
            super.validate(specimenDefinition);
            ValidationSupport.choiceElement(specimenDefinition.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.checkList(specimenDefinition.derivedFromCanonical, "derivedFromCanonical", Canonical.class);
            ValidationSupport.checkList(specimenDefinition.derivedFromUri, "derivedFromUri", Uri.class);
            ValidationSupport.requireNonNull(specimenDefinition.status, "status");
            ValidationSupport.choiceElement(specimenDefinition.subject, "subject", CodeableConcept.class, Reference.class);
            ValidationSupport.checkList(specimenDefinition.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(specimenDefinition.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(specimenDefinition.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(specimenDefinition.patientPreparation, "patientPreparation", CodeableConcept.class);
            ValidationSupport.checkList(specimenDefinition.collection, "collection", CodeableConcept.class);
            ValidationSupport.checkList(specimenDefinition.typeTested, "typeTested", TypeTested.class);
            ValidationSupport.checkReferenceType(specimenDefinition.subject, "subject", "Group");
        }

        protected Builder from(SpecimenDefinition specimenDefinition) {
            super.from(specimenDefinition);
            url = specimenDefinition.url;
            identifier = specimenDefinition.identifier;
            version = specimenDefinition.version;
            versionAlgorithm = specimenDefinition.versionAlgorithm;
            name = specimenDefinition.name;
            title = specimenDefinition.title;
            derivedFromCanonical.addAll(specimenDefinition.derivedFromCanonical);
            derivedFromUri.addAll(specimenDefinition.derivedFromUri);
            status = specimenDefinition.status;
            experimental = specimenDefinition.experimental;
            subject = specimenDefinition.subject;
            date = specimenDefinition.date;
            publisher = specimenDefinition.publisher;
            contact.addAll(specimenDefinition.contact);
            description = specimenDefinition.description;
            useContext.addAll(specimenDefinition.useContext);
            jurisdiction.addAll(specimenDefinition.jurisdiction);
            purpose = specimenDefinition.purpose;
            copyright = specimenDefinition.copyright;
            copyrightLabel = specimenDefinition.copyrightLabel;
            approvalDate = specimenDefinition.approvalDate;
            lastReviewDate = specimenDefinition.lastReviewDate;
            effectivePeriod = specimenDefinition.effectivePeriod;
            typeCollected = specimenDefinition.typeCollected;
            patientPreparation.addAll(specimenDefinition.patientPreparation);
            timeAspect = specimenDefinition.timeAspect;
            collection.addAll(specimenDefinition.collection);
            typeTested.addAll(specimenDefinition.typeTested);
            return this;
        }
    }

    /**
     * Specimen conditioned in a container as expected by the testing laboratory.
     */
    public static class TypeTested extends BackboneElement {
        private final Boolean isDerived;
        @Binding(
            bindingName = "IntendedSpecimenType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The type of specimen conditioned in a container for lab testing.",
            valueSet = "http://terminology.hl7.org/ValueSet/v2-0487"
        )
        private final CodeableConcept type;
        @Binding(
            bindingName = "SpecimenContainedPreference",
            strength = BindingStrength.Value.REQUIRED,
            description = "Degree of preference of a type of conditioned specimen.",
            valueSet = "http://hl7.org/fhir/ValueSet/specimen-contained-preference|5.0.0"
        )
        @Required
        private final SpecimenContainedPreference preference;
        private final Container container;
        private final Markdown requirement;
        private final Duration retentionTime;
        private final Boolean singleUse;
        @Binding(
            bindingName = "RejectionCriterion",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Criterion for rejection of the specimen by laboratory.",
            valueSet = "http://hl7.org/fhir/ValueSet/rejection-criteria"
        )
        private final List<CodeableConcept> rejectionCriterion;
        private final List<Handling> handling;
        @Binding(
            bindingName = "TestingDestination",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes specifying where the specimen will be tested.",
            valueSet = "http://hl7.org/fhir/ValueSet/diagnostic-service-sections"
        )
        private final List<CodeableConcept> testingDestination;

        private TypeTested(Builder builder) {
            super(builder);
            isDerived = builder.isDerived;
            type = builder.type;
            preference = builder.preference;
            container = builder.container;
            requirement = builder.requirement;
            retentionTime = builder.retentionTime;
            singleUse = builder.singleUse;
            rejectionCriterion = Collections.unmodifiableList(builder.rejectionCriterion);
            handling = Collections.unmodifiableList(builder.handling);
            testingDestination = Collections.unmodifiableList(builder.testingDestination);
        }

        /**
         * Primary of secondary specimen.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getIsDerived() {
            return isDerived;
        }

        /**
         * The kind of specimen conditioned for testing expected by lab.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The preference for this type of conditioned specimen.
         * 
         * @return
         *     An immutable object of type {@link SpecimenContainedPreference} that is non-null.
         */
        public SpecimenContainedPreference getPreference() {
            return preference;
        }

        /**
         * The specimen's container.
         * 
         * @return
         *     An immutable object of type {@link Container} that may be null.
         */
        public Container getContainer() {
            return container;
        }

        /**
         * Requirements for delivery and special handling of this kind of conditioned specimen.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getRequirement() {
            return requirement;
        }

        /**
         * The usual time that a specimen of this kind is retained after the ordered tests are completed, for the purpose of 
         * additional testing.
         * 
         * @return
         *     An immutable object of type {@link Duration} that may be null.
         */
        public Duration getRetentionTime() {
            return retentionTime;
        }

        /**
         * Specimen can be used by only one test or panel if the value is "true".
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getSingleUse() {
            return singleUse;
        }

        /**
         * Criterion for rejection of the specimen in its container by the laboratory.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getRejectionCriterion() {
            return rejectionCriterion;
        }

        /**
         * Set of instructions for preservation/transport of the specimen at a defined temperature interval, prior the testing 
         * process.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Handling} that may be empty.
         */
        public List<Handling> getHandling() {
            return handling;
        }

        /**
         * Where the specimen will be tested: e.g., lab, sector, device or any combination of these.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getTestingDestination() {
            return testingDestination;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (isDerived != null) || 
                (type != null) || 
                (preference != null) || 
                (container != null) || 
                (requirement != null) || 
                (retentionTime != null) || 
                (singleUse != null) || 
                !rejectionCriterion.isEmpty() || 
                !handling.isEmpty() || 
                !testingDestination.isEmpty();
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
                    accept(isDerived, "isDerived", visitor);
                    accept(type, "type", visitor);
                    accept(preference, "preference", visitor);
                    accept(container, "container", visitor);
                    accept(requirement, "requirement", visitor);
                    accept(retentionTime, "retentionTime", visitor);
                    accept(singleUse, "singleUse", visitor);
                    accept(rejectionCriterion, "rejectionCriterion", visitor, CodeableConcept.class);
                    accept(handling, "handling", visitor, Handling.class);
                    accept(testingDestination, "testingDestination", visitor, CodeableConcept.class);
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
            TypeTested other = (TypeTested) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(isDerived, other.isDerived) && 
                Objects.equals(type, other.type) && 
                Objects.equals(preference, other.preference) && 
                Objects.equals(container, other.container) && 
                Objects.equals(requirement, other.requirement) && 
                Objects.equals(retentionTime, other.retentionTime) && 
                Objects.equals(singleUse, other.singleUse) && 
                Objects.equals(rejectionCriterion, other.rejectionCriterion) && 
                Objects.equals(handling, other.handling) && 
                Objects.equals(testingDestination, other.testingDestination);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    isDerived, 
                    type, 
                    preference, 
                    container, 
                    requirement, 
                    retentionTime, 
                    singleUse, 
                    rejectionCriterion, 
                    handling, 
                    testingDestination);
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
            private Boolean isDerived;
            private CodeableConcept type;
            private SpecimenContainedPreference preference;
            private Container container;
            private Markdown requirement;
            private Duration retentionTime;
            private Boolean singleUse;
            private List<CodeableConcept> rejectionCriterion = new ArrayList<>();
            private List<Handling> handling = new ArrayList<>();
            private List<CodeableConcept> testingDestination = new ArrayList<>();

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
             * Convenience method for setting {@code isDerived}.
             * 
             * @param isDerived
             *     Primary or secondary specimen
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #isDerived(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder isDerived(java.lang.Boolean isDerived) {
                this.isDerived = (isDerived == null) ? null : Boolean.of(isDerived);
                return this;
            }

            /**
             * Primary of secondary specimen.
             * 
             * @param isDerived
             *     Primary or secondary specimen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder isDerived(Boolean isDerived) {
                this.isDerived = isDerived;
                return this;
            }

            /**
             * The kind of specimen conditioned for testing expected by lab.
             * 
             * @param type
             *     Type of intended specimen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The preference for this type of conditioned specimen.
             * 
             * <p>This element is required.
             * 
             * @param preference
             *     preferred | alternate
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder preference(SpecimenContainedPreference preference) {
                this.preference = preference;
                return this;
            }

            /**
             * The specimen's container.
             * 
             * @param container
             *     The specimen's container
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder container(Container container) {
                this.container = container;
                return this;
            }

            /**
             * Requirements for delivery and special handling of this kind of conditioned specimen.
             * 
             * @param requirement
             *     Requirements for specimen delivery and special handling
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder requirement(Markdown requirement) {
                this.requirement = requirement;
                return this;
            }

            /**
             * The usual time that a specimen of this kind is retained after the ordered tests are completed, for the purpose of 
             * additional testing.
             * 
             * @param retentionTime
             *     The usual time for retaining this kind of specimen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder retentionTime(Duration retentionTime) {
                this.retentionTime = retentionTime;
                return this;
            }

            /**
             * Convenience method for setting {@code singleUse}.
             * 
             * @param singleUse
             *     Specimen for single use only
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #singleUse(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder singleUse(java.lang.Boolean singleUse) {
                this.singleUse = (singleUse == null) ? null : Boolean.of(singleUse);
                return this;
            }

            /**
             * Specimen can be used by only one test or panel if the value is "true".
             * 
             * @param singleUse
             *     Specimen for single use only
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder singleUse(Boolean singleUse) {
                this.singleUse = singleUse;
                return this;
            }

            /**
             * Criterion for rejection of the specimen in its container by the laboratory.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param rejectionCriterion
             *     Criterion specified for specimen rejection
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder rejectionCriterion(CodeableConcept... rejectionCriterion) {
                for (CodeableConcept value : rejectionCriterion) {
                    this.rejectionCriterion.add(value);
                }
                return this;
            }

            /**
             * Criterion for rejection of the specimen in its container by the laboratory.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param rejectionCriterion
             *     Criterion specified for specimen rejection
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder rejectionCriterion(Collection<CodeableConcept> rejectionCriterion) {
                this.rejectionCriterion = new ArrayList<>(rejectionCriterion);
                return this;
            }

            /**
             * Set of instructions for preservation/transport of the specimen at a defined temperature interval, prior the testing 
             * process.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param handling
             *     Specimen handling before testing
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder handling(Handling... handling) {
                for (Handling value : handling) {
                    this.handling.add(value);
                }
                return this;
            }

            /**
             * Set of instructions for preservation/transport of the specimen at a defined temperature interval, prior the testing 
             * process.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param handling
             *     Specimen handling before testing
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder handling(Collection<Handling> handling) {
                this.handling = new ArrayList<>(handling);
                return this;
            }

            /**
             * Where the specimen will be tested: e.g., lab, sector, device or any combination of these.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param testingDestination
             *     Where the specimen will be tested
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder testingDestination(CodeableConcept... testingDestination) {
                for (CodeableConcept value : testingDestination) {
                    this.testingDestination.add(value);
                }
                return this;
            }

            /**
             * Where the specimen will be tested: e.g., lab, sector, device or any combination of these.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param testingDestination
             *     Where the specimen will be tested
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder testingDestination(Collection<CodeableConcept> testingDestination) {
                this.testingDestination = new ArrayList<>(testingDestination);
                return this;
            }

            /**
             * Build the {@link TypeTested}
             * 
             * <p>Required elements:
             * <ul>
             * <li>preference</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link TypeTested}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid TypeTested per the base specification
             */
            @Override
            public TypeTested build() {
                TypeTested typeTested = new TypeTested(this);
                if (validating) {
                    validate(typeTested);
                }
                return typeTested;
            }

            protected void validate(TypeTested typeTested) {
                super.validate(typeTested);
                ValidationSupport.requireNonNull(typeTested.preference, "preference");
                ValidationSupport.checkList(typeTested.rejectionCriterion, "rejectionCriterion", CodeableConcept.class);
                ValidationSupport.checkList(typeTested.handling, "handling", Handling.class);
                ValidationSupport.checkList(typeTested.testingDestination, "testingDestination", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(typeTested);
            }

            protected Builder from(TypeTested typeTested) {
                super.from(typeTested);
                isDerived = typeTested.isDerived;
                type = typeTested.type;
                preference = typeTested.preference;
                container = typeTested.container;
                requirement = typeTested.requirement;
                retentionTime = typeTested.retentionTime;
                singleUse = typeTested.singleUse;
                rejectionCriterion.addAll(typeTested.rejectionCriterion);
                handling.addAll(typeTested.handling);
                testingDestination.addAll(typeTested.testingDestination);
                return this;
            }
        }

        /**
         * The specimen's container.
         */
        public static class Container extends BackboneElement {
            @Binding(
                bindingName = "ContainerMaterial",
                strength = BindingStrength.Value.EXAMPLE,
                description = "SCT 32039001 |Glass|, 61088005 |Plastic|, 425620007 |Metal|",
                valueSet = "http://hl7.org/fhir/ValueSet/container-material"
            )
            private final CodeableConcept material;
            @Binding(
                bindingName = "ContainerType",
                strength = BindingStrength.Value.EXAMPLE,
                description = "SCT descendants of 706041008 |Device for body fluid and tissue collection/transfer/processing (physical object)|",
                valueSet = "http://hl7.org/fhir/ValueSet/specimen-container-type"
            )
            private final CodeableConcept type;
            @Binding(
                bindingName = "ContainerCap",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Color of the container cap.",
                valueSet = "http://hl7.org/fhir/ValueSet/container-cap"
            )
            private final CodeableConcept cap;
            private final Markdown description;
            private final SimpleQuantity capacity;
            @Choice({ SimpleQuantity.class, String.class })
            private final Element minimumVolume;
            private final List<Additive> additive;
            private final Markdown preparation;

            private Container(Builder builder) {
                super(builder);
                material = builder.material;
                type = builder.type;
                cap = builder.cap;
                description = builder.description;
                capacity = builder.capacity;
                minimumVolume = builder.minimumVolume;
                additive = Collections.unmodifiableList(builder.additive);
                preparation = builder.preparation;
            }

            /**
             * The type of material of the container.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getMaterial() {
                return material;
            }

            /**
             * The type of container used to contain this kind of specimen.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * Color of container cap.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCap() {
                return cap;
            }

            /**
             * The textual description of the kind of container.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getDescription() {
                return description;
            }

            /**
             * The capacity (volume or other measure) of this kind of container.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} that may be null.
             */
            public SimpleQuantity getCapacity() {
                return capacity;
            }

            /**
             * The minimum volume to be conditioned in the container.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} or {@link String} that may be null.
             */
            public Element getMinimumVolume() {
                return minimumVolume;
            }

            /**
             * Substance introduced in the kind of container to preserve, maintain or enhance the specimen. Examples: Formalin, 
             * Citrate, EDTA.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Additive} that may be empty.
             */
            public List<Additive> getAdditive() {
                return additive;
            }

            /**
             * Special processing that should be applied to the container for this kind of specimen.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getPreparation() {
                return preparation;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (material != null) || 
                    (type != null) || 
                    (cap != null) || 
                    (description != null) || 
                    (capacity != null) || 
                    (minimumVolume != null) || 
                    !additive.isEmpty() || 
                    (preparation != null);
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
                        accept(material, "material", visitor);
                        accept(type, "type", visitor);
                        accept(cap, "cap", visitor);
                        accept(description, "description", visitor);
                        accept(capacity, "capacity", visitor);
                        accept(minimumVolume, "minimumVolume", visitor);
                        accept(additive, "additive", visitor, Additive.class);
                        accept(preparation, "preparation", visitor);
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
                Container other = (Container) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(material, other.material) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(cap, other.cap) && 
                    Objects.equals(description, other.description) && 
                    Objects.equals(capacity, other.capacity) && 
                    Objects.equals(minimumVolume, other.minimumVolume) && 
                    Objects.equals(additive, other.additive) && 
                    Objects.equals(preparation, other.preparation);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        material, 
                        type, 
                        cap, 
                        description, 
                        capacity, 
                        minimumVolume, 
                        additive, 
                        preparation);
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
                private CodeableConcept material;
                private CodeableConcept type;
                private CodeableConcept cap;
                private Markdown description;
                private SimpleQuantity capacity;
                private Element minimumVolume;
                private List<Additive> additive = new ArrayList<>();
                private Markdown preparation;

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
                 * The type of material of the container.
                 * 
                 * @param material
                 *     The material type used for the container
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder material(CodeableConcept material) {
                    this.material = material;
                    return this;
                }

                /**
                 * The type of container used to contain this kind of specimen.
                 * 
                 * @param type
                 *     Kind of container associated with the kind of specimen
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Color of container cap.
                 * 
                 * @param cap
                 *     Color of container cap
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder cap(CodeableConcept cap) {
                    this.cap = cap;
                    return this;
                }

                /**
                 * The textual description of the kind of container.
                 * 
                 * @param description
                 *     The description of the kind of container
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder description(Markdown description) {
                    this.description = description;
                    return this;
                }

                /**
                 * The capacity (volume or other measure) of this kind of container.
                 * 
                 * @param capacity
                 *     The capacity of this kind of container
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder capacity(SimpleQuantity capacity) {
                    this.capacity = capacity;
                    return this;
                }

                /**
                 * Convenience method for setting {@code minimumVolume} with choice type String.
                 * 
                 * @param minimumVolume
                 *     Minimum volume
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #minimumVolume(Element)
                 */
                public Builder minimumVolume(java.lang.String minimumVolume) {
                    this.minimumVolume = (minimumVolume == null) ? null : String.of(minimumVolume);
                    return this;
                }

                /**
                 * The minimum volume to be conditioned in the container.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link SimpleQuantity}</li>
                 * <li>{@link String}</li>
                 * </ul>
                 * 
                 * @param minimumVolume
                 *     Minimum volume
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder minimumVolume(Element minimumVolume) {
                    this.minimumVolume = minimumVolume;
                    return this;
                }

                /**
                 * Substance introduced in the kind of container to preserve, maintain or enhance the specimen. Examples: Formalin, 
                 * Citrate, EDTA.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param additive
                 *     Additive associated with container
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder additive(Additive... additive) {
                    for (Additive value : additive) {
                        this.additive.add(value);
                    }
                    return this;
                }

                /**
                 * Substance introduced in the kind of container to preserve, maintain or enhance the specimen. Examples: Formalin, 
                 * Citrate, EDTA.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param additive
                 *     Additive associated with container
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder additive(Collection<Additive> additive) {
                    this.additive = new ArrayList<>(additive);
                    return this;
                }

                /**
                 * Special processing that should be applied to the container for this kind of specimen.
                 * 
                 * @param preparation
                 *     Special processing applied to the container for this specimen type
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder preparation(Markdown preparation) {
                    this.preparation = preparation;
                    return this;
                }

                /**
                 * Build the {@link Container}
                 * 
                 * @return
                 *     An immutable object of type {@link Container}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Container per the base specification
                 */
                @Override
                public Container build() {
                    Container container = new Container(this);
                    if (validating) {
                        validate(container);
                    }
                    return container;
                }

                protected void validate(Container container) {
                    super.validate(container);
                    ValidationSupport.choiceElement(container.minimumVolume, "minimumVolume", SimpleQuantity.class, String.class);
                    ValidationSupport.checkList(container.additive, "additive", Additive.class);
                    ValidationSupport.requireValueOrChildren(container);
                }

                protected Builder from(Container container) {
                    super.from(container);
                    material = container.material;
                    type = container.type;
                    cap = container.cap;
                    description = container.description;
                    capacity = container.capacity;
                    minimumVolume = container.minimumVolume;
                    additive.addAll(container.additive);
                    preparation = container.preparation;
                    return this;
                }
            }

            /**
             * Substance introduced in the kind of container to preserve, maintain or enhance the specimen. Examples: Formalin, 
             * Citrate, EDTA.
             */
            public static class Additive extends BackboneElement {
                @ReferenceTarget({ "SubstanceDefinition" })
                @Choice({ CodeableConcept.class, Reference.class })
                @Binding(
                    bindingName = "ContainerAdditive",
                    strength = BindingStrength.Value.EXAMPLE,
                    description = "Substance added to specimen container.",
                    valueSet = "http://terminology.hl7.org/ValueSet/v2-0371"
                )
                @Required
                private final Element additive;

                private Additive(Builder builder) {
                    super(builder);
                    additive = builder.additive;
                }

                /**
                 * Substance introduced in the kind of container to preserve, maintain or enhance the specimen. Examples: Formalin, 
                 * Citrate, EDTA.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} or {@link Reference} that is non-null.
                 */
                public Element getAdditive() {
                    return additive;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (additive != null);
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
                            accept(additive, "additive", visitor);
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
                    Additive other = (Additive) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(additive, other.additive);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            additive);
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
                    private Element additive;

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
                     * Substance introduced in the kind of container to preserve, maintain or enhance the specimen. Examples: Formalin, 
                     * Citrate, EDTA.
                     * 
                     * <p>This element is required.
                     * 
                     * <p>This is a choice element with the following allowed types:
                     * <ul>
                     * <li>{@link CodeableConcept}</li>
                     * <li>{@link Reference}</li>
                     * </ul>
                     * 
                     * When of type {@link Reference}, the allowed resource types for this reference are:
                     * <ul>
                     * <li>{@link SubstanceDefinition}</li>
                     * </ul>
                     * 
                     * @param additive
                     *     Additive associated with container
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder additive(Element additive) {
                        this.additive = additive;
                        return this;
                    }

                    /**
                     * Build the {@link Additive}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>additive</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Additive}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Additive per the base specification
                     */
                    @Override
                    public Additive build() {
                        Additive additive = new Additive(this);
                        if (validating) {
                            validate(additive);
                        }
                        return additive;
                    }

                    protected void validate(Additive additive) {
                        super.validate(additive);
                        ValidationSupport.requireChoiceElement(additive.additive, "additive", CodeableConcept.class, Reference.class);
                        ValidationSupport.checkReferenceType(additive.additive, "additive", "SubstanceDefinition");
                        ValidationSupport.requireValueOrChildren(additive);
                    }

                    protected Builder from(Additive additive) {
                        super.from(additive);
                        this.additive = additive.additive;
                        return this;
                    }
                }
            }
        }

        /**
         * Set of instructions for preservation/transport of the specimen at a defined temperature interval, prior the testing 
         * process.
         */
        public static class Handling extends BackboneElement {
            @Binding(
                bindingName = "HandlingConditionSet",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Set of handling instructions prior testing of the specimen.",
                valueSet = "http://hl7.org/fhir/ValueSet/handling-condition"
            )
            private final CodeableConcept temperatureQualifier;
            private final Range temperatureRange;
            private final Duration maxDuration;
            private final Markdown instruction;

            private Handling(Builder builder) {
                super(builder);
                temperatureQualifier = builder.temperatureQualifier;
                temperatureRange = builder.temperatureRange;
                maxDuration = builder.maxDuration;
                instruction = builder.instruction;
            }

            /**
             * It qualifies the interval of temperature, which characterizes an occurrence of handling. Conditions that are not 
             * related to temperature may be handled in the instruction element.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getTemperatureQualifier() {
                return temperatureQualifier;
            }

            /**
             * The temperature interval for this set of handling instructions.
             * 
             * @return
             *     An immutable object of type {@link Range} that may be null.
             */
            public Range getTemperatureRange() {
                return temperatureRange;
            }

            /**
             * The maximum time interval of preservation of the specimen with these conditions.
             * 
             * @return
             *     An immutable object of type {@link Duration} that may be null.
             */
            public Duration getMaxDuration() {
                return maxDuration;
            }

            /**
             * Additional textual instructions for the preservation or transport of the specimen. For instance, 'Protect from light 
             * exposure'.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getInstruction() {
                return instruction;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (temperatureQualifier != null) || 
                    (temperatureRange != null) || 
                    (maxDuration != null) || 
                    (instruction != null);
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
                        accept(temperatureQualifier, "temperatureQualifier", visitor);
                        accept(temperatureRange, "temperatureRange", visitor);
                        accept(maxDuration, "maxDuration", visitor);
                        accept(instruction, "instruction", visitor);
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
                Handling other = (Handling) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(temperatureQualifier, other.temperatureQualifier) && 
                    Objects.equals(temperatureRange, other.temperatureRange) && 
                    Objects.equals(maxDuration, other.maxDuration) && 
                    Objects.equals(instruction, other.instruction);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        temperatureQualifier, 
                        temperatureRange, 
                        maxDuration, 
                        instruction);
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
                private CodeableConcept temperatureQualifier;
                private Range temperatureRange;
                private Duration maxDuration;
                private Markdown instruction;

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
                 * It qualifies the interval of temperature, which characterizes an occurrence of handling. Conditions that are not 
                 * related to temperature may be handled in the instruction element.
                 * 
                 * @param temperatureQualifier
                 *     Qualifies the interval of temperature
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder temperatureQualifier(CodeableConcept temperatureQualifier) {
                    this.temperatureQualifier = temperatureQualifier;
                    return this;
                }

                /**
                 * The temperature interval for this set of handling instructions.
                 * 
                 * @param temperatureRange
                 *     Temperature range for these handling instructions
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder temperatureRange(Range temperatureRange) {
                    this.temperatureRange = temperatureRange;
                    return this;
                }

                /**
                 * The maximum time interval of preservation of the specimen with these conditions.
                 * 
                 * @param maxDuration
                 *     Maximum preservation time
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder maxDuration(Duration maxDuration) {
                    this.maxDuration = maxDuration;
                    return this;
                }

                /**
                 * Additional textual instructions for the preservation or transport of the specimen. For instance, 'Protect from light 
                 * exposure'.
                 * 
                 * @param instruction
                 *     Preservation instruction
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder instruction(Markdown instruction) {
                    this.instruction = instruction;
                    return this;
                }

                /**
                 * Build the {@link Handling}
                 * 
                 * @return
                 *     An immutable object of type {@link Handling}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Handling per the base specification
                 */
                @Override
                public Handling build() {
                    Handling handling = new Handling(this);
                    if (validating) {
                        validate(handling);
                    }
                    return handling;
                }

                protected void validate(Handling handling) {
                    super.validate(handling);
                    ValidationSupport.requireValueOrChildren(handling);
                }

                protected Builder from(Handling handling) {
                    super.from(handling);
                    temperatureQualifier = handling.temperatureQualifier;
                    temperatureRange = handling.temperatureRange;
                    maxDuration = handling.maxDuration;
                    instruction = handling.instruction;
                    return this;
                }
            }
        }
    }
}
