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

@System("http://hl7.org/fhir/encounter-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class EncounterStatus extends Code {
    /**
     * Planned
     * 
     * <p>The Encounter has not yet started.
     */
    public static final EncounterStatus PLANNED = EncounterStatus.builder().value(Value.PLANNED).build();

    /**
     * In Progress
     * 
     * <p>The Encounter has begun and the patient is present / the practitioner and the patient are meeting.
     */
    public static final EncounterStatus IN_PROGRESS = EncounterStatus.builder().value(Value.IN_PROGRESS).build();

    /**
     * On Hold
     * 
     * <p>The Encounter has begun, but is currently on hold, e.g. because the patient is temporarily on leave.
     */
    public static final EncounterStatus ON_HOLD = EncounterStatus.builder().value(Value.ON_HOLD).build();

    /**
     * Discharged
     * 
     * <p>The Encounter has been clinically completed, the patient has been discharged from the facility or the visit has 
     * ended, and the patient may have departed (refer to subjectStatus). While the encounter is in this status, 
     * administrative activities are usually performed, collating all required documentation and charge information before 
     * being released for billing, at which point the status will move to completed.
     */
    public static final EncounterStatus DISCHARGED = EncounterStatus.builder().value(Value.DISCHARGED).build();

    /**
     * Completed
     * 
     * <p>The Encounter has ended.
     */
    public static final EncounterStatus COMPLETED = EncounterStatus.builder().value(Value.COMPLETED).build();

    /**
     * Cancelled
     * 
     * <p>The Encounter has ended before it has begun.
     */
    public static final EncounterStatus CANCELLED = EncounterStatus.builder().value(Value.CANCELLED).build();

    /**
     * Discontinued
     * 
     * <p>The Encounter has started, but was not able to be completed. Further action may need to be performed, such as 
     * rescheduling appointments related to this encounter.
     */
    public static final EncounterStatus DISCONTINUED = EncounterStatus.builder().value(Value.DISCONTINUED).build();

    /**
     * Entered in Error
     * 
     * <p>This instance should not have been part of this patient's medical record.
     */
    public static final EncounterStatus ENTERED_IN_ERROR = EncounterStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    /**
     * Unknown
     * 
     * <p>The encounter status is unknown. Note that "unknown" is a value of last resort and every attempt should be made to 
     * provide a meaningful value other than "unknown".
     */
    public static final EncounterStatus UNKNOWN = EncounterStatus.builder().value(Value.UNKNOWN).build();

    private volatile int hashCode;

    private EncounterStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this EncounterStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating EncounterStatus objects from a passed enum value.
     */
    public static EncounterStatus of(Value value) {
        switch (value) {
        case PLANNED:
            return PLANNED;
        case IN_PROGRESS:
            return IN_PROGRESS;
        case ON_HOLD:
            return ON_HOLD;
        case DISCHARGED:
            return DISCHARGED;
        case COMPLETED:
            return COMPLETED;
        case CANCELLED:
            return CANCELLED;
        case DISCONTINUED:
            return DISCONTINUED;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        case UNKNOWN:
            return UNKNOWN;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating EncounterStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static EncounterStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating EncounterStatus objects from a passed string value.
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
     * Inherited factory method for creating EncounterStatus objects from a passed string value.
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
        EncounterStatus other = (EncounterStatus) obj;
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
         *     An enum constant for EncounterStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public EncounterStatus build() {
            EncounterStatus encounterStatus = new EncounterStatus(this);
            if (validating) {
                validate(encounterStatus);
            }
            return encounterStatus;
        }

        protected void validate(EncounterStatus encounterStatus) {
            super.validate(encounterStatus);
        }

        protected Builder from(EncounterStatus encounterStatus) {
            super.from(encounterStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Planned
         * 
         * <p>The Encounter has not yet started.
         */
        PLANNED("planned"),

        /**
         * In Progress
         * 
         * <p>The Encounter has begun and the patient is present / the practitioner and the patient are meeting.
         */
        IN_PROGRESS("in-progress"),

        /**
         * On Hold
         * 
         * <p>The Encounter has begun, but is currently on hold, e.g. because the patient is temporarily on leave.
         */
        ON_HOLD("on-hold"),

        /**
         * Discharged
         * 
         * <p>The Encounter has been clinically completed, the patient has been discharged from the facility or the visit has 
         * ended, and the patient may have departed (refer to subjectStatus). While the encounter is in this status, 
         * administrative activities are usually performed, collating all required documentation and charge information before 
         * being released for billing, at which point the status will move to completed.
         */
        DISCHARGED("discharged"),

        /**
         * Completed
         * 
         * <p>The Encounter has ended.
         */
        COMPLETED("completed"),

        /**
         * Cancelled
         * 
         * <p>The Encounter has ended before it has begun.
         */
        CANCELLED("cancelled"),

        /**
         * Discontinued
         * 
         * <p>The Encounter has started, but was not able to be completed. Further action may need to be performed, such as 
         * rescheduling appointments related to this encounter.
         */
        DISCONTINUED("discontinued"),

        /**
         * Entered in Error
         * 
         * <p>This instance should not have been part of this patient's medical record.
         */
        ENTERED_IN_ERROR("entered-in-error"),

        /**
         * Unknown
         * 
         * <p>The encounter status is unknown. Note that "unknown" is a value of last resort and every attempt should be made to 
         * provide a meaningful value other than "unknown".
         */
        UNKNOWN("unknown");

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
         * Factory method for creating EncounterStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding EncounterStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "planned":
                return PLANNED;
            case "in-progress":
                return IN_PROGRESS;
            case "on-hold":
                return ON_HOLD;
            case "discharged":
                return DISCHARGED;
            case "completed":
                return COMPLETED;
            case "cancelled":
                return CANCELLED;
            case "discontinued":
                return DISCONTINUED;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            case "unknown":
                return UNKNOWN;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
