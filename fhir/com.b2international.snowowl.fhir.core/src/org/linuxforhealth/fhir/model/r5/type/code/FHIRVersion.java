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

/*
 * Modifications:
 * 
 * - Remove references to versions earlier than 4.0.1
 */

@System("http://hl7.org/fhir/FHIR-version")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class FHIRVersion extends Code {

    /**
     * 4.0.1
     * 
     * <p>FHIR Release 4 (Normative + STU) with 1 technical errata.
     */
    public static final FHIRVersion VERSION_4_0_1 = FHIRVersion.builder().value(Value.VERSION_4_0_1).build();

    /**
     * 4.1
     * 
     * <p>R4B Ballot #1 version.
     */
    public static final FHIRVersion VERSION_4_1 = FHIRVersion.builder().value(Value.VERSION_4_1).build();

    /**
     * 4.1.0
     * 
     * <p>R4B Ballot #1 + Connectathon 27 (Virtual).
     */
    public static final FHIRVersion VERSION_4_1_0 = FHIRVersion.builder().value(Value.VERSION_4_1_0).build();

    /**
     * 4.2
     * 
     * <p>R5 Preview #1 version.
     */
    public static final FHIRVersion VERSION_4_2 = FHIRVersion.builder().value(Value.VERSION_4_2).build();

    /**
     * 4.2.0
     * 
     * <p>R5 Preview #1 + Connectathon 23 (Sydney).
     */
    public static final FHIRVersion VERSION_4_2_0 = FHIRVersion.builder().value(Value.VERSION_4_2_0).build();

    /**
     * 4.3
     * 
     * <p>R4B version.
     */
    public static final FHIRVersion VERSION_4_3 = FHIRVersion.builder().value(Value.VERSION_4_3).build();

    /**
     * 4.3.0
     * 
     * <p>FHIR Release 4B (Normative + STU).
     */
    public static final FHIRVersion VERSION_4_3_0 = FHIRVersion.builder().value(Value.VERSION_4_3_0).build();

    /**
     * 4.3.0-cibuild
     * 
     * <p>FHIR Release 4B CI-Builld.
     */
    public static final FHIRVersion VERSION_4_3_0_CIBUILD = FHIRVersion.builder().value(Value.VERSION_4_3_0_CIBUILD).build();

    /**
     * 4.3.0-snapshot1
     * 
     * <p>FHIR Release 4B Snapshot #1.
     */
    public static final FHIRVersion VERSION_4_3_0_SNAPSHOT1 = FHIRVersion.builder().value(Value.VERSION_4_3_0_SNAPSHOT1).build();

    /**
     * 4.4
     * 
     * <p>R5 Preview #2 version.
     */
    public static final FHIRVersion VERSION_4_4 = FHIRVersion.builder().value(Value.VERSION_4_4).build();

    /**
     * 4.4.0
     * 
     * <p>R5 Preview #2 + Connectathon 24 (Virtual).
     */
    public static final FHIRVersion VERSION_4_4_0 = FHIRVersion.builder().value(Value.VERSION_4_4_0).build();

    /**
     * 4.5
     * 
     * <p>R5 Preview #3 version.
     */
    public static final FHIRVersion VERSION_4_5 = FHIRVersion.builder().value(Value.VERSION_4_5).build();

    /**
     * 4.5.0
     * 
     * <p>R5 Preview #3 + Connectathon 25 (Virtual).
     */
    public static final FHIRVersion VERSION_4_5_0 = FHIRVersion.builder().value(Value.VERSION_4_5_0).build();

    /**
     * 4.6
     * 
     * <p>R5 Draft Ballot version.
     */
    public static final FHIRVersion VERSION_4_6 = FHIRVersion.builder().value(Value.VERSION_4_6).build();

    /**
     * 4.6.0
     * 
     * <p>R5 Draft Ballot + Connectathon 27 (Virtual).
     */
    public static final FHIRVersion VERSION_4_6_0 = FHIRVersion.builder().value(Value.VERSION_4_6_0).build();

    /**
     * 5.0
     * 
     * <p>R5 Versions.
     */
    public static final FHIRVersion VERSION_5_0 = FHIRVersion.builder().value(Value.VERSION_5_0).build();

    /**
     * 5.0.0
     * 
     * <p>R5 Final Version.
     */
    public static final FHIRVersion VERSION_5_0_0 = FHIRVersion.builder().value(Value.VERSION_5_0_0).build();

    /**
     * 5.0.0-cibuild
     * 
     * <p>R5 Rolling ci-build.
     */
    public static final FHIRVersion VERSION_5_0_0_CIBUILD = FHIRVersion.builder().value(Value.VERSION_5_0_0_CIBUILD).build();

    /**
     * 5.0.0-snapshot1
     * 
     * <p>R5 Preview #2.
     */
    public static final FHIRVersion VERSION_5_0_0_SNAPSHOT1 = FHIRVersion.builder().value(Value.VERSION_5_0_0_SNAPSHOT1).build();

    /**
     * 5.0.0-snapshot2
     * 
     * <p>R5 Interim tooling stage.
     */
    public static final FHIRVersion VERSION_5_0_0_SNAPSHOT2 = FHIRVersion.builder().value(Value.VERSION_5_0_0_SNAPSHOT2).build();

    /**
     * 5.0.0-ballot
     * 
     * <p>R5 Ballot.
     */
    public static final FHIRVersion VERSION_5_0_0_BALLOT = FHIRVersion.builder().value(Value.VERSION_5_0_0_BALLOT).build();

    /**
     * 5.0.0-snapshot3
     * 
     * <p>R5 January 2023 Staging Release + Connectathon 32.
     */
    public static final FHIRVersion VERSION_5_0_0_SNAPSHOT3 = FHIRVersion.builder().value(Value.VERSION_5_0_0_SNAPSHOT3).build();

    /**
     * 5.0.0-draft-final
     * 
     * <p>R5 Final QA.
     */
    public static final FHIRVersion VERSION_5_0_0_DRAFT_FINAL = FHIRVersion.builder().value(Value.VERSION_5_0_0_DRAFT_FINAL).build();

    private volatile int hashCode;

    private FHIRVersion(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this FHIRVersion as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating FHIRVersion objects from a passed enum value.
     */
    public static FHIRVersion of(Value value) {
        switch (value) {
        case VERSION_4_0_1:
            return VERSION_4_0_1;
        case VERSION_4_1:
            return VERSION_4_1;
        case VERSION_4_1_0:
            return VERSION_4_1_0;
        case VERSION_4_2:
            return VERSION_4_2;
        case VERSION_4_2_0:
            return VERSION_4_2_0;
        case VERSION_4_3:
            return VERSION_4_3;
        case VERSION_4_3_0:
            return VERSION_4_3_0;
        case VERSION_4_3_0_CIBUILD:
            return VERSION_4_3_0_CIBUILD;
        case VERSION_4_3_0_SNAPSHOT1:
            return VERSION_4_3_0_SNAPSHOT1;
        case VERSION_4_4:
            return VERSION_4_4;
        case VERSION_4_4_0:
            return VERSION_4_4_0;
        case VERSION_4_5:
            return VERSION_4_5;
        case VERSION_4_5_0:
            return VERSION_4_5_0;
        case VERSION_4_6:
            return VERSION_4_6;
        case VERSION_4_6_0:
            return VERSION_4_6_0;
        case VERSION_5_0:
            return VERSION_5_0;
        case VERSION_5_0_0:
            return VERSION_5_0_0;
        case VERSION_5_0_0_CIBUILD:
            return VERSION_5_0_0_CIBUILD;
        case VERSION_5_0_0_SNAPSHOT1:
            return VERSION_5_0_0_SNAPSHOT1;
        case VERSION_5_0_0_SNAPSHOT2:
            return VERSION_5_0_0_SNAPSHOT2;
        case VERSION_5_0_0_BALLOT:
            return VERSION_5_0_0_BALLOT;
        case VERSION_5_0_0_SNAPSHOT3:
            return VERSION_5_0_0_SNAPSHOT3;
        case VERSION_5_0_0_DRAFT_FINAL:
            return VERSION_5_0_0_DRAFT_FINAL;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating FHIRVersion objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static FHIRVersion of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating FHIRVersion objects from a passed string value.
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
     * Inherited factory method for creating FHIRVersion objects from a passed string value.
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
        FHIRVersion other = (FHIRVersion) obj;
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
         *     An enum constant for FHIRVersion
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public FHIRVersion build() {
            FHIRVersion fHIRVersion = new FHIRVersion(this);
            if (validating) {
                validate(fHIRVersion);
            }
            return fHIRVersion;
        }

        protected void validate(FHIRVersion fHIRVersion) {
            super.validate(fHIRVersion);
        }

        protected Builder from(FHIRVersion fHIRVersion) {
            super.from(fHIRVersion);
            return this;
        }
    }

    public enum Value {
    	
        /**
         * 4.0.1
         * 
         * <p>FHIR Release 4 (Normative + STU) with 1 technical errata.
         */
        VERSION_4_0_1("4.0.1"),

        /**
         * 4.1
         * 
         * <p>R4B Ballot #1 version.
         */
        VERSION_4_1("4.1"),

        /**
         * 4.1.0
         * 
         * <p>R4B Ballot #1 + Connectathon 27 (Virtual).
         */
        VERSION_4_1_0("4.1.0"),

        /**
         * 4.2
         * 
         * <p>R5 Preview #1 version.
         */
        VERSION_4_2("4.2"),

        /**
         * 4.2.0
         * 
         * <p>R5 Preview #1 + Connectathon 23 (Sydney).
         */
        VERSION_4_2_0("4.2.0"),

        /**
         * 4.3
         * 
         * <p>R4B version.
         */
        VERSION_4_3("4.3"),

        /**
         * 4.3.0
         * 
         * <p>FHIR Release 4B (Normative + STU).
         */
        VERSION_4_3_0("4.3.0"),

        /**
         * 4.3.0-cibuild
         * 
         * <p>FHIR Release 4B CI-Builld.
         */
        VERSION_4_3_0_CIBUILD("4.3.0-cibuild"),

        /**
         * 4.3.0-snapshot1
         * 
         * <p>FHIR Release 4B Snapshot #1.
         */
        VERSION_4_3_0_SNAPSHOT1("4.3.0-snapshot1"),

        /**
         * 4.4
         * 
         * <p>R5 Preview #2 version.
         */
        VERSION_4_4("4.4"),

        /**
         * 4.4.0
         * 
         * <p>R5 Preview #2 + Connectathon 24 (Virtual).
         */
        VERSION_4_4_0("4.4.0"),

        /**
         * 4.5
         * 
         * <p>R5 Preview #3 version.
         */
        VERSION_4_5("4.5"),

        /**
         * 4.5.0
         * 
         * <p>R5 Preview #3 + Connectathon 25 (Virtual).
         */
        VERSION_4_5_0("4.5.0"),

        /**
         * 4.6
         * 
         * <p>R5 Draft Ballot version.
         */
        VERSION_4_6("4.6"),

        /**
         * 4.6.0
         * 
         * <p>R5 Draft Ballot + Connectathon 27 (Virtual).
         */
        VERSION_4_6_0("4.6.0"),

        /**
         * 5.0
         * 
         * <p>R5 Versions.
         */
        VERSION_5_0("5.0"),

        /**
         * 5.0.0
         * 
         * <p>R5 Final Version.
         */
        VERSION_5_0_0("5.0.0"),

        /**
         * 5.0.0-cibuild
         * 
         * <p>R5 Rolling ci-build.
         */
        VERSION_5_0_0_CIBUILD("5.0.0-cibuild"),

        /**
         * 5.0.0-snapshot1
         * 
         * <p>R5 Preview #2.
         */
        VERSION_5_0_0_SNAPSHOT1("5.0.0-snapshot1"),

        /**
         * 5.0.0-snapshot2
         * 
         * <p>R5 Interim tooling stage.
         */
        VERSION_5_0_0_SNAPSHOT2("5.0.0-snapshot2"),

        /**
         * 5.0.0-ballot
         * 
         * <p>R5 Ballot.
         */
        VERSION_5_0_0_BALLOT("5.0.0-ballot"),

        /**
         * 5.0.0-snapshot3
         * 
         * <p>R5 January 2023 Staging Release + Connectathon 32.
         */
        VERSION_5_0_0_SNAPSHOT3("5.0.0-snapshot3"),

        /**
         * 5.0.0-draft-final
         * 
         * <p>R5 Final QA.
         */
        VERSION_5_0_0_DRAFT_FINAL("5.0.0-draft-final");

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
         * Factory method for creating FHIRVersion.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding FHIRVersion.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "4.0.1":
                return VERSION_4_0_1;
            case "4.1":
                return VERSION_4_1;
            case "4.1.0":
                return VERSION_4_1_0;
            case "4.2":
                return VERSION_4_2;
            case "4.2.0":
                return VERSION_4_2_0;
            case "4.3":
                return VERSION_4_3;
            case "4.3.0":
                return VERSION_4_3_0;
            case "4.3.0-cibuild":
                return VERSION_4_3_0_CIBUILD;
            case "4.3.0-snapshot1":
                return VERSION_4_3_0_SNAPSHOT1;
            case "4.4":
                return VERSION_4_4;
            case "4.4.0":
                return VERSION_4_4_0;
            case "4.5":
                return VERSION_4_5;
            case "4.5.0":
                return VERSION_4_5_0;
            case "4.6":
                return VERSION_4_6;
            case "4.6.0":
                return VERSION_4_6_0;
            case "5.0":
                return VERSION_5_0;
            case "5.0.0":
                return VERSION_5_0_0;
            case "5.0.0-cibuild":
                return VERSION_5_0_0_CIBUILD;
            case "5.0.0-snapshot1":
                return VERSION_5_0_0_SNAPSHOT1;
            case "5.0.0-snapshot2":
                return VERSION_5_0_0_SNAPSHOT2;
            case "5.0.0-ballot":
                return VERSION_5_0_0_BALLOT;
            case "5.0.0-snapshot3":
                return VERSION_5_0_0_SNAPSHOT3;
            case "5.0.0-draft-final":
                return VERSION_5_0_0_DRAFT_FINAL;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
