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
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.InventoryCountType;
import org.linuxforhealth.fhir.model.r5.type.code.InventoryReportStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A report of inventory or stock items.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class InventoryReport extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "InventoryReportStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the InventoryReport.",
        valueSet = "http://hl7.org/fhir/ValueSet/inventoryreport-status|5.0.0"
    )
    @Required
    private final InventoryReportStatus status;
    @Summary
    @Binding(
        bindingName = "InventoryCountType",
        strength = BindingStrength.Value.REQUIRED,
        description = "The type of count.",
        valueSet = "http://hl7.org/fhir/ValueSet/inventoryreport-counttype|5.0.0"
    )
    @Required
    private final InventoryCountType countType;
    @Summary
    private final CodeableConcept operationType;
    @Summary
    private final CodeableConcept operationTypeReason;
    @Summary
    @Required
    private final DateTime reportedDateTime;
    @ReferenceTarget({ "Practitioner", "Patient", "RelatedPerson", "Device" })
    private final Reference reporter;
    private final Period reportingPeriod;
    @Summary
    private final List<InventoryListing> inventoryListing;
    private final List<Annotation> note;

    private InventoryReport(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        countType = builder.countType;
        operationType = builder.operationType;
        operationTypeReason = builder.operationTypeReason;
        reportedDateTime = builder.reportedDateTime;
        reporter = builder.reporter;
        reportingPeriod = builder.reportingPeriod;
        inventoryListing = Collections.unmodifiableList(builder.inventoryListing);
        note = Collections.unmodifiableList(builder.note);
    }

    /**
     * Business identifier for the InventoryReport.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The status of the inventory check or notification - whether this is draft (e.g. the report is still pending some 
     * updates) or active.
     * 
     * @return
     *     An immutable object of type {@link InventoryReportStatus} that is non-null.
     */
    public InventoryReportStatus getStatus() {
        return status;
    }

    /**
     * Whether the report is about the current inventory count (snapshot) or a differential change in inventory (change).
     * 
     * @return
     *     An immutable object of type {@link InventoryCountType} that is non-null.
     */
    public InventoryCountType getCountType() {
        return countType;
    }

    /**
     * What type of operation is being performed - addition or subtraction.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getOperationType() {
        return operationType;
    }

    /**
     * The reason for this count - regular count, ad-hoc count, new arrivals, etc.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getOperationTypeReason() {
        return operationTypeReason;
    }

    /**
     * When the report has been submitted.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that is non-null.
     */
    public DateTime getReportedDateTime() {
        return reportedDateTime;
    }

    /**
     * Who submits the report.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getReporter() {
        return reporter;
    }

    /**
     * The period the report refers to.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getReportingPeriod() {
        return reportingPeriod;
    }

    /**
     * An inventory listing section (grouped by any of the attributes).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link InventoryListing} that may be empty.
     */
    public List<InventoryListing> getInventoryListing() {
        return inventoryListing;
    }

    /**
     * A note associated with the InventoryReport.
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
            (status != null) || 
            (countType != null) || 
            (operationType != null) || 
            (operationTypeReason != null) || 
            (reportedDateTime != null) || 
            (reporter != null) || 
            (reportingPeriod != null) || 
            !inventoryListing.isEmpty() || 
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
                accept(status, "status", visitor);
                accept(countType, "countType", visitor);
                accept(operationType, "operationType", visitor);
                accept(operationTypeReason, "operationTypeReason", visitor);
                accept(reportedDateTime, "reportedDateTime", visitor);
                accept(reporter, "reporter", visitor);
                accept(reportingPeriod, "reportingPeriod", visitor);
                accept(inventoryListing, "inventoryListing", visitor, InventoryListing.class);
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
        InventoryReport other = (InventoryReport) obj;
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
            Objects.equals(countType, other.countType) && 
            Objects.equals(operationType, other.operationType) && 
            Objects.equals(operationTypeReason, other.operationTypeReason) && 
            Objects.equals(reportedDateTime, other.reportedDateTime) && 
            Objects.equals(reporter, other.reporter) && 
            Objects.equals(reportingPeriod, other.reportingPeriod) && 
            Objects.equals(inventoryListing, other.inventoryListing) && 
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
                status, 
                countType, 
                operationType, 
                operationTypeReason, 
                reportedDateTime, 
                reporter, 
                reportingPeriod, 
                inventoryListing, 
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
        private InventoryReportStatus status;
        private InventoryCountType countType;
        private CodeableConcept operationType;
        private CodeableConcept operationTypeReason;
        private DateTime reportedDateTime;
        private Reference reporter;
        private Period reportingPeriod;
        private List<InventoryListing> inventoryListing = new ArrayList<>();
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
         * Business identifier for the InventoryReport.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for the report
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
         * Business identifier for the InventoryReport.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier for the report
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
         * The status of the inventory check or notification - whether this is draft (e.g. the report is still pending some 
         * updates) or active.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | requested | active | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(InventoryReportStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Whether the report is about the current inventory count (snapshot) or a differential change in inventory (change).
         * 
         * <p>This element is required.
         * 
         * @param countType
         *     snapshot | difference
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder countType(InventoryCountType countType) {
            this.countType = countType;
            return this;
        }

        /**
         * What type of operation is being performed - addition or subtraction.
         * 
         * @param operationType
         *     addition | subtraction
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder operationType(CodeableConcept operationType) {
            this.operationType = operationType;
            return this;
        }

        /**
         * The reason for this count - regular count, ad-hoc count, new arrivals, etc.
         * 
         * @param operationTypeReason
         *     The reason for this count - regular count, ad-hoc count, new arrivals, etc
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder operationTypeReason(CodeableConcept operationTypeReason) {
            this.operationTypeReason = operationTypeReason;
            return this;
        }

        /**
         * When the report has been submitted.
         * 
         * <p>This element is required.
         * 
         * @param reportedDateTime
         *     When the report has been submitted
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reportedDateTime(DateTime reportedDateTime) {
            this.reportedDateTime = reportedDateTime;
            return this;
        }

        /**
         * Who submits the report.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param reporter
         *     Who submits the report
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reporter(Reference reporter) {
            this.reporter = reporter;
            return this;
        }

        /**
         * The period the report refers to.
         * 
         * @param reportingPeriod
         *     The period the report refers to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reportingPeriod(Period reportingPeriod) {
            this.reportingPeriod = reportingPeriod;
            return this;
        }

        /**
         * An inventory listing section (grouped by any of the attributes).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param inventoryListing
         *     An inventory listing section (grouped by any of the attributes)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder inventoryListing(InventoryListing... inventoryListing) {
            for (InventoryListing value : inventoryListing) {
                this.inventoryListing.add(value);
            }
            return this;
        }

        /**
         * An inventory listing section (grouped by any of the attributes).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param inventoryListing
         *     An inventory listing section (grouped by any of the attributes)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder inventoryListing(Collection<InventoryListing> inventoryListing) {
            this.inventoryListing = new ArrayList<>(inventoryListing);
            return this;
        }

        /**
         * A note associated with the InventoryReport.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     A note associated with the InventoryReport
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
         * A note associated with the InventoryReport.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     A note associated with the InventoryReport
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
         * Build the {@link InventoryReport}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>countType</li>
         * <li>reportedDateTime</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link InventoryReport}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid InventoryReport per the base specification
         */
        @Override
        public InventoryReport build() {
            InventoryReport inventoryReport = new InventoryReport(this);
            if (validating) {
                validate(inventoryReport);
            }
            return inventoryReport;
        }

        protected void validate(InventoryReport inventoryReport) {
            super.validate(inventoryReport);
            ValidationSupport.checkList(inventoryReport.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(inventoryReport.status, "status");
            ValidationSupport.requireNonNull(inventoryReport.countType, "countType");
            ValidationSupport.requireNonNull(inventoryReport.reportedDateTime, "reportedDateTime");
            ValidationSupport.checkList(inventoryReport.inventoryListing, "inventoryListing", InventoryListing.class);
            ValidationSupport.checkList(inventoryReport.note, "note", Annotation.class);
            ValidationSupport.checkReferenceType(inventoryReport.reporter, "reporter", "Practitioner", "Patient", "RelatedPerson", "Device");
        }

        protected Builder from(InventoryReport inventoryReport) {
            super.from(inventoryReport);
            identifier.addAll(inventoryReport.identifier);
            status = inventoryReport.status;
            countType = inventoryReport.countType;
            operationType = inventoryReport.operationType;
            operationTypeReason = inventoryReport.operationTypeReason;
            reportedDateTime = inventoryReport.reportedDateTime;
            reporter = inventoryReport.reporter;
            reportingPeriod = inventoryReport.reportingPeriod;
            inventoryListing.addAll(inventoryReport.inventoryListing);
            note.addAll(inventoryReport.note);
            return this;
        }
    }

    /**
     * An inventory listing section (grouped by any of the attributes).
     */
    public static class InventoryListing extends BackboneElement {
        @ReferenceTarget({ "Location" })
        private final Reference location;
        @Summary
        private final CodeableConcept itemStatus;
        private final DateTime countingDateTime;
        @Summary
        private final List<Item> item;

        private InventoryListing(Builder builder) {
            super(builder);
            location = builder.location;
            itemStatus = builder.itemStatus;
            countingDateTime = builder.countingDateTime;
            item = Collections.unmodifiableList(builder.item);
        }

        /**
         * Location of the inventory items.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getLocation() {
            return location;
        }

        /**
         * The status of the items.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getItemStatus() {
            return itemStatus;
        }

        /**
         * The date and time when the items were counted.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getCountingDateTime() {
            return countingDateTime;
        }

        /**
         * The item or items in this listing.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Item} that may be empty.
         */
        public List<Item> getItem() {
            return item;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (location != null) || 
                (itemStatus != null) || 
                (countingDateTime != null) || 
                !item.isEmpty();
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
                    accept(location, "location", visitor);
                    accept(itemStatus, "itemStatus", visitor);
                    accept(countingDateTime, "countingDateTime", visitor);
                    accept(item, "item", visitor, Item.class);
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
            InventoryListing other = (InventoryListing) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(location, other.location) && 
                Objects.equals(itemStatus, other.itemStatus) && 
                Objects.equals(countingDateTime, other.countingDateTime) && 
                Objects.equals(item, other.item);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    location, 
                    itemStatus, 
                    countingDateTime, 
                    item);
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
            private Reference location;
            private CodeableConcept itemStatus;
            private DateTime countingDateTime;
            private List<Item> item = new ArrayList<>();

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
             * Location of the inventory items.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Location}</li>
             * </ul>
             * 
             * @param location
             *     Location of the inventory items
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder location(Reference location) {
                this.location = location;
                return this;
            }

            /**
             * The status of the items.
             * 
             * @param itemStatus
             *     The status of the items that are being reported
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder itemStatus(CodeableConcept itemStatus) {
                this.itemStatus = itemStatus;
                return this;
            }

            /**
             * The date and time when the items were counted.
             * 
             * @param countingDateTime
             *     The date and time when the items were counted
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder countingDateTime(DateTime countingDateTime) {
                this.countingDateTime = countingDateTime;
                return this;
            }

            /**
             * The item or items in this listing.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param item
             *     The item or items in this listing
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
             * The item or items in this listing.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param item
             *     The item or items in this listing
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
             * Build the {@link InventoryListing}
             * 
             * @return
             *     An immutable object of type {@link InventoryListing}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid InventoryListing per the base specification
             */
            @Override
            public InventoryListing build() {
                InventoryListing inventoryListing = new InventoryListing(this);
                if (validating) {
                    validate(inventoryListing);
                }
                return inventoryListing;
            }

            protected void validate(InventoryListing inventoryListing) {
                super.validate(inventoryListing);
                ValidationSupport.checkList(inventoryListing.item, "item", Item.class);
                ValidationSupport.checkReferenceType(inventoryListing.location, "location", "Location");
                ValidationSupport.requireValueOrChildren(inventoryListing);
            }

            protected Builder from(InventoryListing inventoryListing) {
                super.from(inventoryListing);
                location = inventoryListing.location;
                itemStatus = inventoryListing.itemStatus;
                countingDateTime = inventoryListing.countingDateTime;
                item.addAll(inventoryListing.item);
                return this;
            }
        }

        /**
         * The item or items in this listing.
         */
        public static class Item extends BackboneElement {
            @Summary
            private final CodeableConcept category;
            @Summary
            @Required
            private final Quantity quantity;
            @Summary
            @Required
            private final CodeableReference item;

            private Item(Builder builder) {
                super(builder);
                category = builder.category;
                quantity = builder.quantity;
                item = builder.item;
            }

            /**
             * The inventory category or classification of the items being reported. This is meant not for defining the product, but 
             * for inventory categories e.g. 'pending recount' or 'damaged'.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCategory() {
                return category;
            }

            /**
             * The quantity of the item or items being reported.
             * 
             * @return
             *     An immutable object of type {@link Quantity} that is non-null.
             */
            public Quantity getQuantity() {
                return quantity;
            }

            /**
             * The code or reference to the item type.
             * 
             * @return
             *     An immutable object of type {@link CodeableReference} that is non-null.
             */
            public CodeableReference getItem() {
                return item;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (category != null) || 
                    (quantity != null) || 
                    (item != null);
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
                        accept(quantity, "quantity", visitor);
                        accept(item, "item", visitor);
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
                    Objects.equals(category, other.category) && 
                    Objects.equals(quantity, other.quantity) && 
                    Objects.equals(item, other.item);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        category, 
                        quantity, 
                        item);
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
                private Quantity quantity;
                private CodeableReference item;

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
                 * The inventory category or classification of the items being reported. This is meant not for defining the product, but 
                 * for inventory categories e.g. 'pending recount' or 'damaged'.
                 * 
                 * @param category
                 *     The inventory category or classification of the items being reported
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder category(CodeableConcept category) {
                    this.category = category;
                    return this;
                }

                /**
                 * The quantity of the item or items being reported.
                 * 
                 * <p>This element is required.
                 * 
                 * @param quantity
                 *     The quantity of the item or items being reported
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(Quantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * The code or reference to the item type.
                 * 
                 * <p>This element is required.
                 * 
                 * @param item
                 *     The code or reference to the item type
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder item(CodeableReference item) {
                    this.item = item;
                    return this;
                }

                /**
                 * Build the {@link Item}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>quantity</li>
                 * <li>item</li>
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
                    ValidationSupport.requireNonNull(item.quantity, "quantity");
                    ValidationSupport.requireNonNull(item.item, "item");
                    ValidationSupport.requireValueOrChildren(item);
                }

                protected Builder from(Item item) {
                    super.from(item);
                    category = item.category;
                    quantity = item.quantity;
                    this.item = item.item;
                    return this;
                }
            }
        }
    }
}
