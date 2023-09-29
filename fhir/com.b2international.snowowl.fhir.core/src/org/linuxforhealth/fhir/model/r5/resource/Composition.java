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
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.CompositionStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A set of healthcare-related information that is assembled together into a single logical package that provides a 
 * single coherent statement of meaning, establishes its own context and that has clinical attestation with regard to who 
 * is making the statement. A Composition defines the structure and narrative content necessary for a document. However, 
 * a Composition alone does not constitute a document. Rather, the Composition must be the first entry in a Bundle where 
 * Bundle.type=document, and any other resources referenced from Composition must be included as subsequent entries in 
 * the Bundle (for example Patient, Practitioner, Encounter, etc.).
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "cmp-1",
    level = "Rule",
    location = "Composition.section",
    description = "A section must contain at least one of text, entries, or sub-sections",
    expression = "text.exists() or entry.exists() or section.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/Composition"
)
@Constraint(
    id = "cmp-2",
    level = "Rule",
    location = "Composition.section",
    description = "A section can only have an emptyReason if it is empty",
    expression = "emptyReason.empty() or entry.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/Composition"
)
@Constraint(
    id = "composition-3",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/doc-typecodes",
    expression = "type.exists() and type.memberOf('http://hl7.org/fhir/ValueSet/doc-typecodes', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Composition",
    generated = true
)
@Constraint(
    id = "composition-4",
    level = "Warning",
    location = "attester.mode",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/composition-attestation-mode",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/composition-attestation-mode', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Composition",
    generated = true
)
@Constraint(
    id = "composition-5",
    level = "Warning",
    location = "section.orderedBy",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/list-order",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/list-order', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Composition",
    generated = true
)
@Constraint(
    id = "composition-6",
    level = "Warning",
    location = "section.emptyReason",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/list-empty-reason",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/list-empty-reason', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Composition",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Composition extends DomainResource {
    @Summary
    private final Uri url;
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final String version;
    @Summary
    @Binding(
        bindingName = "CompositionStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The workflow/clinical status of the composition.",
        valueSet = "http://hl7.org/fhir/ValueSet/composition-status|5.0.0"
    )
    @Required
    private final CompositionStatus status;
    @Summary
    @Binding(
        bindingName = "DocumentType",
        strength = BindingStrength.Value.PREFERRED,
        description = "Type of a composition.",
        valueSet = "http://hl7.org/fhir/ValueSet/doc-typecodes"
    )
    @Required
    private final CodeableConcept type;
    @Summary
    @Binding(
        bindingName = "DocumentCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "High-level kind of a clinical document at a macro level.",
        valueSet = "http://hl7.org/fhir/ValueSet/referenced-item-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    private final List<Reference> subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Required
    private final DateTime date;
    @Summary
    private final List<UsageContext> useContext;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Device", "Patient", "RelatedPerson", "Organization" })
    @Required
    private final List<Reference> author;
    @Summary
    private final String name;
    @Summary
    @Required
    private final String title;
    private final List<Annotation> note;
    private final List<Attester> attester;
    @Summary
    @ReferenceTarget({ "Organization" })
    private final Reference custodian;
    private final List<RelatedArtifact> relatesTo;
    @Summary
    private final List<Event> event;
    private final List<Section> section;

    private Composition(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        status = builder.status;
        type = builder.type;
        category = Collections.unmodifiableList(builder.category);
        subject = Collections.unmodifiableList(builder.subject);
        encounter = builder.encounter;
        date = builder.date;
        useContext = Collections.unmodifiableList(builder.useContext);
        author = Collections.unmodifiableList(builder.author);
        name = builder.name;
        title = builder.title;
        note = Collections.unmodifiableList(builder.note);
        attester = Collections.unmodifiableList(builder.attester);
        custodian = builder.custodian;
        relatesTo = Collections.unmodifiableList(builder.relatesTo);
        event = Collections.unmodifiableList(builder.event);
        section = Collections.unmodifiableList(builder.section);
    }

    /**
     * An absolute URI that is used to identify this Composition when it is referenced in a specification, model, design or 
     * an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
     * which an authoritative instance of this Composition is (or will be) published. This URL can be the target of a 
     * canonical reference. It SHALL remain the same when the Composition is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A version-independent identifier for the Composition. This identifier stays constant as the composition is changed 
     * over time.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * An explicitly assigned identifer of a variation of the content in the Composition.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getVersion() {
        return version;
    }

    /**
     * The workflow/clinical status of this composition. The status is a marker for the clinical standing of the document.
     * 
     * @return
     *     An immutable object of type {@link CompositionStatus} that is non-null.
     */
    public CompositionStatus getStatus() {
        return status;
    }

    /**
     * Specifies the particular kind of composition (e.g. History and Physical, Discharge Summary, Progress Note). This 
     * usually equates to the purpose of making the composition.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getType() {
        return type;
    }

    /**
     * A categorization for the type of the composition - helps for indexing and searching. This may be implied by or derived 
     * from the code specified in the Composition Type.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Who or what the composition is about. The composition can be about a person, (patient or healthcare practitioner), a 
     * device (e.g. a machine) or even a group of subjects (such as a document about a herd of livestock, or a set of 
     * patients that share a common exposure).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSubject() {
        return subject;
    }

    /**
     * Describes the clinical encounter or type of care this documentation is associated with.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * The composition editing time, when the composition was last logically changed by the author.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that is non-null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
     * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
     * may be used to assist with indexing and searching for appropriate Composition instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * Identifies who is responsible for the information in the composition, not necessarily who typed it in.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that is non-empty.
     */
    public List<Reference> getAuthor() {
        return author;
    }

    /**
     * A natural language name identifying the {{title}}. This name should be usable as an identifier for the module by 
     * machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * Official human-readable label for the composition.
     * 
     * @return
     *     An immutable object of type {@link String} that is non-null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * For any additional notes.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * A participant who has attested to the accuracy of the composition/document.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Attester} that may be empty.
     */
    public List<Attester> getAttester() {
        return attester;
    }

    /**
     * Identifies the organization or group who is responsible for ongoing maintenance of and access to the 
     * composition/document information.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getCustodian() {
        return custodian;
    }

    /**
     * Relationships that this composition has with other compositions or documents that already exist.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link RelatedArtifact} that may be empty.
     */
    public List<RelatedArtifact> getRelatesTo() {
        return relatesTo;
    }

    /**
     * The clinical service, such as a colonoscopy or an appendectomy, being documented.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Event} that may be empty.
     */
    public List<Event> getEvent() {
        return event;
    }

    /**
     * The root of the sections that make up the composition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Section} that may be empty.
     */
    public List<Section> getSection() {
        return section;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (url != null) || 
            !identifier.isEmpty() || 
            (version != null) || 
            (status != null) || 
            (type != null) || 
            !category.isEmpty() || 
            !subject.isEmpty() || 
            (encounter != null) || 
            (date != null) || 
            !useContext.isEmpty() || 
            !author.isEmpty() || 
            (name != null) || 
            (title != null) || 
            !note.isEmpty() || 
            !attester.isEmpty() || 
            (custodian != null) || 
            !relatesTo.isEmpty() || 
            !event.isEmpty() || 
            !section.isEmpty();
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
                accept(status, "status", visitor);
                accept(type, "type", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(subject, "subject", visitor, Reference.class);
                accept(encounter, "encounter", visitor);
                accept(date, "date", visitor);
                accept(useContext, "useContext", visitor, UsageContext.class);
                accept(author, "author", visitor, Reference.class);
                accept(name, "name", visitor);
                accept(title, "title", visitor);
                accept(note, "note", visitor, Annotation.class);
                accept(attester, "attester", visitor, Attester.class);
                accept(custodian, "custodian", visitor);
                accept(relatesTo, "relatesTo", visitor, RelatedArtifact.class);
                accept(event, "event", visitor, Event.class);
                accept(section, "section", visitor, Section.class);
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
        Composition other = (Composition) obj;
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
            Objects.equals(status, other.status) && 
            Objects.equals(type, other.type) && 
            Objects.equals(category, other.category) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(date, other.date) && 
            Objects.equals(useContext, other.useContext) && 
            Objects.equals(author, other.author) && 
            Objects.equals(name, other.name) && 
            Objects.equals(title, other.title) && 
            Objects.equals(note, other.note) && 
            Objects.equals(attester, other.attester) && 
            Objects.equals(custodian, other.custodian) && 
            Objects.equals(relatesTo, other.relatesTo) && 
            Objects.equals(event, other.event) && 
            Objects.equals(section, other.section);
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
                status, 
                type, 
                category, 
                subject, 
                encounter, 
                date, 
                useContext, 
                author, 
                name, 
                title, 
                note, 
                attester, 
                custodian, 
                relatesTo, 
                event, 
                section);
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
        private CompositionStatus status;
        private CodeableConcept type;
        private List<CodeableConcept> category = new ArrayList<>();
        private List<Reference> subject = new ArrayList<>();
        private Reference encounter;
        private DateTime date;
        private List<UsageContext> useContext = new ArrayList<>();
        private List<Reference> author = new ArrayList<>();
        private String name;
        private String title;
        private List<Annotation> note = new ArrayList<>();
        private List<Attester> attester = new ArrayList<>();
        private Reference custodian;
        private List<RelatedArtifact> relatesTo = new ArrayList<>();
        private List<Event> event = new ArrayList<>();
        private List<Section> section = new ArrayList<>();

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
         * An absolute URI that is used to identify this Composition when it is referenced in a specification, model, design or 
         * an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address at 
         * which an authoritative instance of this Composition is (or will be) published. This URL can be the target of a 
         * canonical reference. It SHALL remain the same when the Composition is stored on different servers.
         * 
         * @param url
         *     Canonical identifier for this Composition, represented as a URI (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A version-independent identifier for the Composition. This identifier stays constant as the composition is changed 
         * over time.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Version-independent identifier for the Composition
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
         * A version-independent identifier for the Composition. This identifier stays constant as the composition is changed 
         * over time.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Version-independent identifier for the Composition
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
         *     An explicitly assigned identifer of a variation of the content in the Composition
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
         * An explicitly assigned identifer of a variation of the content in the Composition.
         * 
         * @param version
         *     An explicitly assigned identifer of a variation of the content in the Composition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * The workflow/clinical status of this composition. The status is a marker for the clinical standing of the document.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     registered | partial | preliminary | final | amended | corrected | appended | cancelled | entered-in-error | 
         *     deprecated | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(CompositionStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Specifies the particular kind of composition (e.g. History and Physical, Discharge Summary, Progress Note). This 
         * usually equates to the purpose of making the composition.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     Kind of composition (LOINC if possible)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(CodeableConcept type) {
            this.type = type;
            return this;
        }

        /**
         * A categorization for the type of the composition - helps for indexing and searching. This may be implied by or derived 
         * from the code specified in the Composition Type.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Categorization of Composition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder category(CodeableConcept... category) {
            for (CodeableConcept value : category) {
                this.category.add(value);
            }
            return this;
        }

        /**
         * A categorization for the type of the composition - helps for indexing and searching. This may be implied by or derived 
         * from the code specified in the Composition Type.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Categorization of Composition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder category(Collection<CodeableConcept> category) {
            this.category = new ArrayList<>(category);
            return this;
        }

        /**
         * Who or what the composition is about. The composition can be about a person, (patient or healthcare practitioner), a 
         * device (e.g. a machine) or even a group of subjects (such as a document about a herd of livestock, or a set of 
         * patients that share a common exposure).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subject
         *     Who and/or what the composition is about
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference... subject) {
            for (Reference value : subject) {
                this.subject.add(value);
            }
            return this;
        }

        /**
         * Who or what the composition is about. The composition can be about a person, (patient or healthcare practitioner), a 
         * device (e.g. a machine) or even a group of subjects (such as a document about a herd of livestock, or a set of 
         * patients that share a common exposure).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subject
         *     Who and/or what the composition is about
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder subject(Collection<Reference> subject) {
            this.subject = new ArrayList<>(subject);
            return this;
        }

        /**
         * Describes the clinical encounter or type of care this documentation is associated with.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Context of the Composition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * The composition editing time, when the composition was last logically changed by the author.
         * 
         * <p>This element is required.
         * 
         * @param date
         *     Composition editing time
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(DateTime date) {
            this.date = date;
            return this;
        }

        /**
         * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
         * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
         * may be used to assist with indexing and searching for appropriate Composition instances.
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
         * may be used to assist with indexing and searching for appropriate Composition instances.
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
         * Identifies who is responsible for the information in the composition, not necessarily who typed it in.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Device}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param author
         *     Who and/or what authored the composition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder author(Reference... author) {
            for (Reference value : author) {
                this.author.add(value);
            }
            return this;
        }

        /**
         * Identifies who is responsible for the information in the composition, not necessarily who typed it in.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Device}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param author
         *     Who and/or what authored the composition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder author(Collection<Reference> author) {
            this.author = new ArrayList<>(author);
            return this;
        }

        /**
         * Convenience method for setting {@code name}.
         * 
         * @param name
         *     Name for this Composition (computer friendly)
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
         * A natural language name identifying the {{title}}. This name should be usable as an identifier for the module by 
         * machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this Composition (computer friendly)
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
         * <p>This element is required.
         * 
         * @param title
         *     Human Readable name/title
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
         * Official human-readable label for the composition.
         * 
         * <p>This element is required.
         * 
         * @param title
         *     Human Readable name/title
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * For any additional notes.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     For any additional notes
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
         * For any additional notes.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     For any additional notes
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
         * A participant who has attested to the accuracy of the composition/document.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param attester
         *     Attests to accuracy of composition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder attester(Attester... attester) {
            for (Attester value : attester) {
                this.attester.add(value);
            }
            return this;
        }

        /**
         * A participant who has attested to the accuracy of the composition/document.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param attester
         *     Attests to accuracy of composition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder attester(Collection<Attester> attester) {
            this.attester = new ArrayList<>(attester);
            return this;
        }

        /**
         * Identifies the organization or group who is responsible for ongoing maintenance of and access to the 
         * composition/document information.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param custodian
         *     Organization which maintains the composition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder custodian(Reference custodian) {
            this.custodian = custodian;
            return this;
        }

        /**
         * Relationships that this composition has with other compositions or documents that already exist.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatesTo
         *     Relationships to other compositions/documents
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relatesTo(RelatedArtifact... relatesTo) {
            for (RelatedArtifact value : relatesTo) {
                this.relatesTo.add(value);
            }
            return this;
        }

        /**
         * Relationships that this composition has with other compositions or documents that already exist.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param relatesTo
         *     Relationships to other compositions/documents
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relatesTo(Collection<RelatedArtifact> relatesTo) {
            this.relatesTo = new ArrayList<>(relatesTo);
            return this;
        }

        /**
         * The clinical service, such as a colonoscopy or an appendectomy, being documented.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param event
         *     The clinical service(s) being documented
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder event(Event... event) {
            for (Event value : event) {
                this.event.add(value);
            }
            return this;
        }

        /**
         * The clinical service, such as a colonoscopy or an appendectomy, being documented.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param event
         *     The clinical service(s) being documented
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder event(Collection<Event> event) {
            this.event = new ArrayList<>(event);
            return this;
        }

        /**
         * The root of the sections that make up the composition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param section
         *     Composition is broken into sections
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder section(Section... section) {
            for (Section value : section) {
                this.section.add(value);
            }
            return this;
        }

        /**
         * The root of the sections that make up the composition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param section
         *     Composition is broken into sections
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder section(Collection<Section> section) {
            this.section = new ArrayList<>(section);
            return this;
        }

        /**
         * Build the {@link Composition}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>type</li>
         * <li>date</li>
         * <li>author</li>
         * <li>title</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Composition}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Composition per the base specification
         */
        @Override
        public Composition build() {
            Composition composition = new Composition(this);
            if (validating) {
                validate(composition);
            }
            return composition;
        }

        protected void validate(Composition composition) {
            super.validate(composition);
            ValidationSupport.checkList(composition.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(composition.status, "status");
            ValidationSupport.requireNonNull(composition.type, "type");
            ValidationSupport.checkList(composition.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(composition.subject, "subject", Reference.class);
            ValidationSupport.requireNonNull(composition.date, "date");
            ValidationSupport.checkList(composition.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkNonEmptyList(composition.author, "author", Reference.class);
            ValidationSupport.requireNonNull(composition.title, "title");
            ValidationSupport.checkList(composition.note, "note", Annotation.class);
            ValidationSupport.checkList(composition.attester, "attester", Attester.class);
            ValidationSupport.checkList(composition.relatesTo, "relatesTo", RelatedArtifact.class);
            ValidationSupport.checkList(composition.event, "event", Event.class);
            ValidationSupport.checkList(composition.section, "section", Section.class);
            ValidationSupport.checkReferenceType(composition.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(composition.author, "author", "Practitioner", "PractitionerRole", "Device", "Patient", "RelatedPerson", "Organization");
            ValidationSupport.checkReferenceType(composition.custodian, "custodian", "Organization");
        }

        protected Builder from(Composition composition) {
            super.from(composition);
            url = composition.url;
            identifier.addAll(composition.identifier);
            version = composition.version;
            status = composition.status;
            type = composition.type;
            category.addAll(composition.category);
            subject.addAll(composition.subject);
            encounter = composition.encounter;
            date = composition.date;
            useContext.addAll(composition.useContext);
            author.addAll(composition.author);
            name = composition.name;
            title = composition.title;
            note.addAll(composition.note);
            attester.addAll(composition.attester);
            custodian = composition.custodian;
            relatesTo.addAll(composition.relatesTo);
            event.addAll(composition.event);
            section.addAll(composition.section);
            return this;
        }
    }

    /**
     * A participant who has attested to the accuracy of the composition/document.
     */
    public static class Attester extends BackboneElement {
        @Binding(
            bindingName = "CompositionAttestationMode",
            strength = BindingStrength.Value.PREFERRED,
            description = "The way in which a person authenticated a composition.",
            valueSet = "http://hl7.org/fhir/ValueSet/composition-attestation-mode"
        )
        @Required
        private final CodeableConcept mode;
        private final DateTime time;
        @ReferenceTarget({ "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization" })
        private final Reference party;

        private Attester(Builder builder) {
            super(builder);
            mode = builder.mode;
            time = builder.time;
            party = builder.party;
        }

        /**
         * The type of attestation the authenticator offers.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getMode() {
            return mode;
        }

        /**
         * When the composition was attested by the party.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getTime() {
            return time;
        }

        /**
         * Who attested the composition in the specified way.
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
                (mode != null) || 
                (time != null) || 
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
                    accept(mode, "mode", visitor);
                    accept(time, "time", visitor);
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
            Attester other = (Attester) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(mode, other.mode) && 
                Objects.equals(time, other.time) && 
                Objects.equals(party, other.party);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    mode, 
                    time, 
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
            private CodeableConcept mode;
            private DateTime time;
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
             * The type of attestation the authenticator offers.
             * 
             * <p>This element is required.
             * 
             * @param mode
             *     personal | professional | legal | official
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder mode(CodeableConcept mode) {
                this.mode = mode;
                return this;
            }

            /**
             * When the composition was attested by the party.
             * 
             * @param time
             *     When the composition was attested
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder time(DateTime time) {
                this.time = time;
                return this;
            }

            /**
             * Who attested the composition in the specified way.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param party
             *     Who attested the composition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder party(Reference party) {
                this.party = party;
                return this;
            }

            /**
             * Build the {@link Attester}
             * 
             * <p>Required elements:
             * <ul>
             * <li>mode</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Attester}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Attester per the base specification
             */
            @Override
            public Attester build() {
                Attester attester = new Attester(this);
                if (validating) {
                    validate(attester);
                }
                return attester;
            }

            protected void validate(Attester attester) {
                super.validate(attester);
                ValidationSupport.requireNonNull(attester.mode, "mode");
                ValidationSupport.checkReferenceType(attester.party, "party", "Patient", "RelatedPerson", "Practitioner", "PractitionerRole", "Organization");
                ValidationSupport.requireValueOrChildren(attester);
            }

            protected Builder from(Attester attester) {
                super.from(attester);
                mode = attester.mode;
                time = attester.time;
                party = attester.party;
                return this;
            }
        }
    }

    /**
     * The clinical service, such as a colonoscopy or an appendectomy, being documented.
     */
    public static class Event extends BackboneElement {
        @Summary
        private final Period period;
        @Summary
        @Binding(
            bindingName = "DocumentEventType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "This list of codes represents the main clinical acts being documented.",
            valueSet = "http://terminology.hl7.org/ValueSet/v3-ActCode"
        )
        private final List<CodeableReference> detail;

        private Event(Builder builder) {
            super(builder);
            period = builder.period;
            detail = Collections.unmodifiableList(builder.detail);
        }

        /**
         * The period of time covered by the documentation. There is no assertion that the documentation is a complete 
         * representation for this period, only that it documents events during this time.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getPeriod() {
            return period;
        }

        /**
         * Represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In some cases, the 
         * event is inherent in the typeCode, such as a "History and Physical Report" in which case the procedure being 
         * documented is necessarily a "History and Physical" act. The events may be included as a code or as a reference to an 
         * other resource.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
         */
        public List<CodeableReference> getDetail() {
            return detail;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (period != null) || 
                !detail.isEmpty();
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
                    accept(period, "period", visitor);
                    accept(detail, "detail", visitor, CodeableReference.class);
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
            Event other = (Event) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(period, other.period) && 
                Objects.equals(detail, other.detail);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    period, 
                    detail);
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
            private Period period;
            private List<CodeableReference> detail = new ArrayList<>();

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
             * The period of time covered by the documentation. There is no assertion that the documentation is a complete 
             * representation for this period, only that it documents events during this time.
             * 
             * @param period
             *     The period covered by the documentation
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In some cases, the 
             * event is inherent in the typeCode, such as a "History and Physical Report" in which case the procedure being 
             * documented is necessarily a "History and Physical" act. The events may be included as a code or as a reference to an 
             * other resource.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     The event(s) being documented, as code(s), reference(s), or both
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder detail(CodeableReference... detail) {
                for (CodeableReference value : detail) {
                    this.detail.add(value);
                }
                return this;
            }

            /**
             * Represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In some cases, the 
             * event is inherent in the typeCode, such as a "History and Physical Report" in which case the procedure being 
             * documented is necessarily a "History and Physical" act. The events may be included as a code or as a reference to an 
             * other resource.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param detail
             *     The event(s) being documented, as code(s), reference(s), or both
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder detail(Collection<CodeableReference> detail) {
                this.detail = new ArrayList<>(detail);
                return this;
            }

            /**
             * Build the {@link Event}
             * 
             * @return
             *     An immutable object of type {@link Event}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Event per the base specification
             */
            @Override
            public Event build() {
                Event event = new Event(this);
                if (validating) {
                    validate(event);
                }
                return event;
            }

            protected void validate(Event event) {
                super.validate(event);
                ValidationSupport.checkList(event.detail, "detail", CodeableReference.class);
                ValidationSupport.requireValueOrChildren(event);
            }

            protected Builder from(Event event) {
                super.from(event);
                period = event.period;
                detail.addAll(event.detail);
                return this;
            }
        }
    }

    /**
     * The root of the sections that make up the composition.
     */
    public static class Section extends BackboneElement {
        private final String title;
        @Binding(
            bindingName = "CompositionSectionType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Classification of a section of a composition/document.",
            valueSet = "http://hl7.org/fhir/ValueSet/doc-section-codes"
        )
        private final CodeableConcept code;
        @ReferenceTarget({ "Practitioner", "PractitionerRole", "Device", "Patient", "RelatedPerson", "Organization" })
        private final List<Reference> author;
        private final Reference focus;
        private final Narrative text;
        @Binding(
            bindingName = "SectionEntryOrder",
            strength = BindingStrength.Value.PREFERRED,
            description = "What order applies to the items in the entry.",
            valueSet = "http://hl7.org/fhir/ValueSet/list-order"
        )
        private final CodeableConcept orderedBy;
        private final List<Reference> entry;
        @Binding(
            bindingName = "SectionEmptyReason",
            strength = BindingStrength.Value.PREFERRED,
            description = "If a section is empty, why it is empty.",
            valueSet = "http://hl7.org/fhir/ValueSet/list-empty-reason"
        )
        private final CodeableConcept emptyReason;
        private final List<Composition.Section> section;

        private Section(Builder builder) {
            super(builder);
            title = builder.title;
            code = builder.code;
            author = Collections.unmodifiableList(builder.author);
            focus = builder.focus;
            text = builder.text;
            orderedBy = builder.orderedBy;
            entry = Collections.unmodifiableList(builder.entry);
            emptyReason = builder.emptyReason;
            section = Collections.unmodifiableList(builder.section);
        }

        /**
         * The label for this particular section. This will be part of the rendered content for the document, and is often used 
         * to build a table of contents.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getTitle() {
            return title;
        }

        /**
         * A code identifying the kind of content contained within the section. This must be consistent with the section title.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * Identifies who is responsible for the information in this section, not necessarily who typed it in.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getAuthor() {
            return author;
        }

        /**
         * The actual focus of the section when it is not the subject of the composition, but instead represents something or 
         * someone associated with the subject such as (for a patient subject) a spouse, parent, fetus, or donor. If not focus is 
         * specified, the focus is assumed to be focus of the parent section, or, for a section in the Composition itself, the 
         * subject of the composition. Sections with a focus SHALL only include resources where the logical subject (patient, 
         * subject, focus, etc.) matches the section focus, or the resources have no logical subject (few resources).
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getFocus() {
            return focus;
        }

        /**
         * A human-readable narrative that contains the attested content of the section, used to represent the content of the 
         * resource to a human. The narrative need not encode all the structured data, but is required to contain sufficient 
         * detail to make it "clinically safe" for a human to just read the narrative.
         * 
         * @return
         *     An immutable object of type {@link Narrative} that may be null.
         */
        public Narrative getText() {
            return text;
        }

        /**
         * Specifies the order applied to the items in the section entries.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getOrderedBy() {
            return orderedBy;
        }

        /**
         * A reference to the actual resource from which the narrative in the section is derived.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getEntry() {
            return entry;
        }

        /**
         * If the section is empty, why the list is empty. An empty section typically has some text explaining the empty reason.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getEmptyReason() {
            return emptyReason;
        }

        /**
         * A nested sub-section within this section.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Section} that may be empty.
         */
        public List<Composition.Section> getSection() {
            return section;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (title != null) || 
                (code != null) || 
                !author.isEmpty() || 
                (focus != null) || 
                (text != null) || 
                (orderedBy != null) || 
                !entry.isEmpty() || 
                (emptyReason != null) || 
                !section.isEmpty();
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
                    accept(title, "title", visitor);
                    accept(code, "code", visitor);
                    accept(author, "author", visitor, Reference.class);
                    accept(focus, "focus", visitor);
                    accept(text, "text", visitor);
                    accept(orderedBy, "orderedBy", visitor);
                    accept(entry, "entry", visitor, Reference.class);
                    accept(emptyReason, "emptyReason", visitor);
                    accept(section, "section", visitor, Composition.Section.class);
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
            Section other = (Section) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(title, other.title) && 
                Objects.equals(code, other.code) && 
                Objects.equals(author, other.author) && 
                Objects.equals(focus, other.focus) && 
                Objects.equals(text, other.text) && 
                Objects.equals(orderedBy, other.orderedBy) && 
                Objects.equals(entry, other.entry) && 
                Objects.equals(emptyReason, other.emptyReason) && 
                Objects.equals(section, other.section);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    title, 
                    code, 
                    author, 
                    focus, 
                    text, 
                    orderedBy, 
                    entry, 
                    emptyReason, 
                    section);
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
            private String title;
            private CodeableConcept code;
            private List<Reference> author = new ArrayList<>();
            private Reference focus;
            private Narrative text;
            private CodeableConcept orderedBy;
            private List<Reference> entry = new ArrayList<>();
            private CodeableConcept emptyReason;
            private List<Composition.Section> section = new ArrayList<>();

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
             * Convenience method for setting {@code title}.
             * 
             * @param title
             *     Label for section (e.g. for ToC)
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
             * The label for this particular section. This will be part of the rendered content for the document, and is often used 
             * to build a table of contents.
             * 
             * @param title
             *     Label for section (e.g. for ToC)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder title(String title) {
                this.title = title;
                return this;
            }

            /**
             * A code identifying the kind of content contained within the section. This must be consistent with the section title.
             * 
             * @param code
             *     Classification of section (recommended)
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Identifies who is responsible for the information in this section, not necessarily who typed it in.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Device}</li>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param author
             *     Who and/or what authored the section
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder author(Reference... author) {
                for (Reference value : author) {
                    this.author.add(value);
                }
                return this;
            }

            /**
             * Identifies who is responsible for the information in this section, not necessarily who typed it in.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link Device}</li>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * <li>{@link Organization}</li>
             * </ul>
             * 
             * @param author
             *     Who and/or what authored the section
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder author(Collection<Reference> author) {
                this.author = new ArrayList<>(author);
                return this;
            }

            /**
             * The actual focus of the section when it is not the subject of the composition, but instead represents something or 
             * someone associated with the subject such as (for a patient subject) a spouse, parent, fetus, or donor. If not focus is 
             * specified, the focus is assumed to be focus of the parent section, or, for a section in the Composition itself, the 
             * subject of the composition. Sections with a focus SHALL only include resources where the logical subject (patient, 
             * subject, focus, etc.) matches the section focus, or the resources have no logical subject (few resources).
             * 
             * @param focus
             *     Who/what the section is about, when it is not about the subject of composition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder focus(Reference focus) {
                this.focus = focus;
                return this;
            }

            /**
             * A human-readable narrative that contains the attested content of the section, used to represent the content of the 
             * resource to a human. The narrative need not encode all the structured data, but is required to contain sufficient 
             * detail to make it "clinically safe" for a human to just read the narrative.
             * 
             * @param text
             *     Text summary of the section, for human interpretation
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder text(Narrative text) {
                this.text = text;
                return this;
            }

            /**
             * Specifies the order applied to the items in the section entries.
             * 
             * @param orderedBy
             *     Order of section entries
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder orderedBy(CodeableConcept orderedBy) {
                this.orderedBy = orderedBy;
                return this;
            }

            /**
             * A reference to the actual resource from which the narrative in the section is derived.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param entry
             *     A reference to data that supports this section
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder entry(Reference... entry) {
                for (Reference value : entry) {
                    this.entry.add(value);
                }
                return this;
            }

            /**
             * A reference to the actual resource from which the narrative in the section is derived.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param entry
             *     A reference to data that supports this section
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder entry(Collection<Reference> entry) {
                this.entry = new ArrayList<>(entry);
                return this;
            }

            /**
             * If the section is empty, why the list is empty. An empty section typically has some text explaining the empty reason.
             * 
             * @param emptyReason
             *     Why the section is empty
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder emptyReason(CodeableConcept emptyReason) {
                this.emptyReason = emptyReason;
                return this;
            }

            /**
             * A nested sub-section within this section.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param section
             *     Nested Section
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder section(Composition.Section... section) {
                for (Composition.Section value : section) {
                    this.section.add(value);
                }
                return this;
            }

            /**
             * A nested sub-section within this section.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param section
             *     Nested Section
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder section(Collection<Composition.Section> section) {
                this.section = new ArrayList<>(section);
                return this;
            }

            /**
             * Build the {@link Section}
             * 
             * @return
             *     An immutable object of type {@link Section}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Section per the base specification
             */
            @Override
            public Section build() {
                Section section = new Section(this);
                if (validating) {
                    validate(section);
                }
                return section;
            }

            protected void validate(Section section) {
                super.validate(section);
                ValidationSupport.checkList(section.author, "author", Reference.class);
                ValidationSupport.checkList(section.entry, "entry", Reference.class);
                ValidationSupport.checkList(section.section, "section", Composition.Section.class);
                ValidationSupport.checkReferenceType(section.author, "author", "Practitioner", "PractitionerRole", "Device", "Patient", "RelatedPerson", "Organization");
                ValidationSupport.requireValueOrChildren(section);
            }

            protected Builder from(Section section) {
                super.from(section);
                title = section.title;
                code = section.code;
                author.addAll(section.author);
                focus = section.focus;
                text = section.text;
                orderedBy = section.orderedBy;
                entry.addAll(section.entry);
                emptyReason = section.emptyReason;
                this.section.addAll(section.section);
                return this;
            }
        }
    }
}
