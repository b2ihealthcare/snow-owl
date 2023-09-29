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
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.NutritionIntakeStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of food or fluid that is being consumed by a patient. A NutritionIntake may indicate that the patient may be 
 * consuming the food or fluid now or has consumed the food or fluid in the past. The source of this information can be 
 * the patient, significant other (such as a family member or spouse), or a clinician. A common scenario where this 
 * information is captured is during the history taking process during a patient visit or stay or through an app that 
 * tracks food or fluids consumed. The consumption information may come from sources such as the patient's memory, from a 
 * nutrition label, or from a clinician documenting observed intake.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class NutritionIntake extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    private final List<Canonical> instantiatesCanonical;
    private final List<Uri> instantiatesUri;
    @Summary
    @ReferenceTarget({ "NutritionOrder", "CarePlan", "ServiceRequest" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "NutritionIntake", "Procedure", "Observation" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "NutritionIntakeStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A coded concept indicating the current status of a NutritionIntake.",
        valueSet = "http://hl7.org/fhir/ValueSet/event-status|5.0.0"
    )
    @Required
    private final NutritionIntakeStatus status;
    @Binding(
        bindingName = "NutritionIntakeStatusReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept indicating the reason for the status of the statement.",
        valueSet = "http://hl7.org/fhir/ValueSet/clinicalimpression-status-reason"
    )
    private final List<CodeableConcept> statusReason;
    @Summary
    @Binding(
        bindingName = "NutritionIntakeCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying an overall type of diet or nutrition that is represented by this intake.  See consumedItem for more details.",
        valueSet = "http://hl7.org/fhir/ValueSet/diet-type"
    )
    private final CodeableConcept code;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Choice({ DateTime.class, Period.class })
    private final Element occurrence;
    @Summary
    private final DateTime recorded;
    @ReferenceTarget({ "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization" })
    @Choice({ Boolean.class, Reference.class })
    private final Element reported;
    @Required
    private final List<ConsumedItem> consumedItem;
    private final List<IngredientLabel> ingredientLabel;
    private final List<Performer> performer;
    @ReferenceTarget({ "Location" })
    private final Reference location;
    private final List<Reference> derivedFrom;
    @Binding(
        bindingName = "IntakeReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Reason for why something was ingested.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
    )
    private final List<CodeableReference> reason;
    private final List<Annotation> note;

    private NutritionIntake(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = Collections.unmodifiableList(builder.instantiatesCanonical);
        instantiatesUri = Collections.unmodifiableList(builder.instantiatesUri);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        statusReason = Collections.unmodifiableList(builder.statusReason);
        code = builder.code;
        subject = builder.subject;
        encounter = builder.encounter;
        occurrence = builder.occurrence;
        recorded = builder.recorded;
        reported = builder.reported;
        consumedItem = Collections.unmodifiableList(builder.consumedItem);
        ingredientLabel = Collections.unmodifiableList(builder.ingredientLabel);
        performer = Collections.unmodifiableList(builder.performer);
        location = builder.location;
        derivedFrom = Collections.unmodifiableList(builder.derivedFrom);
        reason = Collections.unmodifiableList(builder.reason);
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * Identifiers associated with this Nutrition Intake that are defined by business processes and/or used to refer to it 
     * when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to this 
     * resource by the performer or other systems and remain constant as the resource is updated and propagates from server 
     * to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Instantiates FHIR protocol or definition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * Instantiates external protocol or definition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * A plan, proposal or order that is fulfilled in whole or in part by this event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * A larger event of which this particular event is a component or step.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * A code representing the patient or other source's judgment about the state of the intake that this assertion is about. 
     * Generally, this will be active or completed.
     * 
     * @return
     *     An immutable object of type {@link NutritionIntakeStatus} that is non-null.
     */
    public NutritionIntakeStatus getStatus() {
        return status;
    }

    /**
     * Captures the reason for the current state of the NutritionIntake.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getStatusReason() {
        return statusReason;
    }

    /**
     * Overall type of nutrition intake.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * The person, animal or group who is/was consuming the food or fluid.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The encounter that establishes the context for this NutritionIntake.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * The interval of time during which it is being asserted that the patient is/was consuming the food or fluid.
     * 
     * @return
     *     An immutable object of type {@link DateTime} or {@link Period} that may be null.
     */
    public Element getOccurrence() {
        return occurrence;
    }

    /**
     * The date when the Nutrition Intake was asserted by the information source.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getRecorded() {
        return recorded;
    }

    /**
     * The person or organization that provided the information about the consumption of this food or fluid. Note: Use 
     * derivedFrom when a NutritionIntake is derived from other resources.
     * 
     * @return
     *     An immutable object of type {@link Boolean} or {@link Reference} that may be null.
     */
    public Element getReported() {
        return reported;
    }

    /**
     * What food or fluid product or item was consumed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ConsumedItem} that is non-empty.
     */
    public List<ConsumedItem> getConsumedItem() {
        return consumedItem;
    }

    /**
     * Total nutrient amounts for the whole meal, product, serving, etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link IngredientLabel} that may be empty.
     */
    public List<IngredientLabel> getIngredientLabel() {
        return ingredientLabel;
    }

    /**
     * Who performed the intake and how they were involved.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
     */
    public List<Performer> getPerformer() {
        return performer;
    }

    /**
     * Where the intake occurred.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * Allows linking the NutritionIntake to the underlying NutritionOrder, or to other information, such as 
     * AllergyIntolerance, that supports or is used to derive the NutritionIntake.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * A reason, Condition or observation for why the food or fluid is /was consumed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Provides extra information about the Nutrition Intake that is not conveyed by the other attributes.
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
            !instantiatesCanonical.isEmpty() || 
            !instantiatesUri.isEmpty() || 
            !basedOn.isEmpty() || 
            !partOf.isEmpty() || 
            (status != null) || 
            !statusReason.isEmpty() || 
            (code != null) || 
            (subject != null) || 
            (encounter != null) || 
            (occurrence != null) || 
            (recorded != null) || 
            (reported != null) || 
            !consumedItem.isEmpty() || 
            !ingredientLabel.isEmpty() || 
            !performer.isEmpty() || 
            (location != null) || 
            !derivedFrom.isEmpty() || 
            !reason.isEmpty() || 
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
                accept(instantiatesCanonical, "instantiatesCanonical", visitor, Canonical.class);
                accept(instantiatesUri, "instantiatesUri", visitor, Uri.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(partOf, "partOf", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(statusReason, "statusReason", visitor, CodeableConcept.class);
                accept(code, "code", visitor);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(occurrence, "occurrence", visitor);
                accept(recorded, "recorded", visitor);
                accept(reported, "reported", visitor);
                accept(consumedItem, "consumedItem", visitor, ConsumedItem.class);
                accept(ingredientLabel, "ingredientLabel", visitor, IngredientLabel.class);
                accept(performer, "performer", visitor, Performer.class);
                accept(location, "location", visitor);
                accept(derivedFrom, "derivedFrom", visitor, Reference.class);
                accept(reason, "reason", visitor, CodeableReference.class);
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
        NutritionIntake other = (NutritionIntake) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(instantiatesCanonical, other.instantiatesCanonical) && 
            Objects.equals(instantiatesUri, other.instantiatesUri) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusReason, other.statusReason) && 
            Objects.equals(code, other.code) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(occurrence, other.occurrence) && 
            Objects.equals(recorded, other.recorded) && 
            Objects.equals(reported, other.reported) && 
            Objects.equals(consumedItem, other.consumedItem) && 
            Objects.equals(ingredientLabel, other.ingredientLabel) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(location, other.location) && 
            Objects.equals(derivedFrom, other.derivedFrom) && 
            Objects.equals(reason, other.reason) && 
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
                instantiatesCanonical, 
                instantiatesUri, 
                basedOn, 
                partOf, 
                status, 
                statusReason, 
                code, 
                subject, 
                encounter, 
                occurrence, 
                recorded, 
                reported, 
                consumedItem, 
                ingredientLabel, 
                performer, 
                location, 
                derivedFrom, 
                reason, 
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
        private List<Canonical> instantiatesCanonical = new ArrayList<>();
        private List<Uri> instantiatesUri = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private List<Reference> partOf = new ArrayList<>();
        private NutritionIntakeStatus status;
        private List<CodeableConcept> statusReason = new ArrayList<>();
        private CodeableConcept code;
        private Reference subject;
        private Reference encounter;
        private Element occurrence;
        private DateTime recorded;
        private Element reported;
        private List<ConsumedItem> consumedItem = new ArrayList<>();
        private List<IngredientLabel> ingredientLabel = new ArrayList<>();
        private List<Performer> performer = new ArrayList<>();
        private Reference location;
        private List<Reference> derivedFrom = new ArrayList<>();
        private List<CodeableReference> reason = new ArrayList<>();
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
         * Identifiers associated with this Nutrition Intake that are defined by business processes and/or used to refer to it 
         * when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to this 
         * resource by the performer or other systems and remain constant as the resource is updated and propagates from server 
         * to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifier
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
         * Identifiers associated with this Nutrition Intake that are defined by business processes and/or used to refer to it 
         * when a direct URL reference to the resource itself is not appropriate. They are business identifiers assigned to this 
         * resource by the performer or other systems and remain constant as the resource is updated and propagates from server 
         * to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External identifier
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
         * Instantiates FHIR protocol or definition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesCanonical
         *     Instantiates FHIR protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesCanonical(Canonical... instantiatesCanonical) {
            for (Canonical value : instantiatesCanonical) {
                this.instantiatesCanonical.add(value);
            }
            return this;
        }

        /**
         * Instantiates FHIR protocol or definition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesCanonical
         *     Instantiates FHIR protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instantiatesCanonical(Collection<Canonical> instantiatesCanonical) {
            this.instantiatesCanonical = new ArrayList<>(instantiatesCanonical);
            return this;
        }

        /**
         * Instantiates external protocol or definition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesUri
         *     Instantiates external protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesUri(Uri... instantiatesUri) {
            for (Uri value : instantiatesUri) {
                this.instantiatesUri.add(value);
            }
            return this;
        }

        /**
         * Instantiates external protocol or definition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesUri
         *     Instantiates external protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instantiatesUri(Collection<Uri> instantiatesUri) {
            this.instantiatesUri = new ArrayList<>(instantiatesUri);
            return this;
        }

        /**
         * A plan, proposal or order that is fulfilled in whole or in part by this event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     Fulfils plan, proposal or order
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
         * A plan, proposal or order that is fulfilled in whole or in part by this event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     Fulfils plan, proposal or order
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
         * A larger event of which this particular event is a component or step.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link NutritionIntake}</li>
         * <li>{@link Procedure}</li>
         * <li>{@link Observation}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder partOf(Reference... partOf) {
            for (Reference value : partOf) {
                this.partOf.add(value);
            }
            return this;
        }

        /**
         * A larger event of which this particular event is a component or step.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link NutritionIntake}</li>
         * <li>{@link Procedure}</li>
         * <li>{@link Observation}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder partOf(Collection<Reference> partOf) {
            this.partOf = new ArrayList<>(partOf);
            return this;
        }

        /**
         * A code representing the patient or other source's judgment about the state of the intake that this assertion is about. 
         * Generally, this will be active or completed.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     preparation | in-progress | not-done | on-hold | stopped | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(NutritionIntakeStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Captures the reason for the current state of the NutritionIntake.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusReason
         *     Reason for current status
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusReason(CodeableConcept... statusReason) {
            for (CodeableConcept value : statusReason) {
                this.statusReason.add(value);
            }
            return this;
        }

        /**
         * Captures the reason for the current state of the NutritionIntake.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusReason
         *     Reason for current status
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder statusReason(Collection<CodeableConcept> statusReason) {
            this.statusReason = new ArrayList<>(statusReason);
            return this;
        }

        /**
         * Overall type of nutrition intake.
         * 
         * @param code
         *     Code representing an overall type of nutrition intake
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * The person, animal or group who is/was consuming the food or fluid.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     Who is/was consuming the food or fluid
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The encounter that establishes the context for this NutritionIntake.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter associated with NutritionIntake
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * The interval of time during which it is being asserted that the patient is/was consuming the food or fluid.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * </ul>
         * 
         * @param occurrence
         *     The date/time or interval when the food or fluid is/was consumed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrence(Element occurrence) {
            this.occurrence = occurrence;
            return this;
        }

        /**
         * The date when the Nutrition Intake was asserted by the information source.
         * 
         * @param recorded
         *     When the intake was recorded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recorded(DateTime recorded) {
            this.recorded = recorded;
            return this;
        }

        /**
         * Convenience method for setting {@code reported} with choice type Boolean.
         * 
         * @param reported
         *     Person or organization that provided the information about the consumption of this food or fluid
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #reported(Element)
         */
        public Builder reported(java.lang.Boolean reported) {
            this.reported = (reported == null) ? null : Boolean.of(reported);
            return this;
        }

        /**
         * The person or organization that provided the information about the consumption of this food or fluid. Note: Use 
         * derivedFrom when a NutritionIntake is derived from other resources.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Boolean}</li>
         * <li>{@link Reference}</li>
         * </ul>
         * 
         * When of type {@link Reference}, the allowed resource types for this reference are:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param reported
         *     Person or organization that provided the information about the consumption of this food or fluid
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reported(Element reported) {
            this.reported = reported;
            return this;
        }

        /**
         * What food or fluid product or item was consumed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param consumedItem
         *     What food or fluid product or item was consumed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder consumedItem(ConsumedItem... consumedItem) {
            for (ConsumedItem value : consumedItem) {
                this.consumedItem.add(value);
            }
            return this;
        }

        /**
         * What food or fluid product or item was consumed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param consumedItem
         *     What food or fluid product or item was consumed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder consumedItem(Collection<ConsumedItem> consumedItem) {
            this.consumedItem = new ArrayList<>(consumedItem);
            return this;
        }

        /**
         * Total nutrient amounts for the whole meal, product, serving, etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param ingredientLabel
         *     Total nutrient for the whole meal, product, serving
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder ingredientLabel(IngredientLabel... ingredientLabel) {
            for (IngredientLabel value : ingredientLabel) {
                this.ingredientLabel.add(value);
            }
            return this;
        }

        /**
         * Total nutrient amounts for the whole meal, product, serving, etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param ingredientLabel
         *     Total nutrient for the whole meal, product, serving
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder ingredientLabel(Collection<IngredientLabel> ingredientLabel) {
            this.ingredientLabel = new ArrayList<>(ingredientLabel);
            return this;
        }

        /**
         * Who performed the intake and how they were involved.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who was performed in the intake
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
         * Who performed the intake and how they were involved.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who was performed in the intake
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
         * Where the intake occurred.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where the intake occurred
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * Allows linking the NutritionIntake to the underlying NutritionOrder, or to other information, such as 
         * AllergyIntolerance, that supports or is used to derive the NutritionIntake.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFrom
         *     Additional supporting information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFrom(Reference... derivedFrom) {
            for (Reference value : derivedFrom) {
                this.derivedFrom.add(value);
            }
            return this;
        }

        /**
         * Allows linking the NutritionIntake to the underlying NutritionOrder, or to other information, such as 
         * AllergyIntolerance, that supports or is used to derive the NutritionIntake.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFrom
         *     Additional supporting information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder derivedFrom(Collection<Reference> derivedFrom) {
            this.derivedFrom = new ArrayList<>(derivedFrom);
            return this;
        }

        /**
         * A reason, Condition or observation for why the food or fluid is /was consumed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason for why the food or fluid is /was consumed
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
         * A reason, Condition or observation for why the food or fluid is /was consumed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Reason for why the food or fluid is /was consumed
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
         * Provides extra information about the Nutrition Intake that is not conveyed by the other attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Further information about the consumption
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
         * Provides extra information about the Nutrition Intake that is not conveyed by the other attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Further information about the consumption
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
         * Build the {@link NutritionIntake}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>subject</li>
         * <li>consumedItem</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link NutritionIntake}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid NutritionIntake per the base specification
         */
        @Override
        public NutritionIntake build() {
            NutritionIntake nutritionIntake = new NutritionIntake(this);
            if (validating) {
                validate(nutritionIntake);
            }
            return nutritionIntake;
        }

        protected void validate(NutritionIntake nutritionIntake) {
            super.validate(nutritionIntake);
            ValidationSupport.checkList(nutritionIntake.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(nutritionIntake.instantiatesCanonical, "instantiatesCanonical", Canonical.class);
            ValidationSupport.checkList(nutritionIntake.instantiatesUri, "instantiatesUri", Uri.class);
            ValidationSupport.checkList(nutritionIntake.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(nutritionIntake.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(nutritionIntake.status, "status");
            ValidationSupport.checkList(nutritionIntake.statusReason, "statusReason", CodeableConcept.class);
            ValidationSupport.requireNonNull(nutritionIntake.subject, "subject");
            ValidationSupport.choiceElement(nutritionIntake.occurrence, "occurrence", DateTime.class, Period.class);
            ValidationSupport.choiceElement(nutritionIntake.reported, "reported", Boolean.class, Reference.class);
            ValidationSupport.checkNonEmptyList(nutritionIntake.consumedItem, "consumedItem", ConsumedItem.class);
            ValidationSupport.checkList(nutritionIntake.ingredientLabel, "ingredientLabel", IngredientLabel.class);
            ValidationSupport.checkList(nutritionIntake.performer, "performer", Performer.class);
            ValidationSupport.checkList(nutritionIntake.derivedFrom, "derivedFrom", Reference.class);
            ValidationSupport.checkList(nutritionIntake.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(nutritionIntake.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(nutritionIntake.basedOn, "basedOn", "NutritionOrder", "CarePlan", "ServiceRequest");
            ValidationSupport.checkReferenceType(nutritionIntake.partOf, "partOf", "NutritionIntake", "Procedure", "Observation");
            ValidationSupport.checkReferenceType(nutritionIntake.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(nutritionIntake.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(nutritionIntake.reported, "reported", "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization");
            ValidationSupport.checkReferenceType(nutritionIntake.location, "location", "Location");
        }

        protected Builder from(NutritionIntake nutritionIntake) {
            super.from(nutritionIntake);
            identifier.addAll(nutritionIntake.identifier);
            instantiatesCanonical.addAll(nutritionIntake.instantiatesCanonical);
            instantiatesUri.addAll(nutritionIntake.instantiatesUri);
            basedOn.addAll(nutritionIntake.basedOn);
            partOf.addAll(nutritionIntake.partOf);
            status = nutritionIntake.status;
            statusReason.addAll(nutritionIntake.statusReason);
            code = nutritionIntake.code;
            subject = nutritionIntake.subject;
            encounter = nutritionIntake.encounter;
            occurrence = nutritionIntake.occurrence;
            recorded = nutritionIntake.recorded;
            reported = nutritionIntake.reported;
            consumedItem.addAll(nutritionIntake.consumedItem);
            ingredientLabel.addAll(nutritionIntake.ingredientLabel);
            performer.addAll(nutritionIntake.performer);
            location = nutritionIntake.location;
            derivedFrom.addAll(nutritionIntake.derivedFrom);
            reason.addAll(nutritionIntake.reason);
            note.addAll(nutritionIntake.note);
            return this;
        }
    }

    /**
     * What food or fluid product or item was consumed.
     */
    public static class ConsumedItem extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "FoodType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Types of food.",
            valueSet = "http://hl7.org/fhir/ValueSet/edible-substance-type"
        )
        @Required
        private final CodeableConcept type;
        @Summary
        @Binding(
            bindingName = "FoodProduct",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Specific food that can be consumed by a patient.",
            valueSet = "http://hl7.org/fhir/ValueSet/food-type"
        )
        @Required
        private final CodeableReference nutritionProduct;
        private final Timing schedule;
        @Summary
        private final SimpleQuantity amount;
        @Summary
        private final SimpleQuantity rate;
        private final Boolean notConsumed;
        @Binding(
            bindingName = "NotConsumedReason",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Reasons for why something was not consumed.",
            valueSet = "http://hl7.org/fhir/ValueSet/not-consumed-reason"
        )
        private final CodeableConcept notConsumedReason;

        private ConsumedItem(Builder builder) {
            super(builder);
            type = builder.type;
            nutritionProduct = builder.nutritionProduct;
            schedule = builder.schedule;
            amount = builder.amount;
            rate = builder.rate;
            notConsumed = builder.notConsumed;
            notConsumedReason = builder.notConsumedReason;
        }

        /**
         * Indicates what a category of item that was consumed: e.g., food, fluid, enteral, etc.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Identifies the food or fluid product that was consumed. This is potentially a link to a resource representing the 
         * details of the food product (TBD) or a simple attribute carrying a code that identifies the food from a known list of 
         * foods.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getNutritionProduct() {
            return nutritionProduct;
        }

        /**
         * Scheduled frequency of consumption.
         * 
         * @return
         *     An immutable object of type {@link Timing} that may be null.
         */
        public Timing getSchedule() {
            return schedule;
        }

        /**
         * Quantity of the specified food.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getAmount() {
            return amount;
        }

        /**
         * Rate at which enteral feeding was administered.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getRate() {
            return rate;
        }

        /**
         * Indicator when a patient is in a setting where it is helpful to know if food was not consumed, such as it was refused, 
         * held (as in tube feedings), or otherwise not provided. If a consumption is being recorded from an app, such as 
         * MyFitnessPal, this indicator will likely not be used.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getNotConsumed() {
            return notConsumed;
        }

        /**
         * Document the reason the food or fluid was not consumed, such as refused, held, etc.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getNotConsumedReason() {
            return notConsumedReason;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (nutritionProduct != null) || 
                (schedule != null) || 
                (amount != null) || 
                (rate != null) || 
                (notConsumed != null) || 
                (notConsumedReason != null);
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
                    accept(nutritionProduct, "nutritionProduct", visitor);
                    accept(schedule, "schedule", visitor);
                    accept(amount, "amount", visitor);
                    accept(rate, "rate", visitor);
                    accept(notConsumed, "notConsumed", visitor);
                    accept(notConsumedReason, "notConsumedReason", visitor);
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
            ConsumedItem other = (ConsumedItem) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(nutritionProduct, other.nutritionProduct) && 
                Objects.equals(schedule, other.schedule) && 
                Objects.equals(amount, other.amount) && 
                Objects.equals(rate, other.rate) && 
                Objects.equals(notConsumed, other.notConsumed) && 
                Objects.equals(notConsumedReason, other.notConsumedReason);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    nutritionProduct, 
                    schedule, 
                    amount, 
                    rate, 
                    notConsumed, 
                    notConsumedReason);
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
            private CodeableReference nutritionProduct;
            private Timing schedule;
            private SimpleQuantity amount;
            private SimpleQuantity rate;
            private Boolean notConsumed;
            private CodeableConcept notConsumedReason;

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
             * Indicates what a category of item that was consumed: e.g., food, fluid, enteral, etc.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     The type of food or fluid product
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Identifies the food or fluid product that was consumed. This is potentially a link to a resource representing the 
             * details of the food product (TBD) or a simple attribute carrying a code that identifies the food from a known list of 
             * foods.
             * 
             * <p>This element is required.
             * 
             * @param nutritionProduct
             *     Code that identifies the food or fluid product that was consumed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder nutritionProduct(CodeableReference nutritionProduct) {
                this.nutritionProduct = nutritionProduct;
                return this;
            }

            /**
             * Scheduled frequency of consumption.
             * 
             * @param schedule
             *     Scheduled frequency of consumption
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder schedule(Timing schedule) {
                this.schedule = schedule;
                return this;
            }

            /**
             * Quantity of the specified food.
             * 
             * @param amount
             *     Quantity of the specified food
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder amount(SimpleQuantity amount) {
                this.amount = amount;
                return this;
            }

            /**
             * Rate at which enteral feeding was administered.
             * 
             * @param rate
             *     Rate at which enteral feeding was administered
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder rate(SimpleQuantity rate) {
                this.rate = rate;
                return this;
            }

            /**
             * Convenience method for setting {@code notConsumed}.
             * 
             * @param notConsumed
             *     Flag to indicate if the food or fluid item was refused or otherwise not consumed
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #notConsumed(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder notConsumed(java.lang.Boolean notConsumed) {
                this.notConsumed = (notConsumed == null) ? null : Boolean.of(notConsumed);
                return this;
            }

            /**
             * Indicator when a patient is in a setting where it is helpful to know if food was not consumed, such as it was refused, 
             * held (as in tube feedings), or otherwise not provided. If a consumption is being recorded from an app, such as 
             * MyFitnessPal, this indicator will likely not be used.
             * 
             * @param notConsumed
             *     Flag to indicate if the food or fluid item was refused or otherwise not consumed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder notConsumed(Boolean notConsumed) {
                this.notConsumed = notConsumed;
                return this;
            }

            /**
             * Document the reason the food or fluid was not consumed, such as refused, held, etc.
             * 
             * @param notConsumedReason
             *     Reason food or fluid was not consumed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder notConsumedReason(CodeableConcept notConsumedReason) {
                this.notConsumedReason = notConsumedReason;
                return this;
            }

            /**
             * Build the {@link ConsumedItem}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>nutritionProduct</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ConsumedItem}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ConsumedItem per the base specification
             */
            @Override
            public ConsumedItem build() {
                ConsumedItem consumedItem = new ConsumedItem(this);
                if (validating) {
                    validate(consumedItem);
                }
                return consumedItem;
            }

            protected void validate(ConsumedItem consumedItem) {
                super.validate(consumedItem);
                ValidationSupport.requireNonNull(consumedItem.type, "type");
                ValidationSupport.requireNonNull(consumedItem.nutritionProduct, "nutritionProduct");
                ValidationSupport.requireValueOrChildren(consumedItem);
            }

            protected Builder from(ConsumedItem consumedItem) {
                super.from(consumedItem);
                type = consumedItem.type;
                nutritionProduct = consumedItem.nutritionProduct;
                schedule = consumedItem.schedule;
                amount = consumedItem.amount;
                rate = consumedItem.rate;
                notConsumed = consumedItem.notConsumed;
                notConsumedReason = consumedItem.notConsumedReason;
                return this;
            }
        }
    }

    /**
     * Total nutrient amounts for the whole meal, product, serving, etc.
     */
    public static class IngredientLabel extends BackboneElement {
        @Binding(
            bindingName = "NutrientType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Types of nutrients that can be found in a nutrition product.",
            valueSet = "http://hl7.org/fhir/ValueSet/nutrient-code"
        )
        @Required
        private final CodeableReference nutrient;
        @Required
        private final SimpleQuantity amount;

        private IngredientLabel(Builder builder) {
            super(builder);
            nutrient = builder.nutrient;
            amount = builder.amount;
        }

        /**
         * Total nutrient consumed. This could be a macronutrient (protein, fat, carbohydrate), or a vitamin and mineral.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getNutrient() {
            return nutrient;
        }

        /**
         * Total amount of nutrient consumed.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that is non-null.
         */
        public SimpleQuantity getAmount() {
            return amount;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (nutrient != null) || 
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
                    accept(nutrient, "nutrient", visitor);
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
            IngredientLabel other = (IngredientLabel) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(nutrient, other.nutrient) && 
                Objects.equals(amount, other.amount);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    nutrient, 
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
            private CodeableReference nutrient;
            private SimpleQuantity amount;

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
             * Total nutrient consumed. This could be a macronutrient (protein, fat, carbohydrate), or a vitamin and mineral.
             * 
             * <p>This element is required.
             * 
             * @param nutrient
             *     Total nutrient consumed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder nutrient(CodeableReference nutrient) {
                this.nutrient = nutrient;
                return this;
            }

            /**
             * Total amount of nutrient consumed.
             * 
             * <p>This element is required.
             * 
             * @param amount
             *     Total amount of nutrient consumed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder amount(SimpleQuantity amount) {
                this.amount = amount;
                return this;
            }

            /**
             * Build the {@link IngredientLabel}
             * 
             * <p>Required elements:
             * <ul>
             * <li>nutrient</li>
             * <li>amount</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link IngredientLabel}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid IngredientLabel per the base specification
             */
            @Override
            public IngredientLabel build() {
                IngredientLabel ingredientLabel = new IngredientLabel(this);
                if (validating) {
                    validate(ingredientLabel);
                }
                return ingredientLabel;
            }

            protected void validate(IngredientLabel ingredientLabel) {
                super.validate(ingredientLabel);
                ValidationSupport.requireNonNull(ingredientLabel.nutrient, "nutrient");
                ValidationSupport.requireNonNull(ingredientLabel.amount, "amount");
                ValidationSupport.requireValueOrChildren(ingredientLabel);
            }

            protected Builder from(IngredientLabel ingredientLabel) {
                super.from(ingredientLabel);
                nutrient = ingredientLabel.nutrient;
                amount = ingredientLabel.amount;
                return this;
            }
        }
    }

    /**
     * Who performed the intake and how they were involved.
     */
    public static class Performer extends BackboneElement {
        @Binding(
            bindingName = "NutritionPerformerType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Type of performance.",
            valueSet = "http://hl7.org/fhir/ValueSet/performer-role"
        )
        private final CodeableConcept function;
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson" })
        @Required
        private final Reference actor;

        private Performer(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Type of performer.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * Who performed the intake.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getActor() {
            return actor;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (function != null) || 
                (actor != null);
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
                    accept(function, "function", visitor);
                    accept(actor, "actor", visitor);
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
                Objects.equals(function, other.function) && 
                Objects.equals(actor, other.actor);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    function, 
                    actor);
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
            private CodeableConcept function;
            private Reference actor;

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
             * Type of performer.
             * 
             * @param function
             *     Type of performer
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * Who performed the intake.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * <li>{@link CareTeam}</li>
             * <li>{@link Patient}</li>
             * <li>{@link Device}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param actor
             *     Who performed the intake
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actor(Reference actor) {
                this.actor = actor;
                return this;
            }

            /**
             * Build the {@link Performer}
             * 
             * <p>Required elements:
             * <ul>
             * <li>actor</li>
             * </ul>
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
                ValidationSupport.requireNonNull(performer.actor, "actor");
                ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson");
                ValidationSupport.requireValueOrChildren(performer);
            }

            protected Builder from(Performer performer) {
                super.from(performer);
                function = performer.function;
                actor = performer.actor;
                return this;
            }
        }
    }
}
