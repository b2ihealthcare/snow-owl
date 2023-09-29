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
import org.linuxforhealth.fhir.model.r5.type.Address;
import org.linuxforhealth.fhir.model.r5.type.Availability;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.Decimal;
import org.linuxforhealth.fhir.model.r5.type.ExtendedContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.VirtualServiceDetail;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.LocationMode;
import org.linuxforhealth.fhir.model.r5.type.code.LocationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Details and position information for a place where services are provided and resources and participants may be stored, 
 * found, contained, or accommodated.
 * 
 * <p>Maturity level: FMM5 (Trial Use)
 */
@Maturity(
    level = 5,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "location-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/v2-0116",
    expression = "operationalStatus.exists() implies (operationalStatus.memberOf('http://terminology.hl7.org/ValueSet/v2-0116', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/Location",
    generated = true
)
@Constraint(
    id = "location-1",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/v3-ServiceDeliveryLocationRoleType",
    expression = "type.exists() implies (type.all(memberOf('http://terminology.hl7.org/ValueSet/v3-ServiceDeliveryLocationRoleType', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/Location",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Location extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "LocationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Indicates whether the location is still in use.",
        valueSet = "http://hl7.org/fhir/ValueSet/location-status|5.0.0"
    )
    private final LocationStatus status;
    @Summary
    @Binding(
        bindingName = "OperationalStatus",
        strength = BindingStrength.Value.PREFERRED,
        description = "The operational status if the location (where typically a bed/room).",
        valueSet = "http://terminology.hl7.org/ValueSet/v2-0116"
    )
    private final Coding operationalStatus;
    @Summary
    private final String name;
    private final List<String> alias;
    @Summary
    private final Markdown description;
    @Summary
    @Binding(
        bindingName = "LocationMode",
        strength = BindingStrength.Value.REQUIRED,
        description = "Indicates whether a resource instance represents a specific location or a class of locations.",
        valueSet = "http://hl7.org/fhir/ValueSet/location-mode|5.0.0"
    )
    private final LocationMode mode;
    @Summary
    @Binding(
        bindingName = "LocationType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Indicates the type of function performed at the location.",
        valueSet = "http://terminology.hl7.org/ValueSet/v3-ServiceDeliveryLocationRoleType"
    )
    private final List<CodeableConcept> type;
    private final List<ExtendedContactDetail> contact;
    private final Address address;
    @Summary
    @Binding(
        bindingName = "LocationForm",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Physical form of the location.",
        valueSet = "http://hl7.org/fhir/ValueSet/location-form"
    )
    private final CodeableConcept form;
    private final Position position;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference managingOrganization;
    @ReferenceTarget({ "Location" })
    private final Reference partOf;
    @Binding(
        bindingName = "LocationCharacteristic",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A custom attribute that could be provided at a service (e.g. Wheelchair accessibiliy).",
        valueSet = "http://hl7.org/fhir/ValueSet/location-characteristic"
    )
    private final List<CodeableConcept> characteristic;
    private final List<Availability> hoursOfOperation;
    private final List<VirtualServiceDetail> virtualService;
    @ReferenceTarget({ "Endpoint" })
    private final List<Reference> endpoint;

    private Location(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        operationalStatus = builder.operationalStatus;
        name = builder.name;
        alias = Collections.unmodifiableList(builder.alias);
        description = builder.description;
        mode = builder.mode;
        type = Collections.unmodifiableList(builder.type);
        contact = Collections.unmodifiableList(builder.contact);
        address = builder.address;
        form = builder.form;
        position = builder.position;
        managingOrganization = builder.managingOrganization;
        partOf = builder.partOf;
        characteristic = Collections.unmodifiableList(builder.characteristic);
        hoursOfOperation = Collections.unmodifiableList(builder.hoursOfOperation);
        virtualService = Collections.unmodifiableList(builder.virtualService);
        endpoint = Collections.unmodifiableList(builder.endpoint);
    }

    /**
     * Unique code or number identifying the location to its users.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The status property covers the general availability of the resource, not the current value which may be covered by the 
     * operationStatus, or by a schedule/slots if they are configured for the location.
     * 
     * @return
     *     An immutable object of type {@link LocationStatus} that may be null.
     */
    public LocationStatus getStatus() {
        return status;
    }

    /**
     * The operational status covers operation values most relevant to beds (but can also apply to rooms/units/chairs/etc. 
     * such as an isolation unit/dialysis chair). This typically covers concepts such as contamination, housekeeping, and 
     * other activities like maintenance.
     * 
     * @return
     *     An immutable object of type {@link Coding} that may be null.
     */
    public Coding getOperationalStatus() {
        return operationalStatus;
    }

    /**
     * Name of the location as used by humans. Does not need to be unique.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A list of alternate names that the location is known as, or was known as, in the past.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link String} that may be empty.
     */
    public List<String> getAlias() {
        return alias;
    }

    /**
     * Description of the Location, which helps in finding or referencing the place.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * Indicates whether a resource instance represents a specific location or a class of locations.
     * 
     * @return
     *     An immutable object of type {@link LocationMode} that may be null.
     */
    public LocationMode getMode() {
        return mode;
    }

    /**
     * Indicates the type of function performed at the location.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * The contact details of communication devices available at the location. This can include addresses, phone numbers, fax 
     * numbers, mobile numbers, email addresses and web sites.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ExtendedContactDetail} that may be empty.
     */
    public List<ExtendedContactDetail> getContact() {
        return contact;
    }

    /**
     * Physical location.
     * 
     * @return
     *     An immutable object of type {@link Address} that may be null.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Physical form of the location, e.g. building, room, vehicle, road, virtual.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getForm() {
        return form;
    }

    /**
     * The absolute geographic location of the Location, expressed using the WGS84 datum (This is the same co-ordinate system 
     * used in KML).
     * 
     * @return
     *     An immutable object of type {@link Position} that may be null.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * The organization responsible for the provisioning and upkeep of the location.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getManagingOrganization() {
        return managingOrganization;
    }

    /**
     * Another Location of which this Location is physically a part of.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPartOf() {
        return partOf;
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
     * What days/times during a week is this location usually open, and any exceptions where the location is not available.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Availability} that may be empty.
     */
    public List<Availability> getHoursOfOperation() {
        return hoursOfOperation;
    }

    /**
     * Connection details of a virtual service (e.g. shared conference call facility with dedicated number/details).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link VirtualServiceDetail} that may be empty.
     */
    public List<VirtualServiceDetail> getVirtualService() {
        return virtualService;
    }

    /**
     * Technical endpoints providing access to services operated for the location.
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
            (status != null) || 
            (operationalStatus != null) || 
            (name != null) || 
            !alias.isEmpty() || 
            (description != null) || 
            (mode != null) || 
            !type.isEmpty() || 
            !contact.isEmpty() || 
            (address != null) || 
            (form != null) || 
            (position != null) || 
            (managingOrganization != null) || 
            (partOf != null) || 
            !characteristic.isEmpty() || 
            !hoursOfOperation.isEmpty() || 
            !virtualService.isEmpty() || 
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
                accept(status, "status", visitor);
                accept(operationalStatus, "operationalStatus", visitor);
                accept(name, "name", visitor);
                accept(alias, "alias", visitor, String.class);
                accept(description, "description", visitor);
                accept(mode, "mode", visitor);
                accept(type, "type", visitor, CodeableConcept.class);
                accept(contact, "contact", visitor, ExtendedContactDetail.class);
                accept(address, "address", visitor);
                accept(form, "form", visitor);
                accept(position, "position", visitor);
                accept(managingOrganization, "managingOrganization", visitor);
                accept(partOf, "partOf", visitor);
                accept(characteristic, "characteristic", visitor, CodeableConcept.class);
                accept(hoursOfOperation, "hoursOfOperation", visitor, Availability.class);
                accept(virtualService, "virtualService", visitor, VirtualServiceDetail.class);
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
        Location other = (Location) obj;
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
            Objects.equals(operationalStatus, other.operationalStatus) && 
            Objects.equals(name, other.name) && 
            Objects.equals(alias, other.alias) && 
            Objects.equals(description, other.description) && 
            Objects.equals(mode, other.mode) && 
            Objects.equals(type, other.type) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(address, other.address) && 
            Objects.equals(form, other.form) && 
            Objects.equals(position, other.position) && 
            Objects.equals(managingOrganization, other.managingOrganization) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(characteristic, other.characteristic) && 
            Objects.equals(hoursOfOperation, other.hoursOfOperation) && 
            Objects.equals(virtualService, other.virtualService) && 
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
                status, 
                operationalStatus, 
                name, 
                alias, 
                description, 
                mode, 
                type, 
                contact, 
                address, 
                form, 
                position, 
                managingOrganization, 
                partOf, 
                characteristic, 
                hoursOfOperation, 
                virtualService, 
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
        private LocationStatus status;
        private Coding operationalStatus;
        private String name;
        private List<String> alias = new ArrayList<>();
        private Markdown description;
        private LocationMode mode;
        private List<CodeableConcept> type = new ArrayList<>();
        private List<ExtendedContactDetail> contact = new ArrayList<>();
        private Address address;
        private CodeableConcept form;
        private Position position;
        private Reference managingOrganization;
        private Reference partOf;
        private List<CodeableConcept> characteristic = new ArrayList<>();
        private List<Availability> hoursOfOperation = new ArrayList<>();
        private List<VirtualServiceDetail> virtualService = new ArrayList<>();
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
         * Unique code or number identifying the location to its users.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Unique code or number identifying the location to its users
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
         * Unique code or number identifying the location to its users.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Unique code or number identifying the location to its users
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
         * The status property covers the general availability of the resource, not the current value which may be covered by the 
         * operationStatus, or by a schedule/slots if they are configured for the location.
         * 
         * @param status
         *     active | suspended | inactive
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(LocationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The operational status covers operation values most relevant to beds (but can also apply to rooms/units/chairs/etc. 
         * such as an isolation unit/dialysis chair). This typically covers concepts such as contamination, housekeeping, and 
         * other activities like maintenance.
         * 
         * @param operationalStatus
         *     The operational status of the location (typically only for a bed/room)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder operationalStatus(Coding operationalStatus) {
            this.operationalStatus = operationalStatus;
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Name of the location as used by humans
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
         * Name of the location as used by humans. Does not need to be unique.
         * 
         * @param name
         *     Name of the location as used by humans
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Convenience method for setting {@code alias}.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param alias
         *     A list of alternate names that the location is known as, or was known as, in the past
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #alias(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder alias(java.lang.String... alias) {
            for (java.lang.String value : alias) {
                this.alias.add((value == null) ? null : String.of(value));
            }
            return this;
        }

        /**
         * A list of alternate names that the location is known as, or was known as, in the past.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param alias
         *     A list of alternate names that the location is known as, or was known as, in the past
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder alias(String... alias) {
            for (String value : alias) {
                this.alias.add(value);
            }
            return this;
        }

        /**
         * A list of alternate names that the location is known as, or was known as, in the past.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param alias
         *     A list of alternate names that the location is known as, or was known as, in the past
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder alias(Collection<String> alias) {
            this.alias = new ArrayList<>(alias);
            return this;
        }

        /**
         * Description of the Location, which helps in finding or referencing the place.
         * 
         * @param description
         *     Additional details about the location that could be displayed as further information to identify the location beyond 
         *     its name
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * Indicates whether a resource instance represents a specific location or a class of locations.
         * 
         * @param mode
         *     instance | kind
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder mode(LocationMode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Indicates the type of function performed at the location.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Type of function performed
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
         * Indicates the type of function performed at the location.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     Type of function performed
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
         * The contact details of communication devices available at the location. This can include addresses, phone numbers, fax 
         * numbers, mobile numbers, email addresses and web sites.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Official contact details for the location
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
         * The contact details of communication devices available at the location. This can include addresses, phone numbers, fax 
         * numbers, mobile numbers, email addresses and web sites.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Official contact details for the location
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
         * Physical location.
         * 
         * @param address
         *     Physical location
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        /**
         * Physical form of the location, e.g. building, room, vehicle, road, virtual.
         * 
         * @param form
         *     Physical form of the location
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder form(CodeableConcept form) {
            this.form = form;
            return this;
        }

        /**
         * The absolute geographic location of the Location, expressed using the WGS84 datum (This is the same co-ordinate system 
         * used in KML).
         * 
         * @param position
         *     The absolute geographic location
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        /**
         * The organization responsible for the provisioning and upkeep of the location.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param managingOrganization
         *     Organization responsible for provisioning and upkeep
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder managingOrganization(Reference managingOrganization) {
            this.managingOrganization = managingOrganization;
            return this;
        }

        /**
         * Another Location of which this Location is physically a part of.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param partOf
         *     Another Location this one is physically a part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder partOf(Reference partOf) {
            this.partOf = partOf;
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
         * What days/times during a week is this location usually open, and any exceptions where the location is not available.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param hoursOfOperation
         *     What days/times during a week is this location usually open (including exceptions)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder hoursOfOperation(Availability... hoursOfOperation) {
            for (Availability value : hoursOfOperation) {
                this.hoursOfOperation.add(value);
            }
            return this;
        }

        /**
         * What days/times during a week is this location usually open, and any exceptions where the location is not available.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param hoursOfOperation
         *     What days/times during a week is this location usually open (including exceptions)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder hoursOfOperation(Collection<Availability> hoursOfOperation) {
            this.hoursOfOperation = new ArrayList<>(hoursOfOperation);
            return this;
        }

        /**
         * Connection details of a virtual service (e.g. shared conference call facility with dedicated number/details).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param virtualService
         *     Connection details of a virtual service (e.g. conference call)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder virtualService(VirtualServiceDetail... virtualService) {
            for (VirtualServiceDetail value : virtualService) {
                this.virtualService.add(value);
            }
            return this;
        }

        /**
         * Connection details of a virtual service (e.g. shared conference call facility with dedicated number/details).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param virtualService
         *     Connection details of a virtual service (e.g. conference call)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder virtualService(Collection<VirtualServiceDetail> virtualService) {
            this.virtualService = new ArrayList<>(virtualService);
            return this;
        }

        /**
         * Technical endpoints providing access to services operated for the location.
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
         *     Technical endpoints providing access to services operated for the location
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
         * Technical endpoints providing access to services operated for the location.
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
         *     Technical endpoints providing access to services operated for the location
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
         * Build the {@link Location}
         * 
         * @return
         *     An immutable object of type {@link Location}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Location per the base specification
         */
        @Override
        public Location build() {
            Location location = new Location(this);
            if (validating) {
                validate(location);
            }
            return location;
        }

        protected void validate(Location location) {
            super.validate(location);
            ValidationSupport.checkList(location.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(location.alias, "alias", String.class);
            ValidationSupport.checkList(location.type, "type", CodeableConcept.class);
            ValidationSupport.checkList(location.contact, "contact", ExtendedContactDetail.class);
            ValidationSupport.checkList(location.characteristic, "characteristic", CodeableConcept.class);
            ValidationSupport.checkList(location.hoursOfOperation, "hoursOfOperation", Availability.class);
            ValidationSupport.checkList(location.virtualService, "virtualService", VirtualServiceDetail.class);
            ValidationSupport.checkList(location.endpoint, "endpoint", Reference.class);
            ValidationSupport.checkReferenceType(location.managingOrganization, "managingOrganization", "Organization");
            ValidationSupport.checkReferenceType(location.partOf, "partOf", "Location");
            ValidationSupport.checkReferenceType(location.endpoint, "endpoint", "Endpoint");
        }

        protected Builder from(Location location) {
            super.from(location);
            identifier.addAll(location.identifier);
            status = location.status;
            operationalStatus = location.operationalStatus;
            name = location.name;
            alias.addAll(location.alias);
            description = location.description;
            mode = location.mode;
            type.addAll(location.type);
            contact.addAll(location.contact);
            address = location.address;
            form = location.form;
            position = location.position;
            managingOrganization = location.managingOrganization;
            partOf = location.partOf;
            characteristic.addAll(location.characteristic);
            hoursOfOperation.addAll(location.hoursOfOperation);
            virtualService.addAll(location.virtualService);
            endpoint.addAll(location.endpoint);
            return this;
        }
    }

    /**
     * The absolute geographic location of the Location, expressed using the WGS84 datum (This is the same co-ordinate system 
     * used in KML).
     */
    public static class Position extends BackboneElement {
        @Required
        private final Decimal longitude;
        @Required
        private final Decimal latitude;
        private final Decimal altitude;

        private Position(Builder builder) {
            super(builder);
            longitude = builder.longitude;
            latitude = builder.latitude;
            altitude = builder.altitude;
        }

        /**
         * Longitude. The value domain and the interpretation are the same as for the text of the longitude element in KML (see 
         * notes on Location main page).
         * 
         * @return
         *     An immutable object of type {@link Decimal} that is non-null.
         */
        public Decimal getLongitude() {
            return longitude;
        }

        /**
         * Latitude. The value domain and the interpretation are the same as for the text of the latitude element in KML (see 
         * notes on Location main page).
         * 
         * @return
         *     An immutable object of type {@link Decimal} that is non-null.
         */
        public Decimal getLatitude() {
            return latitude;
        }

        /**
         * Altitude. The value domain and the interpretation are the same as for the text of the altitude element in KML (see 
         * notes on Location main page).
         * 
         * @return
         *     An immutable object of type {@link Decimal} that may be null.
         */
        public Decimal getAltitude() {
            return altitude;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (longitude != null) || 
                (latitude != null) || 
                (altitude != null);
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
                    accept(longitude, "longitude", visitor);
                    accept(latitude, "latitude", visitor);
                    accept(altitude, "altitude", visitor);
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
            Position other = (Position) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(longitude, other.longitude) && 
                Objects.equals(latitude, other.latitude) && 
                Objects.equals(altitude, other.altitude);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    longitude, 
                    latitude, 
                    altitude);
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
            private Decimal longitude;
            private Decimal latitude;
            private Decimal altitude;

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
             * Longitude. The value domain and the interpretation are the same as for the text of the longitude element in KML (see 
             * notes on Location main page).
             * 
             * <p>This element is required.
             * 
             * @param longitude
             *     Longitude with WGS84 datum
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder longitude(Decimal longitude) {
                this.longitude = longitude;
                return this;
            }

            /**
             * Latitude. The value domain and the interpretation are the same as for the text of the latitude element in KML (see 
             * notes on Location main page).
             * 
             * <p>This element is required.
             * 
             * @param latitude
             *     Latitude with WGS84 datum
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder latitude(Decimal latitude) {
                this.latitude = latitude;
                return this;
            }

            /**
             * Altitude. The value domain and the interpretation are the same as for the text of the altitude element in KML (see 
             * notes on Location main page).
             * 
             * @param altitude
             *     Altitude with WGS84 datum
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder altitude(Decimal altitude) {
                this.altitude = altitude;
                return this;
            }

            /**
             * Build the {@link Position}
             * 
             * <p>Required elements:
             * <ul>
             * <li>longitude</li>
             * <li>latitude</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Position}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Position per the base specification
             */
            @Override
            public Position build() {
                Position position = new Position(this);
                if (validating) {
                    validate(position);
                }
                return position;
            }

            protected void validate(Position position) {
                super.validate(position);
                ValidationSupport.requireNonNull(position.longitude, "longitude");
                ValidationSupport.requireNonNull(position.latitude, "latitude");
                ValidationSupport.requireValueOrChildren(position);
            }

            protected Builder from(Position position) {
                super.from(position);
                longitude = position.longitude;
                latitude = position.latitude;
                altitude = position.altitude;
                return this;
            }
        }
    }
}
