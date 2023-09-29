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
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Address;
import org.linuxforhealth.fhir.model.r5.type.Age;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.Attachment;
import org.linuxforhealth.fhir.model.r5.type.Availability;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Base64Binary;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.ContactDetail;
import org.linuxforhealth.fhir.model.r5.type.ContactPoint;
import org.linuxforhealth.fhir.model.r5.type.Count;
import org.linuxforhealth.fhir.model.r5.type.DataRequirement;
import org.linuxforhealth.fhir.model.r5.type.Date;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Decimal;
import org.linuxforhealth.fhir.model.r5.type.Distance;
import org.linuxforhealth.fhir.model.r5.type.Dosage;
import org.linuxforhealth.fhir.model.r5.type.Duration;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Expression;
import org.linuxforhealth.fhir.model.r5.type.ExtendedContactDetail;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.HumanName;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Instant;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Integer64;
import org.linuxforhealth.fhir.model.r5.type.Markdown;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Money;
import org.linuxforhealth.fhir.model.r5.type.Oid;
import org.linuxforhealth.fhir.model.r5.type.ParameterDefinition;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.PositiveInt;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Ratio;
import org.linuxforhealth.fhir.model.r5.type.RatioRange;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.RelatedArtifact;
import org.linuxforhealth.fhir.model.r5.type.SampledData;
import org.linuxforhealth.fhir.model.r5.type.Signature;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.Time;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.TriggerDefinition;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.Url;
import org.linuxforhealth.fhir.model.r5.type.UsageContext;
import org.linuxforhealth.fhir.model.r5.type.Uuid;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * This resource is used to pass information into and back from an operation (whether invoked directly from REST or 
 * within a messaging environment). It is not persisted or allowed to be referenced by other resources.
 * 
 * <p>Maturity level: FMM5 (Normative)
 */
@Maturity(
    level = 5,
    status = StandardsStatus.Value.NORMATIVE
)
@Constraint(
    id = "inv-1",
    level = "Rule",
    location = "Parameters.parameter",
    description = "A parameter must have one and only one of (value, resource, part)",
    expression = "(part.exists() and value.empty() and resource.empty()) or (part.empty() and (value.exists() xor resource.exists()))",
    source = "http://hl7.org/fhir/StructureDefinition/Parameters"
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class Parameters extends Resource {
    @Summary
    private final List<Parameter> parameter;

    private Parameters(Builder builder) {
        super(builder);
        parameter = Collections.unmodifiableList(builder.parameter);
    }

    /**
     * A parameter passed to or received from the operation.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Parameter} that may be empty.
     */
    public List<Parameter> getParameter() {
        return parameter;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !parameter.isEmpty();
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
                accept(parameter, "parameter", visitor, Parameter.class);
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
        Parameters other = (Parameters) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(parameter, other.parameter);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                meta, 
                implicitRules, 
                language, 
                parameter);
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

    public static class Builder extends Resource.Builder {
        private List<Parameter> parameter = new ArrayList<>();

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
         * A parameter passed to or received from the operation.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Operation Parameter
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder parameter(Parameter... parameter) {
            for (Parameter value : parameter) {
                this.parameter.add(value);
            }
            return this;
        }

        /**
         * A parameter passed to or received from the operation.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Operation Parameter
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder parameter(Collection<Parameter> parameter) {
            this.parameter = new ArrayList<>(parameter);
            return this;
        }

        /**
         * Build the {@link Parameters}
         * 
         * @return
         *     An immutable object of type {@link Parameters}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid Parameters per the base specification
         */
        @Override
        public Parameters build() {
            Parameters parameters = new Parameters(this);
            if (validating) {
                validate(parameters);
            }
            return parameters;
        }

        protected void validate(Parameters parameters) {
            super.validate(parameters);
            ValidationSupport.checkList(parameters.parameter, "parameter", Parameter.class);
        }

        protected Builder from(Parameters parameters) {
            super.from(parameters);
            parameter.addAll(parameters.parameter);
            return this;
        }
    }

    /**
     * A parameter passed to or received from the operation.
     */
    public static class Parameter extends BackboneElement {
        @Summary
        @Required
        private final String name;
        @Summary
        @Choice({ Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class })
        private final Element value;
        @Summary
        private final Resource resource;
        @Summary
        private final List<Parameters.Parameter> part;

        private Parameter(Builder builder) {
            super(builder);
            name = builder.name;
            value = builder.value;
            resource = builder.resource;
            part = Collections.unmodifiableList(builder.part);
        }

        /**
         * The name of the parameter (reference to the operation definition).
         * 
         * @return
         *     An immutable object of type {@link String} that is non-null.
         */
        public String getName() {
            return name;
        }

        /**
         * Conveys the content if the parameter is a data type.
         * 
         * @return
         *     An immutable object of type {@link Base64Binary}, {@link Boolean}, {@link Canonical}, {@link Code}, {@link Date}, 
         *     {@link DateTime}, {@link Decimal}, {@link Id}, {@link Instant}, {@link Integer}, {@link Integer64}, {@link Markdown}, 
         *     {@link Oid}, {@link PositiveInt}, {@link String}, {@link Time}, {@link UnsignedInt}, {@link Uri}, {@link Url}, {@link 
         *     Uuid}, {@link Address}, {@link Age}, {@link Annotation}, {@link Attachment}, {@link CodeableConcept}, {@link 
         *     CodeableReference}, {@link Coding}, {@link ContactPoint}, {@link Count}, {@link Distance}, {@link Duration}, {@link 
         *     HumanName}, {@link Identifier}, {@link Money}, {@link Period}, {@link Quantity}, {@link Range}, {@link Ratio}, {@link 
         *     RatioRange}, {@link Reference}, {@link SampledData}, {@link Signature}, {@link Timing}, {@link ContactDetail}, {@link 
         *     DataRequirement}, {@link Expression}, {@link ParameterDefinition}, {@link RelatedArtifact}, {@link TriggerDefinition}, 
         *     {@link UsageContext}, {@link Availability}, {@link ExtendedContactDetail}, {@link Dosage} or {@link Meta} that may be 
         *     null.
         */
        public Element getValue() {
            return value;
        }

        /**
         * Conveys the content if the parameter is a whole resource.
         * 
         * @return
         *     An immutable object of type {@link Resource} that may be null.
         */
        public Resource getResource() {
            return resource;
        }

        /**
         * A named part of a multi-part parameter.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Parameter} that may be empty.
         */
        public List<Parameters.Parameter> getPart() {
            return part;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (name != null) || 
                (value != null) || 
                (resource != null) || 
                !part.isEmpty();
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
                    accept(name, "name", visitor);
                    accept(value, "value", visitor);
                    accept(resource, "resource", visitor);
                    accept(part, "part", visitor, Parameters.Parameter.class);
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
            Parameter other = (Parameter) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(name, other.name) && 
                Objects.equals(value, other.value) && 
                Objects.equals(resource, other.resource) && 
                Objects.equals(part, other.part);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    name, 
                    value, 
                    resource, 
                    part);
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
            private String name;
            private Element value;
            private Resource resource;
            private List<Parameters.Parameter> part = new ArrayList<>();

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
             * Convenience method for setting {@code name}.
             * 
             * <p>This element is required.
             * 
             * @param name
             *     Name from the definition
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
             * The name of the parameter (reference to the operation definition).
             * 
             * <p>This element is required.
             * 
             * @param name
             *     Name from the definition
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * @param value
             *     If parameter is a data type
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
             * Convenience method for setting {@code value} with choice type Date.
             * 
             * @param value
             *     If parameter is a data type
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.LocalDate value) {
                this.value = (value == null) ? null : Date.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Instant.
             * 
             * @param value
             *     If parameter is a data type
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.ZonedDateTime value) {
                this.value = (value == null) ? null : Instant.of(value);
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Integer.
             * 
             * @param value
             *     If parameter is a data type
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
             * Convenience method for setting {@code value} with choice type String.
             * 
             * @param value
             *     If parameter is a data type
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
             * Convenience method for setting {@code value} with choice type Time.
             * 
             * @param value
             *     If parameter is a data type
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.time.LocalTime value) {
                this.value = (value == null) ? null : Time.of(value);
                return this;
            }

            /**
             * Conveys the content if the parameter is a data type.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link Base64Binary}</li>
             * <li>{@link Boolean}</li>
             * <li>{@link Canonical}</li>
             * <li>{@link Code}</li>
             * <li>{@link Date}</li>
             * <li>{@link DateTime}</li>
             * <li>{@link Decimal}</li>
             * <li>{@link Id}</li>
             * <li>{@link Instant}</li>
             * <li>{@link Integer}</li>
             * <li>{@link Integer64}</li>
             * <li>{@link Markdown}</li>
             * <li>{@link Oid}</li>
             * <li>{@link PositiveInt}</li>
             * <li>{@link String}</li>
             * <li>{@link Time}</li>
             * <li>{@link UnsignedInt}</li>
             * <li>{@link Uri}</li>
             * <li>{@link Url}</li>
             * <li>{@link Uuid}</li>
             * <li>{@link Address}</li>
             * <li>{@link Age}</li>
             * <li>{@link Annotation}</li>
             * <li>{@link Attachment}</li>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link CodeableReference}</li>
             * <li>{@link Coding}</li>
             * <li>{@link ContactPoint}</li>
             * <li>{@link Count}</li>
             * <li>{@link Distance}</li>
             * <li>{@link Duration}</li>
             * <li>{@link HumanName}</li>
             * <li>{@link Identifier}</li>
             * <li>{@link Money}</li>
             * <li>{@link Period}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * <li>{@link Ratio}</li>
             * <li>{@link RatioRange}</li>
             * <li>{@link Reference}</li>
             * <li>{@link SampledData}</li>
             * <li>{@link Signature}</li>
             * <li>{@link Timing}</li>
             * <li>{@link ContactDetail}</li>
             * <li>{@link DataRequirement}</li>
             * <li>{@link Expression}</li>
             * <li>{@link ParameterDefinition}</li>
             * <li>{@link RelatedArtifact}</li>
             * <li>{@link TriggerDefinition}</li>
             * <li>{@link UsageContext}</li>
             * <li>{@link Availability}</li>
             * <li>{@link ExtendedContactDetail}</li>
             * <li>{@link Dosage}</li>
             * <li>{@link Meta}</li>
             * </ul>
             * 
             * @param value
             *     If parameter is a data type
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Conveys the content if the parameter is a whole resource.
             * 
             * @param resource
             *     If parameter is a whole resource
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder resource(Resource resource) {
                this.resource = resource;
                return this;
            }

            /**
             * A named part of a multi-part parameter.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param part
             *     Named part of a multi-part parameter
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder part(Parameters.Parameter... part) {
                for (Parameters.Parameter value : part) {
                    this.part.add(value);
                }
                return this;
            }

            /**
             * A named part of a multi-part parameter.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param part
             *     Named part of a multi-part parameter
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder part(Collection<Parameters.Parameter> part) {
                this.part = new ArrayList<>(part);
                return this;
            }

            /**
             * Build the {@link Parameter}
             * 
             * <p>Required elements:
             * <ul>
             * <li>name</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Parameter}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Parameter per the base specification
             */
            @Override
            public Parameter build() {
                Parameter parameter = new Parameter(this);
                if (validating) {
                    validate(parameter);
                }
                return parameter;
            }

            protected void validate(Parameter parameter) {
                super.validate(parameter);
                ValidationSupport.requireNonNull(parameter.name, "name");
                ValidationSupport.choiceElement(parameter.value, "value", Base64Binary.class, Boolean.class, Canonical.class, Code.class, Date.class, DateTime.class, Decimal.class, Id.class, Instant.class, Integer.class, Integer64.class, Markdown.class, Oid.class, PositiveInt.class, String.class, Time.class, UnsignedInt.class, Uri.class, Url.class, Uuid.class, Address.class, Age.class, Annotation.class, Attachment.class, CodeableConcept.class, CodeableReference.class, Coding.class, ContactPoint.class, Count.class, Distance.class, Duration.class, HumanName.class, Identifier.class, Money.class, Period.class, Quantity.class, Range.class, Ratio.class, RatioRange.class, Reference.class, SampledData.class, Signature.class, Timing.class, ContactDetail.class, DataRequirement.class, Expression.class, ParameterDefinition.class, RelatedArtifact.class, TriggerDefinition.class, UsageContext.class, Availability.class, ExtendedContactDetail.class, Dosage.class, Meta.class);
                ValidationSupport.checkList(parameter.part, "part", Parameters.Parameter.class);
                ValidationSupport.requireValueOrChildren(parameter);
            }

            protected Builder from(Parameter parameter) {
                super.from(parameter);
                name = parameter.name;
                value = parameter.value;
                resource = parameter.resource;
                part.addAll(parameter.part);
                return this;
            }
        }
    }
}
