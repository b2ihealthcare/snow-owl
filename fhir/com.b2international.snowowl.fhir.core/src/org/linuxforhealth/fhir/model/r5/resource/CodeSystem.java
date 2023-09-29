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
import org.linuxforhealth.fhir.model.r5.type.Decimal;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.CodeSystemContentMode;
import org.linuxforhealth.fhir.model.r5.type.code.CodeSystemHierarchyMeaning;
import org.linuxforhealth.fhir.model.r5.type.code.FilterOperator;
import org.linuxforhealth.fhir.model.r5.type.code.PropertyType;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * The CodeSystem resource is used to declare the existence of and describe a code system or code system supplement and 
 * its key properties, and optionally define a part or all of its content.
 * 
 * <p>Maturity level: FMM5 (Normative)
 */
@Maturity(
    level = 5,
    status = StandardsStatus.Value.NORMATIVE
)
@Constraint(
    id = "cnl-0",
    level = "Warning",
    location = "(base)",
    description = "Name should be usable as an identifier for the module by machine processing applications such as code generation",
    expression = "name.exists() implies name.matches('^[A-Z]([A-Za-z0-9_]){1,254}$')",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
)
@Constraint(
    id = "csd-1",
    level = "Rule",
    location = "(base)",
    description = "Within a code system definition, all the codes SHALL be unique",
    expression = "concept.exists() implies concept.code.combine(%resource.concept.descendants().concept.code).isDistinct()",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "CodeSystem.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
)
@Constraint(
    id = "csd-2",
    level = "Warning",
    location = "(base)",
    description = "If there is an explicit hierarchy, a hierarchyMeaning should be provided",
    expression = "concept.concept.exists() implies hierarchyMeaning.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
)
@Constraint(
    id = "csd-3",
    level = "Warning",
    location = "(base)",
    description = "If there is an implicit hierarchy, a hierarchyMeaning should be provided",
    expression = "concept.where(property.code = 'parent' or property.code = 'child').exists() implies hierarchyMeaning.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
)
@Constraint(
    id = "csd-4",
    level = "Rule",
    location = "(base)",
    description = "If the code system content = supplement, it must nominate what it's a supplement for",
    expression = "CodeSystem.content = 'supplement' implies CodeSystem.supplements.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
)
@Constraint(
    id = "csd-5",
    level = "Rule",
    location = "CodeSystem.concept.designation",
    description = "Must have a value for concept.designation.use if concept.designation.additionalUse is present",
    expression = "additionalUse.exists() implies use.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
)
@Constraint(
    id = "codeSystem-6",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem",
    generated = true
)
@Constraint(
    id = "codeSystem-7",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem",
    generated = true
)
@Constraint(
    id = "codeSystem-8",
    level = "Warning",
    location = "concept.designation.use",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/designation-use",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/designation-use', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem",
    generated = true
)
@Constraint(
    id = "codeSystem-9",
    level = "Warning",
    location = "concept.designation.additionalUse",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/designation-use",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/designation-use', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/CodeSystem",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class CodeSystem extends DomainResource {
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
    @Summary
    private final DateTime date;
    @Summary
    private final String publisher;
    @Summary
    private final List<ContactDetail> contact;
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
    private final Markdown copyright;
    private final String copyrightLabel;
    private final Date approvalDate;
    private final Date lastReviewDate;
    @Summary
    private final Period effectivePeriod;
    @Binding(
        bindingName = "DefinitionTopic",
        strength = BindingStrength.Value.EXAMPLE,
        valueSet = "http://hl7.org/fhir/ValueSet/definition-topic"
    )
    private final List<CodeableConcept> topic;
    private final List<ContactDetail> author;
    private final List<ContactDetail> editor;
    private final List<ContactDetail> reviewer;
    private final List<ContactDetail> endorser;
    private final List<RelatedArtifact> relatedArtifact;
    @Summary
    private final Boolean caseSensitive;
    @Summary
    private final Canonical valueSet;
    @Summary
    @Binding(
        bindingName = "CodeSystemHierarchyMeaning",
        strength = BindingStrength.Value.REQUIRED,
        description = "The meaning of the hierarchy of concepts in a code system.",
        valueSet = "http://hl7.org/fhir/ValueSet/codesystem-hierarchy-meaning|5.0.0"
    )
    private final CodeSystemHierarchyMeaning hierarchyMeaning;
    @Summary
    private final Boolean compositional;
    @Summary
    private final Boolean versionNeeded;
    @Summary
    @Binding(
        bindingName = "CodeSystemContentMode",
        strength = BindingStrength.Value.REQUIRED,
        description = "The extent of the content of the code system (the concepts and codes it defines) are represented in a code system resource.",
        valueSet = "http://hl7.org/fhir/ValueSet/codesystem-content-mode|5.0.0"
    )
    @Required
    private final CodeSystemContentMode content;
    @Summary
    private final Canonical supplements;
    @Summary
    private final UnsignedInt count;
    @Summary
    private final List<Filter> filter;
    @Summary
    private final List<Property> property;
    private final List<Concept> concept;

    private CodeSystem(Builder builder) {
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
        topic = Collections.unmodifiableList(builder.topic);
        author = Collections.unmodifiableList(builder.author);
        editor = Collections.unmodifiableList(builder.editor);
        reviewer = Collections.unmodifiableList(builder.reviewer);
        endorser = Collections.unmodifiableList(builder.endorser);
        relatedArtifact = Collections.unmodifiableList(builder.relatedArtifact);
        caseSensitive = builder.caseSensitive;
        valueSet = builder.valueSet;
        hierarchyMeaning = builder.hierarchyMeaning;
        compositional = builder.compositional;
        versionNeeded = builder.versionNeeded;
        content = builder.content;
        supplements = builder.supplements;
        count = builder.count;
        filter = Collections.unmodifiableList(builder.filter);
        property = Collections.unmodifiableList(builder.property);
        concept = Collections.unmodifiableList(builder.concept);
    }

    /**
     * An absolute URI that is used to identify this code system when it is referenced in a specification, model, design or 
     * an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
     * which an authoritative instance of this code system is (or will be) published. This URL can be the target of a 
     * canonical reference. It SHALL remain the same when the code system is stored on different servers. This is used in 
     * [Coding](datatypes.html#Coding).system.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this code system when it is represented in other formats, or referenced 
     * in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the code system when it is referenced in a specification, 
     * model, design or instance. This is an arbitrary value managed by the code system author and is not expected to be 
     * globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is 
     * also no expectation that versions can be placed in a lexicographical sequence. This is used in [Coding](datatypes.
     * html#Coding).version.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Indicates the mechanism used to compare versions to determine which CodeSystem is more current.
     * 
     * @return
     *     An immutable object of type {@link String} or {@link Coding} that may be null.
     */
    public Element getVersionAlgorithm() {
        return versionAlgorithm;
    }

    /**
     * A natural language name identifying the code system. This name should be usable as an identifier for the module by 
     * machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the code system.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The status of this code system. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this code system is authored for testing purposes (or education/evaluation/marketing) 
     * and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The date (and optionally time) when the code system was last significantly changed. The date must change when the 
     * business version changes and it must change if the status code changes. In addition, it should change when the 
     * substantive content of the code system changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual responsible for the release and ongoing maintenance of the code system.
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
     * A free text natural language description of the code system from a consumer's perspective.
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
     * may be used to assist with indexing and searching for appropriate code system instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A legal or geographic region in which the code system is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explanation of why this code system is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * A copyright statement relating to the code system and/or its contents. Copyright statements are generally legal 
     * restrictions on the use and publishing of the code system.
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
     * The period during which the CodeSystem content was or is planned to be in active use.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * Descriptions related to the content of the CodeSystem. Topics provide a high-level categorization as well as keywords 
     * for the CodeSystem that can be useful for filtering and searching.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getTopic() {
        return topic;
    }

    /**
     * An individiual or organization primarily involved in the creation and maintenance of the CodeSystem.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getAuthor() {
        return author;
    }

    /**
     * An individual or organization primarily responsible for internal coherence of the CodeSystem.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getEditor() {
        return editor;
    }

    /**
     * An individual or organization asserted by the publisher to be primarily responsible for review of some aspect of the 
     * CodeSystem.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getReviewer() {
        return reviewer;
    }

    /**
     * An individual or organization asserted by the publisher to be responsible for officially endorsing the CodeSystem for 
     * use in some setting.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ContactDetail} that may be empty.
     */
    public List<ContactDetail> getEndorser() {
        return endorser;
    }

    /**
     * Related artifacts such as additional documentation, justification, dependencies, bibliographic references, and 
     * predecessor and successor artifacts.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
     */
    public List<RelatedArtifact> getRelatedArtifact() {
        return relatedArtifact;
    }

    /**
     * If code comparison is case sensitive when codes within this system are compared to each other.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Canonical reference to the value set that contains all codes in the code system independent of code status.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getValueSet() {
        return valueSet;
    }

    /**
     * The meaning of the hierarchy of concepts as represented in this resource.
     * 
     * @return
     *     An immutable object of type {@link CodeSystemHierarchyMeaning} that may be null.
     */
    public CodeSystemHierarchyMeaning getHierarchyMeaning() {
        return hierarchyMeaning;
    }

    /**
     * The code system defines a compositional (post-coordination) grammar.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getCompositional() {
        return compositional;
    }

    /**
     * This flag is used to signify that the code system does not commit to concept permanence across versions. If true, a 
     * version must be specified when referencing this code system.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getVersionNeeded() {
        return versionNeeded;
    }

    /**
     * The extent of the content of the code system (the concepts and codes it defines) are represented in this resource 
     * instance.
     * 
     * @return
     *     An immutable object of type {@link CodeSystemContentMode} that is non-null.
     */
    public CodeSystemContentMode getContent() {
        return content;
    }

    /**
     * The canonical URL of the code system that this code system supplement is adding designations and properties to.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getSupplements() {
        return supplements;
    }

    /**
     * The total number of concepts defined by the code system. Where the code system has a compositional grammar, the basis 
     * of this count is defined by the system steward.
     * 
     * @return
     *     An immutable object of type {@link UnsignedInt} that may be null.
     */
    public UnsignedInt getCount() {
        return count;
    }

    /**
     * A filter that can be used in a value set compose statement when selecting concepts using a filter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Filter} that may be empty.
     */
    public List<Filter> getFilter() {
        return filter;
    }

    /**
     * A property defines an additional slot through which additional information can be provided about a concept.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
     */
    public List<Property> getProperty() {
        return property;
    }

    /**
     * Concepts that are in the code system. The concept definitions are inherently hierarchical, but the definitions must be 
     * consulted to determine what the meanings of the hierarchical relationships are.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Concept} that may be empty.
     */
    public List<Concept> getConcept() {
        return concept;
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
            !topic.isEmpty() || 
            !author.isEmpty() || 
            !editor.isEmpty() || 
            !reviewer.isEmpty() || 
            !endorser.isEmpty() || 
            !relatedArtifact.isEmpty() || 
            (caseSensitive != null) || 
            (valueSet != null) || 
            (hierarchyMeaning != null) || 
            (compositional != null) || 
            (versionNeeded != null) || 
            (content != null) || 
            (supplements != null) || 
            (count != null) || 
            !filter.isEmpty() || 
            !property.isEmpty() || 
            !concept.isEmpty();
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
                accept(caseSensitive, "caseSensitive", visitor);
                accept(valueSet, "valueSet", visitor);
                accept(hierarchyMeaning, "hierarchyMeaning", visitor);
                accept(compositional, "compositional", visitor);
                accept(versionNeeded, "versionNeeded", visitor);
                accept(content, "content", visitor);
                accept(supplements, "supplements", visitor);
                accept(count, "count", visitor);
                accept(filter, "filter", visitor, Filter.class);
                accept(property, "property", visitor, Property.class);
                accept(concept, "concept", visitor, Concept.class);
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
        CodeSystem other = (CodeSystem) obj;
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
            Objects.equals(caseSensitive, other.caseSensitive) && 
            Objects.equals(valueSet, other.valueSet) && 
            Objects.equals(hierarchyMeaning, other.hierarchyMeaning) && 
            Objects.equals(compositional, other.compositional) && 
            Objects.equals(versionNeeded, other.versionNeeded) && 
            Objects.equals(content, other.content) && 
            Objects.equals(supplements, other.supplements) && 
            Objects.equals(count, other.count) && 
            Objects.equals(filter, other.filter) && 
            Objects.equals(property, other.property) && 
            Objects.equals(concept, other.concept);
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
                caseSensitive, 
                valueSet, 
                hierarchyMeaning, 
                compositional, 
                versionNeeded, 
                content, 
                supplements, 
                count, 
                filter, 
                property, 
                concept);
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
        private List<CodeableConcept> topic = new ArrayList<>();
        private List<ContactDetail> author = new ArrayList<>();
        private List<ContactDetail> editor = new ArrayList<>();
        private List<ContactDetail> reviewer = new ArrayList<>();
        private List<ContactDetail> endorser = new ArrayList<>();
        private List<RelatedArtifact> relatedArtifact = new ArrayList<>();
        private Boolean caseSensitive;
        private Canonical valueSet;
        private CodeSystemHierarchyMeaning hierarchyMeaning;
        private Boolean compositional;
        private Boolean versionNeeded;
        private CodeSystemContentMode content;
        private Canonical supplements;
        private UnsignedInt count;
        private List<Filter> filter = new ArrayList<>();
        private List<Property> property = new ArrayList<>();
        private List<Concept> concept = new ArrayList<>();

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
        public Builder url(Uri url) {
            this.url = url;
            return this;
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
        public Builder identifier(Identifier... identifier) {
            for (Identifier value : identifier) {
                this.identifier.add(value);
            }
            return this;
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
        public Builder identifier(Collection<Identifier> identifier) {
            this.identifier = new ArrayList<>(identifier);
            return this;
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
        public Builder version(java.lang.String version) {
            this.version = (version == null) ? null : String.of(version);
            return this;
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
        public Builder versionAlgorithm(Element versionAlgorithm) {
            this.versionAlgorithm = versionAlgorithm;
            return this;
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
        public Builder name(java.lang.String name) {
            this.name = (name == null) ? null : String.of(name);
            return this;
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
        public Builder name(String name) {
            this.name = name;
            return this;
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
        public Builder title(java.lang.String title) {
            this.title = (title == null) ? null : String.of(title);
            return this;
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
        public Builder title(String title) {
            this.title = title;
            return this;
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
         * A Boolean value to indicate that this code system is authored for testing purposes (or education/evaluation/marketing) 
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
         * The name of the organization or individual responsible for the release and ongoing maintenance of the code system.
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
         * A free text natural language description of the code system from a consumer's perspective.
         * 
         * @param description
         *     Natural language description of the code system
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
        public Builder useContext(UsageContext... useContext) {
            for (UsageContext value : useContext) {
                this.useContext.add(value);
            }
            return this;
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
        public Builder useContext(Collection<UsageContext> useContext) {
            this.useContext = new ArrayList<>(useContext);
            return this;
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
        public Builder jurisdiction(CodeableConcept... jurisdiction) {
            for (CodeableConcept value : jurisdiction) {
                this.jurisdiction.add(value);
            }
            return this;
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
        public Builder jurisdiction(Collection<CodeableConcept> jurisdiction) {
            this.jurisdiction = new ArrayList<>(jurisdiction);
            return this;
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
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
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
         *     When the CodeSystem was approved by publisher
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
         *     When the CodeSystem was approved by publisher
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
         *     When the CodeSystem was last reviewed by the publisher
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
         *     When the CodeSystem was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
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
        public Builder effectivePeriod(Period effectivePeriod) {
            this.effectivePeriod = effectivePeriod;
            return this;
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
        public Builder topic(CodeableConcept... topic) {
            for (CodeableConcept value : topic) {
                this.topic.add(value);
            }
            return this;
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
        public Builder topic(Collection<CodeableConcept> topic) {
            this.topic = new ArrayList<>(topic);
            return this;
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
        public Builder author(ContactDetail... author) {
            for (ContactDetail value : author) {
                this.author.add(value);
            }
            return this;
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
        public Builder author(Collection<ContactDetail> author) {
            this.author = new ArrayList<>(author);
            return this;
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
        public Builder editor(ContactDetail... editor) {
            for (ContactDetail value : editor) {
                this.editor.add(value);
            }
            return this;
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
        public Builder editor(Collection<ContactDetail> editor) {
            this.editor = new ArrayList<>(editor);
            return this;
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
        public Builder reviewer(ContactDetail... reviewer) {
            for (ContactDetail value : reviewer) {
                this.reviewer.add(value);
            }
            return this;
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
        public Builder reviewer(Collection<ContactDetail> reviewer) {
            this.reviewer = new ArrayList<>(reviewer);
            return this;
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
        public Builder endorser(ContactDetail... endorser) {
            for (ContactDetail value : endorser) {
                this.endorser.add(value);
            }
            return this;
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
        public Builder endorser(Collection<ContactDetail> endorser) {
            this.endorser = new ArrayList<>(endorser);
            return this;
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
        public Builder relatedArtifact(RelatedArtifact... relatedArtifact) {
            for (RelatedArtifact value : relatedArtifact) {
                this.relatedArtifact.add(value);
            }
            return this;
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
        public Builder relatedArtifact(Collection<RelatedArtifact> relatedArtifact) {
            this.relatedArtifact = new ArrayList<>(relatedArtifact);
            return this;
        }

        /**
         * Convenience method for setting {@code caseSensitive}.
         * 
         * @param caseSensitive
         *     If code comparison is case sensitive
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #caseSensitive(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder caseSensitive(java.lang.Boolean caseSensitive) {
            this.caseSensitive = (caseSensitive == null) ? null : Boolean.of(caseSensitive);
            return this;
        }

        /**
         * If code comparison is case sensitive when codes within this system are compared to each other.
         * 
         * @param caseSensitive
         *     If code comparison is case sensitive
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder caseSensitive(Boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        /**
         * Canonical reference to the value set that contains all codes in the code system independent of code status.
         * 
         * @param valueSet
         *     Canonical reference to the value set with entire code system
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder valueSet(Canonical valueSet) {
            this.valueSet = valueSet;
            return this;
        }

        /**
         * The meaning of the hierarchy of concepts as represented in this resource.
         * 
         * @param hierarchyMeaning
         *     grouped-by | is-a | part-of | classified-with
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder hierarchyMeaning(CodeSystemHierarchyMeaning hierarchyMeaning) {
            this.hierarchyMeaning = hierarchyMeaning;
            return this;
        }

        /**
         * Convenience method for setting {@code compositional}.
         * 
         * @param compositional
         *     If code system defines a compositional grammar
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #compositional(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder compositional(java.lang.Boolean compositional) {
            this.compositional = (compositional == null) ? null : Boolean.of(compositional);
            return this;
        }

        /**
         * The code system defines a compositional (post-coordination) grammar.
         * 
         * @param compositional
         *     If code system defines a compositional grammar
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder compositional(Boolean compositional) {
            this.compositional = compositional;
            return this;
        }

        /**
         * Convenience method for setting {@code versionNeeded}.
         * 
         * @param versionNeeded
         *     If definitions are not stable
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #versionNeeded(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder versionNeeded(java.lang.Boolean versionNeeded) {
            this.versionNeeded = (versionNeeded == null) ? null : Boolean.of(versionNeeded);
            return this;
        }

        /**
         * This flag is used to signify that the code system does not commit to concept permanence across versions. If true, a 
         * version must be specified when referencing this code system.
         * 
         * @param versionNeeded
         *     If definitions are not stable
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder versionNeeded(Boolean versionNeeded) {
            this.versionNeeded = versionNeeded;
            return this;
        }

        /**
         * The extent of the content of the code system (the concepts and codes it defines) are represented in this resource 
         * instance.
         * 
         * <p>This element is required.
         * 
         * @param content
         *     not-present | example | fragment | complete | supplement
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder content(CodeSystemContentMode content) {
            this.content = content;
            return this;
        }

        /**
         * The canonical URL of the code system that this code system supplement is adding designations and properties to.
         * 
         * @param supplements
         *     Canonical URL of Code System this adds designations and properties to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supplements(Canonical supplements) {
            this.supplements = supplements;
            return this;
        }

        /**
         * The total number of concepts defined by the code system. Where the code system has a compositional grammar, the basis 
         * of this count is defined by the system steward.
         * 
         * @param count
         *     Total concepts in the code system
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder count(UnsignedInt count) {
            this.count = count;
            return this;
        }

        /**
         * A filter that can be used in a value set compose statement when selecting concepts using a filter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param filter
         *     Filter that can be used in a value set
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder filter(Filter... filter) {
            for (Filter value : filter) {
                this.filter.add(value);
            }
            return this;
        }

        /**
         * A filter that can be used in a value set compose statement when selecting concepts using a filter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param filter
         *     Filter that can be used in a value set
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder filter(Collection<Filter> filter) {
            this.filter = new ArrayList<>(filter);
            return this;
        }

        /**
         * A property defines an additional slot through which additional information can be provided about a concept.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Additional information supplied about each concept
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
         * A property defines an additional slot through which additional information can be provided about a concept.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param property
         *     Additional information supplied about each concept
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
         * Concepts that are in the code system. The concept definitions are inherently hierarchical, but the definitions must be 
         * consulted to determine what the meanings of the hierarchical relationships are.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param concept
         *     Concepts in the code system
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder concept(Concept... concept) {
            for (Concept value : concept) {
                this.concept.add(value);
            }
            return this;
        }

        /**
         * Concepts that are in the code system. The concept definitions are inherently hierarchical, but the definitions must be 
         * consulted to determine what the meanings of the hierarchical relationships are.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param concept
         *     Concepts in the code system
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder concept(Collection<Concept> concept) {
            this.concept = new ArrayList<>(concept);
            return this;
        }

        /**
         * Build the {@link CodeSystem}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>content</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link CodeSystem}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid CodeSystem per the base specification
         */
        @Override
        public CodeSystem build() {
            CodeSystem codeSystem = new CodeSystem(this);
            if (validating) {
                validate(codeSystem);
            }
            return codeSystem;
        }

        protected void validate(CodeSystem codeSystem) {
            super.validate(codeSystem);
            ValidationSupport.checkList(codeSystem.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(codeSystem.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(codeSystem.status, "status");
            ValidationSupport.checkList(codeSystem.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(codeSystem.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(codeSystem.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(codeSystem.topic, "topic", CodeableConcept.class);
            ValidationSupport.checkList(codeSystem.author, "author", ContactDetail.class);
            ValidationSupport.checkList(codeSystem.editor, "editor", ContactDetail.class);
            ValidationSupport.checkList(codeSystem.reviewer, "reviewer", ContactDetail.class);
            ValidationSupport.checkList(codeSystem.endorser, "endorser", ContactDetail.class);
            ValidationSupport.checkList(codeSystem.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
            ValidationSupport.requireNonNull(codeSystem.content, "content");
            ValidationSupport.checkList(codeSystem.filter, "filter", Filter.class);
            ValidationSupport.checkList(codeSystem.property, "property", Property.class);
            ValidationSupport.checkList(codeSystem.concept, "concept", Concept.class);
        }

        protected Builder from(CodeSystem codeSystem) {
            super.from(codeSystem);
            url = codeSystem.url;
            identifier.addAll(codeSystem.identifier);
            version = codeSystem.version;
            versionAlgorithm = codeSystem.versionAlgorithm;
            name = codeSystem.name;
            title = codeSystem.title;
            status = codeSystem.status;
            experimental = codeSystem.experimental;
            date = codeSystem.date;
            publisher = codeSystem.publisher;
            contact.addAll(codeSystem.contact);
            description = codeSystem.description;
            useContext.addAll(codeSystem.useContext);
            jurisdiction.addAll(codeSystem.jurisdiction);
            purpose = codeSystem.purpose;
            copyright = codeSystem.copyright;
            copyrightLabel = codeSystem.copyrightLabel;
            approvalDate = codeSystem.approvalDate;
            lastReviewDate = codeSystem.lastReviewDate;
            effectivePeriod = codeSystem.effectivePeriod;
            topic.addAll(codeSystem.topic);
            author.addAll(codeSystem.author);
            editor.addAll(codeSystem.editor);
            reviewer.addAll(codeSystem.reviewer);
            endorser.addAll(codeSystem.endorser);
            relatedArtifact.addAll(codeSystem.relatedArtifact);
            caseSensitive = codeSystem.caseSensitive;
            valueSet = codeSystem.valueSet;
            hierarchyMeaning = codeSystem.hierarchyMeaning;
            compositional = codeSystem.compositional;
            versionNeeded = codeSystem.versionNeeded;
            content = codeSystem.content;
            supplements = codeSystem.supplements;
            count = codeSystem.count;
            filter.addAll(codeSystem.filter);
            property.addAll(codeSystem.property);
            concept.addAll(codeSystem.concept);
            return this;
        }
    }

    /**
     * A filter that can be used in a value set compose statement when selecting concepts using a filter.
     */
    public static class Filter extends BackboneElement {
        @Summary
        @Required
        private final Code code;
        @Summary
        private final String description;
        @Summary
        @Binding(
            bindingName = "FilterOperator",
            strength = BindingStrength.Value.REQUIRED,
            description = "The kind of operation to perform as a part of a property based filter.",
            valueSet = "http://hl7.org/fhir/ValueSet/filter-operator|5.0.0"
        )
        @Required
        private final List<FilterOperator> operator;
        @Summary
        @Required
        private final String value;

        private Filter(Builder builder) {
            super(builder);
            code = builder.code;
            description = builder.description;
            operator = Collections.unmodifiableList(builder.operator);
            value = builder.value;
        }

        /**
         * The code that identifies this filter when it is used as a filter in [ValueSet](valueset.html#).compose.include.filter.
         * 
         * @return
         *     An immutable object of type {@link Code} that is non-null.
         */
        public Code getCode() {
            return code;
        }

        /**
         * A description of how or why the filter is used.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDescription() {
            return description;
        }

        /**
         * A list of operators that can be used with the filter.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link FilterOperator} that is non-empty.
         */
        public List<FilterOperator> getOperator() {
            return operator;
        }

        /**
         * A description of what the value for the filter should be.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (description != null) || 
                !operator.isEmpty() || 
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
                    accept(description, "description", visitor);
                    accept(operator, "operator", visitor, FilterOperator.class);
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
            Filter other = (Filter) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(description, other.description) && 
                Objects.equals(operator, other.operator) && 
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
                    description, 
                    operator, 
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
            private String description;
            private List<FilterOperator> operator = new ArrayList<>();
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
             * The code that identifies this filter when it is used as a filter in [ValueSet](valueset.html#).compose.include.filter.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Code that identifies the filter
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(Code code) {
                this.code = code;
                return this;
            }

            /**
             * Convenience method for setting {@code description}.
             * 
             * @param description
             *     How or why the filter is used
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
             * A description of how or why the filter is used.
             * 
             * @param description
             *     How or why the filter is used
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * A list of operators that can be used with the filter.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param operator
             *     = | is-a | descendent-of | is-not-a | regex | in | not-in | generalizes | child-of | descendent-leaf | exists
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder operator(FilterOperator... operator) {
                for (FilterOperator value : operator) {
                    this.operator.add(value);
                }
                return this;
            }

            /**
             * A list of operators that can be used with the filter.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>This element is required.
             * 
             * @param operator
             *     = | is-a | descendent-of | is-not-a | regex | in | not-in | generalizes | child-of | descendent-leaf | exists
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder operator(Collection<FilterOperator> operator) {
                this.operator = new ArrayList<>(operator);
                return this;
            }

            /**
             * Convenience method for setting {@code value}.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     What to use for the value
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
             * A description of what the value for the filter should be.
             * 
             * <p>This element is required.
             * 
             * @param value
             *     What to use for the value
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(String value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Filter}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * <li>operator</li>
             * <li>value</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Filter}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Filter per the base specification
             */
            @Override
            public Filter build() {
                Filter filter = new Filter(this);
                if (validating) {
                    validate(filter);
                }
                return filter;
            }

            protected void validate(Filter filter) {
                super.validate(filter);
                ValidationSupport.requireNonNull(filter.code, "code");
                ValidationSupport.checkNonEmptyList(filter.operator, "operator", FilterOperator.class);
                ValidationSupport.requireNonNull(filter.value, "value");
                ValidationSupport.requireValueOrChildren(filter);
            }

            protected Builder from(Filter filter) {
                super.from(filter);
                code = filter.code;
                description = filter.description;
                operator.addAll(filter.operator);
                value = filter.value;
                return this;
            }
        }
    }

    /**
     * A property defines an additional slot through which additional information can be provided about a concept.
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
            valueSet = "http://hl7.org/fhir/ValueSet/concept-property-type|5.0.0"
        )
        @Required
        private final PropertyType type;

        private Property(Builder builder) {
            super(builder);
            code = builder.code;
            uri = builder.uri;
            description = builder.description;
            type = builder.type;
        }

        /**
         * A code that is used to identify the property. The code is used internally (in CodeSystem.concept.property.code) and 
         * also externally, such as in property filters.
         * 
         * @return
         *     An immutable object of type {@link Code} that is non-null.
         */
        public Code getCode() {
            return code;
        }

        /**
         * Reference to the formal meaning of the property. One possible source of meaning is the [Concept Properties](codesystem-
         * concept-properties.html) code system.
         * 
         * @return
         *     An immutable object of type {@link Uri} that may be null.
         */
        public Uri getUri() {
            return uri;
        }

        /**
         * A description of the property- why it is defined, and how its value might be used.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDescription() {
            return description;
        }

        /**
         * The type of the property value. Properties of type "code" contain a code defined by the code system (e.g. a reference 
         * to another defined concept).
         * 
         * @return
         *     An immutable object of type {@link PropertyType} that is non-null.
         */
        public PropertyType getType() {
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
            Property other = (Property) obj;
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
            private PropertyType type;

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
             * A code that is used to identify the property. The code is used internally (in CodeSystem.concept.property.code) and 
             * also externally, such as in property filters.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Identifies the property on the concepts, and when referred to in operations
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(Code code) {
                this.code = code;
                return this;
            }

            /**
             * Reference to the formal meaning of the property. One possible source of meaning is the [Concept Properties](codesystem-
             * concept-properties.html) code system.
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
             * A description of the property- why it is defined, and how its value might be used.
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
             * The type of the property value. Properties of type "code" contain a code defined by the code system (e.g. a reference 
             * to another defined concept).
             * 
             * <p>This element is required.
             * 
             * @param type
             *     code | Coding | string | integer | boolean | dateTime | decimal
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(PropertyType type) {
                this.type = type;
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
                return this;
            }
        }
    }

    /**
     * Concepts that are in the code system. The concept definitions are inherently hierarchical, but the definitions must be 
     * consulted to determine what the meanings of the hierarchical relationships are.
     */
    public static class Concept extends BackboneElement {
        @Required
        private final Code code;
        private final String display;
        private final String definition;
        private final List<Designation> designation;
        private final List<Property> property;
        private final List<CodeSystem.Concept> concept;

        private Concept(Builder builder) {
            super(builder);
            code = builder.code;
            display = builder.display;
            definition = builder.definition;
            designation = Collections.unmodifiableList(builder.designation);
            property = Collections.unmodifiableList(builder.property);
            concept = Collections.unmodifiableList(builder.concept);
        }

        /**
         * A code - a text symbol - that uniquely identifies the concept within the code system.
         * 
         * @return
         *     An immutable object of type {@link Code} that is non-null.
         */
        public Code getCode() {
            return code;
        }

        /**
         * A human readable string that is the recommended default way to present this concept to a user.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDisplay() {
            return display;
        }

        /**
         * The formal definition of the concept. The code system resource does not make formal definitions required, because of 
         * the prevalence of legacy systems. However, they are highly recommended, as without them there is no formal meaning 
         * associated with the concept.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDefinition() {
            return definition;
        }

        /**
         * Additional representations for the concept - other languages, aliases, specialized purposes, used for particular 
         * purposes, etc.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Designation} that may be empty.
         */
        public List<Designation> getDesignation() {
            return designation;
        }

        /**
         * A property value for this concept.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Property} that may be empty.
         */
        public List<Property> getProperty() {
            return property;
        }

        /**
         * Defines children of a concept to produce a hierarchy of concepts. The nature of the relationships is variable (is-
         * a/contains/categorizes) - see hierarchyMeaning.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Concept} that may be empty.
         */
        public List<CodeSystem.Concept> getConcept() {
            return concept;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (display != null) || 
                (definition != null) || 
                !designation.isEmpty() || 
                !property.isEmpty() || 
                !concept.isEmpty();
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
                    accept(definition, "definition", visitor);
                    accept(designation, "designation", visitor, Designation.class);
                    accept(property, "property", visitor, Property.class);
                    accept(concept, "concept", visitor, CodeSystem.Concept.class);
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
            Concept other = (Concept) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(display, other.display) && 
                Objects.equals(definition, other.definition) && 
                Objects.equals(designation, other.designation) && 
                Objects.equals(property, other.property) && 
                Objects.equals(concept, other.concept);
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
                    definition, 
                    designation, 
                    property, 
                    concept);
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
            private String definition;
            private List<Designation> designation = new ArrayList<>();
            private List<Property> property = new ArrayList<>();
            private List<CodeSystem.Concept> concept = new ArrayList<>();

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
             * A code - a text symbol - that uniquely identifies the concept within the code system.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Code that identifies concept
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
             *     Text to display to the user
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
             * A human readable string that is the recommended default way to present this concept to a user.
             * 
             * @param display
             *     Text to display to the user
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder display(String display) {
                this.display = display;
                return this;
            }

            /**
             * Convenience method for setting {@code definition}.
             * 
             * @param definition
             *     Formal definition
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #definition(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder definition(java.lang.String definition) {
                this.definition = (definition == null) ? null : String.of(definition);
                return this;
            }

            /**
             * The formal definition of the concept. The code system resource does not make formal definitions required, because of 
             * the prevalence of legacy systems. However, they are highly recommended, as without them there is no formal meaning 
             * associated with the concept.
             * 
             * @param definition
             *     Formal definition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definition(String definition) {
                this.definition = definition;
                return this;
            }

            /**
             * Additional representations for the concept - other languages, aliases, specialized purposes, used for particular 
             * purposes, etc.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param designation
             *     Additional representations for the concept
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder designation(Designation... designation) {
                for (Designation value : designation) {
                    this.designation.add(value);
                }
                return this;
            }

            /**
             * Additional representations for the concept - other languages, aliases, specialized purposes, used for particular 
             * purposes, etc.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param designation
             *     Additional representations for the concept
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder designation(Collection<Designation> designation) {
                this.designation = new ArrayList<>(designation);
                return this;
            }

            /**
             * A property value for this concept.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param property
             *     Property value for the concept
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
             * A property value for this concept.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param property
             *     Property value for the concept
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
             * Defines children of a concept to produce a hierarchy of concepts. The nature of the relationships is variable (is-
             * a/contains/categorizes) - see hierarchyMeaning.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param concept
             *     Child Concepts (is-a/contains/categorizes)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder concept(CodeSystem.Concept... concept) {
                for (CodeSystem.Concept value : concept) {
                    this.concept.add(value);
                }
                return this;
            }

            /**
             * Defines children of a concept to produce a hierarchy of concepts. The nature of the relationships is variable (is-
             * a/contains/categorizes) - see hierarchyMeaning.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param concept
             *     Child Concepts (is-a/contains/categorizes)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder concept(Collection<CodeSystem.Concept> concept) {
                this.concept = new ArrayList<>(concept);
                return this;
            }

            /**
             * Build the {@link Concept}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Concept}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Concept per the base specification
             */
            @Override
            public Concept build() {
                Concept concept = new Concept(this);
                if (validating) {
                    validate(concept);
                }
                return concept;
            }

            protected void validate(Concept concept) {
                super.validate(concept);
                ValidationSupport.requireNonNull(concept.code, "code");
                ValidationSupport.checkList(concept.designation, "designation", Designation.class);
                ValidationSupport.checkList(concept.property, "property", Property.class);
                ValidationSupport.checkList(concept.concept, "concept", CodeSystem.Concept.class);
                ValidationSupport.requireValueOrChildren(concept);
            }

            protected Builder from(Concept concept) {
                super.from(concept);
                code = concept.code;
                display = concept.display;
                definition = concept.definition;
                designation.addAll(concept.designation);
                property.addAll(concept.property);
                this.concept.addAll(concept.concept);
                return this;
            }
        }

        /**
         * Additional representations for the concept - other languages, aliases, specialized purposes, used for particular 
         * purposes, etc.
         */
        public static class Designation extends BackboneElement {
            @Binding(
                bindingName = "Language",
                strength = BindingStrength.Value.REQUIRED,
                description = "IETF language tag for a human language",
                valueSet = "http://hl7.org/fhir/ValueSet/all-languages|5.0.0"
            )
            private final Code language;
            @Binding(
                bindingName = "ConceptDesignationUse",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "Details of how a designation would be used.",
                valueSet = "http://hl7.org/fhir/ValueSet/designation-use"
            )
            private final Coding use;
            @Binding(
                bindingName = "ConceptDesignationUse",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "Details of how a designation would be used.",
                valueSet = "http://hl7.org/fhir/ValueSet/designation-use"
            )
            private final List<Coding> additionalUse;
            @Required
            private final String value;

            private Designation(Builder builder) {
                super(builder);
                language = builder.language;
                use = builder.use;
                additionalUse = Collections.unmodifiableList(builder.additionalUse);
                value = builder.value;
            }

            /**
             * The language this designation is defined for.
             * 
             * @return
             *     An immutable object of type {@link Code} that may be null.
             */
            public Code getLanguage() {
                return language;
            }

            /**
             * A code that details how this designation would be used.
             * 
             * @return
             *     An immutable object of type {@link Coding} that may be null.
             */
            public Coding getUse() {
                return use;
            }

            /**
             * Additional codes that detail how this designation would be used, if there is more than one use.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
             */
            public List<Coding> getAdditionalUse() {
                return additionalUse;
            }

            /**
             * The text value for this designation.
             * 
             * @return
             *     An immutable object of type {@link String} that is non-null.
             */
            public String getValue() {
                return value;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (language != null) || 
                    (use != null) || 
                    !additionalUse.isEmpty() || 
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
                        accept(language, "language", visitor);
                        accept(use, "use", visitor);
                        accept(additionalUse, "additionalUse", visitor, Coding.class);
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
                Designation other = (Designation) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(language, other.language) && 
                    Objects.equals(use, other.use) && 
                    Objects.equals(additionalUse, other.additionalUse) && 
                    Objects.equals(value, other.value);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        language, 
                        use, 
                        additionalUse, 
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
                private Code language;
                private Coding use;
                private List<Coding> additionalUse = new ArrayList<>();
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
                 * The language this designation is defined for.
                 * 
                 * @param language
                 *     Human language of the designation
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder language(Code language) {
                    this.language = language;
                    return this;
                }

                /**
                 * A code that details how this designation would be used.
                 * 
                 * @param use
                 *     Details how this designation would be used
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder use(Coding use) {
                    this.use = use;
                    return this;
                }

                /**
                 * Additional codes that detail how this designation would be used, if there is more than one use.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param additionalUse
                 *     Additional ways how this designation would be used
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder additionalUse(Coding... additionalUse) {
                    for (Coding value : additionalUse) {
                        this.additionalUse.add(value);
                    }
                    return this;
                }

                /**
                 * Additional codes that detail how this designation would be used, if there is more than one use.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param additionalUse
                 *     Additional ways how this designation would be used
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder additionalUse(Collection<Coding> additionalUse) {
                    this.additionalUse = new ArrayList<>(additionalUse);
                    return this;
                }

                /**
                 * Convenience method for setting {@code value}.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     The text value for this designation
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
                 * The text value for this designation.
                 * 
                 * <p>This element is required.
                 * 
                 * @param value
                 *     The text value for this designation
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(String value) {
                    this.value = value;
                    return this;
                }

                /**
                 * Build the {@link Designation}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>value</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Designation}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Designation per the base specification
                 */
                @Override
                public Designation build() {
                    Designation designation = new Designation(this);
                    if (validating) {
                        validate(designation);
                    }
                    return designation;
                }

                protected void validate(Designation designation) {
                    super.validate(designation);
                    ValidationSupport.checkList(designation.additionalUse, "additionalUse", Coding.class);
                    ValidationSupport.requireNonNull(designation.value, "value");
                    ValidationSupport.checkValueSetBinding(designation.language, "language", "http://hl7.org/fhir/ValueSet/all-languages", "urn:ietf:bcp:47");
                    ValidationSupport.requireValueOrChildren(designation);
                }

                protected Builder from(Designation designation) {
                    super.from(designation);
                    language = designation.language;
                    use = designation.use;
                    additionalUse.addAll(designation.additionalUse);
                    value = designation.value;
                    return this;
                }
            }
        }

        /**
         * A property value for this concept.
         */
        public static class Property extends BackboneElement {
            @Required
            private final Code code;
            @Choice({ Code.class, Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class })
            @Required
            private final Element value;

            private Property(Builder builder) {
                super(builder);
                code = builder.code;
                value = builder.value;
            }

            /**
             * A code that is a reference to CodeSystem.property.code.
             * 
             * @return
             *     An immutable object of type {@link Code} that is non-null.
             */
            public Code getCode() {
                return code;
            }

            /**
             * The value of this property.
             * 
             * @return
             *     An immutable object of type {@link Code}, {@link Coding}, {@link String}, {@link Integer}, {@link Boolean}, {@link 
             *     DateTime} or {@link Decimal} that is non-null.
             */
            public Element getValue() {
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
                 * A code that is a reference to CodeSystem.property.code.
                 * 
                 * <p>This element is required.
                 * 
                 * @param code
                 *     Reference to CodeSystem.property.code
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
                 * The value of this property.
                 * 
                 * <p>This element is required.
                 * 
                 * <p>This is a choice element with the following allowed types:
                 * <ul>
                 * <li>{@link Code}</li>
                 * <li>{@link Coding}</li>
                 * <li>{@link String}</li>
                 * <li>{@link Integer}</li>
                 * <li>{@link Boolean}</li>
                 * <li>{@link DateTime}</li>
                 * <li>{@link Decimal}</li>
                 * </ul>
                 * 
                 * @param value
                 *     Value of the property for this concept
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder value(Element value) {
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
                    ValidationSupport.requireChoiceElement(property.value, "value", Code.class, Coding.class, String.class, Integer.class, Boolean.class, DateTime.class, Decimal.class);
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
    }
}
