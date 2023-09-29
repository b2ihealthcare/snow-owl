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
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.ContactPoint;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.ProductShelfLife;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceCorrectiveActionScope;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceNameType;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceProductionIdentifierInUDI;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceRegulatoryIdentifierType;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The characteristics, operational status and capabilities of a medical-related component of a medical device.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "deviceDefinition-0",
    level = "Warning",
    location = "link.relation",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/devicedefinition-relationtype",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/devicedefinition-relationtype', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/DeviceDefinition",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceDefinition extends DomainResource {
    private final Markdown description;
    @Summary
    private final List<Identifier> identifier;
    private final List<UdiDeviceIdentifier> udiDeviceIdentifier;
    private final List<RegulatoryIdentifier> regulatoryIdentifier;
    private final String partNumber;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference manufacturer;
    @Summary
    private final List<DeviceName> deviceName;
    @Summary
    private final String modelNumber;
    @Summary
    private final List<Classification> classification;
    @Summary
    private final List<ConformsTo> conformsTo;
    @Summary
    private final List<HasPart> hasPart;
    private final List<Packaging> packaging;
    private final List<Version> version;
    @Summary
    @Binding(
        bindingName = "Safety",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/device-safety"
    )
    private final List<CodeableConcept> safety;
    private final List<ProductShelfLife> shelfLifeStorage;
    private final List<CodeableConcept> languageCode;
    private final List<Property> property;
    @ReferenceTarget({ "Organization" })
    private final Reference owner;
    private final List<ContactPoint> contact;
    private final List<Link> link;
    private final List<Annotation> note;
    private final List<Material> material;
    @Binding(
        bindingName = "DeviceProductionIdentifierInUDI",
        strength = BindingStrength.Value.REQUIRED,
        description = "The production identifier(s) that are expected to appear in the UDI carrier.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-productidentifierinudi|5.0.0"
    )
    private final List<DeviceProductionIdentifierInUDI> productionIdentifierInUDI;
    private final Guideline guideline;
    private final CorrectiveAction correctiveAction;
    private final List<ChargeItem> chargeItem;

    private DeviceDefinition(Builder builder) {
        super(builder);
        description = builder.description;
        identifier = Collections.unmodifiableList(builder.identifier);
        udiDeviceIdentifier = Collections.unmodifiableList(builder.udiDeviceIdentifier);
        regulatoryIdentifier = Collections.unmodifiableList(builder.regulatoryIdentifier);
        partNumber = builder.partNumber;
        manufacturer = builder.manufacturer;
        deviceName = Collections.unmodifiableList(builder.deviceName);
        modelNumber = builder.modelNumber;
        classification = Collections.unmodifiableList(builder.classification);
        conformsTo = Collections.unmodifiableList(builder.conformsTo);
        hasPart = Collections.unmodifiableList(builder.hasPart);
        packaging = Collections.unmodifiableList(builder.packaging);
        version = Collections.unmodifiableList(builder.version);
        safety = Collections.unmodifiableList(builder.safety);
        shelfLifeStorage = Collections.unmodifiableList(builder.shelfLifeStorage);
        languageCode = Collections.unmodifiableList(builder.languageCode);
        property = Collections.unmodifiableList(builder.property);
        owner = builder.owner;
        contact = Collections.unmodifiableList(builder.contact);
        link = Collections.unmodifiableList(builder.link);
        note = Collections.unmodifiableList(builder.note);
        material = Collections.unmodifiableList(builder.material);
        productionIdentifierInUDI = Collections.unmodifiableList(builder.productionIdentifierInUDI);
        guideline = builder.guideline;
        correctiveAction = builder.correctiveAction;
        chargeItem = Collections.unmodifiableList(builder.chargeItem);
    }

    /**
     * Additional information to describe the device.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * Unique instance identifiers assigned to a device by the software, manufacturers, other organizations or owners. For 
     * example: handle ID. The identifier is typically valued if the udiDeviceIdentifier, partNumber or modelNumber is not 
     * valued and represents a different type of identifier. However, it is permissible to still include those identifiers in 
     * DeviceDefinition.identifier with the appropriate identifier.type.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Unique device identifier (UDI) assigned to device label or package. Note that the Device may include multiple 
     * udiCarriers as it either may include just the udiCarrier for the jurisdiction it is sold, or for multiple 
     * jurisdictions it could have been sold.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UdiDeviceIdentifier} that may be empty.
     */
    public List<UdiDeviceIdentifier> getUdiDeviceIdentifier() {
        return udiDeviceIdentifier;
    }

    /**
     * Identifier associated with the regulatory documentation (certificates, technical documentation, post-market 
     * surveillance documentation and reports) of a set of device models sharing the same intended purpose, risk class and 
     * essential design and manufacturing characteristics. One example is the Basic UDI-DI in Europe.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RegulatoryIdentifier} that may be empty.
     */
    public List<RegulatoryIdentifier> getRegulatoryIdentifier() {
        return regulatoryIdentifier;
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
     * A name of the manufacturer or legal representative e.g. labeler. Whether this is the actual manufacturer or the 
     * labeler or responsible depends on implementation and jurisdiction.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getManufacturer() {
        return manufacturer;
    }

    /**
     * The name or names of the device as given by the manufacturer.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link DeviceName} that may be empty.
     */
    public List<DeviceName> getDeviceName() {
        return deviceName;
    }

    /**
     * The model number for the device for example as defined by the manufacturer or labeler, or other agency.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getModelNumber() {
        return modelNumber;
    }

    /**
     * What kind of device or device system this is.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Classification} that may be empty.
     */
    public List<Classification> getClassification() {
        return classification;
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
     * A device that is part (for example a component) of the present device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link HasPart} that may be empty.
     */
    public List<HasPart> getHasPart() {
        return hasPart;
    }

    /**
     * Information about the packaging of the device, i.e. how the device is packaged.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Packaging} that may be empty.
     */
    public List<Packaging> getPackaging() {
        return packaging;
    }

    /**
     * The version of the device or software.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Version} that may be empty.
     */
    public List<Version> getVersion() {
        return version;
    }

    /**
     * Safety characteristics of the device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSafety() {
        return safety;
    }

    /**
     * Shelf Life and storage information.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ProductShelfLife} that may be empty.
     */
    public List<ProductShelfLife> getShelfLifeStorage() {
        return shelfLifeStorage;
    }

    /**
     * Language code for the human-readable text strings produced by the device (all supported).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getLanguageCode() {
        return languageCode;
    }

    /**
     * Static or essentially fixed characteristics or features of this kind of device that are otherwise not captured in more 
     * specific attributes, e.g., time or timing attributes, resolution, accuracy, and physical attributes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
     */
    public List<Property> getProperty() {
        return property;
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
     * An associated device, attached to, used with, communicating with or linking a previous or new device model to the 
     * focal device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Link} that may be empty.
     */
    public List<Link> getLink() {
        return link;
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
     * A substance used to create the material(s) of which the device is made.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Material} that may be empty.
     */
    public List<Material> getMaterial() {
        return material;
    }

    /**
     * Indicates the production identifier(s) that are expected to appear in the UDI carrier on the device label.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link DeviceProductionIdentifierInUDI} that may be empty.
     */
    public List<DeviceProductionIdentifierInUDI> getProductionIdentifierInUDI() {
        return productionIdentifierInUDI;
    }

    /**
     * Information aimed at providing directions for the usage of this model of device.
     * 
     * @return
     *     An immutable object of type {@link Guideline} that may be null.
     */
    public Guideline getGuideline() {
        return guideline;
    }

    /**
     * Tracking of latest field safety corrective action.
     * 
     * @return
     *     An immutable object of type {@link CorrectiveAction} that may be null.
     */
    public CorrectiveAction getCorrectiveAction() {
        return correctiveAction;
    }

    /**
     * Billing code or reference associated with the device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ChargeItem} that may be empty.
     */
    public List<ChargeItem> getChargeItem() {
        return chargeItem;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (description != null) || 
            !identifier.isEmpty() || 
            !udiDeviceIdentifier.isEmpty() || 
            !regulatoryIdentifier.isEmpty() || 
            (partNumber != null) || 
            (manufacturer != null) || 
            !deviceName.isEmpty() || 
            (modelNumber != null) || 
            !classification.isEmpty() || 
            !conformsTo.isEmpty() || 
            !hasPart.isEmpty() || 
            !packaging.isEmpty() || 
            !version.isEmpty() || 
            !safety.isEmpty() || 
            !shelfLifeStorage.isEmpty() || 
            !languageCode.isEmpty() || 
            !property.isEmpty() || 
            (owner != null) || 
            !contact.isEmpty() || 
            !link.isEmpty() || 
            !note.isEmpty() || 
            !material.isEmpty() || 
            !productionIdentifierInUDI.isEmpty() || 
            (guideline != null) || 
            (correctiveAction != null) || 
            !chargeItem.isEmpty();
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
                accept(description, "description", visitor);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(udiDeviceIdentifier, "udiDeviceIdentifier", visitor, UdiDeviceIdentifier.class);
                accept(regulatoryIdentifier, "regulatoryIdentifier", visitor, RegulatoryIdentifier.class);
                accept(partNumber, "partNumber", visitor);
                accept(manufacturer, "manufacturer", visitor);
                accept(deviceName, "deviceName", visitor, DeviceName.class);
                accept(modelNumber, "modelNumber", visitor);
                accept(classification, "classification", visitor, Classification.class);
                accept(conformsTo, "conformsTo", visitor, ConformsTo.class);
                accept(hasPart, "hasPart", visitor, HasPart.class);
                accept(packaging, "packaging", visitor, Packaging.class);
                accept(version, "version", visitor, Version.class);
                accept(safety, "safety", visitor, CodeableConcept.class);
                accept(shelfLifeStorage, "shelfLifeStorage", visitor, ProductShelfLife.class);
                accept(languageCode, "languageCode", visitor, CodeableConcept.class);
                accept(property, "property", visitor, Property.class);
                accept(owner, "owner", visitor);
                accept(contact, "contact", visitor, ContactPoint.class);
                accept(link, "link", visitor, Link.class);
                accept(note, "note", visitor, Annotation.class);
                accept(material, "material", visitor, Material.class);
                accept(productionIdentifierInUDI, "productionIdentifierInUDI", visitor, DeviceProductionIdentifierInUDI.class);
                accept(guideline, "guideline", visitor);
                accept(correctiveAction, "correctiveAction", visitor);
                accept(chargeItem, "chargeItem", visitor, ChargeItem.class);
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
        DeviceDefinition other = (DeviceDefinition) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(description, other.description) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(udiDeviceIdentifier, other.udiDeviceIdentifier) && 
            Objects.equals(regulatoryIdentifier, other.regulatoryIdentifier) && 
            Objects.equals(partNumber, other.partNumber) && 
            Objects.equals(manufacturer, other.manufacturer) && 
            Objects.equals(deviceName, other.deviceName) && 
            Objects.equals(modelNumber, other.modelNumber) && 
            Objects.equals(classification, other.classification) && 
            Objects.equals(conformsTo, other.conformsTo) && 
            Objects.equals(hasPart, other.hasPart) && 
            Objects.equals(packaging, other.packaging) && 
            Objects.equals(version, other.version) && 
            Objects.equals(safety, other.safety) && 
            Objects.equals(shelfLifeStorage, other.shelfLifeStorage) && 
            Objects.equals(languageCode, other.languageCode) && 
            Objects.equals(property, other.property) && 
            Objects.equals(owner, other.owner) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(link, other.link) && 
            Objects.equals(note, other.note) && 
            Objects.equals(material, other.material) && 
            Objects.equals(productionIdentifierInUDI, other.productionIdentifierInUDI) && 
            Objects.equals(guideline, other.guideline) && 
            Objects.equals(correctiveAction, other.correctiveAction) && 
            Objects.equals(chargeItem, other.chargeItem);
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
                description, 
                identifier, 
                udiDeviceIdentifier, 
                regulatoryIdentifier, 
                partNumber, 
                manufacturer, 
                deviceName, 
                modelNumber, 
                classification, 
                conformsTo, 
                hasPart, 
                packaging, 
                version, 
                safety, 
                shelfLifeStorage, 
                languageCode, 
                property, 
                owner, 
                contact, 
                link, 
                note, 
                material, 
                productionIdentifierInUDI, 
                guideline, 
                correctiveAction, 
                chargeItem);
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
        private Markdown description;
        private List<Identifier> identifier = new ArrayList<>();
        private List<UdiDeviceIdentifier> udiDeviceIdentifier = new ArrayList<>();
        private List<RegulatoryIdentifier> regulatoryIdentifier = new ArrayList<>();
        private String partNumber;
        private Reference manufacturer;
        private List<DeviceName> deviceName = new ArrayList<>();
        private String modelNumber;
        private List<Classification> classification = new ArrayList<>();
        private List<ConformsTo> conformsTo = new ArrayList<>();
        private List<HasPart> hasPart = new ArrayList<>();
        private List<Packaging> packaging = new ArrayList<>();
        private List<Version> version = new ArrayList<>();
        private List<CodeableConcept> safety = new ArrayList<>();
        private List<ProductShelfLife> shelfLifeStorage = new ArrayList<>();
        private List<CodeableConcept> languageCode = new ArrayList<>();
        private List<Property> property = new ArrayList<>();
        private Reference owner;
        private List<ContactPoint> contact = new ArrayList<>();
        private List<Link> link = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<Material> material = new ArrayList<>();
        private List<DeviceProductionIdentifierInUDI> productionIdentifierInUDI = new ArrayList<>();
        private Guideline guideline;
        private CorrectiveAction correctiveAction;
        private List<ChargeItem> chargeItem = new ArrayList<>();

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
         * Additional information to describe the device.
         * 
         * @param description
         *     Additional information to describe the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * Unique instance identifiers assigned to a device by the software, manufacturers, other organizations or owners. For 
         * example: handle ID. The identifier is typically valued if the udiDeviceIdentifier, partNumber or modelNumber is not 
         * valued and represents a different type of identifier. However, it is permissible to still include those identifiers in 
         * DeviceDefinition.identifier with the appropriate identifier.type.
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
         * Unique instance identifiers assigned to a device by the software, manufacturers, other organizations or owners. For 
         * example: handle ID. The identifier is typically valued if the udiDeviceIdentifier, partNumber or modelNumber is not 
         * valued and represents a different type of identifier. However, it is permissible to still include those identifiers in 
         * DeviceDefinition.identifier with the appropriate identifier.type.
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
         * Unique device identifier (UDI) assigned to device label or package. Note that the Device may include multiple 
         * udiCarriers as it either may include just the udiCarrier for the jurisdiction it is sold, or for multiple 
         * jurisdictions it could have been sold.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param udiDeviceIdentifier
         *     Unique Device Identifier (UDI) Barcode string
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder udiDeviceIdentifier(UdiDeviceIdentifier... udiDeviceIdentifier) {
            for (UdiDeviceIdentifier value : udiDeviceIdentifier) {
                this.udiDeviceIdentifier.add(value);
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
         * @param udiDeviceIdentifier
         *     Unique Device Identifier (UDI) Barcode string
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder udiDeviceIdentifier(Collection<UdiDeviceIdentifier> udiDeviceIdentifier) {
            this.udiDeviceIdentifier = new ArrayList<>(udiDeviceIdentifier);
            return this;
        }

        /**
         * Identifier associated with the regulatory documentation (certificates, technical documentation, post-market 
         * surveillance documentation and reports) of a set of device models sharing the same intended purpose, risk class and 
         * essential design and manufacturing characteristics. One example is the Basic UDI-DI in Europe.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param regulatoryIdentifier
         *     Regulatory identifier(s) associated with this device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder regulatoryIdentifier(RegulatoryIdentifier... regulatoryIdentifier) {
            for (RegulatoryIdentifier value : regulatoryIdentifier) {
                this.regulatoryIdentifier.add(value);
            }
            return this;
        }

        /**
         * Identifier associated with the regulatory documentation (certificates, technical documentation, post-market 
         * surveillance documentation and reports) of a set of device models sharing the same intended purpose, risk class and 
         * essential design and manufacturing characteristics. One example is the Basic UDI-DI in Europe.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param regulatoryIdentifier
         *     Regulatory identifier(s) associated with this device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder regulatoryIdentifier(Collection<RegulatoryIdentifier> regulatoryIdentifier) {
            this.regulatoryIdentifier = new ArrayList<>(regulatoryIdentifier);
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
         * A name of the manufacturer or legal representative e.g. labeler. Whether this is the actual manufacturer or the 
         * labeler or responsible depends on implementation and jurisdiction.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param manufacturer
         *     Name of device manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder manufacturer(Reference manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        /**
         * The name or names of the device as given by the manufacturer.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param deviceName
         *     The name or names of the device as given by the manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder deviceName(DeviceName... deviceName) {
            for (DeviceName value : deviceName) {
                this.deviceName.add(value);
            }
            return this;
        }

        /**
         * The name or names of the device as given by the manufacturer.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param deviceName
         *     The name or names of the device as given by the manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder deviceName(Collection<DeviceName> deviceName) {
            this.deviceName = new ArrayList<>(deviceName);
            return this;
        }

        /**
         * Convenience method for setting {@code modelNumber}.
         * 
         * @param modelNumber
         *     The catalog or model number for the device for example as defined by the manufacturer
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
         * The model number for the device for example as defined by the manufacturer or labeler, or other agency.
         * 
         * @param modelNumber
         *     The catalog or model number for the device for example as defined by the manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder modelNumber(String modelNumber) {
            this.modelNumber = modelNumber;
            return this;
        }

        /**
         * What kind of device or device system this is.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param classification
         *     What kind of device or device system this is
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder classification(Classification... classification) {
            for (Classification value : classification) {
                this.classification.add(value);
            }
            return this;
        }

        /**
         * What kind of device or device system this is.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param classification
         *     What kind of device or device system this is
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder classification(Collection<Classification> classification) {
            this.classification = new ArrayList<>(classification);
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
         * A device that is part (for example a component) of the present device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param hasPart
         *     A device, part of the current one
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder hasPart(HasPart... hasPart) {
            for (HasPart value : hasPart) {
                this.hasPart.add(value);
            }
            return this;
        }

        /**
         * A device that is part (for example a component) of the present device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param hasPart
         *     A device, part of the current one
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder hasPart(Collection<HasPart> hasPart) {
            this.hasPart = new ArrayList<>(hasPart);
            return this;
        }

        /**
         * Information about the packaging of the device, i.e. how the device is packaged.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param packaging
         *     Information about the packaging of the device, i.e. how the device is packaged
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder packaging(Packaging... packaging) {
            for (Packaging value : packaging) {
                this.packaging.add(value);
            }
            return this;
        }

        /**
         * Information about the packaging of the device, i.e. how the device is packaged.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param packaging
         *     Information about the packaging of the device, i.e. how the device is packaged
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder packaging(Collection<Packaging> packaging) {
            this.packaging = new ArrayList<>(packaging);
            return this;
        }

        /**
         * The version of the device or software.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param version
         *     The version of the device or software
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
         * The version of the device or software.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param version
         *     The version of the device or software
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
         * Safety characteristics of the device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param safety
         *     Safety characteristics of the device
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
         * Safety characteristics of the device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param safety
         *     Safety characteristics of the device
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
         * Shelf Life and storage information.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param shelfLifeStorage
         *     Shelf Life and storage information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder shelfLifeStorage(ProductShelfLife... shelfLifeStorage) {
            for (ProductShelfLife value : shelfLifeStorage) {
                this.shelfLifeStorage.add(value);
            }
            return this;
        }

        /**
         * Shelf Life and storage information.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param shelfLifeStorage
         *     Shelf Life and storage information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder shelfLifeStorage(Collection<ProductShelfLife> shelfLifeStorage) {
            this.shelfLifeStorage = new ArrayList<>(shelfLifeStorage);
            return this;
        }

        /**
         * Language code for the human-readable text strings produced by the device (all supported).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param languageCode
         *     Language code for the human-readable text strings produced by the device (all supported)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder languageCode(CodeableConcept... languageCode) {
            for (CodeableConcept value : languageCode) {
                this.languageCode.add(value);
            }
            return this;
        }

        /**
         * Language code for the human-readable text strings produced by the device (all supported).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param languageCode
         *     Language code for the human-readable text strings produced by the device (all supported)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder languageCode(Collection<CodeableConcept> languageCode) {
            this.languageCode = new ArrayList<>(languageCode);
            return this;
        }

        /**
         * Static or essentially fixed characteristics or features of this kind of device that are otherwise not captured in more 
         * specific attributes, e.g., time or timing attributes, resolution, accuracy, and physical attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Inherent, essentially fixed, characteristics of this kind of device, e.g., time properties, size, etc
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
         * Static or essentially fixed characteristics or features of this kind of device that are otherwise not captured in more 
         * specific attributes, e.g., time or timing attributes, resolution, accuracy, and physical attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Inherent, essentially fixed, characteristics of this kind of device, e.g., time properties, size, etc
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
         * An associated device, attached to, used with, communicating with or linking a previous or new device model to the 
         * focal device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param link
         *     An associated device, attached to, used with, communicating with or linking a previous or new device model to the 
         *     focal device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder link(Link... link) {
            for (Link value : link) {
                this.link.add(value);
            }
            return this;
        }

        /**
         * An associated device, attached to, used with, communicating with or linking a previous or new device model to the 
         * focal device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param link
         *     An associated device, attached to, used with, communicating with or linking a previous or new device model to the 
         *     focal device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder link(Collection<Link> link) {
            this.link = new ArrayList<>(link);
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
         * A substance used to create the material(s) of which the device is made.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param material
         *     A substance used to create the material(s) of which the device is made
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder material(Material... material) {
            for (Material value : material) {
                this.material.add(value);
            }
            return this;
        }

        /**
         * A substance used to create the material(s) of which the device is made.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param material
         *     A substance used to create the material(s) of which the device is made
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder material(Collection<Material> material) {
            this.material = new ArrayList<>(material);
            return this;
        }

        /**
         * Indicates the production identifier(s) that are expected to appear in the UDI carrier on the device label.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param productionIdentifierInUDI
         *     lot-number | manufactured-date | serial-number | expiration-date | biological-source | software-version
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder productionIdentifierInUDI(DeviceProductionIdentifierInUDI... productionIdentifierInUDI) {
            for (DeviceProductionIdentifierInUDI value : productionIdentifierInUDI) {
                this.productionIdentifierInUDI.add(value);
            }
            return this;
        }

        /**
         * Indicates the production identifier(s) that are expected to appear in the UDI carrier on the device label.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param productionIdentifierInUDI
         *     lot-number | manufactured-date | serial-number | expiration-date | biological-source | software-version
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder productionIdentifierInUDI(Collection<DeviceProductionIdentifierInUDI> productionIdentifierInUDI) {
            this.productionIdentifierInUDI = new ArrayList<>(productionIdentifierInUDI);
            return this;
        }

        /**
         * Information aimed at providing directions for the usage of this model of device.
         * 
         * @param guideline
         *     Information aimed at providing directions for the usage of this model of device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder guideline(Guideline guideline) {
            this.guideline = guideline;
            return this;
        }

        /**
         * Tracking of latest field safety corrective action.
         * 
         * @param correctiveAction
         *     Tracking of latest field safety corrective action
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder correctiveAction(CorrectiveAction correctiveAction) {
            this.correctiveAction = correctiveAction;
            return this;
        }

        /**
         * Billing code or reference associated with the device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param chargeItem
         *     Billing code or reference associated with the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder chargeItem(ChargeItem... chargeItem) {
            for (ChargeItem value : chargeItem) {
                this.chargeItem.add(value);
            }
            return this;
        }

        /**
         * Billing code or reference associated with the device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param chargeItem
         *     Billing code or reference associated with the device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder chargeItem(Collection<ChargeItem> chargeItem) {
            this.chargeItem = new ArrayList<>(chargeItem);
            return this;
        }

        /**
         * Build the {@link DeviceDefinition}
         * 
         * @return
         *     An immutable object of type {@link DeviceDefinition}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid DeviceDefinition per the base specification
         */
        @Override
        public DeviceDefinition build() {
            DeviceDefinition deviceDefinition = new DeviceDefinition(this);
            if (validating) {
                validate(deviceDefinition);
            }
            return deviceDefinition;
        }

        protected void validate(DeviceDefinition deviceDefinition) {
            super.validate(deviceDefinition);
            ValidationSupport.checkList(deviceDefinition.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(deviceDefinition.udiDeviceIdentifier, "udiDeviceIdentifier", UdiDeviceIdentifier.class);
            ValidationSupport.checkList(deviceDefinition.regulatoryIdentifier, "regulatoryIdentifier", RegulatoryIdentifier.class);
            ValidationSupport.checkList(deviceDefinition.deviceName, "deviceName", DeviceName.class);
            ValidationSupport.checkList(deviceDefinition.classification, "classification", Classification.class);
            ValidationSupport.checkList(deviceDefinition.conformsTo, "conformsTo", ConformsTo.class);
            ValidationSupport.checkList(deviceDefinition.hasPart, "hasPart", HasPart.class);
            ValidationSupport.checkList(deviceDefinition.packaging, "packaging", Packaging.class);
            ValidationSupport.checkList(deviceDefinition.version, "version", Version.class);
            ValidationSupport.checkList(deviceDefinition.safety, "safety", CodeableConcept.class);
            ValidationSupport.checkList(deviceDefinition.shelfLifeStorage, "shelfLifeStorage", ProductShelfLife.class);
            ValidationSupport.checkList(deviceDefinition.languageCode, "languageCode", CodeableConcept.class);
            ValidationSupport.checkList(deviceDefinition.property, "property", Property.class);
            ValidationSupport.checkList(deviceDefinition.contact, "contact", ContactPoint.class);
            ValidationSupport.checkList(deviceDefinition.link, "link", Link.class);
            ValidationSupport.checkList(deviceDefinition.note, "note", Annotation.class);
            ValidationSupport.checkList(deviceDefinition.material, "material", Material.class);
            ValidationSupport.checkList(deviceDefinition.productionIdentifierInUDI, "productionIdentifierInUDI", DeviceProductionIdentifierInUDI.class);
            ValidationSupport.checkList(deviceDefinition.chargeItem, "chargeItem", ChargeItem.class);
            ValidationSupport.checkReferenceType(deviceDefinition.manufacturer, "manufacturer", "Organization");
            ValidationSupport.checkReferenceType(deviceDefinition.owner, "owner", "Organization");
        }

        protected Builder from(DeviceDefinition deviceDefinition) {
            super.from(deviceDefinition);
            description = deviceDefinition.description;
            identifier.addAll(deviceDefinition.identifier);
            udiDeviceIdentifier.addAll(deviceDefinition.udiDeviceIdentifier);
            regulatoryIdentifier.addAll(deviceDefinition.regulatoryIdentifier);
            partNumber = deviceDefinition.partNumber;
            manufacturer = deviceDefinition.manufacturer;
            deviceName.addAll(deviceDefinition.deviceName);
            modelNumber = deviceDefinition.modelNumber;
            classification.addAll(deviceDefinition.classification);
            conformsTo.addAll(deviceDefinition.conformsTo);
            hasPart.addAll(deviceDefinition.hasPart);
            packaging.addAll(deviceDefinition.packaging);
            version.addAll(deviceDefinition.version);
            safety.addAll(deviceDefinition.safety);
            shelfLifeStorage.addAll(deviceDefinition.shelfLifeStorage);
            languageCode.addAll(deviceDefinition.languageCode);
            property.addAll(deviceDefinition.property);
            owner = deviceDefinition.owner;
            contact.addAll(deviceDefinition.contact);
            link.addAll(deviceDefinition.link);
            note.addAll(deviceDefinition.note);
            material.addAll(deviceDefinition.material);
            productionIdentifierInUDI.addAll(deviceDefinition.productionIdentifierInUDI);
            guideline = deviceDefinition.guideline;
            correctiveAction = deviceDefinition.correctiveAction;
            chargeItem.addAll(deviceDefinition.chargeItem);
            return this;
        }
    }

    /**
     * Unique device identifier (UDI) assigned to device label or package. Note that the Device may include multiple 
     * udiCarriers as it either may include just the udiCarrier for the jurisdiction it is sold, or for multiple 
     * jurisdictions it could have been sold.
     */
    public static class UdiDeviceIdentifier extends BackboneElement {
        @Required
        private final String deviceIdentifier;
        @Required
        private final Uri issuer;
        @Required
        private final Uri jurisdiction;
        private final List<MarketDistribution> marketDistribution;

        private UdiDeviceIdentifier(Builder builder) {
            super(builder);
            deviceIdentifier = builder.deviceIdentifier;
            issuer = builder.issuer;
            jurisdiction = builder.jurisdiction;
            marketDistribution = Collections.unmodifiableList(builder.marketDistribution);
        }

        /**
         * The identifier that is to be associated with every Device that references this DeviceDefintiion for the issuer and 
         * jurisdiction provided in the DeviceDefinition.udiDeviceIdentifier.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getDeviceIdentifier() {
            return deviceIdentifier;
        }

        /**
         * The organization that assigns the identifier algorithm.
         * 
         * @return
         *     An immutable object of type {@link Uri} that is non-null.
         */
        public Uri getIssuer() {
            return issuer;
        }

        /**
         * The jurisdiction to which the deviceIdentifier applies.
         * 
         * @return
         *     An immutable object of type {@link Uri} that is non-null.
         */
        public Uri getJurisdiction() {
            return jurisdiction;
        }

        /**
         * Indicates where and when the device is available on the market.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link MarketDistribution} that may be empty.
         */
        public List<MarketDistribution> getMarketDistribution() {
            return marketDistribution;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (deviceIdentifier != null) || 
                (issuer != null) || 
                (jurisdiction != null) || 
                !marketDistribution.isEmpty();
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
                    accept(marketDistribution, "marketDistribution", visitor, MarketDistribution.class);
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
            UdiDeviceIdentifier other = (UdiDeviceIdentifier) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(deviceIdentifier, other.deviceIdentifier) && 
                Objects.equals(issuer, other.issuer) && 
                Objects.equals(jurisdiction, other.jurisdiction) && 
                Objects.equals(marketDistribution, other.marketDistribution);
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
                    marketDistribution);
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
            private List<MarketDistribution> marketDistribution = new ArrayList<>();

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
             *     The identifier that is to be associated with every Device that references this DeviceDefintiion for the issuer and 
             *     jurisdiction provided in the DeviceDefinition.udiDeviceIdentifier
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
             * The identifier that is to be associated with every Device that references this DeviceDefintiion for the issuer and 
             * jurisdiction provided in the DeviceDefinition.udiDeviceIdentifier.
             * 
             * <p>This element is required.
             * 
             * @param deviceIdentifier
             *     The identifier that is to be associated with every Device that references this DeviceDefintiion for the issuer and 
             *     jurisdiction provided in the DeviceDefinition.udiDeviceIdentifier
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder deviceIdentifier(String deviceIdentifier) {
                this.deviceIdentifier = deviceIdentifier;
                return this;
            }

            /**
             * The organization that assigns the identifier algorithm.
             * 
             * <p>This element is required.
             * 
             * @param issuer
             *     The organization that assigns the identifier algorithm
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder issuer(Uri issuer) {
                this.issuer = issuer;
                return this;
            }

            /**
             * The jurisdiction to which the deviceIdentifier applies.
             * 
             * <p>This element is required.
             * 
             * @param jurisdiction
             *     The jurisdiction to which the deviceIdentifier applies
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder jurisdiction(Uri jurisdiction) {
                this.jurisdiction = jurisdiction;
                return this;
            }

            /**
             * Indicates where and when the device is available on the market.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param marketDistribution
             *     Indicates whether and when the device is available on the market
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder marketDistribution(MarketDistribution... marketDistribution) {
                for (MarketDistribution value : marketDistribution) {
                    this.marketDistribution.add(value);
                }
                return this;
            }

            /**
             * Indicates where and when the device is available on the market.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param marketDistribution
             *     Indicates whether and when the device is available on the market
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder marketDistribution(Collection<MarketDistribution> marketDistribution) {
                this.marketDistribution = new ArrayList<>(marketDistribution);
                return this;
            }

            /**
             * Build the {@link UdiDeviceIdentifier}
             * 
             * <p>Required elements:
             * <ul>
             * <li>deviceIdentifier</li>
             * <li>issuer</li>
             * <li>jurisdiction</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link UdiDeviceIdentifier}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid UdiDeviceIdentifier per the base specification
             */
            @Override
            public UdiDeviceIdentifier build() {
                UdiDeviceIdentifier udiDeviceIdentifier = new UdiDeviceIdentifier(this);
                if (validating) {
                    validate(udiDeviceIdentifier);
                }
                return udiDeviceIdentifier;
            }

            protected void validate(UdiDeviceIdentifier udiDeviceIdentifier) {
                super.validate(udiDeviceIdentifier);
                ValidationSupport.requireNonNull(udiDeviceIdentifier.deviceIdentifier, "deviceIdentifier");
                ValidationSupport.requireNonNull(udiDeviceIdentifier.issuer, "issuer");
                ValidationSupport.requireNonNull(udiDeviceIdentifier.jurisdiction, "jurisdiction");
                ValidationSupport.checkList(udiDeviceIdentifier.marketDistribution, "marketDistribution", MarketDistribution.class);
                ValidationSupport.requireValueOrChildren(udiDeviceIdentifier);
            }

            protected Builder from(UdiDeviceIdentifier udiDeviceIdentifier) {
                super.from(udiDeviceIdentifier);
                deviceIdentifier = udiDeviceIdentifier.deviceIdentifier;
                issuer = udiDeviceIdentifier.issuer;
                jurisdiction = udiDeviceIdentifier.jurisdiction;
                marketDistribution.addAll(udiDeviceIdentifier.marketDistribution);
                return this;
            }
        }

        /**
         * Indicates where and when the device is available on the market.
         */
        public static class MarketDistribution extends BackboneElement {
            @Required
            private final Period marketPeriod;
            @Required
            private final Uri subJurisdiction;

            private MarketDistribution(Builder builder) {
                super(builder);
                marketPeriod = builder.marketPeriod;
                subJurisdiction = builder.subJurisdiction;
            }

            /**
             * Begin and end dates for the commercial distribution of the device.
             * 
             * @return
             *     An immutable object of type {@link Period} that is non-null.
             */
            public Period getMarketPeriod() {
                return marketPeriod;
            }

            /**
             * National state or territory to which the marketDistribution recers, typically where the device is commercialized.
             * 
             * @return
             *     An immutable object of type {@link Uri} that is non-null.
             */
            public Uri getSubJurisdiction() {
                return subJurisdiction;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (marketPeriod != null) || 
                    (subJurisdiction != null);
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
                        accept(marketPeriod, "marketPeriod", visitor);
                        accept(subJurisdiction, "subJurisdiction", visitor);
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
                MarketDistribution other = (MarketDistribution) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(marketPeriod, other.marketPeriod) && 
                    Objects.equals(subJurisdiction, other.subJurisdiction);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        marketPeriod, 
                        subJurisdiction);
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
                private Period marketPeriod;
                private Uri subJurisdiction;

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
                 * Begin and end dates for the commercial distribution of the device.
                 * 
                 * <p>This element is required.
                 * 
                 * @param marketPeriod
                 *     Begin and end dates for the commercial distribution of the device
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder marketPeriod(Period marketPeriod) {
                    this.marketPeriod = marketPeriod;
                    return this;
                }

                /**
                 * National state or territory to which the marketDistribution recers, typically where the device is commercialized.
                 * 
                 * <p>This element is required.
                 * 
                 * @param subJurisdiction
                 *     National state or territory where the device is commercialized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder subJurisdiction(Uri subJurisdiction) {
                    this.subJurisdiction = subJurisdiction;
                    return this;
                }

                /**
                 * Build the {@link MarketDistribution}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>marketPeriod</li>
                 * <li>subJurisdiction</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link MarketDistribution}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid MarketDistribution per the base specification
                 */
                @Override
                public MarketDistribution build() {
                    MarketDistribution marketDistribution = new MarketDistribution(this);
                    if (validating) {
                        validate(marketDistribution);
                    }
                    return marketDistribution;
                }

                protected void validate(MarketDistribution marketDistribution) {
                    super.validate(marketDistribution);
                    ValidationSupport.requireNonNull(marketDistribution.marketPeriod, "marketPeriod");
                    ValidationSupport.requireNonNull(marketDistribution.subJurisdiction, "subJurisdiction");
                    ValidationSupport.requireValueOrChildren(marketDistribution);
                }

                protected Builder from(MarketDistribution marketDistribution) {
                    super.from(marketDistribution);
                    marketPeriod = marketDistribution.marketPeriod;
                    subJurisdiction = marketDistribution.subJurisdiction;
                    return this;
                }
            }
        }
    }

    /**
     * Identifier associated with the regulatory documentation (certificates, technical documentation, post-market 
     * surveillance documentation and reports) of a set of device models sharing the same intended purpose, risk class and 
     * essential design and manufacturing characteristics. One example is the Basic UDI-DI in Europe.
     */
    public static class RegulatoryIdentifier extends BackboneElement {
        @Binding(
            bindingName = "DeviceRegulatoryIdentifierType",
            strength = BindingStrength.Value.REQUIRED,
            description = "Device regulatory identifier type.",
            valueSet = "http://hl7.org/fhir/ValueSet/devicedefinition-regulatory-identifier-type|5.0.0"
        )
        @Required
        private final DeviceRegulatoryIdentifierType type;
        @Required
        private final String deviceIdentifier;
        @Required
        private final Uri issuer;
        @Required
        private final Uri jurisdiction;

        private RegulatoryIdentifier(Builder builder) {
            super(builder);
            type = builder.type;
            deviceIdentifier = builder.deviceIdentifier;
            issuer = builder.issuer;
            jurisdiction = builder.jurisdiction;
        }

        /**
         * The type of identifier itself.
         * 
         * @return
         *     An immutable object of type {@link DeviceRegulatoryIdentifierType} that is non-null.
         */
        public DeviceRegulatoryIdentifierType getType() {
            return type;
        }

        /**
         * The identifier itself.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getDeviceIdentifier() {
            return deviceIdentifier;
        }

        /**
         * The organization that issued this identifier.
         * 
         * @return
         *     An immutable object of type {@link Uri} that is non-null.
         */
        public Uri getIssuer() {
            return issuer;
        }

        /**
         * The jurisdiction to which the deviceIdentifier applies.
         * 
         * @return
         *     An immutable object of type {@link Uri} that is non-null.
         */
        public Uri getJurisdiction() {
            return jurisdiction;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (deviceIdentifier != null) || 
                (issuer != null) || 
                (jurisdiction != null);
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
                    accept(deviceIdentifier, "deviceIdentifier", visitor);
                    accept(issuer, "issuer", visitor);
                    accept(jurisdiction, "jurisdiction", visitor);
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
            RegulatoryIdentifier other = (RegulatoryIdentifier) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(deviceIdentifier, other.deviceIdentifier) && 
                Objects.equals(issuer, other.issuer) && 
                Objects.equals(jurisdiction, other.jurisdiction);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    deviceIdentifier, 
                    issuer, 
                    jurisdiction);
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
            private DeviceRegulatoryIdentifierType type;
            private String deviceIdentifier;
            private Uri issuer;
            private Uri jurisdiction;

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
             * The type of identifier itself.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     basic | master | license
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(DeviceRegulatoryIdentifierType type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code deviceIdentifier}.
             * 
             * <p>This element is required.
             * 
             * @param deviceIdentifier
             *     The identifier itself
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
             * The identifier itself.
             * 
             * <p>This element is required.
             * 
             * @param deviceIdentifier
             *     The identifier itself
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder deviceIdentifier(String deviceIdentifier) {
                this.deviceIdentifier = deviceIdentifier;
                return this;
            }

            /**
             * The organization that issued this identifier.
             * 
             * <p>This element is required.
             * 
             * @param issuer
             *     The organization that issued this identifier
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder issuer(Uri issuer) {
                this.issuer = issuer;
                return this;
            }

            /**
             * The jurisdiction to which the deviceIdentifier applies.
             * 
             * <p>This element is required.
             * 
             * @param jurisdiction
             *     The jurisdiction to which the deviceIdentifier applies
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder jurisdiction(Uri jurisdiction) {
                this.jurisdiction = jurisdiction;
                return this;
            }

            /**
             * Build the {@link RegulatoryIdentifier}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>deviceIdentifier</li>
             * <li>issuer</li>
             * <li>jurisdiction</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link RegulatoryIdentifier}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid RegulatoryIdentifier per the base specification
             */
            @Override
            public RegulatoryIdentifier build() {
                RegulatoryIdentifier regulatoryIdentifier = new RegulatoryIdentifier(this);
                if (validating) {
                    validate(regulatoryIdentifier);
                }
                return regulatoryIdentifier;
            }

            protected void validate(RegulatoryIdentifier regulatoryIdentifier) {
                super.validate(regulatoryIdentifier);
                ValidationSupport.requireNonNull(regulatoryIdentifier.type, "type");
                ValidationSupport.requireNonNull(regulatoryIdentifier.deviceIdentifier, "deviceIdentifier");
                ValidationSupport.requireNonNull(regulatoryIdentifier.issuer, "issuer");
                ValidationSupport.requireNonNull(regulatoryIdentifier.jurisdiction, "jurisdiction");
                ValidationSupport.requireValueOrChildren(regulatoryIdentifier);
            }

            protected Builder from(RegulatoryIdentifier regulatoryIdentifier) {
                super.from(regulatoryIdentifier);
                type = regulatoryIdentifier.type;
                deviceIdentifier = regulatoryIdentifier.deviceIdentifier;
                issuer = regulatoryIdentifier.issuer;
                jurisdiction = regulatoryIdentifier.jurisdiction;
                return this;
            }
        }
    }

    /**
     * The name or names of the device as given by the manufacturer.
     */
    public static class DeviceName extends BackboneElement {
        @Summary
        @Required
        private final String name;
        @Summary
        @Binding(
            bindingName = "DeviceNameType",
            strength = BindingStrength.Value.REQUIRED,
            description = "The type of name the device is referred by.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-nametype|5.0.0"
        )
        @Required
        private final DeviceNameType type;

        private DeviceName(Builder builder) {
            super(builder);
            name = builder.name;
            type = builder.type;
        }

        /**
         * A human-friendly name that is used to refer to the device - depending on the type, it can be the brand name, the 
         * common name or alias, or other.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getName() {
            return name;
        }

        /**
         * The type of deviceName.
RegisteredName | UserFriendlyName | PatientReportedName.
         * 
         * @return
         *     An immutable object of type {@link DeviceNameType} that is non-null.
         */
        public DeviceNameType getType() {
            return type;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (name != null) || 
                (type != null);
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
                    accept(name, "name", visitor);
                    accept(type, "type", visitor);
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
            DeviceName other = (DeviceName) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(name, other.name) && 
                Objects.equals(type, other.type);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    name, 
                    type);
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
            private String name;
            private DeviceNameType type;

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
             * Convenience method for setting {@code name}.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     A name that is used to refer to the device
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
             * A human-friendly name that is used to refer to the device - depending on the type, it can be the brand name, the 
             * common name or alias, or other.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     A name that is used to refer to the device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * The type of deviceName.
RegisteredName | UserFriendlyName | PatientReportedName.
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
             * Build the {@link DeviceName}
             * 
             * <p>Required elements:
             * <ul>
             * <li>name</li>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link DeviceName}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid DeviceName per the base specification
             */
            @Override
            public DeviceName build() {
                DeviceName deviceName = new DeviceName(this);
                if (validating) {
                    validate(deviceName);
                }
                return deviceName;
            }

            protected void validate(DeviceName deviceName) {
                super.validate(deviceName);
                ValidationSupport.requireNonNull(deviceName.name, "name");
                ValidationSupport.requireNonNull(deviceName.type, "type");
                ValidationSupport.requireValueOrChildren(deviceName);
            }

            protected Builder from(DeviceName deviceName) {
                super.from(deviceName);
                name = deviceName.name;
                type = deviceName.type;
                return this;
            }
        }
    }

    /**
     * What kind of device or device system this is.
     */
    public static class Classification extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "DeviceKind",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Type of device e.g. according to official classification.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-type"
        )
        @Required
        private final CodeableConcept type;
        private final List<RelatedArtifact> justification;

        private Classification(Builder builder) {
            super(builder);
            type = builder.type;
            justification = Collections.unmodifiableList(builder.justification);
        }

        /**
         * A classification or risk class of the device model.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Further information qualifying this classification of the device model.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
         */
        public List<RelatedArtifact> getJustification() {
            return justification;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                !justification.isEmpty();
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
                    accept(justification, "justification", visitor, RelatedArtifact.class);
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
            Classification other = (Classification) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(justification, other.justification);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    justification);
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
            private List<RelatedArtifact> justification = new ArrayList<>();

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
             * A classification or risk class of the device model.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     A classification or risk class of the device model
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Further information qualifying this classification of the device model.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param justification
             *     Further information qualifying this classification of the device model
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder justification(RelatedArtifact... justification) {
                for (RelatedArtifact value : justification) {
                    this.justification.add(value);
                }
                return this;
            }

            /**
             * Further information qualifying this classification of the device model.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param justification
             *     Further information qualifying this classification of the device model
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder justification(Collection<RelatedArtifact> justification) {
                this.justification = new ArrayList<>(justification);
                return this;
            }

            /**
             * Build the {@link Classification}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Classification}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Classification per the base specification
             */
            @Override
            public Classification build() {
                Classification classification = new Classification(this);
                if (validating) {
                    validate(classification);
                }
                return classification;
            }

            protected void validate(Classification classification) {
                super.validate(classification);
                ValidationSupport.requireNonNull(classification.type, "type");
                ValidationSupport.checkList(classification.justification, "justification", RelatedArtifact.class);
                ValidationSupport.requireValueOrChildren(classification);
            }

            protected Builder from(Classification classification) {
                super.from(classification);
                type = classification.type;
                justification.addAll(classification.justification);
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
        @Summary
        @Binding(
            bindingName = "DeviceSpecificationCategory",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/device-specification-category"
        )
        private final CodeableConcept category;
        @Summary
        @Binding(
            bindingName = "DeviceSpecificationType",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/device-specification-type"
        )
        @Required
        private final CodeableConcept specification;
        @Summary
        private final List<String> version;
        private final List<RelatedArtifact> source;

        private ConformsTo(Builder builder) {
            super(builder);
            category = builder.category;
            specification = builder.specification;
            version = Collections.unmodifiableList(builder.version);
            source = Collections.unmodifiableList(builder.source);
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
         *     An unmodifiable list containing immutable objects of type {@link String} that may be empty.
         */
        public List<String> getVersion() {
            return version;
        }

        /**
         * Standard, regulation, certification, or guidance website, document, or other publication, or similar, supporting the 
         * conformance.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
         */
        public List<RelatedArtifact> getSource() {
            return source;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (category != null) || 
                (specification != null) || 
                !version.isEmpty() || 
                !source.isEmpty();
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
                    accept(version, "version", visitor, String.class);
                    accept(source, "source", visitor, RelatedArtifact.class);
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
                Objects.equals(version, other.version) && 
                Objects.equals(source, other.source);
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
                    version, 
                    source);
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
            private List<String> version = new ArrayList<>();
            private List<RelatedArtifact> source = new ArrayList<>();

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
             *     Describes the common type of the standard, specification, or formal guidance
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
             *     Identifies the standard, specification, or formal guidance that the device adheres to the Device Specification type
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
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param version
             *     The specific form or variant of the standard, specification or formal guidance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #version(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder version(java.lang.String... version) {
                for (java.lang.String value : version) {
                    this.version.add((value == null) ? null : String.of(value));
                }
                return this;
            }

            /**
             * Identifies the specific form or variant of the standard, specification, or formal guidance. This may be a 'version 
             * number', release, document edition, publication year, or other label.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param version
             *     The specific form or variant of the standard, specification or formal guidance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder version(String... version) {
                for (String value : version) {
                    this.version.add(value);
                }
                return this;
            }

            /**
             * Identifies the specific form or variant of the standard, specification, or formal guidance. This may be a 'version 
             * number', release, document edition, publication year, or other label.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param version
             *     The specific form or variant of the standard, specification or formal guidance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder version(Collection<String> version) {
                this.version = new ArrayList<>(version);
                return this;
            }

            /**
             * Standard, regulation, certification, or guidance website, document, or other publication, or similar, supporting the 
             * conformance.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param source
             *     Standard, regulation, certification, or guidance website, document, or other publication, or similar, supporting the 
             *     conformance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder source(RelatedArtifact... source) {
                for (RelatedArtifact value : source) {
                    this.source.add(value);
                }
                return this;
            }

            /**
             * Standard, regulation, certification, or guidance website, document, or other publication, or similar, supporting the 
             * conformance.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param source
             *     Standard, regulation, certification, or guidance website, document, or other publication, or similar, supporting the 
             *     conformance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder source(Collection<RelatedArtifact> source) {
                this.source = new ArrayList<>(source);
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
                ValidationSupport.checkList(conformsTo.version, "version", String.class);
                ValidationSupport.checkList(conformsTo.source, "source", RelatedArtifact.class);
                ValidationSupport.requireValueOrChildren(conformsTo);
            }

            protected Builder from(ConformsTo conformsTo) {
                super.from(conformsTo);
                category = conformsTo.category;
                specification = conformsTo.specification;
                version.addAll(conformsTo.version);
                source.addAll(conformsTo.source);
                return this;
            }
        }
    }

    /**
     * A device that is part (for example a component) of the present device.
     */
    public static class HasPart extends BackboneElement {
        @Summary
        @ReferenceTarget({ "DeviceDefinition" })
        @Required
        private final Reference reference;
        private final Integer count;

        private HasPart(Builder builder) {
            super(builder);
            reference = builder.reference;
            count = builder.count;
        }

        /**
         * Reference to the device that is part of the current device.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getReference() {
            return reference;
        }

        /**
         * Number of instances of the component device in the current device.
         * 
         * @return
         *     An immutable object of type {@link Integer} that may be null.
         */
        public Integer getCount() {
            return count;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (reference != null) || 
                (count != null);
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
                    accept(reference, "reference", visitor);
                    accept(count, "count", visitor);
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
            HasPart other = (HasPart) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(reference, other.reference) && 
                Objects.equals(count, other.count);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    reference, 
                    count);
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
            private Reference reference;
            private Integer count;

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
             * Reference to the device that is part of the current device.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link DeviceDefinition}</li>
             * </ul>
             * 
             * @param reference
             *     Reference to the part
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reference(Reference reference) {
                this.reference = reference;
                return this;
            }

            /**
             * Convenience method for setting {@code count}.
             * 
             * @param count
             *     Number of occurrences of the part
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #count(org.linuxforhealth.fhir.model.type.Integer)
             */
            public Builder count(java.lang.Integer count) {
                this.count = (count == null) ? null : Integer.of(count);
                return this;
            }

            /**
             * Number of instances of the component device in the current device.
             * 
             * @param count
             *     Number of occurrences of the part
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder count(Integer count) {
                this.count = count;
                return this;
            }

            /**
             * Build the {@link HasPart}
             * 
             * <p>Required elements:
             * <ul>
             * <li>reference</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link HasPart}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid HasPart per the base specification
             */
            @Override
            public HasPart build() {
                HasPart hasPart = new HasPart(this);
                if (validating) {
                    validate(hasPart);
                }
                return hasPart;
            }

            protected void validate(HasPart hasPart) {
                super.validate(hasPart);
                ValidationSupport.requireNonNull(hasPart.reference, "reference");
                ValidationSupport.checkReferenceType(hasPart.reference, "reference", "DeviceDefinition");
                ValidationSupport.requireValueOrChildren(hasPart);
            }

            protected Builder from(HasPart hasPart) {
                super.from(hasPart);
                reference = hasPart.reference;
                count = hasPart.count;
                return this;
            }
        }
    }

    /**
     * Information about the packaging of the device, i.e. how the device is packaged.
     */
    public static class Packaging extends BackboneElement {
        private final Identifier identifier;
        private final CodeableConcept type;
        private final Integer count;
        private final List<Distributor> distributor;
        private final List<DeviceDefinition.UdiDeviceIdentifier> udiDeviceIdentifier;
        private final List<DeviceDefinition.Packaging> packaging;

        private Packaging(Builder builder) {
            super(builder);
            identifier = builder.identifier;
            type = builder.type;
            count = builder.count;
            distributor = Collections.unmodifiableList(builder.distributor);
            udiDeviceIdentifier = Collections.unmodifiableList(builder.udiDeviceIdentifier);
            packaging = Collections.unmodifiableList(builder.packaging);
        }

        /**
         * The business identifier of the packaged medication.
         * 
         * @return
         *     An immutable object of type {@link Identifier} that may be null.
         */
        public Identifier getIdentifier() {
            return identifier;
        }

        /**
         * A code that defines the specific type of packaging.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The number of items contained in the package (devices or sub-packages).
         * 
         * @return
         *     An immutable object of type {@link Integer} that may be null.
         */
        public Integer getCount() {
            return count;
        }

        /**
         * An organization that distributes the packaged device.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Distributor} that may be empty.
         */
        public List<Distributor> getDistributor() {
            return distributor;
        }

        /**
         * Unique Device Identifier (UDI) Barcode string on the packaging.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link UdiDeviceIdentifier} that may be empty.
         */
        public List<DeviceDefinition.UdiDeviceIdentifier> getUdiDeviceIdentifier() {
            return udiDeviceIdentifier;
        }

        /**
         * Allows packages within packages.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Packaging} that may be empty.
         */
        public List<DeviceDefinition.Packaging> getPackaging() {
            return packaging;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (identifier != null) || 
                (type != null) || 
                (count != null) || 
                !distributor.isEmpty() || 
                !udiDeviceIdentifier.isEmpty() || 
                !packaging.isEmpty();
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
                    accept(identifier, "identifier", visitor);
                    accept(type, "type", visitor);
                    accept(count, "count", visitor);
                    accept(distributor, "distributor", visitor, Distributor.class);
                    accept(udiDeviceIdentifier, "udiDeviceIdentifier", visitor, DeviceDefinition.UdiDeviceIdentifier.class);
                    accept(packaging, "packaging", visitor, DeviceDefinition.Packaging.class);
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
            Packaging other = (Packaging) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(identifier, other.identifier) && 
                Objects.equals(type, other.type) && 
                Objects.equals(count, other.count) && 
                Objects.equals(distributor, other.distributor) && 
                Objects.equals(udiDeviceIdentifier, other.udiDeviceIdentifier) && 
                Objects.equals(packaging, other.packaging);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    identifier, 
                    type, 
                    count, 
                    distributor, 
                    udiDeviceIdentifier, 
                    packaging);
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
            private Identifier identifier;
            private CodeableConcept type;
            private Integer count;
            private List<Distributor> distributor = new ArrayList<>();
            private List<DeviceDefinition.UdiDeviceIdentifier> udiDeviceIdentifier = new ArrayList<>();
            private List<DeviceDefinition.Packaging> packaging = new ArrayList<>();

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
             * The business identifier of the packaged medication.
             * 
             * @param identifier
             *     Business identifier of the packaged medication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder identifier(Identifier identifier) {
                this.identifier = identifier;
                return this;
            }

            /**
             * A code that defines the specific type of packaging.
             * 
             * @param type
             *     A code that defines the specific type of packaging
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code count}.
             * 
             * @param count
             *     The number of items contained in the package (devices or sub-packages)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #count(org.linuxforhealth.fhir.model.type.Integer)
             */
            public Builder count(java.lang.Integer count) {
                this.count = (count == null) ? null : Integer.of(count);
                return this;
            }

            /**
             * The number of items contained in the package (devices or sub-packages).
             * 
             * @param count
             *     The number of items contained in the package (devices or sub-packages)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder count(Integer count) {
                this.count = count;
                return this;
            }

            /**
             * An organization that distributes the packaged device.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param distributor
             *     An organization that distributes the packaged device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder distributor(Distributor... distributor) {
                for (Distributor value : distributor) {
                    this.distributor.add(value);
                }
                return this;
            }

            /**
             * An organization that distributes the packaged device.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param distributor
             *     An organization that distributes the packaged device
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder distributor(Collection<Distributor> distributor) {
                this.distributor = new ArrayList<>(distributor);
                return this;
            }

            /**
             * Unique Device Identifier (UDI) Barcode string on the packaging.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param udiDeviceIdentifier
             *     Unique Device Identifier (UDI) Barcode string on the packaging
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder udiDeviceIdentifier(DeviceDefinition.UdiDeviceIdentifier... udiDeviceIdentifier) {
                for (DeviceDefinition.UdiDeviceIdentifier value : udiDeviceIdentifier) {
                    this.udiDeviceIdentifier.add(value);
                }
                return this;
            }

            /**
             * Unique Device Identifier (UDI) Barcode string on the packaging.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param udiDeviceIdentifier
             *     Unique Device Identifier (UDI) Barcode string on the packaging
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder udiDeviceIdentifier(Collection<DeviceDefinition.UdiDeviceIdentifier> udiDeviceIdentifier) {
                this.udiDeviceIdentifier = new ArrayList<>(udiDeviceIdentifier);
                return this;
            }

            /**
             * Allows packages within packages.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param packaging
             *     Allows packages within packages
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder packaging(DeviceDefinition.Packaging... packaging) {
                for (DeviceDefinition.Packaging value : packaging) {
                    this.packaging.add(value);
                }
                return this;
            }

            /**
             * Allows packages within packages.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param packaging
             *     Allows packages within packages
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder packaging(Collection<DeviceDefinition.Packaging> packaging) {
                this.packaging = new ArrayList<>(packaging);
                return this;
            }

            /**
             * Build the {@link Packaging}
             * 
             * @return
             *     An immutable object of type {@link Packaging}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Packaging per the base specification
             */
            @Override
            public Packaging build() {
                Packaging packaging = new Packaging(this);
                if (validating) {
                    validate(packaging);
                }
                return packaging;
            }

            protected void validate(Packaging packaging) {
                super.validate(packaging);
                ValidationSupport.checkList(packaging.distributor, "distributor", Distributor.class);
                ValidationSupport.checkList(packaging.udiDeviceIdentifier, "udiDeviceIdentifier", DeviceDefinition.UdiDeviceIdentifier.class);
                ValidationSupport.checkList(packaging.packaging, "packaging", DeviceDefinition.Packaging.class);
                ValidationSupport.requireValueOrChildren(packaging);
            }

            protected Builder from(Packaging packaging) {
                super.from(packaging);
                identifier = packaging.identifier;
                type = packaging.type;
                count = packaging.count;
                distributor.addAll(packaging.distributor);
                udiDeviceIdentifier.addAll(packaging.udiDeviceIdentifier);
                this.packaging.addAll(packaging.packaging);
                return this;
            }
        }

        /**
         * An organization that distributes the packaged device.
         */
        public static class Distributor extends BackboneElement {
            private final String name;
            @ReferenceTarget({ "Organization" })
            private final List<Reference> organizationReference;

            private Distributor(Builder builder) {
                super(builder);
                name = builder.name;
                organizationReference = Collections.unmodifiableList(builder.organizationReference);
            }

            /**
             * Distributor's human-readable name.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getName() {
                return name;
            }

            /**
             * Distributor as an Organization resource.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
             */
            public List<Reference> getOrganizationReference() {
                return organizationReference;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (name != null) || 
                    !organizationReference.isEmpty();
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
                        accept(name, "name", visitor);
                        accept(organizationReference, "organizationReference", visitor, Reference.class);
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
                Distributor other = (Distributor) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(name, other.name) && 
                    Objects.equals(organizationReference, other.organizationReference);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        name, 
                        organizationReference);
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
                private String name;
                private List<Reference> organizationReference = new ArrayList<>();

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
                 * Convenience method for setting {@code name}.
                 * 
                 * @param name
                 *     Distributor's human-readable name
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
                 * Distributor's human-readable name.
                 * 
                 * @param name
                 *     Distributor's human-readable name
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder name(String name) {
                    this.name = name;
                    return this;
                }

                /**
                 * Distributor as an Organization resource.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
                 * <ul>
                 * <li>{@link Organization}</li>
                 * </ul>
                 * 
                 * @param organizationReference
                 *     Distributor as an Organization resource
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder organizationReference(Reference... organizationReference) {
                    for (Reference value : organizationReference) {
                        this.organizationReference.add(value);
                    }
                    return this;
                }

                /**
                 * Distributor as an Organization resource.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
                 * <ul>
                 * <li>{@link Organization}</li>
                 * </ul>
                 * 
                 * @param organizationReference
                 *     Distributor as an Organization resource
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder organizationReference(Collection<Reference> organizationReference) {
                    this.organizationReference = new ArrayList<>(organizationReference);
                    return this;
                }

                /**
                 * Build the {@link Distributor}
                 * 
                 * @return
                 *     An immutable object of type {@link Distributor}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Distributor per the base specification
                 */
                @Override
                public Distributor build() {
                    Distributor distributor = new Distributor(this);
                    if (validating) {
                        validate(distributor);
                    }
                    return distributor;
                }

                protected void validate(Distributor distributor) {
                    super.validate(distributor);
                    ValidationSupport.checkList(distributor.organizationReference, "organizationReference", Reference.class);
                    ValidationSupport.checkReferenceType(distributor.organizationReference, "organizationReference", "Organization");
                    ValidationSupport.requireValueOrChildren(distributor);
                }

                protected Builder from(Distributor distributor) {
                    super.from(distributor);
                    name = distributor.name;
                    organizationReference.addAll(distributor.organizationReference);
                    return this;
                }
            }
        }
    }

    /**
     * The version of the device or software.
     */
    public static class Version extends BackboneElement {
        private final CodeableConcept type;
        private final Identifier component;
        @Required
        private final String value;

        private Version(Builder builder) {
            super(builder);
            type = builder.type;
            component = builder.component;
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
                value = version.value;
                return this;
            }
        }
    }

    /**
     * Static or essentially fixed characteristics or features of this kind of device that are otherwise not captured in more 
     * specific attributes, e.g., time or timing attributes, resolution, accuracy, and physical attributes.
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
         * Code that specifies the property such as a resolution or color being represented.
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
             * Code that specifies the property such as a resolution or color being represented.
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

    /**
     * An associated device, attached to, used with, communicating with or linking a previous or new device model to the 
     * focal device.
     */
    public static class Link extends BackboneElement {
        @Binding(
            bindingName = "DeviceDefinitionRelationType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The type of relation between this and the linked device.",
            valueSet = "http://hl7.org/fhir/ValueSet/devicedefinition-relationtype"
        )
        @Required
        private final Coding relation;
        @Required
        private final CodeableReference relatedDevice;

        private Link(Builder builder) {
            super(builder);
            relation = builder.relation;
            relatedDevice = builder.relatedDevice;
        }

        /**
         * The type indicates the relationship of the related device to the device instance.
         * 
         * @return
         *     An immutable object of type {@link Coding} that is non-null.
         */
        public Coding getRelation() {
            return relation;
        }

        /**
         * A reference to the linked device.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getRelatedDevice() {
            return relatedDevice;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (relation != null) || 
                (relatedDevice != null);
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
                    accept(relation, "relation", visitor);
                    accept(relatedDevice, "relatedDevice", visitor);
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
            Link other = (Link) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(relation, other.relation) && 
                Objects.equals(relatedDevice, other.relatedDevice);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    relation, 
                    relatedDevice);
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
            private Coding relation;
            private CodeableReference relatedDevice;

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
             * The type indicates the relationship of the related device to the device instance.
             * 
             * <p>This element is required.
             * 
             * @param relation
             *     The type indicates the relationship of the related device to the device instance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relation(Coding relation) {
                this.relation = relation;
                return this;
            }

            /**
             * A reference to the linked device.
             * 
             * <p>This element is required.
             * 
             * @param relatedDevice
             *     A reference to the linked device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relatedDevice(CodeableReference relatedDevice) {
                this.relatedDevice = relatedDevice;
                return this;
            }

            /**
             * Build the {@link Link}
             * 
             * <p>Required elements:
             * <ul>
             * <li>relation</li>
             * <li>relatedDevice</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Link}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Link per the base specification
             */
            @Override
            public Link build() {
                Link link = new Link(this);
                if (validating) {
                    validate(link);
                }
                return link;
            }

            protected void validate(Link link) {
                super.validate(link);
                ValidationSupport.requireNonNull(link.relation, "relation");
                ValidationSupport.requireNonNull(link.relatedDevice, "relatedDevice");
                ValidationSupport.requireValueOrChildren(link);
            }

            protected Builder from(Link link) {
                super.from(link);
                relation = link.relation;
                relatedDevice = link.relatedDevice;
                return this;
            }
        }
    }

    /**
     * A substance used to create the material(s) of which the device is made.
     */
    public static class Material extends BackboneElement {
        @Required
        private final CodeableConcept substance;
        private final Boolean alternate;
        private final Boolean allergenicIndicator;

        private Material(Builder builder) {
            super(builder);
            substance = builder.substance;
            alternate = builder.alternate;
            allergenicIndicator = builder.allergenicIndicator;
        }

        /**
         * A substance that the device contains, may contain, or is made of - for example latex - to be used to determine patient 
         * compatibility. This is not intended to represent the composition of the device, only the clinically relevant materials.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getSubstance() {
            return substance;
        }

        /**
         * Indicates an alternative material of the device.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getAlternate() {
            return alternate;
        }

        /**
         * Whether the substance is a known or suspected allergen.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getAllergenicIndicator() {
            return allergenicIndicator;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (substance != null) || 
                (alternate != null) || 
                (allergenicIndicator != null);
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
                    accept(substance, "substance", visitor);
                    accept(alternate, "alternate", visitor);
                    accept(allergenicIndicator, "allergenicIndicator", visitor);
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
            Material other = (Material) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(substance, other.substance) && 
                Objects.equals(alternate, other.alternate) && 
                Objects.equals(allergenicIndicator, other.allergenicIndicator);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    substance, 
                    alternate, 
                    allergenicIndicator);
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
            private CodeableConcept substance;
            private Boolean alternate;
            private Boolean allergenicIndicator;

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
             * A substance that the device contains, may contain, or is made of - for example latex - to be used to determine patient 
             * compatibility. This is not intended to represent the composition of the device, only the clinically relevant materials.
             * 
             * <p>This element is required.
             * 
             * @param substance
             *     A relevant substance that the device contains, may contain, or is made of
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder substance(CodeableConcept substance) {
                this.substance = substance;
                return this;
            }

            /**
             * Convenience method for setting {@code alternate}.
             * 
             * @param alternate
             *     Indicates an alternative material of the device
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #alternate(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder alternate(java.lang.Boolean alternate) {
                this.alternate = (alternate == null) ? null : Boolean.of(alternate);
                return this;
            }

            /**
             * Indicates an alternative material of the device.
             * 
             * @param alternate
             *     Indicates an alternative material of the device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder alternate(Boolean alternate) {
                this.alternate = alternate;
                return this;
            }

            /**
             * Convenience method for setting {@code allergenicIndicator}.
             * 
             * @param allergenicIndicator
             *     Whether the substance is a known or suspected allergen
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #allergenicIndicator(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder allergenicIndicator(java.lang.Boolean allergenicIndicator) {
                this.allergenicIndicator = (allergenicIndicator == null) ? null : Boolean.of(allergenicIndicator);
                return this;
            }

            /**
             * Whether the substance is a known or suspected allergen.
             * 
             * @param allergenicIndicator
             *     Whether the substance is a known or suspected allergen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder allergenicIndicator(Boolean allergenicIndicator) {
                this.allergenicIndicator = allergenicIndicator;
                return this;
            }

            /**
             * Build the {@link Material}
             * 
             * <p>Required elements:
             * <ul>
             * <li>substance</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Material}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Material per the base specification
             */
            @Override
            public Material build() {
                Material material = new Material(this);
                if (validating) {
                    validate(material);
                }
                return material;
            }

            protected void validate(Material material) {
                super.validate(material);
                ValidationSupport.requireNonNull(material.substance, "substance");
                ValidationSupport.requireValueOrChildren(material);
            }

            protected Builder from(Material material) {
                super.from(material);
                substance = material.substance;
                alternate = material.alternate;
                allergenicIndicator = material.allergenicIndicator;
                return this;
            }
        }
    }

    /**
     * Information aimed at providing directions for the usage of this model of device.
     */
    public static class Guideline extends BackboneElement {
        private final List<UsageContext> useContext;
        private final Markdown usageInstruction;
        private final List<RelatedArtifact> relatedArtifact;
        private final List<CodeableConcept> indication;
        private final List<CodeableConcept> contraindication;
        private final List<CodeableConcept> warning;
        private final String intendedUse;

        private Guideline(Builder builder) {
            super(builder);
            useContext = Collections.unmodifiableList(builder.useContext);
            usageInstruction = builder.usageInstruction;
            relatedArtifact = Collections.unmodifiableList(builder.relatedArtifact);
            indication = Collections.unmodifiableList(builder.indication);
            contraindication = Collections.unmodifiableList(builder.contraindication);
            warning = Collections.unmodifiableList(builder.warning);
            intendedUse = builder.intendedUse;
        }

        /**
         * The circumstances that form the setting for using the device.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
         */
        public List<UsageContext> getUseContext() {
            return useContext;
        }

        /**
         * Detailed written and visual directions for the user on how to use the device.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getUsageInstruction() {
            return usageInstruction;
        }

        /**
         * A source of information or reference for this guideline.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
         */
        public List<RelatedArtifact> getRelatedArtifact() {
            return relatedArtifact;
        }

        /**
         * A clinical condition for which the device was designed to be used.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getIndication() {
            return indication;
        }

        /**
         * A specific situation when a device should not be used because it may cause harm.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getContraindication() {
            return contraindication;
        }

        /**
         * Specific hazard alert information that a user needs to know before using the device.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getWarning() {
            return warning;
        }

        /**
         * A description of the general purpose or medical use of the device or its function.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getIntendedUse() {
            return intendedUse;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !useContext.isEmpty() || 
                (usageInstruction != null) || 
                !relatedArtifact.isEmpty() || 
                !indication.isEmpty() || 
                !contraindication.isEmpty() || 
                !warning.isEmpty() || 
                (intendedUse != null);
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
                    accept(useContext, "useContext", visitor, UsageContext.class);
                    accept(usageInstruction, "usageInstruction", visitor);
                    accept(relatedArtifact, "relatedArtifact", visitor, RelatedArtifact.class);
                    accept(indication, "indication", visitor, CodeableConcept.class);
                    accept(contraindication, "contraindication", visitor, CodeableConcept.class);
                    accept(warning, "warning", visitor, CodeableConcept.class);
                    accept(intendedUse, "intendedUse", visitor);
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
            Guideline other = (Guideline) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(useContext, other.useContext) && 
                Objects.equals(usageInstruction, other.usageInstruction) && 
                Objects.equals(relatedArtifact, other.relatedArtifact) && 
                Objects.equals(indication, other.indication) && 
                Objects.equals(contraindication, other.contraindication) && 
                Objects.equals(warning, other.warning) && 
                Objects.equals(intendedUse, other.intendedUse);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    useContext, 
                    usageInstruction, 
                    relatedArtifact, 
                    indication, 
                    contraindication, 
                    warning, 
                    intendedUse);
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
            private List<UsageContext> useContext = new ArrayList<>();
            private Markdown usageInstruction;
            private List<RelatedArtifact> relatedArtifact = new ArrayList<>();
            private List<CodeableConcept> indication = new ArrayList<>();
            private List<CodeableConcept> contraindication = new ArrayList<>();
            private List<CodeableConcept> warning = new ArrayList<>();
            private String intendedUse;

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
             * The circumstances that form the setting for using the device.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param useContext
             *     The circumstances that form the setting for using the device
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
             * The circumstances that form the setting for using the device.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param useContext
             *     The circumstances that form the setting for using the device
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
             * Detailed written and visual directions for the user on how to use the device.
             * 
             * @param usageInstruction
             *     Detailed written and visual directions for the user on how to use the device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder usageInstruction(Markdown usageInstruction) {
                this.usageInstruction = usageInstruction;
                return this;
            }

            /**
             * A source of information or reference for this guideline.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedArtifact
             *     A source of information or reference for this guideline
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relatedArtifact(RelatedArtifact... relatedArtifact) {
                for (RelatedArtifact value : relatedArtifact) {
                    this.relatedArtifact.add(value);
                }
                return this;
            }

            /**
             * A source of information or reference for this guideline.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedArtifact
             *     A source of information or reference for this guideline
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder relatedArtifact(Collection<RelatedArtifact> relatedArtifact) {
                this.relatedArtifact = new ArrayList<>(relatedArtifact);
                return this;
            }

            /**
             * A clinical condition for which the device was designed to be used.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param indication
             *     A clinical condition for which the device was designed to be used
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder indication(CodeableConcept... indication) {
                for (CodeableConcept value : indication) {
                    this.indication.add(value);
                }
                return this;
            }

            /**
             * A clinical condition for which the device was designed to be used.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param indication
             *     A clinical condition for which the device was designed to be used
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder indication(Collection<CodeableConcept> indication) {
                this.indication = new ArrayList<>(indication);
                return this;
            }

            /**
             * A specific situation when a device should not be used because it may cause harm.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param contraindication
             *     A specific situation when a device should not be used because it may cause harm
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder contraindication(CodeableConcept... contraindication) {
                for (CodeableConcept value : contraindication) {
                    this.contraindication.add(value);
                }
                return this;
            }

            /**
             * A specific situation when a device should not be used because it may cause harm.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param contraindication
             *     A specific situation when a device should not be used because it may cause harm
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder contraindication(Collection<CodeableConcept> contraindication) {
                this.contraindication = new ArrayList<>(contraindication);
                return this;
            }

            /**
             * Specific hazard alert information that a user needs to know before using the device.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param warning
             *     Specific hazard alert information that a user needs to know before using the device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder warning(CodeableConcept... warning) {
                for (CodeableConcept value : warning) {
                    this.warning.add(value);
                }
                return this;
            }

            /**
             * Specific hazard alert information that a user needs to know before using the device.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param warning
             *     Specific hazard alert information that a user needs to know before using the device
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder warning(Collection<CodeableConcept> warning) {
                this.warning = new ArrayList<>(warning);
                return this;
            }

            /**
             * Convenience method for setting {@code intendedUse}.
             * 
             * @param intendedUse
             *     A description of the general purpose or medical use of the device or its function
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #intendedUse(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder intendedUse(java.lang.String intendedUse) {
                this.intendedUse = (intendedUse == null) ? null : String.of(intendedUse);
                return this;
            }

            /**
             * A description of the general purpose or medical use of the device or its function.
             * 
             * @param intendedUse
             *     A description of the general purpose or medical use of the device or its function
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder intendedUse(String intendedUse) {
                this.intendedUse = intendedUse;
                return this;
            }

            /**
             * Build the {@link Guideline}
             * 
             * @return
             *     An immutable object of type {@link Guideline}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Guideline per the base specification
             */
            @Override
            public Guideline build() {
                Guideline guideline = new Guideline(this);
                if (validating) {
                    validate(guideline);
                }
                return guideline;
            }

            protected void validate(Guideline guideline) {
                super.validate(guideline);
                ValidationSupport.checkList(guideline.useContext, "useContext", UsageContext.class);
                ValidationSupport.checkList(guideline.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
                ValidationSupport.checkList(guideline.indication, "indication", CodeableConcept.class);
                ValidationSupport.checkList(guideline.contraindication, "contraindication", CodeableConcept.class);
                ValidationSupport.checkList(guideline.warning, "warning", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(guideline);
            }

            protected Builder from(Guideline guideline) {
                super.from(guideline);
                useContext.addAll(guideline.useContext);
                usageInstruction = guideline.usageInstruction;
                relatedArtifact.addAll(guideline.relatedArtifact);
                indication.addAll(guideline.indication);
                contraindication.addAll(guideline.contraindication);
                warning.addAll(guideline.warning);
                intendedUse = guideline.intendedUse;
                return this;
            }
        }
    }

    /**
     * Tracking of latest field safety corrective action.
     */
    public static class CorrectiveAction extends BackboneElement {
        @Required
        private final Boolean recall;
        @Binding(
            bindingName = "DeviceCorrectiveActionScope",
            strength = BindingStrength.Value.REQUIRED,
            description = "The type or scope of the corrective action.",
            valueSet = "http://hl7.org/fhir/ValueSet/device-correctiveactionscope|5.0.0"
        )
        private final DeviceCorrectiveActionScope scope;
        @Required
        private final Period period;

        private CorrectiveAction(Builder builder) {
            super(builder);
            recall = builder.recall;
            scope = builder.scope;
            period = builder.period;
        }

        /**
         * Whether the last corrective action known for this device was a recall.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that is non-null.
         */
        public Boolean getRecall() {
            return recall;
        }

        /**
         * The scope of the corrective action - whether the action targeted all units of a given device model, or only a specific 
         * set of batches identified by lot numbers, or individually identified devices identified by the serial name.
         * 
         * @return
         *     An immutable object of type {@link DeviceCorrectiveActionScope} that may be null.
         */
        public DeviceCorrectiveActionScope getScope() {
            return scope;
        }

        /**
         * Start and end dates of the corrective action.
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
                (recall != null) || 
                (scope != null) || 
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
                    accept(recall, "recall", visitor);
                    accept(scope, "scope", visitor);
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
            CorrectiveAction other = (CorrectiveAction) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(recall, other.recall) && 
                Objects.equals(scope, other.scope) && 
                Objects.equals(period, other.period);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    recall, 
                    scope, 
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
            private Boolean recall;
            private DeviceCorrectiveActionScope scope;
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
             * Convenience method for setting {@code recall}.
             * 
             * <p>This element is required.
             * 
             * @param recall
             *     Whether the corrective action was a recall
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #recall(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder recall(java.lang.Boolean recall) {
                this.recall = (recall == null) ? null : Boolean.of(recall);
                return this;
            }

            /**
             * Whether the last corrective action known for this device was a recall.
             * 
             * <p>This element is required.
             * 
             * @param recall
             *     Whether the corrective action was a recall
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder recall(Boolean recall) {
                this.recall = recall;
                return this;
            }

            /**
             * The scope of the corrective action - whether the action targeted all units of a given device model, or only a specific 
             * set of batches identified by lot numbers, or individually identified devices identified by the serial name.
             * 
             * @param scope
             *     model | lot-numbers | serial-numbers
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder scope(DeviceCorrectiveActionScope scope) {
                this.scope = scope;
                return this;
            }

            /**
             * Start and end dates of the corrective action.
             * 
             * <p>This element is required.
             * 
             * @param period
             *     Start and end dates of the corrective action
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link CorrectiveAction}
             * 
             * <p>Required elements:
             * <ul>
             * <li>recall</li>
             * <li>period</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link CorrectiveAction}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid CorrectiveAction per the base specification
             */
            @Override
            public CorrectiveAction build() {
                CorrectiveAction correctiveAction = new CorrectiveAction(this);
                if (validating) {
                    validate(correctiveAction);
                }
                return correctiveAction;
            }

            protected void validate(CorrectiveAction correctiveAction) {
                super.validate(correctiveAction);
                ValidationSupport.requireNonNull(correctiveAction.recall, "recall");
                ValidationSupport.requireNonNull(correctiveAction.period, "period");
                ValidationSupport.requireValueOrChildren(correctiveAction);
            }

            protected Builder from(CorrectiveAction correctiveAction) {
                super.from(correctiveAction);
                recall = correctiveAction.recall;
                scope = correctiveAction.scope;
                period = correctiveAction.period;
                return this;
            }
        }
    }

    /**
     * Billing code or reference associated with the device.
     */
    public static class ChargeItem extends BackboneElement {
        @Required
        private final CodeableReference chargeItemCode;
        @Required
        private final Quantity count;
        private final Period effectivePeriod;
        private final List<UsageContext> useContext;

        private ChargeItem(Builder builder) {
            super(builder);
            chargeItemCode = builder.chargeItemCode;
            count = builder.count;
            effectivePeriod = builder.effectivePeriod;
            useContext = Collections.unmodifiableList(builder.useContext);
        }

        /**
         * The code or reference for the charge item.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getChargeItemCode() {
            return chargeItemCode;
        }

        /**
         * Coefficient applicable to the billing code.
         * 
         * @return
         *     An immutable object of type {@link Quantity} that is non-null.
         */
        public Quantity getCount() {
            return count;
        }

        /**
         * A specific time period in which this charge item applies.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getEffectivePeriod() {
            return effectivePeriod;
        }

        /**
         * The context to which this charge item applies.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
         */
        public List<UsageContext> getUseContext() {
            return useContext;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (chargeItemCode != null) || 
                (count != null) || 
                (effectivePeriod != null) || 
                !useContext.isEmpty();
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
                    accept(chargeItemCode, "chargeItemCode", visitor);
                    accept(count, "count", visitor);
                    accept(effectivePeriod, "effectivePeriod", visitor);
                    accept(useContext, "useContext", visitor, UsageContext.class);
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
            ChargeItem other = (ChargeItem) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(chargeItemCode, other.chargeItemCode) && 
                Objects.equals(count, other.count) && 
                Objects.equals(effectivePeriod, other.effectivePeriod) && 
                Objects.equals(useContext, other.useContext);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    chargeItemCode, 
                    count, 
                    effectivePeriod, 
                    useContext);
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
            private CodeableReference chargeItemCode;
            private Quantity count;
            private Period effectivePeriod;
            private List<UsageContext> useContext = new ArrayList<>();

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
             * The code or reference for the charge item.
             * 
             * <p>This element is required.
             * 
             * @param chargeItemCode
             *     The code or reference for the charge item
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder chargeItemCode(CodeableReference chargeItemCode) {
                this.chargeItemCode = chargeItemCode;
                return this;
            }

            /**
             * Coefficient applicable to the billing code.
             * 
             * <p>This element is required.
             * 
             * @param count
             *     Coefficient applicable to the billing code
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder count(Quantity count) {
                this.count = count;
                return this;
            }

            /**
             * A specific time period in which this charge item applies.
             * 
             * @param effectivePeriod
             *     A specific time period in which this charge item applies
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder effectivePeriod(Period effectivePeriod) {
                this.effectivePeriod = effectivePeriod;
                return this;
            }

            /**
             * The context to which this charge item applies.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param useContext
             *     The context to which this charge item applies
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
             * The context to which this charge item applies.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param useContext
             *     The context to which this charge item applies
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
             * Build the {@link ChargeItem}
             * 
             * <p>Required elements:
             * <ul>
             * <li>chargeItemCode</li>
             * <li>count</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ChargeItem}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ChargeItem per the base specification
             */
            @Override
            public ChargeItem build() {
                ChargeItem chargeItem = new ChargeItem(this);
                if (validating) {
                    validate(chargeItem);
                }
                return chargeItem;
            }

            protected void validate(ChargeItem chargeItem) {
                super.validate(chargeItem);
                ValidationSupport.requireNonNull(chargeItem.chargeItemCode, "chargeItemCode");
                ValidationSupport.requireNonNull(chargeItem.count, "count");
                ValidationSupport.checkList(chargeItem.useContext, "useContext", UsageContext.class);
                ValidationSupport.requireValueOrChildren(chargeItem);
            }

            protected Builder from(ChargeItem chargeItem) {
                super.from(chargeItem);
                chargeItemCode = chargeItem.chargeItemCode;
                count = chargeItem.count;
                effectivePeriod = chargeItem.effectivePeriod;
                useContext.addAll(chargeItem.useContext);
                return this;
            }
        }
    }
}
