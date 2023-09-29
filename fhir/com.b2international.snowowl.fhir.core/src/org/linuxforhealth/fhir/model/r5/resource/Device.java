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
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Base64Binary;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.ContactPoint;
import org.linuxforhealth.fhir.model.r5.type.Count;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceNameType;
import org.linuxforhealth.fhir.model.r5.type.code.FHIRDeviceStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.UDIEntryType;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A type of a manufactured item that is used in the provision of healthcare without being substantially changed through 
 * that activity. The device may be a medical or non-medical device.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "dev-1",
    level = "Rule",
    location = "(base)",
    description = "only one Device.name.display SHALL be true when there is more than one Device.name",
    expression = "name.where(display=true).count() <= 1",
    source = "http://hl7.org/fhir/StructureDefinition/Device"
)
@Constraint(
    id = "device-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/device-availability-status",
    expression = "availabilityStatus.exists() implies (availabilityStatus.memberOf('http://hl7.org/fhir/ValueSet/device-availability-status', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Device",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Device extends DomainResource {
    private final List<Identifier> identifier;
    private final String displayName;
    private final CodeableReference definition;
    @Summary
    private final List<UdiCarrier> udiCarrier;
    @Summary
    @Binding(
        bindingName = "FHIRDeviceStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The record status of the device.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-status|5.0.0"
    )
    private final FHIRDeviceStatus status;
    @Binding(
        bindingName = "FHIRDeviceAvailabilityStatus",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The availability status reason of the device.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-availability-status"
    )
    private final CodeableConcept availabilityStatus;
    private final Identifier biologicalSourceEvent;
    private final String manufacturer;
    private final DateTime manufactureDate;
    private final DateTime expirationDate;
    private final String lotNumber;
    private final String serialNumber;
    private final List<Name> name;
    private final String modelNumber;
    private final String partNumber;
    @Binding(
        bindingName = "DeviceCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Categories of medical devices.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-category"
    )
    private final List<CodeableConcept> category;
    @Binding(
        bindingName = "DeviceType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes to identify medical devices.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-type"
    )
    private final List<CodeableConcept> type;
    private final List<Version> version;
    private final List<ConformsTo> conformsTo;
    private final List<Property> property;
    @Binding(
        bindingName = "DeviceOperationMode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Operational mode of a device.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-operation-mode"
    )
    private final CodeableConcept mode;
    private final Count cycle;
    private final Duration duration;
    @ReferenceTarget({ "Organization" })
    private final Reference owner;
    private final List<ContactPoint> contact;
    @ReferenceTarget({ "Location" })
    private final Reference location;
    private final Uri url;
    @ReferenceTarget({ "Endpoint" })
    private final List<Reference> endpoint;
    private final List<CodeableReference> gateway;
    private final List<Annotation> note;
    @Summary
    @Binding(
        bindingName = "Safety",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/device-safety"
    )
    private final List<CodeableConcept> safety;
    @ReferenceTarget({ "Device" })
    private final Reference parent;

    private Device(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        displayName = builder.displayName;
        definition = builder.definition;
        udiCarrier = Collections.unmodifiableList(builder.udiCarrier);
        status = builder.status;
        availabilityStatus = builder.availabilityStatus;
        biologicalSourceEvent = builder.biologicalSourceEvent;
        manufacturer = builder.manufacturer;
        manufactureDate = builder.manufactureDate;
        expirationDate = builder.expirationDate;
        lotNumber = builder.lotNumber;
        serialNumber = builder.serialNumber;
        name = Collections.unmodifiableList(builder.name);
        modelNumber = builder.modelNumber;
        partNumber = builder.partNumber;
        category = Collections.unmodifiableList(builder.category);
        type = Collections.unmodifiableList(builder.type);
        version = Collections.unmodifiableList(builder.version);
        conformsTo = Collections.unmodifiableList(builder.conformsTo);
        property = Collections.unmodifiableList(builder.property);
        mode = builder.mode;
        cycle = builder.cycle;
        duration = builder.duration;
        owner = builder.owner;
        contact = Collections.unmodifiableList(builder.contact);
        location = builder.location;
        url = builder.url;
        endpoint = Collections.unmodifiableList(builder.endpoint);
        gateway = Collections.unmodifiableList(builder.gateway);
        note = Collections.unmodifiableList(builder.note);
        safety = Collections.unmodifiableList(builder.safety);
        parent = builder.parent;
    }

    /**
     * Unique instance identifiers assigned to a device by manufacturers other organizations or owners.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The name used to display by default when the device is referenced. Based on intent of use by the resource creator, 
     * this may reflect one of the names in Device.name, or may be another simple name.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * The reference to the definition for the device.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getDefinition() {
        return definition;
    }

    /**
     * Unique device identifier (UDI) assigned to device label or package. Note that the Device may include multiple 
     * udiCarriers as it either may include just the udiCarrier for the jurisdiction it is sold, or for multiple 
     * jurisdictions it could have been sold.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UdiCarrier} that may be empty.
     */
    public List<UdiCarrier> getUdiCarrier() {
        return udiCarrier;
    }

    /**
     * The Device record status. This is not the status of the device like availability.
     * 
     * @return
     *     An immutable object of type {@link FHIRDeviceStatus} that may be null.
     */
    public FHIRDeviceStatus getStatus() {
        return status;
    }

    /**
     * The availability of the device.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getAvailabilityStatus() {
        return availabilityStatus;
    }

    /**
     * An identifier that supports traceability to the event during which material in this product from one or more 
     * biological entities was obtained or pooled.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getBiologicalSourceEvent() {
        return biologicalSourceEvent;
    }

    /**
     * A name of the manufacturer or entity legally responsible for the device.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * The date and time when the device was manufactured.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getManufactureDate() {
        return manufactureDate;
    }

    /**
     * The date and time beyond which this device is no longer valid or should not be used (if applicable).
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getExpirationDate() {
        return expirationDate;
    }

    /**
     * Lot number assigned by the manufacturer.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getLotNumber() {
        return lotNumber;
    }

    /**
     * The serial number assigned by the organization when the device was manufactured.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * This represents the manufacturer's name of the device as provided by the device, from a UDI label, or by a person 
     * describing the Device. This typically would be used when a person provides the name(s) or when the device represents 
     * one of the names available from DeviceDefinition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Name} that may be empty.
     */
    public List<Name> getName() {
        return name;
    }

    /**
     * The manufacturer's model number for the device.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getModelNumber() {
        return modelNumber;
    }

    /**
     * The part number or catalog number of the device.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getPartNumber() {
        return partNumber;
    }

    /**
     * Devices may be associated with one or more categories.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * The kind or type of device. A device instance may have more than one type - in which case those are the types that 
     * apply to the specific instance of the device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * The actual design of the device or software version running on the device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Version} that may be empty.
     */
    public List<Version> getVersion() {
        return version;
    }

    /**
     * Identifies the standards, specifications, or formal guidances for the capabilities supported by the device. The device 
     * may be certified as conformant to these specifications e.g., communication, performance, process, measurement, or 
     * specialization standards.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ConformsTo} that may be empty.
     */
    public List<ConformsTo> getConformsTo() {
        return conformsTo;
    }

    /**
     * Static or essentially fixed characteristics or features of the device (e.g., time or timing attributes, resolution, 
     * accuracy, intended use or instructions for use, and physical attributes) that are not otherwise captured in more 
     * specific attributes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
     */
    public List<Property> getProperty() {
        return property;
    }

    /**
     * The designated condition for performing a task with the device.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getMode() {
        return mode;
    }

    /**
     * The series of occurrences that repeats during the operation of the device.
     * 
     * @return
     *     An immutable object of type {@link Count} that may be null.
     */
    public Count getCycle() {
        return cycle;
    }

    /**
     * A measurement of time during the device's operation (e.g., days, hours, mins, etc.).
     * 
     * @return
     *     An immutable object of type {@link Duration} that may be null.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * An organization that is responsible for the provision and ongoing maintenance of the device.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getOwner() {
        return owner;
    }

    /**
     * Contact details for an organization or a particular human that is responsible for the device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactPoint} that may be empty.
     */
    public List<ContactPoint> getContact() {
        return contact;
    }

    /**
     * The place where the device can be found.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * A network address on which the device may be contacted directly.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * Technical endpoints providing access to services provided by the device defined at this resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEndpoint() {
        return endpoint;
    }

    /**
     * The linked device acting as a communication controller, data collector, translator, or concentrator for the current 
     * device (e.g., mobile phone application that relays a blood pressure device's data).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getGateway() {
        return gateway;
    }

    /**
     * Descriptive information, usage information or implantation information that is not captured in an existing element.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Provides additional safety characteristics about a medical device. For example devices containing latex.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSafety() {
        return safety;
    }

    /**
     * The higher level or encompassing device that this device is a logical part of.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getParent() {
        return parent;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (displayName != null) || 
            (definition != null) || 
            !udiCarrier.isEmpty() || 
            (status != null) || 
            (availabilityStatus != null) || 
            (biologicalSourceEvent != null) || 
            (manufacturer != null) || 
            (manufactureDate != null) || 
            (expirationDate != null) || 
            (lotNumber != null) || 
            (serialNumber != null) || 
            !name.isEmpty() || 
            (modelNumber != null) || 
            (partNumber != null) || 
            !category.isEmpty() || 
            !type.isEmpty() || 
            !version.isEmpty() || 
            !conformsTo.isEmpty() || 
            !property.isEmpty() || 
            (mode != null) || 
            (cycle != null) || 
            (duration != null) || 
            (owner != null) || 
            !contact.isEmpty() || 
            (location != null) || 
            (url != null) || 
            !endpoint.isEmpty() || 
            !gateway.isEmpty() || 
            !note.isEmpty() || 
            !safety.isEmpty() || 
            (parent != null);
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
                accept(displayName, "displayName", visitor);
                accept(definition, "definition", visitor);
                accept(udiCarrier, "udiCarrier", visitor, UdiCarrier.class);
                accept(status, "status", visitor);
                accept(availabilityStatus, "availabilityStatus", visitor);
                accept(biologicalSourceEvent, "biologicalSourceEvent", visitor);
                accept(manufacturer, "manufacturer", visitor);
                accept(manufactureDate, "manufactureDate", visitor);
                accept(expirationDate, "expirationDate", visitor);
                accept(lotNumber, "lotNumber", visitor);
                accept(serialNumber, "serialNumber", visitor);
                accept(name, "name", visitor, Name.class);
                accept(modelNumber, "modelNumber", visitor);
                accept(partNumber, "partNumber", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(type, "type", visitor, CodeableConcept.class);
                accept(version, "version", visitor, Version.class);
                accept(conformsTo, "conformsTo", visitor, ConformsTo.class);
                accept(property, "property", visitor, Property.class);
                accept(mode, "mode", visitor);
                accept(cycle, "cycle", visitor);
                accept(duration, "duration", visitor);
                accept(owner, "owner", visitor);
                accept(contact, "contact", visitor, ContactPoint.class);
                accept(location, "location", visitor);
                accept(url, "url", visitor);
                accept(endpoint, "endpoint", visitor, Reference.class);
                accept(gateway, "gateway", visitor, CodeableReference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(safety, "safety", visitor, CodeableConcept.class);
                accept(parent, "parent", visitor);
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
        Device other = (Device) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(displayName, other.displayName) && 
            Objects.equals(definition, other.definition) && 
            Objects.equals(udiCarrier, other.udiCarrier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(availabilityStatus, other.availabilityStatus) && 
            Objects.equals(biologicalSourceEvent, other.biologicalSourceEvent) && 
            Objects.equals(manufacturer, other.manufacturer) && 
            Objects.equals(manufactureDate, other.manufactureDate) && 
            Objects.equals(expirationDate, other.expirationDate) && 
            Objects.equals(lotNumber, other.lotNumber) && 
            Objects.equals(serialNumber, other.serialNumber) && 
            Objects.equals(name, other.name) && 
            Objects.equals(modelNumber, other.modelNumber) && 
            Objects.equals(partNumber, other.partNumber) && 
            Objects.equals(category, other.category) && 
            Objects.equals(type, other.type) && 
            Objects.equals(version, other.version) && 
            Objects.equals(conformsTo, other.conformsTo) && 
            Objects.equals(property, other.property) && 
            Objects.equals(mode, other.mode) && 
            Objects.equals(cycle, other.cycle) && 
            Objects.equals(duration, other.duration) && 
            Objects.equals(owner, other.owner) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(location, other.location) && 
            Objects.equals(url, other.url) && 
            Objects.equals(endpoint, other.endpoint) && 
            Objects.equals(gateway, other.gateway) && 
            Objects.equals(note, other.note) && 
            Objects.equals(safety, other.safety) && 
            Objects.equals(parent, other.parent);
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
                displayName, 
                definition, 
                udiCarrier, 
                status, 
                availabilityStatus, 
                biologicalSourceEvent, 
                manufacturer, 
                manufactureDate, 
                expirationDate, 
                lotNumber, 
                serialNumber, 
                name, 
                modelNumber, 
                partNumber, 
                category, 
                type, 
                version, 
                conformsTo, 
                property, 
                mode, 
                cycle, 
                duration, 
                owner, 
                contact, 
                location, 
                url, 
                endpoint, 
                gateway, 
                note, 
                safety, 
                parent);
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
        private String displayName;
        private CodeableReference definition;
        private List<UdiCarrier> udiCarrier = new ArrayList<>();
        private FHIRDeviceStatus status;
        private CodeableConcept availabilityStatus;
        private Identifier biologicalSourceEvent;
        private String manufacturer;
        private DateTime manufactureDate;
        private DateTime expirationDate;
        private String lotNumber;
        private String serialNumber;
        private List<Name> name = new ArrayList<>();
        private String modelNumber;
        private String partNumber;
        private List<CodeableConcept> category = new ArrayList<>();
        private List<CodeableConcept> type = new ArrayList<>();
        private List<Version> version = new ArrayList<>();
        private List<ConformsTo> conformsTo = new ArrayList<>();
        private List<Property> property = new ArrayList<>();
        private CodeableConcept mode;
        private Count cycle;
        private Duration duration;
        private Reference owner;
        private List<ContactPoint> contact = new ArrayList<>();
        private Reference location;
        private Uri url;
        private List<Reference> endpoint = new ArrayList<>();
        private List<CodeableReference> gateway = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<CodeableConcept> safety = new ArrayList<>();
        private Reference parent;

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
         * Unique instance identifiers assigned to a device by manufacturers other organizations or owners.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Instance identifier
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
         * Unique instance identifiers assigned to a device by manufacturers other organizations or owners.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Instance identifier
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
         * Convenience method for setting {@code displayName}.
         * 
         * @param displayName
         *     The name used to display by default when the device is referenced
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #displayName(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder displayName(java.lang.String displayName) {
            this.displayName = (displayName == null) ? null : String.of(displayName);
            return this;
        }

        /**
         * The name used to display by default when the device is referenced. Based on intent of use by the resource creator, 
         * this may reflect one of the names in Device.name, or may be another simple name.
         * 
         * @param displayName
         *     The name used to display by default when the device is referenced
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * The reference to the definition for the device.
         * 
         * @param definition
         *     The reference to the definition for the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder definition(CodeableReference definition) {
            this.definition = definition;
            return this;
        }

        /**
         * Unique device identifier (UDI) assigned to device label or package. Note that the Device may include multiple 
         * udiCarriers as it either may include just the udiCarrier for the jurisdiction it is sold, or for multiple 
         * jurisdictions it could have been sold.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param udiCarrier
         *     Unique Device Identifier (UDI) Barcode string
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder udiCarrier(UdiCarrier... udiCarrier) {
            for (UdiCarrier value : udiCarrier) {
                this.udiCarrier.add(value);
            }
            return this;
        }

        /**
         * Unique device identifier (UDI) assigned to device label or package. Note that the Device may include multiple 
         * udiCarriers as it either may include just the udiCarrier for the jurisdiction it is sold, or for multiple 
         * jurisdictions it could have been sold.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param udiCarrier
         *     Unique Device Identifier (UDI) Barcode string
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder udiCarrier(Collection<UdiCarrier> udiCarrier) {
            this.udiCarrier = new ArrayList<>(udiCarrier);
            return this;
        }

        /**
         * The Device record status. This is not the status of the device like availability.
         * 
         * @param status
         *     active | inactive | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(FHIRDeviceStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The availability of the device.
         * 
         * @param availabilityStatus
         *     lost | damaged | destroyed | available
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder availabilityStatus(CodeableConcept availabilityStatus) {
            this.availabilityStatus = availabilityStatus;
            return this;
        }

        /**
         * An identifier that supports traceability to the event during which material in this product from one or more 
         * biological entities was obtained or pooled.
         * 
         * @param biologicalSourceEvent
         *     An identifier that supports traceability to the event during which material in this product from one or more 
         *     biological entities was obtained or pooled
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder biologicalSourceEvent(Identifier biologicalSourceEvent) {
            this.biologicalSourceEvent = biologicalSourceEvent;
            return this;
        }

        /**
         * Convenience method for setting {@code manufacturer}.
         * 
         * @param manufacturer
         *     Name of device manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #manufacturer(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder manufacturer(java.lang.String manufacturer) {
            this.manufacturer = (manufacturer == null) ? null : String.of(manufacturer);
            return this;
        }

        /**
         * A name of the manufacturer or entity legally responsible for the device.
         * 
         * @param manufacturer
         *     Name of device manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        /**
         * The date and time when the device was manufactured.
         * 
         * @param manufactureDate
         *     Date when the device was made
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder manufactureDate(DateTime manufactureDate) {
            this.manufactureDate = manufactureDate;
            return this;
        }

        /**
         * The date and time beyond which this device is no longer valid or should not be used (if applicable).
         * 
         * @param expirationDate
         *     Date and time of expiry of this device (if applicable)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder expirationDate(DateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        /**
         * Convenience method for setting {@code lotNumber}.
         * 
         * @param lotNumber
         *     Lot number of manufacture
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #lotNumber(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder lotNumber(java.lang.String lotNumber) {
            this.lotNumber = (lotNumber == null) ? null : String.of(lotNumber);
            return this;
        }

        /**
         * Lot number assigned by the manufacturer.
         * 
         * @param lotNumber
         *     Lot number of manufacture
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
            return this;
        }

        /**
         * Convenience method for setting {@code serialNumber}.
         * 
         * @param serialNumber
         *     Serial number assigned by the manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #serialNumber(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder serialNumber(java.lang.String serialNumber) {
            this.serialNumber = (serialNumber == null) ? null : String.of(serialNumber);
            return this;
        }

        /**
         * The serial number assigned by the organization when the device was manufactured.
         * 
         * @param serialNumber
         *     Serial number assigned by the manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        /**
         * This represents the manufacturer's name of the device as provided by the device, from a UDI label, or by a person 
         * describing the Device. This typically would be used when a person provides the name(s) or when the device represents 
         * one of the names available from DeviceDefinition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     The name or names of the device as known to the manufacturer and/or patient
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(Name... name) {
            for (Name value : name) {
                this.name.add(value);
            }
            return this;
        }

        /**
         * This represents the manufacturer's name of the device as provided by the device, from a UDI label, or by a person 
         * describing the Device. This typically would be used when a person provides the name(s) or when the device represents 
         * one of the names available from DeviceDefinition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param name
         *     The name or names of the device as known to the manufacturer and/or patient
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder name(Collection<Name> name) {
            this.name = new ArrayList<>(name);
            return this;
        }

        /**
         * Convenience method for setting {@code modelNumber}.
         * 
         * @param modelNumber
         *     The manufacturer's model number for the device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #modelNumber(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder modelNumber(java.lang.String modelNumber) {
            this.modelNumber = (modelNumber == null) ? null : String.of(modelNumber);
            return this;
        }

        /**
         * The manufacturer's model number for the device.
         * 
         * @param modelNumber
         *     The manufacturer's model number for the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder modelNumber(String modelNumber) {
            this.modelNumber = modelNumber;
            return this;
        }

        /**
         * Convenience method for setting {@code partNumber}.
         * 
         * @param partNumber
         *     The part number or catalog number of the device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #partNumber(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder partNumber(java.lang.String partNumber) {
            this.partNumber = (partNumber == null) ? null : String.of(partNumber);
            return this;
        }

        /**
         * The part number or catalog number of the device.
         * 
         * @param partNumber
         *     The part number or catalog number of the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder partNumber(String partNumber) {
            this.partNumber = partNumber;
            return this;
        }

        /**
         * Devices may be associated with one or more categories.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Indicates a high-level grouping of the device
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
         * Devices may be associated with one or more categories.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Indicates a high-level grouping of the device
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
         * The kind or type of device. A device instance may have more than one type - in which case those are the types that 
         * apply to the specific instance of the device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     The kind or type of device
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
         * The kind or type of device. A device instance may have more than one type - in which case those are the types that 
         * apply to the specific instance of the device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     The kind or type of device
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
         * The actual design of the device or software version running on the device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param version
         *     The actual design of the device or software version running on the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder version(Version... version) {
            for (Version value : version) {
                this.version.add(value);
            }
            return this;
        }

        /**
         * The actual design of the device or software version running on the device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param version
         *     The actual design of the device or software version running on the device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder version(Collection<Version> version) {
            this.version = new ArrayList<>(version);
            return this;
        }

        /**
         * Identifies the standards, specifications, or formal guidances for the capabilities supported by the device. The device 
         * may be certified as conformant to these specifications e.g., communication, performance, process, measurement, or 
         * specialization standards.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param conformsTo
         *     Identifies the standards, specifications, or formal guidances for the capabilities supported by the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder conformsTo(ConformsTo... conformsTo) {
            for (ConformsTo value : conformsTo) {
                this.conformsTo.add(value);
            }
            return this;
        }

        /**
         * Identifies the standards, specifications, or formal guidances for the capabilities supported by the device. The device 
         * may be certified as conformant to these specifications e.g., communication, performance, process, measurement, or 
         * specialization standards.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param conformsTo
         *     Identifies the standards, specifications, or formal guidances for the capabilities supported by the device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder conformsTo(Collection<ConformsTo> conformsTo) {
            this.conformsTo = new ArrayList<>(conformsTo);
            return this;
        }

        /**
         * Static or essentially fixed characteristics or features of the device (e.g., time or timing attributes, resolution, 
         * accuracy, intended use or instructions for use, and physical attributes) that are not otherwise captured in more 
         * specific attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Inherent, essentially fixed, characteristics of the device. e.g., time properties, size, material, etc.
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder property(Property... property) {
            for (Property value : property) {
                this.property.add(value);
            }
            return this;
        }

        /**
         * Static or essentially fixed characteristics or features of the device (e.g., time or timing attributes, resolution, 
         * accuracy, intended use or instructions for use, and physical attributes) that are not otherwise captured in more 
         * specific attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Inherent, essentially fixed, characteristics of the device. e.g., time properties, size, material, etc.
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder property(Collection<Property> property) {
            this.property = new ArrayList<>(property);
            return this;
        }

        /**
         * The designated condition for performing a task with the device.
         * 
         * @param mode
         *     The designated condition for performing a task
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder mode(CodeableConcept mode) {
            this.mode = mode;
            return this;
        }

        /**
         * The series of occurrences that repeats during the operation of the device.
         * 
         * @param cycle
         *     The series of occurrences that repeats during the operation of the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder cycle(Count cycle) {
            this.cycle = cycle;
            return this;
        }

        /**
         * A measurement of time during the device's operation (e.g., days, hours, mins, etc.).
         * 
         * @param duration
         *     A measurement of time during the device's operation (e.g., days, hours, mins, etc.)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        /**
         * An organization that is responsible for the provision and ongoing maintenance of the device.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param owner
         *     Organization responsible for device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder owner(Reference owner) {
            this.owner = owner;
            return this;
        }

        /**
         * Contact details for an organization or a particular human that is responsible for the device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Details for human/organization for support
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
         * Contact details for an organization or a particular human that is responsible for the device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Details for human/organization for support
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
         * The place where the device can be found.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where the device is found
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * A network address on which the device may be contacted directly.
         * 
         * @param url
         *     Network address to contact device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * Technical endpoints providing access to services provided by the device defined at this resource.
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
         *     Technical endpoints providing access to electronic services provided by the device
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
         * Technical endpoints providing access to services provided by the device defined at this resource.
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
         *     Technical endpoints providing access to electronic services provided by the device
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
         * The linked device acting as a communication controller, data collector, translator, or concentrator for the current 
         * device (e.g., mobile phone application that relays a blood pressure device's data).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param gateway
         *     Linked device acting as a communication/data collector, translator or controller
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder gateway(CodeableReference... gateway) {
            for (CodeableReference value : gateway) {
                this.gateway.add(value);
            }
            return this;
        }

        /**
         * The linked device acting as a communication controller, data collector, translator, or concentrator for the current 
         * device (e.g., mobile phone application that relays a blood pressure device's data).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param gateway
         *     Linked device acting as a communication/data collector, translator or controller
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder gateway(Collection<CodeableReference> gateway) {
            this.gateway = new ArrayList<>(gateway);
            return this;
        }

        /**
         * Descriptive information, usage information or implantation information that is not captured in an existing element.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Device notes and comments
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder note(Annotation... note) {
            for (Annotation value : note) {
                this.note.add(value);
            }
            return this;
        }

        /**
         * Descriptive information, usage information or implantation information that is not captured in an existing element.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Device notes and comments
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder note(Collection<Annotation> note) {
            this.note = new ArrayList<>(note);
            return this;
        }

        /**
         * Provides additional safety characteristics about a medical device. For example devices containing latex.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param safety
         *     Safety Characteristics of Device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder safety(CodeableConcept... safety) {
            for (CodeableConcept value : safety) {
                this.safety.add(value);
            }
            return this;
        }

        /**
         * Provides additional safety characteristics about a medical device. For example devices containing latex.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param safety
         *     Safety Characteristics of Device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder safety(Collection<CodeableConcept> safety) {
            this.safety = new ArrayList<>(safety);
            return this;
        }

        /**
         * The higher level or encompassing device that this device is a logical part of.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param parent
         *     The higher level or encompassing device that this device is a logical part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder parent(Reference parent) {
            this.parent = parent;
            return this;
        }

        /**
         * Build the {@link Device}
         * 
         * @return
         *     An immutable object of type {@link Device}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Device per the base specification
         */
        @Override
        public Device build() {
            Device device = new Device(this);
            if (validating) {
                validate(device);
            }
            return device;
        }

        protected void validate(Device device) {
            super.validate(device);
            ValidationSupport.checkList(device.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(device.udiCarrier, "udiCarrier", UdiCarrier.class);
            ValidationSupport.checkList(device.name, "name", Name.class);
            ValidationSupport.checkList(device.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(device.type, "type", CodeableConcept.class);
            ValidationSupport.checkList(device.version, "version", Version.class);
            ValidationSupport.checkList(device.conformsTo, "conformsTo", ConformsTo.class);
            ValidationSupport.checkList(device.property, "property", Property.class);
            ValidationSupport.checkList(device.contact, "contact", ContactPoint.class);
            ValidationSupport.checkList(device.endpoint, "endpoint", Reference.class);
            ValidationSupport.checkList(device.gateway, "gateway", CodeableReference.class);
            ValidationSupport.checkList(device.note, "note", Annotation.class);
            ValidationSupport.checkList(device.safety, "safety", CodeableConcept.class);
            ValidationSupport.checkReferenceType(device.owner, "owner", "Organization");
            ValidationSupport.checkReferenceType(device.location, "location", "Location");
            ValidationSupport.checkReferenceType(device.endpoint, "endpoint", "Endpoint");
            ValidationSupport.checkReferenceType(device.parent, "parent", "Device");
        }

        protected Builder from(Device device) {
            super.from(device);
            identifier.addAll(device.identifier);
            displayName = device.displayName;
            definition = device.definition;
            udiCarrier.addAll(device.udiCarrier);
            status = device.status;
            availabilityStatus = device.availabilityStatus;
            biologicalSourceEvent = device.biologicalSourceEvent;
            manufacturer = device.manufacturer;
            manufactureDate = device.manufactureDate;
            expirationDate = device.expirationDate;
            lotNumber = device.lotNumber;
            serialNumber = device.serialNumber;
            name.addAll(device.name);
            modelNumber = device.modelNumber;
            partNumber = device.partNumber;
            category.addAll(device.category);
            type.addAll(device.type);
            version.addAll(device.version);
            conformsTo.addAll(device.conformsTo);
            property.addAll(device.property);
            mode = device.mode;
            cycle = device.cycle;
            duration = device.duration;
            owner = device.owner;
            contact.addAll(device.contact);
            location = device.location;
            url = device.url;
            endpoint.addAll(device.endpoint);
            gateway.addAll(device.gateway);
            note.addAll(device.note);
            safety.addAll(device.safety);
            parent = device.parent;
            return this;
        }
    }

    /**
     * Unique device identifier (UDI) assigned to device label or package. Note that the Device may include multiple 
     * udiCarriers as it either may include just the udiCarrier for the jurisdiction it is sold, or for multiple 
     * jurisdictions it could have been sold.
     */
    public static class UdiCarrier extends BackboneElement {
        @Summary
        @Required
        private final String deviceIdentifier;
        @Summary
        @Required
        private final Uri issuer;
        private final Uri jurisdiction;
        @Summary
        private final Base64Binary carrierAIDC;
        @Summary
        private final String carrierHRF;
        @Binding(
            bindingName = "UDIEntryType",
            strength = BindingStrength.Value.REQUIRED,
            description = "Codes to identify how UDI data was entered.",
            valueSet = "http://hl7.org/fhir/ValueSet/udi-entry-type|5.0.0"
        )
        private final UDIEntryType entryType;

        private UdiCarrier(Builder builder) {
            super(builder);
            deviceIdentifier = builder.deviceIdentifier;
            issuer = builder.issuer;
            jurisdiction = builder.jurisdiction;
            carrierAIDC = builder.carrierAIDC;
            carrierHRF = builder.carrierHRF;
            entryType = builder.entryType;
        }

        /**
         * The device identifier (DI) is a mandatory, fixed portion of a UDI that identifies the labeler and the specific version 
         * or model of a device.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getDeviceIdentifier() {
            return deviceIdentifier;
        }

        /**
         * Organization that is charged with issuing UDIs for devices. For example, the US FDA issuers include: 
1) GS1: http:
         * //hl7.org/fhir/NamingSystem/gs1-di, 
2) HIBCC: http://hl7.org/fhir/NamingSystem/hibcc-diI, 
3) ICCBBA for blood 
         * containers: http://hl7.org/fhir/NamingSystem/iccbba-blood-di, 
4) ICCBA for other devices: http://hl7.
         * org/fhir/NamingSystem/iccbba-other-di # Informationsstelle für Arzneispezialitäten (IFA GmbH) (EU only): http://hl7.
         * org/fhir/NamingSystem/ifa-gmbh-di.
         * 
         * @return
         *     An immutable object of type {@link Uri} that is non-null.
         */
        public Uri getIssuer() {
            return issuer;
        }

        /**
         * The identity of the authoritative source for UDI generation within a jurisdiction. All UDIs are globally unique within 
         * a single namespace with the appropriate repository uri as the system. For example, UDIs of devices managed in the U.S. 
         * by the FDA, the value is http://hl7.org/fhir/NamingSystem/us-fda-udi or in the European Union by the European 
         * Commission http://hl7.org/fhir/NamingSystem/eu-ec-udi.
         * 
         * @return
         *     An immutable object of type {@link Uri} that may be null.
         */
        public Uri getJurisdiction() {
            return jurisdiction;
        }

        /**
         * The full UDI carrier of the Automatic Identification and Data Capture (AIDC) technology representation of the barcode 
         * string as printed on the packaging of the device - e.g., a barcode or RFID. Because of limitations on character sets 
         * in XML and the need to round-trip JSON data through XML, AIDC Formats *SHALL* be base64 encoded.
         * 
         * @return
         *     An immutable object of type {@link Base64Binary} that may be null.
         */
        public Base64Binary getCarrierAIDC() {
            return carrierAIDC;
        }

        /**
         * The full UDI carrier as the human readable form (HRF) representation of the barcode string as printed on the packaging 
         * of the device.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getCarrierHRF() {
            return carrierHRF;
        }

        /**
         * A coded entry to indicate how the data was entered.
         * 
         * @return
         *     An immutable object of type {@link UDIEntryType} that may be null.
         */
        public UDIEntryType getEntryType() {
            return entryType;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (deviceIdentifier != null) || 
                (issuer != null) || 
                (jurisdiction != null) || 
                (carrierAIDC != null) || 
                (carrierHRF != null) || 
                (entryType != null);
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
                    accept(deviceIdentifier, "deviceIdentifier", visitor);
                    accept(issuer, "issuer", visitor);
                    accept(jurisdiction, "jurisdiction", visitor);
                    accept(carrierAIDC, "carrierAIDC", visitor);
                    accept(carrierHRF, "carrierHRF", visitor);
                    accept(entryType, "entryType", visitor);
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
            UdiCarrier other = (UdiCarrier) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(deviceIdentifier, other.deviceIdentifier) && 
                Objects.equals(issuer, other.issuer) && 
                Objects.equals(jurisdiction, other.jurisdiction) && 
                Objects.equals(carrierAIDC, other.carrierAIDC) && 
                Objects.equals(carrierHRF, other.carrierHRF) && 
                Objects.equals(entryType, other.entryType);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    deviceIdentifier, 
                    issuer, 
                    jurisdiction, 
                    carrierAIDC, 
                    carrierHRF, 
                    entryType);
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
            private String deviceIdentifier;
            private Uri issuer;
            private Uri jurisdiction;
            private Base64Binary carrierAIDC;
            private String carrierHRF;
            private UDIEntryType entryType;

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
             * Convenience method for setting {@code deviceIdentifier}.
             * 
             * <p>This element is required.
             * 
             * @param deviceIdentifier
             *     Mandatory fixed portion of UDI
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #deviceIdentifier(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder deviceIdentifier(java.lang.String deviceIdentifier) {
                this.deviceIdentifier = (deviceIdentifier == null) ? null : String.of(deviceIdentifier);
                return this;
            }

            /**
             * The device identifier (DI) is a mandatory, fixed portion of a UDI that identifies the labeler and the specific version 
             * or model of a device.
             * 
             * <p>This element is required.
             * 
             * @param deviceIdentifier
             *     Mandatory fixed portion of UDI
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder deviceIdentifier(String deviceIdentifier) {
                this.deviceIdentifier = deviceIdentifier;
                return this;
            }

            /**
             * Organization that is charged with issuing UDIs for devices. For example, the US FDA issuers include: 
1) GS1: http:
             * //hl7.org/fhir/NamingSystem/gs1-di, 
2) HIBCC: http://hl7.org/fhir/NamingSystem/hibcc-diI, 
3) ICCBBA for blood 
             * containers: http://hl7.org/fhir/NamingSystem/iccbba-blood-di, 
4) ICCBA for other devices: http://hl7.
             * org/fhir/NamingSystem/iccbba-other-di # Informationsstelle für Arzneispezialitäten (IFA GmbH) (EU only): http://hl7.
             * org/fhir/NamingSystem/ifa-gmbh-di.
             * 
             * <p>This element is required.
             * 
             * @param issuer
             *     UDI Issuing Organization
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder issuer(Uri issuer) {
                this.issuer = issuer;
                return this;
            }

            /**
             * The identity of the authoritative source for UDI generation within a jurisdiction. All UDIs are globally unique within 
             * a single namespace with the appropriate repository uri as the system. For example, UDIs of devices managed in the U.S. 
             * by the FDA, the value is http://hl7.org/fhir/NamingSystem/us-fda-udi or in the European Union by the European 
             * Commission http://hl7.org/fhir/NamingSystem/eu-ec-udi.
             * 
             * @param jurisdiction
             *     Regional UDI authority
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder jurisdiction(Uri jurisdiction) {
                this.jurisdiction = jurisdiction;
                return this;
            }

            /**
             * The full UDI carrier of the Automatic Identification and Data Capture (AIDC) technology representation of the barcode 
             * string as printed on the packaging of the device - e.g., a barcode or RFID. Because of limitations on character sets 
             * in XML and the need to round-trip JSON data through XML, AIDC Formats *SHALL* be base64 encoded.
             * 
             * @param carrierAIDC
             *     UDI Machine Readable Barcode String
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder carrierAIDC(Base64Binary carrierAIDC) {
                this.carrierAIDC = carrierAIDC;
                return this;
            }

            /**
             * Convenience method for setting {@code carrierHRF}.
             * 
             * @param carrierHRF
             *     UDI Human Readable Barcode String
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #carrierHRF(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder carrierHRF(java.lang.String carrierHRF) {
                this.carrierHRF = (carrierHRF == null) ? null : String.of(carrierHRF);
                return this;
            }

            /**
             * The full UDI carrier as the human readable form (HRF) representation of the barcode string as printed on the packaging 
             * of the device.
             * 
             * @param carrierHRF
             *     UDI Human Readable Barcode String
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder carrierHRF(String carrierHRF) {
                this.carrierHRF = carrierHRF;
                return this;
            }

            /**
             * A coded entry to indicate how the data was entered.
             * 
             * @param entryType
             *     barcode | rfid | manual | card | self-reported | electronic-transmission | unknown
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder entryType(UDIEntryType entryType) {
                this.entryType = entryType;
                return this;
            }

            /**
             * Build the {@link UdiCarrier}
             * 
             * <p>Required elements:
             * <ul>
             * <li>deviceIdentifier</li>
             * <li>issuer</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link UdiCarrier}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid UdiCarrier per the base specification
             */
            @Override
            public UdiCarrier build() {
                UdiCarrier udiCarrier = new UdiCarrier(this);
                if (validating) {
                    validate(udiCarrier);
                }
                return udiCarrier;
            }

            protected void validate(UdiCarrier udiCarrier) {
                super.validate(udiCarrier);
                ValidationSupport.requireNonNull(udiCarrier.deviceIdentifier, "deviceIdentifier");
                ValidationSupport.requireNonNull(udiCarrier.issuer, "issuer");
                ValidationSupport.requireValueOrChildren(udiCarrier);
            }

            protected Builder from(UdiCarrier udiCarrier) {
                super.from(udiCarrier);
                deviceIdentifier = udiCarrier.deviceIdentifier;
                issuer = udiCarrier.issuer;
                jurisdiction = udiCarrier.jurisdiction;
                carrierAIDC = udiCarrier.carrierAIDC;
                carrierHRF = udiCarrier.carrierHRF;
                entryType = udiCarrier.entryType;
                return this;
            }
        }
    }

    /**
     * This represents the manufacturer's name of the device as provided by the device, from a UDI label, or by a person 
     * describing the Device. This typically would be used when a person provides the name(s) or when the device represents 
     * one of the names available from DeviceDefinition.
     */
    public static class Name extends BackboneElement {
        @Summary
        @Required
        private final String value;
        @Summary
        @Binding(
            bindingName = "DeviceNameType",
            strength = BindingStrength.Value.REQUIRED,
            description = "The type of name the device is referred by.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-nametype|5.0.0"
        )
        @Required
        private final DeviceNameType type;
        @Summary
        private final Boolean display;

        private Name(Builder builder) {
            super(builder);
            value = builder.value;
            type = builder.type;
            display = builder.display;
        }

        /**
         * The actual name that identifies the device.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getValue() {
            return value;
        }

        /**
         * Indicates the kind of name. RegisteredName | UserFriendlyName | PatientReportedName.
         * 
         * @return
         *     An immutable object of type {@link DeviceNameType} that is non-null.
         */
        public DeviceNameType getType() {
            return type;
        }

        /**
         * Indicates the default or preferred name to be displayed.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getDisplay() {
            return display;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (value != null) || 
                (type != null) || 
                (display != null);
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
                    accept(type, "type", visitor);
                    accept(display, "display", visitor);
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
            Name other = (Name) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(value, other.value) && 
                Objects.equals(type, other.type) && 
                Objects.equals(display, other.display);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    value, 
                    type, 
                    display);
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
            private String value;
            private DeviceNameType type;
            private Boolean display;

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
             * Convenience method for setting {@code value}.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     The term that names the device
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder value(java.lang.String value) {
                this.value = (value == null) ? null : String.of(value);
                return this;
            }

            /**
             * The actual name that identifies the device.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     The term that names the device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(String value) {
                this.value = value;
                return this;
            }

            /**
             * Indicates the kind of name. RegisteredName | UserFriendlyName | PatientReportedName.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     registered-name | user-friendly-name | patient-reported-name
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(DeviceNameType type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code display}.
             * 
             * @param display
             *     The preferred device name
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #display(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder display(java.lang.Boolean display) {
                this.display = (display == null) ? null : Boolean.of(display);
                return this;
            }

            /**
             * Indicates the default or preferred name to be displayed.
             * 
             * @param display
             *     The preferred device name
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder display(Boolean display) {
                this.display = display;
                return this;
            }

            /**
             * Build the {@link Name}
             * 
             * <p>Required elements:
             * <ul>
             * <li>value</li>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Name}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Name per the base specification
             */
            @Override
            public Name build() {
                Name name = new Name(this);
                if (validating) {
                    validate(name);
                }
                return name;
            }

            protected void validate(Name name) {
                super.validate(name);
                ValidationSupport.requireNonNull(name.value, "value");
                ValidationSupport.requireNonNull(name.type, "type");
                ValidationSupport.requireValueOrChildren(name);
            }

            protected Builder from(Name name) {
                super.from(name);
                value = name.value;
                type = name.type;
                display = name.display;
                return this;
            }
        }
    }

    /**
     * The actual design of the device or software version running on the device.
     */
    public static class Version extends BackboneElement {
        @Binding(
            bindingName = "FHIRDeviceVersionType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The type of version indicated for the device.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-versiontype"
        )
        private final CodeableConcept type;
        private final Identifier component;
        private final DateTime installDate;
        @Required
        private final String value;

        private Version(Builder builder) {
            super(builder);
            type = builder.type;
            component = builder.component;
            installDate = builder.installDate;
            value = builder.value;
        }

        /**
         * The type of the device version, e.g. manufacturer, approved, internal.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The hardware or software module of the device to which the version applies.
         * 
         * @return
         *     An immutable object of type {@link Identifier} that may be null.
         */
        public Identifier getComponent() {
            return component;
        }

        /**
         * The date the version was installed on the device.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getInstallDate() {
            return installDate;
        }

        /**
         * The version text.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (component != null) || 
                (installDate != null) || 
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
                    accept(type, "type", visitor);
                    accept(component, "component", visitor);
                    accept(installDate, "installDate", visitor);
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
            Version other = (Version) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(component, other.component) && 
                Objects.equals(installDate, other.installDate) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    component, 
                    installDate, 
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
            private CodeableConcept type;
            private Identifier component;
            private DateTime installDate;
            private String value;

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
             * The type of the device version, e.g. manufacturer, approved, internal.
             * 
             * @param type
             *     The type of the device version, e.g. manufacturer, approved, internal
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The hardware or software module of the device to which the version applies.
             * 
             * @param component
             *     The hardware or software module of the device to which the version applies
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder component(Identifier component) {
                this.component = component;
                return this;
            }

            /**
             * The date the version was installed on the device.
             * 
             * @param installDate
             *     The date the version was installed on the device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder installDate(DateTime installDate) {
                this.installDate = installDate;
                return this;
            }

            /**
             * Convenience method for setting {@code value}.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     The version text
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder value(java.lang.String value) {
                this.value = (value == null) ? null : String.of(value);
                return this;
            }

            /**
             * The version text.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     The version text
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(String value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Version}
             * 
             * <p>Required elements:
             * <ul>
             * <li>value</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Version}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Version per the base specification
             */
            @Override
            public Version build() {
                Version version = new Version(this);
                if (validating) {
                    validate(version);
                }
                return version;
            }

            protected void validate(Version version) {
                super.validate(version);
                ValidationSupport.requireNonNull(version.value, "value");
                ValidationSupport.requireValueOrChildren(version);
            }

            protected Builder from(Version version) {
                super.from(version);
                type = version.type;
                component = version.component;
                installDate = version.installDate;
                value = version.value;
                return this;
            }
        }
    }

    /**
     * Identifies the standards, specifications, or formal guidances for the capabilities supported by the device. The device 
     * may be certified as conformant to these specifications e.g., communication, performance, process, measurement, or 
     * specialization standards.
     */
    public static class ConformsTo extends BackboneElement {
        @Binding(
            bindingName = "DeviceSpecificationCategory",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The kind of standards used by the device.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-specification-category"
        )
        private final CodeableConcept category;
        @Binding(
            bindingName = "DeviceSpecification-type",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The type of version indicated for the device.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-specification-type"
        )
        @Required
        private final CodeableConcept specification;
        private final String version;

        private ConformsTo(Builder builder) {
            super(builder);
            category = builder.category;
            specification = builder.specification;
            version = builder.version;
        }

        /**
         * Describes the type of the standard, specification, or formal guidance.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCategory() {
            return category;
        }

        /**
         * Code that identifies the specific standard, specification, protocol, formal guidance, regulation, legislation, or 
         * certification scheme to which the device adheres.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getSpecification() {
            return specification;
        }

        /**
         * Identifies the specific form or variant of the standard, specification, or formal guidance. This may be a 'version 
         * number', release, document edition, publication year, or other label.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getVersion() {
            return version;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (category != null) || 
                (specification != null) || 
                (version != null);
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
                    accept(category, "category", visitor);
                    accept(specification, "specification", visitor);
                    accept(version, "version", visitor);
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
            ConformsTo other = (ConformsTo) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(category, other.category) && 
                Objects.equals(specification, other.specification) && 
                Objects.equals(version, other.version);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    category, 
                    specification, 
                    version);
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
            private CodeableConcept category;
            private CodeableConcept specification;
            private String version;

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
             * Describes the type of the standard, specification, or formal guidance.
             * 
             * @param category
             *     Describes the common type of the standard, specification, or formal guidance. communication | performance | measurement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder category(CodeableConcept category) {
                this.category = category;
                return this;
            }

            /**
             * Code that identifies the specific standard, specification, protocol, formal guidance, regulation, legislation, or 
             * certification scheme to which the device adheres.
             * 
             * <p>This element is required.
             * 
             * @param specification
             *     Identifies the standard, specification, or formal guidance that the device adheres to
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder specification(CodeableConcept specification) {
                this.specification = specification;
                return this;
            }

            /**
             * Convenience method for setting {@code version}.
             * 
             * @param version
             *     Specific form or variant of the standard
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
             * Identifies the specific form or variant of the standard, specification, or formal guidance. This may be a 'version 
             * number', release, document edition, publication year, or other label.
             * 
             * @param version
             *     Specific form or variant of the standard
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder version(String version) {
                this.version = version;
                return this;
            }

            /**
             * Build the {@link ConformsTo}
             * 
             * <p>Required elements:
             * <ul>
             * <li>specification</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ConformsTo}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ConformsTo per the base specification
             */
            @Override
            public ConformsTo build() {
                ConformsTo conformsTo = new ConformsTo(this);
                if (validating) {
                    validate(conformsTo);
                }
                return conformsTo;
            }

            protected void validate(ConformsTo conformsTo) {
                super.validate(conformsTo);
                ValidationSupport.requireNonNull(conformsTo.specification, "specification");
                ValidationSupport.requireValueOrChildren(conformsTo);
            }

            protected Builder from(ConformsTo conformsTo) {
                super.from(conformsTo);
                category = conformsTo.category;
                specification = conformsTo.specification;
                version = conformsTo.version;
                return this;
            }
        }
    }

    /**
     * Static or essentially fixed characteristics or features of the device (e.g., time or timing attributes, resolution, 
     * accuracy, intended use or instructions for use, and physical attributes) that are not otherwise captured in more 
     * specific attributes.
     */
    public static class Property extends BackboneElement {
        @Binding(
            bindingName = "DevicePropertyType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Device property type.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-property-type"
        )
        @Required
        private final CodeableConcept type;
        @Choice({ Quantity.class, CodeableConcept.class, String.class, Boolean.class, Integer.class, Range.class, Attachment.class })
        @Required
        private final Element value;

        private Property(Builder builder) {
            super(builder);
            type = builder.type;
            value = builder.value;
        }

        /**
         * Code that specifies the property, such as resolution, color, size, being represented.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The value of the property specified by the associated property.type code.
         * 
         * @return
         *     An immutable object of type {@link Quantity}, {@link CodeableConcept}, {@link String}, {@link Boolean}, {@link 
         *     Integer}, {@link Range} or {@link Attachment} that is non-null.
         */
        public Element getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
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
                    accept(type, "type", visitor);
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
            Property other = (Property) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
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
            private CodeableConcept type;
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
             * Code that specifies the property, such as resolution, color, size, being represented.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Code that specifies the property being represented
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type String.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Value of the property
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.String value) {
                this.value = (value == null) ? null : String.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Value of the property
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.Boolean value) {
                this.value = (value == null) ? null : Boolean.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Integer.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Value of the property
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.Integer value) {
                this.value = (value == null) ? null : Integer.of(value);
                return this;
            }

            /**
             * The value of the property specified by the associated property.type code.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Quantity}</li>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link String}</li>
             * <li>{@link Boolean}</li>
             * <li>{@link Integer}</li>
             * <li>{@link Range}</li>
             * <li>{@link Attachment}</li>
             * </ul>
             * 
             * @param value
             *     Value of the property
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Property}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>value</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Property}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Property per the base specification
             */
            @Override
            public Property build() {
                Property property = new Property(this);
                if (validating) {
                    validate(property);
                }
                return property;
            }

            protected void validate(Property property) {
                super.validate(property);
                ValidationSupport.requireNonNull(property.type, "type");
                ValidationSupport.requireChoiceElement(property.value, "value", Quantity.class, CodeableConcept.class, String.class, Boolean.class, Integer.class, Range.class, Attachment.class);
                ValidationSupport.requireValueOrChildren(property);
            }

            protected Builder from(Property property) {
                super.from(property);
                type = property.type;
                value = property.value;
                return this;
            }
        }
    }
}
