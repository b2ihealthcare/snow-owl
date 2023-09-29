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
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.EpisodeOfCareStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * An association between a patient and an organization / healthcare provider(s) during which time encounters may occur. 
 * The managing organization assumes a level of responsibility for the patient during this time.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "episodeOfCare-0",
    level = "Warning",
    location = "diagnosis.use",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-diagnosis-use",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/encounter-diagnosis-use', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/EpisodeOfCare",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class EpisodeOfCare extends DomainResource {
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "EpisodeOfCareStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the episode of care.",
        valueSet = "http://hl7.org/fhir/ValueSet/episode-of-care-status|5.0.0"
    )
    @Required
    private final EpisodeOfCareStatus status;
    private final List<StatusHistory> statusHistory;
    @Summary
    @Binding(
        bindingName = "EpisodeOfCareType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The type of the episode of care.",
        valueSet = "http://hl7.org/fhir/ValueSet/episodeofcare-type"
    )
    private final List<CodeableConcept> type;
    @Summary
    private final List<Reason> reason;
    @Summary
    private final List<Diagnosis> diagnosis;
    @Summary
    @ReferenceTarget({ "Patient" })
    @Required
    private final Reference patient;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference managingOrganization;
    @Summary
    private final Period period;
    @ReferenceTarget({ "ServiceRequest" })
    private final List<Reference> referralRequest;
    @ReferenceTarget({ "Practitioner", "PractitionerRole" })
    private final Reference careManager;
    @ReferenceTarget({ "CareTeam" })
    private final List<Reference> careTeam;
    @ReferenceTarget({ "Account" })
    private final List<Reference> account;

    private EpisodeOfCare(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        statusHistory = Collections.unmodifiableList(builder.statusHistory);
        type = Collections.unmodifiableList(builder.type);
        reason = Collections.unmodifiableList(builder.reason);
        diagnosis = Collections.unmodifiableList(builder.diagnosis);
        patient = builder.patient;
        managingOrganization = builder.managingOrganization;
        period = builder.period;
        referralRequest = Collections.unmodifiableList(builder.referralRequest);
        careManager = builder.careManager;
        careTeam = Collections.unmodifiableList(builder.careTeam);
        account = Collections.unmodifiableList(builder.account);
    }

    /**
     * The EpisodeOfCare may be known by different identifiers for different contexts of use, such as when an external agency 
     * is tracking the Episode for funding purposes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * planned | waitlist | active | onhold | finished | cancelled.
     * 
     * @return
     *     An immutable object of type {@link EpisodeOfCareStatus} that is non-null.
     */
    public EpisodeOfCareStatus getStatus() {
        return status;
    }

    /**
     * The history of statuses that the EpisodeOfCare has been through (without requiring processing the history of the 
     * resource).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link StatusHistory} that may be empty.
     */
    public List<StatusHistory> getStatusHistory() {
        return statusHistory;
    }

    /**
     * A classification of the type of episode of care; e.g. specialist referral, disease management, type of funded care.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * The list of medical reasons that are expected to be addressed during the episode of care.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reason} that may be empty.
     */
    public List<Reason> getReason() {
        return reason;
    }

    /**
     * The list of medical conditions that were addressed during the episode of care.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Diagnosis} that may be empty.
     */
    public List<Diagnosis> getDiagnosis() {
        return diagnosis;
    }

    /**
     * The patient who is the focus of this episode of care.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getPatient() {
        return patient;
    }

    /**
     * The organization that has assumed the specific responsibilities for care coordination, care delivery, or other 
     * services for the specified duration.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getManagingOrganization() {
        return managingOrganization;
    }

    /**
     * The interval during which the managing organization assumes the defined responsibility.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getReferralRequest() {
        return referralRequest;
    }

    /**
     * The practitioner that is the care manager/care coordinator for this patient.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getCareManager() {
        return careManager;
    }

    /**
     * The list of practitioners that may be facilitating this episode of care for specific purposes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getCareTeam() {
        return careTeam;
    }

    /**
     * The set of accounts that may be used for billing for this EpisodeOfCare.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getAccount() {
        return account;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !statusHistory.isEmpty() || 
            !type.isEmpty() || 
            !reason.isEmpty() || 
            !diagnosis.isEmpty() || 
            (patient != null) || 
            (managingOrganization != null) || 
            (period != null) || 
            !referralRequest.isEmpty() || 
            (careManager != null) || 
            !careTeam.isEmpty() || 
            !account.isEmpty();
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
                accept(status, "status", visitor);
                accept(statusHistory, "statusHistory", visitor, StatusHistory.class);
                accept(type, "type", visitor, CodeableConcept.class);
                accept(reason, "reason", visitor, Reason.class);
                accept(diagnosis, "diagnosis", visitor, Diagnosis.class);
                accept(patient, "patient", visitor);
                accept(managingOrganization, "managingOrganization", visitor);
                accept(period, "period", visitor);
                accept(referralRequest, "referralRequest", visitor, Reference.class);
                accept(careManager, "careManager", visitor);
                accept(careTeam, "careTeam", visitor, Reference.class);
                accept(account, "account", visitor, Reference.class);
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
        EpisodeOfCare other = (EpisodeOfCare) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusHistory, other.statusHistory) && 
            Objects.equals(type, other.type) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(diagnosis, other.diagnosis) && 
            Objects.equals(patient, other.patient) && 
            Objects.equals(managingOrganization, other.managingOrganization) && 
            Objects.equals(period, other.period) && 
            Objects.equals(referralRequest, other.referralRequest) && 
            Objects.equals(careManager, other.careManager) && 
            Objects.equals(careTeam, other.careTeam) && 
            Objects.equals(account, other.account);
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
                status, 
                statusHistory, 
                type, 
                reason, 
                diagnosis, 
                patient, 
                managingOrganization, 
                period, 
                referralRequest, 
                careManager, 
                careTeam, 
                account);
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
        private EpisodeOfCareStatus status;
        private List<StatusHistory> statusHistory = new ArrayList<>();
        private List<CodeableConcept> type = new ArrayList<>();
        private List<Reason> reason = new ArrayList<>();
        private List<Diagnosis> diagnosis = new ArrayList<>();
        private Reference patient;
        private Reference managingOrganization;
        private Period period;
        private List<Reference> referralRequest = new ArrayList<>();
        private Reference careManager;
        private List<Reference> careTeam = new ArrayList<>();
        private List<Reference> account = new ArrayList<>();

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
         * The EpisodeOfCare may be known by different identifiers for different contexts of use, such as when an external agency 
         * is tracking the Episode for funding purposes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier(s) relevant for this EpisodeOfCare
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
         * The EpisodeOfCare may be known by different identifiers for different contexts of use, such as when an external agency 
         * is tracking the Episode for funding purposes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier(s) relevant for this EpisodeOfCare
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
         * planned | waitlist | active | onhold | finished | cancelled.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     planned | waitlist | active | onhold | finished | cancelled | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(EpisodeOfCareStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The history of statuses that the EpisodeOfCare has been through (without requiring processing the history of the 
         * resource).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusHistory
         *     Past list of status codes (the current status may be included to cover the start date of the status)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusHistory(StatusHistory... statusHistory) {
            for (StatusHistory value : statusHistory) {
                this.statusHistory.add(value);
            }
            return this;
        }

        /**
         * The history of statuses that the EpisodeOfCare has been through (without requiring processing the history of the 
         * resource).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusHistory
         *     Past list of status codes (the current status may be included to cover the start date of the status)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder statusHistory(Collection<StatusHistory> statusHistory) {
            this.statusHistory = new ArrayList<>(statusHistory);
            return this;
        }

        /**
         * A classification of the type of episode of care; e.g. specialist referral, disease management, type of funded care.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Type/class - e.g. specialist referral, disease management
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept... type) {
            for (CodeableConcept value : type) {
                this.type.add(value);
            }
            return this;
        }

        /**
         * A classification of the type of episode of care; e.g. specialist referral, disease management, type of funded care.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Type/class - e.g. specialist referral, disease management
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder type(Collection<CodeableConcept> type) {
            this.type = new ArrayList<>(type);
            return this;
        }

        /**
         * The list of medical reasons that are expected to be addressed during the episode of care.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The list of medical reasons that are expected to be addressed during the episode of care
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reason(Reason... reason) {
            for (Reason value : reason) {
                this.reason.add(value);
            }
            return this;
        }

        /**
         * The list of medical reasons that are expected to be addressed during the episode of care.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The list of medical reasons that are expected to be addressed during the episode of care
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reason(Collection<Reason> reason) {
            this.reason = new ArrayList<>(reason);
            return this;
        }

        /**
         * The list of medical conditions that were addressed during the episode of care.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param diagnosis
         *     The list of medical conditions that were addressed during the episode of care
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder diagnosis(Diagnosis... diagnosis) {
            for (Diagnosis value : diagnosis) {
                this.diagnosis.add(value);
            }
            return this;
        }

        /**
         * The list of medical conditions that were addressed during the episode of care.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param diagnosis
         *     The list of medical conditions that were addressed during the episode of care
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder diagnosis(Collection<Diagnosis> diagnosis) {
            this.diagnosis = new ArrayList<>(diagnosis);
            return this;
        }

        /**
         * The patient who is the focus of this episode of care.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param patient
         *     The patient who is the focus of this episode of care
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patient(Reference patient) {
            this.patient = patient;
            return this;
        }

        /**
         * The organization that has assumed the specific responsibilities for care coordination, care delivery, or other 
         * services for the specified duration.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param managingOrganization
         *     Organization that assumes responsibility for care coordination
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder managingOrganization(Reference managingOrganization) {
            this.managingOrganization = managingOrganization;
            return this;
        }

        /**
         * The interval during which the managing organization assumes the defined responsibility.
         * 
         * @param period
         *     Interval during responsibility is assumed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param referralRequest
         *     Originating Referral Request(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder referralRequest(Reference... referralRequest) {
            for (Reference value : referralRequest) {
                this.referralRequest.add(value);
            }
            return this;
        }

        /**
         * Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param referralRequest
         *     Originating Referral Request(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder referralRequest(Collection<Reference> referralRequest) {
            this.referralRequest = new ArrayList<>(referralRequest);
            return this;
        }

        /**
         * The practitioner that is the care manager/care coordinator for this patient.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param careManager
         *     Care manager/care coordinator for the patient
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder careManager(Reference careManager) {
            this.careManager = careManager;
            return this;
        }

        /**
         * The list of practitioners that may be facilitating this episode of care for specific purposes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param careTeam
         *     Other practitioners facilitating this episode of care
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder careTeam(Reference... careTeam) {
            for (Reference value : careTeam) {
                this.careTeam.add(value);
            }
            return this;
        }

        /**
         * The list of practitioners that may be facilitating this episode of care for specific purposes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param careTeam
         *     Other practitioners facilitating this episode of care
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder careTeam(Collection<Reference> careTeam) {
            this.careTeam = new ArrayList<>(careTeam);
            return this;
        }

        /**
         * The set of accounts that may be used for billing for this EpisodeOfCare.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Account}</li>
         * </ul>
         * 
         * @param account
         *     The set of accounts that may be used for billing for this EpisodeOfCare
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder account(Reference... account) {
            for (Reference value : account) {
                this.account.add(value);
            }
            return this;
        }

        /**
         * The set of accounts that may be used for billing for this EpisodeOfCare.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Account}</li>
         * </ul>
         * 
         * @param account
         *     The set of accounts that may be used for billing for this EpisodeOfCare
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder account(Collection<Reference> account) {
            this.account = new ArrayList<>(account);
            return this;
        }

        /**
         * Build the {@link EpisodeOfCare}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>patient</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link EpisodeOfCare}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid EpisodeOfCare per the base specification
         */
        @Override
        public EpisodeOfCare build() {
            EpisodeOfCare episodeOfCare = new EpisodeOfCare(this);
            if (validating) {
                validate(episodeOfCare);
            }
            return episodeOfCare;
        }

        protected void validate(EpisodeOfCare episodeOfCare) {
            super.validate(episodeOfCare);
            ValidationSupport.checkList(episodeOfCare.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(episodeOfCare.status, "status");
            ValidationSupport.checkList(episodeOfCare.statusHistory, "statusHistory", StatusHistory.class);
            ValidationSupport.checkList(episodeOfCare.type, "type", CodeableConcept.class);
            ValidationSupport.checkList(episodeOfCare.reason, "reason", Reason.class);
            ValidationSupport.checkList(episodeOfCare.diagnosis, "diagnosis", Diagnosis.class);
            ValidationSupport.requireNonNull(episodeOfCare.patient, "patient");
            ValidationSupport.checkList(episodeOfCare.referralRequest, "referralRequest", Reference.class);
            ValidationSupport.checkList(episodeOfCare.careTeam, "careTeam", Reference.class);
            ValidationSupport.checkList(episodeOfCare.account, "account", Reference.class);
            ValidationSupport.checkReferenceType(episodeOfCare.patient, "patient", "Patient");
            ValidationSupport.checkReferenceType(episodeOfCare.managingOrganization, "managingOrganization", "Organization");
            ValidationSupport.checkReferenceType(episodeOfCare.referralRequest, "referralRequest", "ServiceRequest");
            ValidationSupport.checkReferenceType(episodeOfCare.careManager, "careManager", "Practitioner", "PractitionerRole");
            ValidationSupport.checkReferenceType(episodeOfCare.careTeam, "careTeam", "CareTeam");
            ValidationSupport.checkReferenceType(episodeOfCare.account, "account", "Account");
        }

        protected Builder from(EpisodeOfCare episodeOfCare) {
            super.from(episodeOfCare);
            identifier.addAll(episodeOfCare.identifier);
            status = episodeOfCare.status;
            statusHistory.addAll(episodeOfCare.statusHistory);
            type.addAll(episodeOfCare.type);
            reason.addAll(episodeOfCare.reason);
            diagnosis.addAll(episodeOfCare.diagnosis);
            patient = episodeOfCare.patient;
            managingOrganization = episodeOfCare.managingOrganization;
            period = episodeOfCare.period;
            referralRequest.addAll(episodeOfCare.referralRequest);
            careManager = episodeOfCare.careManager;
            careTeam.addAll(episodeOfCare.careTeam);
            account.addAll(episodeOfCare.account);
            return this;
        }
    }

    /**
     * The history of statuses that the EpisodeOfCare has been through (without requiring processing the history of the 
     * resource).
     */
    public static class StatusHistory extends BackboneElement {
        @Binding(
            bindingName = "EpisodeOfCareStatus",
            strength = BindingStrength.Value.REQUIRED,
            description = "The status of the episode of care.",
            valueSet = "http://hl7.org/fhir/ValueSet/episode-of-care-status|5.0.0"
        )
        @Required
        private final EpisodeOfCareStatus status;
        @Required
        private final Period period;

        private StatusHistory(Builder builder) {
            super(builder);
            status = builder.status;
            period = builder.period;
        }

        /**
         * planned | waitlist | active | onhold | finished | cancelled.
         * 
         * @return
         *     An immutable object of type {@link EpisodeOfCareStatus} that is non-null.
         */
        public EpisodeOfCareStatus getStatus() {
            return status;
        }

        /**
         * The period during this EpisodeOfCare that the specific status applied.
         * 
         * @return
         *     An immutable object of type {@link Period} that is non-null.
         */
        public Period getPeriod() {
            return period;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (status != null) || 
                (period != null);
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
                    accept(status, "status", visitor);
                    accept(period, "period", visitor);
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
            StatusHistory other = (StatusHistory) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(status, other.status) && 
                Objects.equals(period, other.period);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    status, 
                    period);
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
            private EpisodeOfCareStatus status;
            private Period period;

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
             * planned | waitlist | active | onhold | finished | cancelled.
             * 
             * <p>This element is required.
             * 
             * @param status
             *     planned | waitlist | active | onhold | finished | cancelled | entered-in-error
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder status(EpisodeOfCareStatus status) {
                this.status = status;
                return this;
            }

            /**
             * The period during this EpisodeOfCare that the specific status applied.
             * 
             * <p>This element is required.
             * 
             * @param period
             *     Duration the EpisodeOfCare was in the specified status
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link StatusHistory}
             * 
             * <p>Required elements:
             * <ul>
             * <li>status</li>
             * <li>period</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link StatusHistory}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid StatusHistory per the base specification
             */
            @Override
            public StatusHistory build() {
                StatusHistory statusHistory = new StatusHistory(this);
                if (validating) {
                    validate(statusHistory);
                }
                return statusHistory;
            }

            protected void validate(StatusHistory statusHistory) {
                super.validate(statusHistory);
                ValidationSupport.requireNonNull(statusHistory.status, "status");
                ValidationSupport.requireNonNull(statusHistory.period, "period");
                ValidationSupport.requireValueOrChildren(statusHistory);
            }

            protected Builder from(StatusHistory statusHistory) {
                super.from(statusHistory);
                status = statusHistory.status;
                period = statusHistory.period;
                return this;
            }
        }
    }

    /**
     * The list of medical reasons that are expected to be addressed during the episode of care.
     */
    public static class Reason extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "reason-use",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-reason-use"
        )
        private final CodeableConcept use;
        @Summary
        @Binding(
            bindingName = "reason-code",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-reason"
        )
        private final List<CodeableReference> value;

        private Reason(Builder builder) {
            super(builder);
            use = builder.use;
            value = Collections.unmodifiableList(builder.value);
        }

        /**
         * What the reason value should be used as e.g. Chief Complaint, Health Concern, Health Maintenance (including screening).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getUse() {
            return use;
        }

        /**
         * The medical reason that is expected to be addressed during the episode of care, expressed as a text, code or a 
         * reference to another resource.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (use != null) || 
                !value.isEmpty();
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
                    accept(use, "use", visitor);
                    accept(value, "value", visitor, CodeableReference.class);
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
            Reason other = (Reason) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(use, other.use) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    use, 
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
            private CodeableConcept use;
            private List<CodeableReference> value = new ArrayList<>();

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
             * What the reason value should be used as e.g. Chief Complaint, Health Concern, Health Maintenance (including screening).
             * 
             * @param use
             *     What the reason value should be used for/as
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder use(CodeableConcept use) {
                this.use = use;
                return this;
            }

            /**
             * The medical reason that is expected to be addressed during the episode of care, expressed as a text, code or a 
             * reference to another resource.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param value
             *     Medical reason to be addressed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(CodeableReference... value) {
                for (CodeableReference _value : value) {
                    this.value.add(_value);
                }
                return this;
            }

            /**
             * The medical reason that is expected to be addressed during the episode of care, expressed as a text, code or a 
             * reference to another resource.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param value
             *     Medical reason to be addressed
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder value(Collection<CodeableReference> value) {
                this.value = new ArrayList<>(value);
                return this;
            }

            /**
             * Build the {@link Reason}
             * 
             * @return
             *     An immutable object of type {@link Reason}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Reason per the base specification
             */
            @Override
            public Reason build() {
                Reason reason = new Reason(this);
                if (validating) {
                    validate(reason);
                }
                return reason;
            }

            protected void validate(Reason reason) {
                super.validate(reason);
                ValidationSupport.checkList(reason.value, "value", CodeableReference.class);
                ValidationSupport.requireValueOrChildren(reason);
            }

            protected Builder from(Reason reason) {
                super.from(reason);
                use = reason.use;
                value.addAll(reason.value);
                return this;
            }
        }
    }

    /**
     * The list of medical conditions that were addressed during the episode of care.
     */
    public static class Diagnosis extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "condition-code",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
        )
        private final List<CodeableReference> condition;
        @Summary
        @Binding(
            bindingName = "DiagnosisUse",
            strength = BindingStrength.Value.PREFERRED,
            description = "The type of diagnosis this condition represents.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-diagnosis-use"
        )
        private final CodeableConcept use;

        private Diagnosis(Builder builder) {
            super(builder);
            condition = Collections.unmodifiableList(builder.condition);
            use = builder.use;
        }

        /**
         * The medical condition that was addressed during the episode of care, expressed as a text, code or a reference to 
         * another resource.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getCondition() {
            return condition;
        }

        /**
         * Role that this diagnosis has within the episode of care (e.g. admission, billing, discharge …).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getUse() {
            return use;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !condition.isEmpty() || 
                (use != null);
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
                    accept(condition, "condition", visitor, CodeableReference.class);
                    accept(use, "use", visitor);
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
            Diagnosis other = (Diagnosis) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(condition, other.condition) && 
                Objects.equals(use, other.use);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    condition, 
                    use);
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
            private List<CodeableReference> condition = new ArrayList<>();
            private CodeableConcept use;

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
             * The medical condition that was addressed during the episode of care, expressed as a text, code or a reference to 
             * another resource.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param condition
             *     The medical condition that was addressed during the episode of care
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder condition(CodeableReference... condition) {
                for (CodeableReference value : condition) {
                    this.condition.add(value);
                }
                return this;
            }

            /**
             * The medical condition that was addressed during the episode of care, expressed as a text, code or a reference to 
             * another resource.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param condition
             *     The medical condition that was addressed during the episode of care
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder condition(Collection<CodeableReference> condition) {
                this.condition = new ArrayList<>(condition);
                return this;
            }

            /**
             * Role that this diagnosis has within the episode of care (e.g. admission, billing, discharge …).
             * 
             * @param use
             *     Role that this diagnosis has within the episode of care (e.g. admission, billing, discharge …)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder use(CodeableConcept use) {
                this.use = use;
                return this;
            }

            /**
             * Build the {@link Diagnosis}
             * 
             * @return
             *     An immutable object of type {@link Diagnosis}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Diagnosis per the base specification
             */
            @Override
            public Diagnosis build() {
                Diagnosis diagnosis = new Diagnosis(this);
                if (validating) {
                    validate(diagnosis);
                }
                return diagnosis;
            }

            protected void validate(Diagnosis diagnosis) {
                super.validate(diagnosis);
                ValidationSupport.checkList(diagnosis.condition, "condition", CodeableReference.class);
                ValidationSupport.requireValueOrChildren(diagnosis);
            }

            protected Builder from(Diagnosis diagnosis) {
                super.from(diagnosis);
                condition.addAll(diagnosis.condition);
                use = diagnosis.use;
                return this;
            }
        }
    }
}
