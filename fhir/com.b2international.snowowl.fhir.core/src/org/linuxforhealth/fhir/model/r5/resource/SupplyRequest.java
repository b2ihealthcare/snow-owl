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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
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
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.RequestPriority;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.SupplyRequestStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of a request to deliver a medication, substance or device used in the healthcare setting to a particular 
 * destination for a particular person or organization.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class SupplyRequest extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "SupplyRequestStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Status of the supply request.",
        valueSet = "http://hl7.org/fhir/ValueSet/supplyrequest-status|5.0.0"
    )
    private final SupplyRequestStatus status;
    @Summary
    private final List<Reference> basedOn;
    @Summary
    @Binding(
        bindingName = "SupplyRequestKind",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Category of supply request.",
        valueSet = "http://hl7.org/fhir/ValueSet/supplyrequest-kind"
    )
    private final CodeableConcept category;
    @Summary
    @Binding(
        bindingName = "RequestPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "Identifies the level of importance to be assigned to actioning the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final RequestPriority priority;
    @ReferenceTarget({ "Patient" })
    private final Reference deliverFor;
    @Summary
    @Binding(
        bindingName = "SupplyRequestItem",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The item that was requested.",
        valueSet = "http://hl7.org/fhir/ValueSet/supply-item"
    )
    @Required
    private final CodeableReference item;
    @Summary
    @Required
    private final Quantity quantity;
    private final List<Parameter> parameter;
    @Summary
    @Choice({ DateTime.class, Period.class, Timing.class })
    private final Element occurrence;
    @Summary
    private final DateTime authoredOn;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device", "CareTeam" })
    private final Reference requester;
    @Summary
    @ReferenceTarget({ "Organization", "HealthcareService" })
    private final List<Reference> supplier;
    @Binding(
        bindingName = "SupplyRequestReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The reason why the supply item was requested.",
        valueSet = "http://hl7.org/fhir/ValueSet/supplyrequest-reason"
    )
    private final List<CodeableReference> reason;
    @ReferenceTarget({ "Organization", "Location" })
    private final Reference deliverFrom;
    @ReferenceTarget({ "Organization", "Location", "Patient", "RelatedPerson" })
    private final Reference deliverTo;

    private SupplyRequest(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        basedOn = Collections.unmodifiableList(builder.basedOn);
        category = builder.category;
        priority = builder.priority;
        deliverFor = builder.deliverFor;
        item = builder.item;
        quantity = builder.quantity;
        parameter = Collections.unmodifiableList(builder.parameter);
        occurrence = builder.occurrence;
        authoredOn = builder.authoredOn;
        requester = builder.requester;
        supplier = Collections.unmodifiableList(builder.supplier);
        reason = Collections.unmodifiableList(builder.reason);
        deliverFrom = builder.deliverFrom;
        deliverTo = builder.deliverTo;
    }

    /**
     * Business identifiers assigned to this SupplyRequest by the author and/or other systems. These identifiers remain 
     * constant as the resource is updated and propagates from server to server.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Status of the supply request.
     * 
     * @return
     *     An immutable object of type {@link SupplyRequestStatus} that may be null.
     */
    public SupplyRequestStatus getStatus() {
        return status;
    }

    /**
     * Plan/proposal/order fulfilled by this request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * Category of supply, e.g. central, non-stock, etc. This is used to support work flows associated with the supply 
     * process.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCategory() {
        return category;
    }

    /**
     * Indicates how quickly this SupplyRequest should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link RequestPriority} that may be null.
     */
    public RequestPriority getPriority() {
        return priority;
    }

    /**
     * The patient to whom the supply will be given or for whom they will be used.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getDeliverFor() {
        return deliverFor;
    }

    /**
     * The item that is requested to be supplied. This is either a link to a resource representing the details of the item or 
     * a code that identifies the item from a known list.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that is non-null.
     */
    public CodeableReference getItem() {
        return item;
    }

    /**
     * The amount that is being ordered of the indicated item.
     * 
     * @return
     *     An immutable object of type {@link Quantity} that is non-null.
     */
    public Quantity getQuantity() {
        return quantity;
    }

    /**
     * Specific parameters for the ordered item. For example, the size of the indicated item.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Parameter} that may be empty.
     */
    public List<Parameter> getParameter() {
        return parameter;
    }

    /**
     * When the request should be fulfilled.
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Period} or {@link Timing} that may be null.
     */
    public Element getOccurrence() {
        return occurrence;
    }

    /**
     * When the request was made.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getAuthoredOn() {
        return authoredOn;
    }

    /**
     * The device, practitioner, etc. who initiated the request.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequester() {
        return requester;
    }

    /**
     * Who is intended to fulfill the request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupplier() {
        return supplier;
    }

    /**
     * The reason why the supply item was requested.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Where the supply is expected to come from.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getDeliverFrom() {
        return deliverFrom;
    }

    /**
     * Where the supply is destined to go.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getDeliverTo() {
        return deliverTo;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !basedOn.isEmpty() || 
            (category != null) || 
            (priority != null) || 
            (deliverFor != null) || 
            (item != null) || 
            (quantity != null) || 
            !parameter.isEmpty() || 
            (occurrence != null) || 
            (authoredOn != null) || 
            (requester != null) || 
            !supplier.isEmpty() || 
            !reason.isEmpty() || 
            (deliverFrom != null) || 
            (deliverTo != null);
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
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(category, "category", visitor);
                accept(priority, "priority", visitor);
                accept(deliverFor, "deliverFor", visitor);
                accept(item, "item", visitor);
                accept(quantity, "quantity", visitor);
                accept(parameter, "parameter", visitor, Parameter.class);
                accept(occurrence, "occurrence", visitor);
                accept(authoredOn, "authoredOn", visitor);
                accept(requester, "requester", visitor);
                accept(supplier, "supplier", visitor, Reference.class);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(deliverFrom, "deliverFrom", visitor);
                accept(deliverTo, "deliverTo", visitor);
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
        SupplyRequest other = (SupplyRequest) obj;
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
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(category, other.category) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(deliverFor, other.deliverFor) && 
            Objects.equals(item, other.item) && 
            Objects.equals(quantity, other.quantity) && 
            Objects.equals(parameter, other.parameter) && 
            Objects.equals(occurrence, other.occurrence) && 
            Objects.equals(authoredOn, other.authoredOn) && 
            Objects.equals(requester, other.requester) && 
            Objects.equals(supplier, other.supplier) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(deliverFrom, other.deliverFrom) && 
            Objects.equals(deliverTo, other.deliverTo);
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
                basedOn, 
                category, 
                priority, 
                deliverFor, 
                item, 
                quantity, 
                parameter, 
                occurrence, 
                authoredOn, 
                requester, 
                supplier, 
                reason, 
                deliverFrom, 
                deliverTo);
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
        private SupplyRequestStatus status;
        private List<Reference> basedOn = new ArrayList<>();
        private CodeableConcept category;
        private RequestPriority priority;
        private Reference deliverFor;
        private CodeableReference item;
        private Quantity quantity;
        private List<Parameter> parameter = new ArrayList<>();
        private Element occurrence;
        private DateTime authoredOn;
        private Reference requester;
        private List<Reference> supplier = new ArrayList<>();
        private List<CodeableReference> reason = new ArrayList<>();
        private Reference deliverFrom;
        private Reference deliverTo;

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
         * Business identifiers assigned to this SupplyRequest by the author and/or other systems. These identifiers remain 
         * constant as the resource is updated and propagates from server to server.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for SupplyRequest
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
         * Business identifiers assigned to this SupplyRequest by the author and/or other systems. These identifiers remain 
         * constant as the resource is updated and propagates from server to server.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for SupplyRequest
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
         * Status of the supply request.
         * 
         * @param status
         *     draft | active | suspended +
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(SupplyRequestStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Plan/proposal/order fulfilled by this request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     What other request is fulfilled by this supply request
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
         * Plan/proposal/order fulfilled by this request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     What other request is fulfilled by this supply request
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
         * Category of supply, e.g. central, non-stock, etc. This is used to support work flows associated with the supply 
         * process.
         * 
         * @param category
         *     The kind of supply (central, non-stock, etc.)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder category(CodeableConcept category) {
            this.category = category;
            return this;
        }

        /**
         * Indicates how quickly this SupplyRequest should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(RequestPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * The patient to whom the supply will be given or for whom they will be used.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param deliverFor
         *     The patient for who the supply request is for
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder deliverFor(Reference deliverFor) {
            this.deliverFor = deliverFor;
            return this;
        }

        /**
         * The item that is requested to be supplied. This is either a link to a resource representing the details of the item or 
         * a code that identifies the item from a known list.
         * 
         * <p>This element is required.
         * 
         * @param item
         *     Medication, Substance, or Device requested to be supplied
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder item(CodeableReference item) {
            this.item = item;
            return this;
        }

        /**
         * The amount that is being ordered of the indicated item.
         * 
         * <p>This element is required.
         * 
         * @param quantity
         *     The requested amount of the item indicated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder quantity(Quantity quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Specific parameters for the ordered item. For example, the size of the indicated item.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Ordered item details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder parameter(Parameter... parameter) {
            for (Parameter value : parameter) {
                this.parameter.add(value);
            }
            return this;
        }

        /**
         * Specific parameters for the ordered item. For example, the size of the indicated item.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Ordered item details
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder parameter(Collection<Parameter> parameter) {
            this.parameter = new ArrayList<>(parameter);
            return this;
        }

        /**
         * When the request should be fulfilled.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * <li>{@link Timing}</li>
         * </ul>
         * 
         * @param occurrence
         *     When the request should be fulfilled
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrence(Element occurrence) {
            this.occurrence = occurrence;
            return this;
        }

        /**
         * When the request was made.
         * 
         * @param authoredOn
         *     When the request was made
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authoredOn(DateTime authoredOn) {
            this.authoredOn = authoredOn;
            return this;
        }

        /**
         * The device, practitioner, etc. who initiated the request.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Device}</li>
         * <li>{@link CareTeam}</li>
         * </ul>
         * 
         * @param requester
         *     Individual making the request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requester(Reference requester) {
            this.requester = requester;
            return this;
        }

        /**
         * Who is intended to fulfill the request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Organization}</li>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param supplier
         *     Who is intended to fulfill the request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supplier(Reference... supplier) {
            for (Reference value : supplier) {
                this.supplier.add(value);
            }
            return this;
        }

        /**
         * Who is intended to fulfill the request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Organization}</li>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param supplier
         *     Who is intended to fulfill the request
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supplier(Collection<Reference> supplier) {
            this.supplier = new ArrayList<>(supplier);
            return this;
        }

        /**
         * The reason why the supply item was requested.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The reason why the supply item was requested
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
         * The reason why the supply item was requested.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     The reason why the supply item was requested
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
         * Where the supply is expected to come from.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param deliverFrom
         *     The origin of the supply
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder deliverFrom(Reference deliverFrom) {
            this.deliverFrom = deliverFrom;
            return this;
        }

        /**
         * Where the supply is destined to go.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * <li>{@link Location}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * </ul>
         * 
         * @param deliverTo
         *     The destination of the supply
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder deliverTo(Reference deliverTo) {
            this.deliverTo = deliverTo;
            return this;
        }

        /**
         * Build the {@link SupplyRequest}
         * 
         * <p>Required elements:
         * <ul>
         * <li>item</li>
         * <li>quantity</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link SupplyRequest}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid SupplyRequest per the base specification
         */
        @Override
        public SupplyRequest build() {
            SupplyRequest supplyRequest = new SupplyRequest(this);
            if (validating) {
                validate(supplyRequest);
            }
            return supplyRequest;
        }

        protected void validate(SupplyRequest supplyRequest) {
            super.validate(supplyRequest);
            ValidationSupport.checkList(supplyRequest.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(supplyRequest.basedOn, "basedOn", Reference.class);
            ValidationSupport.requireNonNull(supplyRequest.item, "item");
            ValidationSupport.requireNonNull(supplyRequest.quantity, "quantity");
            ValidationSupport.checkList(supplyRequest.parameter, "parameter", Parameter.class);
            ValidationSupport.choiceElement(supplyRequest.occurrence, "occurrence", DateTime.class, Period.class, Timing.class);
            ValidationSupport.checkList(supplyRequest.supplier, "supplier", Reference.class);
            ValidationSupport.checkList(supplyRequest.reason, "reason", CodeableReference.class);
            ValidationSupport.checkReferenceType(supplyRequest.deliverFor, "deliverFor", "Patient");
            ValidationSupport.checkReferenceType(supplyRequest.requester, "requester", "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson", "Device", "CareTeam");
            ValidationSupport.checkReferenceType(supplyRequest.supplier, "supplier", "Organization", "HealthcareService");
            ValidationSupport.checkReferenceType(supplyRequest.deliverFrom, "deliverFrom", "Organization", "Location");
            ValidationSupport.checkReferenceType(supplyRequest.deliverTo, "deliverTo", "Organization", "Location", "Patient", "RelatedPerson");
        }

        protected Builder from(SupplyRequest supplyRequest) {
            super.from(supplyRequest);
            identifier.addAll(supplyRequest.identifier);
            status = supplyRequest.status;
            basedOn.addAll(supplyRequest.basedOn);
            category = supplyRequest.category;
            priority = supplyRequest.priority;
            deliverFor = supplyRequest.deliverFor;
            item = supplyRequest.item;
            quantity = supplyRequest.quantity;
            parameter.addAll(supplyRequest.parameter);
            occurrence = supplyRequest.occurrence;
            authoredOn = supplyRequest.authoredOn;
            requester = supplyRequest.requester;
            supplier.addAll(supplyRequest.supplier);
            reason.addAll(supplyRequest.reason);
            deliverFrom = supplyRequest.deliverFrom;
            deliverTo = supplyRequest.deliverTo;
            return this;
        }
    }

    /**
     * Specific parameters for the ordered item. For example, the size of the indicated item.
     */
    public static class Parameter extends BackboneElement {
        @Binding(
            bindingName = "ParameterCode",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A code that identifies the device detail."
        )
        private final CodeableConcept code;
        @Choice({ CodeableConcept.class, Quantity.class, Range.class, Boolean.class })
        private final Element value;

        private Parameter(Builder builder) {
            super(builder);
            code = builder.code;
            value = builder.value;
        }

        /**
         * A code or string that identifies the device detail being asserted.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * The value of the device detail.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept}, {@link Quantity}, {@link Range} or {@link Boolean} that may be 
         *     null.
         */
        public Element getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (value != null);
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
                    accept(value, "value", visitor);
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
            Parameter other = (Parameter) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    value);
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
            private Element value;

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
             * A code or string that identifies the device detail being asserted.
             * 
             * @param code
             *     Item detail
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * @param value
             *     Value of detail
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.Boolean value) {
                this.value = (value == null) ? null : Boolean.of(value);
                return this;
            }

            /**
             * The value of the device detail.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * <li>{@link Boolean}</li>
             * </ul>
             * 
             * @param value
             *     Value of detail
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Parameter}
             * 
             * @return
             *     An immutable object of type {@link Parameter}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Parameter per the base specification
             */
            @Override
            public Parameter build() {
                Parameter parameter = new Parameter(this);
                if (validating) {
                    validate(parameter);
                }
                return parameter;
            }

            protected void validate(Parameter parameter) {
                super.validate(parameter);
                ValidationSupport.choiceElement(parameter.value, "value", CodeableConcept.class, Quantity.class, Range.class, Boolean.class);
                ValidationSupport.requireValueOrChildren(parameter);
            }

            protected Builder from(Parameter parameter) {
                super.from(parameter);
                code = parameter.code;
                value = parameter.value;
                return this;
            }
        }
    }
}
