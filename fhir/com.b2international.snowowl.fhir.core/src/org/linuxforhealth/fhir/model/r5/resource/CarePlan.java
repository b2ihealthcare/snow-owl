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
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.CarePlanIntent;
import org.linuxforhealth.fhir.model.r5.type.code.CarePlanStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Describes the intention of how one or more practitioners intend to deliver care for a particular patient, group or 
 * community for a period of time, possibly limited to care for a specific condition or set of conditions.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class CarePlan extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final List<Canonical> instantiatesCanonical;
    @Summary
    private final List<Uri> instantiatesUri;
    @Summary
    @ReferenceTarget({ "CarePlan", "ServiceRequest", "RequestOrchestration", "NutritionOrder" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "CarePlan" })
    private final List<Reference> replaces;
    @Summary
    @ReferenceTarget({ "CarePlan" })
    private final List<Reference> partOf;
    @Summary
    @Binding(
        bindingName = "CarePlanStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Indicates whether the plan is currently being acted upon, represents future intentions or is now a historical record.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-status|5.0.0"
    )
    @Required
    private final CarePlanStatus status;
    @Summary
    @Binding(
        bindingName = "CarePlanIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes indicating the degree of authority/intentionality associated with a care plan.",
        valueSet = "http://hl7.org/fhir/ValueSet/care-plan-intent|5.0.0"
    )
    @Required
    private final CarePlanIntent intent;
    @Summary
    @Binding(
        bindingName = "CarePlanCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Identifies what \"kind\" of plan this is to support differentiation between multiple co-existing plans; e.g. \"Home health\", \"psychiatric\", \"asthma\", \"disease management\", etc.",
        valueSet = "http://hl7.org/fhir/ValueSet/care-plan-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    private final String title;
    @Summary
    private final String description;
    @Summary
    @ReferenceTarget({ "Patient", "Group" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    private final Period period;
    @Summary
    private final DateTime created;
    @Summary
    @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "Device", "RelatedPerson", "Organization", "CareTeam" })
    private final Reference custodian;
    @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "Device", "RelatedPerson", "Organization", "CareTeam" })
    private final List<Reference> contributor;
    @ReferenceTarget({ "CareTeam" })
    private final List<Reference> careTeam;
    @Summary
    @Binding(
        bindingName = "CarePlanAddresses",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes that describe the health issues this plan addresses.",
        valueSet = "http://hl7.org/fhir/ValueSet/clinical-findings"
    )
    private final List<CodeableReference> addresses;
    private final List<Reference> supportingInfo;
    @ReferenceTarget({ "Goal" })
    private final List<Reference> goal;
    private final List<Activity> activity;
    private final List<Annotation> note;

    private CarePlan(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = Collections.unmodifiableList(builder.instantiatesCanonical);
        instantiatesUri = Collections.unmodifiableList(builder.instantiatesUri);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        replaces = Collections.unmodifiableList(builder.replaces);
        partOf = Collections.unmodifiableList(builder.partOf);
        status = builder.status;
        intent = builder.intent;
        category = Collections.unmodifiableList(builder.category);
        title = builder.title;
        description = builder.description;
        subject = builder.subject;
        encounter = builder.encounter;
        period = builder.period;
        created = builder.created;
        custodian = builder.custodian;
        contributor = Collections.unmodifiableList(builder.contributor);
        careTeam = Collections.unmodifiableList(builder.careTeam);
        addresses = Collections.unmodifiableList(builder.addresses);
        supportingInfo = Collections.unmodifiableList(builder.supportingInfo);
        goal = Collections.unmodifiableList(builder.goal);
        activity = Collections.unmodifiableList(builder.activity);
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * Business identifiers assigned to this care plan by the performer or other systems which remain constant as the 
     * resource is updated and propagates from server to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The URL pointing to a FHIR-defined protocol, guideline, questionnaire or other definition that is adhered to in whole 
     * or in part by this CarePlan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * The URL pointing to an externally maintained protocol, guideline, questionnaire or other definition that is adhered to 
     * in whole or in part by this CarePlan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * A higher-level request resource (i.e. a plan, proposal or order) that is fulfilled in whole or in part by this care 
     * plan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * Completed or terminated care plan whose function is taken by this new care plan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getReplaces() {
        return replaces;
    }

    /**
     * A larger care plan of which this particular care plan is a component or step.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * Indicates whether the plan is currently being acted upon, represents future intentions or is now a historical record.
     * 
     * @return
     *     An immutable object of type {@link CarePlanStatus} that is non-null.
     */
    public CarePlanStatus getStatus() {
        return status;
    }

    /**
     * Indicates the level of authority/intentionality associated with the care plan and where the care plan fits into the 
     * workflow chain.
     * 
     * @return
     *     An immutable object of type {@link CarePlanIntent} that is non-null.
     */
    public CarePlanIntent getIntent() {
        return intent;
    }

    /**
     * Identifies what "kind" of plan this is to support differentiation between multiple co-existing plans; e.g. "Home 
     * health", "psychiatric", "asthma", "disease management", "wellness plan", etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Human-friendly name for the care plan.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * A description of the scope and nature of the plan.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Identifies the patient or group whose intended care is described by the plan.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The Encounter during which this CarePlan was created or to which the creation of this record is tightly associated.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Indicates when the plan did (or is intended to) come into effect and end.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Represents when this particular CarePlan record was created in the system, which is often a system-generated date.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getCreated() {
        return created;
    }

    /**
     * When populated, the custodian is responsible for the care plan. The care plan is attributed to the custodian.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getCustodian() {
        return custodian;
    }

    /**
     * Identifies the individual(s), organization or device who provided the contents of the care plan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getContributor() {
        return contributor;
    }

    /**
     * Identifies all people and organizations who are expected to be involved in the care envisioned by this plan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getCareTeam() {
        return careTeam;
    }

    /**
     * Identifies the conditions/problems/concerns/diagnoses/etc. whose management and/or mitigation are handled by this plan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getAddresses() {
        return addresses;
    }

    /**
     * Identifies portions of the patient's record that specifically influenced the formation of the plan. These might 
     * include comorbidities, recent procedures, limitations, recent assessments, etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInfo() {
        return supportingInfo;
    }

    /**
     * Describes the intended objective(s) of carrying out the care plan.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getGoal() {
        return goal;
    }

    /**
     * Identifies an action that has occurred or is a planned action to occur as part of the plan. For example, a medication 
     * to be used, lab tests to perform, self-monitoring that has occurred, education etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Activity} that may be empty.
     */
    public List<Activity> getActivity() {
        return activity;
    }

    /**
     * General notes about the care plan not covered elsewhere.
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
            !replaces.isEmpty() || 
            !partOf.isEmpty() || 
            (status != null) || 
            (intent != null) || 
            !category.isEmpty() || 
            (title != null) || 
            (description != null) || 
            (subject != null) || 
            (encounter != null) || 
            (period != null) || 
            (created != null) || 
            (custodian != null) || 
            !contributor.isEmpty() || 
            !careTeam.isEmpty() || 
            !addresses.isEmpty() || 
            !supportingInfo.isEmpty() || 
            !goal.isEmpty() || 
            !activity.isEmpty() || 
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
                accept(replaces, "replaces", visitor, Reference.class);
                accept(partOf, "partOf", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(intent, "intent", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(title, "title", visitor);
                accept(description, "description", visitor);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(period, "period", visitor);
                accept(created, "created", visitor);
                accept(custodian, "custodian", visitor);
                accept(contributor, "contributor", visitor, Reference.class);
                accept(careTeam, "careTeam", visitor, Reference.class);
                accept(addresses, "addresses", visitor, CodeableReference.class);
                accept(supportingInfo, "supportingInfo", visitor, Reference.class);
                accept(goal, "goal", visitor, Reference.class);
                accept(activity, "activity", visitor, Activity.class);
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
        CarePlan other = (CarePlan) obj;
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
            Objects.equals(replaces, other.replaces) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(status, other.status) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(category, other.category) && 
            Objects.equals(title, other.title) && 
            Objects.equals(description, other.description) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(period, other.period) && 
            Objects.equals(created, other.created) && 
            Objects.equals(custodian, other.custodian) && 
            Objects.equals(contributor, other.contributor) && 
            Objects.equals(careTeam, other.careTeam) && 
            Objects.equals(addresses, other.addresses) && 
            Objects.equals(supportingInfo, other.supportingInfo) && 
            Objects.equals(goal, other.goal) && 
            Objects.equals(activity, other.activity) && 
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
                replaces, 
                partOf, 
                status, 
                intent, 
                category, 
                title, 
                description, 
                subject, 
                encounter, 
                period, 
                created, 
                custodian, 
                contributor, 
                careTeam, 
                addresses, 
                supportingInfo, 
                goal, 
                activity, 
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
        private List<Reference> replaces = new ArrayList<>();
        private List<Reference> partOf = new ArrayList<>();
        private CarePlanStatus status;
        private CarePlanIntent intent;
        private List<CodeableConcept> category = new ArrayList<>();
        private String title;
        private String description;
        private Reference subject;
        private Reference encounter;
        private Period period;
        private DateTime created;
        private Reference custodian;
        private List<Reference> contributor = new ArrayList<>();
        private List<Reference> careTeam = new ArrayList<>();
        private List<CodeableReference> addresses = new ArrayList<>();
        private List<Reference> supportingInfo = new ArrayList<>();
        private List<Reference> goal = new ArrayList<>();
        private List<Activity> activity = new ArrayList<>();
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
         * Business identifiers assigned to this care plan by the performer or other systems which remain constant as the 
         * resource is updated and propagates from server to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this plan
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
         * Business identifiers assigned to this care plan by the performer or other systems which remain constant as the 
         * resource is updated and propagates from server to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Ids for this plan
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
         * The URL pointing to a FHIR-defined protocol, guideline, questionnaire or other definition that is adhered to in whole 
         * or in part by this CarePlan.
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
         * The URL pointing to a FHIR-defined protocol, guideline, questionnaire or other definition that is adhered to in whole 
         * or in part by this CarePlan.
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
         * The URL pointing to an externally maintained protocol, guideline, questionnaire or other definition that is adhered to 
         * in whole or in part by this CarePlan.
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
         * The URL pointing to an externally maintained protocol, guideline, questionnaire or other definition that is adhered to 
         * in whole or in part by this CarePlan.
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
         * A higher-level request resource (i.e. a plan, proposal or order) that is fulfilled in whole or in part by this care 
         * plan.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link RequestOrchestration}</li>
         * <li>{@link NutritionOrder}</li>
         * </ul>
         * 
         * @param basedOn
         *     Fulfills plan, proposal or order
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
         * A higher-level request resource (i.e. a plan, proposal or order) that is fulfilled in whole or in part by this care 
         * plan.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link RequestOrchestration}</li>
         * <li>{@link NutritionOrder}</li>
         * </ul>
         * 
         * @param basedOn
         *     Fulfills plan, proposal or order
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
         * Completed or terminated care plan whose function is taken by this new care plan.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * </ul>
         * 
         * @param replaces
         *     CarePlan replaced by this CarePlan
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder replaces(Reference... replaces) {
            for (Reference value : replaces) {
                this.replaces.add(value);
            }
            return this;
        }

        /**
         * Completed or terminated care plan whose function is taken by this new care plan.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * </ul>
         * 
         * @param replaces
         *     CarePlan replaced by this CarePlan
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder replaces(Collection<Reference> replaces) {
            this.replaces = new ArrayList<>(replaces);
            return this;
        }

        /**
         * A larger care plan of which this particular care plan is a component or step.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced CarePlan
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
         * A larger care plan of which this particular care plan is a component or step.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced CarePlan
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
         * Indicates whether the plan is currently being acted upon, represents future intentions or is now a historical record.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | on-hold | revoked | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(CarePlanStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Indicates the level of authority/intentionality associated with the care plan and where the care plan fits into the 
         * workflow chain.
         * 
         * <p>This element is required.
         * 
         * @param intent
         *     proposal | plan | order | option | directive
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(CarePlanIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * Identifies what "kind" of plan this is to support differentiation between multiple co-existing plans; e.g. "Home 
         * health", "psychiatric", "asthma", "disease management", "wellness plan", etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of plan
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
         * Identifies what "kind" of plan this is to support differentiation between multiple co-existing plans; e.g. "Home 
         * health", "psychiatric", "asthma", "disease management", "wellness plan", etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Type of plan
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
         * Convenience method for setting {@code title}.
         * 
         * @param title
         *     Human-friendly name for the care plan
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
         * Human-friendly name for the care plan.
         * 
         * @param title
         *     Human-friendly name for the care plan
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Convenience method for setting {@code description}.
         * 
         * @param description
         *     Summary of nature of plan
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
         * A description of the scope and nature of the plan.
         * 
         * @param description
         *     Summary of nature of plan
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Identifies the patient or group whose intended care is described by the plan.
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
         *     Who the care plan is for
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The Encounter during which this CarePlan was created or to which the creation of this record is tightly associated.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     The Encounter during which this CarePlan was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Indicates when the plan did (or is intended to) come into effect and end.
         * 
         * @param period
         *     Time period plan covers
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * Represents when this particular CarePlan record was created in the system, which is often a system-generated date.
         * 
         * @param created
         *     Date record was first recorded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder created(DateTime created) {
            this.created = created;
            return this;
        }

        /**
         * When populated, the custodian is responsible for the care plan. The care plan is attributed to the custodian.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Device}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param custodian
         *     Who is the designated responsible party
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder custodian(Reference custodian) {
            this.custodian = custodian;
            return this;
        }

        /**
         * Identifies the individual(s), organization or device who provided the contents of the care plan.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Device}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param contributor
         *     Who provided the content of the care plan
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contributor(Reference... contributor) {
            for (Reference value : contributor) {
                this.contributor.add(value);
            }
            return this;
        }

        /**
         * Identifies the individual(s), organization or device who provided the contents of the care plan.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Device}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param contributor
         *     Who provided the content of the care plan
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contributor(Collection<Reference> contributor) {
            this.contributor = new ArrayList<>(contributor);
            return this;
        }

        /**
         * Identifies all people and organizations who are expected to be involved in the care envisioned by this plan.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param careTeam
         *     Who's involved in plan?
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder careTeam(Reference... careTeam) {
            for (Reference value : careTeam) {
                this.careTeam.add(value);
            }
            return this;
        }

        /**
         * Identifies all people and organizations who are expected to be involved in the care envisioned by this plan.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param careTeam
         *     Who's involved in plan?
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder careTeam(Collection<Reference> careTeam) {
            this.careTeam = new ArrayList<>(careTeam);
            return this;
        }

        /**
         * Identifies the conditions/problems/concerns/diagnoses/etc. whose management and/or mitigation are handled by this plan.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param addresses
         *     Health issues this plan addresses
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder addresses(CodeableReference... addresses) {
            for (CodeableReference value : addresses) {
                this.addresses.add(value);
            }
            return this;
        }

        /**
         * Identifies the conditions/problems/concerns/diagnoses/etc. whose management and/or mitigation are handled by this plan.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param addresses
         *     Health issues this plan addresses
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder addresses(Collection<CodeableReference> addresses) {
            this.addresses = new ArrayList<>(addresses);
            return this;
        }

        /**
         * Identifies portions of the patient's record that specifically influenced the formation of the plan. These might 
         * include comorbidities, recent procedures, limitations, recent assessments, etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Information considered as part of plan
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supportingInfo(Reference... supportingInfo) {
            for (Reference value : supportingInfo) {
                this.supportingInfo.add(value);
            }
            return this;
        }

        /**
         * Identifies portions of the patient's record that specifically influenced the formation of the plan. These might 
         * include comorbidities, recent procedures, limitations, recent assessments, etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Information considered as part of plan
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supportingInfo(Collection<Reference> supportingInfo) {
            this.supportingInfo = new ArrayList<>(supportingInfo);
            return this;
        }

        /**
         * Describes the intended objective(s) of carrying out the care plan.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Goal}</li>
         * </ul>
         * 
         * @param goal
         *     Desired outcome of plan
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder goal(Reference... goal) {
            for (Reference value : goal) {
                this.goal.add(value);
            }
            return this;
        }

        /**
         * Describes the intended objective(s) of carrying out the care plan.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Goal}</li>
         * </ul>
         * 
         * @param goal
         *     Desired outcome of plan
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder goal(Collection<Reference> goal) {
            this.goal = new ArrayList<>(goal);
            return this;
        }

        /**
         * Identifies an action that has occurred or is a planned action to occur as part of the plan. For example, a medication 
         * to be used, lab tests to perform, self-monitoring that has occurred, education etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param activity
         *     Action to occur or has occurred as part of plan
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder activity(Activity... activity) {
            for (Activity value : activity) {
                this.activity.add(value);
            }
            return this;
        }

        /**
         * Identifies an action that has occurred or is a planned action to occur as part of the plan. For example, a medication 
         * to be used, lab tests to perform, self-monitoring that has occurred, education etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param activity
         *     Action to occur or has occurred as part of plan
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder activity(Collection<Activity> activity) {
            this.activity = new ArrayList<>(activity);
            return this;
        }

        /**
         * General notes about the care plan not covered elsewhere.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments about the plan
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
         * General notes about the care plan not covered elsewhere.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments about the plan
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
         * Build the {@link CarePlan}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>intent</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link CarePlan}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid CarePlan per the base specification
         */
        @Override
        public CarePlan build() {
            CarePlan carePlan = new CarePlan(this);
            if (validating) {
                validate(carePlan);
            }
            return carePlan;
        }

        protected void validate(CarePlan carePlan) {
            super.validate(carePlan);
            ValidationSupport.checkList(carePlan.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(carePlan.instantiatesCanonical, "instantiatesCanonical", Canonical.class);
            ValidationSupport.checkList(carePlan.instantiatesUri, "instantiatesUri", Uri.class);
            ValidationSupport.checkList(carePlan.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(carePlan.replaces, "replaces", Reference.class);
            ValidationSupport.checkList(carePlan.partOf, "partOf", Reference.class);
            ValidationSupport.requireNonNull(carePlan.status, "status");
            ValidationSupport.requireNonNull(carePlan.intent, "intent");
            ValidationSupport.checkList(carePlan.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(carePlan.subject, "subject");
            ValidationSupport.checkList(carePlan.contributor, "contributor", Reference.class);
            ValidationSupport.checkList(carePlan.careTeam, "careTeam", Reference.class);
            ValidationSupport.checkList(carePlan.addresses, "addresses", CodeableReference.class);
            ValidationSupport.checkList(carePlan.supportingInfo, "supportingInfo", Reference.class);
            ValidationSupport.checkList(carePlan.goal, "goal", Reference.class);
            ValidationSupport.checkList(carePlan.activity, "activity", Activity.class);
            ValidationSupport.checkList(carePlan.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(carePlan.basedOn, "basedOn", "CarePlan", "ServiceRequest", "RequestOrchestration", "NutritionOrder");
            ValidationSupport.checkReferenceType(carePlan.replaces, "replaces", "CarePlan");
            ValidationSupport.checkReferenceType(carePlan.partOf, "partOf", "CarePlan");
            ValidationSupport.checkReferenceType(carePlan.subject, "subject", "Patient", "Group");
            ValidationSupport.checkReferenceType(carePlan.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(carePlan.custodian, "custodian", "Patient", "Practitioner", "PractitionerRole", "Device", "RelatedPerson", "Organization", "CareTeam");
            ValidationSupport.checkReferenceType(carePlan.contributor, "contributor", "Patient", "Practitioner", "PractitionerRole", "Device", "RelatedPerson", "Organization", "CareTeam");
            ValidationSupport.checkReferenceType(carePlan.careTeam, "careTeam", "CareTeam");
            ValidationSupport.checkReferenceType(carePlan.goal, "goal", "Goal");
        }

        protected Builder from(CarePlan carePlan) {
            super.from(carePlan);
            identifier.addAll(carePlan.identifier);
            instantiatesCanonical.addAll(carePlan.instantiatesCanonical);
            instantiatesUri.addAll(carePlan.instantiatesUri);
            basedOn.addAll(carePlan.basedOn);
            replaces.addAll(carePlan.replaces);
            partOf.addAll(carePlan.partOf);
            status = carePlan.status;
            intent = carePlan.intent;
            category.addAll(carePlan.category);
            title = carePlan.title;
            description = carePlan.description;
            subject = carePlan.subject;
            encounter = carePlan.encounter;
            period = carePlan.period;
            created = carePlan.created;
            custodian = carePlan.custodian;
            contributor.addAll(carePlan.contributor);
            careTeam.addAll(carePlan.careTeam);
            addresses.addAll(carePlan.addresses);
            supportingInfo.addAll(carePlan.supportingInfo);
            goal.addAll(carePlan.goal);
            activity.addAll(carePlan.activity);
            note.addAll(carePlan.note);
            return this;
        }
    }

    /**
     * Identifies an action that has occurred or is a planned action to occur as part of the plan. For example, a medication 
     * to be used, lab tests to perform, self-monitoring that has occurred, education etc.
     */
    public static class Activity extends BackboneElement {
        @Binding(
            bindingName = "CarePlanActivityPerformed",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Identifies the results of the activity.",
            valueSet = "http://hl7.org/fhir/ValueSet/care-plan-activity-performed"
        )
        private final List<CodeableReference> performedActivity;
        private final List<Annotation> progress;
        @ReferenceTarget({ "Appointment", "CommunicationRequest", "DeviceRequest", "MedicationRequest", "NutritionOrder", "Task", "ServiceRequest", "VisionPrescription", "RequestOrchestration", "ImmunizationRecommendation", "SupplyRequest" })
        private final Reference plannedActivityReference;

        private Activity(Builder builder) {
            super(builder);
            performedActivity = Collections.unmodifiableList(builder.performedActivity);
            progress = Collections.unmodifiableList(builder.progress);
            plannedActivityReference = builder.plannedActivityReference;
        }

        /**
         * Identifies the activity that was performed. For example, an activity could be patient education, exercise, or a 
         * medication administration. The reference to an "event" resource, such as Procedure or Encounter or Observation, 
         * represents the activity that was performed. The requested activity can be conveyed using the CarePlan.activity.
         * plannedActivityReference (a reference to a “request�? resource).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getPerformedActivity() {
            return performedActivity;
        }

        /**
         * Notes about the adherence/status/progress of the activity.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
         */
        public List<Annotation> getProgress() {
            return progress;
        }

        /**
         * The details of the proposed activity represented in a specific resource.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getPlannedActivityReference() {
            return plannedActivityReference;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !performedActivity.isEmpty() || 
                !progress.isEmpty() || 
                (plannedActivityReference != null);
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
                    accept(performedActivity, "performedActivity", visitor, CodeableReference.class);
                    accept(progress, "progress", visitor, Annotation.class);
                    accept(plannedActivityReference, "plannedActivityReference", visitor);
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
            Activity other = (Activity) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(performedActivity, other.performedActivity) && 
                Objects.equals(progress, other.progress) && 
                Objects.equals(plannedActivityReference, other.plannedActivityReference);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    performedActivity, 
                    progress, 
                    plannedActivityReference);
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
            private List<CodeableReference> performedActivity = new ArrayList<>();
            private List<Annotation> progress = new ArrayList<>();
            private Reference plannedActivityReference;

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
             * Identifies the activity that was performed. For example, an activity could be patient education, exercise, or a 
             * medication administration. The reference to an "event" resource, such as Procedure or Encounter or Observation, 
             * represents the activity that was performed. The requested activity can be conveyed using the CarePlan.activity.
             * plannedActivityReference (a reference to a “request�? resource).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param performedActivity
             *     Results of the activity (concept, or Appointment, Encounter, Procedure, etc.)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder performedActivity(CodeableReference... performedActivity) {
                for (CodeableReference value : performedActivity) {
                    this.performedActivity.add(value);
                }
                return this;
            }

            /**
             * Identifies the activity that was performed. For example, an activity could be patient education, exercise, or a 
             * medication administration. The reference to an "event" resource, such as Procedure or Encounter or Observation, 
             * represents the activity that was performed. The requested activity can be conveyed using the CarePlan.activity.
             * plannedActivityReference (a reference to a “request�? resource).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param performedActivity
             *     Results of the activity (concept, or Appointment, Encounter, Procedure, etc.)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder performedActivity(Collection<CodeableReference> performedActivity) {
                this.performedActivity = new ArrayList<>(performedActivity);
                return this;
            }

            /**
             * Notes about the adherence/status/progress of the activity.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param progress
             *     Comments about the activity status/progress
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder progress(Annotation... progress) {
                for (Annotation value : progress) {
                    this.progress.add(value);
                }
                return this;
            }

            /**
             * Notes about the adherence/status/progress of the activity.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param progress
             *     Comments about the activity status/progress
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder progress(Collection<Annotation> progress) {
                this.progress = new ArrayList<>(progress);
                return this;
            }

            /**
             * The details of the proposed activity represented in a specific resource.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Appointment}</li>
             * <li>{@link CommunicationRequest}</li>
             * <li>{@link DeviceRequest}</li>
             * <li>{@link MedicationRequest}</li>
             * <li>{@link NutritionOrder}</li>
             * <li>{@link Task}</li>
             * <li>{@link ServiceRequest}</li>
             * <li>{@link VisionPrescription}</li>
             * <li>{@link RequestOrchestration}</li>
             * <li>{@link ImmunizationRecommendation}</li>
             * <li>{@link SupplyRequest}</li>
             * </ul>
             * 
             * @param plannedActivityReference
             *     Activity that is intended to be part of the care plan
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder plannedActivityReference(Reference plannedActivityReference) {
                this.plannedActivityReference = plannedActivityReference;
                return this;
            }

            /**
             * Build the {@link Activity}
             * 
             * @return
             *     An immutable object of type {@link Activity}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Activity per the base specification
             */
            @Override
            public Activity build() {
                Activity activity = new Activity(this);
                if (validating) {
                    validate(activity);
                }
                return activity;
            }

            protected void validate(Activity activity) {
                super.validate(activity);
                ValidationSupport.checkList(activity.performedActivity, "performedActivity", CodeableReference.class);
                ValidationSupport.checkList(activity.progress, "progress", Annotation.class);
                ValidationSupport.checkReferenceType(activity.plannedActivityReference, "plannedActivityReference", "Appointment", "CommunicationRequest", "DeviceRequest", "MedicationRequest", "NutritionOrder", "Task", "ServiceRequest", "VisionPrescription", "RequestOrchestration", "ImmunizationRecommendation", "SupplyRequest");
                ValidationSupport.requireValueOrChildren(activity);
            }

            protected Builder from(Activity activity) {
                super.from(activity);
                performedActivity.addAll(activity.performedActivity);
                progress.addAll(activity.progress);
                plannedActivityReference = activity.plannedActivityReference;
                return this;
            }
        }
    }
}
