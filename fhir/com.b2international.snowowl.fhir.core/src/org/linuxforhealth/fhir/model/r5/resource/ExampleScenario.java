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
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ExampleScenarioActorType;
import org.linuxforhealth.fhir.model.r5.type.code.PublicationStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Example of workflow instance.
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
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "cnl-1",
    level = "Warning",
    location = "ExampleScenario.url",
    description = "URL should not contain | or # - these characters make processing canonical references problematic",
    expression = "exists() implies matches('^[^|# ]+$')",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-1",
    level = "Rule",
    location = "ExampleScenario.instance",
    description = "StructureVersion is required if structureType is not FHIR (but may still be present even if FHIR)",
    expression = "structureType.exists() and structureType.memberOf('http://hl7.org/fhir/ValueSet/resource-types').not() implies structureVersion.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-2",
    level = "Rule",
    location = "ExampleScenario.instance",
    description = "instance.content is only allowed if there are no instance.versions",
    expression = "content.exists() implies version.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-3",
    level = "Rule",
    location = "(base)",
    description = "Must have actors if status is active or required",
    expression = "status='active' or status='retired' implies actor.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-4",
    level = "Rule",
    location = "(base)",
    description = "Must have processes if status is active or required",
    expression = "status='active' or status='retired' implies process.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-5",
    level = "Rule",
    location = "ExampleScenario.process",
    description = "Processes must have steps if ExampleScenario status is active or required",
    expression = "%resource.status='active' or %resource.status='retired' implies step.exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-6",
    level = "Rule",
    location = "(base)",
    description = "Actor keys must be unique",
    expression = "actor.key.count() = actor.key.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-7",
    level = "Rule",
    location = "(base)",
    description = "Actor titles must be unique",
    expression = "actor.title.count() = actor.title.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-8",
    level = "Rule",
    location = "(base)",
    description = "Instance keys must be unique",
    expression = "instance.key.count() = instance.key.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-9",
    level = "Rule",
    location = "(base)",
    description = "Instance titles must be unique",
    expression = "instance.title.count() = instance.title.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-10",
    level = "Rule",
    location = "ExampleScenario.instance",
    description = "Version keys must be unique within an instance",
    expression = "version.key.count() = version.key.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-11",
    level = "Rule",
    location = "ExampleScenario.instance",
    description = "Version titles must be unique within an instance",
    expression = "version.title.count() = version.title.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-12",
    level = "Rule",
    location = "(base)",
    description = "Process titles must be unique",
    expression = "process.title.count() = process.title.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-13",
    level = "Rule",
    location = "ExampleScenario.process.step",
    description = "Alternative titles must be unique within a step",
    expression = "alternative.title.count() = alternative.title.distinct().count()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-14",
    level = "Rule",
    location = "ExampleScenario.instance.containedInstance",
    description = "InstanceReference must be a key of an instance defined in the ExampleScenario",
    expression = "%resource.instance.where(key=%context.instanceReference).exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-15",
    level = "Rule",
    location = "ExampleScenario.instance.containedInstance",
    description = "versionReference must be specified if the referenced instance defines versions",
    expression = "versionReference.empty() implies %resource.instance.where(key=%context.instanceReference).version.empty()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-16",
    level = "Rule",
    location = "ExampleScenario.instance.containedInstance",
    description = "versionReference must be a key of a version within the instance pointed to by instanceReference",
    expression = "versionReference.exists() implies %resource.instance.where(key=%context.instanceReference).version.where(key=%context.versionReference).exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-17",
    level = "Rule",
    location = "ExampleScenario.process.step.operation",
    description = "If specified, initiator must be a key of an actor within the ExampleScenario",
    expression = "initiator.exists() implies initiator = 'OTHER' or %resource.actor.where(key=%context.initiator).exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-18",
    level = "Rule",
    location = "ExampleScenario.process.step.operation",
    description = "If specified, receiver must be a key of an actor within the ExampleScenario",
    expression = "receiver.exists() implies receiver = 'OTHER' or %resource.actor.where(key=%context.receiver).exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-19",
    level = "Warning",
    location = "ExampleScenario.actor",
    description = "Actor should be referenced in at least one operation",
    expression = "%resource.process.descendants().select(operation).where(initiator=%context.key or receiver=%context.key).exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-20",
    level = "Warning",
    location = "ExampleScenario.instance",
    description = "Instance should be referenced in at least one location",
    expression = "%resource.process.descendants().select(instanceReference).where($this=%context.key).exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-21",
    level = "Warning",
    location = "ExampleScenario.instance",
    description = "Instance version should be referenced in at least one operation",
    expression = "version.exists() implies version.key.intersect(%resource.process.descendants().where(instanceReference = %context.key).versionReference).exists()",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-22",
    level = "Rule",
    location = "ExampleScenario.process.step",
    description = "Can have a process, a workflow, one or more operations or none of these, but cannot have a combination",
    expression = "(process.exists() implies workflow.empty() and operation.empty()) and (workflow.exists() implies operation.empty())",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exs-23",
    level = "Rule",
    location = "ExampleScenario.actor",
    description = "actor.key canot be 'OTHER'",
    expression = "key != 'OTHER'",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario"
)
@Constraint(
    id = "exampleScenario-24",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/version-algorithm",
    expression = "versionAlgorithm.as(String).exists() implies (versionAlgorithm.as(String).memberOf('http://hl7.org/fhir/ValueSet/version-algorithm', 'extensible'))",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario",
    generated = true
)
@Constraint(
    id = "exampleScenario-25",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/jurisdiction",
    expression = "jurisdiction.exists() implies (jurisdiction.all(memberOf('http://hl7.org/fhir/ValueSet/jurisdiction', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario",
    generated = true
)
@Constraint(
    id = "exampleScenario-26",
    level = "Warning",
    location = "instance.structureType",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/examplescenario-instance-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/examplescenario-instance-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario",
    generated = true
)
@Constraint(
    id = "exampleScenario-27",
    level = "Warning",
    location = "process.step.operation.type",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/testscript-operation-codes",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/testscript-operation-codes', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ExampleScenario",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ExampleScenario extends DomainResource {
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
    private final Markdown copyright;
    private final String copyrightLabel;
    private final List<Actor> actor;
    private final List<Instance> instance;
    private final List<Process> process;

    private ExampleScenario(Builder builder) {
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
        actor = Collections.unmodifiableList(builder.actor);
        instance = Collections.unmodifiableList(builder.instance);
        process = Collections.unmodifiableList(builder.process);
    }

    /**
     * An absolute URI that is used to identify this example scenario when it is referenced in a specification, model, design 
     * or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address 
     * at which an authoritative instance of this example scenario is (or will be) published. This URL can be the target of a 
     * canonical reference. It SHALL remain the same when the example scenario is stored on different servers.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getUrl() {
        return url;
    }

    /**
     * A formal identifier that is used to identify this example scenario when it is represented in other formats, or 
     * referenced in a specification, model, design or an instance.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The identifier that is used to identify this version of the example scenario when it is referenced in a specification, 
     * model, design or instance. This is an arbitrary value managed by the example scenario author and is not expected to be 
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
     * Temporarily retained for tooling purposes.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getName() {
        return name;
    }

    /**
     * A short, descriptive, user-friendly title for the ExampleScenario.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The status of this example scenario. Enables tracking the life-cycle of the content.
     * 
     * @return
     *     An immutable object of type {@link PublicationStatus} that is non-null.
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * A Boolean value to indicate that this example scenario is authored for testing purposes (or 
     * education/evaluation/marketing) and is not intended to be used for genuine usage.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getExperimental() {
        return experimental;
    }

    /**
     * The date (and optionally time) when the example scenario was last significantly changed. The date must change when the 
     * business version changes and it must change if the status code changes. In addition, it should change when the 
     * substantive content of the example scenario changes. (e.g. the 'content logical definition').
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * The name of the organization or individual responsible for the release and ongoing maintenance of the example scenario.
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
     * A free text natural language description of the ExampleScenario from a consumer's perspective.
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
     * may be used to assist with indexing and searching for appropriate example scenario instances.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link UsageContext} that may be empty.
     */
    public List<UsageContext> getUseContext() {
        return useContext;
    }

    /**
     * A legal or geographic region in which the example scenario is intended to be used.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getJurisdiction() {
        return jurisdiction;
    }

    /**
     * What the example scenario resource is created for. This should not be used to show the business purpose of the 
     * scenario itself, but the purpose of documenting a scenario.
     * 
     * @return
     *     An immutable object of type {@link Markdown} that may be null.
     */
    public Markdown getPurpose() {
        return purpose;
    }

    /**
     * A copyright statement relating to the example scenario and/or its contents. Copyright statements are generally legal 
     * restrictions on the use and publishing of the example scenario.
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
     * A system or person who shares or receives an instance within the scenario.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Actor} that may be empty.
     */
    public List<Actor> getActor() {
        return actor;
    }

    /**
     * A single data collection that is shared as part of the scenario.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Instance} that may be empty.
     */
    public List<Instance> getInstance() {
        return instance;
    }

    /**
     * A group of operations that represents a significant step within a scenario.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Process} that may be empty.
     */
    public List<Process> getProcess() {
        return process;
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
            !actor.isEmpty() || 
            !instance.isEmpty() || 
            !process.isEmpty();
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
                accept(actor, "actor", visitor, Actor.class);
                accept(instance, "instance", visitor, Instance.class);
                accept(process, "process", visitor, Process.class);
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
        ExampleScenario other = (ExampleScenario) obj;
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
            Objects.equals(actor, other.actor) && 
            Objects.equals(instance, other.instance) && 
            Objects.equals(process, other.process);
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
                actor, 
                instance, 
                process);
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
        private List<Actor> actor = new ArrayList<>();
        private List<Instance> instance = new ArrayList<>();
        private List<Process> process = new ArrayList<>();

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
         * An absolute URI that is used to identify this example scenario when it is referenced in a specification, model, design 
         * or an instance; also called its canonical identifier. This SHOULD be globally unique and SHOULD be a literal address 
         * at which an authoritative instance of this example scenario is (or will be) published. This URL can be the target of a 
         * canonical reference. It SHALL remain the same when the example scenario is stored on different servers.
         * 
         * @param url
         *     Canonical identifier for this example scenario, represented as a URI (globally unique)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder url(Uri url) {
            this.url = url;
            return this;
        }

        /**
         * A formal identifier that is used to identify this example scenario when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the example scenario
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
         * A formal identifier that is used to identify this example scenario when it is represented in other formats, or 
         * referenced in a specification, model, design or an instance.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Additional identifier for the example scenario
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
         *     Business version of the example scenario
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
         * The identifier that is used to identify this version of the example scenario when it is referenced in a specification, 
         * model, design or instance. This is an arbitrary value managed by the example scenario author and is not expected to be 
         * globally unique. For example, it might be a timestamp (e.g. yyyymmdd) if a managed version is not available. There is 
         * also no expectation that versions can be placed in a lexicographical sequence.
         * 
         * @param version
         *     Business version of the example scenario
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
         *     To be removed?
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
         * Temporarily retained for tooling purposes.
         * 
         * @param name
         *     To be removed?
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
         *     Name for this example scenario (human friendly)
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
         * A short, descriptive, user-friendly title for the ExampleScenario.
         * 
         * @param title
         *     Name for this example scenario (human friendly)
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * The status of this example scenario. Enables tracking the life-cycle of the content.
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
         * A Boolean value to indicate that this example scenario is authored for testing purposes (or 
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
         * The date (and optionally time) when the example scenario was last significantly changed. The date must change when the 
         * business version changes and it must change if the status code changes. In addition, it should change when the 
         * substantive content of the example scenario changes. (e.g. the 'content logical definition').
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
         * The name of the organization or individual responsible for the release and ongoing maintenance of the example scenario.
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
         * A free text natural language description of the ExampleScenario from a consumer's perspective.
         * 
         * @param description
         *     Natural language description of the ExampleScenario
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
         * may be used to assist with indexing and searching for appropriate example scenario instances.
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
         * may be used to assist with indexing and searching for appropriate example scenario instances.
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
         * A legal or geographic region in which the example scenario is intended to be used.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for example scenario (if applicable)
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
         * A legal or geographic region in which the example scenario is intended to be used.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param jurisdiction
         *     Intended jurisdiction for example scenario (if applicable)
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
         * What the example scenario resource is created for. This should not be used to show the business purpose of the 
         * scenario itself, but the purpose of documenting a scenario.
         * 
         * @param purpose
         *     The purpose of the example, e.g. to illustrate a scenario
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder purpose(Markdown purpose) {
            this.purpose = purpose;
            return this;
        }

        /**
         * A copyright statement relating to the example scenario and/or its contents. Copyright statements are generally legal 
         * restrictions on the use and publishing of the example scenario.
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
         * A system or person who shares or receives an instance within the scenario.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param actor
         *     Individual involved in exchange
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder actor(Actor... actor) {
            for (Actor value : actor) {
                this.actor.add(value);
            }
            return this;
        }

        /**
         * A system or person who shares or receives an instance within the scenario.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param actor
         *     Individual involved in exchange
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder actor(Collection<Actor> actor) {
            this.actor = new ArrayList<>(actor);
            return this;
        }

        /**
         * A single data collection that is shared as part of the scenario.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instance
         *     Data used in the scenario
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instance(Instance... instance) {
            for (Instance value : instance) {
                this.instance.add(value);
            }
            return this;
        }

        /**
         * A single data collection that is shared as part of the scenario.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instance
         *     Data used in the scenario
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instance(Collection<Instance> instance) {
            this.instance = new ArrayList<>(instance);
            return this;
        }

        /**
         * A group of operations that represents a significant step within a scenario.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param process
         *     Major process within scenario
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder process(Process... process) {
            for (Process value : process) {
                this.process.add(value);
            }
            return this;
        }

        /**
         * A group of operations that represents a significant step within a scenario.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param process
         *     Major process within scenario
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder process(Collection<Process> process) {
            this.process = new ArrayList<>(process);
            return this;
        }

        /**
         * Build the {@link ExampleScenario}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ExampleScenario}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ExampleScenario per the base specification
         */
        @Override
        public ExampleScenario build() {
            ExampleScenario exampleScenario = new ExampleScenario(this);
            if (validating) {
                validate(exampleScenario);
            }
            return exampleScenario;
        }

        protected void validate(ExampleScenario exampleScenario) {
            super.validate(exampleScenario);
            ValidationSupport.checkList(exampleScenario.identifier, "identifier", Identifier.class);
            ValidationSupport.choiceElement(exampleScenario.versionAlgorithm, "versionAlgorithm", String.class, Coding.class);
            ValidationSupport.requireNonNull(exampleScenario.status, "status");
            ValidationSupport.checkList(exampleScenario.contact, "contact", ContactDetail.class);
            ValidationSupport.checkList(exampleScenario.useContext, "useContext", UsageContext.class);
            ValidationSupport.checkList(exampleScenario.jurisdiction, "jurisdiction", CodeableConcept.class);
            ValidationSupport.checkList(exampleScenario.actor, "actor", Actor.class);
            ValidationSupport.checkList(exampleScenario.instance, "instance", Instance.class);
            ValidationSupport.checkList(exampleScenario.process, "process", Process.class);
        }

        protected Builder from(ExampleScenario exampleScenario) {
            super.from(exampleScenario);
            url = exampleScenario.url;
            identifier.addAll(exampleScenario.identifier);
            version = exampleScenario.version;
            versionAlgorithm = exampleScenario.versionAlgorithm;
            name = exampleScenario.name;
            title = exampleScenario.title;
            status = exampleScenario.status;
            experimental = exampleScenario.experimental;
            date = exampleScenario.date;
            publisher = exampleScenario.publisher;
            contact.addAll(exampleScenario.contact);
            description = exampleScenario.description;
            useContext.addAll(exampleScenario.useContext);
            jurisdiction.addAll(exampleScenario.jurisdiction);
            purpose = exampleScenario.purpose;
            copyright = exampleScenario.copyright;
            copyrightLabel = exampleScenario.copyrightLabel;
            actor.addAll(exampleScenario.actor);
            instance.addAll(exampleScenario.instance);
            process.addAll(exampleScenario.process);
            return this;
        }
    }

    /**
     * A system or person who shares or receives an instance within the scenario.
     */
    public static class Actor extends BackboneElement {
        @Required
        private final String key;
        @Binding(
            bindingName = "ExampleScenarioActorType",
            strength = BindingStrength.Value.REQUIRED,
            description = "The type of actor - system or human.",
            valueSet = "http://hl7.org/fhir/ValueSet/examplescenario-actor-type|5.0.0"
        )
        @Required
        private final ExampleScenarioActorType type;
        @Required
        private final String title;
        private final Markdown description;

        private Actor(Builder builder) {
            super(builder);
            key = builder.key;
            type = builder.type;
            title = builder.title;
            description = builder.description;
        }

        /**
         * A unique string within the scenario that is used to reference the actor.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getKey() {
            return key;
        }

        /**
         * The category of actor - person or system.
         * 
         * @return
         *     An immutable object of type {@link ExampleScenarioActorType} that is non-null.
         */
        public ExampleScenarioActorType getType() {
            return type;
        }

        /**
         * The human-readable name for the actor used when rendering the scenario.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getTitle() {
            return title;
        }

        /**
         * An explanation of who/what the actor is and its role in the scenario.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (key != null) || 
                (type != null) || 
                (title != null) || 
                (description != null);
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
                    accept(type, "type", visitor);
                    accept(title, "title", visitor);
                    accept(description, "description", visitor);
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
            Actor other = (Actor) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(key, other.key) && 
                Objects.equals(type, other.type) && 
                Objects.equals(title, other.title) && 
                Objects.equals(description, other.description);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    key, 
                    type, 
                    title, 
                    description);
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
            private String key;
            private ExampleScenarioActorType type;
            private String title;
            private Markdown description;

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
             * Convenience method for setting {@code key}.
             * 
             * <p>This element is required.
             * 
             * @param key
             *     ID or acronym of the actor
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #key(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder key(java.lang.String key) {
                this.key = (key == null) ? null : String.of(key);
                return this;
            }

            /**
             * A unique string within the scenario that is used to reference the actor.
             * 
             * <p>This element is required.
             * 
             * @param key
             *     ID or acronym of the actor
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder key(String key) {
                this.key = key;
                return this;
            }

            /**
             * The category of actor - person or system.
             * 
             * <p>This element is required.
             * 
             * @param type
             *     person | system
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(ExampleScenarioActorType type) {
                this.type = type;
                return this;
            }

            /**
             * Convenience method for setting {@code title}.
             * 
             * <p>This element is required.
             * 
             * @param title
             *     Label for actor when rendering
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
             * The human-readable name for the actor used when rendering the scenario.
             * 
             * <p>This element is required.
             * 
             * @param title
             *     Label for actor when rendering
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder title(String title) {
                this.title = title;
                return this;
            }

            /**
             * An explanation of who/what the actor is and its role in the scenario.
             * 
             * @param description
             *     Details about actor
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * Build the {@link Actor}
             * 
             * <p>Required elements:
             * <ul>
             * <li>key</li>
             * <li>type</li>
             * <li>title</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Actor}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Actor per the base specification
             */
            @Override
            public Actor build() {
                Actor actor = new Actor(this);
                if (validating) {
                    validate(actor);
                }
                return actor;
            }

            protected void validate(Actor actor) {
                super.validate(actor);
                ValidationSupport.requireNonNull(actor.key, "key");
                ValidationSupport.requireNonNull(actor.type, "type");
                ValidationSupport.requireNonNull(actor.title, "title");
                ValidationSupport.requireValueOrChildren(actor);
            }

            protected Builder from(Actor actor) {
                super.from(actor);
                key = actor.key;
                type = actor.type;
                title = actor.title;
                description = actor.description;
                return this;
            }
        }
    }

    /**
     * A single data collection that is shared as part of the scenario.
     */
    public static class Instance extends BackboneElement {
        @Required
        private final String key;
        @Binding(
            bindingName = "InstanceType",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The structure that defines the instance",
            valueSet = "http://hl7.org/fhir/ValueSet/examplescenario-instance-type"
        )
        @Required
        private final Coding structureType;
        private final String structureVersion;
        @Choice({ Canonical.class, Uri.class })
        private final Element structureProfile;
        @Required
        private final String title;
        private final Markdown description;
        private final Reference content;
        private final List<Version> version;
        private final List<ContainedInstance> containedInstance;

        private Instance(Builder builder) {
            super(builder);
            key = builder.key;
            structureType = builder.structureType;
            structureVersion = builder.structureVersion;
            structureProfile = builder.structureProfile;
            title = builder.title;
            description = builder.description;
            content = builder.content;
            version = Collections.unmodifiableList(builder.version);
            containedInstance = Collections.unmodifiableList(builder.containedInstance);
        }

        /**
         * A unique string within the scenario that is used to reference the instance.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getKey() {
            return key;
        }

        /**
         * A code indicating the kind of data structure (FHIR resource or some other standard) this is an instance of.
         * 
         * @return
         *     An immutable object of type {@link Coding} that is non-null.
         */
        public Coding getStructureType() {
            return structureType;
        }

        /**
         * Conveys the version of the data structure instantiated. I.e. what release of FHIR, X12, OpenEHR, etc. is instance 
         * compliant with.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getStructureVersion() {
            return structureVersion;
        }

        /**
         * Refers to a profile, template or other ruleset the instance adheres to.
         * 
         * @return
         *     An immutable object of type {@link Canonical} or {@link Uri} that may be null.
         */
        public Element getStructureProfile() {
            return structureProfile;
        }

        /**
         * A short descriptive label the instance to be used in tables or diagrams.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getTitle() {
            return title;
        }

        /**
         * An explanation of what the instance contains and what it's for.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * Points to an instance (typically an example) that shows the data that would corespond to this instance.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getContent() {
            return content;
        }

        /**
         * Represents the instance as it was at a specific time-point.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Version} that may be empty.
         */
        public List<Version> getVersion() {
            return version;
        }

        /**
         * References to other instances that can be found within this instance (e.g. the observations contained in a bundle).
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link ContainedInstance} that may be empty.
         */
        public List<ContainedInstance> getContainedInstance() {
            return containedInstance;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (key != null) || 
                (structureType != null) || 
                (structureVersion != null) || 
                (structureProfile != null) || 
                (title != null) || 
                (description != null) || 
                (content != null) || 
                !version.isEmpty() || 
                !containedInstance.isEmpty();
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
                    accept(structureType, "structureType", visitor);
                    accept(structureVersion, "structureVersion", visitor);
                    accept(structureProfile, "structureProfile", visitor);
                    accept(title, "title", visitor);
                    accept(description, "description", visitor);
                    accept(content, "content", visitor);
                    accept(version, "version", visitor, Version.class);
                    accept(containedInstance, "containedInstance", visitor, ContainedInstance.class);
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
            Instance other = (Instance) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(key, other.key) && 
                Objects.equals(structureType, other.structureType) && 
                Objects.equals(structureVersion, other.structureVersion) && 
                Objects.equals(structureProfile, other.structureProfile) && 
                Objects.equals(title, other.title) && 
                Objects.equals(description, other.description) && 
                Objects.equals(content, other.content) && 
                Objects.equals(version, other.version) && 
                Objects.equals(containedInstance, other.containedInstance);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    key, 
                    structureType, 
                    structureVersion, 
                    structureProfile, 
                    title, 
                    description, 
                    content, 
                    version, 
                    containedInstance);
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
            private String key;
            private Coding structureType;
            private String structureVersion;
            private Element structureProfile;
            private String title;
            private Markdown description;
            private Reference content;
            private List<Version> version = new ArrayList<>();
            private List<ContainedInstance> containedInstance = new ArrayList<>();

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
             * Convenience method for setting {@code key}.
             * 
             * <p>This element is required.
             * 
             * @param key
             *     ID or acronym of the instance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #key(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder key(java.lang.String key) {
                this.key = (key == null) ? null : String.of(key);
                return this;
            }

            /**
             * A unique string within the scenario that is used to reference the instance.
             * 
             * <p>This element is required.
             * 
             * @param key
             *     ID or acronym of the instance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder key(String key) {
                this.key = key;
                return this;
            }

            /**
             * A code indicating the kind of data structure (FHIR resource or some other standard) this is an instance of.
             * 
             * <p>This element is required.
             * 
             * @param structureType
             *     Data structure for example
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder structureType(Coding structureType) {
                this.structureType = structureType;
                return this;
            }

            /**
             * Convenience method for setting {@code structureVersion}.
             * 
             * @param structureVersion
             *     E.g. 4.0.1
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #structureVersion(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder structureVersion(java.lang.String structureVersion) {
                this.structureVersion = (structureVersion == null) ? null : String.of(structureVersion);
                return this;
            }

            /**
             * Conveys the version of the data structure instantiated. I.e. what release of FHIR, X12, OpenEHR, etc. is instance 
             * compliant with.
             * 
             * @param structureVersion
             *     E.g. 4.0.1
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder structureVersion(String structureVersion) {
                this.structureVersion = structureVersion;
                return this;
            }

            /**
             * Refers to a profile, template or other ruleset the instance adheres to.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Canonical}</li>
             * <li>{@link Uri}</li>
             * </ul>
             * 
             * @param structureProfile
             *     Rules instance adheres to
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder structureProfile(Element structureProfile) {
                this.structureProfile = structureProfile;
                return this;
            }

            /**
             * Convenience method for setting {@code title}.
             * 
             * <p>This element is required.
             * 
             * @param title
             *     Label for instance
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
             * A short descriptive label the instance to be used in tables or diagrams.
             * 
             * <p>This element is required.
             * 
             * @param title
             *     Label for instance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder title(String title) {
                this.title = title;
                return this;
            }

            /**
             * An explanation of what the instance contains and what it's for.
             * 
             * @param description
             *     Human-friendly description of the instance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * Points to an instance (typically an example) that shows the data that would corespond to this instance.
             * 
             * @param content
             *     Example instance data
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder content(Reference content) {
                this.content = content;
                return this;
            }

            /**
             * Represents the instance as it was at a specific time-point.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param version
             *     Snapshot of instance that changes
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder version(Version... version) {
                for (Version value : version) {
                    this.version.add(value);
                }
                return this;
            }

            /**
             * Represents the instance as it was at a specific time-point.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param version
             *     Snapshot of instance that changes
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder version(Collection<Version> version) {
                this.version = new ArrayList<>(version);
                return this;
            }

            /**
             * References to other instances that can be found within this instance (e.g. the observations contained in a bundle).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param containedInstance
             *     Resources contained in the instance
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder containedInstance(ContainedInstance... containedInstance) {
                for (ContainedInstance value : containedInstance) {
                    this.containedInstance.add(value);
                }
                return this;
            }

            /**
             * References to other instances that can be found within this instance (e.g. the observations contained in a bundle).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param containedInstance
             *     Resources contained in the instance
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder containedInstance(Collection<ContainedInstance> containedInstance) {
                this.containedInstance = new ArrayList<>(containedInstance);
                return this;
            }

            /**
             * Build the {@link Instance}
             * 
             * <p>Required elements:
             * <ul>
             * <li>key</li>
             * <li>structureType</li>
             * <li>title</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Instance}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Instance per the base specification
             */
            @Override
            public Instance build() {
                Instance instance = new Instance(this);
                if (validating) {
                    validate(instance);
                }
                return instance;
            }

            protected void validate(Instance instance) {
                super.validate(instance);
                ValidationSupport.requireNonNull(instance.key, "key");
                ValidationSupport.requireNonNull(instance.structureType, "structureType");
                ValidationSupport.choiceElement(instance.structureProfile, "structureProfile", Canonical.class, Uri.class);
                ValidationSupport.requireNonNull(instance.title, "title");
                ValidationSupport.checkList(instance.version, "version", Version.class);
                ValidationSupport.checkList(instance.containedInstance, "containedInstance", ContainedInstance.class);
                ValidationSupport.requireValueOrChildren(instance);
            }

            protected Builder from(Instance instance) {
                super.from(instance);
                key = instance.key;
                structureType = instance.structureType;
                structureVersion = instance.structureVersion;
                structureProfile = instance.structureProfile;
                title = instance.title;
                description = instance.description;
                content = instance.content;
                version.addAll(instance.version);
                containedInstance.addAll(instance.containedInstance);
                return this;
            }
        }

        /**
         * Represents the instance as it was at a specific time-point.
         */
        public static class Version extends BackboneElement {
            @Required
            private final String key;
            @Required
            private final String title;
            private final Markdown description;
            private final Reference content;

            private Version(Builder builder) {
                super(builder);
                key = builder.key;
                title = builder.title;
                description = builder.description;
                content = builder.content;
            }

            /**
             * A unique string within the instance that is used to reference the version of the instance.
             * 
             * @return
             *     An immutable object of type {@link String} that is non-null.
             */
            public String getKey() {
                return key;
            }

            /**
             * A short descriptive label the version to be used in tables or diagrams.
             * 
             * @return
             *     An immutable object of type {@link String} that is non-null.
             */
            public String getTitle() {
                return title;
            }

            /**
             * An explanation of what this specific version of the instance contains and represents.
             * 
             * @return
             *     An immutable object of type {@link Markdown} that may be null.
             */
            public Markdown getDescription() {
                return description;
            }

            /**
             * Points to an instance (typically an example) that shows the data that would flow at this point in the scenario.
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getContent() {
                return content;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (key != null) || 
                    (title != null) || 
                    (description != null) || 
                    (content != null);
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
                        accept(title, "title", visitor);
                        accept(description, "description", visitor);
                        accept(content, "content", visitor);
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
                Version other = (Version) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(key, other.key) && 
                    Objects.equals(title, other.title) && 
                    Objects.equals(description, other.description) && 
                    Objects.equals(content, other.content);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        key, 
                        title, 
                        description, 
                        content);
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
                private String key;
                private String title;
                private Markdown description;
                private Reference content;

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
                 * Convenience method for setting {@code key}.
                 * 
                 * <p>This element is required.
                 * 
                 * @param key
                 *     ID or acronym of the version
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #key(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder key(java.lang.String key) {
                    this.key = (key == null) ? null : String.of(key);
                    return this;
                }

                /**
                 * A unique string within the instance that is used to reference the version of the instance.
                 * 
                 * <p>This element is required.
                 * 
                 * @param key
                 *     ID or acronym of the version
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder key(String key) {
                    this.key = key;
                    return this;
                }

                /**
                 * Convenience method for setting {@code title}.
                 * 
                 * <p>This element is required.
                 * 
                 * @param title
                 *     Label for instance version
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
                 * A short descriptive label the version to be used in tables or diagrams.
                 * 
                 * <p>This element is required.
                 * 
                 * @param title
                 *     Label for instance version
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder title(String title) {
                    this.title = title;
                    return this;
                }

                /**
                 * An explanation of what this specific version of the instance contains and represents.
                 * 
                 * @param description
                 *     Details about version
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder description(Markdown description) {
                    this.description = description;
                    return this;
                }

                /**
                 * Points to an instance (typically an example) that shows the data that would flow at this point in the scenario.
                 * 
                 * @param content
                 *     Example instance version data
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder content(Reference content) {
                    this.content = content;
                    return this;
                }

                /**
                 * Build the {@link Version}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>key</li>
                 * <li>title</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Version}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Version per the base specification
                 */
                @Override
                public Version build() {
                    Version version = new Version(this);
                    if (validating) {
                        validate(version);
                    }
                    return version;
                }

                protected void validate(Version version) {
                    super.validate(version);
                    ValidationSupport.requireNonNull(version.key, "key");
                    ValidationSupport.requireNonNull(version.title, "title");
                    ValidationSupport.requireValueOrChildren(version);
                }

                protected Builder from(Version version) {
                    super.from(version);
                    key = version.key;
                    title = version.title;
                    description = version.description;
                    content = version.content;
                    return this;
                }
            }
        }

        /**
         * References to other instances that can be found within this instance (e.g. the observations contained in a bundle).
         */
        public static class ContainedInstance extends BackboneElement {
            @Required
            private final String instanceReference;
            private final String versionReference;

            private ContainedInstance(Builder builder) {
                super(builder);
                instanceReference = builder.instanceReference;
                versionReference = builder.versionReference;
            }

            /**
             * A reference to the key of an instance found within this one.
             * 
             * @return
             *     An immutable object of type {@link String} that is non-null.
             */
            public String getInstanceReference() {
                return instanceReference;
            }

            /**
             * A reference to the key of a specific version of an instance in this instance.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getVersionReference() {
                return versionReference;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (instanceReference != null) || 
                    (versionReference != null);
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
                        accept(instanceReference, "instanceReference", visitor);
                        accept(versionReference, "versionReference", visitor);
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
                ContainedInstance other = (ContainedInstance) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(instanceReference, other.instanceReference) && 
                    Objects.equals(versionReference, other.versionReference);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        instanceReference, 
                        versionReference);
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
                private String instanceReference;
                private String versionReference;

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
                 * Convenience method for setting {@code instanceReference}.
                 * 
                 * <p>This element is required.
                 * 
                 * @param instanceReference
                 *     Key of contained instance
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #instanceReference(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder instanceReference(java.lang.String instanceReference) {
                    this.instanceReference = (instanceReference == null) ? null : String.of(instanceReference);
                    return this;
                }

                /**
                 * A reference to the key of an instance found within this one.
                 * 
                 * <p>This element is required.
                 * 
                 * @param instanceReference
                 *     Key of contained instance
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder instanceReference(String instanceReference) {
                    this.instanceReference = instanceReference;
                    return this;
                }

                /**
                 * Convenience method for setting {@code versionReference}.
                 * 
                 * @param versionReference
                 *     Key of contained instance version
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #versionReference(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder versionReference(java.lang.String versionReference) {
                    this.versionReference = (versionReference == null) ? null : String.of(versionReference);
                    return this;
                }

                /**
                 * A reference to the key of a specific version of an instance in this instance.
                 * 
                 * @param versionReference
                 *     Key of contained instance version
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder versionReference(String versionReference) {
                    this.versionReference = versionReference;
                    return this;
                }

                /**
                 * Build the {@link ContainedInstance}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>instanceReference</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link ContainedInstance}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid ContainedInstance per the base specification
                 */
                @Override
                public ContainedInstance build() {
                    ContainedInstance containedInstance = new ContainedInstance(this);
                    if (validating) {
                        validate(containedInstance);
                    }
                    return containedInstance;
                }

                protected void validate(ContainedInstance containedInstance) {
                    super.validate(containedInstance);
                    ValidationSupport.requireNonNull(containedInstance.instanceReference, "instanceReference");
                    ValidationSupport.requireValueOrChildren(containedInstance);
                }

                protected Builder from(ContainedInstance containedInstance) {
                    super.from(containedInstance);
                    instanceReference = containedInstance.instanceReference;
                    versionReference = containedInstance.versionReference;
                    return this;
                }
            }
        }
    }

    /**
     * A group of operations that represents a significant step within a scenario.
     */
    public static class Process extends BackboneElement {
        @Summary
        @Required
        private final String title;
        private final Markdown description;
        private final Markdown preConditions;
        private final Markdown postConditions;
        private final List<Step> step;

        private Process(Builder builder) {
            super(builder);
            title = builder.title;
            description = builder.description;
            preConditions = builder.preConditions;
            postConditions = builder.postConditions;
            step = Collections.unmodifiableList(builder.step);
        }

        /**
         * A short descriptive label the process to be used in tables or diagrams.
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getTitle() {
            return title;
        }

        /**
         * An explanation of what the process represents and what it does.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getDescription() {
            return description;
        }

        /**
         * Description of the initial state of the actors, environment and data before the process starts.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getPreConditions() {
            return preConditions;
        }

        /**
         * Description of the final state of the actors, environment and data after the process has been successfully completed.
         * 
         * @return
         *     An immutable object of type {@link Markdown} that may be null.
         */
        public Markdown getPostConditions() {
            return postConditions;
        }

        /**
         * A significant action that occurs as part of the process.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Step} that may be empty.
         */
        public List<Step> getStep() {
            return step;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (title != null) || 
                (description != null) || 
                (preConditions != null) || 
                (postConditions != null) || 
                !step.isEmpty();
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
                    accept(description, "description", visitor);
                    accept(preConditions, "preConditions", visitor);
                    accept(postConditions, "postConditions", visitor);
                    accept(step, "step", visitor, Step.class);
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
            Process other = (Process) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(title, other.title) && 
                Objects.equals(description, other.description) && 
                Objects.equals(preConditions, other.preConditions) && 
                Objects.equals(postConditions, other.postConditions) && 
                Objects.equals(step, other.step);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    title, 
                    description, 
                    preConditions, 
                    postConditions, 
                    step);
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
            private Markdown description;
            private Markdown preConditions;
            private Markdown postConditions;
            private List<Step> step = new ArrayList<>();

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
             * <p>This element is required.
             * 
             * @param title
             *     Label for procss
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
             * A short descriptive label the process to be used in tables or diagrams.
             * 
             * <p>This element is required.
             * 
             * @param title
             *     Label for procss
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder title(String title) {
                this.title = title;
                return this;
            }

            /**
             * An explanation of what the process represents and what it does.
             * 
             * @param description
             *     Human-friendly description of the process
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(Markdown description) {
                this.description = description;
                return this;
            }

            /**
             * Description of the initial state of the actors, environment and data before the process starts.
             * 
             * @param preConditions
             *     Status before process starts
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder preConditions(Markdown preConditions) {
                this.preConditions = preConditions;
                return this;
            }

            /**
             * Description of the final state of the actors, environment and data after the process has been successfully completed.
             * 
             * @param postConditions
             *     Status after successful completion
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder postConditions(Markdown postConditions) {
                this.postConditions = postConditions;
                return this;
            }

            /**
             * A significant action that occurs as part of the process.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param step
             *     Event within of the process
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder step(Step... step) {
                for (Step value : step) {
                    this.step.add(value);
                }
                return this;
            }

            /**
             * A significant action that occurs as part of the process.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param step
             *     Event within of the process
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder step(Collection<Step> step) {
                this.step = new ArrayList<>(step);
                return this;
            }

            /**
             * Build the {@link Process}
             * 
             * <p>Required elements:
             * <ul>
             * <li>title</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Process}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Process per the base specification
             */
            @Override
            public Process build() {
                Process process = new Process(this);
                if (validating) {
                    validate(process);
                }
                return process;
            }

            protected void validate(Process process) {
                super.validate(process);
                ValidationSupport.requireNonNull(process.title, "title");
                ValidationSupport.checkList(process.step, "step", Step.class);
                ValidationSupport.requireValueOrChildren(process);
            }

            protected Builder from(Process process) {
                super.from(process);
                title = process.title;
                description = process.description;
                preConditions = process.preConditions;
                postConditions = process.postConditions;
                step.addAll(process.step);
                return this;
            }
        }

        /**
         * A significant action that occurs as part of the process.
         */
        public static class Step extends BackboneElement {
            private final String number;
            private final ExampleScenario.Process process;
            private final Canonical workflow;
            private final Operation operation;
            private final List<Alternative> alternative;
            private final Boolean pause;

            private Step(Builder builder) {
                super(builder);
                number = builder.number;
                process = builder.process;
                workflow = builder.workflow;
                operation = builder.operation;
                alternative = Collections.unmodifiableList(builder.alternative);
                pause = builder.pause;
            }

            /**
             * The sequential number of the step, e.g. 1.2.5.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getNumber() {
                return number;
            }

            /**
             * Indicates that the step is a complex sub-process with its own steps.
             * 
             * @return
             *     An immutable object of type {@link ExampleScenario.Process} that may be null.
             */
            public ExampleScenario.Process getProcess() {
                return process;
            }

            /**
             * Indicates that the step is defined by a seaparate scenario instance.
             * 
             * @return
             *     An immutable object of type {@link Canonical} that may be null.
             */
            public Canonical getWorkflow() {
                return workflow;
            }

            /**
             * The step represents a single operation invoked on receiver by sender.
             * 
             * @return
             *     An immutable object of type {@link Operation} that may be null.
             */
            public Operation getOperation() {
                return operation;
            }

            /**
             * Indicates an alternative step that can be taken instead of the sub-process, scenario or operation. E.g. to represent 
             * non-happy-path/exceptional/atypical circumstances.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Alternative} that may be empty.
             */
            public List<Alternative> getAlternative() {
                return alternative;
            }

            /**
             * If true, indicates that, following this step, there is a pause in the flow and the subsequent step will occur at some 
             * later time (triggered by some event).
             * 
             * @return
             *     An immutable object of type {@link Boolean} that may be null.
             */
            public Boolean getPause() {
                return pause;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (number != null) || 
                    (process != null) || 
                    (workflow != null) || 
                    (operation != null) || 
                    !alternative.isEmpty() || 
                    (pause != null);
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
                        accept(number, "number", visitor);
                        accept(process, "process", visitor);
                        accept(workflow, "workflow", visitor);
                        accept(operation, "operation", visitor);
                        accept(alternative, "alternative", visitor, Alternative.class);
                        accept(pause, "pause", visitor);
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
                Step other = (Step) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(number, other.number) && 
                    Objects.equals(process, other.process) && 
                    Objects.equals(workflow, other.workflow) && 
                    Objects.equals(operation, other.operation) && 
                    Objects.equals(alternative, other.alternative) && 
                    Objects.equals(pause, other.pause);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        number, 
                        process, 
                        workflow, 
                        operation, 
                        alternative, 
                        pause);
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
                private String number;
                private ExampleScenario.Process process;
                private Canonical workflow;
                private Operation operation;
                private List<Alternative> alternative = new ArrayList<>();
                private Boolean pause;

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
                 * Convenience method for setting {@code number}.
                 * 
                 * @param number
                 *     Sequential number of the step
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #number(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder number(java.lang.String number) {
                    this.number = (number == null) ? null : String.of(number);
                    return this;
                }

                /**
                 * The sequential number of the step, e.g. 1.2.5.
                 * 
                 * @param number
                 *     Sequential number of the step
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder number(String number) {
                    this.number = number;
                    return this;
                }

                /**
                 * Indicates that the step is a complex sub-process with its own steps.
                 * 
                 * @param process
                 *     Step is nested process
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder process(ExampleScenario.Process process) {
                    this.process = process;
                    return this;
                }

                /**
                 * Indicates that the step is defined by a seaparate scenario instance.
                 * 
                 * @param workflow
                 *     Step is nested workflow
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder workflow(Canonical workflow) {
                    this.workflow = workflow;
                    return this;
                }

                /**
                 * The step represents a single operation invoked on receiver by sender.
                 * 
                 * @param operation
                 *     Step is simple action
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder operation(Operation operation) {
                    this.operation = operation;
                    return this;
                }

                /**
                 * Indicates an alternative step that can be taken instead of the sub-process, scenario or operation. E.g. to represent 
                 * non-happy-path/exceptional/atypical circumstances.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param alternative
                 *     Alternate non-typical step action
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder alternative(Alternative... alternative) {
                    for (Alternative value : alternative) {
                        this.alternative.add(value);
                    }
                    return this;
                }

                /**
                 * Indicates an alternative step that can be taken instead of the sub-process, scenario or operation. E.g. to represent 
                 * non-happy-path/exceptional/atypical circumstances.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param alternative
                 *     Alternate non-typical step action
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder alternative(Collection<Alternative> alternative) {
                    this.alternative = new ArrayList<>(alternative);
                    return this;
                }

                /**
                 * Convenience method for setting {@code pause}.
                 * 
                 * @param pause
                 *     Pause in the flow?
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #pause(org.linuxforhealth.fhir.model.type.Boolean)
                 */
                public Builder pause(java.lang.Boolean pause) {
                    this.pause = (pause == null) ? null : Boolean.of(pause);
                    return this;
                }

                /**
                 * If true, indicates that, following this step, there is a pause in the flow and the subsequent step will occur at some 
                 * later time (triggered by some event).
                 * 
                 * @param pause
                 *     Pause in the flow?
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder pause(Boolean pause) {
                    this.pause = pause;
                    return this;
                }

                /**
                 * Build the {@link Step}
                 * 
                 * @return
                 *     An immutable object of type {@link Step}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Step per the base specification
                 */
                @Override
                public Step build() {
                    Step step = new Step(this);
                    if (validating) {
                        validate(step);
                    }
                    return step;
                }

                protected void validate(Step step) {
                    super.validate(step);
                    ValidationSupport.checkList(step.alternative, "alternative", Alternative.class);
                    ValidationSupport.requireValueOrChildren(step);
                }

                protected Builder from(Step step) {
                    super.from(step);
                    number = step.number;
                    process = step.process;
                    workflow = step.workflow;
                    operation = step.operation;
                    alternative.addAll(step.alternative);
                    pause = step.pause;
                    return this;
                }
            }

            /**
             * The step represents a single operation invoked on receiver by sender.
             */
            public static class Operation extends BackboneElement {
                @Binding(
                    strength = BindingStrength.Value.EXTENSIBLE,
                    valueSet = "http://hl7.org/fhir/ValueSet/testscript-operation-codes"
                )
                private final Coding type;
                @Required
                private final String title;
                private final String initiator;
                private final String receiver;
                private final Markdown description;
                private final Boolean initiatorActive;
                private final Boolean receiverActive;
                private final ExampleScenario.Instance.ContainedInstance request;
                private final ExampleScenario.Instance.ContainedInstance response;

                private Operation(Builder builder) {
                    super(builder);
                    type = builder.type;
                    title = builder.title;
                    initiator = builder.initiator;
                    receiver = builder.receiver;
                    description = builder.description;
                    initiatorActive = builder.initiatorActive;
                    receiverActive = builder.receiverActive;
                    request = builder.request;
                    response = builder.response;
                }

                /**
                 * The standardized type of action (FHIR or otherwise).
                 * 
                 * @return
                 *     An immutable object of type {@link Coding} that may be null.
                 */
                public Coding getType() {
                    return type;
                }

                /**
                 * A short descriptive label the step to be used in tables or diagrams.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that is non-null.
                 */
                public String getTitle() {
                    return title;
                }

                /**
                 * The system that invokes the action/transmits the data.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getInitiator() {
                    return initiator;
                }

                /**
                 * The system on which the action is invoked/receives the data.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that may be null.
                 */
                public String getReceiver() {
                    return receiver;
                }

                /**
                 * An explanation of what the operation represents and what it does.
                 * 
                 * @return
                 *     An immutable object of type {@link Markdown} that may be null.
                 */
                public Markdown getDescription() {
                    return description;
                }

                /**
                 * If false, the initiator is deactivated right after the operation.
                 * 
                 * @return
                 *     An immutable object of type {@link Boolean} that may be null.
                 */
                public Boolean getInitiatorActive() {
                    return initiatorActive;
                }

                /**
                 * If false, the receiver is deactivated right after the operation.
                 * 
                 * @return
                 *     An immutable object of type {@link Boolean} that may be null.
                 */
                public Boolean getReceiverActive() {
                    return receiverActive;
                }

                /**
                 * A reference to the instance that is transmitted from requester to receiver as part of the invocation of the operation.
                 * 
                 * @return
                 *     An immutable object of type {@link ExampleScenario.Instance.ContainedInstance} that may be null.
                 */
                public ExampleScenario.Instance.ContainedInstance getRequest() {
                    return request;
                }

                /**
                 * A reference to the instance that is transmitted from receiver to requester as part of the operation's synchronous 
                 * response (if any).
                 * 
                 * @return
                 *     An immutable object of type {@link ExampleScenario.Instance.ContainedInstance} that may be null.
                 */
                public ExampleScenario.Instance.ContainedInstance getResponse() {
                    return response;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (type != null) || 
                        (title != null) || 
                        (initiator != null) || 
                        (receiver != null) || 
                        (description != null) || 
                        (initiatorActive != null) || 
                        (receiverActive != null) || 
                        (request != null) || 
                        (response != null);
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
                            accept(title, "title", visitor);
                            accept(initiator, "initiator", visitor);
                            accept(receiver, "receiver", visitor);
                            accept(description, "description", visitor);
                            accept(initiatorActive, "initiatorActive", visitor);
                            accept(receiverActive, "receiverActive", visitor);
                            accept(request, "request", visitor);
                            accept(response, "response", visitor);
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
                    Operation other = (Operation) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(type, other.type) && 
                        Objects.equals(title, other.title) && 
                        Objects.equals(initiator, other.initiator) && 
                        Objects.equals(receiver, other.receiver) && 
                        Objects.equals(description, other.description) && 
                        Objects.equals(initiatorActive, other.initiatorActive) && 
                        Objects.equals(receiverActive, other.receiverActive) && 
                        Objects.equals(request, other.request) && 
                        Objects.equals(response, other.response);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            type, 
                            title, 
                            initiator, 
                            receiver, 
                            description, 
                            initiatorActive, 
                            receiverActive, 
                            request, 
                            response);
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
                    private Coding type;
                    private String title;
                    private String initiator;
                    private String receiver;
                    private Markdown description;
                    private Boolean initiatorActive;
                    private Boolean receiverActive;
                    private ExampleScenario.Instance.ContainedInstance request;
                    private ExampleScenario.Instance.ContainedInstance response;

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
                     * The standardized type of action (FHIR or otherwise).
                     * 
                     * @param type
                     *     Kind of action
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder type(Coding type) {
                        this.type = type;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code title}.
                     * 
                     * <p>This element is required.
                     * 
                     * @param title
                     *     Label for step
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
                     * A short descriptive label the step to be used in tables or diagrams.
                     * 
                     * <p>This element is required.
                     * 
                     * @param title
                     *     Label for step
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder title(String title) {
                        this.title = title;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code initiator}.
                     * 
                     * @param initiator
                     *     Who starts the operation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #initiator(org.linuxforhealth.fhir.model.type.String)
                     */
                    public Builder initiator(java.lang.String initiator) {
                        this.initiator = (initiator == null) ? null : String.of(initiator);
                        return this;
                    }

                    /**
                     * The system that invokes the action/transmits the data.
                     * 
                     * @param initiator
                     *     Who starts the operation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder initiator(String initiator) {
                        this.initiator = initiator;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code receiver}.
                     * 
                     * @param receiver
                     *     Who receives the operation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #receiver(org.linuxforhealth.fhir.model.type.String)
                     */
                    public Builder receiver(java.lang.String receiver) {
                        this.receiver = (receiver == null) ? null : String.of(receiver);
                        return this;
                    }

                    /**
                     * The system on which the action is invoked/receives the data.
                     * 
                     * @param receiver
                     *     Who receives the operation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder receiver(String receiver) {
                        this.receiver = receiver;
                        return this;
                    }

                    /**
                     * An explanation of what the operation represents and what it does.
                     * 
                     * @param description
                     *     Human-friendly description of the operation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder description(Markdown description) {
                        this.description = description;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code initiatorActive}.
                     * 
                     * @param initiatorActive
                     *     Initiator stays active?
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #initiatorActive(org.linuxforhealth.fhir.model.type.Boolean)
                     */
                    public Builder initiatorActive(java.lang.Boolean initiatorActive) {
                        this.initiatorActive = (initiatorActive == null) ? null : Boolean.of(initiatorActive);
                        return this;
                    }

                    /**
                     * If false, the initiator is deactivated right after the operation.
                     * 
                     * @param initiatorActive
                     *     Initiator stays active?
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder initiatorActive(Boolean initiatorActive) {
                        this.initiatorActive = initiatorActive;
                        return this;
                    }

                    /**
                     * Convenience method for setting {@code receiverActive}.
                     * 
                     * @param receiverActive
                     *     Receiver stays active?
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @see #receiverActive(org.linuxforhealth.fhir.model.type.Boolean)
                     */
                    public Builder receiverActive(java.lang.Boolean receiverActive) {
                        this.receiverActive = (receiverActive == null) ? null : Boolean.of(receiverActive);
                        return this;
                    }

                    /**
                     * If false, the receiver is deactivated right after the operation.
                     * 
                     * @param receiverActive
                     *     Receiver stays active?
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder receiverActive(Boolean receiverActive) {
                        this.receiverActive = receiverActive;
                        return this;
                    }

                    /**
                     * A reference to the instance that is transmitted from requester to receiver as part of the invocation of the operation.
                     * 
                     * @param request
                     *     Instance transmitted on invocation
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder request(ExampleScenario.Instance.ContainedInstance request) {
                        this.request = request;
                        return this;
                    }

                    /**
                     * A reference to the instance that is transmitted from receiver to requester as part of the operation's synchronous 
                     * response (if any).
                     * 
                     * @param response
                     *     Instance transmitted on invocation response
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder response(ExampleScenario.Instance.ContainedInstance response) {
                        this.response = response;
                        return this;
                    }

                    /**
                     * Build the {@link Operation}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>title</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Operation}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Operation per the base specification
                     */
                    @Override
                    public Operation build() {
                        Operation operation = new Operation(this);
                        if (validating) {
                            validate(operation);
                        }
                        return operation;
                    }

                    protected void validate(Operation operation) {
                        super.validate(operation);
                        ValidationSupport.requireNonNull(operation.title, "title");
                        ValidationSupport.requireValueOrChildren(operation);
                    }

                    protected Builder from(Operation operation) {
                        super.from(operation);
                        type = operation.type;
                        title = operation.title;
                        initiator = operation.initiator;
                        receiver = operation.receiver;
                        description = operation.description;
                        initiatorActive = operation.initiatorActive;
                        receiverActive = operation.receiverActive;
                        request = operation.request;
                        response = operation.response;
                        return this;
                    }
                }
            }

            /**
             * Indicates an alternative step that can be taken instead of the sub-process, scenario or operation. E.g. to represent 
             * non-happy-path/exceptional/atypical circumstances.
             */
            public static class Alternative extends BackboneElement {
                @Required
                private final String title;
                private final Markdown description;
                private final List<ExampleScenario.Process.Step> step;

                private Alternative(Builder builder) {
                    super(builder);
                    title = builder.title;
                    description = builder.description;
                    step = Collections.unmodifiableList(builder.step);
                }

                /**
                 * The label to display for the alternative that gives a sense of the circumstance in which the alternative should be 
                 * invoked.
                 * 
                 * @return
                 *     An immutable object of type {@link String} that is non-null.
                 */
                public String getTitle() {
                    return title;
                }

                /**
                 * A human-readable description of the alternative explaining when the alternative should occur rather than the base step.
                 * 
                 * @return
                 *     An immutable object of type {@link Markdown} that may be null.
                 */
                public Markdown getDescription() {
                    return description;
                }

                /**
                 * Indicates the operation, sub-process or scenario that happens if the alternative option is selected.
                 * 
                 * @return
                 *     An unmodifiable list containing immutable objects of type {@link Step} that may be empty.
                 */
                public List<ExampleScenario.Process.Step> getStep() {
                    return step;
                }

                @Override
                public boolean hasChildren() {
                    return super.hasChildren() || 
                        (title != null) || 
                        (description != null) || 
                        !step.isEmpty();
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
                            accept(description, "description", visitor);
                            accept(step, "step", visitor, ExampleScenario.Process.Step.class);
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
                    Alternative other = (Alternative) obj;
                    return Objects.equals(id, other.id) && 
                        Objects.equals(extension, other.extension) && 
                        Objects.equals(modifierExtension, other.modifierExtension) && 
                        Objects.equals(title, other.title) && 
                        Objects.equals(description, other.description) && 
                        Objects.equals(step, other.step);
                }

                @Override
                public int hashCode() {
                    int result = hashCode;
                    if (result == 0) {
                        result = Objects.hash(id, 
                            extension, 
                            modifierExtension, 
                            title, 
                            description, 
                            step);
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
                    private Markdown description;
                    private List<ExampleScenario.Process.Step> step = new ArrayList<>();

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
                     * <p>This element is required.
                     * 
                     * @param title
                     *     Label for alternative
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
                     * The label to display for the alternative that gives a sense of the circumstance in which the alternative should be 
                     * invoked.
                     * 
                     * <p>This element is required.
                     * 
                     * @param title
                     *     Label for alternative
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder title(String title) {
                        this.title = title;
                        return this;
                    }

                    /**
                     * A human-readable description of the alternative explaining when the alternative should occur rather than the base step.
                     * 
                     * @param description
                     *     Human-readable description of option
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder description(Markdown description) {
                        this.description = description;
                        return this;
                    }

                    /**
                     * Indicates the operation, sub-process or scenario that happens if the alternative option is selected.
                     * 
                     * <p>Adds new element(s) to the existing list.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param step
                     *     Alternative action(s)
                     * 
                     * @return
                     *     A reference to this Builder instance
                     */
                    public Builder step(ExampleScenario.Process.Step... step) {
                        for (ExampleScenario.Process.Step value : step) {
                            this.step.add(value);
                        }
                        return this;
                    }

                    /**
                     * Indicates the operation, sub-process or scenario that happens if the alternative option is selected.
                     * 
                     * <p>Replaces the existing list with a new one containing elements from the Collection.
                     * If any of the elements are null, calling {@link #build()} will fail.
                     * 
                     * @param step
                     *     Alternative action(s)
                     * 
                     * @return
                     *     A reference to this Builder instance
                     * 
                     * @throws NullPointerException
                     *     If the passed collection is null
                     */
                    public Builder step(Collection<ExampleScenario.Process.Step> step) {
                        this.step = new ArrayList<>(step);
                        return this;
                    }

                    /**
                     * Build the {@link Alternative}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>title</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Alternative}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Alternative per the base specification
                     */
                    @Override
                    public Alternative build() {
                        Alternative alternative = new Alternative(this);
                        if (validating) {
                            validate(alternative);
                        }
                        return alternative;
                    }

                    protected void validate(Alternative alternative) {
                        super.validate(alternative);
                        ValidationSupport.requireNonNull(alternative.title, "title");
                        ValidationSupport.checkList(alternative.step, "step", ExampleScenario.Process.Step.class);
                        ValidationSupport.requireValueOrChildren(alternative);
                    }

                    protected Builder from(Alternative alternative) {
                        super.from(alternative);
                        title = alternative.title;
                        description = alternative.description;
                        step.addAll(alternative.step);
                        return this;
                    }
                }
            }
        }
    }
}
