/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/related-artifact-type")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class RelatedArtifactType extends Code {
    /**
     * Documentation
     * 
     * <p>Additional documentation for the knowledge resource. This would include additional instructions on usage as well as 
     * additional information on clinical context or appropriateness.
     */
    public static final RelatedArtifactType DOCUMENTATION = RelatedArtifactType.builder().value(Value.DOCUMENTATION).build();

    /**
     * Justification
     * 
     * <p>The target artifact is a summary of the justification for the knowledge resource including supporting evidence, 
     * relevant guidelines, or other clinically important information. This information is intended to provide a way to make 
     * the justification for the knowledge resource available to the consumer of interventions or results produced by the 
     * knowledge resource.
     */
    public static final RelatedArtifactType JUSTIFICATION = RelatedArtifactType.builder().value(Value.JUSTIFICATION).build();

    /**
     * Citation
     * 
     * <p>Bibliographic citation for papers, references, or other relevant material for the knowledge resource. This is 
     * intended to allow for citation of related material, but that was not necessarily specifically prepared in connection 
     * with this knowledge resource.
     */
    public static final RelatedArtifactType CITATION = RelatedArtifactType.builder().value(Value.CITATION).build();

    /**
     * Predecessor
     * 
     * <p>The previous version of the knowledge artifact, used to establish an ordering of versions of an artifact, 
     * independent of the status of each version.
     */
    public static final RelatedArtifactType PREDECESSOR = RelatedArtifactType.builder().value(Value.PREDECESSOR).build();

    /**
     * Successor
     * 
     * <p>The subsequent version of the knowledge artfact, used to establish an ordering of versions of an artifact, 
     * independent of the status of each version.
     */
    public static final RelatedArtifactType SUCCESSOR = RelatedArtifactType.builder().value(Value.SUCCESSOR).build();

    /**
     * Derived From
     * 
     * <p>This artifact is derived from the target artifact. This is intended to capture the relationship in which a 
     * particular knowledge resource is based on the content of another artifact, but is modified to capture either a 
     * different set of overall requirements, or a more specific set of requirements such as those involved in a particular 
     * institution or clinical setting. The artifact may be derived from one or more target artifacts.
     */
    public static final RelatedArtifactType DERIVED_FROM = RelatedArtifactType.builder().value(Value.DERIVED_FROM).build();

    /**
     * Depends On
     * 
     * <p>This artifact depends on the target artifact. There is a requirement to use the target artifact in the creation or 
     * interpretation of this artifact.
     */
    public static final RelatedArtifactType DEPENDS_ON = RelatedArtifactType.builder().value(Value.DEPENDS_ON).build();

    /**
     * Composed Of
     * 
     * <p>This artifact is composed of the target artifact. This artifact is constructed with the target artifact as a 
     * component. The target artifact is a part of this artifact. (A dataset is composed of data.).
     */
    public static final RelatedArtifactType COMPOSED_OF = RelatedArtifactType.builder().value(Value.COMPOSED_OF).build();

    /**
     * Part Of
     * 
     * <p>This artifact is a part of the target artifact. The target artifact is composed of this artifact (and possibly 
     * other artifacts).
     */
    public static final RelatedArtifactType PART_OF = RelatedArtifactType.builder().value(Value.PART_OF).build();

    /**
     * Amends
     * 
     * <p>This artifact amends or changes the target artifact. This artifact adds additional information that is functionally 
     * expected to replace information in the target artifact. This artifact replaces a part but not all of the target 
     * artifact.
     */
    public static final RelatedArtifactType AMENDS = RelatedArtifactType.builder().value(Value.AMENDS).build();

    /**
     * Amended With
     * 
     * <p>This artifact is amended with or changed by the target artifact. There is information in this artifact that should 
     * be functionally replaced with information in the target artifact.
     */
    public static final RelatedArtifactType AMENDED_WITH = RelatedArtifactType.builder().value(Value.AMENDED_WITH).build();

    /**
     * Appends
     * 
     * <p>This artifact adds additional information to the target artifact. The additional information does not replace or 
     * change information in the target artifact.
     */
    public static final RelatedArtifactType APPENDS = RelatedArtifactType.builder().value(Value.APPENDS).build();

    /**
     * Appended With
     * 
     * <p>This artifact has additional information in the target artifact.
     */
    public static final RelatedArtifactType APPENDED_WITH = RelatedArtifactType.builder().value(Value.APPENDED_WITH).build();

    /**
     * Cites
     * 
     * <p>This artifact cites the target artifact. This may be a bibliographic citation for papers, references, or other 
     * relevant material for the knowledge resource. This is intended to allow for citation of related material, but that was 
     * not necessarily specifically prepared in connection with this knowledge resource.
     */
    public static final RelatedArtifactType CITES = RelatedArtifactType.builder().value(Value.CITES).build();

    /**
     * Cited By
     * 
     * <p>This artifact is cited by the target artifact.
     */
    public static final RelatedArtifactType CITED_BY = RelatedArtifactType.builder().value(Value.CITED_BY).build();

    /**
     * Is Comment On
     * 
     * <p>This artifact contains comments about the target artifact.
     */
    public static final RelatedArtifactType COMMENTS_ON = RelatedArtifactType.builder().value(Value.COMMENTS_ON).build();

    /**
     * Has Comment In
     * 
     * <p>This artifact has comments about it in the target artifact. The type of comments may be expressed in the 
     * targetClassifier element such as reply, review, editorial, feedback, solicited, unsolicited, structured, unstructured.
     */
    public static final RelatedArtifactType COMMENT_IN = RelatedArtifactType.builder().value(Value.COMMENT_IN).build();

    /**
     * Contains
     * 
     * <p>This artifact is a container in which the target artifact is contained. A container is a data structure whose 
     * instances are collections of other objects. (A database contains the dataset.).
     */
    public static final RelatedArtifactType CONTAINS = RelatedArtifactType.builder().value(Value.CONTAINS).build();

    /**
     * Contained In
     * 
     * <p>This artifact is contained in the target artifact. The target artifact is a data structure whose instances are 
     * collections of other objects.
     */
    public static final RelatedArtifactType CONTAINED_IN = RelatedArtifactType.builder().value(Value.CONTAINED_IN).build();

    /**
     * Corrects
     * 
     * <p>This artifact identifies errors and replacement content for the target artifact.
     */
    public static final RelatedArtifactType CORRECTS = RelatedArtifactType.builder().value(Value.CORRECTS).build();

    /**
     * Correction In
     * 
     * <p>This artifact has corrections to it in the target artifact. The target artifact identifies errors and replacement 
     * content for this artifact.
     */
    public static final RelatedArtifactType CORRECTION_IN = RelatedArtifactType.builder().value(Value.CORRECTION_IN).build();

    /**
     * Replaces
     * 
     * <p>This artifact replaces or supersedes the target artifact. The target artifact may be considered deprecated.
     */
    public static final RelatedArtifactType REPLACES = RelatedArtifactType.builder().value(Value.REPLACES).build();

    /**
     * Replaced With
     * 
     * <p>This artifact is replaced with or superseded by the target artifact. This artifact may be considered deprecated.
     */
    public static final RelatedArtifactType REPLACED_WITH = RelatedArtifactType.builder().value(Value.REPLACED_WITH).build();

    /**
     * Retracts
     * 
     * <p>This artifact retracts the target artifact. The content that was published in the target artifact should be 
     * considered removed from publication and should no longer be considered part of the public record.
     */
    public static final RelatedArtifactType RETRACTS = RelatedArtifactType.builder().value(Value.RETRACTS).build();

    /**
     * Retracted By
     * 
     * <p>This artifact is retracted by the target artifact. The content that was published in this artifact should be 
     * considered removed from publication and should no longer be considered part of the public record.
     */
    public static final RelatedArtifactType RETRACTED_BY = RelatedArtifactType.builder().value(Value.RETRACTED_BY).build();

    /**
     * Signs
     * 
     * <p>This artifact is a signature of the target artifact.
     */
    public static final RelatedArtifactType SIGNS = RelatedArtifactType.builder().value(Value.SIGNS).build();

    /**
     * Similar To
     * 
     * <p>This artifact has characteristics in common with the target artifact. This relationship may be used in systems to �
     * ��deduplicate�? knowledge artifacts from different sources, or in systems to show “similar items�?.
     */
    public static final RelatedArtifactType SIMILAR_TO = RelatedArtifactType.builder().value(Value.SIMILAR_TO).build();

    /**
     * Supports
     * 
     * <p>This artifact provides additional support for the target artifact. The type of support is not documentation as it 
     * does not describe, explain, or instruct regarding the target artifact.
     */
    public static final RelatedArtifactType SUPPORTS = RelatedArtifactType.builder().value(Value.SUPPORTS).build();

    /**
     * Supported With
     * 
     * <p>The target artifact contains additional information related to the knowledge artifact but is not documentation as 
     * the additional information does not describe, explain, or instruct regarding the knowledge artifact content or 
     * application. This could include an associated dataset.
     */
    public static final RelatedArtifactType SUPPORTED_WITH = RelatedArtifactType.builder().value(Value.SUPPORTED_WITH).build();

    /**
     * Transforms
     * 
     * <p>This artifact was generated by transforming the target artifact (e.g., format or language conversion). This is 
     * intended to capture the relationship in which a particular knowledge resource is based on the content of another 
     * artifact, but changes are only apparent in form and there is only one target artifact with the “transforms�? 
     * relationship type.
     */
    public static final RelatedArtifactType TRANSFORMS = RelatedArtifactType.builder().value(Value.TRANSFORMS).build();

    /**
     * Transformed Into
     * 
     * <p>This artifact was transformed into the target artifact (e.g., by format or language conversion).
     */
    public static final RelatedArtifactType TRANSFORMED_INTO = RelatedArtifactType.builder().value(Value.TRANSFORMED_INTO).build();

    /**
     * Transformed With
     * 
     * <p>This artifact was generated by transforming a related artifact (e.g., format or language conversion), noted 
     * separately with the “transforms�? relationship type. This transformation used the target artifact to inform the 
     * transformation. The target artifact may be a conversion script or translation guide.
     */
    public static final RelatedArtifactType TRANSFORMED_WITH = RelatedArtifactType.builder().value(Value.TRANSFORMED_WITH).build();

    /**
     * Documents
     * 
     * <p>This artifact provides additional documentation for the target artifact. This could include additional instructions 
     * on usage as well as additional information on clinical context or appropriateness.
     */
    public static final RelatedArtifactType DOCUMENTS = RelatedArtifactType.builder().value(Value.DOCUMENTS).build();

    /**
     * Specification Of
     * 
     * <p>The target artifact is a precise description of a concept in this artifact. This may be used when the 
     * RelatedArtifact datatype is used in elements contained in this artifact.
     */
    public static final RelatedArtifactType SPECIFICATION_OF = RelatedArtifactType.builder().value(Value.SPECIFICATION_OF).build();

    /**
     * Created With
     * 
     * <p>This artifact was created with the target artifact. The target artifact is a tool or support material used in the 
     * creation of the artifact, and not content that the artifact was derived from.
     */
    public static final RelatedArtifactType CREATED_WITH = RelatedArtifactType.builder().value(Value.CREATED_WITH).build();

    /**
     * Cite As
     * 
     * <p>The related artifact is the citation for this artifact.
     */
    public static final RelatedArtifactType CITE_AS = RelatedArtifactType.builder().value(Value.CITE_AS).build();

    private volatile int hashCode;

    private RelatedArtifactType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this RelatedArtifactType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating RelatedArtifactType objects from a passed enum value.
     */
    public static RelatedArtifactType of(Value value) {
        switch (value) {
        case DOCUMENTATION:
            return DOCUMENTATION;
        case JUSTIFICATION:
            return JUSTIFICATION;
        case CITATION:
            return CITATION;
        case PREDECESSOR:
            return PREDECESSOR;
        case SUCCESSOR:
            return SUCCESSOR;
        case DERIVED_FROM:
            return DERIVED_FROM;
        case DEPENDS_ON:
            return DEPENDS_ON;
        case COMPOSED_OF:
            return COMPOSED_OF;
        case PART_OF:
            return PART_OF;
        case AMENDS:
            return AMENDS;
        case AMENDED_WITH:
            return AMENDED_WITH;
        case APPENDS:
            return APPENDS;
        case APPENDED_WITH:
            return APPENDED_WITH;
        case CITES:
            return CITES;
        case CITED_BY:
            return CITED_BY;
        case COMMENTS_ON:
            return COMMENTS_ON;
        case COMMENT_IN:
            return COMMENT_IN;
        case CONTAINS:
            return CONTAINS;
        case CONTAINED_IN:
            return CONTAINED_IN;
        case CORRECTS:
            return CORRECTS;
        case CORRECTION_IN:
            return CORRECTION_IN;
        case REPLACES:
            return REPLACES;
        case REPLACED_WITH:
            return REPLACED_WITH;
        case RETRACTS:
            return RETRACTS;
        case RETRACTED_BY:
            return RETRACTED_BY;
        case SIGNS:
            return SIGNS;
        case SIMILAR_TO:
            return SIMILAR_TO;
        case SUPPORTS:
            return SUPPORTS;
        case SUPPORTED_WITH:
            return SUPPORTED_WITH;
        case TRANSFORMS:
            return TRANSFORMS;
        case TRANSFORMED_INTO:
            return TRANSFORMED_INTO;
        case TRANSFORMED_WITH:
            return TRANSFORMED_WITH;
        case DOCUMENTS:
            return DOCUMENTS;
        case SPECIFICATION_OF:
            return SPECIFICATION_OF;
        case CREATED_WITH:
            return CREATED_WITH;
        case CITE_AS:
            return CITE_AS;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating RelatedArtifactType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static RelatedArtifactType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating RelatedArtifactType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating RelatedArtifactType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(Value.from(value));
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
        RelatedArtifactType other = (RelatedArtifactType) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(Value.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for RelatedArtifactType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public RelatedArtifactType build() {
            RelatedArtifactType relatedArtifactType = new RelatedArtifactType(this);
            if (validating) {
                validate(relatedArtifactType);
            }
            return relatedArtifactType;
        }

        protected void validate(RelatedArtifactType relatedArtifactType) {
            super.validate(relatedArtifactType);
        }

        protected Builder from(RelatedArtifactType relatedArtifactType) {
            super.from(relatedArtifactType);
            return this;
        }
    }

    public enum Value {
        /**
         * Documentation
         * 
         * <p>Additional documentation for the knowledge resource. This would include additional instructions on usage as well as 
         * additional information on clinical context or appropriateness.
         */
        DOCUMENTATION("documentation"),

        /**
         * Justification
         * 
         * <p>The target artifact is a summary of the justification for the knowledge resource including supporting evidence, 
         * relevant guidelines, or other clinically important information. This information is intended to provide a way to make 
         * the justification for the knowledge resource available to the consumer of interventions or results produced by the 
         * knowledge resource.
         */
        JUSTIFICATION("justification"),

        /**
         * Citation
         * 
         * <p>Bibliographic citation for papers, references, or other relevant material for the knowledge resource. This is 
         * intended to allow for citation of related material, but that was not necessarily specifically prepared in connection 
         * with this knowledge resource.
         */
        CITATION("citation"),

        /**
         * Predecessor
         * 
         * <p>The previous version of the knowledge artifact, used to establish an ordering of versions of an artifact, 
         * independent of the status of each version.
         */
        PREDECESSOR("predecessor"),

        /**
         * Successor
         * 
         * <p>The subsequent version of the knowledge artfact, used to establish an ordering of versions of an artifact, 
         * independent of the status of each version.
         */
        SUCCESSOR("successor"),

        /**
         * Derived From
         * 
         * <p>This artifact is derived from the target artifact. This is intended to capture the relationship in which a 
         * particular knowledge resource is based on the content of another artifact, but is modified to capture either a 
         * different set of overall requirements, or a more specific set of requirements such as those involved in a particular 
         * institution or clinical setting. The artifact may be derived from one or more target artifacts.
         */
        DERIVED_FROM("derived-from"),

        /**
         * Depends On
         * 
         * <p>This artifact depends on the target artifact. There is a requirement to use the target artifact in the creation or 
         * interpretation of this artifact.
         */
        DEPENDS_ON("depends-on"),

        /**
         * Composed Of
         * 
         * <p>This artifact is composed of the target artifact. This artifact is constructed with the target artifact as a 
         * component. The target artifact is a part of this artifact. (A dataset is composed of data.).
         */
        COMPOSED_OF("composed-of"),

        /**
         * Part Of
         * 
         * <p>This artifact is a part of the target artifact. The target artifact is composed of this artifact (and possibly 
         * other artifacts).
         */
        PART_OF("part-of"),

        /**
         * Amends
         * 
         * <p>This artifact amends or changes the target artifact. This artifact adds additional information that is functionally 
         * expected to replace information in the target artifact. This artifact replaces a part but not all of the target 
         * artifact.
         */
        AMENDS("amends"),

        /**
         * Amended With
         * 
         * <p>This artifact is amended with or changed by the target artifact. There is information in this artifact that should 
         * be functionally replaced with information in the target artifact.
         */
        AMENDED_WITH("amended-with"),

        /**
         * Appends
         * 
         * <p>This artifact adds additional information to the target artifact. The additional information does not replace or 
         * change information in the target artifact.
         */
        APPENDS("appends"),

        /**
         * Appended With
         * 
         * <p>This artifact has additional information in the target artifact.
         */
        APPENDED_WITH("appended-with"),

        /**
         * Cites
         * 
         * <p>This artifact cites the target artifact. This may be a bibliographic citation for papers, references, or other 
         * relevant material for the knowledge resource. This is intended to allow for citation of related material, but that was 
         * not necessarily specifically prepared in connection with this knowledge resource.
         */
        CITES("cites"),

        /**
         * Cited By
         * 
         * <p>This artifact is cited by the target artifact.
         */
        CITED_BY("cited-by"),

        /**
         * Is Comment On
         * 
         * <p>This artifact contains comments about the target artifact.
         */
        COMMENTS_ON("comments-on"),

        /**
         * Has Comment In
         * 
         * <p>This artifact has comments about it in the target artifact. The type of comments may be expressed in the 
         * targetClassifier element such as reply, review, editorial, feedback, solicited, unsolicited, structured, unstructured.
         */
        COMMENT_IN("comment-in"),

        /**
         * Contains
         * 
         * <p>This artifact is a container in which the target artifact is contained. A container is a data structure whose 
         * instances are collections of other objects. (A database contains the dataset.).
         */
        CONTAINS("contains"),

        /**
         * Contained In
         * 
         * <p>This artifact is contained in the target artifact. The target artifact is a data structure whose instances are 
         * collections of other objects.
         */
        CONTAINED_IN("contained-in"),

        /**
         * Corrects
         * 
         * <p>This artifact identifies errors and replacement content for the target artifact.
         */
        CORRECTS("corrects"),

        /**
         * Correction In
         * 
         * <p>This artifact has corrections to it in the target artifact. The target artifact identifies errors and replacement 
         * content for this artifact.
         */
        CORRECTION_IN("correction-in"),

        /**
         * Replaces
         * 
         * <p>This artifact replaces or supersedes the target artifact. The target artifact may be considered deprecated.
         */
        REPLACES("replaces"),

        /**
         * Replaced With
         * 
         * <p>This artifact is replaced with or superseded by the target artifact. This artifact may be considered deprecated.
         */
        REPLACED_WITH("replaced-with"),

        /**
         * Retracts
         * 
         * <p>This artifact retracts the target artifact. The content that was published in the target artifact should be 
         * considered removed from publication and should no longer be considered part of the public record.
         */
        RETRACTS("retracts"),

        /**
         * Retracted By
         * 
         * <p>This artifact is retracted by the target artifact. The content that was published in this artifact should be 
         * considered removed from publication and should no longer be considered part of the public record.
         */
        RETRACTED_BY("retracted-by"),

        /**
         * Signs
         * 
         * <p>This artifact is a signature of the target artifact.
         */
        SIGNS("signs"),

        /**
         * Similar To
         * 
         * <p>This artifact has characteristics in common with the target artifact. This relationship may be used in systems to �
         * ��deduplicate�? knowledge artifacts from different sources, or in systems to show “similar items�?.
         */
        SIMILAR_TO("similar-to"),

        /**
         * Supports
         * 
         * <p>This artifact provides additional support for the target artifact. The type of support is not documentation as it 
         * does not describe, explain, or instruct regarding the target artifact.
         */
        SUPPORTS("supports"),

        /**
         * Supported With
         * 
         * <p>The target artifact contains additional information related to the knowledge artifact but is not documentation as 
         * the additional information does not describe, explain, or instruct regarding the knowledge artifact content or 
         * application. This could include an associated dataset.
         */
        SUPPORTED_WITH("supported-with"),

        /**
         * Transforms
         * 
         * <p>This artifact was generated by transforming the target artifact (e.g., format or language conversion). This is 
         * intended to capture the relationship in which a particular knowledge resource is based on the content of another 
         * artifact, but changes are only apparent in form and there is only one target artifact with the “transforms�? 
         * relationship type.
         */
        TRANSFORMS("transforms"),

        /**
         * Transformed Into
         * 
         * <p>This artifact was transformed into the target artifact (e.g., by format or language conversion).
         */
        TRANSFORMED_INTO("transformed-into"),

        /**
         * Transformed With
         * 
         * <p>This artifact was generated by transforming a related artifact (e.g., format or language conversion), noted 
         * separately with the “transforms�? relationship type. This transformation used the target artifact to inform the 
         * transformation. The target artifact may be a conversion script or translation guide.
         */
        TRANSFORMED_WITH("transformed-with"),

        /**
         * Documents
         * 
         * <p>This artifact provides additional documentation for the target artifact. This could include additional instructions 
         * on usage as well as additional information on clinical context or appropriateness.
         */
        DOCUMENTS("documents"),

        /**
         * Specification Of
         * 
         * <p>The target artifact is a precise description of a concept in this artifact. This may be used when the 
         * RelatedArtifact datatype is used in elements contained in this artifact.
         */
        SPECIFICATION_OF("specification-of"),

        /**
         * Created With
         * 
         * <p>This artifact was created with the target artifact. The target artifact is a tool or support material used in the 
         * creation of the artifact, and not content that the artifact was derived from.
         */
        CREATED_WITH("created-with"),

        /**
         * Cite As
         * 
         * <p>The related artifact is the citation for this artifact.
         */
        CITE_AS("cite-as");

        private final java.lang.String value;

        Value(java.lang.String value) {
            this.value = value;
        }

        /**
         * @return
         *     The java.lang.String value of the code represented by this enum
         */
        public java.lang.String value() {
            return value;
        }

        /**
         * Factory method for creating RelatedArtifactType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding RelatedArtifactType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "documentation":
                return DOCUMENTATION;
            case "justification":
                return JUSTIFICATION;
            case "citation":
                return CITATION;
            case "predecessor":
                return PREDECESSOR;
            case "successor":
                return SUCCESSOR;
            case "derived-from":
                return DERIVED_FROM;
            case "depends-on":
                return DEPENDS_ON;
            case "composed-of":
                return COMPOSED_OF;
            case "part-of":
                return PART_OF;
            case "amends":
                return AMENDS;
            case "amended-with":
                return AMENDED_WITH;
            case "appends":
                return APPENDS;
            case "appended-with":
                return APPENDED_WITH;
            case "cites":
                return CITES;
            case "cited-by":
                return CITED_BY;
            case "comments-on":
                return COMMENTS_ON;
            case "comment-in":
                return COMMENT_IN;
            case "contains":
                return CONTAINS;
            case "contained-in":
                return CONTAINED_IN;
            case "corrects":
                return CORRECTS;
            case "correction-in":
                return CORRECTION_IN;
            case "replaces":
                return REPLACES;
            case "replaced-with":
                return REPLACED_WITH;
            case "retracts":
                return RETRACTS;
            case "retracted-by":
                return RETRACTED_BY;
            case "signs":
                return SIGNS;
            case "similar-to":
                return SIMILAR_TO;
            case "supports":
                return SUPPORTS;
            case "supported-with":
                return SUPPORTED_WITH;
            case "transforms":
                return TRANSFORMS;
            case "transformed-into":
                return TRANSFORMED_INTO;
            case "transformed-with":
                return TRANSFORMED_WITH;
            case "documents":
                return DOCUMENTS;
            case "specification-of":
                return SPECIFICATION_OF;
            case "created-with":
                return CREATED_WITH;
            case "cite-as":
                return CITE_AS;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
