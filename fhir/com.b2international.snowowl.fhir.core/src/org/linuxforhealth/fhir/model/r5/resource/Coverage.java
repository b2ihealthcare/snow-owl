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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Money;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.CoverageKind;
import org.linuxforhealth.fhir.model.r5.type.code.CoverageStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Financial instrument which may be used to reimburse or pay for health care products and services. Includes both 
 * insurance and self-payment.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "coverage-0",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/coverage-type",
    expression = "type.exists() implies (type.memberOf('http://hl7.org/fhir/ValueSet/coverage-type', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/Coverage",
    generated = true
)
@Constraint(
    id = "coverage-1",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/subscriber-relationship",
    expression = "relationship.exists() implies (relationship.memberOf('http://hl7.org/fhir/ValueSet/subscriber-relationship', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Coverage",
    generated = true
)
@Constraint(
    id = "coverage-2",
    level = "Warning",
    location = "class.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/coverage-class",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/coverage-class', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Coverage",
    generated = true
)
@Constraint(
    id = "coverage-3",
    level = "Warning",
    location = "costToBeneficiary.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/coverage-copay-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/coverage-copay-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Coverage",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Coverage extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "CoverageStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A code specifying the state of the resource instance.",
        valueSet = "http://hl7.org/fhir/ValueSet/fm-status|5.0.0"
    )
    @Required
    private final CoverageStatus status;
    @Summary
    @Binding(
        bindingName = "CoverageKind",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/coverage-kind|5.0.0"
    )
    @Required
    private final CoverageKind kind;
    private final List<PaymentBy> paymentBy;
    @Summary
    @Binding(
        bindingName = "CoverageType",
        strength = BindingStrength.Value.PREFERRED,
        description = "The type of insurance: public health, worker compensation; private accident, auto, private health, etc.) or a direct payment by an individual or organization.",
        valueSet = "http://hl7.org/fhir/ValueSet/coverage-type"
    )
    private final CodeableConcept type;
    @Summary
    @ReferenceTarget({ "Patient", "RelatedPerson", "Organization" })
    private final Reference policyHolder;
    @Summary
    @ReferenceTarget({ "Patient", "RelatedPerson" })
    private final Reference subscriber;
    @Summary
    private final List<Identifier> subscriberId;
    @Summary
    @ReferenceTarget({ "Patient" })
    @Required
    private final Reference beneficiary;
    @Summary
    private final String dependent;
    @Binding(
        bindingName = "Relationship",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The relationship between the Subscriber and the Beneficiary (insured/covered party/patient).",
        valueSet = "http://hl7.org/fhir/ValueSet/subscriber-relationship"
    )
    private final CodeableConcept relationship;
    @Summary
    private final Period period;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference insurer;
    private final List<Class> clazz;
    @Summary
    private final PositiveInt order;
    @Summary
    private final String network;
    private final List<CostToBeneficiary> costToBeneficiary;
    private final Boolean subrogation;
    @ReferenceTarget({ "Contract" })
    private final List<Reference> contract;
    @ReferenceTarget({ "InsurancePlan" })
    private final Reference insurancePlan;

    private Coverage(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        kind = builder.kind;
        paymentBy = Collections.unmodifiableList(builder.paymentBy);
        type = builder.type;
        policyHolder = builder.policyHolder;
        subscriber = builder.subscriber;
        subscriberId = Collections.unmodifiableList(builder.subscriberId);
        beneficiary = builder.beneficiary;
        dependent = builder.dependent;
        relationship = builder.relationship;
        period = builder.period;
        insurer = builder.insurer;
        clazz = Collections.unmodifiableList(builder.clazz);
        order = builder.order;
        network = builder.network;
        costToBeneficiary = Collections.unmodifiableList(builder.costToBeneficiary);
        subrogation = builder.subrogation;
        contract = Collections.unmodifiableList(builder.contract);
        insurancePlan = builder.insurancePlan;
    }

    /**
     * The identifier of the coverage as issued by the insurer.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The status of the resource instance.
     * 
     * @return
     *     An immutable object of type {@link CoverageStatus} that is non-null.
     */
    public CoverageStatus getStatus() {
        return status;
    }

    /**
     * The nature of the coverage be it insurance, or cash payment such as self-pay.
     * 
     * @return
     *     An immutable object of type {@link CoverageKind} that is non-null.
     */
    public CoverageKind getKind() {
        return kind;
    }

    /**
     * Link to the paying party and optionally what specifically they will be responsible to pay.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link PaymentBy} that may be empty.
     */
    public List<PaymentBy> getPaymentBy() {
        return paymentBy;
    }

    /**
     * The type of coverage: social program, medical plan, accident coverage (workers compensation, auto), group health or 
     * payment by an individual or organization.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * The party who 'owns' the insurance policy.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPolicyHolder() {
        return policyHolder;
    }

    /**
     * The party who has signed-up for or 'owns' the contractual relationship to the policy or to whom the benefit of the 
     * policy for services rendered to them or their family is due.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubscriber() {
        return subscriber;
    }

    /**
     * The insurer assigned ID for the Subscriber.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getSubscriberId() {
        return subscriberId;
    }

    /**
     * The party who benefits from the insurance coverage; the patient when products and/or services are provided.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getBeneficiary() {
        return beneficiary;
    }

    /**
     * A designator for a dependent under the coverage.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDependent() {
        return dependent;
    }

    /**
     * The relationship of beneficiary (patient) to the subscriber.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getRelationship() {
        return relationship;
    }

    /**
     * Time period during which the coverage is in force. A missing start date indicates the start date isn't known, a 
     * missing end date means the coverage is continuing to be in force.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * The program or plan underwriter, payor, insurance company.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getInsurer() {
        return insurer;
    }

    /**
     * A suite of underwriter specific classifiers.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Class} that may be empty.
     */
    public List<Class> getClazz() {
        return clazz;
    }

    /**
     * The order of applicability of this coverage relative to other coverages which are currently in force. Note, there may 
     * be gaps in the numbering and this does not imply primary, secondary etc. as the specific positioning of coverages 
     * depends upon the episode of care. For example; a patient might have (0) auto insurance (1) their own health insurance 
     * and (2) spouse's health insurance. When claiming for treatments which were not the result of an auto accident then 
     * only coverages (1) and (2) above would be applicatble and would apply in the order specified in parenthesis.
     * 
     * @return
     *     An immutable object of type {@link PositiveInt} that may be null.
     */
    public PositiveInt getOrder() {
        return order;
    }

    /**
     * The insurer-specific identifier for the insurer-defined network of providers to which the beneficiary may seek 
     * treatment which will be covered at the 'in-network' rate, otherwise 'out of network' terms and conditions apply.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getNetwork() {
        return network;
    }

    /**
     * A suite of codes indicating the cost category and associated amount which have been detailed in the policy and may 
     * have been included on the health card.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CostToBeneficiary} that may be empty.
     */
    public List<CostToBeneficiary> getCostToBeneficiary() {
        return costToBeneficiary;
    }

    /**
     * When 'subrogation=true' this insurance instance has been included not for adjudication but to provide insurers with 
     * the details to recover costs.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getSubrogation() {
        return subrogation;
    }

    /**
     * The policy(s) which constitute this insurance coverage.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getContract() {
        return contract;
    }

    /**
     * The insurance plan details, benefits and costs, which constitute this insurance coverage.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getInsurancePlan() {
        return insurancePlan;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            (kind != null) || 
            !paymentBy.isEmpty() || 
            (type != null) || 
            (policyHolder != null) || 
            (subscriber != null) || 
            !subscriberId.isEmpty() || 
            (beneficiary != null) || 
            (dependent != null) || 
            (relationship != null) || 
            (period != null) || 
            (insurer != null) || 
            !clazz.isEmpty() || 
            (order != null) || 
            (network != null) || 
            !costToBeneficiary.isEmpty() || 
            (subrogation != null) || 
            !contract.isEmpty() || 
            (insurancePlan != null);
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
                accept(kind, "kind", visitor);
                accept(paymentBy, "paymentBy", visitor, PaymentBy.class);
                accept(type, "type", visitor);
                accept(policyHolder, "policyHolder", visitor);
                accept(subscriber, "subscriber", visitor);
                accept(subscriberId, "subscriberId", visitor, Identifier.class);
                accept(beneficiary, "beneficiary", visitor);
                accept(dependent, "dependent", visitor);
                accept(relationship, "relationship", visitor);
                accept(period, "period", visitor);
                accept(insurer, "insurer", visitor);
                accept(clazz, "class", visitor, Class.class);
                accept(order, "order", visitor);
                accept(network, "network", visitor);
                accept(costToBeneficiary, "costToBeneficiary", visitor, CostToBeneficiary.class);
                accept(subrogation, "subrogation", visitor);
                accept(contract, "contract", visitor, Reference.class);
                accept(insurancePlan, "insurancePlan", visitor);
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
        Coverage other = (Coverage) obj;
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
            Objects.equals(kind, other.kind) && 
            Objects.equals(paymentBy, other.paymentBy) && 
            Objects.equals(type, other.type) && 
            Objects.equals(policyHolder, other.policyHolder) && 
            Objects.equals(subscriber, other.subscriber) && 
            Objects.equals(subscriberId, other.subscriberId) && 
            Objects.equals(beneficiary, other.beneficiary) && 
            Objects.equals(dependent, other.dependent) && 
            Objects.equals(relationship, other.relationship) && 
            Objects.equals(period, other.period) && 
            Objects.equals(insurer, other.insurer) && 
            Objects.equals(clazz, other.clazz) && 
            Objects.equals(order, other.order) && 
            Objects.equals(network, other.network) && 
            Objects.equals(costToBeneficiary, other.costToBeneficiary) && 
            Objects.equals(subrogation, other.subrogation) && 
            Objects.equals(contract, other.contract) && 
            Objects.equals(insurancePlan, other.insurancePlan);
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
                kind, 
                paymentBy, 
                type, 
                policyHolder, 
                subscriber, 
                subscriberId, 
                beneficiary, 
                dependent, 
                relationship, 
                period, 
                insurer, 
                clazz, 
                order, 
                network, 
                costToBeneficiary, 
                subrogation, 
                contract, 
                insurancePlan);
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
        private CoverageStatus status;
        private CoverageKind kind;
        private List<PaymentBy> paymentBy = new ArrayList<>();
        private CodeableConcept type;
        private Reference policyHolder;
        private Reference subscriber;
        private List<Identifier> subscriberId = new ArrayList<>();
        private Reference beneficiary;
        private String dependent;
        private CodeableConcept relationship;
        private Period period;
        private Reference insurer;
        private List<Class> clazz = new ArrayList<>();
        private PositiveInt order;
        private String network;
        private List<CostToBeneficiary> costToBeneficiary = new ArrayList<>();
        private Boolean subrogation;
        private List<Reference> contract = new ArrayList<>();
        private Reference insurancePlan;

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
         * The identifier of the coverage as issued by the insurer.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier(s) for this coverage
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
         * The identifier of the coverage as issued by the insurer.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier(s) for this coverage
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
         * The status of the resource instance.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     active | cancelled | draft | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(CoverageStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The nature of the coverage be it insurance, or cash payment such as self-pay.
         * 
         * <p>This element is required.
         * 
         * @param kind
         *     insurance | self-pay | other
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder kind(CoverageKind kind) {
            this.kind = kind;
            return this;
        }

        /**
         * Link to the paying party and optionally what specifically they will be responsible to pay.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param paymentBy
         *     Self-pay parties and responsibility
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder paymentBy(PaymentBy... paymentBy) {
            for (PaymentBy value : paymentBy) {
                this.paymentBy.add(value);
            }
            return this;
        }

        /**
         * Link to the paying party and optionally what specifically they will be responsible to pay.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param paymentBy
         *     Self-pay parties and responsibility
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder paymentBy(Collection<PaymentBy> paymentBy) {
            this.paymentBy = new ArrayList<>(paymentBy);
            return this;
        }

        /**
         * The type of coverage: social program, medical plan, accident coverage (workers compensation, auto), group health or 
         * payment by an individual or organization.
         * 
         * @param type
         *     Coverage category such as medical or accident
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * The party who 'owns' the insurance policy.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param policyHolder
         *     Owner of the policy
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder policyHolder(Reference policyHolder) {
            this.policyHolder = policyHolder;
            return this;
        }

        /**
         * The party who has signed-up for or 'owns' the contractual relationship to the policy or to whom the benefit of the 
         * policy for services rendered to them or their family is due.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * </ul>
         * 
         * @param subscriber
         *     Subscriber to the policy
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subscriber(Reference subscriber) {
            this.subscriber = subscriber;
            return this;
        }

        /**
         * The insurer assigned ID for the Subscriber.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subscriberId
         *     ID assigned to the subscriber
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subscriberId(Identifier... subscriberId) {
            for (Identifier value : subscriberId) {
                this.subscriberId.add(value);
            }
            return this;
        }

        /**
         * The insurer assigned ID for the Subscriber.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subscriberId
         *     ID assigned to the subscriber
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder subscriberId(Collection<Identifier> subscriberId) {
            this.subscriberId = new ArrayList<>(subscriberId);
            return this;
        }

        /**
         * The party who benefits from the insurance coverage; the patient when products and/or services are provided.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param beneficiary
         *     Plan beneficiary
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder beneficiary(Reference beneficiary) {
            this.beneficiary = beneficiary;
            return this;
        }

        /**
         * Convenience method for setting {@code dependent}.
         * 
         * @param dependent
         *     Dependent number
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #dependent(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder dependent(java.lang.String dependent) {
            this.dependent = (dependent == null) ? null : String.of(dependent);
            return this;
        }

        /**
         * A designator for a dependent under the coverage.
         * 
         * @param dependent
         *     Dependent number
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dependent(String dependent) {
            this.dependent = dependent;
            return this;
        }

        /**
         * The relationship of beneficiary (patient) to the subscriber.
         * 
         * @param relationship
         *     Beneficiary relationship to the subscriber
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relationship(CodeableConcept relationship) {
            this.relationship = relationship;
            return this;
        }

        /**
         * Time period during which the coverage is in force. A missing start date indicates the start date isn't known, a 
         * missing end date means the coverage is continuing to be in force.
         * 
         * @param period
         *     Coverage start and end dates
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * The program or plan underwriter, payor, insurance company.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param insurer
         *     Issuer of the policy
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder insurer(Reference insurer) {
            this.insurer = insurer;
            return this;
        }

        /**
         * A suite of underwriter specific classifiers.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param clazz
         *     Additional coverage classifications
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder clazz(Class... clazz) {
            for (Class value : clazz) {
                this.clazz.add(value);
            }
            return this;
        }

        /**
         * A suite of underwriter specific classifiers.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param clazz
         *     Additional coverage classifications
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder clazz(Collection<Class> clazz) {
            this.clazz = new ArrayList<>(clazz);
            return this;
        }

        /**
         * The order of applicability of this coverage relative to other coverages which are currently in force. Note, there may 
         * be gaps in the numbering and this does not imply primary, secondary etc. as the specific positioning of coverages 
         * depends upon the episode of care. For example; a patient might have (0) auto insurance (1) their own health insurance 
         * and (2) spouse's health insurance. When claiming for treatments which were not the result of an auto accident then 
         * only coverages (1) and (2) above would be applicatble and would apply in the order specified in parenthesis.
         * 
         * @param order
         *     Relative order of the coverage
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder order(PositiveInt order) {
            this.order = order;
            return this;
        }

        /**
         * Convenience method for setting {@code network}.
         * 
         * @param network
         *     Insurer network
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #network(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder network(java.lang.String network) {
            this.network = (network == null) ? null : String.of(network);
            return this;
        }

        /**
         * The insurer-specific identifier for the insurer-defined network of providers to which the beneficiary may seek 
         * treatment which will be covered at the 'in-network' rate, otherwise 'out of network' terms and conditions apply.
         * 
         * @param network
         *     Insurer network
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder network(String network) {
            this.network = network;
            return this;
        }

        /**
         * A suite of codes indicating the cost category and associated amount which have been detailed in the policy and may 
         * have been included on the health card.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param costToBeneficiary
         *     Patient payments for services/products
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder costToBeneficiary(CostToBeneficiary... costToBeneficiary) {
            for (CostToBeneficiary value : costToBeneficiary) {
                this.costToBeneficiary.add(value);
            }
            return this;
        }

        /**
         * A suite of codes indicating the cost category and associated amount which have been detailed in the policy and may 
         * have been included on the health card.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param costToBeneficiary
         *     Patient payments for services/products
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder costToBeneficiary(Collection<CostToBeneficiary> costToBeneficiary) {
            this.costToBeneficiary = new ArrayList<>(costToBeneficiary);
            return this;
        }

        /**
         * Convenience method for setting {@code subrogation}.
         * 
         * @param subrogation
         *     Reimbursement to insurer
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #subrogation(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder subrogation(java.lang.Boolean subrogation) {
            this.subrogation = (subrogation == null) ? null : Boolean.of(subrogation);
            return this;
        }

        /**
         * When 'subrogation=true' this insurance instance has been included not for adjudication but to provide insurers with 
         * the details to recover costs.
         * 
         * @param subrogation
         *     Reimbursement to insurer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subrogation(Boolean subrogation) {
            this.subrogation = subrogation;
            return this;
        }

        /**
         * The policy(s) which constitute this insurance coverage.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Contract}</li>
         * </ul>
         * 
         * @param contract
         *     Contract details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contract(Reference... contract) {
            for (Reference value : contract) {
                this.contract.add(value);
            }
            return this;
        }

        /**
         * The policy(s) which constitute this insurance coverage.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Contract}</li>
         * </ul>
         * 
         * @param contract
         *     Contract details
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contract(Collection<Reference> contract) {
            this.contract = new ArrayList<>(contract);
            return this;
        }

        /**
         * The insurance plan details, benefits and costs, which constitute this insurance coverage.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link InsurancePlan}</li>
         * </ul>
         * 
         * @param insurancePlan
         *     Insurance plan details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder insurancePlan(Reference insurancePlan) {
            this.insurancePlan = insurancePlan;
            return this;
        }

        /**
         * Build the {@link Coverage}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>kind</li>
         * <li>beneficiary</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Coverage}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Coverage per the base specification
         */
        @Override
        public Coverage build() {
            Coverage coverage = new Coverage(this);
            if (validating) {
                validate(coverage);
            }
            return coverage;
        }

        protected void validate(Coverage coverage) {
            super.validate(coverage);
            ValidationSupport.checkList(coverage.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(coverage.status, "status");
            ValidationSupport.requireNonNull(coverage.kind, "kind");
            ValidationSupport.checkList(coverage.paymentBy, "paymentBy", PaymentBy.class);
            ValidationSupport.checkList(coverage.subscriberId, "subscriberId", Identifier.class);
            ValidationSupport.requireNonNull(coverage.beneficiary, "beneficiary");
            ValidationSupport.checkList(coverage.clazz, "class", Class.class);
            ValidationSupport.checkList(coverage.costToBeneficiary, "costToBeneficiary", CostToBeneficiary.class);
            ValidationSupport.checkList(coverage.contract, "contract", Reference.class);
            ValidationSupport.checkReferenceType(coverage.policyHolder, "policyHolder", "Patient", "RelatedPerson", "Organization");
            ValidationSupport.checkReferenceType(coverage.subscriber, "subscriber", "Patient", "RelatedPerson");
            ValidationSupport.checkReferenceType(coverage.beneficiary, "beneficiary", "Patient");
            ValidationSupport.checkReferenceType(coverage.insurer, "insurer", "Organization");
            ValidationSupport.checkReferenceType(coverage.contract, "contract", "Contract");
            ValidationSupport.checkReferenceType(coverage.insurancePlan, "insurancePlan", "InsurancePlan");
        }

        protected Builder from(Coverage coverage) {
            super.from(coverage);
            identifier.addAll(coverage.identifier);
            status = coverage.status;
            kind = coverage.kind;
            paymentBy.addAll(coverage.paymentBy);
            type = coverage.type;
            policyHolder = coverage.policyHolder;
            subscriber = coverage.subscriber;
            subscriberId.addAll(coverage.subscriberId);
            beneficiary = coverage.beneficiary;
            dependent = coverage.dependent;
            relationship = coverage.relationship;
            period = coverage.period;
            insurer = coverage.insurer;
            clazz.addAll(coverage.clazz);
            order = coverage.order;
            network = coverage.network;
            costToBeneficiary.addAll(coverage.costToBeneficiary);
            subrogation = coverage.subrogation;
            contract.addAll(coverage.contract);
            insurancePlan = coverage.insurancePlan;
            return this;
        }
    }

    /**
     * Link to the paying party and optionally what specifically they will be responsible to pay.
     */
    public static class PaymentBy extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Patient", "RelatedPerson", "Organization" })
        @Required
        private final Reference party;
        @Summary
        private final String responsibility;

        private PaymentBy(Builder builder) {
            super(builder);
            party = builder.party;
            responsibility = builder.responsibility;
        }

        /**
         * The list of parties providing non-insurance payment for the treatment costs.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getParty() {
            return party;
        }

        /**
         *  Description of the financial responsibility.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getResponsibility() {
            return responsibility;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (party != null) || 
                (responsibility != null);
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
                    accept(party, "party", visitor);
                    accept(responsibility, "responsibility", visitor);
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
            PaymentBy other = (PaymentBy) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(party, other.party) && 
                Objects.equals(responsibility, other.responsibility);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    party, 
                    responsibility);
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
            private Reference party;
            private String responsibility;

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
             * The list of parties providing non-insurance payment for the treatment costs.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param party
             *     Parties performing self-payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder party(Reference party) {
                this.party = party;
                return this;
            }

            /**
             * Convenience method for setting {@code responsibility}.
             * 
             * @param responsibility
             *     Party's responsibility
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #responsibility(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder responsibility(java.lang.String responsibility) {
                this.responsibility = (responsibility == null) ? null : String.of(responsibility);
                return this;
            }

            /**
             *  Description of the financial responsibility.
             * 
             * @param responsibility
             *     Party's responsibility
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder responsibility(String responsibility) {
                this.responsibility = responsibility;
                return this;
            }

            /**
             * Build the {@link PaymentBy}
             * 
             * <p>Required elements:
             * <ul>
             * <li>party</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link PaymentBy}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid PaymentBy per the base specification
             */
            @Override
            public PaymentBy build() {
                PaymentBy paymentBy = new PaymentBy(this);
                if (validating) {
                    validate(paymentBy);
                }
                return paymentBy;
            }

            protected void validate(PaymentBy paymentBy) {
                super.validate(paymentBy);
                ValidationSupport.requireNonNull(paymentBy.party, "party");
                ValidationSupport.checkReferenceType(paymentBy.party, "party", "Patient", "RelatedPerson", "Organization");
                ValidationSupport.requireValueOrChildren(paymentBy);
            }

            protected Builder from(PaymentBy paymentBy) {
                super.from(paymentBy);
                party = paymentBy.party;
                responsibility = paymentBy.responsibility;
                return this;
            }
        }
    }

    /**
     * A suite of underwriter specific classifiers.
     */
    public static class Class extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "CoverageClass",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The policy classifications, e.g. Group, Plan, Class, etc.",
            valueSet = "http://hl7.org/fhir/ValueSet/coverage-class"
        )
        @Required
        private final CodeableConcept type;
        @Summary
        @Required
        private final Identifier value;
        @Summary
        private final String name;

        private Class(Builder builder) {
            super(builder);
            type = builder.type;
            value = builder.value;
            name = builder.name;
        }

        /**
         * The type of classification for which an insurer-specific class label or number and optional name is provided. For 
         * example, type may be used to identify a class of coverage or employer group, policy, or plan.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The alphanumeric identifier associated with the insurer issued label.
         * 
         * @return
         *     An immutable object of type {@link Identifier} that is non-null.
         */
        public Identifier getValue() {
            return value;
        }

        /**
         * A short description for the class.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getName() {
            return name;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (value != null) || 
                (name != null);
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
                    accept(name, "name", visitor);
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
            Class other = (Class) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(value, other.value) && 
                Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    value, 
                    name);
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
            private Identifier value;
            private String name;

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
             * The type of classification for which an insurer-specific class label or number and optional name is provided. For 
             * example, type may be used to identify a class of coverage or employer group, policy, or plan.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Type of class such as 'group' or 'plan'
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The alphanumeric identifier associated with the insurer issued label.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     Value associated with the type
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Identifier value) {
                this.value = value;
                return this;
            }

            /**
             * Convenience method for setting {@code name}.
             * 
             * @param name
             *     Human readable description of the type and value
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
             * A short description for the class.
             * 
             * @param name
             *     Human readable description of the type and value
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Build the {@link Class}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>value</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Class}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Class per the base specification
             */
            @Override
            public Class build() {
                Class _class = new Class(this);
                if (validating) {
                    validate(_class);
                }
                return _class;
            }

            protected void validate(Class _class) {
                super.validate(_class);
                ValidationSupport.requireNonNull(_class.type, "type");
                ValidationSupport.requireNonNull(_class.value, "value");
                ValidationSupport.requireValueOrChildren(_class);
            }

            protected Builder from(Class _class) {
                super.from(_class);
                type = _class.type;
                value = _class.value;
                name = _class.name;
                return this;
            }
        }
    }

    /**
     * A suite of codes indicating the cost category and associated amount which have been detailed in the policy and may 
     * have been included on the health card.
     */
    public static class CostToBeneficiary extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "CopayTypes",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The types of services to which patient copayments are specified.",
            valueSet = "http://hl7.org/fhir/ValueSet/coverage-copay-type"
        )
        private final CodeableConcept type;
        @Binding(
            bindingName = "BenefitCategory",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/ex-benefitcategory"
        )
        private final CodeableConcept category;
        @Binding(
            bindingName = "BenefitNetwork",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/benefit-network"
        )
        private final CodeableConcept network;
        @Binding(
            bindingName = "BenefitUnit",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/benefit-unit"
        )
        private final CodeableConcept unit;
        @Binding(
            bindingName = "BenefitTerm",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/benefit-term"
        )
        private final CodeableConcept term;
        @Summary
        @Choice({ SimpleQuantity.class, Money.class })
        private final Element value;
        private final List<Exception> exception;

        private CostToBeneficiary(Builder builder) {
            super(builder);
            type = builder.type;
            category = builder.category;
            network = builder.network;
            unit = builder.unit;
            term = builder.term;
            value = builder.value;
            exception = Collections.unmodifiableList(builder.exception);
        }

        /**
         * The category of patient centric costs associated with treatment.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Code to identify the general type of benefits under which products and services are provided.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCategory() {
            return category;
        }

        /**
         * Is a flag to indicate whether the benefits refer to in-network providers or out-of-network providers.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getNetwork() {
            return network;
        }

        /**
         * Indicates if the benefits apply to an individual or to the family.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getUnit() {
            return unit;
        }

        /**
         * The term or period of the values such as 'maximum lifetime benefit' or 'maximum annual visits'.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getTerm() {
            return term;
        }

        /**
         * The amount due from the patient for the cost category.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} or {@link Money} that may be null.
         */
        public Element getValue() {
            return value;
        }

        /**
         * A suite of codes indicating exceptions or reductions to patient costs and their effective periods.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Exception} that may be empty.
         */
        public List<Exception> getException() {
            return exception;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (category != null) || 
                (network != null) || 
                (unit != null) || 
                (term != null) || 
                (value != null) || 
                !exception.isEmpty();
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
                    accept(category, "category", visitor);
                    accept(network, "network", visitor);
                    accept(unit, "unit", visitor);
                    accept(term, "term", visitor);
                    accept(value, "value", visitor);
                    accept(exception, "exception", visitor, Exception.class);
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
            CostToBeneficiary other = (CostToBeneficiary) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(category, other.category) && 
                Objects.equals(network, other.network) && 
                Objects.equals(unit, other.unit) && 
                Objects.equals(term, other.term) && 
                Objects.equals(value, other.value) && 
                Objects.equals(exception, other.exception);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    category, 
                    network, 
                    unit, 
                    term, 
                    value, 
                    exception);
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
            private CodeableConcept category;
            private CodeableConcept network;
            private CodeableConcept unit;
            private CodeableConcept term;
            private Element value;
            private List<Exception> exception = new ArrayList<>();

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
             * The category of patient centric costs associated with treatment.
             * 
             * @param type
             *     Cost category
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Code to identify the general type of benefits under which products and services are provided.
             * 
             * @param category
             *     Benefit classification
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder category(CodeableConcept category) {
                this.category = category;
                return this;
            }

            /**
             * Is a flag to indicate whether the benefits refer to in-network providers or out-of-network providers.
             * 
             * @param network
             *     In or out of network
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder network(CodeableConcept network) {
                this.network = network;
                return this;
            }

            /**
             * Indicates if the benefits apply to an individual or to the family.
             * 
             * @param unit
             *     Individual or family
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder unit(CodeableConcept unit) {
                this.unit = unit;
                return this;
            }

            /**
             * The term or period of the values such as 'maximum lifetime benefit' or 'maximum annual visits'.
             * 
             * @param term
             *     Annual or lifetime
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder term(CodeableConcept term) {
                this.term = term;
                return this;
            }

            /**
             * The amount due from the patient for the cost category.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link SimpleQuantity}</li>
             * <li>{@link Money}</li>
             * </ul>
             * 
             * @param value
             *     The amount or percentage due from the beneficiary
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * A suite of codes indicating exceptions or reductions to patient costs and their effective periods.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param exception
             *     Exceptions for patient payments
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder exception(Exception... exception) {
                for (Exception value : exception) {
                    this.exception.add(value);
                }
                return this;
            }

            /**
             * A suite of codes indicating exceptions or reductions to patient costs and their effective periods.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param exception
             *     Exceptions for patient payments
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder exception(Collection<Exception> exception) {
                this.exception = new ArrayList<>(exception);
                return this;
            }

            /**
             * Build the {@link CostToBeneficiary}
             * 
             * @return
             *     An immutable object of type {@link CostToBeneficiary}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid CostToBeneficiary per the base specification
             */
            @Override
            public CostToBeneficiary build() {
                CostToBeneficiary costToBeneficiary = new CostToBeneficiary(this);
                if (validating) {
                    validate(costToBeneficiary);
                }
                return costToBeneficiary;
            }

            protected void validate(CostToBeneficiary costToBeneficiary) {
                super.validate(costToBeneficiary);
                ValidationSupport.choiceElement(costToBeneficiary.value, "value", SimpleQuantity.class, Money.class);
                ValidationSupport.checkList(costToBeneficiary.exception, "exception", Exception.class);
                ValidationSupport.requireValueOrChildren(costToBeneficiary);
            }

            protected Builder from(CostToBeneficiary costToBeneficiary) {
                super.from(costToBeneficiary);
                type = costToBeneficiary.type;
                category = costToBeneficiary.category;
                network = costToBeneficiary.network;
                unit = costToBeneficiary.unit;
                term = costToBeneficiary.term;
                value = costToBeneficiary.value;
                exception.addAll(costToBeneficiary.exception);
                return this;
            }
        }

        /**
         * A suite of codes indicating exceptions or reductions to patient costs and their effective periods.
         */
        public static class Exception extends BackboneElement {
            @Summary
            @Binding(
                bindingName = "CoverageFinancialException",
                strength = BindingStrength.Value.EXAMPLE,
                description = "The types of exceptions from the part or full value of financial obligations such as copays.",
                valueSet = "http://hl7.org/fhir/ValueSet/coverage-financial-exception"
            )
            @Required
            private final CodeableConcept type;
            @Summary
            private final Period period;

            private Exception(Builder builder) {
                super(builder);
                type = builder.type;
                period = builder.period;
            }

            /**
             * The code for the specific exception.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * The timeframe the exception is in force.
             * 
             * @return
             *     An immutable object of type {@link Period} that may be null.
             */
            public Period getPeriod() {
                return period;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
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
                        accept(type, "type", visitor);
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
                Exception other = (Exception) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(period, other.period);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
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
                private CodeableConcept type;
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
                 * The code for the specific exception.
                 * 
                 * <p>This element is required.
                 * 
                 * @param type
                 *     Exception category
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * The timeframe the exception is in force.
                 * 
                 * @param period
                 *     The effective period of the exception
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder period(Period period) {
                    this.period = period;
                    return this;
                }

                /**
                 * Build the {@link Exception}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>type</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Exception}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Exception per the base specification
                 */
                @Override
                public Exception build() {
                    Exception exception = new Exception(this);
                    if (validating) {
                        validate(exception);
                    }
                    return exception;
                }

                protected void validate(Exception exception) {
                    super.validate(exception);
                    ValidationSupport.requireNonNull(exception.type, "type");
                    ValidationSupport.requireValueOrChildren(exception);
                }

                protected Builder from(Exception exception) {
                    super.from(exception);
                    type = exception.type;
                    period = exception.period;
                    return this;
                }
            }
        }
    }
}
