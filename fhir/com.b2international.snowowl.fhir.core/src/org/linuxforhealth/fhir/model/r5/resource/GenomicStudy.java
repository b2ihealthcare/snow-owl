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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.GenomicStudyStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A GenomicStudy is a set of analyses performed to analyze and generate genomic data.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "genomicStudy-0",
    level = "Warning",
    location = "analysis.genomeBuild",
    description = "SHALL, if possible, contain a code from value set http://loinc.org/vs/LL1040-6",
    expression = "$this.memberOf('http://loinc.org/vs/LL1040-6', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/GenomicStudy",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class GenomicStudy extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "GenomicStudyStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the GenomicStudy.",
        valueSet = "http://hl7.org/fhir/ValueSet/genomicstudy-status|5.0.0"
    )
    @Required
    private final GenomicStudyStatus status;
    @Summary
    @Binding(
        bindingName = "GenomicStudyType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The type relevant to GenomicStudy.",
        valueSet = "http://hl7.org/fhir/ValueSet/genomicstudy-type"
    )
    private final List<CodeableConcept> type;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Substance", "BiologicallyDerivedProduct", "NutritionProduct" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    private final DateTime startDate;
    @ReferenceTarget({ "ServiceRequest", "Task" })
    private final List<Reference> basedOn;
    @ReferenceTarget({ "Practitioner", "PractitionerRole" })
    private final Reference referrer;
    @ReferenceTarget({ "Practitioner", "PractitionerRole" })
    private final List<Reference> interpreter;
    private final List<CodeableReference> reason;
    private final Canonical instantiatesCanonical;
    private final Uri instantiatesUri;
    private final List<Annotation> note;
    private final Markdown description;
    private final List<Analysis> analysis;

    private GenomicStudy(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        type = Collections.unmodifiableList(builder.type);
        subject = builder.subject;
        encounter = builder.encounter;
        startDate = builder.startDate;
        basedOn = Collections.unmodifiableList(builder.basedOn);
        referrer = builder.referrer;
        interpreter = Collections.unmodifiableList(builder.interpreter);
        reason = Collections.unmodifiableList(builder.reason);
        instantiatesCanonical = builder.instantiatesCanonical;
        instantiatesUri = builder.instantiatesUri;
        note = Collections.unmodifiableList(builder.note);
        description = builder.description;
        analysis = Collections.unmodifiableList(builder.analysis);
    }

    /**
     * Identifiers for this genomic study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The status of the genomic study.
     * 
     * @return
     *     An immutable object of type {@link GenomicStudyStatus} that is non-null.
     */
    public GenomicStudyStatus getStatus() {
        return status;
    }

    /**
     * The type of the study, e.g., Familial variant segregation, Functional variation detection, or Gene expression 
     * profiling.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * The primary subject of the genomic study.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The healthcare event with which this genomics study is associated.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * When the genomic study was started.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getStartDate() {
        return startDate;
    }

    /**
     * Event resources that the genomic study is based on.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * Healthcare professional who requested or referred the genomic study.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getReferrer() {
        return referrer;
    }

    /**
     * Healthcare professionals who interpreted the genomic study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getInterpreter() {
        return interpreter;
    }

    /**
     * Why the genomic study was performed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * The defined protocol that describes the study.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * The URL pointing to an externally maintained protocol that describes the study.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * Comments related to the genomic study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Description of the genomic study.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * The details about a specific analysis that was performed in this GenomicStudy.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Analysis} that may be empty.
     */
    public List<Analysis> getAnalysis() {
        return analysis;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !type.isEmpty() || 
            (subject != null) || 
            (encounter != null) || 
            (startDate != null) || 
            !basedOn.isEmpty() || 
            (referrer != null) || 
            !interpreter.isEmpty() || 
            !reason.isEmpty() || 
            (instantiatesCanonical != null) || 
            (instantiatesUri != null) || 
            !note.isEmpty() || 
            (description != null) || 
            !analysis.isEmpty();
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
                accept(type, "type", visitor, CodeableConcept.class);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(startDate, "startDate", visitor);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(referrer, "referrer", visitor);
                accept(interpreter, "interpreter", visitor, Reference.class);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(instantiatesCanonical, "instantiatesCanonical", visitor);
                accept(instantiatesUri, "instantiatesUri", visitor);
                accept(note, "note", visitor, Annotation.class);
                accept(description, "description", visitor);
                accept(analysis, "analysis", visitor, Analysis.class);
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
        GenomicStudy other = (GenomicStudy) obj;
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
            Objects.equals(type, other.type) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(startDate, other.startDate) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(referrer, other.referrer) && 
            Objects.equals(interpreter, other.interpreter) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(instantiatesCanonical, other.instantiatesCanonical) && 
            Objects.equals(instantiatesUri, other.instantiatesUri) && 
            Objects.equals(note, other.note) && 
            Objects.equals(description, other.description) && 
            Objects.equals(analysis, other.analysis);
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
                type, 
                subject, 
                encounter, 
                startDate, 
                basedOn, 
                referrer, 
                interpreter, 
                reason, 
                instantiatesCanonical, 
                instantiatesUri, 
                note, 
                description, 
                analysis);
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
        private GenomicStudyStatus status;
        private List<CodeableConcept> type = new ArrayList<>();
        private Reference subject;
        private Reference encounter;
        private DateTime startDate;
        private List<Reference> basedOn = new ArrayList<>();
        private Reference referrer;
        private List<Reference> interpreter = new ArrayList<>();
        private List<CodeableReference> reason = new ArrayList<>();
        private Canonical instantiatesCanonical;
        private Uri instantiatesUri;
        private List<Annotation> note = new ArrayList<>();
        private Markdown description;
        private List<Analysis> analysis = new ArrayList<>();

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
         * Identifiers for this genomic study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers for this genomic study
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
         * Identifiers for this genomic study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers for this genomic study
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
         * The status of the genomic study.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     registered | available | cancelled | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(GenomicStudyStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The type of the study, e.g., Familial variant segregation, Functional variation detection, or Gene expression 
         * profiling.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     The type of the study (e.g., Familial variant segregation, Functional variation detection, or Gene expression 
         *     profiling)
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
         * The type of the study, e.g., Familial variant segregation, Functional variation detection, or Gene expression 
         * profiling.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     The type of the study (e.g., Familial variant segregation, Functional variation detection, or Gene expression 
         *     profiling)
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
         * The primary subject of the genomic study.
         * 
         * <p>This element is required.
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
         *     The primary subject of the genomic study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The healthcare event with which this genomics study is associated.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     The healthcare event with which this genomics study is associated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * When the genomic study was started.
         * 
         * @param startDate
         *     When the genomic study was started
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder startDate(DateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Event resources that the genomic study is based on.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Event resources that the genomic study is based on
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder basedOn(Reference... basedOn) {
            for (Reference value : basedOn) {
                this.basedOn.add(value);
            }
            return this;
        }

        /**
         * Event resources that the genomic study is based on.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Event resources that the genomic study is based on
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder basedOn(Collection<Reference> basedOn) {
            this.basedOn = new ArrayList<>(basedOn);
            return this;
        }

        /**
         * Healthcare professional who requested or referred the genomic study.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param referrer
         *     Healthcare professional who requested or referred the genomic study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder referrer(Reference referrer) {
            this.referrer = referrer;
            return this;
        }

        /**
         * Healthcare professionals who interpreted the genomic study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param interpreter
         *     Healthcare professionals who interpreted the genomic study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder interpreter(Reference... interpreter) {
            for (Reference value : interpreter) {
                this.interpreter.add(value);
            }
            return this;
        }

        /**
         * Healthcare professionals who interpreted the genomic study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param interpreter
         *     Healthcare professionals who interpreted the genomic study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder interpreter(Collection<Reference> interpreter) {
            this.interpreter = new ArrayList<>(interpreter);
            return this;
        }

        /**
         * Why the genomic study was performed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why the genomic study was performed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reason(CodeableReference... reason) {
            for (CodeableReference value : reason) {
                this.reason.add(value);
            }
            return this;
        }

        /**
         * Why the genomic study was performed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why the genomic study was performed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reason(Collection<CodeableReference> reason) {
            this.reason = new ArrayList<>(reason);
            return this;
        }

        /**
         * The defined protocol that describes the study.
         * 
         * @param instantiatesCanonical
         *     The defined protocol that describes the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesCanonical(Canonical instantiatesCanonical) {
            this.instantiatesCanonical = instantiatesCanonical;
            return this;
        }

        /**
         * The URL pointing to an externally maintained protocol that describes the study.
         * 
         * @param instantiatesUri
         *     The URL pointing to an externally maintained protocol that describes the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesUri(Uri instantiatesUri) {
            this.instantiatesUri = instantiatesUri;
            return this;
        }

        /**
         * Comments related to the genomic study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments related to the genomic study
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
         * Comments related to the genomic study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments related to the genomic study
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
         * Description of the genomic study.
         * 
         * @param description
         *     Description of the genomic study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * The details about a specific analysis that was performed in this GenomicStudy.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param analysis
         *     Genomic Analysis Event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder analysis(Analysis... analysis) {
            for (Analysis value : analysis) {
                this.analysis.add(value);
            }
            return this;
        }

        /**
         * The details about a specific analysis that was performed in this GenomicStudy.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param analysis
         *     Genomic Analysis Event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder analysis(Collection<Analysis> analysis) {
            this.analysis = new ArrayList<>(analysis);
            return this;
        }

        /**
         * Build the {@link GenomicStudy}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link GenomicStudy}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid GenomicStudy per the base specification
         */
        @Override
        public GenomicStudy build() {
            GenomicStudy genomicStudy = new GenomicStudy(this);
            if (validating) {
                validate(genomicStudy);
            }
            return genomicStudy;
        }

        protected void validate(GenomicStudy genomicStudy) {
            super.validate(genomicStudy);
            ValidationSupport.checkList(genomicStudy.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(genomicStudy.status, "status");
            ValidationSupport.checkList(genomicStudy.type, "type", CodeableConcept.class);
            ValidationSupport.requireNonNull(genomicStudy.subject, "subject");
            ValidationSupport.checkList(genomicStudy.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(genomicStudy.interpreter, "interpreter", Reference.class);
            ValidationSupport.checkList(genomicStudy.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(genomicStudy.note, "note", Annotation.class);
            ValidationSupport.checkList(genomicStudy.analysis, "analysis", Analysis.class);
            ValidationSupport.checkReferenceType(genomicStudy.subject, "subject", "Patient", "Group", "Substance", "BiologicallyDerivedProduct", "NutritionProduct");
            ValidationSupport.checkReferenceType(genomicStudy.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(genomicStudy.basedOn, "basedOn", "ServiceRequest", "Task");
            ValidationSupport.checkReferenceType(genomicStudy.referrer, "referrer", "Practitioner", "PractitionerRole");
            ValidationSupport.checkReferenceType(genomicStudy.interpreter, "interpreter", "Practitioner", "PractitionerRole");
        }

        protected Builder from(GenomicStudy genomicStudy) {
            super.from(genomicStudy);
            identifier.addAll(genomicStudy.identifier);
            status = genomicStudy.status;
            type.addAll(genomicStudy.type);
            subject = genomicStudy.subject;
            encounter = genomicStudy.encounter;
            startDate = genomicStudy.startDate;
            basedOn.addAll(genomicStudy.basedOn);
            referrer = genomicStudy.referrer;
            interpreter.addAll(genomicStudy.interpreter);
            reason.addAll(genomicStudy.reason);
            instantiatesCanonical = genomicStudy.instantiatesCanonical;
            instantiatesUri = genomicStudy.instantiatesUri;
            note.addAll(genomicStudy.note);
            description = genomicStudy.description;
            analysis.addAll(genomicStudy.analysis);
            return this;
        }
    }

    /**
     * The details about a specific analysis that was performed in this GenomicStudy.
     */
    public static class Analysis extends BackboneElement {
        @Summary
        private final List<Identifier> identifier;
        @Summary
        @Binding(
            bindingName = "GenomicStudyMethodType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The method type of the GenomicStudy analysis.",
            valueSet = "http://hl7.org/fhir/ValueSet/genomicstudy-methodtype"
        )
        private final List<CodeableConcept> methodType;
        @Binding(
            bindingName = "GenomicStudyChangeType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The change type relevant to GenomicStudy analysis.",
            valueSet = "http://hl7.org/fhir/ValueSet/genomicstudy-changetype"
        )
        private final List<CodeableConcept> changeType;
        @Binding(
            bindingName = "HumanRefSeqNCBIBuildId",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Human reference sequence NCBI build ID",
            valueSet = "http://loinc.org/vs/LL1040-6"
        )
        private final CodeableConcept genomeBuild;
        private final Canonical instantiatesCanonical;
        private final Uri instantiatesUri;
        @Summary
        private final String title;
        @Summary
        private final List<Reference> focus;
        @Summary
        @ReferenceTarget({ "Specimen" })
        private final List<Reference> specimen;
        private final DateTime date;
        private final List<Annotation> note;
        @ReferenceTarget({ "Procedure", "Task" })
        private final Reference protocolPerformed;
        @ReferenceTarget({ "DocumentReference", "Observation" })
        private final List<Reference> regionsStudied;
        @ReferenceTarget({ "DocumentReference", "Observation" })
        private final List<Reference> regionsCalled;
        private final List<Input> input;
        private final List<Output> output;
        private final List<Performer> performer;
        private final List<Device> device;

        private Analysis(Builder builder) {
            super(builder);
            identifier = Collections.unmodifiableList(builder.identifier);
            methodType = Collections.unmodifiableList(builder.methodType);
            changeType = Collections.unmodifiableList(builder.changeType);
            genomeBuild = builder.genomeBuild;
            instantiatesCanonical = builder.instantiatesCanonical;
            instantiatesUri = builder.instantiatesUri;
            title = builder.title;
            focus = Collections.unmodifiableList(builder.focus);
            specimen = Collections.unmodifiableList(builder.specimen);
            date = builder.date;
            note = Collections.unmodifiableList(builder.note);
            protocolPerformed = builder.protocolPerformed;
            regionsStudied = Collections.unmodifiableList(builder.regionsStudied);
            regionsCalled = Collections.unmodifiableList(builder.regionsCalled);
            input = Collections.unmodifiableList(builder.input);
            output = Collections.unmodifiableList(builder.output);
            performer = Collections.unmodifiableList(builder.performer);
            device = Collections.unmodifiableList(builder.device);
        }

        /**
         * Identifiers for the analysis event.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
         */
        public List<Identifier> getIdentifier() {
            return identifier;
        }

        /**
         * Type of the methods used in the analysis, e.g., Fluorescence in situ hybridization (FISH), Karyotyping, or 
         * Microsatellite instability testing (MSI).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getMethodType() {
            return methodType;
        }

        /**
         * Type of the genomic changes studied in the analysis, e.g., DNA, RNA, or amino acid change.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getChangeType() {
            return changeType;
        }

        /**
         * The reference genome build that is used in this analysis.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getGenomeBuild() {
            return genomeBuild;
        }

        /**
         * The defined protocol that describes the analysis.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getInstantiatesCanonical() {
            return instantiatesCanonical;
        }

        /**
         * The URL pointing to an externally maintained protocol that describes the analysis.
         * 
         * @return
         *     An immutable object of type {@link Uri} that may be null.
         */
        public Uri getInstantiatesUri() {
            return instantiatesUri;
        }

        /**
         * Name of the analysis event (human friendly).
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getTitle() {
            return title;
        }

        /**
         * The focus of a genomic analysis when it is not the patient of record representing something or someone associated with 
         * the patient such as a spouse, parent, child, or sibling. For example, in trio testing, the GenomicStudy.subject would 
         * be the child (proband) and the GenomicStudy.analysis.focus of a specific analysis would be the parent.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getFocus() {
            return focus;
        }

        /**
         * The specimen used in the analysis event.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getSpecimen() {
            return specimen;
        }

        /**
         * The date of the analysis event.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getDate() {
            return date;
        }

        /**
         * Any notes capture with the analysis event.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
         */
        public List<Annotation> getNote() {
            return note;
        }

        /**
         * The protocol that was performed for the analysis event.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getProtocolPerformed() {
            return protocolPerformed;
        }

        /**
         * The genomic regions to be studied in the analysis (BED file).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getRegionsStudied() {
            return regionsStudied;
        }

        /**
         * Genomic regions actually called in the analysis event (BED file).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getRegionsCalled() {
            return regionsCalled;
        }

        /**
         * Inputs for the analysis event.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Input} that may be empty.
         */
        public List<Input> getInput() {
            return input;
        }

        /**
         * Outputs for the analysis event.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Output} that may be empty.
         */
        public List<Output> getOutput() {
            return output;
        }

        /**
         * Performer for the analysis event.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
         */
        public List<Performer> getPerformer() {
            return performer;
        }

        /**
         * Devices used for the analysis (e.g., instruments, software), with settings and parameters.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Device} that may be empty.
         */
        public List<Device> getDevice() {
            return device;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !identifier.isEmpty() || 
                !methodType.isEmpty() || 
                !changeType.isEmpty() || 
                (genomeBuild != null) || 
                (instantiatesCanonical != null) || 
                (instantiatesUri != null) || 
                (title != null) || 
                !focus.isEmpty() || 
                !specimen.isEmpty() || 
                (date != null) || 
                !note.isEmpty() || 
                (protocolPerformed != null) || 
                !regionsStudied.isEmpty() || 
                !regionsCalled.isEmpty() || 
                !input.isEmpty() || 
                !output.isEmpty() || 
                !performer.isEmpty() || 
                !device.isEmpty();
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
                    accept(identifier, "identifier", visitor, Identifier.class);
                    accept(methodType, "methodType", visitor, CodeableConcept.class);
                    accept(changeType, "changeType", visitor, CodeableConcept.class);
                    accept(genomeBuild, "genomeBuild", visitor);
                    accept(instantiatesCanonical, "instantiatesCanonical", visitor);
                    accept(instantiatesUri, "instantiatesUri", visitor);
                    accept(title, "title", visitor);
                    accept(focus, "focus", visitor, Reference.class);
                    accept(specimen, "specimen", visitor, Reference.class);
                    accept(date, "date", visitor);
                    accept(note, "note", visitor, Annotation.class);
                    accept(protocolPerformed, "protocolPerformed", visitor);
                    accept(regionsStudied, "regionsStudied", visitor, Reference.class);
                    accept(regionsCalled, "regionsCalled", visitor, Reference.class);
                    accept(input, "input", visitor, Input.class);
                    accept(output, "output", visitor, Output.class);
                    accept(performer, "performer", visitor, Performer.class);
                    accept(device, "device", visitor, Device.class);
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
            Analysis other = (Analysis) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(identifier, other.identifier) && 
                Objects.equals(methodType, other.methodType) && 
                Objects.equals(changeType, other.changeType) && 
                Objects.equals(genomeBuild, other.genomeBuild) && 
                Objects.equals(instantiatesCanonical, other.instantiatesCanonical) && 
                Objects.equals(instantiatesUri, other.instantiatesUri) && 
                Objects.equals(title, other.title) && 
                Objects.equals(focus, other.focus) && 
                Objects.equals(specimen, other.specimen) && 
                Objects.equals(date, other.date) && 
                Objects.equals(note, other.note) && 
                Objects.equals(protocolPerformed, other.protocolPerformed) && 
                Objects.equals(regionsStudied, other.regionsStudied) && 
                Objects.equals(regionsCalled, other.regionsCalled) && 
                Objects.equals(input, other.input) && 
                Objects.equals(output, other.output) && 
                Objects.equals(performer, other.performer) && 
                Objects.equals(device, other.device);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    identifier, 
                    methodType, 
                    changeType, 
                    genomeBuild, 
                    instantiatesCanonical, 
                    instantiatesUri, 
                    title, 
                    focus, 
                    specimen, 
                    date, 
                    note, 
                    protocolPerformed, 
                    regionsStudied, 
                    regionsCalled, 
                    input, 
                    output, 
                    performer, 
                    device);
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
            private List<Identifier> identifier = new ArrayList<>();
            private List<CodeableConcept> methodType = new ArrayList<>();
            private List<CodeableConcept> changeType = new ArrayList<>();
            private CodeableConcept genomeBuild;
            private Canonical instantiatesCanonical;
            private Uri instantiatesUri;
            private String title;
            private List<Reference> focus = new ArrayList<>();
            private List<Reference> specimen = new ArrayList<>();
            private DateTime date;
            private List<Annotation> note = new ArrayList<>();
            private Reference protocolPerformed;
            private List<Reference> regionsStudied = new ArrayList<>();
            private List<Reference> regionsCalled = new ArrayList<>();
            private List<Input> input = new ArrayList<>();
            private List<Output> output = new ArrayList<>();
            private List<Performer> performer = new ArrayList<>();
            private List<Device> device = new ArrayList<>();

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
             * Identifiers for the analysis event.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param identifier
             *     Identifiers for the analysis event
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
             * Identifiers for the analysis event.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param identifier
             *     Identifiers for the analysis event
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
             * Type of the methods used in the analysis, e.g., Fluorescence in situ hybridization (FISH), Karyotyping, or 
             * Microsatellite instability testing (MSI).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param methodType
             *     Type of the methods used in the analysis (e.g., FISH, Karyotyping, MSI)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder methodType(CodeableConcept... methodType) {
                for (CodeableConcept value : methodType) {
                    this.methodType.add(value);
                }
                return this;
            }

            /**
             * Type of the methods used in the analysis, e.g., Fluorescence in situ hybridization (FISH), Karyotyping, or 
             * Microsatellite instability testing (MSI).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param methodType
             *     Type of the methods used in the analysis (e.g., FISH, Karyotyping, MSI)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder methodType(Collection<CodeableConcept> methodType) {
                this.methodType = new ArrayList<>(methodType);
                return this;
            }

            /**
             * Type of the genomic changes studied in the analysis, e.g., DNA, RNA, or amino acid change.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param changeType
             *     Type of the genomic changes studied in the analysis (e.g., DNA, RNA, or AA change)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder changeType(CodeableConcept... changeType) {
                for (CodeableConcept value : changeType) {
                    this.changeType.add(value);
                }
                return this;
            }

            /**
             * Type of the genomic changes studied in the analysis, e.g., DNA, RNA, or amino acid change.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param changeType
             *     Type of the genomic changes studied in the analysis (e.g., DNA, RNA, or AA change)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder changeType(Collection<CodeableConcept> changeType) {
                this.changeType = new ArrayList<>(changeType);
                return this;
            }

            /**
             * The reference genome build that is used in this analysis.
             * 
             * @param genomeBuild
             *     Genome build that is used in this analysis
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder genomeBuild(CodeableConcept genomeBuild) {
                this.genomeBuild = genomeBuild;
                return this;
            }

            /**
             * The defined protocol that describes the analysis.
             * 
             * @param instantiatesCanonical
             *     The defined protocol that describes the analysis
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instantiatesCanonical(Canonical instantiatesCanonical) {
                this.instantiatesCanonical = instantiatesCanonical;
                return this;
            }

            /**
             * The URL pointing to an externally maintained protocol that describes the analysis.
             * 
             * @param instantiatesUri
             *     The URL pointing to an externally maintained protocol that describes the analysis
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instantiatesUri(Uri instantiatesUri) {
                this.instantiatesUri = instantiatesUri;
                return this;
            }

            /**
             * Convenience method for setting {@code title}.
             * 
             * @param title
             *     Name of the analysis event (human friendly)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #title(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder title(java.lang.String title) {
                this.title = (title == null) ? null : String.of(title);
                return this;
            }

            /**
             * Name of the analysis event (human friendly).
             * 
             * @param title
             *     Name of the analysis event (human friendly)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder title(String title) {
                this.title = title;
                return this;
            }

            /**
             * The focus of a genomic analysis when it is not the patient of record representing something or someone associated with 
             * the patient such as a spouse, parent, child, or sibling. For example, in trio testing, the GenomicStudy.subject would 
             * be the child (proband) and the GenomicStudy.analysis.focus of a specific analysis would be the parent.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param focus
             *     What the genomic analysis is about, when it is not about the subject of record
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
             * The focus of a genomic analysis when it is not the patient of record representing something or someone associated with 
             * the patient such as a spouse, parent, child, or sibling. For example, in trio testing, the GenomicStudy.subject would 
             * be the child (proband) and the GenomicStudy.analysis.focus of a specific analysis would be the parent.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param focus
             *     What the genomic analysis is about, when it is not about the subject of record
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
             * The specimen used in the analysis event.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Specimen}</li>
             * </ul>
             * 
             * @param specimen
             *     The specimen used in the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder specimen(Reference... specimen) {
                for (Reference value : specimen) {
                    this.specimen.add(value);
                }
                return this;
            }

            /**
             * The specimen used in the analysis event.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Specimen}</li>
             * </ul>
             * 
             * @param specimen
             *     The specimen used in the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder specimen(Collection<Reference> specimen) {
                this.specimen = new ArrayList<>(specimen);
                return this;
            }

            /**
             * The date of the analysis event.
             * 
             * @param date
             *     The date of the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder date(DateTime date) {
                this.date = date;
                return this;
            }

            /**
             * Any notes capture with the analysis event.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Any notes capture with the analysis event
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
             * Any notes capture with the analysis event.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Any notes capture with the analysis event
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
             * The protocol that was performed for the analysis event.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Procedure}</li>
             * <li>{@link Task}</li>
             * </ul>
             * 
             * @param protocolPerformed
             *     The protocol that was performed for the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder protocolPerformed(Reference protocolPerformed) {
                this.protocolPerformed = protocolPerformed;
                return this;
            }

            /**
             * The genomic regions to be studied in the analysis (BED file).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link DocumentReference}</li>
             * <li>{@link Observation}</li>
             * </ul>
             * 
             * @param regionsStudied
             *     The genomic regions to be studied in the analysis (BED file)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder regionsStudied(Reference... regionsStudied) {
                for (Reference value : regionsStudied) {
                    this.regionsStudied.add(value);
                }
                return this;
            }

            /**
             * The genomic regions to be studied in the analysis (BED file).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link DocumentReference}</li>
             * <li>{@link Observation}</li>
             * </ul>
             * 
             * @param regionsStudied
             *     The genomic regions to be studied in the analysis (BED file)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder regionsStudied(Collection<Reference> regionsStudied) {
                this.regionsStudied = new ArrayList<>(regionsStudied);
                return this;
            }

            /**
             * Genomic regions actually called in the analysis event (BED file).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link DocumentReference}</li>
             * <li>{@link Observation}</li>
             * </ul>
             * 
             * @param regionsCalled
             *     Genomic regions actually called in the analysis event (BED file)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder regionsCalled(Reference... regionsCalled) {
                for (Reference value : regionsCalled) {
                    this.regionsCalled.add(value);
                }
                return this;
            }

            /**
             * Genomic regions actually called in the analysis event (BED file).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link DocumentReference}</li>
             * <li>{@link Observation}</li>
             * </ul>
             * 
             * @param regionsCalled
             *     Genomic regions actually called in the analysis event (BED file)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder regionsCalled(Collection<Reference> regionsCalled) {
                this.regionsCalled = new ArrayList<>(regionsCalled);
                return this;
            }

            /**
             * Inputs for the analysis event.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param input
             *     Inputs for the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder input(Input... input) {
                for (Input value : input) {
                    this.input.add(value);
                }
                return this;
            }

            /**
             * Inputs for the analysis event.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param input
             *     Inputs for the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder input(Collection<Input> input) {
                this.input = new ArrayList<>(input);
                return this;
            }

            /**
             * Outputs for the analysis event.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param output
             *     Outputs for the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder output(Output... output) {
                for (Output value : output) {
                    this.output.add(value);
                }
                return this;
            }

            /**
             * Outputs for the analysis event.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param output
             *     Outputs for the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder output(Collection<Output> output) {
                this.output = new ArrayList<>(output);
                return this;
            }

            /**
             * Performer for the analysis event.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param performer
             *     Performer for the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder performer(Performer... performer) {
                for (Performer value : performer) {
                    this.performer.add(value);
                }
                return this;
            }

            /**
             * Performer for the analysis event.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param performer
             *     Performer for the analysis event
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder performer(Collection<Performer> performer) {
                this.performer = new ArrayList<>(performer);
                return this;
            }

            /**
             * Devices used for the analysis (e.g., instruments, software), with settings and parameters.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param device
             *     Devices used for the analysis (e.g., instruments, software), with settings and parameters
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder device(Device... device) {
                for (Device value : device) {
                    this.device.add(value);
                }
                return this;
            }

            /**
             * Devices used for the analysis (e.g., instruments, software), with settings and parameters.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param device
             *     Devices used for the analysis (e.g., instruments, software), with settings and parameters
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder device(Collection<Device> device) {
                this.device = new ArrayList<>(device);
                return this;
            }

            /**
             * Build the {@link Analysis}
             * 
             * @return
             *     An immutable object of type {@link Analysis}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Analysis per the base specification
             */
            @Override
            public Analysis build() {
                Analysis analysis = new Analysis(this);
                if (validating) {
                    validate(analysis);
                }
                return analysis;
            }

            protected void validate(Analysis analysis) {
                super.validate(analysis);
                ValidationSupport.checkList(analysis.identifier, "identifier", Identifier.class);
                ValidationSupport.checkList(analysis.methodType, "methodType", CodeableConcept.class);
                ValidationSupport.checkList(analysis.changeType, "changeType", CodeableConcept.class);
                ValidationSupport.checkList(analysis.focus, "focus", Reference.class);
                ValidationSupport.checkList(analysis.specimen, "specimen", Reference.class);
                ValidationSupport.checkList(analysis.note, "note", Annotation.class);
                ValidationSupport.checkList(analysis.regionsStudied, "regionsStudied", Reference.class);
                ValidationSupport.checkList(analysis.regionsCalled, "regionsCalled", Reference.class);
                ValidationSupport.checkList(analysis.input, "input", Input.class);
                ValidationSupport.checkList(analysis.output, "output", Output.class);
                ValidationSupport.checkList(analysis.performer, "performer", Performer.class);
                ValidationSupport.checkList(analysis.device, "device", Device.class);
                ValidationSupport.checkReferenceType(analysis.specimen, "specimen", "Specimen");
                ValidationSupport.checkReferenceType(analysis.protocolPerformed, "protocolPerformed", "Procedure", "Task");
                ValidationSupport.checkReferenceType(analysis.regionsStudied, "regionsStudied", "DocumentReference", "Observation");
                ValidationSupport.checkReferenceType(analysis.regionsCalled, "regionsCalled", "DocumentReference", "Observation");
                ValidationSupport.requireValueOrChildren(analysis);
            }

            protected Builder from(Analysis analysis) {
                super.from(analysis);
                identifier.addAll(analysis.identifier);
                methodType.addAll(analysis.methodType);
                changeType.addAll(analysis.changeType);
                genomeBuild = analysis.genomeBuild;
                instantiatesCanonical = analysis.instantiatesCanonical;
                instantiatesUri = analysis.instantiatesUri;
                title = analysis.title;
                focus.addAll(analysis.focus);
                specimen.addAll(analysis.specimen);
                date = analysis.date;
                note.addAll(analysis.note);
                protocolPerformed = analysis.protocolPerformed;
                regionsStudied.addAll(analysis.regionsStudied);
                regionsCalled.addAll(analysis.regionsCalled);
                input.addAll(analysis.input);
                output.addAll(analysis.output);
                performer.addAll(analysis.performer);
                device.addAll(analysis.device);
                return this;
            }
        }

        /**
         * Inputs for the analysis event.
         */
        public static class Input extends BackboneElement {
            @Summary
            @ReferenceTarget({ "DocumentReference" })
            private final Reference file;
            @Binding(
                bindingName = "GenomicStudyDataFormat",
                strength = BindingStrength.Value.EXAMPLE,
                description = "The data format of the data file.",
                valueSet = "http://hl7.org/fhir/ValueSet/genomicstudy-dataformat"
            )
            private final CodeableConcept type;
            @ReferenceTarget({ "GenomicStudy" })
            @Choice({ Identifier.class, Reference.class })
            private final Element generatedBy;

            private Input(Builder builder) {
                super(builder);
                file = builder.file;
                type = builder.type;
                generatedBy = builder.generatedBy;
            }

            /**
             * File containing input data.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getFile() {
                return file;
            }

            /**
             * Type of input data, e.g., BAM, CRAM, or FASTA.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * The analysis event or other GenomicStudy that generated this input file.
             * 
             * @return
             *     An immutable object of type {@link Identifier} or {@link Reference} that may be null.
             */
            public Element getGeneratedBy() {
                return generatedBy;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (file != null) || 
                    (type != null) || 
                    (generatedBy != null);
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
                        accept(file, "file", visitor);
                        accept(type, "type", visitor);
                        accept(generatedBy, "generatedBy", visitor);
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
                Input other = (Input) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(file, other.file) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(generatedBy, other.generatedBy);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        file, 
                        type, 
                        generatedBy);
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
                private Reference file;
                private CodeableConcept type;
                private Element generatedBy;

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
                 * File containing input data.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link DocumentReference}</li>
                 * </ul>
                 * 
                 * @param file
                 *     File containing input data
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder file(Reference file) {
                    this.file = file;
                    return this;
                }

                /**
                 * Type of input data, e.g., BAM, CRAM, or FASTA.
                 * 
                 * @param type
                 *     Type of input data (e.g., BAM, CRAM, or FASTA)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * The analysis event or other GenomicStudy that generated this input file.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Identifier}</li>
                 * <li>{@link Reference}</li>
                 * </ul>
                 * 
                 * When of type {@link Reference}, the allowed resource types for this reference are:
                 * <ul>
                 * <li>{@link GenomicStudy}</li>
                 * </ul>
                 * 
                 * @param generatedBy
                 *     The analysis event or other GenomicStudy that generated this input file
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder generatedBy(Element generatedBy) {
                    this.generatedBy = generatedBy;
                    return this;
                }

                /**
                 * Build the {@link Input}
                 * 
                 * @return
                 *     An immutable object of type {@link Input}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Input per the base specification
                 */
                @Override
                public Input build() {
                    Input input = new Input(this);
                    if (validating) {
                        validate(input);
                    }
                    return input;
                }

                protected void validate(Input input) {
                    super.validate(input);
                    ValidationSupport.choiceElement(input.generatedBy, "generatedBy", Identifier.class, Reference.class);
                    ValidationSupport.checkReferenceType(input.file, "file", "DocumentReference");
                    ValidationSupport.checkReferenceType(input.generatedBy, "generatedBy", "GenomicStudy");
                    ValidationSupport.requireValueOrChildren(input);
                }

                protected Builder from(Input input) {
                    super.from(input);
                    file = input.file;
                    type = input.type;
                    generatedBy = input.generatedBy;
                    return this;
                }
            }
        }

        /**
         * Outputs for the analysis event.
         */
        public static class Output extends BackboneElement {
            @Summary
            @ReferenceTarget({ "DocumentReference" })
            private final Reference file;
            @Summary
            @Binding(
                bindingName = "GenomicStudyDataFormat",
                strength = BindingStrength.Value.EXAMPLE,
                description = "The data format of the data file.",
                valueSet = "http://hl7.org/fhir/ValueSet/genomicstudy-dataformat"
            )
            private final CodeableConcept type;

            private Output(Builder builder) {
                super(builder);
                file = builder.file;
                type = builder.type;
            }

            /**
             * File containing output data.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getFile() {
                return file;
            }

            /**
             * Type of output data, e.g., VCF, MAF, or BAM.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (file != null) || 
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
                        accept(file, "file", visitor);
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
                Output other = (Output) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(file, other.file) && 
                    Objects.equals(type, other.type);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        file, 
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
                private Reference file;
                private CodeableConcept type;

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
                 * File containing output data.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link DocumentReference}</li>
                 * </ul>
                 * 
                 * @param file
                 *     File containing output data
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder file(Reference file) {
                    this.file = file;
                    return this;
                }

                /**
                 * Type of output data, e.g., VCF, MAF, or BAM.
                 * 
                 * @param type
                 *     Type of output data (e.g., VCF, MAF, or BAM)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Build the {@link Output}
                 * 
                 * @return
                 *     An immutable object of type {@link Output}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Output per the base specification
                 */
                @Override
                public Output build() {
                    Output output = new Output(this);
                    if (validating) {
                        validate(output);
                    }
                    return output;
                }

                protected void validate(Output output) {
                    super.validate(output);
                    ValidationSupport.checkReferenceType(output.file, "file", "DocumentReference");
                    ValidationSupport.requireValueOrChildren(output);
                }

                protected Builder from(Output output) {
                    super.from(output);
                    file = output.file;
                    type = output.type;
                    return this;
                }
            }
        }

        /**
         * Performer for the analysis event.
         */
        public static class Performer extends BackboneElement {
            @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Device" })
            private final Reference actor;
            private final CodeableConcept role;

            private Performer(Builder builder) {
                super(builder);
                actor = builder.actor;
                role = builder.role;
            }

            /**
             * The organization, healthcare professional, or others who participated in performing this analysis.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getActor() {
                return actor;
            }

            /**
             * Role of the actor for this analysis.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getRole() {
                return role;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (actor != null) || 
                    (role != null);
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
                        accept(actor, "actor", visitor);
                        accept(role, "role", visitor);
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
                Performer other = (Performer) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(actor, other.actor) && 
                    Objects.equals(role, other.role);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        actor, 
                        role);
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
                private Reference actor;
                private CodeableConcept role;

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
                 * The organization, healthcare professional, or others who participated in performing this analysis.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Practitioner}</li>
                 * <li>{@link PractitionerRole}</li>
                 * <li>{@link Organization}</li>
                 * <li>{@link Device}</li>
                 * </ul>
                 * 
                 * @param actor
                 *     The organization, healthcare professional, or others who participated in performing this analysis
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder actor(Reference actor) {
                    this.actor = actor;
                    return this;
                }

                /**
                 * Role of the actor for this analysis.
                 * 
                 * @param role
                 *     Role of the actor for this analysis
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder role(CodeableConcept role) {
                    this.role = role;
                    return this;
                }

                /**
                 * Build the {@link Performer}
                 * 
                 * @return
                 *     An immutable object of type {@link Performer}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Performer per the base specification
                 */
                @Override
                public Performer build() {
                    Performer performer = new Performer(this);
                    if (validating) {
                        validate(performer);
                    }
                    return performer;
                }

                protected void validate(Performer performer) {
                    super.validate(performer);
                    ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner", "PractitionerRole", "Organization", "Device");
                    ValidationSupport.requireValueOrChildren(performer);
                }

                protected Builder from(Performer performer) {
                    super.from(performer);
                    actor = performer.actor;
                    role = performer.role;
                    return this;
                }
            }
        }

        /**
         * Devices used for the analysis (e.g., instruments, software), with settings and parameters.
         */
        public static class Device extends BackboneElement {
            @ReferenceTarget({ "Device" })
            private final Reference device;
            private final CodeableConcept function;

            private Device(Builder builder) {
                super(builder);
                device = builder.device;
                function = builder.function;
            }

            /**
             * Device used for the analysis.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getDevice() {
                return device;
            }

            /**
             * Specific function for the device used for the analysis.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getFunction() {
                return function;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (device != null) || 
                    (function != null);
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
                        accept(function, "function", visitor);
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
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(device, other.device) && 
                    Objects.equals(function, other.function);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        device, 
                        function);
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
                private CodeableConcept function;

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
                 * Device used for the analysis.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Device}</li>
                 * </ul>
                 * 
                 * @param device
                 *     Device used for the analysis
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder device(Reference device) {
                    this.device = device;
                    return this;
                }

                /**
                 * Specific function for the device used for the analysis.
                 * 
                 * @param function
                 *     Specific function for the device used for the analysis
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder function(CodeableConcept function) {
                    this.function = function;
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
                    ValidationSupport.checkReferenceType(device.device, "device", "Device");
                    ValidationSupport.requireValueOrChildren(device);
                }

                protected Builder from(Device device) {
                    super.from(device);
                    this.device = device.device;
                    function = device.function;
                    return this;
                }
            }
        }
    }
}
