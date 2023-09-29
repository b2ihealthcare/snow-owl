/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/questionnaire-answer-constraint")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class QuestionnaireAnswerConstraint extends Code {
    /**
     * Options only
     * 
     * <p>Only values listed as answerOption or in the expansion of the answerValueSet are permitted
     */
    public static final QuestionnaireAnswerConstraint OPTIONS_ONLY = QuestionnaireAnswerConstraint.builder().value(Value.OPTIONS_ONLY).build();

    /**
     * Options or 'type'
     * 
     * <p>In addition to the values listed as answerOption or in the expansion of the answerValueSet, any other values that 
     * correspond to the specified item.type are permitted
     */
    public static final QuestionnaireAnswerConstraint OPTIONS_OR_TYPE = QuestionnaireAnswerConstraint.builder().value(Value.OPTIONS_OR_TYPE).build();

    /**
     * Options or string
     * 
     * <p>In addition to the values listed as answerOption or in the expansion of the answerValueSet, free-text strings are 
     * permitted. Answers will have a type of 'string'.
     */
    public static final QuestionnaireAnswerConstraint OPTIONS_OR_STRING = QuestionnaireAnswerConstraint.builder().value(Value.OPTIONS_OR_STRING).build();

    private volatile int hashCode;

    private QuestionnaireAnswerConstraint(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this QuestionnaireAnswerConstraint as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating QuestionnaireAnswerConstraint objects from a passed enum value.
     */
    public static QuestionnaireAnswerConstraint of(Value value) {
        switch (value) {
        case OPTIONS_ONLY:
            return OPTIONS_ONLY;
        case OPTIONS_OR_TYPE:
            return OPTIONS_OR_TYPE;
        case OPTIONS_OR_STRING:
            return OPTIONS_OR_STRING;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating QuestionnaireAnswerConstraint objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static QuestionnaireAnswerConstraint of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating QuestionnaireAnswerConstraint objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating QuestionnaireAnswerConstraint objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(Value.from(value));
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
        QuestionnaireAnswerConstraint other = (QuestionnaireAnswerConstraint) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(Value.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for QuestionnaireAnswerConstraint
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public QuestionnaireAnswerConstraint build() {
            QuestionnaireAnswerConstraint questionnaireAnswerConstraint = new QuestionnaireAnswerConstraint(this);
            if (validating) {
                validate(questionnaireAnswerConstraint);
            }
            return questionnaireAnswerConstraint;
        }

        protected void validate(QuestionnaireAnswerConstraint questionnaireAnswerConstraint) {
            super.validate(questionnaireAnswerConstraint);
        }

        protected Builder from(QuestionnaireAnswerConstraint questionnaireAnswerConstraint) {
            super.from(questionnaireAnswerConstraint);
            return this;
        }
    }

    public enum Value {
        /**
         * Options only
         * 
         * <p>Only values listed as answerOption or in the expansion of the answerValueSet are permitted
         */
        OPTIONS_ONLY("optionsOnly"),

        /**
         * Options or 'type'
         * 
         * <p>In addition to the values listed as answerOption or in the expansion of the answerValueSet, any other values that 
         * correspond to the specified item.type are permitted
         */
        OPTIONS_OR_TYPE("optionsOrType"),

        /**
         * Options or string
         * 
         * <p>In addition to the values listed as answerOption or in the expansion of the answerValueSet, free-text strings are 
         * permitted. Answers will have a type of 'string'.
         */
        OPTIONS_OR_STRING("optionsOrString");

        private final java.lang.String value;

        Value(java.lang.String value) {
            this.value = value;
        }

        /**
         * @return
         *     The java.lang.String value of the code represented by this enum
         */
        public java.lang.String value() {
            return value;
        }

        /**
         * Factory method for creating QuestionnaireAnswerConstraint.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding QuestionnaireAnswerConstraint.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "optionsOnly":
                return OPTIONS_ONLY;
            case "optionsOrType":
                return OPTIONS_OR_TYPE;
            case "optionsOrString":
                return OPTIONS_OR_STRING;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
