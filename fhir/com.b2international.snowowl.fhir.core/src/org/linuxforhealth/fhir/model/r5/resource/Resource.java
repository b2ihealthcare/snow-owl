/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.resource;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.builder.AbstractBuilder;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.AbstractVisitable;

/**
 * This is the base resource type for everything.
 * 
 * <p>Maturity level: FMM5 (Normative)
 */
@Maturity(
    level = 5,
    status = StandardsStatus.Value.NORMATIVE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public abstract class Resource extends AbstractVisitable {
    @Summary
    protected final java.lang.String id;
    @Summary
    protected final Meta meta;
    @Summary
    protected final Uri implicitRules;
    @Binding(
        bindingName = "Language",
        strength = BindingStrength.Value.REQUIRED,
        description = "IETF language tag for a human language",
        valueSet = "http://hl7.org/fhir/ValueSet/all-languages|5.0.0"
    )
    protected final Code language;

    protected volatile int hashCode;

    protected Resource(Builder builder) {
        id = builder.id;
        meta = builder.meta;
        implicitRules = builder.implicitRules;
        language = builder.language;
    }

    /**
     * The logical id of the resource, as used in the URL for the resource. Once assigned, this value never changes.
     * 
     * @return
     *     An immutable object of type {@link java.lang.String} that may be null.
     */
    public java.lang.String getId() {
        return id;
    }

    /**
     * The metadata about the resource. This is content that is maintained by the infrastructure. Changes to the content 
     * might not always be associated with version changes to the resource.
     * 
     * @return
     *     An immutable object of type {@link Meta} that may be null.
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * A reference to a set of rules that were followed when the resource was constructed, and which must be understood when 
     * processing the content. Often, this is a reference to an implementation guide that defines the special rules along 
     * with other profiles etc.
     * 
     * @return
     *     An immutable object of type {@link Uri} that may be null.
     */
    public Uri getImplicitRules() {
        return implicitRules;
    }

    /**
     * The base language in which the resource is written.
     * 
     * @return
     *     An immutable object of type {@link Code} that may be null.
     */
    public Code getLanguage() {
        return language;
    }

    /**
     * @return
     *     true if the resource can be cast to the requested resourceType
     */
    public <T extends Resource> boolean is(Class<T> resourceType) {
        return resourceType.isInstance(this);
    }

    /**
     * @throws ClassCastException
     *     when this resources cannot be cast to the requested resourceType
     */
    public <T extends Resource> T as(Class<T> resourceType) {
        return resourceType.cast(this);
    }

    public boolean hasChildren() {
        return (id != null) || 
            (meta != null) || 
            (implicitRules != null) || 
            (language != null);
    }

    /**
     * Create a new Builder from the contents of this Resource
     */
    public abstract Builder toBuilder();

    public static abstract class Builder extends AbstractBuilder<Resource> {
        protected java.lang.String id;
        protected Meta meta;
        protected Uri implicitRules;
        protected Code language;

        protected Builder() {
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
        public Builder id(java.lang.String id) {
            this.id = id;
            return this;
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
        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
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
        public Builder implicitRules(Uri implicitRules) {
            this.implicitRules = implicitRules;
            return this;
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
        public Builder language(Code language) {
            this.language = language;
            return this;
        }

        @Override
        public abstract Resource build();

        protected void validate(Resource resource) {
            ValidationSupport.checkId(resource.id);
            ValidationSupport.checkValueSetBinding(resource.language, "language", "http://hl7.org/fhir/ValueSet/all-languages", "urn:ietf:bcp:47");
        }

        protected Builder from(Resource resource) {
            id = resource.id;
            meta = resource.meta;
            implicitRules = resource.implicitRules;
            language = resource.language;
            return this;
        }
    }
}
