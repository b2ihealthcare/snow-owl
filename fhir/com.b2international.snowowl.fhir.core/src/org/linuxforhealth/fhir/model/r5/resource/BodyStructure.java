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
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Record details about an anatomical structure. This resource may be used when a coded concept does not provide the 
 * necessary detail needed for the use case.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "bodyStructure-0",
    level = "Warning",
    location = "includedStructure.bodyLandmarkOrientation.surfaceOrientation",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/bodystructure-relative-location",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/bodystructure-relative-location', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/BodyStructure",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class BodyStructure extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final Boolean active;
    @Summary
    @Binding(
        bindingName = "BodyStructureCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes describing anatomic morphology.",
        valueSet = "http://hl7.org/fhir/ValueSet/bodystructure-code"
    )
    private final CodeableConcept morphology;
    @Required
    private final List<IncludedStructure> includedStructure;
    private final List<BodyStructure.IncludedStructure> excludedStructure;
    @Summary
    private final Markdown description;
    private final List<Attachment> image;
    @Summary
    @ReferenceTarget({ "Patient" })
    @Required
    private final Reference patient;

    private BodyStructure(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        active = builder.active;
        morphology = builder.morphology;
        includedStructure = Collections.unmodifiableList(builder.includedStructure);
        excludedStructure = Collections.unmodifiableList(builder.excludedStructure);
        description = builder.description;
        image = Collections.unmodifiableList(builder.image);
        patient = builder.patient;
    }

    /**
     * Identifier for this instance of the anatomical structure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Whether this body site is in active use.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * The kind of structure being represented by the body structure at `BodyStructure.location`. This can define both normal 
     * and abnormal morphologies.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getMorphology() {
        return morphology;
    }

    /**
     * The anatomical location(s) or region(s) of the specimen, lesion, or body structure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link IncludedStructure} that is non-empty.
     */
    public List<IncludedStructure> getIncludedStructure() {
        return includedStructure;
    }

    /**
     * The anatomical location(s) or region(s) not occupied or represented by the specimen, lesion, or body structure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link IncludedStructure} that may be empty.
     */
    public List<BodyStructure.IncludedStructure> getExcludedStructure() {
        return excludedStructure;
    }

    /**
     * A summary, characterization or explanation of the body structure.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * Image or images used to identify a location.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Attachment} that may be empty.
     */
    public List<Attachment> getImage() {
        return image;
    }

    /**
     * The person to which the body site belongs.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getPatient() {
        return patient;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (active != null) || 
            (morphology != null) || 
            !includedStructure.isEmpty() || 
            !excludedStructure.isEmpty() || 
            (description != null) || 
            !image.isEmpty() || 
            (patient != null);
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
                accept(morphology, "morphology", visitor);
                accept(includedStructure, "includedStructure", visitor, IncludedStructure.class);
                accept(excludedStructure, "excludedStructure", visitor, BodyStructure.IncludedStructure.class);
                accept(description, "description", visitor);
                accept(image, "image", visitor, Attachment.class);
                accept(patient, "patient", visitor);
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
        BodyStructure other = (BodyStructure) obj;
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
            Objects.equals(morphology, other.morphology) && 
            Objects.equals(includedStructure, other.includedStructure) && 
            Objects.equals(excludedStructure, other.excludedStructure) && 
            Objects.equals(description, other.description) && 
            Objects.equals(image, other.image) && 
            Objects.equals(patient, other.patient);
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
                morphology, 
                includedStructure, 
                excludedStructure, 
                description, 
                image, 
                patient);
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
        private CodeableConcept morphology;
        private List<IncludedStructure> includedStructure = new ArrayList<>();
        private List<BodyStructure.IncludedStructure> excludedStructure = new ArrayList<>();
        private Markdown description;
        private List<Attachment> image = new ArrayList<>();
        private Reference patient;

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
         * Identifier for this instance of the anatomical structure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Bodystructure identifier
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
         * Identifier for this instance of the anatomical structure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Bodystructure identifier
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
         *     Whether this record is in active use
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
         * Whether this body site is in active use.
         * 
         * @param active
         *     Whether this record is in active use
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        /**
         * The kind of structure being represented by the body structure at `BodyStructure.location`. This can define both normal 
         * and abnormal morphologies.
         * 
         * @param morphology
         *     Kind of Structure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder morphology(CodeableConcept morphology) {
            this.morphology = morphology;
            return this;
        }

        /**
         * The anatomical location(s) or region(s) of the specimen, lesion, or body structure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param includedStructure
         *     Included anatomic location(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder includedStructure(IncludedStructure... includedStructure) {
            for (IncludedStructure value : includedStructure) {
                this.includedStructure.add(value);
            }
            return this;
        }

        /**
         * The anatomical location(s) or region(s) of the specimen, lesion, or body structure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param includedStructure
         *     Included anatomic location(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder includedStructure(Collection<IncludedStructure> includedStructure) {
            this.includedStructure = new ArrayList<>(includedStructure);
            return this;
        }

        /**
         * The anatomical location(s) or region(s) not occupied or represented by the specimen, lesion, or body structure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param excludedStructure
         *     Excluded anatomic locations(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder excludedStructure(BodyStructure.IncludedStructure... excludedStructure) {
            for (BodyStructure.IncludedStructure value : excludedStructure) {
                this.excludedStructure.add(value);
            }
            return this;
        }

        /**
         * The anatomical location(s) or region(s) not occupied or represented by the specimen, lesion, or body structure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param excludedStructure
         *     Excluded anatomic locations(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder excludedStructure(Collection<BodyStructure.IncludedStructure> excludedStructure) {
            this.excludedStructure = new ArrayList<>(excludedStructure);
            return this;
        }

        /**
         * A summary, characterization or explanation of the body structure.
         * 
         * @param description
         *     Text description
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * Image or images used to identify a location.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param image
         *     Attached images
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder image(Attachment... image) {
            for (Attachment value : image) {
                this.image.add(value);
            }
            return this;
        }

        /**
         * Image or images used to identify a location.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param image
         *     Attached images
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder image(Collection<Attachment> image) {
            this.image = new ArrayList<>(image);
            return this;
        }

        /**
         * The person to which the body site belongs.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param patient
         *     Who this is about
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patient(Reference patient) {
            this.patient = patient;
            return this;
        }

        /**
         * Build the {@link BodyStructure}
         * 
         * <p>Required elements:
         * <ul>
         * <li>includedStructure</li>
         * <li>patient</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link BodyStructure}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid BodyStructure per the base specification
         */
        @Override
        public BodyStructure build() {
            BodyStructure bodyStructure = new BodyStructure(this);
            if (validating) {
                validate(bodyStructure);
            }
            return bodyStructure;
        }

        protected void validate(BodyStructure bodyStructure) {
            super.validate(bodyStructure);
            ValidationSupport.checkList(bodyStructure.identifier, "identifier", Identifier.class);
            ValidationSupport.checkNonEmptyList(bodyStructure.includedStructure, "includedStructure", IncludedStructure.class);
            ValidationSupport.checkList(bodyStructure.excludedStructure, "excludedStructure", BodyStructure.IncludedStructure.class);
            ValidationSupport.checkList(bodyStructure.image, "image", Attachment.class);
            ValidationSupport.requireNonNull(bodyStructure.patient, "patient");
            ValidationSupport.checkReferenceType(bodyStructure.patient, "patient", "Patient");
        }

        protected Builder from(BodyStructure bodyStructure) {
            super.from(bodyStructure);
            identifier.addAll(bodyStructure.identifier);
            active = bodyStructure.active;
            morphology = bodyStructure.morphology;
            includedStructure.addAll(bodyStructure.includedStructure);
            excludedStructure.addAll(bodyStructure.excludedStructure);
            description = bodyStructure.description;
            image.addAll(bodyStructure.image);
            patient = bodyStructure.patient;
            return this;
        }
    }

    /**
     * The anatomical location(s) or region(s) of the specimen, lesion, or body structure.
     */
    public static class IncludedStructure extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "BodySite",
            strength = BindingStrength.Value.EXAMPLE,
            description = "SNOMED CT Body site concepts",
            valueSet = "http://hl7.org/fhir/ValueSet/body-site"
        )
        @Required
        private final CodeableConcept structure;
        @Binding(
            bindingName = "BodyStructureQualifier",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Concepts modifying the anatomic location.",
            valueSet = "http://hl7.org/fhir/ValueSet/bodystructure-relative-location"
        )
        private final CodeableConcept laterality;
        private final List<BodyLandmarkOrientation> bodyLandmarkOrientation;
        @ReferenceTarget({ "ImagingSelection" })
        private final List<Reference> spatialReference;
        @Binding(
            bindingName = "BodyStructureQualifier",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Concepts modifying the anatomic location.",
            valueSet = "http://hl7.org/fhir/ValueSet/bodystructure-relative-location"
        )
        private final List<CodeableConcept> qualifier;

        private IncludedStructure(Builder builder) {
            super(builder);
            structure = builder.structure;
            laterality = builder.laterality;
            bodyLandmarkOrientation = Collections.unmodifiableList(builder.bodyLandmarkOrientation);
            spatialReference = Collections.unmodifiableList(builder.spatialReference);
            qualifier = Collections.unmodifiableList(builder.qualifier);
        }

        /**
         * Code that represents the included structure.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getStructure() {
            return structure;
        }

        /**
         * Code that represents the included structure laterality.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getLaterality() {
            return laterality;
        }

        /**
         * Body locations in relation to a specific body landmark (tatoo, scar, other body structure).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link BodyLandmarkOrientation} that may be empty.
         */
        public List<BodyLandmarkOrientation> getBodyLandmarkOrientation() {
            return bodyLandmarkOrientation;
        }

        /**
         * XY or XYZ-coordinate orientation for structure.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getSpatialReference() {
            return spatialReference;
        }

        /**
         * Code that represents the included structure qualifier.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getQualifier() {
            return qualifier;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (structure != null) || 
                (laterality != null) || 
                !bodyLandmarkOrientation.isEmpty() || 
                !spatialReference.isEmpty() || 
                !qualifier.isEmpty();
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
                    accept(structure, "structure", visitor);
                    accept(laterality, "laterality", visitor);
                    accept(bodyLandmarkOrientation, "bodyLandmarkOrientation", visitor, BodyLandmarkOrientation.class);
                    accept(spatialReference, "spatialReference", visitor, Reference.class);
                    accept(qualifier, "qualifier", visitor, CodeableConcept.class);
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
            IncludedStructure other = (IncludedStructure) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(structure, other.structure) && 
                Objects.equals(laterality, other.laterality) && 
                Objects.equals(bodyLandmarkOrientation, other.bodyLandmarkOrientation) && 
                Objects.equals(spatialReference, other.spatialReference) && 
                Objects.equals(qualifier, other.qualifier);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    structure, 
                    laterality, 
                    bodyLandmarkOrientation, 
                    spatialReference, 
                    qualifier);
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
            private CodeableConcept structure;
            private CodeableConcept laterality;
            private List<BodyLandmarkOrientation> bodyLandmarkOrientation = new ArrayList<>();
            private List<Reference> spatialReference = new ArrayList<>();
            private List<CodeableConcept> qualifier = new ArrayList<>();

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
             * Code that represents the included structure.
             * 
             * <p>This element is required.
             * 
             * @param structure
             *     Code that represents the included structure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder structure(CodeableConcept structure) {
                this.structure = structure;
                return this;
            }

            /**
             * Code that represents the included structure laterality.
             * 
             * @param laterality
             *     Code that represents the included structure laterality
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder laterality(CodeableConcept laterality) {
                this.laterality = laterality;
                return this;
            }

            /**
             * Body locations in relation to a specific body landmark (tatoo, scar, other body structure).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param bodyLandmarkOrientation
             *     Landmark relative location
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder bodyLandmarkOrientation(BodyLandmarkOrientation... bodyLandmarkOrientation) {
                for (BodyLandmarkOrientation value : bodyLandmarkOrientation) {
                    this.bodyLandmarkOrientation.add(value);
                }
                return this;
            }

            /**
             * Body locations in relation to a specific body landmark (tatoo, scar, other body structure).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param bodyLandmarkOrientation
             *     Landmark relative location
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder bodyLandmarkOrientation(Collection<BodyLandmarkOrientation> bodyLandmarkOrientation) {
                this.bodyLandmarkOrientation = new ArrayList<>(bodyLandmarkOrientation);
                return this;
            }

            /**
             * XY or XYZ-coordinate orientation for structure.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link ImagingSelection}</li>
             * </ul>
             * 
             * @param spatialReference
             *     Cartesian reference for structure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder spatialReference(Reference... spatialReference) {
                for (Reference value : spatialReference) {
                    this.spatialReference.add(value);
                }
                return this;
            }

            /**
             * XY or XYZ-coordinate orientation for structure.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link ImagingSelection}</li>
             * </ul>
             * 
             * @param spatialReference
             *     Cartesian reference for structure
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder spatialReference(Collection<Reference> spatialReference) {
                this.spatialReference = new ArrayList<>(spatialReference);
                return this;
            }

            /**
             * Code that represents the included structure qualifier.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param qualifier
             *     Code that represents the included structure qualifier
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder qualifier(CodeableConcept... qualifier) {
                for (CodeableConcept value : qualifier) {
                    this.qualifier.add(value);
                }
                return this;
            }

            /**
             * Code that represents the included structure qualifier.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param qualifier
             *     Code that represents the included structure qualifier
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder qualifier(Collection<CodeableConcept> qualifier) {
                this.qualifier = new ArrayList<>(qualifier);
                return this;
            }

            /**
             * Build the {@link IncludedStructure}
             * 
             * <p>Required elements:
             * <ul>
             * <li>structure</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link IncludedStructure}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid IncludedStructure per the base specification
             */
            @Override
            public IncludedStructure build() {
                IncludedStructure includedStructure = new IncludedStructure(this);
                if (validating) {
                    validate(includedStructure);
                }
                return includedStructure;
            }

            protected void validate(IncludedStructure includedStructure) {
                super.validate(includedStructure);
                ValidationSupport.requireNonNull(includedStructure.structure, "structure");
                ValidationSupport.checkList(includedStructure.bodyLandmarkOrientation, "bodyLandmarkOrientation", BodyLandmarkOrientation.class);
                ValidationSupport.checkList(includedStructure.spatialReference, "spatialReference", Reference.class);
                ValidationSupport.checkList(includedStructure.qualifier, "qualifier", CodeableConcept.class);
                ValidationSupport.checkReferenceType(includedStructure.spatialReference, "spatialReference", "ImagingSelection");
                ValidationSupport.requireValueOrChildren(includedStructure);
            }

            protected Builder from(IncludedStructure includedStructure) {
                super.from(includedStructure);
                structure = includedStructure.structure;
                laterality = includedStructure.laterality;
                bodyLandmarkOrientation.addAll(includedStructure.bodyLandmarkOrientation);
                spatialReference.addAll(includedStructure.spatialReference);
                qualifier.addAll(includedStructure.qualifier);
                return this;
            }
        }

        /**
         * Body locations in relation to a specific body landmark (tatoo, scar, other body structure).
         */
        public static class BodyLandmarkOrientation extends BackboneElement {
            @Binding(
                bindingName = "bodyLandmarkOrientationLandmarkDescription",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Select SNOMED code system values. Values used in a podiatry setting to decsribe landmarks on the body.",
                valueSet = "http://hl7.org/fhir/ValueSet/body-site"
            )
            private final List<CodeableConcept> landmarkDescription;
            @Binding(
                bindingName = "bodyLandmarkOrientationClockFacePosition",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Select SNOMED CT codes. A set of codes that describe a things orientation based on a hourly positions of a clock face.",
                valueSet = "http://hl7.org/fhir/ValueSet/bodystructure-bodylandmarkorientation-clockface-position"
            )
            private final List<CodeableConcept> clockFacePosition;
            private final List<DistanceFromLandmark> distanceFromLandmark;
            @Binding(
                bindingName = "bodyLandmarkOrientationSurfaceOrientation",
                strength = BindingStrength.Value.PREFERRED,
                description = "Select SNOMED code system values. The surface area a body location is in relation to a landmark.",
                valueSet = "http://hl7.org/fhir/ValueSet/bodystructure-relative-location"
            )
            private final List<CodeableConcept> surfaceOrientation;

            private BodyLandmarkOrientation(Builder builder) {
                super(builder);
                landmarkDescription = Collections.unmodifiableList(builder.landmarkDescription);
                clockFacePosition = Collections.unmodifiableList(builder.clockFacePosition);
                distanceFromLandmark = Collections.unmodifiableList(builder.distanceFromLandmark);
                surfaceOrientation = Collections.unmodifiableList(builder.surfaceOrientation);
            }

            /**
             * A description of a landmark on the body used as a reference to locate something else.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getLandmarkDescription() {
                return landmarkDescription;
            }

            /**
             * An description of the direction away from a landmark something is located based on a radial clock dial.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getClockFacePosition() {
                return clockFacePosition;
            }

            /**
             * The distance in centimeters a certain observation is made from a body landmark.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link DistanceFromLandmark} that may be empty.
             */
            public List<DistanceFromLandmark> getDistanceFromLandmark() {
                return distanceFromLandmark;
            }

            /**
             * The surface area a body location is in relation to a landmark.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getSurfaceOrientation() {
                return surfaceOrientation;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !landmarkDescription.isEmpty() || 
                    !clockFacePosition.isEmpty() || 
                    !distanceFromLandmark.isEmpty() || 
                    !surfaceOrientation.isEmpty();
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
                        accept(landmarkDescription, "landmarkDescription", visitor, CodeableConcept.class);
                        accept(clockFacePosition, "clockFacePosition", visitor, CodeableConcept.class);
                        accept(distanceFromLandmark, "distanceFromLandmark", visitor, DistanceFromLandmark.class);
                        accept(surfaceOrientation, "surfaceOrientation", visitor, CodeableConcept.class);
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
                BodyLandmarkOrientation other = (BodyLandmarkOrientation) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(landmarkDescription, other.landmarkDescription) && 
                    Objects.equals(clockFacePosition, other.clockFacePosition) && 
                    Objects.equals(distanceFromLandmark, other.distanceFromLandmark) && 
                    Objects.equals(surfaceOrientation, other.surfaceOrientation);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        landmarkDescription, 
                        clockFacePosition, 
                        distanceFromLandmark, 
                        surfaceOrientation);
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
                private List<CodeableConcept> landmarkDescription = new ArrayList<>();
                private List<CodeableConcept> clockFacePosition = new ArrayList<>();
                private List<DistanceFromLandmark> distanceFromLandmark = new ArrayList<>();
                private List<CodeableConcept> surfaceOrientation = new ArrayList<>();

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
                 * A description of a landmark on the body used as a reference to locate something else.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param landmarkDescription
                 *     Body ]andmark description
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder landmarkDescription(CodeableConcept... landmarkDescription) {
                    for (CodeableConcept value : landmarkDescription) {
                        this.landmarkDescription.add(value);
                    }
                    return this;
                }

                /**
                 * A description of a landmark on the body used as a reference to locate something else.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param landmarkDescription
                 *     Body ]andmark description
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder landmarkDescription(Collection<CodeableConcept> landmarkDescription) {
                    this.landmarkDescription = new ArrayList<>(landmarkDescription);
                    return this;
                }

                /**
                 * An description of the direction away from a landmark something is located based on a radial clock dial.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param clockFacePosition
                 *     Clockface orientation
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder clockFacePosition(CodeableConcept... clockFacePosition) {
                    for (CodeableConcept value : clockFacePosition) {
                        this.clockFacePosition.add(value);
                    }
                    return this;
                }

                /**
                 * An description of the direction away from a landmark something is located based on a radial clock dial.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param clockFacePosition
                 *     Clockface orientation
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder clockFacePosition(Collection<CodeableConcept> clockFacePosition) {
                    this.clockFacePosition = new ArrayList<>(clockFacePosition);
                    return this;
                }

                /**
                 * The distance in centimeters a certain observation is made from a body landmark.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param distanceFromLandmark
                 *     Landmark relative location
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder distanceFromLandmark(DistanceFromLandmark... distanceFromLandmark) {
                    for (DistanceFromLandmark value : distanceFromLandmark) {
                        this.distanceFromLandmark.add(value);
                    }
                    return this;
                }

                /**
                 * The distance in centimeters a certain observation is made from a body landmark.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param distanceFromLandmark
                 *     Landmark relative location
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder distanceFromLandmark(Collection<DistanceFromLandmark> distanceFromLandmark) {
                    this.distanceFromLandmark = new ArrayList<>(distanceFromLandmark);
                    return this;
                }

                /**
                 * The surface area a body location is in relation to a landmark.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param surfaceOrientation
                 *     Relative landmark surface orientation
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder surfaceOrientation(CodeableConcept... surfaceOrientation) {
                    for (CodeableConcept value : surfaceOrientation) {
                        this.surfaceOrientation.add(value);
                    }
                    return this;
                }

                /**
                 * The surface area a body location is in relation to a landmark.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param surfaceOrientation
                 *     Relative landmark surface orientation
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder surfaceOrientation(Collection<CodeableConcept> surfaceOrientation) {
                    this.surfaceOrientation = new ArrayList<>(surfaceOrientation);
                    return this;
                }

                /**
                 * Build the {@link BodyLandmarkOrientation}
                 * 
                 * @return
                 *     An immutable object of type {@link BodyLandmarkOrientation}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid BodyLandmarkOrientation per the base specification
                 */
                @Override
                public BodyLandmarkOrientation build() {
                    BodyLandmarkOrientation bodyLandmarkOrientation = new BodyLandmarkOrientation(this);
                    if (validating) {
                        validate(bodyLandmarkOrientation);
                    }
                    return bodyLandmarkOrientation;
                }

                protected void validate(BodyLandmarkOrientation bodyLandmarkOrientation) {
                    super.validate(bodyLandmarkOrientation);
                    ValidationSupport.checkList(bodyLandmarkOrientation.landmarkDescription, "landmarkDescription", CodeableConcept.class);
                    ValidationSupport.checkList(bodyLandmarkOrientation.clockFacePosition, "clockFacePosition", CodeableConcept.class);
                    ValidationSupport.checkList(bodyLandmarkOrientation.distanceFromLandmark, "distanceFromLandmark", DistanceFromLandmark.class);
                    ValidationSupport.checkList(bodyLandmarkOrientation.surfaceOrientation, "surfaceOrientation", CodeableConcept.class);
                    ValidationSupport.requireValueOrChildren(bodyLandmarkOrientation);
                }

                protected Builder from(BodyLandmarkOrientation bodyLandmarkOrientation) {
                    super.from(bodyLandmarkOrientation);
                    landmarkDescription.addAll(bodyLandmarkOrientation.landmarkDescription);
                    clockFacePosition.addAll(bodyLandmarkOrientation.clockFacePosition);
                    distanceFromLandmark.addAll(bodyLandmarkOrientation.distanceFromLandmark);
                    surfaceOrientation.addAll(bodyLandmarkOrientation.surfaceOrientation);
                    return this;
                }
            }

            /**
             * The distance in centimeters a certain observation is made from a body landmark.
             */
            public static class DistanceFromLandmark extends BackboneElement {
                @Binding(
                    bindingName = "DeviceType",
                    strength = BindingStrength.Value.EXAMPLE,
                    description = "Codes to identify medical devices.",
                    valueSet = "http://hl7.org/fhir/ValueSet/device-type"
                )
                private final List<CodeableReference> device;
                private final List<Quantity> value;

                private DistanceFromLandmark(Builder builder) {
                    super(builder);
                    device = Collections.unmodifiableList(builder.device);
                    value = Collections.unmodifiableList(builder.value);
                }

                /**
                 * An instrument, tool, analyzer, etc. used in the measurement.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
                 */
                public List<CodeableReference> getDevice() {
                    return device;
                }

                /**
                 * The measured distance (e.g., in cm) from a body landmark.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Quantity} that may be empty.
                 */
                public List<Quantity> getValue() {
                    return value;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        !device.isEmpty() || 
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
                            accept(device, "device", visitor, CodeableReference.class);
                            accept(value, "value", visitor, Quantity.class);
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
                    DistanceFromLandmark other = (DistanceFromLandmark) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(device, other.device) && 
                        Objects.equals(value, other.value);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            device, 
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
                    private List<CodeableReference> device = new ArrayList<>();
                    private List<Quantity> value = new ArrayList<>();

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
                     * An instrument, tool, analyzer, etc. used in the measurement.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param device
                     *     Measurement device
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder device(CodeableReference... device) {
                        for (CodeableReference value : device) {
                            this.device.add(value);
                        }
                        return this;
                    }

                    /**
                     * An instrument, tool, analyzer, etc. used in the measurement.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param device
                     *     Measurement device
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder device(Collection<CodeableReference> device) {
                        this.device = new ArrayList<>(device);
                        return this;
                    }

                    /**
                     * The measured distance (e.g., in cm) from a body landmark.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param value
                     *     Measured distance from body landmark
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder value(Quantity... value) {
                        for (Quantity _value : value) {
                            this.value.add(_value);
                        }
                        return this;
                    }

                    /**
                     * The measured distance (e.g., in cm) from a body landmark.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param value
                     *     Measured distance from body landmark
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder value(Collection<Quantity> value) {
                        this.value = new ArrayList<>(value);
                        return this;
                    }

                    /**
                     * Build the {@link DistanceFromLandmark}
                     * 
                     * @return
                     *     An immutable object of type {@link DistanceFromLandmark}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid DistanceFromLandmark per the base specification
                     */
                    @Override
                    public DistanceFromLandmark build() {
                        DistanceFromLandmark distanceFromLandmark = new DistanceFromLandmark(this);
                        if (validating) {
                            validate(distanceFromLandmark);
                        }
                        return distanceFromLandmark;
                    }

                    protected void validate(DistanceFromLandmark distanceFromLandmark) {
                        super.validate(distanceFromLandmark);
                        ValidationSupport.checkList(distanceFromLandmark.device, "device", CodeableReference.class);
                        ValidationSupport.checkList(distanceFromLandmark.value, "value", Quantity.class);
                        ValidationSupport.requireValueOrChildren(distanceFromLandmark);
                    }

                    protected Builder from(DistanceFromLandmark distanceFromLandmark) {
                        super.from(distanceFromLandmark);
                        device.addAll(distanceFromLandmark.device);
                        value.addAll(distanceFromLandmark.value);
                        return this;
                    }
                }
            }
        }
    }
}
