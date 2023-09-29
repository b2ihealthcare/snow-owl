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
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Expression;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.Url;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ConsentDataMeaning;
import org.linuxforhealth.fhir.model.r5.type.code.ConsentProvisionType;
import org.linuxforhealth.fhir.model.r5.type.code.ConsentState;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * A record of a healthcare consumer’s choices or choices made on their behalf by a third party, which permits or 
 * denies identified recipient(s) or recipient role(s) to perform one or more actions within a given policy context, for 
 * specific purposes and periods of time.
 * 
 * <p>Maturity level: FMM2 (Trial Use)
 */
@Maturity(
    level = 2,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "consent-0",
    level = "Warning",
    location = "provision.actor.role",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/participation-role-type",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/participation-role-type', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Consent",
    generated = true
)
@Constraint(
    id = "consent-1",
    level = "Warning",
    location = "provision.purpose",
    description = "SHALL, if possible, contain a code from value set http://terminology.hl7.org/ValueSet/v3-PurposeOfUse",
    expression = "$this.memberOf('http://terminology.hl7.org/ValueSet/v3-PurposeOfUse', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Consent",
    generated = true
)
@Constraint(
    id = "consent-2",
    level = "Warning",
    location = "provision.documentType",
    description = "SHOULD contain a code from value set http://hl7.org/fhir/ValueSet/consent-content-class",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/consent-content-class', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Consent",
    generated = true
)
@Constraint(
    id = "consent-3",
    level = "Warning",
    location = "provision.resourceType",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/resource-types",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/resource-types', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/Consent",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Consent extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "ConsentState",
        strength = BindingStrength.Value.REQUIRED,
        description = "Indicates the state of the consent.",
        valueSet = "http://hl7.org/fhir/ValueSet/consent-state-codes|5.0.0"
    )
    @Required
    private final ConsentState status;
    @Summary
    @Binding(
        bindingName = "ConsentCategory",
        strength = BindingStrength.Value.EXAMPLE,
        description = "A classification of the type of consents found in a consent statement.",
        valueSet = "http://hl7.org/fhir/ValueSet/consent-category"
    )
    private final List<CodeableConcept> category;
    @Summary
    @ReferenceTarget({ "Patient", "Practitioner", "Group" })
    private final Reference subject;
    @Summary
    private final Date date;
    @Summary
    private final Period period;
    @Summary
    @ReferenceTarget({ "CareTeam", "HealthcareService", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole" })
    private final List<Reference> grantor;
    @Summary
    @ReferenceTarget({ "CareTeam", "HealthcareService", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole" })
    private final List<Reference> grantee;
    @ReferenceTarget({ "HealthcareService", "Organization", "Patient", "Practitioner" })
    private final List<Reference> manager;
    @ReferenceTarget({ "HealthcareService", "Organization", "Patient", "Practitioner" })
    private final List<Reference> controller;
    private final List<Attachment> sourceAttachment;
    @ReferenceTarget({ "Consent", "DocumentReference", "Contract", "QuestionnaireResponse" })
    private final List<Reference> sourceReference;
    @Binding(
        bindingName = "ConsentRegulatoryBasis",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Regulatory policy examples",
        valueSet = "http://hl7.org/fhir/ValueSet/consent-policy"
    )
    private final List<CodeableConcept> regulatoryBasis;
    private final PolicyBasis policyBasis;
    @ReferenceTarget({ "DocumentReference" })
    private final List<Reference> policyText;
    @Summary
    private final List<Verification> verification;
    @Summary
    @Binding(
        bindingName = "ConsentProvisionType",
        strength = BindingStrength.Value.REQUIRED,
        description = "Sets the base decision for Consent to be either permit or deny, with provisions assumed to be a negation of the previous level.",
        valueSet = "http://hl7.org/fhir/ValueSet/consent-provision-type|5.0.0"
    )
    private final ConsentProvisionType decision;
    @Summary
    private final List<Provision> provision;

    private Consent(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        category = Collections.unmodifiableList(builder.category);
        subject = builder.subject;
        date = builder.date;
        period = builder.period;
        grantor = Collections.unmodifiableList(builder.grantor);
        grantee = Collections.unmodifiableList(builder.grantee);
        manager = Collections.unmodifiableList(builder.manager);
        controller = Collections.unmodifiableList(builder.controller);
        sourceAttachment = Collections.unmodifiableList(builder.sourceAttachment);
        sourceReference = Collections.unmodifiableList(builder.sourceReference);
        regulatoryBasis = Collections.unmodifiableList(builder.regulatoryBasis);
        policyBasis = builder.policyBasis;
        policyText = Collections.unmodifiableList(builder.policyText);
        verification = Collections.unmodifiableList(builder.verification);
        decision = builder.decision;
        provision = Collections.unmodifiableList(builder.provision);
    }

    /**
     * Unique identifier for this copy of the Consent Statement.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Indicates the current state of this Consent resource.
     * 
     * @return
     *     An immutable object of type {@link ConsentState} that is non-null.
     */
    public ConsentState getStatus() {
        return status;
    }

    /**
     * A classification of the type of consents found in the statement. This element supports indexing and retrieval of 
     * consent statements.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getCategory() {
        return category;
    }

    /**
     * The patient/healthcare practitioner or group of persons to whom this consent applies.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * Date the consent instance was agreed to.
     * 
     * @return
     *     An immutable object of type {@link Date} that may be null.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Effective period for this Consent Resource and all provisions unless specified in that provision.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * The entity responsible for granting the rights listed in a Consent Directive.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getGrantor() {
        return grantor;
    }

    /**
     * The entity responsible for complying with the Consent Directive, including any obligations or limitations on 
     * authorizations and enforcement of prohibitions.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getGrantee() {
        return grantee;
    }

    /**
     * The actor that manages the consent through its lifecycle.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getManager() {
        return manager;
    }

    /**
     * The actor that controls/enforces the access according to the consent.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getController() {
        return controller;
    }

    /**
     * The source on which this consent statement is based. The source might be a scanned original paper form.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Attachment} that may be empty.
     */
    public List<Attachment> getSourceAttachment() {
        return sourceAttachment;
    }

    /**
     * A reference to a consent that links back to such a source, a reference to a document repository (e.g. XDS) that stores 
     * the original consent document.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSourceReference() {
        return sourceReference;
    }

    /**
     * A set of codes that indicate the regulatory basis (if any) that this consent supports.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getRegulatoryBasis() {
        return regulatoryBasis;
    }

    /**
     * A Reference or URL used to uniquely identify the policy the organization will enforce for this Consent. This Reference 
     * or URL should be specific to the version of the policy and should be dereferencable to a computable policy of some 
     * form.
     * 
     * @return
     *     An immutable object of type {@link PolicyBasis} that may be null.
     */
    public PolicyBasis getPolicyBasis() {
        return policyBasis;
    }

    /**
     * A Reference to the human readable policy explaining the basis for the Consent.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPolicyText() {
        return policyText;
    }

    /**
     * Whether a treatment instruction (e.g. artificial respiration: yes or no) was verified with the patient, his/her family 
     * or another authorized person.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Verification} that may be empty.
     */
    public List<Verification> getVerification() {
        return verification;
    }

    /**
     * Action to take - permit or deny - as default.
     * 
     * @return
     *     An immutable object of type {@link ConsentProvisionType} that may be null.
     */
    public ConsentProvisionType getDecision() {
        return decision;
    }

    /**
     * An exception to the base policy of this consent. An exception can be an addition or removal of access permissions.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Provision} that may be empty.
     */
    public List<Provision> getProvision() {
        return provision;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !category.isEmpty() || 
            (subject != null) || 
            (date != null) || 
            (period != null) || 
            !grantor.isEmpty() || 
            !grantee.isEmpty() || 
            !manager.isEmpty() || 
            !controller.isEmpty() || 
            !sourceAttachment.isEmpty() || 
            !sourceReference.isEmpty() || 
            !regulatoryBasis.isEmpty() || 
            (policyBasis != null) || 
            !policyText.isEmpty() || 
            !verification.isEmpty() || 
            (decision != null) || 
            !provision.isEmpty();
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
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(status, "status", visitor);
                accept(category, "category", visitor, CodeableConcept.class);
                accept(subject, "subject", visitor);
                accept(date, "date", visitor);
                accept(period, "period", visitor);
                accept(grantor, "grantor", visitor, Reference.class);
                accept(grantee, "grantee", visitor, Reference.class);
                accept(manager, "manager", visitor, Reference.class);
                accept(controller, "controller", visitor, Reference.class);
                accept(sourceAttachment, "sourceAttachment", visitor, Attachment.class);
                accept(sourceReference, "sourceReference", visitor, Reference.class);
                accept(regulatoryBasis, "regulatoryBasis", visitor, CodeableConcept.class);
                accept(policyBasis, "policyBasis", visitor);
                accept(policyText, "policyText", visitor, Reference.class);
                accept(verification, "verification", visitor, Verification.class);
                accept(decision, "decision", visitor);
                accept(provision, "provision", visitor, Provision.class);
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
        Consent other = (Consent) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(category, other.category) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(date, other.date) && 
            Objects.equals(period, other.period) && 
            Objects.equals(grantor, other.grantor) && 
            Objects.equals(grantee, other.grantee) && 
            Objects.equals(manager, other.manager) && 
            Objects.equals(controller, other.controller) && 
            Objects.equals(sourceAttachment, other.sourceAttachment) && 
            Objects.equals(sourceReference, other.sourceReference) && 
            Objects.equals(regulatoryBasis, other.regulatoryBasis) && 
            Objects.equals(policyBasis, other.policyBasis) && 
            Objects.equals(policyText, other.policyText) && 
            Objects.equals(verification, other.verification) && 
            Objects.equals(decision, other.decision) && 
            Objects.equals(provision, other.provision);
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
                identifier, 
                status, 
                category, 
                subject, 
                date, 
                period, 
                grantor, 
                grantee, 
                manager, 
                controller, 
                sourceAttachment, 
                sourceReference, 
                regulatoryBasis, 
                policyBasis, 
                policyText, 
                verification, 
                decision, 
                provision);
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
        private List<Identifier> identifier = new ArrayList<>();
        private ConsentState status;
        private List<CodeableConcept> category = new ArrayList<>();
        private Reference subject;
        private Date date;
        private Period period;
        private List<Reference> grantor = new ArrayList<>();
        private List<Reference> grantee = new ArrayList<>();
        private List<Reference> manager = new ArrayList<>();
        private List<Reference> controller = new ArrayList<>();
        private List<Attachment> sourceAttachment = new ArrayList<>();
        private List<Reference> sourceReference = new ArrayList<>();
        private List<CodeableConcept> regulatoryBasis = new ArrayList<>();
        private PolicyBasis policyBasis;
        private List<Reference> policyText = new ArrayList<>();
        private List<Verification> verification = new ArrayList<>();
        private ConsentProvisionType decision;
        private List<Provision> provision = new ArrayList<>();

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
         * Unique identifier for this copy of the Consent Statement.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier for this record (external references)
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
         * Unique identifier for this copy of the Consent Statement.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifier for this record (external references)
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
         * Indicates the current state of this Consent resource.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     draft | active | inactive | not-done | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(ConsentState status) {
            this.status = status;
            return this;
        }

        /**
         * A classification of the type of consents found in the statement. This element supports indexing and retrieval of 
         * consent statements.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classification of the consent statement - for indexing/retrieval
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
         * A classification of the type of consents found in the statement. This element supports indexing and retrieval of 
         * consent statements.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param category
         *     Classification of the consent statement - for indexing/retrieval
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
         * The patient/healthcare practitioner or group of persons to whom this consent applies.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     Who the consent applies to
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Convenience method for setting {@code date}.
         * 
         * @param date
         *     Fully executed date of the consent
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #date(org.linuxforhealth.fhir.model.type.Date)
         */
        public Builder date(java.time.LocalDate date) {
            this.date = (date == null) ? null : Date.of(date);
            return this;
        }

        /**
         * Date the consent instance was agreed to.
         * 
         * @param date
         *     Fully executed date of the consent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        /**
         * Effective period for this Consent Resource and all provisions unless specified in that provision.
         * 
         * @param period
         *     Effective period for this Consent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        /**
         * The entity responsible for granting the rights listed in a Consent Directive.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param grantor
         *     Who is granting rights according to the policy and rules
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder grantor(Reference... grantor) {
            for (Reference value : grantor) {
                this.grantor.add(value);
            }
            return this;
        }

        /**
         * The entity responsible for granting the rights listed in a Consent Directive.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param grantor
         *     Who is granting rights according to the policy and rules
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder grantor(Collection<Reference> grantor) {
            this.grantor = new ArrayList<>(grantor);
            return this;
        }

        /**
         * The entity responsible for complying with the Consent Directive, including any obligations or limitations on 
         * authorizations and enforcement of prohibitions.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param grantee
         *     Who is agreeing to the policy and rules
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder grantee(Reference... grantee) {
            for (Reference value : grantee) {
                this.grantee.add(value);
            }
            return this;
        }

        /**
         * The entity responsible for complying with the Consent Directive, including any obligations or limitations on 
         * authorizations and enforcement of prohibitions.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CareTeam}</li>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param grantee
         *     Who is agreeing to the policy and rules
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder grantee(Collection<Reference> grantee) {
            this.grantee = new ArrayList<>(grantee);
            return this;
        }

        /**
         * The actor that manages the consent through its lifecycle.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * </ul>
         * 
         * @param manager
         *     Consent workflow management
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder manager(Reference... manager) {
            for (Reference value : manager) {
                this.manager.add(value);
            }
            return this;
        }

        /**
         * The actor that manages the consent through its lifecycle.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * </ul>
         * 
         * @param manager
         *     Consent workflow management
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder manager(Collection<Reference> manager) {
            this.manager = new ArrayList<>(manager);
            return this;
        }

        /**
         * The actor that controls/enforces the access according to the consent.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * </ul>
         * 
         * @param controller
         *     Consent Enforcer
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder controller(Reference... controller) {
            for (Reference value : controller) {
                this.controller.add(value);
            }
            return this;
        }

        /**
         * The actor that controls/enforces the access according to the consent.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link HealthcareService}</li>
         * <li>{@link Organization}</li>
         * <li>{@link Patient}</li>
         * <li>{@link Practitioner}</li>
         * </ul>
         * 
         * @param controller
         *     Consent Enforcer
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder controller(Collection<Reference> controller) {
            this.controller = new ArrayList<>(controller);
            return this;
        }

        /**
         * The source on which this consent statement is based. The source might be a scanned original paper form.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param sourceAttachment
         *     Source from which this consent is taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder sourceAttachment(Attachment... sourceAttachment) {
            for (Attachment value : sourceAttachment) {
                this.sourceAttachment.add(value);
            }
            return this;
        }

        /**
         * The source on which this consent statement is based. The source might be a scanned original paper form.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param sourceAttachment
         *     Source from which this consent is taken
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder sourceAttachment(Collection<Attachment> sourceAttachment) {
            this.sourceAttachment = new ArrayList<>(sourceAttachment);
            return this;
        }

        /**
         * A reference to a consent that links back to such a source, a reference to a document repository (e.g. XDS) that stores 
         * the original consent document.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Consent}</li>
         * <li>{@link DocumentReference}</li>
         * <li>{@link Contract}</li>
         * <li>{@link QuestionnaireResponse}</li>
         * </ul>
         * 
         * @param sourceReference
         *     Source from which this consent is taken
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder sourceReference(Reference... sourceReference) {
            for (Reference value : sourceReference) {
                this.sourceReference.add(value);
            }
            return this;
        }

        /**
         * A reference to a consent that links back to such a source, a reference to a document repository (e.g. XDS) that stores 
         * the original consent document.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Consent}</li>
         * <li>{@link DocumentReference}</li>
         * <li>{@link Contract}</li>
         * <li>{@link QuestionnaireResponse}</li>
         * </ul>
         * 
         * @param sourceReference
         *     Source from which this consent is taken
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder sourceReference(Collection<Reference> sourceReference) {
            this.sourceReference = new ArrayList<>(sourceReference);
            return this;
        }

        /**
         * A set of codes that indicate the regulatory basis (if any) that this consent supports.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param regulatoryBasis
         *     Regulations establishing base Consent
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder regulatoryBasis(CodeableConcept... regulatoryBasis) {
            for (CodeableConcept value : regulatoryBasis) {
                this.regulatoryBasis.add(value);
            }
            return this;
        }

        /**
         * A set of codes that indicate the regulatory basis (if any) that this consent supports.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param regulatoryBasis
         *     Regulations establishing base Consent
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder regulatoryBasis(Collection<CodeableConcept> regulatoryBasis) {
            this.regulatoryBasis = new ArrayList<>(regulatoryBasis);
            return this;
        }

        /**
         * A Reference or URL used to uniquely identify the policy the organization will enforce for this Consent. This Reference 
         * or URL should be specific to the version of the policy and should be dereferencable to a computable policy of some 
         * form.
         * 
         * @param policyBasis
         *     Computable version of the backing policy
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder policyBasis(PolicyBasis policyBasis) {
            this.policyBasis = policyBasis;
            return this;
        }

        /**
         * A Reference to the human readable policy explaining the basis for the Consent.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DocumentReference}</li>
         * </ul>
         * 
         * @param policyText
         *     Human Readable Policy
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder policyText(Reference... policyText) {
            for (Reference value : policyText) {
                this.policyText.add(value);
            }
            return this;
        }

        /**
         * A Reference to the human readable policy explaining the basis for the Consent.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DocumentReference}</li>
         * </ul>
         * 
         * @param policyText
         *     Human Readable Policy
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder policyText(Collection<Reference> policyText) {
            this.policyText = new ArrayList<>(policyText);
            return this;
        }

        /**
         * Whether a treatment instruction (e.g. artificial respiration: yes or no) was verified with the patient, his/her family 
         * or another authorized person.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param verification
         *     Consent Verified by patient or family
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder verification(Verification... verification) {
            for (Verification value : verification) {
                this.verification.add(value);
            }
            return this;
        }

        /**
         * Whether a treatment instruction (e.g. artificial respiration: yes or no) was verified with the patient, his/her family 
         * or another authorized person.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param verification
         *     Consent Verified by patient or family
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder verification(Collection<Verification> verification) {
            this.verification = new ArrayList<>(verification);
            return this;
        }

        /**
         * Action to take - permit or deny - as default.
         * 
         * @param decision
         *     deny | permit
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder decision(ConsentProvisionType decision) {
            this.decision = decision;
            return this;
        }

        /**
         * An exception to the base policy of this consent. An exception can be an addition or removal of access permissions.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param provision
         *     Constraints to the base Consent.policyRule/Consent.policy
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder provision(Provision... provision) {
            for (Provision value : provision) {
                this.provision.add(value);
            }
            return this;
        }

        /**
         * An exception to the base policy of this consent. An exception can be an addition or removal of access permissions.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param provision
         *     Constraints to the base Consent.policyRule/Consent.policy
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder provision(Collection<Provision> provision) {
            this.provision = new ArrayList<>(provision);
            return this;
        }

        /**
         * Build the {@link Consent}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Consent}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Consent per the base specification
         */
        @Override
        public Consent build() {
            Consent consent = new Consent(this);
            if (validating) {
                validate(consent);
            }
            return consent;
        }

        protected void validate(Consent consent) {
            super.validate(consent);
            ValidationSupport.checkList(consent.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(consent.status, "status");
            ValidationSupport.checkList(consent.category, "category", CodeableConcept.class);
            ValidationSupport.checkList(consent.grantor, "grantor", Reference.class);
            ValidationSupport.checkList(consent.grantee, "grantee", Reference.class);
            ValidationSupport.checkList(consent.manager, "manager", Reference.class);
            ValidationSupport.checkList(consent.controller, "controller", Reference.class);
            ValidationSupport.checkList(consent.sourceAttachment, "sourceAttachment", Attachment.class);
            ValidationSupport.checkList(consent.sourceReference, "sourceReference", Reference.class);
            ValidationSupport.checkList(consent.regulatoryBasis, "regulatoryBasis", CodeableConcept.class);
            ValidationSupport.checkList(consent.policyText, "policyText", Reference.class);
            ValidationSupport.checkList(consent.verification, "verification", Verification.class);
            ValidationSupport.checkList(consent.provision, "provision", Provision.class);
            ValidationSupport.checkReferenceType(consent.subject, "subject", "Patient", "Practitioner", "Group");
            ValidationSupport.checkReferenceType(consent.grantor, "grantor", "CareTeam", "HealthcareService", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole");
            ValidationSupport.checkReferenceType(consent.grantee, "grantee", "CareTeam", "HealthcareService", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole");
            ValidationSupport.checkReferenceType(consent.manager, "manager", "HealthcareService", "Organization", "Patient", "Practitioner");
            ValidationSupport.checkReferenceType(consent.controller, "controller", "HealthcareService", "Organization", "Patient", "Practitioner");
            ValidationSupport.checkReferenceType(consent.sourceReference, "sourceReference", "Consent", "DocumentReference", "Contract", "QuestionnaireResponse");
            ValidationSupport.checkReferenceType(consent.policyText, "policyText", "DocumentReference");
        }

        protected Builder from(Consent consent) {
            super.from(consent);
            identifier.addAll(consent.identifier);
            status = consent.status;
            category.addAll(consent.category);
            subject = consent.subject;
            date = consent.date;
            period = consent.period;
            grantor.addAll(consent.grantor);
            grantee.addAll(consent.grantee);
            manager.addAll(consent.manager);
            controller.addAll(consent.controller);
            sourceAttachment.addAll(consent.sourceAttachment);
            sourceReference.addAll(consent.sourceReference);
            regulatoryBasis.addAll(consent.regulatoryBasis);
            policyBasis = consent.policyBasis;
            policyText.addAll(consent.policyText);
            verification.addAll(consent.verification);
            decision = consent.decision;
            provision.addAll(consent.provision);
            return this;
        }
    }

    /**
     * A Reference or URL used to uniquely identify the policy the organization will enforce for this Consent. This Reference 
     * or URL should be specific to the version of the policy and should be dereferencable to a computable policy of some 
     * form.
     */
    public static class PolicyBasis extends BackboneElement {
        private final Reference reference;
        private final Url url;

        private PolicyBasis(Builder builder) {
            super(builder);
            reference = builder.reference;
            url = builder.url;
        }

        /**
         * A Reference that identifies the policy the organization will enforce for this Consent.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getReference() {
            return reference;
        }

        /**
         * A URL that links to a computable version of the policy the organization will enforce for this Consent.
         * 
         * @return
         *     An immutable object of type {@link Url} that may be null.
         */
        public Url getUrl() {
            return url;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (reference != null) || 
                (url != null);
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
                    accept(reference, "reference", visitor);
                    accept(url, "url", visitor);
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
            PolicyBasis other = (PolicyBasis) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(reference, other.reference) && 
                Objects.equals(url, other.url);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    reference, 
                    url);
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
            private Reference reference;
            private Url url;

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
             * A Reference that identifies the policy the organization will enforce for this Consent.
             * 
             * @param reference
             *     Reference backing policy resource
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder reference(Reference reference) {
                this.reference = reference;
                return this;
            }

            /**
             * A URL that links to a computable version of the policy the organization will enforce for this Consent.
             * 
             * @param url
             *     URL to a computable backing policy
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder url(Url url) {
                this.url = url;
                return this;
            }

            /**
             * Build the {@link PolicyBasis}
             * 
             * @return
             *     An immutable object of type {@link PolicyBasis}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid PolicyBasis per the base specification
             */
            @Override
            public PolicyBasis build() {
                PolicyBasis policyBasis = new PolicyBasis(this);
                if (validating) {
                    validate(policyBasis);
                }
                return policyBasis;
            }

            protected void validate(PolicyBasis policyBasis) {
                super.validate(policyBasis);
                ValidationSupport.requireValueOrChildren(policyBasis);
            }

            protected Builder from(PolicyBasis policyBasis) {
                super.from(policyBasis);
                reference = policyBasis.reference;
                url = policyBasis.url;
                return this;
            }
        }
    }

    /**
     * Whether a treatment instruction (e.g. artificial respiration: yes or no) was verified with the patient, his/her family 
     * or another authorized person.
     */
    public static class Verification extends BackboneElement {
        @Summary
        @Required
        private final Boolean verified;
        @Binding(
            bindingName = "ConsentVerificationType",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Types of Verification/Validation.",
            valueSet = "http://hl7.org/fhir/ValueSet/consent-verification"
        )
        private final CodeableConcept verificationType;
        @ReferenceTarget({ "Organization", "Practitioner", "PractitionerRole" })
        private final Reference verifiedBy;
        @ReferenceTarget({ "Patient", "RelatedPerson" })
        private final Reference verifiedWith;
        private final List<DateTime> verificationDate;

        private Verification(Builder builder) {
            super(builder);
            verified = builder.verified;
            verificationType = builder.verificationType;
            verifiedBy = builder.verifiedBy;
            verifiedWith = builder.verifiedWith;
            verificationDate = Collections.unmodifiableList(builder.verificationDate);
        }

        /**
         * Has the instruction been verified.
         * 
         * @return
         *     An immutable object of type {@link Boolean} that is non-null.
         */
        public Boolean getVerified() {
            return verified;
        }

        /**
         * Extensible list of verification type starting with verification and re-validation.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getVerificationType() {
            return verificationType;
        }

        /**
         * The person who conducted the verification/validation of the Grantor decision.
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getVerifiedBy() {
            return verifiedBy;
        }

        /**
         * Who verified the instruction (Patient, Relative or other Authorized Person).
         * 
         * @return
         *     An immutable object of type {@link Reference} that may be null.
         */
        public Reference getVerifiedWith() {
            return verifiedWith;
        }

        /**
         * Date(s) verification was collected.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link DateTime} that may be empty.
         */
        public List<DateTime> getVerificationDate() {
            return verificationDate;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (verified != null) || 
                (verificationType != null) || 
                (verifiedBy != null) || 
                (verifiedWith != null) || 
                !verificationDate.isEmpty();
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
                    accept(verified, "verified", visitor);
                    accept(verificationType, "verificationType", visitor);
                    accept(verifiedBy, "verifiedBy", visitor);
                    accept(verifiedWith, "verifiedWith", visitor);
                    accept(verificationDate, "verificationDate", visitor, DateTime.class);
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
            Verification other = (Verification) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(verified, other.verified) && 
                Objects.equals(verificationType, other.verificationType) && 
                Objects.equals(verifiedBy, other.verifiedBy) && 
                Objects.equals(verifiedWith, other.verifiedWith) && 
                Objects.equals(verificationDate, other.verificationDate);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    verified, 
                    verificationType, 
                    verifiedBy, 
                    verifiedWith, 
                    verificationDate);
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
            private Boolean verified;
            private CodeableConcept verificationType;
            private Reference verifiedBy;
            private Reference verifiedWith;
            private List<DateTime> verificationDate = new ArrayList<>();

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
             * Convenience method for setting {@code verified}.
             * 
             * <p>This element is required.
             * 
             * @param verified
             *     Has been verified
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #verified(org.linuxforhealth.fhir.model.type.Boolean)
             */
            public Builder verified(java.lang.Boolean verified) {
                this.verified = (verified == null) ? null : Boolean.of(verified);
                return this;
            }

            /**
             * Has the instruction been verified.
             * 
             * <p>This element is required.
             * 
             * @param verified
             *     Has been verified
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder verified(Boolean verified) {
                this.verified = verified;
                return this;
            }

            /**
             * Extensible list of verification type starting with verification and re-validation.
             * 
             * @param verificationType
             *     Business case of verification
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder verificationType(CodeableConcept verificationType) {
                this.verificationType = verificationType;
                return this;
            }

            /**
             * The person who conducted the verification/validation of the Grantor decision.
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Organization}</li>
             * <li>{@link Practitioner}</li>
             * <li>{@link PractitionerRole}</li>
             * </ul>
             * 
             * @param verifiedBy
             *     Person conducting verification
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder verifiedBy(Reference verifiedBy) {
                this.verifiedBy = verifiedBy;
                return this;
            }

            /**
             * Who verified the instruction (Patient, Relative or other Authorized Person).
             * 
             * <p>Allowed resource types for this reference:
             * <ul>
             * <li>{@link Patient}</li>
             * <li>{@link RelatedPerson}</li>
             * </ul>
             * 
             * @param verifiedWith
             *     Person who verified
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder verifiedWith(Reference verifiedWith) {
                this.verifiedWith = verifiedWith;
                return this;
            }

            /**
             * Date(s) verification was collected.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param verificationDate
             *     When consent verified
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder verificationDate(DateTime... verificationDate) {
                for (DateTime value : verificationDate) {
                    this.verificationDate.add(value);
                }
                return this;
            }

            /**
             * Date(s) verification was collected.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param verificationDate
             *     When consent verified
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder verificationDate(Collection<DateTime> verificationDate) {
                this.verificationDate = new ArrayList<>(verificationDate);
                return this;
            }

            /**
             * Build the {@link Verification}
             * 
             * <p>Required elements:
             * <ul>
             * <li>verified</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Verification}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Verification per the base specification
             */
            @Override
            public Verification build() {
                Verification verification = new Verification(this);
                if (validating) {
                    validate(verification);
                }
                return verification;
            }

            protected void validate(Verification verification) {
                super.validate(verification);
                ValidationSupport.requireNonNull(verification.verified, "verified");
                ValidationSupport.checkList(verification.verificationDate, "verificationDate", DateTime.class);
                ValidationSupport.checkReferenceType(verification.verifiedBy, "verifiedBy", "Organization", "Practitioner", "PractitionerRole");
                ValidationSupport.checkReferenceType(verification.verifiedWith, "verifiedWith", "Patient", "RelatedPerson");
                ValidationSupport.requireValueOrChildren(verification);
            }

            protected Builder from(Verification verification) {
                super.from(verification);
                verified = verification.verified;
                verificationType = verification.verificationType;
                verifiedBy = verification.verifiedBy;
                verifiedWith = verification.verifiedWith;
                verificationDate.addAll(verification.verificationDate);
                return this;
            }
        }
    }

    /**
     * An exception to the base policy of this consent. An exception can be an addition or removal of access permissions.
     */
    public static class Provision extends BackboneElement {
        @Summary
        private final Period period;
        private final List<Actor> actor;
        @Summary
        @Binding(
            bindingName = "ConsentAction",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Detailed codes for the consent action.",
            valueSet = "http://hl7.org/fhir/ValueSet/consent-action"
        )
        private final List<CodeableConcept> action;
        @Summary
        @Binding(
            bindingName = "SecurityLabels",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Example Security Labels from the Healthcare Privacy and Security Classification System.",
            valueSet = "http://hl7.org/fhir/ValueSet/security-label-examples"
        )
        private final List<Coding> securityLabel;
        @Summary
        @Binding(
            bindingName = "PurposeOfUse",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "What purposes of use are controlled by this exception. If more than one label is specified, operations must have all the specified labels.",
            valueSet = "http://terminology.hl7.org/ValueSet/v3-PurposeOfUse"
        )
        private final List<Coding> purpose;
        @Summary
        @Binding(
            bindingName = "ConsentContentClass",
            strength = BindingStrength.Value.PREFERRED,
            description = "The document type a consent provision covers.",
            valueSet = "http://hl7.org/fhir/ValueSet/consent-content-class"
        )
        private final List<Coding> documentType;
        @Summary
        @Binding(
            bindingName = "ConsentContentClass",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "The resource types a consent provision covers.",
            valueSet = "http://hl7.org/fhir/ValueSet/resource-types"
        )
        private final List<Coding> resourceType;
        @Summary
        @Binding(
            bindingName = "ConsentContentCode",
            strength = BindingStrength.Value.EXAMPLE,
            description = "If this code is found in an instance, then the exception applies.",
            valueSet = "http://hl7.org/fhir/ValueSet/consent-content-code"
        )
        private final List<CodeableConcept> code;
        @Summary
        private final Period dataPeriod;
        @Summary
        private final List<Data> data;
        private final Expression expression;
        private final List<Consent.Provision> provision;

        private Provision(Builder builder) {
            super(builder);
            period = builder.period;
            actor = Collections.unmodifiableList(builder.actor);
            action = Collections.unmodifiableList(builder.action);
            securityLabel = Collections.unmodifiableList(builder.securityLabel);
            purpose = Collections.unmodifiableList(builder.purpose);
            documentType = Collections.unmodifiableList(builder.documentType);
            resourceType = Collections.unmodifiableList(builder.resourceType);
            code = Collections.unmodifiableList(builder.code);
            dataPeriod = builder.dataPeriod;
            data = Collections.unmodifiableList(builder.data);
            expression = builder.expression;
            provision = Collections.unmodifiableList(builder.provision);
        }

        /**
         * Timeframe for this provision.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getPeriod() {
            return period;
        }

        /**
         * Who or what is controlled by this provision. Use group to identify a set of actors by some property they share (e.g. 
         * 'admitting officers').
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Actor} that may be empty.
         */
        public List<Actor> getActor() {
            return actor;
        }

        /**
         * Actions controlled by this provision.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getAction() {
            return action;
        }

        /**
         * A security label, comprised of 0..* security label fields (Privacy tags), which define which resources are controlled 
         * by this exception.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
         */
        public List<Coding> getSecurityLabel() {
            return securityLabel;
        }

        /**
         * The context of the activities a user is taking - why the user is accessing the data - that are controlled by this 
         * provision.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
         */
        public List<Coding> getPurpose() {
            return purpose;
        }

        /**
         * The documentType(s) covered by this provision. The type can be a CDA document, or some other type that indicates what 
         * sort of information the consent relates to.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
         */
        public List<Coding> getDocumentType() {
            return documentType;
        }

        /**
         * The resourceType(s) covered by this provision. The type can be a FHIR resource type or a profile on a type that 
         * indicates what information the consent relates to.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
         */
        public List<Coding> getResourceType() {
            return resourceType;
        }

        /**
         * If this code is found in an instance, then the provision applies.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getCode() {
            return code;
        }

        /**
         * Clinical or Operational Relevant period of time that bounds the data controlled by this provision.
         * 
         * @return
         *     An immutable object of type {@link Period} that may be null.
         */
        public Period getDataPeriod() {
            return dataPeriod;
        }

        /**
         * The resources controlled by this provision if specific resources are referenced.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Data} that may be empty.
         */
        public List<Data> getData() {
            return data;
        }

        /**
         * A computable (FHIRPath or other) definition of what is controlled by this consent.
         * 
         * @return
         *     An immutable object of type {@link Expression} that may be null.
         */
        public Expression getExpression() {
            return expression;
        }

        /**
         * Provisions which provide exceptions to the base provision or subprovisions.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Provision} that may be empty.
         */
        public List<Consent.Provision> getProvision() {
            return provision;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (period != null) || 
                !actor.isEmpty() || 
                !action.isEmpty() || 
                !securityLabel.isEmpty() || 
                !purpose.isEmpty() || 
                !documentType.isEmpty() || 
                !resourceType.isEmpty() || 
                !code.isEmpty() || 
                (dataPeriod != null) || 
                !data.isEmpty() || 
                (expression != null) || 
                !provision.isEmpty();
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
                    accept(actor, "actor", visitor, Actor.class);
                    accept(action, "action", visitor, CodeableConcept.class);
                    accept(securityLabel, "securityLabel", visitor, Coding.class);
                    accept(purpose, "purpose", visitor, Coding.class);
                    accept(documentType, "documentType", visitor, Coding.class);
                    accept(resourceType, "resourceType", visitor, Coding.class);
                    accept(code, "code", visitor, CodeableConcept.class);
                    accept(dataPeriod, "dataPeriod", visitor);
                    accept(data, "data", visitor, Data.class);
                    accept(expression, "expression", visitor);
                    accept(provision, "provision", visitor, Consent.Provision.class);
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
            Provision other = (Provision) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(period, other.period) && 
                Objects.equals(actor, other.actor) && 
                Objects.equals(action, other.action) && 
                Objects.equals(securityLabel, other.securityLabel) && 
                Objects.equals(purpose, other.purpose) && 
                Objects.equals(documentType, other.documentType) && 
                Objects.equals(resourceType, other.resourceType) && 
                Objects.equals(code, other.code) && 
                Objects.equals(dataPeriod, other.dataPeriod) && 
                Objects.equals(data, other.data) && 
                Objects.equals(expression, other.expression) && 
                Objects.equals(provision, other.provision);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    period, 
                    actor, 
                    action, 
                    securityLabel, 
                    purpose, 
                    documentType, 
                    resourceType, 
                    code, 
                    dataPeriod, 
                    data, 
                    expression, 
                    provision);
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
            private List<Actor> actor = new ArrayList<>();
            private List<CodeableConcept> action = new ArrayList<>();
            private List<Coding> securityLabel = new ArrayList<>();
            private List<Coding> purpose = new ArrayList<>();
            private List<Coding> documentType = new ArrayList<>();
            private List<Coding> resourceType = new ArrayList<>();
            private List<CodeableConcept> code = new ArrayList<>();
            private Period dataPeriod;
            private List<Data> data = new ArrayList<>();
            private Expression expression;
            private List<Consent.Provision> provision = new ArrayList<>();

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
             * Timeframe for this provision.
             * 
             * @param period
             *     Timeframe for this provision
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder period(Period period) {
                this.period = period;
                return this;
            }

            /**
             * Who or what is controlled by this provision. Use group to identify a set of actors by some property they share (e.g. 
             * 'admitting officers').
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param actor
             *     Who|what controlled by this provision (or group, by role)
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
             * Who or what is controlled by this provision. Use group to identify a set of actors by some property they share (e.g. 
             * 'admitting officers').
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param actor
             *     Who|what controlled by this provision (or group, by role)
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
             * Actions controlled by this provision.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param action
             *     Actions controlled by this provision
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder action(CodeableConcept... action) {
                for (CodeableConcept value : action) {
                    this.action.add(value);
                }
                return this;
            }

            /**
             * Actions controlled by this provision.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param action
             *     Actions controlled by this provision
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder action(Collection<CodeableConcept> action) {
                this.action = new ArrayList<>(action);
                return this;
            }

            /**
             * A security label, comprised of 0..* security label fields (Privacy tags), which define which resources are controlled 
             * by this exception.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param securityLabel
             *     Security Labels that define affected resources
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder securityLabel(Coding... securityLabel) {
                for (Coding value : securityLabel) {
                    this.securityLabel.add(value);
                }
                return this;
            }

            /**
             * A security label, comprised of 0..* security label fields (Privacy tags), which define which resources are controlled 
             * by this exception.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param securityLabel
             *     Security Labels that define affected resources
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder securityLabel(Collection<Coding> securityLabel) {
                this.securityLabel = new ArrayList<>(securityLabel);
                return this;
            }

            /**
             * The context of the activities a user is taking - why the user is accessing the data - that are controlled by this 
             * provision.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param purpose
             *     Context of activities covered by this provision
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder purpose(Coding... purpose) {
                for (Coding value : purpose) {
                    this.purpose.add(value);
                }
                return this;
            }

            /**
             * The context of the activities a user is taking - why the user is accessing the data - that are controlled by this 
             * provision.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param purpose
             *     Context of activities covered by this provision
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder purpose(Collection<Coding> purpose) {
                this.purpose = new ArrayList<>(purpose);
                return this;
            }

            /**
             * The documentType(s) covered by this provision. The type can be a CDA document, or some other type that indicates what 
             * sort of information the consent relates to.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param documentType
             *     e.g. Resource Type, Profile, CDA, etc
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder documentType(Coding... documentType) {
                for (Coding value : documentType) {
                    this.documentType.add(value);
                }
                return this;
            }

            /**
             * The documentType(s) covered by this provision. The type can be a CDA document, or some other type that indicates what 
             * sort of information the consent relates to.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param documentType
             *     e.g. Resource Type, Profile, CDA, etc
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder documentType(Collection<Coding> documentType) {
                this.documentType = new ArrayList<>(documentType);
                return this;
            }

            /**
             * The resourceType(s) covered by this provision. The type can be a FHIR resource type or a profile on a type that 
             * indicates what information the consent relates to.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param resourceType
             *     e.g. Resource Type, Profile, etc
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder resourceType(Coding... resourceType) {
                for (Coding value : resourceType) {
                    this.resourceType.add(value);
                }
                return this;
            }

            /**
             * The resourceType(s) covered by this provision. The type can be a FHIR resource type or a profile on a type that 
             * indicates what information the consent relates to.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param resourceType
             *     e.g. Resource Type, Profile, etc
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder resourceType(Collection<Coding> resourceType) {
                this.resourceType = new ArrayList<>(resourceType);
                return this;
            }

            /**
             * If this code is found in an instance, then the provision applies.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param code
             *     e.g. LOINC or SNOMED CT code, etc. in the content
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept... code) {
                for (CodeableConcept value : code) {
                    this.code.add(value);
                }
                return this;
            }

            /**
             * If this code is found in an instance, then the provision applies.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param code
             *     e.g. LOINC or SNOMED CT code, etc. in the content
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder code(Collection<CodeableConcept> code) {
                this.code = new ArrayList<>(code);
                return this;
            }

            /**
             * Clinical or Operational Relevant period of time that bounds the data controlled by this provision.
             * 
             * @param dataPeriod
             *     Timeframe for data controlled by this provision
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder dataPeriod(Period dataPeriod) {
                this.dataPeriod = dataPeriod;
                return this;
            }

            /**
             * The resources controlled by this provision if specific resources are referenced.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param data
             *     Data controlled by this provision
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder data(Data... data) {
                for (Data value : data) {
                    this.data.add(value);
                }
                return this;
            }

            /**
             * The resources controlled by this provision if specific resources are referenced.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param data
             *     Data controlled by this provision
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder data(Collection<Data> data) {
                this.data = new ArrayList<>(data);
                return this;
            }

            /**
             * A computable (FHIRPath or other) definition of what is controlled by this consent.
             * 
             * @param expression
             *     A computable expression of the consent
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder expression(Expression expression) {
                this.expression = expression;
                return this;
            }

            /**
             * Provisions which provide exceptions to the base provision or subprovisions.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param provision
             *     Nested Exception Provisions
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder provision(Consent.Provision... provision) {
                for (Consent.Provision value : provision) {
                    this.provision.add(value);
                }
                return this;
            }

            /**
             * Provisions which provide exceptions to the base provision or subprovisions.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param provision
             *     Nested Exception Provisions
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder provision(Collection<Consent.Provision> provision) {
                this.provision = new ArrayList<>(provision);
                return this;
            }

            /**
             * Build the {@link Provision}
             * 
             * @return
             *     An immutable object of type {@link Provision}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Provision per the base specification
             */
            @Override
            public Provision build() {
                Provision provision = new Provision(this);
                if (validating) {
                    validate(provision);
                }
                return provision;
            }

            protected void validate(Provision provision) {
                super.validate(provision);
                ValidationSupport.checkList(provision.actor, "actor", Actor.class);
                ValidationSupport.checkList(provision.action, "action", CodeableConcept.class);
                ValidationSupport.checkList(provision.securityLabel, "securityLabel", Coding.class);
                ValidationSupport.checkList(provision.purpose, "purpose", Coding.class);
                ValidationSupport.checkList(provision.documentType, "documentType", Coding.class);
                ValidationSupport.checkList(provision.resourceType, "resourceType", Coding.class);
                ValidationSupport.checkList(provision.code, "code", CodeableConcept.class);
                ValidationSupport.checkList(provision.data, "data", Data.class);
                ValidationSupport.checkList(provision.provision, "provision", Consent.Provision.class);
                ValidationSupport.requireValueOrChildren(provision);
            }

            protected Builder from(Provision provision) {
                super.from(provision);
                period = provision.period;
                actor.addAll(provision.actor);
                action.addAll(provision.action);
                securityLabel.addAll(provision.securityLabel);
                purpose.addAll(provision.purpose);
                documentType.addAll(provision.documentType);
                resourceType.addAll(provision.resourceType);
                code.addAll(provision.code);
                dataPeriod = provision.dataPeriod;
                data.addAll(provision.data);
                expression = provision.expression;
                this.provision.addAll(provision.provision);
                return this;
            }
        }

        /**
         * Who or what is controlled by this provision. Use group to identify a set of actors by some property they share (e.g. 
         * 'admitting officers').
         */
        public static class Actor extends BackboneElement {
            @Binding(
                bindingName = "ConsentActorRole",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "How an actor is involved in the consent considerations.",
                valueSet = "http://hl7.org/fhir/ValueSet/participation-role-type"
            )
            private final CodeableConcept role;
            @ReferenceTarget({ "Device", "Group", "CareTeam", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole" })
            private final Reference reference;

            private Actor(Builder builder) {
                super(builder);
                role = builder.role;
                reference = builder.reference;
            }

            /**
             * How the individual is involved in the resources content that is described in the exception.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getRole() {
                return role;
            }

            /**
             * The resource that identifies the actor. To identify actors by type, use group to identify a set of actors by some 
             * property they share (e.g. 'admitting officers').
             * 
             * @return
             *     An immutable object of type {@link Reference} that may be null.
             */
            public Reference getReference() {
                return reference;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (role != null) || 
                    (reference != null);
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
                        accept(role, "role", visitor);
                        accept(reference, "reference", visitor);
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
                    Objects.equals(role, other.role) && 
                    Objects.equals(reference, other.reference);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        role, 
                        reference);
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
                private CodeableConcept role;
                private Reference reference;

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
                 * How the individual is involved in the resources content that is described in the exception.
                 * 
                 * @param role
                 *     How the actor is involved
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder role(CodeableConcept role) {
                    this.role = role;
                    return this;
                }

                /**
                 * The resource that identifies the actor. To identify actors by type, use group to identify a set of actors by some 
                 * property they share (e.g. 'admitting officers').
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Device}</li>
                 * <li>{@link Group}</li>
                 * <li>{@link CareTeam}</li>
                 * <li>{@link Organization}</li>
                 * <li>{@link Patient}</li>
                 * <li>{@link Practitioner}</li>
                 * <li>{@link RelatedPerson}</li>
                 * <li>{@link PractitionerRole}</li>
                 * </ul>
                 * 
                 * @param reference
                 *     Resource for the actor (or group, by role)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder reference(Reference reference) {
                    this.reference = reference;
                    return this;
                }

                /**
                 * Build the {@link Actor}
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
                    ValidationSupport.checkReferenceType(actor.reference, "reference", "Device", "Group", "CareTeam", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole");
                    ValidationSupport.requireValueOrChildren(actor);
                }

                protected Builder from(Actor actor) {
                    super.from(actor);
                    role = actor.role;
                    reference = actor.reference;
                    return this;
                }
            }
        }

        /**
         * The resources controlled by this provision if specific resources are referenced.
         */
        public static class Data extends BackboneElement {
            @Summary
            @Binding(
                bindingName = "ConsentDataMeaning",
                strength = BindingStrength.Value.REQUIRED,
                description = "How a resource reference is interpreted when testing consent restrictions.",
                valueSet = "http://hl7.org/fhir/ValueSet/consent-data-meaning|5.0.0"
            )
            @Required
            private final ConsentDataMeaning meaning;
            @Summary
            @Required
            private final Reference reference;

            private Data(Builder builder) {
                super(builder);
                meaning = builder.meaning;
                reference = builder.reference;
            }

            /**
             * How the resource reference is interpreted when testing consent restrictions.
             * 
             * @return
             *     An immutable object of type {@link ConsentDataMeaning} that is non-null.
             */
            public ConsentDataMeaning getMeaning() {
                return meaning;
            }

            /**
             * A reference to a specific resource that defines which resources are covered by this consent.
             * 
             * @return
             *     An immutable object of type {@link Reference} that is non-null.
             */
            public Reference getReference() {
                return reference;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (meaning != null) || 
                    (reference != null);
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
                        accept(meaning, "meaning", visitor);
                        accept(reference, "reference", visitor);
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
                Data other = (Data) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(meaning, other.meaning) && 
                    Objects.equals(reference, other.reference);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        meaning, 
                        reference);
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
                private ConsentDataMeaning meaning;
                private Reference reference;

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
                 * How the resource reference is interpreted when testing consent restrictions.
                 * 
                 * <p>This element is required.
                 * 
                 * @param meaning
                 *     instance | related | dependents | authoredby
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder meaning(ConsentDataMeaning meaning) {
                    this.meaning = meaning;
                    return this;
                }

                /**
                 * A reference to a specific resource that defines which resources are covered by this consent.
                 * 
                 * <p>This element is required.
                 * 
                 * @param reference
                 *     The actual data reference
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder reference(Reference reference) {
                    this.reference = reference;
                    return this;
                }

                /**
                 * Build the {@link Data}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>meaning</li>
                 * <li>reference</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Data}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Data per the base specification
                 */
                @Override
                public Data build() {
                    Data data = new Data(this);
                    if (validating) {
                        validate(data);
                    }
                    return data;
                }

                protected void validate(Data data) {
                    super.validate(data);
                    ValidationSupport.requireNonNull(data.meaning, "meaning");
                    ValidationSupport.requireNonNull(data.reference, "reference");
                    ValidationSupport.requireValueOrChildren(data);
                }

                protected Builder from(Data data) {
                    super.from(data);
                    meaning = data.meaning;
                    reference = data.reference;
                    return this;
                }
            }
        }
    }
}
