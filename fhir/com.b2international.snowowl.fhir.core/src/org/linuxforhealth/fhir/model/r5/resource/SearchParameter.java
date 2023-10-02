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
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.FHIRTypes;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.SearchComparator;
import org.linuxforhealth.fhir.model.r5.type.code.SearchModifierCode;
import org.linuxforhealth.fhir.model.r5.type.code.SearchParamType;
import org.linuxforhealth.fhir.model.r5.type.code.SearchProcessingModeType;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A search parameter that defines a named search item that can be used to search/filter on a resource.
 * 
 * <p>Maturity level: FMM5 (Trial Use)
 */
@Maturity(
    level = 5,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "cnl-0",
    level = "Warning",
    location = "(base)",
    description = "Name should be usable as an identifier for the module by machine processing applications such as code generation",
    expression = "name.exists() implies name.matches('^[A-Z]([A-Za-z0-9_]){1,254}$')",
    source = "http://hl7.org/fhir/StructureDefinition/SearchParameter"
)
@Constraint(
    id = "spd-1",
    level = "Rule",
    location = "(base)",
    description = "If an expression is present, there SHALL be a processingMode",
    expression = "expression.empty() or processingMode.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/SearchParameter"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "SearchParameter.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/SearchParameter"
)
@Constraint(
    id = "spd-2",
    level = "Rule",
    location = "(base)",
    description = "Search parameters can only have chain names when the search parameter type is 'reference'",
    expression = "chain.empty() or type = 'reference'",
    source = "http://hl7.org/fhir/StructureDefinition/SearchParameter"
)
@Constraint(
    id = "spd-3",
    level = "Rule",
    location = "(base)",
    description = "Search parameters comparator can only be used on type 'number', 'date', 'quantity' or 'special'.",
    expression = "comparator.empty() or (type in ('number' | 'date' | 'quantity' | 'special'))",
    source = "http://hl7.org/fhir/StructureDefinition/SearchParameter"
)
@Constraint(
    id = "searchParameter-4",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/SearchParameter",
    generated = true
)
@Constraint(
    id = "searchParameter-5",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/SearchParameter",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class SearchParameter extends DomainResource {
    @Summary
    @Required
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
    @Required
    private final String name;
    @Summary
    private final String title;
    private final Canonical derivedFrom;
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
    @Summary
    @Required
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
    @Required
    private final Code code;
    @Summary
    @Binding(
        bindingName = "FHIRTypes",
        strength = BindingStrength.Value.REQUIRED,
        description = "A type of resource, or a Reference (from all versions)",
        valueSet = "http://hl7.org/fhir/ValueSet/version-independent-all-resource-types|5.0.0"
    )
    @Required
    private final List<FHIRTypes> base;
    @Summary
    @Binding(
        bindingName = "SearchParamType",
        strength = BindingStrength.Value.REQUIRED,
        description = "Data types allowed to be used for search parameters.",
        valueSet = "http://hl7.org/fhir/ValueSet/search-param-type|5.0.0"
    )
    @Required
    private final SearchParamType type;
    private final String expression;
    @Binding(
        bindingName = "SearchProcessingModeType",
        strength = BindingStrength.Value.REQUIRED,
        description = "How a search parameter relates to the set of elements returned by evaluating its expression query.",
        valueSet = "http://hl7.org/fhir/ValueSet/search-processingmode|5.0.0"
    )
    private final SearchProcessingModeType processingMode;
    private final String constraint;
    @Binding(
        bindingName = "FHIRTypes",
        strength = BindingStrength.Value.REQUIRED,
        description = "A type of resource, or a Reference (from all versions)",
        valueSet = "http://hl7.org/fhir/ValueSet/version-independent-all-resource-types|5.0.0"
    )
    private final List<FHIRTypes> target;
    private final Boolean multipleOr;
    private final Boolean multipleAnd;
    @Binding(
        bindingName = "SearchComparator",
        strength = BindingStrength.Value.REQUIRED,
        description = "What Search Comparator Codes are supported in search.",
        valueSet = "http://hl7.org/fhir/ValueSet/search-comparator|5.0.0"
    )
    private final List<SearchComparator> comparator;
    @Binding(
        bindingName = "SearchModifierCode",
        strength = BindingStrength.Value.REQUIRED,
        description = "A supported modifier for a search parameter.",
        valueSet = "http://hl7.org/fhir/ValueSet/search-modifier-code|5.0.0"
    )
    private final List<SearchModifierCode> modifier;
    private final List<String> chain;
    private final List<Component> component;

    private SearchParameter(Builder builder) {
        super(builder);
        url = builder.url;
        identifier = Collections.unmodifiableList(builder.identifier);
        version = builder.version;
        versionAlgorithm = builder.versionAlgorithm;
        name = builder.name;
        title = builder.title;
        derivedFrom = builder.derivedFrom;
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
        code = builder.code;
        base = Collections.unmodifiableList(builder.base);
        type = builder.type;
        expression = builder.expression;
        processingMode = builder.processingMode;
        constraint = builder.constraint;
        target = Collections.unmodifiableList(builder.target);
        multipleOr = builder.multipleOr;
        multipleAnd = builder.multipleAnd;
        comparator = Collections.unmodifiableList(builder.comparator);
        modifier = Collections.unmodifiableList(builder.modifier);
        chain = Collections.unmodifiableList(builder.chain);
        component = Collections.unmodifiableList(builder.component);
    }

    /**
     * An absolute URI that is used to identify this search parameter when it is referenced in a specification, model, design 
     * or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address 
     * at which an authoritative instance of this search parameter is (or will be) published. This URL can be the target of a 
     * canonical reference. It SHALL remain the same when the search parameter is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that is non-null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this search parameter when it is represented in other formats, or 
     * referenced in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the search parameter when it is referenced in a specification, 
     * model, design or instance. This is an arbitrary value managed by the search parameter author and is not expected to be 
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
     * A natural language name identifying the search parameter. This name should be usable as an identifier for the module 
     * by machine processing applications such as code generation.
     * 
     * @return
     *     An immutable object of type {@link String} that is non-null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the search parameter.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Where this search parameter is originally defined. If a derivedFrom is provided, then the details in the search 
     * parameter must be consistent with the definition from which it is defined. i.e. the parameter should have the same 
     * meaning, and (usually) the functionality should be a proper subset of the underlying search parameter.
     * 
     * @return
     *     An immutable object of type {@link Canonical} that may be null.
     */
    public Canonical getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * The status of this search parameter. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this search parameter is authored for testing purposes (or 
     * education/evaluation/marketing) and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The date (and optionally time) when the search parameter was last significantly changed. The date must change when the 
     * business version changes and it must change if the status code changes. In addition, it should change when the 
     * substantive content of the search parameter changes.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual tresponsible for the release and ongoing maintenance of the search 
     * parameter.
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
     * And how it used.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that is non-null.
     */
    public Markdown getDescription() {
        return description;
    }

    /**
     * The content was developed with a focus and intent of supporting the contexts that are listed. These contexts may be 
     * general categories (gender, age, ...) or may be references to specific programs (insurance plans, studies, ...) and 
     * may be used to assist with indexing and searching for appropriate search parameter instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A legal or geographic region in which the search parameter is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Explanation of why this search parameter is needed and why it has been designed as it has.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * A copyright statement relating to the search parameter and/or its contents. Copyright statements are generally legal 
     * restrictions on the use and publishing of the search parameter.
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
     * The label that is recommended to be used in the URL or the parameter name in a parameters resource for this search 
     * parameter. In some cases, servers may need to use a different CapabilityStatement searchParam.name to differentiate 
     * between multiple SearchParameters that happen to have the same code.
     * 
     * @return
     *     An immutable object of type {@link Code} that is non-null.
     */
    public Code getCode() {
        return code;
    }

    /**
     * The base resource type(s) that this search parameter can be used against.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link FHIRTypes} that is non-empty.
     */
    public List<FHIRTypes> getBase() {
        return base;
    }

    /**
     * The type of value that a search parameter may contain, and how the content is interpreted.
     * 
     * @return
     *     An immutable object of type {@link SearchParamType} that is non-null.
     */
    public SearchParamType getType() {
        return type;
    }

    /**
     * A FHIRPath expression that returns a set of elements for the search parameter.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * How the search parameter relates to the set of elements returned by evaluating the expression query.
     * 
     * @return
     *     An immutable object of type {@link SearchProcessingModeType} that may be null.
     */
    public SearchProcessingModeType getProcessingMode() {
        return processingMode;
    }

    /**
     * FHIRPath expression that defines/sets a complex constraint for when this SearchParameter is applicable.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getConstraint() {
        return constraint;
    }

    /**
     * Types of resource (if a resource is referenced).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link FHIRTypes} that may be empty.
     */
    public List<FHIRTypes> getTarget() {
        return target;
    }

    /**
     * Whether multiple values are allowed for each time the parameter exists. Values are separated by commas, and the 
     * parameter matches if any of the values match.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getMultipleOr() {
        return multipleOr;
    }

    /**
     * Whether multiple parameters are allowed - e.g. more than one parameter with the same name. The search matches if all 
     * the parameters match.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getMultipleAnd() {
        return multipleAnd;
    }

    /**
     * Comparators supported for the search parameter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link SearchComparator} that may be empty.
     */
    public List<SearchComparator> getComparator() {
        return comparator;
    }

    /**
     * A modifier supported for the search parameter.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link SearchModifierCode} that may be empty.
     */
    public List<SearchModifierCode> getModifier() {
        return modifier;
    }

    /**
     * Contains the names of any search parameters which may be chained to the containing search parameter. Chained 
     * parameters may be added to search parameters of type reference and specify that resources will only be returned if 
     * they contain a reference to a resource which matches the chained parameter value. Values for this field should be 
     * drawn from SearchParameter.code for a parameter on the target resource type.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link String} that may be empty.
     */
    public List<String> getChain() {
        return chain;
    }

    /**
     * Used to define the parts of a composite search parameter.
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
            !identifier.isEmpty() || 
            (version != null) || 
            (versionAlgorithm != null) || 
            (name != null) || 
            (title != null) || 
            (derivedFrom != null) || 
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
            (code != null) || 
            !base.isEmpty() || 
            (type != null) || 
            (expression != null) || 
            (processingMode != null) || 
            (constraint != null) || 
            !target.isEmpty() || 
            (multipleOr != null) || 
            (multipleAnd != null) || 
            !comparator.isEmpty() || 
            !modifier.isEmpty() || 
            !chain.isEmpty() || 
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
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(version, "version", visitor);
                accept(versionAlgorithm, "versionAlgorithm", visitor);
                accept(name, "name", visitor);
                accept(title, "title", visitor);
                accept(derivedFrom, "derivedFrom", visitor);
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
                accept(code, "code", visitor);
                accept(base, "base", visitor, FHIRTypes.class);
                accept(type, "type", visitor);
                accept(expression, "expression", visitor);
                accept(processingMode, "processingMode", visitor);
                accept(constraint, "constraint", visitor);
                accept(target, "target", visitor, FHIRTypes.class);
                accept(multipleOr, "multipleOr", visitor);
                accept(multipleAnd, "multipleAnd", visitor);
                accept(comparator, "comparator", visitor, SearchComparator.class);
                accept(modifier, "modifier", visitor, SearchModifierCode.class);
                accept(chain, "chain", visitor, String.class);
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
        SearchParameter other = (SearchParameter) obj;
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
            Objects.equals(derivedFrom, other.derivedFrom) && 
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
            Objects.equals(code, other.code) && 
            Objects.equals(base, other.base) && 
            Objects.equals(type, other.type) && 
            Objects.equals(expression, other.expression) && 
            Objects.equals(processingMode, other.processingMode) && 
            Objects.equals(constraint, other.constraint) && 
            Objects.equals(target, other.target) && 
            Objects.equals(multipleOr, other.multipleOr) && 
            Objects.equals(multipleAnd, other.multipleAnd) && 
            Objects.equals(comparator, other.comparator) && 
            Objects.equals(modifier, other.modifier) && 
            Objects.equals(chain, other.chain) && 
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
                derivedFrom, 
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
                code, 
                base, 
                type, 
                expression, 
                processingMode, 
                constraint, 
                target, 
                multipleOr, 
                multipleAnd, 
                comparator, 
                modifier, 
                chain, 
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
        private List<Identifier> identifier = new ArrayList<>();
        private String version;
        private Element versionAlgorithm;
        private String name;
        private String title;
        private Canonical derivedFrom;
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
        private Code code;
        private List<FHIRTypes> base = new ArrayList<>();
        private SearchParamType type;
        private String expression;
        private SearchProcessingModeType processingMode;
        private String constraint;
        private List<FHIRTypes> target = new ArrayList<>();
        private Boolean multipleOr;
        private Boolean multipleAnd;
        private List<SearchComparator> comparator = new ArrayList<>();
        private List<SearchModifierCode> modifier = new ArrayList<>();
        private List<String> chain = new ArrayList<>();
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
         * An absolute URI that is used to identify this search parameter when it is referenced in a specification, model, design 
         * or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address 
         * at which an authoritative instance of this search parameter is (or will be) published. This URL can be the target of a 
         * canonical reference. It SHALL remain the same when the search parameter is stored on different servers.
         * 
         * <p>This element is required.
         * 
         * @param url
         *     Canonical identifier for this search parameter, represented as a URI (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A formal identifier that is used to identify this search parameter when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the search parameter (business identifier)
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
         * A formal identifier that is used to identify this search parameter when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the search parameter (business identifier)
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
         *     Business version of the search parameter
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
         * The identifier that is used to identify this version of the search parameter when it is referenced in a specification, 
         * model, design or instance. This is an arbitrary value managed by the search parameter author and is not expected to be 
         * globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is 
         * also no expectation that versions can be placed in a lexicographical sequence.
         * 
         * @param version
         *     Business version of the search parameter
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
         * <p>This element is required.
         * 
         * @param name
         *     Name for this search parameter (computer friendly)
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
         * A natural language name identifying the search parameter. This name should be usable as an identifier for the module 
         * by machine processing applications such as code generation.
         * 
         * <p>This element is required.
         * 
         * @param name
         *     Name for this search parameter (computer friendly)
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
         *     Name for this search parameter (human friendly)
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
         * A short, descriptive, user-friendly title for the search parameter.
         * 
         * @param title
         *     Name for this search parameter (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Where this search parameter is originally defined. If a derivedFrom is provided, then the details in the search 
         * parameter must be consistent with the definition from which it is defined. i.e. the parameter should have the same 
         * meaning, and (usually) the functionality should be a proper subset of the underlying search parameter.
         * 
         * @param derivedFrom
         *     Original definition for the search parameter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder derivedFrom(Canonical derivedFrom) {
            this.derivedFrom = derivedFrom;
            return this;
        }

        /**
         * The status of this search parameter. Enables tracking the life-cycle of the content.
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
         * A Boolean value to indicate that this search parameter is authored for testing purposes (or 
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
         * The date (and optionally time) when the search parameter was last significantly changed. The date must change when the 
         * business version changes and it must change if the status code changes. In addition, it should change when the 
         * substantive content of the search parameter changes.
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
         * The name of the organization or individual tresponsible for the release and ongoing maintenance of the search 
         * parameter.
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
         * And how it used.
         * 
         * <p>This element is required.
         * 
         * @param description
         *     Natural language description of the search parameter
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
         * may be used to assist with indexing and searching for appropriate search parameter instances.
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
         * may be used to assist with indexing and searching for appropriate search parameter instances.
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
         * A legal or geographic region in which the search parameter is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for search parameter (if applicable)
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
         * A legal or geographic region in which the search parameter is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for search parameter (if applicable)
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
         * Explanation of why this search parameter is needed and why it has been designed as it has.
         * 
         * @param purpose
         *     Why this search parameter is defined
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * A copyright statement relating to the search parameter and/or its contents. Copyright statements are generally legal 
         * restrictions on the use and publishing of the search parameter.
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
         * The label that is recommended to be used in the URL or the parameter name in a parameters resource for this search 
         * parameter. In some cases, servers may need to use a different CapabilityStatement searchParam.name to differentiate 
         * between multiple SearchParameters that happen to have the same code.
         * 
         * <p>This element is required.
         * 
         * @param code
         *     Recommended name for parameter in search url
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(Code code) {
            this.code = code;
            return this;
        }

        /**
         * The base resource type(s) that this search parameter can be used against.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param base
         *     The resource type(s) this search parameter applies to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder base(FHIRTypes... base) {
            for (FHIRTypes value : base) {
                this.base.add(value);
            }
            return this;
        }

        /**
         * The base resource type(s) that this search parameter can be used against.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>This element is required.
         * 
         * @param base
         *     The resource type(s) this search parameter applies to
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder base(Collection<FHIRTypes> base) {
            this.base = new ArrayList<>(base);
            return this;
        }

        /**
         * The type of value that a search parameter may contain, and how the content is interpreted.
         * 
         * <p>This element is required.
         * 
         * @param type
         *     number | date | string | token | reference | composite | quantity | uri | special
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder type(SearchParamType type) {
            this.type = type;
            return this;
        }

        /**
         * Convenience method for setting {@code expression}.
         * 
         * @param expression
         *     FHIRPath expression that extracts the values
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #expression(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder expression(java.lang.String expression) {
            this.expression = (expression == null) ? null : String.of(expression);
            return this;
        }

        /**
         * A FHIRPath expression that returns a set of elements for the search parameter.
         * 
         * @param expression
         *     FHIRPath expression that extracts the values
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        /**
         * How the search parameter relates to the set of elements returned by evaluating the expression query.
         * 
         * @param processingMode
         *     normal | phonetic | other
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder processingMode(SearchProcessingModeType processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        /**
         * Convenience method for setting {@code constraint}.
         * 
         * @param constraint
         *     FHIRPath expression that constraints the usage of this SearchParamete
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #constraint(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder constraint(java.lang.String constraint) {
            this.constraint = (constraint == null) ? null : String.of(constraint);
            return this;
        }

        /**
         * FHIRPath expression that defines/sets a complex constraint for when this SearchParameter is applicable.
         * 
         * @param constraint
         *     FHIRPath expression that constraints the usage of this SearchParamete
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder constraint(String constraint) {
            this.constraint = constraint;
            return this;
        }

        /**
         * Types of resource (if a resource is referenced).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param target
         *     Types of resource (if a resource reference)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder target(FHIRTypes... target) {
            for (FHIRTypes value : target) {
                this.target.add(value);
            }
            return this;
        }

        /**
         * Types of resource (if a resource is referenced).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param target
         *     Types of resource (if a resource reference)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder target(Collection<FHIRTypes> target) {
            this.target = new ArrayList<>(target);
            return this;
        }

        /**
         * Convenience method for setting {@code multipleOr}.
         * 
         * @param multipleOr
         *     Allow multiple values per parameter (or)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #multipleOr(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder multipleOr(java.lang.Boolean multipleOr) {
            this.multipleOr = (multipleOr == null) ? null : Boolean.of(multipleOr);
            return this;
        }

        /**
         * Whether multiple values are allowed for each time the parameter exists. Values are separated by commas, and the 
         * parameter matches if any of the values match.
         * 
         * @param multipleOr
         *     Allow multiple values per parameter (or)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder multipleOr(Boolean multipleOr) {
            this.multipleOr = multipleOr;
            return this;
        }

        /**
         * Convenience method for setting {@code multipleAnd}.
         * 
         * @param multipleAnd
         *     Allow multiple parameters (and)
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #multipleAnd(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder multipleAnd(java.lang.Boolean multipleAnd) {
            this.multipleAnd = (multipleAnd == null) ? null : Boolean.of(multipleAnd);
            return this;
        }

        /**
         * Whether multiple parameters are allowed - e.g. more than one parameter with the same name. The search matches if all 
         * the parameters match.
         * 
         * @param multipleAnd
         *     Allow multiple parameters (and)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder multipleAnd(Boolean multipleAnd) {
            this.multipleAnd = multipleAnd;
            return this;
        }

        /**
         * Comparators supported for the search parameter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param comparator
         *     eq | ne | gt | lt | ge | le | sa | eb | ap
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder comparator(SearchComparator... comparator) {
            for (SearchComparator value : comparator) {
                this.comparator.add(value);
            }
            return this;
        }

        /**
         * Comparators supported for the search parameter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param comparator
         *     eq | ne | gt | lt | ge | le | sa | eb | ap
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder comparator(Collection<SearchComparator> comparator) {
            this.comparator = new ArrayList<>(comparator);
            return this;
        }

        /**
         * A modifier supported for the search parameter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifier
         *     missing | exact | contains | not | text | in | not-in | below | above | type | identifier | of-type | code-text | text-
         *     advanced | iterate
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder modifier(SearchModifierCode... modifier) {
            for (SearchModifierCode value : modifier) {
                this.modifier.add(value);
            }
            return this;
        }

        /**
         * A modifier supported for the search parameter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifier
         *     missing | exact | contains | not | text | in | not-in | below | above | type | identifier | of-type | code-text | text-
         *     advanced | iterate
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder modifier(Collection<SearchModifierCode> modifier) {
            this.modifier = new ArrayList<>(modifier);
            return this;
        }

        /**
         * Convenience method for setting {@code chain}.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param chain
         *     Chained names supported
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #chain(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder chain(java.lang.String... chain) {
            for (java.lang.String value : chain) {
                this.chain.add((value == null) ? null : String.of(value));
            }
            return this;
        }

        /**
         * Contains the names of any search parameters which may be chained to the containing search parameter. Chained 
         * parameters may be added to search parameters of type reference and specify that resources will only be returned if 
         * they contain a reference to a resource which matches the chained parameter value. Values for this field should be 
         * drawn from SearchParameter.code for a parameter on the target resource type.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param chain
         *     Chained names supported
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder chain(String... chain) {
            for (String value : chain) {
                this.chain.add(value);
            }
            return this;
        }

        /**
         * Contains the names of any search parameters which may be chained to the containing search parameter. Chained 
         * parameters may be added to search parameters of type reference and specify that resources will only be returned if 
         * they contain a reference to a resource which matches the chained parameter value. Values for this field should be 
         * drawn from SearchParameter.code for a parameter on the target resource type.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param chain
         *     Chained names supported
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder chain(Collection<String> chain) {
            this.chain = new ArrayList<>(chain);
            return this;
        }

        /**
         * Used to define the parts of a composite search parameter.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param component
         *     For Composite resources to define the parts
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
         * Used to define the parts of a composite search parameter.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param component
         *     For Composite resources to define the parts
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
         * Build the {@link SearchParameter}
         * 
         * <p>Required elements:
         * <ul>
         * <li>url</li>
         * <li>name</li>
         * <li>status</li>
         * <li>description</li>
         * <li>code</li>
         * <li>base</li>
         * <li>type</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link SearchParameter}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid SearchParameter per the base specification
         */
        @Override
        public SearchParameter build() {
            SearchParameter searchParameter = new SearchParameter(this);
            if (validating) {
                validate(searchParameter);
            }
            return searchParameter;
        }

        protected void validate(SearchParameter searchParameter) {
            super.validate(searchParameter);
            ValidationSupport.requireNonNull(searchParameter.url, "url");
            ValidationSupport.checkList(searchParameter.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(searchParameter.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(searchParameter.name, "name");
            ValidationSupport.requireNonNull(searchParameter.status, "status");
            ValidationSupport.checkList(searchParameter.contact, "contact", ContactDetail.class);
            ValidationSupport.requireNonNull(searchParameter.description, "description");
            ValidationSupport.checkList(searchParameter.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(searchParameter.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.requireNonNull(searchParameter.code, "code");
            ValidationSupport.checkNonEmptyList(searchParameter.base, "base", FHIRTypes.class);
            ValidationSupport.requireNonNull(searchParameter.type, "type");
            ValidationSupport.checkList(searchParameter.target, "target", FHIRTypes.class);
            ValidationSupport.checkList(searchParameter.comparator, "comparator", SearchComparator.class);
            ValidationSupport.checkList(searchParameter.modifier, "modifier", SearchModifierCode.class);
            ValidationSupport.checkList(searchParameter.chain, "chain", String.class);
            ValidationSupport.checkList(searchParameter.component, "component", Component.class);
        }

        protected Builder from(SearchParameter searchParameter) {
            super.from(searchParameter);
            url = searchParameter.url;
            identifier.addAll(searchParameter.identifier);
            version = searchParameter.version;
            versionAlgorithm = searchParameter.versionAlgorithm;
            name = searchParameter.name;
            title = searchParameter.title;
            derivedFrom = searchParameter.derivedFrom;
            status = searchParameter.status;
            experimental = searchParameter.experimental;
            date = searchParameter.date;
            publisher = searchParameter.publisher;
            contact.addAll(searchParameter.contact);
            description = searchParameter.description;
            useContext.addAll(searchParameter.useContext);
            jurisdiction.addAll(searchParameter.jurisdiction);
            purpose = searchParameter.purpose;
            copyright = searchParameter.copyright;
            copyrightLabel = searchParameter.copyrightLabel;
            code = searchParameter.code;
            base.addAll(searchParameter.base);
            type = searchParameter.type;
            expression = searchParameter.expression;
            processingMode = searchParameter.processingMode;
            constraint = searchParameter.constraint;
            target.addAll(searchParameter.target);
            multipleOr = searchParameter.multipleOr;
            multipleAnd = searchParameter.multipleAnd;
            comparator.addAll(searchParameter.comparator);
            modifier.addAll(searchParameter.modifier);
            chain.addAll(searchParameter.chain);
            component.addAll(searchParameter.component);
            return this;
        }
    }

    /**
     * Used to define the parts of a composite search parameter.
     */
    public static class Component extends BackboneElement {
        @Required
        private final Canonical definition;
        @Required
        private final String expression;

        private Component(Builder builder) {
            super(builder);
            definition = builder.definition;
            expression = builder.expression;
        }

        /**
         * The definition of the search parameter that describes this part.
         * 
         * @return
         *     An immutable object of type {@link Canonical} that is non-null.
         */
        public Canonical getDefinition() {
            return definition;
        }

        /**
         * A sub-expression that defines how to extract values for this component from the output of the main SearchParameter.
         * expression.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getExpression() {
            return expression;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (definition != null) || 
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
                    accept(definition, "definition", visitor);
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
            Component other = (Component) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(definition, other.definition) && 
                Objects.equals(expression, other.expression);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    definition, 
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
            private Canonical definition;
            private String expression;

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
             * The definition of the search parameter that describes this part.
             * 
             * <p>This element is required.
             * 
             * @param definition
             *     Defines how the part works
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder definition(Canonical definition) {
                this.definition = definition;
                return this;
            }

            /**
             * Convenience method for setting {@code expression}.
             * 
             * <p>This element is required.
             * 
             * @param expression
             *     Subexpression relative to main expression
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #expression(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder expression(java.lang.String expression) {
                this.expression = (expression == null) ? null : String.of(expression);
                return this;
            }

            /**
             * A sub-expression that defines how to extract values for this component from the output of the main SearchParameter.
             * expression.
             * 
             * <p>This element is required.
             * 
             * @param expression
             *     Subexpression relative to main expression
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder expression(String expression) {
                this.expression = expression;
                return this;
            }

            /**
             * Build the {@link Component}
             * 
             * <p>Required elements:
             * <ul>
             * <li>definition</li>
             * <li>expression</li>
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
                ValidationSupport.requireNonNull(component.definition, "definition");
                ValidationSupport.requireNonNull(component.expression, "expression");
                ValidationSupport.requireValueOrChildren(component);
            }

            protected Builder from(Component component) {
                super.from(component);
                definition = component.definition;
                expression = component.expression;
                return this;
            }
        }
    }
}