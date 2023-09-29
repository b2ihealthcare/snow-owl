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
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.ContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Expression;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BasisType;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The Measure resource provides the definition of a quality measure.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "cnl-0",
    level = "Warning",
    location = "(base)",
    description = "Name should be usable as an identifier for the module by machine processing applications such as code generation",
    expression = "name.exists() implies name.matches('^[A-Z]([A-Za-z0-9_]){1,254}$')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "mea-1",
    level = "Rule",
    location = "(base)",
    description = "Stratifier SHALL be either a single criteria or a set of criteria components",
    expression = "group.stratifier.all((code | description | criteria).exists() xor component.exists())",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "Measure.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "mea-2",
    level = "Warning",
    location = "Measure.group.linkId",
    description = "Link ids should be 255 characters or less",
    expression = "$this.length() <= 255",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "mea-3",
    level = "Warning",
    location = "Measure.group.population.linkId",
    description = "Link ids should be 255 characters or less",
    expression = "$this.length() <= 255",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "mea-4",
    level = "Warning",
    location = "Measure.group.stratifier.linkId",
    description = "Link ids should be 255 characters or less",
    expression = "$this.length() <= 255",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "mea-5",
    level = "Warning",
    location = "Measure.group.stratifier.component.linkId",
    description = "Link ids should be 255 characters or less",
    expression = "$this.length() <= 255",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "mea-6",
    level = "Warning",
    location = "Measure.supplementalData.linkId",
    description = "Link ids should be 255 characters or less",
    expression = "$this.length() <= 255",
    source = "http://hl7.org/fhir/StructureDefinition/Measure"
)
@Constraint(
    id = "measure-7",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-8",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/participant-resource-types",
    expression = "subject.as(CodeableConcept).exists() implies (subject.as(CodeableConcept).memberOf('http://hl7.org/fhir/ValueSet/participant-resource-types', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-9",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-10",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/measure-scoring",
    expression = "scoring.exists() implies (scoring.memberOf('http://terminology.hl7.org/ValueSet/measure-scoring', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-11",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/composite-measure-scoring",
    expression = "compositeScoring.exists() implies (compositeScoring.memberOf('http://hl7.org/fhir/ValueSet/composite-measure-scoring', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-12",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/measure-type",
    expression = "type.exists() implies (type.all(memberOf('http://hl7.org/fhir/ValueSet/measure-type', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-13",
    level = "Warning",
    location = "group.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/measure-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/measure-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-14",
    level = "Warning",
    location = "group.subject",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/participant-resource-types",
    expression = "$this.as(CodeableConcept).memberOf('http://hl7.org/fhir/ValueSet/participant-resource-types', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-15",
    level = "Warning",
    location = "group.scoring",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/measure-scoring",
    expression = "$this.memberOf('http://terminology.hl7.org/ValueSet/measure-scoring', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-16",
    level = "Warning",
    location = "group.population.code",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/measure-population",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/measure-population', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-17",
    level = "Warning",
    location = "group.population.aggregateMethod",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/measure-aggregate-method",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/measure-aggregate-method', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Constraint(
    id = "measure-18",
    level = "Warning",
    location = "supplementalData.usage",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/measure-data-usage",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/measure-data-usage', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Measure",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Measure extends DomainResource {
    @Summary
    private final Uri url;
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final String version;
    @Summary
    @Choice({ String.class, Coding.class })
    @Binding(
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/version-algorithm"
    )
    private final Element versionAlgorithm;
    @Summary
    private final String name;
    @Summary
    private final String title;
    private final String subtitle;
    @Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The lifecycle status of an artifact.",
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    @Required
    private final PublicationStatus status;
    @Summary
    private final Boolean experimental;
    @ReferenceTarget({ "Group" })
    @Choice({ CodeableConcept.class, Reference.class })
    @Binding(
        bindingName = "SubjectType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The possible types of subjects for a measure (E.g. Patient, Practitioner, Organization, Location, etc.).",
        valueSet = "http://hl7.org/fhir/ValueSet/participant-resource-types"
    )
    private final Element subject;
    @Summary
    @Binding(
        bindingName = "BasisType",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/fhir-types|5.0.0"
    )
    private final BasisType basis;
    @Summary
    private final DateTime date;
    @Summary
    private final String publisher;
    @Summary
    private final List<ContactDetail> contact;
    @Summary
    private final Markdown description;
    @Summary
    private final List<UsageContext> useContext;
    @Summary
    @Binding(
        bindingName = "Jurisdiction",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Countries and regions within which this artifact is targeted for use.",
        valueSet = "http://hl7.org/fhir/ValueSet/jurisdiction"
    )
    private final List<CodeableConcept> jurisdiction;
    private final Markdown purpose;
    private final Markdown usage;
    private final Markdown copyright;
    private final String copyrightLabel;
    private final Date approvalDate;
    private final Date lastReviewDate;
    @Summary
    private final Period effectivePeriod;
    @Binding(
        bindingName = "DefinitionTopic",
        strength = BindingStrength.Value.EXAMPLE,
        description = "High-level categorization of the definition, used for searching, sorting, and filtering.",
        valueSet = "http://hl7.org/fhir/ValueSet/definition-topic"
    )
    private final List<CodeableConcept> topic;
    private final List<ContactDetail> author;
    private final List<ContactDetail> editor;
    private final List<ContactDetail> reviewer;
    private final List<ContactDetail> endorser;
    private final List<RelatedArtifact> relatedArtifact;
    private final List<Canonical> library;
    @Summary
    private final Markdown disclaimer;
    @Summary
    @Binding(
        bindingName = "MeasureScoring",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The scoring type of the measure.",
        valueSet = "http://terminology.hl7.org/ValueSet/measure-scoring"
    )
    private final CodeableConcept scoring;
    @Summary
    @Binding(
        bindingName = "MeasureScoringUnit",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/measure-scoring-unit"
    )
    private final CodeableConcept scoringUnit;
    @Summary
    @Binding(
        bindingName = "CompositeMeasureScoring",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The composite scoring method of the measure.",
        valueSet = "http://hl7.org/fhir/ValueSet/composite-measure-scoring"
    )
    private final CodeableConcept compositeScoring;
    @Summary
    @Binding(
        bindingName = "MeasureType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The type of measure (includes codes from 2.16.840.1.113883.1.11.20368).",
        valueSet = "http://hl7.org/fhir/ValueSet/measure-type"
    )
    private final List<CodeableConcept> type;
    @Summary
    private final Markdown riskAdjustment;
    @Summary
    private final Markdown rateAggregation;
    @Summary
    private final Markdown rationale;
    @Summary
    private final Markdown clinicalRecommendationStatement;
    @Summary
    @Binding(
        bindingName = "MeasureImprovementNotation",
        strength = BindingStrength.Value.REQUIRED,
        description = "Observation values that indicate what change in a measurement value or score is indicative of an improvement in the measured item or scored issue.",
        valueSet = "http://hl7.org/fhir/ValueSet/measure-improvement-notation|5.0.0"
    )
    private final CodeableConcept improvementNotation;
    private final List<Term> term;
    @Summary
    private final Markdown guidance;
    private final List<Group> group;
    private final List<SupplementalData> supplementalData;

    private Measure(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        versionAlgorithm = builder.versionAlgorithm;
        name = builder.name;
        title = builder.title;
        subtitle = builder.subtitle;
        status = builder.status;
        experimental = builder.experimental;
        subject = builder.subject;
        basis = builder.basis;
        date = builder.date;
        publisher = builder.publisher;
        contact = Collections.unmodifiableList(builder.contact);
        description = builder.description;
        useContext = Collections.unmodifiableList(builder.useContext);
        jurisdiction = Collections.unmodifiableList(builder.jurisdiction);
        purpose = builder.purpose;
        usage = builder.usage;
        copyright = builder.copyright;
        copyrightLabel = builder.copyrightLabel;
        approvalDate = builder.approvalDate;
        lastReviewDate = builder.lastReviewDate;
        effectivePeriod = builder.effectivePeriod;
        topic = Collections.unmodifiableList(builder.topic);
        author = Collections.unmodifiableList(builder.author);
        editor = Collections.unmodifiableList(builder.editor);
        reviewer = Collections.unmodifiableList(builder.reviewer);
        endorser = Collections.unmodifiableList(builder.endorser);
        relatedArtifact = Collections.unmodifiableList(builder.relatedArtifact);
        library = Collections.unmodifiableList(builder.library);
        disclaimer = builder.disclaimer;
        scoring = builder.scoring;
        scoringUnit = builder.scoringUnit;
        compositeScoring = builder.compositeScoring;
        type = Collections.unmodifiableList(builder.type);
        riskAdjustment = builder.riskAdjustment;
        rateAggregation = builder.rateAggregation;
        rationale = builder.rationale;
        clinicalRecommendationStatement = builder.clinicalRecommendationStatement;
        improvementNotation = builder.improvementNotation;
        term = Collections.unmodifiableList(builder.term);
        guidance = builder.guidance;
        group = Collections.unmodifiableList(builder.group);
        supplementalData = Collections.unmodifiableList(builder.supplementalData);
    }

    /**
     * An absolute URI that is used to identify this measure when it is referenced in a specification, model, design or an 
     * instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
     * which an authoritative instance of this measure is (or will be) published. This URL can be the target of a canonical 
     * reference. It SHALL remain the same when the measure is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this measure when it is represented in other formats, or referenced in a 
     * specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the measure when it is referenced in a specification, model, 
     * design or instance. This is an arbitrary value managed by the measure author and is not expected to be globally 
     * unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is also no 
     * expectation that versions can be placed in a lexicographical sequence. To provide a version consistent with the 
     * Decision Support Service specification, use the format Major.Minor.Revision (e.g. 1.0.0). For more information on 
     * versioning knowledge assets, refer to the Decision Support Service specification. Note that a version is required for 
     * non-experimental active artifacts.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Indicates the mechanism used to compare versions to determine which is more current.
     * 
     * @return
     *     An immutable object of type {@link String} or {@link Coding} that may be null.
     */
    public Element getVersionAlgorithm() {
        return versionAlgorithm;
    }

    /**
     * A natural language name identifying the measure. This name should be usable as an identifier for the module by machine 
     * processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the measure.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * An explanatory or alternate title for the measure giving additional information about its content.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * The status of this measure. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this measure is authored for testing purposes (or education/evaluation/marketing) and 
     * is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The intended subjects for the measure. If this element is not provided, a Patient subject is assumed, but the subject 
     * of the measure can be anything.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} or {@link Reference} that may be null.
     */
    public Element getSubject() {
        return subject;
    }

    /**
     * The population basis specifies the type of elements in the population. For a subject-based measure, this is boolean 
     * (because the subject and the population basis are the same, and the population criteria define yes/no values for each 
     * individual in the population). For measures that have a population basis that is different than the subject, this 
     * element specifies the type of the population basis. For example, an encounter-based measure has a subject of Patient 
     * and a population basis of Encounter, and the population criteria all return lists of Encounters.
     * 
     * @return
     *     An immutable object of type {@link BasisType} that may be null.
     */
    public BasisType getBasis() {
        return basis;
    }

    /**
     * The date (and optionally time) when the measure was last significantly changed. The date must change when the business 
     * version changes and it must change if the status code changes. In addition, it should change when the substantive 
     * content of the measure changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual responsible for the release and ongoing maintenance of the measure.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Contact details to assist a user in finding and communicating with the publisher.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getContact() {
        return contact;
    }

    /**
     * A free text natural language description of the measure from a consumer's perspective.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
     * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
     * may be used to assist with indexing and searching for appropriate measure instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A legal or geographic region in which the measure is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explanation of why this measure is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * A detailed description, from a clinical perspective, of how the measure is used.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getUsage() {
        return usage;
    }

    /**
     * A copyright statement relating to the measure and/or its contents. Copyright statements are generally legal 
     * restrictions on the use and publishing of the measure.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getCopyright() {
        return copyright;
    }

    /**
     * A short string (&lt;50 characters), suitable for inclusion in a page footer that identifies the copyright holder, 
     * effective period, and optionally whether rights are resctricted. (e.g. 'All rights reserved', 'Some rights reserved').
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getCopyrightLabel() {
        return copyrightLabel;
    }

    /**
     * The date on which the resource content was approved by the publisher. Approval happens once when the content is 
     * officially approved for usage.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getApprovalDate() {
        return approvalDate;
    }

    /**
     * The date on which the resource content was last reviewed. Review happens periodically after approval but does not 
     * change the original approval date.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getLastReviewDate() {
        return lastReviewDate;
    }

    /**
     * The period during which the measure content was or is planned to be in active use.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * Descriptive topics related to the content of the measure. Topics provide a high-level categorization grouping types of 
     * measures that can be useful for filtering and searching.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getTopic() {
        return topic;
    }

    /**
     * An individiual or organization primarily involved in the creation and maintenance of the content.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getAuthor() {
        return author;
    }

    /**
     * An individual or organization primarily responsible for internal coherence of the content.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getEditor() {
        return editor;
    }

    /**
     * An individual or organization asserted by the publisher to be primarily responsible for review of some aspect of the 
     * content.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getReviewer() {
        return reviewer;
    }

    /**
     * An individual or organization asserted by the publisher to be responsible for officially endorsing the content for use 
     * in some setting.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getEndorser() {
        return endorser;
    }

    /**
     * Related artifacts such as additional documentation, justification, or bibliographic references.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
     */
    public List<RelatedArtifact> getRelatedArtifact() {
        return relatedArtifact;
    }

    /**
     * A reference to a Library resource containing the formal logic used by the measure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getLibrary() {
        return library;
    }

    /**
     * Notices and disclaimers regarding the use of the measure or related to intellectual property (such as code systems) 
     * referenced by the measure.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDisclaimer() {
        return disclaimer;
    }

    /**
     * Indicates how the calculation is performed for the measure, including proportion, ratio, continuous-variable, and 
     * cohort. The value set is extensible, allowing additional measure scoring types to be represented.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getScoring() {
        return scoring;
    }

    /**
     * Defines the expected units of measure for the measure score. This element SHOULD be specified as a UCUM unit.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getScoringUnit() {
        return scoringUnit;
    }

    /**
     * If this is a composite measure, the scoring method used to combine the component measures to determine the composite 
     * score.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCompositeScoring() {
        return compositeScoring;
    }

    /**
     * Indicates whether the measure is used to examine a process, an outcome over time, a patient-reported outcome, or a 
     * structure measure such as utilization.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getType() {
        return type;
    }

    /**
     * A description of the risk adjustment factors that may impact the resulting score for the measure and how they may be 
     * accounted for when computing and reporting measure results.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getRiskAdjustment() {
        return riskAdjustment;
    }

    /**
     * Describes how to combine the information calculated, based on logic in each of several populations, into one 
     * summarized result.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getRateAggregation() {
        return rateAggregation;
    }

    /**
     * Provides a succinct statement of the need for the measure. Usually includes statements pertaining to importance 
     * criterion: impact, gap in care, and evidence.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getRationale() {
        return rationale;
    }

    /**
     * Provides a summary of relevant clinical guidelines or other clinical recommendations supporting the measure.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getClinicalRecommendationStatement() {
        return clinicalRecommendationStatement;
    }

    /**
     * Information on whether an increase or decrease in score is the preferred result (e.g., a higher score indicates better 
     * quality OR a lower score indicates better quality OR quality is within a range).
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getImprovementNotation() {
        return improvementNotation;
    }

    /**
     * Provides a description of an individual term used within the measure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Term} that may be empty.
     */
    public List<Term> getTerm() {
        return term;
    }

    /**
     * Additional guidance for the measure including how it can be used in a clinical context, and the intent of the measure.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getGuidance() {
        return guidance;
    }

    /**
     * A group of population criteria for the measure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Group} that may be empty.
     */
    public List<Group> getGroup() {
        return group;
    }

    /**
     * The supplemental data criteria for the measure report, specified as either the name of a valid CQL expression within a 
     * referenced library, or a valid FHIR Resource Path.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link SupplementalData} that may be empty.
     */
    public List<SupplementalData> getSupplementalData() {
        return supplementalData;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (url != null) || 
            !identifier.isEmpty() || 
            (version != null) || 
            (versionAlgorithm != null) || 
            (name != null) || 
            (title != null) || 
            (subtitle != null) || 
            (status != null) || 
            (experimental != null) || 
            (subject != null) || 
            (basis != null) || 
            (date != null) || 
            (publisher != null) || 
            !contact.isEmpty() || 
            (description != null) || 
            !useContext.isEmpty() || 
            !jurisdiction.isEmpty() || 
            (purpose != null) || 
            (usage != null) || 
            (copyright != null) || 
            (copyrightLabel != null) || 
            (approvalDate != null) || 
            (lastReviewDate != null) || 
            (effectivePeriod != null) || 
            !topic.isEmpty() || 
            !author.isEmpty() || 
            !editor.isEmpty() || 
            !reviewer.isEmpty() || 
            !endorser.isEmpty() || 
            !relatedArtifact.isEmpty() || 
            !library.isEmpty() || 
            (disclaimer != null) || 
            (scoring != null) || 
            (scoringUnit != null) || 
            (compositeScoring != null) || 
            !type.isEmpty() || 
            (riskAdjustment != null) || 
            (rateAggregation != null) || 
            (rationale != null) || 
            (clinicalRecommendationStatement != null) || 
            (improvementNotation != null) || 
            !term.isEmpty() || 
            (guidance != null) || 
            !group.isEmpty() || 
            !supplementalData.isEmpty();
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
                accept(url, "url", visitor);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(version, "version", visitor);
                accept(versionAlgorithm, "versionAlgorithm", visitor);
                accept(name, "name", visitor);
                accept(title, "title", visitor);
                accept(subtitle, "subtitle", visitor);
                accept(status, "status", visitor);
                accept(experimental, "experimental", visitor);
                accept(subject, "subject", visitor);
                accept(basis, "basis", visitor);
                accept(date, "date", visitor);
                accept(publisher, "publisher", visitor);
                accept(contact, "contact", visitor, ContactDetail.class);
                accept(description, "description", visitor);
                accept(useContext, "useContext", visitor, UsageContext.class);
                accept(jurisdiction, "jurisdiction", visitor, CodeableConcept.class);
                accept(purpose, "purpose", visitor);
                accept(usage, "usage", visitor);
                accept(copyright, "copyright", visitor);
                accept(copyrightLabel, "copyrightLabel", visitor);
                accept(approvalDate, "approvalDate", visitor);
                accept(lastReviewDate, "lastReviewDate", visitor);
                accept(effectivePeriod, "effectivePeriod", visitor);
                accept(topic, "topic", visitor, CodeableConcept.class);
                accept(author, "author", visitor, ContactDetail.class);
                accept(editor, "editor", visitor, ContactDetail.class);
                accept(reviewer, "reviewer", visitor, ContactDetail.class);
                accept(endorser, "endorser", visitor, ContactDetail.class);
                accept(relatedArtifact, "relatedArtifact", visitor, RelatedArtifact.class);
                accept(library, "library", visitor, Canonical.class);
                accept(disclaimer, "disclaimer", visitor);
                accept(scoring, "scoring", visitor);
                accept(scoringUnit, "scoringUnit", visitor);
                accept(compositeScoring, "compositeScoring", visitor);
                accept(type, "type", visitor, CodeableConcept.class);
                accept(riskAdjustment, "riskAdjustment", visitor);
                accept(rateAggregation, "rateAggregation", visitor);
                accept(rationale, "rationale", visitor);
                accept(clinicalRecommendationStatement, "clinicalRecommendationStatement", visitor);
                accept(improvementNotation, "improvementNotation", visitor);
                accept(term, "term", visitor, Term.class);
                accept(guidance, "guidance", visitor);
                accept(group, "group", visitor, Group.class);
                accept(supplementalData, "supplementalData", visitor, SupplementalData.class);
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
        Measure other = (Measure) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(url, other.url) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(version, other.version) && 
            Objects.equals(versionAlgorithm, other.versionAlgorithm) && 
            Objects.equals(name, other.name) && 
            Objects.equals(title, other.title) && 
            Objects.equals(subtitle, other.subtitle) && 
            Objects.equals(status, other.status) && 
            Objects.equals(experimental, other.experimental) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(basis, other.basis) && 
            Objects.equals(date, other.date) && 
            Objects.equals(publisher, other.publisher) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(description, other.description) && 
            Objects.equals(useContext, other.useContext) && 
            Objects.equals(jurisdiction, other.jurisdiction) && 
            Objects.equals(purpose, other.purpose) && 
            Objects.equals(usage, other.usage) && 
            Objects.equals(copyright, other.copyright) && 
            Objects.equals(copyrightLabel, other.copyrightLabel) && 
            Objects.equals(approvalDate, other.approvalDate) && 
            Objects.equals(lastReviewDate, other.lastReviewDate) && 
            Objects.equals(effectivePeriod, other.effectivePeriod) && 
            Objects.equals(topic, other.topic) && 
            Objects.equals(author, other.author) && 
            Objects.equals(editor, other.editor) && 
            Objects.equals(reviewer, other.reviewer) && 
            Objects.equals(endorser, other.endorser) && 
            Objects.equals(relatedArtifact, other.relatedArtifact) && 
            Objects.equals(library, other.library) && 
            Objects.equals(disclaimer, other.disclaimer) && 
            Objects.equals(scoring, other.scoring) && 
            Objects.equals(scoringUnit, other.scoringUnit) && 
            Objects.equals(compositeScoring, other.compositeScoring) && 
            Objects.equals(type, other.type) && 
            Objects.equals(riskAdjustment, other.riskAdjustment) && 
            Objects.equals(rateAggregation, other.rateAggregation) && 
            Objects.equals(rationale, other.rationale) && 
            Objects.equals(clinicalRecommendationStatement, other.clinicalRecommendationStatement) && 
            Objects.equals(improvementNotation, other.improvementNotation) && 
            Objects.equals(term, other.term) && 
            Objects.equals(guidance, other.guidance) && 
            Objects.equals(group, other.group) && 
            Objects.equals(supplementalData, other.supplementalData);
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
                url, 
                identifier, 
                version, 
                versionAlgorithm, 
                name, 
                title, 
                subtitle, 
                status, 
                experimental, 
                subject, 
                basis, 
                date, 
                publisher, 
                contact, 
                description, 
                useContext, 
                jurisdiction, 
                purpose, 
                usage, 
                copyright, 
                copyrightLabel, 
                approvalDate, 
                lastReviewDate, 
                effectivePeriod, 
                topic, 
                author, 
                editor, 
                reviewer, 
                endorser, 
                relatedArtifact, 
                library, 
                disclaimer, 
                scoring, 
                scoringUnit, 
                compositeScoring, 
                type, 
                riskAdjustment, 
                rateAggregation, 
                rationale, 
                clinicalRecommendationStatement, 
                improvementNotation, 
                term, 
                guidance, 
                group, 
                supplementalData);
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
        private Uri url;
        private List<Identifier> identifier = new ArrayList<>();
        private String version;
        private Element versionAlgorithm;
        private String name;
        private String title;
        private String subtitle;
        private PublicationStatus status;
        private Boolean experimental;
        private Element subject;
        private BasisType basis;
        private DateTime date;
        private String publisher;
        private List<ContactDetail> contact = new ArrayList<>();
        private Markdown description;
        private List<UsageContext> useContext = new ArrayList<>();
        private List<CodeableConcept> jurisdiction = new ArrayList<>();
        private Markdown purpose;
        private Markdown usage;
        private Markdown copyright;
        private String copyrightLabel;
        private Date approvalDate;
        private Date lastReviewDate;
        private Period effectivePeriod;
        private List<CodeableConcept> topic = new ArrayList<>();
        private List<ContactDetail> author = new ArrayList<>();
        private List<ContactDetail> editor = new ArrayList<>();
        private List<ContactDetail> reviewer = new ArrayList<>();
        private List<ContactDetail> endorser = new ArrayList<>();
        private List<RelatedArtifact> relatedArtifact = new ArrayList<>();
        private List<Canonical> library = new ArrayList<>();
        private Markdown disclaimer;
        private CodeableConcept scoring;
        private CodeableConcept scoringUnit;
        private CodeableConcept compositeScoring;
        private List<CodeableConcept> type = new ArrayList<>();
        private Markdown riskAdjustment;
        private Markdown rateAggregation;
        private Markdown rationale;
        private Markdown clinicalRecommendationStatement;
        private CodeableConcept improvementNotation;
        private List<Term> term = new ArrayList<>();
        private Markdown guidance;
        private List<Group> group = new ArrayList<>();
        private List<SupplementalData> supplementalData = new ArrayList<>();

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
         * An absolute URI that is used to identify this measure when it is referenced in a specification, model, design or an 
         * instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
         * which an authoritative instance of this measure is (or will be) published. This URL can be the target of a canonical 
         * reference. It SHALL remain the same when the measure is stored on different servers.
         * 
         * @param url
         *     Canonical identifier for this measure, represented as a URI (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A formal identifier that is used to identify this measure when it is represented in other formats, or referenced in a 
         * specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the measure
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
         * A formal identifier that is used to identify this measure when it is represented in other formats, or referenced in a 
         * specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the measure
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
         * Convenience method for setting {@code version}.
         * 
         * @param version
         *     Business version of the measure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #version(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder version(java.lang.String version) {
            this.version = (version == null) ? null : String.of(version);
            return this;
        }

        /**
         * The identifier that is used to identify this version of the measure when it is referenced in a specification, model, 
         * design or instance. This is an arbitrary value managed by the measure author and is not expected to be globally 
         * unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is also no 
         * expectation that versions can be placed in a lexicographical sequence. To provide a version consistent with the 
         * Decision Support Service specification, use the format Major.Minor.Revision (e.g. 1.0.0). For more information on 
         * versioning knowledge assets, refer to the Decision Support Service specification. Note that a version is required for 
         * non-experimental active artifacts.
         * 
         * @param version
         *     Business version of the measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * Convenience method for setting {@code versionAlgorithm} with choice type String.
         * 
         * @param versionAlgorithm
         *     How to compare versions
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #versionAlgorithm(Element)
         */
        public Builder versionAlgorithm(java.lang.String versionAlgorithm) {
            this.versionAlgorithm = (versionAlgorithm == null) ? null : String.of(versionAlgorithm);
            return this;
        }

        /**
         * Indicates the mechanism used to compare versions to determine which is more current.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link String}</li>
         * <li>{@link Coding}</li>
         * </ul>
         * 
         * @param versionAlgorithm
         *     How to compare versions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder versionAlgorithm(Element versionAlgorithm) {
            this.versionAlgorithm = versionAlgorithm;
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Name for this measure (computer friendly)
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
         * A natural language name identifying the measure. This name should be usable as an identifier for the module by machine 
         * processing applications such as code generation.
         * 
         * @param name
         *     Name for this measure (computer friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Convenience method for setting {@code title}.
         * 
         * @param title
         *     Name for this measure (human friendly)
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
         * A short, descriptive, user-friendly title for the measure.
         * 
         * @param title
         *     Name for this measure (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Convenience method for setting {@code subtitle}.
         * 
         * @param subtitle
         *     Subordinate title of the measure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #subtitle(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder subtitle(java.lang.String subtitle) {
            this.subtitle = (subtitle == null) ? null : String.of(subtitle);
            return this;
        }

        /**
         * An explanatory or alternate title for the measure giving additional information about its content.
         * 
         * @param subtitle
         *     Subordinate title of the measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        /**
         * The status of this measure. Enables tracking the life-cycle of the content.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | retired | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(PublicationStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Convenience method for setting {@code experimental}.
         * 
         * @param experimental
         *     For testing purposes, not real usage
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #experimental(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder experimental(java.lang.Boolean experimental) {
            this.experimental = (experimental == null) ? null : Boolean.of(experimental);
            return this;
        }

        /**
         * A Boolean value to indicate that this measure is authored for testing purposes (or education/evaluation/marketing) and 
         * is not intended to be used for genuine usage.
         * 
         * @param experimental
         *     For testing purposes, not real usage
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder experimental(Boolean experimental) {
            this.experimental = experimental;
            return this;
        }

        /**
         * The intended subjects for the measure. If this element is not provided, a Patient subject is assumed, but the subject 
         * of the measure can be anything.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link CodeableConcept}</li>
         * <li>{@link Reference}</li>
         * </ul>
         * 
         * When of type {@link Reference}, the allowed resource types for this reference are:
         * <ul>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Element subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The population basis specifies the type of elements in the population. For a subject-based measure, this is boolean 
         * (because the subject and the population basis are the same, and the population criteria define yes/no values for each 
         * individual in the population). For measures that have a population basis that is different than the subject, this 
         * element specifies the type of the population basis. For example, an encounter-based measure has a subject of Patient 
         * and a population basis of Encounter, and the population criteria all return lists of Encounters.
         * 
         * @param basis
         *     Population basis
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder basis(BasisType basis) {
            this.basis = basis;
            return this;
        }

        /**
         * The date (and optionally time) when the measure was last significantly changed. The date must change when the business 
         * version changes and it must change if the status code changes. In addition, it should change when the substantive 
         * content of the measure changes.
         * 
         * @param date
         *     Date last changed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(DateTime date) {
            this.date = date;
            return this;
        }

        /**
         * Convenience method for setting {@code publisher}.
         * 
         * @param publisher
         *     Name of the publisher/steward (organization or individual)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #publisher(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder publisher(java.lang.String publisher) {
            this.publisher = (publisher == null) ? null : String.of(publisher);
            return this;
        }

        /**
         * The name of the organization or individual responsible for the release and ongoing maintenance of the measure.
         * 
         * @param publisher
         *     Name of the publisher/steward (organization or individual)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        /**
         * Contact details to assist a user in finding and communicating with the publisher.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder contact(ContactDetail... contact) {
            for (ContactDetail value : contact) {
                this.contact.add(value);
            }
            return this;
        }

        /**
         * Contact details to assist a user in finding and communicating with the publisher.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contact
         *     Contact details for the publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder contact(Collection<ContactDetail> contact) {
            this.contact = new ArrayList<>(contact);
            return this;
        }

        /**
         * A free text natural language description of the measure from a consumer's perspective.
         * 
         * @param description
         *     Natural language description of the measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
         * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
         * may be used to assist with indexing and searching for appropriate measure instances.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     The context that the content is intended to support
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder useContext(UsageContext... useContext) {
            for (UsageContext value : useContext) {
                this.useContext.add(value);
            }
            return this;
        }

        /**
         * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
         * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
         * may be used to assist with indexing and searching for appropriate measure instances.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     The context that the content is intended to support
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder useContext(Collection<UsageContext> useContext) {
            this.useContext = new ArrayList<>(useContext);
            return this;
        }

        /**
         * A legal or geographic region in which the measure is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for measure (if applicable)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder jurisdiction(CodeableConcept... jurisdiction) {
            for (CodeableConcept value : jurisdiction) {
                this.jurisdiction.add(value);
            }
            return this;
        }

        /**
         * A legal or geographic region in which the measure is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for measure (if applicable)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder jurisdiction(Collection<CodeableConcept> jurisdiction) {
            this.jurisdiction = new ArrayList<>(jurisdiction);
            return this;
        }

        /**
         * Explanation of why this measure is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this measure is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * A detailed description, from a clinical perspective, of how the measure is used.
         * 
         * @param usage
         *     Describes the clinical usage of the measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder usage(Markdown usage) {
            this.usage = usage;
            return this;
        }

        /**
         * A copyright statement relating to the measure and/or its contents. Copyright statements are generally legal 
         * restrictions on the use and publishing of the measure.
         * 
         * @param copyright
         *     Use and/or publishing restrictions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder copyright(Markdown copyright) {
            this.copyright = copyright;
            return this;
        }

        /**
         * Convenience method for setting {@code copyrightLabel}.
         * 
         * @param copyrightLabel
         *     Copyright holder and year(s)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #copyrightLabel(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder copyrightLabel(java.lang.String copyrightLabel) {
            this.copyrightLabel = (copyrightLabel == null) ? null : String.of(copyrightLabel);
            return this;
        }

        /**
         * A short string (&lt;50 characters), suitable for inclusion in a page footer that identifies the copyright holder, 
         * effective period, and optionally whether rights are resctricted. (e.g. 'All rights reserved', 'Some rights reserved').
         * 
         * @param copyrightLabel
         *     Copyright holder and year(s)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder copyrightLabel(String copyrightLabel) {
            this.copyrightLabel = copyrightLabel;
            return this;
        }

        /**
         * Convenience method for setting {@code approvalDate}.
         * 
         * @param approvalDate
         *     When the measure was approved by publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #approvalDate(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder approvalDate(java.time.LocalDate approvalDate) {
            this.approvalDate = (approvalDate == null) ? null : Date.of(approvalDate);
            return this;
        }

        /**
         * The date on which the resource content was approved by the publisher. Approval happens once when the content is 
         * officially approved for usage.
         * 
         * @param approvalDate
         *     When the measure was approved by publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder approvalDate(Date approvalDate) {
            this.approvalDate = approvalDate;
            return this;
        }

        /**
         * Convenience method for setting {@code lastReviewDate}.
         * 
         * @param lastReviewDate
         *     When the measure was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #lastReviewDate(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder lastReviewDate(java.time.LocalDate lastReviewDate) {
            this.lastReviewDate = (lastReviewDate == null) ? null : Date.of(lastReviewDate);
            return this;
        }

        /**
         * The date on which the resource content was last reviewed. Review happens periodically after approval but does not 
         * change the original approval date.
         * 
         * @param lastReviewDate
         *     When the measure was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
        }

        /**
         * The period during which the measure content was or is planned to be in active use.
         * 
         * @param effectivePeriod
         *     When the measure is expected to be used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effectivePeriod(Period effectivePeriod) {
            this.effectivePeriod = effectivePeriod;
            return this;
        }

        /**
         * Descriptive topics related to the content of the measure. Topics provide a high-level categorization grouping types of 
         * measures that can be useful for filtering and searching.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param topic
         *     The category of the measure, such as Education, Treatment, Assessment, etc
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder topic(CodeableConcept... topic) {
            for (CodeableConcept value : topic) {
                this.topic.add(value);
            }
            return this;
        }

        /**
         * Descriptive topics related to the content of the measure. Topics provide a high-level categorization grouping types of 
         * measures that can be useful for filtering and searching.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param topic
         *     The category of the measure, such as Education, Treatment, Assessment, etc
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder topic(Collection<CodeableConcept> topic) {
            this.topic = new ArrayList<>(topic);
            return this;
        }

        /**
         * An individiual or organization primarily involved in the creation and maintenance of the content.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param author
         *     Who authored the content
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder author(ContactDetail... author) {
            for (ContactDetail value : author) {
                this.author.add(value);
            }
            return this;
        }

        /**
         * An individiual or organization primarily involved in the creation and maintenance of the content.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param author
         *     Who authored the content
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder author(Collection<ContactDetail> author) {
            this.author = new ArrayList<>(author);
            return this;
        }

        /**
         * An individual or organization primarily responsible for internal coherence of the content.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param editor
         *     Who edited the content
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder editor(ContactDetail... editor) {
            for (ContactDetail value : editor) {
                this.editor.add(value);
            }
            return this;
        }

        /**
         * An individual or organization primarily responsible for internal coherence of the content.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param editor
         *     Who edited the content
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder editor(Collection<ContactDetail> editor) {
            this.editor = new ArrayList<>(editor);
            return this;
        }

        /**
         * An individual or organization asserted by the publisher to be primarily responsible for review of some aspect of the 
         * content.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reviewer
         *     Who reviewed the content
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reviewer(ContactDetail... reviewer) {
            for (ContactDetail value : reviewer) {
                this.reviewer.add(value);
            }
            return this;
        }

        /**
         * An individual or organization asserted by the publisher to be primarily responsible for review of some aspect of the 
         * content.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reviewer
         *     Who reviewed the content
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reviewer(Collection<ContactDetail> reviewer) {
            this.reviewer = new ArrayList<>(reviewer);
            return this;
        }

        /**
         * An individual or organization asserted by the publisher to be responsible for officially endorsing the content for use 
         * in some setting.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param endorser
         *     Who endorsed the content
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder endorser(ContactDetail... endorser) {
            for (ContactDetail value : endorser) {
                this.endorser.add(value);
            }
            return this;
        }

        /**
         * An individual or organization asserted by the publisher to be responsible for officially endorsing the content for use 
         * in some setting.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param endorser
         *     Who endorsed the content
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder endorser(Collection<ContactDetail> endorser) {
            this.endorser = new ArrayList<>(endorser);
            return this;
        }

        /**
         * Related artifacts such as additional documentation, justification, or bibliographic references.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedArtifact
         *     Additional documentation, citations, etc
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relatedArtifact(RelatedArtifact... relatedArtifact) {
            for (RelatedArtifact value : relatedArtifact) {
                this.relatedArtifact.add(value);
            }
            return this;
        }

        /**
         * Related artifacts such as additional documentation, justification, or bibliographic references.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedArtifact
         *     Additional documentation, citations, etc
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relatedArtifact(Collection<RelatedArtifact> relatedArtifact) {
            this.relatedArtifact = new ArrayList<>(relatedArtifact);
            return this;
        }

        /**
         * A reference to a Library resource containing the formal logic used by the measure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param library
         *     Logic used by the measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder library(Canonical... library) {
            for (Canonical value : library) {
                this.library.add(value);
            }
            return this;
        }

        /**
         * A reference to a Library resource containing the formal logic used by the measure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param library
         *     Logic used by the measure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder library(Collection<Canonical> library) {
            this.library = new ArrayList<>(library);
            return this;
        }

        /**
         * Notices and disclaimers regarding the use of the measure or related to intellectual property (such as code systems) 
         * referenced by the measure.
         * 
         * @param disclaimer
         *     Disclaimer for use of the measure or its referenced content
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder disclaimer(Markdown disclaimer) {
            this.disclaimer = disclaimer;
            return this;
        }

        /**
         * Indicates how the calculation is performed for the measure, including proportion, ratio, continuous-variable, and 
         * cohort. The value set is extensible, allowing additional measure scoring types to be represented.
         * 
         * @param scoring
         *     proportion | ratio | continuous-variable | cohort
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder scoring(CodeableConcept scoring) {
            this.scoring = scoring;
            return this;
        }

        /**
         * Defines the expected units of measure for the measure score. This element SHOULD be specified as a UCUM unit.
         * 
         * @param scoringUnit
         *     What units?
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder scoringUnit(CodeableConcept scoringUnit) {
            this.scoringUnit = scoringUnit;
            return this;
        }

        /**
         * If this is a composite measure, the scoring method used to combine the component measures to determine the composite 
         * score.
         * 
         * @param compositeScoring
         *     opportunity | all-or-nothing | linear | weighted
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder compositeScoring(CodeableConcept compositeScoring) {
            this.compositeScoring = compositeScoring;
            return this;
        }

        /**
         * Indicates whether the measure is used to examine a process, an outcome over time, a patient-reported outcome, or a 
         * structure measure such as utilization.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     process | outcome | structure | patient-reported-outcome | composite
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
         * Indicates whether the measure is used to examine a process, an outcome over time, a patient-reported outcome, or a 
         * structure measure such as utilization.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param type
         *     process | outcome | structure | patient-reported-outcome | composite
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
         * A description of the risk adjustment factors that may impact the resulting score for the measure and how they may be 
         * accounted for when computing and reporting measure results.
         * 
         * @param riskAdjustment
         *     How risk adjustment is applied for this measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder riskAdjustment(Markdown riskAdjustment) {
            this.riskAdjustment = riskAdjustment;
            return this;
        }

        /**
         * Describes how to combine the information calculated, based on logic in each of several populations, into one 
         * summarized result.
         * 
         * @param rateAggregation
         *     How is rate aggregation performed for this measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder rateAggregation(Markdown rateAggregation) {
            this.rateAggregation = rateAggregation;
            return this;
        }

        /**
         * Provides a succinct statement of the need for the measure. Usually includes statements pertaining to importance 
         * criterion: impact, gap in care, and evidence.
         * 
         * @param rationale
         *     Detailed description of why the measure exists
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder rationale(Markdown rationale) {
            this.rationale = rationale;
            return this;
        }

        /**
         * Provides a summary of relevant clinical guidelines or other clinical recommendations supporting the measure.
         * 
         * @param clinicalRecommendationStatement
         *     Summary of clinical guidelines
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder clinicalRecommendationStatement(Markdown clinicalRecommendationStatement) {
            this.clinicalRecommendationStatement = clinicalRecommendationStatement;
            return this;
        }

        /**
         * Information on whether an increase or decrease in score is the preferred result (e.g., a higher score indicates better 
         * quality OR a lower score indicates better quality OR quality is within a range).
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
         * Provides a description of an individual term used within the measure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param term
         *     Defined terms used in the measure documentation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder term(Term... term) {
            for (Term value : term) {
                this.term.add(value);
            }
            return this;
        }

        /**
         * Provides a description of an individual term used within the measure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param term
         *     Defined terms used in the measure documentation
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder term(Collection<Term> term) {
            this.term = new ArrayList<>(term);
            return this;
        }

        /**
         * Additional guidance for the measure including how it can be used in a clinical context, and the intent of the measure.
         * 
         * @param guidance
         *     Additional guidance for implementers (deprecated)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder guidance(Markdown guidance) {
            this.guidance = guidance;
            return this;
        }

        /**
         * A group of population criteria for the measure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param group
         *     Population criteria group
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
         * A group of population criteria for the measure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param group
         *     Population criteria group
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
         * The supplemental data criteria for the measure report, specified as either the name of a valid CQL expression within a 
         * referenced library, or a valid FHIR Resource Path.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supplementalData
         *     What other data should be reported with the measure
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supplementalData(SupplementalData... supplementalData) {
            for (SupplementalData value : supplementalData) {
                this.supplementalData.add(value);
            }
            return this;
        }

        /**
         * The supplemental data criteria for the measure report, specified as either the name of a valid CQL expression within a 
         * referenced library, or a valid FHIR Resource Path.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supplementalData
         *     What other data should be reported with the measure
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supplementalData(Collection<SupplementalData> supplementalData) {
            this.supplementalData = new ArrayList<>(supplementalData);
            return this;
        }

        /**
         * Build the {@link Measure}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Measure}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Measure per the base specification
         */
        @Override
        public Measure build() {
            Measure measure = new Measure(this);
            if (validating) {
                validate(measure);
            }
            return measure;
        }

        protected void validate(Measure measure) {
            super.validate(measure);
            ValidationSupport.checkList(measure.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(measure.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(measure.status, "status");
            ValidationSupport.choiceElement(measure.subject, "subject", CodeableConcept.class, Reference.class);
            ValidationSupport.checkList(measure.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(measure.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(measure.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(measure.topic, "topic", CodeableConcept.class);
            ValidationSupport.checkList(measure.author, "author", ContactDetail.class);
            ValidationSupport.checkList(measure.editor, "editor", ContactDetail.class);
            ValidationSupport.checkList(measure.reviewer, "reviewer", ContactDetail.class);
            ValidationSupport.checkList(measure.endorser, "endorser", ContactDetail.class);
            ValidationSupport.checkList(measure.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
            ValidationSupport.checkList(measure.library, "library", Canonical.class);
            ValidationSupport.checkList(measure.type, "type", CodeableConcept.class);
            ValidationSupport.checkList(measure.term, "term", Term.class);
            ValidationSupport.checkList(measure.group, "group", Group.class);
            ValidationSupport.checkList(measure.supplementalData, "supplementalData", SupplementalData.class);
            ValidationSupport.checkReferenceType(measure.subject, "subject", "Group");
        }

        protected Builder from(Measure measure) {
            super.from(measure);
            url = measure.url;
            identifier.addAll(measure.identifier);
            version = measure.version;
            versionAlgorithm = measure.versionAlgorithm;
            name = measure.name;
            title = measure.title;
            subtitle = measure.subtitle;
            status = measure.status;
            experimental = measure.experimental;
            subject = measure.subject;
            basis = measure.basis;
            date = measure.date;
            publisher = measure.publisher;
            contact.addAll(measure.contact);
            description = measure.description;
            useContext.addAll(measure.useContext);
            jurisdiction.addAll(measure.jurisdiction);
            purpose = measure.purpose;
            usage = measure.usage;
            copyright = measure.copyright;
            copyrightLabel = measure.copyrightLabel;
            approvalDate = measure.approvalDate;
            lastReviewDate = measure.lastReviewDate;
            effectivePeriod = measure.effectivePeriod;
            topic.addAll(measure.topic);
            author.addAll(measure.author);
            editor.addAll(measure.editor);
            reviewer.addAll(measure.reviewer);
            endorser.addAll(measure.endorser);
            relatedArtifact.addAll(measure.relatedArtifact);
            library.addAll(measure.library);
            disclaimer = measure.disclaimer;
            scoring = measure.scoring;
            scoringUnit = measure.scoringUnit;
            compositeScoring = measure.compositeScoring;
            type.addAll(measure.type);
            riskAdjustment = measure.riskAdjustment;
            rateAggregation = measure.rateAggregation;
            rationale = measure.rationale;
            clinicalRecommendationStatement = measure.clinicalRecommendationStatement;
            improvementNotation = measure.improvementNotation;
            term.addAll(measure.term);
            guidance = measure.guidance;
            group.addAll(measure.group);
            supplementalData.addAll(measure.supplementalData);
            return this;
        }
    }

    /**
     * Provides a description of an individual term used within the measure.
     */
    public static class Term extends BackboneElement {
        @Binding(
            bindingName = "DefinitionCode",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codeable representations of measure definition terms.",
            valueSet = "http://hl7.org/fhir/ValueSet/measure-definition-example"
        )
        private final CodeableConcept code;
        private final Markdown definition;

        private Term(Builder builder) {
            super(builder);
            code = builder.code;
            definition = builder.definition;
        }

        /**
         * A codeable representation of the defined term.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * Provides a definition for the term as used within the measure.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDefinition() {
            return definition;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (definition != null);
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
                    accept(definition, "definition", visitor);
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
            Term other = (Term) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(definition, other.definition);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    definition);
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
            private Markdown definition;

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
             * A codeable representation of the defined term.
             * 
             * @param code
             *     What term?
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Provides a definition for the term as used within the measure.
             * 
             * @param definition
             *     Meaning of the term
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definition(Markdown definition) {
                this.definition = definition;
                return this;
            }

            /**
             * Build the {@link Term}
             * 
             * @return
             *     An immutable object of type {@link Term}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Term per the base specification
             */
            @Override
            public Term build() {
                Term term = new Term(this);
                if (validating) {
                    validate(term);
                }
                return term;
            }

            protected void validate(Term term) {
                super.validate(term);
                ValidationSupport.requireValueOrChildren(term);
            }

            protected Builder from(Term term) {
                super.from(term);
                code = term.code;
                definition = term.definition;
                return this;
            }
        }
    }

    /**
     * A group of population criteria for the measure.
     */
    public static class Group extends BackboneElement {
        private final String linkId;
        @Binding(
            bindingName = "MeasureGroupExample",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Example of measure groups.",
            valueSet = "http://hl7.org/fhir/ValueSet/measure-group-example"
        )
        private final CodeableConcept code;
        private final Markdown description;
        @Summary
        @Binding(
            bindingName = "MeasureType",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://hl7.org/fhir/ValueSet/measure-type"
        )
        private final List<CodeableConcept> type;
        @ReferenceTarget({ "Group" })
        @Choice({ CodeableConcept.class, Reference.class })
        @Binding(
            bindingName = "SubjectType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The possible types of subjects for a measure (E.g. Patient, Practitioner, Organization, Location, etc.).",
            valueSet = "http://hl7.org/fhir/ValueSet/participant-resource-types"
        )
        private final Element subject;
        @Summary
        @Binding(
            bindingName = "BasisType",
            strength = BindingStrength.Value.REQUIRED,
            valueSet = "http://hl7.org/fhir/ValueSet/fhir-types|5.0.0"
        )
        private final BasisType basis;
        @Summary
        @Binding(
            bindingName = "MeasureScoring",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://terminology.hl7.org/ValueSet/measure-scoring"
        )
        private final CodeableConcept scoring;
        @Summary
        @Binding(
            bindingName = "MeasureScoringUnit",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/measure-scoring-unit"
        )
        private final CodeableConcept scoringUnit;
        @Summary
        private final Markdown rateAggregation;
        @Summary
        @Binding(
            bindingName = "MeasureImprovementNotation",
            strength = BindingStrength.Value.REQUIRED,
            valueSet = "http://hl7.org/fhir/ValueSet/measure-improvement-notation|5.0.0"
        )
        private final CodeableConcept improvementNotation;
        private final List<Canonical> library;
        private final List<Population> population;
        private final List<Stratifier> stratifier;

        private Group(Builder builder) {
            super(builder);
            linkId = builder.linkId;
            code = builder.code;
            description = builder.description;
            type = Collections.unmodifiableList(builder.type);
            subject = builder.subject;
            basis = builder.basis;
            scoring = builder.scoring;
            scoringUnit = builder.scoringUnit;
            rateAggregation = builder.rateAggregation;
            improvementNotation = builder.improvementNotation;
            library = Collections.unmodifiableList(builder.library);
            population = Collections.unmodifiableList(builder.population);
            stratifier = Collections.unmodifiableList(builder.stratifier);
        }

        /**
         * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getLinkId() {
            return linkId;
        }

        /**
         * Indicates a meaning for the group. This can be as simple as a unique identifier, or it can establish meaning in a 
         * broader context by drawing from a terminology, allowing groups to be correlated across measures.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * The human readable description of this population group.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * Indicates whether the measure is used to examine a process, an outcome over time, a patient-reported outcome, or a 
         * structure measure such as utilization.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * The intended subjects for the measure. If this element is not provided, a Patient subject is assumed, but the subject 
         * of the measure can be anything.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} or {@link Reference} that may be null.
         */
        public Element getSubject() {
            return subject;
        }

        /**
         * The population basis specifies the type of elements in the population. For a subject-based measure, this is boolean 
         * (because the subject and the population basis are the same, and the population criteria define yes/no values for each 
         * individual in the population). For measures that have a population basis that is different than the subject, this 
         * element specifies the type of the population basis. For example, an encounter-based measure has a subject of Patient 
         * and a population basis of Encounter, and the population criteria all return lists of Encounters.
         * 
         * @return
         *     An immutable object of type {@link BasisType} that may be null.
         */
        public BasisType getBasis() {
            return basis;
        }

        /**
         * Indicates how the calculation is performed for the measure, including proportion, ratio, continuous-variable, and 
         * cohort. The value set is extensible, allowing additional measure scoring types to be represented.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getScoring() {
            return scoring;
        }

        /**
         * Defines the expected units of measure for the measure score. This element SHOULD be specified as a UCUM unit.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getScoringUnit() {
            return scoringUnit;
        }

        /**
         * Describes how to combine the information calculated, based on logic in each of several populations, into one 
         * summarized result.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getRateAggregation() {
            return rateAggregation;
        }

        /**
         * Information on whether an increase or decrease in score is the preferred result (e.g., a higher score indicates better 
         * quality OR a lower score indicates better quality OR quality is within a range).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getImprovementNotation() {
            return improvementNotation;
        }

        /**
         * A reference to a Library resource containing the formal logic used by the measure group.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
         */
        public List<Canonical> getLibrary() {
            return library;
        }

        /**
         * A population criteria for the measure.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Population} that may be empty.
         */
        public List<Population> getPopulation() {
            return population;
        }

        /**
         * The stratifier criteria for the measure report, specified as either the name of a valid CQL expression defined within 
         * a referenced library or a valid FHIR Resource Path.
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
                (description != null) || 
                !type.isEmpty() || 
                (subject != null) || 
                (basis != null) || 
                (scoring != null) || 
                (scoringUnit != null) || 
                (rateAggregation != null) || 
                (improvementNotation != null) || 
                !library.isEmpty() || 
                !population.isEmpty() || 
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
                    accept(description, "description", visitor);
                    accept(type, "type", visitor, CodeableConcept.class);
                    accept(subject, "subject", visitor);
                    accept(basis, "basis", visitor);
                    accept(scoring, "scoring", visitor);
                    accept(scoringUnit, "scoringUnit", visitor);
                    accept(rateAggregation, "rateAggregation", visitor);
                    accept(improvementNotation, "improvementNotation", visitor);
                    accept(library, "library", visitor, Canonical.class);
                    accept(population, "population", visitor, Population.class);
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
                Objects.equals(description, other.description) && 
                Objects.equals(type, other.type) && 
                Objects.equals(subject, other.subject) && 
                Objects.equals(basis, other.basis) && 
                Objects.equals(scoring, other.scoring) && 
                Objects.equals(scoringUnit, other.scoringUnit) && 
                Objects.equals(rateAggregation, other.rateAggregation) && 
                Objects.equals(improvementNotation, other.improvementNotation) && 
                Objects.equals(library, other.library) && 
                Objects.equals(population, other.population) && 
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
                    description, 
                    type, 
                    subject, 
                    basis, 
                    scoring, 
                    scoringUnit, 
                    rateAggregation, 
                    improvementNotation, 
                    library, 
                    population, 
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
            private Markdown description;
            private List<CodeableConcept> type = new ArrayList<>();
            private Element subject;
            private BasisType basis;
            private CodeableConcept scoring;
            private CodeableConcept scoringUnit;
            private Markdown rateAggregation;
            private CodeableConcept improvementNotation;
            private List<Canonical> library = new ArrayList<>();
            private List<Population> population = new ArrayList<>();
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
             *     Unique id for group in measure
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
             * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
             * 
             * @param linkId
             *     Unique id for group in measure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder linkId(String linkId) {
                this.linkId = linkId;
                return this;
            }

            /**
             * Indicates a meaning for the group. This can be as simple as a unique identifier, or it can establish meaning in a 
             * broader context by drawing from a terminology, allowing groups to be correlated across measures.
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
             * The human readable description of this population group.
             * 
             * @param description
             *     Summary description
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * Indicates whether the measure is used to examine a process, an outcome over time, a patient-reported outcome, or a 
             * structure measure such as utilization.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     process | outcome | structure | patient-reported-outcome | composite
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
             * Indicates whether the measure is used to examine a process, an outcome over time, a patient-reported outcome, or a 
             * structure measure such as utilization.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     process | outcome | structure | patient-reported-outcome | composite
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
             * The intended subjects for the measure. If this element is not provided, a Patient subject is assumed, but the subject 
             * of the measure can be anything.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Reference}</li>
             * </ul>
             * 
             * When of type {@link Reference}, the allowed resource types for this reference are:
             * <ul>
             * <li>{@link Group}</li>
             * </ul>
             * 
             * @param subject
             *     E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder subject(Element subject) {
                this.subject = subject;
                return this;
            }

            /**
             * The population basis specifies the type of elements in the population. For a subject-based measure, this is boolean 
             * (because the subject and the population basis are the same, and the population criteria define yes/no values for each 
             * individual in the population). For measures that have a population basis that is different than the subject, this 
             * element specifies the type of the population basis. For example, an encounter-based measure has a subject of Patient 
             * and a population basis of Encounter, and the population criteria all return lists of Encounters.
             * 
             * @param basis
             *     Population basis
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder basis(BasisType basis) {
                this.basis = basis;
                return this;
            }

            /**
             * Indicates how the calculation is performed for the measure, including proportion, ratio, continuous-variable, and 
             * cohort. The value set is extensible, allowing additional measure scoring types to be represented.
             * 
             * @param scoring
             *     proportion | ratio | continuous-variable | cohort
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder scoring(CodeableConcept scoring) {
                this.scoring = scoring;
                return this;
            }

            /**
             * Defines the expected units of measure for the measure score. This element SHOULD be specified as a UCUM unit.
             * 
             * @param scoringUnit
             *     What units?
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder scoringUnit(CodeableConcept scoringUnit) {
                this.scoringUnit = scoringUnit;
                return this;
            }

            /**
             * Describes how to combine the information calculated, based on logic in each of several populations, into one 
             * summarized result.
             * 
             * @param rateAggregation
             *     How is rate aggregation performed for this measure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder rateAggregation(Markdown rateAggregation) {
                this.rateAggregation = rateAggregation;
                return this;
            }

            /**
             * Information on whether an increase or decrease in score is the preferred result (e.g., a higher score indicates better 
             * quality OR a lower score indicates better quality OR quality is within a range).
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
             * A reference to a Library resource containing the formal logic used by the measure group.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param library
             *     Logic used by the measure group
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder library(Canonical... library) {
                for (Canonical value : library) {
                    this.library.add(value);
                }
                return this;
            }

            /**
             * A reference to a Library resource containing the formal logic used by the measure group.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param library
             *     Logic used by the measure group
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder library(Collection<Canonical> library) {
                this.library = new ArrayList<>(library);
                return this;
            }

            /**
             * A population criteria for the measure.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param population
             *     Population criteria
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
             * A population criteria for the measure.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param population
             *     Population criteria
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
             * The stratifier criteria for the measure report, specified as either the name of a valid CQL expression defined within 
             * a referenced library or a valid FHIR Resource Path.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param stratifier
             *     Stratifier criteria for the measure
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
             * The stratifier criteria for the measure report, specified as either the name of a valid CQL expression defined within 
             * a referenced library or a valid FHIR Resource Path.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param stratifier
             *     Stratifier criteria for the measure
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
                ValidationSupport.checkList(group.type, "type", CodeableConcept.class);
                ValidationSupport.choiceElement(group.subject, "subject", CodeableConcept.class, Reference.class);
                ValidationSupport.checkList(group.library, "library", Canonical.class);
                ValidationSupport.checkList(group.population, "population", Population.class);
                ValidationSupport.checkList(group.stratifier, "stratifier", Stratifier.class);
                ValidationSupport.checkReferenceType(group.subject, "subject", "Group");
                ValidationSupport.requireValueOrChildren(group);
            }

            protected Builder from(Group group) {
                super.from(group);
                linkId = group.linkId;
                code = group.code;
                description = group.description;
                type.addAll(group.type);
                subject = group.subject;
                basis = group.basis;
                scoring = group.scoring;
                scoringUnit = group.scoringUnit;
                rateAggregation = group.rateAggregation;
                improvementNotation = group.improvementNotation;
                library.addAll(group.library);
                population.addAll(group.population);
                stratifier.addAll(group.stratifier);
                return this;
            }
        }

        /**
         * A population criteria for the measure.
         */
        public static class Population extends BackboneElement {
            private final String linkId;
            @Binding(
                bindingName = "MeasurePopulationType",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "The type of population.",
                valueSet = "http://hl7.org/fhir/ValueSet/measure-population"
            )
            private final CodeableConcept code;
            private final Markdown description;
            private final Expression criteria;
            @ReferenceTarget({ "Group" })
            private final Reference groupDefinition;
            private final String inputPopulationId;
            @Binding(
                bindingName = "MeasureAggregateMethod",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/measure-aggregate-method"
            )
            private final CodeableConcept aggregateMethod;

            private Population(Builder builder) {
                super(builder);
                linkId = builder.linkId;
                code = builder.code;
                description = builder.description;
                criteria = builder.criteria;
                groupDefinition = builder.groupDefinition;
                inputPopulationId = builder.inputPopulationId;
                aggregateMethod = builder.aggregateMethod;
            }

            /**
             * An identifier that is unique within the Measure allowing linkage to the equivalent population in a MeasureReport 
             * resource.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getLinkId() {
                return linkId;
            }

            /**
             * The type of population criteria.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCode() {
                return code;
            }

            /**
             * The human readable description of this population criteria.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getDescription() {
                return description;
            }

            /**
             * An expression that specifies the criteria for the population, typically the name of an expression in a library.
             * 
             * @return
             *     An immutable object of type {@link Expression} that may be null.
             */
            public Expression getCriteria() {
                return criteria;
            }

            /**
             * A Group resource that defines this population as a set of characteristics.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getGroupDefinition() {
                return groupDefinition;
            }

            /**
             * The id of a population element in this measure that provides the input for this population criteria. In most cases, 
             * the scoring structure of the measure implies specific relationships (e.g. the Numerator uses the Denominator as the 
             * source in a proportion scoring). In some cases, however, multiple possible choices exist and must be resolved 
             * explicitly. For example in a ratio measure with multiple initial populations, the denominator must specify which 
             * population should be used as the starting point.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getInputPopulationId() {
                return inputPopulationId;
            }

            /**
             * Specifies which method should be used to aggregate measure observation values. For most scoring types, this is implied 
             * by scoring (e.g. a proportion measure counts members of the populations). For continuous variables, however, this 
             * information must be specified to ensure correct calculation.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getAggregateMethod() {
                return aggregateMethod;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (linkId != null) || 
                    (code != null) || 
                    (description != null) || 
                    (criteria != null) || 
                    (groupDefinition != null) || 
                    (inputPopulationId != null) || 
                    (aggregateMethod != null);
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
                        accept(description, "description", visitor);
                        accept(criteria, "criteria", visitor);
                        accept(groupDefinition, "groupDefinition", visitor);
                        accept(inputPopulationId, "inputPopulationId", visitor);
                        accept(aggregateMethod, "aggregateMethod", visitor);
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
                    Objects.equals(description, other.description) && 
                    Objects.equals(criteria, other.criteria) && 
                    Objects.equals(groupDefinition, other.groupDefinition) && 
                    Objects.equals(inputPopulationId, other.inputPopulationId) && 
                    Objects.equals(aggregateMethod, other.aggregateMethod);
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
                        description, 
                        criteria, 
                        groupDefinition, 
                        inputPopulationId, 
                        aggregateMethod);
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
                private Markdown description;
                private Expression criteria;
                private Reference groupDefinition;
                private String inputPopulationId;
                private CodeableConcept aggregateMethod;

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
                 *     Unique id for population in measure
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
                 * An identifier that is unique within the Measure allowing linkage to the equivalent population in a MeasureReport 
                 * resource.
                 * 
                 * @param linkId
                 *     Unique id for population in measure
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder linkId(String linkId) {
                    this.linkId = linkId;
                    return this;
                }

                /**
                 * The type of population criteria.
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
                 * The human readable description of this population criteria.
                 * 
                 * @param description
                 *     The human readable description of this population criteria
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder description(Markdown description) {
                    this.description = description;
                    return this;
                }

                /**
                 * An expression that specifies the criteria for the population, typically the name of an expression in a library.
                 * 
                 * @param criteria
                 *     The criteria that defines this population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder criteria(Expression criteria) {
                    this.criteria = criteria;
                    return this;
                }

                /**
                 * A Group resource that defines this population as a set of characteristics.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Group}</li>
                 * </ul>
                 * 
                 * @param groupDefinition
                 *     A group resource that defines this population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder groupDefinition(Reference groupDefinition) {
                    this.groupDefinition = groupDefinition;
                    return this;
                }

                /**
                 * Convenience method for setting {@code inputPopulationId}.
                 * 
                 * @param inputPopulationId
                 *     Which population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #inputPopulationId(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder inputPopulationId(java.lang.String inputPopulationId) {
                    this.inputPopulationId = (inputPopulationId == null) ? null : String.of(inputPopulationId);
                    return this;
                }

                /**
                 * The id of a population element in this measure that provides the input for this population criteria. In most cases, 
                 * the scoring structure of the measure implies specific relationships (e.g. the Numerator uses the Denominator as the 
                 * source in a proportion scoring). In some cases, however, multiple possible choices exist and must be resolved 
                 * explicitly. For example in a ratio measure with multiple initial populations, the denominator must specify which 
                 * population should be used as the starting point.
                 * 
                 * @param inputPopulationId
                 *     Which population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder inputPopulationId(String inputPopulationId) {
                    this.inputPopulationId = inputPopulationId;
                    return this;
                }

                /**
                 * Specifies which method should be used to aggregate measure observation values. For most scoring types, this is implied 
                 * by scoring (e.g. a proportion measure counts members of the populations). For continuous variables, however, this 
                 * information must be specified to ensure correct calculation.
                 * 
                 * @param aggregateMethod
                 *     Aggregation method for a measure score (e.g. sum, average, median, minimum, maximum, count)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder aggregateMethod(CodeableConcept aggregateMethod) {
                    this.aggregateMethod = aggregateMethod;
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
                    ValidationSupport.checkReferenceType(population.groupDefinition, "groupDefinition", "Group");
                    ValidationSupport.requireValueOrChildren(population);
                }

                protected Builder from(Population population) {
                    super.from(population);
                    linkId = population.linkId;
                    code = population.code;
                    description = population.description;
                    criteria = population.criteria;
                    groupDefinition = population.groupDefinition;
                    inputPopulationId = population.inputPopulationId;
                    aggregateMethod = population.aggregateMethod;
                    return this;
                }
            }
        }

        /**
         * The stratifier criteria for the measure report, specified as either the name of a valid CQL expression defined within 
         * a referenced library or a valid FHIR Resource Path.
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
            private final Markdown description;
            private final Expression criteria;
            @ReferenceTarget({ "Group" })
            private final Reference groupDefinition;
            private final List<Component> component;

            private Stratifier(Builder builder) {
                super(builder);
                linkId = builder.linkId;
                code = builder.code;
                description = builder.description;
                criteria = builder.criteria;
                groupDefinition = builder.groupDefinition;
                component = Collections.unmodifiableList(builder.component);
            }

            /**
             * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getLinkId() {
                return linkId;
            }

            /**
             * Indicates a meaning for the stratifier. This can be as simple as a unique identifier, or it can establish meaning in a 
             * broader context by drawing from a terminology, allowing stratifiers to be correlated across measures.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCode() {
                return code;
            }

            /**
             * The human readable description of this stratifier criteria.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getDescription() {
                return description;
            }

            /**
             * An expression that specifies the criteria for the stratifier. This is typically the name of an expression defined 
             * within a referenced library, but it may also be a path to a stratifier element.
             * 
             * @return
             *     An immutable object of type {@link Expression} that may be null.
             */
            public Expression getCriteria() {
                return criteria;
            }

            /**
             * A Group resource that defines this population as a set of characteristics.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getGroupDefinition() {
                return groupDefinition;
            }

            /**
             * A component of the stratifier criteria for the measure report, specified as either the name of a valid CQL expression 
             * defined within a referenced library or a valid FHIR Resource Path.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Component} that may be empty.
             */
            public List<Component> getComponent() {
                return component;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (linkId != null) || 
                    (code != null) || 
                    (description != null) || 
                    (criteria != null) || 
                    (groupDefinition != null) || 
                    !component.isEmpty();
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
                        accept(description, "description", visitor);
                        accept(criteria, "criteria", visitor);
                        accept(groupDefinition, "groupDefinition", visitor);
                        accept(component, "component", visitor, Component.class);
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
                    Objects.equals(description, other.description) && 
                    Objects.equals(criteria, other.criteria) && 
                    Objects.equals(groupDefinition, other.groupDefinition) && 
                    Objects.equals(component, other.component);
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
                        description, 
                        criteria, 
                        groupDefinition, 
                        component);
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
                private Markdown description;
                private Expression criteria;
                private Reference groupDefinition;
                private List<Component> component = new ArrayList<>();

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
                 *     Unique id for stratifier in measure
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
                 * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
                 * 
                 * @param linkId
                 *     Unique id for stratifier in measure
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder linkId(String linkId) {
                    this.linkId = linkId;
                    return this;
                }

                /**
                 * Indicates a meaning for the stratifier. This can be as simple as a unique identifier, or it can establish meaning in a 
                 * broader context by drawing from a terminology, allowing stratifiers to be correlated across measures.
                 * 
                 * @param code
                 *     Meaning of the stratifier
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(CodeableConcept code) {
                    this.code = code;
                    return this;
                }

                /**
                 * The human readable description of this stratifier criteria.
                 * 
                 * @param description
                 *     The human readable description of this stratifier
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder description(Markdown description) {
                    this.description = description;
                    return this;
                }

                /**
                 * An expression that specifies the criteria for the stratifier. This is typically the name of an expression defined 
                 * within a referenced library, but it may also be a path to a stratifier element.
                 * 
                 * @param criteria
                 *     How the measure should be stratified
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder criteria(Expression criteria) {
                    this.criteria = criteria;
                    return this;
                }

                /**
                 * A Group resource that defines this population as a set of characteristics.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Group}</li>
                 * </ul>
                 * 
                 * @param groupDefinition
                 *     A group resource that defines this population
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder groupDefinition(Reference groupDefinition) {
                    this.groupDefinition = groupDefinition;
                    return this;
                }

                /**
                 * A component of the stratifier criteria for the measure report, specified as either the name of a valid CQL expression 
                 * defined within a referenced library or a valid FHIR Resource Path.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param component
                 *     Stratifier criteria component for the measure
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
                 * A component of the stratifier criteria for the measure report, specified as either the name of a valid CQL expression 
                 * defined within a referenced library or a valid FHIR Resource Path.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param component
                 *     Stratifier criteria component for the measure
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
                    ValidationSupport.checkList(stratifier.component, "component", Component.class);
                    ValidationSupport.checkReferenceType(stratifier.groupDefinition, "groupDefinition", "Group");
                    ValidationSupport.requireValueOrChildren(stratifier);
                }

                protected Builder from(Stratifier stratifier) {
                    super.from(stratifier);
                    linkId = stratifier.linkId;
                    code = stratifier.code;
                    description = stratifier.description;
                    criteria = stratifier.criteria;
                    groupDefinition = stratifier.groupDefinition;
                    component.addAll(stratifier.component);
                    return this;
                }
            }

            /**
             * A component of the stratifier criteria for the measure report, specified as either the name of a valid CQL expression 
             * defined within a referenced library or a valid FHIR Resource Path.
             */
            public static class Component extends BackboneElement {
                private final String linkId;
                @Binding(
                    bindingName = "MeasureStratifierExample",
                    strength = BindingStrength.Value.EXAMPLE,
                    description = "Meaning of the stratifier.",
                    valueSet = "http://hl7.org/fhir/ValueSet/measure-stratifier-example"
                )
                private final CodeableConcept code;
                private final Markdown description;
                private final Expression criteria;
                @ReferenceTarget({ "Group" })
                private final Reference groupDefinition;

                private Component(Builder builder) {
                    super(builder);
                    linkId = builder.linkId;
                    code = builder.code;
                    description = builder.description;
                    criteria = builder.criteria;
                    groupDefinition = builder.groupDefinition;
                }

                /**
                 * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getLinkId() {
                    return linkId;
                }

                /**
                 * Indicates a meaning for the stratifier component. This can be as simple as a unique identifier, or it can establish 
                 * meaning in a broader context by drawing from a terminology, allowing stratifiers to be correlated across measures.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getCode() {
                    return code;
                }

                /**
                 * The human readable description of this stratifier criteria component.
                 * 
                 * @return
                 *     An immutable object of type {@link Markdown} that may be null.
                 */
                public Markdown getDescription() {
                    return description;
                }

                /**
                 * An expression that specifies the criteria for this component of the stratifier. This is typically the name of an 
                 * expression defined within a referenced library, but it may also be a path to a stratifier element.
                 * 
                 * @return
                 *     An immutable object of type {@link Expression} that may be null.
                 */
                public Expression getCriteria() {
                    return criteria;
                }

                /**
                 * A Group resource that defines this population as a set of characteristics.
                 * 
                 * @return
                 *     An immutable object of type {@link Reference} that may be null.
                 */
                public Reference getGroupDefinition() {
                    return groupDefinition;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (linkId != null) || 
                        (code != null) || 
                        (description != null) || 
                        (criteria != null) || 
                        (groupDefinition != null);
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
                            accept(description, "description", visitor);
                            accept(criteria, "criteria", visitor);
                            accept(groupDefinition, "groupDefinition", visitor);
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
                        Objects.equals(description, other.description) && 
                        Objects.equals(criteria, other.criteria) && 
                        Objects.equals(groupDefinition, other.groupDefinition);
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
                            description, 
                            criteria, 
                            groupDefinition);
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
                    private Markdown description;
                    private Expression criteria;
                    private Reference groupDefinition;

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
                     *     Unique id for stratifier component in measure
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
                     * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
                     * 
                     * @param linkId
                     *     Unique id for stratifier component in measure
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder linkId(String linkId) {
                        this.linkId = linkId;
                        return this;
                    }

                    /**
                     * Indicates a meaning for the stratifier component. This can be as simple as a unique identifier, or it can establish 
                     * meaning in a broader context by drawing from a terminology, allowing stratifiers to be correlated across measures.
                     * 
                     * @param code
                     *     Meaning of the stratifier component
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder code(CodeableConcept code) {
                        this.code = code;
                        return this;
                    }

                    /**
                     * The human readable description of this stratifier criteria component.
                     * 
                     * @param description
                     *     The human readable description of this stratifier component
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder description(Markdown description) {
                        this.description = description;
                        return this;
                    }

                    /**
                     * An expression that specifies the criteria for this component of the stratifier. This is typically the name of an 
                     * expression defined within a referenced library, but it may also be a path to a stratifier element.
                     * 
                     * @param criteria
                     *     Component of how the measure should be stratified
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder criteria(Expression criteria) {
                        this.criteria = criteria;
                        return this;
                    }

                    /**
                     * A Group resource that defines this population as a set of characteristics.
                     * 
                     * <p>Allowed resource types for this reference:
                     * <ul>
                     * <li>{@link Group}</li>
                     * </ul>
                     * 
                     * @param groupDefinition
                     *     A group resource that defines this population
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder groupDefinition(Reference groupDefinition) {
                        this.groupDefinition = groupDefinition;
                        return this;
                    }

                    /**
                     * Build the {@link Component}
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
                        ValidationSupport.checkReferenceType(component.groupDefinition, "groupDefinition", "Group");
                        ValidationSupport.requireValueOrChildren(component);
                    }

                    protected Builder from(Component component) {
                        super.from(component);
                        linkId = component.linkId;
                        code = component.code;
                        description = component.description;
                        criteria = component.criteria;
                        groupDefinition = component.groupDefinition;
                        return this;
                    }
                }
            }
        }
    }

    /**
     * The supplemental data criteria for the measure report, specified as either the name of a valid CQL expression within a 
     * referenced library, or a valid FHIR Resource Path.
     */
    public static class SupplementalData extends BackboneElement {
        private final String linkId;
        @Binding(
            bindingName = "MeasureSupplementalDataExample",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Meaning of the supplemental data.",
            valueSet = "http://hl7.org/fhir/ValueSet/measure-supplemental-data-example"
        )
        private final CodeableConcept code;
        @Binding(
            bindingName = "MeasureDataUsage",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The intended usage for supplemental data elements in the measure.",
            valueSet = "http://hl7.org/fhir/ValueSet/measure-data-usage"
        )
        private final List<CodeableConcept> usage;
        private final Markdown description;
        @Required
        private final Expression criteria;

        private SupplementalData(Builder builder) {
            super(builder);
            linkId = builder.linkId;
            code = builder.code;
            usage = Collections.unmodifiableList(builder.usage);
            description = builder.description;
            criteria = builder.criteria;
        }

        /**
         * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getLinkId() {
            return linkId;
        }

        /**
         * Indicates a meaning for the supplemental data. This can be as simple as a unique identifier, or it can establish 
         * meaning in a broader context by drawing from a terminology, allowing supplemental data to be correlated across 
         * measures.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * An indicator of the intended usage for the supplemental data element. Supplemental data indicates the data is 
         * additional information requested to augment the measure information. Risk adjustment factor indicates the data is 
         * additional information used to calculate risk adjustment factors when applying a risk model to the measure calculation.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getUsage() {
            return usage;
        }

        /**
         * The human readable description of this supplemental data.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * The criteria for the supplemental data. This is typically the name of a valid expression defined within a referenced 
         * library, but it may also be a path to a specific data element. The criteria defines the data to be returned for this 
         * element.
         * 
         * @return
         *     An immutable object of type {@link Expression} that is non-null.
         */
        public Expression getCriteria() {
            return criteria;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (linkId != null) || 
                (code != null) || 
                !usage.isEmpty() || 
                (description != null) || 
                (criteria != null);
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
                    accept(usage, "usage", visitor, CodeableConcept.class);
                    accept(description, "description", visitor);
                    accept(criteria, "criteria", visitor);
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
            SupplementalData other = (SupplementalData) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(linkId, other.linkId) && 
                Objects.equals(code, other.code) && 
                Objects.equals(usage, other.usage) && 
                Objects.equals(description, other.description) && 
                Objects.equals(criteria, other.criteria);
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
                    usage, 
                    description, 
                    criteria);
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
            private List<CodeableConcept> usage = new ArrayList<>();
            private Markdown description;
            private Expression criteria;

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
             *     Unique id for supplementalData in measure
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
             * An identifier that is unique within the Measure allowing linkage to the equivalent item in a MeasureReport resource.
             * 
             * @param linkId
             *     Unique id for supplementalData in measure
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder linkId(String linkId) {
                this.linkId = linkId;
                return this;
            }

            /**
             * Indicates a meaning for the supplemental data. This can be as simple as a unique identifier, or it can establish 
             * meaning in a broader context by drawing from a terminology, allowing supplemental data to be correlated across 
             * measures.
             * 
             * @param code
             *     Meaning of the supplemental data
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * An indicator of the intended usage for the supplemental data element. Supplemental data indicates the data is 
             * additional information requested to augment the measure information. Risk adjustment factor indicates the data is 
             * additional information used to calculate risk adjustment factors when applying a risk model to the measure calculation.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param usage
             *     supplemental-data | risk-adjustment-factor
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder usage(CodeableConcept... usage) {
                for (CodeableConcept value : usage) {
                    this.usage.add(value);
                }
                return this;
            }

            /**
             * An indicator of the intended usage for the supplemental data element. Supplemental data indicates the data is 
             * additional information requested to augment the measure information. Risk adjustment factor indicates the data is 
             * additional information used to calculate risk adjustment factors when applying a risk model to the measure calculation.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param usage
             *     supplemental-data | risk-adjustment-factor
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder usage(Collection<CodeableConcept> usage) {
                this.usage = new ArrayList<>(usage);
                return this;
            }

            /**
             * The human readable description of this supplemental data.
             * 
             * @param description
             *     The human readable description of this supplemental data
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * The criteria for the supplemental data. This is typically the name of a valid expression defined within a referenced 
             * library, but it may also be a path to a specific data element. The criteria defines the data to be returned for this 
             * element.
             * 
             * <p>This element is required.
             * 
             * @param criteria
             *     Expression describing additional data to be reported
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder criteria(Expression criteria) {
                this.criteria = criteria;
                return this;
            }

            /**
             * Build the {@link SupplementalData}
             * 
             * <p>Required elements:
             * <ul>
             * <li>criteria</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link SupplementalData}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid SupplementalData per the base specification
             */
            @Override
            public SupplementalData build() {
                SupplementalData supplementalData = new SupplementalData(this);
                if (validating) {
                    validate(supplementalData);
                }
                return supplementalData;
            }

            protected void validate(SupplementalData supplementalData) {
                super.validate(supplementalData);
                ValidationSupport.checkList(supplementalData.usage, "usage", CodeableConcept.class);
                ValidationSupport.requireNonNull(supplementalData.criteria, "criteria");
                ValidationSupport.requireValueOrChildren(supplementalData);
            }

            protected Builder from(SupplementalData supplementalData) {
                super.from(supplementalData);
                linkId = supplementalData.linkId;
                code = supplementalData.code;
                usage.addAll(supplementalData.usage);
                description = supplementalData.description;
                criteria = supplementalData.criteria;
                return this;
            }
        }
    }
}
