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
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.CharacteristicCombination;
import org.linuxforhealth.fhir.model.r5.type.code.EvidenceVariableHandling;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The EvidenceVariable resource describes an element that knowledge (Evidence) is about.
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
    source = "http://hl7.org/fhir/StructureDefinition/EvidenceVariable"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "EvidenceVariable.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/EvidenceVariable"
)
@Constraint(
    id = "evv-1",
    level = "Rule",
    location = "EvidenceVariable.characteristic",
    description = "In a characteristic, at most one of these six elements shall be used: definitionReference or definitionCanonical or definitionCodeableConcept or definitionId or definitionByTypeAndValue or definitionByCombination",
    expression = "(definitionReference.count() + definitionCanonical.count() + definitionCodeableConcept.count() + definitionId.count() + definitionByTypeAndValue.count() + definitionByCombination.count())  < 2",
    source = "http://hl7.org/fhir/StructureDefinition/EvidenceVariable"
)
@Constraint(
    id = "evidenceVariable-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/EvidenceVariable",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class EvidenceVariable extends DomainResource {
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
    @Summary
    private final String shortTitle;
    @Summary
    @Binding(
        bindingName = "PublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The lifecycle status of an artifact.",
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    @Required
    private final PublicationStatus status;
    private final Boolean experimental;
    @Summary
    private final DateTime date;
    @Summary
    private final String publisher;
    @Summary
    private final List<ContactDetail> contact;
    @Summary
    private final Markdown description;
    private final List<Annotation> note;
    @Summary
    private final List<UsageContext> useContext;
    private final Markdown purpose;
    private final Markdown copyright;
    private final String copyrightLabel;
    private final Date approvalDate;
    private final Date lastReviewDate;
    private final Period effectivePeriod;
    private final List<ContactDetail> author;
    private final List<ContactDetail> editor;
    private final List<ContactDetail> reviewer;
    private final List<ContactDetail> endorser;
    private final List<RelatedArtifact> relatedArtifact;
    private final Boolean actual;
    @Summary
    private final List<Characteristic> characteristic;
    @Binding(
        bindingName = "EvidenceVariableHandling",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/variable-handling|5.0.0"
    )
    private final EvidenceVariableHandling handling;
    private final List<Category> category;

    private EvidenceVariable(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        versionAlgorithm = builder.versionAlgorithm;
        name = builder.name;
        title = builder.title;
        shortTitle = builder.shortTitle;
        status = builder.status;
        experimental = builder.experimental;
        date = builder.date;
        publisher = builder.publisher;
        contact = Collections.unmodifiableList(builder.contact);
        description = builder.description;
        note = Collections.unmodifiableList(builder.note);
        useContext = Collections.unmodifiableList(builder.useContext);
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
        relatedArtifact = Collections.unmodifiableList(builder.relatedArtifact);
        actual = builder.actual;
        characteristic = Collections.unmodifiableList(builder.characteristic);
        handling = builder.handling;
        category = Collections.unmodifiableList(builder.category);
    }

    /**
     * An absolute URI that is used to identify this evidence variable when it is referenced in a specification, model, 
     * design or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal 
     * address at which an authoritative instance of this evidence variable is (or will be) published. This URL can be the 
     * target of a canonical reference. It SHALL remain the same when the evidence variable is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this evidence variable when it is represented in other formats, or 
     * referenced in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the evidence variable when it is referenced in a 
     * specification, model, design or instance. This is an arbitrary value managed by the evidence variable author and is 
     * not expected to be globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not 
     * available. There is also no expectation that versions can be placed in a lexicographical sequence. To provide a 
     * version consistent with the Decision Support Service specification, use the format Major.Minor.Revision (e.g. 1.0.0). 
     * For more information on versioning knowledge assets, refer to the Decision Support Service specification. Note that a 
     * version is required for non-experimental active artifacts.
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
     * A natural language name identifying the evidence variable. This name should be usable as an identifier for the module 
     * by machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the evidence variable.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The short title provides an alternate title for use in informal descriptive contexts where the full, formal title is 
     * not necessary.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * The status of this evidence variable. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this resource is authored for testing purposes (or education/evaluation/marketing) 
     * and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The date (and optionally time) when the evidence variable was last significantly changed. The date must change when 
     * the business version changes and it must change if the status code changes. In addition, it should change when the 
     * substantive content of the evidence variable changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual responsible for the release and ongoing maintenance of the evidence 
     * variable.
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
     * A free text natural language description of the evidence variable from a consumer's perspective.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * A human-readable string to clarify or explain concepts about the resource.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
     * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
     * may be used to assist with indexing and searching for appropriate evidence variable instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * Explanation of why this EvidenceVariable is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * A copyright statement relating to the resource and/or its contents. Copyright statements are generally legal 
     * restrictions on the use and publishing of the resource.
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

See guidance around (not) making local changes to elements [here](canonicalresource.
     * html#localization).
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
     * The period during which the resource content was or is planned to be in active use.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectivePeriod() {
        return effectivePeriod;
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
     * True if the actual variable measured, false if a conceptual representation of the intended variable.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getActual() {
        return actual;
    }

    /**
     * A defining factor of the EvidenceVariable. Multiple characteristics are applied with "and" semantics.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Characteristic} that may be empty.
     */
    public List<Characteristic> getCharacteristic() {
        return characteristic;
    }

    /**
     * The method of handling in statistical analysis.
     * 
     * @return
     *     An immutable object of type {@link EvidenceVariableHandling} that may be null.
     */
    public EvidenceVariableHandling getHandling() {
        return handling;
    }

    /**
     * A grouping for ordinal or polychotomous variables.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Category} that may be empty.
     */
    public List<Category> getCategory() {
        return category;
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
            (shortTitle != null) || 
            (status != null) || 
            (experimental != null) || 
            (date != null) || 
            (publisher != null) || 
            !contact.isEmpty() || 
            (description != null) || 
            !note.isEmpty() || 
            !useContext.isEmpty() || 
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
            !relatedArtifact.isEmpty() || 
            (actual != null) || 
            !characteristic.isEmpty() || 
            (handling != null) || 
            !category.isEmpty();
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
                accept(shortTitle, "shortTitle", visitor);
                accept(status, "status", visitor);
                accept(experimental, "experimental", visitor);
                accept(date, "date", visitor);
                accept(publisher, "publisher", visitor);
                accept(contact, "contact", visitor, ContactDetail.class);
                accept(description, "description", visitor);
                accept(note, "note", visitor, Annotation.class);
                accept(useContext, "useContext", visitor, UsageContext.class);
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
                accept(relatedArtifact, "relatedArtifact", visitor, RelatedArtifact.class);
                accept(actual, "actual", visitor);
                accept(characteristic, "characteristic", visitor, Characteristic.class);
                accept(handling, "handling", visitor);
                accept(category, "category", visitor, Category.class);
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
        EvidenceVariable other = (EvidenceVariable) obj;
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
            Objects.equals(shortTitle, other.shortTitle) && 
            Objects.equals(status, other.status) && 
            Objects.equals(experimental, other.experimental) && 
            Objects.equals(date, other.date) && 
            Objects.equals(publisher, other.publisher) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(description, other.description) && 
            Objects.equals(note, other.note) && 
            Objects.equals(useContext, other.useContext) && 
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
            Objects.equals(relatedArtifact, other.relatedArtifact) && 
            Objects.equals(actual, other.actual) && 
            Objects.equals(characteristic, other.characteristic) && 
            Objects.equals(handling, other.handling) && 
            Objects.equals(category, other.category);
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
                shortTitle, 
                status, 
                experimental, 
                date, 
                publisher, 
                contact, 
                description, 
                note, 
                useContext, 
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
                relatedArtifact, 
                actual, 
                characteristic, 
                handling, 
                category);
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
        private String shortTitle;
        private PublicationStatus status;
        private Boolean experimental;
        private DateTime date;
        private String publisher;
        private List<ContactDetail> contact = new ArrayList<>();
        private Markdown description;
        private List<Annotation> note = new ArrayList<>();
        private List<UsageContext> useContext = new ArrayList<>();
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
        private List<RelatedArtifact> relatedArtifact = new ArrayList<>();
        private Boolean actual;
        private List<Characteristic> characteristic = new ArrayList<>();
        private EvidenceVariableHandling handling;
        private List<Category> category = new ArrayList<>();

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
         * An absolute URI that is used to identify this evidence variable when it is referenced in a specification, model, 
         * design or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal 
         * address at which an authoritative instance of this evidence variable is (or will be) published. This URL can be the 
         * target of a canonical reference. It SHALL remain the same when the evidence variable is stored on different servers.
         * 
         * @param url
         *     Canonical identifier for this evidence variable, represented as a URI (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A formal identifier that is used to identify this evidence variable when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the evidence variable
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
         * A formal identifier that is used to identify this evidence variable when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the evidence variable
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
         *     Business version of the evidence variable
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
         * The identifier that is used to identify this version of the evidence variable when it is referenced in a 
         * specification, model, design or instance. This is an arbitrary value managed by the evidence variable author and is 
         * not expected to be globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not 
         * available. There is also no expectation that versions can be placed in a lexicographical sequence. To provide a 
         * version consistent with the Decision Support Service specification, use the format Major.Minor.Revision (e.g. 1.0.0). 
         * For more information on versioning knowledge assets, refer to the Decision Support Service specification. Note that a 
         * version is required for non-experimental active artifacts.
         * 
         * @param version
         *     Business version of the evidence variable
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
         *     Name for this evidence variable (computer friendly)
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
         * A natural language name identifying the evidence variable. This name should be usable as an identifier for the module 
         * by machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this evidence variable (computer friendly)
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
         *     Name for this evidence variable (human friendly)
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
         * A short, descriptive, user-friendly title for the evidence variable.
         * 
         * @param title
         *     Name for this evidence variable (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Convenience method for setting {@code shortTitle}.
         * 
         * @param shortTitle
         *     Title for use in informal contexts
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #shortTitle(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder shortTitle(java.lang.String shortTitle) {
            this.shortTitle = (shortTitle == null) ? null : String.of(shortTitle);
            return this;
        }

        /**
         * The short title provides an alternate title for use in informal descriptive contexts where the full, formal title is 
         * not necessary.
         * 
         * @param shortTitle
         *     Title for use in informal contexts
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder shortTitle(String shortTitle) {
            this.shortTitle = shortTitle;
            return this;
        }

        /**
         * The status of this evidence variable. Enables tracking the life-cycle of the content.
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
         * A Boolean value to indicate that this resource is authored for testing purposes (or education/evaluation/marketing) 
         * and is not intended to be used for genuine usage.
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
         * The date (and optionally time) when the evidence variable was last significantly changed. The date must change when 
         * the business version changes and it must change if the status code changes. In addition, it should change when the 
         * substantive content of the evidence variable changes.
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
         * The name of the organization or individual responsible for the release and ongoing maintenance of the evidence 
         * variable.
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
         * A free text natural language description of the evidence variable from a consumer's perspective.
         * 
         * @param description
         *     Natural language description of the evidence variable
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(Markdown description) {
            this.description = description;
            return this;
        }

        /**
         * A human-readable string to clarify or explain concepts about the resource.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Used for footnotes or explanatory notes
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
         * A human-readable string to clarify or explain concepts about the resource.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Used for footnotes or explanatory notes
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
         * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
         * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
         * may be used to assist with indexing and searching for appropriate evidence variable instances.
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
         * may be used to assist with indexing and searching for appropriate evidence variable instances.
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
         * Explanation of why this EvidenceVariable is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this EvidenceVariable is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * A copyright statement relating to the resource and/or its contents. Copyright statements are generally legal 
         * restrictions on the use and publishing of the resource.
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
         *     When the resource was approved by publisher
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

See guidance around (not) making local changes to elements [here](canonicalresource.
         * html#localization).
         * 
         * @param approvalDate
         *     When the resource was approved by publisher
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
         *     When the resource was last reviewed by the publisher
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
         *     When the resource was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
        }

        /**
         * The period during which the resource content was or is planned to be in active use.
         * 
         * @param effectivePeriod
         *     When the resource is expected to be used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effectivePeriod(Period effectivePeriod) {
            this.effectivePeriod = effectivePeriod;
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
         * Convenience method for setting {@code actual}.
         * 
         * @param actual
         *     Actual or conceptual
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
         * True if the actual variable measured, false if a conceptual representation of the intended variable.
         * 
         * @param actual
         *     Actual or conceptual
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actual(Boolean actual) {
            this.actual = actual;
            return this;
        }

        /**
         * A defining factor of the EvidenceVariable. Multiple characteristics are applied with "and" semantics.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param characteristic
         *     A defining factor of the EvidenceVariable
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder characteristic(Characteristic... characteristic) {
            for (Characteristic value : characteristic) {
                this.characteristic.add(value);
            }
            return this;
        }

        /**
         * A defining factor of the EvidenceVariable. Multiple characteristics are applied with "and" semantics.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param characteristic
         *     A defining factor of the EvidenceVariable
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder characteristic(Collection<Characteristic> characteristic) {
            this.characteristic = new ArrayList<>(characteristic);
            return this;
        }

        /**
         * The method of handling in statistical analysis.
         * 
         * @param handling
         *     continuous | dichotomous | ordinal | polychotomous
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder handling(EvidenceVariableHandling handling) {
            this.handling = handling;
            return this;
        }

        /**
         * A grouping for ordinal or polychotomous variables.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     A grouping for ordinal or polychotomous variables
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder category(Category... category) {
            for (Category value : category) {
                this.category.add(value);
            }
            return this;
        }

        /**
         * A grouping for ordinal or polychotomous variables.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     A grouping for ordinal or polychotomous variables
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder category(Collection<Category> category) {
            this.category = new ArrayList<>(category);
            return this;
        }

        /**
         * Build the {@link EvidenceVariable}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link EvidenceVariable}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid EvidenceVariable per the base specification
         */
        @Override
        public EvidenceVariable build() {
            EvidenceVariable evidenceVariable = new EvidenceVariable(this);
            if (validating) {
                validate(evidenceVariable);
            }
            return evidenceVariable;
        }

        protected void validate(EvidenceVariable evidenceVariable) {
            super.validate(evidenceVariable);
            ValidationSupport.checkList(evidenceVariable.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(evidenceVariable.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(evidenceVariable.status, "status");
            ValidationSupport.checkList(evidenceVariable.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(evidenceVariable.note, "note", Annotation.class);
            ValidationSupport.checkList(evidenceVariable.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(evidenceVariable.author, "author", ContactDetail.class);
            ValidationSupport.checkList(evidenceVariable.editor, "editor", ContactDetail.class);
            ValidationSupport.checkList(evidenceVariable.reviewer, "reviewer", ContactDetail.class);
            ValidationSupport.checkList(evidenceVariable.endorser, "endorser", ContactDetail.class);
            ValidationSupport.checkList(evidenceVariable.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
            ValidationSupport.checkList(evidenceVariable.characteristic, "characteristic", Characteristic.class);
            ValidationSupport.checkList(evidenceVariable.category, "category", Category.class);
        }

        protected Builder from(EvidenceVariable evidenceVariable) {
            super.from(evidenceVariable);
            url = evidenceVariable.url;
            identifier.addAll(evidenceVariable.identifier);
            version = evidenceVariable.version;
            versionAlgorithm = evidenceVariable.versionAlgorithm;
            name = evidenceVariable.name;
            title = evidenceVariable.title;
            shortTitle = evidenceVariable.shortTitle;
            status = evidenceVariable.status;
            experimental = evidenceVariable.experimental;
            date = evidenceVariable.date;
            publisher = evidenceVariable.publisher;
            contact.addAll(evidenceVariable.contact);
            description = evidenceVariable.description;
            note.addAll(evidenceVariable.note);
            useContext.addAll(evidenceVariable.useContext);
            purpose = evidenceVariable.purpose;
            copyright = evidenceVariable.copyright;
            copyrightLabel = evidenceVariable.copyrightLabel;
            approvalDate = evidenceVariable.approvalDate;
            lastReviewDate = evidenceVariable.lastReviewDate;
            effectivePeriod = evidenceVariable.effectivePeriod;
            author.addAll(evidenceVariable.author);
            editor.addAll(evidenceVariable.editor);
            reviewer.addAll(evidenceVariable.reviewer);
            endorser.addAll(evidenceVariable.endorser);
            relatedArtifact.addAll(evidenceVariable.relatedArtifact);
            actual = evidenceVariable.actual;
            characteristic.addAll(evidenceVariable.characteristic);
            handling = evidenceVariable.handling;
            category.addAll(evidenceVariable.category);
            return this;
        }
    }

    /**
     * A defining factor of the EvidenceVariable. Multiple characteristics are applied with "and" semantics.
     */
    public static class Characteristic extends BackboneElement {
        private final Id linkId;
        private final Markdown description;
        private final List<Annotation> note;
        private final Boolean exclude;
        @Summary
        @ReferenceTarget({ "EvidenceVariable", "Group", "Evidence" })
        private final Reference definitionReference;
        @Summary
        private final Canonical definitionCanonical;
        @Summary
        private final CodeableConcept definitionCodeableConcept;
        @Summary
        private final Expression definitionExpression;
        @Summary
        private final Id definitionId;
        @Summary
        private final DefinitionByTypeAndValue definitionByTypeAndValue;
        private final DefinitionByCombination definitionByCombination;
        @Choice({ Quantity.class, Range.class })
        private final Element instances;
        @Choice({ Quantity.class, Range.class })
        private final Element duration;
        private final List<TimeFromEvent> timeFromEvent;

        private Characteristic(Builder builder) {
            super(builder);
            linkId = builder.linkId;
            description = builder.description;
            note = Collections.unmodifiableList(builder.note);
            exclude = builder.exclude;
            definitionReference = builder.definitionReference;
            definitionCanonical = builder.definitionCanonical;
            definitionCodeableConcept = builder.definitionCodeableConcept;
            definitionExpression = builder.definitionExpression;
            definitionId = builder.definitionId;
            definitionByTypeAndValue = builder.definitionByTypeAndValue;
            definitionByCombination = builder.definitionByCombination;
            instances = builder.instances;
            duration = builder.duration;
            timeFromEvent = Collections.unmodifiableList(builder.timeFromEvent);
        }

        /**
         * Label used for when a characteristic refers to another characteristic.
         * 
         * @return
         *     An immutable object of type {@link Id} that may be null.
         */
        public Id getLinkId() {
            return linkId;
        }

        /**
         * A short, natural language description of the characteristic that could be used to communicate the criteria to an end-
         * user.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * A human-readable string to clarify or explain concepts about the characteristic.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
         */
        public List<Annotation> getNote() {
            return note;
        }

        /**
         * When true, this characteristic is an exclusion criterion. In other words, not matching this characteristic definition 
         * is equivalent to meeting this criterion.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getExclude() {
            return exclude;
        }

        /**
         * Defines the characteristic using a Reference.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getDefinitionReference() {
            return definitionReference;
        }

        /**
         * Defines the characteristic using Canonical.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getDefinitionCanonical() {
            return definitionCanonical;
        }

        /**
         * Defines the characteristic using CodeableConcept.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getDefinitionCodeableConcept() {
            return definitionCodeableConcept;
        }

        /**
         * Defines the characteristic using Expression.
         * 
         * @return
         *     An immutable object of type {@link Expression} that may be null.
         */
        public Expression getDefinitionExpression() {
            return definitionExpression;
        }

        /**
         * Defines the characteristic using id.
         * 
         * @return
         *     An immutable object of type {@link Id} that may be null.
         */
        public Id getDefinitionId() {
            return definitionId;
        }

        /**
         * Defines the characteristic using both a type and value[x] elements.
         * 
         * @return
         *     An immutable object of type {@link DefinitionByTypeAndValue} that may be null.
         */
        public DefinitionByTypeAndValue getDefinitionByTypeAndValue() {
            return definitionByTypeAndValue;
        }

        /**
         * Defines the characteristic as a combination of two or more characteristics.
         * 
         * @return
         *     An immutable object of type {@link DefinitionByCombination} that may be null.
         */
        public DefinitionByCombination getDefinitionByCombination() {
            return definitionByCombination;
        }

        /**
         * Number of occurrences meeting the characteristic.
         * 
         * @return
         *     An immutable object of type {@link Quantity} or {@link Range} that may be null.
         */
        public Element getInstances() {
            return instances;
        }

        /**
         * Length of time in which the characteristic is met.
         * 
         * @return
         *     An immutable object of type {@link Quantity} or {@link Range} that may be null.
         */
        public Element getDuration() {
            return duration;
        }

        /**
         * Timing in which the characteristic is determined.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link TimeFromEvent} that may be empty.
         */
        public List<TimeFromEvent> getTimeFromEvent() {
            return timeFromEvent;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (linkId != null) || 
                (description != null) || 
                !note.isEmpty() || 
                (exclude != null) || 
                (definitionReference != null) || 
                (definitionCanonical != null) || 
                (definitionCodeableConcept != null) || 
                (definitionExpression != null) || 
                (definitionId != null) || 
                (definitionByTypeAndValue != null) || 
                (definitionByCombination != null) || 
                (instances != null) || 
                (duration != null) || 
                !timeFromEvent.isEmpty();
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
                    accept(description, "description", visitor);
                    accept(note, "note", visitor, Annotation.class);
                    accept(exclude, "exclude", visitor);
                    accept(definitionReference, "definitionReference", visitor);
                    accept(definitionCanonical, "definitionCanonical", visitor);
                    accept(definitionCodeableConcept, "definitionCodeableConcept", visitor);
                    accept(definitionExpression, "definitionExpression", visitor);
                    accept(definitionId, "definitionId", visitor);
                    accept(definitionByTypeAndValue, "definitionByTypeAndValue", visitor);
                    accept(definitionByCombination, "definitionByCombination", visitor);
                    accept(instances, "instances", visitor);
                    accept(duration, "duration", visitor);
                    accept(timeFromEvent, "timeFromEvent", visitor, TimeFromEvent.class);
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
            Characteristic other = (Characteristic) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(linkId, other.linkId) && 
                Objects.equals(description, other.description) && 
                Objects.equals(note, other.note) && 
                Objects.equals(exclude, other.exclude) && 
                Objects.equals(definitionReference, other.definitionReference) && 
                Objects.equals(definitionCanonical, other.definitionCanonical) && 
                Objects.equals(definitionCodeableConcept, other.definitionCodeableConcept) && 
                Objects.equals(definitionExpression, other.definitionExpression) && 
                Objects.equals(definitionId, other.definitionId) && 
                Objects.equals(definitionByTypeAndValue, other.definitionByTypeAndValue) && 
                Objects.equals(definitionByCombination, other.definitionByCombination) && 
                Objects.equals(instances, other.instances) && 
                Objects.equals(duration, other.duration) && 
                Objects.equals(timeFromEvent, other.timeFromEvent);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    linkId, 
                    description, 
                    note, 
                    exclude, 
                    definitionReference, 
                    definitionCanonical, 
                    definitionCodeableConcept, 
                    definitionExpression, 
                    definitionId, 
                    definitionByTypeAndValue, 
                    definitionByCombination, 
                    instances, 
                    duration, 
                    timeFromEvent);
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
            private Markdown description;
            private List<Annotation> note = new ArrayList<>();
            private Boolean exclude;
            private Reference definitionReference;
            private Canonical definitionCanonical;
            private CodeableConcept definitionCodeableConcept;
            private Expression definitionExpression;
            private Id definitionId;
            private DefinitionByTypeAndValue definitionByTypeAndValue;
            private DefinitionByCombination definitionByCombination;
            private Element instances;
            private Element duration;
            private List<TimeFromEvent> timeFromEvent = new ArrayList<>();

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
             * Label used for when a characteristic refers to another characteristic.
             * 
             * @param linkId
             *     Label for internal linking
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder linkId(Id linkId) {
                this.linkId = linkId;
                return this;
            }

            /**
             * A short, natural language description of the characteristic that could be used to communicate the criteria to an end-
             * user.
             * 
             * @param description
             *     Natural language description of the characteristic
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * A human-readable string to clarify or explain concepts about the characteristic.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Used for footnotes or explanatory notes
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
             * A human-readable string to clarify or explain concepts about the characteristic.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param note
             *     Used for footnotes or explanatory notes
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
             * Convenience method for setting {@code exclude}.
             * 
             * @param exclude
             *     Whether the characteristic is an inclusion criterion or exclusion criterion
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #exclude(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder exclude(java.lang.Boolean exclude) {
                this.exclude = (exclude == null) ? null : Boolean.of(exclude);
                return this;
            }

            /**
             * When true, this characteristic is an exclusion criterion. In other words, not matching this characteristic definition 
             * is equivalent to meeting this criterion.
             * 
             * @param exclude
             *     Whether the characteristic is an inclusion criterion or exclusion criterion
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder exclude(Boolean exclude) {
                this.exclude = exclude;
                return this;
            }

            /**
             * Defines the characteristic using a Reference.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link EvidenceVariable}</li>
             * <li>{@link Group}</li>
             * <li>{@link Evidence}</li>
             * </ul>
             * 
             * @param definitionReference
             *     Defines the characteristic (without using type and value) by a Reference
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definitionReference(Reference definitionReference) {
                this.definitionReference = definitionReference;
                return this;
            }

            /**
             * Defines the characteristic using Canonical.
             * 
             * @param definitionCanonical
             *     Defines the characteristic (without using type and value) by a Canonical
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definitionCanonical(Canonical definitionCanonical) {
                this.definitionCanonical = definitionCanonical;
                return this;
            }

            /**
             * Defines the characteristic using CodeableConcept.
             * 
             * @param definitionCodeableConcept
             *     Defines the characteristic (without using type and value) by a CodeableConcept
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definitionCodeableConcept(CodeableConcept definitionCodeableConcept) {
                this.definitionCodeableConcept = definitionCodeableConcept;
                return this;
            }

            /**
             * Defines the characteristic using Expression.
             * 
             * @param definitionExpression
             *     Defines the characteristic (without using type and value) by an expression
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definitionExpression(Expression definitionExpression) {
                this.definitionExpression = definitionExpression;
                return this;
            }

            /**
             * Defines the characteristic using id.
             * 
             * @param definitionId
             *     Defines the characteristic (without using type and value) by an id
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definitionId(Id definitionId) {
                this.definitionId = definitionId;
                return this;
            }

            /**
             * Defines the characteristic using both a type and value[x] elements.
             * 
             * @param definitionByTypeAndValue
             *     Defines the characteristic using type and value
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definitionByTypeAndValue(DefinitionByTypeAndValue definitionByTypeAndValue) {
                this.definitionByTypeAndValue = definitionByTypeAndValue;
                return this;
            }

            /**
             * Defines the characteristic as a combination of two or more characteristics.
             * 
             * @param definitionByCombination
             *     Used to specify how two or more characteristics are combined
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definitionByCombination(DefinitionByCombination definitionByCombination) {
                this.definitionByCombination = definitionByCombination;
                return this;
            }

            /**
             * Number of occurrences meeting the characteristic.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * </ul>
             * 
             * @param instances
             *     Number of occurrences meeting the characteristic
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instances(Element instances) {
                this.instances = instances;
                return this;
            }

            /**
             * Length of time in which the characteristic is met.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * </ul>
             * 
             * @param duration
             *     Length of time in which the characteristic is met
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder duration(Element duration) {
                this.duration = duration;
                return this;
            }

            /**
             * Timing in which the characteristic is determined.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param timeFromEvent
             *     Timing in which the characteristic is determined
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder timeFromEvent(TimeFromEvent... timeFromEvent) {
                for (TimeFromEvent value : timeFromEvent) {
                    this.timeFromEvent.add(value);
                }
                return this;
            }

            /**
             * Timing in which the characteristic is determined.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param timeFromEvent
             *     Timing in which the characteristic is determined
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder timeFromEvent(Collection<TimeFromEvent> timeFromEvent) {
                this.timeFromEvent = new ArrayList<>(timeFromEvent);
                return this;
            }

            /**
             * Build the {@link Characteristic}
             * 
             * @return
             *     An immutable object of type {@link Characteristic}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Characteristic per the base specification
             */
            @Override
            public Characteristic build() {
                Characteristic characteristic = new Characteristic(this);
                if (validating) {
                    validate(characteristic);
                }
                return characteristic;
            }

            protected void validate(Characteristic characteristic) {
                super.validate(characteristic);
                ValidationSupport.checkList(characteristic.note, "note", Annotation.class);
                ValidationSupport.choiceElement(characteristic.instances, "instances", Quantity.class, Range.class);
                ValidationSupport.choiceElement(characteristic.duration, "duration", Quantity.class, Range.class);
                ValidationSupport.checkList(characteristic.timeFromEvent, "timeFromEvent", TimeFromEvent.class);
                ValidationSupport.checkReferenceType(characteristic.definitionReference, "definitionReference", "EvidenceVariable", "Group", "Evidence");
                ValidationSupport.requireValueOrChildren(characteristic);
            }

            protected Builder from(Characteristic characteristic) {
                super.from(characteristic);
                linkId = characteristic.linkId;
                description = characteristic.description;
                note.addAll(characteristic.note);
                exclude = characteristic.exclude;
                definitionReference = characteristic.definitionReference;
                definitionCanonical = characteristic.definitionCanonical;
                definitionCodeableConcept = characteristic.definitionCodeableConcept;
                definitionExpression = characteristic.definitionExpression;
                definitionId = characteristic.definitionId;
                definitionByTypeAndValue = characteristic.definitionByTypeAndValue;
                definitionByCombination = characteristic.definitionByCombination;
                instances = characteristic.instances;
                duration = characteristic.duration;
                timeFromEvent.addAll(characteristic.timeFromEvent);
                return this;
            }
        }

        /**
         * Defines the characteristic using both a type and value[x] elements.
         */
        public static class DefinitionByTypeAndValue extends BackboneElement {
            @Summary
            @Binding(
                bindingName = "UsageContextType",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://terminology.hl7.org/ValueSet/usage-context-type"
            )
            @Required
            private final CodeableConcept type;
            @Binding(
                bindingName = "DefinitionMethod",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/definition-method"
            )
            private final List<CodeableConcept> method;
            @ReferenceTarget({ "Device", "DeviceMetric" })
            private final Reference device;
            @Summary
            @Choice({ CodeableConcept.class, Boolean.class, Quantity.class, Range.class, Reference.class, Id.class })
            @Required
            private final Element value;
            @Binding(
                bindingName = "CharacteristicOffset",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/characteristic-offset"
            )
            private final CodeableConcept offset;

            private DefinitionByTypeAndValue(Builder builder) {
                super(builder);
                type = builder.type;
                method = Collections.unmodifiableList(builder.method);
                device = builder.device;
                value = builder.value;
                offset = builder.offset;
            }

            /**
             * Used to express the type of characteristic.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that is non-null.
             */
            public CodeableConcept getType() {
                return type;
            }

            /**
             * Method for how the characteristic value was determined.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getMethod() {
                return method;
            }

            /**
             * Device used for determining characteristic.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getDevice() {
                return device;
            }

            /**
             * Defines the characteristic when paired with characteristic.type.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept}, {@link Boolean}, {@link Quantity}, {@link Range}, {@link 
             *     Reference} or {@link Id} that is non-null.
             */
            public Element getValue() {
                return value;
            }

            /**
             * Defines the reference point for comparison when valueQuantity or valueRange is not compared to zero.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getOffset() {
                return offset;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (type != null) || 
                    !method.isEmpty() || 
                    (device != null) || 
                    (value != null) || 
                    (offset != null);
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
                        accept(method, "method", visitor, CodeableConcept.class);
                        accept(device, "device", visitor);
                        accept(value, "value", visitor);
                        accept(offset, "offset", visitor);
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
                DefinitionByTypeAndValue other = (DefinitionByTypeAndValue) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(type, other.type) && 
                    Objects.equals(method, other.method) && 
                    Objects.equals(device, other.device) && 
                    Objects.equals(value, other.value) && 
                    Objects.equals(offset, other.offset);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        type, 
                        method, 
                        device, 
                        value, 
                        offset);
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
                private List<CodeableConcept> method = new ArrayList<>();
                private Reference device;
                private Element value;
                private CodeableConcept offset;

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
                 * Used to express the type of characteristic.
                 * 
                 * <p>This element is required.
                 * 
                 * @param type
                 *     Expresses the type of characteristic
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder type(CodeableConcept type) {
                    this.type = type;
                    return this;
                }

                /**
                 * Method for how the characteristic value was determined.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param method
                 *     Method for how the characteristic value was determined
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder method(CodeableConcept... method) {
                    for (CodeableConcept value : method) {
                        this.method.add(value);
                    }
                    return this;
                }

                /**
                 * Method for how the characteristic value was determined.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param method
                 *     Method for how the characteristic value was determined
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder method(Collection<CodeableConcept> method) {
                    this.method = new ArrayList<>(method);
                    return this;
                }

                /**
                 * Device used for determining characteristic.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Device}</li>
                 * <li>{@link DeviceMetric}</li>
                 * </ul>
                 * 
                 * @param device
                 *     Device used for determining characteristic
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder device(Reference device) {
                    this.device = device;
                    return this;
                }

                /**
                 * Convenience method for setting {@code value} with choice type Boolean.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     Defines the characteristic when coupled with characteristic.type
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
                 * Defines the characteristic when paired with characteristic.type.
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
                 * <li>{@link Id}</li>
                 * </ul>
                 * 
                 * @param value
                 *     Defines the characteristic when coupled with characteristic.type
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(Element value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Defines the reference point for comparison when valueQuantity or valueRange is not compared to zero.
                 * 
                 * @param offset
                 *     Reference point for valueQuantity or valueRange
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder offset(CodeableConcept offset) {
                    this.offset = offset;
                    return this;
                }

                /**
                 * Build the {@link DefinitionByTypeAndValue}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>type</li>
                 * <li>value</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link DefinitionByTypeAndValue}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid DefinitionByTypeAndValue per the base specification
                 */
                @Override
                public DefinitionByTypeAndValue build() {
                    DefinitionByTypeAndValue definitionByTypeAndValue = new DefinitionByTypeAndValue(this);
                    if (validating) {
                        validate(definitionByTypeAndValue);
                    }
                    return definitionByTypeAndValue;
                }

                protected void validate(DefinitionByTypeAndValue definitionByTypeAndValue) {
                    super.validate(definitionByTypeAndValue);
                    ValidationSupport.requireNonNull(definitionByTypeAndValue.type, "type");
                    ValidationSupport.checkList(definitionByTypeAndValue.method, "method", CodeableConcept.class);
                    ValidationSupport.requireChoiceElement(definitionByTypeAndValue.value, "value", CodeableConcept.class, Boolean.class, Quantity.class, Range.class, Reference.class, Id.class);
                    ValidationSupport.checkReferenceType(definitionByTypeAndValue.device, "device", "Device", "DeviceMetric");
                    ValidationSupport.requireValueOrChildren(definitionByTypeAndValue);
                }

                protected Builder from(DefinitionByTypeAndValue definitionByTypeAndValue) {
                    super.from(definitionByTypeAndValue);
                    type = definitionByTypeAndValue.type;
                    method.addAll(definitionByTypeAndValue.method);
                    device = definitionByTypeAndValue.device;
                    value = definitionByTypeAndValue.value;
                    offset = definitionByTypeAndValue.offset;
                    return this;
                }
            }
        }

        /**
         * Defines the characteristic as a combination of two or more characteristics.
         */
        public static class DefinitionByCombination extends BackboneElement {
            @Binding(
                bindingName = "CharacteristicCombination",
                strength = BindingStrength.Value.REQUIRED,
                valueSet = "http://hl7.org/fhir/ValueSet/characteristic-combination|5.0.0"
            )
            @Required
            private final CharacteristicCombination code;
            private final PositiveInt threshold;
            @Required
            private final List<EvidenceVariable.Characteristic> characteristic;

            private DefinitionByCombination(Builder builder) {
                super(builder);
                code = builder.code;
                threshold = builder.threshold;
                characteristic = Collections.unmodifiableList(builder.characteristic);
            }

            /**
             * Used to specify if two or more characteristics are combined with OR or AND.
             * 
             * @return
             *     An immutable object of type {@link CharacteristicCombination} that is non-null.
             */
            public CharacteristicCombination getCode() {
                return code;
            }

            /**
             * Provides the value of "n" when "at-least" or "at-most" codes are used.
             * 
             * @return
             *     An immutable object of type {@link PositiveInt} that may be null.
             */
            public PositiveInt getThreshold() {
                return threshold;
            }

            /**
             * A defining factor of the characteristic.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Characteristic} that is non-empty.
             */
            public List<EvidenceVariable.Characteristic> getCharacteristic() {
                return characteristic;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (code != null) || 
                    (threshold != null) || 
                    !characteristic.isEmpty();
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
                        accept(threshold, "threshold", visitor);
                        accept(characteristic, "characteristic", visitor, EvidenceVariable.Characteristic.class);
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
                DefinitionByCombination other = (DefinitionByCombination) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(code, other.code) && 
                    Objects.equals(threshold, other.threshold) && 
                    Objects.equals(characteristic, other.characteristic);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        code, 
                        threshold, 
                        characteristic);
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
                private CharacteristicCombination code;
                private PositiveInt threshold;
                private List<EvidenceVariable.Characteristic> characteristic = new ArrayList<>();

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
                 * Used to specify if two or more characteristics are combined with OR or AND.
                 * 
                 * <p>This element is required.
                 * 
                 * @param code
                 *     all-of | any-of | at-least | at-most | statistical | net-effect | dataset
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(CharacteristicCombination code) {
                    this.code = code;
                    return this;
                }

                /**
                 * Provides the value of "n" when "at-least" or "at-most" codes are used.
                 * 
                 * @param threshold
                 *     Provides the value of "n" when "at-least" or "at-most" codes are used
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder threshold(PositiveInt threshold) {
                    this.threshold = threshold;
                    return this;
                }

                /**
                 * A defining factor of the characteristic.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param characteristic
                 *     A defining factor of the characteristic
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder characteristic(EvidenceVariable.Characteristic... characteristic) {
                    for (EvidenceVariable.Characteristic value : characteristic) {
                        this.characteristic.add(value);
                    }
                    return this;
                }

                /**
                 * A defining factor of the characteristic.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>This element is required.
                 * 
                 * @param characteristic
                 *     A defining factor of the characteristic
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder characteristic(Collection<EvidenceVariable.Characteristic> characteristic) {
                    this.characteristic = new ArrayList<>(characteristic);
                    return this;
                }

                /**
                 * Build the {@link DefinitionByCombination}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>code</li>
                 * <li>characteristic</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link DefinitionByCombination}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid DefinitionByCombination per the base specification
                 */
                @Override
                public DefinitionByCombination build() {
                    DefinitionByCombination definitionByCombination = new DefinitionByCombination(this);
                    if (validating) {
                        validate(definitionByCombination);
                    }
                    return definitionByCombination;
                }

                protected void validate(DefinitionByCombination definitionByCombination) {
                    super.validate(definitionByCombination);
                    ValidationSupport.requireNonNull(definitionByCombination.code, "code");
                    ValidationSupport.checkNonEmptyList(definitionByCombination.characteristic, "characteristic", EvidenceVariable.Characteristic.class);
                    ValidationSupport.requireValueOrChildren(definitionByCombination);
                }

                protected Builder from(DefinitionByCombination definitionByCombination) {
                    super.from(definitionByCombination);
                    code = definitionByCombination.code;
                    threshold = definitionByCombination.threshold;
                    characteristic.addAll(definitionByCombination.characteristic);
                    return this;
                }
            }
        }

        /**
         * Timing in which the characteristic is determined.
         */
        public static class TimeFromEvent extends BackboneElement {
            private final Markdown description;
            private final List<Annotation> note;
            @Choice({ CodeableConcept.class, Reference.class, DateTime.class, Id.class })
            @Binding(
                bindingName = "EvidenceVariableEvent",
                strength = BindingStrength.Value.EXAMPLE,
                valueSet = "http://hl7.org/fhir/ValueSet/evidence-variable-event"
            )
            private final Element event;
            private final Quantity quantity;
            private final Range range;

            private TimeFromEvent(Builder builder) {
                super(builder);
                description = builder.description;
                note = Collections.unmodifiableList(builder.note);
                event = builder.event;
                quantity = builder.quantity;
                range = builder.range;
            }

            /**
             * Human readable description.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getDescription() {
                return description;
            }

            /**
             * A human-readable string to clarify or explain concepts about the timeFromEvent.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
             */
            public List<Annotation> getNote() {
                return note;
            }

            /**
             * The event used as a base point (reference point) in time.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept}, {@link Reference}, {@link DateTime} or {@link Id} that may be 
             *     null.
             */
            public Element getEvent() {
                return event;
            }

            /**
             * Used to express the observation at a defined amount of time before or after the event.
             * 
             * @return
             *     An immutable object of type {@link Quantity} that may be null.
             */
            public Quantity getQuantity() {
                return quantity;
            }

            /**
             * Used to express the observation within a period before and/or after the event.
             * 
             * @return
             *     An immutable object of type {@link Range} that may be null.
             */
            public Range getRange() {
                return range;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (description != null) || 
                    !note.isEmpty() || 
                    (event != null) || 
                    (quantity != null) || 
                    (range != null);
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
                        accept(description, "description", visitor);
                        accept(note, "note", visitor, Annotation.class);
                        accept(event, "event", visitor);
                        accept(quantity, "quantity", visitor);
                        accept(range, "range", visitor);
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
                TimeFromEvent other = (TimeFromEvent) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(description, other.description) && 
                    Objects.equals(note, other.note) && 
                    Objects.equals(event, other.event) && 
                    Objects.equals(quantity, other.quantity) && 
                    Objects.equals(range, other.range);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        description, 
                        note, 
                        event, 
                        quantity, 
                        range);
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
                private Markdown description;
                private List<Annotation> note = new ArrayList<>();
                private Element event;
                private Quantity quantity;
                private Range range;

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
                 * Human readable description.
                 * 
                 * @param description
                 *     Human readable description
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder description(Markdown description) {
                    this.description = description;
                    return this;
                }

                /**
                 * A human-readable string to clarify or explain concepts about the timeFromEvent.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param note
                 *     Used for footnotes or explanatory notes
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
                 * A human-readable string to clarify or explain concepts about the timeFromEvent.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param note
                 *     Used for footnotes or explanatory notes
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
                 * The event used as a base point (reference point) in time.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link CodeableConcept}</li>
                 * <li>{@link Reference}</li>
                 * <li>{@link DateTime}</li>
                 * <li>{@link Id}</li>
                 * </ul>
                 * 
                 * @param event
                 *     The event used as a base point (reference point) in time
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder event(Element event) {
                    this.event = event;
                    return this;
                }

                /**
                 * Used to express the observation at a defined amount of time before or after the event.
                 * 
                 * @param quantity
                 *     Used to express the observation at a defined amount of time before or after the event
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder quantity(Quantity quantity) {
                    this.quantity = quantity;
                    return this;
                }

                /**
                 * Used to express the observation within a period before and/or after the event.
                 * 
                 * @param range
                 *     Used to express the observation within a period before and/or after the event
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder range(Range range) {
                    this.range = range;
                    return this;
                }

                /**
                 * Build the {@link TimeFromEvent}
                 * 
                 * @return
                 *     An immutable object of type {@link TimeFromEvent}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid TimeFromEvent per the base specification
                 */
                @Override
                public TimeFromEvent build() {
                    TimeFromEvent timeFromEvent = new TimeFromEvent(this);
                    if (validating) {
                        validate(timeFromEvent);
                    }
                    return timeFromEvent;
                }

                protected void validate(TimeFromEvent timeFromEvent) {
                    super.validate(timeFromEvent);
                    ValidationSupport.checkList(timeFromEvent.note, "note", Annotation.class);
                    ValidationSupport.choiceElement(timeFromEvent.event, "event", CodeableConcept.class, Reference.class, DateTime.class, Id.class);
                    ValidationSupport.requireValueOrChildren(timeFromEvent);
                }

                protected Builder from(TimeFromEvent timeFromEvent) {
                    super.from(timeFromEvent);
                    description = timeFromEvent.description;
                    note.addAll(timeFromEvent.note);
                    event = timeFromEvent.event;
                    quantity = timeFromEvent.quantity;
                    range = timeFromEvent.range;
                    return this;
                }
            }
        }
    }

    /**
     * A grouping for ordinal or polychotomous variables.
     */
    public static class Category extends BackboneElement {
        private final String name;
        @Choice({ CodeableConcept.class, Quantity.class, Range.class })
        private final Element value;

        private Category(Builder builder) {
            super(builder);
            name = builder.name;
            value = builder.value;
        }

        /**
         * Description of the grouping.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getName() {
            return name;
        }

        /**
         * Definition of the grouping.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept}, {@link Quantity} or {@link Range} that may be null.
         */
        public Element getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (name != null) || 
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
                    accept(name, "name", visitor);
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
            Category other = (Category) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(name, other.name) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    name, 
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
            private String name;
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
             * Convenience method for setting {@code name}.
             * 
             * @param name
             *     Description of the grouping
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
             * Description of the grouping.
             * 
             * @param name
             *     Description of the grouping
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Definition of the grouping.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * </ul>
             * 
             * @param value
             *     Definition of the grouping
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Category}
             * 
             * @return
             *     An immutable object of type {@link Category}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Category per the base specification
             */
            @Override
            public Category build() {
                Category category = new Category(this);
                if (validating) {
                    validate(category);
                }
                return category;
            }

            protected void validate(Category category) {
                super.validate(category);
                ValidationSupport.choiceElement(category.value, "value", CodeableConcept.class, Quantity.class, Range.class);
                ValidationSupport.requireValueOrChildren(category);
            }

            protected Builder from(Category category) {
                super.from(category);
                name = category.name;
                value = category.value;
                return this;
            }
        }
    }
}
