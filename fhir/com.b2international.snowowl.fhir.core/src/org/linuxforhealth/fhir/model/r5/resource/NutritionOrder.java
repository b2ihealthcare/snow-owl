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
import org.linuxforhealth.fhir.model.r5.type.Boolean;
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
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.NutritionOrderIntent;
import org.linuxforhealth.fhir.model.r5.type.code.NutritionOrderPriority;
import org.linuxforhealth.fhir.model.r5.type.code.NutritionOrderStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A request to supply a diet, formula feeding (enteral) or oral nutritional supplement to a patient/resident.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "nor-1",
    level = "Warning",
    location = "(base)",
    description = "Nutrition Order SHALL contain either Oral Diet , Supplement, or Enteral Formula class",
    expression = "oralDiet.exists() or supplement.exists() or enteralFormula.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/NutritionOrder"
)
@Constraint(
    id = "nutritionOrder-2",
    level = "Warning",
    location = "enteralFormula.routeOfAdministration",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/enteral-route",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/enteral-route', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/NutritionOrder",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class NutritionOrder extends DomainResource {
    private final List<Identifier> identifier;
    @Summary
    private final List<Canonical> instantiatesCanonical;
    @Summary
    private final List<Uri> instantiatesUri;
    private final List<Uri> instantiates;
    @ReferenceTarget({ "CarePlan", "NutritionOrder", "ServiceRequest" })
    private final List<Reference> basedOn;
    @Summary
    private final Identifier groupIdentifier;
    @Summary
    @Binding(
        bindingName = "NutritionOrderStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes identifying the lifecycle stage of the nutrition order.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-status|5.0.0"
    )
    @Required
    private final NutritionOrderStatus status;
    @Summary
    @Binding(
        bindingName = "NutritiionOrderIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes indicating the degree of authority/intentionality associated with a nutrition order.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-intent|5.0.0"
    )
    @Required
    private final NutritionOrderIntent intent;
    @Binding(
        bindingName = "NutritionOrderPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "Identifies the level of importance to be assigned to actioning the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final NutritionOrderPriority priority;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    @Required
    private final Reference subject;
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    private final List<Reference> supportingInformation;
    @Summary
    @Required
    private final DateTime dateTime;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole" })
    private final Reference orderer;
    private final List<CodeableReference> performer;
    @ReferenceTarget({ "AllergyIntolerance" })
    private final List<Reference> allergyIntolerance;
    @Binding(
        bindingName = "PatientDiet",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Medical, cultural or ethical food preferences to help with catering requirements.",
        valueSet = "http://hl7.org/fhir/ValueSet/encounter-diet"
    )
    private final List<CodeableConcept> foodPreferenceModifier;
    @Binding(
        bindingName = "FoodType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes used to indicate the type of food that should NOT be given to the patient.",
        valueSet = "http://hl7.org/fhir/ValueSet/food-type"
    )
    private final List<CodeableConcept> excludeFoodModifier;
    private final Boolean outsideFoodAllowed;
    private final OralDiet oralDiet;
    private final List<Supplement> supplement;
    private final EnteralFormula enteralFormula;
    private final List<Annotation> note;

    private NutritionOrder(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = Collections.unmodifiableList(builder.instantiatesCanonical);
        instantiatesUri = Collections.unmodifiableList(builder.instantiatesUri);
        instantiates = Collections.unmodifiableList(builder.instantiates);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        groupIdentifier = builder.groupIdentifier;
        status = builder.status;
        intent = builder.intent;
        priority = builder.priority;
        subject = builder.subject;
        encounter = builder.encounter;
        supportingInformation = Collections.unmodifiableList(builder.supportingInformation);
        dateTime = builder.dateTime;
        orderer = builder.orderer;
        performer = Collections.unmodifiableList(builder.performer);
        allergyIntolerance = Collections.unmodifiableList(builder.allergyIntolerance);
        foodPreferenceModifier = Collections.unmodifiableList(builder.foodPreferenceModifier);
        excludeFoodModifier = Collections.unmodifiableList(builder.excludeFoodModifier);
        outsideFoodAllowed = builder.outsideFoodAllowed;
        oralDiet = builder.oralDiet;
        supplement = Collections.unmodifiableList(builder.supplement);
        enteralFormula = builder.enteralFormula;
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * Identifiers assigned to this order by the order sender or by the order receiver.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
     * part by this NutritionOrder.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
     * whole or in part by this NutritionOrder.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * The URL pointing to a protocol, guideline, orderset or other definition that is adhered to in whole or in part by this 
     * NutritionOrder.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiates() {
        return instantiates;
    }

    /**
     * A plan or request that is fulfilled in whole or in part by this nutrition order.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * A shared identifier common to all nutrition orders that were authorized more or less simultaneously by a single 
     * author, representing the composite or group identifier.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getGroupIdentifier() {
        return groupIdentifier;
    }

    /**
     * The workflow status of the nutrition order/request.
     * 
     * @return
     *     An immutable object of type {@link NutritionOrderStatus} that is non-null.
     */
    public NutritionOrderStatus getStatus() {
        return status;
    }

    /**
     * Indicates the level of authority/intentionality associated with the NutrionOrder and where the request fits into the 
     * workflow chain.
     * 
     * @return
     *     An immutable object of type {@link NutritionOrderIntent} that is non-null.
     */
    public NutritionOrderIntent getIntent() {
        return intent;
    }

    /**
     * Indicates how quickly the Nutrition Order should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link NutritionOrderPriority} that may be null.
     */
    public NutritionOrderPriority getPriority() {
        return priority;
    }

    /**
     * The person or set of individuals who needs the nutrition order for an oral diet, nutritional supplement and/or enteral 
     * or formula feeding.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * An encounter that provides additional information about the healthcare context in which this request is made.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Information to support fulfilling (i.e. dispensing or administering) of the nutrition, for example, patient height and 
     * weight).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInformation() {
        return supportingInformation;
    }

    /**
     * The date and time that this nutrition order was requested.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that is non-null.
     */
    public DateTime getDateTime() {
        return dateTime;
    }

    /**
     * The practitioner that holds legal responsibility for ordering the diet, nutritional supplement, or formula feedings.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getOrderer() {
        return orderer;
    }

    /**
     * The specified desired performer of the nutrition order.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getPerformer() {
        return performer;
    }

    /**
     * A link to a record of allergies or intolerances which should be included in the nutrition order.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getAllergyIntolerance() {
        return allergyIntolerance;
    }

    /**
     * This modifier is used to convey order-specific modifiers about the type of food that should be given. These can be 
     * derived from patient allergies, intolerances, or preferences such as Halal, Vegan or Kosher. This modifier applies to 
     * the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getFoodPreferenceModifier() {
        return foodPreferenceModifier;
    }

    /**
     * This modifier is used to convey Order-specific modifier about the type of oral food or oral fluids that should not be 
     * given. These can be derived from patient allergies, intolerances, or preferences such as No Red Meat, No Soy or No 
     * Wheat or Gluten-Free. While it should not be necessary to repeat allergy or intolerance information captured in the 
     * referenced AllergyIntolerance resource in the excludeFoodModifier, this element may be used to convey additional 
     * specificity related to foods that should be eliminated from the patient’s diet for any reason. This modifier applies 
     * to the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getExcludeFoodModifier() {
        return excludeFoodModifier;
    }

    /**
     * This modifier is used to convey whether a food item is allowed to be brought in by the patient and/or family. If set 
     * to true, indicates that the receiving system does not need to supply the food item.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getOutsideFoodAllowed() {
        return outsideFoodAllowed;
    }

    /**
     * Diet given orally in contrast to enteral (tube) feeding.
     * 
     * @return
     *     An immutable object of type {@link OralDiet} that may be null.
     */
    public OralDiet getOralDiet() {
        return oralDiet;
    }

    /**
     * Oral nutritional products given in order to add further nutritional value to the patient's diet.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Supplement} that may be empty.
     */
    public List<Supplement> getSupplement() {
        return supplement;
    }

    /**
     * Feeding provided through the gastrointestinal tract via a tube, catheter, or stoma that delivers nutrition distal to 
     * the oral cavity.
     * 
     * @return
     *     An immutable object of type {@link EnteralFormula} that may be null.
     */
    public EnteralFormula getEnteralFormula() {
        return enteralFormula;
    }

    /**
     * Comments made about the {{title}} by the requester, performer, subject or other participants.
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
            !instantiates.isEmpty() || 
            !basedOn.isEmpty() || 
            (groupIdentifier != null) || 
            (status != null) || 
            (intent != null) || 
            (priority != null) || 
            (subject != null) || 
            (encounter != null) || 
            !supportingInformation.isEmpty() || 
            (dateTime != null) || 
            (orderer != null) || 
            !performer.isEmpty() || 
            !allergyIntolerance.isEmpty() || 
            !foodPreferenceModifier.isEmpty() || 
            !excludeFoodModifier.isEmpty() || 
            (outsideFoodAllowed != null) || 
            (oralDiet != null) || 
            !supplement.isEmpty() || 
            (enteralFormula != null) || 
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
                accept(instantiates, "instantiates", visitor, Uri.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(groupIdentifier, "groupIdentifier", visitor);
                accept(status, "status", visitor);
                accept(intent, "intent", visitor);
                accept(priority, "priority", visitor);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(supportingInformation, "supportingInformation", visitor, Reference.class);
                accept(dateTime, "dateTime", visitor);
                accept(orderer, "orderer", visitor);
                accept(performer, "performer", visitor, CodeableReference.class);
                accept(allergyIntolerance, "allergyIntolerance", visitor, Reference.class);
                accept(foodPreferenceModifier, "foodPreferenceModifier", visitor, CodeableConcept.class);
                accept(excludeFoodModifier, "excludeFoodModifier", visitor, CodeableConcept.class);
                accept(outsideFoodAllowed, "outsideFoodAllowed", visitor);
                accept(oralDiet, "oralDiet", visitor);
                accept(supplement, "supplement", visitor, Supplement.class);
                accept(enteralFormula, "enteralFormula", visitor);
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
        NutritionOrder other = (NutritionOrder) obj;
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
            Objects.equals(instantiates, other.instantiates) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(groupIdentifier, other.groupIdentifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(supportingInformation, other.supportingInformation) && 
            Objects.equals(dateTime, other.dateTime) && 
            Objects.equals(orderer, other.orderer) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(allergyIntolerance, other.allergyIntolerance) && 
            Objects.equals(foodPreferenceModifier, other.foodPreferenceModifier) && 
            Objects.equals(excludeFoodModifier, other.excludeFoodModifier) && 
            Objects.equals(outsideFoodAllowed, other.outsideFoodAllowed) && 
            Objects.equals(oralDiet, other.oralDiet) && 
            Objects.equals(supplement, other.supplement) && 
            Objects.equals(enteralFormula, other.enteralFormula) && 
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
                instantiates, 
                basedOn, 
                groupIdentifier, 
                status, 
                intent, 
                priority, 
                subject, 
                encounter, 
                supportingInformation, 
                dateTime, 
                orderer, 
                performer, 
                allergyIntolerance, 
                foodPreferenceModifier, 
                excludeFoodModifier, 
                outsideFoodAllowed, 
                oralDiet, 
                supplement, 
                enteralFormula, 
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
        private List<Uri> instantiates = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private Identifier groupIdentifier;
        private NutritionOrderStatus status;
        private NutritionOrderIntent intent;
        private NutritionOrderPriority priority;
        private Reference subject;
        private Reference encounter;
        private List<Reference> supportingInformation = new ArrayList<>();
        private DateTime dateTime;
        private Reference orderer;
        private List<CodeableReference> performer = new ArrayList<>();
        private List<Reference> allergyIntolerance = new ArrayList<>();
        private List<CodeableConcept> foodPreferenceModifier = new ArrayList<>();
        private List<CodeableConcept> excludeFoodModifier = new ArrayList<>();
        private Boolean outsideFoodAllowed;
        private OralDiet oralDiet;
        private List<Supplement> supplement = new ArrayList<>();
        private EnteralFormula enteralFormula;
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
         * Identifiers assigned to this order by the order sender or by the order receiver.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers assigned to this order
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
         * Identifiers assigned to this order by the order sender or by the order receiver.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers assigned to this order
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
         * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
         * part by this NutritionOrder.
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
         * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
         * part by this NutritionOrder.
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
         * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this NutritionOrder.
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
         * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this NutritionOrder.
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
         * The URL pointing to a protocol, guideline, orderset or other definition that is adhered to in whole or in part by this 
         * NutritionOrder.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiates
         *     Instantiates protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiates(Uri... instantiates) {
            for (Uri value : instantiates) {
                this.instantiates.add(value);
            }
            return this;
        }

        /**
         * The URL pointing to a protocol, guideline, orderset or other definition that is adhered to in whole or in part by this 
         * NutritionOrder.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiates
         *     Instantiates protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instantiates(Collection<Uri> instantiates) {
            this.instantiates = new ArrayList<>(instantiates);
            return this;
        }

        /**
         * A plan or request that is fulfilled in whole or in part by this nutrition order.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     What this order fulfills
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
         * A plan or request that is fulfilled in whole or in part by this nutrition order.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link NutritionOrder}</li>
         * <li>{@link ServiceRequest}</li>
         * </ul>
         * 
         * @param basedOn
         *     What this order fulfills
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
         * A shared identifier common to all nutrition orders that were authorized more or less simultaneously by a single 
         * author, representing the composite or group identifier.
         * 
         * @param groupIdentifier
         *     Composite Request ID
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder groupIdentifier(Identifier groupIdentifier) {
            this.groupIdentifier = groupIdentifier;
            return this;
        }

        /**
         * The workflow status of the nutrition order/request.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | on-hold | revoked | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(NutritionOrderStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Indicates the level of authority/intentionality associated with the NutrionOrder and where the request fits into the 
         * workflow chain.
         * 
         * <p>This element is required.
         * 
         * @param intent
         *     proposal | plan | directive | order | original-order | reflex-order | filler-order | instance-order | option
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(NutritionOrderIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * Indicates how quickly the Nutrition Order should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(NutritionOrderPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * The person or set of individuals who needs the nutrition order for an oral diet, nutritional supplement and/or enteral 
         * or formula feeding.
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
         *     Who requires the diet, formula or nutritional supplement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * An encounter that provides additional information about the healthcare context in which this request is made.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     The encounter associated with this nutrition order
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Information to support fulfilling (i.e. dispensing or administering) of the nutrition, for example, patient height and 
         * weight).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Information to support fulfilling of the nutrition order
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supportingInformation(Reference... supportingInformation) {
            for (Reference value : supportingInformation) {
                this.supportingInformation.add(value);
            }
            return this;
        }

        /**
         * Information to support fulfilling (i.e. dispensing or administering) of the nutrition, for example, patient height and 
         * weight).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Information to support fulfilling of the nutrition order
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supportingInformation(Collection<Reference> supportingInformation) {
            this.supportingInformation = new ArrayList<>(supportingInformation);
            return this;
        }

        /**
         * The date and time that this nutrition order was requested.
         * 
         * <p>This element is required.
         * 
         * @param dateTime
         *     Date and time the nutrition order was requested
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dateTime(DateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        /**
         * The practitioner that holds legal responsibility for ordering the diet, nutritional supplement, or formula feedings.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param orderer
         *     Who ordered the diet, formula or nutritional supplement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder orderer(Reference orderer) {
            this.orderer = orderer;
            return this;
        }

        /**
         * The specified desired performer of the nutrition order.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who is desired to perform the administration of what is being ordered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performer(CodeableReference... performer) {
            for (CodeableReference value : performer) {
                this.performer.add(value);
            }
            return this;
        }

        /**
         * The specified desired performer of the nutrition order.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who is desired to perform the administration of what is being ordered
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder performer(Collection<CodeableReference> performer) {
            this.performer = new ArrayList<>(performer);
            return this;
        }

        /**
         * A link to a record of allergies or intolerances which should be included in the nutrition order.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link AllergyIntolerance}</li>
         * </ul>
         * 
         * @param allergyIntolerance
         *     List of the patient's food and nutrition-related allergies and intolerances
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder allergyIntolerance(Reference... allergyIntolerance) {
            for (Reference value : allergyIntolerance) {
                this.allergyIntolerance.add(value);
            }
            return this;
        }

        /**
         * A link to a record of allergies or intolerances which should be included in the nutrition order.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link AllergyIntolerance}</li>
         * </ul>
         * 
         * @param allergyIntolerance
         *     List of the patient's food and nutrition-related allergies and intolerances
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder allergyIntolerance(Collection<Reference> allergyIntolerance) {
            this.allergyIntolerance = new ArrayList<>(allergyIntolerance);
            return this;
        }

        /**
         * This modifier is used to convey order-specific modifiers about the type of food that should be given. These can be 
         * derived from patient allergies, intolerances, or preferences such as Halal, Vegan or Kosher. This modifier applies to 
         * the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param foodPreferenceModifier
         *     Order-specific modifier about the type of food that should be given
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder foodPreferenceModifier(CodeableConcept... foodPreferenceModifier) {
            for (CodeableConcept value : foodPreferenceModifier) {
                this.foodPreferenceModifier.add(value);
            }
            return this;
        }

        /**
         * This modifier is used to convey order-specific modifiers about the type of food that should be given. These can be 
         * derived from patient allergies, intolerances, or preferences such as Halal, Vegan or Kosher. This modifier applies to 
         * the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param foodPreferenceModifier
         *     Order-specific modifier about the type of food that should be given
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder foodPreferenceModifier(Collection<CodeableConcept> foodPreferenceModifier) {
            this.foodPreferenceModifier = new ArrayList<>(foodPreferenceModifier);
            return this;
        }

        /**
         * This modifier is used to convey Order-specific modifier about the type of oral food or oral fluids that should not be 
         * given. These can be derived from patient allergies, intolerances, or preferences such as No Red Meat, No Soy or No 
         * Wheat or Gluten-Free. While it should not be necessary to repeat allergy or intolerance information captured in the 
         * referenced AllergyIntolerance resource in the excludeFoodModifier, this element may be used to convey additional 
         * specificity related to foods that should be eliminated from the patient’s diet for any reason. This modifier applies 
         * to the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param excludeFoodModifier
         *     Order-specific modifier about the type of food that should not be given
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder excludeFoodModifier(CodeableConcept... excludeFoodModifier) {
            for (CodeableConcept value : excludeFoodModifier) {
                this.excludeFoodModifier.add(value);
            }
            return this;
        }

        /**
         * This modifier is used to convey Order-specific modifier about the type of oral food or oral fluids that should not be 
         * given. These can be derived from patient allergies, intolerances, or preferences such as No Red Meat, No Soy or No 
         * Wheat or Gluten-Free. While it should not be necessary to repeat allergy or intolerance information captured in the 
         * referenced AllergyIntolerance resource in the excludeFoodModifier, this element may be used to convey additional 
         * specificity related to foods that should be eliminated from the patient’s diet for any reason. This modifier applies 
         * to the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param excludeFoodModifier
         *     Order-specific modifier about the type of food that should not be given
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder excludeFoodModifier(Collection<CodeableConcept> excludeFoodModifier) {
            this.excludeFoodModifier = new ArrayList<>(excludeFoodModifier);
            return this;
        }

        /**
         * Convenience method for setting {@code outsideFoodAllowed}.
         * 
         * @param outsideFoodAllowed
         *     Capture when a food item is brought in by the patient and/or family
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #outsideFoodAllowed(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder outsideFoodAllowed(java.lang.Boolean outsideFoodAllowed) {
            this.outsideFoodAllowed = (outsideFoodAllowed == null) ? null : Boolean.of(outsideFoodAllowed);
            return this;
        }

        /**
         * This modifier is used to convey whether a food item is allowed to be brought in by the patient and/or family. If set 
         * to true, indicates that the receiving system does not need to supply the food item.
         * 
         * @param outsideFoodAllowed
         *     Capture when a food item is brought in by the patient and/or family
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder outsideFoodAllowed(Boolean outsideFoodAllowed) {
            this.outsideFoodAllowed = outsideFoodAllowed;
            return this;
        }

        /**
         * Diet given orally in contrast to enteral (tube) feeding.
         * 
         * @param oralDiet
         *     Oral diet components
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder oralDiet(OralDiet oralDiet) {
            this.oralDiet = oralDiet;
            return this;
        }

        /**
         * Oral nutritional products given in order to add further nutritional value to the patient's diet.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supplement
         *     Supplement components
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supplement(Supplement... supplement) {
            for (Supplement value : supplement) {
                this.supplement.add(value);
            }
            return this;
        }

        /**
         * Oral nutritional products given in order to add further nutritional value to the patient's diet.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supplement
         *     Supplement components
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supplement(Collection<Supplement> supplement) {
            this.supplement = new ArrayList<>(supplement);
            return this;
        }

        /**
         * Feeding provided through the gastrointestinal tract via a tube, catheter, or stoma that delivers nutrition distal to 
         * the oral cavity.
         * 
         * @param enteralFormula
         *     Enteral formula components
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder enteralFormula(EnteralFormula enteralFormula) {
            this.enteralFormula = enteralFormula;
            return this;
        }

        /**
         * Comments made about the {{title}} by the requester, performer, subject or other participants.
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
         * Comments made about the {{title}} by the requester, performer, subject or other participants.
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
        public Builder note(Collection<Annotation> note) {
            this.note = new ArrayList<>(note);
            return this;
        }

        /**
         * Build the {@link NutritionOrder}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>intent</li>
         * <li>subject</li>
         * <li>dateTime</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link NutritionOrder}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid NutritionOrder per the base specification
         */
        @Override
        public NutritionOrder build() {
            NutritionOrder nutritionOrder = new NutritionOrder(this);
            if (validating) {
                validate(nutritionOrder);
            }
            return nutritionOrder;
        }

        protected void validate(NutritionOrder nutritionOrder) {
            super.validate(nutritionOrder);
            ValidationSupport.checkList(nutritionOrder.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(nutritionOrder.instantiatesCanonical, "instantiatesCanonical", Canonical.class);
            ValidationSupport.checkList(nutritionOrder.instantiatesUri, "instantiatesUri", Uri.class);
            ValidationSupport.checkList(nutritionOrder.instantiates, "instantiates", Uri.class);
            ValidationSupport.checkList(nutritionOrder.basedOn, "basedOn", Reference.class);
            ValidationSupport.requireNonNull(nutritionOrder.status, "status");
            ValidationSupport.requireNonNull(nutritionOrder.intent, "intent");
            ValidationSupport.requireNonNull(nutritionOrder.subject, "subject");
            ValidationSupport.checkList(nutritionOrder.supportingInformation, "supportingInformation", Reference.class);
            ValidationSupport.requireNonNull(nutritionOrder.dateTime, "dateTime");
            ValidationSupport.checkList(nutritionOrder.performer, "performer", CodeableReference.class);
            ValidationSupport.checkList(nutritionOrder.allergyIntolerance, "allergyIntolerance", Reference.class);
            ValidationSupport.checkList(nutritionOrder.foodPreferenceModifier, "foodPreferenceModifier", CodeableConcept.class);
            ValidationSupport.checkList(nutritionOrder.excludeFoodModifier, "excludeFoodModifier", CodeableConcept.class);
            ValidationSupport.checkList(nutritionOrder.supplement, "supplement", Supplement.class);
            ValidationSupport.checkList(nutritionOrder.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(nutritionOrder.basedOn, "basedOn", "CarePlan", "NutritionOrder", "ServiceRequest");
            ValidationSupport.checkReferenceType(nutritionOrder.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(nutritionOrder.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(nutritionOrder.orderer, "orderer", "Practitioner", "PractitionerRole");
            ValidationSupport.checkReferenceType(nutritionOrder.allergyIntolerance, "allergyIntolerance", "AllergyIntolerance");
        }

        protected Builder from(NutritionOrder nutritionOrder) {
            super.from(nutritionOrder);
            identifier.addAll(nutritionOrder.identifier);
            instantiatesCanonical.addAll(nutritionOrder.instantiatesCanonical);
            instantiatesUri.addAll(nutritionOrder.instantiatesUri);
            instantiates.addAll(nutritionOrder.instantiates);
            basedOn.addAll(nutritionOrder.basedOn);
            groupIdentifier = nutritionOrder.groupIdentifier;
            status = nutritionOrder.status;
            intent = nutritionOrder.intent;
            priority = nutritionOrder.priority;
            subject = nutritionOrder.subject;
            encounter = nutritionOrder.encounter;
            supportingInformation.addAll(nutritionOrder.supportingInformation);
            dateTime = nutritionOrder.dateTime;
            orderer = nutritionOrder.orderer;
            performer.addAll(nutritionOrder.performer);
            allergyIntolerance.addAll(nutritionOrder.allergyIntolerance);
            foodPreferenceModifier.addAll(nutritionOrder.foodPreferenceModifier);
            excludeFoodModifier.addAll(nutritionOrder.excludeFoodModifier);
            outsideFoodAllowed = nutritionOrder.outsideFoodAllowed;
            oralDiet = nutritionOrder.oralDiet;
            supplement.addAll(nutritionOrder.supplement);
            enteralFormula = nutritionOrder.enteralFormula;
            note.addAll(nutritionOrder.note);
            return this;
        }
    }

    /**
     * Diet given orally in contrast to enteral (tube) feeding.
     */
    public static class OralDiet extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "OralDiet",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes used to indicate the type of diet being ordered for a patient.",
            valueSet = "http://hl7.org/fhir/ValueSet/diet-type"
        )
        private final List<CodeableConcept> type;
        private final Schedule schedule;
        private final List<Nutrient> nutrient;
        private final List<Texture> texture;
        @Binding(
            bindingName = "FluidConsistencyType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes used to represent the consistency of fluids and liquids provided to the patient.",
            valueSet = "http://hl7.org/fhir/ValueSet/consistency-type"
        )
        private final List<CodeableConcept> fluidConsistencyType;
        @Summary
        private final String instruction;

        private OralDiet(Builder builder) {
            super(builder);
            type = Collections.unmodifiableList(builder.type);
            schedule = builder.schedule;
            nutrient = Collections.unmodifiableList(builder.nutrient);
            texture = Collections.unmodifiableList(builder.texture);
            fluidConsistencyType = Collections.unmodifiableList(builder.fluidConsistencyType);
            instruction = builder.instruction;
        }

        /**
         * The kind of diet or dietary restriction such as fiber restricted diet or diabetic diet.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * Schedule information for an oral diet.
         * 
         * @return
         *     An immutable object of type {@link Schedule} that may be null.
         */
        public Schedule getSchedule() {
            return schedule;
        }

        /**
         * Class that defines the quantity and type of nutrient modifications (for example carbohydrate, fiber or sodium) 
         * required for the oral diet.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Nutrient} that may be empty.
         */
        public List<Nutrient> getNutrient() {
            return nutrient;
        }

        /**
         * Class that describes any texture modifications required for the patient to safely consume various types of solid foods.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Texture} that may be empty.
         */
        public List<Texture> getTexture() {
            return texture;
        }

        /**
         * The required consistency (e.g. honey-thick, nectar-thick, thin, thickened.) of liquids or fluids served to the patient.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getFluidConsistencyType() {
            return fluidConsistencyType;
        }

        /**
         * Free text or additional instructions or information pertaining to the oral diet.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getInstruction() {
            return instruction;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !type.isEmpty() || 
                (schedule != null) || 
                !nutrient.isEmpty() || 
                !texture.isEmpty() || 
                !fluidConsistencyType.isEmpty() || 
                (instruction != null);
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
                    accept(schedule, "schedule", visitor);
                    accept(nutrient, "nutrient", visitor, Nutrient.class);
                    accept(texture, "texture", visitor, Texture.class);
                    accept(fluidConsistencyType, "fluidConsistencyType", visitor, CodeableConcept.class);
                    accept(instruction, "instruction", visitor);
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
            OralDiet other = (OralDiet) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(schedule, other.schedule) && 
                Objects.equals(nutrient, other.nutrient) && 
                Objects.equals(texture, other.texture) && 
                Objects.equals(fluidConsistencyType, other.fluidConsistencyType) && 
                Objects.equals(instruction, other.instruction);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    schedule, 
                    nutrient, 
                    texture, 
                    fluidConsistencyType, 
                    instruction);
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
            private Schedule schedule;
            private List<Nutrient> nutrient = new ArrayList<>();
            private List<Texture> texture = new ArrayList<>();
            private List<CodeableConcept> fluidConsistencyType = new ArrayList<>();
            private String instruction;

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
             * The kind of diet or dietary restriction such as fiber restricted diet or diabetic diet.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Type of oral diet or diet restrictions that describe what can be consumed orally
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
             * The kind of diet or dietary restriction such as fiber restricted diet or diabetic diet.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Type of oral diet or diet restrictions that describe what can be consumed orally
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
             * Schedule information for an oral diet.
             * 
             * @param schedule
             *     Scheduling information for oral diets
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder schedule(Schedule schedule) {
                this.schedule = schedule;
                return this;
            }

            /**
             * Class that defines the quantity and type of nutrient modifications (for example carbohydrate, fiber or sodium) 
             * required for the oral diet.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param nutrient
             *     Required nutrient modifications
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder nutrient(Nutrient... nutrient) {
                for (Nutrient value : nutrient) {
                    this.nutrient.add(value);
                }
                return this;
            }

            /**
             * Class that defines the quantity and type of nutrient modifications (for example carbohydrate, fiber or sodium) 
             * required for the oral diet.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param nutrient
             *     Required nutrient modifications
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder nutrient(Collection<Nutrient> nutrient) {
                this.nutrient = new ArrayList<>(nutrient);
                return this;
            }

            /**
             * Class that describes any texture modifications required for the patient to safely consume various types of solid foods.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param texture
             *     Required texture modifications
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder texture(Texture... texture) {
                for (Texture value : texture) {
                    this.texture.add(value);
                }
                return this;
            }

            /**
             * Class that describes any texture modifications required for the patient to safely consume various types of solid foods.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param texture
             *     Required texture modifications
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder texture(Collection<Texture> texture) {
                this.texture = new ArrayList<>(texture);
                return this;
            }

            /**
             * The required consistency (e.g. honey-thick, nectar-thick, thin, thickened.) of liquids or fluids served to the patient.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param fluidConsistencyType
             *     The required consistency of fluids and liquids provided to the patient
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder fluidConsistencyType(CodeableConcept... fluidConsistencyType) {
                for (CodeableConcept value : fluidConsistencyType) {
                    this.fluidConsistencyType.add(value);
                }
                return this;
            }

            /**
             * The required consistency (e.g. honey-thick, nectar-thick, thin, thickened.) of liquids or fluids served to the patient.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param fluidConsistencyType
             *     The required consistency of fluids and liquids provided to the patient
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder fluidConsistencyType(Collection<CodeableConcept> fluidConsistencyType) {
                this.fluidConsistencyType = new ArrayList<>(fluidConsistencyType);
                return this;
            }

            /**
             * Convenience method for setting {@code instruction}.
             * 
             * @param instruction
             *     Instructions or additional information about the oral diet
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #instruction(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder instruction(java.lang.String instruction) {
                this.instruction = (instruction == null) ? null : String.of(instruction);
                return this;
            }

            /**
             * Free text or additional instructions or information pertaining to the oral diet.
             * 
             * @param instruction
             *     Instructions or additional information about the oral diet
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instruction(String instruction) {
                this.instruction = instruction;
                return this;
            }

            /**
             * Build the {@link OralDiet}
             * 
             * @return
             *     An immutable object of type {@link OralDiet}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid OralDiet per the base specification
             */
            @Override
            public OralDiet build() {
                OralDiet oralDiet = new OralDiet(this);
                if (validating) {
                    validate(oralDiet);
                }
                return oralDiet;
            }

            protected void validate(OralDiet oralDiet) {
                super.validate(oralDiet);
                ValidationSupport.checkList(oralDiet.type, "type", CodeableConcept.class);
                ValidationSupport.checkList(oralDiet.nutrient, "nutrient", Nutrient.class);
                ValidationSupport.checkList(oralDiet.texture, "texture", Texture.class);
                ValidationSupport.checkList(oralDiet.fluidConsistencyType, "fluidConsistencyType", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(oralDiet);
            }

            protected Builder from(OralDiet oralDiet) {
                super.from(oralDiet);
                type.addAll(oralDiet.type);
                schedule = oralDiet.schedule;
                nutrient.addAll(oralDiet.nutrient);
                texture.addAll(oralDiet.texture);
                fluidConsistencyType.addAll(oralDiet.fluidConsistencyType);
                instruction = oralDiet.instruction;
                return this;
            }
        }

        /**
         * Schedule information for an oral diet.
         */
        public static class Schedule extends BackboneElement {
            private final List<Timing> timing;
            private final Boolean asNeeded;
            @Binding(
                bindingName = "OralDietAsNeededReason",
                strength = BindingStrength.Value.EXAMPLE,
                description = "A coded concept identifying the precondition that should be met or evaluated prior to       consuming a nutrition product.",
                valueSet = "http://hl7.org/fhir/ValueSet/medication-as-needed-reason"
            )
            private final CodeableConcept asNeededFor;

            private Schedule(Builder builder) {
                super(builder);
                timing = Collections.unmodifiableList(builder.timing);
                asNeeded = builder.asNeeded;
                asNeededFor = builder.asNeededFor;
            }

            /**
             * The time period and frequency at which the diet should be given. The diet should be given for the combination of all 
             * schedules if more than one schedule is present.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Timing} that may be empty.
             */
            public List<Timing> getTiming() {
                return timing;
            }

            /**
             * Indicates whether the product is only taken when needed within a specific dosing schedule.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getAsNeeded() {
                return asNeeded;
            }

            /**
             * Indicates whether the product is only taken based on a precondition for taking the product.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getAsNeededFor() {
                return asNeededFor;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !timing.isEmpty() || 
                    (asNeeded != null) || 
                    (asNeededFor != null);
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
                        accept(timing, "timing", visitor, Timing.class);
                        accept(asNeeded, "asNeeded", visitor);
                        accept(asNeededFor, "asNeededFor", visitor);
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
                Schedule other = (Schedule) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(timing, other.timing) && 
                    Objects.equals(asNeeded, other.asNeeded) && 
                    Objects.equals(asNeededFor, other.asNeededFor);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        timing, 
                        asNeeded, 
                        asNeededFor);
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
                private List<Timing> timing = new ArrayList<>();
                private Boolean asNeeded;
                private CodeableConcept asNeededFor;

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
                 * The time period and frequency at which the diet should be given. The diet should be given for the combination of all 
                 * schedules if more than one schedule is present.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param timing
                 *     Scheduled frequency of diet
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder timing(Timing... timing) {
                    for (Timing value : timing) {
                        this.timing.add(value);
                    }
                    return this;
                }

                /**
                 * The time period and frequency at which the diet should be given. The diet should be given for the combination of all 
                 * schedules if more than one schedule is present.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param timing
                 *     Scheduled frequency of diet
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder timing(Collection<Timing> timing) {
                    this.timing = new ArrayList<>(timing);
                    return this;
                }

                /**
                 * Convenience method for setting {@code asNeeded}.
                 * 
                 * @param asNeeded
                 *     Take 'as needed'
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #asNeeded(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder asNeeded(java.lang.Boolean asNeeded) {
                    this.asNeeded = (asNeeded == null) ? null : Boolean.of(asNeeded);
                    return this;
                }

                /**
                 * Indicates whether the product is only taken when needed within a specific dosing schedule.
                 * 
                 * @param asNeeded
                 *     Take 'as needed'
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder asNeeded(Boolean asNeeded) {
                    this.asNeeded = asNeeded;
                    return this;
                }

                /**
                 * Indicates whether the product is only taken based on a precondition for taking the product.
                 * 
                 * @param asNeededFor
                 *     Take 'as needed' for x
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder asNeededFor(CodeableConcept asNeededFor) {
                    this.asNeededFor = asNeededFor;
                    return this;
                }

                /**
                 * Build the {@link Schedule}
                 * 
                 * @return
                 *     An immutable object of type {@link Schedule}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Schedule per the base specification
                 */
                @Override
                public Schedule build() {
                    Schedule schedule = new Schedule(this);
                    if (validating) {
                        validate(schedule);
                    }
                    return schedule;
                }

                protected void validate(Schedule schedule) {
                    super.validate(schedule);
                    ValidationSupport.checkList(schedule.timing, "timing", Timing.class);
                    ValidationSupport.requireValueOrChildren(schedule);
                }

                protected Builder from(Schedule schedule) {
                    super.from(schedule);
                    timing.addAll(schedule.timing);
                    asNeeded = schedule.asNeeded;
                    asNeededFor = schedule.asNeededFor;
                    return this;
                }
            }
        }

        /**
         * Class that defines the quantity and type of nutrient modifications (for example carbohydrate, fiber or sodium) 
         * required for the oral diet.
         */
        public static class Nutrient extends BackboneElement {
            @Binding(
                bindingName = "NutrientModifier",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Codes for types of nutrients that are being modified such as carbohydrate or sodium.",
                valueSet = "http://hl7.org/fhir/ValueSet/nutrient-code"
            )
            private final CodeableConcept modifier;
            private final SimpleQuantity amount;

            private Nutrient(Builder builder) {
                super(builder);
                modifier = builder.modifier;
                amount = builder.amount;
            }

            /**
             * The nutrient that is being modified such as carbohydrate or sodium.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getModifier() {
                return modifier;
            }

            /**
             * The quantity of the specified nutrient to include in diet.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} that may be null.
             */
            public SimpleQuantity getAmount() {
                return amount;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (modifier != null) || 
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
                        accept(modifier, "modifier", visitor);
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
                Nutrient other = (Nutrient) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(modifier, other.modifier) && 
                    Objects.equals(amount, other.amount);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        modifier, 
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
                private CodeableConcept modifier;
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
                 * The nutrient that is being modified such as carbohydrate or sodium.
                 * 
                 * @param modifier
                 *     Type of nutrient that is being modified
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder modifier(CodeableConcept modifier) {
                    this.modifier = modifier;
                    return this;
                }

                /**
                 * The quantity of the specified nutrient to include in diet.
                 * 
                 * @param amount
                 *     Quantity of the specified nutrient
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder amount(SimpleQuantity amount) {
                    this.amount = amount;
                    return this;
                }

                /**
                 * Build the {@link Nutrient}
                 * 
                 * @return
                 *     An immutable object of type {@link Nutrient}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Nutrient per the base specification
                 */
                @Override
                public Nutrient build() {
                    Nutrient nutrient = new Nutrient(this);
                    if (validating) {
                        validate(nutrient);
                    }
                    return nutrient;
                }

                protected void validate(Nutrient nutrient) {
                    super.validate(nutrient);
                    ValidationSupport.requireValueOrChildren(nutrient);
                }

                protected Builder from(Nutrient nutrient) {
                    super.from(nutrient);
                    modifier = nutrient.modifier;
                    amount = nutrient.amount;
                    return this;
                }
            }
        }

        /**
         * Class that describes any texture modifications required for the patient to safely consume various types of solid foods.
         */
        public static class Texture extends BackboneElement {
            @Binding(
                bindingName = "TextureModifier",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Codes for food consistency types or texture modifications to apply to foods.",
                valueSet = "http://hl7.org/fhir/ValueSet/texture-code"
            )
            private final CodeableConcept modifier;
            @Binding(
                bindingName = "TextureModifiedFoodType",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Codes for types of foods that are texture-modified.",
                valueSet = "http://hl7.org/fhir/ValueSet/modified-foodtype"
            )
            private final CodeableConcept foodType;

            private Texture(Builder builder) {
                super(builder);
                modifier = builder.modifier;
                foodType = builder.foodType;
            }

            /**
             * Any texture modifications (for solid foods) that should be made, e.g. easy to chew, chopped, ground, and pureed.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getModifier() {
                return modifier;
            }

            /**
             * The food type(s) (e.g. meats, all foods) that the texture modification applies to. This could be all foods types.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getFoodType() {
                return foodType;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (modifier != null) || 
                    (foodType != null);
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
                        accept(modifier, "modifier", visitor);
                        accept(foodType, "foodType", visitor);
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
                Texture other = (Texture) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(modifier, other.modifier) && 
                    Objects.equals(foodType, other.foodType);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        modifier, 
                        foodType);
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
                private CodeableConcept modifier;
                private CodeableConcept foodType;

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
                 * Any texture modifications (for solid foods) that should be made, e.g. easy to chew, chopped, ground, and pureed.
                 * 
                 * @param modifier
                 *     Code to indicate how to alter the texture of the foods, e.g. pureed
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder modifier(CodeableConcept modifier) {
                    this.modifier = modifier;
                    return this;
                }

                /**
                 * The food type(s) (e.g. meats, all foods) that the texture modification applies to. This could be all foods types.
                 * 
                 * @param foodType
                 *     Concepts that are used to identify an entity that is ingested for nutritional purposes
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder foodType(CodeableConcept foodType) {
                    this.foodType = foodType;
                    return this;
                }

                /**
                 * Build the {@link Texture}
                 * 
                 * @return
                 *     An immutable object of type {@link Texture}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Texture per the base specification
                 */
                @Override
                public Texture build() {
                    Texture texture = new Texture(this);
                    if (validating) {
                        validate(texture);
                    }
                    return texture;
                }

                protected void validate(Texture texture) {
                    super.validate(texture);
                    ValidationSupport.requireValueOrChildren(texture);
                }

                protected Builder from(Texture texture) {
                    super.from(texture);
                    modifier = texture.modifier;
                    foodType = texture.foodType;
                    return this;
                }
            }
        }
    }

    /**
     * Oral nutritional products given in order to add further nutritional value to the patient's diet.
     */
    public static class Supplement extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "SupplementType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes for nutritional supplements to be provided to the patient.",
            valueSet = "http://hl7.org/fhir/ValueSet/supplement-type"
        )
        private final CodeableReference type;
        private final String productName;
        private final Schedule schedule;
        private final SimpleQuantity quantity;
        @Summary
        private final String instruction;

        private Supplement(Builder builder) {
            super(builder);
            type = builder.type;
            productName = builder.productName;
            schedule = builder.schedule;
            quantity = builder.quantity;
            instruction = builder.instruction;
        }

        /**
         * The kind of nutritional supplement product required such as a high protein or pediatric clear liquid supplement.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getType() {
            return type;
        }

        /**
         * The product or brand name of the nutritional supplement such as "Acme Protein Shake".
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getProductName() {
            return productName;
        }

        /**
         * Schedule information for a supplement.
         * 
         * @return
         *     An immutable object of type {@link Schedule} that may be null.
         */
        public Schedule getSchedule() {
            return schedule;
        }

        /**
         * The amount of the nutritional supplement to be given.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getQuantity() {
            return quantity;
        }

        /**
         * Free text or additional instructions or information pertaining to the oral supplement.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getInstruction() {
            return instruction;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (productName != null) || 
                (schedule != null) || 
                (quantity != null) || 
                (instruction != null);
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
                    accept(productName, "productName", visitor);
                    accept(schedule, "schedule", visitor);
                    accept(quantity, "quantity", visitor);
                    accept(instruction, "instruction", visitor);
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
            Supplement other = (Supplement) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(productName, other.productName) && 
                Objects.equals(schedule, other.schedule) && 
                Objects.equals(quantity, other.quantity) && 
                Objects.equals(instruction, other.instruction);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    productName, 
                    schedule, 
                    quantity, 
                    instruction);
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
            private CodeableReference type;
            private String productName;
            private Schedule schedule;
            private SimpleQuantity quantity;
            private String instruction;

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
             * The kind of nutritional supplement product required such as a high protein or pediatric clear liquid supplement.
             * 
             * @param type
             *     Type of supplement product requested
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableReference type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code productName}.
             * 
             * @param productName
             *     Product or brand name of the nutritional supplement
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #productName(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder productName(java.lang.String productName) {
                this.productName = (productName == null) ? null : String.of(productName);
                return this;
            }

            /**
             * The product or brand name of the nutritional supplement such as "Acme Protein Shake".
             * 
             * @param productName
             *     Product or brand name of the nutritional supplement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder productName(String productName) {
                this.productName = productName;
                return this;
            }

            /**
             * Schedule information for a supplement.
             * 
             * @param schedule
             *     Scheduling information for supplements
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder schedule(Schedule schedule) {
                this.schedule = schedule;
                return this;
            }

            /**
             * The amount of the nutritional supplement to be given.
             * 
             * @param quantity
             *     Amount of the nutritional supplement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder quantity(SimpleQuantity quantity) {
                this.quantity = quantity;
                return this;
            }

            /**
             * Convenience method for setting {@code instruction}.
             * 
             * @param instruction
             *     Instructions or additional information about the oral supplement
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #instruction(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder instruction(java.lang.String instruction) {
                this.instruction = (instruction == null) ? null : String.of(instruction);
                return this;
            }

            /**
             * Free text or additional instructions or information pertaining to the oral supplement.
             * 
             * @param instruction
             *     Instructions or additional information about the oral supplement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instruction(String instruction) {
                this.instruction = instruction;
                return this;
            }

            /**
             * Build the {@link Supplement}
             * 
             * @return
             *     An immutable object of type {@link Supplement}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Supplement per the base specification
             */
            @Override
            public Supplement build() {
                Supplement supplement = new Supplement(this);
                if (validating) {
                    validate(supplement);
                }
                return supplement;
            }

            protected void validate(Supplement supplement) {
                super.validate(supplement);
                ValidationSupport.requireValueOrChildren(supplement);
            }

            protected Builder from(Supplement supplement) {
                super.from(supplement);
                type = supplement.type;
                productName = supplement.productName;
                schedule = supplement.schedule;
                quantity = supplement.quantity;
                instruction = supplement.instruction;
                return this;
            }
        }

        /**
         * Schedule information for a supplement.
         */
        public static class Schedule extends BackboneElement {
            private final List<Timing> timing;
            private final Boolean asNeeded;
            @Binding(
                bindingName = "SupplementAsNeededReason",
                strength = BindingStrength.Value.EXAMPLE,
                description = "A coded concept identifying the precondition that should be met or evaluated prior to       consuming a supplement.",
                valueSet = "http://hl7.org/fhir/ValueSet/medication-as-needed-reason"
            )
            private final CodeableConcept asNeededFor;

            private Schedule(Builder builder) {
                super(builder);
                timing = Collections.unmodifiableList(builder.timing);
                asNeeded = builder.asNeeded;
                asNeededFor = builder.asNeededFor;
            }

            /**
             * The time period and frequency at which the supplement should be given. The supplement should be given for the 
             * combination of all schedules if more than one schedule is present.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Timing} that may be empty.
             */
            public List<Timing> getTiming() {
                return timing;
            }

            /**
             * Indicates whether the supplement is only taken when needed within a specific dosing schedule.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getAsNeeded() {
                return asNeeded;
            }

            /**
             * Indicates whether the supplement is only taken based on a precondition for taking the supplement.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getAsNeededFor() {
                return asNeededFor;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !timing.isEmpty() || 
                    (asNeeded != null) || 
                    (asNeededFor != null);
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
                        accept(timing, "timing", visitor, Timing.class);
                        accept(asNeeded, "asNeeded", visitor);
                        accept(asNeededFor, "asNeededFor", visitor);
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
                Schedule other = (Schedule) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(timing, other.timing) && 
                    Objects.equals(asNeeded, other.asNeeded) && 
                    Objects.equals(asNeededFor, other.asNeededFor);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        timing, 
                        asNeeded, 
                        asNeededFor);
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
                private List<Timing> timing = new ArrayList<>();
                private Boolean asNeeded;
                private CodeableConcept asNeededFor;

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
                 * The time period and frequency at which the supplement should be given. The supplement should be given for the 
                 * combination of all schedules if more than one schedule is present.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param timing
                 *     Scheduled frequency of diet
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder timing(Timing... timing) {
                    for (Timing value : timing) {
                        this.timing.add(value);
                    }
                    return this;
                }

                /**
                 * The time period and frequency at which the supplement should be given. The supplement should be given for the 
                 * combination of all schedules if more than one schedule is present.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param timing
                 *     Scheduled frequency of diet
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder timing(Collection<Timing> timing) {
                    this.timing = new ArrayList<>(timing);
                    return this;
                }

                /**
                 * Convenience method for setting {@code asNeeded}.
                 * 
                 * @param asNeeded
                 *     Take 'as needed'
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #asNeeded(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder asNeeded(java.lang.Boolean asNeeded) {
                    this.asNeeded = (asNeeded == null) ? null : Boolean.of(asNeeded);
                    return this;
                }

                /**
                 * Indicates whether the supplement is only taken when needed within a specific dosing schedule.
                 * 
                 * @param asNeeded
                 *     Take 'as needed'
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder asNeeded(Boolean asNeeded) {
                    this.asNeeded = asNeeded;
                    return this;
                }

                /**
                 * Indicates whether the supplement is only taken based on a precondition for taking the supplement.
                 * 
                 * @param asNeededFor
                 *     Take 'as needed' for x
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder asNeededFor(CodeableConcept asNeededFor) {
                    this.asNeededFor = asNeededFor;
                    return this;
                }

                /**
                 * Build the {@link Schedule}
                 * 
                 * @return
                 *     An immutable object of type {@link Schedule}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Schedule per the base specification
                 */
                @Override
                public Schedule build() {
                    Schedule schedule = new Schedule(this);
                    if (validating) {
                        validate(schedule);
                    }
                    return schedule;
                }

                protected void validate(Schedule schedule) {
                    super.validate(schedule);
                    ValidationSupport.checkList(schedule.timing, "timing", Timing.class);
                    ValidationSupport.requireValueOrChildren(schedule);
                }

                protected Builder from(Schedule schedule) {
                    super.from(schedule);
                    timing.addAll(schedule.timing);
                    asNeeded = schedule.asNeeded;
                    asNeededFor = schedule.asNeededFor;
                    return this;
                }
            }
        }
    }

    /**
     * Feeding provided through the gastrointestinal tract via a tube, catheter, or stoma that delivers nutrition distal to 
     * the oral cavity.
     */
    public static class EnteralFormula extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "EnteralFormulaType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes for type of enteral formula to be administered to patient.",
            valueSet = "http://hl7.org/fhir/ValueSet/entformula-type"
        )
        private final CodeableReference baseFormulaType;
        private final String baseFormulaProductName;
        private final List<CodeableReference> deliveryDevice;
        private final List<Additive> additive;
        private final SimpleQuantity caloricDensity;
        @Binding(
            bindingName = "EnteralRouteOfAdministration",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Codes specifying the route of administration of enteral formula.",
            valueSet = "http://hl7.org/fhir/ValueSet/enteral-route"
        )
        private final CodeableConcept routeOfAdministration;
        private final List<Administration> administration;
        private final SimpleQuantity maxVolumeToDeliver;
        @Summary
        private final Markdown administrationInstruction;

        private EnteralFormula(Builder builder) {
            super(builder);
            baseFormulaType = builder.baseFormulaType;
            baseFormulaProductName = builder.baseFormulaProductName;
            deliveryDevice = Collections.unmodifiableList(builder.deliveryDevice);
            additive = Collections.unmodifiableList(builder.additive);
            caloricDensity = builder.caloricDensity;
            routeOfAdministration = builder.routeOfAdministration;
            administration = Collections.unmodifiableList(builder.administration);
            maxVolumeToDeliver = builder.maxVolumeToDeliver;
            administrationInstruction = builder.administrationInstruction;
        }

        /**
         * The type of enteral or infant formula such as an adult standard formula with fiber or a soy-based infant formula.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getBaseFormulaType() {
            return baseFormulaType;
        }

        /**
         * The product or brand name of the enteral or infant formula product such as "ACME Adult Standard Formula".
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getBaseFormulaProductName() {
            return baseFormulaProductName;
        }

        /**
         * The intended type of device that is to be used for the administration of the enteral formula.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getDeliveryDevice() {
            return deliveryDevice;
        }

        /**
         * Indicates modular components to be provided in addition or mixed with the base formula.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Additive} that may be empty.
         */
        public List<Additive> getAdditive() {
            return additive;
        }

        /**
         * The amount of energy (calories) that the formula should provide per specified volume, typically per mL or fluid oz. 
         * For example, an infant may require a formula that provides 24 calories per fluid ounce or an adult may require an 
         * enteral formula that provides 1.5 calorie/mL.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getCaloricDensity() {
            return caloricDensity;
        }

        /**
         * The route or physiological path of administration into the patient's gastrointestinal tract for purposes of providing 
         * the formula feeding, e.g. nasogastric tube.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRouteOfAdministration() {
            return routeOfAdministration;
        }

        /**
         * Formula administration instructions as structured data. This repeating structure allows for changing the 
         * administration rate or volume over time for both bolus and continuous feeding. An example of this would be an 
         * instruction to increase the rate of continuous feeding every 2 hours.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Administration} that may be empty.
         */
        public List<Administration> getAdministration() {
            return administration;
        }

        /**
         * The maximum total quantity of formula that may be administered to a subject over the period of time, e.g. 1440 mL over 
         * 24 hours.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getMaxVolumeToDeliver() {
            return maxVolumeToDeliver;
        }

        /**
         * Free text formula administration, feeding instructions or additional instructions or information.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getAdministrationInstruction() {
            return administrationInstruction;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (baseFormulaType != null) || 
                (baseFormulaProductName != null) || 
                !deliveryDevice.isEmpty() || 
                !additive.isEmpty() || 
                (caloricDensity != null) || 
                (routeOfAdministration != null) || 
                !administration.isEmpty() || 
                (maxVolumeToDeliver != null) || 
                (administrationInstruction != null);
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
                    accept(baseFormulaType, "baseFormulaType", visitor);
                    accept(baseFormulaProductName, "baseFormulaProductName", visitor);
                    accept(deliveryDevice, "deliveryDevice", visitor, CodeableReference.class);
                    accept(additive, "additive", visitor, Additive.class);
                    accept(caloricDensity, "caloricDensity", visitor);
                    accept(routeOfAdministration, "routeOfAdministration", visitor);
                    accept(administration, "administration", visitor, Administration.class);
                    accept(maxVolumeToDeliver, "maxVolumeToDeliver", visitor);
                    accept(administrationInstruction, "administrationInstruction", visitor);
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
            EnteralFormula other = (EnteralFormula) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(baseFormulaType, other.baseFormulaType) && 
                Objects.equals(baseFormulaProductName, other.baseFormulaProductName) && 
                Objects.equals(deliveryDevice, other.deliveryDevice) && 
                Objects.equals(additive, other.additive) && 
                Objects.equals(caloricDensity, other.caloricDensity) && 
                Objects.equals(routeOfAdministration, other.routeOfAdministration) && 
                Objects.equals(administration, other.administration) && 
                Objects.equals(maxVolumeToDeliver, other.maxVolumeToDeliver) && 
                Objects.equals(administrationInstruction, other.administrationInstruction);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    baseFormulaType, 
                    baseFormulaProductName, 
                    deliveryDevice, 
                    additive, 
                    caloricDensity, 
                    routeOfAdministration, 
                    administration, 
                    maxVolumeToDeliver, 
                    administrationInstruction);
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
            private CodeableReference baseFormulaType;
            private String baseFormulaProductName;
            private List<CodeableReference> deliveryDevice = new ArrayList<>();
            private List<Additive> additive = new ArrayList<>();
            private SimpleQuantity caloricDensity;
            private CodeableConcept routeOfAdministration;
            private List<Administration> administration = new ArrayList<>();
            private SimpleQuantity maxVolumeToDeliver;
            private Markdown administrationInstruction;

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
             * The type of enteral or infant formula such as an adult standard formula with fiber or a soy-based infant formula.
             * 
             * @param baseFormulaType
             *     Type of enteral or infant formula
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder baseFormulaType(CodeableReference baseFormulaType) {
                this.baseFormulaType = baseFormulaType;
                return this;
            }

            /**
             * Convenience method for setting {@code baseFormulaProductName}.
             * 
             * @param baseFormulaProductName
             *     Product or brand name of the enteral or infant formula
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #baseFormulaProductName(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder baseFormulaProductName(java.lang.String baseFormulaProductName) {
                this.baseFormulaProductName = (baseFormulaProductName == null) ? null : String.of(baseFormulaProductName);
                return this;
            }

            /**
             * The product or brand name of the enteral or infant formula product such as "ACME Adult Standard Formula".
             * 
             * @param baseFormulaProductName
             *     Product or brand name of the enteral or infant formula
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder baseFormulaProductName(String baseFormulaProductName) {
                this.baseFormulaProductName = baseFormulaProductName;
                return this;
            }

            /**
             * The intended type of device that is to be used for the administration of the enteral formula.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param deliveryDevice
             *     Intended type of device for the administration
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder deliveryDevice(CodeableReference... deliveryDevice) {
                for (CodeableReference value : deliveryDevice) {
                    this.deliveryDevice.add(value);
                }
                return this;
            }

            /**
             * The intended type of device that is to be used for the administration of the enteral formula.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param deliveryDevice
             *     Intended type of device for the administration
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder deliveryDevice(Collection<CodeableReference> deliveryDevice) {
                this.deliveryDevice = new ArrayList<>(deliveryDevice);
                return this;
            }

            /**
             * Indicates modular components to be provided in addition or mixed with the base formula.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param additive
             *     Components to add to the feeding
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder additive(Additive... additive) {
                for (Additive value : additive) {
                    this.additive.add(value);
                }
                return this;
            }

            /**
             * Indicates modular components to be provided in addition or mixed with the base formula.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param additive
             *     Components to add to the feeding
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder additive(Collection<Additive> additive) {
                this.additive = new ArrayList<>(additive);
                return this;
            }

            /**
             * The amount of energy (calories) that the formula should provide per specified volume, typically per mL or fluid oz. 
             * For example, an infant may require a formula that provides 24 calories per fluid ounce or an adult may require an 
             * enteral formula that provides 1.5 calorie/mL.
             * 
             * @param caloricDensity
             *     Amount of energy per specified volume that is required
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder caloricDensity(SimpleQuantity caloricDensity) {
                this.caloricDensity = caloricDensity;
                return this;
            }

            /**
             * The route or physiological path of administration into the patient's gastrointestinal tract for purposes of providing 
             * the formula feeding, e.g. nasogastric tube.
             * 
             * @param routeOfAdministration
             *     How the formula should enter the patient's gastrointestinal tract
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder routeOfAdministration(CodeableConcept routeOfAdministration) {
                this.routeOfAdministration = routeOfAdministration;
                return this;
            }

            /**
             * Formula administration instructions as structured data. This repeating structure allows for changing the 
             * administration rate or volume over time for both bolus and continuous feeding. An example of this would be an 
             * instruction to increase the rate of continuous feeding every 2 hours.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param administration
             *     Formula feeding instruction as structured data
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder administration(Administration... administration) {
                for (Administration value : administration) {
                    this.administration.add(value);
                }
                return this;
            }

            /**
             * Formula administration instructions as structured data. This repeating structure allows for changing the 
             * administration rate or volume over time for both bolus and continuous feeding. An example of this would be an 
             * instruction to increase the rate of continuous feeding every 2 hours.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param administration
             *     Formula feeding instruction as structured data
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder administration(Collection<Administration> administration) {
                this.administration = new ArrayList<>(administration);
                return this;
            }

            /**
             * The maximum total quantity of formula that may be administered to a subject over the period of time, e.g. 1440 mL over 
             * 24 hours.
             * 
             * @param maxVolumeToDeliver
             *     Upper limit on formula volume per unit of time
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder maxVolumeToDeliver(SimpleQuantity maxVolumeToDeliver) {
                this.maxVolumeToDeliver = maxVolumeToDeliver;
                return this;
            }

            /**
             * Free text formula administration, feeding instructions or additional instructions or information.
             * 
             * @param administrationInstruction
             *     Formula feeding instructions expressed as text
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder administrationInstruction(Markdown administrationInstruction) {
                this.administrationInstruction = administrationInstruction;
                return this;
            }

            /**
             * Build the {@link EnteralFormula}
             * 
             * @return
             *     An immutable object of type {@link EnteralFormula}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid EnteralFormula per the base specification
             */
            @Override
            public EnteralFormula build() {
                EnteralFormula enteralFormula = new EnteralFormula(this);
                if (validating) {
                    validate(enteralFormula);
                }
                return enteralFormula;
            }

            protected void validate(EnteralFormula enteralFormula) {
                super.validate(enteralFormula);
                ValidationSupport.checkList(enteralFormula.deliveryDevice, "deliveryDevice", CodeableReference.class);
                ValidationSupport.checkList(enteralFormula.additive, "additive", Additive.class);
                ValidationSupport.checkList(enteralFormula.administration, "administration", Administration.class);
                ValidationSupport.requireValueOrChildren(enteralFormula);
            }

            protected Builder from(EnteralFormula enteralFormula) {
                super.from(enteralFormula);
                baseFormulaType = enteralFormula.baseFormulaType;
                baseFormulaProductName = enteralFormula.baseFormulaProductName;
                deliveryDevice.addAll(enteralFormula.deliveryDevice);
                additive.addAll(enteralFormula.additive);
                caloricDensity = enteralFormula.caloricDensity;
                routeOfAdministration = enteralFormula.routeOfAdministration;
                administration.addAll(enteralFormula.administration);
                maxVolumeToDeliver = enteralFormula.maxVolumeToDeliver;
                administrationInstruction = enteralFormula.administrationInstruction;
                return this;
            }
        }

        /**
         * Indicates modular components to be provided in addition or mixed with the base formula.
         */
        public static class Additive extends BackboneElement {
            @Binding(
                bindingName = "EnteralFormulaAdditiveType",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Codes for the type of modular component such as protein, carbohydrate or fiber to be provided in addition to or mixed with the base formula.",
                valueSet = "http://hl7.org/fhir/ValueSet/entformula-additive"
            )
            private final CodeableReference type;
            private final String productName;
            private final SimpleQuantity quantity;

            private Additive(Builder builder) {
                super(builder);
                type = builder.type;
                productName = builder.productName;
                quantity = builder.quantity;
            }

            /**
             * Indicates the type of modular component such as protein, carbohydrate, fat or fiber to be provided in addition to or 
             * mixed with the base formula.
             * 
             * @return
             *     An immutable object of type {@link CodeableReference} that may be null.
             */
            public CodeableReference getType() {
                return type;
            }

            /**
             * The product or brand name of the type of modular component to be added to the formula.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getProductName() {
                return productName;
            }

            /**
             * The amount of additive to be given in addition or to be mixed in with the base formula.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} that may be null.
             */
            public SimpleQuantity getQuantity() {
                return quantity;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    (productName != null) || 
                    (quantity != null);
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
                        accept(productName, "productName", visitor);
                        accept(quantity, "quantity", visitor);
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
                Additive other = (Additive) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(productName, other.productName) && 
                    Objects.equals(quantity, other.quantity);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
                        productName, 
                        quantity);
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
                private CodeableReference type;
                private String productName;
                private SimpleQuantity quantity;

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
                 * Indicates the type of modular component such as protein, carbohydrate, fat or fiber to be provided in addition to or 
                 * mixed with the base formula.
                 * 
                 * @param type
                 *     Type of modular component to add to the feeding
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableReference type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Convenience method for setting {@code productName}.
                 * 
                 * @param productName
                 *     Product or brand name of the modular additive
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #productName(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder productName(java.lang.String productName) {
                    this.productName = (productName == null) ? null : String.of(productName);
                    return this;
                }

                /**
                 * The product or brand name of the type of modular component to be added to the formula.
                 * 
                 * @param productName
                 *     Product or brand name of the modular additive
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder productName(String productName) {
                    this.productName = productName;
                    return this;
                }

                /**
                 * The amount of additive to be given in addition or to be mixed in with the base formula.
                 * 
                 * @param quantity
                 *     Amount of additive to be given or mixed in
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(SimpleQuantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * Build the {@link Additive}
                 * 
                 * @return
                 *     An immutable object of type {@link Additive}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Additive per the base specification
                 */
                @Override
                public Additive build() {
                    Additive additive = new Additive(this);
                    if (validating) {
                        validate(additive);
                    }
                    return additive;
                }

                protected void validate(Additive additive) {
                    super.validate(additive);
                    ValidationSupport.requireValueOrChildren(additive);
                }

                protected Builder from(Additive additive) {
                    super.from(additive);
                    type = additive.type;
                    productName = additive.productName;
                    quantity = additive.quantity;
                    return this;
                }
            }
        }

        /**
         * Formula administration instructions as structured data. This repeating structure allows for changing the 
         * administration rate or volume over time for both bolus and continuous feeding. An example of this would be an 
         * instruction to increase the rate of continuous feeding every 2 hours.
         */
        public static class Administration extends BackboneElement {
            private final Schedule schedule;
            private final SimpleQuantity quantity;
            @Choice({ SimpleQuantity.class, Ratio.class })
            private final Element rate;

            private Administration(Builder builder) {
                super(builder);
                schedule = builder.schedule;
                quantity = builder.quantity;
                rate = builder.rate;
            }

            /**
             * Schedule information for an enteral formula.
             * 
             * @return
             *     An immutable object of type {@link Schedule} that may be null.
             */
            public Schedule getSchedule() {
                return schedule;
            }

            /**
             * The volume of formula to provide to the patient per the specified administration schedule.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} that may be null.
             */
            public SimpleQuantity getQuantity() {
                return quantity;
            }

            /**
             * The rate of administration of formula via a feeding pump, e.g. 60 mL per hour, according to the specified schedule.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} or {@link Ratio} that may be null.
             */
            public Element getRate() {
                return rate;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (schedule != null) || 
                    (quantity != null) || 
                    (rate != null);
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
                        accept(schedule, "schedule", visitor);
                        accept(quantity, "quantity", visitor);
                        accept(rate, "rate", visitor);
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
                Administration other = (Administration) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(schedule, other.schedule) && 
                    Objects.equals(quantity, other.quantity) && 
                    Objects.equals(rate, other.rate);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        schedule, 
                        quantity, 
                        rate);
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
                private Schedule schedule;
                private SimpleQuantity quantity;
                private Element rate;

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
                 * Schedule information for an enteral formula.
                 * 
                 * @param schedule
                 *     Scheduling information for enteral formula products
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder schedule(Schedule schedule) {
                    this.schedule = schedule;
                    return this;
                }

                /**
                 * The volume of formula to provide to the patient per the specified administration schedule.
                 * 
                 * @param quantity
                 *     The volume of formula to provide
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(SimpleQuantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * The rate of administration of formula via a feeding pump, e.g. 60 mL per hour, according to the specified schedule.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link SimpleQuantity}</li>
                 * <li>{@link Ratio}</li>
                 * </ul>
                 * 
                 * @param rate
                 *     Speed with which the formula is provided per period of time
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder rate(Element rate) {
                    this.rate = rate;
                    return this;
                }

                /**
                 * Build the {@link Administration}
                 * 
                 * @return
                 *     An immutable object of type {@link Administration}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Administration per the base specification
                 */
                @Override
                public Administration build() {
                    Administration administration = new Administration(this);
                    if (validating) {
                        validate(administration);
                    }
                    return administration;
                }

                protected void validate(Administration administration) {
                    super.validate(administration);
                    ValidationSupport.choiceElement(administration.rate, "rate", SimpleQuantity.class, Ratio.class);
                    ValidationSupport.requireValueOrChildren(administration);
                }

                protected Builder from(Administration administration) {
                    super.from(administration);
                    schedule = administration.schedule;
                    quantity = administration.quantity;
                    rate = administration.rate;
                    return this;
                }
            }

            /**
             * Schedule information for an enteral formula.
             */
            public static class Schedule extends BackboneElement {
                private final List<Timing> timing;
                private final Boolean asNeeded;
                @Binding(
                    bindingName = "EnteralFormulaAsNeededReason",
                    strength = BindingStrength.Value.EXAMPLE,
                    description = "A coded concept identifying the precondition that should be met or evaluated prior to       consuming an enteral formula.",
                    valueSet = "http://hl7.org/fhir/ValueSet/medication-as-needed-reason"
                )
                private final CodeableConcept asNeededFor;

                private Schedule(Builder builder) {
                    super(builder);
                    timing = Collections.unmodifiableList(builder.timing);
                    asNeeded = builder.asNeeded;
                    asNeededFor = builder.asNeededFor;
                }

                /**
                 * The time period and frequency at which the enteral formula should be given. The enteral formula should be given for 
                 * the combination of all schedules if more than one schedule is present.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Timing} that may be empty.
                 */
                public List<Timing> getTiming() {
                    return timing;
                }

                /**
                 * Indicates whether the enteral formula is only taken when needed within a specific dosing schedule.
                 * 
                 * @return
                 *     An immutable object of type {@link Boolean} that may be null.
                 */
                public Boolean getAsNeeded() {
                    return asNeeded;
                }

                /**
                 * Indicates whether the enteral formula is only taken based on a precondition for taking the enteral formula.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getAsNeededFor() {
                    return asNeededFor;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        !timing.isEmpty() || 
                        (asNeeded != null) || 
                        (asNeededFor != null);
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
                            accept(timing, "timing", visitor, Timing.class);
                            accept(asNeeded, "asNeeded", visitor);
                            accept(asNeededFor, "asNeededFor", visitor);
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
                    Schedule other = (Schedule) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(timing, other.timing) && 
                        Objects.equals(asNeeded, other.asNeeded) && 
                        Objects.equals(asNeededFor, other.asNeededFor);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            timing, 
                            asNeeded, 
                            asNeededFor);
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
                    private List<Timing> timing = new ArrayList<>();
                    private Boolean asNeeded;
                    private CodeableConcept asNeededFor;

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
                     * The time period and frequency at which the enteral formula should be given. The enteral formula should be given for 
                     * the combination of all schedules if more than one schedule is present.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param timing
                     *     Scheduled frequency of enteral formula
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder timing(Timing... timing) {
                        for (Timing value : timing) {
                            this.timing.add(value);
                        }
                        return this;
                    }

                    /**
                     * The time period and frequency at which the enteral formula should be given. The enteral formula should be given for 
                     * the combination of all schedules if more than one schedule is present.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param timing
                     *     Scheduled frequency of enteral formula
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder timing(Collection<Timing> timing) {
                        this.timing = new ArrayList<>(timing);
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code asNeeded}.
                     * 
                     * @param asNeeded
                     *     Take 'as needed'
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #asNeeded(org.linuxforhealth.fhir.model.type.Boolean)
                     */
                    public Builder asNeeded(java.lang.Boolean asNeeded) {
                        this.asNeeded = (asNeeded == null) ? null : Boolean.of(asNeeded);
                        return this;
                    }

                    /**
                     * Indicates whether the enteral formula is only taken when needed within a specific dosing schedule.
                     * 
                     * @param asNeeded
                     *     Take 'as needed'
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder asNeeded(Boolean asNeeded) {
                        this.asNeeded = asNeeded;
                        return this;
                    }

                    /**
                     * Indicates whether the enteral formula is only taken based on a precondition for taking the enteral formula.
                     * 
                     * @param asNeededFor
                     *     Take 'as needed' for x
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder asNeededFor(CodeableConcept asNeededFor) {
                        this.asNeededFor = asNeededFor;
                        return this;
                    }

                    /**
                     * Build the {@link Schedule}
                     * 
                     * @return
                     *     An immutable object of type {@link Schedule}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Schedule per the base specification
                     */
                    @Override
                    public Schedule build() {
                        Schedule schedule = new Schedule(this);
                        if (validating) {
                            validate(schedule);
                        }
                        return schedule;
                    }

                    protected void validate(Schedule schedule) {
                        super.validate(schedule);
                        ValidationSupport.checkList(schedule.timing, "timing", Timing.class);
                        ValidationSupport.requireValueOrChildren(schedule);
                    }

                    protected Builder from(Schedule schedule) {
                        super.from(schedule);
                        timing.addAll(schedule.timing);
                        asNeeded = schedule.asNeeded;
                        asNeededFor = schedule.asNeededFor;
                        return this;
                    }
                }
            }
        }
    }
}
