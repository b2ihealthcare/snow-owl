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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Money;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.AccountStatus;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A financial tool for tracking value accrued for a particular purpose. In the healthcare field, used to track charges 
 * for a patient, cost centers, etc.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "act-1",
    level = "Rule",
    location = "Account.diagnosis",
    description = "The dateOfDiagnosis is not valid when using a reference to a diagnosis",
    expression = "condition.reference.empty().not() implies dateOfDiagnosis.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/Account"
)
@Constraint(
    id = "act-2",
    level = "Rule",
    location = "Account.procedure",
    description = "The dateOfService is not valid when using a reference to a procedure",
    expression = "code.reference.empty().not() implies dateOfService.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/Account"
)
@Constraint(
    id = "account-3",
    level = "Warning",
    location = "diagnosis.type",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/encounter-diagnosis-use",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/encounter-diagnosis-use', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Account",
    generated = true
)
@Constraint(
    id = "account-4",
    level = "Warning",
    location = "balance.aggregate",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/account-aggregate",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/account-aggregate', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Account",
    generated = true
)
@Constraint(
    id = "account-5",
    level = "Warning",
    location = "balance.term",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/account-balance-term",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/account-balance-term', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Account",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Account extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "AccountStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Indicates whether the account is available to be used.",
        valueSet = "http://hl7.org/fhir/ValueSet/account-status|5.0.0"
    )
    @Required
    private final AccountStatus status;
    @Summary
    @Binding(
        bindingName = "AccountBillingStatus",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Indicates whether the account is available to be used for billing purposes.",
        valueSet = "http://hl7.org/fhir/ValueSet/account-billing-status"
    )
    private final CodeableConcept billingStatus;
    @Summary
    @Binding(
        bindingName = "AccountType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The usage type of this account, permits categorization of accounts.",
        valueSet = "http://hl7.org/fhir/ValueSet/account-type"
    )
    private final CodeableConcept type;
    @Summary
    private final String name;
    @Summary
    @ReferenceTarget({ "Patient", "Device", "Practitioner", "PractitionerRole", "Location", "HealthcareService", "Organization" })
    private final List<Reference> subject;
    @Summary
    private final Period servicePeriod;
    @Summary
    private final List<Coverage> coverage;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference owner;
    @Summary
    private final Markdown description;
    private final List<Guarantor> guarantor;
    @Summary
    private final List<Diagnosis> diagnosis;
    @Summary
    private final List<Procedure> procedure;
    private final List<RelatedAccount> relatedAccount;
    @Binding(
        bindingName = "AccountCurrency",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/currencies|5.0.0"
    )
    private final CodeableConcept currency;
    private final List<Balance> balance;
    private final Instant calculatedAt;

    private Account(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        billingStatus = builder.billingStatus;
        type = builder.type;
        name = builder.name;
        subject = Collections.unmodifiableList(builder.subject);
        servicePeriod = builder.servicePeriod;
        coverage = Collections.unmodifiableList(builder.coverage);
        owner = builder.owner;
        description = builder.description;
        guarantor = Collections.unmodifiableList(builder.guarantor);
        diagnosis = Collections.unmodifiableList(builder.diagnosis);
        procedure = Collections.unmodifiableList(builder.procedure);
        relatedAccount = Collections.unmodifiableList(builder.relatedAccount);
        currency = builder.currency;
        balance = Collections.unmodifiableList(builder.balance);
        calculatedAt = builder.calculatedAt;
    }

    /**
     * Unique identifier used to reference the account. Might or might not be intended for human use (e.g. credit card 
     * number).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Indicates whether the account is presently used/usable or not.
     * 
     * @return
     *     An immutable object of type {@link AccountStatus} that is non-null.
     */
    public AccountStatus getStatus() {
        return status;
    }

    /**
     * The BillingStatus tracks the lifecycle of the account through the billing process. It indicates how transactions are 
     * treated when they are allocated to the account.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getBillingStatus() {
        return billingStatus;
    }

    /**
     * Categorizes the account for reporting and searching purposes.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * Name used for the account when displaying it to humans in reports, etc.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * Identifies the entity which incurs the expenses. While the immediate recipients of services or goods might be entities 
     * related to the subject, the expenses were ultimately incurred by the subject of the Account.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSubject() {
        return subject;
    }

    /**
     * The date range of services associated with this account.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getServicePeriod() {
        return servicePeriod;
    }

    /**
     * The party(s) that are responsible for covering the payment of this account, and what order should they be applied to 
     * the account.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Coverage} that may be empty.
     */
    public List<Coverage> getCoverage() {
        return coverage;
    }

    /**
     * Indicates the service area, hospital, department, etc. with responsibility for managing the Account.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getOwner() {
        return owner;
    }

    /**
     * Provides additional information about what the account tracks and how it is used.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * The parties responsible for balancing the account if other payment options fall short.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Guarantor} that may be empty.
     */
    public List<Guarantor> getGuarantor() {
        return guarantor;
    }

    /**
     * When using an account for billing a specific Encounter the set of diagnoses that are relevant for billing are stored 
     * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Diagnosis} that may be empty.
     */
    public List<Diagnosis> getDiagnosis() {
        return diagnosis;
    }

    /**
     * When using an account for billing a specific Encounter the set of procedures that are relevant for billing are stored 
     * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Procedure} that may be empty.
     */
    public List<Procedure> getProcedure() {
        return procedure;
    }

    /**
     * Other associated accounts related to this account.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatedAccount} that may be empty.
     */
    public List<RelatedAccount> getRelatedAccount() {
        return relatedAccount;
    }

    /**
     * The default currency for the account.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCurrency() {
        return currency;
    }

    /**
     * The calculated account balances - these are calculated and processed by the finance system.The balances with a 
     * `term` that is not current are usually generated/updated by an invoicing or similar process.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Balance} that may be empty.
     */
    public List<Balance> getBalance() {
        return balance;
    }

    /**
     * Time the balance amount was calculated.
     * 
     * @return
     *     An immutable object of type {@link Instant} that may be null.
     */
    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            (billingStatus != null) || 
            (type != null) || 
            (name != null) || 
            !subject.isEmpty() || 
            (servicePeriod != null) || 
            !coverage.isEmpty() || 
            (owner != null) || 
            (description != null) || 
            !guarantor.isEmpty() || 
            !diagnosis.isEmpty() || 
            !procedure.isEmpty() || 
            !relatedAccount.isEmpty() || 
            (currency != null) || 
            !balance.isEmpty() || 
            (calculatedAt != null);
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
                accept(billingStatus, "billingStatus", visitor);
                accept(type, "type", visitor);
                accept(name, "name", visitor);
                accept(subject, "subject", visitor, Reference.class);
                accept(servicePeriod, "servicePeriod", visitor);
                accept(coverage, "coverage", visitor, Coverage.class);
                accept(owner, "owner", visitor);
                accept(description, "description", visitor);
                accept(guarantor, "guarantor", visitor, Guarantor.class);
                accept(diagnosis, "diagnosis", visitor, Diagnosis.class);
                accept(procedure, "procedure", visitor, Procedure.class);
                accept(relatedAccount, "relatedAccount", visitor, RelatedAccount.class);
                accept(currency, "currency", visitor);
                accept(balance, "balance", visitor, Balance.class);
                accept(calculatedAt, "calculatedAt", visitor);
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
        Account other = (Account) obj;
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
            Objects.equals(billingStatus, other.billingStatus) && 
            Objects.equals(type, other.type) && 
            Objects.equals(name, other.name) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(servicePeriod, other.servicePeriod) && 
            Objects.equals(coverage, other.coverage) && 
            Objects.equals(owner, other.owner) && 
            Objects.equals(description, other.description) && 
            Objects.equals(guarantor, other.guarantor) && 
            Objects.equals(diagnosis, other.diagnosis) && 
            Objects.equals(procedure, other.procedure) && 
            Objects.equals(relatedAccount, other.relatedAccount) && 
            Objects.equals(currency, other.currency) && 
            Objects.equals(balance, other.balance) && 
            Objects.equals(calculatedAt, other.calculatedAt);
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
                billingStatus, 
                type, 
                name, 
                subject, 
                servicePeriod, 
                coverage, 
                owner, 
                description, 
                guarantor, 
                diagnosis, 
                procedure, 
                relatedAccount, 
                currency, 
                balance, 
                calculatedAt);
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
        private AccountStatus status;
        private CodeableConcept billingStatus;
        private CodeableConcept type;
        private String name;
        private List<Reference> subject = new ArrayList<>();
        private Period servicePeriod;
        private List<Coverage> coverage = new ArrayList<>();
        private Reference owner;
        private Markdown description;
        private List<Guarantor> guarantor = new ArrayList<>();
        private List<Diagnosis> diagnosis = new ArrayList<>();
        private List<Procedure> procedure = new ArrayList<>();
        private List<RelatedAccount> relatedAccount = new ArrayList<>();
        private CodeableConcept currency;
        private List<Balance> balance = new ArrayList<>();
        private Instant calculatedAt;

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
         * Unique identifier used to reference the account. Might or might not be intended for human use (e.g. credit card 
         * number).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Account number
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
         * Unique identifier used to reference the account. Might or might not be intended for human use (e.g. credit card 
         * number).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Account number
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
         * Indicates whether the account is presently used/usable or not.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     active | inactive | entered-in-error | on-hold | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The BillingStatus tracks the lifecycle of the account through the billing process. It indicates how transactions are 
         * treated when they are allocated to the account.
         * 
         * @param billingStatus
         *     Tracks the lifecycle of the account through the billing process
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder billingStatus(CodeableConcept billingStatus) {
            this.billingStatus = billingStatus;
            return this;
        }

        /**
         * Categorizes the account for reporting and searching purposes.
         * 
         * @param type
         *     E.g. patient, expense, depreciation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Human-readable label
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
         * Name used for the account when displaying it to humans in reports, etc.
         * 
         * @param name
         *     Human-readable label
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Identifies the entity which incurs the expenses. While the immediate recipients of services or goods might be entities 
         * related to the subject, the expenses were ultimately incurred by the subject of the Account.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Device}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Location}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param subject
         *     The entity that caused the expenses
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference... subject) {
            for (Reference value : subject) {
                this.subject.add(value);
            }
            return this;
        }

        /**
         * Identifies the entity which incurs the expenses. While the immediate recipients of services or goods might be entities 
         * related to the subject, the expenses were ultimately incurred by the subject of the Account.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Device}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Location}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param subject
         *     The entity that caused the expenses
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder subject(Collection<Reference> subject) {
            this.subject = new ArrayList<>(subject);
            return this;
        }

        /**
         * The date range of services associated with this account.
         * 
         * @param servicePeriod
         *     Transaction window
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder servicePeriod(Period servicePeriod) {
            this.servicePeriod = servicePeriod;
            return this;
        }

        /**
         * The party(s) that are responsible for covering the payment of this account, and what order should they be applied to 
         * the account.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param coverage
         *     The party(s) that are responsible for covering the payment of this account, and what order should they be applied to 
         *     the account
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder coverage(Coverage... coverage) {
            for (Coverage value : coverage) {
                this.coverage.add(value);
            }
            return this;
        }

        /**
         * The party(s) that are responsible for covering the payment of this account, and what order should they be applied to 
         * the account.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param coverage
         *     The party(s) that are responsible for covering the payment of this account, and what order should they be applied to 
         *     the account
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder coverage(Collection<Coverage> coverage) {
            this.coverage = new ArrayList<>(coverage);
            return this;
        }

        /**
         * Indicates the service area, hospital, department, etc. with responsibility for managing the Account.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param owner
         *     Entity managing the Account
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder owner(Reference owner) {
            this.owner = owner;
            return this;
        }

        /**
         * Provides additional information about what the account tracks and how it is used.
         * 
         * @param description
         *     Explanation of purpose/use
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * The parties responsible for balancing the account if other payment options fall short.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param guarantor
         *     The parties ultimately responsible for balancing the Account
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder guarantor(Guarantor... guarantor) {
            for (Guarantor value : guarantor) {
                this.guarantor.add(value);
            }
            return this;
        }

        /**
         * The parties responsible for balancing the account if other payment options fall short.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param guarantor
         *     The parties ultimately responsible for balancing the Account
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder guarantor(Collection<Guarantor> guarantor) {
            this.guarantor = new ArrayList<>(guarantor);
            return this;
        }

        /**
         * When using an account for billing a specific Encounter the set of diagnoses that are relevant for billing are stored 
         * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param diagnosis
         *     The list of diagnoses relevant to this account
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder diagnosis(Diagnosis... diagnosis) {
            for (Diagnosis value : diagnosis) {
                this.diagnosis.add(value);
            }
            return this;
        }

        /**
         * When using an account for billing a specific Encounter the set of diagnoses that are relevant for billing are stored 
         * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param diagnosis
         *     The list of diagnoses relevant to this account
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder diagnosis(Collection<Diagnosis> diagnosis) {
            this.diagnosis = new ArrayList<>(diagnosis);
            return this;
        }

        /**
         * When using an account for billing a specific Encounter the set of procedures that are relevant for billing are stored 
         * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param procedure
         *     The list of procedures relevant to this account
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder procedure(Procedure... procedure) {
            for (Procedure value : procedure) {
                this.procedure.add(value);
            }
            return this;
        }

        /**
         * When using an account for billing a specific Encounter the set of procedures that are relevant for billing are stored 
         * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param procedure
         *     The list of procedures relevant to this account
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder procedure(Collection<Procedure> procedure) {
            this.procedure = new ArrayList<>(procedure);
            return this;
        }

        /**
         * Other associated accounts related to this account.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedAccount
         *     Other associated accounts related to this account
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relatedAccount(RelatedAccount... relatedAccount) {
            for (RelatedAccount value : relatedAccount) {
                this.relatedAccount.add(value);
            }
            return this;
        }

        /**
         * Other associated accounts related to this account.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedAccount
         *     Other associated accounts related to this account
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relatedAccount(Collection<RelatedAccount> relatedAccount) {
            this.relatedAccount = new ArrayList<>(relatedAccount);
            return this;
        }

        /**
         * The default currency for the account.
         * 
         * @param currency
         *     The base or default currency
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder currency(CodeableConcept currency) {
            this.currency = currency;
            return this;
        }

        /**
         * The calculated account balances - these are calculated and processed by the finance system.The balances with a 
         * `term` that is not current are usually generated/updated by an invoicing or similar process.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param balance
         *     Calculated account balance(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder balance(Balance... balance) {
            for (Balance value : balance) {
                this.balance.add(value);
            }
            return this;
        }

        /**
         * The calculated account balances - these are calculated and processed by the finance system.The balances with a 
         * `term` that is not current are usually generated/updated by an invoicing or similar process.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param balance
         *     Calculated account balance(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder balance(Collection<Balance> balance) {
            this.balance = new ArrayList<>(balance);
            return this;
        }

        /**
         * Convenience method for setting {@code calculatedAt}.
         * 
         * @param calculatedAt
         *     Time the balance amount was calculated
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #calculatedAt(org.linuxforhealth.fhir.model.type.Instant)
         */
        public Builder calculatedAt(java.time.ZonedDateTime calculatedAt) {
            this.calculatedAt = (calculatedAt == null) ? null : Instant.of(calculatedAt);
            return this;
        }

        /**
         * Time the balance amount was calculated.
         * 
         * @param calculatedAt
         *     Time the balance amount was calculated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder calculatedAt(Instant calculatedAt) {
            this.calculatedAt = calculatedAt;
            return this;
        }

        /**
         * Build the {@link Account}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Account}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Account per the base specification
         */
        @Override
        public Account build() {
            Account account = new Account(this);
            if (validating) {
                validate(account);
            }
            return account;
        }

        protected void validate(Account account) {
            super.validate(account);
            ValidationSupport.checkList(account.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(account.status, "status");
            ValidationSupport.checkList(account.subject, "subject", Reference.class);
            ValidationSupport.checkList(account.coverage, "coverage", Coverage.class);
            ValidationSupport.checkList(account.guarantor, "guarantor", Guarantor.class);
            ValidationSupport.checkList(account.diagnosis, "diagnosis", Diagnosis.class);
            ValidationSupport.checkList(account.procedure, "procedure", Procedure.class);
            ValidationSupport.checkList(account.relatedAccount, "relatedAccount", RelatedAccount.class);
            ValidationSupport.checkList(account.balance, "balance", Balance.class);
            ValidationSupport.checkReferenceType(account.subject, "subject", "Patient", "Device", "Practitioner", "PractitionerRole", "Location", "HealthcareService", "Organization");
            ValidationSupport.checkReferenceType(account.owner, "owner", "Organization");
        }

        protected Builder from(Account account) {
            super.from(account);
            identifier.addAll(account.identifier);
            status = account.status;
            billingStatus = account.billingStatus;
            type = account.type;
            name = account.name;
            subject.addAll(account.subject);
            servicePeriod = account.servicePeriod;
            coverage.addAll(account.coverage);
            owner = account.owner;
            description = account.description;
            guarantor.addAll(account.guarantor);
            diagnosis.addAll(account.diagnosis);
            procedure.addAll(account.procedure);
            relatedAccount.addAll(account.relatedAccount);
            currency = account.currency;
            balance.addAll(account.balance);
            calculatedAt = account.calculatedAt;
            return this;
        }
    }

    /**
     * The party(s) that are responsible for covering the payment of this account, and what order should they be applied to 
     * the account.
     */
    public static class Coverage extends BackboneElement {
        @Summary
        @ReferenceTarget({ "Coverage" })
        @Required
        private final Reference coverage;
        @Summary
        private final PositiveInt priority;

        private Coverage(Builder builder) {
            super(builder);
            coverage = builder.coverage;
            priority = builder.priority;
        }

        /**
         * The party(s) that contribute to payment (or part of) of the charges applied to this account (including self-pay).

A 
         * coverage may only be responsible for specific types of charges, and the sequence of the coverages in the account could 
         * be important when processing billing.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getCoverage() {
            return coverage;
        }

        /**
         * The priority of the coverage in the context of this account.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getPriority() {
            return priority;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (coverage != null) || 
                (priority != null);
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
                    accept(coverage, "coverage", visitor);
                    accept(priority, "priority", visitor);
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
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(coverage, other.coverage) && 
                Objects.equals(priority, other.priority);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    coverage, 
                    priority);
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
            private Reference coverage;
            private PositiveInt priority;

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
             * The party(s) that contribute to payment (or part of) of the charges applied to this account (including self-pay).

A 
             * coverage may only be responsible for specific types of charges, and the sequence of the coverages in the account could 
             * be important when processing billing.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Coverage}</li>
             * </ul>
             * 
             * @param coverage
             *     The party(s), such as insurances, that may contribute to the payment of this account
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder coverage(Reference coverage) {
                this.coverage = coverage;
                return this;
            }

            /**
             * The priority of the coverage in the context of this account.
             * 
             * @param priority
             *     The priority of the coverage in the context of this account
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder priority(PositiveInt priority) {
                this.priority = priority;
                return this;
            }

            /**
             * Build the {@link Coverage}
             * 
             * <p>Required elements:
             * <ul>
             * <li>coverage</li>
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
                ValidationSupport.requireNonNull(coverage.coverage, "coverage");
                ValidationSupport.checkReferenceType(coverage.coverage, "coverage", "Coverage");
                ValidationSupport.requireValueOrChildren(coverage);
            }

            protected Builder from(Coverage coverage) {
                super.from(coverage);
                this.coverage = coverage.coverage;
                priority = coverage.priority;
                return this;
            }
        }
    }

    /**
     * The parties responsible for balancing the account if other payment options fall short.
     */
    public static class Guarantor extends BackboneElement {
        @ReferenceTarget({ "Patient", "RelatedPerson", "Organization" })
        @Required
        private final Reference party;
        private final Boolean onHold;
        private final Period period;

        private Guarantor(Builder builder) {
            super(builder);
            party = builder.party;
            onHold = builder.onHold;
            period = builder.period;
        }

        /**
         * The entity who is responsible.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getParty() {
            return party;
        }

        /**
         * A guarantor may be placed on credit hold or otherwise have their role temporarily suspended.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getOnHold() {
            return onHold;
        }

        /**
         * The timeframe during which the guarantor accepts responsibility for the account.
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
                (party != null) || 
                (onHold != null) || 
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
                    accept(party, "party", visitor);
                    accept(onHold, "onHold", visitor);
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
            Guarantor other = (Guarantor) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(party, other.party) && 
                Objects.equals(onHold, other.onHold) && 
                Objects.equals(period, other.period);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    party, 
                    onHold, 
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
            private Reference party;
            private Boolean onHold;
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
             * The entity who is responsible.
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
             *     Responsible entity
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder party(Reference party) {
                this.party = party;
                return this;
            }

            /**
             * Convenience method for setting {@code onHold}.
             * 
             * @param onHold
             *     Credit or other hold applied
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #onHold(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder onHold(java.lang.Boolean onHold) {
                this.onHold = (onHold == null) ? null : Boolean.of(onHold);
                return this;
            }

            /**
             * A guarantor may be placed on credit hold or otherwise have their role temporarily suspended.
             * 
             * @param onHold
             *     Credit or other hold applied
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder onHold(Boolean onHold) {
                this.onHold = onHold;
                return this;
            }

            /**
             * The timeframe during which the guarantor accepts responsibility for the account.
             * 
             * @param period
             *     Guarantee account during
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link Guarantor}
             * 
             * <p>Required elements:
             * <ul>
             * <li>party</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Guarantor}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Guarantor per the base specification
             */
            @Override
            public Guarantor build() {
                Guarantor guarantor = new Guarantor(this);
                if (validating) {
                    validate(guarantor);
                }
                return guarantor;
            }

            protected void validate(Guarantor guarantor) {
                super.validate(guarantor);
                ValidationSupport.requireNonNull(guarantor.party, "party");
                ValidationSupport.checkReferenceType(guarantor.party, "party", "Patient", "RelatedPerson", "Organization");
                ValidationSupport.requireValueOrChildren(guarantor);
            }

            protected Builder from(Guarantor guarantor) {
                super.from(guarantor);
                party = guarantor.party;
                onHold = guarantor.onHold;
                period = guarantor.period;
                return this;
            }
        }
    }

    /**
     * When using an account for billing a specific Encounter the set of diagnoses that are relevant for billing are stored 
     * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
     */
    public static class Diagnosis extends BackboneElement {
        private final PositiveInt sequence;
        @Summary
        @Binding(
            bindingName = "condition-code",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
        )
        @Required
        private final CodeableReference condition;
        private final DateTime dateOfDiagnosis;
        @Binding(
            bindingName = "DiagnosisUse",
            strength = BindingStrength.Value.PREFERRED,
            description = "The type of diagnosis this condition represents.",
            valueSet = "http://hl7.org/fhir/ValueSet/encounter-diagnosis-use"
        )
        private final List<CodeableConcept> type;
        private final Boolean onAdmission;
        @Binding(
            bindingName = "diagnosis-package-code",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Local or Regional package codes, e.g. DRGs"
        )
        private final List<CodeableConcept> packageCode;

        private Diagnosis(Builder builder) {
            super(builder);
            sequence = builder.sequence;
            condition = builder.condition;
            dateOfDiagnosis = builder.dateOfDiagnosis;
            type = Collections.unmodifiableList(builder.type);
            onAdmission = builder.onAdmission;
            packageCode = Collections.unmodifiableList(builder.packageCode);
        }

        /**
         * Ranking of the diagnosis (for each type).
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getSequence() {
            return sequence;
        }

        /**
         * The diagnosis relevant to the account.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getCondition() {
            return condition;
        }

        /**
         * Ranking of the diagnosis (for each type).
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getDateOfDiagnosis() {
            return dateOfDiagnosis;
        }

        /**
         * Type that this diagnosis has relevant to the account (e.g. admission, billing, discharge …).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * Was the Diagnosis present on Admission in the related Encounter.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getOnAdmission() {
            return onAdmission;
        }

        /**
         * The package code can be used to group diagnoses that may be priced or delivered as a single product. Such as DRGs.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getPackageCode() {
            return packageCode;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (sequence != null) || 
                (condition != null) || 
                (dateOfDiagnosis != null) || 
                !type.isEmpty() || 
                (onAdmission != null) || 
                !packageCode.isEmpty();
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
                    accept(sequence, "sequence", visitor);
                    accept(condition, "condition", visitor);
                    accept(dateOfDiagnosis, "dateOfDiagnosis", visitor);
                    accept(type, "type", visitor, CodeableConcept.class);
                    accept(onAdmission, "onAdmission", visitor);
                    accept(packageCode, "packageCode", visitor, CodeableConcept.class);
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
            Diagnosis other = (Diagnosis) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(sequence, other.sequence) && 
                Objects.equals(condition, other.condition) && 
                Objects.equals(dateOfDiagnosis, other.dateOfDiagnosis) && 
                Objects.equals(type, other.type) && 
                Objects.equals(onAdmission, other.onAdmission) && 
                Objects.equals(packageCode, other.packageCode);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    sequence, 
                    condition, 
                    dateOfDiagnosis, 
                    type, 
                    onAdmission, 
                    packageCode);
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
            private PositiveInt sequence;
            private CodeableReference condition;
            private DateTime dateOfDiagnosis;
            private List<CodeableConcept> type = new ArrayList<>();
            private Boolean onAdmission;
            private List<CodeableConcept> packageCode = new ArrayList<>();

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
             * Ranking of the diagnosis (for each type).
             * 
             * @param sequence
             *     Ranking of the diagnosis (for each type)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder sequence(PositiveInt sequence) {
                this.sequence = sequence;
                return this;
            }

            /**
             * The diagnosis relevant to the account.
             * 
             * <p>This element is required.
             * 
             * @param condition
             *     The diagnosis relevant to the account
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder condition(CodeableReference condition) {
                this.condition = condition;
                return this;
            }

            /**
             * Ranking of the diagnosis (for each type).
             * 
             * @param dateOfDiagnosis
             *     Date of the diagnosis (when coded diagnosis)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dateOfDiagnosis(DateTime dateOfDiagnosis) {
                this.dateOfDiagnosis = dateOfDiagnosis;
                return this;
            }

            /**
             * Type that this diagnosis has relevant to the account (e.g. admission, billing, discharge …).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Type that this diagnosis has relevant to the account (e.g. admission, billing, discharge …)
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
             * Type that this diagnosis has relevant to the account (e.g. admission, billing, discharge …).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     Type that this diagnosis has relevant to the account (e.g. admission, billing, discharge …)
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
             * Convenience method for setting {@code onAdmission}.
             * 
             * @param onAdmission
             *     Diagnosis present on Admission
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #onAdmission(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder onAdmission(java.lang.Boolean onAdmission) {
                this.onAdmission = (onAdmission == null) ? null : Boolean.of(onAdmission);
                return this;
            }

            /**
             * Was the Diagnosis present on Admission in the related Encounter.
             * 
             * @param onAdmission
             *     Diagnosis present on Admission
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder onAdmission(Boolean onAdmission) {
                this.onAdmission = onAdmission;
                return this;
            }

            /**
             * The package code can be used to group diagnoses that may be priced or delivered as a single product. Such as DRGs.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param packageCode
             *     Package Code specific for billing
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder packageCode(CodeableConcept... packageCode) {
                for (CodeableConcept value : packageCode) {
                    this.packageCode.add(value);
                }
                return this;
            }

            /**
             * The package code can be used to group diagnoses that may be priced or delivered as a single product. Such as DRGs.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param packageCode
             *     Package Code specific for billing
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder packageCode(Collection<CodeableConcept> packageCode) {
                this.packageCode = new ArrayList<>(packageCode);
                return this;
            }

            /**
             * Build the {@link Diagnosis}
             * 
             * <p>Required elements:
             * <ul>
             * <li>condition</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Diagnosis}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Diagnosis per the base specification
             */
            @Override
            public Diagnosis build() {
                Diagnosis diagnosis = new Diagnosis(this);
                if (validating) {
                    validate(diagnosis);
                }
                return diagnosis;
            }

            protected void validate(Diagnosis diagnosis) {
                super.validate(diagnosis);
                ValidationSupport.requireNonNull(diagnosis.condition, "condition");
                ValidationSupport.checkList(diagnosis.type, "type", CodeableConcept.class);
                ValidationSupport.checkList(diagnosis.packageCode, "packageCode", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(diagnosis);
            }

            protected Builder from(Diagnosis diagnosis) {
                super.from(diagnosis);
                sequence = diagnosis.sequence;
                condition = diagnosis.condition;
                dateOfDiagnosis = diagnosis.dateOfDiagnosis;
                type.addAll(diagnosis.type);
                onAdmission = diagnosis.onAdmission;
                packageCode.addAll(diagnosis.packageCode);
                return this;
            }
        }
    }

    /**
     * When using an account for billing a specific Encounter the set of procedures that are relevant for billing are stored 
     * here on the account where they are able to be sequenced appropriately prior to processing to produce claim(s).
     */
    public static class Procedure extends BackboneElement {
        private final PositiveInt sequence;
        @Summary
        @Binding(
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/procedure-code"
        )
        @Required
        private final CodeableReference code;
        private final DateTime dateOfService;
        @Binding(
            bindingName = "procedure-type",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Usage for the specific procedure - e.g. billing"
        )
        private final List<CodeableConcept> type;
        @Binding(
            bindingName = "procedure-package-code",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Local or Regional package codes, e.g. DRGs"
        )
        private final List<CodeableConcept> packageCode;
        @Summary
        @ReferenceTarget({ "Device" })
        private final List<Reference> device;

        private Procedure(Builder builder) {
            super(builder);
            sequence = builder.sequence;
            code = builder.code;
            dateOfService = builder.dateOfService;
            type = Collections.unmodifiableList(builder.type);
            packageCode = Collections.unmodifiableList(builder.packageCode);
            device = Collections.unmodifiableList(builder.device);
        }

        /**
         * Ranking of the procedure (for each type).
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getSequence() {
            return sequence;
        }

        /**
         * The procedure relevant to the account.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that is non-null.
         */
        public CodeableReference getCode() {
            return code;
        }

        /**
         * Date of the procedure when using a coded procedure. If using a reference to a procedure, then the date on the 
         * procedure should be used.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getDateOfService() {
            return dateOfService;
        }

        /**
         * How this procedure value should be used in charging the account.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * The package code can be used to group procedures that may be priced or delivered as a single product. Such as DRGs.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getPackageCode() {
            return packageCode;
        }

        /**
         * Any devices that were associated with the procedure relevant to the account.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getDevice() {
            return device;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (sequence != null) || 
                (code != null) || 
                (dateOfService != null) || 
                !type.isEmpty() || 
                !packageCode.isEmpty() || 
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
                    accept(sequence, "sequence", visitor);
                    accept(code, "code", visitor);
                    accept(dateOfService, "dateOfService", visitor);
                    accept(type, "type", visitor, CodeableConcept.class);
                    accept(packageCode, "packageCode", visitor, CodeableConcept.class);
                    accept(device, "device", visitor, Reference.class);
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
            Procedure other = (Procedure) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(sequence, other.sequence) && 
                Objects.equals(code, other.code) && 
                Objects.equals(dateOfService, other.dateOfService) && 
                Objects.equals(type, other.type) && 
                Objects.equals(packageCode, other.packageCode) && 
                Objects.equals(device, other.device);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    sequence, 
                    code, 
                    dateOfService, 
                    type, 
                    packageCode, 
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
            private PositiveInt sequence;
            private CodeableReference code;
            private DateTime dateOfService;
            private List<CodeableConcept> type = new ArrayList<>();
            private List<CodeableConcept> packageCode = new ArrayList<>();
            private List<Reference> device = new ArrayList<>();

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
             * Ranking of the procedure (for each type).
             * 
             * @param sequence
             *     Ranking of the procedure (for each type)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder sequence(PositiveInt sequence) {
                this.sequence = sequence;
                return this;
            }

            /**
             * The procedure relevant to the account.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     The procedure relevant to the account
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableReference code) {
                this.code = code;
                return this;
            }

            /**
             * Date of the procedure when using a coded procedure. If using a reference to a procedure, then the date on the 
             * procedure should be used.
             * 
             * @param dateOfService
             *     Date of the procedure (when coded procedure)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dateOfService(DateTime dateOfService) {
                this.dateOfService = dateOfService;
                return this;
            }

            /**
             * How this procedure value should be used in charging the account.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     How this procedure value should be used in charging the account
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
             * How this procedure value should be used in charging the account.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     How this procedure value should be used in charging the account
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
             * The package code can be used to group procedures that may be priced or delivered as a single product. Such as DRGs.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param packageCode
             *     Package Code specific for billing
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder packageCode(CodeableConcept... packageCode) {
                for (CodeableConcept value : packageCode) {
                    this.packageCode.add(value);
                }
                return this;
            }

            /**
             * The package code can be used to group procedures that may be priced or delivered as a single product. Such as DRGs.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param packageCode
             *     Package Code specific for billing
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder packageCode(Collection<CodeableConcept> packageCode) {
                this.packageCode = new ArrayList<>(packageCode);
                return this;
            }

            /**
             * Any devices that were associated with the procedure relevant to the account.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Device}</li>
             * </ul>
             * 
             * @param device
             *     Any devices that were associated with the procedure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder device(Reference... device) {
                for (Reference value : device) {
                    this.device.add(value);
                }
                return this;
            }

            /**
             * Any devices that were associated with the procedure relevant to the account.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Device}</li>
             * </ul>
             * 
             * @param device
             *     Any devices that were associated with the procedure
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder device(Collection<Reference> device) {
                this.device = new ArrayList<>(device);
                return this;
            }

            /**
             * Build the {@link Procedure}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Procedure}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Procedure per the base specification
             */
            @Override
            public Procedure build() {
                Procedure procedure = new Procedure(this);
                if (validating) {
                    validate(procedure);
                }
                return procedure;
            }

            protected void validate(Procedure procedure) {
                super.validate(procedure);
                ValidationSupport.requireNonNull(procedure.code, "code");
                ValidationSupport.checkList(procedure.type, "type", CodeableConcept.class);
                ValidationSupport.checkList(procedure.packageCode, "packageCode", CodeableConcept.class);
                ValidationSupport.checkList(procedure.device, "device", Reference.class);
                ValidationSupport.checkReferenceType(procedure.device, "device", "Device");
                ValidationSupport.requireValueOrChildren(procedure);
            }

            protected Builder from(Procedure procedure) {
                super.from(procedure);
                sequence = procedure.sequence;
                code = procedure.code;
                dateOfService = procedure.dateOfService;
                type.addAll(procedure.type);
                packageCode.addAll(procedure.packageCode);
                device.addAll(procedure.device);
                return this;
            }
        }
    }

    /**
     * Other associated accounts related to this account.
     */
    public static class RelatedAccount extends BackboneElement {
        @Binding(
            bindingName = "AccountRelationship",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Indicates the relationship between the accounts",
            valueSet = "http://hl7.org/fhir/ValueSet/account-relationship"
        )
        private final CodeableConcept relationship;
        @ReferenceTarget({ "Account" })
        @Required
        private final Reference account;

        private RelatedAccount(Builder builder) {
            super(builder);
            relationship = builder.relationship;
            account = builder.account;
        }

        /**
         * Relationship of the associated Account.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRelationship() {
            return relationship;
        }

        /**
         * Reference to an associated Account.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getAccount() {
            return account;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (relationship != null) || 
                (account != null);
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
                    accept(relationship, "relationship", visitor);
                    accept(account, "account", visitor);
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
            RelatedAccount other = (RelatedAccount) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(relationship, other.relationship) && 
                Objects.equals(account, other.account);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    relationship, 
                    account);
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
            private CodeableConcept relationship;
            private Reference account;

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
             * Relationship of the associated Account.
             * 
             * @param relationship
             *     Relationship of the associated Account
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relationship(CodeableConcept relationship) {
                this.relationship = relationship;
                return this;
            }

            /**
             * Reference to an associated Account.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Account}</li>
             * </ul>
             * 
             * @param account
             *     Reference to an associated Account
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder account(Reference account) {
                this.account = account;
                return this;
            }

            /**
             * Build the {@link RelatedAccount}
             * 
             * <p>Required elements:
             * <ul>
             * <li>account</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link RelatedAccount}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid RelatedAccount per the base specification
             */
            @Override
            public RelatedAccount build() {
                RelatedAccount relatedAccount = new RelatedAccount(this);
                if (validating) {
                    validate(relatedAccount);
                }
                return relatedAccount;
            }

            protected void validate(RelatedAccount relatedAccount) {
                super.validate(relatedAccount);
                ValidationSupport.requireNonNull(relatedAccount.account, "account");
                ValidationSupport.checkReferenceType(relatedAccount.account, "account", "Account");
                ValidationSupport.requireValueOrChildren(relatedAccount);
            }

            protected Builder from(RelatedAccount relatedAccount) {
                super.from(relatedAccount);
                relationship = relatedAccount.relationship;
                account = relatedAccount.account;
                return this;
            }
        }
    }

    /**
     * The calculated account balances - these are calculated and processed by the finance system.The balances with a 
     * `term` that is not current are usually generated/updated by an invoicing or similar process.
     */
    public static class Balance extends BackboneElement {
        @Binding(
            bindingName = "AccountAggregate",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Indicates the balance was outstanding at the given age.",
            valueSet = "http://hl7.org/fhir/ValueSet/account-aggregate"
        )
        private final CodeableConcept aggregate;
        @Binding(
            bindingName = "AccountBalanceTerm",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Indicates the balance was outstanding at the given age.",
            valueSet = "http://hl7.org/fhir/ValueSet/account-balance-term"
        )
        private final CodeableConcept term;
        private final Boolean estimate;
        @Required
        private final Money amount;

        private Balance(Builder builder) {
            super(builder);
            aggregate = builder.aggregate;
            term = builder.term;
            estimate = builder.estimate;
            amount = builder.amount;
        }

        /**
         * Who is expected to pay this part of the balance.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getAggregate() {
            return aggregate;
        }

        /**
         * The term of the account balances - The balance value is the amount that was outstanding for this age.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getTerm() {
            return term;
        }

        /**
         * The amount is only an estimated value - this is likely common for `current` term balances, but not with known terms 
         * (that were generated by a backend process).
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getEstimate() {
            return estimate;
        }

        /**
         * The actual balance value calculated for the age defined in the term property.
         * 
         * @return
         *     An immutable object of type {@link Money} that is non-null.
         */
        public Money getAmount() {
            return amount;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (aggregate != null) || 
                (term != null) || 
                (estimate != null) || 
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
                    accept(aggregate, "aggregate", visitor);
                    accept(term, "term", visitor);
                    accept(estimate, "estimate", visitor);
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
            Balance other = (Balance) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(aggregate, other.aggregate) && 
                Objects.equals(term, other.term) && 
                Objects.equals(estimate, other.estimate) && 
                Objects.equals(amount, other.amount);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    aggregate, 
                    term, 
                    estimate, 
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
            private CodeableConcept aggregate;
            private CodeableConcept term;
            private Boolean estimate;
            private Money amount;

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
             * Who is expected to pay this part of the balance.
             * 
             * @param aggregate
             *     Who is expected to pay this part of the balance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder aggregate(CodeableConcept aggregate) {
                this.aggregate = aggregate;
                return this;
            }

            /**
             * The term of the account balances - The balance value is the amount that was outstanding for this age.
             * 
             * @param term
             *     current | 30 | 60 | 90 | 120
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder term(CodeableConcept term) {
                this.term = term;
                return this;
            }

            /**
             * Convenience method for setting {@code estimate}.
             * 
             * @param estimate
             *     Estimated balance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #estimate(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder estimate(java.lang.Boolean estimate) {
                this.estimate = (estimate == null) ? null : Boolean.of(estimate);
                return this;
            }

            /**
             * The amount is only an estimated value - this is likely common for `current` term balances, but not with known terms 
             * (that were generated by a backend process).
             * 
             * @param estimate
             *     Estimated balance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder estimate(Boolean estimate) {
                this.estimate = estimate;
                return this;
            }

            /**
             * The actual balance value calculated for the age defined in the term property.
             * 
             * <p>This element is required.
             * 
             * @param amount
             *     Calculated amount
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder amount(Money amount) {
                this.amount = amount;
                return this;
            }

            /**
             * Build the {@link Balance}
             * 
             * <p>Required elements:
             * <ul>
             * <li>amount</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Balance}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Balance per the base specification
             */
            @Override
            public Balance build() {
                Balance balance = new Balance(this);
                if (validating) {
                    validate(balance);
                }
                return balance;
            }

            protected void validate(Balance balance) {
                super.validate(balance);
                ValidationSupport.requireNonNull(balance.amount, "amount");
                ValidationSupport.requireValueOrChildren(balance);
            }

            protected Builder from(Balance balance) {
                super.from(balance);
                aggregate = balance.aggregate;
                term = balance.term;
                estimate = balance.estimate;
                amount = balance.amount;
                return this;
            }
        }
    }
}
