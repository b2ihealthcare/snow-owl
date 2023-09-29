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
import org.linuxforhealth.fhir.model.r5.type.Address;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Decimal;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Money;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ClaimResponseStatus;
import org.linuxforhealth.fhir.model.r5.type.code.RemittanceOutcome;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.Use;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This resource provides the adjudication details from the processing of a Claim resource.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "claimResponse-0",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/claim-type",
    expression = "type.exists() and type.memberOf('http://hl7.org/fhir/ValueSet/claim-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ClaimResponse",
    generated = true
)
@Constraint(
    id = "claimResponse-1",
    level = "Warning",
    location = "processNote.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/note-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/note-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ClaimResponse",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ClaimResponse extends DomainResource {
    private final List<Identifier> identifier;
    private final List<Identifier> traceNumber;
    @Summary
    @Binding(
        bindingName = "ClaimResponseStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "A code specifying the state of the resource instance.",
        valueSet = "http://hl7.org/fhir/ValueSet/fm-status|5.0.0"
    )
    @Required
    private final ClaimResponseStatus status;
    @Summary
    @Binding(
        bindingName = "ClaimType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The type or discipline-style of the claim.",
        valueSet = "http://hl7.org/fhir/ValueSet/claim-type"
    )
    @Required
    private final CodeableConcept type;
    @Binding(
        bindingName = "ClaimSubType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A more granular claim typecode.",
        valueSet = "http://hl7.org/fhir/ValueSet/claim-subtype"
    )
    private final CodeableConcept subType;
    @Summary
    @Binding(
        bindingName = "Use",
        strength = BindingStrength.Value.REQUIRED,
        description = "Claim, preauthorization, predetermination.",
        valueSet = "http://hl7.org/fhir/ValueSet/claim-use|5.0.0"
    )
    @Required
    private final Use use;
    @Summary
    @ReferenceTarget({ "Patient" })
    @Required
    private final Reference patient;
    @Summary
    @Required
    private final DateTime created;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference insurer;
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization" })
    private final Reference requestor;
    @Summary
    @ReferenceTarget({ "Claim" })
    private final Reference request;
    @Summary
    @Binding(
        bindingName = "RemittanceOutcome",
        strength = BindingStrength.Value.REQUIRED,
        description = "The result of the claim processing.",
        valueSet = "http://hl7.org/fhir/ValueSet/claim-outcome|5.0.0"
    )
    @Required
    private final RemittanceOutcome outcome;
    @Summary
    @Binding(
        bindingName = "AdjudicationDecision",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The overall result of the claim adjudication.",
        valueSet = "http://hl7.org/fhir/ValueSet/claim-decision"
    )
    private final CodeableConcept decision;
    private final String disposition;
    private final String preAuthRef;
    private final Period preAuthPeriod;
    private final List<Event> event;
    @Binding(
        bindingName = "PayeeType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A code for the party to be reimbursed.",
        valueSet = "http://hl7.org/fhir/ValueSet/payeetype"
    )
    private final CodeableConcept payeeType;
    @ReferenceTarget({ "Encounter" })
    private final List<Reference> encounter;
    @Binding(
        bindingName = "DiagnosisRelatedGroup",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/ex-diagnosisrelatedgroup"
    )
    private final CodeableConcept diagnosisRelatedGroup;
    private final List<Item> item;
    private final List<AddItem> addItem;
    private final List<ClaimResponse.Item.Adjudication> adjudication;
    @Summary
    private final List<Total> total;
    private final Payment payment;
    @Binding(
        bindingName = "FundsReserve",
        strength = BindingStrength.Value.EXAMPLE,
        description = "For whom funds are to be reserved: (Patient, Provider, None).",
        valueSet = "http://hl7.org/fhir/ValueSet/fundsreserve"
    )
    private final CodeableConcept fundsReserve;
    @Binding(
        bindingName = "Forms",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The forms codes.",
        valueSet = "http://hl7.org/fhir/ValueSet/forms"
    )
    private final CodeableConcept formCode;
    private final Attachment form;
    private final List<ProcessNote> processNote;
    @ReferenceTarget({ "CommunicationRequest" })
    private final List<Reference> communicationRequest;
    private final List<Insurance> insurance;
    private final List<Error> error;

    private ClaimResponse(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        traceNumber = Collections.unmodifiableList(builder.traceNumber);
        status = builder.status;
        type = builder.type;
        subType = builder.subType;
        use = builder.use;
        patient = builder.patient;
        created = builder.created;
        insurer = builder.insurer;
        requestor = builder.requestor;
        request = builder.request;
        outcome = builder.outcome;
        decision = builder.decision;
        disposition = builder.disposition;
        preAuthRef = builder.preAuthRef;
        preAuthPeriod = builder.preAuthPeriod;
        event = Collections.unmodifiableList(builder.event);
        payeeType = builder.payeeType;
        encounter = Collections.unmodifiableList(builder.encounter);
        diagnosisRelatedGroup = builder.diagnosisRelatedGroup;
        item = Collections.unmodifiableList(builder.item);
        addItem = Collections.unmodifiableList(builder.addItem);
        adjudication = Collections.unmodifiableList(builder.adjudication);
        total = Collections.unmodifiableList(builder.total);
        payment = builder.payment;
        fundsReserve = builder.fundsReserve;
        formCode = builder.formCode;
        form = builder.form;
        processNote = Collections.unmodifiableList(builder.processNote);
        communicationRequest = Collections.unmodifiableList(builder.communicationRequest);
        insurance = Collections.unmodifiableList(builder.insurance);
        error = Collections.unmodifiableList(builder.error);
    }

    /**
     * A unique identifier assigned to this claim response.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getTraceNumber() {
        return traceNumber;
    }

    /**
     * The status of the resource instance.
     * 
     * @return
     *     An immutable object of type {@link ClaimResponseStatus} that is non-null.
     */
    public ClaimResponseStatus getStatus() {
        return status;
    }

    /**
     * A finer grained suite of claim type codes which may convey additional information such as Inpatient vs Outpatient 
     * and/or a specialty service.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * A finer grained suite of claim type codes which may convey additional information such as Inpatient vs Outpatient 
     * and/or a specialty service.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getSubType() {
        return subType;
    }

    /**
     * A code to indicate whether the nature of the request is: Claim - A request to an Insurer to adjudicate the supplied 
     * charges for health care goods and services under the identified policy and to pay the determined Benefit amount, if 
     * any; Preauthorization - A request to an Insurer to adjudicate the supplied proposed future charges for health care 
     * goods and services under the identified policy and to approve the services and provide the expected benefit amounts 
     * and potentially to reserve funds to pay the benefits when Claims for the indicated services are later submitted; or, 
     * Pre-determination - A request to an Insurer to adjudicate the supplied 'what if' charges for health care goods and 
     * services under the identified policy and report back what the Benefit payable would be had the services actually been 
     * provided.
     * 
     * @return
     *     An immutable object of type {@link Use} that is non-null.
     */
    public Use getUse() {
        return use;
    }

    /**
     * The party to whom the professional services and/or products have been supplied or are being considered and for whom 
     * actual for facast reimbursement is sought.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getPatient() {
        return patient;
    }

    /**
     * The date this resource was created.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that is non-null.
     */
    public DateTime getCreated() {
        return created;
    }

    /**
     * The party responsible for authorization, adjudication and reimbursement.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getInsurer() {
        return insurer;
    }

    /**
     * The provider which is responsible for the claim, predetermination or preauthorization.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequestor() {
        return requestor;
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
     * The outcome of the claim, predetermination, or preauthorization processing.
     * 
     * @return
     *     An immutable object of type {@link RemittanceOutcome} that is non-null.
     */
    public RemittanceOutcome getOutcome() {
        return outcome;
    }

    /**
     * The result of the claim, predetermination, or preauthorization adjudication.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getDecision() {
        return decision;
    }

    /**
     * A human readable description of the status of the adjudication.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * Reference from the Insurer which is used in later communications which refers to this adjudication.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getPreAuthRef() {
        return preAuthRef;
    }

    /**
     * The time frame during which this authorization is effective.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPreAuthPeriod() {
        return preAuthPeriod;
    }

    /**
     * Information code for an event with a corresponding date or period.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Event} that may be empty.
     */
    public List<Event> getEvent() {
        return event;
    }

    /**
     * Type of Party to be reimbursed: subscriber, provider, other.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPayeeType() {
        return payeeType;
    }

    /**
     * Healthcare encounters related to this claim.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEncounter() {
        return encounter;
    }

    /**
     * A package billing code or bundle code used to group products and services to a particular health condition (such as 
     * heart attack) which is based on a predetermined grouping code system.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getDiagnosisRelatedGroup() {
        return diagnosisRelatedGroup;
    }

    /**
     * A claim line. Either a simple (a product or service) or a 'group' of details which can also be a simple items or 
     * groups of sub-details.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Item} that may be empty.
     */
    public List<Item> getItem() {
        return item;
    }

    /**
     * The first-tier service adjudications for payor added product or service lines.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link AddItem} that may be empty.
     */
    public List<AddItem> getAddItem() {
        return addItem;
    }

    /**
     * The adjudication results which are presented at the header level rather than at the line-item or add-item levels.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Adjudication} that may be empty.
     */
    public List<ClaimResponse.Item.Adjudication> getAdjudication() {
        return adjudication;
    }

    /**
     * Categorized monetary totals for the adjudication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Total} that may be empty.
     */
    public List<Total> getTotal() {
        return total;
    }

    /**
     * Payment details for the adjudication of the claim.
     * 
     * @return
     *     An immutable object of type {@link Payment} that may be null.
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * A code, used only on a response to a preauthorization, to indicate whether the benefits payable have been reserved and 
     * for whom.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getFundsReserve() {
        return fundsReserve;
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
     * The actual form, by reference or inclusion, for printing the content or an EOB.
     * 
     * @return
     *     An immutable object of type {@link Attachment} that may be null.
     */
    public Attachment getForm() {
        return form;
    }

    /**
     * A note that describes or explains adjudication results in a human readable form.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ProcessNote} that may be empty.
     */
    public List<ProcessNote> getProcessNote() {
        return processNote;
    }

    /**
     * Request for additional supporting or authorizing information.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getCommunicationRequest() {
        return communicationRequest;
    }

    /**
     * Financial instruments for reimbursement for the health care products and services specified on the claim.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Insurance} that may be empty.
     */
    public List<Insurance> getInsurance() {
        return insurance;
    }

    /**
     * Errors encountered during the processing of the adjudication.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Error} that may be empty.
     */
    public List<Error> getError() {
        return error;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !traceNumber.isEmpty() || 
            (status != null) || 
            (type != null) || 
            (subType != null) || 
            (use != null) || 
            (patient != null) || 
            (created != null) || 
            (insurer != null) || 
            (requestor != null) || 
            (request != null) || 
            (outcome != null) || 
            (decision != null) || 
            (disposition != null) || 
            (preAuthRef != null) || 
            (preAuthPeriod != null) || 
            !event.isEmpty() || 
            (payeeType != null) || 
            !encounter.isEmpty() || 
            (diagnosisRelatedGroup != null) || 
            !item.isEmpty() || 
            !addItem.isEmpty() || 
            !adjudication.isEmpty() || 
            !total.isEmpty() || 
            (payment != null) || 
            (fundsReserve != null) || 
            (formCode != null) || 
            (form != null) || 
            !processNote.isEmpty() || 
            !communicationRequest.isEmpty() || 
            !insurance.isEmpty() || 
            !error.isEmpty();
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
                accept(traceNumber, "traceNumber", visitor, Identifier.class);
                accept(status, "status", visitor);
                accept(type, "type", visitor);
                accept(subType, "subType", visitor);
                accept(use, "use", visitor);
                accept(patient, "patient", visitor);
                accept(created, "created", visitor);
                accept(insurer, "insurer", visitor);
                accept(requestor, "requestor", visitor);
                accept(request, "request", visitor);
                accept(outcome, "outcome", visitor);
                accept(decision, "decision", visitor);
                accept(disposition, "disposition", visitor);
                accept(preAuthRef, "preAuthRef", visitor);
                accept(preAuthPeriod, "preAuthPeriod", visitor);
                accept(event, "event", visitor, Event.class);
                accept(payeeType, "payeeType", visitor);
                accept(encounter, "encounter", visitor, Reference.class);
                accept(diagnosisRelatedGroup, "diagnosisRelatedGroup", visitor);
                accept(item, "item", visitor, Item.class);
                accept(addItem, "addItem", visitor, AddItem.class);
                accept(adjudication, "adjudication", visitor, ClaimResponse.Item.Adjudication.class);
                accept(total, "total", visitor, Total.class);
                accept(payment, "payment", visitor);
                accept(fundsReserve, "fundsReserve", visitor);
                accept(formCode, "formCode", visitor);
                accept(form, "form", visitor);
                accept(processNote, "processNote", visitor, ProcessNote.class);
                accept(communicationRequest, "communicationRequest", visitor, Reference.class);
                accept(insurance, "insurance", visitor, Insurance.class);
                accept(error, "error", visitor, Error.class);
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
        ClaimResponse other = (ClaimResponse) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(traceNumber, other.traceNumber) && 
            Objects.equals(status, other.status) && 
            Objects.equals(type, other.type) && 
            Objects.equals(subType, other.subType) && 
            Objects.equals(use, other.use) && 
            Objects.equals(patient, other.patient) && 
            Objects.equals(created, other.created) && 
            Objects.equals(insurer, other.insurer) && 
            Objects.equals(requestor, other.requestor) && 
            Objects.equals(request, other.request) && 
            Objects.equals(outcome, other.outcome) && 
            Objects.equals(decision, other.decision) && 
            Objects.equals(disposition, other.disposition) && 
            Objects.equals(preAuthRef, other.preAuthRef) && 
            Objects.equals(preAuthPeriod, other.preAuthPeriod) && 
            Objects.equals(event, other.event) && 
            Objects.equals(payeeType, other.payeeType) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(diagnosisRelatedGroup, other.diagnosisRelatedGroup) && 
            Objects.equals(item, other.item) && 
            Objects.equals(addItem, other.addItem) && 
            Objects.equals(adjudication, other.adjudication) && 
            Objects.equals(total, other.total) && 
            Objects.equals(payment, other.payment) && 
            Objects.equals(fundsReserve, other.fundsReserve) && 
            Objects.equals(formCode, other.formCode) && 
            Objects.equals(form, other.form) && 
            Objects.equals(processNote, other.processNote) && 
            Objects.equals(communicationRequest, other.communicationRequest) && 
            Objects.equals(insurance, other.insurance) && 
            Objects.equals(error, other.error);
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
                traceNumber, 
                status, 
                type, 
                subType, 
                use, 
                patient, 
                created, 
                insurer, 
                requestor, 
                request, 
                outcome, 
                decision, 
                disposition, 
                preAuthRef, 
                preAuthPeriod, 
                event, 
                payeeType, 
                encounter, 
                diagnosisRelatedGroup, 
                item, 
                addItem, 
                adjudication, 
                total, 
                payment, 
                fundsReserve, 
                formCode, 
                form, 
                processNote, 
                communicationRequest, 
                insurance, 
                error);
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
        private List<Identifier> traceNumber = new ArrayList<>();
        private ClaimResponseStatus status;
        private CodeableConcept type;
        private CodeableConcept subType;
        private Use use;
        private Reference patient;
        private DateTime created;
        private Reference insurer;
        private Reference requestor;
        private Reference request;
        private RemittanceOutcome outcome;
        private CodeableConcept decision;
        private String disposition;
        private String preAuthRef;
        private Period preAuthPeriod;
        private List<Event> event = new ArrayList<>();
        private CodeableConcept payeeType;
        private List<Reference> encounter = new ArrayList<>();
        private CodeableConcept diagnosisRelatedGroup;
        private List<Item> item = new ArrayList<>();
        private List<AddItem> addItem = new ArrayList<>();
        private List<ClaimResponse.Item.Adjudication> adjudication = new ArrayList<>();
        private List<Total> total = new ArrayList<>();
        private Payment payment;
        private CodeableConcept fundsReserve;
        private CodeableConcept formCode;
        private Attachment form;
        private List<ProcessNote> processNote = new ArrayList<>();
        private List<Reference> communicationRequest = new ArrayList<>();
        private List<Insurance> insurance = new ArrayList<>();
        private List<Error> error = new ArrayList<>();

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
         * A unique identifier assigned to this claim response.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for a claim response
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
         * A unique identifier assigned to this claim response.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for a claim response
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
         * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param traceNumber
         *     Number for tracking
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder traceNumber(Identifier... traceNumber) {
            for (Identifier value : traceNumber) {
                this.traceNumber.add(value);
            }
            return this;
        }

        /**
         * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param traceNumber
         *     Number for tracking
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder traceNumber(Collection<Identifier> traceNumber) {
            this.traceNumber = new ArrayList<>(traceNumber);
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
        public Builder status(ClaimResponseStatus status) {
            this.status = status;
            return this;
        }

        /**
         * A finer grained suite of claim type codes which may convey additional information such as Inpatient vs Outpatient 
         * and/or a specialty service.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     More granular claim type
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * A finer grained suite of claim type codes which may convey additional information such as Inpatient vs Outpatient 
         * and/or a specialty service.
         * 
         * @param subType
         *     More granular claim type
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subType(CodeableConcept subType) {
            this.subType = subType;
            return this;
        }

        /**
         * A code to indicate whether the nature of the request is: Claim - A request to an Insurer to adjudicate the supplied 
         * charges for health care goods and services under the identified policy and to pay the determined Benefit amount, if 
         * any; Preauthorization - A request to an Insurer to adjudicate the supplied proposed future charges for health care 
         * goods and services under the identified policy and to approve the services and provide the expected benefit amounts 
         * and potentially to reserve funds to pay the benefits when Claims for the indicated services are later submitted; or, 
         * Pre-determination - A request to an Insurer to adjudicate the supplied 'what if' charges for health care goods and 
         * services under the identified policy and report back what the Benefit payable would be had the services actually been 
         * provided.
         * 
         * <p>This element is required.
         * 
         * @param use
         *     claim | preauthorization | predetermination
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder use(Use use) {
            this.use = use;
            return this;
        }

        /**
         * The party to whom the professional services and/or products have been supplied or are being considered and for whom 
         * actual for facast reimbursement is sought.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param patient
         *     The recipient of the products and services
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patient(Reference patient) {
            this.patient = patient;
            return this;
        }

        /**
         * The date this resource was created.
         * 
         * <p>This element is required.
         * 
         * @param created
         *     Response creation date
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder created(DateTime created) {
            this.created = created;
            return this;
        }

        /**
         * The party responsible for authorization, adjudication and reimbursement.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param insurer
         *     Party responsible for reimbursement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder insurer(Reference insurer) {
            this.insurer = insurer;
            return this;
        }

        /**
         * The provider which is responsible for the claim, predetermination or preauthorization.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param requestor
         *     Party responsible for the claim
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requestor(Reference requestor) {
            this.requestor = requestor;
            return this;
        }

        /**
         * Original request resource reference.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Claim}</li>
         * </ul>
         * 
         * @param request
         *     Id of resource triggering adjudication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder request(Reference request) {
            this.request = request;
            return this;
        }

        /**
         * The outcome of the claim, predetermination, or preauthorization processing.
         * 
         * <p>This element is required.
         * 
         * @param outcome
         *     queued | complete | error | partial
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder outcome(RemittanceOutcome outcome) {
            this.outcome = outcome;
            return this;
        }

        /**
         * The result of the claim, predetermination, or preauthorization adjudication.
         * 
         * @param decision
         *     Result of the adjudication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder decision(CodeableConcept decision) {
            this.decision = decision;
            return this;
        }

        /**
         * Convenience method for setting {@code disposition}.
         * 
         * @param disposition
         *     Disposition Message
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
         * A human readable description of the status of the adjudication.
         * 
         * @param disposition
         *     Disposition Message
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder disposition(String disposition) {
            this.disposition = disposition;
            return this;
        }

        /**
         * Convenience method for setting {@code preAuthRef}.
         * 
         * @param preAuthRef
         *     Preauthorization reference
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #preAuthRef(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder preAuthRef(java.lang.String preAuthRef) {
            this.preAuthRef = (preAuthRef == null) ? null : String.of(preAuthRef);
            return this;
        }

        /**
         * Reference from the Insurer which is used in later communications which refers to this adjudication.
         * 
         * @param preAuthRef
         *     Preauthorization reference
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder preAuthRef(String preAuthRef) {
            this.preAuthRef = preAuthRef;
            return this;
        }

        /**
         * The time frame during which this authorization is effective.
         * 
         * @param preAuthPeriod
         *     Preauthorization reference effective period
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder preAuthPeriod(Period preAuthPeriod) {
            this.preAuthPeriod = preAuthPeriod;
            return this;
        }

        /**
         * Information code for an event with a corresponding date or period.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param event
         *     Event information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder event(Event... event) {
            for (Event value : event) {
                this.event.add(value);
            }
            return this;
        }

        /**
         * Information code for an event with a corresponding date or period.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param event
         *     Event information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder event(Collection<Event> event) {
            this.event = new ArrayList<>(event);
            return this;
        }

        /**
         * Type of Party to be reimbursed: subscriber, provider, other.
         * 
         * @param payeeType
         *     Party to be paid any benefits payable
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder payeeType(CodeableConcept payeeType) {
            this.payeeType = payeeType;
            return this;
        }

        /**
         * Healthcare encounters related to this claim.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounters associated with the listed treatments
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference... encounter) {
            for (Reference value : encounter) {
                this.encounter.add(value);
            }
            return this;
        }

        /**
         * Healthcare encounters related to this claim.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounters associated with the listed treatments
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder encounter(Collection<Reference> encounter) {
            this.encounter = new ArrayList<>(encounter);
            return this;
        }

        /**
         * A package billing code or bundle code used to group products and services to a particular health condition (such as 
         * heart attack) which is based on a predetermined grouping code system.
         * 
         * @param diagnosisRelatedGroup
         *     Package billing code
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder diagnosisRelatedGroup(CodeableConcept diagnosisRelatedGroup) {
            this.diagnosisRelatedGroup = diagnosisRelatedGroup;
            return this;
        }

        /**
         * A claim line. Either a simple (a product or service) or a 'group' of details which can also be a simple items or 
         * groups of sub-details.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param item
         *     Adjudication for claim line items
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder item(Item... item) {
            for (Item value : item) {
                this.item.add(value);
            }
            return this;
        }

        /**
         * A claim line. Either a simple (a product or service) or a 'group' of details which can also be a simple items or 
         * groups of sub-details.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param item
         *     Adjudication for claim line items
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder item(Collection<Item> item) {
            this.item = new ArrayList<>(item);
            return this;
        }

        /**
         * The first-tier service adjudications for payor added product or service lines.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param addItem
         *     Insurer added line items
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder addItem(AddItem... addItem) {
            for (AddItem value : addItem) {
                this.addItem.add(value);
            }
            return this;
        }

        /**
         * The first-tier service adjudications for payor added product or service lines.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param addItem
         *     Insurer added line items
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder addItem(Collection<AddItem> addItem) {
            this.addItem = new ArrayList<>(addItem);
            return this;
        }

        /**
         * The adjudication results which are presented at the header level rather than at the line-item or add-item levels.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param adjudication
         *     Header-level adjudication
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder adjudication(ClaimResponse.Item.Adjudication... adjudication) {
            for (ClaimResponse.Item.Adjudication value : adjudication) {
                this.adjudication.add(value);
            }
            return this;
        }

        /**
         * The adjudication results which are presented at the header level rather than at the line-item or add-item levels.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param adjudication
         *     Header-level adjudication
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder adjudication(Collection<ClaimResponse.Item.Adjudication> adjudication) {
            this.adjudication = new ArrayList<>(adjudication);
            return this;
        }

        /**
         * Categorized monetary totals for the adjudication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param total
         *     Adjudication totals
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder total(Total... total) {
            for (Total value : total) {
                this.total.add(value);
            }
            return this;
        }

        /**
         * Categorized monetary totals for the adjudication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param total
         *     Adjudication totals
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder total(Collection<Total> total) {
            this.total = new ArrayList<>(total);
            return this;
        }

        /**
         * Payment details for the adjudication of the claim.
         * 
         * @param payment
         *     Payment Details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder payment(Payment payment) {
            this.payment = payment;
            return this;
        }

        /**
         * A code, used only on a response to a preauthorization, to indicate whether the benefits payable have been reserved and 
         * for whom.
         * 
         * @param fundsReserve
         *     Funds reserved status
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder fundsReserve(CodeableConcept fundsReserve) {
            this.fundsReserve = fundsReserve;
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
         * The actual form, by reference or inclusion, for printing the content or an EOB.
         * 
         * @param form
         *     Printed reference or actual form
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder form(Attachment form) {
            this.form = form;
            return this;
        }

        /**
         * A note that describes or explains adjudication results in a human readable form.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param processNote
         *     Note concerning adjudication
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
         * A note that describes or explains adjudication results in a human readable form.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param processNote
         *     Note concerning adjudication
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
         * Request for additional supporting or authorizing information.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CommunicationRequest}</li>
         * </ul>
         * 
         * @param communicationRequest
         *     Request for additional information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder communicationRequest(Reference... communicationRequest) {
            for (Reference value : communicationRequest) {
                this.communicationRequest.add(value);
            }
            return this;
        }

        /**
         * Request for additional supporting or authorizing information.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CommunicationRequest}</li>
         * </ul>
         * 
         * @param communicationRequest
         *     Request for additional information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder communicationRequest(Collection<Reference> communicationRequest) {
            this.communicationRequest = new ArrayList<>(communicationRequest);
            return this;
        }

        /**
         * Financial instruments for reimbursement for the health care products and services specified on the claim.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param insurance
         *     Patient insurance information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder insurance(Insurance... insurance) {
            for (Insurance value : insurance) {
                this.insurance.add(value);
            }
            return this;
        }

        /**
         * Financial instruments for reimbursement for the health care products and services specified on the claim.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param insurance
         *     Patient insurance information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder insurance(Collection<Insurance> insurance) {
            this.insurance = new ArrayList<>(insurance);
            return this;
        }

        /**
         * Errors encountered during the processing of the adjudication.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param error
         *     Processing errors
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder error(Error... error) {
            for (Error value : error) {
                this.error.add(value);
            }
            return this;
        }

        /**
         * Errors encountered during the processing of the adjudication.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param error
         *     Processing errors
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder error(Collection<Error> error) {
            this.error = new ArrayList<>(error);
            return this;
        }

        /**
         * Build the {@link ClaimResponse}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>type</li>
         * <li>use</li>
         * <li>patient</li>
         * <li>created</li>
         * <li>outcome</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ClaimResponse}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ClaimResponse per the base specification
         */
        @Override
        public ClaimResponse build() {
            ClaimResponse claimResponse = new ClaimResponse(this);
            if (validating) {
                validate(claimResponse);
            }
            return claimResponse;
        }

        protected void validate(ClaimResponse claimResponse) {
            super.validate(claimResponse);
            ValidationSupport.checkList(claimResponse.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(claimResponse.traceNumber, "traceNumber", Identifier.class);
            ValidationSupport.requireNonNull(claimResponse.status, "status");
            ValidationSupport.requireNonNull(claimResponse.type, "type");
            ValidationSupport.requireNonNull(claimResponse.use, "use");
            ValidationSupport.requireNonNull(claimResponse.patient, "patient");
            ValidationSupport.requireNonNull(claimResponse.created, "created");
            ValidationSupport.requireNonNull(claimResponse.outcome, "outcome");
            ValidationSupport.checkList(claimResponse.event, "event", Event.class);
            ValidationSupport.checkList(claimResponse.encounter, "encounter", Reference.class);
            ValidationSupport.checkList(claimResponse.item, "item", Item.class);
            ValidationSupport.checkList(claimResponse.addItem, "addItem", AddItem.class);
            ValidationSupport.checkList(claimResponse.adjudication, "adjudication", ClaimResponse.Item.Adjudication.class);
            ValidationSupport.checkList(claimResponse.total, "total", Total.class);
            ValidationSupport.checkList(claimResponse.processNote, "processNote", ProcessNote.class);
            ValidationSupport.checkList(claimResponse.communicationRequest, "communicationRequest", Reference.class);
            ValidationSupport.checkList(claimResponse.insurance, "insurance", Insurance.class);
            ValidationSupport.checkList(claimResponse.error, "error", Error.class);
            ValidationSupport.checkReferenceType(claimResponse.patient, "patient", "Patient");
            ValidationSupport.checkReferenceType(claimResponse.insurer, "insurer", "Organization");
            ValidationSupport.checkReferenceType(claimResponse.requestor, "requestor", "Practitioner", "PractitionerRole", "Organization");
            ValidationSupport.checkReferenceType(claimResponse.request, "request", "Claim");
            ValidationSupport.checkReferenceType(claimResponse.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(claimResponse.communicationRequest, "communicationRequest", "CommunicationRequest");
        }

        protected Builder from(ClaimResponse claimResponse) {
            super.from(claimResponse);
            identifier.addAll(claimResponse.identifier);
            traceNumber.addAll(claimResponse.traceNumber);
            status = claimResponse.status;
            type = claimResponse.type;
            subType = claimResponse.subType;
            use = claimResponse.use;
            patient = claimResponse.patient;
            created = claimResponse.created;
            insurer = claimResponse.insurer;
            requestor = claimResponse.requestor;
            request = claimResponse.request;
            outcome = claimResponse.outcome;
            decision = claimResponse.decision;
            disposition = claimResponse.disposition;
            preAuthRef = claimResponse.preAuthRef;
            preAuthPeriod = claimResponse.preAuthPeriod;
            event.addAll(claimResponse.event);
            payeeType = claimResponse.payeeType;
            encounter.addAll(claimResponse.encounter);
            diagnosisRelatedGroup = claimResponse.diagnosisRelatedGroup;
            item.addAll(claimResponse.item);
            addItem.addAll(claimResponse.addItem);
            adjudication.addAll(claimResponse.adjudication);
            total.addAll(claimResponse.total);
            payment = claimResponse.payment;
            fundsReserve = claimResponse.fundsReserve;
            formCode = claimResponse.formCode;
            form = claimResponse.form;
            processNote.addAll(claimResponse.processNote);
            communicationRequest.addAll(claimResponse.communicationRequest);
            insurance.addAll(claimResponse.insurance);
            error.addAll(claimResponse.error);
            return this;
        }
    }

    /**
     * Information code for an event with a corresponding date or period.
     */
    public static class Event extends BackboneElement {
        @Binding(
            bindingName = "DatesType",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/datestype"
        )
        @Required
        private final CodeableConcept type;
        @Choice({ DateTime.class, Period.class })
        @Required
        private final Element when;

        private Event(Builder builder) {
            super(builder);
            type = builder.type;
            when = builder.when;
        }

        /**
         * A coded event such as when a service is expected or a card printed.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * A date or period in the past or future indicating when the event occurred or is expectd to occur.
         * 
         * @return
         *     An immutable object of type {@link DateTime} or {@link Period} that is non-null.
         */
        public Element getWhen() {
            return when;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (when != null);
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
                    accept(when, "when", visitor);
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
            Event other = (Event) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(when, other.when);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    when);
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
            private Element when;

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
             * A coded event such as when a service is expected or a card printed.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Specific event
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * A date or period in the past or future indicating when the event occurred or is expectd to occur.
             * 
             * <p>This element is required.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link DateTime}</li>
             * <li>{@link Period}</li>
             * </ul>
             * 
             * @param when
             *     Occurance date or period
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder when(Element when) {
                this.when = when;
                return this;
            }

            /**
             * Build the {@link Event}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>when</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Event}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Event per the base specification
             */
            @Override
            public Event build() {
                Event event = new Event(this);
                if (validating) {
                    validate(event);
                }
                return event;
            }

            protected void validate(Event event) {
                super.validate(event);
                ValidationSupport.requireNonNull(event.type, "type");
                ValidationSupport.requireChoiceElement(event.when, "when", DateTime.class, Period.class);
                ValidationSupport.requireValueOrChildren(event);
            }

            protected Builder from(Event event) {
                super.from(event);
                type = event.type;
                when = event.when;
                return this;
            }
        }
    }

    /**
     * A claim line. Either a simple (a product or service) or a 'group' of details which can also be a simple items or 
     * groups of sub-details.
     */
    public static class Item extends BackboneElement {
        @Required
        private final PositiveInt itemSequence;
        private final List<Identifier> traceNumber;
        private final List<PositiveInt> noteNumber;
        private final ReviewOutcome reviewOutcome;
        private final List<Adjudication> adjudication;
        private final List<Detail> detail;

        private Item(Builder builder) {
            super(builder);
            itemSequence = builder.itemSequence;
            traceNumber = Collections.unmodifiableList(builder.traceNumber);
            noteNumber = Collections.unmodifiableList(builder.noteNumber);
            reviewOutcome = builder.reviewOutcome;
            adjudication = Collections.unmodifiableList(builder.adjudication);
            detail = Collections.unmodifiableList(builder.detail);
        }

        /**
         * A number to uniquely reference the claim item entries.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that is non-null.
         */
        public PositiveInt getItemSequence() {
            return itemSequence;
        }

        /**
         * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
         */
        public List<Identifier> getTraceNumber() {
            return traceNumber;
        }

        /**
         * The numbers associated with notes below which apply to the adjudication of this item.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
         */
        public List<PositiveInt> getNoteNumber() {
            return noteNumber;
        }

        /**
         * The high-level results of the adjudication if adjudication has been performed.
         * 
         * @return
         *     An immutable object of type {@link ReviewOutcome} that may be null.
         */
        public ReviewOutcome getReviewOutcome() {
            return reviewOutcome;
        }

        /**
         * If this item is a group then the values here are a summary of the adjudication of the detail items. If this item is a 
         * simple product or service then this is the result of the adjudication of this item.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Adjudication} that may be empty.
         */
        public List<Adjudication> getAdjudication() {
            return adjudication;
        }

        /**
         * A claim detail. Either a simple (a product or service) or a 'group' of sub-details which are simple items.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Detail} that may be empty.
         */
        public List<Detail> getDetail() {
            return detail;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (itemSequence != null) || 
                !traceNumber.isEmpty() || 
                !noteNumber.isEmpty() || 
                (reviewOutcome != null) || 
                !adjudication.isEmpty() || 
                !detail.isEmpty();
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
                    accept(itemSequence, "itemSequence", visitor);
                    accept(traceNumber, "traceNumber", visitor, Identifier.class);
                    accept(noteNumber, "noteNumber", visitor, PositiveInt.class);
                    accept(reviewOutcome, "reviewOutcome", visitor);
                    accept(adjudication, "adjudication", visitor, Adjudication.class);
                    accept(detail, "detail", visitor, Detail.class);
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
            Item other = (Item) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(itemSequence, other.itemSequence) && 
                Objects.equals(traceNumber, other.traceNumber) && 
                Objects.equals(noteNumber, other.noteNumber) && 
                Objects.equals(reviewOutcome, other.reviewOutcome) && 
                Objects.equals(adjudication, other.adjudication) && 
                Objects.equals(detail, other.detail);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    itemSequence, 
                    traceNumber, 
                    noteNumber, 
                    reviewOutcome, 
                    adjudication, 
                    detail);
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
            private PositiveInt itemSequence;
            private List<Identifier> traceNumber = new ArrayList<>();
            private List<PositiveInt> noteNumber = new ArrayList<>();
            private ReviewOutcome reviewOutcome;
            private List<Adjudication> adjudication = new ArrayList<>();
            private List<Detail> detail = new ArrayList<>();

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
             * A number to uniquely reference the claim item entries.
             * 
             * <p>This element is required.
             * 
             * @param itemSequence
             *     Claim item instance identifier
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder itemSequence(PositiveInt itemSequence) {
                this.itemSequence = itemSequence;
                return this;
            }

            /**
             * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param traceNumber
             *     Number for tracking
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder traceNumber(Identifier... traceNumber) {
                for (Identifier value : traceNumber) {
                    this.traceNumber.add(value);
                }
                return this;
            }

            /**
             * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param traceNumber
             *     Number for tracking
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder traceNumber(Collection<Identifier> traceNumber) {
                this.traceNumber = new ArrayList<>(traceNumber);
                return this;
            }

            /**
             * The numbers associated with notes below which apply to the adjudication of this item.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param noteNumber
             *     Applicable note numbers
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder noteNumber(PositiveInt... noteNumber) {
                for (PositiveInt value : noteNumber) {
                    this.noteNumber.add(value);
                }
                return this;
            }

            /**
             * The numbers associated with notes below which apply to the adjudication of this item.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param noteNumber
             *     Applicable note numbers
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder noteNumber(Collection<PositiveInt> noteNumber) {
                this.noteNumber = new ArrayList<>(noteNumber);
                return this;
            }

            /**
             * The high-level results of the adjudication if adjudication has been performed.
             * 
             * @param reviewOutcome
             *     Adjudication results
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reviewOutcome(ReviewOutcome reviewOutcome) {
                this.reviewOutcome = reviewOutcome;
                return this;
            }

            /**
             * If this item is a group then the values here are a summary of the adjudication of the detail items. If this item is a 
             * simple product or service then this is the result of the adjudication of this item.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param adjudication
             *     Adjudication details
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder adjudication(Adjudication... adjudication) {
                for (Adjudication value : adjudication) {
                    this.adjudication.add(value);
                }
                return this;
            }

            /**
             * If this item is a group then the values here are a summary of the adjudication of the detail items. If this item is a 
             * simple product or service then this is the result of the adjudication of this item.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param adjudication
             *     Adjudication details
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder adjudication(Collection<Adjudication> adjudication) {
                this.adjudication = new ArrayList<>(adjudication);
                return this;
            }

            /**
             * A claim detail. Either a simple (a product or service) or a 'group' of sub-details which are simple items.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Adjudication for claim details
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder detail(Detail... detail) {
                for (Detail value : detail) {
                    this.detail.add(value);
                }
                return this;
            }

            /**
             * A claim detail. Either a simple (a product or service) or a 'group' of sub-details which are simple items.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Adjudication for claim details
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder detail(Collection<Detail> detail) {
                this.detail = new ArrayList<>(detail);
                return this;
            }

            /**
             * Build the {@link Item}
             * 
             * <p>Required elements:
             * <ul>
             * <li>itemSequence</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Item}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Item per the base specification
             */
            @Override
            public Item build() {
                Item item = new Item(this);
                if (validating) {
                    validate(item);
                }
                return item;
            }

            protected void validate(Item item) {
                super.validate(item);
                ValidationSupport.requireNonNull(item.itemSequence, "itemSequence");
                ValidationSupport.checkList(item.traceNumber, "traceNumber", Identifier.class);
                ValidationSupport.checkList(item.noteNumber, "noteNumber", PositiveInt.class);
                ValidationSupport.checkList(item.adjudication, "adjudication", Adjudication.class);
                ValidationSupport.checkList(item.detail, "detail", Detail.class);
                ValidationSupport.requireValueOrChildren(item);
            }

            protected Builder from(Item item) {
                super.from(item);
                itemSequence = item.itemSequence;
                traceNumber.addAll(item.traceNumber);
                noteNumber.addAll(item.noteNumber);
                reviewOutcome = item.reviewOutcome;
                adjudication.addAll(item.adjudication);
                detail.addAll(item.detail);
                return this;
            }
        }

        /**
         * The high-level results of the adjudication if adjudication has been performed.
         */
        public static class ReviewOutcome extends BackboneElement {
            @Binding(
                bindingName = "AdjudicationDecision",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/claim-decision"
            )
            private final CodeableConcept decision;
            @Binding(
                bindingName = "AdjudicationDecisionReason",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/claim-decision-reason"
            )
            private final List<CodeableConcept> reason;
            private final String preAuthRef;
            private final Period preAuthPeriod;

            private ReviewOutcome(Builder builder) {
                super(builder);
                decision = builder.decision;
                reason = Collections.unmodifiableList(builder.reason);
                preAuthRef = builder.preAuthRef;
                preAuthPeriod = builder.preAuthPeriod;
            }

            /**
             * The result of the claim, predetermination, or preauthorization adjudication.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getDecision() {
                return decision;
            }

            /**
             * The reasons for the result of the claim, predetermination, or preauthorization adjudication.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getReason() {
                return reason;
            }

            /**
             * Reference from the Insurer which is used in later communications which refers to this adjudication.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getPreAuthRef() {
                return preAuthRef;
            }

            /**
             * The time frame during which this authorization is effective.
             * 
             * @return
             *     An immutable object of type {@link Period} that may be null.
             */
            public Period getPreAuthPeriod() {
                return preAuthPeriod;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (decision != null) || 
                    !reason.isEmpty() || 
                    (preAuthRef != null) || 
                    (preAuthPeriod != null);
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
                        accept(decision, "decision", visitor);
                        accept(reason, "reason", visitor, CodeableConcept.class);
                        accept(preAuthRef, "preAuthRef", visitor);
                        accept(preAuthPeriod, "preAuthPeriod", visitor);
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
                ReviewOutcome other = (ReviewOutcome) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(decision, other.decision) && 
                    Objects.equals(reason, other.reason) && 
                    Objects.equals(preAuthRef, other.preAuthRef) && 
                    Objects.equals(preAuthPeriod, other.preAuthPeriod);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        decision, 
                        reason, 
                        preAuthRef, 
                        preAuthPeriod);
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
                private CodeableConcept decision;
                private List<CodeableConcept> reason = new ArrayList<>();
                private String preAuthRef;
                private Period preAuthPeriod;

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
                 * The result of the claim, predetermination, or preauthorization adjudication.
                 * 
                 * @param decision
                 *     Result of the adjudication
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder decision(CodeableConcept decision) {
                    this.decision = decision;
                    return this;
                }

                /**
                 * The reasons for the result of the claim, predetermination, or preauthorization adjudication.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param reason
                 *     Reason for result of the adjudication
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder reason(CodeableConcept... reason) {
                    for (CodeableConcept value : reason) {
                        this.reason.add(value);
                    }
                    return this;
                }

                /**
                 * The reasons for the result of the claim, predetermination, or preauthorization adjudication.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param reason
                 *     Reason for result of the adjudication
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder reason(Collection<CodeableConcept> reason) {
                    this.reason = new ArrayList<>(reason);
                    return this;
                }

                /**
                 * Convenience method for setting {@code preAuthRef}.
                 * 
                 * @param preAuthRef
                 *     Preauthorization reference
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #preAuthRef(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder preAuthRef(java.lang.String preAuthRef) {
                    this.preAuthRef = (preAuthRef == null) ? null : String.of(preAuthRef);
                    return this;
                }

                /**
                 * Reference from the Insurer which is used in later communications which refers to this adjudication.
                 * 
                 * @param preAuthRef
                 *     Preauthorization reference
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder preAuthRef(String preAuthRef) {
                    this.preAuthRef = preAuthRef;
                    return this;
                }

                /**
                 * The time frame during which this authorization is effective.
                 * 
                 * @param preAuthPeriod
                 *     Preauthorization reference effective period
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder preAuthPeriod(Period preAuthPeriod) {
                    this.preAuthPeriod = preAuthPeriod;
                    return this;
                }

                /**
                 * Build the {@link ReviewOutcome}
                 * 
                 * @return
                 *     An immutable object of type {@link ReviewOutcome}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid ReviewOutcome per the base specification
                 */
                @Override
                public ReviewOutcome build() {
                    ReviewOutcome reviewOutcome = new ReviewOutcome(this);
                    if (validating) {
                        validate(reviewOutcome);
                    }
                    return reviewOutcome;
                }

                protected void validate(ReviewOutcome reviewOutcome) {
                    super.validate(reviewOutcome);
                    ValidationSupport.checkList(reviewOutcome.reason, "reason", CodeableConcept.class);
                    ValidationSupport.requireValueOrChildren(reviewOutcome);
                }

                protected Builder from(ReviewOutcome reviewOutcome) {
                    super.from(reviewOutcome);
                    decision = reviewOutcome.decision;
                    reason.addAll(reviewOutcome.reason);
                    preAuthRef = reviewOutcome.preAuthRef;
                    preAuthPeriod = reviewOutcome.preAuthPeriod;
                    return this;
                }
            }
        }

        /**
         * If this item is a group then the values here are a summary of the adjudication of the detail items. If this item is a 
         * simple product or service then this is the result of the adjudication of this item.
         */
        public static class Adjudication extends BackboneElement {
            @Binding(
                bindingName = "Adjudication",
                strength = BindingStrength.Value.EXAMPLE,
                description = "The adjudication codes.",
                valueSet = "http://hl7.org/fhir/ValueSet/adjudication"
            )
            @Required
            private final CodeableConcept category;
            @Binding(
                bindingName = "AdjudicationReason",
                strength = BindingStrength.Value.EXAMPLE,
                description = "The adjudication reason codes.",
                valueSet = "http://hl7.org/fhir/ValueSet/adjudication-reason"
            )
            private final CodeableConcept reason;
            private final Money amount;
            private final Quantity quantity;

            private Adjudication(Builder builder) {
                super(builder);
                category = builder.category;
                reason = builder.reason;
                amount = builder.amount;
                quantity = builder.quantity;
            }

            /**
             * A code to indicate the information type of this adjudication record. Information types may include the value 
             * submitted, maximum values or percentages allowed or payable under the plan, amounts that: the patient is responsible 
             * for in aggregate or pertaining to this item; amounts paid by other coverages; and, the benefit payable for this item.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getCategory() {
                return category;
            }

            /**
             * A code supporting the understanding of the adjudication result and explaining variance from expected amount.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getReason() {
                return reason;
            }

            /**
             * Monetary amount associated with the category.
             * 
             * @return
             *     An immutable object of type {@link Money} that may be null.
             */
            public Money getAmount() {
                return amount;
            }

            /**
             * A non-monetary value associated with the category. Mutually exclusive to the amount element above.
             * 
             * @return
             *     An immutable object of type {@link Quantity} that may be null.
             */
            public Quantity getQuantity() {
                return quantity;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (category != null) || 
                    (reason != null) || 
                    (amount != null) || 
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
                        accept(category, "category", visitor);
                        accept(reason, "reason", visitor);
                        accept(amount, "amount", visitor);
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
                Adjudication other = (Adjudication) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(category, other.category) && 
                    Objects.equals(reason, other.reason) && 
                    Objects.equals(amount, other.amount) && 
                    Objects.equals(quantity, other.quantity);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        category, 
                        reason, 
                        amount, 
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
                private CodeableConcept category;
                private CodeableConcept reason;
                private Money amount;
                private Quantity quantity;

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
                 * A code to indicate the information type of this adjudication record. Information types may include the value 
                 * submitted, maximum values or percentages allowed or payable under the plan, amounts that: the patient is responsible 
                 * for in aggregate or pertaining to this item; amounts paid by other coverages; and, the benefit payable for this item.
                 * 
                 * <p>This element is required.
                 * 
                 * @param category
                 *     Type of adjudication information
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder category(CodeableConcept category) {
                    this.category = category;
                    return this;
                }

                /**
                 * A code supporting the understanding of the adjudication result and explaining variance from expected amount.
                 * 
                 * @param reason
                 *     Explanation of adjudication outcome
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder reason(CodeableConcept reason) {
                    this.reason = reason;
                    return this;
                }

                /**
                 * Monetary amount associated with the category.
                 * 
                 * @param amount
                 *     Monetary amount
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder amount(Money amount) {
                    this.amount = amount;
                    return this;
                }

                /**
                 * A non-monetary value associated with the category. Mutually exclusive to the amount element above.
                 * 
                 * @param quantity
                 *     Non-monetary value
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(Quantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * Build the {@link Adjudication}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>category</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Adjudication}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Adjudication per the base specification
                 */
                @Override
                public Adjudication build() {
                    Adjudication adjudication = new Adjudication(this);
                    if (validating) {
                        validate(adjudication);
                    }
                    return adjudication;
                }

                protected void validate(Adjudication adjudication) {
                    super.validate(adjudication);
                    ValidationSupport.requireNonNull(adjudication.category, "category");
                    ValidationSupport.requireValueOrChildren(adjudication);
                }

                protected Builder from(Adjudication adjudication) {
                    super.from(adjudication);
                    category = adjudication.category;
                    reason = adjudication.reason;
                    amount = adjudication.amount;
                    quantity = adjudication.quantity;
                    return this;
                }
            }
        }

        /**
         * A claim detail. Either a simple (a product or service) or a 'group' of sub-details which are simple items.
         */
        public static class Detail extends BackboneElement {
            @Required
            private final PositiveInt detailSequence;
            private final List<Identifier> traceNumber;
            private final List<PositiveInt> noteNumber;
            private final ClaimResponse.Item.ReviewOutcome reviewOutcome;
            private final List<ClaimResponse.Item.Adjudication> adjudication;
            private final List<SubDetail> subDetail;

            private Detail(Builder builder) {
                super(builder);
                detailSequence = builder.detailSequence;
                traceNumber = Collections.unmodifiableList(builder.traceNumber);
                noteNumber = Collections.unmodifiableList(builder.noteNumber);
                reviewOutcome = builder.reviewOutcome;
                adjudication = Collections.unmodifiableList(builder.adjudication);
                subDetail = Collections.unmodifiableList(builder.subDetail);
            }

            /**
             * A number to uniquely reference the claim detail entry.
             * 
             * @return
             *     An immutable object of type {@link PositiveInt} that is non-null.
             */
            public PositiveInt getDetailSequence() {
                return detailSequence;
            }

            /**
             * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
             */
            public List<Identifier> getTraceNumber() {
                return traceNumber;
            }

            /**
             * The numbers associated with notes below which apply to the adjudication of this item.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
             */
            public List<PositiveInt> getNoteNumber() {
                return noteNumber;
            }

            /**
             * The high-level results of the adjudication if adjudication has been performed.
             * 
             * @return
             *     An immutable object of type {@link ClaimResponse.Item.ReviewOutcome} that may be null.
             */
            public ClaimResponse.Item.ReviewOutcome getReviewOutcome() {
                return reviewOutcome;
            }

            /**
             * The adjudication results.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Adjudication} that may be empty.
             */
            public List<ClaimResponse.Item.Adjudication> getAdjudication() {
                return adjudication;
            }

            /**
             * A sub-detail adjudication of a simple product or service.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link SubDetail} that may be empty.
             */
            public List<SubDetail> getSubDetail() {
                return subDetail;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (detailSequence != null) || 
                    !traceNumber.isEmpty() || 
                    !noteNumber.isEmpty() || 
                    (reviewOutcome != null) || 
                    !adjudication.isEmpty() || 
                    !subDetail.isEmpty();
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
                        accept(detailSequence, "detailSequence", visitor);
                        accept(traceNumber, "traceNumber", visitor, Identifier.class);
                        accept(noteNumber, "noteNumber", visitor, PositiveInt.class);
                        accept(reviewOutcome, "reviewOutcome", visitor);
                        accept(adjudication, "adjudication", visitor, ClaimResponse.Item.Adjudication.class);
                        accept(subDetail, "subDetail", visitor, SubDetail.class);
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
                Detail other = (Detail) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(detailSequence, other.detailSequence) && 
                    Objects.equals(traceNumber, other.traceNumber) && 
                    Objects.equals(noteNumber, other.noteNumber) && 
                    Objects.equals(reviewOutcome, other.reviewOutcome) && 
                    Objects.equals(adjudication, other.adjudication) && 
                    Objects.equals(subDetail, other.subDetail);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        detailSequence, 
                        traceNumber, 
                        noteNumber, 
                        reviewOutcome, 
                        adjudication, 
                        subDetail);
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
                private PositiveInt detailSequence;
                private List<Identifier> traceNumber = new ArrayList<>();
                private List<PositiveInt> noteNumber = new ArrayList<>();
                private ClaimResponse.Item.ReviewOutcome reviewOutcome;
                private List<ClaimResponse.Item.Adjudication> adjudication = new ArrayList<>();
                private List<SubDetail> subDetail = new ArrayList<>();

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
                 * A number to uniquely reference the claim detail entry.
                 * 
                 * <p>This element is required.
                 * 
                 * @param detailSequence
                 *     Claim detail instance identifier
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder detailSequence(PositiveInt detailSequence) {
                    this.detailSequence = detailSequence;
                    return this;
                }

                /**
                 * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param traceNumber
                 *     Number for tracking
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder traceNumber(Identifier... traceNumber) {
                    for (Identifier value : traceNumber) {
                        this.traceNumber.add(value);
                    }
                    return this;
                }

                /**
                 * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param traceNumber
                 *     Number for tracking
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder traceNumber(Collection<Identifier> traceNumber) {
                    this.traceNumber = new ArrayList<>(traceNumber);
                    return this;
                }

                /**
                 * The numbers associated with notes below which apply to the adjudication of this item.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param noteNumber
                 *     Applicable note numbers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder noteNumber(PositiveInt... noteNumber) {
                    for (PositiveInt value : noteNumber) {
                        this.noteNumber.add(value);
                    }
                    return this;
                }

                /**
                 * The numbers associated with notes below which apply to the adjudication of this item.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param noteNumber
                 *     Applicable note numbers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder noteNumber(Collection<PositiveInt> noteNumber) {
                    this.noteNumber = new ArrayList<>(noteNumber);
                    return this;
                }

                /**
                 * The high-level results of the adjudication if adjudication has been performed.
                 * 
                 * @param reviewOutcome
                 *     Detail level adjudication results
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder reviewOutcome(ClaimResponse.Item.ReviewOutcome reviewOutcome) {
                    this.reviewOutcome = reviewOutcome;
                    return this;
                }

                /**
                 * The adjudication results.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param adjudication
                 *     Detail level adjudication details
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder adjudication(ClaimResponse.Item.Adjudication... adjudication) {
                    for (ClaimResponse.Item.Adjudication value : adjudication) {
                        this.adjudication.add(value);
                    }
                    return this;
                }

                /**
                 * The adjudication results.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param adjudication
                 *     Detail level adjudication details
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder adjudication(Collection<ClaimResponse.Item.Adjudication> adjudication) {
                    this.adjudication = new ArrayList<>(adjudication);
                    return this;
                }

                /**
                 * A sub-detail adjudication of a simple product or service.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param subDetail
                 *     Adjudication for claim sub-details
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder subDetail(SubDetail... subDetail) {
                    for (SubDetail value : subDetail) {
                        this.subDetail.add(value);
                    }
                    return this;
                }

                /**
                 * A sub-detail adjudication of a simple product or service.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param subDetail
                 *     Adjudication for claim sub-details
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder subDetail(Collection<SubDetail> subDetail) {
                    this.subDetail = new ArrayList<>(subDetail);
                    return this;
                }

                /**
                 * Build the {@link Detail}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>detailSequence</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Detail}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Detail per the base specification
                 */
                @Override
                public Detail build() {
                    Detail detail = new Detail(this);
                    if (validating) {
                        validate(detail);
                    }
                    return detail;
                }

                protected void validate(Detail detail) {
                    super.validate(detail);
                    ValidationSupport.requireNonNull(detail.detailSequence, "detailSequence");
                    ValidationSupport.checkList(detail.traceNumber, "traceNumber", Identifier.class);
                    ValidationSupport.checkList(detail.noteNumber, "noteNumber", PositiveInt.class);
                    ValidationSupport.checkList(detail.adjudication, "adjudication", ClaimResponse.Item.Adjudication.class);
                    ValidationSupport.checkList(detail.subDetail, "subDetail", SubDetail.class);
                    ValidationSupport.requireValueOrChildren(detail);
                }

                protected Builder from(Detail detail) {
                    super.from(detail);
                    detailSequence = detail.detailSequence;
                    traceNumber.addAll(detail.traceNumber);
                    noteNumber.addAll(detail.noteNumber);
                    reviewOutcome = detail.reviewOutcome;
                    adjudication.addAll(detail.adjudication);
                    subDetail.addAll(detail.subDetail);
                    return this;
                }
            }

            /**
             * A sub-detail adjudication of a simple product or service.
             */
            public static class SubDetail extends BackboneElement {
                @Required
                private final PositiveInt subDetailSequence;
                private final List<Identifier> traceNumber;
                private final List<PositiveInt> noteNumber;
                private final ClaimResponse.Item.ReviewOutcome reviewOutcome;
                private final List<ClaimResponse.Item.Adjudication> adjudication;

                private SubDetail(Builder builder) {
                    super(builder);
                    subDetailSequence = builder.subDetailSequence;
                    traceNumber = Collections.unmodifiableList(builder.traceNumber);
                    noteNumber = Collections.unmodifiableList(builder.noteNumber);
                    reviewOutcome = builder.reviewOutcome;
                    adjudication = Collections.unmodifiableList(builder.adjudication);
                }

                /**
                 * A number to uniquely reference the claim sub-detail entry.
                 * 
                 * @return
                 *     An immutable object of type {@link PositiveInt} that is non-null.
                 */
                public PositiveInt getSubDetailSequence() {
                    return subDetailSequence;
                }

                /**
                 * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
                 */
                public List<Identifier> getTraceNumber() {
                    return traceNumber;
                }

                /**
                 * The numbers associated with notes below which apply to the adjudication of this item.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
                 */
                public List<PositiveInt> getNoteNumber() {
                    return noteNumber;
                }

                /**
                 * The high-level results of the adjudication if adjudication has been performed.
                 * 
                 * @return
                 *     An immutable object of type {@link ClaimResponse.Item.ReviewOutcome} that may be null.
                 */
                public ClaimResponse.Item.ReviewOutcome getReviewOutcome() {
                    return reviewOutcome;
                }

                /**
                 * The adjudication results.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Adjudication} that may be empty.
                 */
                public List<ClaimResponse.Item.Adjudication> getAdjudication() {
                    return adjudication;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (subDetailSequence != null) || 
                        !traceNumber.isEmpty() || 
                        !noteNumber.isEmpty() || 
                        (reviewOutcome != null) || 
                        !adjudication.isEmpty();
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
                            accept(subDetailSequence, "subDetailSequence", visitor);
                            accept(traceNumber, "traceNumber", visitor, Identifier.class);
                            accept(noteNumber, "noteNumber", visitor, PositiveInt.class);
                            accept(reviewOutcome, "reviewOutcome", visitor);
                            accept(adjudication, "adjudication", visitor, ClaimResponse.Item.Adjudication.class);
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
                    SubDetail other = (SubDetail) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(subDetailSequence, other.subDetailSequence) && 
                        Objects.equals(traceNumber, other.traceNumber) && 
                        Objects.equals(noteNumber, other.noteNumber) && 
                        Objects.equals(reviewOutcome, other.reviewOutcome) && 
                        Objects.equals(adjudication, other.adjudication);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            subDetailSequence, 
                            traceNumber, 
                            noteNumber, 
                            reviewOutcome, 
                            adjudication);
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
                    private PositiveInt subDetailSequence;
                    private List<Identifier> traceNumber = new ArrayList<>();
                    private List<PositiveInt> noteNumber = new ArrayList<>();
                    private ClaimResponse.Item.ReviewOutcome reviewOutcome;
                    private List<ClaimResponse.Item.Adjudication> adjudication = new ArrayList<>();

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
                     * A number to uniquely reference the claim sub-detail entry.
                     * 
                     * <p>This element is required.
                     * 
                     * @param subDetailSequence
                     *     Claim sub-detail instance identifier
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder subDetailSequence(PositiveInt subDetailSequence) {
                        this.subDetailSequence = subDetailSequence;
                        return this;
                    }

                    /**
                     * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param traceNumber
                     *     Number for tracking
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder traceNumber(Identifier... traceNumber) {
                        for (Identifier value : traceNumber) {
                            this.traceNumber.add(value);
                        }
                        return this;
                    }

                    /**
                     * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param traceNumber
                     *     Number for tracking
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder traceNumber(Collection<Identifier> traceNumber) {
                        this.traceNumber = new ArrayList<>(traceNumber);
                        return this;
                    }

                    /**
                     * The numbers associated with notes below which apply to the adjudication of this item.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param noteNumber
                     *     Applicable note numbers
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder noteNumber(PositiveInt... noteNumber) {
                        for (PositiveInt value : noteNumber) {
                            this.noteNumber.add(value);
                        }
                        return this;
                    }

                    /**
                     * The numbers associated with notes below which apply to the adjudication of this item.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param noteNumber
                     *     Applicable note numbers
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder noteNumber(Collection<PositiveInt> noteNumber) {
                        this.noteNumber = new ArrayList<>(noteNumber);
                        return this;
                    }

                    /**
                     * The high-level results of the adjudication if adjudication has been performed.
                     * 
                     * @param reviewOutcome
                     *     Subdetail level adjudication results
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder reviewOutcome(ClaimResponse.Item.ReviewOutcome reviewOutcome) {
                        this.reviewOutcome = reviewOutcome;
                        return this;
                    }

                    /**
                     * The adjudication results.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param adjudication
                     *     Subdetail level adjudication details
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder adjudication(ClaimResponse.Item.Adjudication... adjudication) {
                        for (ClaimResponse.Item.Adjudication value : adjudication) {
                            this.adjudication.add(value);
                        }
                        return this;
                    }

                    /**
                     * The adjudication results.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param adjudication
                     *     Subdetail level adjudication details
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder adjudication(Collection<ClaimResponse.Item.Adjudication> adjudication) {
                        this.adjudication = new ArrayList<>(adjudication);
                        return this;
                    }

                    /**
                     * Build the {@link SubDetail}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>subDetailSequence</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link SubDetail}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid SubDetail per the base specification
                     */
                    @Override
                    public SubDetail build() {
                        SubDetail subDetail = new SubDetail(this);
                        if (validating) {
                            validate(subDetail);
                        }
                        return subDetail;
                    }

                    protected void validate(SubDetail subDetail) {
                        super.validate(subDetail);
                        ValidationSupport.requireNonNull(subDetail.subDetailSequence, "subDetailSequence");
                        ValidationSupport.checkList(subDetail.traceNumber, "traceNumber", Identifier.class);
                        ValidationSupport.checkList(subDetail.noteNumber, "noteNumber", PositiveInt.class);
                        ValidationSupport.checkList(subDetail.adjudication, "adjudication", ClaimResponse.Item.Adjudication.class);
                        ValidationSupport.requireValueOrChildren(subDetail);
                    }

                    protected Builder from(SubDetail subDetail) {
                        super.from(subDetail);
                        subDetailSequence = subDetail.subDetailSequence;
                        traceNumber.addAll(subDetail.traceNumber);
                        noteNumber.addAll(subDetail.noteNumber);
                        reviewOutcome = subDetail.reviewOutcome;
                        adjudication.addAll(subDetail.adjudication);
                        return this;
                    }
                }
            }
        }
    }

    /**
     * The first-tier service adjudications for payor added product or service lines.
     */
    public static class AddItem extends BackboneElement {
        private final List<PositiveInt> itemSequence;
        private final List<PositiveInt> detailSequence;
        private final List<PositiveInt> subdetailSequence;
        private final List<Identifier> traceNumber;
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization" })
        private final List<Reference> provider;
        @Binding(
            bindingName = "RevenueCenter",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes for the revenue or cost centers supplying the service and/or products.",
            valueSet = "http://hl7.org/fhir/ValueSet/ex-revenue-center"
        )
        private final CodeableConcept revenue;
        @Binding(
            bindingName = "ServiceProduct",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Allowable service and product codes.",
            valueSet = "http://hl7.org/fhir/ValueSet/service-uscls"
        )
        private final CodeableConcept productOrService;
        @Binding(
            bindingName = "ServiceProduct",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/service-uscls"
        )
        private final CodeableConcept productOrServiceEnd;
        @ReferenceTarget({ "DeviceRequest", "MedicationRequest", "NutritionOrder", "ServiceRequest", "SupplyRequest", "VisionPrescription" })
        private final List<Reference> request;
        @Binding(
            bindingName = "Modifiers",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Item type or modifiers codes, eg for Oral whether the treatment is cosmetic or associated with TMJ, or an appliance was lost or stolen.",
            valueSet = "http://hl7.org/fhir/ValueSet/claim-modifiers"
        )
        private final List<CodeableConcept> modifier;
        @Binding(
            bindingName = "ProgramCode",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Program specific reason codes.",
            valueSet = "http://hl7.org/fhir/ValueSet/ex-program-code"
        )
        private final List<CodeableConcept> programCode;
        @Choice({ Date.class, Period.class })
        private final Element serviced;
        @ReferenceTarget({ "Location" })
        @Choice({ CodeableConcept.class, Address.class, Reference.class })
        @Binding(
            bindingName = "ServicePlace",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Place of service: pharmacy, school, prison, etc.",
            valueSet = "http://hl7.org/fhir/ValueSet/service-place"
        )
        private final Element location;
        private final SimpleQuantity quantity;
        private final Money unitPrice;
        private final Decimal factor;
        private final Money tax;
        private final Money net;
        private final List<BodySite> bodySite;
        private final List<PositiveInt> noteNumber;
        private final ClaimResponse.Item.ReviewOutcome reviewOutcome;
        private final List<ClaimResponse.Item.Adjudication> adjudication;
        private final List<Detail> detail;

        private AddItem(Builder builder) {
            super(builder);
            itemSequence = Collections.unmodifiableList(builder.itemSequence);
            detailSequence = Collections.unmodifiableList(builder.detailSequence);
            subdetailSequence = Collections.unmodifiableList(builder.subdetailSequence);
            traceNumber = Collections.unmodifiableList(builder.traceNumber);
            provider = Collections.unmodifiableList(builder.provider);
            revenue = builder.revenue;
            productOrService = builder.productOrService;
            productOrServiceEnd = builder.productOrServiceEnd;
            request = Collections.unmodifiableList(builder.request);
            modifier = Collections.unmodifiableList(builder.modifier);
            programCode = Collections.unmodifiableList(builder.programCode);
            serviced = builder.serviced;
            location = builder.location;
            quantity = builder.quantity;
            unitPrice = builder.unitPrice;
            factor = builder.factor;
            tax = builder.tax;
            net = builder.net;
            bodySite = Collections.unmodifiableList(builder.bodySite);
            noteNumber = Collections.unmodifiableList(builder.noteNumber);
            reviewOutcome = builder.reviewOutcome;
            adjudication = Collections.unmodifiableList(builder.adjudication);
            detail = Collections.unmodifiableList(builder.detail);
        }

        /**
         * Claim items which this service line is intended to replace.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
         */
        public List<PositiveInt> getItemSequence() {
            return itemSequence;
        }

        /**
         * The sequence number of the details within the claim item which this line is intended to replace.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
         */
        public List<PositiveInt> getDetailSequence() {
            return detailSequence;
        }

        /**
         * The sequence number of the sub-details within the details within the claim item which this line is intended to replace.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
         */
        public List<PositiveInt> getSubdetailSequence() {
            return subdetailSequence;
        }

        /**
         * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
         */
        public List<Identifier> getTraceNumber() {
            return traceNumber;
        }

        /**
         * The providers who are authorized for the services rendered to the patient.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getProvider() {
            return provider;
        }

        /**
         * The type of revenue or cost center providing the product and/or service.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRevenue() {
            return revenue;
        }

        /**
         * When the value is a group code then this item collects a set of related item details, otherwise this contains the 
         * product, service, drug or other billing code for the item. This element may be the start of a range of .
         * productOrService codes used in conjunction with .productOrServiceEnd or it may be a solo element where .
         * productOrServiceEnd is not used.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getProductOrService() {
            return productOrService;
        }

        /**
         * This contains the end of a range of product, service, drug or other billing codes for the item. This element is not 
         * used when the .productOrService is a group code. This value may only be present when a .productOfService code has been 
         * provided to convey the start of the range. Typically this value may be used only with preauthorizations and not with 
         * claims.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getProductOrServiceEnd() {
            return productOrServiceEnd;
        }

        /**
         * Request or Referral for Goods or Service to be rendered.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getRequest() {
            return request;
        }

        /**
         * Item typification or modifiers codes to convey additional context for the product or service.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getModifier() {
            return modifier;
        }

        /**
         * Identifies the program under which this may be recovered.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getProgramCode() {
            return programCode;
        }

        /**
         * The date or dates when the service or product was supplied, performed or completed.
         * 
         * @return
         *     An immutable object of type {@link Date} or {@link Period} that may be null.
         */
        public Element getServiced() {
            return serviced;
        }

        /**
         * Where the product or service was provided.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept}, {@link Address} or {@link Reference} that may be null.
         */
        public Element getLocation() {
            return location;
        }

        /**
         * The number of repetitions of a service or product.
         * 
         * @return
         *     An immutable object of type {@link SimpleQuantity} that may be null.
         */
        public SimpleQuantity getQuantity() {
            return quantity;
        }

        /**
         * If the item is not a group then this is the fee for the product or service, otherwise this is the total of the fees 
         * for the details of the group.
         * 
         * @return
         *     An immutable object of type {@link Money} that may be null.
         */
        public Money getUnitPrice() {
            return unitPrice;
        }

        /**
         * A real number that represents a multiplier used in determining the overall value of services delivered and/or goods 
         * received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
         * 
         * @return
         *     An immutable object of type {@link Decimal} that may be null.
         */
        public Decimal getFactor() {
            return factor;
        }

        /**
         * The total of taxes applicable for this product or service.
         * 
         * @return
         *     An immutable object of type {@link Money} that may be null.
         */
        public Money getTax() {
            return tax;
        }

        /**
         * The total amount claimed for the group (if a grouper) or the addItem. Net = unit price * quantity * factor.
         * 
         * @return
         *     An immutable object of type {@link Money} that may be null.
         */
        public Money getNet() {
            return net;
        }

        /**
         * Physical location where the service is performed or applies.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link BodySite} that may be empty.
         */
        public List<BodySite> getBodySite() {
            return bodySite;
        }

        /**
         * The numbers associated with notes below which apply to the adjudication of this item.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
         */
        public List<PositiveInt> getNoteNumber() {
            return noteNumber;
        }

        /**
         * The high-level results of the adjudication if adjudication has been performed.
         * 
         * @return
         *     An immutable object of type {@link ClaimResponse.Item.ReviewOutcome} that may be null.
         */
        public ClaimResponse.Item.ReviewOutcome getReviewOutcome() {
            return reviewOutcome;
        }

        /**
         * The adjudication results.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Adjudication} that may be empty.
         */
        public List<ClaimResponse.Item.Adjudication> getAdjudication() {
            return adjudication;
        }

        /**
         * The second-tier service adjudications for payor added services.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Detail} that may be empty.
         */
        public List<Detail> getDetail() {
            return detail;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !itemSequence.isEmpty() || 
                !detailSequence.isEmpty() || 
                !subdetailSequence.isEmpty() || 
                !traceNumber.isEmpty() || 
                !provider.isEmpty() || 
                (revenue != null) || 
                (productOrService != null) || 
                (productOrServiceEnd != null) || 
                !request.isEmpty() || 
                !modifier.isEmpty() || 
                !programCode.isEmpty() || 
                (serviced != null) || 
                (location != null) || 
                (quantity != null) || 
                (unitPrice != null) || 
                (factor != null) || 
                (tax != null) || 
                (net != null) || 
                !bodySite.isEmpty() || 
                !noteNumber.isEmpty() || 
                (reviewOutcome != null) || 
                !adjudication.isEmpty() || 
                !detail.isEmpty();
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
                    accept(itemSequence, "itemSequence", visitor, PositiveInt.class);
                    accept(detailSequence, "detailSequence", visitor, PositiveInt.class);
                    accept(subdetailSequence, "subdetailSequence", visitor, PositiveInt.class);
                    accept(traceNumber, "traceNumber", visitor, Identifier.class);
                    accept(provider, "provider", visitor, Reference.class);
                    accept(revenue, "revenue", visitor);
                    accept(productOrService, "productOrService", visitor);
                    accept(productOrServiceEnd, "productOrServiceEnd", visitor);
                    accept(request, "request", visitor, Reference.class);
                    accept(modifier, "modifier", visitor, CodeableConcept.class);
                    accept(programCode, "programCode", visitor, CodeableConcept.class);
                    accept(serviced, "serviced", visitor);
                    accept(location, "location", visitor);
                    accept(quantity, "quantity", visitor);
                    accept(unitPrice, "unitPrice", visitor);
                    accept(factor, "factor", visitor);
                    accept(tax, "tax", visitor);
                    accept(net, "net", visitor);
                    accept(bodySite, "bodySite", visitor, BodySite.class);
                    accept(noteNumber, "noteNumber", visitor, PositiveInt.class);
                    accept(reviewOutcome, "reviewOutcome", visitor);
                    accept(adjudication, "adjudication", visitor, ClaimResponse.Item.Adjudication.class);
                    accept(detail, "detail", visitor, Detail.class);
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
            AddItem other = (AddItem) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(itemSequence, other.itemSequence) && 
                Objects.equals(detailSequence, other.detailSequence) && 
                Objects.equals(subdetailSequence, other.subdetailSequence) && 
                Objects.equals(traceNumber, other.traceNumber) && 
                Objects.equals(provider, other.provider) && 
                Objects.equals(revenue, other.revenue) && 
                Objects.equals(productOrService, other.productOrService) && 
                Objects.equals(productOrServiceEnd, other.productOrServiceEnd) && 
                Objects.equals(request, other.request) && 
                Objects.equals(modifier, other.modifier) && 
                Objects.equals(programCode, other.programCode) && 
                Objects.equals(serviced, other.serviced) && 
                Objects.equals(location, other.location) && 
                Objects.equals(quantity, other.quantity) && 
                Objects.equals(unitPrice, other.unitPrice) && 
                Objects.equals(factor, other.factor) && 
                Objects.equals(tax, other.tax) && 
                Objects.equals(net, other.net) && 
                Objects.equals(bodySite, other.bodySite) && 
                Objects.equals(noteNumber, other.noteNumber) && 
                Objects.equals(reviewOutcome, other.reviewOutcome) && 
                Objects.equals(adjudication, other.adjudication) && 
                Objects.equals(detail, other.detail);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    itemSequence, 
                    detailSequence, 
                    subdetailSequence, 
                    traceNumber, 
                    provider, 
                    revenue, 
                    productOrService, 
                    productOrServiceEnd, 
                    request, 
                    modifier, 
                    programCode, 
                    serviced, 
                    location, 
                    quantity, 
                    unitPrice, 
                    factor, 
                    tax, 
                    net, 
                    bodySite, 
                    noteNumber, 
                    reviewOutcome, 
                    adjudication, 
                    detail);
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
            private List<PositiveInt> itemSequence = new ArrayList<>();
            private List<PositiveInt> detailSequence = new ArrayList<>();
            private List<PositiveInt> subdetailSequence = new ArrayList<>();
            private List<Identifier> traceNumber = new ArrayList<>();
            private List<Reference> provider = new ArrayList<>();
            private CodeableConcept revenue;
            private CodeableConcept productOrService;
            private CodeableConcept productOrServiceEnd;
            private List<Reference> request = new ArrayList<>();
            private List<CodeableConcept> modifier = new ArrayList<>();
            private List<CodeableConcept> programCode = new ArrayList<>();
            private Element serviced;
            private Element location;
            private SimpleQuantity quantity;
            private Money unitPrice;
            private Decimal factor;
            private Money tax;
            private Money net;
            private List<BodySite> bodySite = new ArrayList<>();
            private List<PositiveInt> noteNumber = new ArrayList<>();
            private ClaimResponse.Item.ReviewOutcome reviewOutcome;
            private List<ClaimResponse.Item.Adjudication> adjudication = new ArrayList<>();
            private List<Detail> detail = new ArrayList<>();

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
             * Claim items which this service line is intended to replace.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param itemSequence
             *     Item sequence number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder itemSequence(PositiveInt... itemSequence) {
                for (PositiveInt value : itemSequence) {
                    this.itemSequence.add(value);
                }
                return this;
            }

            /**
             * Claim items which this service line is intended to replace.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param itemSequence
             *     Item sequence number
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder itemSequence(Collection<PositiveInt> itemSequence) {
                this.itemSequence = new ArrayList<>(itemSequence);
                return this;
            }

            /**
             * The sequence number of the details within the claim item which this line is intended to replace.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detailSequence
             *     Detail sequence number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder detailSequence(PositiveInt... detailSequence) {
                for (PositiveInt value : detailSequence) {
                    this.detailSequence.add(value);
                }
                return this;
            }

            /**
             * The sequence number of the details within the claim item which this line is intended to replace.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detailSequence
             *     Detail sequence number
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder detailSequence(Collection<PositiveInt> detailSequence) {
                this.detailSequence = new ArrayList<>(detailSequence);
                return this;
            }

            /**
             * The sequence number of the sub-details within the details within the claim item which this line is intended to replace.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param subdetailSequence
             *     Subdetail sequence number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder subdetailSequence(PositiveInt... subdetailSequence) {
                for (PositiveInt value : subdetailSequence) {
                    this.subdetailSequence.add(value);
                }
                return this;
            }

            /**
             * The sequence number of the sub-details within the details within the claim item which this line is intended to replace.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param subdetailSequence
             *     Subdetail sequence number
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder subdetailSequence(Collection<PositiveInt> subdetailSequence) {
                this.subdetailSequence = new ArrayList<>(subdetailSequence);
                return this;
            }

            /**
             * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param traceNumber
             *     Number for tracking
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder traceNumber(Identifier... traceNumber) {
                for (Identifier value : traceNumber) {
                    this.traceNumber.add(value);
                }
                return this;
            }

            /**
             * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param traceNumber
             *     Number for tracking
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder traceNumber(Collection<Identifier> traceNumber) {
                this.traceNumber = new ArrayList<>(traceNumber);
                return this;
            }

            /**
             * The providers who are authorized for the services rendered to the patient.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param provider
             *     Authorized providers
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder provider(Reference... provider) {
                for (Reference value : provider) {
                    this.provider.add(value);
                }
                return this;
            }

            /**
             * The providers who are authorized for the services rendered to the patient.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param provider
             *     Authorized providers
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder provider(Collection<Reference> provider) {
                this.provider = new ArrayList<>(provider);
                return this;
            }

            /**
             * The type of revenue or cost center providing the product and/or service.
             * 
             * @param revenue
             *     Revenue or cost center code
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder revenue(CodeableConcept revenue) {
                this.revenue = revenue;
                return this;
            }

            /**
             * When the value is a group code then this item collects a set of related item details, otherwise this contains the 
             * product, service, drug or other billing code for the item. This element may be the start of a range of .
             * productOrService codes used in conjunction with .productOrServiceEnd or it may be a solo element where .
             * productOrServiceEnd is not used.
             * 
             * @param productOrService
             *     Billing, service, product, or drug code
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder productOrService(CodeableConcept productOrService) {
                this.productOrService = productOrService;
                return this;
            }

            /**
             * This contains the end of a range of product, service, drug or other billing codes for the item. This element is not 
             * used when the .productOrService is a group code. This value may only be present when a .productOfService code has been 
             * provided to convey the start of the range. Typically this value may be used only with preauthorizations and not with 
             * claims.
             * 
             * @param productOrServiceEnd
             *     End of a range of codes
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder productOrServiceEnd(CodeableConcept productOrServiceEnd) {
                this.productOrServiceEnd = productOrServiceEnd;
                return this;
            }

            /**
             * Request or Referral for Goods or Service to be rendered.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link DeviceRequest}</li>
             * <li>{@link MedicationRequest}</li>
             * <li>{@link NutritionOrder}</li>
             * <li>{@link ServiceRequest}</li>
             * <li>{@link SupplyRequest}</li>
             * <li>{@link VisionPrescription}</li>
             * </ul>
             * 
             * @param request
             *     Request or Referral for Service
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder request(Reference... request) {
                for (Reference value : request) {
                    this.request.add(value);
                }
                return this;
            }

            /**
             * Request or Referral for Goods or Service to be rendered.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link DeviceRequest}</li>
             * <li>{@link MedicationRequest}</li>
             * <li>{@link NutritionOrder}</li>
             * <li>{@link ServiceRequest}</li>
             * <li>{@link SupplyRequest}</li>
             * <li>{@link VisionPrescription}</li>
             * </ul>
             * 
             * @param request
             *     Request or Referral for Service
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder request(Collection<Reference> request) {
                this.request = new ArrayList<>(request);
                return this;
            }

            /**
             * Item typification or modifiers codes to convey additional context for the product or service.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifier
             *     Service/Product billing modifiers
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder modifier(CodeableConcept... modifier) {
                for (CodeableConcept value : modifier) {
                    this.modifier.add(value);
                }
                return this;
            }

            /**
             * Item typification or modifiers codes to convey additional context for the product or service.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifier
             *     Service/Product billing modifiers
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder modifier(Collection<CodeableConcept> modifier) {
                this.modifier = new ArrayList<>(modifier);
                return this;
            }

            /**
             * Identifies the program under which this may be recovered.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param programCode
             *     Program the product or service is provided under
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder programCode(CodeableConcept... programCode) {
                for (CodeableConcept value : programCode) {
                    this.programCode.add(value);
                }
                return this;
            }

            /**
             * Identifies the program under which this may be recovered.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param programCode
             *     Program the product or service is provided under
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder programCode(Collection<CodeableConcept> programCode) {
                this.programCode = new ArrayList<>(programCode);
                return this;
            }

            /**
             * Convenience method for setting {@code serviced} with choice type Date.
             * 
             * @param serviced
             *     Date or dates of service or product delivery
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #serviced(Element)
             */
            public Builder serviced(java.time.LocalDate serviced) {
                this.serviced = (serviced == null) ? null : Date.of(serviced);
                return this;
            }

            /**
             * The date or dates when the service or product was supplied, performed or completed.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Date}</li>
             * <li>{@link Period}</li>
             * </ul>
             * 
             * @param serviced
             *     Date or dates of service or product delivery
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder serviced(Element serviced) {
                this.serviced = serviced;
                return this;
            }

            /**
             * Where the product or service was provided.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Address}</li>
             * <li>{@link Reference}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param location
             *     Place of service or where product was supplied
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(Element location) {
                this.location = location;
                return this;
            }

            /**
             * The number of repetitions of a service or product.
             * 
             * @param quantity
             *     Count of products or services
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder quantity(SimpleQuantity quantity) {
                this.quantity = quantity;
                return this;
            }

            /**
             * If the item is not a group then this is the fee for the product or service, otherwise this is the total of the fees 
             * for the details of the group.
             * 
             * @param unitPrice
             *     Fee, charge or cost per item
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder unitPrice(Money unitPrice) {
                this.unitPrice = unitPrice;
                return this;
            }

            /**
             * A real number that represents a multiplier used in determining the overall value of services delivered and/or goods 
             * received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
             * 
             * @param factor
             *     Price scaling factor
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder factor(Decimal factor) {
                this.factor = factor;
                return this;
            }

            /**
             * The total of taxes applicable for this product or service.
             * 
             * @param tax
             *     Total tax
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder tax(Money tax) {
                this.tax = tax;
                return this;
            }

            /**
             * The total amount claimed for the group (if a grouper) or the addItem. Net = unit price * quantity * factor.
             * 
             * @param net
             *     Total item cost
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder net(Money net) {
                this.net = net;
                return this;
            }

            /**
             * Physical location where the service is performed or applies.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param bodySite
             *     Anatomical location
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder bodySite(BodySite... bodySite) {
                for (BodySite value : bodySite) {
                    this.bodySite.add(value);
                }
                return this;
            }

            /**
             * Physical location where the service is performed or applies.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param bodySite
             *     Anatomical location
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder bodySite(Collection<BodySite> bodySite) {
                this.bodySite = new ArrayList<>(bodySite);
                return this;
            }

            /**
             * The numbers associated with notes below which apply to the adjudication of this item.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param noteNumber
             *     Applicable note numbers
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder noteNumber(PositiveInt... noteNumber) {
                for (PositiveInt value : noteNumber) {
                    this.noteNumber.add(value);
                }
                return this;
            }

            /**
             * The numbers associated with notes below which apply to the adjudication of this item.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param noteNumber
             *     Applicable note numbers
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder noteNumber(Collection<PositiveInt> noteNumber) {
                this.noteNumber = new ArrayList<>(noteNumber);
                return this;
            }

            /**
             * The high-level results of the adjudication if adjudication has been performed.
             * 
             * @param reviewOutcome
             *     Added items adjudication results
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reviewOutcome(ClaimResponse.Item.ReviewOutcome reviewOutcome) {
                this.reviewOutcome = reviewOutcome;
                return this;
            }

            /**
             * The adjudication results.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param adjudication
             *     Added items adjudication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder adjudication(ClaimResponse.Item.Adjudication... adjudication) {
                for (ClaimResponse.Item.Adjudication value : adjudication) {
                    this.adjudication.add(value);
                }
                return this;
            }

            /**
             * The adjudication results.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param adjudication
             *     Added items adjudication
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder adjudication(Collection<ClaimResponse.Item.Adjudication> adjudication) {
                this.adjudication = new ArrayList<>(adjudication);
                return this;
            }

            /**
             * The second-tier service adjudications for payor added services.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Insurer added line details
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder detail(Detail... detail) {
                for (Detail value : detail) {
                    this.detail.add(value);
                }
                return this;
            }

            /**
             * The second-tier service adjudications for payor added services.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     Insurer added line details
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder detail(Collection<Detail> detail) {
                this.detail = new ArrayList<>(detail);
                return this;
            }

            /**
             * Build the {@link AddItem}
             * 
             * @return
             *     An immutable object of type {@link AddItem}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid AddItem per the base specification
             */
            @Override
            public AddItem build() {
                AddItem addItem = new AddItem(this);
                if (validating) {
                    validate(addItem);
                }
                return addItem;
            }

            protected void validate(AddItem addItem) {
                super.validate(addItem);
                ValidationSupport.checkList(addItem.itemSequence, "itemSequence", PositiveInt.class);
                ValidationSupport.checkList(addItem.detailSequence, "detailSequence", PositiveInt.class);
                ValidationSupport.checkList(addItem.subdetailSequence, "subdetailSequence", PositiveInt.class);
                ValidationSupport.checkList(addItem.traceNumber, "traceNumber", Identifier.class);
                ValidationSupport.checkList(addItem.provider, "provider", Reference.class);
                ValidationSupport.checkList(addItem.request, "request", Reference.class);
                ValidationSupport.checkList(addItem.modifier, "modifier", CodeableConcept.class);
                ValidationSupport.checkList(addItem.programCode, "programCode", CodeableConcept.class);
                ValidationSupport.choiceElement(addItem.serviced, "serviced", Date.class, Period.class);
                ValidationSupport.choiceElement(addItem.location, "location", CodeableConcept.class, Address.class, Reference.class);
                ValidationSupport.checkList(addItem.bodySite, "bodySite", BodySite.class);
                ValidationSupport.checkList(addItem.noteNumber, "noteNumber", PositiveInt.class);
                ValidationSupport.checkList(addItem.adjudication, "adjudication", ClaimResponse.Item.Adjudication.class);
                ValidationSupport.checkList(addItem.detail, "detail", Detail.class);
                ValidationSupport.checkReferenceType(addItem.provider, "provider", "Practitioner", "PractitionerRole", "Organization");
                ValidationSupport.checkReferenceType(addItem.request, "request", "DeviceRequest", "MedicationRequest", "NutritionOrder", "ServiceRequest", "SupplyRequest", "VisionPrescription");
                ValidationSupport.checkReferenceType(addItem.location, "location", "Location");
                ValidationSupport.requireValueOrChildren(addItem);
            }

            protected Builder from(AddItem addItem) {
                super.from(addItem);
                itemSequence.addAll(addItem.itemSequence);
                detailSequence.addAll(addItem.detailSequence);
                subdetailSequence.addAll(addItem.subdetailSequence);
                traceNumber.addAll(addItem.traceNumber);
                provider.addAll(addItem.provider);
                revenue = addItem.revenue;
                productOrService = addItem.productOrService;
                productOrServiceEnd = addItem.productOrServiceEnd;
                request.addAll(addItem.request);
                modifier.addAll(addItem.modifier);
                programCode.addAll(addItem.programCode);
                serviced = addItem.serviced;
                location = addItem.location;
                quantity = addItem.quantity;
                unitPrice = addItem.unitPrice;
                factor = addItem.factor;
                tax = addItem.tax;
                net = addItem.net;
                bodySite.addAll(addItem.bodySite);
                noteNumber.addAll(addItem.noteNumber);
                reviewOutcome = addItem.reviewOutcome;
                adjudication.addAll(addItem.adjudication);
                detail.addAll(addItem.detail);
                return this;
            }
        }

        /**
         * Physical location where the service is performed or applies.
         */
        public static class BodySite extends BackboneElement {
            @Binding(
                bindingName = "OralSites",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/tooth"
            )
            @Required
            private final List<CodeableReference> site;
            @Binding(
                bindingName = "Surface",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/surface"
            )
            private final List<CodeableConcept> subSite;

            private BodySite(Builder builder) {
                super(builder);
                site = Collections.unmodifiableList(builder.site);
                subSite = Collections.unmodifiableList(builder.subSite);
            }

            /**
             * Physical service site on the patient (limb, tooth, etc.).
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that is non-empty.
             */
            public List<CodeableReference> getSite() {
                return site;
            }

            /**
             * A region or surface of the bodySite, e.g. limb region or tooth surface(s).
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getSubSite() {
                return subSite;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !site.isEmpty() || 
                    !subSite.isEmpty();
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
                        accept(site, "site", visitor, CodeableReference.class);
                        accept(subSite, "subSite", visitor, CodeableConcept.class);
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
                BodySite other = (BodySite) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(site, other.site) && 
                    Objects.equals(subSite, other.subSite);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        site, 
                        subSite);
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
                private List<CodeableReference> site = new ArrayList<>();
                private List<CodeableConcept> subSite = new ArrayList<>();

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
                 * Physical service site on the patient (limb, tooth, etc.).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param site
                 *     Location
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder site(CodeableReference... site) {
                    for (CodeableReference value : site) {
                        this.site.add(value);
                    }
                    return this;
                }

                /**
                 * Physical service site on the patient (limb, tooth, etc.).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param site
                 *     Location
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder site(Collection<CodeableReference> site) {
                    this.site = new ArrayList<>(site);
                    return this;
                }

                /**
                 * A region or surface of the bodySite, e.g. limb region or tooth surface(s).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param subSite
                 *     Sub-location
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder subSite(CodeableConcept... subSite) {
                    for (CodeableConcept value : subSite) {
                        this.subSite.add(value);
                    }
                    return this;
                }

                /**
                 * A region or surface of the bodySite, e.g. limb region or tooth surface(s).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param subSite
                 *     Sub-location
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder subSite(Collection<CodeableConcept> subSite) {
                    this.subSite = new ArrayList<>(subSite);
                    return this;
                }

                /**
                 * Build the {@link BodySite}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>site</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link BodySite}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid BodySite per the base specification
                 */
                @Override
                public BodySite build() {
                    BodySite bodySite = new BodySite(this);
                    if (validating) {
                        validate(bodySite);
                    }
                    return bodySite;
                }

                protected void validate(BodySite bodySite) {
                    super.validate(bodySite);
                    ValidationSupport.checkNonEmptyList(bodySite.site, "site", CodeableReference.class);
                    ValidationSupport.checkList(bodySite.subSite, "subSite", CodeableConcept.class);
                    ValidationSupport.requireValueOrChildren(bodySite);
                }

                protected Builder from(BodySite bodySite) {
                    super.from(bodySite);
                    site.addAll(bodySite.site);
                    subSite.addAll(bodySite.subSite);
                    return this;
                }
            }
        }

        /**
         * The second-tier service adjudications for payor added services.
         */
        public static class Detail extends BackboneElement {
            private final List<Identifier> traceNumber;
            @Binding(
                bindingName = "RevenueCenter",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Codes for the revenue or cost centers supplying the service and/or products.",
                valueSet = "http://hl7.org/fhir/ValueSet/ex-revenue-center"
            )
            private final CodeableConcept revenue;
            @Binding(
                bindingName = "ServiceProduct",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Allowable service and product codes.",
                valueSet = "http://hl7.org/fhir/ValueSet/service-uscls"
            )
            private final CodeableConcept productOrService;
            @Binding(
                bindingName = "ServiceProduct",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/service-uscls"
            )
            private final CodeableConcept productOrServiceEnd;
            @Binding(
                bindingName = "Modifiers",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Item type or modifiers codes, eg for Oral whether the treatment is cosmetic or associated with TMJ, or an appliance was lost or stolen.",
                valueSet = "http://hl7.org/fhir/ValueSet/claim-modifiers"
            )
            private final List<CodeableConcept> modifier;
            private final SimpleQuantity quantity;
            private final Money unitPrice;
            private final Decimal factor;
            private final Money tax;
            private final Money net;
            private final List<PositiveInt> noteNumber;
            private final ClaimResponse.Item.ReviewOutcome reviewOutcome;
            private final List<ClaimResponse.Item.Adjudication> adjudication;
            private final List<SubDetail> subDetail;

            private Detail(Builder builder) {
                super(builder);
                traceNumber = Collections.unmodifiableList(builder.traceNumber);
                revenue = builder.revenue;
                productOrService = builder.productOrService;
                productOrServiceEnd = builder.productOrServiceEnd;
                modifier = Collections.unmodifiableList(builder.modifier);
                quantity = builder.quantity;
                unitPrice = builder.unitPrice;
                factor = builder.factor;
                tax = builder.tax;
                net = builder.net;
                noteNumber = Collections.unmodifiableList(builder.noteNumber);
                reviewOutcome = builder.reviewOutcome;
                adjudication = Collections.unmodifiableList(builder.adjudication);
                subDetail = Collections.unmodifiableList(builder.subDetail);
            }

            /**
             * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
             */
            public List<Identifier> getTraceNumber() {
                return traceNumber;
            }

            /**
             * The type of revenue or cost center providing the product and/or service.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getRevenue() {
                return revenue;
            }

            /**
             * When the value is a group code then this item collects a set of related item details, otherwise this contains the 
             * product, service, drug or other billing code for the item. This element may be the start of a range of .
             * productOrService codes used in conjunction with .productOrServiceEnd or it may be a solo element where .
             * productOrServiceEnd is not used.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getProductOrService() {
                return productOrService;
            }

            /**
             * This contains the end of a range of product, service, drug or other billing codes for the item. This element is not 
             * used when the .productOrService is a group code. This value may only be present when a .productOfService code has been 
             * provided to convey the start of the range. Typically this value may be used only with preauthorizations and not with 
             * claims.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getProductOrServiceEnd() {
                return productOrServiceEnd;
            }

            /**
             * Item typification or modifiers codes to convey additional context for the product or service.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getModifier() {
                return modifier;
            }

            /**
             * The number of repetitions of a service or product.
             * 
             * @return
             *     An immutable object of type {@link SimpleQuantity} that may be null.
             */
            public SimpleQuantity getQuantity() {
                return quantity;
            }

            /**
             * If the item is not a group then this is the fee for the product or service, otherwise this is the total of the fees 
             * for the details of the group.
             * 
             * @return
             *     An immutable object of type {@link Money} that may be null.
             */
            public Money getUnitPrice() {
                return unitPrice;
            }

            /**
             * A real number that represents a multiplier used in determining the overall value of services delivered and/or goods 
             * received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
             * 
             * @return
             *     An immutable object of type {@link Decimal} that may be null.
             */
            public Decimal getFactor() {
                return factor;
            }

            /**
             * The total of taxes applicable for this product or service.
             * 
             * @return
             *     An immutable object of type {@link Money} that may be null.
             */
            public Money getTax() {
                return tax;
            }

            /**
             * The total amount claimed for the group (if a grouper) or the addItem.detail. Net = unit price * quantity * factor.
             * 
             * @return
             *     An immutable object of type {@link Money} that may be null.
             */
            public Money getNet() {
                return net;
            }

            /**
             * The numbers associated with notes below which apply to the adjudication of this item.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
             */
            public List<PositiveInt> getNoteNumber() {
                return noteNumber;
            }

            /**
             * The high-level results of the adjudication if adjudication has been performed.
             * 
             * @return
             *     An immutable object of type {@link ClaimResponse.Item.ReviewOutcome} that may be null.
             */
            public ClaimResponse.Item.ReviewOutcome getReviewOutcome() {
                return reviewOutcome;
            }

            /**
             * The adjudication results.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Adjudication} that may be empty.
             */
            public List<ClaimResponse.Item.Adjudication> getAdjudication() {
                return adjudication;
            }

            /**
             * The third-tier service adjudications for payor added services.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link SubDetail} that may be empty.
             */
            public List<SubDetail> getSubDetail() {
                return subDetail;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !traceNumber.isEmpty() || 
                    (revenue != null) || 
                    (productOrService != null) || 
                    (productOrServiceEnd != null) || 
                    !modifier.isEmpty() || 
                    (quantity != null) || 
                    (unitPrice != null) || 
                    (factor != null) || 
                    (tax != null) || 
                    (net != null) || 
                    !noteNumber.isEmpty() || 
                    (reviewOutcome != null) || 
                    !adjudication.isEmpty() || 
                    !subDetail.isEmpty();
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
                        accept(traceNumber, "traceNumber", visitor, Identifier.class);
                        accept(revenue, "revenue", visitor);
                        accept(productOrService, "productOrService", visitor);
                        accept(productOrServiceEnd, "productOrServiceEnd", visitor);
                        accept(modifier, "modifier", visitor, CodeableConcept.class);
                        accept(quantity, "quantity", visitor);
                        accept(unitPrice, "unitPrice", visitor);
                        accept(factor, "factor", visitor);
                        accept(tax, "tax", visitor);
                        accept(net, "net", visitor);
                        accept(noteNumber, "noteNumber", visitor, PositiveInt.class);
                        accept(reviewOutcome, "reviewOutcome", visitor);
                        accept(adjudication, "adjudication", visitor, ClaimResponse.Item.Adjudication.class);
                        accept(subDetail, "subDetail", visitor, SubDetail.class);
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
                Detail other = (Detail) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(traceNumber, other.traceNumber) && 
                    Objects.equals(revenue, other.revenue) && 
                    Objects.equals(productOrService, other.productOrService) && 
                    Objects.equals(productOrServiceEnd, other.productOrServiceEnd) && 
                    Objects.equals(modifier, other.modifier) && 
                    Objects.equals(quantity, other.quantity) && 
                    Objects.equals(unitPrice, other.unitPrice) && 
                    Objects.equals(factor, other.factor) && 
                    Objects.equals(tax, other.tax) && 
                    Objects.equals(net, other.net) && 
                    Objects.equals(noteNumber, other.noteNumber) && 
                    Objects.equals(reviewOutcome, other.reviewOutcome) && 
                    Objects.equals(adjudication, other.adjudication) && 
                    Objects.equals(subDetail, other.subDetail);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        traceNumber, 
                        revenue, 
                        productOrService, 
                        productOrServiceEnd, 
                        modifier, 
                        quantity, 
                        unitPrice, 
                        factor, 
                        tax, 
                        net, 
                        noteNumber, 
                        reviewOutcome, 
                        adjudication, 
                        subDetail);
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
                private List<Identifier> traceNumber = new ArrayList<>();
                private CodeableConcept revenue;
                private CodeableConcept productOrService;
                private CodeableConcept productOrServiceEnd;
                private List<CodeableConcept> modifier = new ArrayList<>();
                private SimpleQuantity quantity;
                private Money unitPrice;
                private Decimal factor;
                private Money tax;
                private Money net;
                private List<PositiveInt> noteNumber = new ArrayList<>();
                private ClaimResponse.Item.ReviewOutcome reviewOutcome;
                private List<ClaimResponse.Item.Adjudication> adjudication = new ArrayList<>();
                private List<SubDetail> subDetail = new ArrayList<>();

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
                 * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param traceNumber
                 *     Number for tracking
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder traceNumber(Identifier... traceNumber) {
                    for (Identifier value : traceNumber) {
                        this.traceNumber.add(value);
                    }
                    return this;
                }

                /**
                 * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param traceNumber
                 *     Number for tracking
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder traceNumber(Collection<Identifier> traceNumber) {
                    this.traceNumber = new ArrayList<>(traceNumber);
                    return this;
                }

                /**
                 * The type of revenue or cost center providing the product and/or service.
                 * 
                 * @param revenue
                 *     Revenue or cost center code
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder revenue(CodeableConcept revenue) {
                    this.revenue = revenue;
                    return this;
                }

                /**
                 * When the value is a group code then this item collects a set of related item details, otherwise this contains the 
                 * product, service, drug or other billing code for the item. This element may be the start of a range of .
                 * productOrService codes used in conjunction with .productOrServiceEnd or it may be a solo element where .
                 * productOrServiceEnd is not used.
                 * 
                 * @param productOrService
                 *     Billing, service, product, or drug code
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder productOrService(CodeableConcept productOrService) {
                    this.productOrService = productOrService;
                    return this;
                }

                /**
                 * This contains the end of a range of product, service, drug or other billing codes for the item. This element is not 
                 * used when the .productOrService is a group code. This value may only be present when a .productOfService code has been 
                 * provided to convey the start of the range. Typically this value may be used only with preauthorizations and not with 
                 * claims.
                 * 
                 * @param productOrServiceEnd
                 *     End of a range of codes
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder productOrServiceEnd(CodeableConcept productOrServiceEnd) {
                    this.productOrServiceEnd = productOrServiceEnd;
                    return this;
                }

                /**
                 * Item typification or modifiers codes to convey additional context for the product or service.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifier
                 *     Service/Product billing modifiers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder modifier(CodeableConcept... modifier) {
                    for (CodeableConcept value : modifier) {
                        this.modifier.add(value);
                    }
                    return this;
                }

                /**
                 * Item typification or modifiers codes to convey additional context for the product or service.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifier
                 *     Service/Product billing modifiers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder modifier(Collection<CodeableConcept> modifier) {
                    this.modifier = new ArrayList<>(modifier);
                    return this;
                }

                /**
                 * The number of repetitions of a service or product.
                 * 
                 * @param quantity
                 *     Count of products or services
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(SimpleQuantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * If the item is not a group then this is the fee for the product or service, otherwise this is the total of the fees 
                 * for the details of the group.
                 * 
                 * @param unitPrice
                 *     Fee, charge or cost per item
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder unitPrice(Money unitPrice) {
                    this.unitPrice = unitPrice;
                    return this;
                }

                /**
                 * A real number that represents a multiplier used in determining the overall value of services delivered and/or goods 
                 * received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
                 * 
                 * @param factor
                 *     Price scaling factor
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder factor(Decimal factor) {
                    this.factor = factor;
                    return this;
                }

                /**
                 * The total of taxes applicable for this product or service.
                 * 
                 * @param tax
                 *     Total tax
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder tax(Money tax) {
                    this.tax = tax;
                    return this;
                }

                /**
                 * The total amount claimed for the group (if a grouper) or the addItem.detail. Net = unit price * quantity * factor.
                 * 
                 * @param net
                 *     Total item cost
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder net(Money net) {
                    this.net = net;
                    return this;
                }

                /**
                 * The numbers associated with notes below which apply to the adjudication of this item.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param noteNumber
                 *     Applicable note numbers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder noteNumber(PositiveInt... noteNumber) {
                    for (PositiveInt value : noteNumber) {
                        this.noteNumber.add(value);
                    }
                    return this;
                }

                /**
                 * The numbers associated with notes below which apply to the adjudication of this item.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param noteNumber
                 *     Applicable note numbers
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder noteNumber(Collection<PositiveInt> noteNumber) {
                    this.noteNumber = new ArrayList<>(noteNumber);
                    return this;
                }

                /**
                 * The high-level results of the adjudication if adjudication has been performed.
                 * 
                 * @param reviewOutcome
                 *     Added items detail level adjudication results
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder reviewOutcome(ClaimResponse.Item.ReviewOutcome reviewOutcome) {
                    this.reviewOutcome = reviewOutcome;
                    return this;
                }

                /**
                 * The adjudication results.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param adjudication
                 *     Added items detail adjudication
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder adjudication(ClaimResponse.Item.Adjudication... adjudication) {
                    for (ClaimResponse.Item.Adjudication value : adjudication) {
                        this.adjudication.add(value);
                    }
                    return this;
                }

                /**
                 * The adjudication results.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param adjudication
                 *     Added items detail adjudication
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder adjudication(Collection<ClaimResponse.Item.Adjudication> adjudication) {
                    this.adjudication = new ArrayList<>(adjudication);
                    return this;
                }

                /**
                 * The third-tier service adjudications for payor added services.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param subDetail
                 *     Insurer added line items
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder subDetail(SubDetail... subDetail) {
                    for (SubDetail value : subDetail) {
                        this.subDetail.add(value);
                    }
                    return this;
                }

                /**
                 * The third-tier service adjudications for payor added services.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param subDetail
                 *     Insurer added line items
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder subDetail(Collection<SubDetail> subDetail) {
                    this.subDetail = new ArrayList<>(subDetail);
                    return this;
                }

                /**
                 * Build the {@link Detail}
                 * 
                 * @return
                 *     An immutable object of type {@link Detail}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Detail per the base specification
                 */
                @Override
                public Detail build() {
                    Detail detail = new Detail(this);
                    if (validating) {
                        validate(detail);
                    }
                    return detail;
                }

                protected void validate(Detail detail) {
                    super.validate(detail);
                    ValidationSupport.checkList(detail.traceNumber, "traceNumber", Identifier.class);
                    ValidationSupport.checkList(detail.modifier, "modifier", CodeableConcept.class);
                    ValidationSupport.checkList(detail.noteNumber, "noteNumber", PositiveInt.class);
                    ValidationSupport.checkList(detail.adjudication, "adjudication", ClaimResponse.Item.Adjudication.class);
                    ValidationSupport.checkList(detail.subDetail, "subDetail", SubDetail.class);
                    ValidationSupport.requireValueOrChildren(detail);
                }

                protected Builder from(Detail detail) {
                    super.from(detail);
                    traceNumber.addAll(detail.traceNumber);
                    revenue = detail.revenue;
                    productOrService = detail.productOrService;
                    productOrServiceEnd = detail.productOrServiceEnd;
                    modifier.addAll(detail.modifier);
                    quantity = detail.quantity;
                    unitPrice = detail.unitPrice;
                    factor = detail.factor;
                    tax = detail.tax;
                    net = detail.net;
                    noteNumber.addAll(detail.noteNumber);
                    reviewOutcome = detail.reviewOutcome;
                    adjudication.addAll(detail.adjudication);
                    subDetail.addAll(detail.subDetail);
                    return this;
                }
            }

            /**
             * The third-tier service adjudications for payor added services.
             */
            public static class SubDetail extends BackboneElement {
                private final List<Identifier> traceNumber;
                @Binding(
                    bindingName = "RevenueCenter",
                    strength = BindingStrength.Value.EXAMPLE,
                    description = "Codes for the revenue or cost centers supplying the service and/or products.",
                    valueSet = "http://hl7.org/fhir/ValueSet/ex-revenue-center"
                )
                private final CodeableConcept revenue;
                @Binding(
                    bindingName = "ServiceProduct",
                    strength = BindingStrength.Value.EXAMPLE,
                    description = "Allowable service and product codes.",
                    valueSet = "http://hl7.org/fhir/ValueSet/service-uscls"
                )
                private final CodeableConcept productOrService;
                @Binding(
                    bindingName = "ServiceProduct",
                    strength = BindingStrength.Value.EXAMPLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/service-uscls"
                )
                private final CodeableConcept productOrServiceEnd;
                @Binding(
                    bindingName = "Modifiers",
                    strength = BindingStrength.Value.EXAMPLE,
                    description = "Item type or modifiers codes, eg for Oral whether the treatment is cosmetic or associated with TMJ, or an appliance was lost or stolen.",
                    valueSet = "http://hl7.org/fhir/ValueSet/claim-modifiers"
                )
                private final List<CodeableConcept> modifier;
                private final SimpleQuantity quantity;
                private final Money unitPrice;
                private final Decimal factor;
                private final Money tax;
                private final Money net;
                private final List<PositiveInt> noteNumber;
                private final ClaimResponse.Item.ReviewOutcome reviewOutcome;
                private final List<ClaimResponse.Item.Adjudication> adjudication;

                private SubDetail(Builder builder) {
                    super(builder);
                    traceNumber = Collections.unmodifiableList(builder.traceNumber);
                    revenue = builder.revenue;
                    productOrService = builder.productOrService;
                    productOrServiceEnd = builder.productOrServiceEnd;
                    modifier = Collections.unmodifiableList(builder.modifier);
                    quantity = builder.quantity;
                    unitPrice = builder.unitPrice;
                    factor = builder.factor;
                    tax = builder.tax;
                    net = builder.net;
                    noteNumber = Collections.unmodifiableList(builder.noteNumber);
                    reviewOutcome = builder.reviewOutcome;
                    adjudication = Collections.unmodifiableList(builder.adjudication);
                }

                /**
                 * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
                 */
                public List<Identifier> getTraceNumber() {
                    return traceNumber;
                }

                /**
                 * The type of revenue or cost center providing the product and/or service.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getRevenue() {
                    return revenue;
                }

                /**
                 * When the value is a group code then this item collects a set of related item details, otherwise this contains the 
                 * product, service, drug or other billing code for the item. This element may be the start of a range of .
                 * productOrService codes used in conjunction with .productOrServiceEnd or it may be a solo element where .
                 * productOrServiceEnd is not used.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getProductOrService() {
                    return productOrService;
                }

                /**
                 * This contains the end of a range of product, service, drug or other billing codes for the item. This element is not 
                 * used when the .productOrService is a group code. This value may only be present when a .productOfService code has been 
                 * provided to convey the start of the range. Typically this value may be used only with preauthorizations and not with 
                 * claims.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getProductOrServiceEnd() {
                    return productOrServiceEnd;
                }

                /**
                 * Item typification or modifiers codes to convey additional context for the product or service.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
                 */
                public List<CodeableConcept> getModifier() {
                    return modifier;
                }

                /**
                 * The number of repetitions of a service or product.
                 * 
                 * @return
                 *     An immutable object of type {@link SimpleQuantity} that may be null.
                 */
                public SimpleQuantity getQuantity() {
                    return quantity;
                }

                /**
                 * If the item is not a group then this is the fee for the product or service, otherwise this is the total of the fees 
                 * for the details of the group.
                 * 
                 * @return
                 *     An immutable object of type {@link Money} that may be null.
                 */
                public Money getUnitPrice() {
                    return unitPrice;
                }

                /**
                 * A real number that represents a multiplier used in determining the overall value of services delivered and/or goods 
                 * received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
                 * 
                 * @return
                 *     An immutable object of type {@link Decimal} that may be null.
                 */
                public Decimal getFactor() {
                    return factor;
                }

                /**
                 * The total of taxes applicable for this product or service.
                 * 
                 * @return
                 *     An immutable object of type {@link Money} that may be null.
                 */
                public Money getTax() {
                    return tax;
                }

                /**
                 * The total amount claimed for the addItem.detail.subDetail. Net = unit price * quantity * factor.
                 * 
                 * @return
                 *     An immutable object of type {@link Money} that may be null.
                 */
                public Money getNet() {
                    return net;
                }

                /**
                 * The numbers associated with notes below which apply to the adjudication of this item.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link PositiveInt} that may be empty.
                 */
                public List<PositiveInt> getNoteNumber() {
                    return noteNumber;
                }

                /**
                 * The high-level results of the adjudication if adjudication has been performed.
                 * 
                 * @return
                 *     An immutable object of type {@link ClaimResponse.Item.ReviewOutcome} that may be null.
                 */
                public ClaimResponse.Item.ReviewOutcome getReviewOutcome() {
                    return reviewOutcome;
                }

                /**
                 * The adjudication results.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Adjudication} that may be empty.
                 */
                public List<ClaimResponse.Item.Adjudication> getAdjudication() {
                    return adjudication;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        !traceNumber.isEmpty() || 
                        (revenue != null) || 
                        (productOrService != null) || 
                        (productOrServiceEnd != null) || 
                        !modifier.isEmpty() || 
                        (quantity != null) || 
                        (unitPrice != null) || 
                        (factor != null) || 
                        (tax != null) || 
                        (net != null) || 
                        !noteNumber.isEmpty() || 
                        (reviewOutcome != null) || 
                        !adjudication.isEmpty();
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
                            accept(traceNumber, "traceNumber", visitor, Identifier.class);
                            accept(revenue, "revenue", visitor);
                            accept(productOrService, "productOrService", visitor);
                            accept(productOrServiceEnd, "productOrServiceEnd", visitor);
                            accept(modifier, "modifier", visitor, CodeableConcept.class);
                            accept(quantity, "quantity", visitor);
                            accept(unitPrice, "unitPrice", visitor);
                            accept(factor, "factor", visitor);
                            accept(tax, "tax", visitor);
                            accept(net, "net", visitor);
                            accept(noteNumber, "noteNumber", visitor, PositiveInt.class);
                            accept(reviewOutcome, "reviewOutcome", visitor);
                            accept(adjudication, "adjudication", visitor, ClaimResponse.Item.Adjudication.class);
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
                    SubDetail other = (SubDetail) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(traceNumber, other.traceNumber) && 
                        Objects.equals(revenue, other.revenue) && 
                        Objects.equals(productOrService, other.productOrService) && 
                        Objects.equals(productOrServiceEnd, other.productOrServiceEnd) && 
                        Objects.equals(modifier, other.modifier) && 
                        Objects.equals(quantity, other.quantity) && 
                        Objects.equals(unitPrice, other.unitPrice) && 
                        Objects.equals(factor, other.factor) && 
                        Objects.equals(tax, other.tax) && 
                        Objects.equals(net, other.net) && 
                        Objects.equals(noteNumber, other.noteNumber) && 
                        Objects.equals(reviewOutcome, other.reviewOutcome) && 
                        Objects.equals(adjudication, other.adjudication);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            traceNumber, 
                            revenue, 
                            productOrService, 
                            productOrServiceEnd, 
                            modifier, 
                            quantity, 
                            unitPrice, 
                            factor, 
                            tax, 
                            net, 
                            noteNumber, 
                            reviewOutcome, 
                            adjudication);
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
                    private List<Identifier> traceNumber = new ArrayList<>();
                    private CodeableConcept revenue;
                    private CodeableConcept productOrService;
                    private CodeableConcept productOrServiceEnd;
                    private List<CodeableConcept> modifier = new ArrayList<>();
                    private SimpleQuantity quantity;
                    private Money unitPrice;
                    private Decimal factor;
                    private Money tax;
                    private Money net;
                    private List<PositiveInt> noteNumber = new ArrayList<>();
                    private ClaimResponse.Item.ReviewOutcome reviewOutcome;
                    private List<ClaimResponse.Item.Adjudication> adjudication = new ArrayList<>();

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
                     * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param traceNumber
                     *     Number for tracking
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder traceNumber(Identifier... traceNumber) {
                        for (Identifier value : traceNumber) {
                            this.traceNumber.add(value);
                        }
                        return this;
                    }

                    /**
                     * Trace number for tracking purposes. May be defined at the jurisdiction level or between trading partners.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param traceNumber
                     *     Number for tracking
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder traceNumber(Collection<Identifier> traceNumber) {
                        this.traceNumber = new ArrayList<>(traceNumber);
                        return this;
                    }

                    /**
                     * The type of revenue or cost center providing the product and/or service.
                     * 
                     * @param revenue
                     *     Revenue or cost center code
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder revenue(CodeableConcept revenue) {
                        this.revenue = revenue;
                        return this;
                    }

                    /**
                     * When the value is a group code then this item collects a set of related item details, otherwise this contains the 
                     * product, service, drug or other billing code for the item. This element may be the start of a range of .
                     * productOrService codes used in conjunction with .productOrServiceEnd or it may be a solo element where .
                     * productOrServiceEnd is not used.
                     * 
                     * @param productOrService
                     *     Billing, service, product, or drug code
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder productOrService(CodeableConcept productOrService) {
                        this.productOrService = productOrService;
                        return this;
                    }

                    /**
                     * This contains the end of a range of product, service, drug or other billing codes for the item. This element is not 
                     * used when the .productOrService is a group code. This value may only be present when a .productOfService code has been 
                     * provided to convey the start of the range. Typically this value may be used only with preauthorizations and not with 
                     * claims.
                     * 
                     * @param productOrServiceEnd
                     *     End of a range of codes
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder productOrServiceEnd(CodeableConcept productOrServiceEnd) {
                        this.productOrServiceEnd = productOrServiceEnd;
                        return this;
                    }

                    /**
                     * Item typification or modifiers codes to convey additional context for the product or service.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param modifier
                     *     Service/Product billing modifiers
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder modifier(CodeableConcept... modifier) {
                        for (CodeableConcept value : modifier) {
                            this.modifier.add(value);
                        }
                        return this;
                    }

                    /**
                     * Item typification or modifiers codes to convey additional context for the product or service.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param modifier
                     *     Service/Product billing modifiers
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder modifier(Collection<CodeableConcept> modifier) {
                        this.modifier = new ArrayList<>(modifier);
                        return this;
                    }

                    /**
                     * The number of repetitions of a service or product.
                     * 
                     * @param quantity
                     *     Count of products or services
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder quantity(SimpleQuantity quantity) {
                        this.quantity = quantity;
                        return this;
                    }

                    /**
                     * If the item is not a group then this is the fee for the product or service, otherwise this is the total of the fees 
                     * for the details of the group.
                     * 
                     * @param unitPrice
                     *     Fee, charge or cost per item
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder unitPrice(Money unitPrice) {
                        this.unitPrice = unitPrice;
                        return this;
                    }

                    /**
                     * A real number that represents a multiplier used in determining the overall value of services delivered and/or goods 
                     * received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
                     * 
                     * @param factor
                     *     Price scaling factor
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder factor(Decimal factor) {
                        this.factor = factor;
                        return this;
                    }

                    /**
                     * The total of taxes applicable for this product or service.
                     * 
                     * @param tax
                     *     Total tax
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder tax(Money tax) {
                        this.tax = tax;
                        return this;
                    }

                    /**
                     * The total amount claimed for the addItem.detail.subDetail. Net = unit price * quantity * factor.
                     * 
                     * @param net
                     *     Total item cost
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder net(Money net) {
                        this.net = net;
                        return this;
                    }

                    /**
                     * The numbers associated with notes below which apply to the adjudication of this item.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param noteNumber
                     *     Applicable note numbers
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder noteNumber(PositiveInt... noteNumber) {
                        for (PositiveInt value : noteNumber) {
                            this.noteNumber.add(value);
                        }
                        return this;
                    }

                    /**
                     * The numbers associated with notes below which apply to the adjudication of this item.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param noteNumber
                     *     Applicable note numbers
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder noteNumber(Collection<PositiveInt> noteNumber) {
                        this.noteNumber = new ArrayList<>(noteNumber);
                        return this;
                    }

                    /**
                     * The high-level results of the adjudication if adjudication has been performed.
                     * 
                     * @param reviewOutcome
                     *     Added items subdetail level adjudication results
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder reviewOutcome(ClaimResponse.Item.ReviewOutcome reviewOutcome) {
                        this.reviewOutcome = reviewOutcome;
                        return this;
                    }

                    /**
                     * The adjudication results.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param adjudication
                     *     Added items subdetail adjudication
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder adjudication(ClaimResponse.Item.Adjudication... adjudication) {
                        for (ClaimResponse.Item.Adjudication value : adjudication) {
                            this.adjudication.add(value);
                        }
                        return this;
                    }

                    /**
                     * The adjudication results.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param adjudication
                     *     Added items subdetail adjudication
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder adjudication(Collection<ClaimResponse.Item.Adjudication> adjudication) {
                        this.adjudication = new ArrayList<>(adjudication);
                        return this;
                    }

                    /**
                     * Build the {@link SubDetail}
                     * 
                     * @return
                     *     An immutable object of type {@link SubDetail}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid SubDetail per the base specification
                     */
                    @Override
                    public SubDetail build() {
                        SubDetail subDetail = new SubDetail(this);
                        if (validating) {
                            validate(subDetail);
                        }
                        return subDetail;
                    }

                    protected void validate(SubDetail subDetail) {
                        super.validate(subDetail);
                        ValidationSupport.checkList(subDetail.traceNumber, "traceNumber", Identifier.class);
                        ValidationSupport.checkList(subDetail.modifier, "modifier", CodeableConcept.class);
                        ValidationSupport.checkList(subDetail.noteNumber, "noteNumber", PositiveInt.class);
                        ValidationSupport.checkList(subDetail.adjudication, "adjudication", ClaimResponse.Item.Adjudication.class);
                        ValidationSupport.requireValueOrChildren(subDetail);
                    }

                    protected Builder from(SubDetail subDetail) {
                        super.from(subDetail);
                        traceNumber.addAll(subDetail.traceNumber);
                        revenue = subDetail.revenue;
                        productOrService = subDetail.productOrService;
                        productOrServiceEnd = subDetail.productOrServiceEnd;
                        modifier.addAll(subDetail.modifier);
                        quantity = subDetail.quantity;
                        unitPrice = subDetail.unitPrice;
                        factor = subDetail.factor;
                        tax = subDetail.tax;
                        net = subDetail.net;
                        noteNumber.addAll(subDetail.noteNumber);
                        reviewOutcome = subDetail.reviewOutcome;
                        adjudication.addAll(subDetail.adjudication);
                        return this;
                    }
                }
            }
        }
    }

    /**
     * Categorized monetary totals for the adjudication.
     */
    public static class Total extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "Adjudication",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The adjudication codes.",
            valueSet = "http://hl7.org/fhir/ValueSet/adjudication"
        )
        @Required
        private final CodeableConcept category;
        @Summary
        @Required
        private final Money amount;

        private Total(Builder builder) {
            super(builder);
            category = builder.category;
            amount = builder.amount;
        }

        /**
         * A code to indicate the information type of this adjudication record. Information types may include: the value 
         * submitted, maximum values or percentages allowed or payable under the plan, amounts that the patient is responsible 
         * for in aggregate or pertaining to this item, amounts paid by other coverages, and the benefit payable for this item.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCategory() {
            return category;
        }

        /**
         * Monetary total amount associated with the category.
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
                (category != null) || 
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
                    accept(category, "category", visitor);
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
            Total other = (Total) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(category, other.category) && 
                Objects.equals(amount, other.amount);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    category, 
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
            private CodeableConcept category;
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
             * A code to indicate the information type of this adjudication record. Information types may include: the value 
             * submitted, maximum values or percentages allowed or payable under the plan, amounts that the patient is responsible 
             * for in aggregate or pertaining to this item, amounts paid by other coverages, and the benefit payable for this item.
             * 
             * <p>This element is required.
             * 
             * @param category
             *     Type of adjudication information
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder category(CodeableConcept category) {
                this.category = category;
                return this;
            }

            /**
             * Monetary total amount associated with the category.
             * 
             * <p>This element is required.
             * 
             * @param amount
             *     Financial total for the category
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder amount(Money amount) {
                this.amount = amount;
                return this;
            }

            /**
             * Build the {@link Total}
             * 
             * <p>Required elements:
             * <ul>
             * <li>category</li>
             * <li>amount</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Total}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Total per the base specification
             */
            @Override
            public Total build() {
                Total total = new Total(this);
                if (validating) {
                    validate(total);
                }
                return total;
            }

            protected void validate(Total total) {
                super.validate(total);
                ValidationSupport.requireNonNull(total.category, "category");
                ValidationSupport.requireNonNull(total.amount, "amount");
                ValidationSupport.requireValueOrChildren(total);
            }

            protected Builder from(Total total) {
                super.from(total);
                category = total.category;
                amount = total.amount;
                return this;
            }
        }
    }

    /**
     * Payment details for the adjudication of the claim.
     */
    public static class Payment extends BackboneElement {
        @Binding(
            bindingName = "PaymentType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The type (partial, complete) of the payment.",
            valueSet = "http://hl7.org/fhir/ValueSet/ex-paymenttype"
        )
        @Required
        private final CodeableConcept type;
        private final Money adjustment;
        @Binding(
            bindingName = "PaymentAdjustmentReason",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Payment Adjustment reason codes.",
            valueSet = "http://hl7.org/fhir/ValueSet/payment-adjustment-reason"
        )
        private final CodeableConcept adjustmentReason;
        private final Date date;
        @Required
        private final Money amount;
        private final Identifier identifier;

        private Payment(Builder builder) {
            super(builder);
            type = builder.type;
            adjustment = builder.adjustment;
            adjustmentReason = builder.adjustmentReason;
            date = builder.date;
            amount = builder.amount;
            identifier = builder.identifier;
        }

        /**
         * Whether this represents partial or complete payment of the benefits payable.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Total amount of all adjustments to this payment included in this transaction which are not related to this claim's 
         * adjudication.
         * 
         * @return
         *     An immutable object of type {@link Money} that may be null.
         */
        public Money getAdjustment() {
            return adjustment;
        }

        /**
         * Reason for the payment adjustment.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getAdjustmentReason() {
            return adjustmentReason;
        }

        /**
         * Estimated date the payment will be issued or the actual issue date of payment.
         * 
         * @return
         *     An immutable object of type {@link Date} that may be null.
         */
        public Date getDate() {
            return date;
        }

        /**
         * Benefits payable less any payment adjustment.
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
        public Identifier getIdentifier() {
            return identifier;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (adjustment != null) || 
                (adjustmentReason != null) || 
                (date != null) || 
                (amount != null) || 
                (identifier != null);
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
                    accept(adjustment, "adjustment", visitor);
                    accept(adjustmentReason, "adjustmentReason", visitor);
                    accept(date, "date", visitor);
                    accept(amount, "amount", visitor);
                    accept(identifier, "identifier", visitor);
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
            Payment other = (Payment) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(adjustment, other.adjustment) && 
                Objects.equals(adjustmentReason, other.adjustmentReason) && 
                Objects.equals(date, other.date) && 
                Objects.equals(amount, other.amount) && 
                Objects.equals(identifier, other.identifier);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    adjustment, 
                    adjustmentReason, 
                    date, 
                    amount, 
                    identifier);
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
            private Money adjustment;
            private CodeableConcept adjustmentReason;
            private Date date;
            private Money amount;
            private Identifier identifier;

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
             * Whether this represents partial or complete payment of the benefits payable.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Partial or complete payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Total amount of all adjustments to this payment included in this transaction which are not related to this claim's 
             * adjudication.
             * 
             * @param adjustment
             *     Payment adjustment for non-claim issues
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder adjustment(Money adjustment) {
                this.adjustment = adjustment;
                return this;
            }

            /**
             * Reason for the payment adjustment.
             * 
             * @param adjustmentReason
             *     Explanation for the adjustment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder adjustmentReason(CodeableConcept adjustmentReason) {
                this.adjustmentReason = adjustmentReason;
                return this;
            }

            /**
             * Convenience method for setting {@code date}.
             * 
             * @param date
             *     Expected date of payment
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
             * Estimated date the payment will be issued or the actual issue date of payment.
             * 
             * @param date
             *     Expected date of payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder date(Date date) {
                this.date = date;
                return this;
            }

            /**
             * Benefits payable less any payment adjustment.
             * 
             * <p>This element is required.
             * 
             * @param amount
             *     Payable amount after adjustment
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
             * @param identifier
             *     Business identifier for the payment
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder identifier(Identifier identifier) {
                this.identifier = identifier;
                return this;
            }

            /**
             * Build the {@link Payment}
             * 
             * <p>Required elements:
             * <ul>
             * <li>type</li>
             * <li>amount</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Payment}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Payment per the base specification
             */
            @Override
            public Payment build() {
                Payment payment = new Payment(this);
                if (validating) {
                    validate(payment);
                }
                return payment;
            }

            protected void validate(Payment payment) {
                super.validate(payment);
                ValidationSupport.requireNonNull(payment.type, "type");
                ValidationSupport.requireNonNull(payment.amount, "amount");
                ValidationSupport.requireValueOrChildren(payment);
            }

            protected Builder from(Payment payment) {
                super.from(payment);
                type = payment.type;
                adjustment = payment.adjustment;
                adjustmentReason = payment.adjustmentReason;
                date = payment.date;
                amount = payment.amount;
                identifier = payment.identifier;
                return this;
            }
        }
    }

    /**
     * A note that describes or explains adjudication results in a human readable form.
     */
    public static class ProcessNote extends BackboneElement {
        private final PositiveInt number;
        @Binding(
            bindingName = "NoteType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The presentation types of notes.",
            valueSet = "http://hl7.org/fhir/ValueSet/note-type"
        )
        private final CodeableConcept type;
        @Required
        private final String text;
        @Binding(
            bindingName = "Language",
            strength = BindingStrength.Value.REQUIRED,
            description = "IETF language tag for a human language",
            valueSet = "http://hl7.org/fhir/ValueSet/all-languages|5.0.0"
        )
        private final CodeableConcept language;

        private ProcessNote(Builder builder) {
            super(builder);
            number = builder.number;
            type = builder.type;
            text = builder.text;
            language = builder.language;
        }

        /**
         * A number to uniquely identify a note entry.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getNumber() {
            return number;
        }

        /**
         * The business purpose of the note text.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The explanation or description associated with the processing.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getText() {
            return text;
        }

        /**
         * A code to define the language used in the text of the note.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getLanguage() {
            return language;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (number != null) || 
                (type != null) || 
                (text != null) || 
                (language != null);
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
                    accept(number, "number", visitor);
                    accept(type, "type", visitor);
                    accept(text, "text", visitor);
                    accept(language, "language", visitor);
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
                Objects.equals(number, other.number) && 
                Objects.equals(type, other.type) && 
                Objects.equals(text, other.text) && 
                Objects.equals(language, other.language);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    number, 
                    type, 
                    text, 
                    language);
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
            private PositiveInt number;
            private CodeableConcept type;
            private String text;
            private CodeableConcept language;

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
             * A number to uniquely identify a note entry.
             * 
             * @param number
             *     Note instance identifier
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder number(PositiveInt number) {
                this.number = number;
                return this;
            }

            /**
             * The business purpose of the note text.
             * 
             * @param type
             *     Note purpose
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code text}.
             * 
             * <p>This element is required.
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
             * <p>This element is required.
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
             * A code to define the language used in the text of the note.
             * 
             * @param language
             *     Language of the text
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder language(CodeableConcept language) {
                this.language = language;
                return this;
            }

            /**
             * Build the {@link ProcessNote}
             * 
             * <p>Required elements:
             * <ul>
             * <li>text</li>
             * </ul>
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
                ValidationSupport.requireNonNull(processNote.text, "text");
                ValidationSupport.checkValueSetBinding(processNote.language, "language", "http://hl7.org/fhir/ValueSet/all-languages", "urn:ietf:bcp:47");
                ValidationSupport.requireValueOrChildren(processNote);
            }

            protected Builder from(ProcessNote processNote) {
                super.from(processNote);
                number = processNote.number;
                type = processNote.type;
                text = processNote.text;
                language = processNote.language;
                return this;
            }
        }
    }

    /**
     * Financial instruments for reimbursement for the health care products and services specified on the claim.
     */
    public static class Insurance extends BackboneElement {
        @Required
        private final PositiveInt sequence;
        @Required
        private final Boolean focal;
        @ReferenceTarget({ "Coverage" })
        @Required
        private final Reference coverage;
        private final String businessArrangement;
        @ReferenceTarget({ "ClaimResponse" })
        private final Reference claimResponse;

        private Insurance(Builder builder) {
            super(builder);
            sequence = builder.sequence;
            focal = builder.focal;
            coverage = builder.coverage;
            businessArrangement = builder.businessArrangement;
            claimResponse = builder.claimResponse;
        }

        /**
         * A number to uniquely identify insurance entries and provide a sequence of coverages to convey coordination of benefit 
         * order.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that is non-null.
         */
        public PositiveInt getSequence() {
            return sequence;
        }

        /**
         * A flag to indicate that this Coverage is to be used for adjudication of this claim when set to true.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that is non-null.
         */
        public Boolean getFocal() {
            return focal;
        }

        /**
         * Reference to the insurance card level information contained in the Coverage resource. The coverage issuing insurer 
         * will use these details to locate the patient's actual coverage within the insurer's information system.
         * 
         * @return
         *     An immutable object of type {@link Reference} that is non-null.
         */
        public Reference getCoverage() {
            return coverage;
        }

        /**
         * A business agreement number established between the provider and the insurer for special business processing purposes.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getBusinessArrangement() {
            return businessArrangement;
        }

        /**
         * The result of the adjudication of the line items for the Coverage specified in this insurance.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getClaimResponse() {
            return claimResponse;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (sequence != null) || 
                (focal != null) || 
                (coverage != null) || 
                (businessArrangement != null) || 
                (claimResponse != null);
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
                    accept(focal, "focal", visitor);
                    accept(coverage, "coverage", visitor);
                    accept(businessArrangement, "businessArrangement", visitor);
                    accept(claimResponse, "claimResponse", visitor);
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
            Insurance other = (Insurance) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(sequence, other.sequence) && 
                Objects.equals(focal, other.focal) && 
                Objects.equals(coverage, other.coverage) && 
                Objects.equals(businessArrangement, other.businessArrangement) && 
                Objects.equals(claimResponse, other.claimResponse);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    sequence, 
                    focal, 
                    coverage, 
                    businessArrangement, 
                    claimResponse);
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
            private Boolean focal;
            private Reference coverage;
            private String businessArrangement;
            private Reference claimResponse;

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
             * A number to uniquely identify insurance entries and provide a sequence of coverages to convey coordination of benefit 
             * order.
             * 
             * <p>This element is required.
             * 
             * @param sequence
             *     Insurance instance identifier
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder sequence(PositiveInt sequence) {
                this.sequence = sequence;
                return this;
            }

            /**
             * Convenience method for setting {@code focal}.
             * 
             * <p>This element is required.
             * 
             * @param focal
             *     Coverage to be used for adjudication
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #focal(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder focal(java.lang.Boolean focal) {
                this.focal = (focal == null) ? null : Boolean.of(focal);
                return this;
            }

            /**
             * A flag to indicate that this Coverage is to be used for adjudication of this claim when set to true.
             * 
             * <p>This element is required.
             * 
             * @param focal
             *     Coverage to be used for adjudication
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder focal(Boolean focal) {
                this.focal = focal;
                return this;
            }

            /**
             * Reference to the insurance card level information contained in the Coverage resource. The coverage issuing insurer 
             * will use these details to locate the patient's actual coverage within the insurer's information system.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Coverage}</li>
             * </ul>
             * 
             * @param coverage
             *     Insurance information
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder coverage(Reference coverage) {
                this.coverage = coverage;
                return this;
            }

            /**
             * Convenience method for setting {@code businessArrangement}.
             * 
             * @param businessArrangement
             *     Additional provider contract number
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #businessArrangement(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder businessArrangement(java.lang.String businessArrangement) {
                this.businessArrangement = (businessArrangement == null) ? null : String.of(businessArrangement);
                return this;
            }

            /**
             * A business agreement number established between the provider and the insurer for special business processing purposes.
             * 
             * @param businessArrangement
             *     Additional provider contract number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder businessArrangement(String businessArrangement) {
                this.businessArrangement = businessArrangement;
                return this;
            }

            /**
             * The result of the adjudication of the line items for the Coverage specified in this insurance.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link ClaimResponse}</li>
             * </ul>
             * 
             * @param claimResponse
             *     Adjudication results
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder claimResponse(Reference claimResponse) {
                this.claimResponse = claimResponse;
                return this;
            }

            /**
             * Build the {@link Insurance}
             * 
             * <p>Required elements:
             * <ul>
             * <li>sequence</li>
             * <li>focal</li>
             * <li>coverage</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Insurance}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Insurance per the base specification
             */
            @Override
            public Insurance build() {
                Insurance insurance = new Insurance(this);
                if (validating) {
                    validate(insurance);
                }
                return insurance;
            }

            protected void validate(Insurance insurance) {
                super.validate(insurance);
                ValidationSupport.requireNonNull(insurance.sequence, "sequence");
                ValidationSupport.requireNonNull(insurance.focal, "focal");
                ValidationSupport.requireNonNull(insurance.coverage, "coverage");
                ValidationSupport.checkReferenceType(insurance.coverage, "coverage", "Coverage");
                ValidationSupport.checkReferenceType(insurance.claimResponse, "claimResponse", "ClaimResponse");
                ValidationSupport.requireValueOrChildren(insurance);
            }

            protected Builder from(Insurance insurance) {
                super.from(insurance);
                sequence = insurance.sequence;
                focal = insurance.focal;
                coverage = insurance.coverage;
                businessArrangement = insurance.businessArrangement;
                claimResponse = insurance.claimResponse;
                return this;
            }
        }
    }

    /**
     * Errors encountered during the processing of the adjudication.
     */
    public static class Error extends BackboneElement {
        private final PositiveInt itemSequence;
        private final PositiveInt detailSequence;
        private final PositiveInt subDetailSequence;
        @Summary
        @Binding(
            bindingName = "AdjudicationError",
            strength = BindingStrength.Value.EXAMPLE,
            description = "The adjudication error codes.",
            valueSet = "http://hl7.org/fhir/ValueSet/adjudication-error"
        )
        @Required
        private final CodeableConcept code;
        @Summary
        private final List<String> expression;

        private Error(Builder builder) {
            super(builder);
            itemSequence = builder.itemSequence;
            detailSequence = builder.detailSequence;
            subDetailSequence = builder.subDetailSequence;
            code = builder.code;
            expression = Collections.unmodifiableList(builder.expression);
        }

        /**
         * The sequence number of the line item submitted which contains the error. This value is omitted when the error occurs 
         * outside of the item structure.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getItemSequence() {
            return itemSequence;
        }

        /**
         * The sequence number of the detail within the line item submitted which contains the error. This value is omitted when 
         * the error occurs outside of the item structure.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getDetailSequence() {
            return detailSequence;
        }

        /**
         * The sequence number of the sub-detail within the detail within the line item submitted which contains the error. This 
         * value is omitted when the error occurs outside of the item structure.
         * 
         * @return
         *     An immutable object of type {@link PositiveInt} that may be null.
         */
        public PositiveInt getSubDetailSequence() {
            return subDetailSequence;
        }

        /**
         * An error code, from a specified code system, which details why the claim could not be adjudicated.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * A [simple subset of FHIRPath](fhirpath.html#simple) limited to element names, repetition indicators and the default 
         * child accessor that identifies one of the elements in the resource that caused this issue to be raised.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link String} that may be empty.
         */
        public List<String> getExpression() {
            return expression;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (itemSequence != null) || 
                (detailSequence != null) || 
                (subDetailSequence != null) || 
                (code != null) || 
                !expression.isEmpty();
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
                    accept(itemSequence, "itemSequence", visitor);
                    accept(detailSequence, "detailSequence", visitor);
                    accept(subDetailSequence, "subDetailSequence", visitor);
                    accept(code, "code", visitor);
                    accept(expression, "expression", visitor, String.class);
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
            Error other = (Error) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(itemSequence, other.itemSequence) && 
                Objects.equals(detailSequence, other.detailSequence) && 
                Objects.equals(subDetailSequence, other.subDetailSequence) && 
                Objects.equals(code, other.code) && 
                Objects.equals(expression, other.expression);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    itemSequence, 
                    detailSequence, 
                    subDetailSequence, 
                    code, 
                    expression);
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
            private PositiveInt itemSequence;
            private PositiveInt detailSequence;
            private PositiveInt subDetailSequence;
            private CodeableConcept code;
            private List<String> expression = new ArrayList<>();

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
             * The sequence number of the line item submitted which contains the error. This value is omitted when the error occurs 
             * outside of the item structure.
             * 
             * @param itemSequence
             *     Item sequence number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder itemSequence(PositiveInt itemSequence) {
                this.itemSequence = itemSequence;
                return this;
            }

            /**
             * The sequence number of the detail within the line item submitted which contains the error. This value is omitted when 
             * the error occurs outside of the item structure.
             * 
             * @param detailSequence
             *     Detail sequence number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder detailSequence(PositiveInt detailSequence) {
                this.detailSequence = detailSequence;
                return this;
            }

            /**
             * The sequence number of the sub-detail within the detail within the line item submitted which contains the error. This 
             * value is omitted when the error occurs outside of the item structure.
             * 
             * @param subDetailSequence
             *     Subdetail sequence number
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder subDetailSequence(PositiveInt subDetailSequence) {
                this.subDetailSequence = subDetailSequence;
                return this;
            }

            /**
             * An error code, from a specified code system, which details why the claim could not be adjudicated.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Error code detailing processing issues
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Convenience method for setting {@code expression}.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param expression
             *     FHIRPath of element(s) related to issue
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #expression(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder expression(java.lang.String... expression) {
                for (java.lang.String value : expression) {
                    this.expression.add((value == null) ? null : String.of(value));
                }
                return this;
            }

            /**
             * A [simple subset of FHIRPath](fhirpath.html#simple) limited to element names, repetition indicators and the default 
             * child accessor that identifies one of the elements in the resource that caused this issue to be raised.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param expression
             *     FHIRPath of element(s) related to issue
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder expression(String... expression) {
                for (String value : expression) {
                    this.expression.add(value);
                }
                return this;
            }

            /**
             * A [simple subset of FHIRPath](fhirpath.html#simple) limited to element names, repetition indicators and the default 
             * child accessor that identifies one of the elements in the resource that caused this issue to be raised.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param expression
             *     FHIRPath of element(s) related to issue
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder expression(Collection<String> expression) {
                this.expression = new ArrayList<>(expression);
                return this;
            }

            /**
             * Build the {@link Error}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Error}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Error per the base specification
             */
            @Override
            public Error build() {
                Error error = new Error(this);
                if (validating) {
                    validate(error);
                }
                return error;
            }

            protected void validate(Error error) {
                super.validate(error);
                ValidationSupport.requireNonNull(error.code, "code");
                ValidationSupport.checkList(error.expression, "expression", String.class);
                ValidationSupport.requireValueOrChildren(error);
            }

            protected Builder from(Error error) {
                super.from(error);
                itemSequence = error.itemSequence;
                detailSequence = error.detailSequence;
                subDetailSequence = error.subDetailSequence;
                code = error.code;
                expression.addAll(error.expression);
                return this;
            }
        }
    }
}
