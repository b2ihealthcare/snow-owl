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
import org.linuxforhealth.fhir.model.r5.type.ContactPoint;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.Url;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.EndpointStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The technical details of an endpoint that can be used for electronic services, such as for web services providing XDS.
 * b, a REST endpoint for another FHIR server, or a s/Mime email address. This may include any security context 
 * information.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "endpoint-0",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/endpoint-environment",
    expression = "environmentType.exists() implies (environmentType.all(memberOf('http://hl7.org/fhir/ValueSet/endpoint-environment', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/Endpoint",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Endpoint extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "EndpointStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the endpoint.",
        valueSet = "http://hl7.org/fhir/ValueSet/endpoint-status|5.0.0"
    )
    @Required
    private final EndpointStatus status;
    @Summary
    @Binding(
        bindingName = "endpoint-contype",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/endpoint-connection-type"
    )
    @Required
    private final List<CodeableConcept> connectionType;
    @Summary
    private final String name;
    @Summary
    private final String description;
    @Summary
    @Binding(
        bindingName = "endpoint-environment-type",
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/endpoint-environment"
    )
    private final List<CodeableConcept> environmentType;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference managingOrganization;
    private final List<ContactPoint> contact;
    @Summary
    private final Period period;
    private final List<Payload> payload;
    @Summary
    @Required
    private final Url address;
    private final List<String> header;

    private Endpoint(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        connectionType = Collections.unmodifiableList(builder.connectionType);
        name = builder.name;
        description = builder.description;
        environmentType = Collections.unmodifiableList(builder.environmentType);
        managingOrganization = builder.managingOrganization;
        contact = Collections.unmodifiableList(builder.contact);
        period = builder.period;
        payload = Collections.unmodifiableList(builder.payload);
        address = builder.address;
        header = Collections.unmodifiableList(builder.header);
    }

    /**
     * Identifier for the organization that is used to identify the endpoint across multiple disparate systems.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The endpoint status represents the general expected availability of an endpoint.
     * 
     * @return
     *     An immutable object of type {@link EndpointStatus} that is non-null.
     */
    public EndpointStatus getStatus() {
        return status;
    }

    /**
     * A coded value that represents the technical details of the usage of this endpoint, such as what WSDLs should be used 
     * in what way. (e.g. XDS.b/DICOM/cds-hook).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that is non-empty.
     */
    public List<CodeableConcept> getConnectionType() {
        return connectionType;
    }

    /**
     * A friendly name that this endpoint can be referred to with.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * The description of the endpoint and what it is for (typically used as supplemental information in an endpoint 
     * directory describing its usage/purpose).
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The type of environment(s) exposed at this endpoint (dev, prod, test, etc.).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getEnvironmentType() {
        return environmentType;
    }

    /**
     * The organization that manages this endpoint (even if technically another organization is hosting this in the cloud, it 
     * is the organization associated with the data).
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getManagingOrganization() {
        return managingOrganization;
    }

    /**
     * Contact details for a human to contact about the endpoint. The primary use of this for system administrator 
     * troubleshooting.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactPoint} that may be empty.
     */
    public List<ContactPoint> getContact() {
        return contact;
    }

    /**
     * The interval during which the endpoint is expected to be operational.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * The set of payloads that are provided/available at this endpoint.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Payload} that may be empty.
     */
    public List<Payload> getPayload() {
        return payload;
    }

    /**
     * The uri that describes the actual end-point to connect to.
     * 
     * @return
     *     An immutable object of type {@link Url} that is non-null.
     */
    public Url getAddress() {
        return address;
    }

    /**
     * Additional headers / information to send as part of the notification.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link String} that may be empty.
     */
    public List<String> getHeader() {
        return header;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !connectionType.isEmpty() || 
            (name != null) || 
            (description != null) || 
            !environmentType.isEmpty() || 
            (managingOrganization != null) || 
            !contact.isEmpty() || 
            (period != null) || 
            !payload.isEmpty() || 
            (address != null) || 
            !header.isEmpty();
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
                accept(connectionType, "connectionType", visitor, CodeableConcept.class);
                accept(name, "name", visitor);
                accept(description, "description", visitor);
                accept(environmentType, "environmentType", visitor, CodeableConcept.class);
                accept(managingOrganization, "managingOrganization", visitor);
                accept(contact, "contact", visitor, ContactPoint.class);
                accept(period, "period", visitor);
                accept(payload, "payload", visitor, Payload.class);
                accept(address, "address", visitor);
                accept(header, "header", visitor, String.class);
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
        Endpoint other = (Endpoint) obj;
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
            Objects.equals(connectionType, other.connectionType) && 
            Objects.equals(name, other.name) && 
            Objects.equals(description, other.description) && 
            Objects.equals(environmentType, other.environmentType) && 
            Objects.equals(managingOrganization, other.managingOrganization) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(period, other.period) && 
            Objects.equals(payload, other.payload) && 
            Objects.equals(address, other.address) && 
            Objects.equals(header, other.header);
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
                connectionType, 
                name, 
                description, 
                environmentType, 
                managingOrganization, 
                contact, 
                period, 
                payload, 
                address, 
                header);
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
        private EndpointStatus status;
        private List<CodeableConcept> connectionType = new ArrayList<>();
        private String name;
        private String description;
        private List<CodeableConcept> environmentType = new ArrayList<>();
        private Reference managingOrganization;
        private List<ContactPoint> contact = new ArrayList<>();
        private Period period;
        private List<Payload> payload = new ArrayList<>();
        private Url address;
        private List<String> header = new ArrayList<>();

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
         * Identifier for the organization that is used to identify the endpoint across multiple disparate systems.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifies this endpoint across multiple systems
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
         * Identifier for the organization that is used to identify the endpoint across multiple disparate systems.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifies this endpoint across multiple systems
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
         * The endpoint status represents the general expected availability of an endpoint.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     active | suspended | error | off | entered-in-error | test
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(EndpointStatus status) {
            this.status = status;
            return this;
        }

        /**
         * A coded value that represents the technical details of the usage of this endpoint, such as what WSDLs should be used 
         * in what way. (e.g. XDS.b/DICOM/cds-hook).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param connectionType
         *     Protocol/Profile/Standard to be used with this endpoint connection
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder connectionType(CodeableConcept... connectionType) {
            for (CodeableConcept value : connectionType) {
                this.connectionType.add(value);
            }
            return this;
        }

        /**
         * A coded value that represents the technical details of the usage of this endpoint, such as what WSDLs should be used 
         * in what way. (e.g. XDS.b/DICOM/cds-hook).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param connectionType
         *     Protocol/Profile/Standard to be used with this endpoint connection
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder connectionType(Collection<CodeableConcept> connectionType) {
            this.connectionType = new ArrayList<>(connectionType);
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     A name that this endpoint can be identified by
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
         * A friendly name that this endpoint can be referred to with.
         * 
         * @param name
         *     A name that this endpoint can be identified by
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Convenience method for setting {@code description}.
         * 
         * @param description
         *     Additional details about the endpoint that could be displayed as further information to identify the description 
         *     beyond its name
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #description(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder description(java.lang.String description) {
            this.description = (description == null) ? null : String.of(description);
            return this;
        }

        /**
         * The description of the endpoint and what it is for (typically used as supplemental information in an endpoint 
         * directory describing its usage/purpose).
         * 
         * @param description
         *     Additional details about the endpoint that could be displayed as further information to identify the description 
         *     beyond its name
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * The type of environment(s) exposed at this endpoint (dev, prod, test, etc.).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param environmentType
         *     The type of environment(s) exposed at this endpoint
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder environmentType(CodeableConcept... environmentType) {
            for (CodeableConcept value : environmentType) {
                this.environmentType.add(value);
            }
            return this;
        }

        /**
         * The type of environment(s) exposed at this endpoint (dev, prod, test, etc.).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param environmentType
         *     The type of environment(s) exposed at this endpoint
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder environmentType(Collection<CodeableConcept> environmentType) {
            this.environmentType = new ArrayList<>(environmentType);
            return this;
        }

        /**
         * The organization that manages this endpoint (even if technically another organization is hosting this in the cloud, it 
         * is the organization associated with the data).
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param managingOrganization
         *     Organization that manages this endpoint (might not be the organization that exposes the endpoint)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder managingOrganization(Reference managingOrganization) {
            this.managingOrganization = managingOrganization;
            return this;
        }

        /**
         * Contact details for a human to contact about the endpoint. The primary use of this for system administrator 
         * troubleshooting.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for source (e.g. troubleshooting)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contact(ContactPoint... contact) {
            for (ContactPoint value : contact) {
                this.contact.add(value);
            }
            return this;
        }

        /**
         * Contact details for a human to contact about the endpoint. The primary use of this for system administrator 
         * troubleshooting.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for source (e.g. troubleshooting)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contact(Collection<ContactPoint> contact) {
            this.contact = new ArrayList<>(contact);
            return this;
        }

        /**
         * The interval during which the endpoint is expected to be operational.
         * 
         * @param period
         *     Interval the endpoint is expected to be operational
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * The set of payloads that are provided/available at this endpoint.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param payload
         *     Set of payloads that are provided by this endpoint
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder payload(Payload... payload) {
            for (Payload value : payload) {
                this.payload.add(value);
            }
            return this;
        }

        /**
         * The set of payloads that are provided/available at this endpoint.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param payload
         *     Set of payloads that are provided by this endpoint
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder payload(Collection<Payload> payload) {
            this.payload = new ArrayList<>(payload);
            return this;
        }

        /**
         * The uri that describes the actual end-point to connect to.
         * 
         * <p>This element is required.
         * 
         * @param address
         *     The technical base address for connecting to this endpoint
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder address(Url address) {
            this.address = address;
            return this;
        }

        /**
         * Convenience method for setting {@code header}.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param header
         *     Usage depends on the channel type
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #header(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder header(java.lang.String... header) {
            for (java.lang.String value : header) {
                this.header.add((value == null) ? null : String.of(value));
            }
            return this;
        }

        /**
         * Additional headers / information to send as part of the notification.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param header
         *     Usage depends on the channel type
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder header(String... header) {
            for (String value : header) {
                this.header.add(value);
            }
            return this;
        }

        /**
         * Additional headers / information to send as part of the notification.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param header
         *     Usage depends on the channel type
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder header(Collection<String> header) {
            this.header = new ArrayList<>(header);
            return this;
        }

        /**
         * Build the {@link Endpoint}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>connectionType</li>
         * <li>address</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Endpoint}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Endpoint per the base specification
         */
        @Override
        public Endpoint build() {
            Endpoint endpoint = new Endpoint(this);
            if (validating) {
                validate(endpoint);
            }
            return endpoint;
        }

        protected void validate(Endpoint endpoint) {
            super.validate(endpoint);
            ValidationSupport.checkList(endpoint.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(endpoint.status, "status");
            ValidationSupport.checkNonEmptyList(endpoint.connectionType, "connectionType", CodeableConcept.class);
            ValidationSupport.checkList(endpoint.environmentType, "environmentType", CodeableConcept.class);
            ValidationSupport.checkList(endpoint.contact, "contact", ContactPoint.class);
            ValidationSupport.checkList(endpoint.payload, "payload", Payload.class);
            ValidationSupport.requireNonNull(endpoint.address, "address");
            ValidationSupport.checkList(endpoint.header, "header", String.class);
            ValidationSupport.checkReferenceType(endpoint.managingOrganization, "managingOrganization", "Organization");
        }

        protected Builder from(Endpoint endpoint) {
            super.from(endpoint);
            identifier.addAll(endpoint.identifier);
            status = endpoint.status;
            connectionType.addAll(endpoint.connectionType);
            name = endpoint.name;
            description = endpoint.description;
            environmentType.addAll(endpoint.environmentType);
            managingOrganization = endpoint.managingOrganization;
            contact.addAll(endpoint.contact);
            period = endpoint.period;
            payload.addAll(endpoint.payload);
            address = endpoint.address;
            header.addAll(endpoint.header);
            return this;
        }
    }

    /**
     * The set of payloads that are provided/available at this endpoint.
     */
    public static class Payload extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "PayloadType",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/endpoint-payload-type"
        )
        private final List<CodeableConcept> type;
        @Summary
        @Binding(
            bindingName = "MimeType",
            strength = BindingStrength.Value.REQUIRED,
            description = "BCP 13 (RFCs 2045, 2046, 2047, 4288, 4289 and 2049)",
            valueSet = "http://hl7.org/fhir/ValueSet/mimetypes|5.0.0"
        )
        private final List<Code> mimeType;

        private Payload(Builder builder) {
            super(builder);
            type = Collections.unmodifiableList(builder.type);
            mimeType = Collections.unmodifiableList(builder.mimeType);
        }

        /**
         * The payload type describes the acceptable content that can be communicated on the endpoint.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * The mime type to send the payload in - e.g. application/fhir+xml, application/fhir+json. If the mime type is not 
         * specified, then the sender could send any content (including no content depending on the connectionType).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Code} that may be empty.
         */
        public List<Code> getMimeType() {
            return mimeType;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !type.isEmpty() || 
                !mimeType.isEmpty();
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
                    accept(type, "type", visitor, CodeableConcept.class);
                    accept(mimeType, "mimeType", visitor, Code.class);
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
            Payload other = (Payload) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(mimeType, other.mimeType);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    mimeType);
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
            private List<CodeableConcept> type = new ArrayList<>();
            private List<Code> mimeType = new ArrayList<>();

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
             * The payload type describes the acceptable content that can be communicated on the endpoint.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     The type of content that may be used at this endpoint (e.g. XDS Discharge summaries)
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
             * The payload type describes the acceptable content that can be communicated on the endpoint.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     The type of content that may be used at this endpoint (e.g. XDS Discharge summaries)
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
             * The mime type to send the payload in - e.g. application/fhir+xml, application/fhir+json. If the mime type is not 
             * specified, then the sender could send any content (including no content depending on the connectionType).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param mimeType
             *     Mimetype to send. If not specified, the content could be anything (including no payload, if the connectionType defined 
             *     this)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder mimeType(Code... mimeType) {
                for (Code value : mimeType) {
                    this.mimeType.add(value);
                }
                return this;
            }

            /**
             * The mime type to send the payload in - e.g. application/fhir+xml, application/fhir+json. If the mime type is not 
             * specified, then the sender could send any content (including no content depending on the connectionType).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param mimeType
             *     Mimetype to send. If not specified, the content could be anything (including no payload, if the connectionType defined 
             *     this)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder mimeType(Collection<Code> mimeType) {
                this.mimeType = new ArrayList<>(mimeType);
                return this;
            }

            /**
             * Build the {@link Payload}
             * 
             * @return
             *     An immutable object of type {@link Payload}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Payload per the base specification
             */
            @Override
            public Payload build() {
                Payload payload = new Payload(this);
                if (validating) {
                    validate(payload);
                }
                return payload;
            }

            protected void validate(Payload payload) {
                super.validate(payload);
                ValidationSupport.checkList(payload.type, "type", CodeableConcept.class);
                ValidationSupport.checkList(payload.mimeType, "mimeType", Code.class);
                ValidationSupport.requireValueOrChildren(payload);
            }

            protected Builder from(Payload payload) {
                super.from(payload);
                type.addAll(payload.type);
                mimeType.addAll(payload.mimeType);
                return this;
            }
        }
    }
}
