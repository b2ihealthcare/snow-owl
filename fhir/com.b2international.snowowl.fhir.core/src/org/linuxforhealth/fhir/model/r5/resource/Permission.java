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
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Expression;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ConsentDataMeaning;
import org.linuxforhealth.fhir.model.r5.type.code.PermissionCombining;
import org.linuxforhealth.fhir.model.r5.type.code.PermissionProvisionType;
import org.linuxforhealth.fhir.model.r5.type.code.PermissionStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Permission resource holds access rules for a given data and context.
 * 
 * <p>Maturity level: FMM0 (Trial Use)
 */
@Maturity(
    level = 0,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "permission-0",
    level = "Warning",
    location = "rule.activity.purpose",
    description = "SHOULD contain a code from value set http://terminology.hl7.org/ValueSet/v3-PurposeOfUse",
    expression = "$this.memberOf('http://terminology.hl7.org/ValueSet/v3-PurposeOfUse', 'preferred')",
    source = "http://hl7.org/fhir/StructureDefinition/Permission",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Permission extends DomainResource {
    @Summary
    @Binding(
        bindingName = "PermissionStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes identifying the lifecycle stage of a product.",
        valueSet = "http://hl7.org/fhir/ValueSet/permission-status|5.0.0"
    )
    @Required
    private final PermissionStatus status;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "RelatedPerson", "HealthcareService" })
    private final Reference asserter;
    @Summary
    private final List<DateTime> date;
    @Summary
    private final Period validity;
    @Summary
    private final Justification justification;
    @Summary
    @Binding(
        bindingName = "PermissionCombining",
        strength = BindingStrength.Value.REQUIRED,
        description = "How the rules are to be combined.",
        valueSet = "http://hl7.org/fhir/ValueSet/permission-rule-combining|5.0.0"
    )
    @Required
    private final PermissionCombining combining;
    @Summary
    private final List<Rule> rule;

    private Permission(Builder builder) {
        super(builder);
        status = builder.status;
        asserter = builder.asserter;
        date = Collections.unmodifiableList(builder.date);
        validity = builder.validity;
        justification = builder.justification;
        combining = builder.combining;
        rule = Collections.unmodifiableList(builder.rule);
    }

    /**
     * Status.
     * 
     * @return
     *     An immutable object of type {@link PermissionStatus} that is non-null.
     */
    public PermissionStatus getStatus() {
        return status;
    }

    /**
     * The person or entity that asserts the permission.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getAsserter() {
        return asserter;
    }

    /**
     * The date that permission was asserted.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link DateTime} that may be empty.
     */
    public List<DateTime> getDate() {
        return date;
    }

    /**
     * The period in which the permission is active.
     * 
     * @return
     *     An immutable object of type {@link Period} that may be null.
     */
    public Period getValidity() {
        return validity;
    }

    /**
     * The asserted justification for using the data.
     * 
     * @return
     *     An immutable object of type {@link Justification} that may be null.
     */
    public Justification getJustification() {
        return justification;
    }

    /**
     * Defines a procedure for arriving at an access decision given the set of rules.
     * 
     * @return
     *     An immutable object of type {@link PermissionCombining} that is non-null.
     */
    public PermissionCombining getCombining() {
        return combining;
    }

    /**
     * A set of rules.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Rule} that may be empty.
     */
    public List<Rule> getRule() {
        return rule;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            (status != null) || 
            (asserter != null) || 
            !date.isEmpty() || 
            (validity != null) || 
            (justification != null) || 
            (combining != null) || 
            !rule.isEmpty();
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
                accept(contained, "contained", visitor, org.linuxforhealth.fhir.model.r5.resource.Resource.class);
                accept(extension, "extension", visitor, Extension.class);
                accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                accept(status, "status", visitor);
                accept(asserter, "asserter", visitor);
                accept(date, "date", visitor, DateTime.class);
                accept(validity, "validity", visitor);
                accept(justification, "justification", visitor);
                accept(combining, "combining", visitor);
                accept(rule, "rule", visitor, Rule.class);
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
        Permission other = (Permission) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(status, other.status) && 
            Objects.equals(asserter, other.asserter) && 
            Objects.equals(date, other.date) && 
            Objects.equals(validity, other.validity) && 
            Objects.equals(justification, other.justification) && 
            Objects.equals(combining, other.combining) && 
            Objects.equals(rule, other.rule);
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
                status, 
                asserter, 
                date, 
                validity, 
                justification, 
                combining, 
                rule);
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
        private PermissionStatus status;
        private Reference asserter;
        private List<DateTime> date = new ArrayList<>();
        private Period validity;
        private Justification justification;
        private PermissionCombining combining;
        private List<Rule> rule = new ArrayList<>();

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
        public Builder contained(org.linuxforhealth.fhir.model.r5.resource.Resource... contained) {
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
        public Builder contained(Collection<org.linuxforhealth.fhir.model.r5.resource.Resource> contained) {
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
         * Status.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     active | entered-in-error | draft | rejected
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(PermissionStatus status) {
            this.status = status;
            return this;
        }

        /**
         * The person or entity that asserts the permission.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * <li>{@link CareTeam}</li>
         * <li>{@link Patient}</li>
         * <li>{@link RelatedPerson}</li>
         * <li>{@link HealthcareService}</li>
         * </ul>
         * 
         * @param asserter
         *     The person or entity that asserts the permission
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder asserter(Reference asserter) {
            this.asserter = asserter;
            return this;
        }

        /**
         * The date that permission was asserted.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param date
         *     The date that permission was asserted
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder date(DateTime... date) {
            for (DateTime value : date) {
                this.date.add(value);
            }
            return this;
        }

        /**
         * The date that permission was asserted.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param date
         *     The date that permission was asserted
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder date(Collection<DateTime> date) {
            this.date = new ArrayList<>(date);
            return this;
        }

        /**
         * The period in which the permission is active.
         * 
         * @param validity
         *     The period in which the permission is active
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder validity(Period validity) {
            this.validity = validity;
            return this;
        }

        /**
         * The asserted justification for using the data.
         * 
         * @param justification
         *     The asserted justification for using the data
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder justification(Justification justification) {
            this.justification = justification;
            return this;
        }

        /**
         * Defines a procedure for arriving at an access decision given the set of rules.
         * 
         * <p>This element is required.
         * 
         * @param combining
         *     deny-overrides | permit-overrides | ordered-deny-overrides | ordered-permit-overrides | deny-unless-permit | permit-
         *     unless-deny
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder combining(PermissionCombining combining) {
            this.combining = combining;
            return this;
        }

        /**
         * A set of rules.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param rule
         *     Constraints to the Permission
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder rule(Rule... rule) {
            for (Rule value : rule) {
                this.rule.add(value);
            }
            return this;
        }

        /**
         * A set of rules.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param rule
         *     Constraints to the Permission
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder rule(Collection<Rule> rule) {
            this.rule = new ArrayList<>(rule);
            return this;
        }

        /**
         * Build the {@link Permission}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>combining</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link Permission}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Permission per the base specification
         */
        @Override
        public Permission build() {
            Permission permission = new Permission(this);
            if (validating) {
                validate(permission);
            }
            return permission;
        }

        protected void validate(Permission permission) {
            super.validate(permission);
            ValidationSupport.requireNonNull(permission.status, "status");
            ValidationSupport.checkList(permission.date, "date", DateTime.class);
            ValidationSupport.requireNonNull(permission.combining, "combining");
            ValidationSupport.checkList(permission.rule, "rule", Rule.class);
            ValidationSupport.checkReferenceType(permission.asserter, "asserter", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "RelatedPerson", "HealthcareService");
        }

        protected Builder from(Permission permission) {
            super.from(permission);
            status = permission.status;
            asserter = permission.asserter;
            date.addAll(permission.date);
            validity = permission.validity;
            justification = permission.justification;
            combining = permission.combining;
            rule.addAll(permission.rule);
            return this;
        }
    }

    /**
     * The asserted justification for using the data.
     */
    public static class Justification extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "ConsentRegulatoryBasis",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Regulatory policy examples",
            valueSet = "http://hl7.org/fhir/ValueSet/consent-policy"
        )
        private final List<CodeableConcept> basis;
        @Summary
        private final List<Reference> evidence;

        private Justification(Builder builder) {
            super(builder);
            basis = Collections.unmodifiableList(builder.basis);
            evidence = Collections.unmodifiableList(builder.evidence);
        }

        /**
         * This would be a codeableconcept, or a coding, which can be constrained to , for example, the 6 grounds for processing 
         * in GDPR.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getBasis() {
            return basis;
        }

        /**
         * Justifing rational.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getEvidence() {
            return evidence;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                !basis.isEmpty() || 
                !evidence.isEmpty();
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
                    accept(basis, "basis", visitor, CodeableConcept.class);
                    accept(evidence, "evidence", visitor, Reference.class);
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
            Justification other = (Justification) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(basis, other.basis) && 
                Objects.equals(evidence, other.evidence);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    basis, 
                    evidence);
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
            private List<CodeableConcept> basis = new ArrayList<>();
            private List<Reference> evidence = new ArrayList<>();

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
             * This would be a codeableconcept, or a coding, which can be constrained to , for example, the 6 grounds for processing 
             * in GDPR.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param basis
             *     The regulatory grounds upon which this Permission builds
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder basis(CodeableConcept... basis) {
                for (CodeableConcept value : basis) {
                    this.basis.add(value);
                }
                return this;
            }

            /**
             * This would be a codeableconcept, or a coding, which can be constrained to , for example, the 6 grounds for processing 
             * in GDPR.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param basis
             *     The regulatory grounds upon which this Permission builds
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder basis(Collection<CodeableConcept> basis) {
                this.basis = new ArrayList<>(basis);
                return this;
            }

            /**
             * Justifing rational.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param evidence
             *     Justifing rational
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder evidence(Reference... evidence) {
                for (Reference value : evidence) {
                    this.evidence.add(value);
                }
                return this;
            }

            /**
             * Justifing rational.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param evidence
             *     Justifing rational
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder evidence(Collection<Reference> evidence) {
                this.evidence = new ArrayList<>(evidence);
                return this;
            }

            /**
             * Build the {@link Justification}
             * 
             * @return
             *     An immutable object of type {@link Justification}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Justification per the base specification
             */
            @Override
            public Justification build() {
                Justification justification = new Justification(this);
                if (validating) {
                    validate(justification);
                }
                return justification;
            }

            protected void validate(Justification justification) {
                super.validate(justification);
                ValidationSupport.checkList(justification.basis, "basis", CodeableConcept.class);
                ValidationSupport.checkList(justification.evidence, "evidence", Reference.class);
                ValidationSupport.requireValueOrChildren(justification);
            }

            protected Builder from(Justification justification) {
                super.from(justification);
                basis.addAll(justification.basis);
                evidence.addAll(justification.evidence);
                return this;
            }
        }
    }

    /**
     * A set of rules.
     */
    public static class Rule extends BackboneElement {
        @Summary
        @Binding(
            bindingName = "PermissionProvisionType",
            strength = BindingStrength.Value.REQUIRED,
            description = "How a rule statement is applied.",
            valueSet = "http://hl7.org/fhir/ValueSet/consent-provision-type|5.0.0"
        )
        private final PermissionProvisionType type;
        @Summary
        private final List<Data> data;
        @Summary
        private final List<Activity> activity;
        @Summary
        @Binding(
            bindingName = "PermissionUsageLimits",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Obligations and Refrains",
            valueSet = "http://hl7.org/fhir/ValueSet/security-label-event-examples"
        )
        private final List<CodeableConcept> limit;

        private Rule(Builder builder) {
            super(builder);
            type = builder.type;
            data = Collections.unmodifiableList(builder.data);
            activity = Collections.unmodifiableList(builder.activity);
            limit = Collections.unmodifiableList(builder.limit);
        }

        /**
         * deny | permit.
         * 
         * @return
         *     An immutable object of type {@link PermissionProvisionType} that may be null.
         */
        public PermissionProvisionType getType() {
            return type;
        }

        /**
         * A description or definition of which activities are allowed to be done on the data.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Data} that may be empty.
         */
        public List<Data> getData() {
            return data;
        }

        /**
         * A description or definition of which activities are allowed to be done on the data.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Activity} that may be empty.
         */
        public List<Activity> getActivity() {
            return activity;
        }

        /**
         * What limits apply to the use of the data.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
         */
        public List<CodeableConcept> getLimit() {
            return limit;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (type != null) || 
                !data.isEmpty() || 
                !activity.isEmpty() || 
                !limit.isEmpty();
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
                    accept(data, "data", visitor, Data.class);
                    accept(activity, "activity", visitor, Activity.class);
                    accept(limit, "limit", visitor, CodeableConcept.class);
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
            Rule other = (Rule) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(type, other.type) && 
                Objects.equals(data, other.data) && 
                Objects.equals(activity, other.activity) && 
                Objects.equals(limit, other.limit);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    type, 
                    data, 
                    activity, 
                    limit);
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
            private PermissionProvisionType type;
            private List<Data> data = new ArrayList<>();
            private List<Activity> activity = new ArrayList<>();
            private List<CodeableConcept> limit = new ArrayList<>();

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
             * deny | permit.
             * 
             * @param type
             *     deny | permit
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder type(PermissionProvisionType type) {
                this.type = type;
                return this;
            }

            /**
             * A description or definition of which activities are allowed to be done on the data.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param data
             *     The selection criteria to identify data that is within scope of this provision
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
             * A description or definition of which activities are allowed to be done on the data.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param data
             *     The selection criteria to identify data that is within scope of this provision
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
             * A description or definition of which activities are allowed to be done on the data.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param activity
             *     A description or definition of which activities are allowed to be done on the data
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder activity(Activity... activity) {
                for (Activity value : activity) {
                    this.activity.add(value);
                }
                return this;
            }

            /**
             * A description or definition of which activities are allowed to be done on the data.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param activity
             *     A description or definition of which activities are allowed to be done on the data
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder activity(Collection<Activity> activity) {
                this.activity = new ArrayList<>(activity);
                return this;
            }

            /**
             * What limits apply to the use of the data.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param limit
             *     What limits apply to the use of the data
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder limit(CodeableConcept... limit) {
                for (CodeableConcept value : limit) {
                    this.limit.add(value);
                }
                return this;
            }

            /**
             * What limits apply to the use of the data.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param limit
             *     What limits apply to the use of the data
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder limit(Collection<CodeableConcept> limit) {
                this.limit = new ArrayList<>(limit);
                return this;
            }

            /**
             * Build the {@link Rule}
             * 
             * @return
             *     An immutable object of type {@link Rule}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Rule per the base specification
             */
            @Override
            public Rule build() {
                Rule rule = new Rule(this);
                if (validating) {
                    validate(rule);
                }
                return rule;
            }

            protected void validate(Rule rule) {
                super.validate(rule);
                ValidationSupport.checkList(rule.data, "data", Data.class);
                ValidationSupport.checkList(rule.activity, "activity", Activity.class);
                ValidationSupport.checkList(rule.limit, "limit", CodeableConcept.class);
                ValidationSupport.requireValueOrChildren(rule);
            }

            protected Builder from(Rule rule) {
                super.from(rule);
                type = rule.type;
                data.addAll(rule.data);
                activity.addAll(rule.activity);
                limit.addAll(rule.limit);
                return this;
            }
        }

        /**
         * A description or definition of which activities are allowed to be done on the data.
         */
        public static class Data extends BackboneElement {
            @Summary
            private final List<Resource> resource;
            @Summary
            private final List<Coding> security;
            @Summary
            private final List<Period> period;
            @Summary
            private final Expression expression;

            private Data(Builder builder) {
                super(builder);
                resource = Collections.unmodifiableList(builder.resource);
                security = Collections.unmodifiableList(builder.security);
                period = Collections.unmodifiableList(builder.period);
                expression = builder.expression;
            }

            /**
             * Explicit FHIR Resource references.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Resource} that may be empty.
             */
            public List<Resource> getResource() {
                return resource;
            }

            /**
             * The data in scope are those with the given codes present in that data .meta.security element.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Coding} that may be empty.
             */
            public List<Coding> getSecurity() {
                return security;
            }

            /**
             * Clinical or Operational Relevant period of time that bounds the data controlled by this rule.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Period} that may be empty.
             */
            public List<Period> getPeriod() {
                return period;
            }

            /**
             * Used when other data selection elements are insufficient.
             * 
             * @return
             *     An immutable object of type {@link Expression} that may be null.
             */
            public Expression getExpression() {
                return expression;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !resource.isEmpty() || 
                    !security.isEmpty() || 
                    !period.isEmpty() || 
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
                        accept(resource, "resource", visitor, Resource.class);
                        accept(security, "security", visitor, Coding.class);
                        accept(period, "period", visitor, Period.class);
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
                Data other = (Data) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(resource, other.resource) && 
                    Objects.equals(security, other.security) && 
                    Objects.equals(period, other.period) && 
                    Objects.equals(expression, other.expression);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        resource, 
                        security, 
                        period, 
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
                private List<Resource> resource = new ArrayList<>();
                private List<Coding> security = new ArrayList<>();
                private List<Period> period = new ArrayList<>();
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
                 * Explicit FHIR Resource references.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param resource
                 *     Explicit FHIR Resource references
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder resource(Resource... resource) {
                    for (Resource value : resource) {
                        this.resource.add(value);
                    }
                    return this;
                }

                /**
                 * Explicit FHIR Resource references.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param resource
                 *     Explicit FHIR Resource references
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder resource(Collection<Resource> resource) {
                    this.resource = new ArrayList<>(resource);
                    return this;
                }

                /**
                 * The data in scope are those with the given codes present in that data .meta.security element.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param security
                 *     Security tag code on .meta.security
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder security(Coding... security) {
                    for (Coding value : security) {
                        this.security.add(value);
                    }
                    return this;
                }

                /**
                 * The data in scope are those with the given codes present in that data .meta.security element.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param security
                 *     Security tag code on .meta.security
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder security(Collection<Coding> security) {
                    this.security = new ArrayList<>(security);
                    return this;
                }

                /**
                 * Clinical or Operational Relevant period of time that bounds the data controlled by this rule.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param period
                 *     Timeframe encompasing data create/update
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder period(Period... period) {
                    for (Period value : period) {
                        this.period.add(value);
                    }
                    return this;
                }

                /**
                 * Clinical or Operational Relevant period of time that bounds the data controlled by this rule.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param period
                 *     Timeframe encompasing data create/update
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder period(Collection<Period> period) {
                    this.period = new ArrayList<>(period);
                    return this;
                }

                /**
                 * Used when other data selection elements are insufficient.
                 * 
                 * @param expression
                 *     Expression identifying the data
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder expression(Expression expression) {
                    this.expression = expression;
                    return this;
                }

                /**
                 * Build the {@link Data}
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
                    ValidationSupport.checkList(data.resource, "resource", Resource.class);
                    ValidationSupport.checkList(data.security, "security", Coding.class);
                    ValidationSupport.checkList(data.period, "period", Period.class);
                    ValidationSupport.requireValueOrChildren(data);
                }

                protected Builder from(Data data) {
                    super.from(data);
                    resource.addAll(data.resource);
                    security.addAll(data.security);
                    period.addAll(data.period);
                    expression = data.expression;
                    return this;
                }
            }

            /**
             * Explicit FHIR Resource references.
             */
            public static class Resource extends BackboneElement {
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

                private Resource(Builder builder) {
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
                    Resource other = (Resource) obj;
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
                     * Build the {@link Resource}
                     * 
                     * <p>Required elements:
                     * <ul>
                     * <li>meaning</li>
                     * <li>reference</li>
                     * </ul>
                     * 
                     * @return
                     *     An immutable object of type {@link Resource}
                     * @throws IllegalStateException
                     *     if the current state cannot be built into a valid Resource per the base specification
                     */
                    @Override
                    public Resource build() {
                        Resource resource = new Resource(this);
                        if (validating) {
                            validate(resource);
                        }
                        return resource;
                    }

                    protected void validate(Resource resource) {
                        super.validate(resource);
                        ValidationSupport.requireNonNull(resource.meaning, "meaning");
                        ValidationSupport.requireNonNull(resource.reference, "reference");
                        ValidationSupport.requireValueOrChildren(resource);
                    }

                    protected Builder from(Resource resource) {
                        super.from(resource);
                        meaning = resource.meaning;
                        reference = resource.reference;
                        return this;
                    }
                }
            }
        }

        /**
         * A description or definition of which activities are allowed to be done on the data.
         */
        public static class Activity extends BackboneElement {
            @Summary
            @ReferenceTarget({ "Device", "Group", "CareTeam", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole" })
            private final List<Reference> actor;
            @Summary
            @Binding(
                bindingName = "ProcessingActivityAction",
                strength = BindingStrength.Value.EXAMPLE,
                description = "Detailed codes for the action.",
                valueSet = "http://hl7.org/fhir/ValueSet/consent-action"
            )
            private final List<CodeableConcept> action;
            @Summary
            @Binding(
                bindingName = "PurposeOfUse",
                strength = BindingStrength.Value.PREFERRED,
                description = "What purposes of use are controlled by this exception. If more than one label is specified, operations must have all the specified labels.",
                valueSet = "http://terminology.hl7.org/ValueSet/v3-PurposeOfUse"
            )
            private final List<CodeableConcept> purpose;

            private Activity(Builder builder) {
                super(builder);
                actor = Collections.unmodifiableList(builder.actor);
                action = Collections.unmodifiableList(builder.action);
                purpose = Collections.unmodifiableList(builder.purpose);
            }

            /**
             * The actor(s) authorized for the defined activity.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
             */
            public List<Reference> getActor() {
                return actor;
            }

            /**
             * Actions controlled by this Rule.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getAction() {
                return action;
            }

            /**
             * The purpose for which the permission is given.
             * 
             * @return
             *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
             */
            public List<CodeableConcept> getPurpose() {
                return purpose;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    !actor.isEmpty() || 
                    !action.isEmpty() || 
                    !purpose.isEmpty();
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
                        accept(actor, "actor", visitor, Reference.class);
                        accept(action, "action", visitor, CodeableConcept.class);
                        accept(purpose, "purpose", visitor, CodeableConcept.class);
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
                Activity other = (Activity) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(actor, other.actor) && 
                    Objects.equals(action, other.action) && 
                    Objects.equals(purpose, other.purpose);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        actor, 
                        action, 
                        purpose);
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
                private List<Reference> actor = new ArrayList<>();
                private List<CodeableConcept> action = new ArrayList<>();
                private List<CodeableConcept> purpose = new ArrayList<>();

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
                 * The actor(s) authorized for the defined activity.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
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
                 * @param actor
                 *     Authorized actor(s)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder actor(Reference... actor) {
                    for (Reference value : actor) {
                        this.actor.add(value);
                    }
                    return this;
                }

                /**
                 * The actor(s) authorized for the defined activity.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * <p>Allowed resource types for the references:
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
                 * @param actor
                 *     Authorized actor(s)
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder actor(Collection<Reference> actor) {
                    this.actor = new ArrayList<>(actor);
                    return this;
                }

                /**
                 * Actions controlled by this Rule.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param action
                 *     Actions controlled by this rule
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
                 * Actions controlled by this Rule.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param action
                 *     Actions controlled by this rule
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
                 * The purpose for which the permission is given.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param purpose
                 *     The purpose for which the permission is given
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder purpose(CodeableConcept... purpose) {
                    for (CodeableConcept value : purpose) {
                        this.purpose.add(value);
                    }
                    return this;
                }

                /**
                 * The purpose for which the permission is given.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param purpose
                 *     The purpose for which the permission is given
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                public Builder purpose(Collection<CodeableConcept> purpose) {
                    this.purpose = new ArrayList<>(purpose);
                    return this;
                }

                /**
                 * Build the {@link Activity}
                 * 
                 * @return
                 *     An immutable object of type {@link Activity}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Activity per the base specification
                 */
                @Override
                public Activity build() {
                    Activity activity = new Activity(this);
                    if (validating) {
                        validate(activity);
                    }
                    return activity;
                }

                protected void validate(Activity activity) {
                    super.validate(activity);
                    ValidationSupport.checkList(activity.actor, "actor", Reference.class);
                    ValidationSupport.checkList(activity.action, "action", CodeableConcept.class);
                    ValidationSupport.checkList(activity.purpose, "purpose", CodeableConcept.class);
                    ValidationSupport.checkReferenceType(activity.actor, "actor", "Device", "Group", "CareTeam", "Organization", "Patient", "Practitioner", "RelatedPerson", "PractitionerRole");
                    ValidationSupport.requireValueOrChildren(activity);
                }

                protected Builder from(Activity activity) {
                    super.from(activity);
                    actor.addAll(activity.actor);
                    action.addAll(activity.action);
                    purpose.addAll(activity.purpose);
                    return this;
                }
            }
        }
    }
}
