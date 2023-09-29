/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.RelatedArtifactPublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.RelatedArtifactType;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Related artifacts such as additional documentation, justification, or bibliographic references.
 */
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class RelatedArtifact extends DataType {
    @Summary
    @Binding(
        bindingName = "RelatedArtifactType",
        strength = BindingStrength.Value.REQUIRED,
        description = "The type of relationship to the related artifact.",
        valueSet = "http://hl7.org/fhir/ValueSet/related-artifact-type|5.0.0"
    )
    @Required
    private final RelatedArtifactType type;
    @Summary
    @Binding(
        bindingName = "RelatedArtifactClassifier",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Additional classifiers for the related artifact.",
        valueSet = "http://hl7.org/fhir/ValueSet/citation-artifact-classifier"
    )
    private final List<CodeableConcept> classifier;
    @Summary
    private final String label;
    @Summary
    private final String display;
    @Summary
    private final Markdown citation;
    @Summary
    private final Attachment document;
    @Summary
    private final Canonical resource;
    @Summary
    private final Reference resourceReference;
    @Summary
    @Binding(
        bindingName = "RelatedArtifactPublicationStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Publication status of an artifact being referred to.",
        valueSet = "http://hl7.org/fhir/ValueSet/publication-status|5.0.0"
    )
    private final RelatedArtifactPublicationStatus publicationStatus;
    @Summary
    private final Date publicationDate;

    private RelatedArtifact(Builder builder) {
        super(builder);
        type = builder.type;
        classifier = Collections.unmodifiableList(builder.classifier);
        label = builder.label;
        display = builder.display;
        citation = builder.citation;
        document = builder.document;
        resource = builder.resource;
        resourceReference = builder.resourceReference;
        publicationStatus = builder.publicationStatus;
        publicationDate = builder.publicationDate;
    }

    /**
     * The type of relationship to the related artifact.
     * 
     * @return
     *     An immutable object of type {@link RelatedArtifactType} that is non-null.
     */
    public RelatedArtifactType getType() {
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
     * A short label that can be used to reference the citation from elsewhere in the containing artifact, such as a footnote 
     * index.
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
     * The document being referenced, represented as an attachment. This is exclusive with the resource element.
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

    /**
     * The publication status of the artifact being referred to.
     * 
     * @return
     *     An immutable object of type {@link RelatedArtifactPublicationStatus} that may be null.
     */
    public RelatedArtifactPublicationStatus getPublicationStatus() {
        return publicationStatus;
    }

    /**
     * The date of publication of the artifact being referred to.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getPublicationDate() {
        return publicationDate;
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
            (resourceReference != null) || 
            (publicationStatus != null) || 
            (publicationDate != null);
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(extension, "extension", visitor, Extension.class);
                accept(type, "type", visitor);
                accept(classifier, "classifier", visitor, CodeableConcept.class);
                accept(label, "label", visitor);
                accept(display, "display", visitor);
                accept(citation, "citation", visitor);
                accept(document, "document", visitor);
                accept(resource, "resource", visitor);
                accept(resourceReference, "resourceReference", visitor);
                accept(publicationStatus, "publicationStatus", visitor);
                accept(publicationDate, "publicationDate", visitor);
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
        RelatedArtifact other = (RelatedArtifact) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(type, other.type) && 
            Objects.equals(classifier, other.classifier) && 
            Objects.equals(label, other.label) && 
            Objects.equals(display, other.display) && 
            Objects.equals(citation, other.citation) && 
            Objects.equals(document, other.document) && 
            Objects.equals(resource, other.resource) && 
            Objects.equals(resourceReference, other.resourceReference) && 
            Objects.equals(publicationStatus, other.publicationStatus) && 
            Objects.equals(publicationDate, other.publicationDate);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                extension, 
                type, 
                classifier, 
                label, 
                display, 
                citation, 
                document, 
                resource, 
                resourceReference, 
                publicationStatus, 
                publicationDate);
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

    public static class Builder extends DataType.Builder {
        private RelatedArtifactType type;
        private List<CodeableConcept> classifier = new ArrayList<>();
        private String label;
        private String display;
        private Markdown citation;
        private Attachment document;
        private Canonical resource;
        private Reference resourceReference;
        private RelatedArtifactPublicationStatus publicationStatus;
        private Date publicationDate;

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
         * The type of relationship to the related artifact.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     documentation | justification | citation | predecessor | successor | derived-from | depends-on | composed-of | part-of 
         *     | amends | amended-with | appends | appended-with | cites | cited-by | comments-on | comment-in | contains | contained-
         *     in | corrects | correction-in | replaces | replaced-with | retracts | retracted-by | signs | similar-to | supports | 
         *     supported-with | transforms | transformed-into | transformed-with | documents | specification-of | created-with | cite-
         *     as
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(RelatedArtifactType type) {
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
         * A short label that can be used to reference the citation from elsewhere in the containing artifact, such as a footnote 
         * index.
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
         * The document being referenced, represented as an attachment. This is exclusive with the resource element.
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
         * The publication status of the artifact being referred to.
         * 
         * @param publicationStatus
         *     draft | active | retired | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder publicationStatus(RelatedArtifactPublicationStatus publicationStatus) {
            this.publicationStatus = publicationStatus;
            return this;
        }

        /**
         * Convenience method for setting {@code publicationDate}.
         * 
         * @param publicationDate
         *     Date of publication of the artifact being referred to
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #publicationDate(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder publicationDate(java.time.LocalDate publicationDate) {
            this.publicationDate = (publicationDate == null) ? null : Date.of(publicationDate);
            return this;
        }

        /**
         * The date of publication of the artifact being referred to.
         * 
         * @param publicationDate
         *     Date of publication of the artifact being referred to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder publicationDate(Date publicationDate) {
            this.publicationDate = publicationDate;
            return this;
        }

        /**
         * Build the {@link RelatedArtifact}
         * 
         * <p>Required elements:
         * <ul>
         * <li>type</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link RelatedArtifact}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid RelatedArtifact per the base specification
         */
        @Override
        public RelatedArtifact build() {
            RelatedArtifact relatedArtifact = new RelatedArtifact(this);
            if (validating) {
                validate(relatedArtifact);
            }
            return relatedArtifact;
        }

        protected void validate(RelatedArtifact relatedArtifact) {
            super.validate(relatedArtifact);
            ValidationSupport.requireNonNull(relatedArtifact.type, "type");
            ValidationSupport.checkList(relatedArtifact.classifier, "classifier", CodeableConcept.class);
            ValidationSupport.requireValueOrChildren(relatedArtifact);
        }

        protected Builder from(RelatedArtifact relatedArtifact) {
            super.from(relatedArtifact);
            type = relatedArtifact.type;
            classifier.addAll(relatedArtifact.classifier);
            label = relatedArtifact.label;
            display = relatedArtifact.display;
            citation = relatedArtifact.citation;
            document = relatedArtifact.document;
            resource = relatedArtifact.resource;
            resourceReference = relatedArtifact.resourceReference;
            publicationStatus = relatedArtifact.publicationStatus;
            publicationDate = relatedArtifact.publicationDate;
            return this;
        }
    }
}
