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

@System("http://hl7.org/fhir/permission-rule-combining")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class PermissionCombining extends Code {
    /**
     * Deny-overrides
     * 
     * <p>The deny overrides combining algorithm is intended for those cases where a deny decision should have priority over 
     * a permit decision.
     */
    public static final PermissionCombining DENY_OVERRIDES = PermissionCombining.builder().value(Value.DENY_OVERRIDES).build();

    /**
     * Permit-overrides
     * 
     * <p>The permit overrides combining algorithm is intended for those cases where a permit decision should have priority 
     * over a deny decision.
     */
    public static final PermissionCombining PERMIT_OVERRIDES = PermissionCombining.builder().value(Value.PERMIT_OVERRIDES).build();

    /**
     * Ordered-deny-overrides
     * 
     * <p>The behavior of this algorithm is identical to that of the “Deny-overrides�? rule-combining algorithm with one 
     * exception. The order in which the collection of rules is evaluated SHALL match the order as listed in the permission.
     */
    public static final PermissionCombining ORDERED_DENY_OVERRIDES = PermissionCombining.builder().value(Value.ORDERED_DENY_OVERRIDES).build();

    /**
     * Ordered-permit-overrides
     * 
     * <p>The behavior of this algorithm is identical to that of the “Permit-overrides�? rule-combining algorithm with one 
     * exception. The order in which the collection of rules is evaluated SHALL match the order as listed in the permission.
     */
    public static final PermissionCombining ORDERED_PERMIT_OVERRIDES = PermissionCombining.builder().value(Value.ORDERED_PERMIT_OVERRIDES).build();

    /**
     * Deny-unless-permit
     * 
     * <p>The “Deny-unless-permit�? combining algorithm is intended for those cases where a permit decision should have 
     * priority over a deny decision, and an “Indeterminate�? or “NotApplicable�? must never be the result. It is 
     * particularly useful at the top level in a policy structure to ensure that a PDP will always return a definite �
     * ��Permit�? or “Deny�? result.
     */
    public static final PermissionCombining DENY_UNLESS_PERMIT = PermissionCombining.builder().value(Value.DENY_UNLESS_PERMIT).build();

    /**
     * Permit-unless-deny
     * 
     * <p>The “Permit-unless-deny�? combining algorithm is intended for those cases where a deny decision should have 
     * priority over a permit decision, and an “Indeterminate�? or “NotApplicable�? must never be the result. It is 
     * particularly useful at the top level in a policy structure to ensure that a PDP will always return a definite �
     * ��Permit�? or “Deny�? result. This algorithm has the following behavior.
     */
    public static final PermissionCombining PERMIT_UNLESS_DENY = PermissionCombining.builder().value(Value.PERMIT_UNLESS_DENY).build();

    private volatile int hashCode;

    private PermissionCombining(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this PermissionCombining as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating PermissionCombining objects from a passed enum value.
     */
    public static PermissionCombining of(Value value) {
        switch (value) {
        case DENY_OVERRIDES:
            return DENY_OVERRIDES;
        case PERMIT_OVERRIDES:
            return PERMIT_OVERRIDES;
        case ORDERED_DENY_OVERRIDES:
            return ORDERED_DENY_OVERRIDES;
        case ORDERED_PERMIT_OVERRIDES:
            return ORDERED_PERMIT_OVERRIDES;
        case DENY_UNLESS_PERMIT:
            return DENY_UNLESS_PERMIT;
        case PERMIT_UNLESS_DENY:
            return PERMIT_UNLESS_DENY;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating PermissionCombining objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static PermissionCombining of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating PermissionCombining objects from a passed string value.
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
     * Inherited factory method for creating PermissionCombining objects from a passed string value.
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
        PermissionCombining other = (PermissionCombining) obj;
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
         *     An enum constant for PermissionCombining
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public PermissionCombining build() {
            PermissionCombining permissionCombining = new PermissionCombining(this);
            if (validating) {
                validate(permissionCombining);
            }
            return permissionCombining;
        }

        protected void validate(PermissionCombining permissionCombining) {
            super.validate(permissionCombining);
        }

        protected Builder from(PermissionCombining permissionCombining) {
            super.from(permissionCombining);
            return this;
        }
    }

    public enum Value {
        /**
         * Deny-overrides
         * 
         * <p>The deny overrides combining algorithm is intended for those cases where a deny decision should have priority over 
         * a permit decision.
         */
        DENY_OVERRIDES("deny-overrides"),

        /**
         * Permit-overrides
         * 
         * <p>The permit overrides combining algorithm is intended for those cases where a permit decision should have priority 
         * over a deny decision.
         */
        PERMIT_OVERRIDES("permit-overrides"),

        /**
         * Ordered-deny-overrides
         * 
         * <p>The behavior of this algorithm is identical to that of the “Deny-overrides�? rule-combining algorithm with one 
         * exception. The order in which the collection of rules is evaluated SHALL match the order as listed in the permission.
         */
        ORDERED_DENY_OVERRIDES("ordered-deny-overrides"),

        /**
         * Ordered-permit-overrides
         * 
         * <p>The behavior of this algorithm is identical to that of the “Permit-overrides�? rule-combining algorithm with one 
         * exception. The order in which the collection of rules is evaluated SHALL match the order as listed in the permission.
         */
        ORDERED_PERMIT_OVERRIDES("ordered-permit-overrides"),

        /**
         * Deny-unless-permit
         * 
         * <p>The “Deny-unless-permit�? combining algorithm is intended for those cases where a permit decision should have 
         * priority over a deny decision, and an “Indeterminate�? or “NotApplicable�? must never be the result. It is 
         * particularly useful at the top level in a policy structure to ensure that a PDP will always return a definite �
         * ��Permit�? or “Deny�? result.
         */
        DENY_UNLESS_PERMIT("deny-unless-permit"),

        /**
         * Permit-unless-deny
         * 
         * <p>The “Permit-unless-deny�? combining algorithm is intended for those cases where a deny decision should have 
         * priority over a permit decision, and an “Indeterminate�? or “NotApplicable�? must never be the result. It is 
         * particularly useful at the top level in a policy structure to ensure that a PDP will always return a definite �
         * ��Permit�? or “Deny�? result. This algorithm has the following behavior.
         */
        PERMIT_UNLESS_DENY("permit-unless-deny");

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
         * Factory method for creating PermissionCombining.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding PermissionCombining.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "deny-overrides":
                return DENY_OVERRIDES;
            case "permit-overrides":
                return PERMIT_OVERRIDES;
            case "ordered-deny-overrides":
                return ORDERED_DENY_OVERRIDES;
            case "ordered-permit-overrides":
                return ORDERED_PERMIT_OVERRIDES;
            case "deny-unless-permit":
                return DENY_UNLESS_PERMIT;
            case "permit-unless-deny":
                return PERMIT_UNLESS_DENY;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
