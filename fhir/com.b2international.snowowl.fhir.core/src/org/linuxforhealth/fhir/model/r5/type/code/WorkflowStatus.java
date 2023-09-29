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

@System("http://hl7.org/fhir/artifactassessment-workflow-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class WorkflowStatus extends Code {
    /**
     * Submitted
     * 
     * <p>The comment has been submitted, but the responsible party has not yet been determined, or the responsible party has 
     * not yet determined the next steps to be taken.
     */
    public static final WorkflowStatus SUBMITTED = WorkflowStatus.builder().value(Value.SUBMITTED).build();

    /**
     * Triaged
     * 
     * <p>The comment has been triaged, meaning the responsible party has been determined and next steps have been identified 
     * to address the comment.
     */
    public static final WorkflowStatus TRIAGED = WorkflowStatus.builder().value(Value.TRIAGED).build();

    /**
     * Waiting for Input
     * 
     * <p>The comment is waiting for input from a specific party before next steps can be taken.
     */
    public static final WorkflowStatus WAITING_FOR_INPUT = WorkflowStatus.builder().value(Value.WAITING_FOR_INPUT).build();

    /**
     * Resolved - No Change
     * 
     * <p>The comment has been resolved and no changes resulted from the resolution
     */
    public static final WorkflowStatus RESOLVED_NO_CHANGE = WorkflowStatus.builder().value(Value.RESOLVED_NO_CHANGE).build();

    /**
     * Resolved - Change Required
     * 
     * <p>The comment has been resolved and changes are required to address the comment
     */
    public static final WorkflowStatus RESOLVED_CHANGE_REQUIRED = WorkflowStatus.builder().value(Value.RESOLVED_CHANGE_REQUIRED).build();

    /**
     * Deferred
     * 
     * <p>The comment is acceptable, but resolution of the comment and application of any associated changes have been 
     * deferred
     */
    public static final WorkflowStatus DEFERRED = WorkflowStatus.builder().value(Value.DEFERRED).build();

    /**
     * Duplicate
     * 
     * <p>The comment is a duplicate of another comment already received
     */
    public static final WorkflowStatus DUPLICATE = WorkflowStatus.builder().value(Value.DUPLICATE).build();

    /**
     * Applied
     * 
     * <p>The comment is resolved and any necessary changes have been applied
     */
    public static final WorkflowStatus APPLIED = WorkflowStatus.builder().value(Value.APPLIED).build();

    /**
     * Published
     * 
     * <p>The necessary changes to the artifact have been published in a new version of the artifact
     */
    public static final WorkflowStatus PUBLISHED = WorkflowStatus.builder().value(Value.PUBLISHED).build();

    /**
     * Entered in Error
     * 
     * <p>The assessment was entered in error
     */
    public static final WorkflowStatus ENTERED_IN_ERROR = WorkflowStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    private volatile int hashCode;

    private WorkflowStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this WorkflowStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating WorkflowStatus objects from a passed enum value.
     */
    public static WorkflowStatus of(Value value) {
        switch (value) {
        case SUBMITTED:
            return SUBMITTED;
        case TRIAGED:
            return TRIAGED;
        case WAITING_FOR_INPUT:
            return WAITING_FOR_INPUT;
        case RESOLVED_NO_CHANGE:
            return RESOLVED_NO_CHANGE;
        case RESOLVED_CHANGE_REQUIRED:
            return RESOLVED_CHANGE_REQUIRED;
        case DEFERRED:
            return DEFERRED;
        case DUPLICATE:
            return DUPLICATE;
        case APPLIED:
            return APPLIED;
        case PUBLISHED:
            return PUBLISHED;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating WorkflowStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static WorkflowStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating WorkflowStatus objects from a passed string value.
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
     * Inherited factory method for creating WorkflowStatus objects from a passed string value.
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
        WorkflowStatus other = (WorkflowStatus) obj;
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
         *     An enum constant for WorkflowStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public WorkflowStatus build() {
            WorkflowStatus workflowStatus = new WorkflowStatus(this);
            if (validating) {
                validate(workflowStatus);
            }
            return workflowStatus;
        }

        protected void validate(WorkflowStatus workflowStatus) {
            super.validate(workflowStatus);
        }

        protected Builder from(WorkflowStatus workflowStatus) {
            super.from(workflowStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Submitted
         * 
         * <p>The comment has been submitted, but the responsible party has not yet been determined, or the responsible party has 
         * not yet determined the next steps to be taken.
         */
        SUBMITTED("submitted"),

        /**
         * Triaged
         * 
         * <p>The comment has been triaged, meaning the responsible party has been determined and next steps have been identified 
         * to address the comment.
         */
        TRIAGED("triaged"),

        /**
         * Waiting for Input
         * 
         * <p>The comment is waiting for input from a specific party before next steps can be taken.
         */
        WAITING_FOR_INPUT("waiting-for-input"),

        /**
         * Resolved - No Change
         * 
         * <p>The comment has been resolved and no changes resulted from the resolution
         */
        RESOLVED_NO_CHANGE("resolved-no-change"),

        /**
         * Resolved - Change Required
         * 
         * <p>The comment has been resolved and changes are required to address the comment
         */
        RESOLVED_CHANGE_REQUIRED("resolved-change-required"),

        /**
         * Deferred
         * 
         * <p>The comment is acceptable, but resolution of the comment and application of any associated changes have been 
         * deferred
         */
        DEFERRED("deferred"),

        /**
         * Duplicate
         * 
         * <p>The comment is a duplicate of another comment already received
         */
        DUPLICATE("duplicate"),

        /**
         * Applied
         * 
         * <p>The comment is resolved and any necessary changes have been applied
         */
        APPLIED("applied"),

        /**
         * Published
         * 
         * <p>The necessary changes to the artifact have been published in a new version of the artifact
         */
        PUBLISHED("published"),

        /**
         * Entered in Error
         * 
         * <p>The assessment was entered in error
         */
        ENTERED_IN_ERROR("entered-in-error");

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
         * Factory method for creating WorkflowStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding WorkflowStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "submitted":
                return SUBMITTED;
            case "triaged":
                return TRIAGED;
            case "waiting-for-input":
                return WAITING_FOR_INPUT;
            case "resolved-no-change":
                return RESOLVED_NO_CHANGE;
            case "resolved-change-required":
                return RESOLVED_CHANGE_REQUIRED;
            case "deferred":
                return DEFERRED;
            case "duplicate":
                return DUPLICATE;
            case "applied":
                return APPLIED;
            case "published":
                return PUBLISHED;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
