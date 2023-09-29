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

@System("http://hl7.org/fhir/audit-event-severity")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class AuditEventSeverity extends Code {
    /**
     * Emergency
     * 
     * <p>System is unusable. e.g., This level should only be reported by infrastructure and should not be used by 
     * applications.
     */
    public static final AuditEventSeverity EMERGENCY = AuditEventSeverity.builder().value(Value.EMERGENCY).build();

    /**
     * Alert
     * 
     * <p>Notification should be sent to trigger action be taken. e.g., Loss of the primary network connection needing 
     * attention.
     */
    public static final AuditEventSeverity ALERT = AuditEventSeverity.builder().value(Value.ALERT).build();

    /**
     * Critical
     * 
     * <p>Critical conditions. e.g., A failure in the system's primary application that will reset automatically.
     */
    public static final AuditEventSeverity CRITICAL = AuditEventSeverity.builder().value(Value.CRITICAL).build();

    /**
     * Error
     * 
     * <p>Error conditions. e.g., An application has exceeded its file storage limit and attempts to write are failing. 
     */
    public static final AuditEventSeverity ERROR = AuditEventSeverity.builder().value(Value.ERROR).build();

    /**
     * Warning
     * 
     * <p>Warning conditions. May indicate that an error will occur if action is not taken. e.g., A non-root file system has 
     * only 2GB remaining.
     */
    public static final AuditEventSeverity WARNING = AuditEventSeverity.builder().value(Value.WARNING).build();

    /**
     * Notice
     * 
     * <p>Notice messages. Normal but significant condition. Events that are unusual, but not error conditions.
     */
    public static final AuditEventSeverity NOTICE = AuditEventSeverity.builder().value(Value.NOTICE).build();

    /**
     * Informational
     * 
     * <p>Normal operational messages that require no action. e.g., An application has started, paused, or ended successfully.
     */
    public static final AuditEventSeverity INFORMATIONAL = AuditEventSeverity.builder().value(Value.INFORMATIONAL).build();

    /**
     * Debug
     * 
     * <p>Debug-level messages. Information useful to developers for debugging the application.
     */
    public static final AuditEventSeverity DEBUG = AuditEventSeverity.builder().value(Value.DEBUG).build();

    private volatile int hashCode;

    private AuditEventSeverity(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this AuditEventSeverity as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating AuditEventSeverity objects from a passed enum value.
     */
    public static AuditEventSeverity of(Value value) {
        switch (value) {
        case EMERGENCY:
            return EMERGENCY;
        case ALERT:
            return ALERT;
        case CRITICAL:
            return CRITICAL;
        case ERROR:
            return ERROR;
        case WARNING:
            return WARNING;
        case NOTICE:
            return NOTICE;
        case INFORMATIONAL:
            return INFORMATIONAL;
        case DEBUG:
            return DEBUG;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating AuditEventSeverity objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static AuditEventSeverity of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating AuditEventSeverity objects from a passed string value.
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
     * Inherited factory method for creating AuditEventSeverity objects from a passed string value.
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
        AuditEventSeverity other = (AuditEventSeverity) obj;
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
         *     An enum constant for AuditEventSeverity
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public AuditEventSeverity build() {
            AuditEventSeverity auditEventSeverity = new AuditEventSeverity(this);
            if (validating) {
                validate(auditEventSeverity);
            }
            return auditEventSeverity;
        }

        protected void validate(AuditEventSeverity auditEventSeverity) {
            super.validate(auditEventSeverity);
        }

        protected Builder from(AuditEventSeverity auditEventSeverity) {
            super.from(auditEventSeverity);
            return this;
        }
    }

    public enum Value {
        /**
         * Emergency
         * 
         * <p>System is unusable. e.g., This level should only be reported by infrastructure and should not be used by 
         * applications.
         */
        EMERGENCY("emergency"),

        /**
         * Alert
         * 
         * <p>Notification should be sent to trigger action be taken. e.g., Loss of the primary network connection needing 
         * attention.
         */
        ALERT("alert"),

        /**
         * Critical
         * 
         * <p>Critical conditions. e.g., A failure in the system's primary application that will reset automatically.
         */
        CRITICAL("critical"),

        /**
         * Error
         * 
         * <p>Error conditions. e.g., An application has exceeded its file storage limit and attempts to write are failing. 
         */
        ERROR("error"),

        /**
         * Warning
         * 
         * <p>Warning conditions. May indicate that an error will occur if action is not taken. e.g., A non-root file system has 
         * only 2GB remaining.
         */
        WARNING("warning"),

        /**
         * Notice
         * 
         * <p>Notice messages. Normal but significant condition. Events that are unusual, but not error conditions.
         */
        NOTICE("notice"),

        /**
         * Informational
         * 
         * <p>Normal operational messages that require no action. e.g., An application has started, paused, or ended successfully.
         */
        INFORMATIONAL("informational"),

        /**
         * Debug
         * 
         * <p>Debug-level messages. Information useful to developers for debugging the application.
         */
        DEBUG("debug");

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
         * Factory method for creating AuditEventSeverity.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding AuditEventSeverity.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "emergency":
                return EMERGENCY;
            case "alert":
                return ALERT;
            case "critical":
                return CRITICAL;
            case "error":
                return ERROR;
            case "warning":
                return WARNING;
            case "notice":
                return NOTICE;
            case "informational":
                return INFORMATIONAL;
            case "debug":
                return DEBUG;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
