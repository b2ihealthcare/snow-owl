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
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.Availability;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.ExtendedContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The details of a healthcare service available at a location.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "healthcareService-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/c80-practice-codes",
    expression = "specialty.exists() implies (specialty.all(memberOf('http://hl7.org/fhir/ValueSet/c80-practice-codes', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/HealthcareService",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class HealthcareService extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final Boolean active;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference providedBy;
    @ReferenceTarget({ "HealthcareService" })
    private final List<Reference> offeredIn;
    @Summary
    @Binding(
        bindingName = "service-category",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A category of the service(s) that could be provided.",
        valueSet = "http://hl7.org/fhir/ValueSet/service-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "service-type",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Additional details about where the content was created (e.g. clinical specialty).",
        valueSet = "http://hl7.org/fhir/ValueSet/service-type"
    )
    private final List<CodeableConcept> type;
    @Summary
    @Binding(
        bindingName = "service-specialty",
        strength = BindingStrength.Value.PREFERRED,
        description = "A specialty that a healthcare service may provide.",
        valueSet = "http://hl7.org/fhir/ValueSet/c80-practice-codes"
    )
    private final List<CodeableConcept> specialty;
    @Summary
    @ReferenceTarget({ "Location" })
    private final List<Reference> location;
    @Summary
    private final String name;
    @Summary
    private final Markdown comment;
    private final Markdown extraDetails;
    @Summary
    private final Attachment photo;
    private final List<ExtendedContactDetail> contact;
    @ReferenceTarget({ "Location" })
    private final List<Reference> coverageArea;
    @Binding(
        bindingName = "ServiceProvisionConditions",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The code(s) that detail the conditions under which the healthcare service is available/offered.",
        valueSet = "http://hl7.org/fhir/ValueSet/service-provision-conditions"
    )
    private final List<CodeableConcept> serviceProvisionCode;
    private final List<Eligibility> eligibility;
    @Binding(
        bindingName = "Program",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Government or local programs that this service applies to.",
        valueSet = "http://hl7.org/fhir/ValueSet/program"
    )
    private final List<CodeableConcept> program;
    @Binding(
        bindingName = "ServiceCharacteristic",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A custom attribute that could be provided at a service (e.g. Wheelchair accessibility).",
        valueSet = "http://hl7.org/fhir/ValueSet/service-mode"
    )
    private final List<CodeableConcept> characteristic;
    @Binding(
        bindingName = "Language",
        strength = BindingStrength.Value.REQUIRED,
        description = "IETF language tag for a human language",
        valueSet = "http://hl7.org/fhir/ValueSet/all-languages|5.0.0"
    )
    private final List<CodeableConcept> communication;
    @Binding(
        bindingName = "ReferralMethod",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The methods of referral can be used when referring to a specific HealthcareService resource.",
        valueSet = "http://hl7.org/fhir/ValueSet/service-referral-method"
    )
    private final List<CodeableConcept> referralMethod;
    private final Boolean appointmentRequired;
    private final List<Availability> availability;
    @ReferenceTarget({ "Endpoint" })
    private final List<Reference> endpoint;

    private HealthcareService(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        active = builder.active;
        providedBy = builder.providedBy;
        offeredIn = Collections.unmodifiableList(builder.offeredIn);
        category = Collections.unmodifiableList(builder.category);
        type = Collections.unmodifiableList(builder.type);
        specialty = Collections.unmodifiableList(builder.specialty);
        location = Collections.unmodifiableList(builder.location);
        name = builder.name;
        comment = builder.comment;
        extraDetails = builder.extraDetails;
        photo = builder.photo;
        contact = Collections.unmodifiableList(builder.contact);
        coverageArea = Collections.unmodifiableList(builder.coverageArea);
        serviceProvisionCode = Collections.unmodifiableList(builder.serviceProvisionCode);
        eligibility = Collections.unmodifiableList(builder.eligibility);
        program = Collections.unmodifiableList(builder.program);
        characteristic = Collections.unmodifiableList(builder.characteristic);
        communication = Collections.unmodifiableList(builder.communication);
        referralMethod = Collections.unmodifiableList(builder.referralMethod);
        appointmentRequired = builder.appointmentRequired;
        availability = Collections.unmodifiableList(builder.availability);
        endpoint = Collections.unmodifiableList(builder.endpoint);
    }

    /**
     * External identifiers for this item.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * This flag is used to mark the record to not be used. This is not used when a center is closed for maintenance, or for 
     * holidays, the notAvailable period is to be used for this.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * The organization that provides this healthcare service.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getProvidedBy() {
        return providedBy;
    }

    /**
     * When the HealthcareService is representing a specific, schedulable service, the availableIn property can refer to a 
     * generic service.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getOfferedIn() {
        return offeredIn;
    }

    /**
     * Identifies the broad category of service being performed or delivered.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * The specific type of service that may be delivered or performed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * Collection of specialties handled by the Healthcare service. This is more of a medical term.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSpecialty() {
        return specialty;
    }

    /**
     * The location(s) where this healthcare service may be provided.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getLocation() {
        return location;
    }

    /**
     * Further description of the service as it would be presented to a consumer while searching.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * Any additional description of the service and/or any specific issues not covered by the other attributes, which can be 
     * displayed as further detail under the serviceName.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getComment() {
        return comment;
    }

    /**
     * Extra details about the service that can't be placed in the other fields.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getExtraDetails() {
        return extraDetails;
    }

    /**
     * If there is a photo/symbol associated with this HealthcareService, it may be included here to facilitate quick 
     * identification of the service in a list.
     * 
     * @return
     *     An immutable object of type {@link Attachment} that may be null.
     */
    public Attachment getPhoto() {
        return photo;
    }

    /**
     * The contact details of communication devices available relevant to the specific HealthcareService. This can include 
     * addresses, phone numbers, fax numbers, mobile numbers, email addresses and web sites.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ExtendedContactDetail} that may be empty.
     */
    public List<ExtendedContactDetail> getContact() {
        return contact;
    }

    /**
     * The location(s) that this service is available to (not where the service is provided).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getCoverageArea() {
        return coverageArea;
    }

    /**
     * The code(s) that detail the conditions under which the healthcare service is available/offered.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getServiceProvisionCode() {
        return serviceProvisionCode;
    }

    /**
     * Does this service have specific eligibility requirements that need to be met in order to use the service?
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Eligibility} that may be empty.
     */
    public List<Eligibility> getEligibility() {
        return eligibility;
    }

    /**
     * Programs that this service is applicable to.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getProgram() {
        return program;
    }

    /**
     * Collection of characteristics (attributes).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCharacteristic() {
        return characteristic;
    }

    /**
     * Some services are specifically made available in multiple languages, this property permits a directory to declare the 
     * languages this is offered in. Typically this is only provided where a service operates in communities with mixed 
     * languages used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCommunication() {
        return communication;
    }

    /**
     * Ways that the service accepts referrals, if this is not provided then it is implied that no referral is required.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getReferralMethod() {
        return referralMethod;
    }

    /**
     * Indicates whether or not a prospective consumer will require an appointment for a particular service at a site to be 
     * provided by the Organization. Indicates if an appointment is required for access to this service.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getAppointmentRequired() {
        return appointmentRequired;
    }

    /**
     * A collection of times that the healthcare service is available.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Availability} that may be empty.
     */
    public List<Availability> getAvailability() {
        return availability;
    }

    /**
     * Technical endpoints providing access to services operated for the specific healthcare services defined at this 
     * resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEndpoint() {
        return endpoint;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (active != null) || 
            (providedBy != null) || 
            !offeredIn.isEmpty() || 
            !category.isEmpty() || 
            !type.isEmpty() || 
            !specialty.isEmpty() || 
            !location.isEmpty() || 
            (name != null) || 
            (comment != null) || 
            (extraDetails != null) || 
            (photo != null) || 
            !contact.isEmpty() || 
            !coverageArea.isEmpty() || 
            !serviceProvisionCode.isEmpty() || 
            !eligibility.isEmpty() || 
            !program.isEmpty() || 
            !characteristic.isEmpty() || 
            !communication.isEmpty() || 
            !referralMethod.isEmpty() || 
            (appointmentRequired != null) || 
            !availability.isEmpty() || 
            !endpoint.isEmpty();
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
                accept(active, "active", visitor);
                accept(providedBy, "providedBy", visitor);
                accept(offeredIn, "offeredIn", visitor, Reference.class);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(type, "type", visitor, CodeableConcept.class);
                accept(specialty, "specialty", visitor, CodeableConcept.class);
                accept(location, "location", visitor, Reference.class);
                accept(name, "name", visitor);
                accept(comment, "comment", visitor);
                accept(extraDetails, "extraDetails", visitor);
                accept(photo, "photo", visitor);
                accept(contact, "contact", visitor, ExtendedContactDetail.class);
                accept(coverageArea, "coverageArea", visitor, Reference.class);
                accept(serviceProvisionCode, "serviceProvisionCode", visitor, CodeableConcept.class);
                accept(eligibility, "eligibility", visitor, Eligibility.class);
                accept(program, "program", visitor, CodeableConcept.class);
                accept(characteristic, "characteristic", visitor, CodeableConcept.class);
                accept(communication, "communication", visitor, CodeableConcept.class);
                accept(referralMethod, "referralMethod", visitor, CodeableConcept.class);
                accept(appointmentRequired, "appointmentRequired", visitor);
                accept(availability, "availability", visitor, Availability.class);
                accept(endpoint, "endpoint", visitor, Reference.class);
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
        HealthcareService other = (HealthcareService) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(active, other.active) && 
            Objects.equals(providedBy, other.providedBy) && 
            Objects.equals(offeredIn, other.offeredIn) && 
            Objects.equals(category, other.category) && 
            Objects.equals(type, other.type) && 
            Objects.equals(specialty, other.specialty) && 
            Objects.equals(location, other.location) && 
            Objects.equals(name, other.name) && 
            Objects.equals(comment, other.comment) && 
            Objects.equals(extraDetails, other.extraDetails) && 
            Objects.equals(photo, other.photo) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(coverageArea, other.coverageArea) && 
            Objects.equals(serviceProvisionCode, other.serviceProvisionCode) && 
            Objects.equals(eligibility, other.eligibility) && 
            Objects.equals(program, other.program) && 
            Objects.equals(characteristic, other.characteristic) && 
            Objects.equals(communication, other.communication) && 
            Objects.equals(referralMethod, other.referralMethod) && 
            Objects.equals(appointmentRequired, other.appointmentRequired) && 
            Objects.equals(availability, other.availability) && 
            Objects.equals(endpoint, other.endpoint);
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
                active, 
                providedBy, 
                offeredIn, 
                category, 
                type, 
                specialty, 
                location, 
                name, 
                comment, 
                extraDetails, 
                photo, 
                contact, 
                coverageArea, 
                serviceProvisionCode, 
                eligibility, 
                program, 
                characteristic, 
                communication, 
                referralMethod, 
                appointmentRequired, 
                availability, 
                endpoint);
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
        private Boolean active;
        private Reference providedBy;
        private List<Reference> offeredIn = new ArrayList<>();
        private List<CodeableConcept> category = new ArrayList<>();
        private List<CodeableConcept> type = new ArrayList<>();
        private List<CodeableConcept> specialty = new ArrayList<>();
        private List<Reference> location = new ArrayList<>();
        private String name;
        private Markdown comment;
        private Markdown extraDetails;
        private Attachment photo;
        private List<ExtendedContactDetail> contact = new ArrayList<>();
        private List<Reference> coverageArea = new ArrayList<>();
        private List<CodeableConcept> serviceProvisionCode = new ArrayList<>();
        private List<Eligibility> eligibility = new ArrayList<>();
        private List<CodeableConcept> program = new ArrayList<>();
        private List<CodeableConcept> characteristic = new ArrayList<>();
        private List<CodeableConcept> communication = new ArrayList<>();
        private List<CodeableConcept> referralMethod = new ArrayList<>();
        private Boolean appointmentRequired;
        private List<Availability> availability = new ArrayList<>();
        private List<Reference> endpoint = new ArrayList<>();

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
         * External identifiers for this item.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifiers for this item
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
         * External identifiers for this item.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifiers for this item
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
         * Convenience method for setting {@code active}.
         * 
         * @param active
         *     Whether this HealthcareService record is in active use
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #active(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder active(java.lang.Boolean active) {
            this.active = (active == null) ? null : Boolean.of(active);
            return this;
        }

        /**
         * This flag is used to mark the record to not be used. This is not used when a center is closed for maintenance, or for 
         * holidays, the notAvailable period is to be used for this.
         * 
         * @param active
         *     Whether this HealthcareService record is in active use
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        /**
         * The organization that provides this healthcare service.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param providedBy
         *     Organization that provides this service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder providedBy(Reference providedBy) {
            this.providedBy = providedBy;
            return this;
        }

        /**
         * When the HealthcareService is representing a specific, schedulable service, the availableIn property can refer to a 
         * generic service.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param offeredIn
         *     The service within which this service is offered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder offeredIn(Reference... offeredIn) {
            for (Reference value : offeredIn) {
                this.offeredIn.add(value);
            }
            return this;
        }

        /**
         * When the HealthcareService is representing a specific, schedulable service, the availableIn property can refer to a 
         * generic service.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param offeredIn
         *     The service within which this service is offered
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder offeredIn(Collection<Reference> offeredIn) {
            this.offeredIn = new ArrayList<>(offeredIn);
            return this;
        }

        /**
         * Identifies the broad category of service being performed or delivered.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Broad category of service being performed or delivered
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
         * Identifies the broad category of service being performed or delivered.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Broad category of service being performed or delivered
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
         * The specific type of service that may be delivered or performed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Type of service that may be delivered or performed
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
         * The specific type of service that may be delivered or performed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Type of service that may be delivered or performed
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
         * Collection of specialties handled by the Healthcare service. This is more of a medical term.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialty
         *     Specialties handled by the HealthcareService
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specialty(CodeableConcept... specialty) {
            for (CodeableConcept value : specialty) {
                this.specialty.add(value);
            }
            return this;
        }

        /**
         * Collection of specialties handled by the Healthcare service. This is more of a medical term.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialty
         *     Specialties handled by the HealthcareService
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder specialty(Collection<CodeableConcept> specialty) {
            this.specialty = new ArrayList<>(specialty);
            return this;
        }

        /**
         * The location(s) where this healthcare service may be provided.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Location(s) where service may be provided
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference... location) {
            for (Reference value : location) {
                this.location.add(value);
            }
            return this;
        }

        /**
         * The location(s) where this healthcare service may be provided.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Location(s) where service may be provided
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder location(Collection<Reference> location) {
            this.location = new ArrayList<>(location);
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Description of service as presented to a consumer while searching
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
         * Further description of the service as it would be presented to a consumer while searching.
         * 
         * @param name
         *     Description of service as presented to a consumer while searching
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Any additional description of the service and/or any specific issues not covered by the other attributes, which can be 
         * displayed as further detail under the serviceName.
         * 
         * @param comment
         *     Additional description and/or any specific issues not covered elsewhere
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder comment(Markdown comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Extra details about the service that can't be placed in the other fields.
         * 
         * @param extraDetails
         *     Extra details about the service that can't be placed in the other fields
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder extraDetails(Markdown extraDetails) {
            this.extraDetails = extraDetails;
            return this;
        }

        /**
         * If there is a photo/symbol associated with this HealthcareService, it may be included here to facilitate quick 
         * identification of the service in a list.
         * 
         * @param photo
         *     Facilitates quick identification of the service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder photo(Attachment photo) {
            this.photo = photo;
            return this;
        }

        /**
         * The contact details of communication devices available relevant to the specific HealthcareService. This can include 
         * addresses, phone numbers, fax numbers, mobile numbers, email addresses and web sites.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Official contact details for the HealthcareService
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contact(ExtendedContactDetail... contact) {
            for (ExtendedContactDetail value : contact) {
                this.contact.add(value);
            }
            return this;
        }

        /**
         * The contact details of communication devices available relevant to the specific HealthcareService. This can include 
         * addresses, phone numbers, fax numbers, mobile numbers, email addresses and web sites.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Official contact details for the HealthcareService
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contact(Collection<ExtendedContactDetail> contact) {
            this.contact = new ArrayList<>(contact);
            return this;
        }

        /**
         * The location(s) that this service is available to (not where the service is provided).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param coverageArea
         *     Location(s) service is intended for/available to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder coverageArea(Reference... coverageArea) {
            for (Reference value : coverageArea) {
                this.coverageArea.add(value);
            }
            return this;
        }

        /**
         * The location(s) that this service is available to (not where the service is provided).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param coverageArea
         *     Location(s) service is intended for/available to
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder coverageArea(Collection<Reference> coverageArea) {
            this.coverageArea = new ArrayList<>(coverageArea);
            return this;
        }

        /**
         * The code(s) that detail the conditions under which the healthcare service is available/offered.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceProvisionCode
         *     Conditions under which service is available/offered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder serviceProvisionCode(CodeableConcept... serviceProvisionCode) {
            for (CodeableConcept value : serviceProvisionCode) {
                this.serviceProvisionCode.add(value);
            }
            return this;
        }

        /**
         * The code(s) that detail the conditions under which the healthcare service is available/offered.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param serviceProvisionCode
         *     Conditions under which service is available/offered
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder serviceProvisionCode(Collection<CodeableConcept> serviceProvisionCode) {
            this.serviceProvisionCode = new ArrayList<>(serviceProvisionCode);
            return this;
        }

        /**
         * Does this service have specific eligibility requirements that need to be met in order to use the service?
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param eligibility
         *     Specific eligibility requirements required to use the service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder eligibility(Eligibility... eligibility) {
            for (Eligibility value : eligibility) {
                this.eligibility.add(value);
            }
            return this;
        }

        /**
         * Does this service have specific eligibility requirements that need to be met in order to use the service?
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param eligibility
         *     Specific eligibility requirements required to use the service
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder eligibility(Collection<Eligibility> eligibility) {
            this.eligibility = new ArrayList<>(eligibility);
            return this;
        }

        /**
         * Programs that this service is applicable to.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param program
         *     Programs that this service is applicable to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder program(CodeableConcept... program) {
            for (CodeableConcept value : program) {
                this.program.add(value);
            }
            return this;
        }

        /**
         * Programs that this service is applicable to.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param program
         *     Programs that this service is applicable to
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder program(Collection<CodeableConcept> program) {
            this.program = new ArrayList<>(program);
            return this;
        }

        /**
         * Collection of characteristics (attributes).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param characteristic
         *     Collection of characteristics (attributes)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder characteristic(CodeableConcept... characteristic) {
            for (CodeableConcept value : characteristic) {
                this.characteristic.add(value);
            }
            return this;
        }

        /**
         * Collection of characteristics (attributes).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param characteristic
         *     Collection of characteristics (attributes)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder characteristic(Collection<CodeableConcept> characteristic) {
            this.characteristic = new ArrayList<>(characteristic);
            return this;
        }

        /**
         * Some services are specifically made available in multiple languages, this property permits a directory to declare the 
         * languages this is offered in. Typically this is only provided where a service operates in communities with mixed 
         * languages used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param communication
         *     The language that this service is offered in
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder communication(CodeableConcept... communication) {
            for (CodeableConcept value : communication) {
                this.communication.add(value);
            }
            return this;
        }

        /**
         * Some services are specifically made available in multiple languages, this property permits a directory to declare the 
         * languages this is offered in. Typically this is only provided where a service operates in communities with mixed 
         * languages used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param communication
         *     The language that this service is offered in
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder communication(Collection<CodeableConcept> communication) {
            this.communication = new ArrayList<>(communication);
            return this;
        }

        /**
         * Ways that the service accepts referrals, if this is not provided then it is implied that no referral is required.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param referralMethod
         *     Ways that the service accepts referrals
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder referralMethod(CodeableConcept... referralMethod) {
            for (CodeableConcept value : referralMethod) {
                this.referralMethod.add(value);
            }
            return this;
        }

        /**
         * Ways that the service accepts referrals, if this is not provided then it is implied that no referral is required.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param referralMethod
         *     Ways that the service accepts referrals
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder referralMethod(Collection<CodeableConcept> referralMethod) {
            this.referralMethod = new ArrayList<>(referralMethod);
            return this;
        }

        /**
         * Convenience method for setting {@code appointmentRequired}.
         * 
         * @param appointmentRequired
         *     If an appointment is required for access to this service
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #appointmentRequired(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder appointmentRequired(java.lang.Boolean appointmentRequired) {
            this.appointmentRequired = (appointmentRequired == null) ? null : Boolean.of(appointmentRequired);
            return this;
        }

        /**
         * Indicates whether or not a prospective consumer will require an appointment for a particular service at a site to be 
         * provided by the Organization. Indicates if an appointment is required for access to this service.
         * 
         * @param appointmentRequired
         *     If an appointment is required for access to this service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder appointmentRequired(Boolean appointmentRequired) {
            this.appointmentRequired = appointmentRequired;
            return this;
        }

        /**
         * A collection of times that the healthcare service is available.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param availability
         *     Times the healthcare service is available (including exceptions)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder availability(Availability... availability) {
            for (Availability value : availability) {
                this.availability.add(value);
            }
            return this;
        }

        /**
         * A collection of times that the healthcare service is available.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param availability
         *     Times the healthcare service is available (including exceptions)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder availability(Collection<Availability> availability) {
            this.availability = new ArrayList<>(availability);
            return this;
        }

        /**
         * Technical endpoints providing access to services operated for the specific healthcare services defined at this 
         * resource.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Endpoint}</li>
         * </ul>
         * 
         * @param endpoint
         *     Technical endpoints providing access to electronic services operated for the healthcare service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder endpoint(Reference... endpoint) {
            for (Reference value : endpoint) {
                this.endpoint.add(value);
            }
            return this;
        }

        /**
         * Technical endpoints providing access to services operated for the specific healthcare services defined at this 
         * resource.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Endpoint}</li>
         * </ul>
         * 
         * @param endpoint
         *     Technical endpoints providing access to electronic services operated for the healthcare service
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder endpoint(Collection<Reference> endpoint) {
            this.endpoint = new ArrayList<>(endpoint);
            return this;
        }

        /**
         * Build the {@link HealthcareService}
         * 
         * @return
         *     An immutable object of type {@link HealthcareService}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid HealthcareService per the base specification
         */
        @Override
        public HealthcareService build() {
            HealthcareService healthcareService = new HealthcareService(this);
            if (validating) {
                validate(healthcareService);
            }
            return healthcareService;
        }

        protected void validate(HealthcareService healthcareService) {
            super.validate(healthcareService);
            ValidationSupport.checkList(healthcareService.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(healthcareService.offeredIn, "offeredIn", Reference.class);
            ValidationSupport.checkList(healthcareService.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.type, "type", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.specialty, "specialty", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.location, "location", Reference.class);
            ValidationSupport.checkList(healthcareService.contact, "contact", ExtendedContactDetail.class);
            ValidationSupport.checkList(healthcareService.coverageArea, "coverageArea", Reference.class);
            ValidationSupport.checkList(healthcareService.serviceProvisionCode, "serviceProvisionCode", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.eligibility, "eligibility", Eligibility.class);
            ValidationSupport.checkList(healthcareService.program, "program", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.characteristic, "characteristic", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.communication, "communication", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.referralMethod, "referralMethod", CodeableConcept.class);
            ValidationSupport.checkList(healthcareService.availability, "availability", Availability.class);
            ValidationSupport.checkList(healthcareService.endpoint, "endpoint", Reference.class);
            ValidationSupport.checkValueSetBinding(healthcareService.communication, "communication", "http://hl7.org/fhir/ValueSet/all-languages", "urn:ietf:bcp:47");
            ValidationSupport.checkReferenceType(healthcareService.providedBy, "providedBy", "Organization");
            ValidationSupport.checkReferenceType(healthcareService.offeredIn, "offeredIn", "HealthcareService");
            ValidationSupport.checkReferenceType(healthcareService.location, "location", "Location");
            ValidationSupport.checkReferenceType(healthcareService.coverageArea, "coverageArea", "Location");
            ValidationSupport.checkReferenceType(healthcareService.endpoint, "endpoint", "Endpoint");
        }

        protected Builder from(HealthcareService healthcareService) {
            super.from(healthcareService);
            identifier.addAll(healthcareService.identifier);
            active = healthcareService.active;
            providedBy = healthcareService.providedBy;
            offeredIn.addAll(healthcareService.offeredIn);
            category.addAll(healthcareService.category);
            type.addAll(healthcareService.type);
            specialty.addAll(healthcareService.specialty);
            location.addAll(healthcareService.location);
            name = healthcareService.name;
            comment = healthcareService.comment;
            extraDetails = healthcareService.extraDetails;
            photo = healthcareService.photo;
            contact.addAll(healthcareService.contact);
            coverageArea.addAll(healthcareService.coverageArea);
            serviceProvisionCode.addAll(healthcareService.serviceProvisionCode);
            eligibility.addAll(healthcareService.eligibility);
            program.addAll(healthcareService.program);
            characteristic.addAll(healthcareService.characteristic);
            communication.addAll(healthcareService.communication);
            referralMethod.addAll(healthcareService.referralMethod);
            appointmentRequired = healthcareService.appointmentRequired;
            availability.addAll(healthcareService.availability);
            endpoint.addAll(healthcareService.endpoint);
            return this;
        }
    }

    /**
     * Does this service have specific eligibility requirements that need to be met in order to use the service?
     */
    public static class Eligibility extends BackboneElement {
        @Binding(
            bindingName = "ServiceEligibility",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Coded values underwhich a specific service is made available."
        )
        private final CodeableConcept code;
        private final Markdown comment;

        private Eligibility(Builder builder) {
            super(builder);
            code = builder.code;
            comment = builder.comment;
        }

        /**
         * Coded value for the eligibility.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * Describes the eligibility conditions for the service.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getComment() {
            return comment;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (comment != null);
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
                    accept(comment, "comment", visitor);
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
            Eligibility other = (Eligibility) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(comment, other.comment);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    comment);
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
            private Markdown comment;

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
             * Coded value for the eligibility.
             * 
             * @param code
             *     Coded value for the eligibility
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Describes the eligibility conditions for the service.
             * 
             * @param comment
             *     Describes the eligibility conditions for the service
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder comment(Markdown comment) {
                this.comment = comment;
                return this;
            }

            /**
             * Build the {@link Eligibility}
             * 
             * @return
             *     An immutable object of type {@link Eligibility}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Eligibility per the base specification
             */
            @Override
            public Eligibility build() {
                Eligibility eligibility = new Eligibility(this);
                if (validating) {
                    validate(eligibility);
                }
                return eligibility;
            }

            protected void validate(Eligibility eligibility) {
                super.validate(eligibility);
                ValidationSupport.requireValueOrChildren(eligibility);
            }

            protected Builder from(Eligibility eligibility) {
                super.from(eligibility);
                code = eligibility.code;
                comment = eligibility.comment;
                return this;
            }
        }
    }
}
