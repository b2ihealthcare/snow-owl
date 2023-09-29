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
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ImmunizationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Describes the event of a patient being administered a vaccine or a record of an immunization as reported by a patient, 
 * a clinician or another party.
 * 
 * <p>Maturity level: FMM5 (Trial Use)
 */
@Maturity(
    level = 5,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "immunization-0",
    level = "Warning",
    location = "performer.function",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/immunization-function",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/immunization-function', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Immunization",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Immunization extends DomainResource {
    private final List<Identifier> identifier;
    @Summary
    @ReferenceTarget({ "CarePlan", "MedicationRequest", "ServiceRequest", "ImmunizationRecommendation" })
    private final List<Reference> basedOn;
    @Summary
    @Binding(
        bindingName = "ImmunizationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-status|5.0.0"
    )
    @Required
    private final ImmunizationStatus status;
    @Binding(
        bindingName = "ImmunizationStatusReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-status-reason"
    )
    private final CodeableConcept statusReason;
    @Summary
    @Binding(
        bindingName = "VaccineCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/vaccine-code"
    )
    @Required
    private final CodeableConcept vaccineCode;
    private final CodeableReference administeredProduct;
    private final CodeableReference manufacturer;
    private final String lotNumber;
    private final Date expirationDate;
    @Summary
    @ReferenceTarget({ "Patient" })
    @Required
    private final Reference patient;
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    private final List<Reference> supportingInformation;
    @Summary
    @Choice({ DateTime.class, String.class })
    @Required
    private final Element occurrence;
    @Summary
    private final Boolean primarySource;
    @Binding(
        bindingName = "ImmunizationReportOrigin",
        strength = BindingStrength.Value.EXAMPLE,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-origin"
    )
    private final CodeableReference informationSource;
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @Binding(
        bindingName = "ImmunizationSite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-site"
    )
    private final CodeableConcept site;
    @Binding(
        bindingName = "ImmunizationRoute",
        strength = BindingStrength.Value.EXAMPLE,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-route"
    )
    private final CodeableConcept route;
    private final SimpleQuantity doseQuantity;
    @Summary
    private final List<Performer> performer;
    @Summary
    private final List<Annotation> note;
    @Binding(
        bindingName = "ImmunizationReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-reason"
    )
    private final List<CodeableReference> reason;
    @Summary
    private final Boolean isSubpotent;
    @Binding(
        bindingName = "SubpotentReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The reason why a dose is considered to be subpotent.",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-subpotent-reason"
    )
    private final List<CodeableConcept> subpotentReason;
    private final List<ProgramEligibility> programEligibility;
    @Binding(
        bindingName = "FundingSource",
        strength = BindingStrength.Value.EXAMPLE,
        description = "x",
        valueSet = "http://hl7.org/fhir/ValueSet/immunization-funding-source"
    )
    private final CodeableConcept fundingSource;
    private final List<Reaction> reaction;
    private final List<ProtocolApplied> protocolApplied;

    private Immunization(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        status = builder.status;
        statusReason = builder.statusReason;
        vaccineCode = builder.vaccineCode;
        administeredProduct = builder.administeredProduct;
        manufacturer = builder.manufacturer;
        lotNumber = builder.lotNumber;
        expirationDate = builder.expirationDate;
        patient = builder.patient;
        encounter = builder.encounter;
        supportingInformation = Collections.unmodifiableList(builder.supportingInformation);
        occurrence = builder.occurrence;
        primarySource = builder.primarySource;
        informationSource = builder.informationSource;
        location = builder.location;
        site = builder.site;
        route = builder.route;
        doseQuantity = builder.doseQuantity;
        performer = Collections.unmodifiableList(builder.performer);
        note = Collections.unmodifiableList(builder.note);
        reason = Collections.unmodifiableList(builder.reason);
        isSubpotent = builder.isSubpotent;
        subpotentReason = Collections.unmodifiableList(builder.subpotentReason);
        programEligibility = Collections.unmodifiableList(builder.programEligibility);
        fundingSource = builder.fundingSource;
        reaction = Collections.unmodifiableList(builder.reaction);
        protocolApplied = Collections.unmodifiableList(builder.protocolApplied);
    }

    /**
     * A unique identifier assigned to this immunization record.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A plan, order or recommendation fulfilled in whole or in part by this immunization.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * Indicates the current status of the immunization event.
     * 
     * @return
     *     An immutable object of type {@link ImmunizationStatus} that is non-null.
     */
    public ImmunizationStatus getStatus() {
        return status;
    }

    /**
     * Indicates the reason the immunization event was not performed.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getStatusReason() {
        return statusReason;
    }

    /**
     * Vaccine that was administered or was to be administered.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getVaccineCode() {
        return vaccineCode;
    }

    /**
     * An indication of which product was administered to the patient. This is typically a more detailed representation of 
     * the concept conveyed by the vaccineCode data element. If a Medication resource is referenced, it may be to a stand-
     * alone resource or a contained resource within the Immunization resource.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getAdministeredProduct() {
        return administeredProduct;
    }

    /**
     * Name of vaccine manufacturer.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getManufacturer() {
        return manufacturer;
    }

    /**
     * Lot number of the vaccine product.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getLotNumber() {
        return lotNumber;
    }

    /**
     * Date vaccine batch expires.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * The patient who either received or did not receive the immunization.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getPatient() {
        return patient;
    }

    /**
     * The visit or admission or other contact between patient and health care provider the immunization was performed as 
     * part of.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Additional information that is relevant to the immunization (e.g. for a vaccine recipient who is pregnant, the 
     * gestational age of the fetus). The reason why a vaccine was given (e.g. occupation, underlying medical condition) 
     * should be conveyed in Immunization.reason, not as supporting information. The reason why a vaccine was not given (e.g. 
     * contraindication) should be conveyed in Immunization.statusReason, not as supporting information.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInformation() {
        return supportingInformation;
    }

    /**
     * Date vaccine administered or was to be administered.
     * 
     * @return
     *     An immutable object of type {@link DateTime} or {@link String} that is non-null.
     */
    public Element getOccurrence() {
        return occurrence;
    }

    /**
     * Indicates whether the data contained in the resource was captured by the individual/organization which was responsible 
     * for the administration of the vaccine rather than as 'secondary reported' data documented by a third party. A value of 
     * 'true' means this data originated with the individual/organization which was responsible for the administration of the 
     * vaccine.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getPrimarySource() {
        return primarySource;
    }

    /**
     * Typically the source of the data when the report of the immunization event is not based on information from the person 
     * who administered the vaccine.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getInformationSource() {
        return informationSource;
    }

    /**
     * The service delivery location where the vaccine administration occurred.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * Body site where vaccine was administered.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getSite() {
        return site;
    }

    /**
     * The path by which the vaccine product is taken into the body.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getRoute() {
        return route;
    }

    /**
     * The quantity of vaccine product that was administered.
     * 
     * @return
     *     An immutable object of type {@link SimpleQuantity} that may be null.
     */
    public SimpleQuantity getDoseQuantity() {
        return doseQuantity;
    }

    /**
     * Indicates who performed the immunization event.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
     */
    public List<Performer> getPerformer() {
        return performer;
    }

    /**
     * Extra information about the immunization that is not conveyed by the other attributes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Describes why the immunization occurred in coded or textual form, or Indicates another resource (Condition, 
     * Observation or DiagnosticReport) whose existence justifies this immunization.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Indication if a dose is considered to be subpotent. By default, a dose should be considered to be potent.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getIsSubpotent() {
        return isSubpotent;
    }

    /**
     * Reason why a dose is considered to be subpotent.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSubpotentReason() {
        return subpotentReason;
    }

    /**
     * Indicates a patient's eligibility for a funding program.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ProgramEligibility} that may be empty.
     */
    public List<ProgramEligibility> getProgramEligibility() {
        return programEligibility;
    }

    /**
     * Indicates the source of the vaccine actually administered. This may be different than the patient eligibility (e.g. 
     * the patient may be eligible for a publically purchased vaccine but due to inventory issues, vaccine purchased with 
     * private funds was actually administered).
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getFundingSource() {
        return fundingSource;
    }

    /**
     * Categorical data indicating that an adverse event is associated in time to an immunization.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reaction} that may be empty.
     */
    public List<Reaction> getReaction() {
        return reaction;
    }

    /**
     * The protocol (set of recommendations) being followed by the provider who administered the dose.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ProtocolApplied} that may be empty.
     */
    public List<ProtocolApplied> getProtocolApplied() {
        return protocolApplied;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !basedOn.isEmpty() || 
            (status != null) || 
            (statusReason != null) || 
            (vaccineCode != null) || 
            (administeredProduct != null) || 
            (manufacturer != null) || 
            (lotNumber != null) || 
            (expirationDate != null) || 
            (patient != null) || 
            (encounter != null) || 
            !supportingInformation.isEmpty() || 
            (occurrence != null) || 
            (primarySource != null) || 
            (informationSource != null) || 
            (location != null) || 
            (site != null) || 
            (route != null) || 
            (doseQuantity != null) || 
            !performer.isEmpty() || 
            !note.isEmpty() || 
            !reason.isEmpty() || 
            (isSubpotent != null) || 
            !subpotentReason.isEmpty() || 
            !programEligibility.isEmpty() || 
            (fundingSource != null) || 
            !reaction.isEmpty() || 
            !protocolApplied.isEmpty();
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
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(status, "status", visitor);
                accept(statusReason, "statusReason", visitor);
                accept(vaccineCode, "vaccineCode", visitor);
                accept(administeredProduct, "administeredProduct", visitor);
                accept(manufacturer, "manufacturer", visitor);
                accept(lotNumber, "lotNumber", visitor);
                accept(expirationDate, "expirationDate", visitor);
                accept(patient, "patient", visitor);
                accept(encounter, "encounter", visitor);
                accept(supportingInformation, "supportingInformation", visitor, Reference.class);
                accept(occurrence, "occurrence", visitor);
                accept(primarySource, "primarySource", visitor);
                accept(informationSource, "informationSource", visitor);
                accept(location, "location", visitor);
                accept(site, "site", visitor);
                accept(route, "route", visitor);
                accept(doseQuantity, "doseQuantity", visitor);
                accept(performer, "performer", visitor, Performer.class);
                accept(note, "note", visitor, Annotation.class);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(isSubpotent, "isSubpotent", visitor);
                accept(subpotentReason, "subpotentReason", visitor, CodeableConcept.class);
                accept(programEligibility, "programEligibility", visitor, ProgramEligibility.class);
                accept(fundingSource, "fundingSource", visitor);
                accept(reaction, "reaction", visitor, Reaction.class);
                accept(protocolApplied, "protocolApplied", visitor, ProtocolApplied.class);
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
        Immunization other = (Immunization) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(status, other.status) && 
            Objects.equals(statusReason, other.statusReason) && 
            Objects.equals(vaccineCode, other.vaccineCode) && 
            Objects.equals(administeredProduct, other.administeredProduct) && 
            Objects.equals(manufacturer, other.manufacturer) && 
            Objects.equals(lotNumber, other.lotNumber) && 
            Objects.equals(expirationDate, other.expirationDate) && 
            Objects.equals(patient, other.patient) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(supportingInformation, other.supportingInformation) && 
            Objects.equals(occurrence, other.occurrence) && 
            Objects.equals(primarySource, other.primarySource) && 
            Objects.equals(informationSource, other.informationSource) && 
            Objects.equals(location, other.location) && 
            Objects.equals(site, other.site) && 
            Objects.equals(route, other.route) && 
            Objects.equals(doseQuantity, other.doseQuantity) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(note, other.note) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(isSubpotent, other.isSubpotent) && 
            Objects.equals(subpotentReason, other.subpotentReason) && 
            Objects.equals(programEligibility, other.programEligibility) && 
            Objects.equals(fundingSource, other.fundingSource) && 
            Objects.equals(reaction, other.reaction) && 
            Objects.equals(protocolApplied, other.protocolApplied);
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
                basedOn, 
                status, 
                statusReason, 
                vaccineCode, 
                administeredProduct, 
                manufacturer, 
                lotNumber, 
                expirationDate, 
                patient, 
                encounter, 
                supportingInformation, 
                occurrence, 
                primarySource, 
                informationSource, 
                location, 
                site, 
                route, 
                doseQuantity, 
                performer, 
                note, 
                reason, 
                isSubpotent, 
                subpotentReason, 
                programEligibility, 
                fundingSource, 
                reaction, 
                protocolApplied);
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
        private List<Reference> basedOn = new ArrayList<>();
        private ImmunizationStatus status;
        private CodeableConcept statusReason;
        private CodeableConcept vaccineCode;
        private CodeableReference administeredProduct;
        private CodeableReference manufacturer;
        private String lotNumber;
        private Date expirationDate;
        private Reference patient;
        private Reference encounter;
        private List<Reference> supportingInformation = new ArrayList<>();
        private Element occurrence;
        private Boolean primarySource;
        private CodeableReference informationSource;
        private Reference location;
        private CodeableConcept site;
        private CodeableConcept route;
        private SimpleQuantity doseQuantity;
        private List<Performer> performer = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<CodeableReference> reason = new ArrayList<>();
        private Boolean isSubpotent;
        private List<CodeableConcept> subpotentReason = new ArrayList<>();
        private List<ProgramEligibility> programEligibility = new ArrayList<>();
        private CodeableConcept fundingSource;
        private List<Reaction> reaction = new ArrayList<>();
        private List<ProtocolApplied> protocolApplied = new ArrayList<>();

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
         * A unique identifier assigned to this immunization record.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier
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
         * A unique identifier assigned to this immunization record.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business identifier
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
         * A plan, order or recommendation fulfilled in whole or in part by this immunization.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * </ul>
         * 
         * @param basedOn
         *     Authority that the immunization event is based on
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
         * A plan, order or recommendation fulfilled in whole or in part by this immunization.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link MedicationRequest}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link ImmunizationRecommendation}</li>
         * </ul>
         * 
         * @param basedOn
         *     Authority that the immunization event is based on
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
         * Indicates the current status of the immunization event.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     completed | entered-in-error | not-done
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(ImmunizationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Indicates the reason the immunization event was not performed.
         * 
         * @param statusReason
         *     Reason for current status
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusReason(CodeableConcept statusReason) {
            this.statusReason = statusReason;
            return this;
        }

        /**
         * Vaccine that was administered or was to be administered.
         * 
         * <p>This element is required.
         * 
         * @param vaccineCode
         *     Vaccine administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder vaccineCode(CodeableConcept vaccineCode) {
            this.vaccineCode = vaccineCode;
            return this;
        }

        /**
         * An indication of which product was administered to the patient. This is typically a more detailed representation of 
         * the concept conveyed by the vaccineCode data element. If a Medication resource is referenced, it may be to a stand-
         * alone resource or a contained resource within the Immunization resource.
         * 
         * @param administeredProduct
         *     Product that was administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder administeredProduct(CodeableReference administeredProduct) {
            this.administeredProduct = administeredProduct;
            return this;
        }

        /**
         * Name of vaccine manufacturer.
         * 
         * @param manufacturer
         *     Vaccine manufacturer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder manufacturer(CodeableReference manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        /**
         * Convenience method for setting {@code lotNumber}.
         * 
         * @param lotNumber
         *     Vaccine lot number
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #lotNumber(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder lotNumber(java.lang.String lotNumber) {
            this.lotNumber = (lotNumber == null) ? null : String.of(lotNumber);
            return this;
        }

        /**
         * Lot number of the vaccine product.
         * 
         * @param lotNumber
         *     Vaccine lot number
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
            return this;
        }

        /**
         * Convenience method for setting {@code expirationDate}.
         * 
         * @param expirationDate
         *     Vaccine expiration date
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
         * Date vaccine batch expires.
         * 
         * @param expirationDate
         *     Vaccine expiration date
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder expirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        /**
         * The patient who either received or did not receive the immunization.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * </ul>
         * 
         * @param patient
         *     Who was immunized
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder patient(Reference patient) {
            this.patient = patient;
            return this;
        }

        /**
         * The visit or admission or other contact between patient and health care provider the immunization was performed as 
         * part of.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter immunization was part of
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Additional information that is relevant to the immunization (e.g. for a vaccine recipient who is pregnant, the 
         * gestational age of the fetus). The reason why a vaccine was given (e.g. occupation, underlying medical condition) 
         * should be conveyed in Immunization.reason, not as supporting information. The reason why a vaccine was not given (e.g. 
         * contraindication) should be conveyed in Immunization.statusReason, not as supporting information.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Additional information in support of the immunization
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
         * Additional information that is relevant to the immunization (e.g. for a vaccine recipient who is pregnant, the 
         * gestational age of the fetus). The reason why a vaccine was given (e.g. occupation, underlying medical condition) 
         * should be conveyed in Immunization.reason, not as supporting information. The reason why a vaccine was not given (e.g. 
         * contraindication) should be conveyed in Immunization.statusReason, not as supporting information.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInformation
         *     Additional information in support of the immunization
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
         * Convenience method for setting {@code occurrence} with choice type String.
         * 
         * <p>This element is required.
         * 
         * @param occurrence
         *     Vaccine administration date
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #occurrence(Element)
         */
        public Builder occurrence(java.lang.String occurrence) {
            this.occurrence = (occurrence == null) ? null : String.of(occurrence);
            return this;
        }

        /**
         * Date vaccine administered or was to be administered.
         * 
         * <p>This element is required.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link String}</li>
         * </ul>
         * 
         * @param occurrence
         *     Vaccine administration date
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrence(Element occurrence) {
            this.occurrence = occurrence;
            return this;
        }

        /**
         * Convenience method for setting {@code primarySource}.
         * 
         * @param primarySource
         *     Indicates context the data was captured in
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #primarySource(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder primarySource(java.lang.Boolean primarySource) {
            this.primarySource = (primarySource == null) ? null : Boolean.of(primarySource);
            return this;
        }

        /**
         * Indicates whether the data contained in the resource was captured by the individual/organization which was responsible 
         * for the administration of the vaccine rather than as 'secondary reported' data documented by a third party. A value of 
         * 'true' means this data originated with the individual/organization which was responsible for the administration of the 
         * vaccine.
         * 
         * @param primarySource
         *     Indicates context the data was captured in
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder primarySource(Boolean primarySource) {
            this.primarySource = primarySource;
            return this;
        }

        /**
         * Typically the source of the data when the report of the immunization event is not based on information from the person 
         * who administered the vaccine.
         * 
         * @param informationSource
         *     Indicates the source of a reported record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder informationSource(CodeableReference informationSource) {
            this.informationSource = informationSource;
            return this;
        }

        /**
         * The service delivery location where the vaccine administration occurred.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where immunization occurred
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * Body site where vaccine was administered.
         * 
         * @param site
         *     Body site vaccine was administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder site(CodeableConcept site) {
            this.site = site;
            return this;
        }

        /**
         * The path by which the vaccine product is taken into the body.
         * 
         * @param route
         *     How vaccine entered body
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder route(CodeableConcept route) {
            this.route = route;
            return this;
        }

        /**
         * The quantity of vaccine product that was administered.
         * 
         * @param doseQuantity
         *     Amount of vaccine administered
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder doseQuantity(SimpleQuantity doseQuantity) {
            this.doseQuantity = doseQuantity;
            return this;
        }

        /**
         * Indicates who performed the immunization event.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who performed event
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
         * Indicates who performed the immunization event.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param performer
         *     Who performed event
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
         * Extra information about the immunization that is not conveyed by the other attributes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional immunization notes
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
         * Extra information about the immunization that is not conveyed by the other attributes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Additional immunization notes
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
         * Describes why the immunization occurred in coded or textual form, or Indicates another resource (Condition, 
         * Observation or DiagnosticReport) whose existence justifies this immunization.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why immunization occurred
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
         * Describes why the immunization occurred in coded or textual form, or Indicates another resource (Condition, 
         * Observation or DiagnosticReport) whose existence justifies this immunization.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why immunization occurred
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
         * Convenience method for setting {@code isSubpotent}.
         * 
         * @param isSubpotent
         *     Dose potency
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #isSubpotent(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder isSubpotent(java.lang.Boolean isSubpotent) {
            this.isSubpotent = (isSubpotent == null) ? null : Boolean.of(isSubpotent);
            return this;
        }

        /**
         * Indication if a dose is considered to be subpotent. By default, a dose should be considered to be potent.
         * 
         * @param isSubpotent
         *     Dose potency
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder isSubpotent(Boolean isSubpotent) {
            this.isSubpotent = isSubpotent;
            return this;
        }

        /**
         * Reason why a dose is considered to be subpotent.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subpotentReason
         *     Reason for being subpotent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subpotentReason(CodeableConcept... subpotentReason) {
            for (CodeableConcept value : subpotentReason) {
                this.subpotentReason.add(value);
            }
            return this;
        }

        /**
         * Reason why a dose is considered to be subpotent.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subpotentReason
         *     Reason for being subpotent
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder subpotentReason(Collection<CodeableConcept> subpotentReason) {
            this.subpotentReason = new ArrayList<>(subpotentReason);
            return this;
        }

        /**
         * Indicates a patient's eligibility for a funding program.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param programEligibility
         *     Patient eligibility for a specific vaccination program
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder programEligibility(ProgramEligibility... programEligibility) {
            for (ProgramEligibility value : programEligibility) {
                this.programEligibility.add(value);
            }
            return this;
        }

        /**
         * Indicates a patient's eligibility for a funding program.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param programEligibility
         *     Patient eligibility for a specific vaccination program
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder programEligibility(Collection<ProgramEligibility> programEligibility) {
            this.programEligibility = new ArrayList<>(programEligibility);
            return this;
        }

        /**
         * Indicates the source of the vaccine actually administered. This may be different than the patient eligibility (e.g. 
         * the patient may be eligible for a publically purchased vaccine but due to inventory issues, vaccine purchased with 
         * private funds was actually administered).
         * 
         * @param fundingSource
         *     Funding source for the vaccine
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder fundingSource(CodeableConcept fundingSource) {
            this.fundingSource = fundingSource;
            return this;
        }

        /**
         * Categorical data indicating that an adverse event is associated in time to an immunization.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reaction
         *     Details of a reaction that follows immunization
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reaction(Reaction... reaction) {
            for (Reaction value : reaction) {
                this.reaction.add(value);
            }
            return this;
        }

        /**
         * Categorical data indicating that an adverse event is associated in time to an immunization.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reaction
         *     Details of a reaction that follows immunization
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reaction(Collection<Reaction> reaction) {
            this.reaction = new ArrayList<>(reaction);
            return this;
        }

        /**
         * The protocol (set of recommendations) being followed by the provider who administered the dose.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param protocolApplied
         *     Protocol followed by the provider
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder protocolApplied(ProtocolApplied... protocolApplied) {
            for (ProtocolApplied value : protocolApplied) {
                this.protocolApplied.add(value);
            }
            return this;
        }

        /**
         * The protocol (set of recommendations) being followed by the provider who administered the dose.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param protocolApplied
         *     Protocol followed by the provider
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder protocolApplied(Collection<ProtocolApplied> protocolApplied) {
            this.protocolApplied = new ArrayList<>(protocolApplied);
            return this;
        }

        /**
         * Build the {@link Immunization}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>vaccineCode</li>
         * <li>patient</li>
         * <li>occurrence</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Immunization}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Immunization per the base specification
         */
        @Override
        public Immunization build() {
            Immunization immunization = new Immunization(this);
            if (validating) {
                validate(immunization);
            }
            return immunization;
        }

        protected void validate(Immunization immunization) {
            super.validate(immunization);
            ValidationSupport.checkList(immunization.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(immunization.basedOn, "basedOn", Reference.class);
            ValidationSupport.requireNonNull(immunization.status, "status");
            ValidationSupport.requireNonNull(immunization.vaccineCode, "vaccineCode");
            ValidationSupport.requireNonNull(immunization.patient, "patient");
            ValidationSupport.checkList(immunization.supportingInformation, "supportingInformation", Reference.class);
            ValidationSupport.requireChoiceElement(immunization.occurrence, "occurrence", DateTime.class, String.class);
            ValidationSupport.checkList(immunization.performer, "performer", Performer.class);
            ValidationSupport.checkList(immunization.note, "note", Annotation.class);
            ValidationSupport.checkList(immunization.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(immunization.subpotentReason, "subpotentReason", CodeableConcept.class);
            ValidationSupport.checkList(immunization.programEligibility, "programEligibility", ProgramEligibility.class);
            ValidationSupport.checkList(immunization.reaction, "reaction", Reaction.class);
            ValidationSupport.checkList(immunization.protocolApplied, "protocolApplied", ProtocolApplied.class);
            ValidationSupport.checkReferenceType(immunization.basedOn, "basedOn", "CarePlan", "MedicationRequest", "ServiceRequest", "ImmunizationRecommendation");
            ValidationSupport.checkReferenceType(immunization.patient, "patient", "Patient");
            ValidationSupport.checkReferenceType(immunization.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(immunization.location, "location", "Location");
        }

        protected Builder from(Immunization immunization) {
            super.from(immunization);
            identifier.addAll(immunization.identifier);
            basedOn.addAll(immunization.basedOn);
            status = immunization.status;
            statusReason = immunization.statusReason;
            vaccineCode = immunization.vaccineCode;
            administeredProduct = immunization.administeredProduct;
            manufacturer = immunization.manufacturer;
            lotNumber = immunization.lotNumber;
            expirationDate = immunization.expirationDate;
            patient = immunization.patient;
            encounter = immunization.encounter;
            supportingInformation.addAll(immunization.supportingInformation);
            occurrence = immunization.occurrence;
            primarySource = immunization.primarySource;
            informationSource = immunization.informationSource;
            location = immunization.location;
            site = immunization.site;
            route = immunization.route;
            doseQuantity = immunization.doseQuantity;
            performer.addAll(immunization.performer);
            note.addAll(immunization.note);
            reason.addAll(immunization.reason);
            isSubpotent = immunization.isSubpotent;
            subpotentReason.addAll(immunization.subpotentReason);
            programEligibility.addAll(immunization.programEligibility);
            fundingSource = immunization.fundingSource;
            reaction.addAll(immunization.reaction);
            protocolApplied.addAll(immunization.protocolApplied);
            return this;
        }
    }

    /**
     * Indicates who performed the immunization event.
     */
    public static class Performer extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "ImmunizationFunction",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "x",
            valueSet = "http://hl7.org/fhir/ValueSet/immunization-function"
        )
        private final CodeableConcept function;
        @Summary
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson" })
        @Required
        private final Reference actor;

        private Performer(Builder builder) {
            super(builder);
            function = builder.function;
            actor = builder.actor;
        }

        /**
         * Describes the type of performance (e.g. ordering provider, administering provider, etc.).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        /**
         * The practitioner or organization who performed the action.
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
             * Describes the type of performance (e.g. ordering provider, administering provider, etc.).
             * 
             * @param function
             *     What type of performance was done
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * The practitioner or organization who performed the action.
             * 
             * <p>This element is required.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param actor
             *     Individual or organization who was performing
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
                ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner", "PractitionerRole", "Organization", "Patient", "RelatedPerson");
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

    /**
     * Indicates a patient's eligibility for a funding program.
     */
    public static class ProgramEligibility extends BackboneElement {
        @Binding(
            bindingName = "VaccineFundingProgram",
            strength = BindingStrength.Value.EXAMPLE,
            description = "x",
            valueSet = "http://hl7.org/fhir/ValueSet/immunization-vaccine-funding-program"
        )
        @Required
        private final CodeableConcept program;
        @Binding(
            bindingName = "ProgramEligibility",
            strength = BindingStrength.Value.EXAMPLE,
            description = "x",
            valueSet = "http://hl7.org/fhir/ValueSet/immunization-program-eligibility"
        )
        @Required
        private final CodeableConcept programStatus;

        private ProgramEligibility(Builder builder) {
            super(builder);
            program = builder.program;
            programStatus = builder.programStatus;
        }

        /**
         * Indicates which program the patient had their eligility evaluated for.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getProgram() {
            return program;
        }

        /**
         * Indicates the patient's eligility status for for a specific payment program.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getProgramStatus() {
            return programStatus;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (program != null) || 
                (programStatus != null);
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
                    accept(program, "program", visitor);
                    accept(programStatus, "programStatus", visitor);
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
            ProgramEligibility other = (ProgramEligibility) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(program, other.program) && 
                Objects.equals(programStatus, other.programStatus);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    program, 
                    programStatus);
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
            private CodeableConcept program;
            private CodeableConcept programStatus;

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
             * Indicates which program the patient had their eligility evaluated for.
             * 
             * <p>This element is required.
             * 
             * @param program
             *     The program that eligibility is declared for
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder program(CodeableConcept program) {
                this.program = program;
                return this;
            }

            /**
             * Indicates the patient's eligility status for for a specific payment program.
             * 
             * <p>This element is required.
             * 
             * @param programStatus
             *     The patient's eligibility status for the program
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder programStatus(CodeableConcept programStatus) {
                this.programStatus = programStatus;
                return this;
            }

            /**
             * Build the {@link ProgramEligibility}
             * 
             * <p>Required elements:
             * <ul>
             * <li>program</li>
             * <li>programStatus</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ProgramEligibility}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ProgramEligibility per the base specification
             */
            @Override
            public ProgramEligibility build() {
                ProgramEligibility programEligibility = new ProgramEligibility(this);
                if (validating) {
                    validate(programEligibility);
                }
                return programEligibility;
            }

            protected void validate(ProgramEligibility programEligibility) {
                super.validate(programEligibility);
                ValidationSupport.requireNonNull(programEligibility.program, "program");
                ValidationSupport.requireNonNull(programEligibility.programStatus, "programStatus");
                ValidationSupport.requireValueOrChildren(programEligibility);
            }

            protected Builder from(ProgramEligibility programEligibility) {
                super.from(programEligibility);
                program = programEligibility.program;
                programStatus = programEligibility.programStatus;
                return this;
            }
        }
    }

    /**
     * Categorical data indicating that an adverse event is associated in time to an immunization.
     */
    public static class Reaction extends BackboneElement {
        private final DateTime date;
        private final CodeableReference manifestation;
        private final Boolean reported;

        private Reaction(Builder builder) {
            super(builder);
            date = builder.date;
            manifestation = builder.manifestation;
            reported = builder.reported;
        }

        /**
         * Date of reaction to the immunization.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getDate() {
            return date;
        }

        /**
         * Details of the reaction.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getManifestation() {
            return manifestation;
        }

        /**
         * Self-reported indicator.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getReported() {
            return reported;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (date != null) || 
                (manifestation != null) || 
                (reported != null);
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
                    accept(date, "date", visitor);
                    accept(manifestation, "manifestation", visitor);
                    accept(reported, "reported", visitor);
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
            Reaction other = (Reaction) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(date, other.date) && 
                Objects.equals(manifestation, other.manifestation) && 
                Objects.equals(reported, other.reported);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    date, 
                    manifestation, 
                    reported);
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
            private DateTime date;
            private CodeableReference manifestation;
            private Boolean reported;

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
             * Date of reaction to the immunization.
             * 
             * @param date
             *     When reaction started
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder date(DateTime date) {
                this.date = date;
                return this;
            }

            /**
             * Details of the reaction.
             * 
             * @param manifestation
             *     Additional information on reaction
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder manifestation(CodeableReference manifestation) {
                this.manifestation = manifestation;
                return this;
            }

            /**
             * Convenience method for setting {@code reported}.
             * 
             * @param reported
             *     Indicates self-reported reaction
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #reported(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder reported(java.lang.Boolean reported) {
                this.reported = (reported == null) ? null : Boolean.of(reported);
                return this;
            }

            /**
             * Self-reported indicator.
             * 
             * @param reported
             *     Indicates self-reported reaction
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reported(Boolean reported) {
                this.reported = reported;
                return this;
            }

            /**
             * Build the {@link Reaction}
             * 
             * @return
             *     An immutable object of type {@link Reaction}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Reaction per the base specification
             */
            @Override
            public Reaction build() {
                Reaction reaction = new Reaction(this);
                if (validating) {
                    validate(reaction);
                }
                return reaction;
            }

            protected void validate(Reaction reaction) {
                super.validate(reaction);
                ValidationSupport.requireValueOrChildren(reaction);
            }

            protected Builder from(Reaction reaction) {
                super.from(reaction);
                date = reaction.date;
                manifestation = reaction.manifestation;
                reported = reaction.reported;
                return this;
            }
        }
    }

    /**
     * The protocol (set of recommendations) being followed by the provider who administered the dose.
     */
    public static class ProtocolApplied extends BackboneElement {
        private final String series;
        @ReferenceTarget({ "Organization" })
        private final Reference authority;
        @Binding(
            bindingName = "TargetDisease",
            strength = BindingStrength.Value.EXAMPLE,
            description = "x",
            valueSet = "http://hl7.org/fhir/ValueSet/immunization-target-disease"
        )
        private final List<CodeableConcept> targetDisease;
        @Required
        private final String doseNumber;
        private final String seriesDoses;

        private ProtocolApplied(Builder builder) {
            super(builder);
            series = builder.series;
            authority = builder.authority;
            targetDisease = Collections.unmodifiableList(builder.targetDisease);
            doseNumber = builder.doseNumber;
            seriesDoses = builder.seriesDoses;
        }

        /**
         * One possible path to achieve presumed immunity against a disease - within the context of an authority.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getSeries() {
            return series;
        }

        /**
         * Indicates the authority who published the protocol (e.g. ACIP) that is being followed.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getAuthority() {
            return authority;
        }

        /**
         * The vaccine preventable disease the dose is being administered against.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getTargetDisease() {
            return targetDisease;
        }

        /**
         * Nominal position in a series as intended by the practitioner administering the dose.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getDoseNumber() {
            return doseNumber;
        }

        /**
         * The recommended number of doses to achieve immunity as intended by the practitioner administering the dose.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getSeriesDoses() {
            return seriesDoses;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (series != null) || 
                (authority != null) || 
                !targetDisease.isEmpty() || 
                (doseNumber != null) || 
                (seriesDoses != null);
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
                    accept(series, "series", visitor);
                    accept(authority, "authority", visitor);
                    accept(targetDisease, "targetDisease", visitor, CodeableConcept.class);
                    accept(doseNumber, "doseNumber", visitor);
                    accept(seriesDoses, "seriesDoses", visitor);
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
            ProtocolApplied other = (ProtocolApplied) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(series, other.series) && 
                Objects.equals(authority, other.authority) && 
                Objects.equals(targetDisease, other.targetDisease) && 
                Objects.equals(doseNumber, other.doseNumber) && 
                Objects.equals(seriesDoses, other.seriesDoses);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    series, 
                    authority, 
                    targetDisease, 
                    doseNumber, 
                    seriesDoses);
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
            private String series;
            private Reference authority;
            private List<CodeableConcept> targetDisease = new ArrayList<>();
            private String doseNumber;
            private String seriesDoses;

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
             * Convenience method for setting {@code series}.
             * 
             * @param series
             *     Name of vaccine series
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #series(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder series(java.lang.String series) {
                this.series = (series == null) ? null : String.of(series);
                return this;
            }

            /**
             * One possible path to achieve presumed immunity against a disease - within the context of an authority.
             * 
             * @param series
             *     Name of vaccine series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder series(String series) {
                this.series = series;
                return this;
            }

            /**
             * Indicates the authority who published the protocol (e.g. ACIP) that is being followed.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param authority
             *     Who is responsible for publishing the recommendations
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder authority(Reference authority) {
                this.authority = authority;
                return this;
            }

            /**
             * The vaccine preventable disease the dose is being administered against.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param targetDisease
             *     Vaccine preventatable disease being targeted
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder targetDisease(CodeableConcept... targetDisease) {
                for (CodeableConcept value : targetDisease) {
                    this.targetDisease.add(value);
                }
                return this;
            }

            /**
             * The vaccine preventable disease the dose is being administered against.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param targetDisease
             *     Vaccine preventatable disease being targeted
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder targetDisease(Collection<CodeableConcept> targetDisease) {
                this.targetDisease = new ArrayList<>(targetDisease);
                return this;
            }

            /**
             * Convenience method for setting {@code doseNumber}.
             * 
             * <p>This element is required.
             * 
             * @param doseNumber
             *     Dose number within series
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #doseNumber(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder doseNumber(java.lang.String doseNumber) {
                this.doseNumber = (doseNumber == null) ? null : String.of(doseNumber);
                return this;
            }

            /**
             * Nominal position in a series as intended by the practitioner administering the dose.
             * 
             * <p>This element is required.
             * 
             * @param doseNumber
             *     Dose number within series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder doseNumber(String doseNumber) {
                this.doseNumber = doseNumber;
                return this;
            }

            /**
             * Convenience method for setting {@code seriesDoses}.
             * 
             * @param seriesDoses
             *     Recommended number of doses for immunity
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #seriesDoses(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder seriesDoses(java.lang.String seriesDoses) {
                this.seriesDoses = (seriesDoses == null) ? null : String.of(seriesDoses);
                return this;
            }

            /**
             * The recommended number of doses to achieve immunity as intended by the practitioner administering the dose.
             * 
             * @param seriesDoses
             *     Recommended number of doses for immunity
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder seriesDoses(String seriesDoses) {
                this.seriesDoses = seriesDoses;
                return this;
            }

            /**
             * Build the {@link ProtocolApplied}
             * 
             * <p>Required elements:
             * <ul>
             * <li>doseNumber</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ProtocolApplied}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ProtocolApplied per the base specification
             */
            @Override
            public ProtocolApplied build() {
                ProtocolApplied protocolApplied = new ProtocolApplied(this);
                if (validating) {
                    validate(protocolApplied);
                }
                return protocolApplied;
            }

            protected void validate(ProtocolApplied protocolApplied) {
                super.validate(protocolApplied);
                ValidationSupport.checkList(protocolApplied.targetDisease, "targetDisease", CodeableConcept.class);
                ValidationSupport.requireNonNull(protocolApplied.doseNumber, "doseNumber");
                ValidationSupport.checkReferenceType(protocolApplied.authority, "authority", "Organization");
                ValidationSupport.requireValueOrChildren(protocolApplied);
            }

            protected Builder from(ProtocolApplied protocolApplied) {
                super.from(protocolApplied);
                series = protocolApplied.series;
                authority = protocolApplied.authority;
                targetDisease.addAll(protocolApplied.targetDisease);
                doseNumber = protocolApplied.doseNumber;
                seriesDoses = protocolApplied.seriesDoses;
                return this;
            }
        }
    }
}
