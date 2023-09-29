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
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.Url;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.ConformanceExpectation;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A set of requirements - a list of features or behaviors of designed systems that are necessary to achieve 
 * organizational or regulatory goals.
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
    source = "http://hl7.org/fhir/StructureDefinition/Requirements"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "Requirements.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/Requirements"
)
@Constraint(
    id = "requirements-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/Requirements",
    generated = true
)
@Constraint(
    id = "requirements-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/Requirements",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Requirements extends DomainResource {
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
    @Summary
    private final List<Canonical> derivedFrom;
    private final List<Url> reference;
    private final List<Canonical> actor;
    private final List<Statement> statement;

    private Requirements(Builder builder) {
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
        derivedFrom = Collections.unmodifiableList(builder.derivedFrom);
        reference = Collections.unmodifiableList(builder.reference);
        actor = Collections.unmodifiableList(builder.actor);
        statement = Collections.unmodifiableList(builder.statement);
    }

    /**
     * An absolute URI that is used to identify this Requirements when it is referenced in a specification, model, design or 
     * an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
     * which an authoritative instance of this Requirements is (or will be) published. This URL can be the target of a 
     * canonical reference. It SHALL remain the same when the Requirements is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this Requirements when it is represented in other formats, or referenced 
     * in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the Requirements when it is referenced in a specification, 
     * model, design or instance. This is an arbitrary value managed by the Requirements author and is not expected to be 
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
     * A natural language name identifying the Requirements. This name should be usable as an identifier for the module by 
     * machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the Requirements.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The status of this Requirements. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this Requirements is authored for testing purposes (or 
     * education/evaluation/marketing) and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The date (and optionally time) when the Requirements was published. The date must change when the business version 
     * changes and it must change if the status code changes. In addition, it should change when the substantive content of 
     * the Requirements changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual responsible for the release and ongoing maintenance of the Requirements.
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
     * A free text natural language description of the requirements.
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
     * may be used to assist with indexing and searching for appropriate Requirements instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A legal or geographic region in which the Requirements is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explanation of why this Requirements is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * A copyright statement relating to the Requirements and/or its contents. Copyright statements are generally legal 
     * restrictions on the use and publishing of the Requirements.
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
     * Another set of Requirements that this set of Requirements builds on and updates.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * A reference to another artifact that created this set of requirements. This could be a Profile, etc., or external 
     * regulation, or business requirements expressed elsewhere.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Url} that may be empty.
     */
    public List<Url> getReference() {
        return reference;
    }

    /**
     * An actor these requirements are in regard to.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getActor() {
        return actor;
    }

    /**
     * The actual statement of requirement, in markdown format.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Statement} that may be empty.
     */
    public List<Statement> getStatement() {
        return statement;
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
            !derivedFrom.isEmpty() || 
            !reference.isEmpty() || 
            !actor.isEmpty() || 
            !statement.isEmpty();
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
                accept(derivedFrom, "derivedFrom", visitor, Canonical.class);
                accept(reference, "reference", visitor, Url.class);
                accept(actor, "actor", visitor, Canonical.class);
                accept(statement, "statement", visitor, Statement.class);
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
        Requirements other = (Requirements) obj;
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
            Objects.equals(derivedFrom, other.derivedFrom) && 
            Objects.equals(reference, other.reference) && 
            Objects.equals(actor, other.actor) && 
            Objects.equals(statement, other.statement);
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
                derivedFrom, 
                reference, 
                actor, 
                statement);
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
        private List<Canonical> derivedFrom = new ArrayList<>();
        private List<Url> reference = new ArrayList<>();
        private List<Canonical> actor = new ArrayList<>();
        private List<Statement> statement = new ArrayList<>();

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
         * An absolute URI that is used to identify this Requirements when it is referenced in a specification, model, design or 
         * an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
         * which an authoritative instance of this Requirements is (or will be) published. This URL can be the target of a 
         * canonical reference. It SHALL remain the same when the Requirements is stored on different servers.
         * 
         * @param url
         *     Canonical identifier for this Requirements, represented as a URI (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A formal identifier that is used to identify this Requirements when it is represented in other formats, or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the Requirements (business identifier)
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
         * A formal identifier that is used to identify this Requirements when it is represented in other formats, or referenced 
         * in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the Requirements (business identifier)
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
         *     Business version of the Requirements
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
         * The identifier that is used to identify this version of the Requirements when it is referenced in a specification, 
         * model, design or instance. This is an arbitrary value managed by the Requirements author and is not expected to be 
         * globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is 
         * also no expectation that versions can be placed in a lexicographical sequence.
         * 
         * @param version
         *     Business version of the Requirements
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
         *     Name for this Requirements (computer friendly)
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
         * A natural language name identifying the Requirements. This name should be usable as an identifier for the module by 
         * machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this Requirements (computer friendly)
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
         *     Name for this Requirements (human friendly)
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
         * A short, descriptive, user-friendly title for the Requirements.
         * 
         * @param title
         *     Name for this Requirements (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * The status of this Requirements. Enables tracking the life-cycle of the content.
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
         * A Boolean value to indicate that this Requirements is authored for testing purposes (or 
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
         * The date (and optionally time) when the Requirements was published. The date must change when the business version 
         * changes and it must change if the status code changes. In addition, it should change when the substantive content of 
         * the Requirements changes.
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
         * The name of the organization or individual responsible for the release and ongoing maintenance of the Requirements.
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
         * A free text natural language description of the requirements.
         * 
         * @param description
         *     Natural language description of the requirements
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
         * may be used to assist with indexing and searching for appropriate Requirements instances.
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
         * may be used to assist with indexing and searching for appropriate Requirements instances.
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
         * A legal or geographic region in which the Requirements is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for Requirements (if applicable)
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
         * A legal or geographic region in which the Requirements is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for Requirements (if applicable)
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
         * Explanation of why this Requirements is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this Requirements is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * A copyright statement relating to the Requirements and/or its contents. Copyright statements are generally legal 
         * restrictions on the use and publishing of the Requirements.
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
         * Another set of Requirements that this set of Requirements builds on and updates.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFrom
         *     Other set of Requirements this builds on
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFrom(Canonical... derivedFrom) {
            for (Canonical value : derivedFrom) {
                this.derivedFrom.add(value);
            }
            return this;
        }

        /**
         * Another set of Requirements that this set of Requirements builds on and updates.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFrom
         *     Other set of Requirements this builds on
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder derivedFrom(Collection<Canonical> derivedFrom) {
            this.derivedFrom = new ArrayList<>(derivedFrom);
            return this;
        }

        /**
         * A reference to another artifact that created this set of requirements. This could be a Profile, etc., or external 
         * regulation, or business requirements expressed elsewhere.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reference
         *     External artifact (rule/document etc. that) created this set of requirements
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reference(Url... reference) {
            for (Url value : reference) {
                this.reference.add(value);
            }
            return this;
        }

        /**
         * A reference to another artifact that created this set of requirements. This could be a Profile, etc., or external 
         * regulation, or business requirements expressed elsewhere.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reference
         *     External artifact (rule/document etc. that) created this set of requirements
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reference(Collection<Url> reference) {
            this.reference = new ArrayList<>(reference);
            return this;
        }

        /**
         * An actor these requirements are in regard to.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param actor
         *     Actor for these requirements
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actor(Canonical... actor) {
            for (Canonical value : actor) {
                this.actor.add(value);
            }
            return this;
        }

        /**
         * An actor these requirements are in regard to.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param actor
         *     Actor for these requirements
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder actor(Collection<Canonical> actor) {
            this.actor = new ArrayList<>(actor);
            return this;
        }

        /**
         * The actual statement of requirement, in markdown format.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statement
         *     Actual statement as markdown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder statement(Statement... statement) {
            for (Statement value : statement) {
                this.statement.add(value);
            }
            return this;
        }

        /**
         * The actual statement of requirement, in markdown format.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param statement
         *     Actual statement as markdown
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder statement(Collection<Statement> statement) {
            this.statement = new ArrayList<>(statement);
            return this;
        }

        /**
         * Build the {@link Requirements}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Requirements}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Requirements per the base specification
         */
        @Override
        public Requirements build() {
            Requirements requirements = new Requirements(this);
            if (validating) {
                validate(requirements);
            }
            return requirements;
        }

        protected void validate(Requirements requirements) {
            super.validate(requirements);
            ValidationSupport.checkList(requirements.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(requirements.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(requirements.status, "status");
            ValidationSupport.checkList(requirements.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(requirements.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(requirements.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(requirements.derivedFrom, "derivedFrom", Canonical.class);
            ValidationSupport.checkList(requirements.reference, "reference", Url.class);
            ValidationSupport.checkList(requirements.actor, "actor", Canonical.class);
            ValidationSupport.checkList(requirements.statement, "statement", Statement.class);
        }

        protected Builder from(Requirements requirements) {
            super.from(requirements);
            url = requirements.url;
            identifier.addAll(requirements.identifier);
            version = requirements.version;
            versionAlgorithm = requirements.versionAlgorithm;
            name = requirements.name;
            title = requirements.title;
            status = requirements.status;
            experimental = requirements.experimental;
            date = requirements.date;
            publisher = requirements.publisher;
            contact.addAll(requirements.contact);
            description = requirements.description;
            useContext.addAll(requirements.useContext);
            jurisdiction.addAll(requirements.jurisdiction);
            purpose = requirements.purpose;
            copyright = requirements.copyright;
            copyrightLabel = requirements.copyrightLabel;
            derivedFrom.addAll(requirements.derivedFrom);
            reference.addAll(requirements.reference);
            actor.addAll(requirements.actor);
            statement.addAll(requirements.statement);
            return this;
        }
    }

    /**
     * The actual statement of requirement, in markdown format.
     */
    public static class Statement extends BackboneElement {
        @Required
        private final Id key;
        private final String label;
        @Binding(
            bindingName = "ConformanceExpectation",
            strength = BindingStrength.Value.REQUIRED,
            valueSet = "http://hl7.org/fhir/ValueSet/conformance-expectation|5.0.0"
        )
        private final List<ConformanceExpectation> conformance;
        private final Boolean conditionality;
        @Required
        private final Markdown requirement;
        private final String derivedFrom;
        private final String parent;
        private final List<Url> satisfiedBy;
        private final List<Url> reference;
        @ReferenceTarget({ "CareTeam", "Device", "Group", "HealthcareService", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
        private final List<Reference> source;

        private Statement(Builder builder) {
            super(builder);
            key = builder.key;
            label = builder.label;
            conformance = Collections.unmodifiableList(builder.conformance);
            conditionality = builder.conditionality;
            requirement = builder.requirement;
            derivedFrom = builder.derivedFrom;
            parent = builder.parent;
            satisfiedBy = Collections.unmodifiableList(builder.satisfiedBy);
            reference = Collections.unmodifiableList(builder.reference);
            source = Collections.unmodifiableList(builder.source);
        }

        /**
         * Key that identifies this statement (unique within this resource).
         * 
         * @return
         *     An immutable object of type {@link Id} that is non-null.
         */
        public Id getKey() {
            return key;
        }

        /**
         * A short human usable label for this statement.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getLabel() {
            return label;
        }

        /**
         * A short human usable label for this statement.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link ConformanceExpectation} that may be empty.
         */
        public List<ConformanceExpectation> getConformance() {
            return conformance;
        }

        /**
         * This boolean flag is set to true of the text of the requirement is conditional on something e.g. it includes lanauage 
         * like 'if x then y'. This conditionality flag is introduced for purposes of filtering and colour highlighting etc.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that may be null.
         */
        public Boolean getConditionality() {
            return conditionality;
        }

        /**
         * The actual requirement for human consumption.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that is non-null.
         */
        public Markdown getRequirement() {
            return requirement;
        }

        /**
         * Another statement on one of the requirements that this requirement clarifies or restricts.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDerivedFrom() {
            return derivedFrom;
        }

        /**
         * A larger requirement that this requirement helps to refine and enable.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getParent() {
            return parent;
        }

        /**
         * A reference to another artifact that satisfies this requirement. This could be a Profile, extension, or an element in 
         * one of those, or a CapabilityStatement, OperationDefinition, SearchParameter, CodeSystem(/code), ValueSet, Libary etc.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Url} that may be empty.
         */
        public List<Url> getSatisfiedBy() {
            return satisfiedBy;
        }

        /**
         * A reference to another artifact that created this requirement. This could be a Profile, etc., or external regulation, 
         * or business requirements expressed elsewhere.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Url} that may be empty.
         */
        public List<Url> getReference() {
            return reference;
        }

        /**
         * Who asked for this statement to be a requirement. By default, it's assumed that the publisher knows who it is if it 
         * matters.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getSource() {
            return source;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (key != null) || 
                (label != null) || 
                !conformance.isEmpty() || 
                (conditionality != null) || 
                (requirement != null) || 
                (derivedFrom != null) || 
                (parent != null) || 
                !satisfiedBy.isEmpty() || 
                !reference.isEmpty() || 
                !source.isEmpty();
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
                    accept(key, "key", visitor);
                    accept(label, "label", visitor);
                    accept(conformance, "conformance", visitor, ConformanceExpectation.class);
                    accept(conditionality, "conditionality", visitor);
                    accept(requirement, "requirement", visitor);
                    accept(derivedFrom, "derivedFrom", visitor);
                    accept(parent, "parent", visitor);
                    accept(satisfiedBy, "satisfiedBy", visitor, Url.class);
                    accept(reference, "reference", visitor, Url.class);
                    accept(source, "source", visitor, Reference.class);
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
            Statement other = (Statement) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(key, other.key) && 
                Objects.equals(label, other.label) && 
                Objects.equals(conformance, other.conformance) && 
                Objects.equals(conditionality, other.conditionality) && 
                Objects.equals(requirement, other.requirement) && 
                Objects.equals(derivedFrom, other.derivedFrom) && 
                Objects.equals(parent, other.parent) && 
                Objects.equals(satisfiedBy, other.satisfiedBy) && 
                Objects.equals(reference, other.reference) && 
                Objects.equals(source, other.source);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    key, 
                    label, 
                    conformance, 
                    conditionality, 
                    requirement, 
                    derivedFrom, 
                    parent, 
                    satisfiedBy, 
                    reference, 
                    source);
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
            private Id key;
            private String label;
            private List<ConformanceExpectation> conformance = new ArrayList<>();
            private Boolean conditionality;
            private Markdown requirement;
            private String derivedFrom;
            private String parent;
            private List<Url> satisfiedBy = new ArrayList<>();
            private List<Url> reference = new ArrayList<>();
            private List<Reference> source = new ArrayList<>();

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
             * Key that identifies this statement (unique within this resource).
             * 
             * <p>This element is required.
             * 
             * @param key
             *     Key that identifies this statement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder key(Id key) {
                this.key = key;
                return this;
            }

            /**
             * Convenience method for setting {@code label}.
             * 
             * @param label
             *     Short Human label for this statement
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
             * A short human usable label for this statement.
             * 
             * @param label
             *     Short Human label for this statement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder label(String label) {
                this.label = label;
                return this;
            }

            /**
             * A short human usable label for this statement.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param conformance
             *     SHALL | SHOULD | MAY | SHOULD-NOT
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder conformance(ConformanceExpectation... conformance) {
                for (ConformanceExpectation value : conformance) {
                    this.conformance.add(value);
                }
                return this;
            }

            /**
             * A short human usable label for this statement.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param conformance
             *     SHALL | SHOULD | MAY | SHOULD-NOT
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder conformance(Collection<ConformanceExpectation> conformance) {
                this.conformance = new ArrayList<>(conformance);
                return this;
            }

            /**
             * Convenience method for setting {@code conditionality}.
             * 
             * @param conditionality
             *     Set to true if requirements statement is conditional
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #conditionality(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder conditionality(java.lang.Boolean conditionality) {
                this.conditionality = (conditionality == null) ? null : Boolean.of(conditionality);
                return this;
            }

            /**
             * This boolean flag is set to true of the text of the requirement is conditional on something e.g. it includes lanauage 
             * like 'if x then y'. This conditionality flag is introduced for purposes of filtering and colour highlighting etc.
             * 
             * @param conditionality
             *     Set to true if requirements statement is conditional
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder conditionality(Boolean conditionality) {
                this.conditionality = conditionality;
                return this;
            }

            /**
             * The actual requirement for human consumption.
             * 
             * <p>This element is required.
             * 
             * @param requirement
             *     The actual requirement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder requirement(Markdown requirement) {
                this.requirement = requirement;
                return this;
            }

            /**
             * Convenience method for setting {@code derivedFrom}.
             * 
             * @param derivedFrom
             *     Another statement this clarifies/restricts ([url#]key)
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #derivedFrom(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder derivedFrom(java.lang.String derivedFrom) {
                this.derivedFrom = (derivedFrom == null) ? null : String.of(derivedFrom);
                return this;
            }

            /**
             * Another statement on one of the requirements that this requirement clarifies or restricts.
             * 
             * @param derivedFrom
             *     Another statement this clarifies/restricts ([url#]key)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder derivedFrom(String derivedFrom) {
                this.derivedFrom = derivedFrom;
                return this;
            }

            /**
             * Convenience method for setting {@code parent}.
             * 
             * @param parent
             *     A larger requirement that this requirement helps to refine and enable
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #parent(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder parent(java.lang.String parent) {
                this.parent = (parent == null) ? null : String.of(parent);
                return this;
            }

            /**
             * A larger requirement that this requirement helps to refine and enable.
             * 
             * @param parent
             *     A larger requirement that this requirement helps to refine and enable
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder parent(String parent) {
                this.parent = parent;
                return this;
            }

            /**
             * A reference to another artifact that satisfies this requirement. This could be a Profile, extension, or an element in 
             * one of those, or a CapabilityStatement, OperationDefinition, SearchParameter, CodeSystem(/code), ValueSet, Libary etc.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param satisfiedBy
             *     Design artifact that satisfies this requirement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder satisfiedBy(Url... satisfiedBy) {
                for (Url value : satisfiedBy) {
                    this.satisfiedBy.add(value);
                }
                return this;
            }

            /**
             * A reference to another artifact that satisfies this requirement. This could be a Profile, extension, or an element in 
             * one of those, or a CapabilityStatement, OperationDefinition, SearchParameter, CodeSystem(/code), ValueSet, Libary etc.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param satisfiedBy
             *     Design artifact that satisfies this requirement
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder satisfiedBy(Collection<Url> satisfiedBy) {
                this.satisfiedBy = new ArrayList<>(satisfiedBy);
                return this;
            }

            /**
             * A reference to another artifact that created this requirement. This could be a Profile, etc., or external regulation, 
             * or business requirements expressed elsewhere.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param reference
             *     External artifact (rule/document etc. that) created this requirement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reference(Url... reference) {
                for (Url value : reference) {
                    this.reference.add(value);
                }
                return this;
            }

            /**
             * A reference to another artifact that created this requirement. This could be a Profile, etc., or external regulation, 
             * or business requirements expressed elsewhere.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param reference
             *     External artifact (rule/document etc. that) created this requirement
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder reference(Collection<Url> reference) {
                this.reference = new ArrayList<>(reference);
                return this;
            }

            /**
             * Who asked for this statement to be a requirement. By default, it's assumed that the publisher knows who it is if it 
             * matters.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link CareTeam}</li>
             * <li>{@link Device}</li>
             * <li>{@link Group}</li>
             * <li>{@link HealthcareService}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Patient}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param source
             *     Who asked for this statement
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder source(Reference... source) {
                for (Reference value : source) {
                    this.source.add(value);
                }
                return this;
            }

            /**
             * Who asked for this statement to be a requirement. By default, it's assumed that the publisher knows who it is if it 
             * matters.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link CareTeam}</li>
             * <li>{@link Device}</li>
             * <li>{@link Group}</li>
             * <li>{@link HealthcareService}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Patient}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param source
             *     Who asked for this statement
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder source(Collection<Reference> source) {
                this.source = new ArrayList<>(source);
                return this;
            }

            /**
             * Build the {@link Statement}
             * 
             * <p>Required elements:
             * <ul>
             * <li>key</li>
             * <li>requirement</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Statement}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Statement per the base specification
             */
            @Override
            public Statement build() {
                Statement statement = new Statement(this);
                if (validating) {
                    validate(statement);
                }
                return statement;
            }

            protected void validate(Statement statement) {
                super.validate(statement);
                ValidationSupport.requireNonNull(statement.key, "key");
                ValidationSupport.checkList(statement.conformance, "conformance", ConformanceExpectation.class);
                ValidationSupport.requireNonNull(statement.requirement, "requirement");
                ValidationSupport.checkList(statement.satisfiedBy, "satisfiedBy", Url.class);
                ValidationSupport.checkList(statement.reference, "reference", Url.class);
                ValidationSupport.checkList(statement.source, "source", Reference.class);
                ValidationSupport.checkReferenceType(statement.source, "source", "CareTeam", "Device", "Group", "HealthcareService", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
                ValidationSupport.requireValueOrChildren(statement);
            }

            protected Builder from(Statement statement) {
                super.from(statement);
                key = statement.key;
                label = statement.label;
                conformance.addAll(statement.conformance);
                conditionality = statement.conditionality;
                requirement = statement.requirement;
                derivedFrom = statement.derivedFrom;
                parent = statement.parent;
                satisfiedBy.addAll(statement.satisfiedBy);
                reference.addAll(statement.reference);
                source.addAll(statement.source);
                return this;
            }
        }
    }
}
