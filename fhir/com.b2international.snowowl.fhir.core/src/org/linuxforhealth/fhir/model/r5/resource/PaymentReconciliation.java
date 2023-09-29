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
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Money;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.NoteType;
import org.linuxforhealth.fhir.model.r5.type.code.PaymentOutcome;
import org.linuxforhealth.fhir.model.r5.type.code.PaymentReconciliationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This resource provides the details including amount of a payment and allocates the payment items being paid.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "paymentReconciliation-0",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/payment-type",
    expression = "type.exists() and type.memberOf('http://hl7.org/fhir/ValueSet/payment-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/PaymentReconciliation",
    generated = true
)
@Constraint(
    id = "paymentReconciliation-1",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/payment-kind",
    expression = "kind.exists() implies (kind.memberOf('http://hl7.org/fhir/ValueSet/payment-kind', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/PaymentReconciliation",
    generated = true
)
@Constraint(
    id = "paymentReconciliation-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/payment-issuertype",
    expression = "issuerType.exists() implies (issuerType.memberOf('http://hl7.org/fhir/ValueSet/payment-issuertype', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/PaymentReconciliation",
    generated = true
)
@Constraint(
    id = "paymentReconciliation-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/v2-0570",
    expression = "method.exists() implies (method.memberOf('http://terminology.hl7.org/ValueSet/v2-0570', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/PaymentReconciliation",
    generated = true
)
@Constraint(
    id = "paymentReconciliation-4",
    level = "Warning",
    location = "allocation.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/payment-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/payment-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/PaymentReconciliation",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class PaymentReconciliation extends DomainResource {
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "PaymentType",
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/payment-type"
    )
    @Required
    private final CodeableConcept type;
    @Summary
    @Binding(
        bindingName = "PaymentReconciliationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A code specifying the state of the resource instance.",
        valueSet = "http://hl7.org/fhir/ValueSet/fm-status|5.0.0"
    )
    @Required
    private final PaymentReconciliationStatus status;
    @Binding(
        bindingName = "PaymentKind",
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/payment-kind"
    )
    private final CodeableConcept kind;
    @Summary
    private final Period period;
    @Summary
    @Required
    private final DateTime created;
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization" })
    private final Reference enterer;
    @Binding(
        bindingName = "PaymentIssuerType",
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/payment-issuertype"
    )
    private final CodeableConcept issuerType;
    @Summary
    @ReferenceTarget({ "Organization", "Patient", "RelatedPerson" })
    private final Reference paymentIssuer;
    @ReferenceTarget({ "Task" })
    private final Reference request;
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization" })
    private final Reference requestor;
    @Binding(
        bindingName = "PaymentOutcome",
        strength = BindingStrength.Value.REQUIRED,
        description = "The outcome of the processing.",
        valueSet = "http://hl7.org/fhir/ValueSet/payment-outcome|5.0.0"
    )
    private final PaymentOutcome outcome;
    private final String disposition;
    @Summary
    @Required
    private final Date date;
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @Binding(
        bindingName = "PaymentMethod",
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://terminology.hl7.org/ValueSet/v2-0570"
    )
    private final CodeableConcept method;
    private final String cardBrand;
    private final String accountNumber;
    private final Date expirationDate;
    private final String processor;
    private final String referenceNumber;
    private final String authorization;
    private final Money tenderedAmount;
    private final Money returnedAmount;
    @Summary
    @Required
    private final Money amount;
    private final Identifier paymentIdentifier;
    private final List<Allocation> allocation;
    @Binding(
        bindingName = "Forms",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The forms codes.",
        valueSet = "http://hl7.org/fhir/ValueSet/forms"
    )
    private final CodeableConcept formCode;
    private final List<ProcessNote> processNote;

    private PaymentReconciliation(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        type = builder.type;
        status = builder.status;
        kind = builder.kind;
        period = builder.period;
        created = builder.created;
        enterer = builder.enterer;
        issuerType = builder.issuerType;
        paymentIssuer = builder.paymentIssuer;
        request = builder.request;
        requestor = builder.requestor;
        outcome = builder.outcome;
        disposition = builder.disposition;
        date = builder.date;
        location = builder.location;
        method = builder.method;
        cardBrand = builder.cardBrand;
        accountNumber = builder.accountNumber;
        expirationDate = builder.expirationDate;
        processor = builder.processor;
        referenceNumber = builder.referenceNumber;
        authorization = builder.authorization;
        tenderedAmount = builder.tenderedAmount;
        returnedAmount = builder.returnedAmount;
        amount = builder.amount;
        paymentIdentifier = builder.paymentIdentifier;
        allocation = Collections.unmodifiableList(builder.allocation);
        formCode = builder.formCode;
        processNote = Collections.unmodifiableList(builder.processNote);
    }

    /**
     * A unique identifier assigned to this payment reconciliation.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Code to indicate the nature of the payment such as payment, adjustment.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * The status of the resource instance.
     * 
     * @return
     *     An immutable object of type {@link PaymentReconciliationStatus} that is non-null.
     */
    public PaymentReconciliationStatus getStatus() {
        return status;
    }

    /**
     * The workflow or activity which gave rise to or during which the payment ocurred such as a kiosk, deposit on account, 
     * periodic payment etc.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getKind() {
        return kind;
    }

    /**
     * The period of time for which payments have been gathered into this bulk payment for settlement.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * The date when the resource was created.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that is non-null.
     */
    public DateTime getCreated() {
        return created;
    }

    /**
     * Payment enterer if not the actual payment issuer.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEnterer() {
        return enterer;
    }

    /**
     * The type of the source such as patient or insurance.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getIssuerType() {
        return issuerType;
    }

    /**
     * The party who generated the payment.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getPaymentIssuer() {
        return paymentIssuer;
    }

    /**
     * Original request resource reference.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequest() {
        return request;
    }

    /**
     * The practitioner who is responsible for the services rendered to the patient.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequestor() {
        return requestor;
    }

    /**
     * The outcome of a request for a reconciliation.
     * 
     * @return
     *     An immutable object of type {@link PaymentOutcome} that may be null.
     */
    public PaymentOutcome getOutcome() {
        return outcome;
    }

    /**
     * A human readable description of the status of the request for the reconciliation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * The date of payment as indicated on the financial instrument.
     * 
     * @return
     *     An immutable object of type {@link Date} that is non-null.
     */
    public Date getDate() {
        return date;
    }

    /**
     * The location of the site or device for electronic transfers or physical location for cash payments.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * The means of payment such as check, card cash, or electronic funds transfer.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getMethod() {
        return method;
    }

    /**
     * The card brand such as debit, Visa, Amex etc. used if a card is the method of payment.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getCardBrand() {
        return cardBrand;
    }

    /**
     * A portion of the account number, often the last 4 digits, used for verification not charging purposes.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * The year and month (YYYY-MM) when the instrument, typically card, expires.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * The name of the card processor, etf processor, bank for checks.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getProcessor() {
        return processor;
    }

    /**
     * The check number, eft reference, car processor reference.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * An alphanumeric issued by the processor to confirm the successful issuance of payment.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getAuthorization() {
        return authorization;
    }

    /**
     * The amount offered by the issuer, typically applies to cash when the issuer provides an amount in bank note 
     * denominations equal to or excess of the amount actually being paid.
     * 
     * @return
     *     An immutable object of type {@link Money} that may be null.
     */
    public Money getTenderedAmount() {
        return tenderedAmount;
    }

    /**
     * The amount returned by the receiver which is excess to the amount payable, often referred to as 'change'.
     * 
     * @return
     *     An immutable object of type {@link Money} that may be null.
     */
    public Money getReturnedAmount() {
        return returnedAmount;
    }

    /**
     * Total payment amount as indicated on the financial instrument.
     * 
     * @return
     *     An immutable object of type {@link Money} that is non-null.
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * Issuer's unique identifier for the payment instrument.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getPaymentIdentifier() {
        return paymentIdentifier;
    }

    /**
     * Distribution of the payment amount for a previously acknowledged payable.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Allocation} that may be empty.
     */
    public List<Allocation> getAllocation() {
        return allocation;
    }

    /**
     * A code for the form to be used for printing the content.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getFormCode() {
        return formCode;
    }

    /**
     * A note that describes or explains the processing in a human readable form.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ProcessNote} that may be empty.
     */
    public List<ProcessNote> getProcessNote() {
        return processNote;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (type != null) || 
            (status != null) || 
            (kind != null) || 
            (period != null) || 
            (created != null) || 
            (enterer != null) || 
            (issuerType != null) || 
            (paymentIssuer != null) || 
            (request != null) || 
            (requestor != null) || 
            (outcome != null) || 
            (disposition != null) || 
            (date != null) || 
            (location != null) || 
            (method != null) || 
            (cardBrand != null) || 
            (accountNumber != null) || 
            (expirationDate != null) || 
            (processor != null) || 
            (referenceNumber != null) || 
            (authorization != null) || 
            (tenderedAmount != null) || 
            (returnedAmount != null) || 
            (amount != null) || 
            (paymentIdentifier != null) || 
            !allocation.isEmpty() || 
            (formCode != null) || 
            !processNote.isEmpty();
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
                accept(status, "status", visitor);
                accept(kind, "kind", visitor);
                accept(period, "period", visitor);
                accept(created, "created", visitor);
                accept(enterer, "enterer", visitor);
                accept(issuerType, "issuerType", visitor);
                accept(paymentIssuer, "paymentIssuer", visitor);
                accept(request, "request", visitor);
                accept(requestor, "requestor", visitor);
                accept(outcome, "outcome", visitor);
                accept(disposition, "disposition", visitor);
                accept(date, "date", visitor);
                accept(location, "location", visitor);
                accept(method, "method", visitor);
                accept(cardBrand, "cardBrand", visitor);
                accept(accountNumber, "accountNumber", visitor);
                accept(expirationDate, "expirationDate", visitor);
                accept(processor, "processor", visitor);
                accept(referenceNumber, "referenceNumber", visitor);
                accept(authorization, "authorization", visitor);
                accept(tenderedAmount, "tenderedAmount", visitor);
                accept(returnedAmount, "returnedAmount", visitor);
                accept(amount, "amount", visitor);
                accept(paymentIdentifier, "paymentIdentifier", visitor);
                accept(allocation, "allocation", visitor, Allocation.class);
                accept(formCode, "formCode", visitor);
                accept(processNote, "processNote", visitor, ProcessNote.class);
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
        PaymentReconciliation other = (PaymentReconciliation) obj;
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
            Objects.equals(status, other.status) && 
            Objects.equals(kind, other.kind) && 
            Objects.equals(period, other.period) && 
            Objects.equals(created, other.created) && 
            Objects.equals(enterer, other.enterer) && 
            Objects.equals(issuerType, other.issuerType) && 
            Objects.equals(paymentIssuer, other.paymentIssuer) && 
            Objects.equals(request, other.request) && 
            Objects.equals(requestor, other.requestor) && 
            Objects.equals(outcome, other.outcome) && 
            Objects.equals(disposition, other.disposition) && 
            Objects.equals(date, other.date) && 
            Objects.equals(location, other.location) && 
            Objects.equals(method, other.method) && 
            Objects.equals(cardBrand, other.cardBrand) && 
            Objects.equals(accountNumber, other.accountNumber) && 
            Objects.equals(expirationDate, other.expirationDate) && 
            Objects.equals(processor, other.processor) && 
            Objects.equals(referenceNumber, other.referenceNumber) && 
            Objects.equals(authorization, other.authorization) && 
            Objects.equals(tenderedAmount, other.tenderedAmount) && 
            Objects.equals(returnedAmount, other.returnedAmount) && 
            Objects.equals(amount, other.amount) && 
            Objects.equals(paymentIdentifier, other.paymentIdentifier) && 
            Objects.equals(allocation, other.allocation) && 
            Objects.equals(formCode, other.formCode) && 
            Objects.equals(processNote, other.processNote);
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
                status, 
                kind, 
                period, 
                created, 
                enterer, 
                issuerType, 
                paymentIssuer, 
                request, 
                requestor, 
                outcome, 
                disposition, 
                date, 
                location, 
                method, 
                cardBrand, 
                accountNumber, 
                expirationDate, 
                processor, 
                referenceNumber, 
                authorization, 
                tenderedAmount, 
                returnedAmount, 
                amount, 
                paymentIdentifier, 
                allocation, 
                formCode, 
                processNote);
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
        private CodeableConcept type;
        private PaymentReconciliationStatus status;
        private CodeableConcept kind;
        private Period period;
        private DateTime created;
        private Reference enterer;
        private CodeableConcept issuerType;
        private Reference paymentIssuer;
        private Reference request;
        private Reference requestor;
        private PaymentOutcome outcome;
        private String disposition;
        private Date date;
        private Reference location;
        private CodeableConcept method;
        private String cardBrand;
        private String accountNumber;
        private Date expirationDate;
        private String processor;
        private String referenceNumber;
        private String authorization;
        private Money tenderedAmount;
        private Money returnedAmount;
        private Money amount;
        private Identifier paymentIdentifier;
        private List<Allocation> allocation = new ArrayList<>();
        private CodeableConcept formCode;
        private List<ProcessNote> processNote = new ArrayList<>();

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
         * A unique identifier assigned to this payment reconciliation.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for a payment reconciliation
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
         * A unique identifier assigned to this payment reconciliation.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for a payment reconciliation
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
         * Code to indicate the nature of the payment such as payment, adjustment.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     Category of payment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
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
        public Builder status(PaymentReconciliationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The workflow or activity which gave rise to or during which the payment ocurred such as a kiosk, deposit on account, 
         * periodic payment etc.
         * 
         * @param kind
         *     Workflow originating payment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder kind(CodeableConcept kind) {
            this.kind = kind;
            return this;
        }

        /**
         * The period of time for which payments have been gathered into this bulk payment for settlement.
         * 
         * @param period
         *     Period covered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * The date when the resource was created.
         * 
         * <p>This element is required.
         * 
         * @param created
         *     Creation date
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder created(DateTime created) {
            this.created = created;
            return this;
        }

        /**
         * Payment enterer if not the actual payment issuer.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param enterer
         *     Who entered the payment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder enterer(Reference enterer) {
            this.enterer = enterer;
            return this;
        }

        /**
         * The type of the source such as patient or insurance.
         * 
         * @param issuerType
         *     Nature of the source
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder issuerType(CodeableConcept issuerType) {
            this.issuerType = issuerType;
            return this;
        }

        /**
         * The party who generated the payment.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * </ul>
         * 
         * @param paymentIssuer
         *     Party generating payment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder paymentIssuer(Reference paymentIssuer) {
            this.paymentIssuer = paymentIssuer;
            return this;
        }

        /**
         * Original request resource reference.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param request
         *     Reference to requesting resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder request(Reference request) {
            this.request = request;
            return this;
        }

        /**
         * The practitioner who is responsible for the services rendered to the patient.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param requestor
         *     Responsible practitioner
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requestor(Reference requestor) {
            this.requestor = requestor;
            return this;
        }

        /**
         * The outcome of a request for a reconciliation.
         * 
         * @param outcome
         *     queued | complete | error | partial
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder outcome(PaymentOutcome outcome) {
            this.outcome = outcome;
            return this;
        }

        /**
         * Convenience method for setting {@code disposition}.
         * 
         * @param disposition
         *     Disposition message
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #disposition(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder disposition(java.lang.String disposition) {
            this.disposition = (disposition == null) ? null : String.of(disposition);
            return this;
        }

        /**
         * A human readable description of the status of the request for the reconciliation.
         * 
         * @param disposition
         *     Disposition message
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder disposition(String disposition) {
            this.disposition = disposition;
            return this;
        }

        /**
         * Convenience method for setting {@code date}.
         * 
         * <p>This element is required.
         * 
         * @param date
         *     When payment issued
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #date(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder date(java.time.LocalDate date) {
            this.date = (date == null) ? null : Date.of(date);
            return this;
        }

        /**
         * The date of payment as indicated on the financial instrument.
         * 
         * <p>This element is required.
         * 
         * @param date
         *     When payment issued
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        /**
         * The location of the site or device for electronic transfers or physical location for cash payments.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where payment collected
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * The means of payment such as check, card cash, or electronic funds transfer.
         * 
         * @param method
         *     Payment instrument
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder method(CodeableConcept method) {
            this.method = method;
            return this;
        }

        /**
         * Convenience method for setting {@code cardBrand}.
         * 
         * @param cardBrand
         *     Type of card
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #cardBrand(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder cardBrand(java.lang.String cardBrand) {
            this.cardBrand = (cardBrand == null) ? null : String.of(cardBrand);
            return this;
        }

        /**
         * The card brand such as debit, Visa, Amex etc. used if a card is the method of payment.
         * 
         * @param cardBrand
         *     Type of card
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder cardBrand(String cardBrand) {
            this.cardBrand = cardBrand;
            return this;
        }

        /**
         * Convenience method for setting {@code accountNumber}.
         * 
         * @param accountNumber
         *     Digits for verification
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #accountNumber(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder accountNumber(java.lang.String accountNumber) {
            this.accountNumber = (accountNumber == null) ? null : String.of(accountNumber);
            return this;
        }

        /**
         * A portion of the account number, often the last 4 digits, used for verification not charging purposes.
         * 
         * @param accountNumber
         *     Digits for verification
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        /**
         * Convenience method for setting {@code expirationDate}.
         * 
         * @param expirationDate
         *     Expiration year-month
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #expirationDate(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder expirationDate(java.time.LocalDate expirationDate) {
            this.expirationDate = (expirationDate == null) ? null : Date.of(expirationDate);
            return this;
        }

        /**
         * The year and month (YYYY-MM) when the instrument, typically card, expires.
         * 
         * @param expirationDate
         *     Expiration year-month
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder expirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        /**
         * Convenience method for setting {@code processor}.
         * 
         * @param processor
         *     Processor name
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #processor(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder processor(java.lang.String processor) {
            this.processor = (processor == null) ? null : String.of(processor);
            return this;
        }

        /**
         * The name of the card processor, etf processor, bank for checks.
         * 
         * @param processor
         *     Processor name
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder processor(String processor) {
            this.processor = processor;
            return this;
        }

        /**
         * Convenience method for setting {@code referenceNumber}.
         * 
         * @param referenceNumber
         *     Check number or payment reference
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #referenceNumber(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder referenceNumber(java.lang.String referenceNumber) {
            this.referenceNumber = (referenceNumber == null) ? null : String.of(referenceNumber);
            return this;
        }

        /**
         * The check number, eft reference, car processor reference.
         * 
         * @param referenceNumber
         *     Check number or payment reference
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder referenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
            return this;
        }

        /**
         * Convenience method for setting {@code authorization}.
         * 
         * @param authorization
         *     Authorization number
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #authorization(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder authorization(java.lang.String authorization) {
            this.authorization = (authorization == null) ? null : String.of(authorization);
            return this;
        }

        /**
         * An alphanumeric issued by the processor to confirm the successful issuance of payment.
         * 
         * @param authorization
         *     Authorization number
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        /**
         * The amount offered by the issuer, typically applies to cash when the issuer provides an amount in bank note 
         * denominations equal to or excess of the amount actually being paid.
         * 
         * @param tenderedAmount
         *     Amount offered by the issuer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder tenderedAmount(Money tenderedAmount) {
            this.tenderedAmount = tenderedAmount;
            return this;
        }

        /**
         * The amount returned by the receiver which is excess to the amount payable, often referred to as 'change'.
         * 
         * @param returnedAmount
         *     Amount returned by the receiver
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder returnedAmount(Money returnedAmount) {
            this.returnedAmount = returnedAmount;
            return this;
        }

        /**
         * Total payment amount as indicated on the financial instrument.
         * 
         * <p>This element is required.
         * 
         * @param amount
         *     Total amount of Payment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Issuer's unique identifier for the payment instrument.
         * 
         * @param paymentIdentifier
         *     Business identifier for the payment
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder paymentIdentifier(Identifier paymentIdentifier) {
            this.paymentIdentifier = paymentIdentifier;
            return this;
        }

        /**
         * Distribution of the payment amount for a previously acknowledged payable.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param allocation
         *     Settlement particulars
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder allocation(Allocation... allocation) {
            for (Allocation value : allocation) {
                this.allocation.add(value);
            }
            return this;
        }

        /**
         * Distribution of the payment amount for a previously acknowledged payable.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param allocation
         *     Settlement particulars
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder allocation(Collection<Allocation> allocation) {
            this.allocation = new ArrayList<>(allocation);
            return this;
        }

        /**
         * A code for the form to be used for printing the content.
         * 
         * @param formCode
         *     Printed form identifier
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder formCode(CodeableConcept formCode) {
            this.formCode = formCode;
            return this;
        }

        /**
         * A note that describes or explains the processing in a human readable form.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param processNote
         *     Note concerning processing
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder processNote(ProcessNote... processNote) {
            for (ProcessNote value : processNote) {
                this.processNote.add(value);
            }
            return this;
        }

        /**
         * A note that describes or explains the processing in a human readable form.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param processNote
         *     Note concerning processing
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder processNote(Collection<ProcessNote> processNote) {
            this.processNote = new ArrayList<>(processNote);
            return this;
        }

        /**
         * Build the {@link PaymentReconciliation}
         * 
         * <p>Required elements:
         * <ul>
         * <li>type</li>
         * <li>status</li>
         * <li>created</li>
         * <li>date</li>
         * <li>amount</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link PaymentReconciliation}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid PaymentReconciliation per the base specification
         */
        @Override
        public PaymentReconciliation build() {
            PaymentReconciliation paymentReconciliation = new PaymentReconciliation(this);
            if (validating) {
                validate(paymentReconciliation);
            }
            return paymentReconciliation;
        }

        protected void validate(PaymentReconciliation paymentReconciliation) {
            super.validate(paymentReconciliation);
            ValidationSupport.checkList(paymentReconciliation.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(paymentReconciliation.type, "type");
            ValidationSupport.requireNonNull(paymentReconciliation.status, "status");
            ValidationSupport.requireNonNull(paymentReconciliation.created, "created");
            ValidationSupport.requireNonNull(paymentReconciliation.date, "date");
            ValidationSupport.requireNonNull(paymentReconciliation.amount, "amount");
            ValidationSupport.checkList(paymentReconciliation.allocation, "allocation", Allocation.class);
            ValidationSupport.checkList(paymentReconciliation.processNote, "processNote", ProcessNote.class);
            ValidationSupport.checkReferenceType(paymentReconciliation.enterer, "enterer", "Practitioner", "PractitionerRole", "Organization");
            ValidationSupport.checkReferenceType(paymentReconciliation.paymentIssuer, "paymentIssuer", "Organization", "Patient", "RelatedPerson");
            ValidationSupport.checkReferenceType(paymentReconciliation.request, "request", "Task");
            ValidationSupport.checkReferenceType(paymentReconciliation.requestor, "requestor", "Practitioner", "PractitionerRole", "Organization");
            ValidationSupport.checkReferenceType(paymentReconciliation.location, "location", "Location");
        }

        protected Builder from(PaymentReconciliation paymentReconciliation) {
            super.from(paymentReconciliation);
            identifier.addAll(paymentReconciliation.identifier);
            type = paymentReconciliation.type;
            status = paymentReconciliation.status;
            kind = paymentReconciliation.kind;
            period = paymentReconciliation.period;
            created = paymentReconciliation.created;
            enterer = paymentReconciliation.enterer;
            issuerType = paymentReconciliation.issuerType;
            paymentIssuer = paymentReconciliation.paymentIssuer;
            request = paymentReconciliation.request;
            requestor = paymentReconciliation.requestor;
            outcome = paymentReconciliation.outcome;
            disposition = paymentReconciliation.disposition;
            date = paymentReconciliation.date;
            location = paymentReconciliation.location;
            method = paymentReconciliation.method;
            cardBrand = paymentReconciliation.cardBrand;
            accountNumber = paymentReconciliation.accountNumber;
            expirationDate = paymentReconciliation.expirationDate;
            processor = paymentReconciliation.processor;
            referenceNumber = paymentReconciliation.referenceNumber;
            authorization = paymentReconciliation.authorization;
            tenderedAmount = paymentReconciliation.tenderedAmount;
            returnedAmount = paymentReconciliation.returnedAmount;
            amount = paymentReconciliation.amount;
            paymentIdentifier = paymentReconciliation.paymentIdentifier;
            allocation.addAll(paymentReconciliation.allocation);
            formCode = paymentReconciliation.formCode;
            processNote.addAll(paymentReconciliation.processNote);
            return this;
        }
    }

    /**
     * Distribution of the payment amount for a previously acknowledged payable.
     */
    public static class Allocation extends BackboneElement {
        private final Identifier identifier;
        private final Identifier predecessor;
        @ReferenceTarget({ "Claim", "Account", "Invoice", "ChargeItem", "Encounter", "Contract" })
        private final Reference target;
        @Choice({ String.class, Identifier.class, PositiveInt.class })
        private final Element targetItem;
        @ReferenceTarget({ "Encounter" })
        private final Reference encounter;
        @ReferenceTarget({ "Account" })
        private final Reference account;
        @Binding(
            bindingName = "PaymentType",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://hl7.org/fhir/ValueSet/payment-type"
        )
        private final CodeableConcept type;
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization" })
        private final Reference submitter;
        @ReferenceTarget({ "ClaimResponse" })
        private final Reference response;
        private final Date date;
        @ReferenceTarget({ "PractitionerRole" })
        private final Reference responsible;
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization" })
        private final Reference payee;
        private final Money amount;

        private Allocation(Builder builder) {
            super(builder);
            identifier = builder.identifier;
            predecessor = builder.predecessor;
            target = builder.target;
            targetItem = builder.targetItem;
            encounter = builder.encounter;
            account = builder.account;
            type = builder.type;
            submitter = builder.submitter;
            response = builder.response;
            date = builder.date;
            responsible = builder.responsible;
            payee = builder.payee;
            amount = builder.amount;
        }

        /**
         * Unique identifier for the current payment item for the referenced payable.
         * 
         * @return
         *     An immutable object of type {@link Identifier} that may be null.
         */
        public Identifier getIdentifier() {
            return identifier;
        }

        /**
         * Unique identifier for the prior payment item for the referenced payable.
         * 
         * @return
         *     An immutable object of type {@link Identifier} that may be null.
         */
        public Identifier getPredecessor() {
            return predecessor;
        }

        /**
         * Specific resource to which the payment/adjustment/advance applies.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getTarget() {
            return target;
        }

        /**
         *  Identifies the claim line item, encounter or other sub-element being paid. Note payment may be partial, that is not 
         * match the then outstanding balance or amount incurred.
         * 
         * @return
         *     An immutable object of type {@link String}, {@link Identifier} or {@link PositiveInt} that may be null.
         */
        public Element getTargetItem() {
            return targetItem;
        }

        /**
         * The Encounter to which this payment applies, may be completed by the receiver, used for search.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getEncounter() {
            return encounter;
        }

        /**
         * The Account to which this payment applies, may be completed by the receiver, used for search.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getAccount() {
            return account;
        }

        /**
         * Code to indicate the nature of the payment.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The party which submitted the claim or financial transaction.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getSubmitter() {
            return submitter;
        }

        /**
         * A resource, such as a ClaimResponse, which contains a commitment to payment.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getResponse() {
            return response;
        }

        /**
         * The date from the response resource containing a commitment to pay.
         * 
         * @return
         *     An immutable object of type {@link Date} that may be null.
         */
        public Date getDate() {
            return date;
        }

        /**
         * A reference to the individual who is responsible for inquiries regarding the response and its payment.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getResponsible() {
            return responsible;
        }

        /**
         * The party which is receiving the payment.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getPayee() {
            return payee;
        }

        /**
         * The monetary amount allocated from the total payment to the payable.
         * 
         * @return
         *     An immutable object of type {@link Money} that may be null.
         */
        public Money getAmount() {
            return amount;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (identifier != null) || 
                (predecessor != null) || 
                (target != null) || 
                (targetItem != null) || 
                (encounter != null) || 
                (account != null) || 
                (type != null) || 
                (submitter != null) || 
                (response != null) || 
                (date != null) || 
                (responsible != null) || 
                (payee != null) || 
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
                    accept(identifier, "identifier", visitor);
                    accept(predecessor, "predecessor", visitor);
                    accept(target, "target", visitor);
                    accept(targetItem, "targetItem", visitor);
                    accept(encounter, "encounter", visitor);
                    accept(account, "account", visitor);
                    accept(type, "type", visitor);
                    accept(submitter, "submitter", visitor);
                    accept(response, "response", visitor);
                    accept(date, "date", visitor);
                    accept(responsible, "responsible", visitor);
                    accept(payee, "payee", visitor);
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
            Allocation other = (Allocation) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(identifier, other.identifier) && 
                Objects.equals(predecessor, other.predecessor) && 
                Objects.equals(target, other.target) && 
                Objects.equals(targetItem, other.targetItem) && 
                Objects.equals(encounter, other.encounter) && 
                Objects.equals(account, other.account) && 
                Objects.equals(type, other.type) && 
                Objects.equals(submitter, other.submitter) && 
                Objects.equals(response, other.response) && 
                Objects.equals(date, other.date) && 
                Objects.equals(responsible, other.responsible) && 
                Objects.equals(payee, other.payee) && 
                Objects.equals(amount, other.amount);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    identifier, 
                    predecessor, 
                    target, 
                    targetItem, 
                    encounter, 
                    account, 
                    type, 
                    submitter, 
                    response, 
                    date, 
                    responsible, 
                    payee, 
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
            private Identifier identifier;
            private Identifier predecessor;
            private Reference target;
            private Element targetItem;
            private Reference encounter;
            private Reference account;
            private CodeableConcept type;
            private Reference submitter;
            private Reference response;
            private Date date;
            private Reference responsible;
            private Reference payee;
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
             * Unique identifier for the current payment item for the referenced payable.
             * 
             * @param identifier
             *     Business identifier of the payment detail
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder identifier(Identifier identifier) {
                this.identifier = identifier;
                return this;
            }

            /**
             * Unique identifier for the prior payment item for the referenced payable.
             * 
             * @param predecessor
             *     Business identifier of the prior payment detail
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder predecessor(Identifier predecessor) {
                this.predecessor = predecessor;
                return this;
            }

            /**
             * Specific resource to which the payment/adjustment/advance applies.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Claim}</li>
             * <li>{@link Account}</li>
             * <li>{@link Invoice}</li>
             * <li>{@link ChargeItem}</li>
             * <li>{@link Encounter}</li>
             * <li>{@link Contract}</li>
             * </ul>
             * 
             * @param target
             *     Subject of the payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder target(Reference target) {
                this.target = target;
                return this;
            }

            /**
             * Convenience method for setting {@code targetItem} with choice type String.
             * 
             * @param targetItem
             *     Sub-element of the subject
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #targetItem(Element)
             */
            public Builder targetItem(java.lang.String targetItem) {
                this.targetItem = (targetItem == null) ? null : String.of(targetItem);
                return this;
            }

            /**
             *  Identifies the claim line item, encounter or other sub-element being paid. Note payment may be partial, that is not 
             * match the then outstanding balance or amount incurred.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link String}</li>
             * <li>{@link Identifier}</li>
             * <li>{@link PositiveInt}</li>
             * </ul>
             * 
             * @param targetItem
             *     Sub-element of the subject
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder targetItem(Element targetItem) {
                this.targetItem = targetItem;
                return this;
            }

            /**
             * The Encounter to which this payment applies, may be completed by the receiver, used for search.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Encounter}</li>
             * </ul>
             * 
             * @param encounter
             *     Applied-to encounter
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder encounter(Reference encounter) {
                this.encounter = encounter;
                return this;
            }

            /**
             * The Account to which this payment applies, may be completed by the receiver, used for search.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Account}</li>
             * </ul>
             * 
             * @param account
             *     Applied-to account
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder account(Reference account) {
                this.account = account;
                return this;
            }

            /**
             * Code to indicate the nature of the payment.
             * 
             * @param type
             *     Category of payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The party which submitted the claim or financial transaction.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param submitter
             *     Submitter of the request
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder submitter(Reference submitter) {
                this.submitter = submitter;
                return this;
            }

            /**
             * A resource, such as a ClaimResponse, which contains a commitment to payment.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link ClaimResponse}</li>
             * </ul>
             * 
             * @param response
             *     Response committing to a payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder response(Reference response) {
                this.response = response;
                return this;
            }

            /**
             * Convenience method for setting {@code date}.
             * 
             * @param date
             *     Date of commitment to pay
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #date(org.linuxforhealth.fhir.model.type.Date)
             */
            public Builder date(java.time.LocalDate date) {
                this.date = (date == null) ? null : Date.of(date);
                return this;
            }

            /**
             * The date from the response resource containing a commitment to pay.
             * 
             * @param date
             *     Date of commitment to pay
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder date(Date date) {
                this.date = date;
                return this;
            }

            /**
             * A reference to the individual who is responsible for inquiries regarding the response and its payment.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link PractitionerRole}</li>
             * </ul>
             * 
             * @param responsible
             *     Contact for the response
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder responsible(Reference responsible) {
                this.responsible = responsible;
                return this;
            }

            /**
             * The party which is receiving the payment.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param payee
             *     Recipient of the payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder payee(Reference payee) {
                this.payee = payee;
                return this;
            }

            /**
             * The monetary amount allocated from the total payment to the payable.
             * 
             * @param amount
             *     Amount allocated to this payable
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder amount(Money amount) {
                this.amount = amount;
                return this;
            }

            /**
             * Build the {@link Allocation}
             * 
             * @return
             *     An immutable object of type {@link Allocation}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Allocation per the base specification
             */
            @Override
            public Allocation build() {
                Allocation allocation = new Allocation(this);
                if (validating) {
                    validate(allocation);
                }
                return allocation;
            }

            protected void validate(Allocation allocation) {
                super.validate(allocation);
                ValidationSupport.choiceElement(allocation.targetItem, "targetItem", String.class, Identifier.class, PositiveInt.class);
                ValidationSupport.checkReferenceType(allocation.target, "target", "Claim", "Account", "Invoice", "ChargeItem", "Encounter", "Contract");
                ValidationSupport.checkReferenceType(allocation.encounter, "encounter", "Encounter");
                ValidationSupport.checkReferenceType(allocation.account, "account", "Account");
                ValidationSupport.checkReferenceType(allocation.submitter, "submitter", "Practitioner", "PractitionerRole", "Organization");
                ValidationSupport.checkReferenceType(allocation.response, "response", "ClaimResponse");
                ValidationSupport.checkReferenceType(allocation.responsible, "responsible", "PractitionerRole");
                ValidationSupport.checkReferenceType(allocation.payee, "payee", "Practitioner", "PractitionerRole", "Organization");
                ValidationSupport.requireValueOrChildren(allocation);
            }

            protected Builder from(Allocation allocation) {
                super.from(allocation);
                identifier = allocation.identifier;
                predecessor = allocation.predecessor;
                target = allocation.target;
                targetItem = allocation.targetItem;
                encounter = allocation.encounter;
                account = allocation.account;
                type = allocation.type;
                submitter = allocation.submitter;
                response = allocation.response;
                date = allocation.date;
                responsible = allocation.responsible;
                payee = allocation.payee;
                amount = allocation.amount;
                return this;
            }
        }
    }

    /**
     * A note that describes or explains the processing in a human readable form.
     */
    public static class ProcessNote extends BackboneElement {
        @Binding(
            bindingName = "NoteType",
            strength = BindingStrength.Value.REQUIRED,
            description = "The presentation types of notes.",
            valueSet = "http://hl7.org/fhir/ValueSet/note-type|5.0.0"
        )
        private final NoteType type;
        private final String text;

        private ProcessNote(Builder builder) {
            super(builder);
            type = builder.type;
            text = builder.text;
        }

        /**
         * The business purpose of the note text.
         * 
         * @return
         *     An immutable object of type {@link NoteType} that may be null.
         */
        public NoteType getType() {
            return type;
        }

        /**
         * The explanation or description associated with the processing.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getText() {
            return text;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (text != null);
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
                    accept(text, "text", visitor);
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
            ProcessNote other = (ProcessNote) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(text, other.text);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    text);
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
            private NoteType type;
            private String text;

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
             * The business purpose of the note text.
             * 
             * @param type
             *     display | print | printoper
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(NoteType type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code text}.
             * 
             * @param text
             *     Note explanatory text
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #text(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder text(java.lang.String text) {
                this.text = (text == null) ? null : String.of(text);
                return this;
            }

            /**
             * The explanation or description associated with the processing.
             * 
             * @param text
             *     Note explanatory text
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder text(String text) {
                this.text = text;
                return this;
            }

            /**
             * Build the {@link ProcessNote}
             * 
             * @return
             *     An immutable object of type {@link ProcessNote}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ProcessNote per the base specification
             */
            @Override
            public ProcessNote build() {
                ProcessNote processNote = new ProcessNote(this);
                if (validating) {
                    validate(processNote);
                }
                return processNote;
            }

            protected void validate(ProcessNote processNote) {
                super.validate(processNote);
                ValidationSupport.requireValueOrChildren(processNote);
            }

            protected Builder from(ProcessNote processNote) {
                super.from(processNote);
                type = processNote.type;
                text = processNote.text;
                return this;
            }
        }
    }
}
