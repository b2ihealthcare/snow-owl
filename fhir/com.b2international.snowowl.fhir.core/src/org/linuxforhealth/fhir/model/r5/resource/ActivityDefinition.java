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
import org.linuxforhealth.fhir.model.r5.type.Age;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.ContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Dosage;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Expression;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.SimpleQuantity;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.ActivityDefinitionKind;
import org.linuxforhealth.fhir.model.r5.type.code.ActivityParticipantType;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.RequestIntent;
import org.linuxforhealth.fhir.model.r5.type.code.RequestPriority;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This resource allows for the definition of some activity to be performed, independent of a particular patient, 
 * practitioner, or other performance context.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "cnl-0",
    level = "Warning",
    location = "(base)",
    description = "Name should be usable as an identifier for the module by machine processing applications such as code generation",
    expression = "name.exists() implies name.matches('^[A-Z]([A-Za-z0-9_]){1,254}$')",
    source = "http://hl7.org/fhir/StructureDefinition/ActivityDefinition"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "ActivityDefinition.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/ActivityDefinition"
)
@Constraint(
    id = "activityDefinition-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/ActivityDefinition",
    generated = true
)
@Constraint(
    id = "activityDefinition-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/participant-resource-types",
    expression = "subject.as(CodeableConcept).exists() implies (subject.as(CodeableConcept).memberOf('http://hl7.org/fhir/ValueSet/participant-resource-types', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/ActivityDefinition",
    generated = true
)
@Constraint(
    id = "activityDefinition-4",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/ActivityDefinition",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ActivityDefinition extends DomainResource {
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
    private final String subtitle;
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
    @ReferenceTarget({ "Group", "MedicinalProductDefinition", "SubstanceDefinition", "AdministrableProductDefinition", "ManufacturedItemDefinition", "PackagedProductDefinition", "EvidenceVariable" })
    @Choice({ CodeableConcept.class, Reference.class, Canonical.class })
    @Binding(
        bindingName = "SubjectType",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "The possible types of subjects for an activity (E.g. Patient, Practitioner, Organization, Location, etc.).",
        valueSet = "http://hl7.org/fhir/ValueSet/participant-resource-types"
    )
    private final Element subject;
    @Summary
    private final DateTime date;
    @Summary
    private final String publisher;
    @Summary
    private final List<ContactDetail> contact;
    @Summary
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
    private final Markdown usage;
    private final Markdown copyright;
    private final String copyrightLabel;
    private final Date approvalDate;
    private final Date lastReviewDate;
    @Summary
    private final Period effectivePeriod;
    @Binding(
        bindingName = "DefinitionTopic",
        strength = BindingStrength.Value.EXAMPLE,
        description = "High-level categorization of the definition, used for searching, sorting, and filtering.",
        valueSet = "http://hl7.org/fhir/ValueSet/definition-topic"
    )
    private final List<CodeableConcept> topic;
    private final List<ContactDetail> author;
    private final List<ContactDetail> editor;
    private final List<ContactDetail> reviewer;
    private final List<ContactDetail> endorser;
    private final List<RelatedArtifact> relatedArtifact;
    private final List<Canonical> library;
    @Summary
    @Binding(
        bindingName = "ActivityDefinitionKind",
        strength = BindingStrength.Value.REQUIRED,
        description = "The kind of activity the definition is describing.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-resource-types|5.0.0"
    )
    private final ActivityDefinitionKind kind;
    private final Canonical profile;
    @Summary
    @Binding(
        bindingName = "ActivityDefinitionType",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Detailed type of the activity; e.g. CBC.",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-code"
    )
    private final CodeableConcept code;
    @Binding(
        bindingName = "RequestIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes indicating the degree of authority/intentionality associated with a request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-intent|5.0.0"
    )
    private final RequestIntent intent;
    @Binding(
        bindingName = "RequestPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "Identifies the level of importance to be assigned to actioning the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final RequestPriority priority;
    @Summary
    private final Boolean doNotPerform;
    @Choice({ Timing.class, Age.class, Range.class, Duration.class })
    private final Element timing;
    @Summary
    @Choice({ Boolean.class, CodeableConcept.class })
    @Binding(
        bindingName = "ProcedureAsNeededReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A coded concept identifying the pre-condition that should hold prior to performing a procedure.  For example \"pain\", \"on flare-up\", etc.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-as-needed-reason"
    )
    private final Element asNeeded;
    private final CodeableReference location;
    private final List<Participant> participant;
    @ReferenceTarget({ "Medication", "Ingredient", "Substance", "SubstanceDefinition" })
    @Choice({ Reference.class, CodeableConcept.class })
    @Binding(
        bindingName = "ActivityProduct",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Code describing the type of substance or medication.",
        valueSet = "http://hl7.org/fhir/ValueSet/medication-codes"
    )
    private final Element product;
    private final SimpleQuantity quantity;
    private final List<Dosage> dosage;
    @Binding(
        bindingName = "BodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A code that identifies the anatomical location.",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final List<CodeableConcept> bodySite;
    private final List<Canonical> specimenRequirement;
    private final List<Canonical> observationRequirement;
    private final List<Canonical> observationResultRequirement;
    private final Canonical transform;
    private final List<DynamicValue> dynamicValue;

    private ActivityDefinition(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        versionAlgorithm = builder.versionAlgorithm;
        name = builder.name;
        title = builder.title;
        subtitle = builder.subtitle;
        status = builder.status;
        experimental = builder.experimental;
        subject = builder.subject;
        date = builder.date;
        publisher = builder.publisher;
        contact = Collections.unmodifiableList(builder.contact);
        description = builder.description;
        useContext = Collections.unmodifiableList(builder.useContext);
        jurisdiction = Collections.unmodifiableList(builder.jurisdiction);
        purpose = builder.purpose;
        usage = builder.usage;
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
        library = Collections.unmodifiableList(builder.library);
        kind = builder.kind;
        profile = builder.profile;
        code = builder.code;
        intent = builder.intent;
        priority = builder.priority;
        doNotPerform = builder.doNotPerform;
        timing = builder.timing;
        asNeeded = builder.asNeeded;
        location = builder.location;
        participant = Collections.unmodifiableList(builder.participant);
        product = builder.product;
        quantity = builder.quantity;
        dosage = Collections.unmodifiableList(builder.dosage);
        bodySite = Collections.unmodifiableList(builder.bodySite);
        specimenRequirement = Collections.unmodifiableList(builder.specimenRequirement);
        observationRequirement = Collections.unmodifiableList(builder.observationRequirement);
        observationResultRequirement = Collections.unmodifiableList(builder.observationResultRequirement);
        transform = builder.transform;
        dynamicValue = Collections.unmodifiableList(builder.dynamicValue);
    }

    /**
     * An absolute URI that is used to identify this activity definition when it is referenced in a specification, model, 
     * design or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal 
     * address at which an authoritative instance of this activity definition is (or will be) published. This URL can be the 
     * target of a canonical reference. It SHALL remain the same when the activity definition is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this activity definition when it is represented in other formats, or 
     * referenced in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the activity definition when it is referenced in a 
     * specification, model, design or instance. This is an arbitrary value managed by the activity definition author and is 
     * not expected to be globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not 
     * available. There is also no expectation that versions can be placed in a lexicographical sequence. To provide a 
     * version consistent with the Decision Support Service specification, use the format Major.Minor.Revision (e.g. 1.0.0). 
     * For more information on versioning knowledge assets, refer to the Decision Support Service specification. Note that a 
     * version is required for non-experimental active assets.
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
     * A natural language name identifying the activity definition. This name should be usable as an identifier for the 
     * module by machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the activity definition.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * An explanatory or alternate title for the activity definition giving additional information about its content.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * The status of this activity definition. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this activity definition is authored for testing purposes (or 
     * education/evaluation/marketing) and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * A code, group definition, or canonical reference that describes or identifies the intended subject of the activity 
     * being defined. Canonical references are allowed to support the definition of protocols for drug and substance quality 
     * specifications, and is allowed to reference a MedicinalProductDefinition, SubstanceDefinition, 
     * AdministrableProductDefinition, ManufacturedItemDefinition, or PackagedProductDefinition resource.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept}, {@link Reference} or {@link Canonical} that may be null.
     */
    public Element getSubject() {
        return subject;
    }

    /**
     * The date (and optionally time) when the activity definition was last significantly changed. The date must change when 
     * the business version changes and it must change if the status code changes. In addition, it should change when the 
     * substantive content of the activity definition changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual responsible for the release and ongoing maintenance of the activity 
     * definition.
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
     * A free text natural language description of the activity definition from a consumer's perspective.
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
     * may be used to assist with indexing and searching for appropriate activity definition instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A legal or geographic region in which the activity definition is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explanation of why this activity definition is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * A detailed description of how the activity definition is used from a clinical perspective.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getUsage() {
        return usage;
    }

    /**
     * A copyright statement relating to the activity definition and/or its contents. Copyright statements are generally 
     * legal restrictions on the use and publishing of the activity definition.
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
     * The period during which the activity definition content was or is planned to be in active use.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * Descriptive topics related to the content of the activity. Topics provide a high-level categorization of the activity 
     * that can be useful for filtering and searching.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getTopic() {
        return topic;
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
     * A reference to a Library resource containing any formal logic used by the activity definition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getLibrary() {
        return library;
    }

    /**
     * A description of the kind of resource the activity definition is representing. For example, a MedicationRequest, a 
     * ServiceRequest, or a CommunicationRequest.
     * 
     * @return
     *     An immutable object of type {@link ActivityDefinitionKind} that may be null.
     */
    public ActivityDefinitionKind getKind() {
        return kind;
    }

    /**
     * A profile to which the target of the activity definition is expected to conform.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getProfile() {
        return profile;
    }

    /**
     * Detailed description of the type of activity; e.g. What lab test, what procedure, what kind of encounter.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * Indicates the level of authority/intentionality associated with the activity and where the request should fit into the 
     * workflow chain.
     * 
     * @return
     *     An immutable object of type {@link RequestIntent} that may be null.
     */
    public RequestIntent getIntent() {
        return intent;
    }

    /**
     * Indicates how quickly the activity should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link RequestPriority} that may be null.
     */
    public RequestPriority getPriority() {
        return priority;
    }

    /**
     * Set this to true if the definition is to indicate that a particular activity should NOT be performed. If true, this 
     * element should be interpreted to reinforce a negative coding. For example NPO as a code with a doNotPerform of true 
     * would still indicate to NOT perform the action.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getDoNotPerform() {
        return doNotPerform;
    }

    /**
     * The timing or frequency upon which the described activity is to occur.
     * 
     * @return
     *     An immutable object of type {@link Timing}, {@link Age}, {@link Range} or {@link Duration} that may be null.
     */
    public Element getTiming() {
        return timing;
    }

    /**
     * If a CodeableConcept is present, it indicates the pre-condition for performing the service. For example "pain", "on 
     * flare-up", etc.
     * 
     * @return
     *     An immutable object of type {@link Boolean} or {@link CodeableConcept} that may be null.
     */
    public Element getAsNeeded() {
        return asNeeded;
    }

    /**
     * Identifies the facility where the activity will occur; e.g. home, hospital, specific clinic, etc.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getLocation() {
        return location;
    }

    /**
     * Indicates who should participate in performing the action described.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Participant} that may be empty.
     */
    public List<Participant> getParticipant() {
        return participant;
    }

    /**
     * Identifies the food, drug or other product being consumed or supplied in the activity.
     * 
     * @return
     *     An immutable object of type {@link Reference} or {@link CodeableConcept} that may be null.
     */
    public Element getProduct() {
        return product;
    }

    /**
     * Identifies the quantity expected to be consumed at once (per dose, per meal, etc.).
     * 
     * @return
     *     An immutable object of type {@link SimpleQuantity} that may be null.
     */
    public SimpleQuantity getQuantity() {
        return quantity;
    }

    /**
     * Provides detailed dosage instructions in the same way that they are described for MedicationRequest resources.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Dosage} that may be empty.
     */
    public List<Dosage> getDosage() {
        return dosage;
    }

    /**
     * Indicates the sites on the subject's body where the procedure should be performed (I.e. the target sites).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getBodySite() {
        return bodySite;
    }

    /**
     * Defines specimen requirements for the action to be performed, such as required specimens for a lab test.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getSpecimenRequirement() {
        return specimenRequirement;
    }

    /**
     * Defines observation requirements for the action to be performed, such as body weight or surface area.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getObservationRequirement() {
        return observationRequirement;
    }

    /**
     * Defines the observations that are expected to be produced by the action.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getObservationResultRequirement() {
        return observationResultRequirement;
    }

    /**
     * A reference to a StructureMap resource that defines a transform that can be executed to produce the intent resource 
     * using the ActivityDefinition instance as the input.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getTransform() {
        return transform;
    }

    /**
     * Dynamic values that will be evaluated to produce values for elements of the resulting resource. For example, if the 
     * dosage of a medication must be computed based on the patient's weight, a dynamic value would be used to specify an 
     * expression that calculated the weight, and the path on the request resource that would contain the result.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link DynamicValue} that may be empty.
     */
    public List<DynamicValue> getDynamicValue() {
        return dynamicValue;
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
            (subtitle != null) || 
            (status != null) || 
            (experimental != null) || 
            (subject != null) || 
            (date != null) || 
            (publisher != null) || 
            !contact.isEmpty() || 
            (description != null) || 
            !useContext.isEmpty() || 
            !jurisdiction.isEmpty() || 
            (purpose != null) || 
            (usage != null) || 
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
            !library.isEmpty() || 
            (kind != null) || 
            (profile != null) || 
            (code != null) || 
            (intent != null) || 
            (priority != null) || 
            (doNotPerform != null) || 
            (timing != null) || 
            (asNeeded != null) || 
            (location != null) || 
            !participant.isEmpty() || 
            (product != null) || 
            (quantity != null) || 
            !dosage.isEmpty() || 
            !bodySite.isEmpty() || 
            !specimenRequirement.isEmpty() || 
            !observationRequirement.isEmpty() || 
            !observationResultRequirement.isEmpty() || 
            (transform != null) || 
            !dynamicValue.isEmpty();
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
                accept(subtitle, "subtitle", visitor);
                accept(status, "status", visitor);
                accept(experimental, "experimental", visitor);
                accept(subject, "subject", visitor);
                accept(date, "date", visitor);
                accept(publisher, "publisher", visitor);
                accept(contact, "contact", visitor, ContactDetail.class);
                accept(description, "description", visitor);
                accept(useContext, "useContext", visitor, UsageContext.class);
                accept(jurisdiction, "jurisdiction", visitor, CodeableConcept.class);
                accept(purpose, "purpose", visitor);
                accept(usage, "usage", visitor);
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
                accept(library, "library", visitor, Canonical.class);
                accept(kind, "kind", visitor);
                accept(profile, "profile", visitor);
                accept(code, "code", visitor);
                accept(intent, "intent", visitor);
                accept(priority, "priority", visitor);
                accept(doNotPerform, "doNotPerform", visitor);
                accept(timing, "timing", visitor);
                accept(asNeeded, "asNeeded", visitor);
                accept(location, "location", visitor);
                accept(participant, "participant", visitor, Participant.class);
                accept(product, "product", visitor);
                accept(quantity, "quantity", visitor);
                accept(dosage, "dosage", visitor, Dosage.class);
                accept(bodySite, "bodySite", visitor, CodeableConcept.class);
                accept(specimenRequirement, "specimenRequirement", visitor, Canonical.class);
                accept(observationRequirement, "observationRequirement", visitor, Canonical.class);
                accept(observationResultRequirement, "observationResultRequirement", visitor, Canonical.class);
                accept(transform, "transform", visitor);
                accept(dynamicValue, "dynamicValue", visitor, DynamicValue.class);
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
        ActivityDefinition other = (ActivityDefinition) obj;
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
            Objects.equals(subtitle, other.subtitle) && 
            Objects.equals(status, other.status) && 
            Objects.equals(experimental, other.experimental) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(date, other.date) && 
            Objects.equals(publisher, other.publisher) && 
            Objects.equals(contact, other.contact) && 
            Objects.equals(description, other.description) && 
            Objects.equals(useContext, other.useContext) && 
            Objects.equals(jurisdiction, other.jurisdiction) && 
            Objects.equals(purpose, other.purpose) && 
            Objects.equals(usage, other.usage) && 
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
            Objects.equals(library, other.library) && 
            Objects.equals(kind, other.kind) && 
            Objects.equals(profile, other.profile) && 
            Objects.equals(code, other.code) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(doNotPerform, other.doNotPerform) && 
            Objects.equals(timing, other.timing) && 
            Objects.equals(asNeeded, other.asNeeded) && 
            Objects.equals(location, other.location) && 
            Objects.equals(participant, other.participant) && 
            Objects.equals(product, other.product) && 
            Objects.equals(quantity, other.quantity) && 
            Objects.equals(dosage, other.dosage) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(specimenRequirement, other.specimenRequirement) && 
            Objects.equals(observationRequirement, other.observationRequirement) && 
            Objects.equals(observationResultRequirement, other.observationResultRequirement) && 
            Objects.equals(transform, other.transform) && 
            Objects.equals(dynamicValue, other.dynamicValue);
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
                subtitle, 
                status, 
                experimental, 
                subject, 
                date, 
                publisher, 
                contact, 
                description, 
                useContext, 
                jurisdiction, 
                purpose, 
                usage, 
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
                library, 
                kind, 
                profile, 
                code, 
                intent, 
                priority, 
                doNotPerform, 
                timing, 
                asNeeded, 
                location, 
                participant, 
                product, 
                quantity, 
                dosage, 
                bodySite, 
                specimenRequirement, 
                observationRequirement, 
                observationResultRequirement, 
                transform, 
                dynamicValue);
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
        private String subtitle;
        private PublicationStatus status;
        private Boolean experimental;
        private Element subject;
        private DateTime date;
        private String publisher;
        private List<ContactDetail> contact = new ArrayList<>();
        private Markdown description;
        private List<UsageContext> useContext = new ArrayList<>();
        private List<CodeableConcept> jurisdiction = new ArrayList<>();
        private Markdown purpose;
        private Markdown usage;
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
        private List<Canonical> library = new ArrayList<>();
        private ActivityDefinitionKind kind;
        private Canonical profile;
        private CodeableConcept code;
        private RequestIntent intent;
        private RequestPriority priority;
        private Boolean doNotPerform;
        private Element timing;
        private Element asNeeded;
        private CodeableReference location;
        private List<Participant> participant = new ArrayList<>();
        private Element product;
        private SimpleQuantity quantity;
        private List<Dosage> dosage = new ArrayList<>();
        private List<CodeableConcept> bodySite = new ArrayList<>();
        private List<Canonical> specimenRequirement = new ArrayList<>();
        private List<Canonical> observationRequirement = new ArrayList<>();
        private List<Canonical> observationResultRequirement = new ArrayList<>();
        private Canonical transform;
        private List<DynamicValue> dynamicValue = new ArrayList<>();

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
         * An absolute URI that is used to identify this activity definition when it is referenced in a specification, model, 
         * design or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal 
         * address at which an authoritative instance of this activity definition is (or will be) published. This URL can be the 
         * target of a canonical reference. It SHALL remain the same when the activity definition is stored on different servers.
         * 
         * @param url
         *     Canonical identifier for this activity definition, represented as a URI (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A formal identifier that is used to identify this activity definition when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the activity definition
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
         * A formal identifier that is used to identify this activity definition when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the activity definition
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
         *     Business version of the activity definition
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
         * The identifier that is used to identify this version of the activity definition when it is referenced in a 
         * specification, model, design or instance. This is an arbitrary value managed by the activity definition author and is 
         * not expected to be globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not 
         * available. There is also no expectation that versions can be placed in a lexicographical sequence. To provide a 
         * version consistent with the Decision Support Service specification, use the format Major.Minor.Revision (e.g. 1.0.0). 
         * For more information on versioning knowledge assets, refer to the Decision Support Service specification. Note that a 
         * version is required for non-experimental active assets.
         * 
         * @param version
         *     Business version of the activity definition
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
         *     Name for this activity definition (computer friendly)
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
         * A natural language name identifying the activity definition. This name should be usable as an identifier for the 
         * module by machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this activity definition (computer friendly)
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
         *     Name for this activity definition (human friendly)
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
         * A short, descriptive, user-friendly title for the activity definition.
         * 
         * @param title
         *     Name for this activity definition (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Convenience method for setting {@code subtitle}.
         * 
         * @param subtitle
         *     Subordinate title of the activity definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #subtitle(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder subtitle(java.lang.String subtitle) {
            this.subtitle = (subtitle == null) ? null : String.of(subtitle);
            return this;
        }

        /**
         * An explanatory or alternate title for the activity definition giving additional information about its content.
         * 
         * @param subtitle
         *     Subordinate title of the activity definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        /**
         * The status of this activity definition. Enables tracking the life-cycle of the content.
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
         * A Boolean value to indicate that this activity definition is authored for testing purposes (or 
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
         * A code, group definition, or canonical reference that describes or identifies the intended subject of the activity 
         * being defined. Canonical references are allowed to support the definition of protocols for drug and substance quality 
         * specifications, and is allowed to reference a MedicinalProductDefinition, SubstanceDefinition, 
         * AdministrableProductDefinition, ManufacturedItemDefinition, or PackagedProductDefinition resource.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link CodeableConcept}</li>
         * <li>{@link Reference}</li>
         * <li>{@link Canonical}</li>
         * </ul>
         * 
         * When of type {@link Reference}, the allowed resource types for this reference are:
         * <ul>
         * <li>{@link Group}</li>
         * <li>{@link MedicinalProductDefinition}</li>
         * <li>{@link SubstanceDefinition}</li>
         * <li>{@link AdministrableProductDefinition}</li>
         * <li>{@link ManufacturedItemDefinition}</li>
         * <li>{@link PackagedProductDefinition}</li>
         * <li>{@link EvidenceVariable}</li>
         * </ul>
         * 
         * @param subject
         *     Type of individual the activity definition is intended for
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Element subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The date (and optionally time) when the activity definition was last significantly changed. The date must change when 
         * the business version changes and it must change if the status code changes. In addition, it should change when the 
         * substantive content of the activity definition changes.
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
         * The name of the organization or individual responsible for the release and ongoing maintenance of the activity 
         * definition.
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
         * A free text natural language description of the activity definition from a consumer's perspective.
         * 
         * @param description
         *     Natural language description of the activity definition
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
         * may be used to assist with indexing and searching for appropriate activity definition instances.
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
         * may be used to assist with indexing and searching for appropriate activity definition instances.
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
         * A legal or geographic region in which the activity definition is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for activity definition (if applicable)
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
         * A legal or geographic region in which the activity definition is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for activity definition (if applicable)
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
         * Explanation of why this activity definition is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this activity definition is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * A detailed description of how the activity definition is used from a clinical perspective.
         * 
         * @param usage
         *     Describes the clinical usage of the activity definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder usage(Markdown usage) {
            this.usage = usage;
            return this;
        }

        /**
         * A copyright statement relating to the activity definition and/or its contents. Copyright statements are generally 
         * legal restrictions on the use and publishing of the activity definition.
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
         *     When the activity definition was approved by publisher
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
         *     When the activity definition was approved by publisher
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
         *     When the activity definition was last reviewed by the publisher
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
         *     When the activity definition was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
        }

        /**
         * The period during which the activity definition content was or is planned to be in active use.
         * 
         * @param effectivePeriod
         *     When the activity definition is expected to be used
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effectivePeriod(Period effectivePeriod) {
            this.effectivePeriod = effectivePeriod;
            return this;
        }

        /**
         * Descriptive topics related to the content of the activity. Topics provide a high-level categorization of the activity 
         * that can be useful for filtering and searching.
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
         * Descriptive topics related to the content of the activity. Topics provide a high-level categorization of the activity 
         * that can be useful for filtering and searching.
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
         * A reference to a Library resource containing any formal logic used by the activity definition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param library
         *     Logic used by the activity definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder library(Canonical... library) {
            for (Canonical value : library) {
                this.library.add(value);
            }
            return this;
        }

        /**
         * A reference to a Library resource containing any formal logic used by the activity definition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param library
         *     Logic used by the activity definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder library(Collection<Canonical> library) {
            this.library = new ArrayList<>(library);
            return this;
        }

        /**
         * A description of the kind of resource the activity definition is representing. For example, a MedicationRequest, a 
         * ServiceRequest, or a CommunicationRequest.
         * 
         * @param kind
         *     Kind of resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder kind(ActivityDefinitionKind kind) {
            this.kind = kind;
            return this;
        }

        /**
         * A profile to which the target of the activity definition is expected to conform.
         * 
         * @param profile
         *     What profile the resource needs to conform to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder profile(Canonical profile) {
            this.profile = profile;
            return this;
        }

        /**
         * Detailed description of the type of activity; e.g. What lab test, what procedure, what kind of encounter.
         * 
         * @param code
         *     Detail type of activity
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * Indicates the level of authority/intentionality associated with the activity and where the request should fit into the 
         * workflow chain.
         * 
         * @param intent
         *     proposal | plan | directive | order | original-order | reflex-order | filler-order | instance-order | option
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(RequestIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * Indicates how quickly the activity should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(RequestPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Convenience method for setting {@code doNotPerform}.
         * 
         * @param doNotPerform
         *     True if the activity should not be performed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #doNotPerform(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder doNotPerform(java.lang.Boolean doNotPerform) {
            this.doNotPerform = (doNotPerform == null) ? null : Boolean.of(doNotPerform);
            return this;
        }

        /**
         * Set this to true if the definition is to indicate that a particular activity should NOT be performed. If true, this 
         * element should be interpreted to reinforce a negative coding. For example NPO as a code with a doNotPerform of true 
         * would still indicate to NOT perform the action.
         * 
         * @param doNotPerform
         *     True if the activity should not be performed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder doNotPerform(Boolean doNotPerform) {
            this.doNotPerform = doNotPerform;
            return this;
        }

        /**
         * The timing or frequency upon which the described activity is to occur.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Timing}</li>
         * <li>{@link Age}</li>
         * <li>{@link Range}</li>
         * <li>{@link Duration}</li>
         * </ul>
         * 
         * @param timing
         *     When activity is to occur
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder timing(Element timing) {
            this.timing = timing;
            return this;
        }

        /**
         * Convenience method for setting {@code asNeeded} with choice type Boolean.
         * 
         * @param asNeeded
         *     Preconditions for service
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #asNeeded(Element)
         */
        public Builder asNeeded(java.lang.Boolean asNeeded) {
            this.asNeeded = (asNeeded == null) ? null : Boolean.of(asNeeded);
            return this;
        }

        /**
         * If a CodeableConcept is present, it indicates the pre-condition for performing the service. For example "pain", "on 
         * flare-up", etc.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Boolean}</li>
         * <li>{@link CodeableConcept}</li>
         * </ul>
         * 
         * @param asNeeded
         *     Preconditions for service
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder asNeeded(Element asNeeded) {
            this.asNeeded = asNeeded;
            return this;
        }

        /**
         * Identifies the facility where the activity will occur; e.g. home, hospital, specific clinic, etc.
         * 
         * @param location
         *     Where it should happen
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(CodeableReference location) {
            this.location = location;
            return this;
        }

        /**
         * Indicates who should participate in performing the action described.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     Who should participate in the action
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder participant(Participant... participant) {
            for (Participant value : participant) {
                this.participant.add(value);
            }
            return this;
        }

        /**
         * Indicates who should participate in performing the action described.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param participant
         *     Who should participate in the action
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder participant(Collection<Participant> participant) {
            this.participant = new ArrayList<>(participant);
            return this;
        }

        /**
         * Identifies the food, drug or other product being consumed or supplied in the activity.
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link Reference}</li>
         * <li>{@link CodeableConcept}</li>
         * </ul>
         * 
         * When of type {@link Reference}, the allowed resource types for this reference are:
         * <ul>
         * <li>{@link Medication}</li>
         * <li>{@link Ingredient}</li>
         * <li>{@link Substance}</li>
         * <li>{@link SubstanceDefinition}</li>
         * </ul>
         * 
         * @param product
         *     What's administered/supplied
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder product(Element product) {
            this.product = product;
            return this;
        }

        /**
         * Identifies the quantity expected to be consumed at once (per dose, per meal, etc.).
         * 
         * @param quantity
         *     How much is administered/consumed/supplied
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder quantity(SimpleQuantity quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Provides detailed dosage instructions in the same way that they are described for MedicationRequest resources.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dosage
         *     Detailed dosage instructions
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dosage(Dosage... dosage) {
            for (Dosage value : dosage) {
                this.dosage.add(value);
            }
            return this;
        }

        /**
         * Provides detailed dosage instructions in the same way that they are described for MedicationRequest resources.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dosage
         *     Detailed dosage instructions
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder dosage(Collection<Dosage> dosage) {
            this.dosage = new ArrayList<>(dosage);
            return this;
        }

        /**
         * Indicates the sites on the subject's body where the procedure should be performed (I.e. the target sites).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     What part of body to perform on
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodySite(CodeableConcept... bodySite) {
            for (CodeableConcept value : bodySite) {
                this.bodySite.add(value);
            }
            return this;
        }

        /**
         * Indicates the sites on the subject's body where the procedure should be performed (I.e. the target sites).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param bodySite
         *     What part of body to perform on
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder bodySite(Collection<CodeableConcept> bodySite) {
            this.bodySite = new ArrayList<>(bodySite);
            return this;
        }

        /**
         * Defines specimen requirements for the action to be performed, such as required specimens for a lab test.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specimenRequirement
         *     What specimens are required to perform this action
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specimenRequirement(Canonical... specimenRequirement) {
            for (Canonical value : specimenRequirement) {
                this.specimenRequirement.add(value);
            }
            return this;
        }

        /**
         * Defines specimen requirements for the action to be performed, such as required specimens for a lab test.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param specimenRequirement
         *     What specimens are required to perform this action
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder specimenRequirement(Collection<Canonical> specimenRequirement) {
            this.specimenRequirement = new ArrayList<>(specimenRequirement);
            return this;
        }

        /**
         * Defines observation requirements for the action to be performed, such as body weight or surface area.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param observationRequirement
         *     What observations are required to perform this action
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder observationRequirement(Canonical... observationRequirement) {
            for (Canonical value : observationRequirement) {
                this.observationRequirement.add(value);
            }
            return this;
        }

        /**
         * Defines observation requirements for the action to be performed, such as body weight or surface area.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param observationRequirement
         *     What observations are required to perform this action
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder observationRequirement(Collection<Canonical> observationRequirement) {
            this.observationRequirement = new ArrayList<>(observationRequirement);
            return this;
        }

        /**
         * Defines the observations that are expected to be produced by the action.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param observationResultRequirement
         *     What observations must be produced by this action
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder observationResultRequirement(Canonical... observationResultRequirement) {
            for (Canonical value : observationResultRequirement) {
                this.observationResultRequirement.add(value);
            }
            return this;
        }

        /**
         * Defines the observations that are expected to be produced by the action.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param observationResultRequirement
         *     What observations must be produced by this action
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder observationResultRequirement(Collection<Canonical> observationResultRequirement) {
            this.observationResultRequirement = new ArrayList<>(observationResultRequirement);
            return this;
        }

        /**
         * A reference to a StructureMap resource that defines a transform that can be executed to produce the intent resource 
         * using the ActivityDefinition instance as the input.
         * 
         * @param transform
         *     Transform to apply the template
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder transform(Canonical transform) {
            this.transform = transform;
            return this;
        }

        /**
         * Dynamic values that will be evaluated to produce values for elements of the resulting resource. For example, if the 
         * dosage of a medication must be computed based on the patient's weight, a dynamic value would be used to specify an 
         * expression that calculated the weight, and the path on the request resource that would contain the result.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dynamicValue
         *     Dynamic aspects of the definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder dynamicValue(DynamicValue... dynamicValue) {
            for (DynamicValue value : dynamicValue) {
                this.dynamicValue.add(value);
            }
            return this;
        }

        /**
         * Dynamic values that will be evaluated to produce values for elements of the resulting resource. For example, if the 
         * dosage of a medication must be computed based on the patient's weight, a dynamic value would be used to specify an 
         * expression that calculated the weight, and the path on the request resource that would contain the result.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param dynamicValue
         *     Dynamic aspects of the definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder dynamicValue(Collection<DynamicValue> dynamicValue) {
            this.dynamicValue = new ArrayList<>(dynamicValue);
            return this;
        }

        /**
         * Build the {@link ActivityDefinition}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ActivityDefinition}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ActivityDefinition per the base specification
         */
        @Override
        public ActivityDefinition build() {
            ActivityDefinition activityDefinition = new ActivityDefinition(this);
            if (validating) {
                validate(activityDefinition);
            }
            return activityDefinition;
        }

        protected void validate(ActivityDefinition activityDefinition) {
            super.validate(activityDefinition);
            ValidationSupport.checkList(activityDefinition.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(activityDefinition.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(activityDefinition.status, "status");
            ValidationSupport.choiceElement(activityDefinition.subject, "subject", CodeableConcept.class, Reference.class, Canonical.class);
            ValidationSupport.checkList(activityDefinition.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(activityDefinition.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(activityDefinition.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(activityDefinition.topic, "topic", CodeableConcept.class);
            ValidationSupport.checkList(activityDefinition.author, "author", ContactDetail.class);
            ValidationSupport.checkList(activityDefinition.editor, "editor", ContactDetail.class);
            ValidationSupport.checkList(activityDefinition.reviewer, "reviewer", ContactDetail.class);
            ValidationSupport.checkList(activityDefinition.endorser, "endorser", ContactDetail.class);
            ValidationSupport.checkList(activityDefinition.relatedArtifact, "relatedArtifact", RelatedArtifact.class);
            ValidationSupport.checkList(activityDefinition.library, "library", Canonical.class);
            ValidationSupport.choiceElement(activityDefinition.timing, "timing", Timing.class, Age.class, Range.class, Duration.class);
            ValidationSupport.choiceElement(activityDefinition.asNeeded, "asNeeded", Boolean.class, CodeableConcept.class);
            ValidationSupport.checkList(activityDefinition.participant, "participant", Participant.class);
            ValidationSupport.choiceElement(activityDefinition.product, "product", Reference.class, CodeableConcept.class);
            ValidationSupport.checkList(activityDefinition.dosage, "dosage", Dosage.class);
            ValidationSupport.checkList(activityDefinition.bodySite, "bodySite", CodeableConcept.class);
            ValidationSupport.checkList(activityDefinition.specimenRequirement, "specimenRequirement", Canonical.class);
            ValidationSupport.checkList(activityDefinition.observationRequirement, "observationRequirement", Canonical.class);
            ValidationSupport.checkList(activityDefinition.observationResultRequirement, "observationResultRequirement", Canonical.class);
            ValidationSupport.checkList(activityDefinition.dynamicValue, "dynamicValue", DynamicValue.class);
            ValidationSupport.checkReferenceType(activityDefinition.subject, "subject", "Group", "MedicinalProductDefinition", "SubstanceDefinition", "AdministrableProductDefinition", "ManufacturedItemDefinition", "PackagedProductDefinition", "EvidenceVariable");
            ValidationSupport.checkReferenceType(activityDefinition.product, "product", "Medication", "Ingredient", "Substance", "SubstanceDefinition");
        }

        protected Builder from(ActivityDefinition activityDefinition) {
            super.from(activityDefinition);
            url = activityDefinition.url;
            identifier.addAll(activityDefinition.identifier);
            version = activityDefinition.version;
            versionAlgorithm = activityDefinition.versionAlgorithm;
            name = activityDefinition.name;
            title = activityDefinition.title;
            subtitle = activityDefinition.subtitle;
            status = activityDefinition.status;
            experimental = activityDefinition.experimental;
            subject = activityDefinition.subject;
            date = activityDefinition.date;
            publisher = activityDefinition.publisher;
            contact.addAll(activityDefinition.contact);
            description = activityDefinition.description;
            useContext.addAll(activityDefinition.useContext);
            jurisdiction.addAll(activityDefinition.jurisdiction);
            purpose = activityDefinition.purpose;
            usage = activityDefinition.usage;
            copyright = activityDefinition.copyright;
            copyrightLabel = activityDefinition.copyrightLabel;
            approvalDate = activityDefinition.approvalDate;
            lastReviewDate = activityDefinition.lastReviewDate;
            effectivePeriod = activityDefinition.effectivePeriod;
            topic.addAll(activityDefinition.topic);
            author.addAll(activityDefinition.author);
            editor.addAll(activityDefinition.editor);
            reviewer.addAll(activityDefinition.reviewer);
            endorser.addAll(activityDefinition.endorser);
            relatedArtifact.addAll(activityDefinition.relatedArtifact);
            library.addAll(activityDefinition.library);
            kind = activityDefinition.kind;
            profile = activityDefinition.profile;
            code = activityDefinition.code;
            intent = activityDefinition.intent;
            priority = activityDefinition.priority;
            doNotPerform = activityDefinition.doNotPerform;
            timing = activityDefinition.timing;
            asNeeded = activityDefinition.asNeeded;
            location = activityDefinition.location;
            participant.addAll(activityDefinition.participant);
            product = activityDefinition.product;
            quantity = activityDefinition.quantity;
            dosage.addAll(activityDefinition.dosage);
            bodySite.addAll(activityDefinition.bodySite);
            specimenRequirement.addAll(activityDefinition.specimenRequirement);
            observationRequirement.addAll(activityDefinition.observationRequirement);
            observationResultRequirement.addAll(activityDefinition.observationResultRequirement);
            transform = activityDefinition.transform;
            dynamicValue.addAll(activityDefinition.dynamicValue);
            return this;
        }
    }

    /**
     * Indicates who should participate in performing the action described.
     */
    public static class Participant extends BackboneElement {
        @Binding(
            bindingName = "ActivityParticipantType",
            strength = BindingStrength.Value.REQUIRED,
            description = "The type of participant in the activity.",
            valueSet = "http://hl7.org/fhir/ValueSet/action-participant-type|5.0.0"
        )
        private final ActivityParticipantType type;
        private final Canonical typeCanonical;
        @ReferenceTarget({ "CareTeam", "Device", "DeviceDefinition", "Endpoint", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson" })
        private final Reference typeReference;
        @Binding(
            bindingName = "ActivityParticipantRole",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Defines roles played by participants for the action.",
            valueSet = "http://terminology.hl7.org/ValueSet/action-participant-role"
        )
        private final CodeableConcept role;
        @Binding(
            bindingName = "ActionParticipantFunction",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/action-participant-function"
        )
        private final CodeableConcept function;

        private Participant(Builder builder) {
            super(builder);
            type = builder.type;
            typeCanonical = builder.typeCanonical;
            typeReference = builder.typeReference;
            role = builder.role;
            function = builder.function;
        }

        /**
         * The type of participant in the action.
         * 
         * @return
         *     An immutable object of type {@link ActivityParticipantType} that may be null.
         */
        public ActivityParticipantType getType() {
            return type;
        }

        /**
         * The type of participant in the action.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getTypeCanonical() {
            return typeCanonical;
        }

        /**
         * The type of participant in the action.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getTypeReference() {
            return typeReference;
        }

        /**
         * The role the participant should play in performing the described action.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getRole() {
            return role;
        }

        /**
         * Indicates how the actor will be involved in the action - author, reviewer, witness, etc.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getFunction() {
            return function;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                (typeCanonical != null) || 
                (typeReference != null) || 
                (role != null) || 
                (function != null);
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
                    accept(typeCanonical, "typeCanonical", visitor);
                    accept(typeReference, "typeReference", visitor);
                    accept(role, "role", visitor);
                    accept(function, "function", visitor);
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
            Participant other = (Participant) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(typeCanonical, other.typeCanonical) && 
                Objects.equals(typeReference, other.typeReference) && 
                Objects.equals(role, other.role) && 
                Objects.equals(function, other.function);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    typeCanonical, 
                    typeReference, 
                    role, 
                    function);
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
            private ActivityParticipantType type;
            private Canonical typeCanonical;
            private Reference typeReference;
            private CodeableConcept role;
            private CodeableConcept function;

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
             * The type of participant in the action.
             * 
             * @param type
             *     careteam | device | group | healthcareservice | location | organization | patient | practitioner | practitionerrole | 
             *     relatedperson
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(ActivityParticipantType type) {
                this.type = type;
                return this;
            }

            /**
             * The type of participant in the action.
             * 
             * @param typeCanonical
             *     Who or what can participate
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder typeCanonical(Canonical typeCanonical) {
                this.typeCanonical = typeCanonical;
                return this;
            }

            /**
             * The type of participant in the action.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link CareTeam}</li>
             * <li>{@link Device}</li>
             * <li>{@link DeviceDefinition}</li>
             * <li>{@link Endpoint}</li>
             * <li>{@link Group}</li>
             * <li>{@link HealthcareService}</li>
             * <li>{@link Location}</li>
             * <li>{@link Organization}</li>
             * <li>{@link Patient}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param typeReference
             *     Who or what can participate
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder typeReference(Reference typeReference) {
                this.typeReference = typeReference;
                return this;
            }

            /**
             * The role the participant should play in performing the described action.
             * 
             * @param role
             *     E.g. Nurse, Surgeon, Parent, etc
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder role(CodeableConcept role) {
                this.role = role;
                return this;
            }

            /**
             * Indicates how the actor will be involved in the action - author, reviewer, witness, etc.
             * 
             * @param function
             *     E.g. Author, Reviewer, Witness, etc
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder function(CodeableConcept function) {
                this.function = function;
                return this;
            }

            /**
             * Build the {@link Participant}
             * 
             * @return
             *     An immutable object of type {@link Participant}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Participant per the base specification
             */
            @Override
            public Participant build() {
                Participant participant = new Participant(this);
                if (validating) {
                    validate(participant);
                }
                return participant;
            }

            protected void validate(Participant participant) {
                super.validate(participant);
                ValidationSupport.checkReferenceType(participant.typeReference, "typeReference", "CareTeam", "Device", "DeviceDefinition", "Endpoint", "Group", "HealthcareService", "Location", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson");
                ValidationSupport.requireValueOrChildren(participant);
            }

            protected Builder from(Participant participant) {
                super.from(participant);
                type = participant.type;
                typeCanonical = participant.typeCanonical;
                typeReference = participant.typeReference;
                role = participant.role;
                function = participant.function;
                return this;
            }
        }
    }

    /**
     * Dynamic values that will be evaluated to produce values for elements of the resulting resource. For example, if the 
     * dosage of a medication must be computed based on the patient's weight, a dynamic value would be used to specify an 
     * expression that calculated the weight, and the path on the request resource that would contain the result.
     */
    public static class DynamicValue extends BackboneElement {
        @Required
        private final String path;
        @Required
        private final Expression expression;

        private DynamicValue(Builder builder) {
            super(builder);
            path = builder.path;
            expression = builder.expression;
        }

        /**
         * The path to the element to be customized. This is the path on the resource that will hold the result of the 
         * calculation defined by the expression. The specified path SHALL be a FHIRPath resolvable on the specified target type 
         * of the ActivityDefinition, and SHALL consist only of identifiers, constant indexers, and a restricted subset of 
         * functions. The path is allowed to contain qualifiers (.) to traverse sub-elements, as well as indexers ([x]) to 
         * traverse multiple-cardinality sub-elements (see the [Simple FHIRPath Profile](fhirpath.html#simple) for full details).
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getPath() {
            return path;
        }

        /**
         * An expression specifying the value of the customized element.
         * 
         * @return
         *     An immutable object of type {@link Expression} that is non-null.
         */
        public Expression getExpression() {
            return expression;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (path != null) || 
                (expression != null);
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
                    accept(path, "path", visitor);
                    accept(expression, "expression", visitor);
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
            DynamicValue other = (DynamicValue) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(path, other.path) && 
                Objects.equals(expression, other.expression);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    path, 
                    expression);
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
            private String path;
            private Expression expression;

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
             * Convenience method for setting {@code path}.
             * 
             * <p>This element is required.
             * 
             * @param path
             *     The path to the element to be set dynamically
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #path(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder path(java.lang.String path) {
                this.path = (path == null) ? null : String.of(path);
                return this;
            }

            /**
             * The path to the element to be customized. This is the path on the resource that will hold the result of the 
             * calculation defined by the expression. The specified path SHALL be a FHIRPath resolvable on the specified target type 
             * of the ActivityDefinition, and SHALL consist only of identifiers, constant indexers, and a restricted subset of 
             * functions. The path is allowed to contain qualifiers (.) to traverse sub-elements, as well as indexers ([x]) to 
             * traverse multiple-cardinality sub-elements (see the [Simple FHIRPath Profile](fhirpath.html#simple) for full details).
             * 
             * <p>This element is required.
             * 
             * @param path
             *     The path to the element to be set dynamically
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder path(String path) {
                this.path = path;
                return this;
            }

            /**
             * An expression specifying the value of the customized element.
             * 
             * <p>This element is required.
             * 
             * @param expression
             *     An expression that provides the dynamic value for the customization
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder expression(Expression expression) {
                this.expression = expression;
                return this;
            }

            /**
             * Build the {@link DynamicValue}
             * 
             * <p>Required elements:
             * <ul>
             * <li>path</li>
             * <li>expression</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link DynamicValue}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid DynamicValue per the base specification
             */
            @Override
            public DynamicValue build() {
                DynamicValue dynamicValue = new DynamicValue(this);
                if (validating) {
                    validate(dynamicValue);
                }
                return dynamicValue;
            }

            protected void validate(DynamicValue dynamicValue) {
                super.validate(dynamicValue);
                ValidationSupport.requireNonNull(dynamicValue.path, "path");
                ValidationSupport.requireNonNull(dynamicValue.expression, "expression");
                ValidationSupport.requireValueOrChildren(dynamicValue);
            }

            protected Builder from(DynamicValue dynamicValue) {
                super.from(dynamicValue);
                path = dynamicValue.path;
                expression = dynamicValue.expression;
                return this;
            }
        }
    }
}
