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

import org.linuxforhealth.fhir.model.annotation.Choice;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.code.*;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/*
 * Modifications:
 *
 * - Changed superclass to MetadataResource even though it is an interface (in FHIR terms); the code generator could
 *   not map this to Java in the expected manner
 *   
 * - Hand-edited type of ConceptMap.gorup.element.target.property to org.linuxforhealth.fhir.model.r5.type.Element
 */

/**
 * A statement of relationships from one set of concepts to one or more other concepts - either concepts in code systems, 
 * or data element/data element concepts, or classes in class models.
 * 
 * <p>Maturity level: FMM3 (Trial Use)
 */
@Maturity(
    level = 3,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "cnl-0",
    level = "Warning",
    location = "(base)",
    description = "Name should be usable as an identifier for the module by machine processing applications such as code generation",
    expression = "name.exists() implies name.matches('^[A-Z]([A-Za-z0-9_]){1,254}$')",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "ConceptMap.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-1",
    level = "Rule",
    location = "ConceptMap.group.element.target",
    description = "If the map is source-is-broader-than-target or not-related-to, there SHALL be some comments, unless the status is 'draft'",
    expression = "comment.exists() or (%resource.status = 'draft') or relationship.empty() or ((relationship != 'source-is-broader-than-target') and (relationship != 'not-related-to'))",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-2",
    level = "Rule",
    location = "ConceptMap.group.unmapped",
    description = "If the mode is 'fixed', either a code or valueSet must be provided, but not both.",
    expression = "(mode = 'fixed') implies ((code.exists() and valueSet.empty()) or (code.empty() and valueSet.exists()))",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-3",
    level = "Rule",
    location = "ConceptMap.group.unmapped",
    description = "If the mode is 'other-map', a url for the other map must be provided",
    expression = "(mode = 'other-map') implies otherMap.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-4",
    level = "Rule",
    location = "ConceptMap.group.element",
    description = "If noMap is present, target SHALL NOT be present",
    expression = "(noMap.exists() and noMap=true) implies target.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-5",
    level = "Rule",
    location = "ConceptMap.group.element",
    description = "Either code or valueSet SHALL be present but not both.",
    expression = "(code.exists() and valueSet.empty()) or (code.empty() and valueSet.exists())",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-6",
    level = "Rule",
    location = "ConceptMap.group.element.target.dependsOn",
    description = "One of value[x] or valueSet must exist, but not both.",
    expression = "(value.exists() and valueSet.empty()) or (value.empty() and valueSet.exists())",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-7",
    level = "Rule",
    location = "ConceptMap.group.element.target",
    description = "Either code or valueSet SHALL be present but not both.",
    expression = "(code.exists() and valueSet.empty()) or (code.empty() and valueSet.exists())",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-8",
    level = "Rule",
    location = "ConceptMap.group.unmapped",
    description = "If the mode is not 'fixed', code, display and valueSet are not allowed",
    expression = "(mode != 'fixed') implies (code.empty() and display.empty() and valueSet.empty())",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-9",
    level = "Rule",
    location = "ConceptMap.group.unmapped",
    description = "If the mode is not 'other-map', relationship must be provided",
    expression = "(mode != 'other-map') implies relationship.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-10",
    level = "Rule",
    location = "ConceptMap.group.unmapped",
    description = "If the mode is not 'other-map', otherMap is not allowed",
    expression = "(mode != 'other-map') implies otherMap.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "cmd-11",
    level = "Rule",
    location = "ConceptMap.property",
    description = "If the property type is code, a system SHALL be specified",
    expression = "type = 'code' implies system.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap"
)
@Constraint(
    id = "conceptMap-12",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap",
    generated = true
)
@Constraint(
    id = "conceptMap-13",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/ConceptMap",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ConceptMap extends MetadataResource {
    @Summary
    private final List<Property> property;
    @Summary
    private final List<AdditionalAttribute> additionalAttribute;
    @Summary
    @Choice({ Uri.class, Canonical.class })
    private final Element sourceScope;
    @Summary
    @Choice({ Uri.class, Canonical.class })
    private final Element targetScope;
    private final List<Group> group;

    private ConceptMap(Builder builder) {
        super(builder);
        property = Collections.unmodifiableList(builder.property);
        additionalAttribute = Collections.unmodifiableList(builder.additionalAttribute);
        sourceScope = builder.sourceScope;
        targetScope = builder.targetScope;
        group = Collections.unmodifiableList(builder.group);
    }

    /**
     * A property defines a slot through which additional information can be provided about a map from source -&gt; target.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
     */
    public List<Property> getProperty() {
        return property;
    }

    /**
     * An additionalAttribute defines an additional data element found in the source or target data model where the data will 
     * come from or be mapped to. Some mappings are based on data in addition to the source data element, where codes in 
     * multiple fields are combined to a single field (or vice versa).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link AdditionalAttribute} that may be empty.
     */
    public List<AdditionalAttribute> getAdditionalAttribute() {
        return additionalAttribute;
    }

    /**
     * Identifier for the source value set that contains the concepts that are being mapped and provides context for the 
     * mappings. Limits the scope of the map to source codes (ConceptMap.group.element code or valueSet) that are members of 
     * this value set.
     * 
     * @return
     *     An immutable object of type {@link Uri} or {@link Canonical} that may be null.
     */
    public Element getSourceScope() {
        return sourceScope;
    }

    /**
     * Identifier for the target value set that provides important context about how the mapping choices are made. Limits the 
     * scope of the map to target codes (ConceptMap.group.element.target code or valueSet) that are members of this value set.
     * 
     * @return
     *     An immutable object of type {@link Uri} or {@link Canonical} that may be null.
     */
    public Element getTargetScope() {
        return targetScope;
    }

    /**
     * A group of mappings that all have the same source and target system.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Group} that may be empty.
     */
    public List<Group> getGroup() {
        return group;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !property.isEmpty() || 
            !additionalAttribute.isEmpty() || 
            (sourceScope != null) || 
            (targetScope != null) || 
            !group.isEmpty();
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
                accept(topic, "topic", visitor, CodeableConcept.class);
                accept(author, "author", visitor, ContactDetail.class);
                accept(editor, "editor", visitor, ContactDetail.class);
                accept(reviewer, "reviewer", visitor, ContactDetail.class);
                accept(endorser, "endorser", visitor, ContactDetail.class);
                accept(relatedArtifact, "relatedArtifact", visitor, RelatedArtifact.class);
                accept(property, "property", visitor, Property.class);
                accept(additionalAttribute, "additionalAttribute", visitor, AdditionalAttribute.class);
                accept(sourceScope, "sourceScope", visitor);
                accept(targetScope, "targetScope", visitor);
                accept(group, "group", visitor, Group.class);
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
        ConceptMap other = (ConceptMap) obj;
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
            Objects.equals(topic, other.topic) && 
            Objects.equals(author, other.author) && 
            Objects.equals(editor, other.editor) && 
            Objects.equals(reviewer, other.reviewer) && 
            Objects.equals(endorser, other.endorser) && 
            Objects.equals(relatedArtifact, other.relatedArtifact) && 
            Objects.equals(property, other.property) && 
            Objects.equals(additionalAttribute, other.additionalAttribute) && 
            Objects.equals(sourceScope, other.sourceScope) && 
            Objects.equals(targetScope, other.targetScope) && 
            Objects.equals(group, other.group);
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
                topic, 
                author, 
                editor, 
                reviewer, 
                endorser, 
                relatedArtifact, 
                property, 
                additionalAttribute, 
                sourceScope, 
                targetScope, 
                group);
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

    public static class Builder extends MetadataResource.Builder {
        private List<Property> property = new ArrayList<>();
        private List<AdditionalAttribute> additionalAttribute = new ArrayList<>();
        private Element sourceScope;
        private Element targetScope;
        private List<Group> group = new ArrayList<>();

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
         * An absolute URI that is used to identify this code system when it is referenced in a specification, model, design or 
         * an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
         * which an authoritative instance of this code system is (or will be) published. This URL can be the target of a 
         * canonical reference. It SHALL remain the same when the code system is stored on different servers. This is used in 
         * [Coding](datatypes.html#Coding).system.
         * 
         * @param url
         *     Canonical identifier for this code system, represented as a URI (globally unique) (Coding.system)
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder url(Uri url) {
            return (Builder) super.url(url);
        }

        /**
         * A formal identifier that is used to identify this code system when it is represented in other formats, or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the code system (business identifier)
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder identifier(Identifier... identifier) {
            return (Builder) super.identifier(identifier);
        }

        /**
         * A formal identifier that is used to identify this code system when it is represented in other formats, or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the code system (business identifier)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder identifier(Collection<Identifier> identifier) {
            return (Builder) super.identifier(identifier);
        }

        /**
         * Convenience method for setting {@code version}.
         * 
         * @param version
         *     Business version of the code system (Coding.version)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #version(org.linuxforhealth.fhir.model.type.String)
         */
        @Override
        public Builder version(java.lang.String version) {
            return (Builder) super.version(version);
        }

        /**
         * The identifier that is used to identify this version of the code system when it is referenced in a specification, 
         * model, design or instance. This is an arbitrary value managed by the code system author and is not expected to be 
         * globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is 
         * also no expectation that versions can be placed in a lexicographical sequence. This is used in [Coding](datatypes.
         * html#Coding).version.
         * 
         * @param version
         *     Business version of the code system (Coding.version)
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder version(String version) {
            return (Builder) super.version(version);
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
        @Override
        public Builder versionAlgorithm(java.lang.String versionAlgorithm) {
            return (Builder) super.versionAlgorithm(versionAlgorithm);
        }

        /**
         * Indicates the mechanism used to compare versions to determine which CodeSystem is more current.
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
        @Override
        public Builder versionAlgorithm(Element versionAlgorithm) {
            return (Builder) super.versionAlgorithm(versionAlgorithm);
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Name for this code system (computer friendly)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #name(org.linuxforhealth.fhir.model.type.String)
         */
        @Override
        public Builder name(java.lang.String name) {
            return (Builder) super.name(name);
        }

        /**
         * A natural language name identifying the code system. This name should be usable as an identifier for the module by 
         * machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this code system (computer friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder name(String name) {
            return (Builder) super.name(name);
        }

        /**
         * Convenience method for setting {@code title}.
         * 
         * @param title
         *     Name for this code system (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #title(org.linuxforhealth.fhir.model.type.String)
         */
        @Override
        public Builder title(java.lang.String title) {
            return (Builder) super.title(title);
        }

        /**
         * A short, descriptive, user-friendly title for the code system.
         * 
         * @param title
         *     Name for this code system (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder title(String title) {
            return (Builder) super.title(title);
        }

        /**
         * The status of this code system. Enables tracking the life-cycle of the content.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | retired | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder status(PublicationStatus status) {
            return (Builder) super.status(status);
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
        @Override
        public Builder experimental(java.lang.Boolean experimental) {
            return (Builder) super.experimental(experimental);
        }

        /**
         * A Boolean value to indicate that this code system is authored for testing purposes (or education/evaluation/marketing) 
         * and is not intended to be used for genuine usage.
         * 
         * @param experimental
         *     For testing purposes, not real usage
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder experimental(Boolean experimental) {
            return (Builder) super.experimental(experimental);
        }

        /**
         * The date (and optionally time) when the code system was last significantly changed. The date must change when the 
         * business version changes and it must change if the status code changes. In addition, it should change when the 
         * substantive content of the code system changes.
         * 
         * @param date
         *     Date last changed
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder date(DateTime date) {
            return (Builder) super.date(date);
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
        @Override
        public Builder publisher(java.lang.String publisher) {
            return (Builder) super.publisher(publisher);
        }

        /**
         * The name of the organization or individual responsible for the release and ongoing maintenance of the code system.
         * 
         * @param publisher
         *     Name of the publisher/steward (organization or individual)
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder publisher(String publisher) {
            return (Builder) super.publisher(publisher);
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
        @Override
        public Builder contact(ContactDetail... contact) {
        	return (Builder) super.contact(contact);
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
        @Override
        public Builder contact(Collection<ContactDetail> contact) {
            return (Builder) super.contact(contact);
        }

        /**
         * A free text natural language description of the code system from a consumer's perspective.
         * 
         * @param description
         *     Natural language description of the code system
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder description(Markdown description) {
            return (Builder) super.description(description);
        }

        /**
         * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
         * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
         * may be used to assist with indexing and searching for appropriate code system instances.
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
        @Override
        public Builder useContext(UsageContext... useContext) {
        	return (Builder) super.useContext(useContext);
        }

        /**
         * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
         * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
         * may be used to assist with indexing and searching for appropriate code system instances.
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
        @Override
        public Builder useContext(Collection<UsageContext> useContext) {
            return (Builder) super.useContext(useContext);
        }

        /**
         * A legal or geographic region in which the code system is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for code system (if applicable)
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder jurisdiction(CodeableConcept... jurisdiction) {
        	return (Builder) super.jurisdiction(jurisdiction);
        }

        /**
         * A legal or geographic region in which the code system is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for code system (if applicable)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder jurisdiction(Collection<CodeableConcept> jurisdiction) {
            return (Builder) super.jurisdiction(jurisdiction);
        }

        /**
         * Explanation of why this code system is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this code system is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder purpose(Markdown purpose) {
            return (Builder) super.purpose(purpose);
        }

        /**
         * A copyright statement relating to the code system and/or its contents. Copyright statements are generally legal 
         * restrictions on the use and publishing of the code system.
         * 
         * @param copyright
         *     Use and/or publishing restrictions
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder copyright(Markdown copyright) {
            return (Builder) super.copyright(copyright);
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
        @Override
        public Builder copyrightLabel(java.lang.String copyrightLabel) {
            return (Builder) super.copyrightLabel(copyrightLabel);
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
        @Override
        public Builder copyrightLabel(String copyrightLabel) {
            return (Builder) super.copyrightLabel(copyrightLabel);
        }

        /**
         * Convenience method for setting {@code approvalDate}.
         * 
         * @param approvalDate
         *     When the CodeSystem was approved by publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #approvalDate(org.linuxforhealth.fhir.model.type.Date)
         */
        @Override
        public Builder approvalDate(java.time.LocalDate approvalDate) {
            return (Builder) super.approvalDate(approvalDate);
        }

        /**
         * The date on which the resource content was approved by the publisher. Approval happens once when the content is 
         * officially approved for usage.
         * 
         * @param approvalDate
         *     When the CodeSystem was approved by publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder approvalDate(Date approvalDate) {
            return (Builder) super.approvalDate(approvalDate);
        }

        /**
         * Convenience method for setting {@code lastReviewDate}.
         * 
         * @param lastReviewDate
         *     When the CodeSystem was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #lastReviewDate(org.linuxforhealth.fhir.model.type.Date)
         */
        @Override
        public Builder lastReviewDate(java.time.LocalDate lastReviewDate) {
            return (Builder) super.lastReviewDate(lastReviewDate);
        }

        /**
         * The date on which the resource content was last reviewed. Review happens periodically after approval but does not 
         * change the original approval date.
         * 
         * @param lastReviewDate
         *     When the CodeSystem was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder lastReviewDate(Date lastReviewDate) {
            return (Builder) super.lastReviewDate(lastReviewDate);
        }

        /**
         * The period during which the CodeSystem content was or is planned to be in active use.
         * 
         * @param effectivePeriod
         *     When the CodeSystem is expected to be used
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder effectivePeriod(Period effectivePeriod) {
            return (Builder) super.effectivePeriod(effectivePeriod);
        }

        /**
         * Descriptions related to the content of the CodeSystem. Topics provide a high-level categorization as well as keywords 
         * for the CodeSystem that can be useful for filtering and searching.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param topic
         *     E.g. Education, Treatment, Assessment, etc
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder topic(CodeableConcept... topic) {
        	return (Builder) super.topic(topic);
        }

        /**
         * Descriptions related to the content of the CodeSystem. Topics provide a high-level categorization as well as keywords 
         * for the CodeSystem that can be useful for filtering and searching.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param topic
         *     E.g. Education, Treatment, Assessment, etc
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder topic(Collection<CodeableConcept> topic) {
            return (Builder) super.topic(topic);
        }

        /**
         * An individiual or organization primarily involved in the creation and maintenance of the CodeSystem.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param author
         *     Who authored the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder author(ContactDetail... author) {
        	return (Builder) super.author(author);
        }

        /**
         * An individiual or organization primarily involved in the creation and maintenance of the CodeSystem.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param author
         *     Who authored the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder author(Collection<ContactDetail> author) {
            return (Builder) super.author(author);
        }

        /**
         * An individual or organization primarily responsible for internal coherence of the CodeSystem.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param editor
         *     Who edited the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder editor(ContactDetail... editor) {
        	return (Builder) super.editor(editor);
        }

        /**
         * An individual or organization primarily responsible for internal coherence of the CodeSystem.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param editor
         *     Who edited the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder editor(Collection<ContactDetail> editor) {
            return (Builder) super.editor(editor);
        }

        /**
         * An individual or organization asserted by the publisher to be primarily responsible for review of some aspect of the 
         * CodeSystem.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reviewer
         *     Who reviewed the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder reviewer(ContactDetail... reviewer) {
        	return (Builder) super.reviewer(reviewer);
        }

        /**
         * An individual or organization asserted by the publisher to be primarily responsible for review of some aspect of the 
         * CodeSystem.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reviewer
         *     Who reviewed the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder reviewer(Collection<ContactDetail> reviewer) {
            return (Builder) super.reviewer(reviewer);
        }

        /**
         * An individual or organization asserted by the publisher to be responsible for officially endorsing the CodeSystem for 
         * use in some setting.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param endorser
         *     Who endorsed the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder endorser(ContactDetail... endorser) {
        	return (Builder) super.endorser(endorser);
        }

        /**
         * An individual or organization asserted by the publisher to be responsible for officially endorsing the CodeSystem for 
         * use in some setting.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param endorser
         *     Who endorsed the CodeSystem
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder endorser(Collection<ContactDetail> endorser) {
            return (Builder) super.endorser(endorser);
        }

        /**
         * Related artifacts such as additional documentation, justification, dependencies, bibliographic references, and 
         * predecessor and successor artifacts.
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
        @Override
        public Builder relatedArtifact(RelatedArtifact... relatedArtifact) {
        	return (Builder) super.relatedArtifact(relatedArtifact);
        }

        /**
         * Related artifacts such as additional documentation, justification, dependencies, bibliographic references, and 
         * predecessor and successor artifacts.
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
        @Override
        public Builder relatedArtifact(Collection<RelatedArtifact> relatedArtifact) {
            return (Builder) super.relatedArtifact(relatedArtifact);
        }

        /**
         * A property defines a slot through which additional information can be provided about a map from source -&gt; target.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Additional properties of the mapping
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder property(Property... property) {
            for (Property value : property) {
                this.property.add(value);
            }
            return this;
        }

        /**
         * A property defines a slot through which additional information can be provided about a map from source -&gt; target.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Additional properties of the mapping
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder property(Collection<Property> property) {
            this.property = new ArrayList<>(property);
            return this;
        }

        /**
         * An additionalAttribute defines an additional data element found in the source or target data model where the data will 
         * come from or be mapped to. Some mappings are based on data in addition to the source data element, where codes in 
         * multiple fields are combined to a single field (or vice versa).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param additionalAttribute
         *     Definition of an additional attribute to act as a data source or target
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder additionalAttribute(AdditionalAttribute... additionalAttribute) {
            for (AdditionalAttribute value : additionalAttribute) {
                this.additionalAttribute.add(value);
            }
            return this;
        }

        /**
         * An additionalAttribute defines an additional data element found in the source or target data model where the data will 
         * come from or be mapped to. Some mappings are based on data in addition to the source data element, where codes in 
         * multiple fields are combined to a single field (or vice versa).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param additionalAttribute
         *     Definition of an additional attribute to act as a data source or target
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder additionalAttribute(Collection<AdditionalAttribute> additionalAttribute) {
            this.additionalAttribute = new ArrayList<>(additionalAttribute);
            return this;
        }

        /**
         * Identifier for the source value set that contains the concepts that are being mapped and provides context for the 
         * mappings. Limits the scope of the map to source codes (ConceptMap.group.element code or valueSet) that are members of 
         * this value set.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Uri}</li>
         * <li>{@link Canonical}</li>
         * </ul>
         * 
         * @param sourceScope
         *     The source value set that contains the concepts that are being mapped
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder sourceScope(Element sourceScope) {
            this.sourceScope = sourceScope;
            return this;
        }

        /**
         * Identifier for the target value set that provides important context about how the mapping choices are made. Limits the 
         * scope of the map to target codes (ConceptMap.group.element.target code or valueSet) that are members of this value set.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Uri}</li>
         * <li>{@link Canonical}</li>
         * </ul>
         * 
         * @param targetScope
         *     The target value set which provides context for the mappings
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder targetScope(Element targetScope) {
            this.targetScope = targetScope;
            return this;
        }

        /**
         * A group of mappings that all have the same source and target system.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param group
         *     Same source and target systems
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
         * A group of mappings that all have the same source and target system.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param group
         *     Same source and target systems
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
         * Build the {@link ConceptMap}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ConceptMap}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ConceptMap per the base specification
         */
        @Override
        public ConceptMap build() {
            ConceptMap conceptMap = new ConceptMap(this);
            if (validating) {
                validate(conceptMap);
            }
            return conceptMap;
        }

        protected void validate(ConceptMap conceptMap) {
            super.validate(conceptMap);
            ValidationSupport.checkList(conceptMap.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(conceptMap.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(conceptMap.status, "status");
            ValidationSupport.checkList(conceptMap.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(conceptMap.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(conceptMap.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(conceptMap.topic, "topic", CodeableConcept.class);
            ValidationSupport.checkList(conceptMap.author, "author", ContactDetail.class);
            ValidationSupport.checkList(conceptMap.editor, "editor", ContactDetail.class);
            ValidationSupport.checkList(conceptMap.reviewer, "reviewer", ContactDetail.class);
            ValidationSupport.checkList(conceptMap.endorser, "endorser", ContactDetail.class);
            ValidationSupport.checkList(conceptMap.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
            ValidationSupport.checkList(conceptMap.property, "property", Property.class);
            ValidationSupport.checkList(conceptMap.additionalAttribute, "additionalAttribute", AdditionalAttribute.class);
            ValidationSupport.choiceElement(conceptMap.sourceScope, "sourceScope", Uri.class, Canonical.class);
            ValidationSupport.choiceElement(conceptMap.targetScope, "targetScope", Uri.class, Canonical.class);
            ValidationSupport.checkList(conceptMap.group, "group", Group.class);
        }

        protected Builder from(ConceptMap conceptMap) {
            super.from(conceptMap);
            property.addAll(conceptMap.property);
            additionalAttribute.addAll(conceptMap.additionalAttribute);
            sourceScope = conceptMap.sourceScope;
            targetScope = conceptMap.targetScope;
            group.addAll(conceptMap.group);
            return this;
        }
    }

    /**
     * A property defines a slot through which additional information can be provided about a map from source -&gt; target.
     */
    public static class Property extends BackboneElement {
        @Summary
        @Required
        private final Code code;
        @Summary
        private final Uri uri;
        @Summary
        private final String description;
        @Summary
        @Binding(
            bindingName = "PropertyType",
            strength = BindingStrength.Value.REQUIRED,
            description = "The type of a property value.",
            valueSet = "http://hl7.org/fhir/ValueSet/conceptmap-property-type|5.0.0"
        )
        @Required
        private final PropertyType type;
        @Summary
        private final Canonical system;

        private Property(Builder builder) {
            super(builder);
            code = builder.code;
            uri = builder.uri;
            description = builder.description;
            type = builder.type;
            system = builder.system;
        }

        /**
         * A code that is used to identify the property. The code is used internally (in ConceptMap.group.element.target.property.
         * code) and also in the $translate operation.
         * 
         * @return
         *     An immutable object of type {@link Code} that is non-null.
         */
        public Code getCode() {
            return code;
        }

        /**
         * Reference to the formal meaning of the property.
         * 
         * @return
         *     An immutable object of type {@link Uri} that may be null.
         */
        public Uri getUri() {
            return uri;
        }

        /**
         * A description of the property - why it is defined, and how its value might be used.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDescription() {
            return description;
        }

        /**
         * The type of the property value.
         * 
         * @return
         *     An immutable object of type {@link PropertyType} that is non-null.
         */
        public PropertyType getType() {
            return type;
        }

        /**
         * The CodeSystem that defines the codes from which values of type ```code``` in property values.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getSystem() {
            return system;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (uri != null) || 
                (description != null) || 
                (type != null) || 
                (system != null);
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
                    accept(uri, "uri", visitor);
                    accept(description, "description", visitor);
                    accept(type, "type", visitor);
                    accept(system, "system", visitor);
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
            Property other = (Property) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(uri, other.uri) && 
                Objects.equals(description, other.description) && 
                Objects.equals(type, other.type) && 
                Objects.equals(system, other.system);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    uri, 
                    description, 
                    type, 
                    system);
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
            private Code code;
            private Uri uri;
            private String description;
            private PropertyType type;
            private Canonical system;

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
             * A code that is used to identify the property. The code is used internally (in ConceptMap.group.element.target.property.
             * code) and also in the $translate operation.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Identifies the property on the mappings, and when referred to in the $translate operation
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(Code code) {
                this.code = code;
                return this;
            }

            /**
             * Reference to the formal meaning of the property.
             * 
             * @param uri
             *     Formal identifier for the property
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder uri(Uri uri) {
                this.uri = uri;
                return this;
            }

            /**
             * Convenience method for setting {@code description}.
             * 
             * @param description
             *     Why the property is defined, and/or what it conveys
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #description(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder description(java.lang.String description) {
                this.description = (description == null) ? null : String.of(description);
                return this;
            }

            /**
             * A description of the property - why it is defined, and how its value might be used.
             * 
             * @param description
             *     Why the property is defined, and/or what it conveys
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * The type of the property value.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     Coding | string | integer | boolean | dateTime | decimal | code
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(PropertyType type) {
                this.type = type;
                return this;
            }

            /**
             * The CodeSystem that defines the codes from which values of type ```code``` in property values.
             * 
             * @param system
             *     The CodeSystem from which code values come
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder system(Canonical system) {
                this.system = system;
                return this;
            }

            /**
             * Build the {@link Property}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Property}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Property per the base specification
             */
            @Override
            public Property build() {
                Property property = new Property(this);
                if (validating) {
                    validate(property);
                }
                return property;
            }

            protected void validate(Property property) {
                super.validate(property);
                ValidationSupport.requireNonNull(property.code, "code");
                ValidationSupport.requireNonNull(property.type, "type");
                ValidationSupport.requireValueOrChildren(property);
            }

            protected Builder from(Property property) {
                super.from(property);
                code = property.code;
                uri = property.uri;
                description = property.description;
                type = property.type;
                system = property.system;
                return this;
            }
        }
    }

    /**
     * An additionalAttribute defines an additional data element found in the source or target data model where the data will 
     * come from or be mapped to. Some mappings are based on data in addition to the source data element, where codes in 
     * multiple fields are combined to a single field (or vice versa).
     */
    public static class AdditionalAttribute extends BackboneElement {
        @Summary
        @Required
        private final Code code;
        @Summary
        private final Uri uri;
        @Summary
        private final String description;
        @Summary
        @Binding(
            bindingName = "ConceptMapmapAttributeType",
            strength = BindingStrength.Value.REQUIRED,
            description = "The type of a mapping attribute value.",
            valueSet = "http://hl7.org/fhir/ValueSet/conceptmap-attribute-type|5.0.0"
        )
        @Required
        private final ConceptMapmapAttributeType type;

        private AdditionalAttribute(Builder builder) {
            super(builder);
            code = builder.code;
            uri = builder.uri;
            description = builder.description;
            type = builder.type;
        }

        /**
         * A code that is used to identify this additional data attribute. The code is used internally in ConceptMap.group.
         * element.target.dependsOn.attribute and ConceptMap.group.element.target.product.attribute.
         * 
         * @return
         *     An immutable object of type {@link Code} that is non-null.
         */
        public Code getCode() {
            return code;
        }

        /**
         * Reference to the formal definition of the source/target data element. For elements defined by the FHIR specification, 
         * or using a FHIR logical model, the correct format is {canonical-url}#{element-id}.
         * 
         * @return
         *     An immutable object of type {@link Uri} that may be null.
         */
        public Uri getUri() {
            return uri;
        }

        /**
         * A description of the additional attribute and/or the data element it refers to - why it is defined, and how the value 
         * might be used in mappings, and a discussion of issues associated with the use of the data element.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDescription() {
            return description;
        }

        /**
         * The type of the source data contained in this concept map for this data element.
         * 
         * @return
         *     An immutable object of type {@link ConceptMapmapAttributeType} that is non-null.
         */
        public ConceptMapmapAttributeType getType() {
            return type;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (uri != null) || 
                (description != null) || 
                (type != null);
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
                    accept(uri, "uri", visitor);
                    accept(description, "description", visitor);
                    accept(type, "type", visitor);
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
            AdditionalAttribute other = (AdditionalAttribute) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(uri, other.uri) && 
                Objects.equals(description, other.description) && 
                Objects.equals(type, other.type);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    uri, 
                    description, 
                    type);
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
            private Code code;
            private Uri uri;
            private String description;
            private ConceptMapmapAttributeType type;

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
             * A code that is used to identify this additional data attribute. The code is used internally in ConceptMap.group.
             * element.target.dependsOn.attribute and ConceptMap.group.element.target.product.attribute.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Identifies this additional attribute through this resource
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(Code code) {
                this.code = code;
                return this;
            }

            /**
             * Reference to the formal definition of the source/target data element. For elements defined by the FHIR specification, 
             * or using a FHIR logical model, the correct format is {canonical-url}#{element-id}.
             * 
             * @param uri
             *     Formal identifier for the data element referred to in this attribte
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder uri(Uri uri) {
                this.uri = uri;
                return this;
            }

            /**
             * Convenience method for setting {@code description}.
             * 
             * @param description
             *     Why the additional attribute is defined, and/or what the data element it refers to is
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #description(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder description(java.lang.String description) {
                this.description = (description == null) ? null : String.of(description);
                return this;
            }

            /**
             * A description of the additional attribute and/or the data element it refers to - why it is defined, and how the value 
             * might be used in mappings, and a discussion of issues associated with the use of the data element.
             * 
             * @param description
             *     Why the additional attribute is defined, and/or what the data element it refers to is
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * The type of the source data contained in this concept map for this data element.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     code | Coding | string | boolean | Quantity
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(ConceptMapmapAttributeType type) {
                this.type = type;
                return this;
            }

            /**
             * Build the {@link AdditionalAttribute}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * <li>type</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link AdditionalAttribute}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid AdditionalAttribute per the base specification
             */
            @Override
            public AdditionalAttribute build() {
                AdditionalAttribute additionalAttribute = new AdditionalAttribute(this);
                if (validating) {
                    validate(additionalAttribute);
                }
                return additionalAttribute;
            }

            protected void validate(AdditionalAttribute additionalAttribute) {
                super.validate(additionalAttribute);
                ValidationSupport.requireNonNull(additionalAttribute.code, "code");
                ValidationSupport.requireNonNull(additionalAttribute.type, "type");
                ValidationSupport.requireValueOrChildren(additionalAttribute);
            }

            protected Builder from(AdditionalAttribute additionalAttribute) {
                super.from(additionalAttribute);
                code = additionalAttribute.code;
                uri = additionalAttribute.uri;
                description = additionalAttribute.description;
                type = additionalAttribute.type;
                return this;
            }
        }
    }

    /**
     * A group of mappings that all have the same source and target system.
     */
    public static class Group extends BackboneElement {
        private final Canonical source;
        private final Canonical target;
        @Required
        private final List<Element> element;
        private final Unmapped unmapped;

        private Group(Builder builder) {
            super(builder);
            source = builder.source;
            target = builder.target;
            element = Collections.unmodifiableList(builder.element);
            unmapped = builder.unmapped;
        }

        /**
         * An absolute URI that identifies the source system where the concepts to be mapped are defined.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getSource() {
            return source;
        }

        /**
         * An absolute URI that identifies the target system that the concepts will be mapped to.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getTarget() {
            return target;
        }

        /**
         * Mappings for an individual concept in the source to one or more concepts in the target.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Element} that is non-empty.
         */
        public List<Element> getElement() {
            return element;
        }

        /**
         * What to do when there is no mapping to a target concept from the source concept and ConceptMap.group.element.noMap is 
         * not true. This provides the "default" to be applied when there is no target concept mapping specified or the expansion 
         * of ConceptMap.group.element.target.valueSet is empty.
         * 
         * @return
         *     An immutable object of type {@link Unmapped} that may be null.
         */
        public Unmapped getUnmapped() {
            return unmapped;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (source != null) || 
                (target != null) || 
                !element.isEmpty() || 
                (unmapped != null);
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
                    accept(source, "source", visitor);
                    accept(target, "target", visitor);
                    accept(element, "element", visitor, Element.class);
                    accept(unmapped, "unmapped", visitor);
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
                Objects.equals(source, other.source) && 
                Objects.equals(target, other.target) && 
                Objects.equals(element, other.element) && 
                Objects.equals(unmapped, other.unmapped);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    source, 
                    target, 
                    element, 
                    unmapped);
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
            private Canonical source;
            private Canonical target;
            private List<Element> element = new ArrayList<>();
            private Unmapped unmapped;

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
             * An absolute URI that identifies the source system where the concepts to be mapped are defined.
             * 
             * @param source
             *     Source system where concepts to be mapped are defined
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder source(Canonical source) {
                this.source = source;
                return this;
            }

            /**
             * An absolute URI that identifies the target system that the concepts will be mapped to.
             * 
             * @param target
             *     Target system that the concepts are to be mapped to
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder target(Canonical target) {
                this.target = target;
                return this;
            }

            /**
             * Mappings for an individual concept in the source to one or more concepts in the target.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param element
             *     Mappings for a concept from the source set
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder element(Element... element) {
                for (Element value : element) {
                    this.element.add(value);
                }
                return this;
            }

            /**
             * Mappings for an individual concept in the source to one or more concepts in the target.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param element
             *     Mappings for a concept from the source set
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder element(Collection<Element> element) {
                this.element = new ArrayList<>(element);
                return this;
            }

            /**
             * What to do when there is no mapping to a target concept from the source concept and ConceptMap.group.element.noMap is 
             * not true. This provides the "default" to be applied when there is no target concept mapping specified or the expansion 
             * of ConceptMap.group.element.target.valueSet is empty.
             * 
             * @param unmapped
             *     What to do when there is no mapping target for the source concept and ConceptMap.group.element.noMap is not true
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder unmapped(Unmapped unmapped) {
                this.unmapped = unmapped;
                return this;
            }

            /**
             * Build the {@link Group}
             * 
             * <p>Required elements:
             * <ul>
             * <li>element</li>
             * </ul>
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
                ValidationSupport.checkNonEmptyList(group.element, "element", Element.class);
                ValidationSupport.requireValueOrChildren(group);
            }

            protected Builder from(Group group) {
                super.from(group);
                source = group.source;
                target = group.target;
                element.addAll(group.element);
                unmapped = group.unmapped;
                return this;
            }
        }

        /**
         * Mappings for an individual concept in the source to one or more concepts in the target.
         */
        public static class Element extends BackboneElement {
            private final Code code;
            private final String display;
            private final Canonical valueSet;
            private final Boolean noMap;
            private final List<Target> target;

            private Element(Builder builder) {
                super(builder);
                code = builder.code;
                display = builder.display;
                valueSet = builder.valueSet;
                noMap = builder.noMap;
                target = Collections.unmodifiableList(builder.target);
            }

            /**
             * Identity (code or path) or the element/item being mapped.
             * 
             * @return
             *     An immutable object of type {@link Code} that may be null.
             */
            public Code getCode() {
                return code;
            }

            /**
             * The display for the code. The display is only provided to help editors when editing the concept map.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getDisplay() {
                return display;
            }

            /**
             * The set of concepts from the ConceptMap.group.source code system which are all being mapped to the target as part of 
             * this mapping rule.
             * 
             * @return
             *     An immutable object of type {@link Canonical} that may be null.
             */
            public Canonical getValueSet() {
                return valueSet;
            }

            /**
             * If noMap = true this indicates that no mapping to a target concept exists for this source concept.
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getNoMap() {
                return noMap;
            }

            /**
             * A concept from the target value set that this concept maps to.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Target} that may be empty.
             */
            public List<Target> getTarget() {
                return target;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (code != null) || 
                    (display != null) || 
                    (valueSet != null) || 
                    (noMap != null) || 
                    !target.isEmpty();
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
                        accept(display, "display", visitor);
                        accept(valueSet, "valueSet", visitor);
                        accept(noMap, "noMap", visitor);
                        accept(target, "target", visitor, Target.class);
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
                Element other = (Element) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(code, other.code) && 
                    Objects.equals(display, other.display) && 
                    Objects.equals(valueSet, other.valueSet) && 
                    Objects.equals(noMap, other.noMap) && 
                    Objects.equals(target, other.target);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        code, 
                        display, 
                        valueSet, 
                        noMap, 
                        target);
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
                private Code code;
                private String display;
                private Canonical valueSet;
                private Boolean noMap;
                private List<Target> target = new ArrayList<>();

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
                 * Identity (code or path) or the element/item being mapped.
                 * 
                 * @param code
                 *     Identifies element being mapped
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(Code code) {
                    this.code = code;
                    return this;
                }

                /**
                 * Convenience method for setting {@code display}.
                 * 
                 * @param display
                 *     Display for the code
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
                 * The display for the code. The display is only provided to help editors when editing the concept map.
                 * 
                 * @param display
                 *     Display for the code
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder display(String display) {
                    this.display = display;
                    return this;
                }

                /**
                 * The set of concepts from the ConceptMap.group.source code system which are all being mapped to the target as part of 
                 * this mapping rule.
                 * 
                 * @param valueSet
                 *     Identifies the set of concepts being mapped
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder valueSet(Canonical valueSet) {
                    this.valueSet = valueSet;
                    return this;
                }

                /**
                 * Convenience method for setting {@code noMap}.
                 * 
                 * @param noMap
                 *     No mapping to a target concept for this source concept
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #noMap(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder noMap(java.lang.Boolean noMap) {
                    this.noMap = (noMap == null) ? null : Boolean.of(noMap);
                    return this;
                }

                /**
                 * If noMap = true this indicates that no mapping to a target concept exists for this source concept.
                 * 
                 * @param noMap
                 *     No mapping to a target concept for this source concept
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder noMap(Boolean noMap) {
                    this.noMap = noMap;
                    return this;
                }

                /**
                 * A concept from the target value set that this concept maps to.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param target
                 *     Concept in target system for element
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder target(Target... target) {
                    for (Target value : target) {
                        this.target.add(value);
                    }
                    return this;
                }

                /**
                 * A concept from the target value set that this concept maps to.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param target
                 *     Concept in target system for element
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder target(Collection<Target> target) {
                    this.target = new ArrayList<>(target);
                    return this;
                }

                /**
                 * Build the {@link Element}
                 * 
                 * @return
                 *     An immutable object of type {@link Element}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Element per the base specification
                 */
                @Override
                public Element build() {
                    Element element = new Element(this);
                    if (validating) {
                        validate(element);
                    }
                    return element;
                }

                protected void validate(Element element) {
                    super.validate(element);
                    ValidationSupport.checkList(element.target, "target", Target.class);
                    ValidationSupport.requireValueOrChildren(element);
                }

                protected Builder from(Element element) {
                    super.from(element);
                    code = element.code;
                    display = element.display;
                    valueSet = element.valueSet;
                    noMap = element.noMap;
                    target.addAll(element.target);
                    return this;
                }
            }

            /**
             * A concept from the target value set that this concept maps to.
             */
            public static class Target extends BackboneElement {
                private final Code code;
                private final String display;
                private final Canonical valueSet;
                @Binding(
                    bindingName = "ConceptMapRelationship",
                    strength = BindingStrength.Value.REQUIRED,
                    description = "The relationship between concepts.",
                    valueSet = "http://hl7.org/fhir/ValueSet/concept-map-relationship|5.0.0"
                )
                @Required
                private final ConceptMapRelationship relationship;
                private final String comment;
                private final List<Property> property;
                private final List<DependsOn> dependsOn;
                private final List<ConceptMap.Group.Element.Target.DependsOn> product;

                private Target(Builder builder) {
                    super(builder);
                    code = builder.code;
                    display = builder.display;
                    valueSet = builder.valueSet;
                    relationship = builder.relationship;
                    comment = builder.comment;
                    property = Collections.unmodifiableList(builder.property);
                    dependsOn = Collections.unmodifiableList(builder.dependsOn);
                    product = Collections.unmodifiableList(builder.product);
                }

                /**
                 * Identity (code or path) or the element/item that the map refers to.
                 * 
                 * @return
                 *     An immutable object of type {@link Code} that may be null.
                 */
                public Code getCode() {
                    return code;
                }

                /**
                 * The display for the code. The display is only provided to help editors when editing the concept map.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getDisplay() {
                    return display;
                }

                /**
                 * The set of concepts from the ConceptMap.group.target code system which are all being mapped to as part of this mapping 
                 * rule. The effect of using this data element is the same as having multiple ConceptMap.group.element.target elements 
                 * with one for each concept in the ConceptMap.group.element.target.valueSet value set.
                 * 
                 * @return
                 *     An immutable object of type {@link Canonical} that may be null.
                 */
                public Canonical getValueSet() {
                    return valueSet;
                }

                /**
                 * The relationship between the source and target concepts. The relationship is read from source to target (e.g. source-
                 * is-narrower-than-target).
                 * 
                 * @return
                 *     An immutable object of type {@link ConceptMapRelationship} that is non-null.
                 */
                public ConceptMapRelationship getRelationship() {
                    return relationship;
                }

                /**
                 * A description of status/issues in mapping that conveys additional information not represented in the structured data.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getComment() {
                    return comment;
                }

                /**
                 * A property value for this source -&gt; target mapping.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
                 */
                public List<Property> getProperty() {
                    return property;
                }

                /**
                 * A set of additional dependencies for this mapping to hold. This mapping is only applicable if the specified data 
                 * attribute can be resolved, and it has the specified value.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link DependsOn} that may be empty.
                 */
                public List<DependsOn> getDependsOn() {
                    return dependsOn;
                }

                /**
                 * Product is the output of a ConceptMap that provides additional values that go in other attributes / data elemnts of 
                 * the target data.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link DependsOn} that may be empty.
                 */
                public List<ConceptMap.Group.Element.Target.DependsOn> getProduct() {
                    return product;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (code != null) || 
                        (display != null) || 
                        (valueSet != null) || 
                        (relationship != null) || 
                        (comment != null) || 
                        !property.isEmpty() || 
                        !dependsOn.isEmpty() || 
                        !product.isEmpty();
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
                            accept(display, "display", visitor);
                            accept(valueSet, "valueSet", visitor);
                            accept(relationship, "relationship", visitor);
                            accept(comment, "comment", visitor);
                            accept(property, "property", visitor, Property.class);
                            accept(dependsOn, "dependsOn", visitor, DependsOn.class);
                            accept(product, "product", visitor, ConceptMap.Group.Element.Target.DependsOn.class);
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
                    Target other = (Target) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(code, other.code) && 
                        Objects.equals(display, other.display) && 
                        Objects.equals(valueSet, other.valueSet) && 
                        Objects.equals(relationship, other.relationship) && 
                        Objects.equals(comment, other.comment) && 
                        Objects.equals(property, other.property) && 
                        Objects.equals(dependsOn, other.dependsOn) && 
                        Objects.equals(product, other.product);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            code, 
                            display, 
                            valueSet, 
                            relationship, 
                            comment, 
                            property, 
                            dependsOn, 
                            product);
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
                    private Code code;
                    private String display;
                    private Canonical valueSet;
                    private ConceptMapRelationship relationship;
                    private String comment;
                    private List<Property> property = new ArrayList<>();
                    private List<DependsOn> dependsOn = new ArrayList<>();
                    private List<ConceptMap.Group.Element.Target.DependsOn> product = new ArrayList<>();

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
                     * Identity (code or path) or the element/item that the map refers to.
                     * 
                     * @param code
                     *     Code that identifies the target element
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder code(Code code) {
                        this.code = code;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code display}.
                     * 
                     * @param display
                     *     Display for the code
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
                     * The display for the code. The display is only provided to help editors when editing the concept map.
                     * 
                     * @param display
                     *     Display for the code
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder display(String display) {
                        this.display = display;
                        return this;
                    }

                    /**
                     * The set of concepts from the ConceptMap.group.target code system which are all being mapped to as part of this mapping 
                     * rule. The effect of using this data element is the same as having multiple ConceptMap.group.element.target elements 
                     * with one for each concept in the ConceptMap.group.element.target.valueSet value set.
                     * 
                     * @param valueSet
                     *     Identifies the set of target concepts
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder valueSet(Canonical valueSet) {
                        this.valueSet = valueSet;
                        return this;
                    }

                    /**
                     * The relationship between the source and target concepts. The relationship is read from source to target (e.g. source-
                     * is-narrower-than-target).
                     * 
                     * <p>This element is required.
                     * 
                     * @param relationship
                     *     related-to | equivalent | source-is-narrower-than-target | source-is-broader-than-target | not-related-to
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder relationship(ConceptMapRelationship relationship) {
                        this.relationship = relationship;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code comment}.
                     * 
                     * @param comment
                     *     Description of status/issues in mapping
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #comment(org.linuxforhealth.fhir.model.type.String)
                     */
                    public Builder comment(java.lang.String comment) {
                        this.comment = (comment == null) ? null : String.of(comment);
                        return this;
                    }

                    /**
                     * A description of status/issues in mapping that conveys additional information not represented in the structured data.
                     * 
                     * @param comment
                     *     Description of status/issues in mapping
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder comment(String comment) {
                        this.comment = comment;
                        return this;
                    }

                    /**
                     * A property value for this source -&gt; target mapping.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param property
                     *     Property value for the source -&gt; target mapping
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder property(Property... property) {
                        for (Property value : property) {
                            this.property.add(value);
                        }
                        return this;
                    }

                    /**
                     * A property value for this source -&gt; target mapping.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param property
                     *     Property value for the source -&gt; target mapping
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder property(Collection<Property> property) {
                        this.property = new ArrayList<>(property);
                        return this;
                    }

                    /**
                     * A set of additional dependencies for this mapping to hold. This mapping is only applicable if the specified data 
                     * attribute can be resolved, and it has the specified value.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param dependsOn
                     *     Other properties required for this mapping
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder dependsOn(DependsOn... dependsOn) {
                        for (DependsOn value : dependsOn) {
                            this.dependsOn.add(value);
                        }
                        return this;
                    }

                    /**
                     * A set of additional dependencies for this mapping to hold. This mapping is only applicable if the specified data 
                     * attribute can be resolved, and it has the specified value.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param dependsOn
                     *     Other properties required for this mapping
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder dependsOn(Collection<DependsOn> dependsOn) {
                        this.dependsOn = new ArrayList<>(dependsOn);
                        return this;
                    }

                    /**
                     * Product is the output of a ConceptMap that provides additional values that go in other attributes / data elemnts of 
                     * the target data.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param product
                     *     Other data elements that this mapping also produces
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder product(ConceptMap.Group.Element.Target.DependsOn... product) {
                        for (ConceptMap.Group.Element.Target.DependsOn value : product) {
                            this.product.add(value);
                        }
                        return this;
                    }

                    /**
                     * Product is the output of a ConceptMap that provides additional values that go in other attributes / data elemnts of 
                     * the target data.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param product
                     *     Other data elements that this mapping also produces
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder product(Collection<ConceptMap.Group.Element.Target.DependsOn> product) {
                        this.product = new ArrayList<>(product);
                        return this;
                    }

                    /**
                     * Build the {@link Target}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>relationship</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Target}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Target per the base specification
                     */
                    @Override
                    public Target build() {
                        Target target = new Target(this);
                        if (validating) {
                            validate(target);
                        }
                        return target;
                    }

                    protected void validate(Target target) {
                        super.validate(target);
                        ValidationSupport.requireNonNull(target.relationship, "relationship");
                        ValidationSupport.checkList(target.property, "property", Property.class);
                        ValidationSupport.checkList(target.dependsOn, "dependsOn", DependsOn.class);
                        ValidationSupport.checkList(target.product, "product", ConceptMap.Group.Element.Target.DependsOn.class);
                        ValidationSupport.requireValueOrChildren(target);
                    }

                    protected Builder from(Target target) {
                        super.from(target);
                        code = target.code;
                        display = target.display;
                        valueSet = target.valueSet;
                        relationship = target.relationship;
                        comment = target.comment;
                        property.addAll(target.property);
                        dependsOn.addAll(target.dependsOn);
                        product.addAll(target.product);
                        return this;
                    }
                }

                /**
                 * A property value for this source -&gt; target mapping.
                 */
                public static class Property extends BackboneElement {
                    @Required
                    private final Code code;
                    @Choice({ Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class, Code.class })
                    @Required
                    private final org.linuxforhealth.fhir.model.r5.type.Element value;

                    private Property(Builder builder) {
                        super(builder);
                        code = builder.code;
                        value = builder.value;
                    }

                    /**
                     * A reference to a mapping property defined in ConceptMap.property.
                     * 
                     * @return
                     *     An immutable object of type {@link Code} that is non-null.
                     */
                    public Code getCode() {
                        return code;
                    }

                    /**
                     * The value of this property. If the type chosen for this element is 'code', then the property SHALL be defined in a 
                     * ConceptMap.property element.
                     * 
                     * @return
                     *     An immutable object of type {@link Coding}, {@link String}, {@link Integer}, {@link Boolean}, {@link DateTime}, {@link 
                     *     Decimal} or {@link Code} that is non-null.
                     */
                    public org.linuxforhealth.fhir.model.r5.type.Element getValue() {
                        return value;
                    }

                    @Override
                    public boolean hasChildren() {
                        return super.hasChildren() || 
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
                        Property other = (Property) obj;
                        return Objects.equals(id, other.id) && 
                            Objects.equals(extension, other.extension) && 
                            Objects.equals(modifierExtension, other.modifierExtension) && 
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
                        private Code code;
                        private org.linuxforhealth.fhir.model.r5.type.Element value;

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
                         * A reference to a mapping property defined in ConceptMap.property.
                         * 
                         * <p>This element is required.
                         * 
                         * @param code
                         *     Reference to ConceptMap.property.code
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder code(Code code) {
                            this.code = code;
                            return this;
                        }

                        /**
                         * Convenience method for setting {@code value} with choice type String.
                         * 
                         * <p>This element is required.
                         * 
                         * @param value
                         *     Value of the property for this concept
                         * 
                         * @return
                         *     A reference to this Builder instance
                         * 
                         * @see #value(Element)
                         */
                        public Builder value(java.lang.String value) {
                            this.value = (value == null) ? null : String.of(value);
                            return this;
                        }

                        /**
                         * Convenience method for setting {@code value} with choice type Integer.
                         * 
                         * <p>This element is required.
                         * 
                         * @param value
                         *     Value of the property for this concept
                         * 
                         * @return
                         *     A reference to this Builder instance
                         * 
                         * @see #value(Element)
                         */
                        public Builder value(java.lang.Integer value) {
                            this.value = (value == null) ? null : Integer.of(value);
                            return this;
                        }

                        /**
                         * Convenience method for setting {@code value} with choice type Boolean.
                         * 
                         * <p>This element is required.
                         * 
                         * @param value
                         *     Value of the property for this concept
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
                         * The value of this property. If the type chosen for this element is 'code', then the property SHALL be defined in a 
                         * ConceptMap.property element.
                         * 
                         * <p>This element is required.
                         * 
                         * <p>This is a choice element with the following allowed types:
                         * <ul>
                         * <li>{@link Coding}</li>
                         * <li>{@link String}</li>
                         * <li>{@link Integer}</li>
                         * <li>{@link Boolean}</li>
                         * <li>{@link DateTime}</li>
                         * <li>{@link Decimal}</li>
                         * <li>{@link Code}</li>
                         * </ul>
                         * 
                         * @param value
                         *     Value of the property for this concept
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder value(org.linuxforhealth.fhir.model.r5.type.Element value) {
                            this.value = value;
                            return this;
                        }

                        /**
                         * Build the {@link Property}
                         * 
                         * <p>Required elements:
                         * <ul>
                         * <li>code</li>
                         * <li>value</li>
                         * </ul>
                         * 
                         * @return
                         *     An immutable object of type {@link Property}
                         * @throws IllegalStateException
                         *     if the current state cannot be built into a valid Property per the base specification
                         */
                        @Override
                        public Property build() {
                            Property property = new Property(this);
                            if (validating) {
                                validate(property);
                            }
                            return property;
                        }

                        protected void validate(Property property) {
                            super.validate(property);
                            ValidationSupport.requireNonNull(property.code, "code");
                            ValidationSupport.requireChoiceElement(property.value, "value", Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class, Code.class);
                            ValidationSupport.requireValueOrChildren(property);
                        }

                        protected Builder from(Property property) {
                            super.from(property);
                            code = property.code;
                            value = property.value;
                            return this;
                        }
                    }
                }

                /**
                 * A set of additional dependencies for this mapping to hold. This mapping is only applicable if the specified data 
                 * attribute can be resolved, and it has the specified value.
                 */
                public static class DependsOn extends BackboneElement {
                    @Required
                    private final Code attribute;
                    @Choice({ Code.class, Coding.class, String.class, Boolean.class, Quantity.class })
                    private final org.linuxforhealth.fhir.model.r5.type.Element value;
                    private final Canonical valueSet;

                    private DependsOn(Builder builder) {
                        super(builder);
                        attribute = builder.attribute;
                        value = builder.value;
                        valueSet = builder.valueSet;
                    }

                    /**
                     * A reference to the additional attribute that holds a value the map depends on.
                     * 
                     * @return
                     *     An immutable object of type {@link Code} that is non-null.
                     */
                    public Code getAttribute() {
                        return attribute;
                    }

                    /**
                     * Data element value that the map depends on / produces.
                     * 
                     * @return
                     *     An immutable object of type {@link Code}, {@link Coding}, {@link String}, {@link Boolean} or {@link Quantity} that may 
                     *     be null.
                     */
                    public org.linuxforhealth.fhir.model.r5.type.Element getValue() {
                        return value;
                    }

                    /**
                     * This mapping applies if the data element value is a code from this value set.
                     * 
                     * @return
                     *     An immutable object of type {@link Canonical} that may be null.
                     */
                    public Canonical getValueSet() {
                        return valueSet;
                    }

                    @Override
                    public boolean hasChildren() {
                        return super.hasChildren() || 
                            (attribute != null) || 
                            (value != null) || 
                            (valueSet != null);
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
                                accept(attribute, "attribute", visitor);
                                accept(value, "value", visitor);
                                accept(valueSet, "valueSet", visitor);
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
                        DependsOn other = (DependsOn) obj;
                        return Objects.equals(id, other.id) && 
                            Objects.equals(extension, other.extension) && 
                            Objects.equals(modifierExtension, other.modifierExtension) && 
                            Objects.equals(attribute, other.attribute) && 
                            Objects.equals(value, other.value) && 
                            Objects.equals(valueSet, other.valueSet);
                    }

                    @Override
                    public int hashCode() {
                        int result = hashCode;
                        if (result == 0) {
                            result = Objects.hash(id, 
                                extension, 
                                modifierExtension, 
                                attribute, 
                                value, 
                                valueSet);
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
                        private Code attribute;
                        private org.linuxforhealth.fhir.model.r5.type.Element value;
                        private Canonical valueSet;

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
                         * A reference to the additional attribute that holds a value the map depends on.
                         * 
                         * <p>This element is required.
                         * 
                         * @param attribute
                         *     A reference to a mapping attribute defined in ConceptMap.additionalAttribute
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder attribute(Code attribute) {
                            this.attribute = attribute;
                            return this;
                        }

                        /**
                         * Convenience method for setting {@code value} with choice type String.
                         * 
                         * @param value
                         *     Value of the referenced data element
                         * 
                         * @return
                         *     A reference to this Builder instance
                         * 
                         * @see #value(Element)
                         */
                        public Builder value(java.lang.String value) {
                            this.value = (value == null) ? null : String.of(value);
                            return this;
                        }

                        /**
                         * Convenience method for setting {@code value} with choice type Boolean.
                         * 
                         * @param value
                         *     Value of the referenced data element
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
                         * Data element value that the map depends on / produces.
                         * 
                         * <p>This is a choice element with the following allowed types:
                         * <ul>
                         * <li>{@link Code}</li>
                         * <li>{@link Coding}</li>
                         * <li>{@link String}</li>
                         * <li>{@link Boolean}</li>
                         * <li>{@link Quantity}</li>
                         * </ul>
                         * 
                         * @param value
                         *     Value of the referenced data element
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder value(org.linuxforhealth.fhir.model.r5.type.Element value) {
                            this.value = value;
                            return this;
                        }

                        /**
                         * This mapping applies if the data element value is a code from this value set.
                         * 
                         * @param valueSet
                         *     The mapping depends on a data element with a value from this value set
                         * 
                         * @return
                         *     A reference to this Builder instance
                         */
                        public Builder valueSet(Canonical valueSet) {
                            this.valueSet = valueSet;
                            return this;
                        }

                        /**
                         * Build the {@link DependsOn}
                         * 
                         * <p>Required elements:
                         * <ul>
                         * <li>attribute</li>
                         * </ul>
                         * 
                         * @return
                         *     An immutable object of type {@link DependsOn}
                         * @throws IllegalStateException
                         *     if the current state cannot be built into a valid DependsOn per the base specification
                         */
                        @Override
                        public DependsOn build() {
                            DependsOn dependsOn = new DependsOn(this);
                            if (validating) {
                                validate(dependsOn);
                            }
                            return dependsOn;
                        }

                        protected void validate(DependsOn dependsOn) {
                            super.validate(dependsOn);
                            ValidationSupport.requireNonNull(dependsOn.attribute, "attribute");
                            ValidationSupport.choiceElement(dependsOn.value, "value", Code.class, Coding.class, String.class, Boolean.class, Quantity.class);
                            ValidationSupport.requireValueOrChildren(dependsOn);
                        }

                        protected Builder from(DependsOn dependsOn) {
                            super.from(dependsOn);
                            attribute = dependsOn.attribute;
                            value = dependsOn.value;
                            valueSet = dependsOn.valueSet;
                            return this;
                        }
                    }
                }
            }
        }

        /**
         * What to do when there is no mapping to a target concept from the source concept and ConceptMap.group.element.noMap is 
         * not true. This provides the "default" to be applied when there is no target concept mapping specified or the expansion 
         * of ConceptMap.group.element.target.valueSet is empty.
         */
        public static class Unmapped extends BackboneElement {
            @Binding(
                bindingName = "ConceptMapGroupUnmappedMode",
                strength = BindingStrength.Value.REQUIRED,
                description = "Defines which action to take if there is no match in the group.",
                valueSet = "http://hl7.org/fhir/ValueSet/conceptmap-unmapped-mode|5.0.0"
            )
            @Required
            private final ConceptMapGroupUnmappedMode mode;
            private final Code code;
            private final String display;
            private final Canonical valueSet;
            @Binding(
                bindingName = "UnmappedConceptMapRelationship",
                strength = BindingStrength.Value.REQUIRED,
                description = "The default relationship value to apply between the source and target concepts when no concept mapping is specified.",
                valueSet = "http://hl7.org/fhir/ValueSet/concept-map-relationship|5.0.0"
            )
            private final UnmappedConceptMapRelationship relationship;
            private final Canonical otherMap;

            private Unmapped(Builder builder) {
                super(builder);
                mode = builder.mode;
                code = builder.code;
                display = builder.display;
                valueSet = builder.valueSet;
                relationship = builder.relationship;
                otherMap = builder.otherMap;
            }

            /**
             * Defines which action to take if there is no match for the source concept in the target system designated for the 
             * group. One of 3 actions are possible: use the unmapped source code (this is useful when doing a mapping between 
             * versions, and only a few codes have changed), use a fixed code (a default code), or alternatively, a reference to a 
             * different concept map can be provided (by canonical URL).
             * 
             * @return
             *     An immutable object of type {@link ConceptMapGroupUnmappedMode} that is non-null.
             */
            public ConceptMapGroupUnmappedMode getMode() {
                return mode;
            }

            /**
             * The fixed code to use when the mode = 'fixed' - all unmapped codes are mapped to a single fixed code.
             * 
             * @return
             *     An immutable object of type {@link Code} that may be null.
             */
            public Code getCode() {
                return code;
            }

            /**
             * The display for the code. The display is only provided to help editors when editing the concept map.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getDisplay() {
                return display;
            }

            /**
             * The set of fixed codes to use when the mode = 'fixed' - all unmapped codes are mapped to each of the fixed codes.
             * 
             * @return
             *     An immutable object of type {@link Canonical} that may be null.
             */
            public Canonical getValueSet() {
                return valueSet;
            }

            /**
             * The default relationship value to apply between the source and target concepts when the source code is unmapped and 
             * the mode is 'fixed' or 'use-source-code'.
             * 
             * @return
             *     An immutable object of type {@link UnmappedConceptMapRelationship} that may be null.
             */
            public UnmappedConceptMapRelationship getRelationship() {
                return relationship;
            }

            /**
             * The canonical reference to an additional ConceptMap resource instance to use for mapping if this ConceptMap resource 
             * contains no matching mapping for the source concept.
             * 
             * @return
             *     An immutable object of type {@link Canonical} that may be null.
             */
            public Canonical getOtherMap() {
                return otherMap;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (mode != null) || 
                    (code != null) || 
                    (display != null) || 
                    (valueSet != null) || 
                    (relationship != null) || 
                    (otherMap != null);
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
                        accept(mode, "mode", visitor);
                        accept(code, "code", visitor);
                        accept(display, "display", visitor);
                        accept(valueSet, "valueSet", visitor);
                        accept(relationship, "relationship", visitor);
                        accept(otherMap, "otherMap", visitor);
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
                Unmapped other = (Unmapped) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(mode, other.mode) && 
                    Objects.equals(code, other.code) && 
                    Objects.equals(display, other.display) && 
                    Objects.equals(valueSet, other.valueSet) && 
                    Objects.equals(relationship, other.relationship) && 
                    Objects.equals(otherMap, other.otherMap);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        mode, 
                        code, 
                        display, 
                        valueSet, 
                        relationship, 
                        otherMap);
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
                private ConceptMapGroupUnmappedMode mode;
                private Code code;
                private String display;
                private Canonical valueSet;
                private UnmappedConceptMapRelationship relationship;
                private Canonical otherMap;

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
                 * Defines which action to take if there is no match for the source concept in the target system designated for the 
                 * group. One of 3 actions are possible: use the unmapped source code (this is useful when doing a mapping between 
                 * versions, and only a few codes have changed), use a fixed code (a default code), or alternatively, a reference to a 
                 * different concept map can be provided (by canonical URL).
                 * 
                 * <p>This element is required.
                 * 
                 * @param mode
                 *     use-source-code | fixed | other-map
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder mode(ConceptMapGroupUnmappedMode mode) {
                    this.mode = mode;
                    return this;
                }

                /**
                 * The fixed code to use when the mode = 'fixed' - all unmapped codes are mapped to a single fixed code.
                 * 
                 * @param code
                 *     Fixed code when mode = fixed
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder code(Code code) {
                    this.code = code;
                    return this;
                }

                /**
                 * Convenience method for setting {@code display}.
                 * 
                 * @param display
                 *     Display for the code
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
                 * The display for the code. The display is only provided to help editors when editing the concept map.
                 * 
                 * @param display
                 *     Display for the code
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder display(String display) {
                    this.display = display;
                    return this;
                }

                /**
                 * The set of fixed codes to use when the mode = 'fixed' - all unmapped codes are mapped to each of the fixed codes.
                 * 
                 * @param valueSet
                 *     Fixed code set when mode = fixed
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder valueSet(Canonical valueSet) {
                    this.valueSet = valueSet;
                    return this;
                }

                /**
                 * The default relationship value to apply between the source and target concepts when the source code is unmapped and 
                 * the mode is 'fixed' or 'use-source-code'.
                 * 
                 * @param relationship
                 *     related-to | equivalent | source-is-narrower-than-target | source-is-broader-than-target | not-related-to
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder relationship(UnmappedConceptMapRelationship relationship) {
                    this.relationship = relationship;
                    return this;
                }

                /**
                 * The canonical reference to an additional ConceptMap resource instance to use for mapping if this ConceptMap resource 
                 * contains no matching mapping for the source concept.
                 * 
                 * @param otherMap
                 *     canonical reference to an additional ConceptMap to use for mapping if the source concept is unmapped
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder otherMap(Canonical otherMap) {
                    this.otherMap = otherMap;
                    return this;
                }

                /**
                 * Build the {@link Unmapped}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>mode</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Unmapped}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Unmapped per the base specification
                 */
                @Override
                public Unmapped build() {
                    Unmapped unmapped = new Unmapped(this);
                    if (validating) {
                        validate(unmapped);
                    }
                    return unmapped;
                }

                protected void validate(Unmapped unmapped) {
                    super.validate(unmapped);
                    ValidationSupport.requireNonNull(unmapped.mode, "mode");
                    ValidationSupport.requireValueOrChildren(unmapped);
                }

                protected Builder from(Unmapped unmapped) {
                    super.from(unmapped);
                    mode = unmapped.mode;
                    code = unmapped.code;
                    display = unmapped.display;
                    valueSet = unmapped.valueSet;
                    relationship = unmapped.relationship;
                    otherMap = unmapped.otherMap;
                    return this;
                }
            }
        }
    }
}
