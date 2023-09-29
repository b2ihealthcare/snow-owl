/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.resource;

import java.util.ArrayList;
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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.SpecimenStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A sample to be used for analysis.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "specimen-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/specimen-role",
    expression = "role.exists() implies (role.all(memberOf('http://hl7.org/fhir/ValueSet/specimen-role', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/Specimen",
    generated = true
)
@Constraint(
    id = "specimen-1",
    level = "Warning",
    location = "collection.fastingStatus",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/v2-0916",
    expression = "$this.as(CodeableConcept).memberOf('http://terminology.hl7.org/ValueSet/v2-0916', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Specimen",
    generated = true
)
@Constraint(
    id = "specimen-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/v2-0493",
    expression = "condition.exists() implies (condition.all(memberOf('http://terminology.hl7.org/ValueSet/v2-0493', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/Specimen",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Specimen extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final Identifier accessionIdentifier;
    @Summary
    @Binding(
        bindingName = "SpecimenStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes providing the status/availability of a specimen.",
        valueSet = "http://hl7.org/fhir/ValueSet/specimen-status|5.0.0"
    )
    private final SpecimenStatus status;
    @Summary
    @Binding(
        bindingName = "SpecimenType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The type of the specimen.",
        valueSet = "http://terminology.hl7.org/ValueSet/v2-0487"
    )
    private final CodeableConcept type;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Device", "BiologicallyDerivedProduct", "Substance", "Location" })
    private final Reference subject;
    @Summary
    private final DateTime receivedTime;
    @ReferenceTarget({ "Specimen" })
    private final List<Reference> parent;
    @ReferenceTarget({ "ServiceRequest" })
    private final List<Reference> request;
    @Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes for the combined status of a specimen.",
        valueSet = "http://hl7.org/fhir/ValueSet/specimen-combined|5.0.0"
    )
    private final PublicationStatus combined;
    @Binding(
        bindingName = "SpecimenRole",
        strength = BindingStrength.Value.PREFERRED,
        description = "Codes describing specimen role.",
        valueSet = "http://hl7.org/fhir/ValueSet/specimen-role"
    )
    private final List<CodeableConcept> role;
    private final List<Feature> feature;
    private final Collection collection;
    private final List<Processing> processing;
    private final List<Container> container;
    @Summary
    @Binding(
        bindingName = "SpecimenCondition",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Codes describing the state of the specimen.",
        valueSet = "http://terminology.hl7.org/ValueSet/v2-0493"
    )
    private final List<CodeableConcept> condition;
    private final List<Annotation> note;

    private Specimen(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        accessionIdentifier = builder.accessionIdentifier;
        status = builder.status;
        type = builder.type;
        subject = builder.subject;
        receivedTime = builder.receivedTime;
        parent = Collections.unmodifiableList(builder.parent);
        request = Collections.unmodifiableList(builder.request);
        combined = builder.combined;
        role = Collections.unmodifiableList(builder.role);
        feature = Collections.unmodifiableList(builder.feature);
        collection = builder.collection;
        processing = Collections.unmodifiableList(builder.processing);
        container = Collections.unmodifiableList(builder.container);
        condition = Collections.unmodifiableList(builder.condition);
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * Id for specimen.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier assigned by the lab when accessioning specimen(s). This is not necessarily the same as the specimen 
     * identifier, depending on local lab procedures.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getAccessionIdentifier() {
        return accessionIdentifier;
    }

    /**
     * The availability of the specimen.
     * 
     * @return
     *     An immutable object of type {@link SpecimenStatus} that may be null.
     */
    public SpecimenStatus getStatus() {
        return status;
    }

    /**
     * The kind of material that forms the specimen.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * Where the specimen came from. This may be from patient(s), from a location (e.g., the source of an environmental 
     * sample), or a sampling of a substance, a biologically-derived product, or a device.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * Time when specimen is received by the testing laboratory for processing or testing.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getReceivedTime() {
        return receivedTime;
    }

    /**
     * Reference to the parent (source) specimen which is used when the specimen was either derived from or a component of 
     * another specimen.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getParent() {
        return parent;
    }

    /**
     * Details concerning a service request that required a specimen to be collected.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getRequest() {
        return request;
    }

    /**
     * This element signifies if the specimen is part of a group or pooled.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that may be null.
     */
    public PublicationStatus getCombined() {
        return combined;
    }

    /**
     * The role or reason for the specimen in the testing workflow.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getRole() {
        return role;
    }

    /**
     * A physical feature or landmark on a specimen, highlighted for context by the collector of the specimen (e.g. surgeon), 
     * that identifies the type of feature as well as its meaning (e.g. the red ink indicating the resection margin of the 
     * right lobe of the excised prostate tissue or wire loop at radiologically suspected tumor location).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Feature} that may be empty.
     */
    public List<Feature> getFeature() {
        return feature;
    }

    /**
     * Details concerning the specimen collection.
     * 
     * @return
     *     An immutable object of type {@link Collection} that may be null.
     */
    public Collection getCollection() {
        return collection;
    }

    /**
     * Details concerning processing and processing steps for the specimen.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Processing} that may be empty.
     */
    public List<Processing> getProcessing() {
        return processing;
    }

    /**
     * The container holding the specimen. The recursive nature of containers; i.e. blood in tube in tray in rack is not 
     * addressed here.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Container} that may be empty.
     */
    public List<Container> getContainer() {
        return container;
    }

    /**
     * A mode or state of being that describes the nature of the specimen.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCondition() {
        return condition;
    }

    /**
     * To communicate any details or issues about the specimen or during the specimen collection. (for example: broken vial, 
     * sent with patient, frozen).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (accessionIdentifier != null) || 
            (status != null) || 
            (type != null) || 
            (subject != null) || 
            (receivedTime != null) || 
            !parent.isEmpty() || 
            !request.isEmpty() || 
            (combined != null) || 
            !role.isEmpty() || 
            !feature.isEmpty() || 
            (collection != null) || 
            !processing.isEmpty() || 
            !container.isEmpty() || 
            !condition.isEmpty() || 
            !note.isEmpty();
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
                accept(accessionIdentifier, "accessionIdentifier", visitor);
                accept(status, "status", visitor);
                accept(type, "type", visitor);
                accept(subject, "subject", visitor);
                accept(receivedTime, "receivedTime", visitor);
                accept(parent, "parent", visitor, Reference.class);
                accept(request, "request", visitor, Reference.class);
                accept(combined, "combined", visitor);
                accept(role, "role", visitor, CodeableConcept.class);
                accept(feature, "feature", visitor, Feature.class);
                accept(collection, "collection", visitor);
                accept(processing, "processing", visitor, Processing.class);
                accept(container, "container", visitor, Container.class);
                accept(condition, "condition", visitor, CodeableConcept.class);
                accept(note, "note", visitor, Annotation.class);
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
        Specimen other = (Specimen) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(accessionIdentifier, other.accessionIdentifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(type, other.type) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(receivedTime, other.receivedTime) && 
            Objects.equals(parent, other.parent) && 
            Objects.equals(request, other.request) && 
            Objects.equals(combined, other.combined) && 
            Objects.equals(role, other.role) && 
            Objects.equals(feature, other.feature) && 
            Objects.equals(collection, other.collection) && 
            Objects.equals(processing, other.processing) && 
            Objects.equals(container, other.container) && 
            Objects.equals(condition, other.condition) && 
            Objects.equals(note, other.note);
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
                accessionIdentifier, 
                status, 
                type, 
                subject, 
                receivedTime, 
                parent, 
                request, 
                combined, 
                role, 
                feature, 
                collection, 
                processing, 
                container, 
                condition, 
                note);
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
        private Identifier accessionIdentifier;
        private SpecimenStatus status;
        private CodeableConcept type;
        private Reference subject;
        private DateTime receivedTime;
        private List<Reference> parent = new ArrayList<>();
        private List<Reference> request = new ArrayList<>();
        private PublicationStatus combined;
        private List<CodeableConcept> role = new ArrayList<>();
        private List<Feature> feature = new ArrayList<>();
        private Collection collection;
        private List<Processing> processing = new ArrayList<>();
        private List<Container> container = new ArrayList<>();
        private List<CodeableConcept> condition = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();

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
        public Builder contained(java.util.Collection<Resource> contained) {
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
        public Builder extension(java.util.Collection<Extension> extension) {
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
        public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * Id for specimen.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Identifier
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
         * Id for specimen.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Identifier
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder identifier(java.util.Collection<Identifier> identifier) {
            this.identifier = new ArrayList<>(identifier);
            return this;
        }

        /**
         * The identifier assigned by the lab when accessioning specimen(s). This is not necessarily the same as the specimen 
         * identifier, depending on local lab procedures.
         * 
         * @param accessionIdentifier
         *     Identifier assigned by the lab
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder accessionIdentifier(Identifier accessionIdentifier) {
            this.accessionIdentifier = accessionIdentifier;
            return this;
        }

        /**
         * The availability of the specimen.
         * 
         * @param status
         *     available | unavailable | unsatisfactory | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(SpecimenStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The kind of material that forms the specimen.
         * 
         * @param type
         *     Kind of material that forms the specimen
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * Where the specimen came from. This may be from patient(s), from a location (e.g., the source of an environmental 
         * sample), or a sampling of a substance, a biologically-derived product, or a device.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Device}</li>
         * <li>{@link BiologicallyDerivedProduct}</li>
         * <li>{@link Substance}</li>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param subject
         *     Where the specimen came from. This may be from patient(s), from a location (e.g., the source of an environmental 
         *     sample), or a sampling of a substance, a biologically-derived product, or a device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Time when specimen is received by the testing laboratory for processing or testing.
         * 
         * @param receivedTime
         *     The time when specimen is received by the testing laboratory
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder receivedTime(DateTime receivedTime) {
            this.receivedTime = receivedTime;
            return this;
        }

        /**
         * Reference to the parent (source) specimen which is used when the specimen was either derived from or a component of 
         * another specimen.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Specimen}</li>
         * </ul>
         * 
         * @param parent
         *     Specimen from which this specimen originated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder parent(Reference... parent) {
            for (Reference value : parent) {
                this.parent.add(value);
            }
            return this;
        }

        /**
         * Reference to the parent (source) specimen which is used when the specimen was either derived from or a component of 
         * another specimen.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Specimen}</li>
         * </ul>
         * 
         * @param parent
         *     Specimen from which this specimen originated
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder parent(java.util.Collection<Reference> parent) {
            this.parent = new ArrayList<>(parent);
            return this;
        }

        /**
         * Details concerning a service request that required a specimen to be collected.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param request
         *     Why the specimen was collected
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder request(Reference... request) {
            for (Reference value : request) {
                this.request.add(value);
            }
            return this;
        }

        /**
         * Details concerning a service request that required a specimen to be collected.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param request
         *     Why the specimen was collected
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder request(java.util.Collection<Reference> request) {
            this.request = new ArrayList<>(request);
            return this;
        }

        /**
         * This element signifies if the specimen is part of a group or pooled.
         * 
         * @param combined
         *     grouped | pooled
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder combined(PublicationStatus combined) {
            this.combined = combined;
            return this;
        }

        /**
         * The role or reason for the specimen in the testing workflow.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param role
         *     The role the specimen serves
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder role(CodeableConcept... role) {
            for (CodeableConcept value : role) {
                this.role.add(value);
            }
            return this;
        }

        /**
         * The role or reason for the specimen in the testing workflow.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param role
         *     The role the specimen serves
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder role(java.util.Collection<CodeableConcept> role) {
            this.role = new ArrayList<>(role);
            return this;
        }

        /**
         * A physical feature or landmark on a specimen, highlighted for context by the collector of the specimen (e.g. surgeon), 
         * that identifies the type of feature as well as its meaning (e.g. the red ink indicating the resection margin of the 
         * right lobe of the excised prostate tissue or wire loop at radiologically suspected tumor location).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param feature
         *     The physical feature of a specimen
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder feature(Feature... feature) {
            for (Feature value : feature) {
                this.feature.add(value);
            }
            return this;
        }

        /**
         * A physical feature or landmark on a specimen, highlighted for context by the collector of the specimen (e.g. surgeon), 
         * that identifies the type of feature as well as its meaning (e.g. the red ink indicating the resection margin of the 
         * right lobe of the excised prostate tissue or wire loop at radiologically suspected tumor location).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param feature
         *     The physical feature of a specimen
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder feature(java.util.Collection<Feature> feature) {
            this.feature = new ArrayList<>(feature);
            return this;
        }

        /**
         * Details concerning the specimen collection.
         * 
         * @param collection
         *     Collection details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder collection(Collection collection) {
            this.collection = collection;
            return this;
        }

        /**
         * Details concerning processing and processing steps for the specimen.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param processing
         *     Processing and processing step details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder processing(Processing... processing) {
            for (Processing value : processing) {
                this.processing.add(value);
            }
            return this;
        }

        /**
         * Details concerning processing and processing steps for the specimen.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param processing
         *     Processing and processing step details
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder processing(java.util.Collection<Processing> processing) {
            this.processing = new ArrayList<>(processing);
            return this;
        }

        /**
         * The container holding the specimen. The recursive nature of containers; i.e. blood in tube in tray in rack is not 
         * addressed here.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param container
         *     Direct container of specimen (tube/slide, etc.)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder container(Container... container) {
            for (Container value : container) {
                this.container.add(value);
            }
            return this;
        }

        /**
         * The container holding the specimen. The recursive nature of containers; i.e. blood in tube in tray in rack is not 
         * addressed here.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param container
         *     Direct container of specimen (tube/slide, etc.)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder container(java.util.Collection<Container> container) {
            this.container = new ArrayList<>(container);
            return this;
        }

        /**
         * A mode or state of being that describes the nature of the specimen.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param condition
         *     State of the specimen
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder condition(CodeableConcept... condition) {
            for (CodeableConcept value : condition) {
                this.condition.add(value);
            }
            return this;
        }

        /**
         * A mode or state of being that describes the nature of the specimen.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param condition
         *     State of the specimen
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder condition(java.util.Collection<CodeableConcept> condition) {
            this.condition = new ArrayList<>(condition);
            return this;
        }

        /**
         * To communicate any details or issues about the specimen or during the specimen collection. (for example: broken vial, 
         * sent with patient, frozen).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments
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
         * To communicate any details or issues about the specimen or during the specimen collection. (for example: broken vial, 
         * sent with patient, frozen).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder note(java.util.Collection<Annotation> note) {
            this.note = new ArrayList<>(note);
            return this;
        }

        /**
         * Build the {@link Specimen}
         * 
         * @return
         *     An immutable object of type {@link Specimen}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Specimen per the base specification
         */
        @Override
        public Specimen build() {
            Specimen specimen = new Specimen(this);
            if (validating) {
                validate(specimen);
            }
            return specimen;
        }

        protected void validate(Specimen specimen) {
            super.validate(specimen);
            ValidationSupport.checkList(specimen.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(specimen.parent, "parent", Reference.class);
            ValidationSupport.checkList(specimen.request, "request", Reference.class);
            ValidationSupport.checkList(specimen.role, "role", CodeableConcept.class);
            ValidationSupport.checkList(specimen.feature, "feature", Feature.class);
            ValidationSupport.checkList(specimen.processing, "processing", Processing.class);
            ValidationSupport.checkList(specimen.container, "container", Container.class);
            ValidationSupport.checkList(specimen.condition, "condition", CodeableConcept.class);
            ValidationSupport.checkList(specimen.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(specimen.subject, "subject", "Patient", "Group", "Device", "BiologicallyDerivedProduct", "Substance", "Location");
            ValidationSupport.checkReferenceType(specimen.parent, "parent", "Specimen");
            ValidationSupport.checkReferenceType(specimen.request, "request", "ServiceRequest");
        }

        protected Builder from(Specimen specimen) {
            super.from(specimen);
            identifier.addAll(specimen.identifier);
            accessionIdentifier = specimen.accessionIdentifier;
            status = specimen.status;
            type = specimen.type;
            subject = specimen.subject;
            receivedTime = specimen.receivedTime;
            parent.addAll(specimen.parent);
            request.addAll(specimen.request);
            combined = specimen.combined;
            role.addAll(specimen.role);
            feature.addAll(specimen.feature);
            collection = specimen.collection;
            processing.addAll(specimen.processing);
            container.addAll(specimen.container);
            condition.addAll(specimen.condition);
            note.addAll(specimen.note);
            return this;
        }
    }

    /**
     * A physical feature or landmark on a specimen, highlighted for context by the collector of the specimen (e.g. surgeon), 
     * that identifies the type of feature as well as its meaning (e.g. the red ink indicating the resection margin of the 
     * right lobe of the excised prostate tissue or wire loop at radiologically suspected tumor location).
     */
    public static class Feature extends BackboneElement {
        @Binding(
            bindingName = "SpecimenFeatureType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "SNOMED CT Body site concepts",
            valueSet = "http://hl7.org/fhir/ValueSet/body-site"
        )
        @Required
        private final CodeableConcept type;
        @Required
        private final String description;

        private Feature(Builder builder) {
            super(builder);
            type = builder.type;
            description = builder.description;
        }

        /**
         * The landmark or feature being highlighted.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Description of the feature of the specimen.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getDescription() {
            return description;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (description != null);
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
                    accept(description, "description", visitor);
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
            Feature other = (Feature) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(description, other.description);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    description);
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
            private String description;

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
            public Builder extension(java.util.Collection<Extension> extension) {
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
            public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * The landmark or feature being highlighted.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Highlighted feature
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code description}.
             * 
             * <p>This element is required.
             * 
             * @param description
             *     Information about the feature
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
             * Description of the feature of the specimen.
             * 
             * <p>This element is required.
             * 
             * @param description
             *     Information about the feature
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * Build the {@link Feature}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>description</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Feature}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Feature per the base specification
             */
            @Override
            public Feature build() {
                Feature feature = new Feature(this);
                if (validating) {
                    validate(feature);
                }
                return feature;
            }

            protected void validate(Feature feature) {
                super.validate(feature);
                ValidationSupport.requireNonNull(feature.type, "type");
                ValidationSupport.requireNonNull(feature.description, "description");
                ValidationSupport.requireValueOrChildren(feature);
            }

            protected Builder from(Feature feature) {
                super.from(feature);
                type = feature.type;
                description = feature.description;
                return this;
            }
        }
    }

    /**
     * Details concerning the specimen collection.
     */
    public static class Collection extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Patient", "RelatedPerson" })
        private final Reference collector;
        @Summary
        @Choice({ DateTime.class, Period.class })
        private final Element collected;
        @Summary
        private final Duration duration;
        private final SimpleQuantity quantity;
        @Binding(
            bindingName = "SpecimenCollectionMethod",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The  technique that is used to perform the procedure.",
            valueSet = "http://hl7.org/fhir/ValueSet/specimen-collection-method"
        )
        private final CodeableConcept method;
        @Binding(
            bindingName = "SpecimenCollectionDevice",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The device that was used to obtain the specimen (e.g. a catheter or catheter part used to draw the blood via a central line)."
        )
        private final CodeableReference device;
        @ReferenceTarget({ "Procedure" })
        private final Reference procedure;
        @Binding(
            bindingName = "BodySite",
            strength = BindingStrength.Value.EXAMPLE,
            description = "SNOMED CT Body site concepts",
            valueSet = "http://hl7.org/fhir/ValueSet/body-site"
        )
        private final CodeableReference bodySite;
        @Summary
        @Choice({ CodeableConcept.class, Duration.class })
        @Binding(
            bindingName = "FastingStatus",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Codes describing the fasting status of the patient.",
            valueSet = "http://terminology.hl7.org/ValueSet/v2-0916"
        )
        private final Element fastingStatus;

        private Collection(Builder builder) {
            super(builder);
            collector = builder.collector;
            collected = builder.collected;
            duration = builder.duration;
            quantity = builder.quantity;
            method = builder.method;
            device = builder.device;
            procedure = builder.procedure;
            bodySite = builder.bodySite;
            fastingStatus = builder.fastingStatus;
        }

        /**
         * Person who collected the specimen.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getCollector() {
            return collector;
        }

        /**
         * Time when specimen was collected from subject - the physiologically relevant time.
         * 
         * @return
         *     An immutable object of type {@link DateTime} or {@link Period} that may be null.
         */
        public Element getCollected() {
            return collected;
        }

        /**
         * The span of time over which the collection of a specimen occurred.
         * 
         * @return
         *     An immutable object of type {@link Duration} that may be null.
         */
        public Duration getDuration() {
            return duration;
        }

        /**
         * The quantity of specimen collected; for instance the volume of a blood sample, or the physical measurement of an 
         * anatomic pathology sample.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getQuantity() {
            return quantity;
        }

        /**
         * A coded value specifying the technique that is used to perform the procedure.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getMethod() {
            return method;
        }

        /**
         * A coded value specifying the technique that is used to perform the procedure.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getDevice() {
            return device;
        }

        /**
         * The procedure event during which the specimen was collected (e.g. the surgery leading to the collection of a pathology 
         * sample).
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getProcedure() {
            return procedure;
        }

        /**
         * Anatomical location from which the specimen was collected (if subject is a patient). This is the target site. This 
         * element is not used for environmental specimens.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getBodySite() {
            return bodySite;
        }

        /**
         * Abstinence or reduction from some or all food, drink, or both, for a period of time prior to sample collection.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} or {@link Duration} that may be null.
         */
        public Element getFastingStatus() {
            return fastingStatus;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (collector != null) || 
                (collected != null) || 
                (duration != null) || 
                (quantity != null) || 
                (method != null) || 
                (device != null) || 
                (procedure != null) || 
                (bodySite != null) || 
                (fastingStatus != null);
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
                    accept(collector, "collector", visitor);
                    accept(collected, "collected", visitor);
                    accept(duration, "duration", visitor);
                    accept(quantity, "quantity", visitor);
                    accept(method, "method", visitor);
                    accept(device, "device", visitor);
                    accept(procedure, "procedure", visitor);
                    accept(bodySite, "bodySite", visitor);
                    accept(fastingStatus, "fastingStatus", visitor);
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
            Collection other = (Collection) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(collector, other.collector) && 
                Objects.equals(collected, other.collected) && 
                Objects.equals(duration, other.duration) && 
                Objects.equals(quantity, other.quantity) && 
                Objects.equals(method, other.method) && 
                Objects.equals(device, other.device) && 
                Objects.equals(procedure, other.procedure) && 
                Objects.equals(bodySite, other.bodySite) && 
                Objects.equals(fastingStatus, other.fastingStatus);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    collector, 
                    collected, 
                    duration, 
                    quantity, 
                    method, 
                    device, 
                    procedure, 
                    bodySite, 
                    fastingStatus);
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
            private Reference collector;
            private Element collected;
            private Duration duration;
            private SimpleQuantity quantity;
            private CodeableConcept method;
            private CodeableReference device;
            private Reference procedure;
            private CodeableReference bodySite;
            private Element fastingStatus;

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
            public Builder extension(java.util.Collection<Extension> extension) {
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
            public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * Person who collected the specimen.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param collector
             *     Who collected the specimen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder collector(Reference collector) {
                this.collector = collector;
                return this;
            }

            /**
             * Time when specimen was collected from subject - the physiologically relevant time.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link DateTime}</li>
             * <li>{@link Period}</li>
             * </ul>
             * 
             * @param collected
             *     Collection time
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder collected(Element collected) {
                this.collected = collected;
                return this;
            }

            /**
             * The span of time over which the collection of a specimen occurred.
             * 
             * @param duration
             *     How long it took to collect specimen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder duration(Duration duration) {
                this.duration = duration;
                return this;
            }

            /**
             * The quantity of specimen collected; for instance the volume of a blood sample, or the physical measurement of an 
             * anatomic pathology sample.
             * 
             * @param quantity
             *     The quantity of specimen collected
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder quantity(SimpleQuantity quantity) {
                this.quantity = quantity;
                return this;
            }

            /**
             * A coded value specifying the technique that is used to perform the procedure.
             * 
             * @param method
             *     Technique used to perform collection
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder method(CodeableConcept method) {
                this.method = method;
                return this;
            }

            /**
             * A coded value specifying the technique that is used to perform the procedure.
             * 
             * @param device
             *     Device used to perform collection
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder device(CodeableReference device) {
                this.device = device;
                return this;
            }

            /**
             * The procedure event during which the specimen was collected (e.g. the surgery leading to the collection of a pathology 
             * sample).
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Procedure}</li>
             * </ul>
             * 
             * @param procedure
             *     The procedure that collects the specimen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder procedure(Reference procedure) {
                this.procedure = procedure;
                return this;
            }

            /**
             * Anatomical location from which the specimen was collected (if subject is a patient). This is the target site. This 
             * element is not used for environmental specimens.
             * 
             * @param bodySite
             *     Anatomical collection site
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder bodySite(CodeableReference bodySite) {
                this.bodySite = bodySite;
                return this;
            }

            /**
             * Abstinence or reduction from some or all food, drink, or both, for a period of time prior to sample collection.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Duration}</li>
             * </ul>
             * 
             * @param fastingStatus
             *     Whether or how long patient abstained from food and/or drink
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder fastingStatus(Element fastingStatus) {
                this.fastingStatus = fastingStatus;
                return this;
            }

            /**
             * Build the {@link Collection}
             * 
             * @return
             *     An immutable object of type {@link Collection}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Collection per the base specification
             */
            @Override
            public Collection build() {
                Collection collection = new Collection(this);
                if (validating) {
                    validate(collection);
                }
                return collection;
            }

            protected void validate(Collection collection) {
                super.validate(collection);
                ValidationSupport.choiceElement(collection.collected, "collected", DateTime.class, Period.class);
                ValidationSupport.choiceElement(collection.fastingStatus, "fastingStatus", CodeableConcept.class, Duration.class);
                ValidationSupport.checkReferenceType(collection.collector, "collector", "Practitioner", "PractitionerRole", "Patient", "RelatedPerson");
                ValidationSupport.checkReferenceType(collection.procedure, "procedure", "Procedure");
                ValidationSupport.requireValueOrChildren(collection);
            }

            protected Builder from(Collection collection) {
                super.from(collection);
                collector = collection.collector;
                collected = collection.collected;
                duration = collection.duration;
                quantity = collection.quantity;
                method = collection.method;
                device = collection.device;
                procedure = collection.procedure;
                bodySite = collection.bodySite;
                fastingStatus = collection.fastingStatus;
                return this;
            }
        }
    }

    /**
     * Details concerning processing and processing steps for the specimen.
     */
    public static class Processing extends BackboneElement {
        private final String description;
        @Binding(
            bindingName = "SpecimenProcessingMethod",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Type indicating the technique used to process the specimen.",
            valueSet = "http://hl7.org/fhir/ValueSet/specimen-processing-method"
        )
        private final CodeableConcept method;
        @ReferenceTarget({ "Substance" })
        private final List<Reference> additive;
        @Choice({ DateTime.class, Period.class })
        private final Element time;

        private Processing(Builder builder) {
            super(builder);
            description = builder.description;
            method = builder.method;
            additive = Collections.unmodifiableList(builder.additive);
            time = builder.time;
        }

        /**
         * Textual description of procedure.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDescription() {
            return description;
        }

        /**
         * A coded value specifying the method used to process the specimen.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getMethod() {
            return method;
        }

        /**
         * Material used in the processing step.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getAdditive() {
            return additive;
        }

        /**
         * A record of the time or period when the specimen processing occurred. For example the time of sample fixation or the 
         * period of time the sample was in formalin.
         * 
         * @return
         *     An immutable object of type {@link DateTime} or {@link Period} that may be null.
         */
        public Element getTime() {
            return time;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (description != null) || 
                (method != null) || 
                !additive.isEmpty() || 
                (time != null);
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
                    accept(description, "description", visitor);
                    accept(method, "method", visitor);
                    accept(additive, "additive", visitor, Reference.class);
                    accept(time, "time", visitor);
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
            Processing other = (Processing) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(description, other.description) && 
                Objects.equals(method, other.method) && 
                Objects.equals(additive, other.additive) && 
                Objects.equals(time, other.time);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    description, 
                    method, 
                    additive, 
                    time);
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
            private String description;
            private CodeableConcept method;
            private List<Reference> additive = new ArrayList<>();
            private Element time;

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
            public Builder extension(java.util.Collection<Extension> extension) {
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
            public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * Convenience method for setting {@code description}.
             * 
             * @param description
             *     Textual description of procedure
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
             * Textual description of procedure.
             * 
             * @param description
             *     Textual description of procedure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * A coded value specifying the method used to process the specimen.
             * 
             * @param method
             *     Indicates the treatment step applied to the specimen
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder method(CodeableConcept method) {
                this.method = method;
                return this;
            }

            /**
             * Material used in the processing step.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Substance}</li>
             * </ul>
             * 
             * @param additive
             *     Material used in the processing step
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder additive(Reference... additive) {
                for (Reference value : additive) {
                    this.additive.add(value);
                }
                return this;
            }

            /**
             * Material used in the processing step.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Substance}</li>
             * </ul>
             * 
             * @param additive
             *     Material used in the processing step
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder additive(java.util.Collection<Reference> additive) {
                this.additive = new ArrayList<>(additive);
                return this;
            }

            /**
             * A record of the time or period when the specimen processing occurred. For example the time of sample fixation or the 
             * period of time the sample was in formalin.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link DateTime}</li>
             * <li>{@link Period}</li>
             * </ul>
             * 
             * @param time
             *     Date and time of specimen processing
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder time(Element time) {
                this.time = time;
                return this;
            }

            /**
             * Build the {@link Processing}
             * 
             * @return
             *     An immutable object of type {@link Processing}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Processing per the base specification
             */
            @Override
            public Processing build() {
                Processing processing = new Processing(this);
                if (validating) {
                    validate(processing);
                }
                return processing;
            }

            protected void validate(Processing processing) {
                super.validate(processing);
                ValidationSupport.checkList(processing.additive, "additive", Reference.class);
                ValidationSupport.choiceElement(processing.time, "time", DateTime.class, Period.class);
                ValidationSupport.checkReferenceType(processing.additive, "additive", "Substance");
                ValidationSupport.requireValueOrChildren(processing);
            }

            protected Builder from(Processing processing) {
                super.from(processing);
                description = processing.description;
                method = processing.method;
                additive.addAll(processing.additive);
                time = processing.time;
                return this;
            }
        }
    }

    /**
     * The container holding the specimen. The recursive nature of containers; i.e. blood in tube in tray in rack is not 
     * addressed here.
     */
    public static class Container extends BackboneElement {
        @ReferenceTarget({ "Device" })
        @Required
        private final Reference device;
        @ReferenceTarget({ "Location" })
        private final Reference location;
        private final SimpleQuantity specimenQuantity;

        private Container(Builder builder) {
            super(builder);
            device = builder.device;
            location = builder.location;
            specimenQuantity = builder.specimenQuantity;
        }

        /**
         * The device resource for the the container holding the specimen. If the container is in a holder then the referenced 
         * device will point to a parent device.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getDevice() {
            return device;
        }

        /**
         * The location of the container holding the specimen.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getLocation() {
            return location;
        }

        /**
         * The quantity of specimen in the container; may be volume, dimensions, or other appropriate measurements, depending on 
         * the specimen type.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getSpecimenQuantity() {
            return specimenQuantity;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (device != null) || 
                (location != null) || 
                (specimenQuantity != null);
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
                    accept(device, "device", visitor);
                    accept(location, "location", visitor);
                    accept(specimenQuantity, "specimenQuantity", visitor);
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
                Objects.equals(device, other.device) && 
                Objects.equals(location, other.location) && 
                Objects.equals(specimenQuantity, other.specimenQuantity);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    device, 
                    location, 
                    specimenQuantity);
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
            private Reference device;
            private Reference location;
            private SimpleQuantity specimenQuantity;

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
            public Builder extension(java.util.Collection<Extension> extension) {
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
            public Builder modifierExtension(java.util.Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * The device resource for the the container holding the specimen. If the container is in a holder then the referenced 
             * device will point to a parent device.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Device}</li>
             * </ul>
             * 
             * @param device
             *     Device resource for the container
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder device(Reference device) {
                this.device = device;
                return this;
            }

            /**
             * The location of the container holding the specimen.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param location
             *     Where the container is
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(Reference location) {
                this.location = location;
                return this;
            }

            /**
             * The quantity of specimen in the container; may be volume, dimensions, or other appropriate measurements, depending on 
             * the specimen type.
             * 
             * @param specimenQuantity
             *     Quantity of specimen within container
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder specimenQuantity(SimpleQuantity specimenQuantity) {
                this.specimenQuantity = specimenQuantity;
                return this;
            }

            /**
             * Build the {@link Container}
             * 
             * <p>Required elements:
             * <ul>
             * <li>device</li>
             * </ul>
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
                ValidationSupport.requireNonNull(container.device, "device");
                ValidationSupport.checkReferenceType(container.device, "device", "Device");
                ValidationSupport.checkReferenceType(container.location, "location", "Location");
                ValidationSupport.requireValueOrChildren(container);
            }

            protected Builder from(Container container) {
                super.from(container);
                device = container.device;
                location = container.location;
                specimenQuantity = container.specimenQuantity;
                return this;
            }
        }
    }
}
