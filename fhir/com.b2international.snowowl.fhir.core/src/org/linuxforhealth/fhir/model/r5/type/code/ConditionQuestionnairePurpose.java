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

@System("http://hl7.org/fhir/condition-questionnaire-purpose")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ConditionQuestionnairePurpose extends Code {
    /**
     * Pre-admit
     * 
     * <p>A pre-admit questionnaire.
     */
    public static final ConditionQuestionnairePurpose PREADMIT = ConditionQuestionnairePurpose.builder().value(Value.PREADMIT).build();

    /**
     * Diff Diagnosis
     * 
     * <p>A questionnaire that helps with diferential diagnosis.
     */
    public static final ConditionQuestionnairePurpose DIFF_DIAGNOSIS = ConditionQuestionnairePurpose.builder().value(Value.DIFF_DIAGNOSIS).build();

    /**
     * Outcome
     * 
     * <p>A questionnaire to check on outcomes for the patient.
     */
    public static final ConditionQuestionnairePurpose OUTCOME = ConditionQuestionnairePurpose.builder().value(Value.OUTCOME).build();

    private volatile int hashCode;

    private ConditionQuestionnairePurpose(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this ConditionQuestionnairePurpose as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating ConditionQuestionnairePurpose objects from a passed enum value.
     */
    public static ConditionQuestionnairePurpose of(Value value) {
        switch (value) {
        case PREADMIT:
            return PREADMIT;
        case DIFF_DIAGNOSIS:
            return DIFF_DIAGNOSIS;
        case OUTCOME:
            return OUTCOME;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating ConditionQuestionnairePurpose objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static ConditionQuestionnairePurpose of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating ConditionQuestionnairePurpose objects from a passed string value.
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
     * Inherited factory method for creating ConditionQuestionnairePurpose objects from a passed string value.
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
        ConditionQuestionnairePurpose other = (ConditionQuestionnairePurpose) obj;
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
         *     An enum constant for ConditionQuestionnairePurpose
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public ConditionQuestionnairePurpose build() {
            ConditionQuestionnairePurpose conditionQuestionnairePurpose = new ConditionQuestionnairePurpose(this);
            if (validating) {
                validate(conditionQuestionnairePurpose);
            }
            return conditionQuestionnairePurpose;
        }

        protected void validate(ConditionQuestionnairePurpose conditionQuestionnairePurpose) {
            super.validate(conditionQuestionnairePurpose);
        }

        protected Builder from(ConditionQuestionnairePurpose conditionQuestionnairePurpose) {
            super.from(conditionQuestionnairePurpose);
            return this;
        }
    }

    public enum Value {
        /**
         * Pre-admit
         * 
         * <p>A pre-admit questionnaire.
         */
        PREADMIT("preadmit"),

        /**
         * Diff Diagnosis
         * 
         * <p>A questionnaire that helps with diferential diagnosis.
         */
        DIFF_DIAGNOSIS("diff-diagnosis"),

        /**
         * Outcome
         * 
         * <p>A questionnaire to check on outcomes for the patient.
         */
        OUTCOME("outcome");

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
         * Factory method for creating ConditionQuestionnairePurpose.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding ConditionQuestionnairePurpose.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "preadmit":
                return PREADMIT;
            case "diff-diagnosis":
                return DIFF_DIAGNOSIS;
            case "outcome":
                return OUTCOME;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
