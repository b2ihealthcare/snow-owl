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
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.AdministrativeGender;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ObservationDataType;
import org.linuxforhealth.fhir.model.r5.type.code.ObservationRangeCategory;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Set of definitional characteristics for a kind of observation or measurement produced or consumed by an orderable 
 * health care service.
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
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition"
)
@Constraint(
    id = "obd-0",
    level = "Rule",
    location = "(base)",
    description = "If permittedUnit exists, then permittedDataType=Quantity must exist.",
    expression = "permittedUnit.exists() implies (permittedDataType = 'Quantity').exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition"
)
@Constraint(
    id = "obd-1",
    level = "Rule",
    location = "ObservationDefinition.component",
    description = "If permittedUnit exists, then permittedDataType=Quantity must exist.",
    expression = "permittedUnit.exists() implies (permittedDataType = 'Quantity').exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition"
)
@Constraint(
    id = "observationDefinition-2",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition",
    generated = true
)
@Constraint(
    id = "observationDefinition-3",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition",
    generated = true
)
@Constraint(
    id = "observationDefinition-4",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/ucum-units",
    expression = "permittedUnit.exists() implies (permittedUnit.all(memberOf('http://hl7.org/fhir/ValueSet/ucum-units', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition",
    generated = true
)
@Constraint(
    id = "observationDefinition-5",
    level = "Warning",
    location = "qualifiedValue.context",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/referencerange-meaning",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/referencerange-meaning', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition",
    generated = true
)
@Constraint(
    id = "observationDefinition-6",
    level = "Warning",
    location = "component.permittedUnit",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/ucum-units",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/ucum-units', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/ObservationDefinition",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ObservationDefinition extends DomainResource {
    @Summary
    private final Uri url;
    @Summary
    private final Identifier identifier;
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
        description = "Codes identifying the state of an ObservationDefinition.",
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
        description = "Codes for country, country subdivision and region for indicating where a resource is intended to be used.",
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
    @Summary
    private final List<Canonical> derivedFromCanonical;
    @Summary
    private final List<Uri> derivedFromUri;
    @Summary
    private final List<CodeableConcept> subject;
    @Summary
    private final CodeableConcept performerType;
    @Summary
    @Binding(
        bindingName = "ObservationCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes for high level observation categories.",
        valueSet = "http://hl7.org/fhir/ValueSet/observation-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    @Binding(
        bindingName = "ObservationCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes identifying names of simple observations.",
        valueSet = "http://hl7.org/fhir/ValueSet/observation-codes"
    )
    @Required
    private final CodeableConcept code;
    @Binding(
        bindingName = "ObservationDataType",
        strength = BindingStrength.Value.REQUIRED,
        description = "Permitted data type for observation value.",
        valueSet = "http://hl7.org/fhir/ValueSet/permitted-data-type|5.0.0"
    )
    private final List<ObservationDataType> permittedDataType;
    private final Boolean multipleResultsAllowed;
    @Binding(
        bindingName = "ObservationBodySite",
        strength = BindingStrength.Value.EXAMPLE,
        description = "SNOMED CT body structures.",
        valueSet = "http://hl7.org/fhir/ValueSet/body-site"
    )
    private final CodeableConcept bodySite;
    @Binding(
        bindingName = "ObservationMethod",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Methods for simple observations.",
        valueSet = "http://hl7.org/fhir/ValueSet/observation-methods"
    )
    private final CodeableConcept method;
    @ReferenceTarget({ "SpecimenDefinition" })
    private final List<Reference> specimen;
    @ReferenceTarget({ "DeviceDefinition", "Device" })
    private final List<Reference> device;
    private final String preferredReportName;
    @Binding(
        bindingName = "ObservationUnit",
        strength = BindingStrength.Value.PREFERRED,
        description = "Codes identifying units of measure.",
        valueSet = "http://hl7.org/fhir/ValueSet/ucum-units"
    )
    private final List<Coding> permittedUnit;
    private final List<QualifiedValue> qualifiedValue;
    @ReferenceTarget({ "ObservationDefinition", "Questionnaire" })
    private final List<Reference> hasMember;
    private final List<Component> component;

    private ObservationDefinition(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = builder.identifier;
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
        derivedFromCanonical = Collections.unmodifiableList(builder.derivedFromCanonical);
        derivedFromUri = Collections.unmodifiableList(builder.derivedFromUri);
        subject = Collections.unmodifiableList(builder.subject);
        performerType = builder.performerType;
        category = Collections.unmodifiableList(builder.category);
        code = builder.code;
        permittedDataType = Collections.unmodifiableList(builder.permittedDataType);
        multipleResultsAllowed = builder.multipleResultsAllowed;
        bodySite = builder.bodySite;
        method = builder.method;
        specimen = Collections.unmodifiableList(builder.specimen);
        device = Collections.unmodifiableList(builder.device);
        preferredReportName = builder.preferredReportName;
        permittedUnit = Collections.unmodifiableList(builder.permittedUnit);
        qualifiedValue = Collections.unmodifiableList(builder.qualifiedValue);
        hasMember = Collections.unmodifiableList(builder.hasMember);
        component = Collections.unmodifiableList(builder.component);
    }

    /**
     * An absolute URL that is used to identify this ObservationDefinition when it is referenced in a specification, model, 
     * design or an instance. This SHALL be a URL, SHOULD be globally unique, and SHOULD be an address at which this 
     * ObservationDefinition is (or will be) published. The URL SHOULD include the major version of the 
     * ObservationDefinition. For more information see Technical and Business Versions.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * Business identifiers assigned to this ObservationDefinition. by the performer and/or other systems. These identifiers 
     * remain constant as the resource is updated and propagates from server to server.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the ObservationDefinition when it is referenced in a 
     * specification, model, design or instance. This is an arbitrary value managed by the ObservationDefinition author and 
     * is not expected to be globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is 
     * not available. There is also no expectation that versions are orderable.
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
     * A natural language name identifying the ObservationDefinition. This name should be usable as an identifier for the 
     * module by machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the ObservationDefinition.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The current state of the ObservationDefinition.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A flag to indicate that this ObservationDefinition is authored for testing purposes (or 
     * education/evaluation/marketing), and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The date (and optionally time) when the ObservationDefinition was last significantly changed. The date must change 
     * when the business version changes and it must change if the status code changes. In addition, it should change when 
     * the substantive content of the ObservationDefinition changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * Helps establish the "authority/credibility" of the ObservationDefinition. May also allow for contact.
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
     * A free text natural language description of the ObservationDefinition from the consumer's perspective.
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
     * may be used to assist with indexing and searching for appropriate ObservationDefinition instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A jurisdiction in which the ObservationDefinition is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explains why this ObservationDefinition is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * Copyright statement relating to the ObservationDefinition and/or its contents. Copyright statements are generally 
     * legal restrictions on the use and publishing of the ObservationDefinition.
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
     * The date on which the asset content was approved by the publisher. Approval happens once when the content is 
     * officially approved for usage.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getApprovalDate() {
        return approvalDate;
    }

    /**
     * The date on which the asset content was last reviewed. Review happens periodically after that, but doesn't change the 
     * original approval date.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getLastReviewDate() {
        return lastReviewDate;
    }

    /**
     * The period during which the ObservationDefinition content was or is planned to be effective.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * The canonical URL pointing to another FHIR-defined ObservationDefinition that is adhered to in whole or in part by 
     * this definition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getDerivedFromCanonical() {
        return derivedFromCanonical;
    }

    /**
     * The URL pointing to an externally-defined observation definition, guideline or other definition that is adhered to in 
     * whole or in part by this definition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getDerivedFromUri() {
        return derivedFromUri;
    }

    /**
     * A code that describes the intended kind of subject of Observation instances conforming to this ObservationDefinition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getSubject() {
        return subject;
    }

    /**
     * The type of individual/organization/device that is expected to act upon instances of this definition.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getPerformerType() {
        return performerType;
    }

    /**
     * A code that classifies the general type of observation.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * Describes what will be observed. Sometimes this is called the observation "name".
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that is non-null.
     */
    public CodeableConcept getCode() {
        return code;
    }

    /**
     * The data types allowed for the value element of the instance observations conforming to this ObservationDefinition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link ObservationDataType} that may be empty.
     */
    public List<ObservationDataType> getPermittedDataType() {
        return permittedDataType;
    }

    /**
     * Multiple results allowed for observations conforming to this ObservationDefinition.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getMultipleResultsAllowed() {
        return multipleResultsAllowed;
    }

    /**
     * The site on the subject's body where the observation is to be made.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getBodySite() {
        return bodySite;
    }

    /**
     * The method or technique used to perform the observation.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getMethod() {
        return method;
    }

    /**
     * The kind of specimen that this type of observation is produced on.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSpecimen() {
        return specimen;
    }

    /**
     * The measurement model of device or actual device used to produce observations of this type.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getDevice() {
        return device;
    }

    /**
     * The preferred name to be used when reporting the results of observations conforming to this ObservationDefinition.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getPreferredReportName() {
        return preferredReportName;
    }

    /**
     * Units allowed for the valueQuantity element in the instance observations conforming to this ObservationDefinition.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
     */
    public List<Coding> getPermittedUnit() {
        return permittedUnit;
    }

    /**
     * A set of qualified values associated with a context and a set of conditions - provides a range for quantitative and 
     * ordinal observations and a collection of value sets for qualitative observations.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link QualifiedValue} that may be empty.
     */
    public List<QualifiedValue> getQualifiedValue() {
        return qualifiedValue;
    }

    /**
     * This ObservationDefinition defines a group observation (e.g. a battery, a panel of tests, a set of vital sign 
     * measurements) that includes the target as a member of the group.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getHasMember() {
        return hasMember;
    }

    /**
     * Some observations have multiple component observations, expressed as separate code value pairs.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Component} that may be empty.
     */
    public List<Component> getComponent() {
        return component;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (url != null) || 
            (identifier != null) || 
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
            !derivedFromCanonical.isEmpty() || 
            !derivedFromUri.isEmpty() || 
            !subject.isEmpty() || 
            (performerType != null) || 
            !category.isEmpty() || 
            (code != null) || 
            !permittedDataType.isEmpty() || 
            (multipleResultsAllowed != null) || 
            (bodySite != null) || 
            (method != null) || 
            !specimen.isEmpty() || 
            !device.isEmpty() || 
            (preferredReportName != null) || 
            !permittedUnit.isEmpty() || 
            !qualifiedValue.isEmpty() || 
            !hasMember.isEmpty() || 
            !component.isEmpty();
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
                accept(identifier, "identifier", visitor);
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
                accept(derivedFromCanonical, "derivedFromCanonical", visitor, Canonical.class);
                accept(derivedFromUri, "derivedFromUri", visitor, Uri.class);
                accept(subject, "subject", visitor, CodeableConcept.class);
                accept(performerType, "performerType", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(code, "code", visitor);
                accept(permittedDataType, "permittedDataType", visitor, ObservationDataType.class);
                accept(multipleResultsAllowed, "multipleResultsAllowed", visitor);
                accept(bodySite, "bodySite", visitor);
                accept(method, "method", visitor);
                accept(specimen, "specimen", visitor, Reference.class);
                accept(device, "device", visitor, Reference.class);
                accept(preferredReportName, "preferredReportName", visitor);
                accept(permittedUnit, "permittedUnit", visitor, Coding.class);
                accept(qualifiedValue, "qualifiedValue", visitor, QualifiedValue.class);
                accept(hasMember, "hasMember", visitor, Reference.class);
                accept(component, "component", visitor, Component.class);
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
        ObservationDefinition other = (ObservationDefinition) obj;
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
            Objects.equals(derivedFromCanonical, other.derivedFromCanonical) && 
            Objects.equals(derivedFromUri, other.derivedFromUri) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(performerType, other.performerType) && 
            Objects.equals(category, other.category) && 
            Objects.equals(code, other.code) && 
            Objects.equals(permittedDataType, other.permittedDataType) && 
            Objects.equals(multipleResultsAllowed, other.multipleResultsAllowed) && 
            Objects.equals(bodySite, other.bodySite) && 
            Objects.equals(method, other.method) && 
            Objects.equals(specimen, other.specimen) && 
            Objects.equals(device, other.device) && 
            Objects.equals(preferredReportName, other.preferredReportName) && 
            Objects.equals(permittedUnit, other.permittedUnit) && 
            Objects.equals(qualifiedValue, other.qualifiedValue) && 
            Objects.equals(hasMember, other.hasMember) && 
            Objects.equals(component, other.component);
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
                derivedFromCanonical, 
                derivedFromUri, 
                subject, 
                performerType, 
                category, 
                code, 
                permittedDataType, 
                multipleResultsAllowed, 
                bodySite, 
                method, 
                specimen, 
                device, 
                preferredReportName, 
                permittedUnit, 
                qualifiedValue, 
                hasMember, 
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

    public static class Builder extends DomainResource.Builder {
        private Uri url;
        private Identifier identifier;
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
        private List<Canonical> derivedFromCanonical = new ArrayList<>();
        private List<Uri> derivedFromUri = new ArrayList<>();
        private List<CodeableConcept> subject = new ArrayList<>();
        private CodeableConcept performerType;
        private List<CodeableConcept> category = new ArrayList<>();
        private CodeableConcept code;
        private List<ObservationDataType> permittedDataType = new ArrayList<>();
        private Boolean multipleResultsAllowed;
        private CodeableConcept bodySite;
        private CodeableConcept method;
        private List<Reference> specimen = new ArrayList<>();
        private List<Reference> device = new ArrayList<>();
        private String preferredReportName;
        private List<Coding> permittedUnit = new ArrayList<>();
        private List<QualifiedValue> qualifiedValue = new ArrayList<>();
        private List<Reference> hasMember = new ArrayList<>();
        private List<Component> component = new ArrayList<>();

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
         * An absolute URL that is used to identify this ObservationDefinition when it is referenced in a specification, model, 
         * design or an instance. This SHALL be a URL, SHOULD be globally unique, and SHOULD be an address at which this 
         * ObservationDefinition is (or will be) published. The URL SHOULD include the major version of the 
         * ObservationDefinition. For more information see Technical and Business Versions.
         * 
         * @param url
         *     Logical canonical URL to reference this ObservationDefinition (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * Business identifiers assigned to this ObservationDefinition. by the performer and/or other systems. These identifiers 
         * remain constant as the resource is updated and propagates from server to server.
         * 
         * @param identifier
         *     Business identifier of the ObservationDefinition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder identifier(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * Convenience method for setting {@code version}.
         * 
         * @param version
         *     Business version of the ObservationDefinition
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
         * The identifier that is used to identify this version of the ObservationDefinition when it is referenced in a 
         * specification, model, design or instance. This is an arbitrary value managed by the ObservationDefinition author and 
         * is not expected to be globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is 
         * not available. There is also no expectation that versions are orderable.
         * 
         * @param version
         *     Business version of the ObservationDefinition
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
         *     Name for this ObservationDefinition (computer friendly)
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
         * A natural language name identifying the ObservationDefinition. This name should be usable as an identifier for the 
         * module by machine processing applications such as code generation.
         * 
         * @param name
         *     Name for this ObservationDefinition (computer friendly)
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
         *     Name for this ObservationDefinition (human friendly)
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
         * A short, descriptive, user-friendly title for the ObservationDefinition.
         * 
         * @param title
         *     Name for this ObservationDefinition (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * The current state of the ObservationDefinition.
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
         *     If for testing purposes, not real usage
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
         * A flag to indicate that this ObservationDefinition is authored for testing purposes (or 
         * education/evaluation/marketing), and is not intended to be used for genuine usage.
         * 
         * @param experimental
         *     If for testing purposes, not real usage
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder experimental(Boolean experimental) {
            this.experimental = experimental;
            return this;
        }

        /**
         * The date (and optionally time) when the ObservationDefinition was last significantly changed. The date must change 
         * when the business version changes and it must change if the status code changes. In addition, it should change when 
         * the substantive content of the ObservationDefinition changes.
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
         *     The name of the individual or organization that published the ObservationDefinition
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
         * Helps establish the "authority/credibility" of the ObservationDefinition. May also allow for contact.
         * 
         * @param publisher
         *     The name of the individual or organization that published the ObservationDefinition
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
         * A free text natural language description of the ObservationDefinition from the consumer's perspective.
         * 
         * @param description
         *     Natural language description of the ObservationDefinition
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
         * may be used to assist with indexing and searching for appropriate ObservationDefinition instances.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     Content intends to support these contexts
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
         * may be used to assist with indexing and searching for appropriate ObservationDefinition instances.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param useContext
         *     Content intends to support these contexts
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
         * A jurisdiction in which the ObservationDefinition is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for this ObservationDefinition (if applicable)
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
         * A jurisdiction in which the ObservationDefinition is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for this ObservationDefinition (if applicable)
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
         * Explains why this ObservationDefinition is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this ObservationDefinition is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * Copyright statement relating to the ObservationDefinition and/or its contents. Copyright statements are generally 
         * legal restrictions on the use and publishing of the ObservationDefinition.
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
         *     When ObservationDefinition was approved by publisher
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
         * The date on which the asset content was approved by the publisher. Approval happens once when the content is 
         * officially approved for usage.
         * 
         * @param approvalDate
         *     When ObservationDefinition was approved by publisher
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
         *     Date on which the asset content was last reviewed by the publisher
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
         * The date on which the asset content was last reviewed. Review happens periodically after that, but doesn't change the 
         * original approval date.
         * 
         * @param lastReviewDate
         *     Date on which the asset content was last reviewed by the publisher
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder lastReviewDate(Date lastReviewDate) {
            this.lastReviewDate = lastReviewDate;
            return this;
        }

        /**
         * The period during which the ObservationDefinition content was or is planned to be effective.
         * 
         * @param effectivePeriod
         *     The effective date range for the ObservationDefinition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder effectivePeriod(Period effectivePeriod) {
            this.effectivePeriod = effectivePeriod;
            return this;
        }

        /**
         * The canonical URL pointing to another FHIR-defined ObservationDefinition that is adhered to in whole or in part by 
         * this definition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromCanonical
         *     Based on FHIR definition of another observation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFromCanonical(Canonical... derivedFromCanonical) {
            for (Canonical value : derivedFromCanonical) {
                this.derivedFromCanonical.add(value);
            }
            return this;
        }

        /**
         * The canonical URL pointing to another FHIR-defined ObservationDefinition that is adhered to in whole or in part by 
         * this definition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromCanonical
         *     Based on FHIR definition of another observation
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder derivedFromCanonical(Collection<Canonical> derivedFromCanonical) {
            this.derivedFromCanonical = new ArrayList<>(derivedFromCanonical);
            return this;
        }

        /**
         * The URL pointing to an externally-defined observation definition, guideline or other definition that is adhered to in 
         * whole or in part by this definition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromUri
         *     Based on external definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFromUri(Uri... derivedFromUri) {
            for (Uri value : derivedFromUri) {
                this.derivedFromUri.add(value);
            }
            return this;
        }

        /**
         * The URL pointing to an externally-defined observation definition, guideline or other definition that is adhered to in 
         * whole or in part by this definition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param derivedFromUri
         *     Based on external definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder derivedFromUri(Collection<Uri> derivedFromUri) {
            this.derivedFromUri = new ArrayList<>(derivedFromUri);
            return this;
        }

        /**
         * A code that describes the intended kind of subject of Observation instances conforming to this ObservationDefinition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subject
         *     Type of subject for the defined observation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(CodeableConcept... subject) {
            for (CodeableConcept value : subject) {
                this.subject.add(value);
            }
            return this;
        }

        /**
         * A code that describes the intended kind of subject of Observation instances conforming to this ObservationDefinition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param subject
         *     Type of subject for the defined observation
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder subject(Collection<CodeableConcept> subject) {
            this.subject = new ArrayList<>(subject);
            return this;
        }

        /**
         * The type of individual/organization/device that is expected to act upon instances of this definition.
         * 
         * @param performerType
         *     Desired kind of performer for such kind of observation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performerType(CodeableConcept performerType) {
            this.performerType = performerType;
            return this;
        }

        /**
         * A code that classifies the general type of observation.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     General type of observation
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
         * A code that classifies the general type of observation.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     General type of observation
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
         * Describes what will be observed. Sometimes this is called the observation "name".
         * 
         * <p>This element is required.
         * 
         * @param code
         *     Type of observation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableConcept code) {
            this.code = code;
            return this;
        }

        /**
         * The data types allowed for the value element of the instance observations conforming to this ObservationDefinition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param permittedDataType
         *     Quantity | CodeableConcept | string | boolean | integer | Range | Ratio | SampledData | time | dateTime | Period
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder permittedDataType(ObservationDataType... permittedDataType) {
            for (ObservationDataType value : permittedDataType) {
                this.permittedDataType.add(value);
            }
            return this;
        }

        /**
         * The data types allowed for the value element of the instance observations conforming to this ObservationDefinition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param permittedDataType
         *     Quantity | CodeableConcept | string | boolean | integer | Range | Ratio | SampledData | time | dateTime | Period
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder permittedDataType(Collection<ObservationDataType> permittedDataType) {
            this.permittedDataType = new ArrayList<>(permittedDataType);
            return this;
        }

        /**
         * Convenience method for setting {@code multipleResultsAllowed}.
         * 
         * @param multipleResultsAllowed
         *     Multiple results allowed for conforming observations
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #multipleResultsAllowed(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder multipleResultsAllowed(java.lang.Boolean multipleResultsAllowed) {
            this.multipleResultsAllowed = (multipleResultsAllowed == null) ? null : Boolean.of(multipleResultsAllowed);
            return this;
        }

        /**
         * Multiple results allowed for observations conforming to this ObservationDefinition.
         * 
         * @param multipleResultsAllowed
         *     Multiple results allowed for conforming observations
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder multipleResultsAllowed(Boolean multipleResultsAllowed) {
            this.multipleResultsAllowed = multipleResultsAllowed;
            return this;
        }

        /**
         * The site on the subject's body where the observation is to be made.
         * 
         * @param bodySite
         *     Body part to be observed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder bodySite(CodeableConcept bodySite) {
            this.bodySite = bodySite;
            return this;
        }

        /**
         * The method or technique used to perform the observation.
         * 
         * @param method
         *     Method used to produce the observation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder method(CodeableConcept method) {
            this.method = method;
            return this;
        }

        /**
         * The kind of specimen that this type of observation is produced on.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link SpecimenDefinition}</li>
         * </ul>
         * 
         * @param specimen
         *     Kind of specimen used by this type of observation
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder specimen(Reference... specimen) {
            for (Reference value : specimen) {
                this.specimen.add(value);
            }
            return this;
        }

        /**
         * The kind of specimen that this type of observation is produced on.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link SpecimenDefinition}</li>
         * </ul>
         * 
         * @param specimen
         *     Kind of specimen used by this type of observation
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder specimen(Collection<Reference> specimen) {
            this.specimen = new ArrayList<>(specimen);
            return this;
        }

        /**
         * The measurement model of device or actual device used to produce observations of this type.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DeviceDefinition}</li>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param device
         *     Measurement device or model of device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder device(Reference... device) {
            for (Reference value : device) {
                this.device.add(value);
            }
            return this;
        }

        /**
         * The measurement model of device or actual device used to produce observations of this type.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DeviceDefinition}</li>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param device
         *     Measurement device or model of device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder device(Collection<Reference> device) {
            this.device = new ArrayList<>(device);
            return this;
        }

        /**
         * Convenience method for setting {@code preferredReportName}.
         * 
         * @param preferredReportName
         *     The preferred name to be used when reporting the observation results
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #preferredReportName(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder preferredReportName(java.lang.String preferredReportName) {
            this.preferredReportName = (preferredReportName == null) ? null : String.of(preferredReportName);
            return this;
        }

        /**
         * The preferred name to be used when reporting the results of observations conforming to this ObservationDefinition.
         * 
         * @param preferredReportName
         *     The preferred name to be used when reporting the observation results
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder preferredReportName(String preferredReportName) {
            this.preferredReportName = preferredReportName;
            return this;
        }

        /**
         * Units allowed for the valueQuantity element in the instance observations conforming to this ObservationDefinition.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param permittedUnit
         *     Unit for quantitative results
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder permittedUnit(Coding... permittedUnit) {
            for (Coding value : permittedUnit) {
                this.permittedUnit.add(value);
            }
            return this;
        }

        /**
         * Units allowed for the valueQuantity element in the instance observations conforming to this ObservationDefinition.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param permittedUnit
         *     Unit for quantitative results
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder permittedUnit(Collection<Coding> permittedUnit) {
            this.permittedUnit = new ArrayList<>(permittedUnit);
            return this;
        }

        /**
         * A set of qualified values associated with a context and a set of conditions - provides a range for quantitative and 
         * ordinal observations and a collection of value sets for qualitative observations.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param qualifiedValue
         *     Set of qualified values for observation results
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder qualifiedValue(QualifiedValue... qualifiedValue) {
            for (QualifiedValue value : qualifiedValue) {
                this.qualifiedValue.add(value);
            }
            return this;
        }

        /**
         * A set of qualified values associated with a context and a set of conditions - provides a range for quantitative and 
         * ordinal observations and a collection of value sets for qualitative observations.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param qualifiedValue
         *     Set of qualified values for observation results
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder qualifiedValue(Collection<QualifiedValue> qualifiedValue) {
            this.qualifiedValue = new ArrayList<>(qualifiedValue);
            return this;
        }

        /**
         * This ObservationDefinition defines a group observation (e.g. a battery, a panel of tests, a set of vital sign 
         * measurements) that includes the target as a member of the group.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ObservationDefinition}</li>
         * <li>{@link Questionnaire}</li>
         * </ul>
         * 
         * @param hasMember
         *     Definitions of related resources belonging to this kind of observation group
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder hasMember(Reference... hasMember) {
            for (Reference value : hasMember) {
                this.hasMember.add(value);
            }
            return this;
        }

        /**
         * This ObservationDefinition defines a group observation (e.g. a battery, a panel of tests, a set of vital sign 
         * measurements) that includes the target as a member of the group.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link ObservationDefinition}</li>
         * <li>{@link Questionnaire}</li>
         * </ul>
         * 
         * @param hasMember
         *     Definitions of related resources belonging to this kind of observation group
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder hasMember(Collection<Reference> hasMember) {
            this.hasMember = new ArrayList<>(hasMember);
            return this;
        }

        /**
         * Some observations have multiple component observations, expressed as separate code value pairs.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param component
         *     Component results
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder component(Component... component) {
            for (Component value : component) {
                this.component.add(value);
            }
            return this;
        }

        /**
         * Some observations have multiple component observations, expressed as separate code value pairs.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param component
         *     Component results
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder component(Collection<Component> component) {
            this.component = new ArrayList<>(component);
            return this;
        }

        /**
         * Build the {@link ObservationDefinition}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>code</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ObservationDefinition}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ObservationDefinition per the base specification
         */
        @Override
        public ObservationDefinition build() {
            ObservationDefinition observationDefinition = new ObservationDefinition(this);
            if (validating) {
                validate(observationDefinition);
            }
            return observationDefinition;
        }

        protected void validate(ObservationDefinition observationDefinition) {
            super.validate(observationDefinition);
            ValidationSupport.choiceElement(observationDefinition.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(observationDefinition.status, "status");
            ValidationSupport.checkList(observationDefinition.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(observationDefinition.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(observationDefinition.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(observationDefinition.derivedFromCanonical, "derivedFromCanonical", Canonical.class);
            ValidationSupport.checkList(observationDefinition.derivedFromUri, "derivedFromUri", Uri.class);
            ValidationSupport.checkList(observationDefinition.subject, "subject", CodeableConcept.class);
            ValidationSupport.checkList(observationDefinition.category, "category", CodeableConcept.class);
            ValidationSupport.requireNonNull(observationDefinition.code, "code");
            ValidationSupport.checkList(observationDefinition.permittedDataType, "permittedDataType", ObservationDataType.class);
            ValidationSupport.checkList(observationDefinition.specimen, "specimen", Reference.class);
            ValidationSupport.checkList(observationDefinition.device, "device", Reference.class);
            ValidationSupport.checkList(observationDefinition.permittedUnit, "permittedUnit", Coding.class);
            ValidationSupport.checkList(observationDefinition.qualifiedValue, "qualifiedValue", QualifiedValue.class);
            ValidationSupport.checkList(observationDefinition.hasMember, "hasMember", Reference.class);
            ValidationSupport.checkList(observationDefinition.component, "component", Component.class);
            ValidationSupport.checkReferenceType(observationDefinition.specimen, "specimen", "SpecimenDefinition");
            ValidationSupport.checkReferenceType(observationDefinition.device, "device", "DeviceDefinition", "Device");
            ValidationSupport.checkReferenceType(observationDefinition.hasMember, "hasMember", "ObservationDefinition", "Questionnaire");
        }

        protected Builder from(ObservationDefinition observationDefinition) {
            super.from(observationDefinition);
            url = observationDefinition.url;
            identifier = observationDefinition.identifier;
            version = observationDefinition.version;
            versionAlgorithm = observationDefinition.versionAlgorithm;
            name = observationDefinition.name;
            title = observationDefinition.title;
            status = observationDefinition.status;
            experimental = observationDefinition.experimental;
            date = observationDefinition.date;
            publisher = observationDefinition.publisher;
            contact.addAll(observationDefinition.contact);
            description = observationDefinition.description;
            useContext.addAll(observationDefinition.useContext);
            jurisdiction.addAll(observationDefinition.jurisdiction);
            purpose = observationDefinition.purpose;
            copyright = observationDefinition.copyright;
            copyrightLabel = observationDefinition.copyrightLabel;
            approvalDate = observationDefinition.approvalDate;
            lastReviewDate = observationDefinition.lastReviewDate;
            effectivePeriod = observationDefinition.effectivePeriod;
            derivedFromCanonical.addAll(observationDefinition.derivedFromCanonical);
            derivedFromUri.addAll(observationDefinition.derivedFromUri);
            subject.addAll(observationDefinition.subject);
            performerType = observationDefinition.performerType;
            category.addAll(observationDefinition.category);
            code = observationDefinition.code;
            permittedDataType.addAll(observationDefinition.permittedDataType);
            multipleResultsAllowed = observationDefinition.multipleResultsAllowed;
            bodySite = observationDefinition.bodySite;
            method = observationDefinition.method;
            specimen.addAll(observationDefinition.specimen);
            device.addAll(observationDefinition.device);
            preferredReportName = observationDefinition.preferredReportName;
            permittedUnit.addAll(observationDefinition.permittedUnit);
            qualifiedValue.addAll(observationDefinition.qualifiedValue);
            hasMember.addAll(observationDefinition.hasMember);
            component.addAll(observationDefinition.component);
            return this;
        }
    }

    /**
     * A set of qualified values associated with a context and a set of conditions - provides a range for quantitative and 
     * ordinal observations and a collection of value sets for qualitative observations.
     */
    public static class QualifiedValue extends BackboneElement {
        @Binding(
            bindingName = "ObservationRangeMeaning",
            strength = BindingStrength.Value.EXTENSIBLE,
            valueSet = "http://hl7.org/fhir/ValueSet/referencerange-meaning"
        )
        private final CodeableConcept context;
        @Binding(
            bindingName = "ObservationRangeAppliesTo",
            strength = BindingStrength.Value.EXAMPLE,
            valueSet = "http://hl7.org/fhir/ValueSet/referencerange-appliesto"
        )
        private final List<CodeableConcept> appliesTo;
        @Binding(
            bindingName = "AdministrativeGender",
            strength = BindingStrength.Value.REQUIRED,
            valueSet = "http://hl7.org/fhir/ValueSet/administrative-gender|5.0.0"
        )
        private final AdministrativeGender gender;
        private final Range age;
        private final Range gestationalAge;
        private final String condition;
        @Binding(
            bindingName = "ObservationRangeCategory",
            strength = BindingStrength.Value.REQUIRED,
            valueSet = "http://hl7.org/fhir/ValueSet/observation-range-category|5.0.0"
        )
        private final ObservationRangeCategory rangeCategory;
        private final Range range;
        private final Canonical validCodedValueSet;
        private final Canonical normalCodedValueSet;
        private final Canonical abnormalCodedValueSet;
        private final Canonical criticalCodedValueSet;

        private QualifiedValue(Builder builder) {
            super(builder);
            context = builder.context;
            appliesTo = Collections.unmodifiableList(builder.appliesTo);
            gender = builder.gender;
            age = builder.age;
            gestationalAge = builder.gestationalAge;
            condition = builder.condition;
            rangeCategory = builder.rangeCategory;
            range = builder.range;
            validCodedValueSet = builder.validCodedValueSet;
            normalCodedValueSet = builder.normalCodedValueSet;
            abnormalCodedValueSet = builder.abnormalCodedValueSet;
            criticalCodedValueSet = builder.criticalCodedValueSet;
        }

        /**
         * A concept defining the context for this set of qualified values.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getContext() {
            return context;
        }

        /**
         * The target population this set of qualified values applies to.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getAppliesTo() {
            return appliesTo;
        }

        /**
         * The gender this set of qualified values applies to.
         * 
         * @return
         *     An immutable object of type {@link AdministrativeGender} that may be null.
         */
        public AdministrativeGender getGender() {
            return gender;
        }

        /**
         * The age range this set of qualified values applies to.
         * 
         * @return
         *     An immutable object of type {@link Range} that may be null.
         */
        public Range getAge() {
            return age;
        }

        /**
         * The gestational age this set of qualified values applies to.
         * 
         * @return
         *     An immutable object of type {@link Range} that may be null.
         */
        public Range getGestationalAge() {
            return gestationalAge;
        }

        /**
         * Text based condition for which the the set of qualified values is valid.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getCondition() {
            return condition;
        }

        /**
         * The category of range of values for continuous or ordinal observations that match the criteria of this set of 
         * qualified values.
         * 
         * @return
         *     An immutable object of type {@link ObservationRangeCategory} that may be null.
         */
        public ObservationRangeCategory getRangeCategory() {
            return rangeCategory;
        }

        /**
         * The range of values defined for continuous or ordinal observations that match the criteria of this set of qualified 
         * values.
         * 
         * @return
         *     An immutable object of type {@link Range} that may be null.
         */
        public Range getRange() {
            return range;
        }

        /**
         * The set of valid coded results for qualitative observations that match the criteria of this set of qualified values.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getValidCodedValueSet() {
            return validCodedValueSet;
        }

        /**
         * The set of normal coded results for qualitative observations that match the criteria of this set of qualified values.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getNormalCodedValueSet() {
            return normalCodedValueSet;
        }

        /**
         * The set of abnormal coded results for qualitative observations that match the criteria of this set of qualified values.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getAbnormalCodedValueSet() {
            return abnormalCodedValueSet;
        }

        /**
         * The set of critical coded results for qualitative observations that match the criteria of this set of qualified values.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that may be null.
         */
        public Canonical getCriticalCodedValueSet() {
            return criticalCodedValueSet;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (context != null) || 
                !appliesTo.isEmpty() || 
                (gender != null) || 
                (age != null) || 
                (gestationalAge != null) || 
                (condition != null) || 
                (rangeCategory != null) || 
                (range != null) || 
                (validCodedValueSet != null) || 
                (normalCodedValueSet != null) || 
                (abnormalCodedValueSet != null) || 
                (criticalCodedValueSet != null);
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
                    accept(context, "context", visitor);
                    accept(appliesTo, "appliesTo", visitor, CodeableConcept.class);
                    accept(gender, "gender", visitor);
                    accept(age, "age", visitor);
                    accept(gestationalAge, "gestationalAge", visitor);
                    accept(condition, "condition", visitor);
                    accept(rangeCategory, "rangeCategory", visitor);
                    accept(range, "range", visitor);
                    accept(validCodedValueSet, "validCodedValueSet", visitor);
                    accept(normalCodedValueSet, "normalCodedValueSet", visitor);
                    accept(abnormalCodedValueSet, "abnormalCodedValueSet", visitor);
                    accept(criticalCodedValueSet, "criticalCodedValueSet", visitor);
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
            QualifiedValue other = (QualifiedValue) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(context, other.context) && 
                Objects.equals(appliesTo, other.appliesTo) && 
                Objects.equals(gender, other.gender) && 
                Objects.equals(age, other.age) && 
                Objects.equals(gestationalAge, other.gestationalAge) && 
                Objects.equals(condition, other.condition) && 
                Objects.equals(rangeCategory, other.rangeCategory) && 
                Objects.equals(range, other.range) && 
                Objects.equals(validCodedValueSet, other.validCodedValueSet) && 
                Objects.equals(normalCodedValueSet, other.normalCodedValueSet) && 
                Objects.equals(abnormalCodedValueSet, other.abnormalCodedValueSet) && 
                Objects.equals(criticalCodedValueSet, other.criticalCodedValueSet);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    context, 
                    appliesTo, 
                    gender, 
                    age, 
                    gestationalAge, 
                    condition, 
                    rangeCategory, 
                    range, 
                    validCodedValueSet, 
                    normalCodedValueSet, 
                    abnormalCodedValueSet, 
                    criticalCodedValueSet);
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
            private CodeableConcept context;
            private List<CodeableConcept> appliesTo = new ArrayList<>();
            private AdministrativeGender gender;
            private Range age;
            private Range gestationalAge;
            private String condition;
            private ObservationRangeCategory rangeCategory;
            private Range range;
            private Canonical validCodedValueSet;
            private Canonical normalCodedValueSet;
            private Canonical abnormalCodedValueSet;
            private Canonical criticalCodedValueSet;

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
             * A concept defining the context for this set of qualified values.
             * 
             * @param context
             *     Context qualifier for the set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder context(CodeableConcept context) {
                this.context = context;
                return this;
            }

            /**
             * The target population this set of qualified values applies to.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param appliesTo
             *     Targetted population for the set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder appliesTo(CodeableConcept... appliesTo) {
                for (CodeableConcept value : appliesTo) {
                    this.appliesTo.add(value);
                }
                return this;
            }

            /**
             * The target population this set of qualified values applies to.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param appliesTo
             *     Targetted population for the set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder appliesTo(Collection<CodeableConcept> appliesTo) {
                this.appliesTo = new ArrayList<>(appliesTo);
                return this;
            }

            /**
             * The gender this set of qualified values applies to.
             * 
             * @param gender
             *     male | female | other | unknown
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder gender(AdministrativeGender gender) {
                this.gender = gender;
                return this;
            }

            /**
             * The age range this set of qualified values applies to.
             * 
             * @param age
             *     Applicable age range for the set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder age(Range age) {
                this.age = age;
                return this;
            }

            /**
             * The gestational age this set of qualified values applies to.
             * 
             * @param gestationalAge
             *     Applicable gestational age range for the set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder gestationalAge(Range gestationalAge) {
                this.gestationalAge = gestationalAge;
                return this;
            }

            /**
             * Convenience method for setting {@code condition}.
             * 
             * @param condition
             *     Condition associated with the set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #condition(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder condition(java.lang.String condition) {
                this.condition = (condition == null) ? null : String.of(condition);
                return this;
            }

            /**
             * Text based condition for which the the set of qualified values is valid.
             * 
             * @param condition
             *     Condition associated with the set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder condition(String condition) {
                this.condition = condition;
                return this;
            }

            /**
             * The category of range of values for continuous or ordinal observations that match the criteria of this set of 
             * qualified values.
             * 
             * @param rangeCategory
             *     reference | critical | absolute
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder rangeCategory(ObservationRangeCategory rangeCategory) {
                this.rangeCategory = rangeCategory;
                return this;
            }

            /**
             * The range of values defined for continuous or ordinal observations that match the criteria of this set of qualified 
             * values.
             * 
             * @param range
             *     The range for continuous or ordinal observations
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder range(Range range) {
                this.range = range;
                return this;
            }

            /**
             * The set of valid coded results for qualitative observations that match the criteria of this set of qualified values.
             * 
             * @param validCodedValueSet
             *     Value set of valid coded values as part of this set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder validCodedValueSet(Canonical validCodedValueSet) {
                this.validCodedValueSet = validCodedValueSet;
                return this;
            }

            /**
             * The set of normal coded results for qualitative observations that match the criteria of this set of qualified values.
             * 
             * @param normalCodedValueSet
             *     Value set of normal coded values as part of this set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder normalCodedValueSet(Canonical normalCodedValueSet) {
                this.normalCodedValueSet = normalCodedValueSet;
                return this;
            }

            /**
             * The set of abnormal coded results for qualitative observations that match the criteria of this set of qualified values.
             * 
             * @param abnormalCodedValueSet
             *     Value set of abnormal coded values as part of this set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder abnormalCodedValueSet(Canonical abnormalCodedValueSet) {
                this.abnormalCodedValueSet = abnormalCodedValueSet;
                return this;
            }

            /**
             * The set of critical coded results for qualitative observations that match the criteria of this set of qualified values.
             * 
             * @param criticalCodedValueSet
             *     Value set of critical coded values as part of this set of qualified values
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder criticalCodedValueSet(Canonical criticalCodedValueSet) {
                this.criticalCodedValueSet = criticalCodedValueSet;
                return this;
            }

            /**
             * Build the {@link QualifiedValue}
             * 
             * @return
             *     An immutable object of type {@link QualifiedValue}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid QualifiedValue per the base specification
             */
            @Override
            public QualifiedValue build() {
                QualifiedValue qualifiedValue = new QualifiedValue(this);
                if (validating) {
                    validate(qualifiedValue);
                }
                return qualifiedValue;
            }

            protected void validate(QualifiedValue qualifiedValue) {
                super.validate(qualifiedValue);
                ValidationSupport.checkList(qualifiedValue.appliesTo, "appliesTo", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(qualifiedValue);
            }

            protected Builder from(QualifiedValue qualifiedValue) {
                super.from(qualifiedValue);
                context = qualifiedValue.context;
                appliesTo.addAll(qualifiedValue.appliesTo);
                gender = qualifiedValue.gender;
                age = qualifiedValue.age;
                gestationalAge = qualifiedValue.gestationalAge;
                condition = qualifiedValue.condition;
                rangeCategory = qualifiedValue.rangeCategory;
                range = qualifiedValue.range;
                validCodedValueSet = qualifiedValue.validCodedValueSet;
                normalCodedValueSet = qualifiedValue.normalCodedValueSet;
                abnormalCodedValueSet = qualifiedValue.abnormalCodedValueSet;
                criticalCodedValueSet = qualifiedValue.criticalCodedValueSet;
                return this;
            }
        }
    }

    /**
     * Some observations have multiple component observations, expressed as separate code value pairs.
     */
    public static class Component extends BackboneElement {
        @Binding(
            bindingName = "ObservationCode",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes identifying names of simple observations.",
            valueSet = "http://hl7.org/fhir/ValueSet/observation-codes"
        )
        @Required
        private final CodeableConcept code;
        @Binding(
            bindingName = "ObservationDataType",
            strength = BindingStrength.Value.REQUIRED,
            description = "Permitted data type for observation value.",
            valueSet = "http://hl7.org/fhir/ValueSet/permitted-data-type|5.0.0"
        )
        private final List<ObservationDataType> permittedDataType;
        @Binding(
            bindingName = "ObservationUnit",
            strength = BindingStrength.Value.PREFERRED,
            description = "Codes identifying units of measure.",
            valueSet = "http://hl7.org/fhir/ValueSet/ucum-units"
        )
        private final List<Coding> permittedUnit;
        private final List<ObservationDefinition.QualifiedValue> qualifiedValue;

        private Component(Builder builder) {
            super(builder);
            code = builder.code;
            permittedDataType = Collections.unmodifiableList(builder.permittedDataType);
            permittedUnit = Collections.unmodifiableList(builder.permittedUnit);
            qualifiedValue = Collections.unmodifiableList(builder.qualifiedValue);
        }

        /**
         * Describes what will be observed.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * The data types allowed for the value element of the instance of this component observations.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link ObservationDataType} that may be empty.
         */
        public List<ObservationDataType> getPermittedDataType() {
            return permittedDataType;
        }

        /**
         * Units allowed for the valueQuantity element in the instance observations conforming to this ObservationDefinition.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
         */
        public List<Coding> getPermittedUnit() {
            return permittedUnit;
        }

        /**
         * A set of qualified values associated with a context and a set of conditions - provides a range for quantitative and 
         * ordinal observations and a collection of value sets for qualitative observations.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link QualifiedValue} that may be empty.
         */
        public List<ObservationDefinition.QualifiedValue> getQualifiedValue() {
            return qualifiedValue;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                !permittedDataType.isEmpty() || 
                !permittedUnit.isEmpty() || 
                !qualifiedValue.isEmpty();
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
                    accept(permittedDataType, "permittedDataType", visitor, ObservationDataType.class);
                    accept(permittedUnit, "permittedUnit", visitor, Coding.class);
                    accept(qualifiedValue, "qualifiedValue", visitor, ObservationDefinition.QualifiedValue.class);
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
            Component other = (Component) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(permittedDataType, other.permittedDataType) && 
                Objects.equals(permittedUnit, other.permittedUnit) && 
                Objects.equals(qualifiedValue, other.qualifiedValue);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    permittedDataType, 
                    permittedUnit, 
                    qualifiedValue);
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
            private CodeableConcept code;
            private List<ObservationDataType> permittedDataType = new ArrayList<>();
            private List<Coding> permittedUnit = new ArrayList<>();
            private List<ObservationDefinition.QualifiedValue> qualifiedValue = new ArrayList<>();

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
             * Describes what will be observed.
             * 
             * <p>This element is required.
             * 
             * @param code
             *     Type of observation
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * The data types allowed for the value element of the instance of this component observations.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param permittedDataType
             *     Quantity | CodeableConcept | string | boolean | integer | Range | Ratio | SampledData | time | dateTime | Period
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder permittedDataType(ObservationDataType... permittedDataType) {
                for (ObservationDataType value : permittedDataType) {
                    this.permittedDataType.add(value);
                }
                return this;
            }

            /**
             * The data types allowed for the value element of the instance of this component observations.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param permittedDataType
             *     Quantity | CodeableConcept | string | boolean | integer | Range | Ratio | SampledData | time | dateTime | Period
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder permittedDataType(Collection<ObservationDataType> permittedDataType) {
                this.permittedDataType = new ArrayList<>(permittedDataType);
                return this;
            }

            /**
             * Units allowed for the valueQuantity element in the instance observations conforming to this ObservationDefinition.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param permittedUnit
             *     Unit for quantitative results
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder permittedUnit(Coding... permittedUnit) {
                for (Coding value : permittedUnit) {
                    this.permittedUnit.add(value);
                }
                return this;
            }

            /**
             * Units allowed for the valueQuantity element in the instance observations conforming to this ObservationDefinition.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param permittedUnit
             *     Unit for quantitative results
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder permittedUnit(Collection<Coding> permittedUnit) {
                this.permittedUnit = new ArrayList<>(permittedUnit);
                return this;
            }

            /**
             * A set of qualified values associated with a context and a set of conditions - provides a range for quantitative and 
             * ordinal observations and a collection of value sets for qualitative observations.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param qualifiedValue
             *     Set of qualified values for observation results
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder qualifiedValue(ObservationDefinition.QualifiedValue... qualifiedValue) {
                for (ObservationDefinition.QualifiedValue value : qualifiedValue) {
                    this.qualifiedValue.add(value);
                }
                return this;
            }

            /**
             * A set of qualified values associated with a context and a set of conditions - provides a range for quantitative and 
             * ordinal observations and a collection of value sets for qualitative observations.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param qualifiedValue
             *     Set of qualified values for observation results
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder qualifiedValue(Collection<ObservationDefinition.QualifiedValue> qualifiedValue) {
                this.qualifiedValue = new ArrayList<>(qualifiedValue);
                return this;
            }

            /**
             * Build the {@link Component}
             * 
             * <p>Required elements:
             * <ul>
             * <li>code</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Component}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Component per the base specification
             */
            @Override
            public Component build() {
                Component component = new Component(this);
                if (validating) {
                    validate(component);
                }
                return component;
            }

            protected void validate(Component component) {
                super.validate(component);
                ValidationSupport.requireNonNull(component.code, "code");
                ValidationSupport.checkList(component.permittedDataType, "permittedDataType", ObservationDataType.class);
                ValidationSupport.checkList(component.permittedUnit, "permittedUnit", Coding.class);
                ValidationSupport.checkList(component.qualifiedValue, "qualifiedValue", ObservationDefinition.QualifiedValue.class);
                ValidationSupport.requireValueOrChildren(component);
            }

            protected Builder from(Component component) {
                super.from(component);
                code = component.code;
                permittedDataType.addAll(component.permittedDataType);
                permittedUnit.addAll(component.permittedUnit);
                qualifiedValue.addAll(component.qualifiedValue);
                return this;
            }
        }
    }
}
