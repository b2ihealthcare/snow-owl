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
import org.linuxforhealth.fhir.model.r5.type.Availability;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.ExtendedContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A specific set of Roles/Locations/specialties/services that a practitioner may perform at an organization for a period 
 * of time.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "practitionerRole-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/c80-practice-codes",
    expression = "specialty.exists() implies (specialty.all(memberOf('http://hl7.org/fhir/ValueSet/c80-practice-codes', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/PractitionerRole",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class PractitionerRole extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final Boolean active;
    @Summary
    private final Period period;
    @Summary
    @ReferenceTarget({ "Practitioner" })
    private final Reference practitioner;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference organization;
    @Summary
    @Binding(
        bindingName = "PractitionerRole",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The role a person plays representing an organization.",
        valueSet = "http://hl7.org/fhir/ValueSet/practitioner-role"
    )
    private final List<CodeableConcept> code;
    @Summary
    @Binding(
        bindingName = "PractitionerSpecialty",
        strength = BindingStrength.Value.PREFERRED,
        description = "Specific specialty associated with the agency.",
        valueSet = "http://hl7.org/fhir/ValueSet/c80-practice-codes"
    )
    private final List<CodeableConcept> specialty;
    @Summary
    @ReferenceTarget({ "Location" })
    private final List<Reference> location;
    @ReferenceTarget({ "HealthcareService" })
    private final List<Reference> healthcareService;
    private final List<ExtendedContactDetail> contact;
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
    private final List<Availability> availability;
    @ReferenceTarget({ "Endpoint" })
    private final List<Reference> endpoint;

    private PractitionerRole(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        active = builder.active;
        period = builder.period;
        practitioner = builder.practitioner;
        organization = builder.organization;
        code = Collections.unmodifiableList(builder.code);
        specialty = Collections.unmodifiableList(builder.specialty);
        location = Collections.unmodifiableList(builder.location);
        healthcareService = Collections.unmodifiableList(builder.healthcareService);
        contact = Collections.unmodifiableList(builder.contact);
        characteristic = Collections.unmodifiableList(builder.characteristic);
        communication = Collections.unmodifiableList(builder.communication);
        availability = Collections.unmodifiableList(builder.availability);
        endpoint = Collections.unmodifiableList(builder.endpoint);
    }

    /**
     * Business Identifiers that are specific to a role/location.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     *  Whether this practitioner role record is in active use. Some systems may use this property to mark non-active 
     * practitioners, such as those that are not currently employed.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * The period during which the person is authorized to act as a practitioner in these role(s) for the organization.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Practitioner that is able to provide the defined services for the organization.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPractitioner() {
        return practitioner;
    }

    /**
     * The organization where the Practitioner performs the roles associated.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getOrganization() {
        return organization;
    }

    /**
     * Roles which this practitioner is authorized to perform for the organization.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCode() {
        return code;
    }

    /**
     * The specialty of a practitioner that describes the functional role they are practicing at a given organization or 
     * location.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSpecialty() {
        return specialty;
    }

    /**
     * The location(s) at which this practitioner provides care.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getLocation() {
        return location;
    }

    /**
     * The list of healthcare services that this worker provides for this role's Organization/Location(s).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getHealthcareService() {
        return healthcareService;
    }

    /**
     * The contact details of communication devices available relevant to the specific PractitionerRole. This can include 
     * addresses, phone numbers, fax numbers, mobile numbers, email addresses and web sites.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ExtendedContactDetail} that may be empty.
     */
    public List<ExtendedContactDetail> getContact() {
        return contact;
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
     * A language the practitioner can use in patient communication. The practitioner may know several languages (listed in 
     * practitioner.communication), however these are the languages that could be advertised in a directory for a patient to 
     * search.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCommunication() {
        return communication;
    }

    /**
     * A collection of times the practitioner is available or performing this role at the location and/or healthcareservice.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Availability} that may be empty.
     */
    public List<Availability> getAvailability() {
        return availability;
    }

    /**
     *  Technical endpoints providing access to services operated for the practitioner with this role. Commonly used for 
     * locating scheduling services, or identifying where to send referrals electronically.
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
            (period != null) || 
            (practitioner != null) || 
            (organization != null) || 
            !code.isEmpty() || 
            !specialty.isEmpty() || 
            !location.isEmpty() || 
            !healthcareService.isEmpty() || 
            !contact.isEmpty() || 
            !characteristic.isEmpty() || 
            !communication.isEmpty() || 
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
                accept(period, "period", visitor);
                accept(practitioner, "practitioner", visitor);
                accept(organization, "organization", visitor);
                accept(code, "code", visitor, CodeableConcept.class);
                accept(specialty, "specialty", visitor, CodeableConcept.class);
                accept(location, "location", visitor, Reference.class);
                accept(healthcareService, "healthcareService", visitor, Reference.class);
                accept(contact, "contact", visitor, ExtendedContactDetail.class);
                accept(characteristic, "characteristic", visitor, CodeableConcept.class);
                accept(communication, "communication", visitor, CodeableConcept.class);
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
        PractitionerRole other = (PractitionerRole) obj;
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
            Objects.equals(period, other.period) && 
            Objects.equals(practitioner, other.practitioner) && 
            Objects.equals(organization, other.organization) && 
            Objects.equals(code, other.code) && 
            Objects.equals(specialty, other.specialty) && 
            Objects.equals(location, other.location) && 
            Objects.equals(healthcareService, other.healthcareService) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(characteristic, other.characteristic) && 
            Objects.equals(communication, other.communication) && 
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
                period, 
                practitioner, 
                organization, 
                code, 
                specialty, 
                location, 
                healthcareService, 
                contact, 
                characteristic, 
                communication, 
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
        private Period period;
        private Reference practitioner;
        private Reference organization;
        private List<CodeableConcept> code = new ArrayList<>();
        private List<CodeableConcept> specialty = new ArrayList<>();
        private List<Reference> location = new ArrayList<>();
        private List<Reference> healthcareService = new ArrayList<>();
        private List<ExtendedContactDetail> contact = new ArrayList<>();
        private List<CodeableConcept> characteristic = new ArrayList<>();
        private List<CodeableConcept> communication = new ArrayList<>();
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
         * Business Identifiers that are specific to a role/location.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers for a role/location
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
         * Business Identifiers that are specific to a role/location.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers for a role/location
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
         *     Whether this practitioner role record is in active use
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
         *  Whether this practitioner role record is in active use. Some systems may use this property to mark non-active 
         * practitioners, such as those that are not currently employed.
         * 
         * @param active
         *     Whether this practitioner role record is in active use
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        /**
         * The period during which the person is authorized to act as a practitioner in these role(s) for the organization.
         * 
         * @param period
         *     The period during which the practitioner is authorized to perform in these role(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * Practitioner that is able to provide the defined services for the organization.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * </ul>
         * 
         * @param practitioner
         *     Practitioner that provides services for the organization
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder practitioner(Reference practitioner) {
            this.practitioner = practitioner;
            return this;
        }

        /**
         * The organization where the Practitioner performs the roles associated.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param organization
         *     Organization where the roles are available
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder organization(Reference organization) {
            this.organization = organization;
            return this;
        }

        /**
         * Roles which this practitioner is authorized to perform for the organization.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param code
         *     Roles which this practitioner may perform
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept... code) {
            for (CodeableConcept value : code) {
                this.code.add(value);
            }
            return this;
        }

        /**
         * Roles which this practitioner is authorized to perform for the organization.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param code
         *     Roles which this practitioner may perform
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder code(Collection<CodeableConcept> code) {
            this.code = new ArrayList<>(code);
            return this;
        }

        /**
         * The specialty of a practitioner that describes the functional role they are practicing at a given organization or 
         * location.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialty
         *     Specific specialty of the practitioner
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
         * The specialty of a practitioner that describes the functional role they are practicing at a given organization or 
         * location.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specialty
         *     Specific specialty of the practitioner
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
         * The location(s) at which this practitioner provides care.
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
         *     Location(s) where the practitioner provides care
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
         * The location(s) at which this practitioner provides care.
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
         *     Location(s) where the practitioner provides care
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
         * The list of healthcare services that this worker provides for this role's Organization/Location(s).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param healthcareService
         *     Healthcare services provided for this role's Organization/Location(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder healthcareService(Reference... healthcareService) {
            for (Reference value : healthcareService) {
                this.healthcareService.add(value);
            }
            return this;
        }

        /**
         * The list of healthcare services that this worker provides for this role's Organization/Location(s).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param healthcareService
         *     Healthcare services provided for this role's Organization/Location(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder healthcareService(Collection<Reference> healthcareService) {
            this.healthcareService = new ArrayList<>(healthcareService);
            return this;
        }

        /**
         * The contact details of communication devices available relevant to the specific PractitionerRole. This can include 
         * addresses, phone numbers, fax numbers, mobile numbers, email addresses and web sites.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Official contact details relating to this PractitionerRole
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
         * The contact details of communication devices available relevant to the specific PractitionerRole. This can include 
         * addresses, phone numbers, fax numbers, mobile numbers, email addresses and web sites.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Official contact details relating to this PractitionerRole
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
         * A language the practitioner can use in patient communication. The practitioner may know several languages (listed in 
         * practitioner.communication), however these are the languages that could be advertised in a directory for a patient to 
         * search.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param communication
         *     A language the practitioner (in this role) can use in patient communication
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
         * A language the practitioner can use in patient communication. The practitioner may know several languages (listed in 
         * practitioner.communication), however these are the languages that could be advertised in a directory for a patient to 
         * search.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param communication
         *     A language the practitioner (in this role) can use in patient communication
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
         * A collection of times the practitioner is available or performing this role at the location and/or healthcareservice.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param availability
         *     Times the Practitioner is available at this location and/or healthcare service (including exceptions)
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
         * A collection of times the practitioner is available or performing this role at the location and/or healthcareservice.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param availability
         *     Times the Practitioner is available at this location and/or healthcare service (including exceptions)
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
         *  Technical endpoints providing access to services operated for the practitioner with this role. Commonly used for 
         * locating scheduling services, or identifying where to send referrals electronically.
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
         *     Endpoints for interacting with the practitioner in this role
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
         *  Technical endpoints providing access to services operated for the practitioner with this role. Commonly used for 
         * locating scheduling services, or identifying where to send referrals electronically.
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
         *     Endpoints for interacting with the practitioner in this role
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
         * Build the {@link PractitionerRole}
         * 
         * @return
         *     An immutable object of type {@link PractitionerRole}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid PractitionerRole per the base specification
         */
        @Override
        public PractitionerRole build() {
            PractitionerRole practitionerRole = new PractitionerRole(this);
            if (validating) {
                validate(practitionerRole);
            }
            return practitionerRole;
        }

        protected void validate(PractitionerRole practitionerRole) {
            super.validate(practitionerRole);
            ValidationSupport.checkList(practitionerRole.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(practitionerRole.code, "code", CodeableConcept.class);
            ValidationSupport.checkList(practitionerRole.specialty, "specialty", CodeableConcept.class);
            ValidationSupport.checkList(practitionerRole.location, "location", Reference.class);
            ValidationSupport.checkList(practitionerRole.healthcareService, "healthcareService", Reference.class);
            ValidationSupport.checkList(practitionerRole.contact, "contact", ExtendedContactDetail.class);
            ValidationSupport.checkList(practitionerRole.characteristic, "characteristic", CodeableConcept.class);
            ValidationSupport.checkList(practitionerRole.communication, "communication", CodeableConcept.class);
            ValidationSupport.checkList(practitionerRole.availability, "availability", Availability.class);
            ValidationSupport.checkList(practitionerRole.endpoint, "endpoint", Reference.class);
            ValidationSupport.checkValueSetBinding(practitionerRole.communication, "communication", "http://hl7.org/fhir/ValueSet/all-languages", "urn:ietf:bcp:47");
            ValidationSupport.checkReferenceType(practitionerRole.practitioner, "practitioner", "Practitioner");
            ValidationSupport.checkReferenceType(practitionerRole.organization, "organization", "Organization");
            ValidationSupport.checkReferenceType(practitionerRole.location, "location", "Location");
            ValidationSupport.checkReferenceType(practitionerRole.healthcareService, "healthcareService", "HealthcareService");
            ValidationSupport.checkReferenceType(practitionerRole.endpoint, "endpoint", "Endpoint");
        }

        protected Builder from(PractitionerRole practitionerRole) {
            super.from(practitionerRole);
            identifier.addAll(practitionerRole.identifier);
            active = practitionerRole.active;
            period = practitionerRole.period;
            practitioner = practitionerRole.practitioner;
            organization = practitionerRole.organization;
            code.addAll(practitionerRole.code);
            specialty.addAll(practitionerRole.specialty);
            location.addAll(practitionerRole.location);
            healthcareService.addAll(practitionerRole.healthcareService);
            contact.addAll(practitionerRole.contact);
            characteristic.addAll(practitionerRole.characteristic);
            communication.addAll(practitionerRole.communication);
            availability.addAll(practitionerRole.availability);
            endpoint.addAll(practitionerRole.endpoint);
            return this;
        }
    }
}
