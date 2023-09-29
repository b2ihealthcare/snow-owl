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
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.OrientationType;
import org.linuxforhealth.fhir.model.r5.type.code.SequenceType;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StrandType;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Representation of a molecular sequence.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "msq-5",
    level = "Rule",
    location = "MolecularSequence.relative.startingSequence",
    description = "Both genomeAssembly and chromosome must be both contained if either one of them is contained",
    expression = "chromosome.exists() = genomeAssembly.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/MolecularSequence"
)
@Constraint(
    id = "msq-6",
    level = "Rule",
    location = "MolecularSequence.relative.startingSequence",
    description = "Have and only have one of the following elements in startingSequence: 1. genomeAssembly; 2 sequence",
    expression = "genomeAssembly.exists() xor sequence.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/MolecularSequence"
)
@Constraint(
    id = "molecularSequence-7",
    level = "Warning",
    location = "relative.coordinateSystem",
    description = "SHALL, if possible, contain a code from value set http://loinc.org/LL5323-2/",
    expression = "$this.memberOf('http://loinc.org/LL5323-2/', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/MolecularSequence",
    generated = true
)
@Constraint(
    id = "molecularSequence-8",
    level = "Warning",
    location = "relative.startingSequence.genomeAssembly",
    description = "SHALL, if possible, contain a code from value set http://loinc.org/LL1040-6/",
    expression = "$this.memberOf('http://loinc.org/LL1040-6/', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/MolecularSequence",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class MolecularSequence extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "sequenceType",
        strength = BindingStrength.Value.REQUIRED,
        description = "Type if a sequence -- DNA, RNA, or amino acid sequence.",
        valueSet = "http://hl7.org/fhir/ValueSet/sequence-type|5.0.0"
    )
    private final SequenceType type;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Substance", "BiologicallyDerivedProduct", "NutritionProduct" })
    private final Reference subject;
    @Summary
    private final List<Reference> focus;
    @Summary
    @ReferenceTarget({ "Specimen" })
    private final Reference specimen;
    @Summary
    @ReferenceTarget({ "Device" })
    private final Reference device;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference performer;
    @Summary
    private final String literal;
    @Summary
    private final List<Attachment> formatted;
    @Summary
    private final List<Relative> relative;

    private MolecularSequence(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        type = builder.type;
        subject = builder.subject;
        focus = Collections.unmodifiableList(builder.focus);
        specimen = builder.specimen;
        device = builder.device;
        performer = builder.performer;
        literal = builder.literal;
        formatted = Collections.unmodifiableList(builder.formatted);
        relative = Collections.unmodifiableList(builder.relative);
    }

    /**
     * A unique identifier for this particular sequence instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Amino Acid Sequence/ DNA Sequence / RNA Sequence.
     * 
     * @return
     *     An immutable object of type {@link SequenceType} that may be null.
     */
    public SequenceType getType() {
        return type;
    }

    /**
     * Indicates the subject this sequence is associated too.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The actual focus of a molecular sequence when it is not the patient of record representing something or someone 
     * associated with the patient such as a spouse, parent, child, or sibling. For example, in trio testing, the subject 
     * would be the child (proband) and the focus would be the parent.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getFocus() {
        return focus;
    }

    /**
     * Specimen used for sequencing.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSpecimen() {
        return specimen;
    }

    /**
     * The method for sequencing, for example, chip information.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getDevice() {
        return device;
    }

    /**
     * The organization or lab that should be responsible for this result.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPerformer() {
        return performer;
    }

    /**
     * Sequence that was observed.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Sequence that was observed as file content. Can be an actual file contents, or referenced by a URL to an external 
     * system.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Attachment} that may be empty.
     */
    public List<Attachment> getFormatted() {
        return formatted;
    }

    /**
     * A sequence defined relative to another sequence.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Relative} that may be empty.
     */
    public List<Relative> getRelative() {
        return relative;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (type != null) || 
            (subject != null) || 
            !focus.isEmpty() || 
            (specimen != null) || 
            (device != null) || 
            (performer != null) || 
            (literal != null) || 
            !formatted.isEmpty() || 
            !relative.isEmpty();
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
                accept(type, "type", visitor);
                accept(subject, "subject", visitor);
                accept(focus, "focus", visitor, Reference.class);
                accept(specimen, "specimen", visitor);
                accept(device, "device", visitor);
                accept(performer, "performer", visitor);
                accept(literal, "literal", visitor);
                accept(formatted, "formatted", visitor, Attachment.class);
                accept(relative, "relative", visitor, Relative.class);
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
        MolecularSequence other = (MolecularSequence) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(type, other.type) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(focus, other.focus) && 
            Objects.equals(specimen, other.specimen) && 
            Objects.equals(device, other.device) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(literal, other.literal) && 
            Objects.equals(formatted, other.formatted) && 
            Objects.equals(relative, other.relative);
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
                type, 
                subject, 
                focus, 
                specimen, 
                device, 
                performer, 
                literal, 
                formatted, 
                relative);
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
        private SequenceType type;
        private Reference subject;
        private List<Reference> focus = new ArrayList<>();
        private Reference specimen;
        private Reference device;
        private Reference performer;
        private String literal;
        private List<Attachment> formatted = new ArrayList<>();
        private List<Relative> relative = new ArrayList<>();

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
         * A unique identifier for this particular sequence instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Unique ID for this particular sequence
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
         * A unique identifier for this particular sequence instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Unique ID for this particular sequence
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
         * Amino Acid Sequence/ DNA Sequence / RNA Sequence.
         * 
         * @param type
         *     aa | dna | rna
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(SequenceType type) {
            this.type = type;
            return this;
        }

        /**
         * Indicates the subject this sequence is associated too.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Substance}</li>
         * <li>{@link BiologicallyDerivedProduct}</li>
         * <li>{@link NutritionProduct}</li>
         * </ul>
         * 
         * @param subject
         *     Subject this sequence is associated too
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The actual focus of a molecular sequence when it is not the patient of record representing something or someone 
         * associated with the patient such as a spouse, parent, child, or sibling. For example, in trio testing, the subject 
         * would be the child (proband) and the focus would be the parent.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focus
         *     What the molecular sequence is about, when it is not about the subject of record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder focus(Reference... focus) {
            for (Reference value : focus) {
                this.focus.add(value);
            }
            return this;
        }

        /**
         * The actual focus of a molecular sequence when it is not the patient of record representing something or someone 
         * associated with the patient such as a spouse, parent, child, or sibling. For example, in trio testing, the subject 
         * would be the child (proband) and the focus would be the parent.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focus
         *     What the molecular sequence is about, when it is not about the subject of record
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder focus(Collection<Reference> focus) {
            this.focus = new ArrayList<>(focus);
            return this;
        }

        /**
         * Specimen used for sequencing.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Specimen}</li>
         * </ul>
         * 
         * @param specimen
         *     Specimen used for sequencing
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specimen(Reference specimen) {
            this.specimen = specimen;
            return this;
        }

        /**
         * The method for sequencing, for example, chip information.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param device
         *     The method for sequencing
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder device(Reference device) {
            this.device = device;
            return this;
        }

        /**
         * The organization or lab that should be responsible for this result.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param performer
         *     Who should be responsible for test result
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performer(Reference performer) {
            this.performer = performer;
            return this;
        }

        /**
         * Convenience method for setting {@code literal}.
         * 
         * @param literal
         *     Sequence that was observed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #literal(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder literal(java.lang.String literal) {
            this.literal = (literal == null) ? null : String.of(literal);
            return this;
        }

        /**
         * Sequence that was observed.
         * 
         * @param literal
         *     Sequence that was observed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder literal(String literal) {
            this.literal = literal;
            return this;
        }

        /**
         * Sequence that was observed as file content. Can be an actual file contents, or referenced by a URL to an external 
         * system.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param formatted
         *     Embedded file or a link (URL) which contains content to represent the sequence
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder formatted(Attachment... formatted) {
            for (Attachment value : formatted) {
                this.formatted.add(value);
            }
            return this;
        }

        /**
         * Sequence that was observed as file content. Can be an actual file contents, or referenced by a URL to an external 
         * system.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param formatted
         *     Embedded file or a link (URL) which contains content to represent the sequence
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder formatted(Collection<Attachment> formatted) {
            this.formatted = new ArrayList<>(formatted);
            return this;
        }

        /**
         * A sequence defined relative to another sequence.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relative
         *     A sequence defined relative to another sequence
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relative(Relative... relative) {
            for (Relative value : relative) {
                this.relative.add(value);
            }
            return this;
        }

        /**
         * A sequence defined relative to another sequence.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relative
         *     A sequence defined relative to another sequence
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relative(Collection<Relative> relative) {
            this.relative = new ArrayList<>(relative);
            return this;
        }

        /**
         * Build the {@link MolecularSequence}
         * 
         * @return
         *     An immutable object of type {@link MolecularSequence}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid MolecularSequence per the base specification
         */
        @Override
        public MolecularSequence build() {
            MolecularSequence molecularSequence = new MolecularSequence(this);
            if (validating) {
                validate(molecularSequence);
            }
            return molecularSequence;
        }

        protected void validate(MolecularSequence molecularSequence) {
            super.validate(molecularSequence);
            ValidationSupport.checkList(molecularSequence.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(molecularSequence.focus, "focus", Reference.class);
            ValidationSupport.checkList(molecularSequence.formatted, "formatted", Attachment.class);
            ValidationSupport.checkList(molecularSequence.relative, "relative", Relative.class);
            ValidationSupport.checkReferenceType(molecularSequence.subject, "subject", "Patient", "Group", "Substance", "BiologicallyDerivedProduct", "NutritionProduct");
            ValidationSupport.checkReferenceType(molecularSequence.specimen, "specimen", "Specimen");
            ValidationSupport.checkReferenceType(molecularSequence.device, "device", "Device");
            ValidationSupport.checkReferenceType(molecularSequence.performer, "performer", "Organization");
        }

        protected Builder from(MolecularSequence molecularSequence) {
            super.from(molecularSequence);
            identifier.addAll(molecularSequence.identifier);
            type = molecularSequence.type;
            subject = molecularSequence.subject;
            focus.addAll(molecularSequence.focus);
            specimen = molecularSequence.specimen;
            device = molecularSequence.device;
            performer = molecularSequence.performer;
            literal = molecularSequence.literal;
            formatted.addAll(molecularSequence.formatted);
            relative.addAll(molecularSequence.relative);
            return this;
        }
    }

    /**
     * A sequence defined relative to another sequence.
     */
    public static class Relative extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "LL5323-2",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Genomic coordinate system.",
            valueSet = "http://loinc.org/LL5323-2/"
        )
        @Required
        private final CodeableConcept coordinateSystem;
        private final Integer ordinalPosition;
        private final Range sequenceRange;
        @Summary
        private final StartingSequence startingSequence;
        @Summary
        private final List<Edit> edit;

        private Relative(Builder builder) {
            super(builder);
            coordinateSystem = builder.coordinateSystem;
            ordinalPosition = builder.ordinalPosition;
            sequenceRange = builder.sequenceRange;
            startingSequence = builder.startingSequence;
            edit = Collections.unmodifiableList(builder.edit);
        }

        /**
         * These are different ways of identifying nucleotides or amino acids within a sequence. Different databases and file 
         * types may use different systems. For detail definitions, see https://loinc.org/92822-6/ for more detail.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCoordinateSystem() {
            return coordinateSystem;
        }

        /**
         * Indicates the order in which the sequence should be considered when putting multiple 'relative' elements together.
         * 
         * @return
         *     An immutable object of type {@link Integer} that may be null.
         */
        public Integer getOrdinalPosition() {
            return ordinalPosition;
        }

        /**
         * Indicates the nucleotide range in the composed sequence when multiple 'relative' elements are used together.
         * 
         * @return
         *     An immutable object of type {@link Range} that may be null.
         */
        public Range getSequenceRange() {
            return sequenceRange;
        }

        /**
         * A sequence that is used as a starting sequence to describe variants that are present in a sequence analyzed.
         * 
         * @return
         *     An immutable object of type {@link StartingSequence} that may be null.
         */
        public StartingSequence getStartingSequence() {
            return startingSequence;
        }

        /**
         * Changes in sequence from the starting sequence.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Edit} that may be empty.
         */
        public List<Edit> getEdit() {
            return edit;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (coordinateSystem != null) || 
                (ordinalPosition != null) || 
                (sequenceRange != null) || 
                (startingSequence != null) || 
                !edit.isEmpty();
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
                    accept(coordinateSystem, "coordinateSystem", visitor);
                    accept(ordinalPosition, "ordinalPosition", visitor);
                    accept(sequenceRange, "sequenceRange", visitor);
                    accept(startingSequence, "startingSequence", visitor);
                    accept(edit, "edit", visitor, Edit.class);
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
            Relative other = (Relative) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(coordinateSystem, other.coordinateSystem) && 
                Objects.equals(ordinalPosition, other.ordinalPosition) && 
                Objects.equals(sequenceRange, other.sequenceRange) && 
                Objects.equals(startingSequence, other.startingSequence) && 
                Objects.equals(edit, other.edit);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    coordinateSystem, 
                    ordinalPosition, 
                    sequenceRange, 
                    startingSequence, 
                    edit);
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
            private CodeableConcept coordinateSystem;
            private Integer ordinalPosition;
            private Range sequenceRange;
            private StartingSequence startingSequence;
            private List<Edit> edit = new ArrayList<>();

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
             * These are different ways of identifying nucleotides or amino acids within a sequence. Different databases and file 
             * types may use different systems. For detail definitions, see https://loinc.org/92822-6/ for more detail.
             * 
             * <p>This element is required.
             * 
             * @param coordinateSystem
             *     Ways of identifying nucleotides or amino acids within a sequence
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder coordinateSystem(CodeableConcept coordinateSystem) {
                this.coordinateSystem = coordinateSystem;
                return this;
            }

            /**
             * Convenience method for setting {@code ordinalPosition}.
             * 
             * @param ordinalPosition
             *     Indicates the order in which the sequence should be considered when putting multiple 'relative' elements together
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #ordinalPosition(org.linuxforhealth.fhir.model.type.Integer)
             */
            public Builder ordinalPosition(java.lang.Integer ordinalPosition) {
                this.ordinalPosition = (ordinalPosition == null) ? null : Integer.of(ordinalPosition);
                return this;
            }

            /**
             * Indicates the order in which the sequence should be considered when putting multiple 'relative' elements together.
             * 
             * @param ordinalPosition
             *     Indicates the order in which the sequence should be considered when putting multiple 'relative' elements together
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder ordinalPosition(Integer ordinalPosition) {
                this.ordinalPosition = ordinalPosition;
                return this;
            }

            /**
             * Indicates the nucleotide range in the composed sequence when multiple 'relative' elements are used together.
             * 
             * @param sequenceRange
             *     Indicates the nucleotide range in the composed sequence when multiple 'relative' elements are used together
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder sequenceRange(Range sequenceRange) {
                this.sequenceRange = sequenceRange;
                return this;
            }

            /**
             * A sequence that is used as a starting sequence to describe variants that are present in a sequence analyzed.
             * 
             * @param startingSequence
             *     A sequence used as starting sequence
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder startingSequence(StartingSequence startingSequence) {
                this.startingSequence = startingSequence;
                return this;
            }

            /**
             * Changes in sequence from the starting sequence.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param edit
             *     Changes in sequence from the starting sequence
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder edit(Edit... edit) {
                for (Edit value : edit) {
                    this.edit.add(value);
                }
                return this;
            }

            /**
             * Changes in sequence from the starting sequence.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param edit
             *     Changes in sequence from the starting sequence
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder edit(Collection<Edit> edit) {
                this.edit = new ArrayList<>(edit);
                return this;
            }

            /**
             * Build the {@link Relative}
             * 
             * <p>Required elements:
             * <ul>
             * <li>coordinateSystem</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Relative}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Relative per the base specification
             */
            @Override
            public Relative build() {
                Relative relative = new Relative(this);
                if (validating) {
                    validate(relative);
                }
                return relative;
            }

            protected void validate(Relative relative) {
                super.validate(relative);
                ValidationSupport.requireNonNull(relative.coordinateSystem, "coordinateSystem");
                ValidationSupport.checkList(relative.edit, "edit", Edit.class);
                ValidationSupport.requireValueOrChildren(relative);
            }

            protected Builder from(Relative relative) {
                super.from(relative);
                coordinateSystem = relative.coordinateSystem;
                ordinalPosition = relative.ordinalPosition;
                sequenceRange = relative.sequenceRange;
                startingSequence = relative.startingSequence;
                edit.addAll(relative.edit);
                return this;
            }
        }

        /**
         * A sequence that is used as a starting sequence to describe variants that are present in a sequence analyzed.
         */
        public static class StartingSequence extends BackboneElement {
            @Summary
            @Binding(
                bindingName = "LL1040-6",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "Human reference sequence NCBI build ID.",
                valueSet = "http://loinc.org/LL1040-6/"
            )
            private final CodeableConcept genomeAssembly;
            @Summary
            @Binding(
                bindingName = "LL2938-0",
                strength = BindingStrength.Value.REQUIRED,
                description = "The chromosome containing the sequence.",
                valueSet = "http://loinc.org/LL2938-0/|5.0.0"
            )
            private final CodeableConcept chromosome;
            @Summary
            @ReferenceTarget({ "MolecularSequence" })
            @Choice({ CodeableConcept.class, String.class, Reference.class })
            @Binding(
                bindingName = "Multiple bindings acceptable (NCBI or LRG)",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Multiple bindings acceptable (NCBI or LRG)"
            )
            private final Element sequence;
            @Summary
            private final Integer windowStart;
            @Summary
            private final Integer windowEnd;
            @Summary
            @Binding(
                bindingName = "orientationType",
                strength = BindingStrength.Value.REQUIRED,
                description = "Type for orientation",
                valueSet = "http://hl7.org/fhir/ValueSet/orientation-type|5.0.0"
            )
            private final OrientationType orientation;
            @Summary
            @Binding(
                bindingName = "strandType",
                strength = BindingStrength.Value.REQUIRED,
                description = "Type for strand",
                valueSet = "http://hl7.org/fhir/ValueSet/strand-type|5.0.0"
            )
            private final StrandType strand;

            private StartingSequence(Builder builder) {
                super(builder);
                genomeAssembly = builder.genomeAssembly;
                chromosome = builder.chromosome;
                sequence = builder.sequence;
                windowStart = builder.windowStart;
                windowEnd = builder.windowEnd;
                orientation = builder.orientation;
                strand = builder.strand;
            }

            /**
             * The genome assembly used for starting sequence, e.g. GRCh38.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getGenomeAssembly() {
                return genomeAssembly;
            }

            /**
             * Structural unit composed of a nucleic acid molecule which controls its own replication through the interaction of 
             * specific proteins at one or more origins of replication ([SO:0000340](http://www.sequenceontology.
             * org/browser/current_svn/term/SO:0000340)).
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getChromosome() {
                return chromosome;
            }

            /**
             * The reference sequence that represents the starting sequence.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept}, {@link String} or {@link Reference} that may be null.
             */
            public Element getSequence() {
                return sequence;
            }

            /**
             * Start position of the window on the starting sequence. This value should honor the rules of the coordinateSystem.
             * 
             * @return
             *     An immutable object of type {@link Integer} that may be null.
             */
            public Integer getWindowStart() {
                return windowStart;
            }

            /**
             * End position of the window on the starting sequence. This value should honor the rules of the coordinateSystem.
             * 
             * @return
             *     An immutable object of type {@link Integer} that may be null.
             */
            public Integer getWindowEnd() {
                return windowEnd;
            }

            /**
             * A relative reference to a DNA strand based on gene orientation. The strand that contains the open reading frame of the 
             * gene is the "sense" strand, and the opposite complementary strand is the "antisense" strand.
             * 
             * @return
             *     An immutable object of type {@link OrientationType} that may be null.
             */
            public OrientationType getOrientation() {
                return orientation;
            }

            /**
             * An absolute reference to a strand. The Watson strand is the strand whose 5'-end is on the short arm of the chromosome, 
             * and the Crick strand as the one whose 5'-end is on the long arm.
             * 
             * @return
             *     An immutable object of type {@link StrandType} that may be null.
             */
            public StrandType getStrand() {
                return strand;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (genomeAssembly != null) || 
                    (chromosome != null) || 
                    (sequence != null) || 
                    (windowStart != null) || 
                    (windowEnd != null) || 
                    (orientation != null) || 
                    (strand != null);
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
                        accept(genomeAssembly, "genomeAssembly", visitor);
                        accept(chromosome, "chromosome", visitor);
                        accept(sequence, "sequence", visitor);
                        accept(windowStart, "windowStart", visitor);
                        accept(windowEnd, "windowEnd", visitor);
                        accept(orientation, "orientation", visitor);
                        accept(strand, "strand", visitor);
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
                StartingSequence other = (StartingSequence) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(genomeAssembly, other.genomeAssembly) && 
                    Objects.equals(chromosome, other.chromosome) && 
                    Objects.equals(sequence, other.sequence) && 
                    Objects.equals(windowStart, other.windowStart) && 
                    Objects.equals(windowEnd, other.windowEnd) && 
                    Objects.equals(orientation, other.orientation) && 
                    Objects.equals(strand, other.strand);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        genomeAssembly, 
                        chromosome, 
                        sequence, 
                        windowStart, 
                        windowEnd, 
                        orientation, 
                        strand);
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
                private CodeableConcept genomeAssembly;
                private CodeableConcept chromosome;
                private Element sequence;
                private Integer windowStart;
                private Integer windowEnd;
                private OrientationType orientation;
                private StrandType strand;

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
                 * The genome assembly used for starting sequence, e.g. GRCh38.
                 * 
                 * @param genomeAssembly
                 *     The genome assembly used for starting sequence, e.g. GRCh38
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder genomeAssembly(CodeableConcept genomeAssembly) {
                    this.genomeAssembly = genomeAssembly;
                    return this;
                }

                /**
                 * Structural unit composed of a nucleic acid molecule which controls its own replication through the interaction of 
                 * specific proteins at one or more origins of replication ([SO:0000340](http://www.sequenceontology.
                 * org/browser/current_svn/term/SO:0000340)).
                 * 
                 * @param chromosome
                 *     Chromosome Identifier
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder chromosome(CodeableConcept chromosome) {
                    this.chromosome = chromosome;
                    return this;
                }

                /**
                 * Convenience method for setting {@code sequence} with choice type String.
                 * 
                 * @param sequence
                 *     The reference sequence that represents the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #sequence(Element)
                 */
                public Builder sequence(java.lang.String sequence) {
                    this.sequence = (sequence == null) ? null : String.of(sequence);
                    return this;
                }

                /**
                 * The reference sequence that represents the starting sequence.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link CodeableConcept}</li>
                 * <li>{@link String}</li>
                 * <li>{@link Reference}</li>
                 * </ul>
                 * 
                 * When of type {@link Reference}, the allowed resource types for this reference are:
                 * <ul>
                 * <li>{@link MolecularSequence}</li>
                 * </ul>
                 * 
                 * @param sequence
                 *     The reference sequence that represents the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder sequence(Element sequence) {
                    this.sequence = sequence;
                    return this;
                }

                /**
                 * Convenience method for setting {@code windowStart}.
                 * 
                 * @param windowStart
                 *     Start position of the window on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #windowStart(org.linuxforhealth.fhir.model.type.Integer)
                 */
                public Builder windowStart(java.lang.Integer windowStart) {
                    this.windowStart = (windowStart == null) ? null : Integer.of(windowStart);
                    return this;
                }

                /**
                 * Start position of the window on the starting sequence. This value should honor the rules of the coordinateSystem.
                 * 
                 * @param windowStart
                 *     Start position of the window on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder windowStart(Integer windowStart) {
                    this.windowStart = windowStart;
                    return this;
                }

                /**
                 * Convenience method for setting {@code windowEnd}.
                 * 
                 * @param windowEnd
                 *     End position of the window on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #windowEnd(org.linuxforhealth.fhir.model.type.Integer)
                 */
                public Builder windowEnd(java.lang.Integer windowEnd) {
                    this.windowEnd = (windowEnd == null) ? null : Integer.of(windowEnd);
                    return this;
                }

                /**
                 * End position of the window on the starting sequence. This value should honor the rules of the coordinateSystem.
                 * 
                 * @param windowEnd
                 *     End position of the window on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder windowEnd(Integer windowEnd) {
                    this.windowEnd = windowEnd;
                    return this;
                }

                /**
                 * A relative reference to a DNA strand based on gene orientation. The strand that contains the open reading frame of the 
                 * gene is the "sense" strand, and the opposite complementary strand is the "antisense" strand.
                 * 
                 * @param orientation
                 *     sense | antisense
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder orientation(OrientationType orientation) {
                    this.orientation = orientation;
                    return this;
                }

                /**
                 * An absolute reference to a strand. The Watson strand is the strand whose 5'-end is on the short arm of the chromosome, 
                 * and the Crick strand as the one whose 5'-end is on the long arm.
                 * 
                 * @param strand
                 *     watson | crick
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder strand(StrandType strand) {
                    this.strand = strand;
                    return this;
                }

                /**
                 * Build the {@link StartingSequence}
                 * 
                 * @return
                 *     An immutable object of type {@link StartingSequence}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid StartingSequence per the base specification
                 */
                @Override
                public StartingSequence build() {
                    StartingSequence startingSequence = new StartingSequence(this);
                    if (validating) {
                        validate(startingSequence);
                    }
                    return startingSequence;
                }

                protected void validate(StartingSequence startingSequence) {
                    super.validate(startingSequence);
                    ValidationSupport.choiceElement(startingSequence.sequence, "sequence", CodeableConcept.class, String.class, Reference.class);
                    ValidationSupport.checkReferenceType(startingSequence.sequence, "sequence", "MolecularSequence");
                    ValidationSupport.requireValueOrChildren(startingSequence);
                }

                protected Builder from(StartingSequence startingSequence) {
                    super.from(startingSequence);
                    genomeAssembly = startingSequence.genomeAssembly;
                    chromosome = startingSequence.chromosome;
                    sequence = startingSequence.sequence;
                    windowStart = startingSequence.windowStart;
                    windowEnd = startingSequence.windowEnd;
                    orientation = startingSequence.orientation;
                    strand = startingSequence.strand;
                    return this;
                }
            }
        }

        /**
         * Changes in sequence from the starting sequence.
         */
        public static class Edit extends BackboneElement {
            @Summary
            private final Integer start;
            @Summary
            private final Integer end;
            @Summary
            private final String replacementSequence;
            @Summary
            private final String replacedSequence;

            private Edit(Builder builder) {
                super(builder);
                start = builder.start;
                end = builder.end;
                replacementSequence = builder.replacementSequence;
                replacedSequence = builder.replacedSequence;
            }

            /**
             * Start position of the edit on the starting sequence. If the coordinate system is either 0-based or 1-based, then start 
             * position is inclusive.
             * 
             * @return
             *     An immutable object of type {@link Integer} that may be null.
             */
            public Integer getStart() {
                return start;
            }

            /**
             * End position of the edit on the starting sequence. If the coordinate system is 0-based then end is exclusive and does 
             * not include the last position. If the coordinate system is 1-base, then end is inclusive and includes the last 
             * position.
             * 
             * @return
             *     An immutable object of type {@link Integer} that may be null.
             */
            public Integer getEnd() {
                return end;
            }

            /**
             * Allele that was observed. Nucleotide(s)/amino acids from start position of sequence to stop position of sequence on 
             * the positive (+) strand of the observed sequence. When the sequence type is DNA, it should be the sequence on the 
             * positive (+) strand. This will lay in the range between variant.start and variant.end.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getReplacementSequence() {
                return replacementSequence;
            }

            /**
             * Allele in the starting sequence. Nucleotide(s)/amino acids from start position of sequence to stop position of 
             * sequence on the positive (+) strand of the starting sequence. When the sequence type is DNA, it should be the sequence 
             * on the positive (+) strand. This will lay in the range between variant.start and variant.end.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getReplacedSequence() {
                return replacedSequence;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (start != null) || 
                    (end != null) || 
                    (replacementSequence != null) || 
                    (replacedSequence != null);
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
                        accept(start, "start", visitor);
                        accept(end, "end", visitor);
                        accept(replacementSequence, "replacementSequence", visitor);
                        accept(replacedSequence, "replacedSequence", visitor);
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
                Edit other = (Edit) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(start, other.start) && 
                    Objects.equals(end, other.end) && 
                    Objects.equals(replacementSequence, other.replacementSequence) && 
                    Objects.equals(replacedSequence, other.replacedSequence);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        start, 
                        end, 
                        replacementSequence, 
                        replacedSequence);
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
                private Integer start;
                private Integer end;
                private String replacementSequence;
                private String replacedSequence;

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
                 * Convenience method for setting {@code start}.
                 * 
                 * @param start
                 *     Start position of the edit on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #start(org.linuxforhealth.fhir.model.type.Integer)
                 */
                public Builder start(java.lang.Integer start) {
                    this.start = (start == null) ? null : Integer.of(start);
                    return this;
                }

                /**
                 * Start position of the edit on the starting sequence. If the coordinate system is either 0-based or 1-based, then start 
                 * position is inclusive.
                 * 
                 * @param start
                 *     Start position of the edit on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder start(Integer start) {
                    this.start = start;
                    return this;
                }

                /**
                 * Convenience method for setting {@code end}.
                 * 
                 * @param end
                 *     End position of the edit on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #end(org.linuxforhealth.fhir.model.type.Integer)
                 */
                public Builder end(java.lang.Integer end) {
                    this.end = (end == null) ? null : Integer.of(end);
                    return this;
                }

                /**
                 * End position of the edit on the starting sequence. If the coordinate system is 0-based then end is exclusive and does 
                 * not include the last position. If the coordinate system is 1-base, then end is inclusive and includes the last 
                 * position.
                 * 
                 * @param end
                 *     End position of the edit on the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder end(Integer end) {
                    this.end = end;
                    return this;
                }

                /**
                 * Convenience method for setting {@code replacementSequence}.
                 * 
                 * @param replacementSequence
                 *     Allele that was observed
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #replacementSequence(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder replacementSequence(java.lang.String replacementSequence) {
                    this.replacementSequence = (replacementSequence == null) ? null : String.of(replacementSequence);
                    return this;
                }

                /**
                 * Allele that was observed. Nucleotide(s)/amino acids from start position of sequence to stop position of sequence on 
                 * the positive (+) strand of the observed sequence. When the sequence type is DNA, it should be the sequence on the 
                 * positive (+) strand. This will lay in the range between variant.start and variant.end.
                 * 
                 * @param replacementSequence
                 *     Allele that was observed
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder replacementSequence(String replacementSequence) {
                    this.replacementSequence = replacementSequence;
                    return this;
                }

                /**
                 * Convenience method for setting {@code replacedSequence}.
                 * 
                 * @param replacedSequence
                 *     Allele in the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #replacedSequence(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder replacedSequence(java.lang.String replacedSequence) {
                    this.replacedSequence = (replacedSequence == null) ? null : String.of(replacedSequence);
                    return this;
                }

                /**
                 * Allele in the starting sequence. Nucleotide(s)/amino acids from start position of sequence to stop position of 
                 * sequence on the positive (+) strand of the starting sequence. When the sequence type is DNA, it should be the sequence 
                 * on the positive (+) strand. This will lay in the range between variant.start and variant.end.
                 * 
                 * @param replacedSequence
                 *     Allele in the starting sequence
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder replacedSequence(String replacedSequence) {
                    this.replacedSequence = replacedSequence;
                    return this;
                }

                /**
                 * Build the {@link Edit}
                 * 
                 * @return
                 *     An immutable object of type {@link Edit}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Edit per the base specification
                 */
                @Override
                public Edit build() {
                    Edit edit = new Edit(this);
                    if (validating) {
                        validate(edit);
                    }
                    return edit;
                }

                protected void validate(Edit edit) {
                    super.validate(edit);
                    ValidationSupport.requireValueOrChildren(edit);
                }

                protected Builder from(Edit edit) {
                    super.from(edit);
                    start = edit.start;
                    end = edit.end;
                    replacementSequence = edit.replacementSequence;
                    replacedSequence = edit.replacedSequence;
                    return this;
                }
            }
        }
    }
}
