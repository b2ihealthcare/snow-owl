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

@System("http://hl7.org/fhir/assert-manual-completion-codes")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class AssertionManualCompletionType extends Code {
    /**
     * Fail
     * 
     * <p>Mark the currently waiting test failed and proceed with the next assert if the stopTestOnFail is false or the next 
     * test in the TestScript if the stopTestOnFail is true.
     */
    public static final AssertionManualCompletionType FAIL = AssertionManualCompletionType.builder().value(Value.FAIL).build();

    /**
     * Pass
     * 
     * <p>Mark the currently waiting test passed (if the test is not failed already) and proceed with the next action in the 
     * TestScript.
     */
    public static final AssertionManualCompletionType PASS = AssertionManualCompletionType.builder().value(Value.PASS).build();

    /**
     * Skip
     * 
     * <p>Mark this assert as skipped and proceed with the next action in the TestScript.
     */
    public static final AssertionManualCompletionType SKIP = AssertionManualCompletionType.builder().value(Value.SKIP).build();

    /**
     * Stop
     * 
     * <p>Stop execution of this TestScript. The overall status of this TestScript is evaluated based on the status of the 
     * completed tests.
     */
    public static final AssertionManualCompletionType STOP = AssertionManualCompletionType.builder().value(Value.STOP).build();

    private volatile int hashCode;

    private AssertionManualCompletionType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this AssertionManualCompletionType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating AssertionManualCompletionType objects from a passed enum value.
     */
    public static AssertionManualCompletionType of(Value value) {
        switch (value) {
        case FAIL:
            return FAIL;
        case PASS:
            return PASS;
        case SKIP:
            return SKIP;
        case STOP:
            return STOP;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating AssertionManualCompletionType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static AssertionManualCompletionType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating AssertionManualCompletionType objects from a passed string value.
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
     * Inherited factory method for creating AssertionManualCompletionType objects from a passed string value.
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
        AssertionManualCompletionType other = (AssertionManualCompletionType) obj;
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
         *     An enum constant for AssertionManualCompletionType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public AssertionManualCompletionType build() {
            AssertionManualCompletionType assertionManualCompletionType = new AssertionManualCompletionType(this);
            if (validating) {
                validate(assertionManualCompletionType);
            }
            return assertionManualCompletionType;
        }

        protected void validate(AssertionManualCompletionType assertionManualCompletionType) {
            super.validate(assertionManualCompletionType);
        }

        protected Builder from(AssertionManualCompletionType assertionManualCompletionType) {
            super.from(assertionManualCompletionType);
            return this;
        }
    }

    public enum Value {
        /**
         * Fail
         * 
         * <p>Mark the currently waiting test failed and proceed with the next assert if the stopTestOnFail is false or the next 
         * test in the TestScript if the stopTestOnFail is true.
         */
        FAIL("fail"),

        /**
         * Pass
         * 
         * <p>Mark the currently waiting test passed (if the test is not failed already) and proceed with the next action in the 
         * TestScript.
         */
        PASS("pass"),

        /**
         * Skip
         * 
         * <p>Mark this assert as skipped and proceed with the next action in the TestScript.
         */
        SKIP("skip"),

        /**
         * Stop
         * 
         * <p>Stop execution of this TestScript. The overall status of this TestScript is evaluated based on the status of the 
         * completed tests.
         */
        STOP("stop");

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
         * Factory method for creating AssertionManualCompletionType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding AssertionManualCompletionType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "fail":
                return FAIL;
            case "pass":
                return PASS;
            case "skip":
                return SKIP;
            case "stop":
                return STOP;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
