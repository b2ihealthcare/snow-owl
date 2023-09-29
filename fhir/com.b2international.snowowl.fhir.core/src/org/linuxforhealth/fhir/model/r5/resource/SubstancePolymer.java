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

import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Properties of a substance specific to it being a polymer.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class SubstancePolymer extends DomainResource {
    @Summary
    private final Identifier identifier;
    @Summary
    private final CodeableConcept clazz;
    @Summary
    private final CodeableConcept geometry;
    @Summary
    private final List<CodeableConcept> copolymerConnectivity;
    @Summary
    private final String modification;
    @Summary
    private final List<MonomerSet> monomerSet;
    @Summary
    private final List<Repeat> repeat;

    private SubstancePolymer(Builder builder) {
        super(builder);
        identifier = builder.identifier;
        clazz = builder.clazz;
        geometry = builder.geometry;
        copolymerConnectivity = Collections.unmodifiableList(builder.copolymerConnectivity);
        modification = builder.modification;
        monomerSet = Collections.unmodifiableList(builder.monomerSet);
        repeat = Collections.unmodifiableList(builder.repeat);
    }

    /**
     * A business idenfier for this polymer, but typically this is handled by a SubstanceDefinition identifier.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Overall type of the polymer.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getClazz() {
        return clazz;
    }

    /**
     * Polymer geometry, e.g. linear, branched, cross-linked, network or dendritic.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getGeometry() {
        return geometry;
    }

    /**
     * Descrtibes the copolymer sequence type (polymer connectivity).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCopolymerConnectivity() {
        return copolymerConnectivity;
    }

    /**
     * Todo - this is intended to connect to a repeating full modification structure, also used by Protein and Nucleic Acid . 
     * String is just a placeholder.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getModification() {
        return modification;
    }

    /**
     * Todo.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link MonomerSet} that may be empty.
     */
    public List<MonomerSet> getMonomerSet() {
        return monomerSet;
    }

    /**
     * Specifies and quantifies the repeated units and their configuration.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Repeat} that may be empty.
     */
    public List<Repeat> getRepeat() {
        return repeat;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (identifier != null) || 
            (clazz != null) || 
            (geometry != null) || 
            !copolymerConnectivity.isEmpty() || 
            (modification != null) || 
            !monomerSet.isEmpty() || 
            !repeat.isEmpty();
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
                accept(identifier, "identifier", visitor);
                accept(clazz, "class", visitor);
                accept(geometry, "geometry", visitor);
                accept(copolymerConnectivity, "copolymerConnectivity", visitor, CodeableConcept.class);
                accept(modification, "modification", visitor);
                accept(monomerSet, "monomerSet", visitor, MonomerSet.class);
                accept(repeat, "repeat", visitor, Repeat.class);
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
        SubstancePolymer other = (SubstancePolymer) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(clazz, other.clazz) && 
            Objects.equals(geometry, other.geometry) && 
            Objects.equals(copolymerConnectivity, other.copolymerConnectivity) && 
            Objects.equals(modification, other.modification) && 
            Objects.equals(monomerSet, other.monomerSet) && 
            Objects.equals(repeat, other.repeat);
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
                clazz, 
                geometry, 
                copolymerConnectivity, 
                modification, 
                monomerSet, 
                repeat);
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
        private Identifier identifier;
        private CodeableConcept clazz;
        private CodeableConcept geometry;
        private List<CodeableConcept> copolymerConnectivity = new ArrayList<>();
        private String modification;
        private List<MonomerSet> monomerSet = new ArrayList<>();
        private List<Repeat> repeat = new ArrayList<>();

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
         * A business idenfier for this polymer, but typically this is handled by a SubstanceDefinition identifier.
         * 
         * @param identifier
         *     A business idenfier for this polymer, but typically this is handled by a SubstanceDefinition identifier
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder identifier(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * Overall type of the polymer.
         * 
         * @param clazz
         *     Overall type of the polymer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder clazz(CodeableConcept clazz) {
            this.clazz = clazz;
            return this;
        }

        /**
         * Polymer geometry, e.g. linear, branched, cross-linked, network or dendritic.
         * 
         * @param geometry
         *     Polymer geometry, e.g. linear, branched, cross-linked, network or dendritic
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder geometry(CodeableConcept geometry) {
            this.geometry = geometry;
            return this;
        }

        /**
         * Descrtibes the copolymer sequence type (polymer connectivity).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param copolymerConnectivity
         *     Descrtibes the copolymer sequence type (polymer connectivity)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder copolymerConnectivity(CodeableConcept... copolymerConnectivity) {
            for (CodeableConcept value : copolymerConnectivity) {
                this.copolymerConnectivity.add(value);
            }
            return this;
        }

        /**
         * Descrtibes the copolymer sequence type (polymer connectivity).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param copolymerConnectivity
         *     Descrtibes the copolymer sequence type (polymer connectivity)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder copolymerConnectivity(Collection<CodeableConcept> copolymerConnectivity) {
            this.copolymerConnectivity = new ArrayList<>(copolymerConnectivity);
            return this;
        }

        /**
         * Convenience method for setting {@code modification}.
         * 
         * @param modification
         *     Todo - this is intended to connect to a repeating full modification structure, also used by Protein and Nucleic Acid . 
         *     String is just a placeholder
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #modification(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder modification(java.lang.String modification) {
            this.modification = (modification == null) ? null : String.of(modification);
            return this;
        }

        /**
         * Todo - this is intended to connect to a repeating full modification structure, also used by Protein and Nucleic Acid . 
         * String is just a placeholder.
         * 
         * @param modification
         *     Todo - this is intended to connect to a repeating full modification structure, also used by Protein and Nucleic Acid . 
         *     String is just a placeholder
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder modification(String modification) {
            this.modification = modification;
            return this;
        }

        /**
         * Todo.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param monomerSet
         *     Todo
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder monomerSet(MonomerSet... monomerSet) {
            for (MonomerSet value : monomerSet) {
                this.monomerSet.add(value);
            }
            return this;
        }

        /**
         * Todo.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param monomerSet
         *     Todo
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder monomerSet(Collection<MonomerSet> monomerSet) {
            this.monomerSet = new ArrayList<>(monomerSet);
            return this;
        }

        /**
         * Specifies and quantifies the repeated units and their configuration.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param repeat
         *     Specifies and quantifies the repeated units and their configuration
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder repeat(Repeat... repeat) {
            for (Repeat value : repeat) {
                this.repeat.add(value);
            }
            return this;
        }

        /**
         * Specifies and quantifies the repeated units and their configuration.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param repeat
         *     Specifies and quantifies the repeated units and their configuration
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder repeat(Collection<Repeat> repeat) {
            this.repeat = new ArrayList<>(repeat);
            return this;
        }

        /**
         * Build the {@link SubstancePolymer}
         * 
         * @return
         *     An immutable object of type {@link SubstancePolymer}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid SubstancePolymer per the base specification
         */
        @Override
        public SubstancePolymer build() {
            SubstancePolymer substancePolymer = new SubstancePolymer(this);
            if (validating) {
                validate(substancePolymer);
            }
            return substancePolymer;
        }

        protected void validate(SubstancePolymer substancePolymer) {
            super.validate(substancePolymer);
            ValidationSupport.checkList(substancePolymer.copolymerConnectivity, "copolymerConnectivity", CodeableConcept.class);
            ValidationSupport.checkList(substancePolymer.monomerSet, "monomerSet", MonomerSet.class);
            ValidationSupport.checkList(substancePolymer.repeat, "repeat", Repeat.class);
        }

        protected Builder from(SubstancePolymer substancePolymer) {
            super.from(substancePolymer);
            identifier = substancePolymer.identifier;
            clazz = substancePolymer.clazz;
            geometry = substancePolymer.geometry;
            copolymerConnectivity.addAll(substancePolymer.copolymerConnectivity);
            modification = substancePolymer.modification;
            monomerSet.addAll(substancePolymer.monomerSet);
            repeat.addAll(substancePolymer.repeat);
            return this;
        }
    }

    /**
     * Todo.
     */
    public static class MonomerSet extends BackboneElement {
        @Summary
        private final CodeableConcept ratioType;
        @Summary
        private final List<StartingMaterial> startingMaterial;

        private MonomerSet(Builder builder) {
            super(builder);
            ratioType = builder.ratioType;
            startingMaterial = Collections.unmodifiableList(builder.startingMaterial);
        }

        /**
         * Captures the type of ratio to the entire polymer, e.g. Monomer/Polymer ratio, SRU/Polymer Ratio.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRatioType() {
            return ratioType;
        }

        /**
         * The starting materials - monomer(s) used in the synthesis of the polymer.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link StartingMaterial} that may be empty.
         */
        public List<StartingMaterial> getStartingMaterial() {
            return startingMaterial;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (ratioType != null) || 
                !startingMaterial.isEmpty();
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
                    accept(ratioType, "ratioType", visitor);
                    accept(startingMaterial, "startingMaterial", visitor, StartingMaterial.class);
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
            MonomerSet other = (MonomerSet) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(ratioType, other.ratioType) && 
                Objects.equals(startingMaterial, other.startingMaterial);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    ratioType, 
                    startingMaterial);
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
            private CodeableConcept ratioType;
            private List<StartingMaterial> startingMaterial = new ArrayList<>();

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
             * Captures the type of ratio to the entire polymer, e.g. Monomer/Polymer ratio, SRU/Polymer Ratio.
             * 
             * @param ratioType
             *     Captures the type of ratio to the entire polymer, e.g. Monomer/Polymer ratio, SRU/Polymer Ratio
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder ratioType(CodeableConcept ratioType) {
                this.ratioType = ratioType;
                return this;
            }

            /**
             * The starting materials - monomer(s) used in the synthesis of the polymer.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param startingMaterial
             *     The starting materials - monomer(s) used in the synthesis of the polymer
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder startingMaterial(StartingMaterial... startingMaterial) {
                for (StartingMaterial value : startingMaterial) {
                    this.startingMaterial.add(value);
                }
                return this;
            }

            /**
             * The starting materials - monomer(s) used in the synthesis of the polymer.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param startingMaterial
             *     The starting materials - monomer(s) used in the synthesis of the polymer
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder startingMaterial(Collection<StartingMaterial> startingMaterial) {
                this.startingMaterial = new ArrayList<>(startingMaterial);
                return this;
            }

            /**
             * Build the {@link MonomerSet}
             * 
             * @return
             *     An immutable object of type {@link MonomerSet}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid MonomerSet per the base specification
             */
            @Override
            public MonomerSet build() {
                MonomerSet monomerSet = new MonomerSet(this);
                if (validating) {
                    validate(monomerSet);
                }
                return monomerSet;
            }

            protected void validate(MonomerSet monomerSet) {
                super.validate(monomerSet);
                ValidationSupport.checkList(monomerSet.startingMaterial, "startingMaterial", StartingMaterial.class);
                ValidationSupport.requireValueOrChildren(monomerSet);
            }

            protected Builder from(MonomerSet monomerSet) {
                super.from(monomerSet);
                ratioType = monomerSet.ratioType;
                startingMaterial.addAll(monomerSet.startingMaterial);
                return this;
            }
        }

        /**
         * The starting materials - monomer(s) used in the synthesis of the polymer.
         */
        public static class StartingMaterial extends BackboneElement {
            @Summary
            private final CodeableConcept code;
            @Summary
            private final CodeableConcept category;
            @Summary
            private final Boolean isDefining;
            @Summary
            private final Quantity amount;

            private StartingMaterial(Builder builder) {
                super(builder);
                code = builder.code;
                category = builder.category;
                isDefining = builder.isDefining;
                amount = builder.amount;
            }

            /**
             * The type of substance for this starting material.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCode() {
                return code;
            }

            /**
             * Substance high level category, e.g. chemical substance.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCategory() {
                return category;
            }

            /**
             * Used to specify whether the attribute described is a defining element for the unique identification of the polymer.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getIsDefining() {
                return isDefining;
            }

            /**
             * A percentage.
             * 
             * @return
             *     An immutable object of type {@link Quantity} that may be null.
             */
            public Quantity getAmount() {
                return amount;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (code != null) || 
                    (category != null) || 
                    (isDefining != null) || 
                    (amount != null);
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
                        accept(category, "category", visitor);
                        accept(isDefining, "isDefining", visitor);
                        accept(amount, "amount", visitor);
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
                StartingMaterial other = (StartingMaterial) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(code, other.code) && 
                    Objects.equals(category, other.category) && 
                    Objects.equals(isDefining, other.isDefining) && 
                    Objects.equals(amount, other.amount);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        code, 
                        category, 
                        isDefining, 
                        amount);
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
                private CodeableConcept category;
                private Boolean isDefining;
                private Quantity amount;

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
                 * The type of substance for this starting material.
                 * 
                 * @param code
                 *     The type of substance for this starting material
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(CodeableConcept code) {
                    this.code = code;
                    return this;
                }

                /**
                 * Substance high level category, e.g. chemical substance.
                 * 
                 * @param category
                 *     Substance high level category, e.g. chemical substance
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder category(CodeableConcept category) {
                    this.category = category;
                    return this;
                }

                /**
                 * Convenience method for setting {@code isDefining}.
                 * 
                 * @param isDefining
                 *     Used to specify whether the attribute described is a defining element for the unique identification of the polymer
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #isDefining(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder isDefining(java.lang.Boolean isDefining) {
                    this.isDefining = (isDefining == null) ? null : Boolean.of(isDefining);
                    return this;
                }

                /**
                 * Used to specify whether the attribute described is a defining element for the unique identification of the polymer.
                 * 
                 * @param isDefining
                 *     Used to specify whether the attribute described is a defining element for the unique identification of the polymer
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder isDefining(Boolean isDefining) {
                    this.isDefining = isDefining;
                    return this;
                }

                /**
                 * A percentage.
                 * 
                 * @param amount
                 *     A percentage
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder amount(Quantity amount) {
                    this.amount = amount;
                    return this;
                }

                /**
                 * Build the {@link StartingMaterial}
                 * 
                 * @return
                 *     An immutable object of type {@link StartingMaterial}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid StartingMaterial per the base specification
                 */
                @Override
                public StartingMaterial build() {
                    StartingMaterial startingMaterial = new StartingMaterial(this);
                    if (validating) {
                        validate(startingMaterial);
                    }
                    return startingMaterial;
                }

                protected void validate(StartingMaterial startingMaterial) {
                    super.validate(startingMaterial);
                    ValidationSupport.requireValueOrChildren(startingMaterial);
                }

                protected Builder from(StartingMaterial startingMaterial) {
                    super.from(startingMaterial);
                    code = startingMaterial.code;
                    category = startingMaterial.category;
                    isDefining = startingMaterial.isDefining;
                    amount = startingMaterial.amount;
                    return this;
                }
            }
        }
    }

    /**
     * Specifies and quantifies the repeated units and their configuration.
     */
    public static class Repeat extends BackboneElement {
        @Summary
        private final String averageMolecularFormula;
        @Summary
        private final CodeableConcept repeatUnitAmountType;
        @Summary
        private final List<RepeatUnit> repeatUnit;

        private Repeat(Builder builder) {
            super(builder);
            averageMolecularFormula = builder.averageMolecularFormula;
            repeatUnitAmountType = builder.repeatUnitAmountType;
            repeatUnit = Collections.unmodifiableList(builder.repeatUnit);
        }

        /**
         * A representation of an (average) molecular formula from a polymer.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getAverageMolecularFormula() {
            return averageMolecularFormula;
        }

        /**
         * How the quantitative amount of Structural Repeat Units is captured (e.g. Exact, Numeric, Average).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRepeatUnitAmountType() {
            return repeatUnitAmountType;
        }

        /**
         * An SRU - Structural Repeat Unit.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RepeatUnit} that may be empty.
         */
        public List<RepeatUnit> getRepeatUnit() {
            return repeatUnit;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (averageMolecularFormula != null) || 
                (repeatUnitAmountType != null) || 
                !repeatUnit.isEmpty();
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
                    accept(averageMolecularFormula, "averageMolecularFormula", visitor);
                    accept(repeatUnitAmountType, "repeatUnitAmountType", visitor);
                    accept(repeatUnit, "repeatUnit", visitor, RepeatUnit.class);
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
            Repeat other = (Repeat) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(averageMolecularFormula, other.averageMolecularFormula) && 
                Objects.equals(repeatUnitAmountType, other.repeatUnitAmountType) && 
                Objects.equals(repeatUnit, other.repeatUnit);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    averageMolecularFormula, 
                    repeatUnitAmountType, 
                    repeatUnit);
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
            private String averageMolecularFormula;
            private CodeableConcept repeatUnitAmountType;
            private List<RepeatUnit> repeatUnit = new ArrayList<>();

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
             * Convenience method for setting {@code averageMolecularFormula}.
             * 
             * @param averageMolecularFormula
             *     A representation of an (average) molecular formula from a polymer
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #averageMolecularFormula(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder averageMolecularFormula(java.lang.String averageMolecularFormula) {
                this.averageMolecularFormula = (averageMolecularFormula == null) ? null : String.of(averageMolecularFormula);
                return this;
            }

            /**
             * A representation of an (average) molecular formula from a polymer.
             * 
             * @param averageMolecularFormula
             *     A representation of an (average) molecular formula from a polymer
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder averageMolecularFormula(String averageMolecularFormula) {
                this.averageMolecularFormula = averageMolecularFormula;
                return this;
            }

            /**
             * How the quantitative amount of Structural Repeat Units is captured (e.g. Exact, Numeric, Average).
             * 
             * @param repeatUnitAmountType
             *     How the quantitative amount of Structural Repeat Units is captured (e.g. Exact, Numeric, Average)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder repeatUnitAmountType(CodeableConcept repeatUnitAmountType) {
                this.repeatUnitAmountType = repeatUnitAmountType;
                return this;
            }

            /**
             * An SRU - Structural Repeat Unit.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param repeatUnit
             *     An SRU - Structural Repeat Unit
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder repeatUnit(RepeatUnit... repeatUnit) {
                for (RepeatUnit value : repeatUnit) {
                    this.repeatUnit.add(value);
                }
                return this;
            }

            /**
             * An SRU - Structural Repeat Unit.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param repeatUnit
             *     An SRU - Structural Repeat Unit
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder repeatUnit(Collection<RepeatUnit> repeatUnit) {
                this.repeatUnit = new ArrayList<>(repeatUnit);
                return this;
            }

            /**
             * Build the {@link Repeat}
             * 
             * @return
             *     An immutable object of type {@link Repeat}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Repeat per the base specification
             */
            @Override
            public Repeat build() {
                Repeat repeat = new Repeat(this);
                if (validating) {
                    validate(repeat);
                }
                return repeat;
            }

            protected void validate(Repeat repeat) {
                super.validate(repeat);
                ValidationSupport.checkList(repeat.repeatUnit, "repeatUnit", RepeatUnit.class);
                ValidationSupport.requireValueOrChildren(repeat);
            }

            protected Builder from(Repeat repeat) {
                super.from(repeat);
                averageMolecularFormula = repeat.averageMolecularFormula;
                repeatUnitAmountType = repeat.repeatUnitAmountType;
                repeatUnit.addAll(repeat.repeatUnit);
                return this;
            }
        }

        /**
         * An SRU - Structural Repeat Unit.
         */
        public static class RepeatUnit extends BackboneElement {
            @Summary
            private final String unit;
            @Summary
            private final CodeableConcept orientation;
            @Summary
            private final Integer amount;
            @Summary
            private final List<DegreeOfPolymerisation> degreeOfPolymerisation;
            @Summary
            private final List<StructuralRepresentation> structuralRepresentation;

            private RepeatUnit(Builder builder) {
                super(builder);
                unit = builder.unit;
                orientation = builder.orientation;
                amount = builder.amount;
                degreeOfPolymerisation = Collections.unmodifiableList(builder.degreeOfPolymerisation);
                structuralRepresentation = Collections.unmodifiableList(builder.structuralRepresentation);
            }

            /**
             * Structural repeat units are essential elements for defining polymers.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getUnit() {
                return unit;
            }

            /**
             * The orientation of the polymerisation, e.g. head-tail, head-head, random.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getOrientation() {
                return orientation;
            }

            /**
             * Number of repeats of this unit.
             * 
             * @return
             *     An immutable object of type {@link Integer} that may be null.
             */
            public Integer getAmount() {
                return amount;
            }

            /**
             * Applies to homopolymer and block co-polymers where the degree of polymerisation within a block can be described.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link DegreeOfPolymerisation} that may be empty.
             */
            public List<DegreeOfPolymerisation> getDegreeOfPolymerisation() {
                return degreeOfPolymerisation;
            }

            /**
             * A graphical structure for this SRU.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link StructuralRepresentation} that may be empty.
             */
            public List<StructuralRepresentation> getStructuralRepresentation() {
                return structuralRepresentation;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (unit != null) || 
                    (orientation != null) || 
                    (amount != null) || 
                    !degreeOfPolymerisation.isEmpty() || 
                    !structuralRepresentation.isEmpty();
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
                        accept(unit, "unit", visitor);
                        accept(orientation, "orientation", visitor);
                        accept(amount, "amount", visitor);
                        accept(degreeOfPolymerisation, "degreeOfPolymerisation", visitor, DegreeOfPolymerisation.class);
                        accept(structuralRepresentation, "structuralRepresentation", visitor, StructuralRepresentation.class);
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
                RepeatUnit other = (RepeatUnit) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(unit, other.unit) && 
                    Objects.equals(orientation, other.orientation) && 
                    Objects.equals(amount, other.amount) && 
                    Objects.equals(degreeOfPolymerisation, other.degreeOfPolymerisation) && 
                    Objects.equals(structuralRepresentation, other.structuralRepresentation);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        unit, 
                        orientation, 
                        amount, 
                        degreeOfPolymerisation, 
                        structuralRepresentation);
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
                private String unit;
                private CodeableConcept orientation;
                private Integer amount;
                private List<DegreeOfPolymerisation> degreeOfPolymerisation = new ArrayList<>();
                private List<StructuralRepresentation> structuralRepresentation = new ArrayList<>();

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
                 * Convenience method for setting {@code unit}.
                 * 
                 * @param unit
                 *     Structural repeat units are essential elements for defining polymers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #unit(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder unit(java.lang.String unit) {
                    this.unit = (unit == null) ? null : String.of(unit);
                    return this;
                }

                /**
                 * Structural repeat units are essential elements for defining polymers.
                 * 
                 * @param unit
                 *     Structural repeat units are essential elements for defining polymers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder unit(String unit) {
                    this.unit = unit;
                    return this;
                }

                /**
                 * The orientation of the polymerisation, e.g. head-tail, head-head, random.
                 * 
                 * @param orientation
                 *     The orientation of the polymerisation, e.g. head-tail, head-head, random
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder orientation(CodeableConcept orientation) {
                    this.orientation = orientation;
                    return this;
                }

                /**
                 * Convenience method for setting {@code amount}.
                 * 
                 * @param amount
                 *     Number of repeats of this unit
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #amount(org.linuxforhealth.fhir.model.type.Integer)
                 */
                public Builder amount(java.lang.Integer amount) {
                    this.amount = (amount == null) ? null : Integer.of(amount);
                    return this;
                }

                /**
                 * Number of repeats of this unit.
                 * 
                 * @param amount
                 *     Number of repeats of this unit
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder amount(Integer amount) {
                    this.amount = amount;
                    return this;
                }

                /**
                 * Applies to homopolymer and block co-polymers where the degree of polymerisation within a block can be described.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param degreeOfPolymerisation
                 *     Applies to homopolymer and block co-polymers where the degree of polymerisation within a block can be described
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder degreeOfPolymerisation(DegreeOfPolymerisation... degreeOfPolymerisation) {
                    for (DegreeOfPolymerisation value : degreeOfPolymerisation) {
                        this.degreeOfPolymerisation.add(value);
                    }
                    return this;
                }

                /**
                 * Applies to homopolymer and block co-polymers where the degree of polymerisation within a block can be described.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param degreeOfPolymerisation
                 *     Applies to homopolymer and block co-polymers where the degree of polymerisation within a block can be described
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder degreeOfPolymerisation(Collection<DegreeOfPolymerisation> degreeOfPolymerisation) {
                    this.degreeOfPolymerisation = new ArrayList<>(degreeOfPolymerisation);
                    return this;
                }

                /**
                 * A graphical structure for this SRU.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param structuralRepresentation
                 *     A graphical structure for this SRU
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder structuralRepresentation(StructuralRepresentation... structuralRepresentation) {
                    for (StructuralRepresentation value : structuralRepresentation) {
                        this.structuralRepresentation.add(value);
                    }
                    return this;
                }

                /**
                 * A graphical structure for this SRU.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param structuralRepresentation
                 *     A graphical structure for this SRU
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder structuralRepresentation(Collection<StructuralRepresentation> structuralRepresentation) {
                    this.structuralRepresentation = new ArrayList<>(structuralRepresentation);
                    return this;
                }

                /**
                 * Build the {@link RepeatUnit}
                 * 
                 * @return
                 *     An immutable object of type {@link RepeatUnit}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid RepeatUnit per the base specification
                 */
                @Override
                public RepeatUnit build() {
                    RepeatUnit repeatUnit = new RepeatUnit(this);
                    if (validating) {
                        validate(repeatUnit);
                    }
                    return repeatUnit;
                }

                protected void validate(RepeatUnit repeatUnit) {
                    super.validate(repeatUnit);
                    ValidationSupport.checkList(repeatUnit.degreeOfPolymerisation, "degreeOfPolymerisation", DegreeOfPolymerisation.class);
                    ValidationSupport.checkList(repeatUnit.structuralRepresentation, "structuralRepresentation", StructuralRepresentation.class);
                    ValidationSupport.requireValueOrChildren(repeatUnit);
                }

                protected Builder from(RepeatUnit repeatUnit) {
                    super.from(repeatUnit);
                    unit = repeatUnit.unit;
                    orientation = repeatUnit.orientation;
                    amount = repeatUnit.amount;
                    degreeOfPolymerisation.addAll(repeatUnit.degreeOfPolymerisation);
                    structuralRepresentation.addAll(repeatUnit.structuralRepresentation);
                    return this;
                }
            }

            /**
             * Applies to homopolymer and block co-polymers where the degree of polymerisation within a block can be described.
             */
            public static class DegreeOfPolymerisation extends BackboneElement {
                @Summary
                private final CodeableConcept type;
                @Summary
                private final Integer average;
                @Summary
                private final Integer low;
                @Summary
                private final Integer high;

                private DegreeOfPolymerisation(Builder builder) {
                    super(builder);
                    type = builder.type;
                    average = builder.average;
                    low = builder.low;
                    high = builder.high;
                }

                /**
                 * The type of the degree of polymerisation shall be described, e.g. SRU/Polymer Ratio.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getType() {
                    return type;
                }

                /**
                 * An average amount of polymerisation.
                 * 
                 * @return
                 *     An immutable object of type {@link Integer} that may be null.
                 */
                public Integer getAverage() {
                    return average;
                }

                /**
                 * A low expected limit of the amount.
                 * 
                 * @return
                 *     An immutable object of type {@link Integer} that may be null.
                 */
                public Integer getLow() {
                    return low;
                }

                /**
                 * A high expected limit of the amount.
                 * 
                 * @return
                 *     An immutable object of type {@link Integer} that may be null.
                 */
                public Integer getHigh() {
                    return high;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (type != null) || 
                        (average != null) || 
                        (low != null) || 
                        (high != null);
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
                            accept(average, "average", visitor);
                            accept(low, "low", visitor);
                            accept(high, "high", visitor);
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
                    DegreeOfPolymerisation other = (DegreeOfPolymerisation) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(type, other.type) && 
                        Objects.equals(average, other.average) && 
                        Objects.equals(low, other.low) && 
                        Objects.equals(high, other.high);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            type, 
                            average, 
                            low, 
                            high);
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
                    private Integer average;
                    private Integer low;
                    private Integer high;

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
                     * The type of the degree of polymerisation shall be described, e.g. SRU/Polymer Ratio.
                     * 
                     * @param type
                     *     The type of the degree of polymerisation shall be described, e.g. SRU/Polymer Ratio
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder type(CodeableConcept type) {
                        this.type = type;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code average}.
                     * 
                     * @param average
                     *     An average amount of polymerisation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #average(org.linuxforhealth.fhir.model.type.Integer)
                     */
                    public Builder average(java.lang.Integer average) {
                        this.average = (average == null) ? null : Integer.of(average);
                        return this;
                    }

                    /**
                     * An average amount of polymerisation.
                     * 
                     * @param average
                     *     An average amount of polymerisation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder average(Integer average) {
                        this.average = average;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code low}.
                     * 
                     * @param low
                     *     A low expected limit of the amount
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #low(org.linuxforhealth.fhir.model.type.Integer)
                     */
                    public Builder low(java.lang.Integer low) {
                        this.low = (low == null) ? null : Integer.of(low);
                        return this;
                    }

                    /**
                     * A low expected limit of the amount.
                     * 
                     * @param low
                     *     A low expected limit of the amount
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder low(Integer low) {
                        this.low = low;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code high}.
                     * 
                     * @param high
                     *     A high expected limit of the amount
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #high(org.linuxforhealth.fhir.model.type.Integer)
                     */
                    public Builder high(java.lang.Integer high) {
                        this.high = (high == null) ? null : Integer.of(high);
                        return this;
                    }

                    /**
                     * A high expected limit of the amount.
                     * 
                     * @param high
                     *     A high expected limit of the amount
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder high(Integer high) {
                        this.high = high;
                        return this;
                    }

                    /**
                     * Build the {@link DegreeOfPolymerisation}
                     * 
                     * @return
                     *     An immutable object of type {@link DegreeOfPolymerisation}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid DegreeOfPolymerisation per the base specification
                     */
                    @Override
                    public DegreeOfPolymerisation build() {
                        DegreeOfPolymerisation degreeOfPolymerisation = new DegreeOfPolymerisation(this);
                        if (validating) {
                            validate(degreeOfPolymerisation);
                        }
                        return degreeOfPolymerisation;
                    }

                    protected void validate(DegreeOfPolymerisation degreeOfPolymerisation) {
                        super.validate(degreeOfPolymerisation);
                        ValidationSupport.requireValueOrChildren(degreeOfPolymerisation);
                    }

                    protected Builder from(DegreeOfPolymerisation degreeOfPolymerisation) {
                        super.from(degreeOfPolymerisation);
                        type = degreeOfPolymerisation.type;
                        average = degreeOfPolymerisation.average;
                        low = degreeOfPolymerisation.low;
                        high = degreeOfPolymerisation.high;
                        return this;
                    }
                }
            }

            /**
             * A graphical structure for this SRU.
             */
            public static class StructuralRepresentation extends BackboneElement {
                @Summary
                private final CodeableConcept type;
                @Summary
                private final String representation;
                @Summary
                private final CodeableConcept format;
                @Summary
                private final Attachment attachment;

                private StructuralRepresentation(Builder builder) {
                    super(builder);
                    type = builder.type;
                    representation = builder.representation;
                    format = builder.format;
                    attachment = builder.attachment;
                }

                /**
                 * The type of structure (e.g. Full, Partial, Representative).
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getType() {
                    return type;
                }

                /**
                 * The structural representation as text string in a standard format e.g. InChI, SMILES, MOLFILE, CDX, SDF, PDB, mmCIF.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getRepresentation() {
                    return representation;
                }

                /**
                 * The format of the representation e.g. InChI, SMILES, MOLFILE, CDX, SDF, PDB, mmCIF.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getFormat() {
                    return format;
                }

                /**
                 * An attached file with the structural representation.
                 * 
                 * @return
                 *     An immutable object of type {@link Attachment} that may be null.
                 */
                public Attachment getAttachment() {
                    return attachment;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (type != null) || 
                        (representation != null) || 
                        (format != null) || 
                        (attachment != null);
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
                            accept(representation, "representation", visitor);
                            accept(format, "format", visitor);
                            accept(attachment, "attachment", visitor);
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
                    StructuralRepresentation other = (StructuralRepresentation) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(type, other.type) && 
                        Objects.equals(representation, other.representation) && 
                        Objects.equals(format, other.format) && 
                        Objects.equals(attachment, other.attachment);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            type, 
                            representation, 
                            format, 
                            attachment);
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
                    private String representation;
                    private CodeableConcept format;
                    private Attachment attachment;

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
                     * The type of structure (e.g. Full, Partial, Representative).
                     * 
                     * @param type
                     *     The type of structure (e.g. Full, Partial, Representative)
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder type(CodeableConcept type) {
                        this.type = type;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code representation}.
                     * 
                     * @param representation
                     *     The structural representation as text string in a standard format e.g. InChI, SMILES, MOLFILE, CDX, SDF, PDB, mmCIF
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #representation(org.linuxforhealth.fhir.model.type.String)
                     */
                    public Builder representation(java.lang.String representation) {
                        this.representation = (representation == null) ? null : String.of(representation);
                        return this;
                    }

                    /**
                     * The structural representation as text string in a standard format e.g. InChI, SMILES, MOLFILE, CDX, SDF, PDB, mmCIF.
                     * 
                     * @param representation
                     *     The structural representation as text string in a standard format e.g. InChI, SMILES, MOLFILE, CDX, SDF, PDB, mmCIF
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder representation(String representation) {
                        this.representation = representation;
                        return this;
                    }

                    /**
                     * The format of the representation e.g. InChI, SMILES, MOLFILE, CDX, SDF, PDB, mmCIF.
                     * 
                     * @param format
                     *     The format of the representation e.g. InChI, SMILES, MOLFILE, CDX, SDF, PDB, mmCIF
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder format(CodeableConcept format) {
                        this.format = format;
                        return this;
                    }

                    /**
                     * An attached file with the structural representation.
                     * 
                     * @param attachment
                     *     An attached file with the structural representation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder attachment(Attachment attachment) {
                        this.attachment = attachment;
                        return this;
                    }

                    /**
                     * Build the {@link StructuralRepresentation}
                     * 
                     * @return
                     *     An immutable object of type {@link StructuralRepresentation}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid StructuralRepresentation per the base specification
                     */
                    @Override
                    public StructuralRepresentation build() {
                        StructuralRepresentation structuralRepresentation = new StructuralRepresentation(this);
                        if (validating) {
                            validate(structuralRepresentation);
                        }
                        return structuralRepresentation;
                    }

                    protected void validate(StructuralRepresentation structuralRepresentation) {
                        super.validate(structuralRepresentation);
                        ValidationSupport.requireValueOrChildren(structuralRepresentation);
                    }

                    protected Builder from(StructuralRepresentation structuralRepresentation) {
                        super.from(structuralRepresentation);
                        type = structuralRepresentation.type;
                        representation = structuralRepresentation.representation;
                        format = structuralRepresentation.format;
                        attachment = structuralRepresentation.attachment;
                        return this;
                    }
                }
            }
        }
    }
}
