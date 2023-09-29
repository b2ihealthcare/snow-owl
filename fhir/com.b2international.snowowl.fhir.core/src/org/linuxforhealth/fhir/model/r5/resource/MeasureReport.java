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
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.MeasureReportStatus;
import org.linuxforhealth.fhir.model.r5.type.code.MeasureReportType;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.SubmitDataUpdateType;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The MeasureReport resource contains the results of the calculation of a measure; and optionally a reference to the 
 * resources involved in that calculation.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "mrp-1",
    level = "Rule",
    location = "(base)",
    description = "Measure Reports used for data collection SHALL NOT communicate group and score information",
    expression = "(type != 'data-exchange') or group.exists().not()",
    source = "http://hl7.org/fhir/StructureDefinition/MeasureReport"
)
@Constraint(
    id = "mrp-2",
    level = "Rule",
    location = "(base)",
    description = "Stratifiers SHALL be either a single criteria or a set of criteria components",
    expression = "group.stratifier.stratum.all(value.exists() xor component.exists())",
    source = "http://hl7.org/fhir/StructureDefinition/MeasureReport"
)
@Constraint(
    id = "measureReport-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/measure-scoring",
    expression = "scoring.exists() implies (scoring.memberOf('http://terminology.hl7.org/ValueSet/measure-scoring', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/MeasureReport",
    generated = true
)
@Constraint(
    id = "measureReport-4",
    level = "Warning",
    location = "group.population.code",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/measure-population",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/measure-population', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/MeasureReport",
    generated = true
)
@Constraint(
    id = "measureReport-5",
    level = "Warning",
    location = "group.stratifier.stratum.population.code",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/measure-population",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/measure-population', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/MeasureReport",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class MeasureReport extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "MeasureReportStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the measure report (e.g. complete, pending, or error)",
        valueSet = "http://hl7.org/fhir/ValueSet/measure-report-status|5.0.0"
    )
    @Required
    private final MeasureReportStatus status;
    @Summary
    @Binding(
        bindingName = "MeasureReportType",
        strength = BindingStrength.Value.REQUIRED,
        description = "The type of the measure report: individual, patient listing, or summary",
        valueSet = "http://hl7.org/fhir/ValueSet/measure-report-type|5.0.0"
    )
    @Required
    private final MeasureReportType type;
    @Summary
    @Binding(
        bindingName = "SubmitDataUpdateType",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/submit-data-update-type|5.0.0"
    )
    private final SubmitDataUpdateType dataUpdateType;
    @Summary
    private final Canonical measure;
    @Summary
    @ReferenceTarget({ "CareTeam", "Device", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
    private final Reference subject;
    @Summary
    private final DateTime date;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "Group" })
    private final Reference reporter;
    @ReferenceTarget({ "Organization" })
    private final Reference reportingVendor;
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @Summary
    @Required
    private final Period period;
    @ReferenceTarget({ "Parameters" })
    private final Reference inputParameters;
    @Summary
    @Binding(
        bindingName = "MeasureScoring",
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://terminology.hl7.org/ValueSet/measure-scoring"
    )
    private final CodeableConcept scoring;
    @Summary
    @Binding(
        bindingName = "MeasureImprovementNotation",
        strength = BindingStrength.Value.REQUIRED,
        description = "The improvement notation of the measure report (e.g. increase or decrease)",
        valueSet = "http://hl7.org/fhir/ValueSet/measure-improvement-notation|5.0.0"
    )
    private final CodeableConcept improvementNotation;
    private final List<Group> group;
    private final List<Reference> supplementalData;
    private final List<Reference> evaluatedResource;

    private MeasureReport(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        type = builder.type;
        dataUpdateType = builder.dataUpdateType;
        measure = builder.measure;
        subject = builder.subject;
        date = builder.date;
        reporter = builder.reporter;
        reportingVendor = builder.reportingVendor;
        location = builder.location;
        period = builder.period;
        inputParameters = builder.inputParameters;
        scoring = builder.scoring;
        improvementNotation = builder.improvementNotation;
        group = Collections.unmodifiableList(builder.group);
        supplementalData = Collections.unmodifiableList(builder.supplementalData);
        evaluatedResource = Collections.unmodifiableList(builder.evaluatedResource);
    }

    /**
     * A formal identifier that is used to identify this MeasureReport when it is represented in other formats or referenced 
     * in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The MeasureReport status. No data will be available until the MeasureReport status is complete.
     * 
     * @return
     *     An immutable object of type {@link MeasureReportStatus} that is non-null.
     */
    public MeasureReportStatus getStatus() {
        return status;
    }

    /**
     * The type of measure report. This may be an individual report, which provides the score for the measure for an 
     * individual member of the population; a subject-listing, which returns the list of members that meet the various 
     * criteria in the measure; a summary report, which returns a population count for each of the criteria in the measure; 
     * or a data-collection, which enables the MeasureReport to be used to exchange the data-of-interest for a quality 
     * measure.
     * 
     * @return
     *     An immutable object of type {@link MeasureReportType} that is non-null.
     */
    public MeasureReportType getType() {
        return type;
    }

    /**
     * Indicates whether the data submitted in a data-exchange report represents a snapshot or incremental update. A snapshot 
     * update replaces all previously submitted data for the receiver, whereas an incremental update represents only updated 
     * and/or changed data and should be applied as a differential update to the existing submitted data for the receiver.
     * 
     * @return
     *     An immutable object of type {@link SubmitDataUpdateType} that may be null.
     */
    public SubmitDataUpdateType getDataUpdateType() {
        return dataUpdateType;
    }

    /**
     * A reference to the Measure that was calculated to produce this report.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getMeasure() {
        return measure;
    }

    /**
     * Optional subject identifying the individual or individuals the report is for.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The date this measure was calculated.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The individual or organization that is reporting the data.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getReporter() {
        return reporter;
    }

    /**
     * A reference to the vendor who queried the data, calculated results and/or generated the report. The ‘reporting 
     * vendor’ is intended to represent the submitting entity when it is not the same as the reporting entity. This 
     * extension is used when the Receiver is interested in getting vendor information in the report.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getReportingVendor() {
        return reportingVendor;
    }

    /**
     * A reference to the location for which the data is being reported.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * The reporting period for which the report was calculated.
     * 
     * @return
     *     An immutable object of type {@link Period} that is non-null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * A reference to a Parameters resource (typically represented using a contained resource) that represents any input 
     * parameters that were provided to the operation that generated the report.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getInputParameters() {
        return inputParameters;
    }

    /**
     * Indicates how the calculation is performed for the measure, including proportion, ratio, continuous-variable, and 
     * cohort. The value set is extensible, allowing additional measure scoring types to be represented. It is expected to be 
     * the same as the scoring element on the referenced Measure.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getScoring() {
        return scoring;
    }

    /**
     * Whether improvement in the measure is noted by an increase or decrease in the measure score.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getImprovementNotation() {
        return improvementNotation;
    }

    /**
     * The results of the calculation, one for each population group in the measure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Group} that may be empty.
     */
    public List<Group> getGroup() {
        return group;
    }

    /**
     * A reference to a Resource that represents additional information collected for the report. If the value of the 
     * supplemental data is not a Resource (i.e. evaluating the supplementalData expression for this case in the measure 
     * results in a value that is not a FHIR Resource), it is reported as a reference to a contained Observation resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupplementalData() {
        return supplementalData;
    }

    /**
     * Evaluated resources are used to capture what data was involved in the calculation of a measure. This usage is only 
     * allowed for individual reports to ensure that the size of the MeasureReport resource is bounded.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEvaluatedResource() {
        return evaluatedResource;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            (type != null) || 
            (dataUpdateType != null) || 
            (measure != null) || 
            (subject != null) || 
            (date != null) || 
            (reporter != null) || 
            (reportingVendor != null) || 
            (location != null) || 
            (period != null) || 
            (inputParameters != null) || 
            (scoring != null) || 
            (improvementNotation != null) || 
            !group.isEmpty() || 
            !supplementalData.isEmpty() || 
            !evaluatedResource.isEmpty();
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
                accept(type, "type", visitor);
                accept(dataUpdateType, "dataUpdateType", visitor);
                accept(measure, "measure", visitor);
                accept(subject, "subject", visitor);
                accept(date, "date", visitor);
                accept(reporter, "reporter", visitor);
                accept(reportingVendor, "reportingVendor", visitor);
                accept(location, "location", visitor);
                accept(period, "period", visitor);
                accept(inputParameters, "inputParameters", visitor);
                accept(scoring, "scoring", visitor);
                accept(improvementNotation, "improvementNotation", visitor);
                accept(group, "group", visitor, Group.class);
                accept(supplementalData, "supplementalData", visitor, Reference.class);
                accept(evaluatedResource, "evaluatedResource", visitor, Reference.class);
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
        MeasureReport other = (MeasureReport) obj;
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
            Objects.equals(type, other.type) && 
            Objects.equals(dataUpdateType, other.dataUpdateType) && 
            Objects.equals(measure, other.measure) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(date, other.date) && 
            Objects.equals(reporter, other.reporter) && 
            Objects.equals(reportingVendor, other.reportingVendor) && 
            Objects.equals(location, other.location) && 
            Objects.equals(period, other.period) && 
            Objects.equals(inputParameters, other.inputParameters) && 
            Objects.equals(scoring, other.scoring) && 
            Objects.equals(improvementNotation, other.improvementNotation) && 
            Objects.equals(group, other.group) && 
            Objects.equals(supplementalData, other.supplementalData) && 
            Objects.equals(evaluatedResource, other.evaluatedResource);
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
                type, 
                dataUpdateType, 
                measure, 
                subject, 
                date, 
                reporter, 
                reportingVendor, 
                location, 
                period, 
                inputParameters, 
                scoring, 
                improvementNotation, 
                group, 
                supplementalData, 
                evaluatedResource);
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
        private MeasureReportStatus status;
        private MeasureReportType type;
        private SubmitDataUpdateType dataUpdateType;
        private Canonical measure;
        private Reference subject;
        private DateTime date;
        private Reference reporter;
        private Reference reportingVendor;
        private Reference location;
        private Period period;
        private Reference inputParameters;
        private CodeableConcept scoring;
        private CodeableConcept improvementNotation;
        private List<Group> group = new ArrayList<>();
        private List<Reference> supplementalData = new ArrayList<>();
        private List<Reference> evaluatedResource = new ArrayList<>();

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
         * A formal identifier that is used to identify this MeasureReport when it is represented in other formats or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the MeasureReport
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
         * A formal identifier that is used to identify this MeasureReport when it is represented in other formats or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the MeasureReport
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
         * The MeasureReport status. No data will be available until the MeasureReport status is complete.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     complete | pending | error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(MeasureReportStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The type of measure report. This may be an individual report, which provides the score for the measure for an 
         * individual member of the population; a subject-listing, which returns the list of members that meet the various 
         * criteria in the measure; a summary report, which returns a population count for each of the criteria in the measure; 
         * or a data-collection, which enables the MeasureReport to be used to exchange the data-of-interest for a quality 
         * measure.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     individual | subject-list | summary | data-exchange
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(MeasureReportType type) {
            this.type = type;
            return this;
        }

        /**
         * Indicates whether the data submitted in a data-exchange report represents a snapshot or incremental update. A snapshot 
         * update replaces all previously submitted data for the receiver, whereas an incremental update represents only updated 
         * and/or changed data and should be applied as a differential update to the existing submitted data for the receiver.
         * 
         * @param dataUpdateType
         *     incremental | snapshot
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dataUpdateType(SubmitDataUpdateType dataUpdateType) {
            this.dataUpdateType = dataUpdateType;
            return this;
        }

        /**
         * A reference to the Measure that was calculated to produce this report.
         * 
         * @param measure
         *     What measure was calculated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder measure(Canonical measure) {
            this.measure = measure;
            return this;
        }

        /**
         * Optional subject identifying the individual or individuals the report is for.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link CareTeam}</li>
         * <li>{@link Device}</li>
         * <li>{@link Group}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Location}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link RelatedPerson}</li>
         * </ul>
         * 
         * @param subject
         *     What individual(s) the report is for
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The date this measure was calculated.
         * 
         * @param date
         *     When the measure was calculated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(DateTime date) {
            this.date = date;
            return this;
        }

        /**
         * The individual or organization that is reporting the data.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param reporter
         *     Who is reporting the data
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reporter(Reference reporter) {
            this.reporter = reporter;
            return this;
        }

        /**
         * A reference to the vendor who queried the data, calculated results and/or generated the report. The ‘reporting 
         * vendor’ is intended to represent the submitting entity when it is not the same as the reporting entity. This 
         * extension is used when the Receiver is interested in getting vendor information in the report.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param reportingVendor
         *     What vendor prepared the data
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reportingVendor(Reference reportingVendor) {
            this.reportingVendor = reportingVendor;
            return this;
        }

        /**
         * A reference to the location for which the data is being reported.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where the reported data is from
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * The reporting period for which the report was calculated.
         * 
         * <p>This element is required.
         * 
         * @param period
         *     What period the report covers
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * A reference to a Parameters resource (typically represented using a contained resource) that represents any input 
         * parameters that were provided to the operation that generated the report.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Parameters}</li>
         * </ul>
         * 
         * @param inputParameters
         *     What parameters were provided to the report
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder inputParameters(Reference inputParameters) {
            this.inputParameters = inputParameters;
            return this;
        }

        /**
         * Indicates how the calculation is performed for the measure, including proportion, ratio, continuous-variable, and 
         * cohort. The value set is extensible, allowing additional measure scoring types to be represented. It is expected to be 
         * the same as the scoring element on the referenced Measure.
         * 
         * @param scoring
         *     What scoring method (e.g. proportion, ratio, continuous-variable)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder scoring(CodeableConcept scoring) {
            this.scoring = scoring;
            return this;
        }

        /**
         * Whether improvement in the measure is noted by an increase or decrease in the measure score.
         * 
         * @param improvementNotation
         *     increase | decrease
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder improvementNotation(CodeableConcept improvementNotation) {
            this.improvementNotation = improvementNotation;
            return this;
        }

        /**
         * The results of the calculation, one for each population group in the measure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param group
         *     Measure results for each group
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder group(Group... group) {
            for (Group value : group) {
                this.group.add(value);
            }
            return this;
        }

        /**
         * The results of the calculation, one for each population group in the measure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param group
         *     Measure results for each group
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder group(Collection<Group> group) {
            this.group = new ArrayList<>(group);
            return this;
        }

        /**
         * A reference to a Resource that represents additional information collected for the report. If the value of the 
         * supplemental data is not a Resource (i.e. evaluating the supplementalData expression for this case in the measure 
         * results in a value that is not a FHIR Resource), it is reported as a reference to a contained Observation resource.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supplementalData
         *     Additional information collected for the report
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supplementalData(Reference... supplementalData) {
            for (Reference value : supplementalData) {
                this.supplementalData.add(value);
            }
            return this;
        }

        /**
         * A reference to a Resource that represents additional information collected for the report. If the value of the 
         * supplemental data is not a Resource (i.e. evaluating the supplementalData expression for this case in the measure 
         * results in a value that is not a FHIR Resource), it is reported as a reference to a contained Observation resource.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supplementalData
         *     Additional information collected for the report
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supplementalData(Collection<Reference> supplementalData) {
            this.supplementalData = new ArrayList<>(supplementalData);
            return this;
        }

        /**
         * Evaluated resources are used to capture what data was involved in the calculation of a measure. This usage is only 
         * allowed for individual reports to ensure that the size of the MeasureReport resource is bounded.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param evaluatedResource
         *     What data was used to calculate the measure score
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder evaluatedResource(Reference... evaluatedResource) {
            for (Reference value : evaluatedResource) {
                this.evaluatedResource.add(value);
            }
            return this;
        }

        /**
         * Evaluated resources are used to capture what data was involved in the calculation of a measure. This usage is only 
         * allowed for individual reports to ensure that the size of the MeasureReport resource is bounded.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param evaluatedResource
         *     What data was used to calculate the measure score
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder evaluatedResource(Collection<Reference> evaluatedResource) {
            this.evaluatedResource = new ArrayList<>(evaluatedResource);
            return this;
        }

        /**
         * Build the {@link MeasureReport}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>type</li>
         * <li>period</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link MeasureReport}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid MeasureReport per the base specification
         */
        @Override
        public MeasureReport build() {
            MeasureReport measureReport = new MeasureReport(this);
            if (validating) {
                validate(measureReport);
            }
            return measureReport;
        }

        protected void validate(MeasureReport measureReport) {
            super.validate(measureReport);
            ValidationSupport.checkList(measureReport.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(measureReport.status, "status");
            ValidationSupport.requireNonNull(measureReport.type, "type");
            ValidationSupport.requireNonNull(measureReport.period, "period");
            ValidationSupport.checkList(measureReport.group, "group", Group.class);
            ValidationSupport.checkList(measureReport.supplementalData, "supplementalData", Reference.class);
            ValidationSupport.checkList(measureReport.evaluatedResource, "evaluatedResource", Reference.class);
            ValidationSupport.checkReferenceType(measureReport.subject, "subject", "CareTeam", "Device", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
            ValidationSupport.checkReferenceType(measureReport.reporter, "reporter", "Practitioner", "PractitionerRole", "Organization", "Group");
            ValidationSupport.checkReferenceType(measureReport.reportingVendor, "reportingVendor", "Organization");
            ValidationSupport.checkReferenceType(measureReport.location, "location", "Location");
            ValidationSupport.checkReferenceType(measureReport.inputParameters, "inputParameters", "Parameters");
        }

        protected Builder from(MeasureReport measureReport) {
            super.from(measureReport);
            identifier.addAll(measureReport.identifier);
            status = measureReport.status;
            type = measureReport.type;
            dataUpdateType = measureReport.dataUpdateType;
            measure = measureReport.measure;
            subject = measureReport.subject;
            date = measureReport.date;
            reporter = measureReport.reporter;
            reportingVendor = measureReport.reportingVendor;
            location = measureReport.location;
            period = measureReport.period;
            inputParameters = measureReport.inputParameters;
            scoring = measureReport.scoring;
            improvementNotation = measureReport.improvementNotation;
            group.addAll(measureReport.group);
            supplementalData.addAll(measureReport.supplementalData);
            evaluatedResource.addAll(measureReport.evaluatedResource);
            return this;
        }
    }

    /**
     * The results of the calculation, one for each population group in the measure.
     */
    public static class Group extends BackboneElement {
        private final String linkId;
        @Summary
        @Binding(
            bindingName = "MeasureGroupExample",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Example of measure groups.",
            valueSet = "http://hl7.org/fhir/ValueSet/measure-group-example"
        )
        private final CodeableConcept code;
        @Summary
        @ReferenceTarget({ "CareTeam", "Device", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
        private final Reference subject;
        private final List<Population> population;
        @Summary
        @Choice({ Quantity.class, DateTime.class, CodeableConcept.class, Period.class, Range.class, Duration.class })
        private final Element measureScore;
        private final List<Stratifier> stratifier;

        private Group(Builder builder) {
            super(builder);
            linkId = builder.linkId;
            code = builder.code;
            subject = builder.subject;
            population = Collections.unmodifiableList(builder.population);
            measureScore = builder.measureScore;
            stratifier = Collections.unmodifiableList(builder.stratifier);
        }

        /**
         * The group from the Measure that corresponds to this group in the MeasureReport resource.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getLinkId() {
            return linkId;
        }

        /**
         * The meaning of the population group as defined in the measure definition.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * Optional subject identifying the individual or individuals the report is for.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getSubject() {
            return subject;
        }

        /**
         * The populations that make up the population group, one for each type of population appropriate for the measure.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Population} that may be empty.
         */
        public List<Population> getPopulation() {
            return population;
        }

        /**
         * The measure score for this population group, calculated as appropriate for the measure type and scoring method, and 
         * based on the contents of the populations defined in the group.
         * 
         * @return
         *     An immutable object of type {@link Quantity}, {@link DateTime}, {@link CodeableConcept}, {@link Period}, {@link Range} 
         *     or {@link Duration} that may be null.
         */
        public Element getMeasureScore() {
            return measureScore;
        }

        /**
         * When a measure includes multiple stratifiers, there will be a stratifier group for each stratifier defined by the 
         * measure.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Stratifier} that may be empty.
         */
        public List<Stratifier> getStratifier() {
            return stratifier;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (linkId != null) || 
                (code != null) || 
                (subject != null) || 
                !population.isEmpty() || 
                (measureScore != null) || 
                !stratifier.isEmpty();
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
                    accept(linkId, "linkId", visitor);
                    accept(code, "code", visitor);
                    accept(subject, "subject", visitor);
                    accept(population, "population", visitor, Population.class);
                    accept(measureScore, "measureScore", visitor);
                    accept(stratifier, "stratifier", visitor, Stratifier.class);
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
            Group other = (Group) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(linkId, other.linkId) && 
                Objects.equals(code, other.code) && 
                Objects.equals(subject, other.subject) && 
                Objects.equals(population, other.population) && 
                Objects.equals(measureScore, other.measureScore) && 
                Objects.equals(stratifier, other.stratifier);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    linkId, 
                    code, 
                    subject, 
                    population, 
                    measureScore, 
                    stratifier);
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
            private String linkId;
            private CodeableConcept code;
            private Reference subject;
            private List<Population> population = new ArrayList<>();
            private Element measureScore;
            private List<Stratifier> stratifier = new ArrayList<>();

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
             * Convenience method for setting {@code linkId}.
             * 
             * @param linkId
             *     Pointer to specific group from Measure
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #linkId(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder linkId(java.lang.String linkId) {
                this.linkId = (linkId == null) ? null : String.of(linkId);
                return this;
            }

            /**
             * The group from the Measure that corresponds to this group in the MeasureReport resource.
             * 
             * @param linkId
             *     Pointer to specific group from Measure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder linkId(String linkId) {
                this.linkId = linkId;
                return this;
            }

            /**
             * The meaning of the population group as defined in the measure definition.
             * 
             * @param code
             *     Meaning of the group
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Optional subject identifying the individual or individuals the report is for.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link CareTeam}</li>
             * <li>{@link Device}</li>
             * <li>{@link Group}</li>
             * <li>{@link HealthcareService}</li>
             * <li>{@link Location}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Patient}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param subject
             *     What individual(s) the report is for
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder subject(Reference subject) {
                this.subject = subject;
                return this;
            }

            /**
             * The populations that make up the population group, one for each type of population appropriate for the measure.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param population
             *     The populations in the group
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder population(Population... population) {
                for (Population value : population) {
                    this.population.add(value);
                }
                return this;
            }

            /**
             * The populations that make up the population group, one for each type of population appropriate for the measure.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param population
             *     The populations in the group
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder population(Collection<Population> population) {
                this.population = new ArrayList<>(population);
                return this;
            }

            /**
             * The measure score for this population group, calculated as appropriate for the measure type and scoring method, and 
             * based on the contents of the populations defined in the group.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Quantity}</li>
             * <li>{@link DateTime}</li>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Period}</li>
             * <li>{@link Range}</li>
             * <li>{@link Duration}</li>
             * </ul>
             * 
             * @param measureScore
             *     What score this group achieved
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder measureScore(Element measureScore) {
                this.measureScore = measureScore;
                return this;
            }

            /**
             * When a measure includes multiple stratifiers, there will be a stratifier group for each stratifier defined by the 
             * measure.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param stratifier
             *     Stratification results
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder stratifier(Stratifier... stratifier) {
                for (Stratifier value : stratifier) {
                    this.stratifier.add(value);
                }
                return this;
            }

            /**
             * When a measure includes multiple stratifiers, there will be a stratifier group for each stratifier defined by the 
             * measure.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param stratifier
             *     Stratification results
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder stratifier(Collection<Stratifier> stratifier) {
                this.stratifier = new ArrayList<>(stratifier);
                return this;
            }

            /**
             * Build the {@link Group}
             * 
             * @return
             *     An immutable object of type {@link Group}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Group per the base specification
             */
            @Override
            public Group build() {
                Group group = new Group(this);
                if (validating) {
                    validate(group);
                }
                return group;
            }

            protected void validate(Group group) {
                super.validate(group);
                ValidationSupport.checkList(group.population, "population", Population.class);
                ValidationSupport.choiceElement(group.measureScore, "measureScore", Quantity.class, DateTime.class, CodeableConcept.class, Period.class, Range.class, Duration.class);
                ValidationSupport.checkList(group.stratifier, "stratifier", Stratifier.class);
                ValidationSupport.checkReferenceType(group.subject, "subject", "CareTeam", "Device", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
                ValidationSupport.requireValueOrChildren(group);
            }

            protected Builder from(Group group) {
                super.from(group);
                linkId = group.linkId;
                code = group.code;
                subject = group.subject;
                population.addAll(group.population);
                measureScore = group.measureScore;
                stratifier.addAll(group.stratifier);
                return this;
            }
        }

        /**
         * The populations that make up the population group, one for each type of population appropriate for the measure.
         */
        public static class Population extends BackboneElement {
            private final String linkId;
            @Summary
            @Binding(
                bindingName = "MeasurePopulation",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "The type of population (e.g. initial, numerator, denominator, etc.).",
                valueSet = "http://hl7.org/fhir/ValueSet/measure-population"
            )
            private final CodeableConcept code;
            private final Integer count;
            @ReferenceTarget({ "List" })
            private final Reference subjectResults;
            @ReferenceTarget({ "MeasureReport" })
            private final List<Reference> subjectReport;
            @ReferenceTarget({ "Group" })
            private final Reference subjects;

            private Population(Builder builder) {
                super(builder);
                linkId = builder.linkId;
                code = builder.code;
                count = builder.count;
                subjectResults = builder.subjectResults;
                subjectReport = Collections.unmodifiableList(builder.subjectReport);
                subjects = builder.subjects;
            }

            /**
             * The population from the Measure that corresponds to this population in the MeasureReport resource.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getLinkId() {
                return linkId;
            }

            /**
             * The type of the population.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCode() {
                return code;
            }

            /**
             * The number of members of the population.
             * 
             * @return
             *     An immutable object of type {@link Integer} that may be null.
             */
            public Integer getCount() {
                return count;
            }

            /**
             * This element refers to a List of individual level MeasureReport resources, one for each subject in this population.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getSubjectResults() {
                return subjectResults;
            }

            /**
             * A reference to an individual level MeasureReport resource for a member of the population.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
             */
            public List<Reference> getSubjectReport() {
                return subjectReport;
            }

            /**
             * Optional Group identifying the individuals that make up the population.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getSubjects() {
                return subjects;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (linkId != null) || 
                    (code != null) || 
                    (count != null) || 
                    (subjectResults != null) || 
                    !subjectReport.isEmpty() || 
                    (subjects != null);
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
                        accept(linkId, "linkId", visitor);
                        accept(code, "code", visitor);
                        accept(count, "count", visitor);
                        accept(subjectResults, "subjectResults", visitor);
                        accept(subjectReport, "subjectReport", visitor, Reference.class);
                        accept(subjects, "subjects", visitor);
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
                Population other = (Population) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(linkId, other.linkId) && 
                    Objects.equals(code, other.code) && 
                    Objects.equals(count, other.count) && 
                    Objects.equals(subjectResults, other.subjectResults) && 
                    Objects.equals(subjectReport, other.subjectReport) && 
                    Objects.equals(subjects, other.subjects);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        linkId, 
                        code, 
                        count, 
                        subjectResults, 
                        subjectReport, 
                        subjects);
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
                private String linkId;
                private CodeableConcept code;
                private Integer count;
                private Reference subjectResults;
                private List<Reference> subjectReport = new ArrayList<>();
                private Reference subjects;

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
                 * Convenience method for setting {@code linkId}.
                 * 
                 * @param linkId
                 *     Pointer to specific population from Measure
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #linkId(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder linkId(java.lang.String linkId) {
                    this.linkId = (linkId == null) ? null : String.of(linkId);
                    return this;
                }

                /**
                 * The population from the Measure that corresponds to this population in the MeasureReport resource.
                 * 
                 * @param linkId
                 *     Pointer to specific population from Measure
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder linkId(String linkId) {
                    this.linkId = linkId;
                    return this;
                }

                /**
                 * The type of the population.
                 * 
                 * @param code
                 *     initial-population | numerator | numerator-exclusion | denominator | denominator-exclusion | denominator-exception | 
                 *     measure-population | measure-population-exclusion | measure-observation
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(CodeableConcept code) {
                    this.code = code;
                    return this;
                }

                /**
                 * Convenience method for setting {@code count}.
                 * 
                 * @param count
                 *     Size of the population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #count(org.linuxforhealth.fhir.model.type.Integer)
                 */
                public Builder count(java.lang.Integer count) {
                    this.count = (count == null) ? null : Integer.of(count);
                    return this;
                }

                /**
                 * The number of members of the population.
                 * 
                 * @param count
                 *     Size of the population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder count(Integer count) {
                    this.count = count;
                    return this;
                }

                /**
                 * This element refers to a List of individual level MeasureReport resources, one for each subject in this population.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link List}</li>
                 * </ul>
                 * 
                 * @param subjectResults
                 *     For subject-list reports, the subject results in this population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder subjectResults(Reference subjectResults) {
                    this.subjectResults = subjectResults;
                    return this;
                }

                /**
                 * A reference to an individual level MeasureReport resource for a member of the population.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
                 * <ul>
                 * <li>{@link MeasureReport}</li>
                 * </ul>
                 * 
                 * @param subjectReport
                 *     For subject-list reports, a subject result in this population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder subjectReport(Reference... subjectReport) {
                    for (Reference value : subjectReport) {
                        this.subjectReport.add(value);
                    }
                    return this;
                }

                /**
                 * A reference to an individual level MeasureReport resource for a member of the population.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
                 * <ul>
                 * <li>{@link MeasureReport}</li>
                 * </ul>
                 * 
                 * @param subjectReport
                 *     For subject-list reports, a subject result in this population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder subjectReport(Collection<Reference> subjectReport) {
                    this.subjectReport = new ArrayList<>(subjectReport);
                    return this;
                }

                /**
                 * Optional Group identifying the individuals that make up the population.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Group}</li>
                 * </ul>
                 * 
                 * @param subjects
                 *     What individual(s) in the population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder subjects(Reference subjects) {
                    this.subjects = subjects;
                    return this;
                }

                /**
                 * Build the {@link Population}
                 * 
                 * @return
                 *     An immutable object of type {@link Population}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Population per the base specification
                 */
                @Override
                public Population build() {
                    Population population = new Population(this);
                    if (validating) {
                        validate(population);
                    }
                    return population;
                }

                protected void validate(Population population) {
                    super.validate(population);
                    ValidationSupport.checkList(population.subjectReport, "subjectReport", Reference.class);
                    ValidationSupport.checkReferenceType(population.subjectResults, "subjectResults", "List");
                    ValidationSupport.checkReferenceType(population.subjectReport, "subjectReport", "MeasureReport");
                    ValidationSupport.checkReferenceType(population.subjects, "subjects", "Group");
                    ValidationSupport.requireValueOrChildren(population);
                }

                protected Builder from(Population population) {
                    super.from(population);
                    linkId = population.linkId;
                    code = population.code;
                    count = population.count;
                    subjectResults = population.subjectResults;
                    subjectReport.addAll(population.subjectReport);
                    subjects = population.subjects;
                    return this;
                }
            }
        }

        /**
         * When a measure includes multiple stratifiers, there will be a stratifier group for each stratifier defined by the 
         * measure.
         */
        public static class Stratifier extends BackboneElement {
            private final String linkId;
            @Binding(
                bindingName = "MeasureStratifierExample",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Meaning of the stratifier.",
                valueSet = "http://hl7.org/fhir/ValueSet/measure-stratifier-example"
            )
            private final CodeableConcept code;
            private final List<Stratum> stratum;

            private Stratifier(Builder builder) {
                super(builder);
                linkId = builder.linkId;
                code = builder.code;
                stratum = Collections.unmodifiableList(builder.stratum);
            }

            /**
             * The stratifier from the Measure that corresponds to this stratifier in the MeasureReport resource.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getLinkId() {
                return linkId;
            }

            /**
             * The meaning of this stratifier, as defined in the measure definition.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCode() {
                return code;
            }

            /**
             * This element contains the results for a single stratum within the stratifier. For example, when stratifying on 
             * administrative gender, there will be four strata, one for each possible gender value.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Stratum} that may be empty.
             */
            public List<Stratum> getStratum() {
                return stratum;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (linkId != null) || 
                    (code != null) || 
                    !stratum.isEmpty();
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
                        accept(linkId, "linkId", visitor);
                        accept(code, "code", visitor);
                        accept(stratum, "stratum", visitor, Stratum.class);
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
                Stratifier other = (Stratifier) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(linkId, other.linkId) && 
                    Objects.equals(code, other.code) && 
                    Objects.equals(stratum, other.stratum);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        linkId, 
                        code, 
                        stratum);
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
                private String linkId;
                private CodeableConcept code;
                private List<Stratum> stratum = new ArrayList<>();

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
                 * Convenience method for setting {@code linkId}.
                 * 
                 * @param linkId
                 *     Pointer to specific stratifier from Measure
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #linkId(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder linkId(java.lang.String linkId) {
                    this.linkId = (linkId == null) ? null : String.of(linkId);
                    return this;
                }

                /**
                 * The stratifier from the Measure that corresponds to this stratifier in the MeasureReport resource.
                 * 
                 * @param linkId
                 *     Pointer to specific stratifier from Measure
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder linkId(String linkId) {
                    this.linkId = linkId;
                    return this;
                }

                /**
                 * The meaning of this stratifier, as defined in the measure definition.
                 * 
                 * @param code
                 *     What stratifier of the group
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(CodeableConcept code) {
                    this.code = code;
                    return this;
                }

                /**
                 * This element contains the results for a single stratum within the stratifier. For example, when stratifying on 
                 * administrative gender, there will be four strata, one for each possible gender value.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param stratum
                 *     Stratum results, one for each unique value, or set of values, in the stratifier, or stratifier components
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder stratum(Stratum... stratum) {
                    for (Stratum value : stratum) {
                        this.stratum.add(value);
                    }
                    return this;
                }

                /**
                 * This element contains the results for a single stratum within the stratifier. For example, when stratifying on 
                 * administrative gender, there will be four strata, one for each possible gender value.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param stratum
                 *     Stratum results, one for each unique value, or set of values, in the stratifier, or stratifier components
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder stratum(Collection<Stratum> stratum) {
                    this.stratum = new ArrayList<>(stratum);
                    return this;
                }

                /**
                 * Build the {@link Stratifier}
                 * 
                 * @return
                 *     An immutable object of type {@link Stratifier}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Stratifier per the base specification
                 */
                @Override
                public Stratifier build() {
                    Stratifier stratifier = new Stratifier(this);
                    if (validating) {
                        validate(stratifier);
                    }
                    return stratifier;
                }

                protected void validate(Stratifier stratifier) {
                    super.validate(stratifier);
                    ValidationSupport.checkList(stratifier.stratum, "stratum", Stratum.class);
                    ValidationSupport.requireValueOrChildren(stratifier);
                }

                protected Builder from(Stratifier stratifier) {
                    super.from(stratifier);
                    linkId = stratifier.linkId;
                    code = stratifier.code;
                    stratum.addAll(stratifier.stratum);
                    return this;
                }
            }

            /**
             * This element contains the results for a single stratum within the stratifier. For example, when stratifying on 
             * administrative gender, there will be four strata, one for each possible gender value.
             */
            public static class Stratum extends BackboneElement {
                @Choice({ CodeableConcept.class, Boolean.class, Quantity.class, Range.class, Reference.class })
                @Binding(
                    bindingName = "MeasureReportStratifierValueExample",
                    strength = BindingStrength.Value.EXAMPLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/measurereport-stratifier-value-example"
                )
                private final Element value;
                private final List<Component> component;
                private final List<Population> population;
                @Choice({ Quantity.class, DateTime.class, CodeableConcept.class, Period.class, Range.class, Duration.class })
                private final Element measureScore;

                private Stratum(Builder builder) {
                    super(builder);
                    value = builder.value;
                    component = Collections.unmodifiableList(builder.component);
                    population = Collections.unmodifiableList(builder.population);
                    measureScore = builder.measureScore;
                }

                /**
                 * The value for this stratum, expressed as a CodeableConcept. When defining stratifiers on complex values, the value 
                 * must be rendered such that the value for each stratum within the stratifier is unique.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept}, {@link Boolean}, {@link Quantity}, {@link Range} or {@link 
                 *     Reference} that may be null.
                 */
                public Element getValue() {
                    return value;
                }

                /**
                 * A stratifier component value.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Component} that may be empty.
                 */
                public List<Component> getComponent() {
                    return component;
                }

                /**
                 * The populations that make up the stratum, one for each type of population appropriate to the measure.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Population} that may be empty.
                 */
                public List<Population> getPopulation() {
                    return population;
                }

                /**
                 * The measure score for this stratum, calculated as appropriate for the measure type and scoring method, and based on 
                 * only the members of this stratum.
                 * 
                 * @return
                 *     An immutable object of type {@link Quantity}, {@link DateTime}, {@link CodeableConcept}, {@link Period}, {@link Range} 
                 *     or {@link Duration} that may be null.
                 */
                public Element getMeasureScore() {
                    return measureScore;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (value != null) || 
                        !component.isEmpty() || 
                        !population.isEmpty() || 
                        (measureScore != null);
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
                            accept(value, "value", visitor);
                            accept(component, "component", visitor, Component.class);
                            accept(population, "population", visitor, Population.class);
                            accept(measureScore, "measureScore", visitor);
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
                    Stratum other = (Stratum) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(value, other.value) && 
                        Objects.equals(component, other.component) && 
                        Objects.equals(population, other.population) && 
                        Objects.equals(measureScore, other.measureScore);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            value, 
                            component, 
                            population, 
                            measureScore);
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
                    private Element value;
                    private List<Component> component = new ArrayList<>();
                    private List<Population> population = new ArrayList<>();
                    private Element measureScore;

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
                     * Convenience method for setting {@code value} with choice type Boolean.
                     * 
                     * @param value
                     *     The stratum value, e.g. male
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
                     * The value for this stratum, expressed as a CodeableConcept. When defining stratifiers on complex values, the value 
                     * must be rendered such that the value for each stratum within the stratifier is unique.
                     * 
                     * <p>This is a choice element with the following allowed types:
                     * <ul>
                     * <li>{@link CodeableConcept}</li>
                     * <li>{@link Boolean}</li>
                     * <li>{@link Quantity}</li>
                     * <li>{@link Range}</li>
                     * <li>{@link Reference}</li>
                     * </ul>
                     * 
                     * @param value
                     *     The stratum value, e.g. male
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder value(Element value) {
                        this.value = value;
                        return this;
                    }

                    /**
                     * A stratifier component value.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param component
                     *     Stratifier component values
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder component(Component... component) {
                        for (Component value : component) {
                            this.component.add(value);
                        }
                        return this;
                    }

                    /**
                     * A stratifier component value.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param component
                     *     Stratifier component values
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder component(Collection<Component> component) {
                        this.component = new ArrayList<>(component);
                        return this;
                    }

                    /**
                     * The populations that make up the stratum, one for each type of population appropriate to the measure.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param population
                     *     Population results in this stratum
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder population(Population... population) {
                        for (Population value : population) {
                            this.population.add(value);
                        }
                        return this;
                    }

                    /**
                     * The populations that make up the stratum, one for each type of population appropriate to the measure.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param population
                     *     Population results in this stratum
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder population(Collection<Population> population) {
                        this.population = new ArrayList<>(population);
                        return this;
                    }

                    /**
                     * The measure score for this stratum, calculated as appropriate for the measure type and scoring method, and based on 
                     * only the members of this stratum.
                     * 
                     * <p>This is a choice element with the following allowed types:
                     * <ul>
                     * <li>{@link Quantity}</li>
                     * <li>{@link DateTime}</li>
                     * <li>{@link CodeableConcept}</li>
                     * <li>{@link Period}</li>
                     * <li>{@link Range}</li>
                     * <li>{@link Duration}</li>
                     * </ul>
                     * 
                     * @param measureScore
                     *     What score this stratum achieved
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder measureScore(Element measureScore) {
                        this.measureScore = measureScore;
                        return this;
                    }

                    /**
                     * Build the {@link Stratum}
                     * 
                     * @return
                     *     An immutable object of type {@link Stratum}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Stratum per the base specification
                     */
                    @Override
                    public Stratum build() {
                        Stratum stratum = new Stratum(this);
                        if (validating) {
                            validate(stratum);
                        }
                        return stratum;
                    }

                    protected void validate(Stratum stratum) {
                        super.validate(stratum);
                        ValidationSupport.choiceElement(stratum.value, "value", CodeableConcept.class, Boolean.class, Quantity.class, Range.class, Reference.class);
                        ValidationSupport.checkList(stratum.component, "component", Component.class);
                        ValidationSupport.checkList(stratum.population, "population", Population.class);
                        ValidationSupport.choiceElement(stratum.measureScore, "measureScore", Quantity.class, DateTime.class, CodeableConcept.class, Period.class, Range.class, Duration.class);
                        ValidationSupport.requireValueOrChildren(stratum);
                    }

                    protected Builder from(Stratum stratum) {
                        super.from(stratum);
                        value = stratum.value;
                        component.addAll(stratum.component);
                        population.addAll(stratum.population);
                        measureScore = stratum.measureScore;
                        return this;
                    }
                }

                /**
                 * A stratifier component value.
                 */
                public static class Component extends BackboneElement {
                    private final String linkId;
                    @Binding(
                        bindingName = "MeasureStratifierExample",
                        strength = BindingStrength.Value.EXAMPLE,
                        description = "Meaning of the stratifier.",
                        valueSet = "http://hl7.org/fhir/ValueSet/measure-stratifier-example"
                    )
                    @Required
                    private final CodeableConcept code;
                    @Choice({ CodeableConcept.class, Boolean.class, Quantity.class, Range.class, Reference.class })
                    @Binding(
                        bindingName = "MeasureReportStratifierValueExample",
                        strength = BindingStrength.Value.EXAMPLE,
                        valueSet = "http://hl7.org/fhir/ValueSet/measurereport-stratifier-value-example"
                    )
                    @Required
                    private final Element value;

                    private Component(Builder builder) {
                        super(builder);
                        linkId = builder.linkId;
                        code = builder.code;
                        value = builder.value;
                    }

                    /**
                     * The stratifier component from the Measure that corresponds to this stratifier component in the MeasureReport resource.
                     * 
                     * @return
                     *     An immutable object of type {@link String} that may be null.
                     */
                    public String getLinkId() {
                        return linkId;
                    }

                    /**
                     * The code for the stratum component value.
                     * 
                     * @return
                     *     An immutable object of type {@link CodeableConcept} that is non-null.
                     */
                    public CodeableConcept getCode() {
                        return code;
                    }

                    /**
                     * The stratum component value.
                     * 
                     * @return
                     *     An immutable object of type {@link CodeableConcept}, {@link Boolean}, {@link Quantity}, {@link Range} or {@link 
                     *     Reference} that is non-null.
                     */
                    public Element getValue() {
                        return value;
                    }

                    @Override
                    public boolean hasChildren() {
                        return super.hasChildren() || 
                            (linkId != null) || 
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
                                accept(linkId, "linkId", visitor);
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
                        Component other = (Component) obj;
                        return Objects.equals(id, other.id) && 
                            Objects.equals(extension, other.extension) && 
                            Objects.equals(modifierExtension, other.modifierExtension) && 
                            Objects.equals(linkId, other.linkId) && 
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
                                linkId, 
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
                        private String linkId;
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
                         * Convenience method for setting {@code linkId}.
                         * 
                         * @param linkId
                         *     Pointer to specific stratifier component from Measure
                         * 
                         * @return
                         *     A reference to this Builder instance
                         * 
                         * @see #linkId(org.linuxforhealth.fhir.model.type.String)
                         */
                        public Builder linkId(java.lang.String linkId) {
                            this.linkId = (linkId == null) ? null : String.of(linkId);
                            return this;
                        }

                        /**
                         * The stratifier component from the Measure that corresponds to this stratifier component in the MeasureReport resource.
                         * 
                         * @param linkId
                         *     Pointer to specific stratifier component from Measure
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder linkId(String linkId) {
                            this.linkId = linkId;
                            return this;
                        }

                        /**
                         * The code for the stratum component value.
                         * 
                         * <p>This element is required.
                         * 
                         * @param code
                         *     What stratifier component of the group
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
                         * <p>This element is required.
                         * 
                         * @param value
                         *     The stratum component value, e.g. male
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
                         * The stratum component value.
                         * 
                         * <p>This element is required.
                         * 
                         * <p>This is a choice element with the following allowed types:
                         * <ul>
                         * <li>{@link CodeableConcept}</li>
                         * <li>{@link Boolean}</li>
                         * <li>{@link Quantity}</li>
                         * <li>{@link Range}</li>
                         * <li>{@link Reference}</li>
                         * </ul>
                         * 
                         * @param value
                         *     The stratum component value, e.g. male
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder value(Element value) {
                            this.value = value;
                            return this;
                        }

                        /**
                         * Build the {@link Component}
                         * 
                         * <p>Required elements:
                         * <ul>
                         * <li>code</li>
                         * <li>value</li>
                         * </ul>
                         * 
                         * @return
                         *     An immutable object of type {@link Component}
                         * @throws IllegalStateException
                         *     if the current state cannot be built into a valid Component per the base specification
                         */
                        @Override
                        public Component build() {
                            Component component = new Component(this);
                            if (validating) {
                                validate(component);
                            }
                            return component;
                        }

                        protected void validate(Component component) {
                            super.validate(component);
                            ValidationSupport.requireNonNull(component.code, "code");
                            ValidationSupport.requireChoiceElement(component.value, "value", CodeableConcept.class, Boolean.class, Quantity.class, Range.class, Reference.class);
                            ValidationSupport.requireValueOrChildren(component);
                        }

                        protected Builder from(Component component) {
                            super.from(component);
                            linkId = component.linkId;
                            code = component.code;
                            value = component.value;
                            return this;
                        }
                    }
                }

                /**
                 * The populations that make up the stratum, one for each type of population appropriate to the measure.
                 */
                public static class Population extends BackboneElement {
                    private final String linkId;
                    @Binding(
                        bindingName = "MeasurePopulation",
                        strength = BindingStrength.Value.EXTENSIBLE,
                        description = "The type of population (e.g. initial, numerator, denominator, etc.).",
                        valueSet = "http://hl7.org/fhir/ValueSet/measure-population"
                    )
                    private final CodeableConcept code;
                    private final Integer count;
                    @ReferenceTarget({ "List" })
                    private final Reference subjectResults;
                    @ReferenceTarget({ "MeasureReport" })
                    private final List<Reference> subjectReport;
                    @ReferenceTarget({ "Group" })
                    private final Reference subjects;

                    private Population(Builder builder) {
                        super(builder);
                        linkId = builder.linkId;
                        code = builder.code;
                        count = builder.count;
                        subjectResults = builder.subjectResults;
                        subjectReport = Collections.unmodifiableList(builder.subjectReport);
                        subjects = builder.subjects;
                    }

                    /**
                     * The population from the Measure that corresponds to this population in the MeasureReport resource.
                     * 
                     * @return
                     *     An immutable object of type {@link String} that may be null.
                     */
                    public String getLinkId() {
                        return linkId;
                    }

                    /**
                     * The type of the population.
                     * 
                     * @return
                     *     An immutable object of type {@link CodeableConcept} that may be null.
                     */
                    public CodeableConcept getCode() {
                        return code;
                    }

                    /**
                     * The number of members of the population in this stratum.
                     * 
                     * @return
                     *     An immutable object of type {@link Integer} that may be null.
                     */
                    public Integer getCount() {
                        return count;
                    }

                    /**
                     * This element refers to a List of individual level MeasureReport resources, one for each subject in this population in 
                     * this stratum.
                     * 
                     * @return
                     *     An immutable object of type {@link Reference} that may be null.
                     */
                    public Reference getSubjectResults() {
                        return subjectResults;
                    }

                    /**
                     * A reference to an individual level MeasureReport resource for a member of the population.
                     * 
                     * @return
                     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
                     */
                    public List<Reference> getSubjectReport() {
                        return subjectReport;
                    }

                    /**
                     * Optional Group identifying the individuals that make up the population.
                     * 
                     * @return
                     *     An immutable object of type {@link Reference} that may be null.
                     */
                    public Reference getSubjects() {
                        return subjects;
                    }

                    @Override
                    public boolean hasChildren() {
                        return super.hasChildren() || 
                            (linkId != null) || 
                            (code != null) || 
                            (count != null) || 
                            (subjectResults != null) || 
                            !subjectReport.isEmpty() || 
                            (subjects != null);
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
                                accept(linkId, "linkId", visitor);
                                accept(code, "code", visitor);
                                accept(count, "count", visitor);
                                accept(subjectResults, "subjectResults", visitor);
                                accept(subjectReport, "subjectReport", visitor, Reference.class);
                                accept(subjects, "subjects", visitor);
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
                        Population other = (Population) obj;
                        return Objects.equals(id, other.id) && 
                            Objects.equals(extension, other.extension) && 
                            Objects.equals(modifierExtension, other.modifierExtension) && 
                            Objects.equals(linkId, other.linkId) && 
                            Objects.equals(code, other.code) && 
                            Objects.equals(count, other.count) && 
                            Objects.equals(subjectResults, other.subjectResults) && 
                            Objects.equals(subjectReport, other.subjectReport) && 
                            Objects.equals(subjects, other.subjects);
                    }

                    @Override
                    public int hashCode() {
                        int result = hashCode;
                        if (result == 0) {
                            result = Objects.hash(id, 
                                extension, 
                                modifierExtension, 
                                linkId, 
                                code, 
                                count, 
                                subjectResults, 
                                subjectReport, 
                                subjects);
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
                        private String linkId;
                        private CodeableConcept code;
                        private Integer count;
                        private Reference subjectResults;
                        private List<Reference> subjectReport = new ArrayList<>();
                        private Reference subjects;

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
                         * Convenience method for setting {@code linkId}.
                         * 
                         * @param linkId
                         *     Pointer to specific population from Measure
                         * 
                         * @return
                         *     A reference to this Builder instance
                         * 
                         * @see #linkId(org.linuxforhealth.fhir.model.type.String)
                         */
                        public Builder linkId(java.lang.String linkId) {
                            this.linkId = (linkId == null) ? null : String.of(linkId);
                            return this;
                        }

                        /**
                         * The population from the Measure that corresponds to this population in the MeasureReport resource.
                         * 
                         * @param linkId
                         *     Pointer to specific population from Measure
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder linkId(String linkId) {
                            this.linkId = linkId;
                            return this;
                        }

                        /**
                         * The type of the population.
                         * 
                         * @param code
                         *     initial-population | numerator | numerator-exclusion | denominator | denominator-exclusion | denominator-exception | 
                         *     measure-population | measure-population-exclusion | measure-observation
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder code(CodeableConcept code) {
                            this.code = code;
                            return this;
                        }

                        /**
                         * Convenience method for setting {@code count}.
                         * 
                         * @param count
                         *     Size of the population
                         * 
                         * @return
                         *     A reference to this Builder instance
                         * 
                         * @see #count(org.linuxforhealth.fhir.model.type.Integer)
                         */
                        public Builder count(java.lang.Integer count) {
                            this.count = (count == null) ? null : Integer.of(count);
                            return this;
                        }

                        /**
                         * The number of members of the population in this stratum.
                         * 
                         * @param count
                         *     Size of the population
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder count(Integer count) {
                            this.count = count;
                            return this;
                        }

                        /**
                         * This element refers to a List of individual level MeasureReport resources, one for each subject in this population in 
                         * this stratum.
                         * 
                         * <p>Allowed resource types for this reference:
                         * <ul>
                         * <li>{@link List}</li>
                         * </ul>
                         * 
                         * @param subjectResults
                         *     For subject-list reports, the subject results in this population
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder subjectResults(Reference subjectResults) {
                            this.subjectResults = subjectResults;
                            return this;
                        }

                        /**
                         * A reference to an individual level MeasureReport resource for a member of the population.
                         * 
                         * <p>Adds new element(s) to the existing list.
                         * If any of the elements are null, calling {@link #build()} will fail.
                         * 
                         * <p>Allowed resource types for the references:
                         * <ul>
                         * <li>{@link MeasureReport}</li>
                         * </ul>
                         * 
                         * @param subjectReport
                         *     For subject-list reports, a subject result in this population
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder subjectReport(Reference... subjectReport) {
                            for (Reference value : subjectReport) {
                                this.subjectReport.add(value);
                            }
                            return this;
                        }

                        /**
                         * A reference to an individual level MeasureReport resource for a member of the population.
                         * 
                         * <p>Replaces the existing list with a new one containing elements from the Collection.
                         * If any of the elements are null, calling {@link #build()} will fail.
                         * 
                         * <p>Allowed resource types for the references:
                         * <ul>
                         * <li>{@link MeasureReport}</li>
                         * </ul>
                         * 
                         * @param subjectReport
                         *     For subject-list reports, a subject result in this population
                         * 
                         * @return
                         *     A reference to this Builder instance
                         * 
                         * @throws NullPointerException
                         *     If the passed collection is null
                         */
                        public Builder subjectReport(Collection<Reference> subjectReport) {
                            this.subjectReport = new ArrayList<>(subjectReport);
                            return this;
                        }

                        /**
                         * Optional Group identifying the individuals that make up the population.
                         * 
                         * <p>Allowed resource types for this reference:
                         * <ul>
                         * <li>{@link Group}</li>
                         * </ul>
                         * 
                         * @param subjects
                         *     What individual(s) in the population
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder subjects(Reference subjects) {
                            this.subjects = subjects;
                            return this;
                        }

                        /**
                         * Build the {@link Population}
                         * 
                         * @return
                         *     An immutable object of type {@link Population}
                         * @throws IllegalStateException
                         *     if the current state cannot be built into a valid Population per the base specification
                         */
                        @Override
                        public Population build() {
                            Population population = new Population(this);
                            if (validating) {
                                validate(population);
                            }
                            return population;
                        }

                        protected void validate(Population population) {
                            super.validate(population);
                            ValidationSupport.checkList(population.subjectReport, "subjectReport", Reference.class);
                            ValidationSupport.checkReferenceType(population.subjectResults, "subjectResults", "List");
                            ValidationSupport.checkReferenceType(population.subjectReport, "subjectReport", "MeasureReport");
                            ValidationSupport.checkReferenceType(population.subjects, "subjects", "Group");
                            ValidationSupport.requireValueOrChildren(population);
                        }

                        protected Builder from(Population population) {
                            super.from(population);
                            linkId = population.linkId;
                            code = population.code;
                            count = population.count;
                            subjectResults = population.subjectResults;
                            subjectReport.addAll(population.subjectReport);
                            subjects = population.subjects;
                            return this;
                        }
                    }
                }
            }
        }
    }
}
