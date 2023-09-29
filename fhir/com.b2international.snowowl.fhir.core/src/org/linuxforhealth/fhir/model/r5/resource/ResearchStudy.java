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
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A scientific study of nature that sometimes includes processes involved in health and disease. For example, clinical 
 * trials are research studies that involve people. These studies may be related to new ways to screen, prevent, 
 * diagnose, and treat disease. They may also study certain outcomes and certain groups of people by looking at data 
 * collected in the past or future.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "researchStudy-0",
    level = "Warning",
    location = "label.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/title-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/title-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-1",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/research-study-prim-purp-type",
    expression = "primaryPurposeType.exists() implies (primaryPurposeType.memberOf('http://hl7.org/fhir/ValueSet/research-study-prim-purp-type', 'preferred'))",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-2",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/study-design",
    expression = "studyDesign.exists() implies (studyDesign.all(memberOf('http://hl7.org/fhir/ValueSet/study-design', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "region.exists() implies (region.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-4",
    level = "Warning",
    location = "associatedParty.role",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/research-study-party-role",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/research-study-party-role', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-5",
    level = "Warning",
    location = "progressStatus.state",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/research-study-status",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/research-study-status', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-6",
    level = "Warning",
    location = "comparisonGroup.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/research-study-arm-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/research-study-arm-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-7",
    level = "Warning",
    location = "objective.type",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/research-study-objective-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/research-study-objective-type', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Constraint(
    id = "researchStudy-8",
    level = "Warning",
    location = "outcomeMeasure.type",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/research-study-objective-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/research-study-objective-type', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/ResearchStudy",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ResearchStudy extends DomainResource {
    private final Uri url;
    @Summary
    private final List<Identifier> identifier;
    private final String version;
    private final String name;
    @Summary
    private final String title;
    private final List<Label> label;
    @Summary
    @ReferenceTarget({ "PlanDefinition" })
    private final List<Reference> protocol;
    @Summary
    @ReferenceTarget({ "ResearchStudy" })
    private final List<Reference> partOf;
    private final List<RelatedArtifact> relatedArtifact;
    private final DateTime date;
    @Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes that convey the current publication status of the research study resource.",
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    @Required
    private final PublicationStatus status;
    @Summary
    @Binding(
        bindingName = "ResearchStudyPrimaryPurposeType",
        strength = BindingStrength.Value.PREFERRED,
        description = "Codes for the main intent of the study.",
        valueSet = "http://hl7.org/fhir/ValueSet/research-study-prim-purp-type"
    )
    private final CodeableConcept primaryPurposeType;
    @Summary
    @Binding(
        bindingName = "ResearchStudyPhase",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes for the stage in the progression of a therapy from initial experimental use in humans in clinical trials to post-market evaluation.",
        valueSet = "http://hl7.org/fhir/ValueSet/research-study-phase"
    )
    private final CodeableConcept phase;
    @Summary
    @Binding(
        bindingName = "StudyDesign",
        strength = BindingStrength.Value.PREFERRED,
        description = "This is a set of terms for study design characteristics.",
        valueSet = "http://hl7.org/fhir/ValueSet/study-design"
    )
    private final List<CodeableConcept> studyDesign;
    @Binding(
        bindingName = "ResearchStudyFocusType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Common codes of research study focus",
        valueSet = "http://hl7.org/fhir/ValueSet/research-study-focus-type"
    )
    private final List<CodeableReference> focus;
    @Summary
    @Binding(
        bindingName = "ConditionCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Identification of the condition or diagnosis.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
    )
    private final List<CodeableConcept> condition;
    @Summary
    @Binding(
        bindingName = "ResearchStudyKeyword",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Words associated with the study that may be useful in discovery."
    )
    private final List<CodeableConcept> keyword;
    @Summary
    @Binding(
        bindingName = "Jurisdiction",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Countries and regions within which this artifact is targeted for use.",
        valueSet = "http://hl7.org/fhir/ValueSet/jurisdiction"
    )
    private final List<CodeableConcept> region;
    private final Markdown descriptionSummary;
    private final Markdown description;
    @Summary
    private final Period period;
    @Summary
    @ReferenceTarget({ "Location", "ResearchStudy", "Organization" })
    private final List<Reference> site;
    private final List<Annotation> note;
    @Binding(
        bindingName = "ResearchStudyClassifiers",
        strength = BindingStrength.Value.EXAMPLE,
        description = "desc.",
        valueSet = "http://hl7.org/fhir/ValueSet/research-study-classifiers"
    )
    private final List<CodeableConcept> classifier;
    private final List<AssociatedParty> associatedParty;
    private final List<ProgressStatus> progressStatus;
    @Summary
    @Binding(
        bindingName = "ResearchStudyReasonStopped",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes for why the study ended prematurely.",
        valueSet = "http://hl7.org/fhir/ValueSet/research-study-reason-stopped"
    )
    private final CodeableConcept whyStopped;
    @Summary
    private final Recruitment recruitment;
    private final List<ComparisonGroup> comparisonGroup;
    private final List<Objective> objective;
    private final List<OutcomeMeasure> outcomeMeasure;
    @Summary
    @ReferenceTarget({ "EvidenceReport", "Citation", "DiagnosticReport" })
    private final List<Reference> result;

    private ResearchStudy(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        name = builder.name;
        title = builder.title;
        label = Collections.unmodifiableList(builder.label);
        protocol = Collections.unmodifiableList(builder.protocol);
        partOf = Collections.unmodifiableList(builder.partOf);
        relatedArtifact = Collections.unmodifiableList(builder.relatedArtifact);
        date = builder.date;
        status = builder.status;
        primaryPurposeType = builder.primaryPurposeType;
        phase = builder.phase;
        studyDesign = Collections.unmodifiableList(builder.studyDesign);
        focus = Collections.unmodifiableList(builder.focus);
        condition = Collections.unmodifiableList(builder.condition);
        keyword = Collections.unmodifiableList(builder.keyword);
        region = Collections.unmodifiableList(builder.region);
        descriptionSummary = builder.descriptionSummary;
        description = builder.description;
        period = builder.period;
        site = Collections.unmodifiableList(builder.site);
        note = Collections.unmodifiableList(builder.note);
        classifier = Collections.unmodifiableList(builder.classifier);
        associatedParty = Collections.unmodifiableList(builder.associatedParty);
        progressStatus = Collections.unmodifiableList(builder.progressStatus);
        whyStopped = builder.whyStopped;
        recruitment = builder.recruitment;
        comparisonGroup = Collections.unmodifiableList(builder.comparisonGroup);
        objective = Collections.unmodifiableList(builder.objective);
        outcomeMeasure = Collections.unmodifiableList(builder.outcomeMeasure);
        result = Collections.unmodifiableList(builder.result);
    }

    /**
     * Canonical identifier for this study resource, represented as a globally unique URI.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * Identifiers assigned to this research study by the sponsor or other systems.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The business version for the study record.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Name for this study (computer friendly).
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * The human readable name of the research study.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Additional names for the study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Label} that may be empty.
     */
    public List<Label> getLabel() {
        return label;
    }

    /**
     * The set of steps expected to be performed as part of the execution of the study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getProtocol() {
        return protocol;
    }

    /**
     * A larger research study of which this particular study is a component or step.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * Citations, references, URLs and other related documents. When using relatedArtifact to share URLs, the relatedArtifact.
     * type will often be set to one of "documentation" or "supported-with" and the URL value will often be in 
     * relatedArtifact.document.url but another possible location is relatedArtifact.resource when it is a canonical URL.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
     */
    public List<RelatedArtifact> getRelatedArtifact() {
        return relatedArtifact;
    }

    /**
     * The date (and optionally time) when the ResearchStudy Resource was last significantly changed. The date must change 
     * when the business version changes and it must change if the status code changes. In addition, it should change when 
     * the substantive content of the ResearchStudy Resource changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The publication state of the resource (not of the study).
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * The type of study based upon the intent of the study activities. A classification of the intent of the study.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPrimaryPurposeType() {
        return primaryPurposeType;
    }

    /**
     * The stage in the progression of a therapy from initial experimental use in humans in clinical trials to post-market 
     * evaluation.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPhase() {
        return phase;
    }

    /**
     * Codes categorizing the type of study such as investigational vs. observational, type of blinding, type of 
     * randomization, safety vs. efficacy, etc.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getStudyDesign() {
        return studyDesign;
    }

    /**
     * The medication(s), food(s), therapy(ies), device(s) or other concerns or interventions that the study is seeking to 
     * gain more information about.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getFocus() {
        return focus;
    }

    /**
     * The condition that is the focus of the study. For example, In a study to examine risk factors for Lupus, might have as 
     * an inclusion criterion "healthy volunteer", but the target condition code would be a Lupus SNOMED code.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCondition() {
        return condition;
    }

    /**
     * Key terms to aid in searching for or filtering the study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getKeyword() {
        return keyword;
    }

    /**
     * A country, state or other area where the study is taking place rather than its precise geographic location or address.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getRegion() {
        return region;
    }

    /**
     * A brief text for explaining the study.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescriptionSummary() {
        return descriptionSummary;
    }

    /**
     * A detailed and human-readable narrative of the study. E.g., study abstract.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * Identifies the start date and the expected (or actual, depending on status) end date for the study.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * A facility in which study activities are conducted.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSite() {
        return site;
    }

    /**
     * Comments made about the study by the performer, subject or other participants.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Additional grouping mechanism or categorization of a research study. Example: FDA regulated device, FDA regulated 
     * drug, MPG Paragraph 23b (a German legal requirement), IRB-exempt, etc. Implementation Note: do not use the classifier 
     * element to support existing semantics that are already supported thru explicit elements in the resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getClassifier() {
        return classifier;
    }

    /**
     * Sponsors, collaborators, and other parties.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link AssociatedParty} that may be empty.
     */
    public List<AssociatedParty> getAssociatedParty() {
        return associatedParty;
    }

    /**
     * Status of study with time for that status.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ProgressStatus} that may be empty.
     */
    public List<ProgressStatus> getProgressStatus() {
        return progressStatus;
    }

    /**
     * A description and/or code explaining the premature termination of the study.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getWhyStopped() {
        return whyStopped;
    }

    /**
     * Target or actual group of participants enrolled in study.
     * 
     * @return
     *     An immutable object of type {@link Recruitment} that may be null.
     */
    public Recruitment getRecruitment() {
        return recruitment;
    }

    /**
     * Describes an expected event or sequence of events for one of the subjects of a study. E.g. for a living subject: 
     * exposure to drug A, wash-out, exposure to drug B, wash-out, follow-up. E.g. for a stability study: {store sample from 
     * lot A at 25 degrees for 1 month}, {store sample from lot A at 40 degrees for 1 month}.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ComparisonGroup} that may be empty.
     */
    public List<ComparisonGroup> getComparisonGroup() {
        return comparisonGroup;
    }

    /**
     * A goal that the study is aiming to achieve in terms of a scientific question to be answered by the analysis of data 
     * collected during the study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Objective} that may be empty.
     */
    public List<Objective> getObjective() {
        return objective;
    }

    /**
     * An "outcome measure", "endpoint", "effect measure" or "measure of effect" is a specific measurement or observation 
     * used to quantify the effect of experimental variables on the participants in a study, or for observational studies, to 
     * describe patterns of diseases or traits or associations with exposures, risk factors or treatment.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link OutcomeMeasure} that may be empty.
     */
    public List<OutcomeMeasure> getOutcomeMeasure() {
        return outcomeMeasure;
    }

    /**
     * Link to one or more sets of results generated by the study. Could also link to a research registry holding the results 
     * such as ClinicalTrials.gov.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getResult() {
        return result;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (url != null) || 
            !identifier.isEmpty() || 
            (version != null) || 
            (name != null) || 
            (title != null) || 
            !label.isEmpty() || 
            !protocol.isEmpty() || 
            !partOf.isEmpty() || 
            !relatedArtifact.isEmpty() || 
            (date != null) || 
            (status != null) || 
            (primaryPurposeType != null) || 
            (phase != null) || 
            !studyDesign.isEmpty() || 
            !focus.isEmpty() || 
            !condition.isEmpty() || 
            !keyword.isEmpty() || 
            !region.isEmpty() || 
            (descriptionSummary != null) || 
            (description != null) || 
            (period != null) || 
            !site.isEmpty() || 
            !note.isEmpty() || 
            !classifier.isEmpty() || 
            !associatedParty.isEmpty() || 
            !progressStatus.isEmpty() || 
            (whyStopped != null) || 
            (recruitment != null) || 
            !comparisonGroup.isEmpty() || 
            !objective.isEmpty() || 
            !outcomeMeasure.isEmpty() || 
            !result.isEmpty();
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
                accept(name, "name", visitor);
                accept(title, "title", visitor);
                accept(label, "label", visitor, Label.class);
                accept(protocol, "protocol", visitor, Reference.class);
                accept(partOf, "partOf", visitor, Reference.class);
                accept(relatedArtifact, "relatedArtifact", visitor, RelatedArtifact.class);
                accept(date, "date", visitor);
                accept(status, "status", visitor);
                accept(primaryPurposeType, "primaryPurposeType", visitor);
                accept(phase, "phase", visitor);
                accept(studyDesign, "studyDesign", visitor, CodeableConcept.class);
                accept(focus, "focus", visitor, CodeableReference.class);
                accept(condition, "condition", visitor, CodeableConcept.class);
                accept(keyword, "keyword", visitor, CodeableConcept.class);
                accept(region, "region", visitor, CodeableConcept.class);
                accept(descriptionSummary, "descriptionSummary", visitor);
                accept(description, "description", visitor);
                accept(period, "period", visitor);
                accept(site, "site", visitor, Reference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(classifier, "classifier", visitor, CodeableConcept.class);
                accept(associatedParty, "associatedParty", visitor, AssociatedParty.class);
                accept(progressStatus, "progressStatus", visitor, ProgressStatus.class);
                accept(whyStopped, "whyStopped", visitor);
                accept(recruitment, "recruitment", visitor);
                accept(comparisonGroup, "comparisonGroup", visitor, ComparisonGroup.class);
                accept(objective, "objective", visitor, Objective.class);
                accept(outcomeMeasure, "outcomeMeasure", visitor, OutcomeMeasure.class);
                accept(result, "result", visitor, Reference.class);
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
        ResearchStudy other = (ResearchStudy) obj;
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
            Objects.equals(name, other.name) && 
            Objects.equals(title, other.title) && 
            Objects.equals(label, other.label) && 
            Objects.equals(protocol, other.protocol) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(relatedArtifact, other.relatedArtifact) && 
            Objects.equals(date, other.date) && 
            Objects.equals(status, other.status) && 
            Objects.equals(primaryPurposeType, other.primaryPurposeType) && 
            Objects.equals(phase, other.phase) && 
            Objects.equals(studyDesign, other.studyDesign) && 
            Objects.equals(focus, other.focus) && 
            Objects.equals(condition, other.condition) && 
            Objects.equals(keyword, other.keyword) && 
            Objects.equals(region, other.region) && 
            Objects.equals(descriptionSummary, other.descriptionSummary) && 
            Objects.equals(description, other.description) && 
            Objects.equals(period, other.period) && 
            Objects.equals(site, other.site) && 
            Objects.equals(note, other.note) && 
            Objects.equals(classifier, other.classifier) && 
            Objects.equals(associatedParty, other.associatedParty) && 
            Objects.equals(progressStatus, other.progressStatus) && 
            Objects.equals(whyStopped, other.whyStopped) && 
            Objects.equals(recruitment, other.recruitment) && 
            Objects.equals(comparisonGroup, other.comparisonGroup) && 
            Objects.equals(objective, other.objective) && 
            Objects.equals(outcomeMeasure, other.outcomeMeasure) && 
            Objects.equals(result, other.result);
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
                name, 
                title, 
                label, 
                protocol, 
                partOf, 
                relatedArtifact, 
                date, 
                status, 
                primaryPurposeType, 
                phase, 
                studyDesign, 
                focus, 
                condition, 
                keyword, 
                region, 
                descriptionSummary, 
                description, 
                period, 
                site, 
                note, 
                classifier, 
                associatedParty, 
                progressStatus, 
                whyStopped, 
                recruitment, 
                comparisonGroup, 
                objective, 
                outcomeMeasure, 
                this.result);
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
        private String name;
        private String title;
        private List<Label> label = new ArrayList<>();
        private List<Reference> protocol = new ArrayList<>();
        private List<Reference> partOf = new ArrayList<>();
        private List<RelatedArtifact> relatedArtifact = new ArrayList<>();
        private DateTime date;
        private PublicationStatus status;
        private CodeableConcept primaryPurposeType;
        private CodeableConcept phase;
        private List<CodeableConcept> studyDesign = new ArrayList<>();
        private List<CodeableReference> focus = new ArrayList<>();
        private List<CodeableConcept> condition = new ArrayList<>();
        private List<CodeableConcept> keyword = new ArrayList<>();
        private List<CodeableConcept> region = new ArrayList<>();
        private Markdown descriptionSummary;
        private Markdown description;
        private Period period;
        private List<Reference> site = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<CodeableConcept> classifier = new ArrayList<>();
        private List<AssociatedParty> associatedParty = new ArrayList<>();
        private List<ProgressStatus> progressStatus = new ArrayList<>();
        private CodeableConcept whyStopped;
        private Recruitment recruitment;
        private List<ComparisonGroup> comparisonGroup = new ArrayList<>();
        private List<Objective> objective = new ArrayList<>();
        private List<OutcomeMeasure> outcomeMeasure = new ArrayList<>();
        private List<Reference> result = new ArrayList<>();

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
         * Canonical identifier for this study resource, represented as a globally unique URI.
         * 
         * @param url
         *     Canonical identifier for this study resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * Identifiers assigned to this research study by the sponsor or other systems.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for study
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
         * Identifiers assigned to this research study by the sponsor or other systems.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Business Identifier for study
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
         *     The business version for the study record
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
         * The business version for the study record.
         * 
         * @param version
         *     The business version for the study record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Name for this study (computer friendly)
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
         * Name for this study (computer friendly).
         * 
         * @param name
         *     Name for this study (computer friendly)
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
         *     Human readable name of the study
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
         * The human readable name of the research study.
         * 
         * @param title
         *     Human readable name of the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Additional names for the study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param label
         *     Additional names for the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder label(Label... label) {
            for (Label value : label) {
                this.label.add(value);
            }
            return this;
        }

        /**
         * Additional names for the study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param label
         *     Additional names for the study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder label(Collection<Label> label) {
            this.label = new ArrayList<>(label);
            return this;
        }

        /**
         * The set of steps expected to be performed as part of the execution of the study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link PlanDefinition}</li>
         * </ul>
         * 
         * @param protocol
         *     Steps followed in executing study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder protocol(Reference... protocol) {
            for (Reference value : protocol) {
                this.protocol.add(value);
            }
            return this;
        }

        /**
         * The set of steps expected to be performed as part of the execution of the study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link PlanDefinition}</li>
         * </ul>
         * 
         * @param protocol
         *     Steps followed in executing study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder protocol(Collection<Reference> protocol) {
            this.protocol = new ArrayList<>(protocol);
            return this;
        }

        /**
         * A larger research study of which this particular study is a component or step.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ResearchStudy}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of larger study
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
         * A larger research study of which this particular study is a component or step.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ResearchStudy}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of larger study
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
         * Citations, references, URLs and other related documents. When using relatedArtifact to share URLs, the relatedArtifact.
         * type will often be set to one of "documentation" or "supported-with" and the URL value will often be in 
         * relatedArtifact.document.url but another possible location is relatedArtifact.resource when it is a canonical URL.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedArtifact
         *     References, URLs, and attachments
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
         * Citations, references, URLs and other related documents. When using relatedArtifact to share URLs, the relatedArtifact.
         * type will often be set to one of "documentation" or "supported-with" and the URL value will often be in 
         * relatedArtifact.document.url but another possible location is relatedArtifact.resource when it is a canonical URL.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedArtifact
         *     References, URLs, and attachments
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
         * The date (and optionally time) when the ResearchStudy Resource was last significantly changed. The date must change 
         * when the business version changes and it must change if the status code changes. In addition, it should change when 
         * the substantive content of the ResearchStudy Resource changes.
         * 
         * @param date
         *     Date the resource last changed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(DateTime date) {
            this.date = date;
            return this;
        }

        /**
         * The publication state of the resource (not of the study).
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
         * The type of study based upon the intent of the study activities. A classification of the intent of the study.
         * 
         * @param primaryPurposeType
         *     treatment | prevention | diagnostic | supportive-care | screening | health-services-research | basic-science | device-
         *     feasibility
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder primaryPurposeType(CodeableConcept primaryPurposeType) {
            this.primaryPurposeType = primaryPurposeType;
            return this;
        }

        /**
         * The stage in the progression of a therapy from initial experimental use in humans in clinical trials to post-market 
         * evaluation.
         * 
         * @param phase
         *     n-a | early-phase-1 | phase-1 | phase-1-phase-2 | phase-2 | phase-2-phase-3 | phase-3 | phase-4
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder phase(CodeableConcept phase) {
            this.phase = phase;
            return this;
        }

        /**
         * Codes categorizing the type of study such as investigational vs. observational, type of blinding, type of 
         * randomization, safety vs. efficacy, etc.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param studyDesign
         *     Classifications of the study design characteristics
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder studyDesign(CodeableConcept... studyDesign) {
            for (CodeableConcept value : studyDesign) {
                this.studyDesign.add(value);
            }
            return this;
        }

        /**
         * Codes categorizing the type of study such as investigational vs. observational, type of blinding, type of 
         * randomization, safety vs. efficacy, etc.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param studyDesign
         *     Classifications of the study design characteristics
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder studyDesign(Collection<CodeableConcept> studyDesign) {
            this.studyDesign = new ArrayList<>(studyDesign);
            return this;
        }

        /**
         * The medication(s), food(s), therapy(ies), device(s) or other concerns or interventions that the study is seeking to 
         * gain more information about.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focus
         *     Drugs, devices, etc. under study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder focus(CodeableReference... focus) {
            for (CodeableReference value : focus) {
                this.focus.add(value);
            }
            return this;
        }

        /**
         * The medication(s), food(s), therapy(ies), device(s) or other concerns or interventions that the study is seeking to 
         * gain more information about.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param focus
         *     Drugs, devices, etc. under study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder focus(Collection<CodeableReference> focus) {
            this.focus = new ArrayList<>(focus);
            return this;
        }

        /**
         * The condition that is the focus of the study. For example, In a study to examine risk factors for Lupus, might have as 
         * an inclusion criterion "healthy volunteer", but the target condition code would be a Lupus SNOMED code.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param condition
         *     Condition being studied
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder condition(CodeableConcept... condition) {
            for (CodeableConcept value : condition) {
                this.condition.add(value);
            }
            return this;
        }

        /**
         * The condition that is the focus of the study. For example, In a study to examine risk factors for Lupus, might have as 
         * an inclusion criterion "healthy volunteer", but the target condition code would be a Lupus SNOMED code.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param condition
         *     Condition being studied
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder condition(Collection<CodeableConcept> condition) {
            this.condition = new ArrayList<>(condition);
            return this;
        }

        /**
         * Key terms to aid in searching for or filtering the study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param keyword
         *     Used to search for the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder keyword(CodeableConcept... keyword) {
            for (CodeableConcept value : keyword) {
                this.keyword.add(value);
            }
            return this;
        }

        /**
         * Key terms to aid in searching for or filtering the study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param keyword
         *     Used to search for the study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder keyword(Collection<CodeableConcept> keyword) {
            this.keyword = new ArrayList<>(keyword);
            return this;
        }

        /**
         * A country, state or other area where the study is taking place rather than its precise geographic location or address.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param region
         *     Geographic area for the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder region(CodeableConcept... region) {
            for (CodeableConcept value : region) {
                this.region.add(value);
            }
            return this;
        }

        /**
         * A country, state or other area where the study is taking place rather than its precise geographic location or address.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param region
         *     Geographic area for the study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder region(Collection<CodeableConcept> region) {
            this.region = new ArrayList<>(region);
            return this;
        }

        /**
         * A brief text for explaining the study.
         * 
         * @param descriptionSummary
         *     Brief text explaining the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder descriptionSummary(Markdown descriptionSummary) {
            this.descriptionSummary = descriptionSummary;
            return this;
        }

        /**
         * A detailed and human-readable narrative of the study. E.g., study abstract.
         * 
         * @param description
         *     Detailed narrative of the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * Identifies the start date and the expected (or actual, depending on status) end date for the study.
         * 
         * @param period
         *     When the study began and ended
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * A facility in which study activities are conducted.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Location}</li>
         * <li>{@link ResearchStudy}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param site
         *     Facility where study activities are conducted
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder site(Reference... site) {
            for (Reference value : site) {
                this.site.add(value);
            }
            return this;
        }

        /**
         * A facility in which study activities are conducted.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Location}</li>
         * <li>{@link ResearchStudy}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param site
         *     Facility where study activities are conducted
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder site(Collection<Reference> site) {
            this.site = new ArrayList<>(site);
            return this;
        }

        /**
         * Comments made about the study by the performer, subject or other participants.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments made about the study
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
         * Comments made about the study by the performer, subject or other participants.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Comments made about the study
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
         * Additional grouping mechanism or categorization of a research study. Example: FDA regulated device, FDA regulated 
         * drug, MPG Paragraph 23b (a German legal requirement), IRB-exempt, etc. Implementation Note: do not use the classifier 
         * element to support existing semantics that are already supported thru explicit elements in the resource.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param classifier
         *     Classification for the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder classifier(CodeableConcept... classifier) {
            for (CodeableConcept value : classifier) {
                this.classifier.add(value);
            }
            return this;
        }

        /**
         * Additional grouping mechanism or categorization of a research study. Example: FDA regulated device, FDA regulated 
         * drug, MPG Paragraph 23b (a German legal requirement), IRB-exempt, etc. Implementation Note: do not use the classifier 
         * element to support existing semantics that are already supported thru explicit elements in the resource.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param classifier
         *     Classification for the study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder classifier(Collection<CodeableConcept> classifier) {
            this.classifier = new ArrayList<>(classifier);
            return this;
        }

        /**
         * Sponsors, collaborators, and other parties.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param associatedParty
         *     Sponsors, collaborators, and other parties
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder associatedParty(AssociatedParty... associatedParty) {
            for (AssociatedParty value : associatedParty) {
                this.associatedParty.add(value);
            }
            return this;
        }

        /**
         * Sponsors, collaborators, and other parties.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param associatedParty
         *     Sponsors, collaborators, and other parties
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder associatedParty(Collection<AssociatedParty> associatedParty) {
            this.associatedParty = new ArrayList<>(associatedParty);
            return this;
        }

        /**
         * Status of study with time for that status.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param progressStatus
         *     Status of study with time for that status
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder progressStatus(ProgressStatus... progressStatus) {
            for (ProgressStatus value : progressStatus) {
                this.progressStatus.add(value);
            }
            return this;
        }

        /**
         * Status of study with time for that status.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param progressStatus
         *     Status of study with time for that status
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder progressStatus(Collection<ProgressStatus> progressStatus) {
            this.progressStatus = new ArrayList<>(progressStatus);
            return this;
        }

        /**
         * A description and/or code explaining the premature termination of the study.
         * 
         * @param whyStopped
         *     accrual-goal-met | closed-due-to-toxicity | closed-due-to-lack-of-study-progress | temporarily-closed-per-study-design
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder whyStopped(CodeableConcept whyStopped) {
            this.whyStopped = whyStopped;
            return this;
        }

        /**
         * Target or actual group of participants enrolled in study.
         * 
         * @param recruitment
         *     Target or actual group of participants enrolled in study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder recruitment(Recruitment recruitment) {
            this.recruitment = recruitment;
            return this;
        }

        /**
         * Describes an expected event or sequence of events for one of the subjects of a study. E.g. for a living subject: 
         * exposure to drug A, wash-out, exposure to drug B, wash-out, follow-up. E.g. for a stability study: {store sample from 
         * lot A at 25 degrees for 1 month}, {store sample from lot A at 40 degrees for 1 month}.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param comparisonGroup
         *     Defined path through the study for a subject
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder comparisonGroup(ComparisonGroup... comparisonGroup) {
            for (ComparisonGroup value : comparisonGroup) {
                this.comparisonGroup.add(value);
            }
            return this;
        }

        /**
         * Describes an expected event or sequence of events for one of the subjects of a study. E.g. for a living subject: 
         * exposure to drug A, wash-out, exposure to drug B, wash-out, follow-up. E.g. for a stability study: {store sample from 
         * lot A at 25 degrees for 1 month}, {store sample from lot A at 40 degrees for 1 month}.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param comparisonGroup
         *     Defined path through the study for a subject
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder comparisonGroup(Collection<ComparisonGroup> comparisonGroup) {
            this.comparisonGroup = new ArrayList<>(comparisonGroup);
            return this;
        }

        /**
         * A goal that the study is aiming to achieve in terms of a scientific question to be answered by the analysis of data 
         * collected during the study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param objective
         *     A goal for the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder objective(Objective... objective) {
            for (Objective value : objective) {
                this.objective.add(value);
            }
            return this;
        }

        /**
         * A goal that the study is aiming to achieve in terms of a scientific question to be answered by the analysis of data 
         * collected during the study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param objective
         *     A goal for the study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder objective(Collection<Objective> objective) {
            this.objective = new ArrayList<>(objective);
            return this;
        }

        /**
         * An "outcome measure", "endpoint", "effect measure" or "measure of effect" is a specific measurement or observation 
         * used to quantify the effect of experimental variables on the participants in a study, or for observational studies, to 
         * describe patterns of diseases or traits or associations with exposures, risk factors or treatment.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param outcomeMeasure
         *     A variable measured during the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder outcomeMeasure(OutcomeMeasure... outcomeMeasure) {
            for (OutcomeMeasure value : outcomeMeasure) {
                this.outcomeMeasure.add(value);
            }
            return this;
        }

        /**
         * An "outcome measure", "endpoint", "effect measure" or "measure of effect" is a specific measurement or observation 
         * used to quantify the effect of experimental variables on the participants in a study, or for observational studies, to 
         * describe patterns of diseases or traits or associations with exposures, risk factors or treatment.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param outcomeMeasure
         *     A variable measured during the study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder outcomeMeasure(Collection<OutcomeMeasure> outcomeMeasure) {
            this.outcomeMeasure = new ArrayList<>(outcomeMeasure);
            return this;
        }

        /**
         * Link to one or more sets of results generated by the study. Could also link to a research registry holding the results 
         * such as ClinicalTrials.gov.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link EvidenceReport}</li>
         * <li>{@link Citation}</li>
         * <li>{@link DiagnosticReport}</li>
         * </ul>
         * 
         * @param result
         *     Link to results generated during the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder result(Reference... result) {
            for (Reference value : result) {
                this.result.add(value);
            }
            return this;
        }

        /**
         * Link to one or more sets of results generated by the study. Could also link to a research registry holding the results 
         * such as ClinicalTrials.gov.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link EvidenceReport}</li>
         * <li>{@link Citation}</li>
         * <li>{@link DiagnosticReport}</li>
         * </ul>
         * 
         * @param result
         *     Link to results generated during the study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder result(Collection<Reference> result) {
            this.result = new ArrayList<>(result);
            return this;
        }

        /**
         * Build the {@link ResearchStudy}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ResearchStudy}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ResearchStudy per the base specification
         */
        @Override
        public ResearchStudy build() {
            ResearchStudy researchStudy = new ResearchStudy(this);
            if (validating) {
                validate(researchStudy);
            }
            return researchStudy;
        }

        protected void validate(ResearchStudy researchStudy) {
            super.validate(researchStudy);
            ValidationSupport.checkList(researchStudy.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(researchStudy.label, "label", Label.class);
            ValidationSupport.checkList(researchStudy.protocol, "protocol", Reference.class);
            ValidationSupport.checkList(researchStudy.partOf, "partOf", Reference.class);
            ValidationSupport.checkList(researchStudy.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
            ValidationSupport.requireNonNull(researchStudy.status, "status");
            ValidationSupport.checkList(researchStudy.studyDesign, "studyDesign", CodeableConcept.class);
            ValidationSupport.checkList(researchStudy.focus, "focus", CodeableReference.class);
            ValidationSupport.checkList(researchStudy.condition, "condition", CodeableConcept.class);
            ValidationSupport.checkList(researchStudy.keyword, "keyword", CodeableConcept.class);
            ValidationSupport.checkList(researchStudy.region, "region", CodeableConcept.class);
            ValidationSupport.checkList(researchStudy.site, "site", Reference.class);
            ValidationSupport.checkList(researchStudy.note, "note", Annotation.class);
            ValidationSupport.checkList(researchStudy.classifier, "classifier", CodeableConcept.class);
            ValidationSupport.checkList(researchStudy.associatedParty, "associatedParty", AssociatedParty.class);
            ValidationSupport.checkList(researchStudy.progressStatus, "progressStatus", ProgressStatus.class);
            ValidationSupport.checkList(researchStudy.comparisonGroup, "comparisonGroup", ComparisonGroup.class);
            ValidationSupport.checkList(researchStudy.objective, "objective", Objective.class);
            ValidationSupport.checkList(researchStudy.outcomeMeasure, "outcomeMeasure", OutcomeMeasure.class);
            ValidationSupport.checkList(researchStudy.result, "result", Reference.class);
            ValidationSupport.checkReferenceType(researchStudy.protocol, "protocol", "PlanDefinition");
            ValidationSupport.checkReferenceType(researchStudy.partOf, "partOf", "ResearchStudy");
            ValidationSupport.checkReferenceType(researchStudy.site, "site", "Location", "ResearchStudy", "Organization");
            ValidationSupport.checkReferenceType(researchStudy.result, "result", "EvidenceReport", "Citation", "DiagnosticReport");
        }

        protected Builder from(ResearchStudy researchStudy) {
            super.from(researchStudy);
            url = researchStudy.url;
            identifier.addAll(researchStudy.identifier);
            version = researchStudy.version;
            name = researchStudy.name;
            title = researchStudy.title;
            label.addAll(researchStudy.label);
            protocol.addAll(researchStudy.protocol);
            partOf.addAll(researchStudy.partOf);
            relatedArtifact.addAll(researchStudy.relatedArtifact);
            date = researchStudy.date;
            status = researchStudy.status;
            primaryPurposeType = researchStudy.primaryPurposeType;
            phase = researchStudy.phase;
            studyDesign.addAll(researchStudy.studyDesign);
            focus.addAll(researchStudy.focus);
            condition.addAll(researchStudy.condition);
            keyword.addAll(researchStudy.keyword);
            region.addAll(researchStudy.region);
            descriptionSummary = researchStudy.descriptionSummary;
            description = researchStudy.description;
            period = researchStudy.period;
            site.addAll(researchStudy.site);
            note.addAll(researchStudy.note);
            classifier.addAll(researchStudy.classifier);
            associatedParty.addAll(researchStudy.associatedParty);
            progressStatus.addAll(researchStudy.progressStatus);
            whyStopped = researchStudy.whyStopped;
            recruitment = researchStudy.recruitment;
            comparisonGroup.addAll(researchStudy.comparisonGroup);
            objective.addAll(researchStudy.objective);
            outcomeMeasure.addAll(researchStudy.outcomeMeasure);
            result.addAll(researchStudy.result);
            return this;
        }
    }

    /**
     * Additional names for the study.
     */
    public static class Label extends BackboneElement {
        @Binding(
            bindingName = "TitleType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "desc.",
            valueSet = "http://hl7.org/fhir/ValueSet/title-type"
        )
        private final CodeableConcept type;
        private final String value;

        private Label(Builder builder) {
            super(builder);
            type = builder.type;
            value = builder.value;
        }

        /**
         * Kind of name.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The name.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
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
                    accept(type, "type", visitor);
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
            Label other = (Label) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
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
            private CodeableConcept type;
            private String value;

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
             * Kind of name.
             * 
             * @param type
             *     primary | official | scientific | plain-language | subtitle | short-title | acronym | earlier-title | language | auto-
             *     translated | human-use | machine-use | duplicate-uid
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code value}.
             * 
             * @param value
             *     The name
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder value(java.lang.String value) {
                this.value = (value == null) ? null : String.of(value);
                return this;
            }

            /**
             * The name.
             * 
             * @param value
             *     The name
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(String value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Label}
             * 
             * @return
             *     An immutable object of type {@link Label}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Label per the base specification
             */
            @Override
            public Label build() {
                Label label = new Label(this);
                if (validating) {
                    validate(label);
                }
                return label;
            }

            protected void validate(Label label) {
                super.validate(label);
                ValidationSupport.requireValueOrChildren(label);
            }

            protected Builder from(Label label) {
                super.from(label);
                type = label.type;
                value = label.value;
                return this;
            }
        }
    }

    /**
     * Sponsors, collaborators, and other parties.
     */
    public static class AssociatedParty extends BackboneElement {
        private final String name;
        @Binding(
            bindingName = "ResearchStudyPartyRole",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "desc.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-study-party-role"
        )
        @Required
        private final CodeableConcept role;
        private final List<Period> period;
        @Binding(
            bindingName = "ResearchStudyPartyOrganizationType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A characterization or type of the entity.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-study-party-organization-type"
        )
        private final List<CodeableConcept> classifier;
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization" })
        private final Reference party;

        private AssociatedParty(Builder builder) {
            super(builder);
            name = builder.name;
            role = builder.role;
            period = Collections.unmodifiableList(builder.period);
            classifier = Collections.unmodifiableList(builder.classifier);
            party = builder.party;
        }

        /**
         * Name of associated party.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getName() {
            return name;
        }

        /**
         * Type of association.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getRole() {
            return role;
        }

        /**
         * Identifies the start date and the end date of the associated party in the role.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Period} that may be empty.
         */
        public List<Period> getPeriod() {
            return period;
        }

        /**
         * A categorization other than role for the associated party.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getClassifier() {
            return classifier;
        }

        /**
         * Individual or organization associated with study (use practitionerRole to specify their organisation).
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getParty() {
            return party;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (name != null) || 
                (role != null) || 
                !period.isEmpty() || 
                !classifier.isEmpty() || 
                (party != null);
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
                    accept(name, "name", visitor);
                    accept(role, "role", visitor);
                    accept(period, "period", visitor, Period.class);
                    accept(classifier, "classifier", visitor, CodeableConcept.class);
                    accept(party, "party", visitor);
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
            AssociatedParty other = (AssociatedParty) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(name, other.name) && 
                Objects.equals(role, other.role) && 
                Objects.equals(period, other.period) && 
                Objects.equals(classifier, other.classifier) && 
                Objects.equals(party, other.party);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    name, 
                    role, 
                    period, 
                    classifier, 
                    party);
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
            private String name;
            private CodeableConcept role;
            private List<Period> period = new ArrayList<>();
            private List<CodeableConcept> classifier = new ArrayList<>();
            private Reference party;

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
             * Convenience method for setting {@code name}.
             * 
             * @param name
             *     Name of associated party
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
             * Name of associated party.
             * 
             * @param name
             *     Name of associated party
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Type of association.
             * 
             * <p>This element is required.
             * 
             * @param role
             *     sponsor | lead-sponsor | sponsor-investigator | primary-investigator | collaborator | funding-source | general-contact 
             *     | recruitment-contact | sub-investigator | study-director | study-chair
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder role(CodeableConcept role) {
                this.role = role;
                return this;
            }

            /**
             * Identifies the start date and the end date of the associated party in the role.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param period
             *     When active in the role
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period... period) {
                for (Period value : period) {
                    this.period.add(value);
                }
                return this;
            }

            /**
             * Identifies the start date and the end date of the associated party in the role.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param period
             *     When active in the role
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder period(Collection<Period> period) {
                this.period = new ArrayList<>(period);
                return this;
            }

            /**
             * A categorization other than role for the associated party.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classifier
             *     nih | fda | government | nonprofit | academic | industry
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder classifier(CodeableConcept... classifier) {
                for (CodeableConcept value : classifier) {
                    this.classifier.add(value);
                }
                return this;
            }

            /**
             * A categorization other than role for the associated party.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classifier
             *     nih | fda | government | nonprofit | academic | industry
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder classifier(Collection<CodeableConcept> classifier) {
                this.classifier = new ArrayList<>(classifier);
                return this;
            }

            /**
             * Individual or organization associated with study (use practitionerRole to specify their organisation).
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param party
             *     Individual or organization associated with study (use practitionerRole to specify their organisation)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder party(Reference party) {
                this.party = party;
                return this;
            }

            /**
             * Build the {@link AssociatedParty}
             * 
             * <p>Required elements:
             * <ul>
             * <li>role</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link AssociatedParty}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid AssociatedParty per the base specification
             */
            @Override
            public AssociatedParty build() {
                AssociatedParty associatedParty = new AssociatedParty(this);
                if (validating) {
                    validate(associatedParty);
                }
                return associatedParty;
            }

            protected void validate(AssociatedParty associatedParty) {
                super.validate(associatedParty);
                ValidationSupport.requireNonNull(associatedParty.role, "role");
                ValidationSupport.checkList(associatedParty.period, "period", Period.class);
                ValidationSupport.checkList(associatedParty.classifier, "classifier", CodeableConcept.class);
                ValidationSupport.checkReferenceType(associatedParty.party, "party", "Practitioner", "PractitionerRole", "Organization");
                ValidationSupport.requireValueOrChildren(associatedParty);
            }

            protected Builder from(AssociatedParty associatedParty) {
                super.from(associatedParty);
                name = associatedParty.name;
                role = associatedParty.role;
                period.addAll(associatedParty.period);
                classifier.addAll(associatedParty.classifier);
                party = associatedParty.party;
                return this;
            }
        }
    }

    /**
     * Status of study with time for that status.
     */
    public static class ProgressStatus extends BackboneElement {
        @Binding(
            bindingName = "ResearchStudyStudyStatus",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "defn.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-study-status"
        )
        @Required
        private final CodeableConcept state;
        private final Boolean actual;
        private final Period period;

        private ProgressStatus(Builder builder) {
            super(builder);
            state = builder.state;
            actual = builder.actual;
            period = builder.period;
        }

        /**
         * Label for status or state (e.g. recruitment status).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getState() {
            return state;
        }

        /**
         * An indication of whether or not the date is a known date when the state changed or will change. A value of true 
         * indicates a known date. A value of false indicates an estimated date.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getActual() {
            return actual;
        }

        /**
         * Date range.
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
                (state != null) || 
                (actual != null) || 
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
                    accept(state, "state", visitor);
                    accept(actual, "actual", visitor);
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
            ProgressStatus other = (ProgressStatus) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(state, other.state) && 
                Objects.equals(actual, other.actual) && 
                Objects.equals(period, other.period);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    state, 
                    actual, 
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
            private CodeableConcept state;
            private Boolean actual;
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
             * Label for status or state (e.g. recruitment status).
             * 
             * <p>This element is required.
             * 
             * @param state
             *     Label for status or state (e.g. recruitment status)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder state(CodeableConcept state) {
                this.state = state;
                return this;
            }

            /**
             * Convenience method for setting {@code actual}.
             * 
             * @param actual
             *     Actual if true else anticipated
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #actual(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder actual(java.lang.Boolean actual) {
                this.actual = (actual == null) ? null : Boolean.of(actual);
                return this;
            }

            /**
             * An indication of whether or not the date is a known date when the state changed or will change. A value of true 
             * indicates a known date. A value of false indicates an estimated date.
             * 
             * @param actual
             *     Actual if true else anticipated
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actual(Boolean actual) {
                this.actual = actual;
                return this;
            }

            /**
             * Date range.
             * 
             * @param period
             *     Date range
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link ProgressStatus}
             * 
             * <p>Required elements:
             * <ul>
             * <li>state</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ProgressStatus}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ProgressStatus per the base specification
             */
            @Override
            public ProgressStatus build() {
                ProgressStatus progressStatus = new ProgressStatus(this);
                if (validating) {
                    validate(progressStatus);
                }
                return progressStatus;
            }

            protected void validate(ProgressStatus progressStatus) {
                super.validate(progressStatus);
                ValidationSupport.requireNonNull(progressStatus.state, "state");
                ValidationSupport.requireValueOrChildren(progressStatus);
            }

            protected Builder from(ProgressStatus progressStatus) {
                super.from(progressStatus);
                state = progressStatus.state;
                actual = progressStatus.actual;
                period = progressStatus.period;
                return this;
            }
        }
    }

    /**
     * Target or actual group of participants enrolled in study.
     */
    public static class Recruitment extends BackboneElement {
        private final UnsignedInt targetNumber;
        private final UnsignedInt actualNumber;
        @ReferenceTarget({ "Group", "EvidenceVariable" })
        private final Reference eligibility;
        @Summary
        @ReferenceTarget({ "Group" })
        private final Reference actualGroup;

        private Recruitment(Builder builder) {
            super(builder);
            targetNumber = builder.targetNumber;
            actualNumber = builder.actualNumber;
            eligibility = builder.eligibility;
            actualGroup = builder.actualGroup;
        }

        /**
         * Estimated total number of participants to be enrolled.
         * 
         * @return
         *     An immutable object of type {@link UnsignedInt} that may be null.
         */
        public UnsignedInt getTargetNumber() {
            return targetNumber;
        }

        /**
         * Actual total number of participants enrolled in study.
         * 
         * @return
         *     An immutable object of type {@link UnsignedInt} that may be null.
         */
        public UnsignedInt getActualNumber() {
            return actualNumber;
        }

        /**
         * Inclusion and exclusion criteria.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getEligibility() {
            return eligibility;
        }

        /**
         * Group of participants who were enrolled in study.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getActualGroup() {
            return actualGroup;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (targetNumber != null) || 
                (actualNumber != null) || 
                (eligibility != null) || 
                (actualGroup != null);
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
                    accept(targetNumber, "targetNumber", visitor);
                    accept(actualNumber, "actualNumber", visitor);
                    accept(eligibility, "eligibility", visitor);
                    accept(actualGroup, "actualGroup", visitor);
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
            Recruitment other = (Recruitment) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(targetNumber, other.targetNumber) && 
                Objects.equals(actualNumber, other.actualNumber) && 
                Objects.equals(eligibility, other.eligibility) && 
                Objects.equals(actualGroup, other.actualGroup);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    targetNumber, 
                    actualNumber, 
                    eligibility, 
                    actualGroup);
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
            private UnsignedInt targetNumber;
            private UnsignedInt actualNumber;
            private Reference eligibility;
            private Reference actualGroup;

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
             * Estimated total number of participants to be enrolled.
             * 
             * @param targetNumber
             *     Estimated total number of participants to be enrolled
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder targetNumber(UnsignedInt targetNumber) {
                this.targetNumber = targetNumber;
                return this;
            }

            /**
             * Actual total number of participants enrolled in study.
             * 
             * @param actualNumber
             *     Actual total number of participants enrolled in study
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actualNumber(UnsignedInt actualNumber) {
                this.actualNumber = actualNumber;
                return this;
            }

            /**
             * Inclusion and exclusion criteria.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Group}</li>
             * <li>{@link EvidenceVariable}</li>
             * </ul>
             * 
             * @param eligibility
             *     Inclusion and exclusion criteria
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder eligibility(Reference eligibility) {
                this.eligibility = eligibility;
                return this;
            }

            /**
             * Group of participants who were enrolled in study.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Group}</li>
             * </ul>
             * 
             * @param actualGroup
             *     Group of participants who were enrolled in study
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actualGroup(Reference actualGroup) {
                this.actualGroup = actualGroup;
                return this;
            }

            /**
             * Build the {@link Recruitment}
             * 
             * @return
             *     An immutable object of type {@link Recruitment}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Recruitment per the base specification
             */
            @Override
            public Recruitment build() {
                Recruitment recruitment = new Recruitment(this);
                if (validating) {
                    validate(recruitment);
                }
                return recruitment;
            }

            protected void validate(Recruitment recruitment) {
                super.validate(recruitment);
                ValidationSupport.checkReferenceType(recruitment.eligibility, "eligibility", "Group", "EvidenceVariable");
                ValidationSupport.checkReferenceType(recruitment.actualGroup, "actualGroup", "Group");
                ValidationSupport.requireValueOrChildren(recruitment);
            }

            protected Builder from(Recruitment recruitment) {
                super.from(recruitment);
                targetNumber = recruitment.targetNumber;
                actualNumber = recruitment.actualNumber;
                eligibility = recruitment.eligibility;
                actualGroup = recruitment.actualGroup;
                return this;
            }
        }
    }

    /**
     * Describes an expected event or sequence of events for one of the subjects of a study. E.g. for a living subject: 
     * exposure to drug A, wash-out, exposure to drug B, wash-out, follow-up. E.g. for a stability study: {store sample from 
     * lot A at 25 degrees for 1 month}, {store sample from lot A at 40 degrees for 1 month}.
     */
    public static class ComparisonGroup extends BackboneElement {
        private final Id linkId;
        @Required
        private final String name;
        @Binding(
            bindingName = "ResearchStudyArmType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "desc.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-study-arm-type"
        )
        private final CodeableConcept type;
        private final Markdown description;
        @ReferenceTarget({ "EvidenceVariable" })
        private final List<Reference> intendedExposure;
        @ReferenceTarget({ "Group" })
        private final Reference observedGroup;

        private ComparisonGroup(Builder builder) {
            super(builder);
            linkId = builder.linkId;
            name = builder.name;
            type = builder.type;
            description = builder.description;
            intendedExposure = Collections.unmodifiableList(builder.intendedExposure);
            observedGroup = builder.observedGroup;
        }

        /**
         * Allows the comparisonGroup for the study and the comparisonGroup for the subject to be linked easily.
         * 
         * @return
         *     An immutable object of type {@link Id} that may be null.
         */
        public Id getLinkId() {
            return linkId;
        }

        /**
         * Unique, human-readable label for this comparisonGroup of the study.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getName() {
            return name;
        }

        /**
         * Categorization of study comparisonGroup, e.g. experimental, active comparator, placebo comparater.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * A succinct description of the path through the study that would be followed by a subject adhering to this 
         * comparisonGroup.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * Interventions or exposures in this comparisonGroup or cohort.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getIntendedExposure() {
            return intendedExposure;
        }

        /**
         * Group of participants who were enrolled in study comparisonGroup.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getObservedGroup() {
            return observedGroup;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (linkId != null) || 
                (name != null) || 
                (type != null) || 
                (description != null) || 
                !intendedExposure.isEmpty() || 
                (observedGroup != null);
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
                    accept(name, "name", visitor);
                    accept(type, "type", visitor);
                    accept(description, "description", visitor);
                    accept(intendedExposure, "intendedExposure", visitor, Reference.class);
                    accept(observedGroup, "observedGroup", visitor);
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
            ComparisonGroup other = (ComparisonGroup) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(linkId, other.linkId) && 
                Objects.equals(name, other.name) && 
                Objects.equals(type, other.type) && 
                Objects.equals(description, other.description) && 
                Objects.equals(intendedExposure, other.intendedExposure) && 
                Objects.equals(observedGroup, other.observedGroup);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    linkId, 
                    name, 
                    type, 
                    description, 
                    intendedExposure, 
                    observedGroup);
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
            private Id linkId;
            private String name;
            private CodeableConcept type;
            private Markdown description;
            private List<Reference> intendedExposure = new ArrayList<>();
            private Reference observedGroup;

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
             * Allows the comparisonGroup for the study and the comparisonGroup for the subject to be linked easily.
             * 
             * @param linkId
             *     Allows the comparisonGroup for the study and the comparisonGroup for the subject to be linked easily
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder linkId(Id linkId) {
                this.linkId = linkId;
                return this;
            }

            /**
             * Convenience method for setting {@code name}.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     Label for study comparisonGroup
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
             * Unique, human-readable label for this comparisonGroup of the study.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     Label for study comparisonGroup
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Categorization of study comparisonGroup, e.g. experimental, active comparator, placebo comparater.
             * 
             * @param type
             *     Categorization of study comparisonGroup
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * A succinct description of the path through the study that would be followed by a subject adhering to this 
             * comparisonGroup.
             * 
             * @param description
             *     Short explanation of study path
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * Interventions or exposures in this comparisonGroup or cohort.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link EvidenceVariable}</li>
             * </ul>
             * 
             * @param intendedExposure
             *     Interventions or exposures in this comparisonGroup or cohort
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder intendedExposure(Reference... intendedExposure) {
                for (Reference value : intendedExposure) {
                    this.intendedExposure.add(value);
                }
                return this;
            }

            /**
             * Interventions or exposures in this comparisonGroup or cohort.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link EvidenceVariable}</li>
             * </ul>
             * 
             * @param intendedExposure
             *     Interventions or exposures in this comparisonGroup or cohort
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder intendedExposure(Collection<Reference> intendedExposure) {
                this.intendedExposure = new ArrayList<>(intendedExposure);
                return this;
            }

            /**
             * Group of participants who were enrolled in study comparisonGroup.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Group}</li>
             * </ul>
             * 
             * @param observedGroup
             *     Group of participants who were enrolled in study comparisonGroup
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder observedGroup(Reference observedGroup) {
                this.observedGroup = observedGroup;
                return this;
            }

            /**
             * Build the {@link ComparisonGroup}
             * 
             * <p>Required elements:
             * <ul>
             * <li>name</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link ComparisonGroup}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid ComparisonGroup per the base specification
             */
            @Override
            public ComparisonGroup build() {
                ComparisonGroup comparisonGroup = new ComparisonGroup(this);
                if (validating) {
                    validate(comparisonGroup);
                }
                return comparisonGroup;
            }

            protected void validate(ComparisonGroup comparisonGroup) {
                super.validate(comparisonGroup);
                ValidationSupport.requireNonNull(comparisonGroup.name, "name");
                ValidationSupport.checkList(comparisonGroup.intendedExposure, "intendedExposure", Reference.class);
                ValidationSupport.checkReferenceType(comparisonGroup.intendedExposure, "intendedExposure", "EvidenceVariable");
                ValidationSupport.checkReferenceType(comparisonGroup.observedGroup, "observedGroup", "Group");
                ValidationSupport.requireValueOrChildren(comparisonGroup);
            }

            protected Builder from(ComparisonGroup comparisonGroup) {
                super.from(comparisonGroup);
                linkId = comparisonGroup.linkId;
                name = comparisonGroup.name;
                type = comparisonGroup.type;
                description = comparisonGroup.description;
                intendedExposure.addAll(comparisonGroup.intendedExposure);
                observedGroup = comparisonGroup.observedGroup;
                return this;
            }
        }
    }

    /**
     * A goal that the study is aiming to achieve in terms of a scientific question to be answered by the analysis of data 
     * collected during the study.
     */
    public static class Objective extends BackboneElement {
        private final String name;
        @Binding(
            bindingName = "ResearchStudyObjectiveType",
            strength = BindingStrength.Value.PREFERRED,
            description = "Codes for the kind of study objective.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-study-objective-type"
        )
        private final CodeableConcept type;
        private final Markdown description;

        private Objective(Builder builder) {
            super(builder);
            name = builder.name;
            type = builder.type;
            description = builder.description;
        }

        /**
         * Unique, human-readable label for this objective of the study.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getName() {
            return name;
        }

        /**
         * The kind of study objective.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Free text description of the objective of the study. This is what the study is trying to achieve rather than how it is 
         * going to achieve it (see ResearchStudy.description).
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (name != null) || 
                (type != null) || 
                (description != null);
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
                    accept(name, "name", visitor);
                    accept(type, "type", visitor);
                    accept(description, "description", visitor);
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
            Objective other = (Objective) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(name, other.name) && 
                Objects.equals(type, other.type) && 
                Objects.equals(description, other.description);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    name, 
                    type, 
                    description);
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
            private String name;
            private CodeableConcept type;
            private Markdown description;

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
             * Convenience method for setting {@code name}.
             * 
             * @param name
             *     Label for the objective
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
             * Unique, human-readable label for this objective of the study.
             * 
             * @param name
             *     Label for the objective
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * The kind of study objective.
             * 
             * @param type
             *     primary | secondary | exploratory
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Free text description of the objective of the study. This is what the study is trying to achieve rather than how it is 
             * going to achieve it (see ResearchStudy.description).
             * 
             * @param description
             *     Description of the objective
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * Build the {@link Objective}
             * 
             * @return
             *     An immutable object of type {@link Objective}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Objective per the base specification
             */
            @Override
            public Objective build() {
                Objective objective = new Objective(this);
                if (validating) {
                    validate(objective);
                }
                return objective;
            }

            protected void validate(Objective objective) {
                super.validate(objective);
                ValidationSupport.requireValueOrChildren(objective);
            }

            protected Builder from(Objective objective) {
                super.from(objective);
                name = objective.name;
                type = objective.type;
                description = objective.description;
                return this;
            }
        }
    }

    /**
     * An "outcome measure", "endpoint", "effect measure" or "measure of effect" is a specific measurement or observation 
     * used to quantify the effect of experimental variables on the participants in a study, or for observational studies, to 
     * describe patterns of diseases or traits or associations with exposures, risk factors or treatment.
     */
    public static class OutcomeMeasure extends BackboneElement {
        private final String name;
        @Binding(
            bindingName = "ResearchStudyObjectiveType",
            strength = BindingStrength.Value.PREFERRED,
            description = "defn.",
            valueSet = "http://hl7.org/fhir/ValueSet/research-study-objective-type"
        )
        private final List<CodeableConcept> type;
        private final Markdown description;
        @ReferenceTarget({ "EvidenceVariable" })
        private final Reference reference;

        private OutcomeMeasure(Builder builder) {
            super(builder);
            name = builder.name;
            type = Collections.unmodifiableList(builder.type);
            description = builder.description;
            reference = builder.reference;
        }

        /**
         * Label for the outcome.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getName() {
            return name;
        }

        /**
         * The parameter or characteristic being assessed as one of the values by which the study is assessed.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getType() {
            return type;
        }

        /**
         * Description of the outcome.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * Structured outcome definition.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getReference() {
            return reference;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (name != null) || 
                !type.isEmpty() || 
                (description != null) || 
                (reference != null);
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
                    accept(name, "name", visitor);
                    accept(type, "type", visitor, CodeableConcept.class);
                    accept(description, "description", visitor);
                    accept(reference, "reference", visitor);
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
            OutcomeMeasure other = (OutcomeMeasure) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(name, other.name) && 
                Objects.equals(type, other.type) && 
                Objects.equals(description, other.description) && 
                Objects.equals(reference, other.reference);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    name, 
                    type, 
                    description, 
                    reference);
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
            private String name;
            private List<CodeableConcept> type = new ArrayList<>();
            private Markdown description;
            private Reference reference;

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
             * Convenience method for setting {@code name}.
             * 
             * @param name
             *     Label for the outcome
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
             * Label for the outcome.
             * 
             * @param name
             *     Label for the outcome
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * The parameter or characteristic being assessed as one of the values by which the study is assessed.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     primary | secondary | exploratory
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
             * The parameter or characteristic being assessed as one of the values by which the study is assessed.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param type
             *     primary | secondary | exploratory
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
             * Description of the outcome.
             * 
             * @param description
             *     Description of the outcome
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * Structured outcome definition.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link EvidenceVariable}</li>
             * </ul>
             * 
             * @param reference
             *     Structured outcome definition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reference(Reference reference) {
                this.reference = reference;
                return this;
            }

            /**
             * Build the {@link OutcomeMeasure}
             * 
             * @return
             *     An immutable object of type {@link OutcomeMeasure}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid OutcomeMeasure per the base specification
             */
            @Override
            public OutcomeMeasure build() {
                OutcomeMeasure outcomeMeasure = new OutcomeMeasure(this);
                if (validating) {
                    validate(outcomeMeasure);
                }
                return outcomeMeasure;
            }

            protected void validate(OutcomeMeasure outcomeMeasure) {
                super.validate(outcomeMeasure);
                ValidationSupport.checkList(outcomeMeasure.type, "type", CodeableConcept.class);
                ValidationSupport.checkReferenceType(outcomeMeasure.reference, "reference", "EvidenceVariable");
                ValidationSupport.requireValueOrChildren(outcomeMeasure);
            }

            protected Builder from(OutcomeMeasure outcomeMeasure) {
                super.from(outcomeMeasure);
                name = outcomeMeasure.name;
                type.addAll(outcomeMeasure.type);
                description = outcomeMeasure.description;
                reference = outcomeMeasure.reference;
                return this;
            }
        }
    }
}
