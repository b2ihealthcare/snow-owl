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
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.Disposition;
import org.linuxforhealth.fhir.model.r5.type.code.InformationType;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.type.code.WorkflowStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This Resource provides one or more comments, classifiers or ratings about a Resource and supports attribution and 
 * rights management metadata for the added content.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ArtifactAssessment extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final String title;
    @ReferenceTarget({ "Citation" })
    @Choice({ Reference.class, Markdown.class })
    private final Element citeAs;
    @Summary
    private final DateTime date;
    private final Markdown copyright;
    private final Date approvalDate;
    @Summary
    private final Date lastReviewDate;
    @Summary
    @Choice({ Reference.class, Canonical.class, Uri.class })
    @Required
    private final Element artifact;
    private final List<Content> content;
    @Summary
    @Binding(
        bindingName = "WorkflowStatus",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/artifactassessment-workflow-status|5.0.0"
    )
    private final WorkflowStatus workflowStatus;
    @Summary
    @Binding(
        bindingName = "Disposition",
        strength = BindingStrength.Value.REQUIRED,
        valueSet = "http://hl7.org/fhir/ValueSet/artifactassessment-disposition|5.0.0"
    )
    private final Disposition disposition;

    private ArtifactAssessment(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        title = builder.title;
        citeAs = builder.citeAs;
        date = builder.date;
        copyright = builder.copyright;
        approvalDate = builder.approvalDate;
        lastReviewDate = builder.lastReviewDate;
        artifact = builder.artifact;
        content = Collections.unmodifiableList(builder.content);
        workflowStatus = builder.workflowStatus;
        disposition = builder.disposition;
    }

    /**
     * A formal identifier that is used to identify this artifact assessment when it is represented in other formats, or 
     * referenced in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * A short title for the assessment for use in displaying and selecting.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Display of or reference to the bibliographic citation of the comment, classifier, or rating.
     * 
     * @return
     *     An immutable object of type {@link Reference} or {@link Markdown} that may be null.
     */
    public Element getCiteAs() {
        return citeAs;
    }

    /**
     * The date (and optionally time) when the artifact assessment was published. The date must change when the disposition 
     * changes and it must change if the workflow status code changes. In addition, it should change when the substantive 
     * content of the artifact assessment changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * A copyright statement relating to the artifact assessment and/or its contents. Copyright statements are generally 
     * legal restrictions on the use and publishing of the artifact assessment.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getCopyright() {
        return copyright;
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
     * A reference to a resource, canonical resource, or non-FHIR resource which the comment or assessment is about.
     * 
     * @return
     *     An immutable object of type {@link Reference}, {@link Canonical} or {@link Uri} that is non-null.
     */
    public Element getArtifact() {
        return artifact;
    }

    /**
     * A component comment, classifier, or rating of the artifact.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Content} that may be empty.
     */
    public List<Content> getContent() {
        return content;
    }

    /**
     * Indicates the workflow status of the comment or change request.
     * 
     * @return
     *     An immutable object of type {@link WorkflowStatus} that may be null.
     */
    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * Indicates the disposition of the responsible party to the comment or change request.
     * 
     * @return
     *     An immutable object of type {@link Disposition} that may be null.
     */
    public Disposition getDisposition() {
        return disposition;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (title != null) || 
            (citeAs != null) || 
            (date != null) || 
            (copyright != null) || 
            (approvalDate != null) || 
            (lastReviewDate != null) || 
            (artifact != null) || 
            !content.isEmpty() || 
            (workflowStatus != null) || 
            (disposition != null);
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
                accept(title, "title", visitor);
                accept(citeAs, "citeAs", visitor);
                accept(date, "date", visitor);
                accept(copyright, "copyright", visitor);
                accept(approvalDate, "approvalDate", visitor);
                accept(lastReviewDate, "lastReviewDate", visitor);
                accept(artifact, "artifact", visitor);
                accept(content, "content", visitor, Content.class);
                accept(workflowStatus, "workflowStatus", visitor);
                accept(disposition, "disposition", visitor);
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
        ArtifactAssessment other = (ArtifactAssessment) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(title, other.title) && 
            Objects.equals(citeAs, other.citeAs) && 
            Objects.equals(date, other.date) && 
            Objects.equals(copyright, other.copyright) && 
            Objects.equals(approvalDate, other.approvalDate) && 
            Objects.equals(lastReviewDate, other.lastReviewDate) && 
            Objects.equals(artifact, other.artifact) && 
            Objects.equals(content, other.content) && 
            Objects.equals(workflowStatus, other.workflowStatus) && 
            Objects.equals(disposition, other.disposition);
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
                title, 
                citeAs, 
                date, 
                copyright, 
                approvalDate, 
                lastReviewDate, 
                artifact, 
                content, 
                workflowStatus, 
                disposition);
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
        private String title;
        private Element citeAs;
        private DateTime date;
        private Markdown copyright;
        private Date approvalDate;
        private Date lastReviewDate;
        private Element artifact;
        private List<Content> content = new ArrayList<>();
        private WorkflowStatus workflowStatus;
        private Disposition disposition;

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
         * A formal identifier that is used to identify this artifact assessment when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the artifact assessment
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
         * A formal identifier that is used to identify this artifact assessment when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the artifact assessment
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
         *     A short title for the assessment for use in displaying and selecting
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
         * A short title for the assessment for use in displaying and selecting.
         * 
         * @param title
         *     A short title for the assessment for use in displaying and selecting
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Display of or reference to the bibliographic citation of the comment, classifier, or rating.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Reference}</li>
         * <li>{@link Markdown}</li>
         * </ul>
         * 
         * When of type {@link Reference}, the allowed resource types for this reference are:
         * <ul>
         * <li>{@link Citation}</li>
         * </ul>
         * 
         * @param citeAs
         *     How to cite the comment or rating
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder citeAs(Element citeAs) {
            this.citeAs = citeAs;
            return this;
        }

        /**
         * The date (and optionally time) when the artifact assessment was published. The date must change when the disposition 
         * changes and it must change if the workflow status code changes. In addition, it should change when the substantive 
         * content of the artifact assessment changes.
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
         * A copyright statement relating to the artifact assessment and/or its contents. Copyright statements are generally 
         * legal restrictions on the use and publishing of the artifact assessment.
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
         * Convenience method for setting {@code approvalDate}.
         * 
         * @param approvalDate
         *     When the artifact assessment was approved by publisher
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
         *     When the artifact assessment was approved by publisher
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
         *     When the artifact assessment was last reviewed by the publisher
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
         *     When the artifact assessment was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
        }

        /**
         * A reference to a resource, canonical resource, or non-FHIR resource which the comment or assessment is about.
         * 
         * <p>This element is required.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Reference}</li>
         * <li>{@link Canonical}</li>
         * <li>{@link Uri}</li>
         * </ul>
         * 
         * @param artifact
         *     The artifact assessed, commented upon or rated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder artifact(Element artifact) {
            this.artifact = artifact;
            return this;
        }

        /**
         * A component comment, classifier, or rating of the artifact.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param content
         *     Comment, classifier, or rating content
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder content(Content... content) {
            for (Content value : content) {
                this.content.add(value);
            }
            return this;
        }

        /**
         * A component comment, classifier, or rating of the artifact.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param content
         *     Comment, classifier, or rating content
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder content(Collection<Content> content) {
            this.content = new ArrayList<>(content);
            return this;
        }

        /**
         * Indicates the workflow status of the comment or change request.
         * 
         * @param workflowStatus
         *     submitted | triaged | waiting-for-input | resolved-no-change | resolved-change-required | deferred | duplicate | 
         *     applied | published | entered-in-error
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder workflowStatus(WorkflowStatus workflowStatus) {
            this.workflowStatus = workflowStatus;
            return this;
        }

        /**
         * Indicates the disposition of the responsible party to the comment or change request.
         * 
         * @param disposition
         *     unresolved | not-persuasive | persuasive | persuasive-with-modification | not-persuasive-with-modification
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder disposition(Disposition disposition) {
            this.disposition = disposition;
            return this;
        }

        /**
         * Build the {@link ArtifactAssessment}
         * 
         * <p>Required elements:
         * <ul>
         * <li>artifact</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ArtifactAssessment}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ArtifactAssessment per the base specification
         */
        @Override
        public ArtifactAssessment build() {
            ArtifactAssessment artifactAssessment = new ArtifactAssessment(this);
            if (validating) {
                validate(artifactAssessment);
            }
            return artifactAssessment;
        }

        protected void validate(ArtifactAssessment artifactAssessment) {
            super.validate(artifactAssessment);
            ValidationSupport.checkList(artifactAssessment.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(artifactAssessment.citeAs, "citeAs", Reference.class, Markdown.class);
            ValidationSupport.requireChoiceElement(artifactAssessment.artifact, "artifact", Reference.class, Canonical.class, Uri.class);
            ValidationSupport.checkList(artifactAssessment.content, "content", Content.class);
            ValidationSupport.checkReferenceType(artifactAssessment.citeAs, "citeAs", "Citation");
        }

        protected Builder from(ArtifactAssessment artifactAssessment) {
            super.from(artifactAssessment);
            identifier.addAll(artifactAssessment.identifier);
            title = artifactAssessment.title;
            citeAs = artifactAssessment.citeAs;
            date = artifactAssessment.date;
            copyright = artifactAssessment.copyright;
            approvalDate = artifactAssessment.approvalDate;
            lastReviewDate = artifactAssessment.lastReviewDate;
            artifact = artifactAssessment.artifact;
            content.addAll(artifactAssessment.content);
            workflowStatus = artifactAssessment.workflowStatus;
            disposition = artifactAssessment.disposition;
            return this;
        }
    }

    /**
     * A component comment, classifier, or rating of the artifact.
     */
    public static class Content extends BackboneElement {
        @Binding(
            bindingName = "InformationType",
            strength = BindingStrength.Value.REQUIRED,
            valueSet = "http://hl7.org/fhir/ValueSet/artifactassessment-information-type|5.0.0"
        )
        private final InformationType informationType;
        private final Markdown summary;
        @Binding(
            bindingName = "EvidenceCertaintyType",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/certainty-type"
        )
        private final CodeableConcept type;
        @Binding(
            bindingName = "EvidenceCertaintyRating",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/certainty-rating"
        )
        private final List<CodeableConcept> classifier;
        private final Quantity quantity;
        @ReferenceTarget({ "Patient", "Practitioner", "PractitionerRole", "Organization", "Device" })
        private final Reference author;
        private final List<Uri> path;
        private final List<RelatedArtifact> relatedArtifact;
        private final Boolean freeToShare;
        private final List<ArtifactAssessment.Content> component;

        private Content(Builder builder) {
            super(builder);
            informationType = builder.informationType;
            summary = builder.summary;
            type = builder.type;
            classifier = Collections.unmodifiableList(builder.classifier);
            quantity = builder.quantity;
            author = builder.author;
            path = Collections.unmodifiableList(builder.path);
            relatedArtifact = Collections.unmodifiableList(builder.relatedArtifact);
            freeToShare = builder.freeToShare;
            component = Collections.unmodifiableList(builder.component);
        }

        /**
         * The type of information this component of the content represents.
         * 
         * @return
         *     An immutable object of type {@link InformationType} that may be null.
         */
        public InformationType getInformationType() {
            return informationType;
        }

        /**
         * A brief summary of the content of this component.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getSummary() {
            return summary;
        }

        /**
         * Indicates what type of content this component represents.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getType() {
            return type;
        }

        /**
         * Represents a rating, classifier, or assessment of the artifact.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getClassifier() {
            return classifier;
        }

        /**
         * A quantitative rating of the artifact.
         * 
         * @return
         *     An immutable object of type {@link Quantity} that may be null.
         */
        public Quantity getQuantity() {
            return quantity;
        }

        /**
         * Indicates who or what authored the content.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getAuthor() {
            return author;
        }

        /**
         * A URI that points to what the comment is about, such as a line of text in the CQL, or a specific element in a resource.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
         */
        public List<Uri> getPath() {
            return path;
        }

        /**
         * Additional related artifacts that provide supporting documentation, additional evidence, or further information 
         * related to the content.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
         */
        public List<RelatedArtifact> getRelatedArtifact() {
            return relatedArtifact;
        }

        /**
         * Acceptable to publicly share the comment, classifier or rating.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getFreeToShare() {
            return freeToShare;
        }

        /**
         * If the informationType is container, the components of the content.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Content} that may be empty.
         */
        public List<ArtifactAssessment.Content> getComponent() {
            return component;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (informationType != null) || 
                (summary != null) || 
                (type != null) || 
                !classifier.isEmpty() || 
                (quantity != null) || 
                (author != null) || 
                !path.isEmpty() || 
                !relatedArtifact.isEmpty() || 
                (freeToShare != null) || 
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
                    accept(informationType, "informationType", visitor);
                    accept(summary, "summary", visitor);
                    accept(type, "type", visitor);
                    accept(classifier, "classifier", visitor, CodeableConcept.class);
                    accept(quantity, "quantity", visitor);
                    accept(author, "author", visitor);
                    accept(path, "path", visitor, Uri.class);
                    accept(relatedArtifact, "relatedArtifact", visitor, RelatedArtifact.class);
                    accept(freeToShare, "freeToShare", visitor);
                    accept(component, "component", visitor, ArtifactAssessment.Content.class);
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
            Content other = (Content) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(informationType, other.informationType) && 
                Objects.equals(summary, other.summary) && 
                Objects.equals(type, other.type) && 
                Objects.equals(classifier, other.classifier) && 
                Objects.equals(quantity, other.quantity) && 
                Objects.equals(author, other.author) && 
                Objects.equals(path, other.path) && 
                Objects.equals(relatedArtifact, other.relatedArtifact) && 
                Objects.equals(freeToShare, other.freeToShare) && 
                Objects.equals(component, other.component);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    informationType, 
                    summary, 
                    type, 
                    classifier, 
                    quantity, 
                    author, 
                    path, 
                    relatedArtifact, 
                    freeToShare, 
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
            private InformationType informationType;
            private Markdown summary;
            private CodeableConcept type;
            private List<CodeableConcept> classifier = new ArrayList<>();
            private Quantity quantity;
            private Reference author;
            private List<Uri> path = new ArrayList<>();
            private List<RelatedArtifact> relatedArtifact = new ArrayList<>();
            private Boolean freeToShare;
            private List<ArtifactAssessment.Content> component = new ArrayList<>();

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
             * The type of information this component of the content represents.
             * 
             * @param informationType
             *     comment | classifier | rating | container | response | change-request
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder informationType(InformationType informationType) {
                this.informationType = informationType;
                return this;
            }

            /**
             * A brief summary of the content of this component.
             * 
             * @param summary
             *     Brief summary of the content
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder summary(Markdown summary) {
                this.summary = summary;
                return this;
            }

            /**
             * Indicates what type of content this component represents.
             * 
             * @param type
             *     What type of content
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(CodeableConcept type) {
                this.type = type;
                return this;
            }

            /**
             * Represents a rating, classifier, or assessment of the artifact.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classifier
             *     Rating, classifier, or assessment
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
             * Represents a rating, classifier, or assessment of the artifact.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param classifier
             *     Rating, classifier, or assessment
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
             * A quantitative rating of the artifact.
             * 
             * @param quantity
             *     Quantitative rating
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder quantity(Quantity quantity) {
                this.quantity = quantity;
                return this;
            }

            /**
             * Indicates who or what authored the content.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Device}</li>
             * </ul>
             * 
             * @param author
             *     Who authored the content
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder author(Reference author) {
                this.author = author;
                return this;
            }

            /**
             * A URI that points to what the comment is about, such as a line of text in the CQL, or a specific element in a resource.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param path
             *     What the comment is directed to
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder path(Uri... path) {
                for (Uri value : path) {
                    this.path.add(value);
                }
                return this;
            }

            /**
             * A URI that points to what the comment is about, such as a line of text in the CQL, or a specific element in a resource.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param path
             *     What the comment is directed to
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder path(Collection<Uri> path) {
                this.path = new ArrayList<>(path);
                return this;
            }

            /**
             * Additional related artifacts that provide supporting documentation, additional evidence, or further information 
             * related to the content.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedArtifact
             *     Additional information
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
             * Additional related artifacts that provide supporting documentation, additional evidence, or further information 
             * related to the content.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param relatedArtifact
             *     Additional information
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
             * Convenience method for setting {@code freeToShare}.
             * 
             * @param freeToShare
             *     Acceptable to publicly share the resource content
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #freeToShare(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder freeToShare(java.lang.Boolean freeToShare) {
                this.freeToShare = (freeToShare == null) ? null : Boolean.of(freeToShare);
                return this;
            }

            /**
             * Acceptable to publicly share the comment, classifier or rating.
             * 
             * @param freeToShare
             *     Acceptable to publicly share the resource content
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder freeToShare(Boolean freeToShare) {
                this.freeToShare = freeToShare;
                return this;
            }

            /**
             * If the informationType is container, the components of the content.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param component
             *     Contained content
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder component(ArtifactAssessment.Content... component) {
                for (ArtifactAssessment.Content value : component) {
                    this.component.add(value);
                }
                return this;
            }

            /**
             * If the informationType is container, the components of the content.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param component
             *     Contained content
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder component(Collection<ArtifactAssessment.Content> component) {
                this.component = new ArrayList<>(component);
                return this;
            }

            /**
             * Build the {@link Content}
             * 
             * @return
             *     An immutable object of type {@link Content}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Content per the base specification
             */
            @Override
            public Content build() {
                Content content = new Content(this);
                if (validating) {
                    validate(content);
                }
                return content;
            }

            protected void validate(Content content) {
                super.validate(content);
                ValidationSupport.checkList(content.classifier, "classifier", CodeableConcept.class);
                ValidationSupport.checkList(content.path, "path", Uri.class);
                ValidationSupport.checkList(content.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
                ValidationSupport.checkList(content.component, "component", ArtifactAssessment.Content.class);
                ValidationSupport.checkReferenceType(content.author, "author", "Patient", "Practitioner", "PractitionerRole", "Organization", "Device");
                ValidationSupport.requireValueOrChildren(content);
            }

            protected Builder from(Content content) {
                super.from(content);
                informationType = content.informationType;
                summary = content.summary;
                type = content.type;
                classifier.addAll(content.classifier);
                quantity = content.quantity;
                author = content.author;
                path.addAll(content.path);
                relatedArtifact.addAll(content.relatedArtifact);
                freeToShare = content.freeToShare;
                component.addAll(content.component);
                return this;
            }
        }
    }
}
