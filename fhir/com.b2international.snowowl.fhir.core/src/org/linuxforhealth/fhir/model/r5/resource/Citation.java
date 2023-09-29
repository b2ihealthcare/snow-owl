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
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
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
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.RelatedArtifactTypeExpanded;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The Citation Resource enables reference to any knowledge artifact for purposes of identification and attribution. The 
 * Citation Resource supports existing reference structures and developing publication practices such as versioning, 
 * expressing complex contributorship roles, and referencing computable resources.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "cnl-0",
    level = "Warning",
    location = "(base)",
    description = "Name should be usable as an identifier for the module by machine processing applications such as code generation",
    expression = "name.exists() implies name.matches('^[A-Z]([A-Za-z0-9_]){1,254}$')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "Citation.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation"
)
@Constraint(
    id = "citation-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-4",
    level = "Warning",
    location = "summary.style",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/citation-summary-style",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/citation-summary-style', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-5",
    level = "Warning",
    location = "classification.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/citation-classification-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/citation-classification-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-6",
    level = "Warning",
    location = "citedArtifact.currentState",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/cited-artifact-status-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/cited-artifact-status-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-7",
    level = "Warning",
    location = "citedArtifact.statusDate.activity",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/cited-artifact-status-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/cited-artifact-status-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-8",
    level = "Warning",
    location = "citedArtifact.title.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/title-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/title-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-9",
    level = "Warning",
    location = "citedArtifact.title.language",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/languages",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/languages', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-10",
    level = "Warning",
    location = "citedArtifact.abstract.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/cited-artifact-abstract-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/cited-artifact-abstract-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-11",
    level = "Warning",
    location = "citedArtifact.abstract.language",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/languages",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/languages', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-12",
    level = "Warning",
    location = "citedArtifact.part.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/cited-artifact-part-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/cited-artifact-part-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-13",
    level = "Warning",
    location = "citedArtifact.relatesTo.classifier",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/citation-artifact-classifier",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/citation-artifact-classifier', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-14",
    level = "Warning",
    location = "citedArtifact.publicationForm.publishedIn.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/published-in-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/published-in-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-15",
    level = "Warning",
    location = "citedArtifact.publicationForm.citedMedium",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/cited-medium",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/cited-medium', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-16",
    level = "Warning",
    location = "citedArtifact.publicationForm.language",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/languages",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/languages', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-17",
    level = "Warning",
    location = "citedArtifact.webLocation.classifier",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/artifact-url-classifier",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/artifact-url-classifier', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-18",
    level = "Warning",
    location = "citedArtifact.classification.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/cited-artifact-classification-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/cited-artifact-classification-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-19",
    level = "Warning",
    location = "citedArtifact.contributorship.entry.contributionType",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/artifact-contribution-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/artifact-contribution-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-20",
    level = "Warning",
    location = "citedArtifact.contributorship.entry.role",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/contributor-role",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/contributor-role', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-21",
    level = "Warning",
    location = "citedArtifact.contributorship.entry.contributionInstance.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/artifact-contribution-instance-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/artifact-contribution-instance-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-22",
    level = "Warning",
    location = "citedArtifact.contributorship.summary.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/contributor-summary-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/contributor-summary-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-23",
    level = "Warning",
    location = "citedArtifact.contributorship.summary.style",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/contributor-summary-style",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/contributor-summary-style', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Constraint(
    id = "citation-24",
    level = "Warning",
    location = "citedArtifact.contributorship.summary.source",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/contributor-summary-source",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/contributor-summary-source', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Citation",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Citation extends DomainResource {
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final Uri url;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final List<Identifier> identifier;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final String version;
    @org.linuxforhealth.fhir.model.annotation.Summary
    @Choice({ String.class, Coding.class })
    @Binding(
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/version-algorithm"
    )
    private final Element versionAlgorithm;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final String name;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final String title;
    @org.linuxforhealth.fhir.model.annotation.Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    @Required
    private final PublicationStatus status;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final Boolean experimental;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final DateTime date;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final String publisher;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final List<ContactDetail> contact;
    private final Markdown description;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final List<UsageContext> useContext;
    @org.linuxforhealth.fhir.model.annotation.Summary
    @Binding(
        bindingName = "Jurisdiction",
        strength = BindingStrength.Value.EXTENSIBLE,
        valueSet = "http://hl7.org/fhir/ValueSet/jurisdiction"
    )
    private final List<CodeableConcept> jurisdiction;
    private final Markdown purpose;
    private final Markdown copyright;
    private final String copyrightLabel;
    private final Date approvalDate;
    private final Date lastReviewDate;
    @org.linuxforhealth.fhir.model.annotation.Summary
    private final Period effectivePeriod;
    private final List<ContactDetail> author;
    private final List<ContactDetail> editor;
    private final List<ContactDetail> reviewer;
    private final List<ContactDetail> endorser;
    private final List<Summary> summary;
    private final List<Classification> classification;
    private final List<Annotation> note;
    @Binding(
        bindingName = "CitationStatusType",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/citation-status-type"
    )
    private final List<CodeableConcept> currentState;
    private final List<StatusDate> statusDate;
    private final List<RelatedArtifact> relatedArtifact;
    private final CitedArtifact citedArtifact;

    private Citation(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        versionAlgorithm = builder.versionAlgorithm;
        name = builder.name;
        title = builder.title;
        status = builder.status;
        experimental = builder.experimental;
        date = builder.date;
        publisher = builder.publisher;
        contact = Collections.unmodifiableList(builder.contact);
        description = builder.description;
        useContext = Collections.unmodifiableList(builder.useContext);
        jurisdiction = Collections.unmodifiableList(builder.jurisdiction);
        purpose = builder.purpose;
        copyright = builder.copyright;
        copyrightLabel = builder.copyrightLabel;
        approvalDate = builder.approvalDate;
        lastReviewDate = builder.lastReviewDate;
        effectivePeriod = builder.effectivePeriod;
        author = Collections.unmodifiableList(builder.author);
        editor = Collections.unmodifiableList(builder.editor);
        reviewer = Collections.unmodifiableList(builder.reviewer);
        endorser = Collections.unmodifiableList(builder.endorser);
        summary = Collections.unmodifiableList(builder.summary);
        classification = Collections.unmodifiableList(builder.classification);
        note = Collections.unmodifiableList(builder.note);
        currentState = Collections.unmodifiableList(builder.currentState);
        statusDate = Collections.unmodifiableList(builder.statusDate);
        relatedArtifact = Collections.unmodifiableList(builder.relatedArtifact);
        citedArtifact = builder.citedArtifact;
    }

    /**
     * An absolute URI that is used to identify this citation record when it is referenced in a specification, model, design 
     * or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address 
     * at which an authoritative instance of this summary is (or will be) published. This URL can be the target of a 
     * canonical reference. It SHALL remain the same when the summary is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this citation record when it is represented in other formats, or 
     * referenced in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the citation record when it is referenced in a specification, 
     * model, design or instance. This is an arbitrary value managed by the citation record author and is not expected to be 
     * globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is 
     * also no expectation that versions can be placed in a lexicographical sequence.
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
     * A natural language name identifying the citation record. This name should be usable as an identifier for the module by 
     * machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the citation record.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The status of this summary. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this citation record is authored for testing purposes (or 
     * education/evaluation/marketing) and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The date (and optionally time) when the citation record was last significantly changed. The date must change when the 
     * business version changes and it must change if the status code changes. In addition, it should change when the 
     * substantive content of the citation record changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual that published the citation record.
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
     * A free text natural language description of the citation from a consumer's perspective.
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
     * may be used to assist with indexing and searching for appropriate citation record instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A legal or geographic region in which the citation record is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explanation of why this citation is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * Use and/or publishing restrictions for the citation record, not for the cited artifact.
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
     * The period during which the citation record content was or is planned to be in active use.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * Who authored or created the citation record.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getAuthor() {
        return author;
    }

    /**
     * Who edited or revised the citation record.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getEditor() {
        return editor;
    }

    /**
     * Who reviewed the citation record.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getReviewer() {
        return reviewer;
    }

    /**
     * Who endorsed the citation record.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getEndorser() {
        return endorser;
    }

    /**
     * A human-readable display of key concepts to represent the citation.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Summary} that may be empty.
     */
    public List<Summary> getSummary() {
        return summary;
    }

    /**
     * The assignment to an organizing scheme.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Classification} that may be empty.
     */
    public List<Classification> getClassification() {
        return classification;
    }

    /**
     * Used for general notes and annotations not coded elsewhere.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * The status of the citation record.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCurrentState() {
        return currentState;
    }

    /**
     * The state or status of the citation record paired with an effective date or period for that state.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link StatusDate} that may be empty.
     */
    public List<StatusDate> getStatusDate() {
        return statusDate;
    }

    /**
     * Artifact related to the citation record.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
     */
    public List<RelatedArtifact> getRelatedArtifact() {
        return relatedArtifact;
    }

    /**
     * The article or artifact being described.
     * 
     * @return
     *     An immutable object of type {@link CitedArtifact} that may be null.
     */
    public CitedArtifact getCitedArtifact() {
        return citedArtifact;
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
            (status != null) || 
            (experimental != null) || 
            (date != null) || 
            (publisher != null) || 
            !contact.isEmpty() || 
            (description != null) || 
            !useContext.isEmpty() || 
            !jurisdiction.isEmpty() || 
            (purpose != null) || 
            (copyright != null) || 
            (copyrightLabel != null) || 
            (approvalDate != null) || 
            (lastReviewDate != null) || 
            (effectivePeriod != null) || 
            !author.isEmpty() || 
            !editor.isEmpty() || 
            !reviewer.isEmpty() || 
            !endorser.isEmpty() || 
            !summary.isEmpty() || 
            !classification.isEmpty() || 
            !note.isEmpty() || 
            !currentState.isEmpty() || 
            !statusDate.isEmpty() || 
            !relatedArtifact.isEmpty() || 
            (citedArtifact != null);
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
                accept(status, "status", visitor);
                accept(experimental, "experimental", visitor);
                accept(date, "date", visitor);
                accept(publisher, "publisher", visitor);
                accept(contact, "contact", visitor, ContactDetail.class);
                accept(description, "description", visitor);
                accept(useContext, "useContext", visitor, UsageContext.class);
                accept(jurisdiction, "jurisdiction", visitor, CodeableConcept.class);
                accept(purpose, "purpose", visitor);
                accept(copyright, "copyright", visitor);
                accept(copyrightLabel, "copyrightLabel", visitor);
                accept(approvalDate, "approvalDate", visitor);
                accept(lastReviewDate, "lastReviewDate", visitor);
                accept(effectivePeriod, "effectivePeriod", visitor);
                accept(author, "author", visitor, ContactDetail.class);
                accept(editor, "editor", visitor, ContactDetail.class);
                accept(reviewer, "reviewer", visitor, ContactDetail.class);
                accept(endorser, "endorser", visitor, ContactDetail.class);
                accept(summary, "summary", visitor, Summary.class);
                accept(classification, "classification", visitor, Classification.class);
                accept(note, "note", visitor, Annotation.class);
                accept(currentState, "currentState", visitor, CodeableConcept.class);
                accept(statusDate, "statusDate", visitor, StatusDate.class);
                accept(relatedArtifact, "relatedArtifact", visitor, RelatedArtifact.class);
                accept(citedArtifact, "citedArtifact", visitor);
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
        Citation other = (Citation) obj;
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
            Objects.equals(status, other.status) && 
            Objects.equals(experimental, other.experimental) && 
            Objects.equals(date, other.date) && 
            Objects.equals(publisher, other.publisher) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(description, other.description) && 
            Objects.equals(useContext, other.useContext) && 
            Objects.equals(jurisdiction, other.jurisdiction) && 
            Objects.equals(purpose, other.purpose) && 
            Objects.equals(copyright, other.copyright) && 
            Objects.equals(copyrightLabel, other.copyrightLabel) && 
            Objects.equals(approvalDate, other.approvalDate) && 
            Objects.equals(lastReviewDate, other.lastReviewDate) && 
            Objects.equals(effectivePeriod, other.effectivePeriod) && 
            Objects.equals(author, other.author) && 
            Objects.equals(editor, other.editor) && 
            Objects.equals(reviewer, other.reviewer) && 
            Objects.equals(endorser, other.endorser) && 
            Objects.equals(summary, other.summary) && 
            Objects.equals(classification, other.classification) && 
            Objects.equals(note, other.note) && 
            Objects.equals(currentState, other.currentState) && 
            Objects.equals(statusDate, other.statusDate) && 
            Objects.equals(relatedArtifact, other.relatedArtifact) && 
            Objects.equals(citedArtifact, other.citedArtifact);
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
                status, 
                experimental, 
                date, 
                publisher, 
                contact, 
                description, 
                useContext, 
                jurisdiction, 
                purpose, 
                copyright, 
                copyrightLabel, 
                approvalDate, 
                lastReviewDate, 
                effectivePeriod, 
                author, 
                editor, 
                reviewer, 
                endorser, 
                summary, 
                classification, 
                note, 
                currentState, 
                statusDate, 
                relatedArtifact, 
                citedArtifact);
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
        private PublicationStatus status;
        private Boolean experimental;
        private DateTime date;
        private String publisher;
        private List<ContactDetail> contact = new ArrayList<>();
        private Markdown description;
        private List<UsageContext> useContext = new ArrayList<>();
        private List<CodeableConcept> jurisdiction = new ArrayList<>();
        private Markdown purpose;
        private Markdown copyright;
        private String copyrightLabel;
        private Date approvalDate;
        private Date lastReviewDate;
        private Period effectivePeriod;
        private List<ContactDetail> author = new ArrayList<>();
        private List<ContactDetail> editor = new ArrayList<>();
        private List<ContactDetail> reviewer = new ArrayList<>();
        private List<ContactDetail> endorser = new ArrayList<>();
        private List<Summary> summary = new ArrayList<>();
        private List<Classification> classification = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<CodeableConcept> currentState = new ArrayList<>();
        private List<StatusDate> statusDate = new ArrayList<>();
        private List<RelatedArtifact> relatedArtifact = new ArrayList<>();
        private CitedArtifact citedArtifact;

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
         * An absolute URI that is used to identify this citation record when it is referenced in a specification, model, design 
         * or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address 
         * at which an authoritative instance of this summary is (or will be) published. This URL can be the target of a 
         * canonical reference. It SHALL remain the same when the summary is stored on different servers.
         * 
         * @param url
         *     Canonical identifier for this citation record, represented as a globally unique URI
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A formal identifier that is used to identify this citation record when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier for the citation record itself
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
         * A formal identifier that is used to identify this citation record when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier for the citation record itself
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
         *     Business version of the citation record
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
         * The identifier that is used to identify this version of the citation record when it is referenced in a specification, 
         * model, design or instance. This is an arbitrary value managed by the citation record author and is not expected to be 
         * globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is 
         * also no expectation that versions can be placed in a lexicographical sequence.
         * 
         * @param version
         *     Business version of the citation record
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
         *     Name for this citation record (computer friendly)
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
         * A natural language name identifying the citation record. This name should be usable as an identifier for the module by 
         * machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this citation record (computer friendly)
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
         *     Name for this citation record (human friendly)
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
         * A short, descriptive, user-friendly title for the citation record.
         * 
         * @param title
         *     Name for this citation record (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * The status of this summary. Enables tracking the life-cycle of the content.
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
         * A Boolean value to indicate that this citation record is authored for testing purposes (or 
         * education/evaluation/marketing) and is not intended to be used for genuine usage.
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
         * The date (and optionally time) when the citation record was last significantly changed. The date must change when the 
         * business version changes and it must change if the status code changes. In addition, it should change when the 
         * substantive content of the citation record changes.
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
         *     The publisher of the citation record, not the publisher of the article or artifact being cited
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
         * The name of the organization or individual that published the citation record.
         * 
         * @param publisher
         *     The publisher of the citation record, not the publisher of the article or artifact being cited
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
         *     Contact details for the publisher of the citation record
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
         *     Contact details for the publisher of the citation record
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
         * A free text natural language description of the citation from a consumer's perspective.
         * 
         * @param description
         *     Natural language description of the citation
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
         * may be used to assist with indexing and searching for appropriate citation record instances.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     The context that the citation record content is intended to support
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
         * may be used to assist with indexing and searching for appropriate citation record instances.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     The context that the citation record content is intended to support
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
         * A legal or geographic region in which the citation record is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for citation record (if applicable)
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
         * A legal or geographic region in which the citation record is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for citation record (if applicable)
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
         * Explanation of why this citation is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this citation is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * Use and/or publishing restrictions for the citation record, not for the cited artifact.
         * 
         * @param copyright
         *     Use and/or publishing restrictions for the citation record, not for the cited artifact
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
         *     Copyright holder and year(s) for the ciation record, not for the cited artifact
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
         *     Copyright holder and year(s) for the ciation record, not for the cited artifact
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
         *     When the citation record was approved by publisher
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
         *     When the citation record was approved by publisher
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
         *     When the citation record was last reviewed by the publisher
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
         *     When the citation record was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
        }

        /**
         * The period during which the citation record content was or is planned to be in active use.
         * 
         * @param effectivePeriod
         *     When the citation record is expected to be used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effectivePeriod(Period effectivePeriod) {
            this.effectivePeriod = effectivePeriod;
            return this;
        }

        /**
         * Who authored or created the citation record.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param author
         *     Who authored the citation record
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
         * Who authored or created the citation record.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param author
         *     Who authored the citation record
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
         * Who edited or revised the citation record.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param editor
         *     Who edited the citation record
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
         * Who edited or revised the citation record.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param editor
         *     Who edited the citation record
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
         * Who reviewed the citation record.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reviewer
         *     Who reviewed the citation record
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
         * Who reviewed the citation record.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reviewer
         *     Who reviewed the citation record
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
         * Who endorsed the citation record.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param endorser
         *     Who endorsed the citation record
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
         * Who endorsed the citation record.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param endorser
         *     Who endorsed the citation record
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
         * A human-readable display of key concepts to represent the citation.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param summary
         *     A human-readable display of key concepts to represent the citation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder summary(Summary... summary) {
            for (Summary value : summary) {
                this.summary.add(value);
            }
            return this;
        }

        /**
         * A human-readable display of key concepts to represent the citation.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param summary
         *     A human-readable display of key concepts to represent the citation
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder summary(Collection<Summary> summary) {
            this.summary = new ArrayList<>(summary);
            return this;
        }

        /**
         * The assignment to an organizing scheme.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param classification
         *     The assignment to an organizing scheme
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder classification(Classification... classification) {
            for (Classification value : classification) {
                this.classification.add(value);
            }
            return this;
        }

        /**
         * The assignment to an organizing scheme.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param classification
         *     The assignment to an organizing scheme
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder classification(Collection<Classification> classification) {
            this.classification = new ArrayList<>(classification);
            return this;
        }

        /**
         * Used for general notes and annotations not coded elsewhere.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Used for general notes and annotations not coded elsewhere
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
         * Used for general notes and annotations not coded elsewhere.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Used for general notes and annotations not coded elsewhere
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
         * The status of the citation record.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param currentState
         *     The status of the citation record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder currentState(CodeableConcept... currentState) {
            for (CodeableConcept value : currentState) {
                this.currentState.add(value);
            }
            return this;
        }

        /**
         * The status of the citation record.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param currentState
         *     The status of the citation record
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder currentState(Collection<CodeableConcept> currentState) {
            this.currentState = new ArrayList<>(currentState);
            return this;
        }

        /**
         * The state or status of the citation record paired with an effective date or period for that state.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusDate
         *     An effective date or period for a status of the citation record
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statusDate(StatusDate... statusDate) {
            for (StatusDate value : statusDate) {
                this.statusDate.add(value);
            }
            return this;
        }

        /**
         * The state or status of the citation record paired with an effective date or period for that state.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statusDate
         *     An effective date or period for a status of the citation record
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder statusDate(Collection<StatusDate> statusDate) {
            this.statusDate = new ArrayList<>(statusDate);
            return this;
        }

        /**
         * Artifact related to the citation record.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedArtifact
         *     Artifact related to the citation record
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
         * Artifact related to the citation record.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatedArtifact
         *     Artifact related to the citation record
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
         * The article or artifact being described.
         * 
         * @param citedArtifact
         *     The article or artifact being described
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder citedArtifact(CitedArtifact citedArtifact) {
            this.citedArtifact = citedArtifact;
            return this;
        }

        /**
         * Build the {@link Citation}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Citation}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Citation per the base specification
         */
        @Override
        public Citation build() {
            Citation citation = new Citation(this);
            if (validating) {
                validate(citation);
            }
            return citation;
        }

        protected void validate(Citation citation) {
            super.validate(citation);
            ValidationSupport.checkList(citation.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(citation.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(citation.status, "status");
            ValidationSupport.checkList(citation.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(citation.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(citation.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(citation.author, "author", ContactDetail.class);
            ValidationSupport.checkList(citation.editor, "editor", ContactDetail.class);
            ValidationSupport.checkList(citation.reviewer, "reviewer", ContactDetail.class);
            ValidationSupport.checkList(citation.endorser, "endorser", ContactDetail.class);
            ValidationSupport.checkList(citation.summary, "summary", Summary.class);
            ValidationSupport.checkList(citation.classification, "classification", Classification.class);
            ValidationSupport.checkList(citation.note, "note", Annotation.class);
            ValidationSupport.checkList(citation.currentState, "currentState", CodeableConcept.class);
            ValidationSupport.checkList(citation.statusDate, "statusDate", StatusDate.class);
            ValidationSupport.checkList(citation.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
        }

        protected Builder from(Citation citation) {
            super.from(citation);
            url = citation.url;
            identifier.addAll(citation.identifier);
            version = citation.version;
            versionAlgorithm = citation.versionAlgorithm;
            name = citation.name;
            title = citation.title;
            status = citation.status;
            experimental = citation.experimental;
            date = citation.date;
            publisher = citation.publisher;
            contact.addAll(citation.contact);
            description = citation.description;
            useContext.addAll(citation.useContext);
            jurisdiction.addAll(citation.jurisdiction);
            purpose = citation.purpose;
            copyright = citation.copyright;
            copyrightLabel = citation.copyrightLabel;
            approvalDate = citation.approvalDate;
            lastReviewDate = citation.lastReviewDate;
            effectivePeriod = citation.effectivePeriod;
            author.addAll(citation.author);
            editor.addAll(citation.editor);
            reviewer.addAll(citation.reviewer);
            endorser.addAll(citation.endorser);
            summary.addAll(citation.summary);
            classification.addAll(citation.classification);
            note.addAll(citation.note);
            currentState.addAll(citation.currentState);
            statusDate.addAll(citation.statusDate);
            relatedArtifact.addAll(citation.relatedArtifact);
            citedArtifact = citation.citedArtifact;
            return this;
        }
    }

    /**
     * A human-readable display of key concepts to represent the citation.
     */
    public static class Summary extends BackboneElement {
        @Binding(
            bindingName = "CitationSummaryStyle",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://hl7.org/fhir/ValueSet/citation-summary-style"
        )
        private final CodeableConcept style;
        @org.linuxforhealth.fhir.model.annotation.Summary
        @Required
        private final Markdown text;

        private Summary(Builder builder) {
            super(builder);
            style = builder.style;
            text = builder.text;
        }

        /**
         * Format for display of the citation summary.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getStyle() {
            return style;
        }

        /**
         * The human-readable display of the citation summary.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that is non-null.
         */
        public Markdown getText() {
            return text;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (style != null) || 
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
                    accept(style, "style", visitor);
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
            Summary other = (Summary) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(style, other.style) && 
                Objects.equals(text, other.text);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    style, 
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
            private CodeableConcept style;
            private Markdown text;

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
             * Format for display of the citation summary.
             * 
             * @param style
             *     Format for display of the citation summary
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder style(CodeableConcept style) {
                this.style = style;
                return this;
            }

            /**
             * The human-readable display of the citation summary.
             * 
             * <p>This element is required.
             * 
             * @param text
             *     The human-readable display of the citation summary
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder text(Markdown text) {
                this.text = text;
                return this;
            }

            /**
             * Build the {@link Summary}
             * 
             * <p>Required elements:
             * <ul>
             * <li>text</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Summary}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Summary per the base specification
             */
            @Override
            public Summary build() {
                Summary summary = new Summary(this);
                if (validating) {
                    validate(summary);
                }
                return summary;
            }

            protected void validate(Summary summary) {
                super.validate(summary);
                ValidationSupport.requireNonNull(summary.text, "text");
                ValidationSupport.requireValueOrChildren(summary);
            }

            protected Builder from(Summary summary) {
                super.from(summary);
                style = summary.style;
                text = summary.text;
                return this;
            }
        }
    }

    /**
     * The assignment to an organizing scheme.
     */
    public static class Classification extends BackboneElement {
        @Binding(
            bindingName = "CitationClassificationType",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://hl7.org/fhir/ValueSet/citation-classification-type"
        )
        private final CodeableConcept type;
        @Binding(
            bindingName = "CitationArtifactClassifier",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/citation-artifact-classifier"
        )
        private final List<CodeableConcept> classifier;

        private Classification(Builder builder) {
            super(builder);
            type = builder.type;
            classifier = Collections.unmodifiableList(builder.classifier);
        }

        /**
         * The kind of classifier (e.g. publication type, keyword).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * The specific classification value.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getClassifier() {
            return classifier;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                !classifier.isEmpty();
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
                    accept(classifier, "classifier", visitor, CodeableConcept.class);
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
            Classification other = (Classification) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(classifier, other.classifier);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    classifier);
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
            private List<CodeableConcept> classifier = new ArrayList<>();

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
             * The kind of classifier (e.g. publication type, keyword).
             * 
             * @param type
             *     The kind of classifier (e.g. publication type, keyword)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * The specific classification value.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classifier
             *     The specific classification value
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
             * The specific classification value.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classifier
             *     The specific classification value
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
             * Build the {@link Classification}
             * 
             * @return
             *     An immutable object of type {@link Classification}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Classification per the base specification
             */
            @Override
            public Classification build() {
                Classification classification = new Classification(this);
                if (validating) {
                    validate(classification);
                }
                return classification;
            }

            protected void validate(Classification classification) {
                super.validate(classification);
                ValidationSupport.checkList(classification.classifier, "classifier", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(classification);
            }

            protected Builder from(Classification classification) {
                super.from(classification);
                type = classification.type;
                classifier.addAll(classification.classifier);
                return this;
            }
        }
    }

    /**
     * The state or status of the citation record paired with an effective date or period for that state.
     */
    public static class StatusDate extends BackboneElement {
        @Binding(
            bindingName = "CitationStatusType",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/citation-status-type"
        )
        @Required
        private final CodeableConcept activity;
        private final Boolean actual;
        @Required
        private final Period period;

        private StatusDate(Builder builder) {
            super(builder);
            activity = builder.activity;
            actual = builder.actual;
            period = builder.period;
        }

        /**
         * The state or status of the citation record (that will be paired with the period).
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getActivity() {
            return activity;
        }

        /**
         * Whether the status date is actual (has occurred) or expected (estimated or anticipated).
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getActual() {
            return actual;
        }

        /**
         * When the status started and/or ended.
         * 
         * @return
         *     An immutable object of type {@link Period} that is non-null.
         */
        public Period getPeriod() {
            return period;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (activity != null) || 
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
                    accept(activity, "activity", visitor);
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
            StatusDate other = (StatusDate) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(activity, other.activity) && 
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
                    activity, 
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
            private CodeableConcept activity;
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
             * The state or status of the citation record (that will be paired with the period).
             * 
             * <p>This element is required.
             * 
             * @param activity
             *     Classification of the status
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder activity(CodeableConcept activity) {
                this.activity = activity;
                return this;
            }

            /**
             * Convenience method for setting {@code actual}.
             * 
             * @param actual
             *     Either occurred or expected
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
             * Whether the status date is actual (has occurred) or expected (estimated or anticipated).
             * 
             * @param actual
             *     Either occurred or expected
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder actual(Boolean actual) {
                this.actual = actual;
                return this;
            }

            /**
             * When the status started and/or ended.
             * 
             * <p>This element is required.
             * 
             * @param period
             *     When the status started and/or ended
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Build the {@link StatusDate}
             * 
             * <p>Required elements:
             * <ul>
             * <li>activity</li>
             * <li>period</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link StatusDate}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid StatusDate per the base specification
             */
            @Override
            public StatusDate build() {
                StatusDate statusDate = new StatusDate(this);
                if (validating) {
                    validate(statusDate);
                }
                return statusDate;
            }

            protected void validate(StatusDate statusDate) {
                super.validate(statusDate);
                ValidationSupport.requireNonNull(statusDate.activity, "activity");
                ValidationSupport.requireNonNull(statusDate.period, "period");
                ValidationSupport.requireValueOrChildren(statusDate);
            }

            protected Builder from(StatusDate statusDate) {
                super.from(statusDate);
                activity = statusDate.activity;
                actual = statusDate.actual;
                period = statusDate.period;
                return this;
            }
        }
    }

    /**
     * The article or artifact being described.
     */
    public static class CitedArtifact extends BackboneElement {
        @org.linuxforhealth.fhir.model.annotation.Summary
        private final List<Identifier> identifier;
        @org.linuxforhealth.fhir.model.annotation.Summary
        private final List<Identifier> relatedIdentifier;
        @org.linuxforhealth.fhir.model.annotation.Summary
        private final DateTime dateAccessed;
        private final Version version;
        @Binding(
            bindingName = "CitedArtifactStatusType",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://hl7.org/fhir/ValueSet/cited-artifact-status-type"
        )
        private final List<CodeableConcept> currentState;
        private final List<StatusDate> statusDate;
        private final List<Title> title;
        private final List<Abstract> _abstract;
        private final Part part;
        private final List<RelatesTo> relatesTo;
        private final List<PublicationForm> publicationForm;
        private final List<WebLocation> webLocation;
        private final List<Classification> classification;
        private final Contributorship contributorship;
        private final List<Annotation> note;

        private CitedArtifact(Builder builder) {
            super(builder);
            identifier = Collections.unmodifiableList(builder.identifier);
            relatedIdentifier = Collections.unmodifiableList(builder.relatedIdentifier);
            dateAccessed = builder.dateAccessed;
            version = builder.version;
            currentState = Collections.unmodifiableList(builder.currentState);
            statusDate = Collections.unmodifiableList(builder.statusDate);
            title = Collections.unmodifiableList(builder.title);
            _abstract = Collections.unmodifiableList(builder._abstract);
            part = builder.part;
            relatesTo = Collections.unmodifiableList(builder.relatesTo);
            publicationForm = Collections.unmodifiableList(builder.publicationForm);
            webLocation = Collections.unmodifiableList(builder.webLocation);
            classification = Collections.unmodifiableList(builder.classification);
            contributorship = builder.contributorship;
            note = Collections.unmodifiableList(builder.note);
        }

        /**
         * A formal identifier that is used to identify the cited artifact when it is represented in other formats, or referenced 
         * in a specification, model, design or an instance.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
         */
        public List<Identifier> getIdentifier() {
            return identifier;
        }

        /**
         * A formal identifier that is used to identify things closely related to the cited artifact.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
         */
        public List<Identifier> getRelatedIdentifier() {
            return relatedIdentifier;
        }

        /**
         * When the cited artifact was accessed.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getDateAccessed() {
            return dateAccessed;
        }

        /**
         * The defined version of the cited artifact.
         * 
         * @return
         *     An immutable object of type {@link Version} that may be null.
         */
        public Version getVersion() {
            return version;
        }

        /**
         * The status of the cited artifact.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getCurrentState() {
            return currentState;
        }

        /**
         * An effective date or period, historical or future, actual or expected, for a status of the cited artifact.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link StatusDate} that may be empty.
         */
        public List<StatusDate> getStatusDate() {
            return statusDate;
        }

        /**
         * The title details of the article or artifact.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Title} that may be empty.
         */
        public List<Title> getTitle() {
            return title;
        }

        /**
         * The abstract may be used to convey article-contained abstracts, externally-created abstracts, or other descriptive 
         * summaries.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Abstract} that may be empty.
         */
        public List<Abstract> getAbstract() {
            return _abstract;
        }

        /**
         * The component of the article or artifact.
         * 
         * @return
         *     An immutable object of type {@link Part} that may be null.
         */
        public Part getPart() {
            return part;
        }

        /**
         * The artifact related to the cited artifact.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RelatesTo} that may be empty.
         */
        public List<RelatesTo> getRelatesTo() {
            return relatesTo;
        }

        /**
         * If multiple, used to represent alternative forms of the article that are not separate citations.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link PublicationForm} that may be empty.
         */
        public List<PublicationForm> getPublicationForm() {
            return publicationForm;
        }

        /**
         * Used for any URL for the article or artifact cited.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link WebLocation} that may be empty.
         */
        public List<WebLocation> getWebLocation() {
            return webLocation;
        }

        /**
         * The assignment to an organizing scheme.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Classification} that may be empty.
         */
        public List<Classification> getClassification() {
            return classification;
        }

        /**
         * This element is used to list authors and other contributors, their contact information, specific contributions, and 
         * summary statements.
         * 
         * @return
         *     An immutable object of type {@link Contributorship} that may be null.
         */
        public Contributorship getContributorship() {
            return contributorship;
        }

        /**
         * Any additional information or content for the article or artifact.
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
                !relatedIdentifier.isEmpty() || 
                (dateAccessed != null) || 
                (version != null) || 
                !currentState.isEmpty() || 
                !statusDate.isEmpty() || 
                !title.isEmpty() || 
                !_abstract.isEmpty() || 
                (part != null) || 
                !relatesTo.isEmpty() || 
                !publicationForm.isEmpty() || 
                !webLocation.isEmpty() || 
                !classification.isEmpty() || 
                (contributorship != null) || 
                !note.isEmpty();
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
                    accept(identifier, "identifier", visitor, Identifier.class);
                    accept(relatedIdentifier, "relatedIdentifier", visitor, Identifier.class);
                    accept(dateAccessed, "dateAccessed", visitor);
                    accept(version, "version", visitor);
                    accept(currentState, "currentState", visitor, CodeableConcept.class);
                    accept(statusDate, "statusDate", visitor, StatusDate.class);
                    accept(title, "title", visitor, Title.class);
                    accept(_abstract, "abstract", visitor, Abstract.class);
                    accept(part, "part", visitor);
                    accept(relatesTo, "relatesTo", visitor, RelatesTo.class);
                    accept(publicationForm, "publicationForm", visitor, PublicationForm.class);
                    accept(webLocation, "webLocation", visitor, WebLocation.class);
                    accept(classification, "classification", visitor, Classification.class);
                    accept(contributorship, "contributorship", visitor);
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
            CitedArtifact other = (CitedArtifact) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(identifier, other.identifier) && 
                Objects.equals(relatedIdentifier, other.relatedIdentifier) && 
                Objects.equals(dateAccessed, other.dateAccessed) && 
                Objects.equals(version, other.version) && 
                Objects.equals(currentState, other.currentState) && 
                Objects.equals(statusDate, other.statusDate) && 
                Objects.equals(title, other.title) && 
                Objects.equals(_abstract, other._abstract) && 
                Objects.equals(part, other.part) && 
                Objects.equals(relatesTo, other.relatesTo) && 
                Objects.equals(publicationForm, other.publicationForm) && 
                Objects.equals(webLocation, other.webLocation) && 
                Objects.equals(classification, other.classification) && 
                Objects.equals(contributorship, other.contributorship) && 
                Objects.equals(note, other.note);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    identifier, 
                    relatedIdentifier, 
                    dateAccessed, 
                    version, 
                    currentState, 
                    statusDate, 
                    title, 
                    _abstract, 
                    part, 
                    relatesTo, 
                    publicationForm, 
                    webLocation, 
                    classification, 
                    contributorship, 
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

        public static class Builder extends BackboneElement.Builder {
            private List<Identifier> identifier = new ArrayList<>();
            private List<Identifier> relatedIdentifier = new ArrayList<>();
            private DateTime dateAccessed;
            private Version version;
            private List<CodeableConcept> currentState = new ArrayList<>();
            private List<StatusDate> statusDate = new ArrayList<>();
            private List<Title> title = new ArrayList<>();
            private List<Abstract> _abstract = new ArrayList<>();
            private Part part;
            private List<RelatesTo> relatesTo = new ArrayList<>();
            private List<PublicationForm> publicationForm = new ArrayList<>();
            private List<WebLocation> webLocation = new ArrayList<>();
            private List<Classification> classification = new ArrayList<>();
            private Contributorship contributorship;
            private List<Annotation> note = new ArrayList<>();

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
             * A formal identifier that is used to identify the cited artifact when it is represented in other formats, or referenced 
             * in a specification, model, design or an instance.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param identifier
             *     Unique identifier. May include DOI, PMID, PMCID, etc
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
             * A formal identifier that is used to identify the cited artifact when it is represented in other formats, or referenced 
             * in a specification, model, design or an instance.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param identifier
             *     Unique identifier. May include DOI, PMID, PMCID, etc
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
             * A formal identifier that is used to identify things closely related to the cited artifact.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedIdentifier
             *     Identifier not unique to the cited artifact. May include trial registry identifiers
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relatedIdentifier(Identifier... relatedIdentifier) {
                for (Identifier value : relatedIdentifier) {
                    this.relatedIdentifier.add(value);
                }
                return this;
            }

            /**
             * A formal identifier that is used to identify things closely related to the cited artifact.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedIdentifier
             *     Identifier not unique to the cited artifact. May include trial registry identifiers
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder relatedIdentifier(Collection<Identifier> relatedIdentifier) {
                this.relatedIdentifier = new ArrayList<>(relatedIdentifier);
                return this;
            }

            /**
             * When the cited artifact was accessed.
             * 
             * @param dateAccessed
             *     When the cited artifact was accessed
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dateAccessed(DateTime dateAccessed) {
                this.dateAccessed = dateAccessed;
                return this;
            }

            /**
             * The defined version of the cited artifact.
             * 
             * @param version
             *     The defined version of the cited artifact
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder version(Version version) {
                this.version = version;
                return this;
            }

            /**
             * The status of the cited artifact.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param currentState
             *     The status of the cited artifact
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder currentState(CodeableConcept... currentState) {
                for (CodeableConcept value : currentState) {
                    this.currentState.add(value);
                }
                return this;
            }

            /**
             * The status of the cited artifact.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param currentState
             *     The status of the cited artifact
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder currentState(Collection<CodeableConcept> currentState) {
                this.currentState = new ArrayList<>(currentState);
                return this;
            }

            /**
             * An effective date or period, historical or future, actual or expected, for a status of the cited artifact.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param statusDate
             *     An effective date or period for a status of the cited artifact
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder statusDate(StatusDate... statusDate) {
                for (StatusDate value : statusDate) {
                    this.statusDate.add(value);
                }
                return this;
            }

            /**
             * An effective date or period, historical or future, actual or expected, for a status of the cited artifact.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param statusDate
             *     An effective date or period for a status of the cited artifact
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder statusDate(Collection<StatusDate> statusDate) {
                this.statusDate = new ArrayList<>(statusDate);
                return this;
            }

            /**
             * The title details of the article or artifact.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param title
             *     The title details of the article or artifact
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder title(Title... title) {
                for (Title value : title) {
                    this.title.add(value);
                }
                return this;
            }

            /**
             * The title details of the article or artifact.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param title
             *     The title details of the article or artifact
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder title(Collection<Title> title) {
                this.title = new ArrayList<>(title);
                return this;
            }

            /**
             * The abstract may be used to convey article-contained abstracts, externally-created abstracts, or other descriptive 
             * summaries.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param _abstract
             *     Summary of the article or artifact
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder _abstract(Abstract... _abstract) {
                for (Abstract value : _abstract) {
                    this._abstract.add(value);
                }
                return this;
            }

            /**
             * The abstract may be used to convey article-contained abstracts, externally-created abstracts, or other descriptive 
             * summaries.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param _abstract
             *     Summary of the article or artifact
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder _abstract(Collection<Abstract> _abstract) {
                this._abstract = new ArrayList<>(_abstract);
                return this;
            }

            /**
             * The component of the article or artifact.
             * 
             * @param part
             *     The component of the article or artifact
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder part(Part part) {
                this.part = part;
                return this;
            }

            /**
             * The artifact related to the cited artifact.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatesTo
             *     The artifact related to the cited artifact
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder relatesTo(RelatesTo... relatesTo) {
                for (RelatesTo value : relatesTo) {
                    this.relatesTo.add(value);
                }
                return this;
            }

            /**
             * The artifact related to the cited artifact.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatesTo
             *     The artifact related to the cited artifact
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder relatesTo(Collection<RelatesTo> relatesTo) {
                this.relatesTo = new ArrayList<>(relatesTo);
                return this;
            }

            /**
             * If multiple, used to represent alternative forms of the article that are not separate citations.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param publicationForm
             *     If multiple, used to represent alternative forms of the article that are not separate citations
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder publicationForm(PublicationForm... publicationForm) {
                for (PublicationForm value : publicationForm) {
                    this.publicationForm.add(value);
                }
                return this;
            }

            /**
             * If multiple, used to represent alternative forms of the article that are not separate citations.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param publicationForm
             *     If multiple, used to represent alternative forms of the article that are not separate citations
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder publicationForm(Collection<PublicationForm> publicationForm) {
                this.publicationForm = new ArrayList<>(publicationForm);
                return this;
            }

            /**
             * Used for any URL for the article or artifact cited.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param webLocation
             *     Used for any URL for the article or artifact cited
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder webLocation(WebLocation... webLocation) {
                for (WebLocation value : webLocation) {
                    this.webLocation.add(value);
                }
                return this;
            }

            /**
             * Used for any URL for the article or artifact cited.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param webLocation
             *     Used for any URL for the article or artifact cited
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder webLocation(Collection<WebLocation> webLocation) {
                this.webLocation = new ArrayList<>(webLocation);
                return this;
            }

            /**
             * The assignment to an organizing scheme.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classification
             *     The assignment to an organizing scheme
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder classification(Classification... classification) {
                for (Classification value : classification) {
                    this.classification.add(value);
                }
                return this;
            }

            /**
             * The assignment to an organizing scheme.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classification
             *     The assignment to an organizing scheme
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder classification(Collection<Classification> classification) {
                this.classification = new ArrayList<>(classification);
                return this;
            }

            /**
             * This element is used to list authors and other contributors, their contact information, specific contributions, and 
             * summary statements.
             * 
             * @param contributorship
             *     Attribution of authors and other contributors
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder contributorship(Contributorship contributorship) {
                this.contributorship = contributorship;
                return this;
            }

            /**
             * Any additional information or content for the article or artifact.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Any additional information or content for the article or artifact
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
             * Any additional information or content for the article or artifact.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Any additional information or content for the article or artifact
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
             * Build the {@link CitedArtifact}
             * 
             * @return
             *     An immutable object of type {@link CitedArtifact}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid CitedArtifact per the base specification
             */
            @Override
            public CitedArtifact build() {
                CitedArtifact citedArtifact = new CitedArtifact(this);
                if (validating) {
                    validate(citedArtifact);
                }
                return citedArtifact;
            }

            protected void validate(CitedArtifact citedArtifact) {
                super.validate(citedArtifact);
                ValidationSupport.checkList(citedArtifact.identifier, "identifier", Identifier.class);
                ValidationSupport.checkList(citedArtifact.relatedIdentifier, "relatedIdentifier", Identifier.class);
                ValidationSupport.checkList(citedArtifact.currentState, "currentState", CodeableConcept.class);
                ValidationSupport.checkList(citedArtifact.statusDate, "statusDate", StatusDate.class);
                ValidationSupport.checkList(citedArtifact.title, "title", Title.class);
                ValidationSupport.checkList(citedArtifact._abstract, "abstract", Abstract.class);
                ValidationSupport.checkList(citedArtifact.relatesTo, "relatesTo", RelatesTo.class);
                ValidationSupport.checkList(citedArtifact.publicationForm, "publicationForm", PublicationForm.class);
                ValidationSupport.checkList(citedArtifact.webLocation, "webLocation", WebLocation.class);
                ValidationSupport.checkList(citedArtifact.classification, "classification", Classification.class);
                ValidationSupport.checkList(citedArtifact.note, "note", Annotation.class);
                ValidationSupport.requireValueOrChildren(citedArtifact);
            }

            protected Builder from(CitedArtifact citedArtifact) {
                super.from(citedArtifact);
                identifier.addAll(citedArtifact.identifier);
                relatedIdentifier.addAll(citedArtifact.relatedIdentifier);
                dateAccessed = citedArtifact.dateAccessed;
                version = citedArtifact.version;
                currentState.addAll(citedArtifact.currentState);
                statusDate.addAll(citedArtifact.statusDate);
                title.addAll(citedArtifact.title);
                _abstract.addAll(citedArtifact._abstract);
                part = citedArtifact.part;
                relatesTo.addAll(citedArtifact.relatesTo);
                publicationForm.addAll(citedArtifact.publicationForm);
                webLocation.addAll(citedArtifact.webLocation);
                classification.addAll(citedArtifact.classification);
                contributorship = citedArtifact.contributorship;
                note.addAll(citedArtifact.note);
                return this;
            }
        }

        /**
         * The defined version of the cited artifact.
         */
        public static class Version extends BackboneElement {
            @Required
            private final String value;
            @ReferenceTarget({ "Citation" })
            private final Reference baseCitation;

            private Version(Builder builder) {
                super(builder);
                value = builder.value;
                baseCitation = builder.baseCitation;
            }

            /**
             * The version number or other version identifier.
             * 
             * @return
             *     An immutable object of type {@link String} that is non-null.
             */
            public String getValue() {
                return value;
            }

            /**
             * Citation for the main version of the cited artifact.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getBaseCitation() {
                return baseCitation;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (value != null) || 
                    (baseCitation != null);
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
                        accept(baseCitation, "baseCitation", visitor);
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
                Version other = (Version) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(value, other.value) && 
                    Objects.equals(baseCitation, other.baseCitation);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        value, 
                        baseCitation);
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
                private String value;
                private Reference baseCitation;

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
                 * Convenience method for setting {@code value}.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     The version number or other version identifier
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
                 * The version number or other version identifier.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     The version number or other version identifier
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(String value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Citation for the main version of the cited artifact.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Citation}</li>
                 * </ul>
                 * 
                 * @param baseCitation
                 *     Citation for the main version of the cited artifact
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder baseCitation(Reference baseCitation) {
                    this.baseCitation = baseCitation;
                    return this;
                }

                /**
                 * Build the {@link Version}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>value</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Version}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Version per the base specification
                 */
                @Override
                public Version build() {
                    Version version = new Version(this);
                    if (validating) {
                        validate(version);
                    }
                    return version;
                }

                protected void validate(Version version) {
                    super.validate(version);
                    ValidationSupport.requireNonNull(version.value, "value");
                    ValidationSupport.checkReferenceType(version.baseCitation, "baseCitation", "Citation");
                    ValidationSupport.requireValueOrChildren(version);
                }

                protected Builder from(Version version) {
                    super.from(version);
                    value = version.value;
                    baseCitation = version.baseCitation;
                    return this;
                }
            }
        }

        /**
         * An effective date or period, historical or future, actual or expected, for a status of the cited artifact.
         */
        public static class StatusDate extends BackboneElement {
            @Binding(
                bindingName = "CitedArtifactStatusType",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/cited-artifact-status-type"
            )
            @Required
            private final CodeableConcept activity;
            private final Boolean actual;
            @Required
            private final Period period;

            private StatusDate(Builder builder) {
                super(builder);
                activity = builder.activity;
                actual = builder.actual;
                period = builder.period;
            }

            /**
             * A definition of the status associated with a date or period.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getActivity() {
                return activity;
            }

            /**
             * Either occurred or expected.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getActual() {
                return actual;
            }

            /**
             * When the status started and/or ended.
             * 
             * @return
             *     An immutable object of type {@link Period} that is non-null.
             */
            public Period getPeriod() {
                return period;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (activity != null) || 
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
                        accept(activity, "activity", visitor);
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
                StatusDate other = (StatusDate) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(activity, other.activity) && 
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
                        activity, 
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
                private CodeableConcept activity;
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
                 * A definition of the status associated with a date or period.
                 * 
                 * <p>This element is required.
                 * 
                 * @param activity
                 *     Classification of the status
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder activity(CodeableConcept activity) {
                    this.activity = activity;
                    return this;
                }

                /**
                 * Convenience method for setting {@code actual}.
                 * 
                 * @param actual
                 *     Either occurred or expected
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
                 * Either occurred or expected.
                 * 
                 * @param actual
                 *     Either occurred or expected
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder actual(Boolean actual) {
                    this.actual = actual;
                    return this;
                }

                /**
                 * When the status started and/or ended.
                 * 
                 * <p>This element is required.
                 * 
                 * @param period
                 *     When the status started and/or ended
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder period(Period period) {
                    this.period = period;
                    return this;
                }

                /**
                 * Build the {@link StatusDate}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>activity</li>
                 * <li>period</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link StatusDate}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid StatusDate per the base specification
                 */
                @Override
                public StatusDate build() {
                    StatusDate statusDate = new StatusDate(this);
                    if (validating) {
                        validate(statusDate);
                    }
                    return statusDate;
                }

                protected void validate(StatusDate statusDate) {
                    super.validate(statusDate);
                    ValidationSupport.requireNonNull(statusDate.activity, "activity");
                    ValidationSupport.requireNonNull(statusDate.period, "period");
                    ValidationSupport.requireValueOrChildren(statusDate);
                }

                protected Builder from(StatusDate statusDate) {
                    super.from(statusDate);
                    activity = statusDate.activity;
                    actual = statusDate.actual;
                    period = statusDate.period;
                    return this;
                }
            }
        }

        /**
         * The title details of the article or artifact.
         */
        public static class Title extends BackboneElement {
            @Binding(
                bindingName = "TitleType",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/title-type"
            )
            private final List<CodeableConcept> type;
            @Binding(
                bindingName = "Language",
                strength = BindingStrength.Value.PREFERRED,
                description = "A human language.",
                valueSet = "http://hl7.org/fhir/ValueSet/languages"
            )
            private final CodeableConcept language;
            @Required
            private final Markdown text;

            private Title(Builder builder) {
                super(builder);
                type = Collections.unmodifiableList(builder.type);
                language = builder.language;
                text = builder.text;
            }

            /**
             * Used to express the reason for or classification of the title.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getType() {
                return type;
            }

            /**
             * Used to express the specific language of the title.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getLanguage() {
                return language;
            }

            /**
             * The title of the article or artifact.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that is non-null.
             */
            public Markdown getText() {
                return text;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !type.isEmpty() || 
                    (language != null) || 
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
                        accept(type, "type", visitor, CodeableConcept.class);
                        accept(language, "language", visitor);
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
                Title other = (Title) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(language, other.language) && 
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
                        language, 
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
                private List<CodeableConcept> type = new ArrayList<>();
                private CodeableConcept language;
                private Markdown text;

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
                 * Used to express the reason for or classification of the title.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param type
                 *     The kind of title
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
                 * Used to express the reason for or classification of the title.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param type
                 *     The kind of title
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
                 * Used to express the specific language of the title.
                 * 
                 * @param language
                 *     Used to express the specific language
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder language(CodeableConcept language) {
                    this.language = language;
                    return this;
                }

                /**
                 * The title of the article or artifact.
                 * 
                 * <p>This element is required.
                 * 
                 * @param text
                 *     The title of the article or artifact
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder text(Markdown text) {
                    this.text = text;
                    return this;
                }

                /**
                 * Build the {@link Title}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>text</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Title}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Title per the base specification
                 */
                @Override
                public Title build() {
                    Title title = new Title(this);
                    if (validating) {
                        validate(title);
                    }
                    return title;
                }

                protected void validate(Title title) {
                    super.validate(title);
                    ValidationSupport.checkList(title.type, "type", CodeableConcept.class);
                    ValidationSupport.requireNonNull(title.text, "text");
                    ValidationSupport.requireValueOrChildren(title);
                }

                protected Builder from(Title title) {
                    super.from(title);
                    type.addAll(title.type);
                    language = title.language;
                    text = title.text;
                    return this;
                }
            }
        }

        /**
         * The abstract may be used to convey article-contained abstracts, externally-created abstracts, or other descriptive 
         * summaries.
         */
        public static class Abstract extends BackboneElement {
            @Binding(
                bindingName = "CitedArtifactAbstractType",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/cited-artifact-abstract-type"
            )
            private final CodeableConcept type;
            @Binding(
                bindingName = "Language",
                strength = BindingStrength.Value.PREFERRED,
                description = "A human language.",
                valueSet = "http://hl7.org/fhir/ValueSet/languages"
            )
            private final CodeableConcept language;
            @Required
            private final Markdown text;
            private final Markdown copyright;

            private Abstract(Builder builder) {
                super(builder);
                type = builder.type;
                language = builder.language;
                text = builder.text;
                copyright = builder.copyright;
            }

            /**
             * Used to express the reason for or classification of the abstract.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * Used to express the specific language of the abstract.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getLanguage() {
                return language;
            }

            /**
             * Abstract content.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that is non-null.
             */
            public Markdown getText() {
                return text;
            }

            /**
             * Copyright notice for the abstract.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getCopyright() {
                return copyright;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    (language != null) || 
                    (text != null) || 
                    (copyright != null);
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
                        accept(language, "language", visitor);
                        accept(text, "text", visitor);
                        accept(copyright, "copyright", visitor);
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
                Abstract other = (Abstract) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(language, other.language) && 
                    Objects.equals(text, other.text) && 
                    Objects.equals(copyright, other.copyright);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
                        language, 
                        text, 
                        copyright);
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
                private CodeableConcept language;
                private Markdown text;
                private Markdown copyright;

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
                 * Used to express the reason for or classification of the abstract.
                 * 
                 * @param type
                 *     The kind of abstract
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Used to express the specific language of the abstract.
                 * 
                 * @param language
                 *     Used to express the specific language
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder language(CodeableConcept language) {
                    this.language = language;
                    return this;
                }

                /**
                 * Abstract content.
                 * 
                 * <p>This element is required.
                 * 
                 * @param text
                 *     Abstract content
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder text(Markdown text) {
                    this.text = text;
                    return this;
                }

                /**
                 * Copyright notice for the abstract.
                 * 
                 * @param copyright
                 *     Copyright notice for the abstract
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder copyright(Markdown copyright) {
                    this.copyright = copyright;
                    return this;
                }

                /**
                 * Build the {@link Abstract}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>text</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Abstract}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Abstract per the base specification
                 */
                @Override
                public Abstract build() {
                    Abstract _abstract = new Abstract(this);
                    if (validating) {
                        validate(_abstract);
                    }
                    return _abstract;
                }

                protected void validate(Abstract _abstract) {
                    super.validate(_abstract);
                    ValidationSupport.requireNonNull(_abstract.text, "text");
                    ValidationSupport.requireValueOrChildren(_abstract);
                }

                protected Builder from(Abstract _abstract) {
                    super.from(_abstract);
                    type = _abstract.type;
                    language = _abstract.language;
                    text = _abstract.text;
                    copyright = _abstract.copyright;
                    return this;
                }
            }
        }

        /**
         * The component of the article or artifact.
         */
        public static class Part extends BackboneElement {
            @Binding(
                bindingName = "CitedArtifactPartType",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/cited-artifact-part-type"
            )
            private final CodeableConcept type;
            private final String value;
            @ReferenceTarget({ "Citation" })
            private final Reference baseCitation;

            private Part(Builder builder) {
                super(builder);
                type = builder.type;
                value = builder.value;
                baseCitation = builder.baseCitation;
            }

            /**
             * The kind of component.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * The specification of the component.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getValue() {
                return value;
            }

            /**
             * The citation for the full article or artifact.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getBaseCitation() {
                return baseCitation;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    (value != null) || 
                    (baseCitation != null);
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
                        accept(baseCitation, "baseCitation", visitor);
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
                Part other = (Part) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(value, other.value) && 
                    Objects.equals(baseCitation, other.baseCitation);
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
                        baseCitation);
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
                private Reference baseCitation;

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
                 * The kind of component.
                 * 
                 * @param type
                 *     The kind of component
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
                 *     The specification of the component
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
                 * The specification of the component.
                 * 
                 * @param value
                 *     The specification of the component
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(String value) {
                    this.value = value;
                    return this;
                }

                /**
                 * The citation for the full article or artifact.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Citation}</li>
                 * </ul>
                 * 
                 * @param baseCitation
                 *     The citation for the full article or artifact
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder baseCitation(Reference baseCitation) {
                    this.baseCitation = baseCitation;
                    return this;
                }

                /**
                 * Build the {@link Part}
                 * 
                 * @return
                 *     An immutable object of type {@link Part}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Part per the base specification
                 */
                @Override
                public Part build() {
                    Part part = new Part(this);
                    if (validating) {
                        validate(part);
                    }
                    return part;
                }

                protected void validate(Part part) {
                    super.validate(part);
                    ValidationSupport.checkReferenceType(part.baseCitation, "baseCitation", "Citation");
                    ValidationSupport.requireValueOrChildren(part);
                }

                protected Builder from(Part part) {
                    super.from(part);
                    type = part.type;
                    value = part.value;
                    baseCitation = part.baseCitation;
                    return this;
                }
            }
        }

        /**
         * The artifact related to the cited artifact.
         */
        public static class RelatesTo extends BackboneElement {
            @Binding(
                bindingName = "RelatedArtifactTypeExpanded",
                strength = BindingStrength.Value.REQUIRED,
                valueSet = "http://hl7.org/fhir/ValueSet/related-artifact-type-all|5.0.0"
            )
            @Required
            private final RelatedArtifactTypeExpanded type;
            @Binding(
                bindingName = "CitationArtifactClassifier",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/citation-artifact-classifier"
            )
            private final List<CodeableConcept> classifier;
            private final String label;
            private final String display;
            private final Markdown citation;
            private final Attachment document;
            private final Canonical resource;
            private final Reference resourceReference;

            private RelatesTo(Builder builder) {
                super(builder);
                type = builder.type;
                classifier = Collections.unmodifiableList(builder.classifier);
                label = builder.label;
                display = builder.display;
                citation = builder.citation;
                document = builder.document;
                resource = builder.resource;
                resourceReference = builder.resourceReference;
            }

            /**
             * The type of relationship to the related artifact.
             * 
             * @return
             *     An immutable object of type {@link RelatedArtifactTypeExpanded} that is non-null.
             */
            public RelatedArtifactTypeExpanded getType() {
                return type;
            }

            /**
             * Provides additional classifiers of the related artifact.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getClassifier() {
                return classifier;
            }

            /**
             * A short label that can be used to reference the related artifact from elsewhere in the containing artifact, such as a 
             * footnote index.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getLabel() {
                return label;
            }

            /**
             * A brief description of the document or knowledge resource being referenced, suitable for display to a consumer.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getDisplay() {
                return display;
            }

            /**
             * A bibliographic citation for the related artifact. This text SHOULD be formatted according to an accepted citation 
             * format.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getCitation() {
                return citation;
            }

            /**
             * The document being referenced, represented as an attachment. Do not use this element if using the resource element to 
             * provide the canonical to the related artifact.
             * 
             * @return
             *     An immutable object of type {@link Attachment} that may be null.
             */
            public Attachment getDocument() {
                return document;
            }

            /**
             * The related artifact, such as a library, value set, profile, or other knowledge resource.
             * 
             * @return
             *     An immutable object of type {@link Canonical} that may be null.
             */
            public Canonical getResource() {
                return resource;
            }

            /**
             * The related artifact, if the artifact is not a canonical resource, or a resource reference to a canonical resource.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getResourceReference() {
                return resourceReference;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    !classifier.isEmpty() || 
                    (label != null) || 
                    (display != null) || 
                    (citation != null) || 
                    (document != null) || 
                    (resource != null) || 
                    (resourceReference != null);
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
                        accept(classifier, "classifier", visitor, CodeableConcept.class);
                        accept(label, "label", visitor);
                        accept(display, "display", visitor);
                        accept(citation, "citation", visitor);
                        accept(document, "document", visitor);
                        accept(resource, "resource", visitor);
                        accept(resourceReference, "resourceReference", visitor);
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
                RelatesTo other = (RelatesTo) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(classifier, other.classifier) && 
                    Objects.equals(label, other.label) && 
                    Objects.equals(display, other.display) && 
                    Objects.equals(citation, other.citation) && 
                    Objects.equals(document, other.document) && 
                    Objects.equals(resource, other.resource) && 
                    Objects.equals(resourceReference, other.resourceReference);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
                        classifier, 
                        label, 
                        display, 
                        citation, 
                        document, 
                        resource, 
                        resourceReference);
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
                private RelatedArtifactTypeExpanded type;
                private List<CodeableConcept> classifier = new ArrayList<>();
                private String label;
                private String display;
                private Markdown citation;
                private Attachment document;
                private Canonical resource;
                private Reference resourceReference;

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
                 * The type of relationship to the related artifact.
                 * 
                 * <p>This element is required.
                 * 
                 * @param type
                 *     documentation | justification | citation | predecessor | successor | derived-from | depends-on | composed-of | part-of 
                 *     | amends | amended-with | appends | appended-with | cites | cited-by | comments-on | comment-in | contains | contained-
                 *     in | corrects | correction-in | replaces | replaced-with | retracts | retracted-by | signs | similar-to | supports | 
                 *     supported-with | transforms | transformed-into | transformed-with | documents | specification-of | created-with | cite-
                 *     as | reprint | reprint-of
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(RelatedArtifactTypeExpanded type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Provides additional classifiers of the related artifact.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param classifier
                 *     Additional classifiers
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
                 * Provides additional classifiers of the related artifact.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param classifier
                 *     Additional classifiers
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
                 * Convenience method for setting {@code label}.
                 * 
                 * @param label
                 *     Short label
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #label(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder label(java.lang.String label) {
                    this.label = (label == null) ? null : String.of(label);
                    return this;
                }

                /**
                 * A short label that can be used to reference the related artifact from elsewhere in the containing artifact, such as a 
                 * footnote index.
                 * 
                 * @param label
                 *     Short label
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder label(String label) {
                    this.label = label;
                    return this;
                }

                /**
                 * Convenience method for setting {@code display}.
                 * 
                 * @param display
                 *     Brief description of the related artifact
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #display(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder display(java.lang.String display) {
                    this.display = (display == null) ? null : String.of(display);
                    return this;
                }

                /**
                 * A brief description of the document or knowledge resource being referenced, suitable for display to a consumer.
                 * 
                 * @param display
                 *     Brief description of the related artifact
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder display(String display) {
                    this.display = display;
                    return this;
                }

                /**
                 * A bibliographic citation for the related artifact. This text SHOULD be formatted according to an accepted citation 
                 * format.
                 * 
                 * @param citation
                 *     Bibliographic citation for the artifact
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder citation(Markdown citation) {
                    this.citation = citation;
                    return this;
                }

                /**
                 * The document being referenced, represented as an attachment. Do not use this element if using the resource element to 
                 * provide the canonical to the related artifact.
                 * 
                 * @param document
                 *     What document is being referenced
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder document(Attachment document) {
                    this.document = document;
                    return this;
                }

                /**
                 * The related artifact, such as a library, value set, profile, or other knowledge resource.
                 * 
                 * @param resource
                 *     What artifact is being referenced
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder resource(Canonical resource) {
                    this.resource = resource;
                    return this;
                }

                /**
                 * The related artifact, if the artifact is not a canonical resource, or a resource reference to a canonical resource.
                 * 
                 * @param resourceReference
                 *     What artifact, if not a conformance resource
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder resourceReference(Reference resourceReference) {
                    this.resourceReference = resourceReference;
                    return this;
                }

                /**
                 * Build the {@link RelatesTo}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>type</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link RelatesTo}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid RelatesTo per the base specification
                 */
                @Override
                public RelatesTo build() {
                    RelatesTo relatesTo = new RelatesTo(this);
                    if (validating) {
                        validate(relatesTo);
                    }
                    return relatesTo;
                }

                protected void validate(RelatesTo relatesTo) {
                    super.validate(relatesTo);
                    ValidationSupport.requireNonNull(relatesTo.type, "type");
                    ValidationSupport.checkList(relatesTo.classifier, "classifier", CodeableConcept.class);
                    ValidationSupport.requireValueOrChildren(relatesTo);
                }

                protected Builder from(RelatesTo relatesTo) {
                    super.from(relatesTo);
                    type = relatesTo.type;
                    classifier.addAll(relatesTo.classifier);
                    label = relatesTo.label;
                    display = relatesTo.display;
                    citation = relatesTo.citation;
                    document = relatesTo.document;
                    resource = relatesTo.resource;
                    resourceReference = relatesTo.resourceReference;
                    return this;
                }
            }
        }

        /**
         * If multiple, used to represent alternative forms of the article that are not separate citations.
         */
        public static class PublicationForm extends BackboneElement {
            private final PublishedIn publishedIn;
            @Binding(
                bindingName = "CitedMedium",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/cited-medium"
            )
            private final CodeableConcept citedMedium;
            private final String volume;
            private final String issue;
            private final DateTime articleDate;
            private final String publicationDateText;
            private final String publicationDateSeason;
            private final DateTime lastRevisionDate;
            @Binding(
                bindingName = "Language",
                strength = BindingStrength.Value.PREFERRED,
                description = "A human language.",
                valueSet = "http://hl7.org/fhir/ValueSet/languages"
            )
            private final List<CodeableConcept> language;
            private final String accessionNumber;
            private final String pageString;
            private final String firstPage;
            private final String lastPage;
            private final String pageCount;
            private final Markdown copyright;

            private PublicationForm(Builder builder) {
                super(builder);
                publishedIn = builder.publishedIn;
                citedMedium = builder.citedMedium;
                volume = builder.volume;
                issue = builder.issue;
                articleDate = builder.articleDate;
                publicationDateText = builder.publicationDateText;
                publicationDateSeason = builder.publicationDateSeason;
                lastRevisionDate = builder.lastRevisionDate;
                language = Collections.unmodifiableList(builder.language);
                accessionNumber = builder.accessionNumber;
                pageString = builder.pageString;
                firstPage = builder.firstPage;
                lastPage = builder.lastPage;
                pageCount = builder.pageCount;
                copyright = builder.copyright;
            }

            /**
             * The collection the cited article or artifact is published in.
             * 
             * @return
             *     An immutable object of type {@link PublishedIn} that may be null.
             */
            public PublishedIn getPublishedIn() {
                return publishedIn;
            }

            /**
             * Describes the form of the medium cited. Common codes are "Internet" or "Print". The CitedMedium value set has 6 codes. 
             * The codes internet, print, and offline-digital-storage are the common codes for a typical publication form, though 
             * internet and print are more common for study citations. Three additional codes (each appending one of the primary 
             * codes with "-without-issue" are used for situations when a study is published both within an issue (of a periodical 
             * release as commonly done for journals) AND is published separately from the issue (as commonly done with early online 
             * publication), to represent specific identification of the publication form not associated with the issue.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getCitedMedium() {
                return citedMedium;
            }

            /**
             * Volume number of journal or other collection in which the article is published.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getVolume() {
                return volume;
            }

            /**
             * Issue, part or supplement of journal or other collection in which the article is published.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getIssue() {
                return issue;
            }

            /**
             * The date the article was added to the database, or the date the article was released.
             * 
             * @return
             *     An immutable object of type {@link DateTime} that may be null.
             */
            public DateTime getArticleDate() {
                return articleDate;
            }

            /**
             * Text representation of the date on which the issue of the cited artifact was published.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getPublicationDateText() {
                return publicationDateText;
            }

            /**
             * Spring, Summer, Fall/Autumn, Winter.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getPublicationDateSeason() {
                return publicationDateSeason;
            }

            /**
             * The date the article was last revised or updated in the database.
             * 
             * @return
             *     An immutable object of type {@link DateTime} that may be null.
             */
            public DateTime getLastRevisionDate() {
                return lastRevisionDate;
            }

            /**
             * The language or languages in which this form of the article is published.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getLanguage() {
                return language;
            }

            /**
             * Entry number or identifier for inclusion in a database.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getAccessionNumber() {
                return accessionNumber;
            }

            /**
             * Used for full display of pagination.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getPageString() {
                return pageString;
            }

            /**
             * Used for isolated representation of first page.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getFirstPage() {
                return firstPage;
            }

            /**
             * Used for isolated representation of last page.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getLastPage() {
                return lastPage;
            }

            /**
             * Actual or approximate number of pages or screens. Distinct from reporting the page numbers.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getPageCount() {
                return pageCount;
            }

            /**
             * Copyright notice for the full article or artifact.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getCopyright() {
                return copyright;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (publishedIn != null) || 
                    (citedMedium != null) || 
                    (volume != null) || 
                    (issue != null) || 
                    (articleDate != null) || 
                    (publicationDateText != null) || 
                    (publicationDateSeason != null) || 
                    (lastRevisionDate != null) || 
                    !language.isEmpty() || 
                    (accessionNumber != null) || 
                    (pageString != null) || 
                    (firstPage != null) || 
                    (lastPage != null) || 
                    (pageCount != null) || 
                    (copyright != null);
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
                        accept(publishedIn, "publishedIn", visitor);
                        accept(citedMedium, "citedMedium", visitor);
                        accept(volume, "volume", visitor);
                        accept(issue, "issue", visitor);
                        accept(articleDate, "articleDate", visitor);
                        accept(publicationDateText, "publicationDateText", visitor);
                        accept(publicationDateSeason, "publicationDateSeason", visitor);
                        accept(lastRevisionDate, "lastRevisionDate", visitor);
                        accept(language, "language", visitor, CodeableConcept.class);
                        accept(accessionNumber, "accessionNumber", visitor);
                        accept(pageString, "pageString", visitor);
                        accept(firstPage, "firstPage", visitor);
                        accept(lastPage, "lastPage", visitor);
                        accept(pageCount, "pageCount", visitor);
                        accept(copyright, "copyright", visitor);
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
                PublicationForm other = (PublicationForm) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(publishedIn, other.publishedIn) && 
                    Objects.equals(citedMedium, other.citedMedium) && 
                    Objects.equals(volume, other.volume) && 
                    Objects.equals(issue, other.issue) && 
                    Objects.equals(articleDate, other.articleDate) && 
                    Objects.equals(publicationDateText, other.publicationDateText) && 
                    Objects.equals(publicationDateSeason, other.publicationDateSeason) && 
                    Objects.equals(lastRevisionDate, other.lastRevisionDate) && 
                    Objects.equals(language, other.language) && 
                    Objects.equals(accessionNumber, other.accessionNumber) && 
                    Objects.equals(pageString, other.pageString) && 
                    Objects.equals(firstPage, other.firstPage) && 
                    Objects.equals(lastPage, other.lastPage) && 
                    Objects.equals(pageCount, other.pageCount) && 
                    Objects.equals(copyright, other.copyright);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        publishedIn, 
                        citedMedium, 
                        volume, 
                        issue, 
                        articleDate, 
                        publicationDateText, 
                        publicationDateSeason, 
                        lastRevisionDate, 
                        language, 
                        accessionNumber, 
                        pageString, 
                        firstPage, 
                        lastPage, 
                        pageCount, 
                        copyright);
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
                private PublishedIn publishedIn;
                private CodeableConcept citedMedium;
                private String volume;
                private String issue;
                private DateTime articleDate;
                private String publicationDateText;
                private String publicationDateSeason;
                private DateTime lastRevisionDate;
                private List<CodeableConcept> language = new ArrayList<>();
                private String accessionNumber;
                private String pageString;
                private String firstPage;
                private String lastPage;
                private String pageCount;
                private Markdown copyright;

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
                 * The collection the cited article or artifact is published in.
                 * 
                 * @param publishedIn
                 *     The collection the cited article or artifact is published in
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder publishedIn(PublishedIn publishedIn) {
                    this.publishedIn = publishedIn;
                    return this;
                }

                /**
                 * Describes the form of the medium cited. Common codes are "Internet" or "Print". The CitedMedium value set has 6 codes. 
                 * The codes internet, print, and offline-digital-storage are the common codes for a typical publication form, though 
                 * internet and print are more common for study citations. Three additional codes (each appending one of the primary 
                 * codes with "-without-issue" are used for situations when a study is published both within an issue (of a periodical 
                 * release as commonly done for journals) AND is published separately from the issue (as commonly done with early online 
                 * publication), to represent specific identification of the publication form not associated with the issue.
                 * 
                 * @param citedMedium
                 *     Internet or Print
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder citedMedium(CodeableConcept citedMedium) {
                    this.citedMedium = citedMedium;
                    return this;
                }

                /**
                 * Convenience method for setting {@code volume}.
                 * 
                 * @param volume
                 *     Volume number of journal or other collection in which the article is published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #volume(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder volume(java.lang.String volume) {
                    this.volume = (volume == null) ? null : String.of(volume);
                    return this;
                }

                /**
                 * Volume number of journal or other collection in which the article is published.
                 * 
                 * @param volume
                 *     Volume number of journal or other collection in which the article is published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder volume(String volume) {
                    this.volume = volume;
                    return this;
                }

                /**
                 * Convenience method for setting {@code issue}.
                 * 
                 * @param issue
                 *     Issue, part or supplement of journal or other collection in which the article is published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #issue(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder issue(java.lang.String issue) {
                    this.issue = (issue == null) ? null : String.of(issue);
                    return this;
                }

                /**
                 * Issue, part or supplement of journal or other collection in which the article is published.
                 * 
                 * @param issue
                 *     Issue, part or supplement of journal or other collection in which the article is published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder issue(String issue) {
                    this.issue = issue;
                    return this;
                }

                /**
                 * The date the article was added to the database, or the date the article was released.
                 * 
                 * @param articleDate
                 *     The date the article was added to the database, or the date the article was released
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder articleDate(DateTime articleDate) {
                    this.articleDate = articleDate;
                    return this;
                }

                /**
                 * Convenience method for setting {@code publicationDateText}.
                 * 
                 * @param publicationDateText
                 *     Text representation of the date on which the issue of the cited artifact was published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #publicationDateText(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder publicationDateText(java.lang.String publicationDateText) {
                    this.publicationDateText = (publicationDateText == null) ? null : String.of(publicationDateText);
                    return this;
                }

                /**
                 * Text representation of the date on which the issue of the cited artifact was published.
                 * 
                 * @param publicationDateText
                 *     Text representation of the date on which the issue of the cited artifact was published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder publicationDateText(String publicationDateText) {
                    this.publicationDateText = publicationDateText;
                    return this;
                }

                /**
                 * Convenience method for setting {@code publicationDateSeason}.
                 * 
                 * @param publicationDateSeason
                 *     Season in which the cited artifact was published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #publicationDateSeason(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder publicationDateSeason(java.lang.String publicationDateSeason) {
                    this.publicationDateSeason = (publicationDateSeason == null) ? null : String.of(publicationDateSeason);
                    return this;
                }

                /**
                 * Spring, Summer, Fall/Autumn, Winter.
                 * 
                 * @param publicationDateSeason
                 *     Season in which the cited artifact was published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder publicationDateSeason(String publicationDateSeason) {
                    this.publicationDateSeason = publicationDateSeason;
                    return this;
                }

                /**
                 * The date the article was last revised or updated in the database.
                 * 
                 * @param lastRevisionDate
                 *     The date the article was last revised or updated in the database
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder lastRevisionDate(DateTime lastRevisionDate) {
                    this.lastRevisionDate = lastRevisionDate;
                    return this;
                }

                /**
                 * The language or languages in which this form of the article is published.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param language
                 *     Language(s) in which this form of the article is published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder language(CodeableConcept... language) {
                    for (CodeableConcept value : language) {
                        this.language.add(value);
                    }
                    return this;
                }

                /**
                 * The language or languages in which this form of the article is published.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param language
                 *     Language(s) in which this form of the article is published
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder language(Collection<CodeableConcept> language) {
                    this.language = new ArrayList<>(language);
                    return this;
                }

                /**
                 * Convenience method for setting {@code accessionNumber}.
                 * 
                 * @param accessionNumber
                 *     Entry number or identifier for inclusion in a database
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #accessionNumber(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder accessionNumber(java.lang.String accessionNumber) {
                    this.accessionNumber = (accessionNumber == null) ? null : String.of(accessionNumber);
                    return this;
                }

                /**
                 * Entry number or identifier for inclusion in a database.
                 * 
                 * @param accessionNumber
                 *     Entry number or identifier for inclusion in a database
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder accessionNumber(String accessionNumber) {
                    this.accessionNumber = accessionNumber;
                    return this;
                }

                /**
                 * Convenience method for setting {@code pageString}.
                 * 
                 * @param pageString
                 *     Used for full display of pagination
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #pageString(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder pageString(java.lang.String pageString) {
                    this.pageString = (pageString == null) ? null : String.of(pageString);
                    return this;
                }

                /**
                 * Used for full display of pagination.
                 * 
                 * @param pageString
                 *     Used for full display of pagination
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder pageString(String pageString) {
                    this.pageString = pageString;
                    return this;
                }

                /**
                 * Convenience method for setting {@code firstPage}.
                 * 
                 * @param firstPage
                 *     Used for isolated representation of first page
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #firstPage(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder firstPage(java.lang.String firstPage) {
                    this.firstPage = (firstPage == null) ? null : String.of(firstPage);
                    return this;
                }

                /**
                 * Used for isolated representation of first page.
                 * 
                 * @param firstPage
                 *     Used for isolated representation of first page
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder firstPage(String firstPage) {
                    this.firstPage = firstPage;
                    return this;
                }

                /**
                 * Convenience method for setting {@code lastPage}.
                 * 
                 * @param lastPage
                 *     Used for isolated representation of last page
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #lastPage(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder lastPage(java.lang.String lastPage) {
                    this.lastPage = (lastPage == null) ? null : String.of(lastPage);
                    return this;
                }

                /**
                 * Used for isolated representation of last page.
                 * 
                 * @param lastPage
                 *     Used for isolated representation of last page
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder lastPage(String lastPage) {
                    this.lastPage = lastPage;
                    return this;
                }

                /**
                 * Convenience method for setting {@code pageCount}.
                 * 
                 * @param pageCount
                 *     Number of pages or screens
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #pageCount(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder pageCount(java.lang.String pageCount) {
                    this.pageCount = (pageCount == null) ? null : String.of(pageCount);
                    return this;
                }

                /**
                 * Actual or approximate number of pages or screens. Distinct from reporting the page numbers.
                 * 
                 * @param pageCount
                 *     Number of pages or screens
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder pageCount(String pageCount) {
                    this.pageCount = pageCount;
                    return this;
                }

                /**
                 * Copyright notice for the full article or artifact.
                 * 
                 * @param copyright
                 *     Copyright notice for the full article or artifact
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder copyright(Markdown copyright) {
                    this.copyright = copyright;
                    return this;
                }

                /**
                 * Build the {@link PublicationForm}
                 * 
                 * @return
                 *     An immutable object of type {@link PublicationForm}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid PublicationForm per the base specification
                 */
                @Override
                public PublicationForm build() {
                    PublicationForm publicationForm = new PublicationForm(this);
                    if (validating) {
                        validate(publicationForm);
                    }
                    return publicationForm;
                }

                protected void validate(PublicationForm publicationForm) {
                    super.validate(publicationForm);
                    ValidationSupport.checkList(publicationForm.language, "language", CodeableConcept.class);
                    ValidationSupport.requireValueOrChildren(publicationForm);
                }

                protected Builder from(PublicationForm publicationForm) {
                    super.from(publicationForm);
                    publishedIn = publicationForm.publishedIn;
                    citedMedium = publicationForm.citedMedium;
                    volume = publicationForm.volume;
                    issue = publicationForm.issue;
                    articleDate = publicationForm.articleDate;
                    publicationDateText = publicationForm.publicationDateText;
                    publicationDateSeason = publicationForm.publicationDateSeason;
                    lastRevisionDate = publicationForm.lastRevisionDate;
                    language.addAll(publicationForm.language);
                    accessionNumber = publicationForm.accessionNumber;
                    pageString = publicationForm.pageString;
                    firstPage = publicationForm.firstPage;
                    lastPage = publicationForm.lastPage;
                    pageCount = publicationForm.pageCount;
                    copyright = publicationForm.copyright;
                    return this;
                }
            }

            /**
             * The collection the cited article or artifact is published in.
             */
            public static class PublishedIn extends BackboneElement {
                @Binding(
                    bindingName = "PublishedInType",
                    strength = BindingStrength.Value.EXTENSIBLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/published-in-type"
                )
                private final CodeableConcept type;
                private final List<Identifier> identifier;
                private final String title;
                @ReferenceTarget({ "Organization" })
                private final Reference publisher;
                private final String publisherLocation;

                private PublishedIn(Builder builder) {
                    super(builder);
                    type = builder.type;
                    identifier = Collections.unmodifiableList(builder.identifier);
                    title = builder.title;
                    publisher = builder.publisher;
                    publisherLocation = builder.publisherLocation;
                }

                /**
                 * Kind of container (e.g. Periodical, database, or book).
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getType() {
                    return type;
                }

                /**
                 * Journal identifiers include ISSN, ISO Abbreviation and NLMuniqueID; Book identifiers include ISBN.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
                 */
                public List<Identifier> getIdentifier() {
                    return identifier;
                }

                /**
                 * Name of the database or title of the book or journal.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getTitle() {
                    return title;
                }

                /**
                 * Name of or resource describing the publisher.
                 * 
                 * @return
                 *     An immutable object of type {@link Reference} that may be null.
                 */
                public Reference getPublisher() {
                    return publisher;
                }

                /**
                 * Geographic location of the publisher.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getPublisherLocation() {
                    return publisherLocation;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (type != null) || 
                        !identifier.isEmpty() || 
                        (title != null) || 
                        (publisher != null) || 
                        (publisherLocation != null);
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
                            accept(identifier, "identifier", visitor, Identifier.class);
                            accept(title, "title", visitor);
                            accept(publisher, "publisher", visitor);
                            accept(publisherLocation, "publisherLocation", visitor);
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
                    PublishedIn other = (PublishedIn) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(type, other.type) && 
                        Objects.equals(identifier, other.identifier) && 
                        Objects.equals(title, other.title) && 
                        Objects.equals(publisher, other.publisher) && 
                        Objects.equals(publisherLocation, other.publisherLocation);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            type, 
                            identifier, 
                            title, 
                            publisher, 
                            publisherLocation);
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
                    private List<Identifier> identifier = new ArrayList<>();
                    private String title;
                    private Reference publisher;
                    private String publisherLocation;

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
                     * Kind of container (e.g. Periodical, database, or book).
                     * 
                     * @param type
                     *     Kind of container (e.g. Periodical, database, or book)
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder type(CodeableConcept type) {
                        this.type = type;
                        return this;
                    }

                    /**
                     * Journal identifiers include ISSN, ISO Abbreviation and NLMuniqueID; Book identifiers include ISBN.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param identifier
                     *     Journal identifiers include ISSN, ISO Abbreviation and NLMuniqueID; Book identifiers include ISBN
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
                     * Journal identifiers include ISSN, ISO Abbreviation and NLMuniqueID; Book identifiers include ISBN.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param identifier
                     *     Journal identifiers include ISSN, ISO Abbreviation and NLMuniqueID; Book identifiers include ISBN
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
                     * Convenience method for setting {@code title}.
                     * 
                     * @param title
                     *     Name of the database or title of the book or journal
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
                     * Name of the database or title of the book or journal.
                     * 
                     * @param title
                     *     Name of the database or title of the book or journal
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder title(String title) {
                        this.title = title;
                        return this;
                    }

                    /**
                     * Name of or resource describing the publisher.
                     * 
                     * <p>Allowed resource types for this reference:
                     * <ul>
                     * <li>{@link Organization}</li>
                     * </ul>
                     * 
                     * @param publisher
                     *     Name of or resource describing the publisher
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder publisher(Reference publisher) {
                        this.publisher = publisher;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code publisherLocation}.
                     * 
                     * @param publisherLocation
                     *     Geographic location of the publisher
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #publisherLocation(org.linuxforhealth.fhir.model.type.String)
                     */
                    public Builder publisherLocation(java.lang.String publisherLocation) {
                        this.publisherLocation = (publisherLocation == null) ? null : String.of(publisherLocation);
                        return this;
                    }

                    /**
                     * Geographic location of the publisher.
                     * 
                     * @param publisherLocation
                     *     Geographic location of the publisher
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder publisherLocation(String publisherLocation) {
                        this.publisherLocation = publisherLocation;
                        return this;
                    }

                    /**
                     * Build the {@link PublishedIn}
                     * 
                     * @return
                     *     An immutable object of type {@link PublishedIn}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid PublishedIn per the base specification
                     */
                    @Override
                    public PublishedIn build() {
                        PublishedIn publishedIn = new PublishedIn(this);
                        if (validating) {
                            validate(publishedIn);
                        }
                        return publishedIn;
                    }

                    protected void validate(PublishedIn publishedIn) {
                        super.validate(publishedIn);
                        ValidationSupport.checkList(publishedIn.identifier, "identifier", Identifier.class);
                        ValidationSupport.checkReferenceType(publishedIn.publisher, "publisher", "Organization");
                        ValidationSupport.requireValueOrChildren(publishedIn);
                    }

                    protected Builder from(PublishedIn publishedIn) {
                        super.from(publishedIn);
                        type = publishedIn.type;
                        identifier.addAll(publishedIn.identifier);
                        title = publishedIn.title;
                        publisher = publishedIn.publisher;
                        publisherLocation = publishedIn.publisherLocation;
                        return this;
                    }
                }
            }
        }

        /**
         * Used for any URL for the article or artifact cited.
         */
        public static class WebLocation extends BackboneElement {
            @Binding(
                bindingName = "ArtifactUrlClassifier",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/artifact-url-classifier"
            )
            private final List<CodeableConcept> classifier;
            private final Uri url;

            private WebLocation(Builder builder) {
                super(builder);
                classifier = Collections.unmodifiableList(builder.classifier);
                url = builder.url;
            }

            /**
             * A characterization of the object expected at the web location.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getClassifier() {
                return classifier;
            }

            /**
             * The specific URL.
             * 
             * @return
             *     An immutable object of type {@link Uri} that may be null.
             */
            public Uri getUrl() {
                return url;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !classifier.isEmpty() || 
                    (url != null);
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
                        accept(classifier, "classifier", visitor, CodeableConcept.class);
                        accept(url, "url", visitor);
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
                WebLocation other = (WebLocation) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(classifier, other.classifier) && 
                    Objects.equals(url, other.url);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        classifier, 
                        url);
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
                private List<CodeableConcept> classifier = new ArrayList<>();
                private Uri url;

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
                 * A characterization of the object expected at the web location.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param classifier
                 *     Code the reason for different URLs, e.g. abstract and full-text
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
                 * A characterization of the object expected at the web location.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param classifier
                 *     Code the reason for different URLs, e.g. abstract and full-text
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
                 * The specific URL.
                 * 
                 * @param url
                 *     The specific URL
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder url(Uri url) {
                    this.url = url;
                    return this;
                }

                /**
                 * Build the {@link WebLocation}
                 * 
                 * @return
                 *     An immutable object of type {@link WebLocation}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid WebLocation per the base specification
                 */
                @Override
                public WebLocation build() {
                    WebLocation webLocation = new WebLocation(this);
                    if (validating) {
                        validate(webLocation);
                    }
                    return webLocation;
                }

                protected void validate(WebLocation webLocation) {
                    super.validate(webLocation);
                    ValidationSupport.checkList(webLocation.classifier, "classifier", CodeableConcept.class);
                    ValidationSupport.requireValueOrChildren(webLocation);
                }

                protected Builder from(WebLocation webLocation) {
                    super.from(webLocation);
                    classifier.addAll(webLocation.classifier);
                    url = webLocation.url;
                    return this;
                }
            }
        }

        /**
         * The assignment to an organizing scheme.
         */
        public static class Classification extends BackboneElement {
            @Binding(
                bindingName = "CitedArtifactClassificationType",
                strength = BindingStrength.Value.EXTENSIBLE,
                valueSet = "http://hl7.org/fhir/ValueSet/cited-artifact-classification-type"
            )
            private final CodeableConcept type;
            @Binding(
                bindingName = "CitationArtifactClassifier",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/citation-artifact-classifier"
            )
            private final List<CodeableConcept> classifier;
            @ReferenceTarget({ "ArtifactAssessment" })
            private final List<Reference> artifactAssessment;

            private Classification(Builder builder) {
                super(builder);
                type = builder.type;
                classifier = Collections.unmodifiableList(builder.classifier);
                artifactAssessment = Collections.unmodifiableList(builder.artifactAssessment);
            }

            /**
             * The kind of classifier (e.g. publication type, keyword).
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * The specific classification value.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getClassifier() {
                return classifier;
            }

            /**
             * Complex or externally created classification.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
             */
            public List<Reference> getArtifactAssessment() {
                return artifactAssessment;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    !classifier.isEmpty() || 
                    !artifactAssessment.isEmpty();
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
                        accept(classifier, "classifier", visitor, CodeableConcept.class);
                        accept(artifactAssessment, "artifactAssessment", visitor, Reference.class);
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
                Classification other = (Classification) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(classifier, other.classifier) && 
                    Objects.equals(artifactAssessment, other.artifactAssessment);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
                        classifier, 
                        artifactAssessment);
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
                private List<CodeableConcept> classifier = new ArrayList<>();
                private List<Reference> artifactAssessment = new ArrayList<>();

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
                 * The kind of classifier (e.g. publication type, keyword).
                 * 
                 * @param type
                 *     The kind of classifier (e.g. publication type, keyword)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * The specific classification value.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param classifier
                 *     The specific classification value
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
                 * The specific classification value.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param classifier
                 *     The specific classification value
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
                 * Complex or externally created classification.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
                 * <ul>
                 * <li>{@link ArtifactAssessment}</li>
                 * </ul>
                 * 
                 * @param artifactAssessment
                 *     Complex or externally created classification
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder artifactAssessment(Reference... artifactAssessment) {
                    for (Reference value : artifactAssessment) {
                        this.artifactAssessment.add(value);
                    }
                    return this;
                }

                /**
                 * Complex or externally created classification.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
                 * <ul>
                 * <li>{@link ArtifactAssessment}</li>
                 * </ul>
                 * 
                 * @param artifactAssessment
                 *     Complex or externally created classification
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder artifactAssessment(Collection<Reference> artifactAssessment) {
                    this.artifactAssessment = new ArrayList<>(artifactAssessment);
                    return this;
                }

                /**
                 * Build the {@link Classification}
                 * 
                 * @return
                 *     An immutable object of type {@link Classification}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Classification per the base specification
                 */
                @Override
                public Classification build() {
                    Classification classification = new Classification(this);
                    if (validating) {
                        validate(classification);
                    }
                    return classification;
                }

                protected void validate(Classification classification) {
                    super.validate(classification);
                    ValidationSupport.checkList(classification.classifier, "classifier", CodeableConcept.class);
                    ValidationSupport.checkList(classification.artifactAssessment, "artifactAssessment", Reference.class);
                    ValidationSupport.checkReferenceType(classification.artifactAssessment, "artifactAssessment", "ArtifactAssessment");
                    ValidationSupport.requireValueOrChildren(classification);
                }

                protected Builder from(Classification classification) {
                    super.from(classification);
                    type = classification.type;
                    classifier.addAll(classification.classifier);
                    artifactAssessment.addAll(classification.artifactAssessment);
                    return this;
                }
            }
        }

        /**
         * This element is used to list authors and other contributors, their contact information, specific contributions, and 
         * summary statements.
         */
        public static class Contributorship extends BackboneElement {
            private final Boolean complete;
            private final List<Entry> entry;
            private final List<Summary> summary;

            private Contributorship(Builder builder) {
                super(builder);
                complete = builder.complete;
                entry = Collections.unmodifiableList(builder.entry);
                summary = Collections.unmodifiableList(builder.summary);
            }

            /**
             * Indicates if the list includes all authors and/or contributors.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getComplete() {
                return complete;
            }

            /**
             * An individual entity named as a contributor, for example in the author list or contributor list.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Entry} that may be empty.
             */
            public List<Entry> getEntry() {
                return entry;
            }

            /**
             * Used to record a display of the author/contributor list without separate data element for each list member.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Summary} that may be empty.
             */
            public List<Summary> getSummary() {
                return summary;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (complete != null) || 
                    !entry.isEmpty() || 
                    !summary.isEmpty();
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
                        accept(complete, "complete", visitor);
                        accept(entry, "entry", visitor, Entry.class);
                        accept(summary, "summary", visitor, Summary.class);
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
                Contributorship other = (Contributorship) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(complete, other.complete) && 
                    Objects.equals(entry, other.entry) && 
                    Objects.equals(summary, other.summary);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        complete, 
                        entry, 
                        summary);
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
                private Boolean complete;
                private List<Entry> entry = new ArrayList<>();
                private List<Summary> summary = new ArrayList<>();

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
                 * Convenience method for setting {@code complete}.
                 * 
                 * @param complete
                 *     Indicates if the list includes all authors and/or contributors
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #complete(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder complete(java.lang.Boolean complete) {
                    this.complete = (complete == null) ? null : Boolean.of(complete);
                    return this;
                }

                /**
                 * Indicates if the list includes all authors and/or contributors.
                 * 
                 * @param complete
                 *     Indicates if the list includes all authors and/or contributors
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder complete(Boolean complete) {
                    this.complete = complete;
                    return this;
                }

                /**
                 * An individual entity named as a contributor, for example in the author list or contributor list.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param entry
                 *     An individual entity named as a contributor
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder entry(Entry... entry) {
                    for (Entry value : entry) {
                        this.entry.add(value);
                    }
                    return this;
                }

                /**
                 * An individual entity named as a contributor, for example in the author list or contributor list.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param entry
                 *     An individual entity named as a contributor
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder entry(Collection<Entry> entry) {
                    this.entry = new ArrayList<>(entry);
                    return this;
                }

                /**
                 * Used to record a display of the author/contributor list without separate data element for each list member.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param summary
                 *     Used to record a display of the author/contributor list without separate data element for each list member
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder summary(Summary... summary) {
                    for (Summary value : summary) {
                        this.summary.add(value);
                    }
                    return this;
                }

                /**
                 * Used to record a display of the author/contributor list without separate data element for each list member.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param summary
                 *     Used to record a display of the author/contributor list without separate data element for each list member
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder summary(Collection<Summary> summary) {
                    this.summary = new ArrayList<>(summary);
                    return this;
                }

                /**
                 * Build the {@link Contributorship}
                 * 
                 * @return
                 *     An immutable object of type {@link Contributorship}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Contributorship per the base specification
                 */
                @Override
                public Contributorship build() {
                    Contributorship contributorship = new Contributorship(this);
                    if (validating) {
                        validate(contributorship);
                    }
                    return contributorship;
                }

                protected void validate(Contributorship contributorship) {
                    super.validate(contributorship);
                    ValidationSupport.checkList(contributorship.entry, "entry", Entry.class);
                    ValidationSupport.checkList(contributorship.summary, "summary", Summary.class);
                    ValidationSupport.requireValueOrChildren(contributorship);
                }

                protected Builder from(Contributorship contributorship) {
                    super.from(contributorship);
                    complete = contributorship.complete;
                    entry.addAll(contributorship.entry);
                    summary.addAll(contributorship.summary);
                    return this;
                }
            }

            /**
             * An individual entity named as a contributor, for example in the author list or contributor list.
             */
            public static class Entry extends BackboneElement {
                @ReferenceTarget({ "Practitioner", "Organization" })
                @Required
                private final Reference contributor;
                private final String forenameInitials;
                @ReferenceTarget({ "Organization", "PractitionerRole" })
                private final List<Reference> affiliation;
                @Binding(
                    bindingName = "ArtifactContributionType",
                    strength = BindingStrength.Value.EXTENSIBLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/artifact-contribution-type"
                )
                private final List<CodeableConcept> contributionType;
                @Binding(
                    bindingName = "ContributorRole",
                    strength = BindingStrength.Value.EXTENSIBLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/contributor-role"
                )
                private final CodeableConcept role;
                private final List<ContributionInstance> contributionInstance;
                private final Boolean correspondingContact;
                private final PositiveInt rankingOrder;

                private Entry(Builder builder) {
                    super(builder);
                    contributor = builder.contributor;
                    forenameInitials = builder.forenameInitials;
                    affiliation = Collections.unmodifiableList(builder.affiliation);
                    contributionType = Collections.unmodifiableList(builder.contributionType);
                    role = builder.role;
                    contributionInstance = Collections.unmodifiableList(builder.contributionInstance);
                    correspondingContact = builder.correspondingContact;
                    rankingOrder = builder.rankingOrder;
                }

                /**
                 * The identity of the individual contributor.
                 * 
                 * @return
                 *     An immutable object of type {@link Reference} that is non-null.
                 */
                public Reference getContributor() {
                    return contributor;
                }

                /**
                 * For citation styles that use initials.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getForenameInitials() {
                    return forenameInitials;
                }

                /**
                 * Organization affiliated with the contributor.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
                 */
                public List<Reference> getAffiliation() {
                    return affiliation;
                }

                /**
                 * This element identifies the specific nature of an individual’s contribution with respect to the cited work.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
                 */
                public List<CodeableConcept> getContributionType() {
                    return contributionType;
                }

                /**
                 * The role of the contributor (e.g. author, editor, reviewer, funder).
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getRole() {
                    return role;
                }

                /**
                 * Contributions with accounting for time or number.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link ContributionInstance} that may be empty.
                 */
                public List<ContributionInstance> getContributionInstance() {
                    return contributionInstance;
                }

                /**
                 * Whether the contributor is the corresponding contributor for the role.
                 * 
                 * @return
                 *     An immutable object of type {@link Boolean} that may be null.
                 */
                public Boolean getCorrespondingContact() {
                    return correspondingContact;
                }

                /**
                 * Provides a numerical ranking to represent the degree of contributorship relative to other contributors, such as 1 for 
                 * first author and 2 for second author.
                 * 
                 * @return
                 *     An immutable object of type {@link PositiveInt} that may be null.
                 */
                public PositiveInt getRankingOrder() {
                    return rankingOrder;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (contributor != null) || 
                        (forenameInitials != null) || 
                        !affiliation.isEmpty() || 
                        !contributionType.isEmpty() || 
                        (role != null) || 
                        !contributionInstance.isEmpty() || 
                        (correspondingContact != null) || 
                        (rankingOrder != null);
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
                            accept(contributor, "contributor", visitor);
                            accept(forenameInitials, "forenameInitials", visitor);
                            accept(affiliation, "affiliation", visitor, Reference.class);
                            accept(contributionType, "contributionType", visitor, CodeableConcept.class);
                            accept(role, "role", visitor);
                            accept(contributionInstance, "contributionInstance", visitor, ContributionInstance.class);
                            accept(correspondingContact, "correspondingContact", visitor);
                            accept(rankingOrder, "rankingOrder", visitor);
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
                    Entry other = (Entry) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(contributor, other.contributor) && 
                        Objects.equals(forenameInitials, other.forenameInitials) && 
                        Objects.equals(affiliation, other.affiliation) && 
                        Objects.equals(contributionType, other.contributionType) && 
                        Objects.equals(role, other.role) && 
                        Objects.equals(contributionInstance, other.contributionInstance) && 
                        Objects.equals(correspondingContact, other.correspondingContact) && 
                        Objects.equals(rankingOrder, other.rankingOrder);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            contributor, 
                            forenameInitials, 
                            affiliation, 
                            contributionType, 
                            role, 
                            contributionInstance, 
                            correspondingContact, 
                            rankingOrder);
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
                    private Reference contributor;
                    private String forenameInitials;
                    private List<Reference> affiliation = new ArrayList<>();
                    private List<CodeableConcept> contributionType = new ArrayList<>();
                    private CodeableConcept role;
                    private List<ContributionInstance> contributionInstance = new ArrayList<>();
                    private Boolean correspondingContact;
                    private PositiveInt rankingOrder;

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
                     * The identity of the individual contributor.
                     * 
                     * <p>This element is required.
                     * 
                     * <p>Allowed resource types for this reference:
                     * <ul>
                     * <li>{@link Practitioner}</li>
                     * <li>{@link Organization}</li>
                     * </ul>
                     * 
                     * @param contributor
                     *     The identity of the individual contributor
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder contributor(Reference contributor) {
                        this.contributor = contributor;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code forenameInitials}.
                     * 
                     * @param forenameInitials
                     *     For citation styles that use initials
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #forenameInitials(org.linuxforhealth.fhir.model.type.String)
                     */
                    public Builder forenameInitials(java.lang.String forenameInitials) {
                        this.forenameInitials = (forenameInitials == null) ? null : String.of(forenameInitials);
                        return this;
                    }

                    /**
                     * For citation styles that use initials.
                     * 
                     * @param forenameInitials
                     *     For citation styles that use initials
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder forenameInitials(String forenameInitials) {
                        this.forenameInitials = forenameInitials;
                        return this;
                    }

                    /**
                     * Organization affiliated with the contributor.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * <p>Allowed resource types for the references:
                     * <ul>
                     * <li>{@link Organization}</li>
                     * <li>{@link PractitionerRole}</li>
                     * </ul>
                     * 
                     * @param affiliation
                     *     Organizational affiliation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder affiliation(Reference... affiliation) {
                        for (Reference value : affiliation) {
                            this.affiliation.add(value);
                        }
                        return this;
                    }

                    /**
                     * Organization affiliated with the contributor.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * <p>Allowed resource types for the references:
                     * <ul>
                     * <li>{@link Organization}</li>
                     * <li>{@link PractitionerRole}</li>
                     * </ul>
                     * 
                     * @param affiliation
                     *     Organizational affiliation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder affiliation(Collection<Reference> affiliation) {
                        this.affiliation = new ArrayList<>(affiliation);
                        return this;
                    }

                    /**
                     * This element identifies the specific nature of an individual’s contribution with respect to the cited work.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param contributionType
                     *     The specific contribution
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder contributionType(CodeableConcept... contributionType) {
                        for (CodeableConcept value : contributionType) {
                            this.contributionType.add(value);
                        }
                        return this;
                    }

                    /**
                     * This element identifies the specific nature of an individual’s contribution with respect to the cited work.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param contributionType
                     *     The specific contribution
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder contributionType(Collection<CodeableConcept> contributionType) {
                        this.contributionType = new ArrayList<>(contributionType);
                        return this;
                    }

                    /**
                     * The role of the contributor (e.g. author, editor, reviewer, funder).
                     * 
                     * @param role
                     *     The role of the contributor (e.g. author, editor, reviewer, funder)
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder role(CodeableConcept role) {
                        this.role = role;
                        return this;
                    }

                    /**
                     * Contributions with accounting for time or number.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param contributionInstance
                     *     Contributions with accounting for time or number
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder contributionInstance(ContributionInstance... contributionInstance) {
                        for (ContributionInstance value : contributionInstance) {
                            this.contributionInstance.add(value);
                        }
                        return this;
                    }

                    /**
                     * Contributions with accounting for time or number.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param contributionInstance
                     *     Contributions with accounting for time or number
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder contributionInstance(Collection<ContributionInstance> contributionInstance) {
                        this.contributionInstance = new ArrayList<>(contributionInstance);
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code correspondingContact}.
                     * 
                     * @param correspondingContact
                     *     Whether the contributor is the corresponding contributor for the role
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #correspondingContact(org.linuxforhealth.fhir.model.type.Boolean)
                     */
                    public Builder correspondingContact(java.lang.Boolean correspondingContact) {
                        this.correspondingContact = (correspondingContact == null) ? null : Boolean.of(correspondingContact);
                        return this;
                    }

                    /**
                     * Whether the contributor is the corresponding contributor for the role.
                     * 
                     * @param correspondingContact
                     *     Whether the contributor is the corresponding contributor for the role
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder correspondingContact(Boolean correspondingContact) {
                        this.correspondingContact = correspondingContact;
                        return this;
                    }

                    /**
                     * Provides a numerical ranking to represent the degree of contributorship relative to other contributors, such as 1 for 
                     * first author and 2 for second author.
                     * 
                     * @param rankingOrder
                     *     Ranked order of contribution
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder rankingOrder(PositiveInt rankingOrder) {
                        this.rankingOrder = rankingOrder;
                        return this;
                    }

                    /**
                     * Build the {@link Entry}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>contributor</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Entry}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Entry per the base specification
                     */
                    @Override
                    public Entry build() {
                        Entry entry = new Entry(this);
                        if (validating) {
                            validate(entry);
                        }
                        return entry;
                    }

                    protected void validate(Entry entry) {
                        super.validate(entry);
                        ValidationSupport.requireNonNull(entry.contributor, "contributor");
                        ValidationSupport.checkList(entry.affiliation, "affiliation", Reference.class);
                        ValidationSupport.checkList(entry.contributionType, "contributionType", CodeableConcept.class);
                        ValidationSupport.checkList(entry.contributionInstance, "contributionInstance", ContributionInstance.class);
                        ValidationSupport.checkReferenceType(entry.contributor, "contributor", "Practitioner", "Organization");
                        ValidationSupport.checkReferenceType(entry.affiliation, "affiliation", "Organization", "PractitionerRole");
                        ValidationSupport.requireValueOrChildren(entry);
                    }

                    protected Builder from(Entry entry) {
                        super.from(entry);
                        contributor = entry.contributor;
                        forenameInitials = entry.forenameInitials;
                        affiliation.addAll(entry.affiliation);
                        contributionType.addAll(entry.contributionType);
                        role = entry.role;
                        contributionInstance.addAll(entry.contributionInstance);
                        correspondingContact = entry.correspondingContact;
                        rankingOrder = entry.rankingOrder;
                        return this;
                    }
                }

                /**
                 * Contributions with accounting for time or number.
                 */
                public static class ContributionInstance extends BackboneElement {
                    @Binding(
                        bindingName = "ArtifactContributionInstanceType",
                        strength = BindingStrength.Value.EXTENSIBLE,
                        valueSet = "http://hl7.org/fhir/ValueSet/artifact-contribution-instance-type"
                    )
                    @Required
                    private final CodeableConcept type;
                    private final DateTime time;

                    private ContributionInstance(Builder builder) {
                        super(builder);
                        type = builder.type;
                        time = builder.time;
                    }

                    /**
                     * The specific contribution.
                     * 
                     * @return
                     *     An immutable object of type {@link CodeableConcept} that is non-null.
                     */
                    public CodeableConcept getType() {
                        return type;
                    }

                    /**
                     * The time that the contribution was made.
                     * 
                     * @return
                     *     An immutable object of type {@link DateTime} that may be null.
                     */
                    public DateTime getTime() {
                        return time;
                    }

                    @Override
                    public boolean hasChildren() {
                        return super.hasChildren() || 
                            (type != null) || 
                            (time != null);
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
                                accept(time, "time", visitor);
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
                        ContributionInstance other = (ContributionInstance) obj;
                        return Objects.equals(id, other.id) && 
                            Objects.equals(extension, other.extension) && 
                            Objects.equals(modifierExtension, other.modifierExtension) && 
                            Objects.equals(type, other.type) && 
                            Objects.equals(time, other.time);
                    }

                    @Override
                    public int hashCode() {
                        int result = hashCode;
                        if (result == 0) {
                            result = Objects.hash(id, 
                                extension, 
                                modifierExtension, 
                                type, 
                                time);
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
                        private DateTime time;

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
                         * The specific contribution.
                         * 
                         * <p>This element is required.
                         * 
                         * @param type
                         *     The specific contribution
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder type(CodeableConcept type) {
                            this.type = type;
                            return this;
                        }

                        /**
                         * The time that the contribution was made.
                         * 
                         * @param time
                         *     The time that the contribution was made
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder time(DateTime time) {
                            this.time = time;
                            return this;
                        }

                        /**
                         * Build the {@link ContributionInstance}
                         * 
                         * <p>Required elements:
                         * <ul>
                         * <li>type</li>
                         * </ul>
                         * 
                         * @return
                         *     An immutable object of type {@link ContributionInstance}
                         * @throws IllegalStateException
                         *     if the current state cannot be built into a valid ContributionInstance per the base specification
                         */
                        @Override
                        public ContributionInstance build() {
                            ContributionInstance contributionInstance = new ContributionInstance(this);
                            if (validating) {
                                validate(contributionInstance);
                            }
                            return contributionInstance;
                        }

                        protected void validate(ContributionInstance contributionInstance) {
                            super.validate(contributionInstance);
                            ValidationSupport.requireNonNull(contributionInstance.type, "type");
                            ValidationSupport.requireValueOrChildren(contributionInstance);
                        }

                        protected Builder from(ContributionInstance contributionInstance) {
                            super.from(contributionInstance);
                            type = contributionInstance.type;
                            time = contributionInstance.time;
                            return this;
                        }
                    }
                }
            }

            /**
             * Used to record a display of the author/contributor list without separate data element for each list member.
             */
            public static class Summary extends BackboneElement {
                @Binding(
                    bindingName = "ContributorSummaryType",
                    strength = BindingStrength.Value.EXTENSIBLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/contributor-summary-type"
                )
                private final CodeableConcept type;
                @Binding(
                    bindingName = "ContributorSummaryStyle",
                    strength = BindingStrength.Value.EXTENSIBLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/contributor-summary-style"
                )
                private final CodeableConcept style;
                @Binding(
                    bindingName = "ContributorSummarySource",
                    strength = BindingStrength.Value.EXTENSIBLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/contributor-summary-source"
                )
                private final CodeableConcept source;
                @Required
                private final Markdown value;

                private Summary(Builder builder) {
                    super(builder);
                    type = builder.type;
                    style = builder.style;
                    source = builder.source;
                    value = builder.value;
                }

                /**
                 * Used most commonly to express an author list or a contributorship statement.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getType() {
                    return type;
                }

                /**
                 * The format for the display string, such as author last name with first letter capitalized followed by forename 
                 * initials.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getStyle() {
                    return style;
                }

                /**
                 * Used to code the producer or rule for creating the display string.
                 * 
                 * @return
                 *     An immutable object of type {@link CodeableConcept} that may be null.
                 */
                public CodeableConcept getSource() {
                    return source;
                }

                /**
                 * The display string for the author list, contributor list, or contributorship statement.
                 * 
                 * @return
                 *     An immutable object of type {@link Markdown} that is non-null.
                 */
                public Markdown getValue() {
                    return value;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (type != null) || 
                        (style != null) || 
                        (source != null) || 
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
                            accept(style, "style", visitor);
                            accept(source, "source", visitor);
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
                    Summary other = (Summary) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(type, other.type) && 
                        Objects.equals(style, other.style) && 
                        Objects.equals(source, other.source) && 
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
                            style, 
                            source, 
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
                    private CodeableConcept style;
                    private CodeableConcept source;
                    private Markdown value;

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
                     * Used most commonly to express an author list or a contributorship statement.
                     * 
                     * @param type
                     *     Such as author list, contributorship statement, funding statement, acknowledgements statement, or conflicts of 
                     *     interest statement
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder type(CodeableConcept type) {
                        this.type = type;
                        return this;
                    }

                    /**
                     * The format for the display string, such as author last name with first letter capitalized followed by forename 
                     * initials.
                     * 
                     * @param style
                     *     The format for the display string
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder style(CodeableConcept style) {
                        this.style = style;
                        return this;
                    }

                    /**
                     * Used to code the producer or rule for creating the display string.
                     * 
                     * @param source
                     *     Used to code the producer or rule for creating the display string
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder source(CodeableConcept source) {
                        this.source = source;
                        return this;
                    }

                    /**
                     * The display string for the author list, contributor list, or contributorship statement.
                     * 
                     * <p>This element is required.
                     * 
                     * @param value
                     *     The display string for the author list, contributor list, or contributorship statement
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder value(Markdown value) {
                        this.value = value;
                        return this;
                    }

                    /**
                     * Build the {@link Summary}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>value</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Summary}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Summary per the base specification
                     */
                    @Override
                    public Summary build() {
                        Summary summary = new Summary(this);
                        if (validating) {
                            validate(summary);
                        }
                        return summary;
                    }

                    protected void validate(Summary summary) {
                        super.validate(summary);
                        ValidationSupport.requireNonNull(summary.value, "value");
                        ValidationSupport.requireValueOrChildren(summary);
                    }

                    protected Builder from(Summary summary) {
                        super.from(summary);
                        type = summary.type;
                        style = summary.style;
                        source = summary.source;
                        value = summary.value;
                        return this;
                    }
                }
            }
        }
    }
}
